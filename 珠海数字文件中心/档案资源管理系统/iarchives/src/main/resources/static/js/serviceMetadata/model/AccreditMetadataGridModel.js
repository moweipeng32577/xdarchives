/**
 * Created by tanly on 2017/11/1 0024.
 */
Ext.define('ServiceMetadata.model.AccreditMetadataGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'cid', type: 'string', mapping: 'cid'},
        {name: 'operation', type: 'string', mapping:'operation'},
        {name: 'mstatus', type: 'string', mapping:'mstatus'},
        {name: 'operationmsg', type: 'string', mapping:'operationmsg'},
        {name: 'shortname', type: 'string', mapping:'shortname'},
        {name: 'sortsequence', type: 'number', mapping:'sortsequence'}
    ]
});