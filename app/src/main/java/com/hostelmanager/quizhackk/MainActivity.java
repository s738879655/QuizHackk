package com.hostelmanager.quizhackk;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.test.mock.MockPackageManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.chartboost.sdk.Chartboost;
import com.google.ads.mediation.chartboost.ChartboostAdapter;
import com.google.android.ads.mediationtestsuite.MediationTestSuite;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import com.hostelmanager.quizhackk.databinding.ActivityMainBinding;
import com.jirbo.adcolony.AdColonyAdapter;
import com.jirbo.adcolony.AdColonyBundleBuilder;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, RewardedVideoAdListener,PurchasesUpdatedListener{
    public static MediaProjection sMediaProjection;

   //For in app billing
    private BillingClient mBillingClient;
private String mPremiumUpgradePrice;
public static  boolean optionFour=true;

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 5566;
    private static final int REQUEST_CODE = 55566;
    private ActivityMainBinding binding;
    private MediaProjectionManager mProjectionManager;
    private AdView mAdView;

    private InterstitialAd mInterstitialAd;
    private RewardedVideoAd mRewardedVideoAd;
 //   Button tutorial;
    private BroadcastReceiver mReceiver = null;
    private Spinner spinner;
    private Spinner spinner1;
    TextView counting;
    private Button btn;
    int interval=1;
    int qq = 7;
    public static String engine="Google";
    public static String option="3 options";
    public static boolean isChecked=false;
    public static boolean isChecked1=false;
    private AdRequest request;

    Switch switchBtn;
    Activity activity;

    public static CheckBox checkbox3;
    Switch switchBtn1;

    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        checkDrawOverlayPermission();
        Bundle extras = new Bundle();
    /*    extras.putString(InMobiNetworkKeys.AGE_GROUP, InMobiNetworkValues.BELOW_18);
        extras.putString(InMobiNetworkKeys.AGE_GROUP, InMobiNetworkValues.BETWEEN_18_AND_24);
        extras.putString(InMobiNetworkKeys.AGE_GROUP, InMobiNetworkValues.BETWEEN_25_AND_29);
        extras.putString(InMobiNetworkKeys.AGE_GROUP, InMobiNetworkValues.BETWEEN_30_AND_34);
        extras.putString(InMobiNetworkKeys.AGE_GROUP, InMobiNetworkValues.BETWEEN_35_AND_44);
        extras.putString(InMobiNetworkKeys.AREA_CODE, "12345");
*/
       // AdColony.configure(this,           // activity context
         //       "\tappca135fed42fc480698",
          //      "vz1dbf3e518541436994");



        if(getIntent().getExtras() != null){
            for(String key : getIntent().getExtras().keySet()){
                if(key.equals("link")){
                    Log.d(key,getIntent().getExtras().getString(key));
                    Intent in =new Intent(Intent.ACTION_VIEW);
                    in.setData(Uri.parse("http://"+getIntent().getExtras().getString(key)));
                    //Toast.makeText(this,getIntent().getExtras().getString(key),Toast.LENGTH_LONG).show();
                    startActivity(in);
                }
            }
        }




      //  AdColonyBundleBuilder.setShowPrePopup(true);
       // AdColonyBundleBuilder.setShowPostPopup(true);
     //   request = new AdRequest.Builder()
      //          .addNetworkExtrasBundle(AdColonyAdapter.class, AdColonyBundleBuilder.build())
       //         .build();

        Bundle bundle = new ChartboostAdapter.ChartboostExtrasBundleBuilder()
                .setFramework(Chartboost.CBFramework.CBFrameworkOther, "1.2.3")
                .build();
        request = new AdRequest.Builder()
                .addNetworkExtrasBundle(ChartboostAdapter.class, bundle).addNetworkExtrasBundle(AdColonyAdapter.class, AdColonyBundleBuilder.build())
                .build();
       // adLoader.loadAd(adRequest);

        //  test id  ca-app-pub-3940256099942544~3347511713
        // main id   "ca-app-pub-3184687614190176~1319231353"
        MobileAds.initialize(this,
                "ca-app-pub-3940256099942544~3347511713");
        mAdView =findViewById(R.id.adView);
        btn = findViewById(R.id.test);

        spinner =findViewById(R.id.spinner);
      //  spinner1 =findViewById(R.id.spinner1);
        counting=findViewById(R.id.counting);
        switchBtn=findViewById(R.id.idSwitch);
        switchBtn1=findViewById(R.id.idSwitch1);

        counting.setVisibility(View.VISIBLE);

        checkbox3=findViewById(R.id.checkBox3);

     //   AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(request);
        qq=7;



      //  MediationTestSuite.launch(MainActivity.this, "ca-app-pub-3184687614190176~1319231353");

       // MediationTestSuite.setAdRequest(request.build());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("videoId");

        mInterstitialAd = new InterstitialAd(this);

        // test id   ca-app-pub-3940256099942544/1033173712

        //main "ca-app-pub-3184687614190176/3725902849"
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        mInterstitialAd.loadAd(request);


        validate();

        //Location



      /*  if(sMediaProjection==null)
        {
            for(int i=15;i>=0;i--)
            {

                try {
                    thread.sleep(1000);
                    counter.setText(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }*/





        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);


        loadRewardedVideoAd();


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.engine, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        /*
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.options, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
       // spinner1.setAdapter(adapter1);
       // spinner1.setOnItemSelectedListener(this);
*/
        final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenReceiver(this);
        registerReceiver(mReceiver, filter);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }


        });



        mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        binding.test.setOnClickListener(view -> {

          //  mInterstitialAd.show();
            if(!checkDrawOverlayPermission()){
                checkDrawOverlayPermission();
                return;
            }
           if(!checkWritePermission()){
                checkWritePermission();
                return;
            }
            if(!checkLocationPermission()){
                checkLocationPermission();
                return;
            }
            if (mRewardedVideoAd.isLoaded()) {
                mRewardedVideoAd.show();
            }
            else if(sMediaProjection==null)
            {
                startMediaProjection();
                mInterstitialAd.show();
            }
            else
            {
               // mInterstitialAd.show();
                Toast.makeText(this, "Already Running in Background !", Toast.LENGTH_SHORT).show();
            }

        });


       int delay = 1000;
        int period = 1000;
        final Timer time = new Timer();
        time.scheduleAtFixedRate(new TimerTask() {
            public void run() {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        qq--;
                        if(qq==0)
                        {

                            counting.setVisibility(View.INVISIBLE);
                            time.cancel();
                            btn.setEnabled(true);

                        }

                      counting.setText(qq+ "");

                    }
                });


               // Toast.makeText(MainActivity.this, interval+"", Toast.LENGTH_SHORT).show();
            }
        }, delay, period);



/*
        tutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] value = new String[1];
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                       value[0] = dataSnapshot.getValue(String.class);
                      //  Log.d(TAG, "Value is: " + value);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                     //   Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
                watchYoutubeVideo(value[0]);
            }
        });
*/
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.i("token", "FCM Registration Token: " + token);

    }

    private boolean checkLocationPermission()
    {
        try {
            if (ActivityCompat.checkSelfPermission(this, mPermission)
                    != MockPackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{mPermission},
                        REQUEST_CODE_PERMISSION);

                return false;

                // If any permission above not allowed by user, this condition will
                //  execute every time, else your else part will work
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    private boolean checkWritePermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            return false;
        }
        return true;
    }

    private void startMediaProjection() {
        startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
    }

    private boolean checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("kanna","get write permission");
                }
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            sMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
            if (sMediaProjection == null)
            {
                Log.d("kanna", "not get permission of media projection");
                Toast.makeText(this, "Need MediaProjection", Toast.LENGTH_LONG).show();
                startMediaProjection();
            }
            else {
                startBubble();
             //   loadRewardedVideoAd();
            }
        }
    }


    private void startBubble() {
        Log.d("kanna","start bubble");
        binding.test.setEnabled(false);
        Intent intent = new Intent(this, BubbleService.class);
        stopService(intent);
        startService(intent);
    }




  /*  public  void watchYoutubeVideo(String id) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            startActivity(appIntent);
        }
        catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }
    */



    @Override
    protected void onRestart() {

      //  loadRewardedVideoAd();
        if(!mRewardedVideoAd.isLoaded())
        {
            btn.setEnabled(false);
            qq=7;
            int delay = 1000;

            counting.setVisibility(View.VISIBLE);
            counting.setText("6");
            int period = 1000;
            final Timer time = new Timer();
            time.scheduleAtFixedRate(new TimerTask() {
                public void run() {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run()
                        {

                            qq--;
                            if(qq==0)
                            {

                                counting.setVisibility(View.INVISIBLE);
                                time.cancel();
                                btn.setEnabled(true);

                            }

                            counting.setText(qq +" ");

                        }
                    });

                }
            }, delay, period);


        }

        else
            btn.setEnabled(true);




        Timer buttonTimer = new Timer();
        buttonTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        btn.setEnabled(true);
                    }
                });
            }
        }, 7000);

        if (sMediaProjection == null) {
            Intent intent=new Intent(this,BubbleService.class);
            stopService(intent);
        }
        super.onRestart();

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        engine=adapterView.getItemAtPosition(i).toString();
       // option=adapterView.getItemAtPosition(i).toString();
       // Toast.makeText(this, , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onRewardedVideoAdLoaded() {

        if(sMediaProjection==null)
            binding.test.setEnabled(true);

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {

        Toast.makeText(this,"You Have to watch complete (ad) .",Toast.LENGTH_LONG).show();
        loadRewardedVideoAd();
        binding.test.setEnabled(false);
    }


    @Override
    public void onRewarded(RewardItem rewardItem) {
       if(sMediaProjection==null)
        startMediaProjection();
    }
    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
     //   mInterstitialAd.show();
        loadRewardedVideoAd();
    }

    @Override
    public void onRewardedVideoCompleted() {
            loadRewardedVideoAd();
    }

  /*  private void loadRewardedVideoAd() {
        //  test ca-app-pub-3940256099942544/5224354917

        // mmain  "ca-app-pub-3184687614190176/3302111451"
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());
    }

    */


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {

            case R.id.rateus:
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.hostelmanager.quizhackk")));
                }
                catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "com.hostelmanager.quizhackk")));
                }
                return true;
            case R.id.feedback:
                try
                {
                    Intent intent = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:"+"svksvksvksvk@gmail.com"));
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Regrading QuizHack");
                    startActivity(intent);
                }
                catch(ActivityNotFoundException e){
                    //TODO smth
                }
                return true;
       /*     case R.id.buy:



                mBillingClient = BillingClient.newBuilder(activity).setListener(this).build();
                mBillingClient.startConnection(new BillingClientStateListener() {
                    @Override
                    public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
                        if (billingResponseCode == BillingClient.BillingResponse.OK) {
                            // The billing client is ready. You can query purchases here.

                            List skuList = new ArrayList<>();
                            skuList.add("premium_upgrade");

                            SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                            params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                            mBillingClient.querySkuDetailsAsync(params.build(),
                                    new SkuDetailsResponseListener() {
                                        @Override
                                        public void onSkuDetailsResponse(int responseCode, List skuDetailsList) {
                                            // Process the result.

                                            if (responseCode == BillingClient.BillingResponse.OK
                                                    && skuDetailsList != null) {
                                                for (SkuDetails skuD :skuDetailsList) {
                                                    String sku = skuD.getSku();
                                                    String price = skuD.getPrice();
                                                    if ("premium_upgrade".equals(sku)) {
                                                        mPremiumUpgradePrice = price;
                                                    }
                                                }
                                            }
                                        }
                                    });
                        }
                    }
                    @Override
                    public void onBillingServiceDisconnected() {
                        // Try to restart the connection on the next request to
                        // Google Play by calling the startConnection() method.
                    }
                });

                return true;*/
            default:
                return super.onOptionsItemSelected(item);

        }


    }

    public void onSwitchClick(View v)
    {
        if(switchBtn.isChecked())
        {
            Toast.makeText(this, "WebView ON", Toast.LENGTH_SHORT).show();
            isChecked=true;
        }
        else
        {
            isChecked=false;
            Toast.makeText(this, "WebView OFF", Toast.LENGTH_SHORT).show();
        }
    }
    public void onSwitchClick1(View v)
    {
        if(switchBtn1.isChecked())
        {
            Toast.makeText(this, "QuizHack Suggest you the First word of the answer. Accuracy(High)", Toast.LENGTH_SHORT).show();
            isChecked1=true;
        }
        else
        {
            isChecked1=false;
            Toast.makeText(this, "QuizHack Suggest you the Complete answer. Accuracy(Low)", Toast.LENGTH_SHORT).show();
        }
    }



    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                request);
    }

    public void validate()
    {
        if(!getPackageName().equals("com.hostelmanager.quizhackk")||!(getApplicationName(this)).equals("QuizHack"))
        {
            binding.test.setEnabled(false);
            Toast.makeText(this,"Download QuizHack From Play Store",Toast.LENGTH_LONG).show();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.hostelmanager.quizhackk")));
            }
            catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" +"com.hostelmanager.quizhackk" )));
            }
        }


    }

    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }


    @Override
    public void onStart() {
        super.onStart();

    //    Chartboost.onStart(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        validate();
        super.onResume();

        if (sMediaProjection == null)
        {
            Intent intent=new Intent(this,BubbleService.class);
            stopService(intent);
            //  binding.test.setVisibility(View.VISIBLE);
        }
        Chartboost.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
       Chartboost.onPause(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Chartboost.onStop(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
            mRewardedVideoAd.destroy(this);
        Intent intent=new Intent(this,BubbleService.class);
        stopService(intent);
        Chartboost.onDestroy(this);
    }

    @Override
    public void onBackPressed() {
        // If an interstitial is on screen, close it.
        if (Chartboost.onBackPressed()) {
            return;
        } else {
            mInterstitialAd.show();
        }
    }




    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {

    }
}
