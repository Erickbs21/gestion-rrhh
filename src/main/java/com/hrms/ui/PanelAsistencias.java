package com.hrms.ui;

import com.hrms.dao.AsistenciaDAO;
import com.hrms.dao.EmpleadoDAO;
import com.hrms.model.Asistencia;
import com.hrms.model.Empleado;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Panel para el control de asistencia
 */
public class PanelAsistencias extends JPanel {
    private JTable tablaAsistencias;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> comboEmpleados;
    private JButton botonRegistrarEntrada, botonRegistrarSalida, botonReporte, botonRegistroManual;
    private JSpinner selectorFecha;
    private AsistenciaDAO asistenciaDAO;
    private EmpleadoDAO empleadoDAO;
    
    public PanelAsistencias() {
        // Inicializar DAOs
        asistenciaDAO = new AsistenciaDAO();
        empleadoDAO = new EmpleadoDAO();
        
        // Configurar el panel
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel superior para selección de empleado y registro
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // Selector de empleado
        panelSuperior.add(new JLabel("Empleado:"));
        comboEmpleados = new JComboBox<>();
        panelSuperior.add(comboEmpleados);
        
        // Botones de registro
        botonRegistrarEntrada = new JButton("Registrar Entrada");
        botonRegistrarSalida = new JButton("Registrar Salida");
        panelSuperior.add(botonRegistrarEntrada);
        panelSuperior.add(botonRegistrarSalida);
        
        // Selector de fecha
        panelSuperior.add(new JLabel("Fecha:"));
        SpinnerDateModel modeloFecha = new SpinnerDateModel();
        selectorFecha = new JSpinner(modeloFecha);
        JSpinner.DateEditor editorFecha = new JSpinner.DateEditor(selectorFecha, "dd/MM/yyyy");
        selectorFecha.setEditor(editorFecha);
        selectorFecha.setValue(new Date()); // Fecha actual
        panelSuperior.add(selectorFecha);
        
        // Botones adicionales
        botonReporte = new JButton("Generar Reporte");
        botonRegistroManual = new JButton("Registro Manual");
        panelSuperior.add(botonReporte);
        panelSuperior.add(botonRegistroManual);
        
        add(panelSuperior, BorderLayout.NORTH);
        
        // Tabla de asistencias
        String[] nombreColumnas = {"ID", "Empleado", "Fecha", "Hora Entrada", "Hora Salida", "Estado", "Notas"};
        modeloTabla = new DefaultTableModel(nombreColumnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer la tabla no editable
            }
        };
        
        tablaAsistencias = new JTable(modeloTabla);
        tablaAsistencias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaAsistencias.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane panelDesplazamiento = new JScrollPane(tablaAsistencias);
        add(panelDesplazamiento, BorderLayout.CENTER);
        
        // Agregar listeners
        botonRegistrarEntrada.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarEntrada();
            }
        });
        
        botonRegistrarSalida.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarSalida();
            }
        });
        
        botonReporte.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generarReporteAsistencia();
            }
        });
        
        botonRegistroManual.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarDialogoAsistenciaManual();
            }
        });
        
        selectorFecha.addChangeListener(e -> {
            Date fechaSeleccionada = (Date) selectorFecha.getValue();
            cargarAsistenciasPorFecha(new java.sql.Date(fechaSeleccionada.getTime()));
        });
        
        // Cargar datos iniciales
        cargarListaEmpleados();
        cargarAsistenciasPorFecha(new java.sql.Date(new Date().getTime()));
    }
    
    private void cargarListaEmpleados() {
        comboEmpleados.removeAllItems();
        
        List<Empleado> empleados = empleadoDAO.obtenerTodosEmpleados();
        for (Empleado empleado : empleados) {
            comboEmpleados.addItem(empleado.getNombreCompleto() + " (ID: " + empleado.getId() + ")");
        }
    }
    
    private void cargarAsistenciasPorFecha(java.sql.Date fecha) {
        List<Asistencia> listaAsistencias = asistenciaDAO.obtenerAsistenciasPorFecha(fecha);
        actualizarTablaConAsistencias(listaAsistencias);
    }
    
    private void actualizarTablaConAsistencias(List<Asistencia> listaAsistencias) {
        modeloTabla.setRowCount(0); // Limpiar tabla
        
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");
        
        for (Asistencia asistencia : listaAsistencias) {
            Empleado empleado = empleadoDAO.obtenerEmpleadoPorId(asistencia.getEmpleadoId());
            String nombreEmpleado = empleado != null ? empleado.getNombreCompleto() : "Desconocido";
            
            String fechaStr = asistencia.getFecha() != null ? formatoFecha.format(asistencia.getFecha()) : "";
            String horaEntradaStr = asistencia.getHoraEntrada() != null ? formatoHora.format(asistencia.getHoraEntrada()) : "";
            String horaSalidaStr = asistencia.getHoraSalida() != null ? formatoHora.format(asistencia.getHoraSalida()) : "";
            
            modeloTabla.addRow(new Object[]{
                asistencia.getId(),
                nombreEmpleado,
                fechaStr,
                horaEntradaStr,
                horaSalidaStr,
                asistencia.getEstado(),
                asistencia.getNotas()
            });
        }
    }
    
    private void registrarEntrada() {
        if (comboEmpleados.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, seleccione un empleado", 
                "Selección requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Obtener el ID del empleado seleccionado
        String empleadoSeleccionado = (String) comboEmpleados.getSelectedItem();
        int empleadoId = Integer.parseInt(empleadoSeleccionado.substring(empleadoSeleccionado.lastIndexOf("ID: ") + 4, empleadoSeleccionado.length() - 1));
        
        // Verificar si ya hay un registro de entrada sin salida para hoy
        Asistencia ultimaAsistencia = asistenciaDAO.obtenerUltimaAsistenciaPorEmpleadoId(empleadoId);
        if (ultimaAsistencia != null && ultimaAsistencia.getFecha().equals(new java.sql.Date(new Date().getTime())) && ultimaAsistencia.getHoraSalida() == null) {
            JOptionPane.showMessageDialog(this, 
                "El empleado ya tiene un registro de entrada sin salida para hoy", 
                "Registro duplicado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Registrar entrada
        if (asistenciaDAO.registrarEntrada(empleadoId)) {
            JOptionPane.showMessageDialog(this, 
                "Entrada registrada correctamente", 
                "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
            
            // Recargar datos
            cargarAsistenciasPorFecha(new java.sql.Date(new Date().getTime()));
        } else {
            JOptionPane.showMessageDialog(this, 
                "Error al registrar la entrada", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void registrarSalida() {
        if (comboEmpleados.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, seleccione un empleado", 
                "Selección requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Obtener el ID del empleado seleccionado
        String empleadoSeleccionado = (String) comboEmpleados.getSelectedItem();
        int empleadoId = Integer.parseInt(empleadoSeleccionado.substring(empleadoSeleccionado.lastIndexOf("ID: ") + 4, empleadoSeleccionado.length() - 1));
        
        // Buscar el último registro de entrada sin salida
        Asistencia ultimaAsistencia = asistenciaDAO.obtenerUltimaAsistenciaPorEmpleadoId(empleadoId);
        if (ultimaAsistencia == null || ultimaAsistencia.getHoraSalida() != null) {
            JOptionPane.showMessageDialog(this, 
                "No hay un registro de entrada sin salida para este empleado", 
                "Registro no encontrado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Registrar salida
        if (asistenciaDAO.registrarSalida(ultimaAsistencia.getId())) {
            JOptionPane.showMessageDialog(this, 
                "Salida registrada correctamente", 
                "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
            
            // Recargar datos
            cargarAsistenciasPorFecha(new java.sql.Date(new Date().getTime()));
        } else {
            JOptionPane.showMessageDialog(this, 
                "Error al registrar la salida", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void generarReporteAsistencia() {
        // Implementar lógica para generar reportes
        JFileChooser selectorArchivo = new JFileChooser();
        selectorArchivo.setDialogTitle("Guardar Reporte de Asistencia");
        
        if (selectorArchivo.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(this, 
                "Reporte generado correctamente en: " + selectorArchivo.getSelectedFile().getPath(), 
                "Reporte Generado", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void mostrarDialogoAsistenciaManual() {
        // Crear un diálogo para registro manual de asistencia
        final JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Registro Manual de Asistencia", true);
        
        dialogo.setSize(500, 350);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout());
        
        // Panel de formulario
        JPanel panelFormulario = new JPanel(new GridLayout(6, 2, 10, 10));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Selector de empleado
        JComboBox<String> comboEmpleado = new JComboBox<>();
        List<Empleado> empleados = empleadoDAO.obtenerTodosEmpleados();
        for (Empleado empleado : empleados) {
            comboEmpleado.addItem(empleado.getNombreCompleto() + " (ID: " + empleado.getId() + ")");
        }
        
        // Selector de fecha
        JSpinner selectorFechaAsistencia = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editorFecha = new JSpinner.DateEditor(selectorFechaAsistencia, "dd/MM/yyyy");
        selectorFechaAsistencia.setEditor(editorFecha);
        selectorFechaAsistencia.setValue(new Date()); // Fecha actual
        
        // Selectores de hora
        SpinnerDateModel modeloHoraEntrada = new SpinnerDateModel();
        JSpinner selectorHoraEntrada = new JSpinner(modeloHoraEntrada);
        JSpinner.DateEditor editorHoraEntrada = new JSpinner.DateEditor(selectorHoraEntrada, "HH:mm:ss");
        selectorHoraEntrada.setEditor(editorHoraEntrada);
        
        SpinnerDateModel modeloHoraSalida = new SpinnerDateModel();
        JSpinner selectorHoraSalida = new JSpinner(modeloHoraSalida);
        JSpinner.DateEditor editorHoraSalida = new JSpinner.DateEditor(selectorHoraSalida, "HH:mm:ss");
        selectorHoraSalida.setEditor(editorHoraSalida);
        
        // Selector de estado
        String[] opcionesEstado = {"Presente", "Ausente", "Tardanza", "Permiso"};
        JComboBox<String> comboEstado = new JComboBox<>(opcionesEstado);
        
        // Campo de notas
        JTextField campoNotas = new JTextField(20);
        
        // Agregar campos al panel
        panelFormulario.add(new JLabel("Empleado:"));
        panelFormulario.add(comboEmpleado);
        panelFormulario.add(new JLabel("Fecha:"));
        panelFormulario.add(selectorFechaAsistencia);
        panelFormulario.add(new JLabel("Hora Entrada:"));
        panelFormulario.add(selectorHoraEntrada);
        panelFormulario.add(new JLabel("Hora Salida:"));
        panelFormulario.add(selectorHoraSalida);
        panelFormulario.add(new JLabel("Estado:"));
        panelFormulario.add(comboEstado);
        panelFormulario.add(new JLabel("Notas:"));
        panelFormulario.add(campoNotas);
        
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
                try {
                    // Obtener el ID del empleado seleccionado
                    String empleadoSeleccionado = (String) comboEmpleado.getSelectedItem();
                    int empleadoId = Integer.parseInt(empleadoSeleccionado.substring(empleadoSeleccionado.lastIndexOf("ID: ") + 4, empleadoSeleccionado.length() - 1));
                    
                    // Crear objeto Asistencia
                    Asistencia asistencia = new Asistencia();
                    asistencia.setEmpleadoId(empleadoId);
                    asistencia.setFecha(new java.sql.Date(((Date) selectorFechaAsistencia.getValue()).getTime()));
                    
                    // Obtener horas de los spinners
                    Calendar calEntrada = Calendar.getInstance();
                    calEntrada.setTime((Date) selectorHoraEntrada.getValue());
                    asistencia.setHoraEntrada(new Time(calEntrada.get(Calendar.HOUR_OF_DAY), calEntrada.get(Calendar.MINUTE), calEntrada.get(Calendar.SECOND)));
                    
                    Calendar calSalida = Calendar.getInstance();
                    calSalida.setTime((Date) selectorHoraSalida.getValue());
                    asistencia.setHoraSalida(new Time(calSalida.get(Calendar.HOUR_OF_DAY), calSalida.get(Calendar.MINUTE), calSalida.get(Calendar.SECOND)));
                    
                    asistencia.setEstado((String) comboEstado.getSelectedItem());
                    asistencia.setNotas(campoNotas.getText().trim());
                    
                    // Guardar asistencia
                    if (asistenciaDAO.agregarAsistencia(asistencia)) {
                        JOptionPane.showMessageDialog(dialogo, 
                            "Asistencia registrada correctamente", 
                            "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
                        dialogo.dispose();
                        
                        // Recargar datos
                        cargarAsistenciasPorFecha(new java.sql.Date(((Date) selectorFecha.getValue()).getTime()));
                    } else {
                        JOptionPane.showMessageDialog(dialogo, 
                            "Error al registrar la asistencia", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialogo, 
                        "Error en los datos ingresados: " + ex.getMessage(), 
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
