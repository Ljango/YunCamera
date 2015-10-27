package jango.camera;

import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import cz.msebera.android.httpclient.Header;
import jango.camera.utils.FileUtil;

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
            Uri uri = Uri.parse(filepath);
            filepath = FileUtil.getRealFilePath(getApplicationContext(),uri);
        }else {
            Picasso.with(getApplicationContext()).load("file://"+filepath).into(result_img);
        }

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        File myFile = new File(filepath);
        System.out.print(filepath);
        try {
            params.put("file", myFile);
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        //  123.57.235.14

        client.post("http://192.168.2.26/upload", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.print(responseBody);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.print("onFailure");
            }
        });




       /* new Thread(new Runnable() {

            @Override
            public void run() {
                uploadFileAndString("http://192.168.2.26:5000/cell_upload",
                        "a.jpg", new File(filepath));
            }
        }).start();*/
        super.onStart();
    }



    @Override
    protected void onDestroy(){
        super.onDestroy();
    }
}
