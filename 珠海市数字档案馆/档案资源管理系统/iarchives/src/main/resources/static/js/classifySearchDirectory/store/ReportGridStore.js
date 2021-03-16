/**
 * Created by Administrator on 2019/9/21.
 */
Ext.define('ClassifySearchDirectory.store.ReportGridStore',{
    extend:'Ext.data.Store',
    model:'ClassifySearchDirectory.model.ReportGridModel',
    pageSize:XD.pageSize,
    remoteSort:true,//将所有的排序操作推迟到服务器. 如果设置为 false, 则在客户端本地排序
    proxy:{
        type:'ajax',
        url:'/report/getNodeReport',
        extraParams:{},
        reader:{
            type:'json',
            rootProperty:'content',//
            totalProperty:'totalElements'//检索数据集中记录总数的属性名称
        }
    }
});