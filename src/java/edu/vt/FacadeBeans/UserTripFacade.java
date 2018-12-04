/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.vt.FacadeBeans;

import edu.vt.EntityBeans.UserTrip;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author tcole
 */
@Stateless
public class UserTripFacade extends AbstractFacade<UserTrip> {

  @PersistenceContext(unitName = "Trailblazer-Team8PU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public UserTripFacade() {
    super(UserTrip.class);
  }
  
  /**
   * @param primaryKey is the Primary Key of the User entity in a table row in
   * the database.
   * @return a list of trips associated with the User whose primary key is
   * primaryKey
   */
  public List<UserTrip> findTripsByUserPrimaryKey(Integer primaryKey) {
    return (List<UserTrip>) em.createNamedQuery("UserTrip.findTripsByUserDatabasePrimaryKey")
            .setParameter("primaryKey", primaryKey)
            .getResultList();
  }
}
