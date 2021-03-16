package com.wisdom.web.service;

import com.wisdom.secondaryDataSource.entity.Tb_codeset_sx;
import com.wisdom.secondaryDataSource.repository.SecondaryDataNodeRepository;
import com.wisdom.secondaryDataSource.repository.SxCodesetRepository;
import com.wisdom.secondaryDataSource.repository.SxTemplateRepository;
import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.entity.Tb_codeset;
import com.wisdom.web.repository.CodesetRepository;
import com.wisdom.web.repository.TemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanly on 2017/11/3 0003.
 */
@Service
@Transactional
public class CodesettingService {

    @Autowired
    TemplateRepository templateRepository;

    @Autowired
    CodesetRepository codesetRepository;

    @Autowired
    SxCodesetRepository sxCodesetRepository;
    @Autowired
    SecondaryDataNodeRepository secondaryDataNodeRepository;
    @Autowired
    SxTemplateRepository sxTemplateRepository;

    public List<Tb_codeset> findCodesetByDatanodeid(String datanodeid){
        return codesetRepository.findByDatanodeidOrderByOrdernum(datanodeid);
    }

    public List<Tb_codeset_sx> findSxCodesetByDatanodeid(String datanodeid){
        return sxCodesetRepository.findByDatanodeidOrderByOrdernum(datanodeid);
    }

    public String[] getCodesetFieldcodeByNodeid(String datanodeid){
        List<Tb_codeset> codesetList = findCodesetByDatanodeid(datanodeid);
        List<String> codesetFieldcodeList = new ArrayList<>();
        for(Tb_codeset codeset:codesetList){
            codesetFieldcodeList.add(codeset.getFieldcode());
        }
        String[] codesetArr = new String[codesetFieldcodeList.size()];
        codesetFieldcodeList.toArray(codesetArr);
        return codesetArr;
    }

    public ExtMsg setCode(String datanodeid, String[] fieldcodelist){
        List<Tb_codeset> codesetList = new ArrayList<Tb_codeset>();
        for(int i=0; i<fieldcodelist.length;i++){
            String[] fieldcode_split=fieldcodelist[i].split("∪");
            Tb_codeset codeset=new Tb_codeset();
            if("".equals(fieldcode_split[0])){//add
                codeset.setOrdernum(i+1);
                codeset.setDatanodeid(datanodeid);
                codeset.setFieldcode(fieldcode_split[1]);
                codeset.setFieldname(fieldcode_split[2]);
                codeset.setSplitcode(fieldcode_split[3]);
                codeset.setFieldlength(Integer.parseInt(fieldcode_split[4]));
                codesetList.add(codeset);
            }else{
                codeset.setOrdernum(i+1);
                codeset.setDatanodeid(datanodeid);
                codeset.setFieldcode(fieldcode_split[4]);
                codeset.setFieldname(fieldcode_split[1]);
                codeset.setSplitcode(fieldcode_split[2]);
                codeset.setFieldlength(Integer.parseInt(fieldcode_split[3]));
                codesetList.add(codeset);
            }
        }
        String lastFieldcode = codesetList.get(codesetList.size()-1).getFieldcode();
        String lastFieldcodeType = templateRepository.findFtypeByFieldcodeAndNodeid(lastFieldcode,datanodeid);
        if(!"calculation".equals(lastFieldcodeType)){
            return new ExtMsg(false,"档号设置最后一个字段必须为统计型,请检查该字段模板设置是否正确",null);
        }
        List<Tb_codeset> oldList=codesetRepository.findByDatanodeidOrderByOrdernum(datanodeid);
        codesetRepository.delete(oldList); //删除节点下所有codeset
        codesetRepository.save(codesetList);
        return null;
    }

    @Transactional(value = "transactionManagerSecondary")
    public ExtMsg setSxCode(String datanodeid, String[] fieldcodelist,String filedtable){
        List<Tb_codeset_sx> codesetList = new ArrayList<Tb_codeset_sx>();
        for(int i=0; i<fieldcodelist.length;i++){
            String[] fieldcode_split=fieldcodelist[i].split("∪");
            Tb_codeset_sx codeset=new Tb_codeset_sx();
            if("".equals(fieldcode_split[0])){//add
                codeset.setOrdernum(i+1);
                codeset.setDatanodeid(datanodeid);
                codeset.setFieldcode(fieldcode_split[1]);
                codeset.setFieldname(fieldcode_split[2]);
                codeset.setSplitcode(fieldcode_split[3]);
                codeset.setFiledtable(filedtable);
                codeset.setFieldlength(Integer.parseInt(fieldcode_split[4]));
                codesetList.add(codeset);
            }else{
                codeset.setOrdernum(i+1);
                codeset.setDatanodeid(datanodeid);
                codeset.setFieldcode(fieldcode_split[4]);
                codeset.setFieldname(fieldcode_split[1]);
                codeset.setSplitcode(fieldcode_split[2]);
                codeset.setFiledtable(filedtable);
                codeset.setFieldlength(Integer.parseInt(fieldcode_split[3]));
                codesetList.add(codeset);
            }
        }
        String lastFieldcode = codesetList.get(codesetList.size()-1).getFieldcode();
        String lastFieldcodeType = sxTemplateRepository.findFtypeByFieldcodeAndNodeid(lastFieldcode,datanodeid)[0];
        if(!"calculation".equals(lastFieldcodeType)){
            return new ExtMsg(false,"档号设置最后一个字段必须为统计型,请检查该字段模板设置是否正确",null);
        }
        List<Tb_codeset_sx> oldList=sxCodesetRepository.findByDatanodeidAndFiledtableInOrderByOrdernum(datanodeid,new String[]{filedtable,""});
        oldList.addAll(sxCodesetRepository.findFieldlengthByDatanodeidfAndFiledtable(datanodeid,""));
        sxCodesetRepository.delete(oldList); //删除节点下所有codeset
        sxCodesetRepository.save(codesetList);
        return null;
    }

    /**
     *  获取档号设置字段名
     * @param nodeid 节点ID
     * @return
     */
    public List<String> getCodeSettingFields(String nodeid){
        return codesetRepository.findFieldcodeByDatanodeid(nodeid);
    }


    public List<Tb_codeset> findAllByDatanodeid(String nodeid){
        return codesetRepository.findAllByDatanodeid(nodeid);
    }
    /**
     *  获取档号设置分割符号
     * @param nodeid 节点ID
     * @return
     */
    public List<String> getCodeSettingSplitCodes(String nodeid){
        List<String> splitCodes = codesetRepository.findSplitcodeByDatanodeid(nodeid);
        if(splitCodes.size()>1){
            splitCodes.remove(splitCodes.size()-1);//最后一个移除
        }
        return splitCodes;
    }

    /**
     *  获取计算项单位长度
     * @param nodeid 节点ID
     * @return
     */
    public Integer getCalFieldLength(String nodeid){
        List<Object> fieldLengthList = codesetRepository.findFieldlengthByDatanodeid(nodeid);
        if(fieldLengthList.size()>0){
            Integer intvalue = Integer.parseInt(fieldLengthList.get(fieldLengthList.size()-1).toString());//计算项是最后一个
            return intvalue;
        }
        return null;
    }

    /**
     * 获取最后一项的单位长度
     * @param nodeid
     * @return
     */
    public Integer getLastCalFieldLength(String nodeid){
        List<Object> fieldLengthList = codesetRepository.findFieldlengthByDatanodeid(nodeid);
        if(fieldLengthList.size()>0){
            Integer intvalue = Integer.parseInt(fieldLengthList.get(fieldLengthList.size()-1).toString());
            return intvalue;
        }
        return null;
    }

    /**
     * 获取全宗号单位长度
     * @param nodeid
     * @return
     */
    public Integer getFundsFieldLength(String nodeid){
        return codesetRepository.findFieldlengthByDatanodeidAndFieldcode(nodeid,"funds");
    }

    /**
     * 获取案卷号单位长度
     * @param nodeid
     * @return
     */
    public Integer getFileFieldLength(String nodeid){
        return codesetRepository.findFieldlengthByDatanodeidAndFieldcode(nodeid,"file");
    }

    public void deleteCodesettingByNodeid(String nodeid){
        List<Tb_codeset> codesetList = codesetRepository.findByDatanodeidOrderByOrdernum(nodeid);
        codesetRepository.delete(codesetList);
    }

    @Transactional(value = "transactionManagerSecondary")
    public void deleteSxCodesettingByNodeid(String nodeid,String[] tableType){
        /*List<Tb_codeset_sx> codesetList = sxCodesetRepository.findByDatanodeidOrderByOrdernum(nodeid);
        sxCodesetRepository.delete(codesetList);*/
        sxCodesetRepository.deleteByDatanodeidAndTable(nodeid,tableType);
    }

    public List<Tb_codeset> SaveCodeset(List<Tb_codeset> codeset){
        return codesetRepository.save(codeset);
    }

    public List<Tb_codeset_sx> SaveSxCodeset(List<Tb_codeset_sx> codeset){
        return sxCodesetRepository.save(codeset);
    }

    public void deleteCodesetByNodeid(String nodeid){
        List<Tb_codeset> codesetList = codesetRepository.findByDatanodeidOrderByOrdernum(nodeid);
        codesetRepository.delete(codesetList);
    }

    //比较案卷和卷内的档号设置是否一致，除了卷内文件档号组成字段多出最后一个“卷内顺序号”外，其它都要一致(字段、顺序、分隔符、长度都要一致)
    public ExtMsg compareCodeset(String ajNodeid,String jnNodeid){
        List<Tb_codeset> ajCodesets = codesetRepository.findByDatanodeidOrderByOrdernum(ajNodeid);
        List<Tb_codeset> jnCodesets = codesetRepository.findByDatanodeidOrderByOrdernum(jnNodeid);
        if (ajCodesets.size() == 0 || jnCodesets.size() == 0) {
            return new ExtMsg(false, "当前档案类型的案卷和卷内档号无法匹配上，请检查档号设置信息是否正确！", null);
        } else {
            //卷内不比较最后一个，需要去掉
            jnCodesets.remove(jnCodesets.size() - 1);
            if (ajCodesets.size() != jnCodesets.size()) {
                return new ExtMsg(false, "当前档案类型的案卷和卷内档号无法匹配上，请检查档号设置信息是否正确！", null);
            } else {
                boolean compare = true;
                for (int i = 0; i < ajCodesets.size(); i++) {
                    Tb_codeset ajCodeset = ajCodesets.get(i);
                    Tb_codeset jnCodeset = jnCodesets.get(i);
                    //比较两个实体中字段名称、顺序、分隔符、长度是否一致
                    if (!ajCodeset.compareCodeset(jnCodeset)) {
                        compare = false;
                    }
                }
                if (compare) {
                    return new ExtMsg(true, "案卷和卷内的档号设置一致", null);
                } else {
                    return new ExtMsg(false, "当前档案类型的案卷和卷内档号无法匹配上，请检查档号设置信息是否正确！", null);
                }
            }
        }
    }

    //获取模拟档号
    public String getSimulationArchivecode(String nodeid){
        List<Tb_codeset> codesetList = codesetRepository.findByDatanodeidOrderByOrdernum(nodeid);
        String[] num =new String[]{" ","0","00","000","0000","00000","000000","0000000","000000000"};
        StringBuffer stringBuffer = new StringBuffer();
        //拼接模拟档号
        for (Tb_codeset tb_codeset : codesetList) {
            stringBuffer.append(num[(int)tb_codeset.getFieldlength()]).append(tb_codeset.getSplitcode());
        }
        stringBuffer.deleteCharAt(stringBuffer.length()-1); //删除末尾的字符
        return stringBuffer.toString();
    }
}
