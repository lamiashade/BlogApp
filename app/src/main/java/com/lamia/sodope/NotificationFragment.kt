package com.lamia.sodope


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.lamia.sodope.R.id.notification_list
import kotlinx.android.synthetic.main.fragment_notification.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class NotificationFragment : Fragment() {

    var FbasefireStore: FirebaseFirestore? = null
    var mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    var currentUser: FirebaseUser? = null

    var notifListView: RecyclerView? = null
    var blog_list: List<BlogPost>? = null
    var NotificationRecylerAdapter:NotificationRecylerAdapter? = null

    var lastVisible: DocumentSnapshot? = null
    var isFirstPageFirstLoad:Boolean? = true


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view:View? = inflater.inflate(R.layout.fragment_notification, container, false)

        FbasefireStore = FirebaseFirestore.getInstance()

        blog_list = ArrayList()

        notifListView = view!!.findViewById(R.id.notification_list)
        NotificationRecylerAdapter = NotificationRecylerAdapter(blog_list as ArrayList<BlogPost>)
        notifListView!!.layoutManager = LinearLayoutManager(container!!.context)
        notifListView!!.adapter = NotificationRecylerAdapter
        notifListView!!.setHasFixedSize(true)

        currentUser = mAuth!!.currentUser

        if(currentUser != null) {


            var notificationQuery:Query = FbasefireStore!!.collection("Posts").orderBy("timeStamp", Query.Direction.DESCENDING)

            notificationQuery.addSnapshotListener(activity!!, EventListener<QuerySnapshot>() { documentSnapshot, exception ->

                if (!documentSnapshot!!.isEmpty()) {

                    if (isFirstPageFirstLoad!!) {

                        lastVisible = documentSnapshot.documents.get(documentSnapshot.size() - 1);
                        (blog_list as ArrayList<BlogPost>).clear()

                    }

                    for (doc: DocumentChange in documentSnapshot.documentChanges) {

                        if (doc.type == DocumentChange.Type.ADDED) {

                            var blogPost = doc.document.toObject(BlogPost::class.java)

                                if (isFirstPageFirstLoad!!) {

                                    (blog_list as ArrayList<BlogPost>).add(blogPost)

                                } else {

                                    (blog_list as ArrayList<BlogPost>).add(0, blogPost)

                                }

                                NotificationRecylerAdapter!!.notifyDataSetChanged()

                        }
                    }

                    isFirstPageFirstLoad = false
                }
            })
        }


        // Inflate the layout for this fragment
        return view

    }

}
