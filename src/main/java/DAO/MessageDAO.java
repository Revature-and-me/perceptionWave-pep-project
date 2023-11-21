package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

import Model.Message;

public class MessageDAO {
    private Connection connection;

    public MessageDAO(Connection connection) {
        this.connection = connection;
    }

    public void create(Message message) throws SQLException {
    String sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
    PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    statement.setInt(1, message.getPosted_by());
    statement.setString(2, message.getMessage_text());
    statement.setLong(3, message.getTime_posted_epoch());
    int affectedRows = statement.executeUpdate();
    if (affectedRows == 0) {
        throw new SQLException("Creating message failed, no rows affected.");
    }
    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
            message.setMessage_id(generatedKeys.getInt(1));
        } else {
            throw new SQLException("Creating message failed, no ID obtained.");
        }
    }
}


    public Message read(int message_id) throws SQLException {
        String sql = "SELECT * FROM message WHERE message_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, message_id);
        ResultSet result = statement.executeQuery();
        if (result.next()) {
            int posted_by = result.getInt("posted_by");
            String message_text = result.getString("message_text");
            long time_posted_epoch = result.getLong("time_posted_epoch");
            return new Message(message_id, posted_by, message_text, time_posted_epoch);
        }
        return null;
    }

    public void update(Message message) throws SQLException {
        String sql = "UPDATE message SET posted_by = ?, message_text = ?, time_posted_epoch = ? WHERE message_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, message.getPosted_by());
        statement.setString(2, message.getMessage_text());
        statement.setLong(3, message.getTime_posted_epoch());
        statement.setInt(4, message.getMessage_id());
        statement.executeUpdate();
    }

    public void delete(Message message) throws SQLException {
        String sql = "DELETE FROM message WHERE message_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, message.getMessage_id());
        statement.executeUpdate();
    }

    public List<Message> getAll() throws SQLException {
        String sql = "SELECT * FROM message";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();
        List<Message> messages = new ArrayList<>();
        while (result.next()) {
            int message_id = result.getInt("message_id");
            int posted_by = result.getInt("posted_by");
            String message_text = result.getString("message_text");
            long time_posted_epoch = result.getLong("time_posted_epoch");
            messages.add(new Message(message_id, posted_by, message_text, time_posted_epoch));
        }
        return messages;
    }

    public List<Message> readByAccount(int account_id) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM message WHERE posted_by = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, account_id);
        ResultSet result = statement.executeQuery();
        while (result.next()) {
            int message_id = result.getInt("message_id");
            String message_text = result.getString("message_text");
            long time_posted_epoch = result.getLong("time_posted_epoch");
            messages.add(new Message(message_id, account_id, message_text, time_posted_epoch));
        }
        return messages;
    }
    
}
