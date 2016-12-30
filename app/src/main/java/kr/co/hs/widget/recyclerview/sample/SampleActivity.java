package kr.co.hs.widget.recyclerview.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import kr.co.hs.widget.recyclerview.HsRecyclerView;

/**
 * Created by Bae on 2016-12-30.
 */
public class SampleActivity extends AppCompatActivity {

    HsRecyclerView mHsRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        mHsRecyclerView = (HsRecyclerView) findViewById(R.id.RecyclerView);
    }

}
