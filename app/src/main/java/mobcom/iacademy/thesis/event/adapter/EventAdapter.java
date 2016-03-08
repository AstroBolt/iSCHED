package mobcom.iacademy.thesis.event.adapter;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.List;

import mobcom.iacademy.thesis.R;
import mobcom.iacademy.thesis.model.EventBean;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.TaskHolder> {

    List<EventBean> eventBean;
    private static OnItemClickListener mItemClickListener;

    public EventAdapter(List<EventBean> eventBean) {
        this.eventBean = eventBean;
    }

    @Override
    public int getItemCount() {
        return eventBean.size();
    }

    @Override
    public TaskHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_list, viewGroup, false);
        TaskHolder taskHolder = new TaskHolder(view);
        return taskHolder;
    }

    @Override
    public void onBindViewHolder(TaskHolder taskHolder, final int i) {
        taskHolder.title.setText(eventBean.get(i).getEvent());
        taskHolder.timeStart.setText(eventBean.get(i).getTimeStart() + " - " + eventBean.get(i).getTimeEnd());

        if (eventBean.get(i).getUsername().equals(ParseUser.getCurrentUser().getObjectId())) {
            taskHolder.image.setBackgroundColor(Color.parseColor("#4a148c"));
        } else {
            taskHolder.image.setBackgroundColor(Color.parseColor("#2196f3"));
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class TaskHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cv;
        TextView title;
        TextView timeStart;
        ImageView image;


        public TaskHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            title = (TextView) itemView.findViewById(R.id.cardTitle);
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
        void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }


}
