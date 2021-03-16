/**
 * Created by Administrator on 2020/3/4.
 */
Ext.define('Reservation.view.RestitutionCancelFormView', {
    extend: 'Ext.form.Panel',
    xtype: 'restitutionCancelFormView',
    itemId: 'restitutionCancelFormViewId',
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
            itemId:'cancelerId',
            fieldLabel: '取消人',
            name: 'canceler',
            allowBlank: false,
            editable: false,
            margin: '5 0 0 0'
        }, {
            columnWidth: .03,
            value:'<label style="color:#ff0b23;!important;">*</label>',
            margin: '5 0 0 1',
            xtype: 'displayfield'
        }, {
            columnWidth: .47,
            xtype: 'textfield',
            name: 'canceltime',
            fieldLabel: '取消时间',
            format: 'Y-m-d H:i:s',
            editable: false,
            margin: '5 0 0 0'
        }, {
            columnWidth: .03,
            xtype: 'displayfield'
        }, {
            columnWidth: .47,
            xtype: 'textfield',
            name:'yystate',
            itemId:'yystateId',
            fieldLabel: '状态',
            editable: false,
            margin: '10 0 0 0'
        },{
            columnWidth: .03,
            xtype: 'displayfield'
        }],
    buttons: [
        {text: '提交', itemId: 'CancelformSubmit'},
        {text: '返回', itemId: 'CancelformClose'}
    ]
});