package com.hrms.model;

import java.sql.Time;
import java.util.Date;

/**
 * Clase que representa un registro de asistencia en el sistema
 */
public class Asistencia {
    private int id;
    private int empleadoId;
    private Date fecha;
    private Time horaEntrada;
    private Time horaSalida;
    private String estado; // "Presente", "Ausente", "Tardanza", etc.
    private String notas;
    
    // Constructor vacío
    public Asistencia() {
    }
    
    // Constructor sin id
    public Asistencia(int empleadoId, Date fecha, Time horaEntrada, Time horaSalida, 
                     String estado, String notas) {
        this.empleadoId = empleadoId;
        this.fecha = fecha;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
        this.estado = estado;
        this.notas = notas;
    }
    
    // Constructor completo
    public Asistencia(int id, int empleadoId, Date fecha, Time horaEntrada, Time horaSalida, 
                     String estado, String notas) {
        this.id = id;
        this.empleadoId = empleadoId;
        this.fecha = fecha;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
        this.estado = estado;
        this.notas = notas;
    }
    
    // Getters y setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getEmpleadoId() {
        return empleadoId;
    }
    
    public void setEmpleadoId(int empleadoId) {
        this.empleadoId = empleadoId;
    }
    
    public Date getFecha() {
        return fecha;
    }
    
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    
    public Time getHoraEntrada() {
        return horaEntrada;
    }
    
    public void setHoraEntrada(Time horaEntrada) {
        this.horaEntrada = horaEntrada;
    }
    
    public Time getHoraSalida() {
        return horaSalida;
    }
    
    public void setHoraSalida(Time horaSalida) {
        this.horaSalida = horaSalida;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public String getNotas() {
        return notas;
    }
    
    public void setNotas(String notas) {
        this.notas = notas;
    }
    
    // Método para calcular las horas trabajadas
    public double getHorasTrabajadas() {
        if (horaEntrada == null || horaSalida == null) {
            return 0.0;
        }
        
        // Convertir a milisegundos y calcular la diferencia
        long diffMillis = horaSalida.getTime() - horaEntrada.getTime();
        
        // Convertir a horas
        return diffMillis / (1000.0 * 60 * 60);
    }
    
    @Override
    public String toString() {
        return "Asistencia{" +
                "id=" + id +
                ", empleadoId=" + empleadoId +
                ", fecha=" + fecha +
                ", horaEntrada=" + horaEntrada +
                ", horaSalida=" + horaSalida +
                ", estado='" + estado + '\'' +
                '}';
    }
}
