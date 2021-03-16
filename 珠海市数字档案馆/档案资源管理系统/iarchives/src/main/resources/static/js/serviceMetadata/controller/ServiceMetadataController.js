/**
 * Created by tanly on 2017/11/1 0024.
 */
Ext.define('ServiceMetadata.controller.ServiceMetadataController', {
    extend: 'Ext.app.Controller',

    views: ['AccreditMetadataView', 'AccreditMetadataGridView', 'AccreditMetadataPromptView'],//加载view
    stores: ['AccreditMetadataTreeStore', 'AccreditMetadataGridStore','ServiceMetadataAccreditStore',
        'ServiceMetadataOperationStore'],//加载store
    models: ['AccreditMetadataTreeModel', 'AccreditMetadataGridModel'],//加载model
    init: function () {
        var ifShowRightPanel = false;
        this.control({
            'accreditMetadataView [itemId=treepanelId]': {
                select: function (treemodel, record) {
                    window.treesettingview = treemodel.view;
                    var systemConfigView = treemodel.view.findParentByType('accreditMetadataView');
                    if (!ifShowRightPanel) {
                        systemConfigView.remove(systemConfigView.down('[itemId=accreditMetadataPromptViewID]'));
                        systemConfigView.add({
                            xtype: 'accreditMetadataGridView'
                        });
                        ifShowRightPanel = true;
                    }
                    var gridView = systemConfigView.down('[itemId=accreditMetadataGridViewID]');
                    gridView.setTitle("当前位置：" + record.get('text'));
                    gridView.initGrid({parentid: record.get('fnid')});
                }
            },
            'accreditMetadataGridView': {
                beforedrop: function (node, data, overmodel, position, dropHandlers) {
                    dropHandlers.cancelDrop();
                    if (data.records.length > 1) {
                        XD.msg('不支持批量选择拖拽排序，请选择一条数据');
                    } else {
                        XD.confirm('确认将分类[ ' + data.records[0].get('shortname') + ' ]移动到[ '
                            + overmodel.get('shortname') + ' ]的' + ("before" == position ? '前面吗' : '后面吗'), function () {
                            var overorder = overmodel.get('sequence');
                            var target;
                            if (typeof(overorder) == 'undefined') {
                                target = -1;
                            } else if ("before" == position) {
                                target = overorder;
                            } else if ("after" == position) {
                                target = overorder + 1;
                            }
                            Ext.Ajax.request({
                                url: '/systemconfig/orderConfig/',
                                params: {
                                    configid:data.records[0].get('id'),
                                    order:target
                                },
                                method: 'post',
                                success: function () {
                                    data.view.getStore().reload();
                                    XD.msg('顺序修改成功');
                                    var dragzone = Ext.getCmp(data.view.id).getPlugins()[0].dropZone;
                                    dragzone.invalidateDrop();
                                    dragzone.handleNodeDrop(data, overmodel, position);
                                    dragzone.fireViewEvent('drop', node, data, overmodel, position);
                                }
                            });
                        });
                    }
                }
            },
            'accreditMetadataGridView button[itemId=add]': {
                click: function (view) {
                    var treeid=window.treesettingview.selection.get('fnid')==''? 'undefined':window.treesettingview.selection.get('fnid');
                    Ext.Ajax.request({
                        url: '/serviceMetadata/testAdd/' + treeid,
                        method: 'post',
                        success: function (response) {
                            var respText = Ext.decode(response.responseText);
                            if (respText.success == true) {
                                var win = new Ext.create('ServiceMetadata.view.AccreditMetadataWindow', {
                                    title: '增加参数',
                                    classview: view.findParentByType('accreditMetadataView')
                                });
                                win.show();
                            } else {
                                XD.msg(respText.msg);
                            }
                        },
                        failure: function () {
                            XD.msg("操作失败");
                        }
                    });
                }
            },
            'accreditMetadataGridView button[itemId=update]': {
                click: function (view) {
                    var systemConfigGridView = view.findParentByType('accreditMetadataGridView');
                    var select = systemConfigGridView.getSelectionModel();
                    if (select.getSelection().length != 1) {
                        XD.msg("请选择一条操作记录!");
                    } else if (select.getSelection().length > 1) {
                        XD.msg("只能选择一条操作记录!");
                    } else {
                        var win = new Ext.create('ServiceMetadata.view.AccreditMetadataWindow', {
                            title: '修改参数',
                            classview: view.findParentByType('accreditMetadataView')
                        });
                        win.down('form').loadRecord(select.getLastSelected());
                        win.show();
                    }
                }
            },
            'accreditMetadataGridView button[itemId=delete]': {
                click: function (view) {
                    var systemConfigGridView = view.findParentByType('accreditMetadataGridView');
                    var select = systemConfigGridView.getSelectionModel();
                    if (!select.hasSelection()) {
                        XD.msg("请选择操作记录!");
                    } else {
                        if(window.treesettingview.selection.get('fnid') == ''){
                            this.confirm("确认信息","是否确认删除该参数类型及其所有参数记录？", function () {
                                this.confirm("警告信息","请注意！<br/>该参数类型的在相关界面可能已使用，如果删除将导致其不能正常使用，请确认是否还要继续删除？", function () {
                                    this.deleteRecord(systemConfigGridView,select);
                                },this);
                            },this);
                        }else{
                            this.confirm("确认信息","请注意！<br/>相关界面中如果已经使用该参数，将导致不能正常使用，是否确认删除该参数？", function () {
                                this.deleteRecord(systemConfigGridView,select);
                            },this);
                        }
                    }
                }
            },
            'accreditMetadataWindow button[itemId=cancel]': {
                click: function (view) {
                    var systemConfigWindow = view.findParentByType('accreditMetadataWindow');
                    systemConfigWindow.close();
                }
            },
            'accreditMetadataWindow button[itemId=save]': {
                click: function (view) {
                    var systemConfigWindow = view.findParentByType('accreditMetadataWindow');
                    var form = systemConfigWindow.down('form');
                    var data = form.getValues();
                    if (data['shortname'] == '' || data['operation'] == '') {
                        XD.msg("有必填项未填写");
                        return;
                    }
                    var URL = '/serviceMetadata/addSystemConfig';
                    if (systemConfigWindow.title == '增加参数') {
                        form.getForm().findField('parentid').setValue(window.treesettingview.selection.get('fnid'));
                        this.formSubmit(systemConfigWindow,form,URL);
                    }else{
                        if(window.treesettingview.selection.get('fnid') == ''){
                            if(form.getForm().findField('operation').isDirty()){
                                this.confirm("警告信息","请注意！<br/>该参数类型在相关界面可能已使用，如果修改参数值将导致其不能正常使用，请确认是否还要继续保存修改？", function () {
                                    this.formSubmit(systemConfigWindow,form,URL);
                                },this);
                            }else{
                                this.formSubmit(systemConfigWindow,form,URL);
                            }
                        }else{
                            if(form.getForm().findField('operation').isDirty()||form.getForm().findField('shortname').isDirty()){
                                this.confirm("确认信息","请注意！<br/>相关界面中如果已经使用该参数，将导致不能正常使用，是否确认修改该参数？", function () {
                                    this.formSubmit(systemConfigWindow,form,URL);
                                },this);
                            }else{
                                this.formSubmit(systemConfigWindow,form,URL);
                            }
                        }
                    }
                }
            }
        });
    },formSubmit : function (systemConfigWindow,form,URL) {
        form.submit({
            waitTitle: '提示',
            waitMsg: '正在提交数据请稍后...',
            url: URL,
            method: 'POST',
            success: function (form, action) {
                var respText = Ext.decode(action.response.responseText);
                if (respText.success == true) {
                    var systemConfigGridView = systemConfigWindow.classview.down('[itemId=accreditMetadataGridViewID]');
                    var childNodes = window.treesettingview.selection.childNodes;
                    if (systemConfigWindow.title == '增加参数') {
                        systemConfigGridView.getStore().reload({
                            callback: function () {
                            }
                        });//重置callback
                        systemConfigWindow.close();
                        if (window.treesettingview.selection.data.fnid=='') {
                            window.treesettingview.selection.insertChild(childNodes.length, {
                                text: respText.data.operation,
                                leaf: true,
                                fnid: respText.data.cid,
                                parentid: respText.data.parentid
                            })
                        }
                        XD.msg(respText.msg);
                    } else {//修改
                        if (childNodes.length > 0) {
                            var treeStore = window.treesettingview.getStore();
                            treeStore.proxy.extraParams.parentconfigid = window.treesettingview.selection.get('fnid');
                            treeStore.load({node: window.treesettingview.selection, scope: this});
                        }
                        systemConfigGridView.getStore().reload();
                        systemConfigWindow.close();
                        XD.msg(respText.msg);
                    }
                } else {
                    XD.msg(respText.msg);
                }
            },
            failure: function (form, action) {
                var respText = Ext.decode(action.response.responseText);
                var systemConfigGridView = systemConfigWindow.classview.down('[itemId=accreditMetadataGridViewID]');
                systemConfigGridView.getStore().reload();
                XD.msg(respText.msg);
            }
        });
    },deleteRecord : function (systemConfigGridView,select) {
        var gridselections = select.getSelection();
        var gridstore = systemConfigGridView.getStore();
        var configid = [];
        for (var i = 0; i < gridselections.length; i++) {
            configid.push(gridselections[i].get("cid"));
        }
        var configids = configid.join(',');
        Ext.Ajax.request({
            url: '/serviceMetadata/deletAccredit/' + configids,
            method: 'delete',
            sync: true,
            success: function (response) {
                var respText = Ext.decode(response.responseText);
                XD.msg(respText.msg);
                var successid = respText.data.split(',');
                gridstore.loadPage(1, {//默认重新加载第一页
                    callback: function () {
                        var newnode = {
                            text: window.treesettingview.selection.get('operation'),
                            leaf: true,//改变样式
                            fnid: window.treesettingview.selection.get('fnid')
                        };
                        if (systemConfigGridView.getStore().totalCount == 0) {//无子
                            var selectid = window.treesettingview.selection.get('fnid');
                            if (selectid == '') {
                                var node = window.treesettingview.node;
                                while (node.hasChildNodes()) {
                                    node.removeChild(node.firstChild);
                                }
                            }else{
                                var parentnode = window.treesettingview.selection.parentNode;
                                // parentnode.replaceChild(newnode, window.treesettingview.selection);

                                var childNodes = parentnode.childNodes;
                                for (var j = 0; j < childNodes.length; j++) {
                                    if (childNodes[j].get('fnid') == selectid) {
                                        window.treesettingview.getSelectionModel().select(childNodes[j]);//重新选中
                                        break;
                                    }
                                }
                            }
                        } else {
                            var childNodes = window.treesettingview.selection.childNodes;
                            if (childNodes.length > 0) {
                                for (var i = 0; i < successid.length; i++) {
                                    var selectid = successid[i];
                                    for (var j = 0; j < childNodes.length; j++) {
                                        if (childNodes[j].get('fnid') == selectid) {
                                            childNodes[j].remove();
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        for(var i = 0; i < systemConfigGridView.getStore().getCount(); i++){
                            systemConfigGridView.getSelectionModel().deselect(systemConfigGridView.getStore().getAt(i));
                        }
                        systemConfigGridView.acrossSelections = [];
                    }
                });
            }
        });
    },confirm: function (confirm, text, callbackyes, scope, callbackno) {
        Ext.MessageBox.confirm(confirm, text, function (btn) {
            if (btn == 'yes') {
                if (typeof callbackyes == 'undefined') {
                    return;
                }
                var fn = callbackyes.bind(this);
                fn();
            }
            if (btn == 'no') {
                if (typeof callbackno == 'undefined') {
                    return;
                }
                var fn = callbackno.bind(this);
                fn();
            }
        }, scope);
    }
});