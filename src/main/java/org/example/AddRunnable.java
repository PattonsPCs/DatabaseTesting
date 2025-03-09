package org.example;

public class AddRunnable implements Runnable{

    @Override
    public void run(){
        Database db = new Database();
        db.createTable();

        for (int i = 0; i < 10; i++){
            db.addEntry("Entry #" + i);
        }
        db.close();
    }
}
