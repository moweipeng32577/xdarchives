/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Workflow.model.WorkflowGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'text', type: 'string'},
        {name: 'desci', type: 'string'},
        {name: 'orders', type: 'int'},
        {name: 'nexttext', type: 'string'}
    ]
});