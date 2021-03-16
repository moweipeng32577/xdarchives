/**
 * Created by Administrator on 2019/10/31.
 */



Ext.define('Management.store.LookBackCaptureDocGridStore',{
    extend:'Ext.data.Store',
    model:'Management.model.LookBackCaptureDocGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/management/getNodeBackCaptureDoc',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
