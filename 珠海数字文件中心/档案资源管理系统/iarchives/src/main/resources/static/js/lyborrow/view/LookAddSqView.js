/**
 * Created by yl on 2017/11/3.
 */
Ext.define('Borrow.view.LookAddSqView', {
    extend: 'Ext.window.Window',
    xtype: 'lookAddSqView',
    height: '100%',
    width: '100%',
    draggable: false,//禁止拖动
    resizable: false,//禁止缩放
    modal: true,
    closeToolText:'关闭',
    title: '查档申请',
    closeAction: 'hide',
    layout: 'border',
    closable:false,
    split:true,
    items: [{xtype: 'lookAddFormView'}, {xtype: 'lookAddFormGridView'}]
});