package com.katlic.mpgtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MpgDbInterface extends SQLiteOpenHelper{

	private static final String DATABASE_NAME = "MpgTrackerUserData";
	private static final int DATABASE_VERSION = 1;
	
	private static final String VehicleInfo = "mpg_vehicle_information";
	private static final String VehicleInfo_id = "vehicle_id";
	private static final String VehicleInfo_make = "make";
	private static final String VehicleInfo_model = "model";
	private static final String VehicleInfo_year = "year";
	private static final String VehicleInfo_tranny = "tranny";
	
	private static final String UserLogs = "mpg_user_logs";
	private static final String UserLogs_id = "log_id";
	private static final String UserLogs_vehicle_id = "vehicle_id";
	private static final String UserLogs_date = "date";
	private static final String UserLogs_comment = "comment";
	private static final String UserLogs_gal = "gal";
	private static final String UserLogs_miles = "miles";
	private static final String UserLogs_mpg = "mpg";
	
	private static final String CREATE_VEHICLE_INFO =  //this will create the database (our two tables)
			"CREATE TABLE IF NOT EXISTS " + VehicleInfo
			+ "(" 
			+ VehicleInfo_id + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
			+ VehicleInfo_make + " TEXT NOT NULL, "
			+ VehicleInfo_model + " TEXT NOT NULL, "
			+ VehicleInfo_year + " TEXT, "
			+ VehicleInfo_tranny + " TEXT "
			+ ");";
	
	private static final String CREATE_USER_LOGS = 
			"CREATE TABLE IF NOT EXISTS " + UserLogs
			+ "(" 
			+ UserLogs_id + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
			+ UserLogs_vehicle_id + " INTEGER NOT NULL, "
			+ UserLogs_date + " TEXT NOT NULL, "
			+ UserLogs_comment + " TEXT, "
			+ UserLogs_gal + " TEXT NOT NULL, "
			+ UserLogs_miles + " TEXT NOT NULL, "
			+ UserLogs_mpg + " TEXT NOT NULL "
			+ ");";
	
	private SQLiteDatabase mpgTrackerDDB;
	
	public MpgDbInterface(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mpgTrackerDDB = getWritableDatabase();	//this is super important. will be used to write to the db everytime.
	}

	@Override
	public void onCreate(SQLiteDatabase db) { //Runs our create statement
		db.execSQL(CREATE_VEHICLE_INFO);
		db.execSQL(CREATE_USER_LOGS);
		Log.i("MPG TRACKER", "Creating Table " + VehicleInfo + " in " + DATABASE_NAME);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { //Will upgrade our DB
		onCreate(db); //cheap way to update haha.
	}

	public void manageDbUserVehicle(String mode, String make, String model, String year, String tran, String id){ //will let us add new vehicles to our DB
		ContentValues values = new ContentValues();
		values.put(VehicleInfo_make, make);
		values.put(VehicleInfo_model, model);
		values.put(VehicleInfo_year, year);
		values.put(VehicleInfo_tranny, tran);
		
		mpgTrackerDDB.delete(VehicleInfo, null, null); //empty the table
		mpgTrackerDDB.insert(VehicleInfo, null, values); //refill the table (no sense for the extra code when we only allow one vehicle (currently!!! expansion point here? I think so)
		
		/*switch(mode){ //the expansion code already written? WHAAAAAT?!? haha
			case "insert"://run an insert
				mpgTrackerDDB.insert(VehicleInfo, null, values);
				break;
			case "update"://run an update
				String strFilter = VehicleInfo_id + " = " + Integer.parseInt(id);
				mpgTrackerDDB.update(VehicleInfo, values, strFilter, null);
				break;		}*/
	}
	
	public void manageDbUserLogs(String mode, String date, String comment, String gal, String miles, String mpg, String id){ //will let us add new vehicles to our DB
			//Build our data object
		ContentValues values = new ContentValues();
		values.put(UserLogs_date, date);
		values.put(UserLogs_comment, comment);
		values.put(UserLogs_gal, gal);
		values.put(UserLogs_miles, miles);
		values.put(UserLogs_mpg, mpg);
		values.put(UserLogs_vehicle_id, 0);
		String strFilter;
		switch(mode){ //This will use our data object in the way the caller wanted.
			case "insert": //run an insert
				mpgTrackerDDB.insert(UserLogs, null, values);
				break;
			case "update": //run an update
				strFilter = UserLogs_id + "=" + Integer.parseInt(id);
				mpgTrackerDDB.update(UserLogs, values, strFilter, null);
				break;
			case "delete": //run a delete 
				strFilter = UserLogs_id + "=" + Integer.parseInt(id);
				mpgTrackerDDB.delete(UserLogs, strFilter, null);
				break;
		}
	}
	
	public Cursor getMpgDbInformation(String info_wanted, String id){
			//Set values that will be set below
		String get_id = UserLogs_id + "=" + Integer.parseInt(id);
		Cursor data = null; //holds the cursor to be returned
		switch(info_wanted){
			case "vehicle"://get user vehicle information
				//need to add the get_id here if you want to add more vehicles *as shown*
				data = mpgTrackerDDB.query(VehicleInfo, new String[]{VehicleInfo_make, VehicleInfo_model, VehicleInfo_year, VehicleInfo_tranny}, null/*get_id*/, null, null, null, null, null);
				break;
			case "logs"://get user vehicle logs
				data = mpgTrackerDDB.query(UserLogs, new String[]{UserLogs_id, UserLogs_date, UserLogs_comment, UserLogs_gal, UserLogs_miles, UserLogs_mpg}, null, null, null, null, UserLogs_date, null);
				break;
			case "log"://get A log's info
				data = mpgTrackerDDB.query(UserLogs, new String[]{UserLogs_date, UserLogs_comment, UserLogs_gal, UserLogs_miles, UserLogs_mpg}, get_id, null, null, null, UserLogs_date, null);				break;
		}
		
		return data;
	}
}
