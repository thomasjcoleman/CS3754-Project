/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

/**
 *
 * @author
 */
@Entity
@Table(name = "UserFile")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "UserFile.findAll", query = "SELECT u FROM UserFile u")
  , @NamedQuery(name = "UserFile.findById", query = "SELECT u FROM UserFile u WHERE u.id = :id")
  , @NamedQuery(name = "UserFile.findByFilename", query = "SELECT u FROM UserFile u WHERE u.filename = :filename")
  , @NamedQuery(name = "UserFile.findUserFilesByUserId", query = "SELECT u FROM UserFile u WHERE u.userId.id = :userId")
})
public class UserFile implements Serializable {

  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "id")
  private Integer id;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 256)
  @Column(name = "filename")
  private String filename;
  @JoinColumn(name = "trip_id", referencedColumnName = "id")
  @ManyToOne
  private User tripId;
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  @ManyToOne
  private User userId;

  public UserFile() {
  }

  public UserFile(Integer id) {
    this.id = id;
  }

  public UserFile(Integer id, String filename) {
    this.id = id;
    this.filename = filename;
  }
  
  public UserFile(String filename, User id) {
        this.filename = filename;
        userId = id;
    }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public User getTripId() {
    return tripId;
  }

  public void setTripId(User tripId) {
    this.tripId = tripId;
  }

  public User getUserId() {
    return userId;
  }

  public void setUserId(User userId) {
    this.userId = userId;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (id != null ? id.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof UserFile)) {
      return false;
    }
    UserFile other = (UserFile) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "edu.vt.EntityBeans.UserFile[ id=" + id + " ]";
  }
  
  public String getFilePath() {
        return Constants.FILES_ABSOLUTE_PATH + getFilename();
    }
  
}
