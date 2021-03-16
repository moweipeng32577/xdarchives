/**
 * Created by tanly on 2017/11/1 0024.
 */
Ext.define('SystemConfig.model.SystemConfigGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string', mapping: 'configid'},
        {name: 'configcode', type: 'string', mapping:'code'},
        {name: 'configvalue', type: 'string', mapping:'value'},
        {name: 'sequence', type: 'number'}
    ]
});