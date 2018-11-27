/*
 * Created by Christian Dufrois on 2018.11.24
 * Copyright © 2018 Christian Dufrois. All rights reserved.
 */
package edu.vt.EntityBeans;

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
 * @author tcole
 */
@Entity
@Table(name = "UserInterests")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "UserInterests.findAll", query = "SELECT u FROM UserInterests u")
  , @NamedQuery(name = "UserInterests.findById", query = "SELECT u FROM UserInterests u WHERE u.id = :id")
  , @NamedQuery(name = "UserInterests.findByTrailname", query = "SELECT u FROM UserInterests u WHERE u.trailname = :trailname")
  , @NamedQuery(name = "UserInterests.findByInterested", query = "SELECT u FROM UserInterests u WHERE u.interested = :interested")
  , @NamedQuery(name = "UserInterests.findByCompleted", query = "SELECT u FROM UserInterests u WHERE u.completed = :completed")
  , @NamedQuery(name = "UserInterests.findByUserIdAndCompleted", query = "SELECT u FROM UserInterests u WHERE u.completed = :completed AND u.userId.id = :key")
  , @NamedQuery(name = "UserInterests.findByUserIdAndInterested", query = "SELECT u FROM UserInterests u WHERE u.interested = :interested AND u.userId.id = :key")
})
public class UserInterests implements Serializable {

  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "id")
  private Integer id;
  
  @Basic(optional = false)
  @NotNull
  @Column(name = "trail_id")
  private Integer trailId;
  
  @Column(name = "interested")
  private Boolean interested;
  
  @Column(name = "completed")
  private Boolean completed;
  
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  @ManyToOne
  private User userId;

  public UserInterests() {
  }

  public UserInterests(Integer id) {
    this.id = id;
  }

  public UserInterests(Integer id, Integer trailId) {
    this.id = id;
    this.trailId = trailId;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getTrailId() {
    return trailId;
  }

  public void setTrailId(Integer trailId) {
    this.trailId = trailId;
  }

  public Boolean getInterested() {
    return interested;
  }

  public void setInterested(Boolean interested) {
    this.interested = interested;
  }

  public Boolean getCompleted() {
    return completed;
  }

  public void setCompleted(Boolean completed) {
    this.completed = completed;
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
    if (!(object instanceof UserInterests)) {
      return false;
    }
    UserInterests other = (UserInterests) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "edu.vt.EntityBeans.UserInterests[ id=" + id + " ]";
  }
  
}
