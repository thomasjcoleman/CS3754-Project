/*
 * Created by Christian Dufrois on 2018.11.24
 * Copyright © 2018 Christian Dufrois. All rights reserved.
 */
package edu.vt.controllers;

import edu.vt.EntityBeans.Trail;
import edu.vt.EntityBeans.UserInterests;
import edu.vt.FacadeBeans.UserFacade;
import edu.vt.controllers.util.JsfUtil;
import edu.vt.controllers.util.JsfUtil.PersistAction;
import edu.vt.FacadeBeans.UserInterestsFacade;
import edu.vt.globals.Methods;

import java.io.Serializable;
import java.text.DecimalFormat;
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
import javax.inject.Inject;

@Named("userInterestsController")
@SessionScoped
public class UserInterestsController implements Serializable {
    @Inject private TrailController tc;

    @EJB
    private edu.vt.FacadeBeans.UserInterestsFacade ejbFacade;

    @EJB
    private UserFacade userFacade;

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
        newInterest.setInterested(Boolean.FALSE);
        newInterest.setCompleted(Boolean.FALSE);
        initializeEmbeddableKey();
        return newInterest;
    }

    public void addInterested(Trail trail) {

        int userPrimaryKey = (int) Methods.sessionMap().get("user_id");
        newInterest = getFacade().findTrail(userPrimaryKey, trail.getId()); // Change to findCompleted

        if (newInterest != null) {
            newInterest.setInterested(Boolean.TRUE);
            persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("UserInterestsUpdated"));
            interestedTrails = null;

        } else {
            prepareCreate();
            newInterest.setInterested(Boolean.TRUE);
            newInterest.setTrailId(trail.getId());
            newInterest.setUserId(userFacade.getUser(userPrimaryKey));

            persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("UserInterestsCreated"));
            if (!JsfUtil.isValidationFailed()) {
                interestedTrails = null;
            }
        }
    }

    public void addCompleted(Trail trail) {
        int userPrimaryKey = (int) Methods.sessionMap().get("user_id");
        newInterest = getFacade().findTrail(userPrimaryKey, trail.getId()); // Change to findCompleted

        if (newInterest != null) {
            newInterest.setCompleted(Boolean.TRUE);
            persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("UserInterestsUpdated"));
            completedTrails = null;

        } else {
            prepareCreate();
            newInterest.setCompleted(Boolean.TRUE);
            newInterest.setTrailId(trail.getId());
            newInterest.setUserId(userFacade.getUser(userPrimaryKey));

            persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("UserInterestsCreated"));
            if (!JsfUtil.isValidationFailed()) {
                completedTrails = null;
            }
        }
    }

    public void removeInterested(Trail trail) {

        int userPrimaryKey = (int) Methods.sessionMap().get("user_id");
        newInterest = getFacade().findTrail(userPrimaryKey, trail.getId()); // Change to findCompleted

        // Do something if there
        if (newInterest != null) {
            newInterest.setInterested(Boolean.FALSE);

            // Remove if both are set to false
            if (!newInterest.getInterested() && !newInterest.getCompleted()) {
                persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("UserInterestsDeleted"));
                if (!JsfUtil.isValidationFailed()) {
                    newInterest = null; // Remove selection
                    interestedTrails = null;    // Invalidate list of items to trigger re-query.
                }
            } // Update if not
            else {
                persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("UserInterestsDeleted"));
                newInterest = null; // Remove selection
                interestedTrails = null;
            }
        }
    }

    public void removeCompleted(Trail trail) {
        int userPrimaryKey = (int) Methods.sessionMap().get("user_id");
        newInterest = getFacade().findTrail(userPrimaryKey, trail.getId()); // Change to findCompleted

        // Do something if there
        if (newInterest != null) {
            newInterest.setCompleted(Boolean.FALSE);

            // Remove if both are set to false
            if (!newInterest.getInterested() && !newInterest.getCompleted()) {
                persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("UserInterestsDeleted"));
                if (!JsfUtil.isValidationFailed()) {
                    newInterest = null; // Remove selection
                    completedTrails = null;    // Invalidate list of items to trigger re-query.
                }
            } // Update if not
            else {
                persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("UserInterestsDeleted"));
                newInterest = null; // Remove selection
                completedTrails = null;
            }
        }
    }

    public boolean trailMarkedInterested(Integer trailId) {
        int userPrimaryKey = (int) Methods.sessionMap().get("user_id");
        UserInterests ui = getFacade().findTrail(userPrimaryKey, trailId); // Change to findCompleted
        if (ui != null) {
            return ui.getInterested();
        }
        return false;
    }

    public boolean trailMarkedCompleted(Integer trailId) {
        int userPrimaryKey = (int) Methods.sessionMap().get("user_id");
        UserInterests ui = getFacade().findTrail(userPrimaryKey, trailId); // Change to findCompleted
        if (ui != null) {
            return ui.getCompleted();
        }
        return false;
    }

    public List<UserInterests> getCompletedTrails() {
        int userPrimaryKey = (int) Methods.sessionMap().get("user_id");
        completedTrails = getFacade().findCompleted(true, userPrimaryKey); // Change to findCompleted
        return completedTrails;
    }

    public List<UserInterests> getInterestedTrails() {
        int userPrimaryKey = (int) Methods.sessionMap().get("user_id");
        interestedTrails = getFacade().findInterested(true, userPrimaryKey); // Change to findInterested
        return interestedTrails;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (newInterest != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(newInterest);
                } else {
                    getFacade().remove(newInterest);
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
    public double getMiles()
    {
        double sum = 0;
        completedTrails = getCompletedTrails();
        if (completedTrails == null) {
            return 0;
        }
        for (int i = 0; i < completedTrails.size(); i++)
        {
            sum += tc.getTrailByID(completedTrails.get(i).getTrailId()).getLength();
        }
        return sum;
    }
    
    public double getAscent()
    {
        double sum = 0;
        completedTrails = getCompletedTrails();
        if (completedTrails == null) {
            return 0;
        }
        for (int i = 0; i < completedTrails.size(); i++)
        {
            sum += tc.getTrailByID(completedTrails.get(i).getTrailId()).getAscentDist();
        }
        return sum;
    }
    
    public String getEverestStat()
    {
        DecimalFormat df = new DecimalFormat("#.###");
        return df.format(getAscent() / 29029);
    }
    
    public int getNumTrails()
    {
        completedTrails = getCompletedTrails(); 
        return completedTrails == null ? 0 : completedTrails.size();
    }
}
