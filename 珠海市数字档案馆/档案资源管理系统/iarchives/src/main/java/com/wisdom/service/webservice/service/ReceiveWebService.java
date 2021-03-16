package com.wisdom.service.webservice.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * 接收数据服务
 * Created by tanly on 2018/11/7 0018.
 */
@WebService(
        endpointInterface = "com.wisdom.service.webservice.service.ReceiveWebService",
        serviceName = "ReceiveDataService",
        targetNamespace = "http://serviceImpl.webservice.service.wisdom.com/")
public interface ReceiveWebService {

    @WebMethod
    String receiveData(@WebParam(name = "dataJson") String dataJson, @WebParam(name = "fileJson") String fileJson);

}
