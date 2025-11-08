package org.dmgarcia.app.security;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.dmgarcia.app.infra.JPAUtil;
import org.dmgarcia.app.model.Category;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class CategoryRepository extends BaseRepository {

    public Optional<Category> find(Integer id) {
        EntityManager em = JPAUtil.getEMF().createEntityManager();
        try {
            TypedQuery<Category> q = em.createQuery(
                    "SELECT c FROM Category c " +
                            "LEFT JOIN FETCH c.parent " +
                            "WHERE c.id = :id", Category.class);
            q.setParameter("id", id);

            Category cat;
            try {
                cat = q.getSingleResult();
            } catch (NoResultException e) {
                cat = null;
            }

            return Optional.ofNullable(cat);
        } finally {
            em.close();
        }
    }

    public List<Category> listActive() {
        EntityManager em = JPAUtil.getEMF().createEntityManager();
        try {
            return em.createQuery(
                    "select r from Category r where r.deletedAt is null order by r.id",
                    Category.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Category save(Category r) {
        EntityManager em = JPAUtil.getEMF().createEntityManager();
        return inTx(em, e -> e.merge(r));
    }

    public List<Category> findRootCategories(){
        try (EntityManager em = JPAUtil.getEMF().createEntityManager()) {
            return em.createQuery(
                    "SELECT c FROM Category c WHERE c.parent IS NULL ORDER BY c.code",
                    Category.class
            ).getResultList();
        }
    }

    public void softDelete(String code) {
        EntityManager em = JPAUtil.getEMF().createEntityManager();
        inTx(em, e -> {
            Category r = e.find(Category.class, code);
            if (r != null && r.getDeletedAt() == null) {
                r.setDeletedAt(LocalDateTime.now());
            }
            return null;
        });
    }
}