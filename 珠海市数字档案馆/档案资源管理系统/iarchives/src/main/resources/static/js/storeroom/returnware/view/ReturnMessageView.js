/**
 * Created by SunK on 2018/10/18 0018.
 */
Ext.define('ReturnWare.view.ReturnMessageView', {
    extend: 'Ext.window.Window',
    xtype: 'ReturnMessage',
    itemId: 'ReturnMessage',
    title: '档案归还信息',
    width: 600,
    height: 200,
    modal: true,
    closeToolText: '关闭',
    items: [{
        xtype: 'form',
        autoScroll: true,
        itemId:'form',
        layout: {
            type: 'vbox',
            align: 'stretch'
        },
        fieldDefaults: {
            labelWidth: 80
        },
        bodyPadding: 20,
        items: [{
            layout: 'column',
            items: [{
                columnWidth: 1,
                items: [{
                    name: 'userName',
                    xtype: 'textfield',
                    allowBlank: 'false',
                    fieldLabel: '入库人',
                    itemId:'userName',
                    emptyText: '请输入...',
                    //value:'请输入...',
                    style: 'width: 90%',
                    // listeners:{
                    //     focus: function(b){
                    //         //获取焦点
                    //         b.setValue("")
                    //     }
                    // }
                }]
            },{
                columnWidth: 1,
                items: [{
                    name: 'remark',
                    xtype: 'textfield',
                    allowBlank: 'false',
                    fieldLabel: '备注',
                    itemId:'remarkId',
                    emptyText: '请输入...',
                    //value:'请输入...',
                    style: 'width: 90%'
                }]
            },{
                columnWidth: 1,
                items: [{
                    name: 'entryids',
                    xtype: 'textfield',
                    allowBlank: 'false',
                    fieldLabel: '条目ID',
                    hidden:true,
                    itemId:'entryidsId',
                    emptyText: '请输入...',
                    //value:'请输入...',
                    style: 'width: 90%'
                }]
            },{
                columnWidth: 1,
                items: [{
                    name: 'nodeid',
                    xtype: 'textfield',
                    allowBlank: 'false',
                    hidden:true,
                    fieldLabel: '节点ID',
                    itemId:'nodeidId',
                    emptyText: '请输入...',
                    //value:'请输入...',
                    style: 'width: 90%'
                }]
            }]
        }]
    }],

    buttons: [{
        text: '确定',
        itemId: 'saveGh'
    }, {
        text: '取消',
        itemId: 'cancelGh'
    }
    ]
});