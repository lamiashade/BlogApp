package com.lamia.sodope

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_new_post.*
import java.util.*
import com.google.firebase.firestore.FirebaseFirestoreSettings
import java.io.File
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NewPostActivity : AppCompatActivity() {

    var newpostToolbar: android.support.v7.widget.Toolbar? = null
    var newPostImage: ImageView? = null
    var newPostBtn: Button? = null
    var newPostTxt: EditText? = null
    var postimageUri: Uri?= null
    var newPstprogBr:ProgressBar? = null

    var FbaseFirestore:FirebaseFirestore? =null
    var mStoreageRef:StorageReference? = null
    var mAuth:FirebaseAuth? = null

    var user_id:String? = null

    var compressedImageFile:Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        newpostToolbar = new_post_toolbar

        setSupportActionBar(newpostToolbar)
        supportActionBar!!.setTitle("Add New Post")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        newPostImage = new_post_Image
        newPostTxt = new_post_txt
        newPostBtn = newPost_btn

        newPstprogBr = new_post_progBr

        FbaseFirestore = FirebaseFirestore.getInstance()
        mStoreageRef = FirebaseStorage.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

        user_id = mAuth!!.currentUser!!.uid


        newPostImage!!.setOnClickListener() {

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setMinCropResultSize(900,600)
                    .setAspectRatio(16, 9)
                    .start(this)

        }

        newPostBtn!!.setOnClickListener(){

            var postDesc:String? = newPostTxt!!.text.toString()

            if(postDesc!!.isNotEmpty() && postimageUri != null){

                newPstprogBr!!.visibility = View.VISIBLE

                newPostBtn!!.isEnabled = false

                var randomImgName = FieldValue.serverTimestamp().toString()

                var filepath = mStoreageRef!!.child("post_images").child(randomImgName + ".jpeg")

                filepath.putFile(postimageUri!!).addOnCompleteListener(){task->

                 if(task.isSuccessful){

                     var newImageFile:File= File(postimageUri!!.path)

                     try {
                         compressedImageFile = Uri.fromFile(id.zelory.compressor.Compressor(this)
                                 .setMaxWidth(100)
                                 .setMaxHeight(100)
                                 .setQuality(15)
                                 .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                 .compressToFile(newImageFile))
                     }catch(e: IOException){

                         e.printStackTrace()
                     }

                     var thumbNailPath = mStoreageRef!!.child("post_images/thumb_nails").child(randomImgName + ".jpeg")

                     var thumbNailUri:Uri?= null

                     thumbNailPath.downloadUrl.addOnCompleteListener(){thmbtask->

                         if(thmbtask.isSuccessful){
                             thumbNailUri = thmbtask.result
                         }

                         thumbNailPath.putFile(compressedImageFile!!)
                     }

                     var downloadUri:Uri? = null

                     filepath.downloadUrl.addOnCompleteListener() { task ->

                         if (task.isSuccessful) {

                             downloadUri = task.result

                         } else {

                             var error = task.exception.toString()

                             Toast.makeText(this, " Error: " + error, Toast.LENGTH_LONG).show()
                         }

                         var postMap:MutableMap<String,Any> = hashMapOf()

                         var dateString:String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm"))

                         postMap.put("image_url", downloadUri.toString())
                         postMap.put("desc", postDesc)
                         postMap.put("user_id", user_id!!)
                         postMap.put("thumb_nail", thumbNailUri.toString())
                         postMap.put("timeStamp", dateString)


                         FbaseFirestore!!.collection("Posts").add(postMap as Map<String, Any>).addOnCompleteListener(){task->

                             if(task.isSuccessful){

                                 Toast.makeText(this,"Post was created",Toast.LENGTH_LONG).show()


                                 var backToMain:Intent = Intent(this,MainActivity::class.java)
                                 startActivity(backToMain)
                                 finish()

                                 newPostBtn!!.isEnabled = true

                             }else{

                                 var error = task.exception.toString()

                                 Toast.makeText(this, " Error: " + error, Toast.LENGTH_LONG).show()
                             }

                             newPstprogBr!!.visibility = View.INVISIBLE
                         }
                     }

                 }else{

                     var error = task.exception.toString()

                     Toast.makeText(this, " Error: " + error, Toast.LENGTH_LONG).show()

                     newPstprogBr!!.visibility = View.INVISIBLE

                 }

                }

            }else{


                Toast.makeText(this, " No Image selected and or Description entered. " , Toast.LENGTH_LONG).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            var result = CropImage.getActivityResult(data)

            if (resultCode == RESULT_OK) {

                postimageUri = result.getUri()

                newPostImage!!.setImageURI(postimageUri)

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                var error = result.getError()
                Toast.makeText(this, "Error: " + error, Toast.LENGTH_LONG).show()
            }
        }
    }
}
