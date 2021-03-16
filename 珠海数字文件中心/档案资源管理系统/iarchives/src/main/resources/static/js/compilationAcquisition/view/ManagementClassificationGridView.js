/**
 * 分类设置表格视图
 */
Ext.define('CompilationAcquisition.view.ManagementClassificationGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype:'managementClassificationGridView',
    itemId:'classificationGridViewID',
	searchstore:[
        {item: 'title', name: '题名'}
    ],
    store: 'ManagementClassificationStore',
    columns:[
        {text: '档号', dataIndex: 'archivecode', flex: 2, menuDisabled: true},//档号 实体属性：archivecode
        {text: '题名', dataIndex: 'title', flex: 4, menuDisabled: true},//题名 实体属性：title
        {text: '归档年度', dataIndex: 'filingyear', flex: 1, menuDisabled: true},//归档年度 实体属性：filingyear
        {text: '保管期限', dataIndex: 'entryretention', flex: 1, menuDisabled: true},//保管期限 实体属性：entryretention
        {text: '档案类型', dataIndex: 'organ', flex: 1, menuDisabled: true}//档案类型 实体属性：organ
    ]
});