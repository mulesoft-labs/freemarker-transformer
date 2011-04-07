package org.mule.module.freemarker.config;

import org.mule.api.MuleMessage;
import org.mule.construct.SimpleFlowConstruct;
import org.mule.tck.FunctionalTestCase;

import java.util.HashMap;
import java.util.Map;

public class FreemarkerTransformerNamespaceHandlerTest extends FunctionalTestCase {

    @Override
    protected String getConfigResources() {
        return "freemarker-namespace-config.xml";
    }

    public void testInlineTransform() throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("username", "mulesoft");
        map.put("accountid", "12345");
        SimpleFlowConstruct flow = lookupFlowConstruct("transformTest");
        MuleMessage message = flow.process(getTestEvent(map)).getMessage();
        assertEquals("\n                My Username is mulesoft" +
                     "\n                My Account Id is 12345\n", message.getPayload());
    }

    public void testExternalTransform() throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("username", "mulesoft");
        SimpleFlowConstruct flow = lookupFlowConstruct("externalTransformTest");
        MuleMessage message = flow.process(getTestEvent(map)).getMessage();
        assertEquals("My account is mulesoft", message.getPayload());
    }

    private SimpleFlowConstruct lookupFlowConstruct(String name) {
        return (SimpleFlowConstruct) muleContext.getRegistry().lookupFlowConstruct(name);
    }

}
