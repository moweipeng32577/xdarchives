/**
 * Created by yl on 2017/11/3.
 */
Ext.define('Restitution.view.RestitutionSqView', {
    extend: 'Ext.window.Window',
    xtype: 'restitutionSqView',
    height: '100%',
    width: '100%',
    header: false,
    draggable: false,//禁止拖动
    resizable: false,//禁止缩放
    modal: true,
    closeToolText:'关闭',
    closeAction: 'hide',
    layout: 'border',
    split:true,
    items: [
        {xtype: 'restitutionFormItemView'},
        {xtype: 'restitutionFormGridView'}
        ]
});