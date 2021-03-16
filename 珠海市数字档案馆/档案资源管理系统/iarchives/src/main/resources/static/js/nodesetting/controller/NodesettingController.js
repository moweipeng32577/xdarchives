/**
 * Created by tanly on 2017/10/24 0024.
 */
Ext.define('Nodesetting.controller.NodesettingController', {
    extend: 'Ext.app.Controller',

    views: ['NodesettingView', 'NodesettingTreeView', 'NodesettingSxTreeView', 'NodesettingGridView',  'NodesettingSxGridView', 'NodesettingPromptView', 'NodesettingSxPromptView', 'NodesettingDetailFormView', 'NodesettingTreeComboboxView'],//加载view
    stores: ['NodesettingTreeStore','NodesettingSxTreeStore', 'NodesettingGridStore', 'NodesettingSxGridStore'],//加载store
    models: ['NodesettingTreeModel', 'NodesettingGridModel'],//加载model
    init: function () {
        var ifShowRightPanel = false;
        var ifShowRightSxPanel = false;
        this.control({
            'nodesettingView':{
                tabchange:function(view){
                    var treeView;
                    if(view.activeTab.title == '档案系统'){
                        treeView = view.down('[itemId=daTree]');
                        treeView.getStore().proxy.extraParams.xtType = '档案系统';
                        window.classViewTab='档案系统';
                    }else if(view.activeTab.title == '声像系统'){
                        treeView = view.down('[itemId=sxTree]');
                        treeView.getStore().proxy.extraParams.xtType = '声像系统';
                        window.classViewTab='声像系统';
                    }
                }
            },
            'nodesettingSxTreeView': {
                select: function (treemodel, record) {
                    window.treesettingSxview = treemodel.view;
                    var nodesettingView = treemodel.view.up('[itemId=sxxtId]');
                    var nodesettingPromptView = nodesettingView.down('[itemId=nodesettingSxPromptViewID]');
                    if (!ifShowRightSxPanel) {
                        nodesettingPromptView.removeAll();
                        nodesettingPromptView.add({
                            xtype: 'nodesettingSxGridView'
                        });
                        ifShowRightSxPanel = true;
                    }
                    var nodesettingGrid = nodesettingPromptView.down('[itemId=nodesettingSxGridViewID]');
                    window.nodesettingSxGridView = nodesettingGrid;
                    nodesettingGrid.setTitle("当前位置：" + record.get('text'));
                    var selectionfnid = (record.get('fnid') == 'functionid') ? '' : record.get('fnid');
                    nodesettingGrid.initGrid({parentnodeid: selectionfnid, xtType: window.classViewTab});
                }
            },
            'nodesettingTreeView': {
                select: function (treemodel, record) {
                    window.treesettingview = treemodel.view;
                    var nodesettingView = treemodel.view.up('[itemId=daxtId]');
                    var nodesettingPromptView = nodesettingView.down('[itemId=nodesettingPromptViewID]');
                    if (!ifShowRightPanel) {
                        nodesettingPromptView.removeAll();
                        nodesettingPromptView.add({
                            xtype: 'nodesettingGridView'
                        });
                        ifShowRightPanel = true;
                    }
                    var nodesettingGrid = nodesettingPromptView.down('[itemId=nodesettingGridViewID]');
                    window.nodesettingGridView = nodesettingGrid;
                    nodesettingGrid.setTitle("当前位置：" + record.get('text'));
                    var selectionfnid = (record.get('fnid') == 'functionid') ? '' : record.get('fnid');
                    nodesettingGrid.initGrid({parentnodeid: selectionfnid,xtType:window.classViewTab});
                }//,
                // itemcontextmenu: function (view, record, item, index, e, eOpts) {
                //     window.treesettingview = view;
                //     e.preventDefault();//取消浏览器的默认右键点击事件
                //     item.ctxMenu = new Ext.menu.Menu({
                //         margin: '0 0 10 0',
                //         items: [//暂时不开放此功能
                //             {
                //                 text: "初始化",
                //                 iconCls:'x-ctxmenu-exchange-icon',
                //                 handler: function () {
                //                     XD.confirm('初始化数据节点将清除当前模式的节点设置，是否确认初始化?', function () {
                //                         var nodesettingTreeView = view.findParentByType('nodesettingView')
                //                             .getComponent('nodesettingTreeViewItemID').getComponent('nodesettingTreeViewID');
                //                         var nodesettingTreeViewStore = nodesettingTreeView.getStore();
                //                         nodesettingTreeViewStore.proxy.url = '/nodesetting/changeTreeModel';
                //                         Ext.apply(nodesettingTreeViewStore.proxy.extraParams, {initModel: 'classification-organ'});//分类-机构，若要换成【机构-分类】模式则改为initModel:'organ-classification'
                //                         nodesettingTreeViewStore.loadPage(1);
                //                     })
                //                 }
                //             },
                //             // {
                //             //     text: "增加节点",
                //             //     itemId: 'addNodeItemID',
                //             //     iconCls:'x-ctxmenu-add-icon',
                //             //     menu: new Ext.menu.Menu({
                //             //         items: [{
                //             //             text: '机构',
                //             //             iconCls:'x-ctxmenu-type-icon',
                //             //             handler: function () {
                //             //                 new Ext.create('Nodesetting.view.NodesettingDetailFormView').show();
                //             //             }
                //             //         }, {
                //             //             text: '分类',
                //             //             iconCls:'x-ctxmenu-type-icon',
                //             //             handler: function () {
                //             //                 var win = new Ext.create('Nodesetting.view.NodesettingDetailFormView');
                //             //                 win.title = '增加分类节点';
                //             //
                //             //                 var picker = win.down('[itemId=nodenameitemid]');
                //             //                 picker.url = '/nodesetting/getClassificationByParentClassId';
                //             //                 picker.extraParams = {pcid: ''};
                //             //                 picker.on('render', function (picker) {
                //             //                     picker.store.load();
                //             //                 });
                //             //                 win.show();
                //             //             }
                //             //         }]
                //             //     })
                //             // }, {
                //             //     text: "增加子节点",
                //             //     iconCls:'x-ctxmenu-addleaf-icon',
                //             //     menu: new Ext.menu.Menu({
                //             //         items: [{
                //             //             text: '机构',
                //             //             iconCls:'x-ctxmenu-type-icon',
                //             //             handler: function () {
                //             //                 var win = new Ext.create('Nodesetting.view.NodesettingDetailFormView');
                //             //                 win.title = '增加机构子节点';
                //             //                 win.show();
                //             //             }
                //             //         }, {
                //             //             text: '分类',
                //             //             iconCls:'x-ctxmenu-type-icon',
                //             //             handler: function () {
                //             //                 var win = new Ext.create('Nodesetting.view.NodesettingDetailFormView');
                //             //                 win.title = '增加分类子节点';
                //             //
                //             //                 var picker = win.down('[itemId=nodenameitemid]');
                //             //                 picker.url = '/nodesetting/getClassificationByParentClassId';
                //             //                 picker.extraParams = {pcid: ''};
                //             //                 picker.on('render', function (picker) {
                //             //                     picker.store.load();
                //             //                 });
                //             //                 win.show();
                //             //             }
                //             //         }]
                //             //     })
                //             // }, {
                //             //     text: "修改",
                //             //     itemId: 'editNodeItemID',
                //             //     iconCls:'x-ctxmenu-edit-icon',
                //             //     handler: function () {
                //             //         var win = new Ext.create('Nodesetting.view.NodesettingDetailFormView');
                //             //         win.title = '修改树节点';
                //             //         win.down('[itemId=multcolumnId]').items.items[0].columnWidth = 1;
                //             //         win.down('[itemId=multcolumnId]').items.removeAt(1);//移除checkbox
                //             //         if(view.selection.get('roottype')=='classification'){
                //             //             var picker = win.down('[itemId=nodenameitemid]');
                //             //             picker.url = '/nodesetting/getClassificationByParentClassId';
                //             //             picker.extraParams = {pcid: ''};
                //             //             picker.on('render', function (picker) {
                //             //                 picker.store.load();
                //             //             });
                //             //         }
                //             //         win.down('form').load({
                //             //             url: '/nodesetting/getTreeNode',
                //             //             params: {nodeid: view.selection.get('fnid')},
                //             //             success: function (form, action) {
                //             //             },
                //             //             failure: function () {
                //             //                 XD.msg('加载数据失败');
                //             //             }
                //             //         });
                //             //         win.show();
                //             //     }
                //             // }, {
                //             //     text: "删除",
                //             //     itemId: 'deleteNodeItemID',
                //             //     iconCls:'x-ctxmenu-delete-icon',
                //             //     handler: function () {
                //             //         XD.confirm('本操作将删除该节点及其所有子节点，是否确认删除?', function () {
                //             //             var array = [];
                //             //             array[0] = window.treesettingview.selection.get('fnid');
                //             //             Ext.Ajax.request({
                //             //                 params: {nodeid: array},
                //             //                 url: '/nodesetting/deleteNode',
                //             //                 method: 'post',
                //             //                 sync: true,
                //             //                 success: function (resp) {
                //             //                     var respText = Ext.decode(resp.responseText);
                //             //                     if (respText.success == true) {
                //             //                         var childNodes_parent = window.treesettingview.selection.parentNode.childNodes;
                //             //                         if (childNodes_parent.length == 1) {
                //             //                             var newnode = {
                //             //                                 text: window.treesettingview.selection.parentNode.get('text'),
                //             //                                 leaf: true,//改变样式
                //             //                                 fnid: window.treesettingview.selection.parentNode.get('fnid'),
                //             //                                 parentid: window.treesettingview.selection.parentNode.get('parentid'),
                //             //                                 roottype: window.treesettingview.selection.parentNode.get('roottype')
                //             //                             };
                //             //                             window.treesettingview.selection.parentNode.parentNode.replaceChild(newnode, window.treesettingview.selection.parentNode);
                //             //                         } else {
                //             //                             window.treesettingview.selection.remove();
                //             //                         }
                //             //                         window.nodesettingGridView.getStore().reload();
                //             //                         XD.msg("删除成功");
                //             //                     } else {
                //             //                         XD.msg('删除操作中断');
                //             //                     }
                //             //                 },
                //             //                 failure: function () {
                //             //                     XD.msg('操作中断');
                //             //                 }
                //             //             });
                //             //         });
                //             //     }
                //             // }
                //         ]
                //     }).showAt(e.getXY());
                //
                //     // if (view.selection.get('fnid') == 'functionid') {
                //     //     item.ctxMenu.remove(item.ctxMenu.getComponent('addNodeItemID'));
                //     //     item.ctxMenu.remove(item.ctxMenu.getComponent('editNodeItemID'));
                //     //     item.ctxMenu.remove(item.ctxMenu.getComponent('deleteNodeItemID'));
                //     // }
                // }
            },
            'nodesettingGridView':{
                beforedrop: function (node, data, overmodel, position, dropHandlers) {
                    dropHandlers.cancelDrop();
                    if (data.records.length > 1) {
                        XD.msg('不支持批量选择拖拽排序，请选择一条数据');
                    } else {
                        XD.confirm('确认将节点[ ' + data.records[0].get('nodename') + ' ]移动到[ '
                            + overmodel.get('nodename') + ' ]的' + ("before" == position ? '前面吗' : '后面吗'), function () {
                            var overorder = overmodel.get('orders');
                            var target;
                            if (typeof(overorder) == 'undefined') {
                                target = -1;
                            } else if ("before" == position) {
                                target = overorder;
                            } else if ("after" == position) {
                                target = overorder + 1;
                            }
                            Ext.Ajax.request({
                                url: '/nodesetting/node/' + data.records[0].get('nodeid') + '/' + target,
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
            'nodesettingDetailFormView button[itemId=nodesettingSaveBtnID]': {
                click: function (view) {
                    var nodesettingDetailFormView = view.findParentByType('nodesettingDetailFormView');
                    var form = nodesettingDetailFormView.down('form');
                    var selectionfnid = (window.treesettingview.selection.get('fnid') == 'functionid') ? '' : window.treesettingview.selection.get('fnid');

                    var addTreeNodeUrl = '/nodesetting/addTreeNode';
                    var addChildTreeNodeUrl = '/nodesetting/addChildTreeNode';
                    var params1 = {}, params2 = {};
                    var nodename = form.down('[itemId=nodenameitemid]').value;
                    if (nodesettingDetailFormView.title != '修改树节点' && nodesettingDetailFormView.title != '修改列表节点') {
                        var ischecked = form.down('[itemId=containchilditemid]').checked;
                        params1 = {
                            nodetype_real: 1,
                            nodename_real: nodename,
                            selectedid: selectionfnid,
                            containchild: ischecked
                        };
                        params2 = {
                            nodetype_real: 2,
                            nodename_real: nodename,
                            selectedid: selectionfnid,
                            containchild: ischecked
                        };
                    }

                    var URL = '/nodesetting/updateTreeNode';
                    var params = {nodename_real: nodename};
                    if (nodesettingDetailFormView.title == '增加机构节点') {
                        URL = addTreeNodeUrl;
                        params = params1
                    } else if (nodesettingDetailFormView.title == '增加分类节点') {
                        URL = addTreeNodeUrl;
                        params = params2;
                    } else if (nodesettingDetailFormView.title == '增加机构子节点') {
                        URL = addChildTreeNodeUrl;
                        params = params1;
                    } else if (nodesettingDetailFormView.title == '增加分类子节点') {
                        URL = addChildTreeNodeUrl;
                        params = params2;
                    }
                    var data = form.getValues();
                    if (typeof nodename == 'undefined' || data['nodecode'] == '') {
                        XD.msg("有必填项未填写");
                        return;
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
                                nodesettingDetailFormView.close();
                                var treeStore, childNodes;
                                if (respText.msg == '增加子节点成功') {
                                    childNodes = window.treesettingview.selection.childNodes;
                                    if (window.treesettingview.selection.get('leaf') == true || childNodes.length > 0) {
                                        window.treesettingview.selection.insertChild(childNodes.length, {
                                            text: respText.data.nodename,
                                            leaf: respText.data.leaf,
                                            fnid: respText.data.nodeid,
                                            parentid: respText.data.parentnodeid,
                                            roottype: respText.data.nodetype
                                        });
                                        treeStore = window.treesettingview.getStore();
                                        treeStore.proxy.extraParams.pcid = respText.data.nodeid;
                                        treeStore.load({
                                            node: window.treesettingview.selection.childNodes[childNodes.length - 1],
                                            scope: this
                                        });
                                    }
                                    if (typeof window.nodesettingGridView !== 'undefined') {
                                        window.nodesettingGridView.getStore().reload();
                                    }
                                } else if (respText.msg == '增加节点成功') {
                                    var childNodes_parent = window.treesettingview.selection.parentNode.childNodes;
                                    window.treesettingview.selection.parentNode.insertChild(childNodes_parent.length, {
                                        text: respText.data.nodename,
                                        leaf: respText.data.leaf,
                                        fnid: respText.data.nodeid,
                                        parentid: respText.data.parentnodeid,
                                        roottype: respText.data.nodetype
                                    });

                                    treeStore = window.treesettingview.getStore();
                                    treeStore.proxy.extraParams.pcid = respText.data.nodeid;
                                    treeStore.load({
                                        node: window.treesettingview.selection.parentNode.childNodes[childNodes_parent.length - 1],
                                        scope: this
                                    });

                                } else if (nodesettingDetailFormView.title == '修改树节点') {
                                    var selection = window.treesettingview.selection;
                                    var parentnode = window.treesettingview.selection.parentNode;
                                    treeStore = window.treesettingview.getStore();
                                    treeStore.proxy.extraParams.pcid = parentnode.get('fnid');
                                    treeStore.load({
                                        node: parentnode,
                                        scope: this,
                                        callback: function () {
                                            var childNodes = parentnode.childNodes;
                                            for (var j = 0; j < childNodes.length; j++) {
                                                if (childNodes[j].get('fnid') == selection.get('fnid')) {
                                                    window.treesettingview.getSelectionModel().select(childNodes[j]);//重新选中
                                                    break;
                                                }
                                            }
                                        }
                                    });
                                    if (typeof window.nodesettingGridView !== 'undefined') {
                                        window.nodesettingGridView.getStore().reload();
                                    }
                                } else if (nodesettingDetailFormView.title == '修改列表节点') {
                                    childNodes = window.treesettingview.selection.childNodes;
                                    if (childNodes.length > 0) {
                                        treeStore = window.treesettingview.getStore();
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
                                    window.nodesettingGridView.getStore().reload();
                                }
                                XD.msg(respText.msg);
                            } else {
                                XD.msg(respText.msg);
                            }
                        },
                        failure: function () {
                            XD.msg("操作失败!");
                        }
                    });
                }
            },
            'nodesettingDetailFormView button[itemId=nodesettingCancelBtnID]': {
                click: function (view) {
                    var nodesettingDetailFormView = view.findParentByType('nodesettingDetailFormView');
                    nodesettingDetailFormView.close();
                }
            },

            'nodesettingGridView button[itemId=changeModelBtnID]': {
                click: function (view) {
                    XD.confirm('初始化数据节点将清除当前模式的节点设置，是否确认初始化?', function () {
                        var nodesettingTreeView = view.findParentByType('nodesettingView').down('[itemId=nodesettingTreeViewID]');
                        var nodesettingTreeViewStore = nodesettingTreeView.getStore();
                        nodesettingTreeViewStore.proxy.url = '/nodesetting/changeTreeModel';
                        Ext.apply(nodesettingTreeViewStore.proxy.extraParams, {
                            // initModel: nodesettingTreeView.getRootNode().firstChild.data.roottype
                            initModel:'classification-organ'//分类-机构，若要换成【机构-分类】模式则改为roottype:'organ-classification'
                        });
                        nodesettingTreeViewStore.loadPage(1);
                    })
                }
            },
            'nodesettingGridView button[itemId=addOrganBtn]': {
                click: function (view) {
                    var win = new Ext.create('Nodesetting.view.NodesettingDetailFormView');
                    win.title = '增加机构子节点';
                    win.show();
                }
            },
            'nodesettingGridView button[itemId=addClassBtn]': {
                click: function (view) {
                    var win = new Ext.create('Nodesetting.view.NodesettingDetailFormView');
                    win.title = '增加分类子节点';

                    var picker = win.down('[itemId=nodenameitemid]');
                    picker.url = '/nodesetting/getClassificationByParentClassId';
                    picker.extraParams = {pcid: ''};
                    picker.on('render', function (picker) {
                        picker.store.load();
                    });
                    win.show();
                }
            },
            'nodesettingGridView button[itemId=updateNodeBtnID]': {
                click: function (view) {
                    var win = new Ext.create('Nodesetting.view.NodesettingDetailFormView');

                    var nodesettingGridView = view.findParentByType('nodesettingGridView');
                    var selection = nodesettingGridView.getSelectionModel().getSelection();
                    var selectednodeid;
                    win.title = '修改树节点';
                    win.down('[itemId=multcolumnId]').items.items[0].columnWidth = 1;
                    win.down('[itemId=multcolumnId]').items.removeAt(1);//移除checkbox
                    if (window.treesettingview.selection != null) {
                        selectednodeid = window.treesettingview.selection.get('fnid');
                    }
                    if (selection.length == 1) {
                        selectednodeid = selection[0].get('nodeid');
                        win.title = '修改列表节点';
                    } else if (selection.length > 1 || selectednodeid == null || selectednodeid == "") {
                        XD.msg('请选择一条记录');
                        return;
                    }
                    if(selection[0].get('nodetype')==2){
                        var picker = win.down('[itemId=nodenameitemid]');
                        picker.url = '/nodesetting/getClassificationByParentClassId';
                        picker.extraParams = {pcid: ''};
                        picker.on('render', function (picker) {
                            picker.store.load();
                        });
                    }

                    win.down('form').load({
                        url: '/nodesetting/getTreeNode',
                        params: {nodeid: selectednodeid},
                        success: function () {
                        },
                        failure: function () {
                            XD.msg('加载数据失败');
                        }
                    });
                    win.show();
                }
            },
            'nodesettingGridView button[itemId=deleteNodeBtnID]': {
                click: function (view) {
                    var nodesettingGridView = view.findParentByType('nodesettingGridView');
                    var selection = nodesettingGridView.getSelectionModel().getSelection();
                    if (selection.length < 1) {
                        XD.msg("请选择操作记录!");
                        return;
                    }
                    var array = [];
                    var hasclass = false;
                    for (var i = 0; i < selection.length; i++) {
                        if (selection[i].get('nodetype') === 2) {
                            XD.msg('所选数据包含分类节点，请到分类设置进行分类删除操作！');
                            hasclass = true;
                            break;
                        }
                        array[i] = selection[i].get('nodeid');
                    }
                    if (hasclass) {
                        return;
                    }
                    XD.confirm('本操作将删除该节点及其所有子节点，是否确认删除?', function () {
                        Ext.Msg.wait('正在进行删除操作，请耐心等待……','正在操作');
                        Ext.Ajax.request({
                            params: {nodeid: array},
                            url: '/nodesetting/deleteNode',
                            method: 'post',
                            timeout:10000000000,
                            sync: true,
                            success: function (resp) {
                                var respText = Ext.decode(resp.responseText);
                                if (respText.success == true) {
                                    nodesettingGridView.delReload(selection.length, function () {
                                        var newnode = {
                                            text: window.treesettingview.selection.get('text'),
                                            leaf: true,//改变样式
                                            fnid: window.treesettingview.selection.get('fnid'),
                                            parentid: window.treesettingview.selection.get('parentid'),
                                            roottype: window.treesettingview.selection.get('roottype')
                                        };
                                        if (nodesettingGridView.getStore().getTotalCount() == 0) {//删除所有子节点，更改当前节点样式
                                            var parentnode = window.treesettingview.selection.parentNode;
                                            if (parentnode != null) {
                                                var replacenode = parentnode.replaceChild(newnode, window.treesettingview.selection);
                                                var childNodes = parentnode.childNodes;
                                                for (var j = 0; j < childNodes.length; j++) {
                                                    if (childNodes[j].get('fnid') == replacenode.get('fnid')) {
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
                                                for (var i = 0; i < selection.length; i++) {
                                                    for (var j = 0; j < childNodes.length; j++) {
                                                        if (childNodes[j].get('fnid') == selection[i].get('nodeid')) {
                                                            childNodes[j].remove();
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    });
                                    Ext.Msg.wait('删除完成','正在操作').hide();
                                    XD.msg("删除成功");
                                } else {
                                    XD.msg('删除失败');
                                }
                            },
                            failure: function () {
                                XD.msg("操作中断");
                            }
                        });
                    });
                }
            }
        });
    }
});