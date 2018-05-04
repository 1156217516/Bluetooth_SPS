package com.example.admin.bluetooth_sps.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zhaofan on 2017/7/5.
 */
public class StreamUtils {
    public static  String readFromString(InputStream in) throws IOException{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int len=0;
        byte[] buffer = new byte[1024];
        while((len=in.read(buffer))!=-1) {
            out.write(buffer,0,len);
        }
        String result=out.toString();
        in.close();
        out.close();
        return result;
    }
}
