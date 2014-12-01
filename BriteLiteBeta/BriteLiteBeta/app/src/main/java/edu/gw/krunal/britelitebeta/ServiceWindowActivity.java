package edu.gw.krunal.britelitebeta;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.HashMap;


public class ServiceWindowActivity extends Activity {
    private Spinner mPickupDaySpinner;
    private Spinner mPickupTimeSpinner;
    private Spinner mDropOffDaySpinner;
    private Spinner mDropOffTimeSpinner;
    private Button mSetPreferenceButton;
    private ParseUser currentUser;
    private HashMap<String,Integer> day_time = new HashMap<String, Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_window);
        //initialize HashMap
        if(!day_time.isEmpty())
            day_time.clear();

        //Hate to hard code but need to do this.
        day_time.put("Friday",0);
        day_time.put("Saturday",1);
        day_time.put("Sunday",2);
        day_time.put("Monday",3);
        day_time.put("8am - 9am",0);
        day_time.put("9am - 10am",1);
        day_time.put("10am - 11am",2);
        day_time.put("4pm - 5pm",3);
        day_time.put("5pm - 6pm",4);

        currentUser = ParseUser.getCurrentUser();

        //wire it up.
        mPickupDaySpinner = (Spinner)findViewById(R.id.pickup_day_array_spinner);
        mPickupTimeSpinner=(Spinner)findViewById(R.id.pickup_time_array_spinner);
        mDropOffDaySpinner =(Spinner)findViewById(R.id.dropoff_day_array_spinner);
        mDropOffTimeSpinner =(Spinner)findViewById(R.id.dropoff_time_array_spinner);
        mSetPreferenceButton =(Button)findViewById(R.id.set_preference_button);

        //add listner.
        mSetPreferenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Check internet connection
                if(!MyUtils.CheckInternet(ServiceWindowActivity.this)) {
                    makeToast(getResources().getString(R.string.internet_not_available_error));
                    return;
                }


                if(validateDateTime())
                {
                    //save the changes to database.
                    currentUser.put("pickupDay",mPickupDaySpinner.getSelectedItem().toString());
                    currentUser.put("pickupTime",mPickupTimeSpinner.getSelectedItem().toString());
                    currentUser.put("dropoffDay",mDropOffDaySpinner.getSelectedItem().toString());
                    currentUser.put("dropoffTime",mDropOffTimeSpinner.getSelectedItem().toString());
                    currentUser.put("userPreferenceAdded", true);
                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e!=null)
                                makeToast(e.getLocalizedMessage());
                            else {
                                makeToast("successfully added preference.");
                                finish();
                            }
                        }
                    });
                }
                else
                {
                    makeToast(getResources().getString(R.string.service_window_error_message));
                }
            }
        });

        //if already exist preference then set it.
        if(currentUser.getBoolean("userPreferenceAdded"))
        {
            fillSpinners();
        }
    }

    /**
     * this method will fill the Spinners of Service Window activity with user's preference.
     */
    private void fillSpinners(){
        if(day_time.containsKey(currentUser.getString("pickupDay")))
            mPickupDaySpinner.setSelection(day_time.get(currentUser.getString("pickupDay")));
        if(day_time.containsKey(currentUser.getString("pickupTime")))
            mPickupTimeSpinner.setSelection(day_time.get(currentUser.getString("pickupTime")));
        if(day_time.containsKey(currentUser.getString("dropoffDay")))
            mDropOffDaySpinner.setSelection(day_time.get(currentUser.getString("dropoffDay")));
        if(day_time.containsKey(currentUser.getString("dropoffTime")))
            mDropOffTimeSpinner.setSelection(day_time.get(currentUser.getString("dropoffTime")));


    }
    private void makeToast(String text){
        Toast.makeText(ServiceWindowActivity.this, text, Toast.LENGTH_LONG).show();
    }

    /**
     * This method validate 24 hour gap in between pickup and delivery
     * @return boolean value.
     */
    private Boolean validateDateTime(){
        if(mPickupDaySpinner.getSelectedItemPosition()>= mDropOffDaySpinner.getSelectedItemPosition())
            return false;
        else if(mPickupDaySpinner.getSelectedItemPosition()==mDropOffDaySpinner.getSelectedItemPosition()-1 && mPickupTimeSpinner.getSelectedItemPosition()>mDropOffTimeSpinner.getSelectedItemPosition())
            return false;
        else
            return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.service_window, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_back) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
