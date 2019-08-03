package com.example.travelmantics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class ListActivity extends AppCompatActivity {
    private ListAdapter listAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_activity_menu, menu);

        MenuItem insertMenu = menu.findItem(R.id.insert_menu);
        if (FirebaseUtil.isAdmin) {
            insertMenu.setVisible(true);
        }else{
            insertMenu.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert_menu:
                Intent goToMainActivity = new Intent(this, MainActivity.class);
                startActivity(goToMainActivity);
                return true;
            case R.id.logout:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                FirebaseUtil.attachStateListener();
                            }
                        });
                FirebaseUtil.dettachStateListener();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUtil.openFbReference("traveldeals", this);
        recyclerView = findViewById(R.id.list_rel);
        listAdapter = new ListAdapter();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(listAdapter);

        FirebaseUtil.attachStateListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtil.dettachStateListener();
    }

    public void showMenu(){
        invalidateOptionsMenu();
    }
}
