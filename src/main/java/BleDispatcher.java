package nd.edu.bluenet_testbed;

import nd.edu.bluenet_stack.DummyBLE;
import nd.edu.bluenet_stack.Reader;

public class BleDispatcher extends DummyBLE {
	
	private Map<byte[], Reader> mReaderMap = new HashMap<byte[], Reader>();
	private List<Reader> mUnmappedReaders = new ArrayList<Reader>();


	@Override
	public void setReadCB(Reader reader) {
		mUnmappedReaders.add(new Reader() {
			public int read(AdvertisementPayload advPayload) {
				mReaderMap.put(advPayload.getDestID(), reader);
				return reader.read(advPayload);
			}

			public int read(String src, byte[] message) {
				throw new java.lang.UnsupportedOperationException("Not supported.");
			}
		});
			
	}

	@Override
	public int write(AdvertisementPayload advPayload) {

		if (mReaderMap.contains(advPayload.getDestID())) {
			
			if (null != advPayload.getOneHopNeighbor()) {
				Reader reader = mReaderMap.get(advPayload.getOneHopNeighbor());
				return reader.read(advPayload);
			}
			else {
				for (Map.Entry<byte [], Reader> entry : mReaderMap.entrySet())	{
				    entry.getValue().read(advPayload);
				}
			}
		}
		else {
			for (Reader reader : mUnmappedReaders) {
				reader.read(advPayload);
			}
		}
		

	}
}