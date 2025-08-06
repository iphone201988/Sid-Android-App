package com.tech.sid.base.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.tech.sid.BuildConfig
import com.tech.sid.CommonFunctionClass
import com.tech.sid.base.utils.event.NetworkErrorHandler
import com.tech.sid.data.api.ApiHelper
import com.tech.sid.data.api.ApiHelperImpl
import com.tech.sid.data.api.ApiService
import com.tech.sid.data.api.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    fun provideBaseUrl() = Constants.BASE_URL
//    fun provideBaseUrl() = BuildConfig.BASE_URL

    @Singleton
    @Provides
    fun networkErrorHandler(context: Application): NetworkErrorHandler {
        return NetworkErrorHandler(context)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val customLogger = Interceptor { chain ->
            val request = chain.request()

            // --- Log Request Body ---
            val requestBody = request.body
            val buffer = okio.Buffer()
            requestBody?.writeTo(buffer)
            val requestBodyString = buffer.readUtf8()

            val response = chain.proceed(request)

            // --- Log Response Body ---
            val responseBody = response.peekBody(Long.MAX_VALUE)
            val responseBodyString = responseBody.string()

//            Log.d("API_RESPONSE", "\n"  +"\n" +"""
//            📤 REQUEST
//            → URL: ${request.url}
//            → METHOD: ${request.method}
//            → BODY: $requestBodyString
//
//            📥 RESPONSE
//            ← CODE: ${response.code}
//            ← BODY: $responseBodyString
//        """.trimIndent())
            if (BuildConfig.DEBUG){
                Log.d(
                    "API_RESPONSE",
                    "\n\n📤 REQUEST\n→ URL: ${request.url}\n→ METHOD: ${request.method}\n→ BODY:"
                )
                prettyJson("API_RESPONSE", requestBodyString)

                Log.d("API_RESPONSE", "\n📥 RESPONSE\n← CODE: ${response.code}\n← BODY:")
                prettyJson("API_RESPONSE", responseBodyString)
            }

            response
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(customLogger)
            .connectTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .build()
    }

    fun prettyJson(tag: String = "API_RESPONSE", rawJson: String) {
        if (rawJson.isEmpty()) {
            return
        }
        try {
            val json = if (rawJson.trim().startsWith("{"))
                JSONObject(rawJson)
            else
                JSONArray(rawJson)

            val pretty = json.toString() // indent with 2 spaces
            printVeryLongJson(tag, pretty)

        } catch (e: Exception) {

            CommonFunctionClass.logPrint(    tag="API_RESPONSE",  response ="Invalid JSON: ${e.message}")
            CommonFunctionClass.logPrint(  tag="API_RESPONSE",    response =rawJson)
        }
    }

    fun printVeryLongJson(tag: String, prettyJson: String) {
        val lines = prettyJson.split("\n")
        for (line in lines) {
            Log.d(tag, line)
        }
    }

    /*
        @Provides
        @Singleton
        fun provideOkHttpClient(): OkHttpClient {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            val customResponseLogger = Interceptor { chain ->
                val request = chain.request()
                val response = chain.proceed(request)

                // Clone the response body for logging
                val responseBody = response.peekBody(Long.MAX_VALUE)

                Log.d("API_RESPONSE", """
                URL: ${request.url}
                METHOD: ${request.method}
                CODE: ${response.code}
                BODY_SEND: ${response.body}
                BODY: ${responseBody.string()}
            """.trimIndent())

                response
            }

            return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)       // existing detailed log
                .addInterceptor(customResponseLogger)     // your custom response log
                .connectTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .build()
        }
    */

    /*  @Provides
      @Singleton
      fun provideOkHttpClient(): OkHttpClient {
          val loggingInterceptor = HttpLoggingInterceptor()
          loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
          return OkHttpClient.Builder()
              .addInterceptor(loggingInterceptor)
              .connectTimeout(5, TimeUnit.MINUTES)
              .writeTimeout(5, TimeUnit.MINUTES)
              .readTimeout(5, TimeUnit.MINUTES)
          .build()
      }
  */
//    @Provides
//    @Singleton
//    fun provideOkHttpClient() = if (BuildConfig.DEBUG) {
//        val loggingInterceptor = HttpLoggingInterceptor()
//        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
//        OkHttpClient.Builder()
//            .addInterceptor(loggingInterceptor)
//            .connectTimeout(5, TimeUnit.MINUTES)
//            .writeTimeout(5, TimeUnit.MINUTES) // write timeout
//            .readTimeout(5, TimeUnit.MINUTES) // read timeout
//            /*.addInterceptor(BasicAuthInterceptor(Constants.USERNAME, Constants.PASSWORD))*/
//            .build()
//    } else OkHttpClient
//        .Builder()
//        .connectTimeout(5, TimeUnit.MINUTES)
//        .writeTimeout(5, TimeUnit.MINUTES) // write timeout
//        .readTimeout(5, TimeUnit.MINUTES) // read timeout
//        .build()


    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        BASE_URL: String
    ): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideApiHelper(apiHelper: ApiHelperImpl): ApiHelper = apiHelper

    @Provides
    @Singleton
    fun provideSharedPref(application: Application): SharedPreferences {
        return application.getSharedPreferences(application.packageName, Context.MODE_PRIVATE)
    }
}