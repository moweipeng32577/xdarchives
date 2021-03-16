/**
 * Created by Administrator on 2019/6/27.
 */


Ext.define('ClassifySearchDirectory.controller.ClassifySearchDirectoryController',{
    extend : 'Ext.app.Controller',
    views :  [
        'ClassifySearchDirectoryView',
        'ClassifySearchFormDirectoryView',
        'ClassifySearchDirectoryGridView',
        'ClassifySearchExportDirectoryView',
        'ClassifySearchPromptDirectoryView',
        'ClassifySearchDirectoryTreeView',
        'ClassifySearchDirectoryFormView',
        'ReportGridView'
    ],
    stores:  [
        'ClassifySearchDirectoryTreeStore',
        'ClassifyBookmarksGridStore',
        'ReportGridStore'
    ],
    models:  [
        'ClassifySearchDirectoryTreeModel',
        'ClassifySearchDirectoryGridModel',
        'ReportGridModel'
    ],
    init : function() {
        var ifShowRightPanel = false;
        var classifySearchResultGridView ,
            printWin;
        var count = 0;
        var treeNode;
        this.control({
            'classifySearchDirectoryTreeView': {
                select: function (treemodel, record) {
                    var classifySearchView = treemodel.view.findParentByType('classifySearchDirectoryView');
                    var classifySearchPromptView = classifySearchView.down('classifySearchPromptDirectoryView');
                    var nodeType = record.data.nodeType;
                    //树节点为分类，更改右边页面为“请选择机构节点”
                    if(nodeType ==2){
                        classifySearchPromptView.removeAll();
                        classifySearchPromptView.add({
                            xtype: 'classifySearchPromptDirectoryView'
                        });
                        ifShowRightPanel = true;
                        return;
                    }
                    if(!ifShowRightPanel || !classifySearchView.down('advancedSearchDynamicForm')){
                        classifySearchPromptView.removeAll();
                        classifySearchPromptView.add({
                            xtype: 'classifySearchFormDirectoryView'
                        });
                        ifShowRightPanel = true;
                    }
                    var classifySearchResultgrid = classifySearchView.down('classifySearchDirectoryGridView');
                    var buttons = classifySearchResultgrid.down("toolbar");
                    var tbseparator = classifySearchResultgrid.down("toolbar").query('tbseparator');
                    var nodeids = record.get('fnid');
                    var treetype = record.get('treetype');//档案节点还是声像节点

                    classifySearchResultgrid.nodeids = nodeids;
                    classifySearchResultgrid.nodeid = nodeids;
                    if(treetype=='sx'){
                        classifySearchResultgrid.initGrid({nodeid:classifySearchResultgrid.nodeid,xtType:'声像系统'});
                    }else{
                        classifySearchResultgrid.initGrid({nodeid:classifySearchResultgrid.nodeid});
                    }

                    var advancedSearchDynamicForm = classifySearchView.down('advancedSearchDynamicForm');
                    this.initAdvancedSearchFormField(advancedSearchDynamicForm,nodeids,treetype);
                    var classifySearchFormDirectoryView = classifySearchView.down('classifySearchFormDirectoryView');
                    var datasoure = classifySearchFormDirectoryView.down('[itemId=datasoureId]');
                    if(treetype=='sx'){
                        classifySearchFormDirectoryView.down('[itemId=directoryId]').inputValue = 'soundimage';
                        classifySearchFormDirectoryView.down('[itemId=directoryId]').setValue(true);
                        datasoure.hide();
                    }else{
                        classifySearchFormDirectoryView.down('[itemId=directoryId]').inputValue = 'directory';
                        classifySearchFormDirectoryView.down('[itemId=directoryId]').setValue(true);
                        datasoure.show();
                    }
                }
            },
            'classifySearchFormDirectoryView':{
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
            'classifySearchFormDirectoryView button[itemId=topSearchBtn]':{click:this.doAdvancedSearch},
            'classifySearchFormDirectoryView button[itemId=bottomSearchBtn]':{click:this.doAdvancedSearch},
            'classifySearchFormDirectoryView button[itemId=topClearBtn]':{click:this.doAdvancedSearchClear},
            'classifySearchFormDirectoryView button[itemId=bottomClearBtn]':{click:this.doAdvancedSearchClear},
            'classifySearchFormDirectoryView button[itemId=topCloseBtn]':{click:this.doAdvancedSearchClose},
            'classifySearchFormDirectoryView button[itemId=bottomCloseBtn]':{click:this.doAdvancedSearchClose},
            'classifySearchDirectoryGridView button[itemId=classifySearchBackId]':{click:this.doAdvancedSearchBack},
            'classifySearchDirectoryGridView button[itemId=classifySearchShowId]':{
                click:function(btn){
                    var classifySearchResultGridView = btn.findParentByType('classifySearchDirectoryGridView');
                    var record = classifySearchResultGridView.selModel.getSelection();
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
                    var datasoure = classifySearchResultGridView.getStore().getProxy().extraParams.datasoure;  //数据源
                    form.datasoure = datasoure;
                    form.operate = 'look';
                    form.entryids = entryids;
                    form.nodeids = nodeids;
                    form.entryid = entryids[0];
                    this.initFormField(form, 'hide', record[0].get('nodeid'));
                    this.initFormData('look',form, entryid);
                }
            },

            'classifySearchDirectoryGridView ':{
                eleview: this.activeEleForm
            },
            'classifySearchDirectoryGridView': {
                rowdblclick: function (view,record) {
                    var entryid = record.get('entryid');
                    var form = this.findFormView(view).down('dynamicform');
                    this.initFormField(form, 'hide', record.get('nodeid'));
                    this.initFormData('look',form, entryid);
                }
            },
            'classifySearchDirectoryGridView [itemId=setBookmarks]':{//收藏（或取消收藏）
                click:function(view){
                    var sGrid = view.findParentByType('classifySearchDirectoryGridView');
                    var gridModel = sGrid.getSelectionModel();
                    var record = gridModel.getSelection();
                    if(record.length==0){
                        XD.msg('请选择需要操作的数据!');
                        return;
                    }
                    setBookmarks(sGrid);
                }
            },
            'classifySearchDirectoryGridView [itemId=viewBookmarks]':{//查看收藏（或返回）
                click:function(btn){
                    var sGrid = btn.findParentByType('classifySearchDirectoryGridView');
                    if(sGrid.bookmarkStatus==false){
                        sGrid.setTitle('当前位置：个人收藏');
                        sGrid.down('[itemId=setBookmarks]').setText('取消收藏');
                        sGrid.down('[itemId=setBookmarks]').setIconCls('fa fa-star-o');
                        sGrid.down('[itemId=viewBookmarks]').setText('返回');
                        sGrid.down('[itemId=viewBookmarks]').setIconCls('fa fa-undo');
                        var pagesize = sGrid.getStore().pageSize;
                        sGrid.getStore().removeAll();
                        var bookmarksStore=Ext.create('ClassifySearchDirectory.store.ClassifyBookmarksGridStore');//查找到用户收藏的条目
                        bookmarksStore.setPageSize(pagesize);
                        sGrid.reconfigure(bookmarksStore);
                        sGrid.notResetInitGrid();
                        sGrid.bookmarkStatus=true;
                    }else{
                        sGrid.setTitle('当前位置：分类检索');
                        sGrid.down('[itemId=setBookmarks]').setText('收藏');
                        sGrid.down('[itemId=setBookmarks]').setIconCls('fa fa-star');
                        sGrid.down('[itemId=viewBookmarks]').setText('查看收藏');
                        sGrid.down('[itemId=viewBookmarks]').setIconCls('fa fa-heart');
                        sGrid.getStore().removeAll();
                        sGrid.bookmarkStatus=false;
                        sGrid.getStore().proxy.url = '/classifySearchDirectory/findByClassifySearch';
                        sGrid.notResetInitGrid();
                    }
                }
            },

            'classifySearchDirectoryView [itemId=preBtn]':{
                click:this.preHandler
            },
            'classifySearchDirectoryView [itemId=nextBtn]':{
                click:this.nextHandler
            },
            'classifySearchDirectoryView button[itemId=back]':{//查看条目表单　返回
                click:function(btn){
                    this.activeGrid(btn,false);
                }
            },
            'classifySearchDirectoryGridView button[itemId=classifySearchExportId]':{
                click: function (view) {
                    var names = [];
                    var keys = [];
                    classifySearchResultGridView = view.findParentByType('classifySearchDirectoryGridView');
                    var record = classifySearchResultGridView.selModel.getSelection();
                    var form = this.findFormView(view).down('dynamicform');
                    form.nodeid = record[0].get('nodeid');
                    var datasoure = classifySearchResultGridView.getStore().getProxy().extraParams.datasoure;  //数据源
                    var formField;
                    if(datasoure=='soundimage'){  //声像系统
                        formField = form.getSxFormField();//根据节点id查询表单字段
                    }else{
                        formField = form.getFormField();//根据节点id查询表单字段
                    }
                    for(var i = 0; i<formField.length; i++){
                        names.push(formField[i].fieldname);
                        keys.push(formField[i].fieldcode);
                    }
                    if (record.length == 0) {
                        XD.msg('请选择需要导出的数据');
                        return;
                    }else{
                        Ext.create("ClassifySearchDirectory.view.ClassifySearchExportDirectoryView",{
                            names:names,
                            keys:keys,
                            datasoure:datasoure
                        }).show();
                    }
                }
            },
            'classifySearchExportDirectoryView button[itemId=classifySearchExportBtnId]':{
                click: function (view) {
                    var classifySearchExportWin = view.findParentByType('classifySearchExportDirectoryView');
                    var names = classifySearchExportWin.names;
                    var keys = classifySearchExportWin.keys;
                    var exportFileName = classifySearchExportWin.down('[itemId=classifySearchExportFileNameId]').getValue();
                    var select = classifySearchResultGridView.getSelectionModel();
                    if(''==exportFileName){
                        XD.msg('文件名称不能为空');
                        return;
                    }
                    var pattern = new RegExp("[/:*?\"<>|]");
                    if(pattern.test(exportFileName) || exportFileName.indexOf('\\') > -1) {
                        XD.msg("文件名称不能包含下列任何字符：\\/:*?\"<>|");
                        return;
                    }
                    var newExportFileName = encodeURIComponent(exportFileName);
                    var exportDetails = select.getSelection();
                    var array = [];
                    for (i = 0; i < exportDetails.length; i++) {
                        array[i] = exportDetails[i].get('entryid');
                    }
                    var datasoure = classifySearchExportWin.datasoure;
                    var downloadForm = document.createElement('form');
                    document.body.appendChild(downloadForm);
                    inputTextElement = document.createElement('input');
                    inputTextElementto = document.createElement('input');
                    inputTextElementn = document.createElement('input');
                    inputTextElementk = document.createElement('input');
                    inputTextElement.name ='entryids';
                    inputTextElement.value = array;
                    inputTextElementto.name = 'fileName';
                    inputTextElementto.value = exportFileName;
                    inputTextElementn.name = 'names';
                    inputTextElementn.value = names;
                    inputTextElementk.name = 'keys';
                    inputTextElementk.value = keys;
                    downloadForm.appendChild(inputTextElement);
                    downloadForm.appendChild(inputTextElementto);
                    downloadForm.appendChild(inputTextElementn);
                    downloadForm.appendChild(inputTextElementk);
                    downloadForm.action='/classifySearch/exportData?type='+datasoure;
                    downloadForm.method = "post";
                    downloadForm.submit();
                    view.up('classifySearchExportDirectoryView').close();
                }
            },
            'classifySearchExportDirectoryView button[itemId=classifySearchExportCloseBtnId]':{
                click: function (view) {
                    view.up('classifySearchExportDirectoryView').close();
                }
            },
            'classifySearchDirectoryGridView button[itemId=print]':{//打印
                click:function (btn) {
                    printWin = this.chooseReport(btn,printWin);
                }
            },
            'ReportGridView button[itemId=showAllReport]':{//显示所有报表
                click:function (btn) {
                    var reportGrid = btn.findParentByType('ReportGridView');
                    if (reportGrid.down('[itemId=showAllReport]').text == '显示所有报表'){
                        reportGrid.down('[itemId=showAllReport]').setText('显示当前报表');
                        reportGrid.initGrid({nodeid:reportGrid.nodeid+',私有报表',flag:'all'});
                    }else if(reportGrid.down('[itemId=showAllReport]').text == '显示当前报表'){
                        reportGrid.down('[itemId=showAllReport]').setText('显示所有报表');
                        reportGrid.initGrid({nodeid:reportGrid.nodeid+',私有报表'});
                    }
                }
            },
            'ReportGridView button[itemId=back]':{//返回
                click:function (btn) {
                    printWin.close();
                }
            },
            'ReportGridView button[itemId=print]':{//打印
                click:function (btn) {
                    var reportGrid = btn.findParentByType('ReportGridView');
                    var records = reportGrid.getSelectionModel().getSelection();
                    if (records.length == 0){
                        XD.msg('请选择需要打印的报表');
                        return;
                    }
                    var record = records[0];
                    var filename = record.get('filename');
                    if(!filename){
                        XD.msg('无报表样式文件，请在报表管理中上传');
                        return;
                    }
                    if (reportServer == 'UReport'){
                        Ext.Ajax.request({
                            method:'GET',
                            url:'/report/ifFileExist/' + record.get('reportid'),
                            scope:this,
                            async:false,
                            success:function (response) {
                                var responseText = Ext.decode(response.responseText);
                                if (responseText.success == true){
                                    var params = {};
                                    params['entryid'] = reportGrid.entryids.join(",");
                                    XD.UReportPrint(null,filename, params);
                                }else {
                                    XD.msg('打印失败！' + responseText.msg);
                                    return;
                                }
                            }
                        });
                    }
                    else if (reportServer == 'FReport'){
                        Ext.Ajax.request({
                            method:'GET',
                            url:'/report/ifFileExist/' + record.get('reportid'),
                            scope:this,
                            async:false,
                            success:function (response) {
                                var responseText = Ext.decode(response.responseText);
                                if (responseText.success == true){
                                    var win = null;
                                    XD.FRprint(win,filename,reportGrid.entryids.length > 0 ? "'entryid':'" + reportGrid.entryids.join(",") + "','nodeid':'" + reportGrid.nodeid + "'" : '','数据采集');
                                }else {
                                    XD.msg('打印失败！' + responseText.msg);
                                    return;
                                }
                            }
                        });
                    }
                }
            }
        });
    },


    //打开报表显示列表
    chooseReport:function (btn) {
       var grid = btn.up('classifySearchDirectoryGridView');
       var ids = [];
       Ext.each(grid.getSelectionModel().getSelection(),function () {
           ids.push(this.get('entryid'));
       });
       if(ids.length == 0){
           XD.msg('请至少选择一条需要打印的数据');
           return;
       }
       var reportGridWin = Ext.create('Ext.window.Window',{
           width:'100%',
           height:'100%',
           header:false,
           draggable:false,
           resizable:false,
           model:true,
           closeToolText:'关闭',
           layout:'fit',
           items:[{
               xtype:'ReportGridView',
               entryids:ids,
               nodeid:grid.nodeid
           }]
       });
       var reportGrid = reportGridWin.down('ReportGridView');
       reportGrid.initGrid({nodeid:reportGrid.nodeid + ',私有报表'});
       reportGrid.down('[itemId=basicgridCloseBtn]').setVisible(false);
       reportGridWin.show();
       return reportGridWin;
    },
    //获取分类检索应用视图
    findView: function (btn) {
        return btn.findParentByType('classifySearchDirectoryView');
    },
    //获取检索表单界面视图
    findSearchformView: function (btn) {
        return this.findView(btn).down('[itemId=formview]');
    },
    //获取查看动态表单界面视图
    findFormView: function (btn) {
        return this.findView(btn).down('classifySearchDirectoryFormView');
    },

    //获取列表界面视图
    findGridView: function (btn) {
        return this.findView(btn).down('classifySearchDirectoryGridView');
    },
    //切换到列表界面视图
    activeGrid: function (btn,flag) {
        var view = this.findView(btn);
        var gridview = this.findGridView(btn);
        view.setActiveItem(gridview);
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
            gridview.notResetInitGrid();
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
        /*formview.setActiveTab(0);*/
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
        var form = this.findView(btn).down('classifySearchFormDirectoryView');
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
        var fieldValue = [];
        for(var name in formValues){//遍历表单中的所有值
            formParams[name] = formValues[name];
            if(typeof(formValues[name]) != "undefined" && formValues[name] != '' && formValues[name] != 'and' &&
                formValues[name] != 'like' && formValues[name] != 'equal' && formValues[name] != 'or'){
                fieldColumn.push(name);
                fieldValue.push(formValues[name]);
            }
        }

        var datasoure = form.down('[itemId=datasoureId]').getChecked()[0].inputValue;//数据源
        var grid = this.findView(btn).down('classifySearchDirectoryGridView');

        // formParams.nodeid = grid.nodeids[0];
        // formParams.nodeids = grid.nodeids.join(',');
        formParams.nodeid = grid.nodeid;
        formParams.nodeids = grid.nodeid;
        //点击非叶子节点时，是否查询出其包含的所有叶子节点数据
        formParams.ifSearchLeafNode = true;
        //点击非叶子节点时，是否查询出当前非叶子节点及其包含的所有非叶子节点数据
        formParams.ifContainSelfNode = false;
        formParams.datasoure = datasoure;
        Ext.Array.each(grid.getColumns(), function(item){
            var column = item;
            if(item.xtype == 'gridcolumn'){
                item.renderer = function(value){
                    return value;
                }
            }
            if(column.dataIndex=="tdn"){
                column.renderer = function(value) {
                    return value['nodename'];
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
        /*切换至列表界面*/
        this.activeGrid(btn);
        /*加载页面*/
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
        // Ext.Ajax.request({
        //     method: 'post',
        //     url:'/classifySearch/setLastSearchInfo',
        //     timeout:XD.timeout,
        //     scope: this,
        //     async: true,
        //     params: {
        //         nodeid: grid.nodeids[0],
        //         fieldColumn: fieldColumn,
        //         fieldValue: fieldValue,
        //         type: '分类检索'
        //     },
        //     success:function(res){
        //     },
        //     failure:function(){
        //         Ext.MessageBox.hide();
        //         XD.msg('操作失败！');
        //     }
        // });
    },
    doAdvancedSearchBack:function(btn){//返回检索条件输入页面
        var grid = this.findView(btn).down('classifySearchDirectoryGridView');
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
        form.nodeid = nodeid;
        form.removeAll();//移除form中的所有表单控件
        var field = {
            xtype: 'hidden',
            name: 'entryid'
        };
        form.add(field);
        var formField;
        if(form.datasoure=='soundimage'){  //声像系统
            formField = form.getSxFormField();//根据节点id查询表单字段
        }else{
            formField = form.getFormField();//根据节点id查询表单字段
        }
        if(formField.length==0){
            XD.msg('请检查模板设置信息是否正确');
            return;
        }
        form.templates = formField;
        form.initField(formField,operate);//重新动态添加表单控件
//        }
        return '加载表单控件成功';
    },

    getCurrentClassifySearchform:function (btn) {
        return btn.up('classifySearchDirectoryFormView');
    },

    //点击上一条
    preHandler:function(btn){
        var currentClassifySearchform = this.getCurrentClassifySearchform(btn);
        var form = currentClassifySearchform.down('dynamicform');
        this.refreshFormData(form, 'pre');
    },

    //点击下一条
    nextHandler:function(btn){
        var currentClassifySearchform = this.getCurrentClassifySearchform(btn);
        var form = currentClassifySearchform.down('dynamicform');
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
        var url;
        if(form.datasoure=="directory"){  //数据源为目录中心
            url = "/manageDirectory/entries/";
        }else if(form.datasoure=="management"){  //数据源为档案系统
            url = "/management/entries/";
        }else{  //声像系统
            url = "/simpleSearchDirectory/getSxEntry/";
        }
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
        /*var etips = formview.down('[itemId=etips]');
        etips.show();*/
        this.activeForm(form);
        Ext.Ajax.request({
            method:'GET',
            scope:this,
            url:url+entryid,
            success:function(response){
                if(operate!='look'){
                    var settingState = ifSettingCorrect(form.nodeid,form.templates);
                    if(!settingState){
                        return;
                    }
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
               /* var eleview = formview.down('electronic');
                eleview.initData(entryid);
                var solidview = formview.down('solid');
                solidview.initData(entryid);*/
                // var longview = formview.down('long');
                // longview.initData(entryid);
//                form.formStateChange(operate);
                /*form.fileLabelStateChange(eleview,operate);
                form.fileLabelStateChange(solidview,operate);*/
                // form.fileLabelStateChange(longview,operate);
            }
        });
    },

    doAdvancedSearchClear:function(btn){//清除检索条件页面所有控件的输入值
        var form = this.findView(btn).down('classifySearchFormDirectoryView').getForm();
                form.reset();//表单重置
    },

    doAdvancedSearchClose:function(btn){
        parent.closeObj.close(parent.layer.getFrameIndex(window.name));
    },

    initAdvancedSearchFormField:function(form, nodeid,treetype){
        if(form){
            if (form.nodeid != nodeid) {//切换节点后，form和tree的节点id不相等
                form.nodeid = nodeid;//用左侧树节点的id初始化form的nodeid参数
                form.removeAll();//移除form中的所有表单控件
                var formField;
                if(treetype=='sx'){  //声像系统
                    formField = form.getSxFormField();//根据节点id查询表单字段
                }else{
                    formField = form.getFormField();//根据节点id查询表单字段
                }
                formField.type = '分类检索';
                if(formField.length==0){
                    XD.msg('请检查模板设置信息是否正确');
                    return;
                }
                form.templates = formField;
                form.initSearchConditionField(formField,true);//重新动态添加表单控件
            }
        }
        return '加载表单控件成功';
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

//设置收藏状态
function setBookmarks(grid) {
    var record = grid.getSelectionModel().selected;
    if (record.length < 1) {
        XD.msg('请选择一条数据进行操作');
        return;
    }
    var array = [];
    for (var i = 0; i < record.length; i++) {
        array[i] = record.items[i].get('id');
    }
    var operate;
    if(!grid.bookmarkStatus){//bookmarkStatus值为false，代表收藏操作，为true则代表取消收藏操作
        operate = '收藏';
    }else{
        operate = '取消收藏';
    }
    var datasoure = grid.getStore().getProxy().extraParams.datasoure;  //数据源
    Ext.Msg.wait('正在进行'+operate+'操作，请耐心等待……','正在操作');
    Ext.Ajax.request({
        url:'/bookmarks/setBookmarks',
        method: 'POST',
        timeout:XD.timeout,
        params:{
            entryids:array,
            bookmarkStatus:grid.bookmarkStatus,
            type:datasoure
        },
        success:function(response) {
            var resp = Ext.decode(response.responseText);
            if(resp.success==false){
                Ext.Msg.wait(operate+'操作中断','正在操作').hide();
                Ext.MessageBox.alert("提示信息", resp.msg, function(){
                });
            }else{
                Ext.Msg.wait(operate+'操作完成','正在操作').hide();
                XD.msg(resp.msg);
                if(operate == '取消收藏'){
                    grid.getStore().reload();
                }
            }
            Ext.MessageBox.hide();
        },
        failure:function(){
            Ext.MessageBox.hide();
            XD.msg('操作失败');
        }
    });
}
