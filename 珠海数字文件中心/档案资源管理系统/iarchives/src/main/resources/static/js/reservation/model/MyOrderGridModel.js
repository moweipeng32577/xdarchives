/**
 * Created by Administrator on 2020/4/27.
 */


Ext.define('Reservation.model.MyOrderGridModel',{
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
        {name: 'returnstate', type: 'string'},
        {name: 'cancelreason', type: 'string'}
    ]
});
