Ext.define('DigitalProcess.view.DigitalProcessRecordForm', {
    extend: 'Ext.window.Window',
    xtype: 'DigitalProcessRecordForm',
    itemId:'DigitalProcessRecordForm',
    title: '选择节点',
    frame: true,
    resizable: true,
    closeToolText:'关闭',
    width: 610,
    minWidth: 610,
    minHeight: 150,
    modal:true,
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

    items: [
        {
            xtype: 'form',
            modelValidation: true,
            margin: '15',
            items: [
                {
                    columnWidth:1,
                    xtype: 'TreeComboboxView',
                    //fieldLabel: '拆件到分类',
                    fieldLabel: '节点',
                    editable: false,
                    url: '/nodesetting/getSzhWCLNodeByParentId',
                    extraParams: {pcid:''},//根节点的ParentNodeID为空，故此处传入参数为空串
                    allowBlank: false,
                    name: 'nodename',
                    itemId: 'dismantleNode',
                    margin:'20 20 5 10',
                    allowBlack:false,
                    afterLabelTextTpl: [
                        '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
                    ]
                }
            ]
        },

    ],

    buttons: [
        { text: '提交',itemId:'submit'},
        { text: '关闭',itemId:'close'}
    ]
});