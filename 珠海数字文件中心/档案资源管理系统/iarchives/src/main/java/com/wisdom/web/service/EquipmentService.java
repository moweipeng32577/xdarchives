package com.wisdom.web.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wisdom.util.ReadExcel;
import com.wisdom.util.SpecificationUtil;
import com.wisdom.util.ValueUtil;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.ElectronicRepository;
import com.wisdom.web.repository.EquipmentRepository;
import com.wisdom.web.security.SecurityUser;
import org.apache.commons.io.FileUtils;
import org.codehaus.groovy.util.ListHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional
public class EquipmentService {
    @Value("${system.document.rootpath}")
    private String rootpath;

    private static long chunkSize = 5242880;// 文件分片大小5M

    @Autowired
    EquipmentRepository equipmentRepository;

    @Autowired
    ElectronicRepository electronicRepository;

    @Autowired
    LogService logService;

    private static Map<String, String> columnMap = new HashMap();

    static {
        columnMap.put("设备名称", "name");
        columnMap.put("设备类型", "type");
        columnMap.put("品牌", "brand");
        columnMap.put("型号", "model");
        columnMap.put("规格", "specifications");
        columnMap.put("单价", "price");
        columnMap.put("数量", "amount");
        columnMap.put("采购时间", "purchasetime");
        columnMap.put("到货验收时间", "acceptancetime");
        columnMap.put("所属部门", "organname");
        columnMap.put("ip地址", "ipaddress");
    }

    public Page<Tb_equipment> getEquipments(int page, int start, int limit, String condition, String operator, String content, String sort) {
        Sort sortobj = WebSort.getSortByJson(sort);
        Specifications specifications = null;
        if (content != null) {
            specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
        }
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(new Sort.Order(Sort.Direction.DESC,"purchasetime"));
        return equipmentRepository.findAll(specifications,new PageRequest(page - 1, limit, (Sort)(sortobj == null ? new Sort(sorts) : sortobj)));
    }

    private String getUploadDir() {
        String dir = "";
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        dir = rootpath + "/equipment/" + userDetails.getUsername();
        File upDir = new File(dir);
        if (!upDir.exists()) {
            upDir.mkdirs();
        }

        return dir;
    }

    public void uploadchunk(Map<String, Object> param) throws Exception {
        String tempFileName = param.get("filename") + "_tmp";
        File confFile = new File(getUploadDir(), param.get("filename") + ".conf");
        File tmpFile = new File(getUploadDir(), tempFileName);
        RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
        RandomAccessFile accessConfFile = new RandomAccessFile(confFile, "rw");

        long offset = chunkSize * Integer.parseInt((String) param.get("chunk"));
        // 定位到该分片的偏移量
        accessTmpFile.seek(offset);
        // 写入该分片数据
        accessTmpFile.write((byte[]) param.get("content"));

        // 把该分段标记为 true 表示完成
        accessConfFile.setLength(Integer.parseInt((String) param.get("chunks")));
        accessConfFile.seek(Integer.parseInt((String) param.get("chunk")));
        accessConfFile.write(Byte.MAX_VALUE);

        // completeList 检查是否全部完成,如果数组里是否全部都是(全部分片都成功上传)
        byte[] completeList = FileUtils.readFileToByteArray(confFile);
        byte isComplete = Byte.MAX_VALUE;
        for (int i = 0; i < completeList.length && isComplete == Byte.MAX_VALUE; i++) {
            // 与运算, 如果有部分没有完成则 isComplete 不是 Byte.MAX_VALUE
            isComplete = (byte) (isComplete & completeList[i]);
        }

        accessTmpFile.close();
        accessConfFile.close();
        // 上传完成，删除临时文件
        if (isComplete == Byte.MAX_VALUE) {
            confFile.delete();
            tmpFile.renameTo(new File(getUploadDir(), (String) param
                    .get("filename")));
        }
    }

    public void uploadfileInform(Map<String, Object> param) throws Exception {
        String targetFileName = (String) param.get("filename");
        File tmpFile = new File(getUploadDir(), targetFileName);
        RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
        // 写入数据
        accessTmpFile.write((byte[]) param.get("content"));
        accessTmpFile.close();
    }

    public Integer deleteElectronic(String eleids) {
        String[] eleidArray = eleids.split(",");
        List<Tb_electronic> electronics = electronicRepository.findByEleidInOrderBySortsequence(eleidArray);// 获取删除电子文件
        for (Tb_electronic electronic : electronics) {
            File file = new File(rootpath + electronic.getFilepath() + "/" + electronic.getFilename());
            file.delete();// 删除电子文件
        }
        return electronicRepository.deleteByEleidIn(eleidArray);
    }

    public Map<String, Object> saveElectronic(String informid, String filename) {
        File targetFile = new File(getUploadDir(), filename);
        Map<String, Object> map = new HashMap<>();
        Tb_electronic ele = new Tb_electronic();
        ele.setEntryid(informid == null ? "" : informid);
        ele.setFilename(filename);
        ele.setFilepath(getUploadDir().replace(rootpath, ""));
        ele.setFilesize(String.valueOf(targetFile.length()));
        ele.setFiletype(filename.substring(filename.lastIndexOf('.') + 1));
        ele = electronicRepository.save(ele);// 保存电子文件
        map = ele.getMap();
        return map;
    }

    public Tb_equipment addEquipment(String[] eleids,String equipmentID,String acceptancetime, String amount, String brand, String model, String type,
                                     String name, String price, String purchasetime, String remarks, String specifications,String ipAddress,String organname) {
        Tb_equipment equipment = new Tb_equipment();
        equipment.setAcceptancetime(acceptancetime);
        equipment.setAmount(Integer.parseInt(amount));
        equipment.setBrand(brand);
        equipment.setModel(model);
        equipment.setName(name);
        equipment.setRemarks(remarks);
        equipment.setSpecifications(specifications);
        equipment.setType(type);
        equipment.setPurchasetime(purchasetime);
        if(price!=null&&!"".equals(price)){
            equipment.setPrice(new BigDecimal(price));
        }
        equipment.setIpaddress(ipAddress);
        equipment.setOrganname(organname);
        String[] equipmentIDs = {equipmentID};
        if (!"".equals(equipmentID) || null != equipmentID){
            equipmentRepository.deleteByEquipmentID(equipmentIDs);
        }
        Tb_equipment equipmentSave = equipmentRepository.save(equipment);
        if (eleids != null) {
            electronicRepository.updateEntryid(equipmentSave.getEquipmentID(), eleids);
        }
        return equipmentSave;
    }

    public List<Tb_electronic> getEquipmentFile(String equipmentID) {
        return electronicRepository.findByEntryidOrderBySortsequence(equipmentID);
    }

    public boolean deleteEquipment(String[] equipmentIDs) {
        if(null==equipmentIDs){
            return false;
        }
        for(String str : equipmentIDs){
         logService.recordTextLog("设备管理","删除设备信息;条目id:"+str);
        }
       Integer result = equipmentRepository.deleteByEquipmentID(equipmentIDs);
       if (result < 0){
           return false;
       }else{
           Integer resultElectronic = electronicRepository.deleteByEntryidIn(equipmentIDs);
           if (resultElectronic < 0){
               return false;
           }
       }
       return true;
    }

    public String importEquipment(MultipartFile fileImport) throws Exception{
        ObjectMapper json = new ObjectMapper();
        Map<String, Object> resMap = new ListHashMap<>();//返回
        String fileName = fileImport.getOriginalFilename();
        String prefix = fileName.substring(fileName.lastIndexOf("."));
        String tempName = UUID.randomUUID().toString().replace("-", "");
        File tempDir = new File(rootpath + "/importEquipment");
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        File tempFile = File.createTempFile(tempName, prefix, tempDir);
        fileImport.transferTo(tempFile);
        //获取excel表列头
        List<String> ExcelHeadList = ReadExcel.getHeadField(tempFile);
        String[] fieldcode = new String[ExcelHeadList.size()];
        String[] fieldname = ExcelHeadList.toArray(new String[ExcelHeadList.size()]);
        for (int i = 0; i < fieldname.length; i++) {
            fieldcode[i] = columnMap.get(fieldname[i]);
        }
        List<Tb_equipment> equipments = new ArrayList<>();
        List<List<String>> lists = ReadExcel.readAllVersionExcel(tempFile, fieldname);// 解析文件
        for (List list : lists) {//已知code和name长度相同并对应
            equipments.add(ValueUtil.creatEquipment(fieldcode, list));
        }
        equipmentRepository.save(equipments);
        tempFile.delete();//用完就删
        resMap.put("success", true);
        resMap.put("msg", "导入成功");
        return json.writeValueAsString(resMap);
    }

}
