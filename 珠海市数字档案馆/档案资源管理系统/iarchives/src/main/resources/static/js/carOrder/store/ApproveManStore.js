/**
 * Created by Administrator on 2020/4/21.
 */


Ext.define('CarOrder.store.ApproveManStore',{
    extend:'Ext.data.Store',
    xtype:'approveManStore',
    fields: ['userid', 'realname'],
    proxy: {
        type: 'ajax',
        url: '/electron/getApproveMan',
        extraParams: {
            worktext:'公车预约审批'
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
