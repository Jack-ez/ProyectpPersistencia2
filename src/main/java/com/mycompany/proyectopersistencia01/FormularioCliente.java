
package com.mycompany.proyectopersistencia01;

import com.productos.dao.ClienteDAO;
import com.productos.dao.UsuarioDAO;
import com.productos.entities.Cliente;
import com.productos.entities.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FormularioCliente extends JFrame {

    private final ClienteDAO dao = new ClienteDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    private final JTextField txtId = new JTextField();
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtTelefono = new JTextField();
    private final JTextField txtDireccion = new JTextField();

    private final JComboBox<Usuario> cmbUsuario = new JComboBox<>();

    private final DefaultTableModel modelo =
        new DefaultTableModel(
            new Object[]{
                "ID", "Nombre", "Teléfono",
                "Dirección", "Usuario responsable"
            }, 0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

    private final JTable tabla = new JTable(modelo);

    public FormularioCliente() {
        configurarVentana();
        construirFormulario();
        cargarUsuarios();
        cargarClientes();
    }

    private void configurarVentana() {
        setTitle("ECUAQUÍMICA - Gestión de clientes");
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
            "GESTIÓN DE CLIENTES",
            SwingConstants.CENTER
        );

        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        titulo.setForeground(new Color(23, 77, 53));

        principal.add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(10, 10));

        JPanel formulario = new JPanel(new GridLayout(5, 2, 8, 8));

        formulario.setBorder(
            BorderFactory.createTitledBorder("Datos del cliente")
        );

        txtId.setEditable(false);

        formulario.add(new JLabel("ID:"));
        formulario.add(txtId);

        formulario.add(new JLabel("Nombre:"));
        formulario.add(txtNombre);

        formulario.add(new JLabel("Teléfono:"));
        formulario.add(txtTelefono);

        formulario.add(new JLabel("Dirección:"));
        formulario.add(txtDireccion);

        formulario.add(new JLabel("Usuario responsable:"));
        formulario.add(cmbUsuario);

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

        JScrollPane scroll = new JScrollPane(tabla);

        scroll.setBorder(
            BorderFactory.createTitledBorder("Clientes registrados")
        );

        centro.add(superior, BorderLayout.NORTH);
        centro.add(scroll, BorderLayout.CENTER);

        principal.add(centro, BorderLayout.CENTER);
        add(principal);

        btnGuardar.addActionListener(e -> guardarCliente());
        btnActualizar.addActionListener(e -> actualizarCliente());
        btnEliminar.addActionListener(e -> eliminarCliente());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()
                    && tabla.getSelectedRow() >= 0) {
                cargarSeleccion();
            }
        });
    }

    // Cargar usuarios en la lista desplegable
    private void cargarUsuarios() {
        try {
            cmbUsuario.removeAllItems();

            List<Usuario> usuarios = usuarioDAO.listar();

            for (Usuario u : usuarios) {
                cmbUsuario.addItem(u);
            }

            cmbUsuario.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(
                        JList<?> lista, Object valor, int indice,
                        boolean seleccionado, boolean tieneFoco) {

                    super.getListCellRendererComponent(
                        lista, valor, indice, seleccionado, tieneFoco
                    );

                    if (valor instanceof Usuario u) {
                        setText(
                            u.getId() + " - " + u.getNombre()
                        );
                    }

                    return this;
                }
            });

        } catch (RuntimeException e) {
            mostrarError("No se pudieron cargar los usuarios.", e);
        }
    }

    // Consultar clientes
    private void cargarClientes() {
        try {
            modelo.setRowCount(0);

            List<Cliente> clientes = dao.listar();

            for (Cliente c : clientes) {
                modelo.addRow(new Object[]{
                    c.getId(),
                    c.getNombre(),
                    c.getTelefono(),
                    c.getDireccion(),
                    c.getUsuario().getNombre()
                });
            }

        } catch (RuntimeException e) {
            mostrarError("No se pudieron cargar los clientes.", e);
        }
    }

    // Registrar cliente
    private void guardarCliente() {
        try {
            Cliente cliente = leerFormulario();

            if (cliente == null) {
                return;
            }

            dao.guardar(cliente);

            JOptionPane.showMessageDialog(
                this,
                "Cliente registrado correctamente."
            );

            limpiarFormulario();
            cargarClientes();

        } catch (RuntimeException e) {
            mostrarError("No se pudo registrar el cliente.", e);
        }
    }

    // Actualizar cliente
    private void actualizarCliente() {
        try {
            if (txtId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un cliente de la tabla."
                );
                return;
            }

            Cliente cliente = leerFormulario();

            if (cliente == null) {
                return;
            }

            cliente.setId(Integer.valueOf(txtId.getText()));

            if (dao.actualizar(cliente)) {
                JOptionPane.showMessageDialog(
                    this,
                    "Cliente actualizado correctamente."
                );

                limpiarFormulario();
                cargarClientes();

            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "El cliente ya no existe."
                );
            }

        } catch (RuntimeException e) {
            mostrarError("No se pudo actualizar el cliente.", e);
        }
    }

    // Eliminar cliente
    private void eliminarCliente() {
        try {
            if (txtId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un cliente de la tabla."
                );
                return;
            }

            int id = Integer.parseInt(txtId.getText());

            int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Desea eliminar el cliente seleccionado?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
            );

            if (respuesta == JOptionPane.YES_OPTION) {

                if (dao.eliminar(id)) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Cliente eliminado correctamente."
                    );

                    limpiarFormulario();
                    cargarClientes();

                } else {
                    JOptionPane.showMessageDialog(
                        this,
                        "El cliente ya no existe."
                    );
                }
            }

        } catch (RuntimeException e) {
            mostrarError("No se pudo eliminar el cliente.", e);
        }
    }

    // Leer y validar los campos
    private Cliente leerFormulario() {

        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String direccion = txtDireccion.getText().trim();

        Usuario usuario = (Usuario) cmbUsuario.getSelectedItem();

        if (nombre.isEmpty() || telefono.isEmpty()
                || direccion.isEmpty()) {

            JOptionPane.showMessageDialog(
                this,
                "Complete todos los campos."
            );

            return null;
        }

        if (usuario == null) {
            JOptionPane.showMessageDialog(
                this,
                "Primero registre un usuario y selecciónelo."
            );

            return null;
        }

        Cliente cliente = new Cliente();
        cliente.setNombre(nombre);
        cliente.setTelefono(telefono);
        cliente.setDireccion(direccion);
        cliente.setUsuario(usuario);

        return cliente;
    }

    // Cargar datos seleccionados
    private void cargarSeleccion() {

        int fila = tabla.getSelectedRow();

        if (fila >= 0) {

            txtId.setText(modelo.getValueAt(fila, 0).toString());
            txtNombre.setText(modelo.getValueAt(fila, 1).toString());
            txtTelefono.setText(modelo.getValueAt(fila, 2).toString());
            txtDireccion.setText(modelo.getValueAt(fila, 3).toString());

            Integer idUsuario = null;

            for (int i = 0; i < cmbUsuario.getItemCount(); i++) {
                Usuario u = cmbUsuario.getItemAt(i);

                if (u.getNombre().equals(
                        modelo.getValueAt(fila, 4).toString())) {
                    idUsuario = u.getId();
                    break;
                }
            }

            if (idUsuario != null) {
                for (int i = 0; i < cmbUsuario.getItemCount(); i++) {
                    if (cmbUsuario.getItemAt(i).getId()
                            .equals(idUsuario)) {
                        cmbUsuario.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
    }

    // Limpiar campos
    private void limpiarFormulario() {
        txtId.setText("");
        txtNombre.setText("");
        txtTelefono.setText("");
        txtDireccion.setText("");

        if (cmbUsuario.getItemCount() > 0) {
            cmbUsuario.setSelectedIndex(0);
        }

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
