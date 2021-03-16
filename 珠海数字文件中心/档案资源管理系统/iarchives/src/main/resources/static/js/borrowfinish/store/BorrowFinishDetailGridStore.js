/**
 * Created by Administrator on 2018/11/28.
 */

Ext.define('Borrowfinish.store.BorrowFinishDetailGridStore',{
    extend:'Ext.data.Store',
    model:'Borrowfinish.model.BorrowFinishDetailGridModel',
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
