package com.zeitbuilder.zeitbuilder.ui.choosers;

import com.intellij.codeInsight.generation.PsiFieldMember;
import com.intellij.ide.util.MemberChooser;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiClass;
import com.zeitbuilder.zeitbuilder.domain.models.FieldInfo;
import com.zeitbuilder.zeitbuilder.model.BuilderSelection;
import com.zeitbuilder.zeitbuilder.services.FieldSelectionService;
import com.zeitbuilder.zeitbuilder.storage.StorageSettings;
import com.zeitbuilder.zeitbuilder.ui.models.FieldSelectionResult;

import javax.swing.JCheckBox;
import java.awt.BorderLayout;
import java.util.List;
import java.util.Objects;

import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;

public class ClassMemberChooserUI implements MemberChooserProvider {

	private final FieldSelectionService fieldService;
	private final StorageSettings settings;

	public ClassMemberChooserUI() {
		this(
			ApplicationManager.getApplication().getService(FieldSelectionService.class),
			ApplicationManager.getApplication().getService(StorageSettings.class)
		);
	}

	public ClassMemberChooserUI(FieldSelectionService fieldService, StorageSettings settings) {
		this.fieldService = fieldService;
		this.settings = settings;
	}

	@Override
	public BuilderSelection chooseFieldsAndOptions(PsiClass psiClass) {
		List<FieldInfo> availableFields = fieldService.getAvailableFields(psiClass);

		if (availableFields.isEmpty()) {
			return BuilderSelection.empty();
		}

		FieldSelectionResult uiResult = showFieldSelectionDialog(psiClass, availableFields);

		if (uiResult.isCancelled()) {
			return BuilderSelection.cancelled();
		}

		return fieldService.createSelection(
			uiResult.getSelectedFieldNames(),
			uiResult.isUseInstanceBased()
		);
	}

	private FieldSelectionResult showFieldSelectionDialog(PsiClass psiClass, List<FieldInfo> fields) {
		List<PsiFieldMember> members = fields.stream()
			.map(fieldInfo -> findPsiFieldMember(psiClass, fieldInfo.getName()))
			.filter(Objects::nonNull)
			.toList();

		MemberChooser<PsiFieldMember> chooser = new MemberChooser<>(
			members.toArray(PsiFieldMember[]::new),
			false,
			true,
			psiClass.getProject(),
			false
		);

		JCheckBox useInstanceBasedCheckBox = new JCheckBox("Use instance-based builder");
		useInstanceBasedCheckBox.setSelected(settings.isUseInstanceBased());
		chooser.getContentPanel().add(useInstanceBasedCheckBox, BorderLayout.SOUTH);

		chooser.setCopyJavadocVisible(false);
		chooser.setTitle("Select Fields to Be Available in Builder");

		PsiFieldMember[] defaultSelected = members.stream()
			.filter(member -> fields.stream()
				.anyMatch(field -> field.getName().equals(member.getElement().getName()) && field.isDefaultSelected()))
			.toArray(PsiFieldMember[]::new);
		chooser.selectElements(defaultSelected);

		chooser.show();

		if (!chooser.isOK()) {
			return FieldSelectionResult.cancelled();
		}

		settings.setUseInstanceBased(useInstanceBasedCheckBox.isSelected());

		List<PsiFieldMember> selectedMembers = requireNonNull(chooser.getSelectedElements());
		List<String> fieldNames = selectedMembers.stream()
			.map(member -> member.getElement().getName())
			.toList();

		return new FieldSelectionResult(fieldNames, useInstanceBasedCheckBox.isSelected(), false);
	}

	private PsiFieldMember findPsiFieldMember(PsiClass psiClass, String fieldName) {
		return stream(psiClass.getAllFields())
			.filter(field -> fieldName.equals(field.getName()))
			.map(PsiFieldMember::new)
			.findFirst()
			.orElse(null);
	}
}
