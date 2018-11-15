/*
 * Created by Thomas Coleman on 2018.10.30
 * Copyright © 2018 Thomas Coleman. All rights reserved. 
 */
package edu.vt.EntityBeans;

import edu.vt.globals.Constants;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "UserPhoto")

@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "UserPhoto.findPhotosByUserDatabasePrimaryKey", query = "SELECT p FROM UserPhoto p WHERE p.userId.id = :primaryKey")
  , @NamedQuery(name = "UserPhoto.findAll", query = "SELECT u FROM UserPhoto u")
  , @NamedQuery(name = "UserPhoto.findById", query = "SELECT u FROM UserPhoto u WHERE u.id = :id")
  , @NamedQuery(name = "UserPhoto.findByExtension", query = "SELECT u FROM UserPhoto u WHERE u.extension = :extension")
})

public class UserPhoto implements Serializable {

  /*-------------------
    Instance variables
   -------------------*/
  private static final long serialVersionUID = 1L;
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "id")
  private Integer id;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 5)
  @Column(name = "extension")
  private String extension;

  @JoinColumn(name = "user_id", referencedColumnName = "id")
  @ManyToOne
  private User userId;

  /*-------------
    Constructors
   -------------*/
  public UserPhoto() {
  }

  public UserPhoto(Integer id) {
    this.id = id;
  }

  public UserPhoto(Integer id, String extension) {
    this.id = id;
    this.extension = extension;
  }

  // This method is added to the generated code
  public UserPhoto(String fileExtension, User id) {
    this.extension = fileExtension;
    userId = id;
  }

  /*--------------------
    Getters and Setters
   --------------------*/
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getExtension() {
    return extension;
  }

  public void setExtension(String extension) {
    this.extension = extension;
  }

  public User getUserId() {
    return userId;
  }

  public void setUserId(User userId) {
    this.userId = userId;
  }

  /*-----------------
    Instance Methods
   -----------------*/
  /**
   * @return Generates and returns a hash code value for the object with id
   */
  @Override
  public int hashCode() {
    int hash = 0;
    hash += (id != null ? id.hashCode() : 0);
    return hash;
  }

  /**
   * Checks if the UserPhoto object identified by 'object' is the same as the
   * UserPhoto object identified by 'id'
   *
   * @param object The UserPhoto object identified by 'object'
   * @return True if the UserPhoto 'object' and 'id' are the same; otherwise,
   * return False
   */
  @Override
  public boolean equals(Object object) {
    if (!(object instanceof UserPhoto)) {
      return false;
    }
    UserPhoto other = (UserPhoto) object;
    return (this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id));
  }

  @Override
  public String toString() {
    return id.toString();
  }
  
  public String getPhotoFilename() {
    return getUserId() + "." + getExtension();
  }

  public String getThumbnailFileName() {
    return getUserId() + "_thumbnail." + getExtension();
  }

  public String getPhotoFilePath() {
    return Constants.PHOTOS_ABSOLUTE_PATH + getPhotoFilename();
  }

  public String getThumbnailFilePath() {
    return Constants.PHOTOS_ABSOLUTE_PATH + getThumbnailFileName();
  }

}
