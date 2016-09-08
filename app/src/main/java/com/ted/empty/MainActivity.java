package com.ted.empty;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    FoldLayout mFoldLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(new PolyToPolyView(this));
        setContentView(R.layout.fold_layout);
        mFoldLayout = (FoldLayout)findViewById(R.id.id_fold_layout);
    }

}
