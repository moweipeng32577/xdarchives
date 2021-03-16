/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Workflow.store.NodeQxSelectStore',{
    extend:'Ext.data.Store',
    xtype:'nodeQxSelectStore',
    model:'Workflow.model.NodeQxSelectModel',
    idProperty: 'id',
    fields: ['id','text'],
    proxy: {
        type: 'ajax',
        url: '/workflow/getNodeQx',
        reader: {
            type: 'json'
        }
    }
});
