package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Intent addListIntent;
    private List<ToDoList> todoList;
    FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReferenceToDo;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String currentToDoId = GetUserId();
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference("Users").child(currentToDoId);
        scoresRef.keepSynced(true);
        databaseReferenceToDo = FirebaseDatabase.getInstance().getReference().child("Users").child(currentToDoId);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            addListIntent = new Intent(MainActivity.this, AddListActivity.class);
            startActivity(addListIntent);
        });

        Toolbar toolbar = findViewById(R.id.app_bar_layout);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("To Do List");

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        todoList = new ArrayList<>();

        ToDoListAdapter adapter = new ToDoListAdapter(this, todoList);
        recyclerView.setAdapter(adapter);
    }

    protected String GetUserId() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String useremail;
        if(this.firebaseUser != null) {
            useremail = this.firebaseUser.getEmail();
            useremail = useremail != null ? useremail.replaceAll("[.$\\[\\]#/]", "") : null;
        }
        else {
            useremail = "12345";
        }
        return useremail;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseUser == null){
            SendUserToLoginActivity();
        }
        else {
            databaseReferenceToDo.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    if (snapshot.exists()) {
                        DisplayMsgs(snapshot);
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void DisplayMsgs(DataSnapshot snapshot) {
        //todoList.clear();
        Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();
        String uniqueID = UUID.randomUUID().toString();
        while(iterator.hasNext()) {
            String curdate = (String) iterator.next().getValue();
            String curtime = (String) iterator.next().getValue();
            String datemsg = (String) iterator.next().getValue();
            String descMsg = (String) iterator.next().getValue();
            String timeMsg = (String) iterator.next().getValue();
            String titlemsg = (String) iterator.next().getValue();
            todoList.add(new ToDoList(uniqueID, titlemsg, descMsg, datemsg, timeMsg, curdate, curtime));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.main_logout){
            firebaseAuth.signOut();
            SendUserToLoginActivity();
        }
        return true;
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this , LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
}