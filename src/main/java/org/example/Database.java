package org.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;


public class Database {


    private final HikariDataSource dataSource;
    public Database() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:sqlite:file:./tester.db");
        config.setUsername("");
        config.setPassword("");

        dataSource = new HikariDataSource(config);
    }

    public void createTable(){
        try(Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()){
            statement.execute("CREATE TABLE IF NOT EXISTS entries (entry_ID INTEGER PRIMARY KEY, entry_TEXT TEXT)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addEntry(String entryText){
        String sql = "INSERT INTO entries (entry_TEXT) VALUES(?)";
        try (Connection connection = dataSource.getConnection(); var prepStmt = connection.prepareStatement(sql)){
            prepStmt.setString(1, entryText);
            prepStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteEntry(String entryText){
        String sql = "DELETE FROM entries WHERE entry_TEXT = ?";
        try (Connection connection = dataSource.getConnection(); PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, entryText);
            System.out.println("Attemping to delete entry: '" + entryText + "'");
            int rowsAffected = stmt.executeUpdate();
            if(rowsAffected > 0){
                System.out.println("Entry deleted successfully.");
            } else {
                System.out.println("No entry found with the provided text.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        if(dataSource == null){
            System.out.println("Data Source is not open.");
        } else{
            dataSource.close();
        }
    }

    public int getTableLength(){
        String sql = "SELECT COUNT(*) FROM entries";
        try (Connection connection = dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void main(String[] args) {
        Database db = new Database();
        db.createTable();


        Thread deleteThread = new Thread(new DeleteRunnable());
        Thread addThread = new Thread(new AddRunnable());
        deleteThread.start();
        db.close();
    }
}

