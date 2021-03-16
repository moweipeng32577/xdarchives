/**
 * Created by tanly on 2017/11/2 0002.
 */

Ext.define('AccreditMetadata.view.AccreditMetadataWindow', {
    extend: 'Ext.window.Window',
    xtype: 'accreditMetadataWindow',
    itemId: 'accreditMetadataWindowid',
    title: '增加授权元数据',
    width: 750,
    height: 300,
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
        trackResetOnLoad: true,
        items: [{
            fieldLabel: '',
            name: 'aid',
            hidden: true
        }, {
            fieldLabel: '',
            name: 'sortsequence',
            hidden: true,
            value:'1'
        }, {
            fieldLabel: '',
            name: 'parentid',
            hidden: true
        },{
            fieldLabel: '',
            name: 'publishtime',
            hidden: true
        }, {
            xtype: 'textfield',
            fieldLabel: '授权标识符',
            allowBlank: false,
            name: 'shortname'
        }, {
            xtype: 'textfield',
            fieldLabel: '授权名称',
            allowBlank: false,
            name: 'fullname'
        }, {
            xtype: 'textfield',
            fieldLabel: '授权类型',
            allowBlank: false,
            name: 'atype'
        }/*, {
            xtype: 'textfield',
            fieldLabel: '参数值',
            name: 'text',
            allowBlank: false
        }*/]
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
