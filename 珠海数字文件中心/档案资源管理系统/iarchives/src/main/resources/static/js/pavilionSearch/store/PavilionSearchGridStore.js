/**
 * Created by RonJiang on 2017/10/24 0024.
 */
Ext.define('PavilionSearch.store.PavilionSearchGridStore',{
    extend:'Ext.data.Store',
    model:'PavilionSearch.model.PavilionSearchGridModel',
    pageSize: XD.pageSize,//此处设置每页显示记录条数
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url:'/simpleSearch/findBySearchPlatform',//条目开放的档案
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});