/**
 * Created by yl on 2017/10/26.
 */
Ext.define('Appraisal.model.AppraisalGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'entryid', type: 'string'},
        {name: 'nodeid', type: 'string'},
        {name: 'eleid', type: 'string'},
        {name: 'title', type: 'string'},
        {name: 'filenumber', type: 'string'},
        {name: 'archivecode', type: 'string'},
        {name: 'funds', type: 'string'},
        {name: 'catalog', type: 'string'},
        {name: 'filecode', type: 'string'},
        {name: 'innerfile', type: 'string'},
        {name: 'filingyear', type: 'string'},
        {name: 'entryretention', type: 'string'},
        {name: 'organ', type: 'string'},
        {name: 'recordcode', type: 'string'},
        {name: 'security', type: 'string'},
        {name: 'pages', type: 'string'},
        {name: 'pageno', type: 'string'},
        {name: 'filedate', type: 'string'},
        {name: 'responsible', type: 'string'},
        {name: 'serial', type: 'string'},
        {name: 'flagopen', type: 'string'},
        {name: 'entrystorage', type: 'string'},
        {name: 'descriptiondate', type: 'string'},
        {name: 'descriptionuser', type: 'string'},
        {name: 'fscount', type: 'int'},
        {name: 'kccount', type: 'int'}
    ]
});