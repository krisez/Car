package cn.krisez.car;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;

public class PeopleActivity extends AppCompatActivity {

    AMapLocationClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);
        Button button = findViewById(R.id.get_location);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client = new AMapLocationClient(PeopleActivity.this);
                client.setLocationListener(new AMapLocationListener() {
                    @Override
                    public void onLocationChanged(AMapLocation aMapLocation) {
                        Log.d("PeopleActivity", "onLocationChanged:" + aMapLocation);
                    }
                });
                client.startLocation();
            }

        });

        findViewById(R.id.end_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.stopLocation();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (client != null)
            client.onDestroy();
    }
}
