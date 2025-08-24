package com.zeitbuilder.zeitbuilder.ui.choosers;

import com.intellij.psi.PsiClass;
import com.zeitbuilder.zeitbuilder.model.BuilderSelection;

public interface MemberChooserProvider {
	BuilderSelection chooseFieldsAndOptions(PsiClass psiClass);
}
