package org.mule.module.freemarker.config;

import org.mule.config.spring.handlers.AbstractMuleNamespaceHandler;
import org.mule.config.spring.parsers.collection.ChildMapEntryDefinitionParser;
import org.mule.config.spring.parsers.generic.TextDefinitionParser;
import org.mule.config.spring.parsers.specific.MessageProcessorDefinitionParser;
import org.mule.module.freemarker.FreemarkerTransformer;

public class FreemarkerTransformerNamespaceHandler extends AbstractMuleNamespaceHandler {

    public void init() {
        registerBeanDefinitionParser("transformer", new MessageProcessorDefinitionParser(FreemarkerTransformer.class));
        registerBeanDefinitionParser("template", new TextDefinitionParser("template"));

        registerBeanDefinitionParser("context-property", new ChildMapEntryDefinitionParser("contextProperties", "key", "value"));
    }

}

