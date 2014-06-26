/*
 * Copyright (C) 2014 Stefan Eng
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.powerout;

import android.app.Activity;
import android.content.ComponentName;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.content.SharedPreferences.Editor;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.power.out.R;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends Activity {
    static final String TAG = "com.powerout.MainActivity";
    static final int PICK_CONTACT_REQUEST = 1;

    private ArrayList<String> listItems;
    private ListView myListView;
    private ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        PackageManager pm = getPackageManager();
        ComponentName pluggedReceiver = new ComponentName(getApplicationContext(), PluggedReceiver.class);
        ComponentName unpluggedReceiver = new ComponentName(getApplication(), UnpluggedReceiver.class);
        pm.setComponentEnabledSetting(
                pluggedReceiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
        );

        pm.setComponentEnabledSetting(
                unpluggedReceiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
        );

        myListView = (ListView) findViewById(R.id.contactView);
        listItems =  new ArrayList<String>();

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);


        Map<String,?> keys = prefs.getAll();
        for(Map.Entry<String,?> entry : keys.entrySet()){
            listItems.add(entry.getKey().toString());
        }

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems);
        myListView.setAdapter(adapter);

    }

    public void pickContact(View view) {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // Get the URI that points to the selected contact
                Uri contactUri = data.getData();
                // We only need the NUMBER column, because there will be only one row in the result
                String[] projection = {
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                };

                CursorLoader cursorLoader = new CursorLoader(getApplicationContext(),
                        contactUri, projection, null, null, null);

                Cursor cursor = cursorLoader.loadInBackground();

                cursor.moveToFirst();

                // Retrieve the phone number from the NUMBER column
                int numberColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(numberColumn);

                int nameColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String name = cursor.getString(nameColumn);

                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(this);

                Editor editor = sharedPreferences.edit();
                editor.putString(name, number);
                editor.commit();


                if(! listItems.contains(name)) {
                    listItems.add(name);
                    adapter.notifyDataSetChanged();
                }

                //ListView contactList = (ListView) findViewById(R.id.contactView);


                Log.i(TAG, "Phone number: " + String.valueOf(number));
                // Do something with the phone number...
            }
        }
    }

    public void stopReceivers(View view) {
        PackageManager pm = getPackageManager();
        ComponentName pluggedReceiver = new ComponentName(getApplicationContext(), PluggedReceiver.class);
        ComponentName unpluggedReceiver = new ComponentName(getApplication(), UnpluggedReceiver.class);
        pm.setComponentEnabledSetting(
                pluggedReceiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
        );

        pm.setComponentEnabledSetting(
                unpluggedReceiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
        );

        Button startButton = (Button) findViewById(R.id.startReceivers);
        Button stopButton = (Button) findViewById(R.id.stopReceivers);
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
    }

    public void startReceivers(View view) {
        PackageManager pm = getPackageManager();
        ComponentName pluggedReceiver = new ComponentName(getApplicationContext(), PluggedReceiver.class);
        ComponentName unpluggedReceiver = new ComponentName(getApplication(), UnpluggedReceiver.class);
        pm.setComponentEnabledSetting(
                pluggedReceiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
        );

        pm.setComponentEnabledSetting(
                unpluggedReceiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
        );

        Button startButton = (Button) findViewById(R.id.startReceivers);
        Button stopButton = (Button) findViewById(R.id.stopReceivers);
        stopButton.setEnabled(true);
        startButton.setEnabled(false);
    }

    public void clearNumbers(View view) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        listItems.clear();
        adapter.notifyDataSetChanged();
        Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}
