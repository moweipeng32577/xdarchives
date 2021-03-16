package com.wisdom.web.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wisdom.util.SpecificationUtil;
import com.wisdom.web.entity.Tb_guidance_fileuser;
import com.wisdom.web.entity.Tb_guidance_leader;
import com.wisdom.web.entity.Tb_guidance_organ;
import com.wisdom.web.entity.Tb_guidance_safekeep;
import com.wisdom.web.entity.Tb_guidance_workfunds;
import com.wisdom.web.entity.Tb_guidance_workplan;
import com.wisdom.web.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2020/9/29.
 */
@Service
@Transactional
public class SupervisionGuidanceService {

    @Autowired
    GuidanceLeaderRepository guidanceLeaderRepository;

    @Autowired
    GuidanceOrganRepository guidanceOrganRepository;

    @Autowired
    GuidanceWorkfundsRepository guidanceWorkfundsRepository;

    @Autowired
    GuidanceWorkplanRepository guidanceWorkplanRepository;

    @Autowired
    GuidanceFileUserRepository guidanceFileUserRepository;

    @Autowired
    GuidanceSafeKeepRepository guidanceSafeKeepRepository;



    public List getSelectYear(){
        List<Tb_guidance_leader> returnList = new ArrayList<>();
        List<String> leaderList = guidanceLeaderRepository.getSelectYear();
        List<String> organList = guidanceOrganRepository.getSelectYear();
        List<String> workfundsList = guidanceWorkfundsRepository.getSelectYear();
        List<String> workplanList = guidanceWorkplanRepository.getSelectYear();
        List<String> fileuserList = guidanceFileUserRepository.getSelectYear();
        List<String> safekeepList = guidanceSafeKeepRepository.getSelectYear();
        leaderList.removeAll(organList);
        leaderList.addAll(organList);
        leaderList.removeAll(workfundsList);
        leaderList.addAll(workfundsList);
        leaderList.removeAll(workplanList);
        leaderList.addAll(workplanList);
        leaderList.removeAll(fileuserList);
        leaderList.addAll(fileuserList);
        leaderList.removeAll(safekeepList);
        leaderList.addAll(safekeepList);
        for(String selectyear : leaderList){
            Tb_guidance_leader leader = new Tb_guidance_leader();
            leader.setSelectyear(selectyear);
            returnList.add(leader);
        }
        return returnList;
    }

    public Page<Tb_guidance_leader> getGuidanceLeaders(String organid, String selectyear, int page, int limit, Sort sort){
        sort=sort==null?new Sort(Sort.Direction.DESC,"id"):sort;
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort);
        Specifications sp = Specifications.where(new SpecificationUtil("organid","equal",organid)).
                and(new SpecificationUtil("selectyear","equal",selectyear));
        return guidanceLeaderRepository.findAll(sp,pageRequest);
    }

    public Page<Tb_guidance_organ> getGuidanceOrgans(String organid, String selectyear, int page, int limit, Sort sort){
        sort=sort==null?new Sort(Sort.Direction.DESC,"id"):sort;
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort);
        Specifications sp = Specifications.where(new SpecificationUtil("organid","equal",organid)).
                and(new SpecificationUtil("selectyear","equal",selectyear));
        return guidanceOrganRepository.findAll(sp,pageRequest);
    }

    public Page<Tb_guidance_fileuser> getGuidanceFileUsers(String organid, String selectyear, int page, int limit, Sort sort){
        sort=sort==null?new Sort(Sort.Direction.DESC,"id"):sort;
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort);
        Specifications sp = Specifications.where(new SpecificationUtil("organid","equal",organid)).
                and(new SpecificationUtil("selectyear","equal",selectyear));
        return guidanceFileUserRepository.findAll(sp,pageRequest);
    }

    public Page<Tb_guidance_workfunds> getGuidanceWorkFundss(String organid, String selectyear, int page, int limit, Sort sort){
        sort=sort==null?new Sort(Sort.Direction.DESC,"id"):sort;
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort);
        Specifications sp = Specifications.where(new SpecificationUtil("organid","equal",organid)).
                and(new SpecificationUtil("selectyear","equal",selectyear));
        return guidanceWorkfundsRepository.findAll(sp,pageRequest);
    }

    public Page<Tb_guidance_workplan> getGuidanceWorkPlans(String organid, String selectyear, int page, int limit, Sort sort){
        sort=sort==null?new Sort(Sort.Direction.DESC,"id"):sort;
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort);
        Specifications sp = Specifications.where(new SpecificationUtil("organid","equal",organid)).
                and(new SpecificationUtil("selectyear","equal",selectyear));
        return guidanceWorkplanRepository.findAll(sp,pageRequest);
    }

    public void setGuidances(Tb_guidance_safekeep safekeep, String organid, String selectyear, String leaderData, String organData, String fileuserData, String workfundsData, String workplanData){
        Tb_guidance_safekeep tbGuidanceSafekeep = guidanceSafeKeepRepository.findByOrganidAndSelectyear(organid,selectyear);
        if(tbGuidanceSafekeep!=null&&(safekeep.getId()==null||"".equals(safekeep.getId()))){
            safekeep.setId(tbGuidanceSafekeep.getId());
        }
        guidanceSafeKeepRepository.save(safekeep);  //保存保管条件
        //保存数据
        if(leaderData!=null&&!"[]".equals(leaderData)){
            setGuidanceJson(leaderData,organid,selectyear,"leader");
        }
        if(organData!=null&&!"[]".equals(organData)){
            setGuidanceJson(organData,organid,selectyear,"organ");
        }
        if(fileuserData!=null&&!"[]".equals(fileuserData)){
            setGuidanceJson(fileuserData,organid,selectyear,"fileuser");
        }
        if(workfundsData!=null&&!"[]".equals(workfundsData)){
            setGuidanceJson(workfundsData,organid,selectyear,"workfunds");
        }
        if(workplanData!=null&&!"[]".equals(workplanData)){
            setGuidanceJson(workplanData,organid,selectyear,"workplan");
        }
    }

    public void setGuidanceJson(String objectData,String organid, String selectyear,String type){
        JSONArray json = (JSONArray) JSONArray.parse(objectData);
        if("leader".equals(type)){
            List<Tb_guidance_leader> returnList = new ArrayList<>();
            for (Object obj : json) {
                Tb_guidance_leader leader = new Tb_guidance_leader();
                JSONObject jo = (JSONObject)obj;
                leader.setSelectyear(selectyear);
                leader.setOrganid(organid);
                leader.setUsername(jo.getString("username"));
                leader.setPost(jo.getString("post"));
                leader.setPoliticstate(jo.getString("politicstate"));
                leader.setMobilephone(jo.getString("mobilephone"));
                leader.setStarttime(jo.getString("starttime"));
                returnList.add(leader);
            }
            guidanceLeaderRepository.save(returnList);
        }else if("organ".equals(type)){
            List<Tb_guidance_organ> returnList = new ArrayList<>();
            for (Object obj : json) {
                Tb_guidance_organ organ = new Tb_guidance_organ();
                JSONObject jo = (JSONObject)obj;
                organ.setSelectyear(selectyear);
                organ.setOrganid(organid);
                organ.setOrganname(jo.getString("organname"));
                organ.setClasstype(jo.getString("classtype"));
                organ.setIsindependent(jo.getString("isindependent"));
                organ.setUnderdepartment(jo.getString("underdepartment"));
                organ.setMobilephone(jo.getString("mobilephone"));
                organ.setUsername(jo.getString("username"));
                organ.setPost(jo.getString("post"));
                organ.setPoliticstate(jo.getString("politicstate"));
                organ.setMobilephone(jo.getString("mobilephone"));
                organ.setFulltimenum(jo.getInteger("fulltimenum"));
                organ.setParttimenum(jo.getInteger("parttimenum"));
                returnList.add(organ);
            }
            guidanceOrganRepository.save(returnList);
        }else if("fileuser".equals(type)){
            List<Tb_guidance_fileuser> returnList = new ArrayList<>();
            for (Object obj : json) {
                Tb_guidance_fileuser fileuser = new Tb_guidance_fileuser();
                JSONObject jo = (JSONObject)obj;
                fileuser.setSelectyear(selectyear);
                fileuser.setOrganid(organid);
                fileuser.setUsername(jo.getString("username"));
                fileuser.setSex(jo.getString("sex"));
                fileuser.setWorkno(jo.getString("workno"));
                fileuser.setOfficephone(jo.getString("officephone"));
                fileuser.setMobilephone(jo.getString("mobilephone"));
                fileuser.setIsfulltime(jo.getString("isfulltime"));
                fileuser.setWorkdate(jo.getString("workdate"));
                fileuser.setAduitdate(jo.getString("aduitdate"));
                returnList.add(fileuser);
            }
            guidanceFileUserRepository.save(returnList);
        }else if("workfunds".equals(type)){
            List<Tb_guidance_workfunds> returnList = new ArrayList<>();
            for (Object obj : json) {
                Tb_guidance_workfunds workfunds = new Tb_guidance_workfunds();
                JSONObject jo = (JSONObject)obj;
                workfunds.setSelectyear(selectyear);
                workfunds.setOrganid(organid);
                workfunds.setArchivesfunds(jo.getString("archivesfunds"));
                workfunds.setSituatuion(jo.getString("situatuion"));
                workfunds.setRemark(jo.getString("remark"));
                returnList.add(workfunds);
            }
            guidanceWorkfundsRepository.save(returnList);
        }else if("workplan".equals(type)){
            List<Tb_guidance_workplan> returnList = new ArrayList<>();
            for (Object obj : json) {
                Tb_guidance_workplan workplan = new Tb_guidance_workplan();
                JSONObject jo = (JSONObject)obj;
                workplan.setSelectyear(selectyear);
                workplan.setOrganid(organid);
                workplan.setIsyearplan(jo.getString("isyearplan"));
                workplan.setIsyearconclusion(jo.getString("isyearconclusion"));
                workplan.setIsyearaduit(jo.getString("isyearaduit"));
                workplan.setAttachment(jo.getString("attachment"));
                returnList.add(workplan);
            }
            guidanceWorkplanRepository.save(returnList);
        }
    }

    public void deleteSuperGuidanceByType(String[] ids,String type){
        if("leader".equals(type)){
            guidanceLeaderRepository.deleteByIdIn(ids);
        }else if("organ".equals(type)){
            guidanceOrganRepository.deleteByIdIn(ids);
        }else if("fileuser".equals(type)){
            guidanceFileUserRepository.deleteByIdIn(ids);
        }else if("workfunds".equals(type)){
            guidanceWorkfundsRepository.deleteByIdIn(ids);
        }else if("workplan".equals(type)){
            guidanceWorkplanRepository.deleteByIdIn(ids);
        }
    }
}
