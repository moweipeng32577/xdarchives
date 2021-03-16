/**
 * Created by Administrator on 2019/6/17.
 */
Ext.define('Accept.store.AcceptdocFinishGridStore',{
    extend:'Ext.data.Store',
    model:'Accept.model.AcceptdocFinishGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    autoLoad:true,
    proxy: {
        type: 'ajax',
        url: '/accept/getAcceptDocByState',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});