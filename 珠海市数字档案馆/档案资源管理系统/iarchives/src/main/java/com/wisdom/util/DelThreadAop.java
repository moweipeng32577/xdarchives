package com.wisdom.util;

import com.wisdom.web.service.EntryCaptureService;
import com.wisdom.web.service.EntryService;
import com.wisdom.web.service.InformService;
import com.wisdom.web.service.OrganService;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;

/**
 * aop 记录日志
 * 
 * @author wjh
 */
@Aspect
@Component
public class DelThreadAop {

	public static DelThreadAop delAop;

	@Autowired
    EntryService entryService;

	@Autowired
    EntryCaptureService entryCaptureService;

	@Autowired
	OrganService organService;

	@PostConstruct
	public void init() {
		delAop = this;
		delAop.entryService = this.entryService;
		delAop.entryCaptureService = this.entryCaptureService;
		delAop.organService = this.organService;
	}



	/**
	 * 删除文件
	 *
	 * @param module
	 *            模块名
	 * @param entries
	 *            条目id数组
	 */
	public static void delElectronic( String module, String[] entries) {
		List<String[]> subAry = new InformService().subArray(entries, 1000);// 处理ORACLE1000参数问题
		for (String[] ary : subAry) {
			//删除条目关联
			if("数据管理".equals(module)){
				delAop.entryService.delEntryRef(ary);
			}else{//数据采集
				delAop.entryCaptureService.delEntryRef(ary);
			}
		}
	}

	/**
	 * 删除机构关联数据
	 *
	 * @param organidArr
	 *             机构节点id数组
	 * @param nodeIds
	 *             档案数据节点id数组
	 *@param nodeidSxArr
	 *            声像数据节点id数组
	 */
	public static void delOrganRef(String[] organidArr, String[] nodeIds, String[] nodeidSxArr) {
		System.out.println("机构关联数据删除开始"+new Date());
		//删除档案机构关联数据
		delAop.organService.delDaOrganRef(organidArr, nodeIds);
		//删除声像机构关联数据
		delAop.organService.delSxOrganRef(organidArr, nodeidSxArr);
		System.out.println("机构关联数据删除完成"+new Date());
	}

}