package com.globo.ateam.uzi.services;

import com.globo.ateam.uzi.utils.DefaultJmeterProperties;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.extractor.JSR223PostProcessor;
import org.apache.jmeter.gui.action.Save;
import org.apache.jmeter.modifiers.JSR223PreProcessor;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class JmeterService {

    private static final String TMP_DIR = System.getProperty("java.io.tmpdir", "/tmp");
    private static final String JMETER_PROP = System.getProperty("jmeter.properties", "UNDEF");

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    void start() throws IOException {
        //JMeter Engine
        StandardJMeterEngine jmeter = new StandardJMeterEngine();

        //JMeter initialization (properties, log levels, locale, etc)
        initProperties();
        JMeterUtils.initLocale();

        // JMeter Test Plan, basic all u JOrphan HashTree
        HashTree testPlanTree = new HashTree();

        // JSR223 scripts
        JSR223PreProcessor jsr223PreProcessor = getJsr223PreProcessor();
        JSR223PostProcessor jsr223PostProcessor = getJsr223PostProcessor(jsr223PreProcessor);

        // HTTP Sampler
        HTTPSampler httpSampler = new HTTPSampler();
        httpSampler.setDomain("127.0.0.1");
        httpSampler.setPort(8080);
        httpSampler.setPath("/test");
        httpSampler.setMethod("GET");

        // Loop Controller
        LoopController loopController = new LoopController();
        loopController.setLoops(1);
        loopController.addTestElement(httpSampler);
        loopController.setFirst(true);
        loopController.initialize();

        // Thread Group
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setNumThreads(1);
        threadGroup.setStartTime(1426532760000L);
        threadGroup.setEndTime(1426532760000L);
        threadGroup.setDuration(10L);
        threadGroup.setScheduler(false);
        threadGroup.setSamplerController(loopController);

        // Test Plan
        TestPlan testPlan = new TestPlan("Create JMeter Script From Java Code");

        // Construct Test Plan from previously initialized elements
        testPlanTree.add("testPlan", testPlan);
        testPlanTree.add("loopController", loopController);
        testPlanTree.add("threadGroup", threadGroup);
        testPlanTree.add("httpSampler", httpSampler);
        testPlanTree.add("jsr223PreProcessor", jsr223PreProcessor);
        testPlanTree.add("jsr223PostProcessor", jsr223PostProcessor);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        XStream xStream = new XStream(new PureJavaReflectionProvider());
        xStream.toXML(jsr223PreProcessor, os);

        JMeterUtils.setJMeterHome("/Users/m.monteiro/.bzt/jmeter-taurus/3.2");
        SaveService.loadProperties();
        if(false) SaveService.saveElement(jsr223PreProcessor, os);
        log.error(new String(os.toByteArray(), Charset.defaultCharset()));

        // Run Test Plan
        jmeter.configure(testPlanTree);
        jmeter.run();
    }

    private JSR223PostProcessor getJsr223PostProcessor(JSR223PreProcessor jsr223PreProcessor) {
        JSR223PostProcessor jsr223PostProcessor = new JSR223PostProcessor();
        jsr223PostProcessor.setScriptLanguage(jsr223PreProcessor.getScriptLanguage());
        jsr223PostProcessor.setScript("import com.timgroup.statsd.StatsDClient; " +
                "StatsDClient statsd = (StatsDClient) vars.getObject(\"statsd\"); " +
                "long reqTime = sampler.sample().getTime(); " +
                "if (reqTime == 0L) reqTime=1L; " +
                "statsd.incrementCounter(\"http\" + sampler.sample().getResponseCode()); " +
                "statsd.recordExecutionTime(\"requestTime\", reqTime); " +
                "statsd.recordGaugeValue(\"bytesPerSec\", (sampler.sample().getBytes()/reqTime));");
        return jsr223PostProcessor;
    }

    private JSR223PreProcessor getJsr223PreProcessor() {
        JSR223PreProcessor jsr223PreProcessor = new JSR223PreProcessor();
        jsr223PreProcessor.setScriptLanguage("beanshell");
        jsr223PreProcessor.setScript("if (vars.getObject(\"statsd\") == null) " +
                "vars.putObject(\"statsd\", new com.timgroup.statsd.NonBlockingStatsDClient(\"my.test\", \"127.0.0.1\", 8125));");
        return jsr223PreProcessor;
    }

    private void initProperties() throws IOException {
        final String jmeterPropertyFile = "UNDEF".equals(JMETER_PROP) ? TMP_DIR + "/jmeter.properties" : JMETER_PROP;
        if ("UNDEF".equals(JMETER_PROP)) {
            log.warn("jmeter.properties is UNDEF. Using " + jmeterPropertyFile);
            Path jmeterPropertiesPath = Paths.get(jmeterPropertyFile);
            if (Files.notExists(jmeterPropertiesPath)) {
                try (BufferedWriter writer = Files.newBufferedWriter(jmeterPropertiesPath)) {
                    for (String l : DefaultJmeterProperties.CONTENT) writer.append(l).append((char) 0x0a);
                }
            }
        }
        JMeterUtils.loadJMeterProperties(jmeterPropertyFile);
    }
}
