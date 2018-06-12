package  nd.edu.bluenet_testbed;

import static org.junit.Assert.*;
import org.junit.*;

import java.util.*;

import nd.edu.bluenet_stack.DummyBLE;
import nd.edu.bluenet_stack.Coordinate;

public class SandboxTest {
	private Sandbox mSandbox = null;

	@Before
	public void setup () {
		mSandbox = new Sandbox("", "data/test");
	}

	@Test
	public void shouldGetCorrectNumberOfAgents() {
		assertEquals(5, mSandbox.getLocations().length);
	}
}