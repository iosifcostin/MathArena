package iosifcostin.MathArena.customAnnotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class MathMlValidator implements ConstraintValidator<ValidMathMlFormat, String> {

    @Override
    public void initialize(ValidMathMlFormat constraintAnnotation) {

    }

    @Override
    public boolean isValid(String mathml, ConstraintValidatorContext context) {
        return validateMathMlFormat(mathml);
    }

    private boolean validateMathMlFormat(String mathml) {
        return mathml.startsWith("<math xmlns=");
    }
}
