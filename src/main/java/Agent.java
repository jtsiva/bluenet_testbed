package nd.edu.bluenet_testbed;

import nd.edu.bluenet_stack.*;

public class Agent {
	LocationPlayback mLocPlayback = null;
	SimulatorStack mSimStack = null;

	public Agent() {
		mLocPlayback = new LocationPlayback();
		mSimStack = new SimulatorStack();
	}

	public void setBLELayer(DummyBLE ble) {
		mSimStack.setBLELayer(ble);
	}
}