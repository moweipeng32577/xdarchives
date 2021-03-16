/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Borrow.store.ElectronBorrowGridStore',{
    extend:'Ext.data.Store',
    model:'Borrow.model.ElectronBorrowGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/management/entries',
        extraParams: {dataids:''},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
