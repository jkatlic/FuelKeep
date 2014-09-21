package com.katlic.mpgtracker;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class MpgTrackerAddNewLogActivity extends ActionBarActivity implements OnClickListener {
	
	//Add in edit text fields here.
	MpgDbInterface mpg_db;
	Toast popUpMessage;
	EditText comment, gal, miles, date;
	TextView headerLabel;
	String log_id_for_update;
	
	public static final String LOG_ID = "log_id";
	private boolean update = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mpg_tracker_add_new_log);
		
		Button save_log = (Button)findViewById(R.id.add_new_log_submit_button);
		save_log.setOnClickListener(this);
		
		Button remove_log = (Button)findViewById(R.id.remove_log_button);
		
			//Build the inputs in the class
		headerLabel = (TextView)findViewById(R.id.add_new_log_form_header);
		comment = (EditText)findViewById(R.id.mpg_add_log_comment_input);
		gal = (EditText)findViewById(R.id.mpg_add_log_gallons_input);
		miles = (EditText)findViewById(R.id.mpg_add_log_miles_input);
		date = (EditText)findViewById(R.id.mpg_add_log_date_input);
			//Build the classes DBI object
		mpg_db = new MpgDbInterface(this);
			//Get the intent of this activity.
		Intent editIntent = getIntent();
		
		Object log_number = editIntent.getSerializableExtra(LOG_ID);
		
		if(log_number == null){ //we clicked add
			//change the header string to Add new
			headerLabel.setText(R.string.add_new_log_form_label);
			save_log.setText(R.string.mpg_save);
			remove_log.setVisibility(View.INVISIBLE);
			update = false;
		}else{	//we clicked a log
			//change the header string to Edit Log
			headerLabel.setText(R.string.mpg_edit_old_activity_title);
			save_log.setText(R.string.mpg_update);
			remove_log.setVisibility(View.VISIBLE);
			remove_log.setOnClickListener(this);
			//get the id to hit the DBI class for the log info.
			String log_id = log_number.toString();
			
			Cursor log_info = mpg_db.getMpgDbInformation("log", log_id);
			//add the info into the inputs.
			if(log_info.moveToFirst()){
				log_id_for_update = log_id;
				comment.setText(log_info.getString(1));
				gal.setText(log_info.getString(2));
				miles.setText(log_info.getString(3));
				date.setText(log_info.getString(0));
			}
			update = true;
		}
		
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
			View rootView = inflater
					.inflate(R.layout.fragment_mpg_tracker_add_new_log,
							container, false);
			return rootView;
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.add_new_log_submit_button:
				//Add the new log to the DB then return to the main activity
				popUpMessage = Toast.makeText(this, "Saving your log information!", Toast.LENGTH_LONG);
				popUpMessage.show();
				
				String mpg = "";
				//calculate the mpg Miles / Gallons = mpg
				mpg = Integer.toString(Integer.parseInt(miles.getText().toString()) / Integer.parseInt(gal.getText().toString()));
				if(update){
					//update the information
					// Save the information to the DB then return to the main activity.
					mpg_db.manageDbUserLogs("update", date.getText().toString(), comment.getText().toString(), gal.getText().toString(), miles.getText().toString(), mpg, log_id_for_update);
				}else{
					// Save the information to the DB then return to the main activity.
					mpg_db.manageDbUserLogs("insert", date.getText().toString(), comment.getText().toString(), gal.getText().toString(), miles.getText().toString(), mpg, "-1");
				}
				break;
			
			case R.id.remove_log_button:
				popUpMessage = Toast.makeText(this, "Removing your log information!", Toast.LENGTH_LONG);
				popUpMessage.show();
				mpg_db.manageDbUserLogs("delete", null, null, null, null, null, log_id_for_update);
				break;
			default:
				break;
		}
		Intent myReturn = new Intent();
		setResult(this.RESULT_OK, myReturn);
		finish();
	}

}
