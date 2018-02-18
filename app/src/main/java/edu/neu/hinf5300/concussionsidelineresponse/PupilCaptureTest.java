package edu.neu.hinf5300.concussionsidelineresponse;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PupilCaptureTest extends AppCompatActivity implements SurfaceHolder.Callback {

    SurfaceView mSurfaceView;
    SurfaceHolder mSurfaceHolder;
    Camera mCamera;
    boolean mPreviewRunning;
    Button buttonCapture;
    TextView eyeText;
    TextView instructionText;

    int flashIndex = 1;
    Camera.Parameters p;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pupil_capture_test);

        buttonCapture = (Button) findViewById(R.id.captureButton);
        mSurfaceView = (SurfaceView) findViewById(R.id.surface_camera);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        eyeText = (TextView)findViewById(R.id.image_title);
        instructionText = (TextView)findViewById(R.id.flashText);

        Log.d("FLASH: " + String.valueOf(flashIndex), "FLASH OFF FOR LEFT EYE");

        buttonCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (flashIndex == 1 ){
                    eyeText.setText("STEP 2: CAPTURE RIGHT EYE");
                    cameraFlashOff();
                    mCamera.takePicture(null, null, mPictureCallBack);
                    buttonCapture.setText("CAPTURE WITH FLASH ON");
                    instructionText.setText("Please let the player know that camera flash will be ON. Make sure the right eye is clearly seen in the circle below");
                } else if (flashIndex == 2){
                    eyeText.setText("STEP 3: CAPTURE LEFT EYE");



                    //MediaPlayer mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.flashrecording);
                   // mPlayer.start();

                    cameraFlashOn();

                    mCamera.takePicture(null, null, mPictureCallBack);
                    buttonCapture.setText("CAPTURE WITH FLASH OFF");


                    new AlertDialog.Builder(PupilCaptureTest.this).setTitle("Good Job!").setMessage("Move your camera to the left eye. Make sure you can see the eye in the circle below").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){
                            Toast.makeText(getApplicationContext(), "Capturing left eye", Toast.LENGTH_SHORT).show();
                        }
                    }).show();
                    instructionText.setText("Flash is turned off now. Make sure you can see player's eyes clearly in the circle below");

                } else if (flashIndex == 3){
                    eyeText.setText("STEP 4: CAPTURE LEFT EYE");

                    cameraFlashOff();
                    mCamera.takePicture(null, null, mPictureCallBack);
                    buttonCapture.setText("CAPTURE WITH FLASH ON");
                    instructionText.setText("Please let the player know that camera flash will be ON. Make sure you can see the eye in the circle below");
                } else if (flashIndex == 4){
                    eyeText.setText("STEP 4: CAPTURE LEFT EYE");

                   // MediaPlayer mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.flashrecording);
                    //mPlayer.start();
                    cameraFlashOn();
                    //buttonCapture.setText("CAPTURE WITH FLASH ON");
                    mCamera.takePicture(null, null, mPictureCallBack);

                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            Intent intent = new Intent(PupilCaptureTest.this, measurementInstructions.class);
                            startActivity(intent);
                        }
                    }.start();
                }

                flashIndex++;
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    Camera.PictureCallback mPictureCallBack = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] imageData, Camera camera) {
            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.camera);
            mediaPlayer.start();


            if (imageData!= null){
                int screenWidth = getResources().getDisplayMetrics().widthPixels;
                int screenHeight = getResources().getDisplayMetrics().heightPixels;

                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, (imageData != null) ? imageData.length : 0);

                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap, screenHeight, screenWidth, true);
                    int w = scaled.getWidth();
                    int h = scaled.getHeight();
                    Matrix mtx = new Matrix();
                    mtx.postRotate(90);
                    bitmap = Bitmap.createBitmap(scaled, 0, 0, w, h, mtx, true);
                }
                else{

                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap, screenWidth, screenHeight, true);
                    bitmap = scaled;
                }

                String filePath = saveToExternalStorage(bitmap);
                Toast.makeText(getApplicationContext(), "Image saved locally at: " + filePath, Toast.LENGTH_SHORT).show();
                mCamera.startPreview();
            }
        }
    };

    private String saveToExternalStorage(Bitmap bitmap) {

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!path.exists()) {
            path.mkdirs();
        }

        File file = new File(path, "eyeImage.jpeg");
        int i = 0;
        while (file.exists()) {
            file = new File(path, "eyeImage" + Integer.toString(i+1) + ".jpeg");
            i++;
        }
        FileOutputStream fos;

        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e ){
            throw new RuntimeException(e);
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        return path.getAbsolutePath();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mCamera = Camera.open();
        setCameraisplayOrientation(this, Camera.CameraInfo.CAMERA_FACING_BACK, mCamera);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mPreviewRunning) {
            mCamera.stopPreview();
        }

       p = mCamera.getParameters();

        List<Camera.Size> sizes = p.getSupportedPreviewSizes();

        Camera.Size cs = sizes.get(0);

        p.setPreviewSize(cs.width, cs.height);
        mCamera.setDisplayOrientation(90);

        mCamera.setParameters(p);

        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mCamera.startPreview();
        mPreviewRunning = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        mCamera.stopPreview();
        mPreviewRunning = false;
        mCamera.release();

    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int width, int height) {
        Camera.Size optimalSize = null;
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) height / width;

        for (Camera.Size size : sizes) {
            if (size.height != width) continue;
            double ratio = (double) size.width / size.height;
            if (ratio <= targetRatio + ASPECT_TOLERANCE && ratio >= targetRatio - ASPECT_TOLERANCE) {
                optimalSize = size;
            }
        }
        return optimalSize;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "PupilCaptureTest Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://edu.neu.hinf5300.concussionsidelineresponse/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "PupilCaptureTest Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://edu.neu.hinf5300.concussionsidelineresponse/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public static void setCameraisplayOrientation(Activity activity, int cameraID, Camera camera){
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraID, cameraInfo);

        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;

        switch (rotation){
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;

        result = (cameraInfo.orientation - degrees + 360) % 360;
        camera.setDisplayOrientation(result);
    }

    private void cameraFlashOn(){
       p = mCamera.getParameters();
        p.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        p.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
        mCamera.setParameters(p);
        mCamera.startPreview();
    }

    private void cameraFlashOff(){
        p = mCamera.getParameters();
        p.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        mCamera.setParameters(p);
        mCamera.startPreview();
    }
}