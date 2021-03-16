Ext.define('CategoryDictionary.controller.CategoryDictionaryController', {
    extend: 'Ext.app.Controller',
  	views: [
  		'CategoryDictionaryView',
  		'CategoryDictionaryFormView',
  		'CategoryDictionaryGridView'
  	],
  	stores: ['CategoryDictionaryGridStore','CategoryDictionaryTreeStore'],
  	models: ['CategoryDictionaryGridModel','CategoryDictionaryTreeModel'],
  	init: function () {
		this.control({
			//点击树节点之后加载信息
			'treepanel':{
				select:function (treemodel, record) {
  		 			var gridview = this.findView(treemodel.view).down('[itemId=gridview]');
                    var grid = gridview.down('categoryDictionaryGridView');
                    
                    var buttons = grid.down("toolbar").query('button');
                    var tbseparator = grid.down("toolbar").query('tbseparator');
                    
                    if (record.data.text!='分类设置字典') {
						this.hideCategoryBtn(buttons,tbseparator);//隐藏分类按钮
						// 显示分类的数据列表
						grid.categoryid = record.get('fnid');
                    	grid.initGrid({categoryid:record.get('fnid')});
					} else {
						this.hideBtn(buttons,tbseparator);//隐藏按钮
						// 显示分类的字词数据列表
						grid.categoeyid = record.get('fnid');
                    	grid.initGrid({categoeyid:record.get('fnid')});
					}
				}
			},
			//分类操作start -----------------------------------------
			'categoryDictionaryGridView button[itemId=saveCategory]':{//增加
                click:this.cSaveHandler
            },
            'categoryDictionaryGridView button[itemId=modifyCategory]':{//修改
                click:this.cModifyHandler
            },
            'categoryDictionaryGridView button[itemId=delCategory]':{//删除
                click:this.cDelHandler
            },
			//分类操作end -------------------------------------------
			//字词操作start -----------------------------------------
			'categoryDictionaryGridView button[itemId=save]':{//增加
                click:this.saveHandler
            },
            'categoryDictionaryGridView button[itemId=modify]':{//修改
                click:this.modifyHandler
            },
            'categoryDictionaryGridView button[itemId=del]':{//删除
                click:this.delHandler
            },
            //字词操作end -------------------------------------------
            'categoryDictionaryFormView button[itemId=save]':{//删除
                click:this.submitHandler
            }
		});
  	},
  	//获取分类设置字典应用视图
    findView: function (btn) {
        return btn.findParentByType('categoryDictionary');
    },
    //获取列表界面视图
    findGridView: function (btn) {
        return this.findView(btn).getComponent('gridview');
    },
    findGrid: function (btn) {
    	return this.findView(btn).down('categoryDictionaryGridView');
    },
    findForm: function (btn) {
    	return btn.up('categoryDictionaryFormView');
    },
    // 增加分类
    cSaveHandler: function(btn){
        var grid = this.findGrid(btn);
        var categoryWin = Ext.create('Ext.window.Window',{
            width: '50%',
            height: '50%',
            title: '增加分类',
            modal: true,
            closeToolText: '关闭',
            layout:'fit',
            xtype: 'categoryWin',
            items:[{
                xtype:'categoryDictionaryFormView',
                grid:grid
            }]
        });
        categoryWin.show();
    },
    // 修改分类
    cModifyHandler: function(btn) {
    	var gridview = this.findGridView(btn);
        var grid = gridview.down('categoryDictionaryGridView');
        var record = grid.selModel.getSelection();
        if (record.length == 0) {
            XD.msg('请至少选择一条需要修改的数据');
            return;
        }
        var categoryWin = Ext.create('Ext.window.Window',{
            width: '50%',
            height: '50%',
            title: '修改分类',
            modal: true,
            closeToolText: '关闭',
            layout:'fit',
            xtype: 'categoryWin',
            items:[{
                xtype:'categoryDictionaryFormView',
                grid:grid
            }]
        });
        var form = categoryWin.down('categoryDictionaryFormView');
        form.getForm().findField('name').setValue(record[0].get('name'));
        form.getForm().findField('remark').setValue(record[0].get('remark'));
        categoryWin.show();
    },
  	// 增加分类字典
  	saveHandler: function(btn){
  		var gridview = this.findGridView(btn);
        var tree = gridview.down('treepanel');
        var grid = this.findGrid(btn);
        var node = tree.selModel.getSelected().items[0];
        if (!node) {//若点击增加分类字典时左侧未选中任何节点，则提示选择节点
            XD.msg('请选择节点');
            return;
        }
        var parentid = node != null ? node.get('text'):'';
        var categoryWin = Ext.create('Ext.window.Window',{
            width: '50%',
            height: '50%',
            title: '增加字词',
            modal: true,
            closeToolText: '关闭',
            layout:'fit',
            xtype: 'categoryWin',
            items:[{
                xtype:'categoryDictionaryFormView',
                grid:grid,
                parentid:parentid
            }]
        });
        categoryWin.show();
  	},
  	
  	// 修改分类字典
  	modifyHandler: function(btn){
  		var gridview = this.findGridView(btn);
        var tree = gridview.down('treepanel');
        var grid = gridview.down('categoryDictionaryGridView');
        var record = grid.selModel.getSelection();
  		var node = tree.selModel.getSelected().items[0];
        if (!node) {//若点击增加分类字典时左侧未选中任何节点，则提示选择节点
            XD.msg('请选择节点');
            return;
        }
        if (record.length == 0) {
            XD.msg('请至少选择一条需要修改的数据');
            return;
        }
        var categoryWin = Ext.create('Ext.window.Window',{
            width: '50%',
            height: '50%',
            title: '修改字词',
            modal: true,
            closeToolText: '关闭',
            layout:'fit',
            xtype: 'categoryWin',
            items:[{
                xtype:'categoryDictionaryFormView',
                grid:grid
            }]
        });
        var form = categoryWin.down('categoryDictionaryFormView');
        if(node){
            form.getForm().findField('name').setValue(record[0].get('name'));
            form.getForm().findField('remark').setValue(record[0].get('remark'));
        }
        categoryWin.show();
  	},
  	// 提交分类字典信息
  	submitHandler: function(btn){
  		var formview = this.findForm(btn);
  		var name = formview.getForm().findField('name').getValue();
  		var remark = formview.getForm().findField('remark').getValue();
  		if (name == null || name == '') {
  			XD.msg('名称不能为空');
            return;
  		}
  		var gridview = this.findGridView(formview.grid);
        var tree = gridview.down('treepanel');
  		var node = tree.selModel.getSelected().items[0];
  		var parentid = node.get('fnid');
  		if (formview.up('window').title == '增加分类') {
  			Ext.Ajax.request({
	            url: '/categoryDictionary/addCategory',
	            async:false,
	            params:{
	                name:name,
	                remark:remark
	            },
	            success: function (form) {
	                //刷新列表数据，关闭表单窗口
	                tree.getStore().reload();
	                formview.grid.initGrid({categoryid:parentid});
	                formview.up('window').close();
	            },
	            failure: function () {
	                XD.msg('操作失败');
	            }
	        });
  		} else if (formview.up('window').title == '增加字词') {
  			Ext.Ajax.request({
	            url: '/categoryDictionary/addCategory',
	            async:false,
	            params:{
	                name:name,
	                parentid:parentid,
	                remark:remark
	            },
	            success: function (form) {
	                //刷新列表数据，关闭表单窗口
	                tree.getStore().reload();
	                formview.grid.initGrid({categoryid:parentid});
	                formview.up('window').close();
	            },
	            failure: function () {
	                XD.msg('操作失败');
	            }
	        });
  		} else if (formview.up('window').title == '修改分类' || formview.up('window').title == '修改字词') {
  			var record = this.findGrid(formview.grid).selModel.getSelection();
  			var categoryid = record[0].get('categoryid');
  			Ext.Ajax.request({
	            url: '/categoryDictionary/modifyCategory',
	            async:false,
	            params:{
	            	categoryid:categoryid,
	                name:name,
	                remark:remark
	            },
	            success: function (form) {
	            	//刷新列表数据，关闭表单窗口
	            	if (formview.up('window').title == '修改分类') {
		                formview.grid.initGrid();
	            	} else {
	            		formview.grid.initGrid({categoryid:parentid});
	            	}
	                tree.getStore().reload();
	                formview.up('window').close();
	            },
	            failure: function () {
	                XD.msg('操作失败');
	            }
	        });
  		}
  	},
  	// 删除分类
    cDelHandler: function(btn) {
    	var gridview = this.findGridView(btn);
    	var tree = gridview.down('treepanel');
        var grid = gridview.down('categoryDictionaryGridView');
        var record = grid.selModel.getSelection();
        if (record.length == 0) {
            XD.msg('请至少选择一条需要删除的数据');
            return;
        }
        XD.confirm('确定要删除这' + record.length + '条数据吗',function(){
        	var tmp = [];
            for (var i = 0; i < record.length; i++) {
                tmp.push(record[i].get('categoryid'));
            }
            var categoryid = tmp.join(',');
            Ext.Ajax.request({
            	method: 'DELETE',
            	url: '/categoryDictionary/deleteCategory/' + categoryid,
            	success: function (form, action) {
	                //刷新列表数据，关闭表单窗口
	                tree.getStore().reload();
	                grid.initGrid();
	            },
	            failure: function () {
	                XD.msg('操作失败');
	            }
            });
        },this);
    },
  	// 删除分类字典
  	delHandler: function(btn){
  		var gridview = this.findGridView(btn);
        var tree = gridview.down('treepanel');
        var grid = gridview.down('categoryDictionaryGridView');
        var record = grid.selModel.getSelection();
        var node = tree.selModel.getSelected().items[0];
        if (!node) {//若点击增加分类字典时左侧未选中任何节点，则提示选择节点
            XD.msg('请选择节点');
            return;
        }
        if (record.length == 0) {
            XD.msg('请至少选择一条需要删除的数据');
            return;
        }
        var parentid = node != null ? node.get('text'):'';
        XD.confirm('确定要删除这' + record.length + '条数据吗',function(){
        	var tmp = [];
            for (var i = 0; i < record.length; i++) {
                tmp.push(record[i].get('categoryid'));
            }
            var categoryid = tmp.join(',');
            Ext.Ajax.request({
            	method: 'DELETE',
            	url: '/categoryDictionary/deleteCategory/' + categoryid,
            	success: function (form, action) {
	                //刷新列表数据，关闭表单窗口
	                tree.getStore().reload();
	                grid.initGrid({categoryid:parentid});
	            },
	            failure: function () {
	                XD.msg('操作失败');
	            }
            });
        },this);
  	},
  	
    hideBtn:function (buttons,tbseparator) {
        if(buttons.length>=3){
        	buttons[buttons.length-6].show();//增加分类按钮
            buttons[buttons.length-5].show();//修改分类按钮
            buttons[buttons.length-4].show();//删除分类按钮
            buttons[buttons.length-3].hide();//增加按钮
            buttons[buttons.length-2].hide();//修改按钮
            buttons[buttons.length-1].hide();//删除按钮
        }
        if(tbseparator.length>=3){
        	tbseparator[tbseparator.length-5].show();
            tbseparator[tbseparator.length-4].show();
            tbseparator[tbseparator.length-3].hide();
            tbseparator[tbseparator.length-2].hide();
            tbseparator[tbseparator.length-1].hide();
        }
    },
    hideCategoryBtn:function (buttons,tbseparator) {
        if(buttons.length>=3){
            buttons[buttons.length-6].hide();//增加分类按钮
            buttons[buttons.length-5].hide();//修改分类按钮
            buttons[buttons.length-4].hide();//删除分类按钮
            buttons[buttons.length-3].show();//增加按钮
            buttons[buttons.length-2].show();//修改按钮
            buttons[buttons.length-1].show();//删除按钮
        }
        if(tbseparator.length>=2){
            tbseparator[tbseparator.length-5].hide();
            tbseparator[tbseparator.length-4].hide();
            tbseparator[tbseparator.length-3].hide();
            tbseparator[tbseparator.length-2].show();
            tbseparator[tbseparator.length-1].show();
        }
    }
})