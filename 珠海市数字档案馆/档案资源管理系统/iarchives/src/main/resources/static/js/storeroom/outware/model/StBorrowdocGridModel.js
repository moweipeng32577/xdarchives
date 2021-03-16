/**
 * Created by Administrator on 2019/6/12.
 */

Ext.define('Outware.model.StBorrowdocGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string',mapping:'userid'},
        {name: 'borrowman', type: 'string'},
        {name: 'borrowmd', type: 'string'},
        {name: 'borroworgan', type: 'string'},
        {name: 'borrowdate', type: 'string'},
        {name: 'desci', type: 'string'},
        {name: 'borrowts', type: 'int'},
        {name: 'outwarestate', type: 'string'}
    ]
});
