var NodeIdf = "";
var templateViewInfo;
var RealoadtemplateView;
Ext.define('MetadataTemplate.controller.MetadataTemplateController', {
    extend: 'Ext.app.Controller',
    views: [
        'MetadataTemplateView', 'MetadataTemplateTreeView', 'MetadataTemplateGridView', 'MetadataTemplatePromptView', 'MetadataTemplateCopyFormView',
        'MetadataTemplateTreeComboboxView', 'MetadataTemplateSelectionView','MetadataTemplateDetailView',
        'MetadataTemplateGridPreView', 'MetadataTemplateFormView', 'MetadataTemplateFormInfoView','GroupingManagement',
        'AddGroupFieldWindow'
    ],//加载view
    stores: [
        'MetadataTemplateTreeStore', 'MetadataTemplateGridStore',
        'MetadataTemplateSelectStore','CodesettingSelectStore',
        'GroupManagementStore'
    ],//加载store
    models: [
        'MetadataTemplateTreeModel', 'MetadataTemplateGridModel',
        'CodesettingJsonModel','GroupManagementModel'
    ],//加载model
    init: function () {
        this.control({
            'templateTreeView': {
                select: function (treemodel, record) {
                    var templateView = treemodel.view.findParentByType('atemplateView');
                    RealoadtemplateView=templateView;
                    var templatePromptView = templateView.down('[itemId=templatePromptViewID]');
                    var bgSelectOrgan = templateView.down('[itemId=bgSelectOrgan]');
                    if (record.parentNode != null) {//非根目录（功能节点）
                        var templateGridView = templatePromptView.down('[itemId=templateGridViewID]');
                        templatePromptView.setActiveItem(templateGridView);
                        templateGridView.setTitle("当前位置：" + record.get('text'));
                        // if (record.get('leaf')) {
                        //     templateGridView.down('button[itemId=synctemplatebtnid]').setDisabled(true);
                        // } else {
                        //     templateGridView.down('button[itemId=synctemplatebtnid]').setDisabled(false);
                        // }
                        templateGridView.nodeid = record.get('fnid');
                        templateGridView.initGrid({nodeid: record.get('fnid')});
                        NodeIdf=record.get('fnid');
                        var tbseparator = templateGridView.down("toolbar").query('tbseparator');
                        //this.templateButtonHandler(templateGridView, record.get('fnid'), tbseparator);
                    } else {
                        templatePromptView.setActiveItem(bgSelectOrgan);
                    }
                }
            },
            'atemplateGridView button[itemId=setfieldbtnid]': {
                click: function (btn) {
                    var templateView = btn.findParentByType('atemplateView');
                    var treeview = templateView.down('[itemId=templateTreeViewID]');
                    var gridview = templateView.down('[itemId=templateGridViewID]');
                    if (treeview.selection.isRoot()) {
                        XD.msg('请选择有效的数据分类');
                        return;
                    }
                    var nodeid = treeview.selection.get('fnid');
                    Ext.Ajax.request({
                        url: '/template/isActionable/' + nodeid,
                        method: 'post',
                        success: function (sp) {
                            var data = Ext.decode(sp.responseText);
                            if(data.success){
                                Ext.Ajax.request({
                                    params: {nodeid: nodeid},
                                    url: '/metadataTemplate/getSelectedByNodeid',
                                    method: 'post',
                                    sync: true,
                                    success: function (resp) {
                                        var respText = Ext.decode(resp.responseText);
                                        if (respText.success == true) {
                                            var selectWin = Ext.create('MetadataTemplate.view.MetadataTemplateSelectionView', {
                                                selectedfnid: nodeid,
                                                gridview:gridview
                                            });
                                            selectWin.items.get(0).store.proxy.extraParams = {nodeid: nodeid};
                                            selectWin.items.get(0).getStore().load(function () {
                                                selectWin.items.get(0).setValue(respText.data);
                                            });
                                            selectWin.show();
                                        } else {
                                            XD.msg(respText.msg);
                                        }
                                    },
                                    failure: function () {
                                        XD.msg('操作中断');
                                    }
                                });
                            } else {
                                XD.msg("当前模板已被锁定，不可进行此操作。");
                            }
                        }
                    });
                }
            },
            'templateSelectionView button[itemId=templateSelectClose]': {
                click: function (btn) {
                    var templateSelectionView = btn.findParentByType('templateSelectionView');
                    templateSelectionView.close();
                }
            },
            'templateSelectionView button[itemId=templateSelectSubmit]': {
                click: function (btn) {
                    var templateSelectionView = btn.findParentByType('templateSelectionView');
                    if (templateSelectionView.items.get(0).getValue().length == 0) {
                        XD.msg('请至少选择一个字段');
                        return;
                    }
                    var fieldnames = templateSelectionView.items.get(0).toField.getStore().data.items;
                    var fieldnamearray = [];
                    for (var i = 0; i < fieldnames.length; i++) {
                        fieldnamearray[i] = fieldnames[i].get("fieldname");
                    }
                    Ext.Ajax.request({
                        params: {
                            nodeid: templateSelectionView.selectedfnid,
                            fieldnames: fieldnamearray
                        },
                        url: '/metadataTemplate/submitfields',
                        method: 'post',
                        sync: true,
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            if (respText.success == true) {
                                templateSelectionView.gridview.getStore().reload();
                                XD.msg('提交成功');
                                templateSelectionView.close();
                            } else {
                                XD.msg(respText.msg);
                            }
                        },
                        failure: function () {
                            XD.msg('操作中断');
                        }
                    });
                }
            },
            'atemplateGridView button[itemId=updatefieldbtnid]': {
                click: function (btn) {
                    var templateGridView = btn.findParentByType('atemplateGridView');
                    var select = templateGridView.getSelectionModel();
                    if (select.getSelection().length != 1) {
                        XD.msg('请选择一条操作记录');
                    } else {
                        var nodeid = templateGridView.dataParams.nodeid;
                        Ext.Ajax.request({
                            url: '/template/isActionable/' + nodeid,
                            method: 'post',
                            success: function (sp) {
                                var data = Ext.decode(sp.responseText);
                                if(data.success){
                                    var win = new Ext.create('MetadataTemplate.view.MetadataTemplateDetailView',{
                                        gridview:templateGridView
                                    });
                                    win.down('form').loadRecord(select.getLastSelected());
                                    win.show();
                                    var tablename = select.getLastSelected().get('fieldtable');
                                    var fieldcode = select.getLastSelected().get('fieldcode');
                                    // Ext.Ajax.request({
                                    //     url: '/template/getfdlength/',
                                    //     method: 'get',
                                    //     params: {
                                    //         tablename: tablename,
                                    //         fieldcode:fieldcode
                                    //     },
                                    //     success: function (sp) {
                                    //         var fieldlength = Ext.decode(sp.responseText).data;
                                    //         win.down('form').down('[name=fdlength]').setValue(fieldlength);
                                    //     },
                                    //     failure: function () {
                                    //         XD.msg('操作中断！');
                                    //     }
                                    // });
                                    if (!select.getLastSelected().get('gfield')) {
                                        win.down('[itemId=isgridsetting]').el.slideOut();
                                    }
                                    if (!select.getLastSelected().get('qfield')) {
                                        win.down('[itemId=isquerysetting]').el.slideOut();
                                    }
                                    if (!select.getLastSelected().get('ffield')) {
                                        win.down('[itemId=isformsetting]').el.slideOut();
                                    }else{
                                        if(fieldcode=='fscount' || fieldcode=='kccount'){
                                            //设置默认份数及库存份数为1
                                            win.down('[itemId=defaultValue]').setValue('1');
                                        }
                                    }
                                } else {
                                    XD.msg("当前模板已被锁定，不可进行此操作。");
                                }
                            }
                        });
                    }
                }
            },
            'templateDetailView button[itemId=templateCancelBtnID]': {
                click: function (btn) {
                    var closeview = btn.findParentByType('templateDetailView');
                    closeview.close();
                }
            },
            'templateDetailView button[itemId=templateSaveBtnID]': {
                click: function (btn) {
                    var templateDetailView = btn.findParentByType('templateDetailView');
                    templateDetailView.down('form').submit({
                        waitTitle: '提示',
                        waitMsg: '正在提交数据请稍后...',
                        url: '/metadataTemplate/UpdateTemplate',
                        method: 'post',
                        success: function (form, action) {
                            var respText = Ext.decode(action.response.responseText);
                            if (respText.success == true) {
                                templateDetailView.gridview.getStore().reload();
                                templateDetailView.close();
                            }
                            XD.msg(respText.msg);
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            // 模板预览
            'atemplateGridView button[itemId=resultPreviewbtnid]': {
                click: function (btn) {
                    var templateGridView = btn.findParentByType('atemplateGridView');
                    templateViewInfo = templateGridView.up('atemplateView');
                    var record = templateGridView.store.totalCount;
                    if (record.length < 1) {
                        XD.msg('无模板字段，请先设置模板信息！');
                        return;
                    }
                    getGridPreView(templateGridView);
                }
            },
            // 模板预览 - 表单界面
            'atemplateGridPreView button[itemId=gridviewbtnid]': {
                click: function (btn) {
                    var nodeid = btn.up('window').nodeid;
                    var templateFormInfoView = Ext.create('MetadataTemplate.view.MetadataTemplateFormInfoView');
                    var dynamicform = templateFormInfoView.down('atemplateFormView').down('dynamicform');

                    gridPreViewInfo = btn.up('window');
                    // 隐藏模板预览界面
                    btn.up('window').hide();
                    var pre = dynamicform.down('[itemId=preBtn]');
                    var next = dynamicform.down('[itemId=nextBtn]');
                    // 隐藏上一条&下一条的按钮
//            		pre.hide();
//            		next.hide();
                    initFormField(dynamicform, 'look', nodeid);
                    templateViewInfo.setActiveItem(templateFormInfoView);
                }
            },
            // 模板预览 - 表单界面 - 返回
            'atemplateFormView button[itemId=back]': {
                click: function (btn) {
                    var templateGridView = templateViewInfo.down('atemplateGridView');
                    getGridPreView(templateGridView);
                    // 返回模板维护界面
                    templateViewInfo.setActiveItem(templateViewInfo.down('[itemId=gridview]'));
                }
            },
            // 模板预览 - 返回
            'atemplateGridPreView button[itemId=rebackbtnid]': {
                click: function (btn) {
                    btn.up('window').hide();
                }
            },
            //复制模板
            'atemplateGridView button[itemId=copytemplatebtnid]': {
                click: function (btn) {
                    var templateView = btn.findParentByType('atemplateView');
                    var treeselection = templateView.down('templateTreeView').selection;
                    var nodeid = treeselection.get('fnid');
                    Ext.Ajax.request({
                        url: '/template/isActionable/' + nodeid,
                        method: 'post',
                        success: function (sp) {
                            var data = Ext.decode(sp.responseText);
                            if(data.success){
                                var gridview = templateView.down('[itemId=templateGridViewID]');
                                if (treeselection.isRoot()) {
                                    XD.msg('请选择有效的数据分类');
                                    return;
                                }
                                var record = treeselection;
                                var fullname = record.get('text');
                                while (!record.parentNode.isRoot()) {
                                    fullname = record.parentNode.get('text') + '_' + fullname;
                                    record = record.parentNode;
                                }
                                var win = new Ext.create('MetadataTemplate.view.MetadataTemplateCopyFormView',{
                                    gridview:gridview,
                                    treeView:templateView.down('templateTreeView')
                                });
                                var picker = win.down('[itemId=sourceSelectItemID]');
                                picker.extraParams = {pcid:templateView.down('templateTreeView').selection.get('fnid')};
                                picker.on('render', function (picker) {
                                    picker.store.load();
                                });
                                var picker = win.down('[itemId=targetSelectItemID]');
                                picker.extraParams = {pcid:templateView.down('templateTreeView').selection.get('fnid')};
                                picker.on('render', function (picker) {
                                    picker.store.load();
                                });
                                win.down('form').items.get('sourceItemID').setValue(treeselection.get('fnid'));
                                win.down('form').items.get('sourceSelectItemID').setValue(fullname);
                                win.down('form').items.get('targetItemID').setValue(treeselection.get('fnid'));
                                win.down('form').items.get('targetSelectItemID').setValue(fullname);
                                win.show();
                            } else {
                                XD.msg("当前模板已被锁定，不可进行此操作。");
                            }
                        }
                    });
                }
            },
            'templateCopyFormView button[itemId=templateSaveBtnID]': {
                click: function (btn) {
                    var templateCopyFormView = btn.findParentByType('templateCopyFormView');
                    var form = templateCopyFormView.down('form');
                    var sourceid = form.getValues()['sourceid'];
                    var targetid = form.getValues()['targetid'];
                    if(sourceid===targetid){
                        XD.msg('源模板与目标模板数据节点不能为同一节点，请重新选择');
                        return;
                    }

                    var withCode=false;
                    var confirmMsg='若目标模板存在数据，该操作将覆盖原数据（不包括档号设置数据），是否确认复制？';
                    if(form.down('[itemId=withCodeID]').checked){
                        withCode=true;
                        confirmMsg='若目标模板存在数据，该操作将覆盖原数据（包括档号设置数据），是否确认复制？';
                    }
                    XD.confirm(confirmMsg, function () {
                        form.submit({
                            waitTitle: '提示',
                            waitMsg: '正在处理请稍后...',
                            url: '/metadataTemplate/copyTemplate',
                            params:{withCode:withCode},
                            method: 'post',
                            success: function (form, action) {
                                var respText = Ext.decode(action.response.responseText);
                                templateCopyFormView.gridview.getStore().reload();
                                templateCopyFormView.close();
                                XD.msg(respText.msg);
                            },
                            failure: function () {
                                XD.msg('操作中断');
                            }
                        });
                    });
                }
            },
            //复制模板 返回
            'templateDetailView button[itemId=templateCancelBtnID]': {
                click: function (btn) {
                    var closeview = btn.findParentByType('templateDetailView');
                    closeview.close();
                }
            },
            //删除模板
            'atemplateGridView button[itemId=deletetemplatebtnid]': {
                click: function (btn) {
                    var mainview = btn.findParentByType('atemplateView');
                    var treeview = mainview.down('[itemId=templateTreeViewID]');
                    if (treeview.selection.isRoot()) {
                        XD.msg('请选择有效的数据分类');
                        return;
                    }
                    Ext.Ajax.request({
                        url: '/template/isActionable/' + treeview.selection.get('fnid'),
                        method: 'post',
                        success: function (sp) {
                            var data = Ext.decode(sp.responseText);
                            if(data.success){
                                XD.confirm('本次操作将删除当前所有模板数据（包括当前数据节点档号设置），是否继续?',function (){
                                    deleteTemplate(treeview);
                                });
                            } else {
                                XD.msg("当前模板已被锁定，不可进行此操作。");
                            }
                        }
                    });
                }
            },
            // 锁定模板
            'atemplateGridView button[itemId=luckTemplate]': {
                click: function (btn) {
                    var templateView = btn.findParentByType('atemplateView');
                    var treeview = templateView.down('[itemId=templateTreeViewID]');
                    var gridview = templateView.down('[itemId=templateGridViewID]');
                    if (treeview.selection.isRoot()) {
                        XD.msg('请选择有效的数据分类');
                        return;
                    }
                    var nodeid = treeview.selection.get('fnid');
                    var tbseparator = gridview.down("toolbar").query('tbseparator');
                    var info = this;
                    XD.confirm('确定要对当前模板进行锁定吗？<br />锁定后，该节点下的所有子节点的模板将被替换成当前节点的模板。<br />且所有子节点模板将不可更改，除非解锁当前模板。',function(){
                        Ext.Msg.wait('正在锁定模板，请耐心等待……', '正在操作');
                        Ext.Ajax.request({
                            url: '/template/updateNodeLuckState/' + nodeid + "/" + "1",
                            method: 'post',
                            sync: true,
                            success: function (resp) {
                                Ext.Msg.wait('同步成功', '正在操作').hide();
                                XD.msg("成功锁定当前模板！<br />在当前模板，进行模板、档号设置的操作，将同步更新到所有子节点模板。");
                                info.templateButtonHandler(gridview, nodeid, tbseparator);
                            },
                            failure: function () {
                                XD.msg('操作中断');
                            }
                        });
                    },this);
                    gridview.notResetInitGrid({nodeid:nodeid});
                }
            },
            // 解锁模板
            'atemplateGridView button[itemId=unluckTemplate]': {
                click: function (btn) {
                    var templateView = btn.findParentByType('atemplateView');
                    var treeview = templateView.down('[itemId=templateTreeViewID]');
                    var gridview = templateView.down('[itemId=templateGridViewID]');
                    if (treeview.selection.isRoot()) {
                        XD.msg('请选择有效的数据分类');
                        return;
                    }
                    var nodeid = treeview.selection.get('fnid');
                    var tbseparator = gridview.down("toolbar").query('tbseparator');
                    var info = this;
                    Ext.Ajax.request({
                        url: '/template/updateNodeLuckState/' + nodeid + "/" + "0",
                        method: 'post',
                        sync: true,
                        success: function (resp) {
                            XD.msg("成功解锁当前模板！<br />可对子节点模板单独进行维护。");
                            info.templateButtonHandler(gridview, nodeid, tbseparator);
                        },
                        failure: function (resp) {
                            XD.msg(Ext.decode(resp.responseText).msg);
                        }
                    });
                    gridview.notResetInitGrid({nodeid:nodeid});
                }
            },
            'atemplateGridView button[itemId=exportID]': { //导出字段模板
                render: function (btn) {
                    btn.down('[itemId=exportTemplate]').on('click', function () {
                        var templateGridView = btn.findParentByType('atemplateGridView');
                        var nodeid = templateGridView.dataParams.nodeid;
                        var filename = "";
                        Ext.MessageBox.wait('正在处理请稍后...', '提示');
                        Ext.Ajax.request({
                            url: '/metadataTemplate/exportFieldModel?nodeid=' + nodeid,
                            method: 'post',
                            success: function (sp) {
                                var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
                                var isIE = userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1; //判断是否IE<11浏览器
                                var obj = Ext.decode(sp.responseText).data;
                                filename = obj.nodename;
                                if (isIE) {
                                    window.open('/template/downLoadModel?nodename=' + encodeURIComponent(filename));
                                } else {
                                    window.location.href = '/template/downLoadModel?nodename=' + encodeURIComponent(filename);
                                }
                                Ext.MessageBox.hide();
                                XD.msg('文件生成成功，正在准备下载');
                            }
                        });
                    });
                    btn.down('[itemId=importTemplate]').on('click', function () {
                        var win = Ext.create('Comps.view.TepmUploadView', {});
                        win.show();
                    });
                }
            },
            'atemplateGridView button[itemId=importTemplate]':{ //导入字段模板
                click: this.importHandler
            },
            'templateCopyFormView button[itemId=templateCancelBtnID]': {
                click: function (btn) {
                    var closeview = btn.findParentByType('templateCopyFormView');
                    closeview.close();
                }
            },
            'atemplateGridView button[itemId=groupManagement]':{
                click: function (btn) {
                    // var grid = btn.up('atemplateGridView');
                    // var groupManagentView = grid.up('atemplateView').down('GroupingManagement');
                    // groupManagentView.preview = grid;
                    // grid.up('atemplateView').setActiveItem(groupManagentView);
                    var importWin = Ext.create('Ext.window.Window', {
                        width: '50%',
                        height: '50%',
                        layout: 'fit',
                        xtype:'groupManage',
                        items: [{
                            xtype: 'GroupingManagement'
                            //archivesType:GroupingManagement
                        }]
                    });
                    importWin.down('GroupingManagement').getStore().reload();
                    importWin.show();
                }
            },
            'GroupingManagement button[itemId=back]':{
                click: function (btn) {
                    // var groupManagentView = btn.up('GroupingManagement');
                    // var grid = groupManagentView.preview;
                    // groupManagentView.up('atemplateView').setActiveItem(grid);
                    btn.up('window').close();
                }
            },
            'GroupingManagement button[itemId=add]':{
                click:function (btn) {
                    var win = new Ext.create('MetadataTemplate.view.AddGroupFieldWindow', {
                        title: '增加分组字段'
                    });
                    win.preview=btn.up('GroupingManagement');
                    win.show();
                }
            },
            'addGroupFieldWindow button[itemId=save]':{
                click:function (view) {
                    var addGroupFieldview = view.findParentByType('addGroupFieldWindow');
                    var form = addGroupFieldview.down('form');
                    var data = form.getValues();
                    if (data['groupname'] == '' ) {
                        XD.msg("有必填项未填写");
                        return;
                    }
                    var URL = '/metadataTemplate/addGroupField';
                    form.submit({
                        waitTitle: '提示',
                        waitMsg: '正在提交数据请稍后...',
                        url: URL,
                        method: 'POST',
                        success: function (form, action) {
                            XD.msg("保存成功");
                            addGroupFieldview.close();
                            var GroupingManagement = addGroupFieldview.preview;
                            //清除勾选记录
                            GroupingManagement.getSelectionModel().clearSelections();
                            GroupingManagement.getStore().reload();
                        }
                    });
                }
            },
            'GroupingManagement button[itemId=modify]':{
                click:function (view) {
                    var GroupingManagement = view.findParentByType('GroupingManagement');
                    var select = GroupingManagement.getSelectionModel();
                    if (select.getSelection().length != 1) {
                        XD.msg("请选择一条操作记录!");
                    } else if (select.getSelection().length > 1) {
                        XD.msg("只能选择一条操作记录!");
                    } else {
                        var win = new Ext.create('MetadataTemplate.view.AddGroupFieldWindow', {
                            title: '增加分组字段'
                        });
                        win.preview=GroupingManagement;
                        win.down('form').loadRecord(select.getLastSelected());
                        win.show();
                    }
                }
            },
            'GroupingManagement button[itemId=del]':{
                click:function (view) {
                    var GroupingManagement = view.findParentByType('GroupingManagement');
                    var select = GroupingManagement.getSelectionModel();
                    if (!select.hasSelection()) {
                        XD.msg("请选择操作记录!");
                    } else {
                        XD.confirm("确认删除？", function () {
                            this.delGroupField(GroupingManagement,select);
                        },this);
                    }
                }
            },
            'addGroupFieldWindow button[itemId=cancel]': {
                click: function (view) {
                    var addGroupFieldWindow = view.findParentByType('addGroupFieldWindow');
                    addGroupFieldWindow.close();
                }
            },
        });
    },
    //模板按钮控制
    templateButtonHandler: function(templateGridView, nodeid, tbseparator) {
        Ext.Ajax.request({
            method:'POST',
            url: '/template/getLuckState/' + nodeid,//判断当前节点是否为父节点,且模板的锁定状态
            async:false,
            scope:this,
            success: function (response) {
                var msg = Ext.decode(response.responseText).msg;
                tbseparator[5].setVisible(true);
                tbseparator[6].setVisible(false);
                if (msg == 'child') {
                    templateGridView.down('[itemId=luckTemplate]').setVisible(false);
                    templateGridView.down('[itemId=unluckTemplate]').setVisible(false);
                    tbseparator[5].setVisible(false);
                    tbseparator[6].setVisible(false);
                } else if (msg == 'luck') {
                    templateGridView.down('[itemId=luckTemplate]').setVisible(true);
                    templateGridView.down('[itemId=unluckTemplate]').setVisible(false);
                } else {
                    templateGridView.down('[itemId=unluckTemplate]').setVisible(true);
                    templateGridView.down('[itemId=luckTemplate]').setVisible(false);
                }
            }
        });
    },
    importHandler: function (btn) {
        var win = Ext.create('Metadata.view.MetadataTempUpload', {});
        win.show();
    },
    delGroupField:function (GroupingManagement,select) {
        var gridselections = select.getSelection();
        var gridstore = GroupingManagement.getStore();
        var configid = [];
        for (var i = 0; i < gridselections.length; i++) {
            configid.push(gridselections[i].get("groupid"));
        }
        var configids = configid.join(',');
        Ext.Ajax.request({
            url: '/metadataTemplate/delGroupField/',
            method: 'post',
            sync: true,
            params:{configid:configid},
            timeout:XD.timeout,
            success: function (response) {
                XD.msg('删除成功！');
                gridstore.reload();
            }
        });
    }
});
function getGridPreView (templateGridView) {
    var templateGridPreView = Ext.create('Ext.window.Window',{
        width:'70%',
        height:'80%',
        modal:true,
        title:'模板预览',
        closeToolText:'关闭',
        closeAction:'hide',
        layout:'fit',
        items:[{
            xtype: 'atemplateGridPreView'
        }]
    });
    var nodeid = templateGridView.dataParams.nodeid;
    templateGridPreView.nodeid = nodeid;
    templateGridPreView.down('atemplateGridPreView').initGrid({nodeid: nodeid, type: '模板预览'});
    templateGridPreView.show();
}
function initFormField(form, operate, nodeid) {
    if (form.nodeid != nodeid) {//切换节点后，form和tree的节点id不相等
        form.nodeid = nodeid;//用左侧树节点的id初始化form的nodeid参数
        form.removeAll();//移除form中的所有表单控件
        var field = {
            xtype: 'hidden',
            name: 'entryid'
        };
        form.add(field);
        var formField;
        Ext.Ajax.request({
            url: '/metadataTemplate/form',
            async:false,
            params:{
                nodeid:nodeid
            },
            success: function (response) {
                formField = Ext.decode(response.responseText);
                if(formField.length==0){
                    XD.msg('请检查档号设置信息是否正确');
                    return;
                }
                form.templates = formField;
                form.initField(formField,operate);//重新动态添加表单控件
            }
        });
    }
    return '加载表单控件成功';
}
function deleteTemplate(btn) {
    var mainview = btn.findParentByType('atemplateView');
    var treeview = mainview.down('[itemId=templateTreeViewID]');
    var gridview = mainview.down('[itemId=templateGridViewID]');

    var downloadForm = document.createElement('form');
    document.body.appendChild(downloadForm);
    var inputTextElement;
    inputTextElement = document.createElement('input');
    inputTextElement.name = 'nodeid';
    inputTextElement.value = treeview.selection.get('fnid');
    downloadForm.appendChild(inputTextElement);
    downloadForm.action = '/metadataTemplate/export';
    downloadForm.submit();
    Ext.Ajax.request({
        params: {nodeid: treeview.selection.get('fnid')},
        url: '/metadataTemplate/deleteTemplateByNodeid',
        method: 'post',
        sync:true,
        success: function () {
            gridview.getStore().reload();
            XD.msg('删除成功');
        },
        failure: function (response) {
            XD.msg(Ext.decode(response.responseText).data);
        }
    });
}