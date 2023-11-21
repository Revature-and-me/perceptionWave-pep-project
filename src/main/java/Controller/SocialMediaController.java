package Controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import Util.ConnectionUtil;
/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases.
 */
public class SocialMediaController {

    private AccountService accountService;
    private MessageService messageService;
    
    
    
    public SocialMediaController() {
        Connection connection = ConnectionUtil.getConnection();
        accountService = new AccountService(connection);
        messageService = new MessageService(connection);
    }
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        
        app.get("/", ctx -> {System.out.println("Context Running!");});

        // login and register
        app.post("/register", this::registerHandler);
        app.post("/login", this::loginHandler);

        //Accounts path
        app.get("/accounts", this::getAllAccountsHandler);
        app.get("/accounts/{account_id}", this::getAccountHandler);
        app.post("/accounts", this::createAccountHandler);
        app.patch("/accounts/{account_id}", this::updateAccountHandler);
        app.delete("/accounts/{account_id}", this::deleteAccountHandler);

        // Messages path
        app.get("/messages", this::getAllMessagesHandler);
        app.get("/messages/{message_id}", this::getMessageHandler);
        app.get("/accounts/{account_id}/messages", this::getMessagesByAccountHandler);
        app.post("/messages", this::createMessageHandler);
        app.patch("/messages/{message_id}", this::updateMessageHandler);
        app.delete("/messages/{message_id}", this::deleteMessageHandler);
        
        // return
        return app;
    }

    private void registerHandler(Context context) {
        try{
            Account account = context.bodyAsClass(Account.class);
            if (account.getUsername().isBlank() || account.getPassword().length() < 4) {
                context.status(400);
                context.json(""); //Invalid username or password
            } else if (accountService.readByUsername(account.getUsername()) != null) {
                context.status(400);
                context.json(""); //Username already exists
            } else {
                accountService.create(account);
                context.status(200);
                context.json(account);
            }
        } catch(SQLException e){
            System.out.println(e.toString());
            context.status(500);
            context.json("Internal server error");
        }
    }

    private void loginHandler(Context context) {
        try {
            Account account = context.bodyAsClass(Account.class);
            Account existingAccount = accountService.readByUsername(account.getUsername());
            if (existingAccount != null && existingAccount.getPassword().equals(account.getPassword())) {
                context.status(200);
                context.json(existingAccount);
            } else {
                context.status(401);
                context.json(""); //Unauthorized
            }
        } catch (SQLException e) {
            context.status(500);
            context.json("Internal server error");
        }
    } 
    

    private void getAllAccountsHandler(Context context) {
        try {
            List<Account> accounts = accountService.getAll();
            context.json(accounts);
        } catch (SQLException e) {
            context.status(500);
            context.json("Internal server error");
        }
    }

    private void getAccountHandler(Context context) {
        try {
            int account_id = Integer.parseInt(context.pathParam("account_id"));
            Account account = accountService.read(account_id);
            if (account != null) {
                context.json(account);
            } else {
                context.status(404);
                context.json("Account not found");
            }
        } catch (NumberFormatException e) {
            context.status(400);
            context.json("Invalid account ID");
        } catch (SQLException e) {
            context.status(500);
            context.json("Internal server error");
        }
    }

    private void createAccountHandler(Context context) {
        try {
            Account account = context.bodyAsClass(Account.class);
            accountService.create(account);
            context.status(201);
            context.json("Account created");
        } catch (SQLException e) {
            context.status(500);
            context.json("Internal server error");
        }
    }

    private void updateAccountHandler(Context context) {
        try {
            int account_id = Integer.parseInt(context.pathParam("account_id"));
            Account account = context.bodyAsClass(Account.class);
            account.setAccount_id(account_id);
            accountService.update(account);
            context.status(200);
            context.json("Account updated");
        } catch (NumberFormatException e) {
            context.status(400);
            context.json("Invalid account ID");
        } catch (SQLException e) {
            context.status(500);
            context.json("Internal server error");
        }
    }

    private void deleteAccountHandler(Context context) {
        try {
            int account_id = Integer.parseInt(context.pathParam("account_id"));
            Account account = accountService.read(account_id);
            if (account != null) {
                accountService.delete(account);
                context.status(200);
                context.json("Account deleted");
            } else {
                context.status(404);
                context.json("Account not found");
            }
        } catch (NumberFormatException e) {
            context.status(400);
            context.json("Invalid account ID");
        } catch (SQLException e) {
            context.status(500);
            context.json("Internal server error");
        }
    }

    private void getAllMessagesHandler(Context context) {
        try {
            List<Message> messages = messageService.getAll();
            context.status(200);
            context.json(messages);
        } catch (SQLException e) {
            context.status(500);
            context.json("Internal server error");
        }
    }

    private void getMessageHandler(Context context) {
        try {
            int message_id = Integer.parseInt(context.pathParam("message_id"));
            Message message = messageService.read(message_id);
            if (message != null) {
                context.status(200);
                context.json(message);
            } else {
                context.status(200);
                context.json(""); // Message not found
            }
        } catch (NumberFormatException e) {
            context.status(400);
            context.json(""); // Invalid message ID
        } catch (SQLException e) {
            context.status(500);
            context.json("Internal server error");
        }
    }

    private void getMessagesByAccountHandler(Context context) {
        try {
            int account_id = Integer.parseInt(context.pathParam("account_id"));
            List<Message> messages = messageService.readByAccount(account_id);
            context.status(200);
            context.json(messages);
        } catch (NumberFormatException e) {
            context.status(400);
            context.json("Invalid account ID");
        } catch (SQLException e) {
            context.status(500);
            context.json("Internal server error");
        }
    }
    

    private void createMessageHandler(Context context) {
        try {
            Message message = context.bodyAsClass(Message.class);
            if (message.getMessage_text().isBlank() || message.getMessage_text().length() > 255) {
                context.status(400);
                context.json(""); // Invalid message text
            } else if (accountService.read(message.getPosted_by()) == null) {
                context.status(400);
                context.json(""); //Invalid posted_by
            } else {
                messageService.create(message);
                context.status(200);
                context.json(message);
            }
        } catch (SQLException e) {
            context.status(500);
            context.json("Internal server error");
        }
    }
    
    private void deleteMessageHandler(Context context) {
        try {
            int message_id = Integer.parseInt(context.pathParam("message_id"));
            Message message = messageService.read(message_id);
            if (message != null) {
                Message deletedMessage = new Message(message.getMessage_id(), message.getPosted_by(), message.getMessage_text(), message.getTime_posted_epoch());
                messageService.delete(message);
                context.status(200);
                context.json(deletedMessage); // Return the deleted message
            } else {
                context.status(200);
                context.json(""); //Message not found
            }
        } catch (NumberFormatException e) {
            context.status(400);
            context.json("Invalid message ID");
        } catch (SQLException e) {
            context.status(500);
            context.json("Internal server error");
        }
    }
    
    
    private void updateMessageHandler(Context context) {
        try {
            int message_id = Integer.parseInt(context.pathParam("message_id"));
            Message message = context.bodyAsClass(Message.class);
            if (message.getMessage_text() == null || message.getMessage_text().isBlank() || message.getMessage_text().length() > 255) {
                context.status(400);
                context.json(""); //Invalid message text
            } else {
                Message existingMessage = messageService.read(message_id);
                if (existingMessage == null) {
                    context.status(400);
                    context.json(""); // Message not found
                } else {
                    existingMessage.setMessage_text(message.getMessage_text());
                    messageService.update(existingMessage);
                    context.status(200);
                    context.json(existingMessage);
                }
            }
        } catch (NumberFormatException e) {
            context.status(400);
            context.json("Invalid message ID");
        } catch (SQLException e) {
            context.status(500);
            context.json("Internal server error");
        }
    }
    
    

}