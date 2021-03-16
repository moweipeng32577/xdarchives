/**
 * Created by tanly on 2017/12/7 0007.
 */
Ext.define('Mission.model.OpenGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'text', type: 'string'},
        {name: 'state', type: 'string'},
        {name: 'type', type: 'string'}
    ]
});