package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_data_node;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by tanly on 2017/10/30 0030.
 */
public interface DataNodeRepository  extends JpaRepository<Tb_data_node, String>, JpaSpecificationExecutor<Tb_data_node> {

    List<Tb_data_node> findByParentnodeid(String pcid);

    List<Tb_data_node> findByParentnodeidOrderBySortsequence(String pcid);

    List<Tb_data_node> findByParentnodeidIsNullOrParentnodeid(String pcid);

    @Query(value = "select * from tb_data_node_sx t where parentnodeid=?1 or parentnodeid is null",nativeQuery = true)
    List<Tb_data_node> findSxByParentnodeidIsNullOrParentnodeid(String pcid);

    List<Tb_data_node> findByParentnodeidIsNullOrParentnodeidOrderBySortsequence(String pcid);

    @Query(value = "select nodeid from Tb_data_node where refid=?1")
    List<String> findNodeidByRefid(String refid);
    //原生sql递归查找省去在service层递归查找生成sql时间
    //"select nodeid from Tb_data_node where parentnodeid=?1"+
//    @Query(nativeQuery = true,value = "WITH _children AS(SELECT nodeid,parentnodeid from tb_data_node WHERE parentnodeid=? UNION ALL SELECT t.nodeid,t.parentnodeid FROM tb_data_node t INNER JOIN _children c on t.parentnodeid=c.nodeid )SELECT distinct nodeid from _children")
//    List<String> findNodeidByParentnodeid(String parentnodeid);

    @Query(value = "select refid from Tb_data_node where nodeid=?1")
    String findRefidByNodeid(String nodeid);

    @Query(value = "select parentnodeid from Tb_data_node where nodeid=?1")
    String findParentnodeidByNodeid(String nodeid);

    @Query(value = "select nodename from Tb_data_node where nodeid=?1")
    String findNodenameByNodeid(String nodeid);

    @Query(value = "select n from Tb_data_node n where nodeid = (select parentnodeid from Tb_data_node where nodeid = ?1)")
    Tb_data_node findParentnodeByNodeid(String nodeid);

    /**
     * @param nodeid
     * @return Tb_data_node
     */
    Tb_data_node findByNodeid(String nodeid);
    
    @Query(value = "select nodetype from Tb_data_node where nodeid=?1")
    int findNodetypeByNodeid(String nodeid);
    
    /**
     * @param pageable
     * @param parentnodeid
     * @return
     */
    Page<Tb_data_node> findByParentnodeid(Pageable pageable, String parentnodeid);

    @Query(value = "select u from Tb_data_node u where u.nodeid in (select parentnodeid from Tb_data_node where nodeid in (?1))")
    List<Tb_data_node> getParentNodes(String[] nodeids);

    @Modifying
    @Query(value = "update Tb_data_node set sortsequence = sortsequence + 1 where sortsequence between ?1 and ?2")
    int modifyNodeOrder(int start,int end);
    
    @Modifying
    @Query(value = "update Tb_data_node set luckstate = ?2 where nodeid = ?1")
    Integer updateNodeLuckState(String nodeid, String luckstate);

    List<Tb_data_node> findByRefid(String refId);
    @Query(value = "select * from tb_data_node_sx u where u.refid=?1",nativeQuery = true)
    List<Tb_data_node> findSxByRefid(String refId);
    List<Tb_data_node> findByRefidOrderBySortsequence(String refId);
    
    @Query(value = "select nodeid from Tb_data_node where refid = ?1")
    List<String> findNodeListByRefid(String refId);

    Tb_data_node findByNodename(String nodeName);
    
    @Query(value = "select t.nodeid from Tb_data_node t where nodename = '文书档案' and parentnodeid in (select nodeid from Tb_data_node where nodename = '已归管理')")
    String findDCFile();//查找已归管理   - 文书档案
    
    @Query(value = "select t.nodeid from Tb_data_node t where nodename like concat('%','案卷','%') and parentnodeid in (select nodeid from Tb_data_node where parentnodeid in (select nodeid from Tb_data_node where nodename = '案卷管理'))")
    List<String> findFiles();//查找案卷管理   - 文书案卷
    
    @Query(value = "select t.nodeid from Tb_data_node t where nodename like concat('%','卷内','%') and parentnodeid in (select nodeid from Tb_data_node where parentnodeid in (select nodeid from Tb_data_node where nodename = '案卷管理'))")
    List<String> findInnerFiles();//查找案卷管理   - 卷内文件
    
    @Query(value = "select t.nodeid from Tb_data_node t where nodename = '文书案卷' and parentnodeid in (select nodeid from Tb_data_node where nodename = '文书档案' and parentnodeid in (select nodeid from Tb_data_node where nodename = '案卷管理'))")
    List<String> findDCFiles();//查找到案卷管理 - 文书档案 - 文书案卷节点信息
    
    @Query(value = "select t.nodeid from Tb_data_node t where nodename = '卷内文件' and parentnodeid in (select nodeid from Tb_data_node where nodename = '文书档案' and parentnodeid in (select nodeid from Tb_data_node where nodename = '案卷管理'))")
    List<String> findDCInnerFiles();//查找到案卷管理 - 文书档案 - 卷内文件节点信息
    
//    @Query(value = "select * from tb_data_node where nodeid in(" +
//            "select nodeid from tb_user_data_node where userid=?1" +
//            " union select nodeid from  tb_role_data_node where roleid in (select roleid from tb_user_role where userid=?1))",nativeQuery=true)
//    List<Tb_data_node> getMyAuthWithParent(String userid);

    @Query(value = "select new Tb_data_node(n.nodeid,n.nodename,n.nodetype,n.classlevel,n.sortsequence,n.organid) from Tb_data_node n where nodeid in(select nodeid from Tb_user_data_node where userid=?1) ORDER BY sortsequence")
    List<Tb_data_node> getMyAuth(String userid);
    @Query(value = "select * from tb_data_node_sx where nodeid in(select nodeid from tb_user_data_node_sx where userid=?1) ORDER BY sortsequence",nativeQuery=true)
    List<Tb_data_node> getSxMyAuth(String userid);

    @Query(value = "select * from tb_data_node_sx  ORDER BY sortsequence",nativeQuery=true)
    List<Tb_data_node> getSxMyAuthAll();

    @Query(value = "select new Tb_data_node(n.nodeid,n.nodename,n.nodetype,n.classlevel,n.sortsequence,n.organid) from Tb_data_node n where nodeid in (select nodeid from Tb_role_data_node where roleid in (select roleid from Tb_user_role where userid=?1)) ORDER BY sortsequence")
    List<Tb_data_node> getMyAuthFromRole(String userid);
    @Query(value = "select * from tb_data_node_sx where nodeid in (select nodeid from tb_role_data_node_sx where roleid in (select roleid from tb_user_role where userid=?1)) ORDER BY sortsequence",nativeQuery=true)
    List<Tb_data_node> getSxMyAuthFromRole(String userid);

    @Query(value = "select new Tb_data_node(n.nodeid,n.parentnodeid) from Tb_data_node n ORDER BY sortsequence")
    List<Tb_data_node> findAllNodeidParentid();
    @Query(value = "select * from tb_data_node_sx n ORDER BY sortsequence",nativeQuery = true)
    List<Tb_data_node> findSxAllNodeidParentid();

    Integer deleteByNodeid(String nodeId);

    Integer deleteByNodeidIn(String[] nodeIds);

    @Query(value = "select max(sortsequence) from Tb_data_node")
    Integer getMaxOrder();

    List<Tb_data_node> findByNodeidIn(String[] nodeIds);

    Tb_data_node findClassByNodeid(String nodeid);

    Tb_data_node findByClasslevelAndParentnodeid(Integer classlevel, String parentid);

    Tb_data_node findByClasslevelAndClassidAndOrganid(Integer classlevel, String classid, String organid);

    /**
     * 获取：某节点下，最大的节点编码
     * @param parentid
     * @return
     */
    @Query(value = "SELECT max(nodecode) FROM Tb_data_node where parentnodeid=?1")
    String getMaxNodecodeByParentnodeid(String parentid);

    @Query(value = "SELECT max(nodecode) FROM tb_data_node_sx where parentnodeid=?1",nativeQuery = true)
    String getSxMaxNodecodeByParentnodeid(String parentid);

    @Query(value = "SELECT max(nodecode) FROM Tb_data_node where parentnodeid=?1 or parentnodeid is null")
    String getMaxNodecodeByParentnodeidOrNull(String parentid);

    /**
     * 获取：最顶层分类节点的父节点（机构）：organ-class模式
     * @return
     */
    @Query(value = "select d from Tb_data_node d where d.nodetype='1' and d.refid in (" +
            "select organid from Tb_right_organ where organid not in (select parentid from Tb_right_organ))")
    List<Tb_data_node> getOrganRemixNode();

    List<Tb_data_node> findByRefidIn(String[] refIds);
    @Query(value = "SELECT * FROM tb_data_node_sx where refid in (?1)",nativeQuery = true)
    List<Tb_data_node> findSxByRefidIn(String[] refIds);

    /**
     * 获取第一层机构的父节点（分类）
     * @return
     */
    @Query(value = "select d from Tb_data_node d where d.nodeid in (" +
            "select distinct parentnodeid from Tb_data_node where refid in (" +
            "select organid from Tb_right_organ where parentid=?1))")
    List<Tb_data_node> getParentOfFirstOrgan(String parentid);

    @Query(value = "select * from tb_data_node_sx d where d.nodeid in (" +
            "select distinct parentnodeid from tb_data_node_sx where refid in (" +
            "select organid from tb_right_organ where parentid=?1))",nativeQuery = true)
    List<Tb_data_node> getSxParentOfFirstOrgan(String parentid);

    @Modifying
    @Query(value = "update Tb_data_node set sortsequence = sortsequence + 1 where sortsequence between ?1 and ?2 " +
            "and parentnodeid = (select parentnodeid from Tb_data_node where nodeid=?3) ")
    int modifyOrganNodeOrder(int start,int end,String nodeid);

    @Modifying
    @Query(value = "update Tb_data_node set sortsequence = sortsequence + 1 where sortsequence between ?1 and ?2 " +
            "and parentnodeid = ?3 ")
    int modifyOrganNodeOrderByParent(int start,int end,String parentnodeid);

    @Modifying
    @Query(value = "update Tb_data_node set sortsequence = ?1 where refid=?2")
    int modifyOrderByRefid(int order,String refId);

    @Query(value="select d from Tb_data_node d where d.refid=?1 and d.parentnodeid =(" +
            "select parentnodeid from Tb_data_node where nodeid=?2)")
    Tb_data_node getNodeByRefidAndNodeid(String refid,String nodeid);

    @Modifying
    @Query(value = "update Tb_data_node set nodelevel = nodelevel + 1,classid = ?2,classlevel = ?3 where nodeid in ?1")
    int moveOrganUpdate(String[] nodeids,String classid,int classlevel);

    @Modifying
    @Query(value = "delete from Tb_data_node where refid in ?1")
    int deleteByRefidIN(String[] refIds);

    @Query(value = "select t.nodeid from Tb_data_node t where refid in ?1")
    String[] findByRefidIN(String[] refIds);

    @Modifying
    @Query(value = "update Tb_data_node set parentnodeid = ?1 where nodeid in ?2")
    int updateParentid(String pid,String[] nodeids);

    @Query(value="select d from Tb_data_node d where d.nodeid in ?1 and d.refid in (select organid from Tb_right_organ where parentid='0')")
    List<Tb_data_node> findTopNodes(String[] ids);

    //设置富滇的tb_data_node的classid--start
    @Query(value = "select t.nodecode from Tb_data_node t where t.nodetype=2 order by t.nodecode")
    List<String> findAllFl();

    @Query(value = "select t.nodecode from Tb_data_node t where t.nodecode like concat(?1,'%') and t.nodecode <> ?1 and t.nodetype=2 ")
    List<String> findAllFlNext(String nodecode);

    @Query(value = "select t.classid from Tb_data_node t  where t.nodecode=?1")
    String findClassid(String nodecode);
    /*@Modifying
    @Query(value = "update tb_data_node set  classid=(select classid from tb_data_node  where nodecode=?1) where nodecode like concat(?1,'%') and nodetype=1",nativeQuery = true)
    int updateAllNodeNext(String nodecode);*/
    @Modifying
    @Query(value = "update tb_data_node set  classid=?2 where nodecode like concat(?1,'%') and nodetype=1",nativeQuery = true)
    int updateAllNodeNext(String nodecode,String classid);
    //设置富滇的tb_data_node的classid--end

    //更新node案卷节点nodecode异常--start
    @Query(value = "select t.nodecode from Tb_data_node t where nodename='案卷管理' and length(parentnodeid)<1")
    String findAjByNodeid();
    @Query(value = "select t from Tb_data_node t where t.nodecode  like concat(?1,'%') and t.nodename like '%案卷' order by nodecode")
    List<Tb_data_node> findAjs(String nodecode);
    @Modifying
    @Query(value = "update Tb_data_node set nodecode=?1 where nodeid=?2")
    int updateNodecode(String nodecode,String nodeid);
    //更新node案卷节点nodecode异常--end

    @Query(value = "select nodelevel from Tb_data_node where nodeid=?1")
    String findNodeLevelByNodeid(String nodeid);

    @Query(value = "select parentnodeid from Tb_data_node where nodeid=?1")
    String findParentNodeidByNodeid(String nodeid);

    @Query(value = "select t.nodecode from Tb_data_node t where t.nodecode like concat(?1,'%') and t.organid=?2")
    List<Tb_data_node> findByNodecodeAndOrganid(String jnNodecode,String organid);

    List<Tb_data_node> findByOrganid(String organID);
    
    @Query(value = "select t.nodeid from Tb_data_node t where nodename = '文书档案' and parentnodeid in (select n.nodeid from Tb_data_node n where nodename = '已归管理')")
    String findWSInfoByNodename();

    @Query(value = "select t from Tb_data_node t where classid=?1")
    List<Tb_data_node> findByClassid(String classid);

    @Query(value = "select nodeid,nodename,leaf from Tb_data_node where parentnodeid=?1 order by sortsequence")
    List<Tb_data_node> findDataNodes(String parentnodeid);

    @Query(value = "select t.nodeid from Tb_data_node t where t.nodetype=1 and t.classlevel=2")
    List<String> findAjNodeId();

    @Query(value = "select nodeid from Tb_data_node where parentnodeid=(select nodeid from Tb_data_node where nodename=?1) and nodename=?2")
    String findnodeidByParentnodename(String Parentnodename,String nodename);

    @Query(value = "select nodeid from Tb_data_node where parentnodeid=?1 and nodename=?2")
    String findnodeidByParentnodeid(String parentnodeid,String nodenmae );

    @Query(value = "select nodeid from Tb_data_node where parentnodeid=?1 order by nodecode")
    List<String> findnodeidOrderBynodecode(String parentnodeid);

    //原生sql递归查找省去在service层递归查找生成sql时间
//    @Query(nativeQuery = true,value = "With RECURSIVE _findNameByNode As( select * from tb_data_node where nodeid=? Union ALL SELECT t.* from tb_data_node t INNER JOIN _findNameByNode f ON t.nodeid=f.parentnodeid )select DISTINCT* from _findNameByNode  where nodelevel='1'")
//    Tb_data_node findByLevelWithLoop(String nodeid);
    //原生sql递归查找省去在service层递归查找生成sql时间
//    @Query(nativeQuery = true,value = "With RECURSIVE _findNameByNode As( select * from tb_data_node where nodeid=?1 Union ALL SELECT t.* from tb_data_node t INNER JOIN _findNameByNode f ON t.nodeid=f.parentnodeid )select DISTINCT * from _findNameByNode where nodename=?2")
//    Tb_data_node findByNodeNameWithLoop(String nodeid,String name);
    //原生sql递归查找省去在service层递归查找生成sql时间
//    @Query(nativeQuery = true,value = "with RECURSIVE _findParentidBynodeLoop AS(SELECT * FROM tb_data_node WHERE nodeid=?1 UNION ALL SELECT t.* FROM tb_data_node t INNER JOIN _findParentidBynodeLoop f ON t.nodeid = f.parentnodeid )select DISTINCT * from _findParentidBynodeLoop where parentnodeid=?2")
//    Tb_data_node findByParentidWithLoop(String nodeid,String pid);

    @Query(value = "select t.nodeid from Tb_data_node t where t.organid=?1")
    List<String> findNodeidsByOrganid(String organid);

    @Query(value = "select a.classlevel from tb_data_node a inner JOIN tb_entry_index b on b.entryid = ?1 and a.nodeid = b.nodeid" ,nativeQuery = true)
    String findClassLevelByEntryId(String entryId);

    @Query(value = "select nodeid from Tb_data_node t where parentnodeid=?1 and organid=?2")
    String findByParentnodeidAndOrganid(String parentnodeid,String organid);

    @Query(value = "select t from Tb_data_node t where nodename = '文书文件' and parentnodeid in (select nodeid from Tb_data_node where nodename = '未归管理')")
    Tb_data_node findWSFile();//查找未归管理   - 文书文件
}