# 🏥 Sistema de Gestión de Citas Médicas

Sistema web profesional para la gestión de citas médicas desarrollado con Spring Boot y MySQL.

## 📋 Características

- ✅ Gestión de médicos y pacientes
- ✅ Programación y seguimiento de citas
- ✅ Sistema de dictámenes médicos
- ✅ Calendario interactivo
- ✅ Historial de citas
- ✅ Modo oscuro
- ✅ Diseño responsive

## 🚀 Tecnologías

- **Backend:** Spring Boot 3.x, Java 17
- **Frontend:** Thymeleaf, HTML5, CSS3, JavaScript
- **Base de Datos:** MySQL 8.0
- **Autenticación:** Google OAuth 2.0

## 📦 Instalación

### Prerequisitos

- JDK 17 o superior
- MySQL 8.0
- Maven 3.6+

### Configuración

1. **Clonar el repositorio:**

   ```bash
   git clone https://github.com/Brandknn/proyectoProfe.git
   cd proyectoProfe
   ```

2. **Configurar la base de datos:**

   - Crear base de datos `medico` en MySQL
   - Ejecutar script de población:
     ```bash
     mysql -u root -p medico < Evidencia_BD_1000_Registros/insert_1000_registros.sql
     ```

3. **Configurar credenciales:**

   - Editar `src/main/resources/application.properties`
   - Actualizar credenciales de MySQL y OAuth

4. **Ejecutar la aplicación:**

   ```bash
   ./mvnw spring-boot:run
   ```

5. **Acceder:**
   - Abrir navegador en: `http://localhost:8080`

## 📁 Estructura del Proyecto

```
proyectoProfe/
├── src/main/
│   ├── java/com/example/demo/
│   │   ├── controller/      # Controladores MVC
│   │   ├── model/           # Entidades JPA
│   │   ├── repository/      # Repositorios de datos
│   │   └── service/         # Lógica de negocio
│   └── resources/
│       ├── static/          # CSS, JS, imágenes
│       ├── templates/       # Vistas Thymeleaf
│       └── application.properties
├── Evidencia_BD_1000_Registros/  # Scripts y evidencia de BD
├── scripts/                      # Scripts de desarrollo
└── pom.xml
```

## 👥 Equipo de Desarrollo

- Brandon Braca
- Sebastián
- Esteban
- Nagle

## 📄 Licencia

Este proyecto fue desarrollado como proyecto académico para la Universidad.

---

**Fecha:** Noviembre 2025  
**Curso:** Bases de Datos / Estructuras de Datos
