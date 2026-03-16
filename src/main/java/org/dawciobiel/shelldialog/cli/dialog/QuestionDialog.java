package org.dawciobiel.shelldialog.cli.askfortextline;

import org.dawciobiel.shelldialog.cli.result.Result;
import org.dawciobiel.shelldialog.cli.header.border.BorderType;
import org.dawciobiel.shelldialog.ui.IShowTitle;

public class QuestionDialog implements DialogQuestionShowable, IShowTitle {

    private final String title;
    private final BorderType borderType;

    public QuestionDialog(String titleQuestion) {
        this.title = titleQuestion;
        this.borderType = BorderType.BORDER_ALL;
    }

    public Result<String> show() {
return null;
    }


    @Override
    public void showTitle(String title) {

    }
}
