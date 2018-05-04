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
public class NetGateFragment40 extends Fragment implements View.OnClickListener{
    public TextView tvContentNet;
    private TextView tvFNameNet;
    public ScrollView svContentNet;
    private Button btnReadConfigNet1;
    private Button btnReadConfigNet2;
    private Button btnWriteConfigNet;
    private Button clearNet;
    private Button btnResetNet;
    private Button btnUpdateNet;
    private Button btnEndConfigNet;

    private EditText etIdNet;
    private EditText etIP1Net;
    private EditText etIP2Net;
    private EditText etIP3Net;
    private EditText etIP4Net;
    private EditText etPortNet;

    private View view;
    private MainActivity mainActivity;
    private DownloadFileFragment downloadFileFragment=new DownloadFileFragment();
    private int[] netID;//网关的ID
    private long exitTime=0;
    private int totalNum=0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.net_gate_fragment40, container,false);
        mainActivity = (MainActivity) getActivity();
        initView();
        initEvent();
        setState(false);
        return view;


    }
    private void initView() {
        btnReadConfigNet1=(Button)view.findViewById(R.id.read_config_net1);
        btnReadConfigNet2=(Button)view.findViewById(R.id.read_config_net2);
        btnWriteConfigNet =(Button)view.findViewById(R.id.write_config_net);
        btnResetNet=(Button)view.findViewById(R.id.reset_net);
        btnUpdateNet=(Button)view.findViewById(R.id.update_net);
        btnEndConfigNet=(Button)view.findViewById(R.id.end_config_net);
        tvContentNet = (TextView) view.findViewById(R.id.tv_content_net);
        tvFNameNet = (TextView) view.findViewById(R.id.tv_FName_net);
        svContentNet = (ScrollView) view.findViewById(R.id.sv_content_net);
        clearNet = (Button) view.findViewById(R.id.clear_net);

        etIdNet =(EditText)view.findViewById(R.id.et_id_net);
        etIP1Net=(EditText)view.findViewById(R.id.et_ip1_net);
        etIP2Net=(EditText)view.findViewById(R.id.et_ip2_net);
        etIP3Net=(EditText)view.findViewById(R.id.et_ip3_net);
        etIP4Net=(EditText)view.findViewById(R.id.et_ip4_net);
        etPortNet=(EditText)view.findViewById(R.id.et_port_net);
        new RegionNumberEditTextUtils(getContext(),etIP1Net,0);
        new RegionNumberEditTextUtils(getContext(),etIP2Net,0);
        new RegionNumberEditTextUtils(getContext(),etIP3Net,0);
        new RegionNumberEditTextUtils(getContext(),etIP4Net,0);
        new RegionNumberEditTextUtils(getContext(),etPortNet,1);
        netID = new int[12];
        for(int i=0;i<12;i++) {
            netID[i] = 0;
        }
    }

    private void initEvent() {
        btnReadConfigNet1.setOnClickListener(this);
        btnReadConfigNet2.setOnClickListener(this);
        btnWriteConfigNet.setOnClickListener(this);
        btnResetNet.setOnClickListener(this);
        btnUpdateNet.setOnClickListener(this);
        btnEndConfigNet.setOnClickListener(this);
        clearNet.setOnClickListener(this);
    }

    public void setState(boolean b) {
        btnReadConfigNet1.setEnabled(b);
        btnReadConfigNet2.setEnabled(b);
        btnWriteConfigNet.setEnabled(b);
        btnResetNet.setEnabled(b);
        btnUpdateNet.setEnabled(b);
        btnEndConfigNet.setEnabled(b);
        etIdNet.setEnabled(b);
        etIP1Net.setEnabled(b);
        etIP2Net.setEnabled(b);
        etIP3Net.setEnabled(b);
        etIP4Net.setEnabled(b);
        etPortNet.setEnabled(b);
    }
    public void receiveDataDealNet(String string) {
        if(getCharByInt(string, 0)==0xff&&getCharByInt(string, 1)==0xfe) {
            System.out.println("帧标识：" + String.valueOf(getCharByInt(string, 2)));
            if (downloadFileFragment.openToastFlag) {
                switch (getCharByInt(string, 2)) {
                    case 0x26:
                        if(getCharByInt(string, 3)==0x18) {
                            Toast.makeText(getActivity(), "读取成功！", Toast.LENGTH_SHORT).show();
                            netID = new int[12];
                            String NetID = "";
                            netID[0] = getCharByInt(string, 4);
                            netID[1] = getCharByInt(string, 5);
                            netID[2] = getCharByInt(string, 6);
                            netID[3] = getCharByInt(string, 7);
                            netID[4] = getCharByInt(string, 8);
                            netID[5] = getCharByInt(string, 9);
                            netID[6] = getCharByInt(string, 10);
                            netID[7] = getCharByInt(string, 11);
                            netID[8] = getCharByInt(string, 12);
                            netID[9] = getCharByInt(string, 13);
                            netID[10] = getCharByInt(string, 14);
                            netID[11] = getCharByInt(string, 15);
                            for (int i = 0; i < 12; i++)
                                NetID = NetID + (getString(string, i + 4));
                            etIdNet.setText(NetID);
                        }
                        break;
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
                        tvFNameNet.setText(revFileNameString);
                        break;
                    case 0x27:
                        Toast.makeText(getActivity(), "配置IP成功！", Toast.LENGTH_SHORT).show();
                        break;
                    case 0x28:
                        Toast.makeText(getActivity(), "读取IP成功！", Toast.LENGTH_SHORT).show();
                        etIP1Net.setText(String.valueOf(getCharByInt(string, 17)));
                        etIP2Net.setText(String.valueOf(getCharByInt(string, 18)));
                        etIP3Net.setText(String.valueOf(getCharByInt(string, 19)));
                        etIP4Net.setText(String.valueOf(getCharByInt(string, 20)));
                        int portH = 0, portL = 0;
                        portH = getCharByInt(string, 21);
                        portL = getCharByInt(string, 22);
                        etPortNet.setText(String.valueOf(portH << 8 | portL));
                        break;
                    case 0x2D:
                        if(getCharByInt(string, 0)==0x18) {
                            Toast.makeText(getActivity(), "退出配置成功！", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }


    //string中提取字符串(string的start数位到end数位)，返回int数值
    private String getString(String string, int pos) {
        int start=0,end=0;
        start=pos*2;
        end=start+1;
        char buf[] = new char[2];
        string.getChars(start, end + 1, buf, 0);
        string = new String(buf);
        return string;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.read_config_net1:
                if (!downloadFileFragment.openToastFlag) {
                    Toast.makeText(getActivity(), "正在写入，请等待完成后再操作！", Toast.LENGTH_SHORT).show();
                    return;
                }
                byte[] writeBuffer = new byte[24];
                writeBuffer[0] = (byte) 0xff;
                writeBuffer[1] = (byte) 0xfe;
                writeBuffer[2] = (byte) 0x06;
                writeBuffer[3] = (byte) 0x18;
                writeBuffer[4] = (byte) 0x00;
                writeBuffer[5] = (byte) 0x00;
                writeBuffer[6] = (byte) 0x00;
                writeBuffer[7] = (byte) 0x00;
                writeBuffer[8] = (byte) 0x00;
                writeBuffer[9] = (byte) 0x00;
                writeBuffer[10] = (byte) 0x00;
                writeBuffer[11] = (byte) 0x00;
                writeBuffer[12] = (byte) 0x00;
                writeBuffer[13] = (byte) 0x00;
                writeBuffer[14] = (byte) 0x00;
                writeBuffer[15] = (byte) 0x00;
                writeBuffer[16] = (byte) 0x00;
                writeBuffer[17] = (byte) 0x00;
                writeBuffer[18] = (byte) 0x00;
                writeBuffer[19] = (byte) 0x00;
                writeBuffer[20] = (byte) 0x00;
                writeBuffer[21] = Exclusive_Or(writeBuffer, 21);
                writeBuffer[22] = (byte) 0xab;
                writeBuffer[23] = (byte) 0xaa;
                mainActivity.target_chara.setValue(writeBuffer);
                mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
                break;
            case R.id.read_config_net2:
                if (!downloadFileFragment.openToastFlag) {
                    Toast.makeText(getActivity(), "正在写入，请等待完成后再操作！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (netID[0]==0&&netID[1]==0&&netID[2]==0&&netID[3]==0&&netID[4]==0&&netID[5]==0&&netID[6]==0) {
                    Toast.makeText(getActivity(), "请先读取设备，传感器ID不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                byte[] buffer = new byte[32];
                buffer[0] = (byte) 0xff;
                buffer[1] = (byte) 0xfe;
                buffer[2] = (byte) 0x08;
                buffer[3] = (byte) 0x20;
                buffer[4] = (byte)netID[0];
                buffer[5] = (byte)netID[1];
                buffer[6] = (byte)netID[2];
                buffer[7] = (byte)netID[3];
                buffer[8] = (byte)netID[4];
                buffer[9] = (byte)netID[5];
                buffer[10] = (byte)netID[6];
                buffer[11] = (byte)netID[7];
                buffer[12] = (byte)netID[8];
                buffer[13] = (byte)netID[9];
                buffer[14] = (byte)netID[10];
                buffer[15] = (byte)netID[11];
                buffer[16] = (byte) 0x00;
                buffer[17] = (byte) 0x00;
                buffer[18] = (byte) 0x00;
                buffer[19] = (byte) 0x00;
                buffer[20] = (byte) 0x00;
                buffer[21] = (byte) 0x00;
                buffer[22] = (byte) 0x00;
                buffer[23] = (byte) 0x00;
                buffer[24] = (byte) 0x00;
                buffer[25] = (byte) 0x00;
                buffer[26] = (byte) 0x00;
                buffer[27] = (byte) 0x00;
                buffer[28] = (byte) 0x00;
                buffer[29] = Exclusive_Or(buffer, 29);
                buffer[30] = (byte) 0xab;
                buffer[31] = (byte) 0xaa;
                mainActivity.target_chara.setValue(buffer);
                mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
                break;
            case R.id.write_config_net:
                if (!downloadFileFragment.openToastFlag) {
                    Toast.makeText(getActivity(), "正在写入，请等待完成后再操作！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etIP1Net.getText()) || TextUtils.isEmpty(etIP2Net.getText())
                        || TextUtils.isEmpty(etIP3Net.getText()) || TextUtils.isEmpty(etIP4Net.getText())
                        || TextUtils.isEmpty(etPortNet.getText())) {
                    Toast.makeText(getActivity(), "请确定基本信息是否填写完整", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (netID[0]==0&&netID[1]==0&&netID[2]==0&&netID[3]==0&&netID[4]==0&&netID[5]==0&&netID[6]==0) {
                    Toast.makeText(getActivity(), "请先读取设备，传感器ID不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                int portH=0,portL=0;
                int port=Integer.parseInt(etPortNet.getText().toString(),10);
                portL=Integer.parseInt(Integer.toHexString(port),16)&0xff;
                portH=Integer.parseInt(Integer.toHexString(port),16)>>8;
                buffer = new byte[32];
                //System.out.println("oldSensorID："+oldSensorID);
                buffer[0] = (byte) 0xff;
                buffer[1] = (byte) 0xfe;
                buffer[2] = (byte) 0x07;
                buffer[3] = (byte) 0x20;
                buffer[4] =  (byte)netID[0];
                buffer[5] =  (byte)netID[1];
                buffer[6] =  (byte)netID[2];
                buffer[7] =  (byte)netID[3];
                buffer[8] =  (byte)netID[4];
                buffer[9] =  (byte)netID[5];
                buffer[10] = (byte)netID[6];
                buffer[11] = (byte)netID[7];
                buffer[12] = (byte)netID[8];
                buffer[13] = (byte)netID[9];
                buffer[14] = (byte)netID[10];
                buffer[15] = (byte)netID[11];
                buffer[16] = (byte)0x00;
                buffer[17] = (byte) Integer.parseInt(etIP1Net.getText().toString(), 10);
                buffer[18] = (byte) Integer.parseInt(etIP2Net.getText().toString(), 10);
                buffer[19] = (byte) Integer.parseInt(etIP3Net.getText().toString(), 10);
                buffer[20] = (byte) Integer.parseInt(etIP4Net.getText().toString(), 10);
                buffer[21] =  (byte) portH;
                buffer[22] = (byte) portL;
                buffer[23] = (byte) 0x00;
                buffer[24] = (byte) 0x00;
                buffer[25] = (byte) 0x00;
                buffer[26] = (byte) 0x00;
                buffer[27] = (byte) 0x00;
                buffer[28] = (byte) 0x00;
                buffer[29] = Exclusive_Or(buffer, 29);
                buffer[30] = (byte) 0xab;
                buffer[31] = (byte) 0xaa;
                mainActivity.target_chara.setValue(buffer);
                mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
                break;
            case R.id.reset_net:
                if (!downloadFileFragment.openToastFlag) {
                    Toast.makeText(getActivity(), "正在写入，请等待完成后再操作！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (netID[0]==0&&netID[1]==0&&netID[2]==0&&netID[3]==0&&netID[4]==0&&netID[5]==0&&netID[6]==0) {
                    Toast.makeText(getActivity(), "请先读取设备，传感器ID不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                byte[] buff = new byte[20];
                buff[0] = (byte) 0xff;
                buff[1] = (byte) 0xfe;
                buff[2] = (byte) 0x62;
                buff[3] = (byte) 0x14;
                buff[4] =  (byte)netID[0];
                buff[5] =  (byte)netID[1];
                buff[6] =  (byte)netID[2];
                buff[7] =  (byte)netID[3];
                buff[8] =  (byte)netID[4];
                buff[9] =  (byte)netID[5];
                buff[10] = (byte)netID[6];
                buff[11] = (byte)netID[7];
                buff[12] = (byte)netID[8];
                buff[13] = (byte)netID[9];
                buff[14] = (byte)netID[10];
                buff[15] = (byte)netID[11];
                buff[16] = (byte) 0x00;
                buff[17] =  Exclusive_Or(buff,17);
                buff[18] = (byte) 0xab;
                buff[19] = (byte) 0xaa;
                mainActivity.target_chara.setValue(buff);
                mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
                break;
            case R.id.update_net:
                if((System.currentTimeMillis()-exitTime) > 2000) {
                    Toast.makeText(getActivity(), "再按一次开始升级", Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                    return;
                }
                if(totalNum==0) {
                    Toast.makeText(getActivity(), "请先读取网关", Toast.LENGTH_SHORT).show();
                    return;
                }
                byte[] Buffer = new byte[22];
                Buffer[0] = (byte) 0xff;
                Buffer[1] = (byte) 0xfe;
                Buffer[2] = (byte) 0x60;
                Buffer[3] = (byte) 0x16;
                Buffer[4] =  (byte)netID[0];
                Buffer[5] =  (byte)netID[1];
                Buffer[6] =  (byte)netID[2];
                Buffer[7] =  (byte)netID[3];
                Buffer[8] =  (byte)netID[4];
                Buffer[9] =  (byte)netID[5];
                Buffer[10] = (byte)netID[6];
                Buffer[11] = (byte)netID[7];
                Buffer[12] = (byte)netID[8];
                Buffer[13] = (byte)netID[9];
                Buffer[14] = (byte)netID[10];
                Buffer[15] = (byte)netID[11];
                Buffer[16] = 0x00;
                Buffer[17] = (byte) totalNum;
                Buffer[18] = 0x00;
                Buffer[19] = Exclusive_Or(Buffer, 19);
                Buffer[20] = (byte) 0xab;
                Buffer[21] = (byte) 0xaa;
                mainActivity.target_chara.setValue(Buffer);
                mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
                break;
            case R.id.end_config_net:
                if (!downloadFileFragment.openToastFlag) {
                    Toast.makeText(getActivity(), "正在写入，请等待完成后再操作！", Toast.LENGTH_SHORT).show();
                    return;
                }
                byte[] Buff = new byte[24];
                Buff[0] = (byte) 0xff;
                Buff[1] = (byte) 0xfe;
                Buff[2] = (byte) 0x0d;
                Buff[3] = (byte) 0x18;
                Buff[4] = (byte) 0x00;
                Buff[5] = (byte) 0x00;
                Buff[6] = (byte) 0x00;
                Buff[7] = (byte) 0x00;
                Buff[8] = (byte) 0x00;
                Buff[9] = (byte) 0x00;
                Buff[10] = (byte) 0x00;
                Buff[11] = (byte) 0x00;
                Buff[12] = (byte) 0x00;
                Buff[13] = (byte) 0x00;
                Buff[14] = (byte) 0x00;
                Buff[15] = (byte) 0x00;
                Buff[16] = (byte) 0x00;
                Buff[17] = (byte) 0x00;
                Buff[18] = (byte) 0x00;
                Buff[19] = (byte) 0x00;
                Buff[20] = (byte) 0x00;
                Buff[21] = Exclusive_Or(Buff, 21);
                Buff[22] = (byte) 0xab;
                Buff[23] = (byte) 0xaa;
                mainActivity.target_chara.setValue(Buff);
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
