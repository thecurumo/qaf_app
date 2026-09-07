package com.thecurumo.qaf;

import android.os.Bundle;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerPlugin(DatabaseSetupPlugin.class);
        super.onCreate(savedInstanceState);
    }
}