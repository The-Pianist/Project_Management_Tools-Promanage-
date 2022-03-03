package com.example.promanage.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.example.promanage.R
import com.example.promanage.databinding.ActivitySignInBinding
import com.example.promanage.models.User
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : BaseActivity() {
    private var binding:ActivitySignInBinding?=null

    private lateinit var auth :FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivitySignInBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        auth= FirebaseAuth.getInstance()

        binding?.btnSignIn?.setOnClickListener { signInRegisterUser() }
        setupActionBar()
    }

    private fun setupActionBar() {
        setSupportActionBar(binding?.toolbarSignInActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_24dp)
        }

        binding?.toolbarSignInActivity?.setNavigationOnClickListener { onBackPressed() }
    }

    private fun signInRegisterUser(){
        val email:String=binding?.etEmail?.text.toString()
        val password:String=binding?.etPassword?.text.toString()

        if(validateForm(email,password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        Toast.makeText(this@SignInActivity,
                            "You have successfully signed in.", Toast.LENGTH_LONG).show()

                        Log.d("Sign in", "signInWithEmail:success")
                        val user = auth.currentUser
                        val intent=Intent(this,MainActivity::class.java)
                        startActivity(intent)

                    } else {
                        Toast.makeText(this@SignInActivity,
                            task.exception!!.message, Toast.LENGTH_LONG).show()
                        Log.w("Sign in","SignInWithEmail:Failure",task.exception)
                    }
                }
        }
    }

    private fun validateForm( email: String, password: String): Boolean {
        return when {
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

    fun signInSuccess(user: User){
        hideProgressDialog()
        val intent=Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
