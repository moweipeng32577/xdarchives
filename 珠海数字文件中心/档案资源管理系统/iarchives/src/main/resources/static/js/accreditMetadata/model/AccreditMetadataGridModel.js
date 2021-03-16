/**
 * Created by tanly on 2017/11/1 0024.
 */
Ext.define('AccreditMetadata.model.AccreditMetadataGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'aid', type: 'string', mapping: 'aid'},
        {name: 'shortname', type: 'string', mapping:'shortname'},
        {name: 'fullname', type: 'string', mapping:'fullname'},
        {name: 'atype', type: 'string', mapping:'atype'},
        {name: 'text', type: 'string', mapping:'text'},
        {name: 'publishtime', type: 'string', mapping:'publishtime'},
        {name: 'sortsequence', type: 'number', mapping:'sortsequence'}
    ]
});