package com.example.admin.bluetooth_sps.Ui.Activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.bluetooth_sps.Base.BaseActivity;
import com.example.admin.bluetooth_sps.BlueTooth.BlueTooth_Devices;
import com.example.admin.bluetooth_sps.Ui.Fragment.DownloadFileFragment;
import com.example.admin.bluetooth_sps.File.FileWrite;
import com.example.admin.bluetooth_sps.Message.MessageEvent;
import com.example.admin.bluetooth_sps.Message.MessageEvent2;
import com.example.admin.bluetooth_sps.Service.MyService;
import com.example.admin.bluetooth_sps.R;
import com.example.admin.bluetooth_sps.Ui.Fragment.NetGateFragment20;
import com.example.admin.bluetooth_sps.Ui.Fragment.NetGateFragment40;
import com.example.admin.bluetooth_sps.Ui.Fragment.SensorFragment20;
import com.example.admin.bluetooth_sps.Ui.Fragment.SensorFragment40;
import com.example.admin.bluetooth_sps.Ui.Fragment.SensorFragmentNB;
import com.example.admin.bluetooth_sps.Ui.Fragment.SensorFragmentNBNew;
import com.example.admin.bluetooth_sps.Ui.Fragment.SensorFragmentNBOld;
import com.example.admin.bluetooth_sps.Ui.View.SlidingMenu;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.example.admin.bluetooth_sps.Utils.MyUtils.Exclusive_Or;
import static com.example.admin.bluetooth_sps.Utils.MyUtils.getCharByInt;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private LocalBroadcastManager localBroadcastManager;
    private MyReceiver myReceiver;
    private final static String TAG = "MainActivity";
    public static String HEART_RATE_MEASUREMENT = "0000ffe1-0000-1000-8000-00805f9b34fb";
    public static String DEVICE_NAME = "DEVICE_NAME";
    public static String DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static String DEVICE_RSSI = "DEVICE_RSSI";
    public static MyService mService;
    public static BluetoothGattCharacteristic target_chara = null;
    public int tabBottomFlag=0;
    public int tabFlag=0;  //使用哪个fragment（对象是lora，选择地磁还是网关）
    private int parkLen=0;    //总数据包长
    private int parkLenDe=0;  //当前数据包长
    private int dis_flag_1=1;   //1：字符显示  2：16进制显示
    boolean dis_flag=true;//是否是一个整包
    private int count=0;//用于判断第一包不完整，来了第二包

    private String receiveData="";//收到的数据（进行打包）
    private TextView tvTab1;
    private TextView tvTab2;
    private TextView tvTab3;
    private Button btnSearch;
    private Button btnDisconnect;
    private LinearLayout tabTopLayout1;
    private LinearLayout tabTopLayout2;
    private View viewBottom;
    private SlidingMenu mMenu;
    private String mDeviceName;
    private String mDeviceAddress;
    private String mRssi;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private Handler mhandler = new Handler();
    public DownloadFileFragment downloadFileFragment;
    public NetGateFragment20 netGateFragment20;
    public SensorFragment20 sensorFragment20;
    public NetGateFragment40 netGateFragment40;
    public SensorFragment40 sensorFragment40;
    public SensorFragmentNBNew sensorFragmentNBNew;
    public SensorFragmentNBOld sensorFragmentNBOld;
    public SensorFragmentNB sensorFragmentNB;

    public long exitTime = 0;
    private int fileNameIndex = -1;
    public String rev_str = "";
    private int mMenuWidth;

    //左侧菜单
    private RelativeLayout menuLayoutLogin;
    private RelativeLayout menuLayoutRegister;
    private RelativeLayout menuLayoutChangeMain;
    private LinearLayout menuLayoutAbout;

    public void setRev_str(String rev_str) {
        this.rev_str = rev_str;
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==100){
                new AlertDialog.Builder(MainActivity.this).setTitle("升级配置工具")
                        .setMessage("确定要升级配置工具吗？配置工具将重启，请再次打开配置工具，重新连接上蓝牙，转到本软件主界面长按升级按钮，打开下发文件区域并选择升级文件然后开始升级。")
                        .setNegativeButton("升级", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(btnSearch.getText().toString() .equals("重新连接")
                                        ||btnSearch.getText().toString() .equals("未连接")
                                        ||btnSearch.getText().toString() .equals("搜索蓝牙设备")){
                                    Toast.makeText(MainActivity.this, "请先连接上蓝牙", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                byte[] buff = new byte[8];
                                buff[0] = (byte) 0xff;
                                buff[1] = (byte) 0xfe;
                                buff[2] = (byte) 0xCC;
                                buff[3] = (byte) 0x08;
                                buff[4] = (byte) 0x01;
                                buff[5] = Exclusive_Or(buff,5);
                                buff[6] = (byte) 0xab;
                                buff[7] = (byte) 0xaa;
                                target_chara.setValue(buff);
                                mService.writeCharacteristic(target_chara);
                            }
                        })
                        .setPositiveButton("取消", null)
                        .setCancelable(false)
                        .create()
                        .show();
            }
        }
    };
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerBroad();
        EventBus.getDefault().register(this);
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
        mMenu = (SlidingMenu) findViewById(R.id.id_menu);

        initView();
        initEvent();
        initViewPages();
        hideDownloadFileFragment();
        //如果有虚拟键盘
        if(checkDeviceHasNavigationBar(this)){
            viewBottom.setVisibility(View.VISIBLE);
        }
    }

    private void registerBroad(){
        localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        IntentFilter filter=new IntentFilter();
        filter.addAction("com.zega.action.message");
        myReceiver = new MyReceiver();
        localBroadcastManager.registerReceiver(myReceiver,filter);
    }

    class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            //L.e("MainActivityOnReceive");
            handler.sendEmptyMessage(100);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null)
            return;
        mDeviceName = data.getStringExtra(DEVICE_NAME);
        if (mDeviceName == null)
            mDeviceName = "未命名";
        mDeviceAddress = data.getStringExtra(DEVICE_ADDRESS);

        mRssi = data.getStringExtra(DEVICE_RSSI);
        if (requestCode == 0) {
            Intent gattServiceIntent = new Intent(MainActivity.this, MyService.class);
            bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        }
        if (resultCode == 1) {
            fileNameIndex = data.getIntExtra("FileNameIndex", 0);
            downloadFileFragment.fileNameString=downloadFileFragment.list.get(fileNameIndex);
            downloadFileFragment.tvFilePath.setText(downloadFileFragment.AbsolutePATH + downloadFileFragment.fileNameString);
            downloadFileFragment.mFilePath = downloadFileFragment.tvFilePath.getText().toString();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int action = event.getAction();
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }else {
                finish();
                System.exit(0);
            }
            return true;
        }
        if(keyCode==R.id.btnUpdate20) {
            if (event.getRepeatCount() == 0) {//识别长按短按的代码
                event.startTracking();
                sensorFragment20.isLongPressKey = false;
            } else {
                sensorFragment20.isLongPressKey = true;
                return true;
            }
        }
        if(keyCode==R.id.btnUpdate40) {
            if (event.getRepeatCount() == 0) {//识别长按短按的代码
                event.startTracking();
                sensorFragment40.isLongPressKey = false;
            } else {
                sensorFragment40.isLongPressKey = true;
                return true;
            }
        }
        if(keyCode==R.id.btnUpdateNBNew) {
            if (event.getRepeatCount() == 0) {//识别长按短按的代码
                event.startTracking();
                sensorFragmentNBNew.isLongPressKey = false;
            } else {
                sensorFragmentNBNew.isLongPressKey = true;
                return true;
            }
        }
        if(keyCode==R.id.btnUpdateNB) {
            if (event.getRepeatCount() == 0) {//识别长按短按的代码
                event.startTracking();
                sensorFragmentNB.isLongPressKey = false;
            } else {
                sensorFragmentNB.isLongPressKey = true;
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode==R.id.btnUpdate20) {
            if (sensorFragment20.isLongPressKey) {
                sensorFragment20.isLongPressKey = false;
                return true;
            }
            if (sensorFragment40.isLongPressKey) {
                sensorFragment40.isLongPressKey = false;
                return true;
            }
            if (sensorFragmentNBNew.isLongPressKey) {
                sensorFragmentNBNew.isLongPressKey = false;
                return true;
            }
            if (sensorFragmentNB.isLongPressKey) {
                sensorFragmentNB.isLongPressKey = false;
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    private void initView() {
        tvTab1=(TextView) findViewById(R.id.tv_tab1);
        tvTab2=(TextView) findViewById(R.id.tv_tab2);
        tvTab3=(TextView) findViewById(R.id.tv_tab3);
        btnSearch = (Button) findViewById(R.id.btn_search);
        btnDisconnect = (Button) findViewById(R.id.btn_disconnect);
        tabTopLayout1 =(LinearLayout)findViewById(R.id.tab_top_layout1);
        tabTopLayout2 =(LinearLayout)findViewById(R.id.tab_top_layout2);
        viewBottom = (View) findViewById(R.id.view_bottom);
        tabTopLayout1.setVisibility(View.VISIBLE);
        tabTopLayout2.setVisibility(View.GONE);
        viewBottom.setVisibility(View.GONE);


        //左侧菜单
        menuLayoutLogin=(RelativeLayout) findViewById(R.id.RL_login);
        menuLayoutRegister=(RelativeLayout) findViewById(R.id.RL_register);
        menuLayoutChangeMain=(RelativeLayout) findViewById(R.id.menu_layout_change_main);
        menuLayoutAbout=(LinearLayout) findViewById(R.id.menu_layout_about);
        menuLayoutLogin.setEnabled(false);
        menuLayoutRegister.setEnabled(false);
        menuLayoutChangeMain.setEnabled(false);
        menuLayoutAbout.setEnabled(false);
    }
    private void initEvent() {

        tvTab1.setOnClickListener(this);
        tvTab2.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        btnDisconnect.setOnClickListener(this);
        btnDisconnect.setEnabled(false);

        //左侧菜单
        menuLayoutLogin.setOnClickListener(this);
        menuLayoutRegister.setOnClickListener(this);
        menuLayoutChangeMain.setOnClickListener(this);
        menuLayoutAbout.setOnClickListener(this);
    }
    public void initViewPages(){
        initViewPage(-1);
        initViewPage(6);
        initViewPage(5);
        initViewPage(4);
        initViewPage(3);
        initViewPage(2);
        initViewPage(1);
        initViewPage(0);
    }

    public void initViewPage(int i) {
        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction transaction=fm.beginTransaction();
        hideFragment(transaction);
        switch (i){
            case -1:
                if(downloadFileFragment==null){
                    downloadFileFragment=new DownloadFileFragment();
                    transaction.add(R.id.downloadFile, downloadFileFragment);
                } else {
                    transaction.show(downloadFileFragment);
                }
                break;
            case 0:
                if(sensorFragment20 ==null){
                    sensorFragment20 =new SensorFragment20();
                    transaction.add(R.id.id_content, sensorFragment20);
                } else {
                    transaction.show(sensorFragment20);
                }
                break;
            case 1:
                if(netGateFragment20 ==null){
                    netGateFragment20 =new NetGateFragment20();
                    transaction.add(R.id.id_content, netGateFragment20);
                } else {
                    transaction.show(netGateFragment20);
                }
                break;
            case 2:
                if(sensorFragment40 ==null){
                    sensorFragment40 =new SensorFragment40();
                    transaction.add(R.id.id_content, sensorFragment40);
                } else {
                    transaction.show(sensorFragment40);
                }
                break;
            case 3:
                if(netGateFragment40 ==null){
                    netGateFragment40 =new NetGateFragment40();
                    transaction.add(R.id.id_content, netGateFragment40);
                } else {
                    transaction.show(netGateFragment40);
                }
                break;
            case 4:
                if(sensorFragmentNBNew ==null){
                    sensorFragmentNBNew =new SensorFragmentNBNew();
                    transaction.add(R.id.id_content, sensorFragmentNBNew);
                } else {
                    transaction.show(sensorFragmentNBNew);
                }
                break;
            case 5:
                if(sensorFragmentNBOld ==null){
                    sensorFragmentNBOld =new SensorFragmentNBOld();
                    transaction.add(R.id.id_content, sensorFragmentNBOld);
                } else {
                    transaction.show(sensorFragmentNBOld);
                }
                break;
            case 6:
                if(sensorFragmentNB ==null){
                    sensorFragmentNB =new SensorFragmentNB();
                    transaction.add(R.id.id_content, sensorFragmentNB);
                } else {
                    transaction.show(sensorFragmentNB);
                }
                break;
            default:
                break;
        }
        transaction.commit();
    }
    public void hideFragment(FragmentTransaction transaction) {
        if (sensorFragment20 != null) {
            transaction.hide(sensorFragment20);
        }
        if (netGateFragment20 != null) {
            transaction.hide(netGateFragment20);
        }
        if (sensorFragment40 != null) {
            transaction.hide(sensorFragment40);
        }
        if (netGateFragment40 != null) {
            transaction.hide(netGateFragment40);
        }
        if (sensorFragmentNBNew != null) {
            transaction.hide(sensorFragmentNBNew);
        }
        if (sensorFragmentNBOld != null) {
            transaction.hide(sensorFragmentNBOld);
        }
        if (sensorFragmentNB != null) {
            transaction.hide(sensorFragmentNB);
        }
    }

    public void hideDownloadFileFragment(){
        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction transaction=fm.beginTransaction();
        if (downloadFileFragment != null) {
            transaction.hide(downloadFileFragment);
        }
        transaction.commit();
    }


    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((MyService.LocalBinder) service).getService();
            if (!mService.initialize()) {
                finish();
            }
            // Automatically connects to the device upon successful start-up
            // initialization.
            mService.connect(mDeviceAddress);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            final String action = intent.getAction();
            //System.out.println("testAction"+action);
            if (MyService.ACTION_GATT_CONNECTED.equals(action)) {
                //btnSearch.setText("已连接");
                System.out.println("BroadcastReceiver :" + "device connected");

            } else if (MyService.ACTION_GATT_DISCONNECTED.equals(action)) {
                btnSearch.setText("未连接");
                //System.out.println("BroadcastReceiver :" + "device disconnected");

            } else if (MyService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mService.getSupportedGattServices());
                System.out.println("BroadcastReceiver :" + "device SERVICES_DISCOVERED");
            } else if (MyService.ACTION_DATA_AVAILABLE.equals(action)) {
                final String string = intent.getExtras().getString(MyService.EXTRA_DATA);
                if (string.compareTo("00") == 0) {
                    btnSearch.setText("已连接:" + mDeviceName);
                    btnDisconnect.setEnabled(true);
                    sensorFragment20.setState(true);
                    netGateFragment20.setState(true);
                    sensorFragment40.setState(true);
                    netGateFragment40.setState(true);
                    sensorFragmentNBNew.setState(true);
                    sensorFragmentNBOld.setState(true);
                    sensorFragmentNB.setState(true);
                    //rev_str = "蓝牙设备已连接上"+"\n";
                }
                dealReceiveData(string);
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent messageEvent){
        String string=messageEvent.getMessage();
        dealReceiveData(string);
    }

    //根据左右滑动关闭和打开菜单里的功能
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent2 messageEvent){
        boolean b=messageEvent.getMessage();
        //右划
        if(b){
            menuLayoutLogin.setEnabled(true);
            menuLayoutRegister.setEnabled(true);
            menuLayoutChangeMain.setEnabled(true);
            menuLayoutAbout.setEnabled(true);
        }else {
            menuLayoutLogin.setEnabled(false);
            menuLayoutRegister.setEnabled(false);
            menuLayoutChangeMain.setEnabled(false);
            menuLayoutAbout.setEnabled(false);
        }
    }

    private void dealReceiveData(String string){
        if(string.compareTo("00") != 0) {
            receiveDataPark(string);
            if (dis_flag_1 == 1 && dis_flag) {
                //只是处理字符串
                for(int i=0;i<string.length()/2;i++){
                    if(getCharByInt(string,i)>127) {
                        string="";
                        break;
                    }
                }
                if (string.compareTo("") != 0) {
                    //只有NB配置字符显示（此处处理字符串，lora的不处理不显示）
                    if(tabBottomFlag==2||tabBottomFlag==3||tabBottomFlag==4)
                        displayDataByString(tabFlag, string);
                }
                receiveData = "";//处理完后将其置空
            }else if (dis_flag_1 == 2 && dis_flag) {
                //System.out.println("receiveData:"+receiveData);
                if(receiveData!="") {
                    //保护程序，如果数据不合法则保存起来，不处理
                    if(receiveData.length()/2<4){
                        System.out.println("Error_receiveData:"+receiveData);
                        Toast.makeText(MainActivity.this,"接收数据不完整",Toast.LENGTH_SHORT).show();
                        new FileWrite(receiveData);
                        receiveData="";
                    }else {
                        int lenth=getCharByInt(receiveData,3);
                        if(getCharByInt(receiveData,2)==0x61)
                            lenth=getCharByInt(receiveData,3)<<8|getCharByInt(receiveData,4);
                        if(lenth!=receiveData.length()/2) {
                            System.out.println("Error_receiveData:" + receiveData);
                            System.out.println(getCharByInt(receiveData, 3) + " " + receiveData.length());
                            Toast.makeText(MainActivity.this, "接收数据不完整", Toast.LENGTH_SHORT).show();
                            new FileWrite(receiveData);
                            receiveData = "";
                        }
                    }
                    downloadFileFragment.receiveDataDeal(receiveData);
                    if (tabFlag == 0) {
                        if(tabBottomFlag==0)
                            sensorFragment20.receiveDataDeal(receiveData);
                        else if(tabBottomFlag==1)
                            sensorFragment40.receiveDataDeal(receiveData);
                    }else if (tabFlag == 1) {
                        if(tabBottomFlag==0)
                            netGateFragment20.receiveDataDealNet(receiveData);
                        else if(tabBottomFlag==1)
                            netGateFragment40.receiveDataDealNet(receiveData);
                    }
                    if(tabBottomFlag==2)
                        sensorFragmentNBNew.receiveDataDeal(receiveData);
                    if(tabBottomFlag==4)
                        sensorFragmentNB.receiveDataDeal(receiveData);
                    displayData(tabFlag, receiveData);
                }
                receiveData = "";//处理完后将其置空
            }
        }
    }

    private void receiveDataPark(String string) {
        System.out.println("string:"+string);
        if(dis_flag) {
            dis_flag_1 = 1;
            parkLenDe=0;
        }
        if(getCharByInt(string,0)==0xff&&getCharByInt(string,1)==0xfe&&dis_flag){
            dis_flag_1=2;
            parkLen=getCharByInt(string,3);
            if(getCharByInt(string,2)==0x61)
                parkLen=getCharByInt(string,3)<<8|getCharByInt(string,4);


        }
        parkLenDe += string.length();
//        System.out.println("parkLen:"+parkLen);
//        System.out.println("parkLenDe:"+parkLenDe);
        if(parkLenDe/2<parkLen) {
            dis_flag = false;
            receiveData+=string;
        }else if(parkLenDe/2==parkLen){
            if(getCharByInt(string,string.length()/2-1)==0xaa) {
                //System.out.println("receiveData ELSE:"+receiveData);
                receiveData+=string;
                dis_flag = true;
                parkLenDe=0;
                parkLen=0;    //置空
            }
        }else if(parkLenDe/2>parkLen) {//字母里混杂16进制数据时，清空数据
            //System.out.println("receiveData last:" + receiveData);
            receiveData ="";
            dis_flag = true;
            parkLenDe = 0;
            parkLen = 0;    //置空
        }
        //System.out.println("receiveData:"+receiveData);
    }


    private String addBlank(String string){
        StringBuilder sb=new StringBuilder();
        sb.append(string);
        for(int i=2;i<sb.length();i+=3) {
            sb.insert(i, " ");
        }
        sb.insert(sb.length(), " ");
        string=sb.toString();
        return string;
    }
    private void displayData(final int flag,String rev_string) {
        DateFormat df = new SimpleDateFormat("HH:mm:ss yyyy年MM月dd日");
        Date curDate =  new Date(System.currentTimeMillis());//获取当前时间
        String timeStr =  df.format(curDate);
        rev_str =rev_str+ addBlank(rev_string.toUpperCase())+ "\n"+ timeStr +"\n"+"\n";
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
        if (flag == 0) {
            if(tabBottomFlag==0) {
                sensorFragment20.tvContent.setText(rev_str);
                sensorFragment20.svContent.scrollTo(0, sensorFragment20.tvContent.getMeasuredHeight());
            }else if(tabBottomFlag==1) {
                sensorFragment40.tvContent.setText(rev_str);
                sensorFragment40.svContent.scrollTo(0, sensorFragment40.tvContent.getMeasuredHeight());
            }
        } else if (flag == 1) {
            if(tabBottomFlag==0) {
                netGateFragment20.tvContentNet.setText(rev_str);
                netGateFragment20.svContentNet.scrollTo(0, netGateFragment20.tvContentNet.getMeasuredHeight());
            }else if(tabBottomFlag==1) {
                netGateFragment40.tvContentNet.setText(rev_str);
                netGateFragment40.svContentNet.scrollTo(0, netGateFragment40.tvContentNet.getMeasuredHeight());
            }
        }
        if(tabBottomFlag==2){
            sensorFragmentNBNew.tvContent.setText(rev_str);
            sensorFragmentNBNew.svContent.scrollTo(0, sensorFragmentNBNew.tvContent.getMeasuredHeight());
        }
        if(tabBottomFlag==3){
            sensorFragmentNBOld.tvContent.setText(rev_str);
            sensorFragmentNBOld.svContent.scrollTo(0, sensorFragmentNBOld.tvContent.getMeasuredHeight());
        }
        if(tabBottomFlag==4){
            sensorFragmentNB.tvContent.setText(rev_str);
            sensorFragmentNB.svContent.scrollTo(0, sensorFragmentNB.tvContent.getMeasuredHeight());
        }
//            }
//        });
    }
    private String transfer(String string){
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<string.length()/2;i++){
            char c=(char)getCharByInt(string,i);
            sb.append(c);
        }
        return sb.toString();
    }

    private void displayDataByString(final int flag,String string){
        rev_str += transfer(string) +"";
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
        if(tabBottomFlag==2){
            sensorFragmentNBNew.tvContent.setText(rev_str);
            sensorFragmentNBNew.svContent.scrollTo(0, sensorFragmentNBNew.tvContent.getMeasuredHeight());
        }
        if(tabBottomFlag==3){
            sensorFragmentNBOld.tvContent.setText(rev_str);
            sensorFragmentNBOld.svContent.scrollTo(0, sensorFragmentNBOld.tvContent.getMeasuredHeight());
        }
        if(tabBottomFlag==4){
            sensorFragmentNB.tvContent.setText(rev_str);
            sensorFragmentNB.svContent.scrollTo(0, sensorFragmentNB.tvContent.getMeasuredHeight());
        }
        if (flag == 0) {
            sensorFragment20.tvContent.setText(rev_str);
            sensorFragment20.svContent.scrollTo(0, sensorFragment20.tvContent.getMeasuredHeight());
        } else if (flag == 1) {
            netGateFragment20.tvContentNet.setText(rev_str);
            netGateFragment20.svContentNet.scrollTo(0, netGateFragment20.tvContentNet.getMeasuredHeight());
        }
        if (flag == 0) {
            sensorFragment40.tvContent.setText(rev_str);
            sensorFragment40.svContent.scrollTo(0, sensorFragment40.tvContent.getMeasuredHeight());
        } else if (flag == 1) {
            netGateFragment40.tvContentNet.setText(rev_str);
            netGateFragment40.svContentNet.scrollTo(0, netGateFragment40.tvContentNet.getMeasuredHeight());
        }
//            }
//        });
    }



    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(MyService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(MyService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(MyService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {

        if (gattServices == null)
            return;
        String uuid = null;
        String unknownServiceString = "unknown_service";
        String unknownCharaString = "unknown_characteristic";


        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();

        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {

            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            System.out.println("Service uuid:" + uuid);

            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();

            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();

            ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();

                if (gattCharacteristic.getUuid().toString().equals(HEART_RATE_MEASUREMENT)) {
                    mhandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mService.readCharacteristic(gattCharacteristic);
                        }
                    }, 200);

                    mService.setCharacteristicNotification(gattCharacteristic, true);
                    target_chara = gattCharacteristic;
                    //mService.writeCharacteristic(gattCharacteristic);
                }
                List<BluetoothGattDescriptor> descriptors = gattCharacteristic.getDescriptors();
                for (BluetoothGattDescriptor descriptor : descriptors) {
                    System.out.println("---descriptor UUID:" + descriptor.getUuid());
                    mService.getCharacteristicDescriptor(descriptor);
                    // mBluetoothLeService.setCharacteristicNotification(gattCharacteristic,true);
                }

                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unbindService(mServiceConnection);
        unregisterReceiver(mGattUpdateReceiver);
        if(localBroadcastManager!=null){
            localBroadcastManager.unregisterReceiver(myReceiver);
        }
        mService.close();
        mService.disconnect();
        mService.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mService != null) {
            final boolean result = mService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                //判断是否开启了蓝牙，如果是关闭状态则打开蓝牙
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (adapter == null || !adapter.isEnabled()) {
                    startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
                } else if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Toast.makeText(getApplicationContext(), "请打开手机GPS定位功能配合蓝牙操作！", Toast.LENGTH_SHORT).show();
                } else
                    startActivityForResult(new Intent(MainActivity.this, BlueTooth_Devices.class), 0);
                break;
            case R.id.btn_disconnect:
                unbindService(mServiceConnection);
                unregisterReceiver(mGattUpdateReceiver);
                mService.close();
                mService.disconnect();
                mService = null;
                btnSearch.setText("重新连接");
                sensorFragment20.setState(false);
                netGateFragment20.setState(false);
                sensorFragment40.setState(false);
                netGateFragment40.setState(false);
                sensorFragmentNBNew.setState(false);
                sensorFragmentNBOld.setState(false);
                sensorFragmentNB.setState(false);
                btnDisconnect.setEnabled(false);
                break;
            case R.id.tv_tab1:
                if(tabBottomFlag==0)
                    initViewPage(0);
                else if(tabBottomFlag==1)
                    initViewPage(2);
                tabFlag = 0;
                tvTab1.setBackgroundColor(Color.parseColor("#62CC74"));
                tvTab2.setBackgroundColor(Color.parseColor("#969696"));
                break;
            case R.id.tv_tab2:
                if(tabBottomFlag==0)
                    initViewPage(1);
                else if(tabBottomFlag==1)
                    initViewPage(3);
                tabFlag=1;
                tvTab1.setBackgroundColor(Color.parseColor("#969696"));
                tvTab2.setBackgroundColor(Color.parseColor("#62CC74"));
                break;
            case R.id.RL_login:

                break;
            case R.id.RL_register:

                break;
            case R.id.menu_layout_change_main:
                final int[] index = {0};
                android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(this);
                String[] softwareStr=new String[]{"   2.0配置工具","   4.0配置工具","   NB配置工具(长唯一ID)","   NB旧配置工具","   NB配置工具（短唯一ID）"};
                builder.setTitle("更改配置软件")
                        .setItems(softwareStr, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mMenuWidth=mMenu.getmScreenWidth()-mMenu.getmMenuRightPadding();
                                 switch (i){
                                     case 0:
                                         mMenu.smoothScrollTo(mMenuWidth, 0);
                                         tabTopLayout1.setVisibility(View.VISIBLE);
                                         tabTopLayout2.setVisibility(View.GONE);
                                         tabFlag = 0;
                                         tvTab1.setText("2.0车位检测器");
                                         tvTab2.setText("2.0车位网关");
                                         tvTab1.setBackgroundColor(Color.parseColor("#62CC74"));
                                         tvTab2.setBackgroundColor(Color.parseColor("#969696"));
                                         initViewPage(0);
                                         tabBottomFlag=0;
                                         Toast.makeText(getApplicationContext(), "已打开2.0配置工具", Toast.LENGTH_SHORT).show();
                                         break;
                                     case 1:
                                         mMenu.smoothScrollTo(mMenuWidth, 0);
                                         tabTopLayout1.setVisibility(View.VISIBLE);
                                         tabTopLayout2.setVisibility(View.GONE);
                                         tabFlag = 0;
                                         tvTab1.setText("4.0车位检测器");
                                         tvTab2.setText("4.0车位网关");
                                         tvTab1.setBackgroundColor(Color.parseColor("#62CC74"));
                                         tvTab2.setBackgroundColor(Color.parseColor("#969696"));
                                         initViewPage(2);
                                         tabBottomFlag=1;
                                         Toast.makeText(getApplicationContext(), "已打开4.0配置工具", Toast.LENGTH_SHORT).show();
                                         break;
                                     case 2:
                                         mMenu.smoothScrollTo(mMenuWidth, 0);
                                         tabTopLayout1.setVisibility(View.GONE);
                                         tabTopLayout2.setVisibility(View.VISIBLE);
                                         tvTab3.setText("NB地磁配置");
                                         initViewPage(4);
                                         tabBottomFlag=2;
                                         Toast.makeText(getApplicationContext(), "已打开NB配置工具", Toast.LENGTH_SHORT).show();
                                         break;
                                     case 3:
                                         mMenu.smoothScrollTo(mMenuWidth, 0);
                                         tabTopLayout1.setVisibility(View.GONE);
                                         tabTopLayout2.setVisibility(View.VISIBLE);
                                         tvTab3.setText("NB旧地磁配置");
                                         initViewPage(5);
                                         tabBottomFlag=3;
                                         Toast.makeText(getApplicationContext(), "已打开NB旧配置工具", Toast.LENGTH_SHORT).show();
                                         break;
                                     case 4:
                                         mMenu.smoothScrollTo(mMenuWidth, 0);
                                         tabTopLayout1.setVisibility(View.GONE);
                                         tabTopLayout2.setVisibility(View.VISIBLE);
                                         tvTab3.setText("NB最新地磁配置");
                                         initViewPage(6);
                                         tabBottomFlag=4;
                                         Toast.makeText(getApplicationContext(), "已打开NB最新配置工具", Toast.LENGTH_SHORT).show();
                                         break;
                                 }
                            }
                        })
                .setNegativeButton("取消",null)
                .setCancelable(false)
                .create().show();
                break;

            case R.id.menu_layout_about:
                startActivity(new Intent(this,AboutActivity.class));
                break;


            default:
                break;
        }
    }

    //获取是否存在NavigationBar
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;

    }



}



