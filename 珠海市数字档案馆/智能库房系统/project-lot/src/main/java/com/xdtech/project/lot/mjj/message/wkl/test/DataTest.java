package com.xdtech.project.lot.mjj.message.wkl.test;



import com.xdtech.project.lot.mjj.message.data.wkl.WKLMJJTHResultData;
import com.xdtech.project.lot.mjj.message.type.InetAddress32;
import com.xdtech.project.lot.mjj.message.type.Int16;
import com.xdtech.project.lot.mjj.message.type.Int8;
import com.xdtech.project.lot.mjj.message.wkl.WKLMJJMessageContent;
import com.xdtech.project.lot.mjj.message.wkl.WKLMJJMessagePackage;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class DataTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
//		WKLMJJMessagePackage wmmp = new WKLMJJMessagePackage();
//		InetAddress address = null;
//		try {
//			address = InetAddress.getByName("192.168.0.124");
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		wmmp.MJJ_ADDRESS = new InetAddress32(address);
//		
//		WKLMJJMessageContent content = new WKLMJJMessageContent();
//		content.CMD_KEY = new Int8(0x81);
//		
//		WKLMJJOpenResultData data = new WKLMJJOpenResultData();
//		data.RESULT = new Int8(0x1);
//		
//		content.DATA = data;
//		
//		wmmp.CONTENT = content;
//		
//		System.out.println(wmmp);

        WKLMJJMessagePackage wmmp = new WKLMJJMessagePackage();
        InetAddress address = null;
        try {
            address = InetAddress.getByName("192.168.0.124");
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        wmmp.MJJ_ADDRESS = new InetAddress32(address);

        WKLMJJMessageContent content = new WKLMJJMessageContent();

        content.CMD_KEY = new Int8(0x86);

        WKLMJJTHResultData data = new WKLMJJTHResultData();
        data.TEMPERATURE = new Int16(0x12C);
        data.HUMIDITY = new Int16(0x1F4);

        content.DATA = data;

        wmmp.CONTENT = content;

        System.out.println(wmmp);

//		InetAddress address = null;
//		try {
//			address = InetAddress.getByName("127.0.0.1");
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		wmmp.MJJ_ADDRESS = new InetAddress32(address);
//		
//		WKLMJJMessageContent content = new WKLMJJMessageContent();
//		
//		content.CMD_KEY = new Int8(0x06);
//		
//		WKLMJJTHData data = new WKLMJJTHData();
//		
//		content.DATA = data;
//		
//		System.out.println(wmmp);
//		
//		WKLMJJMessagePackage recvice = new WKLMJJMessagePackage();
//		
//		recvice.load(wmmp.toBytes());
    }

}
