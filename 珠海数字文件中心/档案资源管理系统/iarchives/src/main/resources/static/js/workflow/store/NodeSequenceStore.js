/**
 * Created by luzc on 2020/6/16.
 */
Ext.define('Workflow.store.NodeSequenceStore',{
    extend:'Ext.data.Store',
    model:'Workflow.model.WorkflowGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/workflow/getWorkNodeBySequence',
        //extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});