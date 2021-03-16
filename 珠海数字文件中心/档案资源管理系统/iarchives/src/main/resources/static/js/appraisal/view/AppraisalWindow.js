/**
 * Created by yl on 2017/10/27.
 */
Ext.define('Appraisal.view.AppraisalWindow', {
    extend: 'Ext.window.Window',
    xtype: 'appraisalWindow',
    title: '鉴定',
    width: 280,
    height: 210,
    modal: true,

    resizable: false,
    layout: {
        type: 'vbox',
        align: 'center'
    },
    items: [
        {
            xtype: 'displayfield',
            fieldLabel: '文件日期',
            labelWidth: 120,
            width: 220,
            height:30,
            itemId:'filedate'
        },
        {
            xtype: 'displayfield',
            fieldLabel: '原保管期限',
            labelWidth: 120,
            width: 220,
            height:30,
            itemId:'yretention'
        },
        {
            fieldLabel: '保管期限',
            itemId: 'approvaldateID',
            xtype: 'combobox',
            editable: false,
            allowBlank: false,
            forceSelection: true,
            displayField: 'code',
            valueField: 'code',
            queryMode: 'local',
            store: Ext.create('Ext.data.Store', {
                proxy: {
                    type: 'ajax',
                    extraParams: {
                        value: ''
                    },
                    url: '/systemconfig/enums',
                    reader: {
                        type: 'json'
                    }
                },
                autoLoad: true
            }),
            labelWidth: 80,
            width: 220
        }
    ],
    buttons: [{
        itemId: 'appraisalWinSaveBtnID',
        text: '保存'
    }, {
        itemId: 'appraisalWinCloseBtnID',
        text: '返回'
    }]
});