/**
 * Created by tanly on 2017/11/2 0002.
 */

Ext.define('MetadataTemplate.view.AddGroupFieldWindow', {
    extend: 'Ext.window.Window',
    xtype: 'addGroupFieldWindow',
    itemId: 'addGroupFieldWindow',
    title: '增加参数',
    width: 750,
    height: 240,
    modal: true,
    closeToolText: '关闭',
    preview:'',
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
        margin: '25',
        modelValidation: true,
        trackResetOnLoad:true,
        items: [{
            fieldLabel: '',
            name: 'groupid',
            hidden: true
        }, {
            xtype: 'textfield',
            fieldLabel: '组名',
            allowBlank: false,
            name: 'groupname'
        }, {
            xtype: 'textfield',
            fieldLabel: '组描述',
            name: 'grouptext',
            allowBlank: false
        }
        ]
    }]
    ,
    buttons: [{
        text: '保存',
        itemId: 'save'
    }, {
        text: '取消',
        itemId: 'cancel'
    }
    ]
});
