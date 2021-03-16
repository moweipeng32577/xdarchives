package com.xdtech.component.storeroom.controller;

import com.xdtech.component.storeroom.entity.ExtMsg;
import com.xdtech.component.storeroom.entity.Inventory;
import com.xdtech.component.storeroom.entity.InventoryResult;
import com.xdtech.component.storeroom.repository.InventoryRepository;
import com.xdtech.component.storeroom.service.InventoryService;
import com.xdtech.component.storeroom.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * 实体档案盘点控制器
 *
 * V0.1
 * 初始版本
 * Created by Rong on 2018/4/26.
 */
@Controller
@RequestMapping(value = "/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private InventoryRepository inventoryRepository;

    @RequestMapping("/main")
    public String inware() {
        return "/inlet/storeroom/inventory";
    }

    @RequestMapping("/importData")
    @ResponseBody
    public void importData(@RequestParam("import") MultipartFile file,String shid,String description,String shidMsg) {

        //从session获取用户名
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session=request.getSession();
        String user=(String)session.getAttribute("username");

        HttpServletResponse response = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getResponse();


        String resultMsg="";
        try{

            InputStream inputStream = file.getInputStream();
            OutputStream outputStream = new FileOutputStream("D:\\xd_inventory.txt");
            int bytesWritten = 0;
            int byteCount = 0;
            byte[] bytes = new byte[1024];
            while ((byteCount = inputStream.read(bytes)) != -1){
                outputStream.write(bytes, 0, byteCount);
                bytesWritten += byteCount;
            }
            inputStream.close();
            outputStream.close();
            Inventory inventory=new Inventory();
            inventory.setDescription(description);
            inventory.setCheckuser(user);

            File inventoryFile=new File("D:\\xd_inventory.txt");
            resultMsg=inventoryService.save(inventory,inventoryFile,shid,shidMsg);


            response.setContentType ("text/html");
            //response.write("{success:true,Msg:\"文件上传成功\"}");
            response.getWriter().write("{\"success\":true}");
            if(resultMsg.equals("0")){
                //return new ExtMsg(true,"盘点范围无存档",null);
            }else if(resultMsg.equals("1")){
               // return new ExtMsg(true,"盘点清单为空",null);
            }else if(resultMsg.equals("2")){
                //return new ExtMsg(true,"盘点成功",null);
            }

        }catch(Exception e){
            e.printStackTrace();
        }finally{

        }
        //return new ExtMsg(true,"盘点中断",null);
    }

    /*@RequestMapping("/show")
    @ResponseBody
    public List<Inventory> importData(){

        return inventoryRepository.findAll();
    }*/

    /**
     *
     * @param page
     * @param limit
     * @return 返回盘点记录集合
     */
    @RequestMapping(value = "/show", method = RequestMethod.GET)
    @ResponseBody
    public Page<Inventory> findInventory(int page, int limit){
        //PageRequest pageRequest = new PageRequest(page-1, limit);
        //Page<Inventory> result = inventoryService.findInventory(pageRequest);
        Page<Inventory> result = inventoryService.findAll(page-1,limit);
        return result;
    }

    /**
     *
     * @param page
     * @param limit
     * @return 返回盘点记录集合
     */
    @RequestMapping(value = "/resultShow", method = RequestMethod.GET)
    @ResponseBody
    public Page<InventoryResult> findInventoryResult(int page, int limit, String checkid, String resulttype){
    //public ExtMsg findInventoryResult(int page, int limit, String checkid, String resulttype){
        PageRequest pageRequest = new PageRequest(page-1, limit);
        Page<InventoryResult> result = inventoryService.findInventoryResult(pageRequest,resulttype,checkid);
        String resultMsg="";
        if(result.getSize()>0){
            resultMsg="1";
        }else{
            resultMsg="0";
        }
        return result;
        //return new ExtMsg(true,resultMsg,result);
    }

    /**
     *
     * @param checkid
     * @param resulttype
     * @return 返回盘点记录集合
     */
    @RequestMapping("/result")
    @ResponseBody
    public ExtMsg findInventoryResult(String checkid, String resulttype){
        List<InventoryResult> result = inventoryService.findInventoryResults(resulttype,checkid);
        String resultMsg="";
        if(result.size()>0){
            resultMsg="1";
            return new ExtMsg(true,"1",null);
        }else{
            resultMsg="0";
            return new ExtMsg(true,"0",null);
        }
        //return new ExtMsg(true,resultMsg,null);
    }

    /**
     * 修改盘点状态异常记录
     * @param chipcodes
     * @return
     */
    @RequestMapping("/changeSta")
    @ResponseBody
    public ExtMsg changeStatus(String chipcodes){
        storageService.changeStatus(chipcodes);
        return new ExtMsg(true,"修改成功",null);
    }

    /**
     * 修改盘点位置异常
     * @param chipShids
     * @return
     */
    @RequestMapping("/changeShel")
    @ResponseBody
    public ExtMsg changeShel(String chipShids){

        String[] chipshs=chipShids.split(";");
        storageService.changeShel(chipshs);
        return new ExtMsg(true,"修改成功",null);
    }
}
