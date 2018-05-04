package com.example.admin.bluetooth_sps.Ui.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
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
public class SensorFragment40 extends Fragment implements View.OnClickListener{
    private View view;
    private Button btnSelectFrequency;
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
    private TextView tvSensorId;
    private TextView tvFName;
    private EditText etProvince;
    private EditText etCity;
    private EditText etArea;
    private EditText etStreet;
    private EditText etParking;
    private EditText etPosition;
    private EditText etStopTime;
    private EditText etGapTime;
    public TextView tvContent;
    private TextView tvActiveStatus;
    private Spinner spinAgility;
    private Spinner spinCarPut;
    public ScrollView svContent;

    public long exitTime = 0;
    public boolean isLongPressKey;  //检测按钮是否长按

    private String cat_put_select;
    private boolean SensorFirstFlag = true;
    private boolean flag=true;   //检测第几次长按按钮
    private int frequencyIndex=0;
    private String agility;
    private byte[] sensorID;//地磁的ID
    private DownloadFileFragment downloadFileFragment=new DownloadFileFragment();
    private MainActivity mainActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sensor_fragment40, container,false);
        mainActivity = (MainActivity) getActivity();
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
                    mainActivity.initViewPage(2);
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
        btnSelectFrequency = (Button) view.findViewById(R.id.btnSelectFrequency);
        btnWakeConfig = (Button) view.findViewById(R.id.wake_config);
        btnReadConfig = (Button) view.findViewById(R.id.read_config);
        btnWriteConfig = (Button) view.findViewById(R.id.write_config);
        btnReset = (Button) view.findViewById(R.id.reset);
        btnActive = (Button) view.findViewById(R.id.active);
        btnRecovery = (Button) view.findViewById(R.id.recovery);
        btnEndConfig = (Button) view.findViewById(R.id.end_config);
        clear = (Button) view.findViewById(R.id.clear);
        btnEnd = (Button) view.findViewById(R.id.end);
        btnUpdate = (Button) view.findViewById(R.id.btnUpdate40);
        tvContent = (TextView) view.findViewById(R.id.tv_content);
        tvActiveStatus = (TextView) view.findViewById(R.id.active_status);
        tvSensorId = (TextView) view.findViewById(R.id.sensor_id);
        tvFName = (TextView) view.findViewById(R.id.tv_FName);
        etProvince = (EditText) view.findViewById(R.id.et_province);
        etCity = (EditText) view.findViewById(R.id.et_city);
        etArea = (EditText) view.findViewById(R.id.et_area);
        etStreet = (EditText) view.findViewById(R.id.et_street);
        etParking = (EditText) view.findViewById(R.id.et_parking);
        etPosition = (EditText) view.findViewById(R.id.et_position);
        etStopTime = (EditText) view.findViewById(R.id.stop_time);
        etGapTime = (EditText) view.findViewById(R.id.gap_time);
        svContent = (ScrollView) view.findViewById(R.id.sv_content);
        spinCarPut = (Spinner) view.findViewById(R.id.car_put);
        spinAgility = (Spinner) view.findViewById(R.id.lingMin);
        new RegionNumberEditTextUtils(getContext(),etProvince,0);
        new RegionNumberEditTextUtils(getContext(),etCity,0);
        new RegionNumberEditTextUtils(getContext(),etArea,0);
        new RegionNumberEditTextUtils(getContext(),etStreet,0);
        new RegionNumberEditTextUtils(getContext(),etParking,0);
        new RegionNumberEditTextUtils(getContext(),etPosition,0);
        new RegionNumberEditTextUtils(getContext(),etStopTime,2);
        new RegionNumberEditTextUtils(getContext(),etGapTime,3);

    }
    private void initEvent() {
        btnSelectFrequency.setOnClickListener(this);
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
        btnSelectFrequency.setEnabled(b);
        tvContent.setEnabled(b);
        tvActiveStatus.setEnabled(b);
        tvSensorId.setEnabled(b);
        etProvince.setEnabled(b);
        etCity.setEnabled(b);
        etArea.setEnabled(b);
        etStreet.setEnabled(b);
        etParking.setEnabled(b);
        etPosition.setEnabled(b);
        etStopTime.setEnabled(b);
        etGapTime.setEnabled(b);
        svContent.setEnabled(b);
        spinAgility.setEnabled(b);
        spinCarPut.setEnabled(b);
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

    @SuppressLint("SetTextI18n")
     public void receiveDataDeal(String string) {
        //ff fe开头
        if(getCharByInt(string, 0)==0xff&&getCharByInt(string, 1)==0xfe) {
            System.out.println("帧标识：" + String.valueOf(getCharByInt(string, 2)));
            if (downloadFileFragment.openToastFlag) {
                switch (getCharByInt(string, 2)){
                    case 2:
                        if(getCharByInt(string, 3)==0x20){
                            if(getCharByInt(string, 23)==0)
                                Toast.makeText(getActivity(), "接收到无车数据包", Toast.LENGTH_SHORT).show();
                            else if(getCharByInt(string, 23)==1)
                                Toast.makeText(getActivity(), "接收到有车数据包", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 8://0x08
                        sensorID =new byte[12];
                        sensorID[0]= (byte)getCharByInt(string, 4);
                        sensorID[1]= (byte)getCharByInt(string, 5);
                        sensorID[2]= (byte)getCharByInt(string, 6);
                        sensorID[3]= (byte)getCharByInt(string, 7);
                        sensorID[4]= (byte)getCharByInt(string, 8);
                        sensorID[5]= (byte)getCharByInt(string, 9);
                        sensorID[6]= (byte)getCharByInt(string, 10);
                        sensorID[7]= (byte)getCharByInt(string, 11);
                        sensorID[8]= (byte)getCharByInt(string, 12);
                        sensorID[9]= (byte)getCharByInt(string, 13);
                        sensorID[10]=(byte)getCharByInt(string, 14);
                        sensorID[11]=(byte)getCharByInt(string, 15);
                        tvSensorId.setText("");
                        char buf[] = new char[24];
                        string.getChars(8, 31 + 1, buf, 0);
                        tvSensorId.append(new String(buf));

                        if (string.charAt(34) == '0' && string.charAt(35) == '1')
                            tvActiveStatus.setText("已激活");
                        else
                            tvActiveStatus.setText("未激活");

                        Toast.makeText(getActivity(), "接收到唤醒回复 请立即点击读取", Toast.LENGTH_SHORT).show();
                        break;
                    case 9://0x09
                        Toast.makeText(getActivity(), "配置超时,请重新点击唤醒并读取", Toast.LENGTH_SHORT).show();
                        break;
                    case 38://0x26
                        if(getCharByInt(string, 3)==0x28) {
                            Toast.makeText(getActivity(), "读取成功", Toast.LENGTH_SHORT).show();
                            if (getCharByInt(string, 20) > 30 && getCharByInt(string, 21) > 30) {
                                etProvince.setText(String.valueOf(getCharByInt(string, 20) - 0x30) + String.valueOf(getCharByInt(string, 21) - 0x30));
                                etProvince.setTextColor(Color.BLACK);
                            } else {
                                etProvince.setText(String.valueOf(getCharByInt(string, 20)) + String.valueOf(getCharByInt(string, 21)));
                                etProvince.setTextColor(Color.RED);
                            }
                            if (getCharByInt(string, 22) > 30 && getCharByInt(string, 23) > 30) {
                                etCity.setText(String.valueOf(getCharByInt(string, 22) - 0x30) + String.valueOf(getCharByInt(string, 23) - 0x30));
                                etCity.setTextColor(Color.BLACK);
                            } else {
                                etCity.setText(String.valueOf(getCharByInt(string, 22)) + String.valueOf(getCharByInt(string, 23)));
                                etCity.setTextColor(Color.RED);
                            }
                            if (getCharByInt(string, 24) > 30 && getCharByInt(string, 25) > 30) {
                                etArea.setText(String.valueOf(getCharByInt(string, 24) - 0x30) + String.valueOf(getCharByInt(string, 25) - 0x30));
                                etArea.setTextColor(Color.BLACK);
                            } else {
                                etArea.setText(String.valueOf(getCharByInt(string, 24)) + String.valueOf(getCharByInt(string, 25)));
                                etArea.setTextColor(Color.RED);
                            }
                            if (getCharByInt(string, 26) > 30 && getCharByInt(string, 27) > 30 && getCharByInt(string, 28) > 30) {
                                etStreet.setText(String.valueOf(getCharByInt(string, 26) - 0x30) + String.valueOf(getCharByInt(string, 27) - 0x30) + String.valueOf(getCharByInt(string, 28) - 0x30));
                                etStreet.setTextColor(Color.BLACK);
                            } else {
                                etStreet.setText(String.valueOf(getCharByInt(string, 26)) + String.valueOf(getCharByInt(string, 27)) + String.valueOf(getCharByInt(string, 28)));
                                etStreet.setTextColor(Color.RED);
                            }
                            if (getCharByInt(string, 29) > 30 && getCharByInt(string, 30) > 30) {
                                etParking.setText(String.valueOf(getCharByInt(string, 29) - 0x30) + String.valueOf(getCharByInt(string, 30) - 0x30));
                                etParking.setTextColor(Color.BLACK);
                            } else {
                                etParking.setText(String.valueOf(getCharByInt(string, 29)) + String.valueOf(getCharByInt(string, 30)));
                                etParking.setTextColor(Color.RED);
                            }
                            if (getCharByInt(string, 31) > 30 && getCharByInt(string, 32) > 30 && getCharByInt(string, 33) > 30){
                                etPosition.setText(String.valueOf(getCharByInt(string, 31) - 0x30) + String.valueOf(getCharByInt(string, 32) - 0x30) + String.valueOf(getCharByInt(string, 33) - 0x30));
                                etPosition.setTextColor(Color.BLACK);
                            }else {
                                etPosition.setText(String.valueOf(getCharByInt(string, 31)) + String.valueOf(getCharByInt(string, 32)) + String.valueOf(getCharByInt(string, 33)));
                                etPosition.setTextColor(Color.RED);
                            }
                            etStopTime.setText(String.valueOf(getCharByInt(string, 18)));
                            etGapTime.setText(String.valueOf(getCharByInt(string, 19)));
                            if (getCharByInt(string, 34) == 0x00)
                                spinAgility.setSelection(0);
                            else if (getCharByInt(string, 34) == 0x0A)
                                spinAgility.setSelection(1);
                            else
                                spinAgility.setSelection(2);

                            if (string.charAt(35) == '0')
                                spinCarPut.setSelection(0);
                            else if (string.charAt(35) == '1')
                                spinCarPut.setSelection(1);
                        }
                        break;
                    case 39://0x27
                        Toast.makeText(getActivity(), "配置成功", Toast.LENGTH_SHORT).show();
                        break;
                    case 42://0x2a
                        if(getCharByInt(string, 3)==0x18)
                            Toast.makeText(getActivity(), "恢复出厂设置成功", Toast.LENGTH_SHORT).show();
                        break;
                    case 44://0x2C
                        Toast.makeText(getActivity(), "重启成功", Toast.LENGTH_SHORT).show();
                        break;
                    case 45://0x2D
                        Toast.makeText(getActivity(), "退出配置成功", Toast.LENGTH_SHORT).show();
                        break;
                    case 43://0x2B
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
                                        case 0:setFrequency(0);break;
                                        case 1:setFrequency(1);break;
                                        case 2:setFrequency(2);break;
                                        case 3:setFrequency(3);break;
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
                        downloadFileFragment.totalNum = getCharByInt(string,4);
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
            case R.id.btnSelectFrequency:
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
                            case 3:selectFrequency(0x15, 0x36, 0x08);break;//473M
                            case 4:selectFrequency(0x20, 0x36, 0x08);break;//474M
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
                if(TextUtils.isEmpty(tvSensorId.getText())){
                    Toast.makeText(getActivity(), "传感器id不能为空，请先唤醒设备！", Toast.LENGTH_SHORT).show();
                    return;
                }
                byte[] writeBuffer = new byte[40];
                writeBuffer[0] = (byte) 0xff;
                writeBuffer[1] = (byte) 0xfe;
                writeBuffer[2] = (byte) 0x06;
                writeBuffer[3] = (byte) 0x28;
                writeBuffer[4]=sensorID[0];
                writeBuffer[5]=sensorID[1];
                writeBuffer[6]=sensorID[2];
                writeBuffer[7]=sensorID[3];
                writeBuffer[8]=sensorID[4];
                writeBuffer[9]=sensorID[5];
                writeBuffer[10]=sensorID[6];
                writeBuffer[11]=sensorID[7];
                writeBuffer[12]=sensorID[8];
                writeBuffer[13]=sensorID[9];
                writeBuffer[14]=sensorID[10];
                writeBuffer[15]=sensorID[11];
                writeBuffer[16]=0x00;
                writeBuffer[17]=0x00;
                writeBuffer[18]=0x00;
                writeBuffer[19]=0x00;
                writeBuffer[20]=0x00;
                writeBuffer[21]=0x00;
                writeBuffer[22]=0x00;
                writeBuffer[23]=0x00;
                writeBuffer[24]=0x00;
                writeBuffer[25]=0x00;
                writeBuffer[26]=0x00;
                writeBuffer[27]=0x00;
                writeBuffer[28]=0x00;
                writeBuffer[29]=0x00;
                writeBuffer[30]=0x00;
                writeBuffer[31]=0x00;
                writeBuffer[32]=0x00;
                writeBuffer[33]=0x00;
                writeBuffer[34]=0x00;
                writeBuffer[35]=0x00;
                writeBuffer[36]=0x00;
                writeBuffer[37]=Exclusive_Or(writeBuffer, 37);
                writeBuffer[38]=(byte) 0xab;
                writeBuffer[39]=(byte) 0xaa;
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
                if (TextUtils.isEmpty(tvSensorId.getText())
                        || TextUtils.isEmpty(etStopTime.getText())
                        || TextUtils.isEmpty(etGapTime.getText())) {
                    Toast.makeText(getActivity(), "请确定基本信息是否填写完整", Toast.LENGTH_SHORT).show();
                    return;
                }
                byte[] writeBuff = new byte[40];
                writeBuff[0]=(byte) 0xff;
                writeBuff[1]=(byte) 0xfe;
                writeBuff[2]=(byte) 0x07;
                writeBuff[3]=(byte) 0x28;
                writeBuff[4]=sensorID[0];
                writeBuff[5]=sensorID[1];
                writeBuff[6]=sensorID[2];
                writeBuff[7]=sensorID[3];
                writeBuff[8]=sensorID[4];
                writeBuff[9]=sensorID[5];
                writeBuff[10]=sensorID[6];
                writeBuff[11]=sensorID[7];
                writeBuff[12]=sensorID[8];
                writeBuff[13]=sensorID[9];
                writeBuff[14]=sensorID[10];
                writeBuff[15]=sensorID[11];
                writeBuff[16]=(byte)0x00;
                writeBuff[17]=(byte) Integer.parseInt(cat_put_select, 10);
                writeBuff[18]=(byte) Integer.parseInt(etStopTime.getText().toString(), 10);
                writeBuff[19]=(byte) Integer.parseInt(etGapTime.getText().toString(), 10);
                writeBuff[20]=(byte)(Integer.parseInt(etProvince.getText().toString(),16)/16+0x30);
                writeBuff[21]=(byte)(Integer.parseInt(etProvince.getText().toString(),16)%16+0x30);
                writeBuff[22]=(byte)(Integer.parseInt(etCity.getText().toString(),16)/16+0x30);
                writeBuff[23]=(byte)(Integer.parseInt(etCity.getText().toString(), 16)%16+0x30);
                writeBuff[24]=(byte)(Integer.parseInt(etArea.getText().toString(), 16)/16+0x30);
                writeBuff[25]=(byte)(Integer.parseInt(etArea.getText().toString(), 16)%16+0x30);
                writeBuff[26]=(byte)(Integer.parseInt(etStreet.getText().toString(),  16)/256+0x30);
                writeBuff[27]=(byte)(Integer.parseInt(etStreet.getText().toString(),  16)%256/16+0x30);
                writeBuff[28]=(byte)(Integer.parseInt(etStreet.getText().toString(),  16)%16+0x30);
                writeBuff[29]=(byte)(Integer.parseInt(etParking.getText().toString(), 16)/16+0x30);
                writeBuff[30]=(byte)(Integer.parseInt(etParking.getText().toString(), 16)%16+0x30);
                writeBuff[31]=(byte)(Integer.parseInt(etPosition.getText().toString(), 16)/256+0x30);
                writeBuff[32]=(byte)(Integer.parseInt(etPosition.getText().toString(), 16)%256/16+0x30);
                writeBuff[33]=(byte)(Integer.parseInt(etPosition.getText().toString(), 16)%16+0x30);
                writeBuff[34]=(byte)Integer.parseInt(agility,10);
                writeBuff[35]=(byte)0x00;
                writeBuff[36]=(byte)0x00;
                writeBuff[37]=Exclusive_Or(writeBuff,37);
                writeBuff[38]=(byte) 0xab;
                writeBuff[39]=(byte) 0xaa;
                mainActivity.target_chara.setValue(writeBuff);
                mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
                break;
            case R.id.reset:
                if(!downloadFileFragment.openToastFlag){
                    Toast.makeText(getActivity(), "正在写入，请等待完成后再操作！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(tvSensorId.getText())) {
                    Toast.makeText(getActivity(), "传感器ID不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                writeBuffer = new byte[24];
                writeBuffer[0]=(byte) 0xff;
                writeBuffer[1]=(byte) 0xfe;
                writeBuffer[2]=(byte) 0x0c;
                writeBuffer[3]=(byte) 0x18 ;
                writeBuffer[4]=sensorID[0];
                writeBuffer[5]=sensorID[1];
                writeBuffer[6]=sensorID[2];
                writeBuffer[7]=sensorID[3];
                writeBuffer[8]=sensorID[4];
                writeBuffer[9]=sensorID[5];
                writeBuffer[10]=sensorID[6];
                writeBuffer[11]=sensorID[7];
                writeBuffer[12]=sensorID[8];
                writeBuffer[13]=sensorID[9];
                writeBuffer[14]=sensorID[10];
                writeBuffer[15]=sensorID[11];
                writeBuffer[16]=0x00;
                writeBuffer[17]=0x00;
                writeBuffer[18]=0x00;
                writeBuffer[19]=0x00;
                writeBuffer[20]=0x00;
                writeBuffer[21]=Exclusive_Or(writeBuffer,21);
                writeBuffer[22]=(byte)0xab;
                writeBuffer[23]=(byte)0xaa;
                mainActivity.target_chara.setValue(writeBuffer);
                mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
                break;
            case R.id.active:
                if(!downloadFileFragment.openToastFlag){
                    Toast.makeText(getActivity(), "正在写入，请等待完成后再操作！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(tvSensorId.getText())) {
                    Toast.makeText(getActivity(), "传感器ID不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                writeBuffer = new byte[24];
                writeBuffer[0]=(byte) 0xff;
                writeBuffer[1]=(byte) 0xfe;
                writeBuffer[2]=(byte) 0x0b;
                writeBuffer[3]=(byte) 0x18 ;
                writeBuffer[4]=sensorID[0];
                writeBuffer[5]=sensorID[1];
                writeBuffer[6]=sensorID[2];
                writeBuffer[7]=sensorID[3];
                writeBuffer[8]=sensorID[4];
                writeBuffer[9]=sensorID[5];
                writeBuffer[10]=sensorID[6];
                writeBuffer[11]=sensorID[7];
                writeBuffer[12]=sensorID[8];
                writeBuffer[13]=sensorID[9];
                writeBuffer[14]=sensorID[10];
                writeBuffer[15]=sensorID[11];
                writeBuffer[16]=0x00;
                writeBuffer[17]=0x00;
                writeBuffer[18]=0x00;
                writeBuffer[19]=0x00;
                writeBuffer[20]=0x00;
                writeBuffer[21]=Exclusive_Or(writeBuffer,21);
                writeBuffer[22]=(byte)0xab;
                writeBuffer[23]=(byte)0xaa;
                mainActivity.target_chara.setValue(writeBuffer);
                mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
                break;
            case R.id.recovery:
                if(!downloadFileFragment.openToastFlag){
                    Toast.makeText(getActivity(), "正在写入，请等待完成后再操作！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(tvSensorId.getText())) {
                    Toast.makeText(getActivity(), "传感器ID不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                writeBuffer = new byte[24];
                writeBuffer[0]=(byte) 0xff;
                writeBuffer[1]=(byte) 0xfe;
                writeBuffer[2]=(byte) 0x0a;
                writeBuffer[3]=(byte) 0x18 ;
                writeBuffer[4]=sensorID[0];
                writeBuffer[5]=sensorID[1];
                writeBuffer[6]=sensorID[2];
                writeBuffer[7]=sensorID[3];
                writeBuffer[8]=sensorID[4];
                writeBuffer[9]=sensorID[5];
                writeBuffer[10]=sensorID[6];
                writeBuffer[11]=sensorID[7];
                writeBuffer[12]=sensorID[8];
                writeBuffer[13]=sensorID[9];
                writeBuffer[14]=sensorID[10];
                writeBuffer[15]=sensorID[11];
                writeBuffer[16]=0x00;
                writeBuffer[17]=0x00;
                writeBuffer[18]=0x00;
                writeBuffer[19]=0x00;
                writeBuffer[20]=0x00;
                writeBuffer[21]=Exclusive_Or(writeBuffer,21);
                writeBuffer[22]=(byte)0xab;
                writeBuffer[23]=(byte)0xaa;
                mainActivity.target_chara.setValue(writeBuffer);
                mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
                break;
            case R.id.end_config:
                if(!downloadFileFragment.openToastFlag){
                    Toast.makeText(getActivity(), "正在写入，请等待完成后再操作！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(tvSensorId.getText())) {
                    Toast.makeText(getActivity(), "传感器ID不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                writeBuffer = new byte[24];
                writeBuffer[0]=(byte) 0xff;
                writeBuffer[1]=(byte) 0xfe;
                writeBuffer[2]=(byte) 0x0d;
                writeBuffer[3]=(byte) 0x18 ;
                writeBuffer[4]=sensorID[0];
                writeBuffer[5]=sensorID[1];
                writeBuffer[6]=sensorID[2];
                writeBuffer[7]=sensorID[3];
                writeBuffer[8]=sensorID[4];
                writeBuffer[9]=sensorID[5];
                writeBuffer[10]=sensorID[6];
                writeBuffer[11]=sensorID[7];
                writeBuffer[12]=sensorID[8];
                writeBuffer[13]=sensorID[9];
                writeBuffer[14]=sensorID[10];
                writeBuffer[15]=sensorID[11];
                writeBuffer[16]=(byte)0x00;
                writeBuffer[17]=(byte)0x00;
                writeBuffer[18]=(byte)0x00;
                writeBuffer[19]=(byte)0x00;
                writeBuffer[20]=(byte)0x00;
                writeBuffer[21]=Exclusive_Or(writeBuffer,21);
                writeBuffer[22]=(byte)0xab;
                writeBuffer[23]=(byte)0xaa;
                mainActivity.target_chara.setValue(writeBuffer);
                mainActivity.mService.writeCharacteristic(mainActivity.target_chara);

                break;
            case R.id.btnUpdate40:
                if((System.currentTimeMillis()-exitTime) > 2000) {
                    Toast.makeText(getActivity(), "再按一次开始升级", Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                    return;
                }else if (downloadFileFragment.totalNum == 0) {
                    Toast.makeText(getActivity(), "请先唤醒设备", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(TextUtils.isEmpty(tvSensorId.getText())){
                    Toast.makeText(getActivity(), "传感器ID不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                writeBuffer = new byte[40];
                writeBuffer[0]=(byte)0xff;
                writeBuffer[1]=(byte)0xfe;
                writeBuffer[2]=(byte)0x60;
                writeBuffer[3]=(byte)0x28;
                writeBuffer[4]=sensorID[0];
                writeBuffer[5]=sensorID[1];
                writeBuffer[6]=sensorID[2];
                writeBuffer[7]=sensorID[3];
                writeBuffer[8]=sensorID[4];
                writeBuffer[9]=sensorID[5];
                writeBuffer[10]=sensorID[6];
                writeBuffer[11]=sensorID[7];
                writeBuffer[12]=sensorID[8];
                writeBuffer[13]=sensorID[9];
                writeBuffer[14]=sensorID[10];
                writeBuffer[15]=sensorID[11];
                writeBuffer[16]=(byte)0x00;
                writeBuffer[17]=(byte)downloadFileFragment.totalNum;
                writeBuffer[18]=(byte)0x00;
                writeBuffer[19]=(byte)0x00;
                writeBuffer[20]=(byte)0x00;
                writeBuffer[21]=(byte)0x00;
                writeBuffer[22]=(byte)0x00;
                writeBuffer[23]=(byte)0x00;
                writeBuffer[24]=(byte)0x00;
                writeBuffer[25]=(byte)0x00;
                writeBuffer[26]=(byte)0x00;
                writeBuffer[27]=(byte)0x00;
                writeBuffer[28]=(byte)0x00;
                writeBuffer[29]=(byte)0x00;
                writeBuffer[30]=(byte)0x00;
                writeBuffer[31]=(byte)0x00;
                writeBuffer[32]=(byte)0x00;
                writeBuffer[33]=(byte)0x00;
                writeBuffer[34]=(byte)0x00;
                writeBuffer[35]=(byte)0x00;
                writeBuffer[36]=(byte)0x00;
                writeBuffer[37]=Exclusive_Or(writeBuffer, 37);
                writeBuffer[38]=(byte) 0xab;
                writeBuffer[39]=(byte) 0xaa;
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
