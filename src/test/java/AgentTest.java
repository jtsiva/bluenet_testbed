package  nd.edu.bluenet_testbed;

import static org.junit.Assert.*;
import org.junit.*;

import java.util.*;

import nd.edu.bluenet_stack.DummyBLE;
import nd.edu.bluenet_stack.Coordinate;

public class AgentTest {
	private static final double DELTA = 1e-4; //NOTE: THIS IS OUR PRECISION
	private Agent mAgent = null;
	private DummyBLE mBLE = null;


	@Before
	public void setup() {
		mAgent = new Agent(this.getClass().getClassLoader().getResource("./2015270055.txt").getFile());
		mBLE = new DummyBLE();
		mAgent.setBLELayer (mBLE);
	}

	@Test
	public void shouldInitialize() {
		assertNotNull (mAgent);

		Coordinate entry = mAgent.getLocation();

		assertEquals(51.5052098874482, entry.mLatitude, DELTA);
		assertEquals(-0.112585974857316, entry.mLongitude, DELTA);

	}

	@Test
	public void shouldNotReturnNaN () {
		while (!mAgent.playbackDone) {
			mAgent.update(5000);
			Coordinate coor = mAgent.getLocation();
			assertTrue(coor.mLatitude == coor.mLatitude);
			assertTrue(coor.mLongitude == coor.mLongitude);
		}
	}
}