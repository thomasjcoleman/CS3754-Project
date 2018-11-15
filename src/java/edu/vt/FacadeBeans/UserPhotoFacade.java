/*
 * Created by Thomas Coleman on 2018.10.30
 * Copyright © 2018 Thomas Coleman. All rights reserved.
 */
package edu.vt.FacadeBeans;

import edu.vt.EntityBeans.UserPhoto;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class UserPhotoFacade extends AbstractFacade<UserPhoto> {

  @PersistenceContext(unitName = "Trailblazer-Team8PU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  // Constructor, which just routes to the constructor for a user photo.
  public UserPhotoFacade() {
    super(UserPhoto.class);
  }

  /**
   * @param primaryKey is the Primary Key of the User entity in a table row in
   * the database.
   * @return a list of photos associated with the User whose primary key is
   * primaryKey
   */
  public List<UserPhoto> findPhotosByUserPrimaryKey(Integer primaryKey) {
    return (List<UserPhoto>) em.createNamedQuery("UserPhoto.findPhotosByUserDatabasePrimaryKey")
            .setParameter("primaryKey", primaryKey)
            .getResultList();
  }
}
