/**
 * Created by Rong on 2017/10/25.
 */
Ext.define('Borrow.view.BorrowGridView', {
    extend: 'Comps.view.EntryGridView',
    xtype: 'borrowgrid',
    dataUrl: encodeURI('/dataopen/entriesByPower'),
    hasCloseButton:false,
    tbar: [{
        itemId: 'stAdd',
        xtype: 'button',
        iconCls:'fa fa-plus-circle',
        text: '添加实体查档'
    }, '-', {
        itemId: 'lookAdd',
        xtype: 'button',
        iconCls:'fa fa-eye',
        text: '处理实体查档'
    }, '-', {
        itemId: 'electronAdd',
        xtype: 'button',
        iconCls:'fa fa-plus-circle',
        text: '添加电子查档'
    }, '-', {
        itemId: 'dealElectronAdd',
        xtype: 'button',
        iconCls:'fa fa-indent',
        text: '处理电子查档'
    }, '-', {
        itemId: 'stAddDoc',
        xtype: 'button',
        iconCls:'fa fa-check-square',
        text: '提交实体查档'
    }, '-', {
        itemId: 'electronAddDoc',
        xtype: 'button',
        iconCls:'fa fa-check-square',
        text: '提交电子查档'
    }],
    searchstore: {
        proxy: {
            type: 'ajax',
            url: '/template/queryName',
            extraParams: {nodeid: 0},
            reader: {
                type: 'json',
                rootProperty: 'content',
                totalProperty: 'totalElements'
            }
        }
    }
});