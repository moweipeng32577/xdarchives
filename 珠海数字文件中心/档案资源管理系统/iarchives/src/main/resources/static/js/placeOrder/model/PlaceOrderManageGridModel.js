/**
 * Created by Administrator on 2020/4/28.
 */


Ext.define('PlaceOrder.model.PlaceOrderManageGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'floor', type: 'string'},
        {name: 'placedesc', type: 'string'},
        {name: 'state', type: 'string'},
        {name: 'remark', type: 'string'}
    ]
});
