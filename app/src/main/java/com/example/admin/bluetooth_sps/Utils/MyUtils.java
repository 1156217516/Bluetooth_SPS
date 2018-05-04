package com.example.admin.bluetooth_sps.Utils;

/**
 * Created by Administrator on 2018/4/28/028.
 */

public class MyUtils {

    //string中提取字符串(string的start数位到end数位)，返回int数值
    public static int getCharByInt(String string, int pos) {
        int start=0,end=0;
        start=pos*2;
        end=start+1;
        char buf[] = new char[2];
        string.getChars(start, end + 1, buf, 0);
        string = new String(buf);
        int b = Integer.parseInt(string, 16);
        return b;
    }

    //校验函数
    public static byte Exclusive_Or(byte buffer[], int len) {
        byte eorbit = 0;
        for (int i = 0; i < len; i++) {
            eorbit ^= buffer[i];
        }
        return eorbit;
    }

}
