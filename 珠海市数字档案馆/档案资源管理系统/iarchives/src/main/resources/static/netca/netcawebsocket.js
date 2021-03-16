/* 
   NetcaWebsocket 模块(V1.3.0)
   该模块主要提供核心实现和基础的PKI接口，对外入口为NetcaPKI。通过NetcaPKI.xx调用接口
   netca_系列函数为旧的封装函数。
   版本 V1.0.0
        封装了Webscoket连接过程, 模拟 Promise 来处理异步调用过程
        支持 signeddata签名, 数字信封 ,签章,非对称加解密接口
   版本 V1.2.0  2018-09-18
        增加PKCS#1签名验证接口   Sign 和  VerifySignature
   版本 V1.3.0  2019-05-07
        优化判断连接判断接口时间
   版本 V1.4.0  2019-11-01
        增加显示证书信息UI的接口
*/

var NetcaPKI = (function () {
 
  

    "use strict";

    var PKIObject = {};

    var _PkiMgr = null;

    function easyPromise() {
        this.succesCallBack = function (res) {};
        this.failCallBack = function (res) {};
    };

    easyPromise.prototype.Then = function (fun) {
        this.successCallBack = fun;
        return this;
    }

    easyPromise.prototype.Catch = function (fun) {
        this.failCallBack = fun;
        return this;
    }


    function easySynPromise() {
        this.succesCallBackMessage = null;
        this.failCallBackMessage = null;
    };

    easySynPromise.prototype.Then = function (fun) {
        if (fun != null && this.failCallBackMessage == null)
            fun(this.succesCallBackMessage);
        return this;
    }

    easySynPromise.prototype.Catch = function (fun) {
        if (fun != null && this.failCallBackMessage != null)
            fun(this.failCallBackMessage);
        return this;
    }
    easySynPromise.prototype.FireEvent = function (successmsg, errormsg) {
        this.succesCallBackMessage = successmsg;
        this.failCallBackMessage = errormsg;
    }




    function WebSocketManager() {

        //原生的wsObj对象
        this._wsObj = null;
        this._tryConnectIndex = 0;
        this._registerFunctionPromiseMap = {};
        this._tryConnectList = new Array();;
        this._requestIdIndex = 0;
	    this._isWsConnecting=false;

    }


    WebSocketManager.prototype.RegisterPromise = function (promiseObj, params) {
        var messageItem = {};
        var curRequest = ++this._requestIdIndex;
        messageItem.requestId = curRequest;
        messageItem.promiseCallBack = promiseObj;
        messageItem.params = params;
        this._registerFunctionPromiseMap[curRequest] = messageItem;

        //生成请求的数据
        var sendData = JSON.stringify({
            "requestVersion": 1,
            "requestOrigin": "45B45638-A006-4cf1-A298-816B376D867E",
            "requestId": curRequest,
            "requestQuery": params
        });

        return sendData;

    }
    WebSocketManager.prototype.GetValidConnectURL = function () {

        for (var i in this._tryConnectList) {

            var connectInfoItem = this._tryConnectList[i];
            //表示能够连接成功
            if (connectInfoItem.canConnect == 1) {
                return connectInfoItem;
            }
        }

        //没有可成功的连接，继续下个索引处理                 
        if (this._tryConnectIndex >= this._tryConnectList.length) {
            return null;
        } else {
            return this._tryConnectList[this._tryConnectIndex++];
        }

    }
    WebSocketManager.prototype.AddConnectObject = function (obj) {
        this._tryConnectList.push(obj);
    }


    WebSocketManager.prototype.ProcessSuccessMessageCallBack = function (message) {
        //判断是否正常数据
        try {
            var obj = JSON.parse(message);

            if (obj.requestId != null) {
                var messageItem = this._registerFunctionPromiseMap[obj.requestId];
                if (messageItem == null) {
                    //没有办法定位是哪个实例，只是默认不处理该信息
                    alert("调用证书服务发生异常" + e.message);
                    console.log(e + "_registerFunctionPromiseMap  Fail ");

                } else {

                    if (obj.responseResult.status == 0) {
                        if (messageItem.promiseCallBack.successCallBack != null) {
                            messageItem.promiseCallBack.successCallBack(obj.responseEntity);
                        }
                    } else {
                        if (messageItem.promiseCallBack.failCallBack != null) {
                            messageItem.promiseCallBack.failCallBack(obj.responseResult);
                        }
                    }
                }
            }

        } catch (e) {

            //没有办法定位是哪个实例，只是默认不处理该信息
            alert("调用中间件服务发生异常" + e.message);
            console.log(e + "parse JSON Message Fail ");
        }

    }

	WebSocketManager.prototype.ProcessServiceConnectFailCallBack = function (jsonMessage, addMsg) 
	{

		  if (addMsg==null||addMsg=="")
		  {
			 addMsg="数字证书服务连接失败，请确认已安装最新的数字证书驱动并且开启了客户端!";
		  }

		  try {
				    var obj = JSON.parse(jsonMessage);


					var NETCA_ERROR_WEBSOCKETCONNECTFAIL=-11000;
                    var responseResult={};
					responseResult.status=NETCA_ERROR_WEBSOCKETCONNECTFAIL;
                    responseResult.msg=addMsg;

					var retData = JSON.stringify({
						"requestVersion": obj.requestVersion,
						"requestOrigin": obj.requestOrigin,
						"requestId": obj.requestId,
						"responseResult": responseResult
					});


                  this.ProcessSuccessMessageCallBack(retData);
				   

			} catch (e) {

			 
			   alert(addMsg);
		   }
	


	}

    //调用一次获取处理数据发送一次
    WebSocketManager.prototype.WebSocketDoSend = function (jsonMessage) {

        var selfObject = this;


        //使用了单进程处理方式
        //这里不能够使用多个连接对象
        //由于异步的处理，存在连接服务未完成，重复进入这个流程，导致WS报连接中的错误
        //这里应用设置了判断是否连接状态处理
 
        if(selfObject._wsObj != null&&selfObject._isWsConnecting)
        {
			     selfObject.ProcessServiceConnectFailCallBack(jsonMessage,"数字证书服务连接中,请稍后重试!");
                //alert("数字证书服务连接中,请稍后重试!");
                return;
        }


        if (selfObject._wsObj != null && selfObject._wsObj.readyState == 1) {

            selfObject._wsObj.send(jsonMessage);
        } else {


            var connectInfoItem = this.GetValidConnectURL();
            if (connectInfoItem == null) {
                selfObject._tryConnectIndex = 0;
				  selfObject.ProcessServiceConnectFailCallBack(jsonMessage,"数字证书服务连接失败，请确认已安装最新的数字证书驱动并且开启了客户端!");
               // alert("数字证书服务连接失败，请确认已安装最新的数字证书驱动并且开启了客户端!");
                return;
            }


            //初始化连接服务的数据
            var connectConfig = {};
            connectConfig.url = connectInfoItem.url;
            connectConfig.subprotocol = "crypto-jsonrpc-protocol";

            try {



                selfObject._wsObj = null;

                selfObject._isWsConnecting=true;

                //开始连接
                selfObject._wsObj = new WebSocket(connectConfig.url, connectConfig.subprotocol);

                selfObject._wsObj.onopen = function () {
                    
                    selfObject._isWsConnecting=false;
                    //获取队列数据 发送数据
                    connectInfoItem.canConnect = 1;
                    if (selfObject._wsObj != null) {
                        selfObject._wsObj.send(jsonMessage);
                    }
                };

                selfObject._wsObj.onmessage = function (evt) {

                    selfObject.ProcessSuccessMessageCallBack(evt.data);
                };

                selfObject._wsObj.onclose = function () {

                     selfObject._isWsConnecting=false;

                };

                selfObject._wsObj.onerror = function () {

                    //断开这个连接，重新连接、
                     selfObject._isWsConnecting=false;

                    if (selfObject._wsObj != null && selfObject._wsObj.readyState == 1) {

                         selfObject._wsObj.close();
                         selfObject._wsObj = null;

                    } else {
                         selfObject._wsObj = null;
                        //连接不上 会进入这里 或者其他错误
                        if (connectInfoItem.canConnect == 1) {
                            selfObject._tryConnectIndex = 0;
                           //todo 经发现有时候长连接会断开 这里弹框影响用户体验
			
							//alert("连接数字证书服务失败,请刷新页面重试!");
							  selfObject.ProcessServiceConnectFailCallBack(jsonMessage,"数字证书服务连接中,请稍后重试!");
							
                        } else { //其他地址连接试试

                            selfObject.WebSocketDoSend(jsonMessage);
                        }
                    }

                };
            } catch (e) {

                selfObject._isWsConnecting=false;
              //这里连接失败一般就是服务访问不了或者安全拒绝
                console.log(e + "try to connect" + connectConfig._url);
                if (connectInfoItem.canConnect == 1) {
                    selfObject._tryConnectIndex = 0;
					//todo 经发现有时候长连接会断开 这里弹框影响用户体验
					  selfObject.ProcessServiceConnectFailCallBack(jsonMessage,"数字证书服务连接中,请稍后重试!");
                    //alert("连接数字证书服务失败,请刷新页面重试!");
                } else { //其他地址连接试试
                    selfObject.WebSocketDoSend(jsonMessage);
                }

            }

        }

    };



    WebSocketManager.prototype.ProcessSynMessageCallBack = function (message, synpromise) {
        //判断是否正常数据
        try {
            var obj = JSON.parse(message);
            if (obj.responseResult.status == 0) {

                synpromise.FireEvent(obj.responseEntity, null);

            } else {

                synpromise.FireEvent(null, obj.responseResult);
            }

        } catch (e) {

            obj = JSON.parse("{\"responseResult\": {\"status\":-1,\"msg\": \"响应的数据不是有效的JSON数据\"}}");
            //没有办法定位是哪个实例，只是默认不处理该信息
            console.log(e + "parse JSON Message Fail ");
            synpromise.FireEvent(null, obj.responseResult);
        }

    }


    WebSocketManager.prototype.SendRPCMessage = function (params) {
        var retPromise = new easyPromise();
        var sendMsg = this.RegisterPromise(retPromise, params);


        //如果不支持websocket 使用Com方式，只能IE使用，这里不判断是否IE
        if (typeof WebSocket != 'undefined') {

            this.WebSocketDoSend(sendMsg);
            return retPromise;
        } else {

            var comObj = null;

            try {

                comObj = new ActiveXObject("NetcaPki.Utilities");

            } catch (e) {

                alert("创建NetcaPki对象失败!");
                return retPromise;
            }



            var retSynPromise = new easySynPromise();
            var retString = "";
            //加载成功 调用接口
            try {

                retString = comObj.CryptoJsonRpcCallBack(sendMsg);

            } catch (e) {

                if (-2146827850 == e.number) {
                    //对象不支持此属性或方法
                    alert("当前NETCA_Crypto版本可能过低,请确保版本正常");
                    return retPromise;
                }

                alert("调用中间件接口异常." + e);
                return retPromise;

            }

            this.ProcessSynMessageCallBack(retString, retSynPromise);

            return retSynPromise;

        }

    }




    function initializeObject(obj) {

        _PkiMgr = new WebSocketManager()
        //尝试列表
        var TryList = [

            "wss://127.0.0.1:10443",
            "wss://127.0.0.1:20443",
            "wss://127.0.0.1:30443",
        ];


        for (var i in TryList) {

            var connectInfoItem = {};
            connectInfoItem.url = TryList[i];
            connectInfoItem.canConnect = 0;
            _PkiMgr.AddConnectObject(connectInfoItem);

        }

    }



    function SendNetcaCryptoJsonRpcMessage(params) {

        return _PkiMgr.SendRPCMessage(params);
    }


  
   function createSynCallBackJsonString(errorMsg,status)
	{
		    var responseResult={};
			responseResult.status=status;
			responseResult.msg=errorMsg;

			var retData = JSON.stringify({
				"requestVersion": 1,
                "requestOrigin": "45B45638-A006-4cf1-A298-816B376D867E",
				"requestId": -1,
				"responseResult": responseResult
			});

			return retData;
	}


	 //检测服务是否正常
     PKIObject.CheckLocalServerOk = function (params) 
	  {

        //使用GetVersion 来处理 如果使用IE低版本 调用COM组件了
		var requestQueryParams = {};
        requestQueryParams["function"] ="GetVersionInfo1";
        requestQueryParams["param"] = params;
        return SendNetcaCryptoJsonRpcMessage(requestQueryParams);
	  }


     //这个方法 兼容所有浏览器，但是IE版本可以精确判断是否安装了中间件
	 //

	  PKIObject.checkPKIInstall = function (params) 
	  {

      //如果是IE 使用COM方式类处理
      if (typeof WebSocket != 'undefined') 
	  { 
		 //使用GetVersion 来处理 如果使用IE低版本 调用COM组件了
		var requestQueryParams = {};
        requestQueryParams["function"] ="GetVersionInfo1";
        requestQueryParams["param"] = params;
        return SendNetcaCryptoJsonRpcMessage(requestQueryParams);

		}
		else
		{
		    var retSynPromise = new easySynPromise();
		    var comObj = null;
            try {

                 comObj = new ActiveXObject("NetcaPki.Utilities");
                _PkiMgr.ProcessSynMessageCallBack(createSynCallBackJsonString("", 0),retSynPromise);
                 return retSynPromise;

            } catch (e) {

                var NETCA_ERROR_WEBSOCKETCONNECTFAIL=-11000;
                _PkiMgr.ProcessSynMessageCallBack(createSynCallBackJsonString("数字证书服务连接失败(1)，请确认已安装最新的数字证书驱动并且开启了客户端", NETCA_ERROR_WEBSOCKETCONNECTFAIL),retSynPromise);
                return retSynPromise;
            }

			//加载成功 调用接口
            try {

                retString = comObj.CryptoJsonRpcCallBack(sendMsg);
                _PkiMgr.ProcessSynMessageCallBack(createSynCallBackJsonString("", 0),retSynPromise);
                return retSynPromise;

            } catch (e) {

                _PkiMgr.ProcessSynMessageCallBack(createSynCallBackJsonString("数字证书服务连接失败(2)，请确认已安装最新的数字证书驱动并且开启了客户端", NETCA_ERROR_WEBSOCKETCONNECTFAIL),retSynPromise);
                return retSynPromise;

            }
		}
       
	  }

	  

    //RSA 加解密模块
    PKIObject.publicKeyEncrypt = function (params) {
        var requestQueryParams = {};
        requestQueryParams["function"] = "PublicKeyEncrypt";
        requestQueryParams["param"] = params;
        return SendNetcaCryptoJsonRpcMessage(requestQueryParams);;
    }

    PKIObject.privateKeyDecrypt = function (params) {
        var requestQueryParams = {};
        requestQueryParams["function"] = "PrivateKeyDecrypt";
        requestQueryParams["param"] = params;
        return SendNetcaCryptoJsonRpcMessage(requestQueryParams);;
    }

    //SignedData 模块
    PKIObject.signedDataSign = function (params) {

        var requestQueryParams = {};
        requestQueryParams["function"] = "SignedDataSign";
        requestQueryParams["param"] = params;
        return SendNetcaCryptoJsonRpcMessage(requestQueryParams);;
    }
    PKIObject.signedDataVerify = function (params) {

        var requestQueryParams = {};
        requestQueryParams["function"] = "SignedDataVerify";
        requestQueryParams["param"] = params;
        return SendNetcaCryptoJsonRpcMessage(requestQueryParams);;
    }

    //数字信封 模块
    PKIObject.envelopedDataEncrypt = function (params) {

        var requestQueryParams = {};
        requestQueryParams["function"] = "EnvelopedDataEncrypt";
        requestQueryParams["param"] = params;
        return SendNetcaCryptoJsonRpcMessage(requestQueryParams);;
    }
    PKIObject.envelopedDataDecrypt = function (params) {

        var requestQueryParams = {};
        requestQueryParams["function"] = "EnvelopedDataDecrypt";
        requestQueryParams["param"] = params;
        return SendNetcaCryptoJsonRpcMessage(requestQueryParams);;
    }

    //PDFSign 模块
    PKIObject.pdfAutoSign = function (params) {
        var requestQueryParams = {};
        requestQueryParams["function"] = "PdfAutoSign";
        requestQueryParams["param"] = params;
        return SendNetcaCryptoJsonRpcMessage(requestQueryParams);;
    }
	
	PKIObject.getCertStringAttribute = function(params){
		 var requestQueryParams = {};
        requestQueryParams["function"] = "GetCertStringAttribute";
        requestQueryParams["param"] = params;
        return SendNetcaCryptoJsonRpcMessage(requestQueryParams);
	}
	
	PKIObject.isInsertKey = function(params){
		 var requestQueryParams = {};
        requestQueryParams["function"] = "IsInsertKey";
        requestQueryParams["param"] = params;
        return SendNetcaCryptoJsonRpcMessage(requestQueryParams);
	}
	
	
	PKIObject.verifyKeyPwd = function(params){
		 var requestQueryParams = {};
        requestQueryParams["function"] = "VerifyKeyPwd";
        requestQueryParams["param"] = params;
        return SendNetcaCryptoJsonRpcMessage(requestQueryParams);
	}
	
	PKIObject.getVersionInfo = function(params){
		 var requestQueryParams = {};
        requestQueryParams["function"] ="GetVersionInfo";
        requestQueryParams["param"] = params;
        return SendNetcaCryptoJsonRpcMessage(requestQueryParams);
	}
	PKIObject.monitorDevice=function(params){		
		var requestQueryParams = {};
        requestQueryParams["function"] ="MonitorDevice";
        requestQueryParams["param"] = params;
        return SendNetcaCryptoJsonRpcMessage(requestQueryParams);		
	}
	

	
	PKIObject.getCertList=function(params){		
		var requestQueryParams = {};
        requestQueryParams["function"] ="GetCertList";
        requestQueryParams["param"] = params;
        return SendNetcaCryptoJsonRpcMessage(requestQueryParams);		
	}
	PKIObject.GetCertList = function (params) {
	    return this.getCertList(params);
	}


	PKIObject.hashData=function(params){		
		var requestQueryParams = {};
        requestQueryParams["function"] ="HashData";
        requestQueryParams["param"] = params;
        return SendNetcaCryptoJsonRpcMessage(requestQueryParams);		
	}
	PKIObject.HashData = function (params) {
	    return this.hashData(params);
	}

	PKIObject.getCertStringExtensionValue=function(params){		
		var requestQueryParams = {};
        requestQueryParams["function"] ="GetCertStringExtensionValue";
        requestQueryParams["param"] = params;
        return SendNetcaCryptoJsonRpcMessage(requestQueryParams);		
	}
	PKIObject.GetCertStringExtensionValue = function (params) {
	    return this.getCertStringExtensionValue(params);
	}

	PKIObject.getClientVersionInfo = function(params){
		 var requestQueryParams = {};
        requestQueryParams["function"] ="GetClientVersionInfo";
        requestQueryParams["param"] = params;
        return SendNetcaCryptoJsonRpcMessage(requestQueryParams);
	}

    //>=1.4.1版本 
	PKIObject.sign = function (params) {
	    var requestQueryParams = {};
	    requestQueryParams["function"] = "Sign";
	    requestQueryParams["param"] = params;
	    return SendNetcaCryptoJsonRpcMessage(requestQueryParams);
	}

    //>=1.4.1版本 
	PKIObject.verifySignature = function (params) {
	    var requestQueryParams = {};
	    requestQueryParams["function"] = "VerifySignature";
	    requestQueryParams["param"] = params;
	    return SendNetcaCryptoJsonRpcMessage(requestQueryParams);
	}
    
    PKIObject.GetVersion = function(params)
    {
        var requestQueryParams = {};
        requestQueryParams["appName"] = "SignatureCreator";
        requestQueryParams["function"] = "GetSealClientVersion";
        requestQueryParams["param"] = params;
        return SendNetcaCryptoJsonRpcMessage(requestQueryParams);
    }
 
    //>=1.4.1版本 
    PKIObject.verifySignature = function (params) {
        var requestQueryParams = {};
        requestQueryParams["function"] = "VerifySignature";
        requestQueryParams["param"] = params;
        return SendNetcaCryptoJsonRpcMessage(requestQueryParams);
    }

    
    PKIObject.clearPwdCache = function (params) {
        var requestQueryParams = {};
        requestQueryParams["function"] = "ClearPwdCache";
        requestQueryParams["param"] = params;
        return SendNetcaCryptoJsonRpcMessage(requestQueryParams);
    }

    PKIObject.cipher = function (params) {
        var requestQueryParams = {};
        requestQueryParams["function"] = "Cipher";
        requestQueryParams["param"] = params;
        return SendNetcaCryptoJsonRpcMessage(requestQueryParams);
    }

    PKIObject.verifyCertificate = function (params) {
        var requestQueryParams = {};
        requestQueryParams["function"] = "VerifyCertificate";
        requestQueryParams["param"] = params;
        return SendNetcaCryptoJsonRpcMessage(requestQueryParams);
    }
    
    PKIObject.netcaAA_VerifyUserCert = function (params) {
        var requestQueryParams = {};
 
        requestQueryParams["function"] = "NetcaAAVerifyUserCert";
        requestQueryParams["param"] = params;
        return SendNetcaCryptoJsonRpcMessage(requestQueryParams);
    }

    PKIObject.netcaAA_ChkOneCert = function (params) {
        var requestQueryParams = {};
 
        requestQueryParams["function"] = "NetcaAAChkOneCert";
        requestQueryParams["param"] = params;
        return SendNetcaCryptoJsonRpcMessage(requestQueryParams);
    }

	PKIObject.modifyKeyPwd = function(params){
		 var requestQueryParams = {};
        requestQueryParams["function"] = "ModifyKeyPwd";
        requestQueryParams["param"] = params;
        return SendNetcaCryptoJsonRpcMessage(requestQueryParams);
    }

	PKIObject.unlockKeyPwd = function(params){
		 var requestQueryParams = {};
        requestQueryParams["function"] = "UnlockKeyPwd";
        requestQueryParams["param"] = params;
        return SendNetcaCryptoJsonRpcMessage(requestQueryParams);
    }

	PKIObject.generateP10 = function(params){
		 var requestQueryParams = {};
        requestQueryParams["function"] = "GenerateP10";
        requestQueryParams["param"] = params;
        return SendNetcaCryptoJsonRpcMessage(requestQueryParams);
    }

	PKIObject.installCertificate = function(params){
		 var requestQueryParams = {};
        requestQueryParams["function"] = "InstallCertificate";
        requestQueryParams["param"] = params;
        return SendNetcaCryptoJsonRpcMessage(requestQueryParams);
    }

	PKIObject.keyXClientPluginShell = function (params) {
	    var requestQueryParams = {};
	    requestQueryParams["function"] = "KeyXClientPluginShell";
	    requestQueryParams["param"] = params;
		requestQueryParams["appName"] = "KeyXClientApp";
	    return SendNetcaCryptoJsonRpcMessage(requestQueryParams);
	}

	PKIObject.displayCert = function (params) {
	    var requestQueryParams = {};
	    requestQueryParams["function"] = "DisplayCert";
	    requestQueryParams["param"] = params;
	    return SendNetcaCryptoJsonRpcMessage(requestQueryParams);
	}
	
	PKIObject.SendNetcaCryptoJsonRpcMessage = function(params){
        return SendNetcaCryptoJsonRpcMessage(params);
    }
	
    initializeObject();

    return PKIObject;


}());



function utf8_to_b64(str) {
    return window.btoa(unescape(encodeURIComponent(str)));
}

function b64_to_utf8(str) {
    return decodeURIComponent(escape(window.atob(str)));
}


//检测版本是否正常信息
function netca_checkServiceOK(SuccessCallBack, FailedCallBack) {
    var NETCA_ERROR_WEBSOCKETCONNECTFAIL = -11000;
    var params = {};
    NetcaPKI.CheckLocalServerOk(params)
	    .Then(function (res) {
	        SuccessCallBack(res);

	    })
        .Catch(function (res) {
            if (res.status == NETCA_ERROR_WEBSOCKETCONNECTFAIL) {

                //连接失败 返回错误 其他错误认为是成功
                FailedCallBack(res);
            }
            else {
                SuccessCallBack(res);
            }
        });
}




//检测版本是否正常信息
function netca_checkPKIInstall(SuccessCallBack, FailedCallBack) {
    var NETCA_ERROR_WEBSOCKETCONNECTFAIL = -11000;
    var params = {};
    NetcaPKI.checkPKIInstall(params)
	    .Then(function (res) {
	        SuccessCallBack(res);

	    })
        .Catch(function (res) {
            if (res.status == NETCA_ERROR_WEBSOCKETCONNECTFAIL) {

                //连接失败 返回错误 其他错误认为是成功
                FailedCallBack(res);
            }
            else {
                SuccessCallBack(res);
            }
        });
}

/* 非对称加密，加密文件内容
 selectType，选择证书的来源
 selectCondition，选择证书的条件，
publicKeyEncFilePath,要加密的文件路径
 */
function netca_publicKeyEncryptFile(selectType, selectCondition, publicKeyEncFilePath, SuccessPublicKeyEncryptFileCallBack, FailedPublicKeyEncryptFileCallBack) {
    var params = {
        cert: { //证书(CertificateParams)           
            type: selectType,
            condition: selectCondition
        },
        data: { //数据(DataParams)           
            fileName: publicKeyEncFilePath
        }
    }
    NetcaPKI.publicKeyEncrypt(params)
        .Then(function (res) {
            publicKeyEncResult.value = res.encryptValue;
            lastPublicKeyEncResult.value = res.encryptValue;
        })
        .Catch(function (res) {
            alert(res.msg);
        })
}

/* 非对称加密，加密文本
 selectType，选择证书的来源
 selectCondition，选择证书的条件，
tbs,要加密的文本 
 */
function netca_publicKeyEncryptText(selectType, selectCondition, tbs, SuccessPublicKeyEncryptFileCallBack, FailedPublicKeyEncryptFileCallBack) {
    var params = {
        cert: { //证书(CertificateParams)           
            type: selectType,
            condition: selectCondition
        },
        data: { //数据(DataParams)           
            text: tbs
        }
    };
    NetcaPKI.publicKeyEncrypt(params)
        .Then(function (res) {
            SuccessPublicKeyEncryptFileCallBack(res);
        })
        .Catch(function (res) {
            FailedPublicKeyEncryptFileCallBack(res);
        })
}

/* 非对称解密
 selectType，选择证书的来源
 selectCondition，选择证书的条件，
cipherData,要解密的密文 
savefile,解密后如果需要使用文件保存，传入的文件路径,如果为null，则不使用文件保存
 */
function netca_privateKeyDecrypt(selectType, selectCondition, cipherData, savefile, SuccessPrivateKeyDecryptCallBack, FailedPrivateKeyDecryptCallBack) {
    var params = {
        cert: {
            type: selectType,
            condition: selectCondition
        },
        // algo: 0, //非对称加密算法
        encData: cipherData,
        savefileName: savefile
    };
    NetcaPKI.privateKeyDecrypt(params)
        .Then(function (res) {
            SuccessPrivateKeyDecryptCallBack(res);
        })
        .Catch(function (res) {
            FailedPrivateKeyDecryptCallBack(res);
        })
}

/* signedData签名
certEncode, 证书编码，如果为空字符串，则使用selectType,和selectCondition构造的条件选择证书
 selectType，certEncode为空时，使用该项选择证书
 selectCondition，certEncode为空时，使用该项选择证书，
 tbs,要签名的明文 
 _useSubjectKeyId：是否使用密钥标识符
 _useQ7：是否使用主体密钥标识符来标识证书，默认为true
 _detached：整数	是否不带原文，默认为0
 _tsaURL：时间戳url
 _includeCertOption//整数	包含证书的标识
 */
function netca_signedDataSign(certEncode, selectType, selectCondition, tbs, _useSubjectKeyId, _useQ7, _tsaURL, _detached, _includeCertOption, _certPwd, SuccessSignedDataSignCallBack, FailedSignedDataSignCallBack) {
    var params = {};
    if (_certPwd == "") {
        params = {
            cert: { //证书(CertificateParams)
                encode: certEncode, //可选字段但不能为空
                type: selectType,
                condition: selectCondition
            },
            data: { //数据(DataParams)
                text: tbs
            },
            useSubjectKeyId: _useSubjectKeyId, //是否使用主体密钥标识符来标识证书，默认为true
            useQ7: _useQ7, //布尔值	是否使用国密Q7的方式，默认为false
            detached: parseInt(_detached), //整数	是否不带原文，默认为0
            tsaURL: _tsaURL,
            includeCertOption: _includeCertOption//整数	包含证书的标识       
        };
    } else {
        params = {
            cert: { //证书(CertificateParams)
                encode: certEncode, //可选字段但不能为空
                type: selectType,
                condition: selectCondition
            },
            data: { //数据(DataParams)
                text: tbs
            },
            useSubjectKeyId: _useSubjectKeyId, //是否使用主体密钥标识符来标识证书，默认为true
            useQ7: _useQ7, //布尔值	是否使用国密Q7的方式，默认为false
            detached: parseInt(_detached), //整数	是否不带原文，默认为0
            tsaURL: _tsaURL,
            includeCertOption: _includeCertOption,//整数	包含证书的标识
            certPwd: _certPwd
        };
    }
    NetcaPKI.signedDataSign(params)
        .Then(function (res) {
            if (res.result == -5) {
                FailedSignedDataSignCallBack(res);
            }
            else {
                SuccessSignedDataSignCallBack(res);
            }

        })
        .Catch(function (res) {
            FailedSignedDataSignCallBack(res);
        })

}

/* signedData验证
verifyLevel, 验证级别
signValue：base64编码的签名值
 tbs：base64编码后的明文 
 */

function netca_signedDataVerify(verifyLevel, signValue, tbs, SuccessSignedDataVerifyCallBack, FailedSignedDataVerifyCallBack) {
    var params = {
        verifyLevel: verifyLevel, //验证级别，默认为验证签名本身，不验证证书，默认为1
        signedData: signValue //signedData的编码值
    };
    if (tbs != null) {
        params.originData = {
            text: tbs
        };
    }
    NetcaPKI.signedDataVerify(params)
        .Then(function (res) {
            SuccessSignedDataVerifyCallBack(res);
        })
        .Catch(function (res) {
            FailedSignedDataVerifyCallBack(res);
        })
}

/* 数字信封加密---加密文件内容
certEncode, 证书编码，如果为空字符串，则使用selectType,和selectCondition构造的条件选择证书
 selectType，certEncode为空时，使用该项选择证书
 selectCondition，certEncode为空时，使用该项选择证书，
 _fieldName,明文文件路径
 _algo,使用的对称加密算法，默认为AES 128 CBC模式
 */

function netca_envelopedDataEncryptFile(certCode, selectType, selectCondition, _fieldName, _algo, SuccessEnvelopedDataEncryptCallBack, FailedEnvelopedDataEncryptCallBack) {


    var params = {
        cert: { //证书(CertificateParams)
            encode: certCode,
            type: selectType,
            condition: selectCondition
        },
        algo: _algo, //对称加密算法，默认为128位的AES CBC模式
        data: {
            fileName: _fieldName
        }
    };
    NetcaPKI.envelopedDataEncrypt(params)
         .Then(function (res) {
             SuccessEnvelopedDataEncryptCallBack(res);
         })
         .Catch(function (res) {
             FailedEnvelopedDataEncryptCallBack(res);
         })
}




/* 数字信封加密---加密文本
certEncode, 证书编码，如果为空字符串，则使用selectType,和selectCondition构造的条件选择证书
 selectType，certEncode为空时，使用该项选择证书
 selectCondition，certEncode为空时，使用该项选择证书，
 clearText,明文字符串
 _algo,使用的对称加密算法，默认为AES 128 CBC模式
 SuccessEnvelopedDataEncryptCallBack，加密成功时回调接口
 FailedEnvelopedDataEncryptCallBack，加密失败时回调接口

 */

function netca_envelopedDataEncryptText(certCode, selectType, selectCondition, clearText, _algo, SuccessEnvelopedDataEncryptCallBack, FailedEnvelopedDataEncryptCallBack) {


    var params = {
        cert: { //证书(CertificateParams)
            encode: certCode,
            type: selectType,
            condition: selectCondition
        },
        algo: _algo, //对称加密算法，默认为128位的AES CBC模式
        data: {
            text: clearText
        }
    };
    NetcaPKI.envelopedDataEncrypt(params)
        .Then(function (res) {
            SuccessEnvelopedDataEncryptCallBack(res);
        })
        .Catch(function (res) {
            FailedEnvelopedDataEncryptCallBack(res);
        })
}


/* 解密数字信封
encData,数字信封编码
saveFile,解密后如果需要使用文件保存，传入的文件路径,如果为null，则不使用文件保存
SuccessEnvDecryptCallBack，解密成功时执行的回调函数
FailedEnvDecryptCallBack，解密失败时执行的回调函数
*/

function netca_envelopedDataDecrypt(encData, saveFile, SuccessEnvDecryptCallBack, FailedEnvDecryptCallBack) {
    var params = {
        encData: encData, //要解密的数据
        savefileName: saveFile
    };
    NetcaPKI.envelopedDataDecrypt(params)
        .Then(function (res) {
            SuccessEnvDecryptCallBack(res);
        })
        .Catch(function (res) {
            FailedEnvDecryptCallBack(res);
        })
}

function netca_AutoSignField(_hashAlgo, src_File, dest_File, revInfoInclude, _tsaURL, _tsaUsr, _tsaPwd, _tsaHashAlgo, _allowCertType, _selMode,
                                _fieldName, SignSuccessCallBack, SignFailedCallBack) {
    var params = {
        hashAlgo: _hashAlgo, //例如：“sha-1”，“sha-256”
        srcFile: src_File, //源pdf文件
        destFile: dest_File, //源pdf文件
        revInfoIncludeFlag: revInfoInclude, //签名时是否包含签名证书状态信息，true包含
        selMode: _selMode, //选择模式，有两种：按证书选择（0）和按印章选择（1）
        tsaURL: _tsaURL, //时间戳地址
        tsaUsr: _tsaUsr, //时间戳服务对应用户名
        tsaPwd: _tsaPwd, //时间戳服务对应用户的密码
        tsaHashAlgo: _tsaHashAlgo, //时间戳使用的hash算法，例如”sha-1”，”sha-256”等
        allowCertType: _allowCertType,
        /**
         * allowCertType为NULL或者ANY表示不进行限制。
         * RSA为RSA类型，SM2为SM2类型，ECC为ECC类型包括SM2，ECC!SM2为不包括SM2的ECC类型。
         * RSA:SM2为既包括RSA也包括SM2。
         * 默认支持RSA,SM2类型的证书。
         * 中间件 5.2版本新增的
         */

        signField: {
            fieldName: _fieldName //要进行签名的签名域名称
        }
    };
    NetcaPKI.pdfAutoSign(params)
        .Then(function (res) {
            SignSuccessCallBack();
        })
        .Catch(function (res) {
            SignFailedCallBack(res);
        });;
}




function netca_AutoSignPosition(_hashAlgo, src_File, dest_File, revInfoInclude, _tsaURL, _tsaUsr, _tsaPwd, _tsaHashAlgo, _allowCertType, _selMode,
                                _pdfSign_pageNumber, _pdfSign_xpos, _pdfSign_ypos, _pdfSign_seal_width, _pdfSign_seal_height,
								 SignSuccessCallBack, SignFailedCallBack) {
    var params = {
        hashAlgo: _hashAlgo, //例如：“sha-1”，“sha-256”
        srcFile: src_File, //源pdf文件
        destFile: dest_File, //源pdf文件
        revInfoIncludeFlag: revInfoInclude, //签名时是否包含签名证书状态信息，true包含
        selMode: _selMode, //选择模式，有两种：按证书选择（0）和按印章选择（1）
        tsaURL: _tsaURL, //时间戳地址
        tsaUsr: _tsaUsr, //时间戳服务对应用户名
        tsaPwd: _tsaPwd, //时间戳服务对应用户的密码
        tsaHashAlgo: _tsaHashAlgo, //时间戳使用的hash算法，例如”sha-1”，”sha-256”等
        allowCertType: _allowCertType,
        /**
         * allowCertType为NULL或者ANY表示不进行限制。
         * RSA为RSA类型，SM2为SM2类型，ECC为ECC类型包括SM2，ECC!SM2为不包括SM2的ECC类型。
         * RSA:SM2为既包括RSA也包括SM2。
         * 默认支持RSA,SM2类型的证书。
         * 中间件 5.2版本新增的
         */
        signPosition: {
            pageNum: parseInt(_pdfSign_pageNumber), //输入参数，整型，PDF文档的页码，页码从1开始计算。
            xPos: parseInt(_pdfSign_xpos), //输入参数，整型，签章左下角的水平向右方向坐标。
            yPos: parseInt(_pdfSign_ypos), //输入参数，整型，签章左下角的垂直向上方向坐标。
            width: parseInt(_pdfSign_seal_width), //输入参数，整型，签章的宽度。
            height: parseInt(_pdfSign_seal_height) //输入参数，整型，签章的高度。
        }
    };
    NetcaPKI.pdfAutoSign(params)
        .Then(function (res) {
            SignSuccessCallBack();
        })
        .Catch(function (res) {
            SignFailedCallBack(res);
        });
}




/*
 获取证书或证书属性
 certEncode, 证书编码，如果为空字符串，则使用selectType,和selectCondition构造的条件选择证书
 selectType，certEncode为空时，使用该项选择证书
 selectCondition，certEncode为空时，使用该项选择证书，
 attId，证书属性
 successCallBack，成功时执行的回调函数
 failedCallBack，失败时执行的回调函数
 */

/* 从USBKey中选择NETCA 颁发的签名证书
  var selectType = "{\"UIFlag\":\"default\", \"InValidity\":true,\"Type\":\"signature\", \"Method\":\"device\",\"Value\":\"any\"}";
  var selectCondition = "IssuerCN~'NETCA' && InValidity='True' && CertType='Signature'";
*/

/* 从微软证书库中选择NETCA 颁发的加密证书 

       var selectType = "{\"UIFlag\":\"default\",\"Type\":\"encrypt\",\"Method\":\"store\", \"Value\":{\"Type\":\"current user\",\"Value\":\"my\"}}";
       var selectCondition = "IssuerCN~'NETCA' && CertType='Encrypt'";
*/

function netca_getCertStringAttribute(certEncode, selectType, selectCondition, attId, successCallBack, failedCallBack,form) {
    var result;
    var params = {
        cert: {
            "encode": certEncode,
            "type": selectType,
            "condition": selectCondition
        },
        id: parseInt(attId)
    };
    NetcaPKI.getCertStringAttribute(params)
	    .Then(function (res) {
	        successCallBack(res,form);
	    })
        .Catch(function (res) {
            failedCallBack(res);
        });
    return result;
}


/*
 *判断指定设备的是否插入
 certEncode,证书编码，可为空字符串，表示判断证书所在的设备是否插入
 keySN,设备序列号，可为空，表示判断设备序列号为KeySN的设备是否插入
 keyType,设备类型，可为-1，表示判断设备类型为keyType的设备是否插入
 certEncode，keySN，keyType 都设置时，三者之间的关系为交集，即判断设备设备序列号为
 keySN,设备类型为keyType,且包含证书certEncode的设备是否插入。
 都不设置时，判断任何一个Key是否插入。
 InsertCallBack,有插入设备时执行的回调函数
 UnInsertCallBack，无插入的设备时执行的回调函数
 */
function netca_isInsertKey(certEncode, keySN, keyType, InsertCallBack, UnInsertCallBack) {
    var params = {
        cert: {
            "encode": certEncode
        },//可选,指定证书所在的Key是否插入
        sn: keySN, //可选
        type: parseInt(keyType) //可选
    };
    NetcaPKI.isInsertKey(params)
	    .Then(function (res) {

	        if (res.insertCount > 0) {
	            InsertCallBack(res);
	        }
	        else {
	            UnInsertCallBack(res);

	        }

	    })
        .Catch(function (res) {
            UnInsertCallBack(res);
        });;
}



function netca_Custom_PdfSignAndUpload(certEncode, pageNum, x, y, signPdfBase64Str, UploadPdfUrl, successCallBack, failedCallBack) {
    var params = {

        "certEncode": certEncode,
        "pageNum": pageNum,
        "x": x,
        "y": y,
        "signPdfBytes": signPdfBase64Str,
        "uploadPdfUrl": UploadPdfUrl

    };
    NetcaPKI.Custom_PdfSignAndUpload(params)
	    .Then(function (res) {
	        successCallBack(res);
	    })
        .Catch(function (res) {
            failedCallBack(res);
        });;
}

function netca_Custom_PdfSignAndUploadURL(certEncode, pageNum, x, y, width, height, signUrl, UploadPdfUrl, successCallBack, failedCallBack) {
    //signPdfUrl
    var params = {

        "certEncode": certEncode,
        "pageNum": pageNum,
        "x": x,
        "y": y,
        "width": width,
        "height": height,
        "signPdfUrl": signUrl,
        "uploadPdfUrl": UploadPdfUrl

    };
    NetcaPKI.Custom_PdfSignAndUploadByURL(params)
	    .Then(function (res) {
	        successCallBack(res);
	    })
        .Catch(function (res) {
            failedCallBack(res);
        });;
}

//获取签章
//参数是证书编码
function netca_getSealImage(certEncode, SuccessGetSealImageCallBack, FailedGetSealImageCallBack,form) {


    var params = {
        cert: {
            "encode": certEncode
        }
    };


    NetcaPKI.GetNetcaSealImage(params)
	    .Then(function (res) {
	        SuccessGetSealImageCallBack(res,form);
	    })
        .Catch(function (res) {
            FailedGetSealImageCallBack(res);
        });
}

 
/*
 *验证指定设备的密码
 password,介质密码，可为空字符串，则由底层弹出密码输入框
 certEncode,证书编码，可为空字符串，表示验证证书所在的Key的密码
 keySN,设备序列号，可为空，表示验证设备序列号为KeySN的设备的密码
 keyType,设备类型，可为-1，表示验证设备类型为keyType的设备的密码，
 certEncode，keySN，keyType 都设置时，三者之间的关系为交集，即验证的设备是设备序列号为
 keySN,设备类型为keyType,且包含证书certEncode.
 都不设置时, 默认验证第一个设备的PIN码
  SuccessCallBack，成功时执行的回调函数
 FailedCallBack，失败时执行的回调函数
 */
function netca_verifyKeyPwd(password, certEncode, keySN, keyType, SuccessCallBack, FailedCallBack) {

    var params = {
        pwd: password,
        cert: {
            "encode": certEncode
        },//可选,指定证书所在的Key是否插入
        sn: keySN, //可选
        type: parseInt(keyType) //可选
    };

    NetcaPKI.verifyKeyPwd(params)
	    .Then(function (res) {
	        if (res.result == 1) {
	            var tip = "密码验证成功，验证的设备类型为 " + res.type +
				";  验证的设备序列号为 " + res.sn
				+ ";  密码剩余重试次数为 " + res.retrynum;

	            SuccessCallBack(res);
	        }
	        else {
	            var tip = "密码验证失败，错误码为" + res.result +
				";  验证的设备类型为 " + res.type +
				";  验证的设备序列号为 " + res.sn
				+ ";  密码剩余重试次数为 " + res.retrynum;

	            FailedCallBack(res);
	        }

	    })
        .Catch(function (res) {
            alert(res.msg);
        });;
}


function netca_getCertStringExtensionValue(certEncode, selectType, selectCondition, oidString, oidType, SuccessCallBack, FailedCallBack) {
    var params = {
        cert: {
            "encode": certEncode,
            "type": selectType,
            "condition": selectCondition
        },
        oidstr: oidString,
        type: oidType
    };


    NetcaPKI.GetCertStringExtensionValue(params)
        .Then(function (res) {
            SuccessCallBack(res);
        })
        .Catch(function (res) {
            FailedCallBack(res);
        });

}


//获取版本号相关信息
function netca_getVersionInfo(SuccessGetVersionInfoCallBack, FailedGetVersionInfoCallBack) {

    var params = {};
    NetcaPKI.getVersionInfo(params)
	    .Then(function (res) {
	        SuccessGetVersionInfoCallBack(res);
	    })
        .Catch(function (res) {
            FailedGetVersionInfoCallBack(res);
        });
}

//获取版本号相关信息
function netca_clearPwdCache(SuccessCallBack, FailedCallBack) {

    var params = {};
    NetcaPKI.clearPwdCache(params)
	    .Then(function (res) {
	        SuccessCallBack(res);
	    })
        .Catch(function (res) {
            FailedCallBack(res);
        });
}

//获取客户端KeyX版本号相关信息
function netca_getClientVersionInfo(SuccessGetVersionInfoCallBack, FailedGetVersionInfoCallBack) {

    var params = {};
    NetcaPKI.getClientVersionInfo(params)
	    .Then(function (res) {
	        SuccessGetVersionInfoCallBack(res);
	    })
        .Catch(function (res) {
            FailedGetVersionInfoCallBack(res);
        });
}


//监控设备插拔
function netca_monitorDevice(_delayTime, SuccessMonitorDeviceCallBack, FailedMonitorDeviceCallBack) {

    var params = {
        delayTime: _delayTime //延迟时间，默认为0毫秒
    };
    NetcaPKI.monitorDevice(params)
	    .Then(function (res) {
	        SuccessMonitorDeviceCallBack(res);
	    })
        .Catch(function (res) {
            FailedMonitorDeviceCallBack(res);
        });
}



//PKCS#1签名
//version >=1.4.1
/*
参数说明:
 certEncode, 证书编码，如果为空字符串，则使用selectType,和selectCondition构造的条件选择证书
 selectType，certEncode为空时，使用该项选择证书
 selectCondition，certEncode为空时，使用该项选择证书
 signAlgo，  签名算法 整数 SHA256WthRSA签名算法为4,SM3WithSM2签名算法为25 参考NETCA Crypto API 文档
 origindata base64编码字符串 ，待签名的数据
 SuccessSignCallBack，成功时执行的回调函数
 FailedSignCallBack，失败时执行的回调函数
 */
function netca_sign(certEncode, selectType, selectCondition, signAlgo, origindata, SuccessSignCallBack, FailedSignCallBack) {

    var params = {
        cert: {
            "encode": certEncode,
            "type": selectType,
            "condition": selectCondition
        },
        data: {
            text: origindata
        },
        algo: signAlgo
    };
    NetcaPKI.sign(params)
	    .Then(function (res) {
	        SuccessSignCallBack(res);
	    })
        .Catch(function (res) {
            FailedSignCallBack(res);
        });
}

//PKCS#1签名
//version >=1.4.1
/*

 certEncode,字符串, 证书编码，如果为空字符串，则使用selectType,和selectCondition构造的条件选择证书
 selectType，certEncode为空时，使用该项选择证书
 selectCondition，certEncode为空时，使用该项选择证书，
 origindata ，字符串 base64编码,原签名数据
 signAlgo，签名算法 SHA256WthRSA签名算法为4,SM3WithSM2签名算法为25 参考NETCA Crypto API 文档
 SuccessSignVerifyCallBack，成功时执行的回调函数
 FailedSignVerifyCallBack，失败时执行的回调函数
 */
function netca_verifySignature(certEncode, selectType, selectCondition, signAlgo, origindata, signValue, SuccessSignVerifyCallBack, FailedSignVerifyCallBack) {

    var params = {
        cert: {
            "encode": certEncode,
            "type": selectType,
            "condition": selectCondition
        },
        data: {
            text: origindata
        },
        signature: signValue,
        algo: signAlgo
    };
    NetcaPKI.verifySignature(params)
	    .Then(function (res) {
	        SuccessSignVerifyCallBack(res);
	    })
        .Catch(function (res) {
            FailedSignVerifyCallBack(res);
        });
}

 
 

//标准签名/签章接口
/*通过查找符合条件的证书，
解析源文件和PDF字节流，
可在指定页面指定位置，定位签名或签章，
可在已有域进行签名或签章，
并将结果保存到目标文件中。
 */
function netca_SignatureCreator_PdfSignSealField(_srcFile, _srcBytes, _destFile, _certEncode, _selMode,
    _signFieldText, _sealImageEncode, _revInfoIncludeFlag, _fieldName,
     _tsaUrl, _tsaUsr, _tsaPwd, _tsaHashAlgo,
    _SignatureCreatorSuccessCallBack, _SignatureCreatorFailedCallBack) {
    var params = {
        srcFile: _srcFile,                      //源pdf文件
        srcBytes: _srcBytes,                    //源Pdf文件的Base64编码
        destFile: _destFile,                    //目标pdf文件
        certEncode: _certEncode,                //签名证书Base64编码
        selMode: _selMode,                      //操作模式
        signFieldText: _signFieldText,          //签名域显示的文字
        sealImageEncode: _sealImageEncode,      //签章图片Base64编码
        revInfoIncludeFlag: _revInfoIncludeFlag,//是否包含吊销信息
        SignField:                              //签名域对象
        {
            fieldName: _fieldName              //签名域名称
        },
        Tsa:                                    //时间戳对象
        {
            tsaUrl: _tsaUrl,                    //时间戳地址
            tsaUsr: _tsaUsr,                    //时间戳服务对应用户名
            tsaPwd: _tsaPwd,                    //时间戳服务对应用户的密码
            tsaHashAlgo: _tsaHashAlgo           //时间戳使用的hash算法，例如”sha-1”，”sha-256”等
        }
    };
    NetcaPKI.SignatureCreatorPdfSignSealFieldOrPosition(params)
        .Then(function (res) {
            _SignatureCreatorSuccessCallBack(res);
        })
        .Catch(function (res) {
            _SignatureCreatorFailedCallBack(res);
        });
}

function netca_SignatureCreator_PdfSignSealPosition(_srcFile, _srcBytes, _destFile, _certEncode, _selMode,
    _signFieldText, _sealImageEncode, _revInfoIncludeFlag, _pageNum, _xPos, _yPos, _width, _height,
    _tsaUrl, _tsaUsr, _tsaPwd, _tsaHashAlgo,
    _SignatureCreatorSuccessCallBack, _SignatureCreatorFailedCallBack) {
    var params = {
        srcFile: _srcFile,                      //源pdf文件
        srcBytes: _srcBytes,                    //源Pdf文件的Base64编码
        destFile: _destFile,                    //目标pdf文件
        certEncode: _certEncode,                //签名证书Base64编码
        selMode: _selMode,                      //操作模式
        signFieldText: _signFieldText,          //签名域显示的文字
        sealImageEncode: _sealImageEncode,      //签章图片Base64编码
        revInfoIncludeFlag: _revInfoIncludeFlag,//是否包含吊销信息
        SignPosition:                           //签名位置对象
        {
            pageNum: _pageNum,                  //PDF文档的页码
            xPos: _xPos,                        //签名域/签章左下角的水平向右方向坐标
            yPos: _yPos,                        //签名域/签章左下角的垂直向上方向坐标
            width: _width,                      //签名域/签章的宽度
            height: _height                    //签名域/签章的高度
        },
        Tsa:                                    //时间戳对象
        {
            tsaUrl: _tsaUrl,                    //时间戳地址
            tsaUsr: _tsaUsr,                    //时间戳服务对应用户名
            tsaPwd: _tsaPwd,                    //时间戳服务对应用户的密码
            tsaHashAlgo: _tsaHashAlgo           //时间戳使用的hash算法，例如”sha-1”，”sha-256”等
        }
    };
    NetcaPKI.SignatureCreatorPdfSignSealFieldOrPosition(params)
        .Then(function (res) {
            _SignatureCreatorSuccessCallBack(res);
        })
        .Catch(function (res) {
            _SignatureCreatorFailedCallBack(res);
        });
}

function netca_SignatureCreator_PdfSignSealFieldEx(_srcFile, _srcBytes, _destFile, _certEncode, _selMode,
    _signFieldText, _sealImageEncode, _revInfoIncludeFlag, _fieldName,
     _tsaUrl, _tsaUsr, _tsaPwd, _tsaHashAlgo,
    _text, _timeFormat, _widthPercentage, _heightPercentage, _fontName, _fontSize, _A, _R, _G, _B, _align,
    _SignatureCreatorSuccessCallBack, _SignatureCreatorFailedCallBack) {
    var params = {
        srcFile: _srcFile,                      //源pdf文件
        srcBytes: _srcBytes,                    //源Pdf文件的Base64编码
        destFile: _destFile,                    //目标pdf文件
        certEncode: _certEncode,                //签名证书Base64编码
        selMode: _selMode,                      //操作模式
        signFieldText: _signFieldText,          //签名域显示的文字
        sealImageEncode: _sealImageEncode,      //签章图片Base64编码
        revInfoIncludeFlag: _revInfoIncludeFlag,//是否包含吊销信息
        SignField:                              //签名域对象
        {
            fieldName: _fieldName              //签名域名称
        },
        Tsa:                                    //时间戳对象
        {
            tsaUrl: _tsaUrl,                    //时间戳地址
            tsaUsr: _tsaUsr,                    //时间戳服务对应用户名
            tsaPwd: _tsaPwd,                    //时间戳服务对应用户的密码
            tsaHashAlgo: _tsaHashAlgo           //时间戳使用的hash算法，例如”sha-1”，”sha-256”等
        },
        TimeObj:
        {
            text: _text,                        //自定义文本信息
            timeFormat: _timeFormat,            //日期格式
            widthPercentage: _widthPercentage,  //宽偏移百分比
            heightPercentage: _heightPercentage,//高偏移百分比
            fontName: _fontName,                //字体
            fontSize: _fontSize,                //字体大小
            FontColor:
            {
                A: _A,
                R: _R,
                G: _G,
                B: _B
            },
            align: _align                       //对齐方式
        }
    };
    NetcaPKI.SignatureCreatorPdfSignSealFieldOrPositionEx(params)
        .Then(function (res) {
            _SignatureCreatorSuccessCallBack(res);
        })
        .Catch(function (res) {
            _SignatureCreatorFailedCallBack(res);
        });
}

function netca_SignatureCreator_PdfSignSealPositionEx(_srcFile, _srcBytes, _destFile, _certEncode, _selMode,
    _signFieldText, _sealImageEncode, _revInfoIncludeFlag, _positionStartPage, _positionEndPage, _xPos, _yPos, _positioinWidth, _positionHeight,
    _tsaUrl, _tsaUsr, _tsaPwd, _tsaHashAlgo,
    _text, _timeFormat, _widthPercentage, _heightPercentage, _fontName, _fontSize, _A, _R, _G, _B, _align,
    _SignatureCreatorSuccessCallBack, _SignatureCreatorFailedCallBack) {
    var params = {
        srcFile: _srcFile,                      //源pdf文件
        srcBytes: _srcBytes,                    //源Pdf文件的Base64编码
        destFile: _destFile,                    //目标pdf文件
        certEncode: _certEncode,                //签名证书Base64编码
        selMode: _selMode,                      //操作模式
        signFieldText: _signFieldText,          //签名域显示的文字
        sealImageEncode: _sealImageEncode,      //签章图片Base64编码
        revInfoIncludeFlag: _revInfoIncludeFlag,//是否包含吊销信息
        SignPosition:                           //签名位置对象
        {
            startPage: _positionStartPage,      //PDF文档的开始页码
            endPage: _positionEndPage,          //PDF文档的结束页码
            xPos: _xPos,                        //签名域/签章左下角的水平向右方向坐标
            yPos: _yPos,                        //签名域/签章左下角的垂直向上方向坐标
            width: _positioinWidth,             //签名域/签章的宽度
            height: _positionHeight             //签名域/签章的高度
        },
        Tsa:                                    //时间戳对象
        {
            tsaUrl: _tsaUrl,                    //时间戳地址
            tsaUsr: _tsaUsr,                    //时间戳服务对应用户名
            tsaPwd: _tsaPwd,                    //时间戳服务对应用户的密码
            tsaHashAlgo: _tsaHashAlgo           //时间戳使用的hash算法，例如”sha-1”，”sha-256”等
        },
        TimeObj:
        {
            text: _text,                        //自定义文本信息
            timeFormat: _timeFormat,            //日期格式
            widthPercentage: _widthPercentage,  //宽偏移百分比
            heightPercentage: _heightPercentage,//高偏移百分比
            fontName: _fontName,                //字体
            fontSize: _fontSize,                //字体大小
            FontColor:
            {
                A: _A,
                R: _R,
                G: _G,
                B: _B
            },
            align: _align                       //对齐方式
        }
    };
    NetcaPKI.SignatureCreatorPdfSignSealFieldOrPositionEx(params)
        .Then(function (res) {
            _SignatureCreatorSuccessCallBack(res);
        })
        .Catch(function (res) {
            _SignatureCreatorFailedCallBack(res);
        });
}

function netca_SignatureCreator_SealKeyWord(_srcFile, _srcBytes, _destFile, _certEncode, _selMode,
    _signFieldText, _sealImageEncode, _revInfoIncludeFlag,
    _tsaUrl, _tsaUsr, _tsaPwd, _tsaHashAlgo,
    _text, _timeFormat, _widthPercentage, _heightPercentage, _fontName, _fontSize, _A, _R, _G, _B, _align,
    _keyWord, _keyWordStartPage, _keyWordEndPage, _keyWordIndex, _keyWordWidth, _keyWordHeight, _offsetX, _offsetY,
    _SignatureCreatorSuccessCallBack, _SignatureCreatorFailedCallBack) {
    var params = {
        srcFile: _srcFile,                      //源pdf文件
        srcBytes: _srcBytes,                    //源Pdf文件的Base64编码
        destFile: _destFile,                    //目标pdf文件
        certEncode: _certEncode,                //签名证书Base64编码
        selMode: _selMode,                      //操作模式
        signFieldText: _signFieldText,          //签名域显示的文字
        sealImageEncode: _sealImageEncode,      //签章图片Base64编码
        revInfoIncludeFlag: _revInfoIncludeFlag,//是否包含吊销信息
        SealKeyWord:                            //关键字对象
        {
            keyWord: _keyWord,                  //关键字
            startPage: _keyWordStartPage,       //PDF文档的开始页码
            endPage: _keyWordEndPage,           //PDF文档的结束页码
            keyWordIndex: _keyWordIndex,        //关键字索引
            width: _keyWordWidth,               //签名域矩形的宽度
            height: _keyWordHeight,             //签名域矩形的高度
            offsetX: _offsetX,                  //水平偏移量
            offsetY: _offsetY                   //垂直偏移量
        },
        Tsa:                                    //时间戳对象
        {
            tsaUrl: _tsaUrl,                    //时间戳地址
            tsaUsr: _tsaUsr,                    //时间戳服务对应用户名
            tsaPwd: _tsaPwd,                    //时间戳服务对应用户的密码
            tsaHashAlgo: _tsaHashAlgo           //时间戳使用的hash算法，例如”sha-1”，”sha-256”等
        },
        TimeObj:
        {
            text: _text,                        //自定义信息
            timeFormat: _timeFormat,            //日期格式
            widthPercentage: _widthPercentage,  //宽偏移百分比
            heightPercentage: _heightPercentage,//高偏移百分比
            fontName: _fontName,                //字体
            fontSize: _fontSize,                //字体大小
            FontColor:
            {
                A: _A,
                R: _R,
                G: _G,
                B: _B
            },
            align: _align                       //对齐方式
        }
    };
    NetcaPKI.SignatureCreatorPdfSignSealFieldOrPositionEx(params)
        .Then(function (res) {
            _SignatureCreatorSuccessCallBack(res);
        })
        .Catch(function (res) {
            _SignatureCreatorFailedCallBack(res);
        });
}

function netca_GetNetcaSealImage(certEncode, successCallBack, failedCallBack) {
    var params = {
        "cert": {
            "encode": certEncode
        }
    };
    NetcaPKI.GetNetcaSealImage(params)
	    .Then(function (res) {
	        successCallBack(res);
	    })
        .Catch(function (res) {
            failedCallBack(res);
        });;
}


function netca_HashData(hashData, algo, cp, successCallBack, failedCallBack) {
    params = {
        hashAlgo: algo,
        data: { //数据(DataParams)
            text: hashData
        },
        flag: cp
    };

    NetcaPKI.HashData(params)
	    .Then(function (res) {
	        successCallBack(res);
	    })
        .Catch(function (res) {
            failedCallBack(res);
        });;
}

function netca_cipher(useCipherEnc, cipherData, cipherAlgo, cipherKey, successCallBack, failedCallBack) {
    params = {
        algo: cipherAlgo,
        data: { //数据(DataParams)
            text: cipherData
        },
        key: { //数据(DataParams)
            hexText: cipherKey
        },
        useEnc: useCipherEnc
    };

    NetcaPKI.cipher(params)
	    .Then(function (res) {
	        successCallBack(res);
	    })
        .Catch(function (res) {
            failedCallBack(res);
        });;
}



function netca_GetCertList(successCallBack, failedCallBack) {

    var params = {};
    NetcaPKI.GetCertList(params)
	    .Then(function (res) {
	        successCallBack(res);
	    })
        .Catch(function (res) {
            failedCallBack(res);
        });;
}

function netca_getSealClientVersion(successCallBack, failedCallBack) {

    var params = {};
    NetcaPKI.getSealClientVersion(params)
        .Then(function (res) {
            successCallBack(res);
        })
        .Catch(function (res) {
            failedCallBack(res);
        });;
}

function netca_verifyCertificate(_certEncode, _bUseCrl, _bUseOcsp, _crlEncode, _ocspUrl, _time, successCallBack, failedCallBack) {
    var params = {
        cert: {
            "encode": _certEncode
        },
        bUseCrl: _bUseCrl,
        bUseOcsp: _bUseOcsp,
        crlEncode: _crlEncode,
        ocspUrl: _ocspUrl,
        time: _time
    };

    NetcaPKI.verifyCertificate(params)
        .Then(function (res) {
            successCallBack(res);
        })
        .Catch(function (res) {
            failedCallBack(res);
        });;
}

function netca_signatureVerifierVerifyPDF(_srcFile, _srcBytes, _level, successCallBack, failedCallBack) {
    var params = {
        srcFile: _srcFile,
        srcBytes: _srcBytes,
        level: _level
    };
    NetcaPKI.signatureVerifierVerifyPDF(params)
        .Then(function (res) {
            successCallBack(res);
        })
        .Catch(function (res) {
            failedCallBack(res);
        });;
}

function netca_signatureVerifierUndoPDF(_signFile, _signBytes, _index, successCallBack, failedCallBack) {
    var params = {
        signFile: _signFile,
        signBytes: _signBytes,
        index: _index
    };
    NetcaPKI.signatureVerifierUndoPDF(params)
        .Then(function (res) {
            successCallBack(res);
        })
        .Catch(function (res) {
            failedCallBack(res);
        });;

}


function netca_certauth_verifyCert(serverUrl, certEncode, selectType, selectCondition, checkVerifytime, successCallBack, failedCallBack) {
    var params = {
        url: serverUrl,
        verifytime: checkVerifytime,
        cert: { //证书(CertificateParams)
            encode: certEncode,
            type: selectType,
            condition: selectCondition
        }
    };
    NetcaPKI.netcaAA_VerifyUserCert(params)
        .Then(function (res) {
            successCallBack(res);
        })
        .Catch(function (res) {
            failedCallBack(res);
        });;

}

function netca_certauth_chkonecert(serverUrl, certEncode, selectType, selectCondition, successCallBack, failedCallBack) {
    var params = {
        url: serverUrl,
        cert: { //证书(CertificateParams)
            encode: certEncode,
            type: selectType,
            condition: selectCondition
        }
    };
    NetcaPKI.netcaAA_ChkOneCert(params)
        .Then(function (res) {
            successCallBack(res);
        })
        .Catch(function (res) {
            failedCallBack(res);
        });;

}

function netca_modifyKeyPwd(_sn, _type, _oldPwd, _newPwd, SuccessCallBack, FailedCallBack) {
    var params = {
        sn: _sn,
        type: parseInt(_type),
        oldPwd: _oldPwd,
        newPwd: _newPwd
    };

    NetcaPKI.modifyKeyPwd(params)
	    .Then(function (res) {
	        if (res.result == 1) {
	            SuccessCallBack(res);
	        }
	        else {
	            FailedCallBack(res);
	        }

	    })
        .Catch(function (res) {
            alert(res.msg);
        });;
}

function netca_unlockKeyPwd(_sn, _type, _unlockPwd, _newPwd, SuccessCallBack, FailedCallBack) {
    var params = {
        sn: _sn,
        type: parseInt(_type),
        unlockPwd: _unlockPwd,
        newPwd: _newPwd
    };

    NetcaPKI.unlockKeyPwd(params)
	    .Then(function (res) {
	        if (res.result == 1) {
	            SuccessCallBack(res);
	        }
	        else {
	            FailedCallBack(res);
	        }

	    })
        .Catch(function (res) {
            alert(res.msg);
        });;
}

function netca_generateP10(_sn, _type, _keypairAlgo, _keypairLabel, _keypairBits, _signAlgo,
    _c, _st, _l, _o, _email, _ou, _cn, successCallBack, failedCallBack) {
    var params = {
        sn: _sn,
        type: parseInt(_type),
        keypairAlgo: parseInt(_keypairAlgo),
        keypairLabel: _keypairLabel,
        keypairBits: parseInt(_keypairBits),
        signAlgo: parseInt(_signAlgo),
        SubjectObj: {
            c: _c,
            st: _st,
            l: _l,
            o: _o,
            email: _email,
            ou: _ou,
            cn: _cn
        }
    };
    NetcaPKI.generateP10(params)
        .Then(function (res) {
            successCallBack(res);
        })
        .Catch(function (res) {
            failedCallBack(res);
        });;

}

function netca_installCertificate(_sn, _type, _encKeypair, _encCert, _signCert, successCallBack, failedCallBack) {
    var params = {
        sn: _sn,
        type: parseInt(_type),
        encKeypair: _encKeypair,
        encCert: _encCert,
        signCert: _signCert
    };
    NetcaPKI.installCertificate(params)
        .Then(function (res) {
            successCallBack(res);
        })
        .Catch(function (res) {
            failedCallBack(res);
        });;

}

function KeyXClient_PluginShell(inuid,SuccessCallBack,FailedCallBack)
{
	var params = {
		uid:inuid 
	};
    NetcaPKI.keyXClientPluginShell(params)
        .Then(function (res) {
            
        })
        .Catch(function (res) {
            alert(res.msg);
        })
}