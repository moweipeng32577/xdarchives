var g_ticket;

function InsertCallBack(res,form)
{
  
    //当前插入key 可以开始登陆签名
    loginByCertEvent(form);
}


function UnInsertCallBack(res)
{
 
    alert("未找到证书介质，请插入一个数字证书介质KEY");
 
}


function loginByCert(form) {

	//检测客户端版本
    netca_getClientVersionInfo(SuccessGetVersionInfoCallBack, FailedGetVersionInfoCallBack,form);
    //netca_getVersionInfo(SuccessGetVersionInfoCallBack,FailedGetVersionInfoCallBack);
  
}

function SuccessGetVersionInfoCallBack(res,form)
{
	var versionInfo=res.VersionInfo;
	var version =res.Version;
	//alert("网证通相关驱动已经安装\n版本号为："+version+"\n版本号信息为："+versionInfo+"\n版本UID为："+res.UID);
    netca_isInsertKey("", "", "", InsertCallBack, UnInsertCallBack,form);
}

function FailedGetVersionInfoCallBack(res)
{
	alert("请先安装网证通相关驱动"+res.msg);
}

function SuccessSignedDataSignCallBack(res,form)
{

    var extendVerify = "none";
    var none = $("#none").prop("checked");
    var verfiyCert = $("#verifyCert").prop("checked");
    var ocspVerfiyCert = $("#ocspVerfiyCert").prop("checked");
    var checkCert = $("#checkCert").prop("checked");

    var signValue = res.signValue;
    //frmLogin.signature.value = signValue;

    

   /* if (verfiyCert == true) {					//还需要网关验证

        extendVerify = "verifyCert";

    } else if (ocspVerfiyCert == true) {		//需要OCSP验证

        extendVerify = "ocspVerfiyCert";

    } else if (checkCert == true) {
        extendVerify = "checkCert";
    }*/
    extendVerify = "verifyCert";


    //alert("步骤3：签名结果提交后台");
    $.ajax({
        type: "POST",
        dataType: "json",
        async: false,
        timeout : 100000,
        data: { signValue: signValue, source: g_ticket, extendVerify: extendVerify },
        //url: "CertLoginVerify.servlet",
        url:"/netca/getCertLoginVerify",
        // 对于异步请求，是处理完“主干”的js代码后，才会执行success的，故在success前修改汉tempHandlerObjs即可
        success: function (doc) {
            /*$("#showMessageP").html(doc.responseText);
            $("#loadBeford").hide();*/
            //frmLogin.showMessage.value = doc.responseText;
            $("input[name='username']").val(res.certObject.subjectCN);
            $("input[name='password']").val(res.certObject.AppUsrCertNO+'&wztca');
            form.submit();
        },
        error: function (error) {
            /*$("#loadBeford").hide();
            $("#showMessageP").html(error.responseText);*/
            //frmLogin.showMessage.value = error.responseText;
            alert("网关验证失败！");
        }
    });
}
 

function  FailedSignedDataSignCallBack(res)
{
    alert(res.msg);	
}



//步骤2：前端对随机数进行签名，并把签名结果发送到服务端
function loginByCertEvent(form) {
	
	
	/* 如果需要后台进行证书+网关验证 */

	 try {
      
	    g_ticket = getRandomByServer(5);
		//alert("步骤1：服务端已产生随机数！");
		

		//alert("步骤2：对随机数进行签名！");
		//var certID = document.getElementById("certname").value;
	     //var signValue = signedDataByCertificate(oCert[certID], ticket, false, "");

		var certEncode = "";
 
		var type = "{\"UIFlag\":\"default\", \"InValidity\":true,\"Type\":\"signature\", \"Method\":\"device\",\"Value\":\"any\"}";
		var condition = "IssuerCN~'NETCA' && InValidity='True' && CertType='Signature'";
		var tbs = utf8_to_b64(g_ticket);
		var useSubjectKeyId = false;   
		var useQ7 = false;
		var detached = false;
		var tsaURL = "";
		var includeCertOption = 2;
		netca_signedDataSign(certEncode, type, condition, tbs, useSubjectKeyId, useQ7, tsaURL, detached, includeCertOption, "", SuccessSignedDataSignCallBack, FailedSignedDataSignCallBack,form);
        //带原文签名
		
	} catch (e) {
		alert("登录失败!" + e.description);
		return null;
	}
}

/**
 * 后台产生随机数 2018年2月12日
 * 
 * @returns 
 */
function getRandomByServer(number){
	
	var random = " ";
	/*$.ajax({
		type : "post",
		dataType : "html",
		async : false,
		data : {"number" : number}, 
		url : "CetRandomByServer.servlet",
		// 对于异步请求，是处理完“主干”的js代码后，才会执行success的，故在success前修改汉tempHandlerObjs即可
		success : function(doc) {
			random = doc;
		},
		error : function(error) {
			alert("后台产生随机数失败：" + error);
		}
	});*/
    var ran=Math.random();
    $.ajax({
        type:'POST',
        url:"/netca/getRandomByServer?number="+number+"&t="+ran,
        //data:{number : number},
        async:false,
        // contentType:"application/json",
        dataType: 'json',
        success:function(data) {
            random = data;
        },
        error:function(data){
            alert("后台产生随机数失败：" + data);
        }
    });

	return random;
}