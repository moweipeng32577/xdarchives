package com.wisdom.web.service;

import com.wisdom.web.entity.Tb_data_node;
import com.wisdom.web.repository.DataNodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by RonJiang on 2017/12/5 0005.
 */
@Service
@Transactional
public class PublicUtilService {

    @Autowired
    private DataNodeRepository dataNodeRepository;

    public String getNodeid(String parentid){
        String result = "2c9ae731604d4f2501604db99d0d0007";
        if(parentid == null){
            return result;
        }

        Tb_data_node node = dataNodeRepository.findByNodeid(parentid);
        String organid = node.getOrganid();
        while(node.getNodetype() == 1){
            node = dataNodeRepository.findParentnodeByNodeid(node.getNodeid());
        }
        Tb_data_node innernode = dataNodeRepository.findByClasslevelAndParentnodeid(1,node.getParentnodeid());
        if(innernode != null){
            Tb_data_node resultnode = dataNodeRepository.findByClasslevelAndClassidAndOrganid(1,innernode.getClassid(),organid);
            result = resultnode.getNodeid();
        }else{
            List<Tb_data_node> list;
            String pid = dataNodeRepository.findParentnodeidByNodeid(parentid);
            if (pid == null || "".equals(pid.trim())) {
                list = dataNodeRepository.findByParentnodeidIsNullOrParentnodeid("");
            } else {
                list = dataNodeRepository.findByParentnodeid(pid);
            }
            for (Tb_data_node dn : list){
                if(dn.getNodename().equals("卷内文件")){
                    return dn.getNodeid();
                }
            }
        }

        return result;
    }


    public String getFileNodeid(String nodeid){
        Tb_data_node innernode = dataNodeRepository.findByNodeid(nodeid);
        String organid = innernode.getOrganid();
        while(innernode.getNodetype() == 1){
            innernode = dataNodeRepository.findParentnodeByNodeid(innernode.getNodeid());
        }
        Tb_data_node filenode = dataNodeRepository.findByClasslevelAndParentnodeid(2,innernode.getParentnodeid());
        Tb_data_node resultnode = dataNodeRepository.findByClasslevelAndClassidAndOrganid(2,filenode.getClassid(),organid);
        if(resultnode==null){//根据卷内文件的父节点去获取相应的案卷中的单位节点
            filenode =dataNodeRepository.findParentnodeByNodeid(innernode.getNodeid());
            resultnode = dataNodeRepository.findByClasslevelAndClassidAndOrganid(2,filenode.getClassid(),organid);
        }
        return resultnode.getNodeid();
    }

    public String getJnNodeid(String parentid){
        String result = "2c9ae731604d4f2501604db99d0d0007";
        if(parentid == null){
            return result;
        }

        Tb_data_node node = dataNodeRepository.findByNodeid(parentid);
        String organid = node.getOrganid();
        while(node.getNodetype() == 1){
            node = dataNodeRepository.findParentnodeByNodeid(node.getNodeid());
        }
        Tb_data_node innernode = dataNodeRepository.findByClasslevelAndParentnodeid(1,node.getParentnodeid());
        if(innernode != null){
            Tb_data_node resultnode = dataNodeRepository.findByClasslevelAndClassidAndOrganid(1,innernode.getClassid(),organid);
            result = resultnode.getNodeid();
        }else{
            List<Tb_data_node> list;
            String pid = dataNodeRepository.findParentnodeidByNodeid(parentid);
            if (pid == null || "".equals(pid.trim())) {
                list = dataNodeRepository.findByParentnodeidIsNullOrParentnodeid("");
            } else {
                list = dataNodeRepository.findByParentnodeid(pid);
            }
            for (Tb_data_node dn : list){
                if(dn.getNodename().equals("卷内文件")){
                    String jnNodecode=dn.getNodecode();
                    List<Tb_data_node> jnList=dataNodeRepository.findByNodecodeAndOrganid(jnNodecode,organid);
                    if(!jnList.isEmpty()){
                        return jnList.get(0).getNodeid();
                    }

                }
            }
        }

        return result;
    }
}
