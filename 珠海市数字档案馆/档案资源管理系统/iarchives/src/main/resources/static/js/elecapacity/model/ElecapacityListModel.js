/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Elecapacity.model.ElecapacityListModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'organ', type: 'string'},
        {name: 'funds', type: 'string'},
        {name: 'count', type: 'string'},
        {name: 'size', type: 'string'}
    ]
});