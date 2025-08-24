package com.zeitbuilder.zeitbuilder.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.zeitbuilder.zeitbuilder.ui.choosers.MemberChooserProvider;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BuilderGeneratorActionTest extends BasePlatformTestCase {

	private BuilderGeneratorAction action;

	@Mock
	private AnActionEvent mockEvent;

	@Mock
	private Presentation mockPresentation;

	@Mock
	private MemberChooserProvider mockMemberChooserProvider;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		MockitoAnnotations.openMocks(this);
		action = new BuilderGeneratorAction(mockMemberChooserProvider);
	}

	public void testUpdateShouldDisableActionWhenNoProject() {
		when(mockEvent.getProject()).thenReturn(null);
		when(mockEvent.getPresentation()).thenReturn(mockPresentation);

		action.update(mockEvent);

		verify(mockPresentation).setEnabledAndVisible(false);
	}

	public void testUpdateShouldDisableActionWhenNoEditor() {
		when(mockEvent.getProject()).thenReturn(myFixture.getProject());
		when(mockEvent.getData(CommonDataKeys.EDITOR)).thenReturn(null);
		when(mockEvent.getPresentation()).thenReturn(mockPresentation);

		action.update(mockEvent);

		verify(mockPresentation).setEnabledAndVisible(false);
	}

	public void testUpdateShouldDisableActionWhenNoFile() {
		when(mockEvent.getProject()).thenReturn(myFixture.getProject());
		when(mockEvent.getData(CommonDataKeys.EDITOR)).thenReturn(mock(Editor.class));
		when(mockEvent.getData(CommonDataKeys.PSI_FILE)).thenReturn(null);
		when(mockEvent.getPresentation()).thenReturn(mockPresentation);

		action.update(mockEvent);

		verify(mockPresentation).setEnabledAndVisible(false);
	}

	@Override
	protected String getTestDataPath() {
		return "testData";
	}
}