package nd.edu.bluenet_testbed;

import nd.edu.bluenet_stack.ProtocolContainer;
import nd.edu.bluenet_stack.DummyBLE;
import nd.edu.bluenet_stack.Reader;
import nd.edu.bluenet_stack.AdvertisementPayload;

public class SimulatorStack extends ProtocolContainer {

	public SimulatorStack() {
		super();
	}


	@Override
	protected void setupLayers() {
		mLayers.add(mRoute);
		mLayers.add(mGrp);
		mLayers.add(mLoc);
		mLayers.add(mMsg);
		mLayers.add(mBLE);
	}

	public void setBLELayer(DummyBLE ble) {
		mBLE = ble;
		setupQuery();
		connectLayers();
	}

}