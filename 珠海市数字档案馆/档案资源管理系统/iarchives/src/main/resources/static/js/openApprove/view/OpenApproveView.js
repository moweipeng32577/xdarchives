/**
 * Created by tanly on 2017/12/5.
 */
Ext.define('OpenApprove.view.OpenApproveView', {
    extend: 'Ext.panel.Panel',
    xtype: 'openApproveView',
    layout: 'border',
    items: [{xtype:'openApproveGridView'},{xtype:'openApproveFormView'}]
});