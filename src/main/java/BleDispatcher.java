package nd.edu.bluenet_testbed;

import java.util.*;

import nd.edu.bluenet_stack.*;

public class BleDispatcher extends DummyBLE {
	
	private Map<String, Reader> mReaderMap = new HashMap<String, Reader>();
	private Set<String> mNearby = new HashSet<String>();

	public void setNearbyState(String id, boolean isNearby) {
		if (isNearby) {
			mNearby.add(id);
		}
		else {
			mNearby.remove(id);
		}
	}

	@Override
	public void setReadCB(Reader reader) {
		//This only works if setQueryCB is called right before this function
		mReaderMap.put(mID, reader);
	}

	//Need to set up concurrent queue for synchronization of multiple write actions

	@Override
	public int write(AdvertisementPayload advPayload) {
		int retVal = -1;

		if (null != advPayload.getOneHopNeighbor() && mNearby.contains(new String(advPayload.getOneHopNeighbor()))) {
			Reader reader = mReaderMap.get(new String(advPayload.getOneHopNeighbor()));
			retVal = reader.read(advPayload);
		}
		else {
			for (Map.Entry<String, Reader> entry : mReaderMap.entrySet())	{
				if (mNearby.contains(entry.getKey())) {
			    	retVal = entry.getValue().read(advPayload);
			    }
			}
		}
		
		
		return retVal;
	}
}