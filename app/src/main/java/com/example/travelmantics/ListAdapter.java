package com.example.travelmantics;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder>{
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    public ArrayList<TravelDeal> mData;

    public ListAdapter() {
        firebaseDatabase = FirebaseUtil.firebaseDatabase;
        databaseReference = FirebaseUtil.databaseReference;
        mData = FirebaseUtil.mData;
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.hasChildren()) {
                    TravelDeal travelDeal = dataSnapshot.getValue(TravelDeal.class);
                    //here we set the key to our respective class model items since it hasn't been set
                    travelDeal.setId(dataSnapshot.getKey());
                    mData.add(travelDeal);
                    notifyItemChanged(mData.size()-1);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_item,parent,false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        TravelDeal deal = mData.get(position);
        holder.tvTitle.setText(deal.getTitle());
        holder.tvDescription.setText(deal.getDescription());
        holder.tvPrice.setText(deal.getPrice());
        holder.showImage(deal.getImageUrl());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTitle,tvDescription,tvPrice;
        ImageView img;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvPrice = itemView.findViewById(R.id.tv_price);
            img = itemView.findViewById(R.id.single_items_img);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            TravelDeal deal = mData.get(position);
            Intent intent = new Intent(view.getContext(),MainActivity.class);
            intent.putExtra("traveldeal",deal);
            view.getContext().startActivity(intent);

        }
        public void showImage(String url){
            if (url != null && !url.isEmpty()) {
                Glide.with(img.getContext())
                        .load(url)
                        .placeholder(R.drawable.placeholder)
                        .into(img);

            }
        }
    }
}
