// dark-mode.js - Código para el modo nocturno (más robusto)
document.addEventListener('DOMContentLoaded', function () {
    const toggle = document.getElementById('theme-toggle');
    const body = document.body;

    // Estado almacenado (string 'true' / 'false')
    const stored = localStorage.getItem('dark-mode');
    console.log('Dark mode stored value:', stored);
    
    // Por defecto modo oscuro si no hay nada guardado
    const isDarkMode = stored === null ? true : stored === 'true';
    console.log('Is dark mode:', isDarkMode);

    // Aplicar estado inicial basado en localStorage
    if (isDarkMode) {
        body.classList.add('dark-mode');
        if (toggle) toggle.checked = true;
        console.log('Applied dark mode');
    } else {
        body.classList.remove('dark-mode');
        if (toggle) toggle.checked = false;
        console.log('Applied light mode');
    }
    
    // Guardar el estado inicial si es la primera vez
    if (stored === null) {
        localStorage.setItem('dark-mode', 'true');
        console.log('Saved initial dark mode to localStorage');
    }

    // Si no existe el checkbox, no fallar: intentamos sincronizar sólo con body/localStorage
    if (!toggle) {
        return;
    }

//verficar git
    // Handler centralizado para actualizar estado
    function applyToggleState(checked) {
        if (checked) {
            body.classList.add('dark-mode');
        } else {
            body.classList.remove('dark-mode');
        }
        localStorage.setItem('dark-mode', checked);
    }

    // Escuchar cambios directos en el checkbox
    toggle.addEventListener('change', () => {
        applyToggleState(toggle.checked);
    });

    // Soportar clicks en la etiqueta <label for="theme-toggle"> (algunos estilos usan input oculto)
    const label = document.querySelector('label[for="theme-toggle"]');
    if (label) {
        label.addEventListener('click', () => {
            // Espera breve para que el checkbox cambie su estado
            setTimeout(() => applyToggleState(toggle.checked), 10);
        });
    }
});