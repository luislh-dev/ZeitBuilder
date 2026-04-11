package com.zeitbuilder.zeitbuilder.generator;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiRecordComponent;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
public class BuilderClassGenerator {

	private static final String BUILDER_METHOD = "builder";
	private static final String BUILDER_CLASS = "Builder";

	public void generateBuilder(PsiClass psiClass, List<String> fieldNames, boolean includeInBuilder) {
		removeBuilderArtifacts(psiClass);

		if (psiClass.isRecord()) {
			List<PsiRecordComponent> components = mapNamesToComponents(psiClass, fieldNames);
			generateRecordBuilder(psiClass, components, includeInBuilder);
		} else {
			List<PsiField> fields = mapNamesToFields(psiClass, fieldNames);
			generateClassBuilder(psiClass, fields, includeInBuilder);
		}
	}

	private void generateRecordBuilder(PsiClass psiClass, List<PsiRecordComponent> components, boolean includeInBuilder) {
		PsiElement endOfClass = psiClass.getLastChild();
		PsiClass builderClass = (PsiClass) psiClass.addBefore(CodeBuilderHelper.createRecordBuilderClass(psiClass, components), endOfClass);

		PsiElement anchor = builderClass;
		PsiMethod toBuilderMethod = null;
		if (includeInBuilder) {
			toBuilderMethod = (PsiMethod) psiClass.addBefore(CodeBuilderHelper.createRecordToBuilderMethod(psiClass, components), builderClass);
			anchor = toBuilderMethod;
		}

		PsiMethod builderMethod = (PsiMethod) psiClass.addBefore(CodeBuilderHelper.createBuilderMethod(psiClass), anchor);
		formatClassCode(psiClass, builderClass, toBuilderMethod, builderMethod);
	}

	private void generateClassBuilder(PsiClass psiClass, List<PsiField> fields, boolean includeInBuilder) {
		PsiElement endOfClass = psiClass.getLastChild();

		boolean hasNoArgsConstructor = false;
		PsiMethod[] constructors = psiClass.getConstructors();
		for (PsiMethod constructor : constructors) {
			if (constructor.getParameterList().isEmpty()) {
				hasNoArgsConstructor = true;
				break;
			}
		}

		if (!hasNoArgsConstructor) {
            // Check if any of the fields in the class are final and not initialized.
			// If they are final, a no-args constructor without initializing them will cause a compilation error.
			boolean hasFinalFields = false;
			for (PsiField field : psiClass.getFields()) {
				if (field.hasModifierProperty(com.intellij.psi.PsiModifier.FINAL) && field.getInitializer() == null) {
					hasFinalFields = true;
					break;
				}
			}

			if (!hasFinalFields) {
				psiClass.addBefore(CodeBuilderHelper.createNoArgsConstructor(psiClass), endOfClass);
			}
		}

		psiClass.addBefore(CodeBuilderHelper.createBuilderConstructor(psiClass, fields), endOfClass);
		
		PsiClass builderClass = (PsiClass) psiClass.addBefore(CodeBuilderHelper.createBuilderClass(psiClass, fields), endOfClass);

		PsiElement anchor = builderClass;
		PsiMethod toBuilderMethod = null;
		if (includeInBuilder) {
			toBuilderMethod = (PsiMethod) psiClass.addBefore(CodeBuilderHelper.createToBuilderMethod(psiClass, fields), builderClass);
			anchor = toBuilderMethod;
		}

		PsiMethod builderMethod = (PsiMethod) psiClass.addBefore(CodeBuilderHelper.createBuilderMethod(psiClass), anchor);
		formatClassCode(psiClass, builderClass, toBuilderMethod, builderMethod);
	}


	public void removeBuilderArtifacts(PsiClass psiClass) {
		for (PsiClass innerClass : psiClass.getInnerClasses()) {
			if (BUILDER_CLASS.equals(innerClass.getName())) {
				innerClass.delete();
			}
		}
		for (PsiMethod method : psiClass.getMethods()) {
			boolean isBuilderMethod = method.getName().equals(BUILDER_METHOD) || method.getName().equals("toBuilder");
			boolean isBuilderConstructor = method.isConstructor()
										   && method.getParameterList().getParametersCount() == 1
										   && BUILDER_CLASS.equals(method.getParameterList().getParameters()[0].getType().getPresentableText());

			if ((isBuilderMethod && method.getParameterList().isEmpty()) || isBuilderConstructor) {
				method.delete();
			}
		}
	}

	@NotNull
	private List<PsiRecordComponent> mapNamesToComponents(PsiClass psiClass, List<String> names) {
		return Arrays.stream(psiClass.getRecordComponents())
			.filter(c -> names.contains(c.getName()))
			.toList();
	}

	@NotNull
	private List<PsiField> mapNamesToFields(PsiClass psiClass, List<String> names) {
		return Arrays.stream(psiClass.getFields())
			.filter(f -> names.contains(f.getName()))
			.toList();
	}

	public void formatClassCode(PsiClass psiClass, PsiElement builderClass, PsiMethod toBuilderMethod, PsiMethod builderMethod) {
		JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(psiClass.getProject());
		styleManager.shortenClassReferences(builderClass);
		if (toBuilderMethod != null) styleManager.shortenClassReferences(toBuilderMethod);
		if (builderMethod != null) styleManager.shortenClassReferences(builderMethod);
		styleManager.optimizeImports(psiClass.getContainingFile());
	}
}