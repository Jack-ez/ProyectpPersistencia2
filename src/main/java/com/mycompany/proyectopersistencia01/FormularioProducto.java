
package com.mycompany.proyectopersistencia01;

import com.productos.dao.ProductoDAO;
import com.productos.entities.Producto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FormularioProducto extends JFrame {

    private final ProductoDAO dao = new ProductoDAO();

    private final JTextField txtId = new JTextField();
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtTipo = new JTextField();
    private final JTextField txtPrecio = new JTextField();
    private final JTextField txtCantidad = new JTextField();

    private final DefaultTableModel modelo =
        new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Tipo", "Precio", "Cantidad"}, 0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

    private final JTable tabla = new JTable(modelo);

    public FormularioProducto() {
        configurarVentana();
        construirFormulario();
        cargarProductos();
    }

    private void configurarVentana() {
        setTitle("ECUAQUÍMICA - Sistema de productos NUTRIL");
        setSize(850, 570);
        setMinimumSize(new Dimension(750, 500));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void construirFormulario() {
        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel(
            "GESTIÓN DE PRODUCTOS NUTRIL",
            SwingConstants.CENTER
        );
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        titulo.setForeground(new Color(23, 77, 53));
        principal.add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(10, 10));

        JPanel formulario = new JPanel(new GridLayout(5, 2, 8, 8));
        formulario.setBorder(BorderFactory.createTitledBorder(
            "Datos del producto"
        ));

        txtId.setEditable(false);

        formulario.add(new JLabel("ID:"));
        formulario.add(txtId);

        formulario.add(new JLabel("Nombre:"));
        formulario.add(txtNombre);

        formulario.add(new JLabel("Tipo o descripción:"));
        formulario.add(txtTipo);

        formulario.add(new JLabel("Precio:"));
        formulario.add(txtPrecio);

        formulario.add(new JLabel("Cantidad:"));
        formulario.add(txtCantidad);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));

        JButton btnGuardar = new JButton("Guardar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");

        botones.add(btnGuardar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);

        JPanel superior = new JPanel(new BorderLayout(5, 5));
        superior.add(formulario, BorderLayout.CENTER);
        superior.add(botones, BorderLayout.SOUTH);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(24);
        tabla.getTableHeader().setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder(
            "Productos registrados"
        ));

        centro.add(superior, BorderLayout.NORTH);
        centro.add(scroll, BorderLayout.CENTER);

        principal.add(centro, BorderLayout.CENTER);
        add(principal);

        btnGuardar.addActionListener(e -> guardarProducto());
        btnActualizar.addActionListener(e -> actualizarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                cargarSeleccion();
            }
        });
    }

    private void cargarProductos() {
        try {
            modelo.setRowCount(0);

            List<Producto> productos = dao.listar();

            for (Producto p : productos) {
                modelo.addRow(new Object[]{
                    p.getId(),
                    p.getNombre(),
                    p.getTipo(),
                    p.getPrecio(),
                    p.getCantidad()
                });
            }
        } catch (RuntimeException e) {
            mostrarError("No se pudieron cargar los productos.", e);
        }
    }

    private void guardarProducto() {
        try {
            Producto producto = leerFormulario();

            if (producto == null) {
                return;
            }

            dao.guardar(producto);

            JOptionPane.showMessageDialog(
                this, "Producto guardado correctamente."
            );

            limpiarFormulario();
            cargarProductos();

        } catch (RuntimeException e) {
            mostrarError("No se pudo guardar el producto.", e);
        }
    }

    private void actualizarProducto() {
        try {
            if (txtId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                    this, "Seleccione un producto de la tabla."
                );
                return;
            }

            Producto producto = leerFormulario();

            if (producto == null) {
                return;
            }

            producto.setId(Integer.valueOf(txtId.getText()));

            if (dao.actualizar(producto)) {
                JOptionPane.showMessageDialog(
                    this, "Producto actualizado correctamente."
                );
                limpiarFormulario();
                cargarProductos();
            } else {
                JOptionPane.showMessageDialog(
                    this, "El producto ya no existe."
                );
            }

        } catch (RuntimeException e) {
            mostrarError("No se pudo actualizar el producto.", e);
        }
    }

    private void eliminarProducto() {
        try {
            if (txtId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                    this, "Seleccione un producto de la tabla."
                );
                return;
            }

            int id = Integer.parseInt(txtId.getText());

            int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Desea eliminar el producto seleccionado?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
            );

            if (respuesta == JOptionPane.YES_OPTION) {
                if (dao.eliminar(id)) {
                    JOptionPane.showMessageDialog(
                        this, "Producto eliminado correctamente."
                    );
                    limpiarFormulario();
                    cargarProductos();
                } else {
                    JOptionPane.showMessageDialog(
                        this, "El producto ya no existe."
                    );
                }
            }

        } catch (RuntimeException e) {
            mostrarError("No se pudo eliminar el producto.", e);
        }
    }

    private Producto leerFormulario() {
        String nombre = txtNombre.getText().trim();
        String tipo = txtTipo.getText().trim();

        if (nombre.isEmpty() || tipo.isEmpty()
                || txtPrecio.getText().trim().isEmpty()
                || txtCantidad.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                this, "Complete todos los campos."
            );
            return null;
        }

        try {
            double precio = Double.parseDouble(
                txtPrecio.getText().trim().replace(',', '.')
            );
            int cantidad = Integer.parseInt(
                txtCantidad.getText().trim()
            );

            if (!Double.isFinite(precio)
                    || precio < 0 || cantidad < 0) {
                JOptionPane.showMessageDialog(
                    this, "El precio y la cantidad deben ser válidos "
                        + "y no negativos."
                );
                return null;
            }

            return new Producto(
                null, nombre, tipo, precio, cantidad
            );

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                this,
                "Revise el precio y la cantidad. "
                    + "El precio puede ser 32.50 y la cantidad 100."
            );
            return null;
        }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();

        if (fila >= 0) {
            txtId.setText(modelo.getValueAt(fila, 0).toString());
            txtNombre.setText(modelo.getValueAt(fila, 1).toString());
            txtTipo.setText(modelo.getValueAt(fila, 2).toString());
            txtPrecio.setText(modelo.getValueAt(fila, 3).toString());
            txtCantidad.setText(modelo.getValueAt(fila, 4).toString());
        }
    }

    private void limpiarFormulario() {
        txtId.setText("");
        txtNombre.setText("");
        txtTipo.setText("");
        txtPrecio.setText("");
        txtCantidad.setText("");
        tabla.clearSelection();
        txtNombre.requestFocusInWindow();
    }

    private void mostrarError(String mensaje, Exception e) {
        JOptionPane.showMessageDialog(
            this,
            mensaje + "\n" + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}

