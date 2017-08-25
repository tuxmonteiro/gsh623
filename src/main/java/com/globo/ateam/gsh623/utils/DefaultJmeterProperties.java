package com.globo.ateam.gsh623.utils;

public class DefaultJmeterProperties {
    
    public static final String[] CONTENT = new String[]{
            "jmeter.laf.mac=System",
            "not_in_menu=org.apache.jmeter.protocol.mongodb.sampler.MongoScriptSampler,org.apache.jmeter.protocol.mongodb.config.MongoSourceElement,\\",
            "    org.apache.jmeter.timers.BSFTimer,org.apache.jmeter.modifiers.BSFPreProcessor,org.apache.jmeter.extractor.BSFPostProcessor,org.apache.jmeter.assertions.BSFAssertion,\\",
            "    org.apache.jmeter.visualizers.BSFListener,org.apache.jmeter.protocol.java.sampler.BSFSampler,\\",
            "    org.apache.jmeter.protocol.http.control.gui.SoapSamplerGui",
            "gui.quick_0=ThreadGroupGui",
            "gui.quick_1=HttpTestSampleGui",
            "gui.quick_2=RegexExtractorGui",
            "gui.quick_3=AssertionGui",
            "gui.quick_4=ConstantTimerGui",
            "gui.quick_5=TestActionGui",
            "gui.quick_6=JSR223PostProcessor",
            "gui.quick_7=JSR223PreProcessor",
            "gui.quick_8=DebugSampler",
            "gui.quick_9=ViewResultsFullVisualizer",
            "remote_hosts=127.0.0.1",
            "sampleresult.timestamp.start=true",
            "upgrade_properties=/bin/upgrade.properties",
            "HTTPResponse.parsers=htmlParser wmlParser cssParser",
            "cssParser.className=org.apache.jmeter.protocol.http.parser.CssParser",
            "cssParser.types=text/css",
            "htmlParser.className=org.apache.jmeter.protocol.http.parser.LagartoBasedHtmlParser",
            "htmlParser.types=text/html application/xhtml+xml application/xml text/xml",
            "wmlParser.className=org.apache.jmeter.protocol.http.parser.RegexpHTMLParser",
            "wmlParser.types=text/vnd.wap.wml",
            "summariser.name=summary",
            "beanshell.server.file=../extras/startup.bsh",
            "cookies=cookies",
            "view.results.tree.renderers_order=.RenderAsText,.RenderAsRegexp,.RenderAsCssJQuery,.RenderAsXPath,org.apache.jmeter.extractor.json.render.RenderAsJsonRenderer,.RenderAsHTML,.RenderAsHTMLFormatted,.RenderAsHTMLWithEmbedded,.RenderAsDocument,.RenderAsJSON,.RenderAsXML",
            "classfinder.functions.contain=.functions.",
            "classfinder.functions.notContain=.gui.",
            "user.properties=user.properties",
            "system.properties=system.properties",
            "jmeter.reportgenerator.apdex_satisfied_threshold=500",
            "jmeter.reportgenerator.apdex_tolerated_threshold=1500"
    };
}
