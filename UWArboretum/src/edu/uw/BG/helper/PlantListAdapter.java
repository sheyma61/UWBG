package edu.uw.BG.helper;

import java.util.ArrayList;
import java.util.List;

import com.esri.core.portal.Portal.GetAuthCodeCallback;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import edu.uw.BG.R;
import edu.uw.BG.model.Plant;

public class PlantListAdapter extends ArrayAdapter {
	
	ArrayList<Plant> plantList;
	Context mContext;
	boolean bookmarks;

	public PlantListAdapter(Context context, int resource, List objects, boolean bookmarks) {
		super(context, resource, objects);
		plantList=(ArrayList<Plant>) objects;
		mContext=context;
		this.bookmarks=bookmarks;
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return plantList.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v=convertView;
		final int pos=position;
		
		if(!bookmarks){
			
			if(v==null){
			
				v=LayoutInflater.from(mContext).inflate(R.layout.plant_list_element, null);
					
			}
		
			if(position%2==0) v.setBackgroundColor(mContext.getResources().getColor(R.color.RowBlue));
			else v.setBackgroundColor(mContext.getResources().getColor(R.color.RowPink));
				
			Plant plant=plantList.get(position);
			TextView name=(TextView) v.findViewById(R.id.plant_name);
		    TextView sciename=(TextView) v.findViewById(R.id.scientific_name);
		    TextView accession=(TextView) v.findViewById(R.id.accession);
		    name.setText(plant.plantName);
		    sciename.setText(plant.family);
		    accession.setText(plant.UWBGAccession);
		}
		
		
		else {
			if(v==null){
				
				v=LayoutInflater.from(mContext).inflate(R.layout.bookmark_list_element, null);
					
			}
			Plant plant=plantList.get(position);
			TextView name=(TextView) v.findViewById(R.id.plant_name);
		    TextView sciename=(TextView) v.findViewById(R.id.scientific_name);
		    TextView accession=(TextView) v.findViewById(R.id.accession);
		    name.setText(plant.plantName);
		    sciename.setText(plant.family);
		    accession.setText(plant.UWBGAccession);
		    
		    ImageButton removeB=(ImageButton) v.findViewById(R.id.remove_bookmark);
		   
		    removeB.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
										
					DeleteBookmarkAsyncTask dba=new DeleteBookmarkAsyncTask();
					dba.execute(plantList.get(pos).UWBGAccession);
					
					plantList.remove(pos);
				    notifyDataSetChanged();
					
				}
			});
			
		}
		return v;
	}
	
	class DeleteBookmarkAsyncTask extends AsyncTask<String, Void, Integer>{

		@Override
		protected Integer doInBackground(String... params) {
			int res=0;
			SQliteHelper sqlh=new SQliteHelper(mContext,"",null,1);
			SQLiteDatabase sql=sqlh.getWritableDatabase();
			synchronized(sql){
				res=sql.delete("bookmarkplants", "accession='"+params[0]+"'", null);	
				sql.close();
			}
			
			sqlh.close();
			return res;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
		}
		
		
		
	}//DeleteAsyncTask

	@Override
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub
		return true;
	}
	
	

}
