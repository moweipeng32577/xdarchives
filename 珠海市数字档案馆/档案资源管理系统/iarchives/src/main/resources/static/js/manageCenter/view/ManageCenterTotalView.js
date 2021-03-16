/**
 * Created by Administrator on 2020/7/21.
 */


Ext.define('ManageCenter.view.ManageCenterTotalView', {
    extend: 'Ext.form.Panel',
    xtype: 'manageCenterTotalView',
    itemId: 'manageCenterTotalViewId',
    autoScroll: true,
    layout: 'fit',
    items: [{
        xtype: 'form',
        layout: 'column',
        bodyPadding: 20,
        items: [{
            xtype: 'fieldset',
            title: "总计",
            layout: 'column',
            columnWidth: 1,
            margin: '10 0 0 0',
            items: [
                {
                    columnWidth: .3,
                    fieldLabel: '电子文件',
                    xtype: 'textfield',
                    name: 'elefile',
                    readOnly: true,
                    labelWidth: 100
                }, {
                    columnWidth: .05,
                    xtype: 'displayfield'
                }, {
                    columnWidth: .3,
                    fieldLabel: '电子档案',
                    xtype: 'textfield',
                    name: 'elearchive',
                    readOnly: true,
                    labelWidth: 100
                }, {
                    columnWidth: .05,
                    xtype: 'displayfield'
                }, {
                    columnWidth: .3,
                    xtype: 'textfield',
                    name: 'transfernum',
                    fieldLabel: '移交数量',
                    readOnly: true,
                    labelWidth: 100
                }
            ]
        }, {
            xtype: 'fieldset',
            title: "归档总计",
            layout: 'column',
            columnWidth: 1,
            margin: '20 0 0 0',
            items: [{
                columnWidth: .47,
                fieldLabel: '未归数据',
                xtype: 'textfield',
                name: 'unfillingnum',
                readOnly: true,
                labelWidth: 100
            }, {
                columnWidth: .06,
                xtype: 'displayfield'
            }, {
                columnWidth: .47,
                xtype: 'textfield',
                name: 'fillingnum',
                fieldLabel: '归档未移交数据',
                readOnly: true,
                labelWidth: 100
            }]
        }, {
            xtype: 'fieldset',
            title: "接收与归档总计",
            layout: 'column',
            columnWidth: 1,
            margin: '20 0 0 0',
            items: [{
                columnWidth: .3,
                fieldLabel: '当天接收合计',
                xtype: 'textfield',
                name: 'receiveday',
                readOnly: true,
                labelWidth: 100
            }, {
                columnWidth: .05,
                xtype: 'displayfield'
            }, {
                columnWidth: .3,
                fieldLabel: '当月接收合计',
                xtype: 'textfield',
                name: 'receivemonth',
                readOnly: true,
                labelWidth: 100
            }, {
                columnWidth: .05,
                xtype: 'displayfield'
            }, {
                columnWidth: .3,
                fieldLabel: '最后接收时间',
                xtype: 'textfield',
                name: 'lastreceivetime',
                readOnly: true,
                labelWidth: 100
            }, {
                columnWidth: 1,
                xtype: 'displayfield'
            }, {
                columnWidth: .3,
                xtype: 'textfield',
                name: 'fillingday',
                fieldLabel: '当日归档数据',
                readOnly: true,
                labelWidth: 100
            }, {
                columnWidth: .05,
                xtype: 'displayfield'
            }, {
                columnWidth: .3,
                xtype: 'textfield',
                name: 'fillingmonth',
                fieldLabel: '当月归档数据',
                readOnly: true,
                labelWidth: 100
            }, {
                columnWidth: .05,
                xtype: 'displayfield'
            }, {
                columnWidth: .3,
                fieldLabel: '最后归档时间',
                xtype: 'textfield',
                name: 'lastfillingtime',
                readOnly: true,
                labelWidth: 100
            }]
        }
        ]
    }
    ]
});
