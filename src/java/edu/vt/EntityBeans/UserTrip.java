/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.vt.EntityBeans;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author tcole
 */
@Entity
@Table(name = "UserTrip")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "UserTrip.findAll", query = "SELECT u FROM UserTrip u")
  , @NamedQuery(name = "UserTrip.findById", query = "SELECT u FROM UserTrip u WHERE u.id = :id")
  , @NamedQuery(name = "UserTrip.findByTrailId", query = "SELECT u FROM UserTrip u WHERE u.trailId = :trailId")
  , @NamedQuery(name = "UserSurvey.findByTripDate", query = "SELECT u FROM UserTrip u WHERE u.tripDate = :tripDate")})
public class UserTrip implements Serializable {

  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "id")
  private Integer id;
  @Basic(optional = false)
  @Column(name = "trail_id")
  private Integer trailId;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 256)
  @Column(name = "trip_name")
  private String tripName;
  @Basic(optional = false)
  @NotNull
  @Column(name = "trip_date")
  @Temporal(TemporalType.DATE)
  private Date tripDate;

  public UserTrip() {
  }

  public UserTrip(Integer id) {
    this.id = id;
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

  public String getTripName() {
    return tripName;
  }

  public void setTripName(String tripName) {
    this.tripName = tripName;
  }
  
  public Date getTripDate() {
    return tripDate;
  }

  public void setTripDate(Date tripDate) {
    this.tripDate = tripDate;
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
    if (!(object instanceof UserTrip)) {
      return false;
    }
    UserTrip other = (UserTrip) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "edu.vt.EntityBeans.UserTrip[ id=" + id + " ]";
  }
  
}
