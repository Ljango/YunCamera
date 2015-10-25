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

public class ResultActivity extends AppCompatActivity {

    private ImageView result_img;
    private String filepath ="";
    private ImageLoader imageLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        result_img = (ImageView) findViewById(R.id.result_img);
        filepath = getIntent().getStringExtra("Path");
        Matrix matrix = new Matrix();
    }
    @Override
    protected void onStart(){
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(ResultActivity.this));
        imageLoader = ImageLoader.getInstance();
        initimage();
        System.out.print(filepath);
        Log.d("hehe", filepath);
        imageLoader.displayImage("file://"+filepath, result_img,initimage());
        super.onStart();
    }
    @Override
    protected void onDestroy(){
        ImageLoader.getInstance().destroy();
        super.onDestroy();
    }
    private DisplayImageOptions initimage(){

        DisplayImageOptions options;
        options = new DisplayImageOptions.Builder()
                .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .build();//构建完成
        /**/
        return options;
    }

}
