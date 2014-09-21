package com.katlic.mpgtracker;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.os.Build;

public class MpgTrackerSettingsActivity extends ActionBarActivity implements OnClickListener {

	EditText make,model,year;
	Spinner tranny;
	MpgDbInterface mpg_db;
	Toast popUpMessage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mpg_tracker_settings);
		
		//if there is any data in the db then add all of it to the form.
		
		Button B = (Button)findViewById(R.id.mpg_vehicle_info_save_button);
		B.setOnClickListener(this);
		
		make = (EditText)findViewById(R.id.mpg_settings_vehicle_make_input);
		model = (EditText)findViewById(R.id.mpg_settings_vehicle_model_input);
		year = (EditText)findViewById(R.id.mpg_settings_vehicle_year_input);
		tranny = (Spinner)findViewById(R.id.mpg_settings_vehicle_tranny_select);
		
		mpg_db = new MpgDbInterface(this);
		
		Cursor vehicle_info = mpg_db.getMpgDbInformation("vehicle", "-1"); //load vehicle info into the right spots
		Log.i("MPG_TRACKER", "Data returned: " + Boolean.toString(vehicle_info.moveToFirst()));
		if(vehicle_info.moveToFirst()){  //makes sure there is data in our DB table
			make.setText(vehicle_info.getString(0));
			model.setText(vehicle_info.getString(1));
			year.setText(vehicle_info.getString(2));
			
			ArrayAdapter myAdap = (ArrayAdapter) tranny.getAdapter(); //cast to an ArrayAdapter
			tranny.setSelection(myAdap.getPosition(vehicle_info.getString(3)));
		}
		
		//add the vehicle info the form.
		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(
					R.layout.fragment_mpg_tracker_settings, container, false);
			return rootView;
		}
	}

	@Override
	public void onClick(View arg0) {
		popUpMessage = Toast.makeText(this, "Saving your vehicle information!", Toast.LENGTH_LONG);
		popUpMessage.show();
		
		// Save the information to the DB then return to the main activity.
		mpg_db.manageDbUserVehicle("update", make.getText().toString(), model.getText().toString(), year.getText().toString(), tranny.getSelectedItem().toString(), null);
		
		Intent myReturn = new Intent();
		setResult(this.RESULT_OK, myReturn);
		finish();
	}

}
