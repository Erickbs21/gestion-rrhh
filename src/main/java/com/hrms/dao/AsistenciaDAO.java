package com.hrms.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.hrms.model.Asistencia;
import com.hrms.util.ConexionBD;

/**
 * Clase DAO para operaciones CRUD de asistencia
 */
public class AsistenciaDAO {
    
    // Método para obtener todos los registros de asistencia
    public List<Asistencia> obtenerTodasAsistencias() {
        List<Asistencia> listaAsistencias = new ArrayList<>();
        
        try (Connection conn = ConexionBD.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM asistencias ORDER BY fecha DESC, hora_entrada DESC")) {
            
            while (rs.next()) {
                Asistencia asistencia = mapResultSetToAsistencia(rs);
                listaAsistencias.add(asistencia);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return listaAsistencias;
    }
    
    // Método para obtener los registros de asistencia de un empleado específico
    public List<Asistencia> obtenerAsistenciasPorEmpleadoId(int empleadoId) {
        List<Asistencia> listaAsistencias = new ArrayList<>();
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT * FROM asistencias WHERE empleado_id = ? ORDER BY fecha DESC, hora_entrada DESC")) {
            
            stmt.setInt(1, empleadoId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Asistencia asistencia = mapResultSetToAsistencia(rs);
                    listaAsistencias.add(asistencia);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return listaAsistencias;
    }
    
    // Método para obtener los registros de asistencia por fecha
    public List<Asistencia> obtenerAsistenciasPorFecha(Date fecha) {
        List<Asistencia> listaAsistencias = new ArrayList<>();
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT * FROM asistencias WHERE fecha = ? ORDER BY empleado_id")) {
            
            stmt.setDate(1, fecha);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Asistencia asistencia = mapResultSetToAsistencia(rs);
                    listaAsistencias.add(asistencia);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return listaAsistencias;
    }
    
    // Método para registrar una entrada
    public boolean registrarEntrada(int empleadoId) {
        String sql = "INSERT INTO asistencias (empleado_id, fecha, hora_entrada, estado) VALUES (?, CURDATE(), CURTIME(), 'Presente')";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, empleadoId);
            
            int filasAfectadas = stmt.executeUpdate();
            
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Método para registrar una salida
    public boolean registrarSalida(int asistenciaId) {
        String sql = "UPDATE asistencias SET hora_salida = CURTIME() WHERE id = ? AND hora_salida IS NULL";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, asistenciaId);
            
            int filasAfectadas = stmt.executeUpdate();
            
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Método para obtener el último registro de asistencia de un empleado
    public Asistencia obtenerUltimaAsistenciaPorEmpleadoId(int empleadoId) {
        Asistencia asistencia = null;
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT * FROM asistencias WHERE empleado_id = ? ORDER BY fecha DESC, hora_entrada DESC LIMIT 1")) {
            
            stmt.setInt(1, empleadoId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    asistencia = mapResultSetToAsistencia(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return asistencia;
    }
    
    // Método para actualizar el estado de asistencia
    public boolean actualizarEstadoAsistencia(int asistenciaId, String estado, String notas) {
        String sql = "UPDATE asistencias SET estado = ?, notas = ? WHERE id = ?";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, estado);
            stmt.setString(2, notas);
            stmt.setInt(3, asistenciaId);
            
            int filasAfectadas = stmt.executeUpdate();
            
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Método para agregar un registro de asistencia completo
    public boolean agregarAsistencia(Asistencia asistencia) {
        String sql = "INSERT INTO asistencias (empleado_id, fecha, hora_entrada, hora_salida, estado, notas) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, asistencia.getEmpleadoId());
            stmt.setDate(2, new java.sql.Date(asistencia.getFecha().getTime()));
            stmt.setTime(3, asistencia.getHoraEntrada());
            stmt.setTime(4, asistencia.getHoraSalida());
            stmt.setString(5, asistencia.getEstado());
            stmt.setString(6, asistencia.getNotas());
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas == 0) {
                return false;
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    asistencia.setId(generatedKeys.getInt(1));
                }
            }
            
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Método auxiliar para mapear un ResultSet a un objeto Asistencia
    private Asistencia mapResultSetToAsistencia(ResultSet rs) throws SQLException {
        Asistencia asistencia = new Asistencia();
        asistencia.setId(rs.getInt("id"));
        asistencia.setEmpleadoId(rs.getInt("empleado_id"));
        asistencia.setFecha(rs.getDate("fecha"));
        asistencia.setHoraEntrada(rs.getTime("hora_entrada"));
        asistencia.setHoraSalida(rs.getTime("hora_salida"));
        asistencia.setEstado(rs.getString("estado"));
        asistencia.setNotas(rs.getString("notas"));
        
        return asistencia;
    }
}
