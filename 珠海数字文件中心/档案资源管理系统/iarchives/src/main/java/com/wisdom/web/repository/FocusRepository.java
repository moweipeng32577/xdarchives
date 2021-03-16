package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_focus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Rong on 2017/10/31.
 */
public interface FocusRepository extends JpaRepository<Tb_focus,String> {

    Tb_focus findByFocusid(String id);

    List<Tb_focus> findByFocusidIn(String[] ids);

    Integer deleteByFocusidIn(String[] ids);

    List<Tb_focus> findByFocusidInOrderBySortsequence(String[] ids);
}
