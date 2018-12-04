package edu.vt.controllers;

import com.drew.imaging.ImageMetadataReader;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDirectory;
import edu.vt.EntityBeans.User;
import edu.vt.EntityBeans.UserFile;
import edu.vt.FacadeBeans.UserFacade;
import edu.vt.controllers.util.JsfUtil;
import edu.vt.controllers.util.JsfUtil.PersistAction;
import edu.vt.FacadeBeans.UserFileFacade;
import edu.vt.globals.Constants;
import edu.vt.globals.Methods;
import java.io.File;
import java.io.IOException;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
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

@Named("userFileController")
@SessionScoped
public class UserFileController implements Serializable {

  @EJB
  private UserFacade userFacade;

  @EJB
  private UserFileFacade userFileFacade;
  private List<UserFile> items = null;
  private UserFile selected;
  private String selectedRowNumber = "0";
  HashMap<Integer, String> cleanedFileNameHashMap = null;

  private int tripId;

  public UserFileController() {
  }

  public UserFile getSelected() {
    return selected;
  }

  public void setSelected(UserFile selected) {
    this.selected = selected;
  }

  public String getSelectedRowNumber() {
    return selectedRowNumber;
  }

  public void setSelectedRowNumber(String selectedRowNumber) {
    this.selectedRowNumber = selectedRowNumber;
  }

  public UserFacade getUserFacade() {
    return userFacade;
  }

  public void setUserFacade(UserFacade userFacade) {
    this.userFacade = userFacade;
  }

  public UserFileFacade getUserFileFacade() {
    return userFileFacade;
  }

  public void setUserFileFacade(UserFileFacade userFileFacade) {
    this.userFileFacade = userFileFacade;
  }

  public int getTripId() {
    return tripId;
  }

  public void setTripId(int tripId) {
    this.tripId = tripId;
  }

  public UserFile prepareCreate() {
    selected = new UserFile();
    return selected;
  }

  public void create() {
    persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("UserFileCreated"));
    if (!JsfUtil.isValidationFailed()) {
      selected = null;
      items = null;
    }
  }

  public void update() {
    persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("UserFileUpdated"));
  }

  public void destroy() {
    persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("UserFileDeleted"));
    if (!JsfUtil.isValidationFailed()) {
      selected = null; // Remove selection
      items = null;    // Invalidate list of items to trigger re-query.
    }
  }

  public List<UserFile> getItems() {

    // Obtain only those files from the database that belong to the signed-in user
    items = getUserFileFacade().findUserFilesByTripId(tripId);

    // Instantiate a new hash map object
    cleanedFileNameHashMap = new HashMap<>();

    // Loop over a list with lambda
    items.forEach(userFile -> {

      String storedFileName = userFile.getFilename();

      // Remove the "userId_" (e.g., "4_") prefix in the stored filename
      String cleanedFileName = storedFileName.substring(storedFileName.indexOf("_") + 1);

      // Obtain the file database Primary Key id
      Integer fileId = userFile.getId();

      // Create an entry in the hash map as a key-value pair
      cleanedFileNameHashMap.put(fileId, cleanedFileName);
    });
    return items;
  }

  /**
   * @param persistAction refers to CREATE, UPDATE (Edit) or DELETE action
   * @param successMessage displayed to inform the user about the result
   */
  private void persist(PersistAction persistAction, String successMessage) {

    if (selected != null) {
      try {
        if (persistAction != PersistAction.DELETE) {
          /*
                     -------------------------------------------------
                     Perform CREATE or EDIT operation in the database.
                     -------------------------------------------------
                     The edit(selected) method performs the SAVE (STORE) operation of the "selected"
                     object in the database regardless of whether the object is a newly
                     created object (CREATE) or an edited (updated) object (EDIT or UPDATE).
                    
                     UserFileFacade inherits the edit(selected) method from the AbstractFacade class.
           */
          getUserFileFacade().edit(selected);
        } else {
          /*
                     -----------------------------------------
                     Perform DELETE operation in the database.
                     -----------------------------------------
                     The remove method performs the DELETE operation in the database.
                    
                     UserFileFacade inherits the remove(selected) method from the AbstractFacade class.
           */
          getUserFileFacade().remove(selected);
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
          JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccurred"));
        }
      } catch (Exception ex) {
        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccurred"));
      }
    }
  }

  public UserFile getUserFile(java.lang.Integer id) {
    return getUserFileFacade().find(id);
  }

  public List<UserFile> getItemsAvailableSelectMany() {
    return getUserFileFacade().findAll();
  }

  public List<UserFile> getItemsAvailableSelectOne() {
    return getUserFileFacade().findAll();
  }

  @FacesConverter(forClass = UserFile.class)
  public static class UserFileControllerConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
      if (value == null || value.length() == 0) {
        return null;
      }
      UserFileController controller = (UserFileController) facesContext.getApplication().getELResolver().
              getValue(facesContext.getELContext(), null, "userFileController");
      return controller.getUserFile(getKey(value));
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
      if (object instanceof UserFile) {
        UserFile o = (UserFile) object;
        return getStringKey(o.getId());
      } else {
        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), UserFile.class.getName()});
        return null;
      }
    }

  }

  public String deleteSelectedUserFile() {

    UserFile userFileToDelete = selected;

    /*
        We need to preserve the messages since we will redirect to show a
        different JSF page after successful deletion of the user file.
     */
    Methods.preserveMessages();

    if (userFileToDelete == null) {
      Methods.showMessage("Fatal Error", "No File Selected!", "You do not have a file to delete!");
      return "";
    } else {
      try {
        // Delete the file from CloudStorage/FileStorage
        Files.deleteIfExists(Paths.get(userFileToDelete.getFilePath()));

        // Delete the user file record from the database
        getUserFileFacade().remove(userFileToDelete);
        // UserFileFacade inherits the remove() method from AbstractFacade

        Methods.showMessage("Information", "Success!", "Selected File is Successfully Deleted!");

        // See method below
        refreshFileList();

        return "/userFile/ListTripFiles?faces-redirect=true";

      } catch (IOException ex) {
        Methods.showMessage("Fatal Error", "Something went wrong while deleting the user file!",
                "See: " + ex.getMessage());
        return "";
      }
    }
  }

  public void refreshFileList() {
    /*
        By setting the items to null, we force the getItems
        method above to retrieve all of the user's files again.
     */
    selected = null; // Remove selection
    items = null;    // Invalidate list of items to trigger re-query.
  }

  /**
   *
   * @param fileId database primary key value for a user file
   * @return the file if it is an image file; otherwise return a blank image
   */
  public String imageFile(Integer fileId) {
    UserFile userFile = getUserFileFacade().getUserFile(fileId);
    String imageFileName = userFile.getFilename();

    String fileExtension = imageFileName.substring(imageFileName.lastIndexOf(".") + 1);

    String fileExtensionInCaps = fileExtension.toUpperCase();

    switch (fileExtensionInCaps) {
      case "JPG":
      case "JPEG":
      case "PNG":
      case "GIF":
        return Constants.FILES_RELATIVE_PATH + imageFileName;
      case "MP4":
      case "MOV":
      case "OGG":
      case "WEBM":
        return "/resources/images/videoFile.png";
      default:
        return "/resources/images/viewFile.png";
    }
  }

  public String cleanedFilenameForFileId(Integer fileId) {
    String cleanedFileName = cleanedFileNameHashMap.get(fileId);
    return cleanedFileName;
  }

  public String cleanedFileNameForSelected() {
    Integer fileId = selected.getId();

    // Obtain the cleaned filename for the given fileId
    String cleanedFileName = cleanedFileNameHashMap.get(fileId);

    return cleanedFileName;
  }

  public String selectedFileRelativePath() {
    return Constants.FILES_RELATIVE_PATH + selected.getFilename();
  }

  public boolean isImage() {
    switch (extensionOfSelectedFileInCaps()) {
      case "JPG":
      case "JPEG":
      case "PNG":
      case "GIF":
        return true;
      default:
        return false;
    }
  }

  public boolean isViewable() {

    switch (extensionOfSelectedFileInCaps()) {
      case "CSS":
      case "CSV":
      case "HTML":
      case "JAVA":
      case "PDF":
      case "SQL":
      case "TXT":
        return true;
      default:
        return false;
    }
  }

  public boolean isVideo() {

    String fileExtension = extensionOfSelectedFileInCaps();

    return (fileExtension.equals("MP4") || fileExtension.equals("MOV") || fileExtension.equals("OGG") || fileExtension.equals("WEBM"));
  }

  public String extensionOfSelectedFileInCaps() {
    String userFileName = selected.getFilename();
    String fileExtension = userFileName.substring(userFileName.lastIndexOf(".") + 1);
    String fileExtensionInCaps = fileExtension.toUpperCase();
    return fileExtensionInCaps;
  }
  
  /**
   * Check if the selected file has geocoding data.
   * @return True if there is geocoding data.
   */
  public boolean fileHasGeocoding() {
    if (selected == null) { return false; }
    String filename = selected.getFilename();
    File imgFile = new File(Constants.FILES_ABSOLUTE_PATH, filename);
    try {
      Metadata metadata = ImageMetadataReader.readMetadata(imgFile);
      Collection<GpsDirectory> gpsDirectories = metadata.getDirectoriesOfType(GpsDirectory.class);
      for (GpsDirectory gpsDirectory : gpsDirectories) {
        // Try to read out the location, making sure it's non-zero
        GeoLocation geoLocation = gpsDirectory.getGeoLocation();
        if (geoLocation != null && !geoLocation.isZero()) {
          // Geocoding data is available
          return true;
        }
      }
    } catch (Exception e) {
    }
    return false;
  }
  
  /**
   * Get geocoding data in JSON format for a file.
   * @return The string containing the geolocation data and related info.
   */
  public String getFileGeocoding() {
    String filename = selected.getFilename();
    File imgFile = new File(Constants.FILES_ABSOLUTE_PATH, filename);
    try {
      Metadata metadata = ImageMetadataReader.readMetadata(imgFile);
      Collection<GpsDirectory> gpsDirectories = metadata.getDirectoriesOfType(GpsDirectory.class);
      for (GpsDirectory gpsDirectory : gpsDirectories) {
        // Try to read out the location, making sure it's non-zero
        GeoLocation geoLocation = gpsDirectory.getGeoLocation();
        if (geoLocation != null && !geoLocation.isZero()) {
          // Add to the output JSON
          return String.format("{filename: '%s', filepath: '%s', lat: %f, lng: %f}",
                  filename, Constants.FILES_RELATIVE_PATH + filename, geoLocation.getLatitude(), geoLocation.getLongitude());
        }
      }
    } catch (Exception e) {
    }
    return "";
  }

}
