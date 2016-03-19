package mobcom.iacademy.thesis.group.utilities;

import android.widget.CheckBox;
import android.widget.TextView;


public class GroupHolder {
    private CheckBox checkBox;
    private TextView textView;

    public GroupHolder() {
    }

    public GroupHolder(TextView textView, CheckBox checkBox) {
        this.checkBox = checkBox;
        this.textView = textView;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public TextView getTextView() {
        return textView;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }
}
