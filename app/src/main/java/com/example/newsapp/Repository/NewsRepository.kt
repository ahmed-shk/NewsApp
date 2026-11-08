package com.example.newsapp.Repository

import com.example.newsapp.API.ApiClient
import com.example.newsapp.DB.ArticleDatabase
import com.example.newsapp.Models.Article
import retrofit2.Retrofit

class NewsRepository(val db: ArticleDatabase) {


    suspend fun getHeadlines(countryCode:String,pageNumber:Int) = ApiClient.api.getHeadline(countryCode,pageNumber)
    suspend fun getSearchNews(searchQuery:String,pageNumber:Int) = ApiClient.api.searchForNews(searchQuery,pageNumber)


    suspend fun upsert(article: Article) = db.getArticlesDao().upsert(article)
    fun getFavouriteNews() = db.getArticlesDao().getArticles()
    suspend fun deleteArticle(article: Article) = db.getArticlesDao().deleteArticle(article)


}