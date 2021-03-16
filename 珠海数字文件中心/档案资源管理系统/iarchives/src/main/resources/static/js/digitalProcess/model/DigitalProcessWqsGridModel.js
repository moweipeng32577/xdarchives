Ext.define('DigitalProcess.model.DigitalProcessWqsGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'batchcode', type: 'string'},
        {name: 'archivecode', type: 'string'},
        {name: 'workstate', type: 'string'},
        {name: 'lendstate', type: 'string'},
        {name: 'checkstate', type: 'string'},
        {name: 'scanstate', type: 'string'},
        {name: 'checkcount', type: 'string'},
        {name: 'picturestate', type: 'string'},
        {name: 'businesssigner', type: 'string'},
        {name: 'businesssigncode', type: 'string'},
        {name: 'signtime', type: 'string'},
        {name: 'entrysigner', type: 'string'},
        {name: 'entrysigncode', type: 'string'},
        {name: 'entrysigntime', type: 'string'},
        {name: 'entrysignorgan', type: 'string'},
        {name: 'a0', type: 'string'},
        {name: 'a1', type: 'string'},
        {name: 'a2', type: 'string'},
        {name: 'a3', type: 'string'},
        {name: 'a4', type: 'string'},
        {name: 'za4', type: 'string'}
    ]
});