/**
 * Created by yl on 2017/12/5.
 */
Ext.define('Appraisal.store.ApproveManStore',{
    extend:'Ext.data.Store',
    xtype:'approveManStore',
    fields: ['userid', 'realname'],
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '/electron/getApproveMan',
        extraParams: {
            worktext:'销毁审批'
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});