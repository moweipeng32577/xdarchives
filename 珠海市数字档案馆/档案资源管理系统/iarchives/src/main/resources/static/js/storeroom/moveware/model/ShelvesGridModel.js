/**
 * Created by Rong on 2018/4/27.
 */
Ext.define('Moveware.model.ShelvesGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string',mapping:'shid'},
        {name: 'citydisplay', type: 'string'},
        {name: 'unitdisplay', type: 'string'},
        {name: 'roomdisplay', type: 'string'},
        {name: 'zonedisplay', type: 'string'}
    ]
});