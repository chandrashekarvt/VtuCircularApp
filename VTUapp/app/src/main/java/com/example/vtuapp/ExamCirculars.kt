package com.example.vtuapp

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_exam_circulars.*
import kotlinx.android.synthetic.main.internet_alert.view.*
import org.json.JSONArray
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class ExamCirculars : AppCompatActivity(), articleClicked {


    private lateinit var customAdapter: CustomAdapter


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_exam_circulars)

        val actionBar = supportActionBar
        actionBar!!.title = "Latest Exam Circulars"


        actionBar.setDisplayHomeAsUpEnabled(true)


        examCircRecyclerView.layoutManager = LinearLayoutManager(this)
        customAdapter = CustomAdapter(this)

        examCircRecyclerView.adapter = customAdapter

        val networkConnection = NetworkConnection(this)


        val networkDialog = LayoutInflater.from(this).inflate(R.layout.internet_alert, null);

        val builder = AlertDialog.Builder(this).setView(networkDialog)

        val alertDialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)






        fetchData("https://vtu-data.herokuapp.com/2")


        networkConnection.observe(this, Observer { isConnected ->
            if(isConnected){
                if(alertDialog.isShowing) {
                    fetchData("https://vtu-data.herokuapp.com/2")
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



    private fun fetchData(url: String, t:Int = 1){
        if(t>3){
            return;
        }

        ECPBar.visibility = View.VISIBLE


        val allCirReq = JsonArrayRequest(
            Request.Method.GET, url,null,
            Response.Listener<JSONArray> { response ->

                val articles: ArrayList<Article> = ArrayList();
                for(i in 0 until response.length()){
                    val obj = response.getJSONObject(i);
                    val article = Article(
                        i+1,
                            obj.getString("posted_on"),
                            obj.getString("Content"),
                            obj.getString("url")
                    )
                    articles.add(article);
                }

                customAdapter.updateArticles(articles)

                ECPBar.visibility = View.GONE

            },
            Response.ErrorListener {
                Log.d("ERROR API", "Exam circular error");
                ECPBar.visibility = View.GONE

                fetchData(url, t+1);



            })
        MySingleton.getInstance(this).addToRequestQueue(allCirReq);
    }



    override fun onSupportNavigateUp(): Boolean {
        onBackPressed();
        return true;
    }

    override fun articleClickListener(article: Article) {
        val url = article.url

        val webIntent = Intent(Intent.ACTION_VIEW);
        webIntent.data = Uri.parse(url);
        startActivity(webIntent)
    }
}