/**
 * Created by Administrator on 2020/4/21.
 */


Ext.define('CarOrder.model.OrderManageGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'starttime', type: 'string'},
        {name: 'endtime', type: 'string'},
        {name: 'caruser', type: 'string'},
        {name: 'phonenumber', type: 'string'},
        {name: 'ordertime', type: 'string'},
        {name: 'useway', type: 'string'},
        {name: 'state', type: 'string'},
        {name: 'cancelreason', type: 'string'}
    ]
});
