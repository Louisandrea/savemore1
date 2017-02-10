package com.example.louisa.savemore;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import Adapters.SavingGoalHolder;
import Adapters.SharedCostHolder;
import Models.SavingGoals;
import Models.SharedCost;
import butterknife.Bind;
import butterknife.ButterKnife;

public class HomePage extends BaseActivity {
    @Bind(R.id.addSharedCost)
    Button addSharedCost;
    @Bind(R.id.addSavingsGoal)
    Button addSavingGoal;
    @Bind(R.id.btn_show_shared_items)
    Button showSharedItems;
    @Bind(R.id.btn_show_saving_goals)
    Button showSavingGoals;
    @Bind(R.id.recyclerViewLayoutSavingGoals)
    RecyclerView recyclerViewLayoutSavingGoals;
    @Bind(R.id.recyclerViewLayout)
    RecyclerView recyclerView;

    private FirebaseRecyclerAdapter<SharedCost, SharedCostHolder> mRecyclerViewAdapter;
    private FirebaseRecyclerAdapter<SavingGoals, SavingGoalHolder> mRecycleViewAdapterSaving;
    LinearLayoutManager linearLayoutManager;
    LinearLayoutManager linearLayoutManager2;
    private Query shareCostQuery;
    private Query savingGoalsQuery;


    Context context;
    String loginUserEmail;
    String userEmail;

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        context = this;
        ButterKnife.bind(this);
        loginUserEmail = getUserEmail();
        userEmail = mAuth.getCurrentUser().getEmail();

        setUpRecyclerView();

        fetchData();

        setUpNavigationBar();

        setClickEvent();


    }

    //Method for navigation bar
    private void setUpNavigationBar() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

    }//End of method for navigation bar

    //Drawer content for side navigation menu
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        mDrawerLayout.closeDrawers();
                        switch (menuItem.getItemId()) {
                            case R.id.nav_home:
                                break;
                            case R.id.nav_settings:
                                startActivity(new Intent(HomePage.this, MainActivity.class));
                                break;

                        }
                        return true;
                    }
                });
    }//End of side navigation menu

    //Method for click event
    private void setClickEvent() {
        //When click on the shared cost button
        showSharedItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleView(0);
            }
        });

        //When click on the saving goals button
        showSavingGoals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleView(1);

            }
        });

        //When click on the "Plus" for shared cost image button
        addSharedCost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddSharedCost.class));
            }
        });

        //When click on the "Plus" for savings goal image button
        addSavingGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomePage.this, AddSavingGoals.class));
            }
        });

    }//End of the click event method

    //Method for switching the view for shared cost and saving goals
    private void toggleView(int type) {
        if (type == 0) {
            recyclerView.setVisibility(View.VISIBLE);
            recyclerViewLayoutSavingGoals.setVisibility(View.GONE);
        } else {
            recyclerViewLayoutSavingGoals.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);

        }
    }//End of the switching view method

    @Override
    public void onStart() {
        super.onStart();

        attachRecyclerViewAdapter();
        attachRecyclerViewAdapterSaving();
    }


    //Attach recycler view adapter to the view for shared cost
    private void attachRecyclerViewAdapter() {
        mRecyclerViewAdapter = new FirebaseRecyclerAdapter<SharedCost, SharedCostHolder>(
                SharedCost.class, R.layout.shared_cost_items, SharedCostHolder.class, shareCostQuery) {
            @Override
            protected void populateViewHolder(SharedCostHolder viewHolder, final SharedCost sharedCost, final int position) {
                viewHolder.setContent(sharedCost);
                RelativeLayout base_layout = (RelativeLayout) viewHolder.itemView.findViewById(R.id.base_layout);
                base_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateFriends(getRef(position).getKey(), sharedCost);
                        Log.i("tag", userEmail);
                        Log.i("tag", sharedCost.getSender_email());

                    }

                });


                base_layout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        showItemOption(getRef(position).getKey(), sharedCost);
                        return false;
                    }
                });

                if (userEmail.equals(sharedCost.getSender_email())) {
                    viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            showItemOption(getRef(position).getKey(), sharedCost);
                            return false;
                        }
                    });
                }
            }
        };
        recyclerView.setAdapter(mRecyclerViewAdapter);

    }//End of attach recycler adapter to the view for shared cost


    //Method for attach recycler adapter to the view for saving goals
    private void attachRecyclerViewAdapterSaving() {
        mRecycleViewAdapterSaving = new FirebaseRecyclerAdapter<SavingGoals, SavingGoalHolder>(
                SavingGoals.class, R.layout.saving_goals_items, SavingGoalHolder.class, savingGoalsQuery) {
            @Override
            protected void populateViewHolder(SavingGoalHolder viewHolder, final SavingGoals savingGoals, final int position) {
                viewHolder.setContent(savingGoals);
                RelativeLayout base_layout = (RelativeLayout) viewHolder.itemView.findViewById(R.id.base_layout);
                base_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateGoals(getRef(position).getKey(), savingGoals);
                        Log.i("tag", userEmail);
                        Log.i("tag", savingGoals.getSender_email());

                    }

                });


                if (userEmail.equals(savingGoals.getSender_email())) {
                    base_layout.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            showItemOptionSavingGoals(getRef(position).getKey(), savingGoals);
                            return false;
                        }
                    });

                }
            }
        };
        recyclerViewLayoutSavingGoals.setAdapter(mRecycleViewAdapterSaving);

    }//End of attach recycler adapter view for saving goals

    //Method Long Click Listener
    private void showItemOption(final String key, final SharedCost sharedCost) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final String[] items = new String[]{"Edit", "Delete", "Send Reminder"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        gotoToEditActivity(key, sharedCost);
                        break;
                    case 1:
                        confirmDelete(key, 0);
                        break;
                    case 2:
                        startActivity(new Intent(HomePage.this, EmailNotification.class));

                }
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }//End of Method

    //Method Long Click Listener for Saving Goals
    private void showItemOptionSavingGoals(final String key, final SavingGoals savingGoals) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final String[] items = new String[]{"Edit", "Delete"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        gotoToEditActivitySaving(key, savingGoals);
                        break;
                    case 1:
                        confirmDelete(key, 1);
                        break;
                }
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }

    //Method for edit activity shared cost
    private void gotoToEditActivity(String key, SharedCost sharedCost) {
        Intent intent = new Intent(context, EditSharedCost.class);
        intent.putExtra("key", key);
        intent.putExtra("item", sharedCost);
        startActivity(intent);

    }//End of method

    //Method for edit activity savings goal
    private void gotoToEditActivitySaving(String key, SavingGoals savingGoals) {
        Intent intent = new Intent(context, EditSavingGoals.class);
        intent.putExtra("key", key);
        intent.putExtra("item", savingGoals);
        startActivity(intent);

    }//End of method

    //Method for delete
    private void confirmDelete(final String key, final int deleteType) {
        new AlertDialog.Builder(context)
                .setMessage("Are you sure you want to delete this item")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (deleteType == 0) {
                            mDatabase.getReference("sharedCost").child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(context, "Item successfully deleted!", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(context, "Unable to delete item!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            mDatabase.getReference("savingGoals").child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(context, "Item successfully deleted!", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(context, "Unable to delete item!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }

                    }
                })
                .setNegativeButton("NO", null)
                .show();
    }//End of delete


    //Method for Updating Goals
    private void updateGoals(String key, SavingGoals savingGoals) {
        Intent intent = new Intent(context, SavingsGoalHome.class);
        intent.putExtra("key", key);
        intent.putExtra("item", savingGoals);
        startActivity(intent);

    } //End of method

    //Method for Updating Friends
    private void updateFriends(String key, SharedCost sharedCost) {
        Intent intent = new Intent(context, SharedCostHome.class);
        intent.putExtra("key", key);
        intent.putExtra("item", sharedCost);
        startActivity(intent);

    } //End of method


    @Override
    public void onStop() {
        super.onStop();
        if (mRecyclerViewAdapter != null) {
            mRecyclerViewAdapter.cleanup();
        }
        if (mRecycleViewAdapterSaving != null) {
            mRecycleViewAdapterSaving.cleanup();
        }
    }

    private void setUpRecyclerView() {
        //Recycler View for shared cost
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerViewLayoutSavingGoals.setItemAnimator(new DefaultItemAnimator());

        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        //Recycler View for saving goals
        linearLayoutManager2 = new LinearLayoutManager(context);
        linearLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewLayoutSavingGoals.setLayoutManager(linearLayoutManager2);
    }

    //Grab data from Firebase
    private void fetchData() {
        DatabaseReference databaseRef = mDatabase.getReference("sharedCost");
        shareCostQuery = databaseRef.orderByChild(loginUserEmail).equalTo(true).limitToLast(100);

        DatabaseReference databaseRefSaving = mDatabase.getReference("savingGoals");
        savingGoalsQuery = databaseRefSaving.orderByChild(loginUserEmail).equalTo(true).limitToLast(100);


    }//End of method


}
