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

@FacesValidator("emailValidator")
public class EmailValidator implements Validator {

  @Override
  public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    String email = (String) value;

    if (email == null || email.isEmpty()) {
      // If no entered email, do nothing; the required flag will catch this.
      return;
    }

    // Regex to validate the email address entered
    String regex = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    if (!email.matches(regex)) {
      throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_FATAL,
              "Unrecognized Email Address!", "Please Enter a Valid Email Address!"));
    }
  }

}
