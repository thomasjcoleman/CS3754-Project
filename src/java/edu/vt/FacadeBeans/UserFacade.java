/*
 * Created by Thomas Coleman 2018.06.15
 * Copyright © 2018 Thomas Coleman. All rights reserved.
 */
package edu.vt.FacadeBeans;

import edu.vt.EntityBeans.User;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class UserFacade extends AbstractFacade<User> {

  @PersistenceContext(unitName = "Trailblazer-Team8PU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  // Constructor, which just routes to the constructor for a user.
  public UserFacade() {
    super(User.class);
  }

  /**
   * @param id is the Primary Key of the User entity in a table row in the
   * database.
   * @return object reference of the User object whose primary key is id
   */
  public User getUser(int id) {
    return em.find(User.class, id);
  }

  /**
   * @param username is the username attribute (column) value of the user
   * @return object reference of the User entity whose user name is username
   */
  public User findByUsername(String username) {
    if (em.createQuery("SELECT c FROM User c WHERE c.username = :username")
            .setParameter("username", username)
            .getResultList().isEmpty()) {
      return null;
    } else {
      return (User) (em.createQuery("SELECT c FROM User c WHERE c.username = :username")
              .setParameter("username", username)
              .getSingleResult());
    }
  }

  /**
   * @param googleId is is the google ID attribute of the user to find
   * @return object reference of the User entity whose user name is username
   */
  public User findByGoogleId(String googleId) {
    if (em.createQuery("SELECT c FROM User c WHERE c.googleId = :googleId")
            .setParameter("googleId", googleId)
            .getResultList().isEmpty()) {
      return null;
    } else {
      return (User) (em.createQuery("SELECT c FROM User c WHERE c.googleId = :googleId")
              .setParameter("googleId", googleId)
              .getSingleResult());
    }
  }

  /**
   * Deletes the User entity whose primary key is id
   *
   * @param id is the Primary Key of the User entity in a table row in the
   * database.
   */
  public void deleteUser(int id) {
    User user = em.find(User.class, id);
    em.remove(user);
  }

}
