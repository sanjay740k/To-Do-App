package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddListActivity extends AppCompatActivity {

    private EditText title, description, date, time;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private Button saveInfo, deleteInfo;
    private String currentToDoId, Uemail, currDate ,currTime;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReferenceToDo, databaseReferenceGMsgKey, scoresRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);

        scoresRef = FirebaseDatabase.getInstance().getReference("Users");
        scoresRef.keepSynced(true);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getInstance().getCurrentUser();
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

        title = (EditText) findViewById(R.id.editTextTitle);
        description = (EditText) findViewById(R.id.editTextMsg);
        date = (EditText) findViewById(R.id.editTextDate);
        time = (EditText) findViewById(R.id.editTextTime);
        saveInfo = (Button) findViewById(R.id.save);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddListActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(AddListActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                time.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        saveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SavemsgInfo();
                Intent loginIntent = new Intent(AddListActivity.this , MainActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                finish();
            }
        });
    }

    private void SavemsgInfo() {
        String titlemsg = title.getText().toString();
        String msg = description.getText().toString();
        String datemsg = date.getText().toString();
        String timemsg = time.getText().toString();
        String msgKey = databaseReferenceToDo.push().getKey();

        if(TextUtils.isEmpty(msg)){
            Toast.makeText(AddListActivity.this ,"Please write message", Toast.LENGTH_LONG).show();
        }
        else{
            Calendar calendarDate = Calendar.getInstance();
            java.text.SimpleDateFormat simpleDateFormatDate = new java.text.SimpleDateFormat("MMM dd, yyyy");
            currDate = simpleDateFormatDate.format(calendarDate.getTime());

            Calendar calendarTime = Calendar.getInstance();
            java.text.SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat("hh:mm a");
            currTime = simpleDateFormatTime.format(calendarTime.getTime());

            HashMap<String ,Object> hashMapGmsgKey = new HashMap<>();
            databaseReferenceToDo.updateChildren(hashMapGmsgKey);

            databaseReferenceGMsgKey = databaseReferenceToDo.child(msgKey);

            HashMap<String ,Object> hashMapGmsgInfo = new HashMap<>();
            hashMapGmsgInfo.put("title" ,titlemsg);
            hashMapGmsgInfo.put("message" ,msg);
            hashMapGmsgInfo.put("date" ,datemsg);
            hashMapGmsgInfo.put("time" ,timemsg);
            hashMapGmsgInfo.put("curdate" ,currDate);
            hashMapGmsgInfo.put("curtime" ,currTime);
            databaseReferenceGMsgKey.updateChildren(hashMapGmsgInfo);
        }
    }
}