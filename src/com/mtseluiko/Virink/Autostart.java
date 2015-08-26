package com.mtseluiko.Virink;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Михаил on 11.08.2015.
 */
public class Autostart extends BroadcastReceiver {
    public void onReceive(Context context, Intent arg1) {
        Intent intent = new Intent(context, MyService.class);
        context.startService(intent);
    }}