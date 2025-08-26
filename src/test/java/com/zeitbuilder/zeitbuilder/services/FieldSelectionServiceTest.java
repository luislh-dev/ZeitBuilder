package com.zeitbuilder.zeitbuilder.services;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.zeitbuilder.zeitbuilder.domain.models.FieldInfo;
import com.zeitbuilder.zeitbuilder.model.BuilderSelection;

import java.util.List;

public class FieldSelectionServiceTest extends BasePlatformTestCase {
	private FieldSelectionService service;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		service = new FieldSelectionService();
	}

	public void testGetAvailableFields() {
		String testCode = """
            public class Person {
                private String name;
                private int age;
                private long serialVersionUID;
            }
            """;

		PsiFile file = myFixture.configureByText("Person.java", testCode);
		PsiClass psiClass = ((PsiJavaFile) file).getClasses()[0];

		List<FieldInfo> fields = service.getAvailableFields(psiClass);

		assertEquals(3, fields.size());

		FieldInfo nameField = fields.stream()
			.filter(f -> "name".equals(f.getName()))
			.findFirst().orElse(null);
		assertNotNull(nameField);
		assertTrue(nameField.isDefaultSelected());

		FieldInfo serialField = fields.stream()
			.filter(f -> "serialVersionUID".equals(f.getName()))
			.findFirst().orElse(null);

		assertNotNull(serialField);
		assertFalse(serialField.isDefaultSelected());
	}

	public void testCreateSelectionWithFields() {
		List<String> fields = List.of("name", "age");

		BuilderSelection selection = service.createSelection(fields, true);

		assertFalse(selection.isEmpty());
		assertFalse(selection.isCancelled());
		assertEquals(2, selection.getFieldNames().size());
		assertTrue(selection.isIncludeInBuilder());
	}

	public void testGetAvailableFieldsExcludesInvalidFields() {
	    String testCode = """
	            public class TestClass {
	                private String name;
	                private int age;
	                private static String staticField;
	                private final String finalField = "constant";
	                private long serialVersionUID;
	                private String $syntheticField;
	            }
	            """;

	    PsiFile file = myFixture.configureByText("TestClass.java", testCode);
	    PsiClass psiClass = ((PsiJavaFile) file).getClasses()[0];

	    List<FieldInfo> fields = service.getAvailableFields(psiClass);

	    assertEquals(4, fields.size());

	    List<String> fieldNames = fields.stream()
	        .map(FieldInfo::getName)
	        .toList();

	    assertTrue(fieldNames.contains("name"));
	    assertTrue(fieldNames.contains("age"));
	    assertFalse(fieldNames.contains("staticField"));
	    assertTrue(fieldNames.contains("finalField"));
	    assertFalse(fieldNames.contains("$syntheticField"));
	}

	public void testCreateSelectionEmpty() {
		BuilderSelection selection = service.createSelection(List.of(), false);

		assertTrue(selection.isEmpty());
		assertFalse(selection.isCancelled());
	}
}