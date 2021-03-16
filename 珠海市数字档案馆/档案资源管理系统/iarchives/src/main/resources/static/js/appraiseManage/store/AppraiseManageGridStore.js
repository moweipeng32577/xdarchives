/**
 * Created by Administrator on 2020/3/23.
 */


Ext.define('AppraiseManage.store.AppraiseManageGridStore',{
    extend:'Ext.data.Store',
    model:'AppraiseManage.model.AppraiseManageGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/appraiseManage/getAppraiseManage',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
