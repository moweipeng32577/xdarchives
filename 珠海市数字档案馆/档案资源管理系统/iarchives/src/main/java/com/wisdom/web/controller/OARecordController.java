package com.wisdom.web.controller;



import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.entity.Tb_long_retention;
import com.wisdom.web.entity.Tb_oa_record;
import com.wisdom.web.entity.WebSort;
import com.wisdom.web.repository.LongRetentionRepository;
import com.wisdom.web.repository.OaRecordRepository;
import com.wisdom.web.service.ExportExcelService;
import com.wisdom.web.service.OARecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Map;

@Controller
@RequestMapping(value = "/OARecord")
public class OARecordController {

    @Autowired
    OARecordService oaRecordService;

    @Autowired
    ExportExcelService excelService;

    @Autowired
    OaRecordRepository oaRecordRepository;

    @Autowired
    LongRetentionRepository longRetentionRepository;

    @Value("${system.document.rootpath}")
    private String rootpath;//系统文件根目录
    /**
     * 根据条件获取日志数据
     * @param page 页码
     * @param limit 每页数
     * @param condition 查询字段
     * @param operator 查询方式
     * @param content 查询内容
     * @return
     */
    @RequestMapping("/findOARecordBySearch")
    @ResponseBody
    public Page<Tb_oa_record> findLogDetailBySearch(int page, int limit, String condition,
                                                    String operator, String content, String sort,String nodeid) {
        Sort sortobj = WebSort.getSortByJson(sort);
        Page<Tb_oa_record> oa_records = oaRecordService.findBySearch(page, limit, condition, operator, content, sortobj,nodeid);
        oa_records.getContent().stream().forEach(tb_oa_record -> {
            Tb_long_retention longRetention = longRetentionRepository.findByEntryid(tb_oa_record.getEntryid());
            if (longRetention != null) {
                tb_oa_record.setCheckstatus(longRetention.getCheckstatus());
                tb_oa_record.setAuthenticity(longRetention.getAuthenticity());
                tb_oa_record.setIntegrity(longRetention.getIntegrity());
                tb_oa_record.setUsability(longRetention.getUsability());
                tb_oa_record.setSafety(longRetention.getSafety());
            }
        });
        return oa_records;
    }

    @RequestMapping("/OARecord")
    @ResponseBody
    public ExtMsg OARecord(){
        //获取配置参数  链接ftp 获取文件  解压  导入
        Map<String,String> map = oaRecordService.oaimport();
        if(null!=map.get("erro")){
            return new ExtMsg(true, "接收成功", null);
        }
        return new ExtMsg(true, map.get("erro"), null);
    }

    @RequestMapping("/exportOA")
    @ResponseBody
    public void exportOA(HttpServletResponse response,String filepath)throws Exception{
        String loadPath = rootpath + File.separator +"OAFile" + File.separator + "OA接收"+File.separator+"OA导出"+File.separator;
        if (null!=filepath&&!"".equals(filepath)) {
            File file = new File(loadPath+filepath);
            String str = file.getName();
            String fileName = str.substring(0, str.indexOf("."));
            excelService.wirteFile(response, loadPath+filepath, fileName, "ok", true);
        }
    }

    @RequestMapping("/createOAZIP")
    @ResponseBody
    public Map createOAZIP(String ids,boolean selectAll)throws Exception{
       Map map = oaRecordService.createOA(ids,selectAll);
       return map;
    }
}
