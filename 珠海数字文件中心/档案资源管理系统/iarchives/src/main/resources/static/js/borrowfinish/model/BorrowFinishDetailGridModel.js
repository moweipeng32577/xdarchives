/**
 * Created by Administrator on 2018/11/28.
 */

Ext.define('Borrowfinish.model.BorrowFinishDetailGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string',mapping:'entryid'},
        {name: 'entrystorage', type: 'string'},//borrowmsgid
        {name: 'title', type: 'string'},
        {name: 'filenumber', type: 'string'},
        {name: 'archivecode', type: 'string'},
        {name: 'funds', type: 'string'},
        {name: 'catafog', type: 'string'},
        {name: 'serial', type: 'string'},//审批通过时间
        {name: 'entrysecurity', type: 'string'},//查档天数
        {name: 'responsible',type:'string'},//到期时间
        {name:'pages',type:'string'}//归还状态
    ]
});
