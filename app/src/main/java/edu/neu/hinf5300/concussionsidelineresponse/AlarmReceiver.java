package edu.neu.hinf5300.concussionsidelineresponse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by adityaponnada on 04/12/16.
 */
public class AlarmReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {

        //Toast.makeText(context, "Setting up repeated surveys", Toast.LENGTH_SHORT).show();
        Intent secIntent = new Intent(context, SymptomActivity.class);
        secIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(secIntent);
    }
}
