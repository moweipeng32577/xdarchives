/**
 * Created by yl on 2019/1/10.
 */
Ext.define('Acquisition.model.OfflineAccessionResultGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'filename', type: 'string'},
        {name: 'checkstatus', type: 'string'},
        {name: 'authenticity', type: 'string'},
        {name: 'integrity', type: 'string'},
        {name: 'usability', type: 'string'},
        {name: 'safety', type: 'string'},
        {name: 'isaccess', type: 'string'},
    ]
});

