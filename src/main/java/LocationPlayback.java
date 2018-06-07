package nd.edu.bluenet_testbed;

import java.util.ArrayList;
import java.nio.file.*;

public class LocationPlayback {

	public class LocationEntry {
		public double mLatitude;
		public double mLongitude;
		public long mTimestamp;
	}

	private List<LocationEntry> mEntries = new ArrayList<LocationEntry>();
	private int mIndex = 0;

	public LocationPlayback () {

	}

	public LocationPlayback(String fileName) {
		initialize(filename);
	}

	public void initialize(String filename) {
		String line = null;
		try {
			//from: https://alvinalexander.com/java/java-bufferedreader-readline-string-examples
		    // wrap a BufferedReader around FileReader
		    BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));

		    // use the readLine method of the BufferedReader to read one line at a time.
		    // the readLine method returns null when there is nothing else to read.
		    while ((line = bufferedReader.readLine()) != null)
		    {
		    	//Assuming the line is formatted as timestamp latitude longitude
		        records.add(line);
		    }
		  
		    // close the BufferedReader when we're done
		    bufferedReader.close();
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		} finally {
		    if (bufferedReader != null) {
		    	bufferedReader.close();
		    }
		}
	}

	// TODO:
	// generate paths by linearly interpolating between location entries
	// update on tick
	// track/seek

}