/**
 * Created by Administrator on 2020/3/4.
 */

Ext.define('Reservation.view.RestitutionReplyFormView', {
    extend: 'Ext.form.Panel',
    xtype: 'restitutionReplyFormView',
    itemId: 'restitutionReplyFormViewId',
    region: 'center',
    autoScroll: true,
    fieldDefaults: {
        labelWidth: 120
    },
    layout: 'column',
    bodyPadding: 15,
    items: [
        {xtype: 'textfield', name: 'id', hidden: true},
        {
            columnWidth: .47,
            xtype: 'textfield',
            itemId:'replierId',
            fieldLabel: '回复人',
            name: 'replier',
            allowBlank: false,
            editable: false,
            margin: '10 0 0 0'
        }, {
            columnWidth: .03,
            value:'<label style="color:#ff0b23;!important;">*</label>',
            margin: '10 0 0 1',
            xtype: 'displayfield'
        }, {
            columnWidth: .47,
            xtype: 'textfield',
            name: 'replytime',
            fieldLabel: '回复时间',
            format: 'Y-m-d H:i:s',
            editable: false,
            margin: '10 0 0 0'
        },
        {
            columnWidth: .03,
            margin: '10 0 0 0',
            xtype: 'displayfield'
        },
        {
            columnWidth: .97,
            xtype: 'textfield',
            name:'yystate',
            fieldLabel: '状态',
            itemId:'yystateId',
            editable: false,
            margin: '10 0 0 0'
        },{
            columnWidth: .03,
            itemId:'yystateDisId',
            margin: '10 0 0 0',
            xtype: 'displayfield'
        }, {
            columnWidth: .97,
            xtype: 'textarea',
            name: 'replycontent',
            fieldLabel: '回复内容',
            allowBlank: false,
            margin: '10 0 0 0'
        }, {
            columnWidth: .03,
            value:'<label style="color:#ff0b23;!important;">*</label>',
            margin: '10 0 0 0',
            xtype: 'displayfield'
        }],
    buttons: [
        {text: '提交', itemId: 'replyformSubmit'},
        {text: '返回', itemId: 'replyformClose'}
    ]
});