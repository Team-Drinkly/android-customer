package com.project.drinkly.ui.onboarding.signUp

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.project.drinkly.BuildConfig
import com.project.drinkly.api.ApiClient
import com.project.drinkly.databinding.ActivityPassWebBinding
import com.project.drinkly.ui.onboarding.viewModel.LoginViewModel
import com.project.drinkly.util.MyApplication
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.URISyntaxException
import okhttp3.*
import okio.ByteString.Companion.encode
import java.net.URLEncoder


class PassWebActivity : AppCompatActivity() {

    private lateinit var mWebView: WebView

    lateinit var activityPassWebBinding: ActivityPassWebBinding
    lateinit var viewModel: LoginViewModel
    val apiClient = ApiClient(this)

    var getTokenVersionId = ""
    var getEncData = ""
    var getIntegrityValue = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityPassWebBinding = ActivityPassWebBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        observeViewModel()

        activityPassWebBinding.run {

            mWebView = webViewPass

            // 웹뷰의 설정을 다음과 같이 맞춰주시기 바랍니다.
            mWebView.settings.javaScriptEnabled = true // 필수설정(true)
            mWebView.settings.domStorageEnabled = true // 필수설정(true)
            mWebView.settings.databaseEnabled = true
            mWebView.settings.javaScriptCanOpenWindowsAutomatically = true // 필수설정(true)

            mWebView.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            mWebView.settings.loadsImagesAutomatically = true
            mWebView.settings.builtInZoomControls = true
//            mWebView.settings.supportZoom()
            mWebView.settings.setSupportMultipleWindows(true)
            mWebView.settings.loadWithOverviewMode = true
            mWebView.settings.useWideViewPort = true
            mWebView.settings.allowFileAccess = true //파일 접근 허용 설정

            mWebView.webViewClient = DemoWebViewClient()


            viewModel.getNiceUrlData(this@PassWebActivity, MyApplication.oauthId)
        }
        setContentView(activityPassWebBinding.root)
    }

    fun observeViewModel() {
        viewModel.run {
            passUrl.observe(this@PassWebActivity) {
                Log.d("DrinklyLog", "pass url : ${it}")
                mWebView.loadUrl(it)
            }
        }
    }

    inner class DemoWebViewClient : WebViewClient() {

        override fun onReceivedHttpError(
            view: WebView?,
            request: WebResourceRequest?,
            errorResponse: WebResourceResponse?
        ) {
            super.onReceivedHttpError(view, request, errorResponse)
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
        }

        override fun onReceivedSslError(
            view: WebView?, handler: SslErrorHandler,
            error: SslError?
        ) {

        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {

            Log.d("DrinklyLog", "redirect url : $url")

            // 웹뷰 내 표준창에서 외부앱(통신사 인증앱)을 호출하려면 intent:// URI를 별도로 처리해줘야 합니다.
            // 다음 소스를 적용 해주세요.
            if (url.startsWith("intent://")) {
                var intent: Intent? = null
                try {
                    intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                    if (intent != null) {
                        // 앱실행
                        startActivity(intent)
                    }
                } catch (e: URISyntaxException) {
                    // URI 문법 오류 시 처리 구간
                } catch (e: ActivityNotFoundException) {
                    val packageName = intent?.`package`
                    if (packageName != null && packageName != "") {
                        // 앱이 설치되어 있지 않을 경우 구글마켓 이동
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
                    }
                }
                // return 값을 반드시 true로 해야 합니다.
                return true
            } else if (url.startsWith("https://play.google.com/store/apps/details?id=") || url.startsWith("market://details?id=")) {
                // 표준창 내 앱설치하기 버튼 클릭 시 PlayStore 앱으로 연결하기 위한 로직
                val uri = Uri.parse(url)
                val packageName = uri.getQueryParameter("id")
                if (packageName != null && packageName != "") {
                    // 구글마켓 이동
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
                }
                // return 값을 반드시 true로 해야 합니다.
                return true
            } else if (url.startsWith(BuildConfig.SERVER_URL)) {
                Log.d("DrinklyLog", "${url.toHttpUrl().query}")
                Log.d("DrinklyLog", "${url.toUri().query.toString()}")

                var httpBody = url?.toHttpUrl()?.query.toString()

                val uri = Uri.parse(url)

                // 특정 키의 값 가져오기
                val id = URLEncoder.encode(uri.getQueryParameter("id"), "UTF-8")
                val type = URLEncoder.encode(uri.getQueryParameter("type"), "UTF-8")
                val tokenVersionId = URLEncoder.encode(uri.getQueryParameter("token_version_id"), "UTF-8")
                val encData = URLEncoder.encode(uri.getQueryParameter("enc_data"), "UTF-8")
                val integrityValue = URLEncoder.encode(uri.getQueryParameter("integrity_value"), "UTF-8")

                // 서버 요청
                viewModel.callBackNiceData(this@PassWebActivity, id, tokenVersionId, encData, integrityValue)

                return true
            }

            // return 값을 반드시 false로 해야 합니다.
            return false
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            mWebView.clearCache(true)
        }
    }
}