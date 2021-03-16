/**
 * Created by Administrator on 2019/5/17.
 */

Ext.define('PavilionSearch.view.ApplyPrintAddView', {
    extend: 'Ext.window.Window',
    xtype: 'applyPrintAddView',
    height: '100%',
    width: '100%',
    draggable: false,//禁止拖动
    resizable: false,//禁止缩放
    modal: true,
    closeToolText:'关闭',
    title: '打印申请',
    closeAction: 'hide',
    closable:false,
    requires: [
        'Ext.layout.container.Border'
    ],
    layout: 'border',
    items: [{xtype: 'applyPrintView'}, {xtype: 'applyPrintGridView'}]
});
