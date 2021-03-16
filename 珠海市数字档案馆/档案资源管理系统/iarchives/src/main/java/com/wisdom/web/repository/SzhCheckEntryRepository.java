package com.wisdom.web.repository;

import com.wisdom.web.entity.Szh_check_entry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Administrator on 2018/12/4.
 */
public interface SzhCheckEntryRepository extends JpaRepository<Szh_check_entry,String> {

    List<Szh_check_entry> findByCheckuserid(String checkuserid);

    Szh_check_entry findByBatchentryidAndAndState(String batchentry, String state);
}
