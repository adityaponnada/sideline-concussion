package edu.neu.hinf5300.concussionsidelineresponse;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

public class SymptomActivity extends AppCompatActivity {

    public Context context;
    private int score;
    private int playerScore; //an average of symptoms.
    private int PLAYER_SCORE_THRESHOLD = 5;
    private boolean isPlayer;

    //FOR COACH QUESTIONS
    private ArrayList<String> coachQuestions = new ArrayList<String>();
    private ArrayList<String> coachAnswers = new ArrayList<String>(); //answers that would indicate a concussion
    private ArrayList<String> selectedCoachAnswers = new ArrayList<String>(); // all "" mean no response recorded
    private int NUM_COACH_QUESTIONS = 3;


    //FOR PLAYER QUESTIONS
    private ArrayList<String> playerQuestions = new ArrayList<String>();
    private ArrayList<Integer> playerAnswers = new ArrayList<Integer>();  // starts out filled with 0s
    private ArrayList<String> selectedPlayerQuestions = new ArrayList<String>();


    private int NUM_PLAYER_QUESTIONS_ON_PAGE = 4;
    private int current_question; //the number of question sets that the player has gone through

    private String TAG = "Symptom Activity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom);

        context = this;
        score = 0;
        playerScore = 0;
        isPlayer = false;
        current_question = 0;
        for (int i = 0; i < NUM_COACH_QUESTIONS; i++){
            coachQuestions.add("");
            coachAnswers.add("");
            selectedCoachAnswers.add("");
        }

        for (int i = 0; i < NUM_PLAYER_QUESTIONS_ON_PAGE; i++){
            selectedPlayerQuestions.add("");
        }

        createCoachesQuestionsArray();
        createPlayerQuestionsArray();

        TextView coachQuestion1 = (TextView)findViewById(R.id.symptom_coach_question_1);
        TextView coachQuestion2 = (TextView)findViewById(R.id.symptom_coach_question_2);
        TextView coachQuestion3 = (TextView)findViewById(R.id.symptom_coach_question_3);

        final RelativeLayout coachLayout = (RelativeLayout)findViewById(R.id.symptoms_coach_relative_layout);
        final RelativeLayout playerLayout = (RelativeLayout)findViewById(R.id.symptoms_player_relative_layout);
        final RelativeLayout betweenLayout = (RelativeLayout)findViewById(R.id.symptom_coach_to_player_layout);



        coachQuestion1.setText(coachQuestions.get(0));
        coachQuestion2.setText(coachQuestions.get(1));
        coachQuestion3.setText(coachQuestions.get(2));

        Button submitButton = (Button)findViewById(R.id.symptom_coach_submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             if (areAllQuestionsAnswered()){
                 Log.d("ALARM SET: ", "STARTED");
                 //setAlarmService(300);
                 calculateSymptomScore();
                 coachLayout.setVisibility(View.GONE);
                 betweenLayout.setVisibility(View.VISIBLE);
                 isPlayer = true;

             } else {
                 Toast t = Toast.makeText(context, "Please Answer all of the questions", Toast.LENGTH_LONG);
                 t.show();
             }
            }
        });

        Button startButton = (Button) findViewById(R.id.symptom_coach_to_player_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                betweenLayout.setVisibility(View.GONE);
                fillInPlayerView();
                playerLayout.setVisibility(View.VISIBLE);
            }
        });

        RadioGroup coachAnswers1 = (RadioGroup)findViewById(R.id.symptom_coach_radiogroup_1);
        RadioGroup coachAnswers2 = (RadioGroup)findViewById(R.id.symptom_coach_radiogroup_2);
        RadioGroup coachAnswers3 = (RadioGroup)findViewById(R.id.symptom_coach_radiogroup_3);


       // coachAnswers1.setOnCheckedChangeListener(listener);
        coachAnswers1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                RadioButton rb=(RadioButton)findViewById(checkedId);
                selectedCoachAnswers.set(0, rb.getText().toString());
                Log.d(TAG, "Button Selected: " + rb.getText().toString());
            }
        });

        coachAnswers2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                RadioButton rb=(RadioButton)findViewById(checkedId);
                selectedCoachAnswers.set(1, rb.getText().toString());
                Log.d(TAG, "Button Selected: " + rb.getText().toString());
            }
        });

        coachAnswers3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                RadioButton rb=(RadioButton)findViewById(checkedId);
                selectedCoachAnswers.set(2, rb.getText().toString());
                Log.d(TAG, "Button Selected: " + rb.getText().toString());
            }
        });

    }


    /*
    checks if all questions have been answered.
    @returns - true if all questions have been answered, false if not all questions have been answered
     */

    private boolean areAllQuestionsAnswered(){
        for (int i = 0; i < selectedCoachAnswers.size(); i++) {
            if (selectedCoachAnswers.get(i).equals("")) {
                return false;
            }
        }
        return true;
    }

    /*
     //TODO update this to have both player and coach answers accounted for
     */
    private void calculateSymptomScore(){
        if (!isPlayer){
            for (int i = 0; i < NUM_COACH_QUESTIONS; i++){
                Log.d(TAG, "Selected: " + selectedCoachAnswers.get(i) + " - Actual: " + coachAnswers.get(i));
                Log.d(TAG, "Symptom Score: " + score);
                if (selectedCoachAnswers.get(i).equals(coachAnswers.get(i))){
                    score++;
                }
            }
            if (score > 0){
                CSRConstants.isLikelyConcussed = true;
                CSRConstants.concussionDetectionString = context.getString(R.string.concussion_detected_after_symptoms);
                CSRConstants.fromPage = CSRConstants.symptomsString;
                Intent resultsActivity = new Intent(SymptomActivity.this, Concussionresults.class);
                startActivity(resultsActivity);
            }
        } else {
            //TODO select threshold for concussion symptoms.
            for (int i = 0; i < playerAnswers.size(); i++){
                playerScore = playerScore + playerAnswers.get(i);
            }
            int i = playerScore/playerAnswers.size(); //average it out.
            playerScore = i;
            if (playerScore > PLAYER_SCORE_THRESHOLD){
                CSRConstants.isLikelyConcussed = true;
                CSRConstants.concussionDetectionString = context.getString(R.string.concussion_detected_after_symptoms);

            } else {
                CSRConstants.isLikelyConcussed = false;
                CSRConstants.concussionDetectionString = context.getString(R.string.no_concussion_detected_after_symptoms);
            }
            CSRConstants.fromPage = CSRConstants.symptomsString;
            Intent resultsActivity = new Intent(SymptomActivity.this, Concussionresults.class);
            startActivity(resultsActivity);


        }


        /*
        Toast t = Toast.makeText(context, "Score: " + score + "/3 indicate concussion", Toast.LENGTH_LONG);
        t.show();
        */

    }



    /*
    Reads in text file of symptom questions as well as their asnwers that would suggest a concussion
    and puts them into arraylist
    */
    private void createCoachesQuestionsArray(){
        ArrayList<String> array = new ArrayList<String>();
        ArrayList<String> array2 = new ArrayList<String>();
        try {
            //create asset manager in order to access files in the asset folder
            AssetManager am = context.getAssets();
            //use and input stream to access this file if it exists
            InputStream is = am.open("coach_symptom_questions.txt");
            Scanner scan = new Scanner(new InputStreamReader(is));
            while (scan.hasNext()) {
                String question = scan.nextLine();
                array.add(question);
                String answer = scan.nextLine();
                array2.add(answer);
            }
            scan.close();
            is.close();
        } catch (IOException ex) {
        }
        coachQuestions = array;
        coachAnswers = array2;
    }



    /*
       Reads in text file of symptoms and puts them into an arraylist
     */
    private void createPlayerQuestionsArray(){
        ArrayList<String> array = new ArrayList<String>();
        try {
            //create asset manager in order to access files in the asset folder
            AssetManager am = context.getAssets();
            //use and input stream to access this file if it exists
            InputStream is = am.open("player_symptom_questions.txt");
            Scanner scan = new Scanner(new InputStreamReader(is));
            while (scan.hasNext()) {
                String question = scan.nextLine();
                array.add(question);
                playerQuestions.add("");
                playerAnswers.add(0);
            }
            scan.close();
            is.close();
        } catch (IOException ex) {
        }
        playerQuestions = array;
    }


    /*
    creates an array of max NUM_PLAYER_QUESTIONS_ON_PAGE to display on the page.
     */
    private void selectPlayerQuestions(){

        Log.d(TAG, "Remainder: " + ((playerQuestions.size()-current_question)%NUM_PLAYER_QUESTIONS_ON_PAGE != 0));
        if ((playerQuestions.size()-current_question)<NUM_PLAYER_QUESTIONS_ON_PAGE) {
            NUM_PLAYER_QUESTIONS_ON_PAGE = (playerQuestions.size()-current_question);
            Log.d(TAG, "NUMPLAYERQS: " +NUM_PLAYER_QUESTIONS_ON_PAGE);

        }
        for (int i = 0; i < NUM_PLAYER_QUESTIONS_ON_PAGE; i++){
            selectedPlayerQuestions.add(i, playerQuestions.get(current_question));
            current_question++;
            Log.d(TAG, "CURRENT QUESITON: " +current_question);
        }

    }



    /*
     Creates view of what players see
    */
    private void fillInPlayerView(){
        selectPlayerQuestions();
        LinearLayout ll = (LinearLayout) findViewById(R.id.symptoms_player_linear_layout);
        ll.removeAllViews();
        for (int i = 0; i < NUM_PLAYER_QUESTIONS_ON_PAGE; i++){
            final int j = i + (current_question - NUM_PLAYER_QUESTIONS_ON_PAGE);
            final TextView tv = new TextView(this);
            tv.setText(selectedPlayerQuestions.get(i) + ":  0");
            tv.setTextSize(20);
            ll.addView(tv);

            final String s = selectedPlayerQuestions.get(i);
            final SeekBar sb = new SeekBar(this);
            sb.setMax(10);
            sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                public void onStopTrackingTouch(SeekBar arg0) {
                    playerAnswers.remove(j);
                    playerAnswers.add(j, (sb.getProgress()));
                    Log.d(TAG, "SEEK BAR: " + (sb.getProgress()));
                    Log.d(TAG, "Size of array: " + playerAnswers.size());
                    printArray(playerAnswers);
                    tv.setText(s+ ":  " + (sb.getProgress()));
                }

                public void onStartTrackingTouch(SeekBar arg0) {}

                public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {}
            });
            ll.addView(sb);
            //}


        }

        final Button b = (Button)findViewById(R.id.player_symptom_continue_button);
        if (current_question < playerQuestions.size()){
            b.setText("Next");

        } else {
            b.setText("Submit");
        }
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current_question < playerQuestions.size()) {
                    fillInPlayerView();
                } else {
                    calculateSymptomScore();
                }

            }
        });

       // ll.addView(b);

    }
    /*
    helper method
     */

    private void printArray(ArrayList<Integer> al){
        String s = "";
        for (int i = 0; i < al.size(); i++){
            s = s + al.get(i) + ", ";
        }
        Log.d(TAG, "Player Answers: " + s);
    }


    /*
    private void setAlarmService (int seconds){

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(SymptomActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.d("ALARM: ", "SET");

        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.SECOND, seconds);
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

        //Concussionresults.this.finish();

    } */

}
