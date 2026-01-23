async function updateCartCount() {
    try {
        const res = await fetch('/api/carrito/count');
        if(res.ok) {
            const data = await res.json();
            const badge = document.getElementById('carrito-contador');
            if(badge) badge.innerText = data.count || '0';
        }
    } catch(e) { console.error(e); }
}
document.addEventListener('DOMContentLoaded', updateCartCount);

function showToast(id, msg) {
    const el = document.getElementById(id);
    if(el) {
        el.querySelector('.toast-body').innerHTML = msg;
        new bootstrap.Toast(el).show();
    }
}

async function manejarAñadirCarrito(event, form) {
    event.preventDefault();
    if (form.dataset.auth !== 'true') {
        showToast('toastWarning', 'Inicia sesión para comprar.');
        setTimeout(() => window.location.href = '/auth/login', 2000);
        return;
    }
    
    const btn = form.querySelector('button');
    const original = btn.innerHTML;
    btn.disabled = true;
    btn.innerHTML = '...';

    try {
        const res = await fetch(form.action, { method: 'POST', body: new FormData(form) });
        const data = await res.json();
        
        if(res.ok) {
            showToast('toastSuccess', data.message);
            updateCartCount();
            btn.classList.replace('btn-primary','btn-success');
            btn.innerHTML = '✔';
        } else {
            throw new Error(data.message);
        }
    } catch(e) {
        showToast('toastWarning', e.message);
    } finally {
        setTimeout(() => {
            btn.disabled = false;
            btn.classList.replace('btn-success','btn-primary');
            btn.innerHTML = original;
        }, 2000);
    }
}

function mostrarDetalles(id) { alert('ID del libro: ' + id); }