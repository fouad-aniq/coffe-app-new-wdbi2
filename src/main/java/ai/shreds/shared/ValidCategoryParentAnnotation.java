package ai.shreds.shared;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

import ai.shreds.application.validators.ValidCategoryParentValidator;

@Documented
@Constraint(validatedBy = ValidCategoryParentValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCategoryParent {
    
    String message() default \"Invalid category parent.\";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}