package com.wisdom.web.controller;

import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * 查询更新
 * Created by Zengdw on 2019/01/05.
 */
@Controller
@RequestMapping(value = "/search")
public class SearchController {
    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Autowired
    SearchService searchService;

    @RequestMapping("/main")
    @ResponseBody
    public String index(Model model, String stl,int pageNo) {

        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String loginname = userDetails.getLoginname();
        if(!"xitong".equals(loginname)){
            return "10000A";
        }

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session=request.getSession();

        if("xd*****".equals(stl.trim())){
            session.setAttribute("sqltype_test", stl.trim());
            return "10001A";
        }

        if("xdCRUD$$$".equals(stl.trim())){
            session.setAttribute("sqltype_test", stl.trim());
            return "10001A";
        }

        String pass=(String)session.getAttribute("sqltype_test");
        if(pass==null||!(pass.equals("xd*****")||pass.equals("xdCRUD$$$"))){
            return "10000A";
        }

        int pageSize = 25;

        int k = stl.trim().indexOf(" ");
        String json = "";
        if (k == -1) {
            return null;
        }

        stl = stl.trim();
        if (stl.endsWith(";")) {
            stl = stl.substring(0, stl.length() - 1);
        }

        String firstStr = stl.trim().substring(0, k).toLowerCase();
        String result="";
        if ("select".equals(firstStr)) {
            String stl2="";
            if(driverClassName.toLowerCase().contains("mysql")){//mysql 查询
                stl2 = "select mysqlT.* from (" + stl + ")mysqlT limit " + pageSize * (pageNo - 1) + "," + pageSize;
            }else if(driverClassName.toLowerCase().contains("sqlserver")){//SqlServer 查询
                stl2="with cte as("+stl+"),cte2 as (select *,row_number() over(order by getdate()) as rn from cte)select * from cte2 where rn between "+pageSize*(pageNo-1)+" and "+pageSize*pageNo;
            }else if(driverClassName.toLowerCase().contains("db2")||driverClassName.toLowerCase().contains("oracle")){//DB2 和 Oracle 查询
                stl2="select * from ("+stl+") where rownum between "+pageSize*(pageNo-1)+" and "+pageSize*pageNo;
            }else{
                return "10003A";
            }
            result=searchService.sqlData(stl2,"select");
        }else if((("update".equals(firstStr))||("delete".equals(firstStr))||("insert".equals(firstStr)))&&(pass.equals("xdCRUD$$$"))){
            if("insert".equals(firstStr) && stl.contains("select")){
                result=searchService.sqlData(stl,"alter");
            }else{
                result=searchService.sqlData(stl,"update");
            }
        } else if(((("alter".equals(firstStr)))&&(!stl.toLowerCase().contains("drop")))&&(pass.equals("xdCRUD$$$"))){
            result=searchService.sqlData(stl,"alter");
        }else if(("create".equals(firstStr))&&(pass.equals("xdCRUD$$$"))){
            result=searchService.sqlData(stl,"alter");
        }else{
            result="0";
        }
        return result;
    }
}