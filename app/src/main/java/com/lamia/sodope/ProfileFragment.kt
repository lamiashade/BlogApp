package com.lamia.sodope


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_profile.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class ProfileFragment : Fragment() {

    var FbasefireStore: FirebaseFirestore? = null
    var mAuth: FirebaseAuth? = FirebaseAuth.getInstance()

    //User profile Top Card
    var prof_top_card:CardView? = null
    var prof_img:CircleImageView? = null
    var prof_img_bg:ImageView? = null
    var prof_user_name:TextView? = null

    //User profile Bottom Card
    var profile_bottom_card:CardView? = null
    var prof_Post_count:TextView? = null
    var prof_freindds_count:TextView? = null
    var prof_reutation_count:TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        FbasefireStore = FirebaseFirestore.getInstance()

        var view = inflater.inflate(R.layout.fragment_profile, container, false)

        //User profile Top Card
        prof_top_card = view.findViewById(R.id.profile_user_image_container)
        prof_img = view.findViewById(R.id.profile_user_picture)
        prof_img_bg = view.findViewById(R.id.profile_user_background)
        prof_user_name = view.findViewById(R.id.profile_user_name)

        //User profile Bottom Card
        profile_bottom_card = view!!.findViewById(R.id.profile_user_info)
        prof_Post_count = view.findViewById(R.id.profile_post_count)
        prof_freindds_count = view.findViewById(R.id.profile_friends_count)
        prof_reutation_count = view.findViewById(R.id.profile_reputation_count)


        // retrirving User data
        var currentUser = mAuth!!.currentUser

        if(currentUser != null){

            var user_id = currentUser.uid.toString()

            FbasefireStore!!.collection("Users").document(user_id).get().addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    var user_name = task.result.getString("name")
                    var user_image = task.result.getString("image")

                    setName(user_name!!)
                    setImage(user_image!!)

                }
            }

            prof_Post_count!!.text = "15"
            prof_freindds_count!!.text = "929"
            prof_reutation_count!!.text = "2100"
        }

        // Inflate the layout for this fragment
        return view
    }

    fun setName(name:String){
        prof_user_name!!.text = name
    }

    fun setImage(imgUrl:String){
        Glide.with(context!!).load(imgUrl).into(prof_img!!)
    }

}
