package org.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Database {
    private final HikariDataSource dataSource;
    private static final Logger logger = LoggerFactory.getLogger(Database.class);
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
            logger.error("Error creating table: ",  e);
        }
    }

    public void addEntry(String entryText){
        String sql = "INSERT INTO entries (entry_TEXT) VALUES(?)";
        try (Connection connection = dataSource.getConnection(); var prepStmt = connection.prepareStatement(sql)){
            prepStmt.setString(1, entryText);
            prepStmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error adding entry: ", e);
        }
    }

    public void deleteEntry(String entryText){
        String sql = "DELETE FROM entries WHERE entry_TEXT = ?";
        try (Connection connection = dataSource.getConnection(); PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, entryText);
            System.out.println("Attempting to delete entry: '" + entryText + "'");
            int rowsAffected = stmt.executeUpdate();
            if(rowsAffected > 0){
                System.out.println("Entry deleted successfully.");
            } else {
                System.out.println("No entry found with the provided text.");
            }
        } catch (SQLException e) {
            logger.error("Error deleting entry: ", e);
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
            logger.error("Error getting table length: ", e);
            return -1;
        }
    }

    public String getID(){
        String sql = "SELECT entry_ID FROM entries";
        try (Connection connection = dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println("Trying to get ID...");
            if(resultSet.next()){
                return resultSet.getNString("entry_ID");
            }
            else{
                return null;
            }
        } catch (SQLException e){
            logger.error("Error getting ID: ", e);
            return "Error Thrown.";
        }
    }

    public String getValue(){
        String sql = "SELECT entry_TEXT FROM entries";
        try (Connection connection = dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println("Trying to get value...");
            if(resultSet.next()){
                return resultSet.getNString("entry_TEXT");
            }
            else {
                return null;
            }

        } catch (SQLException e) {
            logger.error("Error getting value: ", e);
            return "Error Thrown.";
        }
    }

    public static void main(String[] args) {
        Database db = new Database();
        db.createTable();

        Thread getThread = new Thread(new GetRunnable());

        getThread.start();
        db.close();
    }
}

