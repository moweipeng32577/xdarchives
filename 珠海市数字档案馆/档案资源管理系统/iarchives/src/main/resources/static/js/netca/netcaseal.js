
/* 
    Netca电子签章模块(V1.3.0)
    版本 V1.0.0
        提供基础的电子签章接口    

    版本 V1.2.0  2019-10-07
        提供关键字的印章接口
*/

NetcaPKI.getSealClientVersion = function (params) {
    var requestQueryParams = {};
    requestQueryParams["appName"] = "SignatureCreator";
    requestQueryParams["function"] = "GetSealClientVersion";
    requestQueryParams["param"] = params;
    return NetcaPKI.SendNetcaCryptoJsonRpcMessage(requestQueryParams);
}
	
NetcaPKI.getNetcaSealImage=function(params){		
    var requestQueryParams = {};
    requestQueryParams["function"] ="GetNetcaSealImage";
    requestQueryParams["param"] = params;
    return NetcaPKI.SendNetcaCryptoJsonRpcMessage(requestQueryParams);		
}

NetcaPKI.GetNetcaSealImage=function(params){		
    return NetcaPKI.getNetcaSealImage(params);
}

NetcaPKI.SignatureCreatorPdfSignSealFieldOrPosition = function(params)
{
    var requestQueryParams = {};
    requestQueryParams["appName"] = "SignatureCreator";
    requestQueryParams["function"] = "SignatureCreatorSignSeal";
    requestQueryParams["param"] = params;
    return NetcaPKI.SendNetcaCryptoJsonRpcMessage(requestQueryParams);
}
	
NetcaPKI.signatureCreatorSignSeal = function(params)
{
    var requestQueryParams = {};
    requestQueryParams["appName"] = "SignatureCreator";
    requestQueryParams["function"] = "SignatureCreatorSignSeal";
    requestQueryParams["param"] = params;
    return NetcaPKI.SendNetcaCryptoJsonRpcMessage(requestQueryParams);
}

NetcaPKI.SignatureCreatorPdfSignSealFieldOrPositionEx = function(params)
{
    var requestQueryParams = {};
    requestQueryParams["appName"] = "SignatureCreator";
    requestQueryParams["function"] = "SignatureCreatorSignSealEx";
    requestQueryParams["param"] = params;
    return NetcaPKI.SendNetcaCryptoJsonRpcMessage(requestQueryParams);
}

NetcaPKI.signatureCreatorSignSealEx = function(params)
{
    var requestQueryParams = {};
    requestQueryParams["appName"] = "SignatureCreator";
    requestQueryParams["function"] = "SignatureCreatorSignSealEx";
    requestQueryParams["param"] = params;
    return NetcaPKI.SendNetcaCryptoJsonRpcMessage(requestQueryParams);
}

NetcaPKI.SignatureCreatorSignSealEx = function(params)
{
    return NetcaPKI.signatureCreatorSignSealEx(params)
}

NetcaPKI.signatureVerifierVerifyPDF = function (params) {
    var requestQueryParams = {};
    requestQueryParams["appName"] = "SignatureCreator";
    requestQueryParams["function"] = "SignatureVerifierVerifyPDF";
    requestQueryParams["param"] = params;
    return NetcaPKI.SendNetcaCryptoJsonRpcMessage(requestQueryParams);
}

NetcaPKI.signatureVerifierUndoPDF = function (params) {
    var requestQueryParams = {};
    requestQueryParams["appName"] = "SignatureCreator";
    requestQueryParams["function"] = "SignatureVerifierUndoPDF";
    requestQueryParams["param"] = params;
    return NetcaPKI.SendNetcaCryptoJsonRpcMessage(requestQueryParams);
}
	
NetcaPKI.Custom_PdfSignAndUpload=function(params){		
	var requestQueryParams = {};
    requestQueryParams["function"] ="Custom_PdfSignAndUploadByBytes";
    requestQueryParams["param"] = params;
    return NetcaPKI.SendNetcaCryptoJsonRpcMessage(requestQueryParams);		
}
	
NetcaPKI.custom_PdfSignAndUploadByBytes=function(params){		
	var requestQueryParams = {};
    requestQueryParams["function"] ="Custom_PdfSignAndUploadByBytes";
    requestQueryParams["param"] = params;
    return NetcaPKI.SendNetcaCryptoJsonRpcMessage(requestQueryParams);		
}
	
NetcaPKI.Custom_PdfSignAndUploadByURL=function(params){		
	var requestQueryParams = {};
    requestQueryParams["function"] ="Custom_PdfSignAndUploadByURL";
    requestQueryParams["param"] = params;
    return NetcaPKI.SendNetcaCryptoJsonRpcMessage(requestQueryParams);		
}
	
NetcaPKI.pdfSignSealAndUploadByURLOrBytes=function(params){		
	var requestQueryParams = {};
    requestQueryParams["appName"] = "SignatureCreator";
    requestQueryParams["function"] ="PdfSignSealAndUploadByURLOrBytes";
    requestQueryParams["param"] = params;
    return NetcaPKI.SendNetcaCryptoJsonRpcMessage(requestQueryParams);		
}