package com.hrms.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase utilitaria para gestionar la conexión a la base de datos
 */
public class ConexionBD {
    // Configuración de la conexión a la base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/bd_hrms";
    private static final String USUARIO = "root";
    private static final String CLAVE = ""; // Cambia esto por tu contraseña de MySQL
    
    // Método para obtener una conexión a la base de datos
    public static Connection obtenerConexion() throws SQLException {
        try {
            // Cargar el driver de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establecer y devolver la conexión
            return DriverManager.getConnection(URL, USUARIO, CLAVE);
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se pudo cargar el driver de MySQL", e);
        }
    }
    
    // Método para probar la conexión a la base de datos
    public static boolean probarConexion() {
        try (Connection conn = obtenerConexion()) {
            return conn != null;
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
            return false;
        }
    }
}
