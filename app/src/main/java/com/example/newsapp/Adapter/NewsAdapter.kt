package com.example.newsapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.Models.Article
import com.example.newsapp.R

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {
    class ArticleViewHolder(view: View) : RecyclerView.ViewHolder(view)
    lateinit var articleImage: ImageView
    lateinit var articleSource: TextView
    lateinit var articleTitle: TextView
    lateinit var articleDescription: TextView
    lateinit var articleDateTime: TextView
    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(
            oldItem: Article,
            newItem: Article
        ): Boolean {
            return oldItem.url == newItem.url
        }
        override fun areContentsTheSame(
            oldItem: Article,
            newItem: Article
        ): Boolean {

            return oldItem == newItem

        }
    }
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ArticleViewHolder {
        return ArticleViewHolder(

            LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)

        )
    }
    override fun onBindViewHolder(
        holder: ArticleViewHolder,
        position: Int
    ) {

        val article = differ.currentList[position]
        articleImage = holder.itemView.findViewById(R.id.articleClip)
        articleSource = holder.itemView.findViewById(R.id.source)
        articleTitle = holder.itemView.findViewById(R.id.title)
        articleDescription = holder.itemView.findViewById(R.id.description)
        articleDateTime = holder.itemView.findViewById(R.id.dateTime)


        holder.itemView.apply {



            Glide.with(context).load(article.urlToImage).into(articleImage)

            articleTitle.text = article?.title
            articleDescription.text = article?.description
            articleDateTime.text = article?.publishedAt
            articleSource.text = article?.source?.name

            setOnItemClickListener {
                onItemClickListener?.let {
                    it(article)
                }
            }



        }
    }
    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    private var onItemClickListener:((Article) -> Unit)? = null
    fun  setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

}