package com.wisdom.web.service;

import com.wisdom.web.entity.ExtDateRangeData;
import com.wisdom.web.entity.ExtOperators;
import com.wisdom.web.entity.Tb_entry_index;
import com.wisdom.web.repository.EntryIndexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by tanly on 2018/2/6 0006.
 */
@Service
@Transactional
public class DuplicationCheckingService {
    @Autowired
    EntryIndexRepository entryIndexRepository;

}
