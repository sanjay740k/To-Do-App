package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public String currentToDoId;
    private Intent addListIntent;
    private FloatingActionButton fab;
    private FirebaseAuth firebaseAuth;
    private List<ToDoList> todoList;
    private RecyclerView recyclerView;
    private String Uemail;
    private DatabaseReference databaseReferenceToDo ,scoresRef;
    private FirebaseUser firebaseUser;
    //public static final String strId = "User_Id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scoresRef = FirebaseDatabase.getInstance().getReference("Users");
        scoresRef.keepSynced(true);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null) {
            Uemail = firebaseUser.getEmail();
            currentToDoId = Uemail;
            currentToDoId = currentToDoId.replace(".", "");
            currentToDoId = currentToDoId.replace("$", "");
            currentToDoId = currentToDoId.replace("[", "");
            currentToDoId = currentToDoId.replace("]", "");
            currentToDoId = currentToDoId.replace("#", "");
            currentToDoId = currentToDoId.replace("/", "");
        }
        else {
            currentToDoId = "12345";
        }
        databaseReferenceToDo = FirebaseDatabase.getInstance().getReference().child("Users").child(currentToDoId);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addListIntent = new Intent(MainActivity.this, AddListActivity.class);
                startActivity(addListIntent);
            }
        });

        Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.app_bar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("To Do List");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        todoList = new ArrayList<>();

        ToDoListAdapter adapter = new ToDoListAdapter(this, todoList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseUser == null){
            SendUserToLoginActivity();
        }
        databaseReferenceToDo.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    DisplayMsgs(snapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    DisplayMsgs(snapshot);
                }
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

    private void DisplayMsgs(DataSnapshot snapshot) {
        Iterator iterator = snapshot.getChildren().iterator();
        String uniqueID = UUID.randomUUID().toString();
        while(iterator.hasNext()) {
            String curdate = (String) ((DataSnapshot) iterator.next()).getValue();
            String curtime = (String) ((DataSnapshot) iterator.next()).getValue();
            String datemsg = (String) ((DataSnapshot) iterator.next()).getValue();
            String descMsg = (String) ((DataSnapshot) iterator.next()).getValue();
            String timeMsg = (String) ((DataSnapshot) iterator.next()).getValue();
            String titlemsg = (String) ((DataSnapshot) iterator.next()).getValue();
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

        if(item.getItemId()==R.id.sort_by_op){
            return true;
        }
        else if(item.getItemId()==R.id.sort_by_op){

        }
        else if(item.getItemId() == R.id.setting_op){

        }
        else if(item.getItemId() == R.id.main_logout){
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