package nd.edu.bluenet_testbed;

import java.util.List;
import java.util.ArrayList;
//import java.nio.file.BufferedReader;
import java.io.*;

import nd.edu.bluenet_stack.LocationManager;

public class LocationPlayback {

	public class LocationEntry {
		public double mLatitude;
		public double mLongitude;
		public long mTimestamp; //milliseconds

		public LocationEntry(long timestamp, double lat, double lon) {
			mTimestamp = timestamp;
			mLatitude = lat;
			mLongitude = lon;
		}
	}

	private List<LocationEntry> mEntries = new ArrayList<LocationEntry>();
	private LocationEntry mCurrent = null;
	private int mIndex = 0; //points to most recent known location

	public LocationPlayback () {

	}

	public LocationPlayback(String fileName) {
		initialize(fileName);
	}

	public void initialize(String filename) {
		final int TIMESTAMP = 0;
		final int LATITUDE = 1;
		final int LONGITUDE = 2;

		String line = null;
		boolean first = true;

		try {
			//from: https://alvinalexander.com/java/java-bufferedreader-readline-string-examples
		    // wrap a BufferedReader around FileReader
		    BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));

		    // use the readLine method of the BufferedReader to read one line at a time.
		    // the readLine method returns null when there is nothing else to read.
		    long firstTime = 0;
		    while ((line = bufferedReader.readLine()) != null)
		    {
		    	//Assuming the line is formatted as timestamp latitude longitude
		    	
		        String[] parts = line.split("\\s+");
		        
		        if (first) {
		        	firstTime = Long.parseLong(parts[TIMESTAMP]);
		        	first = false;
		        }


		        mEntries.add(new LocationEntry(Long.parseLong(parts[TIMESTAMP]) - firstTime, 
		        	Double.parseDouble(parts[LATITUDE]),
		        	Double.parseDouble(parts[LONGITUDE])));
		    }
		  
		    // close the BufferedReader when we're done
		    bufferedReader.close();
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}

		mCurrent = mEntries.get(mIndex); //get the first one.
	}

	public void update() {
		update(1);
	}

	public void update(long timeIndexDiff) {
		if (timeIndexDiff > 0) {
			boolean done = false;
			for (int i = mIndex; i < mEntries.size() && !done; i++) {
				if (timeIndexDiff + mCurrent.mTimestamp < mEntries.get(i).mTimestamp) {
					mIndex = i - 1; //we overshot it so go back 1
					done = true;
				}
			}

			//exact match
			if (timeIndexDiff + mCurrent.mTimestamp == mEntries.get(mIndex).mTimestamp) {
				mCurrent = mEntries.get(mIndex);
			}
			else { //linearly interpolate a position based on constant speed between known locs
				mCurrent = expectedLoc(mEntries.get(mIndex), 
									  mEntries.get(mIndex + 1), 
									  timeIndexDiff + mCurrent.mTimestamp);
			}

		}
	}

	public void seek(long timeIndex) {
		//positive or negative: binary search
	}

	public int size() {
		return mEntries.size();
	}

	public LocationEntry getLocation() {
		return mCurrent;
	}

	private LocationEntry expectedLoc(LocationEntry first, LocationEntry second, long timeIndex) {
		// System.out.println ("first and second entries:");
		// System.out.print(first.mLatitude);
		// System.out.print (" ");
		// System.out.println (first.mLongitude);

		// System.out.print(second.mLatitude);
		// System.out.print (" ");
		// System.out.println (second.mLongitude);
		// System.out.println("-----------------------------");

		double fraction = (timeIndex - first.mTimestamp) / (double)(second.mTimestamp - first.mTimestamp);
		//https://stackoverflow.com/questions/38767074/intermediate-points-between-2-geographic-coordinates
		//https://www.movable-type.co.uk/scripts/latlong.html

		// System.out.print("fraction: ");
		// System.out.println(fraction);
		double dist = fraction * LocationManager.distance(first.mLatitude, first.mLongitude, 
			second.mLatitude, second.mLongitude);

		// System.out.print("total vs fraction: ");
		// System.out.print(LocationManager.distance(first.mLatitude, first.mLongitude, 
		// 	second.mLatitude, second.mLongitude));
		// System.out.print(" vs ");
		// System.out.println(dist);

		double lat1 = first.mLatitude;
		double lon1 = first.mLongitude;
		double lat2 = second.mLatitude;
		double lon2 = second.mLongitude;

		double constant = Math.PI / 180;
        double angular = dist / 6371000;
        double a = Math.sin((1-fraction) * angular) / Math.sin(angular);
        double b = Math.sin(fraction * angular) / Math.sin(angular);
        double x = a * Math.cos(lat1 * constant) * Math.cos(lon1 * constant) + 
                   b * Math.cos(lat2 * constant) * Math.cos(lon2 * constant);
        double y = a * Math.cos(lat1 * constant) * Math.sin(lon1 * constant) + 
                   b * Math.cos(lat2 * constant) * Math.sin(lon2 * constant);
        double z = a * Math.sin(lat1 * constant) + b * Math.sin(lat2 * constant);
        double lat3 = Math.atan2(z, Math.sqrt(x * x + y * y));
        double lon3 = Math.atan2(y, x);

		return new LocationEntry(timeIndex, lat3 / constant, lon3 / constant);
	}


}