var eventView, addView, type, eventGridView;
Ext.define('DataEvent.controller.DataEventController', {
    extend: 'Ext.app.Controller',

    views: [
    	'DataEventView', 'DataEventDetailGridView',
    	'DataEventGridView', 'DataEventAddView', 
    	'DataEventFormView', 'DataEventSetFormView',
    	'SimpleSearchView', 'SimpleSearchGridView'
    ],
    stores: [
    	'DataEventStore', 'SimpleSearchGridStore'
    ],
    models: [
    	'DataEventModel', 'SimpleSearchGridModel'
    ],
    
    init: function () {
    	this.control({
    		'dataEventView': {//加载事件管理
    			render: function (view) {
    				var dataEventGridView = view.down('dataEventGridView');
    				dataEventGridView.initGrid();
    				
    				eventView = view;
    				eventGridView = dataEventGridView;
    			}
    		},
    		// 添加事件
    		'dataEventGridView [itemId=addEvent]': {
    			click: function (button) {
    				// 窗口每次都需要重建
    				var dataEventAddView = Ext.create('DataEvent.view.DataEventAddView');
    				dataEventAddView.show();
    				
    				addView = dataEventAddView;
    				type = '添加';
    			}
    		},
    		// 添加事件 - 确认提交按钮
    		'dataEventAddView [itemId=sure]': {
    			click: function (button) {
    				var refiditemid = addView.down('[itemId=refiditemid]').value;
    				if (refiditemid == '') {
    					XD.msg('事件描述不能为空！');
    					return;
    				}
    				var transunitid = addView.down('[itemId=transunitid]').value;
    				if (transunitid == '') {
    					XD.msg('事件获取不能为空！');
    					return;
    				}
    				if (type == '修改') {
    					var eventid = eventGridView.selection.id;
    				}
    				Ext.Ajax.request({
						params: {
		                    eventname: refiditemid,
		                    eventnumber: transunitid,
		                    eventid: eventid,
		                    type: type
		                },
		                url: '/dataEvent/addDataEvent',
		                method: 'post',
		                sync: true,
		                success: function (response) {
							XD.msg(Ext.decode(response.responseText).msg);
							// 刷新表单
							eventGridView.initGrid();
							button.up('window').hide();
		                },
		                failure: function () {
		                	Ext.MessageBox.hide();
		                    XD.msg('操作中断');
		                }
					});
    			}
    		},
    		// 修改事件
    		'dataEventGridView [itemId=updateEvent]': {
    			click: function (button) {
    				// 获取到当前表单中的已选择数据
    				var record = eventGridView.getSelectionModel().selected;
    				if (record.length < 1) {
    					XD.msg('请选择一条需要进行修改的档案关联');
    					return;
    				}
    				var dataEventAddView = eventView.down('dataEventAddView');
    				// 获取到当前选中的数据信息
    				var eventname = record.items[0].data.eventname;
    				var eventnumber = record.items[0].data.eventnumber;
    				// 填充到添加档案关联窗口
    				dataEventAddView.down('[itemId=refiditemid]').setValue(eventname);
    				dataEventAddView.down('[itemId=transunitid]').setValue(eventnumber);
    				dataEventAddView.show();
    				
    				addView = dataEventAddView;
    				type = '修改';
    			}
    		},
    		// 删除事件
    		'dataEventGridView [itemId=deleteEvent]': {
    			click: function (button) {
    				// 获取到当前表单中的已选择数据
    				var record = eventGridView.getSelectionModel().selected;
    				if (record.length < 1) {
    					XD.msg('请选择一条需要进行删除的档案关联');
    					return;
    				}
    				XD.confirm('确定要删除这' + record.items.length + '条数据吗',function(){
            			Ext.MessageBox.wait('正在删除数据...','提示');
	    				var eventid = "";
	    				for (var i = 0; i < record.items.length; i++) {
	    					if (i < record.items.length - 1) {
	    						eventid += record.items[i].id + ",";
	    					} else {
	    						eventid += record.items[i].id;
	    					}
	    				}
						Ext.Ajax.request({
							params: {
			                    eventid: eventid
			                },
			                url: '/dataEvent/deleteEvent',
			                method: 'post',
			                sync: true,
			                success: function (response) {
			                	Ext.MessageBox.hide();
								XD.msg(Ext.decode(response.responseText).msg);
								// 刷新表单
								eventGridView.initGrid();
			                },
			                failure: function () {
			                	Ext.MessageBox.hide();
			                    XD.msg('操作中断');
			                }
						});
    				},this);
    			}
    		},
    		// 查看档案关联事件
    		'dataEventGridView [itemId=lookEvent]': {
    			click: function (button) {
    				var dataEventDetailGridView = eventView.down('dataEventDetailGridView');
    				// 获取到当前表单中的已选择数据
    				var record = eventGridView.getSelectionModel().selected;
    				if (record.length < 1) {
    					XD.msg('请选择一条需要查看的档案关联');
    					return;
    				}
    				if (record.length > 1) {
    					XD.msg('只能选择一条需要查看的档案关联');
    					return;
    				}
    				
    				// 显示档案关联的所有条目
					dataEventDetailGridView.initGrid({eventid: record.items[0].data.eventid});
    				// 显示所有条目视图
    				eventView.setActiveItem(dataEventDetailGridView);
    			}
    		},
    		// 关闭添加&修改事件窗口
    		'dataEventAddView [itemId=close]': {
    			click: function (button) {
    				button.up('window').hide();
    			}
    		},
    		// 添加事件 - 导入
    		'dataEventDetailGridView [itemId=leadInID]': {
    			click: function (button) {
    				eventView.eventid = eventGridView.selection.id;
    				this.initSearchView();
    			}
    		},
    		// 添加事件 - 删除
    		'dataEventDetailGridView [itemId=deleteBtnID]': {
    			click: function (button) {
    				var dataEventDetailGridView = eventView.down('dataEventDetailGridView');
                    var select = dataEventDetailGridView.getSelectionModel();
                    if (!select.hasSelection()) {
                        XD.msg('请选择需要删除的数据');
                        return;
                    }
                    XD.confirm('确定要删除这' + select.selected.length + '条数据吗',function(){
            			Ext.MessageBox.wait('正在删除数据...','提示');
	                    var eventid = dataEventDetailGridView.dataParams.eventid;
	                    var entryid = "";
	                    for (var i = 0; i < select.selected.length; i++) {
	                    	if (i < select.selected.length - 1) {
	                    		entryid += select.selected.items[i].data.entryid + ",";
	                    	} else {
	                    		entryid += select.selected.items[i].data.entryid;
	                    	}
	                    }
	                    Ext.Ajax.request({
	                        params: {
	                        	eventid: eventid,
	                            entryid: entryid
	                        },
	                        url: '/dataEvent/deleteEventEntry',
	                        method: 'POST',
	                        sync: true,
	                        success: function (resp, opts) {
	                        	Ext.MessageBox.hide();
	                            var respText = Ext.decode(resp.responseText);
	                            XD.msg(respText.msg);
	                            dataEventDetailGridView.initGrid({eventid: eventid});
	                        },
	                        failure: function () {
	                        	Ext.MessageBox.hide();
	                            XD.msg('操作失败');
	                        }
	                    });
                    },this);
    			}
    		},
    		// 添加事件 - 查看
    		'dataEventDetailGridView [itemId=seeBtnID]': {
    			click: function (button) {
    				var dataEventDetailGridView = eventView.down('dataEventDetailGridView');
                    var select = dataEventDetailGridView.getSelectionModel();
                    if (select.selected.length != 1) {
                        XD.msg('请选择需要一条需要查看的数据');
                        return;
                    }
                    
                    var entryids = [];
                    var nodeids = [];
                    for(var i = 0; i < select.selected.length; i++){
                        entryids.push(select.selected.items[i].get('entryid'));
                        nodeids.push(select.selected.items[i].get('nodeid'));
                    }
                    var dataEventFormView = eventView.down('dataEventFormView');
    				var form = dataEventFormView.down('dynamicform');
                    form.operate = 'look';
                    form.entryids = entryids;
                    form.nodeids = nodeids;
                    form.entryid = entryids[0];
                    type = '查看';
                    this.initFormField(form, 'hide', select.selected.items[0].get('nodeid'));
                    this.initFormData('look', form, select.selected.items[0].get('entryid'), eventView);
                    
                    dataEventFormView.down('electronic').entryid = entryids[0];
                    this.activeForm(form, eventView);
    			}
    		},
    		// 显示电子文件视图
            'dataEventDetailGridView': {
            	eleview: this.activeEleForm
            },
    		// 添加事件 - 查看条目 - 返回
    		'dataEventFormView [itemId=back]': {
    			click: function (button) {
    				var dataEventDetailGridView = eventView.down('dataEventDetailGridView');
    				eventView.setActiveItem(dataEventDetailGridView);
    			}
    		},
    		// 添加事件 - 返回
    		'dataEventDetailGridView [itemId=back]': {
    			click: function (button) {
    				eventGridView.initGrid();
    				// 显示添加档案关联视图
    				eventView.setActiveItem(eventGridView);
    			}
    		},
    		// 导入条目 - 简单检索
    		'simpleSearchView [itemId=simpleSearchSearchfieldId]': {
                search: function (searchfield) {
                    //获取检索框的值
                    var simpleSearchSearchView = searchfield.findParentByType('panel');
                    var condition = simpleSearchSearchView.down('[itemId=simpleSearchSearchComboId]').getValue(); //字段
                    var operator = 'like';//操作符
                    var content = searchfield.getValue(); //内容
                    //检索数据
                    //如果有勾选在结果中检索，则添加检索条件，如果没有勾选，则重置检索条件
                    var grid = simpleSearchSearchView.findParentByType('panel').down('simpleSearchGridView');
                    var gridstore = grid.getStore();
                    //加载列表数据
                    var searchcondition = condition;
                    var searchoperator = operator;
                    var searchcontent = content;
                    var inresult = simpleSearchSearchView.down('[itemId=inresult]').getValue();
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
                    grid.parentXtype = 'simpleSearchView';
                    grid.formXtype = 'EntryFormView';
                }
            },
    		// 简单检索界面 - 正式导入条目
    		'simpleSearchGridView [itemId=searchleadinId]': {
    			click: function (view) {
    				var simpleSearchGridView = view.findParentByType('simpleSearchGridView');
                    var select = simpleSearchGridView.getSelectionModel();
                    if (!select.hasSelection()) {
                        XD.msg('请选择需要导入的数据');
                        return;
                    }

                    var datas = select.getSelection();
                    var array = [];
                    for (var i = 0; i < datas.length; i++) {
                        array[i] = datas[i].get('entryid');
                    }
                    eventView.entryid = array;
                    Ext.Ajax.request({
                        params: {
                        	eventid: eventView.eventid,
                            entryid: array
                        },
                        url: '/dataEvent/leadInEntry',
                        method: 'POST',
                        sync: true,
                        success: function (resp, opts) {
                            var respText = Ext.decode(resp.responseText);
                            XD.msg(respText.msg);
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
    			}
    		},
    		// 导入条目 - 查看条目信息
    		'simpleSearchGridView [itemId=simpleSearchShowId]': {
    			click: function (view) {
    				var simpleSearchGridView = view.up('simpleSearchGridView');
                    var select = simpleSearchGridView.getSelectionModel().selected;
                    if (select.items.length != 1) {
                        XD.msg('请选择需要一条需要查看的数据');
                        return;
                    }
                    
                    var entryids = [];
                    var nodeids = [];
                    for(var i = 0; i < select.items.length; i++){
                        entryids.push(select.items[i].get('entryid'));
                        nodeids.push(select.items[i].get('nodeid'));
                    }
    				var form = eventView.down('dataEventFormView').down('dynamicform');
                    form.operate = 'look';
                    form.entryids = entryids;
                    form.nodeids = nodeids;
                    form.entryid = entryids[0];
                    type = '导入查看';
                    this.initFormField(form, 'hide', select.items[0].get('nodeid'));
                    this.initFormData('look', form, select.items[0].get('entryid'), simpleSearchGridView.up('simpleSearchView'));
                    
                    // 刷新电子文件视图显示
                    form.down('electronic').entryid = entryids[0];
                    
                    this.activeForm(form, simpleSearchGridView.up('simpleSearchView'));
    			}
    		},
    		// 导入条目 - 关闭当前页面
    		'simpleSearchGridView [itemId=simpleSearchBackId]': {
    			click: function (view) {
    				// 关闭当前检索页面
    				window.leadIn.setVisible(false);
    				
    				// 刷新数据关联条目表格页面
                    eventView.down('dataEventDetailGridView').initGrid({eventid: eventView.eventid});
    			}
    		}
    	})
    },
    
    activeEleForm:function(obj){
        var formview = eventView.down('dataEventSetFormView').down('dataEventFormView');
    	formview.type = '数据关联';
    	
        eventView.setActiveItem(formview.findParentByType('panel'));
        formview.items.get(0).disable();
        var eleview = formview.down('electronic');
        var solidview = formview.down('solid');
        eleview.operateFlag = "look"; //电子文件查看标识符
        solidview.operateFlag = "look";//利用文件查看标识符
        eleview.initData(obj.entryid);
        solidview.initData(obj.entryid);
        var from =formview.down('dynamicform');
        //电子文件按钮权限
        var elebtns = eleview.down('toolbar').query('button');
        from.getELetopBtn(elebtns,eleview.operateFlag );
        var soildbtns = solidview.down('toolbar').query('button');
        from.getELetopBtn(soildbtns,solidview.operateFlag);
        formview.setActiveTab(1);
        return formview;
    },
    
    initSearchView: function () {
    	window.leadIn = Ext.create("Ext.window.Window", {
	        width: '100%',
	        height: '100%',
	        title: '办理',
	        modal: true,
	        header: false,
	        draggable: false,//禁止拖动
	        resizable: false,//禁止缩放
	        closeToolText: '关闭',
	        layout: 'fit',
	        items: [{xtype: 'simpleSearchView'}]
	    }).show();
	    window.leadIn.down('simpleSearchGridView').acrossSelections = [];
	    Ext.on('resize', function (a, b) {
	        window.leadIn.setPosition(0, 0);
	        window.leadIn.fitContainer();
	    });
    },
    
    initFormField: function (form, operate, nodeid) {
//        if (form.nodeid != nodeid) {
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
//        }
        return '加载表单控件成功';
    },
    
    //切换到表单界面视图
    activeForm: function (view, parentView) {
    	var dataEventSetFormView = eventView.down('dataEventSetFormView');
    	parentView.setActiveItem(dataEventSetFormView);
    	
    	var dataEventFormView = dataEventSetFormView.down('dataEventFormView');
    	dataEventFormView.items.get(0).enable();
    	dataEventFormView.setActiveTab(0);
    	return eventView;
    },
    
    //初始化条目表单界面
    initFormData: function(operate, form, entryid, view){
        var prebtn = form.down('[itemId=preBtn]');
        var nextbtn = form.down('[itemId=nextBtn]');
        
        var count = 1;
        for(var i = 0; i < form.entryids.length; i++){
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
        
        var fields = form.getForm().getFields().items;
        var nullvalue = new Ext.data.Model();
        for (var i = 0; i < fields.length; i++) {
            if (fields[i].value && typeof(fields[i].value) == 'string' && fields[i].value.indexOf('label') > -1) {
                continue;
            }
            if (fields[i].xtype == 'combobox') {
                fields[i].originalValue = null;
            }
            nullvalue.set(fields[i].name, null);
        }
        form.loadRecord(nullvalue);
        
    	Ext.each(fields,function (item) {
            item.setReadOnly(true);
        });
        var eleview = view.down('electronic');
        var solidview = view.down('solid');
    	Ext.Ajax.request({
            method: 'GET',
            scope: this,
            url: '/management/entries/' + entryid,
            success: function (response) {
                var entry = Ext.decode(response.responseText);
                var data = Ext.decode(response.responseText);
                if (data.organ) {
                	entry.organ = data.organ;//机构
                }
                form.loadRecord({getData: function () {return entry;}});
                var fieldCode = form.getRangeDateForCode();//字段编号，用于特殊的自定义字段(范围型日期)
                if (fieldCode != null) {
                    //动态解析数据库日期范围数据并加载至两个datefield中
                    form.initDaterangeContent(entry);
                }
                eleview.initData(entry.entryid);
                solidview.initData(entry.entryid);
            }
        });
        form.fileLabelStateChange(eleview, operate);
        form.fileLabelStateChange(solidview, operate);
    }
})