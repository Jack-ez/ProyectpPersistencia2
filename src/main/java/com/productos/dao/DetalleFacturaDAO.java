

package com.productos.dao;

import com.productos.entities.DetalleFactura;
import com.productos.entities.Factura;
import com.productos.entities.Producto;
import com.productos.persistence.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class DetalleFacturaDAO {

    // GUARDAR UN DETALLE DE FACTURA
    public void guardar(DetalleFactura detalle) {

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // Recuperar las entidades relacionadas desde la base de datos
            Factura factura = em.getReference(
                    Factura.class,
                    detalle.getFactura().getId()
            );

            Producto producto = em.getReference(
                    Producto.class,
                    detalle.getProducto().getId()
            );

            detalle.setFactura(factura);
            detalle.setProducto(producto);

            // Calcular subtotal
            detalle.setSubtotal(
                    detalle.getCantidad() * detalle.getPrecioUnitario()
            );

            // Guardar detalle
            em.persist(detalle);

            // Actualizar el total de la factura
            recalcularTotalFactura(em, factura.getId());

            tx.commit();

        } catch (Exception e) {

            if (tx.isActive()) {
                tx.rollback();
            }

            throw new RuntimeException(
                    "Error al guardar el detalle de factura: " + e.getMessage(), e
            );

        } finally {
            em.close();
        }
    }

    // LISTAR TODOS LOS DETALLES
    public List<DetalleFactura> listar() {

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.createQuery(
                    "SELECT d FROM DetalleFactura d "
                    + "JOIN FETCH d.factura "
                    + "JOIN FETCH d.producto "
                    + "ORDER BY d.id",
                    DetalleFactura.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

    // BUSCAR UN DETALLE POR SU ID
    public DetalleFactura buscarPorId(Integer id) {

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.createQuery(
                    "SELECT d FROM DetalleFactura d "
                    + "JOIN FETCH d.factura "
                    + "JOIN FETCH d.producto "
                    + "WHERE d.id = :id",
                    DetalleFactura.class
            )
            .setParameter("id", id)
            .getSingleResult();

        } finally {
            em.close();
        }
    }

    // LISTAR DETALLES DE UNA FACTURA ESPECÍFICA
    public List<DetalleFactura> listarPorFactura(Integer idFactura) {

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.createQuery(
                    "SELECT d FROM DetalleFactura d "
                    + "JOIN FETCH d.factura "
                    + "JOIN FETCH d.producto "
                    + "WHERE d.factura.id = :idFactura "
                    + "ORDER BY d.id",
                    DetalleFactura.class
            )
            .setParameter("idFactura", idFactura)
            .getResultList();

        } finally {
            em.close();
        }
    }

    // LISTAR DETALLES DE UN PRODUCTO ESPECÍFICO
    public List<DetalleFactura> listarPorProducto(Integer idProducto) {

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.createQuery(
                    "SELECT d FROM DetalleFactura d "
                    + "JOIN FETCH d.factura "
                    + "JOIN FETCH d.producto "
                    + "WHERE d.producto.id = :idProducto "
                    + "ORDER BY d.id",
                    DetalleFactura.class
            )
            .setParameter("idProducto", idProducto)
            .getResultList();

        } finally {
            em.close();
        }
    }

    // ACTUALIZAR UN DETALLE DE FACTURA
    public void actualizar(DetalleFactura detalle) {

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // Buscar el detalle existente
            DetalleFactura existente = em.find(
                    DetalleFactura.class,
                    detalle.getId()
            );

            if (existente == null) {
                throw new RuntimeException("No existe el detalle indicado.");
            }

            // Guardar el ID de la factura anterior
            Integer idFacturaAnterior = existente.getFactura().getId();

            // Recuperar las nuevas relaciones
            Factura nuevaFactura = em.getReference(
                    Factura.class,
                    detalle.getFactura().getId()
            );

            Producto nuevoProducto = em.getReference(
                    Producto.class,
                    detalle.getProducto().getId()
            );

            // Actualizar los datos
            existente.setFactura(nuevaFactura);
            existente.setProducto(nuevoProducto);
            existente.setCantidad(detalle.getCantidad());
            existente.setPrecioUnitario(detalle.getPrecioUnitario());

            // Recalcular subtotal
            existente.setSubtotal(
                    existente.getCantidad() * existente.getPrecioUnitario()
            );

            // Actualizar total de la factura anterior
            recalcularTotalFactura(em, idFacturaAnterior);

            // Actualizar total de la nueva factura
            recalcularTotalFactura(em, nuevaFactura.getId());

            tx.commit();

        } catch (Exception e) {

            if (tx.isActive()) {
                tx.rollback();
            }

            throw new RuntimeException(
                    "Error al actualizar el detalle: " + e.getMessage(), e
            );

        } finally {
            em.close();
        }
    }

    // ELIMINAR UN DETALLE DE FACTURA
    public void eliminar(Integer id) {

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            DetalleFactura detalle = em.find(
                    DetalleFactura.class,
                    id
            );

            if (detalle == null) {
                throw new RuntimeException("No existe el detalle indicado.");
            }

            // Guardar el ID de la factura antes de eliminar
            Integer idFactura = detalle.getFactura().getId();

            // Eliminar detalle
            em.remove(detalle);

            // Recalcular el total de la factura
            recalcularTotalFactura(em, idFactura);

            tx.commit();

        } catch (Exception e) {

            if (tx.isActive()) {
                tx.rollback();
            }

            throw new RuntimeException(
                    "Error al eliminar el detalle: " + e.getMessage(), e
            );

        } finally {
            em.close();
        }
    }

    // RECALCULAR AUTOMÁTICAMENTE EL TOTAL DE UNA FACTURA
    private void recalcularTotalFactura(EntityManager em, Integer idFactura) {

        Double total = em.createQuery(
                "SELECT COALESCE(SUM(d.subtotal), 0.0) "
                + "FROM DetalleFactura d "
                + "WHERE d.factura.id = :idFactura",
                Double.class
        )
        .setParameter("idFactura", idFactura)
        .getSingleResult();

        Factura factura = em.find(Factura.class, idFactura);

        if (factura != null) {
            factura.setTotal(total);
        }
    }
}
