package com.hrms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.hrms.model.Empleado;
import com.hrms.util.ConexionBD;

/**
 * Clase DAO para operaciones CRUD de empleados
 */
public class EmpleadoDAO {
    
    // Método para obtener todos los empleados
    public List<Empleado> obtenerTodosEmpleados() {
        List<Empleado> empleados = new ArrayList<>();
        
        try (Connection conn = ConexionBD.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM empleados ORDER BY apellido, nombre")) {
            
            while (rs.next()) {
                Empleado empleado = mapResultSetToEmpleado(rs);
                empleados.add(empleado);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return empleados;
    }
    
    // Método para obtener un empleado por su ID
    public Empleado obtenerEmpleadoPorId(int id) {
        Empleado empleado = null;
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM empleados WHERE id = ?")) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    empleado = mapResultSetToEmpleado(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return empleado;
    }
    
    // Método para buscar empleados por nombre o departamento
    public List<Empleado> buscarEmpleados(String terminoBusqueda) {
        List<Empleado> empleados = new ArrayList<>();
        String patronBusqueda = "%" + terminoBusqueda + "%";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT * FROM empleados WHERE nombre LIKE ? OR apellido LIKE ? OR departamento LIKE ? ORDER BY apellido, nombre")) {
            
            stmt.setString(1, patronBusqueda);
            stmt.setString(2, patronBusqueda);
            stmt.setString(3, patronBusqueda);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Empleado empleado = mapResultSetToEmpleado(rs);
                    empleados.add(empleado);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return empleados;
    }
    
    // Método para agregar un nuevo empleado
    public boolean agregarEmpleado(Empleado empleado) {
        String sql = "INSERT INTO empleados (nombre, apellido, correo, telefono, direccion, " +
                    "departamento, cargo, fecha_contratacion, salario, perfil_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // Establecer los parámetros
            stmt.setString(1, empleado.getNombre());
            stmt.setString(2, empleado.getApellido());
            stmt.setString(3, empleado.getCorreo());
            stmt.setString(4, empleado.getTelefono());
            stmt.setString(5, empleado.getDireccion());
            stmt.setString(6, empleado.getDepartamento());
            stmt.setString(7, empleado.getCargo());
            stmt.setDate(8, new java.sql.Date(empleado.getFechaContratacion().getTime()));
            stmt.setDouble(9, empleado.getSalario());
            stmt.setInt(10, empleado.getPerfilId());
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas == 0) {
                return false;
            }
            
            // Obtener el ID generado
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    empleado.setId(generatedKeys.getInt(1));
                }
            }
            
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Método para actualizar un empleado existente
    public boolean actualizarEmpleado(Empleado empleado) {
        String sql = "UPDATE empleados SET nombre = ?, apellido = ?, correo = ?, " +
                    "telefono = ?, direccion = ?, departamento = ?, cargo = ?, " +
                    "fecha_contratacion = ?, salario = ?, perfil_id = ? WHERE id = ?";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Establecer los parámetros
            stmt.setString(1, empleado.getNombre());
            stmt.setString(2, empleado.getApellido());
            stmt.setString(3, empleado.getCorreo());
            stmt.setString(4, empleado.getTelefono());
            stmt.setString(5, empleado.getDireccion());
            stmt.setString(6, empleado.getDepartamento());
            stmt.setString(7, empleado.getCargo());
            stmt.setDate(8, new java.sql.Date(empleado.getFechaContratacion().getTime()));
            stmt.setDouble(9, empleado.getSalario());
            stmt.setInt(10, empleado.getPerfilId());
            stmt.setInt(11, empleado.getId());
            
            int filasAfectadas = stmt.executeUpdate();
            
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Método para eliminar un empleado
    public boolean eliminarEmpleado(int id) {
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM empleados WHERE id = ?")) {
            
            stmt.setInt(1, id);
            
            int filasAfectadas = stmt.executeUpdate();
            
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Método auxiliar para mapear un ResultSet a un objeto Empleado
    private Empleado mapResultSetToEmpleado(ResultSet rs) throws SQLException {
        Empleado empleado = new Empleado();
        empleado.setId(rs.getInt("id"));
        empleado.setNombre(rs.getString("nombre"));
        empleado.setApellido(rs.getString("apellido"));
        empleado.setCorreo(rs.getString("correo"));
        empleado.setTelefono(rs.getString("telefono"));
        empleado.setDireccion(rs.getString("direccion"));
        empleado.setDepartamento(rs.getString("departamento"));
        empleado.setCargo(rs.getString("cargo"));
        empleado.setFechaContratacion(rs.getDate("fecha_contratacion"));
        empleado.setSalario(rs.getDouble("salario"));
        empleado.setPerfilId(rs.getInt("perfil_id"));
        
        return empleado;
    }
}
