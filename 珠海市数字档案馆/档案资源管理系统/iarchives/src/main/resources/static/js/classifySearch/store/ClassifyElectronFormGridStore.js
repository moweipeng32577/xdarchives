/**
 * Created by yl on 2017/10/26.
 */
Ext.define('ClassifySearch.store.ClassifyElectronFormGridStore', {
    extend: 'Ext.data.Store',
    model: 'ClassifySearch.model.ClassifyElectronFormGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/electron/getEntryIndex',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});