/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.vt.FacadeBeans;

import edu.vt.EntityBeans.UserFile;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author tcole
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
  
}
