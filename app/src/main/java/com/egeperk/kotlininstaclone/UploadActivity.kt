package com.egeperk.kotlininstaclone

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Binder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.egeperk.kotlininstaclone.databinding.ActivityUploadBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.lang.Exception
import java.util.*
import java.util.jar.Manifest

class UploadActivity : AppCompatActivity() {

    private lateinit var binding : ActivityUploadBinding

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    var selectedImage : Uri? = null
    private lateinit var  auth : FirebaseAuth
    private lateinit var storage : FirebaseStorage
    private lateinit var firestore : FirebaseFirestore
    //var selectedPicture : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        registerLaunch()

        auth = Firebase.auth
        storage = Firebase.storage
        firestore = Firebase.firestore

    }

    fun upload(view : View) {
        val uuid = UUID.randomUUID()
        val imageName ="$uuid.jpeg"

        val reference = storage.reference
        val imageReference = reference.child("images").child(imageName)

        if (selectedImage != null){
            imageReference.putFile(selectedImage!!).addOnSuccessListener {

                val uploadReference=storage.reference.child("images").child(imageName)
                uploadReference.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()


                    val postMap = hashMapOf<String,Any>()
                    postMap.put("downloadUrl",downloadUrl)
                    postMap.put("userEmail",auth.currentUser!!.email!!)
                    postMap.put("comment", binding.commentText.text.toString())
                    postMap.put("date", Timestamp.now())


                    firestore.collection("Posts").add(postMap).addOnSuccessListener {
                        val intent = Intent(this@UploadActivity, FeedActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                    }.addOnFailureListener{
                        Toast.makeText(this@UploadActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
                    }


                }

            }.addOnFailureListener {
            Toast.makeText(this, it.localizedMessage,Toast.LENGTH_SHORT).show()
            }

        }

    }

    fun goToGallery(view : View) {

        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view,"Permission needed",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", View.OnClickListener {

                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }).show()
            } else {
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)

            }


        } else {
            val intentToGallery : Intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        }


    }


    fun registerLaunch(){

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result -> if (result.resultCode == RESULT_OK) {
                val intentFromResult = result.data
            if (intentFromResult != null) {
                        selectedImage = intentFromResult.data
                        binding.imageView.setImageURI(selectedImage)

                }
            }
        }


        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            result -> if (result) {
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        } else {
            Toast.makeText(this@UploadActivity, "Permission needed",Toast.LENGTH_SHORT).show()
        }
        }
    }
    /** private fun makeSmallImage(selectedImage : Bitmap, maximumSize : Int) : Bitmap {
        var width : Int = selectedImage.width
        var height : Int = selectedImage.height

        val bitmapRatio : Double = width.toDouble()/height.toDouble()
        if (bitmapRatio >1) {
            width = maximumSize
            val scaledHeight = width/bitmapRatio
            height = scaledHeight.toInt()
        } else {
            height = maximumSize
            val scaledWidth = height * bitmapRatio
            width = scaledWidth.toInt()
        }

        return Bitmap.createScaledBitmap(selectedImage, width,height,true)



    }
    */
}