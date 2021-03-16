/**
 * Created by Administrator on 2020/7/29.
 */


Ext.define('ManageCenter.view.ManagementGridView', {
    extend: 'Comps.view.EntryGridView',
    xtype: 'managementgrid',
    dataUrl: '/management/entries',
    tbar:[{
        text:'返回',
        itemId:'backId'
    }],
    title: '当前节点：',
    searchstore: {
        proxy: {
            type: 'ajax',
            url: '/template/queryName',
            extraParams: {nodeid: 0},
            actionMethods:{read:'POST'},
            reader: {
                type: 'json',
                rootProperty: 'content',
                totalProperty: 'totalElements'
            }
        }
    },
    hasSelectAllBox: true
});
