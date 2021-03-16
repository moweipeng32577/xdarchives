package com.wisdom.service.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 信息推送服务
 * Created by Rong on 2018/6/26.
 */
@Service
public class WebSocketService {

    private static String NOTICE_PATH = "/message";     //通知刷新订阅地址
    private static String MESSAGE_PATH = "/message";    //推送信息订阅地址
    private static String ARCHIVEBAR_PATH = "/archiveBar";    //推送归档进度
    private static String GENERATEARCHIVEBAR_PATH = "/generateArchiveBar";    //推送生成档号进度
    private static String BACKUP_PATH = "/backupdatabase";    //推送备份所有结束后的提示
    private static String RESTORE_PATH = "/restore";    //推送恢复备份所有结束后的提示

    @Autowired
    SimpMessagingTemplate template;

    /**
     * 通知所有用户刷新
     */
    public void noticeRefresh(){
        template.convertAndSend(NOTICE_PATH,"");
    }

    /**
     * 通知指定用户刷新
     * @param userid 用户ID
     */
    public void noticeRefresh(String userid){
        template.convertAndSendToUser(userid, NOTICE_PATH,"");
    }

    /**
     * 通知多用户刷新
     * @param userids   用户ID数组
     */
    public void noticeRefresh(String[] userids){
        for(String userid : userids){
            noticeRefresh(userid);
        }
    }

    /**
     * 通知多用户刷新
     * @param userids   用户ID列表
     */
    public void noticeRefresh(List<String> userids){
        userids.parallelStream().forEach(userid -> noticeRefresh(userid));
    }

    /**
     * 推送信息到所有用户
     * @param message   推送信息
     */
    public void sendMessage(String message){
        template.convertAndSend(MESSAGE_PATH, message);
    }

    /**
     * 推送信息到指定用户
     * @param userid    用户ID
     * @param message   推送信息
     */
    public void sendMessage(String userid, String message){
        template.convertAndSendToUser(userid, MESSAGE_PATH, message);
    }

    /**
     * 推送信息到多用户
     * @param userids   用户ID数组
     * @param message   推送信息
     */
    public void sendMessage(String[] userids, String message){
        for(String userid : userids){
            sendMessage(userid, message);
        }
    }

    /**
     * 推送信息到多用户
     * @param userids   用户ID列表
     * @param message   推送信息
     */
    public void sendMessage(List<String> userids, String message){
        userids.parallelStream().forEach(userid -> sendMessage(userid, message));
    }

    /**
     * 推送归档进度给指定用户
     * @param userid   用户ID
     * @param browseid  推送进度信息
     */
    public void refreshArchiveProgressBar(String userid, String browseid) {//通知用户刷新进度条
        template.convertAndSendToUser(userid, ARCHIVEBAR_PATH, browseid);
    }

    /**
     * 推送生成档号进度给指定用户
     * @param userid   用户ID
     * @param browseid  推送进度信息
     */
    public void refreshGenerateArchivecodeBar(String userid, String browseid) {//通知用户刷新进度条
        template.convertAndSendToUser(userid, GENERATEARCHIVEBAR_PATH, browseid);
    }

    /**
     *
     * @param userid
     * @param message
     */
    public void sendMessageBackupDatabase(String userid, String message) {//通知用户备份完毕
        template.convertAndSendToUser(userid, BACKUP_PATH, message);
    }

    /**
     *
     * @param userid   用户ID
     * @param message  推送内容
     */
    public void sendMessagerestoreAll(String userid, String message) {//通知用户备份完毕
        template.convertAndSendToUser(userid, RESTORE_PATH, message);
    }

}
