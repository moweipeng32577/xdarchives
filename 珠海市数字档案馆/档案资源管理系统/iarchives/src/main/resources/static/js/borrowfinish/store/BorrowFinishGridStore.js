/**
 * Created by Administrator on 2018/11/28.
 */

Ext.define('Borrowfinish.store.BorrowFinishGridStore',{
    extend:'Ext.data.Store',
    model:'Borrowfinish.model.BorrowFinishGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/indexly/getBorrowFinishMsg',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
