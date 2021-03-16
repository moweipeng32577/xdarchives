package com.wisdom.service.jms;

import com.wisdom.util.MD5;
import com.wisdom.web.entity.Szh_electronic_capture;
import com.wisdom.web.entity.Tb_user;
import com.wisdom.web.repository.SzhCalloutEntryRepository;
import com.wisdom.web.repository.SzhElectronicCaptureRepository;
import com.wisdom.web.repository.UserRepository;
import com.wisdom.web.service.ElectronicService;
import com.wisdom.web.service.SzhElectronicService;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.RandomAccessFile;

//@Service
public class MessageConsumerService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ElectronicService electronicService;

    @Autowired
    SzhElectronicService szhElectronicService;

    @Autowired
    SzhCalloutEntryRepository szhCalloutEntryRepository;

    @Autowired
    SzhElectronicCaptureRepository szhElectronicCaptureRepository;

    @Autowired
    JmsMessagingTemplate jmsMessagingTemplate;

    /**
     * @param token
     * @return
     */
    private boolean validate(String token){
        Boolean result = false;
        try {
            String[] str = MD5.AESdecode(token).split("&");
            String user = str[0];
            String password = str[1];
            Tb_user dbuser = userRepository.findByLoginname(user);
            //用户名不存在或密码错误,直接返回空
            if(dbuser == null || !password.equals(dbuser.getPassword())){
                return result;
            }
            result = true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    @Bean(name = "jmslcFactory")
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(){
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(jmsMessagingTemplate.getConnectionFactory());
        factory.setConcurrency("1");
        factory.setRecoveryInterval(1000L);
        return factory;
    }

    @JmsListener(destination = "up.queue", containerFactory = "jmslcFactory")
    public void receiveMessage(ActiveMQBytesMessage msg){
        processMsg(msg);
    }

    @JmsListener(destination = "ActiveMQ.DLQ", containerFactory = "jmslcFactory")
    public void receiveDLQMessage(ActiveMQBytesMessage msg){
        processMsg(msg);
    }

    private void processMsg(ActiveMQBytesMessage msg){
        try{
            String token = (String)msg.getProperty("token");
            if(!validate(token)){
                return;
            }
            //批次名
            String batchname = (String)msg.getProperty("batchname");
            //条目主键
            String entryid = (String)msg.getProperty("entryid");
            //条目档号
            String archivecode = (String)msg.getProperty("archivecode");
            //文件名称
            String filename = (String)msg.getProperty("filename");

            //图像幅面(A4.A3....)
            String imgsize = (String)msg.getProperty("imgsize");
            //上传类型（进入扫描库scan或者成品库other）
            String type = (String)msg.getProperty("type");

            //获取图像服务器存储路径
            String basepath = "";
            String storagepath = "";
            File targetFile = null;
            Szh_electronic_capture electronicCapture =  szhElectronicCaptureRepository.findByEntryidAndFilename(entryid,filename);
            if(electronicCapture==null){//判断文件是否存在
                switch (type) {
                    case "scan":
                        basepath = electronicService.getScanBaseDir(batchname, archivecode);
                        storagepath = electronicService.getScanStorageDir(batchname, archivecode);
                        targetFile = new File(storagepath, filename);
                        if(targetFile.exists()){
                            return;
                        }
                        break;
                    case "other":
                        basepath = electronicService.getStorageBaseDir("capture", entryid);
                        storagepath = electronicService.getStorageDir("capture", entryid);
                        targetFile = new File(storagepath, filename);
                        break;
                    default:
                        break;
                }

                //正文图像写数据库
                RandomAccessFile accessTmpFile = new RandomAccessFile(targetFile, "rw");
                accessTmpFile.write(msg.getContent().getData());
                accessTmpFile.close();

                //保存电子文件表
                szhElectronicService.saveElectronic(basepath, entryid, filename, msg.getBodyLength());

                targetFile = null;
                msg.clearBody();
                msg = null;
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
