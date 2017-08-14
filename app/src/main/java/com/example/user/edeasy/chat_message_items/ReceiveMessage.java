package com.example.user.edeasy.chat_message_items;

/**
 * Created by ASUS on 20-Jul-17.
 */

public class ReceiveMessage extends Item {

    private String message;

    public ReceiveMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
