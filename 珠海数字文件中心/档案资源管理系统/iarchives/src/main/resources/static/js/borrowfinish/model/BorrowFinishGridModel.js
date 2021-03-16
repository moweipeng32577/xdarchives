/**
 * Created by Administrator on 2018/11/28.
 */

Ext.define('Borrowfinish.model.BorrowFinishGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string',mapping:'docid'},
        {name: 'type', type: 'string'},
        {name: 'desci', type: 'string'},
        {name: 'borrowdate', type: 'string'},
        {name: 'borrowts', type: 'int'},
        {name: 'borrowtyts', type: 'int'},
        {name: 'state', type: 'string'},
        {name: 'finishtime', type: 'string'},
    ]
});
