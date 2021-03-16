package com.wisdom.web.controller;

import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.entity.Szh_media_metadata;
import com.wisdom.web.service.MetadataAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 流水线管理
 */
@Controller
@RequestMapping(value = "/metadataAdmin")
public class MetadataAdminController {

	@Autowired
	MetadataAdminService metadataAdminService;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@RequestMapping("/main")
	public String main(Model model, String isp) {
		return "/inlet/metadataAdmin";
	}

	/**
	 * 分页获取元数据信息
	 * @param page 页码
	 * @param limit 每页数
	 * @param condition 查询字段
	 * @param operator  条件
	 * @param content 查询字段值
	 * @return
	 */
	@RequestMapping("/getMetadataBySearch")
	@ResponseBody
	public Page<Szh_media_metadata> getMetadataBySearch(int page, int limit, String sort, String condition, String operator,	String content){
		return metadataAdminService.getMetadataBySearch(page,limit,sort,condition,operator,content);
	}

	/**
	 * 获取元数据
	 * @param id 主键
	 * @return
	 */
	@RequestMapping("/getMetadataForm")
	@ResponseBody
	public ExtMsg getMetadataForm(String id){
		return new ExtMsg(true,"",metadataAdminService.getMetadataForm(id));
	}

	/**
	 * 新增/修改元数据
	 * @param metadata 元数据
	 * @return
	 */
	@RequestMapping("/metadataFormSubmit")
	@ResponseBody
	public ExtMsg metadataFormSubmit(Szh_media_metadata metadata){
		boolean state = metadataAdminService.metadataFormSubmit(metadata);
		return new ExtMsg(state,"",null);
	}

	/**
	 * 删除元数据
	 * @param ids 元数据id数组
	 * @return
	 */
	@RequestMapping("/delMetadatas")
	@ResponseBody
	public ExtMsg delMetadatas(String[] ids){
		boolean state = metadataAdminService.delMetadatas(ids);
		return new ExtMsg(state,"",null);
	}
}