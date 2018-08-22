package com.lamia.sodope

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.support.constraint.R.id.parent
import android.support.v4.content.res.TypedArrayUtils.getString
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlin.coroutines.experimental.coroutineContext
import com.bumptech.glide.request.RequestOptions



class BlogRecyclerAdapter(val blog_list: List<BlogPost>) : RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder>() {

    var fbFireStore:FirebaseFirestore? = null
    var mAuth:FirebaseAuth? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogRecyclerAdapter.ViewHolder {

        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.blog_list_item, parent,false ))
    }

    override fun getItemCount(): Int {

        return blog_list.size
    }

    override fun onBindViewHolder(holder: BlogRecyclerAdapter.ViewHolder, position: Int) {

        var desc_data = blog_list.get(position).desc
        holder.setDescText(desc_data!!)

        var image_url = blog_list.get(position).image_url
        holder.setImage(image_url!!)

        var user_name:String? = null
        var user_image:String? = null

        var user_id = blog_list.get(position).user_id
        fbFireStore = FirebaseFirestore.getInstance()

        fbFireStore!!.collection("Users").document(user_id).get().addOnCompleteListener() { task ->

            if (task.isSuccessful) {
                user_name = task.result.getString("name")
                user_image = task.result.getString("image")

                holder.setUserId(user_name!!)
                holder.setUserPic(user_image!!)

            }
        }

        var time:String? = blog_list.get(position).timeStamp
        holder.setDate(time!!)

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var descView:TextView? = null
        var imageView:ImageView? = null
        var userIdView:TextView?= null
        var userPic:ImageView? = null
        var timeView:TextView? = null


        fun setDescText(text:String){

            descView =  itemView.findViewById(R.id.blog_desc)
            descView!!.setText(text)

        }

        fun setImage(downloadUri:String){

            imageView = itemView.findViewById(R.id.blog_image)
            Glide.with(itemView.context).load(downloadUri).into(imageView!!)

        }

        fun setUserId(name:String){
            userIdView = itemView.findViewById(R.id.blog_user_name)
            userIdView!!.setText(name)

        }

        fun setUserPic(imgdUri:String){

            val placeholderOption = RequestOptions()
            placeholderOption.placeholder(R.drawable.profile_placeholder)

            userPic = itemView.findViewById(R.id.blog_user_image)
            Glide.with(itemView.context).applyDefaultRequestOptions(placeholderOption).load(imgdUri).into(userPic!!)
        }

        fun setDate(time:String){

            timeView = itemView.findViewById(R.id.blog_date)
            timeView!!.setText(time)

        }


    }

}