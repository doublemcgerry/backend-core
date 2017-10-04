package rz.thesis.core.save;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import rz.thesis.core.Core;
import rz.thesis.core.options.SoftwareOptionsReader;

public class SaveModuleTest {
	private static Core core;
	@ClassRule
	public static TemporaryFolder tempFolder = new TemporaryFolder();

	@BeforeClass
	public static void before() {
		File configDir = tempFolder.getRoot();
		File configFile = new File(configDir, "config.xml");

		SoftwareOptionsReader options = new SoftwareOptionsReader(configFile);
		options.setValue("projectFolder", configDir.getAbsolutePath());
		core = new Core(options, "", new AppenderSkeleton() {

			@Override
			public boolean requiresLayout() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void close() {
				// TODO Auto-generated method stub

			}

			@Override
			protected void append(LoggingEvent event) {
				// TODO Auto-generated method stub
			}
		});
	}

	@Test
	public void testRetrieve() {
		assertNotNull(core.getModule(SaveModule.class));
	}

	@Test
	public void testLoadFileFromDisk() {
		String sectionName = "sectionName";
		SaveModule saveModule = core.getModule(SaveModule.class);
		PersistentSettings settings = new PersistentSettings() {
			private static final long serialVersionUID = 1L;

		};

		assertNull(saveModule.getSection(sectionName, settings.getClass()));
		saveModule.setSectionObject(sectionName, settings);
		saveModule.saveSection(sectionName);
		assertTrue(new File(tempFolder.getRoot(), sectionName).exists());

	}

}
