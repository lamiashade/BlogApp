package com.lamia.sodope


import android.nfc.Tag
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.fragment_home.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class HomeFragment : Fragment() {

    var blogListView:RecyclerView?= null
    var blog_list: List<BlogPost>? = null

    var FbasefireStore: FirebaseFirestore? = null
    var mAuth:FirebaseAuth? = FirebaseAuth.getInstance()

    var blogRecyclerAdapter:BlogRecyclerAdapter? = null

    var lastVisible:DocumentSnapshot? = null
    var isFirstPageFirstLoad:Boolean? = true

    var notifListView: RecyclerView?= null
    var NotificationRecylerAdapter:NotificationRecylerAdapter? = null

    var currentUser: FirebaseUser? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_home, container, false)

        blog_list = ArrayList()

        //Blog Post List
        blogListView = view.findViewById(R.id.blog_list_view)
        blogRecyclerAdapter = BlogRecyclerAdapter(blog_list as ArrayList<BlogPost>)
        blogListView!!.layoutManager = LinearLayoutManager(container!!.context)
        blogListView!!.adapter = blogRecyclerAdapter
        blogListView!!.setHasFixedSize(true)


        currentUser = mAuth!!.currentUser

        if (currentUser != null) {

            FbasefireStore = FirebaseFirestore.getInstance()

            blogListView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {

                    var reachedBottom = !recyclerView!!.canScrollVertically(1)

                    if(reachedBottom){

                        Toast.makeText(context, "Reached bottom ", Toast.LENGTH_LONG).show()

//                        loadMorePost()

                    }

                    }
                })


            var firstQuery = FbasefireStore!!.collection("Posts")
                    .orderBy("timeStamp", Query.Direction.DESCENDING)
                    .limit(2)

            firstQuery.addSnapshotListener(activity!!, EventListener<QuerySnapshot>() { documentSnapshot, exception ->

                Log.d("Home Fragment: ", exception.toString())

                if (!documentSnapshot!!.isEmpty()) {

                    if (isFirstPageFirstLoad!!) {

                        lastVisible = documentSnapshot.documents.get(documentSnapshot.size() - 1);
                        (blog_list as ArrayList<BlogPost>).clear()

                    }

                    for (doc: DocumentChange in documentSnapshot.documentChanges) {

                        if (doc.type == DocumentChange.Type.ADDED) {

                            var blogPost = doc.document.toObject(BlogPost::class.java)

                            if(isFirstPageFirstLoad!!) {

                                (blog_list as ArrayList<BlogPost>).add(blogPost)

                            }else{

                            (blog_list as ArrayList<BlogPost>).add(0,blogPost)

                            }

                            blogRecyclerAdapter!!.notifyDataSetChanged()

                        }
                    }

                    isFirstPageFirstLoad = false
                }
            })


        }

        // Inflate the layout for this fragment
        return view
    }

    fun loadMorePost() {

        if (currentUser != null) {

            var nextQuery = FbasefireStore!!.collection("Posts")
                    .orderBy("timeStamp", Query.Direction.DESCENDING)
                    .startAfter(lastVisible)
                    .limit(1)

            try {

                nextQuery.addSnapshotListener { docSnapshot, excpt ->

                    Log.d("Home Fragment: ", excpt.toString())

                    if (!docSnapshot!!.isEmpty) {

                        lastVisible = docSnapshot.documents.get(docSnapshot.size() - 1)
                        for (doc: DocumentChange in docSnapshot.documentChanges) {

                            if (doc.type == DocumentChange.Type.ADDED) {

                                var blogPost = doc.document.toObject(BlogPost::class.java)
                                (blog_list as ArrayList<BlogPost>).add(blogPost)

                                blogRecyclerAdapter!!.notifyDataSetChanged()

                            }
                        }
                    }

                }
            } catch (e:Exception){


            }
        }
    }
}
