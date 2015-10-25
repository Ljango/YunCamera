package jango.camera;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import jango.camera.utils.Constants;
import jango.camera.utils.FileUtil;
import jango.camera.utils.SettingToast;

public class MainActivity extends AppCompatActivity {
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private Camera.Parameters mParameters;
    private File mPictureFile;
    private Button mysavebtn;
    private final int REQUEST_CATEGORY = 11;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart(){
        intview();
        super.onStart();
    }

    private void intview(){
        mSurfaceView = (SurfaceView) findViewById(R.id.camera_preview01); // components
        mysavebtn = (Button) findViewById(R.id.my_takepic);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(surfaceCallback);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("requestCode", String.valueOf(requestCode));
        switch (requestCode){
            case REQUEST_CATEGORY:
                Log.d("requestCode", String.valueOf(data.getData()));
                startResult(String.valueOf(data.getData()));
            //    Bitmap cameraBitmap = (Bitmap) data.getExtras().get("data");
            //    new SavePictureTask().execute();
           //     data.getData();
            //    startResult();
                break;
        }
    }

    public void MyOnlick(View view){
        switch(view.getId()){
            case R.id.my_takepic:
                mCamera.stopPreview();// stop the previe
                mCamera.takePicture(null, null, pictureCallback); // picture
                break;
            case R.id.my_chosepic:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                intent.putExtra("return-data", true);
                startActivityForResult(intent, REQUEST_CATEGORY);

                /*intent.putExtra("crop", "true");
                intent.putExtra("aspectX",1);
                intent.putExtra("aspectY",1);
                intent.putExtra("outputX", 80);
                intent.putExtra("outputY", 80);*/
                break;
            case R.id.my_info:
                break;
        }
    }




    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

        public void surfaceCreated(SurfaceHolder holder) {
            Log.v("", "surfaceCallback====");
            mCamera = Camera.open(); // Turn on the camera
            try {
                mCamera.setPreviewDisplay(holder); // Set Preview
            } catch (IOException e) {
                mCamera.release();// release camera
                mCamera = null;
            }
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            Log.v("", "====surfaceChanged");
            mParameters = mCamera.getParameters();

            mParameters.setExposureCompensation(0);
            if (Integer.parseInt(Build.VERSION.SDK) >= 8)
                setDisplayOrientation(mCamera, 90);
            else {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    mParameters.set("orientation", "portrait");
                    mParameters.set("rotation", 90);
                }
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    mParameters.set("orientation", "landscape");
                    mParameters.set("rotation", 90);
                }
            }
            mParameters.setPictureFormat(PixelFormat.JPEG);
            List<Camera.Size> sizes = mParameters.getSupportedPreviewSizes();
            Camera.Size optimalSize = getOptimalPreviewSize(sizes, width, height);
            mParameters.setPictureSize(optimalSize.width, optimalSize.height);

            mCamera.setParameters(mParameters);
            mCamera.startPreview();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.v("", "====surfaceDestroyed");
            mCamera.stopPreview();// stop preview
            mCamera.release(); // Release camera resources
            mCamera = null;
        }
    };

    protected void setDisplayOrientation(Camera camera, int angle) {
        Method downPolymorphic;
        try {
            downPolymorphic = camera.getClass().getMethod(
                    "setDisplayOrientation", new Class[] { int.class });
            if (downPolymorphic != null)
                downPolymorphic.invoke(camera, new Object[] { angle });
        } catch (Exception e1) {

        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.2;
        double targetRatio = (double) w / h;
        if (sizes == null)
            return null;
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        int targetHeight = h;
        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            Log.v("", "Checking size " + size.width + "w " + size.height + "h");
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
        // Cannot find the one match the aspect ratio, ignore the
        // requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }


    // 图片回调
    Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        // @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            new SavePictureTask().execute(data);
            camera.startPreview();
        }
    };

    // 保存图片
    class SavePictureTask extends AsyncTask<byte[], String, String> {
        @Override
        protected String doInBackground(byte[]... params) {
            String sdStatus = Environment.getExternalStorageState();
            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                Message message = mHandler.obtainMessage();
                message.what = Constants.IS_HAVE_SDCARD;
                message.obj = getString(R.string.have_sdcard);
                mHandler.sendMessage(message);
                return null;

            }
            mPictureFile = FileUtil.getOutputMediaFile();
            try {
                FileOutputStream fos = new FileOutputStream(
                        mPictureFile.getPath()); // Get
                // stream
                fos.write(params[0]); // Written to the file
                Message message = mHandler.obtainMessage();
                message.what = Constants.UPDATA_TOAST_MSG;
                message.obj = getString(R.string.photo_save_path);
                mHandler.sendMessage(message);
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.UPDATA_TOAST_MSG:
                    SettingToast.setToastStrLong(MainActivity.this,
                            msg.obj.toString() + mPictureFile.getPath());
                    startResult(mPictureFile.getPath());
                    break;
                case Constants.IS_HAVE_SDCARD:
                    SettingToast.setToastStrLong(MainActivity.this,
                            msg.obj.toString());
                    break;
                default:
                    break;
            }
        }

    };

    private void startResult(String path){
        startActivity(new Intent(MainActivity.this,ResultActivity.class)
                .putExtra("Path",path));
    }
}
