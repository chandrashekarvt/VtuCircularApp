package com.example.vtuapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Observer

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.android.synthetic.main.internet_alert.view.*
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_main)


        val ab = supportActionBar
        ab!!.hide()

        val networkConnection = NetworkConnection(this)


        val networkDialog = LayoutInflater.from(this).inflate(R.layout.internet_alert, null);

        val builder = AlertDialog.Builder(this).setView(networkDialog)

        val alertDialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)




        networkConnection.observe(this, Observer { isConnected ->
            if(isConnected){


                if(alertDialog.isShowing) {
                    networkDialog.layoutDisconnected.visibility = View.GONE
                    networkDialog.layoutConnected.visibility = View.VISIBLE
                    Timer("SettingUp", false).schedule(500) {
                        alertDialog.dismiss()
                    }
                }
            }else
            {
                networkDialog.layoutConnected.visibility = View.GONE
                networkDialog.layoutDisconnected.visibility = View.VISIBLE
                alertDialog.show()

            }
        })

    }

    fun allCircular(view: View) {
        val intent = Intent(this, AllCirculars::class.java);
        startActivity(intent)
    }

    fun examCircular(view: View) {
        val intent = Intent(this, ExamCirculars::class.java);
        startActivity(intent)
    }
}