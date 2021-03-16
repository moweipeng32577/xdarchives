/**
 * Created by Administrator on 2020/4/21.
 */


Ext.define('CarOrder.model.CarOrderManageGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'carnumber', type: 'string'},
        {name: 'cartype', type: 'string'},
        {name: 'state', type: 'string'},
        {name: 'remark', type: 'string'}
    ]
});
