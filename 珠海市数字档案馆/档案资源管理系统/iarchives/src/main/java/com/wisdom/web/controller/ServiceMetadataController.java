package com.wisdom.web.controller;

/**
 * Created by SunK on 2020/5/9 0009.
 */

import com.wisdom.web.entity.*;
import com.wisdom.web.repository.ServiceConfigRepository;
import com.wisdom.web.service.AuditService;
import com.wisdom.web.service.CaptureMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 业务元数据控制器
 */
@Controller
@RequestMapping(value = "/serviceMetadata")
public class ServiceMetadataController {

//著录、归档、移交、入库、导入、导出、开发、销毁鉴定、借阅利用

    @Autowired
    CaptureMetadataService captureMetadataService;
    @Autowired
    ServiceConfigRepository serviceConfigRepository;
    @Autowired
    AuditService auditService;

    @RequestMapping(value = "/main")
    public String metadataManagement() {

        return "/inlet/serviceMetadata";
    }


    /**
     * @param entryids 条目id数组
     * @param module   模块名
     */
    @RequestMapping(value = "/captureServiceMetadataByZL")
    @ResponseBody
    public ExtMsg captureServiceMetadata(String[] entryids, String module, String operation) {
        int r = captureMetadataService.captureServiceMetadataByZL(entryids, module, operation);
        if (r > 0) {
            return new ExtMsg(true, "采集业务元数据成功", r);
        }
        return new ExtMsg(false, "采集业务元数据失败", 0);
    }


    /**
     * 入库
     *
     * @param docid  条目id数组
     * @param module 模块名
     */
    @RequestMapping(value = "/captureServiceMetadataByRK")
    @ResponseBody
    public ExtMsg captureServiceMetadataRK(String docid, String module, String operation) {
        if ("".equals(docid) || null == docid) {
            return new ExtMsg(false, "采集业务元数据失败", 0);
        }
        String[] entryids = auditService.getEntryidsByDocid(docid);
        int r = captureMetadataService.captureServiceMetadataByZL(entryids, module, operation);
        if (r > 0) {
            return new ExtMsg(true, "采集业务元数据成功", r);
        }
        return new ExtMsg(false, "采集业务元数据失败", 0);
    }



    /**
     * @param entryids 条目id
     * @param module   模块名
     */
    @RequestMapping(value = "/captureServiceMetadataBy")
    @ResponseBody
    public ExtMsg captureServiceMetadata(String entryids, String module, String operation,
                                           String nodeid,String condition,String operator,String content) {
        if ("".equals(entryids) || null == entryids) {
            return new ExtMsg(false, "采集业务元数据失败", 0);
        }
        if ("".equals(nodeid) || null == nodeid) {
            return new ExtMsg(false, "采集业务元数据失败", 0);
        }
        String[] ids = captureMetadataService.getAllId(entryids,nodeid,condition,operator,content);
        int r = captureMetadataService.captureServiceMetadataByZL(ids, module, operation);
        if (r > 0) {
            return new ExtMsg(true, "采集业务元数据成功", r);
        }
        return new ExtMsg(false, "采集业务元数据失败", 0);
    }



    /** 移交
     * @param module   模块名
     */
    @RequestMapping(value = "/captureServiceMetadataByYJ")
    @ResponseBody
    public ExtMsg captureServiceMetadataYJ(String module, String operation, String transdocid, HttpServletRequest request) {
        String[] entryidData = (String[])request.getSession().getAttribute("choiceEntryIds");//获取最终选择的非卷内条目;
        Integer r = captureMetadataService.captureServiceMetadataByZL(entryidData, module, operation);
        List<String> jnEntryIds=(List<String>)request.getSession().getAttribute("jnEntryIds");//所有的卷内条目
        entryidData=new String[jnEntryIds.size()];
        jnEntryIds.toArray(entryidData);
        captureMetadataService.captureServiceMetadataByZL(entryidData, module, operation);
        request.getSession().setAttribute("choiceEntryIds",new String[]{""});
        request.getSession().setAttribute("jnEntryIds",new ArrayList<>());
        if (r > 0) {
            return new ExtMsg(true, "采集业务元数据成功", r);
        }
        return new ExtMsg(false, "采集业务元数据失败", 0);
    }

    @RequestMapping(value = "/getByParentconfigid")
    @ResponseBody
    public List<ExtNcTree> getByParentid(String parentid) {
        return captureMetadataService.findByParentconfigid(parentid);
    }


    @RequestMapping(value = "/getByParentid")
    @ResponseBody
    public Page<Tb_service_config> getByParentid(int page, int limit, String condition, String
            operator, String content, String parentid, String sort) {
        Sort sortobj = WebSort.getSortByJson(sort);
        return captureMetadataService.findBySearch(page, limit, condition, operator, content, parentid, sortobj);
    }


    @RequestMapping(value = "/addSystemConfig")
    @ResponseBody
    public ExtMsg addSystemConfig(@ModelAttribute("form") Tb_service_config service_config) {
        ExtMsg extMsg = new ExtMsg();
        String cid = service_config.getCid();
        if (!"".equals(cid)) {//修改
            String parentconfigid = service_config.getParentid();
            //判断是否有父id
            if (!"".equals(parentconfigid)) {//需要保证在父节点下参数名称是唯一
                if (captureMetadataService.countByParentidAndOperation(parentconfigid, service_config.getOperation()) == 0) {//判断业务行为是否唯一
                    Tb_service_config tb_service_config = captureMetadataService.saveServiceConfig(service_config);
                    extMsg = new ExtMsg(true, "修改成功", tb_service_config);

                } else if (service_config.getCid().equals(captureMetadataService.findByParentidAndOperation(parentconfigid, service_config.getOperation()).getCid())) {
                    Tb_service_config tb_service_config = captureMetadataService.saveServiceConfig(service_config);
                    extMsg = new ExtMsg(true, "修改成功", tb_service_config);
                } else {
                    extMsg = new ExtMsg(false, "修改失败,该参数类型下已存在'" + service_config.getOperation() + "'相同的参数值",
                            null);
                }
            } else {
                service_config.setParentid(null);
                if (captureMetadataService.countByOperationAndParentidIsNull(service_config.getOperation()) == 0) {
                    Tb_service_config tb_service_config = captureMetadataService.saveServiceConfig(service_config);
                    extMsg = new ExtMsg(true, "修改成功", tb_service_config);
                } else if (service_config.getCid().equals(captureMetadataService.findByOperationAndParentidIsNull(service_config.getOperation()).getCid())) {
                    Tb_service_config tb_service_config = captureMetadataService.saveServiceConfig(service_config);
                    extMsg = new ExtMsg(true, "修改成功", tb_service_config);
                } else {
                    extMsg = new ExtMsg(false, "修改失败,该参数类型下已存在'" + service_config.getOperation() + "'相同的参数值",
                            null);
                }
            }
        } else {//新增
            Integer maxSequence;
            if (service_config.getParentid() == null || "".equals(service_config.getParentid().trim())) {
                maxSequence = serviceConfigRepository.findMaxSequenceByParentidOrNull(service_config.getParentid());
            } else {
                maxSequence = serviceConfigRepository.findMaxSequenceByParentid(service_config.getParentid());
            }
            service_config.setSortsequence(maxSequence == null ? 0 : maxSequence + 1);//设置顺序
            if (captureMetadataService.countByParentidAndOperation(service_config.getParentid(), service_config.getOperation()) == 0) {//判断业务行为是否唯一
                if ("".equals(service_config.getParentid())) {
                    service_config.setParentid(null);
                }
                Tb_service_config tb_service_config = captureMetadataService.saveServiceConfig(service_config);
                extMsg = new ExtMsg(true, "新增成功", tb_service_config);
            } else {
                extMsg = new ExtMsg(false, "增加失败,该参数类型下已存在'" + service_config.getOperation() + "'相同的参数值",
                        null);
            }

        }
        return extMsg;
    }

    @RequestMapping(value = "/deletAccredit/{configids}", method = RequestMethod.DELETE)
    @ResponseBody
    public ExtMsg deleteConfig(@PathVariable String configids) {
        ExtMsg extMsg;
        String successid = "";
        String[] configid = configids.split(",");
        String parentconfigid = serviceConfigRepository.findByCid(configid[0]) == null ? null : serviceConfigRepository
                .findByCid(configid[0]).getParentid();
        //判断是否存在父ID,若存在，判断父级是否被Tb_data_template使用
        if (parentconfigid != null) {
//            if (templateService.findByFenums(systemConfigService.findByConfigid(parentconfigid).getValue()).size() > 0) {
//                extMsg = new ExtMsg(false, "删除失败,该参数值的父级已在‘模版维护’中使用", null);
//            } else {
            if (captureMetadataService.deleteByCidIn(configid) > 0) {
                successid += configids;
                extMsg = new ExtMsg(true, "删除成功", successid);
            } else {
                extMsg = new ExtMsg(false, "删除失败", null);
            }
//            }
        } else {
            String[] msgs = new String[configid.length];
            for (int i = 0; i < configid.length; i++) {
                String code = captureMetadataService.findByConfigid(configid[i]).getOperation();
//                if (templateService.findByFenums(systemConfigService.findByConfigid(configid[i]).getValue()).size() >
//                        0) {
//                    msgs[i] = "'"+code+"'" + "删除失败,该参数值已在‘模版维护’中使用";
//                } else {
                if (captureMetadataService.deleteByCid(configid[i]) > 0) {
                    msgs[i] = "'" + code + "'删除成功";
                    successid += (configid[i] + ",");
                } else {
                    msgs[i] = "'" + code + "'删除失败";
                }
//                }
            }
            String msg = "";
            for (String m : msgs) {
                if (m != null) {
                    msg += m + ";<br />";
                }
            }
            extMsg = new ExtMsg(true, msg, successid);
        }
        return extMsg;
    }

    @RequestMapping("/queryName")
    @ResponseBody
    public List<Tb_system_config> queryConditionTemplate(String metadataType) {
        List<Tb_system_config> list = captureMetadataService.queryConditionTemplate(metadataType);
        return list;
    }


    @RequestMapping("/queryAccreditName")
    @ResponseBody
    public List<Tb_accredit> queryAccreditName() {
        List<Tb_accredit> list = captureMetadataService.queryAccreditTemplate();
        return list;
    }


    @RequestMapping(value = "/testAdd/{treeid}", method = RequestMethod.POST)
    @ResponseBody
    public ExtMsg testAdd(@PathVariable String treeid) {
        ExtMsg extMsg;
        String parentconfigid = captureMetadataService.findByConfigid(treeid) == null ? null : captureMetadataService.findByConfigid(treeid).getParentid();
        //如果不存在父ID，允许新增
        if (parentconfigid == null) {
            extMsg = new ExtMsg(true, "允许新增", null);
        } else {
            extMsg = new ExtMsg(false, "只允许三层结构，不能新增", null);
        }
        return extMsg;
    }

    @RequestMapping(value = "/getuserList")
    @ResponseBody
    public List<Tb_user> getuserList(String userid){
        return captureMetadataService.getuserList(userid);
    }

}
