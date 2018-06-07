package nd.edu.bluenet_testbed;

import nd.edu.bluenet_stack.ProtocolContainer;
import nd.edu.bluenet_stack.DummyBLE;

public class SimulatorStack extends ProtocolContainer {

	protected DummyBLE mSimBLE = null;

	public SimulatorStack(DummyBLE ble) {
		mSimBLE = ble;
		super();
	}

	@Override
	public setupLayers () {
		mLayers.add(mRoute);
		mLayers.add(mGrp);
		mLayers.add(mLoc);
		mLayers.add(mMsg);
		mLayers.add(mSimBLE);
	}

	@Override
	public connectLayers () {
		//Connect the layers together

		//the simulator ble layer gets AdvertisementPayloads and passes them to 
		//the message layer
		mSimBLE.setReadCB(mMsg);

		//The message layer writes AdvertisementPayloads to the 
		//simulator ble layer
		mMsg.setWriteCB(mSimBLE);

		//The message layer will hand off messages to this (the top layer) to be printed
		//However, an AdvertisementPayload is passed up then it is sent to LocationManager
		//to handle
		mMsg.setReadCB(mLoc);

		//The location manager passes messages up the stack to the group manager
		mLoc.setReadCB(mGrp);

		//group manager hands to routing manager or all the way to result handler
		mGrp.setReadCB(new Reader() {
			public int read(AdvertisementPayload advPayload) {
				return mRoute.read(advPayload);
			}
			public int read(String src, byte[] message) {
				if (mResultHandler != null) {
			    	mResultHandler.provide(src, message);
			    }
				return 0;
			}
		});

		//routing manager can only hand 'up' to result handler
		mRoute.setReadCB(new Reader() {
			public int read(AdvertisementPayload advPayload) {
				return -1;
			}
			public int read(String src, byte[] message) {
				if (mResultHandler != null) {
			    	mResultHandler.provide(src, message);
			    }
				return 0;
			}
		});

		//pass writes down to the message layer
		mRoute.setWriteCB(mMsg);
	}
}