package com.zeitbuilder.zeitbuilder.ui.choosers;

import com.intellij.codeInsight.generation.PsiFieldMember;
import com.intellij.ide.util.MemberChooser;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.psi.PsiClass;
import com.zeitbuilder.zeitbuilder.domain.models.FieldInfo;
import com.zeitbuilder.zeitbuilder.model.BuilderSelection;
import com.zeitbuilder.zeitbuilder.model.HierarchyType;
import com.zeitbuilder.zeitbuilder.services.FieldSelectionService;
import com.zeitbuilder.zeitbuilder.storage.StorageSettings;
import com.zeitbuilder.zeitbuilder.ui.models.FieldSelectionResult;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
			uiResult.isUseInstanceBased(),
			uiResult.getHierarchyType()
		);
	}

	private FieldSelectionResult showFieldSelectionDialog(PsiClass psiClass, List<FieldInfo> fields) {
		List<PsiFieldMember> members = fields.stream()
			.map(fieldInfo -> findPsiFieldMember(psiClass, fieldInfo.name()))
			.filter(Objects::nonNull)
			.toList();

		MemberChooser<PsiFieldMember> chooser = new MemberChooser<>(
			members.toArray(PsiFieldMember[]::new),
			false,
			true,
			psiClass.getProject(),
			false
		);

		JCheckBox useInstanceBasedCheckBox = new JCheckBox("Generate toBuilder() method");
		useInstanceBasedCheckBox.setSelected(settings.isUseInstanceBased());

		JLabel hierarchyLabel = new JLabel("Builder Type:");
		ComboBox<HierarchyType> hierarchyComboBox = new ComboBox<>(HierarchyType.values());

		// Cargar selección previa o poner por defecto STANDARD
		HierarchyType savedHierarchyType = settings.getHierarchyType();
		if (savedHierarchyType == null) {
			savedHierarchyType = HierarchyType.STANDARD;
		}
		hierarchyComboBox.setSelectedItem(savedHierarchyType);

		JPanel hierarchyPanel = new JPanel();
		hierarchyPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 5));
		hierarchyPanel.add(hierarchyLabel);
		hierarchyPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		hierarchyPanel.add(hierarchyComboBox);

		JPanel optionsPanel = new JPanel(new BorderLayout());
		optionsPanel.add(useInstanceBasedCheckBox, BorderLayout.NORTH);
		optionsPanel.add(hierarchyPanel, BorderLayout.CENTER);

		chooser.getContentPanel().add(optionsPanel, BorderLayout.SOUTH);

		chooser.setCopyJavadocVisible(false);
		chooser.setTitle("Select Fields to Be Available in Builder");

		PsiFieldMember[] defaultSelected = members.stream()
			.filter(member -> fields.stream()
				.anyMatch(field -> field.name().equals(member.getElement().getName()) && field.defaultSelected()))
			.toArray(PsiFieldMember[]::new);
		chooser.selectElements(defaultSelected);

		chooser.show();

		if (!chooser.isOK()) {
			return FieldSelectionResult.cancelled();
		}

		settings.setUseInstanceBased(useInstanceBasedCheckBox.isSelected());
		settings.setHierarchyType((HierarchyType) hierarchyComboBox.getSelectedItem());

		List<PsiFieldMember> selectedMembers = requireNonNull(chooser.getSelectedElements());
		List<String> fieldNames = selectedMembers.stream()
			.map(member -> member.getElement().getName())
			.toList();

		return new FieldSelectionResult(fieldNames, useInstanceBasedCheckBox.isSelected(), false, (HierarchyType) hierarchyComboBox.getSelectedItem());
	}

	private PsiFieldMember findPsiFieldMember(PsiClass psiClass, String fieldName) {
		return stream(psiClass.getAllFields())
			.filter(field -> fieldName.equals(field.getName()))
			.map(PsiFieldMember::new)
			.findFirst()
			.orElse(null);
	}
}
