package edu.uw.BG;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import edu.uw.BG.helper.AddBookmarkAsync;
import edu.uw.BG.model.Plant;


public class PlantFullFragment extends Fragment{
	Plant plant;

	public PlantFullFragment(Plant p) {
		super();
		plant=p;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.plant_full_layout, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		if(plant==null) return;
		
	    View v=getView();
	    ((TextView) v.findViewById(R.id.plant_name)).setText(plant.plantName);
	    ((TextView) v.findViewById(R.id.family)).setText(plant.family);
	    ((TextView) v.findViewById(R.id.family_common)).setText(plant.familyCommonName);
	    ((TextView) v.findViewById(R.id.accession)).setText(plant.UWBGAccession);
	    ((TextView) v.findViewById(R.id.genus)).setText(plant.genus);
	    ((TextView) v.findViewById(R.id.scientificn)).setText(plant.scientificName);
	    ((TextView) v.findViewById(R.id.specific_epi)).setText(plant.epithet);
	    ((TextView) v.findViewById(R.id.source)).setText(plant.source);
	    ((TextView) v.findViewById(R.id.map_grid)).setText(plant.mapGrid);
	    ((TextView) v.findViewById(R.id.report)).setText(plant.lastReportedCondition);
	    
	    Button addtoBookmarkButton=(Button) v.findViewById(R.id.addBookmarks);
	    
	    
	    addtoBookmarkButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
		         AddBookmarkAsync addbookasync=new AddBookmarkAsync(getActivity());
		         if(plant!=null) addbookasync.execute(new Plant[]{plant});
		         Toast.makeText(getActivity(), "Added to BookMarks", Toast.LENGTH_SHORT).show();
				
			}
		});
	    
		super.onActivityCreated(savedInstanceState);
	}

	
	

}
