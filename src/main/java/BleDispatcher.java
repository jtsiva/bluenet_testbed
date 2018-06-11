package nd.edu.bluenet_testbed;

import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import javafx.util.Pair;

import nd.edu.bluenet_stack.*;

public class BleDispatcher extends DummyBLE {
	
	private Map<String, Reader> mReaderMap = new HashMap<String, Reader>();
	private Map<String, ConcurrentLinkedQueue> mDevQ = new HashMap<String, ConcurrentLinkedQueue>();
	private Map<String, Thread> mDevProcessor = new HashMap<String, Thread>();

	private Map<Pair<String,String>, Integer> mNearby = new HashMap<Pair<String,String>, Integer>();
	private boolean mFinished = false;

	private boolean mGlobalControl;

	public BleDispatcher () {
		this(false);
	}

	public BleDispatcher (boolean useGlobalControl) {
		mGlobalControl = useGlobalControl;
	}

	public void setNearbyState(String canThis, String seeThis, Integer nearby) {
		//symmetric!
		mNearby.put(new Pair(canThis, seeThis), nearby);
		mNearby.put(new Pair(seeThis, canThis), nearby);
	
	}

	public void finish() {
		if (!mGlobalControl) {
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
	}

	public void update() {
		//https://stackoverflow.com/questions/12815460/hashmap-iterating-the-key-value-pairs-in-random-order
		// We want to simulate a non deterministic processing order of the receiving devices

		if (mGlobalControl) {
			List<Map.Entry<String,ConcurrentLinkedQueue>> list = new ArrayList<Map.Entry<String,ConcurrentLinkedQueue>>(mDevQ.entrySet());

			// each time you want a different order.
			Collections.shuffle(list);
			for(Map.Entry<String, ConcurrentLinkedQueue> entry: list) {
				if (null != entry.getValue()) {
					ConcurrentLinkedQueue<AdvertisementPayload> q = entry.getValue();
					AdvertisementPayload advPayload = q.poll();
		        	if (null != advPayload) {
		        		Reader reader = mReaderMap.get(entry.getKey());
		        		reader.read(advPayload);
		        	}
		        }
			}
		}
	}

	private void setupThread(String id) {
		Thread t = new Thread() {
			private String myID = id;
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
	public void setReadCB(Reader reader) {
		//This only works if setQueryCB is called right before this function
		mReaderMap.put(mID, reader);
		mDevQ.put(mID, new ConcurrentLinkedQueue<AdvertisementPayload>());
		if (!mGlobalControl) {
			setupThread(mID);
		}
		
	}

	@Override
	public int write(AdvertisementPayload advPayload) {
		int retVal = 0;

		if (null != advPayload.getOneHopNeighbor()) {
			if (0 != mNearby.get(new Pair(new String(advPayload.getSrcID()), new String(advPayload.getOneHopNeighbor())))) {
				mDevQ.get(new String(advPayload.getOneHopNeighbor())).add(advPayload);
			}
		}
		else {
			for (Map.Entry<String, ConcurrentLinkedQueue> entry : mDevQ.entrySet())	{
				if (0 != mNearby.get(new Pair(new String(advPayload.getSrcID()), entry.getKey()))) {
			    	entry.getValue().add(advPayload);
			    }
			}
		}
		
		
		return retVal;
	}
}