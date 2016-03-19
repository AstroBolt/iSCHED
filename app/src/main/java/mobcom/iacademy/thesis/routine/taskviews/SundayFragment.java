package mobcom.iacademy.thesis.routine.taskviews;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import mobcom.iacademy.thesis.R;
import mobcom.iacademy.thesis.routine.model.RoutineBean;
import mobcom.iacademy.thesis.routine.model.TaskBean;
import mobcom.iacademy.thesis.routine.utilities.TaskAdapter;
import mobcom.iacademy.thesis.routine.controller.EditTaskActivity;


public class SundayFragment extends Fragment {

    Context context;
    private List<TaskBean> posts = new ArrayList<>();
    private RoutineBean routine = new RoutineBean();
    private Intent intent;
    RecyclerView rv;
    private ProgressBar progressBar;
    TaskAdapter adapter;
    private TextView emptyView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.sunday_layout, container, false);
        progressBar = (ProgressBar) root.findViewById(R.id.progressBar);
        emptyView = (TextView) root.findViewById(R.id.empty);
        emptyView.setVisibility(View.GONE);
        rv = (RecyclerView) root.findViewById(R.id.rv);
        rv.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(context);
        rv.setLayoutManager(llm);


        intent = getActivity().getIntent();
        if (intent != null) {
            routine = new RoutineBean(intent.getStringExtra("groupId"), intent.getStringExtra("groupName"), intent.getStringExtra("groupAdmin"));
        }

        getSundayTasks();

        adapter.SetOnItemClickListener(new TaskAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                try{
                TaskBean note = posts.get(position);
                viewTask(note.getId(),
                        note.getTitle(),
                        note.getPriority(),
                        note.getDueDate(),
                        note.getTimeStart(),
                        note.getContent(),
                        note.getUsername(),
                        note.getRoutineGroup(),
                        routine.getId(),
                        routine.getRoutineAdmin(),
                        routine.getRoutineName());
                }catch (IndexOutOfBoundsException ibe){
                    TaskBean note = posts.get(position);
                    viewTask(note.getId(),
                            note.getTitle(),
                            note.getPriority(),
                            note.getDueDate(),
                            note.getTimeStart(),
                            note.getContent(),
                            note.getUsername(),
                            note.getRoutineGroup(),
                            routine.getId(),
                            routine.getRoutineAdmin(),
                            routine.getRoutineName());
                }
            }
        });

        return root;
    }

    private void getSundayTasks() {
        //Sunday
        progressBar.setVisibility(View.VISIBLE);
        adapter = new TaskAdapter(posts);
        rv.setAdapter(adapter);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Routine");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.whereEqualTo("routineGroup", routine.getId());
        query.whereEqualTo("SelectedDay", 0);
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
                    if(list.size() == 0){
                        emptyView.setVisibility(View.VISIBLE);
                    }else{
                        emptyView.setVisibility(View.GONE);
                    }
                } else {
                    Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
                }
            }
        });
    }

    private void viewTask(final String id, final String title, final String priority, final String dueDate, final String timeStart, final String content, final String owner, final String routineGroup, final String routineId, final String routineAdmin, final String routineName) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setCancelable(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage("Description: " + content + "\n" + "Day: Sunday" + "\n"
                + "Priority: " + priority + "\n" + "Time Start: " + timeStart + "\n" +
                "Due Date: " + dueDate);
        alertDialog.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                intent = new Intent(getActivity().getApplication(), EditTaskActivity.class);
                intent.putExtra("selectedDay", "Sunday");
                intent.putExtra("noteId", id);
                intent.putExtra("noteTitle", title);
                intent.putExtra("noteContent", content);
                intent.putExtra("noteDate", dueDate);
                intent.putExtra("notePriority", priority);
                intent.putExtra("noteUsername", owner);
                intent.putExtra("noteGroupName", routineGroup);
                intent.putExtra("timeStart", timeStart);
                intent.putExtra("groupId", routineId);
                intent.putExtra("groupAdmin", routineAdmin);
                intent.putExtra("groupName", routineName);
                startActivity(intent);
            }
        }).setNegativeButton("Cancel", null).show();

    }


}
