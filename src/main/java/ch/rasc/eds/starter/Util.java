package ch.rasc.eds.starter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

public abstract class Util {

	public static <T> List<ValidationErrors> validateObject(Validator validator, T entity) {
		Set<ConstraintViolation<T>> constraintViolations = validator.validate(entity);

		Map<String, List<String>> fieldMessages = new HashMap<>();
		if (!constraintViolations.isEmpty()) {
			for (ConstraintViolation<T> constraintViolation : constraintViolations) {
				String property = constraintViolation.getPropertyPath().toString();
				List<String> messages = fieldMessages.get(property);
				if (messages == null) {
					messages = new ArrayList<>();
					fieldMessages.put(property, messages);
				}
				messages.add(constraintViolation.getMessage());
			}
		}
		List<ValidationErrors> validationErrors = new ArrayList<>();
		fieldMessages.forEach((k, v) -> {
			ValidationErrors errors = new ValidationErrors();
			errors.setField(k);
			errors.setMessage(v.toArray(new String[v.size()]));
			validationErrors.add(errors);
		});

		return validationErrors;
	}

}
