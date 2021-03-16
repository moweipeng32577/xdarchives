/**
 * Created by Administrator on 2019/5/18.
 */


Ext.define('MetadataSearch.store.ApplyPrintEleGridStore',{
    extend:'Ext.data.Store',
    model:'MetadataSearch.model.ApplyPrintEleGridModel',
    pageSize: XD.pageSize,//此处设置每页显示记录条数
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url:'/simpleSearch/getApplySetPrint',
        extraParams: {
            entryid:''
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
