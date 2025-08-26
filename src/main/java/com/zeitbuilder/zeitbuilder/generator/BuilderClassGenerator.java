package com.zeitbuilder.zeitbuilder.generator;

import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class BuilderClassGenerator {
	private BuilderClassGenerator() {}

	public static void generateBuilder(PsiClass psiClass, List<String> fieldNames, boolean includeInBuilder) {
		List<PsiField> selectedFields = mapNamesToFields(psiClass, fieldNames);

		removeBuilderArtifacts(psiClass);

		PsiElement endOfClass = psiClass.getLastChild();

		psiClass.addBefore(createBuilderConstructor(psiClass, selectedFields), endOfClass);

		PsiElement builderClass = psiClass.addBefore(createBuilderClass(psiClass, selectedFields), endOfClass);

		PsiElement anchor = builderClass;
		if (includeInBuilder) {
			anchor = psiClass.addBefore(createToBuilderMethod(psiClass, selectedFields), builderClass);
		}

		psiClass.addBefore(createBuilderMethod(psiClass), anchor);

		formatClassCode(psiClass, builderClass);
	}


	@NotNull
	public static PsiClass createBuilderClass(PsiClass psiClass, List<PsiField> fields) {
		PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiClass.getProject());

		StringBuilder text = new StringBuilder("public static class Builder {");

		// campos en el builder
		for (PsiField field : fields) {
			String fieldType = field.getType().getCanonicalText();
			String fieldName = field.getName();
			text.append("private ").append(fieldType).append(" ").append(fieldName).append(";");
		}

		// métodos fluidos
		for (PsiField field : fields) {
			String fieldType = field.getType().getCanonicalText();
			String fieldName = field.getName();
			text.append("public Builder ").append(fieldName).append("(")
				.append(fieldType).append(" ").append(fieldName).append(") {")
				.append("this.").append(fieldName).append(" = ").append(fieldName).append(";")
				.append("return this;")
				.append("}");
		}

		// build()
		text.append("public ").append(psiClass.getName()).append(" build() {")
			.append("return new ").append(psiClass.getName()).append("(this);")
			.append("}}");

		PsiClass dummyClass = elementFactory.createClassFromText(text.toString(), psiClass);
		return dummyClass.getInnerClasses()[0];
	}

	public static PsiMethod createBuilderConstructor(PsiClass psiClass, List<PsiField> fields) {
		PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiClass.getProject());

		StringBuilder text = new StringBuilder("private ").append(psiClass.getName()).append("(Builder builder) {");
		for (PsiField field : fields) {
			String fieldName = field.getName();
			text.append("this.").append(fieldName).append(" = builder.").append(fieldName).append(";");
		}
		text.append("}");

		return elementFactory.createMethodFromText(text.toString(), psiClass);
	}

	public static PsiMethod createToBuilderMethod(PsiClass psiClass, List<PsiField> fields) {
		PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiClass.getProject());

		StringBuilder text = new StringBuilder("public Builder toBuilder() {");
		text.append("return new Builder()");
		for (PsiField field : fields) {
			String fieldName = field.getName();
			text.append(".").append(fieldName).append("(this.").append(fieldName).append(")");
		}
		text.append(";}");
		return elementFactory.createMethodFromText(text.toString(), psiClass);
	}

	public static PsiMethod createBuilderMethod(PsiClass psiClass) {
		PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiClass.getProject());
		String text = "public static Builder builder() { return new Builder(); }";
		return elementFactory.createMethodFromText(text, psiClass);
	}

	public static void removeBuilderArtifacts(PsiClass psiClass) {
		// eliminar inner class Builder
		for (PsiClass innerClass : psiClass.getInnerClasses()) {
			if ("Builder".equals(innerClass.getName())) {
				innerClass.delete();
				break;
			}
		}

		// eliminar métodos previos builder(), toBuilder() y constructor Person(Builder)
		for (PsiMethod method : psiClass.getMethods()) {
			String name = method.getName();
			if ((name.equals("builder") || name.equals("toBuilder"))
				&& method.getParameterList().getParametersCount() == 0) {
				method.delete();
			}
			if (method.isConstructor() && method.getParameterList().getParametersCount() == 1
				&& method.getParameterList().getParameters()[0].getType().getPresentableText().equals("Builder")) {
				method.delete();
			}
		}
	}

	@NotNull
	private static List<PsiField> mapNamesToFields(PsiClass psiClass, List<String> selectedFieldNames) {
		return Arrays.stream(psiClass.getFields())
			.filter(f -> selectedFieldNames.contains(f.getName()))
			.toList();
	}

	public static void formatClassCode(PsiClass psiClass, PsiElement builderClass) {
		JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(psiClass.getProject());
		styleManager.shortenClassReferences(builderClass);
		styleManager.optimizeImports(psiClass.getContainingFile());
	}
}
