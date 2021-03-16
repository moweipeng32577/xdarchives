/**
 * Created by Administrator on 2018/12/1.
 */

Ext.define('CheckGroup.store.CheckGroupUserGridStore',{
    extend:'Ext.data.Store',
    model:'CheckGroup.model.CheckGroupUserGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/checkGroup/getCheckUser',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
