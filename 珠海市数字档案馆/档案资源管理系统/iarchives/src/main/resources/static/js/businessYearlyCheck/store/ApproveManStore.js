/**
 * Created by Administrator on 2020/10/15.
 */


Ext.define('BusinessYearlyCheck.store.ApproveManStore',{
    extend:'Ext.data.Store',
    fields: ['userid', 'realname'],
    proxy: {
        type: 'ajax',
        url: '/electron/getApproveMan',
        extraParams: {
            worktext:'年检审核'
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
