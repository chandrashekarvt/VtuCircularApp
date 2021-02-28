package com.example.vtuapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView



class CustomAdapter(private val listener: articleClicked) : RecyclerView.Adapter<CustomViewHolder>() {

    private val articleList: ArrayList<Article> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.article_item, parent, false);

        val viewHolder = CustomViewHolder(view);

        view.setOnClickListener {
            listener.articleClickListener(articleList[viewHolder.adapterPosition])
        }

        return viewHolder;
    }

    override fun getItemCount(): Int {
        return articleList.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val article = articleList[position]
        holder.articleContent.text = article.Content
        holder.postedOn.text = "Posted on "+article.postedOn
    }

    fun updateArticles(articles: ArrayList<Article>){
        articleList.clear();
        articleList.addAll(articles);
        notifyDataSetChanged();
    }

}




class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val articleContent:TextView = itemView.findViewById(R.id.ArticleContent);
    val postedOn: TextView = itemView.findViewById(R.id.posted_date);
}

interface articleClicked{
    fun articleClickListener(article: Article)
}