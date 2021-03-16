/**
 * Created by Administrator on 2019/6/24.
 */


Ext.define('AcceptDirectory.store.ImportGridStore', {
    extend:'Ext.data.Store',
    model:'AcceptDirectory.model.ImportGridModel',
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/previews',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
