/**
 * Created by yl on 2017/11/3.
 */
Ext.define('BillApproval.view.BillApprovalView', {
    extend: 'Ext.panel.Panel',
    xtype: 'billApprovalView',
    layout: 'border',
    items: [{xtype:'billApprovalGridView'},{xtype:'billApprovalFormView'}]
});