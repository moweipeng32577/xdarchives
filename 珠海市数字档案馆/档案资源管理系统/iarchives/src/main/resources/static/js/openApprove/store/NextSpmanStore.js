/**
 * Created by tanly on 2017/12/5.
 */
Ext.define('OpenApprove.store.NextSpmanStore',{
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