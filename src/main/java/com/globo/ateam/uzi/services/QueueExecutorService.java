package com.globo.ateam.uzi.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.*;

@Service
public class QueueExecutorService {

    private static final String TMP_DIR = System.getProperty("java.io.tmpdir", "/tmp");
    private static final char   BRK_LN = (char) 0x0a;
    private static final Integer TASK_LIMIT = Integer.parseInt(System.getProperty("task.limit", "0"));

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ConcurrentLinkedQueue<Callable<Result>> queue = new ConcurrentLinkedQueue<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final CommandService commandService;

    @Value("${build.project}")
    private String buildProject;

    @Autowired
    public QueueExecutorService(CommandService commandService) throws IOException {
        log.info("Using " + TMP_DIR + " as tmpdir");
        this.commandService = commandService;
    }

    public void put(long testId, byte[] body) {
        if (taskQueueOverflow(testId)) return;
        queue.add(() -> {
            log.info("executing task id " + testId);
            try {
                final byte[] confBody = buildConf(body);
                final String idDir = extractIdDirectory(testId);
                final String confFile = idDir + "/test.jmx";
                writeToFile(confBody, confFile);
                // TODO
                return new Result(testId, extractResult(commandService.run(testId)));
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            return new Result(-1L, "");
        });
        log.info("added task id " + testId);
    }

    private String extractIdDirectory(long testId) {
        return TMP_DIR + "/" + buildProject + "/" + testId;
    }

    private String extractResultFile(long testId) {
        return extractIdDirectory(testId) + "/result";
    }

    private boolean taskQueueOverflow(long testId) {
        try {
            createDir(extractIdDirectory(testId));
            if (TASK_LIMIT != 0 && queue.size() >= TASK_LIMIT) {
                String resultFile = extractResultFile(testId);
                String errorQueueOverflowMessage = "ERROR: Task Queue Overflow. Try again later";
                writeToFile(("{\"status\":\"" + errorQueueOverflowMessage + "\"}").getBytes(Charset.defaultCharset()), resultFile);
                log.error(errorQueueOverflowMessage);
                log.warn("task id " + testId + " NOT executed");
                return true;
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return true;
        }
        return false;
    }

    private void createDir(String idDir) throws IOException {
        if (!Files.exists(Paths.get(idDir))) Files.createDirectory(Paths.get(idDir));
    }

    private void writeToFile(byte[] tree, String confFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(confFile)) {
            fos.write(tree);
        }
    }

    private String extractResult(BufferedReader bufferedReader) throws IOException {
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            result.append(line);
            result.append(BRK_LN);
        }
        return result.toString();
    }

    // TODO
    private byte[] buildConf(byte[] body) throws IOException {
        return null;
    }

    @Scheduled(fixedDelay = 5000)
    public void run() throws ExecutionException, InterruptedException, IOException {
        if (!queue.isEmpty()) {
            final Callable<Result> task = queue.poll();
            final Result result = executor.submit(task).get();
            long id = result.getId();
            String resultFile = extractResultFile(id);
            String resultBody = result.getResult();
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(resultFile))) {
                writer.write(resultBody);
            }
            log.info("task id " + id + " executed");
        }
    }

    private static class Result {
        private final long id;
        private final String result;

        Result(long id, String result) {
            this.id = id;
            this.result = result;
        }

        long getId() {
            return id;
        }

        String getResult() {
            return result;
        }
    }

}
