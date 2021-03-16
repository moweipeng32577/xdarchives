package com.wisdom.web.repository;

import com.wisdom.web.entity.Szh_link_sign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrator on 2019/7/24.
 */
public interface SzhLinkSignRepository extends JpaRepository<Szh_link_sign, Integer>, JpaSpecificationExecutor<Szh_link_sign> {

    Integer deleteByIdInAndLink(String[] ids,String link);

    @Query(value = "select t.calloutid from Szh_link_sign t where userid =?1 and batchcode =?2 and link =?3")
    List<String> getByUseridAndAndBatchcodeAndAndLink(String userid, String batchcode, String link);

    @Query(value = "select t.calloutid from Szh_link_sign t where userid =?1 and link =?2 and assemblyid = ?3")
    List<String> getByUseridAndAndLink(String userid,String link,String assemblyid);

    @Query(value = "select t from Szh_link_sign t where calloutid =?1 and link =?2")
    Szh_link_sign getByCalloutidAndLink(String ids,String link);

    int deleteByCalloutidInAndLink(String[] calloutids,String link);

    int deleteByCalloutidInAndLinkIn(String[] calloutids,String[] links);

    Integer deleteByCalloutidIn(String[] calloutids);
}
