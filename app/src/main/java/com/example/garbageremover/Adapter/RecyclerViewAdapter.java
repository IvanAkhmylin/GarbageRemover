package com.example.garbageremover.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.garbageremover.Model.RequestModel;
import com.example.garbageremover.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    Context context;
    ArrayList<RequestModel> requests;
    onItemClickListener mListener;
    public RecyclerViewAdapter(Context context , ArrayList<RequestModel> requests){
        this.context = context;
        this.requests = requests;
    }
    public interface onItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(onItemClickListener listener){
        mListener = listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.clean_request_recycler_item,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Glide.with(context).load(Uri.parse(requests.get(i).getCustomerImageUri()))
                .placeholder(R.drawable.ic_photo_camera_black_24dp)
                .apply(new RequestOptions()
                .override(1500,2000))
                .into(viewHolder.recyclerImage);
        viewHolder.recyclerCustomer.setText(requests.get(i).getCustomerName());
        viewHolder.recyclerPrice.setText(requests.get(i).getPayment());
        viewHolder.recyclerDescription.setText(requests.get(i).getDescription());
        viewHolder.recyclerAddress.setText(requests.get(i).getAddress());
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView recyclerImage ;
        TextView recyclerCustomer,recyclerPrice,recyclerDescription,recyclerAddress;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            mListener.onItemClick(position);
                        }
                    }
                }
            });
            recyclerImage = itemView.findViewById(R.id.recyclerImage);
            recyclerCustomer = itemView.findViewById(R.id.recyclerCustomer);
            recyclerPrice = itemView.findViewById(R.id.recyclerPrice);
            recyclerDescription = itemView.findViewById(R.id.recyclerDescription);
            recyclerAddress = itemView.findViewById(R.id.recyclerAddress);
        }



    }
}

