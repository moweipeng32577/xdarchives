/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Inform.store.InformGridStore',{
    extend:'Ext.data.Store',
    model:'Inform.model.InformGridModel',
    //autoLoad: true,
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/inform/getInforms',
        extraParams: {flag:''},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
