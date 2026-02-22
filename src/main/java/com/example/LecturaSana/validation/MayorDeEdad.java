package com.example.LecturaSana.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Anotación para validar que un usuario es mayor de 18 años.
 */
@Documented
@Constraint(validatedBy = MayorDeEdadValidator.class) // Enlaza con la lógica
@Target({ ElementType.FIELD, ElementType.METHOD }) // Se puede usar en campos o métodos
@Retention(RetentionPolicy.RUNTIME)
public @interface MayorDeEdad {
    
    // Mensaje de error por defecto
    String message() default "El usuario debe ser mayor de 18 años";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}