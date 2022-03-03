package com.example.promanage.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.promanage.R
import com.example.promanage.databinding.ActivityCreateBoardBinding
import com.example.promanage.firebase.FirestoreClass
import com.example.promanage.models.Board
import com.example.promanage.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class CreateBoardActivity : BaseActivity() {
    private lateinit var mUserName:String
    private var mSelectedImageURI: Uri?=null
    private var mBoardImageURL:String=""
    private var binding:ActivityCreateBoardBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityCreateBoardBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)
        setupActionBar()

        if(intent.hasExtra(Constants.NAME)){
            mUserName= intent.getStringExtra(Constants.NAME).toString()
        }

        binding?.ivBoardImage?.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)
                ==PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }else{
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_PERMISSION
                ) }
        }

        binding?.btnCreate?.setOnClickListener {
            if(mSelectedImageURI!=null){ uploadImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                createBoard()
            }
        }

    }
    private fun setupActionBar() {
        setSupportActionBar(binding?.toolbarCreateBoardActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_24dp)
        }

        binding?.toolbarCreateBoardActivity?.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== Constants.READ_PERMISSION){
            if(grantResults.isNotEmpty()&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            } }else{
            Toast.makeText(this,"Permission Denied", Toast.LENGTH_LONG).show()
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
                    .placeholder(R.drawable.ic_board_place_holder)
                    .into(binding!!.ivBoardImage)}catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    fun boardCreatedSuccessfully(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun createBoard(){
        val assignedUserArrayList:ArrayList<String> = ArrayList()
        assignedUserArrayList.add(getCurrentUserID())

        var board=Board(
        binding?.etBoardName?.text.toString(),
        mBoardImageURL,
        mUserName,
        assignedUserArrayList
        )

        FirestoreClass().createBoard(this , board)
    }

    private fun uploadImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        val  sRef: StorageReference = FirebaseStorage
            .getInstance().reference
            .child("BOARD_IMAGE"+System.currentTimeMillis() + "."
                    + Constants.getFileExtension(this,mSelectedImageURI))

        sRef.putFile(mSelectedImageURI!!).addOnSuccessListener{
                taskSnapshot->
            Log.i("BOARD Image URL",taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri->
                Log.i("Downloadable Board Image URL",uri.toString())
                mBoardImageURL=uri.toString()

                createBoard()
            } }.addOnFailureListener{
                exception->Toast.makeText(this,exception.message,Toast.LENGTH_LONG).show()
            hideProgressDialog()
        }
    }

}