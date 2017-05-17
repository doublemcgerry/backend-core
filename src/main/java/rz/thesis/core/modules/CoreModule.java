package rz.thesis.core.modules;

import java.util.List;
import java.util.Map;

import rz.thesis.core.Core;

public abstract class CoreModule {

	private final CoreModuleSettings settings;
	private final Core core;
	private final String name;
	private final Map<String, CoreDependency> dependencies;

	public CoreModule(String name, Core core, CoreModuleSettings settings, List<CoreDependency> dependencies) {
		this.dependencies = new CoreDependenciesListBuilder().addDependency(dependencies).toMap();
		this.name = name;
		this.settings = settings;
		this.core = core;
	}

	public CoreModule(String name, Core core, CoreModuleSettings settings) {
		this.dependencies = new CoreDependenciesListBuilder().toMap();
		this.name = name;
		this.settings = settings;
		this.core = core;
	}

	public abstract void initializeModule();

	public abstract void startModule();

	public abstract void stopModule();

	public CoreModuleSettings getSettings() {
		return settings;
	}

	protected Core getCore() {
		return core;
	}

	public String getName() {
		return name;
	}

	public abstract List<ServiceDefinition> getServiceDefinition();

	public Map<String, CoreDependency> getDependencies() {
		return dependencies;
	}

}
