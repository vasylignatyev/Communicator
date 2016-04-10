package biz.atelecom.communicator.models;

import android.os.Bundle;

import java.util.Date;

import biz.atelecom.communicator.ChatFragment;

/**
 * Created by vignatyev on 24.03.2016.
 */
public class Message {
    public Message() {
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
