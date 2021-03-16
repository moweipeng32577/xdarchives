package com.wisdom.util;

/**
 * Created by Leo on 2019/6/14 0014.
 */
public class GuavaUsedKeys {
    public static final String TB_ELECTLIST = "tb_electList";                       //  文件巡查
    public static final String FILE_CHECKER_RESULT_MAP = "fileCheckerResultMap";    //  文件巡查
    public static final String HANDUP_PROGRESS_SUFFIX = "-handupProgress";          //   批量挂接 挂接进度的缓存后缀； key -> UserId + 后缀
    public static final String HANDUP_FILE_SUFFIX = "-handupFile";                           //   批量挂接 挂接log和异常文件的缓存后缀；  key -> SessionId + 后缀
    public static final String HANDUP_FILES = "HandupFiles";                          //    同时挂接时，判断文件名是否相同的缓存
    public static final String IS_HAS_FILE_LAST_TIME = "is_has_file_last_time";    //     上次是挂接是否有文件残留
    public static final String IS_HANDUP_CONTINUE = "is_handup_continue";           //是否继续挂接
    public static final String PROCESS_SUFFIX = "_handup_process";                  //单个用户挂接进程的后缀  key -> UserId + 后缀
    public static final String HANDUP_MESSAGE_SUFFIX = "_handup_Message";           //传给前端的挂接信息

    public static final String NODE_ALL_LIST = "all_nodes";                     //所有节点
    public static final String NODE_USER_LIST_SUFFIX = "_user_nodes";           //用户权限节点，userid+后缀
    public static final String NODE_ROLE_LIST_SUFFIX = "_role_nodes";           //角色权限节点，roleid+后缀
    public static final String NODE_USER_TIME = "_user_time";           //用户获取非缓存个人权限节点时间
    public static final String APPRAISAL_NODE ="appraisal_node";                //鉴定过期的节点

    //机构删除用
    public static final String ORGAN_USER_ARR_SUFFIX = "_user_organ";           //删除的机构节点，userid+后缀
    public static final String NODE_DA_USER_ARR_SUFFIX = "_danode_organ";           //删除的机构相关档案数据节点，userid+后缀
    public static final String NODE_SX_USER_ARR_SUFFIX = "_sxnode_organ";           //删除的机构相关声像数据节点，userid+后缀
}
