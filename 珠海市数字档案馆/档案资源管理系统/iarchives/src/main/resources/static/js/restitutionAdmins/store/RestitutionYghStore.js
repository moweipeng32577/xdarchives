/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Restitution.store.RestitutionYghStore',{
    extend:'Ext.data.Store',
    model:'Restitution.model.RestitutionYghModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/jyAdmins/getBorrowEntryIndex',
        extraParams: {flag:'已归还'},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
