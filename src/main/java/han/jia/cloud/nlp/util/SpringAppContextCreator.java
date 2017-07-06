package han.jia.cloud.nlp.util;

import java.io.IOException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

/**
 * A utility class to create or get the existing
 * {@code org.springframework.context.ApplicationContext}
 * 
 * <p>
 * This class prevents subclassing and instantiation. Whenever the application
 * context is needed, one of the static methods needs to be invoked. No matter
 * how many times the method is called, it ensures that the (costly) context
 * will be created only once.
 * 
 * <p>
 * Usage example:
 * 
 * <pre>
 * ApplicationContext ctx = SpringAppContextCreator.getAppContext();
 *   -- The default 'application-context.xml' will be used.
 *   
 * ApplicationContext ctx = SpringAppContextCreator.getAppContext("my-application-contxt.xml");
 *   -- Use the supplied application context file
 *   
 * MyBean bean = ctx.getBean(MyBean.class);
 * ...
 * </pre>
 * 
 * @author Jiayun Han
 * 
 */
public final class SpringAppContextCreator implements ApplicationContextAware {

	private final static Logger logger = LoggerFactory.getLogger(SpringAppContextCreator.class);

	private static ApplicationContext appContext;

	private SpringAppContextCreator() {
	}

	@Override
	public void setApplicationContext(ApplicationContext val) throws BeansException {

		if (appContext == null) {
			appContext = val;
		}
	}
	
	public static ApplicationContext getAppContext() throws IOException {
		return getAppContext("application-context.xml");
	}

	/**
	 * Returns {@code org.springframework.context.ApplicationContext} using the
	 * supplied context file to create one in case no context exists.
	 * 
	 * @param appContextFileName
	 *            The base name of the context configuration file. It must be
	 *            placed under the resources folder.
	 *            
	 * @return The application context
	 * @throws IOException
	 *             If the specified application context file does not exist or
	 *             cannot be read.
	 */
	@SuppressWarnings("resource")
	public static ApplicationContext getAppContext(String appContextFileName) throws IOException {

		if (appContext == null) {
			
			Objects.requireNonNull(appContextFileName, "application file cannot be null");
			logger.info("Loading application context...");

			GenericApplicationContext parentContext = new GenericApplicationContext(new DefaultListableBeanFactory());
			parentContext.refresh();

			new ClassPathXmlApplicationContext(new String[] { appContextFileName }, parentContext);

			logger.info("Application context loaded.");
		}

		return appContext;
	}
}
