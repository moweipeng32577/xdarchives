/**
 * Created by yl on 2017/10/23.
 */
Ext.define('Log.view.LogShowInfo',{
    extend:'Ext.window.Window',
    xtype:'logShowInfo',
    title: '查看日志信息',
    width: 760,
    height: 360,
    minWidth: 760,
    minHeight: 360,
    layout: 'fit',
    autoShow: true,
    modal: true,
    resizable: false,//是否可以改变窗口大小
    closeToolText:'关闭',
    items: [{
        xtype: 'form',
        itemID:'logFormID',
        autoScroll: true,
        layout: {
            type: 'vbox',
            align: 'stretch'
        },
        fieldDefaults: {
            labelWidth: 80
        },
        bodyPadding: 15,
        items: [{
            layout: 'column',
            items: [{
                columnWidth: .5,
                items: [{
                    xtype: 'textfield',
                    name:'operate_user',
                    fieldLabel: '操作人',
                    editable: false,
                    blankText: '帐号不能为空',
                    style: 'width: 90%'
                }]
            }, {
                columnWidth: .5,
                items: [{
                    xtype: 'textfield',
                    fieldLabel: '模块',
                    name:'module',
                    editable: false,
                    style: 'width: 100%'
                }]
            }, {
            	columnWidth: .5,
                items: [{
                    xtype: 'textfield',
                    name:'realname',
                    fieldLabel: '用户名',
                    editable: false,
                    style: 'width: 90%'
                }]
            }, {
                columnWidth: .5,
                items: [{
                    xtype: 'textfield',
                    fieldLabel: '机构',
                    name:'organ',
                    editable: false,
                    style: 'width: 100%'
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: .5,
                items: [{
                    xtype: 'textfield',
                    fieldLabel: 'ip地址',
                    name:'ip',
                    editable: false,
                    style: 'width: 90%'
                }]
            }, {
                columnWidth: .5,
                items: [{
                    xtype: 'textfield',
                    fieldLabel: '操作时间',
                    name:'startTime',
                    editable: false,
                    style: 'width: 100%'
                }]
            }]
        }, {
            xtype: 'textarea',
            fieldLabel: '功能描述',
            name:'desci',
            editable: false,
            height: 100
        }]
    }],
    buttons: [{
        itemId:'logShowInfoBackID',
        text: '返回'
    }]
});