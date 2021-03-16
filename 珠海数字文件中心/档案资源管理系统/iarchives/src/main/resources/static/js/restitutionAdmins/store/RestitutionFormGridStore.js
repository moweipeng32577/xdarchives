/**
 * Created by yl on 2017/10/26.
 */
Ext.define('Restitution.store.RestitutionFormGridStore', {
    extend: 'Ext.data.Store',
    model: 'Restitution.model.RestitutionFormGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/jyAdmins/getMyBorrowmsgs',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});