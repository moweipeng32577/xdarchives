/**
 * Created by yl on 2017/10/25.
 */
Ext.define('CompilationAcquisition.store.ImportGridStore', {
    extend:'Ext.data.Store',
    model:'CompilationAcquisition.model.ImportGridModel',
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