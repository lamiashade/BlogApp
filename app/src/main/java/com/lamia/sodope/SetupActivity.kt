package com.lamia.sodope

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.UrlUriLoader
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_setup.*
import java.net.URI
import java.net.URL


class SetupActivity : AppCompatActivity() {

    var mainImageUri: Uri? = null
    var setupToolBar: android.support.v7.widget.Toolbar? = null
    var circleImage: CircleImageView? = null
    var setupName: TextView? = null
    var setupBtn: Button? = null
    var userId :String? = null
    var isChanged:Boolean = false
    var setupProgressBar: ProgressBar? = null

    var mStorageRef: StorageReference? = null
    var mAuth = FirebaseAuth.getInstance()
    var fDb:FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        setupToolBar = setup_Tool_Bar
        setSupportActionBar(setupToolBar)
        supportActionBar!!.setTitle("Account Setup")

        circleImage = setup_image
        setupName = setup_Name
        setupBtn = setup_btn
        userId = mAuth.currentUser!!.uid

        setupProgressBar = setup_progress_bar
        mStorageRef = FirebaseStorage.getInstance().reference
        fDb = FirebaseFirestore.getInstance()

        setup_progress_bar.visibility = View.VISIBLE
        setupBtn!!.isEnabled = false

        fDb!!.collection("Users").document(userId!!).get().addOnCompleteListener(){task ->

            if(task.isSuccessful){
                if (task.getResult().exists()){

                    Toast.makeText(this, "User data Exists.", Toast.LENGTH_LONG).show()

                    var name = task.getResult().getString("name")
                    var image:String = task.getResult().getString("image")!!

//                            "https://firebasestorage.googleapis.com/v0/b/my-project-1524378594030.appspot.com/o/profile_images%2FxUe0cS8dFRQlqTKM7KMKRPUpn773?alt=media&token=2a3007f3-2041-4daf-8b76-9a254bccaf42"

                    mainImageUri = Uri.parse(image)

                    setupName!!.setText(name)

                    var placeHolderRequest = RequestOptions()
                    placeHolderRequest.placeholder(R.drawable.default_profile_pic)

                    Glide.with(applicationContext).load(image).into(circleImage!!)

                }

            }else  {
                var error = task.exception!!.message
                Toast.makeText(this, "Error: " + error, Toast.LENGTH_LONG).show()

            }
            setup_progress_bar.visibility = View.INVISIBLE
            setupBtn!!.isEnabled = true
        }

        setupBtn!!.setOnClickListener() {

            var userName = setupName!!.text.toString()

            if (userName.isNotEmpty() && mainImageUri != null) {

                setupProgressBar!!.visibility = View.VISIBLE

                if (isChanged) {

                    userId = mAuth.currentUser!!.uid


                    var imagePath = mStorageRef!!.child("profile_images").child(userId!!)

                    imagePath.putFile(mainImageUri!!).addOnCompleteListener() {task->

                        if (task.isSuccessful) {

                            storeFirestore(task, userName)

                        } else {
                            var error = task.exception!!.message
                            Toast.makeText(this, "Error: " + error, Toast.LENGTH_LONG).show()

                            setupProgressBar!!.visibility = View.INVISIBLE
                        }

                    }
                }

            }else{

                storeFirestore(null, userName)

            }
        }


        circleImage!!.setOnClickListener() {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

              setupPermissions()

            }
        }
    }

    fun storeFirestore(task: Task<UploadTask.TaskSnapshot>?, userName:String){


        var download_uri:Uri?= null

        if (task != null) {

            var filepath = mStorageRef!!.child("profile_images").child(userId + ".jpg")

            filepath.downloadUrl.addOnCompleteListener() { task ->

                if (task.isSuccessful) {

                    download_uri = task.result

                } else {

                    Toast.makeText(this, "Could not get file URl", Toast.LENGTH_LONG).show()
                }
            }

        }else{

            download_uri= mainImageUri
        }

        var userMap:MutableMap<String,String> = hashMapOf()


        userMap.put("name", userName)
        userMap.put("image", mainImageUri.toString())


        fDb!!.collection("Users").document(userId!!).set(userMap as Map<String, Any>).addOnCompleteListener(){task->

            if(task.isSuccessful){
                Toast.makeText(this, "User settings are updated.", Toast.LENGTH_LONG).show()
                var mainIntent = Intent(this,MainActivity::class.java)
                startActivity(mainIntent)
                finish()

            }else{

                var error = task.exception!!.message

                Toast.makeText(this, "FireStore Erro: " + error, Toast.LENGTH_LONG).show()

            }
            setupProgressBar!!.visibility = View.INVISIBLE
        }


        Toast.makeText(this, "uploaded Image", Toast.LENGTH_LONG).show()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            var result = CropImage.getActivityResult(data)

            if (resultCode == RESULT_OK) {

                mainImageUri = result.getUri()

                circleImage!!.setImageURI(mainImageUri)

                isChanged = true

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                var error = result.getError()
                Toast.makeText(this,"Error: " + error,Toast.LENGTH_LONG).show()
            }
        }

    }

    fun setupPermissions(){

        var TAG = "PERMISSIONS"

        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission is Denied", Toast.LENGTH_LONG).show()
            Log.i(TAG, "Permission to select photo is denied")

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }else {

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this)

        }

    }
}
