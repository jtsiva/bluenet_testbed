package  nd.edu.bluenet_testbed;

import static org.junit.Assert.*;
import org.junit.*;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import java.nio.charset.StandardCharsets;

import nd.edu.bluenet_stack.*;


public class BleDispatcherTest {

	BleDispatcher mBLE = null;

	public class SimpleLayer extends LayerBase implements Reader {
		public String mID;
		public AdvertisementPayload mMessage = null;

		public SimpleLayer(int id) {
			mID = String.valueOf(id);
		}

		public int read(AdvertisementPayload advPayload) {
			mMessage = advPayload;
			return 0;
		}

		public int read(String src, byte[] message) {
			throw new java.lang.UnsupportedOperationException("Not supported.");
		}
	}

	@Before
	public void setup () {
		mBLE = new BleDispatcher();
	}

	@After
	public void cleanup () {
		
	}

	private Query setupQuery(int id) {
		Query query = new Query() {
			public String ask(String question) {
				final int TAG = 0;
				final int QUERY = 1;
				
				String[] parts = question.split("\\.", 2);

				String resultString = new String();

				if (Objects.equals("global", parts[TAG])) {
					if (Objects.equals("id", parts[QUERY])) {
						resultString = String.valueOf(id);
					}
				}

				return resultString;
			}
		};

		return query;
	}

	@Test
	public void shouldSendToSingleReaderNotDirected() {
		SimpleLayer layer = new SimpleLayer(1);
		mBLE.setQueryCB(setupQuery(1));
		mBLE.setReadCB(layer);

		AdvertisementPayload advPayload = new AdvertisementPayload();
		advPayload.setSrcID("1111");
		advPayload.setMsgID((byte)1);

		mBLE.setNearbyState("1", true);

		mBLE.write(advPayload);
		mBLE.finish();
		assertEquals(layer.mMessage, advPayload);
	}

	@Test
	public void shouldNotSendToSingleReaderNotDirectedNotNearby() {
		SimpleLayer layer = new SimpleLayer(1);
		mBLE.setQueryCB(setupQuery(1));
		mBLE.setReadCB(layer);

		AdvertisementPayload advPayload = new AdvertisementPayload();
		advPayload.setSrcID("1111");
		advPayload.setMsgID((byte)1);

		mBLE.setNearbyState("1", false);

		mBLE.write(advPayload);
		mBLE.finish();
		assertEquals(layer.mMessage, null);
	}

	@Test
	public void shouldSendToSingleReaderDirected() {
		SimpleLayer layer = new SimpleLayer(1);
		mBLE.setQueryCB(setupQuery(1));
		mBLE.setReadCB(layer);

		AdvertisementPayload advPayload = new AdvertisementPayload();
		advPayload.setSrcID("1111");
		advPayload.setMsgID((byte)1);
		advPayload.setOneHopNeighbor("1");

		mBLE.setNearbyState("1", true);

		mBLE.write(advPayload);
		mBLE.finish();
		assertEquals(layer.mMessage, advPayload);
	}

	@Test
	public void shouldNotSendToSingleReaderDirectedNotNearby() {
		SimpleLayer layer = new SimpleLayer(1);
		mBLE.setQueryCB(setupQuery(1));
		mBLE.setReadCB(layer);

		AdvertisementPayload advPayload = new AdvertisementPayload();
		advPayload.setSrcID("1111");
		advPayload.setMsgID((byte)1);
		advPayload.setOneHopNeighbor("1");

		mBLE.setNearbyState("1", false);

		mBLE.write(advPayload);
		mBLE.finish();
		assertEquals(layer.mMessage, null);
	}

	@Test
	public void shouldSetupMultipleAllReceive() {
		List<SimpleLayer> layers = new ArrayList<SimpleLayer>();
		for (int i = 0; i < 5; i++) {
			SimpleLayer tmp = new SimpleLayer(i);
			layers.add (tmp);
			mBLE.setQueryCB(setupQuery(i));
			mBLE.setReadCB(tmp);
			mBLE.setNearbyState(String.valueOf(i), true);
		}

		AdvertisementPayload advPayload = new AdvertisementPayload();
		advPayload.setSrcID("1111");
		advPayload.setMsgID((byte)56);
		mBLE.write(advPayload);
		mBLE.finish();
		for (SimpleLayer layer: layers) {
			assertEquals(layer.mMessage, advPayload);
		}
	}

	@Test
	public void shouldSetupMultipleOneReceiveDirected() {
		List<SimpleLayer> layers = new ArrayList<SimpleLayer>();
		for (int i = 0; i < 5; i++) {
			SimpleLayer tmp = new SimpleLayer(i);
			layers.add (tmp);
			mBLE.setQueryCB(setupQuery(i));
			mBLE.setReadCB(tmp);
			mBLE.setNearbyState(String.valueOf(i), true);
		}

		AdvertisementPayload advPayload = new AdvertisementPayload();
		advPayload.setSrcID("1111");
		advPayload.setMsgID((byte)56);
		advPayload.setOneHopNeighbor(String.valueOf(0));
		mBLE.write(advPayload);
		mBLE.finish();
		for (SimpleLayer layer: layers) {
			if (layer.mID.equals("0")) {
				assertEquals(layer.mMessage, advPayload);
			}
			else {
				assertEquals(layer.mMessage, null);
			}
		}
	}

	@Test
	public void shouldSetupMultipleOneReceiveNearby() {
		List<SimpleLayer> layers = new ArrayList<SimpleLayer>();
		for (int i = 0; i < 5; i++) {
			SimpleLayer tmp = new SimpleLayer(i);
			layers.add (tmp);
			mBLE.setQueryCB(setupQuery(i));
			mBLE.setReadCB(tmp);
			mBLE.setNearbyState(String.valueOf(i), false);
		}

		mBLE.setNearbyState("0", true);

		AdvertisementPayload advPayload = new AdvertisementPayload();
		advPayload.setSrcID("1111");
		advPayload.setMsgID((byte)56);
		mBLE.write(advPayload);
		mBLE.finish();
		for (SimpleLayer layer: layers) {
			if (layer.mID.equals("0")) {
				assertEquals(layer.mMessage, advPayload);
			}
			else {
				assertEquals(layer.mMessage, null);
			}
		}
	}
}