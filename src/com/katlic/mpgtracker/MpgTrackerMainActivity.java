package com.katlic.mpgtracker;


import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class MpgTrackerMainActivity extends ActionBarActivity implements OnClickListener{

	final Context main_activity = this;
	TextView make, model, year, tranny;
	
	Toast popUpMessage;
	MpgDbInterface mpg_db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mpg_tracker_main);

		mpg_db = new MpgDbInterface(this);
		
		make = (TextView)findViewById(R.id.frontEndMakeLabel);
		model = (TextView)findViewById(R.id.frontEndModelLabel);
		year = (TextView)findViewById(R.id.frontEndYearLabel);
		tranny = (TextView)findViewById(R.id.frontEndTransmissionLabel);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.mainUserViewContainer, new PlaceholderFragment()).commit();
		}
	}
	
	@Override
	public void onResume(){ //this lets us hook into the method that gets called every time this activity moves to the foreground
		super.onResume();
		//check for vehicle information in the DB
		
		Cursor vehicle_info = mpg_db.getMpgDbInformation("vehicle", "-1"); //load vehicle info into the right spots
		Log.i("MPG_TRACKER", "Data returned: " + Boolean.toString(vehicle_info.moveToFirst()));
		if(vehicle_info.moveToFirst()){  //makes sure there is data in our DB table
			popUpMessage = Toast.makeText(main_activity, "Now loading vehicle and log info..", Toast.LENGTH_LONG);
			popUpMessage.show();
			make.setText(getResources().getString(R.string.main_user_make_label) + " " + vehicle_info.getString(0));
			model.setText(getResources().getString(R.string.main_user_model_label) + " " + vehicle_info.getString(1));
			year.setText(getResources().getString(R.string.main_user_year_label) + " " + vehicle_info.getString(2));
			tranny.setText(getResources().getString(R.string.main_user_transmission_label) + " " + vehicle_info.getString(3));
		}else{
			//if none exists prompt them to add vehicle info
			// then go to the settings activity so they can do that.
			popUpMessage = Toast.makeText(main_activity,  "Please add a vehicle first.", Toast.LENGTH_LONG);
			popUpMessage.show();
			Intent go_to_settings = new Intent(main_activity, MpgTrackerSettingsActivity.class);
			startActivity(go_to_settings);
		}
		
			//get user logs
			//we will count how many loops we have and add up each MPG entry then divide it to get an average and display that to the user.
		Cursor user_logs = mpg_db.getMpgDbInformation("logs", "-1"); //load all log information for that vehicle into the scrollable table.
		int loop_count = 0;
		if(user_logs.moveToFirst()){
				// Get the TableLayout
	        TableLayout user_log_table = (TableLayout)findViewById(R.id.userDataTableLayoutContainer);
	        user_log_table.removeAllViews();
	        do{	//Go through each row in the cursor (the do..while lets us get the first row of data.
	        	Log.i("MPG_TRACKER", "Adding log: " + loop_count);
	            	// Create a TableRow and give it an ID
	            TableRow tr = new TableRow(main_activity);
	            tr.setId(Integer.parseInt(user_logs.getString(0)));
	            tr.setLayoutParams(new LayoutParams(
	                    LayoutParams.MATCH_PARENT,
	                    LayoutParams.WRAP_CONTENT));   
	            //this works it goes through and adds all of the values in the row as they should display on the screen.
	            tr.addView( CreateTextView(user_logs.getString(1) ) );
	            tr.addView( CreateTextView(user_logs.getString(2) ) );
	            tr.addView( CreateTextView(user_logs.getString(3) ) );
	            tr.addView( CreateTextView(user_logs.getString(4) ) );
	            tr.addView( CreateTextView(user_logs.getString(5) ) );
	            tr.setClickable(true);
	            tr.setOnClickListener(this);
	            	// Add the TableRow to the TableLayout
	            user_log_table.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
	            loop_count++;
	        } while(user_logs.moveToNext());
	        user_log_table.refreshDrawableState();
		}
		Log.i("MPG_TRACKER", "Logs returned: " + Integer.toString(user_logs.getCount()));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mpg_tracker_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		Intent newIntent; //need to first create the intent to open another activity
		switch(item.getItemId()){
			case R.id.action_settings:
				newIntent = new Intent(main_activity, MpgTrackerSettingsActivity.class);
				startActivityForResult(newIntent, 0); //this is done so that we can return data back to this activity
				break;
			case R.id.add_new_log:
				newIntent = new Intent(main_activity, MpgTrackerAddNewLogActivity.class);
				startActivityForResult(newIntent, 0); //a built in function to start activities based on intents
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public TextView CreateTextView(String text){
		// Create a TextView to house the comment value
		
		//LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		//params.setMargins(0,0,2,0);
		
        TextView new_text_view = new TextView(main_activity);
        new_text_view.setText(text + "    ");
        new_text_view.setTextColor(Color.BLACK);
        //new_text_view.setLayoutParams(params);
        /*new_text_view.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));*/
        return new_text_view;
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
					R.layout.fragment_mpg_tracker_main, container, false);
			return rootView;
		}
	}

	@Override
	public void onClick(View v) {
		//Load up the add new log activity and make it an update. 
		TableRow clickedRow = (TableRow)v;
		
		Intent newIntent = new Intent(main_activity, MpgTrackerAddNewLogActivity.class);
		
		newIntent.putExtra(MpgTrackerAddNewLogActivity.LOG_ID, clickedRow.getId());
		
		startActivityForResult(newIntent, 0); //a built in function to start activities based on intents
		
		
		// pass the ID to the addNewLog activity so it can load up the info for the user.
		popUpMessage = Toast.makeText(main_activity,  "Loading that up." + Integer.toString(clickedRow.getId()), Toast.LENGTH_LONG);
		popUpMessage.show();
	}

}
