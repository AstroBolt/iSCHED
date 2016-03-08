package mobcom.iacademy.thesis.routine.days;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import mobcom.iacademy.thesis.R;
import mobcom.iacademy.thesis.model.RoutineBean;
import mobcom.iacademy.thesis.model.TaskBean;
import mobcom.iacademy.thesis.routine.adapter.TaskAdapter;
import mobcom.iacademy.thesis.routine.main.EditTaskActivity;

public class MondayFragment extends Fragment {

    Context context;
    TaskAdapter adapter;
    private List<TaskBean> posts;
    private RoutineBean routine = new RoutineBean();
    private Intent intent;
    RecyclerView rv;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.monday_layout, container, false);

        progressBar = (ProgressBar) root.findViewById(R.id.progressBar);
        rv = (RecyclerView) root.findViewById(R.id.rv);
        rv.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(context);
        rv.setLayoutManager(llm);


        intent = getActivity().getIntent();
        if (intent != null) {
            routine = new RoutineBean(intent.getStringExtra("groupId"), intent.getStringExtra("groupName"), intent.getStringExtra("groupAdmin"));
        }

        getMondayTasks();
        adapter.SetOnItemClickListener(new TaskAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TaskBean note = posts.get(position);
                intent = new Intent(getActivity().getApplication(), EditTaskActivity.class);
                intent.putExtra("selectedDay", "Sunday");
                intent.putExtra("noteId", note.getId());
                intent.putExtra("noteTitle", note.getTitle());
                intent.putExtra("noteContent", note.getContent());
                intent.putExtra("noteDate", note.getDueDate());
                intent.putExtra("notePriority", note.getPriority());
                intent.putExtra("noteUsername", note.getUsername());
                intent.putExtra("noteGroupName", note.getRoutineGroup());
                intent.putExtra("timeStart", note.getTimeStart());
                intent.putExtra("groupId", routine.getId());
                intent.putExtra("groupAdmin", routine.getRoutineAdmin());
                intent.putExtra("groupName", routine.getRoutineName());
                startActivity(intent);
            }
        });
        return root;
    }

    private void getMondayTasks() {
        //Sunday
        progressBar.setVisibility(View.VISIBLE);
        posts = new ArrayList<>();
        adapter = new TaskAdapter(posts);
        rv.setAdapter(adapter);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Routine");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.whereEqualTo("routineGroup", routine.getId());
        query.whereEqualTo("SelectedDay", 1);
        query.whereEqualTo("isCompleted", false);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    progressBar.setVisibility(View.GONE);
                    posts.clear();
                    for (ParseObject post : list) {
                        TaskBean task = new TaskBean(post.getObjectId(), post.getString("Title"), post.getString("Content"), post.getString("DueDate"), post.getString("Priority"), post.getString("username"), post.getString("routineGroup"), post.getString("timeStart"));
                        posts.add(task);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
                }
            }
        });
    }
}
