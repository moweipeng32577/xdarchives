/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Workflow.model.WorkflowSelectModel', {
    extend: 'Ext.data.Model',
    xtype:'workflowSelectModel',
    fields: [{name: 'id', type: 'string',mapping:'userid'},
        {name: 'realname', type: 'string'}]
});
