package edu.uw.BG;

import java.util.List;

import android.app.Activity;
import android.app.SearchManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.android.map.Callout;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.CallbackListener;
import com.esri.core.map.FeatureSet;
import com.esri.core.map.Field;
import com.esri.core.map.Graphic;
import com.esri.core.tasks.ags.query.Query;

import edu.uw.BG.model.Plant;


public class UWArboretumActivity extends Activity  implements OnQueryTextListener{
	
	MapView mMapView;
	Envelope env;
	boolean m_isMapLoaded;
	Graphic m_identifiedGraphic;
	private Callout m_callout;
	private int m_calloutStyle;
	private ViewGroup calloutContent;
    private Point mapPoint;
    Graphic grSymbol;
    GraphicsLayer grLayer;
    int gID=-1;
    Envelope en;
    Point mLocation;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);     
       
		//mMapView = new MapView(this);
		mMapView=(MapView) findViewById(R.id.map);
		
		
		 
        LocationDisplayManager ls = mMapView.getLocationDisplayManager();
        
		ls.setLocationListener(new MyLocationListener());
		ls.start();
		ls.setAutoPanMode(LocationDisplayManager.AutoPanMode.OFF);
        
		m_callout=mMapView.getCallout();
		calloutContent=(ViewGroup) getLayoutInflater().inflate(R.layout.identify_callout_content, null);
		m_callout.setContent(calloutContent);
		m_calloutStyle = R.xml.identify_calloutstyle;
		
		//adjust to UWBG map extents
		
		//env=new Envelope(-1.3296373526814876E7, 3930962.41823043, -1.2807176545789773E7, 4201243.7502468005);
		//env=new Envelope(1277698.7250303626, 231387.09607818723,1282477.4996485263, 244562.67943646014);
		env=new Envelope(1279108.190939039, 231920.20797632635, 1280635.8646959215, 237807.50744993985);	
		
		/*mMapView.addLayer(new ArcGISTiledMapServiceLayer("" +
				"http://services.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer"));
		*/
		mMapView.addLayer(new ArcGISDynamicMapServiceLayer( //the address for the forest photo, the background
				"http://uwbgmaps.cfr.washington.edu/arcgis/rest/services/UWBG/MapServer"));
	    mMapView.addLayer(new ArcGISDynamicMapServiceLayer(//the address for the street addresses
		"http://uwbgmaps.cfr.washington.edu/arcgis/rest/services/Basemaps/MapServer"));
        mMapView.addLayer(new ArcGISDynamicMapServiceLayer( //the address for the all the layers except basemap 
         "http://uwbgmaps.cfr.washington.edu/arcgis/rest/services/PublicFeatures/MapServer"));
		mMapView.addLayer(new ArcGISFeatureLayer(
	    		"http://uwbgmaps.cfr.washington.edu/arcgis/rest/services/PublicFeatures/MapServer/3",ArcGISFeatureLayer.MODE.ONDEMAND));
	    
		grLayer=new GraphicsLayer();
		mMapView.addLayer(grLayer);
      
		
		mMapView.setOnStatusChangedListener(new OnStatusChangedListener(){

			
			private static final long serialVersionUID = 1L;

			public void onStatusChanged(Object source, STATUS status) {
				 if ((source == mMapView) && (status == STATUS.INITIALIZED)) {
			          // Set the flag to true					 
			 			mMapView.setExtent(env);
			 			//mMapView.zoomTo(new Point(1280131.4888656288, 236022.7787359506),(float) 0.5);
			 			//mMapView.zoomToResolution(new Point(1280131.4888656288, 236022.7787359506),20.0);
			          m_isMapLoaded = true;
			        }		
			} 
			
		});
		
		 mMapView.setOnSingleTapListener(new OnSingleTapListener() {

		      private static final long serialVersionUID = 1L;

		   
		      public void onSingleTap(float x, float y) {

		        if (m_isMapLoaded) {
		          // If map is initialized and Single tap is registered on
		          // screen
		          // identify the location selected
		        	Log.i("test","Single tap");
		          identifyLocation(x, y);
		        }
		      }
		    });
    }
    
    void identifyLocation(float x, float y) {

        // Hide the callout, if the callout from previous tap is still showing
        // on map
        if (m_callout.isShowing()) {
          m_callout.hide();
        }  
        if(grLayer.getGraphic(gID)!=null) grLayer.removeGraphic(gID);

        // Find out if the user tapped on a feature
        SearchForFeature(x, y);

      }

   
      private void SearchForFeature(float x, float y) {
    	  Log.i("test","search for");
        mapPoint = mMapView.toMapPoint(x, y);

        if (mapPoint != null) {
 
          for (Layer layer : mMapView.getLayers()) {
            if (layer == null)
              continue;

            if (layer instanceof ArcGISFeatureLayer) {
            	Log.i("test","feature layer");
              ArcGISFeatureLayer fLayer = (ArcGISFeatureLayer) layer;
              
              GetFeature(fLayer, x, y);
            } else
              continue;
          }
        }
      }

      private void GetFeature(ArcGISFeatureLayer fLayer, float x, float y) {
    	 // Point mapaPoint = mMapView.toMapPoint(x, y);
    	  Log.i("test","point"+x+" "+y+" "+mapPoint.getX()+" "+mapPoint.getY());
    	  Query q=new Query();    	  
    	  en=new Envelope(mapPoint , 10, 10);
    	  //q.setGeometry(new Point(1279818.7759083658, 235077.4850228727));
    	  q.setGeometry(en);
          fLayer.queryFeatures(q, callback);
       
        return;
      }
      
      CallbackListener<FeatureSet> callback = new CallbackListener<FeatureSet>() {

          public void onCallback(FeatureSet fSet) {           
            Log.i("test","at call back");
            Graphic[] gs=fSet.getGraphics();
            Integer[] ids=fSet.getObjectIds();
            List<Field> flist=fSet.getFields();
            String name="init";
            if(ids!=null) Log.i("testid",ids[0]+"");
            if(gs!=null){
          	  name=(String) gs[0].getAttributeValue("Plants.Name");
          	  Log.i("test","at call back and graphic not null"+name);
            
           /* for(String t:gs[0].getAttributeNames()){
            	Log.i("test","attribute name: "+t+" ");
            }*/
          	  
            showCallout(m_callout, gs[0]);
            
            }
          }
          public void onError(Throwable arg0) {
              
          }
      };
      
      private void showCallout(Callout calloutView, Graphic graphic) {

    	    // Get the values of attributes for the Graphic
    	  Plant plant=new Plant();
          plant.plantName=(String) graphic.getAttributeValue("Plants.Name");
          plant.family=(String) graphic.getAttributeValue("BGBaseData.Family");
          plant.familyCommonName=(String) graphic.getAttributeValue("BGBaseData.FamilyCommonName");
          plant.cultivarName=(String) graphic.getAttributeValue("BGBaseData.CultivarName");
          plant.genus=(String) graphic.getAttributeValue("Plants.Genus");
          plant.HerbariumSpecimen=(String) graphic.getAttributeValue("BGBaseData.HerbariumVoucherNumber");
          plant.lastReportedCondition=(String) graphic.getAttributeValue("BGBaseData.Condition");
          plant.mapGrid=(String) graphic.getAttributeValue("Plants.Grid ");
          plant.scientificName=(String) graphic.getAttributeValue("BGBaseData.ScientificName");
          plant.source=(String) graphic.getAttributeValue("BGBaseData.SourceCity");
          plant.UWBGAccession=(String) graphic.getAttributeValue("Plants.Accession");

    	    // Set callout properties
    	  calloutView.setCoordinates(mapPoint);
    	  calloutView.setStyle(m_calloutStyle);
    	  calloutView.setMaxWidth(325); 
    	  
          
    	  TextView planttv=(TextView) calloutContent.findViewById(R.id.plantname);
    	  TextView familytv=(TextView) calloutContent.findViewById(R.id.family);
    	  planttv.setText(plant.plantName);
    	  familytv.setText(plant.family);
    	
    	  calloutView.setContent(calloutContent);
    	  calloutView.show();
    	  
    	  /*SimpleMarkerSymbol sms = new SimpleMarkerSymbol(Color.RED, 15, SimpleMarkerSymbol.STYLE.CIRCLE);
          grSymbol=new Graphic(mapPoint, sms);         
          gID=grLayer.addGraphic(grSymbol);*/
          

    	  }

	@Override
	protected void onDestroy() {
		super.onDestroy();
 }
	@Override
	protected void onPause() {
		super.onPause();
		mMapView.pause();
 }
	@Override
	protected void onResume() {
		super.onResume();
		mMapView.unpause();
	}
	
	private class MyLocationListener implements LocationListener {
		 
		public MyLocationListener() {
			super();
		}
	 
		public void onLocationChanged(Location loc) {
			if (loc == null)
				return;
			Log.i("test","at location "+loc.getLatitude()+" "+loc.getLongitude());
			boolean zoomToMe = (mLocation == null) ? true : false;
			mLocation = new Point(loc.getLongitude(), loc.getLatitude());
			if (zoomToMe) {
				Point p = (Point) GeometryEngine.project(mLocation, SpatialReference.create(4326),
                        mMapView.getSpatialReference()); 
				
				 /*SimpleMarkerSymbol sms = new SimpleMarkerSymbol(Color.RED, 15, SimpleMarkerSymbol.STYLE.CIRCLE);
				 grSymbol=new Graphic(p, sms);
				grLayer.addGraphic(grSymbol);*/
				 Log.i("test","GPSLOC: "+p.getX()+" "+p.getY());
				mMapView.zoomToResolution(p, 20.0);
			} 
		}
	 
			public void onProviderDisabled(String provider) {
				Toast.makeText(getApplicationContext(), "GPS Disabled. To see your location on the map please enble the GPS",
						Toast.LENGTH_SHORT).show();
			}
	 
			public void onProviderEnabled(String provider) {
				Toast.makeText(getApplicationContext(), "GPS Enabled",
						Toast.LENGTH_SHORT).show();
			}
	 
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	 
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		    Log.i("test","at create option menu");
	 		getMenuInflater().inflate(R.menu.menu, menu);
	 		SearchView searchView= (SearchView) menu.findItem(R.id.search).getActionView();
	 		
	 		searchView.setOnQueryTextListener(this);
	 		
	 	    searchView.setIconifiedByDefault(true);
	 	    searchView.setQueryHint("Search UW Arboretum");
	 	    
	 		return true;
	}

	public boolean onQueryTextSubmit(String query) {
		
		//create a asynctask and query plants
		
		return true;
	}

	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub
		return false;
	}


}