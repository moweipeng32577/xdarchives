/**
 * Created by tanly on 2017/11/4 0004.
 */
var orderStore = Ext.create("Ext.data.Store", {
    fields: ["Name", "Value"],
    data: [
        { Name: "正序", Value: '0' },
        { Name: "倒序", Value: '1'}
    ]
});
Ext.define('Management.view.OrdersettingDetailFormView', {
    extend: 'Ext.form.Panel',
    title: '设置信息:',
    width: 300,
    height: 120,
    layout: 'form',
    xtype: 'ordersettingDetailFormView',
    itemId: 'ordersettingDetailFormViewID',
    autoScroll: true,
    items: [
        {
            xtype: 'textfield',
            itemId: 'areaid',
            fieldLabel: '域名描述',
            readOnly: true,
            maxLength: 32,
            name: 'areatext'
        }, {
            xtype: "combobox",
            name: "direction",
            fieldLabel: "排序方式",
            itemId: 'directionId',
            store: orderStore,
            editable: false,
            displayField: "Name",
            valueField: "Value",
            queryMode: "local"
        }, {
            xtype: 'textfield',
            itemId: 'hiddenfieldId',
            name: 'hideid',
            hidden: true
        }
    ],
    buttons:[{
        text: '保存排序设置',
        itemId: 'ordersettingSaveBtnId'
    }]
});