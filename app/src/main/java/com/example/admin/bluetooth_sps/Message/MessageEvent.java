package com.example.admin.bluetooth_sps.Message;

/**
 * Created by admin on 2018/1/23.
 */

public class MessageEvent {

    private String message;

    public MessageEvent(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}
