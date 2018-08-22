package com.lamia.sodope

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    val myAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var loginEmailTxt = login_email
        var loginPasswordTxt = login_password
        var loginButton = login_btn
        var loginRegButton = login_regs
        val loginProgressBar = progressBar


        loginRegButton.setOnClickListener(){

            var createAcc_intent = Intent(this,RegisterActivity::class.java)
            startActivity(createAcc_intent)
        }

      loginButton.setOnClickListener(){

          var email = loginEmailTxt.text.toString()
          var password = loginPasswordTxt.text.toString()

          if(email.isNotEmpty() && password.isNotEmpty()){

              loginProgressBar.visibility = View.VISIBLE

            myAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(){task ->

                if(task.isSuccessful){
                    sendtoMain()

                }else{
                    val errorMessage = task.exception!!.message
                    Toast.makeText(this,"Error " + errorMessage,Toast.LENGTH_LONG ).show()

                }
                loginProgressBar.visibility = View.INVISIBLE
            }

          }
      }

    }

    override fun onStart() {
        super.onStart()

        var currentUser = myAuth.currentUser

        if(currentUser != null){

            sendtoMain()
        }
    }

    fun sendtoMain(){
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
        finish()

    }
}
