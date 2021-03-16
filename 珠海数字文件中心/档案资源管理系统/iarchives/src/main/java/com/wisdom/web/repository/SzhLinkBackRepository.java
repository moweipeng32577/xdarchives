package com.wisdom.web.repository;

import com.wisdom.web.entity.Szh_link_back;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrator on 2019/7/24.
 */
public interface SzhLinkBackRepository extends JpaRepository<Szh_link_back,String>,JpaSpecificationExecutor<Szh_link_back> {

    List<Szh_link_back> findByCalloutidAndLinkOrderByBacktimeDesc(String id, String link);
    List<Szh_link_back> findByCalloutidAndStatusOrderByBacktimeDesc(String id,String status);
    List<Szh_link_back> findByCalloutidIn(String ids[]);
    List<Szh_link_back> findByCalloutidInAndStatusOrderByBacktimeDesc(String[] id,String status);
    List<Szh_link_back> findByCalloutidOrderByBacktimeDesc(String id);

    @Query("select t.link,t.status,t.backname,t.backtime,t.depict from Szh_link_back t where t.calloutid=?1 order by t.backtime desc ")
    List<Szh_link_back> findByEnriyid(String calloutid);

    Integer deleteByCalloutidIn(String[] calloutids);

    @Query("select t from Szh_link_back t where t.calloutid=?1 and status = '退回'")
    List<Szh_link_back> getLinkBack(String calloutid);
}
