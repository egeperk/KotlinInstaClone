package com.egeperk.kotlininstaclone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.egeperk.kotlininstaclone.databinding.ActivityUploadBinding
import com.egeperk.kotlininstaclone.databinding.ReyclerRowBinding
import com.egeperk.kotlininstaclone.model.Post
import com.squareup.picasso.Picasso

class PostAdapter(val postArrayList: ArrayList<Post>): RecyclerView.Adapter<PostAdapter.PostHolder>() {

    class PostHolder (val binding: ReyclerRowBinding) : RecyclerView.ViewHolder(binding.root){
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding = ReyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PostHolder(binding)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.binding.recyclerEmailText.text = postArrayList.get(position).email
        Picasso.get().load(postArrayList.get(position).downloadUrl).into(holder.binding.recyclerImageView)
        holder.binding.recyclerCommentText.text = postArrayList.get(position).comment
    }

    override fun getItemCount(): Int {
        return postArrayList.size
    }
}