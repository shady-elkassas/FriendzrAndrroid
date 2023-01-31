package com.friendzrandroid.core.di

import com.friendzrandroid.auth.data.datasource.AuthAPIS
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.GeneralSecurityException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.*

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    //        private const val BASE_URL = "https://localtest.friendzsocialmedia.com/api/"
    private const val BASE_URL = "https://www.friendzsocialmedia.com/api/"


    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor()
        .apply {
            level = HttpLoggingInterceptor.Level.BODY
        }


//    @Singleton
//    @Provides
//    fun providesOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
//
//      val ok =   OkHttpClient
//            .Builder()
//            .addInterceptor { chain ->
//                val request = chain.request().newBuilder()
////                    .addHeader("authorization", UserSessionManagement.getKeyAuthToken())
//                    .addHeader("Content-Type", "application/json; charset=utf-8")
//                    .build()
//                return@addInterceptor chain.proceed(request)
//
//            }
//
//        return ok.build()
//    }


//            .followRedirects(true)
//            .followSslRedirects(true)
//            .retryOnConnectionFailure(true)
//            .cache(null)
//


    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    @Singleton
    @Provides
    fun provideAuthApis(retrofit: Retrofit): AuthAPIS {
        return retrofit
            .create(AuthAPIS::class.java)
    }


    @Singleton
    @Provides
    fun getUnsafeOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                }

                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                    return arrayOf()
                }
            })

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory

            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            // builder.hostnameVerifier { _, _ -> true }
            builder.hostnameVerifier ( hostnameVerifier = HostnameVerifier{ _, _ -> true })

            return builder.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}