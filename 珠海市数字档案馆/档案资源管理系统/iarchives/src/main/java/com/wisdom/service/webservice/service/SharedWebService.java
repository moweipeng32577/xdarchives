package com.wisdom.service.webservice.service;

import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.entity.SharedPage;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * 用户类webService服务
 * Created by wjh
 */
@WebService(
        endpointInterface = "com.wisdom.service.webservice.service.SharedWebService",
        serviceName="UserWebService",
        targetNamespace="http://serviceImpl.webservice.service.wisdom.com/")
public interface SharedWebService {

    @WebMethod
    String login(@WebParam(name = "user") String user,
                 @WebParam(name = "password") String password);

    @WebMethod
    String destroyQueue(@WebParam(name = "token") String token);

    @WebMethod
    String clearExsit(@WebParam(name = "token") String token,
                      @WebParam(name = "archivecode") String archivecode,
                      @WebParam(name = "type") String type);

    @WebMethod
    String uploadValidate(@WebParam(name = "token") String token,
                          @WebParam(name = "archivecode") String archivecode);

    @WebMethod
    String downloadQueue(@WebParam(name = "token") String token,
                         @WebParam(name = "archivecodes") String archivecodes,
                         @WebParam(name = "type") String type);

    @WebMethod
    String listfiles(@WebParam(name = "token") String token,
                     @WebParam(name = "archivecode") String archivecode);

    @WebMethod
    String countScanFiles(@WebParam(name = "token") String token,
                          @WebParam(name = "batchname") String batchname,
                          @WebParam(name = "archivecode") String archivecode);

//    @WebMethod
//    SharedPage getSharedEntryData(@WebParam(name = "condition") String condition,
//                                  @WebParam(name = "operator") String operator,
//                                  @WebParam(name = "content") String content,
//                                  @WebParam(name = "page") Integer page,
//                                  @WebParam(name = "limit") Integer limit);
//
//    @WebMethod
//    SharedPage getSharedUserData(@WebParam(name = "condition") String condition,
//                                 @WebParam(name = "operator") String operator,
//                                 @WebParam(name = "content") String content,
//                                 @WebParam(name = "page") Integer page,
//                                 @WebParam(name = "limit") Integer limit);
//
//    @WebMethod
//    SharedPage getSharedOrganData(@WebParam(name = "condition") String condition,
//                                  @WebParam(name = "operator") String operator,
//                                  @WebParam(name = "content") String content,
//                                  @WebParam(name = "page") Integer page,
//                                  @WebParam(name = "limit") Integer limit);

    @WebMethod
    String getSharedData(@WebParam(name = "dataType") int dataType,
                         @WebParam(name = "condition") String condition,
                         @WebParam(name = "operator") String operator,
                         @WebParam(name = "content") String content,
                         @WebParam(name = "page") Integer page,
                         @WebParam(name = "limit") Integer limit);


}
