/**
 * Created by yl on 2017/12/6.
 */
Ext.define('DestructionBill.view.DestructionBillWindows', {
    extend: 'Ext.window.Window',
    xtype:'destructionBillWindows',
    title: '查看条目及原文',
    width: '100%',
    height: '100%',
    layout:'fit',
    closeToolText:'关闭',
    items:[{xtype:'EntryFormView'}]
});