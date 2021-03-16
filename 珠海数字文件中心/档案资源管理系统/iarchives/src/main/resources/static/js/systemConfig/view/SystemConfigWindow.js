/**
 * Created by tanly on 2017/11/2 0002.
 */

Ext.define('SystemConfig.view.SystemConfigWindow', {
    extend: 'Ext.window.Window',
    xtype: 'systemConfigWindow',
    itemId: 'systemConfigWindowid',
    title: '增加参数',
    width: 750,
    height: 240,
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
        margin: '25',
        modelValidation: true,
        trackResetOnLoad:true,
        items: [{
            fieldLabel: '',
            name: 'configid',
            hidden: true
        }, {
            fieldLabel: '',
            name: 'sequence',
            hidden: true
        }, {
            fieldLabel: '',
            name: 'parentconfigid',
            hidden: true
        }, {
            xtype: 'textfield',
            fieldLabel: '参数名称',
            allowBlank: false,
            name: 'code'
        }, {
            xtype: 'textfield',
            fieldLabel: '参数值',
            name: 'value',
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
