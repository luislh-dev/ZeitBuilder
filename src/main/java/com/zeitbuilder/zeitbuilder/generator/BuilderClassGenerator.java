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

	public static void generateBuilder(PsiClass psiClass, List<String> fieldNames, boolean useInstanceBased) {
		List<PsiField> selectedFields = mapNamesToFields(psiClass, fieldNames);

		removeBuilderClasses(psiClass);

		PsiElement endOfClass = psiClass.getLastChild();

		PsiElement builderClass = useInstanceBased
			? psiClass.addBefore(createInstanceBasedBuilderClass(psiClass, selectedFields), endOfClass)
			: psiClass.addBefore(createBuilderClass(psiClass, selectedFields), endOfClass);
		PsiElement butMethod = psiClass.addBefore(createButMethod(psiClass, selectedFields), builderClass);
		psiClass.addBefore(createBuilderMethod(psiClass), butMethod);

		formatClassCode(psiClass, builderClass);
	}

	@NotNull
	public static PsiClass createInstanceBasedBuilderClass(PsiClass psiClass, List<PsiField> fields) {
		PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiClass.getProject());

		StringBuilder text = new StringBuilder("public static class Builder {");
		text.append("private final ").append(psiClass.getName())
			.append(" instance = new ").append(psiClass.getName()).append("();");

		for (PsiField field : fields) {
			String fieldType = field.getType().getCanonicalText();
			String fieldName = field.getName();

			text.append("public Builder ").append(fieldName).append("(")
				.append(fieldType).append(" ").append(fieldName).append(") {")
				.append("instance.set")
				.append(Character.toUpperCase(fieldName.charAt(0)))
				.append(fieldName.substring(1))
				.append("(").append(fieldName).append(");")
				.append("return this;")
				.append("}");
		}

		text.append("public ").append(psiClass.getName()).append(" build() {")
			.append("return instance;")
			.append("}}");

		PsiClass dummyClass = elementFactory.createClassFromText(text.toString(), psiClass);
		return dummyClass.getInnerClasses()[0];
	}


	@NotNull
	public static PsiClass createBuilderClass(PsiClass psiClass, List<PsiField> fields) {
		PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiClass.getProject());

		StringBuilder text = new StringBuilder("public static class Builder {");

		for (PsiField field : fields) {
			String fieldType = field.getType().getCanonicalText();
			String fieldName = field.getName();
			text.append("private ").append(fieldType).append(" ").append(fieldName).append(";");
		}

		for (PsiField field : fields) {
			String fieldType = field.getType().getCanonicalText();
			String fieldName = field.getName();

			text.append("public Builder ").append(fieldName).append("(")
					.append(fieldType).append(" ").append(fieldName).append(") {")
					.append("this.").append(fieldName).append(" = ").append(fieldName).append(";")
					.append("return this;")
					.append("}");
		}

		text.append("public ").append(psiClass.getName()).append(" build() {");
		text.append(psiClass.getName()).append(" obj = new ").append(psiClass.getName()).append("();");
		for (PsiField field : fields) {
			String fieldName = field.getName();
			text.append("obj.set")
					.append(Character.toUpperCase(fieldName.charAt(0)))
					.append(fieldName.substring(1))
					.append("(this.").append(fieldName).append(");");
		}
		text.append("return obj;");
		text.append("}}");

		PsiClass dummyClass = elementFactory.createClassFromText(text.toString(), psiClass);
		return dummyClass.getInnerClasses()[0];
	}


	public static PsiMethod createButMethod(PsiClass psiClass, List<PsiField> fields) {
		PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiClass.getProject());

		StringBuilder text = new StringBuilder("public Builder but() {");
		text.append("return new Builder()");
		for (PsiField field : fields) {
			String fieldName = field.getName();
			text.append(".").append(fieldName).append("(").append(fieldName).append(")");
		}
		text.append(";}");
		return elementFactory.createMethodFromText(text.toString(), psiClass);
	}


	public static PsiMethod createBuilderMethod(PsiClass psiClass) {
		PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiClass.getProject());

		String text = "public static Builder builder() {" +
					  "return new Builder();" +
					  "}";

		return elementFactory.createMethodFromText(text, psiClass);
	}

	public static void removeBuilderClasses(PsiClass psiClass) {
		PsiClass[] innerClasses = psiClass.getInnerClasses();
		for( PsiClass innerClass : innerClasses ){
			if( "Builder".equals(innerClass.getName()) ){
				innerClass.delete();
				break;
			}
		}

		PsiMethod[] methods = psiClass.getMethods();
		for( PsiMethod method : methods ){
			if( "but".equals(method.getName()) && method.getParameterList().getParametersCount() == 0 ){
				method.delete();
				break;
			}
		}

		for( PsiMethod method : methods ){
			if( "builder".equals(method.getName()) && method.getParameterList().getParametersCount() == 0 ){
				method.delete();
				break;
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

