package org.example;

public class GetRunnable implements Runnable{
    private final Database db = new Database();

    @Override
    public void run(){
        db.createTable();

        String ID = db.getID();
        String value = db.getValue();

        System.out.println("ID: " + ID);
        System.out.println("Value: " + value);
    }
}
