/**
 * Created by tanly on 2017/11/1 0024.
 */
Ext.define('Classificationsetting.controller.ClassificationsettingController', {
    extend: 'Ext.app.Controller',

    views: ['ClassificationsettingView', 'ClassificationsettingGridView', 'ClassificationsettingPromptView',
        'ClassPreviewNodeView','ClassificationsettingSequenceView','ClassificationsettingSequenceGridView',
        'NodeMediaSettingView'],//加载view
    stores: ['ClassificationsettingTreeStore', 'ClassificationsettingGridStore','ClassificationsettingPreviewTreeStore','ClassificationsettingSequenceStore'],//加载store
    models: ['ClassificationsettingTreeModel', 'ClassificationsettingGridModel'],//加载model
    init: function () {
        var ifShowRightPanel = false;
        var ifShowRightSxPanel = false;
        var classgridView;
        this.control({
            'classificationsettingView':{
                tabchange:function(view){
                    if(view.activeTab.title == '档案系统'){
                        var treeView = view.down('[itemId=treepanelDaId]');
                        treeView.getStore().proxy.extraParams.pcid = '';
                        treeView.getStore().proxy.url = '/nodesetting/getClassificationByParentClassId';
                        treeView.getStore().proxy.extraParams.xtType = '档案系统';
                        treeView.getStore().load();
                        window.classViewTab='档案系统';
                        ifShowRightPanel = false;
                    }else if(view.activeTab.title == '声像系统'){
                        var treeView = view.down('[itemId=treepanelSxId]');
                        treeView.getStore().proxy.extraParams.pcid = '';
                        treeView.getStore().proxy.url = '/nodesetting/getClassificationByParentClassId';
                        treeView.getStore().proxy.extraParams.xtType = '声像系统';
                        treeView.getStore().load();
                        window.classViewTab='声像系统';
                        ifShowRightSxPanel = false;
                    }
                }
            },
            'classificationsettingView [itemId=treepanelDaId]': {
                select: function (treemodel, record) {
                    window.treesettingview = treemodel.view;
                    var classView = treemodel.view.up('[itemId=daxtId]');
                    if (!ifShowRightPanel) {
                        classView.remove(classView.down('[itemId=gridDaId]'));
                        classView.add({
                            xtype: 'classificationsettingGridView'
                        });
                        ifShowRightPanel = true;
                    }
                    var gridView = classView.down('[itemId=classChangeViewID]');
                    gridView.setTitle("当前位置：" + record.get('text'));
                    gridView.initGrid({classificationid: record.get('fnid'),xtType:window.classViewTab});
                }
            },
            'classificationsettingView [itemId=treepanelSxId]': {
                select: function (treemodel, record) {
                    window.treesettingview = treemodel.view;
                    var classView = treemodel.view.up('[itemId=sxxtId]');
                    if (!ifShowRightSxPanel) {
                        classView.remove(classView.down('[itemId=gridSxId]'));
                        classView.add({
                            xtype: 'classificationsettingGridView'
                        });
                        ifShowRightSxPanel = true;
                    }
                    var gridView = classView.down('[itemId=classChangeViewID]');
                    gridView.setTitle("当前位置：" + record.get('text'));
                    gridView.initGrid({classificationid: record.get('fnid'),xtType:window.classViewTab});
                }
            },
            'classificationsettingGridView': {
                beforedrop: function (node, data, overmodel, position, dropHandlers) {
                    dropHandlers.cancelDrop();
                    if (data.records.length > 1) {
                        XD.msg('不支持批量选择拖拽排序，请选择一条数据');
                    } else {
                        XD.confirm('确认将分类[ ' + data.records[0].get('classname') + ' ]移动到[ '
                            + overmodel.get('classname') + ' ]的' + ("before" == position ? '前面吗' : '后面吗'), function () {
                            var overorder = overmodel.get('sortsequence');
                            var target;
                            if (typeof(overorder) == 'undefined') {
                                target = -1;
                            } else if ("before" == position) {
                                target = overorder;
                            } else if ("after" == position) {
                                target = overorder + 1;
                            }
                            Ext.Ajax.request({
                                url: '/classificationsetting/classification/' + data.records[0].get('classid') + '/'+overmodel.get('classid')+'/'+target,
                                method: 'post',
                                success: function () {
                                    data.view.getStore().reload();
                                    XD.msg('顺序修改成功');
                                    var dragzone = Ext.getCmp(data.view.id).getPlugins()[0].dropZone;
                                    dragzone.invalidateDrop();
                                    dragzone.handleNodeDrop(data, overmodel, position);
                                    dragzone.fireViewEvent('drop', node, data, overmodel, position);

                                    var treeStore = window.treesettingview.getStore();
                                    if (window.treesettingview.selection.get('expanded')) {
                                        treeStore.proxy.extraParams.pcid = window.treesettingview.selection.get('fnid');
                                        treeStore.load({node: window.treesettingview.selection, scope: this});
                                    } else {//未展开过时，重新load已选节点，会导致+号消失，无法展开子节点，故做判断处理
                                        var selected = window.treesettingview.selection;
                                        var selectedParent = window.treesettingview.selection.parentNode;
                                        treeStore.proxy.extraParams.pcid = window.treesettingview.selection.parentNode.get('fnid');
                                        treeStore.load({
                                            node: window.treesettingview.selection.parentNode,
                                            scope: this,
                                            callback: function () {
                                                var childNodes = selectedParent.childNodes;
                                                for (var j = 0; j < childNodes.length; j++) {
                                                    if (childNodes[j].get('fnid') === selected.get('fnid')) {
                                                        window.treesettingview.getSelectionModel().select(childNodes[j]);//重新选中
                                                        break;
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        });
                    }
                }
            },
            'classificationsettingGridView button[itemId=addclassbtnid]': {
                click: function (view) {
                    var win = new Ext.create('Classificationsetting.view.ClassificationsettingDetailView', {
                        title: '增加分类',
                        classview: view.findParentByType('classificationsettingView'),
                        needValid: true
                    });
                    win.show();
                }
            },
            'classificationsettingGridView button[itemId=updateclassbtnid]': {
                click: function (view) {
                    var classificationsettingGridView = view.findParentByType('classificationsettingGridView');
                    var select = classificationsettingGridView.getSelectionModel();
                    if (select.getSelection().length != 1) {
                        XD.msg("请选择一条操作记录!");
                    } else {
                        var win = new Ext.create('Classificationsetting.view.ClassificationsettingDetailView', {
                            title: '修改分类',
                            classview: view.findParentByType('classificationsettingView'),
                            needValid: true
                        });
                        win.down('form').loadRecord(select.getLastSelected());
                        var classlevel = select.getLastSelected().get('classlevel');
                        if (classlevel === "卷内管理") {
                            win.down('[itemId=classlevelItem]').setValue(1);
                        } else if (classlevel === "案卷管理") {
                            win.down('[itemId=classlevelItem]').setValue(2);
                        } else if (classlevel === "未归管理") {
                            win.down('[itemId=classlevelItem]').setValue(3);
                        } else if (classlevel === "已归管理") {
                            win.down('[itemId=classlevelItem]').setValue(4);
                        } else if (classlevel === "资料管理") {
                            win.down('[itemId=classlevelItem]').setValue(5);
                        } else if (classlevel === "文件管理") {
                            win.down('[itemId=classlevelItem]').setValue(6);
                        } else if (classlevel === "全宗卷管理") {
                            win.down('[itemId=classlevelItem]').setValue(7);
                        } else if (classlevel === "编研采集") {
                            win.down('[itemId=classlevelItem]').setValue(8);
                        }
                        else {
                            win.down('[itemId=classlevelItem]').setValue('');
                        }
                        win.show();
                    }
                }
            },
            //删除
            'classificationsettingGridView button[itemId=deleteclassbtnid]': {
                click: function (view) {
                    var classGridView = view.findParentByType('classificationsettingGridView');
                    var select = classGridView.getSelectionModel();
                    if (!select.hasSelection()) {
                        XD.msg("请选择操作记录!");
                    } else {
                        var gridselections = select.getSelection();
                        var array = new Array();
                        var idStr='';
                        if(gridselections.length>50){
                            XD.msg("不支持超过50个记录的删除!");
                            return;
                        }
                        for (var i = 0; i < gridselections.length; i++) {
                            array[i] = gridselections[i].get("classid");
                            idStr+=gridselections[i].get("classid")+',';
                        }
                        idStr=idStr.substring(0,idStr.length-1);
                        Ext.Msg.wait('正在检测是否存在关联数据，请稍等......','正在操作');
                        Ext.Ajax.request({
                            params: {ids:array},
                            url: '/classificationsetting/deleteValidate',
                            method: 'post',
                            timeout:XD.timeout,
                            success: function (resp) {
                                var respText = Ext.decode(resp.responseText);
                                Ext.Msg.hide();
                                if(!respText.success){
                                    var win = new Ext.create('Classificationsetting.view.ClassPreviewNodeView', {
                                        height:window.innerHeight * 6 / 7,
                                        width:400,
                                        ids:array,
                                        idStr:idStr,
                                        classGridView:classGridView
                                    });
                                    win.down('[itemId=classSaveBtnID]').setText('删除');
                                    var treeView = win.down('treepanel');
                                    treeView.getStore().proxy.extraParams = {ids:array,xtType:window.classViewTab};
                                    treeView.getStore().proxy.url = '/classificationsetting/deletePreview';
                                    //treeView.getStore().proxy.aysnc=false;
                                    treeView.on('render', function (treeView) {
                                        treeView.getStore().load();
                                    });

                                     //var d = new Ext.util.DelayedTask(function () {
                                         win.show();
                                         //Ext.Msg.wait('检测完毕','正在操作').hide();
                                     //});
                                    //d.delay(300);
                                    window.organPreviewParams=treeView.getStore().proxy.extraParams;
                                }else{
                                    XD.msg("所选分类中仍存在关联得条目数据，请勿删除");
                                }
                            },
                            failure: function () {
                                Ext.Msg.hide();
                                XD.msg('操作中断');
                            }
                        });
                    }
                }
            },
            'classificationsettingGridView button[itemId=isMedia]':{
                click:this.setMedia
            },
            'nodeMediaSettingView button[itemId=setMedia]':{
                click:this.saveMedia
            },
            //调序
            'classificationsettingGridView button[itemId=sequence]': {
                click: function (view) {
                    var classGridView = view.findParentByType('classificationsettingGridView');
                    classgridView = view.findParentByType('classificationsettingGridView');
                    var select = classGridView.getSelectionModel();
                    if (!select.hasSelection()) {
                        XD.msg('请选择操作记录');
                        return;
                    } else {
                        var gridselections = select.getSelection();
                        var array = [];
                        for (var i = 0; i < gridselections.length; i++) {
                            array.push(gridselections[i].get("classid"));
                        }

                    }
                    var sequenceClass = Ext.create('Ext.window.Window',{
                        width:'60%',
                        height:'75%',
                        modal:true,
                        title:'分类调序',
                        closeToolText:'关闭',
                        closeAction:'hide',
                        layout:'fit',
                        items:[{
                            xtype: 'classificationsettingSequenceView'//调序视图
                        }]
                    });
                    sequenceClass.show();
                    window.sequenceClass=sequenceClass;
                    var view = sequenceClass.down('classificationsettingSequenceView').down('classificationsettingSequenceGridView');
                    view.initGrid({classid: array});
                }
            },
            'classificationsettingSequenceView button[itemId="up"]': {
                click: function (view) {
                    var grid = view.findParentByType('classificationsettingSequenceView').down('classificationsettingSequenceGridView');
                    var record = grid.selModel.getSelection();//当前选择的数据
                    if (record.length < 1) {
                        XD.msg('请选择一条需要上调的数据');
                        return;
                    } else if (record.length > 1) {
                        XD.msg('只能选择一条数据进行操作');
                        return;
                    }
                    var count = grid.getStore().getTotalCount();
                    var recordall = grid.getStore().getRange(0,count);
                    var currentcount = 0;
                    for(var i=0;i<count;i++){
                        if(record[0]==recordall[i]){
                            currentcount = i;
                        }
                    }
                    if(currentcount==0){
                        XD.msg('当前选择的是第一条数据无法进行上调操作');
                        return;
                    }
                    var array = [];
                    for (var i = 0; i < recordall.length; i++) {
                        array.push(recordall[i].get("classid"));
                    }
                    Ext.Ajax.request({
                        method: 'post',
                        url: '/classificationsetting/classsortsequence',
                        params: {
                            classid: array,
                            currentcount:currentcount,
                            operate:'up'
                        },
                        success: function (response) {
                            grid.getStore().load(function () {
                                var select;
                                for(var i=0;i<grid.getStore().getCount();i++){
                                    var storeRe = grid.getStore().getAt(i);
                                    if(record[0].get('classid')==storeRe.get('classid')){
                                        select = storeRe;
                                        break;
                                    }
                                }
                                grid.getSelectionModel().select(select);
                                grid.fireEvent('itemclick', grid, select);
                            });
                            var treeStore = window.treesettingview.getStore();
                            if (window.treesettingview.selection.get('expanded')) {
                                treeStore.proxy.extraParams.pcid = window.treesettingview.selection.get('fnid');
                                treeStore.load({node: window.treesettingview.selection, scope: this});
                            } else {//未展开过时，重新load已选节点，会导致+号消失，无法展开子节点，故做判断处理
                                var selected = window.treesettingview.selection;
                                var selectedParent = window.treesettingview.selection.parentNode;
                                treeStore.proxy.extraParams.pcid = window.treesettingview.selection.parentNode.get('fnid');
                                treeStore.load({
                                    node: window.treesettingview.selection.parentNode,
                                    scope: this,
                                    callback: function () {
                                        var childNodes = selectedParent.childNodes;
                                        for (var j = 0; j < childNodes.length; j++) {
                                            if (childNodes[j].get('fnid') === selected.get('fnid')) {
                                                window.treesettingview.getSelectionModel().select(childNodes[j]);//重新选中
                                                break;
                                            }
                                        }
                                    }
                                });
                            }
                        },
                        failure:function(response){
                            XD.msg('操作失败');
                        }
                    });

                }
            },

            'classificationsettingSequenceView button[itemId="down"]': {
                click: function (view) {
                    var grid = view.findParentByType('classificationsettingSequenceView').down('classificationsettingSequenceGridView');
                    var record = grid.selModel.getSelection();//当前选择的数据
                    if (record.length < 1) {
                        XD.msg('请选择一条需要下调的数据');
                        return;
                    } else if (record.length > 1) {
                        XD.msg('只能选择一条数据进行操作');
                        return;
                    }
                    var count = grid.getStore().getTotalCount();
                    var recordall = grid.getStore().getRange(0,count);
                    var currentcount = 0;
                    for(var i=0;i<count;i++){
                        if(record[0]==recordall[i]){
                            currentcount = i;
                        }
                    }
                    if(currentcount==count-1){
                        XD.msg('当前选择的是最后一条数据无法进行下调操作');
                        return;
                    }
                    var array = [];
                    for (var i = 0; i < recordall.length; i++) {
                        array.push(recordall[i].get("classid"));
                    }
                    Ext.Ajax.request({
                        method: 'post',
                        url: '/classificationsetting/classsortsequence',
                        params: {
                            classid: array,
                            currentcount:currentcount,
                            operate:'down'
                        },
                        success: function (response) {
                            grid.getStore().load(function () {
                                var select;
                                for(var i=0;i<grid.getStore().getCount();i++){
                                    var storeRe = grid.getStore().getAt(i);
                                    if(record[0].get('classid')==storeRe.get('classid')){
                                        select = storeRe;
                                        break;
                                    }
                                }
                                grid.getSelectionModel().select(select);
                                grid.fireEvent('itemclick', grid, select);
                            });
                            var treeStore = window.treesettingview.getStore();
                            if (window.treesettingview.selection.get('expanded')) {
                                treeStore.proxy.extraParams.pcid = window.treesettingview.selection.get('fnid');
                                treeStore.load({node: window.treesettingview.selection, scope: this});
                            } else {//未展开过时，重新load已选节点，会导致+号消失，无法展开子节点，故做判断处理
                                var selected = window.treesettingview.selection;
                                var selectedParent = window.treesettingview.selection.parentNode;
                                treeStore.proxy.extraParams.pcid = window.treesettingview.selection.parentNode.get('fnid');
                                treeStore.load({
                                    node: window.treesettingview.selection.parentNode,
                                    scope: this,
                                    callback: function () {
                                        var childNodes = selectedParent.childNodes;
                                        for (var j = 0; j < childNodes.length; j++) {
                                            if (childNodes[j].get('fnid') === selected.get('fnid')) {
                                                window.treesettingview.getSelectionModel().select(childNodes[j]);//重新选中
                                                break;
                                            }
                                        }
                                    }
                                });
                            }
                        },
                        failure:function(response){
                            XD.msg('操作失败');
                        }
                    });

                }
            },

            'classificationsettingSequenceView button[itemId="back"]': {
                click: function (view) {
                    classgridView.getStore().reload();
                    window.sequenceClass.close();
                }
            },
            'classificationsettingDetailView button[itemId=classCancelBtnID]': {
                click: function (view) {
                    var classificationsettingDetailView = view.findParentByType('classificationsettingDetailView');
                    classificationsettingDetailView.close();
                }
            },
            'classPreviewNodeView button[itemId=classCancelBtnID]': {
                click: function (view) {
                    var view = view.findParentByType('classPreviewNodeView');
                    view.close();
                }
            },
            'classPreviewNodeView button[itemId=classSaveBtnID]': {
                click: function (view) {
                    var that=this;
                    var classPreviewView = view.findParentByType('classPreviewNodeView');
                    if(view.getText()==='删除'){
                        var classGridView = classPreviewView.classGridView;
                        var gridselections = classGridView.getSelectionModel().getSelection();
                        XD.confirm("是否确定删除该分类及其相关联数据", function () {
                            Ext.Msg.wait('正在进行删除操作，请耐心等待……','正在操作');
                            Ext.Ajax.request({
                                params: {ids: classPreviewView.ids,xtType:window.classViewTab},
                                url: '/classificationsetting/deleteClass',
                                method: 'post',
                                sync: true,
                                timeout:15000000,
                                success: function () {
                                    classGridView.delReload(gridselections.length, function () {
                                        var newnode = {
                                            text: window.treesettingview.selection.get('text'),
                                            leaf: true,//改变样式
                                            fnid: window.treesettingview.selection.get('fnid')
                                        };
                                        if (classGridView.getStore().getTotalCount() == 0) {//无子
                                            var selectid = window.treesettingview.selection.get('fnid');
                                            var parentnode = window.treesettingview.selection.parentNode;
                                            if (parentnode != null) {
                                                parentnode.replaceChild(newnode, window.treesettingview.selection);
                                                var childNodes = parentnode.childNodes;
                                                for (var j = 0; j < childNodes.length; j++) {
                                                    if (childNodes[j].get('fnid') == selectid) {
                                                        window.treesettingview.getSelectionModel().select(childNodes[j]);//重新选中
                                                        break;
                                                    }
                                                }
                                            } else {//根节点
                                                window.treesettingview.selection.removeAll();
                                            }
                                        } else {
                                            var childNodes = window.treesettingview.selection.childNodes;
                                            if (childNodes.length > 0) {
                                                for (var i = 0; i < gridselections.length; i++) {
                                                    var selectid = gridselections[i].get('classid');
                                                    for (var j = 0; j < childNodes.length; j++) {
                                                        if (childNodes[j].get('fnid') == selectid) {
                                                            childNodes[j].remove();
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    });
                                    var treeStore = window.treesettingview.getStore();
                                    treeStore.load({node: window.treesettingview.selection, scope: this});
                                    Ext.Msg.hide();
                                    classPreviewView.close();
                                    XD.msg("删除成功");
                                    /*if(window.classViewTab=='声像系统'){
                                        var url=responseText.data.codelevel;
                                        url=url+'?szType=B2&classid='+classPreviewView.idStr+'&userid='+syncUserid;//删除分类B2
                                        //发送跨域请求
                                        that.crossDomainByUrl(url);
                                    }*/
                                }
                            });
                        });
                    }else{
                        Ext.Msg.hide();
                        var classDetailView =  classPreviewView.classDetailView;
                        var form = classDetailView.down('form');
                        var URL = '/classificationsetting/updateClass';
                        var classid = window.treesettingview.selection.get('fnid');
                        var params = {parentclassid_real: classid,xtType:window.classViewTab};
                        if (classDetailView.title === '增加分类') {
                            URL = '/classificationsetting/addClass';
                            params = {
                                xtType:window.classViewTab,
                            	parentclassid_real: classid
                            }//window.fnid
                        }
                        form.submit({
                            waitTitle: '提示',
                            waitMsg: '正在提交数据请稍后...',
                            url: URL,
                            method: 'POST',
                            params: params,
                            success: function (form, action) {
                                var respText = Ext.decode(action.response.responseText);
                                if (respText.success == true) {
                                    var classgrid = classDetailView.classview.down('[itemId=classChangeViewID]');
                                    var childNodes = window.treesettingview.selection.childNodes;
                                    if (respText.msg == '增加成功') {
                                        classgrid.getStore().reload({
                                            callback: function () {
                                            }
                                        });//重置callback
                                        classDetailView.close();
                                        classPreviewView.close();
                                        if (window.treesettingview.selection.get('leaf') || childNodes.length > 0) {
                                            window.treesettingview.selection.insertChild(childNodes.length, {
                                                text: respText.data.classname,
                                                leaf: true,
                                                fnid: respText.data.classid,
                                                parentid: respText.data.parentclassid
                                            })
                                        }
                                        /*if(window.classViewTab=='声像系统'){
                                            var url=respText.data.codelevel;
                                            url=url+'?szType=B&classid='+respText.data.classid+'&userid='+syncUserid;//增加分类B
                                            //发送跨域请求
                                            that.crossDomainByUrl(url);
                                        }*/
                                    } else {//修改
                                        if (childNodes.length > 0) {
                                            var treeStore = window.treesettingview.getStore();
                                            if (window.treesettingview.selection.get('expanded')) {
                                                treeStore.proxy.extraParams.pcid = window.treesettingview.selection.get('fnid');
                                                treeStore.load({node: window.treesettingview.selection, scope: this});
                                            } else {
                                                var selected = window.treesettingview.selection;
                                                var selectedParent = window.treesettingview.selection.parentNode;
                                                treeStore.proxy.extraParams.pcid = window.treesettingview.selection.parentNode.get('fnid');
                                                treeStore.load({
                                                    node: window.treesettingview.selection.parentNode,
                                                    scope: this,
                                                    callback: function () {
                                                        var childNodes = selectedParent.childNodes;
                                                        for (var j = 0; j < childNodes.length; j++) {
                                                            if (childNodes[j].get('fnid') == selected.get('fnid')) {
                                                                window.treesettingview.getSelectionModel().select(childNodes[j]);//重新选中
                                                                break;
                                                            }
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                        classgrid.getStore().reload();
                                        classDetailView.close();
                                        classPreviewView.close();
                                        /*if(window.classViewTab=='声像系统'){
                                            var url=respText.data.codelevel;
                                            url=url+'?szType=B1&classid='+respText.data.classid;//修改分类B1
                                            //发送跨域请求
                                            that.crossDomainByUrl(url);
                                        }*/
                                    }
                                }
                                XD.msg(respText.msg);
                            },
                            failure: function (form, action) {
                                Ext.Msg.hide();
                            	var respText = Ext.decode(action.response.responseText);
                                XD.msg(respText.msg);
                            }
                        });
                    }
                }
            },
            'classificationsettingDetailView button[itemId=classPreviewBtnID]': {
                click: function (view) {
                    var classDetailView = view.findParentByType('classificationsettingDetailView');
                    var form = classDetailView.down('form');
                    var formname = form.down('[itemId=classnameitemid]').getValue();
                    var selectedId = window.treesettingview.selection.get('fnid');
                    var data = form.getValues();
                    if (data['classname'] === '' || data['code'] === '') {
                        XD.msg("有必填项未填写");
                        return;
                    }
                    var previewParams = {
                        classid:form.down('[itemId=classiditemid]').getValue(),
                        classname:formname,
                        previewType:'update'
                    };
                    if (classDetailView.title === '增加分类') {
                        previewParams = {
                            parentclassid_real: selectedId,
                            classname:formname,
                            previewType:'add'
                        }
                    }
                    if(!classDetailView.needValid){
                        openPreview(classDetailView,previewParams);
                    }else{
                        Ext.Ajax.request({
                            url: '/classificationsetting/classValid',
                            params: {
                                formname: formname,
                                parentid: selectedId
                            },
                            sync: true,
                            success: function (resp) {
                                if (Ext.decode(resp.responseText).success) {
                                    XD.msg("【"+window.treesettingview.selection.get('text')+"】下已有【"+formname+"】分类，请修改分类名称！");
                                } else {
                                    openPreview(classDetailView,previewParams);
                                }
                            }
                        });
                    }
                }
            },
            'classificationsettingDetailView': {
                render: function (view) {
                    if (view.title === '修改分类') {
                        view.needValid = false;
                        var nameItem = view.down('[itemId=classnameitemid]');
                        nameItem.on('change', function (self, newValue) {
                            var detailView = self.up('classificationsettingDetailView');
                            detailView.needValid = (newValue !== self.originalValue);
                        })
                    }
                }
            }
        });
    },

    //跨域请求
    crossDomainByUrl:function(url){
        var store = Ext.create('Ext.data.Store', {
            model: 'Classificationsetting.model.ClassificationsettingGridModel',
            autoLoad: false,
            proxy: new Ext.data.ScriptTagProxy({
                url: url
            }),
            reader: new Ext.data.JsonReader({
                rootProperty: 'objList'
            })
        });
        store.load();
    }
    ,
    saveMedia:function(view){
        var form = view.up('nodeMediaSettingView').down('form');
        form.submit({
            waitTitle: '提示',
            waitMsg: '正在提交数据请稍后...',
            url: '/nodesetting/updateClass',
            method: 'POST',
            success: function (form, action) {
                XD.msg(action.result.msg);
                view.up('nodeMediaSettingView').close();
                //刷新列表
                window.mediaGridView.getStore().reload();
            }})
    },
    setMedia:function (view) {
        var classificationsettingGridView = view.findParentByType('classificationsettingGridView');
        window.mediaGridView=classificationsettingGridView;
        var select = classificationsettingGridView.getSelectionModel();
        if (select.getSelection().length != 1) {
            XD.msg("请选择一条操作记录!");
        } else {
            var win = new Ext.create('Classificationsetting.view.NodeMediaSettingView', {
                title: '标识声像节点',
                classview: view.findParentByType('NodeMediaSettingView'),
                needValid: true
            });
            win.down('form').loadRecord(select.getLastSelected());
            if (loginname != 'xitong') {
                var organid = select.getLastSelected().get("organid");
                if (organid == undefined) {
                    XD.msg("非本单位分类不能修改!");
                    return;
                }
            }
            win.show();
        }
    }

});

function openPreview(classDetailView,params) {
    var win = new Ext.create('Classificationsetting.view.ClassPreviewNodeView', {
        height:window.innerHeight * 6 / 7,
        width:400,
        classDetailView: classDetailView
    });
    var treeView = win.down('[itemId=previewTreepanelId]');
    treeView.getStore().proxy.extraParams = params;
    treeView.getStore().proxy.extraParams.xtType =  window.classViewTab;
    treeView.getStore().proxy.url = '/classificationsetting/preview';
    treeView.on('render', function (treeView) {
        treeView.getStore().load();
    });
    win.show();
}