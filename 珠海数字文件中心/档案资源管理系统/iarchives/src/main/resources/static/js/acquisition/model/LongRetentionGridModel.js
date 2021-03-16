/**
 * Created by RonJiang on 2018/4/21 0021.
 */
Ext.define('Acquisition.model.LongRetentionGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string',mapping:'entryid'},
        {name: 'title', type: 'string'},
        {name: 'archivecode', type: 'string'},
        {name: 'checkstatus', type: 'string'},
        {name: 'authenticity', type: 'string'},
        {name: 'integrity', type: 'string'},
        {name: 'usability', type: 'string'},
        {name: 'safety', type: 'string'}
    ]
});