package com.hrms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.hrms.model.Perfil;
import com.hrms.util.ConexionBD;

/**
 * Clase DAO para operaciones CRUD de perfiles
 */
public class PerfilDAO {
    
    // Método para obtener todos los perfiles
    public List<Perfil> obtenerTodosPerfiles() {
        List<Perfil> perfiles = new ArrayList<>();
        
        try (Connection conn = ConexionBD.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM perfiles ORDER BY nombre")) {
            
            while (rs.next()) {
                Perfil perfil = mapResultSetToPerfil(rs);
                perfiles.add(perfil);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return perfiles;
    }
    
    // Método para obtener un perfil por su ID
    public Perfil obtenerPerfilPorId(int id) {
        Perfil perfil = null;
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM perfiles WHERE id = ?")) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    perfil = mapResultSetToPerfil(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return perfil;
    }
    
    // Método para agregar un nuevo perfil
    public boolean agregarPerfil(Perfil perfil) {
        String sql = "INSERT INTO perfiles (nombre, descripcion, permisos) VALUES (?, ?, ?)";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, perfil.getNombre());
            stmt.setString(2, perfil.getDescripcion());
            stmt.setString(3, perfil.getPermisos());
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas == 0) {
                return false;
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    perfil.setId(generatedKeys.getInt(1));
                }
            }
            
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Método para actualizar un perfil existente
    public boolean actualizarPerfil(Perfil perfil) {
        String sql = "UPDATE perfiles SET nombre = ?, descripcion = ?, permisos = ? WHERE id = ?";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, perfil.getNombre());
            stmt.setString(2, perfil.getDescripcion());
            stmt.setString(3, perfil.getPermisos());
            stmt.setInt(4, perfil.getId());
            
            int filasAfectadas = stmt.executeUpdate();
            
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Método para eliminar un perfil
    public boolean eliminarPerfil(int id) {
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM perfiles WHERE id = ?")) {
            
            stmt.setInt(1, id);
            
            int filasAfectadas = stmt.executeUpdate();
            
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Método para asignar un perfil a un empleado
    public boolean asignarPerfilAEmpleado(int perfilId, int empleadoId) {
        String sql = "UPDATE empleados SET perfil_id = ? WHERE id = ?";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, perfilId);
            stmt.setInt(2, empleadoId);
            
            int filasAfectadas = stmt.executeUpdate();
            
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Método auxiliar para mapear un ResultSet a un objeto Perfil
    private Perfil mapResultSetToPerfil(ResultSet rs) throws SQLException {
        Perfil perfil = new Perfil();
        perfil.setId(rs.getInt("id"));
        perfil.setNombre(rs.getString("nombre"));
        perfil.setDescripcion(rs.getString("descripcion"));
        perfil.setPermisos(rs.getString("permisos"));
        
        return perfil;
    }
}
