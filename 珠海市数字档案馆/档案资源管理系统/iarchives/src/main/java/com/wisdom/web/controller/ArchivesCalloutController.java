package com.wisdom.web.controller;

import com.wisdom.util.LogAnnotation;
import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.entity.Szh_archives_callout;
import com.wisdom.web.entity.Szh_callout_entry;
import com.wisdom.web.repository.SzhArchivesCalloutRepository;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.ArchivesCalloutService;
import org.bouncycastle.jcajce.provider.asymmetric.ec.KeyFactorySpi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 档案调出控制器
 */
@Controller
@RequestMapping(value = "/archivesCallout")
public class ArchivesCalloutController {

	@Autowired
    ArchivesCalloutService archivesCalloutService;

    @Autowired
    SzhArchivesCalloutRepository szhArchivesCalloutRepository;

	@RequestMapping("/main")
	public String main() {
		return "/inlet/archivesCallout";
	}

	/**
	 * 分页获取调档批次信息
	 * @param page 页码
	 * @param limit 每页数
	 * @param condition 查询字段
	 * @param operator  条件
	 * @param content 查询字段值
	 * @return
	 */
	@RequestMapping("/getArchivesCalloutBySearch")
	@ResponseBody
	public Page<Szh_archives_callout> getArchivesCalloutBySearch(int page, int limit, String sort,String condition, String operator, String content){
		return archivesCalloutService.getArchivesCalloutBySearch(page,limit,sort,condition,operator,content);
	}

	/**
	 * 分页获取调档批次条目信息
	 * @param page 页码
	 * @param limit 每页数
	 * @param condition 查询字段
	 * @param operator  条件
	 * @param content 查询字段值
	 * @return
	 */
	@RequestMapping("/getCalloutEntryBySearch")
	@ResponseBody
	public Page<Szh_callout_entry> getCalloutEntryBySearch(String batchcode, int page, int limit, String sort, String condition, String operator, String content){
		return archivesCalloutService.getCalloutEntryBySearch(batchcode,page,limit,sort,condition,operator,content);
	}

	/**
	 * 获取批次表单内容
	 * @param id 数据id(标识新增OR修改)
	 * @return
	 */
	@RequestMapping("/getBatchAddForm")
	@ResponseBody
	public ExtMsg getBatchAddForm(String id){
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // 系统绑定对象(全局)
		Szh_archives_callout callout;
		if(id!=null&&!"".equals(id)){//修改获取表单
			callout = archivesCalloutService.getArchivesCallout(id);
		}else{//添加获取表单
			String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			callout = new Szh_archives_callout();
			callout.setLendtime(date);
			callout.setLender(userDetails.getLoginname());
			callout.setBatchname(date.replaceAll("-","").substring(0,6)+"0001");
		}
		return  new ExtMsg(true,"成功",callout);
	}

	/**
	 * 提交调档表单
	 * @param callout 单据
	 * @return
	 */
	@LogAnnotation(module = "流水线设置-档案调出",sites = "1",fields = "batchname,assemblycode",connect = "##批次号：,#流水号", startDesc = "批次调档，条目详细：")
	@RequestMapping("/batchAddFormSubmit")
	@ResponseBody
	public ExtMsg batchAddFormSubmit(Szh_archives_callout callout){
		boolean state = false;
		try{
			archivesCalloutService.batchAddFormSubmit(callout);
			state = true;
		}catch (Exception e){
			e.printStackTrace();
		}
		return  new ExtMsg(state,"",null);
	}

	/**
	 * 获取批次条目表单内容
	 * @param id 数据id
	 * @return
	 */
	@RequestMapping("/getEntryAddForm")
	@ResponseBody
	public ExtMsg getEntryAddForm(String id){
		return  new ExtMsg(true,"成功",archivesCalloutService.getEntryAddForm(id));
	}

	/**
	 * 提交批次条目表单
	 * @param entry 单据
	 * @return
	 */
	@LogAnnotation(module = "流水线设置-档案调出-增加批次条目",sites = "1",fields = "batchcode,archivecode",connect = "#批次号,#归档号",startDesc = "增加批次条目：")
	@RequestMapping("/entryAddFormSubmit")
	@ResponseBody
	public ExtMsg entryAddFormSubmit(Szh_callout_entry entry, String nodeid){
		String state = "操作失败";
		try{
			state = archivesCalloutService.entryAddFormSubmit(entry,nodeid);
		}catch (Exception e){
			e.printStackTrace();
		}
		return  new ExtMsg("操作成功".equals(state)?true:false,state,null);
	}

	/**
	 * 删除批次
	 * @param batchcodes 单据
	 * @return
	 */
	@RequestMapping("/batchDel")
	@ResponseBody
	public ExtMsg batchDel(String[] batchcodes){
		boolean state = false;
		try {
			archivesCalloutService.batchDel(batchcodes);
			state = true;
		}catch (Exception e){
			e.printStackTrace();
		}
		return  new ExtMsg(state,"",null);
	}

	/**
	 * 删除批次条目
	 * @param ids 条目数组id
	 * @return
	 */
	@RequestMapping("/entryDel")
	@ResponseBody
	public ExtMsg entryDel(String[] ids){
		boolean state = true;
		try{
			archivesCalloutService.entryDel(ids);
			state = true;
		}catch (Exception e){
			e.printStackTrace();
		}
		return  new ExtMsg(state,"",null);
	}

	/**
	 * 归还批次条目
	 * @param entryids 条目数组id
	 * @return
	 */
	@RequestMapping("/entryReturn")
	@ResponseBody
	public ExtMsg entryReturn(String[] entryids){
		boolean state = false;
		try {
			state = archivesCalloutService.entryReturn(entryids);
		}catch (Exception e){
			e.printStackTrace();
		}
		return  new ExtMsg(state,"",null);
	}

	@RequestMapping("/isHasCalloutEntry")
	@ResponseBody
	public ExtMsg isHasCalloutEntry(String batchcode) {
		boolean flag = archivesCalloutService.isHasCalloutEntry(batchcode);
		return  new ExtMsg(true,"",flag);
	}

	@RequestMapping("/downloadEntryTemp")
	@ResponseBody
	public void downloadEntryTemp(String nodeid, String type, HttpServletRequest request, HttpServletResponse response) {
		archivesCalloutService.getDownLoadEntryTemp(request,response, nodeid,type);
	}

    /**
     * 【档案接收-流水线分配】提交
     * @param assemblycode 流水线名
     * @return
     */
    @RequestMapping("/saveAssemblycode")
    @ResponseBody
    public ExtMsg saveAssemblycode(String[] ids,String assemblycode,String assembly){
        boolean state = false;
        if(ids !=null && !"".equals(ids)){
            for(String id:ids){
                szhArchivesCalloutRepository.updateAssemblycodeBy(id,assemblycode,assembly);
            }
            state = true;
        }
        return new ExtMsg(state, "", null);

    }
}