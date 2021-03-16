package com.wisdom.web.controller;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wisdom.secondaryDataSource.repository.SecondaryDataNodeRepository;
import com.wisdom.util.*;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.ClassificationService;
import com.wisdom.web.service.ExportExcelService;
import com.wisdom.web.service.NodesettingService;
import com.wisdom.web.service.OrganService;
import org.checkerframework.checker.units.qual.A;
import org.codehaus.groovy.util.ListHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

/**
 * 机构管理控制器 Created by xd on 2017/9/27.
 */
@Controller
@RequestMapping(value = "/organ")
public class OrganController {

	@Autowired
	OrganService organService;

	@Autowired
	ClassificationService classificationService;

	@Autowired
	RightOrganRepository rightOrganRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	ExportExcelService exportExcelService;

	@Autowired
	NodesettingService nodesettingService;

	@Autowired
	AcquisitionController acquisitionController;

	@Autowired
	DataNodeRepository dataNodeRepository;

	@Autowired
	SecondaryDataNodeRepository secondaryDataNodeRepository;

	@Autowired
	FundsRepository fundsRepository;

	@Value("${system.document.rootpath}")
	private String rootpath;

	private static Map<String, String> columnMap = new HashMap();

	static {
		columnMap.put("机构名称", "organname");
		columnMap.put("机构编码", "refid");
		columnMap.put("机构类型", "organtype");
		columnMap.put("服务名称", "servicesname");
		columnMap.put("系统名称", "systemname");
//        columnMap.put("引用", "refid");
		columnMap.put("描述", "desciption");
		columnMap.put("备注", "desciption");
		columnMap.put("机构状态", "usestatus");
		columnMap.put("状态", "usestatus");
		columnMap.put("机构(问题)代码", "code");
		columnMap.put("上级机构", "parentid");
		columnMap.put("机构层级", "organlevel");
		columnMap.put("当前层顺序", "sortsequence");
	}

	@RequestMapping("/main")
	public String index(Model model, String isp) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(isp!=null){
			Object functionButton = JSON.toJSON(FunctionUtil.getQxFunction(isp));
			model.addAttribute("functionButton", functionButton);
		}
		model.addAttribute("userid", userDetails.getUserid());
		return "/inlet/organ";
	}

	@RequestMapping("/organValid")
	@ResponseBody
	public ExtMsg organValid(String formname, String parentid) {
		List<Tb_right_organ> organList = rightOrganRepository.findByOrgannameAndParentid(formname, parentid);
		boolean isExist = organList.size() > 0;
		return new ExtMsg(isExist, "", null);
	}

	@LogAnnotation(module = "机构管理", startDesc = "更新操作，机构id为：", sites = "1", fields = "organid,organname", connect = "，机构名称为：,。")
	@RequestMapping("/updateOrgan")
	@ResponseBody
	public ExtMsg updateOrgan(Tb_right_organ right_organ,String xtType) {
		right_organ=organService.updateOrgan(right_organ, xtType);
		GuavaCache.removeValueByKey(GuavaUsedKeys.NODE_ALL_LIST);   //清空记录在变量中的所有节点数据
		//更新数据节点更新时间
		nodesettingService.updateNodeChangeTime();
		try{
			//更新声像系统数据
			organService.updateSxOrgan(right_organ);
		}catch(Exception e){
			e.printStackTrace();
			return new ExtMsg(true, "修改档案机构节点成功，修改声像机构节点失败", right_organ);
		}
		return new ExtMsg(true, "修改成功", right_organ);
	}

	@LogAnnotation(module = "机构管理", startDesc = "增加操作，机构名称为：", sites = "1", fields = "organname")
	@RequestMapping("/addOrgan")
	@ResponseBody
	public ExtMsg addOrgan(Tb_right_organ right_organ, String parentid_real,String xtType) {
		GuavaCache.removeValueByKey(GuavaUsedKeys.NODE_ALL_LIST);   //清空记录在变量中的所有节点数据
		//更新数据节点更新时间
		nodesettingService.updateNodeChangeTime();
		right_organ = organService.addOrgan(right_organ, parentid_real);
		if(right_organ==null){
			return new ExtMsg(true, "增加失败", right_organ);
		}
		//增加相应的节点，模板，档号，节点的用户授权  声像
		Tb_funds funds=fundsRepository.findByOrganid(right_organ.getOrganid());
		GuavaCache.removeValueByKey(GuavaUsedKeys.NODE_ALL_LIST);   //清空记录在变量中的所有节点数据
		//更新数据节点更新时间
		nodesettingService.updateNodeChangeTime();
		try{
			organService.addOrganDataNodeSx(right_organ, parentid_real, right_organ.getSortsequence(),funds);
		}catch(Exception e){
			e.printStackTrace();
			return new ExtMsg(true, "档案机构节点增加成功,声像机构节点增加失败,请确认两边机构是否一致", right_organ);
		}
		return new ExtMsg(true, "增加成功", right_organ);
	}

	//@LogAnnotation(module = "机构管理", startDesc = "删除操作，机构id为：", sites = "1")
	@RequestMapping("/deleteOrgans")
	@ResponseBody
	public void deleteOrgans(String[] ids,String xtType) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userid=userDetails.getUserid();
		GuavaCache.removeValueByKey(GuavaUsedKeys.NODE_ALL_LIST);   //清空记录在变量中的所有节点数据
		//清除个人权限数据缓存
		GuavaCache.removeValueByKey(userid + GuavaUsedKeys.NODE_USER_LIST_SUFFIX);
		GuavaCache.removeValueByKey(userid + GuavaUsedKeys.NODE_ROLE_LIST_SUFFIX);
		//更新数据节点更新时间
		nodesettingService.updateNodeChangeTime();

		//清除个人删除数据缓存
		GuavaCache.removeValueByKey(userid + GuavaUsedKeys.ORGAN_USER_ARR_SUFFIX);
		GuavaCache.removeValueByKey(userid + GuavaUsedKeys.NODE_DA_USER_ARR_SUFFIX);
		GuavaCache.removeValueByKey(userid + GuavaUsedKeys.NODE_SX_USER_ARR_SUFFIX);

		List<String> organidList=new ArrayList<>();//所有删除的机构节点
		organService.deleteOrgans(ids,organidList);//删除机构节点
		String[] organidArr=organidList.toArray(new String[organidList.size()]);//所有删除的机构节点（包括子节点）
		//查找关联数据节点
		String[] nodeidArr=dataNodeRepository.findByRefidIN(organidArr);
		String[] nodeidSxArr=secondaryDataNodeRepository.findByRefidIN(organidArr);//声像系统
		organService.deleteDatanode(organidArr);//删除数据节点
		try{
			organService.deleteSxDatanode(organidArr);//删除数据节点 声像
		}catch(Exception e){
			e.printStackTrace();
		}

		//机构删除信息放进个人缓存
		GuavaCache.setKeyValue(userid + GuavaUsedKeys.ORGAN_USER_ARR_SUFFIX, organidArr);
		GuavaCache.setKeyValue(userid + GuavaUsedKeys.NODE_DA_USER_ARR_SUFFIX, nodeidArr);
		GuavaCache.setKeyValue(userid + GuavaUsedKeys.NODE_SX_USER_ARR_SUFFIX, nodeidSxArr);

		/*DelOrganThread delThread = new DelOrganThread(organidArr, nodeidArr, nodeidSxArr);// 开启线程
		delThread.start();*/
		GuavaCache.removeValueByKey(GuavaUsedKeys.NODE_ALL_LIST);   //清空记录在变量中的所有节点数据
		//更新数据节点更新时间
		nodesettingService.updateNodeChangeTime();
		acquisitionController.delTransWriteLog(ids, "机构管理", "删除数据");// 写日志
	}

	/**
	 * 删除机构关联数据  直接在上边删除机构方法删除会造成页面刷新阻塞
	 */
	@RequestMapping("/deleteOrgansRef")
	@ResponseBody
	public void deleteOrgansRef() {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userid=userDetails.getUserid();
		//获取个人机构删除缓存
		String[] organidArr=(String[]) GuavaCache.getValueByKey(userid + GuavaUsedKeys.ORGAN_USER_ARR_SUFFIX);//机构id
		String[] nodeidArr=(String[]) GuavaCache.getValueByKey(userid + GuavaUsedKeys.NODE_DA_USER_ARR_SUFFIX);//档案系统关联机构数据节点
		String[] nodeidSxArr=(String[]) GuavaCache.getValueByKey(userid + GuavaUsedKeys.NODE_SX_USER_ARR_SUFFIX);//声像系统关联机构数据节点
		DelOrganThread delThread = new DelOrganThread(organidArr, nodeidArr, nodeidSxArr);// 开启线程
		delThread.start();
	}

	@RequestMapping("/organs")
	@ResponseBody
	public Page<Tb_right_organ> findOrganDetailBySearch(int page, int limit, String condition, String operator,
			String content, String organid, String sort) {
		Sort sortobj = WebSort.getSortByJson(sort);
		return organService.findBySearch(page, limit, condition, operator, content, organid, sortobj);
	}

	@RequestMapping("/organids")
	@ResponseBody
	public Page<Tb_right_organ> findOrganByOrganids(int page, int limit, String organid, String sort) {
		Sort sortobj = WebSort.getSortByJson(sort);
		return organService.findByOrganids(page, limit, organid, sortobj);
	}

	@RequestMapping("/organsortsequence")
	@ResponseBody
	public void findOrganBySortsequence(String[] organid, int currentcount, String operate) {
		organService.findBySortquence(organid, currentcount, operate);
	}

	@RequestMapping("/order/{organid}/{overorganid}/{targetorder}")
	@ResponseBody
	public ExtMsg modifyorder(@PathVariable String organid, @PathVariable String overorganid,
			@PathVariable String targetorder) {
		Tb_right_organ organ = organService.findOrgan(organid);
		organService.modifyOrganOrder(organ, Integer.parseInt(targetorder), overorganid);
		return null;
	}

	/**
	 * 通过机构id获取机构名称
	 *
	 * @param organid
	 * @return
	 */
	@RequestMapping("/getOrgan/{organid}")
	@ResponseBody
	public ExtMsg findOrganByOrganid(@PathVariable String organid) {
		return new ExtMsg(true, "success", organService.findOrganByOrganid(organid));
	}

	/**
	 * 检验当前用户是否是最高级三员用户
	 *
	 * @return
	 */
	@RequestMapping("/topAdminCheck")
	@ResponseBody
	public ExtMsg topAdminCheck() {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String name = userDetails.getLoginname();
		if (name.equals("aqbm") || name.equals("xitong") || name.equals("aqsj")) {
			return new ExtMsg(true, "", null);
		} else {
			return new ExtMsg(false, "", null);
		}
	}

	@RequestMapping("/preview")
	@ResponseBody
	public List<NodesettingTree> preview(String organId, String organName, String parentId, String previewType,String xtType) {
		if (previewType == null) {
			return null;
		} else if (previewType.equals("add")) {
			return organService.addOrganPreview(organName, parentId,xtType);
		} else {
			return classificationService.updatePreview(organName, organId,xtType);
		}
	}

	@RequestMapping("/userbyorgan")
	@ResponseBody
	public ExtMsg findUserByOrganid(String[] organid) {
		boolean flag = false;
		for (int i = 0; i < organid.length; i++) {
			List<String> userrealname = userRepository.findUseridByOrganidOrderByUserid(organid[i]);
			for (int j = 0; j < userrealname.size(); j++) {
				if ("安全保密管理员".equals(userrealname.get(j)) || "系统管理员".equals(userrealname.get(j)) || "安全审计员".equals(userrealname.get(j))) {
					flag = true;
					break;
				}
			}
		}
		return new ExtMsg(true, "", flag);
	}

	//todo
	//考虑：导入机构界面只用选择excel文件，节点默认为外面选择的节点，导入完成后刷新当前节点即可，像增加功能
	//验证必填数据是否存在，及数据的合法性
	//同级机构是否重名：excel的与系统已有机构是否重名、excel中的各个机构是否重名
	@RequestMapping(value = "/importOrgan", method = RequestMethod.POST)
	@ResponseBody
	public String importOrgan(MultipartFile fileImport, String parentid) throws Exception {
//		MultipartFile转File
		ObjectMapper json = new ObjectMapper();
		String fileName = fileImport.getOriginalFilename();
		String prefix = fileName.substring(fileName.lastIndexOf("."));
		String tempName = UUID.randomUUID().toString().replace("-", "");
		File tempDir = new File(rootpath + "/importOrgan");
		if (!tempDir.exists()) {
			tempDir.mkdirs();
		}
		File tempFile = File.createTempFile(tempName, prefix, tempDir);
		fileImport.transferTo(tempFile);

		//获取excel表列头
		List<String> ExcelHeadList = ReadExcel.getHeadField(tempFile);
		String[] fieldcode = new String[ExcelHeadList.size()];
		String[] fieldname = ExcelHeadList.toArray(new String[ExcelHeadList.size()]);
		for(int i =0;i<fieldname.length;i++){
			fieldcode[i]=columnMap.get(fieldname[i]);
		}

		//2019年7月3日16:22:48
		List<Tb_right_organ> organList = new ArrayList<>();
		List<List<String>> lists = ReadExcel.readAllVersionExcel(tempFile, fieldname);// 解析文件
		Map<String,String> parentOrganMap=new HashMap<>();//organlevel,organid
		Map<String,String> parentOrganNameMap=new HashMap<>();//organname,organid
		Set<String> parentOrganidSet=new HashSet<>();//父级机构id集合
		Map<String, Object> resMap = new ListHashMap<>();//返回
		//挂接点上级机构
		Tb_right_organ gjOrgan;
		String gjOrganlevel="";
		if("0".equals(parentid)){//挂到根节点 机构设置，不用拼接organlevel
		}else{//挂到指定的机构节点,需要拼接层级organlevel
			gjOrgan=rightOrganRepository.findByOrganid(parentid);//挂接机构节点
			gjOrganlevel=gjOrgan.getOrganlevel();
		}
		//存放excel中的机构名 判断重复
		List<String> name_list = new ArrayList<>();
		for (List list : lists) {//已知code和name长度相同并对应
			Tb_right_organ organ = ValueUtil.creatRightOrgan(fieldcode, list);
			if(name_list.contains(organ.getOrganname())){//excel中存在重复机构名
				continue;
			}
			int organ_name_count = rightOrganRepository.countByOrgannameAndParentid(organ.getOrganname(),parentid);
			if(organ_name_count>0){//存在重复
				resMap.put("success", true);
				resMap.put("msg","导入失败，同级机构名重复："+organ.getOrganname());
				//删除刚添加的机构
				organService.deleteAddOrgans(organList);
				return json.writeValueAsString(resMap);
			}
			//机构类型
			String organtype = organ.getOrgantype() == "" ? "a" : organ.getOrgantype().toLowerCase();
			//机构状态
			String usestatus = organ.getUsestatus() == "" ? "a" : organ.getUsestatus().toLowerCase();
			if (organtype.indexOf("部门") != -1 || organtype.indexOf("department") != -1) {
				organ.setOrgantype("department");
			} else {
				organ.setOrgantype("unit");
			}
			if (usestatus.indexOf("启用") != -1 || usestatus.indexOf("1") != -1) {
				organ.setUsestatus("1");
			} else {
				organ.setUsestatus("0");
			}
			organ.setServicesid("402789f55d54dc21015d54dccadd0000");
			organ.setSystemid("402789f55d54e087015d54e0cfca0000");
			String parentOrganName=organ.getParentid();
			String organlevel=organ.getOrganlevel();
			//按层级来保存，先保存第一层，然后依次往下，这样可以保存该层机构后获取相应的organid作为下一层的parentid
			//先判断有没有上级机构为空的，有的话把它先作为第一层级保存，然后再找上级机构是刚保存的那些机构的，可以设置相应的parentid
			String newParentid="";
			if("".equals(organlevel)||organlevel==null){//机构层级为空，需要机构名称唯一
				if(parentOrganName==null||"".equals(parentOrganName)){//上级机构为空，直接挂接在指定的挂节点下边
					organ.setParentid(parentid);
				}else{//设置新的parentid
					if(parentOrganNameMap.containsKey(parentOrganName)) {//上级机构集合已经含有这个organname
						newParentid=parentOrganNameMap.get(parentOrganName);
						organ.setParentid(newParentid);
						parentOrganidSet.add(newParentid);
					}else{//查找上级机构
						List<Tb_right_organ> parentOrgans=rightOrganRepository.findWithOrganname(parentOrganName);
						if(parentOrgans.size()==1){
							newParentid=parentOrgans.get(0).getOrganid();
							organ.setParentid(newParentid);
							parentOrganidSet.add(newParentid);
						}else if(parentOrgans.size()>1){//机构名称重复，返回异常信息
							resMap.put("success", true);
							resMap.put("msg","导入失败，上级机构名重复："+parentOrganName);
							//删除刚添加的机构
							organService.deleteAddOrgans(organList);
							return json.writeValueAsString(resMap);
						}else{//上级级机构为空（正常的话，上一级已经在子节点之前添加，属于非正常顺序，暂不考虑）
							resMap.put("success", true);
							resMap.put("msg","导入失败，找不到上级机构："+parentOrganName);
							//删除刚添加的机构
							organService.deleteAddOrgans(organList);
							return json.writeValueAsString(resMap);
						}
					}
				}
				List<Tb_right_organ> rightOrgans = rightOrganRepository.findByParentidOrderBySortsequence(organ.getParentid());
				if(rightOrgans.size()>0){
					//判断当前的排序是否正确
					if(rightOrgans.get(rightOrgans.size()-1).getSortsequence()!=rightOrgans.size()){
						int count = 1;
						for(Tb_right_organ rightOrgan : rightOrgans){
							rightOrgan.setSortsequence(count);
							count++;
						}
						rightOrganRepository.save(rightOrgans);
					}
				}
				//设置导入机构排序号
				organ.setSortsequence(rightOrgans.size()+1);
				organ=rightOrganRepository.save(organ);
				//保存节点到机构map
				parentOrganMap.put(organ.getOrganname(),organ.getOrganid());
			}else{//机构层级有填写  参考001.001.002
				if("0".equals(parentid)){//挂到根节点 机构设置，不用拼接organlevel
				}else{//挂到指定的机构节点,需要拼接层级organlevel
					organlevel=gjOrganlevel+"."+organlevel;
				}
				Tb_right_organ newOrgan=rightOrganRepository.findByOrganlevel(organlevel);
				if(newOrgan!=null){//已经存在这个organlevel的节点，返回异常信息
					resMap.put("success", true);
					resMap.put("msg","导入失败，机构层级重复");
					//删除刚添加的机构
					organService.deleteAddOrgans(organList);
					return json.writeValueAsString(resMap);
				}else{//organlevel还没存在，可以直接添加
					organ.setOrganlevel(organlevel);
				}

				if(parentOrganName==null||"".equals(parentOrganName)||"0".equals(parentOrganName)){//上级机构为空或者0，一级节点
					if("0".equals(parentid)){//挂到根节点
						organ.setParentid("0");
					}else{
						organ.setParentid(parentid);
						parentOrganidSet.add(newParentid);
					}

				}else{//非一级节点
					if(organlevel.indexOf(".")>-1){//标准的层级编号001.001.002
						String parentOrganlevel=organlevel.substring(0,organlevel.lastIndexOf("."));//此节点的上一级机构
						if(parentOrganMap.containsKey(parentOrganlevel)){//上级机构集合已经含有这个organlevel
							newParentid=parentOrganMap.get(parentOrganlevel);
							organ.setParentid(newParentid);
							parentOrganidSet.add(newParentid);
						}else{//上级机构集合还没含有这个organlevel
							//查找上级机构
							Tb_right_organ parentOrgan=rightOrganRepository.findByOrganlevel(parentOrganlevel);//organlevel默认唯一
							if(parentOrgan==null){//上级级机构为空（正常的话，上一级已经在子节点之前添加，属于非正常顺序，暂不考虑）
								resMap.put("success", true);
								resMap.put("msg","导入失败，机构层级："+organlevel+"的上级级机构为空");
								//删除刚添加的机构
								organService.deleteAddOrgans(organList);
								return json.writeValueAsString(resMap);
							}else{//上级机构已经存在
								organ.setParentid(parentOrgan.getOrganid());
								parentOrganidSet.add(newParentid);
								//保存上级节点到机构map
								parentOrganMap.put(parentOrgan.getOrganlevel(),parentOrgan.getOrganid());
							}
						}
					}else{//层级编号不规范,返回异常信息
						resMap.put("success", true);
						resMap.put("msg","导入失败，机构层级不规范");
						//删除刚添加的机构
						organService.deleteAddOrgans(organList);
						return json.writeValueAsString(resMap);
					}
				}
				List<Tb_right_organ> rightOrgans = rightOrganRepository.findByParentidOrderBySortsequence(organ.getParentid());
				if(rightOrgans.size()>0){
					//判断当前的排序是否正确
					if(rightOrgans.get(rightOrgans.size()-1).getSortsequence()!=rightOrgans.size()){
						int count = 1;
						for(Tb_right_organ rightOrgan : rightOrgans){
							rightOrgan.setSortsequence(count);
							count++;
						}
						rightOrganRepository.save(rightOrgans);
					}
				}
				//设置导入机构排序号
				organ.setSortsequence(rightOrgans.size()+1);
				organ=rightOrganRepository.save(organ);
				//保存节点到机构map
				parentOrganMap.put(organ.getOrganlevel(),organ.getOrganid());
			}
			organList.add(organ);
			name_list.add(organ.getOrganname());
		}

		tempFile.delete();//用完就删
		resMap.put("success", true);
		resMap.put("msg","导入成功");
		String jsonString = json.writeValueAsString(resMap);
		organService.importOrgan(organList, parentid,parentOrganidSet);
		try{
			organService.importSxOrgan(organList, parentid,parentOrganidSet);//同步到声像系统
		}catch(Exception e){
			e.printStackTrace();
		}

		//String jsonString = json.writeValueAsString(organService.importOrgan(organList, parentid));
		//导入成功后更新缓存
		GuavaCache.removeValueByKey(GuavaUsedKeys.NODE_ALL_LIST);   //清空记录在变量中的所有节点数据
		//更新数据节点更新时间
		nodesettingService.updateNodeChangeTime();
		return jsonString;
	}

	//导出机构
	@RequestMapping("/expOrgan")
	public void expOrgan(String[] columnNames, HttpServletResponse response, String[] ids) {
//        if (ids == null || ids.length == 0) {
//            return null;
//        }
		//获取code
		String[] copyColumnNames = columnNames==null?new String[0]:columnNames;
		List<String> fieldcode = new ArrayList<>();
		List<String> fieldname = new ArrayList<>();
		for (String str : copyColumnNames) {
			if(columnMap.get(str)!=null&&!"".equals(columnMap.get(str))) {
				fieldcode.add(columnMap.get(str));
				fieldname.add(str);
			}
		}
		exportExcelService.exporOrgan(fieldcode,fieldname,ids,response);
	}
}