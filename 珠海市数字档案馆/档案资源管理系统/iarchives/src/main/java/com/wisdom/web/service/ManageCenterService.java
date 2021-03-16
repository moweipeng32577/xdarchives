package com.wisdom.web.service;

import com.wisdom.util.GainField;
import com.wisdom.util.SpecificationUtil;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2020/7/21.
 */
@Service
@Transactional
public class ManageCenterService {


    @Autowired
    RightOrganRepository rightOrganRepository;

    @Autowired
    EntryIndexCaptureRepository entryIndexCaptureRepository;

    @Autowired
    EntryIndexRepository entryIndexRepository;

    @Autowired
    TransdocEntryRepository transdocEntryRepository;

    @Autowired
    DataNodeRepository dataNodeRepository;

    @Autowired
    NodesettingService nodesettingService;

    @Autowired
    LogMsgRepository logMsgRepository;



    public Page<MediaEntry> getManageCenterData(int page, int limit) {
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        Specifications specifications = Specifications.where(new SpecificationUtil("organtype","equal","unit"));
        specifications = specifications.and(new SpecificationUtil("organname","notEqual","从化区立档单位"));
        Page<Tb_right_organ> organPage = rightOrganRepository.findAll(specifications, pageRequest);
        List<Tb_right_organ> organList = organPage.getContent();
        List<MediaEntry> returnList = new ArrayList<>();
        for(Tb_right_organ organ : organList){
            MediaEntry mediaEntry = new MediaEntry();
            mediaEntry.setEntryid(organ.getOrganid());
            mediaEntry.setTitle(organ.getOrganname());
            mediaEntry.setBackground("");
            returnList.add(mediaEntry);
        }
        return new PageImpl<MediaEntry>(returnList,pageRequest,organPage.getTotalElements());
    }

    public Page<ManageCenterTotal> getManageCenterUnitNum(int page, int limit) {
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        Specifications specifications = Specifications.where(new SpecificationUtil("organtype","equal","unit"));
        Page<Tb_right_organ> organPage = rightOrganRepository.findAll(specifications, pageRequest);
        List<Tb_right_organ> organList = organPage.getContent();
        List<ManageCenterTotal> returnList = new ArrayList<>();
        long transfernum = 0;
        long elefile = 0;
        long elearchive = 0;
        for(Tb_right_organ organ : organList){
            elefile = entryIndexCaptureRepository.getNumByNodeid(organ.getOrganid());
            elearchive = entryIndexRepository.getNumByNodeid(organ.getOrganid());
            transfernum = entryIndexRepository.getNumByNodeidAndEntryid(organ.getOrganid(),"已入库");
            ManageCenterTotal manageCenterTotal = new ManageCenterTotal();
            manageCenterTotal.setElefile(elefile);
            manageCenterTotal.setElearchive(elearchive);
            manageCenterTotal.setTransfernum(transfernum);
            manageCenterTotal.setUnit(organ.getOrganname());
            manageCenterTotal.setOrganid(organ.getOrganid());
            returnList.add(manageCenterTotal);
        }
        return new PageImpl<ManageCenterTotal>(returnList,pageRequest,organPage.getTotalElements());
    }

    public List<ManageCenterTotal> getManageCenterYearNum() {
        List<String> yearIndex = entryIndexRepository.getFiledates();
        List<String> yearCapture = entryIndexCaptureRepository.getFiledates();
        for(String yearindex : yearCapture){
            if(!yearIndex.contains(yearindex)){
                yearIndex.add(yearindex);
            }
        }
        List<ManageCenterTotal> returnList = new ArrayList<>();
        long transfernum = 0;
        long elefile = 0;
        long elearchive = 0;
        for (String year : yearIndex) {
            ManageCenterTotal manageCenterTotal = new ManageCenterTotal();
            if (year != null && year.length() == 4) {
                manageCenterTotal.setYear(year);
                elefile = entryIndexCaptureRepository.getNumByFiledateLike(year);
                elearchive = entryIndexRepository.getNumByFiledateLike(year);
                transfernum = entryIndexRepository.getNumByFiledateAndEntryidLike(year, "已入库");
            } else {
                if (year == null) {
                    manageCenterTotal.setYear("无年度");
                } else if ("".equals(year)) {
                    manageCenterTotal.setYear("");
                } else {
                    manageCenterTotal.setYear(year);
                }
                elefile = entryIndexCaptureRepository.getNumByFiledate(year);
                elearchive = entryIndexRepository.getNumByFiledate(year);
                transfernum = entryIndexRepository.getNumByFiledateAndEntryid(year, "已入库");
            }
            manageCenterTotal.setElefile(elefile);
            manageCenterTotal.setElearchive(elearchive);
            manageCenterTotal.setTransfernum(transfernum);
            returnList.add(manageCenterTotal);
        }
        return returnList;
    }

    public ManageCenterTotal getManageCenterTotal() {
        ManageCenterTotal manageCenterTotal = new ManageCenterTotal();
        long transfernum = 0;
        long elefile = 0;
        long elearchive = 0;
        long unfillingnum = 0;
        long fillingnum = 0;
        long receiveday = 0;
        long receivemonth = 0;
        String lastreceivetime = "";
        long fillingday = 0;
        long fillingmonth = 0;
        String lastfillingtime = "";
        elefile = entryIndexCaptureRepository.getAllCount();
        elearchive = entryIndexRepository.getAllCount();
        transfernum = transdocEntryRepository.getNumByState("已入库");
        Tb_data_node nofillmanage = dataNodeRepository.findByNodename("未归管理");
        //未归管理下的所有子节点
        List<String> fillmanageList = nodesettingService.getNodeidLoop(nofillmanage.getNodeid(), true, null);
        String[] infilemanageis = new String[fillmanageList.size()];
        fillmanageList.toArray(infilemanageis);
        unfillingnum = entryIndexCaptureRepository.getNumByNodeids(infilemanageis);
        fillingnum = elefile - unfillingnum;
        String datestr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        fillingday = logMsgRepository.getNumByDesciAndEndtime("归档操作",datestr);
        //获取第一天和最后一天
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.DAY_OF_MONTH, 1);
        String fristday = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime())+" 00:00:00";
        cal.roll(Calendar.DAY_OF_MONTH, -1);
        String lastday = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime())+" 23:59:59";
        fillingmonth = logMsgRepository.getNumByDesciAndStarEndtime("归档操作",fristday,lastday);
        lastfillingtime = logMsgRepository.getMaxEndtime("归档操作");
        //统计接收数目
        Tb_data_node wsFile = dataNodeRepository.findWSFile();  //未归-文书文件
        //未归-文书文件下的所有子节点
        List<String> wsfileList = nodesettingService.getNodeidLoop(wsFile.getNodeid(), true, null);
        String[] wsfileis = new String[wsfileList.size()];
        wsfileList.toArray(wsfileis);
        receiveday = entryIndexCaptureRepository.getNumByNodeidsAndDescriptiondate(wsfileis,datestr);
        receivemonth = entryIndexCaptureRepository.getNumByNodeidsAndDescriptiondateStarEnd(wsfileis,fristday,lastday);
        lastreceivetime = entryIndexCaptureRepository.getMaxDescriptiondate(wsfileis);
        manageCenterTotal.setElefile(elefile);
        manageCenterTotal.setElearchive(elearchive);
        manageCenterTotal.setTransfernum(transfernum);
        manageCenterTotal.setUnfillingnum(unfillingnum);
        manageCenterTotal.setFillingnum(fillingnum);
        manageCenterTotal.setReceiveday(receiveday);
        manageCenterTotal.setReceivemonth(receivemonth);
        manageCenterTotal.setLastreceivetime(lastreceivetime);
        manageCenterTotal.setFillingday(fillingday);
        manageCenterTotal.setFillingmonth(fillingmonth);
        manageCenterTotal.setLastfillingtime(lastfillingtime);
        return manageCenterTotal;
    }
}
