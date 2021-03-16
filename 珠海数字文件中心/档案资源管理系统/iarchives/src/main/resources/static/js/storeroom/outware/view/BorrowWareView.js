/**
 * Created by tanly on 2017/12/6 0006.
 */
Ext.define('Outware.view.BorrowWareView', {
    extend: 'Comps.view.EntryGridView',
    xtype: 'borrowWareView',
    //dataUrl: '/inware/returnWare',
    tbar: [{
        text: '读取档案管理数据',
        itemId: 'update'
    }],
    searchstore:{
        proxy: {
            type: 'ajax',
            url:'/outware/outwares',//查看出库记录
            extraParams:{nodeid:0},
            reader: {
                type: 'json',
                rootProperty: 'content',
                totalProperty: 'totalElements'
            }
        }
    }
});