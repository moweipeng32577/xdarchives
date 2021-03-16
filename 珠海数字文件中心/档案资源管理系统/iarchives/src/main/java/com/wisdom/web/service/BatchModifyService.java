package com.wisdom.web.service;

import com.wisdom.util.DBCompatible;
import com.wisdom.util.GainField;
import com.wisdom.util.SpecificationUtil;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

import java.net.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by RonJiang on 2018/2/1 0001.
 */
@Service
@Transactional
public class BatchModifyService {

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	EntryIndexRepository entryIndexRepository;

	@Autowired
	EntryIndexCaptureRepository entryIndexCaptureRepository;

	@Autowired
	CodesettingService codesettingService;
	
	@Autowired
	EntryCaptureService entryCaptureService;

	@Autowired
	EntryIndexCaptureService entryIndexCaptureService;
	
	@Autowired
	EntryDetailRepository entryDetailRepository;
	
	@Autowired
	EntryDetailCaptureRepository entryDetailCaptureRepository;

	@Autowired
	EntryIndexTempRepository entryIndexTempRepository;

	@Autowired
	EntryIndexSqTempRepository entryIndexSqTempRepository;

	@Autowired
	EntryIndexService entryIndexService;

	@Autowired
	CodesetRepository codesetRepository;

	@Autowired
	DataNodeRepository dataNodeRepository;

	@Autowired
	TemplateRepository templateRepository;

	@Autowired
	RightOrganRepository rightOrganRepository;
	
	@Autowired
	AuditService auditService;

	@Autowired
	ManageDirectoryService manageDirectoryService;

	@Autowired
	EntryIndexManageRepository entryIndexManageRepository;

	@Autowired
	EntryIndexAcceptRepository entryIndexAcceptRepository;

	@Autowired
    EntryDetailAcceptRepository entryDetailAcceptRepository;

    @Autowired
    EntryDetailManageRepository entryDetailManageRepository;

	private static final String PLACE_FRONT = "front";
	private static final String PLACE_BEHIND = "behind";
	private static final String PLACE_ANYWHERE = "anywhere";

	public String getEntryids(String datanodeid, String[] entryidArr, Object formConditions, String basicCondition,
			String basicOperator, String basicContent, ExtOperators formOperators, ExtDateRangeData daterangedata,
			String logic, boolean ifSearchLeafNode, boolean ifContainSelfNode, String type) {
		String entryids = "";
		if (type != null && !type.equals("") && type.equals("数据管理")) {
//			Tb_entry_index formIndex = (Tb_entry_index) formConditions;
			Tb_entry_index formIndex = new Tb_entry_index();
			BeanUtils.copyProperties(formConditions, formIndex);
			List<Tb_entry_index> entry_indexList = entryIndexService.getEntryList(datanodeid, basicCondition,
					basicOperator, basicContent, formIndex, formOperators, daterangedata, logic, ifSearchLeafNode,
					ifContainSelfNode);
			String[] entryidData = entryidArr;
			for (Tb_entry_index entry_index : entry_indexList) {
				boolean found = false;
				for (String entryid : entryidData) {
					if (entryid.equals(entry_index.getEntryid())) {
						found = true;
						break;
					}
				}
				if (!found) {
					entryids += entry_index.getEntryid() + ",";
				}
			}
		} else if (type != null && !type.equals("") && type.equals("数据采集")) {
//			Tb_entry_index_capture formIndexCapture = (Tb_entry_index_capture) formConditions;
			Tb_entry_index_capture formIndexCapture = new Tb_entry_index_capture();
			BeanUtils.copyProperties(formConditions, formIndexCapture);
			List<Tb_entry_index_capture> entry_indexList = entryIndexCaptureService.getEntryList(datanodeid,
					basicCondition, basicOperator, basicContent, formIndexCapture, formOperators, daterangedata, logic,
					ifSearchLeafNode, ifContainSelfNode);
			//获取待审核的条目,用于过滤数据审核模块中待审核的条目
			String[] auditid = auditService.getAuditEntryidsByNodeid(datanodeid);
			if(auditid.length>0&&!"".equals(auditid[0])){
				int length = entryidArr.length;
				entryidArr= Arrays.copyOf(entryidArr,length+ auditid.length);//扩容
				System.arraycopy(auditid, 0, entryidArr, length,auditid.length);
			}
			String[] entryidData = entryidArr;
			for (Tb_entry_index_capture entry_index : entry_indexList) {
				boolean found = false;
				for (String entryid : entryidData) {
					if (entryid.equals(entry_index.getEntryid())) {
						found = true;
						break;
					}
				}
				if (!found) {
					entryids += entry_index.getEntryid() + ",";
				}
			}
		} else if (type != null && !type.equals("") && type.equals("目录管理")){
			Tb_entry_index_manage formIndex = new Tb_entry_index_manage();
			BeanUtils.copyProperties(formConditions, formIndex);
			List<Tb_entry_index_manage> entry_indexList = manageDirectoryService.getManageEntryList(datanodeid, basicCondition,
					basicOperator, basicContent, formIndex, formOperators, daterangedata, logic, ifSearchLeafNode,
					ifContainSelfNode);
			String[] entryidData = entryidArr;
			for (Tb_entry_index_manage entry_index : entry_indexList) {
				boolean found = false;
				for (String entryid : entryidData) {
					if (entryid.equals(entry_index.getEntryid())) {
						found = true;
						break;
					}
				}
				if (!found) {
					entryids += entry_index.getEntryid() + ",";
				}
			}
		}
			return entryids;
	}

	private List<Tb_entry_index_temp> getTempList(String entryidStr, PageRequest pageRequestAll,
			String uniquetag, String type) {
		List<Tb_entry_index_temp> index_tempList = new ArrayList<>();
		// 先全部数据写进临时表，查的时候才会有多页，不然只能查到一页
		if (type != null && !type.equals("") && type.equals("数据管理")) {
			String sql="select * from v_index_detail where 1=1 "+entryidStr;
			Query query = entityManager.createNativeQuery(sql, Tb_index_detail.class);
			List<Tb_index_detail> entryindexList = query.getResultList();
			for (Tb_index_detail entry_index : entryindexList) {
				Tb_entry_index_temp entry_index_temp = new Tb_entry_index_temp();
				entry_index .setUniquetag(uniquetag);
				BeanUtils.copyProperties(entry_index, entry_index_temp);
				index_tempList.add(entry_index_temp);
			}
		} else if (type != null && !type.equals("") && type.equals("数据采集")) {
			String sql="select * from v_index_detail_capture where 1=1 "+entryidStr;
			Query query = entityManager.createNativeQuery(sql, Tb_index_detail_capture.class);
			List<Tb_index_detail_capture> entryindexCaptureList = query.getResultList();
		for (Tb_index_detail_capture entry_index : entryindexCaptureList) {
			entry_index.setUniquetag(uniquetag);
			Tb_entry_index_temp entry_index_temp = new Tb_entry_index_temp();
			BeanUtils.copyProperties(entry_index, entry_index_temp);
			index_tempList.add(entry_index_temp);
			}
		}else if (type != null && !type.equals("") && type.equals("目录管理")){
			String sql="select * from v_index_detail_manage where 1=1 "+entryidStr;
			Query query = entityManager.createNativeQuery(sql, Tb_index_detail_manage.class);
			List<Tb_index_detail_manage> entryindexList = query.getResultList();
			for (Tb_index_detail_manage entry_index : entryindexList) {
				Tb_entry_index_temp entry_index_temp = new Tb_entry_index_temp();
				entry_index .setUniquetag(uniquetag);
				BeanUtils.copyProperties(entry_index, entry_index_temp);
				index_tempList.add(entry_index_temp);
			}
		}else if(type != null && !type.equals("") && type.equals("目录接收")){
			String sql="select * from v_index_detail_accept where 1=1 "+entryidStr;
			Query query = entityManager.createNativeQuery(sql, Tb_index_detail_accept.class);
			List<Tb_index_detail_accept> entryindexList = query.getResultList();
			for (Tb_index_detail_accept entry_index : entryindexList) {
				Tb_entry_index_temp entry_index_temp = new Tb_entry_index_temp();
				entry_index .setUniquetag(uniquetag);
				BeanUtils.copyProperties(entry_index, entry_index_temp);
				index_tempList.add(entry_index_temp);
			}
		}
		return index_tempList;
	}

	public Specifications getSpecifications(Specifications specifications, String replaceFieldcode,
			String searchFieldvalue, String type) {
		// 对于fscount、kccount不能用like去查找
		if (type != null && !type.equals("") && type.equals("数据管理")) {
			if ("fscount".equals(replaceFieldcode) || "kccount".equals(replaceFieldcode)) {
				specifications = specifications
						.and(new SpecificationUtil<Tb_entry_index>(replaceFieldcode, "equal", searchFieldvalue));
			} else {
				specifications = specifications
						.and(new SpecificationUtil<Tb_entry_index>(replaceFieldcode, "like", searchFieldvalue));
			}
		} else if (type != null && !type.equals("") && type.equals("数据采集")) {
			if ("fscount".equals(replaceFieldcode) || "kccount".equals(replaceFieldcode)) {
				specifications = specifications.and(
						new SpecificationUtil<Tb_entry_index_capture>(replaceFieldcode, "equal", searchFieldvalue));
			} else {
				specifications = specifications
						.and(new SpecificationUtil<Tb_entry_index_capture>(replaceFieldcode, "like", searchFieldvalue));
			}
		}
		return specifications;
	}

	public String replaceStr(String replaceFieldcode,String searchFieldvalue, String type) {
		String replaceStr="";
		// 对于fscount、kccount不能用like去查找
		if (type != null && !type.equals("")) {
			if ("fscount".equals(replaceFieldcode) || "kccount".equals(replaceFieldcode)) {
				replaceStr=replaceFieldcode+" = '"+searchFieldvalue+"' ";
			} else {
				replaceStr=replaceFieldcode+" like '%"+searchFieldvalue+"%' ";
			}
		}
		return replaceStr;
	}

	/**
	 * 获取批量修改结果预览
	 *
	 * @param entryidArr
	 *            需修改条目的id
	 * @param condition
	 *            检索条件
	 * @param operator
	 *            检索操作符
	 * @param content
	 *            检索内容
	 * @param datanodeid
	 *            节点id
	 * @param fieldModifyData
	 *            修改具体数据
	 * @param page
	 * @param limit
	 * @return
	 */
	public Page<Tb_entry_index_temp> getModifyResultPreview(String[] entryidArr, String condition, String operator,
															String content, String datanodeid, String fieldModifyData, int page, int limit, String isSelectAll,
															Object formConditions, ExtOperators formOperators, ExtDateRangeData daterangedata, String logic,
															boolean ifSearchLeafNode, boolean ifContainSelfNode, String basicCondition, String basicOperator,
															String basicContent, Sort sort, String type,String pageState) {
		String uniquetag = getUniquetagByType("modi");//修改
		//entryIndexTempRepository.deleteByUniquetag(uniquetag);//删除之前的预览结果，新生成的预览结果就不会出现之前的数据
		if("true".equals(pageState)){//翻页状态，不用重新更新临时表数据
		}else{//首次加载新数据
			if ("true".equals(isSelectAll)) {
				String entryids = getEntryids(datanodeid, entryidArr, formConditions, basicCondition, basicOperator,
						basicContent, formOperators, daterangedata, logic, ifSearchLeafNode, ifContainSelfNode, type);
				entryidArr = entryids.substring(0, entryids.length() - 1).split(",");
			}
			List<List<String>>mEndList=new ArrayList<>();
			List<String> mList = Arrays.asList(entryidArr);
			if( mList.size()%500!=0) {
				for (int j = 0; j < mList.size() / 500 + 1; j++) {
					if ((j * 500 + 500) < mList.size()) {
						mEndList.add(mList.subList(j * 500, j * 500+ 500));
					} else if ((j * 500 + 500) > mList.size()) {
						mEndList.add(mList.subList(j * 500, mList.size()));
					} else if (mList.size() <500) {
						mEndList.add(mList.subList(0, mList.size()));
					}
				}
			}else if(mList.size()%500==0){
				for (int j = 0; j < mList.size() / 500; j++) {
					if ((j * 500 + 500) <= mList.size()) {
						mEndList.add(mList.subList(j *500, j * 500 + 500));
					} else if ((j * 500+ 500) > mList.size()) {
						mEndList.add(mList.subList(j *500, mList.size()));
					} else if (mList.size() < 500) {
						mEndList.add(mList.subList(0, mList.size()));
					}
				}
			}
			PageRequest pageRequestAll = new PageRequest(0, 30000000);
			List<Tb_entry_index_temp> index_tempList = new ArrayList<>();
			for(int i=0;i<mEndList.size();i++) {
				List<Tb_entry_index_temp> index_tempListNew = new ArrayList<>();
				List<String> entrys = mEndList.get(i);
				String entryidStr="'"+String.join("','", entrys)+"'";
				entryidStr=" and entryid in("+entryidStr+") ";
				if (type != null && !type.equals("")) {
					index_tempListNew = getTempList(entryidStr, pageRequestAll, uniquetag, type);
					index_tempList.addAll(index_tempListNew);
				}
			}
			Map<String, Object> result = doModifyContent(datanodeid, index_tempList, fieldModifyData);
			List<String> modifyFieldcodeList = (List<String>) result.get("fieldcodeList");
			index_tempList = (List<Tb_entry_index_temp>) result.get("entryindexList");
			setTempArchivecodes(modifyFieldcodeList, datanodeid, index_tempList,uniquetag);// 档号自动重新生成
			entryIndexTempRepository.save(index_tempList);
		}

		Specification<Tb_entry_index_temp> searchUniquetag = uniquetagCondition(uniquetag);
		Specifications sp = Specifications.where(searchUniquetag);
		if (content != null) {
			sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
		}
		PageRequest pageRequest = new PageRequest(page - 1, limit, sort);
		return entryIndexTempRepository.findAll(sp, pageRequest);
	}

	/**
	 * 获取批量替换结果预览
	 *
	 * @param entryidArr
	 *            需修改条目的id
	 * @param condition
	 *            检索条件
	 * @param operator
	 *            检索操作符
	 * @param content
	 *            检索内容
	 * @param datanodeid
	 *            节点id
	 * @param fieldReplaceData
	 *            替换具体数据
	 * @param ifContainSpace
	 *            是否包含前后空格
	 * @param page
	 * @param limit
	 * @return
	 */
	public Page<Tb_entry_index_temp> getReplaceResultPreview(String[] entryidArr, String condition, String operator,
															 String content, String datanodeid, String fieldReplaceData, boolean ifContainSpace, int page, int limit,
															 String isSelectAll, Object formConditions, ExtOperators formOperators, ExtDateRangeData daterangedata,
															 String logic, boolean ifSearchLeafNode, boolean ifContainSelfNode, String basicCondition,
															 String basicOperator, String basicContent, Sort sort, String type,String pageState) {
		String uniquetag = getUniquetagByType("repl");//替换
		if("true".equals(pageState)){//翻页状态，不用重新更新临时表数据
		}else {//首次加载新数据
			/* 参数处理---------------------------------------------start */
			String[] fieldModifyArr = fieldReplaceData.split("∪");
			String replaceFieldcode = fieldModifyArr[0].split("_")[0];
			String searchFieldvalue = fieldModifyArr[1];
			String replacement;
			if (fieldModifyArr.length == 2) {
				replacement = "";
			} else {
				replacement = fieldModifyArr[2];
			}
		/* 参数处理---------------------------------------------end */
		/* 检索条件处理---------------------------------------------start */
			if ("true".equals(isSelectAll)) {
				String entryids = getEntryids(datanodeid, entryidArr, formConditions, basicCondition, basicOperator,
						basicContent, formOperators, daterangedata, logic, ifSearchLeafNode, ifContainSelfNode, type);
				entryidArr = entryids.substring(0, entryids.length() - 1).split(",");
			}
			List<List<String>>mEndList=new ArrayList<>();
			List<String> mList = Arrays.asList(entryidArr);
			if( mList.size()%500!=0) {
				for (int j = 0; j < mList.size() / 500 + 1; j++) {
					if ((j * 500 + 500) < mList.size()) {
						mEndList.add(mList.subList(j * 500, j * 500+ 500));
					} else if ((j * 500 + 500) > mList.size()) {
						mEndList.add(mList.subList(j * 500, mList.size()));
					} else if (mList.size() <500) {
						mEndList.add(mList.subList(0, mList.size()));
					}
				}
			}else if(mList.size()%500==0){
				for (int j = 0; j < mList.size() / 500; j++) {
					if ((j * 500 + 500) <= mList.size()) {
						mEndList.add(mList.subList(j *500, j * 500 + 500));
					} else if ((j * 500+ 500) > mList.size()) {
						mEndList.add(mList.subList(j *500, mList.size()));
					} else if (mList.size() < 500) {
						mEndList.add(mList.subList(0, mList.size()));
					}
				}
			}
			PageRequest pageRequestAll = new PageRequest(page - 1, 300000000);
			List<Tb_entry_index_temp> entry_index_tempList = new ArrayList<>();
			for(int i=0;i<mEndList.size();i++) {
				List<Tb_entry_index_temp> index_tempListNew = new ArrayList<>();
				List<String> entrys = mEndList.get(i);
				String entryidStr="'"+String.join("','", entrys)+"'";
				if (type != null && !type.equals("")) {
					String replaceStr="";
					if ("".equals(DBCompatible.getInstance().findExpressionOf())) {// oracle
						replaceStr = replaceStr( replaceFieldcode, searchFieldvalue, type);
					} else {
						replaceStr = replaceStr(replaceFieldcode, getValue(searchFieldvalue.length(), searchFieldvalue), type);
					}
					String serachCondition=" and entryid in("+entryidStr+") and "+replaceStr;
					index_tempListNew = getTempList(serachCondition, pageRequestAll, uniquetag, type);
					entry_index_tempList.addAll(index_tempListNew);
				}
			}
			Object replace = doReplaceContent(entry_index_tempList, replaceFieldcode, searchFieldvalue, replacement,
					ifContainSpace);
			if (replace != null) {
				entry_index_tempList = (List<Tb_entry_index_temp>) replace;// 需修改字段处理
				List<String> replaceFieldcodeList = new ArrayList<>();
				replaceFieldcodeList.add(replaceFieldcode);
				setTempArchivecodes(replaceFieldcodeList, datanodeid, entry_index_tempList,uniquetag);// 档号自动重新生成
				entryIndexTempRepository.save(entry_index_tempList);
			}else{
				return null;
			}
		}

		Specification<Tb_entry_index_temp> searchUniquetag = uniquetagCondition(uniquetag);
		Specifications sp = Specifications.where(searchUniquetag);
		if (content != null) {
			sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
		}
		PageRequest pageRequest = new PageRequest(page - 1, limit, sort);
		return entryIndexTempRepository.findAll(sp, pageRequest);
	}

	/**
	 * 获取批量增加结果预览
	 *
	 * @param entryidArr
	 *            需修改条目的id
	 * @param condition
	 *            检索条件
	 * @param operator
	 *            检索操作符
	 * @param content
	 *            检索内容
	 * @param datanodeid
	 *            节点id
	 * @param fieldModifyData
	 *            增加具体数据
	 * @param page
	 * @param limit
	 * @return
	 */
	public Page<Tb_entry_index_temp> getAddResultPreview(String[] entryidArr, String condition, String operator,
														 String content, String datanodeid, String fieldModifyData, int page, int limit, String isSelectAll,
														 Object formConditions, ExtOperators formOperators, ExtDateRangeData daterangedata, String logic,
														 boolean ifSearchLeafNode, boolean ifContainSelfNode, String basicCondition, String basicOperator,
														 String basicContent, Sort sort, String type,String pageState) {
		String uniquetag = getUniquetagByType("add");//增加

		if("true".equals(pageState)){//翻页状态，不用重新更新临时表数据
		}else {//首次加载新数据
			if ("true".equals(isSelectAll)) {
				String entryids = getEntryids(datanodeid, entryidArr, formConditions, basicCondition, basicOperator,
						basicContent, formOperators, daterangedata, logic, ifSearchLeafNode, ifContainSelfNode, type);
				entryidArr = entryids.substring(0, entryids.length() - 1).split(",");
			}
		/* 检索条件处理---------------------------------------------start */
			List<List<String>>mEndList=new ArrayList<>();
			List<String> mList = Arrays.asList(entryidArr);
			if( mList.size()%500!=0) {
				for (int j = 0; j < mList.size() / 500 + 1; j++) {
					if ((j * 500 + 500) < mList.size()) {
						mEndList.add(mList.subList(j * 500, j * 500+ 500));
					} else if ((j * 500 + 500) > mList.size()) {
						mEndList.add(mList.subList(j * 500, mList.size()));
					} else if (mList.size() <500) {
						mEndList.add(mList.subList(0, mList.size()));
					}
				}
			}else if(mList.size()%500==0){
				for (int j = 0; j < mList.size() / 500; j++) {
					if ((j * 500 + 500) <= mList.size()) {
						mEndList.add(mList.subList(j *500, j * 500 + 500));
					} else if ((j * 500+ 500) > mList.size()) {
						mEndList.add(mList.subList(j *500, mList.size()));
					} else if (mList.size() < 500) {
						mEndList.add(mList.subList(0, mList.size()));
					}
				}
			}
			PageRequest pageRequestAll = new PageRequest(0, 30000000);
			List<Tb_entry_index_temp> entry_index_tempList = new ArrayList<>();
			for(int i=0;i<mEndList.size();i++) {
				List<Tb_entry_index_temp> index_tempListNew = new ArrayList<>();
				List<String> entrys = mEndList.get(i);
				String entryidStr="'"+String.join("','", entrys)+"'";
				entryidStr=" and entryid in("+entryidStr+") ";
				if (type != null && !type.equals("")) {
					index_tempListNew = getTempList(entryidStr, pageRequestAll, uniquetag, type);
					entry_index_tempList.addAll(index_tempListNew);
				}
			}
			Map<String, Object> result = doAddContent(entry_index_tempList, fieldModifyData);
			List<String> addFieldcodeList = (List<String>) result.get("fieldcodeList");
			entry_index_tempList = (List<Tb_entry_index_temp>) result.get("entryindexList");// 需修改字段处理
			setTempArchivecodes(addFieldcodeList, datanodeid, entry_index_tempList,uniquetag);// 档号自动重新生成
			entryIndexTempRepository.save(entry_index_tempList);
		}

		Specification<Tb_entry_index_temp> searchUniquetag = uniquetagCondition(uniquetag);
		Specifications sp = Specifications.where(searchUniquetag);
		if (content != null) {
			sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
		}
		PageRequest pageRequest = new PageRequest(page - 1, limit, sort);
		return entryIndexTempRepository.findAll(sp, pageRequest);
	}

	/**
	 *
	 * @param condition
	 * @param operator
	 * @param content
	 * @param page
	 * @param limit
	 * @param isSelectAll
	 * @param sort
	 * @param resultType 匹配结果
	 * 1 没找到相关条目
	 * 2 匹配到多条条目
	 * 3  存储位置不够详细
	 * 4  放入密集架空间不足
	 * 5  已入库
	 * 6  存储位置没有匹配到
	 * 7 存储位置信息为空
	 * 8  可以进行入库
	 * @return
	 */
	public Page<Tb_entry_index_temp> getKfResultPreview(String condition, String operator,
														 String content,int page, int limit, String isSelectAll,Sort sort,String resultType) {
		String uniquetag = getUniquetagByType("kfdr");//库房导入
		Specification<Tb_entry_index_temp> searchUniquetag = uniquetagCondition(uniquetag);
		Specifications sp = Specifications.where(searchUniquetag);
		if (content != null) {
			sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
		}
		if (resultType != null&&!"".equals(resultType)) {
			sp = ClassifySearchService.addSearchbarCondition(sp, "sparefield5", "equal", resultType);
		}

		PageRequest pageRequest = new PageRequest(page - 1, limit, sort);
		return entryIndexTempRepository.findAll(sp, pageRequest);
	}

	public boolean updateReplaceEntryindex(String[] entryidArr, String nodeid, String fieldModifyData, String type,String batchtype) {
		String uniquetag = getUniquetagByType(batchtype);
		String[] field = fieldModifyData.split("∩");
		boolean flag=false;
		String fc_pattern = "^[f][0-5][0-9]";//副表字段
		for (int a = 0; a < field.length; a++) {
			String value = field[a].split("∪")[0].split("_")[0];
			field[a]=value;
			if (value.matches(fc_pattern)) {
				flag=true;//有修改副表字段
			}
		}
		//获取到临时数据
//		List<Tb_entry_index_temp> entry_index_temps;
//		if(entryidArr.length > 0){
//			entry_index_temps = entryIndexTempRepository.findByEntryidInAndUniquetag(entryidArr, uniquetag);
//		} else {
//		entry_index_temps = entryIndexTempRepository.findByUniquetagOrderByFilenumberAscDescriptiondateAscTitleAsc(uniquetag);
//		}
		List<String> entry;
		entry = entryIndexTempRepository.findEntryidByUniquetag(uniquetag);
		String[] entryids = new String[entry.size()];
		entry.toArray(entryids);

//		String[] entryids = new String[entry_index_temps.size()];
//		for (int i = 0; i < entry_index_temps.size(); i++) {
//			entryids[i] =  entry_index_temps.get(i).getEntryid();
//		}
		List<String[]> subAry = new InformService().subArray(entryids, 1000);//处理ORACLE1000参数问题
		List<String> codeSettingFieldCodeList = codesettingService.getCodeSettingFields(nodeid);
		if (type != null && !type.equals("") && type.equals("数据管理")) {
			List<Tb_entry_index> entry_indexs = new ArrayList<>();
			List<Tb_entry_detail> entry_details = new ArrayList<>();
			for (String[] ary : subAry) {
				entry_indexs.addAll(entryIndexRepository.findByEntryidIn(ary));
				if(flag){//有修改副表字段就增加副表条目
					entry_details.addAll(entryDetailRepository.findByEntryidIn(ary));
				}
			}
			//List<Tb_entry_index> entry_indexs = entryIndexRepository.findByEntryidIn(entryids);
			for (int a = 0; a < field.length; a++) {
				String value = field[a];
				for (int i = 0; i < entry.size(); i++) {
					for (int j = 0; j < entry_indexs.size(); j++) {
						if (entry.get(i).trim().equals(entry_indexs.get(j).getEntryid())) {
							String[] ids = {entry.get(i)};
							List<Tb_entry_index_temp> index_temp = entryIndexTempRepository.findByEntryidIn(ids);
							if (codeSettingFieldCodeList.contains(value)&&index_temp!=null) {//如果替换字段为档号组成字段
								String archivecode = (String) GainField.getFieldValueByName("archivecode", index_temp.get(0));
								GainField.setFieldValueByName("archivecode", entry_indexs.get(j), archivecode);
							}
							String filedValue = (String) GainField.getFieldValueByName(value, index_temp.get(0));
							if (flag&&value.matches(fc_pattern)) {//副表字段
								GainField.setFieldValueByName(value, entry_details.get(j), filedValue);
							}else{
								GainField.setFieldValueByName(value, entry_indexs.get(j), filedValue);
							}
						}
					}
				}
			}
		} else if (type != null && !type.equals("") && type.equals("数据采集")) {
			List<Tb_entry_index_capture> entry_index_captures = new ArrayList<>();
			List<Tb_entry_detail_capture> entry_detail_captures = new ArrayList<>();
			for (String[] ary : subAry) {
				entry_index_captures.addAll(entryIndexCaptureRepository.findByEntryidIn(ary));
				if (flag) {//有修改副表字段就增加副表条目
					entry_detail_captures.addAll(entryDetailCaptureRepository.findByEntryidIn(ary));
				}
			}
			//List<Tb_entry_index_capture> entry_index_captures = entryIndexCaptureRepository.findByEntryidIn(entryids);
			for (int a = 0; a < field.length; a++) {
				String value = field[a];
				for (int i = 0; i < entry.size(); i++) {
					for (int j = 0; j < entry_index_captures.size(); j++) {
						if (entry.get(i).trim().equals(entry_index_captures.get(j).getEntryid())) {
							String[] ids = {entry.get(i)};
							List<Tb_entry_index_temp> index_temp = entryIndexTempRepository.findByEntryidIn(ids);
							if (codeSettingFieldCodeList.contains(value) && index_temp != null) {//如果替换字段为档号组成字段
								String archivecode = (String) GainField.getFieldValueByName("archivecode", index_temp.get(0));
								GainField.setFieldValueByName("archivecode", entry_index_captures.get(j), archivecode);
							}
							String filedValue = (String) GainField.getFieldValueByName(value, index_temp.get(0));
							if (flag && value.matches(fc_pattern)) {//副表字段
								GainField.setFieldValueByName(value, entry_detail_captures.get(j), filedValue);
							} else {
								GainField.setFieldValueByName(value, entry_index_captures.get(j), filedValue);
							}
						}
					}
				}
			}
		}else if ("目录接收".equals(type)) {
            List<Tb_entry_index_accept> entry_index_accepts = new ArrayList<>();
            List<Tb_entry_detail_accept> entry_detail_accepts = new ArrayList<>();
            for (String[] ary : subAry) {
                entry_index_accepts.addAll(entryIndexAcceptRepository.findByEntryidIn(ary));
                if(flag){//有修改副表字段就增加副表条目
                    entry_detail_accepts.addAll(entryDetailAcceptRepository.findByEntryidIn(ary));
                }
            }
            for (int a = 0; a < field.length; a++) {
                String value = field[a];
                for (int i = 0; i < entry.size(); i++) {
                    for (int j = 0; j < entry_index_accepts.size(); j++) {
                        if (entry.get(i).trim().equals(entry_index_accepts.get(j).getEntryid())) {
							String[] ids = {entry.get(i)};
							List<Tb_entry_index_temp> index_temp = entryIndexTempRepository.findByEntryidIn(ids);
                            if (codeSettingFieldCodeList.contains(value)) {//如果替换字段为档号组成字段
                                String archivecode = (String) GainField.getFieldValueByName("archivecode", index_temp.get(0));
                                GainField.setFieldValueByName("archivecode", entry_index_accepts.get(j), archivecode);
                            }
                            String filedValue = (String) GainField.getFieldValueByName(value, index_temp.get(0));
                            if (flag&&value.matches(fc_pattern)) {//副表字段
                                GainField.setFieldValueByName(value, entry_detail_accepts.get(j), filedValue);
                            }else{
                                GainField.setFieldValueByName(value, entry_index_accepts.get(j), filedValue);
                            }
                        }
                    }
                }
            }
        }else if ("目录管理".equals(type)) {
            List<Tb_entry_index_manage> entry_index_manages = new ArrayList<>();
            List<Tb_entry_detail_manage> entry_detail_manages = new ArrayList<>();
            for (String[] ary : subAry) {
                entry_index_manages.addAll(entryIndexManageRepository.findByEntryidIn(ary));
                if(flag){//有修改副表字段就增加副表条目
                    entry_detail_manages.addAll(entryDetailManageRepository.findByEntryidIn(ary));
                }
            }
            for (int a = 0; a < field.length; a++) {
                String value = field[a];
                for (int i = 0; i < entry.size(); i++) {
                    for (int j = 0; j < entry_index_manages.size(); j++) {
                        if (entry.get(i).trim().equals(entry_index_manages.get(j).getEntryid())) {
							String[] ids = {entry.get(i)};
							List<Tb_entry_index_temp> index_temp = entryIndexTempRepository.findByEntryidIn(ids);
                            if (codeSettingFieldCodeList.contains(value)) {//如果替换字段为档号组成字段
                                String archivecode = (String) GainField.getFieldValueByName("archivecode", index_temp.get(0));
                                GainField.setFieldValueByName("archivecode", entry_index_manages.get(j), archivecode);
                            }
                            String filedValue = (String) GainField.getFieldValueByName(value, index_temp.get(0));
                            if (flag&&value.matches(fc_pattern)) {//副表字段
                                GainField.setFieldValueByName(value, entry_detail_manages.get(j), filedValue);
                            }else{
                                GainField.setFieldValueByName(value, entry_index_manages.get(j), filedValue);
                            }
                        }
                    }
                }
            }
        }
		deleteEntryIndexTempByUniquetagByType(batchtype);
		return true;
	}

	/**
	 * 设置档号
	 *
	 * @param fieldcodeList
	 * @param datanodeid
	 * @return
	 */
	public void setTempArchivecodes(List<String> fieldcodeList, String datanodeid,
									List<Tb_entry_index_temp> entryindexList,String uniquetag) {
		List<String> codeSettingFieldCodeList = codesettingService.getCodeSettingFields(datanodeid);
		List<String> codeSettingSplitCodeList = codesettingService.getCodeSettingSplitCodes(datanodeid);
		List<Object> codeSettingFieldLength = codesetRepository.findFieldlengthByDatanodeid(datanodeid);
		boolean containCodesetFieldFlag = ifContainCodesetField(fieldcodeList, codeSettingFieldCodeList);
		if (containCodesetFieldFlag) {
			List<String> archivecodeList = getArchivecodeList(entryindexList, codeSettingFieldLength,
					codeSettingFieldCodeList, codeSettingSplitCodeList);
			if (archivecodeList != null) {
				for (int i = 0; i < archivecodeList.size(); i++) {
					//entryindexList.get(i).setArchivecode(archivecodeList.get(i));
					entryIndexTempRepository.updateArchivecode(archivecodeList.get(i),entryindexList.get(i).getEntryid(),uniquetag);
					entryindexList.get(i).setArchivecode(archivecodeList.get(i));
				}
			}
		}
	}

	/**
	 * 设置档号
	 *
	 * @param fieldcodeList
	 * @param datanodeid
	 * @return
	 */
	public void setTempArchivecodes(List<String> fieldcodeList, String datanodeid,
			List<Tb_entry_index_temp> entryindexList) {
		List<String> codeSettingFieldCodeList = codesettingService.getCodeSettingFields(datanodeid);
		List<String> codeSettingSplitCodeList = codesettingService.getCodeSettingSplitCodes(datanodeid);
		List<Object> codeSettingFieldLength = codesetRepository.findFieldlengthByDatanodeid(datanodeid);
		boolean containCodesetFieldFlag = ifContainCodesetField(fieldcodeList, codeSettingFieldCodeList);
		if (containCodesetFieldFlag) {
			List<String> archivecodeList = getArchivecodeList(entryindexList, codeSettingFieldLength,
					codeSettingFieldCodeList, codeSettingSplitCodeList);
			if (archivecodeList != null) {
				for (int i = 0; i < archivecodeList.size(); i++) {
					entryindexList.get(i).setArchivecode(archivecodeList.get(i));
				}
			}
		}
	}

	public List<String> getArchivecodeList(List<Tb_entry_index_temp> handlerList, List<Object> codeSettingFieldLength,
			List<String> fieldCodes, List<String> splitCodes) {
		List<String> archivecodeList = new ArrayList<>();
		Map<String, Map<String, String>> mapFiled = entryIndexService.getConfigMap();//获取参数设置的MAP
		for (int i = 0; i < handlerList.size(); i++) {
			String archivecode = "";
			List<Tb_data_template> enumList = templateRepository.getByNodeidFtype("enum", handlerList.get(i).getNodeid());//获取某节点的模板中属于enum的字段
			for (int j = 0; j < fieldCodes.size() - 1; j++) {
				Integer length = Integer.parseInt(codeSettingFieldLength.get(j).toString());
				String splitValue = splitCodes.get(j);
				String fieldValue = GainField.getFieldValueByName(fieldCodes.get(j), handlerList.get(i)) + "";
				if (fieldValue.isEmpty()) {
					return null;
				}
				if (!fieldValue.isEmpty()) {
					if (isNumeric(fieldValue)) {
						fieldValue = entryIndexService.alignValue(length, Integer.valueOf(fieldValue));
					}
					if (fieldCodes.get(j).equals("organ")) {
						Tb_data_node node = dataNodeRepository.findByNodeid(handlerList.get(i).getNodeid());
						Tb_right_organ right_organ = rightOrganRepository.findByOrganid(node.getRefid());
						if (right_organ.getCode() != null && !right_organ.getCode().equals("")) {
							fieldValue = right_organ.getCode();
						}
						GainField.setFieldValueByName(fieldCodes.get(j), handlerList.get(i), right_organ.getOrganname());
					}else{
						GainField.setFieldValueByName(fieldCodes.get(j), handlerList.get(i), fieldValue);
					}
					fieldValue = entryIndexService.getConfigByName(fieldCodes.get(j), fieldValue, enumList, mapFiled);
					if (isNumeric(fieldValue)) {
						fieldValue = entryIndexService.alignValue(length, Integer.valueOf(fieldValue));
					}
					archivecode += fieldValue + splitValue;
				}
			}
			archivecode += (GainField.getFieldValueByName(fieldCodes.get(fieldCodes.size() - 1), handlerList.get(i))
					+ "");
			archivecodeList.add(archivecode);
		}
		return archivecodeList;
	}

	/**
	 * 检验批量修改字段是否包含档号构成字段
	 *
	 * @param modifyFields
	 *            需批量修改字段
	 * @param codeSettingFields
	 *            档号构成字段
	 * @return
	 */
	public boolean ifContainCodesetField(List<String> modifyFields, List<String> codeSettingFields) {
		boolean ifContainCodesetField = false;
		for (int i = 0; i < modifyFields.size(); i++) {
			if (codeSettingFields.contains(modifyFields.get(i))) {
				ifContainCodesetField = true;
				break;
			}
		}
		return ifContainCodesetField;
	}

	private boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	public List<String> getArchivecodeList(Object handlerList, List<String> fieldCodes, List<String> splitCodes, String type) {
        List<String> archivecodeList = new ArrayList<>();
        String archivecode = "";
        String nodeid = "";
        String sql = "";
        if (type.equals("数据管理")) {
        	Tb_entry_index entry_index = (Tb_entry_index) handlerList;
        	nodeid = entry_index.getNodeid();
        } else if (type.equals("数据采集")) {
        	Tb_entry_index_capture entry_index = (Tb_entry_index_capture) handlerList;
        	nodeid = entry_index.getNodeid();
        }
        //获取所有计算项长度
		Map<String, Map<String, String>> mapFiled = entryIndexService.getConfigMap();//获取参数设置的MAP
		List<Tb_data_template> enumList = templateRepository.getByNodeidFtype("enum",nodeid);//获取某节点的模板中属于enum的字段
        List<Object> fieldlength = codesetRepository.findFieldlengthByDatanodeid(nodeid);
        for (int j = 0; j < fieldCodes.size(); j++) {
            String fieldValue = GainField.getFieldValueByName(fieldCodes.get(j), handlerList) + "";
            String splitValue = splitCodes.get(j);
            if (!fieldValue.isEmpty()) {
            	if (entryCaptureService.isNumeric(fieldValue)) {
            		Integer length = Integer.parseInt(fieldlength.get(j).toString());
            		if(fieldValue.length()!=length && fieldValue.length()>0){
            			fieldValue = entryIndexService.alignValue(length, Integer.valueOf(fieldValue));
            		}
            	}
				GainField.setFieldValueByName(fieldCodes.get(j),handlerList,fieldValue);
				fieldValue = entryIndexService.getConfigByName(fieldCodes.get(j), fieldValue, enumList, mapFiled);
				if (entryCaptureService.isNumeric(fieldValue)) {
					Integer length = Integer.parseInt(fieldlength.get(j).toString());
					if(fieldValue.length()!=length && fieldValue.length()>0){
						fieldValue = entryIndexService.alignValue(length, Integer.valueOf(fieldValue));
					}
				}
            	//如果是机构名称
            	String organ = templateRepository.findOrganFtypeByNodeid(nodeid);
            	if (fieldCodes.get(j).equals("organ") && organ.equals("string")) {
                	Tb_data_node node = dataNodeRepository.findByNodeid(nodeid);
                	Tb_right_organ right_organ = rightOrganRepository.findByOrganid(node.getRefid());
                	if (right_organ.getCode() != null && !right_organ.getCode().equals("")) {
                		archivecode += right_organ.getCode() + splitValue;
                	} else {
                		archivecode += fieldValue + splitValue;
                	}
            	} else {
            		archivecode += fieldValue + splitValue;
            	}
            } else {
                return null;
            }
        }
        if (fieldCodes.size() >= 1) {
        	String calFieldcode = fieldCodes.get(fieldCodes.size()-1);
        	String calValue = "";
        	String codeSettingFieldValues = archivecode.substring(0, archivecode.length()-1);
        	if (type.equals("数据采集")) {
        		sql = "select max("+ DBCompatible.getInstance().findExpressionOfToNumber(calFieldcode)+") from tb_entry_index_capture where archivecode like " +
        				"'%"+codeSettingFieldValues+"%' and nodeid = '"+nodeid+"'";
        	} else {
        		sql = "select max("+ DBCompatible.getInstance().findExpressionOfToNumber(calFieldcode)+") from tb_entry_index where archivecode like " +
                        "'%"+codeSettingFieldValues+"%' and nodeid = '"+nodeid+"'";
        	}
            Query query = entityManager.createNativeQuery(sql);
            int maxCalValue = query.getSingleResult() == null ? 0 : Integer.valueOf(query.getSingleResult().toString());
            if(maxCalValue==0){
            	calValue = entryIndexService.alignValue(fieldlength.get(fieldlength.size()-1).toString(), 1);
            } else {
                calValue = entryIndexService.alignValue(fieldlength.get(fieldlength.size()-1).toString(), maxCalValue+1);
            }
            GainField.setFieldValueByName(fieldCodes.get(fieldCodes.size()-1),handlerList,calValue);
            archivecode+=calValue;
        }
        archivecodeList.add(archivecode);
        return archivecodeList;
    }

	public Map<String, Object> doModifyContent(String datanodeid, List index_tempList, String fieldModifyData) {
		Map<String, Object> result = new HashMap<>();
		List<String> modifyFieldcodeList = new ArrayList<>();
		String[] dataInfo = fieldModifyData.split("∩");
		for (int i = 0; i < dataInfo.length; i++) {// 遍历需修改字段
			String fieldModify = dataInfo[i];
			String[] fieldModifyArr = fieldModify.split("∪");
			String field = fieldModifyArr[0];
			if (fieldModifyArr.length == 3) {
				GainField.setFieldValues(field, index_tempList, fieldModifyArr[2]);
			}
			if (fieldModifyArr.length == 2) {
				GainField.setFieldValues(field, index_tempList, "");
			}
			modifyFieldcodeList.add(field);
		}
		result.put("fieldcodeList", modifyFieldcodeList);// 需修改字段集合
		result.put("entryindexList", index_tempList);// 修改后的结果集合
		return result;
	}
	
	public String getValue(Integer valueLength, String searchFieldvalue) {
		String regEx = "?.+_(){}[]*^$\\|\"";
		String searchValue = "";
		for (int i = 0; i < valueLength; i++) {
			String field = String.valueOf(searchFieldvalue.charAt(i));
			if (regEx.contains(field)) {// 如果需要修改的字段中包含特殊字符
				searchValue += "\\" + field;
			}
			if (!regEx.contains(field)) {// 如果需要修改的字段中包含不特殊字符(中文/英文等)
				searchValue += field;
			}
		}
		return searchValue;
	}

	public Object doReplaceContent(List entryindexList, String replaceFieldcode, String searchFieldvalue,
			String replacement, boolean ifContainSpace) {
		String[] foundSearchFieldvalues = GainField.getFieldValues(entryindexList, replaceFieldcode);// 获取需修改字段值
		String replaceValue = getValue(replacement.length(), replacement);
		String value = getValue(searchFieldvalue.length(), searchFieldvalue);
		for (int j = 0; j < foundSearchFieldvalues.length; j++) {
			if (foundSearchFieldvalues[j].contains(searchFieldvalue)) {
				// 替换选中的数据
				String fieldValue = foundSearchFieldvalues[j].replaceAll(value, replaceValue);
				if (ifContainSpace) {//如果勾选包含前后空格
					fieldValue = fieldValue.replaceAll(" ", "");
				}
				GainField.setFieldValueByName(replaceFieldcode, entryindexList.get(j), fieldValue);
			}
		}
		return entryindexList;
	}

	public Map<String, Object> doAddContent(List entryindexList, String fieldModifyData) {
		Map<String, Object> result = new HashMap<>();
		List<String> addFieldcodeList = new ArrayList<>();
		String[] fieldModifyArr = fieldModifyData.split("∩")[0].split("∪");
		String addFieldcode = fieldModifyArr[0].split("_")[0];
		String addContentValue = fieldModifyArr[1];
		String[] insertPlaceAndIndex;
		if (fieldModifyArr[2].contains("_")) {
			insertPlaceAndIndex = fieldModifyArr[2].split("_");
		} else {
			insertPlaceAndIndex = new String[] { fieldModifyArr[2] };
		}
		String insertPlace = insertPlaceAndIndex[0];
		int insertIndex = 0;
		if (insertPlaceAndIndex.length == 2) {
			insertIndex = Integer.valueOf(insertPlaceAndIndex[1]);
		}
		String[] foundSearchFieldvalues = GainField.getFieldValues(entryindexList, addFieldcode);// 获取需修改字段值
		if (PLACE_FRONT.equals(insertPlace)) {
			for (int i = 0; i < foundSearchFieldvalues.length; i++) {
				GainField.setFieldValueByName(addFieldcode, entryindexList.get(i),
						addContentValue + foundSearchFieldvalues[i]);
			}
		}
		if (PLACE_BEHIND.equals(insertPlace)) {
			for (int i = 0; i < foundSearchFieldvalues.length; i++) {
				GainField.setFieldValueByName(addFieldcode, entryindexList.get(i),
						foundSearchFieldvalues[i] + addContentValue);
			}
		}
		if (PLACE_ANYWHERE.equals(insertPlace)) {
			for (int i = 0; i < foundSearchFieldvalues.length; i++) {
				String finalContent;
				if (insertIndex > foundSearchFieldvalues[i].length()) {
					finalContent = foundSearchFieldvalues[i] + addContentValue;
				} else {
					finalContent = foundSearchFieldvalues[i].substring(0, insertIndex - 1) + addContentValue
							+ foundSearchFieldvalues[i].substring(insertIndex - 1);
				}
				GainField.setFieldValueByName(addFieldcode, entryindexList.get(i), finalContent);
			}
		}
		addFieldcodeList.add(addFieldcode);
		result.put("fieldcodeList", addFieldcodeList);// 需修改字段集合
		result.put("entryindexList", entryindexList);// 修改后的结果集合
		return result;
	}

	private static String getMACAddress() {
		InetAddress ia = null;
		// 获得网络接口对象（即网卡），并得到mac地址，mac地址存在于一个byte数组中。
		byte[] mac = new byte[0];
		try {
			ia = InetAddress.getLocalHost();
			mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
		} catch (SocketException e) {
		} catch (UnknownHostException e) {
		}
		// 下面代码是把mac地址拼装成String
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mac.length; i++) {
			if (i != 0) {
				sb.append("-");
			}
			// mac[i] & 0xFF 是为了把byte转化为正整数
			String s = Integer.toHexString(mac[i] & 0xFF);
			sb.append(s.length() == 1 ? 0 + s : s);
		}
		// 把字符串所有小写字母改为大写成为正规的mac地址并返回
		return sb.toString().toUpperCase();
	}

	//获取本机MAC地址集合
	public static List<String> getMacList(){
		ArrayList<String> tmpMacList=new ArrayList<>();
		try{
			java.util.Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
			StringBuilder sb = new StringBuilder();
			while(en.hasMoreElements()){
				NetworkInterface iface = en.nextElement();
				List<InterfaceAddress> addrs = iface.getInterfaceAddresses();
				for(InterfaceAddress addr : addrs) {
					InetAddress ip = addr.getAddress();
					NetworkInterface network = NetworkInterface.getByInetAddress(ip);
					if(network==null){continue;}
					byte[] mac = network.getHardwareAddress();
					if(mac==null){continue;}
					sb.delete( 0, sb.length() );//归零
					//for (int i = 0; i < mac.length; i++) {sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));}
					// 下面代码是把mac地址拼装成String
					for (int i = 0; i < mac.length; i++) {
						if (i != 0) {
							sb.append("-");
						}
						// mac[i] & 0xFF 是为了把byte转化为正整数
						String s = Integer.toHexString(mac[i] & 0xFF);
						sb.append(s.length() == 1 ? 0 + s : s);
					}
					tmpMacList.add(sb.toString().toUpperCase());//把字符串所有小写字母改为大写成为正规的mac地址
				}        }
		}catch(Exception e){
			e.printStackTrace();
		}
		if(tmpMacList.size()<=0){return tmpMacList;}
		/***去重，同一个网卡的ipv4,ipv6得到的mac都是一样的，肯定有重复，下面这段代码是。。流式处理***/
		List<String> unique = tmpMacList.stream().distinct().collect(Collectors.toList());
		return unique;
	}

	public void deleteEntryIndexTempByUniquetag() {
		String uniquetag = getUniquetag();
		entryIndexTempRepository.deleteByUniquetag(uniquetag);
	}

	public void deleteEntryIndexTempByUniquetagByType(String type) {
		String uniquetag = getUniquetagByType(type);
		entryIndexTempRepository.deleteByUniquetag(uniquetag);
	}

	public void deleteSqEntryIndexTempByUniquetag() {
		String uniquetag = getUniquetag();
		entryIndexSqTempRepository.deleteByUniquetag(uniquetag);
	}

	public static String getUniquetag() {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		//String mac = getMACAddress();
		List<String> macs=getMacList();
		String mac="";
		if(macs.size()>0){//获取第一个mac地址
			mac=macs.get(0);
		}
		return userDetails.getLoginname() + "-" + mac;
	}

	public static String getUniquetagByType(String type) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		//String mac = getMACAddress();
		//List<String> macs=getMacList();
		String mac="";
		/*if(macs.size()>0){//获取第一个mac地址
			mac=macs.get(0);
		}*/
		return userDetails.getLoginname() + "_" +type+"-"+ mac;
	}

	public static Specification<Tb_entry_index_temp> uniquetagCondition(String uniquetag) {
		Specification<Tb_entry_index_temp> searchUniquetag = new Specification<Tb_entry_index_temp>() {
			@Override
			public Predicate toPredicate(Root<Tb_entry_index_temp> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.equal(root.get("uniquetag"), uniquetag);
				return cb.or(p);
			}
		};
		return searchUniquetag;
	}

	/**
	 * 归档时批量修改
	 *
	 * @param entryidArr
	 *            需修改条目的id
	 * @return
	 */
	public void updateFileModify(String[] entryidArr, String datanodeid, String fieldModifyData, String type) {
		String uniquetag;
		if(type != null && !type.equals("") && type.equals("数据采集")){
			uniquetag = getUniquetagByType("cjgd");
		}else{
			uniquetag = getUniquetagByType("glgd");
		}
		List<String[]> subAry = new InformService().subArray(entryidArr, 1000);//处理ORACLE1000参数问题
		List<Tb_entry_index_temp> index_tempList = new ArrayList<>();
		for(int i=0;i<subAry.size();i++) {
			List<Tb_entry_index_temp> index_tempListNew = entryIndexTempRepository.findByEntryidInAndUniquetag(subAry.get(i),uniquetag);
			index_tempList.addAll(index_tempListNew);
		}
		Map<String, Object> result = doModifyContent(datanodeid, index_tempList, fieldModifyData);
		List<String> modifyFieldcodeList = (List<String>) result.get("fieldcodeList");
		index_tempList = (List<Tb_entry_index_temp>) result.get("entryindexList");
		setTempArchivecodes(modifyFieldcodeList, datanodeid, index_tempList,uniquetag);// 档号自动重新生成
		entryIndexTempRepository.save(index_tempList);
	}

	/**
	 * 归档时批量替换
	 *
	 * @param entryidArr
	 *            需修改条目的id
	 * @return
	 */
	public void updateFileReplace(String[] entryidArr,String datanodeid, String fieldReplaceData, boolean ifContainSpace, String type) {
		String uniquetag;
		if(type != null && !type.equals("") && type.equals("数据采集")){
			uniquetag = getUniquetagByType("cjgd");
		}else{
			uniquetag = getUniquetagByType("glgd");
		}

		/* 参数处理---------------------------------------------start */
		String[] fieldModifyArr = fieldReplaceData.split("∪");
		String replaceFieldcode = fieldModifyArr[0].split("_")[0];
		String searchFieldvalue = fieldModifyArr[1];
		String replacement;
		if (fieldModifyArr.length == 2) {
			replacement = "";
		} else {
			replacement = fieldModifyArr[2];
		}
		/* 参数处理---------------------------------------------end */
		List<String[]> subAry = new InformService().subArray(entryidArr, 1000);//处理ORACLE1000参数问题
		List<Tb_entry_index_temp> entry_index_tempList = new ArrayList<>();
		for (int i = 0; i < subAry.size(); i++) {
			String[] entrys = subAry.get(i);
			String entryidStr = "'" + String.join("','", entrys) + "'";
			String replaceStr = "";
			if ("".equals(DBCompatible.getInstance().findExpressionOf())) {// oracle
				replaceStr = replaceStr(replaceFieldcode, searchFieldvalue, type);
			} else {
				replaceStr = replaceStr(replaceFieldcode, getValue(searchFieldvalue.length(), searchFieldvalue), type);
			}
			String serachCondition = " entryid in(" + entryidStr + ") and " + replaceStr;
			String sql = "select * from tb_entry_index_temp where " + serachCondition;
			Query query = entityManager.createNativeQuery(sql, Tb_entry_index_temp.class);
			List<Tb_entry_index_temp> index_tempListNew = query.getResultList();
			entry_index_tempList.addAll(index_tempListNew);
		}
		Object replace = doReplaceContent(entry_index_tempList, replaceFieldcode, searchFieldvalue, replacement,
				ifContainSpace);
		if (replace != null) {
			entry_index_tempList = (List<Tb_entry_index_temp>) replace;// 需修改字段处理
			List<String> replaceFieldcodeList = new ArrayList<>();
			replaceFieldcodeList.add(replaceFieldcode);
			setTempArchivecodes(replaceFieldcodeList, datanodeid, entry_index_tempList,uniquetag);// 档号自动重新生成
			entryIndexTempRepository.save(entry_index_tempList);
		}
	}

	/**
	 * 归档时批量增加
	 *
	 * @param entryidArr
	 *            需修改条目的id
	 * @return
	 */
	public void updateFileAdd(String[] entryidArr,String datanodeid, String fieldModifyData, String type) {
		String uniquetag;
		if(type != null && !type.equals("") && type.equals("数据采集")){
			uniquetag = getUniquetagByType("cjgd");
		}else{
			uniquetag = getUniquetagByType("glgd");
		}
		List<String[]> subAry = new InformService().subArray(entryidArr, 1000);//处理ORACLE1000参数问题
		List<Tb_entry_index_temp> entry_index_tempList = new ArrayList<>();
		for(int i=0;i<subAry.size();i++) {
			List<Tb_entry_index_temp> index_tempListNew = entryIndexTempRepository.findByEntryidInAndUniquetag(subAry.get(i),uniquetag);
			entry_index_tempList.addAll(index_tempListNew);
		}
		Map<String, Object> result = doAddContent(entry_index_tempList, fieldModifyData);
		List<String> addFieldcodeList = (List<String>) result.get("fieldcodeList");
		entry_index_tempList = (List<Tb_entry_index_temp>) result.get("entryindexList");// 需修改字段处理
		setTempArchivecodes(addFieldcodeList, datanodeid, entry_index_tempList,uniquetag);// 档号自动重新生成
		entryIndexTempRepository.save(entry_index_tempList);
	}
}