/**
 * Created by Administrator on 2019/6/27.
 */


Ext.define('ClassifySearchDirectory.model.ClassifySearchDirectoryGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string', mapping: 'entryid'},
        {name: 'electronic', type: 'string'},
        {name: 'archivecode', type: 'string'},
        {name: 'title', type: 'string'},
        {name: 'filedate', type: 'string'},
        {name: 'responsible', type: 'string'},
        {name: 'filenumber', type: 'string'},
        {name: 'archivetype', type: 'string'},
        {name: 'classifyname', type: 'string'}
    ]
});
