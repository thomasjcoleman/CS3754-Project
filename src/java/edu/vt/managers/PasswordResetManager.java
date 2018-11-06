
package edu.vt.managers;

import edu.vt.EntityBeans.User;
import edu.vt.FacadeBeans.UserFacade;
import edu.vt.globals.Constants;
import edu.vt.globals.Methods;
import edu.vt.globals.Password;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named(value = "passwordResetManager")
@SessionScoped

public class PasswordResetManager implements Serializable {

  /* Instance Variables */
  private String username;
  private String password;
  private String confirmPassword;
  private String answerToSecurityQuestion;

  @EJB
  private UserFacade userFacade;

  /* Getter and Setter Methods */
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

  public String getConfirmPassword() {
    return confirmPassword;
  }

  public void setConfirmPassword(String confirmPassword) {
    this.confirmPassword = confirmPassword;
  }

  public String getAnswerToSecurityQuestion() {
    return answerToSecurityQuestion;
  }

  public void setAnswerToSecurityQuestion(String answerToSecurityQuestion) {
    this.answerToSecurityQuestion = answerToSecurityQuestion;
  }

  public UserFacade getUserFacade() {
    return userFacade;
  }

  /* Instance Methods */
  // Check existance of a user and redirect to their security question.
  public String usernameSubmit() {
    Methods.preserveMessages();

    User user = getUserFacade().findByUsername(username);
    if (user == null) {
      Methods.showMessage("Fatal Error", "Unknown Username!",
              "Entered username " + username + " does not exist!");
      return "";
    } else {
      // Redirect to show the SecurityQuestion page
      return "/userPasswordChange/SecurityQuestion?faces-redirect=true";
    }
  }

  // Get the security question for this user.
  public String getSelectedSecurityQuestionForUsername() {
    User user = getUserFacade().findByUsername(username);
    int questionNumber = user.getSecurityQuestionNumber();
    return Constants.QUESTIONS[questionNumber];
  }

  // Check the submitted answer.
  public String securityAnswerSubmit() {
    Methods.preserveMessages();

    // Obtain the object reference of the User object with username
    User user = getUserFacade().findByUsername(username);
    String actualSecurityAnswer = user.getSecurityAnswer();
    String enteredSecurityAnswer = getAnswerToSecurityQuestion();
    if (actualSecurityAnswer.equals(enteredSecurityAnswer)) {
      // Correct answer.
      return "/userPasswordChange/ResetPassword?faces-redirect=true";

    } else {
      Methods.showMessage("Error", "Answer to the Security Question is Incorrect!", "");
      return "";
    }
  }

  // Reset the user's password.
  public String resetPassword() {
    // Verify confirmed password equals the entered one.
    if (!password.equals(confirmPassword)) {
      Methods.showMessage("Fatal Error", "Unmatched Passwords!",
              "Password and Confirm Password must Match!");
      return "";
    }

    // Password is valid now.
    Methods.preserveMessages();

    User user = getUserFacade().findByUsername(username);
    try {
      // Hash the password and store it.
      String parts = Password.createHash(password);
      user.setPassword(parts);
      getUserFacade().edit(user);
      username = password = confirmPassword = answerToSecurityQuestion = "";

      // Redirect to the index.
      Methods.showMessage("Information", "Success!", "Your Password has been Reset!");
      return "/index?faces-redirect=true";

    } catch (EJBException | Password.CannotPerformOperationException ex) {
      Methods.showMessage("Fatal Error", "Something went wrong while resetting your password!",
              "See: " + ex.getMessage());
    }
    return "";
  }

}
