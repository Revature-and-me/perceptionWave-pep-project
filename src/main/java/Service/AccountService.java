package Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import DAO.AccountDAO;
import Model.Account;

public class AccountService {
    private AccountDAO accountDAO;

    public AccountService(Connection connection) {
        accountDAO = new AccountDAO(connection);
    }

    public void create(Account account) throws SQLException {
        accountDAO.create(account);
    }

    public Account read(int account_id) throws SQLException {
        return accountDAO.read(account_id);
    }

    public Account readByUsername(String username) throws SQLException {
        return accountDAO.readByUsername(username);
    }

    public void update(Account account) throws SQLException {
        accountDAO.update(account);
    }

    public void delete(Account account) throws SQLException {
        accountDAO.delete(account);
    }

    public List<Account> getAll() throws SQLException {
        return accountDAO.getAll();
    }
}

