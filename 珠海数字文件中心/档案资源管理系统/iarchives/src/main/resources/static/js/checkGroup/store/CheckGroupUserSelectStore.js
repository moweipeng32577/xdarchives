/**
 * Created by Administrator on 2018/12/3.
 */

Ext.define('CheckGroup.store.CheckGroupUserSelectStore',{
    extend:'Ext.data.Store',
    xtype:'checkGroupUserSelectStore',
    model:'CheckGroup.model.CheckGroupUserSelectModel',
    idProperty: 'userid',
    fields: ['userid','realname'],
    proxy: {
        type: 'ajax',
        url: '/workflow/getWorkUser',
        reader: {
            type: 'json'
        }
    }
});