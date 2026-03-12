package com.example.cropcart.news

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cropcart.R
import com.example.cropcart.gui.text.SimpleTextView

class ApitubeArticleAdapter(private val context: Context, private var articles: List<ApitubeArticle>) : RecyclerView.Adapter<ApitubeArticleAdapter.ItemViewHolder>(){
    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val titleV: SimpleTextView = view.findViewById(R.id.title)
        val descV: SimpleTextView = view.findViewById(R.id.desc)
        val publishedAtV: SimpleTextView = view.findViewById(R.id.publishedAt)
        val sourceV: SimpleTextView = view.findViewById(R.id.source)

        fun bind(item: ApitubeArticle){
            titleV.text = item.title
            descV.text = item.description
            publishedAtV.text = item.published_at
            sourceV.text = item.source.name
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_article, parent, false))
    }

    override fun onBindViewHolder(
        holder: ApitubeArticleAdapter.ItemViewHolder,
        position: Int
    ) {
        holder.bind(articles[position])
    }

    override fun getItemCount(): Int = articles.size

    fun updateData(articles: List<ApitubeArticle>){
        this.articles = articles
        notifyDataSetChanged()
    }
}