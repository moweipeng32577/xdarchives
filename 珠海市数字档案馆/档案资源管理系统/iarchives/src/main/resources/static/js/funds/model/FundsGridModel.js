/**
 * Created by RonJiang on 2018/4/8 0008.
 */
Ext.define('Funds.model.FundsGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string',mapping:'fundsid'},
        {name: 'fundsname', type: 'string'},
        {name: 'fundsnameformername', type: 'string'},
        {name: 'fundsstarttime', type: 'string'},
        {name: 'fundsendtime', type: 'string'},
        {name: 'funds', type: 'string'},
        {name: 'contactorgan', type: 'string'},
        {name: 'fundsguidedoc', type: 'string'}
    ]
});