/**
 * Created by Administrator on 2020/4/23.
 */


Ext.define('PlaceOrder.view.PlaceOrderLookFromView', {
    extend: 'Ext.panel.Panel',
    xtype: 'placeOrderLookFromView',
    itemId: 'placeOrderLookFromViewId',
    height: '55%',
    layout: 'fit',
    region: 'center',
    autoScroll: true,
    items: [{
        xtype: 'form',
        layout: 'column',
        autoScroll: true,
        bodyPadding: 16,
        fieldDefaults: {
            labelWidth: 70
        },
        items: [
            {
                columnWidth: .3,
                xtype: 'label',
                text: '温馨提示：红色外框表示输入非法数据！',
                style: {
                    color: 'red',
                    'font-size': '16px'
                },
                margin: '10 0 0 0'
            }, {
                columnWidth: .6,
                xtype: 'displayfield'
            }, {
                columnWidth: .47,
                fieldLabel: '预约人',
                itemId:'placeuserId',
                xtype: 'textfield',
                name: 'placeuser',
                labelWidth: 85,
                margin: '10 0 0 0',
                afterLabelTextTpl: [
                    '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>']
            },{
                columnWidth:.06,
                xtype:'displayfield'
            },{
                columnWidth: .47,
                fieldLabel: '单位',
                xtype: 'textfield',
                name: 'userorgan',
                labelWidth: 85,
                margin: '10 0 0 0',
                afterLabelTextTpl: [
                    '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>']
            },{
                columnWidth: .47,
                fieldLabel: '联系电话',
                xtype: 'textfield',
                name: 'phonenumber',
                labelWidth: 85,
                margin: '10 0 0 0',
                regex :/(^1[0-9]{10}$)/,
                regexText   : "电话格式不正确！",
                afterLabelTextTpl: [
                    '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>']
            },{
                columnWidth:.06,
                xtype:'displayfield'
            },{
                columnWidth: .47,
                xtype: 'textfield',
                itemId: 'usewayId',
                name: 'useway',
                fieldLabel: '使用用途',
                labelWidth: 85,
                margin: '10 0 0 0',
                afterLabelTextTpl: [
                    '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>']
            },{
                columnWidth: .47,
                fieldLabel: '预约时间',
                name: 'ordertime',
                xtype: 'textfield',
                labelWidth: 85,
                editable: false,
                margin:'10 0 0 0',
                afterLabelTextTpl: [
                    '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>']
            },{
                columnWidth:.06,
                xtype:'displayfield'
            },{
                columnWidth: .23,
                fieldLabel: '开始时间',
                xtype: 'datetimefield',
                name: 'starttime',
                format: 'Y-m-d H:i',
                labelWidth: 85,
                editable: false,
                margin:'10 0 0 0'
            }, {
                columnWidth:.01,
                xtype:'displayfield'
            },{
                columnWidth: .23,
                fieldLabel: '结束时间',
                xtype: 'datetimefield',
                format: 'Y-m-d H:i',
                name: 'endtime',
                labelWidth: 85,
                editable: false,
                margin:'10 0 0 0'
            },{
                columnWidth:.47,
                itemId: 'canceluserId',
                xtype: 'textfield',
                fieldLabel: '取消人',
                hidden:true,
                labelWidth: 85,
                margin: '10 0 0 0',
                name: 'canceluser'
            },{
                columnWidth:.06,
                itemId: 'cancedisId',
                hidden:true,
                margin: '10 0 0 0',
                xtype:'displayfield'
            },{
                columnWidth:.47,
                itemId: 'canceltimeId',
                xtype: 'textfield',
                fieldLabel: '取消时间',
                hidden:true,
                labelWidth: 85,
                margin: '10 0 0 0',
                name: 'canceltime'
            },{
                columnWidth:1,
                itemId: 'cancelreasonId',
                xtype: 'textarea',
                fieldLabel: '取消原因',
                hidden:true,
                labelWidth: 85,
                margin: '10 0 0 0',
                name: 'cancelreason'
            },{
                columnWidth: 1,
                xtype: 'textarea',
                fieldLabel: '备注',
                labelWidth: 85,
                name: 'remark',
                margin: '10 0 0 0'
            }
        ]
    }],
    buttons: [
        {text: '关闭', itemId: 'lookOrderClose'}
    ]
});

