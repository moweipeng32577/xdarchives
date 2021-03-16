package com.wisdom.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.wisdom.secondaryDataSource.entity.*;
import com.wisdom.secondaryDataSource.entity.Tb_data_node_sx;
import com.wisdom.web.entity.*;
import com.wisdom.web.service.EntryIndexService;
import com.wisdom.web.service.NodesettingService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/6/27.
 */
@Controller
@RequestMapping(value = "/classifySearchDirectory")
public class ClassifySearchDirectoryController {


    @Autowired
    NodesettingService nodesettingService;

    @Autowired
    EntryIndexService entryIndexService;

    @Value("${system.report.server}")
    private String reportServer;//报表服务

    @Value("${system.instantSearch.opened}")
    private String instantSearch;//判断是否开启即时搜索

    @Value("${system.loginType}")
    private String systemLoginType;//登录系统设置  政务网1  局域网0

    @RequestMapping("/main")
    public String index(Model model,String flag) {
        model.addAttribute("buttonflag", flag);
        model.addAttribute("reportServer",reportServer);
        model.addAttribute("systemLoginType",systemLoginType);
        return "/inlet/classifySearchDirectory";
    }

    /**
     * 目录高级检索，不使用框架自带的json转换，避免循环引用
     */
    @RequestMapping("/findByClassifySearch")
    public void findByClassifySearch(String datasoure,int page, int start, int limit, String condition, String operator, String content, Tb_index_detail_manage formConditions,Tb_index_detail detailformConditions,Tb_entry_index_sx sxformConditions, ExtOperators formOperators, ExtDateRangeData daterangedata, String logic, String nodeids, HttpServletResponse httpServletResponse, String sort) {
        Sort sortobj1 = WebSort.getSortByJson(sort);
        Page result = entryIndexService.getEntrybaseManage(datasoure,nodeids, condition, operator, content, formConditions,detailformConditions,sxformConditions, formOperators, daterangedata, logic, page, limit, sortobj1);
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("utf-8");
        String json = JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect);
        try {
            httpServletResponse.getWriter().write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**目录检索
     *   生成数据节点全名，转换分页结果
     * @param result
     * @param pageRequest
     * @return
     */
    public Page<Tb_index_detail_manage> convertManageNodefullnameAll(Page<Tb_index_detail_manage> result,PageRequest pageRequest){
        List<Tb_index_detail_manage> content = result.getContent();
        long totalElements = result.getTotalElements();
        List<Tb_index_detail_manage> returnResult = new ArrayList<>();
        Map<String,Object[]> parentmap = nodesettingService.findAllParentOfNode();
        for(Tb_index_detail_manage entryIndex:content){
            Tb_index_detail_manage entry_index = new Tb_index_detail_manage();
            BeanUtils.copyProperties(entryIndex,entry_index);
            String nodeid = entry_index.getNodeid();
            Tb_data_node node = (Tb_data_node)parentmap.get(nodeid)[0];
            entry_index.setTdn(node);
            List<Tb_data_node> parents = (List<Tb_data_node>)parentmap.get(nodeid)[1];
            if (node.getNodename() != null && !"".equals(node.getNodename())) {
                StringBuffer nodefullname = new StringBuffer(node.getNodename());
                for(Tb_data_node parent : parents){
                    if(parent == null){
                        continue;
                    }
                    nodefullname.insert(0, "_");
                    nodefullname.insert(0, parent.getNodename());
                }
                entry_index.setNodefullname(nodefullname.toString());
            }
            returnResult.add(entry_index);
        }
        return new PageImpl(returnResult,pageRequest,totalElements);
    }

    /**目录检索
     *   生成数据节点全名，转换分页结果
     * @param result
     * @return
     */
    public List<Tb_entry_index_sx> convertSxNodefullnameAll(List<Tb_entry_index_sx> result){
        List<Tb_entry_index_sx> returnResult = new ArrayList<>();
        Map<String,Object[]> parentmap = nodesettingService.findAllSecondaryParentOfNode();
        for(Tb_entry_index_sx entryIndex : result){
            Tb_entry_index_sx entry_index = new Tb_entry_index_sx();
            BeanUtils.copyProperties(entryIndex,entry_index);
            String nodeid = entry_index.getNodeid();
            if(parentmap.get(nodeid)!=null) {
                Tb_data_node_sx node = (Tb_data_node_sx) parentmap.get(nodeid)[0];
                entry_index.setTdn(node);
                List<Tb_data_node_sx> parents = (List<Tb_data_node_sx>) parentmap.get(nodeid)[1];
                if (node.getNodename() != null && !"".equals(node.getNodename())) {
                    StringBuffer nodefullname = new StringBuffer(node.getNodename());
                    for (Tb_data_node_sx parent : parents) {
                        if (parent == null) {
                            continue;
                        }
                        nodefullname.insert(0, "_");
                        nodefullname.insert(0, parent.getNodename());
                    }
                    entry_index.setNodefullname(nodefullname.toString());
                }
                returnResult.add(entry_index);
            }
        }
        return returnResult;
    }
}
