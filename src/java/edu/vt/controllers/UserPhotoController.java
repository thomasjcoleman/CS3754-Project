/*
 * Created by Thomas Coleman on 2018.10.30
 * Copyright © 2018 Thomas Coleman. All rights reserved. 
 */
package edu.vt.controllers;

import edu.vt.EntityBeans.User;
import edu.vt.EntityBeans.UserPhoto;
import edu.vt.FacadeBeans.UserFacade;
import edu.vt.FacadeBeans.UserPhotoFacade;
import edu.vt.globals.Constants;
import edu.vt.globals.Methods;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.primefaces.model.UploadedFile;

@Named("userPhotoController")
@SessionScoped

public class UserPhotoController implements Serializable {

  /*-------------------
    Instance variables
   -------------------*/
  private String filename;
  private UploadedFile file;

  @EJB
  private UserFacade userFacade;
  @EJB
  private UserPhotoFacade userPhotoFacade;

  /*--------------------
    Getters and Setters
   --------------------*/
  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public UploadedFile getFile() {
    return file;
  }

  public void setFile(UploadedFile file) {
    this.file = file;
  }

  public UserFacade getUserFacade() {
    return userFacade;
  }

  public UserPhotoFacade getUserPhotoFacade() {
    return userPhotoFacade;
  }

  /*------------------
     Instance Methods
    -----------------*/
  // Upload a photo
  public String upload() {
    Methods.preserveMessages();

    // Make sure a file is selected
    if (file.getSize() == 0) {
      Methods.showMessage("Information", "No File Selected!",
              "You need to choose a file first before clicking Upload.");
      return "";
    }

    // Make sure it's an image
    String mimeFileType = file.getContentType();
    if (mimeFileType.startsWith("image/")) {
      String fileExtension = mimeFileType.subSequence(6, mimeFileType.length()).toString();
      String fileExtensionInCaps = fileExtension.toUpperCase();
      switch (fileExtensionInCaps) {
        case "JPG":
        case "JPEG":
        case "PNG":
        case "GIF":
          // File is an acceptable image type
          break;
        default:
          Methods.showMessage("Fatal Error", "Unrecognized File Type!",
                  "Selected file type is not a JPG, JPEG, PNG, or GIF!");
          return "";
      }
    } else {
      Methods.showMessage("Fatal Error", "Unrecognized File Type!",
              "Selected file type is not a JPG, JPEG, PNG, or GIF!");
      return "";
    }

    // store it to the database and redirect to the user's profile.
    storePhotoFile(file);
    return "/userAccount/Profile?faces-redirect=true";
  }

  // Store uploaded photo and thumbnail
  public String storePhotoFile(UploadedFile file) {
    Methods.preserveMessages();

    try {
      // delete old photo if it exists
      deletePhoto();
      User signedInUser = (User) Methods.sessionMap().get("user");

      // get image type file extension, or default to png if none found
      String mimeFileType = file.getContentType();
      String fileExtension = mimeFileType.startsWith("image/") ? mimeFileType.subSequence(6, mimeFileType.length()).toString() : "png";

      // add the new photo to the database
      UserPhoto newPhoto = new UserPhoto(fileExtension, signedInUser);
      getUserPhotoFacade().create(newPhoto);
      UserPhoto photo = getUserPhotoFacade().findPhotosByUserPrimaryKey(signedInUser.getId()).get(0);
      InputStream inputStream = file.getInputstream();
      File uploadedFile = inputStreamToFile(inputStream, photo.getPhotoFilename());

      // save the thumbnail version of the uploaded file
      saveThumbnail(uploadedFile, photo);

    } catch (IOException ex) {
      Methods.showMessage("Fatal Error", "Something went wrong while storing the user's photo file!",
              "See: " + ex.getMessage());
    }

    // redirect to show the Profile page
    Methods.showMessage("Information", "Success!", "User's Photo File is Successfully Uploaded!");
    return "/userAccount/Profile?faces-redirect=true";
  }

  /**
   * Write a given input stream to a file.
   *
   * @param inputStream of bytes to be written into file with name
   * targetFilename
   * @param targetFilename
   * @return the created file targetFile
   * @throws IOException
   */
  private File inputStreamToFile(InputStream inputStream, String targetFilename) throws IOException {
    File targetFile = null;

    try {
      // write data into a buffer...
      byte[] buffer = new byte[inputStream.available()];
      inputStream.read(buffer);

      //... and output it to the file
      targetFile = new File(Constants.PHOTOS_ABSOLUTE_PATH, targetFilename);
      OutputStream outStream = new FileOutputStream(targetFile);
      outStream.write(buffer);
      outStream.close();

    } catch (IOException ex) {
      Methods.showMessage("Fatal Error", "Something went wrong in input stream to file!",
              "See: " + ex.getMessage());
    }

    return targetFile;
  }

  // Store thumbnail image
  private void saveThumbnail(File inputFile, UserPhoto inputPhoto) {
    try {
      // scale the input photo down...
      BufferedImage uploadedPhoto = ImageIO.read(inputFile);
      BufferedImage thumbnailPhoto = Scalr.resize(uploadedPhoto, Constants.THUMBNAIL_SIZE);

      // ... and output it to a file
      File thumbnailPhotoFile = new File(Constants.PHOTOS_ABSOLUTE_PATH, inputPhoto.getThumbnailFileName());
      ImageIO.write(thumbnailPhoto, inputPhoto.getExtension(), thumbnailPhotoFile);

    } catch (IOException ex) {
      Methods.showMessage("Fatal Error", "Something went wrong while saving the thumbnail file!",
              "See: " + ex.getMessage());
    }
  }

  // Delete a user's photo and thumbnail.
  public void deletePhoto() {
    User signedInUser = (User) Methods.sessionMap().get("user");
    Integer userKey = signedInUser.getId();

    // obtain all the user's photos
    List<UserPhoto> photoList = getUserPhotoFacade().findPhotosByUserPrimaryKey(userKey);

    // if the user uploaded a photo...
    if (!photoList.isEmpty()) {
      // ...find the first one...
      UserPhoto photo = photoList.get(0);

      try {
        // ...and try to delete it.
        Files.deleteIfExists(Paths.get(photo.getPhotoFilePath()));
        Files.deleteIfExists(Paths.get(photo.getThumbnailFilePath()));
        getUserPhotoFacade().remove(photo);
      } catch (IOException ex) {
        Methods.showMessage("Fatal Error",
                "Something went wrong while deleting the user photo file!",
                "See: " + ex.getMessage());
      }
    }
  }
}
