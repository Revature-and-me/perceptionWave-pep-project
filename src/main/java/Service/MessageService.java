package Service;

import DAO.MessageDAO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import Model.Message;

public class MessageService {
    private MessageDAO messageDAO;

    public MessageService(Connection connection) {
        messageDAO = new MessageDAO(connection);
    }

    public void create(Message message) throws SQLException {
        messageDAO.create(message);
    }

    public Message read(int message_id) throws SQLException {
        return messageDAO.read(message_id);
    }
    
    public List<Message> readByAccount(int account_id) throws SQLException {
        return messageDAO.readByAccount(account_id);
    }

    public void update(Message message) throws SQLException {
        messageDAO.update(message);
    }

    public void delete(Message message) throws SQLException {
        messageDAO.delete(message);
    }

    public List<Message> getAll() throws SQLException {
        return messageDAO.getAll();
    }
}

