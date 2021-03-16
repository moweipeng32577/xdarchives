package com.wisdom.web.service;

import com.wisdom.secondaryDataSource.entity.Tb_entry_index_sx;
import com.wisdom.secondaryDataSource.repository.SecondaryEntryIndexRepository;
import com.wisdom.service.websocket.WebSocketService;
import com.wisdom.util.DBCompatible;
import com.wisdom.util.DateUtil;
import com.wisdom.util.GainField;
import com.wisdom.util.SpecificationUtil;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import com.xdtech.component.storeroom.entity.Storage;
import com.xdtech.component.storeroom.repository.StorageRepository;
import com.xdtech.project.lot.mjj.message.type.Int;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by yl on 2017/10/26.
 */
@Service
@Transactional
public class JyAdminsService {

	@Value("${workflow.stborrow.approve.workid}")
	private String stborrowWorkid;

	@Value("${system.document.rootpath}")
	private String rootpath;//系统文件根目录

	@Value("${system.loginType}")
	private String systemLoginType;//登录系统设置  政务网1  局域网0

	@Autowired
	BorrowDocRepository borrowDocRepository;

	@Autowired
	BorrowMsgRepository borrowMsgRepository;

	@Autowired
	EntryIndexRepository entryIndexRepository;

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserService userService;

	@Autowired
	TaskRepository taskRepository;

	@Autowired
	RightOrganRepository rightOrganRepository;

	@Autowired
	OrganService organService;

	@Autowired
	UserNodeRepository userNodeRepository;
	
	@Autowired
	FlowsRepository flowsRepository;

	@Autowired
	WebSocketService webSocketService;

	@Autowired
	StorageRepository storageRepository;

	@Autowired
	FeedbackRepository feedbackRepository;

    @Autowired
    ReserveRepository reserveRepository;

	@Autowired
	ShowroomDatePersonRepository showroomDatePersonRepository;

	@Autowired
	EntryCaptureService entryCaptureService;

	@Autowired
	ShowroomRepository showroomRepository;

	@Autowired
	ElectronicRepository electronicRepository;

	@Autowired
	ElectronicService electronicService;

	@Autowired
	ElectronApproveService electronApproveService;

	@Autowired
	NodesettingService nodesettingService;

	@Autowired
	UserFunctionRepository userFunctionRepository;

	@Autowired
	UserRoleRepository userRoleRepository;

	@Autowired
	SecondaryEntryIndexRepository secondaryEntryIndexRepository;

    @Autowired
    ClassifySearchService classifySearchService;

	public Page<Tb_borrowdoc> getBorrowDocs(String flag, String condition, String operator, String content, String state, String type, int page, int limit, Sort sort) {
		if (condition!=null && condition.equals("returnstate") && !state.equals("已通过")){
			content = "";
		}
		PageRequest pageRequest = new PageRequest(page - 1, limit, sort == null ?
				new Sort(Sort.Direction.DESC, condition == null || content == null || content.equals("") ? "borrowdate" : condition,"docid") : sort);
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		// Page<Tb_borrowdoc> list =
		// borrowDocRepository.findByBorrowmanAndState(pageRequest,userDetails.getRealname(),state);
		if ("1".equals(flag)) {
			return borrowDocRepository.findByBorrowmanidAndStateAndType(pageRequest,userDetails.getUserid(), state, type);
		} else {
			if("0".equals(systemLoginType)){//局域网设置显示所有借阅信息数据
				if (condition!=null && !condition.equals("") && operator!=null && !operator.equals("") && content!=null && !content.equals("")){
					Specifications specifications = Specifications.where(new SpecificationUtil("state", "equal", state))
							.and(new SpecificationUtil("type", "equal", type)).and(new SpecificationUtil(condition, operator, content));// 过滤为当前机构
					return borrowDocRepository.findAll(specifications, pageRequest);
				}
				Specifications specifications = Specifications.where(new SpecificationUtil("state", "equal", state))
						.and(new SpecificationUtil("type", "equal", type));// 过滤为当前机构
				return borrowDocRepository.findAll(specifications, pageRequest);
			}
			String userid = userDetails.getUserid();
			// 1.1查找当前用户所属单位，需要网上找，当前机构可能为部门
			String organid = userRepository.findOrganidByUserid(userid);
			Tb_right_organ organ = rightOrganRepository.findOne(organid);
			while (organ.getOrgantype() != null && organ.getOrgantype().equals(Tb_right_organ.ORGAN_TYPE_DEPARTMENT)) {
				organ = rightOrganRepository.findOne(organ.getParentid());
			}
			// 1.2查找当前机构下的所有用户
			List<String> organidList = organService.getOrganidLoop(organ.getOrganid(), true, new ArrayList<String>());
			organidList.add(organ.getOrganid());
			String[] organs = new String[organidList.size()];
			Specification<Tb_user> specification = UserService.getUsers(organidList.toArray(organs));
			List<Tb_user> useridList = userRepository.findAll(Specifications.where(specification));
			////////////////////////////////////////// 过滤机构用户start///////////////////////////////////////////////
			Specification<Tb_borrowdoc> SearchBorrowname = new Specification<Tb_borrowdoc>() {
				@Override
				public Predicate toPredicate(Root<Tb_borrowdoc> root, CriteriaQuery<?> criteriaQuery,
						CriteriaBuilder criteriaBuilder) {
					CriteriaBuilder.In p = criteriaBuilder.in(root.get("borrowmanid"));
					for (Tb_user user : useridList) {
						p.value(user.getUserid());
					}
					return criteriaBuilder.or(p);
				}
			};
			////////////////////////////////////////// 过滤机构用户end///////////////////////////////////////////////
			Specifications specifications = Specifications.where(SearchBorrowname)
					.and(new SpecificationUtil("state", "equal", state))
					.and(new SpecificationUtil("type", "equal", type));// 过滤为当前机构

			// return
			// borrowDocRepository.findByStateAndTypeOrderByBorrowdate(pageRequest,
			// state, type);
			return borrowDocRepository.findAll(specifications, pageRequest);
		}
	}

    public Page<Tb_borrowdoc> getBorrowDocsBySelfApprove(String flag, String state, String type, int page, int limit, Sort sort) {
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort == null ?
                new Sort(Sort.Direction.DESC, "borrowdate","docid") : sort);
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Tb_flows> flows = flowsRepository.findBySpman(userDetails.getUserid());
        String[] misgid = new String[flows.size()];
        misgid = flows.stream().map(msg -> msg.getMsgid()).collect(Collectors.toList()).toArray(misgid);
        return borrowDocRepository.findByBorrowcodeInAndStateAndType(pageRequest,misgid, state, type);
    }

	/**
	 * 获取全部单据记录
	 * 
	 * @param page
	 *            页码
	 * @param limit
	 *            每页数
	 * @return 分页单据
	 */
	public Page<Tb_borrowdoc> getBorrowDocs(int page, int limit, String condition, String operator, String content) {
		Sort sort = new Sort(Sort.Direction.DESC, "borrowdate");
		Specifications specifications = ClassifySearchService.addSearchbarCondition(Specifications.where(getSearchStateCondition("已通过")), condition, operator, content);
		PageRequest pageRequest = new PageRequest(page - 1, limit, sort);
		return borrowDocRepository.findAll(specifications, pageRequest);
	}

	public static Specification<Tb_borrowdoc> getSearchStateCondition(String state){
		Specification<Tb_borrowdoc> searchStateCondition = new Specification<Tb_borrowdoc>() {
			@Override
			public Predicate toPredicate(Root<Tb_borrowdoc> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				CriteriaBuilder.In in = criteriaBuilder.in(root.get("state"));
					in.value(state);
				return criteriaBuilder.or(in);
			}
		};
		return searchStateCondition;
	}

	public static Specification<Tb_reserve> getSearchlymodeCondition(String lymode){
		Specification<Tb_reserve> searchlymodeCondition = new Specification<Tb_reserve>() {
			@Override
			public Predicate toPredicate(Root<Tb_reserve> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				CriteriaBuilder.In in = criteriaBuilder.in(root.get("lymode"));
				in.value(lymode);
				return criteriaBuilder.or(in);
			}
		};
		return searchlymodeCondition;
	}

    public static Specification<Tb_reserve> getSearchSubmiteridCondition(String submiterid){
        Specification<Tb_reserve> searchSubmiteridCondition = new Specification<Tb_reserve>() {
            @Override
            public Predicate toPredicate(Root<Tb_reserve> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                CriteriaBuilder.In in = criteriaBuilder.in(root.get("submiterid"));
                in.value(submiterid);
                return criteriaBuilder.or(in);
            }
        };
        return searchSubmiteridCondition;
    }

	/**
	 * 获取批示
	 *
	 * @param borrowdoc
	 *            查档单号id
	 * @return
	 */
	public Tb_borrowdoc getBorrowDoc(String borrowdoc) {
		return borrowDocRepository.findByDocid(borrowdoc);
	}

	public Page<Tb_entry_index> getEntryIndex(int page, int limit, String borrowdocid,String type) {
		List<Tb_borrowmsg> borrowmsgs = new ArrayList<>();
		if(type!=null&&"outware".equals(type)){
			borrowmsgs = borrowMsgRepository.getBorrowmsgsByOutware(borrowdocid);
		}else{
			borrowmsgs = borrowMsgRepository.getBorrowmsgsByBorrowdocid(borrowdocid);
		}
		Tb_borrowdoc borrowdoc=borrowDocRepository.findByDocid(borrowdocid);
		Map<String, String> map = new HashMap<>();
		for (Tb_borrowmsg borrowmsg : borrowmsgs) {
			map.put(borrowmsg.getEntryid() + "1", borrowmsg.getJybackdate());// 审批通过时间
			map.put(borrowmsg.getEntryid() + "2", borrowmsg.getJyts() + "");// 查档天数
			map.put(borrowmsg.getEntryid() + "3", borrowmsg.getId());
			map.put(borrowmsg.getEntryid() + "4", borrowmsg.getState());// 归还状态
			map.put(borrowmsg.getEntryid() + "5", borrowmsg.getLyqx());
			map.put(borrowmsg.getEntryid() + "6", borrowmsg.getType());  //查档类型
		}
		String[] ids = GainField.getFieldValues(borrowmsgs, "entryid").length == 0 ? new String[] { "" }
				: GainField.getFieldValues(borrowmsgs, "entryid");
		PageRequest pageRequest = new PageRequest(page - 1, limit);
		List<Tb_entry_index> entry_indices=new ArrayList<>();
		long totalElements;
		if("soundimage".equals(borrowdoc.getDatasourcetype())){
			Page<Tb_entry_index_sx> pages = secondaryEntryIndexRepository.findByEntryidIn(ids, pageRequest);
			for (Tb_entry_index_sx entryIndexSx : pages.getContent()) {
				Tb_entry_index index=new Tb_entry_index();
				BeanUtils.copyProperties(entryIndexSx,index);
				entry_indices.add(index);
			}
			totalElements = pages.getTotalElements();
		}else {
			Page<Tb_entry_index> pages = entryIndexRepository.findByEntryidIn(ids, pageRequest);
			entry_indices = pages.getContent();
			totalElements = pages.getTotalElements();
		}
		List<Tb_entry_index_borrow> tb_entry_indexes = new ArrayList<>();
		Map<String,Object[]> parentmap = nodesettingService.findAllParentOfNode();
		for (Tb_entry_index entry_index : entry_indices) {
			Tb_entry_index_borrow eib = new Tb_entry_index_borrow();
			BeanUtils.copyProperties(entry_index, eib);
			eib.setSerial(map.get(entry_index.getEntryid() + "1"));// 审批通过时间
			eib.setEntrysecurity(map.get(entry_index.getEntryid() + "2"));// 查档天数
			if(type==null){
				eib.setEntrystorage(map.get(entry_index.getEntryid() + "3"));
			}
			eib.setState(map.get(entry_index.getEntryid() + "4"));// 归还状态
			eib.setLyqx(map.get(entry_index.getEntryid() + "5"));
			eib.setType(map.get(entry_index.getEntryid() + "6"));  //查档类型
			String fullname="";
			if("soundimage".equals(borrowdoc.getDatasourcetype())){
				fullname= nodesettingService.getSxNodefullnameLoop(entry_index.getNodeid(),"_","");
			}else {
				fullname= electronApproveService.getFullnameByNodeid(parentmap,entry_index.getNodeid());
			}
			eib.setNodefullname(fullname);
			if (eib.getSerial() != null && !"".equals(eib.getSerial())) {
				eib.setResponsible(
						DateUtil.getExpirationDate(eib.getSerial(), Integer.valueOf(eib.getEntrysecurity())));
			} else {
				eib.setResponsible("");
			}
			tb_entry_indexes.add(eib);
		}
		return new PageImpl(tb_entry_indexes, pageRequest, totalElements);
	}

	/**
	 * 根据归还状态查询借出实体数据
	 * 
	 * @param flag
	 *            归还状态
	 * @param page
	 *            页码
	 * @param limit
	 *            每页总数
	 * @return
	 */
	public Page<Tb_entry_index_borrow> getBorrowEntryIndex(String flag, int page, int limit, String condition,
			String operator, String content) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		Specification<Tb_borrowmsg> searchStateAndLyqx = null;
//		if (flag != null && !"".equals(flag)) {
//			searchStateAndLyqx = getSearchStateAndLyqxCondition(flag, "查看");
//		}

		////////////////////////////////////////// 过滤机构用户start///////////////////////////////////////////////
		// String userid = userDetails.getUserid();
		// //1.1查找当前用户所属单位，需要网上找，当前机构可能为部门
		// String organid = userRepository.findOrganidByUserid(userid);
		// Tb_right_organ organ = rightOrganRepository.findOne(organid);
		// while (organ.getOrgantype() != null &&
		// organ.getOrgantype().equals(Tb_right_organ.ORGAN_TYPE_DEPARTMENT)){
		// organ = rightOrganRepository.findOne(organ.getParentid());
		// }
		// //1.2查找当前机构下的所有用户
		// List<String> organidList =
		// organService.getOrganidLoop(organ.getOrganid(), true, new
		// ArrayList<String>());
		// organidList.add(organ.getOrganid());
		// String[] organs = new String[organidList.size()];
		// Specification<Tb_user> specification =
		// UserService.getUsers(organidList.toArray(organs));
		// List<Tb_user> useridList =
		// userRepository.findAll(Specifications.where(specification));
		// Specification<Tb_borrowmsg> SearchBorrowname = new
		// Specification<Tb_borrowmsg>() {
		// @Override
		// public Predicate toPredicate(Root<Tb_borrowmsg> root,
		// CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		// CriteriaBuilder.In p = criteriaBuilder.in(root.get("approver"));
		// for(Tb_user user:useridList){
		// p.value(user.getUserid());
		// }
		// return criteriaBuilder.or(p);
		// }
		// };
		////////////////////////////////////////// 过滤机构用户end///////////////////////////////////////////////

		Specifications specifications = null;
        if (flag != null && !"".equals(flag)) {
            specifications = specifications.where(new SpecificationUtil("state","equal",flag));
        }
        String splitCondition = "";// 用于判断检索条件
		if (condition != null && !"".equals(condition)) {
			splitCondition = condition.split(",")[0];
		}
		// 此处检索使用borrowMsgRepository的方法，故检索字段只能为Tb_borrowmsg中字段，Tb_entry_index表中字段检索暂时无法实现
		if (content != null && ("borrowman".equals(splitCondition) || "msgid".equals(splitCondition)
				|| "borrowdate".equals(splitCondition) || "jyts".equals(splitCondition)
				|| "jybackdate".equals(splitCondition) || "backdate".equals(splitCondition))) {
			specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
		}else if (content != null && !"".equals(content) && ("archivecode".equals(splitCondition) || "funds".equals(splitCondition)
                || "responsible".equals(splitCondition) || "catalog".equals(splitCondition)|| "filecode".equals(splitCondition)
                || "recordcode".equals(splitCondition)|| "title".equals(splitCondition))) {//检索字段为Tb_entry_index表中字段时
            Specifications sp = ClassifySearchService.addSearchbarCondition(null,condition, operator, content);
            List<Tb_entry_index> indexs = entryIndexRepository.findAll(sp);
            String[] entryids = GainField.getFieldValues(indexs, "entryid");
            if(entryids.length==0){
                return null;
            }
            Specification<Tb_borrowmsg> searchentryid = getSearchEntryidCondition(entryids);
            specifications = specifications.and(Specifications.where(searchentryid));//查询出条目
        }
        String[] lyqx = new String[]{"查看","借出"};
        Specification<Tb_borrowmsg> searchlyqx = getSearchlyqxCondition(lyqx);
        specifications = specifications.and(Specifications.where(searchlyqx));
        String[] type = new String[]{"实体查档","电子、实体查档"};
        Specification<Tb_borrowmsg> searchtype = getSearchtypeCondition(type);
        specifications =  specifications.and(Specifications.where(searchtype));
		PageRequest pageRequest = new PageRequest(page - 1, limit);
        Page<Tb_borrowmsg> paegBorrowmsgs = borrowMsgRepository.findAll(specifications, pageRequest);
        List<Tb_borrowmsg> borrowmsgs = paegBorrowmsgs.getContent();
        String[] entryidss = new String[borrowmsgs.size()];
        borrowmsgs.stream().map(msg -> msg.getEntryid()).collect(Collectors.toList()).toArray(entryidss);
		List<Tb_entry_index_borrow> entry_index_borrows = getReturnBorrowEntryList(borrowmsgs, getEntryindexMap(entryidss));
		return new PageImpl(entry_index_borrows, pageRequest, paegBorrowmsgs.getTotalElements());
	}

    /**
     * 根据归还状态查询借出实体数据
     *  @param flag 归还状态
     * @param page 页码
     * @param limit 每页总数
     * @return
     */
    public Page<Tb_entry_index_borrow> getBorrowIndexEntry(String flag, int page, int limit, String condition,
                                                            String operator, String content) {
        Specifications specifications = null;
        if (flag != null && !"".equals(flag)) {
            specifications = specifications.where(new SpecificationUtil("state","equal",flag));
        }
        String splitCondition = "";// 用于判断检索条件
        if (condition != null && !"".equals(condition)) {
            splitCondition = condition.split(",")[0];
        }
        List<Tb_borrowmsg> borrowmsgs = new ArrayList<>();
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        if (content != null && !"".equals(content) && ("archivecode".equals(splitCondition) || "funds".equals(splitCondition)
                || "responsible".equals(splitCondition) || "catalog".equals(splitCondition)|| "filecode".equals(splitCondition)
                || "recordcode".equals(splitCondition)|| "title".equals(splitCondition))) {//检索字段为Tb_entry_index表中字段时
            String searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "sid", operator);
            String sql="select b.* from tb_borrowmsg b left join tb_entry_index sid on b.entryid=sid.entryid " +
                    "where b.type in('实体查档','电子、实体查档','调档') and b.lyqx in('查看','借出') and b.state = '"+flag+"'"+ searchCondition;
            Query query = entityManager.createNativeQuery(sql, Tb_borrowmsg.class);
            borrowmsgs = query.getResultList();
        }else {
            if (content != null && ("borrowman".equals(splitCondition) || "msgid".equals(splitCondition)
                    || "borrowdate".equals(splitCondition) || "jyts".equals(splitCondition)
                    || "jybackdate".equals(splitCondition) || "backdate".equals(splitCondition))) {
                specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
            }
            String[] lyqx = new String[]{"查看","借出"};
            Specification<Tb_borrowmsg> searchlyqx = getSearchlyqxCondition(lyqx);
            specifications = specifications.and(Specifications.where(searchlyqx));
            String[] type = new String[]{"实体查档","电子、实体查档","调档"};
            Specification<Tb_borrowmsg> searchtype = getSearchtypeCondition(type);
            specifications =  specifications.and(Specifications.where(searchtype));
            Page<Tb_borrowmsg> paegBorrowmsgs = borrowMsgRepository.findAll(specifications, pageRequest);
            borrowmsgs = paegBorrowmsgs.getContent();
        }
        if(borrowmsgs.size()==0){
            return null;
        }
        String[] entryidss = new String[borrowmsgs.size()];
        borrowmsgs.stream().map(msg -> msg.getEntryid()).collect(Collectors.toList()).toArray(entryidss);
        List<Tb_entry_index_borrow> entry_index_borrows = getReturnBorrowEntryList(borrowmsgs, getEntryindexMap(entryidss));
        return new PageImpl(entry_index_borrows, pageRequest, borrowmsgs.size());
    }

	/**
	 * 归还
	 *
	 * @param ids
	 *            查档数据id数组
	 * @return
	 */
	public void restitution(String[] ids,String returnMan,String remarkValue) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Tb_borrowmsg> borrowmsgs = borrowMsgRepository.findByMsgidIn(ids);// 获取单据数据
		String[] entryids = GainField.getFieldValues(borrowmsgs, "entryid").length == 0 ? new String[] { "" }
				: GainField.getFieldValues(borrowmsgs, "entryid");
		String[] borrowCodes = GainField.getFieldValues(borrowmsgs, "borrowcode").length == 0 ? new String[] { "" }
				: GainField.getFieldValues(borrowmsgs, "borrowcode");
		changeTaskState();// 判断/删除本机构下到期通知,并通知客户端更新
		// 获取单据实际数据
		Page<Tb_entry_index> pages = entryIndexRepository.findByEntryidIn(entryids, new PageRequest(0, 100));
		List<Tb_entry_index> entry_indices = pages.getContent();
		for (Tb_borrowmsg borrowmsg : borrowmsgs) {
			borrowmsg.setReturnware(returnMan);	//归还人
			borrowmsg.setDescription(remarkValue); 	//备注
			borrowmsg.setState("已归还");
			borrowmsg.setReturntime(DateUtil.getCurrentTime());
			borrowmsg.setReturnloginname(userDetails.getLoginname());
		}
		for (Tb_entry_index entry_index : entry_indices) {
			// 修改库存份数
			entry_index.setKccount(String.valueOf(Integer.parseInt(entry_index.getKccount().trim()) + 1));
		}

		List<Tb_borrowdoc> docList = borrowDocRepository.findByBorrowcodeIn(borrowCodes);
		for (Tb_borrowdoc doc : docList) {
			String state = "全部归还";
			List<Tb_borrowmsg> msgList = borrowMsgRepository.getBorrowmsgsByBorrowdocid(doc.getId());
			for (Tb_borrowmsg msg : msgList) {
				if ("未归还".equals(msg.getState()) && "查看".equals(msg.getLyqx())&&!"电子查档".equals(msg.getType())) {
					state = "部分归还";
					break;
				}
			}
			doc.setReturnstate(state);
		}
	}

    public void moveout(String[] ids) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Tb_borrowdoc> docList = borrowDocRepository.findByDocidIn(ids);
        for (Tb_borrowdoc doc : docList) {
            doc.setReturnstate("已转出");
        }
        List<Tb_borrowmsg> msgList = borrowMsgRepository.getBorrowmsgsByBorrowdocidAndType(ids);
        for (Tb_borrowmsg borrowmsg : msgList) {
            borrowmsg.setReturnware(userDetails.getRealname());	//转出人
            borrowmsg.setState("已转出");
            borrowmsg.setReturntime(DateUtil.getCurrentTime());//转出时间
            borrowmsg.setReturnloginname(userDetails.getLoginname());//转出人账号
        }
//        changeTaskState();// 判断/删除本机构下到期通知,并通知客户端更新
    }

	/**
	 * 获取到期的查档条目
	 *
	 * @return
	 */
	public List<Tb_borrowmsg> getOverdueMsg() {
		String overdueSql = "select * from tb_borrowmsg where "
				+ DBCompatible.getInstance().findExpressionOfDateAddLeNow("jybackdate", "jyts") + " AND state='未归还'";
		Query query = entityManager.createNativeQuery(overdueSql, Tb_borrowmsg.class);
		return query.getResultList();
	}

	/**
	 * 获取即将到期的查档条目
	 *
	 * @return
	 */
	public List<Tb_borrowmsg> getImminentMsg() {
		String imminentSql = "select * from tb_borrowmsg where "
				+ DBCompatible.getInstance().findExpressionOfDateAddLeNow("jybackdate", "jyts-1") + " AND state='未归还'";
		Query query = entityManager.createNativeQuery(imminentSql, Tb_borrowmsg.class);
		return query.getResultList();
	}

	public void askToReturn(String[] ids) {
		List<Tb_borrowmsg> borrowmsgs = borrowMsgRepository.findByMsgidIn(ids);// 获取单据数据
		for (Tb_borrowmsg borrowmsg : borrowmsgs) {
			Tb_user user = userService.findByRealname(borrowmsg.getBorrowman());
			Tb_task taskForBorrowman = new Tb_task();
			taskForBorrowman.setLoginname(user != null ? user.getUserid() : "");
			taskForBorrowman.setState(Tb_task.STATE_WAIT_HANDLE);// 待处理
			taskForBorrowman.setText("请尽快归还！" + borrowmsg.getBorrowman() + " 实体查档到期");
			taskForBorrowman.setType("查档到期提醒");
			taskForBorrowman.setTime(new Date());
			taskForBorrowman.setBorrowmsgid(borrowmsg.getId());
			taskRepository.save(taskForBorrowman);// 生成任务
		}
	}

	public Map<String, Tb_entry_index> getEntryindexMap(String[] entryids) {
		Map<String, Tb_entry_index> entryindexMap = new HashMap<String, Tb_entry_index>();
		List<Tb_entry_index> entry_indexs = entryIndexRepository.findByEntryidIn(entryids);
		for (Tb_entry_index entry_index : entry_indexs) {
			entryindexMap.put(entry_index.getEntryid(), entry_index);
		}
		return entryindexMap;
	}

	public List<Tb_entry_index_borrow> getReturnBorrowEntryList(List<Tb_borrowmsg> borrowmsgs,
			Map<String, Tb_entry_index> entryindexMap) {
		List<Tb_entry_index_borrow> entry_index_borrows = new ArrayList<Tb_entry_index_borrow>();
		Map<String,Object[]> parentmap = nodesettingService.findAllParentOfNode();
		for (Tb_borrowmsg borrowmsg : borrowmsgs) {// 获取实际数据拼接完整
			Tb_user user = userRepository.findByUserid(borrowmsg.getApprover());
			Tb_entry_index entry_index = entryindexMap.get(borrowmsg.getEntryid());
			Tb_entry_index_borrow entry_index_borrow = new Tb_entry_index_borrow();
			if (entry_index != null) {
				BeanUtils.copyProperties(entry_index, entry_index_borrow);
				entry_index_borrow.setId(borrowmsg.getId());
				entry_index_borrow.setBorrowman(borrowmsg.getBorrowman());
				entry_index_borrow.setBorrowdate(borrowmsg.getBorrowdate());// 查档时间
				entry_index_borrow.setBorrowmantel(borrowmsg.getBorrowmantel());// 查档电话
				entry_index_borrow.setReturntime(borrowmsg.getReturntime());//归还时间
				entry_index_borrow.setLyqx(borrowmsg.getLyqx());
				entry_index_borrow.setJybackdate(borrowmsg.getJybackdate());// 审批通过时间
				entry_index_borrow.setRenewreason(borrowmsg.getRenewreason());// 续借理由
				entry_index_borrow.setReturnloginname(borrowmsg.getReturnloginname());//归还人账号
				entry_index_borrow.setReturnware(borrowmsg.getReturnware());	//归还人
				entry_index_borrow.setDescription(borrowmsg.getDescription());	//备注
				String fullname = electronApproveService.getFullnameByNodeid(parentmap,entry_index.getNodeid());
				entry_index_borrow.setNodefullname(fullname);
				entry_index_borrow.setApprover(user != null ? user.getRealname() : "");
				if (borrowmsg.getJybackdate() != null && !"".equals(borrowmsg.getJybackdate())) {
					entry_index_borrow
							.setBackdate(DateUtil.getExpirationDate(borrowmsg.getJybackdate(), borrowmsg.getJyts()));// 到期时间
				} else {
					entry_index_borrow.setBackdate("");
				}
				entry_index_borrow.setJyts(borrowmsg.getJyts());
				entry_index_borrows.add(entry_index_borrow);
			}
		}
		return entry_index_borrows;
	}

    public static Specification<Tb_borrowmsg> getSearchEntryidCondition(String[] entryids){
        Specification<Tb_borrowmsg> searchEntryid = null;
        if(entryids!=null && entryids.length > 0){
            searchEntryid = new Specification<Tb_borrowmsg>() {
                @Override
                public Predicate toPredicate(Root<Tb_borrowmsg> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                    CriteriaBuilder.In<String> inValue = criteriaBuilder.in(root.get("entryid"));
                    for(String entryid : entryids){
                        inValue.value(entryid);
                    }
                    return inValue;
                }
            };
        }
        return searchEntryid;
    }

	public static Specification<Tb_borrowmsg> getSearchtypeCondition(String[] type) {
		Specification<Tb_borrowmsg> searchType = new Specification<Tb_borrowmsg>() {
			@Override
			public Predicate toPredicate(Root<Tb_borrowmsg> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
                Predicate[] predicates = new Predicate[type.length];
                for (int i = 0; i < type.length; i++) {
                    predicates[i] = criteriaBuilder.equal(root.get("type"), type[i]);
                }
                return criteriaBuilder.or(predicates);
			}
		};
		return searchType;
	}

    public static Specification<Tb_borrowmsg> getSearchlyqxCondition(String[] lyqx) {
        Specification<Tb_borrowmsg> searchlyqx = new Specification<Tb_borrowmsg>() {
            @Override
            public Predicate toPredicate(Root<Tb_borrowmsg> root, CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder criteriaBuilder) {
                Predicate[] predicates = new Predicate[lyqx.length];
                for (int i = 0; i < lyqx.length; i++) {
                    predicates[i] = criteriaBuilder.equal(root.get("lyqx"), lyqx[i]);
                }
                return criteriaBuilder.or(predicates);
            }
        };
        return searchlyqx;
    }

	public String checkExpireGhEntry() throws Exception {
		String state = "0";
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Tb_user_node> user_nodes = userNodeRepository.getUserNodes(stborrowWorkid);
		String[] userids = GainField.getFieldValues(user_nodes, "userid").length == 0 ? new String[] { "" }
				: GainField.getFieldValues(user_nodes, "userid");
		// 过滤其它机构用户
		userids = userService.getFilteredUseridByOrgan(userids);
		StringBuffer approversBuff = new StringBuffer();
		approversBuff.append("'");
		for (String str : userids) {
			approversBuff.append(str + "','");
		}
		// 因为过滤会去掉当前用户，加上自己
		approversBuff.append(userDetails.getUserid() + "','");
		approversBuff.append("'");
		String overdueSql = "select * from tb_borrowmsg where approver in (" + approversBuff.toString() + ")" + " and "
				+ DBCompatible.getInstance().findExpressionOfDateAddLeNow("jybackdate", "jyts") + " AND state='未归还'";
		Query query = entityManager.createNativeQuery(overdueSql, Tb_borrowmsg.class);
		List<Tb_borrowmsg> borrowmsgs = query.getResultList();
		if (borrowmsgs != null && borrowmsgs.size() > 0) {
			state = "1";
		}
		return state;
	}

	/**
	 * 删除任务信息并且通知客户端更新
	 */
	public void changeTaskState() {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String overdueSql = "select * from tb_borrowmsg where "
				+ DBCompatible.getInstance().findExpressionOfDateAddLeNow("jybackdate", "jyts")
				+ " AND state='未归还'  and approver='" + userDetails.getUserid() + "'";
		Query query = entityManager.createNativeQuery(overdueSql, Tb_borrowmsg.class);
		List<Tb_borrowmsg> borrowmsgs = query.getResultList();
		if (borrowmsgs.size() > 0) {
			taskRepository.deleteByLoginnameInAndTasktype(new String[] { userDetails.getUserid() }, "查档到期提醒");
			webSocketService.noticeRefresh();// 通知全部链接用户更新通知
		}
	}

	/**
	 * 查档登记表单提交
	 * 
	 * @param borrowdoc
	 *            表单对象
	 * @return 表单id
	 * @throws Exception
	 */
	public String borrowFormSubmit(Tb_borrowdoc borrowdoc) throws Exception {
		String borrowcode = UUID.randomUUID().toString().replace("-", "");// 查档单号
		String borrowDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
		borrowdoc.setState("已通过"); // 默认修改状态未通过
		borrowdoc.setBorrowcode(borrowcode); // 设置查档单号
		borrowdoc.setType("实体查档"); // 设置查档类型
		borrowdoc.setBorrowdate(borrowDate);// 设置查档时间
		borrowdoc.setReturnstate("未归还");// 设置单据归还状态
		borrowdoc = borrowDocRepository.save(borrowdoc);
		return borrowdoc.getBorrowcode();
	}

	public String borrowModify(Tb_borrowdoc borrowdoc){
		//获取code  -set进对象
		Tb_borrowdoc borrowdoc1 = getBorrowDoc(borrowdoc.getId());
		if(borrowdoc1.getBorrowcode()!=null) {
			borrowdoc.setBorrowcode(borrowdoc1.getBorrowcode());
		}
		borrowdoc.setId(borrowdoc1.getId());
		borrowdoc.setBorrowdate(borrowdoc1.getBorrowdate());
		borrowdoc.setBorrowtyts(borrowdoc1.getBorrowtyts());
		borrowdoc.setType(borrowdoc1.getType());
		borrowdoc.setState(borrowdoc1.getState());
		//borrowdoc.setReturnstate("未归还");// 设置单据归还状态
		borrowdoc.setReturnstate(borrowdoc1.getReturnstate());// 设置单据归还状态
		borrowdoc =borrowDocRepository.save(borrowdoc);
		//更新条目的查档
		//1根据doc的id查询出它所有的msg条目
		List<Tb_borrowmsg> borrowmsgList = borrowMsgRepository.findByBorrowcodeIn(new String[]{borrowdoc.getBorrowcode()});
		for(Tb_borrowmsg borrowmsg:borrowmsgList){
			//borrowmsg.setApprover(userDetails.getUserid());// 设置审批人
			//borrowmsg.setBorrowcode(borrowcode);// 设置查档单号
			//borrowmsg.setBorrowdate(borrowdate);// 设置查档时间
			//borrowmsg.setJybackdate(borrowdate);// 设置查档归还时间现与查档时间一样???
			borrowmsg.setBorrowman(borrowdoc.getBorrowman());// 设置查档人
			borrowmsg.setBorrowmantel(borrowdoc.getBorrowmantel());// 设置查档人电话
			//borrowmsg.setEntryid(entry_index.getEntryid());// 设置条目id
			borrowmsg.setJyts(borrowdoc.getBorrowts());// 设置查档天数
			borrowMsgRepository.save(borrowmsg);
		}
		return borrowdoc.getBorrowcode();
	}

	/**
	 * 查档数据导入
	 * 
	 * @param borrowcode
	 *            查档单号
	 * @param dataids
	 *            需要导入的数据
	 * @return 成功数量
	 */
	public int[] entryImport(String borrowcode, String[] dataids) throws Exception {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String borrowdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
		List<Tb_entry_index> entry_indices = entryIndexRepository.findByEntryidIn(dataids);
		Tb_borrowdoc borrowdoc = borrowDocRepository.findByBorrowcode(borrowcode);
		List<Tb_borrowmsg> hasBorrowmsgs = borrowMsgRepository
				.findByBorrowcodeInAndEntryidIn(new String[] { borrowcode }, dataids);
		List<String> hasEntryids = new ArrayList<>();
		for (Tb_borrowmsg borrowmsg : hasBorrowmsgs) {
			if("未归还".equals(borrowmsg.getState())) {
				hasEntryids.add(borrowmsg.getEntryid());
			}
		}
		List<Tb_borrowmsg> borrowmsgs = new ArrayList<>();
		int repeatCount = 0;// 重复条目数
		int notKcCount = 0;// 库存份数不足的条目数
		for (Tb_entry_index entry_index : entry_indices) {
			String kccountStr = entry_index.getKccount()==null||"".equals(entry_index.getKccount())?"0":entry_index.getKccount().trim();
			Integer kccount = Integer.valueOf(kccountStr);
			if (hasEntryids.contains(entry_index.getEntryid())) {// 判断是否重复
				repeatCount++;
				continue;
			}

			if (kccount == 0) {// 判断是否包含有库存份数为0的条目
				notKcCount++;
				continue;
			}

			entry_index.setKccount(String.valueOf(kccount - 1));// 重新计算库存份数
			Tb_borrowmsg borrowmsg = new Tb_borrowmsg();
			borrowmsg.setApprover(userDetails.getUserid());// 设置审批人
			borrowmsg.setBorrowcode(borrowcode);// 设置查档单号
			borrowmsg.setBorrowdate(borrowdate);// 设置查档时间
			borrowmsg.setJybackdate(borrowdate);// 设置查档归还时间现与查档时间一样???
			borrowmsg.setBorrowman(borrowdoc.getBorrowman());// 设置查档人
			borrowmsg.setBorrowmantel(borrowdoc.getBorrowmantel());// 设置查档人电话
			borrowmsg.setEntryid(entry_index.getEntryid());// 设置条目id
			borrowmsg.setJyts(borrowdoc.getBorrowts());// 设置查档天数
			borrowmsg.setLyqx("借出");// 设置利用权限
			borrowmsg.setState("未归还");// 设置归还状态
			borrowmsgs.add(borrowmsg);
			borrowdoc.setReturnstate("未归还");
		}

		borrowMsgRepository.save(borrowmsgs);
		return new int[] { dataids.length - repeatCount - notKcCount, repeatCount, notKcCount };
	}

	public String deteteImport(String[] borrowCodes) {
		List<Tb_borrowmsg> borrowmsgs = borrowMsgRepository.findByBorrowcodeIn(borrowCodes);// 获取查档条目
		String msg = "";
		for (Tb_borrowmsg borrowmsg : borrowmsgs) {
			if ("未归还".equals(borrowmsg.getState())) {// 判断是否存在未归还记录
				Tb_entry_index entry_index = entryIndexRepository.findByEntryid(borrowmsg.getEntryid());
				if(entry_index!=null) {
					msg += ",</br>" + entry_index.getArchivecode();
				}
			}
		}

		if (!"".equals(msg)) {
			msg = "档号为:[" + msg.substring(1) + "</br>]的条目存在未归还记录,请归还后再删除!";
		} else {
			borrowDocRepository.deleteByBorrowcodeIn(borrowCodes);
			borrowMsgRepository.deleteByBorrowcodeIn(borrowCodes);
			msg = "删除成功";
		}

		return msg;
	}

	/**
	 * 根据查档单号显示查档条目
	 * 
	 * @param page
	 *            页码
	 * @param limit
	 *            分页条目数
	 * @param borrowcode
	 *            查档单号
	 * @return 返回数据
	 */
	public Page<Tb_entry_index> getMyBorrowmsgs(int page, int limit, String borrowcode) {
		Specifications specifications = ClassifySearchService.addSearchbarCondition(null, "borrowcode", "equal",
				borrowcode);
		List<Tb_borrowmsg> borrowmsgs = borrowMsgRepository.findAll(specifications);
		Specification<Tb_entry_index> searchEntryidsCondition = new Specification<Tb_entry_index>() {
			@Override
			public Predicate toPredicate(Root<Tb_entry_index> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				CriteriaBuilder.In in = criteriaBuilder.in(root.get("entryid"));
				in.value("");
				for (Tb_borrowmsg borrowmsg : borrowmsgs) {
					in.value(borrowmsg.getEntryid());
				}
				return criteriaBuilder.or(in);
			}
		};
		PageRequest pageRequest = new PageRequest(page - 1, limit);
		return entryIndexRepository.findAll(searchEntryidsCondition, pageRequest);
	}

	/**
	 * 根据查档单号显示未归查档条目
	 *
	 * @param page
	 *            页码
	 * @param limit
	 *            分页条目数
	 * @param borrowcode
	 *            查档单号
	 * @return 返回数据
	 */
	public Page<Tb_entry_index> getMyWGBorrowmsgs(int page, int limit, String borrowcode) {
		Specifications specifications = ClassifySearchService.addSearchbarCondition(null, "borrowcode", "equal",
				borrowcode);
		List<Tb_borrowmsg> borrowmsgs = borrowMsgRepository.findByBorrowcodeAndState(borrowcode);
		Specification<Tb_entry_index> searchEntryidsCondition = new Specification<Tb_entry_index>() {
			@Override
			public Predicate toPredicate(Root<Tb_entry_index> root, CriteriaQuery<?> criteriaQuery,
										 CriteriaBuilder criteriaBuilder) {
				CriteriaBuilder.In in = criteriaBuilder.in(root.get("entryid"));
				in.value("");
				for (Tb_borrowmsg borrowmsg : borrowmsgs) {
					in.value(borrowmsg.getEntryid());
				}
				return criteriaBuilder.or(in);
			}
		};
		PageRequest pageRequest = new PageRequest(page - 1, limit);
		return entryIndexRepository.findAll(searchEntryidsCondition, pageRequest);
	}
	/**
	 * 移除查档数据导入
	 * 
	 * @param borrowcode
	 *            查档单号
	 * @param dataids
	 *            需要导入的数据
	 * @return 成功数量
	 */
	public int removeImport(String borrowcode, String[] dataids) throws Exception {
		List<String> entryids= new ArrayList<>();
		for(String str:dataids) {
			if (str != null && !"".equals(str)) {
				List<Tb_borrowmsg> borrowmsg = borrowMsgRepository.findBycodeAndEntryid(borrowcode, str);
				for(Tb_borrowmsg tb_borrowmsg:borrowmsg) {
					if ("未归还".equals(tb_borrowmsg.getState())) {
						entryids.add(tb_borrowmsg.getEntryid());
					}
				}
			}
		}
		int states = borrowMsgRepository.deleteByBorrowcodeAndEntryidIn(borrowcode, dataids);
		if (states > 0) {
			//1.获取需要移除的条目
			List<Tb_entry_index> entry_indices = entryIndexRepository.findByEntryidIn(dataids);
			for (Tb_entry_index entry_index : entry_indices) {// 重置库存份数
				for(String id:entryids){
					if(entry_index.getEntryid().equals(id)) {
						entry_index.setKccount(Integer.parseInt(entry_index.getKccount().trim()) + 1 + "");
					}
				}
			}
		}
		//获取单据的全部条目
		//int allCount=borrowMsgRepository.findByBorrowCode(borrowcode);
		List<Tb_borrowmsg> wgborrowmsgs = borrowMsgRepository.findByBorrowcodeAndState(borrowcode);
		List<Tb_borrowmsg> ygBorrowmsgs = borrowMsgRepository.findYGByBorrowcodeAndState(borrowcode);
		//Tb_borrowdoc borrowdoc = borrowDocRepository.findByDocid(borrowcode);
		if(wgborrowmsgs.size()>0&&ygBorrowmsgs.size()>0){//存在未归条目
			//部分归还
			borrowDocRepository.setBorrowdocState("部分归还",borrowcode);
		}else if(ygBorrowmsgs.size()>0&&wgborrowmsgs.size()==0){
			//全部归还
			borrowDocRepository.setBorrowdocState("全部归还",borrowcode);
		}else if(ygBorrowmsgs.size()==0&&wgborrowmsgs.size()>0){
			//未归还
			borrowDocRepository.setBorrowdocState("未归还",borrowcode);
		}else if(ygBorrowmsgs.size()==0&&wgborrowmsgs.size()==0){
			//全部归还
			borrowDocRepository.setBorrowdocState("全部归还",borrowcode);
		}
		return states;
	}
	
	public Page getCommonDealDetails(String msgid,String approveTitle,String handleMan){
        List<DealDetails> dealDetails = new ArrayList<>(); //办理详情集合
        List<Sort.Order> orders=new ArrayList<>();
        orders.add( new Sort.Order(Sort.Direction. ASC, "approvedate"));
        orders.add( new Sort.Order(Sort.Direction. ASC, "flowsid"));
        Pageable pageable= new PageRequest(0,100, new Sort(orders));
        List<Tb_flows> flows = flowsRepository.findByMsgid(msgid,pageable);
        String[] spmanIds = new String[flows.size()];      //审批人id
        Map<String,String> idRealnameMap = new HashMap<>();//审批人id与审批人姓名对应集合
        Map<String,String> nodeApproveMap = new HashMap<>();//节点与审批意见对应集合
        String[] approves = approveTitle!=null?approveTitle.split("意见"):new String[]{};//审批意见
        for(int i=0;i<flows.size();i++){
            Tb_flows flow = flows.get(i);
            spmanIds[i] = flow.getSpman()!=null?flow.getSpman():"";
            if(!"启动".equals(flow.getText())&&!"结束".equals(flow.getText())){
                for(String approve:approves){
                    if(approve.indexOf(flow.getText())>-1){
                        nodeApproveMap.put(flow.getText(),approve);
                    }
                }
            }
        }

        List<Tb_user> users = userRepository.findByUseridIn(spmanIds);
        for(Tb_user user:users){
            idRealnameMap.put(user.getUserid(),user.getRealname());
        }

        for(Tb_flows flow:flows){
            String approveText = nodeApproveMap.get(flow.getText());//获取环节
            approveText = approveText!=null?"意见"+approveText.substring(0,approveText.indexOf(flow.getText())):null;//获取批示
            String realname = null;//获取审批人
            if("结束".equals(flow.getText())){
                realname = idRealnameMap.get(flows.get(flows.size()-2).getSpman());
            }else{
                realname = idRealnameMap.get(flow.getSpman())!=null?idRealnameMap.get(flow.getSpman()):handleMan;
            }
            String date = "处理中".equals(flow.getState())?null:flow.getDate()+"";
            dealDetails.add(new DealDetails(flow.getText(), realname, flow.getState(), date, approveText));
        }

        return new PageImpl(dealDetails,null,0);
    }

	public Page getDealDetails(String borrowdocid){
		Tb_borrowdoc borrowdoc = borrowDocRepository.findByDocid(borrowdocid);
		if(borrowdoc!=null){
			return getCommonDealDetails(borrowdoc.getBorrowcode(),borrowdoc.getApprove(),borrowdoc.getBorrowman());
		}
		return new PageImpl(new ArrayList(),null,0);
	}

	//获取单据的归还状态
	public String getBrrowdocReturnState(String[] docids){

		for(String id:docids) {
			Tb_borrowdoc borrowdoc = borrowDocRepository.findByDocid(id);
		}
		String returnState="";
		return returnState;
	}

	/**
	 * 获取全部单据未借出记录
	 *
	 * @param page
	 *            页码
	 * @param limit
	 *            每页数
	 * @return 分页单据
	 */
	public Page<Tb_borrowdoc> getOutwareBorrowdocs(String outwarestate,String type,int page, int limit, String condition, String operator, String content) {
		Sort sort = new Sort(Sort.Direction.DESC, "borrowdate");
		List<String> borrowcodes = borrowMsgRepository.findBorrowcodes();
		Specifications specifications = ClassifySearchService.addSearchbarCondition(Specifications.where(getSearchStateCondition("已通过")), condition, operator, content);
		PageRequest pageRequest = new PageRequest(page - 1, limit, sort);
		return borrowDocRepository.findAll(specifications.and(getOutwareStateCondition(borrowcodes)).and(new SpecificationUtil("outwarestate","equal",outwarestate)), pageRequest);
	}

	public static Specification<Tb_borrowdoc> getOutwareStateCondition(List<String> borrowcodes){
		Specification<Tb_borrowdoc> searchStateCondition = new Specification<Tb_borrowdoc>() {
			@Override
			public Predicate toPredicate(Root<Tb_borrowdoc> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				CriteriaBuilder.In<String> inValue = criteriaBuilder.in(root.get("borrowcode"));
				for(String borrowcode : borrowcodes){
					inValue.value(borrowcode);
				}
				return inValue;
			}
		};
		return searchStateCondition;
	}

	public void setOutwareState(String[] borrowcodes,String type){
		List<Tb_borrowdoc> borrowdocs = borrowDocRepository.findByBorrowcodeIn(borrowcodes);
        List<Tb_borrowmsg> borrowmsgs =  borrowMsgRepository.findByBorrowcodeInAndType(borrowcodes,"调档");
        List list = new ArrayList();
        for(Tb_borrowmsg borrowmsg:borrowmsgs){
            borrowmsg.setState("已调档");
            list.add(borrowmsg.getBorrowcode());
        }
		for(Tb_borrowdoc borrowdoc : borrowdocs){
			borrowdoc.setOutwarestate("已借出");
			if(list.size()>0 && list.contains(borrowdoc.getBorrowcode())){
                borrowdoc.setReturnstate("已调档");
            }
			if("1".equals(type)){
				taskRepository.updateByBorrowmsgid(borrowdoc.getBorrowcode(),"实体出库");
			}
		}
	}

	public Map<String,List<Tb_entry_index>> getBorrowMsgEntryid(String[] borrowcodes){
		String[] entryids = borrowMsgRepository.getBorrowMsgEntryid(borrowcodes);
		List<String> hasInwareEntrys = new ArrayList<>();
		List<String> noInwareEntrys = new ArrayList<>();
		Map<String,List<Tb_entry_index>> map = new HashMap<>();
		for(int i=0;i<entryids.length;i++){
            List<Storage> storage = storageRepository.getByEntry(entryids[i]);
			if(storage!=null){
				hasInwareEntrys.add(entryids[i]);
			}else{
				noInwareEntrys.add(entryids[i]);
			}
		}
		String[] hasInwareStrs = new String[hasInwareEntrys.size()];
		hasInwareEntrys.toArray(hasInwareStrs);
		String[] noInwareStrs = new String[noInwareEntrys.size()];
		noInwareEntrys.toArray(noInwareStrs);
		List<Tb_entry_index> hasEntrys = entryIndexRepository.findByEntryidIn(hasInwareStrs);
		List<Tb_entry_index> noEntrys = new ArrayList<>();
		if (noInwareEntrys.size()>0){
			noEntrys = entryIndexRepository.findByEntryidIn(noInwareStrs);
		}
		map.put("hasEntrys",hasEntrys);
		map.put("noEntrys",noEntrys);
		return map;
	}

	public void setAppraise(String borrowdocid,String labeltext,String content){
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Tb_feedback feedback = feedbackRepository.findByBorrowdocid(borrowdocid);
		if(feedback!=null){   //判断修改还是新增
			feedback.setAppraise(labeltext);
			feedback.setAppraisetext(content);
			feedback.setContent(content);
			feedback.setAsktime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
			feedbackRepository.save(feedback);
		}else {
			Tb_borrowdoc borrowdoc = borrowDocRepository.findByDocid(borrowdocid);
			Tb_feedback feedbackset = new Tb_feedback();
			feedbackset.setAppraise(labeltext);
			feedbackset.setBorrowdocid(borrowdoc.getId());
			feedbackset.setTitle(borrowdoc.getBorrowdate()+" "+borrowdoc.getDesci());
			feedbackset.setAppraisetext(content);
			feedbackset.setFlag("未回复");
			feedbackset.setContent(content);
			feedbackset.setAskman(userDetails.getRealname());
			feedbackset.setAsktime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
			feedbackset.setSubmiterid(userDetails.getUserid());  //设置提交人id
			feedbackRepository.save(feedbackset);
		}
	}

	public ExtMsg getAppraise(String borrowdocid){
		Tb_feedback feedback = feedbackRepository.findByBorrowdocid(borrowdocid);
		if(feedback!=null){
			return new ExtMsg(true,"",feedback);
		}else{
			return new ExtMsg(false,"",null);
		}
	}

	public Page<Tb_reserve> getReservationdocs(String type,int page, int limit, String condition, String operator, String content,String lymode,String taskid,Sort sortobj) {
	    Sort sort = new Sort(Sort.Direction.DESC, "djtime");
        PageRequest pageRequest = new PageRequest(page-1, limit, sortobj==null?sort:sortobj);
		if(taskid!=null){
			return reserveRepository.findByBorrowmig(pageRequest,taskid);
		}
		Specifications sp = ClassifySearchService.addSearchbarCondition(Specifications.where(getSearchlymodeCondition(lymode)), condition, operator, content);
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if("Ly".equals(type) && "查档预约".equals(lymode)){//利用平台只能看到用户自己的预约单据
            sp = sp.and(Specifications.where(getSearchSubmiteridCondition(userDetails.getUserid())));
        }
		return reserveRepository.findAll(sp, pageRequest);
	}

    public Tb_reserve reservationAddForm(Tb_reserve tb_reserve,String[] eleids){
        Tb_reserve reserve = reserveRepository.save(tb_reserve);
		if (eleids != null) {
			List<Tb_electronic> electronics = electronicRepository.findByEleidInOrderBySortsequence(eleids);
			for (Tb_electronic electronic : electronics) {
				// 获取原来电子文件
				File targetFile = new File(rootpath + electronic.getFilepath(), electronic.getFilename());
				// 获取新的存储电子文件路径
				String filepath = electronicService.getUploadDirBorrow(reserve.getDocid())
						.replace(rootpath, "");
				// 把之前原来电子文件转存到存储路径
				targetFile.renameTo(new File(rootpath + filepath, electronic.getFilename()));
				// 转存完成后删除原来的文件
				targetFile.delete();
				electronic.setEntryid(reserve.getDocid());
				electronic.setFilepath(filepath);
			}
		}
		return reserve;
    }

	//对应的展厅的指定日期的预约人数+申请来馆人数，判断是否当天预约人数已满，已满的话返回提示预约另一天
	public boolean checkAudiences(Tb_reserve tbreserve, String showroomid, int yyPerson){
		String yytime=tbreserve.getYytime();//预约时间  2020-03-18 11:48:25
		if(yytime!=null&&yytime.length()>10){
			yytime=yytime.substring(0,10);//截取到日期
		}
		//查询该展厅的当天已预约人数和展厅的每日参观人数限制
		Tb_showroom_date_person sdp= showroomDatePersonRepository.findByShoeroomidAndVisitingdate(showroomid, yytime);
		if(sdp!=null){
			int sum=sdp.getAudiences()+yyPerson;//当天预约总数
			Tb_showroom showroom=showroomRepository.findByShowroomid(showroomid);
			if(sum>showroom.getAudiences()){//预约总人数大于展厅人数
				return false;//提示预约人数超出
			}else{//更新当天的预约总人数
				sdp.setAudiences(sum);
			}
		}else{//当天该展厅暂无预约信息，可以新增一条信息
			Tb_showroom_date_person newSdp=new Tb_showroom_date_person();
			newSdp.setShowroomid(showroomid);
			newSdp.setVisitingdate(yytime);
			newSdp.setAudiences(yyPerson);
			showroomDatePersonRepository.save(newSdp);
		}
		return true;
	}

	public void setYyTask(Tb_reserve tb_reserve){
		//拥有预约管理权限的用户进行提醒
		List<String> userids = userFunctionRepository.findUseridsByFunctionname("预约管理");
		List<String> useridList = userRoleRepository.findUseridsByFunctionname("预约管理");
		userids.removeAll(useridList);
		userids.addAll(useridList);  //所有拥有预约管理权限的用户
		List<Tb_task> taskReserves = new ArrayList<>();
		for(String userid : userids){
			//预约通知提醒
			Tb_task task = new Tb_task();
			task.setLoginname(userid);
			task.setState(Tb_task.STATE_WAIT_HANDLE);// 待处理
			task.setText("有新预约，请回复!");
			task.setType("预约提醒");
			task.setTime(new Date());
			task.setBorrowmsgid(tb_reserve.getDocid());
			taskReserves.add(task);
		}
		taskRepository.save(taskReserves);
	}

	public String reservationReplyAddForm(Tb_reserve tb_reserve,String docid,String taskid){
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Tb_task taskNew = taskRepository.findByBorrowmsgidAndLoginname(docid,userDetails.getUserid());// 获取任务修改任务状态
		if("完成".equals(taskNew.getState())){
			return "2";
		}else{
			Tb_reserve tbreserve = reserveRepository.findByDocid(docid);
			tbreserve.setReplier(tb_reserve.getReplier());
			tbreserve.setReplytime(tb_reserve.getReplytime());
			tbreserve.setReplycontent(tb_reserve.getReplycontent());
			tbreserve.setYystate(tb_reserve.getYystate());
			taskRepository.updateByMsgid(docid);  //更新所有预约回复任务状态
			reserveRepository.save(tbreserve);
			return "1";
		}
	}

	public Tb_reserve reservationCancelAddForm(String docid){
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Tb_reserve tb_reserve = reserveRepository.findByDocid(docid);
        tb_reserve.setCanceler(userDetails.getRealname());
        tb_reserve.setCanceltime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		tb_reserve.setYystate("已取消");
		//展厅参观取消预约时，更新已预约人数表的对应展厅的当天预约人数
		String showroomid=tb_reserve.getBorrowdate();//预约中只有展厅预约才在Borrowdate有存储信息
		if(showroomid!=null && !"".equals(showroomid)){
			String date=tb_reserve.getYytime();
			if(date!=null && date.length()>10){//预约时间 截取到日期
				//获取展厅预约记录
				Tb_showroom_date_person sdp = showroomDatePersonRepository.findByShoeroomidAndVisitingdate(showroomid,date.substring(0,10));
				if(sdp!=null){
					//获取来馆人数
					int lgnum=0;
					String lgPerson = tb_reserve.getBorrowmantime();
					try{
						lgnum = Integer.valueOf(lgPerson);
					}catch(Exception e){
						e.printStackTrace();
					}
					//更新预约人数
					if(sdp.getAudiences()>lgnum){//总预约人数>该预约来馆人数
						sdp.setAudiences(sdp.getAudiences()-lgnum);
					}else{
						sdp.setAudiences(0);
					}
				}
			}
		}
		return reserveRepository.save(tb_reserve);
	}

	public void inwareReturn(String ids){
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String[] entryids = ids.split(",");
		List<Tb_borrowmsg> borrowmsgs = borrowMsgRepository.findByEntryidInAndLyqxAndState(entryids,"查看","未归还");
		for(Tb_borrowmsg borrowmsg:borrowmsgs){
			borrowmsg.setState("已归还");
			borrowmsg.setReturntime(DateUtil.getCurrentTime());
			borrowmsg.setReturnloginname(userDetails.getLoginname());
			Tb_entry_index entryIndex = entryIndexRepository.findByEntryid(borrowmsg.getEntryid());
			entryIndex.setKccount(String.valueOf(Integer.valueOf(entryIndex.getKccount().trim()) + 1));
			entryIndexRepository.save(entryIndex);
		}
		borrowMsgRepository.save(borrowmsgs);
		List<Tb_borrowdoc> docList = borrowDocRepository.findByEntryids(entryids);
		for (Tb_borrowdoc doc : docList) {
			String state = "全部归还";
			List<Tb_borrowmsg> msgList = borrowMsgRepository.getBorrowmsgsByBorrowdocid(doc.getId());
			for (Tb_borrowmsg msg : msgList) {
				if ("未归还".equals(msg.getState()) && "查看".equals(msg.getLyqx())&&!"电子查档".equals(msg.getType())) {
					state = "部分归还";
					break;
				}
			}
			doc.setReturnstate(state);
		}
	}

	public Tb_borrowdoc isBorrowdocOutware(String borrowcode){
		Tb_borrowdoc borrowdoc = borrowDocRepository.findByBorrowcode(borrowcode);
		if("已借出".equals(borrowdoc.getOutwarestate())){ //更新任务状态
			taskRepository.updateByBorrowmsgid(borrowcode,"实体出库");
		}
		return borrowdoc;
	}

	/**
	 *
	 * 根据docid获取批示
	 * @param docid
	 * @throws
	 */
	public Set<String> getApproveByDocid(String docid,String doccode){
		Set<String> list=new HashSet<>();
		Tb_borrowdoc tb_borrowdoc =null;
		if(doccode==null||"".equals(doccode)){
			tb_borrowdoc= borrowDocRepository.findByDocid(docid);
		}
		if(tb_borrowdoc!=null){
			List<Tb_flows> flows = flowsRepository.findByMsgidOrderByApprovedate(tb_borrowdoc.getBorrowcode());
			String[] approves = tb_borrowdoc.getApprove()!=null?tb_borrowdoc.getApprove().split("意见"):new String[]{};//审批意见
			for(int i=0;i<flows.size();i++){
				Tb_flows flow = flows.get(i);
				if(!"启动".equals(flow.getText())&&!"结束".equals(flow.getText())){
					for(String approve:approves){
						//approve=approve.replaceAll("\n","|");
						if(approve.indexOf(flow.getText())>-1){
							list.add(i+"|"+flow.getText()+"-意见"+approve+"。");
						}
					}
				}
			}
		}
		return list;
	}
}
class DealDetails{
    private String node;
    private String spman;
    private String status;
    private String spdate;
    private String approve;

    public DealDetails(){}

    public DealDetails(String node,String spman,String status,String spdate,String approve){
        this.node = node;
        this.spman = spman;
        this.status = status;
        this.spdate = spdate;
        this.approve = approve;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getSpman() {
        return spman;
    }

    public void setSpman(String spman) {
        this.spman = spman;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSpdate() {
        return spdate;
    }

    public void setSpdate(String spdate) {
        this.spdate = spdate;
    }

    public String getApprove() {
        return approve;
    }

    public void setApprove(String approve) {
        this.approve = approve;
    }
}