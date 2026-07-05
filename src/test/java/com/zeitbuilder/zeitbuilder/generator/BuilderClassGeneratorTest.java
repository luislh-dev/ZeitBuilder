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
			builderClassGenerator.generateBuilder(psiClass, List.of("name", "age"), false, com.zeitbuilder.zeitbuilder.model.HierarchyType.STANDARD)
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
			builderClassGenerator.generateBuilder(psiClass, List.of("name", "age"), true, com.zeitbuilder.zeitbuilder.model.HierarchyType.STANDARD)
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

	public void testGenerateBuilderCreatesNoArgsConstructorWhenNoFinalFields() {
		String testCode = """
            public class Person {
                private String name;
            }
            """;

		PsiFile file = myFixture.configureByText("Person.java", testCode);
		PsiClass psiClass = ((PsiJavaFile) file).getClasses()[0];

		WriteCommandAction.runWriteCommandAction(myFixture.getProject(), () ->
			builderClassGenerator.generateBuilder(psiClass, List.of("name"), false, com.zeitbuilder.zeitbuilder.model.HierarchyType.STANDARD)
		);

		PsiMethod[] constructors = psiClass.getConstructors();
		assertTrue("Class should have a no-args constructor",
			Arrays.stream(constructors).anyMatch(m -> m.getParameterList().isEmpty()));

		assertTrue("Class should have a private Builder constructor",
			Arrays.stream(constructors).anyMatch(m ->
				m.getParameterList().getParametersCount() == 1 &&
				m.getParameterList().getParameters()[0].getType().getPresentableText().equals("Builder")
			));
	}

	public void testGenerateBuilderDoesNotCreateNoArgsConstructorWhenFinalFieldUsed() {
		String testCode = """
            public class Person {
                private final String identifier;
                private String name;
            }
            """;

		PsiFile file = myFixture.configureByText("Person.java", testCode);
		PsiClass psiClass = ((PsiJavaFile) file).getClasses()[0];

		WriteCommandAction.runWriteCommandAction(myFixture.getProject(), () ->
			builderClassGenerator.generateBuilder(psiClass, List.of("name", "identifier"), false, com.zeitbuilder.zeitbuilder.model.HierarchyType.STANDARD)
		);

		PsiMethod[] constructors = psiClass.getConstructors();
		assertFalse("Class should NOT have a no-args constructor due to uninitialized final field",
			Arrays.stream(constructors).anyMatch(m -> m.getParameterList().isEmpty()));

		assertTrue("Class should still have a private Builder constructor",
			Arrays.stream(constructors).anyMatch(m ->
				m.getParameterList().getParametersCount() == 1 &&
				m.getParameterList().getParameters()[0].getType().getPresentableText().equals("Builder")
			));
	}

	public void testGenerateRecordBuilderWithMissingFields() {
		String testCode = """
            public record Country(String name, int population, boolean active) {
            }
            """;

		PsiFile file = myFixture.configureByText("Country.java", testCode);
		PsiClass psiClass = ((PsiJavaFile) file).getClasses()[0];

		WriteCommandAction.runWriteCommandAction(myFixture.getProject(), () ->
			builderClassGenerator.generateBuilder(psiClass, List.of("name"), false, com.zeitbuilder.zeitbuilder.model.HierarchyType.STANDARD)
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
			builderClassGenerator.generateBuilder(psiClass, List.of("name", "code"), true, com.zeitbuilder.zeitbuilder.model.HierarchyType.STANDARD)
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

	public void testGenerateExtensibleBuilderCreatesCorrectStructure() {
		String testCode = """
            public class Employee extends Person {
                private String department;
            }
            class Person {
                private String name;
            }
            """;

		PsiFile file = myFixture.configureByText("Employee.java", testCode);
		PsiClass psiClass = ((PsiJavaFile) file).getClasses()[0]; // Employee

		WriteCommandAction.runWriteCommandAction(myFixture.getProject(), () ->
			builderClassGenerator.generateBuilder(psiClass, List.of("department"), false, com.zeitbuilder.zeitbuilder.model.HierarchyType.EXTENSIBLE)
		);

		PsiClass[] innerClasses = psiClass.getInnerClasses();
		assertEquals(2, innerClasses.length); // Abstract Builder and Impl Builder

		boolean hasAbstractBuilder = Arrays.stream(innerClasses).anyMatch(c -> "Builder".equals(c.getName()));
		boolean hasImplBuilder = Arrays.stream(innerClasses).anyMatch(c -> "EmployeeBuilderImpl".equals(c.getName()));
		assertTrue("Missing abstract Builder class", hasAbstractBuilder);
		assertTrue("Missing Impl Builder class", hasImplBuilder);

		PsiMethod[] methods = psiClass.getMethods();
		assertTrue("Missing builder() method", Arrays.stream(methods).anyMatch(m -> "builder".equals(m.getName())));
		
		// Should have protected constructor with Builder<?, ?>
		assertTrue("Missing protected constructor taking Builder<?, ?>", Arrays.stream(methods).anyMatch(m ->
				m.isConstructor() &&
				m.hasModifierProperty("protected") &&
				m.getParameterList().getParametersCount() == 1 &&
				m.getParameterList().getParameters()[0].getType().getPresentableText().startsWith("Builder")
		));

		assertFalse("Should not have toBuilder by default", Arrays.stream(methods).anyMatch(m -> "toBuilder".equals(m.getName())));
	}

	public void testGenerateExtensibleBuilderWithToBuilderMethod() {
		String testCode = """
            public class Developer {
                private String language;
            }
            """;

		PsiFile file = myFixture.configureByText("Developer.java", testCode);
		PsiClass psiClass = ((PsiJavaFile) file).getClasses()[0];

		WriteCommandAction.runWriteCommandAction(myFixture.getProject(), () ->
			builderClassGenerator.generateBuilder(psiClass, List.of("language"), true, com.zeitbuilder.zeitbuilder.model.HierarchyType.EXTENSIBLE)
		);

		PsiMethod[] mainClassMethods = psiClass.getMethods();
		assertTrue("Missing builder() method", Arrays.stream(mainClassMethods).anyMatch(m -> "builder".equals(m.getName())));
		assertTrue("Missing toBuilder() method", Arrays.stream(mainClassMethods).anyMatch(m -> "toBuilder".equals(m.getName())));
	}

	public void testGenerateExtensibleBuilderMultipleTimesDoesNotDuplicateConstructor() {
		String testCode = """
            public class Developer extends Person {
                private String language;
            }
            class Person {
                private String name;
            }
            """;

		PsiFile file = myFixture.configureByText("Developer.java", testCode);
		PsiClass psiClass = ((PsiJavaFile) file).getClasses()[0];

		// First generation
		WriteCommandAction.runWriteCommandAction(myFixture.getProject(), () ->
			builderClassGenerator.generateBuilder(psiClass, List.of("language"), false, com.zeitbuilder.zeitbuilder.model.HierarchyType.EXTENSIBLE)
		);
		long constructorsFirstGen = Arrays.stream(psiClass.getConstructors()).count();

		// Second generation (simulate user regenerating the builder over the same class)
		WriteCommandAction.runWriteCommandAction(myFixture.getProject(), () ->
			builderClassGenerator.generateBuilder(psiClass, List.of("language"), false, com.zeitbuilder.zeitbuilder.model.HierarchyType.EXTENSIBLE)
		);
		long constructorsSecondGen = Arrays.stream(psiClass.getConstructors()).count();

		assertEquals("Generating builder multiple times should properly delete the previous protected constructor",
			constructorsFirstGen, constructorsSecondGen);
	}

	public void testExtensibleAbstractBuilderUsesCanonicalModifierOrder() {
		String testCode = """
            public class Developer {
                private String language;
            }
            """;

		PsiFile file = myFixture.configureByText("Developer.java", testCode);
		PsiClass psiClass = ((PsiJavaFile) file).getClasses()[0];

		WriteCommandAction.runWriteCommandAction(myFixture.getProject(), () ->
			builderClassGenerator.generateBuilder(psiClass, List.of("language"), false, com.zeitbuilder.zeitbuilder.model.HierarchyType.EXTENSIBLE)
		);

		PsiClass abstractBuilder = Arrays.stream(psiClass.getInnerClasses())
			.filter(c -> "Builder".equals(c.getName()))
			.findFirst().orElseThrow();

		// Sonar java:S1124 - modifiers must follow the canonical order (abstract before static).
		assertTrue("Abstract Builder should declare modifiers as 'abstract static'",
			abstractBuilder.getText().contains("abstract static class Builder"));
		assertFalse("Abstract Builder must not use 'static abstract' order",
			abstractBuilder.getText().contains("static abstract"));
	}
}
