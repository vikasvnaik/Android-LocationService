package com.mvvm.data.remote

import android.app.Application
import android.os.Build
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.mvvm.data.BuildConfig
import com.mvvm.domain.entity.response.base.Success
import com.mvvm.domain.entity.wrapped.Event
import com.mvvm.domain.extention.G
import com.mvvm.domain.extention.fromJson
import com.mvvm.domain.extention.isNotEmptyOrNull
import com.mvvm.domain.extention.post
import com.mvvm.domain.manager.UserPrefDataManager
import kotlinx.coroutines.channels.Channel
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import timber.log.Timber
import java.security.cert.CertificateException
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class RetrofitManager(
    private val context: Application,
   // private val userDataManager: UserPrefDataManager
) {

    val success = Channel<String>()
    val error = Channel<String>()
    val failure = Channel<String>()
    val httpCode = Channel<Event<Int>>()
    val tokenExpire = Channel<Event<String>>()
    private val LINE_SEPARATOR = System.getProperty("line.separator")!!

    private val isDebug = BuildConfig.DEBUG

    private val okHttpInterceptor = run {
        val httpLoggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                if (!isDebug) return
                when {
                    message.startsWith("{") -> {
                        JSONObject(message).toString(1).let {
                            val lines =
                                it.split(LINE_SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                            for (line in lines) {
                                Timber.d("║ $line")
                            }
                        }
                    }
                    message.startsWith("[") -> {
                        JSONArray(message).toString(1)?.let {
                            val lines =
                                it.split(LINE_SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                            lines.forEach {
                                Timber.d("║ $it")
                            }
                        }
                    }
                    else -> {
                        Timber.d("║ $message")
                    }
                }
            }
        })
        httpLoggingInterceptor.apply {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        }
    }
    private val refreshToken = RefreshToken()

    private val contentType = "application/json".toMediaType()
    private val okHttpClient =
        (if (BuildConfig.DEBUG) getUnsafeOkHttpClient() else OkHttpClient().newBuilder())
            .connectTimeout(10.toLong(), TimeUnit.SECONDS)
            .readTimeout(10.toLong(), TimeUnit.SECONDS)
            .writeTimeout(10.toLong(), TimeUnit.SECONDS)
            .addInterceptor(refreshToken)
            .addInterceptor(UserAgentInterceptor())
            .addInterceptor(GlobalHandler())
            .addInterceptor(okHttpInterceptor)
            .addInterceptor(IO())
            .build()

    fun retrofit(baseUrl: String): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(baseUrl)
        /*.addConverterFactory(factory)
        .addConverterFactory(ScalarsConverterFactory.create())*/
        .addConverterFactory(
            G.json.asConverterFactory(contentType)
        ).build()

    fun OkHttpClient.Builder.headers(headers: Map<String, String> = emptyMap()): OkHttpClient.Builder {
        if (headers.isNotEmpty()) {
            addInterceptor {
                val builder = it.request().newBuilder()
                headers.forEach { (key, value) ->
                    builder.addHeader(key, value)
                }
                it.proceed(builder.build())
            }
        }
        return this
    }

    private inner class UserAgentInterceptor : Interceptor {

        private val userAgent: String by lazy {
            buildUserAgent()
        }

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val builder = request.newBuilder()
            builder.header("User-Agent", userAgent)
            val httpUrl = request.url.newBuilder().setQueryParameter("locale", "en").build()
            builder.url(httpUrl)
            print("API call")
           /* if (userDataManager.isUserLoggedIn) {
                userDataManager.token!!.isNotEmptyOrNull {
                    builder.header("Authorization", "Token $this")
                }
            }*/
            return chain.proceed(builder.build())
        }

        private fun buildUserAgent(): String {
            val versionName = BuildConfig.VERSION_NAME
            val versionCode = BuildConfig.VERSION_CODE
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            val version = Build.VERSION.SDK_INT
            val versionRelease = Build.VERSION.RELEASE
            val installerName = context.packageName
            return "$installerName / $versionName ($versionCode); ($manufacturer; $model; SDK $version; Android $versionRelease)"
        }
    }

    private inner class GlobalHandler : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder().build()
            val response = chain.proceed(request)
            httpCode.post(Event(response.code))
            print("API Response")
            if (response.isSuccessful) {
                val source = response.body?.source()
                source?.request(java.lang.Long.MAX_VALUE)
                source?.buffer?.clone()?.readUtf8()?.apply {
                    success.post(this)
                }
            } else {
                val source = response.body?.source()
                source?.request(Long.MAX_VALUE)
                source?.buffer?.clone()?.readUtf8()?.apply {
                    error.post(this)
                }
            }
            return response
        }

    }

    private inner class IO : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            try {
                val request = chain.request().newBuilder().build()
                return chain.proceed(request)
            } catch (e: Exception) {
                failure.post(e.toString())
            }
            return chain.proceed(chain.request())
        }

    }

    private fun getUnsafeOkHttpClient(): OkHttpClient.Builder {
        try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(
                    chain: Array<java.security.cert.X509Certificate>,
                    authType: String
                ) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(
                    chain: Array<java.security.cert.X509Certificate>,
                    authType: String
                ) {
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
            builder.hostnameVerifier(HostnameVerifier { _, _ -> true })
            return builder
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private inner class RefreshToken : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder().build()
            val response = chain.proceed(request)
            when (response.code) {
                403 -> {
                    val successModel = response.body?.string().fromJson<Success>()
                    tokenExpire.post(Event(successModel.message))
                }
            }
            return response
        }

    }
}