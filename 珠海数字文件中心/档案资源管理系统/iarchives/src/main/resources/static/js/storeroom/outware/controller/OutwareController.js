/**
 * Created by Rong on 2018/4/27.
 */
var selectBorrowcode = [];
var NodeIdf = "";
Ext.define('Outware.controller.OutwareController',{
    extend: 'Ext.app.Controller',

    views: ['OutwareView','OutwareTabView','HistoryView','TransferWareView','BorrowWareView',
        'ManagementView','ManagementGridView','OutWareDetailView','EntityDetailView','StBorrowdocGridView',
        'ViewOutwareDetail','AdvancedSearchFormView','ExportDetailView','StBorrowdocDetailGridView'],
    stores:['ManagementStore','OutwareStore','StBorrowdocGridStore','OutwareDetailStore',
        'StBorrowdocDetailGridStore'],
    model:['ManagementModel','OutwareModel','StBorrowdocGridModel','StBorrowdocDetailGridModel'],

    init:function(){
        this.control({
            'transferWareView button[itemId=save]':{
                click:this.saveHandler
            },'transferWareView button[itemId=dzbtn]':{
                click:this.chooseHandler
            },'transferWareView button[itemId=cancel]':{
                click:function(btn){
                    btn.findParentByType('transferWareView').close();
                    var grid;
                }
            },
            'historyView [itemId=outwaregrid]':{//点击打开出库记录，显示电子条目
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
            },
            'managementgridView [itemId=idsBack]':{
                click:this.addIdsHandler
            },'managementgridView [itemId=addOutwareBookmarks]':{
                click:this.addIdsware
            },'managementgridView [itemId=viewOutware]':{//选择电子档案-查看添加
                click:function (btn) {
                    var DetailsWin = Ext.create('Ext.window.Window',{
                        width:900,
                        height:530,
                        title:'查看出库',
                        layout:'fit',
                        modal:true,
                        closeToolText:'关闭',
                        closeAction:'hide',
                        items:[{
                            xtype: 'ViewOutwareDetail'
                        }]
                    });
                    var store = DetailsWin.down('ViewOutwareDetail').getStore();
                    store.reload();
                    DetailsWin.show();
                }
            },'ViewOutwareDetail button[itemId=deleteOutware]':{//选择电子档案-查看添加-删除
                click:this.deleteOutware
            }, 'managementgridView [itemId=advancedsearch]':{//选择电子档案-高级检索
                click:this.advancedSearch
            }, //高级检索
            'advancedSearchFormView [itemId=topSearchBtn]':{click:this.doAdvancedSearch},
            'advancedSearchFormView [itemId=bottomSearchBtn]':{click:this.doAdvancedSearch},
            'advancedSearchFormView [itemId=topClearBtn]':{click:this.doAdvancedSearchClear},
            'advancedSearchFormView [itemId=bottomClearBtn]':{click:this.doAdvancedSearchClear},
            'advancedSearchFormView [itemId=topCloseBtn]':{click:this.doAdvancedSearchClose},
            'advancedSearchFormView [itemId=bottomCloseBtn]':{click:this.doAdvancedSearchClose},
            'outwareTabView': {
                tabchange: function (view) {
                    if (view.activeTab.title == '出库历史记录查询') {
                        var gridcard=view.down('[itemId=outwaregrid]');
                        //gridcard.initGrid({nodeid:'4028802f5f90a1f7015f9102d29f00b0'});
                        gridcard.getStore().reload();
                    }
                },
                afterrender:function(view){
                    var transferWareView = view.down('transferWareView');
                    if(borrowCode!=''){  //实体出库
                        selectBorrowcode = borrowCode;
                        Ext.Ajax.request({
                            url:'/jyAdmins/getBorrowMsgEntryid',
                            params:{
                                borrowcodes:selectBorrowcode
                            },
                            success:function (resp) {
                                var entryMap = Ext.decode(resp.responseText);
                                var entrys = entryMap.hasEntrys;
                                var noentrys = entryMap.noEntrys;
                                if(noentrys.length>0){
                                    var msg ;
                                    for(var i=0;i<noentrys.length;i++){
                                        if(i==0){
                                            msg = '档号为：[' +noentrys[i].archivecode+"]";
                                        }else{
                                            msg = msg +"，["+noentrys[i].archivecode+"]";
                                        }
                                    }
                                    XD.msg(msg+'，'+noentrys.length+'条还未入库，成功选择'+entrys.length+"条");
                                }else{
                                    XD.msg('成功选择'+entrys.length+"条");
                                }
                                var ids;
                                var names;
                                if(entrys.length>0){
                                    for(var i=0;i<entrys.length;i++){
                                        if(i==0){
                                            ids = entrys[i].entryid;
                                            names = entrys[i].archivecode+' : '+entrys[i].title +'，库存位置：'+entrys[i].entrystorage;
                                        }else {
                                            ids = ids+','+entrys[i].entryid;
                                            names = names +'\n'+ entrys[i].archivecode+' : '+entrys[i].title+'，库存位置：'+entrys[i].entrystorage;
                                        }
                                    }
                                    transferWareView.down('[itemId=ids]').setValue(ids);
                                    transferWareView.down('[itemId=idsName]').setValue(names);
                                }else{
                                    selectBorrowcode=[];
                                }
                            },
                            failure:function () {
                                XD.msg('操作失败');
                            }
                        });
                        var waretype = transferWareView.down('[itemId=waretype]');
                        var store = waretype.getStore();
                        var selectStore;
                        for(var i=0;i<store.getCount();i++){
                            var record = store.getAt(i);
                            if(record.get('text')=='查档出库'){
                                selectStore = record;
                                break;
                            }
                        }
                        waretype.select(selectStore);
                    }
                }
            },
            'managementgridView [itemId=idsClose]':{
                click:function(btn) {
                    btn.findParentByType('managementView').close();
                }
            }/*,
            'outWareDetailView [itemId=detailGridView]':{//点击打开档案详细
                itemclick:this.entryItemClickHandler
            }*/,
            'transferWareView [itemId=selectBorrowdoc]':{//选择查档单据
                click:function (view) {
                    var transferWareView = view.findParentByType('transferWareView');
                    var borrowdocView = Ext.create("Ext.window.Window",{
                        title:'查档单据',
                        modal: true,
                        width:'100%',
                        height:'100%',
                        maximizable : true,
                        layout:"fit",
                        items:[{
                            xtype:"stBorrowdocGridView",
                            itemId:"stBorrowdocGridViewId"
                        }]
                    });
                    var stBorrowdocGridView = borrowdocView.down('stBorrowdocGridView');
                    var store = stBorrowdocGridView.getStore();
                    store.proxy.extraParams.outwarestate = "未借出";
                    store.proxy.extraParams.type = "实体查档";
                    store.load(function () {
                        stBorrowdocGridView.getSelectionModel().clearSelections();
                    });
                    stBorrowdocGridView.transferWareView = transferWareView;
                    borrowdocView.show();
                }
            },

            'stBorrowdocGridView [itemId=borrowdocOut]':{//单据出库
                click:function (view) {
                   var stBorrowdocGridView = view.findParentByType('stBorrowdocGridView');
                   var select = stBorrowdocGridView.getSelectionModel().getSelection();
                   if(select.length<1){
                       XD.msg('请至少选择一条数据');
                       return;
                   }
                   var borrowcodestr = selectBorrowcode;
                   for(var i=0;i<select.length;i++){
                       var replace = [];
                       for(var y=0;y<select.length;y++){
                           replace.push(select[y].get('borrowcode'))
                       }
                       var flag = true;
                       for(var j=0;j<borrowcodestr.length;j++){
                           if(select[i].get('borrowcode')==borrowcodestr[j]){
                               flag = false;
                               break;
                           }
                       }
                       if(flag){
                           selectBorrowcode = replace;
                       }
                   }
                    Ext.Ajax.request({
                        url:'/jyAdmins/getBorrowMsgEntryid',
                        params:{
                            borrowcodes:selectBorrowcode
                        },
                        success:function (resp) {
                            var entryMap = Ext.decode(resp.responseText);
                            var entrys = entryMap.hasEntrys;
                            var noentrys = entryMap.noEntrys;
                            if(noentrys.length>0){
                                var msg ;
                                for(var i=0;i<noentrys.length;i++){
                                    if(i==0){
                                        msg = '档号为：[' +noentrys[i].archivecode+"]";
                                    }else{
                                        msg = msg +"，["+noentrys[i].archivecode+"]";
                                    }
                                }
                                XD.msg(msg+'，'+noentrys.length+'条还未入库，成功选择'+entrys.length+"条");
                            }else{
                                XD.msg('成功选择'+entrys.length+"条");
                            }
                            var ids;
                            var names;
                            if(entrys.length>0){
                                for(var i=0;i<entrys.length;i++){
                                    if(i==0){
                                        ids = entrys[i].entryid;
                                        names = entrys[i].archivecode+' : '+entrys[i].title +'，库存位置：'+entrys[i].entrystorage;
                                    }else {
                                        ids = ids+','+entrys[i].entryid;
                                        names = names +'\n'+ entrys[i].archivecode+' : '+entrys[i].title+'，库存位置：'+entrys[i].entrystorage;
                                    }
                                }
                                stBorrowdocGridView.transferWareView.down('[itemId=ids]').setValue(ids);
                                stBorrowdocGridView.transferWareView.down('[itemId=idsName]').setValue(names);
                            }else{
                                selectBorrowcode=[];
                            }
                            view.findParentByType('window').close();
                        },
                        failure:function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'stBorrowdocGridView [itemId=lookDeatail]':{//查看单据详情
                click:function (view) {
                    var stBorrowdocGridView = view.findParentByType('stBorrowdocGridView');
                    var select = stBorrowdocGridView.getSelectionModel().getSelection();
                    if(select.length!=1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    var media = Ext.create("Ext.window.Window", {
                        width: '70%',
                        height: '60%',
                        title: '查看单据详情',
                        modal: true,
                        closeToolText: '关闭',
                        closeAction:'hide',
                        layout: 'fit',
                        items: [{
                            xtype: 'stBorrowdocDetailGridView'
                        }]
                    });
                    var stBorrowdocDetailGridView = media.down('stBorrowdocDetailGridView');
                    stBorrowdocDetailGridView.initGrid({borrowdocid:select[0].get('id'),type:'outware'});
                    media.show();
                }
            },

            'stBorrowdocGridView [itemId=back]':{//返回
                click:function (view) {
                    view.findParentByType('window').close();
                }
            },

            "outWareDetailView button[itemId=export]":{
                click:this.openExport
            },

            "exportDetailView button[itemId=SaveExport]":{
                click:this.exportDetail
            },

            "exportDetailView button[itemId=cancelExport]":{
                click:function (view) {
                    view.findParentByType('window').close();
                },

            },'outWareDetailView [itemId=lookDetail]':{
                click:function (view) {
                    var grid = view.up('outWareDetailView');
                    var records = grid.getSelectionModel().getSelection();
                    if (records.length == 0){
                        XD.msg('请至少选择一条数据');
                        return;
                    }
                    var archivecodeArr = [];
                    Ext.each(records,function (item) {
                        archivecodeArr.push(item.data.archivecode);
                    })
                    archivecodeArr = archivecodeArr.join();
                    /*var nw=window.open("/QRcode/main?archivecode=" + archivecode, 'newwindow', 'height=600, width=1200, top=0, left=0, toolbar=no, menubar=no, scrollbars=no, resizable=no, location=no, status=no');
                    nw.document.title = '库存档案详细信息.....';*/
                    //window.open("/QRcode/main");

                    var entityDetailView = new Ext.create('Outware.view.EntityDetailView');
                    entityDetailView .show();

                    //档案详细页面
                    var ran=Math.random();
                    document.getElementById("frame1").src ="/QRcode/main?archivecode="+archivecodeArr+'&t='+ran;
                }
            },
            'stBorrowdocDetailGridView [itemId=lookEntry]':{
                click:function (view) {
                    var grid = view.up('stBorrowdocDetailGridView');
                    var records = grid.getSelectionModel().getSelection();
                    if (records.length == 0){
                        XD.msg('请至少选择一条数据');
                        return;
                    }
                    var archivecodeArr = [];
                    Ext.each(records,function (item) {
                        if(""==item.data.archivecode||null==item.data.archivecode){
                            archivecodeArr.push("id-"+item.data.entryid);
                        }else {
                            archivecodeArr.push(item.data.archivecode);
                        }
                    });
                    archivecodeArr = archivecodeArr.join();
                    var entityDetailView = new Ext.create('Outware.view.EntityDetailView');
                    entityDetailView .show();

                    //档案详细页面
                    var ran=Math.random();
                    document.getElementById("frame1").src ="/QRcode/main?archivecode="+archivecodeArr+'&t='+ran;
                }
            }
        });
    },


    openExport:function (view) {
        var inWareDetailView = view.findParentByType('outWareDetailView');
        var select = inWareDetailView.getSelectionModel().getSelection();
        if(select.length<1){
            XD.msg('请至少选择一条数据');
            return;
        }
        var ids = [];
        for(var i=0;i<select.length;i++){
            ids.push(select[i].get('entryid'));
        }
        var exportMissView = Ext.create('Outware.view.ExportDetailView');
        exportMissView.ids =ids;
        exportMissView.show();
    },
    exportDetail:function (view) {
        var ExportMissView = view.findParentByType('exportDetailView');
        var fileName = ExportMissView.down('[itemId=userFileName]').getValue();
        var zipPassword = ExportMissView.down('[itemId=zipPassword]').getValue();
        var b = ExportMissView.down('[itemId=addZipKey]').checked;
        var form = ExportMissView.down('[itemId=form]');

        if (fileName!=null&&fileName!="请输入..."&&fileName!="") {
            var pattern = new RegExp("[/:*?\"<>|]");
            if (pattern.test(fileName) || fileName.indexOf('\\') > -1) {
                XD.msg("文件名称不能包含下列任何字符：\\/:*?\"<>|");
                return;
            }
            if(zipPassword==""&&b){
                XD.msg("zip压缩密码不能为空");
                return;
            }
            Ext.MessageBox.wait('正在处理请稍后...');
            Ext.Ajax.request({
                method: 'post',
                url:'/export/exportInware',
                timeout:XD.timeout,
                scope: this,
                async:true,
                params: {
                    fileName:fileName,
                    zipPassword:zipPassword,
                    ids:ExportMissView.ids
                },
                success:function(res){
                    var obj = Ext.decode(res.responseText).data;
                    if(obj.entrySizeMsg=="NO"){
                        XD.msg('条目数超出限制，一次只支持导出50w的条目！');
                        Ext.MessageBox.hide();
                        return;
                    }
                    window.location.href="/export/downloadZipFile?fpath="+encodeURIComponent(obj.filePath)
                    Ext.MessageBox.hide();
                    XD.msg('文件生成成功，正在准备下载');
                    ExportMissView.close()
                },
                failure:function(){
                    Ext.MessageBox.hide();
                    XD.msg('文件生成失败');
                }
            });
        } else {
            XD.msg("文件名不能为空")
        }
    },

    saveHandler:function(btn){
        var textfil=btn.findParentByType('transferWareView').down('[itemId=ids]');
        var namefil=btn.findParentByType('transferWareView').down('[itemId=idsName]');
        var description=btn.findParentByType('transferWareView').down('[name=description]');
        var formpanel = btn.findParentByType('transferWareView').down('form');
        var ids=textfil.getValue();
        if (ids == '' || ids == null ) {XD.msg('请选择电子档案或查档单据');return;}
        formpanel.submit({
            url:'/outware/save',
            waitMsg: '正在出库，请稍后...',
            method : 'POST',
            scope:this,
            params:{
                borrowcodes:selectBorrowcode.join(","),
            },
            success : function(form, action) {
                var respText = Ext.decode(action.response.responseText);
                if (respText.success == true) {
                    XD.msg(respText.msg);
                    textfil.setValue('');
                    namefil.setValue('');
                    description.setValue("");
                    if(selectBorrowcode.length>0){
                        Ext.Ajax.request({
                            url:'/jyAdmins/setOutwareState',
                            params:{
                                borrowcodes:selectBorrowcode,
                                type:borrowCode!=''?'1':'0'
                            },
                            success:function (resp) {
                                selectBorrowcode = [];
                                if(borrowCode!=''){
                                    /*Ext.defer(function () {
                                        parent.closeObj.close(parent.layer.getFrameIndex(window.name));
                                    }, 1000);*/
                                }
                            },
                            failure:function () {
                                XD.msg('操作失败');
                            }
                        });
                    }
                    Ext.Ajax.request({
                        url:'/management/deleteOutwares',//删除操作
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

                    //打开密集架
                    var zone = respText.data.zone;
                    if(zone=='false'){
                        XD.msg("查找库存位置失败");
                        return;
                    }
                    var deviceid = respText.device;
                    var col = this.PrefixZero(respText.data.col, 2)
                    XD.confirm('是否打开'+zone.roomdisplay+'-'+zone.zonedisplay+'-'+col +'列? （注：多条目出库时，默认打开第一条条目的密集架列！）',function() {
                        Ext.Ajax.request({
                            method: 'POST',
                            url: '/deviceTask/openAssginColumnByDeviceId',
                            params:{deviceid:zone.device,col:col},
                            success:function (response) {
                                var obj = Ext.decode(response.responseText);
                                XD.msg(obj.msg);
                                Ext.defer(function () {
                                    parent.closeObj.close(parent.layer.getFrameIndex(window.name));
                                }, 1000);
                            },
                            failure:function () {
                                XD.msg('打开有异常');
                                Ext.defer(function () {
                                    parent.closeObj.close(parent.layer.getFrameIndex(window.name));
                                }, 1000);
                            }
                        });
                    },this);

                } else {
                    XD.msg(respText.msg);
                }
            },
            failure : function(form, action) {
                XD.msg('操作失败');
            }
        })
    },

    chooseHandler:function(btn){
        selectBorrowcode = [];
        var transferWareView = btn.up('transferWareView');
        var textfil=transferWareView.down('[itemId=ids]');
        var namefil=transferWareView.down('[itemId=idsName]');
        var entryid;
        var entrynames;
        var win = this.getView('ManagementView').create({
            title:'选择档案',
            modal: true,
            //resizable: false,
            width:'100%',
            height:'100%',
            maximizable : true,
            itemId:'chooseEntryView',
            listeners: {
                close: function (_this) {
                    Ext.Ajax.request({
                        url:'/management/findAddOutwares',
                        method:'POST',
                        success:function (response) {
                            var resp = Ext.decode(response.responseText);
                            var data = resp.data;
                            var entryid='';
                            var entryNames='';
                            if(data.length==1){
                                entryid = data[0].entryid;
                                entryNames = data[0].archivecode+' : '+data[0].title+';';
                            }else{
                                for (var i = 0; i < data.length; i++) {
                                    entryid=entryid+data[i].entryid+',';
                                    entryNames=entryNames+data[i].archivecode+' : '+data[i].title+';';
                                }
                                entryid=entryid.substring(0,entryid.length-1);
                                entryNames=entryNames.substring(0,entryNames.length-1);
                            }
                            textfil.setValue(entryid);
                            var temp=entryNames.split(';');
                            var nameStr='';
                            for (i=0;i<temp.length ;i++ ){
                                nameStr+=temp[i]+'\n'
                            }
                            namefil.setValue(nameStr);
                        }
                    });
                }
            }
        });
        //绑定子窗口到父窗口
        transferWareView.chooseEntryView = win;
        var button=win.down('[itemId=basicgridCloseBtn]');
        button.hide();//隐藏【关闭】按钮
        win.show();
        /*//初始化搜索条件下拉选择
        var gridcard=this.getView('ManagementView').down('[itemId=northgrid]');
        gridcard.initGrid({nodeid:'4028802f5f90a1f7015f9102d29f00b0'});*/
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
    //添加
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
        Ext.Msg.wait('正在进行添加操作，请耐心等待……', '正在操作');
        Ext.Ajax.request({
            url: '/management/setAddOutwares',
            method: 'POST',
            timeout: XD.timeout,
            params: {
                entryids: array
            },
            success: function (response) {
                var resp = Ext.decode(response.responseText);
                if (resp.success == false) {
                    Ext.Msg.wait('添加操作中断', '正在操作').hide();
                    Ext.MessageBox.alert("提示信息", resp.msg, function () {
                    });
                } else {
                    Ext.Msg.wait('添加操作完成', '正在操作').hide();
                    XD.msg("添加成功");
                }
            }
        });
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

    itemClickHandler:function(view, record, item){//点击打开出库记录，显示电子条目
        var historyView = view.up('historyView');
        var inid = record.get('outid');
        var win = historyView.down('outWareDetailView');
        historyView.detailView = win;
        var button=win.down('[itemId=basicgridCloseBtn]');//关闭所有页面按钮
        button.hide();//隐藏【关闭】按钮
        win.dataUrl = '/management/findOutWareEntry/'+inid;
        win.initGrid({nodeid:templateNodeid});
    },

    entryItemClickHandler:function(view, record, item){
        var archivecode = record.get('archivecode');
        /*var nw=window.open("/QRcode/main?archivecode=" + archivecode, 'newwindow', 'height=600, width=1200, top=0, left=0, toolbar=no, menubar=no, scrollbars=no, resizable=no, location=no, status=no');
        nw.document.title = '库存档案详细信息.....';*/
        //window.open("/QRcode/main");

        var entityDetailView = new Ext.create('Outware.view.EntityDetailView');
        entityDetailView .show();

        //档案详细页面
        var ran=Math.random();
        document.getElementById("frame1").src ="/QRcode/main?archivecode="+archivecode+'&t='+ran;
    },
    deleteOutware:function (btn) {
        var record = btn.findParentByType('ViewOutwareDetail').getSelection();
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
            url:'/management/deleteOutwares',//删除出库添加
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
                    btn.findParentByType('ViewOutwareDetail').getStore().reload();
                }
            }
        });
    }, advancedSearch:function (btn) {//出库，高级检索
        var grid = btn.findParentByType('managementgridView');
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
    },initAdvancedSearchFormField:function(form, nodeid){
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
    },doAdvancedSearch:function (btn) {//高级检索页面查询方法
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
    }, doAdvancedSearchClear:function(btn){//清除检索条件页面所有控件的输入值
        Ext.Ajax.request({
            url: '/classifySearch/clearSearchInfo',
            async:false,
            params:{
                nodeid:NodeIdf,
                type:'高级检索'
            },
            success: function (response) {
                var form=btn.up('window').down('advancedSearchDynamicForm');
                form.isFill?form._reset(form):form.getForm().reset();
            },
            failure:function(){
                XD.msg('操作失败！');
            }
        });
    },

    doAdvancedSearchClose:function (btn) {
        btn.up('window').close();
    },

    /**
     * 自定义函数名：PrefixZero
     * @param num： 被操作数
     * @param n： 固定的总位数
     */
    PrefixZero:function(num, n) {
        return (Array(n).join(0) + num).slice(-n);
    }
})