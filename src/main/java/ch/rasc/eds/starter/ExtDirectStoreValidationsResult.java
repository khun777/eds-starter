package ch.rasc.eds.starter;

import java.util.List;

import ch.ralscha.extdirectspring.bean.ExtDirectStoreResult;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ExtDirectStoreValidationsResult<T> extends ExtDirectStoreResult<T> {
	private List<ValidationErrors> validations;

	public ExtDirectStoreValidationsResult(T record) {
		super(record);
	}

	public List<ValidationErrors> getValidations() {
		return validations;
	}

	public void setValidations(List<ValidationErrors> validations) {
		this.validations = validations;
		if (this.validations != null && !this.validations.isEmpty()) {
			setSuccess(Boolean.FALSE);
		}
	}

}
