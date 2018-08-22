package com.lamia.sodope

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.FloatingActionButton
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {

    val mAuth = FirebaseAuth.getInstance()
    var addPostButton:FloatingActionButton? = null
    var firebaseFirestore:FirebaseFirestore?= null

    var mContainer:FrameLayout? = null
    var bottomNav:BottomNavigationView?=null

    var current_user_id:String? = null

    var homeFrag:android.support.v4.app.Fragment? = null
    var notificationFrag:android.support.v4.app.Fragment? = null
    var profileFrag:android.support.v4.app.Fragment? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainToolBar = main_toolbar
        setSupportActionBar(mainToolBar)
        supportActionBar!!.setTitle("Photo Blog")

        firebaseFirestore = FirebaseFirestore.getInstance()

        addPostButton = add_post_btn

        bottomNav = main_bottom_nav
        mContainer = main_container

        homeFrag = HomeFragment()
        notificationFrag = NotificationFragment()
        profileFrag = ProfileFragment()


        bottomNav!!.setOnNavigationItemSelectedListener {item ->

            when(item.itemId){

                R.id.bottom_menu_home ->{
                    setFragment(homeFrag!!)
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.bottom_menu_notification -> {
                    setFragment(notificationFrag!!)
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.bottom_menu_profile -> {
                    setFragment(profileFrag!!)
                    return@setOnNavigationItemSelectedListener true
                }

                else -> {
                    return@setOnNavigationItemSelectedListener false
                }

            }
        }


        addPostButton!!.setOnClickListener(){

            var newPsotInt:Intent = Intent(this,NewPostActivity::class.java)
            startActivity(newPsotInt)

        }

    }

    override fun onStart() {
        super.onStart()

        var currentUser = mAuth.currentUser

        if(currentUser == null){

           sendtoLogin()

        }else{

            setFragment(homeFrag!!)

            current_user_id = mAuth.currentUser!!.uid

            firebaseFirestore!!.collection("Users").document(current_user_id!!).get().addOnCompleteListener(){task->

                if(task.isSuccessful){

                    if(task.result.exists()){

//                        var setupInt = Intent(this,SetupActivity::class.java)
//                        startActivity(setupInt)
//                        finish()

                    }

                }
            }

        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.main_menu,menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item!!.itemId){
            R.id.action_logout ->{
                    logOut()
                    return true
            }
            R.id.action_setting_button ->{
                settingAccount()
                return true
            }
        }

        return false
    }

    fun logOut(){
        mAuth.signOut()
        sendtoLogin()
    }

    fun settingAccount(){
        val settingAccount_Intent = Intent(this,SetupActivity::class.java)
        startActivity(settingAccount_Intent)
        finish()
    }

    fun sendtoLogin(){
        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
        finish()

    }

    fun setFragment(fragment:android.support.v4.app.Fragment) {

       var  fragTransaction = supportFragmentManager.beginTransaction()

        fragTransaction.replace(R.id.main_container,fragment)
        fragTransaction.addToBackStack(null)
        fragTransaction.commit()

    }
}
