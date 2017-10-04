package rz.thesis.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import rz.thesis.core.modules.CoreModule;
import rz.thesis.core.modules.CoreModuleSettings;
import rz.thesis.core.modules.ServiceDefinition;
import rz.thesis.core.options.SoftwareOptionsReader;

public class CoreTest {
	private static Core core;

	private static class TestModule extends CoreModule {

		public TestModule(Core core, CoreModuleSettings settings) {
			super(TestModule.class.getSimpleName(), core, settings);

		}

		@Override
		public void initializeModule() {

		}

		@Override
		public void startModule() {

		}

		@Override
		public void stopModule() {

		}

		@Override
		public List<ServiceDefinition> getServiceDefinition() {
			return null;
		}

	}

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
	public void testGetProjectFolder() {
		assertEquals(tempFolder.getRoot().getAbsolutePath(), core.getProjectFolder());
	}

	@Test
	public void testAddModule() {
		CoreModule module = new TestModule(core, new CoreModuleSettings());
		core.addModule(module);
		assertEquals(module, core.getModule(module.getClass()));
		assertEquals(module, core.getModule(module.getClass().getSimpleName()));
	}

	@Test
	public void testGetServiceDefinitions() {
		CoreModule serviceModule = new CoreModule("ServiceModule", core, new CoreModuleSettings()) {

			@Override
			public void stopModule() {
				// TODO Auto-generated method stub

			}

			@Override
			public void startModule() {
				// TODO Auto-generated method stub

			}

			@Override
			public void initializeModule() {
				// TODO Auto-generated method stub

			}

			@Override
			public List<ServiceDefinition> getServiceDefinition() {
				List<ServiceDefinition> servs = new ArrayList<>();
				servs.add(new ServiceDefinition("test", 2000));
				return servs;
			}
		};
		core.addModule(serviceModule);
		assertEquals(serviceModule, core.getModule("ServiceModule"));
		List<ServiceDefinition> services = core.getServiceDefinitions();
		boolean found = false;
		for (ServiceDefinition serviceDefinition : services) {
			if (serviceDefinition.getDescription().equals("test") && serviceDefinition.getPort() == 2000) {
				found = true;
			}
		}
		if (!found) {
			assertTrue(false);
		}
	}

}
