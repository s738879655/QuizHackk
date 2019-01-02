package com.hostelmanager.quizhackk;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.hostelmanager.quizhackk.databinding.BubbleLayoutBinding;
import com.hostelmanager.quizhackk.databinding.ClipLayoutBinding;
import com.hostelmanager.quizhackk.databinding.TrashLayoutBinding;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.hostelmanager.quizhackk.MainActivity.sMediaProjection;
import static com.hostelmanager.quizhackk.MainActivity.engine;
import static com.hostelmanager.quizhackk.MainActivity.option;
import static com.hostelmanager.quizhackk.MainActivity.isChecked;
import static com.hostelmanager.quizhackk.MainActivity.isChecked1;

import static com.hostelmanager.quizhackk.MainActivity.checkbox3;
import static com.hostelmanager.quizhackk.MainActivity.checkbox4;


public class BubbleService extends Service {
    private WindowManager mWindowManager;
    private BubbleLayoutBinding mBubbleLayoutBinding;
    private WindowManager.LayoutParams mBubbleLayoutParams;
    private TrashLayoutBinding mTrashLayoutBinding;
    private WindowManager.LayoutParams mTrashLayoutParams;
    private ClipLayoutBinding mClipLayoutBinding;
    private int[] closeRegion = null;//left, top, right, bottom
    private boolean isClipMode;
    private ImageReader imageReader;

    private VirtualDisplay virtualDisplay;
    int screenWidth, screenHeight, diff;
    ProgressBar progressBar;
    ProgressBar progressBarClone;
    ViewGroup.LayoutParams webParam1;
    ViewGroup.LayoutParams webParam2;
    ViewGroup.LayoutParams webParam3;
    ViewGroup.LayoutParams webParam4;
    ViewGroup.LayoutParams webContainerParam;
    ViewGroup.LayoutParams buttonContainerParam;

    LinearLayout parentContainer;
    LinearLayout webContainer;
    LinearLayout buttonContainer;
    float clipBox[]=new float[4];
    float clipBox1[]=new float[4];
    int clipRegion2[]=new int[4];


    private View layout;
    private View layout1;
    Button button;
    String newUrl = "";
    Button button2;
    Button button1;
    Button button3;
    Button buttonClone;
    Button button2Clone;
    Button button1Clone;
    Button button3Clone;
    TextView textQuest;
    TextView textQuestClone;
    int flag=0;
    WebView webView;
    WebView webView1;
    WebView webView2;
    WebView webView3;

    WebView webViewClone;
    WebView webView1Clone;
    WebView webView2Clone;
    WebView webView3Clone;
    List<String> arr;
    String ques="";
    boolean optionFour=false;
    boolean optionDual=false;

    int size,first=230,second=220;
    boolean forA, forB, forC,forD;
    boolean forAClone, forBClone, forCClone,forDClone;

    int again=0;
    int bg;
    int green;
    int pink;
    int optionA;
    int optionB;
    int optionC;
    int optionD;
    String optiona;
    String optionb;
    String optionc;
    String optiond;

    String find1;
    String find2;
    String find3;
    String find4;

    int optionAClone;
    int optionBClone;
    int optionCClone;
    int optionDClone;



    int DURATION=1300;
    Thread t,t1,t2,t3,tClone,t1Clone,t2Clone,t3Clone;
    TextView msg;
    Context context;
    private  boolean running=false;

    String second1;
    String second2;
    String second3;



    private ImageView cross;
    private ImageView plus;
    private ImageView minus;

    private ImageView crossClone;
    private ImageView plusClone;
    private ImageView minusClone;
    private View mFloatingView;
 //   private static final String DATA_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Quiz Assist/";

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("kanna", "onStart");
        if (sMediaProjection != null) {
            Log.d("kanna", "mediaProjection alive");
        }
        initial();


        return super.onStartCommand(intent, flags, startId);
    }

    private void initial() {
        Log.d("kanna", "initial");

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        mTrashLayoutBinding = TrashLayoutBinding.inflate(layoutInflater);
        if (mTrashLayoutParams == null) {
            mTrashLayoutParams = buildLayoutParamsForBubble(0, 0);
            mTrashLayoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        }
        getWindowManager().addView(mTrashLayoutBinding.getRoot(), mTrashLayoutParams);
        mTrashLayoutBinding.getRoot().setVisibility(View.GONE);

        mBubbleLayoutBinding = BubbleLayoutBinding.inflate(layoutInflater);
        if (mBubbleLayoutParams == null) {
            mBubbleLayoutParams = buildLayoutParamsForBubble(60, 60);
        }
        mBubbleLayoutBinding.setHandler(new BubbleHandler(this));
        getWindowManager().addView(mBubbleLayoutBinding.getRoot(), mBubbleLayoutParams);
    }

    private WindowManager getWindowManager() {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB_MR2)
    public void checkInCloseRegion(float x, float y) {
        if (closeRegion == null) {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;

            x = width;

            int location[] = new int[2];
            View v = mTrashLayoutBinding.getRoot();
            v.getLocationOnScreen(location);
            closeRegion = new int[]{location[0], location[1],
                    location[0] + v.getWidth(), location[1] + v.getHeight()};
        }

        if (Float.compare(x, closeRegion[0]) >= 0 &&
                Float.compare(y, closeRegion[1]) >= 0 &&
                Float.compare(x, closeRegion[2]) <= 0 &&
                Float.compare(3, closeRegion[3]) <= 0) {
            stopSelf();
        } else {
            mTrashLayoutBinding.getRoot().setVisibility(View.GONE);
        }
    }

    public void updateViewLayout(View view, WindowManager.LayoutParams params) {
        mTrashLayoutBinding.getRoot().setVisibility(View.VISIBLE);
        getWindowManager().updateViewLayout(view, params);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    public void startClipMode() {
        destroyWebview();
        mBubbleLayoutBinding.getRoot().setVisibility(View.GONE);
        running =false;

        deleteCache(context);
        destroyWebview();

        webView.setVisibility(View.GONE);
        webView1.setVisibility(View.GONE);
        webView2.setVisibility(View.GONE);
        webView3.setVisibility(View.GONE);



         if(checkbox3.isChecked())
             optionFour=true;
         else
             optionFour=false;
         if(checkbox4.isChecked())
             optionDual=true;
         else
             optionDual=false;


        //   initialiseWebview();
        //      webView.loadUrl("about:blank");
        //    webView1.loadUrl("about:blank");
        //     webView2.loadUrl("about:blank");
        //   webView1.loadUrl("https://www.google.com/");
        //   webView2.loadUrl("https://www.google.com/");
        mTrashLayoutBinding.getRoot().setVisibility(View.GONE);

        // Thread.currentThread().interrupt();

        initialised();


        if (mClipLayoutBinding == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            mClipLayoutBinding = ClipLayoutBinding.inflate(layoutInflater);
            mClipLayoutBinding.setHandler(new ClipHandler(this));
        }
        WindowManager.LayoutParams mClipLayoutParams = buildLayoutParamsForClip();
        ((ClipView) mClipLayoutBinding.getRoot()).updateRegion(clipBox[0], clipBox[1], clipBox[2], clipBox[3]);

        if(!(layout.getVisibility()== View.GONE&&layout1.getVisibility()== View.GONE)) {
            layout.setVisibility(View.GONE);

            layout1.setVisibility(View.GONE);
        }
        // getWindowManager().removeView(layout);
        if(!isClipMode)
            getWindowManager().addView(mClipLayoutBinding.getRoot(), mClipLayoutParams);

        isClipMode = true;


        final Toast toast= Toast.makeText(this, "Select Region Containing Complete Question ." , Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP,0,0);
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, DURATION);
        //  Toast.makeText(this,"Start clip mode.",Toast.LENGTH_SHORT).show();


    }


    public void finishClipMode(int[] clipRegion,float[] clipRegion1) {
        isClipMode = false;





        //   layout.setVisibility(View.VISIBLE);
     /*   final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.RIGHT;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(layout, params);*/


        getWindowManager().removeView(mClipLayoutBinding.getRoot());
        if (clipRegion[2] < 50 || clipRegion[3] < 50) {
           // Toast.makeText(this, "Region is too small.", Toast.LENGTH_SHORT).show();
           // mBubbleLayoutBinding.getRoot().setVisibility(View.VISIBLE);
            // layout.setVisibility(View.VISIBLE);

            clipRegion2[2]=(int) Math.ceil(clipBox1[2]-clipBox1[0]);
            clipRegion2[3]=(int) Math.ceil(clipBox1[3]-clipBox1[1]);
            clipRegion2[0]=(int) Math.ceil(clipBox1[0]);
            clipRegion2[1]=(int) Math.ceil(clipBox1[1]);
            screenshot(clipRegion2);
        } else {
            clipBox1=clipBox.clone();
            clipBox=clipRegion1.clone();
            screenshot(clipRegion);
        }
    }

    public void screenshot(int[] clipRegion) {
        if (sMediaProjection != null) {


            shotScreen(clipRegion);
        } else {
            Toast.makeText(this,
                    "Re-Open the App by closing this one .", Toast.LENGTH_LONG).show();
            stopSelf();
        }
    }

    @SuppressLint("CheckResult")
    private void shotScreen(int[] clipRegion) {
        mBubbleLayoutBinding.getRoot().setVisibility(View.VISIBLE);
        getScreenShot()
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .map(image -> createBitmap(image, clipRegion))
                .doFinally(() -> {
                    Log.d("kanna", "do finally: " + Thread.currentThread().toString());
                    finalRelease();
                  //  mBubbleLayoutBinding.getRoot().setVisibility(View.VISIBLE);
                })
                .subscribe(
                        fileName -> {
                            Log.d("kanna", "onSuccess: " + fileName);

                        },
                        throwable -> {
                            Log.d("kanna", "onError: ", throwable);

                        }
                );

    }

    private void finalRelease() {
        if (virtualDisplay != null) {
            virtualDisplay.release();
            virtualDisplay = null;
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (isClipMode) {
            isClipMode = false;
            getWindowManager().removeView(mClipLayoutBinding.getRoot());
            Toast.makeText(this, "Configuration changed, stop clip mode.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        if (mWindowManager != null) {
            if (mBubbleLayoutBinding != null) {
                mWindowManager.removeView(mBubbleLayoutBinding.getRoot());
            }
            if (mTrashLayoutBinding != null) {
                mWindowManager.removeView(mTrashLayoutBinding.getRoot());
            }
            if (layout != null) {
                mWindowManager.removeView(layout);
            } if (layout1 != null) {
                mWindowManager.removeView(layout1);
            }
            if (mClipLayoutBinding != null) {
                if (isClipMode) {
                    mWindowManager.removeView(mClipLayoutBinding.getRoot());
                }
            }
            mWindowManager = null;
        }
        if (sMediaProjection != null) {
            sMediaProjection.stop();
            sMediaProjection = null;
        }
        super.onDestroy();
    }

    private WindowManager.LayoutParams buildLayoutParamsForBubble(int x, int y) {
        WindowManager.LayoutParams params;
        if (Build.VERSION.SDK_INT >= 26) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= 23) {
            //noinspection deprecation
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSPARENT);
        } else {
            //noinspection deprecation
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_TOAST,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSPARENT);
        }
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = x;
        params.y = y;
        return params;
    }

    private WindowManager.LayoutParams buildLayoutParamsForClip() {
        WindowManager.LayoutParams params;
        if (Build.VERSION.SDK_INT <= 22) {
            //noinspection deprecation
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_TOAST,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                            WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    PixelFormat.TRANSPARENT);
        } else {
            Display display = getWindowManager().getDefaultDisplay();
            /*
            The real size may be smaller than the physical size of the screen
            when the window manager is emulating a smaller display (using adb shell wm size).
             */
            Point sizeReal = new Point();
            display.getRealSize(sizeReal);
            /*
            If requested from activity
            (either using getWindowManager() or (WindowManager) getSystemService(Context.WINDOW_SERVICE))
            resulting size will correspond to current app window size.
            In this case it can be smaller than physical size in multi-window mode.
             */
            Point size = new Point();
            display.getSize(size);

            if (size.x == sizeReal.x) {
                diff = sizeReal.y - size.y;
            } else {
                diff = sizeReal.x - size.x;
            }
            screenWidth = sizeReal.x + diff;
            screenHeight = sizeReal.y + diff;

            Log.d("kanna", "get screen " + screenWidth + " " + screenHeight
                    + " " + sizeReal.x + " " + size.x
                    + " " + sizeReal.y + " " + size.y);
            if (Build.VERSION.SDK_INT >= 26) {
                params = new WindowManager.LayoutParams(
                        screenWidth,
                        screenHeight,
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                        PixelFormat.TRANSPARENT);
            } else {
                //noinspection deprecation
                params = new WindowManager.LayoutParams(
                        screenWidth,
                        screenHeight,
                        WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                        PixelFormat.TRANSPARENT);
            }

        }
        return params;
    }

    //https://stackoverflow.com/questions/14341041/how-to-get-real-screen-height-and-width
    private Single<Image> getScreenShot()
    {

        final Point screenSize = new Point();
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay();
        display.getRealSize(screenSize);
        return Single.create(emitter -> {
            imageReader = ImageReader.newInstance(screenSize.x, screenSize.y,
                    PixelFormat.RGBA_8888, 2);
            virtualDisplay = sMediaProjection.createVirtualDisplay("cap", screenSize.x, screenSize.y,
                    metrics.densityDpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    imageReader.getSurface(), null, null);
            ImageReader.OnImageAvailableListener mImageListener =
                    new ImageReader.OnImageAvailableListener() {
                        Image image = null;

                        @Override
                        public void onImageAvailable(ImageReader imageReader) {
                            try {
                                image = imageReader.acquireLatestImage();
                                Log.d("kanna", "reader: " + Thread.currentThread().toString());
                                if (image != null)
                                {
                                    layout.setVisibility(View.VISIBLE);
                                  if(optionDual)
                                    layout1.setVisibility(View.VISIBLE);

                                    running=true;
                                    emitter.onSuccess(image);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                emitter.onError(new Throwable("ImageReader error"));
                            }
                            imageReader.setOnImageAvailableListener(null, null);
                        }

                    };
            imageReader.setOnImageAvailableListener(mImageListener, null);
        });
    }

    /*private Single<String> updateScan(final String fileName) {
        return Single.create(emitter -> {
            String[] path = new String[]{fileName};
            MediaScannerConnection.scanFile(this, path, null, (s, uri) -> {
                Log.d("kanna", "scan file: " + Thread.currentThread().toString());
                if (uri == null) {
                    emitter.onError(new Throwable("Scan fail" + s));
                } else {
                    emitter.onSuccess(s);
                }
            });
        });
    }*/

    private Bitmap createBitmap(Image image, int[] clipRegion) {
        Log.d("kanna", "check create bitmap: " + Thread.currentThread().toString());
        Bitmap bitmap, bitmapCut;
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * image.getWidth();
        // create bitmap
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        bitmap = Bitmap.createBitmap(image.getWidth() + rowPadding / pixelStride,
                image.getHeight(), Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);

        bitmapCut = Bitmap.createBitmap(bitmap,
                clipRegion[0], clipRegion[1], width - clipRegion[0], clipRegion[3]);
        // detectImage(bitmapCut);
        bitmap.recycle();
        image.close();

       // extractText(bitmapCut);
        extractFromVision(bitmapCut);

        //extractFireBaseText(bitmapCut);

        return bitmapCut;
    }

    /*
        private void writeFile(Bitmap bitmap, String fileName) throws IOException {
            Log.d("kanna", "check write file: " + Thread.currentThread().toString());
            FileOutputStream fos = new FileOutputStream(fileName);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            bitmap.recycle();
        }

        private Single<String> createFile() {
            return Single.create((SingleOnSubscribe<String>) emitter -> {
                Log.d("kanna", "check create filename: " + Thread.currentThread().toString());

                int count = 0;
                File externalFilesDir = Environment.
                        getExternalStoragePublicDirectory(DIRECTORY_PICTURES);
                if (externalFilesDir != null) {
                    directory = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES)
                            .getAbsolutePath() + "/screenshots/";

                    Log.d("kanna", directory);
                    File storeDirectory = new File(directory);
                    if (!storeDirectory.exists()) {
                        boolean success = storeDirectory.mkdirs();
                        if (!success) {
                            emitter.onError(new Throwable("failed to create file storage directory."));
                            return;
                        }
                    }
                } else {
                    emitter.onError(new Throwable("failed to create file storage directory," +
                            " getExternalFilesDir is null."));
                    return;
                }

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd",
                        Locale.ENGLISH);
                Calendar c = Calendar.getInstance();
                fileHead = simpleDateFormat.format(c.getTime()) + "_";
                fileName1 = directory + fileHead + count + ".png";
                File storeFile = new File(fileName1);
                while (storeFile.exists()) {
                    count++;
                    fileName1 = directory + fileHead + count + ".png";
                    storeFile = new File(fileName1);
                }
                emitter.onSuccess(fileName1);
            }).subscribeOn(Schedulers.io());
        }

        private void extractFireBaseText(Bitmap bitmap)
        {
            FirebaseVisionImage visionImage= FirebaseVisionImage.fromBitmap(bitmap);
            Log.d("zxcvvv","Enetered");
            FirebaseVisionTextDetector detector= FirebaseVision.getInstance().getVisionTextDetector();
            detector.detectInImage(visionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                @Override
                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                      //  process(firebaseVisionText);
                        Log.d("zxc","Calling process");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, e+"", Toast.LENGTH_SHORT).show();
                }
            });
        }
    private void process(FirebaseVisionText txt)
    {

        List<FirebaseVisionText.Block> blocks=txt.getBlocks();
        if(blocks.size()==0)
        {
            Toast.makeText(context, "There is no Text", Toast.LENGTH_SHORT).show();
            return;
        }

        for (FirebaseVisionText.Block block: txt.getBlocks()) {

            String text = block.getText();
            Log.d("zx","Calling find Ans");
            findAns(text);

            for (FirebaseVisionText.Line line: block.getLines()) {
                // ...
                for (FirebaseVisionText.Element element: line.getElements()) {
                    // ...
                }
            }
        }

    }
    */



    private void extractFromVision(Bitmap bitmap)
    {
        TextRecognizer textRecognizer=new TextRecognizer.Builder(getApplicationContext()).build();

        if(!textRecognizer.isOperational())
        {
            Toast.makeText(context, "Could not read text", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Frame frame=new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> items=textRecognizer.detect(frame);

            if(items.size()!=0)
            {


                         List<Text> textLines = new ArrayList<>();
                        StringBuilder string=new StringBuilder();
                        for(int i=0;i<items.size();++i)

                        {
                            TextBlock textBlock = items.valueAt(i);
                            List<? extends Text> textComponents = textBlock.getComponents();
                            textLines.addAll(textComponents);
                        }


                Collections.sort(textLines, (t1, t2) -> {
                    int diffOfTops = t1.getBoundingBox().top -  t2.getBoundingBox().top;
                    int diffOfLefts = t1.getBoundingBox().left - t2.getBoundingBox().left;

                    if (diffOfTops != 0) {
                        return diffOfTops;
                    }
                    return diffOfLefts;
                });

                StringBuilder textBuilder = new StringBuilder();
                for (Text text : textLines) {

                        textBuilder.append(text.getValue()+" ").append("\n");
                }
                findAns(textBuilder.toString());

                         /*   TextBlock textBlock=items.valueAt(i);


                            Log.d("length",textBlock.getValue().length()+"");


                            string.append(textBlock.getValue());
                            string.append("\n");
                        }


*/
            }
        }

    }
  /*  private void extractText(Bitmap bitmap) {

        try {
            tessBaseAPI = new TessBaseAPI();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            if (tessBaseAPI == null) {
                Log.e(TAG, "TessBaseAPI is null. TessFactory not returning tess object.");
            }
        }
        tessBaseAPI.init(DATA_PATH, "eng");
        Log.d("hello", "Training file loaded");
        tessBaseAPI.setImage(bitmap);
        String retStr = "";
        try {
            retStr = tessBaseAPI.getUTF8Text();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        tessBaseAPI.end();
        Log.d("vik", retStr);

        findAns(retStr);


    }
*/


    @SuppressLint({"JavascriptInterface", "SetTextI18n"})
    private void findAns(String urlComing)

    {
        Log.d("deqwert", "Enetred findAns");
        //  button.setText("hello");

        switch (engine) {
            case "Google":
                newUrl = "https://www.google.co.in/search?q=";
                break;
            case "Bing":
                newUrl = "https://www.bing.com/search?q=";
                break;
            case "DuckDuckGo":
                newUrl = "https://duckduckgo.com/?q=";
                break;
            case "Ask.com":
                newUrl = "https://www.ask.com/web?q=";
                break;

            case "Aol.":
                newUrl = "https://search.aol.com/aol/search;?q=";
                break;
            case "Excite.com":
                newUrl = "http://msxml.excite.com/search/web?q=";
                break;
            case "Web Crawler":
                newUrl = "http://www.webcrawler.com/serp?q=";
                break;

            default:
                newUrl = "https://www.google.co.in/search?q=";
                break;
        }



        String url = "";

        url = urlComing;

        assert url != null;
        arr = Arrays.asList(url.split("\n"));



        //    newUrl = "https://www.wolframalpha.com/input/?i=";
        size = arr.size();

        ques = "";
        if(!optionFour)
            for (int i = 0; i < size - 3; i++) {

                ques += arr.get(i);

            }
        else
            for (int i = 0; i < size - 4; i++) {

                ques += arr.get(i);

            }

        if(ques.contains(" not ")||ques.contains(" never ")||ques.contains(" didnt ")||ques.contains(" NOT "))
        {
            flag=1;
        }
        Log.d("que", newUrl);

        ques = ques.replaceAll("[^a-zA-Z0-9.'?\" \"-]", "");
        String newq=modifyString(ques);
        String finalNewUrl = newUrl + newq ;


        if(running==true) {
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    // Insert some method call here.

                    Log.d("deqwer", urlComing);


                    Handler mainHandler = new Handler(Looper.getMainLooper());

                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {

                            showQuest(ques);
                            webView.loadUrl(finalNewUrl);
                            webView1.loadUrl(finalNewUrl);
                            webView2.loadUrl(finalNewUrl);


                            if (optionFour) {
                                webView3.loadUrl(finalNewUrl);

                            }
                            progressBar.setMax(100);

                            progressBar.setProgress(1);


                            webView.setWebChromeClient(new WebChromeClient() {
                                public void onProgressChanged(WebView view, int progress) {
                                    progressBar.setProgress(progress);
                                    if (progress == 100) {
                                        progressBar.setVisibility(View.GONE);

                                    } else {

                                        progressBar.setVisibility(View.VISIBLE);

                                    }
                                }
                            });


                            Log.d("qwerr", "Enetrere in webview");
                            //   webView.getSettings().setJavaScriptEnabled(true);

                            webView.setWebViewClient(new WebViewClient() {
                                @Override
                                public void onPageFinished(WebView view, String url) {

                                    Log.d("jhdhdc", "Entered int the On page Finished");
                                    forA = true;

                                    webView.setVisibility(View.VISIBLE);
                                    //  running = true;
                                    findInWeb1();
                                    super.onPageFinished(view, url);
                                }

                                @Override
                                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                    //  Log.i("shouldOverrideUrlLoading", url.toString());
                                    return super.shouldOverrideUrlLoading(view, url);
                                }

                                @Override
                                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                    if (isChecked1)
                                        showWeb1();
                                    else
                                        showWebType1();

                                    super.onPageStarted(view, url, favicon);
                                }
                            });

                        } // This is your code
                    };
                    mainHandler.post(myRunnable);



                }
            });

            t.start();

            t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    // Insert some method call here.

                    Log.d("deqwer", urlComing);


                    Handler mainHandler1 = new Handler(Looper.getMainLooper());

                    Runnable myRunnable1 = new Runnable() {
                        @Override
                        public void run() {


                            //    webView1.getSettings().setJavaScriptEnabled(true);
                            webView1.setWebViewClient(new WebViewClient() {

                                @Override
                                public void onPageFinished(WebView view, String url) {
                                    Log.i("onPageFinished", url);
                                    forB = true;

                                    webView1.setVisibility(View.VISIBLE);
                                    //    running = true;

                                    findInWeb2();
                                    super.onPageFinished(view, url);
                                }

                                @Override
                                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                    //  Log.i("shouldOverrideUrlLoading", url.toString());
                                    return super.shouldOverrideUrlLoading(view, url);
                                }

                                @Override
                                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                    if (isChecked1)
                                        showWeb2();
                                    else
                                        showWebType2();
                                    super.onPageStarted(view, url, favicon);
                                }
                            });


                            webView.setFindListener(new WebView.FindListener() {
                                @Override
                                public void onFindResultReceived(int i, int i1, boolean b) {
                                    Log.d("msg0", i1 + "");
                                    Log.d("qaws", b + "");

                                    //  forA = b;
                                    if (running == true) {

                                        if (i1 > optionA) {
                                            webView.findNext(true);
                                            webView1.findNext(true);
                                            webView2.findNext(true);
                                            optionA = i1;
                                            //   Toast.makeText(context, i1+"", Toast.LENGTH_SHORT).show();
                                        }


                                        Log.d("qwe", "InCounting");
                                        showAnswer();
                                        Log.d("msg", "cdfds");

                                    }
                                }
                            });


                            webView1.setFindListener(new WebView.FindListener() {
                                @Override
                                public void onFindResultReceived(int i, int i1, boolean b) {
                                    Log.d("msg1", i1 + "");
                                    if (running == true) {
                                        if (i1 > optionB) {
                                            webView.findNext(true);
                                            webView1.findNext(true);
                                            webView2.findNext(true);
                                            optionB = i1;
                                        } // forB = b;
                                        showAnswer();
                                        Log.d("msg", "c");
                                    }

                                }
                            });

                        } // This is your code
                    };
                    mainHandler1.post(myRunnable1);



                }
            });
            t1.start();

            t2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    // Insert some method call here.

                    Log.d("deqwer", urlComing);


                    Handler mainHandler2 = new Handler(Looper.getMainLooper());

                    Runnable myRunnable2 = new Runnable() {
                        @Override
                        public void run() {

            webView2.setFindListener(new WebView.FindListener() {
                                @Override
                                public void onFindResultReceived(int i, int i1, boolean b) {
                                    if (running == true) {
                                        Log.d("msg2", i1 + "");
                                        if (i1 > optionC) {
                                            webView.findNext(true);
                                            webView1.findNext(true);
                                            webView2.findNext(true);
                                            optionC = i1;
                                        }

                                        Log.d("msg", "cjj");

                                        showAnswer();
                                    }
                                }
                            });

                            //forC = b;


                            //       webView2.getSettings().setJavaScriptEnabled(true);
                            webView2.setWebViewClient(new WebViewClient() {


                                @Override
                                public void onPageFinished(WebView view, String url) {
                                    Log.i("onPageFinished", url);
                                    forC = true;
                                    //    running = true;
                                    findInWeb3();
                                    webView2.setVisibility(View.VISIBLE);
                                    super.onPageFinished(view, url);
                                }

                                @Override
                                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                    //  Log.i("shouldOverrideUrlLoading", url.toString());
                                    return super.shouldOverrideUrlLoading(view, url);
                                }

                                @Override
                                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                    if (isChecked1)
                                        showWeb3();
                                    else
                                        showWebType3();
                                    super.onPageStarted(view, url, favicon);
                                }
                            });


                        } // This is your code
                    };
                    mainHandler2.post(myRunnable2);



                }
            });
            t2.start();

            t3 = new Thread(new Runnable() {
                @Override
                public void run() {
                    // Insert some method call here.

                    Log.d("deqwer", urlComing);


                    Handler mainHandler3 = new Handler(Looper.getMainLooper());

                    Runnable myRunnable3 = new Runnable() {
                        @Override
                        public void run() {

                            if (optionFour) {



                                button3.setVisibility(View.VISIBLE);
                                button3Clone.setVisibility(View.VISIBLE);
                                webView3.setFindListener(new WebView.FindListener() {
                                    @Override
                                    public void onFindResultReceived(int i, int i1, boolean b) {
                                        if (running == true) {
                                            Log.d("msg2", i1 + "");
                                            if (i1 > optionD) {
                                                webView.findNext(true);
                                                webView1.findNext(true);
                                                webView2.findNext(true);
                                                webView3.findNext(true);
                                                optionD = i1;
                                            }

                                            Log.d("msg", "cjj");

                                            showAnswer();
                                        }
                                    }
                                });

                                //forC = b;


                                //       webView2.getSettings().setJavaScriptEnabled(true);
                                webView3.setWebViewClient(new WebViewClient() {


                                    @Override
                                    public void onPageFinished(WebView view, String url) {
                                        Log.i("onPageFinished", url);
                                        forD = true;
                                        //    running = true;
                                        findInWeb4();
                                        webView3.setVisibility(View.VISIBLE);
                                        super.onPageFinished(view, url);
                                    }

                                    @Override
                                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                        //  Log.i("shouldOverrideUrlLoading", url.toString());
                                        return super.shouldOverrideUrlLoading(view, url);
                                    }

                                    @Override
                                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                        if (isChecked1)
                                            showWeb4();
                                        else
                                            showWebType4();
                                        super.onPageStarted(view, url, favicon);
                                    }
                                });


                            }


                        } // This is your code
                    };
                    mainHandler3.post(myRunnable3);



                }
            });
            t3.start();




            if (optionDual) {
                t1Clone = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Handler mainHandler1 = new Handler(Looper.getMainLooper());

                        Runnable myRunnable1 = new Runnable() {
                            @Override
                            public void run() {

                                showQuest(ques);


                                webViewClone.loadUrl("https://www.google.co.in/search?q=" + newq);
                                webView1Clone.loadUrl("https://www.google.co.in/search?q=" + newq);
                                webView2Clone.loadUrl("https://www.google.co.in/search?q=" + newq);

                                if (optionFour) {

                                    webView3Clone.loadUrl("https://www.google.co.in/search?q=" + newq);
                                }

                                progressBarClone.setMax(100);

                                progressBarClone.setProgress(1);

                                webViewClone.setWebChromeClient(new WebChromeClient() {
                                    public void onProgressChanged(WebView view, int progress) {
                                        progressBarClone.setProgress(progress);
                                        if (progress == 100) {
                                            progressBarClone.setVisibility(View.GONE);

                                        } else {

                                            progressBarClone.setVisibility(View.VISIBLE);

                                        }
                                    }
                                });


                                Log.d("qwerr", "Enetrere in webview");
                                //   webView.getSettings().setJavaScriptEnabled(true);


                                webViewClone.setWebViewClient(new WebViewClient() {
                                    @Override
                                    public void onPageFinished(WebView view, String url) {

                                        Log.d("jhdhdc", "Entered int the On page Finished");
                                        forAClone = true;

                                        //  webViewClone.setVisibility(View.VISIBLE);
                                        //  running = true;
                                        findInWeb1Clone();
                                        super.onPageFinished(view, url);
                                    }

                                    @Override
                                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                        //  Log.i("shouldOverrideUrlLoading", url.toString());
                                        return super.shouldOverrideUrlLoading(view, url);
                                    }

                                    @Override
                                    public void onPageStarted(WebView view, String url, Bitmap favicon) {

                                        super.onPageStarted(view, url, favicon);
                                    }
                                });

                            } // This is your code
                        };
                        mainHandler1.post(myRunnable1);
                    }
                });
                t1Clone.start();

                t2Clone = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Handler mainHandler1 = new Handler(Looper.getMainLooper());

                        Runnable myRunnable1 = new Runnable() {
                            @Override
                            public void run() {

                                //    webView1.getSettings().setJavaScriptEnabled(true);

                                webView1Clone.setWebViewClient(new WebViewClient() {

                                    @Override
                                    public void onPageFinished(WebView view, String url) {
                                        Log.i("onPageFinished", url);
                                        forBClone = true;

                                        //      webView1.setVisibility(View.VISIBLE);
                                        //    running = true;

                                        findInWeb2Clone();
                                        super.onPageFinished(view, url);
                                    }

                                    @Override
                                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                        //  Log.i("shouldOverrideUrlLoading", url.toString());
                                        return super.shouldOverrideUrlLoading(view, url);
                                    }

                                    @Override
                                    public void onPageStarted(WebView view, String url, Bitmap favicon) {

                                        super.onPageStarted(view, url, favicon);
                                    }
                                });


                                webViewClone.setFindListener(new WebView.FindListener() {
                                    @Override
                                    public void onFindResultReceived(int i, int i1, boolean b) {
                                        Log.d("msg0", i1 + "");
                                        Log.d("qaws", b + "");

                                        //  forA = b;
                                        if (running == true) {

                                            if (i1 > optionAClone) {
                                                //  webView.findNext(true);
                                                // webView1.findNext(true);
                                                //   webView2.findNext(true);
                                                optionAClone = i1;
                                                //   Toast.makeText(context, i1+"", Toast.LENGTH_SHORT).show();
                                            }


                                            Log.d("qwe", "InCounting");
                                            showAnswerClone();
                                            Log.d("msg", "cdfds");

                                        }
                                    }
                                });


                                webView1Clone.setFindListener(new WebView.FindListener() {
                                    @Override
                                    public void onFindResultReceived(int i, int i1, boolean b) {
                                        Log.d("msg1", i1 + "");
                                        if (running == true) {
                                            if (i1 > optionBClone) {
                                                // webView.findNext(true);
                                                //  webView1.findNext(true);
                                                //  webView2.findNext(true);
                                                optionBClone = i1;
                                            } // forB = b;
                                            showAnswerClone();
                                            Log.d("msg", "c");
                                        }

                                    }
                                });

                            } // This is your code
                        };
                        mainHandler1.post(myRunnable1);
                    }
                });
                t2Clone.start();

                t3Clone = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Handler mainHandler1 = new Handler(Looper.getMainLooper());

                        Runnable myRunnable1 = new Runnable() {
                            @Override
                            public void run() {

                                webView2Clone.setFindListener(new WebView.FindListener() {
                                    @Override
                                    public void onFindResultReceived(int i, int i1, boolean b) {
                                        if (running == true) {
                                            Log.d("msg2", i1 + "");
                                            if (i1 > optionCClone) {
                                                //webView.findNext(true);
                                                // webView1.findNext(true);
                                                // webView2.findNext(true);
                                                optionCClone = i1;
                                            }

                                            Log.d("msg", "cjj");

                                            showAnswerClone();
                                        }
                                    }
                                });
                                //forC = b;


                                //       webView2.getSettings().setJavaScriptEnabled(true);

                                webView2Clone.setWebViewClient(new WebViewClient() {


                                    @Override
                                    public void onPageFinished(WebView view, String url) {
                                        Log.i("onPageFinished", url);
                                        forCClone = true;
                                        //    running = true;
                                        findInWeb3Clone();
                                        //   webView2.setVisibility(View.VISIBLE);
                                        super.onPageFinished(view, url);
                                    }

                                    @Override
                                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                        //  Log.i("shouldOverrideUrlLoading", url.toString());
                                        return super.shouldOverrideUrlLoading(view, url);
                                    }

                                    @Override
                                    public void onPageStarted(WebView view, String url, Bitmap favicon) {

                                        super.onPageStarted(view, url, favicon);
                                    }
                                });
                            } // This is your code
                        };
                        mainHandler1.post(myRunnable1);
                    }
                });
                tClone.start();

                tClone = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Handler mainHandler1 = new Handler(Looper.getMainLooper());

                        Runnable myRunnable1 = new Runnable() {
                            @Override
                            public void run() {
                                if (optionFour) {
                                    button3.setVisibility(View.VISIBLE);
                                    button3Clone.setVisibility(View.VISIBLE);

                                    webView3Clone.setFindListener(new WebView.FindListener() {
                                        @Override
                                        public void onFindResultReceived(int i, int i1, boolean b) {
                                            if (running == true) {
                                                Log.d("msg2", i1 + "");
                                                if (i1 > optionDClone) {
                                                    //  webView.findNext(true);
                                                    // webView1.findNext(true);
                                                    // webView2.findNext(true);
                                                    // webView3.findNext(true);
                                                    optionDClone = i1;
                                                }

                                                Log.d("msg", "cjj");

                                                showAnswerClone();
                                            }
                                        }
                                    });
                                    //forC = b;


                                    //       webView2.getSettings().setJavaScriptEnabled(true);

                                }


                            } // This is your code
                        };
                        mainHandler1.post(myRunnable1);
                    }
                });

                tClone.start();

            }



        }

    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})


    public void showAnswerClone() {

        if (running == true) {

            Log.d("dfss", optionA + "");
            Log.d("dfssb", optionB + "");
            Log.d("dfssa", optionc + "");

            buttonClone.setText(optionAClone + "  " + optiona);
            button1Clone.setText(optionBClone + "  " + optionb);
            button2Clone.setText(optionCClone + "  " + optionc);
            if(optionFour)
                button3Clone.setText(optionDClone + "  " + optiond);

            if(flag!=1&&!optionFour) {
                if (optionAClone > optionBClone && optionAClone > optionCClone) {
                    buttonClone.setBackgroundColor(green);
                    button1Clone.setBackgroundColor(pink);
                    button2Clone.setBackgroundColor(pink);
                    // showAll();

                } else if (optionBClone > optionAClone && optionBClone > optionCClone) {

                    Log.d("msg", "ch");

                    button1Clone.setBackgroundColor(green);
                    buttonClone.setBackgroundColor(pink);
                    button2Clone.setBackgroundColor(pink);
                    //showAll();

                } else if (optionCClone > optionAClone && optionCClone > optionBClone) {

                    button2Clone.setBackgroundColor(green);
                    buttonClone.setBackgroundColor(pink);
                    button1Clone.setBackgroundColor(pink);

                    // showAll();

                    Log.d("msg", "che");

                }

            }
            else if(flag==1&&!optionFour)
            {
                if (optionAClone < optionBClone && optionAClone < optionCClone) {
                    buttonClone.setBackgroundColor(green);
                    button1Clone.setBackgroundColor(pink);
                    button2Clone.setBackgroundColor(pink);
                    // showAll();


                } else if (optionBClone < optionAClone && optionBClone < optionCClone) {

                    Log.d("msg", "ch");

                    button1Clone.setBackgroundColor(green);
                    buttonClone.setBackgroundColor(pink);
                    button2Clone.setBackgroundColor(pink);

                    //showAll();

                } else if (optionCClone < optionAClone && optionCClone < optionBClone) {

                    button2Clone.setBackgroundColor(green);
                    buttonClone.setBackgroundColor(pink);
                    button1Clone.setBackgroundColor(pink);


                    // showAll();

                    Log.d("msg", "che");


                }
            }
            else if(flag!=1&&optionFour)
            {
                if (optionAClone > optionBClone && optionAClone > optionCClone&&optionAClone>optionDClone) {
                    buttonClone.setBackgroundColor(green);
                    button1Clone.setBackgroundColor(pink);
                    button2Clone.setBackgroundColor(pink);
                    button3Clone.setBackgroundColor(pink);

                    // showAll();

                } else if (optionBClone > optionAClone && optionBClone > optionCClone&&optionBClone>optionDClone) {

                    Log.d("msg", "ch");

                    button1Clone.setBackgroundColor(green);
                    buttonClone.setBackgroundColor(pink);
                    button2Clone.setBackgroundColor(pink);
                    button3Clone.setBackgroundColor(pink);
                    //showAll();

                } else if (optionCClone > optionAClone && optionCClone > optionBClone&&optionCClone>optionDClone) {

                    button2Clone.setBackgroundColor(green);
                    buttonClone.setBackgroundColor(pink);
                    button1Clone.setBackgroundColor(pink);
                    button3Clone.setBackgroundColor(pink);

                    // showAll();


                    Log.d("msg", "che");

                }
                else if (optionDClone > optionAClone && optionDClone > optionBClone && optionDClone>optionCClone) {

                    button3Clone.setBackgroundColor(green);
                    buttonClone.setBackgroundColor(pink);
                    button1Clone.setBackgroundColor(pink);
                    button2Clone.setBackgroundColor(pink);

                    // showAll();


                    Log.d("msg", "che");

                }

            }
            else
            {
                if (optionAClone < optionBClone && optionAClone < optionCClone&&optionAClone<optionDClone) {
                    buttonClone.setBackgroundColor(green);
                    button1Clone.setBackgroundColor(pink);
                    button2Clone.setBackgroundColor(pink);
                    button3Clone.setBackgroundColor(pink);

                    // showAll();

                } else if (optionBClone < optionAClone && optionBClone < optionCClone&&optionBClone<optionDClone) {

                    Log.d("msg", "ch");

                    button1Clone.setBackgroundColor(green);
                    buttonClone.setBackgroundColor(pink);
                    button2Clone.setBackgroundColor(pink);
                    button3Clone.setBackgroundColor(pink);
                    //showAll();

                } else if (optionCClone < optionAClone && optionCClone < optionBClone&&optionCClone<optionDClone) {

                    button2Clone.setBackgroundColor(green);
                    buttonClone.setBackgroundColor(pink);
                    button1Clone.setBackgroundColor(pink);
                    button3Clone.setBackgroundColor(pink);

                    // showAll();


                    Log.d("msg", "che");

                }
                else if (optionD < optionA && optionD < optionB && optionD<optionC) {

                    button3Clone.setBackgroundColor(green);
                    buttonClone.setBackgroundColor(pink);
                    button1Clone.setBackgroundColor(pink);
                    button2Clone.setBackgroundColor(pink);

                    // showAll();

                    Log.d("msg", "che");

                }

            }




        }
    }
    public void showAnswer() {

        if (running == true) {






            //without Clone

            Log.d("dfss", optionA + "");
            Log.d("dfssb", optionB + "");
            Log.d("dfssa", optionc + "");


            button.setText(optionA + "  " + optiona);
            button1.setText(optionB + "  " + optionb);
            button2.setText(optionC + "  " + optionc);
            if(optionFour)
                button3.setText(optionD + "  " + optiond);

if(flag!=1&&!optionFour) {
    if (optionA > optionB && optionA > optionC) {
        button.setBackgroundColor(green);
        button1.setBackgroundColor(pink);
        button2.setBackgroundColor(pink);
        // showAll();
        if (isChecked)
            showWebView1();
        else
            showNothing();

    } else if (optionB > optionA && optionB > optionC) {

        Log.d("msg", "ch");

        button1.setBackgroundColor(green);
        button.setBackgroundColor(pink);
        button2.setBackgroundColor(pink);
        //showAll();
        if (isChecked)
            showWebView2();
        else
            showNothing();
    } else if (optionC > optionA && optionC > optionB) {

        button2.setBackgroundColor(green);
        button.setBackgroundColor(pink);
        button1.setBackgroundColor(pink);

        // showAll();
        if (isChecked)
            showWebView3();
        else
            showNothing();

        Log.d("msg", "che");

    }

}
else if(flag==1&&!optionFour)
{
    if (optionA < optionB && optionA < optionC) {
        button.setBackgroundColor(green);
        button1.setBackgroundColor(pink);
        button2.setBackgroundColor(pink);
        // showAll();
        if (isChecked)
            showWebView1();
        else
            showNothing();

    } else if (optionB < optionA && optionB < optionC) {

        Log.d("msg", "ch");

        button1.setBackgroundColor(green);
        button.setBackgroundColor(pink);
        button2.setBackgroundColor(pink);

        //showAll();
        if (isChecked)
            showWebView2();
        else
            showNothing();
    } else if (optionC < optionA && optionC < optionB) {

        button2.setBackgroundColor(green);
        button.setBackgroundColor(pink);
        button1.setBackgroundColor(pink);


        // showAll();
        if (isChecked)
            showWebView3();
        else
            showNothing();

        Log.d("msg", "che");


    }
}
else if(flag!=1&&optionFour)
{
    if (optionA > optionB && optionA > optionC&&optionA>optionD) {
        button.setBackgroundColor(green);
        button1.setBackgroundColor(pink);
        button2.setBackgroundColor(pink);
        button3.setBackgroundColor(pink);

        // showAll();
        if (isChecked)
            showWebView1();
        else
            showNothing();

    } else if (optionB > optionA && optionB > optionC&&optionB>optionD) {

        Log.d("msg", "ch");

        button1.setBackgroundColor(green);
        button.setBackgroundColor(pink);
        button2.setBackgroundColor(pink);
        button3.setBackgroundColor(pink);
        //showAll();
        if (isChecked)
            showWebView2();
        else
            showNothing();
    } else if (optionC > optionA && optionC > optionB&&optionC>optionD) {

        button2.setBackgroundColor(green);
        button.setBackgroundColor(pink);
        button1.setBackgroundColor(pink);
        button3.setBackgroundColor(pink);

        // showAll();
        if (isChecked)
            showWebView3();
        else
            showNothing();

        Log.d("msg", "che");

    }
    else if (optionD > optionA && optionD > optionB && optionD>optionC) {

        button3.setBackgroundColor(green);
        button.setBackgroundColor(pink);
        button1.setBackgroundColor(pink);
        button2.setBackgroundColor(pink);

        // showAll();
        if (isChecked)
            showWebView4();
        else
            showNothing();

        Log.d("msg", "che");

    }

}
else
{
    if (optionA < optionB && optionA < optionC&&optionA<optionD) {
        button.setBackgroundColor(green);
        button1.setBackgroundColor(pink);
        button2.setBackgroundColor(pink);
        button3.setBackgroundColor(pink);

        // showAll();
        if (isChecked)
            showWebView1();
        else
            showNothing();

    } else if (optionB < optionA && optionB < optionC&&optionB<optionD) {

        Log.d("msg", "ch");

        button1.setBackgroundColor(green);
        button.setBackgroundColor(pink);
        button2.setBackgroundColor(pink);
        button3.setBackgroundColor(pink);
        //showAll();
        if (isChecked)
            showWebView2();
        else
            showNothing();
    } else if (optionC < optionA && optionC < optionB&&optionC<optionD) {

        button2.setBackgroundColor(green);
        button.setBackgroundColor(pink);
        button1.setBackgroundColor(pink);
        button3.setBackgroundColor(pink);

        // showAll();
        if (isChecked)
            showWebView3();
        else
            showNothing();

        Log.d("msg", "che");

    }
    else if (optionD < optionA && optionD < optionB && optionD<optionC) {

        button3.setBackgroundColor(green);
        button.setBackgroundColor(pink);
        button1.setBackgroundColor(pink);
        button2.setBackgroundColor(pink);

        // showAll();
        if (isChecked)
            showWebView4();
        else
            showNothing();

        Log.d("msg", "che");

    }

}


        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate() {

        if(checkbox3.isChecked())
            optionFour=true;
        else
            optionFour=false;
        if(checkbox4.isChecked())
            optionDual=true;
        else
            optionDual=false;

        green = Color.parseColor("#009e17");
        bg = Color.parseColor("#009e17");
        pink = Color.parseColor("#ffffff");
        layout = LayoutInflater.from(this).inflate(R.layout.answer_layout, null);

        layout1 = LayoutInflater.from(this).inflate(R.layout.answer_layout, null);
        webView = (WebView) layout.findViewById(R.id.webView);
        webView1 = (WebView) layout.findViewById(R.id.webView1);
        webView2 = (WebView) layout.findViewById(R.id.webView2);
        webView3 = (WebView) layout.findViewById(R.id.webView3);

        webViewClone = (WebView) layout1.findViewById(R.id.webView);
        webView1Clone = (WebView) layout1.findViewById(R.id.webView1);
        webView2Clone = (WebView) layout1.findViewById(R.id.webView2);
        webView3Clone = (WebView) layout1.findViewById(R.id.webView3);

        button = layout.findViewById(R.id.button);
        button1 = layout.findViewById(R.id.button1);
        button2 = layout.findViewById(R.id.button2);
        button3 = layout.findViewById(R.id.button3);
        buttonClone = layout1.findViewById(R.id.button);
        button1Clone = layout1.findViewById(R.id.button1);
        button2Clone = layout1.findViewById(R.id.button2);
        button3Clone = layout1.findViewById(R.id.button3);
        textQuest=layout.findViewById(R.id.textQuest);
        textQuestClone=layout1.findViewById(R.id.textQuest);
        progressBar = layout.findViewById(R.id.progressBar);
        progressBarClone = layout1.findViewById(R.id.progressBar);


        context = this;

        webView.getSettings().setJavaScriptEnabled(true);

        webView1.getSettings().setJavaScriptEnabled(true);

        webView2.getSettings().setJavaScriptEnabled(true);
        webView3.getSettings().setJavaScriptEnabled(true);

 webViewClone.getSettings().setJavaScriptEnabled(true);

        webView1Clone.getSettings().setJavaScriptEnabled(true);

        webView2Clone.getSettings().setJavaScriptEnabled(true);
        webView3Clone.getSettings().setJavaScriptEnabled(true);


        webView.getSettings().setDomStorageEnabled(true);
        webView1.getSettings().setDomStorageEnabled(true);
        webView2.getSettings().setDomStorageEnabled(true);
        webView3.getSettings().setDomStorageEnabled(true);

        webView.getSettings().setBlockNetworkImage(true);
        webView1.getSettings().setBlockNetworkImage(true);
        webView2.getSettings().setBlockNetworkImage(true);
        webView3.getSettings().setBlockNetworkImage(true);

         webView.getSettings().setLoadWithOverviewMode(true);
         webView1.getSettings().setLoadWithOverviewMode(true);
         webView2.getSettings().setLoadWithOverviewMode(true);
         webView3.getSettings().setLoadWithOverviewMode(true);

            webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            webView1.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            webView2.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            webView3.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView1.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView2.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView3.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);


        webViewClone.getSettings().setDomStorageEnabled(true);
        webView1Clone.getSettings().setDomStorageEnabled(true);
        webView2Clone.getSettings().setDomStorageEnabled(true);
        webView3Clone.getSettings().setDomStorageEnabled(true);

        webViewClone.getSettings().setBlockNetworkImage(true);
        webView1Clone.getSettings().setBlockNetworkImage(true);
        webView2Clone.getSettings().setBlockNetworkImage(true);
        webView3Clone.getSettings().setBlockNetworkImage(true);

         webViewClone.getSettings().setLoadWithOverviewMode(true);
         webView1Clone.getSettings().setLoadWithOverviewMode(true);
         webView2Clone.getSettings().setLoadWithOverviewMode(true);
         webView3Clone.getSettings().setLoadWithOverviewMode(true);

            webViewClone.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            webView1Clone.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            webView2Clone.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            webView3Clone.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

        webViewClone.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView1Clone.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView2Clone.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView3Clone.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        parentContainer=layout.findViewById(R.id.parentContainer);
        webContainer=layout.findViewById(R.id.webContainer);
        buttonContainer=layout.findViewById(R.id.buttonContainer);

        cross=layout.findViewById(R.id.cross);
        plus=layout.findViewById(R.id.plus);
        minus=layout.findViewById(R.id.minus);

        crossClone=layout1.findViewById(R.id.cross);
        plusClone=layout1.findViewById(R.id.plus);
        minusClone=layout1.findViewById(R.id.minus);

        crossClone.setVisibility(View.INVISIBLE);
        plusClone.setVisibility(View.INVISIBLE);
        minusClone.setVisibility(View.INVISIBLE);

       webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
       webView1.setLayerType(View.LAYER_TYPE_HARDWARE, null);
       webView2.setLayerType(View.LAYER_TYPE_HARDWARE, null);
       webView3.setLayerType(View.LAYER_TYPE_HARDWARE, null);



     //  webView.setInitialScale(120);
 /*      webView1.setInitialScale(120);
       webView2.setInitialScale(120);
       webView3.setInitialScale(120);

        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView1.getSettings().setBuiltInZoomControls(true);
        webView1.getSettings().setDisplayZoomControls(false);
        webView2.getSettings().setBuiltInZoomControls(true);
        webView3.getSettings().setBuiltInZoomControls(true);
        webView2.getSettings().setDisplayZoomControls(false);
        webView3.getSettings().setDisplayZoomControls(false);

       webView.getSettings().setUseWideViewPort(true);
        webView1.getSettings().setUseWideViewPort(true);
         webView2.getSettings().setUseWideViewPort(true);
         webView3.getSettings().setUseWideViewPort(true);


        String newUA= "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0";
        webView.getSettings().setUserAgentString(newUA);
        webView1.getSettings().setUserAgentString(newUA);
        webView2.getSettings().setUserAgentString(newUA);
        webView3.getSettings().setUserAgentString(newUA);*/

        webParam1 = webView.getLayoutParams();
        webParam2 = webView1.getLayoutParams();
        webParam3 = webView2.getLayoutParams();
        webParam4 = webView3.getLayoutParams();

        webContainerParam=webContainer.getLayoutParams();
        buttonContainerParam=buttonContainer.getLayoutParams();

    int LAYOUT_FLAG;
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
} else {
    LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
}

 WindowManager.LayoutParams params = new WindowManager.LayoutParams(
    WindowManager.LayoutParams.WRAP_CONTENT,
    WindowManager.LayoutParams.WRAP_CONTENT,
    LAYOUT_FLAG,
    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
    PixelFormat.TRANSLUCENT);

            WindowManager.LayoutParams params1 = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    LAYOUT_FLAG,
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);


            params1.gravity = Gravity.BOTTOM | Gravity.LEFT;        //Initially view will be added to top-left corner
            params1.x = 0;
            params1.y = 0;

            getWindowManager().addView(layout1, params1);



        params.gravity = Gravity.TOP | Gravity.RIGHT;        //Initially view will be added to top-left corner
        params.x = 10;
        params.y = 30;

        getWindowManager().addView(layout, params);


      layout1.setVisibility(View.GONE);

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout.setVisibility(View.GONE);
                layout1.setVisibility(View.GONE);
            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                first+=35;
                second+=40;

                buttonContainerParam.width = second;
                buttonContainer.setLayoutParams(buttonContainerParam);

                webContainerParam.width = second;
                webContainer.setLayoutParams(webContainerParam);




            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                first-=35;


            if(second>200) {
                second-=40;
                buttonContainerParam.width = second;
                buttonContainer.setLayoutParams(buttonContainerParam);

                webContainerParam.width = second;
                webContainer.setLayoutParams(webContainerParam);
            }
            else
            {
                Toast.makeText(context, "Minimum limit reached.", Toast.LENGTH_SHORT).show();
            }

            }
        });

        button.setOnClickListener(view -> BubbleService.this.showWebView1());

        button1.setOnClickListener(view -> BubbleService.this.showWebView2());

        button2.setOnClickListener(view -> BubbleService.this.showWebView3());

        button3.setOnClickListener(view -> BubbleService.this.showWebView4());

        layout.findViewById(R.id.parentContainer).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            private float moveDistance;



            @Override

            public boolean onTouch(View view, MotionEvent motionEvent) {
                WindowManager.LayoutParams params = (WindowManager.LayoutParams) view.getLayoutParams();
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        moveDistance = 0;
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = motionEvent.getRawX();
                        initialTouchY = motionEvent.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        view.performClick();
                        if (Float.compare(moveDistance, 100f) >= 0) {

                        } else {

                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.d("zxcvb","move");
                        params.x = initialX - (int) (motionEvent.getRawX() - initialTouchX);
                        params.y = initialY + (int) (motionEvent.getRawY() - initialTouchY);
                        float distance = motionEvent.getRawX() + initialTouchX
                                + motionEvent.getRawY() - initialTouchY;
                        moveDistance += Math.abs(distance);
                        mWindowManager.updateViewLayout(view, params);
                        break;
                }
                return true;
            }
        });
        layout1.findViewById(R.id.parentContainer).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            private float moveDistance;



            @Override

            public boolean onTouch(View view, MotionEvent motionEvent) {
                WindowManager.LayoutParams params = (WindowManager.LayoutParams) view.getLayoutParams();
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        moveDistance = 0;
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = motionEvent.getRawX();
                        initialTouchY = motionEvent.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        view.performClick();
                        if (Float.compare(moveDistance, 100f) >= 0) {

                        } else {

                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.d("zxcvb","move");
                        params.x = initialX + (int) (motionEvent.getRawX() - initialTouchX);
                        params.y = initialY - (int) (motionEvent.getRawY() - initialTouchY);
                        float distance = motionEvent.getRawX() + initialTouchX
                                + motionEvent.getRawY() - initialTouchY;
                        moveDistance += Math.abs(distance);
                        mWindowManager.updateViewLayout(view, params);
                        break;
                }
                return true;
            }
        });
        super.onCreate();
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {

            Toast.makeText(context, "Cache Memory Error.", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else
            return dir != null && dir.isFile() && dir.delete();
    }

    @SuppressLint("SetTextI18n")
    private void initialised() {
        optionA = 0;
        optionB = 0;
        optionC = 0;
        optionD = 0;
        optionAClone = 0;
        optionBClone = 0;
        optionCClone = 0;
        optionDClone = 0;
        optiona = "Not Found";
        optionb = "Not Found";
        optionc = "Not Found";
        optiond = "Not Found";
        forA = false;
        forB = false;
        forC = false;
        forD = false;
        forAClone = false;
        forBClone = false;
        forCClone = false;
        forDClone = false;

        flag=0;

        find1="Not Found";
        find2="Not Found";
        find3="Not Found";
        find4="Not Found";

        button.setText("OPTION A");
        button1.setText("OPTION B");
        button2.setText("OPTION C");
        buttonClone.setText("OPTION A");
        button1Clone.setText("OPTION B");
        button2Clone.setText("OPTION C");

        if(optionFour) {
            button3.setVisibility(View.VISIBLE);
            button3Clone.setVisibility(View.VISIBLE);
            button3.setText("OPTION D");
            button3Clone.setText("OPTION D");
            button3.setBackgroundColor(pink);
            button3Clone.setBackgroundColor(pink);
        }
        else
        {
            button3.setVisibility(View.GONE);
            button3Clone.setVisibility(View.GONE);

        }




        button2.setBackgroundColor(pink);
        button1.setBackgroundColor(pink);
        button.setBackgroundColor(pink);
        button2Clone.setBackgroundColor(pink);
        button1Clone.setBackgroundColor(pink);
        buttonClone.setBackgroundColor(pink);
    }




    private void destroyWebview() {

        webView.clearCache(true);
        webView1.clearCache(true);
        webView2.clearCache(true);
        webView3.clearCache(true);

        webView.clearMatches();
        webView2.clearMatches();
        webView1.clearMatches();
        webView3.clearMatches();

        webView.stopLoading();
        webView1.stopLoading();
        webView2.stopLoading();
        webView3.stopLoading();

        webView1.clearHistory();
        webView.clearHistory();
        webView2.clearHistory();
        webView3.clearHistory();


        webViewClone.clearCache(true);
        webView1Clone.clearCache(true);
        webView2Clone.clearCache(true);
        webView3Clone.clearCache(true);

        webViewClone.clearMatches();
        webView2Clone.clearMatches();
        webView1Clone.clearMatches();
        webView3Clone.clearMatches();

        webViewClone.stopLoading();
        webView1Clone.stopLoading();
        webView2Clone.stopLoading();
        webView3Clone.stopLoading();

        webView1Clone.clearHistory();
        webViewClone.clearHistory();
        webView2Clone.clearHistory();
        webView3Clone.clearHistory();



    }

  /*  private void initialiseWebview()
    {

        webView = (WebView) layout.findViewById(R.id.webView);
        webView1 = (WebView) layout.findViewById(R.id.webView1);
        webView2 = (WebView) layout.findViewById(R.id.webView2);






    }*/

private void showWebView1()

{
    webParam2.height = 1;
    webParam2.width = 1;
    webView1.setLayoutParams(webParam2);

    webParam1.height =  ViewGroup.LayoutParams.MATCH_PARENT;
    webParam1.width = ViewGroup.LayoutParams.MATCH_PARENT;
    webView.setLayoutParams(webParam1);

    webParam3.height = 1;
    webParam3.width = 1;
    webView2.setLayoutParams(webParam3);

}

private void showWebView2()
{
    webParam1.height = 1;
    webParam1.width = 1;
    webView.setLayoutParams(webParam1);
    webParam2.height = ViewGroup.LayoutParams.MATCH_PARENT;
    webParam2.width = ViewGroup.LayoutParams.MATCH_PARENT;
    webView1.setLayoutParams(webParam2);

    webParam3.height = 1;
    webParam3.width = 1;
    webView2.setLayoutParams(webParam3);
}
private void showWebView3()
    {
        webParam1.height = 1;
        webParam1.width = 1;
        webView.setLayoutParams(webParam1);
        webParam3.height = ViewGroup.LayoutParams.MATCH_PARENT;
        webParam3.width = ViewGroup.LayoutParams.MATCH_PARENT;
        webView2.setLayoutParams(webParam3);
        webParam2.height = 1;
        webParam2.width = 1;
        webView1.setLayoutParams(webParam2);
    }
    private void showWebView4()
    {
        webParam1.height = 1;
        webParam1.width = 1;
        webView.setLayoutParams(webParam1);
        webParam4.height = ViewGroup.LayoutParams.MATCH_PARENT;
        webParam4.width = ViewGroup.LayoutParams.MATCH_PARENT;
        webView3.setLayoutParams(webParam4);
        webParam2.height = 1;
        webParam3.height = 1;
        webParam2.width = 1;
        webParam3.width = 1;
        webView1.setLayoutParams(webParam2);
        webView2.setLayoutParams(webParam3);
    }
private void showWeb1()
{
    if(running==true) {

        String arr1[];


        if (size >= 4) {
if(!optionFour)
            arr1 = arr.get(size - 3).split(" ");
else
    arr1 = arr.get(size - 4).split(" ");
           // String find;

            if (arr1.length > 1) {

                second2 = arr1[1];

            }
            if (!(arr1[0].equals("The") || arr1[0].equals("A") || arr1[0].equals("An") || arr1[0].contains(".")||arr1[0].contains("In"))) {
//                arr1[0] = arr1[0].replaceAll("[^a-zA-Z0-9_.'-]", "");

                find1 = arr1[0];

               // webView.findAllAsync(find1);

                optiona = arr1[0];

            } else {
//                arr1[arr1.length - 1] = arr1[arr1.length - 1].replaceAll("[^a-zA-Z0-9_.'-]", "");
                find1=arr1[arr1.length - 1];
               // webView.findAllAsync(find1);
                Log.d("dfdfdf", arr1[arr1.length - 1]);
                optiona = arr1[arr1.length - 1];
            }
        }
    }

}
private void showWeb2() {
    if (running == true) {
        String arr2[];


        if (size >= 4) {
            if(!optionFour)
            arr2 = arr.get(size - 2).split(" ");
            else
                arr2 = arr.get(size - 3).split(" ");

            String find;
            if (arr2.length > 1) {

                second2 = arr2[1];

            }

            if (!(arr2[0].equals("The") || arr2[0].equals("A") || arr2[0].equals("An") || arr2[0].contains(".")||arr2[0].contains("In"))) {
          //      arr2[0] = arr2[0].replaceAll("[^a-zA-Z0-9_.'-]", "");


                find2 = arr2[0];


                //webView1.findAllAsync(find2);

                optionb = arr2[0];

            } else {
            //    arr2[arr2.length - 1] = arr2[arr2.length - 1].replaceAll("[^a-zA-Z0-9_.'-]", "");
                find2=arr2[arr2.length - 1];
                //webView1.findAllAsync(find2);
                Log.d("dfdfdf", arr2[arr2.length - 1]);
                optionb = arr2[arr2.length - 1];
            }
        }
    }
}

private void showWeb3() {
    if (running == true) {

        String arr3[];
        if (size >= 4) {
            if(!optionFour)
                arr3 = arr.get(size - 1).split(" ");
            else
                arr3 = arr.get(size - 2).split(" ");


System.out.print( arr.get(size - 2));
Log.d("qw", arr.get(size - 2));
            if (arr3.length > 1) {

                second2 = arr3[1];

            }
            if (!(arr3[0].equals("The") || arr3[0].equals("A") || arr3[0].equals("An") || arr3[0].contains(".")||arr3[0].contains("In"))) {
               // arr3[0] = arr3[0].replaceAll("[^a-zA-Z0-9_.'-]", "");
                find3 = arr3[0];



                //webView2.findAllAsync(find3);

                optionc = arr3[0];

            } else {
              //  arr3[arr3.length - 1] = arr3[arr3.length - 1].replaceAll("[^a-zA-Z0-9_.'-]", "");
                find3=arr3[arr3.length - 1];
                //webView2.findAllAsync(find3);
                Log.d("dfdfdf", arr3[arr3.length - 1]);
                optionc = arr3[arr3.length - 1];
            }


        }
    }
}
    private void showWeb4() {
        if (running == true) {

            String arr4[];
            if (size >= 4) {

                    arr4 = arr.get(size - 1).split(" ");



                Log.d("qw", arr.get(size - 1));
                if (arr4.length > 1) {

                    second2 = arr4[1];

                }
                if (!(arr4[0].equals("The") || arr4[0].equals("A") || arr4[0].equals("An") || arr4[0].contains(".")||arr4[0].contains("In"))) {
                 //   arr4[0] = arr4[0].replaceAll("[^a-zA-Z0-9_.'-]", "");
                    find4 = arr4[0];



                    //webView2.findAllAsync(find3);

                    optiond = arr4[0];

                } else {
                   // arr4[arr4.length - 1] = arr4[arr4.length - 1].replaceAll("[^a-zA-Z0-9_.'-]", "");
                    find4=arr4[arr4.length - 1];
                    //webView2.findAllAsync(find3);
                    Log.d("dfdfdf", arr4[arr4.length - 1]);
                    optiond = arr4[arr4.length - 1];
                }


            }
        }
    }

    private void showWebType1() {
        if (running == true) {



            if (size >= 4) {



                if(!optionFour)
                    find1  = arr.get(size - 3).trim();
                else
                    find1  = arr.get(size - 4).trim();

                 optiona=find1;

            }

        }
    }
    private void showWebType2() {
        if (running == true) {



            if (size >= 4) {




                if(!optionFour)
                    find2  = arr.get(size - 2).trim();
                else
                    find2  = arr.get(size - 3).trim();
                optionb=find2;
            }

        }
    }

    private void showWebType3() {
        if (running == true) {



            if (size >= 4) {




                if(!optionFour)
                    find3  = arr.get(size - 1).trim();
                else
                    find3  = arr.get(size - 2).trim();
                optionc=find3;
            }

        }
    }
    private void showWebType4() {
        if (running == true) {



            if (size >= 4) {


                    find4  = arr.get(size - 1).trim();
                optiond=find4;
            }

        }
    }

    /*
public String changeNumToString(String num)
{
    switch (num)
    {
        case "0":
            return "zero";
        case "1":
            return "one";

        case "2":
            return "two";

        case "3":
            return "three";

        case "4":
            return "four";

        case "5":
            return "five";

        case "6":
            return "six";

        case "7":
            return "seven";

        case "8":
            return "eight";

        case "9":
            return "nine";

    }
    return "";
}

*/

public void showNothing()
{
    webParam2.height = 1;
    webParam2.width = ViewGroup.LayoutParams.MATCH_PARENT;
    webView1.setLayoutParams(webParam2);

    webParam1.height =  1;
    webParam1.width = ViewGroup.LayoutParams.MATCH_PARENT;
    webView.setLayoutParams(webParam1);

    webParam3.height = 1;
    webParam3.width = ViewGroup.LayoutParams.MATCH_PARENT;
    webView2.setLayoutParams(webParam3);

    webParam4.height = 1;
    webParam4.width = ViewGroup.LayoutParams.MATCH_PARENT;
    webView3.setLayoutParams(webParam4);
}


    public void showQuest(String ques)

    {
        textQuest.setText("Using( "+engine+" )");
        textQuestClone.setText("DEFAULT :- Using ( Google )");
    }


    public void findInWeb1()
    {
        webView.findAllAsync(find1);
    }
    public void findInWeb1Clone()
    {
       webViewClone.findAllAsync(find1);
    }
    public void findInWeb2()
    {
        webView1.findAllAsync(find2);
    }
    public void findInWeb2Clone()
    {
        webView1Clone.findAllAsync(find2);
    }
    public void findInWeb3()
    {
        webView2.findAllAsync(find3);
    }
    public void findInWeb3Clone()
    {
        webView2Clone.findAllAsync(find3);
    }
    public void findInWeb4()
    {
        webView3.findAllAsync(find4);
    }
    public void findInWeb4Clone()
    {
        webView3Clone.findAllAsync(find4);
    }

    public String modifyString(String str)
    {

        str=str.toLowerCase();
        String[][] replacements = {  {"who",""},
                {"what"," "},
                {"where"," "},

                {"when"," "},


                {" that "," "},
                {" have "," "},
                {" for "," "},
                {" the " ," "},
                {"why "," "},
                {" the "," "},
                {" on "," "},
                {" with "," "},
                {" as "," "},
                {"this"," "},
                {" by "," "},
                {"from"," "},
                {" they " ," "},
                {" a "," "},
                {" an "," "},
                {" and "," "},
                {"following"," "},
                {"among"," "},
                {" my "," "},
                {" are "," "},
                { " in "," "},
                {" to "," "},
                {"these"," "},
                {" is "," "},
                {" does "," "},
                {"which"," "},
                {" his "," " },
                {" her "," "},
                {" also "," "},
                {" never "," "},
                {" have "," "},
                {" it "," "},
                {" not "," "},
                {" we "," "},
                {" means "," "},
                {" you "," "},
                {" comes "," "},
                {" came " ," "},
                {" come"," "},
                {" about "," "},

                {" from ",""},
                {" go "," "},
                {"?"," "},
                {","," "},
                {"!"," "},
                {"'"," "},
                {" has "," "},

                {"\""," "},
                {"not"," "},
                {"isn\"t"," "},
                {"except"," "},
                {"don\"t"," "},
                {"doesn\"t"," "},

                {"wasn\"t"," "},
                {"wouldn\"t"," "},
                {"can\"t"," "}

        };

//loop over the array and replace

        for(String[] replacement: replacements) {
            str = str.replace(replacement[0], replacement[1]);
        }
      /*  str= str.replace(" not ","");
        str= str.replace(" never","");
        str= str.replace(" didnt","");
        str=str.replace("Which of these","");
        str=str.replace("Who among these","");
        str=str.replace("Where is the ","");
        str=str.replace("Where ","");
        str=str.replace("Which of","");
        str=str.replace("Which ","");
        str=  str.replace("following","");
        str=  str.replace(" NOT ","");
        str=  str.replace(" NEVER ","");
        */


    return str;
    }


}