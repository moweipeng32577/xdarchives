package com.wisdom.web.service;

import com.wisdom.util.GainField;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.io.File;
import java.io.RandomAccessFile;
import java.text.Collator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Rong on 2017/10/31.
 */
@Service
@Transactional
public class InformService {
	@Value("${system.document.rootpath}")
	private String rootpath;

	private static long chunkSize = 5242880;// 文件分片大小5M

	@Autowired
	InFormRepository inFormRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	OrganService organService;

	@Autowired
	UserService userService;

	@Autowired
	UserRoleRepository userRoleRepository;

	@Autowired
	InFormUserRepository inFormUserRepository;

	@Autowired
	ElectronicRepository electronicRepository;

	public Tb_inform getInform(String id) {
		return inFormRepository.findByInformidInOrderByInformdate(new String[] { id }).get(0);
	}

	public Page<Tb_inform> findBySearchInforom(String condition, String operator, String content, String flag, int page,
			int limit, Sort sort) {
		Specifications sp = null;
		if ("1".equals(flag)) {
			List<Tb_inform_user> inform_users = getInformUsers();
			Specification<Tb_inform> searchIdDate = getSearchInformidAndDateCondition(inform_users);
			sp = Specifications.where(searchIdDate);
		}
		if (content != null) {
			sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
		}
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(new Sort.Order(Sort.Direction.ASC,"stick"));//置顶
		sorts.add(new Sort.Order(Sort.Direction.DESC,"informdate"));//发布时间降序
		return inFormRepository.findAll(sp, new PageRequest(page - 1, limit, sort == null ? new Sort(sorts) : sort));
	}
	
	/**
     * 按照limit数量分组
     */
    public List<String[]> subArray(String[] arr, int limit) {
        int arrlen = arr.length;
        int count = arrlen % limit == 0 ? arrlen / limit : arrlen / limit + 1;

        List<List<String>> subAryList = new ArrayList<>();

        for (int i = 0; i < count; i++) {//分组
            int index = i * limit;
            List<String> list = new ArrayList<>();
            int j = 0;
            while (j < limit && index < arr.length) {
                list.add(arr[index++]);
                j++;
            }
            subAryList.add(list);
        }

        List<String[]> returnList = new ArrayList<>();

        for (int i = 0; i < subAryList.size(); i++) {//转数组
            List<String> subList = subAryList.get(i);
            String[] subAryItem = new String[subList.size()];
            for (int j = 0; j < subList.size(); j++) {
                subAryItem[j] = subList.get(j);
            }
            returnList.add(subAryItem);
        }

        return returnList;
    }

	/**
	 * 添加公告
	 *
	 * @param title
	 *            标题
	 * @param date
	 *            到期时间
	 * @param html
	 *            公告内容
	 * @return
	 */
	public Tb_inform addInform(String title, String date, String html, String[] eleids, String postedUser,
			String postedUserids, String postedUsergroup, String postedUsergroupids) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();// 获取安全对象
		Tb_inform inform = new Tb_inform();
		try {
			inform.setLimitdate(new SimpleDateFormat("yyyyMMddHHmmss").parse(date + "235959"));
			inform.setInformdate(new Date());// 生成时间
		} catch (ParseException px) {
			px.printStackTrace();
		}
		inform.setTitle(title);
		inform.setText(html);
		inform.setPostedman(userDetails.getRealname());
		if (postedUserids != null && !"".equals(postedUserids.trim())) {
			inform.setPosteduser("已发布");// 是否发布用户
		} else {
			inform.setPosteduser("未发布");// 是否发布用户
		}
		if (postedUsergroupids != null && !"".equals(postedUsergroupids.trim())) {
			inform.setPostedusergroup("已发布");// 是否发布用户组
		} else {
			inform.setPostedusergroup("未发布");// 是否发布用户组
		}
		// inform.setPosteduser("未发布");//是否发布用户
		// inform.setPostedusergroup("未发布");//是否发布用户组
		Tb_inform save = inFormRepository.save(inform);
		if (eleids != null) {
			electronicRepository.updateEntryid(save.getId(), eleids);
		}
		return save;
	}

	/**
	 * 公告修改
	 *
	 * @param inform
	 *            公告对象 从前端获取，只包含id、title、limitdate
	 * @return
	 */
	public Tb_inform editInform(Tb_inform inform, String[] eleids) {
		Tb_inform inform_full = inFormRepository.findByInformidInOrderByInformdate(new String[] { inform.getId() })
				.get(0);
		inform_full.setText(inform.getText());
		inform_full.setLimitdate(inform.getLimitdate());// 修改到期时间
		inform_full.setTitle(inform.getTitle());
		if (eleids != null) {
			electronicRepository.updateEntryid(inform_full.getId(), eleids);
		}
		return inFormRepository.save(inform_full);
	}

	public List<ExtNcTree> getPostedUser(String flag, String organid) {
		List<ExtNcTree> list = new ArrayList<>();
		if ("userfrom".equals(flag)) {
			List<Tb_user> users = new ArrayList<>();
			if (organid == null || "0".equals(organid)) {
				users = userRepository.findAllUseridRealname();
			} else {
				List<String> organidList = organService.getOrganidLoop(organid, true, new ArrayList<String>());
				organidList.add(organid);
				String[] organs = new String[organidList.size()];
				Specification<Tb_user> specification = userService.getUsers(organidList.toArray(organs));
				users = userRepository.findAll(Specifications.where(specification));
			}
			List<String> userList = new ArrayList<String>();
			if (users.size() > 0) {
				for (int i = 0; i < users.size(); i++) {
					Tb_user user = users.get(i);
					userList.add(user.getRealname() + "-" + user.getUserid());
				}
			}
			String[] strings = new String[userList.size()];
			String[] arrStrings = userList.toArray(strings);
			// Collator 类是用来执行区分语言环境的 String 比较的，这里选择使用CHINA
			Comparator comparator = Collator.getInstance(java.util.Locale.CHINA);
			// 使根据指定比较器产生的顺序对指定对象数组进行排序。
			Arrays.sort(arrStrings, comparator);
			for (int i = 0; i < arrStrings.length; i++) {
				ExtNcTree tree = new ExtNcTree();
				String[] teStrings = arrStrings[i].split("-");
				tree.setFnid(teStrings[1]);
				tree.setText(teStrings[0]);
				list.add(tree);
			}
		}
		if ("usergroupfrom".equals(flag)) {
			List<Tb_role> roles = roleRepository.findAllRoleidRolename();
			List<String> roleList = new ArrayList<String>();
			if (roles.size() > 0) {
				for (int i = 0; i < roles.size(); i++) {
					Tb_role role = roles.get(i);
					roleList.add(role.getRolename() + "-" + role.getRoleid());
				}
			}
			String[] strings = new String[roleList.size()];
			String[] arrStrings = roleList.toArray(strings);
			// Collator 类是用来执行区分语言环境的 String 比较的，这里选择使用CHINA
			Comparator comparator = Collator.getInstance(java.util.Locale.CHINA);
			// 使根据指定比较器产生的顺序对指定对象数组进行排序。
			Arrays.sort(arrStrings, comparator);
			for (int i = 0; i < arrStrings.length; i++) {
				ExtNcTree tree = new ExtNcTree();
				String[] teStrings = arrStrings[i].split("-");
				tree.setFnid(teStrings[1]);
				tree.setText(teStrings[0]);
				list.add(tree);
			}
		}
		return list;
	}

	/**
	 * 获取公告已推送用户或用户组
	 *
	 * @param id
	 *            公告id
	 * @param flag
	 *            用户还是用户组标识
	 * @return
	 */
	public String[] getHasPosteds(String id, String flag) {
		List<Tb_inform_user> informusers = inFormUserRepository.findByInformidIn(new String[] { id });// 获取公告用户,用户组对应信息
		String[] userroleids = GainField.getFieldValues(informusers, "userroleid").length == 0 ? new String[] { "" }
				: GainField.getFieldValues(informusers, "userroleid");
		String[] fnids = null;
		if ("userto".equals(flag)) {// 用户
			List<Tb_user> users = userRepository.findByUseridIn(userroleids);
			fnids = GainField.getFieldValues(users, "userid").length == 0 ? new String[] { "" }
					: GainField.getFieldValues(users, "userid");
		}
		if ("usergroupto".equals(flag)) {// 用户组
			List<Tb_role> roles = roleRepository.findByRoleidIn(userroleids);
			fnids = GainField.getFieldValues(roles, "roleid").length == 0 ? new String[] { "" }
					: GainField.getFieldValues(roles, "roleid");
		}
		Comparator comparator = Collator.getInstance(java.util.Locale.CHINA);
		Arrays.sort(fnids, comparator);
		return fnids;
	}

	/**
	 * 发布公告到用户或用户组
	 *
	 * @param id
	 *            公告id
	 * @param posteds
	 *            用户或用户组id
	 * @param flag
	 *            用户或用户组标识
	 * @return
	 */
	public Map<String, Object> postedInform(String id, String[] posteds, String flag) {
		Map<String, Object> resultMap = new HashMap<>();
		List<Tb_inform_user> inform_users = new ArrayList<>();
		if (id != null && !"".equals(id.trim())) {// 列表中点击推送按钮
			List<Tb_inform> informs = inFormRepository.findByInformidInOrderByInformdate(new String[] { id });
			if ("".equals(flag)) {// 增加公告同时推送用户或用户组
				inFormUserRepository.deleteByInformidIn(new String[] { id });// 直接删除id对应的所有中间表（若有）信息，原则上新增的公告id无对应的推送中间表记录
			} else {
				inFormUserRepository.deleteByInformidAndUserroleidIn(id,
						getHasPosteds(id, flag.split("select")[0] + "to"));// 先获取已设置的用户或用户组id，再删除相关的中间表
			}
			if (posteds == null) {// 当数据为空时只清除中间表信息、设置postedusergroup及posteduser的值
				if ("usergroupselect".equals(flag)) {// 用户组
					informs.get(0).setPostedusergroup("未发布");
				}
				if ("userselect".equals(flag)) {// 用户
					informs.get(0).setPosteduser("未发布");
				}
				return null;
			}
			if ("usergroupselect".equals(flag)) {
				informs.get(0).setPostedusergroup("已发布");
			}
			if ("userselect".equals(flag)) {
				informs.get(0).setPosteduser("已发布");
			}
			for (String posted : posteds) {
				Tb_inform_user inform_user = new Tb_inform_user();
				inform_user.setInformid(id);
				inform_user.setUserroleid(posted);
				inform_users.add(inform_user);
			}
			resultMap.put("list", inFormUserRepository.save(inform_users));
		} else {// 表单中点击推送按钮
			String postedUserFlag = "";
			String postedUsergroupFlag = "";
			if (posteds != null) {
				if ("usergroupselect".equals(flag)) {
					postedUsergroupFlag = "true";
				}
				if ("userselect".equals(flag)) {
					postedUserFlag = "true";
				}
				resultMap.put("list", null);
				resultMap.put("user", postedUserFlag);// 可能值为""或"true"
				resultMap.put("role", postedUsergroupFlag);// 可能值为""或"true"
			} else {
				return null;
			}
		}
		return resultMap;
	}

	/**
	 * 删除公告
	 *
	 * @param ids
	 *            公告id数组
	 * @return
	 */
	public Integer informDel(String[] ids) {
		inFormUserRepository.deleteByInformidIn(ids);// 删除公告与用户对应的信息
		electronicRepository.deleteByEntryidIn(ids);
		return inFormRepository.deleteByInformidIn(ids);// 删除公告
	}

	public List<Tb_inform_user> getInformUsers() {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userid = userDetails.getUserid();
		List<String> roleids = userRoleRepository.findRoleidByUserid(userid);
		roleids.add(userid);
		String[] userRoleidsArr = new String[roleids.size()];
		roleids.toArray(userRoleidsArr);
		return inFormUserRepository.findByUserroleidIn(userRoleidsArr);
	}

	public static Specification<Tb_inform> getSearchInformidAndDateCondition(List<Tb_inform_user> inform_users) {
		Specification<Tb_inform> searchIdDate = null;
		if (inform_users != null) {
			searchIdDate = new Specification<Tb_inform>() {
				@Override
				public Predicate toPredicate(Root<Tb_inform> root, CriteriaQuery<?> criteriaQuery,
						CriteriaBuilder criteriaBuilder) {
					Predicate[] predicates = new Predicate[2];
					CriteriaBuilder.In ci = criteriaBuilder.in(root.get("informid"));
					if (inform_users.size() == 0) {
						ci.value("");
					} else {
						for (Tb_inform_user informu_user : inform_users) {
							ci.value(informu_user.getInformid());
						}
					}
					predicates[0] = ci;
					predicates[1] = criteriaBuilder.greaterThanOrEqualTo(root.get("limitdate"), new Date());
					return criteriaBuilder.and(predicates);
				}
			};
		}
		return searchIdDate;
	}

	private String getUploadDir() {
		String dir = "";
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		dir = rootpath + "/inform/" + userDetails.getUsername();
		File upDir = new File(dir);
		if (!upDir.exists()) {
			upDir.mkdirs();
		}

		return dir;
	}

	public void uploadchunk(Map<String, Object> param) throws Exception {
		String tempFileName = param.get("filename") + "_tmp";
		File confFile = new File(getUploadDir(), param.get("filename") + ".conf");
		File tmpFile = new File(getUploadDir(), tempFileName);
		RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
		RandomAccessFile accessConfFile = new RandomAccessFile(confFile, "rw");

		long offset = chunkSize * Integer.parseInt((String) param.get("chunk"));
		// 定位到该分片的偏移量
		accessTmpFile.seek(offset);
		// 写入该分片数据
		accessTmpFile.write((byte[]) param.get("content"));

		// 把该分段标记为 true 表示完成
		accessConfFile.setLength(Integer.parseInt((String) param.get("chunks")));
		accessConfFile.seek(Integer.parseInt((String) param.get("chunk")));
		accessConfFile.write(Byte.MAX_VALUE);

		// completeList 检查是否全部完成,如果数组里是否全部都是(全部分片都成功上传)
		byte[] completeList = FileUtils.readFileToByteArray(confFile);
		byte isComplete = Byte.MAX_VALUE;
		for (int i = 0; i < completeList.length && isComplete == Byte.MAX_VALUE; i++) {
			// 与运算, 如果有部分没有完成则 isComplete 不是 Byte.MAX_VALUE
			isComplete = (byte) (isComplete & completeList[i]);
		}

		accessTmpFile.close();
		accessConfFile.close();
		// 上传完成，删除临时文件
		if (isComplete == Byte.MAX_VALUE) {
			confFile.delete();
			tmpFile.renameTo(new File(getUploadDir(), (String) param
					.get("filename")));
		}
	}

	public void uploadfileInform(Map<String, Object> param) throws Exception {
		String targetFileName = (String) param.get("filename");
		File tmpFile = new File(getUploadDir(), targetFileName);
		RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
		// 写入数据
		accessTmpFile.write((byte[]) param.get("content"));
		accessTmpFile.close();
	}

	public Map<String, Object> saveElectronic(String informid, String filename) {
		File targetFile = new File(getUploadDir(), filename);
		Map<String, Object> map = new HashMap<>();
		Tb_electronic ele = new Tb_electronic();
		ele.setEntryid(informid == null ? "" : informid);
		ele.setFilename(filename);
		ele.setFilepath(getUploadDir().replace(rootpath, ""));
		ele.setFilesize(String.valueOf(targetFile.length()));
		ele.setFiletype(filename.substring(filename.lastIndexOf('.') + 1));
		ele = electronicRepository.save(ele);// 保存电子文件
		map = ele.getMap();
		return map;
	}

	public Integer deleteElectronic(String eleids) {
		String[] eleidArray = eleids.split(",");
		List<Tb_electronic> electronics = electronicRepository.findByEleidInOrderBySortsequence(eleidArray);// 获取删除电子文件
		for (Tb_electronic electronic : electronics) {
			File file = new File(rootpath + electronic.getFilepath() + "/" + electronic.getFilename());
			file.delete();// 删除电子文件
		}
		return electronicRepository.deleteByEleidIn(eleidArray);
	}

	public List<Tb_electronic> getInformFile(String informId) {
		return electronicRepository.findByEntryidOrderBySortsequence(informId);
	}

	public String getInformFilePath(String eleid) {
		Tb_electronic electronic = electronicRepository.findByEleid(eleid);
		return rootpath + electronic.getFilepath() + "/" + electronic.getFilename();
	}

	public void informStick(String[] ids,String level){
		List<Tb_inform> informs = inFormRepository.findByInformidInOrderByLimitdateDesc(ids);
		for(Tb_inform inform:informs){
			inform.setStick(Integer.parseInt(level));
		}
//        Integer i = inFormRepository.getInformMaxStick();
//        if(i==null){
//            inform.setStick(1);
//        }else{
//            inform.setStick(i+1);
//        }
	}

	public boolean cancelStick(String[] ids){
		boolean state = false;
		List<Tb_inform> informs = inFormRepository.findByInformidInOrderByLimitdateDesc(ids);
		for(Tb_inform inform:informs){
			if(inform.getStick()!=null){
				inform.setStick(null);
				state = true;
			}
		}
		return state;
	}
}
