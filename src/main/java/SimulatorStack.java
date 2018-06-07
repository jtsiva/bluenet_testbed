package nd.edu.bluenet_testbed;

import nd.edu.bluenet_stack.ProtocolContainer;
import nd.edu.bluenet_stack.DummyBLE;
import nd.edu.bluenet_stack.Reader;
import nd.edu.bluenet_stack.AdvertisementPayload;

public class SimulatorStack extends ProtocolContainer {

	public SimulatorStack() {
		super();
	}

	public void setBleLayer(DummyBLE ble) {
		mBLE = ble;
		connectLayers();
	}

}