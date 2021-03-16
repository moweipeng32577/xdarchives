Ext.define('ArchivesMigrate.controller.ArchivesMigrateController', {
    extend: 'Ext.app.Controller',
    views: [
    	'ArchivesMigrateGridView','ArchivesMigrateFormView','ArchivesMigrateDetailGridView','ArchivesMigrateView',
		'ArchivesSearchView','ArchivesSearchGridView'
    ],
    stores: [
		'ArchivesMigrateStore','ArchivesMigrateDetailGridStore'
    ],
    models: [
    	'ArchivesMigrateModel','ArchivesMigrateDetailGridModel'
    ],
    init: function () {
    	var archivesMigrateView;
		var archivesMigrateGridView;
		var archivesMigrateDetailGridView;
		var migId;
    	this.control({
			'archivesMigrateGridView': {
				afterrender: function (view) {
					archivesMigrateGridView=view;
					archivesMigrateView=archivesMigrateGridView.findParentByType("archivesMigrateView");
					view.initGrid();
				}
			},
			'archivesMigrateGridView button[itemId=add]': {//新增迁移
				click: function (view) {
					var select = archivesMigrateGridView.acrossSelections;
					var formView = Ext.create({
						modal: true,
						title:'新增迁移单据',
						xtype: 'archivesMigrateFormView',
						listeners:{
							// "close":function () {
							//
							// }
						}
					});
					if(select.length>0) {
						formView.down("[itemId=migrateuserId]").setValue(select[0].get("migrateuser"));
						formView.down("[itemId=migratedescId]").setValue(select[0].get("migratedesc"));
						formView.down("[itemId=remarksid]").setValue(select[0].get("remarks"));
					}
					formView.down('[itemId = migratecountId]').setHidden(true);
					formView.down('[itemId = migratestateId]').setHidden(true);
					formView.down('[itemId = displayfield]').setHidden(true);
					formView.show();
				}
			},
			'archivesMigrateGridView button[itemId=lookMigrate]': {//查看登记
				click: function (view) {
					var select = archivesMigrateGridView.acrossSelections;
					if(select.length!=1) {
						XD.msg('请选择一条操作记录！');
						return;
					}
					var formView = Ext.create({
						modal: true,
						title:'新增迁移单据',
						xtype: 'archivesMigrateFormView',
					});
					formView.down('[itemId = saveId]').setHidden(true);
					formView.down("form").loadRecord(select[0]);
					formView.show();
				}
			},
			'archivesMigrateGridView button[itemId=migrate]': {//查看单据详情
				click: function (view) {
					var select = archivesMigrateGridView.getSelectionModel();
					if (select.getSelection().length == 0) {
						XD.msg('请选择操作记录');
					} else if (select.getSelection().length > 1) {
						XD.msg('只能选中一条数据');
					} else {
						archivesMigrateDetailGridView=this.findDetailGridView(view);
						this.findView(view).setActiveItem(archivesMigrateDetailGridView);
						migId=select.getSelection()[0].get('migid');
						this.findDetailGridView(view).initGrid({migid:migId });
					}
				}
			},
			'archivesMigrateGridView button[itemId=pack]': {//打包
				click: function (view) {
					var select = archivesMigrateGridView.getSelectionModel();
					if (select.getSelection().length != 1) {
						XD.msg('请选中一条数据');
						return;
					}
					if(select.getSelection()[0].get('migratestate')=="迁移中"){
						XD.msg('有数据在打包中');
						return;
					}else if(select.getSelection()[0].get('migratestate')=="迁移完成"){
						XD.confirm('数据已打过包是否重新打包', function () {
							this.migratePack(view,select);
						},this);
						return;
					}
					this.migratePack(view,select);
				}
			},
			'archivesMigrateGridView button[itemId=download]': {//下载
				click: function (view) {
					var select = archivesMigrateGridView.getSelectionModel();
					if (select.getSelection().length != 1) {
						XD.msg('请选中一条数据');
						return;
					}
					if(select.getSelection()[0].get('migratestate')!="迁移完成"){
						XD.msg('只有迁移完成的数据才能下载');
						return;
					}
					location.href = '/archivesMigrate/downLoadMigratePack?migid='+select.getSelection()[0].get('migid');
				}
			},
			'archivesMigrateFormView button[itemId=saveId]': {//登记单据-新增
				click: function (view) {
					var archivesMigrateFormView = view.findParentByType('archivesMigrateFormView');
					//保存迁移记录
					archivesMigrateFormView.items.get('formitemid').getForm().submit({
						method: 'post',
						url: '/archivesMigrate/save',
						scope: this,
						success: function (form, action) {
							var respText = Ext.decode(action.response.responseText);
							XD.msg(respText.msg);
							if(respText.success){
								archivesMigrateGridView.getStore().reload();
								archivesMigrateFormView.close();
							}
						},failure: function () {
							XD.msg('操作中断');
						}
					});
				}
			},
			'archivesMigrateFormView button[itemId=CancelBtnID]': {//登记单据-取消
				click: function (view) {
					view.up('window').close();
				}
			},'archivesMigrateDetailGridView button[itemId=leadInID]': {//单据详情-导入
				click: function (view) {
					window.leadIn = Ext.create("Ext.window.Window", {
						width: '100%',
						height: '100%',
						title: '导入',
						modal: true,
						header: false,
						draggable: false,//禁止拖动
						resizable: false,//禁止缩放
						closeToolText: '关闭',
						layout: 'fit',
						items: [{xtype: 'archivesSearchView'}]
					}).show();
					window.leadIn.down('archivesSearchGridView').acrossSelections = [];
					var store=window.leadIn.down('archivesSearchGridView').getStore();
					store.proxy.extraParams.type="not";//过滤选中的条目
					store.load();
					Ext.on('resize', function (a, b) {
						window.leadIn.setPosition(0, 0);
						window.leadIn.fitContainer();
					});
				}
			},'archivesMigrateDetailGridView button[itemId=deleteBtnID]': {//单据详情-删除
				click: function (view) {
					var select = archivesMigrateDetailGridView.getSelectionModel();
					if (!select.hasSelection()) {
						XD.msg('请选择需要删除的数据');
						return;
					}
					var datas = select.getSelection();
					var array = [];
					for (var i = 0; i < datas.length; i++) {
						array[i] = datas[i].get('entryid');
					}
					XD.confirm('确定要删除这' + array.length + '条数据吗', function () {
						Ext.Ajax.request({
							params: {
								entryids: array,
								migid: migId
							},
							url: '/archivesMigrate/deletArchivesMigrateEntry',
							method: 'POST',
							sync: true,
							success: function (resp, opts) {
								var respText = Ext.decode(resp.responseText);
								XD.msg(respText.msg);
								if(respText.success)
									archivesMigrateDetailGridView.initGrid({migid: migId, type: ""});
							},
							failure: function () {
								XD.msg('操作失败');
							}
						});
					},this);
				}
			},'archivesMigrateDetailGridView button[itemId=seeBtnID]': {//单据详情-查看
				click: function (view) {

				}
			},'archivesMigrateDetailGridView button[itemId=back]': {//单据详情-返回
				click: function (view) {
					this.findView(view).setActiveItem(this.findGridView(view));
					this.findGridView(view).initGrid();
				}
			},
			'archivesSearchGridView button[itemId=searchleadinId]': {//导入条目列表-导入
				click: function (view) {
					var archivesSearchGridView = view.findParentByType('archivesSearchGridView');
					var select = archivesSearchGridView.getSelectionModel();
					if (!select.hasSelection()) {
						XD.msg('请选择需要导入的数据');
						return;
					}
					var datas = select.getSelection();
					var array = [];
					for (var i = 0; i < datas.length; i++) {
						array[i] = datas[i].get('entryid');
					}
					//= archivesMigrateGridView.getSelectionModel().getSelection()[0].get("migId");
					XD.confirm('确定要导入这' + array.length + '条数据吗', function () {
						Ext.Ajax.request({
							params: {
								entryids: array,
								migid: migId
							},
							url: '/archivesMigrate/saveArchivesMigrateEntry',
							method: 'POST',
							sync: true,
							success: function (resp, opts) {
								var respText = Ext.decode(resp.responseText);
								XD.msg(respText.msg);
								window.leadIn.setVisible(false);
								archivesMigrateDetailGridView.initGrid({migid: migId, type: ""});
							},
							failure: function () {
								XD.msg('操作失败');
							}
						});
					},this);
				}
			},
			'archivesSearchGridView button[itemId=archivesSearchShowId]': {//导入条目列表-查看
				click: function (view) {
					var archivesSearchGridView = view.findParentByType('archivesSearchGridView');
					var record = archivesSearchGridView.getSelectionModel().getSelection();
					if (record.length == 0) {
						XD.msg('请至少选择一条需要查看的数据');
						return;
					}
					var entryids = [];
					var nodeids = [];
					for(var i=0;i<record.length;i++){
						entryids.push(record[i].get('entryid'));
						nodeids.push(record[i].get('nodeid'));
					}
					var entryid = record[0].get('entryid');
					var form = view.findParentByType("archivesSearchView").down('EntryFormView').down('dynamicform');
					form.operate = 'look';
					form.entryids = entryids;
					form.nodeids = nodeids;
					form.entryid = entryids[0];
					this.initFormField(form, 'hide', record[0].get('nodeid'));
					this.initFormData('look', form, entryid);
				}
			},
			'archivesSearchGridView button[itemId=archivesSearchBackId]': {//导入条目列表-返回
				click: function (view) {
					if (window.leadIn != null) {
						archivesMigrateView.setActiveItem(archivesMigrateDetailGridView);
						archivesMigrateDetailGridView.initGrid({migid:migId });
						window.leadIn.close();
					}
				}
			},
			'EntryFormView [itemId=preBtn]':{
				click:this.preHandler
			},
			'EntryFormView [itemId=nextBtn]':{
				click:this.nextHandler
			},
			'electronicPro': {
				render: function (view) {
					var buttons = view.down('toolbar').query('button');
					for (var i = 0; i < buttons.length; i++) {
						if (buttons[i].text == '上传' || buttons[i].text == '删除' || buttons[i].text == '返回') {
							continue;
						}
						buttons[i].hide();
					}
				}
			},
			'archivesSearchView [itemId=archivesSearchfieId]': {	//条目检索
				search: function (searchfield) {
					//获取检索框的值
					var archivesSearchSearchView = searchfield.findParentByType('panel');
					var condition = archivesSearchSearchView.down('[itemId=archivesSearchComboId]').getValue(); //字段
					var operator = 'like';//操作符
					var content = searchfield.getValue(); //内容
					//检索数据
					//如果有勾选在结果中检索，则添加检索条件，如果没有勾选，则重置检索条件
					var grid = archivesSearchSearchView.findParentByType('panel').down('archivesSearchGridView');
					var gridstore = grid.getStore();
					//加载列表数据
					var searchcondition = condition;
					var searchoperator = operator;
					var searchcontent = content;
					var inresult = archivesSearchSearchView.down('[itemId=inresult]').getValue();
					if (inresult) {
						var params = gridstore.getProxy().extraParams;
						if (typeof(params.condition) != 'undefined') {
							searchcondition = [params.condition, condition].join(XD.splitChar);
							searchoperator = [params.operator, operator].join(XD.splitChar);
							searchcontent = [params.content, content].join(XD.splitChar);
						}
					}

					grid.dataParams = {
						condition: searchcondition,
						operator: searchoperator,
						entryids:window.leadIn.entryids,
						content: searchcontent
					};

					//检索数据前,修改column的renderer，将检索的内容进行标红
					Ext.Array.each(grid.getColumns(), function () {
						var column = this;
						if (column.dataIndex == condition) {
							column.renderer = function (value) {
								var contentData = content.split(' ');//切割以空格分隔的多个关键词
								var reg = new RegExp(contentData.join('|'), 'g');
								return value.replace(reg, function (match) {
									return '<span style="color:red">' + match + '</span>';
								});
							}
						}
					});
					grid.initGrid();
					grid.parentXtype = 'archivesSearchView';
					grid.formXtype = 'EntryFormView';
				}
			},
    	})
    },
	//迁移打包入口
	migratePack:function(view,select){
    	var _this=this;
		Ext.Ajax.request({
			params: {
				migid: select.getSelection()[0].get('migid')
			},
			url: '/archivesMigrate/migratePack',
			method: 'POST',
			sync: true,
			success: function (resp, opts) {
				Ext.MessageBox.hide();
				//打包中打开定时器
				result = resp.responseText;
				if(result==0) {
					XD.msg("数据开始打包");
					_this.findGridView(view).initGrid();
					_this.migratePackTimer(view);
				}else {
					XD.msg("数据打包中");
				}
			},
			failure: function () {
				XD.msg('操作失败');
			}
		});
	},
	//定时器 判断是否打包完成
	migratePackTimer:function(view){
		var _this = this;
		var x = setInterval(function(){
			var result = 0;
			Ext.Ajax.request({
				method : 'GET' ,
				url:  '/archivesMigrate/isMigrate', //请求路径
				async:false,
				timeout:1000,
				success: function (repon) {
					result = repon.responseText;
					//完成刷新数据
					if(result==0){
						_this.findGridView(view).initGrid();
						window.clearInterval(x);
						XD.msg("数据打包完成");
					}
				}
			});
			return result;
		}, 1000);
		x;
	},
	//点击上一条
	preHandler:function(btn){
		var formView =  btn.findParentByType("EntryFormView");
		var form = formView.down('dynamicform');
		this.refreshFormData(form, 'pre');
	},

	//点击下一条
	nextHandler:function(btn){
		var formView = btn.findParentByType("EntryFormView");
		var form = formView.down('dynamicform');
		this.refreshFormData(form, 'next');
	},
	refreshFormData:function(form, type){
		var entryids = form.entryids;
		var nodeids = form.nodeids;
		var currentEntryid = form.entryid;
		var entryid;
		var nodeid;
		for(var i=0;i<entryids.length;i++){
			if(type == 'pre' && entryids[i] == currentEntryid){
				if(i==0){
					i=entryids.length;
				}
				entryid = entryids[i-1];
				nodeid = nodeids[i-1];
				break;
			}else if(type == 'next' && entryids[i] == currentEntryid){
				if(i==entryids.length-1){
					i=-1;
				}
				entryid = entryids[i+1];
				nodeid = nodeids[i+1];
				break;
			}
		}
		form.entryid = entryid;
		if(form.operate != 'undefined'){
			this.initFormField(form, 'hide', nodeid);//上下条时切换模板
			this.initFormData(form.operate, form, entryid);
			return;
		}
		this.initFormField(form, 'hide', nodeid);
		this.initFormData('look', form, entryid);
	},
	//获取档案迁移应用视图
	findView: function (btn) {
		return btn.findParentByType('archivesMigrateView');
	},
	findGridView: function (btn) {
		return this.findView(btn).down('archivesMigrateGridView');
	},
	findDetailGridView: function (btn) {
		return this.findView(btn).down('archivesMigrateDetailGridView');
	},
	initFormField: function (form, operate, nodeid) {
		form.nodeid = nodeid;//用左侧树节点的id初始化form的nodeid参数
		form.removeAll();//移除form中的所有表单控件
		var field = {
			xtype: 'hidden',
			name: 'entryid'
		};
		form.add(field);
		var formField = form.getFormField();//根据节点id查询表单字段
		if(formField.length==0){
			XD.msg('请检查模板设置信息是否正确');
			return;
		}
		form.templates = formField;
		form.initField(formField,operate);//重新动态添加表单控件
		return '加载表单控件成功';
	},
	initFormData: function (operate, form, entryid) {
		var formview = form.up('EntryFormView');
		var nullvalue = new Ext.data.Model();
		var fields = form.getForm().getFields().items;
		if(operate == 'look') {
			for (var i = 0; i < form.entryids.length; i++) {
				if (form.entryids[i] == entryid) {
					count = i + 1;
					break;
				}
			}
			var total = form.entryids.length;
			var totaltext = form.down('[itemId=totalText]');
			totaltext.setText('当前共有  ' + total + '  条，');
			var nowtext = form.down('[itemId=nowText]');
			nowtext.setText('当前记录是第  ' + count + '  条');

			Ext.each(fields,function (item) {
				item.setReadOnly(true);
			});
		}else{
			Ext.each(fields,function (item) {
				if(!item.freadOnly){
					item.setReadOnly(false);
				}
			});
		}
		for(var i = 0; i < fields.length; i++){
			if(fields[i].value&&typeof(fields[i].value)=='string'&&fields[i].value.indexOf('label')>-1){
				continue;
			}
			if(fields[i].xtype == 'combobox'){
				fields[i].originalValue = null;
			}
			nullvalue.set(fields[i].name, null);
		}
		form.loadRecord(nullvalue);
		this.activeForm(form);
		Ext.Ajax.request({
			method: 'GET',
			scope: this,
			url: '/management/entries/' + entryid,
			success: function (response) {
				if (operate != 'look') {
					var settingState = ifSettingCorrect(form.nodeid, form.templates);
					if (!settingState) {
						return;
					}
				}
				var entry = Ext.decode(response.responseText);
				form.loadRecord({
					getData: function () {
						return entry;
					}
				});
				//字段编号，用于特殊的自定义字段(范围型日期)
				var fieldCode = form.getRangeDateForCode();
				if (fieldCode != null) {
					//动态解析数据库日期范围数据并加载至两个datefield中
					form.initDaterangeContent(entry);
				}
				//初始化原文数据
				var eleview = formview.down('electronic');
				eleview.initData(entryid);
				var solidview = formview.down('solid');
				solidview.initData(entryid);

				form.fileLabelStateChange(eleview, operate);
				form.fileLabelStateChange(solidview, operate);
			}
		});
	},
})