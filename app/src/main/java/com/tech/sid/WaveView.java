package com.tech.sid;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/*public class WaveView extends WebView {

    private boolean pageLoaded = false;

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initializeWebSettings();

        setWebChromeClient(new WebChromeClient());
        setBackgroundColor(Color.TRANSPARENT);
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);

        setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                pageLoaded = true;
            }
        });

        loadUrl("file:///android_asset/voicewave.html");
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initializeWebSettings() {
        WebSettings webSetting = getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setLoadWithOverviewMode(true);
    }

    public void initialize(DisplayMetrics dm) {
        if (!pageLoaded) return; // Prevent premature call

        int width = getMeasuredWidth() > 0 ? getMeasuredWidth() : dm.widthPixels;
        int height = getMeasuredHeight() > 0 ? getMeasuredHeight() : dm.heightPixels / 4; // Example: 1/4 of screen height
        loadUrl("javascript:wave.setWidth(\"" + width + "\")");
        loadUrl("javascript:wave.setHeight(\"" + height + "\")");

        loadUrl("javascript:wave.start()");
    }

    public void stop() {
        if (!pageLoaded) return;
        loadUrl("javascript:wave.stop()");
        removeAllViews();
        clearHistory();
        clearCache(true);
        if (Build.VERSION.SDK_INT < 18) {
            clearView();
        } else {
            loadUrl("about:blank");
        }
        freeMemory();
        pauseTimers();
        pageLoaded = false;
        loadUrl("file:///android_asset/voicewave.html");
    }

    public void speechStarted() {
        if (!pageLoaded) return;
        loadUrl("javascript:wave.setAmplitude(1)");
    }
    public void setAmplitude(float amplitude) {
        loadUrl("javascript:wave.setAmplitude(\"" + amplitude + "\")");
    }
    public void speechEnded() {
        if (!pageLoaded) return;
        loadUrl("javascript:wave.setAmplitude(0.1)");
    }

    public void speechPaused() {
        if (!pageLoaded) return;
        loadUrl("javascript:wave.setAmplitude(0)");
    }
}*/

public class WaveView extends WebView {

    public WaveView(Context context) {
        super(context);
        initializeWebSettings();
        setupWebView();
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeWebSettings();
        setupWebView();
    }
    public void resetWave() {
        // Stop any running animation
//        loadUrl("javascript:wave.stop()");
//
//        // Clear the WebView completely
//        removeAllViews();
//        clearHistory();
//        clearCache(true);
//
//        if (Build.VERSION.SDK_INT < 18) {
//            clearView();
//        } else {
//            loadUrl("about:blank");
//        }
//
//        freeMemory();
//        pauseTimers();

        // Reinitialize WebView
        initializeWebSettings();
        setupWebView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initializeWebSettings() {
        WebSettings webSetting = getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setDomStorageEnabled(true);
        WebView.setWebContentsDebuggingEnabled(true);
    }

    private void setupWebView() {
//        setWebChromeClient(new WebChromeClient());
        setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("WaveView", consoleMessage.message() + " -- From line "
                        + consoleMessage.lineNumber() + " of "
                        + consoleMessage.sourceId());
                return true;
            }
        });
        setBackgroundColor(Color.TRANSPARENT);
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        loadUrl("file:///android_asset/voicewave.html");
    }

    public void initialize(DisplayMetrics dm) {
        // Use full screen width from DisplayMetrics or WebView's measured width
        int width = getMeasuredWidth() > 0 ? getMeasuredWidth() : dm.widthPixels;
        int height = getMeasuredHeight() > 0 ? getMeasuredHeight() : dm.heightPixels / 4; // Example: 1/4 of screen height
        loadUrl("javascript:wave.setWidth(\"" + width + "\")");
        loadUrl("javascript:wave.setHeight(\"" + height + "\")");

        loadUrl("javascript:wave.start()");
        speechPaused();
    }

    public void stop() {
        loadUrl("javascript:wave.stop()");

        removeAllViews();
        clearHistory();
        clearCache(true);

        if (Build.VERSION.SDK_INT < 18) {
            clearView();
        } else {
            loadUrl("about:blank");
        }

        freeMemory();
        pauseTimers();
        loadUrl("file:///android_asset/voicewave.html");
    }

    public void speechStarted() {
        loadUrl("javascript:wave.setAmplitude(\"1\")");
    }

//    public void setAmplitude(float amplitude) {
//        loadUrl("javascript:wave.setAmplitude(\"" + amplitude + "\")");
//    }

    public void setAmplitude(float amplitude) {
     //   Log.i("setAmplitudesetAmplitude", "setAmplitude: " + amplitude);
        loadUrl("javascript:wave.setAmplitude(" + amplitude + ")");
    }
    public void speechEnded() {
        loadUrl("javascript:wave.setAmplitude(\"0.1\")");
    }

    public void speechPaused() {
        loadUrl("javascript:wave.setAmplitude(\"0.0\")");
    }
}
