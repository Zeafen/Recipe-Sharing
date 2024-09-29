package com.receipts.receipt_sharing.domain.apiServices

import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class UnsafeOkHttpClient {
    companion object{
        fun getOkHttpClient() : OkHttpClient{
            try {
                val trustAllCerts : Array<TrustManager> = arrayOf(
                    object : X509TrustManager {
                        override fun checkClientTrusted(
                            p0: Array<out X509Certificate>?,
                            p1: String?
                        ) {
                        }

                        override fun checkServerTrusted(
                            p0: Array<out X509Certificate>?,
                            p1: String?
                        ) {
                        }

                        override fun getAcceptedIssuers(): Array<X509Certificate> {
                            return arrayOf()
                        }
                    })

                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())

                val sslSocketFactory = sslContext.socketFactory

                val builder = OkHttpClient.Builder()
                builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                builder.hostnameVerifier(object : HostnameVerifier{
                    override fun verify(p0: String?, p1: SSLSession?): Boolean {
                        return true
                    }
                })

                val client = builder.build()
                return client
            }
            catch (e : Exception){
                e.printStackTrace()
                throw RuntimeException(e)
            }
        }
    }
}