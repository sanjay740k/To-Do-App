package com.example.todoapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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

public class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.ProductViewHolder> {

    private Context context;
    private List<ToDoList> productList;
    public ToDoListAdapter(Context mCtx, List<ToDoList> productList) {
        this.context = mCtx;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.to_do_list_adapter, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        ToDoList product = productList.get(position);
        holder.textViewTitle.setText(product.getTitle());
        holder.textViewShortDesc.setText(product.getShortdesc());
        holder.textViewDater.setText(product.getDater());
        holder.textViewTimer.setText(product.getTimer());
        holder.textViewCurrentDate.setText(product.getCurrentDate());
        holder.textViewCurrentTime.setText(product.getCurrentTime());

        holder.setListner(product.getTitle());
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewShortDesc, textViewTimer, textViewDater, textViewCurrentDate, textViewCurrentTime;
        public ImageView deleteItem;
        int position;

        public ProductViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewShortDesc = itemView.findViewById(R.id.textViewShortDesc);
            textViewDater = itemView.findViewById(R.id.textViewDater);
            textViewTimer = itemView.findViewById(R.id.textViewTimer);
            textViewCurrentDate = itemView.findViewById(R.id.textViewCurrentDate);
            textViewCurrentTime = itemView.findViewById(R.id.textViewCurrentTime);
            deleteItem = itemView.findViewById(R.id.deleteButoon);
        }

        public void setListner(String curTitle){
            deleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    position = getAdapterPosition();
                    removeItem(position);
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getInstance().getCurrentUser();
                    String currentToDoId;
                    if(firebaseUser != null) {
                        currentToDoId = firebaseUser.getEmail();
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
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    Query query = ref.child("Users").child(currentToDoId).orderByChild("title").equalTo(curTitle);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                                appleSnapshot.getRef().removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e("TAG", "onCancelled", databaseError.toException());
                        }
                    });
                }
            });
        }
    }

    private void removeItem(int position) {
        productList.remove(position);
        notifyItemRemoved(position);
    }


}