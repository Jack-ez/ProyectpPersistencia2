package com.productos.persistence;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {

    private static final EntityManagerFactory FACTORY =
            Persistence.createEntityManagerFactory("com.mycompany_ProyectoPersistencia01_jar_1.0-SNAPSHOTPU");

    public static EntityManagerFactory getEntityManagerFactory() {
        return FACTORY;
    }

    public static void cerrar() {
        if (FACTORY.isOpen()) {
            FACTORY.close();
        }
    }
}
