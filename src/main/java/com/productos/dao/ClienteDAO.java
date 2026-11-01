
package com.productos.dao;

import com.productos.entities.Cliente;
import com.productos.persistence.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;

public class ClienteDAO {

    // Registrar un cliente
    public void guardar(Cliente cliente) {
        EntityManager em = JPAUtil.getEntityManagerFactory()
                                  .createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // Asociar el usuario existente
            cliente.setUsuario(
                em.getReference(
                    com.productos.entities.Usuario.class,
                    cliente.getUsuario().getId()
                )
            );

            em.persist(cliente);
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

    // Consultar clientes mediante JPQL
    public List<Cliente> listar() {
        EntityManager em = JPAUtil.getEntityManagerFactory()
                                  .createEntityManager();

        try {
            return em.createQuery(
                "SELECT c FROM Cliente c "
                + "JOIN FETCH c.usuario ORDER BY c.id",
                Cliente.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

    // Buscar cliente por ID
    public Cliente buscarPorId(Integer id) {
        EntityManager em = JPAUtil.getEntityManagerFactory()
                                  .createEntityManager();

        try {
            return em.createQuery(
                "SELECT c FROM Cliente c "
                + "JOIN FETCH c.usuario WHERE c.id = :id",
                Cliente.class
            )
            .setParameter("id", id)
            .getResultStream()
            .findFirst()
            .orElse(null);

        } finally {
            em.close();
        }
    }

    // Consultar clientes de un usuario
    public List<Cliente> listarPorUsuario(Integer idUsuario) {
        EntityManager em = JPAUtil.getEntityManagerFactory()
                                  .createEntityManager();

        try {
            return em.createQuery(
                "SELECT c FROM Cliente c "
                + "JOIN FETCH c.usuario "
                + "WHERE c.usuario.id = :idUsuario "
                + "ORDER BY c.id",
                Cliente.class
            )
            .setParameter("idUsuario", idUsuario)
            .getResultList();

        } finally {
            em.close();
        }
    }

    // Actualizar cliente
    public boolean actualizar(Cliente cliente) {
        EntityManager em = JPAUtil.getEntityManagerFactory()
                                  .createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Cliente existente =
                    em.find(Cliente.class, cliente.getId());

            if (existente == null) {
                tx.rollback();
                return false;
            }

            existente.setNombre(cliente.getNombre());
            existente.setTelefono(cliente.getTelefono());
            existente.setDireccion(cliente.getDireccion());

            existente.setUsuario(
                em.getReference(
                    com.productos.entities.Usuario.class,
                    cliente.getUsuario().getId()
                )
            );

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

    // Eliminar cliente por ID
    public boolean eliminar(Integer id) {
        EntityManager em = JPAUtil.getEntityManagerFactory()
                                  .createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Cliente cliente = em.find(Cliente.class, id);

            if (cliente == null) {
                tx.rollback();
                return false;
            }

            em.remove(cliente);
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
