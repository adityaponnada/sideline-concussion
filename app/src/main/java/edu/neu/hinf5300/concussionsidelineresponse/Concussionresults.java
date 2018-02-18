package edu.neu.hinf5300.concussionsidelineresponse;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class Concussionresults extends AppCompatActivity {


    private String TAG = "CONCUSSION RESULTS";
    SharedPreferences preferences;
    SharedPreferences.Editor sharedPreferencesEditor;
    int[] pupilSizes = new int[4];
    //final static int RQS_1 = 1;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concussion_results);
        context = this;

        preferences = getSharedPreferences("CSRPreferences", Context.MODE_PRIVATE);


        Button continueTesting = (Button)findViewById(R.id.continueTest);
        Button callButton = (Button)findViewById(R.id.callButton);
        Button mainPageButton = (Button)findViewById(R.id.main_page_button);
        RelativeLayout noConcussionRL = (RelativeLayout)findViewById(R.id.concussion_results_no_concussion_rl);
        RelativeLayout concussionRL = (RelativeLayout)findViewById(R.id.concussion_results_concussion_rl);

        TextView concussionTV = (TextView)findViewById(R.id.concussion_results_concussion_detected);
        TextView noConcussionTV = (TextView) findViewById(R.id.concussion_results_no_concussion_detected);

        Log.d(TAG, "IN CONCUSSION RESULTS ONCREATE");

        if (CSRConstants.fromPage.equals(CSRConstants.secondWorkingMemoryString)){
            compareConcussionScoresToBaselineScores();
            continueTesting.setText("Main Page");
        }


        if (CSRConstants.isLikelyConcussed) {
            concussionRL.setVisibility(View.VISIBLE);
            noConcussionRL.setVisibility(View.GONE);
            concussionTV.setText(context.getString(R.string.urgent) + CSRConstants.concussionDetectionString + " " + context.getString(R.string.concussion_detected_contact_pcp));
        } else {
            concussionRL.setVisibility(View.GONE);
            noConcussionRL.setVisibility(View.VISIBLE);
            noConcussionTV.setText(CSRConstants.concussionDetectionString);
        }

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                //TODO switch this from Lily's number as the default.
                String number = preferences.getString(CSRConstants.playerArrayName + CSRConstants.pcpNumber, "tel:7817528704");
                intent.setData(Uri.parse(number));
                startActivity(intent);
            }
        });

        mainPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //// TODO: 04/12/16 check if this works

                setAlarmService(300);

                Intent intent = new Intent(Concussionresults.this, MainActivity.class);
                startActivity(intent);


            }
        });

        continueTesting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CSRConstants.fromPage.equals(CSRConstants.symptomsString)){
                    Intent intent = new Intent(Concussionresults.this, PupilINstruction.class);
                    startActivity(intent);
                } else if (CSRConstants.fromPage.equals(CSRConstants.pupilWidth1String)){
                    Log.d(TAG, "Got HERE -- Intent");
                    Intent intent2 = new Intent(Concussionresults.this, ReactionTimeActivity.class);
                    startActivity(intent2);
                } else {

                    setAlarmService(300);
                    Intent intent3 = new Intent(Concussionresults.this, MainActivity.class);
                    startActivity(intent3);
                }


            }
        });

    }
        
        


    /*
    return true if the comparison indicates a concussion, return false if the comparison does not
     */
    private void compareConcussionScoresToBaselineScores(){
        int score = 0;
        //compare reaction time strings
        long reactionTimeBaseline = preferences.getLong(CSRConstants.playerUserName + CSRConstants.baselineString + CSRConstants.reactionTimeString, 0);
        long reactionTimeConcussion = preferences.getLong(CSRConstants.playerUserName + CSRConstants.concussionString + CSRConstants.reactionTimeString, 0);
        //TODO set threshold
        if (reactionTimeConcussion > reactionTimeBaseline){
            score++;
            Log.d(TAG, "Reaction Time, Score: " + score);
        }
        int wmBaseline = preferences.getInt(CSRConstants.playerUserName + CSRConstants.baselineString + CSRConstants.workingMemoryString, 0);
        int wmConcussion = preferences.getInt(CSRConstants.playerUserName + CSRConstants.concussionString + CSRConstants.workingMemoryString, 0);
        if (wmConcussion < wmBaseline){
            score++;
            Log.d(TAG, "WM, Score: " + score);
        }
        int swmBaseline = preferences.getInt(CSRConstants.playerUserName + CSRConstants.baselineString + CSRConstants.secondWorkingMemoryString, 0);
        int swmConcussion = preferences.getInt(CSRConstants.playerUserName + CSRConstants.concussionString + CSRConstants.secondWorkingMemoryString, 0);
        if (swmConcussion < swmBaseline){
            score++;
            Log.d(TAG, "SWM, Score: " + score);
        }
        int vizmBaseline = preferences.getInt(CSRConstants.playerUserName + CSRConstants.baselineString + CSRConstants.visualMemoryString, 0);
        int vizmConcussion = preferences.getInt(CSRConstants.playerUserName + CSRConstants.concussionString + CSRConstants.visualMemoryString, 0);
        if (vizmConcussion < vizmBaseline){
            score++;
            Log.d(TAG, "Viz, Score: " + score);
        }

        int semiTandemBaseline = preferences.getInt(CSRConstants.playerUserName + CSRConstants.baselineString + CSRConstants.semiTandemBalanceString, 0);
        int semiTandemConcussion = preferences.getInt(CSRConstants.playerUserName + CSRConstants.concussionString + CSRConstants.semiTandemBalanceString, 0);
        if (semiTandemConcussion < semiTandemBaseline ){
            score++;
            Log.d(TAG, "ST Balance, Score: " + score);
        }

        int tandemBaseline = preferences.getInt(CSRConstants.playerUserName + CSRConstants.baselineString + CSRConstants.tandemBalanceString, 0);
        int tandemConcussion = preferences.getInt(CSRConstants.playerUserName + CSRConstants.concussionString + CSRConstants.tandemBalanceString, 0);
        if (tandemConcussion < tandemBaseline ){
            score++;
            Log.d(TAG, "T Balance, Score: " + score);
        }

        //TODO set thresholds
        Log.d(TAG, "Score: " + score);
        if (score > 2){
            CSRConstants.isLikelyConcussed = true;
            CSRConstants.concussionDetectionString = context.getString(R.string.concussion_detected_after_memory_tests);
        } else {
            CSRConstants.isLikelyConcussed = false;
            CSRConstants.concussionDetectionString = context.getString(R.string.no_concussion_detected_after_memory_test);

        }

    }

    private void setAlarmService (int seconds){

        Toast.makeText(getApplicationContext(), "Setting follow up tests for the player ...", Toast.LENGTH_LONG).show();

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(Concussionresults.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.d("ALARM: ", "SET");

        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.SECOND, seconds);
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

        //Concussionresults.this.finish();

    }

}
