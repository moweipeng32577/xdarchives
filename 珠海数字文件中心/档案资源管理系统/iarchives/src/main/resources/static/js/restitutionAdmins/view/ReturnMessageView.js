/**
 * Created by Leo on 2020/8/10 0010.
 */
Ext.define('Restitution.view.ReturnMessageView', {
    extend: 'Ext.window.Window',
    xtype: 'ReturnMessage',
    itemId: 'returnMessageViewId',
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
                    name: 'returnMan',
                    xtype: 'textfield',
                    allowBlank: 'false',
                    fieldLabel: '入库人',
                    itemId:'returnMan',
                    emptyText: '请输入...',
                    //value:'请输入...',
                    style: 'width: 90%'
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