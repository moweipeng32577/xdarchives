/**
 * Created by Administrator on 2020/7/27.
 */

Ext.define('User.store.FillingSortUserSelectStore',{
    extend:'Ext.data.Store',
    xtype:'fillingSortUserSelectStore',
    model:'User.model.FillingSortUserSelectModel',
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
