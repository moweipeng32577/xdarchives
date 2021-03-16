/**
 * Created by Administrator on 2019/5/23.
 */


Ext.define('ElectronPrintApprove.view.ElectronPrintApproveView', {
    extend: 'Ext.panel.Panel',
    xtype: 'electronPrintApproveView',
    layout: 'border',
    items: [{xtype:'electronPrintApproveGridView'},{xtype:'electronPrintApproveFormView'}]
});
