package com.wisdom.service.timejob.job;

import com.wisdom.web.entity.Tb_entry_index;
import com.wisdom.web.repository.EntryIndexRepository;
import com.wisdom.web.service.LongRetentionService;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * Created by yl on 2020/1/3.
 * 长期保管包定时任务
 */
public class LongRetentionJob implements InterruptableJob {
    @Autowired
    EntryIndexRepository entryIndexRepository;

    @Autowired
    LongRetentionService longRetentionService;

    //用于中断当前任务
    private boolean _interrupted = false;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        _interrupted = false;
        System.out.println(">>>>>>> 长期保管定时任务开始！");
        long count = entryIndexRepository.count();
        System.out.println(">>>>>>> 需要处理的总条目数为：" + count);
        int limit = 5000;
        long totalPages = count / limit + 1;
        for (int page = 1; page <= totalPages; page++) {
            System.out.println(">>>>>>> 长期保管打包开始！总页数：" + totalPages + ">>>当前第" + page + "页");
            //分页获取数据管理条目
            Page<Tb_entry_index> entryIndexList = entryIndexRepository.findAll(new PageRequest(page - 1, limit));
            System.out.println(">>>>>>> 查询到当前页的条目数为：" + entryIndexList.getContent().size());
            for (Tb_entry_index tb_entry_index : entryIndexList.getContent()) {
                if (_interrupted) {
                    return;
                }
                longRetentionService.longRetention(tb_entry_index.getEntryid(),"");
                System.out.println("题名为：>>>>>>>" + tb_entry_index.getTitle() + ">>>>>>>打包成长期保存包成功!");
        }
    }
    }

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        _interrupted = true;
    }
}
