package com.zeitbuilder.zeitbuilder.generator;

import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiRecordComponent;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypes;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Arrays;

public class CodeBuilderHelper {
	private static final String BUILDER_CLASS = "public static class Builder {";

	@NotNull
	public static PsiClass createRecordBuilderClass(PsiClass psiClass, List<PsiRecordComponent> components) {
		StringBuilder text = new StringBuilder(BUILDER_CLASS);

		appendFields(text, components.stream().map(c -> new FieldData(c.getType(), c.getName())).toList());
		appendFluentMethods(text, components.stream().map(c -> new FieldData(c.getType(), c.getName())).toList());

		text.append("public ").append(psiClass.getName()).append(" build() {")
			.append("return new ").append(psiClass.getName()).append("(");
			
		List<String> args = Arrays.stream(psiClass.getRecordComponents())
			.map(c -> components.contains(c) ? c.getName() : getDefaultValue(c.getType()))
			.toList();
			
		text.append(String.join(", ", args))
			.append(");}}");

		return toPsiInnerClass(psiClass, text.toString());
	}

	public static PsiMethod createRecordToBuilderMethod(PsiClass psiClass, List<PsiRecordComponent> components) {
		String bClass = psiClass.getName() + ".Builder";
		String body = "public " + bClass + " toBuilder() { return new " + bClass + "()"
					  + components.stream()
						  .map(c -> "." + c.getName() + "(this." + c.getName() + "())")
						  .reduce("", String::concat)
					  + ";}";
		return JavaPsiFacade.getElementFactory(psiClass.getProject()).createMethodFromText(body, psiClass);
	}

	@NotNull
	public static PsiClass createBuilderClass(PsiClass psiClass, List<PsiField> fields) {
		StringBuilder text = new StringBuilder(BUILDER_CLASS);

		appendFields(text, fields.stream().map(f -> new FieldData(f.getType(), f.getName())).toList());
		appendFluentMethods(text, fields.stream().map(f -> new FieldData(f.getType(), f.getName())).toList());

		text.append("public ").append(psiClass.getName()).append(" build() {")
			.append("return new ").append(psiClass.getName()).append("(this);}}");

		return toPsiInnerClass(psiClass, text.toString());
	}

	public static PsiMethod createBuilderConstructor(PsiClass psiClass, List<PsiField> fields) {
		String body = "private " + psiClass.getName() + "(Builder builder) {"
					  + fields.stream().map(f -> "this." + f.getName() + " = builder." + f.getName() + ";").reduce("", String::concat)
					  + "}";
		return JavaPsiFacade.getElementFactory(psiClass.getProject()).createMethodFromText(body, psiClass);
	}

	public static PsiMethod createToBuilderMethod(PsiClass psiClass, List<PsiField> fields) {
		String bClass = psiClass.getName() + ".Builder";
		String body = "public " + bClass + " toBuilder() { return new " + bClass + "()"
					  + fields.stream()
						  .map(f -> "." + f.getName() + "(this." + f.getName() + ")")
						  .reduce("", String::concat)
					  + ";}";
		return JavaPsiFacade.getElementFactory(psiClass.getProject()).createMethodFromText(body, psiClass);
	}

	public static PsiMethod createBuilderMethod(PsiClass psiClass) {
		String bClass = psiClass.getName() + ".Builder";
		return JavaPsiFacade.getElementFactory(psiClass.getProject())
			.createMethodFromText("public static " + bClass + " builder() { return new " + bClass + "(); }", psiClass);
	}

	private static void appendFields(StringBuilder text, List<FieldData> fields) {
		fields.forEach(f -> text.append("private ").append(f.type().getCanonicalText())
			.append(" ").append(f.name()).append(";"));
	}

	private static void appendFluentMethods(StringBuilder text, List<FieldData> fields) {
		fields.forEach(f -> text.append("public Builder ").append(f.name()).append("(")
			.append(f.type().getCanonicalText()).append(" ").append(f.name()).append(") {")
			.append("this.").append(f.name()).append(" = ").append(f.name()).append("; return this; }"));
	}

	private static PsiClass toPsiInnerClass(PsiClass psiClass, String text) {
		PsiClass dummyClass = JavaPsiFacade.getElementFactory(psiClass.getProject())
			.createClassFromText(text, psiClass);
		return dummyClass.getInnerClasses()[0];
	}

	private static String getDefaultValue(PsiType type) {
		if (PsiTypes.booleanType().equals(type)) return "false";
		if (PsiTypes.byteType().equals(type) || PsiTypes.shortType().equals(type) || PsiTypes.intType().equals(type)) return "0";
		if (PsiTypes.longType().equals(type)) return "0L";
		if (PsiTypes.floatType().equals(type)) return "0.0f";
		if (PsiTypes.doubleType().equals(type)) return "0.0d";
		if (PsiTypes.charType().equals(type)) return "'\\u0000'";
		return "null";
	}

	private record FieldData(PsiType type, String name) {}
}
