/**
 * Created by yl on 2020/6/28.
 */
Ext.define('Datareceive.model.DatareceivedResultGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'entryid', type: 'string',mapping:'entryid'},
        {name: 'title', type: 'string'},
        {name: 'checkstatus', type: 'string'},
        {name: 'authenticity', type: 'string'},
        {name: 'integrity', type: 'string'},
        {name: 'usability', type: 'string'},
        {name: 'safety', type: 'string'}
    ]
});