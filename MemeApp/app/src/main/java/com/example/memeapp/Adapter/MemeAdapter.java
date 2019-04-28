package com.example.memeapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.memeapp.Model.Meme;
import com.example.memeapp.R;

import java.util.ArrayList;


public class MemeAdapter extends RecyclerView.Adapter<MemeAdapter.ViewHolder> {
    Context mContext;
    ArrayList<Meme> mData ;


    public MemeAdapter(Context mContext, ArrayList<Meme> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.item_meme,parent,false);
        return new ViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.memeTitle.setText(mData.get(position).getTitle());
        holder.memeAuthor.setText("Posted by "+mData.get(position).getUserName());
        Glide.with(mContext).load(mData.get(position).getPicture()).into(holder.memePicture);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView memeTitle;
        TextView memeAuthor;
        ImageView memePicture;

        public ViewHolder(View itemView) {
            super(itemView);

            memeTitle = itemView.findViewById(R.id.title_view);
            memeAuthor = itemView.findViewById(R.id.posted_by_view);
            memePicture = itemView.findViewById(R.id.meme_photo_view);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent postDetailActivity = new Intent(mContext,PostDetailActivity.class);
//                    int position = getAdapterPosition();
//
//                    postDetailActivity.putExtra("title",mData.get(position).getTitle());
//                    postDetailActivity.putExtra("postImage",mData.get(position).getPicture());
//                    postDetailActivity.putExtra("memeId",mData.get(position).getMemeId());
//                    postDetailActivity.putExtra("userPhoto",mData.get(position).getUserPhoto());
//
//                    long timestamp  = (long) mData.get(position).getPostedAt();
//                    postDetailActivity.putExtra("postDate",timestamp) ;
//                    mContext.startActivity(postDetailActivity);
//                }
//            });
        }
    }
}
