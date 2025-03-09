package org.example;

public class DeleteRunnable implements Runnable{
    final private Database db = new Database();

    @Override
    public void run(){
        db.createTable();
        int count = 0;

        while(db.getTableLength() > 0){
            db.deleteEntry("Entry #" + count);
            count++;
        }
        db.close();
    }
}
