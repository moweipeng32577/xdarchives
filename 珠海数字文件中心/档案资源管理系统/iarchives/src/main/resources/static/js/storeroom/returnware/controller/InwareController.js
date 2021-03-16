/**
 * Created by Rong on 2018/4/27.
 */
var formvisible,formlayout;
var NodeIdf = "";
Ext.define('ReturnWare.controller.InwareController',{
    extend: 'Ext.app.Controller',

    views: ['InwareView','InwareTabView','NewInwareView','ReturnWareView','HistoryView',
      /*'WareFormView',*/'ManagementView','ManagementGridView','InWareDetailView','EntityDetailView',
    'FormAndGridView','InwareFormView','ManagementEntryGridView','AdvancedSearchFormView','ViewInwareDetail','ReturnMessageView'],
    stores:['CityStore','UnitStore','RoomStore','ZoneStore','ColStore','SectionStore','LayerStore','SideStore','ManagementStore','InwareStore','InwareDetailStore'],
    model:['CityModel','UnitModel','RoomModel','ZoneModel','ColModel','SectionModel','LayerModel','SideModel','ManagementModel','InwareModel'],

    init:function(){
        this.control({
            '[itemId=waregrid] button[itemId=add]':{
                click:this.addHandler
            },
            'wareFormView button[itemId=save]':{
                click:this.saveHandler
            },
            'wareFormView button[itemId=dzbtn]':{
                click:this.chooseHandler
            },
            'wareFormView button[itemId=cancel]':{
                click:this.chooseHandler
            },
            'NewInwareView button[itemId=back]':{
                click:function(btn){
                    btn.findParentByType('NewInwareView').close();
                }
            },
            'historyView [itemId=inwaregrid]':{//点击打开入库记录，显示电子条目
                itemclick:this.itemClickHandler
            },
            'managementView [itemId=treepanelId]':{
                select: function(treemodel, record){
                    NodeIdf = record.get('fnid');
                    var gridcard = treemodel.view.up('managementView').down('[itemId=gridcard]');
                    var onlygrid = gridcard.down('[itemId=onlygrid]');
                    var pairgrid = gridcard.down('[itemId=pairgrid]');
                    var grid;
                    var nodeType = record.data.nodeType;
                    var cls = record.data.cls;
                    var bgSelectOrgan =gridcard.down('[itemId=bgSelectOrgan]');
                    if(nodeType == 2){
                        gridcard.setActiveItem(bgSelectOrgan);
                        return;
                    }
                    /*if (Ext.String.endsWith(record.data.text,'卷',true)) {
                        gridcard.setActiveItem(pairgrid);
                        var ajgrid = pairgrid.down('[itemId=northgrid]');
                        ajgrid.setTitle("当前位置：" + record.data.text);
                        var jngrid = pairgrid.down('[itemId=southgrid]');
                        jngrid.setTitle("查看卷内");
                        if(jngrid.expandOrcollapse == 'expand'){
                            jngrid.expand();
                        }else{
                            jngrid.collapse();
                        }
                        jngrid.dataUrl = '/management/entries/innerfile/'+ '' + '/';
                        jngrid.initGrid(this.getNodeid(record.get('nodeid')));
                        grid = ajgrid;
                    } else {

                    }*/
                    gridcard.setActiveItem(onlygrid);
                    onlygrid.setTitle("当前位置：" + record.data.text);
                    grid = onlygrid;

                    var buttons = grid.down("toolbar").query('button');
                    var tbseparator = grid.down("toolbar").query('tbseparator');

                    grid.nodeid = record.get('fnid');
                    grid.dataUrl = '/management/storageNoEntries',
                    grid.initGrid({nodeid:record.get('fnid')});
                    var fullname=record.get('text');
                    while(record.parentNode.get('text')!='数据管理'){
                        fullname=record.parentNode.get('text')+'_'+fullname;
                        record=record.parentNode;
                    }
                    grid.nodefullname = fullname;
                    grid.parentXtype = 'management';
                    grid.formXtype = 'managementform';
                }
            },'managementgridView [itemId=idsBack]':{
                click:this.addIdsHandler
            },'managementgridView [itemId=setBookmarks]':{//选择电子档案-添加
                click:this.addIdsware
            },'managementgridView [itemId=viewInvare]':{//选择电子档案-查看添加
                click:function (btn) {
                    var DetailsWin = Ext.create('Ext.window.Window',{
                        width:900,
                        height:530,
                        title:'查看添加',
                        layout:'fit',
                        closeToolText:'关闭',
                        closeAction:'hide',
                        items:[{
                            xtype: 'ViewInwareDetail'
                        }]
                    });
                    var store = DetailsWin.down('ViewInwareDetail').getStore();
                    store.reload();
                    DetailsWin.show();
                }
            },'ViewInwareDetail button[itemId=back]':{//选择电子档案-查看添加-返回
                click:function (btn) {
                    btn.findParentByType('window').close();
                }
            },'ViewInwareDetail button[itemId=deleteEntry]':{//选择电子档案-查看添加-删除
                click:this.deleteEntry
            },'managementgridView [itemId=save]':{//著录
                click:this.jargonHandler
            },
            'managementgrid [itemId=save]':{//著录-著录
                click:this.jargonHandler
            },'managementgrid [itemId=advancedsearch]':{//著录-高级检索
                click:this.advancedSearch
            },'managementgrid [itemId=viewInvare]':{//著录-查看添加
                click:function (btn) {
                    var DetailsWin = Ext.create('Ext.window.Window',{
                        width:900,
                        height:530,
                        title:'查看添加',
                        layout:'fit',
                        closeToolText:'关闭',
                        closeAction:'hide',
                        items:[{
                            xtype: 'ViewInwareDetail'
                        }]
                    });
                    var store = DetailsWin.down('ViewInwareDetail').getStore();
                    store.reload();
                    DetailsWin.show();
                }
            },'managementgrid [itemId=back]':{//著录-返回
                click:function (btn) {
                    this.activeGrid(btn,false);
                }
            },
            ///////////高级检索－－－－－－－－－－－－－－－start////////////////////////////
            'managementgridView [itemId=advancedsearch]':{//选择电子档案-高级检索
                click:this.advancedSearch
            },
            'advancedSearchFormView':{
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
            'advancedSearchFormView [itemId=topSearchBtn]':{click:this.doAdvancedSearch},
            'advancedSearchFormView [itemId=bottomSearchBtn]':{click:this.doAdvancedSearch},
            'advancedSearchFormView [itemId=topClearBtn]':{click:this.doAdvancedSearchClear},
            'advancedSearchFormView [itemId=bottomClearBtn]':{click:this.doAdvancedSearchClear},
            'advancedSearchFormView [itemId=topCloseBtn]':{click:this.doAdvancedSearchClose},
            'advancedSearchFormView [itemId=bottomCloseBtn]':{click:this.doAdvancedSearchClose},
            ///////////高级检索－－－－－－－－－－－－－－－end////////////////////////////
            'inwareTabView': {
                render: function (view) {
                    if (view.activeTab.title == '入库历史记录查询') {
                        var gridcard=view.down('[itemId=inwaregrid]');
                        gridcard.initGrid({nodeid:'12345678910'});
                        gridcard.getStore().reload();
                    }else if (view.activeTab.title == '归还入库') {
                        var gridcard=view.down('returnWareView');
                        gridcard.initGrid({nodeid:'12345678910'});
                        gridcard.getStore().reload();
                    }
                }
            },'returnWareView [itemId=add]':{
                click:this.returnHandler
            },'managementgridView [itemId=back]':{//返回
                click:function(btn){
                    btn.findParentByType('managementView').close();
                }
            },'managementgridView [itemId=idsClose]':{//关闭
                click:function(){
                    parent.closeObj.close(parent.layer.getFrameIndex(window.name));
                }
            },'inWareDetailView [itemId=detailGridView]':{//点击打开档案详细
                itemclick:this.entryItemClickHandler
            },'inwareform [itemId=back]':{//返回
                click: function(btn){
                    this.activeGrid(btn,false);
                }
            },'inwareform [itemId=save]':{//著录-保存
                click:this.submitForm
            },'ReturnMessage button[itemId=cancelGh]':{
                click:function(btn){
                    btn.findParentByType('ReturnMessage').close();
                }
            },
            'ReturnMessage button[itemId=saveGh]':{
                click:this.addReturn
            }

        });
    },

    addHandler:function(){
        var win = this.getView('WareFormView').create({
            title:'新增实体档案数据',
            width:800,
            height:450
        });
        win.show();
    },
    saveHandler:function(btn){
        var wareFormView = btn.up('wareFormView');
        var textfil=wareFormView.down('[itemId=ids]');
        var namefil=wareFormView.down('[itemId=idsName]');
        var shidfil=wareFormView.down('[itemId=shid]');
        var ids=textfil.getValue();
        var shid=shidfil.getValue();
        if (shid == '' || shid == null ) {XD.msg('请选择存放位置');return;}
        if (ids == '' || ids == null ) {XD.msg('请选择电子档案');return;}

        var formpanel = btn.findParentByType('wareFormView').down('form');
        formpanel.submit({
            url:'/inware/save',
            method : 'POST',
            success : function(form, action) {
                var respText = Ext.decode(action.response.responseText);
                if (respText.success == true) {
                    XD.msg(respText.msg);
                    textfil.setValue('');
                    namefil.setValue('');
                } else {
                    XD.msg(respText.msg);
                }
                Ext.Ajax.request({
                    url:'/management/deleteInwares',//删除操作
                    method: 'POST',
                    timeout:XD.timeout,
                    params:{
                        entryids:ids
                    },
                    success:function(response) {
                        var resp = Ext.decode(response.responseText);
                        if(resp.success==false){
                            Ext.MessageBox.alert("提示信息", resp.msg, function(){
                            });
                        }else{
                        }
                    }
                });
            },
            failure : function(form, action) {
                XD.msg('操作失败');
            }
        })
    },

    chooseHandler:function(btn){
        var wareFormView = btn.up('wareFormView');
        var textfil=wareFormView.down('[itemId=ids]');
        var namefil=wareFormView.down('[itemId=idsName]');
        var entryid;
        var entrynames;
        var win = this.getView('ManagementView').create({
            title:'选择档案',
            modal: true,
            resizable: false,
            width:'100%',
            height:'100%',
            maximizable : true,
            itemId:'chooseEntryView',
            listeners: {
                close: function (_this) {
                    entryid=_this.down('[itemId=entriesId]').getValue();
                    entrynames=_this.down('[itemId=entriesName]').getValue();
                    textfil.setValue(entryid);
                    var temp=entrynames.split(';');
                    var nameStr='';
                    for (i=0;i<temp.length ;i++ ){
                        nameStr+=temp[i]+'\n'
                    }
                    namefil.setValue(nameStr);
                }
            }
            });
        //绑定子窗口到父窗口
        wareFormView.chooseEntryView = win;
        var button=win.down('[itemId=basicgridCloseBtn]');
        button.hide();//隐藏【关闭】按钮
        /*var winStore = win.down('treepanel').getStore();
        winStore.proxy.extraParams.pcid='';
        winStore.proxy.reader.expanded=true;
        winStore.reload();*/
        win.show();
    },

    addIdsHandler:function(btn){

        var grid = this.findActiveGrid(btn);
        var record = grid.selModel.getSelection();
        var selectCount = record.length;
        //XD.msg(selectCount);
        var selectAll = btn.findParentByType('managementView').down('[itemId=selectAll]').checked;
        if (selectAll) {
            selectCount = grid.selModel.selected.length;//当前页选中
        }
        var tree = this.findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if(!node){
            XD.msg('请选择节点');
            return;
        }
        if (selectAll && selectCount == 0) {
            XD.msg('当前页没有选中数据');
            return;
        }
        if (selectCount == 0) {
            XD.msg('当前页没有选中数据');
            return;
        }
        var entryid;
        var entryNames;
        if (selectAll) {
            entryid = grid.selModel.selected.items[0].get("entryid");
            entryNames = grid.selModel.selected.items[0].get("title");
        } else {
            if(selectCount==1){
                entryid = record[0].get("entryid");
                entryNames = record[0].get("archivecode")+' : '+record[0].get("title")+';';
            }else{
                for (var i = 0; i < record.length; i++) {
                    entryid=entryid+record[i].get("entryid")+',';
                    entryNames=entryNames+record[i].get("archivecode")+' : '+record[i].get("title")+';';
                }
                entryid=entryid.substring(9,entryid.length-1);
                entryNames=entryNames.substring(9,entryNames.length-1);
            }
        }

        //把数据entryid 放到隐藏文件框
        btn.findParentByType('managementView').down('[itemId=entriesId]').setValue(entryid);
        btn.findParentByType('managementView').down('[itemId=entriesName]').setValue(entryNames);

        //关闭窗口
        btn.findParentByType('managementView').close();
    },

    addIdsware:function (btn) {
        var grid = this.findActiveGrid(btn);
        var record = grid.selModel.getSelection();
        var selectCount = record.length;
        if (selectCount == 0) {
            XD.msg('当前页没有选中数据');
            return;
        }
        var array = [];
        for (var i = 0; i < record.length; i++) {
            array[i] = record[i].get('entryid');
        }
        Ext.Msg.wait('正在进行添加操作，请耐心等待……','正在操作');
        Ext.Ajax.request({
            url:'/management/setAddInwares',
            method: 'POST',
            timeout:XD.timeout,
            params:{
                entryids:array
            },
            success:function(response) {
                var resp = Ext.decode(response.responseText);
                if(resp.success==false){
                    Ext.Msg.wait('添加操作中断','正在操作').hide();
                    Ext.MessageBox.alert("提示信息", resp.msg, function(){
                    });
                }else{
                    Ext.Msg.wait('添加操作完成','正在操作').hide();
                    XD.msg("添加成功");

                    Ext.Ajax.request({
                        url:'/management/findAddInwares',
                        method:'POST',
                        success:function (response) {
                            var resp = Ext.decode(response.responseText);
                            var data = resp.data;
                            var entryid;
                            var entryNames;
                            if(data.length==1){
                                entryid = data[0].entryid;
                                entryNames = data[0].archivecode+' : '+data[0].title+';';
                            }else{
                                for (var i = 0; i < data.length; i++) {
                                    entryid=entryid+data[i].entryid+',';
                                    entryNames=entryNames+data[i].archivecode+' : '+data[i].title+';';
                                }
                                entryid=entryid.substring(9,entryid.length-1);
                                entryNames=entryNames.substring(9,entryNames.length-1);
                            }

                            //把数据entryid 放到隐藏文件框
                            btn.findParentByType('managementView').down('[itemId=entriesId]').setValue(entryid);
                            btn.findParentByType('managementView').down('[itemId=entriesName]').setValue(entryNames);

                            //关闭窗口
                            btn.findParentByType('managementView').close();
                        }
                    });
                }
            }
        });
    },

    findInnerGrid:function(btn){
        return this.findView(btn).down('[itemId=southgrid]');
    },

    findTreeView : function (btn) {
        return btn.findParentByType('managementView').down('treepanel');
    },

    initSouthGrid:function (form) {
        var formAndGridView = this.findView(form).down('formAndGrids');//保存表单与表格视图
        var gridview = formAndGridView.down('managementgrid');
        gridview.initGrid({nodeid:form.nodeid});
    },

    changeBtnStatus:function(form, operate){
        var savebtn,continuesave,tbseparator;
        if (form.findParentByType('formAndGrids')) {
            savebtn = this.findFormView(form).down('[itemId=save]');
            continuesave = this.findFormView(form).down('[itemId=continuesave]');
            tbseparator = this.findFormView(form).getDockedItems('toolbar')[0].query('tbseparator');
        }
        if (form.findParentByType('formAndInnerGrid')) {
            savebtn = this.findFormInnerView(form).down('[itemId=save]');
            continuesave = this.findFormInnerView(form).down('[itemId=continuesave]');
            tbseparator = this.findFormInnerView(form).getDockedItems('toolbar')[0].query('tbseparator');
        }
        if (form.findParentByType('formView')) {
            var managementform = formAndGridView.down('formView').down('managementform');
            savebtn = managementform.down('[itemId=save]');
            continuesave = managementform.down('[itemId=continuesave]');
            tbseparator = managementform.getDockedItems('toolbar')[0].query('tbseparator');
        }
        if(operate == 'look'){//查看时隐藏保存及连续录入按钮
            savebtn.setVisible(false);
            continuesave.setVisible(false);
            tbseparator[0].setVisible(false);
            tbseparator[1].setVisible(false);
        }else if(operate == 'modify' || operate == 'insertion'){//修改或插件时隐藏连续录入按钮
            savebtn.setVisible(true);
            continuesave.setVisible(false);
            tbseparator[0].setVisible(false);
            tbseparator[1].setVisible(true);
        }else{
            savebtn.setVisible(true);
            //continuesave.setVisible(true);
            tbseparator[0].setVisible(true);
            //tbseparator[1].setVisible(true);
        }
    },

    getCurrentManagementform:function (btn) {
        if (btn.up('formAndGrids')) {//如果是案卷表单
            return this.findFormView(btn);
        }
        if (btn.up('formAndInnerGrid')){//如果是卷内表单
            return this.findFormInnerView(btn);
        }
        if (btn.up('formView') || btn.xtype == 'entrygrid' || btn.xtype == 'managementgrid') {
            return formAndGridView.down('formView').down('managementform');
        }
    },

    ifSettingCorrect:function (nodeid,templates) {
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
    },

    ifCodesettingCorrect:function (nodeid) {
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

    getGrid: function (btn) {
        var grid;
        if (!btn.findParentByType('formAndGrids')) {
            grid = this.findActiveGrid(btn);
        } else {
            grid = this.findGridToView(btn);
        }
        return grid;
    },

    findGridToView: function (btn) {
        return this.findView(btn).down('formAndGrids').down('managementgrid');
    },

    //切换到表单界面视图
    activeForm: function (form) {
        var view = this.findView(form);
        var formAndGridView = view.down('formAndGrids');//保存表单与表格视图
        view.setActiveItem(formAndGridView);

        var formview = formAndGridView.down('inwareform');
        formview.items.get(0).enable();
        formview.setActiveTab(0);
        return formAndGridView;
    },

    initFormData:function(operate, form, entryid, state){
        var nullvalue = new Ext.data.Model();
        var managementform = form.up('inwareform');
        var fields = form.getForm().getFields().items;
        var prebtn = form.down('[itemId=preBtn]');
        var nextbtn = form.down('[itemId=nextBtn]');
        var savebtn = managementform.down('[itemId=save]');
        var continuesavebtn = managementform.down('[itemId=continuesave]');
        var count = 1;
        if(operate == 'modify' || operate == 'look'){
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
        var etips = form.up('inwareform').down('[itemId=etips]');
        etips.show();
        if(operate!='look'&&operate!='lookfile'){
            var settingState = this.ifSettingCorrect(form.nodeid,form.templates);
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
        var eleview = this.getCurrentManagementform(form).down('electronic');
        var solidview = this.getCurrentManagementform(form).down('solid');
        // var longview = this.getCurrentManagementform(form).down('long');
        if(state == '案卷著录' || state == '卷内著录'){
            //通过节点查询当前模板的默认值
            Ext.Ajax.request({
                method: 'POST',
                params: {
                    nodeid: form.nodeid,
                    entryid: entryid,
                    type: state
                },
                url: '/management/getDefaultInfo',//通过节点的id获取模板中所有配置值默认数据
                success:function (response) {
                    var info = Ext.decode(response.responseText);
                    form.loadRecord({getData: function () {return info.data;}});
                }
            });
            eleview.initData();
            solidview.initData();
            // longview.initData();
        } else {
            Ext.Ajax.request({
                method: 'GET',
                scope: this,
                url: '/management/entries/' + entryid,
                success: function (response) {
                    var entry = Ext.decode(response.responseText);
                    if(operate == 'insertion'){
                        prebtn.setVisible(false);
                        nextbtn.setVisible(false);
                        this.entryID = entryid;
                        delete entry.entryid;
                    }
                    if(operate == 'lookfile'){
                        prebtn.setVisible(false);
                        nextbtn.setVisible(false);
                        savebtn.setVisible(false);
                        continuesavebtn.setVisible(false);
                    }
                    var data = Ext.decode(response.responseText);
                    if (data.organ) {
                        entry.organ = data.organ;//机构
                    }
                    if(operate == 'add'){
                        delete entry.entryid;
                        entry.filingyear = new Date().getFullYear();
                        entry.descriptiondate = Ext.util.Format.date(new Date(),'Y-m-d H:i:s');
                        if (data.keyword && entry.keyword) {
                            entry.keyword = data.keyword;//主题词
                        }
                        Ext.Ajax.request({
                            async:false,
                            url: '/user/getUserRealname',
                            success:function (response) {
                                entry.descriptionuser = Ext.decode(response.responseText).data;
                            }
                        });
                    }
                    if (operate == 'add' || operate == 'modify') {
                        if (!data.organ) {
                            Ext.Ajax.request({
                                async:false,
                                url: '/nodesetting/findByNodeid/' + form.nodeid,
                                success:function (response) {
                                    entry.organ = Ext.decode(response.responseText).data.nodename;
                                }
                            });
                        }
                    }
                    form.loadRecord({getData: function () {return entry;}});
                    if (operate == 'add') {
                        var formValues = form.getValues();
                        var formParams = {};
                        for(var name in formValues){//遍历表单中的所有值
                            formParams[name] = formValues[name];
                        }
                        formParams.nodeid = form.nodeid;
                        formParams.nodename = this.getNodename(form.nodeid);
                        var archive = '';
                        var calFieldName = '';
                        var calValue = '';
                        Ext.Ajax.request({//计算项的数值获取并设置
                            url:form.calurl,//动态URL
                            async:true,
                            params:formParams,
                            success:function(response){
                                var result = Ext.decode(response.responseText).data;
                                if(result){
                                    calFieldName = result.calFieldName;
                                    calValue = result.calValueStr;
                                    archive = result.archive;
                                }
                                var calField = form.getForm().findField(calFieldName);
                                if(calField==null){
                                    return;
                                }
                                calField.setValue(calValue);//设置档号最后一个构成字段的值，填充至文本框中
                                var archiveCode = form.getForm().findField('archivecode');
                                if(archiveCode==null){
                                    return;
                                }
                                archiveCode.setValue(archive);
                            }
                        });
                    }
                    var fieldCode = form.getRangeDateForCode();//字段编号，用于特殊的自定义字段(范围型日期)
                    if (fieldCode != null) {
                        //动态解析数据库日期范围数据并加载至两个datefield中
                        form.initDaterangeContent(entry);
                    }
                    eleview.initData(entry.entryid);
                    solidview.initData(entry.entryid);
                    // longview.initData(entry.entryid);
                }
            });
        }
//        form.formStateChange(operate);
        form.fileLabelStateChange(eleview,operate);
        form.fileLabelStateChange(solidview,operate);
        // form.fileLabelStateChange(longview,operate);
        this.changeBtnStatus(form,operate);
    },

    initFormField:function(form, operate, nodeid){
//        if (form.nodeid != nodeid) {//切换节点后，form和tree的节点id不相等
        form.nodeid = nodeid;//用左侧树节点的id初始化form的nodeid参数
        form.removeAll();//移除form中的所有表单控件
        var field = {
            xtype: 'hidden',
            name: 'entryid'
        };
        form.add(field);
        var formField = form.getFormField();//根据节点id查询表单字段
        if(formField.length==0){
            XD.msg('请检查档号设置信息是否正确');
            return;
        }
        form.templates = formField;
        form.initField(formField,operate);//重新动态添加表单控件
//        }
        return '加载表单控件成功';
    },

    //获取表单界面视图
    findFormView: function (btn) {
       return this.findRKView(btn).down('formAndGrids').down('inwareform');
    },

    findRKView:function(btn){
        if(!btn.findParentByType('managementgridView')){
            return btn.up('managementView');
        }else{
            return btn.up('managementgridView').up('managementView');
        }
    },

    //获取数据管理应用视图
    findView:function(btn){
        return btn.findParentByType('managementView');
    },

    //获取列表界面视图
    findGridView:function(btn){
        return this.findView(btn).getComponent('gridview');
    },

    findActiveGrid:function(btn){
        var active = this.findView(btn).down('[itemId=gridcard]').getLayout().getActiveItem();
        if(active.getXType() == "managementgridView"){
            return active;
        }else if(active.getXType() == "panel"){
            return active.down('[itemId=northgrid]');
        }
    },

    returnHandler:function(btn){
        var grid = btn.findParentByType('returnWareView');
        window.returnGridView=grid;
        var record = grid.selModel.getSelection();
        var selectCount = record.length;
        //XD.msg(selectCount);
        var selectAll = btn.findParentByType('returnWareView').down('[itemId=selectAll]').checked;
        if (selectAll) {
            selectCount = grid.selModel.selected.length;//当前页选中
        }
        if (selectCount == 0) {
            XD.msg('当前页没有选中数据');
            return;
        }
        var entryid;
        var nodeid=record[0].get("nodeid");
        if (selectAll) {
            entryid = grid.selModel.selected.items[0].get("entryid");
        } else {
            if(selectCount==1){
                entryid = record[0].get("entryid");
            }else{
                for (var i = 0; i < record.length; i++) {
                    entryid=entryid+record[i].get("entryid")+',';
                }
                entryid=entryid.substring(9,entryid.length-1);
            }
        }

        //把数据entryid 提交入库
        XD.confirm('是否确定归还入库选中的已出库', function () {
            var win = Ext.create("ReturnWare.view.ReturnMessageView", {});
            win.down('[itemId=entryidsId]').setValue(entryid);
            win.down('[itemId=nodeidId]').setValue(nodeid);
            Ext.Ajax.request({
                url: '/user/getCurrentusr',
                method: 'get',
                sync: true,
                success: function (response) {
                    var resp = Ext.decode(response.responseText);
                    win.down('[itemId=userName]').setValue(resp.data);
                }
            });
            win.show();
        });
    },

    addReturn:function(btn){
        var messageView=btn.up('ReturnMessage');
        var returnMan=messageView.down('[itemId=userName]').getValue();
        var entryids=messageView.down('[itemId=entryidsId]').getValue();
        var remarkValue=messageView.down('[itemId=remarkId]').getValue();
        var nodeid=messageView.down('[itemId=nodeidId]').getValue();

        Ext.Ajax.request({
            params: {ids: entryids,remarkText:remarkValue},
            url: '/inware/returnware',
            method: 'post',
            sync: true,
            success: function () {
                XD.msg('归还入库成功');
                messageView.close();
                window.returnGridView.initGrid({nodeid:nodeid});

                //归还管理归还条目
                Ext.Ajax.request({
                    params: {ids: entryids},
                    url: '/jyAdmins/inwareReturn',
                    method: 'post',
                    success: function () {
                    },
                    failure: function () {
                        XD.msg('归还管理归还失败');
                    }
                });
            },
            failure: function () {
                XD.msg('操作中断');
            }
        });
    },

    itemClickHandler:function(view, record, item){//点击打开入库记录，显示电子条目
        var historyView = view.up('historyView');
        var inid = record.get('inid');
        var win = this.getView('InWareDetailView').create({
            title:'详细条目信息',
            modal: true,
            //resizable: false,
            width:'85%',
            height:'75%',
            itemId:'detailView',
            baseProperty: inid
        });
        historyView.detailView = win;
        var button=win.down('[itemId=basicgridCloseBtn]');//关闭所有页面按钮
        button.hide();//隐藏【关闭】按钮
        win.show();
        var gridcard=win.down('[itemId=detailGridView]');
        gridcard.initGrid({nodeid:templateNodeid});

    },

    entryItemClickHandler:function(view, record, item){
        var archivecode = record.get('archivecode');

       /* var nw=window.open("/QRcode/main?archivecode=" + archivecode);
        nw.document.title = '库存档案详细信息.....';
        //window.open("/QRcode/main");*/

        var entityDetailView = new Ext.create('ReturnWare.view.EntityDetailView');
        entityDetailView .show();

        //档案详细页面
        var ran=Math.random();
        document.getElementById("frame1").src ="/QRcode/main?archivecode="+archivecode+'&t='+ran;
    },

    jargonHandler:function (btn) {
        formvisible = true;
        formlayout = 'formgrid';
        var managementform = this.findFormView(btn);
        managementform.down('electronic').operateFlag='add';
        managementform.saveBtn = managementform.down('[itemId=save]');
        managementform.continueSaveBtn = managementform.down('[itemId=continuesave]');
        managementform.operateFlag = 'add';
        var grid = this.getGrid(btn);
        var form = managementform.down('dynamicform');
        var tree = this.findGridView(btn).down('treepanel');
        var selectCount = grid.selModel.getSelection().length;
        var selectAll = grid.down('[itemId=selectAll]').checked;
        if (selectAll) {
            selectCount = grid.selModel.selected.length;//当前页选中
        }
        var node = tree.selModel.getSelected().items[0];
        if(!node){//若点击著录时左侧未选中任何节点，则提示选择节点
            XD.msg('请选择节点');
            return;
        }
        var initFormFieldState = this.initFormField(form, 'show', node.get('fnid'));
        form.down('[itemId=preNextPanel]').setVisible(false);
        var codesetting = this.getCodesetting(node.get('fnid'));
        var nodename = this.getNodename(node.get('fnid'));
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
                if (selectCount == 0) {
                    this.initFormData('add',form,'','案卷著录');
                    this.activeForm(form);
                    this.initSouthGrid(form);
                }else if(selectCount!=1){
                    XD.msg('只能选择一条数据')
                } else {
                    //选择数据著录，则加载当前数据到表单界面
                    var entryid;
                    if (selectAll) {
                        entryid = grid.selModel.selected.items[0].get("entryid");
                    } else {
                        entryid = grid.selModel.getSelection()[0].get("entryid");
                    }
                    this.initFormData('add', form, entryid,'案卷数据著录');
                    this.activeForm(form);
                    this.initSouthGrid(form);
                }
                if(form.operateType){
                    form.operateType = undefined;
                }
            }
        }else{
            XD.msg('请检查档号模板信息是否正确');
        }
    },

    //高级检索
    advancedSearch:function (btn) {
        var grid = this.getGrid(btn);
        var gridview = btn.findParentByType('panel');
        if(grid.down('[itemId=advancedsearch]').getText()=='取消高级检索'){
            grid.down('[itemId=advancedsearch]').setText('高级检索');
            //取消高级检索，需要把标红清空
            Ext.Array.each(grid.getColumns(), function(){
                var column = this;
                if(column.renderer != false && column.renderer.isSearchRender){
                    column.renderer = false;
                }
            });
            grid.notResetInitGrid({nodeid:grid.nodeid});
        }else{
            var tree = this.findGridView(btn).down('treepanel');
            var node = tree.selModel.getSelected().items[0];
            if(!node){
                XD.msg('请选择节点');
                return;
            }
            var advancedSearchFormWin = Ext.create('Ext.window.Window',{
                width:'100%',
                height:'100%',
                title:'高级检索',
                draggable : false,//禁止拖动
                resizable : false,//禁止缩放
                modal:true,
                closeToolText:'关闭',
                layout:'fit',
                items:[{
                    xtype: 'advancedSearchFormView',
                    grid:grid,
                    //nodeid:grid.nodeid
                    nodeid:NodeIdf
                }]
            });
            var advancedSearchDynamicForm = advancedSearchFormWin.down('advancedSearchDynamicForm');
            this.initAdvancedSearchFormField(advancedSearchDynamicForm,NodeIdf);
            advancedSearchFormWin.show();
        }
    },

    initAdvancedSearchFormField:function(form, nodeid){
        if (form.nodeid != nodeid) {//切换节点后，form和tree的节点id不相等
            form.nodeid = nodeid;//用左侧树节点的id初始化form的nodeid参数
            form.removeAll();//移除form中的所有表单控件
            var formField = form.getFormField();//根据节点id查询表单字段
            formField.type = '高级检索';
            if(formField.length==0){
                XD.msg('请检查模板设置信息是否正确');
                return;
            }
            form.templates = formField;
            form.initSearchConditionField(formField);//重新动态添加表单控件
        }
        return '加载表单控件成功';
    },

    deleteEntry:function (btn) {
        var record = btn.findParentByType('ViewInwareDetail').getSelection();
        if(record.length == 0){
            XD.msg('请至少选择一条需要删除的数据');
            return;
        }
        var array = [];
        for (var i = 0; i < record.length; i++) {
            array[i] = record[i].get('entryid');
        }
        Ext.Msg.wait('正在进行删除操作，请耐心等待……','正在操作');
        Ext.Ajax.request({
            url:'/management/deleteInwares',//删除操作
            method: 'POST',
            timeout:XD.timeout,
            params:{
                entryids:array
            },
            success:function(response) {
                var resp = Ext.decode(response.responseText);
                if(resp.success==false){
                    Ext.Msg.wait('删除操作中断','正在操作').hide();
                    Ext.MessageBox.alert("提示信息", resp.msg, function(){
                    });
                }else{
                    Ext.Msg.wait('删除操作完成','正在操作').hide();
                    XD.msg("删除成功");
                    btn.findParentByType('ViewInwareDetail').getStore().reload();
                }
            }
        });
    },

    doAdvancedSearch:function (btn) {//高级检索页面查询方法
        /*查询参数处理*/
        var form = btn.up('window').down('advancedSearchFormView');
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
        var grid = form.grid;
        //检索数据前,修改column的renderer，将检索的内容进行标红
        Ext.Array.each(grid.getColumns(), function(item){
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
        //formParams.nodeid = grid.nodeid;
        formParams.nodeid = NodeIdf;
        //点击非叶子节点时，是否查询出其包含的所有叶子节点数据
        formParams.ifSearchLeafNode = false;
        //点击非叶子节点时，是否查询出当前非叶子节点及其包含的所有非叶子节点数据
        formParams.ifContainSelfNode = false;
        /*切换至列表界面(关闭表单界面)*/
        btn.up('window').close();
        /*加载页面*/
        //检索数据前,修改column的renderer，将检索的内容进行标红
        grid.dataParams=formParams;
        grid.down('[itemId=advancedsearch]').setText('取消高级检索');
        if(fieldColumn.length==0){
            grid.notResetInitGrid(formParams)
        }else{
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
                nodeid: grid.nodeid,
                fieldColumn: fieldColumn,
                fieldValue: fieldValue,
                type: '高级检索'
            },
            success:function(res){
            },
            failure:function(){
                Ext.MessageBox.hide();
                XD.msg('操作失败！');
            }
        });
    },

    doAdvancedSearchClear:function(btn){//清除检索条件页面所有控件的输入值
        Ext.Ajax.request({
            url: '/classifySearch/clearSearchInfo',
            async:false,
            params:{
                nodeid:NodeIdf,
                type:'高级检索'
            },
            success: function (response) {
                btn.up('window').down('advancedSearchFormView').getForm().reset();//表单重置
            },
            failure:function(){
                XD.msg('操作失败！');
            }
        });
    },

    doAdvancedSearchClose:function (btn) {
        btn.up('window').close();
    },

    //切换到列表界面视图
    activeGrid: function (btn, flag) {
        var view = this.findView(btn);
        view.setActiveItem(this.findGridView(btn));
        if(flag){//根据参数确定是否需要刷新数据
            var grid = this.findActiveGrid(btn);
//            grid.initGrid();
            grid.notResetInitGrid();
        }
    },

    //保存表单数据，返回列表界面视图
    submitForm:function(btn){
        var currentManagementform = this.getCurrentManagementform(btn);
        var eleids = currentManagementform.down('electronic').getEleids();
        var formview = currentManagementform.down('dynamicform');
        //字段编号，用于特殊的自定义字段(范围型日期)
        var nodename = this.getNodename(formview.nodeid);
        var fieldCode = formview.getRangeDateForCode();
        var params = {
            nodeid: formview.nodeid,
            eleid: eleids,
            type: currentManagementform.operateFlag,
            operate: nodename
        };
        if (fieldCode != null) {
            params[fieldCode]=formview.getDaterangeValue();
        }
        var archivecodeSetState = formview.setArchivecodeValueWithNode(nodename);
        if(!archivecodeSetState){//若档号设置失败，则停止后续的表单提交
            return;
        }
        var operateType = formview.operateType;
        var submitType = formview.submitType;
        Ext.MessageBox.wait('正在保存请稍后...','提示');
        formview.submit({
            method:'POST',
            url: '/management/entries',
            params:params,
            scope:this,
            success: function (form,action) {
                Ext.MessageBox.hide();
                var treepanel = this.findTreeView(btn);
                var nodeid = treepanel.selModel.getSelected().items[0].get('fnid');
                if(action.result.success==true){
                    if(operateType=='insertion'){//插件、插卷
                        var pages = action.result.data.pages;
                        var state = this.updateSubsequentData(this.entryID,submitType,pages);
                        //切换到列表界面,同时刷新列表数据
                        if(formview.nodeid != nodeid){
                            this.activeGrid(btn, false);
                            this.findInnerGrid(btn).getStore().reload();
                        }else{
                            this.activeGrid(btn,true);
                            //this.findInnerGrid(btn).getStore().removeAll();
                        }
                        if(!state){
                            return;//保存条目成功，但插件后更新后续数据计算项及档号失败
                        }
                        //XD.msg(action.result.msg);//避免两个提示同时出现
                    }
                    //多条时切换到下一条。单条时或最后一条时切换到列表界面,同时刷新列表数据
                    if(formview.entryids && formview.entryids.length > 1 && formview.entryid != formview.entryids[formview.entryids.length-1]){
                        var allMediaFrame = document.querySelectorAll('#mediaFrame');
                        if(allMediaFrame){
                            for (var i = 0; i < allMediaFrame.length; i++) {
                                allMediaFrame[i].setAttribute('src','');
                            }
                        }
                        this.refreshFormData(formview, 'next');
                    }else{
                        if(formview.nodeid != nodeid){
                            this.activeGrid(btn, false);
                            this.findInnerGrid(btn).getStore().reload();
                        }else{
                            this.activeGrid(btn,true);
                            //this.findInnerGrid(btn).getStore().removeAll();
                        }
                    }
                    XD.msg(action.result.msg);
                }
            },
            failure: function (form, action) {
                Ext.MessageBox.hide();
                XD.msg(action.result.msg);
            }
        });
        if(formview.operateType){
            formview.operateType = undefined;
        }
    }
})