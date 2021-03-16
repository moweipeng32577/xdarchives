package com.wisdom.web.service;

import com.wisdom.web.entity.Tb_fulltext;
import com.wisdom.web.repository.DataNodeRepository;
import com.wisdom.web.repository.FullTextRepository;
import com.wisdom.web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by tanly on 2017/11/17 0017.
 */
@Service
@Transactional
public class FullSearchService {
    @Autowired
    FullTextRepository fullTextRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DataNodeRepository dataNodeRepository;

    public Page<Tb_fulltext> getFulltextByFilter(String filters, int page, int limit){
        PageRequest pageRequest = new PageRequest(page-1,limit);
        return fullTextRepository.findByFilter(filters,pageRequest);
    }

    public StringBuffer getNodeidStringBuffer(String userid){
        String organid = userRepository.findOrganidByUserid(userid);// 获取当前用户机构id
        List<String> authList = dataNodeRepository.findNodeidsByOrganid(organid);  //获取机构对应的所有节点
        StringBuffer authIds = new StringBuffer("(");
        for (int i = 0; i < authList.size(); i++) {//前面请求已判断size不为0
            authIds.append("\"").append(authList.get(i).trim()).append("\"");
            if (i < authList.size() - 1) {
                authIds.append(",");
            } else {
                authIds.append(")");
            }
        }
        return authIds;
    }
}
