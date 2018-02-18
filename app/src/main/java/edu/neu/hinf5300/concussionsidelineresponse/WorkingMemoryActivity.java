package edu.neu.hinf5300.concussionsidelineresponse;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class WorkingMemoryActivity extends AppCompatActivity {

    public Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor sharedPreferencesEditor;

    public CountDownTimer timer;
    private String TAG = "WORKING MEMORY";
    private int TIME_BETWEEN_WORDS = 3000;
    private int numberOfWordsAppeared;
    private int NUM_WORDS_IN_TEST = 6; //determines how many words are shown in a given test/round
    private int NUM_ANSWERS_DISPLAYED = 8;
    private int score; //total number of correctly checked or unchecked boxes, maximum possible score is the NUM_ANSWERS_DISPLAYED

    private List<String> correctWordsList = new ArrayList<String>();
    private List<String> possibleWordAnswerList = new ArrayList<String>();
    private ArrayList<Integer> userAnswers = new ArrayList<Integer>(); //0 means unchecked, 1 means checked
    private ArrayList<Integer> correctAnswers = new ArrayList<Integer>(); //0 means unchecked, 1 means checked

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_working_memory);

        preferences = getSharedPreferences("CSRPreferences", Context.MODE_PRIVATE);
        sharedPreferencesEditor = preferences.edit();
        sharedPreferencesEditor.commit();

        context = this;
        numberOfWordsAppeared = 0;

        for (int i = 0; i < NUM_ANSWERS_DISPLAYED; i++){
            userAnswers.add(0);
            correctAnswers.add(0);
        }
        createWordsArrayFromFile(); //create the correctWords and possibleWordANswers
        setCheckBoxText();
        createCorrectAnswersArray();


        final TextView instructionsTextView = (TextView) findViewById(R.id.working_memory_instructions_textview);
        final TextView wordTextView = (TextView) findViewById(R.id.words_textview);
        final RelativeLayout checkBoxesLayout = (RelativeLayout) findViewById(R.id.check_box_layout);
        Button submitButton = (Button) findViewById(R.id.memory_submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateScore();
                if (CSRConstants.isBaseline == true){
                    CSRConstants.memory_score_baseline = score;
                } else {
                    CSRConstants.memory_score_concussion = score;
                }

                Intent vizmemActivity = new Intent(WorkingMemoryActivity.this, VisualMemoryActivity.class);
                startActivity(vizmemActivity);
            }
        });

        final Button startButton = (Button) findViewById(R.id.working_memory_start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instructionsTextView.setText("Remember these words:");
                startButton.setVisibility(View.GONE);
                startButton.setClickable(false);
                wordTextView.setVisibility(View.VISIBLE);
                wordTextView.setText(correctWordsList.get(numberOfWordsAppeared));
               // instructionsTextView.setVisibility(View.GONE);
                startTimer(wordTextView, instructionsTextView, checkBoxesLayout);
            }
        });


    }



    /*
    Changes the word on the screen every x seconds. After rotating through x number of words,
    it makes the word text view invisible.
    @params - wordTextView - the testview that contains the words.
     */

    public void startTimer(View view, View view2, RelativeLayout layout) {
        final TextView wordTextView = (TextView) view;
        final TextView instructionTextView = (TextView) view2;
        final RelativeLayout checkBoxesLayout = (RelativeLayout) layout;
        timer = new CountDownTimer(TIME_BETWEEN_WORDS, 1000) {
            public void onTick(long millisUntilFinished) {
                //timerTextView.setText("Time: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
               // Log.d(TAG, "Size: " + NUM_WORDS_IN_TEST);
                if (numberOfWordsAppeared < (NUM_WORDS_IN_TEST -1)){
                    numberOfWordsAppeared++;
                    //Log.d(TAG, "Number of Words: " + numberOfWordsAppeared);
                    Log.d(TAG, "Word Chosen: " + correctWordsList.get(numberOfWordsAppeared));
                    wordTextView.setText(correctWordsList.get(numberOfWordsAppeared));
                    startTimer(wordTextView, instructionTextView, checkBoxesLayout);
                } else{
                    Log.d(TAG, "timer's done");
                    instructionTextView.setVisibility(View.VISIBLE);
                    instructionTextView.setText(getResources().getString(R.string.working_memory_instructions_text_2));
                    wordTextView.setVisibility(View.GONE);
                    checkBoxesLayout.setVisibility(View.VISIBLE);

                }
            }
        }.start();
    }


    /*
    Reads in words from a words_memory.txt file in the assets folder.
    Shuffles this arraylist so that the first words are randomly assigned each time.
    Puts these words into different lists -
        correctWordsList - the x words that will be displayed
        possibleWordAnswerList - x*2 words that may be used in the multiple choice checkboxes
     */
    private void createWordsArrayFromFile(){
        ArrayList<String> array = new ArrayList<String>();
        //ArrayList<String> array2 = new ArrayList<String>();
        try {
            //create asset manager in order to access files in the asset folder
            AssetManager am = context.getAssets();
            //use and input stream to access this file if it exists
            InputStream is = am.open("words_memory.txt");
            Scanner scan = new Scanner(new InputStreamReader(is));
            while (scan.hasNext()) {
                String word = scan.nextLine();
                array.add(word);
            }
            scan.close();
            is.close();
        } catch (IOException ex) {
        }
        Collections.shuffle(array);

        for (int i = 0; i < NUM_WORDS_IN_TEST; i++){
            correctWordsList.add(array.get(i));
            Log.d(TAG, "Correct Word: " + array.get(i));
        }

        possibleWordAnswerList.add(array.get(0)); //ensures that at least one word is correct
        Collections.shuffle(array);
       // Log.d(TAG, "Possible Word 1: " + possibleWordAnswerList.get(0));
        for (int i = 1; i < 12; i++){
            possibleWordAnswerList.add(array.get(i));
        }
        Collections.shuffle(possibleWordAnswerList);

       // for(int i = 0; i <12; i++){
       //     Log.d(TAG, "Possible Word: " + possibleWordAnswerList.get(i));
       // }



    }






    private void calculateScore(){
        int temp = 0;
        for (int i = 0; i < NUM_ANSWERS_DISPLAYED; i++){
            if (correctAnswers.get(i) == userAnswers.get(i)){
                temp ++;
            }
        }
        score = temp;
        Log.d(TAG, "Score: " + score);
        /*
        Toast t = Toast.makeText(context, "Score: " + score + "/8 Correct", Toast.LENGTH_LONG);
        t.show();
        */
        storeScore();
    }


    /*
    Stores the score in shared preferences
    */
    private void storeScore(){
        String name = "";
        if (CSRConstants.isBaseline){
            name = CSRConstants.playerUserName + CSRConstants.baselineString + CSRConstants.workingMemoryString;
        } else {
            name = CSRConstants.playerUserName + CSRConstants.concussionString + CSRConstants.workingMemoryString;
        }
        sharedPreferencesEditor.putInt(name, score);
        sharedPreferencesEditor.commit();
    }



    private void createCorrectAnswersArray(){
        for (int i = 0; i < NUM_ANSWERS_DISPLAYED; i++){
            Log.d(TAG, "Word Check: " + possibleWordAnswerList.get(i));
            if(correctWordsList.contains(possibleWordAnswerList.get(i))){
                correctAnswers.add(i, 1);
                Log.d(TAG, "Word Check: correct");
            } else{
                correctAnswers.add(i, 0);
                Log.d(TAG, "Word Check: IN-correct");
            }
        }
    }





    /*
    @params - list - the list of possible answers
     */
    private void setCheckBoxText(){
        final CheckBox checkbox1 = (CheckBox) findViewById(R.id.check_box_1);
        checkbox1.setText(possibleWordAnswerList.get(0));
        checkbox1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkbox1.isChecked()){
                    userAnswers.add(0, 1);
                } else {
                    userAnswers.add(0, 0);
                }
                Log.d(TAG, "1Clicked: " + userAnswers.get(0));

            }
        });

        final CheckBox checkbox2 = (CheckBox) findViewById(R.id.check_box_2);
        checkbox2.setText(possibleWordAnswerList.get(1));
        checkbox2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkbox2.isChecked()){
                    userAnswers.add(1, 1);
                } else {
                    userAnswers.add(1, 0);
                }
                Log.d(TAG, "2Clicked: " + userAnswers.get(1));

            }
        });

        final CheckBox checkbox3 = (CheckBox) findViewById(R.id.check_box_3);
        checkbox3.setText(possibleWordAnswerList.get(2));
        checkbox3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkbox3.isChecked()){
                    userAnswers.add(2, 1);
                } else {
                    userAnswers.add(2, 0);
                }
                Log.d(TAG, "3Clicked: " + userAnswers.get(2));

            }
        });

        final CheckBox checkbox4 = (CheckBox) findViewById(R.id.check_box_4);
        checkbox4.setText(possibleWordAnswerList.get(3));
        checkbox4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkbox4.isChecked()){
                    userAnswers.add(3, 1);
                } else {
                    userAnswers.add(3, 0);
                }
                Log.d(TAG, "4Clicked: " + userAnswers.get(3));

            }
        });

        final CheckBox checkbox5 = (CheckBox) findViewById(R.id.check_box_5);
        checkbox5.setText(possibleWordAnswerList.get(4));
        checkbox5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkbox5.isChecked()){
                    userAnswers.add(4, 1);
                } else {
                    userAnswers.add(4, 0);
                }
                Log.d(TAG, "5Clicked: " + userAnswers.get(4));
            }
        });

        final CheckBox checkbox6 = (CheckBox) findViewById(R.id.check_box_6);
        checkbox6.setText(possibleWordAnswerList.get(5));
        checkbox6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkbox6.isChecked()) {
                    userAnswers.add(5, 1);
                } else {
                    userAnswers.add(5, 0);
                }
                Log.d(TAG, "6Clicked: " + userAnswers.get(5));
            }
        });

        final CheckBox checkbox7 = (CheckBox) findViewById(R.id.check_box_7);
        checkbox7.setText(possibleWordAnswerList.get(6));
        checkbox7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkbox7.isChecked()) {
                    userAnswers.add(6, 1);
                } else {
                    userAnswers.add(6, 0);
                }
                Log.d(TAG, "6Clicked: " + userAnswers.get(6));
            }
        });

        final CheckBox checkbox8 = (CheckBox) findViewById(R.id.check_box_8);
        checkbox8.setText(possibleWordAnswerList.get(7));
        checkbox8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkbox8.isChecked()) {
                    userAnswers.add(7, 1);
                } else {
                    userAnswers.add(7, 0);
                }
                Log.d(TAG, "6Clicked: " + userAnswers.get(7));
            }
        });

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
        } catch (NullPointerException e) {
        }



    }

}
