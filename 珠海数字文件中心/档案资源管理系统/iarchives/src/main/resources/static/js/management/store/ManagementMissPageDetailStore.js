/**
 * Created by Administrator on 2019/3/15.
 */



Ext.define('Management.store.ManagementMissPageDetailStore',{
    extend:'Ext.data.Store',
    model:'Management.model.ManagementMissPageDetailModel',
    pageSize: XD.pageSize,//此处设置每页显示记录条数
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url:'/management/getMissPageCheck',
        //method: 'post',
        extraParams:{
            ids:''
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        },
        actionMethods:{read:'Post'}
    }
});
