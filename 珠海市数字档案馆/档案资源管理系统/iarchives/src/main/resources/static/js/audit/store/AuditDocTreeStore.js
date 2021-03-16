/**
 * Created by Administrator on 2019/10/28.
 */


Ext.define('Audit.store.AuditDocTreeStore',{
    extend:'Ext.data.TreeStore',
    model:'Audit.model.AuditDocTreeModel',
    autoLoad:true,
    proxy: {
        type: 'ajax',
        url: '/audit/getAuditDocTree',
        reader: {
            type: 'json',
            expanded: true
        }
    },
    root: {
        text: '单据审批状态',
        expanded: true
    }
});
