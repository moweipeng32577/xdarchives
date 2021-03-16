/**
 * Created by RonJiang on 2018/01/24
 */
Ext.define('DuplicateChecking.controller.DuplicateCheckingController', {
    extend: 'Ext.app.Controller',
    views: [
        'DuplicateCheckingView',
        'DuplicateCheckingPromptView',
        'DuplicateCheckingFormView',
        'DuplicateCheckingSelectView',
        'DuplicateCheckingGridView',
        'DuplicateCheckingEntryView',
        'DuplicateCheckingExportView',
        'DuplicateCheckingTreeView'
    ],
    models: ['DuplicateCheckingGridModel', 'DuplicateCheckingTreeModel'],
    stores: ['DuplicateCheckingGridStore', 'DuplicateCheckingTreeStore', 'DuplicateCheckingSelectStore'],
    init: function () {
        var count = 0;
        var ifShowRightPanel = false;
        this.control({
            'duplicateCheckingTreeView': {
                select: function (treemodel, record) {
                    var duplicateCheckingView = treemodel.view.findParentByType('duplicateChecking');
                    var duplicateCheckingPromptView = duplicateCheckingView.down('duplicateCheckingPromptView');
                    if (!ifShowRightPanel) {
                        duplicateCheckingPromptView.removeAll();
                        duplicateCheckingPromptView.add({
                            xtype: 'duplicateCheckingFormView'
                        });
                        ifShowRightPanel = true;
                    }
                    var duplicateCheckingGrid = duplicateCheckingView.down('duplicateCheckingGridView');
                    duplicateCheckingGrid.nodeid = record.get('fnid');
                    duplicateCheckingGrid.initGrid({nodeid:duplicateCheckingGrid.nodeid});
                    var advancedSearchDynamicForm = duplicateCheckingView.down('advancedSearchDynamicForm');
                    this.initAdvancedSearchFormField(advancedSearchDynamicForm, record.get('fnid'));
                }
            },
            'duplicateCheckingFormView': {
                render: function (field) {
                    var topLogicCombo = field.getComponent('topLogicCombo');
                    var bottomLogicCombo = field.getComponent('bottomLogicCombo');
                    topLogicCombo.on('change', function (view) {//点击顶部逻辑下拉选，则同步底部逻辑下拉选的值
                        bottomLogicCombo.setValue(view.lastValue);
                    });
                    bottomLogicCombo.on('change', function (view) {//点击底部逻辑下拉选，则同步顶部逻辑下拉选的值
                        topLogicCombo.setValue(view.lastValue);
                    })
                }
            },
            'duplicateCheckingSelectView button[itemId=close]': {
                click: function (btn) {
                    btn.findParentByType('duplicateCheckingSelectView').close();
                }
            },
            'duplicateCheckingSelectView button[itemId=submit]': {
                click: function (btn) {
                    var duplicateCheckingSelectWin = btn.findParentByType('duplicateCheckingSelectView');
                    var selectValue = duplicateCheckingSelectWin.down('[itemId=itemselectorID]').getValue();
                    if (selectValue.length === 0) {
                        XD.msg('请选择查重字段');
                        return;
                    }
                    var treeView = btn.findParentByType('duplicateCheckingSelectView').treeView;
                    var mainView = treeView.up('duplicateChecking');
                    var form = mainView.down('[itemId=duplicateCheckingFormViewId]');
                    var grid = mainView.down('duplicateCheckingGridView');
                    var filedateStartField = form.getForm().findField('filedatestart');
                    var filedateEndField = form.getForm().findField('filedateend');
                    if (filedateStartField != null && filedateEndField != null) {
                        var filedateStartValue = filedateStartField.getValue();
                        var filedateEndValue = filedateEndField.getValue();
                        if (filedateStartValue > filedateEndValue) {
                            XD.msg('开始日期必须小于或等于结束日期');
                            return;
                        }
                    }
                    var formValues = form.getValues();//获取表单中的所有值(类型：js对象)
                    var formParams = {};
                    var fieldColumn = [];
                    for (var name in formValues) {//遍历表单中的所有值
                        formParams[name] = formValues[name];
                        if(typeof(formValues[name]) != "undefined" && formValues[name] != '' && formValues[name] != 'and' &&
                            formValues[name] != 'like' && formValues[name] != 'equal' && formValues[name] != 'or'){
                            fieldColumn.push(name);
                        }
                    }
                    formParams['multiValue'] = selectValue;
                    formParams['nodeid'] = treeView.selection.get('fnid');
                    formParams['ifSearchLeafNode'] = false;//点击非叶子节点时，是否查询出其包含的所有叶子节点数据
                    formParams['ifContainSelfNode'] = false;//点击非叶子节点时，是否查询出当前非叶子节点及其包含的所有非叶子节点数据
                    Ext.Array.each(grid.getColumns(), function(item){
                        if(item.xtype == 'gridcolumn'){
                            item.renderer = function(value){
                                return value;
                            }
                        }
                        var columnValue = formParams[item.dataIndex];
                        if ($.inArray(item.dataIndex,fieldColumn)!=-1) {
                            var searchstrs=[];
                            searchstrs.push(columnValue);
                            item.renderer = function (v) {
                                if(typeof(v) != "undefined"){
                                    var reTag = /<(?:.|\s)*?>/g;
                                    var value = v.replace(reTag, "");
                                    var reg = new RegExp(searchstrs.join('|'), 'g');
                                    return value.replace(reg, function (match) {
                                        return '<span style="color:red">' + match + '</span>'
                                    });
                                }
                            }
                            item.renderer.isSearchRender = true;
                        }
                    });
                    duplicateCheckingSelectWin.hide();
                    if(fieldColumn.length==0){
                        grid.initGrid(formParams)
                    }else{
                        grid.dataParams = formParams;
                        var store = grid.getStore();
                        Ext.apply(store.getProxy(),{
                            extraParams:formParams
                        });
                        store.loadPage(1);
                    }
                    mainView.setActiveItem(grid);
                    grid.parentXtype = 'duplicateChecking';
                    grid.formXtype = 'duplicateCheckingEntryView';
                }
            },
            'duplicateCheckingGridView [itemId=download]': {//导出
                click: function (btn) {
                    var duplicateCheckingGridView = btn.findParentByType('duplicateCheckingGridView');
                    var record = duplicateCheckingGridView.getSelectionModel().getSelection();
                    if (record.length == 0) {
                        XD.msg('请至少选择一条需要导出的数据');
                        return;
                    }
                    var exportWin = Ext.create('DuplicateChecking.view.DuplicateCheckingExportView', {gridView: duplicateCheckingGridView});
                    exportWin.show();
                }
            },
            'duplicateCheckingExportView [itemId=close]': {
                click: function (btn) {
                    btn.findParentByType('window').close();
                }
            },
            'duplicateCheckingExportView [itemId=export]': {
                click: function (btn) {
                    var exportFileName = btn.findParentByType('duplicateCheckingExportView').down('[itemId=filename]').getValue();
                    var gridView = btn.findParentByType('duplicateCheckingExportView').gridView;
                    var selection = gridView.getSelectionModel().getSelection();
                    var idArray = [];
                    //--文件名判断
                    if(''==exportFileName){
                        XD.msg('文件名称不能为空');
                        return;
                    }
                    var pattern = new RegExp("[/:*?\"<>|]");
					if(pattern.test(exportFileName) || exportFileName.indexOf('\\') > -1) {
						XD.msg("文件名称不能包含下列任何字符：\\/:*?\"<>|");
						return;
					} 
                    exportFileName = encodeURIComponent(exportFileName);//拼接的方法需要转换
                    for (var i = 0; i < selection.length; i++) {
                        idArray.push(selection[i].get('entryid'))
                        idArray[i] = selection[i].get('entryid');
                    }
                    var columnArray = [];
                    var columns = gridView.columnManager.getColumns();
                    for (var j = 0; j < columns.length; j++) {
                        if (columns[j].xtype == 'gridcolumn') {
                            var subtext = columns[j].dataIndex + '-' + columns[j].text;
                            columnArray.push(subtext);
                        }
                    }
                    var downloadForm = document.createElement('form');
                    document.body.appendChild(downloadForm);
                    var params = [];
                    params['exportFileName'] = exportFileName;
                    params['idArray'] = idArray;
                    params['columnArray'] = columnArray;
                    var inputTextElement;
                    for (var prop in params){
                        inputTextElement = document.createElement('input');
                        inputTextElement.name = prop;
                        inputTextElement.value = params[prop];
                        downloadForm.appendChild(inputTextElement);
                    }
                    downloadForm.action = '/duplicateChecking/export';
                    downloadForm.submit();
                    btn.findParentByType('window').close();
                }
            },
            'duplicateCheckingGridView [itemId=edit]': {//修改
                click: function (btn) {
                    var duplicateCheckingFormView = this.findFormView(btn);
                    duplicateCheckingFormView.saveBtn = duplicateCheckingFormView.down('[itemId=save]');
                    duplicateCheckingFormView.operateFlag = 'modify';
                    var duplicateCheckingGridView = this.findGridView(btn);
                    var record = duplicateCheckingGridView.getSelectionModel().getSelection();
                    if (record.length == 0) {
                        XD.msg('请至少选择一条需要修改的数据');
                        return;
                    }
                    var entryids = [];
                    var nodeids = [];
                    for(var i=0;i<record.length;i++){
                        entryids.push(record[i].get('entryid'));
                        nodeids.push(record[i].get('nodeid'));
                    }
                    var entryid = record[0].get('entryid');
                    var form = duplicateCheckingFormView.down('dynamicform');
                    form.operate = 'modify';
                    form.entryids = entryids;
                    form.nodeids = nodeids;
                    form.entryid = entryids[0];
                    window.entryids = entryids;
                    var initFormFieldState = this.initFormField(form, 'show', record[0].get('nodeid'));
                    var codesetting = this.getCodesetting(record[0].get('nodeid'));
			        var nodename = this.getNodename(record[0].get('nodeid'));
			        var info = false;
			        if(codesetting){//如果设置了档号字段
			        	info = true;
			        }else{
                        if (nodename == '未归管理' || nodename == '文件管理' || nodename == '资料管理') {
			        		info = true;
			        	}
			        }
			        if(info){
			        	if (typeof(initFormFieldState) != 'undefined') {
			        		this.initFormData('modify',form, entryid);
                    		this.activeForm(form);
			        	}
			        }else{
			        	XD.msg('请检查档号模板信息是否正确');
			        }
                }
            },
            'duplicateCheckingEntryView [itemId=back]': {
                click: function (btn) {
                    this.activeGrid(btn,true);
                }
            },
            'duplicateCheckingEntryView':{
                afterrender:this.addKeyAction
            },
            'duplicateCheckingEntryView [itemId=save]': {
                click: this.submitForm
            },
            'duplicateCheckingGridView': {
                eleview: this.activeEleForm
            },
            'duplicateCheckingEntryView [itemId=preBtn]':{
                click:this.preHandler
            },
            'duplicateCheckingEntryView [itemId=nextBtn]':{
                click:this.nextHandler
            },
            'duplicateCheckingGridView [itemId=delete]': {//删除
                click: this.delHandler
            },
            'duplicateCheckingGridView button[itemId=back]': {
                click: this.doAdvancedSearchBack
            },
            'duplicateCheckingFormView button[itemId=topOperateBtn]': {click: this.doDuplicateChecking},
            'duplicateCheckingFormView button[itemId=bottomOperateBtn]': {click: this.doDuplicateChecking},
            'duplicateCheckingFormView button[itemId=topClearBtn]': {click: this.doAdvancedSearchClear},
            'duplicateCheckingFormView button[itemId=bottomClearBtn]': {click: this.doAdvancedSearchClear},
            'duplicateCheckingFormView button[itemId=topCloseBtn]': {click: this.doAdvancedSearchClose},
            'duplicateCheckingFormView button[itemId=bottomCloseBtn]': {click: this.doAdvancedSearchClose}

        });
    },
    
    getCodesetting: function (nodeid){
    	var isExist = false;//档号构成字段的集合
        Ext.Ajax.request({//获得档号构成字段的集合
            url:'/codesetting/getCodeSettingFields',
            async:false,
            params:{
                nodeid:nodeid
            },
            success:function(response){
                var res = Ext.decode(response.responseText).success;
                if (res) {
                	isExist = true;
                }
            }
        });
        return isExist;
    },

    //获取数据查重应用视图
    findView: function (btn) {
        return btn.findParentByType('duplicateChecking');
    },
    //获取检索表单界面视图
    findSearchformView: function (btn) {
        return this.findView(btn).down('[itemId=formview]');
    },
    //获取查看动态表单界面视图
    findFormView: function (btn) {
        return this.findView(btn).down('duplicateCheckingEntryView');
    },

    //获取列表界面视图
    findGridView: function (btn) {
        return this.findView(btn).down('duplicateCheckingGridView');
    },
    //切换到列表界面视图
    activeGrid: function (btn,flag) {
        var view = this.findView(btn);
        view.setActiveItem(this.findGridView(btn));
        this.findFormView(btn).saveBtn = undefined;//防止切换到列表后Ctrl+S组合键仍然生效
        window.button = undefined;
        if (document.getElementById('mediaFrame')) {
            document.getElementById('mediaFrame').setAttribute('src', '');
        }
        if (document.getElementById('solidFrame')) {
            document.getElementById('solidFrame').setAttribute('src', '');
        }
        // if (document.getElementById('longFrame')) {
        //     document.getElementById('longFrame').setAttribute('src', '');
        // }
        if(flag){//根据参数确定是否需要刷新数据
            var grid = view.down('duplicateCheckingGridView');
            // grid.initGrid();
            grid.notResetInitGrid();
        }
    },
    //切换到检索表单界面视图
    activeSearchform: function (btn) {
        var view = this.findView(btn);
        var formview = this.findSearchformView(btn);
        view.setActiveItem(formview);
        return formview;
    },

    //切换到查看动态表单界面视图
    activeForm: function (btn) {
        var view = this.findView(btn);
        var formview = this.findFormView(btn);
        view.setActiveItem(formview);
        formview.items.get(0).enable();
        formview.setActiveTab(0);
        return formview;
    },

    changeBtnStatus:function(form, operate){
        var savebtn = form.down('[itemId=save]');
        var tbseparator = form.getDockedItems('toolbar')[0].query('tbseparator');
        if(operate == 'look'){//查看时隐藏保存按钮
            savebtn.setVisible(false);
            tbseparator[0].setVisible(false);
        }else{
            savebtn.setVisible(true);
            tbseparator[0].setVisible(true);
        }
    },

    doAdvancedSearchBack: function (btn) {//返回检索条件输入页面
        var grid = this.findView(btn).down('duplicateCheckingGridView');
        grid.getStore().proxy.extraParams = {};//清空参数内容
        /*检索条件重置*/
        var conditionCombo = grid.down('[itemId=condition]');
        var conditionStore = conditionCombo.getStore();
        if (conditionStore.getCount() > 0) {
            conditionCombo.select(conditionStore.getAt(0));
        }
        /*检索操作符重置*/
        var operatorCombo = grid.down('[itemId=operator]');
        var operatorStore = operatorCombo.getStore();
        if (operatorStore.getCount() > 0) {
            operatorCombo.select(operatorStore.getAt(0));
        }
        /*检索内容重置*/
        var searchfield = grid.down('[itemId=value]');
        searchfield.reset();
        this.activeSearchform(btn);
        window.WselectWin.show();
    },

    doAdvancedSearchClear: function (btn) {//清除检索条件页面所有控件的输入值
        this.findView(btn).down('duplicateCheckingFormView').getForm().reset();//表单重置
    },
    doAdvancedSearchClose: function () {
        parent.closeObj.close(parent.layer.getFrameIndex(window.name));
    },
    initAdvancedSearchFormField: function (form, nodeid) {
        if (form.nodeid != nodeid) {//切换节点后，form和tree的节点id不相等
            form.nodeid = nodeid;//用左侧树节点的id初始化form的nodeid参数
            form.removeAll();//移除form中的所有表单控件
            var formField = form.getFormField();//根据节点id查询表单字段
            if (formField.length == 0) {
                XD.msg('请检查模板设置信息是否正确');
                return;
            }
            form.templates = formField;
            form.initSearchConditionField(formField);//重新动态添加表单控件
        }
        return '加载表单控件成功';
    },
    doDuplicateChecking: function (btn) {
        var treeView = btn.findParentByType('duplicateChecking').down('[itemId=duplicateCheckingTreeId]');
        var selectWin = Ext.create('DuplicateChecking.view.DuplicateCheckingSelectView', {treeView: treeView});
        selectWin.items.get(0).store.proxy.extraParams = {nodeid: treeView.selection.get('fnid')};
        selectWin.items.get(0).getStore().load(function () {
            selectWin.items.get(0).setValue();
        });
        selectWin.show();
        window.WselectWin = selectWin;
    },
    //保存表单数据，返回列表界面视图
    submitForm: function (btn) {
        var eleids = btn.findParentByType('duplicateCheckingEntryView').down('electronic').getEleids();
        var form = btn.findParentByType('duplicateCheckingEntryView').down('dynamicform');
        //字段编号，用于特殊的自定义字段(范围型日期)
        var fieldCode = form.getRangeDateForCode();
        var nodename = this.getNodename(form.nodeid);
        var params = {
            nodeid: form.nodeid,
            eleid: eleids,
            type: form.up('duplicateCheckingEntryView').operateFlag,
            operate: nodename
        };
        if (fieldCode != null) {
            params[fieldCode] = form.getDaterangeValue();
        }
        var archivecodeSetState = form.setArchivecodeValueWithNode(nodename);
        if (!archivecodeSetState) {//若档号设置失败，则停止后续的表单提交
            return;
        }
        Ext.MessageBox.wait('正在保存请稍后...', '提示');
        form.submit({
            method: 'POST',
            url: '/management/entries',
            params: params,
            scope: this,
            success: function (form, action) {
                Ext.MessageBox.hide();
                XD.msg(action.result.msg);
                if(window.entryids.length==1){
                    this.activeGrid(btn,true);
                }else{
                //切换到下一个条目
                this.nextHandler(btn);
                }
            },
            failure: function (form, action) {
                Ext.MessageBox.hide();
                XD.msg(action.result.msg);
            }
        });
    },
    getNodename: function (nodeid) {
    	var nodename;
        Ext.Ajax.request({
            async:false,
            url: '/nodesetting/getFirstLevelNode/' + nodeid,
            success:function (response) {
                nodename = Ext.decode(response.responseText);
            }
        });
        return nodename;
    },
    getNodeid:function (parentid) {
        var nodeid;
        var params = {};
        if(typeof(parentid) != 'undefined' && parentid != ''){
            params.parentid = parentid;
        }
        Ext.Ajax.request({
            url:'/publicUtil/getNodeid',
            async:false,
            params:params,
            success:function(response){
                nodeid = Ext.decode(response.responseText).data;
            }
        });
        return nodeid;
    },
    activeEleForm:function(obj){
        var view = this.findView(obj.grid);
        var formview = this.findFormView(obj.grid);
        view.setActiveItem(formview);
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

    initFormField:function(form, operate, nodeid){
//        if(form.nodeid!=nodeid){
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

    getCurrentDuplicateCheckingform:function (btn) {
        return btn.up('duplicateCheckingEntryView');
    },

    //点击上一条
    preHandler:function(btn){
        var currentDuplicateCheckingform = this.getCurrentDuplicateCheckingform(btn);
        var form = currentDuplicateCheckingform.down('dynamicform');
        this.preNextHandler(form, 'pre');
    },

    //点击下一条
    nextHandler:function(btn){
        var currentDuplicateCheckingform = this.getCurrentDuplicateCheckingform(btn);
        var form = currentDuplicateCheckingform.down('dynamicform');
        this.preNextHandler(form, 'next');
    },

    //条目切换，上一条下一条
    preNextHandler:function(form,type){
        var dirty = !!form.getForm().getFields().findBy(function(f){
            return f.wasDirty;
        });
        if(form.operate == 'modify' && dirty){
            XD.confirm('数据已修改，确定保存吗？',function() {
                //保存数据
                var formview = this.form;
                var nodename = this.ref.getNodename(formview.nodeid);
                var params={
                    nodeid: formview.nodeid,
                    type: formview.findParentByType('duplicateCheckingEntryView').operateFlag,
                    eleid: formview.findParentByType('duplicateCheckingEntryView').down('electronic').getEleids(),
                    operate: nodename
                };
                var fieldCode = formview.getRangeDateForCode();//字段编号，用于特殊的自定义字段(范围型日期)
                if (fieldCode != null) {
                    params[fieldCode] = formview.getDaterangeValue();
                }
                var archivecodeSetState = formview.setArchivecodeValueWithNode(nodename);
                if (!archivecodeSetState) {//若档号设置失败，则停止后续的表单提交
                    return;
                }
                Ext.MessageBox.wait('正在保存请稍后...', '提示');
                var formValues = formview.getValues();
                for (var name in formValues) {//遍历表单中的所有值
                    if (name == 'kccount' || name == 'fscount') {
                        if (formValues[name] == '' || formValues[name] == null) {
                            formValues[name] = "0";
                        }
                    }
                }
                formview.submit({
                    method: 'POST',
                    url: '/management/entries',
                    params: params,
                    scope: this,
                    success: function (form, action) {
                        Ext.MessageBox.hide();
                        this.ref.refreshFormData(this.form, this.type);
                    },
                    failure: function (form, action) {
                        Ext.MessageBox.hide();
                        XD.msg(action.result.msg);
                    }
                });
            },{
                ref:this,
                form:form,
                type:type
            },function(){
                this.ref.refreshFormData(this.form, this.type)
            });
        }else{
            this.refreshFormData(form, type);
        }
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

    initFormData:function (operate, form, entryid) {
        var formview = form.up('duplicateCheckingEntryView');
        var nullvalue = new Ext.data.Model();
        var fields = form.getForm().getFields().items;
        for(var i=0;i<form.entryids.length;i++){
            if(form.entryids[i]==entryid){
                count=i+1;
                break;
            }
        }
        var total = form.entryids.length;
        var totaltext = form.down('[itemId=totalText]');
        totaltext.setText('当前共有  ' + total + '  条，');
        var nowtext = form.down('[itemId=nowText]');
        nowtext.setText('当前记录是第  ' + count + '  条');
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
        var etips = formview.down('[itemId=etips]');
        etips.show();
        Ext.Ajax.request({
            method:'GET',
            scope:this,
            url:'/management/entries/'+entryid,
            success:function(response){
                if(operate!='look'){
                    var settingState = ifSettingCorrect(form.nodeid,form.templates);
                    if(!settingState){
                        return;
                    }
                    Ext.each(fields,function (item) {
		                if(!item.freadOnly){
		                    item.setReadOnly(false);
		                }
		            });
		        }else{
		        	Ext.each(fields,function (item) {
		                item.setReadOnly(true);
		            });
		        }
                var entry = Ext.decode(response.responseText);
                form.loadRecord({getData:function(){return entry;}});
                //字段编号，用于特殊的自定义字段(范围型日期)
                var fieldCode = form.getRangeDateForCode();
                if(fieldCode!=null){
                    //动态解析数据库日期范围数据并加载至两个datefield中
                    form.initDaterangeContent(entry);
                }
                //初始化原文数据
                var eleview = formview.down('electronic');
                eleview.initData(entryid);
                var solidview = formview.down('solid');
                solidview.initData(entryid);
                // var longview = formview.down('long');
                // longview.initData(entryid);
//                form.formStateChange(operate);
                form.fileLabelStateChange(eleview,operate);
                form.fileLabelStateChange(solidview,operate);
                // form.fileLabelStateChange(longview,operate);
                this.changeBtnStatus(formview,operate);
            }
        });
    },

    //删除
    delHandler: function (btn) {
        var grid = btn.findParentByType('duplicateCheckingGridView');
        var record = grid.selModel.getSelection();
        if (record.length == 0) {
            XD.msg('请至少选择一条需要删除的数据');
            return;
        }

        XD.confirm('确定要删除这' + record.length + '条数据吗?', function () {
            var tmp = [];
            for (var i = 0; i < record.length; i++) {
                tmp.push(record[i].get('entryid'));
            }
            var entryids = tmp.join(',');
            var tempParams = grid.getStore().proxy.extraParams;
            tempParams['entryids'] = entryids;
            Ext.Ajax.request({
                method: 'post',
                url: '/management/delete' ,
                params:tempParams,
                success: function (response) {
                    var resp = Ext.decode(response.responseText);
                    if('无法删除'==resp.msg) {
                        var titles = resp.data;
                        var title;
                        for (var i = 0; i < titles.length; i++) {
                            if (i == 0) {
                                title = '['+titles[i]+']';
                            } else {
                                title = title + '，' + '['+titles[i]+']';
                            }
                        }
                        XD.msg('无法删除，这  ' + titles.length + '  条题名为  ' + title + '  还处于未归状态')
                    }else {
                        XD.msg(Ext.decode(response.responseText).msg);
                        grid.delReload(record.length);
                    }
                }
            })
        }, this);
    },

    //监听键盘按下事件
    addKeyAction:function (view) {
        var controller = this;
        view.saveBtn = view.down('[itemId=save]');
        view.continueSaveBtn = view.down('[itemId=continuesave]');
        document.onkeydown = function () {
            var oEvent = window.event;
            if (oEvent.ctrlKey && !oEvent.shiftKey && !oEvent.altKey && oEvent.keyCode == 83) { //这里只能用alt，shift，ctrl等去组合其他键event.altKey、event.ctrlKey、event.shiftKey 属性
                // XD.msg('Ctrl+S');
                Ext.defer(function () {
                    if(view.saveBtn && view.operateFlag){//此处若不增加operateFlag判断，点击树节点后初次渲染acquisitionform时，按下ctrl+s会调用此方法
                        controller.submitForm(view.saveBtn);//保存
                    }
                },1);
                event.returnValue = false;//阻止event的默认行为
                // return false;//阻止event的默认行为
            }
        }
    }
});

function ifCodesettingCorrect(nodeid) {
    var codesetting = [];
    Ext.Ajax.request({
        url: '/codesetting/getCodeSettingFields',
        async:false,
        params:{
            nodeid:nodeid
        },
        success: function (response) {
            if(Ext.decode(response.responseText).success==true){
                codesetting = Ext.decode(response.responseText).data;
            }
        }
    });
    if(codesetting.length==0){
        return;
    }
    return '档号设置信息正确';
}

function ifSettingCorrect(nodeid,templates) {
    var hasArchivecode = false;//表单字段是否包含档号（archivecode）
    Ext.each(templates,function (item) {
        if(item.fieldcode=='archivecode'){
            hasArchivecode = true;
        }
    });
    if(hasArchivecode){//若表单字段包含档号，则判断档号设置是否正确
        var codesettingState = this.ifCodesettingCorrect(nodeid);
        if(!codesettingState){
            XD.msg('请检查档号设置信息是否正确');
            return;
        }
    }
    return '档号设置正确';
}