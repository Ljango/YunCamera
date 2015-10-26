package jango.camera;

import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.squareup.picasso.Picasso;

public class ResultActivity extends AppCompatActivity {

    private ImageView result_img;
    private String filepath ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        result_img = (ImageView) findViewById(R.id.result_img);
        filepath = getIntent().getStringExtra("Path");
    }
    @Override
    protected void onStart(){
        if (filepath.contains("content")){
            Picasso.with(getApplicationContext()).load(filepath).into(result_img);
        }else {
            Picasso.with(getApplicationContext()).load("file://"+filepath).into(result_img);
        }
        super.onStart();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
    }
}
