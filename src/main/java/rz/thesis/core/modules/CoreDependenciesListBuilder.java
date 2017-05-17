package rz.thesis.core.modules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rz.thesis.core.modules.CoreDependency.CoreDependencyType;

public class CoreDependenciesListBuilder {
	private Map<String,CoreDependency> depList = new HashMap<>();
	
	public CoreDependenciesListBuilder addDependency(String moduleName,CoreDependencyType dependencyType){
		this.depList.put(moduleName, new CoreDependency(moduleName,dependencyType));
		return this;
	}
	
	public CoreDependenciesListBuilder addDependency(String moduleName){
		this.depList.put(moduleName, new CoreDependency(moduleName));
		return this;
	}
	
	public CoreDependenciesListBuilder addDependency(List<CoreDependency> dependencies){
		for (CoreDependency coreDependency : dependencies) {
			this.depList.put(coreDependency.getModuleName(), coreDependency);
		}
		return this;
	}
	
	
	public Map<String,CoreDependency> toMap(){
		return this.depList;
	}
	
	
	
}
