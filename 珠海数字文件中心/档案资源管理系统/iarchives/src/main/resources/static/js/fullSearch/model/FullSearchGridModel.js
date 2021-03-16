/**
 * Created by tanly on 2017/11/17 0002.
 */
Ext.define('FullSearch.model.FullSearchGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string',mapping:'textid'},
        {name: 'filename', type: 'string'},
        {name: 'filetype', type: 'string'}
    ]
});