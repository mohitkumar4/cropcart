package com.example.cropcart.news

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cropcart.BuildConfig
import com.example.cropcart.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NewsActivity : AppCompatActivity() {
    // views
    private lateinit var rv: RecyclerView
    private lateinit var adapter: ApitubeArticleAdapter

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        rv = findViewById<RecyclerView>(R.id.rv)
        adapter = ApitubeArticleAdapter(this, listOf())
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch{
            getNews()
        }
    }

    suspend fun getNews(){
        val apiKey = BuildConfig.ATITUBE_API

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.apitube.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiTubeService::class.java)

        try {
            val response = withContext(Dispatchers.IO) {
                service.getEverything(apiKey = apiKey, query = "agriculture")
            }

            if (response.results.isNotEmpty()) adapter.updateData(response.results)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }
}