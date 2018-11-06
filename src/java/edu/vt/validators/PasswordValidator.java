/*
 * Created by Thomas Coleman on 2018.10.30
 * Copyright © 2018 Thomas Coleman. All rights reserved.
 */
package edu.vt.validators;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("passwordValidator")
public class PasswordValidator implements Validator {

  @Override
  public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    String enteredPassword = (String) value;

    if (enteredPassword == null || enteredPassword.isEmpty()) {
      // If no entered password, do nothing; the required flag will catch this.
      return;
    }

    /* Disallow any whitespace. */
    int pwdLength = enteredPassword.length();
    for (int i = 0; i < pwdLength; i++) {
      if (Character.isWhitespace(enteredPassword.charAt(i))) {
        throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_FATAL,
                "Password cannot contain a whitespace!", "Please enter a valid password!"));
      }
    }

    // Regex to validate the strength of enteredPassword.
    String regex = "^(?=.{8,32})(?=.*[!@#$%^&*()])(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9]).*$";

    if (!enteredPassword.matches(regex)) {
      throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_WARN,
              "Password Failed!", "The password must be minimum 8 and maximum 32 "
              + "characters long, contain at least one special character above the numbers on the keyboard, "
              + "contain at least one uppercase letter, "
              + "contain at least one lowercase letter, "
              + "and contain at least one digit 0 to 9."));
    }
  }

}
