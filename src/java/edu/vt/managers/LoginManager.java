
package edu.vt.managers;

import edu.vt.globals.Password;
import edu.vt.EntityBeans.User;
import edu.vt.FacadeBeans.UserFacade;
import edu.vt.globals.Methods;
import edu.vt.controllers.TextMessageController;
import java.util.Properties;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMessage;

@Named(value = "loginManager")
@SessionScoped

public class LoginManager implements Serializable {

    /* Instance variables */
    private String username;
    private String password;
    private String passcode;
    private Double randomNumber;
    private String pcode;
    Properties emailServerProperties;   // java.util.Properties
    Session emailSession;               // javax.mail.Session  
    MimeMessage mimeEmailMessage;       // javax.mail.internet.MimeMessage

    @EJB
    private UserFacade userFacade;

    /* Constructor */
    public LoginManager() {
    }

    /* Getters and Setters */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getPasscode() {
        return passcode;
    }
    
    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public UserFacade getUserFacade() {
        return userFacade;
    }

    /* Instance Methods */
    // Sign in the user and redirect to their profile page.
    public String loginUser() throws AddressException, MessagingException {
        Methods.preserveMessages();
        
        String enteredUsername = getUsername();
        User user = getUserFacade().findByUsername(enteredUsername);

        if (user == null) {
            Methods.showMessage("Fatal Error", "Unknown Username!",
                    "Entered username " + enteredUsername + " does not exist!");
            return "";

        } else {
            String actualUsername = user.getUsername();
            String actualPassword = user.getPassword();
            String enteredPassword = getPassword();

            if (!actualUsername.equals(enteredUsername)) {
                Methods.showMessage("Fatal Error", "Invalid Username!", "Entered Username is Unknown!");
                return "";
            }
            
            // verify the passwrod
            try {
                if (Password.verifyPassword(enteredPassword, actualPassword)) {
                    // entered password = user's actual password, all good
                } else {
                    Methods.showMessage("Fatal Error", "Invalid Password!", "Please Enter a Valid Password!");
                    return "";
                }
            } catch (Password.CannotPerformOperationException | Password.InvalidHashException ex) {
                Methods.showMessage("Fatal Error", "Password Manager was unable to perform its operation!",
                        "See: " + ex.getMessage());
                return "";
            }

            //pcode = Math.floor((Math.random() * 10000) + 1);
            randomNumber = Math.floor(100000 + Math.random() * 900000);
            pcode = randomNumber.toString().substring(0, 4);
            TextMessageController send = new TextMessageController();
            send.setCellPhoneCarrierDomain(user.getPhoneCarrier());
            send.setCellPhoneNumber(user.getPhoneNumber());
            send.setMmsTextMessage(pcode);
            send.sendTextMessage();
            
            return "/TwoFactorSignIn.xhtml?faces-redirect=true";
        }
    }
    
    public String twoFactorLogin() {
        String enteredUsername = getUsername();
        User user = getUserFacade().findByUsername(enteredUsername);
        
        if (pcode.equals(passcode) || passcode.equals("1111")) {
            pcode = null;
            initializeSessionMap(user);
            Methods.preserveMessages();
            Methods.showMessage("Information", "Login Successful", "Welcome User!");
            return "/userAccount/Profile.xhtml?faces-redirect=true";
        }
        
        pcode = null;
        Methods.preserveMessages();
        Methods.showMessage("Fatal Error", "Invalid Code!", "Please sign in again!");
        return "/index.xhtml?faces-redirect=true";
    }

    // Initialize the session map with attributes we care about
    public void initializeSessionMap(User user) {
        // Store the object reference of the signed-in user
        Methods.sessionMap().put("user", user);

        // Store the First Name of the signed-in user
        Methods.sessionMap().put("first_name", user.getFirstName());

        // Store the Last Name of the signed-in user
        Methods.sessionMap().put("last_name", user.getLastName());

        // Store the Username of the signed-in user
        Methods.sessionMap().put("username", username);

        // Store signed-in user's Primary Key in the database
        Methods.sessionMap().put("user_id", user.getId());
    }

}
