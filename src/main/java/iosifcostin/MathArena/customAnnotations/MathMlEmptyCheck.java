package iosifcostin.MathArena.customAnnotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MathMlEmptyCheck implements ConstraintValidator<MathMlNotEmpty, String> {


    @Override
    public void initialize(MathMlNotEmpty constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return !value.equals("<math xmlns=\"http://www.w3.org/1998/Math/MathML\"/>");
    }

}
