/*
 * Created by Christian Dufrois on 2018.11.24
 * Copyright © 2018 Christian Dufrois. All rights reserved.
 */
package edu.vt.FacadeBeans;

import edu.vt.EntityBeans.UserInterests;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author tcole
 */
@Stateless
public class UserInterestsFacade extends AbstractFacade<UserInterests> {

    @PersistenceContext(unitName = "Trailblazer-Team8PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UserInterestsFacade() {
        super(UserInterests.class);
    }

    /**
     * Gets a list of trails that the given user did or didn't complete
     *
     * @param completed Get trails user completed or not
     * @param userId The trails for this user
     * @return list of trails
     */
    public List<UserInterests> findCompleted(Boolean completed, Integer userId) {
        List<UserInterests> completedTrails = em.createNamedQuery("UserInterests.findByUserIdAndCompleted")
                .setParameter("completed", completed)
                .setParameter("key", userId)
                .getResultList();

        return completedTrails;
    }

    /**
     * Gets a list of trails that the given user is or isn't interested in
     *
     * @param interested Get trails user is or isn't interested
     * @param userId The trails for this user
     * @return list of trails
     */
    public List<UserInterests> findInterested(Boolean interested, Integer userId) {
        List<UserInterests> interestedTrails = em.createNamedQuery("UserInterests.findByUserIdAndInterested")
                .setParameter("interested", interested)
                .setParameter("key", userId)
                .getResultList();

        return interestedTrails;
    }

}
