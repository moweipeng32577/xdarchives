package com.wisdom.util.netca;

import net.netca.pki.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Value;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.HashMap;

/**
 * 说明：
 * CertAuthClient类是按照《NETCA PKI Crypto Java开发接口开发规范》文档定义的接口提供 网关验证相关的实现代码。
 * 
 * 作者：NETCA开发Java团队
 * 日期：2017-07-05
 * 版权： NETCA
 */
public class CertAuthClient {


	/**==================================================================
	 * 2.网关验证
	 * ==================================================================
	 */ 
	private static long seq = 1;
	/*private static final String WEBSERVICEURL = "WebServiceURL";
	private static final String SMETHODONESERVERURL = "sMethodOneServerUrl";	//sMethodOneServerUrl  WebServiceURL
	private static final String SMETHODTWOSERVERURL = "sMethodTwoServerUrl";
	private static final String SDEFAULTSERVERCERT = "sDefaultServerCert";*/
	private static final String OCSPURL = "OCSPUrl";

	
	/**
	 * 解析证书验证状态码
	 * 
	 * @param certCode 	证书状态码
	 * @return 			证书状态码文字解析
	 */
	public static String parseCertCode(int certCode) {

		switch (certCode) {
		case 0:
			return "证书有效";
		case 1:
			return "验证处理失败";
		case 2:
			return "证书格式有误";
		case 3:
			return "证书不在有效期内";
		case 4:
			return "密钥用途不合(KU和EKU)";
		case 5:
			return "证书名字不合(DN和SAN)";
		case 6:
			return "证书策略不合(取决于CVS的配置)";
		case 7:
			return "证书扩展不合(取决于CVS的配置)";
		case 8:
			return "证书链验证失败(包括了签名检查；基本约束检查；名字约束检查；策略约束检查；ACL检查等)";
		case 9:
			return "证书被注销";
		case 10:
			return "注销状态不能确定(如果设置了已过期的CRL或OCSP无法连接)";
		case 11:
			return "证书不受信任/未被授权";
		case 12:
			return "证书被暂时锁定/未激活";
		default:
			return null;
		}
	}
	

	/**
	 * 网关验证证书
	 * 
	 * @param certificate 需要验证的证书
	 * @param verifytime 证书验证的时间
	 * @param ku 证书密钥用法
	 * @return 返回一个存储证书状态码、签名值、摘要等信息的map
	 * @throws Exception
	 * 
	 * 注：返回的是version、signature、verifytime、digest、certName、status等
	 */
	public static HashMap<String, String> verifyCertEx(Certificate certificate, String verifytime, int ku,String SDEFAULTSERVERCERT, String SMETHODONESERVERURL) throws Exception {

		if (certificate == null) {
			throw new PkiException("找不到电子签名的数字证书，请运行证书安全客户端软件，并插入一个证书介质Key!");
		}
		String digest = null;
		String params = null;
		String responseXML = null;
		String strUrl = null;
		Certificate serverCertificate = null;
		//String base64Cert = NetcaPKI.getAProperty(SDEFAULTSERVERCERT);
		String base64Cert = SDEFAULTSERVERCERT;
		try {
			serverCertificate = NetcaPKI.getX509Certificate(base64Cert);
			//通过配置文件获取 验证网关接口地址，URL
			//strUrl = NetcaPKI.getAProperty(SMETHODONESERVERURL);
			strUrl = SMETHODONESERVERURL;
			//发送特定格式的参数，返回响应的数据
			params = String.format("verifytime=%s&b64cert=%s&ku=%x&ekuoid=%s&namecheck=%s", verifytime,
					URLEncoder.encode(certificate.pemEncode(), "UTF-8"), ku, "", "");
			responseXML = getResponse(params, strUrl, "application/x-www-form-urlencoded; charset=utf-8"); 
			HashMap<String, String> hashMap = getXMLContent(responseXML);
			
			//对比摘要值，是否被篡改
			digest = Util.HexEncode(true, certificate.computeThumbprint(Hash.SHA1));		//网关的哈希算法为SHA1
			int index1 = responseXML.lastIndexOf("<data>");
			int index2 = responseXML.lastIndexOf("</data>");
			if (hashMap.get("digest") == null || !hashMap.get("digest").equalsIgnoreCase(digest)){
				throw new PkiException("被验证的证书摘要不匹配,可能遭到恶意攻击");
			}
			if (index1 < 0 || index2 < 0) {
				throw new PkiException("服务端返回数据包有误");
			}
			
			//验证签名值
			String content = responseXML.substring(index1, index2 + 7);
			String head = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
			content = head + content;
			String signValue = NetcaPKI.hexToBase64(hashMap.get("signature"));
			boolean verifySuccess = verifyData(serverCertificate, content, signValue);
			if (!verifySuccess) {
				throw new PkiException("服务端签名无效");
			}
			
			//验证交易流水号
			String strSeq = hashMap.get("version");
			seq = 1;
			if (Long.valueOf(Double.valueOf(strSeq).longValue()) != seq) {
				throw new PkiException("交易流水号不匹配,可能遭到恶意攻击");
			}
			return hashMap;
		} catch (PkiException e) {
			throw new PkiException("网关验证证书出错："+e.getMessage());
		} catch (IOException e) {
			throw new IOException("网关验证证书，"+e.getMessage());
		} catch (DocumentException e) {
			throw new DocumentException("网关验证证书出错："+e.getMessage());
		} catch(NumberFormatException e){
			throw new NumberFormatException("网关验证证书出错，交易流水号格式有问题："+e.getMessage());
		}catch (Exception e) {
			throw new Exception("网关验证证书出错："+e.getMessage());
		}finally {
			if(serverCertificate!=null){
				serverCertificate.free();
			}
			serverCertificate = null;
		}
	}


	/**
	 * 通过WebService服务进行网关验证证书有效性(某个时间)
	 *
	 * @param certificate 被验证证书
	 * @param verifytime 证书验证时间
	 * @param ku 证书密钥用法
	 * @return 返回一个存储了 数据的证书状态码、签名值、摘要等信息的map
	 * @throws Exception
	 */
	public static HashMap<String, String> verifyCertByWebService(Certificate certificate, String verifytime,
																 int ku, String WEBSERVICEURL) throws Exception {

		if(certificate==null) {
			throw new PkiException("找不到解密的数字证书，请运行证书安全客户端软件，并插入一个正确证书介质Key!");
		}
		HashMap<String, String> hashMap = new HashMap<String, String>();
		URL wsdlLocation = null;
		NetcaCertAA netcaCertAA = null;
		//String uRLPath = NetcaPKI.getAProperty(WEBSERVICEURL);
		String uRLPath = WEBSERVICEURL;
		try {
			wsdlLocation = new URL(uRLPath);
			netcaCertAA = new NetcaCertAA(wsdlLocation);	//wsdlLocation
			WebSevIntfImplDelegate nCertAA = netcaCertAA.getNetcaCertAAWS();
			String ret = nCertAA.verifyUserCert(verifytime, certificate.pemEncode(), Integer.valueOf(ku), null, null);
			hashMap = getXMLContent(ret);
			return hashMap;
		} catch(NullPointerException e){
			throw new DocumentException("找不到电子签名的数字证书，请运行证书安全客户端软件，并插入一个证书介质Key!");
		} catch (DocumentException e) {
			throw new DocumentException("WebService服务进行网关验证证书有效性，解析xml文件失败，请查看编码配置是否有错");
		} catch (Exception e1) {
			throw new Exception("e:"+e1.getMessage());
		}
	}

	/**
	 * 获取字符编码方式
	 * 
	 * @param httpconnect  url连接
	 * @return 返回url下对应的字符编码方式
	 */
	private static String getHttpCharSet(HttpURLConnection httpconnect) {
		String charset = NetcaPKI.NETCAPKI_CP;//默认为"utf-8";
		String contype = httpconnect.getContentType();
		if (contype != null) {
			int p1 = contype.indexOf("charset=");
			if (p1 >= 0) {
				p1 += "charset=".length();
				int p2 = contype.indexOf(';', p1);
				if (p2 > 0){
					charset = contype.substring(p1, p2);
				}else{
					charset = contype.substring(p1);
				}
			}
		}
		return charset;
	}

	/**
	 * 通过返回xml形式字符串，解析相关的内容
	 * 
	 * @param ret xml形式字符串
	 * @return 返回解析出来的 证书状态码、签名值、摘要等信息的map
	 * @throws DocumentException
	 * @throws PkiException
	 */
	private static HashMap<String, String> getXMLContent(String ret) throws DocumentException, PkiException {

		HashMap<String, String> hashMap = new HashMap<String, String>();
		SAXReader saxReader = new SAXReader();
		InputStream inputStream = null;

		Document document = null;
		try {
			inputStream = new ByteArrayInputStream(NetcaPKI.convertByte(ret));
			document = saxReader.read(inputStream);
		} catch(PkiException e){
			throw new DocumentException("读取xml文件不正确：转字节数组有误");
		} catch (DocumentException e) {
			throw new DocumentException("读取xml文件不正确："+e.getMessage());
		}
		Element root = document.getRootElement();
		hashMap.put("version", root.element("version").getText());
		hashMap.put("signature", root.element("signature").getText());
		hashMap.put("verifytime", root.element("data").element("verifytime").getText());
		hashMap.put("digest", root.element("data").element("certsha1hex").getText());
		hashMap.put("certName", root.element("data").element("certname").getText());
		hashMap.put("status", root.element("data").element("status").getText());
		return hashMap;
	}
	
	/**
	 * 解析响应内容，返回对应的hashMap
	 * 
	 * @param response 需要解析的字符串
	 * @return 返回解析出来的 证书状态码、签名值、摘要等信息的map
	 */
	@SuppressWarnings("unused")
	private static HashMap<String, String> getResponseContent(String response) {

		String[] strs = response.split("\\|");
		HashMap<String, String> hashMap = new HashMap<String, String>();

		hashMap.put("version", strs[0]);
		hashMap.put("verifytime", strs[1]);
		hashMap.put("digest", strs[2]);
		hashMap.put("status", strs[3]);
		hashMap.put("signature", strs[4]);

		return hashMap;
	}

	/**
	 * 通过一个请求，进行web响应，返回响应的结果
	 * 
	 * @param request 请求
	 * @param strUrl 提交请求的URL
	 * @param propertyValue 响应网页的格式，如text/paint
	 * @return 返回响应的结果
	 * @throws IOException
	 */
	private static String getResponse(String request, String strUrl, String propertyValue) throws Exception {

		URL url = null;
		HttpURLConnection httpsURLConnection = null;
		try {
			url = new URL(strUrl);
			httpsURLConnection = (HttpURLConnection) url.openConnection();
			httpsURLConnection.setRequestMethod("POST"); 
			httpsURLConnection.setRequestProperty("Content-type", propertyValue);
			httpsURLConnection.setDoOutput(true);
			httpsURLConnection.connect(); 
			httpsURLConnection.getOutputStream().write(NetcaPKI.convertByte(request)); 

			int status = httpsURLConnection.getResponseCode();
			if (status != HttpURLConnection.HTTP_OK) {
				httpsURLConnection.disconnect();
				throw new Exception("http通信错误，状态码："+status);
			}

			int len = httpsURLConnection.getContentLength();
			String charSet = getHttpCharSet(httpsURLConnection);
			byte[] response = new byte[len];
			int readLen = httpsURLConnection.getInputStream().read(response); 
			while (readLen < len) {
				int count = httpsURLConnection.getInputStream().read(response, readLen, len - readLen); 
				if (count < 0){
					break;
				}
				readLen += count;
			}
			httpsURLConnection.disconnect();
			return new String(response, charSet);
		} catch (MalformedURLException e) {
			throw new MalformedURLException("web响应失效，获得的URL是个无效的地址");
		} catch (ProtocolException e) {
			throw new ProtocolException("web响应失效，通过 HTTP POST请求从服务器请求数据失败："+e.getMessage());
		} catch (IOException e) {
			throw new IOException("web响应失效，HTTP读写对应的响应内容失败："+e.getMessage());
		} catch (PkiException e) {
			throw new IOException("web响应失效，HTTP读写对应的响应内容失败："+e.getMessage());
		}
	}

	/**
	 * 通过证书验证签名值与原文(只适用于网关上)
	 * 
	 * @param certificate 需要验证的证书
	 * @param content 原文
	 * @param signValue 签名值
	 * @return 通过证书验证签名值，返回原文是否被篡改，如果是，则false，反正为true
	 * @throws PkiException
	 * 网关使用到的算法为SHA1WITHRSA
	 */
	private static boolean verifyData(Certificate certificate, String content, String signValue) throws PkiException {
		
		int algo = 0;
		byte[] signValueArray = null;
		Signature signature = null;

		try {
			signValueArray = Base64.decode(Base64.ENCODE_NO_NL, signValue);
			
			algo = Signature.SHA1WITHRSA;		//网关
			signature = new Signature(algo, certificate.getPublicKey(Certificate.PURPOSE_SIGN));
			signature.update(NetcaPKI.convertByte(content));
			return signature.verify(signValueArray);
		} catch (PkiException e) {
			throw new PkiException("PCK1验证异常发生："+e.getMessage());
		} finally {
			if (signature != null){
				signature.free();
			}
			signature = null;
		}
	}
	
}
