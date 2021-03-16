/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Borrow.model.ElectronBorrowGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string',mapping:'entryid'},
        {name: 'title', type: 'string'},
        {name: 'number', type: 'string'},
        {name: 'archivecode', type: 'string'},
        {name: 'fscount', type: 'number'},
        {name: 'kccount', type: 'number'},
        {name: 'funds', type: 'string'},
        {name: 'catafog', type: 'string'}
    ]
});