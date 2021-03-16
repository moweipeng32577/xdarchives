/**
 * Created by tanly on 2017/11/1 0024.
 */
Ext.define('Organ.controller.OrganController', {
    extend: 'Ext.app.Controller',

    views: ['OrganView', 'OrganTreeView', 'OrganGridView', 'OrganPromptView',
        'OrganPreviewNodeView', 'OrganSequenceView', 'OrganSequenceGridView',
        'OrganGridView','OrganTreeComboView','OrganPreviewNodeTabView'],
    stores: ['OrganTreeStore', 'OrganGridStore','OrganPreviewTreeStore','OrganSequenceStore'],
    models: ['OrganTreeModel', 'OrganGridModel'],
    init: function () {
        var ifShowRightPanel = false;
        var organgridView;
        var organview;
        this.control({
            'organTreeView': {
                select: function (treemodel, record) {
                    var organView = treemodel.view.findParentByType('organView');
                    organview = treemodel.view.findParentByType('organView');
                    var organPromptView = organView.down('[itemId=organPromptViewID]');
                    if (!ifShowRightPanel) {
                        organPromptView.removeAll();
                        organPromptView.add({
                            xtype: 'organGridView'
                        });
                        ifShowRightPanel = true;
                    }
                    var organgrid = organPromptView.down('[itemId=organGridViewID]');
                    if (record.get('text')==='机构设置') {
                        Ext.Ajax.request({
                            url: '/organ/topAdminCheck',
                            method: 'post',
                            sync: true,
                            success: function (resp) {
                                if(Ext.decode(resp.responseText).success){
                                    organgrid.down('button[itemId=addorganbtnid]').setDisabled(false);
                                    organgrid.down('button[itemId=deleteorganbtnid]').setDisabled(false);
                                }else{
                                    organgrid.down('button[itemId=addorganbtnid]').setDisabled(true);
                                    organgrid.down('button[itemId=deleteorganbtnid]').setDisabled(true);
                                }
                            },
                            failure: function () {
                                XD.msg('检验三员操作中断');
                            }
                        });
                    } else {
                        organgrid.down('button[itemId=addorganbtnid]').setDisabled(false);
                        organgrid.down('button[itemId=deleteorganbtnid]').setDisabled(false);
                    }
                    organgrid.setTitle("当前位置：" + record.get('text'));
                    organgrid.initGrid({organid: record.get('fnid')});
                }
            },
            'organGridView':{
                beforedrop: function (node, data, overmodel, position, dropHandlers) {
                    dropHandlers.cancelDrop();
                    if(data.records.length > 1){
                        XD.msg('不支持批量选择拖拽排序，请选择一条数据');
                    }else{
                        XD.confirm('确认将机构[ '+data.records[0].get('organname')+' ]移动到[ '
                            + overmodel.get('organname')+' ]的' + ("before" == position?'前面吗':'后面吗'),function(){
                            var overorder = overmodel.get('sortsequence');
                            var target;
                            if(typeof(overorder) == 'undefined'){
                                target = -1;
                            }else if("before" == position){
                                target = overorder;
                            }else if("after" == position){
                                target = overorder + 1;
                            }
                            Ext.Ajax.request({
                                url: '/organ/order/'+data.records[0].get('organid')+'/'+overmodel.get('organid')+'/'+target,
                                method: 'post',
                                success: function () {
                                    data.view.getStore().reload();
                                    XD.msg('顺序修改成功');
                                    var dragzone = Ext.getCmp(data.view.id).getPlugins()[0].dropZone;
                                    dragzone.invalidateDrop();
                                    dragzone.handleNodeDrop(data, overmodel, position);
                                    dragzone.fireViewEvent('drop', node, data, overmodel, position);

                                    var treeview = data.view.up('organView').down('[itemId=organTreeViewID]');
                                    var treeStore = treeview.getStore();
                                    if (treeview.selection.get('expanded')) {
                                        treeStore.proxy.extraParams.pcid = treeview.selection.get('fnid');
                                        treeStore.load({node: treeview.selection, scope: this});
                                    } else {//未展开过时，重新load已选节点，会导致+号消失，无法展开子节点，故做判断处理
                                        var selected = treeview.selection;
                                        var selectedParent = treeview.selection.parentNode;
                                        treeStore.proxy.extraParams.pcid = treeview.selection.parentNode.get('fnid');
                                        treeStore.load({
                                            node: treeview.selection.parentNode,
                                            scope: this,
                                            callback: function () {
                                                var childNodes = selectedParent.childNodes;
                                                for (var j = 0; j < childNodes.length; j++) {
                                                    if (childNodes[j].get('fnid') == selected.get('fnid')) {
                                                        treeview.getSelectionModel().select(childNodes[j]);//重新选中
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
            'organGridView [itemId=expOrgan]': {//导出机构
                click: function (btn) {
                    var organGridView = btn.up('organGridView');
                    var select = organGridView.getSelectionModel();
                    if (!select.hasSelection()) {
                        XD.msg('请选择机构条目后再进行操作！');
                        return;
                    }
                    var gridselections = select.getSelection();
                    var array = [];
                    for (var i = 0; i < gridselections.length; i++) {
                        array.push(gridselections[i].get("organid"));
                    }
                    var columnNames = ["机构名称", "机构(问题)代码",  "服务名称", "系统名称","机构类型", "状态","机构编码", "备注"];
                    var reqUrl = "/organ/expOrgan?columnNames=" + columnNames+"&ids="+array;
                    window.location.href = encodeURI(reqUrl);
                }
            },
            'organGridView [itemId=syncToSx]': {//同步机构到声像系统
                click: function (btn) {
                    XD.confirm('执行同步后，声像系统的数据节点会重新初始化，之前的数据和授权需要重新设置，是否确定同步', function () {
                        Ext.Msg.wait('正在进行同步机构操作，请耐心等待……','正在操作');
                        Ext.Ajax.request({
                            params: {initModel: 'classification-organ'},
                            url: '/nodesetting/changeSxTreeModel',
                            method: 'post',
                            sync: true,
                            timeout:XD.timeout,
                            success: function () {
                                Ext.Msg.wait('同步完成','正在操作').hide();
                                XD.msg('同步成功');
                            },
                            failure:function () {
                                Ext.Msg.wait('同步失败','正在操作').hide();
                                XD.msg('同步中断！');
                            }
                        });
                    });
                }
            },
            'organGridView button[itemId=addorganbtnid]': {
                click: function (view) {
                    var win = new Ext.create('Organ.view.OrganDetailView', {
                        title: '增加机构',
                        organview: view.findParentByType('organView'),
                        isPreview: false,
                        needValid: true
                    });
                    win.down('[itemId=useItemid]').setValue(true);
                    win.down('[itemId=typeItem]').setValue('unit');
                    win.show();
                }
            },
            'organGridView button[itemId=updateorganbtnid]': {
                click: function (view) {
                    var organGridView = view.findParentByType('organGridView');
                    var select = organGridView.getSelectionModel();
                    if (select.selected.items.length != 1) {
                        XD.msg('请选择一条操作记录');
                    } else {
                        var win = new Ext.create('Organ.view.OrganDetailView', {
                            title: '修改机构',
                            organview: organGridView.findParentByType('organView'),
                            isPreview: false,
                            needValid: true
                        });
                        win.down('form').loadRecord(select.getLastSelected());
                        if(select.getLastSelected().get('organtype')==='单位'){
                            win.down('[itemId=typeItem]').setValue('unit');
                        }else{
                            win.down('[itemId=typeItem]').setValue('department');
                        }
                        win.show();
                    }
                }
            },
            'organGridView [itemId=importOrgan]': {
                click: function (view) {
                    new Ext.create('Organ.view.OrganImportView',{
                        organView:view.findParentByType('organView')
                    }).show();
                }
            },
            'organGridView [itemId=exportOrgan]': {//导出机构模板
                click: function (btn) {
                    var columnNames = ["机构名称",'机构(问题)代码', "服务名称", "系统名称", "机构类型", "状态",  "机构编码", "上级机构","当前层顺序","机构层级","备注"];
                    var reqUrl = "/export/exportColumnNames?columnNames=" + columnNames;
                    window.location.href = encodeURI(reqUrl);
                }
            },
            'organImportView button[itemId=import]': {
                click: function (btn) {
                    var organImportView = btn.findParentByType('organImportView');
                    var form = organImportView.down('form');
                    if (!form.isValid()) {
                        XD.msg('有必填项没有填写，请处理后再提交');
                        return;
                    }
                    var parentid = form.getValues()['parentid'];
                    // XD.confirm('是否确认导入机构？', function () {
                        form.submit({
                            waitTitle: '提示',
                            waitMsg: '正在处理请稍后...',
                            url: '/organ/importOrgan',
                            method: 'POST',
                            timeout:10000000,
                            success: function (form, action) {
                                var respText = Ext.decode(action.response.responseText);
                                organImportView.organView.down('[itemId=organGridViewID]').getStore().reload();
                                var treeView = organImportView.organView.down('[itemId=organTreeViewID]');
                                var treeStore = treeView.getStore();
                                treeStore.proxy.extraParams.pcid = '0';
                                treeStore.load({
                                    node: treeView.getRootNode(),
                                    scope: this,
                                    callback: function () {
                                        treeView.getSelectionModel().select(treeView.getRootNode());//重新选中
                                    }
                                });
                                organImportView.close();
                                XD.msg(respText.msg);
                            },
                            failure: function () {
                                XD.msg('操作中断');
                            }
                        });
                    // });
                }
            },
            'organImportView button[itemId=cancel]': {
                click: function (btn) {
                    btn.findParentByType('organImportView').close();
                }
            },
            'organGridView button[itemId=deleteorganbtnid]': {
                click: function (view) {
                    var organGridView = view.findParentByType('organGridView');
                    var select = organGridView.getSelectionModel();
                    if (!select.hasSelection()) {
                        XD.msg('请选择操作记录');
                    } else {
                        var gridselections = select.getSelection();
                        var array = [];
                        var idStr='';
                        if(gridselections.length>50){
                            XD.msg("不支持超过50个记录的删除!");
                            return;
                        }
                        for (var i = 0; i < gridselections.length; i++) {
                            array.push(gridselections[i].get("organid"));
                            idStr+=gridselections[i].get("organid")+',';
                        }
                        idStr=idStr.substring(0,idStr.length-1);//去掉最后那个逗号
                        var flag=this.getRealnameResult(array);
                        if(flag){
                            XD.msg("所选机构中包含三员所属的机构，请勿删除");
                            return;
                        }
                        Ext.Msg.wait('正在检测是否存在关联数据，请稍等......','正在操作');
                        Ext.Ajax.request({
                            params: {ids:array},
                            url: '/classificationsetting/deleteValidate',
                            method: 'post',
                            timeout:XD.timeout,
                            success: function (resp) {
                                var respText = Ext.decode(resp.responseText);
                                if(!respText.success){
                                    var win = new Ext.create('Organ.view.OrganPreviewNodeView', {
                                        height: window.innerHeight * 6 / 7,
                                        width: 400,
                                        ids:array,
                                        idStr:idStr,
                                        organGridView:organGridView
                                    });
                                    win.down('[itemId=saveBtn]').setText('删除');
                                    var treeView = win.down('[itemId=previewTreepanelId]').down('treepanel');
                                    treeView.getStore().proxy.extraParams = {ids:array};
                                    treeView.getStore().proxy.url = '/classificationsetting/deletePreview';
                                    treeView.on('render', function (treeView) {
                                        treeView.getStore().load();
                                    });
                                    win.show();
                                    window.organPreviewParams=treeView.getStore().proxy.extraParams;
                                }else{
                                    XD.msg("所选机构中仍存在关联得条目数据，请勿删除");
                                }
                                Ext.Msg.wait('检测完毕','正在操作').hide();
                            },
                            failure: function () {
                                Ext.Msg.wait('检测完毕','正在操作').hide();
                                XD.msg('操作中断');
                            }
                        });
                    }
                }
            },
            'organDetailView button[itemId="cancelBtn"]': {
                click: function (view) {
                    var organDetailView = view.findParentByType('organDetailView');
                    organDetailView.close();
                }
            },
            'organPreviewNodeView button[itemId="saveBtn"]': {
                click: function (view) {
                    var organPreviewView = view.findParentByType('organPreviewNodeView');
                    var that = this;
                    if(view.getText()==='删除'){
                        var organGridView = organPreviewView.organGridView;
                        var gridselections = organGridView.getSelectionModel().getSelection();
                        XD.confirm('是否确定删除该机构及其相关联数据（将会连同机构的用户一同删除）', function () {
                            Ext.Msg.wait('正在进行删除操作，请耐心等待……','正在操作');
                            Ext.Ajax.request({
                                params: {ids: organPreviewView.ids,xtType:window.organViewTab},
                                url: '/organ/deleteOrgans',
                                method: 'post',
                                sync: true,
                                timeout:XD.timeout,
                                success: function () {
                                    Ext.Msg.wait('删除完成','正在操作').hide();
                                    //var responseText = Ext.decode(response.responseText);
                                    organGridView.delReload(gridselections.length, function () {
                                        var treeview = organGridView.findParentByType('organView').down('organTreeView');
                                        var newnode = {
                                            text: treeview.selection.get('text'),
                                            leaf: true,//改变样式
                                            fnid: treeview.selection.get('fnid')
                                        };
                                        if (organGridView.getStore().getTotalCount() == 0) {//删除所有子节点，更改当前节点样式
                                            var parentnode = treeview.selection.parentNode;
                                            if (parentnode != null) {
                                                var replacenode = parentnode.replaceChild(newnode, treeview.selection);
                                                var childNodes = parentnode.childNodes;
                                                for (var j = 0; j < childNodes.length; j++) {
                                                    if (childNodes[j].get('fnid') == replacenode.get('fnid')) {
                                                        treeview.getSelectionModel().select(childNodes[j]);//重新选中
                                                        break;
                                                    }
                                                }
                                            } else {//根节点
                                                treeview.selection.removeAll();
                                            }
                                        } else {
                                            var childNodes = treeview.selection.childNodes;
                                            if (childNodes.length > 0) {
                                                for (var i = 0; i < gridselections.length; i++) {
                                                    for (var j = 0; j < childNodes.length; j++) {
                                                        if (childNodes[j].get('fnid') == gridselections[i].get('organid')) {
                                                            childNodes[j].remove();
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    });
                                    organPreviewView.close();
                                    XD.msg('删除成功');
                                    //最后延时1s发请求删除关联数据
                                    var t=setTimeout("delRef()",1000);

                                    /*var url=responseText.data.systemname;
                                    url=url+'?szType=A2&organid='+organPreviewView.idStr+'&userid='+syncUserid;//删除机构A2
                                    //发送跨域请求
                                    that.crossDomainByUrl(url);*/
                                },
                                failure:function () {
                                    Ext.Msg.wait('删除失败','正在操作').hide();
                                    XD.msg('删除中断！');
                                }
                            });
                        });
                    }else{
                        var organDetailView = organPreviewView.organDetailView;
                        var form = organDetailView.down('form');
                        var URL = '/organ/updateOrgan';
                        var params = {xtType:window.organViewTab};
                        if (organDetailView.title === '增加机构') {
                            URL = '/organ/addOrgan';
                            params = {
                                parentid_real: organDetailView.organview.down('organTreeView').selection.get('fnid'),
                                xtType:window.organViewTab
                            }
                        }
                        submitOrgan(form, URL, params, organDetailView, organPreviewView,this);
                    }
                }
            },

            //调序
            'organGridView button[itemId=sequence]': {
                click: function (view) {
                    var organGridView = view.findParentByType('organGridView');
                    organgridView = view.findParentByType('organGridView');
                    var select = organGridView.getSelectionModel();
                    if (!select.hasSelection()) {
                        XD.msg('请选择操作记录');
                        return;
                    } else {
                        var gridselections = select.getSelection();
                        var array = [];
                        for (var i = 0; i < gridselections.length; i++) {
                            array.push(gridselections[i].get("organid"));
                        }
                    }
                    var sequenceOrgan = Ext.create('Ext.window.Window',{
                        width:'60%',
                        height:'75%',
                        modal:true,
                        title:'机构调序',
                        closeToolText:'关闭',
                        closeAction:'hide',
                        layout:'fit',
                        items:[{
                            xtype: 'organSequenceView'//调序视图
                        }]
                    });
                    sequenceOrgan.show();
                    window.sequenceOrgan=sequenceOrgan;
                    var view = sequenceOrgan.down('organSequenceView').down('organSequenceGridView');
                    view.initGrid({organid: array});
                }
            },
            'organSequenceView button[itemId="up"]': {
                click: function (view) {
                    var grid = view.findParentByType('organSequenceView').down('organSequenceGridView');
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
                        array.push(recordall[i].get("organid"));
                    }
                    Ext.Ajax.request({
                        method: 'post',
                        url: '/organ/organsortsequence',
                        params: {
                            organid: array,
                            currentcount:currentcount,
                            operate:'up'
                        },
                        success: function (response) {
                            //grid.getStore().reload();
                            grid.getStore().load(function(){
                                var select;
                                for(var i=0;i<grid.getStore().getCount();i++){
                                    var storeRe=grid.getStore().getAt(i);
                                    if(record[0].get('organid')==storeRe.get('organid')){
                                        select=storeRe;
                                        break;
                                    }
                                }
                                grid.getSelectionModel().select(select);//重新选中
                                grid.fireEvent('itemclick',grid,select);
                            });
                            var treeview = organview.down('[itemId=organTreeViewID]');
                            var treeStore = treeview.getStore();
                            if (treeview.selection.get('expanded')) {
                                treeStore.proxy.extraParams.pcid = treeview.selection.get('fnid');
                                treeStore.load({node: treeview.selection, scope: this});
                            } else {//未展开过时，重新load已选节点，会导致+号消失，无法展开子节点，故做判断处理
                                var selected = treeview.selection;
                                var selectedParent = treeview.selection.parentNode;
                                treeStore.proxy.extraParams.pcid = treeview.selection.parentNode.get('fnid');
                                treeStore.load({
                                    node: treeview.selection.parentNode,
                                    scope: this,
                                    callback: function () {
                                        var childNodes = selectedParent.childNodes;
                                        for (var j = 0; j < childNodes.length; j++) {
                                            if (childNodes[j].get('fnid') == selected.get('fnid')) {
                                                treeview.getSelectionModel().select(childNodes[j]);//重新选中
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

            'organSequenceView button[itemId="down"]': {
                click: function (view) {
                    var grid = view.findParentByType('organSequenceView').down('organSequenceGridView');
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
                        array.push(recordall[i].get("organid"));
                    }
                    Ext.Ajax.request({
                        method: 'post',
                        url: '/organ/organsortsequence',
                        params: {
                            organid: array,
                            currentcount:currentcount,
                            operate:'down'
                        },
                        success: function (response) {
                            //grid.getStore().reload();
                            grid.getStore().load(function () {
                                var select;
                                for(var i=0;i<grid.getStore().getCount();i++){
                                    var storeRe = grid.getStore().getAt(i);
                                    if(record[0].get('organid')==storeRe.get('organid')){
                                        select = storeRe;
                                        break;
                                    }
                                }
                                grid.getSelectionModel().select(select);//重新选择
                                grid.fireEvent('itemclick', grid, select);
                            });
                            var treeview = organview.down('[itemId=organTreeViewID]');
                            var treeStore = treeview.getStore();
                            if (treeview.selection.get('expanded')) {
                                treeStore.proxy.extraParams.pcid = treeview.selection.get('fnid');
                                treeStore.load({node: treeview.selection, scope: this});
                            } else {//未展开过时，重新load已选节点，会导致+号消失，无法展开子节点，故做判断处理
                                var selected = treeview.selection;
                                var selectedParent = treeview.selection.parentNode;
                                treeStore.proxy.extraParams.pcid = treeview.selection.parentNode.get('fnid');
                                treeStore.load({
                                    node: treeview.selection.parentNode,
                                    scope: this,
                                    callback: function () {
                                        var childNodes = selectedParent.childNodes;
                                        for (var j = 0; j < childNodes.length; j++) {
                                            if (childNodes[j].get('fnid') == selected.get('fnid')) {
                                                treeview.getSelectionModel().select(childNodes[j]);//重新选中
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

            'organSequenceView button[itemId="back"]': {
                click: function (view) {
                    organgridView.getStore().reload();
                    window.sequenceOrgan.close();
                }
            },
            'organDetailView button[itemId="previewBtn"]': {
                click: function (view) {
                    var organDetailView = view.findParentByType('organDetailView');
                    var treeSelection = organDetailView.organview.down('organTreeView').selection;
                    var form = organDetailView.down('form');
                    if (!form.isValid()) {
                        XD.msg('表单仍存在错误内容,请修改！');
                        return;
                    }
                    var data = form.getValues();
                    if (data['organname'] === '' || data['organtype'] === '') {
                        XD.msg('有必填项未填写');
                        return;
                    }
                    if(!organDetailView.needValid){
                        preview(form, organDetailView);//预览
                    }else{
                        Ext.Ajax.request({
                            url: '/organ/organValid',
                            params: {
                                formname: data['organname'],
                                parentid: treeSelection.get('fnid')
                            },
                            success: function (resp) {
                                if (Ext.decode(resp.responseText).success) {
                                    XD.msg("【"+treeSelection.get('text')+"】下已有【"+data['organname']+"】机构，请修改机构名称！");
                                } else {
                                    preview(form, organDetailView);//预览
                                }
                            }
                        });
                    }
                }
            },
            'organPreviewNodeTabView':{
                tabchange:function(view){
                    if(view.activeTab.title == '档案系统'){
                        var treeView = view.down('[itemId=previewTreepanelDaId]');
                        treeView.getStore().proxy.extraParams = window.organPreviewParams;
                        treeView.getStore().proxy.extraParams.xtType = '档案系统';
                        if(window.organPreviewParams.ids){//有ids参数的是删除的预览
                            treeView.getStore().proxy.url = '/classificationsetting/deletePreview';
                        }else{
                            treeView.getStore().proxy.url = '/organ/preview';
                        }
                        treeView.getStore().load();
                        window.organViewTab='档案系统';
                    }else if(view.activeTab.title == '声像系统'){
                        var treeView = view.down('[itemId=previewTreepanelSxId]');
                        treeView.getStore().proxy.extraParams = window.organPreviewParams;
                        treeView.getStore().proxy.extraParams.xtType = '声像系统';
                        if(window.organPreviewParams.ids){//有ids参数的是删除的预览
                            treeView.getStore().proxy.url = '/classificationsetting/deletePreview';
                        }else{
                            treeView.getStore().proxy.url = '/organ/preview';
                        }
                        treeView.getStore().load();
                        window.organViewTab='声像系统';
                    }
                }
            },

            'organDetailView button[itemId="submitBtn"]': {
                click: function (btn) {
                    var organDetailView = btn.findParentByType('organDetailView');
                    var treeSelection = organDetailView.organview.down('organTreeView').selection;
                    var form = organDetailView.down('form');
                    if (!form.isValid()) {
                        XD.msg('表单仍存在错误内容,请修改！');
                        return;
                    }
                    var data = form.getValues();
                    if (data['organname'] === '' || data['organtype'] === '') {
                        XD.msg('有必填项未填写');
                        return;
                    }
                    var operation = '修改';
                    var URL = '/organ/updateOrgan';
                    var params = {};
                    if (organDetailView.title === '增加机构') {
                        operation = '添加';
                        URL = '/organ/addOrgan';
                        params = {parentid_real: treeSelection.get('fnid')}
                    }
                    if(!organDetailView.needValid){
                        if (!organDetailView.isPreview) {
                            noPreview(operation,form,URL,params,organDetailView);
                        } else {
                            submitOrgan(form, URL, params, organDetailView);
                        }
                    }else{
                        Ext.Ajax.request({
                            url: '/organ/organValid',
                            params: {
                                formname: data['organname'],
                                parentid: treeSelection.get('fnid')
                            },
                            success: function (resp) {
                                if (Ext.decode(resp.responseText).success) {
                                    XD.msg("【"+treeSelection.get('text')+"】下已有【"+data['organname']+"】机构，请修改机构名称！");
                                } else {
                                    if (!organDetailView.isPreview) {
                                        noPreview(operation,form,URL,params,organDetailView);
                                    } else {
                                        submitOrgan(form, URL, params, organDetailView);
                                    }
                                }
                            }
                        });
                    }

                }
            },
            'organPreviewNodeView button[itemId=cancelBtn]': {
                click: function (btn) {
                    btn.findParentByType('organPreviewNodeView').close();
                }
            },
            'organDetailView': {
                render: function (view) {
                    if (view.title === '修改机构') {
                        view.isPreview = true;
                        view.needValid = false;
                        view.down('[itemId=previewBtn]').setDisabled(true);
                        var organNameItem = view.down('[itemId=organitem]');
                        organNameItem.on('change', function (self, newValue) {
                            var detailView = organNameItem.up('organDetailView');
                            if (newValue !== organNameItem.originalValue) {
                                detailView.isPreview = false;
                                detailView.needValid = true;
                                detailView.down('[itemId=previewBtn]').setDisabled(false);
                            } else {
                                detailView.isPreview = true;
                                detailView.needValid = false;
                                detailView.down('[itemId=previewBtn]').setDisabled(true);
                            }
                        })
                    }
                }
            }
        });
    },
    getRealnameResult:function (array) {
        var flag;
        Ext.Ajax.request({
            async:false,
            url: '/organ/userbyorgan',
            params: {
                organid: array
            },
            success: function (response) {
                flag = Ext.decode(response.responseText).data;
            }
        });
        return flag;
    },

    //跨域请求
    crossDomainByUrl:function(url){
        var store = Ext.create('Ext.data.Store', {
            model: 'Organ.model.OrganGridModel',
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
});
function preview(form, organDetailView) {//预览
    var params = {
        organId: form.getValues()['organid'],
        organName: form.getValues()['organname'],
        previewType: 'update'
    };
    if (organDetailView.title === '增加机构') {
        params = {
            parentId: organDetailView.organview.down('organTreeView').selection.get('fnid'),
            organName: form.getValues()['organname'],
            previewType: 'add'
        }
    }
    organDetailView.isPreview = true;
    var win = new Ext.create('Organ.view.OrganPreviewNodeView', {
        height: window.innerHeight * 6 / 7,
        width: 400,
        organDetailView: organDetailView
    });
    var treeView = win.down('[itemId=previewTreepanelDaId]');
    treeView.getStore().proxy.extraParams = params;
    treeView.getStore().proxy.url = '/organ/preview';
    treeView.on('render', function (treeView) {
        treeView.getStore().load();
    });
    window.organPreviewParams=params;
    win.show();
}

function delRef(){//删除关联数据
    //最后发请求删除关联数据
    Ext.Ajax.request({
        method: 'POST',
        url: '/organ/deleteOrgansRef'
    });
}

function submitOrgan(form, url, params, organDetailView, organPreviewView,that) {//提交
    form.submit({
        waitTitle: '提示',
        waitMsg: '正在提交数据请稍后...',
        url: url,
        method: 'post',
        params: params,
        success: function (form, action) {
            if (action.response.responseText === '') {
                XD.msg('操作失败');
                return;
            }
            var respText = Ext.decode(action.response.responseText);
            if (respText.success === true) {
                var organname = respText.data.organname;
                var organid = respText.data.organid;
                var treeview = organDetailView.organview.down('[itemId=organTreeViewID]');
                var organgridview = organDetailView.organview.down('[itemId=organGridViewID]');
                var childNodes = treeview.selection.childNodes;
                if (respText.msg.indexOf('增加成功')>-1) {
                    organgridview.getStore().reload();
                    organDetailView.close();
                    if (treeview.selection.get('leaf') || childNodes.length > 0) {
                        treeview.selection.insertChild(childNodes.length, {
                            text: organname,
                            leaf: true,
                            fnid: organid,
                            parentid: respText.data.parentid
                        });
                    }
                    /*var url=respText.data.systemname;
                    url=url+'?szType=A&organid='+organid;//增加机构A
                    //发送跨域请求
                    that.crossDomainByUrl(url);*/
                } else if (respText.msg === '修改成功') {
                    if (childNodes.length > 0) {
                        var treeStore = treeview.getStore();
                        if (treeview.selection.get('expanded')) {
                            treeStore.proxy.extraParams.pcid = treeview.selection.get('fnid');
                            treeStore.load({node: treeview.selection, scope: this});
                        } else {
                            var selected = treeview.selection;
                            var selectedParent = treeview.selection.parentNode;
                            treeStore.proxy.extraParams.pcid = treeview.selection.parentNode.get('fnid');
                            treeStore.load({
                                node: treeview.selection.parentNode,
                                scope: this,
                                callback: function () {
                                    var childNodes = selectedParent.childNodes;
                                    for (var j = 0; j < childNodes.length; j++) {
                                        if (childNodes[j].get('fnid') == selected.get('fnid')) {
                                            treeview.getSelectionModel().select(childNodes[j]);//重新选中
                                            break;
                                        }
                                    }
                                }
                            });
                        }
                    }
                    organgridview.getStore().reload();
                    organDetailView.close();
                    /*var url=respText.data.systemname;
                    url=url+'?szType=A1&organid='+organid;//修改机构A1
                    //发送跨域请求
                    that.crossDomainByUrl(url);*/
                }
                if (typeof organPreviewView !== 'undefined') {
                    organPreviewView.close();
                }
                XD.msg(respText.msg);
            } else {
                XD.msg('操作中断');
            }
        },
        failure: function (form, action) {
            XD.msg('操作中断');
        }
    });
}

function noPreview(operation,form,URL,params,organDetailView) {
    Ext.MessageBox.buttonText = {yes: '预览', no: '是', cancel: '否'};//顾忌到按钮排序问题
    Ext.MessageBox.show({
        title: "确认信息",
        msg: '您尚未预览级联' + operation + '的数据节点，是否确认' + operation + '机构及相关数据节点',
        modal: true,
        buttons: Ext.MessageBox.YESNOCANCEL,
        icon: Ext.MessageBox.QUESTION,
        fn: function (buttonId) {
            if (buttonId === 'yes') {
                preview(form, organDetailView);//预览
            } else if (buttonId === 'no') {
                submitOrgan(form, URL, params, organDetailView);//提交
            }
        }
    });
    Ext.MessageBox.buttonText = {yes: '是', no: '否', cancel: '取消'};//恢复
}