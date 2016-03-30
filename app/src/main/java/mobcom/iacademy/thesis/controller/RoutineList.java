package mobcom.iacademy.thesis.controller;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


import java.util.ArrayList;
import java.util.List;

import mobcom.iacademy.thesis.R;
import mobcom.iacademy.thesis.routine.model.RoutineBean;
import mobcom.iacademy.thesis.routine.controller.TaskInterfaceActivity;

@SuppressWarnings("all")
@TargetApi(11)
public class RoutineList extends ListFragment {


    ArrayAdapter<RoutineBean> mAdapter;
    private ArrayList<RoutineBean> routine;
    private ProgressBar progressBar;
    private TextView emptyView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_routine, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        emptyView = (TextView) view.findViewById(R.id.empty);
        emptyView.setVisibility(View.GONE);

        //set delay
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 1s = 1000ms
                progressBar.setVisibility(View.VISIBLE);
                populateListView();
            }
        }, 1500);


        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                inputDialog();
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView sv = new SearchView(((MainActivity) getActivity()).getSupportActionBar().getThemedContext());
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, sv);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onSearchTextSubmit(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                onSearchTextChanged(newText);
                return false;
            }
        });

    }

    @TargetApi(11)
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        RoutineBean routineBean = routine.get(position);
        Intent intent = new Intent(getActivity().getApplication(), TaskInterfaceActivity.class);
        intent.putExtra("groupId", routineBean.getId());
        intent.putExtra("groupName", routineBean.getRoutineName());
        intent.putExtra("groupAdmin", routineBean.getRoutineAdmin());
        startActivity(intent);
    }

    private void populateListView() {
        routine = new ArrayList<>();
        mAdapter = new ArrayAdapter<>(getActivity(), R.layout.listview_row, routine);
        setListAdapter(mAdapter);
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if ((ni != null) && (ni.isConnected())) {


            ParseQuery<ParseObject> query2 = ParseQuery.getQuery("RoutineGroup");
            query2.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if(e == null){
                        ParseObject.pinAllInBackground(list);
                    }else{
                        Toast.makeText(getActivity().getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            ParseQuery<ParseObject> query = ParseQuery.getQuery("RoutineGroup");
            query.fromLocalDatastore();
            query.whereEqualTo("username", ParseUser.getCurrentUser().getObjectId());
            query.whereEqualTo("isDeleted", false);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {
                        progressBar.setVisibility(View.GONE);
                        routine.clear();
                        for (ParseObject post : list) {
                            RoutineBean routineBean = new RoutineBean(post.getObjectId(), post.getString("routineGroup"), post.getString("username"));
                            routine.add(routineBean);
                        }

                        if (list.size() == 0) {
                            emptyView.setVisibility(View.VISIBLE);
                        } else {
                            emptyView.setVisibility(View.GONE);
                        }

                        ((ArrayAdapter<RoutineBean>) getListAdapter()).notifyDataSetChanged();

                    } else {
                        Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
                    }
                }
            });


        }else{
            progressBar.setVisibility(View.GONE);
            // If there is no connection, let the user know the sync didn't happen
            Toast.makeText(
                    getActivity().getApplicationContext(),
                    "Your device appears to be offline. Unable to fetch routines.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void onSearchTextChanged(String userRoutine) {
        progressBar.setVisibility(View.VISIBLE);
        routine = new ArrayList<>();
        mAdapter = new ArrayAdapter<>(getActivity(), R.layout.listview_row, routine);
        setListAdapter(mAdapter);
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if ((ni != null) && (ni.isConnected())) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("RoutineGroup");
            query.fromLocalDatastore();
            query.whereStartsWith("routineGroup", userRoutine.toUpperCase());
            query.whereEqualTo("username", ParseUser.getCurrentUser().getObjectId());
            query.whereEqualTo("isDeleted", false);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {
                        progressBar.setVisibility(View.GONE);
                        routine.clear();
                        for (ParseObject post : list) {
                            RoutineBean routineBean = new RoutineBean(post.getObjectId(), post.getString("routineGroup"), post.getString("username"));
                            routine.add(routineBean);
                        }
                        ((ArrayAdapter<RoutineBean>) getListAdapter()).notifyDataSetChanged();

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
        }else{
            progressBar.setVisibility(View.GONE);
            // If there is no connection, let the user know the sync didn't happen
            Toast.makeText(
                    getActivity().getApplicationContext(),
                    "Your device appears to be offline. Unable to fetch routines.",
                    Toast.LENGTH_LONG).show();
        }


    }

    private void onSearchTextSubmit(String userRoutine) {

        progressBar.setVisibility(View.VISIBLE);
        routine = new ArrayList<>();
        mAdapter = new ArrayAdapter<>(getActivity(), R.layout.listview_row, routine);
        setListAdapter(mAdapter);
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if ((ni != null) && (ni.isConnected())) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("RoutineGroup");
            query.fromLocalDatastore();
            query.whereEqualTo("routineGroup", userRoutine.toUpperCase());
            query.whereEqualTo("username", ParseUser.getCurrentUser().getObjectId());
            query.whereEqualTo("isDeleted", false);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {
                        progressBar.setVisibility(View.GONE);
                        routine.clear();
                        for (ParseObject post : list) {
                            RoutineBean routineBean = new RoutineBean(post.getObjectId(), post.getString("routineGroup"), post.getString("username"));
                            routine.add(routineBean);
                        }
                        ((ArrayAdapter<RoutineBean>) getListAdapter()).notifyDataSetChanged();

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
        }else{
            progressBar.setVisibility(View.GONE);
            // If there is no connection, let the user know the sync didn't happen
            Toast.makeText(
                    getActivity().getApplicationContext(),
                    "Your device appears to be offline. Unable to fetch routines.",
                    Toast.LENGTH_LONG).show();
        }


    }

    private void inputDialog() {

        //get prompts.xml
        LayoutInflater li = LayoutInflater.from(getActivity().getApplicationContext());
        View promptsView = li.inflate(R.layout.prompt_dialog, null);
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(getActivity());

        //set dialog message
        alBuilder.setMessage("New Routine");

        //set prompts.xml to alert dialog builder
        alBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);


        alBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                String userPrompt = userInput.getText().toString();

                if (!TextUtils.isEmpty(userPrompt)) {
                    addRoutineGroup(userPrompt);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Missing Fields", Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton("Cancel", null);
        AlertDialog alertDialog = alBuilder.create();
        alertDialog.show();
    }

    private void addRoutineGroup(String routineName) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if ((ni != null) && (ni.isConnected())) {
            progressDialog.setMessage(getString(R.string.addRoutine));
            progressDialog.setCancelable(false);
            progressDialog.show();
            final ParseObject routine = new ParseObject("RoutineGroup");
            routine.put("routineGroup", routineName.toUpperCase());
            routine.put("username", ParseUser.getCurrentUser().getObjectId());
            routine.put("isDeleted", false);
            routine.saveEventually();
            Toast.makeText(getActivity().getApplicationContext(), "Routine Successfully Created.", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.cancel();
            // If there is no connection, let the user know the sync didn't happen
            Toast.makeText(
                    getActivity().getApplicationContext(),
                    "Your device appears to be offline. Unable to submit.",
                    Toast.LENGTH_LONG).show();
        }
    }
}
