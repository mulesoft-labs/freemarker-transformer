package org.mule.module.freemarker;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.mule.tools.cloudconnect.annotations.Connector;
import org.mule.tools.cloudconnect.annotations.Operation;
import org.mule.tools.cloudconnect.annotations.Parameter;
import org.mule.tools.cloudconnect.annotations.Property;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

@Connector(namespacePrefix = "freemarker")
public class FreemarkerConnector {

    /**
     * The name of the freemarker template file
     */
    @Property
    private String template;

    /**
     * Processes the template, using data from the map, and outputs
     * the resulting text
     *
     * @param properties Data model used to feed the template
     */
    @Operation
    public String render(@Parameter(optional = true) Map<String, Object> properties) {
        StringWriter out = new StringWriter();
        try {
            Configuration cfg = new Configuration();
            cfg.setClassForTemplateLoading(getClass(), "/");

            cfg.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
            BeansWrapper bw = (BeansWrapper) cfg.getObjectWrapper();
            bw.setSimpleMapWrapper(true);
            bw.setExposureLevel(BeansWrapper.EXPOSE_ALL);
            cfg.setObjectWrapper(bw);

            Template template = cfg.getTemplate(this.template);

            template.process(properties, out);

            out.flush();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException("Cannot open template", e);
        } catch (TemplateException e) {
            throw new RuntimeException("Unable to render template", e);
        }

        return out.toString();
    }
}
