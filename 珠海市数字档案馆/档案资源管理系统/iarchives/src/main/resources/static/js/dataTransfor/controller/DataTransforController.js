var transforView;
Ext.define('DataTransfor.controller.DataTransforController', {
    extend: 'Ext.app.Controller',

    views: [
    	'DataTransforView','DataTransforGridView',
    	'DataTransforSelectView','DataTransforSelectedView',
    	'DataTransforFieldView'
    ],
    stores: [
    	'DataTransforSelectStore','DataTransforSelectedStore',
    	'ImportGridStore','TemplateStore'
    ],
    models: [
    	'DataTransforSelectModel','ImportGridModel'
    ],
    
    init: function () {
    	this.control({
    		//显示原节点的所有条目信息
    		'dataTransforSelectView': {
    			select: function (treemodel, record) {
            		var dataTransforView = treemodel.view.up('dataTransforView');
            		transforView = dataTransforView;
            		
            		var entryGrid = dataTransforView.down('dataTransforGridView');
            		//中间的表格动态刷新
            		entryGrid.initGrid({nodeid: record.get('fnid')});
            		//将主页面的显示视图从背景图改为表格视图
            		var dataTransforGridId = dataTransforView.down('[itemId=dataTransforGridId]');
            		dataTransforGridId.setActiveItem(entryGrid);
            		dataTransforView.nodeid = record.get('fnid');
            	}
            },
            //记录目标节点的节点信息
            'dataTransforSelectedView': {
            	select: function (treemodel, record) {
            		var dataTransforView = treemodel.view.up('dataTransforView');
            		
            		dataTransforView.targetNodeid = record.get('fnid');
            		var templateStore = this.getStore('TemplateStore');
                    templateStore.proxy.extraParams = {nodeid: record.get('fnid')};
                    templateStore.reload();
            	}
            },
            //数据转移 - 首先获取字段设置
            'dataTransforGridView [itemId=getPreview]' : {
            	click: function (button) {
            		var controller = this;
            		//查找到数据转移视图
            		var dataTransforView = button.up('dataTransforView');
            		
            		var nodeid = dataTransforView.nodeid;
                    var targetNodeid = dataTransforView.targetNodeid;
                   	
                    if (typeof(targetNodeid) == 'undefined') {
                    	XD.msg("请选择需要移交的数据节点");
                    	return;
                    }
                    if (typeof(targetNodeid) == 'undefined') {
                    	XD.msg("请选择移交的数据节点");
                    	return;
                    }
                    if (nodeid == targetNodeid) {
                    	XD.msg("原节点与目的节点不能相同，请重新选择！");
                    	return;
                    }
                    Ext.MessageBox.wait('正在处理请稍后...', '提示');
                    Ext.Ajax.request({
                        params: {
                        	nodeid: nodeid,
                        	targetNodeid: targetNodeid
                        },
                        url: '/template/getFieldInfo',
                        method: 'POST',
                        sync: true,
                        success: function (resp) {
                            var info = Ext.decode(resp.responseText);
                            if (info == null) {
                            	Ext.MessageBox.hide();
                            	XD.msg("当前节点不能进行数据转移操作！");
                            	return;
                            } else {
			                    var fieldView = Ext.create('Ext.window.Window', {
						            width: '35%',
    								height: '75%',
						            header: false,
						            modal: true,
						            draggable: true,//禁止拖动
            						resizable: true,//禁止缩放
						            closeToolText: '关闭',
						            layout: 'fit',
						            items: [{
						                xtype: 'dataTransforFieldView'
						            }]
						        });
						        fieldView.show();
						        var dataTransforFieldView = fieldView.down('dataTransforFieldView');
						        dataTransforFieldView.nodeid = nodeid;
						        dataTransforFieldView.targetNodeid = targetNodeid;
				                //提交成功后，刷新字段设置
				                var workspace = fieldView.down('dataTransforFieldView').down('[itemId=workspace]');
				                var fieldstore = workspace.down('[itemId=fieldgrid]').getStore();
				                fieldstore.removeAll();
				                for (var i = 0; i < info.length; i++) {
				                    fieldstore.add({
				                    	fieldName: info[i].fieldName, 
				                    	fieldCode: info[i].fieldCode,
				                    	targetFieldName: info[i].targetFieldName,
				                    	targetFieldCode: info[i].targetFieldCode
				                    });
				                }
				                Ext.MessageBox.hide();
                            }
                        },
                        failure: function () {
                        	Ext.MessageBox.hide();
                            XD.msg("操作失败!");
                        }
                    });
            	}
            },
            '[itemId=fieldgrid]': {
                edit: function (editor, e) {
                	var targetFieldstore = this.getStore('TemplateStore').data.items;
                	// 修改的字段坐标
                	var index = parseInt(e.node.innerText.split('	')[0]) - 1;
                	var workspace = editor.grid.up('[itemId=workspace]');
                	var fieldstore = workspace.down('[itemId=fieldgrid]').getStore().data.items;
                	for (var i = 0; i < targetFieldstore.length; i++) {
						if (targetFieldstore[i].data.fieldname == e.value) {
							fieldstore[index].data.targetFieldCode = targetFieldstore[i].data.fieldcode;
						}
					}
                }
            },
            //字段设置 - 提交
            'dataTransforFieldView [itemId=submit]': {
            	click:function (btn) {
            		//查找到字段设置视图
            		var dataTransforFieldView = btn.up('dataTransforFieldView');
            		var field = dataTransforFieldView.down('[itemId=fieldgrid]').down('[dataIndex=fieldName]');
            		var fieldValues = field.config.$initParent.view.store.data.items;
            		var fieldCodes = "";
            		var targetFieldCodes = "";
            		for (var i = 0; i < fieldValues.length; i++) {
            			if (i < fieldValues.length - 1) {
            				fieldCodes += fieldValues[i].data.fieldCode + ",";
            				targetFieldCodes += fieldValues[i].data.targetFieldCode + ",";
            			} else {
            				fieldCodes += fieldValues[i].data.fieldCode;
            				targetFieldCodes += fieldValues[i].data.targetFieldCode;
            			}
            		}
            		
            		var grid = transforView.down('dataTransforGridView');
            		var isSelectAll = grid.down('[itemId=selectAll]').checked;
            		var entryInfo;
            		if (isSelectAll) {
            			entryInfo = grid.acrossDeSelections;
            		} else {
            			entryInfo = grid.getSelectionModel().getSelection();
            		}
            		var tmp = [];
					for (var i = 0; i < entryInfo.length; i++) {
						tmp.push(entryInfo[i].get('entryid'));
					}
					var entryids = tmp.join(",");
					var condition = grid.down('[itemId=condition]').value;
					var operator = grid.down('[itemId=operator]').value;
					var content = grid.down('[itemId=value]').value;
					Ext.MessageBox.wait('正在处理请稍后...', '提示');
					Ext.Ajax.request({
                        params: {
                        	nodeid: dataTransforFieldView.nodeid,
                        	targetNodeid: dataTransforFieldView.targetNodeid,
                        	fieldCodes: fieldCodes,
                        	targetFieldCodes: targetFieldCodes,
                        	entryids: entryids,
                        	isSelectAll: isSelectAll,
                        	condition: condition,
                        	operator: operator,
                        	content: content,
                        	type: ''
                        },
                        url: '/transfor/entriesTransfer',
                        timeout:XD.timeout,
                        method: 'POST',
                        sync: true,
                        success: function (resp) {
                        	Ext.MessageBox.hide();
                            XD.msg(Ext.decode(resp.responseText).msg);
                            btn.up('window').hide();
                            //刷新表单
                            grid.getStore().reload();
                        },
                        failure: function () {
                        	Ext.MessageBox.hide();
                            XD.msg("操作失败!");
                        }
                    });
                }
            },
            //数据转移 - 字段设置 - 关闭
            'dataTransforFieldView [itemId=close]': {
            	click:function (btn) {
                    btn.up('window').hide();
                }
            }
    	})
    }
})