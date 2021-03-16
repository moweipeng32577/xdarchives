/**
 * Created by yl on 2017/11/7.
 */
Ext.define('ExchangeStorage.model.ExchangeStorageDetailGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'title', type: 'string'},
        {name: 'funds', type: 'string'},
        {name: 'catalog', type: 'string'},
        {name: 'filenumber', type: 'string'},
        {name: 'entryretention', type: 'string'},
        {name: 'filedate', type: 'string'},
        {name: 'filingyear', type: 'string'}
    ]
});