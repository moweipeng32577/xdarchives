Ext.define('CategoryDictionary.store.CategoryDictionaryGridStore',{
    extend:'Ext.data.Store',
    model:'CategoryDictionary.model.CategoryDictionaryGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/categoryDictionary/getCategoryDictionaryBySearch',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});