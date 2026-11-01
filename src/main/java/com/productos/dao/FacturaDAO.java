
package com.productos.dao;

import com.productos.entities.Cliente;
import com.productos.entities.Factura;
import com.productos.persistence.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;

public class FacturaDAO {

    // Registrar una factura
    public void guardar(Factura factura) {
        EntityManager em = JPAUtil.getEntityManagerFactory()
                                  .createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Cliente cliente = em.getReference(
                Cliente.class,
                factura.getCliente().getId()
            );

            factura.setCliente(cliente);
            em.persist(factura);

            tx.commit();

        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    // Consultar todas las facturas mediante JPQL
    public List<Factura> listar() {
        EntityManager em = JPAUtil.getEntityManagerFactory()
                                  .createEntityManager();

        try {
            return em.createQuery(
                "SELECT f FROM Factura f "
                + "JOIN FETCH f.cliente "
                + "ORDER BY f.id",
                Factura.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

    // Buscar factura por ID
    public Factura buscarPorId(Integer id) {
        EntityManager em = JPAUtil.getEntityManagerFactory()
                                  .createEntityManager();

        try {
            return em.createQuery(
                "SELECT f FROM Factura f "
                + "JOIN FETCH f.cliente "
                + "WHERE f.id = :id",
                Factura.class
            )
            .setParameter("id", id)
            .getResultStream()
            .findFirst()
            .orElse(null);

        } finally {
            em.close();
        }
    }

    // Consultar facturas de un cliente
    public List<Factura> listarPorCliente(Integer idCliente) {
        EntityManager em = JPAUtil.getEntityManagerFactory()
                                  .createEntityManager();

        try {
            return em.createQuery(
                "SELECT f FROM Factura f "
                + "JOIN FETCH f.cliente "
                + "WHERE f.cliente.id = :idCliente "
                + "ORDER BY f.id",
                Factura.class
            )
            .setParameter("idCliente", idCliente)
            .getResultList();

        } finally {
            em.close();
        }
    }

    // Actualizar factura
    public boolean actualizar(Factura factura) {
        EntityManager em = JPAUtil.getEntityManagerFactory()
                                  .createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Factura existente =
                    em.find(Factura.class, factura.getId());

            if (existente == null) {
                tx.rollback();
                return false;
            }

            Cliente cliente = em.getReference(
                Cliente.class,
                factura.getCliente().getId()
            );

            existente.setFecha(factura.getFecha());
            existente.setTotal(factura.getTotal());
            existente.setCliente(cliente);

            tx.commit();
            return true;

        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    // Eliminar factura por ID
    public boolean eliminar(Integer id) {
        EntityManager em = JPAUtil.getEntityManagerFactory()
                                  .createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Factura factura = em.find(Factura.class, id);

            if (factura == null) {
                tx.rollback();
                return false;
            }

            em.remove(factura);
            tx.commit();
            return true;

        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
