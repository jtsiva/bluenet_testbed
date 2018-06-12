package nd.edu.bluenet_testbed;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Map;
import java.util.HashMap;
import java.util.List;


public class MainApp extends Application implements MapComponentInitializedListener {

final int MULTIPLIER = 3000;
final long ITERATION_PERIOD = 100;

GoogleMapView mapView;
GoogleMap map;


long mTicks = 0;
Sandbox mSandbox = null;
Timer mTimer = new Timer();
Map<String, Marker> mMarkers = new HashMap<String, Marker>();

@Override
public void start(Stage stage) throws Exception {
   List<String> args = getParameters().getRaw();
   //should be configFile, traceDir
   //
  
   
   mSandbox = new Sandbox("", "data/batch1");

    //Create the JavaFX component and set this as a listener so we know when 
    //the map has been initialized, at which point we can then begin manipulating it.
    mapView = new GoogleMapView();
    mapView.addMapInializedListener(this);

    Scene scene = new Scene(mapView);

    stage.setTitle("BlueNet Testbed");
    stage.setScene(scene);
    stage.show();
}

@Override
public void stop() throws Exception {
    mTimer.cancel();
    mSandbox.finish();
}


@Override
public void mapInitialized() {
    //Set the initial properties of the map.
    MapOptions mapOptions = new MapOptions();

    mapOptions.center(new LatLong(41.6999318,-86.2328356))
            .mapType(MapTypeIdEnum.ROADMAP)
            .overviewMapControl(false)
            .panControl(false)
            .rotateControl(false)
            .scaleControl(false)
            .streetViewControl(false)
            .zoomControl(false)
            .zoom(12);

    map = mapView.createMap(mapOptions);

    mTimer.scheduleAtFixedRate (new TimerTask () {
        @Override
        public void run() {
            //System.err.println(".");
            ++mTicks;
            mSandbox.update(ITERATION_PERIOD * MULTIPLIER);
            Sandbox.CoordinateTag [] coords = mSandbox.getLocations(); 
            //System.out.println(coords.length);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    updateMarkers(coords);
                }
            });
            
        }
    }, ITERATION_PERIOD, ITERATION_PERIOD);

}

private void updateMarkers(Sandbox.CoordinateTag [] coordinates) {
    //System.err.println(coordinates.length);
    for (int i = 0; i < coordinates.length; i++) {
        if (!mMarkers.containsKey(coordinates[i].mTag)) {
            MarkerOptions markerOps = new MarkerOptions();
            markerOps.position( new LatLong(coordinates[i].mCoord.mLatitude, coordinates[i].mCoord.mLongitude))
                    .visible(Boolean.TRUE)
                    .title(coordinates[i].mTag);
            Marker marker = new Marker(markerOps);
            map.addMarker(marker);
            mMarkers.put(coordinates[i].mTag, marker);
            System.out.print("New marker named: " + coordinates[i].mTag + " ");
            System.out.print(coordinates[i].mCoord.mLatitude);
            System.out.print(", ");
            System.out.println(coordinates[i].mCoord.mLongitude);
        }
        else {
            Marker marker = mMarkers.get(coordinates[i].mTag);
            marker.setPosition (new LatLong(coordinates[i].mCoord.mLatitude, coordinates[i].mCoord.mLongitude));
            //System.out.println("Updated marker named: " + coordinates[i].mTag);
            mMarkers.put(coordinates[i].mTag, marker);
        }
    }
}

public static void main(String[] args) {
    launch(args);
}
}