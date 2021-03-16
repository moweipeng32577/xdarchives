/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Restitution.store.RestitutionRegisterStore',{
    extend:'Ext.data.Store',
    model:'Restitution.model.RestitutionRegisterModel',
    pageSize: XD.pageSize,
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '/jyAdmins/getBorrowdocs',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
