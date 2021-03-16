/**
 * Created by Administrator on 2019/10/28.
 */


Ext.define('Audit.view.AuditAdminView', {
    extend:'Ext.panel.Panel',
    xtype:'auditAdminView',
    layout: 'border',
    items:[{
            xtype:'auditDocTreeView',
            bodyBorder: false
        },{
            xtype:'auidtAdminGridView'
        }]
});
