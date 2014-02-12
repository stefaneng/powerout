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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

public class PowerReceiver extends BroadcastReceiver {
    private static final String TAG = "Power Receiver";

    public void onReceive(Context context, Intent intent) {
        switch (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)) {
            case 0:
                // The device is running on battery
                Log.i(TAG, "Running on battery");
                break;
            case BatteryManager.BATTERY_PLUGGED_AC:
                // Implement your logic
                Log.i(TAG, "Plugged in");
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
                // Implement your logic
                Log.i(TAG, "Plugged into usb");
                break;
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                // Implement your logic
                Log.i(TAG, "Battery plugged wireless");
                break;
            default:
                // Unknown state
                Log.i(TAG, "Unknown state");
                break;
        }
    }
}
