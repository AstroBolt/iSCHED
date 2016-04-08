package mobcom.iacademy.thesis.routine.utilities;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseUser;

import java.util.List;

import mobcom.iacademy.thesis.R;
import mobcom.iacademy.thesis.routine.controller.EditTaskActivity;
import mobcom.iacademy.thesis.routine.model.TaskBean;

public class SimpleRecyclerAdapter extends RecyclerView.Adapter<SimpleRecyclerAdapter.VersionViewHolder> {

    List<TaskBean> taskBean;
    Context context;
    Intent intent;

    public SimpleRecyclerAdapter(List<TaskBean> taskBean, Context context) {
        this.context = context;
        this.taskBean = taskBean;

    }


    @Override
    public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.task_list, viewGroup, false);
        VersionViewHolder viewHolder = new VersionViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VersionViewHolder holder, final int position) {
        holder.title.setText(taskBean.get(position).getTitle());
        holder.timeStart.setText(taskBean.get(position).getTimeStart());
        holder.priority.setText(taskBean.get(position).getPriority());


        holder.cardItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle(taskBean.get(position).getTitle());
                alertDialog.setMessage("Description: " + taskBean.get(position).getContent() + "\n"
                        + "Priority: " + taskBean.get(position).getPriority() + "\n" + "Time Start: " + taskBean.get(position).getTimeStart() + "\n" +
                        "Due Date: " + taskBean.get(position).getDueDate());


                alertDialog.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        intent = new Intent(context, EditTaskActivity.class);
                        intent.putExtra("noteId", taskBean.get(position).getId());
                        intent.putExtra("noteTitle", taskBean.get(position).getTitle());
                        intent.putExtra("noteContent", taskBean.get(position).getContent());
                        intent.putExtra("noteDate", taskBean.get(position).getDueDate());
                        intent.putExtra("notePriority", taskBean.get(position).getPriority());
                        intent.putExtra("noteUsername", ParseUser.getCurrentUser().getUsername());
                        intent.putExtra("timeStart", taskBean.get(position).getTimeStart());
                        intent.putExtra("groupId", taskBean.get(position).getRoutineId());
                        intent.putExtra("groupAdmin", ParseUser.getCurrentUser().getUsername());
                        intent.putExtra("groupName",  taskBean.get(position).getRoutineGroup());
                        context.startActivity(intent);
                    }
                }).setNegativeButton("Cancel", null).show();
                alertDialog.show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return taskBean != null ? taskBean.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class VersionViewHolder extends RecyclerView.ViewHolder {
        CardView cardItemLayout;
        TextView title;
        TextView priority;
        TextView timeStart;
        ImageView image;

        public VersionViewHolder(View itemView) {
            super(itemView);

            cardItemLayout = (CardView) itemView.findViewById(R.id.cv);
            title = (TextView) itemView.findViewById(R.id.cardTitle);
            image = (ImageView) itemView.findViewById(R.id.person_photo);
            timeStart = (TextView) itemView.findViewById(R.id.cardTimeStart);
            priority = (TextView) itemView.findViewById(R.id.cardPriority);
            image.setImageResource(R.drawable.ic_action_pin);

        }
    }

}
