package mobcom.iacademy.thesis.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;


import java.util.List;

import mobcom.iacademy.thesis.R;
import mobcom.iacademy.thesis.model.GroupMember;

public class GroupAdapter extends ArrayAdapter<GroupMember> {

    private LayoutInflater inflater;

    public GroupAdapter(Context context, List<GroupMember> GroupMemberList) {
        super(context, R.layout.simplerow, R.id.rowTextView, GroupMemberList);
        // Cache the LayoutInflate to avoid asking for a new one each time.
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // GroupMember to display
        GroupMember groupMember = this.getItem(position);

        // The child views in each row.
        CheckBox checkBox;
        TextView textView;

        // Create a new row view
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.simplerow, null);

            // Find the child views.
            textView = (TextView) convertView.findViewById(R.id.rowTextView);
            checkBox = (CheckBox) convertView.findViewById(R.id.CheckBox01);

            // Optimization: Tag the row with it's child views, so we don't have to
            // call findViewById() later when we reuse the row.
            convertView.setTag(new GroupHolder(textView, checkBox));

            // If CheckBox is toggled, update the GroupMember it is tagged with.
            checkBox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    GroupMember GroupMember = (GroupMember) cb.getTag();
                    GroupMember.setChecked(cb.isChecked());
                }
            });
        }
        // Reuse existing row view
        else {
            // Because we use a ViewHolder, we avoid having to call findViewById().
            GroupHolder viewHolder = (GroupHolder) convertView.getTag();
            checkBox = viewHolder.getCheckBox();
            textView = viewHolder.getTextView();
        }

        // Tag the CheckBox with the GroupMember it is displaying, so that we can
        // access the GroupMember in onClick() when the CheckBox is toggled.
        checkBox.setTag(groupMember);

        // Display GroupMember data
        checkBox.setChecked(groupMember.isChecked());
        textView.setText(groupMember.getGroupName());

        return convertView;
    }
}
