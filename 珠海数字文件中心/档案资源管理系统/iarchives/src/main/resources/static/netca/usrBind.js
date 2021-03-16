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
	form.down('[itemId=caUserid]').setValue(res.AppUsrCertNO);
	form.down('[itemId=caUsername]').setValue(res.subjectCN);
	form.down('[itemId=cacodeid]').setValue(certValue);
	//获取签章信息
    netca_getSealImage(certValue, successGetUserSealImageCallBack, failedGetUserSealImageCallBack,form)
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

function successGetUserSealImageCallBack(res,form) {
    /*document.getElementById("signatureCreator_sealImageEncode").value=res.sealImageBase64;
    document.getElementById("signatureCreator_sealImageEncodeEx").value=res.sealImageBase64;*/
    form.down('[itemId=signcodeid]').setValue(res.sealImageBase64);
}
function failedGetUserSealImageCallBack(res) {
    alert(res.msg);
}