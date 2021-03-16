/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Restitution.store.RestitutionYzcStore',{
    extend:'Ext.data.Store',
    model:'Restitution.model.RestitutionYzcModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/jyAdmins/getBorrowEntryIndex',
        extraParams: {flag:'已转出'},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
