/**
 * Created by Administrator on 2018/12/3.
 */

Ext.define('DigitalInspection.store.CheckGroupStore',{
    extend:'Ext.data.Store',
    xtype:'checkGroupStore',
    fields: ['checkgroupid', 'groupname'],
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/checkGroup/getCheckGroup',
        extraParams: {type:"质检"},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
