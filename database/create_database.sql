-- Crear la base de datos
CREATE DATABASE IF NOT EXISTS bd_hrms;
USE bd_hrms;

-- Tabla de perfiles
CREATE TABLE IF NOT EXISTS perfiles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    descripcion VARCHAR(255),
    permisos TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tabla de empleados
CREATE TABLE IF NOT EXISTS empleados (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    correo VARCHAR(100) UNIQUE,
    telefono VARCHAR(20),
    direccion VARCHAR(255),
    departamento VARCHAR(50),
    cargo VARCHAR(50),
    fecha_contratacion DATE,
    salario DECIMAL(10, 2),
    perfil_id INT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (perfil_id) REFERENCES perfiles(id) ON DELETE SET NULL
);

-- Tabla de asistencia
CREATE TABLE IF NOT EXISTS asistencias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    empleado_id INT NOT NULL,
    fecha DATE NOT NULL,
    hora_entrada TIME,
    hora_salida TIME,
    estado VARCHAR(20) DEFAULT 'Presente',
    notas TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (empleado_id) REFERENCES empleados(id) ON DELETE CASCADE
);

-- Insertar algunos perfiles de ejemplo
INSERT INTO perfiles (nombre, descripcion, permisos) VALUES
('Administrador', 'Acceso completo al sistema', 'admin,empleados,asistencias,perfiles,reportes'),
('Gerente', 'Acceso a gestión de empleados y reportes', 'empleados,asistencias,reportes'),
('Empleado', 'Acceso básico a su información y asistencia', 'perfil_propio,asistencia_propia');

-- Insertar algunos empleados de ejemplo
INSERT INTO empleados (nombre, apellido, correo, telefono, departamento, cargo, fecha_contratacion, salario, perfil_id) VALUES
('Juan', 'Pérez', 'juan.perez@empresa.com', '555-1234', 'Administración', 'Gerente General', '2020-01-15', 5000.00, 1),
('María', 'González', 'maria.gonzalez@empresa.com', '555-5678', 'Recursos Humanos', 'Coordinadora HRMS', '2020-03-10', 3500.00, 2),
('Carlos', 'Rodríguez', 'carlos.rodriguez@empresa.com', '555-9012', 'Tecnología', 'Desarrollador', '2021-05-20', 3000.00, 3),
('Ana', 'Martínez', 'ana.martinez@empresa.com', '555-3456', 'Ventas', 'Ejecutiva de Ventas', '2021-07-05', 2800.00, 3);

-- Insertar algunos registros de asistencia de ejemplo
INSERT INTO asistencias (empleado_id, fecha, hora_entrada, hora_salida, estado) VALUES
(1, CURDATE() - INTERVAL 2 DAY, '08:00:00', '17:00:00', 'Presente'),
(2, CURDATE() - INTERVAL 2 DAY, '08:15:00', '17:30:00', 'Presente'),
(3, CURDATE() - INTERVAL 2 DAY, '08:30:00', '17:15:00', 'Presente'),
(4, CURDATE() - INTERVAL 2 DAY, '09:00:00', '18:00:00', 'Tardanza'),
(1, CURDATE() - INTERVAL 1 DAY, '08:00:00', '17:00:00', 'Presente'),
(2, CURDATE() - INTERVAL 1 DAY, '08:10:00', '17:20:00', 'Presente'),
(3, CURDATE() - INTERVAL 1 DAY, NULL, NULL, 'Ausente'),
(4, CURDATE() - INTERVAL 1 DAY, '08:05:00', '17:10:00', 'Presente');