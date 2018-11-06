
package edu.vt.managers;

import edu.vt.globals.Password;
import edu.vt.EntityBeans.User;
import edu.vt.FacadeBeans.UserFacade;
import edu.vt.globals.Methods;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named(value = "loginManager")
@SessionScoped

public class LoginManager implements Serializable {

    /* Instance variables */
    private String username;
    private String password;

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

    public UserFacade getUserFacade() {
        return userFacade;
    }

    /* Instance Methods */
    // Sign in the user and redirect to their profile page.
    public String loginUser() {
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

            // Redirect to show the Profile page
            initializeSessionMap(user);
            return "/userAccount/Profile.xhtml?faces-redirect=true";
        }
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
