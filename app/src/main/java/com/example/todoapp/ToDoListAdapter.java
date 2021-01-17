package com.example.todoapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.ListViewHolder> {

    private final Context context;
    private final List<ToDoList> todoLists;
    public ToDoListAdapter(Context context, List<ToDoList> todoLists) {
        this.context = context;
        this.todoLists = todoLists;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.to_do_list_adapter, null);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        ToDoList toDoList = todoLists.get(position);
        holder.textViewTitle.setText(toDoList.getTitle());
        holder.textViewShortDesc.setText(toDoList.getShortdesc());
        holder.textViewDater.setText(toDoList.getDater());
        holder.textViewTimer.setText(toDoList.getTimer());
        holder.textViewCurrentDate.setText(toDoList.getCurrentDate());
        holder.textViewCurrentTime.setText(toDoList.getCurrentTime());

        holder.setListener(toDoList.getTitle());
    }

    @Override
    public int getItemCount() {
        return todoLists.size();
    }
    class ListViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewShortDesc, textViewTimer, textViewDater, textViewCurrentDate, textViewCurrentTime;
        public ImageView deleteItem;
        int position;

        public ListViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewShortDesc = itemView.findViewById(R.id.textViewShortDesc);
            textViewDater = itemView.findViewById(R.id.textViewDater);
            textViewTimer = itemView.findViewById(R.id.textViewTimer);
            textViewCurrentDate = itemView.findViewById(R.id.textViewCurrentDate);
            textViewCurrentTime = itemView.findViewById(R.id.textViewCurrentTime);
            deleteItem = itemView.findViewById(R.id.deleteButoon);

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, AddListActivity.class);
                intent.putExtra("title", textViewTitle.getText().toString());
                intent.putExtra("desc", textViewShortDesc.getText().toString());
                intent.putExtra("date", textViewDater.getText().toString());
                intent.putExtra("time", textViewTimer.getText().toString());
                context.startActivity(intent);
            });
        }

        public void setListener(String curTitle){
            deleteItem.setOnClickListener(v -> {
                position = getAdapterPosition();
                removeItemFromList(position);
                removeItemFromFirebase(curTitle);
            });
        }
    }

    private void removeItemFromFirebase(String curTitle) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentToDoId;
        if(firebaseUser != null) {
            currentToDoId = firebaseUser.getEmail();
            currentToDoId = currentToDoId != null ? currentToDoId.replaceAll("[.$\\[\\]#/]", "") : null;
        }
        else {
            currentToDoId = "12345";
        }
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = null;
        if (currentToDoId != null) {
            query = ref.child("Users").child(currentToDoId).orderByChild("title").equalTo(curTitle);
        }

        if (query != null) {
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                        appleSnapshot.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("TAG", "onCancelled", databaseError.toException());
                }
            });
        }
    }

    private void removeItemFromList(int position) {
        todoLists.remove(position);
        notifyItemRemoved(position);
    }
}