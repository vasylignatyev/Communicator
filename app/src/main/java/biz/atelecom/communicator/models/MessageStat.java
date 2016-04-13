package biz.atelecom.communicator.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by vignatyev on 31.03.2016.
 */
public class MessageStat {
    public static String JPARAM_ID = "ID";
    public static String JPARAM_BODY = "BODY";
    public static String JPARAM_UNVIEWED = "UNVIEWED";
    public static String JPARAM_ISSUE_DATE = "ISSUE_DATE";
    public static String JPARAM_ONLINE = "ONLINE";


    public MessageStat(JSONObject jObj) throws JSONException, ParseException {
        if(jObj.has(JPARAM_ID)) {
            id =jObj.getString(JPARAM_ID);
        }
        if(jObj.has(JPARAM_BODY)) {
            body =jObj.getString(JPARAM_BODY);
        }
        if(jObj.has(JPARAM_UNVIEWED)) {
            unviewed =jObj.getInt(JPARAM_UNVIEWED);
        }
        if(jObj.has(JPARAM_ONLINE)) {
            online = jObj.getInt(JPARAM_ONLINE);
        }
        if(jObj.has(JPARAM_ISSUE_DATE)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            issueDate = sdf.parse(jObj.getString(JPARAM_ISSUE_DATE));
        }
    }
    public String id;
    public String body;
    public Date issueDate;
    public int unviewed = 0;
    public int online = 0;
}
