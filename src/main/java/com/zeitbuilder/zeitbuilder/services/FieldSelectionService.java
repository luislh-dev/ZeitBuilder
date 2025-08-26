package com.zeitbuilder.zeitbuilder.services;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifier;
import com.zeitbuilder.zeitbuilder.domain.models.FieldInfo;
import com.zeitbuilder.zeitbuilder.model.BuilderSelection;

import java.util.List;

import static java.util.Arrays.stream;

public class FieldSelectionService {

	public List<FieldInfo> getAvailableFields(PsiClass psiClass) {
		return stream(psiClass.getAllFields())
			.filter(this::isValidField)
			.map(field -> new FieldInfo(field.getName(), isDefaultSelected(field)))
			.toList();
	}

	public BuilderSelection createSelection(List<String> selectedFields, boolean includeInBuilder) {
		if (selectedFields.isEmpty()) {
			return BuilderSelection.empty();
		}
		return new BuilderSelection(selectedFields, includeInBuilder, false);
	}

	private boolean isValidField(PsiField field) {
		if (field == null) return false;

		String fieldName = field.getName();

		if (fieldName.startsWith("$")) return false;
		return !field.hasModifierProperty(PsiModifier.STATIC);
	}

	private boolean isDefaultSelected(PsiField field) {
		return !field.getName().equals("serialVersionUID");
	}
}
