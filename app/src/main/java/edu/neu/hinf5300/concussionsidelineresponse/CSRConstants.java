package edu.neu.hinf5300.concussionsidelineresponse;

import java.util.ArrayList;

/**
 * Created by elizabethstowell on 11/9/16.
 */

public class CSRConstants {

    public static String playerArrayName = "player_names_array";
    public static String coachNameString = "coach_name_string";
    public static String baselineString = ".Baseline";
    public static String hasBaselineString = ".BaselineFinished";
    public static String concussionString = ".Concussion";
    public static String reactionTimeString = ".ReactionTime";
    public static String reactionTimeYellowDotString = ".ReactionTimeYellowDot";
    public static String workingMemoryString = ".WorkingMemory";
    public static String secondWorkingMemoryString = ".SecondWorkingMemory";
    public static String visualMemoryString = ".VisualMemory";
    public static String pupilWidth1String = ".pupilWidth1";
    public static String pupilWidth2String = ".pupilWidth2";
    public static String pupilWidth3String = ".pupilWidth3";
    public static String pupilWidth4String = ".pupilWidth4";
    public static String symptomsString = ".symptoms";
    public static String pcpNumber = ".pcpNumber";
    public static String baselineDate = ".baselineDate";
    public static String tandemBalanceString = ".tandemBalance";
    public static String semiTandemBalanceString = ".semiTandemBalance";


    public static String coachUserName = "";
    public static String playerUserName = "";

    public static boolean isBaseline = false;
    public static boolean isFromSelectPlayerPageToRoster = false;
    public static boolean isBaselineFinished = false; //TODO update this to be at the end of the baseline test
    public static boolean isLikelyConcussed = false;
    public static String concussionDetectionString = "";
    public static String fromPage = "";

    public static ArrayList<Integer> symptoms;
    public static long reactiontime_score_baseline;
    public static long reactiontime_score_concussion;
    public static long reactiontime_yellow_score_baseline;
    public static long reactiontime_yellow_score_concussion;
    public static Integer memory_score_baseline;
    public static Integer memory_score_concussion;



}
