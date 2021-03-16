package com.wisdom.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**MD加密算法
 * Created by Administrator on 2017/8/10.wjh
 */
public class MD5 {
    public static void Md5(String plainText){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if(i<0) i+= 256;
                if(i<16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }


            System.out.println("result: " + buf.toString());//32位的加密


            System.out.println("result: " + buf.toString().substring(8,24));//16位的加密


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    // MD5加码。32位
    public static String MD5(String inStr) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];


        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];


        byte[] md5Bytes = md5.digest(byteArray);


        StringBuffer hexValue = new StringBuffer();


        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    // 可逆的加密算法
    public static String KL(String inStr) {
// String s = new String(inStr);
        char[] a = inStr.toCharArray();
        for (int i = 0; i < a.length; i++) {
            a[i] = (char) (a[i] ^ 't');
        }
        String s = new String(a);
        return s;
    }

    // 加密后解密
    public static String JM(String inStr) {
        char[] a = inStr.toCharArray();
        for (int i = 0; i < a.length; i++) {
            a[i] = (char) (a[i] ^ 't');
        }
        String k = new String(a);
        return k;
    }

    public static String AESencode(String str){
        String result = null;
        try{
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed("neounit".getBytes("UTF-8"));
            keyGenerator.init(128, random);
            SecretKey secretKey = keyGenerator.generateKey();
            SecretKeySpec key = new SecretKeySpec(secretKey.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] byteContent = str.getBytes("UTF-8");
            byte[] byteEncode = cipher.doFinal(byteContent);
            result = Base64.encodeBase64String(byteEncode);
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public static String AESdecode(String str){
        String result = null;
        try{
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed("neounit".getBytes("UTF-8"));
            keyGenerator.init(128, random);
            SecretKey secretKey = keyGenerator.generateKey();
            SecretKeySpec key = new SecretKeySpec(secretKey.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] byteContent = Base64.decodeBase64(str);
            byte[] byteEncode = cipher.doFinal(byteContent);
            //返回加密锁内明文数据
            result =  new String(byteEncode, "utf-8");
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) {
        //Md5("123456");
//        System.out.println();
//        String s = new String("555");
//        System.out.println("原始：" + s);
//        System.out.println("MD5后：" + MD5(s));
//        System.out.println("MD5后再加密：" + KL(MD5(s)));
//        System.out.println("解密为MD5后的：" + JM(KL(MD5(s))));
    }
}
