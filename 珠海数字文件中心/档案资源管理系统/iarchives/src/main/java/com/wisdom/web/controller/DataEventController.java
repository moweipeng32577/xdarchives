package com.wisdom.web.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.wisdom.util.DBCompatible;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.entity.Tb_data_event;
import com.wisdom.web.entity.Tb_entry_index;
import com.wisdom.web.entity.Tb_event_entry;
import com.wisdom.web.entity.WebSort;
import com.wisdom.web.repository.DataEventRepository;
import com.wisdom.web.repository.DataNodeRepository;
import com.wisdom.web.repository.EntryIndexRepository;
import com.wisdom.web.repository.EventEntryRepository;
import com.wisdom.web.repository.UserDataNodeRepository;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.DataEventService;

/**
 * 档案关联控制器
 * @author Administrator
 *
 */
@Controller
@RequestMapping(value = "/dataEvent")
public class DataEventController {
	
	@Autowired
	DataEventService dataEventService;
	
	@Autowired
	DataEventRepository dataEventRepository;
	
	@Autowired
	DataNodeRepository dataNodeRepository;
	
	@Autowired
	UserDataNodeRepository userDataNodeRepository;
	
	@Autowired
	EventEntryRepository eventEntryRepository;
	
	@Autowired
	EntryIndexRepository entryIndexRepository;
	
	@RequestMapping("/main")
	public String acquisition() {
		return "/inlet/dataEvent";
	}
	
	@RequestMapping(value = "/eventExist")
	@ResponseBody
	public ExtMsg eventExist(String entryid) {
		String[] entry = entryid.split(",");
		List<String> event = eventEntryRepository.findByEntryidIn(entry);
		if (event.size() > 0) {
			return new ExtMsg(false, "当前条目已关联事件，不可创建新关联", null);
		}
		return new ExtMsg(true, "可创建新关联", null);
	}
	
	/**
	 * 查看已建立的关联事件
	 * @param
	 * @return
	 */
	@RequestMapping(value = "/lookDataEvent")
	@ResponseBody
	public Page<Tb_data_event> lookDataEvent(String eventid, String condition,String operator,String content,int page, int limit, String sort) {
		if(!"oracle".equals(DBCompatible.getInstance().getDBVersion())&&sort!=null&&sort.indexOf("eleid")!=-1){
			//eleid字段是字符串型 排序规则是按照ASCII码进行排序 需要进行转换
			sort=sort.replace("eleid","eleid*1");
		}else if("oracle".equals(DBCompatible.getInstance().getDBVersion())&&sort!=null&&sort.indexOf("eleid")!=-1){
			sort=sort.replace("eleid","eleid nulls last");
		}
		Sort sortobj = WebSort.getSortByJson(sort);
		Page<Tb_data_event> events = dataEventService.findBySearch(eventid, condition, operator, content, page, limit, sortobj);
		return events;
	}
	
	/**
	 * 通过事件id查找到该事件所有关联的条目信息
	 * @param eventid
	 * @return
	 */
	@RequestMapping(value = "/lookEventEntry")
	@ResponseBody
	public Page<Tb_entry_index> lookEventEntry(String eventid, String entryid, int page, int limit, String sort) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userid = userDetails.getUserid();
		if (eventid == null || "".equals(eventid)) {
			eventid = eventEntryRepository.findEventidByEntryid(entryid);
		}
		// 找到当前关联事件的所有条目信息
		List<String> entryList = eventEntryRepository.findEntryidByEventId(eventid);
		// 查找到关联事件条目的所有数据节点
		if (entryList.size() > 0) {
			// 获取当前用户的所有节点信息
			List<String> userNodeid = userDataNodeRepository.findByUserid(userid);
			String[] nodeList = entryIndexRepository.findNodeidByEntryidIn(entryList.toArray(new String[entryList.size()]));
			
			// 查找到用户有权限的节点跟数据节点的交集
			userNodeid.retainAll(Arrays.asList(nodeList));
			// 查找到当前档案关联id所包含的所有条目id
			List<String> entryids = eventEntryRepository.findEntryidByEventId(eventid);
			if (userNodeid.size() > 0) {
				String[] entryInfo = entryIndexRepository.findEntryidByNodeidIn(userNodeid.toArray(new String[userNodeid.size()]));
				entryids.retainAll(Arrays.asList(entryInfo));
			}
			
			PageRequest pageRequest = new PageRequest(page - 1, limit);
			Page<Tb_entry_index> eList = entryIndexRepository.findByEntryidIn(entryids.toArray(new String[entryids.size()]), pageRequest);
			return eList;
		}
		return null;
	}
	
	/**
	 * 导入数据关联条目
	 * @param eventid
	 * @param entryid
	 * @return
	 */
	@RequestMapping(value = "/leadInEntry")
	@ResponseBody
	public ExtMsg leadInEntry(String eventid, String entryid) {
		List<Tb_event_entry> entries = new ArrayList<>();
		String[] ids = entryid.split(",");
		List<String> event = eventEntryRepository.findByEntryidIn(ids);
		if (event.size() < 1) {
			if (entryid != null && !"".equals(entryid)) {
				for (int i = 0; i < ids.length; i++) {
					Tb_event_entry entry = new Tb_event_entry();
					entry.setEventid(eventid);
					entry.setEntryid(ids[i]);
					entries.add(entry);
				}
				eventEntryRepository.save(entries);
				return new ExtMsg(true, "成功导入"+ids.length+"条关联条目！", null);
			}
		}
		return new ExtMsg(false, "导入关联条目失败！当前条目已关联事件，不可创建新关联", null);
	}
	
	/**
	 * 创建新的关联事件
	 * @param
	 * @return
	 */
	@RequestMapping(value = "/addDataEvent")
	@ResponseBody
	public ExtMsg addDataEvent(String eventname, String eventnumber, String entryid, String eventid, String type) {
		if (type.equals("添加")) {
			Tb_data_event name = dataEventRepository.findByEventname(eventname);
			if (name != null) {
				return new ExtMsg(true, "档案关联事件描述已存在", null);
			}
			Tb_data_event event = new Tb_data_event();
			event.setEventname(eventname);
			event.setEventnumber(eventnumber);
			event.setCreatedate(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
			Tb_data_event returnEvent = dataEventRepository.save(event);
			
			if (entryid != null && !entryid.equals("")) {
				String[] idStrings = entryid.split(",");
				for (int i = 0; i < idStrings.length; i++) {
					Tb_event_entry event_entry = new Tb_event_entry();
					event_entry.setEntryid(idStrings[i]);
					event_entry.setEventid(returnEvent.getEventid());
					eventEntryRepository.save(event_entry);
				}
			}
			return new ExtMsg(true, "创建新关联成功", null);
		} else {
			Tb_data_event event = dataEventRepository.findByEventid(eventid);
			event.setEventname(eventname);
			event.setEventnumber(eventnumber);
			dataEventRepository.updateByEventid(eventname, eventnumber, eventid);
			return new ExtMsg(true, "修改关联成功", null);
		}
	}
	
	/**
	 * 删除档案关联事件
	 * @param
	 * @return
	 */
	@RequestMapping(value = "/deleteEvent")
	@ResponseBody
	public ExtMsg deleteEvent(String eventid) {
		String[] ids = eventid.split(",");
		// 删除数据关联信息
		Integer returnEvent = dataEventRepository.deleteByEventidIn(ids);
		// 删除数据关联信息中对应的条目信息
		eventEntryRepository.deleteByEventid(eventid);
		if (returnEvent > 0) {
			return new ExtMsg(true, "删除关联成功！", null);
		}
		return new ExtMsg(false, "删除关联失败！", null);
	}
	
	/**
	 * 删除档案关联中的条目信息
	 * @param eventid
	 * @param entryid
	 * @return
	 */
	@RequestMapping(value = "/deleteEventEntry")
	@ResponseBody
	public ExtMsg deleteEventEntry(String eventid, String entryid) {
		String[] ids = entryid.split(",");
		Integer returnEvent = eventEntryRepository.deleteByEventidAndEntryidIn(eventid, ids);
		if (returnEvent > 0) {
			return new ExtMsg(true, "成功删除档案关联条目！", null);
		}
		return new ExtMsg(false, "删除档案关联条目失败！", null);
	}
}