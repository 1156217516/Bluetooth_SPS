package com.example.admin.bluetooth_sps.Ui.Fragment;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
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
 * Created by 11562 on 2018/4/10.
 */

public class SensorFragmentNB  extends Fragment implements View.OnClickListener{
    private View view;
    private Button btnReadConfig;
    private Button btnWriteConfig;
    private Button btnReset;
    private Button btnActive;
    private Button btnRecovery;
    private Button btnPoint;
    private Button clear;
    private Button btnEndConfig;
    private Button btnEnd;
    private Button btnUpdate;
    private TextView tvChangeFre;
    private EditText etPosition;
    private EditText etCity;
    private EditText etArea;
    private EditText etIP1;
    private EditText etIP2;
    private EditText etIP3;
    private EditText etIP4;
    private EditText etPort;
    private EditText etStopTime;
    private EditText etGapTime;
    private TextView tvSensorId;
    private TextView tvActive;
    private TextView tvFName;
    public TextView tvContent;
    private TextView tvCopy;
    private Spinner spinAgility;
    private Spinner spinCarPut;
    public ScrollView svContent;
    private int frequencyIndex=0;
    private String cat_put_select;
    private boolean SensorFirstFlag = true;
    public long exitTime = 0;
    public boolean isLongPressKey;  //检测升级按钮是否长按
    private boolean flag=true;   //检测第几次长按升级按钮

    private String agility;
    private int point=0;
    private byte[] sensorID=new byte[6];  //地磁的ID

    private DownloadFileFragment downloadFileFragment;
    private MainActivity mainActivity;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sensor_fragment_nb, container,false);
        mainActivity = (MainActivity) getActivity();
        downloadFileFragment=new DownloadFileFragment();
        init();
        setState(false);
        initEvent();

        btnUpdate.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isLongPressKey = true;
                Log.d("isLongPressKey:", "" + isLongPressKey);
                if (flag) {
                    mainActivity.initViewPage(-1);
                    mainActivity.initViewPage(6);
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
                agility = String.valueOf(position * 10);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        return view;
    }

    private void init() {
        btnReadConfig = (Button) view.findViewById(R.id.read_config);
        btnWriteConfig = (Button) view.findViewById(R.id.write_config);
        btnReset = (Button) view.findViewById(R.id.reset);
        btnActive = (Button) view.findViewById(R.id.active);
        btnRecovery = (Button) view.findViewById(R.id.recovery);
        btnPoint = (Button) view.findViewById(R.id.btn_point);
        clear = (Button) view.findViewById(R.id.clear);
        btnEndConfig = (Button) view.findViewById(R.id.btn_endConfig);
        btnEnd = (Button) view.findViewById(R.id.end);
        btnUpdate = (Button) view.findViewById(R.id.btnUpdateNB);
        tvActive = (TextView) view.findViewById(R.id.tv_active);
        tvFName = (TextView) view.findViewById(R.id.tv_FName);
        tvContent = (TextView) view.findViewById(R.id.tv_content);
        tvCopy = (TextView) view.findViewById(R.id.tv_copy);
        tvSensorId = (TextView) view.findViewById(R.id.sensor_id);
        tvChangeFre = (TextView) view.findViewById(R.id.tvChangeFre);
        svContent = (ScrollView) view.findViewById(R.id.sv_content);
        spinCarPut = (Spinner) view.findViewById(R.id.car_put);
        spinAgility = (Spinner) view.findViewById(R.id.lingMin);
        etCity=(EditText) view.findViewById(R.id.et_city);
        etArea=(EditText) view.findViewById(R.id.et_area);
        etPosition=(EditText) view.findViewById(R.id.et_position);
        etIP1=(EditText) view.findViewById(R.id.et_ip1);
        etIP2=(EditText) view.findViewById(R.id.et_ip2);
        etIP3=(EditText) view.findViewById(R.id.et_ip3);
        etIP4=(EditText) view.findViewById(R.id.et_ip4);
        etPort=(EditText) view.findViewById(R.id.et_port);
        etStopTime=(EditText) view.findViewById(R.id.stop_time);
        etGapTime=(EditText) view.findViewById(R.id.gap_time);
        new RegionNumberEditTextUtils(getContext(),etCity,0);
        new RegionNumberEditTextUtils(getContext(),etArea,0);
        new RegionNumberEditTextUtils(getContext(),etIP1,0);
        new RegionNumberEditTextUtils(getContext(),etIP2,0);
        new RegionNumberEditTextUtils(getContext(),etIP3,0);
        new RegionNumberEditTextUtils(getContext(),etIP4,0);
        new RegionNumberEditTextUtils(getContext(),etPort,1);
        new RegionNumberEditTextUtils(getContext(),etStopTime,2);
        new RegionNumberEditTextUtils(getContext(),etGapTime,3);
        new RegionNumberEditTextUtils(getContext(),etPosition,4);
        for(int i=0;i<6;i++)
            sensorID[i]=0;
    }
    private void initEvent() {
        tvChangeFre.setOnClickListener(this);
        btnReadConfig.setOnClickListener(this);
        btnWriteConfig.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        btnActive.setOnClickListener(this);
        btnPoint.setOnClickListener(this);
        clear.setOnClickListener(this);
        btnRecovery.setOnClickListener(this);
        btnEndConfig.setOnClickListener(this);
        btnEnd.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        tvCopy.setOnClickListener(this);
    }
    public void setState(boolean b) {
        tvChangeFre.setEnabled(b);
        tvCopy.setEnabled(b);
        tvSensorId.setEnabled(b);
        etCity.setEnabled(b);
        etArea.setEnabled(b);
        etPosition.setEnabled(b);
        etIP1.setEnabled(b);
        etIP2.setEnabled(b);
        etIP3.setEnabled(b);
        etIP4.setEnabled(b);
        etPort.setEnabled(b);
        etStopTime.setEnabled(b);
        etGapTime.setEnabled(b);
        btnPoint.setEnabled(b);
        spinAgility.setEnabled(b);
        spinCarPut.setEnabled(b);
        btnReadConfig.setEnabled(b);
        btnWriteConfig.setEnabled(b);
        btnUpdate.setEnabled(b);
        btnReset.setEnabled(b);
        btnActive.setEnabled(b);
        clear.setEnabled(b);
        btnRecovery.setEnabled(b);
        btnEndConfig.setEnabled(b);
    }



    //对回复数据进行处理
    public void receiveDataDeal(String string) {
        //ff fe开头
        //System.out.println("receiveDataDeal:" + string);
        if (getCharByInt(string, 0) == 255 && getCharByInt(string, 1) == 254) {
            System.out.println("帧标识：" + String.valueOf(getCharByInt(string, 2)));
            if (downloadFileFragment.openToastFlag) {
                if (string.length() / 2 < getCharByInt(string, 3)) {
                    Toast.makeText(getActivity(), "接受数据不完整", Toast.LENGTH_SHORT).show();
                    return;
                }
                switch (getCharByInt(string, 2)) {
                    case 150://0x96
                        if(getCharByInt(string, 3)==0x3D) {
                            sensorID[0] = (byte) getCharByInt(string, 5);
                            sensorID[1] = (byte) getCharByInt(string, 6);
                            sensorID[2] = (byte) getCharByInt(string, 7);
                            sensorID[3] = (byte) getCharByInt(string, 8);
                            sensorID[4] = (byte) getCharByInt(string, 9);
                            sensorID[5] = (byte) getCharByInt(string, 10);
                            tvSensorId.setText(string.substring(10, 22));

                            etCity.setText(string.valueOf(getCharByInt(string, 12)));
                            etArea.setText(string.valueOf(getCharByInt(string, 13)));
                            int id0 = getCharByInt(string, 14) * 1000000;
                            int id1 = getCharByInt(string, 15) * 10000;
                            int id2 = getCharByInt(string, 16) * 100;
                            int id3 = getCharByInt(string, 17);

                            etPosition.setText(string.valueOf(id0 + id1 + id2 + id3));
                            String spinCarPutStr = "";
                            String spinAgilityStr = "";
                            int ip0 = getCharByInt(string, 44);
                            int ip1 = getCharByInt(string, 45);
                            int ip2 = getCharByInt(string, 46);
                            int ip3 = getCharByInt(string, 47);
                            int portL = getCharByInt(string, 48);
                            int portH = getCharByInt(string, 49);
                            int port = portH << 8 | portL;
                            etIP1.setText(string.valueOf(ip0));
                            etIP2.setText(string.valueOf(ip1));
                            etIP3.setText(string.valueOf(ip2));
                            etIP4.setText(string.valueOf(ip3));
                            etPort.setText(string.valueOf(port));
                            if (getCharByInt(string, 50) == 0) {
                                spinCarPut.setSelection(0);
                                spinCarPutStr = "一字型";
                            } else if (getCharByInt(string, 50) == 1) {
                                spinCarPut.setSelection(1);
                                spinCarPutStr = "并排型";
                            }
                            etStopTime.setText(String.valueOf(getCharByInt(string, 51)));
                            etGapTime.setText(String.valueOf(getCharByInt(string, 56)));
                            if (getCharByInt(string, 53) == 0) {
                                spinAgility.setSelection(0);
                                spinAgilityStr = "高";
                            } else if (getCharByInt(string, 53) == 10) {
                                spinAgility.setSelection(1);
                                spinAgilityStr = "中";
                            } else if (getCharByInt(string, 53) == 20) {
                                spinAgility.setSelection(2);
                                spinAgilityStr = "低";
                            }else if (getCharByInt(string, 53) == 30) {
                                spinAgility.setSelection(3);
                                spinAgilityStr = "极低";
                            }else if (getCharByInt(string, 53) == 40) {
                                spinAgility.setSelection(4);
                                spinAgilityStr = "超极低";
                            }
                            int x = getCharByInt(string, 34) << 8 | getCharByInt(string, 33);
                            int y = getCharByInt(string, 36) << 8 | getCharByInt(string, 35);
                            int z = getCharByInt(string, 38) << 8 | getCharByInt(string, 37);
                            String sv = string.charAt(80) + "." + string.charAt(81) + "" + string.charAt(82) + "" + string.charAt(83);
                            String hv = string.charAt(84) + "." + string.charAt(85) + "" + string.charAt(86) + "" + string.charAt(87);
                            String carStatus = "";
                            if (getCharByInt(string, 54) == 1)
                                carStatus = "有车";
                            else
                                carStatus = "无车";
                            String isActive = "";
                            if (getCharByInt(string, 57) == 0) {
                                isActive = "未激活";
                                tvActive.setText(isActive);
                            }
                            else if (getCharByInt(string, 57) == 1) {
                                isActive = "已激活";
                                tvActive.setText(isActive);
                            }
                            string = string.toUpperCase();
                            String strData =
                                    "唯一ID：" + string.substring(10, 22) + "\n"
                                            + "激活状态：" + isActive + "\n"
                                            + "泊位号：" + string.valueOf(id0 + id1 + id2 + id3) + "\n"
                                            + "原始磁场:" + string.charAt(36) + "" + string.charAt(37)
                                            + " " + string.charAt(38) + "" + string.charAt(39)
                                            + " " + string.charAt(40) + "" + string.charAt(41)
                                            + " " + string.charAt(42) + "" + string.charAt(43)
                                            + " " + string.charAt(44) + "" + string.charAt(45)
                                            + " " + string.charAt(46) + "" + string.charAt(47) + "\n"
                                            + "当前磁场:" + string.charAt(50) + "" + string.charAt(51)
                                            + " " + string.charAt(51) + "" + string.charAt(53)
                                            + " " + string.charAt(54) + "" + string.charAt(55)
                                            + " " + string.charAt(56) + "" + string.charAt(57)
                                            + " " + string.charAt(58) + "" + string.charAt(59)
                                            + " " + string.charAt(60) + "" + string.charAt(61) + "\n"
                                            + "磁场偏移:" + string.charAt(66) + "" + string.charAt(67)
                                            + " " + string.charAt(68) + "" + string.charAt(69)
                                            + " " + string.charAt(70) + "" + string.charAt(71)
                                            + " " + string.charAt(72) + "" + string.charAt(73)
                                            + " " + string.charAt(74) + "" + string.charAt(75)
                                            + " " + string.charAt(76) + "" + string.charAt(77) + "\n"
                                            + "偏移：X轴 " + x + "  Y轴 " + y + "  Z轴 " + z + "\n"
                                            + "硬件版本：" + hv + "\n"
                                            + "软件版本：" + sv + "\n"
                                            + "信号强度：" + string.charAt(104) + "" + string.charAt(105) + "\n"
                                            + "车位状态：" + carStatus + "\n"
                                            + "休眠时间：" + String.valueOf(getCharByInt(string, 51)) + "\n"
                                            + "心跳间隔：" + String.valueOf(getCharByInt(string, 56)) + "\n"
                                            + "车位分布：" + spinCarPutStr + "\n"
                                            + "灵敏度：" + spinAgilityStr + "\n"
                                            + "电压状态：" + getCharByInt(string, 55);

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("设备信息")
                                    .setMessage(strData)
                                    .setNegativeButton("确定", null)
                                    .setCancelable(false)
                                    .create()
                                    .show();
                        }
                        break;
                    case 105://0x69
                        downloadFileFragment.totalNum = getCharByInt(string, 4);
                        String revFileNameString = (char) getCharByInt(string, 5) + ""
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
                        tvFName.setText("配置工具程序：" + "\n" + revFileNameString);

                        break;
                    default:
                        break;
                }
            }
        }
    }



    private void selectFrequency(int index) {
        byte[] buff = new byte[8];
        buff[0] = (byte) 0xff;
        buff[1] = (byte) 0xfe;
        buff[2] = (byte) 0xc0;
        buff[3] = (byte) 0x08;
        buff[4] = (byte) index;
        buff[5] = Exclusive_Or(buff,5);
        buff[6] = (byte) 0xab;
        buff[7] = (byte) 0xaa;
        mainActivity.target_chara.setValue(buff);
        mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvChangeFre:
                if(!downloadFileFragment.openToastFlag){
                    Toast.makeText(getActivity(), "正在写入，请等待完成后再操作！", Toast.LENGTH_SHORT).show();
                    return;
                }
                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                String[] frequencyStr=new String[]{"430M","432M","436M","438M","440M","默认434M"};
                builder.setTitle("更改配置工具频点")
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
                            case 0:selectFrequency(1);break;
                            case 1:selectFrequency(2);break;
                            case 2:selectFrequency(3);break;
                            case 3:selectFrequency(4);break;
                            case 4:selectFrequency(5);break;
                            case 5:selectFrequency(0);break;
                            default:
                                break;
                        }
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("取消",null);
                builder.create().show();
                break;
            case R.id.tv_copy:
                ClipboardManager cmb = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(tvSensorId.getText().toString());
                Toast.makeText(getActivity(), "已将唯一ID复制到剪切板！", Toast.LENGTH_SHORT).show();
                break;
            case R.id.read_config:
                if(!downloadFileFragment.openToastFlag){
                    Toast.makeText(getActivity(), "正在写入，请等待完成后再操作！", Toast.LENGTH_SHORT).show();
                    return;
                }
                byte[] writeBuffer = new byte[10];
                writeBuffer[0] = (byte) 0xff;
                writeBuffer[1] = (byte) 0xfe;
                writeBuffer[2] = (byte) 0x76;
                writeBuffer[3] = (byte) 0x0a;
                writeBuffer[4] = (byte) 0x03;
                writeBuffer[5] = (byte) 0x88;
                writeBuffer[6] = (byte) 0x00;
                writeBuffer[7] = Exclusive_Or(writeBuffer, 7);
                writeBuffer[8] = (byte) 0xab;
                writeBuffer[9] = (byte) 0xaa;
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
                if (TextUtils.isEmpty(etPosition.getText()) || TextUtils.isEmpty(etCity.getText())
                        || TextUtils.isEmpty(etArea.getText()) || TextUtils.isEmpty(etIP1.getText())
                        || TextUtils.isEmpty(etIP2.getText()) || TextUtils.isEmpty(etIP3.getText())
                        || TextUtils.isEmpty(etIP4.getText()) || TextUtils.isEmpty(etPort.getText())
                        || TextUtils.isEmpty(etStopTime.getText()) || TextUtils.isEmpty(etGapTime.getText()))
                {
                    Toast.makeText(getActivity(), "请确定基本信息是否填写完整", Toast.LENGTH_SHORT).show();
                    return;
                }
                int portL=0,portH=0;
                int port=Integer.parseInt(etPort.getText().toString(),10);
                portL=Integer.parseInt(Integer.toHexString(port),16)&0xff;
                portH=Integer.parseInt(Integer.toHexString(port),16)>>8;
                System.out.println("portH:"+portH+" portL:"+portL);

//              String string=Long.toHexString(Long.parseLong(etPosition.getText().toString(), 10));
                long position1=0,position2=0,position3=0,position4=0;
                long position=Long.parseLong(etPosition.getText().toString(), 16);
                position1=Long.parseLong(Long.toHexString(position >> 24 & 0xff), 10);
                position2=Long.parseLong(Long.toHexString(position >> 16 & 0xff), 10);
                position3=Long.parseLong(Long.toHexString(position >> 8 & 0xff), 10);
                position4=Long.parseLong(Long.toHexString(position & 0xff), 10);
                System.out.println("position:"+position1+" "+position2+" "+position3+" "+position4);

                byte[] writeBuff = new byte[34];
                writeBuff[0] = (byte) 0xff;
                writeBuff[1] = (byte) 0xfe;
                writeBuff[2] = (byte) 0x77;
                writeBuff[3] = (byte) 0x22;
                writeBuff[4] = (byte) 0x03;
                writeBuff[5] = sensorID[0];
                writeBuff[6] = sensorID[1];
                writeBuff[7] = sensorID[2];
                writeBuff[8] = sensorID[3];
                writeBuff[9] = sensorID[4];
                writeBuff[10] = sensorID[5];
                writeBuff[11] = (byte) Integer.parseInt(etIP1.getText().toString(), 10);
                writeBuff[12] = (byte) Integer.parseInt(etIP2.getText().toString(), 10);
                writeBuff[13] = (byte) Integer.parseInt(etIP3.getText().toString(), 10);
                writeBuff[14] = (byte) Integer.parseInt(etIP4.getText().toString(), 10);
                writeBuff[15] = (byte) portL;
                writeBuff[16] = (byte) portH;
                writeBuff[17] = (byte) 0x88;
                writeBuff[18] = (byte) 0x88;
                writeBuff[19] = (byte) Integer.parseInt(etCity.getText().toString(), 10);
                writeBuff[20] = (byte) Integer.parseInt(etArea.getText().toString(), 10);
                writeBuff[21] = (byte) position1;
                writeBuff[22] = (byte) position2;
                writeBuff[23] = (byte) position3;
                writeBuff[24] = (byte) position4;
                writeBuff[25] = (byte) Integer.parseInt(cat_put_select, 10);
                writeBuff[26] = (byte) Integer.parseInt(etStopTime.getText().toString(), 10);
                writeBuff[27] = (byte) (byte)Integer.parseInt(agility,10);
                writeBuff[28] = (byte) Integer.parseInt(etGapTime.getText().toString(), 10);
                writeBuff[29] = (byte) 0x88;
                writeBuff[30] = (byte) 0x00;
                writeBuff[31] = Exclusive_Or(writeBuff, 31);
                writeBuff[32] = (byte) 0xab;
                writeBuff[33] = (byte) 0xaa;

                mainActivity.target_chara.setValue(writeBuff);
                mainActivity. mService.writeCharacteristic(mainActivity.target_chara);

                break;
            case R.id.btnUpdateNB:
                System.out.println("升级！！！");
                if(!downloadFileFragment.openToastFlag){
                    Toast.makeText(getActivity(), "正在写入，请等待完成后再操作！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if((System.currentTimeMillis()-exitTime) > 2000) {
                    Toast.makeText(getActivity(), "再按一次开始升级", Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                    return;
                }
                else if(TextUtils.isEmpty(etArea.getText()) || TextUtils.isEmpty(etCity.getText())|| TextUtils.isEmpty(etPosition.getText())){
                    Toast.makeText(getActivity(), "升级需要填写市.区以及泊位信息！", Toast.LENGTH_SHORT).show();
                    return;
                }else if(sensorID[0]==0&&sensorID[1]==0&&sensorID[2]==0&&sensorID[3]==0&&sensorID[4]==0&&sensorID[5]==0){
                    Toast.makeText(getActivity(), "此次未获得传感器ID，请重新读取信息！", Toast.LENGTH_SHORT).show();
                    return;
                }

                long pos1=0,pos2=0,pos3=0,pos4=0;
                long pos=Long.parseLong(etPosition.getText().toString(), 16);
                pos1=Long.parseLong(Long.toHexString(pos >> 24 & 0xff), 10);
                pos2=Long.parseLong(Long.toHexString(pos >> 16 & 0xff), 10);
                pos3=Long.parseLong(Long.toHexString(pos >> 8 & 0xff), 10);
                pos4=Long.parseLong(Long.toHexString(pos  & 0xff),10);

                byte[] writeBuffer1 = new byte[30];
                writeBuffer1[0] = (byte) 0xff;
                writeBuffer1[1] = (byte) 0xfe;
                writeBuffer1[2] = (byte) 0x7D;
                writeBuffer1[3] = (byte) 0x1E;
                writeBuffer1[4] = (byte) 0x00;
                writeBuffer1[5] = sensorID[0];
                writeBuffer1[6] = sensorID[1];
                writeBuffer1[7] = sensorID[2];
                writeBuffer1[8] = sensorID[3];
                writeBuffer1[9] = sensorID[4];
                writeBuffer1[10] = sensorID[5];
                writeBuffer1[11] = (byte) 0x00;
                writeBuffer1[12] = (byte) 0x00;
                writeBuffer1[13] = (byte) 0x00;
                writeBuffer1[14] = (byte) 0x00;
                writeBuffer1[15] = (byte) 0x00;
                writeBuffer1[16] = (byte) 0x00;
                writeBuffer1[17] = (byte) point;
                writeBuffer1[18] = (byte) Integer.parseInt(etCity.getText().toString(), 10);
                writeBuffer1[19] = (byte) Integer.parseInt(etArea.getText().toString(), 10);
                writeBuffer1[20] = (byte) pos1;
                writeBuffer1[21] = (byte) pos2;
                writeBuffer1[22] = (byte) pos3;
                writeBuffer1[23] = (byte) pos4;
                writeBuffer1[24] = (byte) 0x01;//升级标志位
                writeBuffer1[25] = (byte) downloadFileFragment.totalNum;
                writeBuffer1[26] = (byte) 0x00;
                writeBuffer1[27] = Exclusive_Or(writeBuffer1, 27);
                writeBuffer1[28] = (byte) 0xab;
                writeBuffer1[29] = (byte) 0xaa;
                mainActivity.target_chara.setValue(writeBuffer1);
                mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
                break;
            case R.id.reset:
                if(!downloadFileFragment.openToastFlag){
                    Toast.makeText(getActivity(), "正在写入，请等待完成后再操作！", Toast.LENGTH_SHORT).show();
                    return;
                }
                byte[] buffer = new byte[15];
                buffer[0] = (byte) 0xff;
                buffer[1] = (byte) 0xfe;
                buffer[2] = (byte) 0x97;
                buffer[3] = (byte) 0x0F;
                buffer[4] = (byte) 0x03;
                buffer[5] = sensorID[0];
                buffer[6] = sensorID[1];
                buffer[7] = sensorID[2];
                buffer[8] = sensorID[3];
                buffer[9] = sensorID[4];
                buffer[10] = sensorID[5];
                buffer[11] = (byte) 0x00;
                buffer[12] = Exclusive_Or(buffer,12);
                buffer[13] = (byte) 0xab;
                buffer[14] = (byte) 0xaa;
                mainActivity.target_chara.setValue(buffer);
                mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
                break;
            case R.id.active:
                if(!downloadFileFragment.openToastFlag){
                    Toast.makeText(getActivity(), "正在写入，请等待完成后再操作！", Toast.LENGTH_SHORT).show();
                    return;
                }
                buffer = new byte[15];
                buffer[0] = (byte) 0xff;
                buffer[1] = (byte) 0xfe;
                buffer[2] = (byte) 0x70;
                buffer[3] = (byte) 0x0f;
                buffer[4] = (byte) 0x03;
                buffer[5] = sensorID[0];
                buffer[6] = sensorID[1];
                buffer[7] = sensorID[2];
                buffer[8] = sensorID[3];
                buffer[9] = sensorID[4];
                buffer[10] = sensorID[5];
                buffer[11] = (byte) 0x00;
                buffer[12] = Exclusive_Or(buffer, 12);
                buffer[13] = (byte) 0xab;
                buffer[14] = (byte) 0xaa;
                mainActivity.target_chara.setValue(buffer);
                mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
                break;
            case R.id.recovery:
                if(!downloadFileFragment.openToastFlag){
                    Toast.makeText(getActivity(), "正在写入，请等待完成后再操作！", Toast.LENGTH_SHORT).show();
                    return;
                }
                buffer = new byte[15];
                buffer[0] = (byte) 0xff;
                buffer[1] = (byte) 0xfe;
                buffer[2] = (byte) 0x90;
                buffer[3] = (byte) 0x0f;
                buffer[4] = (byte) 0x03;
                buffer[5] = sensorID[0];
                buffer[6] = sensorID[1];
                buffer[7] = sensorID[2];
                buffer[8] = sensorID[3];
                buffer[9] = sensorID[4];
                buffer[10] = sensorID[5];
                buffer[11] = (byte) 0x00;
                buffer[12] = Exclusive_Or(buffer, 12);
                buffer[13] = (byte) 0xab;
                buffer[14] = (byte) 0xaa;
                mainActivity.target_chara.setValue(buffer);
                mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
                break;
            case R.id.btn_endConfig:
                if(!downloadFileFragment.openToastFlag){
                    Toast.makeText(getActivity(), "正在写入，请等待完成后再操作！", Toast.LENGTH_SHORT).show();
                    return;
                }
                buffer = new byte[15];
                buffer[0] = (byte) 0xff;
                buffer[1] = (byte) 0xfe;
                buffer[2] = (byte) 0x9e;
                buffer[3] = (byte) 0x0f;
                buffer[4] = (byte) 0x03;
                buffer[5] = sensorID[0];
                buffer[6] = sensorID[1];
                buffer[7] = sensorID[2];
                buffer[8] = sensorID[3];
                buffer[9] = sensorID[4];
                buffer[10] = sensorID[5];
                buffer[11] = (byte) 0x00;
                buffer[12] = Exclusive_Or(buffer,12);
                buffer[13] = (byte) 0xab;
                buffer[14] = (byte) 0xaa;
                mainActivity.target_chara.setValue(buffer);
                mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
                break;
            case  R.id.btn_point:
                if(!downloadFileFragment.openToastFlag){
                    Toast.makeText(getActivity(), "正在写入，请等待完成后再操作！", Toast.LENGTH_SHORT).show();
                    return;
                }
                final int[] index1 = {0};
                AlertDialog.Builder builder1=new AlertDialog.Builder(getActivity());
                String[] frequencyStr1=new String[]{"430M","432M","436M","438M","440M","默认434M"};
                builder1.setTitle("选择一个升级频点")
                        .setSingleChoiceItems(frequencyStr1, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                index1[0] =which;
                            }
                        });
                builder1.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (index1[0]) {
                            case 0: point=1;break;
                            case 1: point=2;break;
                            case 2: point=3;break;
                            case 3: point=4;break;
                            case 4: point=5;break;
                            case 5: point=0;break;
                            default:break;
                        }
                        dialog.dismiss();
                    }
                });
                builder1.setPositiveButton("取消",null);
                builder1.create().show();
                break;
            case R.id.clear:
                downloadFileFragment.openToastFlag=true;
                mainActivity.rev_str = "";
                tvContent.setText("");
                break;
            case R.id.end:
                if(!downloadFileFragment.openToastFlag){
                    Toast.makeText(getActivity(), "正在写入，确定退出程序吗", Toast.LENGTH_SHORT).show();
                    //return;
                }
                if((System.currentTimeMillis()-exitTime) > 2000){
                    Toast.makeText(getActivity(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                    return;
                } else {
                    getActivity().finish();
                    System.exit(0);
                }
                break;
            default:
                return;
        }
    }
}
