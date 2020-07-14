package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.intro)

        val logo = findViewById<ImageView>(R.id.mm_logo)
        val name = findViewById<ImageView>(R.id.mm_name)

        val translate: Animation = AnimationUtils.loadAnimation(this, R.anim.logo)
        val alpha: Animation = AnimationUtils.loadAnimation(this, R.anim.name)

        logo.startAnimation(translate)
        name.startAnimation(alpha)

        Handler().postDelayed({ //delay를 위한 handler
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 3500)
    }

}