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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Panel para la gestión de empleados
 */
public class PanelEmpleados extends JPanel {
    private JTable tablaEmpleados;
    private DefaultTableModel modeloTabla;
    private JButton botonAgregar, botonEditar, botonEliminar, botonBuscar;
    private JTextField campoBusqueda;
    private EmpleadoDAO empleadoDAO;
    private PerfilDAO perfilDAO;
    
    public PanelEmpleados() {
        // Inicializar DAOs
        empleadoDAO = new EmpleadoDAO();
        perfilDAO = new PerfilDAO();
        
        // Configurar el panel
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel superior con búsqueda
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.add(new JLabel("Buscar:"));
        
        campoBusqueda = new JTextField(20);
        panelSuperior.add(campoBusqueda);
        
        botonBuscar = new JButton("Buscar");
        panelSuperior.add(botonBuscar);
        
        add(panelSuperior, BorderLayout.NORTH);
        
        // Modelo de tabla para empleados
        String[] nombreColumnas = {"ID", "Nombre", "Apellido", "Correo", "Teléfono", "Departamento", "Cargo", "Fecha Contratación"};
        modeloTabla = new DefaultTableModel(nombreColumnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer la tabla no editable
            }
        };
        
        tablaEmpleados = new JTable(modeloTabla);
        tablaEmpleados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaEmpleados.getTableHeader().setReorderingAllowed(false);
        
        // Configurar el ancho de las columnas
        tablaEmpleados.getColumnModel().getColumn(0).setPreferredWidth(30); // ID
        tablaEmpleados.getColumnModel().getColumn(1).setPreferredWidth(100); // Nombre
        tablaEmpleados.getColumnModel().getColumn(2).setPreferredWidth(100); // Apellido
        tablaEmpleados.getColumnModel().getColumn(3).setPreferredWidth(150); // Correo
        tablaEmpleados.getColumnModel().getColumn(4).setPreferredWidth(100); // Teléfono
        tablaEmpleados.getColumnModel().getColumn(5).setPreferredWidth(120); // Departamento
        tablaEmpleados.getColumnModel().getColumn(6).setPreferredWidth(120); // Cargo
        tablaEmpleados.getColumnModel().getColumn(7).setPreferredWidth(120); // Fecha Contratación
        
        JScrollPane panelDesplazamiento = new JScrollPane(tablaEmpleados);
        add(panelDesplazamiento, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel();
        botonAgregar = new JButton("Agregar Empleado");
        botonEditar = new JButton("Editar Empleado");
        botonEliminar = new JButton("Eliminar Empleado");
        
        panelBotones.add(botonAgregar);
        panelBotones.add(botonEditar);
        panelBotones.add(botonEliminar);
        
        add(panelBotones, BorderLayout.SOUTH);
        
        // Agregar listeners
        botonAgregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarDialogoEmpleado(null);
            }
        });
        
        botonEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int filaSeleccionada = tablaEmpleados.getSelectedRow();
                if (filaSeleccionada >= 0) {
                    int empleadoId = (int) tablaEmpleados.getValueAt(filaSeleccionada, 0);
                    Empleado empleado = empleadoDAO.obtenerEmpleadoPorId(empleadoId);
                    if (empleado != null) {
                        mostrarDialogoEmpleado(empleado);
                    }
                } else {
                    JOptionPane.showMessageDialog(PanelEmpleados.this, 
                        "Por favor, seleccione un empleado para editar", 
                        "Selección requerida", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        
        botonEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int filaSeleccionada = tablaEmpleados.getSelectedRow();
                if (filaSeleccionada >= 0) {
                    int empleadoId = (int) tablaEmpleados.getValueAt(filaSeleccionada, 0);
                    
                    int confirmar = JOptionPane.showConfirmDialog(PanelEmpleados.this,
                        "¿Está seguro de que desea eliminar este empleado?",
                        "Confirmar eliminación",
                        JOptionPane.YES_NO_OPTION);
                    
                    if (confirmar == JOptionPane.YES_OPTION) {
                        if (empleadoDAO.eliminarEmpleado(empleadoId)) {
                            JOptionPane.showMessageDialog(PanelEmpleados.this, 
                                "Empleado eliminado correctamente", 
                                "Eliminación exitosa", JOptionPane.INFORMATION_MESSAGE);
                            cargarDatosEmpleados();
                        } else {
                            JOptionPane.showMessageDialog(PanelEmpleados.this, 
                                "Error al eliminar el empleado", 
                                "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(PanelEmpleados.this, 
                        "Por favor, seleccione un empleado para eliminar", 
                        "Selección requerida", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        
        botonBuscar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String terminoBusqueda = campoBusqueda.getText().trim();
                if (!terminoBusqueda.isEmpty()) {
                    List<Empleado> empleados = empleadoDAO.buscarEmpleados(terminoBusqueda);
                    actualizarTablaConEmpleados(empleados);
                } else {
                    cargarDatosEmpleados();
                }
            }
        });
        
        // Cargar datos iniciales
        cargarDatosEmpleados();
    }
    
    private void cargarDatosEmpleados() {
        List<Empleado> empleados = empleadoDAO.obtenerTodosEmpleados();
        actualizarTablaConEmpleados(empleados);
    }
    
    private void actualizarTablaConEmpleados(List<Empleado> empleados) {
        modeloTabla.setRowCount(0); // Limpiar tabla
        
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        
        for (Empleado empleado : empleados) {
            String fechaContratacion = empleado.getFechaContratacion() != null ? formatoFecha.format(empleado.getFechaContratacion()) : "";
            
            modeloTabla.addRow(new Object[]{
                empleado.getId(),
                empleado.getNombre(),
                empleado.getApellido(),
                empleado.getCorreo(),
                empleado.getTelefono(),
                empleado.getDepartamento(),
                empleado.getCargo(),
                fechaContratacion
            });
        }
    }
    
    private void mostrarDialogoEmpleado(Empleado empleado) {
        // Crear un diálogo para agregar/editar empleado
        final JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            empleado == null ? "Agregar Empleado" : "Editar Empleado", true);
        
        dialogo.setSize(500, 500);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout());
        
        // Panel de formulario
        JPanel panelFormulario = new JPanel(new GridLayout(10, 2, 10, 10));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Campos del formulario
        JTextField campoNombre = new JTextField(20);
        JTextField campoApellido = new JTextField(20);
        JTextField campoCorreo = new JTextField(20);
        JTextField campoTelefono = new JTextField(20);
        JTextField campoDireccion = new JTextField(20);
        JTextField campoDepartamento = new JTextField(20);
        JTextField campoCargo = new JTextField(20);
        JTextField campoSalario = new JTextField(20);
        
        // Selector de fecha para la fecha de contratación
        JSpinner selectorFechaContratacion = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editorFecha = new JSpinner.DateEditor(selectorFechaContratacion, "dd/MM/yyyy");
        selectorFechaContratacion.setEditor(editorFecha);
        
        // Selector de perfil
        JComboBox<String> comboPerfiles = new JComboBox<>();
        List<Perfil> perfiles = perfilDAO.obtenerTodosPerfiles();
        for (Perfil perfil : perfiles) {
            comboPerfiles.addItem(perfil.getNombre() + " (ID: " + perfil.getId() + ")");
        }
        
        // Si estamos editando, establecer los valores actuales
        if (empleado != null) {
            campoNombre.setText(empleado.getNombre());
            campoApellido.setText(empleado.getApellido());
            campoCorreo.setText(empleado.getCorreo());
            campoTelefono.setText(empleado.getTelefono());
            campoDireccion.setText(empleado.getDireccion());
            campoDepartamento.setText(empleado.getDepartamento());
            campoCargo.setText(empleado.getCargo());
            campoSalario.setText(String.valueOf(empleado.getSalario()));
            
            if (empleado.getFechaContratacion() != null) {
                selectorFechaContratacion.setValue(empleado.getFechaContratacion());
            }
            
            // Seleccionar el perfil correcto
            for (int i = 0; i < perfiles.size(); i++) {
                if (perfiles.get(i).getId() == empleado.getPerfilId()) {
                    comboPerfiles.setSelectedIndex(i);
                    break;
                }
            }
        }
        
        // Agregar campos al panel
        panelFormulario.add(new JLabel("Nombre:"));
        panelFormulario.add(campoNombre);
        panelFormulario.add(new JLabel("Apellido:"));
        panelFormulario.add(campoApellido);
        panelFormulario.add(new JLabel("Correo:"));
        panelFormulario.add(campoCorreo);
        panelFormulario.add(new JLabel("Teléfono:"));
        panelFormulario.add(campoTelefono);
        panelFormulario.add(new JLabel("Dirección:"));
        panelFormulario.add(campoDireccion);
        panelFormulario.add(new JLabel("Departamento:"));
        panelFormulario.add(campoDepartamento);
        panelFormulario.add(new JLabel("Cargo:"));
        panelFormulario.add(campoCargo);
        panelFormulario.add(new JLabel("Fecha de Contratación:"));
        panelFormulario.add(selectorFechaContratacion);
        panelFormulario.add(new JLabel("Salario:"));
        panelFormulario.add(campoSalario);
        panelFormulario.add(new JLabel("Perfil:"));
        panelFormulario.add(comboPerfiles);
        
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
                if (campoNombre.getText().trim().isEmpty() || campoApellido.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialogo, 
                        "Los campos Nombre y Apellido son obligatorios", 
                        "Campos requeridos", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                try {
                    // Validar salario
                    double salario = Double.parseDouble(campoSalario.getText().trim());
                    if (salario < 0) {
                        JOptionPane.showMessageDialog(dialogo, 
                            "El salario debe ser un número positivo", 
                            "Valor inválido", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // Crear o actualizar el objeto Empleado
                    Empleado emp = empleado == null ? new Empleado() : empleado;
                    emp.setNombre(campoNombre.getText().trim());
                    emp.setApellido(campoApellido.getText().trim());
                    emp.setCorreo(campoCorreo.getText().trim());
                    emp.setTelefono(campoTelefono.getText().trim());
                    emp.setDireccion(campoDireccion.getText().trim());
                    emp.setDepartamento(campoDepartamento.getText().trim());
                    emp.setCargo(campoCargo.getText().trim());
                    emp.setFechaContratacion((Date) selectorFechaContratacion.getValue());
                    emp.setSalario(salario);
                    
                    // Obtener el ID del perfil seleccionado
                    String perfilSeleccionado = (String) comboPerfiles.getSelectedItem();
                    int perfilId = Integer.parseInt(perfilSeleccionado.substring(perfilSeleccionado.lastIndexOf("ID: ") + 4, perfilSeleccionado.length() - 1));
                    emp.setPerfilId(perfilId);
                    
                    boolean exito;
                    if (empleado == null) {
                        // Agregar nuevo empleado
                        exito = empleadoDAO.agregarEmpleado(emp);
                    } else {
                        // Actualizar empleado existente
                        exito = empleadoDAO.actualizarEmpleado(emp);
                    }
                    
                    if (exito) {
                        JOptionPane.showMessageDialog(dialogo, 
                            "Empleado guardado correctamente", 
                            "Operación exitosa", JOptionPane.INFORMATION_MESSAGE);
                        dialogo.dispose();
                        cargarDatosEmpleados();
                    } else {
                        JOptionPane.showMessageDialog(dialogo, 
                            "Error al guardar el empleado", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialogo, 
                        "El salario debe ser un número válido", 
                        "Valor inválido", JOptionPane.WARNING_MESSAGE);
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
