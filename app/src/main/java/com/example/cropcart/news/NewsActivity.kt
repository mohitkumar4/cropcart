package com.example.cropcart.news

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cropcart.BuildConfig
import com.example.cropcart.R
import com.example.cropcart.news.apitube.ApiTubeService
import com.example.cropcart.news.apitube.ApitubeArticleAdapter
import com.example.cropcart.news.mediastack.MediaStackArticleAdapter
import com.example.cropcart.news.mediastack.MediaStackService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NewsActivity : AppCompatActivity() {
    // apitube
    private lateinit var rvApitube: RecyclerView
    private lateinit var adapterApitube: ApitubeArticleAdapter


    // media stack
    private lateinit var rvMediaStack: RecyclerView
    private lateinit var adapterMediaStack: MediaStackArticleAdapter

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        rvApitube = findViewById<RecyclerView>(R.id.rvApitube)
        adapterApitube = ApitubeArticleAdapter(this, listOf())
        rvApitube.adapter = adapterApitube
        rvApitube.layoutManager = LinearLayoutManager(this)

        rvMediaStack = findViewById<RecyclerView>(R.id.rvMediaStack)
        adapterMediaStack = MediaStackArticleAdapter(this, listOf())
        rvMediaStack.adapter = adapterMediaStack
        rvMediaStack.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch{ getNewsFromApitube() }
        lifecycleScope.launch{ getNewsFromMediaStack() }
    }

    suspend private fun getNewsFromApitube(){
        val apitubeApiKey = BuildConfig.APITUBE_API
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.apitube.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiTubeService::class.java)

        try {
            val response = withContext(Dispatchers.IO) {
                service.getEverything(apiKey = apitubeApiKey, query = "agriculture")
            }

            if (response.results.isNotEmpty()) adapterApitube.updateData(response.results)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    suspend private fun getNewsFromMediaStack() {
        val mediaStackApiKey = BuildConfig.MEDIASTACK_API

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.mediastack.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(MediaStackService::class.java)

        try {
            val response = withContext(Dispatchers.IO) {
                service.getNews(
                    apiKey = mediaStackApiKey,
                    query = "agriculture",
                    languages = "en",
                    countries = "in"
                )
            }

            if (response.data.isNotEmpty()) {
                adapterMediaStack.updateData(response.data)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(this@NewsActivity, "MediaStack Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}