package com.example.igreeldownloader.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.igreeldownloader.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplashBinding


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
       binding.apply {


           val background: Thread = object : Thread() {
               override fun run() {
                   try {
                       // Thread will sleep for 5 seconds
                       sleep((5 * 1000).toLong())

                       // After 5 seconds redirect to another intent
                       val i = Intent(baseContext, MainActivity::class.java)
                       startActivity(i)

                       //Remove activity
                       finish()
                   } catch (e: Exception) {
                       Log.d("jejeex",e.toString())
                   }
               }
           }

           // start thread

           // start thread
           background.start()
       }
    }
}