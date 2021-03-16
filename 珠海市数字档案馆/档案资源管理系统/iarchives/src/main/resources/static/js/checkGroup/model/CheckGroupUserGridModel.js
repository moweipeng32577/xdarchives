/**
 * Created by Administrator on 2018/12/1.
 */

Ext.define('CheckGroup.model.CheckGroupUserGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string',mapping:'checkuserid'},
        {name: 'loginname', type: 'string'},
        {name: 'realname', type: 'string'},
        {name: 'sex', type: 'string'},
        {name: 'organname', type: 'string'},
        {name: 'groupname', type:'string'}
    ]
});
