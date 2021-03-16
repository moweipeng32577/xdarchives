/**
 * Created by RonJiang on 2017/10/24 0024.
 */
Ext.define('ThematicProd.store.SimpleSearchGridStore',{
    extend:'Ext.data.Store',
    xtype:'simpleSearchGridStore',
    model:'ThematicProd.model.SimpleSearchGridModel',
    pageSize: XD.pageSize,//此处设置每页显示记录条数
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url:'/simpleSearch/findBySearchPlatform',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});