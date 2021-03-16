/**
 * Created by Administrator on 2018/11/30.
 */

Ext.define('CheckGroup.store.CheckGroupGridStore',{
    extend:'Ext.data.Store',
    model:'CheckGroup.model.CheckGroupGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/checkGroup/getCheckGroupAll',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
