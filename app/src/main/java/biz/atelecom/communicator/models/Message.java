package biz.atelecom.communicator.models;

import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import biz.atelecom.communicator.ChatFragment;

/**
 * Created by vignatyev on 24.03.2016.
 */
public class Message {
    public static String JPARAM_I_MESSAGE = "I_MESSAGE";
    public static String JPARAM_FROM = "FROM";
    public static String JPARAM_TO = "TO";
    public static String JPARAM_BODY = "BODY";
    public static String JPARAM_ISSUE_DATE = "ISSUE_DATE";
    public static String JPARAM_VIEWED = "VIEWED";
    public Message() {
    }
    public Message(JSONObject jObj) throws JSONException, ParseException {
        if( jObj.has(JPARAM_I_MESSAGE)){
            iMessage = jObj.getInt(JPARAM_I_MESSAGE);
        }
        if( jObj.has(JPARAM_FROM)){
            from = jObj.getString(JPARAM_FROM);
        }
        if( jObj.has(JPARAM_TO)){
            to = jObj.getString(JPARAM_TO);
        }
        if( jObj.has(JPARAM_BODY)){
            body = jObj.getString(JPARAM_BODY);
        }
        if( jObj.has(JPARAM_ISSUE_DATE)){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            issueDate = sdf.parse(jObj.getString(JPARAM_ISSUE_DATE));
        }
        if( jObj.has(JPARAM_VIEWED)){
            viewed = (jObj.getInt(JPARAM_VIEWED) == 1);
        }
    }
    public Message(Bundle data) {
        this.iMessage = data.getInt(ChatFragment.ARG_I_MESSAGE, 0);
        this.from = data.getString(ChatFragment.ARG_NUMBER_A, null);
        this.to = data.getString(ChatFragment.ARG_NUMBER_B, null);
        this.body = data.getString(ChatFragment.ARG_BODY, null);
        this.issueDate = null;
        this.viewed = null;
    }
    public int iMessage;
    public String from;
    public String to;
    public String body;
    public Date issueDate;
    public Boolean viewed;
}
