var codecontentMode = Ext.create("Ext.data.Store",{
    fields:["text","value"],
    data:[
        {text:"题名",value:"题名"},
        {text:"目录号",value:"目录号"},
        {text:"归档年度",value:"归档年度"}
    ]
});

Ext.define('Inware.view.ImportSetCodeView', {
    labelid: '',
    extend: 'Ext.window.Window',
    xtype: 'importSetCodeView',
    itemId: 'importSetCodeViewId',
    frame: true,
    resizable: true,
    flag: '',
    title: '选择入库匹配字段',
    width: 800,
    modal: true,
    closeToolText: '关闭',
    preview: '',//用于保存次层view
    impType: '',//用于保存单选参数
    items: [
        {
            xtype: 'form',
            modelValidation: true,
            bodyPadding: '20 30 10 70',
            layout: 'column',
            items: [{
                columnWidth: 1,
                fieldLabel: '匹配字段',
                xtype: 'tagfield',
                name: 'codecontent',
                itemId:'codecontentId',
                labelWidth: 85,
                allowBlank: false,
                afterLabelTextTpl: [
                    '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
                ],
                store: codecontentMode,
                reference: 'states',
                displayField: 'text',
                valueField: 'value',
                filterPickList: true,
                queryMode: 'local',
                publishes: 'value',
                margin: '10 0 0 0'
            }]
        }
    ],
    buttons: [
        {text: '确定', itemId: 'setCode'},
        {
            text: '取消',
            itemId: 'setCodeClose',
            handler: function (btn) {
                btn.up('window').close();
            }
        }
    ]
});