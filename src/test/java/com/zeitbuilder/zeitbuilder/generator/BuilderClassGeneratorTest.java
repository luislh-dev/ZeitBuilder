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

	private BuilderClassGenerator builderClassGenerator;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		builderClassGenerator = new BuilderClassGenerator();
	}

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
			builderClassGenerator.generateBuilder(psiClass, List.of("name", "age"), false)
		);

		PsiClass[] innerClasses = psiClass.getInnerClasses();
		assertEquals(1, innerClasses.length);
		assertEquals("Builder", innerClasses[0].getName());

		PsiMethod[] methods = psiClass.getMethods();
		assertTrue(Arrays.stream(methods).anyMatch(m -> "builder".equals(m.getName())));

		assertTrue(Arrays.stream(methods).anyMatch(m ->
			m.isConstructor() &&
			m.getParameterList().getParametersCount() == 1 &&
			m.getParameterList().getParameters()[0].getType().getPresentableText().equals("Builder")
		));

		assertFalse(Arrays.stream(methods).anyMatch(m -> "toBuilder".equals(m.getName())));
	}

	public void testGenerateBuilderWithToBuilderMethod() {
		String testCode = """
            public class Person {
                private String name;
                private int age;
            }
            """;

		PsiFile file = myFixture.configureByText("Person.java", testCode);
		PsiClass psiClass = ((PsiJavaFile) file).getClasses()[0];

		WriteCommandAction.runWriteCommandAction(myFixture.getProject(), () ->
			builderClassGenerator.generateBuilder(psiClass, List.of("name", "age"), true)
		);

		PsiClass[] innerClasses = psiClass.getInnerClasses();
		assertEquals(1, innerClasses.length);

		PsiClass builderClass = innerClasses[0];
		assertEquals("Builder", builderClass.getName());

		PsiMethod[] builderMethods = builderClass.getMethods();
		assertTrue(Arrays.stream(builderMethods).anyMatch(m -> "name".equals(m.getName())));
		assertTrue(Arrays.stream(builderMethods).anyMatch(m -> "age".equals(m.getName())));
		assertTrue(Arrays.stream(builderMethods).anyMatch(m -> "build".equals(m.getName())));

		PsiMethod[] mainClassMethods = psiClass.getMethods();
		assertTrue(Arrays.stream(mainClassMethods).anyMatch(m -> "builder".equals(m.getName())));
		assertTrue(Arrays.stream(mainClassMethods).anyMatch(m -> "toBuilder".equals(m.getName())));
	}

	public void testGenerateRecordBuilderWithMissingFields() {
		String testCode = """
            public record Country(String name, int population, boolean active) {
            }
            """;

		PsiFile file = myFixture.configureByText("Country.java", testCode);
		PsiClass psiClass = ((PsiJavaFile) file).getClasses()[0];

		WriteCommandAction.runWriteCommandAction(myFixture.getProject(), () ->
			builderClassGenerator.generateBuilder(psiClass, List.of("name"), false)
		);

		PsiClass builderClass = psiClass.getInnerClasses()[0];
		PsiMethod buildMethod = Arrays.stream(builderClass.getMethods())
			.filter(m -> "build".equals(m.getName()))
			.findFirst().orElseThrow();

		assertTrue(buildMethod.getText().contains("return new Country(name, 0, false);"));
	}

	public void testGenerateRecordBuilderWithAllFields() {
		String testCode = """
            public record Country(String name, String code) {
            }
            """;

		PsiFile file = myFixture.configureByText("Country.java", testCode);
		PsiClass psiClass = ((PsiJavaFile) file).getClasses()[0];

		WriteCommandAction.runWriteCommandAction(myFixture.getProject(), () ->
			builderClassGenerator.generateBuilder(psiClass, List.of("name", "code"), true)
		);

		PsiClass builderClass = psiClass.getInnerClasses()[0];
		PsiMethod buildMethod = Arrays.stream(builderClass.getMethods())
			.filter(m -> "build".equals(m.getName()))
			.findFirst().orElseThrow();

		assertTrue(buildMethod.getText().contains("return new Country(name, code);"));
		
		PsiMethod toBuilderMethod = Arrays.stream(psiClass.getMethods())
			.filter(m -> "toBuilder".equals(m.getName()))
			.findFirst().orElseThrow();
			
		assertTrue(toBuilderMethod.getText().contains("new Builder().name(this.name()).code(this.code())"));
	}

	public void testRemoveBuilderArtifacts() {
		String testCode = """
            public class Person {
                private String name;
            
                private Person(Builder builder) {
                    this.name = builder.name;
                }
            
                public static Builder builder() {
                    return new Builder();
                }
            
                public Builder toBuilder() {
                    return new Builder().name(this.name);
                }
            
                public static class Builder {
                    private String name;
            
                    public Builder name(String name) {
                        this.name = name;
                        return this;
                    }
            
                    public Person build() {
                        return new Person(this);
                    }
                }
            }
            """;

		PsiFile file = myFixture.configureByText("Person.java", testCode);
		PsiClass psiClass = ((PsiJavaFile) file).getClasses()[0];

		WriteCommandAction.runWriteCommandAction(myFixture.getProject(), () ->
			builderClassGenerator.removeBuilderArtifacts(psiClass)
		);

		assertEquals(0, psiClass.getInnerClasses().length);

		PsiMethod[] methods = psiClass.getMethods();
		assertFalse(Arrays.stream(methods).anyMatch(m -> "builder".equals(m.getName())));
		assertFalse(Arrays.stream(methods).anyMatch(m -> "toBuilder".equals(m.getName())));
		assertFalse(Arrays.stream(methods).anyMatch(m ->
			m.isConstructor() &&
			m.getParameterList().getParametersCount() == 1 &&
			m.getParameterList().getParameters()[0].getType().getPresentableText().equals("Builder")
		));
	}
}