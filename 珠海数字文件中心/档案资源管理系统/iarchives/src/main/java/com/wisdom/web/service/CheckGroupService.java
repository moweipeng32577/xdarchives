package com.wisdom.web.service;

import com.wisdom.web.entity.*;
import com.wisdom.web.repository.RightOrganRepository;
import com.wisdom.web.repository.SzhCheckGroupRepository;
import com.wisdom.web.repository.SzhCheckUserRepository;
import com.wisdom.web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/11/30.
 */
@Service
@Transactional
public class CheckGroupService {

    @Autowired
    SzhCheckGroupRepository szhCheckGroupRepository;

    @Autowired
    SzhCheckUserRepository szhCheckUserRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RightOrganRepository rightOrganRepository;

    public List<Szh_check_group> getCheckGroup(String type){
        return szhCheckGroupRepository.findByType(type);
    }

    /**
     * 新增质检组
     *
     * @param check_group 质检组实例
     * @return
     */
    public Szh_check_group userGroupAddSubmit(Szh_check_group check_group) {
        return szhCheckGroupRepository.save(check_group);
    }

    public Szh_check_group getCheckGroupForm(String checkgroupid){
        Szh_check_group check_group = szhCheckGroupRepository.getByCheckgroupid(checkgroupid);
        return check_group;
    }

    public int deletetCheckGroup(String[] checkgroupids){
        int count = 0;
        count = szhCheckGroupRepository.deleteByCheckgroupidIn(checkgroupids);
        szhCheckUserRepository.deleteByCheckgroupidIn(checkgroupids);
        return count;
    }

    public List<BackCheckUser> getCheckUser(String checkgroupid){
        List<Szh_check_user> check_users = szhCheckUserRepository.findByCheckgroupid(checkgroupid);
        Szh_check_group check_group = szhCheckGroupRepository.getByCheckgroupid(checkgroupid);
        List<BackCheckUser> backCheckUsers = new ArrayList<>();
        for(Szh_check_user check_user : check_users){
            BackCheckUser backCheckUser = new BackCheckUser();
            Tb_user user = userRepository.findByUserid(check_user.getUserid());
            Tb_right_organ organ = rightOrganRepository.findByOrganid(user.getOrganid());
            backCheckUser.setCheckuserid(check_user.getCheckuserid());
            backCheckUser.setLoginname(user.getLoginname());
            backCheckUser.setRealname(user.getRealname());
            backCheckUser.setSex(user.getSex());
            backCheckUser.setGroupname(check_group.getGroupname());
            backCheckUser.setOrganname(organ.getOrganname());
            backCheckUsers.add(backCheckUser);
        }
        return backCheckUsers;
    }

    public List<Tb_user> getUser(String checkgroupid){
        List<Szh_check_user> check_users = szhCheckUserRepository.findByCheckgroupid(checkgroupid);
        String[] userids = new String[check_users.size()];
        for(int i=0;i<check_users.size();i++){
            userids[i]=check_users.get(i).getUserid();
        }
        List<Tb_user> users = userRepository.findByUseridIn(userids);
        return users;
    }

    public List<Szh_check_user> setCheckUser(String checkgroupid,String[] userids){
        List<Szh_check_user> check_userList = new ArrayList<>();
        for(int i=0;i<userids.length;i++){
            Szh_check_user check_user = new Szh_check_user();
            check_user.setCheckgroupid(checkgroupid);
            check_user.setUserid(userids[i]);
            check_userList.add(check_user);
        }
        szhCheckUserRepository.deleteByCheckgroupid(checkgroupid);
        List<Szh_check_user> check_users = szhCheckUserRepository.save(check_userList);
        return check_users;
    }

    public int deletetCheckUser(String[] checkuserids){
        int count = 0;
        count = szhCheckUserRepository.deleteByCheckuseridIn(checkuserids);
        return count;
    }

    public List<Szh_check_group> getCheckGroupAll(){
        return szhCheckGroupRepository.findAll();
    }

}

