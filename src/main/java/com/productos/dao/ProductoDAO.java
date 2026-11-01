
package com.productos.dao;

import com.productos.entities.Producto;
import com.productos.persistence.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;

public class ProductoDAO {

    // Registrar un producto
    public void guardar(Producto producto) {
        EntityManager em = JPAUtil.getEntityManagerFactory()
                                  .createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(producto);
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

    // Consultar todos los productos
    public List<Producto> listar() {
        EntityManager em = JPAUtil.getEntityManagerFactory()
                                  .createEntityManager();

        try {
            return em.createQuery(
                "SELECT p FROM Producto p ORDER BY p.id",
                Producto.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    // Buscar producto por ID
    public Producto buscarPorId(Integer id) {
        EntityManager em = JPAUtil.getEntityManagerFactory()
                                  .createEntityManager();

        try {
            return em.find(Producto.class, id);
        } finally {
            em.close();
        }
    }

    // Actualizar producto
    public boolean actualizar(Producto producto) {
        EntityManager em = JPAUtil.getEntityManagerFactory()
                                  .createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Producto existente =
                    em.find(Producto.class, producto.getId());

            if (existente == null) {
                tx.rollback();
                return false;
            }

            existente.setNombre(producto.getNombre());
            existente.setTipo(producto.getTipo());
            existente.setPrecio(producto.getPrecio());
            existente.setCantidad(producto.getCantidad());

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

    // Eliminar producto por ID
    public boolean eliminar(Integer id) {
        EntityManager em = JPAUtil.getEntityManagerFactory()
                                  .createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Producto producto = em.find(Producto.class, id);

            if (producto == null) {
                tx.rollback();
                return false;
            }

            em.remove(producto);
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

