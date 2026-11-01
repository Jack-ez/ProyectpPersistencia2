
package com.mycompany.proyectopersistencia01;

import com.productos.persistence.JPAUtil;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ProyectoPersistencia01 {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            try {

                // Abrir el menú principal
                MenuPrincipal ventana = new MenuPrincipal();

                // Cerrar JPA cuando se cierre el menú principal
                ventana.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        JPAUtil.cerrar();
                    }
                });

                ventana.setVisible(true);

            } catch (RuntimeException e) {

                JOptionPane.showMessageDialog(
                    null,
                    "No se pudo iniciar la aplicación.\n"
                        + e.getMessage(),
                    "Error de inicio",
                    JOptionPane.ERROR_MESSAGE
                );

                JPAUtil.cerrar();
            }
        });
    }
}

