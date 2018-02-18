package edu.neu.hinf5300.concussionsidelineresponse;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class VisualMemoryActivity extends AppCompatActivity {

    public Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor sharedPreferencesEditor;

    public CountDownTimer timer;
    private String TAG = "VISUAL MEMORY";
    private int score;
    private ArrayList<Drawable> imageArray;
    private Drawable answerImage;
    private ArrayList<Integer> answers = new ArrayList<Integer>();
           //contains the index of the correct answerimage based on the index of the question image chosen
    private int answerIndex;
          //contains the index of the correct answer
    private boolean isAnswerSelected;
    private ArrayList<Boolean> whichOptionIsSelected= new ArrayList<Boolean>();

    ImageButton imageButton1;
    ImageButton imageButton2;
    ImageButton imageButton3;
    ImageButton imageButton4;


    private int NUM_IMAGES_DISPLAYED = 4;
    private int NUM_ANSWERS_DISPLAYED = 4;
    private int TIME_OF_TIME_FOR_QUESTION = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_memory);

        context = this;
        preferences = getSharedPreferences("CSRPreferences", Context.MODE_PRIVATE);
        sharedPreferencesEditor = preferences.edit();
        sharedPreferencesEditor.commit();

        //
        answers.add(1);
        answers.add(0);
        answers.add(3);
        answers.add(2);

        answerIndex = 5; //
        imageArray = createImageArray();
        answerImage = createAnswerImage(imageArray);
        for(int i = 0; i < NUM_ANSWERS_DISPLAYED; i++){
            whichOptionIsSelected.add(false);
        }

        isAnswerSelected = false;


        final RelativeLayout start_rl = (RelativeLayout)findViewById(R.id.viz_mem_start_layout);
        final LinearLayout question_ll = (LinearLayout) findViewById(R.id.viz_mem_question_layout);
        final LinearLayout answer_ll = (LinearLayout) findViewById(R.id.viz_mem_answers_layout);
        final TextView instructions_tv = (TextView) findViewById(R.id.viz_mem_instructions_textview);


        imageButton1 = (ImageButton) findViewById(R.id.viz_mem_answers_radiobutton_imageview1);
        imageButton2 = (ImageButton) findViewById(R.id.viz_mem_answers_radiobutton_imageview2);
        imageButton3 = (ImageButton) findViewById(R.id.viz_mem_answers_radiobutton_imageview3);
        imageButton4 = (ImageButton) findViewById(R.id.viz_mem_answers_radiobutton_imageview4);


        Button start_button = (Button)findViewById(R.id.viz_mem_start_button);
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fillInQuestionView(imageArray);
                start_rl.setVisibility(View.GONE);
                question_ll.setVisibility(View.VISIBLE);
                startTimer(answer_ll, question_ll, instructions_tv);
            }
        });

        Button submit_button = (Button) findViewById(R.id.viz_mem_submit_button);
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAnswerSelected){
                    calculateScore();
                    Intent swmActivity = new Intent(VisualMemoryActivity.this, SecondWorkingMemoryActivity.class);
                    startActivity(swmActivity);
                } else {
                    Toast t = Toast.makeText(context, "Please Select an Answer", Toast.LENGTH_LONG);
                    t.show();
                }

            }
        });



    }

    private void startTimer(LinearLayout a, LinearLayout q, TextView t){
        final LinearLayout aa = a;
        final LinearLayout qq = q;
        final TextView tt = t;
        timer = new CountDownTimer(TIME_OF_TIME_FOR_QUESTION, 1000) {
            int i = 0;
            public void onTick(long millisUntilFinished) {
                Log.d(TAG,"Time Left: " + i);
                i = i + 1;
            }

            public void onFinish() {
                fillInAnswerView(answerImage);
                tt.setText(R.string.viz_mem_instructions_2);
                qq.setVisibility(View.GONE);
                aa.setVisibility(View.VISIBLE);
            }
        }.start();
    }





    /*
    Fill in the 4 images for the question view
     */
    private void fillInQuestionView(ArrayList<Drawable> d){
        ImageView iv1 = (ImageView) findViewById(R.id.viz_mem_image1);
        iv1.setImageDrawable(d.get(0));
        ImageView iv2 = (ImageView) findViewById(R.id.viz_mem_image2);
        iv2.setImageDrawable(d.get(1));
        ImageView iv3 = (ImageView) findViewById(R.id.viz_mem_image3);
        iv3.setImageDrawable(d.get(2));
        ImageView iv4 = (ImageView) findViewById(R.id.viz_mem_image4);
        iv4.setImageDrawable(d.get(3));

    }

    /*
    Fill in the buttons for the answers
     */
    private void fillInAnswerView(Drawable d){
        imageButton1.setImageDrawable(d);
        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unselectAllButtons();
                whichOptionIsSelected.add(0, true);
                imageButton1.setBackgroundColor(getResources().getColor(R.color.colorSelectedButton));
                isAnswerSelected = true;
            }
        });

        imageButton2.setImageDrawable(d);
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unselectAllButtons();
                whichOptionIsSelected.add(1, true);
                imageButton2.setBackgroundColor(getResources().getColor(R.color.colorSelectedButton));
                isAnswerSelected = true;
            }
        });

        imageButton3.setImageDrawable(d);
        imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unselectAllButtons();
                whichOptionIsSelected.add(2, true);
                imageButton3.setBackgroundColor(getResources().getColor(R.color.colorSelectedButton));
                isAnswerSelected = true;
            }
        });

        imageButton4.setImageDrawable(d);
        imageButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unselectAllButtons();
                whichOptionIsSelected.add(3, true);
                imageButton4.setBackgroundColor(getResources().getColor(R.color.colorSelectedButton));
                isAnswerSelected = true;
            }
        });
    }

    private void unselectAllButtons(){
        for (int i = 0; i < NUM_ANSWERS_DISPLAYED; i++){
            whichOptionIsSelected.add(i, false);
        }
        imageButton1.setBackgroundColor(getResources().getColor(R.color.colorUnselectedButton));
        imageButton2.setBackgroundColor(getResources().getColor(R.color.colorUnselectedButton));
        imageButton3.setBackgroundColor(getResources().getColor(R.color.colorUnselectedButton));
        imageButton4.setBackgroundColor(getResources().getColor(R.color.colorUnselectedButton));
    }

    /*
    Creates and shuffles an array of the images
     */
    private ArrayList<Drawable> createImageArray(){
        ArrayList<Drawable> array = new ArrayList<Drawable>();
        ArrayList<Drawable> array2 = new ArrayList<Drawable>();
        //add all of the drawable to an array
        array.add(getResources().getDrawable(R.drawable.viz_mem_arrow));
        array.add(getResources().getDrawable(R.drawable.viz_mem_cloud));
        array.add(getResources().getDrawable(R.drawable.viz_mem_flower));
        array.add(getResources().getDrawable(R.drawable.viz_mem_key));
        array.add(getResources().getDrawable(R.drawable.viz_mem_lightning));
        array.add(getResources().getDrawable(R.drawable.viz_mem_paperclip));
        array.add(getResources().getDrawable(R.drawable.viz_mem_plane));
        array.add(getResources().getDrawable(R.drawable.viz_mem_twoarrow));
        Collections.shuffle(array);
      //  array.get(1).rotate(.9);
        for (int i = 0; i < NUM_IMAGES_DISPLAYED; i++){
            array2.add(array.get(i));
        }

        Log.d(TAG, "Array first Size " + array.size());
        Log.d(TAG, "Array Size: " + array2.size());
        return array2;
    }

    /*

     */
    //TODO rotate the images
    private Drawable createAnswerImage(ArrayList<Drawable> a){
        Random r = new Random();
        int index = r.nextInt(4);
        Drawable answerImage = a.get(index);
        Log.d(TAG, "Index: " + index);
        answerIndex = answers.get(index);
        Log.d(TAG, "Answer Index: " + answerIndex);

        return answerImage;
    }

    private void calculateScore(){
        if (whichOptionIsSelected.get(answerIndex)){
           /*
            Toast t = Toast.makeText(context, "correct", Toast.LENGTH_LONG);
            t.show();
            */
            score = 1;
        } else {
            /*
            Toast t2 = Toast.makeText(context, "incorrect", Toast.LENGTH_LONG);
            t2.show();
            */
            score = 0;
        }
        storeScore();
    }

    /*
    stores the score in shared preferences. Stores in a different placed based on whether this is a
    baseline or concussion test
     */

    private void storeScore(){
        String name = "";
        if (CSRConstants.isBaseline){
            name = CSRConstants.playerUserName + CSRConstants.baselineString + CSRConstants.visualMemoryString;
        } else {
            name = CSRConstants.playerUserName + CSRConstants.concussionString + CSRConstants.visualMemoryString;
        }
        sharedPreferencesEditor.putInt(name, score);
        sharedPreferencesEditor.commit();
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


}
