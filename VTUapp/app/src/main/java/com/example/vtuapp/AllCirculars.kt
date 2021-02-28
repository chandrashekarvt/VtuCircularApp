package com.example.vtuapp
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import kotlinx.android.synthetic.main.activity_all_circulars.*
import kotlinx.android.synthetic.main.internet_alert.view.*
import org.json.JSONArray
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule


class AllCirculars : AppCompatActivity(), articleClicked {


    private lateinit var customAdapter: CustomAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_all_circulars)


        val actionBar = supportActionBar
        actionBar!!.title = "Latest Circulars"


        actionBar.setDisplayHomeAsUpEnabled(true)

        allcircRecyclerView.layoutManager = LinearLayoutManager(this)



        customAdapter = CustomAdapter(this)

        allcircRecyclerView.adapter = customAdapter

        val networkConnection = NetworkConnection(this)

        //No Internet
        val networkDialog = LayoutInflater.from(this).inflate(R.layout.internet_alert, null);
        val builder = AlertDialog.Builder(this).setView(networkDialog)
        val alertDialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)



        fetchData("https://vtu-data.herokuapp.com/1")


        networkConnection.observe(this, Observer { isConnected ->
            if(isConnected){

                if(alertDialog.isShowing) {
                    fetchData("https://vtu-data.herokuapp.com/1")
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


    private fun fetchData(url: String, t: Int = 1){

        if(t>3){
            return;
        }



        ACPBar.visibility = View.VISIBLE


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

                ACPBar.visibility = View.GONE
            },
            Response.ErrorListener {
                Log.d("ERROR API", "All circular error");
                ACPBar.visibility = View.GONE
                fetchData(url, t+1)

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