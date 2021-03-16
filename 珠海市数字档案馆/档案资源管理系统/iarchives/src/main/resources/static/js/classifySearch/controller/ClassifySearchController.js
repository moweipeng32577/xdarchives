/**
 * Created by RonJiang on 2017/10/26 0026.
 */
var nodeidInfo;
Ext.define('ClassifySearch.controller.ClassifySearchController',{
    extend : 'Ext.app.Controller',
    views :  [
        'ClassifySearchView',
        'ClassifySearchFormView',
        'ClassifySearchResultGridView',
        'ClassifySearchExportWin',
        'ClassifySearchPromptView',
        'ClassifySearchTreeView',
        'ReportGridView',
        'ClassifyLookAddMxGridView',
        'ClassifyElectronAddView',
        'ClassifyElectronFormGridView',
        'ClassifyElectronFormItemView',
        'ClassifyElectronFormView',
        'ClassifyLookAddFormGridView',
        'ClassifyLookAddFormItemView',
        'ClassifyLookAddFormView',
        'ClassifyLookAddSqView'
    ],
    stores:  [
        'ClassifySearchTreeStore',
        'ReportGridStore',
        'ClassifyBookmarksGridStore',
        'ClassifyLookAddMxGridStore',
        'ClassifyElectronFormGridStore',
        'ClassifyLookAddFormGridStore',
        'ClassifyJypurposeStore',
        'ClassifyApproveManStore'

    ],
    models:  [
        'ClassifySearchTreeModel',
        'ReportGridModel',
        'ClassifySearchGridModel',
        'ClassifyLookAddMxGridModel',
        'ClassifyElectronFormGridModel',
        'ClassifyLookAddFormGridModel'
    ],
    init : function() {
        var ifShowRightPanel = false;
        var classifySearchResultGridView ,
            printWin;
        var count = 0;
        var treeNode;
        this.control({
            'classifySearchTreeView': {
                render: function (view) {
                    if (buttonflag == '1') {//利用平台
                        view.getRootNode().on('expand', function (node) {
                            for (var i = 0; i < node.childNodes.length; i++) {
                                if (node.childNodes[i].raw.text == "已归管理"||node.childNodes[i].raw.text == "归档管理")//默认打开已归管理第一条节点
                                {
                                    treeNode = node.childNodes[i].raw.id;
                                    node.getOwnerTree().expandPath(node.childNodes[i].raw.id, "id");
                                    node.getOwnerTree().getSelectionModel().select(node.childNodes[i]);
                                }
                                if (node.childNodes[i].raw.parentId == treeNode) { //找到已归管理下的所有节点
                                    treeNode = node.childNodes[0].raw.id;
                                    node.getOwnerTree().expandPath(node.childNodes[0].raw.id, "id");
                                    node.getOwnerTree().getSelectionModel().select(node.childNodes[0]);
                                }
                            }
                        })
                    }
                },
                select: function (treemodel, record) {
                    var classifySearchView = treemodel.view.findParentByType('classifySearchView');
                    var classifySearchPromptView = classifySearchView.down('[itemId=classifySearchPromptViewId]');
                    if(record.raw.cls=='folder'){
                        classifySearchPromptView.removeAll();
                        classifySearchPromptView.add({
                            xtype: 'classifySearchPromptView'
                        });
                        ifShowRightPanel = true;
                        return;
                    }

                    if(!ifShowRightPanel || !classifySearchView.down('advancedSearchDynamicForm')){//利用平台标签切换时无法加载动态表单的bug修复
                        classifySearchPromptView.removeAll();
                        classifySearchPromptView.add({
                            xtype: 'classifySearchFormView'
                        });
                        ifShowRightPanel = true;
                    }
                    var classifySearchResultgrid = classifySearchView.down('classifySearchResultGridView');
                    var buttons = classifySearchResultgrid.down("toolbar");
                    var tbseparator = classifySearchResultgrid.down("toolbar").query('tbseparator');
                    if(buttonflag=='1'){//利用平台
                        buttons.down('[itemId=stAdd]').show();
                        buttons.down('[itemId=lookAdd]').show();
                        buttons.down('[itemId=electronAdd]').show();
                        buttons.down('[itemId=dealElectronAdd]').show();
                        tbseparator[tbseparator.length-1].show();
                        tbseparator[tbseparator.length-2].show();
                        tbseparator[tbseparator.length-3].show();
                    }else{
                        //隐藏添加实体查档按钮
                        buttons.down('[itemId=stAdd]').hide();
                        //隐藏处理实体查档按钮
                        buttons.down('[itemId=lookAdd]').hide();
                        //隐藏电子查档按钮
                        buttons.down('[itemId=electronAdd]').hide();
                        //隐藏处理电子查档按钮
                        buttons.down('[itemId=dealElectronAdd]').hide();
                        tbseparator[tbseparator.length-1].hide();
                        tbseparator[tbseparator.length-2].hide();
                        tbseparator[tbseparator.length-3].hide();
                    }
                    var classid = record.get('fnid');
                    var nodeids;
                    Ext.Ajax.request({
                        url: '/nodesetting/getNodeidByRefid',
                        async:false,
                        params:{
                            refid:classid
                        },
                        success: function (response) {
                            nodeids = Ext.decode(response.responseText);
                        }
                    });
                    classifySearchResultgrid.nodeids = nodeids;
                    classifySearchResultgrid.nodeid = nodeids[0];
                    classifySearchResultgrid.initGrid({nodeid:classifySearchResultgrid.nodeid});
                    
                    nodeidInfo = nodeids[0];
                    var advancedSearchDynamicForm = classifySearchView.down('advancedSearchDynamicForm');
                    this.initAdvancedSearchFormField(advancedSearchDynamicForm,nodeids[0]);
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

            'ReportGridView [itemId=showAllReport]':{//显示所有报表
                click:function (btn) {
                    var reportGrid = btn.findParentByType('ReportGridView');
                    if(reportGrid.down('[itemId=showAllReport]').text=='显示所有报表'){
                        reportGrid.down('[itemId=showAllReport]').setText('显示当前报表');
                        reportGrid.initGrid({nodeid:reportGrid.nodeid,flag:'all'});
                    }else if(reportGrid.down('[itemId=showAllReport]').text=='显示当前报表'){
                        reportGrid.down('[itemId=showAllReport]').setText('显示所有报表');
                        reportGrid.initGrid({nodeid:reportGrid.nodeid + ',公有报表'});
                    }
                }
            },

            'ReportGridView [itemId=back]':{//打印返回
                click:function (btn) {
                    printWin.close();
                }
            },
            'classifySearchFormView':{
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
            'classifySearchFormView button[itemId=topSearchBtn]':{click:this.doAdvancedSearch},
            'classifySearchFormView button[itemId=bottomSearchBtn]':{click:this.doAdvancedSearch},
            'classifySearchFormView button[itemId=topClearBtn]':{click:this.doAdvancedSearchClear},
            'classifySearchFormView button[itemId=bottomClearBtn]':{click:this.doAdvancedSearchClear},
            'classifySearchFormView button[itemId=topCloseBtn]':{click:this.doAdvancedSearchClose},
            'classifySearchFormView button[itemId=bottomCloseBtn]':{click:this.doAdvancedSearchClose},
            'classifySearchResultGridView button[itemId=classifySearchBackId]':{click:this.doAdvancedSearchBack},
            'classifySearchResultGridView button[itemId=classifySearchShowId]':{
                click:function(btn){
                    var classifySearchResultGridView = btn.findParentByType('classifySearchResultGridView');
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

            'classifySearchResultGridView ':{
                eleview: this.activeEleForm
            },
            'classifySearchResultGridView': {
                rowdblclick: function (view,record) {
                    var entryid = record.get('entryid');
                    var form = this.findFormView(view).down('dynamicform');
                    this.initFormField(form, 'hide', record.get('nodeid'));
                    this.initFormData('look',form, entryid);
                }
            },
            // 'classifySearchResultGridView button[itemId=close]':{ //关闭按钮
            //     click: function () {
            //         window.boxwin.close();
            //     }
            // },
            'classifySearchResultGridView [itemId=setBookmarks]':{//收藏（或取消收藏）
                click:function(view){
                    var sGrid = view.findParentByType('classifySearchResultGridView');
                    var gridModel = sGrid.getSelectionModel();
                    var record = gridModel.getSelection();
                    if(record.length==0){
                        XD.msg('请选择需要操作的数据!');
                        return;
                    }
                     setBookmarks(sGrid);
                }
            },
            'classifySearchResultGridView [itemId=viewBookmarks]':{//查看收藏（或返回）
                click:function(btn){
                    var sGrid = btn.findParentByType('classifySearchResultGridView');
                    if(sGrid.bookmarkStatus==false){
                        sGrid.setTitle('当前位置：个人收藏');
                        sGrid.down('[itemId=setBookmarks]').setText('取消收藏');
                        sGrid.down('[itemId=setBookmarks]').setIconCls('fa fa-star-o');
                        sGrid.down('[itemId=viewBookmarks]').setText('返回');
                        sGrid.down('[itemId=viewBookmarks]').setIconCls('fa fa-undo');
                        var pagesize = sGrid.getStore().pageSize;
                        sGrid.getStore().removeAll();
                        var bookmarksStore=Ext.create('ClassifySearch.store.ClassifyBookmarksGridStore');//查找到用户收藏的条目
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
                        sGrid.getStore().proxy.url = '/classifySearch/findByClassifySearch';
                        sGrid.notResetInitGrid();
                    }
                }
            },
            'classifySearchResultGridView [itemId=stAdd]':{//添加实体查档
                click: function (view) {
                    var sGrid = view.findParentByType('classifySearchResultGridView');
                    var gridModel = sGrid.getSelectionModel();
                    var record = gridModel.getSelection();
                    if(record.length==0){
                        XD.msg('请选择需要操作的数据!');
                        return;
                    }
                    var dataids = [];
                    for (var i = 0; i < record.length; i++) {
                        dataids.push(record[i].get('entryid'));
                    }
                    Ext.Ajax.request({
                        params: {
                            dataids: dataids,
                            borrowType:'实体查档'
                        },
                        url: '/electron/setStJyBox',
                        method: 'POST',
                        sync: true,
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            if (respText.success == true) {
                                XD.msg(respText.msg);
                            }
                        },
                        failure: function (resp) {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'classifySearchResultGridView [itemId=lookAdd]':{//处理实体查档
                click: function () {
                    var boxwin = Ext.create('Ext.window.Window', {
                        height: '100%',
                        width: '100%',
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        modal: true,
                        closeToolText:'关闭',
                        title: '查档申请',
                        closeAction: 'hide',
                        layout: 'fit',
                        items: [{xtype: 'classifylookAddMxGridView'}]
                    });
                    boxwin.getComponent('classifylookAddMxGridViewId').initGrid({'borrowType':'实体查档'});
                    boxwin.show();
                    window.boxwin = boxwin;
                }
            },
            'classifylookAddMxGridView button[itemId=stAddSq]': {//处理实体、电子查档--查档申请
                click: function (view) {
                    var select = view.findParentByType('classifylookAddMxGridView').getSelectionModel();
                    window.wlookAddMxGrid = view.findParentByType('classifylookAddMxGridView');
                    if (select.getCount() < 1) {
                        XD.msg('至少选择一条数据');
                        return;
                    }
                    var dataids = [];
                    for (var i = 0; i < select.getSelection().length; i++) {
                        dataids.push(select.getSelection()[i].get('entryid'));
                    }
                    if(window.wlookAddMxGrid.type==1){
                        var win = Ext.create('ClassifySearch.view.ClassifyElectronAddView');
                        win.show();
                        var fromGrid = win.getComponent('classifyElectronFormGridViewId');
                        fromGrid.initGrid({dataids: dataids});
                        var form = win.down('[itemId=classifyElectronFormItemViewId]');
                        //form.down('[itemId=spmanId]').getStore().reload();
                        form.load({
                            url: '/electron/getBorrowDocByIds',
                            method: 'POST',
                            params: {
                                dataids: dataids
                            },
                            success: function (form, action) {
                            },
                            failure: function () {
                                XD.msg('操作失败');
                            }
                        });
                    }else{
                        var sqWin = Ext.create('ClassifySearch.view.ClassifyLookAddSqView');
                        var form = sqWin.down('[itemId=classifylookAddFormItemViewId]');
                        form.load({
                            url: '/electron/getBorrowDocByIds',
                            method: 'POST',
                            params: {
                                dataids: dataids
                            },
                            success: function (form, action) {
                            },
                            failure: function () {
                                XD.msg('操作失败');
                            }
                        });
                        sqWin.show();

                        var fromGrid = sqWin.getComponent('classifylookAddFormGridViewId');
                        fromGrid.initGrid({dataids: dataids});
                    }
                }
            },
            'classifylookAddMxGridView button[itemId=remove]': {//处理实体、电子查档--移除
                click: function (view) {
                    var dealgrid = view.findParentByType('classifylookAddMxGridView');
                    var borrowType = dealgrid.type==1?'电子查档':'实体查档';
                    window.wlookAddMxGrid = dealgrid;
                    var select = dealgrid.getSelectionModel();
                    if (select.getCount() < 1) {
                        XD.msg('请至少选择一条记录');
                    } else {
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
            'classifylookAddMxGridView button[itemId=close]': {//处理实体、电子查档--关闭
                click: function () {
                    window.boxwin.close();
                }
            },

            'classifySearchResultGridView [itemId=electronAdd]':{//添加电子查档
                click: function (view) {
                    var sGrid = view.findParentByType('classifySearchResultGridView');
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
                    }

                    Ext.Ajax.request({
                        params: {
                            dataids: dataids,
                            borrowType:'电子查档'
                        },
                        url: '/electron/setStJyBox',
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
            'classifySearchResultGridView [itemId=dealElectronAdd]':{//处理电子查档
                click: function (view) {
                    var boxwin = Ext.create('Ext.window.Window', {
                        height: '100%',
                        width: '100%',
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        modal: true,
                        closeToolText:'关闭',
                        title: '电子查档申请',
                        closeAction: 'hide',
                        layout: 'fit',
                        items: [{xtype: 'classifylookAddMxGridView',type:1}]
                    });
                    var lookAddMxGrid = boxwin.getComponent('classifylookAddMxGridViewId');
                    lookAddMxGrid.initGrid({'borrowType':'电子查档'});
                    boxwin.show();
                    window.boxwin = boxwin;
                }
            },
            'classifyElectronFormItemView button[itemId=classifyElectronFormClose]': { //查档申请关闭按钮
                click: function (view) {
                    view.findParentByType('classifyElectronAddView').close();
                }
            },
            'classifyElectronFormItemView button[itemId=classifyElectronFormSubmit]': { //查档申请提交按钮
                click: function (btn) {
                    var form = btn.findParentByType('classifyElectronFormItemView');
                    var borrowts = form.getComponent('numberfield').getValue();
                    var spman = form.getComponent('spmanId').getValue();

                    if (borrowts == '' || borrowts == null||String(borrowts).indexOf(".")>-1||isNaN(borrowts)||parseInt(borrowts)<1 ) {XD.msg('查档天数不合法');return;}
                    if (spman==null) {XD.msg('审批人不能为空');return;}

                    var electronAddWin = btn.findParentByType('classifyElectronAddView');
                    form.submit({
                        url: '/electron/electronBill',
                        method: 'POST',
                        params: {},
                        success: function () {
                            XD.msg('提交成功');
                            window.wlookAddMxGrid.getStore().loadPage(1);
                            electronAddWin.close();
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'classifySearchView [itemId=preBtn]':{
                click:this.preHandler
            },
            'classifySearchView [itemId=nextBtn]':{
                click:this.nextHandler
            },
            'classifySearchView button[itemId=back]':{//查看条目表单　返回
                click:function(btn){
                    this.activeGrid(btn,false);
                }
            },
            'classifySearchResultGridView button[itemId=classifySearchExportId]':{
                click: function (view) {
                    var names = [];
                    var keys = [];
                    classifySearchResultGridView = view.findParentByType('classifySearchResultGridView');
                    var record = classifySearchResultGridView.selModel.getSelection();
                    var form = this.findFormView(view).down('dynamicform');
                    form.nodeid = record[0].get('nodeid');
                    var formField = form.getFormField();//根据节点id查询表单字段
                    // for(var i = 0; i<formField.length; i++){
                    //     names.push(formField[i].fieldname);
                    //     keys.push(formField[i].fieldcode);
                    // }
                    var columnslist =classifySearchResultGridView.getColumns();
                    for(var i =2;i < columnslist.length;i++){
                        names.push(columnslist[i].text);
                        keys.push(columnslist[i].dataIndex);
                    }
                    if (record.length == 0) {
                        XD.msg('请选择需要导出的数据');
                        return;
                    }else{
                        Ext.create("ClassifySearch.view.ClassifySearchExportWin",{
                            names:names,
                            keys:keys
                        }).show();
                    }
                }
            },
            'classifySearchExportWin button[itemId=classifySearchExportBtnId]':{
                click: function (view) {
                    var classifySearchExportWin = view.findParentByType('classifySearchExportWin');
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
                    downloadForm.action='/classifySearch/exportData?type=management';
                    downloadForm.method = "post";
                    downloadForm.submit();
                   // window.location.href="/classifySearch/exportData?fileName="+encodeURIComponent(exportFileName)+"&entryids="+array;
                    // appWindow.focus();
                    view.up('classifySearchExportWin').close();
                }
            },
            'classifySearchExportWin button[itemId=classifySearchExportCloseBtnId]':{
                click: function (view) {
                    view.up('classifySearchExportWin').close();
                }
            },

            'classifySearchResultGridView [itemId=print]':{//进入打印页面
                click: function (btn) {
                    printWin = this.chooseReport(btn,printWin);
                }
            }
        });
    },



    //打开报表显示列表
    chooseReport:function(btn){
        var grid = btn.up('classifySearchResultGridView');
        var ids = [];
        Ext.each(grid.getSelectionModel().getSelection(), function () {
            ids.push(this.get('entryid'));
        });

        if(ids.length==0){
            XD.msg('请至少选择一条需要打印的数据');
            return;
        }
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
        reportGrid.initGrid({nodeid:reportGrid.nodeid + ',公有报表'});
        reportGridWin.show();
        return reportGridWin;
    },


    //获取分类检索应用视图
    findView: function (btn) {
        return btn.findParentByType('classifySearchView');
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
        return this.findView(btn).down('classifySearchResultGridView');
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
        var form = this.findView(btn).down('classifySearchFormView');
        var filedateStartField = form.getForm().findField('filedatestart');
        var filedateEndField = form.getForm().findField('filedateend');
        var datasoure = form.down('[itemId=datasoureId]').getChecked()[0].inputValue;
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

        var grid = this.findView(btn).down('classifySearchResultGridView');
        if(buttonflag=='1') {//利用平台
            grid.dataUrl = '/classifySearch/findByClassifySearchPlatform';
        }
        formParams.datasoure=datasoure;
        formParams.nodeid = grid.nodeids[0];
        formParams.nodeids = grid.nodeids.join(',');
        //点击非叶子节点时，是否查询出其包含的所有叶子节点数据
        formParams.ifSearchLeafNode = true;
        //点击非叶子节点时，是否查询出当前非叶子节点及其包含的所有非叶子节点数据
        formParams.ifContainSelfNode = false;
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
        Ext.Ajax.request({
            method: 'post',
            url:'/classifySearch/setLastSearchInfo',
            timeout:XD.timeout,
            scope: this,
            async: true,
            params: {
            	nodeid: grid.nodeids[0],
            	fieldColumn: fieldColumn,
            	fieldValue: fieldValue,
            	type: '分类检索'
            },
            success:function(res){
            },
            failure:function(){
                Ext.MessageBox.hide();
                XD.msg('操作失败！');
            }
        });
    },
    doAdvancedSearchBack:function(btn){//返回检索条件输入页面
        var grid = this.findView(btn).down('classifySearchResultGridView');
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

    getCurrentClassifySearchform:function (btn) {
        return btn.up('EntryFormView');
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
            params:{
                datasoure: form.datasoure
            },
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

    doAdvancedSearchClear:function(btn){//清除检索条件页面所有控件的输入值
    	// var form = this.findView(btn).down('classifySearchFormView').getForm();
        var form = this.findView(btn).down('advancedSearchDynamicForm');
    	Ext.Ajax.request({
	        url: '/classifySearch/clearSearchInfo',
	        async:false,
	        params:{
	            nodeid:nodeidInfo,
	            type:'分类检索'
	        },
	        success: function (response) {
	        	// form.reset();//表单重置，只是将表单返回到上一次载入页面时的状态，初次载入有历史值时，并不能清空输入栏
                var formItems = form.getValues();
                for(var field in formItems){
                    if(field.indexOf('Combo')>-1){
                        continue;
                    }
                    form.down('[name='+field+']').setValue();
                }
	        },
	        failure:function(){
                XD.msg('操作失败！');
            }
	    });
    },
    
    doAdvancedSearchClose:function(btn){
        if(!parent.closeObj){//利用平台关闭高级检索,切换回简单检索
            location.href = '/simpleSearch/mainly?flag=1';
            return;
        }
        parent.closeObj.close(parent.layer.getFrameIndex(window.name));
    },
    
    initAdvancedSearchFormField:function(form, nodeid){
        if(form){
            if (form.nodeid != nodeid) {//切换节点后，form和tree的节点id不相等
                form.nodeid = nodeid;//用左侧树节点的id初始化form的nodeid参数
                form.removeAll();//移除form中的所有表单控件
                var formField = form.getFormField();//根据节点id查询表单字段
                formField.type = '分类检索';
                if(formField.length==0){
                    XD.msg('请检查模板设置信息是否正确');
                    return;
                }
                form.templates = formField;
                form.initSearchConditionField(formField);//重新动态添加表单控件
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
    Ext.Msg.wait('正在进行'+operate+'操作，请耐心等待……','正在操作');
    Ext.Ajax.request({
        url:'/bookmarks/setBookmarks',
        method: 'POST',
        timeout:XD.timeout,
        params:{
            entryids:array,
            bookmarkStatus:grid.bookmarkStatus,
            type:"management"
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