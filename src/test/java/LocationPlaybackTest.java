package  nd.edu.bluenet_testbed;

import static org.junit.Assert.*;
import org.junit.*;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import java.nio.charset.StandardCharsets;

import nd.edu.bluenet_stack.LocationManager;


public class LocationPlaybackTest {
	private static final double DELTA = 1e-15;
	private LocationPlayback mLocPlayback = null;
	private String mFileName = null;


	@Before
	public void setup () {
		mFileName = this.getClass().getClassLoader().getResource("./2015270055.txt").getFile();
		mLocPlayback = new LocationPlayback(mFileName);
	}

	@Test
	public void shouldLoadFromFile (){
		LocationPlayback.LocationEntry entry = mLocPlayback.getLocation();

		assertNotNull(entry);
		assertEquals(mLocPlayback.size(), 21693);
	}

	@Test
	public void shouldGetNextKnownLocation() {
		//Start time: 		1510570290730
		//time at change: 	1510577315632

		mLocPlayback.update(1510577315632L - 1510570290730L); //move ahead this much
		LocationPlayback.LocationEntry entry = mLocPlayback.getLocation();
		assertEquals(51.5083567053559, entry.mLatitude, DELTA);
		assertEquals(-0.130784511566284, entry.mLongitude, DELTA);

	}

	@Test
	public void shouldEstimateNextLocationCloserToA() {
		//A: 1510574613168	51.5052098874482	-0.112585974857316
		//B: 1510577315632	51.5083567053559	-0.130784511566284
		
		double latA = 51.5052098874482;
		double lonA = -0.112585974857316;

		double latB = 51.5083567053559;
		double lonB = -0.130784511566284;
		
		mLocPlayback.update(1510574613168L - 1510570290730L);//move to just before change
		mLocPlayback.update();//move ahead one tick
		LocationPlayback.LocationEntry entry = mLocPlayback.getLocation();

		double distA = LocationManager.distance(latA, lonA, entry.mLatitude, entry.mLongitude);
		double distB = LocationManager.distance(latB, lonB, entry.mLatitude, entry.mLongitude);

		// System.out.print("dist to A: ");
		// System.out.println(distA);

		// System.out.print("dist to B: ");
		// System.out.println(distB);

		// System.out.print(entry.mLatitude);
		// System.out.print(" ");
		// System.out.println(entry.mLongitude);
		

		assertTrue(distA < distB);
	}

	@Test
	public void shouldEstimateNextLocationCloserToB() {
		//A: 1510574613168	51.5052098874482	-0.112585974857316
		//B: 1510577315632	51.5083567053559	-0.130784511566284
		//
		//
		double latA = 51.5052098874482;
		double lonA = -0.112585974857316;

		double latB = 51.5083567053559;
		double lonB = -0.130784511566284;
		
		mLocPlayback.update(1510574613168L - 1510570290730L);//move to just before change
		mLocPlayback.update((1510577315632L - 1510574613168L) - 1);//one tick back from B
		LocationPlayback.LocationEntry entry = mLocPlayback.getLocation();

		double distA = LocationManager.distance(latA, lonA, entry.mLatitude, entry.mLongitude);
		double distB = LocationManager.distance(latB, lonB, entry.mLatitude, entry.mLongitude);

		// System.out.println(distA);
		// System.out.print(entry.mLatitude);
		// System.out.print(" ");
		// System.out.println(entry.mLongitude);
		// System.out.println(distB);

		assertTrue(distA > distB);
	}

	@Test
	public void shouldPlayToEndBigSteps () {
		LocationPlayback.LocationEntry entry;
		boolean done = false;
		while (!done) {
			entry = mLocPlayback.getLocation();
			if (null != entry) {
				mLocPlayback.update(10000);
			}
			else {
				done = true;
			}
		}

		assertTrue(true);
	}

	@Test
	public void shouldNeverReturnNaN() {
		LocationPlayback.LocationEntry entry;
		boolean done = false;
		while (!done) {
			entry = mLocPlayback.getLocation();
			if (null != entry) {
				mLocPlayback.update(1500);
				assertTrue(entry.mLatitude == entry.mLatitude); //only way to check for NaN
				assertTrue(entry.mLongitude == entry.mLongitude);
			}
			else {
				done = true;
			}
		}
	}
}