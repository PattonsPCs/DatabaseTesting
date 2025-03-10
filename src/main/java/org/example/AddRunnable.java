package org.example;

public class AddRunnable implements Runnable{
    private final Database db = new Database();


    @Override
    public void run(){
        db.createTable();

        for (int i = 0; i < 1000; i++){
            db.addEntry("Entry #" + i);
        }
        db.close();
    }
}
