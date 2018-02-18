package edu.neu.hinf5300.concussionsidelineresponse;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class PupilImagesGrid extends AppCompatActivity {

    private String TAG = "PUPIL DILATION: ";
    Context context;

    ScaleGestureDetector scaleGestureDetector;
    Float scale = 1f;
    int clickIndex = 1;

    Matrix matrix;

    int pupilWidth;

    int[] pupilSizes = new int[4];

    SharedPreferences preferences;
    SharedPreferences.Editor sharedPreferencesEditor;

    boolean isCropClicked = false;

    TouchImageView image1;
    DrawView drawView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pupil_images_grid);
        context = this;


        final Button reset_button = (Button)findViewById(R.id.reset_button);
        final Button process_button = (Button)findViewById(R.id.process_button);
        final Button clickEnabler = (Button)findViewById(R.id.click_enabler);

        drawView = (DrawView)findViewById(R.id.cropper);
        drawView.setVisibility(View.INVISIBLE);

        scaleGestureDetector = new ScaleGestureDetector(this, new scaleListener());

        image1 = (TouchImageView)findViewById(R.id.imageview_custom);

        matrix = new Matrix();

        preferences = getSharedPreferences("CSRPreferences", Context.MODE_PRIVATE);
        sharedPreferencesEditor = preferences.edit();
        sharedPreferencesEditor.commit();

        Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/pictures/eyeImage.jpeg");

        Bitmap contrastBitmap = changeBitmapContrastBrightness(bitmap, 10, 255);

        image1.setImageBitmap(contrastBitmap);


        reset_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PupilImagesGrid.this, PupilINstruction.class);
                startActivity(intent);
            }
        });

        clickEnabler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                image1.setEnabled(false);
                clickEnabler.setBackgroundColor(getResources().getColor(R.color.colorSelectedButton));

                Toast.makeText(getApplicationContext(), "Use the cropping tool to measure the size of pupil", Toast.LENGTH_LONG).show();


                new CountDownTimer(2000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        drawView.setVisibility(View.VISIBLE);
                        drawView.setEnabled(true);
                    }
                }.start();

                isCropClicked = true;
            }
        });


        process_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isCropClicked == false){
                    Toast.makeText(getApplicationContext(), "Please measure the pupil size before submitting ...", Toast.LENGTH_LONG).show();
                } else {


                    if (clickIndex == 1){
                        Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/pictures/eyeImage1.jpeg");
                        Bitmap contrastBitmap = changeBitmapContrastBrightness(bitmap, 10, 255);
                        image1.setImageBitmap(contrastBitmap);
                        pupilWidth = drawView.getXValue();

                        String name = CSRConstants.playerUserName;
                        Log.d(TAG, "Name: " + name);
                        String name1 = name + CSRConstants.pupilWidth1String;
                        //String name2 = name + CSRConstants.hasBaselineString; //TODO move this to the last place
                        //Log.d(TAG, "Name Saved Under: " +name2);
                        // CSRConstants.isBaselineFinished = true; //TODO move this to the last place
                        sharedPreferencesEditor.putInt(name1, pupilWidth);

                        sharedPreferencesEditor.commit();

                        Log.d("WIDTH in GRID: ", String.valueOf(pupilWidth));
                        clickIndex++;

                        Toast.makeText(getApplicationContext(), "Submitted for processing. Loading the next image", Toast.LENGTH_SHORT).show();

                    } else if (clickIndex == 2){
                        Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/pictures/eyeImage2.jpeg");
                        Bitmap contrastBitmap = changeBitmapContrastBrightness(bitmap, 10, 255);
                        image1.setImageBitmap(contrastBitmap);
                        pupilWidth = drawView.getXValue();

                        String name = CSRConstants.playerUserName;
                        Log.d(TAG, "Name: " + name);
                        String name1 = name + CSRConstants.pupilWidth2String;
                        //String name2 = name + CSRConstants.hasBaselineString; //TODO move this to the last place
                        //Log.d(TAG, "Name Saved Under: " +name2);
                        // CSRConstants.isBaselineFinished = true; //TODO move this to the last place
                        sharedPreferencesEditor.putInt(name1, pupilWidth);

                        sharedPreferencesEditor.commit();

                        Log.d("WIDTH in GRID: ", String.valueOf(pupilWidth));
                        clickIndex++;

                        Toast.makeText(getApplicationContext(), "Submitted for processing. Loading the next image", Toast.LENGTH_SHORT).show();

                    } else if (clickIndex == 3){
                        Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/pictures/eyeImage3.jpeg");
                        Bitmap contrastBitmap = changeBitmapContrastBrightness(bitmap, 10, 255);
                        image1.setImageBitmap(contrastBitmap);
                        pupilWidth = drawView.getXValue();
                        String name = CSRConstants.playerUserName;
                        Log.d(TAG, "Name: " + name);
                        String name1 = name + CSRConstants.pupilWidth3String;
                        //String name2 = name + CSRConstants.hasBaselineString; //TODO move this to the last place
                        //Log.d(TAG, "Name Saved Under: " +name2);
                        // CSRConstants.isBaselineFinished = true; //TODO move this to the last place
                        sharedPreferencesEditor.putInt(name1, pupilWidth);
                        //sharedPreferencesEditor.putBoolean(name2, true);
                        sharedPreferencesEditor.commit();
                        //  pupilSizes[2] = pupilWidth;

                        Log.d("WIDTH in GRID: ", String.valueOf(pupilWidth));
                        clickIndex++;

                        Toast.makeText(getApplicationContext(), "Submitted for processing. Loading the next image", Toast.LENGTH_SHORT).show();

                    } else {
                        pupilWidth = drawView.getXValue();

                        String name = CSRConstants.playerUserName;
                        Log.d(TAG, "Name: " + name);
                        String name1 = name + CSRConstants.pupilWidth4String;
                        //String name2 = name + CSRConstants.hasBaselineString; //TODO move this to the last place
                        //Log.d(TAG, "Name Saved Under: " +name2);
                        // CSRConstants.isBaselineFinished = true; //TODO move this to the last place
                        sharedPreferencesEditor.putInt(name1, pupilWidth);
                        //sharedPreferencesEditor.putBoolean(name2, true);
                        sharedPreferencesEditor.commit();
                        // pupilSizes[2] = pupilWidth;
                        pupilSizes[0] = preferences.getInt(CSRConstants.playerUserName + CSRConstants.pupilWidth1String, 0);
                        pupilSizes[1] = preferences.getInt(CSRConstants.playerUserName + CSRConstants.pupilWidth2String, 0);
                        pupilSizes[2] = preferences.getInt(CSRConstants.playerUserName + CSRConstants.pupilWidth3String, 0);
                        pupilSizes[3] = preferences.getInt(CSRConstants.playerUserName + CSRConstants.pupilWidth4String, 0);

                        detectConcussion();

                        Toast.makeText(getApplicationContext(), "Submitted for processing. Loading results ...", Toast.LENGTH_SHORT).show();

                        new CountDownTimer(2000, 1000) {

                            public void onTick(long millisUntilFinished) {

                            }

                            public void onFinish() {

                               // Log.d(TAG, "In Timer");
                               // Intent intent = new Intent(PupilImagesGrid.this, Concussionresults.class);
                               // startActivity(intent);
                            }
                        }.start();

                        Log.d("WIDTH in GRID: ", String.valueOf(pupilWidth));

                    }
                }
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);

        return true;
    }

    private class scaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            //float scaleFactor = detector.getScaleFactor();
            scale = scale * detector.getScaleFactor();
            scale = Math.max(1f, Math.min(scale, 5f));
            matrix.setScale(scale, scale);
            image1.setImageMatrix(matrix);
            return true;
        }
    }

    public int[] getPupilWidth(){
        return pupilSizes;
    }

    public boolean isDilatedWithNoFlash(){

        if (pupilSizes[0] != pupilSizes[2]){
            return true;
        } else {
            return false;
        }
    }


    public boolean isDilatedWithFlash(){

        if (pupilSizes[1] != pupilSizes[3]){
            return true;
        } else {
            return false;
        }
    }

    public void detectConcussion(){
        if (isDilatedWithNoFlash() || isDilatedWithFlash()){
            CSRConstants.isLikelyConcussed = true;
            CSRConstants.concussionDetectionString = context.getString(R.string.concussion_detected_after_pupil);
        } else {
            CSRConstants.isLikelyConcussed = false;
            CSRConstants.concussionDetectionString = context.getString(R.string.no_concussion_detected_after_pupil);
        }
        CSRConstants.fromPage = CSRConstants.pupilWidth1String;
        Log.d(TAG, "IN DETECT CONCUSSION");
        Intent resultsActivity = new Intent(PupilImagesGrid.this, Concussionresults.class);
        startActivity(resultsActivity);
    }

    public static Bitmap changeBitmapContrastBrightness(Bitmap bmp, float contrast, float brightness)
    {
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        0, 0, 0, 0, 0,
                        0, 0, contrast, 0, 0,
                        0, 0, contrast, 0, 0,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }
}