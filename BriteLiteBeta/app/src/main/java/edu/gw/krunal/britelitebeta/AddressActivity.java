package edu.gw.krunal.britelitebeta;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class AddressActivity extends Activity {
    private Button mAddContactDetailButton;
    private TextView mAddressLine1;
    private TextView mApartment;
    private TextView mCity;
    private TextView mCountry;
    private TextView mState;
    private TextView mZipCode;
    private TextView mPhone;
    private ParseUser currentUser;
    private String ADDRESS_ACTIVITY_TAG = "address_activity_debugging";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        //wire them up
        mAddressLine1 = (TextView)findViewById(R.id.address_addressline1_input);
        mApartment =(TextView)findViewById(R.id.address_apartment_input);
        mCity=(TextView)findViewById(R.id.address_city_input);
        mState =(TextView)findViewById(R.id.address_state_input);
        mCountry=(TextView)findViewById(R.id.address_country_input);
        mZipCode=(TextView)findViewById(R.id.address_zipcode_input);
        mPhone=(TextView)findViewById(R.id.address_phone_input);
        mAddContactDetailButton= (Button)findViewById(R.id.add_contact_detail);
        //setting current user
        currentUser=ParseUser.getCurrentUser();

        mAddContactDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAddressLine1.getText().toString().length()==0)
                    makeToast(getResources().getString(R.string.addressline1_toast_message));
                else if(mApartment.getText().length() == 0)
                    makeToast(getResources().getString(R.string.apartment_toast_message));
                else if(mCity.getText().length()==0)
                    makeToast(getResources().getString(R.string.city_toast_message));
                else if(mState.getText().length()==0)
                    makeToast(getResources().getString(R.string.state_toast_message));
                else if(mCountry.getText().length()==0)
                    makeToast(getResources().getString(R.string.country_toast_message));
                else if(mZipCode.getText().length()==0)
                    makeToast(getResources().getString(R.string.zipcode_toast_message));
                else if(mPhone.getText().length()==0)
                    makeToast(getResources().getString(R.string.phone_toast_message));
                else {
                    //All fields are available.
                    try{
                        int zipcode = Integer.parseInt(mZipCode.getText().toString());
                    }catch(NumberFormatException e)
                    {
                        makeToast(getResources().getString(R.string.zipcode_need_to_be_integer_error));
                        return;
                    }
                    try{
                        long phone= Long.parseLong(mPhone.getText().toString());
                    }catch(NumberFormatException e)
                    {
                        makeToast(getResources().getString(R.string.phone_need_to_be_integer_error)+e.getLocalizedMessage());
                        return;
                    }

                    //check internet Connection as this will save into database.
                    if(!MyUtils.CheckInternet(AddressActivity.this)) {
                        makeToast(getResources().getString(R.string.internet_not_available_error));
                        return;
                    }

                    //Add address into database.
                    currentUser.put("addressLine1",mAddressLine1.getText().toString());
                    currentUser.put("apartment",mApartment.getText().toString());
                    currentUser.put("city",mCity.getText().toString());
                    currentUser.put("state",mState.getText().toString());
                    currentUser.put("country",mCountry.getText().toString());
                    currentUser.put("zipcode",Integer.parseInt(mZipCode.getText().toString()));
                    currentUser.put("phone",Long.parseLong(mPhone.getText().toString()));
                    currentUser.put("contact_added",true);
                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e==null)
                            {
                                finish();
                                //save successful
                            }
                            else {
                                Log.d(ADDRESS_ACTIVITY_TAG,e.getLocalizedMessage());
                                //save unsuccessful
                                makeToast(getResources().getString(R.string.save_error_toast_message));
                            }
                        }
                    });

                }
            }
        });
        //Check if already exists ? yes - set default : No - empty form.
        if(currentUser.getBoolean("contact_added")){
            //fill the form with current value.
            fillForm();
        }
    }

    /**
     * This function will fill the form with current contact details.
     */
    private void fillForm(){
        mAddressLine1.setText(currentUser.getString("addressLine1"));
        mApartment.setText(currentUser.getString("apartment"));
        mCity.setText(currentUser.getString("city"));
        mState.setText(currentUser.getString("state"));
        mCountry.setText(currentUser.getString("country"));
        mZipCode.setText(String.valueOf(currentUser.getInt("zipcode")));
        mPhone.setText(String.valueOf(currentUser.getLong("phone")));
    }
    /**
     * This function is general Toast and takes string and Toast it on screen.
     * @param text
     */
    private void makeToast(String text){
        Toast.makeText(AddressActivity.this,text,Toast.LENGTH_LONG).show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.address, menu);
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
