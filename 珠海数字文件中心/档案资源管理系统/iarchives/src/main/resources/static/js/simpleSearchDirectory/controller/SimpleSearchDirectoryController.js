/**
 * Created by Administrator on 2019/6/26.
 */


Ext.define('SimpleSearchDirectory.controller.SimpleSearchDirectoryController',{
    extend : 'Ext.app.Controller',
    views :  [
        'SimpleSearchDirectoryView','SimpleSearchDirectoryGridView','SimpleSearchDirectoryExportView','SimpleSearchDirectoryFormView','ReportGridView'//简单检索
    ],
    stores:  [
        'SimpleSearchDirectoryGridStore','BookmarksGridStore','ReportGridStore'
    ],
    models:  [
        'SimpleSearchDirectoryGridModel','ReportGridModel'
    ],
    init : function() {
        var simpleSearchGridView,printWin;
        var count = 0;
        var gridflag = false;
        this.control({
            'simpleSearchDirectoryView [itemId=simpleSearchSearchfieldId]':{
                /*change:function (searchfield) {
                    var instantSearch = searchfield.findParentByType('simpleSearchDirectoryView').down('[itemId=instantSearch]').getValue();
                    //如果有勾选即时搜索，则立即检索显示查询
                    if(instantSearch){
                        this.searchInfo(searchfield);
                        gridflag = true;
                    }
                },*/
                search:function(searchfield){
                    this.searchInfo(searchfield);
                    gridflag = true;
                }
            },
            'simpleSearchDirectoryGridView [itemId=simpleSearchShowId]':{
                click:function(btn){
                    var simpleSearchGridView = btn.findParentByType('simpleSearchDirectoryGridView');
                    var record = simpleSearchGridView.selModel.getSelection();
                    if(record.length==0){
                        XD.msg('请至少选择一条需要查看的数据');
                        return;
                    }
                    var entryid = record[0].get('entryid');
                    var form = this.findFormView(btn).down('dynamicform');
                    var entryids = [];
                    var nodeids = [];
                    for(var i=0;i<record.length;i++){
                        entryids.push(record[i].get('entryid'));
                        nodeids.push(record[i].get('nodeid'));
                    }
                    form.operate = 'look';
                    form.entryids = entryids;
                    form.nodeids = nodeids;
                    form.entryid = entryids[0];
                    var datasoure = simpleSearchGridView.getStore().getProxy().extraParams.datasoure;  //数据源
                    form.datasoure = datasoure;
                    this.initFormField(form, 'hide', record[0].get('nodeid'));
                    this.initFormData('look',form, entryid);
                }
            },
            'simpleSearchDirectoryGridView': {
                rowdblclick: function (view, record) {
                    var entryid = record.get('entryid');
                    var form = this.findFormView(view).down('dynamicform');
                    var datasoure = view.getStore().getProxy().extraParams.datasoure;  //数据源
                    form.datasoure = datasoure;
                    this.initFormField(form, 'hide', record.get('nodeid'));
                    this.initFormData('look',form, entryid);
                }
            },
            'simpleSearchDirectoryGridView ':{
                eleview: this.activeEleForm
            },
            'simpleSearchDirectoryGridView [itemId=simpleSearchExportId]':{
                click: function (view) {
                    var names = [];
                    var keys = [];
                    simpleSearchGridView = view.findParentByType('simpleSearchDirectoryGridView');
                    var select = simpleSearchGridView.getSelectionModel();
                    if (!select.hasSelection()) {
                        XD.msg('请选择需要导出的数据');
                        return;
                    }
                    var form = this.findFormView(view).down('dynamicform');
                    form.nodeid = select.getSelection()[0].get('nodeid');
                    var datasoure = simpleSearchGridView.getStore().getProxy().extraParams.datasoure;  //数据源
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
                    if (!select.hasSelection()) {
                        XD.msg('请选择需要导出的数据');
                        return;
                    }else{
                        Ext.create("SimpleSearchDirectory.view.SimpleSearchDirectoryExportView",{
                            names:names,
                            keys:keys,
                            datasoure:datasoure
                        }).show();
                    }
                }
            },
            'simpleSearchDirectoryExportView [itemId=simpleSearchExportBtnId]':{
                click: function (view) {
                    var simpleSearchExportWin = view.findParentByType('simpleSearchDirectoryExportView');
                    var names = simpleSearchExportWin.names;
                    var keys = simpleSearchExportWin.keys;
                    var exportFileName = simpleSearchExportWin.down('[itemId=simpleSearchExportFileNameId]').getValue();
                    var select = simpleSearchGridView.getSelectionModel();
                    //--正则验证
                    //var re;
                    //re = /[a-zA-Z0-9]{6,16}/;
                    //re =  /^[A-Za-z0-9\u4e00-\u9fa5]+$/gi;//判断汉字字母数字
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
                    for (var i = 0; i < exportDetails.length; i++) {
                        array[i] = exportDetails[i].get('entryid');
                    }
                    var datasoure = simpleSearchExportWin.datasoure;
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
                    downloadForm.action='/simpleSearch/exportData?type='+datasoure;
                    downloadForm.method = "post";
                    downloadForm.submit();
                    //window.location.href="/simpleSearch/exportData?fileName="+encodeURIComponent(exportFileName)+"&entryids="+array;
                    // appWindow.focus();
                    view.up('simpleSearchDirectoryExportView').close();
                }
            },
            'simpleSearchDirectoryExportView [itemId=simpleSearchExportCloseBtnId]':{
                click: function (view) {
                    view.up('simpleSearchDirectoryExportView').close();
                }
            },
            'ReportGridView [itemId=showAllReport]':{//显示所有报表
                click:function (btn) {
                    var reportGrid = btn.findParentByType('ReportGridView');
                    if(reportGrid.down('[itemId=showAllReport]').text=='显示所有报表'){
                        reportGrid.down('[itemId=showAllReport]').setText('显示当前报表');
                        reportGrid.initGrid({nodeid:reportGrid.nodeid+ ',私有报表',flag:'all'});
                    }else if(reportGrid.down('[itemId=showAllReport]').text=='显示当前报表'){
                        reportGrid.down('[itemId=showAllReport]').setText('显示所有报表');
                        reportGrid.initGrid({nodeid:reportGrid.nodeid + ',私有报表'});
                    }
                }
            },
            'ReportGridView [itemId=back]':{//打印返回
                click:function (btn) {
                    printWin.close();
                }
            },
            'ReportGridView [itemId=print]':{//打印报表
                click:function (btn) {
                    var reportGrid = btn.findParentByType('ReportGridView');
                    var records = reportGrid.getSelectionModel().getSelection();
                    if(records.length==0){
                        XD.msg('请选择需要打印的报表');
                        return;
                    }
                    var record = records[0];
                    var filename = record.get('filename');
                    if(!filename){
                        XD.msg('无报表样式文件，请在报表管理中上传');
                        return;
                    }
                    if(reportServer == 'UReport'){
                        Ext.Ajax.request({
                            method: 'GET',
                            url: '/report/ifFileExist/' + record.get('reportid'),
                            scope: this,
                            async: false,
                            success: function (response) {
                                var responseText = Ext.decode(response.responseText);
                                if (responseText.success == true) {
                                    var params = {};
                                    params['entryid'] = reportGrid.entryids.join(",");
                                    XD.UReportPrint(null, filename, params);
                                } else {
                                    XD.msg('打印失败！' + responseText.msg);
                                    return;
                                }
                            }
                        });
                    }
                    else if(reportServer == 'FReport') {
                        Ext.Ajax.request({
                            method: 'GET',
                            url: '/report/ifFileExist/' + record.get('reportid'),
                            scope: this,
                            async: false,
                            success: function (response) {
                                var responseText = Ext.decode(response.responseText);
                                if (responseText.success == true) {
                                    //在ajax里面异步打开新标签页，会被拦截(谷歌会拦截，360不会)
                                    var win = null;
                                    XD.FRprint(win, filename, reportGrid.entryids.length > 0 ? "'entryid':'" + reportGrid.entryids.join(",") + "','nodeid':'" + reportGrid.nodeid + "'" : '', '数据采集');
                                } else {
                                    XD.msg('打印失败！' + responseText.msg);
                                    return;
                                }
                            }
                        });
                    }
                }
            },
            'simpleSearchDirectoryGridView [itemId=print]':{
                click: function (btn) {
                    printWin = this.chooseReport(btn,printWin);
                }
            },
            'simpleSearchDirectoryGridView [itemId=setBookmarks]':{//收藏（或取消收藏）
                click:function(view){
                    var sGrid = view.findParentByType('simpleSearchDirectoryGridView');
                    var gridModel = sGrid.getSelectionModel();
                    var record = gridModel.getSelection();
                    if(record.length==0){
                        XD.msg('请选择需要操作的数据!');
                        return;
                    }
                    setBookmarks(sGrid);
                }
            },
            'simpleSearchDirectoryGridView [itemId=viewBookmarks]':{//查看收藏（或返回）
                click:function(btn){
                    var sGrid = btn.findParentByType('simpleSearchDirectoryGridView');
                    if(sGrid.bookmarkStatus==false){
                        sGrid.setTitle('当前位置：个人收藏');
                        sGrid.down('[itemId=setBookmarks]').setText('取消收藏');
                        sGrid.down('[itemId=setBookmarks]').setIconCls('fa fa-star-o');
                        sGrid.down('[itemId=viewBookmarks]').setText('返回');
                        sGrid.down('[itemId=viewBookmarks]').setIconCls('fa fa-undo');
                        // this.resetSearchCondition(sGrid);
                        sGrid.gridPagesSize =sGrid.getStore().pageSize;
                        sGrid.getStore().removeAll();
                        var bookmarksStore=Ext.create('SimpleSearchDirectory.store.BookmarksGridStore');//查找到用户收藏的条目
                        sGrid.reconfigure(bookmarksStore);
                        var datasoure = sGrid.findParentByType('simpleSearchDirectoryView').down('[itemId=datasoure]').getChecked()[0].inputValue;//数据源
                        sGrid.initGrid({datasoure:datasoure});
                        sGrid.bookmarkStatus=true;
                    }else{
                        sGrid.setTitle('当前位置：简单检索');
                        sGrid.down('[itemId=setBookmarks]').setText('收藏');
                        sGrid.down('[itemId=setBookmarks]').setIconCls('fa fa-star');
                        sGrid.down('[itemId=viewBookmarks]').setText('查看收藏');
                        sGrid.down('[itemId=viewBookmarks]').setIconCls('fa fa-heart');
                        // this.resetSearchCondition(sGrid);
                        sGrid.bookmarkStatus=false;
                        var searchfield = this.findView(btn).down('searchfield');
                        var content = searchfield.getValue(); //内容
                        if(typeof(content) != 'undefined' &&gridflag==true&&content == ''){
                            this.searchInfo(searchfield);
                        }
                        if (typeof(content) != 'undefined' && content != null && content != ''&&gridflag==true) {
                            this.searchInfo(searchfield);
                        } else {
                            sGrid.getStore().removeAll();//将表格信息全部移除
                        }
                    }
                    var conditionField = this.findView(btn).down('[itemId=simpleSearchSearchComboId]');
                    conditionField.getStore().reload();
                }
            },

            'simpleSearchDirectoryFormView [itemId=preBtn]':{
                click:this.preHandler
            },
            'simpleSearchDirectoryFormView [itemId=nextBtn]':{
                click:this.nextHandler
            },
            'simpleSearchDirectoryView [itemId=back]': {//返回
                click: function(btn){
                    this.activeGrid(btn, true);
                }
            }
        });
    },

    //打开报表显示列表
    chooseReport:function(btn){
        var grid = btn.up('simpleSearchDirectoryGridView');
        var ids = [];
        Ext.each(grid.getSelectionModel().getSelection(), function () {
            ids.push(this.get('entryid').trim());
        });

        if(ids.length==0){
            XD.msg('请至少选择一条需要打印的数据');
            return;
        }
        if(grid.dataParams.datasoure=='directory'){
            var params = {};
            var entryids = ids.join(',');
            if(reportServer == 'UReport') {
                params['entryid'] = entryids;
                XD.UReportPrint(null, '目录管理表', params);
            }
            else if(reportServer == 'FReport') {
                XD.FRprint(null, '目录管理表', entryids.length > 0 ? "'entryids':'" + entryids + "'" : '');
            }
        }else{
        var reportGridWin = Ext.create('Ext.window.Window',{
            width:'100%',
            height:'100%',
            header:false,
            draggable : false,//禁止拖动
            resizable : false,//禁止缩放
            modal:true,
            closeToolText:'关闭',
            layout:'fit',
            items:[{
                xtype: 'ReportGridView',
                entryids:ids,
                nodeid:grid.nodeid
            }]
        });
        var reportGrid = reportGridWin.down('ReportGridView');
        reportGrid.initGrid({nodeid:reportGrid.nodeid + ',私有报表'});
        reportGrid.down('[itemId=basicgridCloseBtn]').setVisible(false);//隐藏关闭按钮
        reportGridWin.show();
        return reportGridWin;
        }
    },

    //获取简单检索应用视图
    findView: function (btn) {
        return btn.findParentByType('simpleSearchDirectoryView');
    },

    //获取表单界面视图
    findFormView: function (btn) {
        return this.findView(btn).down('simpleSearchDirectoryFormView');
    },

    //获取列表界面视图
    findGridView: function (btn) {
        return this.findView(btn).getComponent('gridview');
    },
    //切换到列表界面视图
    activeGrid: function (btn, flag) {
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
            var grid = gridview.down('simpleSearchDirectoryGridView');
            grid.notResetInitGrid();
        }
    },

    //切换到表单界面视图
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
        if (buttonflag === '1') {
            formview.items.get(1).disable();formview.items.get(2).isJy=true;
        }
        buttonflag === '1'?formview.setActiveTab(2):formview.setActiveTab(1);
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

    //检索表单信息
    searchInfo:function (searchfield) {
        //获取检索框的值
        var simpleSearchSearchView = searchfield.findParentByType('panel');
        var condition = simpleSearchSearchView.down('[itemId=simpleSearchSearchComboId]').getValue(); //字段
        var operator = 'like';//操作符
        var content = searchfield.getValue(); //内容
        var isCollection;
        var sGrid = searchfield.findParentByType('simpleSearchDirectoryView').down('simpleSearchDirectoryGridView');
        if (sGrid.title == '当前位置：个人收藏') {//如果是收藏界面
            isCollection = '收藏';
        }
        var datasoure = simpleSearchSearchView.down('[itemId=datasoure]').getChecked()[0].inputValue;//数据源
        //检索数据
        //如果有勾选在结果中检索，则添加检索条件，如果没有勾选，则重置检索条件
        var grid = simpleSearchSearchView.findParentByType('panel').down('simpleSearchDirectoryGridView');
        var gridstore = grid.getStore();
        //加载列表数据
        var searchcondition = condition;
        var searchoperator = operator;
        var searchcontent = content;
        //如果选择在结果中查询,将两种条件用','号隔开,参数一起传递到后台
        var inresult = simpleSearchSearchView.down('[itemId=inresult]').getValue();
        if(inresult){
            var params = gridstore.getProxy().extraParams;
            if(typeof(params.condition) != 'undefined'){
                searchcondition = [params.condition,condition].join(XD.splitChar);
                searchoperator = [params.operator,operator].join(XD.splitChar);
                searchcontent = [params.content,content].join(XD.splitChar);
            }
        }
        grid.dataParams={
            isCollection: isCollection,
            condition: searchcondition,
            operator: searchoperator,
            content: searchcontent,
            datasoure:datasoure
        };

        //检索数据前,修改column的renderer，将检索的内容进行标红
        Ext.Array.each(grid.getColumns(), function(){
            var column = this;
            if(!inresult && column.xtype == 'gridcolumn'){
                column.renderer = function(value){
                    return value;
                }
            }
            if(column.dataIndex == condition){
                var searchstrs = [];
                var conditions = searchcondition.split(XD.splitChar);
                var contents = searchcontent.split(XD.splitChar);
                for(var i =0;i<conditions.length;i++){
                    if(conditions[i] == condition){
                        searchstrs.push(contents[i]);
                    }
                }
                column.renderer = function(value){
                    var contentData = searchstrs.join('|').split(' ');//切割以空格分隔的多个关键词
                    var reg = new RegExp(contentData.join('|'),'g');
                    return value.replace(reg,function (match) {
                        return '<span style="color:red">'+match+'</span>';
                    });
                }
            }
        });
        if(grid.bookmarkStatus==false){
            var simpleSearchStore = Ext.create('SimpleSearchDirectory.store.SimpleSearchDirectoryGridStore');
            simpleSearchStore.pageSize = typeof(grid.gridPagesSize) == 'undefined' ? grid.getStore().pageSize : grid.gridPagesSize;
            grid.down('pagingtoolbar').down('combo').select(simpleSearchStore.pageSize);
            grid.reconfigure(simpleSearchStore);
        }
        grid.notResetInitGrid();
        grid.parentXtype = 'simpleSearchDirectoryView';
        grid.formXtype = 'simpleSearchDirectoryFormView';
    },

    getCurrentSimpleSearchform:function (btn) {
        return btn.up('simpleSearchDirectoryFormView');
    },

    //点击上一条
    preHandler:function(btn){
        var currentSimpleSearchform = this.getCurrentSimpleSearchform(btn);
        var form = currentSimpleSearchform.down('dynamicform');
        this.refreshFormData(form, 'pre');
    },

    //点击下一条
    nextHandler:function(btn){
        var currentSimpleSearchform = this.getCurrentSimpleSearchform(btn);
        var form = currentSimpleSearchform.down('dynamicform');
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
               /* form.fileLabelStateChange(eleview,operate);
                form.fileLabelStateChange(solidview,operate);*/
                // form.fileLabelStateChange(longview,operate);
            }
        });
    },
    resetSearchCondition:function (grid) {
        grid.getStore().proxy.extraParams.content = '';//清空参数内容
        grid.getStore().removeAll();
        /*检索条件（仅页面显示）重置*/
        var conditionCombo = grid.up('simpleSearchDirectoryView').down('[itemId=simpleSearchSearchComboId]');
        var conditionStore = conditionCombo.getStore();
        if (conditionStore.getCount() > 0) {
            conditionCombo.select(conditionStore.getAt(0));
        }
        /*检索内容（仅页面显示）重置*/
        var searchfield = grid.up('simpleSearchDirectoryView').down('[itemId=simpleSearchSearchfieldId]');
        searchfield.reset();
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
    var record = grid.getSelectionModel().getSelection();
    var array = [];
    for (var i = 0; i < record.length; i++) {
        array[i] = record[i].get('entryid');
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
        }
    });
}
