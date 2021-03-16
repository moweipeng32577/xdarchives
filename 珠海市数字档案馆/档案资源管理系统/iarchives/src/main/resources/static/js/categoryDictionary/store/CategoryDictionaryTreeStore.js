Ext.define('CategoryDictionary.store.CategoryDictionaryTreeStore',{
    extend:'Ext.data.TreeStore',
    model:'CategoryDictionary.model.CategoryDictionaryTreeModel',
    proxy: {
        type: 'ajax',
        url: '/categoryDictionary/getCategoryDictionaryTree',
        reader: {
            type: 'json'
        }
    },
    root: {
        text: '分类设置字典',
        expanded: true
    }
});