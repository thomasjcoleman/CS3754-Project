/*
 * Created by Thomas Coleman on 2018.10.30
 * Copyright © 2018 Thomas Coleman. All rights reserved. 
 */
package edu.vt.controllers;

import edu.vt.EntityBeans.User;
import edu.vt.EntityBeans.UserPhoto;
import edu.vt.EntityBeans.UserFile;
import edu.vt.FacadeBeans.UserFacade;
import edu.vt.FacadeBeans.UserFileFacade;
import edu.vt.FacadeBeans.UserPhotoFacade;
import edu.vt.globals.Constants;
import edu.vt.globals.Methods;
import edu.vt.globals.Password;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

@Named("userController")
@SessionScoped

public class UserController implements Serializable {

  /*-------------------
    Instance variables
   -------------------*/
  private String username;
  private String password;
  private String confirmPassword;

  private String firstName;
  private String middleName;
  private String lastName;

  private String address1;
  private String address2;
  private String city;
  private String state;
  private String zipcode;

  private int securityQuestionNumber;
  private String answerToSecurityQuestion;

  private String email;
  private String phoneNumber;
  private String phoneCarrier;

  private Map<String, Object> security_questions;

  private User selected;

  @EJB
  private UserFacade userFacade;
  @EJB
  private UserPhotoFacade userPhotoFacade;
  @EJB
  private UserFileFacade userFileFacade;

  /*------------
    Constructor
   ------------*/
  public UserController() {
  }

  /*--------------------
    Getters and Setters
   --------------------*/
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

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getMiddleName() {
    return middleName;
  }

  public void setMiddleName(String middleName) {
    this.middleName = middleName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getAddress1() {
    return address1;
  }

  public void setAddress1(String address1) {
    this.address1 = address1;
  }

  public String getAddress2() {
    return address2;
  }

  public void setAddress2(String address2) {
    this.address2 = address2;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getZipcode() {
    return zipcode;
  }

  public void setZipcode(String zipcode) {
    this.zipcode = zipcode;
  }

  public int getSecurityQuestionNumber() {
    return securityQuestionNumber;
  }

  public void setSecurityQuestionNumber(int securityQuestionNumber) {
    this.securityQuestionNumber = securityQuestionNumber;
  }

  public String getAnswerToSecurityQuestion() {
    return answerToSecurityQuestion;
  }

  public void setAnswerToSecurityQuestion(String answerToSecurityQuestion) {
    this.answerToSecurityQuestion = answerToSecurityQuestion;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getPhoneCarrier() {
    return phoneCarrier;
  }

  public void setPhoneCarrier(String phoneCarrier) {
    this.phoneCarrier = phoneCarrier;
  }

  /*
    private Map<String, Object> security_questions;
        String      int
        ---------   ---
        question1,  0
        question2,  1
        question3,  2
   */
  public Map<String, Object> getSecurity_questions() {
    if (security_questions == null) {
      security_questions = new LinkedHashMap<>();
      for (int i = 0; i < Constants.QUESTIONS.length; i++) {
        security_questions.put(Constants.QUESTIONS[i], i);
      }
    }
    return security_questions;
  }

  public void setSecurity_questions(Map<String, Object> security_questions) {
    this.security_questions = security_questions;
  }

  public User getSelected() {
    if (selected == null) {
      selected = (User) Methods.sessionMap().get("user");
    }
    return selected;
  }

  public void setSelected(User selected) {
    this.selected = selected;
  }

  public UserFacade getUserFacade() {
    return userFacade;
  }

  public void setUserFacade(UserFacade userFacade) {
    this.userFacade = userFacade;
  }

  public UserPhotoFacade getUserPhotoFacade() {
    return userPhotoFacade;
  }

  public void setUserPhotoFacade(UserPhotoFacade userPhotoFacade) {
    this.userPhotoFacade = userPhotoFacade;
  }

  public UserFileFacade getUserFileFacade() {
    return userFileFacade;
  }

  public void getUserFileFacade(UserFileFacade userFileFacade) {
    this.userFileFacade = userFileFacade;
  }

  /*------------------
     Instance Methods
    -----------------*/
  // Check if logged in
  public boolean isLoggedIn() {
    return Methods.sessionMap().get("username") != null;
  }

  // Get the list of states
  public String[] listOfStates() {
    return Constants.STATES;
  }

  // Return the security questions chosen by the user
  public String getSelectedSecurityQuestion() {
    User signedInUser = (User) Methods.sessionMap().get("user");
    int questionNumber = signedInUser.getSecurityQuestionNumber();
    return Constants.QUESTIONS[questionNumber];
  }

  // Process a submitted security question answer
  public void securityAnswerSubmit() {
    User signedInUser = (User) Methods.sessionMap().get("user");

    String actualSecurityAnswer = signedInUser.getSecurityAnswer();
    String enteredSecurityAnswer = getAnswerToSecurityQuestion();

    if (actualSecurityAnswer.equals(enteredSecurityAnswer)) {
      // correct answer; delete account
      deleteAccount();
    } else {
      Methods.showMessage("Error", "Answer to the Security Question is Incorrect!", "");
    }
  }

  // Create the user's account and redirect to sign-in page.
  public String createAccount() {
    // Check that password and confirmed password match
    if (!password.equals(confirmPassword)) {
      Methods.showMessage("Fatal Error", "Unmatched Passwords!",
              "Password and Confirm Password must Match!");
      return "";
    }

    Methods.preserveMessages();

    // Now check if the username is already being used...
    User aUser = getUserFacade().findByUsername(username);
    if (aUser != null) {
      // A user already exists
      username = "";
      Methods.showMessage("Fatal Error", "Username Already Exists!", "Please Select a Different One!");
      return "";
    }

    // Should be good now, try to make a new user...
    try {
      User newUser = new User();

      newUser.setFirstName(firstName);
      newUser.setMiddleName(middleName);
      newUser.setLastName(lastName);
      newUser.setAddress1(address1);
      newUser.setAddress2(address2);
      newUser.setCity(city);
      newUser.setState(state);
      newUser.setZipcode(zipcode);
      newUser.setSecurityQuestionNumber(securityQuestionNumber);
      newUser.setSecurityAnswer(answerToSecurityQuestion);
      newUser.setEmail(email);
      newUser.setPhoneNumber(phoneNumber);
      newUser.setPhoneCarrier(phoneCarrier);
      newUser.setUsername(username);

      // securely hash the password
      String parts = Password.createHash(password);
      newUser.setPassword(parts);

      // add the user to the database
      getUserFacade().create(newUser);

    } catch (EJBException | Password.CannotPerformOperationException ex) {
      username = "";
      Methods.showMessage("Fatal Error", "Something went wrong while creating user's account!",
              "See: " + ex.getMessage());
      return "";
    }

    /* Finally, redirect to the sign-in page. */
    Methods.showMessage("Information", "Success!", "User Account is Successfully Created!");
    return "/SignIn?faces-redirect=true";
  }

  // Update the user's account and redirect to the profile page
  public String updateAccount() {
    Methods.preserveMessages();
    User editUser = (User) Methods.sessionMap().get("user");

    try {
      // Try to update the user...
      editUser.setFirstName(this.selected.getFirstName());
      editUser.setMiddleName(this.selected.getMiddleName());
      editUser.setLastName(this.selected.getLastName());

      editUser.setAddress1(this.selected.getAddress1());
      editUser.setAddress2(this.selected.getAddress2());
      editUser.setCity(this.selected.getCity());
      editUser.setState(this.selected.getState());
      editUser.setZipcode(this.selected.getZipcode());
      editUser.setEmail(this.selected.getEmail());
      editUser.setPhoneNumber(this.selected.getPhoneNumber());
      editUser.setPhoneCarrier(this.selected.getPhoneCarrier());

      // Store the changes in the database
      getUserFacade().edit(editUser);

    } catch (EJBException ex) {
      username = "";
      Methods.showMessage("Fatal Error", "Something went wrong while updating user's profile!",
              "See: " + ex.getMessage());
      return "";
    }

    // Account update is completed, redirect to the profile page.
    Methods.showMessage("Information", "Success!", "User's Account is Successfully Updated!");
    return "/userAccount/Profile?faces-redirect=true";
  }

  // Delete account, logout, and return to home page
  public String deleteAccount() {
    Methods.preserveMessages();

    int userKey = (int) Methods.sessionMap().get("user_id");

    try {
      // Delete all of the associated files
      deleteAllUserPhotos(userKey);
      deleteAllUserFiles(userKey);

      // Delete from the database
      getUserFacade().deleteUser(userKey);

    } catch (EJBException ex) {
      username = "";
      Methods.showMessage("Fatal Error", "Something went wrong while deleting user's account!",
              "See: " + ex.getMessage());
      return "";
    }

    // Log out and return to the home page.
    Methods.showMessage("Information", "Success!", "Your Account is Successfully Deleted!");
    logout();
    return "/index?faces-redirect=true";
  }

  // Log out the user and redirect to the home page
  public void logout() {
    Methods.sessionMap().clear();

    // reset the signed-in user's properties
    username = password = confirmPassword = "";
    firstName = middleName = lastName = "";
    address1 = address2 = city = state = zipcode = "";
    securityQuestionNumber = 0;
    answerToSecurityQuestion = email = "";
    phoneNumber = phoneCarrier = "";
    selected = null;

    Methods.preserveMessages();

    try {
      // Invalidate the session...
      ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
      externalContext.invalidateSession();

      // And redirect the page.
      String redirectPageURI = externalContext.getRequestContextPath() + "/index.xhtml";
      externalContext.redirect(redirectPageURI);
    } catch (IOException ex) {
      Methods.showMessage("Fatal Error", "Unable to redirect to the index (home) page!",
              "See: " + ex.getMessage());
    }
  }

  // Get the filepath to the signed-in user's profile photo
  public String userPhoto() {
    Integer userKey = (Integer) Methods.sessionMap().get("user_id");
    List<UserPhoto> photoList = getUserPhotoFacade().findPhotosByUserPrimaryKey(userKey);

    if (photoList.isEmpty()) {
      // No user photo exists. Return defaultUserPhoto.png.
      return Constants.DEFAULT_PHOTO_RELATIVE_PATH;
    }

    // valid photo found, get the path to that one
    String thumbnailFileName = photoList.get(0).getThumbnailFileName();
    return Constants.PHOTOS_RELATIVE_PATH + thumbnailFileName;
  }

  // Delete the user's profile photo
  public void deleteAllUserPhotos(int userKey) {
    List<UserPhoto> photoList = getUserPhotoFacade().findPhotosByUserPrimaryKey(userKey);

    if (!photoList.isEmpty()) {
      // Try to delete the first photo found
      UserPhoto photo = photoList.get(0);
      try {
        Files.deleteIfExists(Paths.get(photo.getPhotoFilePath()));

        // If a thumbnail exists, delete that too
        Files.deleteIfExists(Paths.get(photo.getThumbnailFilePath()));

        // Delete from the database
        getUserPhotoFacade().remove(photo);
        
      } catch (IOException ex) {
        Methods.showMessage("Fatal Error",
                "Something went wrong while deleting user's photo!",
                "See: " + ex.getMessage());
      }
    }
  }

  // Delete all of the user's files
  public void deleteAllUserFiles(int userKey) {
    // TODO
  }

}
