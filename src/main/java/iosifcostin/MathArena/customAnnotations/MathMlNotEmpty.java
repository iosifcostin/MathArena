package iosifcostin.MathArena.customAnnotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(ElementType.FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = MathMlEmptyCheck.class)
@Documented
public @interface MathMlNotEmpty {
    String message() default "please type a description";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
