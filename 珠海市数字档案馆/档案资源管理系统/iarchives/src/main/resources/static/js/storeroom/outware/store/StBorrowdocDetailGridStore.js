/**
 * Created by Administrator on 2020/6/4.
 */


Ext.define('Outware.store.StBorrowdocDetailGridStore',{
    extend:'Ext.data.Store',
    model:'Outware.model.StBorrowdocDetailGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/jyAdmins/getEntryIndex',
        extraParams: {
            borrowdocid:''
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
