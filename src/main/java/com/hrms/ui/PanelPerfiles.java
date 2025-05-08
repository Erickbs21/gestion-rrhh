package com.hrms.ui;

import com.hrms.dao.EmpleadoDAO;
import com.hrms.dao.PerfilDAO;
import com.hrms.model.Empleado;
import com.hrms.model.Perfil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel para la administración de perfiles
 */
public class PanelPerfiles extends JPanel {
    private JTable tablaPerfiles;
    private DefaultTableModel modeloTabla;
    private JButton botonAgregar, botonEditar, botonEliminar, botonAsignar;
    private PerfilDAO perfilDAO;
    private EmpleadoDAO empleadoDAO;
    
    public PanelPerfiles() {
        // Inicializar DAOs
        perfilDAO = new PerfilDAO();
        empleadoDAO = new EmpleadoDAO();
        
        // Configurar el panel
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Modelo de tabla para perfiles
        String[] nombreColumnas = {"ID", "Nombre de Perfil", "Descripción", "Permisos"};
        modeloTabla = new DefaultTableModel(nombreColumnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer la tabla no editable
            }
        };
        
        tablaPerfiles = new JTable(modeloTabla);
        tablaPerfiles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaPerfiles.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane panelDesplazamiento = new JScrollPane(tablaPerfiles);
        add(panelDesplazamiento, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel();
        botonAgregar = new JButton("Agregar Perfil");
        botonEditar = new JButton("Editar Perfil");
        botonEliminar = new JButton("Eliminar Perfil");
        botonAsignar = new JButton("Asignar a Empleado");
        
        panelBotones.add(botonAgregar);
        panelBotones.add(botonEditar);
        panelBotones.add(botonEliminar);
        panelBotones.add(botonAsignar);
        
        add(panelBotones, BorderLayout.SOUTH);
        
        // Agregar listeners
        botonAgregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarDialogoPerfil(null);
            }
        });
        
        botonEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int filaSeleccionada = tablaPerfiles.getSelectedRow();
                if (filaSeleccionada >= 0) {
                    int perfilId = (int) tablaPerfiles.getValueAt(filaSeleccionada, 0);
                    Perfil perfil = perfilDAO.obtenerPerfilPorId(perfilId);
                    if (perfil != null) {
                        mostrarDialogoPerfil(perfil);
                    }
                } else {
                    JOptionPane.showMessageDialog(PanelPerfiles.this, 
                        "Por favor, seleccione un perfil para editar", 
                        "Selección requerida", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        
        botonEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int filaSeleccionada = tablaPerfiles.getSelectedRow();
                if (filaSeleccionada >= 0) {
                    int perfilId = (int) tablaPerfiles.getValueAt(filaSeleccionada, 0);
                    
                    int confirmar = JOptionPane.showConfirmDialog(PanelPerfiles.this,
                        "¿Está seguro de que desea eliminar este perfil?",
                        "Confirmar eliminación",
                        JOptionPane.YES_NO_OPTION);
                    
                    if (confirmar == JOptionPane.YES_OPTION) {
                        if (perfilDAO.eliminarPerfil(perfilId)) {
                            JOptionPane.showMessageDialog(PanelPerfiles.this, 
                                "Perfil eliminado correctamente", 
                                "Eliminación exitosa", JOptionPane.INFORMATION_MESSAGE);
                            cargarDatosPerfiles();
                        } else {
                            JOptionPane.showMessageDialog(PanelPerfiles.this, 
                                "Error al eliminar el perfil", 
                                "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(PanelPerfiles.this, 
                        "Por favor, seleccione un perfil para eliminar", 
                        "Selección requerida", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        
        botonAsignar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int filaSeleccionada = tablaPerfiles.getSelectedRow();
                if (filaSeleccionada >= 0) {
                    int perfilId = (int) tablaPerfiles.getValueAt(filaSeleccionada, 0);
                    String nombrePerfil = (String) tablaPerfiles.getValueAt(filaSeleccionada, 1);
                    mostrarDialogoAsignarPerfil(perfilId, nombrePerfil);
                } else {
                    JOptionPane.showMessageDialog(PanelPerfiles.this, 
                        "Por favor, seleccione un perfil para asignar", 
                        "Selección requerida", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        
        // Cargar datos iniciales
        cargarDatosPerfiles();
    }
    
    private void cargarDatosPerfiles() {
        List<Perfil> perfiles = perfilDAO.obtenerTodosPerfiles();
        actualizarTablaConPerfiles(perfiles);
    }
    
    private void actualizarTablaConPerfiles(List<Perfil> perfiles) {
        modeloTabla.setRowCount(0); // Limpiar tabla
        
        for (Perfil perfil : perfiles) {
            modeloTabla.addRow(new Object[]{
                perfil.getId(),
                perfil.getNombre(),
                perfil.getDescripcion(),
                perfil.getPermisos()
            });
        }
    }
    
    private void mostrarDialogoPerfil(Perfil perfil) {
        // Crear un diálogo para agregar/editar perfil
        final JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            perfil == null ? "Agregar Perfil" : "Editar Perfil", true);
        
        dialogo.setSize(500, 300);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout());
        
        // Panel de formulario
        JPanel panelFormulario = new JPanel(new GridLayout(3, 2, 10, 10));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Campos del formulario
        JTextField campoNombre = new JTextField(20);
        JTextField campoDescripcion = new JTextField(20);
        JTextField campoPermisos = new JTextField(20);
        
        // Si estamos editando, establecer los valores actuales
        if (perfil != null) {
            campoNombre.setText(perfil.getNombre());
            campoDescripcion.setText(perfil.getDescripcion());
            campoPermisos.setText(perfil.getPermisos());
        }
        
        // Agregar campos al panel
        panelFormulario.add(new JLabel("Nombre:"));
        panelFormulario.add(campoNombre);
        panelFormulario.add(new JLabel("Descripción:"));
        panelFormulario.add(campoDescripcion);
        panelFormulario.add(new JLabel("Permisos (separados por comas):"));
        panelFormulario.add(campoPermisos);
        
        dialogo.add(panelFormulario, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel();
        JButton botonGuardar = new JButton("Guardar");
        JButton botonCancelar = new JButton("Cancelar");
        
        panelBotones.add(botonGuardar);
        panelBotones.add(botonCancelar);
        
        dialogo.add(panelBotones, BorderLayout.SOUTH);
        
        // Listener para el botón Guardar
        botonGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Validar campos
                if (campoNombre.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialogo, 
                        "El campo Nombre es obligatorio", 
                        "Campo requerido", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Crear o actualizar el objeto Perfil
                Perfil perf = perfil == null ? new Perfil() : perfil;
                perf.setNombre(campoNombre.getText().trim());
                perf.setDescripcion(campoDescripcion.getText().trim());
                perf.setPermisos(campoPermisos.getText().trim());
                
                boolean exito;
                if (perfil == null) {
                    // Agregar nuevo perfil
                    exito = perfilDAO.agregarPerfil(perf);
                } else {
                    // Actualizar perfil existente
                    exito = perfilDAO.actualizarPerfil(perf);
                }
                
                if (exito) {
                    JOptionPane.showMessageDialog(dialogo, 
                        "Perfil guardado correctamente", 
                        "Operación exitosa", JOptionPane.INFORMATION_MESSAGE);
                    dialogo.dispose();
                    cargarDatosPerfiles();
                } else {
                    JOptionPane.showMessageDialog(dialogo, 
                        "Error al guardar el perfil", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        // Listener para el botón Cancelar
        botonCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialogo.dispose();
            }
        });
        
        dialogo.setVisible(true);
    }
    
    private void mostrarDialogoAsignarPerfil(int perfilId, String nombrePerfil) {
        // Diálogo para asignar perfil a empleado
        final JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Asignar Perfil a Empleado", true);
        
        dialogo.setSize(400, 200);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Mostrar el perfil seleccionado
        panel.add(new JLabel("Perfil:"));
        JTextField campoPerfil = new JTextField(nombrePerfil);
        campoPerfil.setEditable(false);
        panel.add(campoPerfil);
        
        // Selector de empleado
        panel.add(new JLabel("Empleado:"));
        JComboBox<String> comboEmpleados = new JComboBox<>();
        List<Empleado> empleados = empleadoDAO.obtenerTodosEmpleados();
        for (Empleado empleado : empleados) {
            comboEmpleados.addItem(empleado.getNombreCompleto() + " (ID: " + empleado.getId() + ")");
        }
        panel.add(comboEmpleados);
        
        // Botones
        JButton botonAsignar = new JButton("Asignar");
        JButton botonCancelar = new JButton("Cancelar");
        
        JPanel panelBotones = new JPanel();
        panelBotones.add(botonAsignar);
        panelBotones.add(botonCancelar);
        
        dialogo.add(panel, BorderLayout.CENTER);
        dialogo.add(panelBotones, BorderLayout.SOUTH);
        
        // Listener para el botón Asignar
        botonAsignar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (comboEmpleados.getSelectedIndex() == -1) {
                    JOptionPane.showMessageDialog(dialogo, 
                        "Por favor, seleccione un empleado", 
                        "Selección requerida", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Obtener el ID del empleado seleccionado
                String empleadoSeleccionado = (String) comboEmpleados.getSelectedItem();
                int empleadoId = Integer.parseInt(empleadoSeleccionado.substring(empleadoSeleccionado.lastIndexOf("ID: ") + 4, empleadoSeleccionado.length() - 1));
                
                // Asignar perfil al empleado
                if (perfilDAO.asignarPerfilAEmpleado(perfilId, empleadoId)) {
                    JOptionPane.showMessageDialog(dialogo, 
                        "Perfil asignado correctamente", 
                        "Asignación exitosa", JOptionPane.INFORMATION_MESSAGE);
                    dialogo.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialogo, 
                        "Error al asignar el perfil", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        // Listener para el botón Cancelar
        botonCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialogo.dispose();
            }
        });
        
        dialogo.setVisible(true);
    }
}
