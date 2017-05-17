package rz.thesis.core.modules;

public class CoreDependency {
	
	public enum CoreDependencyType{
		REQUIRED,
		OPTIONAL
	}
	
	private final String moduleName;
	private final CoreDependencyType dependencyType;
	
	public CoreDependency(String moduleName,CoreDependencyType dependencyType){
		this.moduleName=moduleName;
		this.dependencyType=dependencyType;
	}
	
	public CoreDependency(String moduleName){
		this.moduleName=moduleName;
		this.dependencyType=CoreDependencyType.REQUIRED;
	}
	
	public <T> CoreDependency(Class<T> type){
		this.moduleName=type.getSimpleName();
		this.dependencyType=CoreDependencyType.REQUIRED;
	}


	public String getModuleName() {
		return moduleName;
	}

	public CoreDependencyType getDependencyType() {
		return dependencyType;
	}
	
	
	
}
