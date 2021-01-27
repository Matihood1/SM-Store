package com.example.projektsklep;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class UsersFragment extends Fragment {
    public static final String EXTRA_USER_DATA = "EXTRA_USER_DATA";
    public static final int NEW_USER_ACTIVITY_REQUEST_CODE = 3;
    public static final int EDIT_USER_ACTIVITY_REQUEST_CODE = 4;
    private User currentUser;
    private User selectedUser;
    private RecyclerView recyclerView;
    private StoreViewModel storeViewModel;
    private MainActivity parentActivity;


    private class UserHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView userNameTextView;
        private TextView userEmailTextView;
        private User user;

        public UserHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.user_list_item, parent, false));
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            userNameTextView = itemView.findViewById(R.id.user_name);
            userEmailTextView = itemView.findViewById(R.id.user_email);
        }

        public void bind(User user) {
            this.user = user;
            userNameTextView.setText(user.getFirstName() + " " + user.getLastName());
            userEmailTextView.setText(user.getEmail());
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(parentActivity, EditUserActivity.class);
            //intent.putExtra(KEY_EXTRA_BOOK_ID, book.getId());
            selectedUser = user;
            if(currentUser.getAdmin() == true) {
                intent.putExtra(EXTRA_USER_DATA, selectedUser);
                intent.putExtra(LoginActivity.EXTRA_LOGIN_USER, currentUser);
                startActivityForResult(intent, EDIT_USER_ACTIVITY_REQUEST_CODE);
            }
            else {
                Snackbar.make(parentActivity.findViewById(R.id.drawer_layout),
                        getString(R.string.product_deleted),
                        Snackbar.LENGTH_LONG).show();
            }
        }

        @Override
        public boolean onLongClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setMessage(R.string.product_delete_confirmation);
            builder.setCancelable(true);

            builder.setPositiveButton(
                    R.string.yes,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            storeViewModel.delete(user);
                            Snackbar.make(parentActivity.findViewById(R.id.drawer_layout),
                                    getString(R.string.product_deleted),
                                    Snackbar.LENGTH_LONG).show();
                            dialog.cancel();
                        }
                    });

            builder.setNegativeButton(
                    R.string.no,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
            return true;
        }
    }

    private class UserAdapter extends RecyclerView.Adapter<UsersFragment.UserHolder> {
        private List<User> users;

        @NonNull
        @Override
        public UsersFragment.UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new UsersFragment.UserHolder(getLayoutInflater(), parent);
        }

        @Override
        public void onBindViewHolder(@NonNull UsersFragment.UserHolder holder, int position) {
            if(users != null) {
                User user = users.get(position);
                holder.bind(user);
            } else {
                Log.d("UsersFragment", "No users");
            }
        }

        @Override
        public int getItemCount() {
            if(users != null) {
                return users.size();
            } else {
                return 0;
            }
        }

        void setUsers(List<User> users) {
            this.users = users;
            notifyDataSetChanged();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        parentActivity = (MainActivity)getActivity();
        storeViewModel = parentActivity.storeViewModel;
        if (parentActivity.getIntent().hasExtra(LoginActivity.EXTRA_LOGIN_USER)) {
            currentUser = (User)parentActivity.getIntent().getSerializableExtra(LoginActivity.EXTRA_LOGIN_USER);
        }

        recyclerView = view.findViewById(R.id.users_recyclerview);
        final UsersFragment.UserAdapter adapter = new UsersFragment.UserAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(parentActivity));

        storeViewModel.findAllUsers().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable final List<User> users) {
                adapter.setUsers(users);
            }
        });

        FloatingActionButton addUserButton = parentActivity.findViewById(R.id.add_item_button);
        if(currentUser.getAdmin() == true) {
            addUserButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(parentActivity, EditUserActivity.class);
                    startActivityForResult(intent, NEW_USER_ACTIVITY_REQUEST_CODE);
                }
            });
        }
        else {
            addUserButton.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == NEW_USER_ACTIVITY_REQUEST_CODE && resultCode == android.app.Activity.RESULT_OK) {
            User user = (User) data.getSerializableExtra(EXTRA_USER_DATA);
            storeViewModel.insert(user);
            Snackbar.make(parentActivity.findViewById(R.id.coordinator_layout),
                    getString(R.string.user_added),
                    Snackbar.LENGTH_LONG).show();
        } else if(requestCode == EDIT_USER_ACTIVITY_REQUEST_CODE && resultCode == android.app.Activity.RESULT_OK) {
            User user = (User) data.getSerializableExtra(EXTRA_USER_DATA);
            storeViewModel.update(user);
            Snackbar.make(parentActivity.findViewById(R.id.coordinator_layout),
                    getString(R.string.user_edited),
                    Snackbar.LENGTH_LONG).show();
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        MenuItem clearSearchItem = menu.findItem(R.id.menu_item_clear);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                List<User> list = storeViewModel.findUserWithNameOrEmail(query);
                ((UsersFragment.UserAdapter)recyclerView.getAdapter()).setUsers(list);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        clearSearchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                searchView.setQuery("", false);
                searchView.clearFocus();
                ((UsersFragment.UserAdapter)recyclerView.getAdapter()).notifyDataSetChanged();
                return true;
            }
        });
        super.onCreateOptionsMenu(menu,inflater);
    }
}