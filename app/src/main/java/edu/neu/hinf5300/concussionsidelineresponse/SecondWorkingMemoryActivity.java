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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class SecondWorkingMemoryActivity extends AppCompatActivity {


    public Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor sharedPreferencesEditor;

    public CountDownTimer timer;
    private String TAG = "WORKING MEMORY";
    private int TIME_BETWEEN_WORDS = 3000;
    private int numberOfWordPairsAppeared;
    private int indexOfPairForAnswer;
    private int NUM_PAIRS_IN_TEST = 10; //determines how many words are shown in a given test/round
    private int NUM_ANSWERS_DISPLAYED = 4; //one will be a correct answer, 3 will be false answers.
    private int score;

    private ArrayList<String> words1 = new ArrayList<String>();
    private ArrayList<String> words2 = new ArrayList<String>();
    private ArrayList<String> answers = new ArrayList<String>();
    private String correctAnswer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_working_memory);

        preferences = getSharedPreferences("CSRPreferences", Context.MODE_PRIVATE);
        sharedPreferencesEditor = preferences.edit();
        sharedPreferencesEditor.commit();

        context = this;
        numberOfWordPairsAppeared = 0;
        correctAnswer = "";
        score = 0;

        Random r = new Random();
        indexOfPairForAnswer = r.nextInt(10);
        createWordsArrayFromFile();


        final TextView instructionsTextView = (TextView) findViewById(R.id.swm_working_memory_instructions_textview);
        final TextView wordTextView = (TextView) findViewById(R.id.swm_words_textview);
        final RelativeLayout checkBoxesLayout = (RelativeLayout) findViewById(R.id.swm_answers_layout);
        Button submitButton = (Button) findViewById(R.id.swm_memory_submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //calculateScore();
                if (CSRConstants.isBaseline == true){
                    CSRConstants.memory_score_baseline = score;
                    Intent mainActivity = new Intent(SecondWorkingMemoryActivity.this, MainActivity.class);
                    startActivity(mainActivity);
                } else {
                    CSRConstants.memory_score_concussion = score;
                    CSRConstants.fromPage = CSRConstants.secondWorkingMemoryString;
                    Intent resultsActivity = new Intent(SecondWorkingMemoryActivity.this, Concussionresults.class);
                    startActivity(resultsActivity);
                }
                calculateScore();

            }
        });

        final Button startButton = (Button) findViewById(R.id.swm_working_memory_start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instructionsTextView.setText(R.string.swm_working_memory_instructions_text_2);
                startButton.setVisibility(View.GONE);
                startButton.setClickable(false);
                wordTextView.setVisibility(View.VISIBLE);
                wordTextView.setText(words1.get(numberOfWordPairsAppeared) + "\n" + words2.get(numberOfWordPairsAppeared));
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
                if (numberOfWordPairsAppeared < (NUM_PAIRS_IN_TEST -1)){
                    numberOfWordPairsAppeared++;
                    //Log.d(TAG, "Number of Words: " + numberOfWordsAppeared);
                   // Log.d(TAG, "Word Chosen: " + correctWordsList.get(numberOfWordsAppeared));
                    wordTextView.setText(words1.get(numberOfWordPairsAppeared) + "\n" + words2.get(numberOfWordPairsAppeared));
                    startTimer(wordTextView, instructionTextView, checkBoxesLayout);
                } else{
                    Log.d(TAG, "timer's done");
                    instructionTextView.setVisibility(View.VISIBLE);
                    instructionTextView.setText(getResources().getString(R.string.swm_working_memory_instructions_text_3));
                    wordTextView.setVisibility(View.GONE);
                    //checkBoxesLayout.setVisibility(View.VISIBLE);
                    fillInAnswerView();

                }
            }
        }.start();
    }

    /*
    Reads in words from a words_memory.txt file in the assets folder.
    Shuffles this arraylist so that the first words are randomly assigned each time.
    Puts these words into different lists -
       words1, words2 (to be paired together)
       answers to populate the answers list including one correct answer.
       save string that is the correct answer to compare later
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

        int j = 0;
        for (int i = 0; i < NUM_PAIRS_IN_TEST*2; i++){
            words2.add(j, array.get(i));
            i++;
            words1.add(j, array.get(i));
            Log.d(TAG, "Word Pairs: " + words1.get(j) + " : " + words2.get(j));

            j++;
        }

        int k = 0;
        for (int i = NUM_PAIRS_IN_TEST*2; i < (NUM_PAIRS_IN_TEST*2 + NUM_ANSWERS_DISPLAYED -1); i++){
            answers.add(k, array.get(i));
            Log.d(TAG, "False Answers: " + answers.get(k));
            k++;
        }
        answers.add(k, words2.get(indexOfPairForAnswer));
        Log.d(TAG, "Correct Answers: " + answers.get(k));
        Collections.shuffle(answers);

        correctAnswer = words2.get(indexOfPairForAnswer);
    }

    /*

     */
    private void fillInAnswerView(){
        RelativeLayout answerLayout = (RelativeLayout)findViewById(R.id.swm_answers_layout);
        answerLayout.setVisibility(View.VISIBLE);
        TextView singleWordTextView = (TextView)findViewById(R.id.swm_single_word_textview);
        singleWordTextView.setText(words1.get(indexOfPairForAnswer));
        RadioGroup answerRadioGroup = (RadioGroup)findViewById(R.id.swm_answers_radiogroup);
        for (int i = 0; i < answers.size(); i++){
            final RadioButton rb = new RadioButton(this);
            rb.setText(answers.get(i));
            answerRadioGroup.addView(rb);
        }
        answerRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                RadioButton rb =(RadioButton)findViewById(checkedId);
                if(rb.getText().toString().equals(correctAnswer)){
                    Log.d(TAG, "CORRECT Button Selected: " + rb.getText().toString());
                    score = 1;
                }
                Log.d(TAG, "Button Selected: " + rb.getText().toString());
            }
        });
    }

    /*
    currently useless method
     */

    private void calculateScore(){
        storeScore();

    }

    private void storeScore(){
        String name = "";
        if (CSRConstants.isBaseline){
            name = CSRConstants.playerUserName + CSRConstants.baselineString + CSRConstants.secondWorkingMemoryString;
            String hasBaseline = CSRConstants.playerUserName + CSRConstants.hasBaselineString;
            sharedPreferencesEditor.putBoolean(hasBaseline, true); //the baseline is done.
            Calendar c = Calendar.getInstance();

            String sDate = c.get(Calendar.MONTH) + "/"
                    + c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.YEAR);
            sharedPreferencesEditor.putString(CSRConstants.playerUserName + CSRConstants.baselineDate, sDate);

        } else {
            name = CSRConstants.playerUserName + CSRConstants.concussionString + CSRConstants.secondWorkingMemoryString;

        }
        sharedPreferencesEditor.putInt(name, score);
        sharedPreferencesEditor.commit();

    }


}
