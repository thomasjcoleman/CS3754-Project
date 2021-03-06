package edu.vt.managers;

import edu.vt.globals.Password;
import edu.vt.EntityBeans.User;
import edu.vt.FacadeBeans.UserFacade;
import edu.vt.globals.Methods;
import edu.vt.controllers.TextMessageController;
import edu.vt.controllers.UserController;
import java.util.Properties;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
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
    @Inject
    UserController userController;

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

            //Randomly generates a number from 1000-9000
            randomNumber = Math.floor(100000 + Math.random() * 900000);
            pcode = randomNumber.toString().substring(0, 4);
            
            //Creates a new TextMessageController object to send the code.
            TextMessageController send = new TextMessageController();
            
            //The new controller is supplied with the user's carrier, number, and passcode.
            send.setCellPhoneCarrierDomain(user.getPhoneCarrier());
            send.setCellPhoneNumber(user.getPhoneNumber());
            
            //Here the text message controller sends the code.
            send.setMmsTextMessage(pcode);
            send.sendTextMessage();
            
            //Instead of initializing a session map and redirecting to the
            //profile page, the user is redirected to the 2nd factor authentication
            //to submit the passcode recieved in their text message.
            return "/TwoFactorSignIn.xhtml?faces-redirect=true";
        }
    }
    
    //This function enables the use to resend a new passcode
    public void sendCode() throws AddressException, MessagingException {
        //A user object is instantiated.
        String enteredUsername = getUsername();
        User user = getUserFacade().findByUsername(enteredUsername);
        
        //A new random number is generated for the passcode and sent to the user.
        randomNumber = Math.floor(100000 + Math.random() * 900000);
        pcode = randomNumber.toString().substring(0, 4);
        //As before we create a new TextMessageController object.
        TextMessageController send = new TextMessageController();
        
        send.setCellPhoneCarrierDomain(user.getPhoneCarrier());
        send.setCellPhoneNumber(user.getPhoneNumber());
        send.setMmsTextMessage(pcode);
        
        send.sendTextMessage();
    }
    
    //This function checks the passcode entered by the user to login.
    public String twoFactorLogin() {
        //A user object is instantiated.
        String enteredUsername = getUsername();
        User user = getUserFacade().findByUsername(enteredUsername);
        
        //The user's inputed passcode is checked against the actual generated code.
        //A code bypass is also created for testing purposes.
        if (pcode.equals(passcode) || passcode.equals("1111")) {
            pcode = null;
            //Once a match is achieved the user is logged in and recieves
            //An information message.
            initializeSessionMap(user);
            Methods.preserveMessages();
            Methods.showMessage("Information", "Login Successful", "Welcome User!");
            return "/userAccount/Profile.xhtml?faces-redirect=true";
        }
        
        //An incorrect passcode results in the fatal error message implogin the
        //user to try again.
        pcode = null;
        Methods.preserveMessages();
        Methods.showMessage("Fatal Error", "Invalid Code!", "Please try again!");
        return "";
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
