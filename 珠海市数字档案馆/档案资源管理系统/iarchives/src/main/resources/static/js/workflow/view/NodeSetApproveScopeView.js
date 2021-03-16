/**
 * Created by Administrator on 2019/8/28.
 */


var approveScopeStore = Ext.create("Ext.data.Store", {
    fields: ["Name", "Value"],
    data: [
        { text: "仅本单位", Value: "仅本单位" },
        { text: "支持跨单位", Value: "支持跨单位"}
    ]
});
Ext.define('Workflow.view.NodeSetApproveScopeView', {
    extend: 'Ext.window.Window',
    xtype: 'nodeSetApproveScopeView',
    itemId:'nodeSetApproveScopeViewId',
    title: '审批范围设置',
    frame: true,
    resizable: true,
    width: 300,
    minWidth: 300,
    minHeight: 100,
    modal:true,
    closeToolText:'关闭',
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
        itemId:'selectApprove',
        items: [
            {  xtype: "combobox",
                fieldLabel: "审批范围",
                itemId:'approveScope',
                margin:'15',
                store: approveScopeStore,
                editable: false,
                allowBlank: false,
                displayField: "text",
                valueField: "Value",
                queryMode: "local"
            }
        ]
    }],

    buttons: [
        { text: '完成',itemId:'setApproveScope'},
        { text: '关闭',itemId:'setApproveScopeClose'}
    ]
});
