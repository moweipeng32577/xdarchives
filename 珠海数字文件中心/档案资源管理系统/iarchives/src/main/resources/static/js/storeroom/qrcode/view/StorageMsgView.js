/**
 * Created by zengdw on 2018/05/09 0001.
 */
var store0 = Ext.create("Ext.data.Store", {
    fields: [],
    data: []
});

Ext.define('Qrcode.view.StorageMsgView', {
    extend: 'Ext.panel.Panel',
    xtype: 'storageMsgView',
    layout:'border',
    items:[{
        region:'north',
        columnWidth:.3,
        xtype:'textfield',
        labelAlign:'right',
        //labelWidth:100,
        fieldLabel:'库存位置',
        itemId:'shidTxt',
        name:'shidTxt',

        margin:'15 10 5 5'

    },{
        region:'west',
        flex:1,
        xtype:'basicgrid',
        itemId:'inwaregrid',
        store:'InwareStore',
        hasSearchBar:false,
        margin:'15 15 15 15',
        columns: [
            {text: '入库时间', dataIndex: 'waretime', flex: 2, menuDisabled: true},
            {text: '入库类型', dataIndex: 'waretype', flex: 1, menuDisabled: true},
            {text: '入库人', dataIndex: 'wareuser', flex: 1, menuDisabled: true},
            {text: '备注', dataIndex: 'description', flex: 2, menuDisabled: true}
        ]
    },{
        region:'center',
        flex:1,
        xtype:'basicgrid',
        itemId:'outwaregrid',
        store:'OutwareStore',
        hasSearchBar:false,
        margin:'15 10 15 15',
        columns: [
            {text: '出库时间', dataIndex: 'waretime', flex: 2, menuDisabled: true},
            {text: '出库类型', dataIndex: 'waretype', flex: 1, menuDisabled: true},
            {text: '出库人', dataIndex: 'wareuser', flex: 1, menuDisabled: true},
            {text: '备注', dataIndex: 'description', flex: 2, menuDisabled: true}
        ]
    }]

});