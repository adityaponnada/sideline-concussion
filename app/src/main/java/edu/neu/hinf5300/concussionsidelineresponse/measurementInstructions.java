package edu.neu.hinf5300.concussionsidelineresponse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class measurementInstructions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement_instructions);

        Button goToMeasurement = (Button)findViewById(R.id.continue_measurement_button);

        goToMeasurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(measurementInstructions.this, PupilImagesGrid.class);
                startActivity(intent);
            }
        });
    }
}
