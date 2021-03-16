/**
 * Created by Administrator on 2020/4/20.
 */


var placeStore = Ext.create("Ext.data.Store", {
    fields: ["name", "value"],
    data: [
        {text: "空闲中", value: "空闲中"},
        {text: "维修中", value: "维修中"}
    ]
});
Ext.define('PlaceManage.view.PlaceManageAddView', {
    extend: 'Ext.window.Window',
    xtype: 'placeManageAddView',
    itemId: 'placeManageAddViewId',
    title: '新增场地',
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
                itemId: 'floorId',
                xtype: 'textfield',
                fieldLabel: '楼层',
                allowBlank: false,
                name: 'floor'
            }, {
                itemId: 'placedescId',
                xtype: 'textfield',
                fieldLabel: '场地描述',
                allowBlank: false,
                name: 'placedesc'
            },{
                xtype: "combobox",
                fieldLabel: "场地状态",
                itemId: 'stateId',
                name: 'state',
                store: placeStore,
                editable: false,
                allowBlank: false,
                displayField: "text",
                valueField: "value",
                queryMode: "local",
                listeners: {
                    afterrender: function (view) {
                        var store = view.getStore();
                        if (store.getCount() > 0) {
                            view.select(store.getAt(0));
                        }
                    }
                }
            }, {
                itemId: 'remarkId',
                xtype: 'textarea',
                fieldLabel: '备注',
                name: 'remark'
            }
        ]
    }],
    buttons: [
        {text: '提交', itemId: 'placeSubmit'},
        {text: '关闭', itemId: 'placeClose'}
    ]
});
