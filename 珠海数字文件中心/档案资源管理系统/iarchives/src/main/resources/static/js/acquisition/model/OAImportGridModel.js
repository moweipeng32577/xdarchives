Ext.define('Acquisition.model.OAImportGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'ip', type: 'string'},
        {name: 'operate_user', type: 'string'},
        {name: 'module', type: 'string'},
        {name: 'startTime', type: 'string'},
        {name: 'desci', type: 'string'},
        {name: 'checkstatus', type: 'string'},
        {name: 'authenticity', type: 'string'},
        {name: 'integrity', type: 'string'},
        {name: 'usability', type: 'string'},
        {name: 'safety', type: 'string'},
        {name: 'title', type: 'string'}
    ]
});