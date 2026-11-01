
package com.productos.dao;

import com.productos.entities.Usuario;
import com.productos.persistence.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;

public class UsuarioDAO {

    // Registrar un usuario
    public void guardar(Usuario usuario) {
        EntityManager em = JPAUtil.getEntityManagerFactory()
                                  .createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(usuario);
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

    // Consultar todos los usuarios mediante JPQL
    public List<Usuario> listar() {
        EntityManager em = JPAUtil.getEntityManagerFactory()
                                  .createEntityManager();

        try {
            return em.createQuery(
                "SELECT u FROM Usuario u ORDER BY u.id",
                Usuario.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    // Buscar usuario por ID
    public Usuario buscarPorId(Integer id) {
        EntityManager em = JPAUtil.getEntityManagerFactory()
                                  .createEntityManager();

        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    // Actualizar usuario
    public boolean actualizar(Usuario usuario) {
        EntityManager em = JPAUtil.getEntityManagerFactory()
                                  .createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Usuario existente =
                    em.find(Usuario.class, usuario.getId());

            if (existente == null) {
                tx.rollback();
                return false;
            }

            existente.setNombre(usuario.getNombre());
            existente.setRol(usuario.getRol());
            existente.setUsuario(usuario.getUsuario());
            existente.setPassword(usuario.getPassword());

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

    // Eliminar usuario por ID
    public boolean eliminar(Integer id) {
        EntityManager em = JPAUtil.getEntityManagerFactory()
                                  .createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Usuario usuario = em.find(Usuario.class, id);

            if (usuario == null) {
                tx.rollback();
                return false;
            }

            em.remove(usuario);
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
