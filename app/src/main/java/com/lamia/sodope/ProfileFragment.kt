package com.lamia.sodope


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
    var prof_top_card:CardView? = profile_user_image_container
    var prof_img:CircleImageView? = profile_user_picture
    var prof_img_bg:ImageView? = profile_user_background
    var prof_user_name:TextView? = profile_user_name

    //User profile Bottom Card
    var profile_bottom_card:CardView? = profile_user_info
    var prof_Post_count:TextView? = profile_post_count
    var prof_freindds_count:TextView? = profile_friends_count
    var prof_reutation_count:TextView? = profile_reputation_count


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }


}
