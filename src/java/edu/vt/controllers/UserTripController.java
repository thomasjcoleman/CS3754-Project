package edu.vt.controllers;

import edu.vt.EntityBeans.User;
import edu.vt.EntityBeans.UserTrip;
import edu.vt.controllers.util.JsfUtil;
import edu.vt.controllers.util.JsfUtil.PersistAction;
import edu.vt.FacadeBeans.UserTripFacade;
import edu.vt.globals.Methods;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("userTripController")
@SessionScoped
public class UserTripController implements Serializable {

  @EJB
  private edu.vt.FacadeBeans.UserTripFacade userTripFacade;

  private List<UserTrip> items = null;
  private UserTrip selected;

  public UserTripController() {
  }

  public UserTrip getSelected() {
    return selected;
  }

  public void setSelected(UserTrip selected) {
    this.selected = selected;
  }

  protected void setEmbeddableKeys() {
  }

  protected void initializeEmbeddableKey() {
  }

  private UserTripFacade getFacade() {
    return userTripFacade;
  }

  public UserTrip prepareCreate() {
    selected = new UserTrip();
    initializeEmbeddableKey();
    return selected;
  }

  public String create() {
    User signedInUser = (User) Methods.sessionMap().get("user");
    selected.setUserId(signedInUser);
    persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("UserTripCreated"));
    if (!JsfUtil.isValidationFailed()) {
      items = null;    // Invalidate list of items to trigger re-query.
      return "/userTrip/ListTrips?faces-redirect=true";
    }
    return "";
  }

  public void update() {
    persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("UserTripUpdated"));
  }

  public void destroy() {
    persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("UserTripDeleted"));
    if (!JsfUtil.isValidationFailed()) {
      selected = null; // Remove selection
      items = null;    // Invalidate list of items to trigger re-query.
    }
  }

  public List<UserTrip> getItems() {
    if (items == null) {
      User signedInUser = (User) Methods.sessionMap().get("user");
      items = getFacade().findTripsByUserPrimaryKey(signedInUser.getId());
    }
    return items;
  }

  private void persist(PersistAction persistAction, String successMessage) {
    if (selected != null) {
      setEmbeddableKeys();
      try {
        if (persistAction != PersistAction.DELETE) {
          getFacade().edit(selected);
        } else {
          getFacade().remove(selected);
        }
        JsfUtil.addSuccessMessage(successMessage);
      } catch (EJBException ex) {
        String msg = "";
        Throwable cause = ex.getCause();
        if (cause != null) {
          msg = cause.getLocalizedMessage();
        }
        if (msg.length() > 0) {
          JsfUtil.addErrorMessage(msg);
        } else {
          JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
      } catch (Exception ex) {
        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
      }
    }
  }

  public UserTrip getUserTrip(java.lang.Integer id) {
    return getFacade().find(id);
  }
}
