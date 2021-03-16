/**
 * Created by yl on 2017/11/29.
 */
Ext.define('DestructionBill.view.DestructionBillDetailView', {
    extend: 'Ext.window.Window',
    xtype: 'destructionBillDetailView',
    title: '查看单据',
    width: '100%',
    height: '100%',
    closeAction: 'hide',
    closeToolText:'关闭',
    draggable : false,
    layout: 'border',
    items:[{xtype:'destructionBillInfoView'},{xtype:'destructionBillDetailGridView'}]
});