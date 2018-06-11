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
		updateLocation();
		
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
		updateLocation();
	}

	private void updateLocation() {
		LocationPlayback.LocationEntry entry = mLocPlayback.getLocation();
		mSimStack.setLocation((float)entry.mLatitude, (float)entry.mLongitude);
	}

	public List<Message> getMessages(int start, int end) {
		if (end < start) {
			end = start;
		}

		if (start < 0) {
			start = 0;
		}

		if (end > mMsgList.size()) {
			end = mMsgList.size();
		}

		return mMsgList.subList(start, end);

	}

	public String getID () {
		return mSimStack.getMyID();
	}

	public Coordinate getLocation() {
		return mSimStack.getLocation(mSimStack.getMyID());
	}


}