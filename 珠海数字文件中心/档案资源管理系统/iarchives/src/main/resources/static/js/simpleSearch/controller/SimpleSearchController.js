/**
 * Created by RonJiang on 2017/10/24 0024.
 */
var setBorrowType="";//保存设置查档类型
var isFlag="";//1为声像开放
var dataSourceType="";
Ext.define('SimpleSearch.controller.SimpleSearchController',{
    extend : 'Ext.app.Controller',
    views :  [
        'SimpleSearchView','SimpleSearchGridView','SimpleSearchExportWin',//简单检索
        'LookAddMxGridView','ElectronAddView','ElectronFormGridView',//查档利用
        'ElectronFormItemView','ElectronFormView','LookAddFormGridView',
        'LookAddFormItemView','LookAddFormView','LookAddSqView','ReportGridView',
        'ElectronicView','ApplyPrintView','ApplyPrintGridView','ApplyPrintFormView','ApplyPrintAddView',
        'ApplySetPrintView','ApplyPrintSetEleView','ApplySetPrintScopeView',
        'SimpleSearchMediaView','MediaDataView','SetBorrowTypeView','StLookFormView','StLookAddFormView',
        'ElectronFormAddView','ElectronAddFormView','MediaGridView','MediaTabView'
    ],
    stores:  [
        'SimpleSearchGridStore','BookmarksGridStore',
        'LookAddMxGridStore','LookAddFormGridStore',
        'ElectronFormGridStore','ApproveManStore',
        'JypurposeStore','ReportGridStore','ApplyPrintEleGridStore','SimpleSearchOpenGridStore',
        'MediaDtStore','ApproveOrganStore','MediaGridStore','PurposeStore'
    ],
    models:  [
        'SimpleSearchGridModel',
        'ElectronFormGridModel',
        'LookAddMxGridModel',
        'LookAddFormGridModel',
        'ReportGridModel',
        'ApplyPrintEleGridModel',
        'MediaDataModel',
        'MediaGridModel'
    ],
    init : function() {
        var simpleSearchGridView,printWin;
        var count = 0;
        var gridflag = false;
        this.control({
            'simpleSearchView':{
                render:function (view) {
                    //打开声像的缩略图显示
                    if(resultType != null){
                        var sGrid = view.down('simpleSearchGridView');
                        sGrid.hide();
                        var sMedia = view.down('simpleSearchMediaView');
                        sMedia.show();
                    }
                    else {
                        var sGrid = view.down('simpleSearchGridView');
                        sGrid.show();
                        var sMedia = view.down('simpleSearchMediaView');
                        sMedia.hide();
                    }
                    var closeBtn = view.down('[itemId=topCloseBtn]');
                    var advandedSearchBtn = view.down('[itemId=advancedSearchBtn]');
                    var sGrid = view.down('simpleSearchGridView');
                    var sMedia = view.down('simpleSearchMediaView').down('mediadtView');
                    var mediaGridView = view.down('simpleSearchMediaView').down('mediaGridView');
                    var buttons = sGrid.down("toolbar");
                    var tbseparator = sGrid.down("toolbar").query('tbseparator');
                    var formview = view.down('EntryFormView');
                    var soildview = formview.down('solid');
                    var datasoureRadio=sGrid.findParentByType('simpleSearchView').down('[itemId=datasoure]');
                    var datasoureRadio2=sGrid.findParentByType('simpleSearchView').down('[itemId=datasoure2]');
                    if(buttonflag=='1'){//利用平台
                        soildview.isJy=true;     //查看添加水印
                        closeBtn.hide();
                        //高级检索按钮
                        advandedSearchBtn.show();
                        //打印按钮
                        buttons.down('[itemId=print]').hide();
                        tbseparator[tbseparator.length-4].hide();
                        var simpleSearchStore = sGrid.getStore();
                        simpleSearchStore.proxy.url = '/simpleSearch/findBySearchPlatform';
                        //隐藏数据源选择
                        datasoureRadio.hide();
                        //    sGrid.initGrid();         关闭了自动检索
                    }else if (buttonflag == "2"){//编研管理中的馆库查询
                        soildview.isJy=true;     //查看添加水印
                        closeBtn.hide();
                        advandedSearchBtn.show();     //高级检索按钮
                        advandedSearchBtn.hide();     // 高级检索按钮
                        buttons.down('[itemId=electronprint]').hide();
                        buttons.down('[itemId=print]').hide();
                        //提交查档单
                        buttons.down('[itemId=electronprint]').hide();
                        //隐藏打印处理按钮
                        buttons.down('[itemId=dealApplyPrint]').hide();
                        //隐藏利用未开放申请表打印按钮
                        buttons.down('[itemId=wkfprint]').hide();
                        tbseparator[tbseparator.length-1].hide();
                        tbseparator[tbseparator.length-2].hide();
                        tbseparator[tbseparator.length-3].hide();
                        tbseparator[tbseparator.length-4].hide();
                        tbseparator[tbseparator.length-5].hide();
                        var simpleSearchStore = sGrid.getStore();
                        var datasoure = simpleSearchStore.getProxy().extraParams.datasoure;  //数据源
                        simpleSearchStore.proxy.url = '/simpleSearch/findBySearchPlatform';
                        sGrid.initGrid({isCompilationManageSystem:true,datasoure:datasoure});//是否为编研管理系统
                        datasoureRadio.hide();
                        datasoureRadio2.show();
                    }else if(buttonflag=='a'){

                        soildview.isJy=true;     //查看添加水印
                        closeBtn.hide();
                        advandedSearchBtn.show();     //高级检索按钮
                        advandedSearchBtn.hide();     // 高级检索按钮
                        buttons.down('[itemId=electronprint]').hide();
                        buttons.down('[itemId=print]').hide();
                        //提交查档单
                        buttons.down('[itemId=electronprint]').hide();
                        buttons.down('[itemId=setBookmarks]').hide();
                        buttons.down('[itemId=viewBookmarks]').hide();
                        buttons.down('[itemId=electronborrowmenu]').hide();
                        buttons.down('[itemId=submiteleborrow]').hide();
                        //隐藏打印处理按钮
                        buttons.down('[itemId=dealApplyPrint]').hide();
                        //隐藏利用未开放申请表打印按钮
                        buttons.down('[itemId=wkfprint]').hide();
                        tbseparator[tbseparator.length-1].hide();
                        tbseparator[tbseparator.length-2].hide();
                        tbseparator[tbseparator.length-3].hide();
                        tbseparator[tbseparator.length-4].hide();
                        tbseparator[tbseparator.length-5].hide();
                        var simpleSearchStore = sGrid.getStore();
                        var datasoure = simpleSearchStore.getProxy().extraParams.datasoure;  //数据源
                        simpleSearchStore.proxy.url = '/simpleSearch/findBySearchPlatform';
                        sGrid.initGrid({isCompilationManageSystem:true,datasoure:datasoure});//是否为编研管理系统
                        datasoureRadio.hide();
                        datasoureRadio2.hide();
                    } else if(buttonflag=='80') {//利用平台声像
                        // var simpleSearchMediaView = sMedia.datastore;
                        sMedia.down('dataview').getStore();
                        sMedia.initGrid();
                        mediaGridView.initGrid();
                        datasoureRadio.hide();
                        dataSourceType="soundimage";
                    }else {
                        closeBtn.show();
                        advandedSearchBtn.hide();     // 高级检索按钮
                        //查档单
                        buttons.down('[itemId=electronborrowmenu]').hide();
                        //打印申请单
                        buttons.down('[itemId=electronprint]').hide();
                        //提交查档单
                        buttons.down('[itemId=submiteleborrow]').hide();
                        //隐藏打印处理按钮
                        if(buttons.down('[itemId=submitborrow]')!=null){
                            buttons.down('[itemId=submitborrow]').hide();
                        }
                        //隐藏利用未开放申请表打印按钮
                        buttons.down('[itemId=wkfprint]').hide();
                        tbseparator[tbseparator.length-1].hide();
                        tbseparator[tbseparator.length-2].hide();
                        tbseparator[tbseparator.length-3].hide();
                        tbseparator[tbseparator.length-4].hide();
                        var datasoure = sGrid.findParentByType('simpleSearchView').down('[itemId=datasoure]').getChecked()[0].inputValue;//数据源
                        sGrid.initGrid({datasoure:datasoure});
                    }
                }
            },

            'mediadtView [itemId=backopen]': {
                click:function (btn) {
                    parent.simpleSearchFrame.close();
                }
            },
            'mediaTabView':{ //隐藏选项卡标题
                render:function(view){
                    view.tabBar.hidden = true;
                }
            },

            'mediaGridView [itemId=mediaDataShowId]':{  //缩列图显示
                click:function (view) {
                    var mediaTabView = view.findParentByType('mediaTabView');
                    var mediadtView = mediaTabView.down('mediadtView');
                    mediaTabView.setActiveItem(mediadtView);
                }
            },

            'mediadtView [itemId=gridShowId]':{  //列表显示
                click:function (view) {
                    var mediaTabView = view.findParentByType('mediaTabView');
                    var mediaGridView = mediaTabView.down('mediaGridView');
                    mediaTabView.setActiveItem(mediaGridView);
                }
            },

            'simpleSearchView [itemId=backopen]': {
                click:function (btn) {
                    parent.simpleSearchFrame.close();
                }
            },
            'mediadtView [itemId=add]': {//加入送审单
                click: this.addFnc
            },
            'simpleSearchView [itemId=add]': {//加入送审单
                click: this.addFnc
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

            'simpleSearchView [itemId=advancedSearchBtn]':{
                click:function () {
                    location.href = '/classifySearch/mainly?flag=1';
                }
            },
            'simpleSearchView [itemId=simpleSearchSearchfieldId]':{
                /*change:function (searchfield) {
                    var instantSearch = searchfield.findParentByType('simpleSearchView').down('[itemId=instantSearch]').getValue();
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
            'mediadtView [itemId=simpleSearchShowId],mediaGridView [itemId=simpleSearchShowId]':{
                click:function(btn){
                    var record;
                    if(btn.findParentByType('mediadtView')){  //缩列图
                        var simpleSearchGridView = btn.findParentByType('mediadtView');
                        record = simpleSearchGridView.acrossSelections ;
                    }else{
                        var mediaGridView = btn.findParentByType('mediaGridView');
                        record = mediaGridView.getSelectionModel().getSelection();
                    }
                    if(record.length==0){
                        XD.msg('请至少选择一条需要查看的数据');
                        return;
                    }
                    var form = this.findFormView(btn).down('dynamicform');
                    if (buttonflag === '80') {//利用平台
                        if(record.length!=1){
                            XD.msg('请选择一条需要查看的数据');
                            return;
                        }
                        form.winType = 'lySimpleSearch';//利用平台标记,日志用
                        if(resultType != null){
                            form.datasoure = 'soundimage';//声像数据
                        }
                    }else{
                        form.winType = 'glSimpleSearch';//管理平台标记，日志用
                    }
                    var entryForm = this.findView(btn).down('EntryFormView');
                    for (var j = 0; j < entryForm.items.items.length; j++) {
                        if ( entryForm.items.get(j).xtype !== 'dynamicform') {
                            entryForm.items.get(j).setDisabled(true);
                        }
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
                    this.initFormData('look', form, entryid);
                    // var nodename=record[0].get('nodefullname');
                    // var mediaFormView = this.getNewMediaFormView(btn,'look',nodename,record);
                    // var form = mediaFormView.down('[itemId=dynamicform]');
                    // var initFormFieldState = this.initFormField(form, 'hide', record[0].get('nodeid'));
                    // if(!initFormFieldState){//表单控件加载失败
                    //     return;
                    // }
                    // form.operate = 'look';
                    // var records = record;
                    // form.entryids = entryids;
                    // form.nodeids = nodeids;
                    // form.selectItem = records;
                    // form.entryid = records[0].get('entryid');
                    // this.initMediaFormData('look', form, records[0].get('entryid'), records[0]);
                    // mediaFormView.down("[itemId=batchUploadBtn]").hide();
                    // mediaFormView.down('[itemId=save]').hide();
                    // if(nodename.indexOf('音频') !== -1){
                    //     mediaFormView.down('[itemId=mediaDetailViewItem]').expand();
                    // }
                    // form.up('simpleSearchView').setActiveItem(mediaFormView);
                }
            },
            'mediaFormView [itemId=mediaBack]': {//查看返回
                click: this.lookBack
            },
            'simpleSearchGridView [itemId=simpleSearchShowId]':{
                click:function(btn){
                    var simpleSearchGridView = btn.findParentByType('simpleSearchGridView');
                    var record = simpleSearchGridView.selModel.getSelection();
                    if(record.length==0){
                        XD.msg('请至少选择一条需要查看的数据');
                        return;
                    }
                    if(dataSourceType=="soundimage"&&record.length!=1){
                        XD.msg('请选择一条需要查看的数据');
                        return;
                    }
                    if (buttonflag === '1'||buttonflag === '2'||buttonflag==='a') {//利用平台  编研管理平台
                        var isOpen = record[0].get('flagopen');
                        var entryForm = this.findView(btn).down('EntryFormView');
                        for (var j = 0; j < entryForm.items.items.length; j++) {
                            if (isOpen === '条目开放' && entryForm.items.get(j).xtype !== 'dynamicform') {
                                entryForm.items.get(j).setDisabled(true);
                            }else if(isOpen === '原文开放' && entryForm.items.get(j).xtype == 'electronic'){
                                entryForm.items.get(j).setDisabled(true);
                            } else {
                                entryForm.items.get(j).setDisabled(false);
                            }
                        }
                    }
                    var entryids = [];
                    var nodeids = [];
                    for (var i = 0; i < record.length; i++) {
                        entryids.push(record[i].get('entryid'));
                        nodeids.push(record[i].get('nodeid'));
                    }
                    if(dataSourceType=="soundimage"){
                        var nodename=record[0].get('nodefullname');
                        var mediaFormView = this.getNewMediaFormView(btn,'look',nodename,record);
                        var form = mediaFormView.down('[itemId=dynamicform]');
                        var initFormFieldState = this.initFormField(form, 'hide', record[0].get('nodeid'));
                        if(!initFormFieldState){//表单控件加载失败
                            return;
                        }
                        form.operate = 'look';
                        var records = record;
                        form.entryids = entryids;
                        form.nodeids = nodeids;
                        form.selectItem = records;
                        form.entryid = records[0].get('entryid');
                        this.initMediaFormData('look', form, records[0].get('entryid'), records[0]);
                        mediaFormView.down("[itemId=batchUploadBtn]").hide();
                        mediaFormView.down('[itemId=save]').hide();
                        if(nodename.indexOf('音频') !== -1){
                            mediaFormView.down('[itemId=mediaDetailViewItem]').expand();
                        }
                        form.up('simpleSearchView').setActiveItem(mediaFormView);
                    }else {
                        var entryid = record[0].get('entryid');
                        var form = this.findFormView(btn).down('dynamicform');
                        form.operate = 'look';
                        form.entryids = entryids;
                        form.nodeids = nodeids;
                        form.entryid = entryids[0];
                        var datasoure = simpleSearchGridView.getStore().getProxy().extraParams.datasoure;  //数据源
                        form.datasoure = datasoure;
                        this.initFormField(form, 'hide', record[0].get('nodeid'));
                        this.initFormData('look', form, entryid);
                    }
                }
            },
            'simpleSearchGridView': {
                rowdblclick: function (view, record) {
                    if (buttonflag === '1') {//利用平台
                        var isOpen = record.get('flagopen');
                        var entryForm = view.up('simpleSearchView').down('EntryFormView');
                        for (var j = 0; j < entryForm.items.items.length; j++) {
                            if (isOpen === '条目开放' && entryForm.items.get(j).xtype !== 'dynamicform') {
                                entryForm.items.get(j).setDisabled(true);
                            } else {
                                entryForm.items.get(j).setDisabled(false);
                            }
                        }
                    }
                    var entryid = record.get('entryid');
                    var form = this.findFormView(view).down('dynamicform');
                    this.initFormField(form, 'hide', record.get('nodeid'));
                    this.initFormData('look',form, entryid);
                },
                afterrender:function (view) {
                    if(titleflag==2){
                        view.setTitle("当前位置：馆库查询");
                        //view.down('[itemId=stborrowmenu]').setText('实体查档申请')
                    }
                }
            },
            'simpleSearchGridView ':{
                eleview: this.activeEleForm
            },
            'mediadtView [itemId=simpleSearchExportId],mediaGridView [itemId=simpleSearchExportId]':{
                click: function (view) {
                    var select;
                    if(view.findParentByType('mediadtView')){  //缩列图
                        simpleSearchGridView = view.findParentByType('mediadtView');
                        select = simpleSearchGridView.acrossSelections ;
                    }else{
                        simpleSearchGridView = view.findParentByType('mediaGridView');
                        select = simpleSearchGridView.getSelectionModel().getSelection();
                        simpleSearchGridView.acrossSelections = select;
                    }
                    if (!select.length) {
                        XD.msg('请选择需要导出的数据');
                        return;
                    } else {
                        var names = [];
                        var keys = [];
                        var columnslist =this.getFormField(select[0].get('nodeid'));
                        for(var i =0;i < columnslist.length;i++){
                            names.push(columnslist[i].fieldname);
                            keys.push(columnslist[i].fieldcode);
                        }
                        // var datasoure = simpleSearchGridView.getStore().getProxy().extraParams.datasoure;  //数据源
                        var datasoure = 'soundimage';
                        Ext.create("SimpleSearch.view.SimpleSearchExportWin",{
                            names:names,
                            keys:keys,
                            datasoure:datasoure==undefined?'management':datasoure
                        }).show();
                    }
                }
            },
            'simpleSearchGridView [itemId=simpleSearchExportId]':{
                click: function (view) {
                    var names = [];
                    var keys = [];
                    simpleSearchGridView = view.findParentByType('simpleSearchGridView');
                    var select = simpleSearchGridView.getSelectionModel();

                    if (!select.hasSelection()) {
                        XD.msg('请选择需要导出的数据');
                        return;
                    }else{
                        var form = this.findFormView(view).down('dynamicform');
                        form.nodeid = select.getSelection()[0].get('nodeid');
                        // var formField = form.getFormField();//根据节点id查询表单字段
                        // for(var i = 0; i<formField.length; i++){
                        //     names.push(formField[i].fieldname);
                        //     keys.push(formField[i].fieldcode);
                        // }
                        var datasoure = simpleSearchGridView.getStore().getProxy().extraParams.datasoure;  //数据源
                        var columnslist =simpleSearchGridView.getColumns();
                        for(var i =2;i < columnslist.length;i++){
                            names.push(columnslist[i].text);
                            keys.push(columnslist[i].dataIndex);
                        }
                        Ext.create("SimpleSearch.view.SimpleSearchExportWin",{
                            names:names,
                            keys:keys,
                            datasoure:datasoure==undefined?'management':datasoure
                        }).show();
                    }
                }
            },
            'simpleSearchExportWin [itemId=simpleSearchExportBtnId]':{
                click: function (view) {
                    var simpleSearchExportWin = view.findParentByType('simpleSearchExportWin');
                    var names = simpleSearchExportWin.names;
                    var keys = simpleSearchExportWin.keys;
                    var exportFileName = simpleSearchExportWin.down('[itemId=simpleSearchExportFileNameId]').getValue();
                    var select;
                    var exportDetails
                    if(buttonflag == 80) {
                        select = simpleSearchGridView.acrossSelections;
                        exportDetails = select;
                    }
                    else {
                        select = simpleSearchGridView.getSelectionModel();
                        exportDetails = select.getSelection();
                    }
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
                    var array = [];
                    for (i = 0; i < exportDetails.length; i++) {
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
                    view.up('simpleSearchExportWin').close();
                }
            },
            'simpleSearchExportWin [itemId=simpleSearchExportCloseBtnId]':{
                click: function (view) {
                    view.up('simpleSearchExportWin').close();
                }
            },
            'mediadtView [itemId=setBookmarks]':{//收藏（或取消收藏）
                click:function(view){
                    var sGrid = view.findParentByType('mediadtView');
                    var record = sGrid.acrossSelections;
                    if(record.length==0){
                        XD.msg('请选择需要操作的数据!');
                        return;
                    }
                    setBookmarks(sGrid,1);
                }
            },
            'simpleSearchGridView [itemId=setBookmarks]':{//收藏（或取消收藏）
                click:function(view){
                    var sGrid = view.findParentByType('simpleSearchGridView');
                    var gridModel = sGrid.getSelectionModel();
                    var record = gridModel.getSelection();
                    if(record.length==0){
                        XD.msg('请选择需要操作的数据!');
                        return;
                    }
                    setBookmarks(sGrid);
                }
            },
            'mediadtView [itemId=viewBookmarks]':{//查看收藏（或返回）
                click:function(btn){
                    var sGrid = btn.findParentByType('mediadtView');
                    if(sGrid.bookmarkStatus==false){
                        sGrid.setTitle('当前位置：个人收藏');
                        sGrid.down('[itemId=setBookmarks]').setText('取消收藏');
                        sGrid.down('[itemId=setBookmarks]').setIconCls('fa fa-star-o');
                        sGrid.down('[itemId=viewBookmarks]').setText('返回');
                        //sGrid.down('[itemId=backopen]').hide();
                        sGrid.down('[itemId=viewBookmarks]').setIconCls('fa fa-undo');
                        // this.resetSearchCondition(sGrid);
                        sGrid.gridPagesSize =sGrid.down('dataview').getStore().pageSize;
                        sGrid.down('dataview').getStore().removeAll();
                        var bookmarksStore=Ext.create('SimpleSearch.store.BookmarksGridStore');//查找到用户收藏的条目
                        sGrid.down('dataview').reconfigure(bookmarksStore);
                        var datasoure = sGrid.findParentByType('simpleSearchView').down('[itemId=datasoure]').getChecked()[0].inputValue;//数据源
                        sGrid.initGrid({searchtype:titleflag,datasoure:datasoure});//判断是否为编研管理系统-馆库查询
                        sGrid.bookmarkStatus=true;
                    }else{
                        sGrid.setTitle('当前位置：简单检索');
                        sGrid.down('[itemId=setBookmarks]').setText('收藏');
                        sGrid.down('[itemId=setBookmarks]').setIconCls('fa fa-star');
                        sGrid.down('[itemId=viewBookmarks]').setText('查看收藏');
                        sGrid.down('[itemId=viewBookmarks]').setIconCls('fa fa-heart');
                        //sGrid.down('[itemId=backopen]').show();
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
                            //sGrid.getStore().removeAll();//将表格信息全部移除
                            this.searchInfo(searchfield);
                        }
                    }
                    var conditionField = this.findView(btn).down('[itemId=simpleSearchSearchComboId]');
                    conditionField.getStore().reload();
                }
            },
            'simpleSearchGridView [itemId=viewBookmarks]':{//查看收藏（或返回）
                click:function(btn){
                    var sGrid = btn.findParentByType('simpleSearchGridView');
                    if(sGrid.bookmarkStatus==false){
                        sGrid.setTitle('当前位置：个人收藏');
                        sGrid.down('[itemId=setBookmarks]').setText('取消收藏');
                        sGrid.down('[itemId=setBookmarks]').setIconCls('fa fa-star-o');
                        sGrid.down('[itemId=viewBookmarks]').setText('返回');
                        sGrid.down('[itemId=viewBookmarks]').setIconCls('fa fa-undo');
                        // this.resetSearchCondition(sGrid);
                        sGrid.gridPagesSize =sGrid.getStore().pageSize;
                        sGrid.getStore().removeAll();
                        var bookmarksStore=Ext.create('SimpleSearch.store.BookmarksGridStore');//查找到用户收藏的条目
                        sGrid.reconfigure(bookmarksStore);
                        var datasoure = sGrid.findParentByType('simpleSearchView').down('[itemId=datasoure]').getChecked()[0].inputValue;//数据源
                        sGrid.initGrid({searchtype:titleflag,datasoure:datasoure});//判断是否为编研管理系统-馆库查询
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
                        }
                        else {
                            sGrid.getStore().removeAll();//将表格信息全部移除
                            //利用平台||馆库查询
                            if(buttonflag=='1'||titleflag==2){
                                var simpleSearchOpenGridStore=Ext.create('SimpleSearch.store.SimpleSearchOpenGridStore');
                                // simpleSearchGridStore.proxy.url = '/simpleSearch/findBySearchPlatform';
                                sGrid.reconfigure(simpleSearchOpenGridStore);
                                sGrid.initGrid();
                            }
                            //管理平台
                            else{
                                var simpleSearchGridStore=Ext.create('SimpleSearch.store.SimpleSearchGridStore');
                                //simpleSearchGridStore.proxy.url = '/simpleSearch/findBySearchPlatform';
                                sGrid.reconfigure(simpleSearchGridStore);
                                sGrid.initGrid();
                            }
                        }
                    }
                    var conditionField = this.findView(btn).down('[itemId=simpleSearchSearchComboId]');
                    conditionField.getStore().reload();
                }
            },

            ///////////////////////实体查档单////////////////////////////
            'mediadtView [itemId=stAdd]':{//添加实体借阅
                click: function (view) {
                    var sGrid = view.findParentByType('mediadtView');
                    var record = sGrid.acrossSelections;
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
                            borrowType:'查档'
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
            'mediadtView [itemId=lookAdd]':{//处理实体借阅
                click: function () {
                    var boxwin = Ext.create('Ext.window.Window', {
                        height: '100%',
                        width: '100%',
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        modal: true,
                        closeToolText:'关闭',
                        title: '借阅申请',
                        closeAction: 'hide',
                        layout: 'fit',
                        items: [{xtype: 'lookAddMxGridView'}]
                    });
                    isFlag="1";
                    boxwin.getComponent('lookAddMxGridViewId').initGrid({'borrowType':'查档','isFlag':isFlag});
                    boxwin.show();
                    window.boxwin = boxwin;
                }
            },
            'simpleSearchGridView [itemId=stAdd]':{//添加实体查档
                click: function (view) {
                    var sGrid = view.findParentByType('simpleSearchGridView');
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
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'simpleSearchGridView [itemId=lookAdd]':{//处理实体查档
                click: function () {
                    var boxwin = Ext.create('Ext.window.Window', {
                        height: '100%',
                        width: '100%',
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        modal: true,
                        closeToolText:'关闭',
                        title: '实体查档申请',
                        closeAction: 'hide',
                        layout: 'fit',
                        items: [{xtype: 'lookAddMxGridView',type:2}]
                    });
                    boxwin.getComponent('lookAddMxGridViewId').initGrid({'borrowType':'实体查档'});
                    boxwin.show();
                    window.boxwin = boxwin;
                }
            },

            'lookAddFormItemView button[itemId=lookAddFormSubmit]': { //提交实体查档单
                click: function (btn) {
                    var form = btn.findParentByType('lookAddFormItemView');
                    var lookAddSqWin = form.findParentByType('lookAddSqView');
                    var borrowts = form.getComponent('borrowtsId').getValue();
                    var spman = form.getComponent('spmanId').getValue();
                    var sendMsg = form.down("[itemId=sendmsgId]").getValue();

                    if(!form.isValid()) {
                        XD.msg('有必填项未填写，请处理后再提交');
                        return;
                    }
                    if (borrowts == '' || borrowts == null||String(borrowts).indexOf(".")>-1||isNaN(borrowts)||parseInt(borrowts)<1 ) {XD.msg('查档天数不合法');return;}
                    if (spman==null) {XD.msg('受理人不能为空');return;}

                    if (isNaN(borrowts) || parseInt(borrowts) <= 0) {
                        XD.msg('非法查档天数输入值');
                        return;
                    }
                    if(form.addType=="noEntry"){
                        Ext.MessageBox.wait('正在提交数据请稍后...','提示');
                        form.submit({
                            waitTitle: '',// 标题
                            url: '/electron/stLookAddForm',
                            params:{
                                eleids:window.wmedia,
                                sendMsg:sendMsg
                            },
                            method: 'POST',
                            success: function (form, action) {
                                var respText = Ext.decode(action.response.responseText);
                                Ext.Msg.hide();
                                XD.msg(respText.msg);
                                // window.wlookAddMxGrid.getStore().loadPage(1);
                                if(lookAddSqWin) {
                                    lookAddSqWin.close();
                                }else{
                                    btn.findParentByType('window').close();
                                }
                            },
                            failure: function () {
                                Ext.Msg.hide();
                                XD.msg('操作失败');
                            }
                        });
                    }else{
                        form.submit({
                            waitTitle: '',// 标题
                            url: '/electron/stAddFormBill',
                            params:{
                                eleids:window.wmedia,
                                sendMsg:sendMsg
                            },
                            method: 'POST',
                            success: function (form, action) {
                                var respText = Ext.decode(action.response.responseText);
                                XD.msg(respText.msg);
                                window.wlookAddMxGrid.getStore().loadPage(1);
                                if(lookAddSqWin) {
                                    lookAddSqWin.close();
                                }else{
                                    btn.findParentByType('window').close();
                                }
                            },
                            failure: function () {
                                XD.msg('操作失败');
                            }
                        });
                    }
                }
            },

            'lookAddFormItemView button[itemId=lookAddFormClose]': { //提交实体查档单界面---关闭
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
                    if( btn.findParentByType("lookAddSqView")){
                        btn.findParentByType("lookAddSqView").close();
                    }else {
                        btn.findParentByType("window").close();
                    }



                }
            },
            //////////////////////////////end///////////////////////////////////



            /////////////////////// //电子查档单 //////////////////////////
            'mediadtView [itemId=electronAdd],mediaGridView [itemId=electronAdd]':{//声像-添加电子
                click: function (view) {
                    var record;
                    if(view.findParentByType('mediadtView')){  //缩列图
                        var sGrid = view.findParentByType('mediadtView');
                        record = sGrid.acrossSelections ;
                    }else{
                        var mediaGridView = view.findParentByType('mediaGridView');
                        record = mediaGridView.getSelectionModel().getSelection();
                    }
                    if(record.length==0){
                        XD.msg('请选择需要操作的数据!');
                        return;
                    }

                    var dataids = [];
                    for (var i = 0; i < record.length; i++) {
                        dataids.push(record[i].get('entryid'));
                        if(buttonflag=='80'){
                            continue;
                        }
                        if (record[i].get('eleid') == undefined) {
                            XD.msg('不能含有电子文件为空的条目');
                            return;
                        }
                    }
                    Ext.Ajax.request({
                        params: {
                            dataids: dataids,
                            borrowType:'查档'
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

            'mediadtView [itemId=dealElectronAdd],mediaGridView [itemId=dealElectronAdd]':{//声像-处理电子
                click: function (view) {
                    var boxwin = Ext.create('Ext.window.Window', {
                        height: '100%',
                        width: '100%',
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        modal: true,
                        closeToolText:'关闭',
                        title: '声像借阅申请',
                        closeAction: 'hide',
                        layout: 'fit',
                        items: [{xtype: 'lookAddMxGridView',type:1}]
                    });
                    var lookAddMxGrid = boxwin.getComponent('lookAddMxGridViewId');
                    isFlag="1";
                    lookAddMxGrid.initGrid({'borrowType':'查档','isFlag':isFlag});
                    boxwin.show();
                    window.boxwin = boxwin;
                }
            },
            'simpleSearchGridView [itemId=electronAdd]':{//添加查档
                click: function (view) {
                    var sGrid = view.findParentByType('simpleSearchGridView');
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
                            borrowType:'查档'
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

            'simpleSearchGridView [itemId=dealElectronAdd]':{ //处理查档
                click: function (view) {
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
                        items: [{xtype: 'lookAddMxGridView',type:1}]
                    });
                    isFlag="";
                    if(buttonflag=="80"||dataSourceType=="soundimage"){
                        isFlag="1";
                    }
                    var lookAddMxGrid = boxwin.getComponent('lookAddMxGridViewId');
                    lookAddMxGrid.initGrid({'borrowType':'查档','isFlag':isFlag});
                    boxwin.show();
                    window.boxwin = boxwin;
                }
            },

            'electronFormGridView button[itemId=setType]':{
                click:function (view) {
                    var electronFormGridView = view.findParentByType('electronFormGridView');
                    var select = electronFormGridView.getSelectionModel().getSelection();
                    if(select.length==0){
                        XD.msg('请至少选择一条数据');
                        return;
                    }
                    var entryids = [];
                    var flag = false;
                    for (var i = 0; i < select.length; i++) {
                        entryids.push(select[i].get('entryid'));
                        //判断条目是否含有电子文件
                        if(select[i].get('eleid')!=''&&select[i].get('eleid')!=undefined){
                            flag = true;
                        }
                    }
                    var setBorrowTypeView = Ext.create("SimpleSearch.view.SetBorrowTypeView");
                    if(flag){
                        setBorrowTypeView.down('[itemId=electronCheckId]').setValue(true);
                    }
                    setBorrowTypeView.select = select;
                    setBorrowTypeView.entryids = entryids;
                    setBorrowTypeView.electronFormGridView = electronFormGridView;
                    setBorrowTypeView.show();
                }
            },

            'setBorrowTypeView button[itemId=stTypeSubmit]':{   //设置查档类型 提交
                click:function (view) {
                    var setBorrowTypeView = view.findParentByType('setBorrowTypeView');
                    var electronCheck = setBorrowTypeView.down('[itemId=electronCheckId]').getValue();
                    var stCheck = setBorrowTypeView.down('[itemId=stCheckId]').getValue();
                    var settype;
                    if(!stCheck&&!electronCheck){
                        XD.msg('请选择查档类型');
                        return;
                    }
                    if(electronCheck&&stCheck){
                        settype = '电子、实体查档';
                    }else if(electronCheck){
                        settype = '电子查档';
                    }else {
                        settype = '实体查档';
                    }
                    Ext.Ajax.request({
                        params: {
                            entryids: setBorrowTypeView.entryids,
                            borrowType:setBorrowType,
                            settype:settype,
                            isFlag:isFlag
                        },
                        url: '/electron/setBorrowType',
                        method: 'POST',
                        sync: true,
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            XD.msg(respText.msg);
                            setBorrowTypeView.close();
                            setBorrowTypeView.electronFormGridView.getStore().proxy.extraParams.loadtype = "hasload";  //不是首次加载
                            setBorrowTypeView.electronFormGridView.getStore().reload();
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'setBorrowTypeView button[itemId=stTypeClose]':{  //设置查档类型 取消
                click:function (view) {
                    view.findParentByType('setBorrowTypeView').close();
                }
            },

            'electronFormItemView button[itemId=electronFormSubmit]': {//查档单--提交
                click: function (btn) {
                    var form = btn.findParentByType('electronFormItemView');
                    var borrowts = form.getComponent('borrowtsId').getValue();
                    var spman = form.getComponent('spmanId').getValue();
                    var sendMsg = form.down("[itemId=sendmsgId]").getValue();
                    var certificatetype = form.down('[itemId=certificatetypeId]').getValue();
                    var certificatenumber = form.down('[itemId=certificatenumberId]').getValue();
                    if(!form.isValid()) {
                        XD.msg('有必填项未填写，请处理后再提交');
                        return;
                    }
                    if (borrowts == '' || borrowts == null||String(borrowts).indexOf(".")>-1||isNaN(borrowts)||parseInt(borrowts)<1 ) {XD.msg('查档天数不合法');return;}
                    if(certificatetype=='身份证'&&(systemType!='0'||type=='selfQuery')){
                        if(!certificatenumber.match(/^[0-9]{15}$/) && !certificatenumber.match(/^[0-9]{17}[0-9xX]$/)){
                            XD.msg('请输入正确的身份证号码');
                            return;
                        }
                    }
                    var electronAddWin = btn.findParentByType('electronAddView');
                    var url;
                    if(form.submitType=="hasEntry"){ //有条目提交
                        var electronFormGridView = electronAddWin.down('electronFormGridView');
                        var store = electronFormGridView.getStore();
                        var flag = false;
                        for(var i=0;i<store.getCount();i++){
                            if(store.getAt(i).get('flagopen')==''||store.getAt(i).get('flagopen')==undefined){
                                flag = true;
                                break;
                            }
                        }
                        if(flag){
                            XD.msg('存在查档条目未设置查档类型');
                            return;
                        }
                        url = '/electron/electronBill';
                    }else{ //无条目提交
                        url = '/electron/electronAddForm';
                    }
                    Ext.MessageBox.wait('正在提交数据请稍后...','提示');
                    form.submit({
                        url: url,
                        method: 'POST',
                        params: {
                            eleids:window.wmedia,
                            sendMsg:sendMsg,
                            submittype:systemType=='0'&&type!='selfQuery'? 'true':'false',
                            dataSourceType:dataSourceType
                        },
                        success: function (form, action) {
                            var respText = Ext.decode(action.response.responseText);
                            Ext.Msg.hide();
                            XD.msg(respText.msg);
                            if(!respText.success){
                                return;
                            }
                            if(electronAddWin) {
                                window.wlookAddMxGrid.getStore().loadPage(1);
                                electronAddWin.close();
                            }else{
                                btn.findParentByType('window').close();
                            }
                        },
                        failure: function () {
                            Ext.Msg.hide();
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'mediadtView [itemId=stAddDoc],mediaGridView [itemId=stAddDoc]': {//提交实体借阅按钮
                click: function () {
                    var borrowRoquest = Ext.create('Ext.window.Window', {
                        height: '100%',
                        width: '100%',
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        modal: true,
                        closeToolText:'关闭',
                        title: '查档申请',
                        closeAction: 'hide',
                        layout: 'fit',
                        items: [{xtype: 'electronFormView'}]
                    });
                    window.wmedia = [];
                    window.borrowStadd = borrowRoquest;
                    var form = borrowRoquest.down('[itemId=electronFormItemViewId]');
                    var approveOrganStore = form.down('[itemId=approveOrgan]').getStore();
                    approveOrganStore.proxy.extraParams.type = "submit"; //申请时获取审批单位
                    approveOrganStore.proxy.extraParams.taskid = "";
                    approveOrganStore.proxy.extraParams.worktext = "查档审批";
                    approveOrganStore.proxy.extraParams.nodeid = "";
                    approveOrganStore.load(); //加载数据
                    form.submitType = "noEntry";  //无条目提交标志
                    form.load({
                        url: '/electron/getBorrowDocByUser',
                        success: function (form, action) {
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                    borrowRoquest.show();
                }
            },
            'stLookAddFormView [itemId=stlookAddFormSubmit]': {
                click: function (btn) {
                    var form = btn.findParentByType('stLookAddFormView');
                    var borrowts = form.getComponent('borrowtsId').getValue();
                    if(!form.isValid()){
                        XD.msg('输入数据不合法');
                        return;
                    }else{
                        if (borrowts == '' || borrowts == null||String(borrowts).indexOf(".")>-1||isNaN(borrowts)||parseInt(borrowts)<1 ) {
                            XD.msg('借阅天数不合法');return;
                        }
                    }
                    form.submit({
                        waitTitle: '',// 标题
                        url: '/electron/stLookAddForm',
                        method: 'POST',
                        params:{
                            eleids:window.wmedia,
                            dataSourceType:dataSourceType
                        },
                        success: function () {
                            XD.msg('提交成功');
                            window.borrowStadd.close();
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            }, 'stLookAddFormView [itemId=stlookAddFormClose]': {//实体查档单界面----关闭
                click: function (view) {
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
                    view.findParentByType("window").close();
                }
            },
            'electronAddFormView button[itemId=electronAddClose]': {//电子查档单界面----关闭
                click: function (view) {
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
                    // if( view.findParentByType("electronAddView")){
                    //     view.findParentByType("electronAddView").close();
                    // }else {
                    view.findParentByType("window").close();
                    //}
                }
            },
            'mediadtView [itemId=electronAddDoc]': {//提交电子借阅按钮
                click: function () {
                    var borrowRoquest = Ext.create('Ext.window.Window', {
                        height: '100%',
                        width: '100%',
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        modal: true,
                        closeToolText:'关闭',
                        title: '电子借阅申请',
                        closeAction: 'hide',
                        layout: 'fit',
                        items: [{xtype: 'electronAddFormView'}]
                    });
                    window.wmedia = [];
                    window.borrowElectronadd = borrowRoquest;
                    var form = borrowRoquest.down('[itemId=electronAddFormViewId]');
                    // var approveOrganStore = form.down('[itemId=approveOrgan]').getStore();
                    // approveOrganStore.proxy.extraParams.type = "submit"; //申请时获取审批单位
                    // approveOrganStore.proxy.extraParams.taskid = "";
                    // approveOrganStore.proxy.extraParams.worktext = "电子借阅审批";
                    // approveOrganStore.proxy.extraParams.nodeid = "";
                    // approveOrganStore.load(); //加载数据
                    form.load({
                        url: '/electron/getBorrowDocByUser',
                        success: function (form, action) {
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                    borrowRoquest.show();
                }
            },
            ////////////////////////////////end///////////////////////////////////////


            ////////////////////////////////电子打印//////////////////////////////

            'simpleSearchGridView [itemId=addApplyPrint]':{//添加打印申请
                click: function (view) {
                    var sGrid = view.findParentByType('simpleSearchGridView');
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

            //处理打印申请
            'simpleSearchGridView [itemId=dealApplyPrint]':{
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
                        items: [{xtype: 'lookAddMxGridView',type:3}]
                    });
                    var lookAddMxGrid = boxwin.getComponent('lookAddMxGridViewId');
                    lookAddMxGrid.down("[itemId=stAddSq]").setText('打印申请');
                    lookAddMxGrid.initGrid({'borrowType':'电子打印'});
                    lookAddMxGrid.down('[itemId=setPrint]').show();
                    boxwin.show();
                    window.boxwin = boxwin;
                }
            },

            //提交电子打印申请单
            'applyPrintFormView button[itemId=printSetFormSubmit]': {
                click: function (btn) {
                    var form = btn.findParentByType('applyPrintFormView');
                    var borrowts = form.getComponent('borrowtsId').getValue();
                    var spman = form.getComponent('spmanId').getValue();
                    var sendMsg = form.down("[itemId=sendmsgId]").getValue();

                    if(!form.isValid()) {
                        XD.msg('有必填项未填写，请处理后再提交');
                        return;
                    }

                    if (borrowts == '' || borrowts == null||String(borrowts).indexOf(".")>-1||isNaN(borrowts)||parseInt(borrowts)<1 ) {XD.msg('查档天数不合法');return;}
                    if (spman==null) {XD.msg('受理人不能为空');return;}

                    Ext.MessageBox.wait('正在提交数据请稍后...','提示');
                    form.submit({
                        url: '/electron/electronPrintSubmit',
                        method: 'POST',
                        params: {
                            eleids:window.wmedia,
                            sendMsg:sendMsg
                        },
                        success: function (form, action) {
                            var respText = Ext.decode(action.response.responseText);
                            Ext.Msg.hide();
                            XD.msg(respText.msg);
                            window.wlookAddMxGrid.getStore().loadPage(1);
                            btn.findParentByType('window').close();
                        },
                        failure: function () {
                            Ext.Msg.hide();
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            //提交电子打印申请单---界面关闭
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
            /////////////////////////////end//////////////////////////////////////////


            'lookAddMxGridView button[itemId=stAddSq]': { //查档申请
                click: function (view) {
                    var select = view.findParentByType('lookAddMxGridView').getSelectionModel();
                    window.wlookAddMxGrid = view.findParentByType('lookAddMxGridView');
                    if (select.getCount() < 1) {
                        XD.msg('至少选择一条数据');
                        return;
                    }
                    var dataids = [];
                    var organids = [];
                    var flag = false;
                    for (var i = 0; i < select.getSelection().length; i++) {
                        dataids.push(select.getSelection()[i].get('entryid'));
                        organids.push(select.getSelection()[i].data.tdn.organid);
                        //判断条目是否存在没有电子文件，并且库存份数为0
                        if((select.getSelection()[i].get('eleid')==''||select.getSelection()[i].get('eleid')==undefined)&&select.getSelection()[i].get('kccount')<1){
                            flag = true;
                        }
                    }
                    window.wmedia = [];
                    // 利用平台在进行【查档申请】时，判断只能是同一个单位的记录，才能一起提交。另一个单位的数据，需要另起申请单据。给予相关的提示。
                    Ext.Ajax.request({
                        params: {
                            organids: organids,
                            flagtype:buttonflag
                        },
                        url: '/electron/isSameOrgan',
                        method: 'POST',
                        async: false,
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            if (respText.success == true) {
                                if(view.text=="查档申请"){//查档
                                    if(flag&&isFlag!="1"){
                                        XD.msg('存在条目无电子文件，并且库存份数为0');
                                        return;
                                    }
                                    var win = Ext.create('SimpleSearch.view.ElectronAddView');
                                    setBorrowType=window.wlookAddMxGrid.dataParams.borrowType;//设置查档类型
                                    win.show();
                                    var fromGrid = win.getComponent('electronFormGridViewId');
                                    fromGrid.initGrid({dataids: dataids,borrowtype:'查档',loadtype:'firstload',"isFlag":isFlag});
                                    if(dataSourceType=="soundimage") {//声像数据异常查档类型设置
                                        fromGrid.down('[itemId=setType]').hide();
                                    }
                                    var formView = win.down('[itemId=electronFormItemViewId]');
                                    var approveOrganStore = formView.down('[itemId=approveOrgan]').getStore();
                                    approveOrganStore.proxy.extraParams.type = "submit"; //申请时获取审批单位
                                    approveOrganStore.proxy.extraParams.taskid = "";
                                    approveOrganStore.proxy.extraParams.worktext = "查档审批";
                                    approveOrganStore.proxy.extraParams.nodeid = "";
                                    approveOrganStore.load(); //加载数据
                                    formView.submitType = "hasEntry";   //有条目提交标志
                                    formView.entryids = dataids;
                                    if(buttonflag === '1'){
                                        formView.organids=organids;
                                    }
                                    formView.load({
                                        url: '/electron/getBorrowDocByIds',
                                        method: 'POST',
                                        params: {
                                            dataids: dataids,
                                            isFlag:isFlag
                                        },
                                        success: function (form, action) {
                                        },
                                        failure: function () {
                                            XD.msg('操作失败');
                                        }
                                    });
                                }else if(window.wlookAddMxGrid.type==2){//实体查档
                                    var sqWin = Ext.create('SimpleSearch.view.LookAddSqView');
                                    var form = sqWin.down('[itemId=lookAddFormItemViewId]');
                                    if(buttonflag === '1'){
                                        form.organids=organids;
                                    }
                                    form.load({
                                        url: '/electron/getBorrowDocByIds',
                                        method: 'POST',
                                        params: {
                                            dataids: dataids,
                                            isFlag:isFlag
                                        },
                                        success: function (form, action) {
                                        },
                                        failure: function () {
                                            XD.msg('操作失败');
                                        }
                                    });
                                    form.addType = "hasEntry";
                                    sqWin.show();
                                    var fromGrid = sqWin.getComponent('lookAddFormGridViewId');
                                    fromGrid.initGrid({dataids: dataids});
                                }else if(window.wlookAddMxGrid.type==3){//打印申请
                                    var win = Ext.create('SimpleSearch.view.ApplyPrintAddView');
                                    var fromGrid = win.getComponent('applyPrintGridViewId');
                                    fromGrid.initGrid({dataids: dataids});
                                    var form = win.down('[itemId=applyPrintFormViewId]');
                                    var approveOrganStore = form.down('[itemId=approveOrgan]').getStore();
                                    approveOrganStore.proxy.extraParams.type = "submit"; //申请时获取审批单位
                                    approveOrganStore.proxy.extraParams.taskid = "";
                                    approveOrganStore.proxy.extraParams.worktext = "电子打印审批";
                                    approveOrganStore.proxy.extraParams.nodeid = "";
                                    approveOrganStore.load(); //加载数据
                                    window.applyPrintFormView = form;
                                    if(buttonflag === '1'){
                                        Ext.Ajax.request({
                                            type: 'ajax',
                                            url: '/electron/getUnitOrganAll',
                                            async:true,
                                            params: {
                                                type: 'submit',
                                                worktext: '电子打印审批',
                                                page: 1,
                                                start: 0,
                                                limit: 25
                                            },
                                            success:function(res){
                                                var obj = JSON.parse(res.responseText);
                                                form.organids=obj[0].organid;
                                            }
                                        })
                                        // form.organids=organids;
                                    }
                                    win.show();
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
                            }else{
                                XD.msg('所选数据中包含其它单位数据，请重新选择！');
                            }
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'lookAddMxGridView button[itemId=remove]': { //移除
                click: function (view) {
                    var dealgrid = view.findParentByType('lookAddMxGridView');
                    window.wlookAddMxGrid = dealgrid;
                    var borrowType;
                    if(dealgrid.type==1){
                        borrowType = '查档';
                    }
                    else if(dealgrid.type == 2){
                        borrowType = '实体查档';
                    }else{
                        borrowType = '电子打印';
                    }
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


            //设置打印范围
            'lookAddMxGridView button[itemId=setPrint]': {
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
                    var win = Ext.create('SimpleSearch.view.ApplySetPrintView');
                    var form = win.down('applyPrintSetEleView');
                    form.entryid = dataids[0];
                    form.entryids = dataids;
                    win.show();
                    this.getApplySetPrint(form);

                }
            },

            //查档单据界面----关闭
            'lookAddMxGridView button[itemId=close]': {
                click: function () {
                    window.boxwin.close();
                }
            },


            //提交电子查档单
            'simpleSearchGridView [itemId=submiteleborrow]':{
                click: function (btn) {
                    var borrowRoquest = Ext.create('Ext.window.Window', {
                        height: '100%',
                        width: '100%',
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        modal: true,
                        closeToolText:'关闭',
                        title: '电子查档申请',
                        closeAction: 'hide',
                        layout: 'fit',
                        closable:false,
                        items: [{xtype: 'electronFormView'}]
                    });
                    window.wmedia = [];
                    window.borrowElectronadd = borrowRoquest;
                    var form = borrowRoquest.down('[itemId=electronFormItemViewId]');
                    var approveOrganStore = form.down('[itemId=approveOrgan]').getStore();
                    approveOrganStore.proxy.extraParams.type = "submit"; //申请时获取审批单位
                    approveOrganStore.proxy.extraParams.taskid = "";
                    approveOrganStore.proxy.extraParams.worktext = "查档审批";
                    approveOrganStore.proxy.extraParams.nodeid = "";
                    approveOrganStore.load(); //加载数据
                    form.submitType = "noEntry";  //无条目提交标志
                    form.load({
                        url: '/electron/getBorrowDocByUser',
                        success: function (form, action) {
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                    borrowRoquest.show();
                }
            },

            'electronFormItemView button[itemId=electronFormClose]': { //查档单界面---关闭
                click: function (btn) {
                    btn.findParentByType("window").close();
                }
            },

            //提交实体查档单
            'simpleSearchGridView [itemId = submitstborrow]': {
                click: function () {
                    var borrowRoquest = Ext.create('Ext.window.Window', {
                        height: '100%',
                        width: '100%',
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        modal: true,
                        closeToolText:'关闭',
                        title: '实体查档申请',
                        closeAction: 'hide',
                        layout: 'fit',
                        closable:false,
                        items: [{xtype: 'lookAddFormView',

                        }]
                    });
                    window.wmedia = [];
                    window.borrowStadd = borrowRoquest;
                    var form = borrowRoquest.down('[itemId=lookAddFormItemViewId]');
                    form.addType = "noEntry";
                    form.load({
                        url: '/electron/getBorrowDocByUser',
                        success: function (form, action) {
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                    borrowRoquest.show();
                }
            },


            'EntryFormView [itemId=preBtn]':{
                click:this.preHandler
            },
            'EntryFormView [itemId=nextBtn]':{
                click:this.nextHandler
            },
            'simpleSearchView [itemId=back]': {//返回
                click: function(btn){
                    this.activeGrid(btn, false);
                }
            },
            'mediadtView [itemId=print]':{//进入打印页面
                click: function (btn) {
                    printWin = this.chooseReport(btn,printWin,1);
                }
            },
            'mediaGridView [itemId=print]':{//进入打印页面
                click: function (btn) {
                    printWin = this.chooseReport(btn,printWin,2);
                }
            },
            'simpleSearchGridView [itemId=print]':{//进入打印页面
                click: function (btn) {
                    printWin = this.chooseReport(btn,printWin);
                }
            },

            'electronFormItemView button[itemId=electronUpId]': {
                click: function (view) {
                    window.leadIn = Ext.create("Ext.window.Window", {
                        width: '100%',
                        height: '100%',
                        title: '添加附件',
                        modal: true,
                        header: false,
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        closeToolText: '关闭',
                        layout: 'fit',
                        items: [{xtype: 'electronicPro'}]
                    });
                    window.wform = view.findParentByType('electronFormItemView');
                    window.ztid = 'undefined';
                    window.leadIn.down('electronicPro').initData(window.ztid);
                    window.leadIn.show();
                }
            },

            'lookAddFormItemView button[itemId=stElectronUpId]': {
                click: function (view) {
                    window.leadIn = Ext.create("Ext.window.Window", {
                        width: '100%',
                        height: '100%',
                        title: '添加附件',
                        modal: true,
                        header: false,
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        closeToolText: '关闭',
                        layout: 'fit',
                        items: [{xtype: 'electronicPro'}]
                    });

                    window.wform = view.findParentByType('lookAddFormItemView');
                    window.ztid = 'undefined';
                    window.leadIn.down('electronicPro').initData(window.ztid);
                    window.leadIn.show();
                }
            },

            'applyPrintSetEleView button[itemId=setScope]':{
                click:function (view) {
                    var applyPrintSetEleView = view.findParentByType('applyPrintSetEleView');
                    var elegrid  = applyPrintSetEleView.down('[itemId=eleGrid]');
                    var select = elegrid.getSelectionModel().getSelection();
                    if(select.length<1){
                        XD.msg('请至少选择一条数据');
                        return;
                    }
                    var applyprintids = [];
                    for(var i=0;i<select.length;i++){
                        applyprintids.push(select[i].get('id'));
                    }
                    var setScopeView =  Ext.create("Ext.window.Window",{
                        width:400,
                        height:250,
                        modal:true,
                        closeToolText:'关闭',
                        header: false,
                        layout:'fit',
                        items:[{
                            xtype: 'applySetPrintScopeView'
                        }]
                    });
                    setScopeView.down('applySetPrintScopeView').applyprintids = applyprintids;
                    setScopeView.down('applySetPrintScopeView').elegrid = elegrid;
                    setScopeView.down('[name=copies]').setValue('1');
                    setScopeView.show();
                }
            },

            'applySetPrintScopeView button[itemId=stPrintSubmit]':{
                click:function (view) {
                    var applySetPrintScopeView = view.findParentByType('applySetPrintScopeView');
                    var applyprintids = applySetPrintScopeView.applyprintids;
                    applySetPrintScopeView.submit({
                        params: {
                            applyprintids: applyprintids
                        },
                        url: '/simpleSearch/setApplySetPrint',
                        method: 'POST',
                        success: function (resp) {
                            XD.msg('提交成功');
                            applySetPrintScopeView.elegrid.getStore().reload();
                            var form = window.applyPrintFormView;
                            form.load({
                                url: '/electron/getPrintBorrowDocByIds',
                                params: {
                                    dataids: form.getForm().findField('id').getValue()
                                },
                                success: function (form, action) {
                                },
                                failure: function () {
                                    XD.msg('操作失败');
                                }
                            });
                            view.findParentByType('window').close();
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'applyPrintSetEleView button[itemId=cleanScope]':{
                click:function (view) {
                    var applyPrintSetEleView = view.findParentByType('applyPrintSetEleView');
                    var elegrid  = applyPrintSetEleView.down('[itemId=eleGrid]');
                    var select = elegrid.getSelectionModel().getSelection();
                    if(select.length<1){
                        XD.msg('请至少选择一条数据');
                        return;
                    }
                    var applyprintids = [];
                    for(var i=0;i<select.length;i++){
                        applyprintids.push(select[i].get('id'));
                    }
                    Ext.Ajax.request({
                        params: {
                            applyprintids: applyprintids
                        },
                        url: '/simpleSearch/cleanScope',
                        method: 'POST',
                        sync: true,
                        success: function (resp) {
                            elegrid.getStore().reload();
                            var form = window.applyPrintFormView;
                            form.load({
                                url: '/electron/getPrintBorrowDocByIds',
                                params: {
                                    dataids: form.getForm().findField('id').getValue()
                                },
                                success: function (form, action) {
                                },
                                failure: function () {
                                    XD.msg('操作失败');
                                }
                            });
                            XD.msg('取消打印成功');
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'applySetPrintScopeView button[itemId=stPrintClose]':{
                click:function (view) {
                    view.findParentByType('window').close();
                }
            },

            'applySetPrintView button[itemId=back]':{
                click:function (view) {
                    view.findParentByType('window').close();
                }
            },

            'applySetPrintView [itemId=preBtn]':{
                click:this.preApplySetPrint
            },

            'applySetPrintView [itemId=nextBtn]':{
                click:this.nextApplySetPrint
            },



            'applyPrintGridView button[itemId=editPrint]': {
                click: function (view) {
                    var select = view.findParentByType('applyPrintGridView').getSelectionModel();
                    if (select.getCount() < 1) {
                        XD.msg('至少选择一条数据');
                        return;
                    }

                    var dataids = [];
                    for (var i = 0; i < select.getSelection().length; i++) {
                        dataids.push(select.getSelection()[i].get('entryid'));
                    }
                    var win = Ext.create('SimpleSearch.view.ApplySetPrintView');
                    var form = win.down('applyPrintSetEleView');
                    form.entryid = dataids[0];
                    form.entryids = dataids;
                    win.show();
                    this.getApplySetPrint(form);
                }
            },

            'applyPrintFormView button[itemId=printElectronUpId]': {
                click: function (view) {
                    window.leadIn = Ext.create("Ext.window.Window", {
                        width: '100%',
                        height: '100%',
                        title: '添加附件',
                        modal: true,
                        header: false,
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        closeToolText: '关闭',
                        layout: 'fit',
                        items: [{xtype: 'electronicPro'}]
                    });
                    window.wform = view.findParentByType('applyPrintFormView');
                    window.ztid = 'undefined';
                    window.leadIn.down('electronicPro').initData(window.ztid);
                    window.leadIn.show();
                }
            },
            'simpleSearchGridView [itemId=wkfprint]':{
                click:function(btn) {
                    var ids = [];
                    var params = {};
                    if(reportServer == 'UReport') {
                        params['docid'] = ids.join(",");
                        XD.UReportPrint(null, '利用未开放档案申请表', params);
                    }
                    else if(reportServer == 'FReport') {
                        XD.FRprint(null, '利用未开放档案申请表', ids.length > 0 ? "'id':'" + ids.join(",") + "'" : '');
                    }

                }
            }
        });
    },
    //获取表单字段
    getFormField:function (nodeid) {
        var formField;
        Ext.Ajax.request({
            url: '/template/sxform',
            async:false,
            scope:this,
            params:{
                nodeid:nodeid
            },
            success: function (response) {
                formField = Ext.decode(response.responseText);
            }
        });
        return formField;
    },
    //打开报表显示列表
    chooseReport:function(btn,printWin,ismedia){
        var grid = btn.up('simpleSearchGridView');
        var ids = [];
        var records;
        if(ismedia == 1) {
            grid = btn.up('mediadtView');
            records = grid.acrossSelections;
        }else if(ismedia == 2){
            grid = btn.up('mediaGridView');
            records = grid.getSelectionModel().getSelection();
        } else {
            records = grid.getSelectionModel().getSelection()
        }
        Ext.each(records, function () {
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
        reportGrid.initGrid({nodeid:reportGrid.nodeid + ',私有报表'});
        reportGridWin.show();
        return reportGridWin;
    },

    //获取简单检索应用视图
    findView: function (btn) {
        return btn.findParentByType('simpleSearchView');
    },

    //获取表单界面视图
    findFormView: function (btn) {
        return this.findView(btn).down('EntryFormView');
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
            var grid = gridview.down('simpleSearchGridView');
            grid.notResetInitGrid();
        }
    },

    //切换到表单界面视图
    activeForm: function (btn) {
        var view = this.findView(btn);
        var formview = this.findFormView(btn);
        view.setActiveItem(formview);
        formview.items.get(0).enable();
        formview.setActiveTab(0);
        if (buttonflag==='2'){ //把管库查询的电子文件屏蔽
            formview.items.get(1).disable();
        }
        return formview;
    },

    activeEleForm:function(obj){
        var isOpen1 = obj.selectedRow.get('flagopen');

        if(isOpen1==='条目开放'){
            XD.msg('档案未开放电子文件权限！如需查阅，请进行申请电子查档操作。');
            return;
        }
        var view = this.findView(obj.grid);
        var formview = this.findFormView(obj.grid);
        if (buttonflag === '1') {//利用平台
            var isOpen = obj.selectedRow.get('flagopen');
            if(isOpen==='条目开放'){
                XD.msg('档案未开放电子文件权限！如需查阅，请进行申请电子查档操作。');
                return;
            }
            for (var j = 0; j < formview.items.items.length; j++) {
                formview.items.get(j).setDisabled(false);
            }
            formview.items.get(1).disable();
        }
        view.setActiveItem(formview);
        formview.items.get(0).disable();
        var eleview = formview.down('electronic');
        var solidview = formview.down('solid');
        eleview.operateFlag = "look"; //电子文件查看标识符
        solidview.operateFlag = "look";//利用文件查看标识符
        eleview.initData(obj.entryid);
        if(dataSourceType=="soundimage"){
            solidview.xtType="声像系统";
        }else {
            solidview.xtType="";
        }
        solidview.initData(obj.entryid);
        var from =formview.down('dynamicform');
        //电子文件按钮权限
        var elebtns = eleview.down('toolbar').query('button');
        from.getELetopBtn(elebtns,eleview.operateFlag );
        var soildbtns = solidview.down('toolbar').query('button');
        from.getELetopBtn(soildbtns,solidview.operateFlag);
        if (buttonflag === '1'||buttonflag === '2') {
            formview.setActiveTab(2)
        }
        else{
            formview.setActiveTab(1)
        }
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
        if(dataSourceType=='soundimage'){  //声像系统
            formField = form.getSxFormField();//根据节点id查询表单字段
            form.xtType="声像系统";
        }else{
            formField = form.getFormField();//根据节点id查询表单字段
            form.xtType="档案系统";
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
        var sGrid = searchfield.findParentByType('simpleSearchView').down('simpleSearchGridView');
        var sMedia = searchfield.up('simpleSearchView').down('mediadtView');
        var mediaTabView = searchfield.up('simpleSearchView').down('mediaTabView');
        if (sGrid.title == '当前位置：个人收藏') {//如果是收藏界面
            isCollection = '收藏';
        }
        var datasoure="";
        if(buttonflag=="80"){
            datasoure = "soundimage";//数据源
        }else {
            datasoure = simpleSearchSearchView.down('[itemId=datasoure]').getChecked()[0].inputValue;//数据源
        }
        dataSourceType=datasoure;
        //检索数据
        //如果有勾选在结果中检索，则添加检索条件，如果没有勾选，则重置检索条件
        var grid = simpleSearchSearchView.findParentByType('panel').down('simpleSearchGridView');
        var gridstore = grid.getStore();
        //加载列表数据
        var searchcondition = condition;
        var searchoperator = operator;
        var searchcontent = content;
        //如果选择在结果中查询,将两种条件用','号隔开,参数一起传递到后台
        var inresult = simpleSearchSearchView.down('[itemId=inresult]').getValue();
        if(inresult){
            var params = gridstore.getProxy().extraParams;
            if(buttonflag=='80') {//利用平台声像
                if(mediaTabView.activeTab.xtype=='mediaGridView'){
                    var mediaGridStore = searchfield.up('simpleSearchView').down('mediaGridView').getStore();
                    var params = mediaGridStore.getProxy().extraParams;
                }else{
                    var simpleSearchMediaViewStore = sMedia.down('dataview').getStore();
                    var params = simpleSearchMediaViewStore.getProxy().extraParams;
                }
            }
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
        if (buttonflag == '2'){
            grid.dataParams.isCompilationManageSystem = true//是否为编研管理系统
        }
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
                var filterContent=[];
                // if(searchstrs.length>1) {
                //     for(var j=0;j<searchstrs.length;j++) {
                //         var flang=false;
                //         for (var o = 0; o < searchstrs.length; o++) {
                //             if (searchstrs[j].length > searchstrs[o].length) {
                //                 if (searchstrs[j].search(searchstrs[o]) != -1) {
                //                     flang=true;
                //                     break;
                //                     //filterContent.push(searchstrs[o]);
                //                 } else {
                //                     //searchstrs.splice(o, 1);
                //                     //break;
                //                 }
                //             } else {
                //                 if (searchstrs[o].search(searchstrs[j]) != -1) {
                //                     flang=true;
                //                     break;
                //                     //filterContent.push(searchstrs[o]);
                //                 } else {
                //                     //searchstrs.splice(o, 1);
                //                     //break;
                //                 }
                //             }
                //         }
                //         if(flang)
                //             filterContent.push(searchstrs[j]);
                //     }
                // }
                //反转数组
                for(var i=searchstrs.length-1;i>=0;i--){
                    filterContent.push(searchstrs[i]);
                }
                var contentData = filterContent.join('|').split(' ');//切割以空格分隔的多个关键词
                column.renderer = function(value){
                    var reg = new RegExp(contentData.join('|'),'g');
                    return value.replace(reg,function (match) {
                        return '<span style="color:red">'+match+'</span>';
                    });
                }
            }
        });
        if(grid.bookmarkStatus==false){
            var simpleSearchStore = Ext.create('SimpleSearch.store.SimpleSearchGridStore');
            simpleSearchStore.pageSize = typeof(grid.gridPagesSize) == 'undefined' ? grid.getStore().pageSize : grid.gridPagesSize;
            grid.down('pagingtoolbar').down('combo').select(simpleSearchStore.pageSize);
            if(buttonflag=='1' || buttonflag == '2' ||buttonflag=='a') {//利用平台和馆库查询
                simpleSearchStore.proxy.url = '/simpleSearch/findBySearchPlatform';
            }
            if(buttonflag=='80') {//利用平台声像
                var simpleSearchMediaViewStore;
                if(mediaTabView.activeTab.xtype=='mediaGridView'){
                    var gridData = searchfield.up('simpleSearchView').down('mediaGridView');
                    simpleSearchMediaViewStore = gridData.getStore();
                    //检索数据前,修改column的renderer，将检索的内容进行标红
                    Ext.Array.each(gridData.getColumns(), function(){
                        var column = this;
                        if(!inresult && column.xtype == 'gridcolumn'){
                            column.renderer = function(value){
                                return value;
                            }
                        }
                        if(column.dataIndex == condition){
                            var searchstrs = [];
                            var conditions=[];
                            var contents=[];
                            if(searchcondition!=null){
                                conditions = searchcondition.split(XD.splitChar);
                            }
                            if(searchcontent!=null){
                                contents = searchcontent.split(XD.splitChar);
                            }
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
                }else{
                    simpleSearchMediaViewStore = sMedia.down('dataview').getStore();
                }
                Ext.apply(simpleSearchMediaViewStore.getProxy().extraParams, {
                    condition: searchcondition,
                    operator: searchoperator,
                    content: searchcontent
                });
                simpleSearchMediaViewStore.loadPage(1);
                return
            }
            grid.reconfigure(simpleSearchStore);
        }
        grid.notResetInitGrid();
        grid.parentXtype = 'simpleSearchView';
        grid.formXtype = 'EntryFormView';
    },

    getCurrentSimpleSearchform:function (btn) {
        return btn.up('EntryFormView');
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

    addFnc:function (btn) {
        var select = btn.findParentByType('simpleSearchGridView').getSelectionModel();
        if (select.getCount() < 1) {
            XD.msg('请至少选择1条记录');
            return;
        }

        var dataids = [];
        for (var i = 0; i < select.getSelection().length; i++) {
            dataids.push(select.getSelection()[i].get('entryid'));
        }
        Ext.Ajax.request({
            params: {
                dataids: dataids
            },
            url: '/dataopen/addtobox',
            method: 'post',
            sync: true,
            success: function (resp) {
                var respText = Ext.decode(resp.responseText);
                if (respText.success == true) {
                    XD.msg(respText.msg);
                }
            },
            failure: function () {
                XD.msg('添加失败');
            }
        });
    },
    //获取新的mediaFormView
    getNewMediaFormView: function (btn, operate, mediaType,record) {
        var formAndGrid;
        if(btn.up('simpleSearchGridView')){
            formAndGrid=btn.up('simpleSearchGridView');
        }else if(btn.findParentByType('mediadtView')){
            formAndGrid=btn.findParentByType('mediadtView')
        }else{
            formAndGrid=btn.findParentByType('mediaGridView');
        }
        var entryid = '';
        var across=record[0];
        if (typeof across !== 'undefined' && (operate === 'look' || operate === 'modify')) {
            entryid = across.get('entryid');
        }
        var accept, uploadLabel;
        var dynamicRegion = 'west', header = true, col_spli = true, collapsed = false, collapsible = false;
        if (mediaType.indexOf('照片') !== -1) {
            accept = {
                title: 'Images',
                extensions: 'jpeg,jpg,png,bmp,gif,tiff,tif,crw,cr2,nef,raf,raw,kdc,mrw,nef,orf,dng,ptx,pef,arw,x3f,rw2',
                mimeTypes: 'image/*'
            };
            uploadLabel = '上传照片';
        } else if (mediaType.indexOf('视频') !== -1) {
            accept = {
                title: 'Videos',
                extensions: 'mp4,avi',
                mimeTypes: 'video/*'
            };
            uploadLabel = '上传视频';
        } else if (mediaType.indexOf('音频') !== -1) {
            accept = {
                title: 'Audio',
                extensions: 'mp3',
                mimeTypes: 'audio/*'
            };
            uploadLabel = '上传音频';
            dynamicRegion = 'south';
            header = false;
            col_spli = false;
            collapsed = true;
            collapsible = true;
        }
        formAndGrid.remove(formAndGrid.down('[itemId=amediaFormView]'));//删除原有的mediaFormView，确保干净
        var dynamicFromItem = {
            region: dynamicRegion,
            title: '条目',
            iconCls: 'x-tab-entry-icon',
            itemId: 'dynamicform',
            xtype: 'dynamicform',
            calurl: '/management/getCalValue',
            items: [{
                xtype: 'hidden',
                name: 'entryid'
            }],
            width: '70%',
            flex: 4,
            collapsible: col_spli,
            split: col_spli
        };
        var detailViewItem = {
            region: 'center',
            header: header,
            title: uploadLabel.substr(2, 2),
            iconCls: 'x-tab-electronic-icon',
            itemId: 'mediaDetailViewItem',
            entrytype: '',
            layout: 'fit',
            xtype: 'panel',
            items: [{
                itemId: 'mediaHtml',
                html: '<div id="mediaDiv" class="pw-view" style="background:white"></div>'
            }],
            flex: 1,
            collapsed: collapsed,
            collapsible: collapsible
        };
        formAndGrid.add({
            itemId: 'amediaFormView',
            xtype: 'mediaFormView',
            entryid: entryid,
            flag: false,//默认不用刷新
            acceptMedia: accept,
            uploadLabel: uploadLabel,
            mediaType: mediaType,
            items: [dynamicFromItem, detailViewItem]
        });
        return formAndGrid.down('[itemId=amediaFormView]');
    },
    initMediaFormData: function (operate, form, entryid, record) {
        var nullvalue = new Ext.data.Model();
        form.down('[itemId=preBtn]').hide();
        form.down('[itemId=nextBtn]').hide();
        var mediaFormView = form.up('mediaFormView');
        var fields = form.getForm().getFields().items;
        var prebtn = mediaFormView.down('[itemId=MpreBtn]');
        var nextbtn = mediaFormView.down('[itemId=MnextBtn]');
        var count;
        if (operate == 'modify' || operate == 'look') {
            for (var i = 0; i < form.entryids.length; i++) {
                if (form.entryids[i] == entryid) {
                    count = i + 1;
                    break;
                }
            }
            var total = form.entryids.length;
            var totaltext = form.up("mediaFormView").down('[itemId=MtotalText]');
            totaltext.setText('当前共有  ' + total + '  条，');
            var nowtext = form.up("mediaFormView").down('[itemId=MnowText]');
            nowtext.setText('当前记录是第  ' + count + '  条');
            totaltext.show();
            nowtext.show();
            prebtn.hide();
            nextbtn.hide();
        }
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
        if (operate != 'look' && operate != 'lookfile') {

        } else {
            Ext.each(fields, function (item) {
                item.setReadOnly(true);
            });
        }
        var urls= '/management/entries/' + entryid+"?xtType="+"声像系统";
        Ext.Ajax.request({
            method: 'GET',
            scope: this,
            url: urls,
            success: function (response) {
                var entry = Ext.decode(response.responseText);
                var data = Ext.decode(response.responseText);
                if (data.organ) {
                    entry.organ = data.organ;//机构
                }
                var fieldCode = form.getRangeDateForCode();//字段编号，用于特殊的自定义字段(范围型日期)
                if (fieldCode != null) {
                    //动态解析数据库日期范围数据并加载至两个datefield中
                    form.initDaterangeContent(entry);
                }
                form.loadRecord({
                    getData: function () {
                        return entry;
                    }
                });
                form.entryid = entry.entryid;
                if (operate == 'look' || operate == 'modify') {
                    Ext.Ajax.request({
                        method: 'POST',
                        params: {entryid: entry.entryid},
                        url: '/electronic/getSxElectronicByEntryid',
                        async: false,
                        success: function (response) {
                            var eleRecord = Ext.decode(response.responseText).data;
                            mediaFormView.currentMD5 = eleRecord.md5;
                            if (mediaFormView.mediaType.indexOf('照片') !== -1) {
                                if (typeof(mediaFormView.photoView) == 'undefined') {
                                    mediaFormView.photoView = new PhotoView({
                                        eleid: 'mediaDiv',
                                        src: '/electronic/loadSpecialMedia?entryid=' + entryid+"&fileType=photo",
                                        initWidth: '90%'
                                    });
                                } else {
                                    Ext.apply(mediaFormView.uploader.options, {
                                        server: '/electronic/serelectronics/' + mediaFormView.entrytype + "/" + form.entryid
                                    });
                                    mediaFormView.photoView.changeImg('/electronic/loadSpecialMedia?entryid=' + entryid);
                                }
                            } else if (mediaFormView.mediaType.indexOf('视频') !== -1) {
                                mediaFormView.compressing = false;
                                var videoHtml = '<a href="/electronic/loadSpecialMedia?entryid=' + entryid + '&fileType=video" style="position:absolute;top:0;right:0;left:0;bottom:0;margin:auto;width:520px;height:320px" id="player"></a>';
                                document.getElementById('mediaDiv').innerHTML = videoHtml;
                                flowplayer("player", "../js/flowplayerFlash/flowplayer.swf", {
                                    plugins: {
                                        controls: {
                                            height: 30,
                                            tooltips: {
                                                buttons: true,
                                                play: '播放',
                                                fullscreen: '全屏',
                                                fullscreenExit: '退出全屏',
                                                pause: '暂停',
                                                mute: '静音',
                                                unmute: '取消静音'
                                            }
                                        }
                                    },
                                    canvas: {
                                        backgroundColor: '#000',
                                        backgroundGradient: [0, 0]//无渐变色
                                    },
                                    clip: {
                                        autoPlay: false,
                                        autoBuffering: true
                                    },
                                    onStart: function (clip) {
                                        animate(this, clip, {
                                            height: 320,
                                            width: 520
                                        })
                                    },
                                    onFullscreen: function (clip) {
                                        setTimeout(function () {
                                            animate(this, clip, {
                                                height: screen.height,
                                                width: screen.width
                                            }, clip);
                                        }, 1000);
                                    }
                                });
                            } else if (mediaFormView.mediaType.indexOf('音频') !== -1) {
                                mediaFormView.compressing = false;
                                var videoHtml = '<div class="audio-box"></div>';
                                document.getElementById('mediaDiv').innerHTML = videoHtml;
                                Ext.Ajax.request({
                                    params: {entryid: entryid},
                                    url: '/electronic/getBrowseByEntryid',
                                    success: function (response) {
                                        var responseText = Ext.decode(response.responseText);
                                        if (responseText.data !== null) {
                                            var name = responseText.data.filename;
                                            name = name.substring(0, name.lastIndexOf('.'));
                                            var audioFn = audioPlay({
                                                song: [{
                                                    title: name,
                                                    src: responseText.data.filepath + "/" + responseText.data.filename,
                                                    cover: '../../img/defaultMedia/default_audio.png'
                                                }],
                                                error: function (msg) {
                                                    XD.msg(msg.meg);
                                                    console.log(msg)
                                                }
                                            });
                                            if (audioFn) {
                                                audioFn.loadFile(false);
                                            }
                                        }
                                    },
                                    failure: function () {
                                        XD.msg('获取浏览音频中断');
                                    }
                                });
                            }
                        },
                        failure: function () {
                            XD.msg('操作失败！');
                        }
                    });
                }
            }
        });
    },
    lookBack: function (btn) {
        btn.up('simpleSearchView').setActiveItem(btn.up('simpleSearchView').down('[itemId=gridview]'));
        if (window.play) {
            window.play(false);//音频停止播放
        }
        btn.up('mediaFormView').destroy();//销毁，防止视频在后台继续播放
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
        var url;
        if(form.datasoure=="soundimage"){  //声像
            url = "/simpleSearchDirectory/getSxEntry/";
        }else{
            url = "/management/entries/";
        }
        Ext.Ajax.request({
            method:'GET',
            scope:this,
            url:url+entryid,
            params: {
                datasoure: form.datasoure
            },
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
                var solidview = formview.down('solid');
                if(form.datasoure=="soundimage"){
                    solidview.xtType="声像系统";
                }else {
                    solidview.xtType="";
                }
                eleview.initData(entryid);
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
    resetSearchCondition:function (grid) {
        grid.getStore().proxy.extraParams.content = '';//清空参数内容
        grid.getStore().removeAll();
        /*检索条件（仅页面显示）重置*/
        var conditionCombo = grid.up('simpleSearchView').down('[itemId=simpleSearchSearchComboId]');
        var conditionStore = conditionCombo.getStore();
        if (conditionStore.getCount() > 0) {
            conditionCombo.select(conditionStore.getAt(0));
        }
        /*检索内容（仅页面显示）重置*/
        var searchfield = grid.up('simpleSearchView').down('[itemId=simpleSearchSearchfieldId]');
        searchfield.reset();
    },

    //点击上一条设置打印范围
    preApplySetPrint:function(btn){
        var applySetPrintView = btn.findParentByType('applySetPrintView');
        var form = applySetPrintView.down('applyPrintSetEleView');
        this.refreshApplySetPrint(form, 'pre');
    },

    //点击下一条设置打印范围
    nextApplySetPrint:function(btn){
        var applySetPrintView = btn.findParentByType('applySetPrintView');
        var form = applySetPrintView.down('applyPrintSetEleView');
        this.refreshApplySetPrint(form, 'next');
    },

    refreshApplySetPrint:function(form, type){
        var entryids = form.entryids;
        var currentEntryid = form.entryid;
        var entryid;
        for(var i=0;i<entryids.length;i++){
            if(type == 'pre' && entryids[i] == currentEntryid){
                if(i==0){
                    i=entryids.length;
                }
                entryid = entryids[i-1];
                break;
            }else if(type == 'next' && entryids[i] == currentEntryid){
                if(i==entryids.length-1){
                    i=-1;
                }
                entryid = entryids[i+1];
                break;
            }
        }
        form.entryid = entryid;
        this.getApplySetPrint(form);
    },

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
function setBookmarks(grid,ismedia) {
    var record;
    if(ismedia != null && ismedia == 1) {
        record = grid.acrossSelections;
    }
    else {
        record = grid.getSelectionModel().getSelection();
    }
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
    var type = "management";
    var datasoure = grid.getStore().getProxy().extraParams.datasoure;  //数据源
    if(titleflag==2){//编研管理系统-馆库查询
        type = datasoure=="soundimage"?"pavilionSoundimage":"pavilion";
    }
    Ext.Msg.wait('正在进行'+operate+'操作，请耐心等待……','正在操作');
    Ext.Ajax.request({
        url:'/bookmarks/setBookmarks',
        method: 'POST',
        timeout:XD.timeout,
        params:{
            entryids:array,
            bookmarkStatus:grid.bookmarkStatus,
            type:type
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

function checkMobile(str) {
    var re = /^1\d{10}$/;
    if (re.test(str)) {
        return "正确";
    } else {
        return "错误";
    }
}
function checkPhone(str){
    var re = /^0\d{2,3}-?\d{7,8}$/;
    if(re.test(str)){
        return "正确";
    }else{
        return "错误";
    }
}