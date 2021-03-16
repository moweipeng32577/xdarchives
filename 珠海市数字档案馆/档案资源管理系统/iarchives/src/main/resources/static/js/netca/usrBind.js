var certValue;

function bindUserCert(){
	$.ajax({
		type : "post",
		dataType : "json",
		async : false,
		data : {"cert":certValue}, 
		url : "UserBindCert.servlet",
		// 对于异步请求，是处理完“主干”的js代码后，才会执行success的，故在success前修改汉tempHandlerObjs即可
		success : function(doc) {
			$("#showMessageP").html(doc.responseText);   
		},
		error : function(error) { 
			$("#showMessageP").html(error.responseText);  
		}
	});
}

function showUser(){
	
	$.ajax({
		type : "post",
		dataType : "json",
		async : false,
		data : {"cert":certValue}, 
		url : "UserBindCert.servlet",
		// 对于异步请求，是处理完“主干”的js代码后，才会执行success的，故在success前修改汉tempHandlerObjs即可
		success : function(doc) {
			$("#showMessageP").html(doc.responseText);   
		},
		error : function(error) { 
			$("#showMessageP").html(error.responseText);  
		}
	});
	$("#certBind").hide();
	$("#userBind").show();
}

function showResult(){
	$("#userBind").hide();
	$("#userBindCert").show();
}

 

function successGetCertStringAttributeCallBack(res,form) {
	certValue=res.certCode;
    /*$("#bindvalue").val(res.AppUsrCertNO);
    $("#certsubject").val(res.subjectCN);*/
    //alert(res.AppUsrCertNO);
    //设置登录账号和密码，提交表单
   /* $(".username").val(res.AppUsrCertNO);
    $(".password").val(res.subjectCN);*/
   	var sysChoose=$("#sysChoose").val();//选择的登录系统
   	if(sysChoose!==''){
        sysChoose='&'+sysChoose;
	}else{//默认登录管理平台 
        sysChoose='&1';
	}
    $("input[name='username']").val(res.subjectCN+sysChoose);
    $("input[name='password']").val(res.AppUsrCertNO+'&wztca');
    form.submit();
}

function failedGetCertStringAttributeCallBack(res) {

    alert(res.msg);
}
//步骤1：获取用户证书
function getUserCert(form) {

    var selectType = "{\"UIFlag\":\"default\", \"InValidity\":true,\"Type\":\"signature\", \"Method\":\"device\",\"Value\":\"any\"}";
    var selectCondition = "IssuerCN~'NETCA' && InValidity='True' && CertType='Signature'";
  
    netca_getCertStringAttribute(null, selectType, selectCondition, -1, successGetCertStringAttributeCallBack,
    failedGetCertStringAttributeCallBack,form);
}
function bindCert() {
	if (certValue != null)
	{
		bindUserCert();
		showResult();
	}
	else
		alert("请先获取证书信息！");
}