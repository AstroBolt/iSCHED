package mobcom.iacademy.thesis.menu;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import mobcom.iacademy.thesis.R;
import mobcom.iacademy.thesis.group.GroupInterfaceActivity;
import mobcom.iacademy.thesis.model.GroupBean;
import mobcom.iacademy.thesis.model.GroupMember;
import mobcom.iacademy.thesis.model.RoutineBean;

public class Group extends ListFragment {

    ArrayAdapter<GroupBean> mAdapter;
    private ArrayList<GroupBean> group;
    private ArrayList<GroupMember> groupMembersList;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    GroupMember groupMembers;
    GroupBean groupBean;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        populateListView();

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

    private void populateListView() {

       progressBar.setVisibility(View.VISIBLE);
        group = new ArrayList<>();
        mAdapter = new ArrayAdapter<>(getActivity(), R.layout.listview_row, group);
        setListAdapter(mAdapter);
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if ((ni != null) && (ni.isConnected())) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("GroupMembers");
            query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
            query.whereEqualTo("isDeleted", false);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {
                        progressBar.setVisibility(View.GONE);
                        group.clear();
                        for (ParseObject post : list) {
                            GroupBean groupBean = new GroupBean(post.getObjectId(), post.getString("usergroup"), post.getString("username"), post.getString("groupId"));
                            group.add(groupBean);
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
                    "Your device appears to be offline. Unable to fetch groups.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void inputDialog() {

        //get prompts.xml
        LayoutInflater li = LayoutInflater.from(getActivity().getApplicationContext());
        View promptsView = li.inflate(R.layout.prompt_dialog, null);
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(getActivity());

        //set dialog message
        alBuilder.setMessage(R.string.new_group);

        //set prompts.xml to alert dialog builder
        alBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);


        alBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                String userPrompt = userInput.getText().toString();

                if (!TextUtils.isEmpty(userPrompt)) {
                    addGroup(userPrompt);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Missing Fields", Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton("Cancel", null);
        AlertDialog alertDialog = alBuilder.create();
        alertDialog.show();
    }

    private void addGroup(final String groupName) {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if ((ni != null) && (ni.isConnected())) {
            //there is internet
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getString(R.string.new_group_loading));
            progressDialog.show();
            final ParseObject newGroup = new ParseObject("Group");
            newGroup.put("admin", ParseUser.getCurrentUser().getObjectId());
            newGroup.put("username", ParseUser.getCurrentUser().getUsername());
            newGroup.put("groupName", groupName.toUpperCase());
            newGroup.put("isDeleted", false);
            newGroup.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        ParseObject groups = new ParseObject("GroupMembers");
                        groups.put("groupId", newGroup.getObjectId());
                        groups.put("username", ParseUser.getCurrentUser().getUsername());
                        groups.put("userId", ParseUser.getCurrentUser().getObjectId());
                        groups.put("usergroup", groupName.toUpperCase());
                        groups.put("isDeleted", false);
                        groups.put("isAdmin", true);
                        groups.put("notified", false);
                        groups.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                populateListView();
                                progressDialog.cancel();
                            }
                        });
                    }
                }
            });
        } else {
            //no internet
            progressDialog.cancel();
            Toast.makeText(getActivity().getApplicationContext(), "Your device appears to be offline. Unable to create group.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        GroupBean note = group.get(position);
        Intent intent = new Intent(getActivity().getApplication(), GroupInterfaceActivity.class);
        intent.putExtra("intent", "groupname");
        intent.putExtra("groupId", note.getId());
        intent.putExtra("groupAdmin", note.getGroupAdmin());
        intent.putExtra("groupName", note.getGroupName());
        intent.putExtra("id", note.getGroupId());
        startActivity(intent);
    }
}
