/**
 * Created by RonJiang on 2018/01/24
 */
// var backupConfirmMsg = '修改数据后，将不能进行还原，建议进行备份，备份需要较长时间，是否备份?';
Ext.define('AcceptDirectory.controller.BatchModifyController', {
    extend: 'Ext.app.Controller',

    models: [
		'BatchModifyTreeModel','BatchModifyTemplatefieldModel',
    	'FieldModifyPreviewGridModel',
    	'BatchModifyTemplateEnumfieldModel'
    ],
    stores: [
    	'BatchModifyTreeStore','BatchModifyTemplatefieldStore',
        'FieldModifyPreviewGridStore',
        'BatchModifyTemplateEnumfieldStore'
    ],
    init : function() {
        var ifShowRightPanel = false;
        this.control({
            'batchModifyTreeView':{
                select: function (treemodel, record) {
                    var batchModifyView = treemodel.view.findParentByType('batchModify');
                    var batchModifyPromptView = batchModifyView.down('batchModifyPromptView');
                    if(!ifShowRightPanel){
                        batchModifyPromptView.removeAll();
                        batchModifyPromptView.add({
                            xtype: 'batchModifyFormView'
                        });
                        ifShowRightPanel = true;
                    }
                    var batchModifyResultgrid = batchModifyView.down('acceptDirectoryGridView');
                    batchModifyResultgrid.nodeid = record.get('fnid');
                    batchModifyResultgrid.initGrid({nodeid:batchModifyResultgrid.nodeid});
                    var advancedSearchDynamicForm = batchModifyView.down('advancedSearchDynamicForm');
                    this.initAdvancedSearchFormField(advancedSearchDynamicForm,record.get('fnid'));
                    var fullname=record.get('text');
                    while(record.parentNode.get('text')!='分类检索'){
                        fullname=record.parentNode.get('text')+'_'+fullname;
                        record=record.parentNode;
                    }
                    batchModifyResultgrid.nodefullname = fullname;
                }
            },
            'batchModifyFormView':{
                render:function(field){
                    var topLogicCombo = field.getComponent('topLogicCombo');
                    var bottomLogicCombo = field.getComponent('bottomLogicCombo');
                    topLogicCombo.on('change',function (view) {//点击顶部逻辑下拉选，则同步底部逻辑下拉选的值
                        bottomLogicCombo.setValue(view.lastValue);
                    });
                    bottomLogicCombo.on('change',function (view) {//点击底部逻辑下拉选，则同步顶部逻辑下拉选的值
                        topLogicCombo.setValue(view.lastValue);
                    });
                }
            },
            'batchModifyFormView button[itemId=topSearchBtn]':{click:this.doAdvancedSearch},
            'batchModifyFormView button[itemId=bottomSearchBtn]':{click:this.doAdvancedSearch},
            'batchModifyFormView button[itemId=topClearBtn]':{click:this.doAdvancedSearchClear},
            'batchModifyFormView button[itemId=bottomClearBtn]':{click:this.doAdvancedSearchClear},
            'batchModifyFormView button[itemId=topCloseBtn]':{click:this.doAdvancedSearchClose},
            'batchModifyFormView button[itemId=bottomCloseBtn]':{click:this.doAdvancedSearchClose},
            'acceptDirectoryGridView [itemId=batchModifyBackId]':{click:this.doAdvancedSearchBack},
            'acceptDirectoryGridView [itemId=batchModifyShowId]':{
                click:function(btn){
                    var acceptDirectoryGridView = btn.findParentByType('acceptDirectoryGridView');
                    var record = acceptDirectoryGridView.selModel.getSelection();
                    if(record.length==0){
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
                    var form = this.findFormView(btn).down('dynamicform');
                    form.operate = 'look';
                    form.entryids = entryids;
                    form.nodeids = nodeids;
                    form.entryid = entryids[0];
                    this.initFormField(form, 'hide', record[0].get('nodeid'));
                    this.initFormData('look',form, entryid);
                }
            },
            'acceptDirectoryGridView ':{
                eleview: this.activeEleForm
            },
            'acceptDirectoryGridView': {
                rowdblclick: function (view,record) {
                    var entryid = record.get('entryid');
                    var form = this.findFormView(view).down('dynamicform');
                    this.initFormField(form, 'hide', record.get('nodeid'));
                    this.initFormData('look',form, entryid);
                }
            },
            'acceptDirectoryGridView [itemId=batchModify]':{//结果列表界面　批量修改
                click:this.doBatchModify
            },
            'batchModifyModifyFormView [itemId=templatefieldCombo]':{
                render:this.loadModifyTemplatefieldCombo,
                select:this.loadModifyTemplateEnumfieldCombo
            },
            'batchModifyModifyFormView button[itemId=addToModify]':{//批量修改窗口 加入修改
                click:this.addToModify
            },
            'batchModifyModifyFormView button[itemId=deleteModify]':{//批量修改窗口　删除修改
                click:this.deleteModify
            },
            'batchModifyModifyFormView button[itemId=clear]':{//批量修改窗口　清除
                click:function (btn) {
                    var formview = btn.up('batchModifyModifyFormView');
                    var fieldModifyPreviewGrid = formview.down('grid');
                    fieldModifyPreviewGrid.getStore().removeAll();
                    this.loadModifyTemplatefieldCombo(formview.getForm().findField('fieldname'));
                }
            },
            'batchModifyModifyFormView button[itemId=getPreview]':{//批量修改窗口　获取预览
                click:function (btn) {
                    var formview = btn.up('batchModifyModifyFormView');
                    var formWin = formview.up('window');
                    var fieldModifyPreviewGrid = formview.down('grid');
                    var fieldModifyPreviewGridStore = fieldModifyPreviewGrid.getStore();
                    var fieldModifyPreviewGridData = fieldModifyPreviewGridStore.data.items;
                    if(fieldModifyPreviewGridData.length==0){
                        XD.msg('请选择修改字段');
                        return;
                    }
                    var fieldModifyData = "";
                    var operateFieldcodes = [];
                    if(fieldModifyPreviewGridData.length > 0){//如果有已存在的修改字段
                        for (var i = 0; i < fieldModifyPreviewGridData.length; i++) {
                            var item = fieldModifyPreviewGridData[i];
                            if (i < fieldModifyPreviewGridData.length - 1) {
                                fieldModifyData += item.get('fieldcode')+'∪'+item.get('fieldname')+'∪'+item.get('fieldvalue')+"∩";
                            } else {
                                fieldModifyData += item.get('fieldcode')+'∪'+item.get('fieldname')+'∪'+item.get('fieldvalue');
                            }
                            operateFieldcodes.push(item.get('fieldcode'));
                        }
                    }
                    var batchModifyResultPreviewWin = Ext.create('Ext.window.Window',{
                        width:'65%',
                        height:'70%',
                        title:'批量操作预览',
                        draggable : true,//可拖动
                        resizable : false,//禁止缩放
                        modal:true,
                        closeAction: 'hide',
                        closeToolText:'关闭',
                        layout:'fit',
                        items:[{
                            xtype:'batchModifyResultPreviewGrid',
                            entryids:formview.entryids,
                            datanodeid:formview.resultgrid.nodeid,
                            fieldmodifydata:fieldModifyData,
                            nodefullname:formview.resultgrid.nodefullname,
                            operateFlag:formWin.title,
                            resultGrid:formview.resultgrid,
                            formview:formview
                        }]
                    });
                    delTempByUniqueType('modi');
                    var resultPreviewGrid = batchModifyResultPreviewWin.down('batchModifyResultPreviewGrid');
                    var params = {
                        entryidArr:resultPreviewGrid.entryids.split(','),
                        nodeid:resultPreviewGrid.datanodeid,
                        fieldModifyData:resultPreviewGrid.fieldmodifydata,
                        multiValue:operateFieldcodes,
                        excludeValues:operateFieldcodes,
                        flag:resultPreviewGrid.operateFlag,
                        type:"目录接收"
                    };
                    var type = true;
                    resultPreviewGrid.initGrid(params,type);
                    batchModifyResultPreviewWin.show();
                    window.batchModifyResultPreviewWins = batchModifyResultPreviewWin;
                    Ext.on('resize',function(a,b){
                        window.batchModifyResultPreviewWins.setPosition(0, 0);
                        window.batchModifyResultPreviewWins.fitContainer();
                    });
                    // if (typeof(resultPreviewGrid.initGrid(params)) == 'undefined') {
                    // 	XD.msg('请检查档号组成字段是否为空');
                    // }
                }
            },
            'batchModifyModifyFormView button[itemId=exit]':{//批量修改窗口　退出
                click:function (btn) {
                    var formview = btn.up('batchModifyModifyFormView');
                    // var resultgridview = formview.resultgrid;
                    var fieldModifyPreviewGrid = formview.down('grid');
                    fieldModifyPreviewGrid.getStore().removeAll();
                    btn.up('window').close();
                    // resultgridview.getStore().reload();//initGrid({nodeid:resultgridview.nodeid});
                }
            },
            'batchModifyResultPreviewGrid button[itemId=batchUpdateBtn]':{//批量操作预览－执行批量更新
                click:this.doBatchUpdate
            },
            'batchModifyResultPreviewGrid button[itemId=backBtn]':{//批量操作预览－返回
                click:function (btn) {
                    var batchType=btn.up('batchModifyResultPreviewGrid').operateFlag;
                    if(batchType=='批量修改'){
                        batchType='modi';
                    }else if(batchType=='批量增加'){
                        batchType='add';
                    }else{
                        batchType='repl';
                    }
                    delTempByUniqueType(batchType);
                    btn.up('window').hide();
                }
            },
            'acceptDirectoryGridView [itemId=batchRepace]':{//结果列表界面　批量替换
                click:this.doBatchReplace
            },
            'batchModifyReplaceFormView [itemId=templatefieldCombo]':{
                render:this.loadReplaceTemplatefieldCombo
            },
            'batchModifyReplaceFormView button[itemId=getPreview]':{//批量替换窗口　获取预览
                click:function (btn) {
                    var formview = btn.up('batchModifyReplaceFormView');
                    var formWin = formview.up('window');
                    var fieldcodeandnameCombo = formview.getForm().findField('fieldname');
                    var fieldcodeAndName = fieldcodeandnameCombo.getValue();
                    var searchcontentField = formview.getForm().findField('searchcontent');
                    var searchcontent = searchcontentField.getValue();
                    var replacecontentField = formview.getForm().findField('replacecontent');
                    var replacecontent = replacecontentField.getValue();
                    var containspace = formview.down('[itemId=ifContainSpaces]').getValue();
                    var ifContainspace = containspace?true:false;
                    var allowempty = formview.down('[itemId=ifAllowEmpty]').getValue();

                    if(!fieldcodeAndName){XD.msg('请选择修改字段');return;}
                    if(!searchcontent){XD.msg('查找内容不允许为空');return;}
                    if(!replacecontent && !allowempty){XD.msg('替换值不允许为空');return;}
                    if(searchcontent==replacecontent && !ifContainspace){XD.msg('替换前后数据无变化');return;}
                    
                    var fieldReplaceData = [fieldcodeAndName+'∪'+searchcontent+'∪'+replacecontent];
                    var operateFieldcodes =[fieldcodeAndName.split('_')[0]];
                    var batchModifyResultPreviewWin = Ext.create('Ext.window.Window',{
                        width:'65%',
                        height:'70%',
                        title:'批量操作预览',
                        draggable : true,//可拖动
                        resizable : false,//禁止缩放
                        modal:true,
                        closeAction: 'hide',
                        closeToolText:'关闭',
                        layout:'fit',
                        items:[{
                            xtype:'batchModifyResultPreviewGrid',
                            entryids:formview.entryids,
                            datanodeid:formview.resultgrid.nodeid,
                            fieldreplacedata:fieldReplaceData,
                            ifcontainspace:ifContainspace,
                            nodefullname:formview.resultgrid.nodefullname,
                            operateFlag:formWin.title,
                            resultGrid:formview.resultgrid
                        }]
                    });
                    delTempByUniqueType('repl');
                    var resultPreviewGrid = batchModifyResultPreviewWin.down('batchModifyResultPreviewGrid');
                    var params = {
                        entryidArr:resultPreviewGrid.entryids.split(','),
                        nodeid:resultPreviewGrid.datanodeid,
                        fieldReplaceData:resultPreviewGrid.fieldreplacedata,
                        ifContainSpace:resultPreviewGrid.ifcontainspace,
                        multiValue:operateFieldcodes,
                        excludeValues:operateFieldcodes,
                        flag:resultPreviewGrid.operateFlag,
                        type:"目录接收"
                    };
                    var type = true;
                    resultPreviewGrid.initGrid(params,type);
                    resultPreviewGrid.getStore().on('load',function(store){
                        if(!resultPreviewGrid.operateCount){//仅第一次load该store完成时给列表的operateCount赋值
                            resultPreviewGrid.operateCount = resultPreviewGrid.getStore().totalCount;
                        }
                    });
                    batchModifyResultPreviewWin.show();
                    window.batchModifyResultPreviewWins = batchModifyResultPreviewWin;
                    Ext.on('resize',function(a,b){
                        window.batchModifyResultPreviewWins.setPosition(0, 0);
                        window.batchModifyResultPreviewWins.fitContainer();
                    });
                }
            },
            'batchModifyReplaceFormView button[itemId=exit]':{//批量替换窗口　退出
                click:function (btn) {
                    // var formview = btn.up('batchModifyReplaceFormView');
                    // var resultgridview = formview.resultgrid;
                    btn.up('window').close();
                    // resultgridview.getStore().reload();//.initGrid({nodeid:resultgridview.nodeid});
                }
            },
            'acceptDirectoryGridView [itemId=batchAdd]':{//结果列表界面　批量增加
                click:this.doBatchAdd
            },
            'batchModifyAddFormView [itemId=templatefieldCombo]':{
                render:this.loadAddTemplatefieldCombo
            },
            'batchModifyAddFormView button[itemId=getPreview]':{//批量增加窗口　获取预览
                click:function (btn) {
                    var formview = btn.up('batchModifyAddFormView');
                    var formWin = formview.up('window');
                    var fieldcodeandnameCombo = formview.getForm().findField('fieldname');
                    var fieldcodeAndName = fieldcodeandnameCombo.getValue();
                    var addcontentField = formview.getForm().findField('addcontent');
                    var addcontent = addcontentField.getValue();
                    var inserttype = formview.getValues()['insertPlace'];
                    var insertplaceindexField = formview.getForm().findField('insertPlaceIndex');
                    var insertplaceindex = insertplaceindexField.getValue();

                    if(!fieldcodeAndName){XD.msg('请选择修改字段');return;}
                    if(!addcontent){XD.msg('添加内容不允许为空');return;}
                    if(!inserttype){XD.msg('请检查位置设置信息');return;}
                    if(inserttype=='anywhere' && !insertplaceindex){XD.msg('请输入插入字符位置');return;}
                    if(isNaN(insertplaceindex)){XD.msg('插入字符位置输入项格式不正确');return;}
                    if(parseInt(insertplaceindex)<1){XD.msg('插入字符位置输入项最小值为1');return;}
                    if(parseInt(insertplaceindex)>8000){XD.msg('插入字符位置输入项最大值为8000');return;}

                    var inserttypeAndPlaceindex,fieldModifyData;
                    if(inserttype=='anywhere'){
                        inserttypeAndPlaceindex = inserttype+'_'+insertplaceindex;
                        fieldModifyData = [fieldcodeAndName+'∪'+addcontent+'∪'+inserttypeAndPlaceindex];
                    }else{
                        fieldModifyData = [fieldcodeAndName+'∪'+addcontent+'∪'+inserttype];
                    }
                    var operateFieldcodes =[fieldcodeAndName.split('_')[0]];
                    var batchModifyResultPreviewWin = Ext.create('Ext.window.Window',{
                        width:'65%',
                        height:'70%',
                        title:'批量操作预览',
                        draggable : true,//可拖动
                        resizable : false,//禁止缩放
                        modal:true,
                        closeAction: 'hide',
                        closeToolText:'关闭',
                        layout:'fit',
                        items:[{
                            xtype:'batchModifyResultPreviewGrid',
                            entryids:formview.entryids,
                            datanodeid:formview.resultgrid.nodeid,
                            fieldmodifydata:fieldModifyData,
                            nodefullname:formview.resultgrid.nodefullname,
                            operateFlag:formWin.title,
                            resultGrid:formview.resultgrid
                        }]
                    });
                    delTempByUniqueType('add');
                    var resultPreviewGrid = batchModifyResultPreviewWin.down('batchModifyResultPreviewGrid');
                    var params = {
                        entryidArr:resultPreviewGrid.entryids.split(','),
                        nodeid:resultPreviewGrid.datanodeid,
                        fieldModifyData:resultPreviewGrid.fieldmodifydata,
                        multiValue:operateFieldcodes,
                        excludeValues:operateFieldcodes,
                        flag:resultPreviewGrid.operateFlag,
                        type:"目录接收"
                    };
                    var type = true;
                    resultPreviewGrid.initGrid(params,type);
                    batchModifyResultPreviewWin.show();
                    window.batchModifyResultPreviewWins = batchModifyResultPreviewWin;
                    Ext.on('resize',function(a,b){
                        window.batchModifyResultPreviewWins.setPosition(0, 0);
                        window.batchModifyResultPreviewWins.fitContainer();
                    });
                }
            },
            'batchModifyAddFormView button[itemId=exit]':{//批量增加窗口　退出
                click:function (btn) {
                    btn.up('window').close();
                }
            },
            'EntryFormView [itemId=preBtn]':{
                click:this.preHandler
            },
            'EntryFormView [itemId=nextBtn]':{
                click:this.nextHandler
            },
            'EntryFormView button[itemId=back]':{
                click:function(btn){
                    this.activeGrid(btn);
                }
            }
        });
    },

    //获取批量修改应用视图
    findView: function (btn) {
        return btn.findParentByType('batchModify');
    },
    //获取检索表单界面视图
    findSearchformView: function (btn) {
        return this.findView(btn).down('[itemId=formview]');
    },
    //获取查看动态表单界面视图
    findFormView: function (btn) {
        return this.findView(btn).down('EntryFormView');
    },

    //获取列表界面视图
    findGridView: function (btn) {
        return btn.up('acceptDirectoryGridView');
    },
    //切换到列表界面视图
    activeGrid: function (btn,flag) {
        var view = this.findView(btn);
        view.setActiveItem(this.findGridView(btn));
        if (document.getElementById('mediaFrame')) {
            document.getElementById('mediaFrame').setAttribute('src', '');
        }
        if (document.getElementById('solidFrame')) {
            document.getElementById('solidFrame').setAttribute('src', '');
        }
        // if (document.getElementById('longFrame')) {
        //     document.getElementById('longFrame').setAttribute('src', '');
        // }
        if (flag) {//根据参数确定是否需要刷新数据
            var grid = this.findGridView(btn);
            grid.notResetInitGrid();
        }
    },
    //切换到检索表单界面视图
    activeSearchform: function (btn) {
        var view = this.findView(btn);
        var Searchformview = this.findSearchformView(btn);
        view.setActiveItem(Searchformview);
        return Searchformview;
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

    doAdvancedSearch:function (btn){//分类检索页面查询方法
        /*查询参数处理*/
        var form = this.findView(btn).down('batchModifyFormView');
        var filedateStartField = form.getForm().findField('filedatestart');
        var filedateEndField = form.getForm().findField('filedateend');
        if(filedateStartField!=null && filedateEndField!=null){
            var filedateStartValue = filedateStartField.getValue();
            var filedateEndValue = filedateEndField.getValue();
            if(filedateStartValue>filedateEndValue){
                XD.msg('开始日期必须小于或等于结束日期');
                return;
            }
        }
        var formValues = form.getValues();//获取表单中的所有值(类型：js对象)
        var formParams = {};
        var fieldColumn = [];
        for(var name in formValues){//遍历表单中的所有值
            formParams[name] = formValues[name];
            if(typeof(formValues[name]) != "undefined" && formValues[name] != '' && formValues[name] != 'and' &&
                formValues[name] != 'like' && formValues[name] != 'equal' && formValues[name] != 'or'){
                fieldColumn.push(name);
            }
        }
        var grid = this.findView(btn).down('acceptDirectoryGridView');
        formParams.nodeid = grid.nodeid;
        //点击非叶子节点时，是否查询出其包含的所有叶子节点数据
        formParams.ifSearchLeafNode = false;
        //点击非叶子节点时，是否查询出当前非叶子节点及其包含的所有非叶子节点数据
        formParams.ifContainSelfNode = false;
        /*切换至列表界面*/
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
        this.activeGrid(btn);
        /*加载页面*/
        if(fieldColumn.length==0){
            grid.initGrid(formParams)
        }else{
            var store = grid.getStore();
            Ext.apply(store.getProxy(),{
                extraParams:formParams
            });
            store.loadPage(1);
        }
    },
    doAdvancedSearchBack:function(btn){//返回检索条件输入页面
        var grid = this.findView(btn).down('acceptDirectoryGridView');
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

    getCurrentBatchModifyform:function (btn) {
        return btn.up('EntryFormView');
    },

    //点击上一条
    preHandler:function(btn){
        var currentBatchModifyform = this.getCurrentBatchModifyform(btn);
        var form = currentBatchModifyform.down('dynamicform');
        this.refreshFormData(form, 'pre');
    },

    //点击下一条
    nextHandler:function(btn){
        var currentBatchModifyform = this.getCurrentBatchModifyform(btn);
        var form = currentBatchModifyform.down('dynamicform');
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

    initFormData:function (operate, form, entryid) {
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
        var etips = formview.down('[itemId=etips]');
        etips.show();
        this.activeForm(form);
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
            }
        });
    },

    doAdvancedSearchClear:function(btn){//清除检索条件页面所有控件的输入值
        this.findView(btn).down('batchModifyFormView').getForm().reset();//表单重置
    },
    doAdvancedSearchClose:function(){
        parent.closeObj.close(parent.layer.getFrameIndex(window.name));
    },
    initAdvancedSearchFormField:function(form, nodeid){
//        if (form.nodeid != nodeid) {//切换节点后，form和tree的节点id不相等
            form.nodeid = nodeid;//用左侧树节点的id初始化form的nodeid参数
            form.removeAll();//移除form中的所有表单控件
            var formField = form.getFormField();//根据节点id查询表单字段
            if(formField.length==0){
                XD.msg('请检查模板设置信息是否正确');
                return;
            }
            form.templates = formField;
            form.initSearchConditionField(formField);//重新动态添加表单控件
//        }
        return '加载表单控件成功';
    },
    doBatchModify:function (btn) {
        var resultGrid = this.findGridView(btn);
        var records = resultGrid.getSelectionModel().getSelection();
        var selectCount = records.length;
        if(selectCount==0){
            XD.msg('请选择数据');
            return;
        }
        var tmp = [];
        for(var i = 0; i < records.length; i++){
            tmp.push(records[i].get('entryid'));
        }
        var entryids = tmp.join(',');
        var batchModifyModifyWin = Ext.create('Ext.window.Window',{
            width:'100%',
            height:'100%',
            title:'批量修改',
            // draggable : true,//可拖动
            // resizable : false,//禁止缩放
            modal:true,
            closeToolText:'关闭',
            layout:'fit',
            items:[{
                xtype: 'batchModifyModifyFormView',
                entryids:entryids,
                resultgrid:resultGrid
            }]
        });
        var fieldModifyPreviewGrid = batchModifyModifyWin.down('grid');
        if(fieldModifyPreviewGrid.getStore().data.length>0){
            fieldModifyPreviewGrid.getStore().removeAll();
        }
        batchModifyModifyWin.show();
        window.batchModifyModifyWins = batchModifyModifyWin;
        Ext.on('resize',function(a,b){
            window.batchModifyModifyWins.setPosition(0, 0);
            window.batchModifyModifyWins.fitContainer();
        });
    },
    
    loadModifyTemplatefieldCombo:function (view) {//加载批量修改form的下拉框
        var combostore = view.getStore();
        var batchModifyModifyFormView = view.up('batchModifyModifyFormView');
        combostore.proxy.extraParams.datanodeidAndFieldcodes = batchModifyModifyFormView.resultgrid.nodeid;
        combostore.load();
    },
    
    loadModifyTemplateEnumfieldCombo:function (view) {//加载批量修改form的下拉框(处理枚举值)
    	var data = view.valueCollection.items[0].data;
    	var batchModifyModifyFormView = view.up('batchModifyModifyFormView');
        var updateFieldvalue = batchModifyModifyFormView.down('[itemId=updateFieldvalue]');
        var enumfieldCombo = batchModifyModifyFormView.down('[itemId=enumfieldCombo]');
        var codeInfo = batchModifyModifyFormView.getForm().findField('code');
        Ext.Ajax.request({
            url: '/template/getInactiveformfield',
            params: {
            	nodeid: data.nodeid,
            	field: data.fieldcode
            },
            method: 'POST',
            success: function (resp) {
            	var value = Ext.decode(resp.responseText);
                if (data.ftype == 'enum') {
                	codeInfo.setValue('');
                	var store = enumfieldCombo.getStore('BatchModifyTemplateEnumfieldStore');
		            store.proxy.extraParams = {configCode: data.fieldname};
		            store.load();
                	
		            updateFieldvalue.hide();
		            enumfieldCombo.show();
		            
		            if (value.msg == '隐藏') {
		            	codeInfo.editable = false;
                		enumfieldCombo.editable = false;
                	} else {
                		codeInfo.editable = true;
                		enumfieldCombo.editable = true;
                	}
		        } else {
		        	updateFieldvalue.show();
		            enumfieldCombo.hide();
		        }
            },
            failure: function (resp) {
                XD.msg('操作失败！');
            }
        });
    },
    
    loadReplaceTemplatefieldCombo:function (view) {//加载批量替换form的下拉框
        var combostore = view.getStore();
        var batchModifyReplaceFormView = view.up('batchModifyReplaceFormView');
        combostore.proxy.extraParams.datanodeidAndFieldcodes = batchModifyReplaceFormView.resultgrid.nodeid;
        combostore.load();
    },
    loadAddTemplatefieldCombo:function (view) {//加载批量增加form的下拉框
        var combostore = view.getStore();
        var batchModifyReplaceFormView = view.up('batchModifyAddFormView');
        combostore.proxy.extraParams.datanodeidAndFieldcodes = batchModifyReplaceFormView.resultgrid.nodeid;
        combostore.load();
    },
    //加入修改
    addToModify:function (btn) {
        /*第一步：判断字段及替换值是否填写正确*/
        var formview = btn.up('batchModifyModifyFormView');
        var combobox = formview.getForm().findField('fieldname');
        var fieldStr = combobox.getValue();
        if(!fieldStr){
            XD.msg('字段值不允许为空');
            return;
        }
        var value = formview.down('[itemId=enumfieldCombo]').lastMutatedValue;
        var updateFieldvalue = formview.getForm().findField('fieldvalue').getValue();
        var allowEmpty = formview.down('[itemId=ifAllowEmpty]').getValue();
        if(!allowEmpty && !updateFieldvalue){
            if(!updateFieldvalue && typeof(value) == 'undefined') {
                XD.msg('替换值不允许为空');
                return;
            }
        }
        /*第二步：将需要修改的字段数据追加至列表显示*/
        var fieldModifyPreviewGrid = formview.down('grid');
        var fieldModifyPreviewGridStore = fieldModifyPreviewGrid.getStore();
        var existedData = fieldModifyPreviewGridStore.data.items;
        var existedDataArr = [];
        if(existedData.length>0){
            Ext.each(existedData,function (item) {
                existedDataArr.push(item.get('fieldcode')+'∪'+item.get('fieldname')+'∪'+item.get('fieldvalue'));
            });
        }
        var params = {
            fieldcode:fieldStr.split('_')[0],
            fieldname:fieldStr.split('_')[1],
            fieldvalue:!updateFieldvalue?value:updateFieldvalue,
            existedDataArr:existedDataArr
        };
        fieldModifyPreviewGridStore.proxy.extraParams = params;
        fieldModifyPreviewGridStore.load({
        	callback: function () {
	            formview.getForm().findField('fieldname').setValue('');
	            formview.getForm().findField('fieldvalue').setValue('');
	            // 清除枚举值的下拉框
	            formview.getForm().findField('code').setValue('');
            }
        });
        /*第三步：刷新字段值下拉框store*/
        this.initModifyComboStore(btn,'add');
    },
    //删除修改
    deleteModify:function (btn) {
        /*第一步：判断是否选定需要删除项*/
        var formview = btn.up('batchModifyModifyFormView');
        var fieldModifyPreviewGrid = formview.down('grid');
        var records = fieldModifyPreviewGrid.getSelectionModel().getSelected().items;
        var selectCount = records.length;
        if(selectCount==0){
            XD.msg('请选择需要删除的记录');
            return;
        }
        /*第二步：删除已选择的字段数据，刷新列表*/
        var delIds = [];
        for(var i = 0; i < records.length; i++){
            delIds.push(records[i].get('fieldcode'));
        }
        fieldModifyPreviewGrid.delIds = delIds;
        var fieldModifyPreviewGridStore = fieldModifyPreviewGrid.getStore();
        var allData = fieldModifyPreviewGridStore.data.items;
        var allIds = [];
        for(var i = 0; i < allData.length; i++){
            allIds.push(allData[i].get('fieldcode'));
        }
        for(var i=0;i<delIds.length;i++){
            for(var j=0;j<allIds.length;j++){
                if(delIds[i]==allIds[j]){
                    allIds.splice(j,1);
                    j--;
                }
            }
        }
        var remainIds = allIds;
        var remainData = [];
        for(var i=0;i<allData.length;i++){
            for(var j=0;j<remainIds.length;j++){
                if(allData[i].get('fieldcode')==remainIds[j]){
                    remainData.push(allData[i]);
                }
            }
        }
        var remainDataArr = [];
        if(remainData.length==0){
            fieldModifyPreviewGridStore.removeAll();
        }else{
            Ext.each(remainData,function (item) {
                remainDataArr.push(item.get('fieldcode')+'∪'+item.get('fieldname')+'∪'+item.get('fieldvalue'));
            });
            Ext.apply(fieldModifyPreviewGridStore.proxy.extraParams, {remainDataArr:remainDataArr});
            fieldModifyPreviewGridStore.load();
        }
        /*第三步：刷新字段值下拉框store*/
        this.initModifyComboStore(btn,'delete');
    },
    //初始化字段值下拉框
    initModifyComboStore:function (btn,operate) {
        var formview = btn.up('batchModifyModifyFormView');
        var gridview = formview.down('grid');
        var combobox = formview.getForm().findField('fieldname');

        var gridRecords = gridview.getStore().data.items;
        var fieldcodes = [];
        for(var i = 0; i < gridRecords.length; i++){
            fieldcodes.push(gridRecords[i].get('fieldcode'));
        }
        if(operate=='add'){
            fieldcodes.push(combobox.getValue().split('_')[0]);
        }
        if(operate=='delete'){
            for(var i=fieldcodes.length-1;i>=0;i--){//倒着检测，不用考虑位置影响
                Ext.each(gridview.delIds,function (item) {
                    if(fieldcodes[i]==item){
                        fieldcodes.splice(i,1);
                    }
                });
            }
        }
        fieldcodes.join(',');

        var combostore = combobox.getStore();
        combostore.proxy.extraParams.datanodeidAndFieldcodes = formview.resultgrid.nodeid+'∪'+fieldcodes;
        combostore.reload();
        
        var enumfieldCombo = formview.down('[itemId=enumfieldCombo]');
        enumfieldCombo.getStore().removeAll();
    },
    doBatchReplace:function (btn) {
        var resultGrid = this.findGridView(btn);
        var records = resultGrid.getSelectionModel().getSelection();
        var selectCount = records.length;
        if(selectCount==0){
            XD.msg('请选择数据');
            return;
        }
        var tmp = [];
        for(var i = 0; i < records.length; i++){
            tmp.push(records[i].get('entryid'));
        }
        var entryids = tmp.join(',');
        var batchModifyReplaceWin = Ext.create('Ext.window.Window',{
            width:'100%',
            height:'100%',
            title:'批量替换',
            // draggable : true,//可拖动
            resizable : false,//禁止缩放
            modal:true,
            closeToolText:'关闭',
            layout:'fit',
            items:[{
                xtype: 'batchModifyReplaceFormView',
                entryids:entryids,
                resultgrid:resultGrid
            }]
        });
        batchModifyReplaceWin.show();
        window.batchModifyReplaceWins = batchModifyReplaceWin;
        Ext.on('resize',function(a,b){
            window.batchModifyReplaceWins.setPosition(0, 0);
            window.batchModifyReplaceWins.fitContainer();
        });
    },
    doBatchAdd:function (btn) {
        var resultGrid = this.findGridView(btn);
        var records = resultGrid.getSelectionModel().getSelection();
        var selectCount = records.length;
        if(selectCount==0){
            XD.msg('请选择数据');
            return;
        }
        var tmp = [];
        for(var i = 0; i < records.length; i++){
            tmp.push(records[i].get('entryid'));
        }
        var entryids = tmp.join(',');
        var batchModifyAddWin = Ext.create('Ext.window.Window',{
            width:'100%',
            height:'100%',
            title:'批量增加',
            // draggable : true,//可拖动
            // resizable : false,//禁止缩放
            modal:true,
            closeToolText:'关闭',
            layout:'fit',
            items:[{
                xtype: 'batchModifyAddFormView',
                entryids:entryids,
                resultgrid:resultGrid
            }]
        });
        batchModifyAddWin.show();
        window.batchModifyAddWins = batchModifyAddWin;
        Ext.on('resize',function(a,b){
            window.batchModifyAddWins.setPosition(0, 0);
            window.batchModifyAddWins.fitContainer();
        });
    },
    //执行批量更新
    doBatchUpdate:function (btn) {
        var previewGrid = btn.up('batchModifyResultPreviewGrid');
        if(previewGrid.operateFlag=='批量修改'){
            this.doModifyBatchUpdate(btn);
        }
        if(previewGrid.operateFlag=='批量替换'){
            this.doReplaceBatchUpdate(btn);
        }
        if(previewGrid.operateFlag=='批量增加'){
            this.doAddBatchUpdate(btn);
        }
    },
    doModifyBatchUpdate:function (btn) {
        var resultPreviewGrid = btn.findParentByType('batchModifyResultPreviewGrid');
        var modifydata = resultPreviewGrid.fieldmodifydata;
        var fieldData = modifydata.split('∩');//fieldcode∪fieldname∪fieldvalue
        var modifyDetail = '';
        for (var i = 0; i < fieldData.length; i++) {
        	var data = fieldData[i].split('∪');
			modifyDetail += '['+data[1]+']设置为“'+data[2]+'”，';
		}
		modifyDetail = modifyDetail.substring(0, modifyDetail.length-1);
        var operateCount = resultPreviewGrid.entryids.split(',').length;//此处操作记录条数为所有选定记录总条数
        var updateConfirmMsg = '本次操作将把['+resultPreviewGrid.nodefullname+']所选记录的'+modifyDetail+',记录数：共'+operateCount+'条, 是否继续?';
        XD.confirm(updateConfirmMsg,function (){
            updateData(btn,'modi');
        },this);
    },
    doReplaceBatchUpdate:function (btn) {
        var resultPreviewGrid = btn.findParentByType('batchModifyResultPreviewGrid');
        var fieldmodifydatas = resultPreviewGrid.fieldreplacedata[0].split('∪');//[fieldcode_fieldname,addcontent,inserttype(或inserttype_insertplaceindex)]
        var fieldname = fieldmodifydatas[0].split('_')[1];
        var replaceDetail = '“'+fieldmodifydatas[1]+'”字符串';
        var ifcontainspace = resultPreviewGrid.ifcontainspace;
        if(ifcontainspace){
            replaceDetail+='及其前后空格值';
        }
        var replacecontent = fieldmodifydatas[2];
        if (replacecontent != '') {
        	replaceDetail += '替换为“'+replacecontent+'”字符串';
        } else {
        	replaceDetail += '替换为空字符串';
        }
        var operateCount = resultPreviewGrid.operateCount;//列表load完成后的记录总条数
        var updateConfirmMsg = '本次操作将把['+resultPreviewGrid.nodefullname+']所选记录的['+fieldname+']里的'+replaceDetail+',记录数：共'+operateCount+'条, 是否继续?';
        if(operateCount == 0){
            XD.msg('未找到包含需替换内容的记录');
            return;
        }
        XD.confirm(updateConfirmMsg,function (){
            updateData(btn,'repl');
        },this);
    },
    doAddBatchUpdate:function (btn) {
        var resultPreviewGrid = btn.findParentByType('batchModifyResultPreviewGrid');
        var fieldmodifydatas = resultPreviewGrid.fieldmodifydata[0].split('∪');//[fieldcode_fieldname,searchcontent,replacement]
        var fieldname = fieldmodifydatas[0].split('_')[1];
        var addcontent = fieldmodifydatas[1];
        var insertPlaceInfo = fieldmodifydatas[2];
        var inserttype,placeindex;
        if(insertPlaceInfo.indexOf('_')!=-1){
            inserttype = insertPlaceInfo.split('_')[0];
            placeindex = insertPlaceInfo.split('_')[1];
        }else{
            inserttype = insertPlaceInfo;
        }
        var updateConfirmMsg;
        var operateCount = resultPreviewGrid.entryids.split(',').length;//此处操作记录条数为所有选定记录总条数
        if(inserttype=='anywhere' && placeindex){
            updateConfirmMsg = '本次操作将在['+resultPreviewGrid.nodefullname+']所选记录的['+fieldname+']的第'+placeindex+'位增加“'+addcontent+'”字符串，记录数：共'+operateCount+'条, 是否继续?';
        }
        if(inserttype=='front'){
            updateConfirmMsg = '本次操作将在['+resultPreviewGrid.nodefullname+']所选记录的['+fieldname+']前面增加“'+addcontent+'”字符串，记录数：共'+operateCount+'条, 是否继续?';
        }
        if(inserttype=='behind'){
            updateConfirmMsg = '本次操作将在['+resultPreviewGrid.nodefullname+']所选记录的['+fieldname+']后面增加“'+addcontent+'”字符串，记录数：共'+operateCount+'条, 是否继续?';
        }

        XD.confirm(updateConfirmMsg,function (){
            updateData(btn,'add');
        },this);
    }
});

function delTempByUniqueType(type) {//清除本机当前用户关联的的临时条目数据
    Ext.Ajax.request({
        method: 'POST',
        params: {batchType: type},
        url: '/batchModify/delTempByUniqueType',
        async:false,
        success: function (response) {
        }
    });
}

function delTempByUniquetag() {//清除本机当前用户关联的的临时条目数据
    Ext.Ajax.request({
        method: 'DELETE',
        url: '/batchModify/delTempByUniquetag',
        asych:false,
        success: function (response) {
        }
    })
}

function updateData(btn,batchtype) {
    var batchModifyResultPreviewWin = btn.up('window');
    var resultPreviewGrid = batchModifyResultPreviewWin.down('batchModifyResultPreviewGrid');
    var params = {
        entryidArr:resultPreviewGrid.entryids.split(','),
        nodeid:resultPreviewGrid.datanodeid,
        fieldModifyData:resultPreviewGrid.fieldmodifydata,
        fieldReplaceData:resultPreviewGrid.fieldreplacedata,
        flag:resultPreviewGrid.operateFlag,
        type:"目录接收",
        batchtype:batchtype
    };
    if(resultPreviewGrid.ifcontainspace!=undefined){
        params.ifContainSpace = resultPreviewGrid.ifcontainspace;
    }
    Ext.Msg.wait('正在执行'+resultPreviewGrid.operateFlag+'操作，请耐心等待……','正在操作');

    var columnArray = [];
    var columns = resultPreviewGrid.columnManager.getColumns();
    for (var j = 0; j < columns.length; j++) {
        if (columns[j].xtype == 'gridcolumn') {
            var subtext = columns[j].dataIndex + '-' + columns[j].text;
            columnArray.push(subtext);
        }
    }
    params['columnArray'] = columnArray;
    var downloadForm = document.createElement('form');
    document.body.appendChild(downloadForm);
    var inputTextElement;
    for (var prop in params){
        inputTextElement = document.createElement('input');
        inputTextElement.name = prop;
        inputTextElement.value = params[prop];
        downloadForm.appendChild(inputTextElement);
    }
    downloadForm.action = '/batchModify/export';
    downloadForm.method = "post";
    downloadForm.submit();
    Ext.Ajax.setTimeout(36000000);
    Ext.Ajax.request({
        url: '/batchModify/updateEntryindex',
        params: params,
        sync : true,
        timeout:XD.timeout,
        success: function (resp) {
            var respText = Ext.decode(resp.responseText);
            batchModifyResultPreviewWin.hide();
            Ext.Msg.wait(resultPreviewGrid.operateFlag+'操作成功','正在操作').hide();
            XD.msg(respText.msg);
            resultPreviewGrid.resultGrid.getStore().reload();
        },
        failure:function (form, action) {
            Ext.Msg.wait(resultPreviewGrid.operateFlag+'操作失败','正在操作').hide();
            XD.msg(action.result.msg);
        }
    })
}