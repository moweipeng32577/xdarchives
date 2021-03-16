/**
 * Created by Administrator on 2020/6/22.
 */


Ext.define('CarOrder.store.NextSpmanStore',{
    extend:'Ext.data.Store',
    xtype:'nextSpmanStore',
    fields: ['userid', 'realname'],
    proxy: {
        type: 'ajax',
        url: '/electronApprove/getNextSpman',
        extraParams: {
            nodeId:''
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
