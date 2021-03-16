/**
 * Created by yl on 2017/10/26.
 */
Ext.define('ExchangeTransfer.model.ExchangeTransferGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string', mapping: 'exchangeid'},
        {name: 'filename', type: 'string'},
        {name: 'filemd5', type: 'string'},
        {name: 'filesize', type: 'string'},
        {name: 'filetime', type: 'string'}
    ]
});