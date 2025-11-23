"""
Script para generar 1000+ registros para la base de datos médica
Genera médicos, pacientes y citas con datos aleatorios realistas
"""

import random
from datetime import datetime, timedelta

# ============================================================================
# LISTAS DE DATOS ALEATORIOS
# ============================================================================

NOMBRES = [
    "Juan", "María", "Carlos", "Ana", "Luis", "Carmen", "José", "Isabel", "Miguel", "Rosa",
    "Pedro", "Laura", "Antonio", "Teresa", "Francisco", "Patricia", "Manuel", "Dolores", "David", "Cristina",
    "Javier", "Marta", "Rafael", "Lucía", "Fernando", "Pilar", "Andrés", "Elena", "Diego", "Paula",
    "Sergio", "Beatriz", "Roberto", "Silvia", "Alejandro", "Raquel", "Jorge", "Natalia", "Alberto", "Sandra",
    "Ricardo", "Verónica", "Enrique", "Clara", "Pablo", "Alicia", "Ramón", "Julia", "Adrián", "Mónica",
    "Eduardo", "Victoria", "Tomás", "Adriana", "Óscar", "Carolina", "Iván", "Diana", "Gabriel", "Irene",
    "Hugo", "Daniela", "Rubén", "Sofía", "Martín", "Nuria", "Daniel", "Eva", "Guillermo", "Andrea",
    "Samuel", "Mercedes",  "Víctor", "Gloria", "Marcos", "Angela", "Álvaro", "Carla", "Raúl", "Lorena",
    "Felipe", "Rocío", "Lorenzo", "Inmaculada", "Gonzalo", "Amparo", "César", "Remedios", "Jesús", "Josefa",
    "Ignacio", "Encarna", "Arturo", "Concepción", "Emilio", "Montserrat", "Vicente", "Consuelo", "Joaquín", "Nieves"
]

APELLIDOS = [
    "García", "Rodríguez", "González", "Fernández", "López", "Martínez", "Sánchez", "Pérez", "Gómez", "Martín",
    "Jiménez", "Ruiz", "Hernández", "Díaz", "Moreno", "Muñoz", "Álvarez", "Romero", "Alonso", "Gutiérrez",
    "Navarro", "Torres", "Domínguez", "Vázquez", "Ramos", "Gil", "Ramírez", "Serrano", "Blanco", "Suárez",
    "Molina", "Castro", "Ortiz", "Rubio", "Marín", "Sanz", "Iglesias", "Nuñez", "Medina", "Garrido",
    "Santos", "Castillo", "Cortés", "Lozano", "Guerrero", "Cano", "Prieto", "Méndez", "Cruz", "Flores",
    "Herrera", "Peña", "León", "Márquez", "Cabrera", "Gallego", "Calvo", "Vidal", "Campos", "Reyes",
    "Vega", "Fuentes", "Carrasco", "Delgado", "Aguilar", "Pascual", "Santana", "Vargas", "Giménez", "Mora",
    "Arias", "Carmona", "Crespo", "Román", "Pastor", "Soto", "Rojas", "Lara", "Moya", "Bravo"
]

MOTIVOS_CITA = [
    "Consulta general", "Control de rutina", "Dolor de cabeza", "Dolor abdominal", "Chequeo médico",
    "Dolor de espalda", "Fiebre", "Tos persistente", "Dolor muscular", "Revisión anual",
    "Control de presión", "Examen de laboratorio", "Dolor en el pecho", "Mareos", "Fatiga",
    "Consulta nutricional", "Dolor articular", "Insomnio", "Estrés", "Ansiedad",
    "Alergias", "Problemas digestivos", "Control de peso", "Consulta respiratoria", "Dolor de garganta"
]

ESTADOS_CITA = ["PENDIENTE", "COMPLETADA", "CANCELADA", "NO_ASISTIO"]

# ============================================================================
# FUNCIONES GENERADORAS
# ============================================================================

def generar_email(nombre, apellido, numero=None):
    """Genera un email único"""
    nombre_clean = nombre.lower().replace(" ", "")
    apellido_clean = apellido.lower().replace(" ", "")
    sufijo = f"{numero}" if numero else f"{random.randint(1, 999)}"
    dominios = ["gmail.com", "hotmail.com", "outlook.com", "yahoo.com", "correo.com"]
    return f"{nombre_clean}.{apellido_clean}{sufijo}@{random.choice(dominios)}"

def generar_telefono():
    """Genera un teléfono colombiano"""
    return f"3{random.randint(100000000, 199999999)}"

def generar_documento(tipo="CC"):
    """Genera un documento único"""
    return f"{random.randint(10000000, 99999999)}"

def generar_cedula_medico():
    """Genera cédula profesional de médico"""
    return f"MP{random.randint(100000, 999999)}"

def generar_fecha_aleatoria(inicio, fin):
    """Genera una fecha aleatoria entre dos fechas"""
    delta = fin - inicio
    random_days = random.randint(0, delta.days)
    return inicio + timedelta(days=random_days)

def generar_hora_aleatoria():
    """Genera una hora aleatoria entre 8AM y 5PM"""
    hora = random.randint(8, 16)
    minuto = random.choice([0, 30])
    return f"{hora:02d}:{minuto:02d}:00"

# ============================================================================
# GENERACIÓN DE DATOS
# ============================================================================

def generar_datos():
    """
    Genera 1000+ registros distribuidos así:
    - 50 médicos  
    - 400 pacientes
    - 600 citas
    TOTAL: 1050 registros
    """
    
    documentos_usados = set()
    emails_usados = set()
    
    medicos = []
    pacientes = []
    citas = []
    
    print("🏥 Generando datos para base de datos médica...")
    print("=" * 60)
    
    # ========== GENERAR 50 MÉDICOS ==========
    print("\n📋 Generando 50 médicos...")
    for i in range(1, 51):
        nombre = random.choice(NOMBRES)
        apellido = f"{random.choice(APELLIDOS)} {random.choice(APELLIDOS)}"
        
        # Generar cédula única
        while True:
            cedula = generar_cedula_medico()
            if cedula not in documentos_usados:
                documentos_usados.add(cedula)
                break
        
        # Generar email único
        while True:
            email = generar_email(nombre, apellido.split()[0], i)
            if email not in emails_usados:
                emails_usados.add(email)
                break
        
        telefono = generar_telefono()
        password = "$2a$10$abcdefghijklmnopqrstuvwxyz123456"  # BCrypt hash simulado
        
        medicos.append({
            'nombre': nombre,
            'apellido': apellido,
            'cedula': cedula,
            'email': email,
            'password': password,
            'telefono': telefono,
            'perfil_completo': 1
        })
    
    print(f"✅ {len(medicos)} médicos generados")
    
    # ========== GENERAR 400 PACIENTES ==========
    print("\n👥 Generando 400 pacientes...")
    for i in range(1, 401):
        nombre = random.choice(NOMBRES)
        apellido = f"{random.choice(APELLIDOS)} {random.choice(APELLIDOS)}"
        
        # Generar documento único
        while True:
            documento = generar_documento()
            if documento not in documentos_usados:
                documentos_usados.add(documento)
                break
        
        # Generar email único
        while True:
            email = generar_email(nombre, apellido.split()[0], 1000 + i)
            if email not in emails_usados:
                emails_usados.add(email)
                break
        
        telefono = generar_telefono()
        medico_id = random.randint(1, 50)  # Asignar a un médico aleatorio
        
        pacientes.append({
            'nombre': nombre,
            'apellido': apellido,
            'documento': documento,
            'correo': email,
            'telefono': telefono,
            'medico_id': medico_id
        })
    
    print(f"✅ {len(pacientes)} pacientes generados")
    
    # ========== GENERAR 600 CITAS ==========
    print("\n📅 Generando 600 citas...")
    fecha_inicio = datetime(2025, 1, 1)
    fecha_fin = datetime(2025, 12, 31)
    
    for i in range(1, 601):
        paciente_id = random.randint(1, 400)
        # Obtener el medico_id del paciente
        medico_id = pacientes[paciente_id - 1]['medico_id']
        
        motivo = random.choice(MOTIVOS_CITA)
        fecha = generar_fecha_aleatoria(fecha_inicio, fecha_fin)
        hora = generar_hora_aleatoria()
        estado = random.choice(ESTADOS_CITA)
        
        citas.append({
            'motivo': motivo,
            'fecha': fecha.strftime('%Y-%m-%d'),
            'hora': hora,
            'estado': estado,
            'medico_id': medico_id,
            'paciente_id': paciente_id
        })
    
    print(f"✅ {len(citas)} citas generadas")
    print("\n" + "=" * 60)
    print(f"🎉 TOTAL: {len(medicos) + len(pacientes) + len(citas)} registros generados")
    
    return medicos, pacientes, citas

# ============================================================================
# GENERACIÓN DE SCRIPT SQL
# ============================================================================

def generar_script_sql(medicos, pacientes, citas):
    """Genera el script SQL con todos los INSERT"""
    
    sql = "-- ============================================\n"
    sql += "-- Script para insertar 1000+ registros\n"
    sql += "-- Generado automáticamente\n"
    sql += f"-- Fecha: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\n"
    sql += "-- ============================================\n\n"
    
    sql += "USE medico;\n\n"
    
    # INSERTs de MÉDICOS
    sql += "-- ============================================\n"
    sql += "-- INSERTAR MÉDICOS (50)\n"
    sql += "-- ============================================\n\n"
    
    for medico in medicos:
        sql += f"""INSERT INTO medicos (nombre, apellido, cedula, email, password, telefono, perfil_completo) 
VALUES ('{medico['nombre']}', '{medico['apellido']}', '{medico['cedula']}', '{medico['email']}', '{medico['password']}', '{medico['telefono']}', {medico['perfil_completo']});\n"""
    
    # INSERTs de PACIENTES
    sql += "\n-- ============================================\n"
    sql += "-- INSERTAR PACIENTES (400)\n"
    sql += "-- ============================================\n\n"
    
    for paciente in pacientes:
        sql += f"""INSERT INTO paciente (nombre, apellido, documento, correo, telefono, medico_id) 
VALUES ('{paciente['nombre']}', '{paciente['apellido']}', '{paciente['documento']}', '{paciente['correo']}', '{paciente['telefono']}', {paciente['medico_id']});\n"""
    
    # INSERTs de CITAS
    sql += "\n-- ============================================\n"
    sql += "-- INSERTAR CITAS (600)\n"
    sql += "-- ============================================\n\n"
    
    for cita in citas:
        sql += f"""INSERT INTO cita (motivo, fecha, hora, estado, medico_id, paciente_id) 
VALUES ('{cita['motivo']}', '{cita['fecha']}', '{cita['hora']}', '{cita['estado']}', {cita['medico_id']}, {cita['paciente_id']});\n"""
    
    sql += "\n-- ============================================\n"
    sql += "-- FIN DEL SCRIPT\n"
    sql += "-- ============================================\n"
    
    return sql

# ============================================================================
# MAIN
# ============================================================================

if __name__ == "__main__":
    # Generar datos
    medicos, pacientes, citas = generar_datos()
    
    # Generar SQL
    print("\n📝 Generando script SQL...")
    sql_script = generar_script_sql(medicos, pacientes, citas)
    
    # Guardar en archivo
    output_file = "insert_1000_registros.sql"
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write(sql_script)
    
    print(f"✅ Script SQL guardado en: {output_file}")
    print("\n" + "=" * 60)
    print("📊 RESUMEN:")
    print(f"   • Médicos: {len(medicos)}")
    print(f"   • Pacientes: {len(pacientes)}")
    print(f"   • Citas: {len(citas)}")
    print(f"   • TOTAL: {len(medicos) + len(pacientes) + len(citas)} registros")
    print("=" * 60)
    print("\n🚀 Para ejecutar el script SQL:")
    print("   1. Abre MySQL Workbench o tu cliente MySQL")
    print("   2. Conecta a tu base de datos 'medico'")
    print("   3. Abre el archivo 'insert_1000_registros.sql'")
    print("   4. Ejecuta el script completo")
    print("\n   O desde línea de comandos:")
    print("   mysql -u root -p medico < insert_1000_registros.sql")
    print("\n✨ ¡Listo!")
