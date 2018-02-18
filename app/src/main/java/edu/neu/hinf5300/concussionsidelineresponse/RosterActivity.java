package edu.neu.hinf5300.concussionsidelineresponse;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;

public class RosterActivity extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor sharedPreferencesEditor;
    public Context context;
    private String TAG = "RosterActivity";
    private EditText firstNameET;
    private EditText lastNameET;
    private EditText pcpET;
    private RelativeLayout current_players_rl;
    private RelativeLayout add_players_rl;
    private ArrayList<TableLayout> tableLayoutArrayList = new ArrayList<TableLayout>();
    private ArrayList<TextView> pcpInfoTextViewArrayList = new ArrayList<TextView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roster);
        context = this;

        preferences = getSharedPreferences("CSRPreferences", Context.MODE_PRIVATE);
        sharedPreferencesEditor = preferences.edit();
        sharedPreferencesEditor.commit();

        current_players_rl = (RelativeLayout)findViewById(R.id.current_players_rl);
        add_players_rl = (RelativeLayout)findViewById(R.id.add_players_rl);
        firstNameET = (EditText) findViewById(R.id.first_name_et);
        lastNameET = (EditText) findViewById(R.id.last_name_et);
        pcpET = (EditText) findViewById(R.id.pcp_number_et);

        //final TextView test = (TextView)findViewById(R.id.test_results_text_view);
        final Button save_button = (Button) findViewById(R.id.save_player_button);
        final Button current_player_button = (Button)findViewById(R.id.current_players_button);
        final Button add_player_button = (Button)findViewById(R.id.add_player_button);
        final TextView no_players_tv = (TextView)findViewById(R.id.no_players_roster_tv);




        if(CSRConstants.isFromSelectPlayerPageToRoster){
            current_player_button.setBackgroundColor(getResources().getColor(R.color.colorSelectedButton));
            add_player_button.setBackgroundColor(getResources().getColor(R.color.mainButtonColor));
            current_players_rl.setVisibility(View.GONE);
            add_players_rl.setVisibility(View.VISIBLE);
            save_button.setText("Back to Baseline");
        } else {
            current_player_button.setBackgroundColor(getResources().getColor(R.color.colorSelectedButton));
            add_player_button.setBackgroundColor(getResources().getColor(R.color.mainButtonColor));
            current_players_rl.setVisibility(View.VISIBLE);
            add_players_rl.setVisibility(View.GONE);
            save_button.setText("Save");
        }


        add_player_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_player_button.setBackgroundColor(getResources().getColor(R.color.mainButtonColor));
                add_player_button.setBackgroundColor(getResources().getColor(R.color.colorSelectedButton));
                current_players_rl.setVisibility(View.GONE);
                add_players_rl.setVisibility(View.VISIBLE);
                firstNameET.setText("");
                lastNameET.setText("");
                pcpET.setText("");

            }
        });

        current_player_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_player_button.setBackgroundColor(getResources().getColor(R.color.colorSelectedButton));
                add_player_button.setBackgroundColor(getResources().getColor(R.color.mainButtonColor));
                current_players_rl.setVisibility(View.VISIBLE);
                add_players_rl.setVisibility(View.GONE);
            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPlayerToTeam();
             //   test.setText(createPlayerData());
                createCurrentPlayerView(no_players_tv);
                if(CSRConstants.isFromSelectPlayerPageToRoster){
                    Intent mainActivity = new Intent(RosterActivity.this, MainActivity.class);
                    startActivity(mainActivity);
                } else {
                    current_players_rl.setVisibility(View.VISIBLE);
                    add_players_rl.setVisibility(View.GONE);
                }
            }
        });

        Button main_menu_button = (Button) findViewById(R.id.main_menu_button);
        main_menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(RosterActivity.this, MainActivity.class);
                startActivity(mainActivity);

            }
        });

    //    test.setText(createPlayerData());
        createCurrentPlayerView(no_players_tv);

    }



    public void addPlayerToTeam(){
        String firstName = firstNameET.getText().toString();
        String lastName = lastNameET.getText().toString();
        String pcpNumber = pcpET.getText().toString();
        if (firstName.equals("") || lastName.equals("") || pcpNumber.equals("")){
            Toast t = Toast.makeText(context, "Please enter the player's first and last name", Toast.LENGTH_LONG);
            t.show();
        }  else if (pcpNumber.length() != 10){
            Toast t2 = Toast.makeText(context, "The number must have exactly 10 digits", Toast.LENGTH_LONG);
            t2.show();
        } else {
            String name = firstName +  " " + lastName;
            ArrayList<String> playerNames = loadArray(CSRConstants.playerArrayName);
            if (!playerNames.contains(name)) {
                playerNames.add(name);
                saveArray(playerNames, CSRConstants.playerArrayName);
                String num = "tel:" + pcpNumber;
                sharedPreferencesEditor.putString(CSRConstants.playerUserName + CSRConstants.pcpNumber,num).commit();
            } else {
                Toast t3 = Toast.makeText(context, name + " is already in the Roster", Toast.LENGTH_LONG);
                t3.show();
            }

        }
    }


    public boolean saveArray(ArrayList<String> array, String arrayName) {
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



    private void createCurrentPlayerView(TextView t){
        t = (TextView) findViewById(R.id.no_players_roster_tv);
        RadioGroup playerRG = (RadioGroup)findViewById(R.id.test_results_radio_group);
        playerRG.removeAllViews();
        ArrayList<String> playersArray = loadArray(CSRConstants.playerArrayName);
        if (playersArray.size() ==0){
            t.setVisibility(View.VISIBLE);
        } else {
           t.setVisibility(View.GONE);
        }

        for(int i = 0; i < playersArray.size(); i++) {
            final RadioButton rdbtn = new RadioButton(this);
            final TextView playerData = new TextView(this);
            playerData.setVisibility(View.GONE);
            playerData.setText(createPCPInfoString(playersArray.get(i)));
            final TableLayout playerDataTL = new TableLayout(this);
            TableLayout.LayoutParams params = new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.WRAP_CONTENT,
                    TableLayout.LayoutParams.WRAP_CONTENT
            );
            //TODO fix margins!
            params.setMargins(100, 100, 5, 5);
            playerDataTL.setLayoutParams(params);

            playerDataTL.setVisibility(View.GONE);

            rdbtn.setText(playersArray.get(i));
            rdbtn.setTextSize(15);
            rdbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideAllPlayerData();
                    CSRConstants.playerUserName = rdbtn.getText().toString();
                    playerDataTL.setVisibility(View.VISIBLE);
                    playerData.setVisibility(View.VISIBLE);

                }
            });
            playerRG.addView(rdbtn);
            playerRG.addView(playerData);
            createSinlePlayerDataTable(playerRG, playerDataTL, playersArray.get(i));
            tableLayoutArrayList.add(playerDataTL);
            pcpInfoTextViewArrayList.add(playerData);
        }
    }



    private void createSinlePlayerDataTable(RadioGroup r, TableLayout tl, String playerName){
        if (preferences.getBoolean((playerName + CSRConstants.hasBaselineString), false)) {
            Log.d(TAG, "Creating Table...");
            //Create Top row/labels
            tl.addView(createNewPlayerDataRow(true, "Test", "Baseline\t", "Concussion"));
            //Create and add reaction time Info:
            tl.addView(createNewPlayerDataRow(false, "Reaction Time\t", "" + preferences.getLong((playerName + CSRConstants.baselineString + CSRConstants.reactionTimeString), 0), "" +preferences.getLong((playerName + CSRConstants.concussionString + CSRConstants.reactionTimeString),0)));
            //Create and add reaction time Yellow Dot Info:
            tl.addView(createNewPlayerDataRow(false, "Yellow Dots\t", "" + preferences.getInt((playerName + CSRConstants.baselineString + CSRConstants.reactionTimeYellowDotString), 0), "" +preferences.getInt((playerName + CSRConstants.concussionString + CSRConstants.reactionTimeYellowDotString), 0)));
            //Create and add semitandem balance Info:
            tl.addView(createNewPlayerDataRow(false, "Balance SemiTandem\t", "" + preferences.getInt((playerName + CSRConstants.baselineString + CSRConstants.semiTandemBalanceString), 0), "" +preferences.getInt((playerName + CSRConstants.concussionString + CSRConstants.semiTandemBalanceString), 0)));
            //Create and add tandem balance Info:
            tl.addView(createNewPlayerDataRow(false, "Balance Tandem\t", "" + preferences.getInt((playerName + CSRConstants.baselineString + CSRConstants.tandemBalanceString), 0), "" +preferences.getInt((playerName + CSRConstants.concussionString + CSRConstants.tandemBalanceString), 0)));
            //Create and add working memory Info:
            tl.addView(createNewPlayerDataRow(false, "Working Memory\t", "" + preferences.getInt((playerName + CSRConstants.baselineString + CSRConstants.workingMemoryString), 0), "" +preferences.getInt((playerName + CSRConstants.concussionString + CSRConstants.workingMemoryString), 0)));
            //Create and add second working memory Info:
            tl.addView(createNewPlayerDataRow(false, "Working Memory2\t", "" + preferences.getInt((playerName + CSRConstants.baselineString + CSRConstants.secondWorkingMemoryString), 0), "" +preferences.getInt((playerName + CSRConstants.concussionString + CSRConstants.secondWorkingMemoryString), 0)));
            //Create and add visual memory Info:
            tl.addView(createNewPlayerDataRow(false, "Visual Memory\t", "" + preferences.getInt((playerName + CSRConstants.baselineString + CSRConstants.visualMemoryString), 0), "" +preferences.getInt((playerName + CSRConstants.concussionString + CSRConstants.visualMemoryString), 0)));
        }
        r.addView(tl);
    }

    private TableRow createNewPlayerDataRow(boolean isTitle, String test, String bl_score, String c_score){
        TableRow playerDataRow = new TableRow(this);
        playerDataRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        TextView test_tv = new TextView(this);
        test_tv.setText(test);
        TextView baseline_tv = new TextView(this);
        baseline_tv.setText(bl_score);
        TextView concussion_tv = new TextView(this);
        concussion_tv.setText(c_score);
        if (isTitle){
            test_tv.setTypeface(test_tv.getTypeface(), Typeface.BOLD);
            baseline_tv.setTypeface(baseline_tv.getTypeface(), Typeface.BOLD);
            concussion_tv.setTypeface(concussion_tv.getTypeface(), Typeface.BOLD);

        }

        playerDataRow.addView(test_tv);
        playerDataRow.addView(baseline_tv);
        playerDataRow.addView(concussion_tv);
        return playerDataRow;
    }

    private String createPCPInfoString(String playerName){
        String s = "\nPCP's Number: " + preferences.getString(playerName + CSRConstants.pcpNumber, "No PCP # Recorded");
        if (preferences.getBoolean((playerName + CSRConstants.hasBaselineString), false)){
            s =  s + "\nDate of Baseline: " + preferences.getString((playerName + CSRConstants.baselineDate), "");
        } else {
            s = s + "\nNo Baseline Recorded";
        }
        return s;
    }

    private void hideAllPlayerData(){
        for (int i = 0; i < tableLayoutArrayList.size(); i++){
            tableLayoutArrayList.get(i).setVisibility(View.GONE);
            pcpInfoTextViewArrayList.get(i).setVisibility(View.GONE);
        }

    }

    /*
    private String createSinglePlayerData(String playerName){
        String s = "";
        if (preferences.getBoolean((playerName + CSRConstants.hasBaselineString), false)){
                    //READS IN REACTION TIME SCORES
                    s = s + "\n\tReaction Time Score:\t\t\t\t\t\t\t\t" + preferences.getLong((playerName + CSRConstants.baselineString + CSRConstants.reactionTimeString), 0)
                    + "\t\t" + preferences.getLong((playerName + CSRConstants.concussionString + CSRConstants.reactionTimeString), 0)
                    //READS IN YELLOWS HIT DURING REACTION TIME
                    + "\n\tReaction Time Yellows Hit:\t\t\t\t\t\t" + preferences.getInt((playerName + CSRConstants.baselineString + CSRConstants.reactionTimeYellowDotString), 0)
                    + "\t\t" +preferences.getInt((playerName + CSRConstants.concussionString + CSRConstants.reactionTimeYellowDotString), 0)
                    //READ IN WORKING MEMORY SCORES
                    + "\n\tWorking Memory Score:\t\t\t\t\t\t\t\t" + preferences.getInt((playerName + CSRConstants.baselineString + CSRConstants.workingMemoryString), 0)
                    + "\t\t" +preferences.getInt((playerName + CSRConstants.concussionString + CSRConstants.workingMemoryString), 0)
                    //READS IN SECOND WORKING MEMORY SCORES
                    + "\n\tSecond Working Memory Score:\t\t" + preferences.getInt((playerName + CSRConstants.baselineString + CSRConstants.secondWorkingMemoryString), 0)
                    +"\t\t" + preferences.getInt((playerName + CSRConstants.concussionString + CSRConstants.secondWorkingMemoryString), 0)
                    //READS IN VISUAL MEMORY SCORES
                    + "\n\tVisual Memory Score:\t\t\t\t\t\t\t\t\t\t" + preferences.getInt((playerName + CSRConstants.baselineString + CSRConstants.visualMemoryString), 0)
                    +"\t\t" + preferences.getInt((playerName + CSRConstants.concussionString + CSRConstants.visualMemoryString), 0)
                    +"\n";

        }
        return s;
    }




    public String createAllPlayerData(){
        String s = "Coach: " + CSRConstants.coachUserName;
        ArrayList<String> playerArray = loadArray(CSRConstants.playerArrayName);
        if (playerArray.size() == 0){
            s = s + "\nNo Players in the Roster";
        } else {
            for (int i = 0; i < playerArray.size(); i++){
                s = s + "\nPlayer Name: " + playerArray.get(i);
                s = s + "\nPCP's Number: " + preferences.getString(playerArray.get(i) + CSRConstants.pcpNumber, "No PCP # Recorded");
                if (preferences.getBoolean((playerArray.get(i) + CSRConstants.hasBaselineString), false)){
                    s = s + "\nDate of Baseline: " + preferences.getString((playerArray.get(i) + CSRConstants.baselineDate), "");
                    s = s + "\n\t\t\t\t\t\t\tBASELINE\t\tCONCUSSION"
                            //READS IN REACTION TIME SCORES
                            + "\n\tReaction Time Score:\t\t" + preferences.getLong((playerArray.get(i) + CSRConstants.baselineString + CSRConstants.reactionTimeString), 0)
                            + "\t\t" + preferences.getLong((playerArray.get(i) + CSRConstants.concussionString + CSRConstants.reactionTimeString), 0)
                            //READS IN YELLOWS HIT DURING REACTION TIME
                            + "\n\tReaction Time Yellows Hit:\t\t" + preferences.getInt((playerArray.get(i) + CSRConstants.baselineString + CSRConstants.reactionTimeYellowDotString), 0)
                            + "\t\t" +preferences.getInt((playerArray.get(i) + CSRConstants.concussionString + CSRConstants.reactionTimeYellowDotString), 0)
                            //READ IN WORKING MEMORY SCORES
                            + "\n\tWorking Memory Score:\t\t" + preferences.getInt((playerArray.get(i) + CSRConstants.baselineString + CSRConstants.workingMemoryString), 0)
                            + "\t\t" +preferences.getInt((playerArray.get(i) + CSRConstants.concussionString + CSRConstants.workingMemoryString), 0)
                            //READS IN SECOND WORKING MEMORY SCORES
                            + "\n\tSecond Working Memory Score:\t\t" + preferences.getInt((playerArray.get(i) + CSRConstants.baselineString + CSRConstants.secondWorkingMemoryString), 0)
                            +"\t\t" + preferences.getInt((playerArray.get(i) + CSRConstants.concussionString + CSRConstants.secondWorkingMemoryString), 0)
                            //READS IN VISUAL MEMORY SCORES
                            + "\n\tVisual Memory Score:\t\t" + preferences.getInt((playerArray.get(i) + CSRConstants.baselineString + CSRConstants.visualMemoryString), 0)
                            +"\t\t" + preferences.getInt((playerArray.get(i) + CSRConstants.concussionString + CSRConstants.visualMemoryString), 0)
                             +"\n";

                } else {
                    s = s + "\n\tNo Baseline Recorded";
                }
            }
        }
        return s;
    }

 */
}

