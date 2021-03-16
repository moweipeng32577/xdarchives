/**
 * Created by yl on 2017/10/26.
 */
Ext.define('StApprove.store.NextSpmanStore',{
    extend:'Ext.data.Store',
    xtype:'nextSpmanStore',
    fields: ['userid', 'realname'],
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '/electronApprove/getNextSpman',
        extraParams: {
            nodeId:nodeId
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});