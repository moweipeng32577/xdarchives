/**
 * Created by Administrator on 2019/6/18.
 */
Ext.define('Accept.store.AcceptdocBatchGridStore',{
    extend:'Ext.data.Store',
    model:'Accept.model.AcceptdocBatchGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/accept/getBatchBySearch',
        extraParams:{acceptdocid:'',state:''},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});