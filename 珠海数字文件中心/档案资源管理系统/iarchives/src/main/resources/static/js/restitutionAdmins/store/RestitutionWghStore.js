/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Restitution.store.RestitutionWghStore',{
    extend:'Ext.data.Store',
    model:'Restitution.model.RestitutionWghModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/jyAdmins/getBorrowEntryIndex',
        extraParams: {flag:'未归还'},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
