package com.lamia.sodope

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    val mAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        var emailFeild = regs_email
        var passwordField = regs_password
        var confirmPasswordField = regs_confirm_password
        var createeAccountBtn = regs_create_acc_btn
        var alreadyHaveAccBtn = regs_already_have_acc_btn
        var progressbar = regs_progess_bar

        alreadyHaveAccBtn.setOnClickListener(){

            var backmain = Intent(this,MainActivity::class.java)
            startActivity(backmain)
        }

        createeAccountBtn.setOnClickListener(){

            var email = emailFeild.text.toString()
            var pass = passwordField.text.toString()
            var con_pass = confirmPasswordField.text.toString()

            if(email.isNotEmpty() && pass.isNotEmpty() && con_pass.isNotEmpty()){
                if (pass.equals(con_pass)){

                    progressbar.visibility = View.VISIBLE

                    mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(){task->

                        if(task.isSuccessful){
                            val setupIntent = Intent(this,SetupActivity::class.java)
                            startActivity(setupIntent)
                            finish()

                        }else{
                            var errorMessage = task.exception!!.message
                            Toast.makeText(this, "Error: " + errorMessage,Toast.LENGTH_LONG).show()

                        }

                        progressbar.visibility = View.INVISIBLE
                    }

                }else{
                    Toast.makeText(this, "Password and cconfirm password do not match",Toast.LENGTH_LONG).show()
                }
            }
        }
    }



    override fun onStart() {
        super.onStart()

        var curentUser = mAuth.currentUser

        if(curentUser != null){
            sendToMain()
        }
    }


    fun sendToMain(){
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
        finish()

    }
}
