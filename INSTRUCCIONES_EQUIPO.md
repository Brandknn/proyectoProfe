# 📝 INSTRUCCIONES PARA EL EQUIPO

## 🎯 Objetivo

Trabajar en el proyecto mientras Brandon está ausente y crear un Pull Request cuando terminen.

---

## 🚀 PASO A PASO

### **1. Clonar el repositorio (SOLO LA PRIMERA VEZ)**

```bash
git clone https://github.com/Brandknn/proyectoProfe.git
cd proyectoProfe
```

### **2. Configurar la Base de Datos (IMPORTANTE - HACER PRIMERO)**

El proyecto necesita **1,050 registros** en la base de datos para funcionar correctamente.

#### **📝 Pasos con MySQL Workbench (RECOMENDADO):**

1. **Abrir MySQL Workbench**
2. **Conectarse** a su servidor MySQL local (usuario: `root`, contraseña: la de cada uno)
3. **Crear la base de datos** si no existe:
   - Click derecho en el panel izquierdo → "Create Schema"
   - Nombre: `medico`
   - Click en "Apply"
4. **Seleccionar la base de datos:**
   - Click en `medico` en el panel izquierdo
5. **Importar los registros:**
   - Ir a: **File** → **Open SQL Script...**
   - Navegar a la carpeta del proyecto
   - Buscar: `Evidencia_BD_1000_Registros/insert_1000_registros.sql`
   - Click en **"Open"**
6. **Ejecutar el script:**
   - Click en el botón ⚡ **"Execute"** (o presionar Ctrl+Shift+Enter)
   - Esperar 1-2 minutos (va a insertar 1,050 registros)
7. **Verificar:**
   ```sql
   SELECT COUNT(*) FROM medicos;    -- Debe dar 50
   SELECT COUNT(*) FROM paciente;   -- Debe dar 400
   SELECT COUNT(*) FROM cita;       -- Debe dar 600
   ```

✅ **Listo** - Ahora tienen la base de datos completa con todos los registros.

---

### **3. Cambiar a la rama de desarrollo**

```bash
git checkout desarrollo
```

Verifica que estés en la rama correcta:

```bash
git branch
# Debe mostrar: * desarrollo
```

### **3. SIEMPRE antes de trabajar: ACTUALIZAR**

```bash
git pull origin desarrollo
```

### **4. Hacer tus cambios**

- Edita archivos
- Crea nuevos archivos
- Borra archivos que no sirvan

### **5. Guardar y subir tus cambios**

```bash
# Ver qué cambió
git status

# Agregar TODOS los cambios
git add .

# Hacer commit con mensaje descriptivo
git commit -m "Descripción de lo que hiciste"

# Subir al repositorio
git push origin desarrollo
```

### **6. Cuando TODOS terminen - Crear Pull Request**

**UNO de ustedes hace esto:**

1. Ve a: https://github.com/Brandknn/proyectoProfe
2. Click en **"Pull requests"**
3. Click en **"New pull request"**
4. Selecciona:
   - **base:** `refactor/css-externalization`
   - **compare:** `desarrollo`
5. Título: "Cambios del equipo - [Fecha de hoy]"
6. Descripción: Lista todo lo que cambiaron
7. Click en **"Create pull request"**
8. **LISTO** - Brandon lo revisará cuando regrese

---

## ⚠️ REGLAS IMPORTANTES

### ✅ SIEMPRE:

- Trabajen en la rama `desarrollo`
- Hagan `git pull` antes de trabajar
- Usen mensajes de commit descriptivos
- Avísense entre ustedes qué están cambiando

### ❌ NUNCA:

- NO trabajen en `refactor/css-externalization` (está protegida)
- NO hagan `git push --force` (está bloqueado)
- NO modifiquen la base de datos sin avisar a los demás

---

## 🆘 Problemas Comunes

### Error: "rejected - non-fast-forward"

**Solución:**

```bash
git pull origin desarrollo
# Si hay conflictos, resuélvelos
git add .
git commit -m "Resolver conflictos"
git push origin desarrollo
```

### Error: "You are not allowed to push"

**Solución:** Verifica que estés en `desarrollo`, no en `refactor/css-externalization`

```bash
git branch  # Debe mostrar * desarrollo
```

### Conflictos al hacer pull

1. Git te mostrará los archivos en conflicto
2. Abre cada archivo
3. Busca estas marcas:
   ```
   <<<<<<< HEAD
   Tu código
   =======
   Código del compañero
   >>>>>>> commit-id
   ```
4. Decide qué código conservar
5. Borra las marcas `<<<<<<<`, `=======`, `>>>>>>>`
6. Guarda el archivo
7. Continúa:
   ```bash
   git add .
   git commit -m "Resolver conflictos"
   git push origin desarrollo
   ```

---

## 📞 Contacto

Si tienen dudas que no puedan resolver entre ustedes:

- Esperen a que Brandon regrese
- O busquen en Google: "git [tu problema]"

---

## ✅ Checklist Final

Antes de crear el Pull Request, verificar:

- [ ] Todos los cambios están en la rama `desarrollo`
- [ ] La aplicación funciona correctamente (`.\mvnw.cmd spring-boot:run`)
- [ ] No hay errores de compilación
- [ ] Hicimos `git pull` para tener la última versión
- [ ] Documentamos los cambios principales

---

**Repositorio:** https://github.com/Brandknn/proyectoProfe  
**Rama de trabajo:** `desarrollo`  
**Creado:** 23/Nov/2025
