/**
 * Created by Administrator on 2019/6/12.
 */


Ext.define('Outware.store.StBorrowdocGridStore',{
    extend:'Ext.data.Store',
    model:'Outware.model.StBorrowdocGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/jyAdmins/getOutwareBorrowdocs',
        extraParams: {outwarestate:'',type:""},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});

