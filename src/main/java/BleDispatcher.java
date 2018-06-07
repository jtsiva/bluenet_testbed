package nd.edu.bluenet_testbed;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import nd.edu.bluenet_stack.*;

public class BleDispatcher extends DummyBLE {
	
	private Map<String, Reader> mReaderMap = new HashMap<String, Reader>();
	private Map<String, ConcurrentLinkedQueue> mDevQ = new HashMap<String, ConcurrentLinkedQueue>();
	private Map<String, Thread> mDevProcessor = new HashMap<String, Thread>();

	private Set<String> mNearby = new HashSet<String>();
	private boolean mFinished = false;

	public void setNearbyState(String id, boolean isNearby) {
		if (isNearby) {
			mNearby.add(id);
		}
		else {
			mNearby.remove(id);
		}
	}

	public void finish() {
		mFinished = true;
		for (Map.Entry<String, Thread> entry : mDevProcessor.entrySet())	{
			try {
				entry.getValue().join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				//????
			}
		}
	}

	@Override
	public void setReadCB(Reader reader) {
		//This only works if setQueryCB is called right before this function
		mReaderMap.put(mID, reader);
		mDevQ.put(mID, new ConcurrentLinkedQueue<AdvertisementPayload>());

		Thread t = new Thread() {
			private String myID = mID;
		    public void run() {
				while (!mFinished || !mDevQ.get(myID).isEmpty()) {
					try {
				    	ConcurrentLinkedQueue<AdvertisementPayload> q = mDevQ.get(myID);
				    	if (null != q) {
				    		AdvertisementPayload advPayload = q.poll();
				        	if (null != advPayload) {
				        		Reader reader = mReaderMap.get(myID);
				        		reader.read(advPayload);
				        	}
				    	}
				    	Thread.sleep(100);
				    } catch (InterruptedException e) {
				    	Thread.currentThread().interrupt();
				    	break;
				    }
				}
		    }
		};

		t.start();
		mDevProcessor.put(mID, t);
	}

	@Override
	public int write(AdvertisementPayload advPayload) {
		int retVal = 0;

		if (null != advPayload.getOneHopNeighbor() && mNearby.contains(new String(advPayload.getOneHopNeighbor()))) {
			mDevQ.get(new String(advPayload.getOneHopNeighbor())).add(advPayload);
		}
		else {
			for (Map.Entry<String, ConcurrentLinkedQueue> entry : mDevQ.entrySet())	{
				if (mNearby.contains(entry.getKey())) {
			    	entry.getValue().add(advPayload);
			    }
			}
		}
		
		
		return retVal;
	}
}