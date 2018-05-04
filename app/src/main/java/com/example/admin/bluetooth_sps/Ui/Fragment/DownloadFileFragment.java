package com.example.admin.bluetooth_sps.Ui.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.bluetooth_sps.File.FileScan;
import com.example.admin.bluetooth_sps.R;
import com.example.admin.bluetooth_sps.Ui.Activity.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import static com.example.admin.bluetooth_sps.Utils.MyUtils.Exclusive_Or;
import static com.example.admin.bluetooth_sps.Utils.MyUtils.getCharByInt;

/**
 * Created by admin on 2017/11/7.
 */
public class DownloadFileFragment extends Fragment implements View.OnClickListener{
    public final static String AbsolutePATH = "/mnt/sdcard/Download/";
    private final static char RECEIVE_READY = '0';
    private final static char RECEIVE_OK = '1';
    private final static char RECEIVE_Number_ERROR = '2';
    private final static char PROGRAM_FLASH = '3';
    private final static char FLASH_ERROR = '4';
    private final static char TOTAL_NUMBER_ERROR = '5';
    private final static char DATA_LEN_ERROR = '6';
    private final static char DEVICE_RESET = '7';
    private final static char UPDATE_FINISH = '8';

    private FileInputStream fis;
    //发送数据包长度
    private final static int FILESIZE = 256;
    //private final static int DATANUM = 256;
    private final static int SENDDATANUM = 268;//256+12
    private byte[] sendFileBuf;//每一包发送的数据包
    private byte[] buffer;//存储文件所有数据
    private int sendDataSize = 0;//文件总大小
    private int tempDataSize = 0;//当前文件位置
    public int totalNum = 0;//总包数
    private int parkNum = 0;//当前包数
    public String fileNameString;//文件名
    //public String revFileNameString;//收到的书包解析成文件名;

    public boolean openToastFlag=true;//表示是否在传输文件，主要作用是屏蔽弹窗
    public boolean updateFlag=true;//表示升级标志，主要作用是屏蔽81包处理
    public ArrayList<String> list;
    private Button btnSelectFile;

    public TextView tvFilePath;
    private TextView tvInit;
    private TextView tvSendByHand;
    public String mFilePath;//最终文件路径


    private View view;
    private MainActivity mainActivity;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.download_file_fragment, container,false);
        mainActivity = (MainActivity) getActivity();
        btnSelectFile = (Button) view.findViewById(R.id.btn_select_file);
        tvFilePath = (TextView) view.findViewById(R.id.filePath);
        tvInit = (TextView)view.findViewById(R.id.tvInit);
        tvSendByHand = (TextView)view.findViewById(R.id.tvSendByHand);
        btnSelectFile.setOnClickListener(this);
        tvFilePath.setOnClickListener(this);
        tvInit.setOnClickListener(this);
        tvSendByHand.setOnClickListener(this);
        return view;
    }

    public void receiveDataDeal(String string){
        System.out.println("string:"+string);
        if(getCharByInt(string,0)==0xff&&getCharByInt(string,1)==0xfe) {
            if (openToastFlag) {
                switch (getCharByInt(string, 2)) {
                    case 131://0x83
                        updateFlag=false;
                        Toast.makeText(getActivity(), "开始发送升级程序,请稍等...", Toast.LENGTH_SHORT).show();
                        if(!updateFlag)
                            sendFileFunction();
                        tvSendByHand.setText(String.valueOf(parkNum));
                        tvSendByHand.append("/" + totalNum);
                        break;
                }
            }
            if(getCharByInt(string, 2)==128)
                updateFlag=true;
            //0x81
            if (getCharByInt(string, 2)==129) {
                tvSendByHand.setText(String.valueOf(parkNum + 1));
                tvSendByHand.append("/" + totalNum);
                int lastParkNum = 0;
                int Data0 = 0, Data1 = 0;
                int tempOneDataNum = SENDDATANUM;
                openToastFlag = false;
                switch (string.charAt(13)) {
                    case RECEIVE_READY:
                        if(!updateFlag){
                            //Log.d("test","buffer.length:"+buffer.length);
                            if (getCharByInt(string, 5) - parkNum != 1)//检测是否请求的是成功的下一包，如果不是我就返回错误提示
                            {
                                Toast.makeText(getActivity(), "设备内部时序发生错误,本软件直接点击传输即可", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                parkNum = getCharByInt(string, 5);
                                if (parkNum == totalNum - 1)//最后一包
                                {
                                    lastParkNum = (sendDataSize - (parkNum * FILESIZE));//最后的余量
                                    Data0 = (lastParkNum + 12) >> 8 & 0xFF;
                                    Data1 = (lastParkNum + 12) & 0xFF;
                                } else {
                                    lastParkNum = FILESIZE;
                                    Data0 = tempOneDataNum >> 8 & 0xFF;
                                    Data1 = tempOneDataNum & 0xFF;
                                }
                                sendFileBuf = new byte[lastParkNum + 12];
                                tempDataSize = tempDataSize + FILESIZE;
                                sendFileBuf[0] = (byte) 0xFF;
                                sendFileBuf[1] = (byte) 0xFE;
                                sendFileBuf[2] = (byte) 0x61;
                                sendFileBuf[3] = (byte) Data0;
                                sendFileBuf[4] = (byte) Data1;
                                sendFileBuf[5] = (byte) totalNum;
                                sendFileBuf[6] = (byte) parkNum;

                                if (lastParkNum == FILESIZE) {
                                    sendFileBuf[7] = 0x00;
                                    System.arraycopy(buffer, tempDataSize, sendFileBuf, 8, lastParkNum);
                                    sendFileBuf[FILESIZE + 8] = (byte) 0x30;
                                    sendFileBuf[FILESIZE + 9] = Exclusive_Or(sendFileBuf, tempOneDataNum - 3);
                                    sendFileBuf[FILESIZE + 10] = (byte) 0xAB;
                                    sendFileBuf[FILESIZE + 11] = (byte) 0xAA;

                                    mainActivity.target_chara.setValue(sendFileBuf);
                                    mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
                                } else {
                                    sendFileBuf[7] = (byte) lastParkNum;
                                    System.arraycopy(buffer, tempDataSize, sendFileBuf, 8, lastParkNum);
                                    sendFileBuf[lastParkNum + 8] = (byte) 0x30;
                                    sendFileBuf[lastParkNum + 9] = Exclusive_Or(sendFileBuf, lastParkNum + 12 - 3);
                                    sendFileBuf[lastParkNum + 10] = (byte) 0xAB;
                                    sendFileBuf[lastParkNum + 11] = (byte) 0xAA;

                                    mainActivity.target_chara.setValue(sendFileBuf);
                                    mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
                                    openToastFlag = true;
                                }
//                            Log.d("RECEIVE_READY","buffer.length:"+buffer.length);
//                            Log.d("test", "tempDataSize:" + tempDataSize);
//                            Log.d("test", "lastParkNum:" + lastParkNum);
                            }
                        }
                        break;
                    case RECEIVE_Number_ERROR:
                        Toast.makeText(getActivity(), "设备接收到的数据包编号不正确,程序将自动发送,请勿操作软件,请稍等...", Toast.LENGTH_SHORT).show();
                        if(!updateFlag) {
                            parkNum = getCharByInt(string, 5);
                            if (parkNum == totalNum - 1)//判断是否是最后一包
                            {
                                lastParkNum = (sendDataSize - (parkNum * FILESIZE));//最后的余量
                                Data0 = (lastParkNum + 12) >> 8 & 0xFF;
                                Data1 = (lastParkNum + 12) & 0xFF;
                            } else {
                                lastParkNum = FILESIZE;
                                Data0 = tempOneDataNum >> 8 & 0xFF;
                                Data1 = tempOneDataNum & 0xFF;
                            }
                            sendFileBuf = new byte[lastParkNum + 12];
                            tempDataSize = parkNum * FILESIZE;
                            //到这步肯定是tempDatasize在此包之前已经计算过了,这里不需要计算了。
                            sendFileBuf[0] = (byte) 0xFF;
                            sendFileBuf[1] = (byte) 0xFE;
                            sendFileBuf[2] = (byte) 0x61;
                            sendFileBuf[3] = (byte) Data0;
                            sendFileBuf[4] = (byte) Data1;
                            sendFileBuf[5] = (byte) totalNum;
                            sendFileBuf[6] = (byte) parkNum;
                            if (lastParkNum == FILESIZE) {
                                sendFileBuf[7] = 0x00;
                                System.arraycopy(buffer, tempDataSize, sendFileBuf, 8, lastParkNum);
                                sendFileBuf[FILESIZE + 8] = (byte) 0x30;
                                sendFileBuf[FILESIZE + 9] = Exclusive_Or(sendFileBuf, tempOneDataNum - 3);
                                sendFileBuf[FILESIZE + 10] = (byte) 0xAB;
                                sendFileBuf[FILESIZE + 11] = (byte) 0xAA;
                                //tvContent.append("正在发生第" + parkNum+1 + "包数据");
                                mainActivity.target_chara.setValue(sendFileBuf);
                                mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
                            } else {
                                sendFileBuf[7] = (byte) lastParkNum;
                                System.arraycopy(buffer, tempDataSize, sendFileBuf, 8, lastParkNum);
                                sendFileBuf[lastParkNum + 8] = (byte) 0x30;
                                sendFileBuf[lastParkNum + 9] = Exclusive_Or(sendFileBuf, lastParkNum + 12 - 3);
                                sendFileBuf[lastParkNum + 10] = (byte) 0xAB;
                                sendFileBuf[lastParkNum + 11] = (byte) 0xAA;
                                //tvContent.append("正在发生第" + parkNum+1 + "包数据");
                                mainActivity.target_chara.setValue(sendFileBuf);
                                mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
                                openToastFlag = true;
                            }
                        }
                        break;
                    case FLASH_ERROR:
                        Toast.makeText(getActivity(), "设备在写FLASH时错误，等设备3秒后自动重启", Toast.LENGTH_SHORT).show();
                        break;
                    case TOTAL_NUMBER_ERROR:
                        Toast.makeText(getActivity(), "接收到总包数与请求升级的总数报不一致,请重新点击发送文件", Toast.LENGTH_SHORT).show();
                        break;
                    case DATA_LEN_ERROR:
                        Toast.makeText(getActivity(), "有效数据长度错误,重新点击发送即可", Toast.LENGTH_SHORT).show();
                        break;
                    case UPDATE_FINISH:
                        parkNum = 0;
                        //tvContent.append("升级程序发送完毕");
                        openToastFlag=true;
                        updateFlag=true;
                        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                        builder.setTitle("通知")
                                .setMessage("升级程序传输完成")
                                .setPositiveButton("确定",null)
                                .create()
                                .show();
                        Vibrator vibrator = (Vibrator) mainActivity.getSystemService(mainActivity.VIBRATOR_SERVICE);
                        //vibrator.vibrate(500);
                        vibrator.vibrate(new long[]{100, 200, 100, 200}, -1);//等待0.2秒，震动0.2秒。。-1表示循环一次，0是从头循序
                        break;
                    default:
                        break;
                }
            }
        }
    }
    private void sendFileFunction() {
        //首次发送的第一包
        int tempSize = 0;
        int Data0 = 0, Data1 = 0;
        int tempOneDataNum = SENDDATANUM;
        buffer = new byte[sendDataSize];

        if (sendDataSize < FILESIZE)
            tempSize = sendDataSize;
        else
            tempSize = FILESIZE;
        parkNum = 0;
        try {
            fis.read(buffer, 0, sendDataSize);
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendFileBuf = new byte[SENDDATANUM];//每一包发送的数据包
        if (tempSize == FILESIZE) {
            Data0 = tempOneDataNum >> 8 & 0xFF;
            Data1 = tempOneDataNum & 0xFF;
        } else {
            Data0 = (tempSize + 12) >> 8 & 0xFF;
            Data1 = (tempSize + 12) & 0xFF;
        }
        //tvContent.append("进入打包阶段");
        sendFileBuf[0] = (byte) 0xFF;
        sendFileBuf[1] = (byte) 0xFE;
        sendFileBuf[2] = (byte) 0x61;
        sendFileBuf[3] = (byte) Data0;
        sendFileBuf[4] = (byte) Data1;
        sendFileBuf[5] = (byte) totalNum;
        sendFileBuf[6] = (byte) parkNum;
        if (tempSize == FILESIZE)
            sendFileBuf[7] = 0x00;
        else
            sendFileBuf[7] = (byte) tempSize;
        System.arraycopy(buffer, 0, sendFileBuf, 8, tempSize);
        if (tempSize == FILESIZE) {
            sendFileBuf[FILESIZE + 8] = (byte) 0x30;
            sendFileBuf[FILESIZE + 9] =  Exclusive_Or(sendFileBuf, tempOneDataNum - 3);
            sendFileBuf[FILESIZE + 10] = (byte) 0xAB;
            sendFileBuf[FILESIZE + 11] = (byte) 0xAA;
            //tvContent.append("正在发生第" + parkNum + "包数据");
            mainActivity.target_chara.setValue(sendFileBuf);
            mainActivity.mService.writeCharacteristic(mainActivity.target_chara);

        } else {
            sendFileBuf[tempSize + 8] = (byte) 0x30;
            sendFileBuf[tempSize + 9] = Exclusive_Or(sendFileBuf, tempSize + 12 - 3);
            sendFileBuf[tempSize + 10] = (byte) 0xAB;
            sendFileBuf[tempSize + 11] = (byte) 0xAA;
            //tvContent.append("正在发生第" + parkNum + "包数据");
            mainActivity.target_chara.setValue(sendFileBuf);
            mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
        }
    }

    //搜索路径下的bin文件，并返回到Vector
    public static ArrayList<String> GetFileName(String fileAbsolutePath) {
        //Vector vecFile = new Vector();
        ArrayList<String> list = new ArrayList<String>();
        File file = new File(fileAbsolutePath);
        File[] subFile = file.listFiles();

        for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
            // 判断是否为文件夹
            if (!subFile[iFileLength].isDirectory()) {
                String filename = subFile[iFileLength].getName();
                // 判断是否为bin结尾
                if (filename.trim().toLowerCase().endsWith(".bin")) {
                    //vecFile.add(filename);
                    list.add(filename);
                }
            }
        }
        return list;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_select_file:
                //System.out.println("--------------------------------------------123");
                list = GetFileName(AbsolutePATH);
                for (int i = 0; i < list.size(); i++)
                    System.out.println(String.valueOf(list.get(i)));
                Intent intent = new Intent(getActivity(), FileScan.class);
                intent.putStringArrayListExtra("FileName", list);
                startActivityForResult(intent, 1);
                break;
            case R.id.filePath:
                if (!tvFilePath.getText().toString().contains(AbsolutePATH)) {
                    Toast.makeText(getActivity(), "请选择升级文件！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    //FileInputStream fis = new FileInputStream("/mnt/sdcard/Download/test.bin");
                    try {
                        fis = new FileInputStream(mFilePath);
                        sendDataSize = fis.available();
                        totalNum = sendDataSize / FILESIZE;
                        if ((sendDataSize - (totalNum * FILESIZE)) > 0)
                            totalNum += 1;
                    } catch (FileNotFoundException e) {
                        Log.d("openFile", "打开文件失败");
                        Toast.makeText(getActivity(), "打开文件失败！", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } catch (IOException e) {
                        Log.d("Error:", "error");
                        e.printStackTrace();
                    }
                }
                tvSendByHand.setText(String.valueOf(parkNum));
                tvSendByHand.append("/" + totalNum);
                int deviceType0=0,deviceType1=0,deviceType2=0,algorithmType=0,version0=0,version1=0,version2=0;
                int RFType=0,netType=0,isMade0=0,isMade1=0,remain=48;
//                int test0=fileNameString.charAt(0);
//                int test1=fileNameString.charAt(1);
//                int test2=fileNameString.charAt(2);
//                System.out.println("fileNameString:"+fileNameString);
//                System.out.println("fileNameString: "+test0+" "+test1+" "+test2);
                deviceType0=fileNameString.charAt(0);
                deviceType1=fileNameString.charAt(1);
                deviceType2=fileNameString.charAt(2);
                algorithmType=fileNameString.charAt(3);
                version0=fileNameString.charAt(4);
                version1=fileNameString.charAt(5);
                version2=fileNameString.charAt(6);
                RFType=fileNameString.charAt(7);
                netType=fileNameString.charAt(8);
                isMade0=fileNameString.charAt(9);
                isMade1=fileNameString.charAt(10);

                byte[] buff = new byte[21];
                buff[0] = (byte) 0xff;
                buff[1] = (byte) 0xfe;
                buff[2] = (byte) 0x63;
                buff[3] = (byte) 0x15;
                buff[4] = (byte) totalNum;
                buff[5] = (byte) deviceType0;
                buff[6] = (byte) deviceType1;
                buff[7] = (byte) deviceType2;
                buff[8] = (byte) algorithmType;
                buff[9] = (byte)  version0;
                buff[10] = (byte) version1;
                buff[11] = (byte) version2;
                buff[12] = (byte) RFType;
                buff[13] = (byte) netType;
                buff[14] = (byte) isMade0;
                buff[15] = (byte) isMade1;
                buff[16] = (byte) remain;
                buff[17] = (byte) remain;
                buff[18] = Exclusive_Or(buff,18);
                buff[19] = (byte) 0xab;
                buff[20] = (byte) 0xaa;
                mainActivity.target_chara.setValue(buff);
                mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
                break;
            case R.id.tvInit:
                if(!openToastFlag){
                    Toast.makeText(getActivity(), "正在写入，请等待完成后再操作！", Toast.LENGTH_SHORT).show();
                    return;
                }
                buff = new byte[8];
                buff[0] = (byte) 0xff;
                buff[1] = (byte) 0xfe;
                buff[2] = (byte) 0xe0;
                buff[3] = (byte) 0x08;
                buff[4] = (byte) 0x00;
                buff[5] = Exclusive_Or(buff,5);
                buff[6] = (byte) 0xab;
                buff[7] = (byte) 0xaa;
                mainActivity.target_chara.setValue(buff);
                mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
                break;

            case R.id.tvSendByHand:
                byte[] sendBuff = new byte[8];
                sendBuff[0] = (byte) 0xff;
                sendBuff[1] = (byte) 0xfe;
                sendBuff[2] = (byte) 0x65;
                sendBuff[3] = (byte) 0x08;
                sendBuff[4] = (byte) 0x00;
                sendBuff[5] = Exclusive_Or(sendBuff, 5);
                sendBuff[6] = (byte) 0xab;
                sendBuff[7] = (byte) 0xaa;
                mainActivity.target_chara.setValue(sendBuff);
                mainActivity.mService.writeCharacteristic(mainActivity.target_chara);
                break;
        }
    }


}
