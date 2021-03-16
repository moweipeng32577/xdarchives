package com.wisdom.web.service;

import com.wisdom.service.websocket.WebSocketService;
import com.wisdom.util.DBCompatible;
import com.wisdom.util.GainField;
import com.wisdom.util.SolidifyThread;
import com.wisdom.util.ZipUtil;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import org.apache.commons.io.FileUtils;
import org.hibernate.Session;
import org.hibernate.jpa.HibernateEntityManager;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.wisdom.web.service.ClassifySearchService.checkFilecode;
import static com.wisdom.web.service.ThematicService.delFolder;

/**
 * 档案条目采集业务类 Created by RonJiang on 2017/11/25 0025.
 */
@Service
@Transactional
public class EntryIndexCaptureService {

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	EntryIndexCaptureRepository entryIndexCaptureRepository;

	@Autowired
	CodesettingService codesettingService;

	@Autowired
	CodesetRepository codesetRepository;

	@Autowired
	DataNodeRepository dataNodeRepository;

	@Autowired
	RightOrganRepository rightOrganRepository;
	
	@Autowired
	FundsService fundsService;

	@Autowired
	EntryIndexService entryIndexService;

	@Autowired
	EntryIndexCaptureService entryIndexCaptureService;

	@Autowired
	NodesettingService nodesettingService;

	@Autowired
	PublicUtilService publicUtilService;

	@Autowired
	EntryIndexSqTempRepository entryIndexSqTempRepository;

	@Autowired
	EntryIndexTempRepository entryIndexTempRepository;

	@Autowired
	EntryDetailCaptureRepository entryDetailCaptureRepository;

	@Autowired
	ElectronicCaptureRepository electronicCaptureRepository;

	@Autowired
	TemplateRepository templateRepository;

	@Autowired
	TransdocEntryRepository transdocEntryRepository;
	
	@Autowired
	EntryCaptureService entryCaptureService;

	@Autowired
	ElectronicVersionCaptureRepository electronicVersionCaptureRepository;

	@Autowired
	OrdersetRepository ordersetRepository;

	@Autowired
	BatchModifyService batchModifyService;

	@Autowired
	WebSocketService webSocketService;

	@Autowired
	FunctionRepository functionRepository;

	@Autowired
	ClassifySearchService classifySearchService;

	@Autowired
	SimpleSearchService simpleSearchService;

	@Autowired
	MetadataTemplateRepository metadataTemplateRepository;

	@Autowired
	UserNodeSortRepository userNodeSortRepository;

	@Value("${system.document.rootpath}")
	private String rootpath;//系统文件根目录

	public Integer getCalValue(Object entryIndexCapture, String nodeid,List<Tb_codeset> codeSettingList, String type) {
		Integer calValue = null;
		if (codeSettingList.size() == 1) {// 档号设置只有一个计算项字段，无其它字段
			String sql = "select max("
					+ DBCompatible.getInstance().findExpressionOfToNumber(codeSettingList.get(0).getFieldcode())
					+ ") from tb_entry_index_capture where nodeid='" + nodeid + "'";
			Query query = entityManager.createNativeQuery(sql);
			int maxCalValue = query.getSingleResult() == null ? 0 : Integer.valueOf(query.getSingleResult().toString());
			if (maxCalValue == 0) {
				return 1;
			}
			calValue = maxCalValue + 1;

			return calValue;
		}
		String codeSettingFieldValues = "";
		Map<String, Map<String, String>> mapFiled = entryIndexService.getConfigMap();//获取参数设置的MAP
		List<Tb_data_template> enumList = templateRepository.getByNodeidFtype("enum", nodeid);//获取某节点的模板中属于enum的字段
		//List<String> spList = codesetRepository.findSplitcodeByDatanodeid(nodeid);
		List<String> spList = new ArrayList<>();//档号分隔符集合
		List<String> fieldlengthList=new ArrayList<>();//字段长度集合
		for(Tb_codeset codeset:codeSettingList){
			spList.add(codeset.getSplitcode());
			fieldlengthList.add(String.valueOf(codeset.getFieldlength()));
		}
		for (int i = 0; i < codeSettingList.size() - 1; i++) {
			String value = "";
			// 通过反射获得档号字段的页面输入值，不含最后一个（计算项）
			String codeSettingFieldValue = GainField.getFieldValueByName(codeSettingList.get(i).getFieldcode(), entryIndexCapture)
					+ "";
			codeSettingFieldValue = entryIndexService.getConfigByName(codeSettingList.get(i).getFieldcode(), codeSettingFieldValue, enumList, mapFiled);
			if (isNumeric(codeSettingFieldValue)) {
				Integer length = Integer.parseInt(fieldlengthList.get(i));
				value = entryIndexService.alignValue(length,Integer.valueOf(codeSettingFieldValue));
			} else {
				if (codeSettingList.get(i).getFieldcode().equals("organ")) {
					Tb_right_organ right_organ = rightOrganRepository.getWithNodeid(nodeid);
					if (right_organ.getCode() != null && !right_organ.getCode().equals("")) {
						value = right_organ.getCode();
					} else {
						value = codeSettingFieldValue;
					}
				} else {
					value = codeSettingFieldValue;
				}
			}
			if (!"null".equals(codeSettingFieldValue) && !"".equals(codeSettingFieldValue)) {
				if (i < codeSettingList.size() - 2) {
					codeSettingFieldValues += value + spList.get(i);
				}
				if (i == codeSettingList.size() - 2) {
					codeSettingFieldValues += value;
				}
			} else {// 页面中档号设置字段无输入值
				return null;
			}
		}
		GainField.setFieldValueByName("archivecode", entryIndexCapture, codeSettingFieldValues);//设置临时档号
		String calValueFieldCode = codeSettingList.get(codeSettingList.size() - 1).getFieldcode();
		String sql = "select max(" + DBCompatible.getInstance().findExpressionOfToNumber(calValueFieldCode)
				+ ") from tb_entry_index_capture where archivecode like '" + codeSettingFieldValues + "%' and nodeid='"
				+ nodeid + "'";
		Query query = entityManager.createNativeQuery(sql);
		int maxCalValue = query.getSingleResult() == null ? 0 : Integer.valueOf(query.getSingleResult().toString());
		if ("预归档".equals(type)) {//预归档需要去看临时表中的档号的计算项最大值
			String uniqueTag=BatchModifyService.getUniquetagByType("cjgd");
			String tempSql = "select max(" + DBCompatible.getInstance().findExpressionOfToNumber(calValueFieldCode)
					+ ") from tb_entry_index_temp where archivecode like '" + codeSettingFieldValues + "%' and uniquetag='"+uniqueTag+"' ";
			Query capturequery = entityManager.createNativeQuery(tempSql);
			int tmaxCalValue = capturequery.getSingleResult() == null ? 0 : Integer.valueOf(capturequery.getSingleResult().toString());
			if (tmaxCalValue > maxCalValue) {//临时表的计算项大于数据管理，返回临时表的计算项+1
				// 通过反射获得档号字段的页面输入计算项的值
				String oldCalValue = GainField.getFieldValueByName(calValueFieldCode, entryIndexCapture)+"";
				if((tmaxCalValue+"").equals(oldCalValue)){//临时表的计算项最大值等于当前条目，则计算项保持不变
					return tmaxCalValue;
				}
				return tmaxCalValue+1;
			}
		}else if (maxCalValue == 0) {
			return 1;
		}
		calValue = maxCalValue + 1;
		return calValue;
	}

	public void updateSubsequentData(Tb_entry_index_capture entryIndexCapture, List<String> codeSettingFieldList,
			String flag, String pages) {
		// 子件插件获取参数
		String innerfile = entryIndexCapture.getInnerfile();
		String archivecode = entryIndexCapture.getArchivecode();

		/* １、参数处理 */
		String nodeid = entryIndexCapture.getNodeid();
		String entryid = entryIndexCapture.getEntryid();
		String filecode = entryIndexCapture.getFilecode();// 案卷号
		String recordcode = entryIndexCapture.getRecordcode();// 件号
		Integer number = codesettingService.getCalFieldLength(nodeid);// 获取计算项单位长度
		String calFieldcode = codeSettingFieldList.get(codeSettingFieldList.size() - 1);
		String calValue = (String) (GainField.getFieldValueByName(calFieldcode, entryIndexCapture) != null
				? GainField.getFieldValueByName(calFieldcode, entryIndexCapture) : "0");
		List<String> codeSettingFieldValues = new ArrayList<>();
		for (int i = 0; i < codeSettingFieldList.size() - 1; i++) {
			// 通过反射获得档号字段的页面输入值，不含最后一个（计算项）
			String codeSettingFieldValue = GainField.getFieldValueByName(codeSettingFieldList.get(i), entryIndexCapture)
					+ "";
			if (!"null".equals(codeSettingFieldValue) && !"".equals(codeSettingFieldValue)) {
				codeSettingFieldValues.add(codeSettingFieldValue);
			} else {
				codeSettingFieldValues.add("");
			}
		}
		List<String> codeSettingSplitCodes = codesettingService.getCodeSettingSplitCodes(nodeid);

		// 先判断有无重复，无重复的话不做排序处理
		List<Tb_entry_index_capture> copyList = entryIndexCaptureRepository.findCopyByArchivecode(archivecode, nodeid);
		if (copyList.size() > 1) {
			if (flag.equals("jnchj")) {// 卷内插件

				/* ２、数据查询及处理 */
				List<Tb_entry_index_capture> resultList = new ArrayList<>();
				if (codeSettingFieldList.size() == 1) {
					Specifications sp = null;
					// 由于数据库计算项字段类型为varchar,此处计算项大小比较采取的是字符串比较,并不十分合理
					Specification<Tb_entry_index_capture> searchNodeidCondition = getSearchNodeidCondition(
							new String[] { nodeid });
					Specification<Tb_entry_index_capture> searchCalvalueCondition = getSearchCalvalueCondition(
							calFieldcode, calValue);
					// 后续数据包括了选定数据本身，因为拆插操作共用这个方法，
					// 插件时需要对该选定数据计算项及档号进行处理，
					// 而拆件时并不需要对选定数据本身进行处理，故后续拆件方法dismantle中会另行对其进行还原，下同
					Specification<Tb_entry_index_capture> searchEntryid = getSearchEntryidCondition(entryid);
					sp = Specifications.where(searchNodeidCondition).and(searchCalvalueCondition).or(searchEntryid);
					resultList = entryIndexCaptureRepository.findAll(sp);
				} else {
					String searchCondition = EntryIndexService.getJointSearchCondition(codeSettingFieldList,
							codeSettingFieldValues, nodeid);
					searchCondition += " and " + calFieldcode + ">'" + calValue + "' or (entryid='" + entryid + "')";
					String sql = "select * from tb_entry_index_capture where " + searchCondition;
					Query query = entityManager.createNativeQuery(sql, Tb_entry_index_capture.class);
					resultList = query.getResultList();
				}

				List<Tb_entry_index_capture> saveList = getModifiedList(entryIndexCapture, resultList, calFieldcode,
						number, codeSettingFieldList, codeSettingSplitCodes, "insertion", pages);
				entryIndexCaptureRepository.save(saveList);
				// 子件插件处理母卷的总页数和总文件数
				int jnadd = 1;
				String zjPages = pages;

				updateAjuFile(archivecode, innerfile, jnadd, zjPages, nodeid, entryid);

			} else if (flag.equals("chju") || flag.equals("syncJn")) {// 案卷排序

				// 先截取档号前端
				String parentArchivecode = archivecode.substring(0, archivecode.lastIndexOf(filecode));
				// 根据案卷号获取后边的相应案卷号的案卷(排除卷内文件)
				List<Tb_entry_index_capture> ajNextList = entryIndexCaptureRepository
						.findAllByArchivecodeLikeAndNext(parentArchivecode, filecode, nodeid);

				// 处理之后的案卷编号filecode各+1
				if (ajNextList.size() > 0) {
					for (int i = ajNextList.size() - 1; i >= 0; i--) {// 反序，从最大那个开始
						// 插卷后，后边的案卷号全部加1
						String filecode0 = ajNextList.get(i).getFilecode();
						String ajArch = ajNextList.get(i).getArchivecode();
						String filecode1 = "";
						int num = filecode0.length();
						try {
							filecode1 = Integer.parseInt(filecode0) + 1 + "";
						} catch (Exception e) {
							e.printStackTrace();
						}
						int newNum = filecode1.length();
						// 重新拼接filecode
						if (filecode1.length() < num) {
							for (int j = 0; j < num - newNum; j++) {
								filecode1 = 0 + filecode1;
							}
						}

						int pNum = parentArchivecode.length();
						if (flag.equals("syncJn")) {// 同步卷内
							// 更新案卷和相关卷内的filecode
							String jnNodeid = publicUtilService.getJnNodeid(nodeid);
							// 现获取该filecode的案卷和卷内,然后按新的案卷号filecode和新的档号archivecode一一更新
							List<Tb_entry_index_capture> ajOneList = entryIndexCaptureRepository
									.findAllByArchivecodeAndFilecode(parentArchivecode, filecode0, nodeid, jnNodeid);
							for (int k = 0; k < ajOneList.size(); k++) {
								// 重新拼接archivecode
								String archivecodeOne = ajOneList.get(k).getArchivecode();
								String subArch = archivecodeOne.substring(pNum, archivecodeOne.length());// 获取案卷号和卷内号
								String newSunArch = subArch.replaceFirst(filecode0, filecode1);// 新案卷号
								String newArchivecode = parentArchivecode + newSunArch;
								entryIndexCaptureRepository.updateFilecodeAndArchivecode(filecode1, newArchivecode,
										archivecodeOne, nodeid, jnNodeid);
							}
						} else {// 只处理案卷
								// 重新拼接archivecode
							String subArch = ajArch.substring(pNum, ajArch.length());// 获取案卷号和卷内号
							String newSunArch = subArch.replaceFirst(filecode0, filecode1);// 新案卷号
							String newArchivecode = parentArchivecode + newSunArch;
							entryIndexCaptureRepository.updateFilecodeAndArchivecode(filecode1, newArchivecode, ajArch,
									nodeid);
						}
					}
				}
				// 最后处理重复的插入数据,先获取filecode，filecode+1，再更新archivecode
				String filecodeY = copyList.get(0).getFilecode();
				String filecodeY1 = "";
				int num = filecodeY.length();
				try {
					filecodeY1 = Integer.parseInt(filecodeY) + 1 + "";
				} catch (Exception e) {
					e.printStackTrace();
				}
				int newNum = filecodeY1.length();
				// 重新拼接filecode
				if (filecodeY1.length() < num) {
					for (int j = 0; j < num - newNum; j++) {
						filecodeY1 = 0 + filecodeY1;
					}
				}

				int pNum = parentArchivecode.length();
				if (flag.equals("syncJn")) {// 同步卷内
					// 更新案卷和相关卷内的filecode
					// 现获取该filecode的卷内
					String jnNodeid = publicUtilService.getJnNodeid(nodeid);
					List<Tb_entry_index_capture> ajCopyList = entryIndexCaptureRepository
							.findAllByCopyArchivecode(parentArchivecode, filecodeY, jnNodeid);
					// 增加案卷
					ajCopyList.add(copyList.get(0));
					// 然后按新的案卷号filecode和新的档号archivecode一一更新
					for (int k = 0; k < ajCopyList.size(); k++) {
						// 重新拼接archivecode
						String archivecodeOne = ajCopyList.get(k).getArchivecode();
						String subArch = archivecodeOne.substring(pNum, archivecodeOne.length());// 获取案卷号和卷内号
						String newSunArch = subArch.replaceFirst(filecodeY, filecodeY1);// 新案卷号
						String newArchivecode = parentArchivecode + newSunArch;

						if (archivecodeOne.equals(archivecode)) {// 原案卷的更新档号
							entryIndexCaptureRepository.updateCopyArchivecode(filecodeY1, newArchivecode,
									archivecodeOne, entryid);
						} else {// 子件更新档号
							entryIndexCaptureRepository.updateFilecodeAndArchivecode(filecodeY1, newArchivecode,
									archivecodeOne, jnNodeid);
						}
					}
				} else {// 只更新案卷
					String subArch = archivecode.substring(pNum, archivecode.length());// 获取案卷号和卷内号
					String newSunArch = subArch.replaceFirst(filecodeY, filecodeY1);// 新案卷号
					String newArchivecode = parentArchivecode + newSunArch;
					entryIndexCaptureRepository.updateCopyArchivecode(filecodeY1, newArchivecode, archivecode, entryid);
				}
			} else if (flag.equals("chji")) {// 插件排序
				String recordcodeY = "";
				String recordcodeY1 = "";
				String parentArchivecode = "";

				// 先判断档号的统计项
				String code = codeSettingFieldList.get(codeSettingFieldList.size() - 1);
				if (code.equals("recordcode")) {
					// 先处理插件之后的档号排序
					updateAjiSquChji(archivecode, recordcode, nodeid);
					parentArchivecode = archivecode.substring(0, archivecode.lastIndexOf(recordcode));
					recordcodeY = copyList.get(0).getRecordcode();
				} else if (code.equals("innerfile")) {
					updateJnAjiSquChji(archivecode, innerfile, nodeid);
					parentArchivecode = archivecode.substring(0, archivecode.lastIndexOf(innerfile));
					recordcodeY = copyList.get(0).getInnerfile();
					// 更新案卷文件总数和总页数
					int jnadd = 1;
					String zjPages = pages;

					updateAjuFile(archivecode, innerfile, jnadd, zjPages, nodeid, entryid);
				}

				// 再处理重复的插入数据,先获取filecode，filecode+1，再更新archivecode
				int num = recordcodeY.length();
				try {
					recordcodeY1 = Integer.parseInt(recordcodeY) + 1 + "";
				} catch (Exception e) {
					e.printStackTrace();
				}
				int newNum = recordcodeY1.length();
				// 重新拼接filecode
				if (recordcodeY1.length() < num) {
					for (int j = 0; j < num - newNum; j++) {
						recordcodeY1 = 0 + recordcodeY1;
					}
				}
				int pNum = parentArchivecode.length();
				String subArch = archivecode.substring(pNum, archivecode.length());// 获取案卷号和卷内号
				String newSunArch = subArch.replaceFirst(recordcodeY, recordcodeY1);// 新案卷号
				String newArchivecode = parentArchivecode + newSunArch;
				if (code.equals("recordcode")) {
					entryIndexCaptureRepository.updateCopyArchivecodeAndRecordcode(recordcodeY1, newArchivecode,
							archivecode, entryid);
				} else if (code.equals("innerfile")) {
					entryIndexCaptureRepository.updateCopyArchivecodeAndInnerfile(recordcodeY1, newArchivecode,
							archivecode, entryid);
				}

			}
		}
	}

	private Integer getCalVal(List<String> codeSettingFieldList, String nodeid) {
		String sql = "select max(" + DBCompatible.getInstance().findExpressionOfToNumber(codeSettingFieldList.get(0))
				+ ") from tb_entry_index_capture where nodeid='" + nodeid + "'";
		Query query = entityManager.createNativeQuery(sql);
		int maxCalValue = query.getSingleResult() == null ? 0 : Integer.valueOf(query.getSingleResult().toString());
		if (maxCalValue == 0) {
			return 1;
		}
		Integer calValue = maxCalValue + 1;

		return calValue;
	}

	private Integer getCalVals(List<String> codeSettingFieldList, Tb_entry_index_capture entry_index_capture,
			String nodeid) {
		String codeSettingFieldValues = "";
		List<String> spList = codesetRepository.findSplitcodeByDatanodeid(nodeid);
		for (int i = 0; i < codeSettingFieldList.size() - 1; i++) {
			// 通过反射获得档号字段的页面输入值，不含最后一个（计算项）
			String codeSettingFieldValue = GainField.getFieldValueByName(codeSettingFieldList.get(i),
					entry_index_capture) + "";
			if (!"null".equals(codeSettingFieldValue) && !"".equals(codeSettingFieldValue)) {
				if (i < codeSettingFieldList.size() - 2) {
					codeSettingFieldValues += codeSettingFieldValue + spList.get(i);
				}
				if (i == codeSettingFieldList.size() - 2) {
					codeSettingFieldValues += codeSettingFieldValue;
				}
			} else {// 页面中档号设置字段无输入值
				return null;
			}
		}
		String calValueFieldCode = codeSettingFieldList.get(codeSettingFieldList.size() - 1);
		String sql = "select max(" + DBCompatible.getInstance().findExpressionOfToNumber(calValueFieldCode)
				+ ") from tb_entry_index_capture where archivecode like '" + codeSettingFieldValues + "%' and nodeid='"
				+ nodeid + "'";
		Query query = entityManager.createNativeQuery(sql);
		int maxCalValue = query.getSingleResult() == null ? 0 : Integer.valueOf(query.getSingleResult().toString());
		if (maxCalValue == 0) {
			return 1;
		}
		return maxCalValue + 1;
	}

	public String dismantle(String entryid, String targetNodeid, String title, String syncType) {
		Tb_entry_index_capture entryIndexCapture = entryIndexCaptureRepository.findByEntryid(entryid);
		String archivecode = entryIndexCapture.getArchivecode();
		String innerfile = entryIndexCapture.getInnerfile();// 卷内文件件号
		String filecode = entryIndexCapture.getFilecode();// 案卷号 案卷用
		String recordcode = entryIndexCapture.getRecordcode();// 件号 案件用
		String zjPages = entryIndexCapture.getPages();
		String nodeid = entryIndexCapture.getNodeid();

		if (archivecode == null) {
			return "档号不能为空";
		}

		List<Tb_codeset> codeFieldList = codesetRepository.findByDatanodeidOrderByOrdernum(nodeid); // 获取档号设置字段集合
		if (codeFieldList.size() != 0) {
			Tb_codeset codeset = codeFieldList.get(codeFieldList.size() - 1); // 获取最后一个档号组成字段
			Object lastFieldValue = GainField.getFieldValueByName(codeset.getFieldcode(), entryIndexCapture);// 获取字段值
			if (lastFieldValue == null) {
				return codeset.getFieldname() + "不能为空";
			}
		} else {
			return "档号组成有误";
		}

		Tb_entry_index_capture entry_index_capture = new Tb_entry_index_capture();
		BeanUtils.copyProperties(entryIndexCapture, entry_index_capture);
		List<String> codeSettingFieldList = codesettingService.getCodeSettingFields(targetNodeid);// 获取档号设置字段集合
		Integer number = codesettingService.getCalFieldLength(targetNodeid);// 获取计算项单位长度
		String restoreCalVal = "";
		Integer calValue = 0;
		if (codeSettingFieldList.size() == 0) {// 没有档号设置直接放置过去
			entry_index_capture.setArchivecode(null);
			entry_index_capture.setNodeid(targetNodeid);
			entryIndexCaptureRepository.save(entry_index_capture);
		} else {
			if (codeSettingFieldList.size() == 1) {// 档号设置只有一个计算项字段，无其它字段
				calValue = getCalVal(codeSettingFieldList, targetNodeid);
			} else {
				calValue = getCalVals(codeSettingFieldList, entry_index_capture, targetNodeid);
			}
			if (calValue != null) {
				restoreCalVal = entryIndexService.alignValue(number,calValue);
			}
			String calFieldcode = codeSettingFieldList.get(codeSettingFieldList.size() - 1);
			GainField.setFieldValueByName(calFieldcode, entry_index_capture, restoreCalVal);// 还原统计项的值，确保拆到其它节点后其数值保持不变
			entry_index_capture.setArchivecode(null);
			entry_index_capture.setNodeid(targetNodeid);
			entryIndexCaptureRepository.save(entry_index_capture);
		}

		// 拆卷并且要求同步卷内文件，把卷内文件也拆到那个节点
		String jnNodeid = "";
		if (syncType.equals("syncInnerFile") && title.equals("1")) {
			jnNodeid = publicUtilService.getJnNodeid(nodeid);
			// 根据档号去查询所有卷内文件
			List<Tb_entry_index_capture> ajList = entryIndexCaptureRepository
					.findAllByArchivecodeLikeAndNodeidOrderByArchivecode(archivecode, jnNodeid);
			if (ajList.size() > 0) {// 案卷本身已更新
				// 把所有相关档号的子件都更新节点和档号
				entryIndexCaptureRepository.updateNodeidAndArchivecode(targetNodeid, archivecode, jnNodeid);
			}
		}

		// 拆卷后，更新案卷件号顺序，更新相应案卷相应卷内文件的档号（即选中记录的后边的记录的档号中的案卷件号减一）
		if (title.equals("1")) {
			// 先截取档号前端
			String parentArchivecode = archivecode.substring(0, archivecode.lastIndexOf(filecode));
			// 根据案卷号获取后边的相应案卷号的案卷(排除卷内文件)
			List<Tb_entry_index_capture> ajNextList = entryIndexCaptureRepository
					.findAllByArchivecodeLikeAndNext(parentArchivecode, filecode, nodeid);
			if (ajNextList.size() > 0) {
				for (int i = 0; i < ajNextList.size(); i++) {
					// 拆件后，后边的案卷号全部减1
					String filecode0 = ajNextList.get(i).getFilecode();
					String archivecode0 = ajNextList.get(i).getArchivecode();
					String filecode1 = "";
					int num = filecode0.length();
					try {
						filecode1 = Integer.parseInt(filecode0) - 1 + "";
					} catch (Exception e) {
						e.printStackTrace();
					}
					int newNum = filecode1.length();
					// 重新拼接filecode
					if (filecode1.length() < num) {
						for (int j = 0; j < num - newNum; j++) {
							filecode1 = 0 + filecode1;
						}
					}
					int pNum = parentArchivecode.length();
					if (syncType.equals("syncInnerFile")) {// 同步更新卷内
						// 更新案卷和相关卷内的filecode
						// 现获取该filecode的案卷和卷内,然后按新的案卷号filecode和新的档号archivecode一一更新
						List<Tb_entry_index_capture> ajOneList = entryIndexCaptureRepository
								.findAllByArchivecodeAndFilecode(parentArchivecode, filecode0, nodeid, jnNodeid);
						for (int k = 0; k < ajOneList.size(); k++) {
							// 重新拼接archivecode
							String archivecodeOne = ajOneList.get(k).getArchivecode();
							String subArch = archivecodeOne.substring(pNum, archivecodeOne.length());// 获取案卷号和卷内号
							String newSunArch = subArch.replaceFirst(filecode0, filecode1);// 新案卷号
							String newArchivecode = parentArchivecode + newSunArch;
							entryIndexCaptureRepository.updateFilecodeAndArchivecode(filecode1, newArchivecode,
									archivecodeOne, nodeid, jnNodeid);
						}

					} else {// 只更新案卷顺序
						String subArch = archivecode0.substring(pNum, archivecode0.length());// 获取案卷号和卷内号
						String newSunArch = subArch.replaceFirst(filecode0, filecode1);// 新案卷号
						String newArchivecode = parentArchivecode + newSunArch;
						entryIndexCaptureRepository.updateFilecodeAndArchivecode(filecode1, newArchivecode,
								archivecode0, nodeid);
					}
				}
			}
		}
		// 如果是卷内拆件，还要更新该卷条目的文件数和总页数，title: 1拆卷 2卷内拆件 3拆件  最后加个排序
		if (title.equals("2") || innerfile != null) {
			// 更新案卷文件总数和总页数
			int jnadd = 0;
			updateAjuFile(archivecode, innerfile, jnadd, zjPages, nodeid, entryid);
			// 2卷内文件排序
			updateJnSqu(archivecode, innerfile, nodeid);
		}
		// 拆件后，更新案卷件号顺序，（即选中记录的后边的记录的档号中的案件号减一）
		if (title.equals("3") && innerfile == null) {// 3 拆件
			updateAjiSqu(archivecode, recordcode, nodeid);// 案件排序
		}
		return "拆件成功";
	}

	public static Specification<Tb_entry_index_capture> getSearchEntryidCondition(String[] entryidArr) {
		Specification<Tb_entry_index_capture> searchEntryID = new Specification<Tb_entry_index_capture>() {
			@Override
			public Predicate toPredicate(Root<Tb_entry_index_capture> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				Predicate[] predicates = new Predicate[entryidArr.length];
				for (int i = 0; i < entryidArr.length; i++) {
					predicates[i] = criteriaBuilder.equal(root.get("entryid"), entryidArr[i]);
				}
				return criteriaBuilder.or(predicates);
			}
		};
		return searchEntryID;
	}

	public List<Tb_entry_index_capture> getEntryList(String nodeid, String condition, String operator, String content,
			Tb_entry_index_capture formConditions, ExtOperators formOperators, ExtDateRangeData daterangedata,
			String logic, boolean ifSearchLeafNode, boolean ifContainSelfNode) {
		String[] nodeids;
		if (ifSearchLeafNode) {
			List<String> nodeidList = nodesettingService.getNodeidLoop(nodeid, ifContainSelfNode,
					new ArrayList<String>());
			nodeids = new String[nodeidList.size()];
			nodeidList.toArray(nodeids);
		} else {
			nodeids = new String[] { nodeid };
		}
		Specification<Tb_entry_index_capture> searchNodeidCondition = ClassifySearchService
				.getSearchNodeidIndexCapture(nodeids);
		Specifications specifications = Specifications.where(searchNodeidCondition);
		if (content != null) {
			specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
		}
		Specification<Tb_entry_index_capture> formAdvancedSearch = ClassifySearchService
				.getFormAdvancedIndexCaptureSearch(formConditions, formOperators, logic);
		Specification<Tb_entry_index_capture> dateRangeCondition = ClassifySearchService
				.getDateRangeIndexCaptureCondition(daterangedata);
		//过滤在移交审核的条目
		List<Tb_transdoc_entry> transdocEntries = transdocEntryRepository.findByStatus("待审核");
		String[] entryidData = GainField.getFieldValues(transdocEntries, "entryid").length == 0 ? new String[]{""} : GainField.getFieldValues(transdocEntries, "entryid");
		Specification<Tb_entry_index_capture> searchEntryidsExcludeCondition = entryCaptureService
				.getSearchEntryidsExcludeCondition(entryidData);
		return entryIndexCaptureRepository.findAll(specifications.and(formAdvancedSearch).and(dateRangeCondition).and(searchEntryidsExcludeCondition),
				new Sort("archivecode"));
	}

	public List<Tb_entry_index_capture> getModifiedList(Tb_entry_index_capture entryIndexCapture,
			List<Tb_entry_index_capture> resultList, String calFieldcode, Integer number,
			List<String> codeSettingFieldList, List<String> codeSettingSplitCodes, String flag, String pages) {
		List<Tb_entry_index_capture> saveList = new ArrayList<>();
		for (Tb_entry_index_capture tb_entry_index_capture : resultList) {
			Tb_entry_index_capture entry_index_capture = new Tb_entry_index_capture();
			BeanUtils.copyProperties(tb_entry_index_capture, entry_index_capture);
			String calVal = (String) (GainField.getFieldValueByName(calFieldcode, entry_index_capture) != null
					&& !"".equals(GainField.getFieldValueByName(calFieldcode, entry_index_capture))
							? GainField.getFieldValueByName(calFieldcode, entry_index_capture) : "0");
			String pageno = entry_index_capture.getPageno() != null && !"".equals(entry_index_capture.getPageno())
					? entry_index_capture.getPageno() : "0";
			String modifiedCalVal = "";
			String modifiedPageno = "";
			Integer calIntVal = Integer.parseInt(calVal);
			Integer pagenoIntVal = Integer.parseInt(pageno);
			if ("insertion".equals(flag)) {// 插件
				modifiedCalVal = entryIndexService.alignValue(number, calIntVal + 1);
				// modifiedPageno = pagenoIntVal+Integer.parseInt(pages!=null &&
				// !"".equals(pages)?pages:"0")+"";//插件时pages值从前端接收
				modifiedPageno = pagenoIntVal + "";
			}
			if ("dismantle".equals(flag)) {// 拆件
				modifiedCalVal = entryIndexService.alignValue(number, calIntVal - 1);
				modifiedPageno = pagenoIntVal - Integer
						.parseInt(entryIndexCapture.getPages() != null && !"".equals(entryIndexCapture.getPages())
								? entryIndexCapture.getPages() : "0")
						+ "";// 拆件时pages值直接从后台获取
			}
			GainField.setFieldValueByName(calFieldcode, entry_index_capture, modifiedCalVal);
			entry_index_capture.setPageno(modifiedPageno);
			if (codeSettingFieldList.size() == 1) {
				entry_index_capture.setArchivecode(modifiedCalVal);
			} else {
				List<String> codeSettingFieldValues = new ArrayList<>();
				Map<String, Map<String, String>> mapFiled = entryIndexService.getConfigMap();//获取参数设置的MAP
				List<Tb_data_template> enumList = templateRepository.getByNodeidFtype("enum", entryIndexCapture.getNodeid());//获取某节点的模板中属于enum的字段
				for (String codeSettingFieldcode : codeSettingFieldList) {
					String codeSettingFieldValue = (String) (GainField.getFieldValueByName(codeSettingFieldcode,
							entry_index_capture) != null
									? GainField.getFieldValueByName(codeSettingFieldcode, entry_index_capture) : "");
					codeSettingFieldValue = entryIndexService.getConfigByName(codeSettingFieldcode, codeSettingFieldValue, enumList, mapFiled);
					codeSettingFieldValues.add(codeSettingFieldValue);
				}
				String archivecode = entryIndexService.produceArchivecode(codeSettingFieldValues, codeSettingSplitCodes,
						modifiedCalVal, entryIndexCapture.getNodeid());
				entry_index_capture.setArchivecode(archivecode);
			}
			saveList.add(entry_index_capture);
		}
		return saveList;
	}

	public Tb_entry_index_capture saveEntryIndexCapture(Tb_entry_index_capture tb_entry_index_capture) {
		return entryIndexCaptureRepository.save(tb_entry_index_capture);
	}

	public Tb_entry_detail_capture saveEntryDetailCapture(Tb_entry_detail_capture tb_entry_detail_capture) {
		return entryDetailCaptureRepository.save(tb_entry_detail_capture);
	}

	public List<Tb_entry_index_capture> saveEntryIndexCaptureList(List<Tb_entry_index_capture> entryIndexCaptures) {
		return entryIndexCaptureRepository.save(entryIndexCaptures);
	}

	public Integer updateEleId(String count, String entryid) {
		return entryIndexCaptureRepository.updateEleId(count, entryid);
	}

	public Tb_entry_index_capture findEntryIndexCapture(String entryid) {
		return entryIndexCaptureRepository.findByEntryid(entryid);
	}

	// 根据案卷、卷内的档号组成字段做匹配
	public List findAllByNodeidAndArchivecodeLike(Integer start, Integer limit, String nodeid, String entryid,
			Sort sort) {
		// 根据档号获取实体
		Tb_entry_index_capture tb_entry_index_capture = entryIndexCaptureRepository.findByEntryid(entryid);
		// 获取案卷档号设置字段集合
		List<String> ajCodeSettingFieldList = codesettingService
				.getCodeSettingFields(tb_entry_index_capture.getNodeid());
		// 档号设置字段值集合
		List<String> codeSettingFieldValues = new ArrayList<>();
		for (int i = 0; i < ajCodeSettingFieldList.size(); i++) {
			String codeSettingFieldValue = GainField.getFieldValueByName(ajCodeSettingFieldList.get(i),
					tb_entry_index_capture) + "";
			if (!"null".equals(codeSettingFieldValue) && !"".equals(codeSettingFieldValue)) {
				codeSettingFieldValues.add(codeSettingFieldValue);
			} else {
				codeSettingFieldValues.add("");
			}
		}
		// 获取卷内档号设置字段集合
		List<String> jnCodeSettingFieldList = codesettingService.getCodeSettingFields(nodeid);
		String searchCondition = entryIndexService.getJNSearchCondition(ajCodeSettingFieldList, codeSettingFieldValues,
				nodeid,
				jnCodeSettingFieldList.size() > 0 ? jnCodeSettingFieldList.get(jnCodeSettingFieldList.size() - 1) : "");
		List list = new ArrayList();
		// 返回的条件语句如果是空字符串，则返回空数据回前端
		if ("".equals(searchCondition)) {
			list.add(0);
			list.add(new ArrayList<Tb_entry_index_capture>());
			return list;
		}
		String countSql = "select count(nodeid) from v_index_detail_capture where " + searchCondition;
		String sql = "select * from v_index_detail_capture where " + searchCondition;
		Query qCount = entityManager.createNativeQuery(countSql);
		int count = Integer.valueOf(qCount.getSingleResult().toString());
		String sortstr = " order by archivecode desc";
		if (sort != null && sort.iterator().hasNext()) {
			Sort.Order order = sort.iterator().next();
			sortstr = " order by " + order.getProperty() + " " + order.getDirection();
		}
		sql = sql + sortstr;
		Query query = entityManager.createNativeQuery(sql, Tb_index_detail_capture.class);
		query.setFirstResult(start);
		query.setMaxResults(limit);
		list.add(count);
		list.add(query.getResultList());
		return list;
		// Specification<Tb_entry_index_capture> searchNodeID = null;
		// if(nodeid!=null && !("".equals(nodeid))){
		// searchNodeID = EntryCaptureService.getSearchNodeidCondition(nodeid);
		// }
		// Specifications specifications = Specifications.where(searchNodeID);
		//// specifications = specifications.and(new
		// SpecificationUtil("archivecode","like",archivecode + "-%"));
		// specifications = specifications.and(new
		// SpecificationUtil("archivecode","beginAt",archivecode + "-"));
		// PageRequest pageRequest = new PageRequest(page-1,limit,new
		// Sort("archivecode"));
		// return entryIndexCaptureRepository.findAll(specifications,
		// pageRequest);
	}

	public String findNodeidByEntryid(String entryid) {
		if (entryid != null && !("".equals(entryid))) {
			return entryIndexCaptureRepository.findNodeidByEntryid(entryid);
		}
		return null;
	}

	/**
	 * 修改拖动卷内文件后，修改卷内顺序号、同时修改相应档号，卷内文件重新排序
	 * 
	 * @param entryIndexCapture
	 *            需移动的采集条目
	 * @param target
	 *            移动目标位置
	 * @param filearchivecode
	 *            卷内文件所属案卷的档号
	 * @param nodeid
	 *            卷内文件数据节点ID
	 * @return
	 */
	public ExtMsg modifyJnEntryindexcaptureOrder(Tb_entry_index_capture entryIndexCapture, int target,
			String filearchivecode, String nodeid) {
		Integer number = codesettingService.getCalFieldLength(nodeid);// 获取计算项单位长度
		List<Tb_entry_index_capture> entryIndexCaptures = entryIndexCaptureRepository
				.findAllByNodeidAndArchivecodeLike(nodeid, filearchivecode + "-%");
		int[] innerFileValues = new int[entryIndexCaptures.size()];
		ExtMsg illegalCharErrorMsg = new ExtMsg(false, "卷内顺序号包含非法字符，无法排序", null);
		for (int i = 0; i < entryIndexCaptures.size(); i++) {
			try {
				int innerFileValue = Integer.parseInt(entryIndexCaptures.get(i).getInnerfile());
				innerFileValues[i] = innerFileValue;
			} catch (NumberFormatException e) {
				return illegalCharErrorMsg;
			}
		}
		int innerFile = 0;
		try {
			innerFile = Integer.parseInt(entryIndexCapture.getInnerfile());
		} catch (NumberFormatException e) {
			return illegalCharErrorMsg;
		}
		if (innerFile < target) {
			// 后移。1.将目标位置包括后面的所有数据后移一个位置；
			for (int i = 0; i < innerFileValues.length; i++) {
				if (innerFileValues[i] >= target) {
					innerFileValues[i]++;
				}
			}
		} else {
			// 前移。1.将目标位置及以后，当前数据以前的数据后移一个位置；
			for (int i = 0; i < innerFileValues.length; i++) {
				if (innerFileValues[i] >= target && innerFileValues[i] < innerFile) {
					innerFileValues[i]++;
				}
			}
		}
		// 2.重新设置卷内顺序号及档号
		ExtMsg illegalCodesetErrorMsg = new ExtMsg(false, "档号设置异常，无法排序", null);
		List<String> codeSettingSplitCodes = codesettingService.getCodeSettingSplitCodes(nodeid);
		if (codeSettingSplitCodes.size() == 0) {
			return illegalCodesetErrorMsg;
		}
		List<String> codesetFieldNames = codesetRepository.findFieldcodeByDatanodeid(nodeid);
		if (codesetFieldNames.size() == 0) {
			return illegalCodesetErrorMsg;
		}
		Map<String, Map<String, String>> mapFiled = entryIndexService.getConfigMap();//获取参数设置的MAP
		List<Tb_data_template> enumList = templateRepository.getByNodeidFtype("enum", nodeid);//获取某节点的模板中属于enum的字段
		List<String> codesetFieldValues = new ArrayList<>();
		for (int i = 0; i < codesetFieldNames.size(); i++) {
			String codesetFieldValue = (String) GainField.getFieldValueByName(codesetFieldNames.get(i),
					entryIndexCaptures.get(i));
			codesetFieldValue = entryIndexService.getConfigByName(codesetFieldNames.get(i), codesetFieldValue, enumList, mapFiled);
			codesetFieldValues.add(codesetFieldValue);
		}
		for (int i = 0; i < entryIndexCaptures.size(); i++) {
			if (entryIndexCaptures.get(i).getEntryid() == entryIndexCapture.getEntryid()) {
				entryIndexCaptures.get(i).setInnerfile(entryIndexService.alignValue(number, target));
			} else {
				entryIndexCaptures.get(i).setInnerfile(entryIndexService.alignValue(number, innerFileValues[i]));
			}
			String archivecode = entryIndexService.produceArchivecode(codesetFieldValues, codeSettingSplitCodes,
					entryIndexCaptures.get(i).getInnerfile(), nodeid);// 档号
			entryIndexCaptures.get(i).setArchivecode(archivecode);
		}
		entryIndexCaptureRepository.save(entryIndexCaptures);
		return null;
	}

	public Page<Tb_entry_index_temp> getEntryIndexCaptures(String entryids, String dataSource, int page, int limit,
														   Sort sort,String ygType,String dataNodeid) {
		String uniquetag=BatchModifyService.getUniquetagByType("cjgd");
		List<String> entryidList = new ArrayList<>();
		for (int i = 0; i < entryids.split(",").length; i++) {
			entryidList.add(entryids.split(",")[i]);
		}
		if ("capture".equals(dataSource)&&"".equals(ygType)) {// 初次加载归档预览列表时
			//addEntriesToTemp(entryids.split(","),uniquetag,dataNodeid);
		}
		List<Sort.Order> sorts = new ArrayList<>();
		if ("".equals(dataSource)||"del".equals(dataSource)){//排序状态  ,del是取消预归档标记
			sorts.add(new Sort.Order(Sort.Direction.ASC, "sortsequence"));// 归档顺序号升序
		}else{//进入预归档
			sorts=getSort(sorts,dataNodeid);
		}

		if("ygd".equals(ygType)){//归档nodeid不变，预归档页面直接查所有个人用户添加的预归档数据
			return entryIndexTempRepository.findByUniquetag(uniquetag,
					new PageRequest(page - 1, limit, sort == null ? new Sort(sorts) : sort));
		}else if("ygdChange".equals(ygType)){//归档nodeid改变，预归档页面直接查所有个人用户添加的未修改数据和为生成档号前的预归档数据
			List<String> stringList=entryIndexTempRepository.findEntryidByUniquetag(uniquetag);//获取临时表个人数据
			if(stringList.size()>0){//获取到entryids后重新加载采集表数据到临时表
				//List转String
				String[] entries=stringList.toArray(new String[stringList.size()]);
				batchModifyService.deleteEntryIndexTempByUniquetag();//先清空个人数据
				addEntriesToTemp(entries,uniquetag,dataNodeid);//重新加载采集表数据
			}
			return entryIndexTempRepository.findByUniquetag(uniquetag,
					new PageRequest(page - 1, limit, sort == null ? new Sort(sorts) : sort));
		}else{
			return entryIndexTempRepository.findByEntryidInAndUniquetag(entryidList, uniquetag,
					new PageRequest(page - 1, limit,  sort));
		}

	}


	//预归档排序获取
	public List<Sort.Order> getSort(List<Sort.Order> sorts ,String nodeid){
		List<Tb_orderset> ordersetList=ordersetRepository.findByDatanodeidOrderByOrdernum(nodeid);
		if(ordersetList.size()>0){
			for(Tb_orderset orderset:ordersetList){
				String fieldcode=orderset.getFieldcode();
				String direction=orderset.getDirection();
				if("1".equals(direction)){//1是降序
					sorts.add(new Sort.Order(Sort.Direction.DESC, fieldcode));
				}else{//默认升序
					sorts.add(new Sort.Order(Sort.Direction.ASC, fieldcode));
				}
			}
		}else{//没有设置排序规则，则用默认排序
			sorts.add(new Sort.Order(Sort.Direction.ASC, "serial"));// 流水号升序
			sorts.add(new Sort.Order(Sort.Direction.ASC, "filenumber"));// 文件编号升序
			sorts.add(new Sort.Order(Sort.Direction.ASC, "descriptiondate"));// 著录时间升序
			sorts.add(new Sort.Order(Sort.Direction.ASC, "title"));// 题名升序
		}
		return sorts;
	}

	//预归档排序sql获取
	public String getOrderStr(String nodeid){
		String sortStr="";//排序
		List<Tb_orderset> ordersetList=ordersetRepository.findByDatanodeidOrderByOrdernum(nodeid);
		if(ordersetList.size()>0){
			for(Tb_orderset orderset:ordersetList){
				String fieldcode=orderset.getFieldcode();
				String direction=orderset.getDirection();
				if("1".equals(direction)){//1是降序
					sortStr += fieldcode+" desc,";
				}else{//默认升序
					sortStr += fieldcode+" asc,";
				}
			}
			sortStr=" order by " +sortStr.substring(0,sortStr.length()-1);
		}else{
			sortStr += " order by serial asc, filenumber asc,descriptiondate asc, title asc ";
		}
		return sortStr;
	}

	//根据entryid加载采集表的数据到临时表
	public void addEntriesToTemp(String[] entryids,String uniquetag,String nodeid){
		List<Tb_index_detail_capture> result = new ArrayList<>();
		String entryidStr ="";
		if(entryids.length>0){
			entryidStr = " and sid.entryid in('" + String.join("','", entryids)+ "') ";
		}
		String sql = "select sid.* from v_index_detail_capture sid where 1=1 " + entryidStr;
		Query query = entityManager.createNativeQuery(sql, Tb_index_detail_capture.class);
		result = query.getResultList();
		List<Tb_entry_index_temp> tempList = new ArrayList<>();
		for (Tb_index_detail_capture entry_index_capture : result) {
			Tb_entry_index_temp entry_index_temp = new Tb_entry_index_temp();
			BeanUtils.copyProperties(entry_index_capture, entry_index_temp,new String[]{"sortsequence"});
			entry_index_temp.setUniquetag(BatchModifyService.getUniquetagByType("cjgd"));
			entry_index_temp.setArchivecode("");
			entry_index_temp.setNodeid(nodeid);
			tempList.add(entry_index_temp);
		}
		entryIndexTempRepository.save(tempList);

		//增加完后排序
		setSortsequence(nodeid,uniquetag);
	}

	//临时表排序
	public void setSortsequence(String nodeid,String uniquetag){
		String sortStr=getOrderStr(nodeid);
		String sql = "select sid.* from tb_entry_index_temp sid where uniquetag='"+uniquetag+"' " + sortStr;
		Query query = entityManager.createNativeQuery(sql, Tb_entry_index_temp.class);
		List<Tb_entry_index_temp> tempList=query.getResultList();
		for(int i=0;i<tempList.size();i++){
			Tb_entry_index_temp temp=tempList.get(i);
			temp.setSortsequence(i+1);
		}
	}

	public Page<Tb_entry_index_sqtemp> getSqtempEntryIndexCaptures(String entryids, String dataSource, String nodeid,
			int page, int limit, Sort sort) {
		List<String> entryidList = new ArrayList<>();
		if (entryids != null && !entryids.equals("")) {
			for (int i = 0; i < entryids.split("∪").length; i++) {
				entryidList.add(entryids.split("∪")[i]);
			}
		}
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(new Sort.Order(Sort.Direction.ASC, "archivecode"));// 原档号升序
		List<String> codeSet = codesetRepository.findFieldcodeByDatanodeid(nodeid);
		String calvalue = "";
		if (codeSet.size() > 0) {
			calvalue = codeSet.get(codeSet.size() - 1);
		}
		if ("capture".equals(dataSource)) {// 初次加载调序列表时
			List<Tb_entry_index_capture> result = entryIndexCaptureRepository.findByEntryidIn(entryids.split("∪"));
			List<Tb_entry_index_sqtemp> tempList = new ArrayList<>();
			for (Tb_entry_index_capture entry_index : result) {
				Tb_entry_index_sqtemp entry_index_sqtemp = new Tb_entry_index_sqtemp();
				BeanUtils.copyProperties(entry_index, entry_index_sqtemp);
				entry_index_sqtemp.setCalvalue((String) GainField.getFieldValueByName(calvalue, entry_index));
				entry_index_sqtemp.setNewarchivecode(entry_index.getArchivecode());// 未作修改时,新档号默认为初始档号
				entry_index_sqtemp.setUniquetag(BatchModifyService.getUniquetag());
				tempList.add(entry_index_sqtemp);
			}
			entryIndexSqTempRepository.save(tempList);
		} else {
			if (entryids == null || entryids.equals("")) {
				String uniquetag = BatchModifyService.getUniquetag();
				List<String> sqtemps = entryIndexSqTempRepository.findEntryidByNodeidAndUniquetag(nodeid, uniquetag);
				entryidList.addAll(sqtemps);
			}
		}
		return entryIndexSqTempRepository.findByEntryidInAndUniquetag(entryidList, BatchModifyService.getUniquetag(),
				new PageRequest(page - 1, limit, sort == null ? new Sort(sorts) : sort));
	}

	public List<Tb_entry_detail_capture> filingEntryIndexCaptures(String entryids,String userid) {

		String uniquetag=BatchModifyService.getUniquetagByType("cjgd");
		List<Tb_entry_index_temp> entryIndexTemps =new ArrayList<>();
		String[] idArr = entryids.split(",");
		if("".equals(entryids)){//全部条目归档
			List<String> entryidList=entryIndexTempRepository.findEntryidByUniquetag(uniquetag);
			entryIndexTemps = entryIndexTempRepository.findByUniquetagOrderBySortsequence(uniquetag);
			idArr=entryidList.toArray(new String[entryidList.size()]);
		}else{//选择条目归档
			entryIndexTemps = entryIndexTempRepository.findByEntryidInAndUniquetag(idArr, uniquetag);
		}
		List<Tb_entry_index_capture> entryIndexCaptures = new ArrayList<>();
		List<Tb_entry_detail_capture> entryDetailCaptures = new ArrayList<>();
		for (int i=0;i<entryIndexTemps.size();i++) {
			Tb_entry_index_temp entryIndexTemp=entryIndexTemps.get(i);
			Tb_entry_index_capture entryIndexCapture = new Tb_entry_index_capture();
			Tb_entry_detail_capture entryDetailCapture=new Tb_entry_detail_capture();
			BeanUtils.copyProperties(entryIndexTemp, entryIndexCapture);
			BeanUtils.copyProperties(entryIndexTemp, entryDetailCapture);
			if (entryIndexTemp.getFscount() != null && !"0".equals(entryIndexTemp.getFscount())) {
				entryIndexCapture.setKccount(entryIndexTemp.getFscount());
			} else if (entryIndexTemp.getKccount() != null && !"0".equals(entryIndexTemp.getKccount())) {
				entryIndexCapture.setFscount(entryIndexTemp.getKccount());
			} else {// 如果库存份数与份数都为null，那么归档后数量就默认为1
				entryIndexCapture.setFscount("1");
				entryIndexCapture.setKccount("1");
			}
			entryIndexCapture=entryIndexCaptureRepository.save(entryIndexCapture);
			entryDetailCapture.setEntryid(entryIndexCapture.getEntryid());
			entryDetailCaptureRepository.save(entryDetailCapture);
			entryDetailCaptures.add(entryDetailCapture);
			webSocketService.refreshArchiveProgressBar(userid,i+1+"&"+entryIndexTemps.size());// 通知刷新
		}
		entryIndexTempRepository.deleteByUniquetagAndEntryids(uniquetag,idArr);
		return entryDetailCaptures;
	}

	public Map<String, Object> getFileNodeidAndEntryid(String nodeid, String entryid) {
		Map<String, Object> result = new HashMap<>();
		Tb_entry_index_capture index = entryIndexCaptureRepository.findByEntryid(entryid);
		String innerfileArchivecode = index.getArchivecode();
		String innerfileNodeid = index.getNodeid();
		/*tring innerfileParentnodeid = dataNodeRepository.findParentnodeidByNodeid(innerfileNodeid);// 卷内文件节点的父节点nodeid
		List<String> childrenNodeids = entryIndexService.getNodeidByWithAs(innerfileParentnodeid);// 卷内文件节点并列的节点的nodeid集合
		String[] childrenNodeidArr = new String[childrenNodeids.size()];
		childrenNodeids.toArray(childrenNodeidArr);*/
		List<String> codeSettingSplitCodes = codesetRepository.findSplitcodeByDatanodeid(innerfileNodeid);
		String fileArchivecode = "";
		if (codeSettingSplitCodes.size() > 1) {
			String codeSettingSplitCode = codeSettingSplitCodes.get(codeSettingSplitCodes.size() - 2);// 档号设置中倒数第二个字段的分隔符
			fileArchivecode = innerfileArchivecode.substring(0, innerfileArchivecode.lastIndexOf(codeSettingSplitCode));
		}
		/*// 根据档号查找是否存在案卷的条目
		List<String> fileEntryids = entryIndexCaptureRepository.findEntryidByArchivecodeAndNodeidIn(fileArchivecode,
				childrenNodeidArr);
		if (fileEntryids.size() == 0) {
			String filenodeid = publicUtilService.getFileNodeid(innerfileNodeid);
			fileEntryids = entryIndexCaptureRepository.findEntryidByArchivecodeAndNodeidIn(fileArchivecode,
					new String[] { filenodeid });
		}*/
		String filenodeid = publicUtilService.getFileNodeid(nodeid);
		List<String> fileEntryids  = entryIndexCaptureRepository.findEntryidByArchivecodeAndNodeidIn(fileArchivecode,
				new String[] { filenodeid });
		String fileEntryid;
		if (fileEntryids.size() == 0) {
			fileEntryid = "";
		} else {
			fileEntryid = fileEntryids.get(0);
		}
		//String fileNodeid = publicUtilService.getFileNodeid(nodeid);
		result.put("entryid", fileEntryid);
		result.put("nodeid", filenodeid);
		result.put("archivecode", fileArchivecode);
		return result;
	}

	public Map<String, Object> getFileNodeidAndEntryidByInnerfileEntryid(String entryid) {
		Map<String, Object> result = new HashMap<>();
		Tb_entry_index_capture index = entryIndexCaptureRepository.findByEntryid(entryid);
		String innerfileArchivecode = index.getArchivecode();
		String innerfileNodeid = index.getNodeid();
		String innerfileParentnodeid = dataNodeRepository.findParentnodeidByNodeid(innerfileNodeid);// 卷内文件节点的父节点nodeid
		List<String> childrenNodeids = entryIndexService.getNodeidByWithAs(innerfileParentnodeid);// 卷内文件节点并列的节点的nodeid集合
		String[] childrenNodeidArr = new String[childrenNodeids.size()];
		childrenNodeids.toArray(childrenNodeidArr);
		List<String> codeSettingSplitCodes = codesetRepository.findSplitcodeByDatanodeid(innerfileNodeid);
		String fileArchivecode = "";
		if (codeSettingSplitCodes.size() > 1) {
			String codeSettingSplitCode = codeSettingSplitCodes.get(codeSettingSplitCodes.size() - 2);// 档号设置中倒数第二个字段的分隔符
			fileArchivecode = innerfileArchivecode.substring(0, innerfileArchivecode.lastIndexOf(codeSettingSplitCode));
		}
		List<String> fileEntryids = entryIndexCaptureRepository.findEntryidByArchivecodeAndNodeidIn(fileArchivecode,
				childrenNodeidArr);
		if (fileEntryids.size() == 0) {
			String filenodeid = publicUtilService.getFileNodeid(innerfileNodeid);
			fileEntryids = entryIndexCaptureRepository.findEntryidByArchivecodeAndNodeidIn(fileArchivecode,
					new String[] { filenodeid });
		}
		String fileEntryid;
		if (fileEntryids.size() == 0) {
			fileEntryid = "";
		} else {
			fileEntryid = fileEntryids.get(0);
		}
		String fileNodeid = entryIndexCaptureRepository.findNodeidByEntryid(fileEntryid);
		result.put("entryid", fileEntryid);
		result.put("nodeid", fileNodeid);
		return result;
	}

	public Object generateArchivecode(String entryids, String nodeid, String[] filingValuesStrArr,
									  Map<String, String> entryidEntryretentionMap,String uniquetag) {
		String[] entryidsData = entryids.split(",");
		List<Tb_entry_index_temp> temps=entryIndexTempRepository.findByEntryidInAndUniquetagOrderBySortsequence(entryidsData,uniquetag);
		Object info = regenerateCodesettingFieldValue(temps, nodeid, filingValuesStrArr,
				entryidEntryretentionMap);
		if (!info.getClass().toString().equals("class java.lang.String")) {
			temps = (List<Tb_entry_index_temp>) info;
		} else {// 档号设置分隔符找不到时regenerateCodesettingFieldValue方法返回null
			return info.toString();
		}
		return entryIndexTempRepository.save(temps);
	}

	private static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	/**
	 * 数据采集归档界面点击“生成档号”时,设置档号构成字段值（包括档号）
	 * 
	 * @param entryIndexCaptures
	 *            需更新的条目数据
	 * @param nodeid
	 *            归档目标节点的节点ID
	 * @param filingValuesStrArr
	 *            档号设置字段的值（表单中的输入值）
	 * @param entryidEntryretentionMap
	 *            保管期限自动鉴定结果map
	 * @return 更新后的条目数据
	 */
	private Object  regenerateCodesettingFieldValue(List<Tb_entry_index_temp> entryIndexCaptures, String nodeid,
													String[] filingValuesStrArr, Map<String, String> entryidEntryretentionMap) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Tb_codeset> codeSettingList = codesetRepository.findByDatanodeidOrderByOrdernum(nodeid);// 获取档号设置集合
		//List<Object> fieldLength = codesetRepository.findFieldlengthByDatanodeid(nodeid);
		// 遍历表单字段，设置除计算项以外的档号字段对应的属性值
		//List<String> codeSettingFieldList = codesettingService.getCodeSettingFields(nodeid);
		Map<String, Map<String, String>> mapFiled = entryIndexService.getConfigMap();//获取参数设置的MAP
		List<Tb_data_template> enumList = templateRepository.getByNodeidFtype("enum", nodeid);//获取某节点的模板中属于enum的字段
		//List<String> codeSettingSplitCodes = codesettingService.getCodeSettingSplitCodes(nodeid);
		List<String> codeSettingSplitCodes = new ArrayList<>();//档号分隔符集合
		List<String> fieldLength=new ArrayList<>();//字段长度集合
		List<String> codeSettingFieldList=new ArrayList<>();//字段集合
		for(Tb_codeset codeset:codeSettingList){
			codeSettingSplitCodes.add(codeset.getSplitcode());
			fieldLength.add(String.valueOf(codeset.getFieldlength()));
			codeSettingFieldList.add(codeset.getFieldcode());
		}
		for (int i = 0; i < entryIndexCaptures.size(); i++) {// 遍历每条需归档的记录
			// 设置所有记录统一的节点ID--------第一步
			entryIndexCaptures.get(i).setNodeid(nodeid);
			List<String> codeSettingFieldValues = new ArrayList<>();
			if (filingValuesStrArr != null && filingValuesStrArr.length > 0) {
				Integer num = 0;
				// 如果页面没有传入计算值
				if (filingValuesStrArr.length < codeSettingFieldList.size()) {
					num = filingValuesStrArr.length;
				} else {
					num = filingValuesStrArr.length - 1;
				}
				for (int j = 0; j < num; j++) {
					Integer length = Integer.valueOf(fieldLength.get(j));
					String[] fieldNameAndValue = filingValuesStrArr[j].split("∪");
					String fieldName = fieldNameAndValue[0];// 属性名
					String value = fieldNameAndValue[1];// 属性值
					if (isNumeric(value)) {
						value = entryIndexService.alignValue(length, Integer.valueOf(value));
					}
					GainField.setFieldValueByName(fieldName, entryIndexCaptures.get(i), value);
					value = entryIndexService.getConfigByName(fieldName, value, enumList, mapFiled);
					if (isNumeric(value)) {
						value = entryIndexService.alignValue(length, Integer.valueOf(value));
					}
					codeSettingFieldValues.add(value);
				}
			}
			if (!entryidEntryretentionMap.isEmpty()) {// 保管期限值通过自动鉴定获得
				entryIndexCaptures.get(i)
						.setEntryretention(entryidEntryretentionMap.get(entryIndexCaptures.get(i).getEntryid()));
			}

			// 设置计算项字段对应的属性值--------第三步
			String calValue = "";
			Integer size = codeSettingList.size() - 1;
			String calculation = codeSettingFieldList.get(size);
			Integer number = (int)codeSettingList.get(size).getFieldlength();;// 获取计算项单位长度
			if (filingValuesStrArr.length == codeSettingFieldList.size()) {// 如果页面传入了计算值
				String info = filingValuesStrArr[filingValuesStrArr.length - 1].split("∪")[1];
				if (i == 0) {
					calValue = info;
				} else {
					calValue = String.valueOf(Integer.parseInt(info) + i);
				}
			} else {
				calValue = String.valueOf(getCalValue(entryIndexCaptures.get(i), nodeid, codeSettingList,""));
			}
			calValue = entryIndexService.alignValue(number, Integer.valueOf(calValue));
			GainField.setFieldValueByName(calculation, entryIndexCaptures.get(i), calValue);
			codeSettingFieldValues.add(calValue);

			// 设置档号--------第四步
			if (codeSettingSplitCodes.size() == 0) {
				return null;
			}
			if (!entryidEntryretentionMap.isEmpty()) {
				// 若保管期限为自动鉴定生成，则档号构成字段值中的保管期限不从前端页面读取，而是间接获取，以下代码将保管期限值插入到list的指定位置
				codeSettingFieldValues.add(
						codesetRepository.findOrdernumByFieldcodeAndDatanodeid("entryretention", nodeid) - 1,
						entryIndexCaptures.get(i).getEntryretention());
			}
			String archivecode = entryIndexService.produceArchivecode(codeSettingFieldValues, codeSettingSplitCodes,
					calValue, nodeid);// 档号
			// 查询当前节点所有数据的档号,判断档号的唯一性
			List<String> archivecodes = entryIndexCaptureRepository.findArchivecodeByNodeid(archivecode, nodeid);
			if (archivecodes.size() > 0) {
				return archivecode;
			}
			entryIndexCaptures.get(i).setArchivecode(archivecode);
			webSocketService.refreshGenerateArchivecodeBar(userDetails.getUserid(),i+1+"&"+entryIndexCaptures.size());// 通知刷新
		}
		return entryIndexCaptures;
	}

	public static Specification<Tb_entry_index_capture> getSearchEntryidCondition(String entryid) {
		Specification<Tb_entry_index_capture> searchEntryid = new Specification<Tb_entry_index_capture>() {
			@Override
			public Predicate toPredicate(Root<Tb_entry_index_capture> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				Predicate p = criteriaBuilder.equal(root.get("entryid"), entryid);
				return criteriaBuilder.and(p);
			}
		};
		return searchEntryid;
	}

	public static Specification<Tb_entry_index_capture> getSearchNodeidCondition(String[] nodeids) {
		Specification<Tb_entry_index_capture> searchNodeID = null;
		if (nodeids != null) {
			if (nodeids.length > 0) {
				searchNodeID = new Specification<Tb_entry_index_capture>() {
					@Override
					public Predicate toPredicate(Root<Tb_entry_index_capture> root, CriteriaQuery<?> criteriaQuery,
							CriteriaBuilder criteriaBuilder) {
						Predicate[] predicates = new Predicate[nodeids.length];
						for (int i = 0; i < nodeids.length; i++) {
							predicates[i] = criteriaBuilder.equal(root.get("nodeid"), nodeids[i]);
							// predicates[i] =
							// criteriaBuilder.equal(root.get("tdn").get("nodeid"),nodeids[i]);
						}
						return criteriaBuilder.or(predicates);
					}
				};
			}
		}
		return searchNodeID;
	}

	public static Specification<Tb_entry_index_capture> getSearchCalvalueCondition(String calFieldcode,
			String calValue) {
		Specification<Tb_entry_index_capture> searchCalvalueCondition = new Specification<Tb_entry_index_capture>() {
			@Override
			public Predicate toPredicate(Root<Tb_entry_index_capture> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				Predicate p = criteriaBuilder.greaterThan(root.get(calFieldcode), calValue);
				return criteriaBuilder.and(p);
			}
		};
		return searchCalvalueCondition;
	}

	/**
	 * 拆卷、件的删除
	 * 
	 * @param entryid
	 * @param nodeid
	 * @param title
	 * @param syncType
	 */
	public String delEntry(String entryid, String nodeid, String title, String syncType) {
		Tb_entry_index_capture tei = entryIndexCaptureRepository.findByEntryid(entryid);
		String archivecode = tei.getArchivecode();
		String innerfile = tei.getInnerfile();
		String filecode = tei.getFilecode();
		String recordcode = tei.getRecordcode();// 件号 案件用
		String zjPages = tei.getPages();
		String zjNodeid = tei.getNodeid();

		if (archivecode == null) {
			return "档号不能为空";
		}

		List<Tb_codeset> codeFieldList = codesetRepository.findByDatanodeidOrderByOrdernum(zjNodeid); // 获取档号设置字段集合
		if (codeFieldList.size() != 0) {
			Tb_codeset codeset = codeFieldList.get(codeFieldList.size() - 1); // 获取最后一个档号组成字段
			Object lastFieldValue = GainField.getFieldValueByName(codeset.getFieldcode(), tei);// 获取字段值
			if (lastFieldValue == null) {
				return codeset.getFieldname() + "不能为空";
			}
		} else {
			return "档号组成有误";
		}

		// 删除案卷或卷内文件
		entryDetailCaptureRepository.deleteByEntryid(entryid);//根据entryid 删除采集条目扩充表的数据
		electronicCaptureRepository.deleteByEntryid(entryid);//根据entryid 删除电子文件采集表数据
		entryIndexCaptureRepository.deleteByEntryid(entryid);//根据entryid 删除采集条目表数据

		String jnNodeid = "";//卷内nodeid
		if (syncType.equals("syncInnerFile") && title.equals("1")) {// 同步删除案卷的卷内文件
			// 先查找要删除的entyid
			jnNodeid = publicUtilService.getJnNodeid(zjNodeid);// 获取卷内nodeid
			List<String> entryids = entryIndexCaptureRepository.findAllByNodeid(archivecode, jnNodeid);
			String[] ids = entryids.toArray(new String[entryids.size()]);
			entryIndexCaptureRepository.deleteByEntryidIn(ids);
			entryDetailCaptureRepository.deleteByEntryidIn(ids);
			electronicCaptureRepository.deleteByEntryidIn(ids);
		}

		if (title.equals("1")) {// 案卷删除后排序
			// 先截取档号前端
			String parentArchivecode = archivecode.substring(0, archivecode.lastIndexOf(filecode));
			// 根据案卷号获取后边的相应案卷号的案卷(排除卷内文件)
			List<Tb_entry_index_capture> ajNextList = entryIndexCaptureRepository
					.findAllByArchivecodeLikeAndNext(parentArchivecode, filecode, zjNodeid);
			if (ajNextList.size() > 0) {
				for (int i = 0; i < ajNextList.size(); i++) {
					// 拆件后，后边的案卷号全部减1
					String filecode0 = ajNextList.get(i).getFilecode();
					String archivecode0 = ajNextList.get(i).getArchivecode();
					String filecode1 = "";
					int num = filecode0.length();
					try {
						filecode1 = Integer.parseInt(filecode0) - 1 + "";
					} catch (Exception e) {
						e.printStackTrace();
					}
					int newNum = filecode1.length();
					// 重新拼接filecode
					if (filecode1.length() < num) {
						for (int j = 0; j < num - newNum; j++) {
							filecode1 = 0 + filecode1;
						}
					}

					int pNum = parentArchivecode.length();
					if (syncType.equals("syncInnerFile")) {// 同步更新卷内
						// 更新案卷和相关卷内的filecode
						// 现获取该filecode的案卷和卷内,然后按新的案卷号filecode和新的档号archivecode一一更新
						List<Tb_entry_index_capture> ajOneList = entryIndexCaptureRepository
								.findAllByArchivecodeAndFilecode(parentArchivecode, filecode0, zjNodeid, jnNodeid);
						for (int k = 0; k < ajOneList.size(); k++) {
							// 重新拼接archivecode
							String archivecodeOne = ajOneList.get(k).getArchivecode();
							String subArch = archivecodeOne.substring(pNum, archivecodeOne.length());// 获取案卷号和卷内号
							String newSunArch = subArch.replaceFirst(filecode0, filecode1);// 新案卷号
							String newArchivecode = parentArchivecode + newSunArch;
							entryIndexCaptureRepository.updateFilecodeAndArchivecode(filecode1, newArchivecode,
									archivecodeOne, zjNodeid, jnNodeid);
						}
					} else {// 只更新案卷
						String subArch = archivecode0.substring(pNum, archivecode0.length());// 获取案卷号和卷内号
						String newSunArch = subArch.replaceFirst(filecode0, filecode1);// 新案卷号
						String newArchivecode = parentArchivecode + newSunArch;
						entryIndexCaptureRepository.updateFilecodeAndArchivecode(filecode1, newArchivecode,
								archivecode0, zjNodeid);
					}

				}
			}
		} else if ((title.equals("2") || innerfile != null)) {// 卷内文件删除后排序
			// 更新案卷文件总数和总页数
			int jnadd = 0;
			updateAjuFile(archivecode, innerfile, jnadd, zjPages, zjNodeid, entryid);
			// 排序
			updateJnSqu(archivecode, innerfile, zjNodeid);
		} else if (title.equals("3") && innerfile == null) {// 案卷条目没有卷内顺序号
			updateAjiSqu(archivecode, recordcode, zjNodeid);// 案件排序
		}
		return "拆件成功";
	}

	/**
	 * 案件拆件排序
	 */
	public void updateAjiSqu(String archivecode, String recordcode, String nodeid) {
		// 先截取档号前端
		String parentArchivecode = archivecode.substring(0, archivecode.lastIndexOf(recordcode));
		// 根据案卷号获取后边的相应件号的案卷(排除卷内文件)
		List<Tb_entry_index_capture> ajNextList = entryIndexCaptureRepository
				.findAllByArchivecodeLikeAndNextRecord(parentArchivecode, recordcode, nodeid);
		if (ajNextList.size() > 0) {
//			for (int i = 0; i < ajNextList.size(); i++) {
			for(Tb_entry_index_capture tb : ajNextList){
				// 拆件后，后边的案卷号全部减1
				String recordcode0 = tb.getRecordcode();
				String archivecode0 = tb.getArchivecode();
				String recordcode1 = "";
				int num = recordcode0.length();
				try {
					recordcode1 = Integer.parseInt(recordcode0) - 1 + "";
				} catch (Exception e) {
					e.printStackTrace();
				}
				int newNum = recordcode1.length();
				// 重新拼接recordcode
				if (recordcode1.length() < num) {
					for (int j = 0; j < num - newNum; j++) {
						recordcode1 = 0 + recordcode1;
					}
				}
				int pNum = parentArchivecode.length();
				String subArch = archivecode0.substring(pNum, archivecode0.length());// 获取件号
				String newSunArch = subArch.replaceFirst(recordcode0, recordcode1);// 新件号
				String newArchivecode = parentArchivecode + newSunArch;
				tb.setRecordcode(recordcode1);
				tb.setArchivecode(newArchivecode);
				entityManager.merge(tb);
//				entryIndexCaptureRepository.updateRecordcodeAndArchivecode(recordcode1, newArchivecode, archivecode0,
//						nodeid);
			}
			entityManager.flush();//使用批量更新代替循环更新 节省建立数据库连接
			entityManager.clear();
		}
	}

	/**
	 * 卷内文件拆件排序
	 */
	public void updateJnSqu(String archivecode, String innerfile, String nodeid) {
		int index=archivecode.lastIndexOf(innerfile);
		if(index>=0) {// 先截取档号前端
			String parentArchivecode = archivecode.substring(0, index);

			// 获取案件之后的其他同一卷内的卷内文件，然后给他们的innerfile和档号-1
			List<Tb_entry_index_capture> jnNextList = entryIndexCaptureRepository
					.findInnerByArchivecodeLikeAndNext(parentArchivecode, innerfile, nodeid);
			if (jnNextList.size() > 0) {
				for (int i = 0; i < jnNextList.size(); i++) {
					// 拆件后，后边的卷内号全部减1
					String innerfile0 = jnNextList.get(i).getInnerfile();
					String archivecodeOne = jnNextList.get(i).getArchivecode();
					String innerfile1 = "";
					int num = innerfile0.length();
					try {
						innerfile1 = Integer.parseInt(innerfile0) - 1 + "";
					} catch (Exception e) {
						e.printStackTrace();
					}
					int newNum = innerfile1.length();
					// 重新拼接filecode
					if (innerfile1.length() < num) {
						for (int j = 0; j < num - newNum; j++) {
							innerfile1 = 0 + innerfile1;
						}
					}
					int pNum = parentArchivecode.length();
					String subArch = archivecodeOne.substring(pNum, archivecodeOne.length());// 获取案卷号和卷内号
					String newSunArch = subArch.replaceFirst(innerfile0, innerfile1);// 新案卷号
					String newArchivecode = parentArchivecode + newSunArch;
					entryIndexCaptureRepository.updateInnerfileAndArchivecode(innerfile1, newArchivecode, archivecodeOne,
							nodeid);
				}
			}
		}
	}

	/**
	 * 更新案卷的文件总数和总页数
	 */
	public void updateAjuFile(String archivecode, String innerfile, int jnadd, String zjPages, String nodeid,
			String entryid) {
		// 1更新该卷条目的文件数和总页数
		// 根据archivecode和innerfile去截取母卷的archivecode
		if (innerfile != null) {
			int index=archivecode.lastIndexOf(innerfile);
			if (index<0) {
				return;
			}// 先判断有没有母卷，没有就直接退出
			List<Tb_entry_index_capture> teics = entryIndexCaptureRepository
					.findByArchivecode(archivecode.substring(0, index - 1));
			String archiveTemp = archivecode.substring(0, index);// 母卷加分割符（分割符长度1）
			if (teics.size() < 1) {
				return;
			}
			String archivecodeMj = archivecode.substring(0, archiveTemp.length() - 1);
			// 根据档号去查找所有的子件集合，然后再去修改母卷的文件数和总页数
			List<Tb_entry_index_capture> zjList = entryIndexCaptureRepository
					.findAllByArchivecodeLikeAndNodeidOrderByArchivecode(archiveTemp, nodeid);
			// Tb_entry_detail f02文件总数 Tb_entry_index pages 总页数
			String fileSize = zjList.size() + "";
			String pages = "0";
			int pageNum = 0;
			if (zjList.size() > 0) {
				for (int i = 0; i < zjList.size(); i++) {
					String pageS = zjList.get(i).getPages();// 页数
					String pageN = zjList.get(i).getPageno();// 页号
					String zjEntryid = zjList.get(i).getEntryid();
					String zjInnerfile = zjList.get(i).getInnerfile();
					// 顺便修改子件的页号
					int pno = 0;
					try {
						pno = Integer.valueOf(zjPages);
						if (pno > 0) {// 拆插件页数>0才执行
							if (zjInnerfile.compareTo(innerfile) > 0 || entryid.equals(zjEntryid)) {
								int zjpno = Integer.valueOf(pageN);
								// 1.插件，后边的页号都加一个插件页数
								if (jnadd == 1) {
									pageN = zjpno + pno + "";
								}
								// 2.拆件，后边的页号都减一个拆件页数
								if (jnadd == 0) {
									if (zjpno > pno) {
										pageN = zjpno - pno + "";
									} else {
										pageN = "";// 页号小于拆插件页数，设置页号为空
									}
								}
							}

						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					// String zjPageNo=pageNum+1+"";
					entryIndexCaptureRepository.updatePagenoByEntryid(zjEntryid, pageN);
					try {
						pageNum += Integer.parseInt(pageS);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				pages = pageNum + "";
			}
			String filenodeid = publicUtilService.getFileNodeid(nodeid);
			// 更新总页数
			entryIndexCaptureRepository.updatePagesByArchivecode(archivecodeMj, pages, filenodeid);
			// 更新文件总数
			entryDetailCaptureRepository.updateFileSizeByArchivecode(archivecodeMj, fileSize, filenodeid);
		}
	}

	/**
	 * 案件插件排序
	 */
	public void updateAjiSquChji(String archivecode, String recordcode, String nodeid) {
		// 先截取档号前端
		String parentArchivecode = archivecode.substring(0, archivecode.lastIndexOf(recordcode));
		// 根据案卷号获取后边的相应件号的案卷(排除卷内文件)
		List<Tb_entry_index_capture> ajNextList = entryIndexCaptureRepository
				.findAllByArchivecodeLikeAndNextRecord(parentArchivecode, recordcode, nodeid);
		if (ajNextList.size() > 0) {
			for (int i = ajNextList.size() - 1; i >= 0; i--) {
				// 插件后，后边的案卷号全部加1
				String recordcode0 = ajNextList.get(i).getRecordcode();
				String archivecode0 = ajNextList.get(i).getArchivecode();
				String recordcode1 = "";
				int num = recordcode0.length();
				try {
					recordcode1 = Integer.parseInt(recordcode0) + 1 + "";
				} catch (Exception e) {
					e.printStackTrace();
				}
				int newNum = recordcode1.length();
				// 重新拼接recordcode
				if (recordcode1.length() < num) {
					for (int j = 0; j < num - newNum; j++) {
						recordcode1 = 0 + recordcode1;
					}
				}
				int pNum = parentArchivecode.length();
				String subArch = archivecode0.substring(pNum, archivecode0.length());// 获取件号
				String newSunArch = subArch.replaceFirst(recordcode0, recordcode1);// 新件号
				String newArchivecode = parentArchivecode + newSunArch;
				entryIndexCaptureRepository.updateRecordcodeAndArchivecode(recordcode1, newArchivecode, archivecode0,
						nodeid);
			}
		}
	}

	/**
	 * 卷内文件案件插件排序
	 */
	public void updateJnAjiSquChji(String archivecode, String innerfile, String nodeid) {
		// 先截取档号前端
		String parentArchivecode = archivecode.substring(0, archivecode.lastIndexOf(innerfile));
		// 根据案卷号获取后边的相应件号的案卷(排除卷内文件)
		List<Tb_entry_index_capture> ajNextList = entryIndexCaptureRepository
				.findInnerByArchivecodeLikeAndNext(parentArchivecode, innerfile, nodeid);
		if (ajNextList.size() > 0) {
			for (int i = ajNextList.size() - 1; i >= 0; i--) {
				// 插件后，后边的案卷号全部+1
				String innerfile0 = ajNextList.get(i).getInnerfile();
				String archivecode0 = ajNextList.get(i).getArchivecode();
				String innerfile1 = "";
				int num = innerfile0.length();
				try {
					innerfile1 = Integer.parseInt(innerfile0) + 1 + "";
				} catch (Exception e) {
					e.printStackTrace();
				}
				int newNum = innerfile1.length();
				// 重新拼接recordcode
				if (innerfile1.length() < num) {
					for (int j = 0; j < num - newNum; j++) {
						innerfile1 = 0 + innerfile1;
					}
				}
				int pNum = parentArchivecode.length();
				String subArch = archivecode0.substring(pNum, archivecode0.length());// 获取件号
				String newSunArch = subArch.replaceFirst(innerfile0, innerfile1);// 新件号
				String newArchivecode = parentArchivecode + newSunArch;
				entryIndexCaptureRepository.updateInnerfileAndArchivecode(innerfile1, newArchivecode, archivecode0,
						nodeid);
			}
		}
	}

	/**
	 * 档号更新
	 */
	public String getArchivecode(String oldArch, Integer pNum, String oldCode, String newCode, String parentArch) {
		String subArch = oldArch.substring(pNum, oldArch.length());// 获取案卷号和卷内号
		String newSunArch = subArch.replaceFirst(oldCode, newCode);// 新案卷号
		return parentArch + newSunArch;
	}


	public int setPagesbyEntryid(String entryid,String pages){
		return entryIndexCaptureRepository.updatePagesByEntryid(pages,entryid);
	}

	/**
	 * 根据电子文件id获取电子文件历史版本
	 */
	public Page<Tb_electronic_version_capture> getElectronicVersion(String eleid,int page,int limit,Sort sort) {
		PageRequest pageRequest = new PageRequest(page - 1, limit, sort == null ? new Sort(Sort.Direction
				.DESC, "createtime") : sort);
		return electronicVersionCaptureRepository.findByEleid(eleid,pageRequest);
	}

	/**
	 * 根据电子文件版本id删除电子文件历史版本
	 */
	public int delElectronicVersion(String[] eleVersions) {
		List<Tb_electronic_version_capture> electronic_versions = electronicVersionCaptureRepository.findByIdIn(eleVersions);
		for(int i=0;i<electronic_versions.size();i++){
			File targetFile = new File(rootpath + electronic_versions.get(i).getFilepath(), electronic_versions.get(i).getFilename());
			targetFile.delete();
		}
		return electronicVersionCaptureRepository.deleteByIdIn(eleVersions);
	}

	/**
	 * 根据电子文件版本id回滚到此版本
	 */
	public void rebackElectronicVersion(String eleVersionid) {
		Tb_electronic_version_capture electronic_version = electronicVersionCaptureRepository.findById(eleVersionid);
		Tb_electronic_capture electronic = electronicCaptureRepository.findByEleid(electronic_version.getEleid());
		try {
			// 获取原来电子文件
			File oldFile = new File(rootpath + electronic.getFilepath(), electronic.getFilename());
			oldFile.delete();

			// 目标电子文件
			File targetFile = new File(rootpath + electronic.getFilepath(), electronic_version.getFilename());
			// 获取回滚版本的电子文件
			File newFile = new File(rootpath + electronic_version.getFilepath(), electronic_version.getFilename());

			FileUtils.copyFile(newFile, targetFile);

			electronic.setFilename(electronic_version.getFilename());
			electronic.setFilesize(String.valueOf(newFile.length()));
			electronic.setFiletype(electronic_version.getFiletype());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<Tb_electronic_version_capture> getEleVersionByids(String[] eleVersionids) {
		return electronicVersionCaptureRepository.findByIdIn(eleVersionids);
	}

	public Tb_electronic_version_capture getEleVersionByid(String eleVersionid) {
		return electronicVersionCaptureRepository.findById(eleVersionid);
	}

	/**
	 * 压缩文件
	 *
	 * @return
	 */
	public String transFiles(String[] eleVersionids) throws IOException {
		//定义下载压缩包名称
		String zipname = "EleVersion" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());

		//文件复制
		List<Tb_electronic_version_capture> electronic_versions = getEleVersionByids(eleVersionids);
		String desPath = null;
		for (Tb_electronic_version_capture ele_Version : electronic_versions) {
			String selectionFilename = ele_Version.getFilename();
			String selectionFilepath = rootpath + ele_Version.getFilepath();
			desPath = selectionFilepath.split(ele_Version.getVersion())[0] + "originalElectronic/" + new SimpleDateFormat("yyyy/M/d").format(new Date());
			desPath += File.separator + zipname;
			File srcFile = new File(selectionFilepath + File.separator + selectionFilename);
			File desFile = new File(desPath + File.separator + selectionFilename);
			FileUtils.copyFile(srcFile, desFile);
		}

		//文件压缩
		String transFilepath = desPath;//.substring(0,desPath.lastIndexOf(File.separator));//创建中转文件夹
		ZipUtil.zip(transFilepath.replaceAll("/", "\\\\"), transFilepath.replaceAll("/", "\\\\") + ".zip", "");//压缩
		String zipPath = transFilepath.replace("/", "\\") + ".zip";
		delFolder(transFilepath);
		return zipPath;
	}

	public List<Tb_index_detail_capture> getInnerfiles(String nodeid, Tb_entry_index_capture tb_entry_index_capture){
		// 获取案卷档号设置字段集合
		List<String> ajCodeSettingFieldList = codesettingService
				.getCodeSettingFields(tb_entry_index_capture.getNodeid());
		// 档号设置字段值集合
		List<String> codeSettingFieldValues = new ArrayList<>();
		for (int i = 0; i < ajCodeSettingFieldList.size(); i++) {
			String codeSettingFieldValue = GainField.getFieldValueByName(ajCodeSettingFieldList.get(i),
					tb_entry_index_capture) + "";
			if (!"null".equals(codeSettingFieldValue) && !"".equals(codeSettingFieldValue)) {
				codeSettingFieldValues.add(codeSettingFieldValue);
			} else {
				codeSettingFieldValues.add("");
			}
		}
		// 获取卷内档号设置字段集合
		List<String> jnCodeSettingFieldList = codesettingService.getCodeSettingFields(nodeid);
		String searchCondition = entryIndexService.getJNSearchCondition(ajCodeSettingFieldList, codeSettingFieldValues,
				nodeid,
				jnCodeSettingFieldList.size() > 0 ? jnCodeSettingFieldList.get(jnCodeSettingFieldList.size() - 1) : "");
		List list = new ArrayList();
		// 返回的条件语句如果是空字符串，则返回空数据回前端
		if ("".equals(searchCondition)) {
			list.add(0);
			list.add(new ArrayList<Tb_index_detail_capture>());
			return list;
		}
		String sql = "select sid.* from v_index_detail_capture sid where " + searchCondition;
		Query query = entityManager.createNativeQuery(sql, Tb_index_detail_capture.class);
		return query.getResultList();
	}

	public List<Tb_index_detail_capture> getLongInnerfiles(String nodeid, Tb_entry_index tb_entry_index,
														   List<String> ajCodeSettingFieldList,List<String> jnCodeSettingFieldList){
		// 获取案卷档号设置字段集合
//		List<String> ajCodeSettingFieldList = codesettingService
//				.getCodeSettingFields(tb_entry_index_capture.getNodeid());
		// 档号设置字段值集合
		List<String> codeSettingFieldValues = new ArrayList<>();
		for (int i = 0; i < ajCodeSettingFieldList.size(); i++) {
			String codeSettingFieldValue = GainField.getFieldValueByName(ajCodeSettingFieldList.get(i),
					tb_entry_index) + "";
			if (!"null".equals(codeSettingFieldValue) && !"".equals(codeSettingFieldValue)) {
				codeSettingFieldValues.add(codeSettingFieldValue);
			} else {
				codeSettingFieldValues.add("");
			}
		}
		// 获取卷内档号设置字段集合
//		List<String> jnCodeSettingFieldList = codesettingService.getCodeSettingFields(nodeid);
		String searchCondition = entryIndexService.getJNSearchCondition(ajCodeSettingFieldList, codeSettingFieldValues, nodeid,
				jnCodeSettingFieldList.size() > 0 ? jnCodeSettingFieldList.get(jnCodeSettingFieldList.size() - 1) : "");
		List list = new ArrayList();
		// 返回的条件语句如果是空字符串，则返回空数据回前端
		if ("".equals(searchCondition)) {
			return new ArrayList<Tb_index_detail_capture>();
		}
		String shCondition=" and sid.entryid not in(select entryid from tb_transdoc_entry where status='"+Tb_transdoc_entry.STATUS_AUDIT+"')";
		String sql = "select sid.* from v_index_detail_capture sid where " + searchCondition +shCondition;
		Query query = entityManager.createNativeQuery(sql, Tb_index_detail_capture.class);
		return query.getResultList();
	}


	public Page<Tb_entry_detail_capture> getEntrybaseto(String[] nodeids, String condition, String operator, String content,
												int page, int limit, Sort sort) {
		String nodeidStr = "";
		if (nodeids.length > 0) {
			nodeidStr = " and nodeid in ('" + String.join("','", nodeids) + "') ";
		}

		PageRequest pageRequest = new PageRequest(page - 1, limit);
		String sortStr = "";//排序
		int sortInt = 0;//判断是否副表表排序
		if (sort != null && sort.iterator().hasNext()) {
			Sort.Order order = sort.iterator().next();
			if("eleid".equals(order.getProperty())){
				sortStr = " order by " + DBCompatible.getInstance().getNullSort(order.getProperty()) + " " + order.getDirection();
			}else {
				sortStr = " order by " + order.getProperty() + " " + order.getDirection();
			}
			sortInt = checkFilecode(order.getProperty());
		} else {
			sortStr = " order by archivecode desc, descriptiondate desc ";
		}

		String searchCondition = "";
		if (content != null) {// 输入框检索
			//元数据字段转换成对应关联的字段
			String mid = metadataTemplateRepository.findTemplateidByNodeidAndFieldcode(nodeids[0],condition);
			String fcod = templateRepository.findFieldcodeByMetadataid(mid);
			searchCondition = classifySearchService.getSqlByConditionsto(fcod, content, "sid", operator);
		}

		String table = "tb_entry_detail_capture";
		String countTable = "tb_entry_detail_capture";
		if (condition == null || checkFilecode(condition) == 0) {//没副表字段的检索,查总数60W+用tb_entry_index会快8s+
			countTable = "tb_entry_index_capture";
			if (sortInt == 0) {//非副表表字段排序
				table = "tb_entry_index_capture";
			}
		}
//		String openStr = simpleSearchService.getSearchOpenStr("原文开放,条目开放");//利用平台-权限档案显示开放的档案数据
		String openStr = "";
		String sql = "select sid.entryid from " + table + " sid where 1=1 " + nodeidStr + searchCondition + openStr;
		String countSql = "select count(nodeid) from " + countTable + " sid where 1=1 " + nodeidStr + searchCondition+ openStr;
		return getPageListTwo(sql, sortStr, countSql, page, limit, pageRequest,table);
	}

	public Page<Tb_index_detail_capture> getEntryCapyures(String checkAll, String entryids,String nodeid, String condition, String operator, String content,
														int page, int limit, Sort sort,String volumeNodeId,List<String> volumeEntryIds,
													    String transforType) {
		if(entryids==null){
			entryids=",";
		}
		String[] entryInfo=entryids.split(",");
		String nodeidStr = " and nodeid ='" + nodeid + "' ";
		if(volumeNodeId!=null&&!"".equals(volumeNodeId)){//判断是否为案卷
			nodeidStr = " and nodeid in('" + nodeid + "','"+volumeNodeId+"') ";
		}
		String[] entryidData;
		if ("true".equals(checkAll)) {//所有记录
			List<Tb_entry_index> entry_indexList;
			if("1".equals(transforType)) {//判断处理移交还是直接移交 2 直接移交 1 处理移交
				entry_indexList = entryIndexService.getDocPreviewEntry(nodeid, condition, operator, content);
			}else {
				entry_indexList = entryIndexService.getLognIndexCapture(nodeid, condition, operator, content);
			}
			if (entry_indexList.size() > 0) {
				List<String> tempEntry = new ArrayList<>();
				for (int i = 0; i < entry_indexList.size(); i++) {
					String entryid = entry_indexList.get(i).getEntryid();
					if (!entryids.contains(entryid.trim())) {
						tempEntry.add(entryid);
					}
				}
				entryidData = tempEntry.toArray(new String[tempEntry.size()]);
			} else {
				entryidData = new String[]{};
			}
		} else {
			entryidData = entryInfo;
		}
		String entryidStr="";
		if(entryidData.length!=0) {
			entryidStr+=" and (sid.entryid in ('" + String.join("','", entryidData) + "')) ";//数据
		}
		if(volumeEntryIds.size()>0) {
			entryidStr += " or sid.entryid in ('" + String.join("','", volumeEntryIds) + "'))";//卷内数据
		}else{
			entryidStr+=") ";
		}
		PageRequest pageRequest = new PageRequest(page - 1, limit);
		String sortStr = "";//排序
		int sortInt = 0;//判断是否副表表排序
		if (sort != null && sort.iterator().hasNext()) {
			Sort.Order order = sort.iterator().next();
			if("eleid".equals(order.getProperty())){
				sortStr = " order by " + DBCompatible.getInstance().getNullSort(order.getProperty()) + " " + order.getDirection();
			}else {
				sortStr = " order by " + order.getProperty() + " " + order.getDirection();
			}
			sortInt = checkFilecode(order.getProperty());
		} else {
			sortStr = " order by lr.checkstatus desc,archivecode desc ";
		}
		String searchCondition = "and ( 1=1 ";
		if (content != null&&!"".equals(operator)) {// 输入框检索
			searchCondition += classifySearchService.getSqlByConditionsto(condition, content, "sid", operator);
		}
		//审核筛选
		String shCondition=" and sid.entryid not in(select entryid from tb_transdoc_entry where status='"+Tb_transdoc_entry.STATUS_AUDIT+"')";
		String table = "tb_index_detail_capture";
		String countTable = "tb_index_detail_capture";
		if (condition == null || checkFilecode(condition) == 0) {//没副表字段的检索,查总数60W+用tb_entry_index会快8s+
			countTable = "tb_entry_index_capture";
			if (sortInt == 0) {//非副表表字段排序
				table = "tb_entry_index_capture";
			}
		}
		String sql = "select sid.entryid from " + table + " sid inner join tb_long_retention lr on sid.entryid=lr.entryid where 1=1 " + nodeidStr + searchCondition+entryidStr+shCondition;
		String countSql = "select count(nodeid) from " + countTable + " sid where 1=1 " + nodeidStr + searchCondition+entryidStr+shCondition;
		Query couuntQuery = entityManager.createNativeQuery(countSql);
		int count = Integer.parseInt(couuntQuery.getResultList().get(0) + "");
		sql = "select tt.* from v_index_detail_capture tt " +
				"inner join (" + DBCompatible.getInstance().sqlPages(sql+sortStr, page - 1, limit) +
				")t on t.entryid = tt.entryid";
		Query query = entityManager.createNativeQuery(sql, Tb_index_detail_capture.class);
		List<Tb_index_detail_capture> resultList=query.getResultList();
		return new PageImpl(resultList, pageRequest, count);
	}

	public Page<Tb_index_detail_capture> getEntryCapyureByDocid(String docid, String condition, String operator, String content,
														  int page, int limit, Sort sort) {
		PageRequest pageRequest = new PageRequest(page - 1, limit);
		String sortStr = "";//排序
		int sortInt = 0;//判断是否副表表排序
		if (sort != null && sort.iterator().hasNext()) {
			Sort.Order order = sort.iterator().next();
			if("eleid".equals(order.getProperty())){
				sortStr = " order by " + DBCompatible.getInstance().getNullSort(order.getProperty()) + " " + order.getDirection();
			}else {
				sortStr = " order by " + order.getProperty() + " " + order.getDirection();
			}
			sortInt = checkFilecode(order.getProperty());
		} else {
			sortStr = " order by archivecode desc, descriptiondate desc ";
		}

		String searchCondition = "";
		if (content != null) {// 输入框检索
			searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "sid", operator);
		}
		//根据docid筛选
		String shCondition=" and entryid in(select entryid from tb_transdoc_entry where docid='"+docid+"')";
		String table = "tb_index_detail_capture";
		String countTable = "tb_index_detail_capture";
		if (condition == null || checkFilecode(condition) == 0) {//没副表字段的检索,查总数60W+用tb_entry_index会快8s+
			countTable = "tb_entry_index_capture";
			if (sortInt == 0) {//非副表表字段排序
				table = "tb_entry_index_capture";
			}
		}
		String sql = "select sid.entryid from " + table + " sid where 1=1 " + searchCondition+ shCondition;
		String countSql = "select count(nodeid) from " + countTable + " sid where 1=1 " + searchCondition+ shCondition;
		Query couuntQuery = entityManager.createNativeQuery(countSql);
		int count = Integer.parseInt(couuntQuery.getResultList().get(0) + "");
		sql = "select tt.* from v_index_detail_capture tt  inner join (" + DBCompatible.getInstance().sqlPages(sql+sortStr, page - 1, limit) + ")t on t.entryid = tt.entryid ";
		Query query = entityManager.createNativeQuery(sql, Tb_index_detail_capture.class);
		List<Tb_index_detail_capture> resultList=query.getResultList();
		return new PageImpl(resultList, pageRequest, count);
	}

	public List<Tb_entry_index_capture> findInnerByArchivecodeLike(String entryid){
		Tb_entry_index_capture tb_entry_index_capture=entryIndexCaptureRepository.findByEntryid(entryid);
		if(tb_entry_index_capture==null){
			return new ArrayList<>();
		}
		return entryIndexCaptureRepository.findInnerByArchivecodeLike(tb_entry_index_capture.getArchivecode()+"-");
	}
	//Tb_index_detail sql原生语句分页查询
	public Page<Tb_entry_detail_capture> getPageListTwo(String sql, String sortStr, String countSql, int page, int limit, PageRequest pageRequest,String tablename) {
		Query couuntQuery = entityManager.createNativeQuery(countSql);
		int count = Integer.parseInt(couuntQuery.getResultList().get(0) + "");
		List<Tb_index_detail> resultList;
		if (count > 1000) {
			sql = "select tt.* from "+tablename+" tt  inner join (" + DBCompatible.getInstance().sqlPages(sql+sortStr, page - 1, limit) + ")t on t.entryid = tt.entryid ";
			Query query = null;
			if("tb_entry_detail_capture".equals(tablename)) {
				query = entityManager.createNativeQuery(sql, Tb_entry_detail_capture.class);
			}else {
				query = entityManager.createNativeQuery(sql, Tb_entry_index_capture.class);
			}
			resultList = query.getResultList();
		} else {
			sql = "select tt.* from "+tablename+" tt  inner join (" + sql + ")t on t.entryid = tt.entryid " + sortStr;
			Query query = null;
			if("tb_entry_detail_capture".equals(tablename)) {
				query = entityManager.createNativeQuery(sql, Tb_entry_detail_capture.class);
			}else {
				query = entityManager.createNativeQuery(sql, Tb_entry_index_capture.class);
			}
			query.setFirstResult((page - 1) * limit);
			query.setMaxResults(limit);
			resultList = query.getResultList();
		}
		return new PageImpl(resultList, pageRequest, count);
	}

	//设置排序
	public void setSortSequence(String[] sortStr,String nodeid) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
		userNodeSortRepository.deleteByUseridAndAndNodeid(userDetails.getUserid(),nodeid);  //删除之前设置排序
		List<Tb_user_node_sort> nodeSorts = new ArrayList<>();
		if(sortStr!=null){
			for(int i=0;i<sortStr.length;i++){
				Tb_user_node_sort userNodeSort = new Tb_user_node_sort();
				String[] fieldStr = sortStr[i].split("_");
				if("升序".equals(fieldStr[2])){
					userNodeSort.setSorttype("asc");
				}else{
					userNodeSort.setSorttype("desc");
				}
				userNodeSort.setFieldcode(fieldStr[0]);
				userNodeSort.setFieldname(fieldStr[1]);
				userNodeSort.setSortsequence(i+1);
				userNodeSort.setUserid(userDetails.getUserid());
				userNodeSort.setNodeid(nodeid);
				nodeSorts.add(userNodeSort);
			}
			userNodeSortRepository.save(nodeSorts);
		}
	}

	//获取节点排序设置
	public List<Tb_user_node_sort> getUserNodeSort(String nodeid) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
		return userNodeSortRepository.findByNodeidAndUseridOrderBySortsequence(nodeid,userDetails.getUserid());
	}

	// 根据案卷、卷内的档号组成字段做匹配，获取案卷以及卷内条目
	public Page<Tb_index_detail_capture> getEntrysByids(int page, int limit, String condition, String operator, String content,
														String[] entryidData, Sort sort) {
		PageRequest pageRequest = new PageRequest(page - 1, limit);
		String entryidStr = " and entryid in('" + String.join("','", entryidData) + "') ";
		String searchCondition = "";
		if (content != null && !"".equals(content.trim())) {//输入框检索
			searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "sid", operator);
		}
		String sql = "select sid.* from v_index_detail_capture sid where 1 = 1 " + entryidStr + searchCondition;
		String countSql = "select count(nodeid) from v_index_detail_capture sid where 1 = 1 " + entryidStr + searchCondition;
		String sortstr ="";
		sortstr = " order by archivecode asc";
		if (sort != null && sort.iterator().hasNext()) {
			Sort.Order order = sort.iterator().next();
			sortstr = " order by " + order.getProperty() + " " + order.getDirection();
		}
		sql = sql + sortstr;
		Query query = entityManager.createNativeQuery(sql, Tb_index_detail_capture.class);
		query.setFirstResult((page - 1) * limit);
		query.setMaxResults(limit);
		Query couuntQuery = entityManager.createNativeQuery(countSql);
		int count = Integer.parseInt(couuntQuery.getResultList().get(0) + "");
		List<Tb_index_detail_capture> captures = query.getResultList();
		return new PageImpl<Tb_index_detail_capture>(captures,pageRequest,count);
	}
}