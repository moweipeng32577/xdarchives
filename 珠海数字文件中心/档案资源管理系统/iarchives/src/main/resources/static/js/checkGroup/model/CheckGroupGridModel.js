/**
 * Created by Administrator on 2018/11/30.
 */


Ext.define('CheckGroup.model.CheckGroupGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string',mapping:'checkgroupid'},
        {name: 'groupname', type: 'string'},
        {name: 'desci', type: 'string'},
        {name: 'type', type: 'string'}
    ]
});