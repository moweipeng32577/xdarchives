/**
 * Created by yl on 2020/5/21.
 */
Ext.define('LongRetention.view.LongRetentionSetting', {
    extend: 'Ext.window.Window',
    xtype: 'longRetentionSetting',
    itemId:'longRetentionSettingid',
    title: '验证项设置',
    width:730,
    height:500,
    draggable: false,//禁止拖动
    resizable: false,//禁止缩放
    modal: true,
    closeToolText: '关闭',
    closeAction: "hide",
    items:[{
        xtype:'form',
        margin: '5',
        items:[{
            layout: 'column',
            items:[{
                columnWidth: 1,
                layout:{
                    type:'vbox',
                    align: 'stretch'
                },
                items: [{
                    xtype:'fieldset',
                    title: '准确性',
                    style:'background:#fff;padding-top:0px',
                    autoHeight:true,
                    labelWidth:60,
                    labelAlign:'right',
                    layout:{
                        type:'hbox',
                        align: 'stretch'
                    },
                    items:[{
                        xtype: "checkbox",
                        itemId: 'authenticity1',
                        checked:true,
                        boxLabel: '目录信息准确性',
                        labelWidth:140,
                        margin: '0 10 0 0'
                    },{
                        xtype: "checkbox",
                        itemId: 'authenticity2',
                        checked: true,
                        boxLabel: '目录和电子档案内容关联准确性',
                        labelWidth:140,
                        margin: '0 10 0 0'
                    },{
                        xtype: "checkbox",
                        itemId: 'authenticity3',
                        checked:true,
                        boxLabel: '电子档案内容准确性',
                        labelWidth:140,
                        margin: '0 10 0 0'
                    },{
                        xtype: "checkbox",
                        itemId: 'authenticity4',
                        checked:true,
                        boxLabel: '电子档案封装包准确性',
                        labelWidth:140,
                        margin: '0 10 0 0'
                    }]
                }]
            },{
                columnWidth: 1,
                layout:{
                    type:'vbox',
                    align: 'stretch'
                },
                items: [{
                    xtype:'fieldset',
                    title: '完整性',
                    style:'background:#fff;padding-top:0px',
                    autoHeight:true,
                    labelWidth:60,
                    labelAlign:'right',
                    layout:{
                        type:'hbox',
                        align: 'stretch'
                    },
                    items:[{
                        xtype: "checkbox",
                        itemId: 'integrity1',
                        checked:true,
                        boxLabel: '电子档案数据总量检测',
                        labelWidth:140,
                        margin: '0 10 0 0'
                    },{
                        xtype: "checkbox",
                        itemId: 'integrity2',
                        checked:true,
                        boxLabel: '目录项目完整性检测',
                        labelWidth:140,
                        margin: '0 10 0 0'
                    },{
                        xtype: "checkbox",
                        itemId: 'integrity3',
                        checked:true,
                        boxLabel: '电子档案封装包完整性',
                        labelWidth:140,
                        margin: '0 10 0 0'
                    }]
                }]
            },{
                columnWidth: 1,
                layout:{
                    type:'vbox',
                    align: 'stretch'
                },
                items: [{
                    xtype:'fieldset',
                    title: '可用性',
                    style:'background:#fff;padding-top:0px',
                    autoHeight:true,
                    labelWidth:60,
                    labelAlign:'right',
                    layout:{
                        type:'hbox',
                        align: 'stretch'
                    },
                    items:[{
                        xtype: "checkbox",
                        itemId: 'usability1',
                        checked:true,
                        boxLabel: '目录数据可用性检测',
                        labelWidth:140,
                        margin: '0 10 0 0'
                    },{
                        xtype: "checkbox",
                        itemId: 'usability2',
                        checked:true,
                        boxLabel: '电子档案内容可用性检测',
                        labelWidth:140,
                        margin: '0 10 0 0'
                    },{
                        xtype: "checkbox",
                        itemId: 'usability3',
                        checked:true,
                        boxLabel: '电子档案内容软硬件环境监测',
                        labelWidth:140,
                        margin: '0 10 0 0'
                    }]
                }]
            },{
                columnWidth: 1,
                layout:{
                    type:'vbox',
                    align: 'stretch'
                },
                items: [{
                    xtype:'fieldset',
                    title: '安全性',
                    style:'background:#fff;padding-top:0px',
                    autoHeight:true,
                    labelWidth:60,
                    labelAlign:'right',
                    layout:{
                        type:'hbox',
                        align: 'stretch'
                    },
                    items:[{
                        xtype: "checkbox",
                        itemId: 'safety1',
                        checked:true,
                        boxLabel: '目录数据安全性检测',
                        labelWidth:140,
                        margin: '0 10 0 0'
                    },{
                        xtype: "checkbox",
                        itemId: 'safety2',
                        checked:true,
                        boxLabel: '电子档案病毒检测',
                        labelWidth:140,
                        margin: '0 10 0 0'
                    },{
                        xtype: "checkbox",
                        itemId: 'safety3',
                        checked:true,
                        boxLabel: '软件系统安全性检测',
                        labelWidth:140,
                        margin: '0 10 0 0'
                    }]
                }]
            }]
        }]
    }],
    buttons: [{
        text: '保存',
        itemId: 'saveId'
    }, {
        text: '取消', handler: function (view) {
            view.up('longRetentionSetting').close();
        }
    }]
});