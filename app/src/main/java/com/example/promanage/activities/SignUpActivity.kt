package com.example.promanage.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.promanage.R
import com.example.promanage.databinding.ActivitySignUpBinding
import com.example.promanage.firebase.FirestoreClass
import com.example.promanage.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpActivity : BaseActivity() {
    private var binding:ActivitySignUpBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivitySignUpBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)
        setUpActionBar()

        binding?.btnSignUp?.setOnClickListener {
            registerUser()
        }
    }

    private fun setUpActionBar(){
        setSupportActionBar(binding?.toolbarSignUpActivity)
        val actionbar=supportActionBar
        if(actionbar!=null){
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_back_24dp)
        }
        binding?.toolbarSignUpActivity?.setNavigationOnClickListener { onBackPressed()}
    }

    private fun validateForm(name: String, email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showError("Please enter name.")
                false
            }
            TextUtils.isEmpty(email) -> {
                showError("Please enter email.")
                false
            }
            TextUtils.isEmpty(password) -> {
                showError("Please enter password.")
                false
            }
            else -> {
                true
            }
        }
    }

    private fun registerUser(){
        val name:String=binding?.etName?.text.toString().trim{it<=' '}
        val email:String=binding?.etEmail?.text.toString().trim{it<=' '}
        val password:String=binding?.etPassword?.text.toString().trim{it<=' '}

        if(validateForm(name, email, password)){
          showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                    task->
                    if(task.isSuccessful){
                        val firebaseuser: FirebaseUser = task.result!!.user!!
                        val registerEmail=firebaseuser.email!!
                       val user= User(firebaseuser.uid,name,registerEmail)
                        FirestoreClass().registerUser(this@SignUpActivity,user)
                    }else{
                        Toast.makeText(this,task.exception!!.message,Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    fun userRegisteredSuccess(){
        Toast.makeText(this,"You have complete the registration",Toast.LENGTH_LONG).show()
        hideProgressDialog()
        FirebaseAuth.getInstance().signOut()
        finish()
    }

}