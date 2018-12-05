package edu.vt.controllers;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named(value = "editorController")
@SessionScoped

public class EditorController implements Serializable {

    /*
     ============================
     Instance Variable (Property)
     ============================
     */
    private String emailMessage;

    /*
    ==================
    Constructor Method
    ==================
     */
    public EditorController() {
        emailMessage = "";      // Initialize emailMessage
    }

    /*
    =========================
    Getter and Setter Methods
    =========================
     */
    public String getEmailMessage() {
        return emailMessage;
    }

    public void setEmailMessage(String givenEmailMessage) {
        this.emailMessage = givenEmailMessage;
    }

    /*
    ===================
    Clear Email Content
    ===================
     */
    public void clearEmailContent() {

        emailMessage = "";
    }

}