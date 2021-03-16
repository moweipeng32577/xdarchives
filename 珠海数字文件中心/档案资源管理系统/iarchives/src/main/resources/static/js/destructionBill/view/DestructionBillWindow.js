/**
 * Created by yl on 2017/12/6.
 */
Ext.define('DestructionBill.view.DestructionBillWindow', {
    extend: 'Ext.window.Window',
    xtype:'destructionBillWindow',
    title: '查看条目及原文',
    width: '100%',
    height: '100%',
    layout:'fit',
    closeToolText:'关闭',
    items:[{xtype:'destructionbillEntryFormView'}]
});