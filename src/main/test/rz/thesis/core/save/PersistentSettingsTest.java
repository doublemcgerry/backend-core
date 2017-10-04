package rz.thesis.core.save;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PersistentSettingsTest {

	@Test
	public void testVersion() {
		PersistentSettings settings = new PersistentSettings(12);
		assertEquals(12, settings.getVersion());
	}

}
