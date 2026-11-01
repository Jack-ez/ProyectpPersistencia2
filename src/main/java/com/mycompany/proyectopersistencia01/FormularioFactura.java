
package com.mycompany.proyectopersistencia01;

import com.productos.dao.FacturaDAO;
import com.productos.dao.ClienteDAO;
import com.productos.entities.Factura;
import com.productos.entities.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class FormularioFactura extends JFrame {

    private final FacturaDAO dao = new FacturaDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();

    private final JTextField txtId = new JTextField();
    private final JTextField txtFecha = new JTextField();
    private final JTextField txtTotal = new JTextField();

    private final JComboBox<Cliente> cmbCliente = new JComboBox<>();

    private final DefaultTableModel modelo =
        new DefaultTableModel(
            new Object[]{
                "ID", "Fecha", "Total", "Cliente", "ID Cliente"
            }, 0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

    private final JTable tabla = new JTable(modelo);

    public FormularioFactura() {
        configurarVentana();
        construirFormulario();
        cargarClientes();
        cargarFacturas();
    }

    private void configurarVentana() {
        setTitle("ECUAQUÍMICA - Gestión de facturas");
        setSize(950, 620);
        setMinimumSize(new Dimension(800, 550));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void construirFormulario() {

        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JLabel titulo = new JLabel(
            "GESTIÓN DE FACTURAS",
            SwingConstants.CENTER
        );

        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        titulo.setForeground(new Color(23, 77, 53));

        principal.add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(10, 10));

        JPanel formulario = new JPanel(new GridLayout(4, 2, 8, 8));

        formulario.setBorder(
            BorderFactory.createTitledBorder("Datos de la factura")
        );

        txtId.setEditable(false);

        formulario.add(new JLabel("ID:"));
        formulario.add(txtId);

        formulario.add(new JLabel("Fecha (AAAA-MM-DD):"));
        formulario.add(txtFecha);

        formulario.add(new JLabel("Total:"));
        formulario.add(txtTotal);

        formulario.add(new JLabel("Cliente:"));
        formulario.add(cmbCliente);

        JPanel botones = new JPanel(
            new FlowLayout(FlowLayout.CENTER, 10, 8)
        );

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

        tabla.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );

        tabla.setRowHeight(24);
        tabla.getTableHeader().setReorderingAllowed(false);

        // Ocultar la columna auxiliar que guarda el ID del cliente
        tabla.removeColumn(
            tabla.getColumnModel().getColumn(4)
        );

        JScrollPane scroll = new JScrollPane(tabla);

        scroll.setBorder(
            BorderFactory.createTitledBorder("Facturas registradas")
        );

        centro.add(superior, BorderLayout.NORTH);
        centro.add(scroll, BorderLayout.CENTER);

        principal.add(centro, BorderLayout.CENTER);
        add(principal);

        btnGuardar.addActionListener(e -> guardarFactura());
        btnActualizar.addActionListener(e -> actualizarFactura());
        btnEliminar.addActionListener(e -> eliminarFactura());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()
                    && tabla.getSelectedRow() >= 0) {
                cargarSeleccion();
            }
        });
    }

    // Cargar clientes en la lista desplegable
    private void cargarClientes() {
        try {
            cmbCliente.removeAllItems();

            List<Cliente> clientes = clienteDAO.listar();

            for (Cliente c : clientes) {
                cmbCliente.addItem(c);
            }

            cmbCliente.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(
                        JList<?> lista, Object valor, int indice,
                        boolean seleccionado, boolean tieneFoco) {

                    super.getListCellRendererComponent(
                        lista, valor, indice, seleccionado, tieneFoco
                    );

                    if (valor instanceof Cliente c) {
                        setText(c.getId() + " - " + c.getNombre());
                    }

                    return this;
                }
            });

        } catch (RuntimeException e) {
            mostrarError("No se pudieron cargar los clientes.", e);
        }
    }

    // Consultar facturas mediante JPQL
    private void cargarFacturas() {
        try {
            modelo.setRowCount(0);

            List<Factura> facturas = dao.listar();

            for (Factura f : facturas) {
                modelo.addRow(new Object[]{
                    f.getId(),
                    f.getFecha(),
                    f.getTotal(),
                    f.getCliente().getNombre(),
                    f.getCliente().getId()
                });
            }

        } catch (RuntimeException e) {
            mostrarError("No se pudieron cargar las facturas.", e);
        }
    }

    // Registrar factura
    private void guardarFactura() {
        try {
            Factura factura = leerFormulario();

            if (factura == null) {
                return;
            }

            dao.guardar(factura);

            JOptionPane.showMessageDialog(
                this,
                "Factura registrada correctamente."
            );

            limpiarFormulario();
            cargarFacturas();

        } catch (RuntimeException e) {
            mostrarError("No se pudo registrar la factura.", e);
        }
    }

    // Actualizar factura
    private void actualizarFactura() {
        try {
            if (txtId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Seleccione una factura de la tabla."
                );
                return;
            }

            Factura factura = leerFormulario();

            if (factura == null) {
                return;
            }

            factura.setId(Integer.valueOf(txtId.getText()));

            if (dao.actualizar(factura)) {
                JOptionPane.showMessageDialog(
                    this,
                    "Factura actualizada correctamente."
                );

                limpiarFormulario();
                cargarFacturas();

            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "La factura ya no existe."
                );
            }

        } catch (RuntimeException e) {
            mostrarError("No se pudo actualizar la factura.", e);
        }
    }

    // Eliminar factura
    private void eliminarFactura() {
        try {
            if (txtId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Seleccione una factura de la tabla."
                );
                return;
            }

            int id = Integer.parseInt(txtId.getText());

            int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Desea eliminar la factura seleccionada?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
            );

            if (respuesta == JOptionPane.YES_OPTION) {

                if (dao.eliminar(id)) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Factura eliminada correctamente."
                    );

                    limpiarFormulario();
                    cargarFacturas();

                } else {
                    JOptionPane.showMessageDialog(
                        this,
                        "La factura ya no existe."
                    );
                }
            }

        } catch (RuntimeException e) {
            mostrarError("No se pudo eliminar la factura.", e);
        }
    }

    // Leer y validar los campos
    private Factura leerFormulario() {

        String fechaTexto = txtFecha.getText().trim();
        String totalTexto = txtTotal.getText().trim();

        Cliente cliente = (Cliente) cmbCliente.getSelectedItem();

        if (fechaTexto.isEmpty() || totalTexto.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Complete todos los campos."
            );
            return null;
        }

        if (cliente == null) {
            JOptionPane.showMessageDialog(
                this,
                "Primero registre un cliente y selecciónelo."
            );
            return null;
        }

        try {
            LocalDate fecha = LocalDate.parse(fechaTexto);

            double total = Double.parseDouble(
                totalTexto.replace(',', '.')
            );

            if (!Double.isFinite(total) || total < 0) {
                JOptionPane.showMessageDialog(
                    this,
                    "El total debe ser un número válido y no negativo."
                );
                return null;
            }

            Factura factura = new Factura();
            factura.setFecha(fecha);
            factura.setTotal(total);
            factura.setCliente(cliente);

            return factura;

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(
                this,
                "Ingrese la fecha con el formato AAAA-MM-DD."
            );
            return null;

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                this,
                "Ingrese un total numérico válido."
            );
            return null;
        }
    }

    // Cargar datos seleccionados
    private void cargarSeleccion() {

        int fila = tabla.getSelectedRow();

        if (fila >= 0) {
            txtId.setText(modelo.getValueAt(fila, 0).toString());
            txtFecha.setText(modelo.getValueAt(fila, 1).toString());
            txtTotal.setText(modelo.getValueAt(fila, 2).toString());

            Integer idCliente = (Integer) modelo.getValueAt(fila, 4);

            for (int i = 0; i < cmbCliente.getItemCount(); i++) {
                Cliente c = cmbCliente.getItemAt(i);

                if (c.getId().equals(idCliente)) {
                    cmbCliente.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    // Limpiar campos
    private void limpiarFormulario() {
        txtId.setText("");
        txtFecha.setText("");
        txtTotal.setText("");

        if (cmbCliente.getItemCount() > 0) {
            cmbCliente.setSelectedIndex(0);
        }

        tabla.clearSelection();
        txtFecha.requestFocusInWindow();
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
