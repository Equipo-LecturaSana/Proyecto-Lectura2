document.addEventListener('DOMContentLoaded', function () {
            
            // --- 1. VALIDACIÓN DE EDAD (MAYOR DE 18) ---
            const campoCumpleanos = document.getElementById('cumpleanos');
            if(campoCumpleanos) {
                const hoy = new Date();
                // Restar 18 años a la fecha actual
                const yyyy = hoy.getFullYear() - 18;
                let mm = hoy.getMonth() + 1; // Enero es 0
                let dd = hoy.getDate();

                // Añadir '0' si es menor de 10
                if (dd < 10) dd = '0' + dd;
                if (mm < 10) mm = '0' + mm;

                // Formato YYYY-MM-DD
                const fechaMaxima = `${yyyy}-${mm}-${dd}`;
                
                // Establece la fecha máxima permitida en el calendario
                // El usuario no podrá seleccionar una fecha posterior a esta
                campoCumpleanos.setAttribute('max', fechaMaxima);
            }

            // --- 2. VALIDACIÓN DE CONTRASEÑA FUERTE (EN TIEMPO REAL) ---
            const passwordInput = document.getElementById('password');
            const seguridadTexto = document.getElementById('seguridad-texto');
            const requisitosTexto = document.getElementById('password-requisitos');

            if(passwordInput) {
                passwordInput.addEventListener('input', function () {
                    const value = passwordInput.value;
                    let strength = 0;
                    
                    // Comprobamos los requisitos del @Pattern
                    if (value.length >= 8) strength++;
                    if (value.match(/[a-z]/)) strength++;
                    if (value.match(/[A-Z]/)) strength++;
                    if (value.match(/[0-9]/)) strength++;
                    if (value.match(/[^A-Za-z0-9]/)) strength++; // Símbolo

                    // Actualizar texto de seguridad
                    if (strength <= 2) {
                        seguridadTexto.textContent = 'SEGURIDAD: BAJA';
                        seguridadTexto.className = 'input-group-text text-danger fw-bold bg-white border-0';
                    } else if (strength <= 4) {
                        seguridadTexto.textContent = 'SEGURIDAD: MEDIA';
                        seguridadTexto.className = 'input-group-text text-warning fw-bold bg-white border-0';
                    } else {
                        seguridadTexto.textContent = 'SEGURIDAD: ALTA';
                        seguridadTexto.className = 'input-group-text text-success fw-bold bg-white border-0';
                    }
                    
                    // Ocultar requisitos si la validación HTML5 (pattern) pasa
                    if (passwordInput.checkValidity()) {
                        requisitosTexto.style.display = 'none';
                    } else {
                        requisitosTexto.style.display = 'block';
                    }
                });
            }
        });

        // --- 3. VALIDACIÓN DE CONFIRMAR CONTRASEÑA (AL ENVIAR) ---
        function validarFormulario() {
            const passwordInput = document.getElementById('password');
            const confirmPasswordInput = document.getElementById('confirmPassword');
            const errorMatchDiv = document.getElementById('password-match-error');

            if (passwordInput.value !== confirmPasswordInput.value) {
                // Mostrar error y marcar campo como inválido
                errorMatchDiv.style.display = 'block';
                confirmPasswordInput.classList.add('is-invalid');
                return false; // Prevenir el envío del formulario
            } else {
                // Ocultar error y marcar como válido
                errorMatchDiv.style.display = 'none';
                confirmPasswordInput.classList.remove('is-invalid');
                return true; // Permitir el envío del formulario
            }
        }