/**
 * Created by SunK on 2020/6/23 0023.
 */
Ext.define('Acquisition.model.ServiceMetadataGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'operateuser', type: 'string'},
        {name: 'operateusername', type: 'string'},
        {name: 'operatetime', type: 'string'},
        {name: 'ip', type: 'string'},
        {name: 'type', type: 'string'}
    ]
});