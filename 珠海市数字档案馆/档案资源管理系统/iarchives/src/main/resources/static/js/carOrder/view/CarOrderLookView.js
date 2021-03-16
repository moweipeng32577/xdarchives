/**
 * Created by Administrator on 2020/4/23.
 */


Ext.define('CarOrder.view.CarOrderLookView', {
    extend: 'Ext.window.Window',
    xtype: 'carOrderLookView',
    height: '100%',
    width: '100%',
    draggable: false,//禁止拖动
    resizable: false,//禁止缩放
    modal: true,
    closeToolText:'关闭',
    title: '查看预约',
    closeAction: 'hide',
    closable:false,
    requires: [
        'Ext.layout.container.Border'
    ],
    layout: 'border',
    items: [{xtype: 'carOrderLookFromView'}, {xtype: 'carOrderLookGridView'}]
});
