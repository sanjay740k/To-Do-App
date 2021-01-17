package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class AddListActivity extends AppCompatActivity {

    String todoTitle, todoMessage, todoDate, todoTime;
    private EditText title, description, date, time;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String currentToDoId, currDate ,currTime, curruid;
    private DatabaseReference databaseReferenceToDo, databaseReferenceGMsgKey;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);

        MainActivity mainActivity = new MainActivity();
        currentToDoId = mainActivity.GetUserId();

        Toolbar toolbar = findViewById(R.id.app_bar_layout);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("To Do List");

        databaseReferenceToDo = FirebaseDatabase.getInstance().getReference().child("Users").child(currentToDoId);

        title = findViewById(R.id.editTextTitle);
        description = findViewById(R.id.editTextMsg);
        date = findViewById(R.id.editTextDate);
        time = findViewById(R.id.editTextTime);
        Button saveInfo = findViewById(R.id.save);

        ChooseDateTime();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            saveInfo.setText("UPDATE");
            title.setText(extras.getString("title"));
            description.setText(extras.getString("desc"));
            date.setText(extras.getString("date"));
            time.setText(extras.getString("time"));
            GetUid();
            saveInfo.setOnClickListener(v -> {
                UpdateInfo();
                SendUserToMainActivity();
            });
        }
        else {
            saveInfo.setText("SAVE");
            saveInfo.setOnClickListener(v -> {
                SavesInfo();
                SendUserToMainActivity();
            });
        }
    }

    private void ChooseDateTime() {
        date.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(AddListActivity.this,
                    (view, year, monthOfYear, dayOfMonth) -> date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year), mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        time.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            @SuppressLint("SetTextI18n") TimePickerDialog timePickerDialog = new TimePickerDialog(AddListActivity.this,
                    (view, hourOfDay, minute) -> time.setText(hourOfDay + ":" + minute), mHour, mMinute, false);
            timePickerDialog.show();
        });
    }

    private void GetUid() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child("Users").child(currentToDoId).orderByChild("title").equalTo(title.getText().toString());
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    curruid = dataSnapshot1.getKey();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TAG", databaseError.getMessage());
            }
        };
        query.addListenerForSingleValueEvent(valueEventListener);
    }

    private void UpdateInfo() {
        GetAllData();
        if(TextUtils.isEmpty(todoMessage)){
            Toast.makeText(AddListActivity.this ,"Please write message", Toast.LENGTH_LONG).show();
        }
        else {
            CurrentDateandTime();
            databaseReferenceGMsgKey = databaseReferenceToDo.child(curruid);
            SetAllData();
        }
    }

    private void SavesInfo() {
        GetAllData();
        String msgKey = databaseReferenceToDo.push().getKey();
        if(TextUtils.isEmpty(todoMessage)){
            Toast.makeText(AddListActivity.this ,"Please write message", Toast.LENGTH_LONG).show();
        }
        else{
            CurrentDateandTime();
            HashMap<String ,Object> hashMapGmsgKey = new HashMap<>();
            databaseReferenceToDo.updateChildren(hashMapGmsgKey);
            if (msgKey != null) {
                databaseReferenceGMsgKey = databaseReferenceToDo.child(msgKey);
            }
            SetAllData();
        }
    }

    private void GetAllData() {
        todoTitle = title.getText().toString().trim();
        todoMessage = description.getText().toString().trim();
        todoDate = date.getText().toString();
        todoTime = time.getText().toString();
    }

    private void SetAllData() {
        HashMap<String, Object> hashMapGmsgInfo = new HashMap<>();
        hashMapGmsgInfo.put("title", todoTitle);
        hashMapGmsgInfo.put("message", todoMessage);
        hashMapGmsgInfo.put("date", todoDate);
        hashMapGmsgInfo.put("time", todoTime);
        hashMapGmsgInfo.put("curdate" ,currDate);
        hashMapGmsgInfo.put("curtime" ,currTime);
        databaseReferenceGMsgKey.updateChildren(hashMapGmsgInfo);
    }

    private void CurrentDateandTime() {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") java.text.SimpleDateFormat simpleDateFormatDate = new java.text.SimpleDateFormat("MMM dd, yyyy");
        currDate = simpleDateFormatDate.format(calendar.getTime());

        @SuppressLint("SimpleDateFormat") java.text.SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat("hh:mm a");
        currTime = simpleDateFormatTime.format(calendar.getTime());
    }

    private void SendUserToMainActivity(){
        Intent loginIntent = new Intent(AddListActivity.this , MainActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
}