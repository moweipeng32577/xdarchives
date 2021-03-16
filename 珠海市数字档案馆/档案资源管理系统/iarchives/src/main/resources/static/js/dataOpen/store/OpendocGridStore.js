/**
 * Created by RonJiang on 2018/4/21 0021.
 */
Ext.define('Dataopen.store.OpendocGridStore',{
    extend:'Ext.data.Store',
    model:'Dataopen.model.OpendocGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/dataopen/getNodeOpendoc',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});