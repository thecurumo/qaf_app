package com.thecurumo.qaf;

import android.os.Bundle;
import androidx.core.view.WindowCompat;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // باید قبل از super.onCreate صدا زده بشه تا decor view درست تنظیم بشه
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        registerPlugin(DatabaseSetupPlugin.class);
        super.onCreate(savedInstanceState);

        // مخفی‌کردن اسکرول‌بار native خودِ WebView — این چیزیه که CSS
        // نمی‌تونه کنترلش کنه، چون مربوط به خودِ کامپوننت اندرویدی WebView است
        bridge.getWebView().setVerticalScrollBarEnabled(false);
        bridge.getWebView().setHorizontalScrollBarEnabled(false);
    }
}