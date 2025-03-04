package io.camunda.blueberry.operation;

import java.util.*;

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

    Map<String, List<String>> snapshotPerComponents = new HashMap<>();
    public void addSnapshotName(String component, String snapshotName) {
        List<String> listSnapshop = snapshotPerComponents.get(component);
        if (listSnapshop == null) {
            listSnapshop = new ArrayList();
        }
        listSnapshop.add(snapshotName);
        snapshotPerComponents.put(component, listSnapshop);
    }


    public class Message {
        public Type type;
        public String message;
        public Date date;
    }
}
