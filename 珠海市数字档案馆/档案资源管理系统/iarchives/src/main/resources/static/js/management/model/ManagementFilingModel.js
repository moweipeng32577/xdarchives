/**
 * Created by RonJiang on 2017/11/30 0030.
 */
Ext.define('Management.model.ManagementFilingModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string',mapping:'entryid'},
        {name: 'archivecode', type: 'string'},
        {name: 'title', type: 'string'},
        {name: 'number', type: 'string'},
        {name: 'filedate', type: 'string'},
        {name: 'responsible', type: 'string'},
        {name: 'pages', type: 'string'}
        ]
});