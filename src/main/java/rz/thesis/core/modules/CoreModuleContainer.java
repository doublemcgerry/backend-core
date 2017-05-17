package rz.thesis.core.modules;

public class CoreModuleContainer {
	private CoreModule module;
	private CoreModuleState state;
	private CoreModuleSettings settings;

	public CoreModuleContainer(CoreModule module, CoreModuleState state, CoreModuleSettings settings) {
		super();
		this.module = module;
		this.state = state;
		this.settings = settings;
	}

	public CoreModule getModule() {
		return module;
	}

	public CoreModuleState getState() {
		return state;
	}

	public CoreModuleSettings getSettings() {
		return settings;
	}

	public void setState(CoreModuleState state) {
		this.state = state;
	}
}
