package nd.edu.bluenet_testbed;

import nd.edu.bluenet_stack.DummyBLE;

public class BleDispatcher extends DummyBLE {
	

	@Override
	public void setReadCB(Reader reader) {
		this.mReadCB = reader;
	}
}