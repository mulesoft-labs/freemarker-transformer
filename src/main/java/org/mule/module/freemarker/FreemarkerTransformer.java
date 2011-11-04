package org.mule.module.freemarker;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.mule.transformer.types.DataTypeFactory;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FreemarkerTransformer extends AbstractMessageTransformer {

    private String outputEncoding;
    private volatile String templateFile;
    private volatile String template;
    private volatile Map contextProperties;

    private Template freemarkerTemplate;
    private Configuration configuration;

    public FreemarkerTransformer() {
        super();

        setReturnDataType(DataTypeFactory.STRING);

        this.configuration = new Configuration();
        this.configuration.setClassForTemplateLoading(getClass(), "/");

        this.configuration.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
        BeansWrapper bw = (BeansWrapper) this.configuration.getObjectWrapper();
        bw.setSimpleMapWrapper(true);
        bw.setExposureLevel(BeansWrapper.EXPOSE_ALL);
        this.configuration.setObjectWrapper(bw);


        this.contextProperties = new HashMap();
    }

    @Override
    public void initialise() throws InitialisationException {
        logger.debug("Initialising transformer: " + this);
        try {
            // Only load the file once at initialize time
            if (templateFile != null) {
                this.freemarkerTemplate = this.configuration.getTemplate(templateFile);
            } else {
                this.freemarkerTemplate = new Template(getName(), new StringReader(template), configuration);
            }
        } catch (Throwable te) {
            throw new InitialisationException(te, this);
        }
    }


    @Override
    public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {
        Object payload = message.getPayload();
        StringWriter out = new StringWriter();
        try {
            this.configuration.setEncoding(Locale.US, outputEncoding);
            //this.configuration.setSharedVariable("payload", payload);
            
            Map contextPropsExpressioned = evaluateExpressions(this.contextProperties, message);
            if(logger.isDebugEnabled()) logger.debug("Context Parameters evaluated "+contextPropsExpressioned);
            
            contextPropsExpressioned.put("payload", payload);
            this.freemarkerTemplate.process(contextPropsExpressioned, out);

            out.flush();
            out.close();
        } catch (Exception e) {
            throw new TransformerException(this, e);
        }

        return out.toString();
    }
    
    private Map evaluateExpressions(Map<?,?> contextProps, MuleMessage message)
    {
    	Map m = new HashMap();
    	for(Map.Entry e: contextProps.entrySet())
    	{
        	m.put(e.getKey(), muleContext.getExpressionManager().evaluate(e.getValue().toString(), message));
    	}
    	
    	return m;
    }

    /**
     * @return the outputEncoding
     */
    public String getOutputEncoding() {
        return outputEncoding;
    }

    /**
     * @param outputEncoding the outputEncoding to set
     */
    public void setOutputEncoding(String outputEncoding) {
        this.outputEncoding = outputEncoding;
    }

    public String getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(String templateFile) {
        this.templateFile = templateFile;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    /**
     * Gets the parameters to be used when applying the transformation
     *
     * @return a map of the parameter names and associated values
     */
    public Map getContextProperties() {
        return contextProperties;
    }

    /**
     * Sets the parameters to be used when applying the transformation
     *
     * @param contextProperties a map of the parameter names and associated values
     */
    public void setContextProperties(Map contextProperties) {
        this.contextProperties = contextProperties;
    }
}
