package com.zeitbuilder.zeitbuilder.action;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.zeitbuilder.zeitbuilder.generator.BuilderClassGenerator;
import com.zeitbuilder.zeitbuilder.model.BuilderSelection;
import com.zeitbuilder.zeitbuilder.ui.choosers.MemberChooserProvider;
import org.jetbrains.annotations.NotNull;

public class BuilderGeneratorAction extends AnAction {

	private final MemberChooserProvider memberChooserProvider;

	public BuilderGeneratorAction() {
		this(ApplicationManager.getApplication().getService(MemberChooserProvider.class));
	}

	public BuilderGeneratorAction(MemberChooserProvider memberChooserProvider) {
		this.memberChooserProvider = memberChooserProvider;
	}

	@Override
	public @NotNull ActionUpdateThread getActionUpdateThread() {
		return ActionUpdateThread.BGT;
	}

	@Override
	public void update(@NotNull AnActionEvent e) {
		e.getPresentation().setEnabledAndVisible(false);

		Project project = e.getProject();
		Editor editor = e.getData(CommonDataKeys.EDITOR);
		PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
		if (project == null || editor == null || file == null) return;

		boolean enabled = ReadAction.compute(() -> {
			int offset = editor.getCaretModel().getOffset();
			PsiClass psiClass = PsiTreeUtil.getParentOfType(
				file.findElementAt(offset),
				PsiClass.class
			);
			return psiClass != null;
		});

		e.getPresentation().setEnabledAndVisible(enabled);
	}

	@Override
	public void actionPerformed(@NotNull AnActionEvent e) {
		Project project = e.getProject();
		Editor editor = e.getData(CommonDataKeys.EDITOR);
		PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
		if (project == null || editor == null || file == null) return;

		PsiClass psiClass = ReadAction.compute(() ->
			PsiTreeUtil.getParentOfType(
				file.findElementAt(editor.getCaretModel().getOffset()),
				PsiClass.class
			)
		);
		if (psiClass == null) return;

		BuilderSelection selection = this.memberChooserProvider.chooseFieldsAndOptions(psiClass);

		if (selection.isEmpty() || selection.isCancelled()) {
			return;
		}

		WriteCommandAction.runWriteCommandAction(project, () ->
			BuilderClassGenerator.generateBuilder(psiClass, selection.getFieldNames(), selection.isUseInstanceBased())
		);
	}
}
