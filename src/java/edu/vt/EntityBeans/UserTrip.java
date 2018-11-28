/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.vt.EntityBeans;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
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
  , @NamedQuery(name = "UserTrip.findByTrailId", query = "SELECT u FROM UserTrip u WHERE u.trailId = :trailId")})
public class UserTrip implements Serializable {

  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "id")
  private Integer id;
  @Column(name = "trail_id")
  private Integer trailId;

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