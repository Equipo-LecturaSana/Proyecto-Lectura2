package com.example.LecturaSana.validation;

import java.time.LocalDate;
import java.time.Period;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Esta clase contiene la lógica de validación para @MayorDeEdad.
 */
public class MayorDeEdadValidator implements ConstraintValidator<MayorDeEdad, LocalDate> {

    @Override
    public boolean isValid(LocalDate fechaNacimiento, ConstraintValidatorContext context) {
        // Si la fecha es nula, no la validamos aquí (para eso está @NotNull)
        if (fechaNacimiento == null) {
            return true; 
        }

        // Calcula el periodo entre la fecha de nacimiento y hoy
        Period period = Period.between(fechaNacimiento, LocalDate.now());

        // Comprueba si el periodo es de al menos 18 años
        return period.getYears() >= 18;
    }
}