package edu.uw.BG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;


/**
 * @author Shima Akhavanfarid
 *
 */

public class First_Activity extends Activity {
	
	FirstFragment firstfrag;
	PlantLookUpFragment plantlookupfrag;
	BookMarkListFragment bookmarkfrag;
	boolean home_flag=true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first_);
		
		if(!isNetworkAvailable()){
			Toast.makeText(this, "Please connect your device to the Internet", Toast.LENGTH_LONG).show();
		}

 		firstfrag=new FirstFragment();
 		plantlookupfrag=new PlantLookUpFragment(findViewById(android.R.id.content));
 		bookmarkfrag=new BookMarkListFragment();
 		
 		getFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, firstfrag).
 		commit();
 		//getFragmentManager().executePendingTransactions();
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		    Log.i("test","at create option menu");
		    
	 		getMenuInflater().inflate(R.menu.menu, menu);
	 		SearchView searchView= (SearchView) menu.findItem(R.id.search).getActionView();
	 		Log.i("test",Build.VERSION.SDK_INT+"");
	 		//searchView.setOnQueryTextListener(this);
	 		//ImageView v=(ImageView) searchView.findViewById(getResources().getIdentifier("android:id/search_button", null, null));
	 		//v.setImageResource(R.drawable.glass);
	 	    //searchView.setIconifiedByDefault(true);
	 	    //searchView.setQueryHint("Plant LookUp");
	 		
	 		
	 		
	 		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch(item.getItemId()){
	    case R.id.home:
	    	if(!home_flag){
	    	home_flag=true;
	    	getFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, firstfrag).
	    	commit();
	    	//getFragmentManager().executePendingTransactions();
	    	}
	    	break;
	    
	    case R.id.maps:
	    	home_flag=false;
	    	Intent intent=new Intent(this,UWArboretumActivity.class);
	    	startActivity(intent);
	    	break;
	    case R.id.search:
	    	home_flag=false;
	    	getFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, plantlookupfrag).
	    	commit();
	    	//getFragmentManager().executePendingTransactions();
	    	break;
	    case R.id.bookmarks:
	    	home_flag=false;
	    	getFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, bookmarkfrag).
	    	commit();
	    	break;	
	    }
		
		return true;
	}
	
	 public boolean isNetworkAvailable() {
	        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
	        // if no network is available networkInfo will be null
	        // otherwise check if we are connected
	        if (networkInfo != null && networkInfo.isConnected()) {
	            return true;
	        }
	        return false;
	    }
	
	
	
}
