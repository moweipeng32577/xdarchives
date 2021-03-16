/**
 * Created by tanly on 2017/11/8 0024.
 */
var NodeIdf="";
var RealoadtemplateView;
var SxRealoadtemplateView;
var sortViewInfo;
var preViewInfo;
var tabType;
Ext.define('Template.controller.TemplateController', {
    extend: 'Ext.app.Controller',

    views: [
    	'TemplateView', 'TemplateTreeView', 'TemplateGridView', 'TemplatePromptView', 'TemplateCopyFormView',
        'TemplateTreeComboboxView', 'TemplateSelectionView','TemplateDetailView',
        'TemplateGridPreView', 'TemplateFormView', 'TemplateFormInfoView',
        'TemplateFieldSortView', 'TemplateSearchSortView',//调整排序
        'CodesettingSelectedFormView','EditFieldFormView','TemplateSxGridView','TemplateSxTreeView'
        ,'TemplateTableFieldGridView','MetadataGridView','MetadataSetView','BasicGridView','EntryGridView',
        'PreFieldPanel','PreFieldSelectionView','ListPropertyFormView','TemplateDescView','TemplateDescGridView',
        'TemplateSxDescView','TemplateSxDescGridView'
        // ,'EditOtherOptionFormView'
    ],//加载view
    stores: [
    	'TemplateTreeStore', 'TemplateGridStore', 'TemplateSelectStore','CodesettingSelectStore',
    	'TemplateFieldStore','TableFieldStore','TemplateUnselectFormStore','TemplateSxTreeStore',
        'TemplateSxGridStore','MetadataGridStore','TemplateDescGridStore','TemplateSxDescGridStore'
    ],//加载store
    models: [
    	'TemplateTreeModel', 'TemplateGridModel', 'CodesettingJsonModel','MetadataGridModel',
        'TemplateDescGridModel','TemplateSxDescGridModel'
    ],//加载model
    // rowIndex:Number,//行索引
    init: function () {
        this.control({
            'templateTreeView': {
                select: function (treemodel, record) {
                    window.xtType='档案系统';
                	var templateView = treemodel.view.findParentByType('templateView');
                    RealoadtemplateView = templateView;
                    RealoadtemplateView.nodeid = record.get('fnid');
                    var templatePromptView = templateView.down('[itemId=templatePromptViewID]');
                    var bgSelectOrgan = templateView.down('[itemId=bgSelectOrgan]');
                    if (record.parentNode != null) {//非根目录（功能节点）
                        var templateGridView = templatePromptView.down('[itemId=templateGridViewID]');
                        templatePromptView.setActiveItem(templateGridView);
                        templateGridView.setTitle("当前位置：" + record.get('text'));
                        if (record.get('leaf')) {
                            templateGridView.down('button[itemId=synctemplatebtnid]').setDisabled(true);
                        } else {
                            templateGridView.down('button[itemId=synctemplatebtnid]').setDisabled(false);
                        }
                        templateGridView.nodeid = record.get('fnid');
                        templateGridView.initGrid({xtType:window.xtType, nodeid: record.get('fnid')});
                        NodeIdf=record.get('fnid');
                        var tbseparator = templateGridView.down("toolbar").query('tbseparator');
                        this.templateButtonHandler(templateGridView, record.get('fnid'), tbseparator);
                    } else {
                        templatePromptView.setActiveItem(bgSelectOrgan);
                    }
                }
            },
            'templateSxTreeView': {   //声像左边树选择
                select: function (treemodel, record) {
                    window.xtType='声像系统';
                    var templateView = treemodel.view.findParentByType('templateView');
                    SxRealoadtemplateView = templateView;
                    SxRealoadtemplateView.nodeid = record.get('fnid');
                    var templatePromptView = templateView.down('[itemId=templateSxPromptViewID]');
                    var bgSelectOrgan = templateView.down('[itemId=bgSelectSxOrgan]');
                    var dataType=record.get('classlevel');
                    if (record.parentNode != null) {//非根目录（功能节点）
                        var templateGridView = templatePromptView.down('[itemId=templateSxGridViewID]');
                        var TemplateTableView = templatePromptView.down('[itemId=templateTable]');
                        TemplateTableView.setActiveItem(0);
                        templatePromptView.setActiveItem(TemplateTableView);
                        templateGridView.setTitle("当前位置：" + record.get('text'));
                        if (record.get('leaf')) {
                            templateGridView.down('button[itemId=synctemplatebtnid]').setDisabled(true);
                        } else {
                            templateGridView.down('button[itemId=synctemplatebtnid]').setDisabled(false);
                        }
                        if(dataType=='8'||dataType=='9'||dataType=='10'){
                            TemplateTableView.items.get(1).setDisabled(true)
                            TemplateTableView.items.get(2).setDisabled(true)
                        }else {
                            tabType="";
                            TemplateTableView.items.get(1).setDisabled(false)
                            TemplateTableView.items.get(2).setDisabled(false)
                        }
                        templateGridView.nodeid = record.get('fnid');
                        templateGridView.initGrid({xtType:window.xtType, nodeid: record.get('fnid')});
                        NodeIdf=record.get('fnid');

                        var tbseparator = templateGridView.down("toolbar").query('tbseparator');
                        this.templateButtonHandler(templateGridView, record.get('fnid'), tbseparator);
                    } else {
                        templatePromptView.setActiveItem(bgSelectOrgan);
                    }
                }
            },
            'templateView':{
                tabchange:function(view){
                    if(view.activeTab.title == '档案系统'){
                        window.xtType='档案系统';
                    }else if(view.activeTab.title == '声像系统'){
                        window.xtType='声像系统';
                    }
                }
            },
            'TemplateTableView': {
                tabchange: function (btn) {//选项卡切换事件
                    //屏蔽按钮 以及修改titie
                    var templateView = btn.up('templateView');
                    var treeview = templateView.down('templateSxTreeView');
                    var record = treeview.getSelectionModel().getSelected().items[0];
                    var templatePromptView = templateView.down('[itemId=templateSxPromptViewID]');
                    var TemplateTableView = templatePromptView.down('[itemId=templateTable]');
                    var tabelPanel = btn.getActiveTab();

                    var tabGridView1 = TemplateTableView.down('[itemId=publicPanel]').down('templateSxGridView');
                    var tabGridView2 = TemplateTableView.down('[itemId=publicPanel2]').down('templateSxGridView');
                    var tabGridView3 = TemplateTableView.down('[itemId=publicPanel3]').down('templateSxGridView');
                    //加载条目数据
                    //templateSxGridView.initGrid({nodeid: record.get('fnid')});
                    //1.判断所选tab是 件 还是 组或者案卷
                    //2.根据 1 来修改 grid的store的url
                    //3.reload
                    if (tabelPanel.title == '组') {
                        tabType="group";
                        tabGridView2.initGrid({nodeid: record.get('fnid'),xtType:window.xtType, tableType: 'tb_docgroupself'});
                    } else if (tabelPanel.title == '案卷') {
                        tabType="dossierself";
                        tabGridView3.initGrid({nodeid: record.get('fnid'),xtType:window.xtType, tableType: 'tb_dossierself'});
                    } else if (tabelPanel.title == '件') {
                        tabType="";
                        tabGridView1.initGrid({nodeid: record.get('fnid'),xtType:window.xtType});
                    }
                    var templateGridView = templatePromptView.down('[itemId=templateSxGridViewID]');
                    var tbseparator = templateGridView.down("toolbar").query('tbseparator');
                    this.templateButtonHandler(templateGridView, record.get('fnid'), tbseparator);
                }
            },
            'codesettingSelectedFormView button[itemId=back]':{//档号设置--返回
                click:function (btn) {
                    btn.up('window').hide();
                }
            },
            // 锁定模板
            'templateGridView button[itemId=luckTemplate]': {
            	click: function (btn) {
            		var templateView = btn.findParentByType('templateView');
                    var treeview = templateView.down('[itemId=templateTreeViewID]');
                    var gridview = templateView.down('[itemId=templateGridViewID]');
                    if (treeview.selection.isRoot()) {
                        XD.msg('请选择有效的数据分类');
                        return;
                    }
                    var nodeid = treeview.selection.get('fnid');
                    var tbseparator = gridview.down("toolbar").query('tbseparator');
                    var info = this;
					XD.confirm('确定要对当前模板进行锁定吗？<br />锁定后，该节点下的所有子节点的模板将被替换成当前节点的模板。<br />且所有子节点模板将不可更改，除非解锁当前模板。',function(){
	            		Ext.Msg.wait('正在锁定模板，请耐心等待……', '正在操作');
	            		Ext.Ajax.request({
	                        url: '/template/updateNodeLuckState/' + nodeid + "/" + "1",
                            params: {
                                xtType:window.xtType
                            },
	                        method: 'post',
                            timeout:3600000,
	                        sync: true,
	                        success: function (resp) {
	                        	Ext.Msg.wait('同步成功', '正在操作').hide();
	                        	XD.msg("成功锁定当前模板！<br />在当前模板，进行模板、档号设置的操作，将同步更新到所有子节点模板。");
	                        	info.templateButtonHandler(gridview, nodeid, tbseparator);
	                        },
	                        failure: function () {
	                            XD.msg('操作中断');
	                        }
	                    });
	                },this);
	                gridview.notResetInitGrid({xtType:window.xtType, nodeid:nodeid});
            	}
            },
            //字段描述
            'templateGridView button[itemId=fieldcodeDesc]': {

                click: function (btn) {
                    var win = new Ext.create('Template.view.TemplateDescView',{});
                    var grid=win.down('templateDescGridView');
                    grid.getStore().load();
                    win.show();
                }
            },
            // 锁定模板 声像
            'templateSxGridView button[itemId=luckTemplate]': {
                click: function (btn) {
                    var templateView = btn.findParentByType('templateView');
                    var treeview = templateView.down('[itemId=templateSxTreeViewID]');
                    var gridview = templateView.down('[itemId=templateSxGridViewID]');
                    if (treeview.selection.isRoot()) {
                        XD.msg('请选择有效的数据分类');
                        return;
                    }
                    var nodeid = treeview.selection.get('fnid');
                    var tbseparator = gridview.down("toolbar").query('tbseparator');
                    var info = this;
                    XD.confirm('确定要对当前模板进行锁定吗？<br />锁定后，该节点下的所有子节点的模板将被替换成当前节点的模板。<br />且所有子节点模板将不可更改，除非解锁当前模板。',function(){
                        Ext.Msg.wait('正在锁定模板，请耐心等待……', '正在操作');
                        Ext.Ajax.request({
                            url: '/template/updateNodeLuckState/' + nodeid + "/" + "1",
                            params: {
                                xtType:window.xtType,
                                tableType:tabType,
                            },
                            method: 'post',
                            timeout:3600000,
                            sync: true,
                            success: function (resp) {
                                Ext.Msg.wait('同步成功', '正在操作').hide();
                                XD.msg("成功锁定当前模板！<br />在当前模板，进行模板、档号设置的操作，将同步更新到所有子节点模板。");
                                info.templateButtonHandler(gridview, nodeid, tbseparator);
                            },
                            failure: function () {
                                XD.msg('操作中断');
                            }
                        });
                    },this);
                    gridview.notResetInitGrid({xtType:window.xtType, nodeid:nodeid});
                }
            },
            // 解锁模板
            'templateGridView button[itemId=unluckTemplate]': {
            	click: function (btn) {
            		var templateView = btn.findParentByType('templateView');
                    var treeview = templateView.down('[itemId=templateTreeViewID]');
                    var gridview = templateView.down('[itemId=templateGridViewID]');
                    if (treeview.selection.isRoot()) {
                        XD.msg('请选择有效的数据分类');
                        return;
                    }
					var nodeid = treeview.selection.get('fnid');
					var tbseparator = gridview.down("toolbar").query('tbseparator');
					var info = this;
            		Ext.Ajax.request({
                        url: '/template/updateNodeLuckState/' + nodeid + "/" + "0",
                        params: {
                            xtType:window.xtType,
                            tableType:tabType,
                        },
                        method: 'post',
                        sync: true,
                        success: function (resp) {
                        	XD.msg("成功解锁当前模板！<br />可对子节点模板单独进行维护。");
                        	info.templateButtonHandler(gridview, nodeid, tbseparator);
                        },
                        failure: function (resp) {
                            XD.msg(Ext.decode(resp.responseText).msg);
                        }
                    });
                    gridview.notResetInitGrid({xtType:window.xtType, nodeid:nodeid});
            	}
            },
            // 解锁模板 声像
            'templateSxGridView button[itemId=unluckTemplate]': {
                click: function (btn) {
                    var templateView = btn.findParentByType('templateView');
                    var treeview = templateView.down('[itemId=templateSxTreeViewID]');
                    var gridview = templateView.down('[itemId=templateSxGridViewID]');
                    if (treeview.selection.isRoot()) {
                        XD.msg('请选择有效的数据分类');
                        return;
                    }
                    var nodeid = treeview.selection.get('fnid');
                    var tbseparator = gridview.down("toolbar").query('tbseparator');
                    var info = this;
                    Ext.Ajax.request({
                        url: '/template/updateNodeLuckState/' + nodeid + "/" + "0",
                        params: {
                            xtType:window.xtType
                        },
                        method: 'post',
                        sync: true,
                        success: function (resp) {
                            XD.msg("成功解锁当前模板！<br />可对子节点模板单独进行维护。");
                            info.templateButtonHandler(gridview, nodeid, tbseparator);
                        },
                        failure: function (resp) {
                            XD.msg(Ext.decode(resp.responseText).msg);
                        }
                    });
                    gridview.notResetInitGrid({xtType:window.xtType, nodeid:nodeid});
                }
            },
            'templateGridView button[itemId=copytemplatebtnid]': {
                click: function (btn) {
                    var templateView = btn.findParentByType('templateView');
                    var treeselection = templateView.down('templateTreeView').selection;
                    var nodeid = treeselection.get('fnid');
                    Ext.Ajax.request({
						url: '/template/isActionable/' + nodeid,
                        params: {
                            xtType:window.xtType
                        },
                        method: 'post',
						success: function (sp) {
							var data = Ext.decode(sp.responseText);
							if(data.success){
			                    var gridview = templateView.down('[itemId=templateGridViewID]');
			                    if (treeselection.isRoot()) {
			                        XD.msg('请选择有效的数据分类');
			                        return;
			                    }
			                    var record = treeselection;
			                    var fullname = record.get('text');
			                    while (!record.parentNode.isRoot()) {
			                        fullname = record.parentNode.get('text') + '_' + fullname;
			                        record = record.parentNode;
			                    }
			                    var win = new Ext.create('Template.view.TemplateCopyFormView',{
			                        gridview:gridview,
			                        treeView:templateView.down('templateTreeView')
			                    });
			                    var picker = win.down('[itemId=sourceSelectItemID]');
			                    picker.extraParams = {xtType:window.xtType, pcid:templateView.down('templateTreeView').selection.get('fnid')};
			                    picker.on('render', function (picker) {
			                        picker.store.load();
			                    });
			                    var picker = win.down('[itemId=targetSelectItemID]');
			                    picker.extraParams = {xtType:window.xtType, pcid:templateView.down('templateTreeView').selection.get('fnid')};
			                    picker.on('render', function (picker) {
			                        picker.store.load();
			                    });
			                    win.down('form').items.get('sourceItemID').setValue(treeselection.get('fnid'));
			                    win.down('form').items.get('sourceSelectItemID').setValue(fullname);
			                    // win.down('form').items.get('targetItemID').setValue(treeselection.get('fnid'));
			                    // win.down('form').items.get('targetSelectItemID').setValue(fullname);
			                    win.show();
							} else {
								XD.msg("当前模板已被锁定，不可进行此操作。");
							}
						}
                    });
                }
            },
            'templateSxGridView button[itemId=copytemplatebtnid]': {//复制模板，声像
                click: function (btn) {
                    var templateView = btn.findParentByType('templateView');
                    var treeselection = templateView.down('templateSxTreeView').selection;
                    var nodeid = treeselection.get('fnid');
                    Ext.Ajax.request({
                        url: '/template/isActionable/' + nodeid,
                        params: {
                            xtType:window.xtType
                        },
                        method: 'post',
                        success: function (sp) {
                            var data = Ext.decode(sp.responseText);
                            if(data.success){
                                var gridview = templateView.down('[itemId=templateSxGridViewID]');
                                if (treeselection.isRoot()) {
                                    XD.msg('请选择有效的数据分类');
                                    return;
                                }
                                var record = treeselection;
                                var fullname = record.get('text');
                                while (!record.parentNode.isRoot()) {
                                    fullname = record.parentNode.get('text') + '_' + fullname;
                                    record = record.parentNode;
                                }
                                var win = new Ext.create('Template.view.TemplateCopyFormView',{
                                    gridview:gridview,
                                    treeView:templateView.down('templateSxTreeView')
                                });
                                var picker = win.down('[itemId=sourceSelectItemID]');
                                picker.extraParams = {xtType:window.xtType, pcid:templateView.down('templateSxTreeView').selection.get('fnid')};
                                picker.on('render', function (picker) {
                                    picker.store.load();
                                });
                                var picker = win.down('[itemId=targetSelectItemID]');
                                picker.extraParams = {xtType:window.xtType, pcid:templateView.down('templateSxTreeView').selection.get('fnid')};
                                picker.on('render', function (picker) {
                                    picker.store.load();
                                });
                                win.down('form').items.get('sourceItemID').setValue(treeselection.get('fnid'));
                                win.down('form').items.get('sourceSelectItemID').setValue(fullname);
                                // win.down('form').items.get('targetItemID').setValue(treeselection.get('fnid'));
                                // win.down('form').items.get('targetSelectItemID').setValue(fullname);
                                win.show();
                            } else {
                                XD.msg("当前模板已被锁定，不可进行此操作。");
                            }
                        }
                    });
                }
            },
            'templateGridView button[itemId=setfieldbtnid]': {
                click: function (btn) {
                    var templateView = btn.findParentByType('templateView');
                    var treeview = templateView.down('[itemId=templateTreeViewID]');
                    var gridview = templateView.down('[itemId=templateGridViewID]');
                    if (treeview.selection.isRoot()) {
                        XD.msg('请选择有效的数据分类');
                        return;
                    }
					var nodeid = treeview.selection.get('fnid');
					Ext.Ajax.request({
						url: '/template/isActionable/' + nodeid,
                        params: {
                            xtType:window.xtType
                        },
                        method: 'post',
						success: function (sp) {
							var data = Ext.decode(sp.responseText);
							if(data.success){
								Ext.Ajax.request({
			                        params: {nodeid: nodeid, xtType:window.xtType},
			                        url: '/template/getSelectedByNodeid',
			                        method: 'post',
			                        sync: true,
			                        success: function (resp) {
			                            var respText = Ext.decode(resp.responseText);
			                            if (respText.success == true) {
			                                var selectWin = Ext.create('Template.view.TemplateSelectionView', {
			                                    selectedfnid: nodeid,
			                                    gridview:gridview
			                                });
			                                selectWin.items.get(0).store.proxy.extraParams = {xtType:window.xtType, nodeid: nodeid};
			                                selectWin.items.get(0).getStore().load(function () {
			                                    selectWin.items.get(0).setValue(respText.data);
			                                });
			                                selectWin.show();
			                            } else {
			                                XD.msg(respText.msg);
			                            }
			                        },
			                        failure: function () {
			                            XD.msg('操作中断');
			                        }
			                    });
							} else {
								XD.msg("当前模板已被锁定，不可进行此操作。");
							}
						}
					});
                }
            },
            'templateGridView button[itemId=setMetadataId]': {  //设置元数据
                click: function (btn) {
                    var templateView = btn.findParentByType('templateView');
                    var templateGridView = btn.findParentByType('templateGridView');
                    var treeview = templateView.down('[itemId=templateTreeViewID]');
                    if (treeview.selection.isRoot()) {
                        XD.msg('请选择有效的数据分类');
                        return;
                    }
                    var nodeid = treeview.selection.get('fnid');
                    window.comboBoxArrt = [];
                    window.comboBoxArrtType = [];
                    var win = Ext.create("Template.view.MetadataSetView");
                    var metadataGridView = win.down('metadataGridView');
                    console.log(templateGridView);
                    win.templateGridView = templateGridView;
                    var store = metadataGridView.getStore();
                    store.removeAll();
                    store.proxy.extraParams.xtType = window.xtType;
                    store.proxy.extraParams.nodeid = nodeid ;
                    store.load();
                    win.show();
                }
            },
            'metadataSetView button[itemId=saveMetadataId]':{  //设置元数据 保存
              click:function (view) {
                  var metadataSetView = view.findParentByType('metadataSetView');
                  var selectMetadata = [];
                  var selectMetadataIds = [];
                  for(var i=0;i<window.comboBoxArrtType.length;i++){
                      var comboBoxTypeValue = window.comboBoxArrtType[i].getValue();
                      var comboBoxValue = window.comboBoxArrt[i].getValue();
                      if(comboBoxTypeValue&&!comboBoxValue||!comboBoxTypeValue&&comboBoxValue){
                          XD.msg('存在只设置了元数据类型或者元数据字段描述');
                          return;
                      }
                      if(comboBoxValue){
                          selectMetadataIds.push(comboBoxValue);
                      }
                      comboBoxTypeValue = comboBoxTypeValue? comboBoxTypeValue:'';
                      comboBoxValue = comboBoxValue? comboBoxValue:'';
                      selectMetadata.push(window.comboBoxArrtType[i].gridId+'∪'+comboBoxTypeValue+'∪'+comboBoxValue)
                  }
                  Ext.Ajax.request({
                      url: '/template/setMetadata/',
                      params: {
                          selectMetadata:selectMetadata,
                          selectMetadataIds:selectMetadataIds
                      },
                      method: 'post',
                      success: function (sp) {
                          var data = Ext.decode(sp.responseText);
                          if(data.success){
                              XD.msg('保存成功');
                              metadataSetView.close();
                              metadataSetView.templateGridView.getStore().reload();
                          } else {
                              XD.msg("保存失败,存在元数据字段重复");
                          }
                      },
                      failure: function () {
                          XD.msg('操作中断');
                      }
                  });
              }  
            },
            'metadataSetView button[itemId=back]':{  //设置元数据 返回
                click:function (view) {
                    view.findParentByType('metadataSetView').close();
                }
            },
            'templateSxGridView button[itemId=setfieldbtnid]': {//字段设置 声像
                click: function (btn) {
                    var tableview = btn.up('TemplateTableView');
                    var tabelPanel = tableview.getActiveTab();
                    var templateView = btn.findParentByType('templateView');
                    var treeview = templateView.down('[itemId=templateSxTreeViewID]');
                    var gridview = templateView.down('[itemId=templateSxGridViewID]');
                    if (treeview.selection.isRoot()) {
                        XD.msg('请选择有效的数据分类');
                        return;
                    }
                    var nodeid = treeview.selection.get('fnid');
                    Ext.Ajax.request({
                        url: '/template/isActionable/' + nodeid,
                        params: {
                            xtType:window.xtType
                        },
                        method: 'post',
                        success: function (sp) {
                            var data = Ext.decode(sp.responseText);
                            var nodeType = '';
                            if (tabelPanel.title != "件" && tabelPanel.title == '组') {
                                nodeType = 'group'
                            } else if (tabelPanel.title != "件" && tabelPanel.title == '案卷') {
                                nodeType = 'dossierself'
                            }
                            if (data.success) {
                                Ext.Ajax.request({
                                    params: {nodeid: nodeid, nodeType: nodeType,xtType:window.xtType},
                                    url: '/template/getSelectedByNodeid',
                                    method: 'get',
                                    sync: true,
                                    success: function (resp) {
                                        var respText = Ext.decode(resp.responseText);
                                        if (respText.success == true) {
                                            var selectWin = Ext.create('Template.view.TemplateSelectionView', {
                                                selectedfnid: nodeid,
                                                gridview: gridview,
                                                tabelPanel: tabelPanel
                                            });
                                            if (tabelPanel.title != "件" && tabelPanel.title == '组') {
                                                selectWin.items.get(0).store.proxy.extraParams = {
                                                    nodeid: nodeid,
                                                    nodeType: nodeType
                                                };
                                                selectWin.items.get(0).store.proxy.url = '/template/getGroupField';
                                            } else if (tabelPanel.title != "件" && tabelPanel.title == '案卷') {
                                                selectWin.items.get(0).store.proxy.extraParams = {
                                                    nodeid: nodeid,
                                                    nodeType: nodeType
                                                };
                                                selectWin.items.get(0).store.proxy.url = '/template/getGroupField';
                                            } else {
                                                selectWin.items.get(0).store.proxy.extraParams = {nodeid: nodeid,xtType:window.xtType};
                                                selectWin.items.get(0).store.proxy.url = '/template/getAllField';
                                            }
                                            selectWin.items.get(0).getStore().load(function () {
                                                selectWin.items.get(0).setValue(respText.data);
                                            });
                                            selectWin.show();
                                        } else {
                                            XD.msg(respText.msg);
                                        }
                                    },
                                    failure: function () {
                                        XD.msg('操作中断');
                                    }
                                });
                            } else {
                                XD.msg("当前模板已被锁定，不可进行此操作。");
                            }
                        }
                    });
                }
            },
            'templateGridView button[itemId=updatefieldbtnid]': {
                click: function (btn) {
                    var templateGridView = btn.findParentByType('templateGridView');
                    var that = this;
                    var select = templateGridView.getSelectionModel();
                    if (select.getSelection().length != 1) {
                        XD.msg('请选择一条操作记录');
                    } else {
                    	var nodeid = templateGridView.dataParams.nodeid;
                        var fieldcode = select.getLastSelected().get('fieldcode');
                        var fieldname = select.getLastSelected().get('fieldname');
	                	Ext.Ajax.request({
							url: '/template/isActionable/' + nodeid,
                            params: {
                                xtType:window.xtType
                            },
	                        method: 'post',
							success: function (sp) {
								var data = Ext.decode(sp.responseText);
								if(data.success){
			                        var win = new Ext.create('Template.view.TemplateDetailView',{
			                            gridview:templateGridView
			                        });
                                    var fieldremark = that.getFileRemark(fieldcode,fieldname);
                                    win.down('form').down('[name=fieldremark]').setValue(fieldremark);
			                        win.down('form').loadRecord(select.getLastSelected());
			                        win.show();
			                      	var tablename = select.getLastSelected().get('fieldtable');
                                    Ext.Ajax.request({
                                        url: '/template/getfdlength/',
                                        method: 'get',
                                        params: {
                                            tablename: tablename,
                                            xtType:window.xtType,
                                            fieldcode:fieldcode
                                        },
                                        success: function (sp) {
                                           var fieldlength = Ext.decode(sp.responseText).data;
                                            win.down('form').down('[name=fdlength]').setValue(fieldlength);
                                            //metdatefield
                                            var metadataid = win.down('form').down('[name=metadataid]').getStore();
                                            var metadatatype = win.down('form').down('[name=metadatatype]');
                                            metadataid.proxy.extraParams.metadataType = metadatatype.getValue();
                                            metadataid.reload();
                                            metadatatype.getStore().reload();
                                        },
                                        failure: function () {
                                            XD.msg('操作中断！');
                                        }
                                    });
			                        if (!select.getLastSelected().get('gfield')) {
			                            win.down('[itemId=isgridsetting]').el.slideOut();
			                        }
			                        if (!select.getLastSelected().get('qfield')) {
			                            win.down('[itemId=isquerysetting]').el.slideOut();
			                        }
			                        if (!select.getLastSelected().get('ffield')) {
			                            win.down('[itemId=isformsetting]').el.slideOut();
			                        }else{
			                            if(fieldcode=='fscount' || fieldcode=='kccount'){
			                                //设置默认份数及库存份数为1
			                                win.down('[itemId=defaultValue]').setValue('1');
			                            }
			                        }
								} else {
									XD.msg("当前模板已被锁定，不可进行此操作。");
								}
							}
	                	});
                    }
                }
            },
            'templateSxGridView button[itemId=fieldcodeDesc]': {
                click: function (btn) {
                    var win = new Ext.create('Template.view.TemplateSxDescView',{});
                    var grid=win.down('templateSxDescGridView');
                    grid.getStore().load();
                    win.show();
                }
            },
            'templateSxGridView button[itemId=updatefieldbtnid]': {//字段更新 声像
                click: function (btn) {
                    var templateGridView = btn.findParentByType('templateSxGridView');
                    var select = templateGridView.getSelectionModel();
                    if (select.getSelection().length != 1) {
                        XD.msg('请选择一条操作记录');
                    } else {
                        var nodeid = templateGridView.dataParams.nodeid;
                        Ext.Ajax.request({
                            url: '/template/isActionable/' + nodeid,
                            params: {
                                xtType:window.xtType
                            },
                            method: 'post',
                            success: function (sp) {
                                var data = Ext.decode(sp.responseText);
                                if(data.success){
                                    var win = new Ext.create('Template.view.TemplateDetailView',{
                                        gridview:templateGridView
                                    });
                                    win.down('form').loadRecord(select.getLastSelected());
                                    win.show();
                                    var tablename = select.getLastSelected().get('fieldtable');
                                    var fieldcode = select.getLastSelected().get('fieldcode');
                                    Ext.Ajax.request({
                                        url: '/template/getfdlength/',
                                        method: 'get',
                                        params: {
                                            tablename: tablename,
                                            xtType:window.xtType,
                                            fieldcode:fieldcode
                                        },
                                        success: function (sp) {
                                            var fieldlength = Ext.decode(sp.responseText).data;
                                            win.down('form').down('[name=fdlength]').setValue(fieldlength);
                                            //metdatefield
                                            var metadataid = win.down('form').down('[name=metadataid]').getStore();
                                            var metadatatype = win.down('form').down('[name=metadatatype]');
                                            metadataid.proxy.extraParams.metadataType = metadatatype.getValue();
                                            metadataid.reload();
                                            metadatatype.getStore().reload();
                                        },
                                        failure: function () {
                                            XD.msg('操作中断！');
                                        }
                                    });
                                    if (!select.getLastSelected().get('gfield')) {
                                        win.down('[itemId=isgridsetting]').el.slideOut();
                                    }
                                    if (!select.getLastSelected().get('qfield')) {
                                        win.down('[itemId=isquerysetting]').el.slideOut();
                                    }
                                    if (!select.getLastSelected().get('ffield')) {
                                        win.down('[itemId=isformsetting]').el.slideOut();
                                    }else{
                                        if(fieldcode=='fscount' || fieldcode=='kccount'){
                                            //设置默认份数及库存份数为1
                                            win.down('[itemId=defaultValue]').setValue('1');
                                        }
                                    }
                                } else {
                                    XD.msg("当前模板已被锁定，不可进行此操作。");
                                }
                            }
                        });
                    }
                }
            },
            'templateGridView button[itemId=exportID]': { //导出字段模板
                render: function (btn) {
                    btn.down('[itemId=exportTemplate]').on('click', function () {
                        var templateGridView = btn.findParentByType('templateGridView');
                        var nodeid = templateGridView.dataParams.nodeid;
                        var filename = "";
                        Ext.MessageBox.wait('正在处理请稍后...', '提示');
                        Ext.Ajax.setTimeout(3600000);
                        Ext.Ajax.request({
                            url: '/template/exportFieldModel?nodeid=' + nodeid,
                            params: {
                                fieldTable:tabType,
                                xtType:window.xtType
                            },
                            method: 'post',
                            success: function (sp) {
                                var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
                                var isIE = userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1; //判断是否IE<11浏览器
                                var obj = Ext.decode(sp.responseText).data;
                                filename = obj.nodename;
                                if (isIE) {
                                    window.open('/template/downLoadModel?nodename=' + encodeURIComponent(filename));
                                } else {
                                    window.location.href = '/template/downLoadModel?nodename=' + encodeURIComponent(filename);
                                }
                                Ext.MessageBox.hide();
                                XD.msg('文件生成成功，正在准备下载');
                            }
                        });
                    });
                    btn.down('[itemId=importTemplate]').on('click', function () {
                        var win = Ext.create('Comps.view.TepmUploadView', {});
                        win.NodeIdf=NodeIdf;
                        win.show();
                    });
                }
            },
            'templateDescGridView [itemId=backtnid]': {
                click: function (btn) {
                    btn.up('templateDescView').close();
                }
            },
            'templateDescGridView [itemId=updateAlltnid]': {
                click: function (btn) {
                    var grid=btn.up('templateDescGridView');
                    var that = this;
                    Ext.Msg.wait('正在更新所有字段的描述，请耐心等待……', '正在操作');
                    Ext.Ajax.request({
                        url: '/template/updateAllDesc',
                        method: 'post',
                        timeout:3600000,
                        sync: true,
                        success: function (resp) {
                            Ext.Msg.wait('更新成功','正在操作').hide();
                            XD.msg("更新成功");
                            grid.getStore().load();
                        },
                        failure: function () {
                            XD.msg('操作中断');
                        }
                    });
                }
            },
            'templateSxDescGridView [itemId=backtnid]': {
                click: function (btn) {
                    btn.up('templateSxDescView').close();
                }
            },
            'templateSxDescGridView [itemId=updateAlltnid]': {
                click: function (btn) {
                    var grid=btn.up('templateSxDescGridView');
                    var that = this;
                    Ext.Msg.wait('正在更新所有字段的描述，请耐心等待……', '正在操作');
                    Ext.Ajax.request({
                        url: '/template/updateSxAllDesc',
                        method: 'post',
                        timeout:3600000,
                        sync: true,
                        success: function (resp) {
                            Ext.Msg.wait('更新成功','正在操作').hide();
                            XD.msg("更新成功");
                            grid.getStore().load();
                        },
                        failure: function () {
                            XD.msg('操作中断');
                        }
                    });
                }
            },
            'templateSxGridView button[itemId=exportID]': { //导出字段模板 声像
                render: function (btn) {
                    btn.down('[itemId=exportTemplate]').on('click', function () {
                        var templateGridView = btn.findParentByType('templateSxGridView');
                        var nodeid = templateGridView.dataParams.nodeid;
                        var filename = "";
                        Ext.MessageBox.wait('正在处理请稍后...', '提示');
                        Ext.Ajax.setTimeout(3600000);
                        Ext.Ajax.request({
                            url: '/template/exportFieldModel?nodeid=' + nodeid,
                            params: {
                                fieldTable:tabType,
                                xtType:window.xtType
                            },
                            method: 'post',
                            success: function (sp) {
                                var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
                                var isIE = userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1; //判断是否IE<11浏览器
                                var obj = Ext.decode(sp.responseText).data;
                                filename = obj.nodename;
                                if (isIE) {
                                    window.open('/template/downLoadModel?nodename=' + encodeURIComponent(filename));
                                } else {
                                    window.location.href = '/template/downLoadModel?nodename=' + encodeURIComponent(filename);
                                }
                                Ext.MessageBox.hide();
                                XD.msg('文件生成成功，正在准备下载');
                            }
                        });
                    });
                    btn.down('[itemId=importTemplate]').on('click', function () {
                        var win = Ext.create('Comps.view.TepmSxUploadView', {});
                        win.NodeIdf=NodeIdf
                        win.show();
                    });
                }
            },
            //快速调整字段setting,by lihj on 20180228
            'templateGridView button[itemId=templatefastsetbtnid]': {
                render: function (btn) {
                    btn.down('[itemId=isListField]').on('click', function () {
                        // console.log('templateid打桩:'+templateid);
                        var setParam=true;
                        var url='/template/updateGFieldToSet/';
                        fastSettingField(url,setParam,btn);
                    });
                    btn.down('[itemId=isnotListField]').on('click', function () {
                        var setParam=false;
                        var url='/template/updateGFieldToSet/';
                        fastSettingField(url,setParam,btn);
                    });
                    btn.down('[itemId=ListFieldSequentialup]').on('click', function () {
                        var setParam=1;
                        var url='/template/updateGfieldSequence/';
                        fastSettingField(url,setParam,btn);
                    });
                    btn.down('[itemId=ListFieldSequentialdown]').on('click', function () {
                        var setParam=-1;
                        var url='/template/updateGfieldSequence/';
                        fastSettingField(url,setParam,btn);
                    });
                    btn.down('[itemId=isRetrieveField]').on('click', function () {
                        var setParam=true;
                        var url='/template/updateQFieldToSet/';
                        fastSettingField(url,setParam,btn);
                    });
                    btn.down('[itemId=isnotRetrieveField]').on('click', function () {
                        var setParam=false;
                        var url='/template/updateQFieldToSet/';
                        fastSettingField(url,setParam,btn);
                    });
                    btn.down('[itemId=RetrieveFieldSequentialup]').on('click', function () {
                        var setParam=1;
                        var url='/template/updateQfieldSequence/';
                        fastSettingField(url,setParam,btn);
                    });
                    btn.down('[itemId=RetrieveFieldSequentialdown]').on('click', function () {
                        var setParam=-1;
                        var url='/template/updateQfieldSequence/';
                        fastSettingField(url,setParam,btn);
                    });
                    //
                    btn.down('[itemId=isFormField]').on('click', function () {
                        var setParam=true;
                        var url='/template/updateFFieldToSet/';
                        fastSettingField(url,setParam,btn);
                    });
                    btn.down('[itemId=isnotFormField]').on('click', function () {
                        var setParam=false;
                        var url='/template/updateFFieldToSet/';
                        fastSettingField(url,setParam,btn);
                    });
                    btn.down('[itemId=FormFieldSequentialup]').on('click', function () {
                        var setParam=1;
                        var url='/template/updateFfieldSequence/';
                        fastSettingField(url,setParam,btn);
                    });
                    btn.down('[itemId=FormFieldSequentialdown]').on('click', function () {
                        var setParam=-1;
                        var url='/template/updateFfieldSequence/';
                        fastSettingField(url,setParam,btn);
                    });
                }
            },
            //同步模块
            'templateGridView button[itemId=synctemplatebtnid]': {
                render: function (btn) {
                    btn.down('[itemId=firstChildWithCode]').on('click', function () {
                        var mainview = btn.findParentByType('templateView');
                        var treeview = mainview.down('[itemId=templateTreeViewID]');
                        if (treeview.selection.isRoot()) {
                            XD.msg('请选择有效的数据分类');
                            return;
                        }
                        XD.confirm('是否确认将当前模板覆盖到它的【首层子节点】模板上（包含档号设置）', function () {
                            Ext.Msg.wait('正在进行同步，请耐心等待……', '正在操作');
                            Ext.Ajax.request({
                                params: {
                                    nodeid: treeview.selection.get('fnid'),
                                    copyType: 'firstChild',
                                    xtType:window.xtType,
                                    syncCodeset: true,
                                    tableType:tabType
                                },
                                url: '/template/synctemplate',
                                method: 'post',
                                success: function (response) {
                                    var responseText = Ext.decode(response.responseText);
                                    if (responseText.success === true) {
                                        Ext.Msg.wait('同步成功', '正在操作').hide();
                                        XD.msg('同步成功！');
                                    } else {
                                        Ext.Msg.wait('同步失败', '正在操作').hide();
                                        XD.msg('同步失败！');
                                    }
                                },
                                failure: function () {
                                    Ext.Msg.wait('同步失败', '正在操作').hide();
                                    XD.msg('操作中断！');
                                }
                            });
                        })
                    });
                    btn.down('[itemId=firstChildWithoutCode]').on('click', function () {
                        var mainview = btn.findParentByType('templateView');
                        var treeview = mainview.down('[itemId=templateTreeViewID]');
                        if (treeview.selection.isRoot()) {
                            XD.msg('请选择有效的数据分类');
                            return;
                        }
                        XD.confirm('是否确认将当前模板覆盖到它的【首层子节点】模板上（不包含档号设置）', function () {
                            Ext.Msg.wait('正在进行同步，请耐心等待……', '正在操作');
                            Ext.Ajax.request({
                                params: {
                                    nodeid: treeview.selection.get('fnid'),
                                    copyType: 'firstChild',
                                    xtType:window.xtType,
                                    syncCodeset: false,
                                    tableType:tabType
                                },
                                url: '/template/synctemplate',
                                method: 'post',
                                success: function (response) {
                                    var responseText = Ext.decode(response.responseText);
                                    if (responseText.success === true) {
                                        Ext.Msg.wait('同步成功', '正在操作').hide();
                                        XD.msg('同步成功！');
                                    } else {
                                        Ext.Msg.wait('同步失败', '正在操作').hide();
                                        XD.msg('同步失败！');
                                    }
                                },
                                failure: function () {
                                    Ext.Msg.wait('同步失败', '正在操作').hide();
                                    XD.msg('操作中断！');
                                }
                            });
                        });
                    });
                    btn.down('[itemId=allChildWithCode]').on('click', function () {
                        var mainview = btn.findParentByType('templateView');
                        var treeview = mainview.down('[itemId=templateTreeViewID]');
                        if (treeview.selection.isRoot()) {
                            XD.msg('请选择有效的数据分类');
                            return;
                        }
                        XD.confirm('是否确认将当前模板覆盖到它的【所有子节点】模板上（包含档号设置）', function () {
                            Ext.Msg.wait('正在进行模板同步，请耐心等待……', '正在操作');
                            Ext.Ajax.request({
                                params: {
                                    nodeid: treeview.selection.get('fnid'),
                                    copyType: 'allChild',
                                    xtType:window.xtType,
                                    syncCodeset: true,
                                    tableType:tabType
                                },
                                url: '/template/synctemplate',
                                method: 'post',
                                success: function (response) {
                                    var responseText = Ext.decode(response.responseText);
                                    if (responseText.success === true) {
                                        Ext.Msg.wait('同步成功', '正在操作').hide();
                                        XD.msg('同步成功！');
                                    } else {
                                        Ext.Msg.wait('同步失败', '正在操作').hide();
                                        XD.msg('同步失败！');
                                    }
                                },
                                failure: function () {
                                    Ext.Msg.wait('同步失败', '正在操作').hide();
                                    XD.msg('操作中断');
                                }
                            });
                        });
                    });
                    btn.down('[itemId=allChildWithoutCode]').on('click', function () {
                        var mainview = btn.findParentByType('templateView');
                        var treeview = mainview.down('[itemId=templateTreeViewID]');
                        if (treeview.selection.isRoot()) {
                            XD.msg('请选择有效的数据分类');
                            return;
                        }
                        XD.confirm('是否确认将当前模板覆盖到它的【所有子节点】模板上（不包含档号设置）', function () {
                            Ext.Msg.wait('正在进行同步，请耐心等待……', '正在操作');
                            Ext.Ajax.request({
                                params: {
                                    nodeid: treeview.selection.get('fnid'),
                                    copyType: 'allChild',
                                    xtType:window.xtType,
                                    syncCodeset: false,
                                    tableType:tabType
                                },
                                url: '/template/synctemplate',
                                method: 'post',
                                success: function (response) {
                                    var responseText = Ext.decode(response.responseText);
                                    if (responseText.success === true) {
                                        Ext.Msg.wait('同步成功', '正在操作').hide();
                                        XD.msg('同步成功！');
                                    } else {
                                        Ext.Msg.wait('同步失败', '正在操作').hide();
                                        XD.msg('同步失败！');
                                    }
                                },
                                failure: function () {
                                    Ext.Msg.wait('同步失败', '正在操作').hide();
                                    XD.msg('操作中断');
                                }
                            });
                        });
                    });
                }
            },
            //同步模块  声像
            'templateSxGridView button[itemId=synctemplatebtnid]': {
                render: function (btn) {
                    btn.down('[itemId=firstChildWithCode]').on('click', function () {
                        var mainview = btn.findParentByType('templateView');
                        var treeview = mainview.down('[itemId=templateSxTreeViewID]');
                        if (treeview.selection.isRoot()) {
                            XD.msg('请选择有效的数据分类');
                            return;
                        }
                        XD.confirm('是否确认将当前模板覆盖到它的【首层子节点】模板上（包含档号设置）', function () {
                            Ext.Msg.wait('正在进行同步，请耐心等待……', '正在操作');
                            Ext.Ajax.request({
                                params: {
                                    nodeid: treeview.selection.get('fnid'),
                                    copyType: 'firstChild',
                                    xtType:window.xtType,
                                    syncCodeset: true,
                                    tableType:tabType
                                },
                                url: '/template/synctemplate',
                                method: 'post',
                                success: function (response) {
                                    var responseText = Ext.decode(response.responseText);
                                    if (responseText.success === true) {
                                        Ext.Msg.wait('同步成功', '正在操作').hide();
                                        XD.msg('同步成功！');
                                    } else {
                                        Ext.Msg.wait('同步失败', '正在操作').hide();
                                        XD.msg('同步失败！');
                                    }
                                },
                                failure: function () {
                                    Ext.Msg.wait('同步失败', '正在操作').hide();
                                    XD.msg('操作中断！');
                                }
                            });
                        })
                    });
                    btn.down('[itemId=firstChildWithoutCode]').on('click', function () {
                        var mainview = btn.findParentByType('templateView');
                        var treeview = mainview.down('[itemId=templateSxTreeViewID]');
                        if (treeview.selection.isRoot()) {
                            XD.msg('请选择有效的数据分类');
                            return;
                        }
                        XD.confirm('是否确认将当前模板覆盖到它的【首层子节点】模板上（不包含档号设置）', function () {
                            Ext.Msg.wait('正在进行同步，请耐心等待……', '正在操作');
                            Ext.Ajax.request({
                                params: {
                                    nodeid: treeview.selection.get('fnid'),
                                    copyType: 'firstChild',
                                    xtType:window.xtType,
                                    syncCodeset: false,
                                    tableType:tabType
                                },
                                url: '/template/synctemplate',
                                method: 'post',
                                success: function (response) {
                                    var responseText = Ext.decode(response.responseText);
                                    if (responseText.success === true) {
                                        Ext.Msg.wait('同步成功', '正在操作').hide();
                                        XD.msg('同步成功！');
                                    } else {
                                        Ext.Msg.wait('同步失败', '正在操作').hide();
                                        XD.msg('同步失败！');
                                    }
                                },
                                failure: function () {
                                    Ext.Msg.wait('同步失败', '正在操作').hide();
                                    XD.msg('操作中断！');
                                }
                            });
                        });
                    });
                    btn.down('[itemId=allChildWithCode]').on('click', function () {
                        var mainview = btn.findParentByType('templateView');
                        var treeview = mainview.down('[itemId=templateSxTreeViewID]');
                        if (treeview.selection.isRoot()) {
                            XD.msg('请选择有效的数据分类');
                            return;
                        }
                        XD.confirm('是否确认将当前模板覆盖到它的【所有子节点】模板上（包含档号设置）', function () {
                            Ext.Msg.wait('正在进行模板同步，请耐心等待……', '正在操作');
                            Ext.Ajax.request({
                                params: {
                                    nodeid: treeview.selection.get('fnid'),
                                    copyType: 'allChild',
                                    xtType:window.xtType,
                                    syncCodeset: true,
                                    tableType:tabType
                                },
                                url: '/template/synctemplate',
                                method: 'post',
                                success: function (response) {
                                    var responseText = Ext.decode(response.responseText);
                                    if (responseText.success === true) {
                                        Ext.Msg.wait('同步成功', '正在操作').hide();
                                        XD.msg('同步成功！');
                                    } else {
                                        Ext.Msg.wait('同步失败', '正在操作').hide();
                                        XD.msg('同步失败！');
                                    }
                                },
                                failure: function () {
                                    Ext.Msg.wait('同步失败', '正在操作').hide();
                                    XD.msg('操作中断');
                                }
                            });
                        });
                    });
                    btn.down('[itemId=allChildWithoutCode]').on('click', function () {
                        var mainview = btn.findParentByType('templateView');
                        var treeview = mainview.down('[itemId=templateSxTreeViewID]');
                        if (treeview.selection.isRoot()) {
                            XD.msg('请选择有效的数据分类');
                            return;
                        }
                        XD.confirm('是否确认将当前模板覆盖到它的【所有子节点】模板上（不包含档号设置）', function () {
                            Ext.Msg.wait('正在进行同步，请耐心等待……', '正在操作');
                            Ext.Ajax.request({
                                params: {
                                    nodeid: treeview.selection.get('fnid'),
                                    copyType: 'allChild',
                                    xtType:window.xtType,
                                    syncCodeset: false,
                                    tableType:tabType
                                },
                                url: '/template/synctemplate',
                                method: 'post',
                                success: function (response) {
                                    var responseText = Ext.decode(response.responseText);
                                    if (responseText.success === true) {
                                        Ext.Msg.wait('同步成功', '正在操作').hide();
                                        XD.msg('同步成功！');
                                    } else {
                                        Ext.Msg.wait('同步失败', '正在操作').hide();
                                        XD.msg('同步失败！');
                                    }
                                },
                                failure: function () {
                                    Ext.Msg.wait('同步失败', '正在操作').hide();
                                    XD.msg('操作中断');
                                }
                            });
                        });
                    });
                }
            },
            'templateGridView button[itemId=deletetemplatebtnid]': {
                click: function (btn) {
                    var mainview = btn.findParentByType('templateView');
                    var treeview = mainview.down('[itemId=templateTreeViewID]');
                    if (treeview.selection.isRoot()) {
                        XD.msg('请选择有效的数据分类');
                        return;
                    }
                    Ext.Ajax.request({
						url: '/template/isActionable/' + treeview.selection.get('fnid'),
                        params: {
                            xtType:window.xtType
                        },
                        method: 'post',
						success: function (sp) {
							var data = Ext.decode(sp.responseText);
							if(data.success){
                                // var backupConfirmMsg = '删除模板数据后，将不能进行还原，建议进行备份，是否备份?';
			                    // var deleteConfirmMsg = '';
			                    // XD.confirm(backupConfirmMsg,function () {
			                    //     // Ext.Msg.wait('正在进行备份，请耐心等待……','正在操作');
			                    //     Ext.Ajax.request({
			                    //         url: '/backupRestore/backup',
			                    //         // timeout : 36000000,
			                    //         async:false,
			                    //         params: {
			                    //             fnidarr:['template'],
			                    //             backupContent:'setting'
			                    //         },
			                    //         method: 'POST',
			                    //         success: function (resp) {
			                    //             var respText = Ext.decode(resp.responseText);
			                    //             XD.confirm('模板备份成功！'+deleteConfirmMsg,function (){
			                    //                 deleteTemplate(treeview);
			                    //             },this);
			                    //         },
			                    //         failure: function () {
			                    //             XD.msg('备份失败!');
			                    //         }
			                    //     });
			                    // },this,function () {
			                    //     XD.confirm('模板未备份！'+deleteConfirmMsg,function (){
			                    //         deleteTemplate(btn);
			                    //     },this);
			                    // });
                                XD.confirm('本次操作将删除当前所有模板数据（包括当前数据节点档号设置），是否继续?',function (){
                                    deleteTemplate(treeview,1);
                                });
							} else {
								XD.msg("当前模板已被锁定，不可进行此操作。");
							}
						}
                    });
                }
            },
            'templateSxGridView button[itemId=deletetemplatebtnid]': {//删除模板 声像
                click: function (btn) {
                    var mainview = btn.findParentByType('templateView');
                    var treeview = mainview.down('[itemId=templateSxTreeViewID]');
                    if (treeview.selection.isRoot()) {
                        XD.msg('请选择有效的数据分类');
                        return;
                    }
                    Ext.Ajax.request({
                        url: '/template/isActionable/' + treeview.selection.get('fnid'),
                        params: {
                            xtType:window.xtType
                        },
                        method: 'post',
                        success: function (sp) {
                            var data = Ext.decode(sp.responseText);
                            if(data.success){
                                XD.confirm('本次操作将删除当前所有模板数据（包括当前数据节点档号设置），是否继续?',function (){
                                    deleteTemplate(treeview,2);
                                });
                            } else {
                                XD.msg("当前模板已被锁定，不可进行此操作。");
                            }
                        }
                    });
                }
            },
            // 模板预览
            'templateGridView button[itemId=resultPreviewbtnid]': {
            	click: function (btn) {
            		var templateGridView = btn.findParentByType('templateGridView');
            		var record = templateGridView.store.totalCount;
            		if (record.length < 1) {
            			XD.msg('无模板字段，请先设置模板信息！');
            			return;
					}
					getGridPreView(templateGridView);
            	}
            },
            // 模板预览  声像
            'templateSxGridView button[itemId=resultPreviewbtnid]': {
                click: function (btn) {
                    var templateGridView = btn.findParentByType('templateSxGridView');
                    var record = templateGridView.store.totalCount;
                    if (record.length < 1) {
                        XD.msg('无模板字段，请先设置模板信息！');
                        return;
                    }
                    getGridPreView(templateGridView);
                }
            },
            // 模板预览 - 表单界面
            'templateGridPreView button[itemId=gridviewbtnid]': {
            	click: function (btn) {
            		var nodeid = btn.up('window').nodeid;
            		gridPreViewInfo = btn.up('window');
                    Ext.Ajax.request({
                        url: '/template/isActionable/' + nodeid,
                        params: {
                            xtType:window.xtType
                        },
                        method: 'post',
                        success: function (sp) {
                            var data = Ext.decode(sp.responseText);
                            if(data.success){
                                Ext.Ajax.request({
                                    params: {xtType:window.xtType, nodeid: nodeid},
                                    url: '/template/getSelectedTableFieldByNode',
                                    method: 'post',
                                    sync: true,
                                    success: function (resp) {
                                        var respText = Ext.decode(resp.responseText);
                                        if (respText.success == true) {
                                            var win =Ext.create('Template.view.TemplateFormView')
                                            // var EditFieldFormwin = Ext.create('Template.view.EditFieldFormView', {
                                            //     selectedfnid: nodeid,
                                            //     gridview:gridview
                                            // });
                                            win.items.get(0).items.get(0).store.proxy.extraParams = {xtType:window.xtType, nodeid: nodeid};
                                            win.items.get(0).items.get(0).getStore().load(function () {
                                                win.items.get(0).items.get(0).setValue(respText.data);
                                            });
                                            win.nodeid=nodeid
                                            var dynamicform = win.down('dynamicform');
                                            initFormField(dynamicform, 'add', nodeid);
                                            var pre = dynamicform.down('[itemId=preBtn]');
                                            var next = dynamicform.down('[itemId=nextBtn]');
                                            // 隐藏上一条&下一条的按钮
                                            pre.hide();
                                            next.hide();
                                            win.show();
                                        } else {
                                            XD.msg(respText.msg);
                                        }
                                    },
                                    failure: function () {
                                        XD.msg('操作中断');
                                    }
                                });
                            } else {
                                XD.msg("当前模板已被锁定，不可进行此操作。");
                            }
                        }
                    });
                    // var win =Ext.create('Template.view.TemplateFormView')
                    // win.show()
                    // var templateFormViewwin = gridPreViewInfo.down('templateFormView');
                    // var dynamicform = templateFormViewwin.down('dynamicform');
            		// initFormField(dynamicform, 'add', nodeid);
                    // var pre = dynamicform.down('[itemId=preBtn]');
                    // var next = dynamicform.down('[itemId=nextBtn]');
                    // // 隐藏上一条&下一条的按钮
                    // pre.hide();
                    // next.hide();
                    // gridPreViewInfo.down('panel').setActiveItem(templateFormViewwin)
            	}
            },
            'PreFieldPanel button[itemId= FieldManagement]':{
                click:function (btn) {
                    var window= btn.up('window')
                    var nodeid = btn.up('window').nodeid;
                    Ext.Ajax.request({
                        url: '/template/isActionable/' + nodeid,
                        params: {
                            xtType:window.xtType
                        },
                        method: 'post',
                        success: function (sp) {
                            var data = Ext.decode(sp.responseText);
                            if(data.success){
                                Ext.Ajax.request({
                                    params: {nodeid: nodeid, xtType:window.xtType},
                                    url: '/template/getSelectedByNodeid',
                                    method: 'post',
                                    sync: true,
                                    success: function (resp) {
                                        var respText = Ext.decode(resp.responseText);
                                        if (respText.success == true) {
                                            var selectWin = Ext.create('Template.view.PreFieldSelectionView', {
                                                selectedfnid: nodeid,
                                                window:window
                                            });
                                            selectWin.items.get(0).store.proxy.extraParams = {xtType:window.xtType, nodeid: nodeid};
                                            selectWin.items.get(0).getStore().load(function () {
                                                selectWin.items.get(0).setValue(respText.data);
                                            });
                                            selectWin.show();
                                        } else {
                                            XD.msg(respText.msg);
                                        }
                                    },
                                    failure: function () {
                                        XD.msg('操作中断');
                                    }
                                });
                            } else {
                                XD.msg("当前模板已被锁定，不可进行此操作。");
                            }
                        }
                    });
                }
            },
            'preFieldSelectionView button[itemId=templateSelectSubmit]': {
                click: function (btn) {
                    var templateSelectionView = btn.findParentByType('preFieldSelectionView');
                    if (templateSelectionView.items.get(0).getValue().length == 0) {
                        XD.msg('请至少选择一个字段');
                        return;
                    }
                    var fieldnames = templateSelectionView.items.get(0).toField.getStore().data.items;
                    var fieldnamearray = [];
                    for (var i = 0; i < fieldnames.length; i++) {
                        fieldnamearray[i] = fieldnames[i].get("fieldname");
                    }
                    var url = '/template/submitfields';
                    var nType = '';
                    if(window.xtType=='声像系统'){
                        if (templateSelectionView.tabelPanel.title == "件") {//默认
                        } else if (templateSelectionView.tabelPanel.title == '组') {
                            nType = 'group';
                            url = '/template/submitGroupfields'
                        } else if (templateSelectionView.tabelPanel.title == '案卷') {
                            nType = 'dossierself';
                            url = '/template/submitGroupfields'
                        }
                    }
                    Ext.Ajax.request({
                        params: {
                            nodeid: templateSelectionView.selectedfnid,
                            xtType:window.xtType,
                            fieldnames: fieldnamearray,
                            nodeType: nType,
                        },
                        url: url,
                        method: 'post',
                        sync: true,
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            if (respText.success == true) {
                                debugger
                                templateSelectionView.window.down('PreFieldPanel').items.get(0).getStore().reload()
                                XD.msg('提交成功');
                                templateSelectionView.close();
                            } else {
                                XD.msg(respText.msg);
                            }
                        },
                        failure: function () {
                            XD.msg('操作中断');
                        }
                    });
                }
            },
            'preFieldSelectionView button[itemId=templateCodeSettingId]':{
                click:function (btn) {
                    var preFieldSelectionView = btn.findParentByType('preFieldSelectionView');
                    preFieldSelectionView.close();
                }
            },
            'PreFieldPanel button[itemId =submit]':{
              click:function (btn) {
                  debugger
                  var editFieldFormView=btn.findParentByType('PreFieldPanel');
                  var win =btn.up('window')
                  var nodeid=win.nodeid
                  if(editFieldFormView.items.items[0].value.length==0){
                      XD.msg("至少选择一项");
                      return;
                  }
                  var leftlist = editFieldFormView.items.items[0].items.items[0].store.data.items;//itemselector 框左边的取得方式 i了i了 结果是一个对象数组
                  var rightfieldnamelist=editFieldFormView.items.items[0].value;//结果是一个数组
                  var leftfieldnamelist=leftlist.map(function (a) {
                      return a.data.fieldcode
                  })//将对象数组按照其中一个 抽取成数组
                  Ext.Ajax.request({
                      params:{
                          nodeid:nodeid,
                          leftfieldnamelist:leftfieldnamelist,
                          xtType:window.xtType,
                          rightfieldnamelist:rightfieldnamelist
                      },
                      url:'/template/updateFormQueue',
                      method:'post',
                      success:function (response) {
                          if(Ext.decode(response.responseText).success===true){
                              XD.msg(Ext.decode(response.responseText).msg);
                              var dynamicform = win.down('dynamicform');
                              initFormField(dynamicform, 'add', nodeid);
                               var pre = dynamicform.down('[itemId=preBtn]');
                              var next = dynamicform.down('[itemId=nextBtn]');
                              // 隐藏上一条&下一条的按钮
                              pre.hide();
                              next.hide();
                              preViewInfo.clearCache=Math.random()
                          }
                      }
                  });
              }
            },
            'PreFieldPanel button[itemId =reflash]':{
              click:function (btn) {
                  var editFieldFormView = btn.findParentByType('PreFieldPanel');
                  var win =btn.up('window');
                  var nodeid =win.nodeid;
                  if(editFieldFormView.items.items[0].value.length==0){
                      return;
                  }
                  var leftlist = editFieldFormView.items.items[0].items.items[0].store.data.items;//itemselector 框左边的取得方式 i了i了 结果是一个对象数组
                  var rightfieldnamelist=editFieldFormView.items.items[0].value;//结果是一个数组
                  var leftfieldnamelist=leftlist.map(function (a) {
                      return a.data.fieldcode
                  })//将对象数组按照其中一个 抽取成数组
                  Ext.Ajax.request({
                      params:{
                          nodeid:nodeid,
                          leftfieldnamelist:leftfieldnamelist,
                          xtType:window.xtType,
                          rightfieldnamelist:rightfieldnamelist
                      },
                      url:'/template/updateFormQueue',
                      method:'post',
                      success:function (response) {
                          if(Ext.decode(response.responseText).success===true){
                              var dynamicform = win.down('dynamicform');
                              initFormField(dynamicform, 'add', nodeid);
                              var pre = dynamicform.down('[itemId=preBtn]');
                              var next = dynamicform.down('[itemId=nextBtn]');
                              // 隐藏上一条&下一条的按钮
                              pre.hide();
                              next.hide();
                              preViewInfo.clearCache=Math.random()
                          }
                      }
                  });
              }

            },
            'PreFieldPanel button[itemId =close]':{
                click:function(btn){
                    var win =btn.up('window')
                    win.close()
                }
            },
            // 模板预览 - 表单界面 - 返回
            'templateFormView button[itemId=back]': {
            	click: function (btn) {
            		// 返回模板维护界面
                    var templateGridPreView = gridPreViewInfo.down('templateGridPreView');
                    gridPreViewInfo.down('panel').setActiveItem(templateGridPreView)
            	}
            },
            // 模板预览 - 返回
            'templateGridPreView button[itemId=rebackbtnid]': {
            	click: function (btn) {
            		btn.up('window').hide();
            	}
            },
            // 模板预览 - 设置检索字段
            'templateGridPreView button[itemId=listsort]': {
            	click: function (btn) {
            		var sortView = Ext.create('Template.view.TemplateFieldSortView');
				    sortView.show();
				    sortViewInfo = sortView;
				    var nodeid;
				    if(window.xtType=='声像系统'){
				        nodeid=SxRealoadtemplateView.nodeid;
                    }else{
                        nodeid=RealoadtemplateView.nodeid;
                    }
					Ext.Ajax.request({
                        params: {xtType:window.xtType, nodeid: nodeid},
                        url: '/template/getSearchField',
                        method: 'post',
                        sync: true,
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            sortView.items.get(0).store.proxy.extraParams = {xtType:window.xtType, nodeid: nodeid};
                            sortView.items.get(0).getStore().load(function () {
                                sortView.items.get(0).setValue(respText.data);
                            });
                        },
                        failure: function () {
                            XD.msg('操作中断');
                        }
                    });
            	}
            },
            // 模板预览 - 设置列表字段
            'templateGridPreView button[itemId=searchsort]': {
            	click: function (btn) {
            		var sortView = Ext.create('Template.view.TemplateSearchSortView');
				    sortView.show();
				    sortViewInfo = sortView;
                    var nodeid;
                    if(window.xtType=='声像系统'){
                        nodeid=SxRealoadtemplateView.nodeid;
                    }else{
                        nodeid=RealoadtemplateView.nodeid;
                    }
					Ext.Ajax.request({
                        params: {xtType:window.xtType, nodeid: nodeid},
                        url: '/template/getSelectedField',
                        method: 'post',
                        sync: true,
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            sortView.items.get(0).store.proxy.extraParams = {xtType:window.xtType, nodeid: nodeid};
                            sortView.items.get(0).getStore().load(function () {
                                sortView.items.get(0).setValue(respText.data);
                            });
                        },
                        failure: function () {
                            XD.msg('操作中断');
                        }
                    });
            	}
            },
            // 设置检索字段 - 提交
            'templateFieldSortView button[itemId=templateSortSubmit]': {
            	click: function (btn) {
            		var templateSortView = btn.findParentByType('templateFieldSortView');
            		var fieldInfo = templateSortView.items.items[0].value;
            		var fieldCode = [];
            		var items = templateSortView.items.items[0].items.items[0].store.data.items;
            		for (var i = 0; i < items.length; i++) {
						fieldCode.push(items[i].data.fieldcode);
					}
            		var rightFieldCode = [];
            		for (var i = 0; i < fieldInfo.length; i++) {
						rightFieldCode.push(fieldInfo[i]);
					}
                    var nodeid;
                    var templateView;
                    if(window.xtType=='声像系统'){
                        nodeid=SxRealoadtemplateView.nodeid;
                        templateView=SxRealoadtemplateView;
                    }else{
                        nodeid=RealoadtemplateView.nodeid;
                        templateView=RealoadtemplateView;
                    }
					Ext.Msg.wait('正在进行保存，请耐心等待……', '正在操作');
            		Ext.Ajax.request({
                        params: {
                            nodeid: nodeid,
                            fieldCode: fieldCode,
                            rightFieldCode: rightFieldCode,
                            xtType:window.xtType,
                            type: 'search'
                        },
                        url: '/template/updateQuence',
                        method: 'post',
                        sync: true,
                        success: function (resp) {
                        	XD.msg('提交成功');
                        	Ext.MessageBox.hide();
                        	// 关闭调整排序界面
                            btn.up('window').close();
                            // 刷新模板预览界面
                            preViewInfo.down('templateGridPreView').initGrid({xtType:window.xtType, nodeid:nodeid, type: '模板预览'});
                            preViewInfo.down('templateGridPreView').down('[itemId=listsearchsort]').getStore().removeAll();
                            preViewInfo.down('templateGridPreView').down('[itemId=listsearchsort]').getStore().proxy.extraParams = {xtType:window.xtType ,nodeid:nodeid};
                            preViewInfo.down('templateGridPreView').down('[itemId=listsearchsort]').getStore().reload();
                            // 刷新模板维护表单界面
                            var templateGridView = templateView.down('templateGridView');
                            templateGridView.getStore().reload();
                        },
                        failure: function () {
                        	Ext.MessageBox.hide();
                            XD.msg('操作中断');
                        }
                    });
            	}
            },
            // 设置列表字段 - 属性配置
            'templateSearchSortView button[itemId=setProperty]':{
                click:function (btn) {
                    var templateSearchSortView=btn.findParentByType('templateSearchSortView');
                    var nodeid;
                    if(templateSearchSortView.items.items[0].value.length==0){
                        XD.msg("至少选择一项");
                        return;
                    }
                    if(window.xtType=='声像系统'){
                        nodeid=SxRealoadtemplateView.nodeid;
                    }else{
                        nodeid=RealoadtemplateView.nodeid;
                    }
                    // var leftlist = templateSearchSortView.items.items[0].items.items[0].store.data.items;//itemselector 框左边的取得方式 结果是一个对象数组
                    // var rightfieldnamelist=templateSearchSortView.items.items[0].value;//结果是一个数组
                    // var leftfieldnamelist=leftlist.map(function (a) {//将对象数组按照其中一个 抽取成数组
                    //     return a.data.fieldcode
                    // })
                    // 在进入配置其他表单前对表单列表做保存
                    // Ext.Ajax.request({
                    //     params: {
                    //         nodeid: nodeid,
                    //         leftfieldnamelist: leftfieldnamelist,
                    //         xtType: window.xtType,
                    //         rightfieldnamelist: rightfieldnamelist
                    //     },
                    //     url: '/template/updateFormQueue',
                    //     method: 'post',
                    //     async:false,
                    //     dataType:'json'
                    // });
                    var ListPropertyFormView = Ext.create('Template.view.ListPropertyFormView',{});
                    var store=ListPropertyFormView.down('templateTableFieldGridView').getStore();
                    if(window.xtType == '档案系统'){
                        store.proxy.url='/template/getTableField';
                        ListPropertyFormView.down('templateTableFieldGridView').setStore(store);
                        ListPropertyFormView.down('templateTableFieldGridView').initGrid({
                            nodeid:nodeid
                        })
                    }else if(window.xtType == '声像系统'){
                        store.proxy.url='/template/getSxTableField';
                        ListPropertyFormView.down('templateTableFieldGridView').initGrid({
                            nodeid:nodeid
                        })
                    }

                    ListPropertyFormView.show()

                }
            },
            // 属性配置 - 保存
            'listPropertyFormView button[itemId=listPropertySaveBtnId]': {
                click: function (btn) {
                    var win =btn.up('window');
                    var form= win.down('form');
                    var obj=form.getValues();
                    var grid=win.down('templateTableFieldGridView');
                    var select=grid.getSelectionModel();
                    if(select.getSelection().length==0){
                        win.close()
                    }
                    var record=select.getSelection();
                    form.submit({
                        url:'/template/updateTemplistField',
                        params:{
                            str1:JSON.stringify(record[0].data),
                            xtType:window.xtType
                        },
                        success:function () {
                            grid.initGrid();
                            XD.msg('保存成功');
                        }
                    })
                }
            },
            // 设置列表字段 - 提交
            'templateSearchSortView button[itemId=templateSortSubmit]': {
            	click: function (btn) {
            		var templateSortView = btn.findParentByType('templateSearchSortView');
            		var fieldInfo = templateSortView.items.items[0].value;
            		var fieldCode = [];
            		var items = templateSortView.items.items[0].items.items[0].store.data.items;
            		for (var i = 0; i < items.length; i++) {
						fieldCode.push(items[i].data.fieldcode);
					}
            		var rightFieldCode = [];
            		for (var i = 0; i < fieldInfo.length; i++) {
						rightFieldCode.push(fieldInfo[i]);
					}
                    var nodeid;
                    var templateView;
                    if(window.xtType=='声像系统'){
                        nodeid=SxRealoadtemplateView.nodeid;
                        templateView=SxRealoadtemplateView;
                    }else{
                        nodeid=RealoadtemplateView.nodeid;
                        templateView=RealoadtemplateView;
                    }
					Ext.Msg.wait('正在进行保存，请耐心等待……', '正在操作');
            		Ext.Ajax.request({
                        params: {
                            nodeid: nodeid,
                            fieldCode: fieldCode,
                            rightFieldCode: rightFieldCode,
                            xtType:window.xtType,
                            type: 'list'
                        },
                        url: '/template/updateQuence',
                        method: 'post',
                        sync: true,
                        success: function (resp) {
                        	XD.msg('提交成功');
                        	Ext.MessageBox.hide();
                        	// 关闭调整排序界面
                            btn.up('window').close();
                            // 刷新模板预览界面
                            preViewInfo.down('templateGridPreView').initGrid({xtType:window.xtType, nodeid:nodeid, type: '模板预览'});
                            // 刷新模板维护表单界面
                            var templateGridView = templateView.down('templateGridView');
                            templateGridView.getStore().reload();
                        },
                        failure: function () {
                        	Ext.MessageBox.hide();
                            XD.msg('操作中断');
                        }
                    });
            	}
            },
            // 调整排序 - 关闭
            'templateFieldSortView button[itemId=templateSortClose]': {
            	click: function (btn) {
            		btn.up('window').close();
            	}
            },
            'templateSearchSortView button[itemId=templateSortClose]': {
            	click: function (btn) {
            		btn.up('window').close();
            	}
            },
            'templateGridView button[itemId=templateCodeSettingId]':{
                click: this.doCodesetting
            },
            'templateSxGridView button[itemId=templateCodeSettingId]':{
                click: this.doCodesetting
            },
            'templateSelectionView button[itemId=templateSelectClose]': {
                click: function (btn) {
                    var templateSelectionView = btn.findParentByType('templateSelectionView');
                    templateSelectionView.close();
                }
            },
            'templateSelectionView button[itemId=templateSelectSubmit]': {
                click: function (btn) {
                    var templateSelectionView = btn.findParentByType('templateSelectionView');
                    if (templateSelectionView.items.get(0).getValue().length == 0) {
                        XD.msg('请至少选择一个字段');
                        return;
                    }
                    var fieldnames = templateSelectionView.items.get(0).toField.getStore().data.items;
                    var fieldnamearray = [];
                    for (var i = 0; i < fieldnames.length; i++) {
                        fieldnamearray[i] = fieldnames[i].get("fieldname");
                    }
                    var url = '/template/submitfields';
                    var nType = '';
                    if(window.xtType=='声像系统'){
                        if (templateSelectionView.tabelPanel.title == "件") {//默认
                        } else if (templateSelectionView.tabelPanel.title == '组') {
                            nType = 'group';
                            url = '/template/submitGroupfields'
                        } else if (templateSelectionView.tabelPanel.title == '案卷') {
                            nType = 'dossierself';
                            url = '/template/submitGroupfields'
                        }
                    }
                    Ext.Ajax.request({
                        params: {
                            nodeid: templateSelectionView.selectedfnid,
                            xtType:window.xtType,
                            fieldnames: fieldnamearray,
                            nodeType: nType,
                        },
                        url: url,
                        method: 'post',
                        sync: true,
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            if (respText.success == true) {
                                templateSelectionView.gridview.getStore().reload();
                                XD.msg('提交成功');
                                templateSelectionView.close();
                            } else {
                                XD.msg(respText.msg);
                            }
                        },
                        failure: function () {
                            XD.msg('操作中断');
                        }
                    });
                }
            },
            'templateDetailView button[itemId=templateCancelBtnID]': {
                click: function (btn) {
                    var closeview = btn.findParentByType('templateDetailView');
                    closeview.close();
                }
            },
            'templateCopyFormView button[itemId=templateCancelBtnID]': {
                click: function (btn) {
                    var closeview = btn.findParentByType('templateCopyFormView');
                    closeview.close();
                }
            },
            'templateDetailView button[itemId=templateSaveBtnID]': {
                click: function (btn) {
                    var templateDetailView = btn.findParentByType('templateDetailView');
                    templateDetailView.down('form').submit({
                        waitTitle: '提示',
                        waitMsg: '正在提交数据请稍后...',
                        url: '/template/UpdateTemplate',
                        params: {
                            xtType:window.xtType,
                            tableType:tabType
                        },
                        method: 'post',
                        success: function (form, action) {
                            var respText = Ext.decode(action.response.responseText);
                            if (respText.success == true) {
                                templateDetailView.gridview.getStore().reload();
                                templateDetailView.close();
                                XD.msg(respText.msg);
                            }else{
                                XD.msg(respText.msg);
                                console.log(respText.msg);
                            }
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'templateCopyFormView button[itemId=templateSaveBtnID]': {
                click: function (btn) {
                    var templateCopyFormView = btn.findParentByType('templateCopyFormView');
                    var form = templateCopyFormView.down('form');
                    var sourceid = form.getValues()['sourceid'];
                    var targetid = form.getValues()['targetSelectItem'];
                    var targetids = targetid.join(',');
                    Ext.each(targetid,function (data) {
                        if(sourceid===data){
                            XD.msg('源模板与目标模板数据节点不能为同一节点，请重新选择');
                            return;
                        }
                    });
                    if(targetid.length>100){
                        XD.msg('目标模板不可超过一百个');
                        return;
                    }

                    var withCode=false;
                    var confirmMsg='若目标模板存在数据，该操作将覆盖原数据（不包括档号设置数据），是否确认复制？';
                    if(form.down('[itemId=withCodeID]').checked){
                        withCode=true;
                        confirmMsg='若目标模板存在数据，该操作将覆盖原数据（包括档号设置数据），是否确认复制？';
                    }
                    XD.confirm(confirmMsg, function () {
                        form.submit({
                            waitTitle: '提示',
                            waitMsg: '正在处理请稍后...',
                            url: '/template/copyTemplate',
                            params:{xtType:window.xtType, withCode:withCode,targetids:targetids},
                            method: 'post',
                            success: function (form, action) {
                                var respText = Ext.decode(action.response.responseText);
                                templateCopyFormView.gridview.getStore().reload();
                                templateCopyFormView.close();
                                XD.msg(respText.msg);
                            },
                            failure: function () {
                                XD.msg('操作中断');
                            }
                        });
                    });
                }
            },
            'codesettingItemSelectedFormView': {
                render: function (field) {
                    field.getComponent("itemselectorID").toField.boundList.on('select', function () {
                        var codesettingSelectedFormView = this.findParentByType('codesettingSelectedFormView');
                        var codesettingDetailFormView = codesettingSelectedFormView.down('[itemId=codesettingDetailFormViewItemID]');
                        var areatextfield = codesettingDetailFormView.down('[itemId=areaid]');
                        var splitcodetextfield = codesettingDetailFormView.down('[itemId=splitcodeid]');
                        var lengthtextfield = codesettingDetailFormView.down('[itemId=lengthid]');
                        var hideidfield = codesettingDetailFormView.down('[itemId=hiddenfieldId]');

                        var temp = this.selModel.selected.items[0].get('fieldcode').split('∪');
                        if (temp[0] == "") {
                            //将从模板中获得的字段传到输入框中
                            areatextfield.setValue(temp[2]);
                            splitcodetextfield.setValue(temp[3]);
                            lengthtextfield.setValue(temp[4]);
                            //把字段全称保存在隐藏域中，输入框修改保存时用到
                            hideidfield.setValue(temp[1]);
                        } else {
                            areatextfield.setValue(temp[1]);
                            splitcodetextfield.setValue(temp[2]);
                            lengthtextfield.setValue(temp[3]);
                            hideidfield.setValue(temp[4]);
                        }
                    });
                }
            },
            'codesettingDetailFormView': {
                render: function (field) {
                    field.getComponent("splitcodeid").on('keyup', function (ob) {
                        var codesettingSelectedFormView = this.findParentByType('codesettingSelectedFormView');
                        var codesettingItemSelectedFormView = codesettingSelectedFormView.down('[itemId=itemselectorItemID]');
                        var codesettingDetailFormView = codesettingSelectedFormView.down('[itemId=codesettingDetailFormViewItemID]');
                        changeToMultiselect(ob, codesettingItemSelectedFormView, codesettingDetailFormView);
                    });
                    field.getComponent("lengthid").on('keyup', function (ob) {
                        var codesettingSelectedFormView = this.findParentByType('codesettingSelectedFormView');
                        var codesettingItemSelectedFormView = codesettingSelectedFormView.down('[itemId=itemselectorItemID]');
                        var codesettingDetailFormView = codesettingSelectedFormView.down('[itemId=codesettingDetailFormViewItemID]');
                        changeToMultiselect(ob, codesettingItemSelectedFormView, codesettingDetailFormView);
                    });
                }
            },
            'codesettingSelectedFormView button[itemId=codesettingSaveBtnId]': {
                click: function (btn) {
                    var codesettingSelectedFormView = btn.findParentByType('codesettingSelectedFormView');
                    var codesettingItemSelectedFormView = codesettingSelectedFormView.down('[itemId=itemselectorItemID]');
                    var tostore = codesettingItemSelectedFormView.getComponent("itemselectorID").toField.boundList.store;
                    if (tostore.getCount() <= 0 ) {
                        XD.msg("请至少选择一个字段");
                        return;
                    }
                    var recordslist = [];
                    for (var i = 0; i < tostore.getCount(); i++) {
                        recordslist.push(tostore.getAt(i).get('fieldcode'));
                    }
                    Ext.Ajax.request({
                        params: {
                            datanodeid: codesettingSelectedFormView.datanodeid,
                            xtType:window.xtType,
                            filedtable:tabType,
                            fieldcodelist: recordslist
                        },
                        url: '/codesetting/setCode',
                        method: 'post',
                        success: function (resp) {
                            XD.msg(Ext.decode(resp.responseText).msg);
                            if(Ext.decode(resp.responseText).success===true){
                                btn.up('window').close();
                            }
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'codesettingSelectedFormView button[itemId=close]':{
                click:function (btn) {
                    btn.up('window').close();
                }
            },'editFieldFormView button[itemId=templateSelectClose]':{
                click:function (btn) {
                    var editFieldFormView=btn.findParentByType('editFieldFormView');
                    editFieldFormView.close();
                }
            },
            'editFieldFormView button[itemId=templateSelectSubmit]':{
                click:function (btn) {
                    var editFieldFormView=btn.findParentByType('editFieldFormView');
                    if(editFieldFormView.items.items[0].value.length==0){
                        XD.msg("至少选择一项");
                        return;
                    }
                    var leftlist = editFieldFormView.items.items[0].items.items[0].store.data.items;//itemselector 框左边的取得方式 i了i了 结果是一个对象数组
                    var rightfieldnamelist=editFieldFormView.items.items[0].value;//结果是一个数组
                    var leftfieldnamelist=leftlist.map(function (a) {
                        return a.data.fieldcode
                    })//将对象数组按照其中一个 抽取成数组
                    Ext.Ajax.request({
                        params:{
                            nodeid:editFieldFormView.selectedfnid,
                            leftfieldnamelist:leftfieldnamelist,
                            xtType:window.xtType,
                            rightfieldnamelist:rightfieldnamelist
                        },
                        url:'/template/updateFormQueue',
                        method:'post',
                        success:function (response) {
                            if(Ext.decode(response.responseText).success===true){
                                XD.msg(Ext.decode(response.responseText).msg);
                                btn.up('window').close();
                                editFieldFormView.gridview.getStore().reload();
                                preViewInfo.clearCache=Math.random()
                            }
                        }
                    });
                }
            },
            'templateGridPreView button[itemId=editFieldBtn]': {
                click: function (btn) {
                    var templateGridPreView = btn.findParentByType('templateGridPreView');
                    var nodeid=templateGridPreView.dataParams.nodeid;
                    var gridview=templateGridPreView.gridview;//由于create方式不同
                    Ext.Ajax.request({
                        url: '/template/isActionable/' + nodeid,
                        params: {
                            xtType:window.xtType
                        },
                        method: 'post',
                        success: function (sp) {
                            var data = Ext.decode(sp.responseText);
                            if(data.success){
                                Ext.Ajax.request({
                                    params: {xtType:window.xtType, nodeid: nodeid},
                                    url: '/template/getSelectedTableFieldByNode',
                                    method: 'post',
                                    sync: true,
                                    success: function (resp) {
                                        var respText = Ext.decode(resp.responseText);
                                        if (respText.success == true) {
                                            var EditFieldFormwin = Ext.create('Template.view.EditFieldFormView', {
                                                selectedfnid: nodeid,
                                                gridview:gridview
                                            });
                                            EditFieldFormwin.items.get(0).store.proxy.extraParams = {xtType:window.xtType, nodeid: nodeid};
                                            EditFieldFormwin.items.get(0).getStore().load(function () {
                                                EditFieldFormwin.items.get(0).setValue(respText.data);
                                            });
                                            EditFieldFormwin.show();
                                        } else {
                                            XD.msg(respText.msg);
                                        }
                                    },
                                    failure: function () {
                                        XD.msg('操作中断');
                                    }
                                });
                            } else {
                                XD.msg("当前模板已被锁定，不可进行此操作。");
                            }
                        }
                    });
                }
            }
            ,
            'PreFieldPanel button[itemId=editOtherOptionsBtn]':{
                click:function (btn) {
                    var editFieldFormView=btn.findParentByType('PreFieldPanel');
                    var win =btn.up('window');
                    var nodeid=win.nodeid;
                    if(editFieldFormView.items.items[0].value.length==0){
                        XD.msg("至少选择一项");
                        return;
                    }
                    var leftlist = editFieldFormView.items.items[0].items.items[0].store.data.items;//itemselector 框左边的取得方式 结果是一个对象数组
                    var rightfieldnamelist=editFieldFormView.items.items[0].value;//结果是一个数组
                    var leftfieldnamelist=leftlist.map(function (a) {
                        return a.data.fieldcode
                    })//将对象数组按照其中一个 抽取成数组
                    // 在进入配置其他表单前对表单列表做保存
                    Ext.Ajax.request({
                        params: {
                            nodeid: nodeid,
                            leftfieldnamelist: leftfieldnamelist,
                            xtType: window.xtType,
                            rightfieldnamelist: rightfieldnamelist
                        },
                        url: '/template/updateFormQueue',
                        method: 'post',
                        async:false,
                        dataType:'json'
                    })
                    // var gridview = btn.findParentByType('editFieldFormView').gridview;
                    // var nodeid = editFieldFormView.selectedfnid;

                    var editOtherOptionFormView = Ext.create('Template.view.EditOtherOptionFormView',{});

                    // var ffield= ffieldfilter(context);
                    // for(var i=0;i<ffield;i++){
                    //     editOtherOptionFormView.down('grid')
                    // }
                    // editOtherOptionFormView.down('templateTableFieldGridView').initGrid({xtType:window.xtType, nodeid: nodeid});
                    var store=editOtherOptionFormView.down('templateTableFieldGridView').getStore();
                    if(window.xtType == '档案系统'){
                        store.proxy.url='/template/getTableField';
                        editOtherOptionFormView.down('templateTableFieldGridView').setStore(store);
                        editOtherOptionFormView.down('templateTableFieldGridView').initGrid({
                            nodeid:nodeid
                        })
                    }else if(window.xtType == '声像系统'){
                        store.proxy.url='/template/getSxTableField';
                        editOtherOptionFormView.down('templateTableFieldGridView').initGrid({
                            nodeid:nodeid
                        })
                    }

                    editOtherOptionFormView.show()

                }
            },
            'editOtherOptionFormView button[itemId=codesettingSaveBtnId]':{
                click:function(btn){
                    var win =btn.up('window');
                    var form= win.down('form');
                    var obj=form.getValues();
                    var grid=win.down('templateTableFieldGridView');
                    var select=grid.getSelectionModel();
                    if(select.getSelection().length==0){
                        win.close()
                    }
                    var record=select.getSelection();
                    form.submit({
                      url:'/template/updateTemplateField',
                      params:{
                          str1:JSON.stringify(record[0].data),
                          // str2:obj,
                          xtType:window.xtType
                      },
                      success:function () {
                          grid.initGrid();
                          XD.msg('保存成功');
                      }
                    })
                }
            },
            'editOtherOptionFormView button[itemId=back]':{
                click:function (btn) {
                    btn.up('window').close()
                }
            },
            'templateTableFieldGridView':{
                select:function (gridview,record) {
                    var panel=gridview.view.findParentByType('editOtherOptionFormView');
                    if(!panel){
                        panel=gridview.view.findParentByType('listPropertyFormView');
                    }
                    var fieldset=panel.down('fieldset');
                    fieldset.down('form').loadRecord(record);
                    // fieldset.items.items[0].setData(record.data);
                    // fieldset.items.items[1].items.setData(record.data)
                }
            },
            'templateGridPreView button[itemId=editFieldBtn]': {
                click: function (btn) {
                    var templateGridPreView = btn.findParentByType('templateGridPreView');
                    var nodeid=templateGridPreView.dataParams.nodeid;
                    // var gridview=templateGridPreView.dataParams.gridview;
                    var gridview=templateGridPreView.gridview;
                    Ext.Ajax.request({
                        url: '/template/isActionable/' + nodeid,
                        params: {
                            xtType:window.xtType
                        },
                        method: 'post',
                        success: function (sp) {
                            var data = Ext.decode(sp.responseText);
                            if(data.success){
                                Ext.Ajax.request({
                                    params: {xtType:window.xtType, nodeid: nodeid},
                                    url: '/template/getSelectedTableFieldByNode',
                                    method: 'post',
                                    sync: true,
                                    success: function (resp) {
                                        var respText = Ext.decode(resp.responseText);
                                        if (respText.success == true) {
                                            var EditFieldFormwin = Ext.create('Template.view.EditFieldFormView', {
                                                selectedfnid: nodeid,
                                                gridview:gridview
                                            });
                                            EditFieldFormwin.items.get(0).store.proxy.extraParams = {xtType:window.xtType, nodeid: nodeid};
                                            EditFieldFormwin.items.get(0).getStore().load(function () {
                                                EditFieldFormwin.items.get(0).setValue(respText.data);
                                            });
                                            EditFieldFormwin.show();
                                        } else {
                                            XD.msg(respText.msg);
                                        }
                                    },
                                    failure: function () {
                                        XD.msg('操作中断');
                                    }
                                });
                            } else {
                                XD.msg("当前模板已被锁定，不可进行此操作。");
                            }
                        }
                    });
                }
            }
        });
    },

    //获取模板维护应用视图
    findView: function (btn) {
        return btn.findParentByType('templateView');
    },

    //获取列表界面视图
    findGridView: function (btn) {
        return this.findView(btn).down('templateGridView');
    },
    
    //模板按钮控制
    templateButtonHandler: function(templateGridView, nodeid, tbseparator) {
    	Ext.Ajax.request({
            method:'POST',
            url: '/template/getLuckState/' + nodeid,//判断当前节点是否为父节点,且模板的锁定状态
            params: {
                xtType:window.xtType
            },
            async:false,
            scope:this,
            success: function (response) {
                var msg = Ext.decode(response.responseText).msg;
                var data = Ext.decode(response.responseText).data;
                tbseparator[5].setVisible(true);
                tbseparator[6].setVisible(false);
                if (msg == 'child') {
                	templateGridView.down('[itemId=luckTemplate]').setVisible(false);
                	templateGridView.down('[itemId=unluckTemplate]').setVisible(false);
			        tbseparator[5].setVisible(false);
			        tbseparator[6].setVisible(false);
                } else if (msg == 'luck') {
    				templateGridView.down('[itemId=luckTemplate]').setVisible(true);
    				templateGridView.down('[itemId=unluckTemplate]').setVisible(false);
                } else {
                	templateGridView.down('[itemId=unluckTemplate]').setVisible(true);
                	templateGridView.down('[itemId=luckTemplate]').setVisible(false);
                }
            }
        });
    },

    //档号设置
    doCodesetting:function (btn) {
        var nodeid="";
        var nodeType = '';
        if(window.xtType=='声像系统'){
            nodeid=SxRealoadtemplateView.nodeid
            var tableview = btn.up('TemplateTableView');
            var tabelPanel = tableview.getActiveTab();
            if (tabelPanel.title != "件" && tabelPanel.title == '组') {
                nodeType = 'group'
            } else if (tabelPanel.title != "件" && tabelPanel.title == '案卷') {
                nodeType = 'dossierself'
            }
        }else {
            nodeid=RealoadtemplateView.nodeid;
        }
        var templateGridView = this.findGridView(btn);
        var templateCodeSettingWin = Ext.create('Ext.window.Window',{
            width:'100%',
            height:'100%',
            title:'档号设置',
            header:false,
            draggable : false,//禁止拖动
            resizable : false,//禁止缩放
            modal:true,
            // closeToolText:'关闭',
            layout:'fit',
            items:[{
                xtype: 'codesettingSelectedFormView',
                datanodeid:nodeid
            }]
        });
        var codesettingSelectedFormView = templateCodeSettingWin.down('codesettingSelectedFormView');
        var detailformview = codesettingSelectedFormView.down('[itemId=codesettingDetailFormViewItemID]');
        detailformview.down('[itemId=areaid]').reset();//清空值
        detailformview.down('[itemId=splitcodeid]').reset();
        detailformview.down('[itemId=lengthid]').reset();
        detailformview.down('[itemId=hiddenfieldId]').reset();
        var itemselectorView = codesettingSelectedFormView.down('[itemId=itemselectorID]');
        itemselectorView.store.proxy.extraParams = {xtType:window.xtType,nodeType:nodeType, datanodeid: codesettingSelectedFormView.datanodeid};
        itemselectorView.getStore().load(function (storedata) {
            if(storedata.length===0){
                XD.msg('请先去模板维护设置模板信息');
            }
            var records = [];
            for (var i = 0; i < storedata.length; i++) {
                var temp = storedata[i].data.fieldcode.split('∪');
                if (temp[0] != "") {
                    records.push(storedata[i]);
                }
            }
            itemselectorView.toField.store.removeAll();
            itemselectorView.setValue(records);
            itemselectorView.toField.boundList.select(0);//默认选中第一个
        });
        templateCodeSettingWin.show();
    },

    //获取其他描述
    getFileRemark:function (fieldcode,fieldname) {
        var fileRemark;
        Ext.Ajax.request({
            url: '/template/getFileRemark',
            async:false,
            params:{
                fieldcode:fieldcode,
                fieldname:fieldname
            },
            success: function (response) {
                fileRemark = Ext.decode(response.responseText);
            }
        });
        return fileRemark;
    },

    importHandler: function (btn) {
        //var view = this.findParentByType('managementgrid');
        // var view = btn.up('templateGridView');
        //
        // var tree = btn.up('acquisitionFormAndGrid').down('treepanel');
        // NodeIdf = tree.selModel.getSelected().items[0].get('fnid');
        //var grid = btn.up('importgrid');
        var win = Ext.create('Comps.view.TepmUploadView', {});
        /*win.on('close', function () {
            view.notResetInitGrid();
        }, view);*/
        win.show();
    }
});

function getGridPreView (templateGridView) {
	var templateGridPreView = Ext.create('Ext.window.Window',{
        width:'70%',
        height:'80%',
        modal:true,
        title:'模板预览',
        closeToolText:'关闭',
        closeAction:'hide',
        layout:'fit',
        items:[{
            xtype:'panel',
            layout:'card',
            activeItem:0,
            items:[{
                xtype:'templateGridPreView'
            },{
                xtype:'templateFormView'
            }]
        }]
    });
    preViewInfo = templateGridPreView;
    var nodeid = templateGridView.dataParams.nodeid;
    window.nodeid = nodeid;
    templateGridPreView.nodeid = nodeid;
    templateGridPreView.down('templateGridPreView').down('[itemId=condition]').getStore().proxy.extraParams = {xtType:window.xtType ,nodeid:nodeid};
    templateGridPreView.down('templateGridPreView').initGrid({xtType:window.xtType, nodeid: nodeid, type: '模板预览',table:tabType});
    templateGridPreView.down('templateGridPreView').gridview=templateGridView
    var operator = templateGridPreView.down('fieldcontainer').down('[itemId=operator]');
	var value = templateGridPreView.down('fieldcontainer').down('[itemId=value]');
	var inresult = templateGridPreView.down('fieldcontainer').down('[itemId=inresult]');
	operator.hide();
	value.hide();
	inresult.hide();
    templateGridPreView.show();
}

function initFormField(form, operate, nodeid) {
	// if (form.nodeid != nodeid) {//切换节点后，form和tree的节点id不相等
        form.nodeid = nodeid;//用左侧树节点的id初始化form的nodeid参数
        form.tableType=tabType;
        form.removeAll();//移除form中的所有表单控件
        var field = {
            xtype: 'hidden',
            name: 'entryid'
        };
        form.add(field);
        var formField = form.getFormField(window.xtType);//根据节点id查询表单字段
        var fields = form.getForm().getFields().items;
        if(formField.length==0){
            XD.msg('请检查档号设置信息是否正确');
            return;
        }
        form.templates = formField;
        form.initField(formField,operate);//重新动态添加表单控件
        if(operate=='look' || operate=='lookfile'){
            Ext.each(fields,function (item) {
                item.setReadOnly(true);
            });
        }
	// }
    return '加载表单控件成功';
}

function deleteTemplate(btn,value) {
    var mainview = btn.findParentByType('templateView');
    var treeview = mainview.down('[itemId=templateTreeViewID]');
    var gridview = mainview.down('[itemId=templateGridViewID]');
    if(value==2){
        treeview = mainview.down('[itemId=templateSxTreeViewID]');
        gridview = mainview.down('[itemId=templateSxGridViewID]');
    }
    var downloadForm = document.createElement('form');
    document.body.appendChild(downloadForm);
    var inputTextElement,inputTextElementTwo,inputTextElementTable;
    inputTextElement = document.createElement('input');
    inputTextElementTwo = document.createElement('input');
    inputTextElement.name = 'nodeid';
    inputTextElement.value = treeview.selection.get('fnid');
    inputTextElementTwo.name = 'xtType';
    inputTextElementTwo.value = window.xtType;
    inputTextElementTable = document.createElement('input');
    inputTextElementTable.name="tableType";
    inputTextElementTable.value=tabType;
    downloadForm.appendChild(inputTextElementTable);
    downloadForm.appendChild(inputTextElement);
    downloadForm.appendChild(inputTextElementTwo);
    downloadForm.action = '/template/export';
    downloadForm.submit();

    Ext.Ajax.request({
        params: {xtType:window.xtType, nodeid: treeview.selection.get('fnid'),tableType:tabType},
        url: '/template/deleteTemplateByNodeid',
        method: 'post',
        sync:true,
        success: function () {
            gridview.getStore().reload();
            XD.msg('删除成功');
            Ext.Ajax.request({//删除档号设置数据
                url: '/codesetting/deleteCodesetByNodeid',
                async:false,
                params:{xtType:window.xtType, nodeid:treeview.selection.get('fnid'),tableType:tabType},
                success: function () {
                }
            });
        },
        failure: function (response) {
            XD.msg(Ext.decode(response.responseText).data);
        }
    });
}
function changeToMultiselect(variable, SelectedFormView, DetailFormView) {
    if (variable.getName() == 'splitcodetext') {
        if (!validaSplitCode(variable)) {
            return;
        }
    } else {
        if (!validaLength(variable)) {
            return;
        }
    }

    var boundlist = SelectedFormView.getComponent("itemselectorID").toField.boundList;
    var tostore = boundlist.store;
    var records = [];
    var hiddenvalue = DetailFormView.down('[itemId=hiddenfieldId]').getValue();
    if (tostore.getCount() > 0) {
        for (var i = 0; i < tostore.getCount(); i++) {
            var record = tostore.getAt(i);
            var num = tostore.indexOf(record);
            var temp = record.data.fieldcode.split('∪');
            if (hiddenvalue == temp[1] || hiddenvalue == temp[4]) {
                var changeValue = insertChange(variable.getValue(), record.data.fieldcode, variable.getName());
                record.data.fieldcode = changeValue;   //要改变提交到后台的值
                records.push(record);
                tostore.remove(record);
                tostore.insert(num, records);
                records = [];
            }
        }
    }
}

function validaSplitCode(splitcode) {
    var str = splitcode.getValue();
    if (str.length > 1) {
        XD.msg("只能输入一个符号!");
        splitcode.setValue('');
        return false;
    }
    // var reg = /~|!|@|#|%|_|-|=|\*|\.|\+|\?|\||·/;
    var reg = /_|-|=|(|)|\*|\.|\||·/;
    if (str.match(reg) == null) {
        if (str != "") {
            // XD.msg("本系统只支持以下分割符号：~ ! @ # % _ - = * . + ? | ·");
            XD.msg("支持分割符号为：&nbsp;&nbsp;_&nbsp;&nbsp;-&nbsp;&nbsp;=&nbsp;&nbsp;*&nbsp;&nbsp;.&nbsp;&nbsp;|&nbsp;&nbsp;·");
            splitcode.setValue('');
        } else {
            XD.msg("[分割符号]不能为空!");
        }
        return false;
    } else {
        return true;
    }
}

function validaLength(length) {
    var str = length.getValue();
    var reg = new RegExp("^([0-9])$");
    if (!reg.test(str)) {
        XD.msg("请输入0到9的一位数字");
        length.setValue('');
        return false;
    } else {
        return true;
    }
}

function insertChange(str, changeValue, isSign) {
    var temp = changeValue.split("∪");
    var haveChange = temp[0] + "∪";
    if (temp[0] == "") {
        if (isSign == 'splitcodetext')//分割符号的改变
            temp[3] = str;
        else//单位长度的改变
            temp[4] = str;
        for (var i = 1; i < temp.length; i++) {
            if (i != temp.length - 1)
                haveChange = haveChange + temp[i] + "∪";
            else
                haveChange = haveChange + temp[i];
        }
    } else {
        //从数据库中获得的字段
        if (isSign == 'splitcodetext')//分割符号的改变
            temp[2] = str;
        else//单位长度的改变
            temp[3] = str;
        for (var i = 1; i < temp.length; i++) {
            if (i != temp.length - 1)
                haveChange = haveChange + temp[i] + "∪";
            else
                haveChange = haveChange + temp[i];
        }
    }
    return haveChange
}

//快速调整字段的方法
function fastSettingField(url,setParam,btn) {
    var templateid=[],//id
        templateGridView = btn.findParentByType('templateGridView'),
        select=templateGridView.getSelectionModel();
    if (select.getSelection().length < 1) {
        XD.msg('请选择不少于一条操作记录');
        return;
    }
    var record = select.getSelection();
    for(var i=0;i<record.length;i++){
        templateid[i]=record[i].get('templateid');
    }

    var selarr = [];
    //选择行的行号
    for (var i=0;i<record.length;i++) {
        //T
        selarr[i]=templateGridView.getStore().indexOfId(record[i].id);
        console.log("index行号:"+selarr[i]);
    }
    Ext.Ajax.request({
        params: {templateid:templateid,
            setParam:setParam
        },
        url: url,
        method: "post",
        success: function (response) {
            var respText = Ext.decode(response.responseText);
            if (respText.success == true) {
                templateGridView.getSelectionModel().clearSelections();
                templateGridView.getStore().reload({
                    //回调事件，回复勾选
                    callback : function() {
                        for (var i = 0; i < selarr.length; i++) {
                            select.select(selarr[i], true);
                            console.log("index回复:" + selarr[i]);
                            select.select(selarr[i], true);
                        }
                    }
                });
            }
            XD.msg(respText.msg);
        },
        failure: function () {
            XD.msg('操作中断！');
        }
    });
}
function ffieldfilter(context){
    var ffield=new Array();
    for(var i=0;i<context.length;i++){
        if(context[i].data.ffield==true){
            ffield.push(context[i]);
        }
    }
    return ffield;
}