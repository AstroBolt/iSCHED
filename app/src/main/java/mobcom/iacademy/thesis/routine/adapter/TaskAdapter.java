package mobcom.iacademy.thesis.routine.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import mobcom.iacademy.thesis.R;
import mobcom.iacademy.thesis.model.TaskBean;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskHolder> {

    List<TaskBean> taskBean;
    private static OnItemClickListener mItemClickListener;

    public TaskAdapter(List<TaskBean> taskBean) {
        this.taskBean = taskBean;
    }

    @Override
    public int getItemCount() {
        return taskBean.size();
    }

    @Override
    public TaskHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.task_list, viewGroup, false);
        TaskHolder taskHolder = new TaskHolder(view);
        return taskHolder;
    }

    @Override
    public void onBindViewHolder(TaskHolder taskHolder, final int i) {
        taskHolder.title.setText(taskBean.get(i).getTitle());
        taskHolder.priority.setText(taskBean.get(i).getPriority());
        taskHolder.timeStart.setText(taskBean.get(i).getTimeStart());
        taskHolder.image.setImageResource(R.drawable.ic_action_pin);


    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class TaskHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cv;
        TextView title;
        TextView priority;
        TextView timeStart;
        ImageView image;


        public TaskHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            title = (TextView) itemView.findViewById(R.id.cardTitle);
            priority = (TextView) itemView.findViewById(R.id.cardPriority);
            timeStart = (TextView) itemView.findViewById(R.id.cardTimeStart);
            image = (ImageView) itemView.findViewById(R.id.person_photo);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }

    }

    public interface OnItemClickListener {
        public void onItemClick(View view , int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }


}
