/**
 * Created by Administrator on 2019/6/24.
 */
Ext.define('Accept.store.SterilizingGridStore',{
    extend:'Ext.data.Store',
    model:'Accept.model.AcceptdocBatchGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/accept/getBatchBySearch',
        extraParams:{acceptdocid:'',state:'正在消毒'},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
