/**
 * Created by Administrator on 2019/2/19.
 */

Ext.define('StApprove.store.SimpleSearchGridStore',{
    extend:'Ext.data.Store',
    xtype:'simpleSearchGridStore',
    model:'StApprove.model.SimpleSearchGridModel',
    pageSize: XD.pageSize,//此处设置每页显示记录条数
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url:'/simpleSearch/findBySearch',
        timeout:XD.timeout,
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
