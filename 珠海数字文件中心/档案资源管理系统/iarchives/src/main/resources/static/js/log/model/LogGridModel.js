/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Log.model.LogGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'ip', type: 'string'},
        {name: 'operate_user', type: 'string'},
        {name: 'module', type: 'string'},
        {name: 'startTime', type: 'string'},
        {name: 'desci', type: 'string'}
    ]
});