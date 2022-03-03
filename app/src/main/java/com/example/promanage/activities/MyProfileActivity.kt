package com.example.promanage.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.promanage.R
import com.example.promanage.databinding.ActivityMyprofileBinding

import com.example.promanage.firebase.FirestoreClass
import com.example.promanage.models.User
import com.example.promanage.utils.Constants
import com.example.promanage.utils.Constants.READ_PERMISSION
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException
import java.util.jar.Manifest

class MyProfileActivity : BaseActivity() {
    private var binding:ActivityMyprofileBinding?=null
    private var mSelectedImageURI: Uri?=null
    private var downloadableUri:String=""
    private lateinit var mUserDetails:User
    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityMyprofileBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)
        setupActionBar()
        FirestoreClass().loadUser(this)

        binding?.ivProfileUserImage?.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)
                ==PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }else{
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_PERMISSION) }
        }

        binding?.btnUpdate?.setOnClickListener {
            if(mSelectedImageURI!=null){
                uploadUserImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                updateUserProfileData()
            }
        }


    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarMyProfileActivity)
        val actionbar= supportActionBar
        if(actionbar!=null){
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionbar.title=resources.getString(R.string.my_profile)
        }
        binding?.toolbarMyProfileActivity?.setNavigationOnClickListener { onBackPressed() }
    }

    fun setUserDataInUI(user:User){
        mUserDetails=user

        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding!!.ivProfileUserImage)
        binding?.etName?.setText(user.name)
        binding?.etEmail?.setText(user.email)
        if(user.mobile!=0L){ binding?.etMobile?.setText(user.mobile.toString()) }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== READ_PERMISSION){
            if(grantResults.isNotEmpty()&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            } }else{
            Toast.makeText(this,"Permission Denied",Toast.LENGTH_LONG).show()
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK
            &&requestCode== Constants.PICK_IMAGE_CODE
            &&data!!.data!=null){
            mSelectedImageURI=data.data

            try{
                Glide
                    .with(this)
                    .load(mSelectedImageURI)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(binding!!.ivProfileUserImage)}catch (e:IOException){
                e.printStackTrace()
            }
        }
    }



    private fun uploadUserImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        if(mSelectedImageURI!=null){
            val  sRef:StorageReference=FirebaseStorage
                .getInstance().reference
                .child("USER_IMAGE"+System.currentTimeMillis() + "."
                        + Constants.getFileExtension(this,mSelectedImageURI))

            sRef.putFile(mSelectedImageURI!!).addOnSuccessListener{
                taskSnapshot->
                Log.i("Firebase Image URL",taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri->
                    Log.i("Downloadable Image URL",uri.toString())
                    downloadableUri=uri.toString()

                    updateUserProfileData()
                }
            }.addOnFailureListener{
                exception->Toast.makeText(this,exception.message,Toast.LENGTH_LONG).show()
                hideProgressDialog()
            }
        }
    }

    fun profileUpdateSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun updateUserProfileData(){
        val userHashMap=HashMap<String,Any>()
        var change=false

        if(downloadableUri.isNotEmpty()&& downloadableUri!=mUserDetails.image){
            userHashMap[Constants.IMAGE]=downloadableUri
            change=true
        }
        if(binding?.etName?.text.toString()!=mUserDetails.name){
            userHashMap[Constants.NAME]=binding?.etName?.text.toString()
            change=true
        }
        if(binding?.etMobile?.text.toString()!=mUserDetails.mobile.toString()){
            userHashMap[Constants.MOBILE]=binding?.etMobile?.text.toString().toLong()
        change=true
        }
        if(change){
        FirestoreClass().updateUserProfileData(this,userHashMap)}
    }



}