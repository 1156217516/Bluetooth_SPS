package com.example.admin.bluetooth_sps.Ui.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
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
public class NetGateFragment20 extends Fragment implements View.OnClickListener{
    public TextView tvContentNet;
    private TextView tvFNameNet;
    public ScrollView svContentNet;
    private Button btnReadConfigNet1;
    private Button btnWriteConfigNet1;
    private Button btnReadConfigNet2;
    private Button btnWriteConfigNet2;
    private Button clearNet;
    private Button btnResetNet;
    private Button btnUpdateNet;
    private Button btnEndConfigNet;

    private EditText etIDNet;
    private EditText etProvinceNet;
    private EditText etCityNet;
    private EditText etAreaNet;
    private EditText etStreetNet;
    private EditText etPositionNet;
    private EditText etIP1Net;
    private EditText etIP2Net;
    private EditText etIP3Net;
    private EditText etIP4Net;
    private EditText etPortNet;


    private View view;
    private MainActivity mainActivity;
    private DownloadFileFragment downloadFileFragment;
    private int NetFirstID=0;
    private long exitTime=0;
    private int totalNum=0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.net_gate_fragment20, container,false);
        mainActivity = (MainActivity) getActivity();
        downloadFileFragment=new DownloadFileFragment();
        initView();
        initEvent();
        setState(false);
        return view;


    }
    private void initView() {
        btnReadConfigNet1=(Button)view.findViewById(R.id.read_config_net1);
        btnWriteConfigNet1=(Button)view.findViewById(R.id.write_config_net1);
        btnReadConfigNet2=(Button)view.findViewById(R.id.read_config_net2);
        btnWriteConfigNet2=(Button)view.findViewById(R.id.write_config_net2);
        btnResetNet=(Button)view.findViewById(R.id.reset_net);
        btnUpdateNet=(Button)view.findViewById(R.id.update_net);
        btnEndConfigNet=(Button)view.findViewById(R.id.end_config_net);
        tvContentNet = (TextView) view.findViewById(R.id.tv_content_net);
        tvFNameNet = (TextView) view.findViewById(R.id.tv_FName_net);
        svContentNet = (ScrollView) view.findViewById(R.id.sv_content_net);
        clearNet = (Button) view.findViewById(R.id.clear_net);

        etIDNet=(EditText)view.findViewById(R.id.et_ID_net);
        etProvinceNet=(EditText)view.findViewById(R.id.et_province_net);
        etCityNet=(EditText)view.findViewById(R.id.et_city_net);
        etAreaNet=(EditText)view.findViewById(R.id.et_area_net);
        etStreetNet=(EditText)view.findViewById(R.id.et_street_net);
        etPositionNet=(EditText)view.findViewById(R.id.et_position_net);
        etIP1Net=(EditText)view.findViewById(R.id.et_ip1_net);
        etIP2Net=(EditText)view.findViewById(R.id.et_ip2_net);
        etIP3Net=(EditText)view.findViewById(R.id.et_ip3_net);
        etIP4Net=(EditText)view.findViewById(R.id.et_ip4_net);
        etPortNet=(EditText)view.findViewById(R.id.et_port_net);
        new RegionNumberEditTextUtils(getContext(),etIDNet,0);
        new RegionNumberEditTextUtils(getContext(),etProvinceNet,5);
        new RegionNumberEditTextUtils(getContext(),etCityNet,5);
        new RegionNumberEditTextUtils(getContext(),etAreaNet,5);
        new RegionNumberEditTextUtils(getContext(),etStreetNet,5);
        new RegionNumberEditTextUtils(getContext(),etPositionNet,5);
        new RegionNumberEditTextUtils(getContext(),etIP1Net,0);
        new RegionNumberEditTextUtils(getContext(),etIP2Net,0);
        new RegionNumberEditTextUtils(getContext(),etIP3Net,0);
        new RegionNumberEditTextUtils(getContext(),etIP4Net,0);
        new RegionNumberEditTextUtils(getContext(),etPortNet,1);

    }

    private void initEvent() {
        btnReadConfigNet1.setOnClickListener(this);
        btnWriteConfigNet1.setOnClickListener(this);
        btnReadConfigNet2.setOnClickListener(this);
        btnWriteConfigNet2.setOnClickListener(this);
        btnResetNet.setOnClickListener(this);
        btnUpdateNet.setOnClickListener(this);
        btnEndConfigNet.setOnClickListener(this);
        clearNet.setOnClickListener(this);
    }

    public void setState(boolean b) {
        btnReadConfigNet1.setEnabled(b);
        btnWriteConfigNet1.setEnabled(b);
        btnReadConfigNet2.setEnabled(b);
        btnWriteConfigNet2.setEnabled(b);
        btnResetNet.setEnabled(b);
        btnUpdateNet.setEnabled(b);
        btnEndConfigNet.setEnabled(b);
        etIDNet.setEnabled(b);
        etProvinceNet.setEnabled(b);
        etCityNet.setEnabled(b);
        etAreaNet.setEnabled(b);
        etStreetNet.setEnabled(b);
        etPositionNet.setEnabled(b);
        etIP1Net.setEnabled(b);
        etIP2Net.setEnabled(b);
        etIP3Net.setEnabled(b);
        etIP4Net.setEnabled(b);
        etPortNet.setEnabled(b);
    }
    public void receiveDataDealNet(String string) {
        if(string!=null&&getCharByInt(string,0)==0xff&&getCharByInt(string,1)==0xfe) {
            System.out.println("帧标识：" + String.valueOf(getCharByInt(string, 2)));
            if (downloadFileFragment.openToastFlag) {
                switch (getCharByInt(string, 2)) {
                    case 0x66:
                        String revFileNameString="";
                        totalNum = getCharByInt(string, 4);
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
                        tvFNameNet.setText("配置工具程序："+"\n"+revFileNameString);
                        break;
                    case 0x2A:
                        if(getCharByInt(string, 3)==0x14) {
                            Toast.makeText(getActivity(), "读取成功！", Toast.LENGTH_SHORT).show();
                            NetFirstID = getCharByInt(string, 10);
                            etIDNet.setText(String.valueOf(getCharByInt(string, 10)));
                            etProvinceNet.setText(string.substring(22, 24));
                            etCityNet.setText(string.substring(24, 26));
                            etAreaNet.setText(string.substring(26, 28));
                            etStreetNet.setText(string.substring(28, 30));
                            etPositionNet.setText(string.substring(30, 32));
                        }
                        break;
                    case 0x2C:
                        Toast.makeText(getActivity(), "读取IP成功！", Toast.LENGTH_SHORT).show();
                        etIP1Net.setText(String.valueOf(getCharByInt(string, 10)));
                        etIP2Net.setText(String.valueOf(getCharByInt(string, 11)));
                        etIP3Net.setText(String.valueOf(getCharByInt(string, 12)));
                        etIP4Net.setText(String.valueOf(getCharByInt(string, 13)));
                        int portH = 0, portL = 0;
                        portH = getCharByInt(string, 14);
                        portL = getCharByInt(string, 15);
                        etPortNet.setText(String.valueOf(portH << 8 | portL));
                        break;
                    case 0x2B:
                        if(getCharByInt(string, 3)==0x0E)
                            Toast.makeText(getActivity(), "配置成功！", Toast.LENGTH_SHORT).show();
                        break;
                    case 0x2D:
						if(getCharByInt(string, 3)==0x0E)
							Toast.makeText(getActivity(), "配置IP成功！", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.read_config_net1:
                if (!downloadFileFragment.openToastFlag) {
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
                writeBuffer[6] = (byte) 0x02;
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
            case R.id.write_config_net1:
                if (!downloadFileFragment.openToastFlag) {
                    Toast.makeText(getActivity(), "正在写入，请等待完成后再操作！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etIDNet.getText()) || TextUtils.isEmpty(etProvinceNet.getText())
                        || TextUtils.isEmpty(etCityNet.getText()) || TextUtils.isEmpty(etAreaNet.getText())
                        || TextUtils.isEmpty(etStreetNet.getText()) || TextUtils.isEmpty(etPositionNet.getText())) {
                    Toast.makeText(getActivity(), "请确定基本信息是否填写完整", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(NetFirstID ==0){
                    Toast.makeText(getActivity(), "请先获取设备信息！", Toast.LENGTH_SHORT).show();
                    return;
                }
                byte[] writeBuff = new byte[20];
                //System.out.println("oldSensorID："+oldSensorID);
                writeBuff[0] = (byte) 0xff;
                writeBuff[1] = (byte) 0xfe;
                writeBuff[2] = (byte) 0x0b;
                writeBuff[3] = (byte) 0x14;
                writeBuff[4] = (byte) NetFirstID;
                writeBuff[5] = (byte) 0x30;
                writeBuff[6] = (byte) 0x02;
                writeBuff[7] = (byte) 0x00;
                writeBuff[8] = (byte) 0x07;
                writeBuff[9] = (byte) 0x30;
                writeBuff[10] = (byte) Integer.parseInt(etIDNet.getText().toString(), 10);
                writeBuff[11] = (byte) Integer.parseInt(etProvinceNet.getText().toString(), 16);
                writeBuff[12] = (byte) Integer.parseInt(etCityNet.getText().toString(), 16);
                writeBuff[13] = (byte) Integer.parseInt(etAreaNet.getText().toString(), 16);
                writeBuff[14] = (byte) Integer.parseInt(etStreetNet.getText().toString(), 16);
                writeBuff[15] = (byte) Integer.parseInt(etPositionNet.getText().toString(), 16);
                writeBuff[16] = (byte) 0x00;
                writeBuff[17] = Exclusive_Or(writeBuff, 17);
                writeBuff[18] = (byte) 0xab;
                writeBuff[19] = (byte) 0xaa;
                mainActivity.target_chara.setValue(writeBuff);
                mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
                break;

            case R.id.read_config_net2:
                if (!downloadFileFragment.openToastFlag) {
                    Toast.makeText(getActivity(), "正在写入，请等待完成后再操作！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(NetFirstID ==0){
                    Toast.makeText(getActivity(), "请先获取设备信息！", Toast.LENGTH_SHORT).show();
                    return;
                }
                writeBuffer = new byte[14];
                writeBuffer[0] = (byte) 0xff;
                writeBuffer[1] = (byte) 0xfe;
                writeBuffer[2] = (byte) 0x0c;
                writeBuffer[3] = (byte) 0x0e;
                writeBuffer[4] = (byte) NetFirstID;
                writeBuffer[5] = (byte) 0x30;
                writeBuffer[6] = (byte) 0x02;
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
            case R.id.write_config_net2:
                if (!downloadFileFragment.openToastFlag) {
                    Toast.makeText(getActivity(), "正在写入，请等待完成后再操作！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etIP1Net.getText()) || TextUtils.isEmpty(etIP2Net.getText())
                        || TextUtils.isEmpty(etIP3Net.getText()) || TextUtils.isEmpty(etIP4Net.getText())
                        || TextUtils.isEmpty(etPortNet.getText())||TextUtils.isEmpty(etIDNet.getText())) {
                    Toast.makeText(getActivity(), "请确定基本信息是否填写完整", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(NetFirstID ==0){
                    Toast.makeText(getActivity(), "请先获取设备信息！", Toast.LENGTH_SHORT).show();
                    return;
                }
                int portH=0,portL=0;
                int port=Integer.parseInt(etPortNet.getText().toString(),10);
                portL=Integer.parseInt(Integer.toHexString(port),16)&0xff;
                portH=Integer.parseInt(Integer.toHexString(port),16)>>8;
                writeBuff = new byte[20];
                //System.out.println("oldSensorID："+oldSensorID);
                writeBuff[0] = (byte) 0xff;
                writeBuff[1] = (byte) 0xfe;
                writeBuff[2] = (byte) 0x0d;
                writeBuff[3] = (byte) 0x14;
                writeBuff[4] = (byte) Integer.parseInt(etIDNet.getText().toString(),10);
                writeBuff[5] = (byte) 0x30;
                writeBuff[6] = (byte) 0x02;
                writeBuff[7] = (byte) 0x00;
                writeBuff[8] = (byte) 0x07;
                writeBuff[9] = (byte) 0x30;
                writeBuff[10] = (byte) Integer.parseInt(etIP1Net.getText().toString(), 10);
                writeBuff[11] = (byte) Integer.parseInt(etIP2Net.getText().toString(), 10);
                writeBuff[12] = (byte) Integer.parseInt(etIP3Net.getText().toString(), 10);
                writeBuff[13] = (byte) Integer.parseInt(etIP4Net.getText().toString(), 10);
                writeBuff[14] = (byte) portH;
                writeBuff[15] = (byte) portL;
                writeBuff[16] = (byte) 0x00;
                writeBuff[17] = Exclusive_Or(writeBuff, 17);
                writeBuff[18] = (byte) 0xab;
                writeBuff[19] = (byte) 0xaa;
                mainActivity.target_chara.setValue(writeBuff);
                mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
                break;
            case R.id.reset_net:
                if (!downloadFileFragment.openToastFlag) {
                    Toast.makeText(getActivity(), "正在写入，请等待完成后再操作！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etIDNet.getText())) {
                    Toast.makeText(getActivity(), "传感器ID不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                byte[] Buffer = new byte[10];
                Buffer[0] = (byte) 0xff;
                Buffer[1] = (byte) 0xfe;
                Buffer[2] = (byte) 0x62;
                Buffer[3] = (byte) 0x0a;
                Buffer[4] = (byte) Integer.parseInt(etIDNet.getText().toString(), 10);
                Buffer[5] = (byte) Integer.parseInt(etIDNet.getText().toString(), 10);
                Buffer[6] = (byte) 0x00;
                Buffer[7] =  Exclusive_Or(Buffer,7);
                Buffer[8] = (byte) 0xab;
                Buffer[9] = (byte) 0xaa;
                mainActivity.target_chara.setValue(Buffer);
                mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
                break;
            case R.id.update_net:
                if((System.currentTimeMillis()-exitTime) > 2000) {
                    Toast.makeText(getActivity(), "再按一次开始升级", Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                    return;
                }
                else if(TextUtils.isEmpty(etIDNet.getText())){
                    Toast.makeText(getActivity(), "传感器ID不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(totalNum==0) {
                    Toast.makeText(getActivity(), "请先读取网关", Toast.LENGTH_SHORT).show();
                    return;
                }
                writeBuffer = new byte[14];
                writeBuffer[0] = (byte) 0xff;
                writeBuffer[1] = (byte) 0xfe;
                writeBuffer[2] = (byte) 0x60;
                writeBuffer[3] = (byte) 0x0e;
                writeBuffer[4] = (byte) Integer.parseInt(etIDNet.getText().toString(), 10);
                writeBuffer[5] = (byte) Integer.parseInt(etIDNet.getText().toString(), 10);
                writeBuffer[6] = (byte) 0x02;
                writeBuffer[7] = (byte) 0x00;
                writeBuffer[8] = (byte) 0x01;
                writeBuffer[9] = (byte) totalNum;
                writeBuffer[10] = (byte) 0x00;
                writeBuffer[11] = Exclusive_Or(writeBuffer, 11);
                writeBuffer[12] = (byte) 0xab;
                writeBuffer[13] = (byte) 0xaa;
                mainActivity.target_chara.setValue(writeBuffer);
                mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
                break;
            case R.id.end_config_net:
                if (!downloadFileFragment.openToastFlag) {
                    Toast.makeText(getActivity(), "正在写入，请等待完成后再操作！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(NetFirstID ==0){
                    Toast.makeText(getActivity(), "请先获取设备信息！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(etIDNet.getText())){
                    Toast.makeText(getActivity(), "请先输入网关ID！", Toast.LENGTH_SHORT).show();
                    return;
                }
                writeBuffer = new byte[14];
                writeBuffer[0] = (byte) 0xff;
                writeBuffer[1] = (byte) 0xfe;
                writeBuffer[2] = (byte) 0x1a;
                writeBuffer[3] = (byte) 0x0e;
                writeBuffer[4] = (byte) Integer.parseInt(etIDNet.getText().toString(),10);
                writeBuffer[5] = (byte) 0x30;
                writeBuffer[6] = (byte) 0x02;
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
            case R.id.clear_net:
                downloadFileFragment.openToastFlag=true;
                mainActivity.setRev_str("");
                tvContentNet.setText("");
                break;
            default:
                break;
        }
    }

}
