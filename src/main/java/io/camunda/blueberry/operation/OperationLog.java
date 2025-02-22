package io.camunda.blueberry.operation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OperationLog {


    private final List<Message> listMessages = new ArrayList<>();

    public OperationLog() {
    }

    public void info(String message) {
        Message msg = new Message();
        msg.type = Type.INFO;
        msg.message = message;
        msg.date = new Date();
        listMessages.add(msg);
    }

    public void warning(String message) {
        Message msg = new Message();
        msg.type = Type.WARNING;
        msg.message = message;
        msg.date = new Date();
        listMessages.add(msg);
    }

    public void error(String message) {
        Message msg = new Message();
        msg.type = Type.ERROR;
        msg.message = message;
        msg.date = new Date();
        listMessages.add(msg);
    }

    public List<Message> getMessages() {
        return listMessages;
    }

    enum Type {INFO, WARNING, ERROR}

    public class Message {
        public Type type;
        public String message;
        public Date date;
    }
}
