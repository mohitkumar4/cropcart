package com.example.cropcart.news.mediastack

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cropcart.R
import com.example.cropcart.gui.text.SimpleTextView

class MediaStackArticleAdapter(private val context: Context, private var articles: List<MediaStackArticle>) : RecyclerView.Adapter<MediaStackArticleAdapter.ItemViewHolder>(){
    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val titleV: SimpleTextView = view.findViewById(R.id.title)
        val descV: SimpleTextView = view.findViewById(R.id.desc)
        val publishedAtV: SimpleTextView = view.findViewById(R.id.publishedAt)
        val sourceV: SimpleTextView = view.findViewById(R.id.source)
        val authorV: SimpleTextView = view.findViewById(R.id.author)

        fun bind(item: MediaStackArticle){
            titleV.text = item.title
            descV.text = item.description
            setMetaData(publishedAtV, item.published_at, "Published date")
            setMetaData(sourceV, item.source, "Source")
            setMetaData(authorV, item.author, "Author")
        }

        private fun setMetaData(textView: SimpleTextView, property: String?, indicatorText: String){
            if (property.isNullOrEmpty()){
                textView.text = ""
                textView.visibility = View.GONE
            }
            else{
                textView.text = "${indicatorText}: ${property}"
                textView.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_mediastack_article, parent, false))
    }

    override fun onBindViewHolder(
        holder: ItemViewHolder,
        position: Int
    ) {
        holder.bind(articles[position])
    }

    override fun getItemCount(): Int = articles.size

    fun updateData(articles: List<MediaStackArticle>){
        this.articles = articles
        notifyDataSetChanged()
    }
}