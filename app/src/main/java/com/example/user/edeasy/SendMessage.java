package com.example.user.edeasy;

import android.content.ClipData;

/**
 * Created by ASUS on 20-Jul-17.
 */

public class SendMessage extends Item {
    private String message;

    public SendMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}