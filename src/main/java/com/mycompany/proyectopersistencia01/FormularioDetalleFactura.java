
package com.mycompany.proyectopersistencia01;

import com.productos.dao.DetalleFacturaDAO;
import com.productos.dao.FacturaDAO;
import com.productos.dao.ProductoDAO;

import com.productos.entities.DetalleFactura;
import com.productos.entities.Factura;
import com.productos.entities.Producto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class FormularioDetalleFactura extends JFrame {

    private final DetalleFacturaDAO detalleDAO = new DetalleFacturaDAO();
    private final FacturaDAO facturaDAO = new FacturaDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();

    private JTextField txtId;
    private JTextField txtCantidad;
    private JTextField txtPrecio;
    private JTextField txtSubtotal;

    private JComboBox<Factura> cmbFactura;
    private JComboBox<Producto> cmbProducto;

    private JTable tabla;
    private DefaultTableModel modelo;

    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    public FormularioDetalleFactura() {

        setTitle("Gestión de Detalles de Factura");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        crearComponentes();
        cargarFacturas();
        cargarProductos();
        cargarDetalles();
    }

    // CREAR LOS COMPONENTES DEL FORMULARIO
    
private void crearComponentes() {

    JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
    panelPrincipal.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
    );

    // TITULO
    JLabel titulo = new JLabel(
            "REGISTRO DE DETALLES DE FACTURA",
            SwingConstants.CENTER
    );

    titulo.setFont(new Font("Arial", Font.BOLD, 20));

    panelPrincipal.add(titulo, BorderLayout.NORTH);

    // PANEL DE CAMPOS
    JPanel panelCampos = new JPanel(new GridLayout(6, 2, 10, 8));

    txtId = new JTextField();
    txtId.setEditable(false);

    txtCantidad = new JTextField();
    txtPrecio = new JTextField();

    txtSubtotal = new JTextField();
    txtSubtotal.setEditable(false);

    cmbFactura = new JComboBox<>();
    cmbProducto = new JComboBox<>();

    panelCampos.add(new JLabel("ID del detalle:"));
    panelCampos.add(txtId);

    panelCampos.add(new JLabel("Factura:"));
    panelCampos.add(cmbFactura);

    panelCampos.add(new JLabel("Producto:"));
    panelCampos.add(cmbProducto);

    panelCampos.add(new JLabel("Cantidad:"));
    panelCampos.add(txtCantidad);

    panelCampos.add(new JLabel("Precio unitario:"));
    panelCampos.add(txtPrecio);

    panelCampos.add(new JLabel("Subtotal:"));
    panelCampos.add(txtSubtotal);

    // BOTONES
    JPanel panelBotones = new JPanel(new FlowLayout());

    btnGuardar = new JButton("Guardar");
    btnActualizar = new JButton("Actualizar");
    btnEliminar = new JButton("Eliminar");
    btnLimpiar = new JButton("Limpiar");

    panelBotones.add(btnGuardar);
    panelBotones.add(btnActualizar);
    panelBotones.add(btnEliminar);
    panelBotones.add(btnLimpiar);

    // AGRUPAR CAMPOS Y BOTONES
    JPanel panelSuperior = new JPanel(new BorderLayout(10, 10));
    panelSuperior.add(panelCampos, BorderLayout.CENTER);
    panelSuperior.add(panelBotones, BorderLayout.SOUTH);

    // TABLA
    modelo = new DefaultTableModel(
            new Object[]{
                "ID",
                "Factura",
                "Producto",
                "Cantidad",
                "Precio unitario",
                "Subtotal",
                "ID Factura",
                "ID Producto"
            }, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    tabla = new JTable(modelo);
    tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JScrollPane scroll = new JScrollPane(tabla);

    // DISTRIBUCION CORRECTA
    JPanel panelCentro = new JPanel(new BorderLayout(10, 10));

    panelCentro.add(panelSuperior, BorderLayout.NORTH);
    panelCentro.add(scroll, BorderLayout.CENTER);

    panelPrincipal.add(panelCentro, BorderLayout.CENTER);

    add(panelPrincipal);

    // OCULTAR COLUMNAS INTERNAS
    ocultarColumnas();

    // EVENTOS DE BOTONES
    btnGuardar.addActionListener(e -> guardar());
    btnActualizar.addActionListener(e -> actualizar());
    btnEliminar.addActionListener(e -> eliminar());
    btnLimpiar.addActionListener(e -> limpiar());

    tabla.getSelectionModel().addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
            seleccionarRegistro();
        }
    });

    // CALCULAR SUBTOTAL
    KeyAdapter calculador = new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
            calcularSubtotal();
        }
    };

    txtCantidad.addKeyListener(calculador);
    txtPrecio.addKeyListener(calculador);

    // CARGAR PRECIO DEL PRODUCTO
    cmbProducto.addActionListener(e -> {
        Producto producto =
                (Producto) cmbProducto.getSelectedItem();

        if (producto != null) {
            txtPrecio.setText(
                    String.valueOf(producto.getPrecio())
            );

            calcularSubtotal();
        }
    });
}


    // MOSTRAR FACTURAS EN EL COMBOBOX
    private void cargarFacturas() {

        cmbFactura.removeAllItems();

        List<Factura> facturas = facturaDAO.listar();

        for (Factura factura : facturas) {
            cmbFactura.addItem(factura);
        }

        cmbFactura.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus
            ) {
                super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus
                );

                if (value instanceof Factura) {
                    Factura factura = (Factura) value;

                    setText(
                            "Factura " + factura.getId()
                            + " - " + factura.getFecha()
                    );
                }

                return this;
            }
        });
    }

    // MOSTRAR PRODUCTOS EN EL COMBOBOX
    private void cargarProductos() {

        cmbProducto.removeAllItems();

        List<Producto> productos = productoDAO.listar();

        for (Producto producto : productos) {
            cmbProducto.addItem(producto);
        }

        cmbProducto.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus
            ) {
                super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus
                );

                if (value instanceof Producto) {
                    Producto producto = (Producto) value;

                    setText(
                            producto.getId()
                            + " - " + producto.getNombre()
                            + " ($" + producto.getPrecio() + ")"
                    );
                }

                return this;
            }
        });
    }

    // CALCULAR EL SUBTOTAL
    private void calcularSubtotal() {

        try {
            int cantidad = Integer.parseInt(
                    txtCantidad.getText().trim()
            );

            double precio = Double.parseDouble(
                    txtPrecio.getText().trim()
            );

            double subtotal = cantidad * precio;

            txtSubtotal.setText(
                    String.format("%.2f", subtotal)
            );

        } catch (NumberFormatException e) {
            txtSubtotal.setText("");
        }
    }

    // OBTENER LOS DATOS DEL FORMULARIO
    private DetalleFactura leerFormulario() {

        if (cmbFactura.getSelectedItem() == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar una factura."
            );
        }

        if (cmbProducto.getSelectedItem() == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un producto."
            );
        }

        int cantidad = Integer.parseInt(
                txtCantidad.getText().trim()
        );

        double precio = Double.parseDouble(
                txtPrecio.getText().trim()
        );

        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero."
            );
        }

        if (!Double.isFinite(precio) || precio < 0) {
            throw new IllegalArgumentException(
                    "El precio debe ser un número válido y no negativo."
            );
        }

        Factura factura =
                (Factura) cmbFactura.getSelectedItem();

        Producto producto =
                (Producto) cmbProducto.getSelectedItem();

        double subtotal = cantidad * precio;

        return new DetalleFactura(
                null,
                cantidad,
                precio,
                subtotal,
                factura,
                producto
        );
    }

    // GUARDAR
    private void guardar() {

        try {
            DetalleFactura detalle = leerFormulario();

            detalleDAO.guardar(detalle);

            JOptionPane.showMessageDialog(
                    this,
                    "Detalle de factura guardado correctamente."
            );

            cargarDetalles();
            limpiar();

        } catch (Exception e) {
            mostrarError(e);
        }
    }

    // ACTUALIZAR
    private void actualizar() {

        try {
            if (txtId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Seleccione un detalle de la tabla."
                );
                return;
            }

            DetalleFactura detalle = leerFormulario();

            detalle.setId(
                    Integer.parseInt(txtId.getText().trim())
            );

            detalleDAO.actualizar(detalle);

            JOptionPane.showMessageDialog(
                    this,
                    "Detalle actualizado correctamente."
            );

            cargarDetalles();
            limpiar();

        } catch (Exception e) {
            mostrarError(e);
        }
    }

    // ELIMINAR
    private void eliminar() {

        try {
            if (txtId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Seleccione un detalle de la tabla."
                );
                return;
            }

            int opcion = JOptionPane.showConfirmDialog(
                    this,
                    "¿Está seguro de eliminar este detalle?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION
            );

            if (opcion == JOptionPane.YES_OPTION) {

                int id = Integer.parseInt(
                        txtId.getText().trim()
                );

                detalleDAO.eliminar(id);

                JOptionPane.showMessageDialog(
                        this,
                        "Detalle eliminado correctamente."
                );

                cargarDetalles();
                limpiar();
            }

        } catch (Exception e) {
            mostrarError(e);
        }
    }

    // CARGAR LOS DETALLES EN LA TABLA
    private void cargarDetalles() {

        modelo.setRowCount(0);

        List<DetalleFactura> detalles = detalleDAO.listar();

        for (DetalleFactura detalle : detalles) {

            modelo.addRow(new Object[]{
                detalle.getId(),
                "Factura " + detalle.getFactura().getId(),
                detalle.getProducto().getNombre(),
                detalle.getCantidad(),
                detalle.getPrecioUnitario(),
                detalle.getSubtotal(),
                detalle.getFactura().getId(),
                detalle.getProducto().getId()
            });
        }

        ocultarColumnas();
    }

    // OCULTAR LAS COLUMNAS DE ID DE LAS RELACIONES
    private void ocultarColumnas() {

        if (tabla.getColumnModel().getColumnCount() >= 8) {
            tabla.getColumnModel().getColumn(6).setMinWidth(0);
            tabla.getColumnModel().getColumn(6).setMaxWidth(0);
            tabla.getColumnModel().getColumn(6).setWidth(0);

            tabla.getColumnModel().getColumn(7).setMinWidth(0);
            tabla.getColumnModel().getColumn(7).setMaxWidth(0);
            tabla.getColumnModel().getColumn(7).setWidth(0);
        }
    }

    // SELECCIONAR UN REGISTRO DE LA TABLA
    private void seleccionarRegistro() {

        int fila = tabla.getSelectedRow();

        if (fila >= 0) {

            int filaModelo = tabla.convertRowIndexToModel(fila);

            txtId.setText(
                    modelo.getValueAt(filaModelo, 0).toString()
            );

            txtCantidad.setText(
                    modelo.getValueAt(filaModelo, 3).toString()
            );

            txtPrecio.setText(
                    modelo.getValueAt(filaModelo, 4).toString()
            );

            txtSubtotal.setText(
                    modelo.getValueAt(filaModelo, 5).toString()
            );

            Integer idFactura = Integer.valueOf(
                    modelo.getValueAt(filaModelo, 6).toString()
            );

            Integer idProducto = Integer.valueOf(
                    modelo.getValueAt(filaModelo, 7).toString()
            );

            seleccionarFactura(idFactura);
            seleccionarProducto(idProducto);
        }
    }

    // SELECCIONAR FACTURA POR ID
    private void seleccionarFactura(Integer id) {

        for (int i = 0; i < cmbFactura.getItemCount(); i++) {

            Factura factura = cmbFactura.getItemAt(i);

            if (factura.getId().equals(id)) {
                cmbFactura.setSelectedIndex(i);
                return;
            }
        }
    }

    // SELECCIONAR PRODUCTO POR ID
    private void seleccionarProducto(Integer id) {

        for (int i = 0; i < cmbProducto.getItemCount(); i++) {

            Producto producto = cmbProducto.getItemAt(i);

            if (producto.getId().equals(id)) {
                cmbProducto.setSelectedIndex(i);
                return;
            }
        }
    }

    // LIMPIAR FORMULARIO
    private void limpiar() {

        txtId.setText("");
        txtCantidad.setText("");
        txtPrecio.setText("");
        txtSubtotal.setText("");

        tabla.clearSelection();

        if (cmbFactura.getItemCount() > 0) {
            cmbFactura.setSelectedIndex(0);
        }

        if (cmbProducto.getItemCount() > 0) {
            cmbProducto.setSelectedIndex(0);
        }
    }

    // MOSTRAR ERRORES
    private void mostrarError(Exception e) {

        String mensaje = e.getMessage();

        if (e.getCause() != null
                && e.getCause().getMessage() != null) {
            mensaje += "\n" + e.getCause().getMessage();
        }

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
