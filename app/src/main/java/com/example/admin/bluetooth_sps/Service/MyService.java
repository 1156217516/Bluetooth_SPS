package com.example.admin.bluetooth_sps.Service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.example.admin.bluetooth_sps.Message.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

/**
 * Created by admin on 2017/9/5.
 */
public class MyService extends Service{
    private final static String TAG = "MyService";
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";


    // Implements callback methods for GATT events that the app cares about. For example,
    // connection change and services discovered.
    public interface OnDataAvailableListener {
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);
    }

    private OnDataAvailableListener mOnDataAvailableListener;
    public void setOnDataAvailableListener(OnDataAvailableListener l)
    {
        mOnDataAvailableListener = l;
    }


    /* 连接远程设备的回调函数 */
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                /* 通过广播更新连接状态 */
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED)//连接成功
            {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        /*
         * 重写onServicesDiscovered，发现蓝牙服务
         * */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS)//发现到服务
            {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                Log.i(TAG, "--onServicesDiscovered called--");
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
                System.out.println("onServicesDiscovered received: " + status);
            }
        }

        /*
         * 特征值的读写
         * */
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
        {
            Log.w(TAG, "onCharacteristicRead");
            if (status == BluetoothGatt.GATT_SUCCESS)
            {
                Log.i(TAG, "--onCharacteristicRead called--");
                //从特征值读取数据
                //byte[] sucString = characteristic.getValue();
                //String string = new String(sucString);
                //System.out.println("MyService:"+string);
                //将数据通过广播到Ble_Activity
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }
        /*
         *   特征值的改变
         * */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
        {
            final byte[] data = characteristic.getValue();
            String string = bytesToHexString(data);
            Log.w(TAG, "onCharacteristicChanged--------"+string);
            EventBus.getDefault().post(new MessageEvent(string));
            //broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
        /*
         * 特征值的写
         * */
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
        {
            Log.w(TAG, "--onCharacteristicWrite--: " + status);
            // 以下语句实现 发送完数据或也显示到界面上
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
        /*
         * 读描述值
         * */
        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
        {
            // super.onDescriptorRead(gatt, descriptor, status);
            Log.w(TAG, "----onDescriptorRead status: " + status);
            byte[] desc = descriptor.getValue();
            if (desc != null)
            {
                Log.w(TAG, "----onDescriptorRead value: " + new String(desc));
            }
        }
        /*
         * 写描述值
         * */
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
        {
            // super.onDescriptorWrite(gatt, descriptor, status);
            Log.w(TAG, "--onDescriptorWrite--: " + status);
        }
        /*
         * 读写蓝牙信号值
         * */
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status)
        {
            // super.onReadRemoteRssi(gatt, rssi, status);
            Log.w(TAG, "--onReadRemoteRssi--: " + status);
            broadcastUpdate(ACTION_DATA_AVAILABLE, rssi);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status)
        {
            // super.onReliableWriteCompleted(gatt, status);
            Log.w(TAG, "--onReliableWriteCompleted--: " + status);
        }

    };


    //广播意图
    private void broadcastUpdate(final String action, int rssi)
    {
        final Intent intent = new Intent(action);
        intent.putExtra(EXTRA_DATA, String.valueOf(rssi));
        sendBroadcast(intent);
    }
    //广播意图
    private void broadcastUpdate(final String action)
    {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }
    /* 广播远程发送过来的数据 */
    public void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic)
    {
        final Intent intent = new Intent(action);
        //从特征值获取数据
        final byte[] data = characteristic.getValue();
        String string = bytesToHexString(data);
        Log.w(TAG, "broadcastUpdate"+string);
        intent.putExtra(EXTRA_DATA, string);
        sendBroadcast(intent);
    }


    public class LocalBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    public boolean initialize()
    {
        if (mBluetoothManager == null)
        {   //获取系统的蓝牙管理器
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null)
            {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null)
        {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }

    // 连接远程蓝牙
    public boolean connect(final String address)
    {
        if (mBluetoothAdapter == null || address == null)
        {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device. Try to reconnect.
        if (mBluetoothDeviceAddress != null
                && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null)
        {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect())//连接蓝牙，其实就是调用BluetoothGatt的连接方法
            {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else
            {
                return false;
            }
        }
		/* 获取远端的蓝牙设备 */
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null)
        {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the
        // autoConnect
        // parameter to false.
		/* 调用device中的connectGatt连接到远程设备 */
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        System.out.println("device.getBondState==" + device.getBondState());
        return true;
    }

    /**
     取消蓝牙连接
     */
    public void disconnect()
    {
        if (mBluetoothAdapter == null || mBluetoothGatt == null)
        {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }
    /**
     关闭所有蓝牙连接
     */
    public void close()
    {
        if (mBluetoothGatt == null)
        {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public void readCharacteristic(BluetoothGattCharacteristic characteristic)
    {
        Log.w(TAG, "readCharacteristic");
        if (mBluetoothAdapter == null || mBluetoothGatt == null)
        {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    private Queue<byte[]> dataInfoQueue = new LinkedList<>();
    private BluetoothGattCharacteristic characteristic;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            send();
        }
    };

    private void send() {
        if (dataInfoQueue != null && !dataInfoQueue.isEmpty()) {
            //检测到发送数据，直接发送
            if (dataInfoQueue.peek() != null) {
                this.characteristic.setValue(dataInfoQueue.poll());//移除并返回队列头部的元素
                mBluetoothGatt.writeCharacteristic(this.characteristic);
            }
            //检测还有数据，延时后继续发送，一般延时100毫秒左右
            if (dataInfoQueue.peek() != null) {
                handler.postDelayed(runnable, 100);

            }
        }
    }

    // 写入特征值
    public void writeCharacteristic(BluetoothGattCharacteristic characteristic)
    {
        Log.w(TAG, "writeCharacteristic");
        this.characteristic=characteristic;
        if (mBluetoothAdapter == null || mBluetoothGatt == null)
        {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        if (dataInfoQueue != null) {
            dataInfoQueue.clear();
            dataInfoQueue = splitPacketFor20Byte(characteristic.getValue());
            handler.post(runnable);
        }
        //mBluetoothGatt.writeCharacteristic(characteristic);
    }


    //数据分包处理 超过20字节需要分包
    private Queue<byte[]> splitPacketFor20Byte(byte[] data) {
        Queue<byte[]> dataInfoQueue = new LinkedList<>();
        if (data != null) {
            int index = 0;
            do {
                byte[] surplusData = new byte[data.length - index];
                byte[] currentData;
                System.arraycopy(data, index, surplusData, 0, data.length - index);
                if (surplusData.length <= 20) {
                    currentData = new byte[surplusData.length];
                    System.arraycopy(surplusData, 0, currentData, 0, surplusData.length);
                    index += surplusData.length;
                } else {
                    currentData = new byte[20];
                    System.arraycopy(data, index, currentData, 0, 20);
                    index += 20;
                }
                dataInfoQueue.offer(currentData);
            } while (index < data.length);
        }
        return dataInfoQueue;
    }



    // 读取RSSi
    public void readRssi()
    {
        if (mBluetoothAdapter == null || mBluetoothGatt == null)
        {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readRemoteRssi();
    }


    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled)
    {
        if (mBluetoothAdapter == null || mBluetoothGatt == null)
        {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        BluetoothGattDescriptor clientConfig = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));

        if (enabled)
        {
            clientConfig.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        } else
        {
            clientConfig.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        }
        mBluetoothGatt.writeDescriptor(clientConfig);
    }


    public void getCharacteristicDescriptor(BluetoothGattDescriptor descriptor)
    {
        if (mBluetoothAdapter == null || mBluetoothGatt == null)
        {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        mBluetoothGatt.readDescriptor(descriptor);
    }


    public List<BluetoothGattService> getSupportedGattServices()
    {
        if (mBluetoothGatt == null)
            return null;
        return mBluetoothGatt.getServices();

    }

    //byte转16进制字符串
    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }



}

