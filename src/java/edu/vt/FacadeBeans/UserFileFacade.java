/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.vt.FacadeBeans;

import edu.vt.EntityBeans.UserFile;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author
 */
@Stateless
public class UserFileFacade extends AbstractFacade<UserFile> {

  @PersistenceContext(unitName = "Trailblazer-Team8PU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public UserFileFacade() {
    super(UserFile.class);
  }

  public UserFile getUserFile(int id) {
    return em.find(UserFile.class, id);
  }

  /**
   *
   * @param primaryKey is the Primary Key of the user entity in the database
   * @return a list of object references of userFiles that belong to the user
   * whose DB Primary Key = primaryKey
   */
  public List<UserFile> findUserFilesByUserPrimaryKey(Integer primaryKey) {
    /*
        The following @NamedQuery definition is given in UserFile.java entity class file:
        @NamedQuery(name = "UserFile.findUserFilesByUserId", query = "SELECT u FROM UserFile u WHERE u.userId.id = :userId")
        
        The following statement obtaines the results from the named database query.
     */
    List<UserFile> userFiles = em.createNamedQuery("UserFile.findUserFilesByUserId")
            .setParameter("userId", primaryKey)
            .getResultList();

    return userFiles;
  }

  /**
   *
   * @param tripId is the idea of the trip in the database.
   * @return A list of files for that trip.
   */
  public List<UserFile> findUserFilesByTripId(Integer tripId) {
    List<UserFile> userFiles = em.createNamedQuery("UserFile.findUserFilesByTripId")
            .setParameter("tripId", tripId)
            .getResultList();

    return userFiles;
  }

  /**
   *
   * @param file_name
   * @return a list of object references of userFiles with the name file_name
   */
  public List<UserFile> findByFilename(String file_name) {
    /*
        The following @NamedQuery definition is given in UserFile.java entity class file:
        @NamedQuery(name = "UserFile.findByFilename", query = "SELECT u FROM UserFile u WHERE u.filename = :filename")
        
        The following statement obtaines the results from the named database query.
     */
    List<UserFile> files = em.createNamedQuery("UserFile.findByFilename")
            .setParameter("filename", file_name)
            .getResultList();

    return files;
  }
}
