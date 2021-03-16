//package com.xdtech.project.lot.acquistion;
//
//import com.alibaba.fastjson.JSON;
//import com.jnrsmcu.sdk.netdevice.*;
//import com.xdtech.project.lot.analysis.DataAnalysis;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * 仁硕电子温湿度传感器数据采集类
// * 需使用RSNetDevice-2.2.0.jar及param.dat
// * Created by Rong on 2019-03-22.
// */
//@Component
//public class RSHTAcquistion {
//
//    @Resource
//    DataAnalysis dataAnalysis;
//
//    public RSHTAcquistion(){
//        Thread capture = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try{
//                    while(dataAnalysis == null){
//                        Thread.sleep(100);
//                    }
//                    String datPath = RSHTAcquistion.class.getResource("/param.dat").getPath();
//                    RSServer rsServer = RSServer.Initiate(2404, datPath);
//                    rsServer.addDataListener(new IDataListener() {
//                        @Override
//                        public void receiveRealtimeData(RealTimeData realTimeData) {    //实时数据获取
//                            Map<String, Object> map = new HashMap<>();
//                            String deviceId = String.valueOf(realTimeData.getDeviceId());
//                            System.out.println("======实时数据获取======="+deviceId);
//                            try{
//                                for(NodeData data : realTimeData.getNodeList()){
//                                    map.put("tem", data.getTem());
//                                    map.put("hum", data.getHum());
//                                    String jsonData = JSON.toJSONString(map);
//                                    dataAnalysis.dataAnalysis("HT", deviceId, jsonData);
//                                }
//                            } catch (Exception e){
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void receiveLoginData(LoginData loginData) {
//                        }
//
//                        @Override
//                        public void receiveStoreData(StoreData storeData) {
//                        }
//
//                        @Override
//                        public void receiveTelecontrolAck(TelecontrolAck telecontrolAck) {
//                        }
//
//                        @Override
//                        public void receiveTimmingAck(TimmingAck timmingAck) {
//                        }
//
//                        @Override
//                        public void receiveParamIds(ParamIdsData paramIdsData) {
//                        }
//
//                        @Override
//                        public void receiveParam(ParamData paramData) {
//                        }
//
//                        @Override
//                        public void receiveWriteParamAck(WriteParamAck writeParamAck) {
//                        }
//
//                        @Override
//                        public void receiveTransDataAck(TransDataAck transDataAck) {
//                        }
//                    });
//                    rsServer.start();
//                } catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        });
//        capture.start();
//    }
//
//}
