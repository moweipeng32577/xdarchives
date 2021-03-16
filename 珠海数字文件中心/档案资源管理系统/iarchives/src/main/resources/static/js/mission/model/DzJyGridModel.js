/**
 * Created by Administrator on 2018/10/23.
 */

Ext.define('Mission.model.DzJyGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'text', type: 'string'},
        {name: 'state', type: 'string'},
        {name: 'type', type: 'string'}
    ]
});
