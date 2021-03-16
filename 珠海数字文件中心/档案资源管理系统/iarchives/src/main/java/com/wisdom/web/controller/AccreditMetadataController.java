package com.wisdom.web.controller;

/**
 * Created by SunK on 2020/5/6 0006.
 */

import com.wisdom.web.entity.*;
import com.wisdom.web.repository.AccreditRepository;
import com.wisdom.web.service.AccreditMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 授权元数据控制器
 */
@Controller
@RequestMapping(value = "/accreditMetadata")//metadataManagement
public class AccreditMetadataController {

    @Autowired
    AccreditMetadataService accreditMetadataService;

    @Autowired
    AccreditRepository accreditRepository;

    @RequestMapping(value = "/main")
    public String metadataManagement() {

        return "/inlet/accreditMetadata";
    }

    @RequestMapping(value = "/getMetadataTree")
    @ResponseBody
    public List<ExtNcTree> getMetadataTree() {
        return accreditMetadataService.getMetadataTree();
    }


    @RequestMapping(value = "/getByParentconfigid")
    @ResponseBody
    public List<ExtNcTree> getByParentconfigid(String parentid) {
        return accreditMetadataService.findByParentconfigid(parentid);
    }


    @RequestMapping(value = "/getByParentid")
    @ResponseBody
    public Page<Tb_accredit> getByParentid(int page, int limit, String condition, String
            operator, String content, String parentid, String sort) {
        Sort sortobj = WebSort.getSortByJson(sort);
        return accreditMetadataService.findBySearch(page, limit, condition, operator, content, parentid, sortobj);
    }


    @RequestMapping(value = "/addSystemConfig")
    @ResponseBody
    public ExtMsg addSystemConfig(@ModelAttribute("form") Tb_accredit accredit) {
        ExtMsg extMsg;
        String configid = accredit.getAid();
        //设置时间
        accredit.setPublishtime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        if (!"".equals(configid)) {//修改
            String parentconfigid = accredit.getParentid();
            //判断是否有父id
            if (!"".equals(parentconfigid)) {//需要保证在父节点下参数名称、参数值都是唯一
                if (accredit.getShortname().equals(accreditMetadataService.findByConfigid(configid).getShortname())) {//判断参数名称是否被修改
                    Tb_accredit tb_system_config = accreditMetadataService.saveSystemConfig(accredit);
                    extMsg = new ExtMsg(true, "修改成功", tb_system_config);
                } else {
                    if (accreditMetadataService.findByParentconfigidAndConfigcode(accredit.getParentid(),
                            accredit.getShortname()) != null) {//判断父节点下修改后的参数名称是否存在
                        extMsg = new ExtMsg(false, "修改失败,该参数类型下已存在'" + accredit.getShortname() + "'相同的参数名称",
                                null);
                    } else {
                        Tb_accredit tb_system_config = accreditMetadataService.saveSystemConfig(accredit);
                        extMsg = new ExtMsg(true, "修改成功", tb_system_config);
                    }
                }
            } else {//需要保证参数名称唯一
                if (accredit.getShortname().equals(accreditMetadataService.findByConfigid(configid).getShortname())) {//判断参数名称是否被修改
                    accredit.setParentid(null);
                    Tb_accredit tb_system_config = accreditMetadataService.saveSystemConfig(accredit);
                    extMsg = new ExtMsg(true, "修改成功", tb_system_config);
                } else {
                    if (accreditMetadataService.findByConfigcode(accredit.getShortname()) != null) {//判断修改后的参数名称是否存在
                        extMsg = new ExtMsg(false, "修改失败,该参数类型下已存在'" + accredit.getShortname() + "'相同的参数名称",
                                null);
                    } else {
                        accredit.setParentid(null);
//                        accredit.setAid(null);
                        Tb_accredit tb_system_config = accreditMetadataService.saveSystemConfig(accredit);
                        extMsg = new ExtMsg(true, "修改成功", tb_system_config);
                    }
                }
            }
        } else {//新增
            Integer maxSequence;
            if (accredit.getParentid() == null || "".equals(accredit.getParentid().trim())) {
                maxSequence = accreditRepository.findMaxSequenceByParentidOrNull(accredit.getParentid());
            } else {
                maxSequence = accreditRepository.findMaxSequenceByParentid(accredit.getParentid());
            }
            accredit.setSortsequence(maxSequence == null ? 0 : maxSequence + 1);//设置顺序
            //需要保证参数名称是唯一
            if ("".equals(accredit.getParentid())) {
                if (accreditMetadataService.findByConfigcode(accredit.getShortname()) != null) {//判断参数名称是否存在
                    extMsg = new ExtMsg(false, "新增失败,该参数类型下已存在'" + accredit.getShortname() + "'相同的参数名称", null);
                } else {
                    accredit.setParentid(null);
                    Tb_accredit tb_system_config = accreditMetadataService.saveSystemConfig(accredit);
                    extMsg = new ExtMsg(true, "新增成功", tb_system_config);
                }
            } else {
                //需要保证在父节点下参数名称是唯一
                if (accreditMetadataService.findByParentconfigidAndConfigcode(accredit.getParentid(),
                        accredit.getShortname()) != null) {//判断父节点下参数名称是否存在
                    extMsg = new ExtMsg(false, "新增失败,该参数类型下已存在'" + accredit.getShortname() + "'相同的参数名称", null);
                } else {
                    Tb_accredit tb_system_config = accreditMetadataService.saveSystemConfig(accredit);
                    extMsg = new ExtMsg(true, "新增成功", tb_system_config);
                }
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
        String parentconfigid = accreditRepository.findByAid(configid[0]) == null ? null : accreditRepository
                .findByAid(configid[0]).getParentid();
        //判断是否存在父ID,若存在，判断父级是否被Tb_data_template使用
        if (parentconfigid != null) {
//            if (templateService.findByFenums(systemConfigService.findByConfigid(parentconfigid).getValue()).size() > 0) {
//                extMsg = new ExtMsg(false, "删除失败,该参数值的父级已在‘模版维护’中使用", null);
//            } else {
            if (accreditMetadataService.deleteByAidIn(configid) > 0) {
                successid += configids;
                extMsg = new ExtMsg(true, "删除成功", successid);
            } else {
                extMsg = new ExtMsg(false, "删除失败", null);
            }
//            }
        } else {
            String[] msgs = new String[configid.length];
            for (int i = 0; i < configid.length; i++) {
                String code = accreditMetadataService.findByConfigid(configid[i]).getShortname();
//                if (templateService.findByFenums(systemConfigService.findByConfigid(configid[i]).getValue()).size() >
//                        0) {
//                    msgs[i] = "'"+code+"'" + "删除失败,该参数值已在‘模版维护’中使用";
//                } else {
                if (accreditMetadataService.deleteByAid(configid[i]) > 0) {
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
}
