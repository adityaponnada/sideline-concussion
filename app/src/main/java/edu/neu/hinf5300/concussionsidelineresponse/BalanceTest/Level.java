package edu.neu.hinf5300.concussionsidelineresponse.BalanceTest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import edu.neu.hinf5300.concussionsidelineresponse.BalanceTest.orientation.Orientation;
import edu.neu.hinf5300.concussionsidelineresponse.BalanceTest.orientation.OrientationListener;
import edu.neu.hinf5300.concussionsidelineresponse.BalanceTest.orientation.OrientationProvider;
import edu.neu.hinf5300.concussionsidelineresponse.BalanceTest.view.LevelView;
import edu.neu.hinf5300.concussionsidelineresponse.CSRConstants;
import edu.neu.hinf5300.concussionsidelineresponse.R;
import edu.neu.hinf5300.concussionsidelineresponse.WorkingMemoryActivity;

/*
 *  This file is part of Level (an Android Bubble Level).
 *  <https://github.com/avianey/Level>
 *
 *  Copyright (C) 2014 Antoine Vianey
 *
 *  Level is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Level is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Level. If not, see <http://www.gnu.org/licenses/>
 */
public class Level extends Activity implements OrientationListener {

    private static Level CONTEXT;

    public Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor sharedPreferencesEditor;

    private static final int DIALOG_CALIBRATE_ID = 1;
    private static final int TOAST_DURATION = 10000;

    private OrientationProvider provider;

    private LevelView view;
     Button start_balance_Button;

    /** Gestion du son */
    private SoundPool soundPool;
    private boolean soundEnabled;
    private int bipSoundID;
    private int bipRate;
    private long lastBip;
    public CountDownTimer timer;
    private int TIME_TO_BALANCE = 10000;
    private int score;
    private String TAG = "LEVEL: ";
    private boolean isTandem;

    private FrameLayout balance_fl;
    private LinearLayout tandem_instructions_rl;
    private LinearLayout semitandem_instructions_rl;

    TextView timesUpTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balance);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        CONTEXT = this;

        context = this;
        preferences = getSharedPreferences("CSRPreferences", Context.MODE_PRIVATE);
        sharedPreferencesEditor = preferences.edit();
        sharedPreferencesEditor.commit();

        view = (LevelView) findViewById(R.id.level);
        // sound
        soundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
        bipSoundID = soundPool.load(this, R.raw.bip, 1);
        bipRate = getResources().getInteger(R.integer.bip_rate);
        score = 0;
        isTandem = false;

        balance_fl = (FrameLayout)findViewById(R.id.balance_test_fl);
        tandem_instructions_rl = (LinearLayout)findViewById(R.id.tandem_instructions_rl);
        semitandem_instructions_rl = (LinearLayout)findViewById(R.id.semi_tandem_instructions_rl);

        start_balance_Button = (Button)findViewById(R.id.start_balance_button);

        Button start_tandem_Button = (Button) findViewById(R.id.tandem_start_button);
        start_tandem_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_balance_Button.setEnabled(true);
                balance_fl.setVisibility(View.VISIBLE);
                tandem_instructions_rl.setVisibility(View.GONE);
                semitandem_instructions_rl.setVisibility(View.GONE);
            }
        });


        Button start_semitandem_Button = (Button) findViewById(R.id.semitandem_start_button);
        start_semitandem_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_balance_Button.setEnabled(true);
                balance_fl.setVisibility(View.VISIBLE);
                tandem_instructions_rl.setVisibility(View.GONE);
                semitandem_instructions_rl.setVisibility(View.GONE);
            }
        });



        start_balance_Button.setVisibility(View.VISIBLE);
        start_balance_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
                start_balance_Button.setEnabled(false);

            }
        });
    }
	/*

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return true;
	}
	*/



    private void startTimer(){
        final OrientationProvider op = edu.neu.hinf5300.concussionsidelineresponse.BalanceTest.orientation.OrientationProvider.getInstance();
        //  final TextView tv = (TextView)findViewById(R.id.balance_test_tv);
        //  tv.setText("");
        timer = new CountDownTimer(TIME_TO_BALANCE, 1000) {
            int i = 0;

            public void onTick(long millisUntilFinished) {

                int seconds = (int) (millisUntilFinished/1000);
                start_balance_Button.setText(String.valueOf(seconds) + " Seconds Left");

                //double countDownValue =
                if (isLevel(op.getPitch(), op.getRoll(), op.getBalance(),op.getBalance())){
                    score++;
                    //   tv.setText(tv.getText() + "Le "); //+ op.getPitch() + "." + op.getRoll() + "." + op.getBalance() + "." + op.getBalance() + ": ");
                    Log.d(TAG, "LEVEL");
                } else {
                    Log.d(TAG, "NOT LEVEL");
                    //    tv.setText(tv.getText() + "NL "); //+ op.getPitch() + "." + op.getRoll() + "." + op.getBalance() + "." + op.getBalance() + ": ");
                }
            }

            public void onFinish() {
                storeScore();
                // Toast.makeText(Level.this, "SCORE:" + score, Toast.LENGTH_SHORT).show();
                timesUpTV = (TextView)findViewById(R.id.times_up_tv);
                timesUpTV.setVisibility(View.VISIBLE);
                //End of activity, move to next activity.
                moveToNextActivity();

            }
        }.start();
    }

    private void moveToNextActivity(){

        new CountDownTimer(2000, 1000){
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                timesUpTV.setVisibility(View.GONE);
                start_balance_Button.setText("START");
                if (!isTandem) {
                    switchToTandem();
                } else {
                    Intent workingMemory = new Intent(Level.this, WorkingMemoryActivity.class);
                    startActivity( workingMemory);
                }

            }
        }.start();
    }


    //TODO update isLevel Method so that it can detect something level.
    private boolean isLevel(float pitch, float roll, float balance, float sensibility){
        return roll <= sensibility  && roll >= - sensibility  && (Math.abs(pitch) <= sensibility
                || Math.abs(pitch) >= 180 - sensibility);
    }

    private void switchToTandem(){
        isTandem = true;
        balance_fl.setVisibility(View.GONE);
        tandem_instructions_rl.setVisibility(View.VISIBLE);
        semitandem_instructions_rl.setVisibility(View.GONE);
    }

    private void storeScore(){
        String s = "";
        if (isTandem){
            if (CSRConstants.isBaseline == true){
                s = CSRConstants.playerUserName + CSRConstants.baselineString + CSRConstants.tandemBalanceString;
            } else {
                s = CSRConstants.playerUserName + CSRConstants.concussionString + CSRConstants.tandemBalanceString;
            }
        } else {
            if (CSRConstants.isBaseline == true){
                s = CSRConstants.playerUserName + CSRConstants.baselineString + CSRConstants.semiTandemBalanceString;
            } else {
                s = CSRConstants.playerUserName + CSRConstants.concussionString + CSRConstants.semiTandemBalanceString;
            }
        }

        sharedPreferencesEditor.putInt(s, score).commit();

    }



    /* Handles item selections */
	/*
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	        case R.id.calibrate:
	            showDialog(DIALOG_CALIBRATE_ID);
	            return true;
	        case R.id.preferences:
	            startActivity(new Intent(this, net.androgames.level.LevelPreferences.class));
	            return true;
        }
        return false;
    }
    */

    protected Dialog onCreateDialog(int id) {
        Dialog dialog;
        switch(id) {
            case DIALOG_CALIBRATE_ID:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.calibrate_title)
                        .setIcon(null)
                        .setCancelable(true)
                        .setPositiveButton(R.string.calibrate, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                provider.saveCalibration();
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .setNeutralButton(R.string.reset, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                provider.resetCalibration();
                            }
                        })
                        .setMessage(R.string.calibrate_message);
                dialog = builder.create();
                break;
            default:
                dialog = null;
        }
        return dialog;
    }

    protected void onResume() {
        super.onResume();
        Log.d("Level", "Level resumed");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        provider = OrientationProvider.getInstance();
        // chargement des effets sonores
        soundEnabled = prefs.getBoolean(edu.neu.hinf5300.concussionsidelineresponse.BalanceTest.LevelPreferences.KEY_SOUND, false);
        // orientation manager
        if (provider.isSupported()) {
            provider.startListening(this);
        } else {
            Toast.makeText(this, getText(R.string.not_supported), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (provider.isListening()) {
            provider.stopListening();
        }
    }

    @Override
    public void onDestroy() {
        if (soundPool != null) {
            soundPool.release();
        }
        super.onDestroy();
    }

    @Override
    public void onOrientationChanged(Orientation orientation, float pitch, float roll, float balance) {
        if (soundEnabled
                && orientation.isLevel(pitch, roll, balance, provider.getSensibility())
                && System.currentTimeMillis() - lastBip > bipRate) {
            AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_RING);
            float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_RING);
            float volume = streamVolumeCurrent / streamVolumeMax;
            lastBip = System.currentTimeMillis();
            soundPool.play(bipSoundID, volume, volume, 1, 0, 1);
        }
        view.onOrientationChanged(orientation, pitch, roll, balance);
    }

    @Override
    public void onCalibrationReset(boolean success) {
        Toast.makeText(this, success ?
                        R.string.calibrate_restored : R.string.calibrate_failed,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCalibrationSaved(boolean success) {
        Toast.makeText(this, success ?
                        R.string.calibrate_saved : R.string.calibrate_failed,
                Toast.LENGTH_SHORT).show();
    }

    public static Level getContext() {
        return CONTEXT;
    }

    public static OrientationProvider getProvider() {
        return getContext().provider;
    }

}