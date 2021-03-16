/**
 * Created by Administrator on 2019/5/23.
 */

Ext.define('ElectronPrintApprove.store.NextSpmanStore',{
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
