package rz.thesis.core.save;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import rz.thesis.core.Core;
import rz.thesis.core.modules.CoreModule;
import rz.thesis.core.modules.CoreModuleSettings;
import rz.thesis.core.modules.ServiceDefinition;
import rz.thesis.core.options.SoftwareOptionsReader;
import rz.thesis.core.serialization.SerializerAdapter;

public class SaveModule extends CoreModule {
	private static final Logger LOGGER = Logger.getLogger(SaveModule.class.getName());
	private Map<String, PersistentSettings> projectMap = new HashMap<>();
	private final SoftwareOptionsReader commonSettings;
	private final Gson serializer;
	private File projectBaseFolder;

	public SaveModule(Core core) {
		super(SaveModule.class.getSimpleName(), core, new CoreModuleSettings());
		this.commonSettings = core.getSoftwareOptionsReader();
		this.projectBaseFolder = new File(this.commonSettings.getValue("projectFolder"));
		this.serializer = new GsonBuilder().registerTypeAdapter(PersistentSettings.class, new SerializerAdapter<>())
				.create();
	}

	@Override
	public void initializeModule() {

	}

	@Override
	public void startModule() {

	}

	@Override
	public void stopModule() {
		this.saveAllSections();
	}

	@Override
	public List<ServiceDefinition> getServiceDefinition() {
		return null;
	}

	public SoftwareOptionsReader getCommonSettings() {
		return commonSettings;
	}

	public <T extends PersistentSettings> T getSection(String sectionName, Class<T> sectionClass) {
		if (isSectionLoaded(sectionName)) {
			return sectionClass.cast(projectMap.get(sectionName));
		} else {
			if (isSectionOnDisk(sectionName)) {
				projectMap.put(sectionName, loadSectionFromDisk(sectionName));
				return sectionClass.cast(projectMap.get(sectionName));
			} else {
				return null;
			}
		}

	}

	public void reloadSectionFromDisk(String sectionName) {
		projectMap.put(sectionName, loadSectionFromDisk(sectionName));
	}
	
	public void setSectionObject(String sectionName,PersistentSettings settings){
		projectMap.put(sectionName, settings);
	}
	
	public void saveSection(String sectionName) {
		if (!isSectionLoaded(sectionName)) {
			return;
		}
		JsonWriter writer;
		try {
			writer = new JsonWriter(new FileWriter(getSectionFilenameOnDisk(sectionName)));
			serializer.toJson(this.projectMap.get(sectionName), PersistentSettings.class, writer);
			writer.flush();
			
		} catch (IOException e) {
			throw new RuntimeException("error while saving the section " + sectionName + " to disk");
		}
	}
	
	public void saveAllSections() {
		for (Map.Entry<String, PersistentSettings> entry : this.projectMap.entrySet()) {
			try {
				saveSection(entry.getKey());
			} catch (RuntimeException ex) {
				LOGGER.error(ex);
			}

		}

	}
	

	private boolean isSectionOnDisk(String sectionName) {
		return getSectionFilenameOnDisk(sectionName).exists();
	}

	private File getSectionFilenameOnDisk(String sectionName) {
		return new File(this.projectBaseFolder, sectionName);
	}

	private boolean isSectionLoaded(String sectionName) {
		return projectMap.containsKey(sectionName);
	}

	private <T> PersistentSettings loadSectionFromDisk(String sectionName) {
		try {
			JsonReader reader = new JsonReader(new FileReader(this.getSectionFilenameOnDisk(sectionName)));
			return serializer.fromJson(reader, PersistentSettings.class);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("error while loading the section " + sectionName + " from disk");
		}

	}

	

	

}
