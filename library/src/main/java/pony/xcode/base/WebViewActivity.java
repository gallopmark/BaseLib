package pony.xcode.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

public abstract class WebViewActivity extends CommonActivity {
    public static final String EXTRA_URL = "URL";
    public static final String EXTRA_TITLE = "title";

    protected LinearLayout mWebContent;
    protected FrameLayout mWebContainer;  //包裹webView的布局
    protected WebView mWebView;

    private boolean mLoadError;  //是否加载失败

    @Override
    protected int getLayoutResourceId() {
        return R.layout.layout_android_web_content;
    }

    @Override
    protected void setup(@Nullable Bundle savedInstanceState) {
        initView();
        initSettings();
        initWebClient();
        mWebView.loadUrl(getExtraUrl());
    }

    protected String getExtraUrl() {
        return getIntent().getStringExtra(EXTRA_URL);
    }

    protected String getExtraTitle() {
        return getIntent().getStringExtra(EXTRA_TITLE);
    }

    private void initView() {
        mWebContent = findViewById(R.id.android_web_content);
        mWebContainer = findViewById(R.id.android_web_parent);
        mWebView = findViewById(R.id.android_webView);
        View titleView = getCustomTitleView();
        if (titleView != null) {
            mWebContent.addView(titleView, 0);  //添加自定义标题
        }
    }

    @Nullable
    protected abstract View getCustomTitleView();

    @SuppressLint("SetJavaScriptEnabled")
    private void initSettings() {
        WebSettings webSettings = mWebView.getSettings();
        //设置UA
        String userAgent = webSettings.getUserAgentString();
        webSettings.setUserAgentString(userAgent + getApplication().getApplicationInfo().name);
        // 设置支持JavaScript
        webSettings.setJavaScriptEnabled(true);
        //5.0以上支持http和https混合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        //网页有缩放的需求才会体现出此特性，比如访问的是电脑版网页
        webSettings.setSupportZoom(true);
        //显示原生缩放控制
        webSettings.setDisplayZoomControls(true);
        webSettings.setBuiltInZoomControls(true);

//        		webSettings.setDatabaseEnabled(true);
//        		String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();

        //是否支持地理位置
        webSettings.setGeolocationEnabled(true);
//        		webSettings.setGeolocationDatabasePath(dir);

        //将图片调整到适合webView的大小
        webSettings.setUseWideViewPort(true);
        // 缩放至屏幕的大小
        webSettings.setLoadWithOverviewMode(true);


        //默认也都是开
        webSettings.setAllowFileAccess(true);//是否可以访问文件协议开头的资源
        webSettings.setAllowContentAccess(true);//是否可以访问contentProvider协议的资源


        //数据缓存分为两种：AppCache和DOM Storage（Web Storage）。
        //dom storage 可以持久化存储，类似sharePreference
        //如果网页端需要存储一些简单的用key/value对即可解决的数据，DOM Storage是非常完美的方案。
        webSettings.setDomStorageEnabled(true);
//        webSettings.setDatabasePath();已无效

//网页端的缓存。无关domStorage。
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setAppCacheEnabled(false);

//        LOAD_CACHE_ONLY： 不使用网络，只读取本地缓存数据，
//        LOAD_DEFAULT：根据cache-control决定是否从网络上取数据。HTTP 协议头里的 Cache-Control，
//        LOAD_CACHE_NORMAL：API level 17中已经废弃, 从API level 11开始作用同- - LOAD_DEFAULT模式，
//        LOAD_NO_CACHE: 不使用缓存，只从网络获取数据，
//        LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。

//        http协议缓存机制是指通过 HTTP 协议头里的 Cache-Control（或 Expires）和 Last-Modified（或 Etag）等字段来控制文件缓存的机制。
//        Cache-Control 用于控制文件在本地缓存有效时长。最常见的，比如服务器回包：Cache-Control:max-age=600 表示文件在本地应该缓存，且有效时长是600秒（从发出请求算起）。在接下来600秒内，如果有请求这个资源，浏览器不会发出 HTTP 请求，而是直接使用本地缓存的文件。
//        Last-Modified 是标识文件在服务器上的最新更新时间。下次请求时，如果文件缓存过期，浏览器通过 If-Modified-Since 字段带上这个时间，发送给服务器，由服务器比较时间戳来判断文件是否有修改。如果没有修改，服务器返回304告诉浏览器继续使用缓存；如果有修改，则返回200，同时返回最新的文件。

//        Cache-Control 通常与 Last-Modified 一起使用。一个用于控制缓存有效时间，一个在缓存失效后，向服务查询是否有更新。
//        Cache-Control 还有一个同功能的字段：Expires。Expires 的值一个绝对的时间点，如：Expires: Thu, 10 Nov 2015 08:45:11 GMT，表示在这个时间点之前，缓存都是有效的。
//        Expires 是 HTTP1.0 标准中的字段，Cache-Control 是 HTTP1.1 标准中新加的字段，功能一样，都是控制缓存的有效时间。当这两个字段同时出现时，Cache-Control 是高优化级的。

//        Etag 也是和 Last-Modified 一样，对文件进行标识的字段。不同的是，Etag 的取值是一个对文件进行标识的特征字串。
//        在向服务器查询文件是否有更新时，浏览器通过 If-None-Match 字段把特征字串发送给服务器，由服务器和文件最新特征字串进行匹配，来判断文件是否有更新。
//        没有更新回包304，有更新回包200。
//        Etag 和 Last-Modified 可根据需求使用一个或两个同时使用。两个同时使用时，只要满足基中一个条件，就认为文件没有更新。


        //允许跨域访问
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            webSettings.setAllowUniversalAccessFromFileURLs(true);


        //5.0以上打开webView跨域携带cookie的许可
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptThirdPartyCookies(mWebView, true);
        }
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
    }

    private void initWebClient() {
        mWebView.setWebViewClient(getWebViewClient());
        mWebView.setWebChromeClient(getWebChromeClient());
        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                if (isSupportDownload()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }
            }
        });
    }

    protected WebViewClient getWebViewClient() {
        return new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                webView.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mLoadError = false;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                WebViewActivity.this.onReceivedError();
                mLoadError = true;
            }

            @Override
            public void onFormResubmission(WebView view, Message dontResend, Message resend) {
                resend.sendToTarget();
            }

            @Override
            public void onReceivedSslError(WebView webView, SslErrorHandler handler, SslError sslError) {
                super.onReceivedSslError(webView, handler, sslError);
                handler.proceed();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!mLoadError) {
                    onPageSuccess();
                    mLoadError = false;
                }
            }
        };
    }

    @Nullable
    protected View getErrorView() {
        return null;
    }

    protected void onReceivedError() {
        mWebView.setVisibility(View.INVISIBLE);
        ensureErrorView();
    }

    private void ensureErrorView() {
        View errorView = findViewById(R.id.android_web_error_content);
        if (errorView == null) {
            errorView = getErrorView();
            if (errorView != null) {
                errorView.setId(R.id.android_web_error_content);
                mWebContainer.addView(errorView);
            }
        }
    }

    protected void onPageSuccess() {
        hideErrorView();
        mWebView.setVisibility(View.VISIBLE);
    }

    private void hideErrorView() {
        View errorView = findViewById(R.id.android_web_error_content);
        if (errorView != null) {
            mWebContainer.removeView(errorView);
        }
    }

    protected WebChromeClient getWebChromeClient() {
        return new WebChromeClient() {
            //当网页请求地理位置
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin
                        , true   //如果不允许的话，JS请求地理位置会得到error：permission denied by user
                        , false);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (TextUtils.isEmpty(title) || title.startsWith("http"))
                    return;//有时候网页端没有设置title会返回url
                WebViewActivity.this.onReceivedTitle(title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (isSupportProgress()) {
                    ProgressBar progressBar = getProgressBar();
                    if (newProgress == 100) {
                        progressBar.setVisibility(View.GONE);//加载完网页进度条消失
                    } else {
                        progressBar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                        progressBar.setProgress(newProgress);//设置进度值
                    }
                }
            }
        };
    }

    protected void onReceivedTitle(String title) {

    }

    protected boolean isSupportProgress() {
        return true;
    }

    /*设置是否支持下载*/
    protected boolean isSupportDownload() {
        return true;
    }

    protected void refresh() {
        mWebView.reload();
    }

    protected ProgressBar getProgressBar() {
        ProgressBar progressBar = findViewById(R.id.android_web_progress);
        if (progressBar == null) {
            View view = getLayoutInflater().inflate(R.layout.layout_android_web_progressbar, mWebContainer, true);
            progressBar = view.findViewById(R.id.android_web_progress);
        }
        return progressBar;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
        mWebView.resumeTimers();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
        mWebView.pauseTimers();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                onBackPressed();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroy() {
        if (mWebView != null) {
            //webview调用destory时,webview仍绑定在Activity上.
            // 这是由于自定义webview构建时传入了该Activity的context对象,因此需要先从父容器中移除webview,然后再销毁webview.
            //还可以让webView运行在单独进程中，不用的时候直接system.exit(0)
            ViewParent parent = mWebView.getParent();
            if (parent != null) {
                //防止内存泄露。
                mWebView.clearHistory();
                ((ViewGroup) mWebView.getParent()).removeView(mWebView);
                mWebView.stopLoading();
                mWebView.setWebChromeClient(null);
                mWebView.setWebViewClient(null);
            }
            mWebView.removeAllViews();
            mWebView.destroy();
        }
        super.onDestroy();
    }

}
