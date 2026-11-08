package com.example.newsapp.UI

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.newsapp.Models.Article
import com.example.newsapp.Models.NewsResponse
import com.example.newsapp.Repository.NewsRepository
import com.example.newsapp.Utils.Resource
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response

class NewsViewModel(app: Application, val newsRepository: NewsRepository): AndroidViewModel(app) {
    var headlines: MutableLiveData<Resource<NewsResponse?>> = MutableLiveData()
    var headlinePage = 1
    var headlineResponse: NewsResponse? = null

    var searchNews: MutableLiveData<Resource<NewsResponse?>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null
    var newSearchQuery: String? = null
    var oldSearchQuery: String? = null

    private fun handleHeadlinesResponse(response: Response<NewsResponse>): Resource<NewsResponse?> {
        if (response.isSuccessful) {
            response.body()?.let { responseResult ->
                headlinePage++
                if (headlineResponse == null) {
                    headlineResponse = responseResult
                } else {
                    val oldArticles = headlineResponse?.articles
                    val newArticles = responseResult.articles
                    newArticles?.let { oldArticles?.addAll(it) }
                }
                return Resource.Success(headlineResponse ?: responseResult)
            }
        }
        return Resource.Error(message = response.message() ?: "Unknown error")
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse?> {
        if (response.isSuccessful) {
            response.body()?.let { responseResult ->

                if (searchNewsResponse == null || oldSearchQuery != newSearchQuery) {
                    searchNewsPage = 1
                    oldSearchQuery = newSearchQuery
                    searchNewsResponse = responseResult
                } else {
                    searchNewsPage++
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = responseResult.articles
                    newArticles?.let { oldArticles?.addAll(it) }
                }
                return Resource.Success(searchNewsResponse ?: responseResult)
            }
        }
        return Resource.Error(message = response.message() ?: "Unknown error")
    }


    fun addToFavourite(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }


    fun getFavoriteNews() = newsRepository.getFavouriteNews()


    fun internetConnection(context: Context) : Boolean {
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
            return getNetworkCapabilities(activeNetwork)?.run {
                when {

                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                            hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                            hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } ?: false
        }
    }

    private suspend fun headlinesInternet(countryCode:String) {
        headlines.postValue(Resource.Loading())
        try {

            if(internetConnection(this.getApplication())) {
                val response = newsRepository.getHeadlines(countryCode,headlinePage)
                headlines.postValue(handleHeadlinesResponse(response))
            } else {
                headlines.postValue(Resource.Error(message = "No Internet"))
            }
        } catch (t: Throwable) {
            when(t) {
                is IOException -> headlines.postValue(Resource.Error(message = "Unable to connect"))
                else -> headlines.postValue(Resource.Error(message = "No Signal"))
            }
        }
    }


    private suspend fun searchNewsInternet(searchQuery:String) {
        newSearchQuery = searchQuery
        searchNews.postValue(Resource.Loading())
        try {

            if(internetConnection(this.getApplication())) {
                val response = newsRepository.getSearchNews(searchQuery,searchNewsPage)
                headlines.postValue(handleSearchNewsResponse(response))
            } else {
                headlines.postValue(Resource.Error(message = "No Internet"))
            }
        } catch (t: Throwable) {
            when(t) {
                is IOException -> headlines.postValue(Resource.Error(message = "Unable to connect"))
                else -> headlines.postValue(Resource.Error(message = "No Signal"))
            }
        }
    }
}
