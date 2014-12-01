package edu.gw.krunal.britelitebeta;

import android.app.ListActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Date;
import java.util.List;


public class Subscription extends ListActivity {
    private Button mSelectSubscription;
    private int PREVIOUS_SELECTED_PLAN = -1;
    private String KEY_PREVIOUS_SELECTED_PLAN = "previous_selected_plan";
    private ParseUser currentUser;
    private String SELECTED_PLAN ="";
    private String KEY_SELECTED_PLAN ="selected_plan_key";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        currentUser=ParseUser.getCurrentUser();

        //check if anything in savedInstanceState handling rotate.
        if(savedInstanceState!=null) {
            PREVIOUS_SELECTED_PLAN = savedInstanceState.getInt(KEY_PREVIOUS_SELECTED_PLAN);
            SELECTED_PLAN = savedInstanceState.getString(KEY_SELECTED_PLAN);
        }
        else {
            if(currentUser.getBoolean("subscriptiontaken"))
                SELECTED_PLAN = currentUser.getString("subscriptionID");
        }

        mSelectSubscription = (Button) findViewById(R.id.select_subscription);
        mSelectSubscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //handling if there isn't any plan selected.(New User)
                TextView clickedView;
                if(PREVIOUS_SELECTED_PLAN != -1) {
                    View v = getListView().getChildAt(PREVIOUS_SELECTED_PLAN);
                    clickedView = (TextView) v.findViewById(R.id.name);
                }
                else {
                    makeToast(getResources().getString(R.string.at_least_select_one_plan));
                    return;
                }


                if(currentUser.getBoolean("subscriptiontaken")&& !SELECTED_PLAN.equals(""))
                {
                     if(SELECTED_PLAN.equalsIgnoreCase(clickedView.getText().toString())){
                         // in case same plan has been selected.
                         makeToast(getResources().getString(R.string.same_subscription_selected_message));
                         return;
                     }
                     else
                     {
                         //setting current time for start date.
                         Date currentTime = new Date();
                         currentUser.put("subscriptionStartDate",currentTime);
                         currentUser.put("subscriptionEndDate",addDays(currentTime,30));
                         currentUser.put("subscriptionID", clickedView.getText().toString());
                         currentUser.saveInBackground(new SaveCallback() {
                             @Override
                             public void done(ParseException e) {
                                 if(e==null)
                                     makeToast(getResources().getString(R.string.subscription_plan_renew_message));
                                 else
                                     makeToast(getResources().getString(R.string.save_error_toast_message));
                                 finish();
                             }
                         });

                     }

                }
                else
                {
                    Date currentTime = new Date();
                    currentUser.put("subscriptionStartDate",currentTime);
                    currentUser.put("subscriptionEndDate",addDays(currentTime,30));
                    currentUser.put("subscriptiontaken",true);
                    currentUser.put("subscriptionID",clickedView.getText().toString());
                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e==null) {
                                makeToast(getResources().getString(R.string.subscription_successfully_saved_message));
                                finish();
                            }
                        }
                    });
                }
               //makeToast("CurrentItem" + clickedView.getText().toString());
            }
        });


        ParseQuery<ParseObject> query = ParseQuery.getQuery("Subscription");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> subscriptionList, ParseException e) {
                if (e == null) {

                    //set Array adapter as you have list of parseobject.
                     SubscriptionArrayAdapter adapter = new SubscriptionArrayAdapter(Subscription.this,R.layout.subscription_row,subscriptionList);
                    setListAdapter(adapter);
                    //set adapter here.
                    //Set click listener.
                    getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            //handle first time user clicking  previous.
                            if(PREVIOUS_SELECTED_PLAN != -1) {
                                View previousView = getListView().getChildAt(PREVIOUS_SELECTED_PLAN);
                                previousView.setBackgroundColor(Color.TRANSPARENT);
                            }

                            //highlighting selected plan.
                            view.setBackgroundColor(getResources().getColor(R.color.menubar_background_color));
                            PREVIOUS_SELECTED_PLAN=position;

                        }
                    });

                    //set default selection.
                    if(currentUser.getBoolean("subscriptiontaken"))
                    {
                        //getting currently selected plan.
                        for(int i=0;i<subscriptionList.size();i++)
                        {
                            if(subscriptionList.get(i).getString("name").equalsIgnoreCase(currentUser.getString("subscriptionID"))) {
                                Log.d("SubscriptionTestingTag", "value of i"+i);
                                PREVIOUS_SELECTED_PLAN = i;
                                break;
                            }
                        }
                    }

                } else {
                   // Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
        if(PREVIOUS_SELECTED_PLAN != -1) {
            // new/old USER + just selected one subscription and rotated screen.
            View v = getListView().getChildAt(PREVIOUS_SELECTED_PLAN);
            currentUser.put("subscriptionID", ((TextView) v.findViewById(R.id.name)).getText().toString());
            currentUser.put("subscriptiontaken",true);
        }
        savedInstanceState.putInt(KEY_PREVIOUS_SELECTED_PLAN, PREVIOUS_SELECTED_PLAN);
        savedInstanceState.putString(KEY_SELECTED_PLAN,SELECTED_PLAN);
    }
    /**
     * Method found in stackoverflow.
     * @param d : current date
     * @param days: days to add.
     * @return Date object with "days" days added to d.
     */
    public static Date addDays(Date d, int days)
    {
        d.setTime(d.getTime() + days * 1000 * 60 * 60 * 24);
        return d;
    }
    /**
     * This function is general Toast and takes string and Toast it on screen.
     * @param text
     */
    private void makeToast(String text){
        Toast.makeText(Subscription.this,text,Toast.LENGTH_LONG).show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.subscription, menu);
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
