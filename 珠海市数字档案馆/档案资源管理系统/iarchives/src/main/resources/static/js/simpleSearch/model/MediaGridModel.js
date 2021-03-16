/**
 * Created by Administrator on 2020/8/3.
 */


Ext.define('SimpleSearch.model.MediaGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string', mapping: 'entryid'},
        {name: 'theme', type: 'string'},
        {name: 'filenumber', type: 'string'},
        {name: 'title', type: 'string'},
        {name: 'filedate', type: 'string'},
        {name: 'address', type: 'string'},
        {name: 'keyword', type: 'string'},
        {name: 'author', type: 'string'}
    ]
});
