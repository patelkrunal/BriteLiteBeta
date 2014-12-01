package edu.gw.krunal.britelitebeta;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;
/**
 * Created by Mukta on 11/7/2014.
 */
public class SubscriptionArrayAdapter extends ArrayAdapter<ParseObject> {
    private List<ParseObject> mParseObjects;
    private final Context mContext;
    private ParseUser currentUser;
    public SubscriptionArrayAdapter(Context context, int resource,  List<ParseObject> objects) {
        super(context, resource, objects);
        mParseObjects = objects;
        mContext = context;
        currentUser=ParseUser.getCurrentUser();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.subscription_row, parent, false);

        //getting correct parse object.


        ParseObject mParseObject = mParseObjects.get(position);


        TextView priceTextView = (TextView) rowView.findViewById(R.id.price);
        TextView nameTextView = (TextView) rowView.findViewById(R.id.name);
        TextView descriptionTextView = (TextView) rowView.findViewById(R.id.description);


        priceTextView.setText("$"+Integer.toString(mParseObject.getInt("price")));
        nameTextView.setText(mParseObject.getString("name"));
        descriptionTextView.setText(mParseObject.getString("description"));

        //setting the color of current subscription view
        if(currentUser.getBoolean("subscriptiontaken"))
        {
            if(currentUser.getString("subscriptionID").equalsIgnoreCase(mParseObject.getString("name")))
                rowView.setBackgroundColor(rowView.getResources().getColor(R.color.menubar_background_color));
        }
        return rowView;
    }
}
