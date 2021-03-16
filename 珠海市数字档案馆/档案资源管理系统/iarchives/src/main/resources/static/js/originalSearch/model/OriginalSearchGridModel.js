/**
 * Created by RonJiang on 2017/11/2 0002.
 */
Ext.define('OriginalSearch.model.OriginalSearchGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string',mapping:'eleid'},
        {name: 'filename', type: 'string'},
        {name: 'filetype', type: 'string'}
    ]
});