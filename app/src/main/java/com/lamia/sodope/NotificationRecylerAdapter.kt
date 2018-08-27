package com.lamia.sodope

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NotificationRecylerAdapter(var blog_list: List<BlogPost>):RecyclerView.Adapter<NotificationRecylerAdapter.ViewHolder>(){

    var fbFireStore: FirebaseFirestore? = null
    var mAuth: FirebaseAuth? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationRecylerAdapter.ViewHolder {

        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.notification_list_item, parent, false))
    }

    override fun getItemCount(): Int {

        return blog_list.size
    }

    override fun onBindViewHolder(holder: NotificationRecylerAdapter.ViewHolder, position: Int) {

        var thumb_url = blog_list.get(position).thumb_nail
        holder.setThumbNail(thumb_url!!)

        var date_data = blog_list.get(position).timeStamp
        holder.setDate(date_data)

        var desc_data = blog_list.get(position).desc
        holder.setDesc(desc_data!!)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var image_thumbNail_View: ImageView? = null
        var notification_date_View: TextView?= null
        var notification_desc_View: TextView? = null


        fun setThumbNail(thumb_nail:String){

            val placeholderOption = RequestOptions()
            placeholderOption.placeholder(R.color.LightGray)

            image_thumbNail_View = itemView.findViewById(R.id.notification_thumb_nail_img)
            Glide.with(itemView.context).applyDefaultRequestOptions(placeholderOption).load(thumb_nail).into(image_thumbNail_View!!)
        }

        fun setDate(date:String){
            notification_date_View = itemView.findViewById(R.id.notification_date_text)
            notification_date_View!!.text = date
        }

        fun setDesc(desc:String){
            notification_desc_View = itemView.findViewById(R.id.notification_description_text)
            notification_desc_View!!.text = desc
        }
    }

}