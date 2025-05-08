package com.hrms.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Ventana principal de la aplicación
 */
public class VentanaPrincipal extends JFrame {
    private JTabbedPane panelPestanas;
    
    public VentanaPrincipal() {
        // Configurar la ventana principal
        setTitle("Sistema de Gestión de Recursos Humanos");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Inicializar componentes
        inicializarComponentes();
        
        // Agregar listener para confirmar salida
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirmar = JOptionPane.showConfirmDialog(
                    VentanaPrincipal.this,
                    "¿Está seguro de que desea salir de la aplicación?",
                    "Confirmar salida",
                    JOptionPane.YES_NO_OPTION);
                
                if (confirmar == JOptionPane.YES_OPTION) {
                    dispose();
                    System.exit(0);
                }
            }
        });
    }
    
    private void inicializarComponentes() {
        // Panel de pestañas principal
        panelPestanas = new JTabbedPane();
        
        // Agregar los diferentes módulos como pestañas
        panelPestanas.addTab("Gestión de Empleados", new PanelEmpleados());
        panelPestanas.addTab("Control de Asistencia", new PanelAsistencias());
        panelPestanas.addTab("Administración de Perfiles", new PanelPerfiles());
        
        // Agregar el panel de pestañas al frame
        add(panelPestanas, BorderLayout.CENTER);
        
        // Barra de menú
        JMenuBar barraMenu = new JMenuBar();
        
        // Menú Archivo
        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.addActionListener(e -> {
            int confirmar = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de que desea salir de la aplicación?",
                "Confirmar salida",
                JOptionPane.YES_NO_OPTION);
            
            if (confirmar == JOptionPane.YES_OPTION) {
                dispose();
                System.exit(0);
            }
        });
        menuArchivo.add(itemSalir);
        
        // Menú Reportes
        JMenu menuReportes = new JMenu("Reportes");
        JMenuItem itemReporteEmpleados = new JMenuItem("Reporte de Empleados");
        itemReporteEmpleados.addActionListener(e -> JOptionPane.showMessageDialog(this, 
            "Funcionalidad de reporte de empleados en desarrollo", 
            "Información", JOptionPane.INFORMATION_MESSAGE));
        
        JMenuItem itemReporteAsistencia = new JMenuItem("Reporte de Asistencia");
        itemReporteAsistencia.addActionListener(e -> JOptionPane.showMessageDialog(this, 
            "Funcionalidad de reporte de asistencia en desarrollo", 
            "Información", JOptionPane.INFORMATION_MESSAGE));
        
        menuReportes.add(itemReporteEmpleados);
        menuReportes.add(itemReporteAsistencia);
        
        // Menú Ayuda
        JMenu menuAyuda = new JMenu("Ayuda");
        JMenuItem itemAcercaDe = new JMenuItem("Acerca de");
        itemAcercaDe.addActionListener(e -> JOptionPane.showMessageDialog(this, 
            "Sistema de Gestión de HRMS v1.0\nDesarrollado para el curso", 
            "Acerca de", JOptionPane.INFORMATION_MESSAGE));
        menuAyuda.add(itemAcercaDe);
        
        // Agregar menús a la barra
        barraMenu.add(menuArchivo);
        barraMenu.add(menuReportes);
        barraMenu.add(menuAyuda);
        
        // Establecer la barra de menú
        setJMenuBar(barraMenu);
    }
}

