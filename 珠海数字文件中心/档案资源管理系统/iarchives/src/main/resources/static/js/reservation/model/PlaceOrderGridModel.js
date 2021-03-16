/**
 * Created by Administrator on 2020/4/28.
 */


Ext.define('Reservation.model.PlaceOrderGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'starttime', type: 'string'},
        {name: 'endtime', type: 'string'},
        {name: 'placeuser', type: 'string'},
        {name: 'phonenumber', type: 'string'},
        {name: 'ordertime', type: 'string'},
        {name: 'useway', type: 'string'},
        {name: 'state', type: 'string'},
        {name: 'cancelreason', type: 'string'}
    ]
});
