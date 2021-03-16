/**
 * Created by xd on 2017/10/21.
 */
Ext.define('FindAccount.store.FindAccountGridStore',{
    extend:'Ext.data.Store',
    model:'FindAccount.model.FindAccountGridModel',
    autoLoad: true,
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/user/getUnitOutuser',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
