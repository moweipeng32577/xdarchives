/**
 * Created by Administrator on 2020/6/4.
 */


Ext.define('Outware.model.StBorrowdocDetailGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string',mapping:'entryid'},
        {name: 'entrystorage', type: 'string'},//borrowmsgid
        {name: 'title', type: 'string'},
        {name: 'filenumber', type: 'string'},
        {name: 'archivecode', type: 'string'},
        {name: 'funds', type: 'string'},
        {name: 'catafog', type: 'string'},
        {name:'pages',type:'string'},//归还状态
        {name:'type',type:'string'}//查档类型
    ]
});
