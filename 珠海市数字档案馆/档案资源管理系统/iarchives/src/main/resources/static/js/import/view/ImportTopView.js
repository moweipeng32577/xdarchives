/**
 * Created by yl on 2017/10/25.
 */
var exportData = Ext.create('Ext.data.Store', {
    fields: ['item', 'name'],
    data: [
        {"item": "excel", "name": "excel"},
        {"item": "zip", "name": "zip"}
    ]
});
Ext.define('Import.view.ImportTopView',{
    extend: 'Ext.Panel',
    xtype: 'importTopView',
    region: 'north',
    height: 40,
    layout: {
        type: 'hbox',
        align: 'middle',
        pack:'center'
    },
    items: [
        {
            xtype: 'label',
            text: '文件名称:',
            style: "margin-right:4px"
        },{
            xtype: 'textfield',
            width:150,
            itemId:'exportFileNameID',
            style: "margin-right:4px"
        },{
            xtype: 'label',
            text: '文件格式:',
            style: "margin-right:4px"
        }, {
            width: 120,
            xtype: 'combo',
            store: exportData,
            displayField: 'name',
            valueField: 'item',
            value: 'excel',
            style: "margin-right:4px"
        }, {
            xtype: 'button',
            itemId:'importBtnID',
            text: '导入'
        }
    ]
});
