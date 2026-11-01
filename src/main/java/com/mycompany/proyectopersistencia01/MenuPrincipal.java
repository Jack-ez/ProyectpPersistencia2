
package com.mycompany.proyectopersistencia01;

import javax.swing.*;
import java.awt.*;

public class MenuPrincipal extends JFrame {

    public MenuPrincipal() {

        setTitle("Sistema de Gestión - ECUAQUIMICA");
        setSize(550, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        crearInterfaz();
    }

    private void crearInterfaz() {

        // PANEL PRINCIPAL
        JPanel panelPrincipal = new JPanel(new BorderLayout(15, 15));
        panelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
        );

        // TÍTULO
        JLabel titulo = new JLabel(
                "SISTEMA DE GESTIÓN",
                SwingConstants.CENTER
        );

        titulo.setFont(new Font("Arial", Font.BOLD, 25));

        JLabel subtitulo = new JLabel(
                "ECUAQUIMICA - Administración de información",
                SwingConstants.CENTER
        );

        subtitulo.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel panelTitulo = new JPanel(new GridLayout(2, 1, 5, 5));
        panelTitulo.add(titulo);
        panelTitulo.add(subtitulo);

        panelPrincipal.add(panelTitulo, BorderLayout.NORTH);

        // PANEL DE BOTONES
        JPanel panelBotones = new JPanel(new GridLayout(5, 1, 10, 10));

        JButton btnProductos = new JButton("Gestión de Productos");
        JButton btnUsuarios = new JButton("Gestión de Usuarios");
        JButton btnClientes = new JButton("Gestión de Clientes");
        JButton btnFacturas = new JButton("Gestión de Facturas");
        JButton btnDetalles = new JButton("Gestión de Detalles de Factura");

        // CONFIGURACIÓN DE BOTONES
        JButton[] botones = {
            btnProductos,
            btnUsuarios,
            btnClientes,
            btnFacturas,
            btnDetalles
        };

        for (JButton boton : botones) {
            boton.setFont(new Font("Arial", Font.BOLD, 15));
            boton.setFocusPainted(false);
            panelBotones.add(boton);
        }

        panelPrincipal.add(panelBotones, BorderLayout.CENTER);

        // PIE DE PÁGINA
        JLabel pie = new JLabel(
                "Sistema desarrollado con Java, JPA, Hibernate y PostgreSQL",
                SwingConstants.CENTER
        );

        pie.setFont(new Font("Arial", Font.PLAIN, 11));

        panelPrincipal.add(pie, BorderLayout.SOUTH);

        add(panelPrincipal);

        // EVENTOS DE LOS BOTONES
        btnProductos.addActionListener(e ->
                abrirFormulario(new FormularioProducto())
        );

        btnUsuarios.addActionListener(e ->
                abrirFormulario(new FormularioUsuario())
        );

        btnClientes.addActionListener(e ->
                abrirFormulario(new FormularioCliente())
        );

        btnFacturas.addActionListener(e ->
                abrirFormulario(new FormularioFactura())
        );

        btnDetalles.addActionListener(e ->
                abrirFormulario(new FormularioDetalleFactura())
        );
    }

    // Abrimos los formularios
    private void abrirFormulario(JFrame formulario) {

        formulario.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        formulario.setLocationRelativeTo(this);
        formulario.setVisible(true);
    }
}

