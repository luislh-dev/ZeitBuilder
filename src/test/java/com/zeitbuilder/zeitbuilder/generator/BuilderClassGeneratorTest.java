package com.zeitbuilder.zeitbuilder.generator;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import java.util.Arrays;
import java.util.List;

public class BuilderClassGeneratorTest extends BasePlatformTestCase {

	public void testGenerateBuilderCreatesCorrectStructure() {
		String testCode = """
            public class Person {
                private String name;
                private int age;
            }
            """;

		PsiFile file = myFixture.configureByText("Person.java", testCode);
		PsiClass psiClass = ((PsiJavaFile) file).getClasses()[0];

		WriteCommandAction.runWriteCommandAction(myFixture.getProject(), () ->
			BuilderClassGenerator.generateBuilder(psiClass, List.of("name", "age"), false)
		);
		PsiClass[] innerClasses = psiClass.getInnerClasses();
		assertEquals(1, innerClasses.length);
		assertEquals("Builder", innerClasses[0].getName());

		PsiMethod[] methods = psiClass.getMethods();
		assertTrue(Arrays.stream(methods).anyMatch(m -> "builder".equals(m.getName())));
		assertTrue(Arrays.stream(methods).anyMatch(m -> "but".equals(m.getName())));
	}

	public void testGenerateInstanceBasedBuilder() {
		String testCode = """
            public class Person {
                private String name;
                public void setName(String name) { this.name = name; }
            }
            """;

		PsiFile file = myFixture.configureByText("Person.java", testCode);
		PsiClass psiClass = ((PsiJavaFile) file).getClasses()[0];

		WriteCommandAction.runWriteCommandAction(myFixture.getProject(), () ->
			BuilderClassGenerator.generateBuilder(psiClass, List.of("name"), true)
		);

		PsiClass[] innerClasses = psiClass.getInnerClasses();
		assertEquals(1, innerClasses.length);

		PsiClass builderClass = innerClasses[0];
		assertEquals("Builder", builderClass.getName());

		PsiMethod[] builderMethods = builderClass.getMethods();

		assertTrue(Arrays.stream(builderMethods).anyMatch(m -> "name".equals(m.getName())));

		assertTrue(Arrays.stream(builderMethods).anyMatch(m -> "build".equals(m.getName())));

		PsiMethod[] mainClassMethods = psiClass.getMethods();
		assertTrue(Arrays.stream(mainClassMethods).anyMatch(m -> "builder".equals(m.getName())));
		assertTrue(Arrays.stream(mainClassMethods).anyMatch(m -> "but".equals(m.getName())));
	}
}