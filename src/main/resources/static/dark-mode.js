// dark-mode.js - Código para el modo nocturno
const toggle = document.getElementById('theme-toggle');
const body = document.body;

// Cargar estado al cargar la página
const isDarkMode = localStorage.getItem('dark-mode') === 'true';
if (!isDarkMode) {
    body.classList.remove('dark-mode'); // Quita la clase si no está activado
} else {
    toggle.checked = true; // Marca el checkbox si está activado
}

// Alternar y guardar estado al cambiar
toggle.addEventListener('change', () => {
    body.classList.toggle('dark-mode');
    localStorage.setItem('dark-mode', body.classList.contains('dark-mode'));
});