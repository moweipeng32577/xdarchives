/**
 * 数据管理控制器
 * Created by Rong on 2017/10/24.
 *
 */
Ext.define('Borrow.controller.BorrowController', {
    extend: 'Ext.app.Controller',
    views: [
        'BorrowView',
        'BorrowGridView',
        'BorrowSXView',
        'ElectronAddView',
        'ElectronFormGridView',
        'ElectronFormItemView',
        'ElectronFormView',
        'LookAddFormGridView',
        'LookAddFormItemView',
        'LookAddFormView',
        'LookAddMxGridView',
        'LookAddSqView',
        'StLookAddFormView',
        'StLookFormView',
        'ElectronAddFormView',
        'ElectronFormAddView',
        'ElectronicView'
    ],
    models: [
        'BorrowModel',
        'ElectronBorrowGridModel',
        'ElectronBorrowTreeModel',
        'ElectronFormGridModel',
        'LookAddMxGridModel',
        'LookAddFormGridModel'
    ],
    stores: [
        'BorrowStore',
        'ApproveManStore',
        'ElectronBorrowGridStore',
        'ElectronBorrowTreeStore',
        'ElectronFormGridStore',
        'JypurposeStore',
        'LookAddFormGridStore',
        'LookAddMxGridStore'
    ],

    init: function () {
        window.downType = 'managementgrid';
        this.control({
            'borrowView [itemId=treepanelId]': {
                render: function (view) {
                    view.getRootNode().on('expand', function (node) {
                        for (var i = 0; i < node.childNodes.length; i++) {
                            if (node.childNodes[i].raw.text == '全宗卷管理') {//权限档案屏蔽全宗卷管理
                                node.childNodes[i].raw.visible = false;
                            }
                        }
                    })
                },
                select: function (treemodel, record) {
                    var managementSXView = treemodel.view.findParentByType('borrowView').down('borrowSXView');
                    managementSXView.removeAll();
                    var items = [{
                        xtype: 'borrowgrid',
                        region: 'center'
                    }];
                    window.downType = 'borrowgrid';
                    managementSXView.add(items);
                    var grid = treemodel.view.findParentByType('borrowView').down('borrowgrid');
                    window.welectronGridView = grid;
                    grid.initGrid({nodeid: record.get('fnid')});
                }
            },
            'borrowgrid button[itemId=dealElectronAdd]': {
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
                        items: [{xtype: 'lookAddMxGridView',type:1}]
                    });
                    var lookAddMxGrid = boxwin.getComponent('lookAddMxGridViewId');
                    lookAddMxGrid.initGrid({'borrowType':'电子查档'});
                    boxwin.show();
                    window.boxwin = boxwin;

                }
            },

            'borrowgrid button[itemId=electronAdd]': {
                click: function (view) {
                    var select = window.welectronGridView.getSelectionModel();
                    if (select.getCount() < 1) {
                        XD.msg('请选择一条数据');
                        return;
                    }

                    var dataids = [];
                    for (var i = 0; i < select.getSelection().length; i++) {
                        dataids.push(select.getSelection()[i].get('entryid'));
                        if (select.getSelection()[i].get('eleid') == undefined) {
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

            'lookAddMxGridView button[itemId=stAddSq]': {
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
                    window.wmedia = [];
                    if(window.wlookAddMxGrid.type==1){
                        var win = Ext.create('Borrow.view.ElectronAddView');
                        win.show();

                        var fromGrid = win.getComponent('electronFormGridViewId');
                        fromGrid.initGrid({dataids: dataids});
                        var form = win.down('[itemId=electronFormItemViewId]');
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
                        var sqWin = Ext.create('Borrow.view.LookAddSqView');
                        var form = sqWin.down('[itemId=lookAddFormItemViewId]');
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
                        var fromGrid = sqWin.getComponent('lookAddFormGridViewId');
                        fromGrid.initGrid({dataids: dataids});
                    }
                }
            },
            'lookAddMxGridView button[itemId=remove]': {
                click: function (view) {
                    var dealgrid = view.findParentByType('lookAddMxGridView');
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

            'lookAddMxGridView button[itemId=close]': {
                click: function () {
                    window.boxwin.close();
                }
            },

            'lookAddFormItemView button[itemId=lookAddFormSubmit]': {
                click: function (btn) {
                    var form = btn.findParentByType('lookAddFormItemView');
                    var lookAddSqWin = form.findParentByType('lookAddSqView');
                    var borrowts = form.getComponent('borrowtsId').getValue();
                    if(!form.isValid()){
                        return;
                    }else{
                        if (borrowts == '' || borrowts == null||String(borrowts).indexOf(".")>-1||isNaN(borrowts)||parseInt(borrowts)<1 ) {
                            XD.msg('查档天数不合法');return;
                        }
                    }

                    form.submit({
                        waitTitle: '',// 标题
                        url: '/electron/stAddFormBill',
                        params:{
                          eleids:window.wmedia
                        },
                        method: 'POST',
                        success: function () {
                            XD.msg('提交成功');
                            window.wlookAddMxGrid.getStore().loadPage(1);
                            lookAddSqWin.close();
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'lookAddFormItemView button[itemId=lookAddFormClose]': {
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
                    btn.findParentByType("lookAddSqView").close();
                }
            },

            'electronFormItemView button[itemId=electronFormSubmit]': {
                click: function (btn) {
                    var form = btn.findParentByType('electronFormItemView');
                    var borrowts = form.getComponent('borrowtsId').getValue();

                    if(!form.isValid()){
                        return;
                    }else{
                        if(!borrowts||isNaN(borrowts)||/\.|-/.test(borrowts+'')){
                            XD.msg('查档天数不合法');return;
                        }
                    }

                    var electronAddWin = btn.findParentByType('electronAddView');
                    form.submit({
                        url: '/electron/electronBill',
                        method: 'POST',
                        params: {
                            eleids:window.wmedia
                        },
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

            'electronFormItemView button[itemId=electronFormClose]': {
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
                    view.findParentByType('electronAddView').close();
                }
            },

            'borrowgrid button[itemId=stAdd]': {
                click: function () {
                    var select = window.welectronGridView.getSelectionModel();
                    if (select.getCount() < 1) {
                        XD.msg('至少选择一条数据');
                        return;
                    }

                    var dataids = [];
                    for (var i = 0; i < select.getSelection().length; i++) {
                        dataids.push(select.getSelection()[i].get('entryid'));
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

            'borrowgrid button[itemId=lookAdd]': {
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
                        items: [{xtype: 'lookAddMxGridView'}]
                    });
                    var lookAddMxGrid = boxwin.getComponent('lookAddMxGridViewId');
                    lookAddMxGrid.initGrid({'borrowType':'实体查档'});
                    boxwin.show();
                    window.boxwin = boxwin;
                }
            },
            'borrowgrid button[itemId=stAddDoc]': {
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
                        closable:false,
                        items: [{xtype: 'stLookFormView'}]
                    });
                    window.wmedia = [];
                    window.borrowStadd = borrowRoquest;
                    var form = borrowRoquest.down('[itemId=stLookAddFormViewId]');
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

            'stLookAddFormView button[itemId=stlookAddFormSubmit]': {
                click: function (btn) {
                    var form = btn.findParentByType('stLookAddFormView');
                    var borrowts = form.getComponent('borrowtsId').getValue();
                    if(!form.isValid()){
                        return;
                    }else{
                        if (borrowts == '' || borrowts == null||String(borrowts).indexOf(".")>-1||isNaN(borrowts)||parseInt(borrowts)<1 ) {
                            XD.msg('查档天数不合法');return;
                        }
                    }

                    form.submit({
                        waitTitle: '',// 标题
                        url: '/electron/stLookAddForm',
                        method: 'POST',
                        params:{
                            eleids:window.wmedia
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
            },

            'stLookAddFormView button[itemId=stlookAddFormClose]': {
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
                    window.borrowStadd.close();
                }
            },
            'borrowgrid button[itemId=electronAddDoc]': {
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
                        closable:false,
                        items: [{xtype: 'electronFormAddView'}]
                    });
                    window.wmedia = [];
                    window.borrowElectronadd = borrowRoquest;
                    var form = borrowRoquest.down('[itemId=electronAddFormViewId]');
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

            'electronAddFormView button[itemId=electronAddSubmit]': {
                click: function (btn) {
                    var form = btn.findParentByType('electronAddFormView');
                    var borrowts = form.getComponent('borrowtsId').getValue();
                    if(!form.isValid()){
                        return;
                    }else{
                        if (borrowts == '' || borrowts == null||String(borrowts).indexOf(".")>-1||isNaN(borrowts)||parseInt(borrowts)<1 ) {
                            XD.msg('查档天数不合法');return;
                        }
                    }

                    form.submit({
                        waitTitle: '',// 标题
                        url: '/electron/electronAddForm',
                        method: 'POST',
                        params:{
                            eleids:window.wmedia
                        },
                        success: function () {
                            XD.msg('提交成功');
                            window.borrowElectronadd.close();
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'electronAddFormView button[itemId=electronAddClose]': {
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
                    window.borrowElectronadd.close();
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
            'electronAddFormView button[itemId=electronAddId]': {
                click: function (view) {
                    window.leadIn = Ext.create("Ext.window.Window", {
                        width: '100%',
                        height: '100%',
                        title: '添加电子文件',
                        modal: true,
                        header: false,
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        closeToolText: '关闭',
                        layout: 'fit',
                        items: [{xtype: 'electronicPro'}]
                    });
                    window.wform = view.findParentByType('electronAddFormView');
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
            'stLookAddFormView button[itemId=stElectronAddId]': {
                click: function (view) {
                    window.leadIn = Ext.create("Ext.window.Window", {
                        width: '100%',
                        height: '100%',
                        title: '添加电子文件',
                        modal: true,
                        header: false,
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        closeToolText: '关闭',
                        layout: 'fit',
                        items: [{xtype: 'electronicPro'}]
                    });
                    window.wform = view.findParentByType('stLookAddFormView');
                    window.ztid = 'undefined';
                    window.leadIn.down('electronicPro').initData(window.ztid);
                    window.leadIn.show();
                }
            }
        })
    },

    //进入模块主页面时加载列表数据
    initGrid: function (view) {
        // var tree = this.findGridView(view).down('treepanel');
        // var selectedNode = tree.selModel.getSelected().items[0];
        // if (selectedNode) {
        //     return;
        // }
        // Ext.defer(function () {
        //     view.nodeid = tree.getStore().getRoot().firstChild.get('fnid');
        //     view.getStore().proxy.extraParams.nodeid = view.nodeid;//加载列表数据
        //     view.initColumns(view);
        //     view.getStore().reload();
        // }, 1);
    },

    //获取查档利用应用视图
    findView: function (btn) {
        return btn.findParentByType('borrowView');
    },

    //获取表单界面视图
    findFormView: function (btn) {
        return this.findView(btn).down('form');
    },

    //获取列表界面视图
    findGridView: function (btn) {
        return this.findView(btn).getComponent('gridview');
    },

    //切换到列表界面视图
    activeGrid: function (btn, e) {
        var view = this.findView(btn);
        view.setActiveItem(this.findGridView(btn));
        var electronic = this.findFormView(btn).down('electronic');
        document.getElementById('mediaFrame').setAttribute('src', '');
        var solid = this.findFormView(btn).down('solid');
        document.getElementById('solidFrame').setAttribute('src', '');
        // var long = this.findFormView(btn).down('long');
        // document.getElementById('longFrame').setAttribute('src', '');
        //electronic.reset();
    },

    //切换到表单界面视图
    activeForm: function (btn, e) {
        var view = this.findView(btn);
        var formview = this.findFormView(btn);
        view.setActiveItem(formview);
        formview.items.get(0).enable();
        formview.setActiveTab(0);
        return formview;
    },

    activeEleForm: function (obj) {
        var view = this.findView(obj.grid);
        var formview = this.findFormView(obj.grid);
        view.setActiveItem(formview);
        formview.items.get(0).disable();
        var eleview = formview.down('electronic');
        eleview.initData(obj.entryid);
        var solidview = formview.down('solid');
        solidview.initData(obj.entryid);
        // var longview = formview.down('long');
        // longview.initData(obj.entryid);
        formview.setActiveTab(1);
        return formview;
    }
});

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