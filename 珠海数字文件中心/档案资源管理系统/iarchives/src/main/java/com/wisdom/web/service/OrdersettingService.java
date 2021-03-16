package com.wisdom.web.service;

import com.wisdom.web.entity.Tb_orderset;
import com.wisdom.web.repository.OrdersetRepository;
import com.wisdom.web.repository.TemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanly on 2017/11/3 0003.
 */
@Service
@Transactional
public class OrdersettingService {

    @Autowired
    TemplateRepository templateRepository;

    @Autowired
    OrdersetRepository ordersetRepository;

    public List<Tb_orderset> findOrdersetByDatanodeid(String datanodeid){
        return ordersetRepository.findByDatanodeidOrderByOrdernum(datanodeid);
    }

    public String[] getOrdersetFieldcodeByNodeid(String datanodeid){
        List<Tb_orderset> codesetList = findOrdersetByDatanodeid(datanodeid);
        List<String> ordersetFieldcodeList = new ArrayList<>();
        for(Tb_orderset orderset:codesetList){
            ordersetFieldcodeList.add(orderset.getFieldcode());
        }
        String[] ordersetArr = new String[ordersetFieldcodeList.size()];
        ordersetFieldcodeList.toArray(ordersetArr);
        return ordersetArr;
    }

    public String setCode(String datanodeid, String[] fieldcodelist){
        List<Tb_orderset> ordersetList = new ArrayList<Tb_orderset>();
        String orderTxt="";
        String fieldname="";
        String direction="";
        for(int i=0; i<fieldcodelist.length;i++){
            String[] fieldcode_split=fieldcodelist[i].split("∪");
            Tb_orderset orderset=new Tb_orderset();
            if("".equals(fieldcode_split[0])){//add
                orderset.setOrdernum(i+1);
                orderset.setDatanodeid(datanodeid);
                orderset.setFieldcode(fieldcode_split[1]);
                orderset.setFieldname(fieldcode_split[2]);
                fieldname=fieldcode_split[2];
                orderset.setDirection(fieldcode_split[3]);
                direction=fieldcode_split[3];
                ordersetList.add(orderset);
            }else{
                orderset.setOrdernum(i+1);
                orderset.setDatanodeid(datanodeid);
                orderset.setFieldcode(fieldcode_split[3]);
                orderset.setFieldname(fieldcode_split[1]);
                fieldname=fieldcode_split[1];
                orderset.setDirection(fieldcode_split[2]);
                direction=fieldcode_split[2];
                ordersetList.add(orderset);
            }
            if("1".equals(direction.trim())){//1是降序
                orderTxt+=fieldname+"_倒序+";
            }else{//默认升序
                orderTxt+=fieldname+"_正序+";
            }
        }
        /*String lastFieldcode = ordersetList.get(ordersetList.size()-1).getFieldcode();
        String lastFieldcodeType = templateRepository.findFtypeByFieldcodeAndNodeid(lastFieldcode,datanodeid);
        if(!"calculation".equals(lastFieldcodeType)){
            return new ExtMsg(false,"档号设置最后一个字段必须为统计型,请检查该字段模板设置是否正确",null);
        }*/
        List<Tb_orderset> oldList=ordersetRepository.findByDatanodeidOrderByOrdernum(datanodeid);
        ordersetRepository.delete(oldList); //删除节点下所有codeset
        ordersetRepository.save(ordersetList);
        return orderTxt.substring(0,orderTxt.lastIndexOf("+"));//去掉最后的加号
    }

    /**
     * 获取归档排序内容 当前归档顺序：流水号_升序+文件件编号_升序+文件时间_降序
     * @param datanodeid
     * @return
     */
    public String getOrderTxt(String datanodeid,String type){
        List<Tb_orderset> ordersetList=ordersetRepository.findByDatanodeidOrderByOrdernum(datanodeid);
        String orderTxt="";
        if(ordersetList.size()>0){
            for(Tb_orderset orderset:ordersetList){
                String fieldname=orderset.getFieldname();
                String direction=orderset.getDirection();
                if("1".equals(direction)){//1是降序
                    orderTxt+=fieldname+"_倒序+";
                }else{//默认升序
                    orderTxt+=fieldname+"_正序+";
                }
            }
            return orderTxt.substring(0,orderTxt.lastIndexOf("+"));//去掉最后的加号
        }else{
            if("management".equals(type)){
                return "文件流水号_正序";
            }else{
                return "文件日期_正序";
            }
        }
    }

    /**
     *  获取档号设置字段名
     * @param nodeid 节点ID
     * @return
     */
    public List<String> getOrderSettingFields(String nodeid){
        return ordersetRepository.findFieldcodeByDatanodeid(nodeid);
    }


    public void deleteOrdersettingByNodeid(String nodeid){
        List<Tb_orderset> ordersetList = ordersetRepository.findByDatanodeidOrderByOrdernum(nodeid);
        ordersetRepository.delete(ordersetList);
    }

    public List<Tb_orderset> SaveOrderset(List<Tb_orderset> orderset){
        return ordersetRepository.save(orderset);
    }

    public void deleteOrdersetByNodeid(String nodeid){
        List<Tb_orderset> ordersetList = ordersetRepository.findByDatanodeidOrderByOrdernum(nodeid);
        ordersetRepository.delete(ordersetList);
    }


}
