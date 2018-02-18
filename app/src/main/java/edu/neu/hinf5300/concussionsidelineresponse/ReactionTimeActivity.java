package edu.neu.hinf5300.concussionsidelineresponse;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import edu.neu.hinf5300.concussionsidelineresponse.BalanceTest.Level;

public class ReactionTimeActivity extends AppCompatActivity {

    public Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor sharedPreferencesEditor;

    public CountDownTimer timer;
    private String TAG = "REACTION TIME";
    private int TIME_BETWEEN_CHANGES = 1000;
    private int NUM_BUTTON_IN_ROUND = 20; //it will be this number plus 1

    private ArrayList<Integer> COLOR_ORDER = new ArrayList<Integer>(); //0 is red, 1 and 2 are yellow
    private ArrayList<Long> reactionTimesArray = new ArrayList<Long>();

    private int numberOfButtonsAppeared;

    private long START_TIME;

    ImageButton targetButton;

    private long averageReactionTime;
    private int numYellowsHit;

    //soundpool for audio cues
    private int mSoundHit;
    private SoundPool mSoundPool;
    private float mVolume = 1f;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reaction_time);

        context = this;
        preferences = getSharedPreferences("CSRPreferences", Context.MODE_PRIVATE);
        sharedPreferencesEditor = preferences.edit();
        sharedPreferencesEditor.commit();

        //load sounds into the sound pool.
        mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        mSoundHit = mSoundPool.load(this, R.raw.erkanozan_miss, 1);

        numberOfButtonsAppeared = 1;

        Random r = new Random();
        for (int i = 0; i < NUM_BUTTON_IN_ROUND+1; i++) {
            COLOR_ORDER.add(r.nextInt(3)); //approximately 1/3 of the buttons will be red
            reactionTimesArray.add(Long.valueOf(0));
        }

        final RelativeLayout handToPlayerRL = (RelativeLayout)findViewById(R.id.reaction_time_hand_to_player_rl);
        final RelativeLayout reactionTimeRL = (RelativeLayout)findViewById(R.id.reaction_time_rl);


        if (CSRConstants.isBaseline){
            handToPlayerRL.setVisibility(View.VISIBLE);
            reactionTimeRL.setVisibility(View.GONE);
            Button isPlayerButton = (Button)findViewById(R.id.reaction_time_hand_to_player_button);
            isPlayerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handToPlayerRL.setVisibility(View.GONE);
                    reactionTimeRL.setVisibility(View.VISIBLE);
                }
            });
        } else {
            handToPlayerRL.setVisibility(View.GONE);
            reactionTimeRL.setVisibility(View.VISIBLE);
        }


        final TextView timerTextView = (TextView) findViewById(R.id.timer_TextView);
        final TextView instructionsTextView = (TextView)findViewById(R.id.reaction_time_instructions_TextView);
        targetButton = (ImageButton) findViewById(R.id.target_Button);
        targetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordTime();
                mSoundPool.play(mSoundHit, mVolume, mVolume, 1, 0, 1f);
            }
        });

        final Button startButton = (Button)findViewById(R.id.reaction_time_start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instructionsTextView.setText(getResources().getString(R.string.reaction_time_instructions_2));
                startButton.setVisibility(View.GONE);
                startButton.setClickable(false);
                timerTextView.setVisibility(View.VISIBLE);
                targetButton.setVisibility(View.VISIBLE);
                startTimer(timerTextView);
                START_TIME = System.currentTimeMillis();

            }
        });


    }


    public void startTimer(TextView view) {
        final TextView timerTextView = (TextView) view;
        timer = new CountDownTimer(TIME_BETWEEN_CHANGES, 1000) {
            public void onTick(long millisUntilFinished) {
                timerTextView.setText("Time: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                AbsoluteLayout.LayoutParams absParams =
                        (AbsoluteLayout.LayoutParams)targetButton.getLayoutParams();
                if (numberOfButtonsAppeared < NUM_BUTTON_IN_ROUND){                 //set number of targets to hit here
                    Log.d(TAG, "Number of Buttons: " + numberOfButtonsAppeared);
                    targetButton.setClickable(true);
                    ArrayList<Integer> bCoordinates = getNextButtonCoordinates();
                    absParams.x = bCoordinates.get(0);
                    absParams.y =  bCoordinates.get(1);
                    setButtonColor();
                }  else{
                    calculateScore();
                    storeScore();
                    Intent balanceTest = new Intent(ReactionTimeActivity.this, Level.class);
                    startActivity(balanceTest);
                }
                targetButton.setLayoutParams(absParams);
                numberOfButtonsAppeared ++;
                Log.d(TAG, "Number buttons: " +numberOfButtonsAppeared);
                startTimer(timerTextView);
            }
        }.start();
    }



    private ArrayList<Integer> getNextButtonCoordinates(){
        ArrayList<Integer> buttonCoordinates = new ArrayList<Integer>();
        Random r = new Random();
        buttonCoordinates.add(0,(r.nextInt(10)*50)); //set x coordinate, random int between 0-10 exclusive (times 10)
        buttonCoordinates.add(1,(r.nextInt(10)*50)); //set y coordinate, random int between 0-10 exclusive (times 10)
        Log.d(TAG, "X coord: " + buttonCoordinates.get(0));
        Log.d(TAG, "Y coord: " + buttonCoordinates.get(1));

        return buttonCoordinates;
    }

    /*
    Must be called AFTER calculateScore()
     */
    private void storeScore(){
        String s = "";
        String s2 = "";
        if(CSRConstants.isBaseline == true) {
            s = CSRConstants.playerUserName + CSRConstants.baselineString + CSRConstants.reactionTimeString;
            s2 = CSRConstants.playerUserName + CSRConstants.baselineString + CSRConstants.reactionTimeYellowDotString;

        } else{
            s = CSRConstants.playerUserName + CSRConstants.concussionString + CSRConstants.reactionTimeString;
            s2 = CSRConstants.playerUserName + CSRConstants.concussionString + CSRConstants.reactionTimeYellowDotString;
        }
        sharedPreferencesEditor.putLong(s, averageReactionTime);
        sharedPreferencesEditor.putInt(s2, numYellowsHit);
        sharedPreferencesEditor.commit();
    }

    private void calculateScore(){
        int num_red_targets = 0;
        long summed_reaction_time = 0;
        for (int i = 0; i < NUM_BUTTON_IN_ROUND; i++){
            if (COLOR_ORDER.get(i) == 0 ){ //if the target is red
                num_red_targets++;
                if (reactionTimesArray.get(i) == 0){ //if the targer was not hit
                    summed_reaction_time = summed_reaction_time + 1;
                } else {
                    summed_reaction_time = summed_reaction_time + reactionTimesArray.get(i);
                }
            } else { //else if the target is yellow
                if (reactionTimesArray.get(i) != 0){ //if the target was hit
                    numYellowsHit++;
                }
            }
        }
        averageReactionTime = summed_reaction_time/num_red_targets;
        Log.d(TAG, "Average Reaction Time: " + averageReactionTime);
        Log.d(TAG, "Yellows Hit: " + numYellowsHit);
        /*
        Toast t = Toast.makeText(context, "Average Reaction Time: " + averageReactionTime + "\nNumber Yellows Hit: " + numYellowsHit, Toast.LENGTH_LONG);
        t.show();
        */

    }


    private void recordTime(){
        Log.d(TAG, "Reaction Time Got here:");
        long reactionTime = System.currentTimeMillis() - START_TIME;
        Log.d(TAG, "Start Time: " + START_TIME);
        Log.d(TAG, "Current Time: " + System.currentTimeMillis());
        Log.d(TAG, "Reaction Time 1: " + reactionTime);
        reactionTime = reactionTime%TIME_BETWEEN_CHANGES;
        Log.d(TAG, "Reaction Time 2: " + reactionTime);
        reactionTimesArray.set(numberOfButtonsAppeared,reactionTime);
        printArray(reactionTimesArray);
        targetButton.setClickable(false); //to ensure that we record the first hit (not a second or third) of a targer button

        String s = "";
        for(int i = 0; i<10; i++){
            s = s + reactionTimesArray.get(i) + ", ";
        }
        Log.d(TAG, "Array: " + s);
        sharedPreferencesEditor.putString("ReactionTimes", s).commit();
    }

    private void setButtonColor(){
        if(COLOR_ORDER.get(numberOfButtonsAppeared) == 0){
            targetButton.setBackgroundResource(R.drawable.reddot);
        } else {
            targetButton.setBackgroundResource(R.drawable.yellowdot);
        }
    }



    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
        try{
            timer.cancel();
        }catch (NullPointerException e){
        }


    }

    private void printArray(ArrayList<Long> ar){
        String s = "ARRAY: ";
        for (int i = 0; i < ar.size(); i++){
            s = s + ar.get(i) + ", ";
        }
        Log.d(TAG, s);
    }


}
