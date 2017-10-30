package com.find.wifitool.wifi;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.find.wifitool.R;
import com.find.wifitool.database.FloorLocation;
import com.find.wifitool.database.InternalDataBase;
import com.find.wifitool.internal.Constants;

import java.util.List;

/**
 * Created by akshay on 30/12/16.
 */

public class WifiArrayAdapter extends ArrayAdapter<WifiObject> {

    private static final String TAG = WifiArrayAdapter.class.getSimpleName();

    Context mContext;

    // Constructor
    public WifiArrayAdapter(Context mContext, int layoutResourceId, List<WifiObject> objects) {
        super(mContext, layoutResourceId, objects);
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final WifiObject wifiItem = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.wifi_list_item, parent, false);
        }

        // Getting UI components
        TextView wifiName = (TextView) convertView.findViewById(R.id.wifiName);
        TextView wifiGroup = (TextView) convertView.findViewById(R.id.fieldGrpName);
        TextView wifiUser = (TextView) convertView.findViewById(R.id.fieldUsrName);

        Button setExhibitButton = (Button) convertView.findViewById(R.id.setExhibitButton);
        setExhibitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String[] exhibitIDs = new String[Constants.exhibitMap.size()];
                int currIndex = 0;
                for(Integer key : Constants.exhibitMap.keySet()) {
                    exhibitIDs[currIndex] = key.toString();
                    currIndex++;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle("Learn Exhibit ID")
                        .setItems(exhibitIDs, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position of the selected item

                                int exhibitID = Integer.parseInt(exhibitIDs[which]);

                                InternalDataBase internalDataBase = new InternalDataBase(mContext);
                                FloorLocation floorLocation = internalDataBase.getLocation(wifiItem.wifiName);
                                floorLocation.setLocExhibitID(exhibitID);
                                internalDataBase.addLocation(floorLocation);
                            }
                        });

                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        Button setPrettyNameButton = (Button) convertView.findViewById(R.id.setPrettyNameButton);
        setPrettyNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext).setTitle("Set location name");
                final EditText editText = new EditText(mContext);
                builder.setView(editText);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strLocationNamePretty = editText.getText().toString();

                        InternalDataBase internalDataBase = new InternalDataBase(mContext);
                        FloorLocation floorLocation = internalDataBase.getLocation(wifiItem.wifiName);
                        floorLocation.setLocNamePretty(strLocationNamePretty);
                        internalDataBase.addLocation(floorLocation);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        InternalDataBase internalDataBase = new InternalDataBase(mContext);
        FloorLocation floorLocation = internalDataBase.getLocation(wifiItem.wifiName);

        // Setting UI components
        wifiName.setText(floorLocation.getLocNamePretty() + " (" + wifiItem.wifiName + ")");
        wifiGroup.setText(wifiItem.grpName);
        wifiUser.setText(wifiItem.userName);

        return convertView;
    }
}
