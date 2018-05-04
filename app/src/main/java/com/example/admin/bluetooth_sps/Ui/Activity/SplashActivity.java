package com.example.admin.bluetooth_sps.Ui.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.bluetooth_sps.Base.BaseActivity;
import com.example.admin.bluetooth_sps.R;
import com.example.admin.bluetooth_sps.Utils.StreamUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


public class SplashActivity extends BaseActivity {
    private static final int CODE_UPDATE_DIALOG = 0;
    private static final int CODE_URL_ERROR = 1;
    private static final int CODE_NET_ERROR = 2;
    private static final int CODE_JSON_ERROR = 3;
    private static final int CODE_ENTER_HOME= 4;

    private TextView tvVersion;
    private TextView tvProgress;
    private RelativeLayout rlRoot;

    private String mVersionName;
    private int mVersionCode;
    private String mDescription;
    private String mDownloadUrl;
    private SharedPreferences mPref;


    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_UPDATE_DIALOG:
                    showUpdateDialog();
                    break;
                case CODE_URL_ERROR:
                    Toast.makeText(SplashActivity.this,"URL错误",Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case CODE_NET_ERROR:
                    Toast.makeText(SplashActivity.this,"网络错误,检查更新失败",Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case CODE_JSON_ERROR:
                    Toast.makeText(SplashActivity.this,"JSON错误",Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case CODE_ENTER_HOME:
                    enterHome();
                    break;
                default:
                    break;
            }
        }
    };

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        //透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        tvVersion=(TextView)findViewById(R.id.tv_version);
        String versionName = getVersionName();
        tvVersion.setText("版本号:"+versionName);
        tvProgress=(TextView)findViewById(R.id.tv_progress);
        tvProgress=(TextView)findViewById(R.id.tv_progress);
        rlRoot=(RelativeLayout)findViewById(R.id.rl_root);
        mPref=getSharedPreferences("config",MODE_PRIVATE);
        boolean autoUpdate=mPref.getBoolean("auto_update",true);
        if(autoUpdate) {
            checkVersion();
        }else {
            handler.sendEmptyMessageDelayed(CODE_ENTER_HOME,2000);
        }

        //渐变动画效果
        AlphaAnimation anim = new AlphaAnimation(0.3f,1f);
        anim.setDuration(2000);
        rlRoot.startAnimation(anim);

    }

    private String getVersionName() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            //int versionCode = packageInfo.versionCode;
            String versionName = packageInfo.versionName;
            //System.out.println("versionName=" + versionName + ";versionCode=" + versionCode);
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    private int getVersionCode() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            int versionCode = packageInfo.versionCode;
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return Integer.parseInt(null);
    }

    private void checkVersion() {
        final long startTime = System.currentTimeMillis();
        new Thread() {
            @Override
            public void run() {
                Message msg=Message.obtain();
                HttpURLConnection connection=null;
                try {
                    URL url = new URL("http://121.43.151.202:54123/update.json");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    connection.connect();

                    int responseCode = connection.getResponseCode();
                    if(responseCode==200)
                    {
                        InputStream inputStream = connection.getInputStream();
                        String result = StreamUtils.readFromString(inputStream);
                        System.out.println("网络返回："+result);

                        JSONObject jo=new JSONObject(result);
                        mVersionName = jo.getString("versionName");
                        mVersionCode = jo.getInt("versionCode");
                        mDescription = jo.getString("description");
                        mDownloadUrl=jo.getString("downloadUrl");

                        if(mVersionCode>getVersionCode()) {
                            msg.what=CODE_UPDATE_DIALOG;
                        }else {
                            msg.what=CODE_ENTER_HOME;
                        }
                    }
                } catch (MalformedURLException e) {
                    msg.what=CODE_URL_ERROR;
                    e.printStackTrace();
                } catch (IOException e) {
                    msg.what=CODE_NET_ERROR;
                    e.printStackTrace();
                } catch (JSONException e) {
                    msg.what=CODE_JSON_ERROR;
                    e.printStackTrace();
                }finally {
                    long endTime=System.currentTimeMillis();
                    long timeUse=endTime-startTime;
                    if(timeUse<2000){
                        try {
                            Thread.sleep(2000-timeUse);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    handler.sendMessage(msg);
                    if(connection!=null) {
                        connection.disconnect();
                    }
                }
            }
        }.start();
    }

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("最新版本:" + mVersionName);
        builder.setMessage(mDescription);
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
//                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
//                    }
//                }
                //System.out.println("立即更新");
                download();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                enterHome();
            }
        });
        builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterHome();
            }
        });
        builder.show();
    }

    private void download() {
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            tvProgress.setVisibility(View.VISIBLE);
            String target = Environment.getExternalStorageDirectory()+"/sps.apk";
            HttpUtils utils=new HttpUtils();
            utils.download(mDownloadUrl, target, new RequestCallBack<File>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    super.onLoading(total, current, isUploading);
                    System.out.println("下载进度！" + current + "/" + total);
                    //System.out.println("下载进度："+current*100/total+"%");
                    tvProgress.setText("下载进度:" + longToString(current)+"/"+ longToString(total)+"M");
                }

                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    System.out.println("下载成功！");
                    Intent intent=new Intent(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    Uri uri;
                    //安卓7.0以上
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        uri = FileProvider.getUriForFile(getApplication(),
                                getApplication().getPackageName() + ".FileProvider",
                                responseInfo.result);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } else {
                        //安卓7.0以下
                        uri = Uri.fromFile(responseInfo.result);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                    //intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    intent.setDataAndType(uri,"application/vnd.android.package-archive");
                    startActivity(intent);
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Toast.makeText(SplashActivity.this,"下载失败！",Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(SplashActivity.this,"没有找到SD卡！",Toast.LENGTH_SHORT).show();
        }

    }

    private void enterHome(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @NonNull
    private String longToString(Long data){
        double data_double=data;
        data_double=data_double/1024/1024;
        BigDecimal b=new  BigDecimal(data_double);
        double  f=b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        return String.valueOf(f);
    }

}
