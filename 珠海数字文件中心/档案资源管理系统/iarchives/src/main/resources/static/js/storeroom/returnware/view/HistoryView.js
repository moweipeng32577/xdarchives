/**
 * Created by tanly on 2017/12/6 0006.
 */
Ext.define('ReturnWare.view.HistoryView', {
    extend: 'Ext.panel.Panel',
    xtype: 'historyView',
    layout:'fit',
    items:[{
        xtype:'basicgrid',
        itemId:'inwaregrid',
        store:'InwareStore',
        hasSearchBar:false,
        selType : 'rowmodel',//默认checkboxmodel 是选择框
        //margin:'15 15 15 15',
        columns: [
            {text: '入库时间', dataIndex: 'waretime', flex: 2, menuDisabled: true},
            {text: '入库类型', dataIndex: 'waretype', flex: 1, menuDisabled: true},
            {text: '入库人', dataIndex: 'wareuser', flex: 1, menuDisabled: true},
            {text: '备注', dataIndex: 'description', flex: 3, menuDisabled: true}
        ]

    }]

});