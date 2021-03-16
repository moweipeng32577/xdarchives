package com.wisdom.util.netca;

import net.netca.pki.Base64;
import net.netca.pki.*;
import net.netca.pki.impl.netcajni.NetcaDevice;
import net.netca.pki.impl.netcajni.NetcaX509Certificate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 说明：
 * NetcaPKI类是按照《NETCA PKI Crypto Java开发接口开发规范》文档定义的接口提供Java相关的实现代码。
 * 
 * 注意：
 * 1、NetcaJCrypto.jar 要求2.0版本或以上。
 * 
 * 作者：NETCA开发Java团队
 * 日期：2017-07-05
 * 版权： NETCA
 * ===================================================================
 * 2018.07.11 增加方法ChangeJKSPassWd(),用于修改JKS证书口令。修改人：陈健松
 */

//2018.07.10 CJS 加入修改jks密码的接口

public class NetcaPKI {
	
	private static Log log = LogFactory.getLog(NetcaPKI.class);
	
	/**
	 * NETCA PKI 中间件接口
	 * 
	 * 本案例为开源代码，请尊重开发人员权益，不要广为传播；
	 * 本案例是对NETCA底层中间件Crypto的开发封装，简化操作代码；
	 * 更深层次、更多功能请参考Crypto的底层开发接口文档；
	 * 用户可以修改该文件，已达到自己需求，但须保障功能正确性
	 * 
	 */
	

	/**========================================================
	 * 0 全局变量 [根据项目实际应用定制,特别注意1~4项修订]
	 * ========================================================
	 */
	/** 定制1：字符串编码方式，特定项目需定制*/
	// 早期案例编码方式为：NETCAPKI_CP_UTF16LE :UTF-16LE
	// 2015年后一般采用UTF8编码方式：UTF-8 
  	public static final String NETCAPKI_CP = "UTF-8";
  	
  	/**  定制2：证书获取方式，特定项目需定制(java中无需定制此项)*/
 	// Device：从设备获取证书,速度更快【推荐】；
 	// CSP：从获取证书，多CA支持时需采用此方式。
  	// 2018年2月12日 -> linmaogui 网证通全部走Device，其他CA走CSP. 
 	public static String NETCAPKI_CERTFROM = "Device";

  	// 定制3：默认的证书筛选条件，特定项目需定制 */
 	// 多CA支持时需定制，如{ "NETCA", "GDCA","SZCA" }
 	public static final String[] NETCAPKI_SUPPORTCAS = {"NETCA"};

 	/**  定制4：NETCA证书实体唯一标识，特定项目需定制 */
 	// 1.3.6.1.4.1.18760.1.12.11/1.3.6.1.4.1.18760.11.12.11.1：NETCA通用定义OID；
 	// 2.16.156.112548：深圳地方标准 */
 	public static String NETCAPKI_UUID = "1.3.6.1.4.1.18760.1.12.11";

 	/** 机构统一信用代码证*/
 	public static String NETCAPKI_ORGUUID = "";
 	//--------------------------------------------------------------------------
 	
 	/**  定制5：HASH算法，一般无需定制 */
 	public static int NETCAPKI_ALGORITHM_HASH = Hash.SHA256;// 默认哈希算法:SHA256  	网关的为SHA1
 	/**  定制6：RSA签名算法，一般无需定制 */
 	public static int NETCAPKI_ALGORITHM_RSASIGN = Signature.SHA256WITHRSA;// RSA签名算法 SHA256WITHRSA
 	/**  定制7：SM2签名算法，一般无需定制 */
 	public static int NETCAPKI_ALGORITHM_SM2SIGN = Signature.SM3WITHSM2;// SM2签名算法
 	
 	/**  定制8：RSA加密算法 AES256CBC */
 	public static int NETCAPKI_ENVELOPEDDATA_ALGORITHM_RSAENV = EnvelopedData.AES256CBC;
 	
 	/**  定制9：SM2加密算法 SM4CBC */
 	public static int NETCAPKI_ENVELOPEDDATA_ALGORITHM_SM2ENV = EnvelopedData.SM4CBC;
 	
 	/**  定制10：RSA对称加密算法 */
 	public static int NETCAPKI_ALGORITHM_SYMENV = Cipher.AES_CBC;
 	
 	/**  定制11：签名包含证书的选项，一般无需定制 */
 	public static int NETCAPKI_SIGNEDDATA_INCLUDE_CERT_OPTION = SignedData.INCLUDE_CERT_OPTION_SELF;
 	
 	/** 定制12：设置该项BASE64编码不换行 */
 	public static int NETCAPKI_BASE64_ENCODE_NO_NL = Base64.ENCODE_NO_NL;
 	
 	/** 定制24: 存放签章图片的ID */
	private static final int SIGNPICTUREID = 8127;
 	
	private static final String[] SIGNPICTUREIDS = {"8130","8131","8134","8132","8133","8135"};
 	
 	public static final int NETCAPKI_CERT_PURPOSE_ALL = 0;		//不区分证书类型

 	public static final String NETCAPKI_KEYSTORE_TYPE_JKS="JKS";
 	public static final String NETCAPKI_KEYSTORE_TYPE_PFX="PKCS12";
 	
  	//证书基本信息
  	//证书base64编码
  	public static final int NETCAPKI_CERT_INFO_BASE64 = 0;
  	//证书姆印
  	public static final int NETCAPKI_CERT_INFO_THUMBPRINT = 1;
  	//序列号
  	public static final int NETCAPKI_CERT_INFO_SERIALNUMBER = 2;
  	//主体
  	public static final int NETCAPKI_CERT_INFO_SUBJECT = 3;
  	//颁发者
  	public static final int NETCAPKI_CERT_INFO_ISSUER = 4;
  	//有效开始时间
  	public static final int NETCAPKI_CERT_INFO_VALIDFROMDATE = 5;
  	//有效结束时间
  	public static final int NETCAPKI_CERT_INFO_VALIDTODATE = 6;
  	//证书的密钥用法
  	public static final int NETCAPKI_CERT_INFO_KEYUSAGE = 7;
  	//公共密钥算法
  	public static final int NETCAPKI_CERT_INFO_PUBLICKEYALGORITHM = 8;
  	
  	// 获取证书客户服务号\取证书证件号码扩展域信息\证书姆印
  	public static final int NETCAPKI_CERT_INFO_USERCERTNO = 9;
  	//旧的用户证书绑定值
  	public static final int NETCAPKI_CERT_INFO_OLDUSERCERTNO = 10;
  	//证书主题中的名称
  	public static final int NETCAPKI_CERT_INFO_SUBJECT_NAME = 11;
  	//证书主题中的CN项（人名）
  	public static final int NETCAPKI_CERT_INFO_SUBJECT_CN = 12;
  	//Subject中的O项（人名）
  	public static final int NETCAPKI_CERT_INFO_SUBJECT_O = 13;
  	//Subject中的地址（L项）
  	public static final int NETCAPKI_CERT_INFO_SUBJECT_L = 14;
  	//证书颁发者的Email
  	public static final int NETCAPKI_CERT_INFO_SUBJECT_EMAIL = 15;
  	//Subject中的部门名（OU项）
  	public static final int NETCAPKI_CERT_INFO_SUBJECT_OU = 16;
  	//用户国家名（C项）
  	public static final int NETCAPKI_CERT_INFO_SUBJECT_C = 17;
  	//用户省州名（S项）
  	public static final int NETCAPKI_CERT_INFO_SUBJECT_S = 18;
  	//CA ID
  	public static final int NETCAPKI_CERT_INFO_CA_CATITLE = 21;
  	//证书类型
  	public static final int NETCAPKI_CERT_INFO_TYPE = 22;
  	//用户证书客服号 
  	public static final int NETCAPKI_CERT_INFO_CUSTOMER_NUMBER = 23;
  	//深圳地标
  	public static final int NETCAPKI_CERT_INFO_SZ_LANDMARK = 24;
  	//证书旧姆印
  	public static final int NETCAPKI_CERT_INFO_OLDTHUMBPRINT = 31; 
  	//纳税人编码
  	public static final int NETCAPKI_CERT_INFO_RATEPAYER_NUMBER = 32;
  	//组织机构代码号
  	public static final int NETCAPKI_CERT_INFO_ORGANIZATION_CODE = 33;
  	//税务登记号
  	public static final int NETCAPKI_CERT_INFO_RATELOAD_NUMBER = 34;
  	//证书来源地
  	public static final int NETCAPKI_CERT_INFO_SOURCE = 35;
  	//证书证件号码扩展域
  	public static final int NETCAPKI_CERT_INFO_NUMBEREXTEND = 36;
  	//证书证件号码扩展域
  	public static final int NETCAPKI_CERT_INFO_NUMBEREXTEND_DECODE = 37;
  	//统一社会信用代码
  	public static final int NETCAPKI_CERT_INFO_UNIFIED_SOCIAL_CREDIT = 38;
  	//GDCA的特定扩展域 51
  	public static final int NETCAPKI_CERT_INFO_SPECIFICEXTEND = 51;
  	// 证书信息域常量解析
   	public static String[] CERTVALUEPARSE = { "0:证书PEM编码", "1:姆印", "2:序列号",
   			"3:主题", "4:颁发者", "5:有效期开始时间", "6:有效期截止时间",
   			"7:密钥用法",
   			"8:证书公钥算法",
   			"9:用户证书绑定值",
   			"10:旧证书用户证书绑定值", // 1-10

   			"11:主体名", "12:人名CN", "13:单位O", "14:地址L", "15:EmailAddress",
   			"16:部门OU", "17:国家C", "18:省ST",
   			"",
   			"", // 11-20
   			"21:CAID", "22:证书类型", "23:证书客服号", "", "", "", "", "",
   			"",
   			"", // 21-30
   			"31:旧姆印", "32:纳税人编码", "33:企业法人代码", "34:税务登记号", "35:证书来源地",
   			"36:证件号码信息扩展域值", "37:明文证件号码", "", "",
   			"", // 31-40
   			"", "", "", "", "", "", "", "", "",
   			"", // 41-50
   			"51:GDCA TrustID[1.2.86.21.1.3]",
   			"52:GDCA TrustID2[1.2.86.21.1.1]", "", "", "", "", "", "", "", "" // 51

   	};

	/**==================================================================
	 * 0.工具类
	 * ==================================================================
	 */ 
  	/**
	 * 字符串转byte数组，采用的编码方式在项目定制常量“NETCAPKI_CP”中定义。
	 * 
	 * @param data 需要转byte数据的字符串
	 * @return byte数组
	 * @throws PkiException
	 * 
	 */
	public static byte[] convertByte(String data) throws PkiException {
		if (isEmpty(data)) {
			throw new PkiException("字符串转字节数组，字符串为空");
		}
		try {
			return data.getBytes(NETCAPKI_CP);
		} catch (UnsupportedEncodingException ex) {
			throw new PkiException("字符串转字节数组编码错误:" + ex.getMessage());
		}
	}

	/**
	 * byte数组转字符串，采用的编码方式为项目定制常量“NETCAPKI_CP”中定义。
	 * 
	 * @param data  需要转字符串的byte数组
	 * @return  转码后的字符串
	 * @throws PkiException
	 */
	public static String convertString(byte[] data) throws PkiException {
		if (isEmpty(data)) {
			throw new PkiException("byte数组转字符串中，字节数组为空");
		}
		try {
			return new String(data, NETCAPKI_CP);
		} catch (UnsupportedEncodingException ex) {
			throw new PkiException("字符串转字符编码错误:" + ex.getMessage());
		}
	}

	/**
	 * 字节数组转Hex编码字符串。大写字母
	 * 
	 * @return
	 * @throws PkiException
	 */
	public static String convertHex(byte[] data) throws PkiException {
		if (isEmpty(data)) {
			throw new PkiException("字节数组转Hex编码字符串中，字节数组为空");
		}
		try {
			return Util.HexEncode(true, data);
		} catch (Exception e) {
			throw new PkiException("字节数组转Hex编码字符串中，出错：" + e.getMessage());
		}
	}
	
	/**
	 * 使用公司工具类，将字节数组进行Base64编码，得到Base64字符串
	 * 
	 * @param data 字节数组
	 * @return Base64编码的字符串
	 * @throws Exception
	 */
	public static String base64Encode(byte[] data) throws PkiException {
		if (isEmpty(data)) {
			throw new PkiException("在进行Base64编码的过程，字节数组为空");
		}
		try {
			return Base64.encode(NETCAPKI_BASE64_ENCODE_NO_NL, data);
		} catch (PkiException e) {
			throw new PkiException("在进行Base64编码的过程中，出错：" + e.getMessage());
		}
	}

	/**
	 * 使用公司工具类，将编码的字符串进行Base64解码，获得字节数组
	 * 
	 * @param data 需要编码的数据
	 * @return Base64解码后的字节数组
	 * @throws Exception
	 */
	public static byte[] base64Decode(String data) throws PkiException {
		if (isEmpty(data)) {
			throw new PkiException("在进行Base64解码的过程中，输入的值为空");
		}
		try {
			return Base64.decode(NETCAPKI_BASE64_ENCODE_NO_NL, data);
		} catch (PkiException e) {
			throw new PkiException("在进行Base64解码的过程中，出错：" + e.getMessage());
		}
	}
	
	/**
	 * 获取随机数
	 * @param length	随机数长度
	 * @return			随机数
	 * @throws PkiException 
	 */
	public static String getRandom(int length) throws PkiException{
		net.netca.pki.global.Pki pki = null;
		try{
			pki = net.netca.pki.global.Pki.getInstance("netca");
			byte[] random = pki.generateRandom(length);
			return convertHex(random);
		}catch(Exception e){
			throw new PkiException("产生随机数失败："+ e.getMessage());
		}finally{
			if (pki != null && pki instanceof Freeable) {
	    		((Freeable)pki).free();
	    	}
		}
	}
	
	/**
	 * 获取信息摘要码（Hex编码方式）。
	 * @param data		信息数据(字符串)
	 * @return			信息摘要码（Hex编码方式）
	 * 注：算法参见项目定制常量“NETCAPKI_ALGORITHM_HASH”中定义
	 * @throws PkiException 
	 */
	public static String hashDataHex(String data) throws PkiException{
		return hashDataHex(convertByte(data));
	}

	/**
	 * 获取信息摘要码（Hex编码方式）。
	 * @param data		信息数据(字节数组)
	 * @return			信息摘要码（Hex编码方式）
	 * 注：算法参见项目定制常量“NETCAPKI_ALGORITHM_HASH”中定义
	 * @throws PkiException 
	 */
	public static String hashDataHex(byte[] data) throws PkiException{
		return convertHex(getHashData(data, NETCAPKI_ALGORITHM_HASH));
	}
	
	
	/**
	 * 获取信息摘要码（BASE64编码方式）。
	 * @param data		信息数据
	 * @return			信息摘要码（Base64编码方式）
	 * 注：Sha1计算的摘要值(BASE64编码)
	 * @throws PkiException 
	 */
	public static String hashDataBase64(String data) throws PkiException{
		return hashDataBase64(convertByte(data));
	}
	
	/**
	 * 获取信息摘要码（BASE64编码方式）。
	 * @param data		信息数据
	 * @return			信息摘要码（Base64编码方式）
	 * 注：Sha1计算的摘要值(BASE64编码)
	 * @throws PkiException 
	 */
	public static String hashDataBase64(byte[] data) throws PkiException{
		return base64Encode(getHashData(data, NETCAPKI_ALGORITHM_HASH));
	}
	
	/**
	 * 将hex编码值转换为Base64编码字符串
	 * 
	 * @param hex hex编码字符串
	 * @return Base64编码字符串
	 * @throws Exception
	 */
	public static String hexToBase64(String hex) throws PkiException {
		
		if (isEmpty(hex)) {
			throw new PkiException("在进行hex转Base64编码过程中，输入的值为空"); 
		}
		try {
			return base64Encode(Util.HexDecode(hex));
		} catch (PkiException e) {
			throw new PkiException("在进行hex转Base64编码过程中，出错：" + e.getMessage());
		}
	}
	
	/**
	 * 进行hash计算，获得摘要码（MD5算法）
	 * @param data	数据
	 * @return		Base64编码的摘要码
	 * @throws PkiException
	 */
	public static String hashDataMD5(String data) throws PkiException {
		return hashDataMD5(convertByte(data));
	}
	
	/**
	 * 进行hash计算，获得摘要码（早期编码方式:MD5哈希算法;Hex制编码）
	 * @param data	数据
	 * @return		Hex编码的摘要码
	 * @throws PkiException
	 */
	public static String hashDataMD5(byte[] data) throws PkiException {
		if (isEmpty(data)) {
			throw new PkiException("在进行hash计算，获得摘要码（MD5算法）过程中，输入的值为空"); 
		}
		Hash hash = null;
		try {
			hash = new Hash(Hash.MD5);
			hash.update(data);
			return convertHex(hash.doFinal());
		} catch (PkiException e) {
			throw new PkiException("在进行hex转Base64编码过程中，出错：" + e.getMessage());
		} finally {
			if(hash!=null){
				hash.free();
			}
			hash = null;
		}
	}

	/**
	 * 二进制 读取文件

	 * @param path 文件路径
	 * @return 读取文件的内容，返回一个字节数组
	 * @throws IOException
	 */
	public static byte[] readFile(String path) throws IOException {
		if(isEmpty(path)){
			throw new IOException("读文件的操作中，文件路径为空");
		}
		File f = new File(path);
		if (!f.exists()) {
			throw new IOException("读取的文件路径不存在");
		}
		
		InputStream inputStream = null;
		byte[] content = null;

		try {
			inputStream = new FileInputStream(path);
			int len = inputStream.available();
			content = new byte[len];
			len = inputStream.read(content);
			return content;
		} catch (FileNotFoundException e) {
			throw new IOException("读文件的操作中，该路径下没有对应的文件进行读取");
		} catch (IOException e) {
			throw new IOException("读文件的操作中，读取文件出现错误："+e.getMessage());
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Exception e3) {
					throw new IOException("读文件的操作中，关闭输入流失败");
				}
			}
		}
	}

	/**
	 * 二进制写文件的操作
	 * 
	 * @param path 文件路径
	 * @param content 字节数组，需要写入文件的内容
	 * @return 读取文件的内容，返回一个字节数组
	 * @throws IOException
	 */
	public static boolean writeFile(String path, byte[] content) throws IOException {
		if(isEmpty(content)){
			throw new IOException("写入的内容为空");
		}
		if(isEmpty(path)){
			throw new IOException("写入的文件路径为空");
		}
		File f = new File(path);
		if (!f.exists()) {
			throw new IOException("写入的文件路径不存在");
		}
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(path);
			outputStream.write(content);
			return true;
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("写文件的操作中，该路径下没有对应的文件");
		} catch (IOException e) {
			throw new IOException("写文件的操作中，写入文件出现错误："+e.getMessage());
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (Exception e) {
					throw new IOException("写文件的操作中，关闭输出流失败");
				}
			}
		}
	}
		
	//2017年7月8日15:51:11注：在java中没有这个方法的相关实现，只有Device上选择需不需要缓存的证书之类的
	/**
	 * 清除PIN码缓存
	 * 调用此函数后，每次调用私钥都需要输入PIN码；
	 * 不调用此函数，只在第一次使用私钥时，输入PIN码，后续调用时不需要重复输入PIN码。
	 */
	public static void clearPinCache() throws Exception {
		throw new Exception("暂不支持该方法，请联系项目的技术人员！");
	}

	
	/**=================================================================================
	 *  1. 证书处理
	 * =================================================================================
	 */
	/**
	 * 获取证书集（通过证书库、或者直接通过弹出框）
	 * 使用频率：较少用到；
	 * 使用场景：
        1）证书登陆时，网页列出插入证书下拉框，需选择对应证书进行登陆；
        2）证书绑定时，绑定相应证书；
         
	 * @param purpose 证书类型：签名、加密、两者兼备
	 * @return
	 * @throws PkiException
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	public static List<Certificate> getX509Certificates(int purpose)
			throws PkiException {

		List<Certificate> list = new ArrayList<Certificate>();
		int count = 0;

		// 通过设备来获取certificate
		if (NETCAPKI_CERTFROM.equals("Device")) {
			log.debug("enter get certlist from Device...");
			int type = Device.ANY_DEVICE;
			int flag = Device.DEVICE_FLAG_CACHE_PIN_IN_PROCESS;
			
			DeviceSet deviceSet = null;
			try {
				deviceSet = new DeviceSet(type, flag);
				if (deviceSet == null) {
					throw new PkiException("获取证书集失败，系统中设备集为空");
				}
				count = deviceSet.count();
				Device device = null;
				KeyPair keyPair = null;
				try{
					for (int i = 0; i < count; i++) { //1.遍历DeviceSet里面所有的Device
						device = deviceSet.get(i);
						if (device == null) {
							continue;
						}
						
						int keyPairCount = device.getKeyPairCount();
						for (int j = 1; j <= keyPairCount; ++j) { //2.遍历Device里面所有的KeyPair
							keyPair = device.getKeyPair(j);
							if (keyPair == null) {
								continue;
							}
							try {
								int certCount = keyPair.getCertificateCount(); //通常来说，一个keypair一个证书！
								for (int k = 0; k < certCount; k++) { //3.遍历KeyPair中的每个Certificate
									Certificate certificate = keyPair.getCertificate(k);
									// 过滤证书：密钥用法(getKeyUsage)
									int kUsage = certificate.getKeyUsage(); //输出就是证书里面密钥用的或起来得到的结果，而不是特定的常量，因为一个证书有多个密钥用法
									log.debug("kUsage: " + kUsage);
									
									if (purpose == Certificate.PURPOSE_SIGN) {
										if ( !(kUsage == (net.netca.pki.global.X509Certificate.KEYUSAGE_DIGITALSIGNATURE | net.netca.pki.global.X509Certificate.KEYUSAGE_NONREPUDIATION)  
												|| (kUsage == -1) 
												|| (kUsage == net.netca.pki.global.X509Certificate.KEYUSAGE_DIGITALSIGNATURE)) ) {
											continue;
										}
									}
									
									if (purpose == Certificate.PURPOSE_ENCRYPT) {
										if ( !(kUsage == (net.netca.pki.global.X509Certificate.KEYUSAGE_KEYENCIPHERMENT | net.netca.pki.global.X509Certificate.KEYUSAGE_DATAENCIPHERMENT) 
												|| (kUsage == -1)
												|| (kUsage == net.netca.pki.global.X509Certificate.KEYUSAGE_KEYENCIPHERMENT)) ) {
											continue;
										}
									}
									log.debug("add a cert...");
									list.add(certificate);
								} ///:~
							} finally {
								if(keyPair != null){
									keyPair.free();
								}
								keyPair = null;
							}
						} ///:~
						
					} ///:~
				}finally{
					if(device != null){
						device.free();
						device = null;
					}
				}
			} catch (PkiException e) {
				throw new PkiException("获取证书集过程中，创建设备集失败，请检查筛选的类型是否出错:" + e.getMessage());
			} finally {
				if(deviceSet != null){
					deviceSet.free();
				}
				deviceSet = null;
			}

			// 通过证书库来获取。
		} else {
			log.debug("enter get certlist from certStore...");
			
			CertStore certStore = null;
			try {
				certStore = new CertStore(CertStore.CURRENT_USER, CertStore.MY);
				if(certStore == null){
					throw new PkiException("打开证书库失败");
				}
				
				count = certStore.getCertificateCount();
				if (count <= 0) { //如果没有证书，直接返回null
					return null;
				}
				for (int i = 1; i < count + 1; i++) { //注意：从1开始！
					Certificate certificate = certStore.getCertificate(i);
					if(certificate == null){
						continue;
					}
					int kUsage = certificate.getKeyUsage();
					log.debug("kUsage: " + kUsage);
					// 过滤证书：密钥用法(getKeyUsage)
					if (purpose == Certificate.PURPOSE_SIGN) {
						if (!((kUsage == (net.netca.pki.global.X509Certificate.KEYUSAGE_DIGITALSIGNATURE | net.netca.pki.global.X509Certificate.KEYUSAGE_NONREPUDIATION))
								|| (kUsage == -1)
								|| (kUsage == net.netca.pki.global.X509Certificate.KEYUSAGE_DIGITALSIGNATURE))) {
							continue;
						}
					}

					if (purpose == Certificate.PURPOSE_ENCRYPT) {
						if (!((kUsage == (net.netca.pki.global.X509Certificate.KEYUSAGE_KEYENCIPHERMENT | net.netca.pki.global.X509Certificate.KEYUSAGE_DATAENCIPHERMENT))
								|| (kUsage == -1)
								|| (kUsage == net.netca.pki.global.X509Certificate.KEYUSAGE_KEYENCIPHERMENT))) {
							continue;
						}
					}
					log.debug("add a cert...");
					list.add(certificate);
				}
			} catch (PkiException e) {
				throw new PkiException("获取证书集过程中，创建证书库失败:" + e.getMessage());
			} finally {
				if(certStore != null){
					certStore.close();
					certStore.free();
				}
				certStore = null;
			}
		}
		
		return list;
	}
	/**
	 * 获得X509证书，返回一个证书对象
	 * 使用频率：较常用；
	 * 使用场景：
        	选择证书通常采用此函数。1）证书绑定时，2）证书登录时；
         	根据全局变量定制项2、3，可通过该函数支持多CA支持；
         * 
	 * @param purpose	证书作用的类型（加密、签名），参见Cetificate类有关PURPOSE字样的常量定义
	 * @return			一个筛选出来的证书
	 */
	public static Certificate getX509Certificate(int purpose) {
		String type = getType(NETCAPKI_CERTFROM, purpose);
		String expr = getFilter(purpose);
		log.debug("type: " + type);
		log.debug("expr: " + expr);		
		return Certificate.select(type, expr);
	}
	/**
	 * 获得X509证书，返回一个证书对象（从证书BASE64编码信息中）
	 * 使用频率：较常用
	 * 
	 * @param base64Cert		证书的Base64编码
	 * @return					一个证书对象
	 * @throws PkiException
	 * 比如：采用指定人员的证书进行加密；
	 */
	public static Certificate getX509Certificate(String base64Cert) throws PkiException {
		if(isEmpty(base64Cert)){
			throw new PkiException("获得X509证书过程中，证书Base64编码的输入值为空");
		}
		Certificate certificate = null;
		try {
			certificate = new Certificate(base64Cert);
		} catch (Exception e) {
			throw new PkiException("通过Base64编码获得X509证书过程中出错："+e.getMessage());
		}
		return certificate;
	}
	
	/**
	 * 通过字节数组获取证书对象
	 * @param data	页面传递过来的证书（字节数组形式）
	 * @return						证书对象
	 * @throws PkiException 
	 * 
	 */
	public static Certificate getX509Certificate(byte[] data) throws PkiException{
		if(isEmpty(data)){
			throw new PkiException("通过字节数组获取证书对象过程中，证书编码数据为空");
		}
		Certificate certificate = null;
		try {
			certificate = new Certificate(data);
		} catch (PkiException e) {
			throw new PkiException("通过字节数组获取证书对象过程中，出错： " + e.getMessage());
		}
		return certificate;
	}
	
	/**
	 * 获取证书对象（根据证书特定域的值）
	 * 使用频率：较少用，有特定需求时，才用到；
	 * 应用场景:
       		1）证书登陆后，会将部分证书属性信息放到session中；
       		2）签名时，会用登陆证书进行签名，此时，需根据session中的属性值获取证书
       		
	 * @param purpose		证书用途，参见Certificate中关于PURPOSE常量的定义；
	 * @param infoType		证书信息特定域类别，证书属性值编号
	 * @param certInfoValue	证书信息特定域值
	 * @return				证书对象
	 * @throws PkiException 
	 */
	public static Certificate getX509Certificate(int purpose, int infoType, String certInfoValue) throws PkiException {

		if(isEmpty(certInfoValue)){
			throw new PkiException("获取证书对象（根据证书特定域的值），证书信息特定域值为空");
		}
		List<Certificate> certificates = getX509Certificates(purpose);
		if(certificates==null){
			return null;
		}
		if(certificates.size()>0){
			Certificate certificate = null;
			try {
				for(int i = 0;i<certificates.size();i++){
					certificate = certificates.get(i);
					if(getX509CertificateInfo(certificate,infoType).equals(certInfoValue)){
						return certificate;
					}else{
						if(certificate!=null){
							certificate.free();
						}
						certificate = null;
					}
					
				}
			} catch (PkiException e) {
				throw new PkiException("获取证书对象（根据证书特定域的值）失败："+e.getMessage());
			}
		}
		return null;
	}
	
	/**
	 * 获得X509证书，返回一个证书对象
	 * @param type	证书类型，也可以是获取条件的JSON。可以参考《NETCA PKI API参考手册》	
	 * @param expr	表达式条件
	 * @return
	 */
	public static Certificate getX509Certificate(String type, String expr) {

		return Certificate.select(type, expr);
	}

	/**
	 * 获取一个服务器证书（从证书库，根据证书主题CN项）
	 * 使用频率：较少用
	 * 使用场景：
        	服务器端使用，一般用来做服务器端证书加密解密用;
       		服务器证书，安装到服务器上，在MMC本地计算机上能看到；
        	获取服务器证书，发送到前台，供客户用来加密；
        	
	 * @param CN		证书主题CN项
	 * @return
	 */
	public static Certificate getServerCertificate(String CN) {

		String type = getType(NETCAPKI_CERTFROM, NETCAPKI_CERT_PURPOSE_ALL);
		String expr = getFilter(NETCAPKI_CERT_PURPOSE_ALL);
		expr += "&&CN='" + CN + "'";
		return Certificate.select(type, expr);
	}

	
	/**
	 * [常用]获取证书属性信息
	 * 使用频率：较常用，多CA支持时，需定制；
        	注：第9项值一般作为证书的绑定值，该项值为一个复合值；
        	
	 * @param certificate	一个证书对象
	 * @param iInfoType		信息类型（见5.1.2 证书中相关信息的类型编码）
	 * @return				相对应的相关信息
	 * @throws PkiException
	 */
	public static String getX509CertificateInfo(Certificate certificate, int iInfoType)
			throws PkiException {

		if (certificate == null) {
			return "";
		}
		try {
			String rt = "";
			switch (iInfoType) {
			
			
			case 0:  	// 获取证书BASE64格式编码字符串     NETCAPKI_CERT_INFO_BASE64 	
				return certificate.pemEncode();
			// 注意：其中的Constants.NETCAPKI_ALGORITHM_HASH是固定编码方式，注意旧版本调用过程hash算法中（可能为sha1）会不会与之不同（SHA256）
			
			//证书基本信息1-8 
			case 1:		//证书姆印   					NETCAPKI_CERT_INFO_THUMBPRINT	
				return Util.HexEncode(true, certificate.computeThumbprint(Hash.SHA1));		//SHA1
			case 2:		//证书序列号 					NETCAPKI_CERT_INFO_SERIALNUMBER 
				return certificate.getSerialNumber();
			case 3:		//证书主题					NETCAPKI_CERT_INFO_SUBJECT	
				return certificate.getSubject();
			case 4:		//证书颁发者主题			
				return certificate.getIssuer();			
			case 5:		//证书有效期起					NETCAPKI_CERT_INFO_VALIDFROMDATE 证书有效期起
				return certificate.getValidityStart().toString();
			case 6:		//证书有效期止					NETCAPKI_CERT_INFO_VALIDTODATE
				return certificate.getValidityEnd().toString();
			case 7:		//密钥用法					NETCAPKI_CERT_INFO_KEYUSAGE
				return Integer.valueOf(certificate.getKeyUsage()).toString();
			case 8: 	//证书的公钥的算法				NETCAPKI_CERT_INFO_PUBLICKEYALGORITHM
			{
				return Integer.valueOf(certificate.getPublicKeyAlgorithm()).toString();
			}
			case 9: 	//NETCAPKI_CERT_INFO_USERCERTNO
			{
				// 证书的唯一标识
				rt = getX509CertificateInfo(certificate, NETCAPKI_CERT_INFO_NUMBEREXTEND);
				if (rt != null && !rt.isEmpty()){
					return rt;
				}
				// 证书客户服务号
				rt = getX509CertificateInfo(certificate, NETCAPKI_CERT_INFO_CUSTOMER_NUMBER);
				if (rt != null && !rt.isEmpty()){
					return rt;
				}
				// 证书姆印
				rt = getX509CertificateInfo(certificate, NETCAPKI_CERT_INFO_THUMBPRINT);
				if (rt != null && !rt.isEmpty()){
					return rt;
				}
				return "";
			}
			case 10: 	//旧的用户证书绑定值；(证书更新后的原有9的取值)	NETCAPKI_CERT_INFO_OLDUSERCERTNO
			{
				if (getX509CertificateInfo(certificate, NETCAPKI_CERT_INFO_CA_CATITLE).equals("NetCA")){
					return getX509CertificateInfo(certificate, NETCAPKI_CERT_INFO_OLDTHUMBPRINT);
				}
				return "";
			}
			//Subject中信息（11~18）
			case 11: 	//证书主题名称；有CN项取CN项值；无CN项，取O的值	NETCAPKI_CERT_INFO_SUBJECT_NAME
			{
				rt = getX509CertificateInfo(certificate, NETCAPKI_CERT_INFO_SUBJECT_CN);
				if (rt != null && !rt.isEmpty()){
					return rt;
				}else{
					return getX509CertificateInfo(certificate, NETCAPKI_CERT_INFO_SUBJECT_O);
				}
			}
			case 12:{	//Subject中的CN项（人名）					NETCAPKI_CERT_INFO_SUBJECT_CN
				rt = certificate.getSubjectCN();
				return ((rt != null && !rt.isEmpty()) ? rt : "");
			}
			case 13:{	//Subject中的O项（人名）					NETCAPKI_CERT_INFO_SUBJECT_O
				rt = certificate.getSubjectO();
				return ((rt != null && !rt.isEmpty()) ? rt : "");
			}
			case 14:{	//Subject中的地址							NETCAPKI_CERT_INFO_SUBJECT_L
				rt = certificate.getSubjectL();
				return ((rt != null && !rt.isEmpty()) ? rt : "");
			}
			case 15:{	//证书颁发者的Email						NETCAPKI_CERT_INFO_SUBJECT_EMAIL
				rt = certificate.getSubjectEmail();
				return ((rt != null && !rt.isEmpty()) ? rt : "");
			}
			case 16:{	//Subject中的部门名（OU项）					NETCAPKI_CERT_INFO_SUBJECT_OU
				rt = certificate.getSubjectOU();
				return ((rt != null && !rt.isEmpty()) ? rt : "");
			}
			case 17:{	///用户国家名（C项）						NETCAPKI_CERT_INFO_SUBJECT_C
				rt = certificate.getSubjectC();
				return ((rt != null && !rt.isEmpty()) ? rt : "");
			}
			case 18:{	//用户省州名（S项）							NETCAPKI_CERT_INFO_SUBJECT_S
				rt = certificate.getSubjectST();
				return ((rt != null && !rt.isEmpty()) ? rt : "");
			}
			case 21:{ 	//CA ID									NETCAPKI_CERT_INFO_CA_CATITLE
				for (int i = 0; i < NETCAPKI_SUPPORTCAS.length; i++){
					if (getX509CertificateInfo(certificate, NETCAPKI_CERT_INFO_ISSUER)
							.indexOf(NETCAPKI_SUPPORTCAS[i]) > 0){
						return NETCAPKI_SUPPORTCAS[i];
					}
				}
				return "";
			}
			//2017年7月8日15:13:13，注：关于扩展OID的还没有实现。
			case 22:{
				if(getX509CertificateInfo(certificate, 21).equals("NETCA")){
					
					try {
						//netca证书类型扩展OID:NETCA OID(1.3.6.1.4.1.18760.1.12.12.2)
	                    //1：服务器证书;2：个人证书;3: 机构证书;4：机构员工证书;5：机构业务证书(注：该类型国密标准待定);0：其他证书
						String flag = null;//certificate.getStringExtension("1.3.6.1.4.1.18760.1.12.12.2");
						byte[] extV = certificate.getExtensionValue("1.3.6.1.4.1.18760.1.12.12.2");
						flag = net.netca.pki.Util.decodeDERString(net.netca.pki.Util.UTF8STRING_TYPE, extV);
						if (flag.equals("001")){
	                         return "3";
	                     }
	                     else if (flag.equals("002")){
	                         return "5";
	                     }
	                     else if (flag.equals("003")){
	                         return "4";
	                     }
	                     else if (flag.equals("004")){
	                         return "2";
	                     }else{
	                    	 return "0";
	                     }
					} catch (Exception e) {
					  	String sCN = getX509CertificateInfo(certificate, 12);
                        String sO = getX509CertificateInfo(certificate, 13);
                        boolean hasSCN = (sCN!=null&&(!sCN.equals("")));
                        boolean hasSO = (sO!=null&&(!sO.equals("")));
                        if ((!hasSO)&&hasSCN){
                            return "2";
                        }
                        else if ((hasSO && (!hasSCN)) ||
                            (hasSO && hasSCN && sO.equals(sCN))){
                            return "3";
                        }
                        else if (
                        		hasSO && hasSCN && (!sO.equals(sCN))){
                            return "4";
                        }
					}
					return "0";
				}else{
					return "0";
				}
					
			}
			case 23:{		//用户证书客服号 			//底层会报错
				if(getX509CertificateInfo(certificate, 21).equals("NETCA")){
					try {
						byte[] extV = certificate.getExtensionValue("1.3.6.1.4.1.18760.1.14");
						return net.netca.pki.Util.decodeDERString(net.netca.pki.Util.UTF8STRING_TYPE, extV);
					} catch (Exception e) {
						return "";
					}
				}else if(getX509CertificateInfo(certificate, 21).equals("GDCA")){
					return getX509CertificateInfo(certificate, 51);
				}else{
					return "";
				}
			}
			case 24:{		//深圳地标
				try {
//					return certificate.getStringExtension("2.16.156.112548");
					byte[] extV = certificate.getExtensionValue("2.16.156.112548");
					return net.netca.pki.Util.decodeDERString(net.netca.pki.Util.UTF8STRING_TYPE, extV);
				} catch (Exception e) {
					return "";
				}
			}
			case 31:{		//证书旧姆印
				try {
					return convertHex(convertByte(certificate.getAttribute(certificate.ATTRIBUTE_PREVCERT_THUMBPRINT)));
//					return convertHex(certificate.getinf);  >>>>>>>>
				} catch (Exception e) {
					return "";
				}
			}
			case 32:{		//纳税人编码
				try {
					return "";
				} catch (Exception e) {
					return "";
				}
			}
			case 33:{		//组织机构代码号
				try {
					return "";
				} catch (Exception e) {
					return "";
				}
			}
			case 34:{		//税务登记号
				try {
					return "";
				} catch (Exception e) {
					return "";
				}
			}
			case 35:{		//证书来源地
				try {
					return "";
				} catch (Exception e) {
					return "";
				}
			}
			case 36:{		//证书证件号码扩展域
				//例子：10001@0006ZZ1XXXXXw==
				try {
//					return certificate.getStringExtension(NETCAPKI_UUID);
					byte[] extV = certificate.getExtensionValue(NETCAPKI_UUID);
					return net.netca.pki.Util.decodeDERString(net.netca.pki.Util.UTF8STRING_TYPE, extV);
				} catch (Exception e) {
					return "";
				}
			}
			case 37:{		//证书证件号码扩展域
				String certExtension = getX509CertificateInfo(certificate, 36);
				if(certExtension!=null && certExtension.length()>13){
					int beginIndex = certExtension.indexOf("@");
					if(beginIndex==-1){
						return "";		//解析内容不是合法的扩展域
					}
					String flag = certExtension.substring(beginIndex+7, beginIndex+8);
					if(flag.equals("1")){
						String b64Identity = certExtension.substring(beginIndex+8);
						byte[] data = base64Decode(b64Identity);
						return convertString(data);
					}else if(flag.equals("0")){
						return certExtension.substring(beginIndex+8);
					}
				}
				return "";
			}
			case 38:{		//统一信用代码, 2018年3月29日11:37:44 新的OID,定制
				try {
					String extStr = certificate.getAttribute(106);		//5.0版本的jar中，Certificate没有对应的属性，但是方法已经实现；5.2版本以及以上，106可以通过certificate.ATTRIBUTE_ENTERPRISEID_ITEMS来代替
					if(extStr.isEmpty()){
						return "";
					}
					String[] extValues = extStr.split("#");
					return extValues[extValues.length-1];
					
				} catch (Exception e) {
					if(getX509CertificateInfo(certificate, 22).equals("3")){		//判断是否为机构证书
						return getX509CertificateInfo(certificate, 37);
					}else{
						return "";
					}
				}
			}
			case 51:{		//GDCA 证书信任号
				String OID = "1.2.86.21.1.3";
				if(getX509CertificateInfo(certificate, 21).equals("GDCA")){
					try {
						return OID;
					} catch (Exception e) {
						return "";
					}
				}
				return "";
			}
			default:return "";
			}
		} catch (PkiException e) {
			throw new PkiException("获取证书的相关信息过程中出错："+e.getMessage());
		}
	}

	/**
	 * 获取证书特定扩展域信息
	 * @param certificate	证书对象
	 * @param OID			OID
	 * @return				证书拓展域信息
	 * @throws PkiException 
	 */
	public static String getX509CertificateInfo(Certificate certificate, String OID) throws PkiException{
		try {
			if (certificate == null)
				throw new PkiException("找不到数字证书，请运行证书安全客户端软件，并插入一个证书介质Key!");
			byte[] extV = certificate.getExtensionValue(OID);
			return net.netca.pki.Util.decodeDERString(net.netca.pki.Util.UTF8STRING_TYPE, extV);
		} catch (PkiException e) {
			throw new PkiException("获取证书特定扩展域信息异常发生："+e.getMessage());
		}
	}
	
	/**
	 * 获得证书的拇指指纹，hash值
	 * 
	 * @param certificate 	证书
	 * @return 				证书的姆印值
	 * @throws PkiException
	 */
	public static String getX509CertificateThumbprint(Certificate certificate)
			throws PkiException {

		try {
			if (certificate == null)
				throw new PkiException("找不到数字证书，请运行证书安全客户端软件，并插入一个证书介质Key!");
			return getX509CertificateInfo(certificate, NETCAPKI_CERT_INFO_THUMBPRINT);
		} catch (PkiException e) {
			throw new PkiException("获得证书的拇指指纹出错："+e.getMessage());
		}
	}


	/**=======================================================================================
	 * 3. 签名操作
	 * =======================================================================================
	 */
	/**
	 * 签名 
	 * [常用]PKCS7签名
	 * 使用场景：较常用，兼容以前代码改造

	 * @param source 原文
	 * @param hasDetached 是否带原文（false：带，true：不带）
	 * @return 签名值
	 * @throws PkiException 
	 */
	public static String signNETCA(String source, boolean hasDetached) throws PkiException {
		return signedDataByPwd(source, hasDetached, null);
	}

	/**
	 * 签名 
	 * 带PIN码PKCS7签名
	 * 使用频率：少用，一般用signNETCA;
	 * 
	 * @param source 原文
	 * @param hasDetached 是否带原文（false：带，true：不带）
	 * @param password 密码
	 * @return 签名值
	 * @throws PkiException 
	 */
	public static String signedDataByPwd(String source, boolean hasDetached, String password)
			throws PkiException{
		try {
			byte[] data = convertByte(source);
			return signedDataByPwd(data, hasDetached, password);
		} catch (PkiException e) {
			throw new PkiException(e.getMessage());
		}
	}

	/**
	 * PKCS#7签名（带PIN码）
	 * 带PIN码PKCS7签名（字节数组）
	 * 使用频率：少用，一般用signNETCA;
	 * 
	 * @param source	原文（字节数组）
	 * @param hasDetached	是否带原文
	 * @param password usbKey的密码
	 * @return			PKCS#7签名值
	 * @throws PkiException 
	 */
	public static String signedDataByPwd(byte[] source, boolean hasDetached, String password) throws PkiException{
		
		Certificate certificate = null;
		try {
			  certificate = getX509Certificate(Certificate.PURPOSE_SIGN);
			  return signedDataByCertificate(certificate, source, hasDetached, password);
		} catch (PkiException e) {
			throw new PkiException("电子签名及验签异常发生："+e.getMessage());
		} finally {
			if(certificate!=null){
				certificate.free();
			}
			certificate = null;
		}	}
	
	/**
	 * PKCS7签名 
	 * 使用证书进行PKCS7签名
	 * 
	 * @param certificate 证书
	 * @param source 原文
	 * @param hasDetached 是否带原文（false：带，true：不带）
	 * @return 签名值
	 * @throws PkiException 
	 */
	public static String signedDataByCertificate(Certificate certificate, String source, boolean hasDetached) throws PkiException{
		return signedDataByCertificate(certificate, source, hasDetached, null);
	}

	/**
	 * PKCS7签名
	 * 
	 * @param certificate 证书
	 * @param source 原文
	 * @param hasDetached 是否带原文（false：带，true：不带）
	 * @param password 密码
	 * @return 签名值
	 * @throws PkiException
	 */
	public static String signedDataByCertificate(Certificate certificate, String source, boolean hasDetached, String password)
			throws PkiException {
		return signedDataByCertificate(certificate, convertByte(source), hasDetached, password);
	}
	
	
	/**
	 * 签名核心方法
	 * 使用证书进行PKCS7签名[具体代码实现]
	 * 
	 * @param certificate 证书对象
	 * @param source	二进制原文
	 * @param hasDetached	是否带原文
	 * @param password usbKey的密码
	 * @return			PKCS#7Base64编码的签名值
	 * @throws PkiException 
	 */
	public static String signedDataByCertificate(Certificate certificate, byte[] source, boolean hasDetached, String password)
			throws PkiException{
		if(isEmpty(source)){
			throw new PkiException("电子签名验签不正确.签名信息为空");
		}
		if(certificate==null){
			throw new PkiException("找不到电子签名的数字证书，请运行证书安全客户端软件，并插入一个证书介质Key!"); 
		}
		byte[] signValue = null;
		SignedData signedData = null;

		try {
			signedData = new SignedData(true);
			signedData.setDetached(hasDetached); // 是否带原文（false：带，true：不带） 证书链4k
			if (isEmpty(password)) {
				signedData.setSignCertificate(certificate);
			} else {
				signedData.setSignCertificate(certificate, password);
			}
			String defaultAlgo = Integer.valueOf(KeyPair.RSA).toString();
			String certAlgo = getX509CertificateInfo(certificate, NETCAPKI_CERT_INFO_PUBLICKEYALGORITHM);

			signedData.setSignAlgorithm(0, certAlgo.equals(defaultAlgo) ? NETCAPKI_ALGORITHM_RSASIGN
					: NETCAPKI_ALGORITHM_SM2SIGN); 
			signedData.setIncludeCertOption(NETCAPKI_SIGNEDDATA_INCLUDE_CERT_OPTION);
			signValue = signedData.sign(source);
			return Base64.encode(NETCAPKI_BASE64_ENCODE_NO_NL, signValue);
		} catch (PkiException e) {
			throw new PkiException("电子签名及验签异常发生："+e.getMessage());
		} finally {
			if (signedData != null){
				signedData.free();
			}
			signedData = null;
		}
	}
	
	/**
	 * PKCS#7时间戳签名
	 * 
	 * @param source 原文，即签名内容
	 * @param url 时间戳服务器URL
	 * @param hasDetached 是否带原文（false：带，true：不带）
	 * @return 签名值
	 * @throws PkiException
	 * @throws Exception
	 */
	public static String signedDataWithTSA(String source, String url, boolean hasDetached) throws PkiException {
		
		Certificate certificate = null;
		try {
			certificate = getX509Certificate(Certificate.PURPOSE_SIGN);
			return signedDataWithTSA(certificate, source, url, hasDetached);
		} finally {
			if (certificate != null) {
				certificate.free();
			}
			certificate = null;
		}
	}

	/**
	 * 时间戳签名核心方法
	 * 
	 * @param certificate 签名用到的证书
	 * @param source 原文，即签名内容
	 * @param url 时间戳服务器URL
	 * @param hasDetached  是否带原文（false：带，true：不带）
	 * @return 签名值信息
	 * @throws Exception
	 */
	public static String signedDataWithTSA(Certificate certificate, String source, String url, boolean hasDetached)
			throws PkiException {

		if(isEmpty(source)){
			throw new PkiException("在对时间戳进行签名的过程中，待签名的原文为空");
		}
		if(isEmpty(url)){
			throw new PkiException("在对时间戳进行签名的过程中，时间戳服务URL为空");
		}
		if(certificate==null){
			throw new PkiException("找不到电子签名的数字证书，请运行证书安全客户端软件，并插入一个证书介质Key!");
		}
		byte[] signValue = null;
		SignedData signedData = null;

		try {
			signedData = new SignedData(true);
			signedData.setDetached(hasDetached);

			signedData.setSignCertificate(certificate);

			String defaultAlgo = Integer.valueOf(KeyPair.RSA).toString();
			String certAlgo = getX509CertificateInfo(certificate, NETCAPKI_CERT_INFO_PUBLICKEYALGORITHM);

			signedData.setSignAlgorithm(0, certAlgo.equals(defaultAlgo) ? NETCAPKI_ALGORITHM_RSASIGN
					: NETCAPKI_ALGORITHM_SM2SIGN);
			signValue = signedData.signWithTimeStamp(convertByte(source),url);
			return base64Encode(signValue);
		} catch (PkiException e) {
			throw new PkiException("时间戳签名及验签异常发生："+e.getMessage());
		} finally {
			if (signedData != null) {
				signedData.free();
			}
			signedData = null;
		}
	}

	/**
	 * 验证时间戳签名值
	 * 
	 * @param source  	 原文
	 * @param signValue  签名值
	 * @return 签名的证书（验证成功返回一个证书）
	 * @throws Exception
	 */
	public static String verifySignedDataWithTSA(String source, String signValue) throws PkiException {

		if(isEmpty(source)){
			throw new PkiException("电子签名验签不正确，原文为空");
		}
		if(isEmpty(signValue)){
			throw new PkiException("电子签名验签不正确，签名信息为空");
		}
		SignedData signedData = null;
		byte[] verifyValue = null;
		byte[] signValueArray = null;

		try {
			signValueArray = Base64.decode(NETCAPKI_BASE64_ENCODE_NO_NL, signValue);
			signedData = new SignedData(false);
			signedData.verifyInit();
			signedData.setVerifyValidity(false);

			// 判断是否为SignedData编码
			if (!SignedData.isSign(signValueArray)) {
				throw new PkiException("验证时间戳签名值，电子签名验签不通过:签名信息格式不正确!");
			}
			boolean isVerifyPass = true;
			// 判断是否带原文
			if (SignedData.isDetachedSign(signValueArray)) {
				// 不带原文
				isVerifyPass = signedData.detachedVerify(convertByte(source), signValueArray);
				if (!isVerifyPass){
					throw new PkiException("验证时间戳签名值，签名信息验证未通过"); // 错误信息。
				}
			} else {
				// 带原文
				verifyValue = signedData.verifyUpdate(signValueArray, 0, signValueArray.length);
				signedData.verifyFinal();
				if (!compareByteArrays(convertByte(source), verifyValue))
					throw new PkiException("验证时间戳签名值，电子签名验签不通过，原文与签名信息不符!");
			}

			int count = signedData.getSignerCount();

			for (int i = 0; i < count; i++) {
				if (signedData.verifyTimeStamp(i)) {
					Date date = signedData.getTimeStampTime(i);
					SimpleDateFormat formatter;
					formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					return formatter.format(date);
				}
			}
		} catch (PkiException e) {
			throw new PkiException("时间戳签名及验签异常发生："+e.getMessage());
		} finally {
			if (signedData != null){
				signedData.free();
			}
			signedData = null;
		}
		return null;
	}
	
	/**
	 * PKCS7签名验证并获取签名证书
	 * 
	 * @param signValue 签名值
	 * @return 签名的证书（验证成功返回一个证书）
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	public static Certificate verifySignedData(String sSource, String signValue)
		throws PkiException, UnsupportedEncodingException {
		try {
			byte[] data = base64Decode(signValue);
			return verifySignedData(sSource, data);
		} catch (PkiException e) {
			throw new PkiException("电子签名验签不通过："+e.getMessage());
		}
	}
	/**
	 * PKCS7时间戳签名验证并获取证书（字节数组签名值）
	 * 
	 * @param signValue 签名值
	 * @return 签名的证书（验证成功返回一个证书）
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	public static Certificate verifySignedData(String sSource, byte[] signValue)
		throws PkiException, UnsupportedEncodingException {

		return (Certificate) getVerifySignedData(sSource, signValue)[1];
	}

	/**
	 * 带原文PKCS#7签名,验证并获取原文。
	 * @param signValue		签名信息
	 * @return				原文
	 * @throws PkiException
	 * @throws UnsupportedEncodingException
	 */
	public static String getSourceFromSignedData(String signValue)
			throws PkiException, UnsupportedEncodingException {
		byte[] data = base64Decode(signValue);
		SignedData signedData = null;
		byte[] source = null;

		try {
			if(!SignedData.isSign(data)){
				throw new PkiException("电子签名验签不通过，签名信息格式不正确");
			}
			signedData = new SignedData(false);
			signedData.setVerifyValidity(false);
			boolean detached = SignedData.isDetachedSign(data);
			if(detached){
				throw new PkiException("电子签名验签不通过，签名信息不带原文信息，不能还原出原文");
			}
			source = signedData.verify(data);			//>>>>>>>>>>验证原文与解析出来的原文
			
			return new String(source,NETCAPKI_CP);
		}catch(UnsupportedEncodingException e){
			throw new UnsupportedEncodingException("电子签名验签不通过，字节数组转数字出错："+e.getMessage());
		}catch (PkiException e) {
			throw new PkiException("电子签名验签不通过："+e.getMessage());
		} finally {
			if (signedData != null){
				signedData.free();
			}
			signedData = null;
		}
	}
	
	/**
	 * PCK1签名
	 * 
	 * @param source 原文
	 * @return 签名值
	 * @throws PkiException
	 */
	public static String signByCertificate(String source) throws PkiException {

		if(isEmpty(source)){
			throw new PkiException("电子签名验签不正确，原文为空");
		}
		Certificate certificate = null;
		try {
			certificate = getX509Certificate(Certificate.PURPOSE_SIGN);
			return signByCertificate(certificate, source);
		} catch (PkiException e) {
			throw new PkiException("电子签名验签不正确："+e.getMessage());
		} finally {
			if (certificate != null) {
				certificate.free();
			}
			certificate = null;
		}
	}

	/**
	 * PCK1签名
	 * 
	 * @param certificate  签名证书
	 * @param source 原文
	 * @return Base64编码的签名值
	 * @throws Exception
	 */
	public static String signByCertificate(Certificate certificate, String source) throws PkiException {

		if(isEmpty(source)){
			throw new PkiException("电子签名验签不正确，原文为空");
		}
		if(certificate==null){
			throw new PkiException("找不到电子签名的数字证书，请运行证书安全客户端软件，并插入一个证书介质Key!");
		}
		int algo = 0;
		byte[] signValue = null;
		Signature signature = null;
		try {
			String str = getX509CertificateInfo(certificate, NETCAPKI_CERT_INFO_PUBLICKEYALGORITHM);
			if (str.equals(Integer.valueOf(KeyPair.RSA).toString()))
				algo = NETCAPKI_ALGORITHM_RSASIGN; 
			else
				algo = NETCAPKI_ALGORITHM_SM2SIGN;
			signature = new Signature(algo, certificate.getKeyPair(Certificate.SEARCH_KEYPAIR_FLAG_CURRENT_USER,
					Certificate.PURPOSE_SIGN, null));
			signature.update(convertByte(source));
			signValue = signature.sign();
			return base64Encode(signValue);
		} catch (PkiException e) {
			throw new PkiException("电子签名验签不正确："+e.getMessage());
		} finally {
			if (signature != null) {
				signature.free();
			}
			signature = null;
		}

	}
	
	/**
	 * PKCS#1签名验证
	 * 
	 * @param strCertificate	证书的Base64编码值
	 * @param source			原文
	 * @param signValue			签名值
	 * @return					是否验证签名成功
	 * @throws PkiException
	 */
	public static boolean verify(String strCertificate, String source, String signValue) throws PkiException {
		
		Certificate certificate = null;
		try {
			certificate = getX509Certificate(strCertificate);
			byte[] data = convertByte(source);
			return verify(certificate, data, signValue);
		} catch (PkiException e) {
			throw new PkiException("电子签名验签不正确："+e.getMessage());
		} finally {
			if(certificate!=null){
				certificate.free();
			}
			certificate = null;
		}
	}
	
	/**
	 * PKCS#1签名验证
	 * 
	 * @param certificate		证书 
	 * @param source			原文
	 * @param signValue			签名值
	 * @return					是否验证签名成功
	 * @throws PkiException
	 */
	public static boolean verify(Certificate certificate, String source, String signValue) throws PkiException {
		return verify(certificate, convertByte(source), signValue);
	}
	
	/**
	 * PKCS#1签名验证。
	 * 
	 * @param certificate	证书
	 * @param source		原文
	 * @param signValue		签名信息
	 * @return				验证是否通过
	 * @throws Exception
	 */
	public static boolean verify(Certificate certificate, byte[] source, String signValue) throws PkiException {
		
		if(isEmpty(source)){
			throw new PkiException("电子签名验签不正确，原文为空");
		}
		if(isEmpty(signValue)){
			throw new PkiException("电子签名验签不正确，签名信息为空");
		}
		if(certificate==null){
			throw new PkiException("找不到电子签名的数字证书，请运行证书安全客户端软件，并插入一个证书介质Key!");
		}
		int algo = 0;
		byte[] signValueArray = null;
		Signature signature = null;

		try {
			signValueArray = base64Decode(signValue);
			if (getX509CertificateInfo(certificate, NETCAPKI_CERT_INFO_PUBLICKEYALGORITHM)
					.equals(Integer.valueOf(KeyPair.RSA).toString()))
				algo = NETCAPKI_ALGORITHM_RSASIGN;
			else
				algo = NETCAPKI_ALGORITHM_SM2SIGN;
			signature = new Signature(algo, certificate.getPublicKey(Certificate.PURPOSE_SIGN));
			signature.update(source); // source:1497840944288|0|18f76ebac352a843b90b83657a07a2c1749b7850|0
			return signature.verify(signValueArray);
		} catch (PkiException e) {
			throw new PkiException("电子签名验签不正确："+e.getMessage());
		} finally {
			if (signature != null){
				signature.free();
			}
			signature = null;
			// 只需要把自己申明定义的signature，free掉，而参数certificate不可以free，因为有可能在上一级代码中还需要继续使用。
		}
	}

	
	/**===================================================================================================
	 * 5.加解密操作
	 * ===================================================================================================
	 */
	/**
	 *[较常用]数字信封加密
	 * 使用自己的加密证书加密信息
	 * 
	 * @param source 原文，待加密值（字节数组）
	 * @return Base64编码过后的加密值
	 * @throws PkiException
	 */
	public static String envelopedData(byte[] source) throws PkiException {

		if(isEmpty(source)){
			throw new PkiException("数字信封加密不正确，待加密值为空");
		}
		Certificate certificate = null;
		try {
			certificate = getX509Certificate(Certificate.PURPOSE_ENCRYPT);
			if (certificate == null) {
				throw new PkiException("找不到加密的数字证书，请运行证书安全客户端软件，并插入一个证书介质Key!");
			}
			return envelopedData(certificate, source);
		} catch (PkiException e) {
			throw new PkiException("数字信封加密不通过："+e.getMessage());
		}
	}

	/**
	 * 数字信封加密
	 * 用服务器证书加密
	 * 
	 * @param strCertificate 	数字信封使用到的证书Base64编码值。
	 * @param source 			原文，待加密值
	 * @return 					加密值
	 * @throws PkiException
	 */
	public static String envelopedData(String strCertificate, byte[] source) throws PkiException {
		
		Certificate certificate = null;
		try {
			certificate = getX509Certificate(strCertificate);
			return envelopedData(certificate, source);
		} catch (PkiException e) {
			throw new PkiException("数字信封加密异常发生："+e.getMessage());
		} finally {
			if(certificate!=null){
				certificate.free();
			}
			certificate = null;
		}
	}
	
	/**
	 * 数字信封加密的核心方法
	 * 
	 * @param certificate	数字信封使用到的证书。
	 * @param source 		原文，待加密值
	 * @return 				加密值
	 * @throws PkiException
	 */
	public static String envelopedData(Certificate certificate, byte[] source) throws PkiException {

		if(isEmpty(source)){
			throw new PkiException("数字信封加密不正确，待加密值为空");
		}
		byte[] envelopedValue = null;

		// 1. 对参数进行筛选
		if (certificate == null) {
			throw new PkiException("找不到加密的数字证书，请运行证书安全客户端软件，并插入一个证书介质Key!");
		}
		EnvelopedData envelopedData = null;

		try {
			envelopedData = new EnvelopedData(true);
			int algo = 0;
			String defalutAlgo = getX509CertificateInfo(certificate, NETCAPKI_CERT_INFO_PUBLICKEYALGORITHM);
			if (defalutAlgo.equals(Integer.valueOf(KeyPair.RSA).toString())) {
				algo = NETCAPKI_ENVELOPEDDATA_ALGORITHM_RSAENV; 
			} else {
				algo = NETCAPKI_ENVELOPEDDATA_ALGORITHM_SM2ENV;
			}
			envelopedData.setEncryptAlgorithm(algo);
			envelopedData.addCertificate(certificate, true);
			envelopedValue = envelopedData.encrypt(source);

			return Base64.encode(NETCAPKI_BASE64_ENCODE_NO_NL, envelopedValue);
		} catch (PkiException e) {
			throw new PkiException("数字信封加密不通过："+e.getMessage());
		} finally {
			if (envelopedData != null){
				envelopedData.free();
			}
			envelopedData = null;
		}
	}

	/**
	 * 数字信封解密
	 * 
	 * @param envelopedValue	数字信封解码信息
	 * @return					解码值
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] developedData(String envelopedValue) throws PkiException, UnsupportedEncodingException {

		EnvelopedData envelopedData = null;
		if (isEmpty(envelopedValue)) {
			throw new PkiException("数字信封需要解码的信息为空");
		}
		byte[] envValue = base64Decode(envelopedValue);
		byte[] developedValue = null;
		try {
			envelopedData = new EnvelopedData(false);
			developedValue = envelopedData.decrypt(envValue);
			return developedValue;
		} catch (PkiException e) {
			throw new PkiException("数字信封解码不通过："+e.getMessage());
		} finally {
			if (envelopedData != null){
				envelopedData.free();
			}
			envelopedData = null;
		}
	}

	/**
	 * 数字信封解密，使用PFX软证书
	 * 使用场景：客户端用cer文件进行加密，服务器端使用PFX软证书进行解密
         1).在负载均衡情况下，PFX证书应放到公共位置（如FTP服务器），解密时下载到本服务器，进行解密；
         2).为防止PIN码泄露，解密后应变更密码；
	 * @param certKeyStorePath	软证书jks或者pfx路径
	 * @param keyStoreType		keyStore类型
	 * @param certPath			证书路径
	 * @param password			证书密码
	 * @param envelopedValue	加密数据
	 * @return
	 * XXX_KEYSTORE_TYPE_JKS=""
	 * KEYSTORE_TYPE_PFX=""
	 */
	public static byte[] developedDataBySoft(String certKeyStorePath, String keyStoreType, String certPath, String password, String envelopedValue)
			throws PkiException, KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
		
		if (isEmpty(certKeyStorePath)) {
			throw new PkiException("在使用软证书进行数字信封解密中，软证书的值为空");
		}
		if (isEmpty(envelopedValue)) {
			throw new PkiException("在使用软证书进行数字信封解密中，待加密的加密值为空");
		}
		
		Certificate certificate = null;
		InputStream inputStream = null;
		EnvelopedData envelopedData = null;
		KeyPair keypair = null;
		KeyStore keystore = null;
		byte[] envValue = null;
		byte[] developedValue = null;
		byte[] data = null;
		
		try {
			inputStream = new FileInputStream(certKeyStorePath);
			data = readFile(certPath);	
			certificate = getX509Certificate(data);
			envValue = Base64.decode(NETCAPKI_BASE64_ENCODE_NO_NL, envelopedValue);  

			keystore = KeyStore.getInstance(keyStoreType);
			keystore.load(inputStream, password.toCharArray()); 

			keypair = Util.getKeyPairFromKeyStore(keystore, certificate, password);
			if (keypair == null) {
				throw new PkiException("数字信封加密不正确，获取的密钥对为空");
			}
			certificate.setKeyPair(keypair);  
			envelopedData = new EnvelopedData(false);  
			envelopedData.addDecryptCertificate(certificate);  
			envelopedData.decryptInit();  
			developedValue = envelopedData.decryptUpdate(envValue, 0, envValue.length);  
			envelopedData.decryptFinal();  
			return developedValue;
			
		} catch (PkiException e) {
			throw new PkiException("数字信封加密过程中出错："+e.getMessage());
		} catch (KeyStoreException e) {
			throw new KeyStoreException("数字信封加密，密钥库获取实例失败："+e.getMessage());
		} catch (IOException e) {
			throw new IOException("数字信封加密，密钥库加载失败，输入流出错："+e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			throw new NoSuchAlgorithmException("数字信封加密，密钥库加载失败，没有对应的加密算法："+e.getMessage());
		} catch (CertificateException e) {
			throw new CertificateException("数字信封加密，密钥库加载失败："+e.getMessage());
		} finally {
			if (keypair != null) {
				keypair.free();
			}
			if (envelopedData != null) {
				envelopedData.free();
			}
			if (certificate != null) {
				certificate.free();
			}
			if(inputStream!=null){
				try {
					inputStream.close();
				} catch (IOException e) {
					throw new IOException("数字信封解密，使用PFX软证书过程中，关闭输入流失败");
				}
			}
			keypair = null;
			envelopedData = null;
			certificate = null;
			inputStream = null;
		}
	}

	/**
	 * 非对称加密
	 * 
	 * @param source 	原文
	 * @return		 	加密值
	 * @throws PkiException
	 * @throws Exception
	 */
	public static byte[] asymmetriEncrypt(String source) throws PkiException {

		if (isEmpty(source)){
			throw new PkiException("电子加解密不正确，原文为空");
		}
		Certificate certificate = null;
		try {
			certificate = getX509Certificate(Certificate.PURPOSE_ENCRYPT);
			return asymmetriEncrypt(source, certificate);
		} catch (PkiException e) {
			throw new PkiException("电子加密及解密异常发生："+e.getMessage());
		} finally {
			if (certificate != null) {
				certificate.free();
				certificate = null; 
			}
		}
	}

	/**
	 * 非对称加密
	 * 
	 * @param source 原文
	 * @param certificate 证书
	 * @return 加密值
	 * @throws PkiException
	 * @throws Exception
	 */
	private static byte[] asymmetriEncrypt(String source, Certificate certificate) throws PkiException {

		if (isEmpty(source)) {
			throw new PkiException("电子加解密不正确，原文为空");
		}
		if(certificate==null) {
			throw new PkiException("找不到加密的数字证书，请运行证书安全客户端软件，并插入一个证书介质Key!");
		}
		try {
			byte[] encryptValue = certificate.encrypt(convertByte(source));
			return encryptValue;
		} catch (PkiException e) {
			throw new PkiException("电子加密及解密异常发生："+e.getMessage());
		}
	}

	/**
	 * 非对称解密
	 * 
	 * @param encryptValue 加密值
	 * @return 解密值
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	public static String asymmetriDecrypt(byte[] encryptValue) throws PkiException, UnsupportedEncodingException {

		if (isEmpty(encryptValue)){
			throw new PkiException("电子加解密不正确，加密值为空");
		}
		Certificate certificate = null;
		try {
			certificate = getX509Certificate(Certificate.PURPOSE_ENCRYPT);
			return asymmetriDecrypt(certificate, encryptValue);
		} catch (PkiException e) {
			throw new PkiException("非对称解密出错："+e.getMessage());
		} finally {
			if (certificate != null) {
				certificate.free();
				certificate = null;
			}
		}
	}

	/**
	 * 非对称解密
	 * 
	 * @param encryptValue 加密值
	 * @param certificate  证书
	 * @return 解密值
	 * @throws PkiException
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	private static String asymmetriDecrypt(Certificate certificate, byte[] encryptValue)
			throws PkiException, UnsupportedEncodingException {

		if (isEmpty(encryptValue)){
			throw new PkiException("电子加解密不正确，加密值为空");
		}
		if(certificate==null) {
			throw new PkiException("找不到解密的数字证书，请运行证书安全客户端软件，并插入一个正确证书介质Key!");
		}
		try {
			byte[] decryptValue = certificate.decrypt(encryptValue);
			return new String(decryptValue, NETCAPKI_CP);
		} catch (PkiException e) {
			throw new PkiException("电子加密及解密异常发生："+e.getMessage());
		} catch (UnsupportedEncodingException e) {
			throw new UnsupportedEncodingException("电子加密及解密异常发生，字节数组转字符串失败");
		}
	}


	/**=======================================================================================
	 * 6. 时间戳操作
	 * =======================================================================================
	 */
	/**
	 * 获得时间戳
	 * 一期："https://classatsa.cnca.net/tsa.asp";（已做废）
	 * 
	 * @param source 原文
	 * @param url 时间戳获取地址
	 * @return 返回时间戳相对应值，字符串数组
	 * @throws PkiException
	 * 说明：一般来说，时间戳地址：http://tsa.cnca.net/NETCATimeStampServer/TSAServer.jsp
	 * 第一位：时间；
	 * 第二位：时间戳Token;
	 * 第三位：时间戳证书；
	 */
	public static String[] getTimeStamp(String source, String url) throws PkiException {

		if(isEmpty(source)) {
			throw new PkiException("获取时间戳不正确，原文为空");
		}
		if(isEmpty(url)) {
			throw new PkiException("获取时间戳不正确，时间戳地址为空");
		}
		TimeStamp timeStamp = null;
		String[] ts = new String[3];

		try {
			timeStamp = new TimeStamp();

			timeStamp.setTsaURL(url);
			int algo = Hash.SHA256;
			byte[] md = getAProperty(algo, source);
			timeStamp.setHashAlgorithm(algo);
			timeStamp.setMessageImprint(md);
			int status = timeStamp.getResponse();

			if (status == TimeStamp.RESP_STATUS_GRANTED || status == TimeStamp.RESP_STATUS_GRANTEDWITHMODS) {
				//1.时间
				Date time = timeStamp.getTime();
				SimpleDateFormat formatter;
				formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				ts[0] = formatter.format(time);
				
				//2.时间戳Token
				byte[] token = timeStamp.getToken();
				ts[1] = Base64.encode(NETCAPKI_BASE64_ENCODE_NO_NL, token);
				
				//3.时间戳证书(Base64编码)
				ts[2] = timeStamp.getTsaCertificate().pemEncode();
				
				return ts;
			}else{
				throw new PkiException(printStatusError(status));
			}
		} catch (PkiException e) {
			throw new PkiException("获取时间戳出现异常："+e.getMessage());
		} finally {
			if (timeStamp != null) {
				timeStamp.free();
			}
			timeStamp = null;
		}
	}
	
	
	/**
	 * 时间戳验证
	 * 
	 * @param source	加戳数据
	 * @param token		时间戳Token 
	 * @return			若验证成功即返回时间戳的时间，否则为null
	 * @throws PkiException
	 * 
	 * 说明：时间格式为 yyyy-MM-dd HH:mm:ss
	 */
	public static String verifyTimeStampToken(String source, String token)throws PkiException{
		
		if(isEmpty(source)){
			throw new PkiException("时间戳验签不正确，原文为空");
		}
		if(isEmpty(token)){
			throw new PkiException("时间戳验签不正确，token为空");
		}
		TimeStamp timeStamp = null;
		Date time = null;
		try {
			byte[] source2 = convertByte(source);
			byte[] token2 = base64Decode(token);
			timeStamp = TimeStamp.verifyTimeStamp(source2, token2);
			
			if(timeStamp==null){
				return null;		// flag = utilTSA.IsValidTime(timeTSA);//判断验证返回时间是否有效
			}
			
			time = timeStamp.getTime();
			SimpleDateFormat formatter;
			formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return formatter.format(time);
		} catch (PkiException e) {
			throw new PkiException("时间戳验签异常发生："+e.getMessage());
		} finally {
			if(timeStamp!=null){
				timeStamp.free();
			}
			timeStamp = null;
		}
	}
	
	/**===================================================================================================
	 * 7. 设备设施操作
	 * ===================================================================================================
	 */
	/**
	 * 获取设备中的序列号List
	 * 第一个介质的介质序列号
	 * 
	 * @return 设备中的序列号集合
	 * @throws PkiException
	 * @throws Exception
	 */
	public static List<String> getKeySerial() throws PkiException {

		ArrayList<String> list = new ArrayList<String>();
		DeviceSet deviceSet = null;

		try {
			deviceSet = new DeviceSet(Device.ANY_DEVICE, Device.DEVICE_FLAG_SILENT);
			int count = deviceSet.count();
			for (int i = 0; i < count; i++) {
				list.add(deviceSet.get(i).getSerialNumber());
			}
			return list;
		} catch (PkiException e) {
			throw new PkiException("获取设备中的序列号集合，创建设备集出错："+e.getMessage());
		} finally {
			if (deviceSet != null) {
				deviceSet.free();
				deviceSet = null;
			}
		}

	}

	/**
	 * 获取介质设备
	 * 
	 * @return 返回当前系统中所有的设施的集合
	 */
	public static DeviceSet getDeviceSet() throws PkiException{

		DeviceSet deviceSet = null;

		try {
			deviceSet = new DeviceSet(Device.ANY_DEVICE, Device.DEVICE_FLAG_SILENT);
		} catch (PkiException e) {
			throw new PkiException("获取设备中的序列号集合，创建设备集出错："+e.getMessage());
		}
		return deviceSet;
	}

	/**
	 * 根据key序列号，选择用户证书
	 * @param keySN		key序列号
	 * @return			证书
	 */
	public static Certificate getCertWithDeviceSN(String keySN){
		
		String type = getType(NETCAPKI_CERTFROM, NETCAPKI_CERT_PURPOSE_ALL);
		
		String expr = "InValidity='True'";
		if (NETCAPKI_SUPPORTCAS.length > 0) {
			expr += "&&(";
			for (int i = 0; i < NETCAPKI_SUPPORTCAS.length; i++) {
				if (i == 0){
					expr += "IssuerCN~'" + NETCAPKI_SUPPORTCAS[i] + "'";
				}else{
					expr += "||IssuerCN~'" + NETCAPKI_SUPPORTCAS[i] + "'";
				}
			}
			expr += ")";
		}
		expr += "&&DeviceSN='" + keySN + "'";
		
		return getX509Certificate(type, expr);
	}

	
	
	/**
	 * 获取config配置文件里边，key对应的value值
	 * @param key	key值
	 * @return		value值
	 */
	public static String getAProperty(String key) {
		// 获得资源包  
		/**
		 * 注意src文件夹下有没有netca_config.properties文件（该文件用于配置网关&服务器证书！）
		 */
	    ResourceBundle rb = ResourceBundle.getBundle("netca_config");
	    // 通过资源包拿到所有的key  
	    Enumeration<String> allKey = rb.getKeys();
	    // 遍历key 获得对应的 value  
	    while (allKey.hasMoreElements()) {  
	        String k = allKey.nextElement();
	        if(key.equals(k)){
	        	return (String) rb.getString(key);
	        }
	    }  
	    return null;
	}
	
	/**
	 * 检查certKeyStorePath路径下，解析出来的证书，证书服务号是否为certServiceNum
	 * @param certKeyStorePath			软证书路径
	 * @param storePassword				证书库密码
	 * @param certServiceNum			证书服务号
	 * @return							成功返回符合的证书，失败抛出异常、或者返回null
	 * @throws Exception
	 */
	public static Certificate checkCertBySoft(String certKeyStorePath, String storePassword, String certServiceNum)
			throws Exception {
		
		Certificate certificate = null;
		CertStore certStore = null;
		FileInputStream certStoreInputStream = null;
		
		if(isEmpty(certServiceNum)){
			throw new Exception("证书服务号为空");
		}
		
		
		try {
			certStoreInputStream = new FileInputStream(certKeyStorePath);
			int count = certStoreInputStream.available();
			byte[] data = new byte[count];
			certStoreInputStream.read(data);
			certStore = CertStore.loadPfx(data, storePassword);
			for(int i = 0;i<certStore.getCertificateCount();i++){
				certificate = certStore.getCertificate(i);
				String certAttribute = getX509CertificateInfo(certificate, NETCAPKI_CERT_INFO_CUSTOMER_NUMBER);
				if(certServiceNum.equals(certAttribute)){
					return certificate;
				}else{
					continue;
				}
			}
			return null;
		}catch (Exception e) {
			throw new Exception("检查软证书中证书服务号是否为"+certServiceNum+"过程中出错："+e.getMessage());
		}finally {
			if(certStoreInputStream!=null){
				certStoreInputStream.close();
			}
			if(certStore!=null){
				certStore.close();
			}
		}
	}

	/**
	 * 两个字节数组的对比
	 * 
	 * @param source
	 *            字节数组1
	 * @param target
	 *            字节数组2
	 * @return 两个数组完全相同，则返回true，反之，则false
	 */
	private static boolean compareByteArrays(byte[] source, byte[] target) {

		int count = source.length;
		if (count != target.length) {
			return false;
		}
		for (int i = 0; i < count; i++) {
			if (source[i]!=target[i]){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * hash运算
	 * @param data	源数据
	 * @param algo	hash算法
	 * @return		摘要值
	 * @throws PkiException
	 */
	private static byte[] getHashData(byte[] data,int algo) throws PkiException{

		if (isEmpty(data)) {
			throw new PkiException("获取信息摘要码（Hex编码方式）不通过，信息数据为空");
		}
		Hash hash = null;
		try {
			hash = new Hash(algo);
			hash.update(data);
			return hash.doFinal();
		} catch (PkiException e) {
			throw new PkiException("获取信息摘要码（Hex编码方式）异常发生："+e.getMessage());
		} finally {
			if(hash!=null){
				hash.free();
			}
			hash = null;
		}
	}
	
	/**
	 * 获得证书类型
	 * 证书类型，也可以是获取条件的JSON。可以参考《NETCA PKI API参考手册》
	 * @param method	证书的获取方法（设备、证书库）
	 * @param purpose	证书作用类型（加密、签名）
	 * @return
	 */
	private static String getType(String method, int purpose) {
		String type = "{\"UIFlag\":\"default\",\"InValidity\":true,";
		if (purpose == Certificate.PURPOSE_SIGN){
			type += "\"Type\":\"signature\",";
		}else if (purpose == Certificate.PURPOSE_ENCRYPT){
			type += "\"Type\":\"encrypt\",";
		}
		
		if (method.toLowerCase().equals("device")){
			type += "\"Method\":\"device\",\"Value\":\"any\"";
		}else{
			type += "\"Method\":\"store\",\"Value\":{\"Type\":\"current user\",\"Value\":\"my\"}";
		}
		type += "}";
		return type;
	}

	/**
	 * 获得表达式条件
	 * @param purpose	证书作用类型（加密、签名）
	 * @return
	 */
	private static String getFilter(int purpose) {
		String filter = "InValidity='True'";
		if (NETCAPKI_SUPPORTCAS.length > 0) {
			filter += "&&(";
			for (int i = 0; i < NETCAPKI_SUPPORTCAS.length; i++) {
				if (i == 0){
					filter += "IssuerCN~'" + NETCAPKI_SUPPORTCAS[i] + "'";
				}else{
					filter += "||IssuerCN~'" + NETCAPKI_SUPPORTCAS[i] + "'";
				}

			}
			filter += ")";
		}
		if (purpose == Certificate.PURPOSE_SIGN){
			filter += "&&CertType='Signature'&&CheckPrivKey='True'";
		}else if (purpose == Certificate.PURPOSE_ENCRYPT){
			filter += "&&CertType='Encrypt'";
		}

		return filter;
	}
	
	/**
	 * 带原文PKCS#7签名,验证并获取原文。
	 * @param signValue		签名信息
	 * @return				原文和证书的对象（0：原文，1：证书）
	 * @throws PkiException
	 * @throws UnsupportedEncodingException
	 */
	private static Object[] getVerifySignedData(String sSource, byte[] signValue)
			throws PkiException, UnsupportedEncodingException {

		if(isEmpty(signValue)){
			throw new PkiException("电子签名验签不正确，签名信息为空");
		}
		Object[] objects = new Object[2];
		SignedData signedData = null;
		byte[] source = null;

		try {
			signedData = new SignedData(false);
			signedData.setVerifyValidity(false);
			
			//2017-8-2  是否带原文
			boolean detached = SignedData.isDetachedSign(signValue);
			if(detached){		//不带原文
				source = convertByte(sSource);
				boolean tbs = signedData.detachedVerify(source, 0, source.length, signValue );
				if(!tbs){
					throw new PkiException("签名信息验签不通过");
				}
				objects[0] = null;
			}else{				//带原文
				source = signedData.verify(signValue);			//>>>>>>>>>>验证原文与解析出来的原文
				if (source == null || source.length == 0) {
					throw new PkiException("电子签名验签不正确，原文为空");
				}
				objects[0]=new String(source,NetcaPKI.NETCAPKI_CP);
				
				if(!((String)objects[0]).equals(sSource)){
					throw new PkiException("电子签名验签不通过，原文与签名信息不符");
				}
				
			}
			objects[1] = signedData.getSignCertificate(0);
			return objects;
			
		}catch(UnsupportedEncodingException e){
			throw new UnsupportedEncodingException("电子签名及验签异常发生，字节数组转数字出错："+e.getMessage());
		}catch (PkiException e) {
			throw new PkiException("电子签名及验签异常发生："+e.getMessage());
		} finally {
			if (signedData != null){
				signedData.free();
			}
		}

	}
	
	/**
	 * 打印状态值对应的文字说明
	 * @param status	状态值
	 * @return			文字说明
	 */
	private static String printStatusError(int status){
		switch(status){
			case -1:return "时间戳错误的响应";
			case -2:return "时间戳签名证书不对";
			case 2:return "时间戳被拒绝";
			case 3:return "时间戳等待";
			case 4:return "时间戳停止服务的警告";
			case 5:return "时间戳停止服务";
			default: return "获取时间戳错误,其他错误";
		}
	}
	
	/**
	 * 获取随机数，并经过hash运算的字节数组值
	 * 
	 * @param algo hash算法
	 * @param source 原文
	 * @return 返回随机字节数组进行hash运算的字节数组
	 * @throws PkiException
	 */
	private static byte[] getAProperty(int algo, String source) throws PkiException {
		Hash hash = null;
		byte[] in = convertByte(source);
		byte[] data;

		try {
			hash = new Hash(algo);
			hash.update(in);
			data = hash.doFinal();
		} catch (PkiException e) {
			throw new PkiException("获取随机数，hash运算异常发生："+e.getMessage());
		} finally {
			if (hash != null) {
				hash.free();
			}
		}
		return data;
	}

	
	/**
	 * 判断是否为空字符串
	 * @param content
	 * @return
	 */
	private static boolean isEmpty(String content) {
		if (content == null || content.trim().length() == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 判断是否为空字节数组
	 * @param content
	 * @return
	 */
	private static boolean isEmpty(byte[] content) {
		if (content == null || content.length == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	
	
	//////////////////////////////////未正式写入文档-暂时不提倡使用//////////////////////////
	/**
	 * [未正式写入文档-暂时不提倡使用]
	 * 签名，使用PFX软证书
	 * @param certKeyStorePath	软证书pfx路径
	 * @param source			原文
	 * @return
	 * XXX_KEYSTORE_TYPE_JKS=""
	 * KEYSTORE_TYPE_PFX=""
	 */
	public static String signedDataByPfx(String certKeyStorePath, String storePassword, String keyPassword, String source, boolean hasDetached)
			throws PkiException, KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
		
		
		Certificate certificate = null;
		if (isEmpty(source)) {
			throw new PkiException("在使用软证书进行数字信封解密中，待加密的加密值为空");
		}
		if (isEmpty(certKeyStorePath)) {
			throw new PkiException("在使用软证书进行验签中，软证书的值为空");
		}
		
		InputStream certStoreInputStream = null;
		InputStream keyStoreInputStream = null;
		CertStore certStore = null;
		
		try {
			certStoreInputStream = new FileInputStream(certKeyStorePath);
			keyStoreInputStream = new FileInputStream(certKeyStorePath);
			int count = certStoreInputStream.available();
			byte[] data = new byte[count];
			certStoreInputStream.read(data);
			certStore = CertStore.loadPfx(data, storePassword);
			certificate = certStore.getCertificate(0);
			
			return signedDataByCertificate(certificate, source, hasDetached, keyPassword);
			
		}  finally {
			if(keyStoreInputStream!=null){
				keyStoreInputStream.close();
			}
			if(certStoreInputStream!=null){
				certStoreInputStream.close();
			}
			if (certificate != null) {
				certificate.free();
			}
			if(certStore!=null){
				certStore.free();
			}
			certificate = null;
		}
	}
	

	/**
	 * [未正式写入文档-暂时不提倡使用]
	 * 验签，使用PFX软证书
	 * @param certKeyStorePath	软证书jks或者pfx路径
	 * @param keyStoreType		keyStore类型
	 * @param source			原文
	 * @return
	 * XXX_KEYSTORE_TYPE_JKS=""
	 * KEYSTORE_TYPE_PFX=""
	 */
	@SuppressWarnings("unused")
	public static Certificate verifyByPfx(String certKeyStorePath, String keyStoreType, String alias, String storePassword, String keyPassword, String signValue, String source)
			throws PkiException, KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
		
		if (isEmpty(certKeyStorePath)) {
			throw new PkiException("在使用软证书进行验签中，软证书的值为空");
		}
		if (isEmpty(signValue)) {
			throw new PkiException("在使用软证书进行验签中，待加密的加密值为空");
		}
		
		Signature signature = null;
		InputStream inputStream = null;
		KeyPair keypair = null;
		KeyStore keystore = null;
		Certificate certificate = null;
	
		try {
			inputStream = new FileInputStream(certKeyStorePath);
			
			keystore = KeyStore.getInstance(keyStoreType);
			keystore.load(inputStream, storePassword.toCharArray()); 
			keypair = Util.getKeyPairFromKeyStore(keystore, alias, keyPassword);
			if (keypair == null) {
				throw new PkiException("软证书验签不正确，获取的密钥对为空");
			}
			//公钥验签
			signature = new Signature(NETCAPKI_ALGORITHM_RSASIGN, keypair.getPublicKey());
			signature.update(convertByte(source)); 
			boolean verifySuccess = signature.verify(base64Decode(signValue));
			if(verifySuccess){
				java.security.cert.Certificate cert = keystore.getCertificate(alias);
				certificate = new Certificate((X509Certificate)cert);
				if(certificate==null){
					throw new PkiException("软证书验签不正确，获取的证书为空");
				}
				return certificate;
			}else{
				throw new PkiException("软证书验签不通过，验签失败");
			}
			
		} catch (PkiException e) {
			throw new PkiException("软证书验签过程中出错："+e.getMessage());
		} catch (KeyStoreException e) {
			throw new KeyStoreException("软证书验签，密钥库获取实例失败："+e.getMessage());
		} catch (IOException e) {
			throw new IOException("软证书验签，密钥库加载失败，输入流出错："+e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			throw new NoSuchAlgorithmException("软证书验签，密钥库加载失败，没有对应的加密算法"+e.getMessage());
		} catch (CertificateException e) {
			throw new CertificateException("软证书验签，密钥库加载失败："+e.getMessage());
		} finally {
			if (keypair != null) {
				keypair.free();
			}
			if(signature!=null){
				signature.free();
			}
			if(inputStream!=null){
				try {
					inputStream.close();
				} catch (IOException e) {
					throw new IOException("数字信封解密，使用PFX软证书过程中，关闭输入流失败");
				}
			}
			keypair = null;
			inputStream = null;
			signature = null;
		}
	}
	
	/**
	 * [未正式写入文档-暂时不提倡使用]
	 * 验签，使用PFX软证书
	 * @param certKeyStorePath	软证书jks或者pfx路径
	 * @param keyStoreType		keyStore类型
	 * @param source			原文
	 * @return
	 * XXX_KEYSTORE_TYPE_JKS=""
	 * KEYSTORE_TYPE_PFX=""
	 */
	public static Certificate verifyByPfx(String certKeyStorePath, String keyStoreType, String storePassword, String keyPassword, String signValue, String source)
			throws PkiException, KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
		
			String alias = getAliasBySoft(certKeyStorePath, keyStoreType, storePassword);
			return verifyByPfx(certKeyStorePath, keyStoreType, alias, storePassword, keyPassword, signValue, source);
		
	}
	
	/**
	 * [未正式写入文档-暂时不提倡使用]
	 * 通过软证书获取别名
	 * @param certKeyStorePath			软证书路径
	 * @param keyStoreType				keystore类型
	 * @param storePassword				keystore的密码
	 * @return							别名
	 */
	public static String getAliasBySoft(String certKeyStorePath, String keyStoreType, String storePassword) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		InputStream inputStream = null;
		KeyStore keystore = null;
		String alias = null;
		try {
			inputStream = new FileInputStream(certKeyStorePath);
			
			keystore = KeyStore.getInstance(keyStoreType);
			keystore.load(inputStream, storePassword.toCharArray()); 
			Enumeration<String> enums = keystore.aliases();
			if(enums.hasMoreElements()){
				alias = enums.nextElement();
				System.out.println(alias);
			}
		}catch(IOException io){
			throw new IOException("获取软证书别名为空");
		}
		return alias;
	}
	
	/**
	 * 获取key设备中的签章图片
	 * @return
	 * @throws Exception
	 */
	public static byte[] getSignPicture() throws Exception {

		return getSignPicture(null);
	}
	
	/**
	 * 通过当前的证书，获取key设备中的签章图片
	 * @param certiData		当前证书的二进制数据，如果传入null，则指定为第一个设备
	 * @return
	 * @throws Exception
	 */
	public static byte[] getSignPicture(byte[] certiData) throws Exception {

		DeviceSet set = null;
		Device device = null;
		Certificate certificate = null;
		KeyPair keyPair = null;
		try {
			set = new DeviceSet(Device.ANY_DEVICE, Device.DEVICE_FLAG_SILENT);
			certificate = new Certificate(certiData);
			
			int count = set.count();
			if (count == 0) {
				throw new Exception("请插入绑定签章图片的设备");
			}
			if(certificate==null||certiData==null||certiData.length==0){
				if (count != 1) {
					throw new Exception("只能插入一个设备");
				}
				device = set.get(0);
			}else{
				boolean findDevice = false;
				String certSN = certificate.getSerialNumber();
				for(int i = 0;i<set.count();i++){
					device = set.get(i);
					net.netca.pki.global.Device device2 = new NetcaDevice(device);		//转为pure JAVA，在2.2.2的jar版本没有直接获取证书的方法
					NetcaX509Certificate tmpCert = null;
					List list = device2.getAllCertificates();
					try {
						for(int index = 0;index<list.size();index++){
							tmpCert = (NetcaX509Certificate) list.get(index);
							if(tmpCert.getSerialNumber().equals(certSN)){
								findDevice = true;
								break;
							}
						}
					} finally {
						if(list!=null){
							for(int index = 0;index<list.size();index++){
								if(list.get(index)!=null){
									((NetcaX509Certificate)list.get(index)).free();
								}
							}
						}
						if (tmpCert!=null) {
							tmpCert.free();
						}
						if(findDevice){
							break;
						}
					}
				}
				if (!findDevice) {
					throw new Exception("当前证书与设备没有签章图片！");
				}
				
			}

			// 去id=8127的数据区解析出签章图片数据
			int length = device.getDataLength(SIGNPICTUREID);
			byte[] data = new byte[length];
			device.readData(SIGNPICTUREID, data, 0, length);

			String strData = new String(data, NETCAPKI_CP);
			//String[] imgs = strData.split("\\|\\|");
			// 取出存放签章图片的具体位置ID (默认取第一张（假设存在多张的话）)
			int imgId = -1;
			for( String temp : SIGNPICTUREIDS) {
				if(strData.contains(temp)){
					imgId = Integer.parseInt(temp);
					break;
				}
			}
			
			// 去存放签章图片的数据块读取数据
			int imgLength = device.getDataLength(imgId);
			byte[] result = new byte[imgLength];
			device.readData(imgId, result, 0, imgLength);

			return result;

		} catch (Exception e) {

			throw new Exception("获取签章图片失败: " + e.getMessage());
		} finally {
			if (set != null) {
				set.free();
			}
			if(keyPair!=null){
				keyPair.free();
			}
			if (device != null) {
				device.free();
			}
			if(certificate!=null){
				certificate.free();
			}
		}
	}
		
	/**
	 * 修改JKS证书口令ChangeJKSPassWd
	 * @param oldPassWd	旧口令
	 * @param newPassWd	新口令
	 * @return
	 * @throws PkiException
	 */
	public static boolean ChangeJKSPassWd(String oldPassWd, String newPassWd) throws PkiException{
		if(isEmpty(oldPassWd)){
			throw new PkiException("请输入正确的加密证书(含私钥)的旧密码");
		}
		if(isEmpty(newPassWd)){
			throw new PkiException("请输入正确的加密证书(含私钥)的新密码");
		}
		
		String envjks = null;
		File f = null;
		FileInputStream in = null;
		KeyStore ks = null;
		char[] cOldPin = null;
		try{
			envjks = NetcaPKI.getAProperty("envjks");
			f = new File(envjks);
			ks = KeyStore.getInstance(KeyStore.getDefaultType());//"JKS"
			in = new FileInputStream(f);
			cOldPin = oldPassWd.toCharArray();
			ks.load(in, cOldPin);
		}catch(IOException e){
			if(e.getMessage() != null && e.getMessage().indexOf("password was incorrect") != -1){
				throw new PkiException("请输入正确的加密证书(含私钥)的旧密码" + e.getMessage());
			}
			
		}catch (Exception e) {
		 	String errMsg = "配置文件设置:读取加密证书(含私钥)文件失败";
		 	errMsg += "加密证书文件(含私钥)路径为" + envjks + "。";
		 	throw new PkiException("配置文件设置:读取加密证书(含私钥)文件失败" + e.getMessage());
		}finally{
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {}
			}
		}
		
		//Store 新PIN
		FileOutputStream out = null;
		try{
			char[] cNewPin = newPassWd.toCharArray();
			Enumeration enu = ks.aliases();
			String ali= null;
			while(enu.hasMoreElements())
			{
				ali = (String) enu.nextElement();
				X509Certificate tmpcer= (X509Certificate)ks.getCertificate(ali);
				X509Certificate[] chain= new X509Certificate[1];
				chain[0] = tmpcer;
				Key key= ks.getKey(ali, cOldPin);
				//此行用于删除，此处不需要 m_ks.deleteEntry(ali);
				if(key != null){
					ks.setKeyEntry(ali, key, cNewPin, chain);
				}else{
					ks.setCertificateEntry(ali, tmpcer);
				}
			}
			out = new FileOutputStream(f);
			ks.store(out, cNewPin);
		}catch (Exception e) {
			e.printStackTrace();
			throw new PkiException("修改加密证书(含私钥)的密码失败" + e.getMessage());
		}finally{
			if(out != null){
				try {
					out.close();
				} catch (IOException e) {}
			}
		}
		
		return true;
	}

	/**
	 * 获取配置文件中的加密证书。证书信息的字节数组。
	 * @return 证书信息的字节数组
	 * @throws PkiException 
	 */
	public static byte[] getEnvCert() throws PkiException{
		String envcer = null;
		byte[] benvcer = null;
		try {
			envcer = getAProperty("envcer");
			benvcer = readFile(envcer);
		} catch (Exception e) {
			String errMsg = "配置文件设置:读取加密证书(cer)文件失败";
		 	errMsg += "加密证书(cer)文件路径文件名为" + envcer + "。";
		 	throw new PkiException("配置文件设置:读取加密证书(cer)文件失败" + e.getMessage());
		}
		return benvcer;
	}

	/** NetcaPKI
	 * 对称解密
	 * @param hexKeyIV		kev与iv的hex编码
	 * @param data			加解密的数据，加密时为原文，解密时为加密值
	 * @param enc			是否问加密，是：加密，否：解密
	 * @return				加解密值
	 * @throws Exception
	 */
	public static byte[] cbcSM4Encrypt(String hexKeyIV, byte[] data, boolean enc) throws Exception {
		
		byte[] update = new byte[0];
		byte[] doFinal = new byte[0];
		byte[] result = new byte[0];
		int algo=Cipher.SM4_CBC;
        byte[] keyIV = Util.HexDecode(hexKeyIV);
        int len = keyIV.length;
        byte[] key = new byte[len/2];
        byte[] iv = new byte[len/2];
        
        if(keyIV.length!=32){
        	throw new Exception("解开的密钥格式有误");
        }else{
        	System.arraycopy(keyIV, 0, key, 0, len/2);
        	System.arraycopy(keyIV, len/2, iv, 0, len/2);
        }
        
        Cipher cipher=new Cipher(algo);
       
        try {
            cipher.setKey(key);
            cipher.setIV(iv);
            cipher.setPadding(Cipher.PADDING_PKCS5);  //PADDING_PKCS5
            cipher.init(enc);
            update=cipher.update(data);
            doFinal=cipher.doFinal();
            
        } catch (Exception e) {
			e.printStackTrace();
			System.out.println("加解密失败："+e.getMessage());
		} finally {
            cipher.free();
        }
        
        result = new byte[update.length + doFinal.length];
        System.arraycopy(update, 0, result, 0, update.length);
    	System.arraycopy(doFinal, 0, result, update.length, doFinal.length);
        
        return result;
	}	

}
