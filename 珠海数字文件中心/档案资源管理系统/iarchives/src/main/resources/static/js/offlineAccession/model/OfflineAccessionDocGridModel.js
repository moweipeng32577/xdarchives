/**
 * Created by RonJiang on 2018/4/18 0018.
 */
Ext.define('OfflineAccession.model.OfflineAccessionDocGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'filename', type: 'string'},
        {name: 'authenticity', type: 'string'},
        {name: 'integrity', type: 'string'},
        {name: 'usability', type: 'string'},
        {name: 'safety', type: 'string'},
        {name: 'checkstatus', type: 'string'},
        {name: 'isaccess', type: 'string'},
    ]
});