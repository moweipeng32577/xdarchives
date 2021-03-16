package com.wisdom.web.repository;

import com.wisdom.web.entity.Szh_callout_capture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SzhCalloutCaptureRepository extends JpaRepository<Szh_callout_capture,String>,JpaSpecificationExecutor<Szh_callout_capture> {
    List<Szh_callout_capture> findByCalloutidIn(String[] calloutId);
    Szh_callout_capture findByCalloutid(String calloutId);
    @Query(value = "select entryid from Szh_callout_capture where calloutid in (?1)")
    String[] findEntryids(String[] calloutid);

    @Query(value = "select entryid from Szh_callout_capture where calloutid =?1")
    String findEntryid(String calloutid);
    Integer deleteByEntryidIn(String[] entryids);

    @Query(value = "select calloutid from Szh_callout_capture where entryid =?1")
    String findCallout(String entryid);
}
