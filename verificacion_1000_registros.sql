-- ============================================
-- SCRIPT DE VERIFICACIÓN DE REGISTROS
-- Base de Datos: medico
-- Proyecto: Sistema de Gestión de Citas Médicas
-- ============================================

-- ============================================
-- 1. CONTEO TOTAL DE REGISTROS POR TABLA
-- ============================================

-- Contar médicos
SELECT 'MÉDICOS' as Tabla, COUNT(*) as Total FROM medicos;

-- Contar pacientes
SELECT 'PACIENTES' as Tabla, COUNT(*) as Total FROM paciente;

-- Contar citas
SELECT 'CITAS' as Tabla, COUNT(*) as Total FROM cita;

-- TOTAL GENERAL
SELECT
    'TOTAL COMPLETO' as Descripcion,
    (
        SELECT COUNT(*)
        FROM medicos
    ) + (
        SELECT COUNT(*)
        FROM paciente
    ) + (
        SELECT COUNT(*)
        FROM cita
    ) as Total_Registros;

-- ============================================
-- 2. VERIFICACIÓN DE DATOS ÚNICOS
-- ============================================

-- Verificar que no hay documentos duplicados en pacientes
SELECT
    'Documentos únicos en pacientes' as Verificacion,
    COUNT(DISTINCT documento) as Unicos,
    COUNT(*) as Total,
    CASE
        WHEN COUNT(DISTINCT documento) = COUNT(*) THEN 'OK - Sin duplicados'
        ELSE 'ERROR - Hay duplicados'
    END as Estado
FROM paciente;

-- Verificar que no hay cédulas duplicadas en médicos
SELECT
    'Cédulas únicas en médicos' as Verificacion,
    COUNT(DISTINCT cedula) as Unicos,
    COUNT(*) as Total,
    CASE
        WHEN COUNT(DISTINCT cedula) = COUNT(*) THEN 'OK - Sin duplicados'
        ELSE 'ERROR - Hay duplicados'
    END as Estado
FROM medicos;

-- ============================================
-- 3. VERIFICACIÓN DE RELACIONES
-- ============================================

-- Verificar que todas las citas tienen médico asignado
SELECT
    'Citas con médico asignado' as Verificacion,
    COUNT(*) as Total,
    COUNT(medico_id) as Con_Medico,
    CASE
        WHEN COUNT(*) = COUNT(medico_id) THEN 'OK - Todas tienen médico'
        ELSE 'ADVERTENCIA - Algunas sin médico'
    END as Estado
FROM cita;

-- Verificar que todas las citas tienen paciente asignado
SELECT
    'Citas con paciente asignado' as Verificacion,
    COUNT(*) as Total,
    COUNT(paciente_id) as Con_Paciente,
    CASE
        WHEN COUNT(*) = COUNT(paciente_id) THEN 'OK - Todas tienen paciente'
        ELSE 'ADVERTENCIA - Algunas sin paciente'
    END as Estado
FROM cita;

-- ============================================
-- 4. DISTRIBUCIÓN DE DATOS
-- ============================================

-- Distribución de citas por estado
SELECT estado as Estado, COUNT(*) as Cantidad
FROM cita
GROUP BY
    estado
ORDER BY Cantidad DESC;

-- Distribución de pacientes por médico (Top 10)
SELECT m.nombre as Medico, m.apellido, COUNT(p.id) as Num_Pacientes
FROM medicos m
    LEFT JOIN paciente p ON m.id = p.medico_id
GROUP BY
    m.id,
    m.nombre,
    m.apellido
ORDER BY Num_Pacientes DESC
LIMIT 10;

-- ============================================
-- 5. MUESTRA DE DATOS
-- ============================================

-- Muestra de los primeros 5 médicos
SELECT id, nombre, apellido, cedula, email FROM medicos LIMIT 5;

-- Muestra de los primeros 5 pacientes
SELECT
    id,
    nombre,
    apellido,
    documento,
    correo
FROM paciente
LIMIT 5;

-- Muestra de las primeras 5 citas
SELECT id, motivo, fecha, hora, estado FROM cita LIMIT 5;

-- ============================================
-- FIN DEL SCRIPT DE VERIFICACIÓN
-- ============================================