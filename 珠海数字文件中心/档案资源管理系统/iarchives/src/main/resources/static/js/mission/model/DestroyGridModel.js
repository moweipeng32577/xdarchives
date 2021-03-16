/**
 * Created by yl on 2017/12/6.
 */
Ext.define('Mission.model.DestroyGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'text', type: 'string'},
        {name: 'state', type: 'string'},
        {name: 'type', type: 'string'}
    ]
});