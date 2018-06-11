package nd.edu.bluenet_testbed;

import nd.edu.bluenet_stack.*;
import java.util.ArrayList;
import java.util.List;

public class Agent {
	public class Message {
		public String mID = null;
		public String mMsg = null;

		public Message (String id, String msg) {
			mID = id;
			mMsg = msg;
		}
	}

	private LocationPlayback mLocPlayback = null;
	private SimulatorStack mSimStack = null;
	private List<Message> mMsgList = new ArrayList<Message>();

	public Agent() {
		mLocPlayback = new LocationPlayback();
		mSimStack = new SimulatorStack();
		
		setupCallback();		
	}

	public Agent(String locationFile) {
		mLocPlayback = new LocationPlayback(locationFile);
		mSimStack = new SimulatorStack();
		
		setupCallback();		
	}

	private void setupCallback() {
		mSimStack.regCallback(new Result () {
			public int provide(String src, byte[] data) {
				mMsgList.add(new Message(src, new String(data)));
				return 0;
			}
		});
	}

	public void setBLELayer(DummyBLE ble) {
		mSimStack.setBLELayer(ble);
	}

	public void update() {
		update(1);
	}

	public void update(long timeIndexDiff) {
		mLocPlayback.update(timeIndexDiff);
	}

	public LocationPlayback.LocationEntry getLocation() {
		return mLocPlayback.getLocation();
	}


}