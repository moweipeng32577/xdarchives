/**
 * Created by Administrator on 2019/5/23.
 */


Ext.define('ElectronPrintApprove.store.NextNodeStore',{
    extend:'Ext.data.Store',
    xtype:'nextNodeStore',
    fields: ['id', 'text'],
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '/electronApprove/getNextNode',
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