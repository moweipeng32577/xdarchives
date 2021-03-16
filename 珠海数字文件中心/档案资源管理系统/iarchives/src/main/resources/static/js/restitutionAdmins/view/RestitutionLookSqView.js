/**
 * Created by SunK on 2018/10/26 0026.
 */
Ext.define('Restitution.view.RestitutionLookSqView', {
    extend: 'Ext.window.Window',
    xtype: 'restitutionLookSqView',
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
        {xtype: 'restitutionLookItemView'},
        {xtype: 'restitutionLookGridView'}
    ]
});