package com.example.cropcart.information

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cropcart.BuildConfig
import com.example.cropcart.R
import com.example.cropcart.information.apitube.ApiTubeService
import com.example.cropcart.information.apitube.ApitubeArticleAdapter
import com.example.cropcart.information.mediastack.MediaStackArticleAdapter
import com.example.cropcart.information.mediastack.MediaStackService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NewsFragment : Fragment() {
    // views
    private lateinit var frg: View

    // apitube
    private lateinit var rvApitube: RecyclerView
    private lateinit var adapterApitube: ApitubeArticleAdapter


    // media stack
    private lateinit var rvMediaStack: RecyclerView
    private lateinit var adapterMediaStack: MediaStackArticleAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        frg = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_news, container, false)

        rvApitube = frg.findViewById<RecyclerView>(R.id.rvApitube)
        adapterApitube = ApitubeArticleAdapter(requireContext(), listOf())
        rvApitube.adapter = adapterApitube
        rvApitube.layoutManager = LinearLayoutManager(requireContext())

        rvMediaStack = frg.findViewById<RecyclerView>(R.id.rvMediaStack)
        adapterMediaStack = MediaStackArticleAdapter(requireContext(), listOf())
        rvMediaStack.adapter = adapterMediaStack
        rvMediaStack.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch{ getNewsFromApitube() }
        lifecycleScope.launch{ getNewsFromMediaStack() }

        return frg
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
        }
    }
}