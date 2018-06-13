package nd.edu.bluenet_testbed;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import javafx.util.Pair;

import nd.edu.bluenet_stack.DummyBLE;
import nd.edu.bluenet_stack.Coordinate;


public class Sandbox {
	private List<Agent> mAgents = new ArrayList<Agent>();
	private DummyBLE mBLE = null;

	public class CoordinateTag {
		public Coordinate mCoord = null;
		public String mTag = null;

		public CoordinateTag(Coordinate coord, String tag) {
			mCoord = coord;
			mTag = tag;
		}
	}

	//TODO: take an xml file with configuration and list of trace files
	//pass each path to initAgent

	public Sandbox(String confFilename, String traceDir, boolean globalCtrl) {
		mBLE = new BleDispatcher(globalCtrl);
		initAgents(traceDir);
	}

	public Sandbox (String confFilename, String traceDir) {
		mBLE = new BleDispatcher();
		initAgents(traceDir);
	}

	public void finish() {
		if (mBLE.getClass() == BleDispatcher.class) {
			((BleDispatcher)mBLE).finish();
		}
	}

	private void initAgents (String traceDir) {
		// String dir = this.getClass().getClassLoader().getResource(traceDir).getFile();
		// File[] files = new File(dir).listFiles();
		// 

		File dir = new File ("/home/josh/projects/bluenet_testbed/" + traceDir);
		//System.err.println(dir);
	    if (null != dir) {
		    for (File file : dir.listFiles()) {
		    	//System.err.println(file);
		        Agent newAgent = new Agent(file.getPath());
				newAgent.setBLELayer(mBLE);
				mAgents.add(newAgent);
				//System.out.println(file.getPath() + " " + newAgent.getID());
		    }

		    for (Agent agent1: mAgents) {
		    	for (Agent agent2: mAgents) {
		    		((BleDispatcher)mBLE).setNearbyState(agent1.getID(), agent2.getID(), 1);
		    	}
		    }
		}
		else {
			System.err.println("Couldn't find folder!");
		}
		

	}

	private void updateAgent(long timeStep) {
		for (Agent agent: mAgents) {
			agent.update(timeStep);
		}

		//check state of nearby agents to determine whether they
		//can talk to each other
		//
		

	}

	//TODO: figure out how to do appropriate time scaling
	public void update (long locationTimeStep) {
		updateAgent(locationTimeStep);

		//will do nothing unless global control is set
		if (mBLE.getClass() == BleDispatcher.class) {
			((BleDispatcher)mBLE).update();
		}
	}

	public CoordinateTag [] getLocations () {
		CoordinateTag [] coords = new CoordinateTag[mAgents.size()];
		int i = 0;
		for (Agent agent : mAgents) {
			coords[i] = new CoordinateTag(agent.getLocation(), agent.getID());
			i++;
		}

		return coords;
	}
	
}