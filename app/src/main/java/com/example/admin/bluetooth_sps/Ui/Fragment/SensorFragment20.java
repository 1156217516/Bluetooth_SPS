package com.example.admin.bluetooth_sps.Ui.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.bluetooth_sps.R;
import com.example.admin.bluetooth_sps.Utils.RegionNumberEditTextUtils;
import com.example.admin.bluetooth_sps.Ui.Activity.MainActivity;

import static com.example.admin.bluetooth_sps.Utils.MyUtils.Exclusive_Or;
import static com.example.admin.bluetooth_sps.Utils.MyUtils.getCharByInt;


/**
 * Created by admin on 2017/11/6.
 */
public class SensorFragment20 extends Fragment implements View.OnClickListener{
    private View view;
    private Button btnWakeConfig;
    private Button btnReadConfig;
    private Button btnWriteConfig;
    private Button btnReset;
    private Button btnActive;
    private Button btnRecovery;
    private Button btnEndConfig;
    private Button clear;
    private Button btnEnd;
    private Button btnUpdate;

    private EditText etSensorId;
    private EditText etGateId;
    private EditText etStopTime;
    private EditText etGapTime;

    private TextView tvSelectFrequency;
    private TextView tvActiveStatus;
    private TextView tvFName;
    private Spinner spinAgility;
    private Spinner spinCarPut;


    public TextView tvContent;
    public ScrollView svContent;

    public long exitTime = 0;
    public boolean isLongPressKey;  //检测按钮是否长按

    private String cat_put_select;
    private int oldSensorID = 1;
    private boolean SensorFirstFlag = true;
    private boolean flag=true;   //检测第几次长按按钮
    private int frequencyIndex=0;
    private String agility;
    private DownloadFileFragment downloadFileFragment;
    private MainActivity mainActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sensor_fragment20, container,false);
        mainActivity = (MainActivity) getActivity();
        downloadFileFragment=new DownloadFileFragment();
        initView();
        initEvent();
        setState(false);

        btnUpdate.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isLongPressKey = true;
                Log.d("isLongPressKey:", "" + isLongPressKey);
                if (flag) {
                    mainActivity.initViewPage(-1);
                    mainActivity.initViewPage(0);
                    flag = false;
                } else {
                    mainActivity.hideDownloadFileFragment();
                    flag = true;
                }
                return true;
            }
        });

        spinCarPut.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cat_put_select = String.valueOf(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinAgility.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                agility = String.valueOf(position*10);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        return view;
    }
    private void initView() {
        btnWakeConfig = (Button) view.findViewById(R.id.wake_config);
        btnReadConfig = (Button) view.findViewById(R.id.read_config);
        btnWriteConfig = (Button) view.findViewById(R.id.write_config);
        btnReset = (Button) view.findViewById(R.id.reset);
        btnActive = (Button) view.findViewById(R.id.active);
        btnRecovery = (Button) view.findViewById(R.id.recovery);
        btnEndConfig = (Button) view.findViewById(R.id.end_config);
        clear = (Button) view.findViewById(R.id.clear);
        btnEnd = (Button) view.findViewById(R.id.end);
        btnUpdate = (Button) view.findViewById(R.id.btnUpdate20);
        tvSelectFrequency = (TextView) view.findViewById(R.id.tvSelectFrequency);
        tvContent = (TextView) view.findViewById(R.id.tv_content);
        tvActiveStatus = (TextView) view.findViewById(R.id.active_status);
        tvFName = (TextView) view.findViewById(R.id.tv_FName);
        etSensorId = (EditText) view.findViewById(R.id.sensor_id);
        etGateId = (EditText) view.findViewById(R.id.gateway_id);
        etStopTime = (EditText) view.findViewById(R.id.stop_time);
        etGapTime = (EditText) view.findViewById(R.id.gap_time);
        svContent = (ScrollView) view.findViewById(R.id.sv_content);
        spinCarPut = (Spinner) view.findViewById(R.id.car_put);
        spinAgility = (Spinner) view.findViewById(R.id.lingMin);
        new RegionNumberEditTextUtils(getContext(),etSensorId,0);
        new RegionNumberEditTextUtils(getContext(),etGateId,0);
        new RegionNumberEditTextUtils(getContext(),etStopTime,2);
        new RegionNumberEditTextUtils(getContext(),etGapTime,3);
    }
    private void initEvent() {
        tvSelectFrequency.setOnClickListener(this);
        btnWakeConfig.setOnClickListener(this);
        btnReadConfig.setOnClickListener(this);
        btnWriteConfig.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        btnActive.setOnClickListener(this);
        btnRecovery.setOnClickListener(this);
        btnEndConfig.setOnClickListener(this);
        clear.setOnClickListener(this);
        btnEnd.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
    }

    public void setState(boolean b) {
        tvContent.setEnabled(b);
        tvActiveStatus.setEnabled(b);
        etSensorId.setEnabled(b);
        etGateId.setEnabled(b);
        etStopTime.setEnabled(b);
        etGapTime.setEnabled(b);
        svContent.setEnabled(b);
        spinAgility.setEnabled(b);
        spinCarPut.setEnabled(b);
        tvSelectFrequency.setEnabled(b);
        btnWakeConfig.setEnabled(b);
        btnReadConfig.setEnabled(b);
        btnWriteConfig.setEnabled(b);
        btnUpdate.setEnabled(b);
        btnReset.setEnabled(b);
        btnActive.setEnabled(b);
        clear.setEnabled(b);
        btnRecovery.setEnabled(b);
        btnEndConfig.setEnabled(b);
    }

    public void receiveDataDeal(String string) {
        //ff fe开头
        if(string!=null&&getCharByInt(string,0)==0xff&&getCharByInt(string,1)==0xfe) {
            System.out.println("帧标识：" + String.valueOf(getCharByInt(string, 2)));
            if (downloadFileFragment.openToastFlag) {
                System.out.print("openToastFlag:"+downloadFileFragment.openToastFlag);
                switch (getCharByInt(string, 2)){
                    case 0x02:;
                        if(getCharByInt(string, 10)==0x00){
                            Toast.makeText(getActivity(), "接收到无车数据包", Toast.LENGTH_SHORT).show();
                        }else if(getCharByInt(string, 10)==0x01){
                            Toast.makeText(getActivity(), "接收到有车数据包", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 0x04://0x04
                        if (string.charAt(18) == '0' && string.charAt(19) == '1')
                            tvActiveStatus.setText("已激活");
                        else
                            tvActiveStatus.setText("未激活");
                        etSensorId.setText(String.valueOf(getCharByInt(string, 5)));
                        Toast.makeText(getActivity(), "接收到唤醒回复 请立即点击读取", Toast.LENGTH_SHORT).show();
                        break;
                    case 0x05://0x05
                        Toast.makeText(getActivity(), "配置超时,请重新点击唤醒并读取", Toast.LENGTH_SHORT).show();
                        break;
                    case 42://0x2a
                        Toast.makeText(getActivity(), "读取成功", Toast.LENGTH_SHORT).show();
                        oldSensorID = getCharByInt(string, 10);
                        etSensorId.setText(String.valueOf(getCharByInt(string, 10)));
                        etGateId.setText(String.valueOf(getCharByInt(string, 11)));
                        etStopTime.setText(String.valueOf(getCharByInt(string, 12)));
                        etGapTime.setText(String.valueOf(getCharByInt(string, 13)));
                        if(getCharByInt(string, 8)==0x00)
                            spinAgility.setSelection(0);
                        else if(getCharByInt(string, 8)==0x0A)
                            spinAgility.setSelection(1);
                        else
                            spinAgility.setSelection(2);
                        if (string.charAt(19) == '0')
                            spinCarPut.setSelection(0);
                        else if (string.charAt(19) == '1')
                            spinCarPut.setSelection(1);
                        break;
                    case 43://0x2b
                        Toast.makeText(getActivity(), "配置成功", Toast.LENGTH_SHORT).show();
                        break;
                    case 46://0x2e
                        Toast.makeText(getActivity(), "恢复出厂设置成功", Toast.LENGTH_SHORT).show();
                        break;
                    case 60://0x3c
                        Toast.makeText(getActivity(), "重启成功", Toast.LENGTH_SHORT).show();
                        break;
                    case 58://0x3a
                        Toast.makeText(getActivity(), "退出配置成功", Toast.LENGTH_SHORT).show();
                        break;
                    case 59://0x3b
                        tvActiveStatus.setText("已激活");
                        Toast.makeText(getActivity(), "激活成功", Toast.LENGTH_SHORT).show();
                        break;
                    case 100://0x64
                        if(getCharByInt(string, 5)==0x01){
                            final int[] index = {0};
                            String[] frequencyStr=new String[]{"480M","479M","478M","477M"};
                            AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                            builder.setTitle("请在10秒内选择升级频点")
                                    .setSingleChoiceItems(frequencyStr, 0, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            index[0] = which;
                                        }
                                    });
                            builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (index[0]) {
                                        case 0:
                                            setFrequency(0);
                                            break;
                                        case 1:
                                            setFrequency(1);
                                            break;
                                        case 2:
                                            setFrequency(2);
                                            break;
                                        case 3:
                                            setFrequency(3);
                                            break;
                                        default:
                                            break;
                                    }
                                    dialog.dismiss();
                                }
                            });
                            //builder.setPositiveButton("取消",null);
                            builder.create().show();
                        }
                        break;
                    case 102://0x66
                        String revFileNameString="";
                        downloadFileFragment.totalNum = getCharByInt(string, 4);
                        if(getCharByInt(string, 3)!=0x08) {
                            revFileNameString
                                    = (char) getCharByInt(string, 5) + ""
                                    + (char) getCharByInt(string, 6) + ""
                                    + (char) getCharByInt(string, 7) + ""
                                    + (char) getCharByInt(string, 8) + ""
                                    + (char) getCharByInt(string, 9) + ""
                                    + (char) getCharByInt(string, 10) + ""
                                    + (char) getCharByInt(string, 11) + ""
                                    + (char) getCharByInt(string, 12) + ""
                                    + (char) getCharByInt(string, 13) + ""
                                    + (char) getCharByInt(string, 14) + ""
                                    + (char) getCharByInt(string, 15) + ""
                                    + (char) getCharByInt(string, 16) + ""
                                    + (char) getCharByInt(string, 17) + "";
                        }
                        tvFName.setText("配置工具程序："+"\n"+revFileNameString);
                        break;
                    case 135://0x87
                        switch (getCharByInt(string, 4)){
                            case 0x00:
                                Toast.makeText(getActivity(), "已设定为默认480频点进行升级", Toast.LENGTH_SHORT).show();
                                break;
                            case 0x01:
                                Toast.makeText(getActivity(), "已设定为479频点进行升级", Toast.LENGTH_SHORT).show();
                                break;
                            case 0x02:
                                Toast.makeText(getActivity(), "已设定为478频点进行升级", Toast.LENGTH_SHORT).show();
                                break;
                            case 0x03:
                                Toast.makeText(getActivity(), "已设定为477频点进行升级", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                break;
                        }
                        break;
                }
            }
        }
    }

    private void setFrequency(int index) {
        byte[] sendBuff = new byte[10];
        sendBuff[0] = (byte) 0xff;
        sendBuff[1] = (byte) 0xfe;
        sendBuff[2] = (byte) 0x67;
        sendBuff[3] = (byte) 0x0a;
        sendBuff[4] = (byte) index;
        sendBuff[5] = (byte) 0x00;
        sendBuff[6] = (byte) 0x00;
        sendBuff[7] = Exclusive_Or(sendBuff, 7);
        sendBuff[8] = (byte) 0xab;
        sendBuff[9] = (byte) 0xaa;
        mainActivity.target_chara.setValue(sendBuff);
        mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
    }

    private void selectFrequency(int data1,int data2,int data3) {
        byte[] sendData = new byte[16];
        sendData[0]=(byte)0xFF;
        sendData[1]=(byte)0xFE;
        sendData[2]=(byte)0xB0;
        sendData[3]=(byte)0x10;
        sendData[4]=(byte)data1;
        sendData[5]=(byte)data2;
        sendData[6]=(byte)data3;
        sendData[7]=(byte)0x00;
        sendData[8]=(byte)0x00;
        sendData[9]=(byte)0x00;
        sendData[10]=(byte)0x00;
        sendData[11]=(byte)0x00;
        sendData[12]=(byte)0x00;
        sendData[13]=Exclusive_Or(sendData, (16 - 3));
        sendData[14]=(byte)0xAB;
        sendData[15]=(byte)0xAA;
        mainActivity.target_chara.setValue(sendData);
        mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
    }




    //十六进制字符串转为byte
    public byte[] hexString2Bytes(String hex) {

        if ((hex == null) || (hex.equals(""))) {
            return null;
        } else if (hex.length() % 2 != 0) {
            return null;
        } else {
            hex = hex.toUpperCase();
            int len = hex.length() / 2;
            byte[] b = new byte[len];
            char[] hc = hex.toCharArray();
            for (int i = 0; i < len; i++) {
                int p = 2 * i;
                b[i] = (byte) (toByte(hc[p]) << 4 | toByte(hc[p + 1]));
            }
            return b;
        }
    }

    //字符转为byte
    private byte toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }

    public static String bytesToHexString(byte[] src) {
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

    //    //16进制字符串转为字符串
//    public String hex2String(String hex) throws UnsupportedEncodingException {
//        String r = bytes2String(hexString2Bytes(hex));
//        return r;
//    }
//
//    //字节数组转字符串
//    public static String bytes2String(byte[] b){
//        String r = null;
//        try {
//            r = new String (b,"UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        return r;
//    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvSelectFrequency:
                final int data2=0x7F,data3=0x40;
                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

                String[] frequencyStr=new String[]{"470M默认频点","471M","472M生产测试专用","473M生产测试专用","474M生产测试专用"
                                                    ,"475测试专用","476测试专用","477M升级","478M升级","479M升级","480M升级"
                                                    ,"481测试专用","481.2测试专用","481.4测试专用","481.6测试专用","481.8测试专用"
                                                    ,"482测试专用","482.2测试专用","482.4测试专用","481.6测试专用"};
                builder.setTitle("选择频点")
                        .setSingleChoiceItems(frequencyStr, frequencyIndex, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                frequencyIndex=which;
                            }
                        });
                builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (frequencyIndex) {
                            case 0:selectFrequency(0x00, 0x36, 0x08);break;//470M
                            case 1:selectFrequency(0x05, 0x36, 0x08);break;//471M
                            case 2:selectFrequency(0x0A, 0x36, 0x08);break;//472M
                            case 3:selectFrequency(0x0F, 0x36, 0x08);break;//473M
                            case 4:selectFrequency(0x14, 0x36, 0x08);break;//474M
                            case 5:selectFrequency(0x19, 0x36, 0x08);break;//475M
                            case 6:selectFrequency(0x1E, 0x36, 0x08);break;//476M
                            case 7:selectFrequency(0x23, data2, data3);break;//477M
                            case 8:selectFrequency(0x28, data2, data3);break;//478M
                            case 9:selectFrequency(0x2D, data2, data3);break;//479M
                            case 10:selectFrequency(0x3F, data2, data3);break;//480M
                            case 11:selectFrequency(0x37, 0x36, 0x08);break;//481M
                            case 12:selectFrequency(0x38, 0x36, 0x08);break;//481.2M
                            case 13:selectFrequency(0x39, 0x36, 0x08);break;//481.4M
                            case 14:selectFrequency(0x3A, 0x36, 0x08);break;//481.6M
                            case 15:selectFrequency(0x3B, 0x36, 0x08);break;//481.8M
                            case 16:selectFrequency(0x3C, 0x36, 0x08);break;//482M
                            case 17:selectFrequency(0x3D, 0x36, 0x08);break;//482.2M
                            case 18:selectFrequency(0x3E, 0x36, 0x08);break;//482.4M
                            case 19:selectFrequency(0x3F, 0x36, 0x08);break;//482.6M
                            default:
                                break;
                        }
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("取消",null);
                builder.create().show();
                break;
            case R.id.wake_config:
                if(!downloadFileFragment.openToastFlag){
                    Toast.makeText(getActivity(), "正在写入，请等待完成后再操作！", Toast.LENGTH_SHORT).show();
                    return;
                }
                String string = "FFFEA00800A9ABAA";
                mainActivity.target_chara.setValue(hexString2Bytes(string));
                mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
                break;
            case R.id.read_config:
                if(!downloadFileFragment.openToastFlag){
                    Toast.makeText(getActivity(), "正在写入，请等待完成后再操作！", Toast.LENGTH_SHORT).show();
                    return;
                }
                byte[] writeBuffer = new byte[14];
                writeBuffer[0] = (byte) 0xff;
                writeBuffer[1] = (byte) 0xfe;
                writeBuffer[2] = (byte) 0x0a;
                writeBuffer[3] = (byte) 0x0e;
                writeBuffer[4] = (byte) 0x30;
                writeBuffer[5] = (byte) 0x30;
                writeBuffer[6] = (byte) 0x01;
                writeBuffer[7] = (byte) 0x00;
                writeBuffer[8] = (byte) 0x01;
                writeBuffer[9] = (byte) 0x30;
                writeBuffer[10] = (byte) 0x00;
                writeBuffer[11] = Exclusive_Or(writeBuffer, 11);
                writeBuffer[12] = (byte) 0xab;
                writeBuffer[13] = (byte) 0xaa;
                mainActivity.target_chara.setValue(writeBuffer);
                mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
                break;
            case R.id.write_config:
                if(!downloadFileFragment.openToastFlag){
                    Toast.makeText(getActivity(), "正在写入，请等待完成后再操作！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!SensorFirstFlag) {
                    Toast.makeText(getActivity(), "请先获取设备基本信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etSensorId.getText()) || TextUtils.isEmpty(etGateId.getText())
                        || TextUtils.isEmpty(etStopTime.getText()) || TextUtils.isEmpty(etGapTime.getText())) {
                    Toast.makeText(getActivity(), "请确定基本信息是否填写完整", Toast.LENGTH_SHORT).show();
                    return;
                }
                byte[] writeBuff = new byte[18];
                //System.out.println("oldSensorID："+oldSensorID);
                writeBuff[0] = (byte) 0xff;
                writeBuff[1] = (byte) 0xfe;
                writeBuff[2] = (byte) 0x0b;
                writeBuff[3] = (byte) 0x12;
                writeBuff[4] = (byte) oldSensorID;
                writeBuff[5] = (byte) 0x30;
                writeBuff[6] = (byte) 0x01;
                writeBuff[7] = (byte) 0x00;
                writeBuff[8] = (byte) 0x05;
                writeBuff[9] = (byte) Integer.parseInt(cat_put_select, 10);
                writeBuff[10] = (byte) Integer.parseInt(etSensorId.getText().toString(), 10);
                writeBuff[11] = (byte) Integer.parseInt(etGateId.getText().toString(), 10);
                writeBuff[12] = (byte) Integer.parseInt(etStopTime.getText().toString(), 10);
                writeBuff[13] = (byte) Integer.parseInt(etGapTime.getText().toString(), 10);
                writeBuff[14] = (byte) (byte)Integer.parseInt(agility,10);
                writeBuff[15] = Exclusive_Or(writeBuff, 15);
                writeBuff[16] = (byte) 0xab;
                writeBuff[17] = (byte) 0xaa;
                mainActivity.target_chara.setValue(writeBuff);
                mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
                break;
            case R.id.reset:
                if(!downloadFileFragment.openToastFlag){
                    Toast.makeText(getActivity(), "正在写入，请等待完成后再操作！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etSensorId.getText())) {
                    Toast.makeText(getActivity(), "传感器ID不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                writeBuffer = new byte[14];
                writeBuffer[0] = (byte) 0xff;
                writeBuffer[1] = (byte) 0xfe;
                writeBuffer[2] = (byte) 0x1c;
                writeBuffer[3] = (byte) 0x0e;
                writeBuffer[4] = (byte) Integer.parseInt(etSensorId.getText().toString(), 10);
                writeBuffer[5] = (byte) 0x30;
                writeBuffer[6] = (byte) 0x01;
                writeBuffer[7] = (byte) 0x00;
                writeBuffer[8] = (byte) 0x01;
                writeBuffer[9] = (byte) 0x30;
                writeBuffer[10] = (byte) 0x00;
                writeBuffer[11] = Exclusive_Or(writeBuffer, 11);
                writeBuffer[12] = (byte) 0xab;
                writeBuffer[13] = (byte) 0xaa;
                mainActivity.target_chara.setValue(writeBuffer);
                mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
                break;
            case R.id.active:
                if(!downloadFileFragment.openToastFlag){
                    Toast.makeText(getActivity(), "正在写入，请等待完成后再操作！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etSensorId.getText())) {
                    Toast.makeText(getActivity(), "传感器ID不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                writeBuffer = new byte[14];
                writeBuffer[0] = (byte) 0xff;
                writeBuffer[1] = (byte) 0xfe;
                writeBuffer[2] = (byte) 0x1b;
                writeBuffer[3] = (byte) 0x0e;
                writeBuffer[4] = (byte) Integer.parseInt(etSensorId.getText().toString(), 10);
                writeBuffer[5] = (byte) 0x30;
                writeBuffer[6] = (byte) 0x01;
                writeBuffer[7] = (byte) 0x00;
                writeBuffer[8] = (byte) 0x01;
                writeBuffer[9] = (byte) 0x30;
                writeBuffer[10] = (byte) 0x00;
                writeBuffer[11] = Exclusive_Or(writeBuffer, 11);
                writeBuffer[12] = (byte) 0xab;
                writeBuffer[13] = (byte) 0xaa;
                mainActivity.target_chara.setValue(writeBuffer);
                mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
                break;
            case R.id.recovery:
                if(!downloadFileFragment.openToastFlag){
                    Toast.makeText(getActivity(), "正在写入，请等待完成后再操作！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etSensorId.getText())) {
                    Toast.makeText(getActivity(), "传感器ID不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                writeBuffer = new byte[14];
                writeBuffer[0] = (byte) 0xff;
                writeBuffer[1] = (byte) 0xfe;
                writeBuffer[2] = (byte) 0x0e;
                writeBuffer[3] = (byte) 0x0e;
                writeBuffer[4] = (byte) Integer.parseInt(etSensorId.getText().toString(), 10);
                writeBuffer[5] = (byte) 0x30;
                writeBuffer[6] = (byte) 0x01;
                writeBuffer[7] = (byte) 0x00;
                writeBuffer[8] = (byte) 0x01;
                writeBuffer[9] = (byte) 0x30;
                writeBuffer[10] = (byte) 0x00;
                writeBuffer[11] = Exclusive_Or(writeBuffer, 11);
                writeBuffer[12] = (byte) 0xab;
                writeBuffer[13] = (byte) 0xaa;
                mainActivity.target_chara.setValue(writeBuffer);
                mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
                break;
            case R.id.end_config:
                if(!downloadFileFragment.openToastFlag){
                    Toast.makeText(getActivity(), "正在写入，请等待完成后再操作！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etSensorId.getText())) {
                    Toast.makeText(getActivity(), "传感器ID不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                writeBuffer = new byte[14];
                writeBuffer[0] = (byte) 0xff;
                writeBuffer[1] = (byte) 0xfe;
                writeBuffer[2] = (byte) 0x1a;
                writeBuffer[3] = (byte) 0x0e;
                writeBuffer[4] = (byte) Integer.parseInt(etSensorId.getText().toString(), 10);
                writeBuffer[5] = (byte) 0x30;
                writeBuffer[6] = (byte) 0x01;
                writeBuffer[7] = (byte) 0x00;
                writeBuffer[8] = (byte) 0x01;
                writeBuffer[9] = (byte) 0x30;
                writeBuffer[10] = (byte) 0x00;
                writeBuffer[11] = Exclusive_Or(writeBuffer, 11);
                writeBuffer[12] = (byte) 0xab;
                writeBuffer[13] = (byte) 0xaa;
                mainActivity.target_chara.setValue(writeBuffer);
                mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
                break;
            case R.id.btnUpdate20:
                if((System.currentTimeMillis()-exitTime) > 2000) {
                    Toast.makeText(getActivity(), "再按一次开始升级", Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                    return;
                }else if (downloadFileFragment.totalNum == 0) {
                    Toast.makeText(getActivity(), "升级文件总包数错误，请检查配置中文件是否正确！", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(TextUtils.isEmpty(etSensorId.getText())){
                    Toast.makeText(getActivity(), "传感器ID不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                writeBuffer = new byte[14];
                writeBuffer[0] = (byte) 0xff;
                writeBuffer[1] = (byte) 0xfe;
                writeBuffer[2] = (byte) 0x60;
                writeBuffer[3] = (byte) 0x0e;
                writeBuffer[4] = (byte) Integer.parseInt(etSensorId.getText().toString(), 10);
                writeBuffer[5] = (byte) 0x00;
                writeBuffer[6] = (byte) 0x01;
                writeBuffer[7] = (byte) 0x00;
                writeBuffer[8] = (byte) 0x01;
                writeBuffer[9] = (byte) downloadFileFragment.totalNum;
                writeBuffer[10] = (byte) 0x00;
                writeBuffer[11] = Exclusive_Or(writeBuffer, 11);
                writeBuffer[12] = (byte) 0xab;
                writeBuffer[13] = (byte) 0xaa;
                mainActivity.target_chara.setValue(writeBuffer);
                mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
                break;
            case R.id.clear:
                downloadFileFragment.openToastFlag=true;
                mainActivity.rev_str = "";
                tvContent.setText("");
                break;
            case R.id.end:
                if(!downloadFileFragment.openToastFlag){
                    Toast.makeText(getActivity(), "正在写入，请等待完成后再操作！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if((System.currentTimeMillis()-exitTime) > 2000){
                    Toast.makeText(getActivity(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                    return;
                } else {
                    mainActivity.finish();
                    System.exit(0);
                }
                break;
            default:
                return;
        }
    }
}
