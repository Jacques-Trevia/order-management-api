package ecommerce_order_api.services.validations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

	@Constraint(validatedBy = CustomerInsertValidator.class)
	@Target({ ElementType.TYPE})
	@Retention(RetentionPolicy.RUNTIME)

	public @interface CustomerInsertValid {
		
		String message() default "Validation error";
		
		Class<?>[] groups() default {};
		
		Class<? extends Payload>[] payload() default {};
}
