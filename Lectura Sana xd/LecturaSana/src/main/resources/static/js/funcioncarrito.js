/**
 * Llama a la API para obtener el número total de items en el carrito
 * y actualiza el badge en el navbar.
 */
async function updateCartCount() {
    try {
        const response = await fetch('/api/carrito/count');
        if (!response.ok) {
            // No fallar silenciosamente, mostrar un error si la API no responde
            console.error('Error: No se pudo obtener el total del carrito.');
            return; 
        }
        
        const data = await response.json();
        const cartBadge = document.getElementById('carrito-contador');
        
        if (cartBadge) {
            // Actualiza el texto del badge con la cuenta recibida
            cartBadge.innerText = data.count || '0'; 
        }
    } catch (error) {
        console.error('Error en updateCartCount:', error.message);
        // Si hay un error de red, poner 0
        const cartBadge = document.getElementById('carrito-contador');
        if (cartBadge) {
            cartBadge.innerText = '0';
        }
    }
}

/**
 * Al cargar el DOM, actualiza el contador del carrito por primera vez.
 */
document.addEventListener('DOMContentLoaded', function () {
    updateCartCount();
});


/**
 * Función helper para inicializar y mostrar un toast de Bootstrap.
 * @param {string} toastId El ID del elemento toast en el HTML (ej: 'toastSuccess')
 * @param {string} message El mensaje a mostrar en el cuerpo del toast.
 */
function showToast(toastId, message) {
    const toastElement = document.getElementById(toastId);
    if (!toastElement) {
        console.error('No se encontró el Toast con ID:', toastId);
        return;
    }

    const toastBody = toastElement.querySelector('.toast-body');
    if (toastBody) {
        toastBody.innerHTML = message;
    }

    try {
        const toastInstance = bootstrap.Toast.getOrCreateInstance(toastElement);
        toastInstance.show();
    } catch (e) {
        console.error('Error al mostrar el toast:', e);
    }
}


/**
 * ¡MODIFICADO CON FETCH (AJAX)!
 * Maneja el evento 'submit' del formulario de añadir al carrito SIN RECARGAR.
 * Esta función ahora es 'async' para poder usar 'await' con 'fetch'.
 */
async function manejarAñadirCarrito(event, form) {
    // 1. Prevenir el envío normal del formulario (¡ya no hay recarga!)
    event.preventDefault();

    // 2. VERIFICAR SI ESTÁ LOGUEADO (tu lógica original)
    const isAuthenticated = form.dataset.auth === 'true';
    if (!isAuthenticated) {
        showToast('toastWarning', '<i class="bi bi-exclamation-circle-fill me-2"></i> Debes iniciar sesión para comprar.');
        setTimeout(() => {
            window.location.href = '/auth/login';
        }, 2000);
        return;
    }

    // 3. PREPARAR EL BOTÓN Y LOS DATOS
    const button = form.querySelector('button[type="submit"]');
    const textoOriginal = button.innerHTML;
    
    // Recolectar datos del formulario
    const formData = new FormData(form);
    const actionUrl = form.action;

    // Deshabilitar botón para evitar doble clic
    button.disabled = true;
    button.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>';

    // 4. ENVIAR DATOS (FETCH) EN SEGUNDO PLANO
    try {
        const response = await fetch(actionUrl, {
            method: 'POST',
            body: formData
        });
        
        const data = await response.json(); // Leer la respuesta JSON del controlador

        if (!response.ok) {
            // Si el controlador devolvió un error (ej. 400 por falta de stock)
            throw new Error(data.message || 'Error al añadir el producto');
        }

        // 5. ÉXITO
        button.innerHTML = '<i class="bi bi-cart-check"></i> Añadido';
        button.classList.remove('btn-primary');
        button.classList.add('btn-success');
        
        showToast('toastSuccess', `<i class="bi bi-check-circle-fill me-2"></i> ${data.message}`); // Mostrar toast de éxito
        
        await updateCartCount(); // ¡Actualizar el contador del navbar!

    } catch (error) {
        // 6. ERROR (ej. Sin stock)
        showToast('toastWarning', `<i class="bi bi-exclamation-triangle-fill me-2"></i> ${error.message}`);
        button.disabled = false; // Reactivar el botón si hay error
    } finally {
        // 7. RESTAURAR BOTÓN (después de 2 segundos)
        setTimeout(() => {
            button.innerHTML = textoOriginal;
            button.classList.remove('btn-success');
            button.classList.add('btn-primary');
            button.disabled = false;
        }, 2000);
    }
}

function mostrarDetalles(libroId) {
    // ¡Aquí está el alert() (la ventanita) que habías perdido!
    alert('Has hecho clic en el libro con ID: ' + libroId);
}