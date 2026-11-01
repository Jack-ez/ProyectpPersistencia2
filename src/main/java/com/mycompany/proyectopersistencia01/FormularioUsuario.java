
package com.mycompany.proyectopersistencia01;

import com.productos.dao.UsuarioDAO;
import com.productos.entities.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FormularioUsuario extends JFrame {

    private final UsuarioDAO dao = new UsuarioDAO();

    private final JTextField txtId = new JTextField();
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtRol = new JTextField();
    private final JTextField txtUsuario = new JTextField();
    private final JPasswordField txtPassword = new JPasswordField();

    private final DefaultTableModel modelo =
        new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Rol", "Usuario"}, 0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

    private final JTable tabla = new JTable(modelo);

    public FormularioUsuario() {
        configurarVentana();
        construirFormulario();
        cargarUsuarios();
    }

    private void configurarVentana() {
        setTitle("ECUAQUÍMICA - Gestión de usuarios");
        setSize(850, 570);
        setMinimumSize(new Dimension(750, 500));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void construirFormulario() {

        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JLabel titulo = new JLabel(
            "GESTIÓN DE USUARIOS",
            SwingConstants.CENTER
        );

        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        titulo.setForeground(new Color(23, 77, 53));

        principal.add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(10, 10));

        JPanel formulario = new JPanel(new GridLayout(5, 2, 8, 8));

        formulario.setBorder(
            BorderFactory.createTitledBorder("Datos del usuario")
        );

        txtId.setEditable(false);

        formulario.add(new JLabel("ID:"));
        formulario.add(txtId);

        formulario.add(new JLabel("Nombre completo:"));
        formulario.add(txtNombre);

        formulario.add(new JLabel("Rol:"));
        formulario.add(txtRol);

        formulario.add(new JLabel("Nombre de usuario:"));
        formulario.add(txtUsuario);

        formulario.add(new JLabel("Contraseña:"));
        formulario.add(txtPassword);

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
            BorderFactory.createTitledBorder("Usuarios registrados")
        );

        centro.add(superior, BorderLayout.NORTH);
        centro.add(scroll, BorderLayout.CENTER);

        principal.add(centro, BorderLayout.CENTER);

        add(principal);

        btnGuardar.addActionListener(e -> guardarUsuario());
        btnActualizar.addActionListener(e -> actualizarUsuario());
        btnEliminar.addActionListener(e -> eliminarUsuario());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()
                    && tabla.getSelectedRow() >= 0) {
                cargarSeleccion();
            }
        });
    }

    // Consultar usuarios mediante JPQL
    private void cargarUsuarios() {
        try {
            modelo.setRowCount(0);

            List<Usuario> usuarios = dao.listar();

            for (Usuario u : usuarios) {
                modelo.addRow(new Object[]{
                    u.getId(),
                    u.getNombre(),
                    u.getRol(),
                    u.getUsuario()
                });
            }

        } catch (RuntimeException e) {
            mostrarError("No se pudieron cargar los usuarios.", e);
        }
    }

    // Registrar usuario
    private void guardarUsuario() {
        try {
            Usuario usuario = leerFormulario();

            if (usuario == null) {
                return;
            }

            dao.guardar(usuario);

            JOptionPane.showMessageDialog(
                this,
                "Usuario registrado correctamente."
            );

            limpiarFormulario();
            cargarUsuarios();

        } catch (RuntimeException e) {
            mostrarError("No se pudo registrar el usuario.", e);
        }
    }

    // Actualizar usuario
    private void actualizarUsuario() {
        try {
            if (txtId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un usuario de la tabla."
                );
                return;
            }

            Usuario usuario = leerFormulario();

            if (usuario == null) {
                return;
            }

            usuario.setId(Integer.valueOf(txtId.getText()));

            if (dao.actualizar(usuario)) {
                JOptionPane.showMessageDialog(
                    this,
                    "Usuario actualizado correctamente."
                );

                limpiarFormulario();
                cargarUsuarios();

            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "El usuario ya no existe."
                );
            }

        } catch (RuntimeException e) {
            mostrarError("No se pudo actualizar el usuario.", e);
        }
    }

    // Eliminar usuario
    private void eliminarUsuario() {
        try {
            if (txtId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un usuario de la tabla."
                );
                return;
            }

            int id = Integer.parseInt(txtId.getText());

            int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Desea eliminar el usuario seleccionado?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
            );

            if (respuesta == JOptionPane.YES_OPTION) {

                if (dao.eliminar(id)) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Usuario eliminado correctamente."
                    );

                    limpiarFormulario();
                    cargarUsuarios();

                } else {
                    JOptionPane.showMessageDialog(
                        this,
                        "El usuario ya no existe."
                    );
                }
            }

        } catch (RuntimeException e) {
            mostrarError("No se pudo eliminar el usuario.", e);
        }
    }

    // Leer y validar los campos
    private Usuario leerFormulario() {

        String nombre = txtNombre.getText().trim();
        String rol = txtRol.getText().trim();
        String usuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (nombre.isEmpty() || rol.isEmpty()
                || usuario.isEmpty() || password.trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                this,
                "Complete todos los campos."
            );

            return null;
        }

        return new Usuario(
            null,
            nombre,
            rol,
            usuario,
            password,
            null
        );
    }

    // Cargar datos seleccionados
    private void cargarSeleccion() {

        int fila = tabla.getSelectedRow();

        if (fila >= 0) {
            txtId.setText(modelo.getValueAt(fila, 0).toString());
            txtNombre.setText(modelo.getValueAt(fila, 1).toString());
            txtRol.setText(modelo.getValueAt(fila, 2).toString());
            txtUsuario.setText(modelo.getValueAt(fila, 3).toString());

            // No se recupera la contraseña almacenada.
            txtPassword.setText("");
        }
    }

    // Limpiar campos
    private void limpiarFormulario() {

        txtId.setText("");
        txtNombre.setText("");
        txtRol.setText("");
        txtUsuario.setText("");
        txtPassword.setText("");

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

