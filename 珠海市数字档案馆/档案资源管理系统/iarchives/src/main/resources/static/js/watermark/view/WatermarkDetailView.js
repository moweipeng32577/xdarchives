/**
 * Created by tanly on 2017/11/2 0002.
 */

Ext.define('Watermark.view.WatermarkDetailView', {
    extend: 'Ext.window.Window',
    xtype: 'WatermarkDetailView',
    itemId: 'WatermarkDetailViewid',
    title: '增加机构',
    width: 400,
    height: 450,
    modal: true,
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
        margin: '22',
        modelValidation: true,
        items: [{
            name: 'organid',
            hidden: true
        }, {
            name: 'servicesid',
            hidden: true
        }, {
            name: 'systemid',
            hidden: true
        }, {
            name: 'parentid',
            hidden: true
        }, {
            fieldLabel: '机构名称',
            name: 'organname',
            afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填选项">*</span>'
            ]
        }, {
            fieldLabel: '机构类型',
            name: 'organtype'
        }, {
            fieldLabel: '服务名称',
            name: 'servicesname'
        }, {
            fieldLabel: '系统名称',
            name: 'systemname'
        }, {
            xtype: 'textfield',
            fieldLabel: '层级',
            name: 'level',
            allowBlank: false,
            regex: /^([1-9]\d*)$/,
            regexText : '请输入正整数',
            afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填选项">*</span>'
            ]
        }, {
            xtype: 'radiogroup',
            fieldLabel: '状态',
            name: 'usestatus',
            items:[{
                boxLabel: '启用',
                inputValue: '1',
                itemId:'useItemid'
            },{
                xtype:'displayfield'
            },{
                boxLabel: '停用',
                inputValue: '0'
            }]
        }, {
            xtype: 'textfield',
            fieldLabel: '备注',
            name: 'desciption'
        }
        ]
    }],
    buttons: [{
        text: '保存',
        itemid: 'organSaveBtnID'
    }, {
        text: '取消',
        itemid: 'organCancelBtnID'
    }]
});