package edu.gw.krunal.britelitebeta;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.RefreshCallback;


public class DashboardActivity extends Activity {
    private ParseUser currentUser;
    private TextView mWelcomeMessage;
    private String DASHBOARD_TESTING_TAG = "dashboard activity Testing";
    private ProgressBar mProgress;
    private TextView mProgressBarStatus;
    private TextView mSubscriptionStatus;
    private TextView mAddressStatus;
    private TextView mServiceWindowStatus;
    private Button mSubscriptionButton;
    private Button mAddressButton;
    private Button mServiceWindowButton;
    private int SUBSCRIPTION_REQUEST_CODE = 1;
    private int ADDRESS_REQUEST_CODE = 2;
    private int SERVICE_WINDOW_REQUEST_CODE = 3;
    private String currentAddress="";
    private int PROGRESS_BAR=5;
    private int DEFAULT_PROGRESS_BAR = 1;
    private int PROGRESS_BAR_INCREMENT = 33;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private String[] mDrawerListViewItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);



        //Wire it Up
        mWelcomeMessage = (TextView) findViewById(R.id.welcome_message_id);
        mProgress = (ProgressBar) findViewById(R.id.profile_progress_bar);
        mSubscriptionStatus = (TextView)findViewById(R.id.subscription_status);
        mAddressStatus = (TextView) findViewById(R.id.address_status);
        mServiceWindowStatus= (TextView)findViewById(R.id.service_window_status);
        mSubscriptionButton= (Button)findViewById(R.id.subscription_button);
        mAddressButton= (Button) findViewById(R.id.add_address);
        mServiceWindowButton = (Button)findViewById(R.id.service_window_button);
        mProgressBarStatus =(TextView)findViewById(R.id.progress_bar_status);
        mDrawerLayout=(DrawerLayout)findViewById(R.id.drawerLayout);
        mDrawerListView=(ListView)findViewById(R.id.drawerListView);

        mDrawerListViewItem =getResources().getStringArray(R.array.drawer_array);

        mDrawerListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,mDrawerListViewItem));
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                makeToast(mDrawerListViewItem[i]);
            }
        });
        //add listeners
        mSubscriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent subscriptionActivityIntent = new Intent(DashboardActivity.this,Subscription.class);
                startActivityForResult(subscriptionActivityIntent,SUBSCRIPTION_REQUEST_CODE);
            }
        });

        mAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addressActivityIntent = new Intent(DashboardActivity.this,AddressActivity.class);
                startActivityForResult(addressActivityIntent,ADDRESS_REQUEST_CODE);
            }
        });

        mServiceWindowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serviceWindowActivityIntent = new Intent(DashboardActivity.this,ServiceWindowActivity.class);
                startActivityForResult(serviceWindowActivityIntent,SERVICE_WINDOW_REQUEST_CODE);
            }
        });


        //check internet connection...
        if(!MyUtils.CheckInternet(DashboardActivity.this)) {
            makeToast(getResources().getString(R.string.internet_not_available_error));
        }
        else
            fillDashboard();




    }

    private void fillDashboard(){

        //reset progress bar to default
        PROGRESS_BAR = DEFAULT_PROGRESS_BAR;
        // Retrieve current user from Parse.com
        currentUser = ParseUser.getCurrentUser();

        //setting welcome message.
        mWelcomeMessage.setText(getResources().getString(R.string.welcome_message) +" "+currentUser.getString("name"));

        //Subscription status.
        if(currentUser.getBoolean("subscriptiontaken"))
        {
            PROGRESS_BAR+=PROGRESS_BAR_INCREMENT;
            //subscription taken true action
            mSubscriptionStatus.setText(getResources().getString(R.string.subscription_status_positive)+" "+currentUser.getDate("subscriptionEndDate").toString());
            mSubscriptionButton.setText(getResources().getString(R.string.subscription_positive_button));

        }
        else
        {
            //false action
            mSubscriptionStatus.setText(getResources().getString(R.string.subscription_status_negative));
            mSubscriptionButton.setText(getResources().getString(R.string.subscription_negative_button));
        }

        //Check Address already exist ?
        if(currentUser.getBoolean("contact_added"))
        {
            PROGRESS_BAR+=PROGRESS_BAR_INCREMENT;
            //address in database
            currentAddress = " "+currentUser.getString("addressLine1") +", "+ currentUser.getString("apartment") +", "+ currentUser.getString("city")+", "+currentUser.getString("state");
            mAddressStatus.setText(getResources().getString(R.string.address_status_positive) + currentAddress);
            mAddressButton.setText(getResources().getString(R.string.change_address));
        }else{
            //address not in database
            mAddressStatus.setText(getResources().getString(R.string.address_status_negative));
            mAddressButton.setText(getResources().getString(R.string.add_address));

        }

        //check if the preference is set or not ?
        if(currentUser.getBoolean("userPreferenceAdded"))
        {
            PROGRESS_BAR+=PROGRESS_BAR_INCREMENT;
            //preference is already added.
            mServiceWindowStatus.setText("Your pickup is scheduled on "+currentUser.getString("pickupDay")+ " at "+ currentUser.getString("pickupTime"));
            mServiceWindowButton.setText(getResources().getString(R.string.service_window_button_change));
        }
        else
        {
            //There is no preference set.
            mServiceWindowStatus.setText(getResources().getString(R.string.service_window_status_negative));
            mServiceWindowButton.setText(getResources().getString(R.string.Set_Service_Window));
        }
        //set the progress bar.
        mProgress.setProgress(PROGRESS_BAR);
        if(PROGRESS_BAR==100)
            mProgressBarStatus.setText(getResources().getString(R.string.profile_complete_message));
        else
            mProgressBarStatus.setText(getResources().getString(R.string.profile_progress_message)+String.valueOf(PROGRESS_BAR)+"%");
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data){
        //called when return from settings page.
        currentUser.refreshInBackground(new RefreshCallback() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                //check internet connection...
                if(!MyUtils.CheckInternet(DashboardActivity.this)) {
                    makeToast(getResources().getString(R.string.internet_not_available_error));
                }
                else
                    fillDashboard();
            }
        });

    }
    /**
     * This function is general Toast and takes string and Toast it on screen.
     * @param text
     */
    private void makeToast(String text){
        Toast.makeText(DashboardActivity.this,text,Toast.LENGTH_LONG).show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id)
        {
            case R.id.action_logout:
                ParseUser.logOut();
                finish();
                break;
            case R.id.action_photo_share:
                Intent photoSharingActivityIntent = new Intent(DashboardActivity.this,PhotoSharingActivity.class);
                startActivity(photoSharingActivityIntent);
                break;
            case R.id.action_add_address:
                Intent addressActivityIntent = new Intent(DashboardActivity.this, AddressActivity.class);
                startActivity(addressActivityIntent);
                break;
            case R.id.action_service_window:
                Intent serviceWindowActivityIntent = new Intent(DashboardActivity.this, ServiceWindowActivity.class);
                startActivity(serviceWindowActivityIntent);
                break;
            case R.id.action_subscription:
                Intent subscriptionActivityIntent = new Intent(DashboardActivity.this, Subscription.class);
                startActivity(subscriptionActivityIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
