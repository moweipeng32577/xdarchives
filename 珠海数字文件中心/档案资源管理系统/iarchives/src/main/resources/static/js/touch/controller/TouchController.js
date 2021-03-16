/**
 * Created by RonJiang on 2017/10/24 0024.
 */
var setBorrowType="";//保存设置查档类型
var isFlag="条目开放";//1为声像开放
var dataSourceType="";
buttonflag="a";
Ext.define('Touch.controller.TouchController', {
    extend: 'Ext.app.Controller',
    views: [
        'SimpleSearchView', 'SimpleSearchGridView'//简单检索

    ],
    stores: [
        'SimpleSearchGridStore'
    ],
    models: [
        'SimpleSearchGridModel'
    ],
    init : function() {
        var simpleSearchGridView, printWin;
        var count = 0;
        var gridflag = false;
        this.control({
            'simpleSearchView button[itemId=search]':{
                click:function (btn) {
                    var view=btn.findParentByType('simpleSearchView');
                    var simpleField=view.down('searchfield');
                    this.searchInfo(simpleField);
                }
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
            'simpleSearchView [itemId=back]': {//返回
                click: function(btn){
                    this.activeGrid(btn, false);
                }
            },
        })
    },
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
            var simpleSearchStore = Ext.create('Touch.store.SimpleSearchGridStore');
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
    findView: function (btn) {
        return btn.findParentByType('simpleSearchView');
    },
    findFormView: function (btn) {
        return this.findView(btn).down('EntryFormView');
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
    findGridView: function (btn) {
        return this.findView(btn).getComponent('gridview');
    },
});