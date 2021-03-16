/**
 * Created by SunK on 2018/9/13 0013.
 */
var borrowcontentMode = Ext.create("Ext.data.Store",{
    fields:["text","value"],
    data:[]
});

Ext.define('Equipment.view.EquipmentExportSetView', {
    extend: 'Ext.window.Window',
    itemId: 'selectorID',
    xtype: 'equipmentExportSetView',
    title: '选择字段',
    width:600,
    height:500,
    bodyPadding: 50,
    modal:true,
    closeToolText:'关闭',
    layout: 'fit',
    items: [{
        itemId: 'itemSelectorID',
        xtype: 'itemselector',
        imagePath: '../ux/images/',
        store: borrowcontentMode,
        displayField: 'text',
        valueField: 'value',
        allowBlank: false,
        msgTarget: 'side',
        fromTitle: '可选字段(按Ctrl+F查找)',
        toTitle: '已选字段',
        buttons:['add', 'remove']
    }],
    buttons: [
        {text: '全选',itemId:'addAllOrNotAll'},
        { text: '保存',itemId:'save'},
        { text: '关闭',itemId:'close'}
    ],
});

