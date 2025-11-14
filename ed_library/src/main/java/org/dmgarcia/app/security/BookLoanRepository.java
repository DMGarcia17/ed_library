package org.dmgarcia.app.security;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.dmgarcia.app.infra.JPAUtil;
import org.dmgarcia.app.model.BookLoan;
import org.dmgarcia.app.model.User;

import java.time.LocalDate;
import java.util.List;

public class BookLoanRepository {

    private final EntityManager em;

    public BookLoanRepository() {
        this.em = JPAUtil.getEMF().createEntityManager(); // usa tu util de JPA
    }

    public List<BookLoan> findAll() {
        return em.createQuery("""
                SELECT bl FROM BookLoan bl
                LEFT JOIN FETCH bl.user LEFT JOIN FETCH bl.book
                ORDER BY bl.loanDate DESC
                """, BookLoan.class)
                .getResultList();
    }

    // préstamos “vigentes”: no devueltos, no cancelados y fecha de préstamo <= hoy
    public List<BookLoan> findActive() {
        return em.createQuery("""
                SELECT bl FROM BookLoan bl
                LEFT JOIN FETCH bl.user LEFT JOIN FETCH bl.book
                WHERE bl.returned = false
                  AND bl.canceled = false
                  AND bl.loanDate <= :today
                ORDER BY bl.loanDate DESC
                """, BookLoan.class)
                .setParameter("today", LocalDate.now())
                .getResultList();
    }

    public List<BookLoan> findReturned() {
        return em.createQuery("""
                SELECT bl FROM BookLoan bl
                LEFT JOIN FETCH bl.user LEFT JOIN FETCH bl.book
                WHERE (bl.returned = true
                       AND bl.canceled = false)
                  OR (bl.returned = false
                      AND bl.canceled = true)
                ORDER BY bl.returnDate DESC
                """, BookLoan.class)
                .getResultList();
    }

    // filtrar por username
    public List<BookLoan> findByUser(String username) {
        return em.createQuery("""
                SELECT bl FROM BookLoan bl
                LEFT JOIN FETCH bl.user LEFT JOIN FETCH bl.book
                WHERE bl.canceled = false
                  AND bl.user.username LIKE :u
                ORDER BY bl.loanDate DESC
                """, BookLoan.class)
                .setParameter("u", "%" + username + "%")
                .getResultList();
    }

    // filtrar por título del libro
    public List<BookLoan> findByBookTitle(String title) {
        return em.createQuery("""
                SELECT bl FROM BookLoan bl
                LEFT JOIN FETCH bl.user LEFT JOIN FETCH bl.book
                WHERE LOWER(bl.book.title) LIKE LOWER(:t)
                ORDER BY bl.loanDate DESC
                """, BookLoan.class)
                .setParameter("t", "%" + title + "%")
                .getResultList();
    }

    public BookLoan findById(Integer id) {
        return em.find(BookLoan.class, id);
    }

    public void save(BookLoan loan) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(loan);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public void update(BookLoan loan) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(loan);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public void markReturned(Integer id) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            BookLoan loan = em.find(BookLoan.class, id);
            if (loan != null && !loan.isCanceled()) {
                loan.setReturned(true);
                loan.setReturnDate(LocalDate.now());
                em.merge(loan);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public void cancel(Integer id) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            BookLoan loan = em.find(BookLoan.class, id);
            if (loan != null) {
                loan.setCanceled(true);
                em.merge(loan);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public List<BookLoan> findAll(User username) {
        return em.createQuery("""
                SELECT bl FROM BookLoan bl
                LEFT JOIN FETCH bl.user LEFT JOIN FETCH bl.book WHERE bl.user=:username
                ORDER BY bl.loanDate DESC
                """, BookLoan.class)
                .setParameter("username", username)
                .getResultList();
    }

    public List<BookLoan> findReturned(User username) {
        return em.createQuery("""
                SELECT bl FROM BookLoan bl
                LEFT JOIN FETCH bl.user LEFT JOIN FETCH bl.book
                WHERE (bl.returned = true
                       AND bl.canceled = false)
                  OR (bl.returned = false
                      AND bl.canceled = true)
                  AND bl.user = :username
                ORDER BY bl.returnDate DESC
                """, BookLoan.class)
                .setParameter("username", username)
                .getResultList();
    }

    public List<BookLoan> findActive(User username) {
        return em.createQuery("""
                SELECT bl FROM BookLoan bl
                LEFT JOIN FETCH bl.user LEFT JOIN FETCH bl.book
                WHERE bl.user = :username
                  AND bl.returned = false
                  AND bl.canceled = false
                  AND bl.loanDate <= :today
                ORDER BY bl.loanDate DESC
                """, BookLoan.class)
                .setParameter("today", LocalDate.now())
                .setParameter("username", username)
                .getResultList();
    }
}
