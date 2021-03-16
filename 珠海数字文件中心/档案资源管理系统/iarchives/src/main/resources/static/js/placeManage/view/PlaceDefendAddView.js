/**
 * Created by Administrator on 2020/6/24.
 */


var defendTypeStore = Ext.create("Ext.data.Store", {
    fields: ["name", "value"],
    data: [
        {text: "维修", value: "维修"},
        {text: "保养", value: "保养"}
    ]
});
Ext.define('PlaceManage.view.PlaceDefendAddView', {
    extend: 'Ext.window.Window',
    xtype: 'placeDefendAddView',
    itemId: 'placeDefendAddViewId',
    title: '新增维护记录',
    frame: true,
    resizable: true,
    width: 600,
    height: 450,
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
                xtype: "combobox",
                fieldLabel: "维护类型",
                itemId: 'defendtypeId',
                name: 'defendtype',
                store: defendTypeStore,
                editable: false,
                allowBlank: false,
                displayField: "text",
                valueField: "value",
                queryMode: "local"
            },{
                itemId: 'defenduserId',
                xtype: 'textfield',
                fieldLabel: '登记人',
                allowBlank: false,
                name: 'defenduser'
            }, {
                itemId: 'phonenumId',
                xtype: 'textfield',
                fieldLabel: '电话',
                allowBlank: false,
                name: 'phonenum'
            },{
                itemId: 'defendtimeId',
                xtype: 'textfield',
                fieldLabel: '时间',
                allowBlank: false,
                name: 'defendtime'
            },{
                itemId: 'defendcostId',
                xtype: 'textfield',
                fieldLabel: '费用',
                allowBlank: false,
                name: 'defendcost'
            },{
                itemId: 'remarkId',
                xtype: 'textarea',
                fieldLabel: '备注',
                name: 'remark'
            }
        ]
    }],
    buttons: [
        {text: '提交', itemId: 'defendSubmit'},
        {text: '关闭', itemId: 'defendClose'}
    ]
});
