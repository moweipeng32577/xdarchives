/**
 * Created by RonJiang on 2017/10/24 0024.
 */
Ext.define('SimpleSearch.store.SimpleSearchGridStore',{
    extend:'Ext.data.Store',
    model:'SimpleSearch.model.SimpleSearchGridModel',
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