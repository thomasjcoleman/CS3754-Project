package edu.vt.managers;

import edu.vt.EntityBeans.User;
import edu.vt.EntityBeans.UserFile;
import edu.vt.FacadeBeans.UserFacade;
import edu.vt.FacadeBeans.UserFileFacade;

import edu.vt.controllers.UserFileController;
import edu.vt.globals.Constants;
import javax.inject.Inject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.model.UploadedFile;
import org.primefaces.event.FileUploadEvent;

@Named(value = "fileUploadManager")
@SessionScoped

public class FileUploadManager implements Serializable {

  /*
    ===============================
    Instance Variables (Properties)
    ===============================
   */
  private UploadedFile uploadedFile;
  @EJB
  private UserFacade userFacade;
  @EJB
  private UserFileFacade userFileFacade;
  @Inject
  private UserFileController userFileController;

  private Integer tripId;

  /*
    =========================
    Getter and Setter Methods
    =========================
   */
  // Returns the uploaded uploadedFile
  public UploadedFile getUploadedFile() {
    return uploadedFile;
  }

  // Obtains the uploaded uploadedFile
  public void setUploadedFile(UploadedFile uploadedFile) {
    this.uploadedFile = uploadedFile;
  }

  public UserFacade getUserFacade() {
    return userFacade;
  }

  public UserFileFacade getUserFileFacade() {
    return userFileFacade;
  }

  public UserFileController getUserFileController() {
    return userFileController;
  }

  public Integer getTripId() {
    return tripId;
  }

  public void setTripId(Integer tripId) {
    this.tripId = tripId;
  }

  /*
    ================
    Instance Methods
    ================
   */
  public String handleFileUpload(FileUploadEvent event) throws IOException {

    try {
      String user_name = (String) FacesContext.getCurrentInstance()
              .getExternalContext().getSessionMap().get("username");

      User user = getUserFacade().findByUsername(user_name);

      // Get the string to use as the file name.
      String userId_filename = user.getId() + "_" + tripId + "_" + event.getFile().getFileName();
      try (InputStream inputStream = event.getFile().getInputstream();) {
        inputStreamToFile(inputStream, userId_filename);
        inputStream.close();
      }

      UserFile newUserFile = new UserFile(userId_filename, user, tripId);

      // If the filename has been used before, replace it.
      List<UserFile> filesFound = getUserFileFacade().findByFilename(userId_filename);
      if (!filesFound.isEmpty()) {
        getUserFileFacade().remove(filesFound.get(0));
      }

      // Create the new UserFile entity (row) in the database
      getUserFileFacade().create(newUserFile);

      // This sets the necessary flag to ensure the messages are preserved.
      FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

      getUserFileController().refreshFileList();

      FacesMessage infoMessage = new FacesMessage(FacesMessage.SEVERITY_INFO,
              "Success!", "File(s) Uploaded Successfully!");
      FacesContext.getCurrentInstance().addMessage(null, infoMessage);

    } catch (IOException e) {
      FacesMessage fatalErrorMessage = new FacesMessage(FacesMessage.SEVERITY_FATAL,
              "Something went wrong during file upload!", "See: " + e.getMessage());
      FacesContext.getCurrentInstance().addMessage(null, fatalErrorMessage);
    }

    return "/userFile/ListTripFiles?faces-redirect=true";
  }

  // Show the File Upload Page
  public String showFileUploadPage() {
    return "/userFile/UploadFile?faces-redirect=true";
  }

  public void upload() throws IOException {

    if (getUploadedFile().getSize() != 0) {
      copyFile(getUploadedFile());
    }
  }

  /**
   * Used to copy an uploadedFile
   *
   * @param file
   * @throws java.io.IOException
   */
  public void copyFile(UploadedFile file) throws IOException {
    FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

    try {
      String user_name = (String) FacesContext.getCurrentInstance()
              .getExternalContext().getSessionMap().get("username");

      User user = getUserFacade().findByUsername(user_name);

      // Form the file string
      String userId_filename = user.getId() + "_"  + tripId + "_" + file.getFileName();
      try (InputStream inputStream = file.getInputstream();) {
        inputStreamToFile(inputStream, userId_filename);
        inputStream.close();
      }

    } catch (IOException e) {
      FacesMessage fatalErrorMessage = new FacesMessage(FacesMessage.SEVERITY_FATAL,
              "Something went wrong during file copy!", "See: " + e.getMessage());
      FacesContext.getCurrentInstance().addMessage(null, fatalErrorMessage);
    }
  }

  private File inputStreamToFile(InputStream inputStream, String file_name)
          throws IOException {

    // Read the series of bytes from the input stream
    byte[] buffer = new byte[inputStream.available()];
    inputStream.read(buffer);

    // Write the series of bytes on uploadedFile.
    File targetFile = new File(Constants.FILES_ABSOLUTE_PATH, file_name);

    OutputStream outStream;
    outStream = new FileOutputStream(targetFile);
    outStream.write(buffer);
    outStream.close();

    return targetFile;
  }

  public void setFileLocation(UserFile data) {
    ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
    ec.getFlash().put("data", data);
  }

  public String getFileLocation() {
    return Constants.FILES_ABSOLUTE_PATH;
  }

  /**
   * Used to return the file extension for a file.
   *
   * @param filename
   * @return
   */
  public static String getExtension(String filename) {

    if (filename == null) {
      return null;
    }
    int extensionPos = filename.lastIndexOf('.');

    int lastUnixPos = filename.lastIndexOf('/');
    int lastWindowsPos = filename.lastIndexOf('\\');
    int lastSeparator = Math.max(lastUnixPos, lastWindowsPos);
    int index = lastSeparator > extensionPos ? -1 : extensionPos;

    if (index == -1) {
      return "";
    } else {
      return filename.substring(index + 1);
    }
  }

}
