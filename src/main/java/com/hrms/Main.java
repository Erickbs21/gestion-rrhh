package com.hrms;

import com.hrms.ui.VentanaPrincipal;
import com.hrms.util.ConexionBD;

import javax.swing.*;
import java.awt.*;

/**
 * Clase principal que inicia la aplicación
 */
public class Main {
    public static void main(String[] args) {
        // Configurar look and feel nativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Iniciar la aplicación en el Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Mostrar pantalla de carga
                JWindow pantallaInicio = mostrarPantallaInicio();
                
                // Comprobar conexión a la base de datos
                if (!ConexionBD.probarConexion()) {
                    pantallaInicio.dispose();
                    JOptionPane.showMessageDialog(null, 
                        "No se pudo conectar con la base de datos. Por favor, verifique la configuración.",
                        "Error de Conexión", 
                        JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
                
                // Crear y mostrar la ventana principal
                VentanaPrincipal ventanaPrincipal = new VentanaPrincipal();
                
                // Cerrar pantalla de inicio y mostrar ventana principal
                pantallaInicio.dispose();
                ventanaPrincipal.setVisible(true);
            }
        });
    }
    
    /**
     * Muestra una pantalla de carga al iniciar la aplicación
     */
    private static JWindow mostrarPantallaInicio() {
        JWindow ventana = new JWindow();
        ventana.setSize(400, 300);
        ventana.setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        
        JLabel titulo = new JLabel("Sistema de Gestión de Recursos Humanos", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titulo, BorderLayout.NORTH);
        
        JLabel imagen = new JLabel(new ImageIcon("recursos/splash.png")); // Puedes crear esta imagen
        panel.add(imagen, BorderLayout.CENTER);
        
        JProgressBar barraProgreso = new JProgressBar();
        barraProgreso.setIndeterminate(true);
        panel.add(barraProgreso, BorderLayout.SOUTH);
        
        ventana.add(panel);
        ventana.setVisible(true);
        
        return ventana;
    }
}
