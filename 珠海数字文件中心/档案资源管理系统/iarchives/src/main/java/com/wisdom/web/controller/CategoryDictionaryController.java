package com.wisdom.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wisdom.util.LogAop;
import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.entity.ExtNcTree;
import com.wisdom.web.entity.Tb_category_dictionary;
import com.wisdom.web.entity.Tb_entry_index;
import com.wisdom.web.entity.Tb_entry_index_capture;
import com.wisdom.web.repository.EntryIndexCaptureRepository;
import com.wisdom.web.repository.EntryIndexRepository;
import com.wisdom.web.service.CategoryDictionaryService;

@Controller
@RequestMapping(value = "/categoryDictionary")
public class CategoryDictionaryController {
	
	@Autowired
    LogAop logAop;
	
	@Autowired
	CategoryDictionaryService categoryDictionaryService;
	
	@Autowired
	EntryIndexCaptureRepository entryRepository;
	
	@Autowired
	EntryIndexRepository entryIndexRepository;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("/main")
    public String index(){
        return "/inlet/categoryDictionary";
    }
    
    @RequestMapping("/getFilingyear")
    @ResponseBody
    public List<String> getFilingyear(){
    	return categoryDictionaryService.getFilingyear();
    }
    
    @RequestMapping("/getEntryretention")
    @ResponseBody
    public List<String> getEntryretention(){
    	return categoryDictionaryService.getEntryretention();
    }
    
    @RequestMapping("/getOrgan")
    @ResponseBody
    public List<String> getOrgan(){
    	return categoryDictionaryService.getOrgan();
    }
    
    /**
     * 获取左侧节点树
     * @return
     */
    @RequestMapping("/getCategoryDictionaryTree")
    @ResponseBody
    public List<ExtNcTree> getAppraisalStandardTree(){
        List<Tb_category_dictionary> categoryList = categoryDictionaryService.getCategoryDictionary();
        List<ExtNcTree> trees = new ArrayList<>();
        for(Tb_category_dictionary category : categoryList){
            ExtNcTree tree = new ExtNcTree();
            tree.setFnid(category.getCategoryid());
            tree.setLeaf(true);
            tree.setText(category.getName());
            trees.add(tree);
        }
        return trees;
    }
    
    /**
     * 通过分类id跟查询条件查找数据
     * @param categoryid
     * @param page
     * @param start
     * @param limit
     * @param condition
     * @param operator
     * @param content
     * @return
     */
    @RequestMapping("/getCategoryDictionaryBySearch")
    @ResponseBody
    public Page<Tb_category_dictionary> getCategoryDictionaryBySearch(String categoryid, int page, int start, int limit, String condition,String operator,String content) {
    	logger.info("categoryid:" + categoryid +";page:" + page + ";start:" + start + ";limt:" + limit);
        return categoryDictionaryService.findBySearch(categoryid,condition,operator,content,page,limit);
    }
    
    @RequestMapping("/addCategory")
    @ResponseBody
    public ExtMsg addCategory(Tb_category_dictionary category) {
    	if (categoryDictionaryService.saveCategory(category) != null) {
    		return new ExtMsg(true, "success", "添加成功");
    	}
    	return new ExtMsg(false, "failed", "字词已存在");
    }
    
    @RequestMapping("/modifyCategory")
    @ResponseBody
    public ExtMsg modifyCategory(Tb_category_dictionary category) {
    	String categoryid = category.getCategoryid();
    	int value = categoryDictionaryService.updateCategoryName(categoryid, category.getName(), category.getRemark());
    	if (value > 0) {
    		return new ExtMsg(true, "success", "修改成功");
    	}
		return new ExtMsg(false, "failed", "修改失败");
    }
    
    /**
     * 通过分类id删除分类信息
     * @param categoryid
     */
    @RequestMapping(value = "/deleteCategory/{categoryid}", method = RequestMethod.DELETE)
    @ResponseBody
    public ExtMsg deleteCategory(@PathVariable String categoryid) {
    	String[] id = categoryid.split(",");
    	Integer value = categoryDictionaryService.delCategory(id);
    	if (value > 0) {
    		return new ExtMsg(true, "success", "删除成功");
    	}
    	return new ExtMsg(false, "failed", "删除失败");
    }
    
    /**
     * 分类设置
     * @param entryid
     * @param year
     * @param retention
     * @param organ
     * @return
     */
    @RequestMapping("/setCategory")
    @ResponseBody
    public ExtMsg setCategory(String entryid, String year, String retention, String organ) {
    	String[] id = entryid.split(",");//取出所有的数据id
    	String value = organ.replace("/", "");
    	//批量设置属性
    	for (int i = 0; i < id.length; i++) {
    		entryRepository.updateYear(id[i], year);
    		entryRepository.updateRetention(id[i], retention);
    		entryRepository.updateOrgan(id[i], value);
    	}
		return new ExtMsg(true, "设置成功", null);
    }
    
    @RequestMapping("/autoSetCategory")
    @ResponseBody
    public ExtMsg autoSetCategory(String entryid, String type) {
    	String[] id = entryid.split(",");//取出所有的数据id
    	if (id.length > 0) {
    		return setEntryInfo(type, id);
    	}
    	return new ExtMsg(false, "请选择需要进行自动分类的数据", null);
    }
    
    private ExtMsg setEntryInfo(String type, String[] id) {
    	List<String> year = getFilingyear();
    	List<String> entryretention = getEntryretention();
    	List<String> organ = getOrgan();
    	if (year.size() > 0 && entryretention.size() > 0 && organ.size() > 0) {
			String title = "";
    		String eid = "";
			if ("数据采集".equals(type)) {
				List<Tb_entry_index_capture> list = entryRepository.findByEntryidIn(id);
				for (int i = 0; i < list.size(); i++) {
					Tb_entry_index_capture entry = (Tb_entry_index_capture) list.get(i);
					title = entry.getTitle();
	        		eid = entry.getEntryid();
	        		for (int j = 0; j < year.size(); j++) {
	        			if (title.contains(year.get(j))) {
	        				entryRepository.updateYear(eid, year.get(j));
	                	}
	        		}
	        		for (int j = 0; j < entryretention.size(); j++) {
	        			if (title.contains(entryretention.get(j))) {
	        				entryRepository.updateRetention(eid, entryretention.get(j));
	                	}
	        		}
	        		for (int j = 0; j < organ.size(); j++) {
	        			if (title.contains(organ.get(j))) {
	        				entryRepository.updateOrgan(eid, organ.get(j));
	                	}
	        		}
				}
			} else {
				List<Tb_entry_index> indexList = entryIndexRepository.findByEntryidIn(id);
				for (int i = 0; i < indexList.size(); i++) {
					Tb_entry_index entry = (Tb_entry_index) indexList.get(i);
    				title = entry.getTitle();
            		eid = entry.getEntryid();
            		for (int j = 0; j < year.size(); j++) {
            			if (title.contains(year.get(j))) {
            				entryRepository.updateYear(eid, year.get(j));
                    	}
            		}
            		for (int j = 0; j < entryretention.size(); j++) {
            			if (title.contains(entryretention.get(j))) {
            				entryRepository.updateRetention(eid, entryretention.get(j));
                    	}
            		}
            		for (int j = 0; j < organ.size(); j++) {
            			if (title.contains(organ.get(j))) {
            				entryRepository.updateOrgan(eid, organ.get(j));
                    	}
            		}
				}
			}
			
        	return new ExtMsg(true, "设置成功", null);
    	}
    	return new ExtMsg(false, "分类设置字典暂无数据", null);
    }
}