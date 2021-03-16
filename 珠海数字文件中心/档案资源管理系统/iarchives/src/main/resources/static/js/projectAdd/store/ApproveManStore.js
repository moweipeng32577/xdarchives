/**
 * Created by Administrator on 2020/4/21.
 */


Ext.define('ProjectAdd.store.ApproveManStore',{
    extend:'Ext.data.Store',
    xtype:'approveManStore',
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
