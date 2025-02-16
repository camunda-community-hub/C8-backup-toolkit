package io.camunda.blueberry.operation;

import io.camunda.blueberry.client.OperateClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OperationLog {


    public OperationLog(){
    }

    enum Type{INFO,WARNING,ERROR    };
    public class Message {
        public Type type;
        public String message;
        public Date date;
    }
    private List<Message> listMessages = new  ArrayList<>();

    public void info(String message) {
        Message msg = new Message();
        msg.type = Type.INFO;
        msg.message = message;
        msg.date = new Date();
        listMessages.add( msg);
    }

    public void warning(String message) {
        Message msg = new Message();
        msg.type = Type.WARNING;
        msg.message = message;
        msg.date = new Date();
        listMessages.add( msg);
    }

    public void error(String message) {
        Message msg = new Message();
        msg.type = Type.ERROR;
        msg.message = message;
        msg.date = new Date();
        listMessages.add( msg);
    }

   public List<Message> getMessages() {return listMessages;}
}
