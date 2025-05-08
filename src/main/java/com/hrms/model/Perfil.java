package com.hrms.model;

/**
 * Clase que representa un perfil de usuario en el sistema
 */
public class Perfil {
    private int id;
    private String nombre;
    private String descripcion;
    private String permisos; // Permisos separados por comas
    
    // Constructor vacío
    public Perfil() {
    }
    
    // Constructor sin id
    public Perfil(String nombre, String descripcion, String permisos) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.permisos = permisos;
    }
    
    // Constructor completo
    public Perfil(int id, String nombre, String descripcion, String permisos) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.permisos = permisos;
    }
    
    // Getters y setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getPermisos() {
        return permisos;
    }
    
    public void setPermisos(String permisos) {
        this.permisos = permisos;
    }
    
    // Método para verificar si el perfil tiene un permiso específico
    public boolean tienePermiso(String permiso) {
        if (permisos == null) {
            return false;
        }
        
        // Dividir los permisos por comas y verificar si contiene el permiso solicitado
        String[] permArray = permisos.split(",");
        for (String perm : permArray) {
            if (perm.trim().equals(permiso)) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public String toString() {
        return "Perfil{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", permisos='" + permisos + '\'' +
                '}';
    }
}

