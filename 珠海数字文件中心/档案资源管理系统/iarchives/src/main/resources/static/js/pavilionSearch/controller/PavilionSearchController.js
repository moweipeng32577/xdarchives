/**
    * Created by RonJiang on 2017/10/24 0024.
    */
Ext.define('PavilionSearch.controller.PavilionSearchController',{
    extend : 'Ext.app.Controller',
    views :  [
       'PavilionSearchGridView','PavilionSearchView','LookAddMxGridView','ApplyPrintAddView','ApplyPrintView','ApplyPrintGridView','ApplyPrintFormView','PrintEleDetailView','PrintEleView'
    ],
    stores:  [
    	'PavilionSearchGridStore','BookmarksGridStore','LookAddMxGridStore','ApproveManStore','ElectronFormGridStore','PrintEleDetailGridStore'
    ],
    models:  [
       'PavilionSearchGridModel','LookAddMxGridModel','ElectronFormGridModel','PrintEleDetailGridModel'
    ],
    init : function() {
        var gridflag = false;
        this.control({
            'pavilionSearchView': {
                render: function (view) {
                    var advandedSearchBtn = view.down('[itemId=advancedSearchBtn]');
                    advandedSearchBtn.hide();     // 高级检索按钮
                    var sGrid = view.down('pavilionSearchGridView');
                    sGrid.initGrid();
                }
            },
            'pavilionSearchGridView [itemId=pavilionSearchShowId]': {
                click: function (btn) {
                    var pavilionSearchGridView = btn.findParentByType('pavilionSearchGridView');
                    var record = pavilionSearchGridView.selModel.getSelection();
                    if (record.length == 0) {
                        XD.msg('请至少选择一条需要查看的数据');
                        return;
                    }
                    var entryid = record[0].get('entryid');
                    var form = this.findFormView(btn).down('dynamicform');
                    var entryids = [];
                    var nodeids = [];
                    for (var i = 0; i < record.length; i++) {
                        entryids.push(record[i].get('entryid'));
                        nodeids.push(record[i].get('nodeid'));
                    }
                    form.operate = 'look';
                    form.entryids = entryids;
                    form.nodeids = nodeids;
                    form.entryid = entryids[0];
                    this.initFormField(form, 'hide', record[0].get('nodeid'));
                    this.initFormData('look', form, entryid);
                }
            },

            'EntryFormView [itemId=preBtn]':{
                click:this.preHandler
            },
            'EntryFormView [itemId=nextBtn]':{
                click:this.nextHandler
            },
            'EntryFormView [itemId=back]': {//返回
                click: function(btn){
                    this.activeGrid(btn, true);
                }
            },
            'pavilionSearchView [itemId=pavilionSearchSearchfieldId]':{
                /*change:function (searchfield) {
                    var instantSearch = searchfield.findParentByType('pavilionSearchView').down('[itemId=instantSearch]').getValue();
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
            'pavilionSearchGridView [itemId=setBookmarks]':{//收藏（或取消收藏）
                click:function(view){
                    var sGrid = view.findParentByType('pavilionSearchGridView');
                    var gridModel = sGrid.getSelectionModel();
                    var record = gridModel.getSelection();
                    if(record.length==0){
                        XD.msg('请选择需要操作的数据!');
                        return;
                    }
                    setBookmarks(sGrid);
                }
            },
            'pavilionSearchGridView [itemId=viewBookmarks]':{//查看收藏（或返回）
                click:function(btn){
                    var sGrid = btn.findParentByType('pavilionSearchGridView');
                    if(sGrid.bookmarkStatus==false){
                        sGrid.setTitle('当前位置：个人收藏');
                        sGrid.down('[itemId=setBookmarks]').setText('取消收藏');
                        sGrid.down('[itemId=setBookmarks]').setIconCls('fa fa-star-o');
                        sGrid.down('[itemId=viewBookmarks]').setText('返回');
                        sGrid.down('[itemId=viewBookmarks]').setIconCls('fa fa-undo');
                        // this.resetSearchCondition(sGrid);
                        sGrid.gridPagesSize =sGrid.getStore().pageSize;
                        sGrid.getStore().removeAll();
                        var bookmarksStore=Ext.create('PavilionSearch.store.BookmarksGridStore');//查找到用户收藏的条目
                        sGrid.reconfigure(bookmarksStore);
                        sGrid.initGrid();
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
                            var pavilionSearchGridStore=Ext.create('PavilionSearch.store.PavilionSearchGridStore');//查找到用户收藏的条目
                            sGrid.reconfigure(pavilionSearchGridStore);
                            sGrid.initGrid();
                        }
                    }
                    var conditionField = this.findView(btn).down('[itemId=pavilionSearchSearchComboId]');
                    conditionField.getStore().reload();
                }
            },

            'pavilionSearchGridView [itemId=addApplyPrint]':{//添加打印申请
                click: function (view) {
                    var sGrid = view.findParentByType('pavilionSearchGridView');
                    var gridModel = sGrid.getSelectionModel();
                    var record = gridModel.getSelection();
                    if(record.length==0){
                        XD.msg('请选择需要操作的数据!');
                        return;
                    }

                    var dataids = [];
                    for (var i = 0; i < record.length; i++) {
                        dataids.push(record[i].get('entryid'));
                        if (record[i].get('eleid') == undefined) {
                            XD.msg('不能含有电子文件为空的条目');
                            return;
                        }
                        if(record[i].get('flagopen') != '原文开放'){
                            XD.msg('不能含有不是原文开放的条目');
                            return;
                        }
                    }

                    Ext.Ajax.request({
                        params: {
                            dataids: dataids,
                            borrowType:'电子打印'
                        },
                        url: '/electron/setPrintBox',
                        method: 'POST',
                        sync: true,
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            if (respText.success == true) {
                                XD.msg(respText.msg);
                            }
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'pavilionSearchGridView [itemId=dealApplyPrint]':{//处理打印申请
                click: function (view) {
                    var boxwin = Ext.create('Ext.window.Window', {
                        height: '100%',
                        width: '100%',
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        modal: true,
                        closeToolText:'关闭',
                        title: '打印申请',
                        closeAction: 'hide',
                        layout: 'fit',
                        items: [{xtype: 'lookAddMxGridView',type:'print'}]
                    });
                    var lookAddMxGrid = boxwin.getComponent('lookAddMxGridViewId');
                    lookAddMxGrid.down("[itemId=stAddSq]").setText('打印申请');
                    lookAddMxGrid.initGrid({'borrowType':'电子打印'});
                    boxwin.show();
                    window.boxwin = boxwin;
                }
            },

            'pavilionSearchGridView [itemId=addApplyDownload]':{//添加下载申请
                click: function (view) {
                    var sGrid = view.findParentByType('pavilionSearchGridView');
                    var gridModel = sGrid.getSelectionModel();
                    var record = gridModel.getSelection();
                    if(record.length==0){
                        XD.msg('请选择需要操作的数据!');
                        return;
                    }

                    var dataids = [];
                    for (var i = 0; i < record.length; i++) {
                        dataids.push(record[i].get('entryid'));
                        if (record[i].get('eleid') == undefined) {
                            XD.msg('不能含有电子文件为空的条目');
                            return;
                        }
                        if(record[i].get('flagopen') != '原文开放'){
                            XD.msg('不能含有不是原文开放的条目');
                            return;
                        }
                    }

                    Ext.Ajax.request({
                        params: {
                            dataids: dataids,
                            borrowType:'下载'
                        },
                        url: '/electron/setPrintBox',
                        method: 'POST',
                        sync: true,
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            if (respText.success == true) {
                                XD.msg(respText.msg);
                            }
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'pavilionSearchGridView [itemId=dealApplyDownload]':{//处理下载申请
                click: function (view) {
                    var boxwin = Ext.create('Ext.window.Window', {
                        height: '100%',
                        width: '100%',
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        modal: true,
                        closeToolText:'关闭',
                        title: '下载申请',
                        closeAction: 'hide',
                        layout: 'fit',
                        items: [{xtype: 'lookAddMxGridView',type:'download'}]
                    });
                    var lookAddMxGrid = boxwin.getComponent('lookAddMxGridViewId');
                    lookAddMxGrid.down("[itemId=stAddSq]").setText('下载申请');
                    lookAddMxGrid.initGrid({'borrowType':'下载'});
                    boxwin.show();
                    window.boxwin = boxwin;
                }
            },

            'lookAddMxGridView button[itemId=stAddSq]': {
                click: function (view) {
                    var select = view.findParentByType('lookAddMxGridView').getSelectionModel();
                    window.wlookAddMxGrid = view.findParentByType('lookAddMxGridView');
                    if (select.getCount() < 1) {
                        XD.msg('至少选择一条数据');
                        return;
                    }

                    var dataids = [];
                    for (var i = 0; i < select.getSelection().length; i++) {
                        dataids.push(select.getSelection()[i].get('entryid'));
                    }
                    window.wmedia = [];

                    if(window.wlookAddMxGrid.type == 'print') { //处理打印申请
                        var win = Ext.create('PavilionSearch.view.ApplyPrintAddView');
                        win.show();
                        var fromGrid = win.getComponent('applyPrintGridViewId');
                        fromGrid.initGrid({dataids: dataids});
                        var form = win.down('[itemId=applyPrintFormViewId]');
                        form.load({
                            url: '/electron/getPrintBorrowDocByIds',
                            params: {
                                dataids: dataids
                            },
                            success: function (form, action) {
                            },
                            failure: function () {
                                XD.msg('操作失败');
                            }
                        });
                    }
                    else if(window.wlookAddMxGrid.type == 'download'){//处理下载申请


                    }
                }
            },

            'lookAddMxGridView button[itemId=remove]': {
                click: function (view) {
                    var dealgrid = view.findParentByType('lookAddMxGridView');
                    window.wlookAddMxGrid = dealgrid;
                    var borrowType;
                    if(dealgrid.type=='print'){
                        borrowType = '电子打印';
                    }
                    else if(dealgrid.type=='download'){
                        borrowType = '下载';
                    }
                    var select = dealgrid.getSelectionModel();
                    if (select.getCount() < 1) {
                        XD.msg('请至少选择一条记录');
                    }
                    else {
                        XD.confirm('是否确定移除选中的记录', function () {
                            var dataids = [];
                            for (var i = 0; i < select.getSelection().length; i++) {
                                dataids.push(select.getSelection()[i].get('entryid'));
                            }
                            Ext.Ajax.request({
                                params: {ids: dataids.join(XD.splitChar),borrowType:borrowType},
                                url: '/electron/deleteBorrowbox',
                                method: 'post',
                                sync: true,
                                success: function () {
                                    dealgrid.getStore().loadPage(1);
                                    XD.msg('移除成功');
                                },
                                failure: function () {
                                    XD.msg('操作中断');
                                }
                            });
                        });
                    }
                }
            },

            'lookAddMxGridView button[itemId=close]': {
                click: function () {
                    window.boxwin.close();
                }
            },

            'applyPrintFormView button[itemId=printSetFormSubmit]': {
                click: function (btn) {
                    var form = btn.findParentByType('applyPrintFormView');
                    var borrowts = form.getComponent('borrowtsId').getValue();
                    var spman = form.getComponent('spmanId').getValue();

                    if (borrowts == '' || borrowts == null||String(borrowts).indexOf(".")>-1||isNaN(borrowts)||parseInt(borrowts)<1 ) {XD.msg('查档天数不合法');return;}
                    if (spman==null) {XD.msg('受理人不能为空');return;}

                    form.submit({
                        url: '/electron/electronPrintSubmit',
                        method: 'POST',
                        params: {
                            eleids:window.wmedia
                        },
                        success: function () {
                            XD.msg('提交成功');
                            window.wlookAddMxGrid.getStore().loadPage(1);
                            btn.findParentByType('window').close();
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'applyPrintFormView button[itemId=printSetFormClose]': {
                click: function (btn) {
                    var eleids = window.wmedia;
                    if(eleids.length>0){
                        Ext.Ajax.request({
                            params: {
                                eleids: eleids
                            },
                            url: '/electron/deleteEvidence',
                            method: 'POST',
                            sync: true,
                            success: function (resp) {
                            },
                            failure: function () {
                                XD.msg('操作失败');
                            }
                        });
                    }
                    btn.findParentByType('window').close();
                }
            },

            'pavilionSearchGridView button[itemId=print]': {
                click: function (btn) {
                    var pavilionSearchGridView = btn.findParentByType('pavilionSearchGridView');
                    var select = pavilionSearchGridView.getSelectionModel().getSelection();
                    if (select.length < 1) {
                        XD.msg('请至少选择一条数据');
                        return;
                    }

                    var entryid = select[0].get("id");
                    var entryids = [];
                    var borrowcode = pavilionSearchGridView.borrowcode;

                    for(var i=0;i<select.length;i++){
                        //根據entryid獲取歸還時間、borrowcode
                        Ext.Ajax.request({
                            url: '/electronPrintApprove/getBorrowmsg',
                            params: {
                                entryid: select[i].get("id")
                            },
                            method: 'POST',
                            success: function (resp) {
                                var respText = Ext.decode(response.responseText);
                            },
                            failure: function (resp) {
                                XD.msg('操作失败！');
                            }
                        });
                        //判断是否已到期
                        if(select[i].get('responsible') < getDateStr(0)){
                            XD.msg('存在打印申请已到期，不允许打印原文！');
                            return;
                        }
                        if(select[i].get('lyqx')==='拒绝'){
                            XD.msg('存在数据未审批通过，不允许打印原文！');
                            return;
                        }
                        entryids.push(select[i].get("id"));
                    }
                    var iframe = document.getElementById("mediaFramePrint");
                    if(iframe){
                        iframe.parentNode.removeChild(iframe);
                    }
                    var printEleView = Ext.create("PavilionSearch.view.PrintEleView");
                    var form = printEleView.down('printEleDetailView');
                    form.entryid = entryid;
                    form.entryids = entryids;
                    form.borrowcode = borrowcode;
                    form.type = "nolyPass";
                    printEleView.show();
                    this.getApplySetPrint(form);
                }
            },

        })
    },

    //获取简单检索应用视图
    findView: function (btn) {
        return btn.findParentByType('pavilionSearchView');
    },

    //获取表单界面视图
    findFormView: function (btn) {
        return this.findView(btn).down('EntryFormView');
    },

    //切换到表单界面视图
    activeForm: function (btn) {
        var view = this.findView(btn);
        var formview = this.findFormView(btn);
        view.setActiveItem(formview);
        formview.items.get(0).enable();
        formview.setActiveTab(0);
        return formview;
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
            var grid = gridview.down('pavilionSearchGridView');
            grid.notResetInitGrid();
        }
    },

    //获取表单
    getCurrentForm:function (btn) {
        return btn.up('EntryFormView');
    },

    //点击上一条
    preHandler:function(btn){
        var currentForm = this.getCurrentForm(btn);
        var form = currentForm.down('dynamicform');
        this.refreshFormData(form, 'pre');
    },

    //点击下一条
    nextHandler:function(btn){
        var currentForm = this.getCurrentForm(btn);
        var form = currentForm.down('dynamicform');
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
    //检索表单信息
    searchInfo:function (searchfield) {
        //获取检索框的值
        var pavilionSearchSearchView = searchfield.findParentByType('panel');
        var condition = pavilionSearchSearchView.down('[itemId=pavilionSearchSearchComboId]').getValue(); //字段
        var operator = 'like';//操作符
        var content = searchfield.getValue(); //内容
        var isCollection;
        var sGrid = searchfield.findParentByType('pavilionSearchView').down('pavilionSearchGridView');
        if (sGrid.title == '当前位置：个人收藏') {//如果是收藏界面
            isCollection = '收藏';
        }
        //检索数据
        //如果有勾选在结果中检索，则添加检索条件，如果没有勾选，则重置检索条件
        var grid = pavilionSearchSearchView.findParentByType('panel').down('pavilionSearchGridView');
        var gridstore = grid.getStore();
        //加载列表数据
        var searchcondition = condition;
        var searchoperator = operator;
        var searchcontent = content;
        //如果选择在结果中查询,将两种条件用','号隔开,参数一起传递到后台
        var inresult = pavilionSearchSearchView.down('[itemId=inresult]').getValue();
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
            content: searchcontent
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
            var pavilionSearchStore = Ext.create('PavilionSearch.store.PavilionSearchGridStore');
            pavilionSearchStore.pageSize = typeof(grid.gridPagesSize) == 'undefined' ? grid.getStore().pageSize : grid.gridPagesSize;
            grid.down('pagingtoolbar').down('combo').select(pavilionSearchStore.pageSize);
            // if(buttonflag=='1') {//利用平台
            //     simpleSearchStore.proxy.url = '/simpleSearch/findBySearchPlatform';
            // }
            grid.reconfigure(pavilionSearchStore);
        }
        grid.notResetInitGrid();
        grid.parentXtype = 'pavilionSearchView';
        grid.formXtype = 'EntryFormView';
    },

    //设置打印范围
    getApplySetPrint:function (form) {
        var count;
        var entryid = form.entryid;
        for (var i = 0; i < form.entryids.length; i++) {
            if (form.entryids[i] == entryid) {
                count = i + 1;
                break;
            }
        }
        var total = form.entryids.length;
        var applySetPrintView = form.findParentByType('applySetPrintView');
        var totaltext = applySetPrintView.down('[itemId=totalText]');
        totaltext.setText('当前共有  ' + total + '  条，');
        var nowtext = applySetPrintView.down('[itemId=nowText]');
        nowtext.setText('当前记录是第  ' + count + '  条');
        var eleGrid = form.down('[itemId=eleGrid]');
        eleGrid.getStore().proxy.extraParams.entryid = form.entryid;
        eleGrid.getStore().reload();
        var allMediaFrame = document.querySelectorAll('#mediaFrame');
        var mediaFrame=allMediaFrame[0];
        mediaFrame.setAttribute('src','');
    }
});

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
    Ext.Msg.wait('正在进行'+operate+'操作，请耐心等待……','正在操作');
    Ext.Ajax.request({
        url:'/bookmarks/setBookmarks',
        method: 'POST',
        timeout:XD.timeout,
        params:{
            entryids:array,
            bookmarkStatus:grid.bookmarkStatus
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

//计算打印范围
function getDateStr(AddDayCount) {
    var dd = new Date();
    dd.setDate(dd.getDate()+AddDayCount);//获取AddDayCount天后的日期
    var y = dd.getFullYear();
    var m = dd.getMonth()+1;//获取当前月份的日期
    var d = dd.getDate();
    if (m >= 1 && m <= 9) {
        m = "0" + m;
    }
    if (d >= 0 && d <= 9) {
        d = "0" + d;
    }
    return y+""+m+""+d;
}