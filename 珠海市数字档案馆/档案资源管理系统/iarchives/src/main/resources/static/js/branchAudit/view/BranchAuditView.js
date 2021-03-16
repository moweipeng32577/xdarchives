/**
 * Created by Administrator on 2020/9/17.
 */
Ext.define('BranchAudit.view.BranchAuditView', {
    extend: 'Ext.Panel',
    xtype: 'branchAuditView',
    layout:'card',
    activeItem:0,
    items: [{
        itemId:'thematicProdGridViewID',
        xtype:'thematicProdGridView'
    },{
        xtype:'thematicProdDetailGridView'
    }]

});
