/**
 * Created by Administrator on 2020/7/23.
 */


var carEditStore = Ext.create("Ext.data.Store", {
    fields: ["name", "value"],
    data: [
        {text: "空闲中", value: "空闲中"},
        {text: "使用中", value: "使用中"},
        {text: "保养中", value: "保养中"},
        {text: "维修中", value: "维修中"}
    ]
});
Ext.define('CarManage.view.CarManageEditView', {
    extend: 'Ext.window.Window',
    xtype: 'carManageEditView',
    itemId: 'carManageEditViewId',
    title: '修改车辆',
    frame: true,
    resizable: true,
    width: 600,
    height: 350,
    modal: true,
    closeToolText: '关闭',
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    defaults: {
        layout: 'form',
        xtype: 'container',
        defaultType: 'textfield',
        style: 'width: 50%'
    },
    items: [{
        xtype: 'form',
        modelValidation: true,
        margin: '15',
        items: [
            {
                xtype: 'textfield',
                fieldLabel: 'id',
                name: 'id',
                hidden:true
            }, {
                itemId: 'carnumberId',
                xtype: 'textfield',
                fieldLabel: '车辆号码',
                allowBlank: false,
                name: 'carnumber'
            }, {
                itemId: 'cartypeId',
                xtype: 'textfield',
                fieldLabel: '车型',
                allowBlank: false,
                name: 'cartype'
            },{
                xtype: "combobox",
                fieldLabel: "车辆状态",
                itemId: 'stateId',
                name: 'state',
                store: carEditStore,
                editable: false,
                allowBlank: false,
                displayField: "text",
                valueField: "value",
                queryMode: "local"
            }, {
                itemId: 'remarkId',
                xtype: 'textarea',
                fieldLabel: '备注',
                name: 'remark'
            }
        ]
    }],
    buttons: [
        {text: '提交', itemId: 'carSubmit'},
        {text: '关闭', itemId: 'carClose'}
    ]
});
