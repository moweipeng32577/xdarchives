Ext.define('MetadataTemplate.view.MetadataTemplateGridPreView',{
    extend:'Comps.view.EntryGridView',
    xtype:'atemplateGridPreView',
    dataUrl:'/management/entries',
    templateUrl:'/metadataTemplate/grid',
    tbar:[{
        xtype: 'button',
        text: '表单界面',
        iconCls:'fa fa-columns',
        itemId: 'gridviewbtnid'
    }, '-', {
    	xtype: 'button',
    	text: '返回',
    	itemId: 'rebackbtnid'
    }],
    hasSearchBar: false,
    hasCheckColumn: false,
    hasPageBar: false,
    hasRownumber: false
});