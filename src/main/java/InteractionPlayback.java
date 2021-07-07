package nd.edu.bluenet_testbed;

import java.util.List;
import java.util.ArrayList;
//import java.nio.file.BufferedReader;
import java.io.*;


public class InteractionPlayback {
	public class Interaction {
		private long mTimestamp = 0;
		private int mType = 0;
		private String mDest = null;
		private String mArgs = null;
	}

	private List<Interaction> mInteractions = new ArrayList<Interaction>();

	public InteractionPlayback () {

	}

	public InteractionPlayback (String filename) {
		
	}
}