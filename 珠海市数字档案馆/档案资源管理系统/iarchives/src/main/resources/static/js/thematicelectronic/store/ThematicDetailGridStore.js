/**
 * Created by Administrator on 2018/11/8.
 */


Ext.define('Thematicelectronic.store.ThematicDetailGridStore',{
    extend:'Ext.data.Store',
    model:'Thematicelectronic.model.ThematicDetailGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/infoCompilation/getThematicDetail',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
