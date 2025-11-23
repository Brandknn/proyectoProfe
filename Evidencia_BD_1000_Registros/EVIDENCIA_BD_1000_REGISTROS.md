# EVIDENCIA DE CUMPLIMIENTO - BASE DE DATOS

## 📋 Requisito del Proyecto

**Asignatura:** Base de Datos  
**Requisito:** Base de datos implementada, conectada con el software web y poblada con al menos 1000 registros  
**Estado:** ✅ CUMPLIDO

---

## 📊 Resumen de Registros Generados

| Tabla     | Cantidad de Registros |
| --------- | --------------------- |
| Médicos   | 50+                   |
| Pacientes | 400+                  |
| Citas     | 600+                  |
| **TOTAL** | **1,050+ registros**  |

---

## 🔧 Archivos de Evidencia Incluidos

### 1. `insert_1000_registros.sql`

**Descripción:** Script SQL completo con todos los INSERT para poblar la base de datos.

- Contiene 50 médicos con datos únicos (cédula profesional, email, teléfono)
- Contiene 400 pacientes con documentos y correos únicos
- Contiene 600 citas con fechas, horas y estados variados
- **Total:** 1,050 statements INSERT

### 2. `verificacion_1000_registros.sql`

**Descripción:** Script de verificación que demuestra que los registros fueron insertados correctamente.

- Conteos totales por tabla
- Verificación de unicidad (sin duplicados)
- Verificación de relaciones (Foreign Keys)
- Distribución de datos
- Muestra de registros

### 3. `generar_datos_1000.py`

**Descripción:** Script Python que genera los datos de forma automática y validada.

- Genera nombres y apellidos realistas
- Valida unicidad de documentos y emails
- Establece relaciones correctas entre tablas
- Evita duplicados mediante sets de Python

---

## ✅ Validaciones Implementadas

### 1. Unicidad de Datos

- ✅ Documentos de pacientes únicos (sin duplicados)
- ✅ Cédulas de médicos únicas
- ✅ Emails únicos tanto para médicos como pacientes

### 2. Integridad Referencial

- ✅ Todas las citas tienen un médico asignado válido
- ✅ Todas las citas tienen un paciente asignado válido
- ✅ Los pacientes están asignados a médicos existentes

### 3. Datos Realistas

- ✅ Nombres colombianos comunes
- ✅ Emails con formatos válidos (@gmail, @hotmail, etc.)
- ✅ Teléfonos celulares colombianos (formato 3XXXXXXXXX)
- ✅ Fechas de citas distribuidas en 2025
- ✅ Horas de citas en horario laboral (8AM - 5PM)

---

## 🎯 Cómo Verificar

### Opción 1: Ejecutar Script de Verificación

```bash
mysql -u root -p medico < verificacion_1000_registros.sql
```

Este script mostrará:

- Conteo exacto de registros por tabla
- Verificación de que no hay duplicados
- Estado de las relaciones entre tablas
- Distribución de datos

### Opción 2: Consultas Manuales

```sql
-- Ver total de registros
SELECT
    (SELECT COUNT(*) FROM medicos) as Medicos,
    (SELECT COUNT(*) FROM paciente) as Pacientes,
    (SELECT COUNT(*) FROM cita) as Citas,
    (SELECT COUNT(*) FROM medicos) +
    (SELECT COUNT(*) FROM paciente) +
    (SELECT COUNT(*) FROM cita) as TOTAL;
```

### Opción 3: Desde la Aplicación Web

1. Iniciar el servidor: `.\mvnw.cmd spring-boot:run`
2. Navegar a `http://localhost:8080/paciente`
3. Observar la lista de pacientes (400+)
4. Navegar a `http://localhost:8080/gestionCitas`
5. Observar la lista de citas (600+)

---

## 🗂️ Estructura de la Base de Datos

### Tabla: medicos

```sql
CREATE TABLE medicos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(255),
    apellido VARCHAR(255),
    cedula VARCHAR(255) UNIQUE,
    email VARCHAR(255),
    password VARCHAR(255),
    telefono VARCHAR(255),
    google_id VARCHAR(255),
    perfil_completo BOOLEAN
);
```

### Tabla: paciente

```sql
CREATE TABLE paciente (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    apellido VARCHAR(255) NOT NULL,
    documento VARCHAR(255) UNIQUE NOT NULL,
    correo VARCHAR(255),
    telefono BIGINT,
    medico_id BIGINT,
    FOREIGN KEY (medico_id) REFERENCES medicos(id)
);
```

### Tabla: cita

```sql
CREATE TABLE cita (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    motivo VARCHAR(255),
    fecha DATE,
    hora TIME,
    estado ENUM('PENDIENTE', 'COMPLETADA', 'CANCELADA', 'NO_ASISTIO'),
    medico_id BIGINT,
    paciente_id BIGINT,
    FOREIGN KEY (medico_id) REFERENCES medicos(id),
    FOREIGN KEY (paciente_id) REFERENCES paciente(id)
);
```

---

## 📸 Evidencia Visual

Para incluir en la presentación, se recomienda tomar capturas de pantalla de:

1. **Resultado del script de verificación** mostrando el conteo de 1000+ registros
2. **Vista de la aplicación web** con la lista de pacientes
3. **Vista de gestión de citas** con múltiples registros
4. **MySQL Workbench** mostrando las tablas con datos

---

## 🔄 Backup y Restauración

### Crear Backup

```bash
mysqldump -u root -p medico > backup_medico.sql
```

### Restaurar desde Backup

```bash
mysql -u root -p medico < backup_medico.sql
```

---

## 📝 Notas Técnicas

- **Motor de BD:** MySQL 8.0+
- **Framework:** Spring Boot con JPA/Hibernate
- **Generación de datos:** Python 3.x
- **Validación:** Sin duplicados, relaciones íntegras
- **Estado:** Producción lista para demostración

---

## ✨ Conclusión

Se ha cumplido satisfactoriamente con el requisito de tener una base de datos:

- ✅ **Implementada:** Todas las tablas creadas con constraints adecuados
- ✅ **Conectada:** Aplicación web Spring Boot conectada y funcional
- ✅ **Poblada:** Más de 1,050 registros con datos realistas y válidos

**Fecha de generación:** 22 de Noviembre de 2025  
**Autor:** Brandon Braca  
**Proyecto:** Sistema de Gestión de Citas Médicas
