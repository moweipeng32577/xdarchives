/**
 * Created by yl on 2017/10/26.
 */
Ext.define('SimpleSearch.store.ApproveManStore',{
    extend:'Ext.data.Store',
    fields: ['userid', 'realname'],
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '/electron/getApproveMan',
        extraParams: {
            worktext:'查档审批',
            type:"0"
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});