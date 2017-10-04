package rz.thesis.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;

import rz.thesis.core.modules.CoreDependency;
import rz.thesis.core.modules.CoreDependency.CoreDependencyType;
import rz.thesis.core.modules.CoreModule;
import rz.thesis.core.modules.CoreModuleContainer;
import rz.thesis.core.modules.CoreModuleState;
import rz.thesis.core.modules.ServiceDefinition;
import rz.thesis.core.options.SoftwareOptionsReader;
import rz.thesis.core.save.SaveModule;

/**
 *
 * @author admiral
 */
public class Core {

	/**
	 * Software options container Contains all the options from config.xml Can add
	 * new options by using add and get methods
	 */
	private SoftwareOptionsReader sor; // options container

	private Logger log;
	private boolean enableLog = true;
	private boolean enableQueueLog = true;
	private String includefolder;

	private Map<String, CoreModuleContainer> modules = new HashMap<>();

	private SaveModule saveModule;

	public Core(SoftwareOptionsReader optionsReader, String includeFolder, AppenderSkeleton appender) {
		log = Logger.getLogger(Core.class);
		log.addAppender(appender);
		init(optionsReader, includeFolder);
	}

	private void init(SoftwareOptionsReader optionsReader, String includeFolder) {

		// log.debug("Core initialization! BUILD:" + getBuildNumber());
		this.includefolder = includeFolder;
		sor = optionsReader;

		log.debug("Using folder:" + optionsReader.getValue("projectFolder", "<current folder>"));

		this.saveModule = new SaveModule(this);
		this.addModule(this.saveModule);

		sor.SaveOptions();

		log.debug("Initialization ended succesfully");

	}

	private void sortModulesInitialization(Set<String> modules, String module, List<String> orderedModules) {
		if (orderedModules.contains(module)) {
			return;
		}
		if (!modules.contains(module)) {
			throw new RuntimeException("Error while initializing, missing dependency " + module);
		}
		CoreModule moduleObject = this.getModule(module);
		Map<String, CoreDependency> dependencies = moduleObject.getDependencies();
		for (Map.Entry<String, CoreDependency> dependency : dependencies.entrySet()) {
			if (dependency.getValue().getDependencyType() == CoreDependencyType.REQUIRED) {
				sortModulesInitialization(modules, dependency.getKey(), orderedModules);
			}
		}
		orderedModules.add(module);
	}

	public void start() {
		log.debug("Initializing modules");
		List<String> sortedModules = new ArrayList<>();
		for (String moduleName : this.modules.keySet()) {
			sortModulesInitialization(this.modules.keySet(), moduleName, sortedModules);
		}

		for (String moduleName : sortedModules) {
			CoreModuleContainer container = this.getModuleContainer(moduleName);
			try {
				container.setState(CoreModuleState.INITIALIZING);
				log.debug("Initializing module : " + container.getModule().getClass().getSimpleName());
				container.getModule().initializeModule();
				container.setState(CoreModuleState.INITIALIZED);
			} catch (Exception e) {
				container.setState(CoreModuleState.ERROR);
				log.error("Error while initializing module " + container.getModule().getClass().getSimpleName() + ": "
						+ e.getMessage());
			}
		}
		log.debug("Starting modules");
		for (final String moduleName : sortedModules) {
			final CoreModuleContainer container = this.getModuleContainer(moduleName);
			Thread thr = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						container.setState(CoreModuleState.STARTING);
						log.debug("Starting module " + container.getModule().getClass().getSimpleName());
						Core.this.getModule(moduleName).startModule();
						container.setState(CoreModuleState.STARTED);
					} catch (Exception e) {
						container.setState(CoreModuleState.ERROR);
						log.error("Error while starting module " + container.getModule().getClass().getSimpleName()
								+ ": " + e.getMessage());
					}

				}
			}, container.getModule().getClass().getSimpleName() + "MainThread");
			thr.start();
		}
		log.debug("Modules thre complete");
	}

	public void startModule(final CoreModuleContainer container) {
		Thread thr = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					container.setState(CoreModuleState.STARTING);
					log.debug("Starting module " + container.getModule().getClass().getSimpleName());
					container.getModule().startModule();
					container.setState(CoreModuleState.STARTED);
				} catch (Exception e) {
					container.setState(CoreModuleState.ERROR);
					log.error("Error while starting module " + container.getModule().getClass().getSimpleName() + ": "
							+ e.getMessage());
				}

			}
		}, container.getModule().getClass().getSimpleName() + "MainThread");
		thr.start();
	}

	public void stopModule(final CoreModuleContainer container) {
		try {
			container.setState(CoreModuleState.STOPPING);
			container.getModule().stopModule();
			container.setState(CoreModuleState.STOPPED);
		} catch (Exception e) {
			container.setState(CoreModuleState.ERROR);
		}
	}

	public String getProjectFolder() {
		return this.sor.getValue("projectFolder");
	}

	public String getIncludeFolder() {
		return includefolder;
	}

	/**
	 * Adds the module to the repository, the name is retrieved from the name
	 * property of the module itself BEWARE, ON DUPLICATE NAME, THE PRESENT ONE WILL
	 * BE OVERWRITTEN
	 * 
	 * @param module
	 *            the module to add to the repo
	 */
	public void addModule(CoreModule module) {
		CoreModuleContainer container = new CoreModuleContainer(module, CoreModuleState.INACTIVE, module.getSettings());
		this.modules.put(module.getName(), container);
	}

	/**
	 * returns the module with the given name from the repo
	 * 
	 * @param name
	 *            name of the module to retrieve
	 * @return the module object if present, null otherwise
	 */
	public CoreModule getModule(String name) {
		CoreModuleContainer container = this.modules.get(name);
		if (container != null) {
			return container.getModule();
		}
		return null;
	}

	/**
	 * returns the container of the module with the given name
	 * 
	 * @param name
	 *            name of the module to retrieve
	 * @return a {@link CoreModuleContainer} instance if the module exists, null
	 *         otherwise
	 */
	private CoreModuleContainer getModuleContainer(String name) {
		return this.modules.get(name);
	}

	/**
	 * returns the module form the defined core modules and also cast it to the
	 * defined type, for easy module retrieval
	 * 
	 * @param type
	 *            type of the module to retrieve
	 * @return the module casted to the provided type
	 */
	public <T> T getModule(Class<T> type) {
		return type.cast(this.modules.get(type.getSimpleName()).getModule());
	}

	/**
	 * Retrieves the service definitions for each module, those service definitions
	 * contains the information of the module and port that the modules listens to
	 * 
	 * @return
	 */
	public List<ServiceDefinition> getServiceDefinitions() {
		List<ServiceDefinition> definitions = new ArrayList<>();
		for (Map.Entry<String, CoreModuleContainer> entry : this.modules.entrySet()) {
			List<ServiceDefinition> moduleDefinitions = entry.getValue().getModule().getServiceDefinition();
			if (moduleDefinitions != null) {
				definitions.addAll(moduleDefinitions);
			}
		}
		return definitions;
	}

	/**
	 * Prepares the core for a graceful shutdown
	 */
	public void stop() {
		log.debug("Stopping core");
		log.debug("Stopping modules");
		for (CoreModuleContainer container : this.modules.values()) {
			try {
				container.setState(CoreModuleState.STOPPING);
				container.getModule().stopModule();
				container.setState(CoreModuleState.STOPPED);
			} catch (Exception e) {
				container.setState(CoreModuleState.ERROR);
				log.error("Error while shutting down module " + container.getModule().getName());
			}
		}
		log.debug("Core stopped");
	}

	/**
	 * returns the build number read from the file version.properties this number is
	 * auto-incremented by the builder
	 *
	 * @return
	 */
	public String getBuildNumber() {
		Properties prop = new Properties();
		try {
			// load a properties file from class path, inside static method
			prop.load(getClass().getClassLoader().getResourceAsStream("version.properties"));

			// get the property value and print it out
			return prop.getProperty("BUILD");

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return "ND";
	}

	/**
	 * Toggle log write
	 */
	public void ToggleLog() {
		enableLog = !enableLog;
	}

	public boolean isLogEnabled() {
		return enableLog;
	}

	public void ToggleQueueLog() {
		enableQueueLog = !enableQueueLog;
	}

	public boolean isQueueLogEnabled() {
		return enableQueueLog;
	}

	public SoftwareOptionsReader getSoftwareOptionsReader() {
		return sor;
	}

	public Logger getLogger() {
		return log;
	}

}
