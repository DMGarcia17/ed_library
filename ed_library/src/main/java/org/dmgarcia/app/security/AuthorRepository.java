package org.dmgarcia.app.security;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.dmgarcia.app.infra.JPAUtil;
import org.dmgarcia.app.model.Author;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class AuthorRepository extends BaseRepository {

    public Optional<Author> find(Integer id) {
        try (EntityManager em = JPAUtil.getEMF().createEntityManager()) {
            TypedQuery<Author> q = em.createQuery(
                    "SELECT c FROM Author c " +
                            "WHERE c.idAuthor = :id", Author.class);
            q.setParameter("id", id);

            Author cat;
            try {
                cat = q.getSingleResult();
            } catch (NoResultException e) {
                cat = null;
            }

            return Optional.ofNullable(cat);
        }
    }

    public List<Author> listActive() {
        EntityManager em = JPAUtil.getEMF().createEntityManager();
        try {
            return em.createQuery(
                    "select r from Author r where r.deletedAt is null order by r.idAuthor",
                    Author.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Author save(Author r) {
        EntityManager em = JPAUtil.getEMF().createEntityManager();
        return inTx(em, e -> e.merge(r));
    }

    public void softDelete(String code) {
        EntityManager em = JPAUtil.getEMF().createEntityManager();
        inTx(em, e -> {
            Author r = e.find(Author.class, code);
            if (r != null && r.getDeletedAt() == null) {
                r.setDeletedAt(LocalDateTime.now());
            }
            return null;
        });
    }
}