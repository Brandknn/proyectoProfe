# 🚀 Flujo de Trabajo en Equipo - ProyectoProfe

## 📋 Estructura de Ramas

### 🔒 **`refactor/css-externalization`** (Rama Principal - PROTEGIDA)

- **Permisos:** Solo **Brandon** puede hacer merge aquí
- **Propósito:** Código estable y aprobado listo para producción
- **Nadie más puede hacer push directo** - solo mediante Pull Request aprobado por Brandon

### 🛠️ **`desarrollo`** (Rama de Desarrollo - COLABORATIVA)

- **Permisos:** Los 3 colaboradores pueden hacer push directo
- **Propósito:** Rama donde el equipo trabaja en conjunto mientras Brandon está ausente
- **Todos los cambios del equipo se integran aquí primero**

---

## 👥 Flujo de Trabajo para el Equipo (mientras Brandon está ausente)

### **Paso 1: Clonar el repositorio (solo la primera vez)**

```bash
git clone https://github.com/Brandknn/proyectoProfe.git
cd proyectoProfe
```

### **Paso 2: Cambiar a la rama de desarrollo**

```bash
git checkout desarrollo
```

### **Paso 3: Trabajar en sus cambios**

**Opción A - trabajar directamente en `desarrollo` (MÁS SIMPLE):**

```bash
# Hacer cambios en el código
git add .
git commit -m "Descripción de los cambios"
git push origin desarrollo
```

**Opción B - Crear rama individual y luego mergear:**

```bash
# Crear rama personal
git checkout -b amigo1/nueva-feature

# Hacer cambios
git add .
git commit -m "Agregar nueva feature"
git push origin amigo1/nueva-feature

# Mergear a desarrollo (desde GitHub o terminal)
git checkout desarrollo
git pull origin desarrollo
git merge amigo1/nueva-feature
git push origin desarrollo
```

### **Paso 4: Antes de hacer cambios, SIEMPRE actualizar**

```bash
git checkout desarrollo
git pull origin desarrollo
```

### **Paso 5: Cuando TODOS terminen - Crear Pull Request para Brandon**

1. Ve a: https://github.com/Brandknn/proyectoProfe/pulls
2. Click en **"New Pull Request"**
3. Configura:
   - **Base:** `refactor/css-externalization` (rama principal)
   - **Compare:** `desarrollo` (rama con todos los cambios del equipo)
4. Título: "Cambios del equipo - [Fecha]"
5. Descripción: Lista de todos los cambios realizados
6. Click en **"Create Pull Request"**
7. **Brandon revisará y aprobará cuando regrese**

---

## 🔧 Configuración que Brandon debe hacer en GitHub

### **1. Crear regla para proteger la rama `desarrollo` (OPCIONAL)**

Si quieres que los amigos no se puedan pisar entre ellos:

Ve a: https://github.com/Brandknn/proyectoProfe/settings/rules

Crear un nuevo ruleset:

- **Nombre:** `desarrollo-protection`
- **Target branches:** `desarrollo`
- **Reglas:**
  - ☑️ Block force pushes (evita que borren el historial)
  - ❌ NO marcar "Require pull request" (para que puedan hacer push directo)

### **2. Configurar rama default (OPCIONAL)**

Ve a: https://github.com/Brandknn/proyectoProfe/settings

En **"Default branch"**, cambiar a `desarrollo` temporalmente para que sea lo primero que vean tus amigos.

---

## 📊 Resumen del Flujo

```
┌─────────────────────────────────────────┐
│  Colaboradores trabajan en "desarrollo" │
│  - Pueden hacer push directo            │
│  - Se coordinan entre ellos             │
└──────────────┬──────────────────────────┘
               │
               │ Pull Request cuando terminen
               ▼
┌─────────────────────────────────────────┐
│  refactor/css-externalization (MAIN)    │
│  - Solo Brandon puede hacer merge       │
│  - Brandon revisa y aprueba             │
└─────────────────────────────────────────┘
```

---

## ✅ Checklist para Brandon antes de irse

- [x] Crear rama `desarrollo`
- [ ] Informar a los colaboradores sobre el flujo de trabajo
- [ ] (Opcional) Configurar protección para `desarrollo` (solo block force push)
- [ ] Compartir este documento con el equipo

---

## 📞 Para los Colaboradores

**Recuerden:**

1. **SIEMPRE trabajen en la rama `desarrollo`**
2. **SIEMPRE hagan `git pull` antes de empezar a trabajar**
3. **Coordínense para no trabajar en los mismos archivos al mismo tiempo**
4. **Cuando terminen TODO, creen UN SOLO Pull Request** de `desarrollo` → `refactor/css-externalization`
5. **Esperen a que Brandon lo revise y apruebe**

---

## 🆘 Problemas Comunes

### "No puedo hacer push a refactor/css-externalization"

✅ **Normal** - Trabaja en `desarrollo` en su lugar

### "Mi push fue rechazado"

1. Verifica que estés en`desarrollo`: `git branch`
2. Actualiza primero: `git pull origin desarrollo`
3. Resuelve conflictos si los hay
4. Vuelve a hacer push

### "Tengo conflictos"

1. `git pull origin desarrollo`
2. Git te mostrará los archivos en conflicto
3. Abre los archivos y busca `<<<<<<<`, `=======`, `>>>>>>>`
4. Resuelve manualmente
5. `git add .`
6. `git commit -m "Resolver conflictos"`
7. `git push origin desarrollo`

---

**Última actualización:** 23/Nov/2025
**Creado por:** Brandon Braca
**Repositorio:** https://github.com/Brandknn/proyectoProfe
