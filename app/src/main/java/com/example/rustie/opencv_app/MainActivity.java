package com.example.rustie.opencv_app;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnTouchListener {
    static {
        System.loadLibrary("opencv_java");
        System.loadLibrary("nonfree");
    }
    private ImageView imageView;
    private Bitmap inputImage; // make bitmap from image resource
    private FeatureDetector detector = FeatureDetector.create(FeatureDetector.SIFT);

    private CameraBridgeViewBase mOpenCvCameraView;


    public BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    //System.loadLibrary("detection_based_tracker"); // load library after initialize OpenCV
                    mOpenCvCameraView.setMaxFrameSize(853, 480); // increases frame rate. change this
                    mOpenCvCameraView.disableFpsMeter();
                    mOpenCvCameraView.enableView();
                } break;

                default: {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);




        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.SurfaceView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        /* inputImage = BitmapFactory.decodeResource(getResources(), R.drawable.test2);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) this.findViewById(R.id.imageView);
        sift(); */

        /*Mat frame = onCameraFrame();

        // CONVERTS MAT TO BMP; GETS MAT (frame) from onCameraFrame()
        Bitmap bmp = null;
        int height = 470;
        int width = 853;
        Mat tmp = new Mat (height, width, CvType.CV_8U, new Scalar(4));
        try {
            //Imgproc.cvtColor(seedsImage, tmp, Imgproc.COLOR_RGB2BGRA);
            Imgproc.cvtColor(frame, tmp, Imgproc.COLOR_GRAY2RGBA, 4);
            bmp = Bitmap.createBitmap(tmp.cols(), tmp.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(tmp, bmp);
        }
        catch (CvException e){
            Log.d("Exception",e.getMessage());} */


    }

    boolean touched=false;

    public boolean onTouch(View v, MotionEvent event) {
        touched = true;
        return true;
    }


    @Override
    public void onResume() {
        super.onResume();
        // by:  Caused by: java.lang.IllegalArgumentException: Service Intent must be explicit: Intent { act=org.opencv.engine.BIND }
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, this, mLoaderCallback);
    }



    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }


    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        return inputFrame.rgba();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();
        if(hasFocus) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }



    public void sift() {
        Mat rgba = new Mat();
        Utils.bitmapToMat(inputImage, rgba); // convert image bitmap to mat

        MatOfKeyPoint keyPoints = new MatOfKeyPoint();
        Imgproc.cvtColor(rgba, rgba, Imgproc.COLOR_RGBA2GRAY);
        detector.detect(rgba, keyPoints);

        Features2d.drawKeypoints(rgba, keyPoints, rgba);
        Utils.matToBitmap(rgba, inputImage); // convert mat back to bitmap

        imageView.setImageBitmap(inputImage); // change the layout imageView
    }


}