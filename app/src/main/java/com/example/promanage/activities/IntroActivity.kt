package com.example.promanage.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.promanage.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {
    private var binding:ActivityIntroBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityIntroBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        binding?.btnSignUpIntro?.setOnClickListener {
           val intent=Intent(this@IntroActivity,SignUpActivity::class.java)
            startActivity(intent)
        }

        binding?.btnSignInIntro?.setOnClickListener {
            startActivity(Intent(this@IntroActivity, SignInActivity::class.java))
        }

    }
}