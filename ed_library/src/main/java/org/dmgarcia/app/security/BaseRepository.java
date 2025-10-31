package org.dmgarcia.app.security;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.function.Function;

public class BaseRepository {
    protected <T> T inTx(EntityManager em, Function<EntityManager, T> work){
        EntityTransaction tx = em.getTransaction();
        try{
            tx.begin();
            T out = work.apply(em);
            tx.commit();
            return out;
        } catch (RuntimeException ex){
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }
}
