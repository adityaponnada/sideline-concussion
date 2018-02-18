package edu.neu.hinf5300.concussionsidelineresponse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class PupilINstruction extends AppCompatActivity {

    Button buttonContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pupil_instruction);

        buttonContinue = (Button) findViewById(R.id.continue_button);


        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PupilINstruction.this, PupilCaptureTest.class);
                startActivity(intent);
            }
        });
    }




}
