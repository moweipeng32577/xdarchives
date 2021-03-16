Ext.define('Management.view.ManagementSequenceGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype:'managementSequenceGridView',
    itemId:'managementSequenceGridViewID',
    store: 'ManagementSequenceStore',
    columns:[
    	{text: '题名', dataIndex: 'title', flex: 3, menuDisabled: true},
        {text: '档号', dataIndex: 'archivecode', flex: 2.5, menuDisabled: true},
        {text: '顺序号', dataIndex: 'calvalue', flex: 1, editor:{allowBlank:false}, menuDisabled: true},
        {text: '新档号', dataIndex: 'newarchivecode', flex: 2.5, menuDisabled: true},
        {text: '页号', dataIndex: 'pageno', flex: 1, editor:{allowBlank:true}, menuDisabled: true},
        {text: '页数', dataIndex: 'pages', flex: 1, editor:{allowBlank:true}, menuDisabled: true}
    ],
    plugins:[
         Ext.create('Ext.grid.plugin.CellEditing',{
             clicksToEdit:1, //设置单击单元格编辑
             listeners : {
                'edit': function(editor, record){
                	var newarchivecode = [];
					var store = record.grid.getStore().data.map;
					for (key in store) {
						newarchivecode.push(store[key].data.newarchivecode);
					}
                    Ext.Ajax.request({
						params: {
		                    entryid: record.record.id,
		                    field: record.field,
		                    order: record.value
		                },
		                url: '/acquisition/updateSqtempArchivecode',
		                method: 'post',
		                success: function (response) {
		                    var resp = Ext.decode(response.responseText);
		                    if (resp.msg == '参数错误') {
		                    	XD.msg(resp.msg);
		                    }
		                    //刷新临时调序表单
		                    var store = editor.grid.store;
					        store.proxy.url = '/management/sqEntryIndexes';
					        store.proxy.extraParams = {entryids: editor.grid.dataParams.entryids, nodeid: editor.grid.nodeid};
					        store.reload();
		                },
		                failure: function () {
		                    XD.msg('操作中断');
		                }
					});
                }
            }
		})
	],
    hasSearchBar:false
});