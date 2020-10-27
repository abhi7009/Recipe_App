package com.example.recipe_app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.FoodViewHolder> {

    private Context context;
    private List<ModelClass> modelClassList;
    private int lastPosition = -1;

    public Adapter(Context context, List<ModelClass> modelClassList) {
        this.context = context;
        this.modelClassList = modelClassList;
    }

    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_item,parent,false);

        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FoodViewHolder holder, final int position) {

        Glide.with(context).load(modelClassList.get(position).getItemImage()).into(holder.image_view);

        // holder.image_view.setImageResource(modelClassList.get(position).getItemImage());
        holder.title.setText(modelClassList.get(position).getItemName());
        holder.description.setText(modelClassList.get(position).getItemDescription());
        holder.price.setText(modelClassList.get(position).getItemPrice());

        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,Detail_Activity.class);
                intent.putExtra("Image", modelClassList.get(holder.getAdapterPosition()).getItemImage());
                intent.putExtra("Description",modelClassList.get(holder.getAdapterPosition()).getItemDescription());
                intent.putExtra("Key",modelClassList.get(holder.getAdapterPosition()).getKey());
                intent.putExtra("Name",modelClassList.get(holder.getAdapterPosition()).getItemName());
                intent.putExtra("Price",modelClassList.get(holder.getAdapterPosition()).getItemPrice());
                context.startActivity(intent);

            }
        });

        setAnimation(holder.itemView,position);

    }

    public void setAnimation(View viewToAnimate , int position){

        if(position>lastPosition){
            ScaleAnimation animation = new ScaleAnimation(0.0f,1.0f,0.0f,1.0f,
                    Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);

            animation.setDuration(1500);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }

    }

    @Override
    public int getItemCount() {
        return modelClassList.size();
    }


    class FoodViewHolder extends RecyclerView.ViewHolder{

        TextView title,description,price;
        ImageView image_view;
        CardView card_view;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            price = itemView.findViewById(R.id.price);
            image_view = itemView.findViewById(R.id.image_view);
            card_view = itemView.findViewById(R.id.card_view);
        }
    }

    public void filteredlist(ArrayList<ModelClass> filterlist) {
        modelClassList = filterlist;
        notifyDataSetChanged();
    }
}
