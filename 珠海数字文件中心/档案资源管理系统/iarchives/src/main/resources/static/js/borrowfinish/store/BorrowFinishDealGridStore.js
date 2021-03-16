/**
 * Created by Administrator on 2018/11/28.
 */

Ext.define('Borrowfinish.store.BorrowFinishDealGridStore',{
    extend:'Ext.data.Store',
    model:'Borrowfinish.model.BorrowFinishDealGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/jyAdmins/getDealDetails',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
