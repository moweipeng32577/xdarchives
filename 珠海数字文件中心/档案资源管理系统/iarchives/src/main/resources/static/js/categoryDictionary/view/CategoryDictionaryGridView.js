Ext.define('CategoryDictionary.view.CategoryDictionaryGridView', {
    extend:'Comps.view.BasicGridView',
    xtype:'categoryDictionaryGridView',
    store:'CategoryDictionaryGridStore',
    searchstore:[
        {item: 'name', name: '名称'},
        {item: 'remark', name: '描述'}
    ],
    tbar:[{
    	itemId:'saveCategory',
    	xtype: 'button',
    	text: '增加分类'
    }, '-', {
	    itemId:'modifyCategory',
    	xtype: 'button',
    	text: '修改分类'
    }, '-', {
    	itemId:'delCategory',
    	xtype: 'button',
    	text: '删除分类'
    }, '-', {
        itemId:'save',
        xtype: 'button',
        text: '增加字词'
    }, '-', {
        itemId:'modify',
        xtype: 'button',
        text: '修改字词'
    }, '-', {
        itemId:'del',
        xtype: 'button',
        text: '删除字词'
    }],
    columns: [
        {text: '字词名称', dataIndex: 'name',flex: 3, menuDisabled: true},
        {text: '描述', dataIndex: 'remark',flex: 4, menuDisabled: true}
    ]
});