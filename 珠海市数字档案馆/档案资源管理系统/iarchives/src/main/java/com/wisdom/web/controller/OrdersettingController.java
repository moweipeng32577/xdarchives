package com.wisdom.web.controller;

import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.entity.Tb_data_template;
import com.wisdom.web.entity.Tb_orderset;
import com.wisdom.web.entity.Tb_user_fillsort;
import com.wisdom.web.repository.DataNodeRepository;
import com.wisdom.web.repository.UserFillSortRepository;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.EntryIndexService;
import com.wisdom.web.service.OrdersettingService;
import com.wisdom.web.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 档号设置控制器 Created by tanly on 2017/10/24 0024.
 */
@Controller
@RequestMapping(value = "/ordersetting")
public class OrdersettingController {

	@Autowired
	OrdersettingService ordersettingService;

	@Autowired
    TemplateService templateService;

	@Autowired
    EntryIndexService entryIndexService;

	@Autowired
    DataNodeRepository dataNodeRepository;

	@Autowired
	UserFillSortRepository userFillSortRepository;


	@RequestMapping("/getTemplateField")
	@ResponseBody
	public List<Tb_data_template> getTemplateField(String datanodeid) {
		List<Tb_data_template> data_templatelists = templateService.findPartialTemplateByNodeidAndExclude(datanodeid,
				new String[] { "archivecode", "fscount", "kccount" });
		List<Tb_orderset> ordersetlists = ordersettingService.findOrdersetByDatanodeid(datanodeid);

		Map<Long, Tb_data_template> map = new TreeMap<>();
		for (int i = 0; i < data_templatelists.size(); i++) {
			Tb_data_template data_template = data_templatelists.get(i);
			String oldFieldcode = data_template.getFieldcode();
			String oldFieldname = data_template.getFieldname();
			boolean flag = false;
			for (Tb_orderset orderset : ordersetlists) {
				if (data_template.getFieldcode().equals(orderset.getFieldcode())) {
					data_template.setFieldcode(orderset.getOrderid() + "∪" + oldFieldname + "∪" + orderset.getDirection()
							+ "∪" + oldFieldcode);
					String fieldname="";
					if("0".equals(orderset.getDirection())){//升序
						fieldname=oldFieldcode + "_" + oldFieldname+" ↓";
					}else{//降序
						fieldname=oldFieldcode + "_" + oldFieldname+" ↑";
					}
					data_template.setFieldname(fieldname);
					map.put(orderset.getOrdernum(), data_template);
					data_templatelists.remove(data_template);// 删除orderset对应字段，利用treemap排序
					i--;// 删除对象，索引前移
					flag = true;// 找到
					break;
				}
			}
			if (!flag) {
				data_template.setFieldcode("∪" + oldFieldcode + "∪" + oldFieldname + "∪0∪4");// 默认长度为4
				data_template.setFieldname(oldFieldcode + "_" + oldFieldname);
			}
		}
		Iterator<Long> iter = map.keySet().iterator();
		while (iter.hasNext()) {
			data_templatelists.add(map.get(iter.next()));// 接到data_templatelists后面
		}
		return data_templatelists;
	}

	@RequestMapping("/setCode")
	@ResponseBody
	public ExtMsg setCode(String datanodeid, String[] fieldcodelist) {
		String result = ordersettingService.setCode(datanodeid, fieldcodelist);
		return new ExtMsg(true, result, null);
	}

	/**
	 * 获取归档排序内容 当前归档顺序：流水号_升序+文件件编号_升序+文件时间_降序
	 * @param nodeid  未归节点
	 * @return
	 */
	@RequestMapping("/getOrderTxt")
	@ResponseBody
	public ExtMsg getOrderTxt(String nodeid,String type) {
		String result = ordersettingService.getOrderTxt(nodeid,type);
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
		//是否有修改归档排序权限
		List<Tb_user_fillsort> userFillsorts = userFillSortRepository.findByUserid(userDetails.getUserid());
		String data = "";
		if(userFillsorts.size()>0){
			data = "true";
		}else{
			data = "false";
		}
		return new ExtMsg(true, result, data);
	}

	@RequestMapping("/getOrderSettingFields")
	@ResponseBody
	public ExtMsg getOrderSettingFields(String nodeid) {
		List<String> orderSettingFields = ordersettingService.getOrderSettingFields(nodeid);
		if (orderSettingFields.size() > 0) {
			return new ExtMsg(true, "获取档号设置字段成功", orderSettingFields);
		}
		return new ExtMsg(false, "获取档号设置字段失败", null);
	}

	@RequestMapping("/deleteOrdersetByNodeid")
	@ResponseBody
	public ExtMsg deleteOrdersetByNodeid(String nodeid) {
		ordersettingService.deleteOrdersetByNodeid(nodeid);
		return new ExtMsg(true, "档号设置删除成功", null);
	}


}
