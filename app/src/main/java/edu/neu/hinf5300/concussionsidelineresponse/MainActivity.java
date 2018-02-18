package edu.neu.hinf5300.concussionsidelineresponse;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import edu.neu.hinf5300.concussionsidelineresponse.BalanceTest.Level;


public class MainActivity extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor sharedPreferencesEditor;
    public Context context;

    private String TAG = "MainMenu";

    private boolean isPlayerNameEntered;
    private RelativeLayout signInRL;
    private RelativeLayout mainPageRL;
    private RelativeLayout playerRL;
    private LinearLayout testingLL;
    private RelativeLayout testingButtonRL;
    private Button playerEnterContinueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        isPlayerNameEntered = false;
        CSRConstants.playerUserName = "";
       // CSRConstants.coachUserName = "LILY";

        Button baselineButton = (Button)findViewById(R.id.baseline_Button);
        Button concussionButton = (Button)findViewById(R.id.concussion_Button);
        Button rosterButton = (Button)findViewById(R.id.roster_Button);
        Button signInButton = (Button)findViewById(R.id.sign_in_button);
        //Button signOutButton = (Button)findViewById(R.id.sign_out_Button);
        final EditText signInEditText = (EditText)findViewById(R.id.sign_in_edit_text);

        signInRL = (RelativeLayout)findViewById(R.id.sign_in_relative_layout);
        mainPageRL = (RelativeLayout)findViewById(R.id.main_page_relative_layout);
        playerRL = (RelativeLayout)findViewById(R.id.choose_player_relative_layout);
        testingLL = (LinearLayout)findViewById(R.id.main_page_testing_ll);
        testingButtonRL = (RelativeLayout)findViewById(R.id.main_page_testing_button_rl);

        final Button backButton =(Button)findViewById(R.id.choose_player_back_button);
        playerEnterContinueButton = (Button)findViewById(R.id.choose_player_button);


        preferences = getSharedPreferences("CSRPreferences", Context.MODE_PRIVATE);
        sharedPreferencesEditor = preferences.edit();
        sharedPreferencesEditor.commit();

        if (preferences.getString(CSRConstants.coachNameString, "") != ""){
            signInRL.setVisibility(View.GONE);
            mainPageRL.setVisibility(View.VISIBLE);
            playerRL.setVisibility(View.GONE);
        }
        fillInTestingView();


        //clearAllPlayerData(); //USED FOR TESTING PURPOSES




        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerRL.setVisibility(View.GONE);
                mainPageRL.setVisibility(View.VISIBLE);
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = signInEditText.getText().toString();
                if (s.equals("")){
                    Toast t = Toast.makeText(context, "Please Enter a Name", Toast.LENGTH_LONG);
                    t.show();
                } else {
                    CSRConstants.coachUserName = signInEditText.getText().toString();
                    sharedPreferencesEditor.putString(CSRConstants.coachNameString, signInEditText.getText().toString()).commit();
                    signInRL.setVisibility(View.GONE);
                    mainPageRL.setVisibility(View.VISIBLE);
                    signInEditText.setText("");
                    fillInTestingView();
                }
            }
        });

        /*
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CSRConstants.coachUserName = "";
                sharedPreferencesEditor.putString(CSRConstants.coachNameString, "").commit();
                signInRL.setVisibility(View.VISIBLE);
                mainPageRL.setVisibility(View.GONE);
            }
        });
        */


        baselineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CSRConstants.isBaseline = true;
                fillInPlayerView();
                mainPageRL.setVisibility(View.GONE);
                playerRL.setVisibility(View.VISIBLE);

            }
        });

        concussionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CSRConstants.isBaseline = false;
                fillInPlayerView();
                mainPageRL.setVisibility(View.GONE);
                playerRL.setVisibility(View.VISIBLE);

            }
        });

        rosterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CSRConstants.isFromSelectPlayerPageToRoster = false;
                Intent rosterActivity = new Intent(MainActivity.this, RosterActivity.class);
                startActivity(rosterActivity);
            }
        });

    }


    //Add menu to the main page
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }


     /* Handles item selections in the menu*/

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	        case R.id.sign_out_in_menu:
                CSRConstants.coachUserName = "";
                sharedPreferencesEditor.putString(CSRConstants.coachNameString, "").commit();
                signInRL.setVisibility(View.VISIBLE);
                mainPageRL.setVisibility(View.GONE);
	            return true;

	        // case R.id.preferences:
	            //startActivity(new Intent(this, net.androgames.level.LevelPreferences.class));
	          //  return true;
        }
        return false;
    }

    /*
    arrayName - make it player_name_size
     */

    public boolean saveArray(ArrayList<String>array, String arrayName) {
        sharedPreferencesEditor.putInt(arrayName +"_size", array.size());
        for(int i=0;i<array.size();i++)
            sharedPreferencesEditor.putString(arrayName + "_" + i, array.get(i));
        return sharedPreferencesEditor.commit();
    }

    public ArrayList<String> loadArray(String arrayName) {
        int size = preferences.getInt(arrayName + "_size", 0);
        ArrayList<String> array = new ArrayList<String>();
        for(int i=0;i<size;i++)
            array.add(i, preferences.getString(arrayName + "_" + i, null));
        return array;
    }



    /*
    Creates view of radiobuttons for coach to select a player
   */
    private void fillInPlayerView(){
        RadioGroup playerRadioGroup = (RadioGroup) findViewById(R.id.choose_player_radiogroup);
        playerRadioGroup.removeAllViews();
        ArrayList<String> playersArray = loadArray(CSRConstants.playerArrayName);
        TextView choosePlayerTV = (TextView) findViewById(R.id.choose_player_textview);
        if (playersArray.size() ==0){
            choosePlayerTV.setText("Go to the Roster to add players");
            playerEnterContinueButton.setText("Add Player");
            playerEnterContinueButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent rosterActivity = new Intent(MainActivity.this, RosterActivity.class);
                    startActivity(rosterActivity);
                }
            });
        } else {
            choosePlayerTV.setText("Select Player:");
            playerEnterContinueButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //playerRL.setVisibility(View.GONE);
                   // mainPageRL.setVisibility(View.VISIBLE);
                    if(CSRConstants.playerUserName.equals("")){
                        Toast t = Toast.makeText(context, "Please Select a Player", Toast.LENGTH_LONG);
                        t.show();
                    } else {
                        if (CSRConstants.isBaseline){
                            Intent reactionTimeActivity = new Intent(MainActivity.this, ReactionTimeActivity.class);
                            startActivity(reactionTimeActivity);
                        } else {
                            Intent symptomActivity = new Intent(MainActivity.this, SymptomActivity.class);
                            startActivity(symptomActivity);
                        }
                    }
                }
            });
        }

        for(int i = 0; i < playersArray.size(); i++) {
            final RadioButton rdbtn = new RadioButton(this);
           // rdbtn.setId(i);
            rdbtn.setText(playersArray.get(i));
            rdbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CSRConstants.playerUserName = rdbtn.getText().toString();
                    Log.d(TAG, CSRConstants.playerUserName);
                }
            });
            playerRadioGroup.addView(rdbtn);
        }
    }


    /*
    Used for testing purposes
     */
    private void clearAllPlayerData(){
        ArrayList<String> s = new ArrayList<String>();
        //s.add("");
        saveArray(s, CSRConstants.playerArrayName);
    }

    /*
    private boolean isAPlayerSelected(){
        if (CSRConstants.)
        return false;
    }
*/


    private void fillInTestingView(){

        if(CSRConstants.coachUserName.toUpperCase().equals("ADITYA")||CSRConstants.coachUserName.toUpperCase().equals("LILY")){ //to make testing buttons appear

            testingButtonRL.setVisibility(View.VISIBLE);
            Button toTestingButton = (Button) findViewById(R.id.to_test_page_button);
            toTestingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    testingLL.setVisibility(View.VISIBLE);
                    mainPageRL.setVisibility(View.GONE);
                }
            });

            RadioButton rb_concussion = (RadioButton)findViewById(R.id.main_page_testing_concussion_rb);
            rb_concussion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CSRConstants.isBaseline = false;
            }
            });
            RadioButton rb_baseline = (RadioButton)findViewById(R.id.main_page_testing_baseline_rb);
            rb_baseline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CSRConstants.isBaseline = true;
                }
            });

            Button testReactionButton = (Button)findViewById(R.id.test_reaction_Button);
            testReactionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent reactionTimeActivity = new Intent(MainActivity.this, ReactionTimeActivity.class);
                    startActivity(reactionTimeActivity);
                //Test code - reaction time
                }
            });

            Button testPupilButton = (Button)findViewById(R.id.test_pupil_Button);
            testPupilButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent pupilActivity = new Intent(MainActivity.this, PupilINstruction.class);
                    startActivity(pupilActivity);
                    //Test code - pupil dilation
                }
            });

            Button testWMemButton = (Button)findViewById(R.id.test_w_mem_Button);
            testWMemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent wMem2Activity = new Intent(MainActivity.this, SecondWorkingMemoryActivity.class);
                    startActivity(wMem2Activity);
                    //Test code - working memory
                }
            });
            Button testVMemButton = (Button)findViewById(R.id.test_v_mem_Button);
            testVMemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent vMemActivity = new Intent(MainActivity.this, VisualMemoryActivity.class);
                    startActivity(vMemActivity);
                //Test code - visual memory
                }
            });
            Button testSWMemButton = (Button)findViewById(R.id.test_sw_mem_Button);
            testSWMemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent swMemActivity = new Intent(MainActivity.this, SecondWorkingMemoryActivity.class);
                    startActivity(swMemActivity);
                    //Test code - working memory
                }
            });
            Button extraTestButton = (Button)findViewById(R.id.extra_test_Button);
            extraTestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent balance = new Intent(MainActivity.this, Level.class);
                    startActivity(balance);
                }
            });

            Button backtoMainPageButton = (Button)findViewById(R.id.back_from_test_Page_Button);
            backtoMainPageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    testingLL.setVisibility(View.GONE);
                    mainPageRL.setVisibility(View.VISIBLE);
                }
            });

        } else {
            testingButtonRL.setVisibility(View.GONE);
        }

    }


}
