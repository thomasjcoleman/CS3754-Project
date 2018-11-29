/*
 * Created by Christian Dufrois on 2018.11.24
 * Copyright © 2018 Christian Dufrois. All rights reserved.
 */
package edu.vt.controllers;

import edu.vt.EntityBeans.Trail;
import edu.vt.EntityBeans.UserInterests;
import edu.vt.controllers.util.JsfUtil;
import edu.vt.controllers.util.JsfUtil.PersistAction;
import edu.vt.FacadeBeans.UserInterestsFacade;
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

@Named("userInterestsController")
@SessionScoped
public class UserInterestsController implements Serializable {

    @EJB
    private edu.vt.FacadeBeans.UserInterestsFacade ejbFacade;

    private UserInterests selected;
    private UserInterests newInterest;

    /*
   * Manage the trails that are completed
     */
    private List<UserInterests> completedTrails = null;

    /*
   * Manage the trails that are completed
     */
    private List<UserInterests> interestedTrails = null;

    public UserInterestsController() {
    }

    public UserInterests getSelected() {
        return selected;
    }

    public void setSelected(UserInterests selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private UserInterestsFacade getFacade() {
        return ejbFacade;
    }

    /*
     * If these get uncommented, make sure the correct lists are updated
     */
    public UserInterests prepareCreate() {
        newInterest = new UserInterests();
        initializeEmbeddableKey();
        return newInterest;
    }

    public void addInterested(Trail trail) {
        prepareCreate();
        
        newInterest.setInterested(Boolean.TRUE);
        newInterest.setTrailId(trail.getId());
        
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("UserInterestsCreated"));
        if (!JsfUtil.isValidationFailed()) {
            completedTrails = null;    // Invalidate list of items to trigger re-query.
            interestedTrails = null;
        }
    }
    
    public void addCompleted(Trail trail) {
        prepareCreate();
        
        newInterest.setInterested(Boolean.TRUE);
        newInterest.setTrailId(trail.getId());
        
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("UserInterestsCreated"));
        if (!JsfUtil.isValidationFailed()) {
            completedTrails = null;    // Invalidate list of items to trigger re-query.
            interestedTrails = null;
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("UserInterestsUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("UserInterestsDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<UserInterests> getCompletedTrails() {
        if (completedTrails == null) {
            int userPrimaryKey = (int) Methods.sessionMap().get("user_id");
            completedTrails = getFacade().findCompleted(true, userPrimaryKey); // Change to findCompleted
        }
        return completedTrails;
    }

    public List<UserInterests> getInterestedTrails() {
        if (interestedTrails == null) {
            int userPrimaryKey = (int) Methods.sessionMap().get("user_id");
            interestedTrails = getFacade().findInterested(true, userPrimaryKey); // Change to findInterested
        }
        return interestedTrails;
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

    public UserInterests getUserInterests(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<UserInterests> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<UserInterests> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = UserInterests.class)
    public static class UserInterestsControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UserInterestsController controller = (UserInterestsController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "userInterestsController");
            return controller.getUserInterests(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof UserInterests) {
                UserInterests o = (UserInterests) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), UserInterests.class.getName()});
                return null;
            }
        }

    }

}
