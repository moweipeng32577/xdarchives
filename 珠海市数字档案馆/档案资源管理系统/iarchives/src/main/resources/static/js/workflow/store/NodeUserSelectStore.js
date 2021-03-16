/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Workflow.store.NodeUserSelectStore',{
    extend:'Ext.data.Store',
    xtype:'nodeUserSelectStore',
    model:'Workflow.model.WorkflowSelectModel',
    idProperty: 'userid',
    fields: ['userid','realname'],
    proxy: {
        type: 'ajax',
        url: '/workflow/getWorkUser',
        reader: {
            type: 'json'
        }
    }
});
