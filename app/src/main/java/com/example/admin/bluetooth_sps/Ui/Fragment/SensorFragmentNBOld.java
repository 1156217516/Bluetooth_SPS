package com.example.admin.bluetooth_sps.Ui.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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

/**
 * Created by admin on 2018/1/9.
 */
public class SensorFragmentNBOld extends Fragment implements View.OnClickListener{
    private View view;
    private MainActivity mainActivity;
    private DownloadFileFragment downloadFileFragment;
    private Button btnWriteConfig;
    private Button btnReset;
    private Button btnActive;
    private Button btnRecovery;
    private Button clear;
    private Button btnEndConfig;
    private Button btnEnd;
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
    public TextView tvContent;
    private Spinner spinAgility;
    private Spinner spinCarPut;
    public ScrollView svContent;

    private String cat_put_select;
    private String agility;
    private byte[] sensorID=new byte[12];//地磁的ID
    public long exitTime = 0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sensor_fragment_nb_old, container, false);
        mainActivity = (MainActivity) getActivity();
        downloadFileFragment=new DownloadFileFragment();
        init();
        setState(false);
        initEvent();

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
        btnWriteConfig = (Button) view.findViewById(R.id.write_config);
        btnReset = (Button) view.findViewById(R.id.reset);
        btnActive = (Button) view.findViewById(R.id.active);
        btnRecovery = (Button) view.findViewById(R.id.recovery);
        clear = (Button) view.findViewById(R.id.clear);
        btnEndConfig = (Button) view.findViewById(R.id.btn_endConfig);
        btnEnd = (Button) view.findViewById(R.id.end);
        tvContent = (TextView) view.findViewById(R.id.tv_content);
        etPosition = (EditText) view.findViewById(R.id.et_position);
        etCity = (EditText) view.findViewById(R.id.et_city);
        etArea = (EditText) view.findViewById(R.id.et_area);
        etIP1 = (EditText) view.findViewById(R.id.et_ip1);
        etIP2 = (EditText) view.findViewById(R.id.et_ip2);
        etIP3 = (EditText) view.findViewById(R.id.et_ip3);
        etIP4 = (EditText) view.findViewById(R.id.et_ip4);
        etPort = (EditText) view.findViewById(R.id.et_port);
        etStopTime = (EditText) view.findViewById(R.id.stop_time);
        etGapTime = (EditText) view.findViewById(R.id.gap_time);
        svContent = (ScrollView) view.findViewById(R.id.sv_content);
        spinCarPut = (Spinner) view.findViewById(R.id.car_put);
        spinAgility = (Spinner) view.findViewById(R.id.lingMin);
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
        for(int i=0;i<12;i++)
            sensorID[i]=0;
    }

    private void initEvent() {
        btnWriteConfig.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        btnActive.setOnClickListener(this);
        clear.setOnClickListener(this);
        btnRecovery.setOnClickListener(this);
        btnEndConfig.setOnClickListener(this);
        btnEnd.setOnClickListener(this);
    }

    public void setState(boolean b) {
        tvContent.setEnabled(b);
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
        svContent.setEnabled(b);
        spinAgility.setEnabled(b);
        spinCarPut.setEnabled(b);
        btnWriteConfig.setEnabled(b);
        btnReset.setEnabled(b);
        btnActive.setEnabled(b);
        clear.setEnabled(b);
        btnRecovery.setEnabled(b);
        btnEndConfig.setEnabled(b);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.write_config:
                if(!downloadFileFragment.openToastFlag){
                    Toast.makeText(getActivity(), "正在写入，请等待完成后再操作！", Toast.LENGTH_SHORT).show();
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
                long position1=0,position2=0,position3=0,position4=0;
                long position=Long.parseLong(etPosition.getText().toString(), 16);
                position1=Long.parseLong(Long.toHexString(position >> 24 & 0xff), 10);
                position2=Long.parseLong(Long.toHexString(position >> 16 & 0xff), 10);
                position3=Long.parseLong(Long.toHexString(position >> 8 & 0xff), 10);
                position4=Long.parseLong(Long.toHexString(position & 0xff), 10);

                byte[] writeBuff = new byte[28];
                writeBuff[0] = (byte) 0xff;
                writeBuff[1] = (byte) 0xfe;
                writeBuff[2] = (byte) 0x77;
                writeBuff[3] = (byte) 0x1c;
                writeBuff[4] = (byte) 0x03;
                writeBuff[5] = (byte) Integer.parseInt(etIP1.getText().toString(), 10);
                writeBuff[6] = (byte) Integer.parseInt(etIP2.getText().toString(), 10);
                writeBuff[7] = (byte) Integer.parseInt(etIP3.getText().toString(), 10);
                writeBuff[8] = (byte) Integer.parseInt(etIP4.getText().toString(), 10);
                writeBuff[9] = (byte) portL;
                writeBuff[10] = (byte) portH;
                writeBuff[11] = (byte) 0x00;
                writeBuff[12] = (byte) 0x00;
                writeBuff[13] = (byte) Integer.parseInt(etCity.getText().toString(), 10);
                writeBuff[14] = (byte) Integer.parseInt(etArea.getText().toString(), 10);
                writeBuff[15] = (byte) position1;
                writeBuff[16] = (byte) position2;
                writeBuff[17] = (byte) position3;
                writeBuff[18] = (byte) position4;
                writeBuff[19] = (byte) Integer.parseInt(cat_put_select, 10);
                writeBuff[20] = (byte) Integer.parseInt(etStopTime.getText().toString(), 10);
                writeBuff[21] = (byte) (byte)Integer.parseInt(agility,10);
                writeBuff[22] = (byte) Integer.parseInt(etGapTime.getText().toString(), 10);
                writeBuff[23] = (byte) 0x00;
                writeBuff[24] = (byte) 0x00;
                writeBuff[25] = Exclusive_Or(writeBuff, 25);
                writeBuff[26] = (byte) 0xab;
                writeBuff[27] = (byte) 0xaa;
                mainActivity.target_chara.setValue(writeBuff);
                mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
                break;
            case R.id.reset:
                if(!downloadFileFragment.openToastFlag){
                    Toast.makeText(getActivity(), "正在写入，请等待完成后再操作！", Toast.LENGTH_SHORT).show();
                    return;
                }
                byte[] writeBuffer = new byte[10];
                writeBuffer[0] = (byte) 0xff;
                writeBuffer[1] = (byte) 0xfe;
                writeBuffer[2] = (byte) 0x97;
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
            case R.id.active:
                if(!downloadFileFragment.openToastFlag){
                    Toast.makeText(getActivity(), "正在写入，请等待完成后再操作！", Toast.LENGTH_SHORT).show();
                    return;
                }
                writeBuffer = new byte[10];
                writeBuffer[0] = (byte) 0xff;
                writeBuffer[1] = (byte) 0xfe;
                writeBuffer[2] = (byte) 0x70;
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
            case R.id.recovery:
                if(!downloadFileFragment.openToastFlag){
                    Toast.makeText(getActivity(), "正在写入，请等待完成后再操作！", Toast.LENGTH_SHORT).show();
                    return;
                }
                writeBuffer = new byte[10];
                writeBuffer[0] = (byte) 0xff;
                writeBuffer[1] = (byte) 0xfe;
                writeBuffer[2] = (byte) 0x90;
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
            case R.id.btn_endConfig:
                if(!downloadFileFragment.openToastFlag){
                    Toast.makeText(getActivity(), "正在写入，请等待完成后再操作！", Toast.LENGTH_SHORT).show();
                    return;
                }
                writeBuffer = new byte[10];
                writeBuffer[0] = (byte) 0xff;
                writeBuffer[1] = (byte) 0xfe;
                writeBuffer[2] = (byte) 0x9e;
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
                    getActivity().finish();
                    System.exit(0);
                }
                break;
            default:
                return;
        }
    }
}
