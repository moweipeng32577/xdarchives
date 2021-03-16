/**
 * Created by yl on 2017/10/26.
 */
Ext.define('WhthinManage.store.LookBorrowdocMxGridStore',{
    extend:'Ext.data.Store',
    model:'WhthinManage.model.LookBorrowdocMxGridModel',
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