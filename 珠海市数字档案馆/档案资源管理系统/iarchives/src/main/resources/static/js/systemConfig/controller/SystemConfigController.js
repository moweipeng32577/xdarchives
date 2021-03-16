/**
 * Created by tanly on 2017/11/1 0024.
 */
Ext.define('SystemConfig.controller.SystemConfigController', {
    extend: 'Ext.app.Controller',

    views: ['SystemConfigView', 'SystemConfigGridView', 'SystemConfigPromptView','SxSystemConfigPromptView','SystemConfigSxGridView','SystemConfigImportView','SxSystemConfigImportView'],//加载view
    stores: ['SystemConfigTreeStore', 'SystemConfigGridStore','SxSystemConfigTreeStore'],//加载store
    models: ['SystemConfigTreeModel', 'SystemConfigGridModel'],//加载model
    init: function () {
        var ifShowRightPanel = false;
        var ifSxShowRightPanel = false;
        this.control({
            'systemConfigView [itemId=treepanelId]': {
                select: function (treemodel, record) {
                    window.treesettingview = treemodel.view;
                    var systemConfigView = treemodel.view.findParentByType('systemConfigView');
                    var systemConfigPromptView=systemConfigView.down('[itemId=systemConfigPromptViewID]');
                    if (!ifShowRightPanel) {
                        systemConfigPromptView.removeAll();
                        systemConfigPromptView.add({
                            xtype: 'systemConfigGridView'
                        });
                        ifShowRightPanel = true;
                    }
                    var gridView = systemConfigView.down('[itemId=systemConfigGridViewID]');
                    gridView.setTitle("当前位置：" + record.get('text'));
                    gridView.initGrid({xtType:window.xtType,configid: record.get('fnid')});
                    window.configid = record.get('fnid');
                    if(window.parent.realname=="系统管理员"){

                    }else {
                        gridView.down('[itemId=add]').hide();
                        gridView.down('[itemId=update]').hide();
                        gridView.down('[itemId=delete]').hide();
                    }
                }
            },'systemConfigView [itemId=sxTreepanelId]': {
                select: function (treemodel, record) {
                    window.sxTreesettingview = treemodel.view;
                    var systemConfigView = treemodel.view.findParentByType('systemConfigView');
                    var sxSystemConfigPromptView=systemConfigView.down('[itemId=sxSystemConfigPromptViewID]');
                    if (!ifSxShowRightPanel) {
                        sxSystemConfigPromptView.removeAll();
                        sxSystemConfigPromptView.add({
                            xtype: 'systemConfigSxGridView'
                        });
                        ifSxShowRightPanel = true;
                    }
                    var gridView = systemConfigView.down('[itemId=systemConfigSxGridViewID]');
                    gridView.setTitle("当前位置：" + record.get('text'));
                    gridView.initGrid({xtType:window.xtType,configid: record.get('fnid')});
                    window.sxConfigid = record.get('fnid');
                }
            },
            'systemConfigView':{
                tabchange:function(view){
                    if(view.activeTab.title == '档案系统'){
                        window.xtType='档案系统';
                        if(window.configid){//重新加载表单
                            var reportgrid=view.down('[itemId=systemConfigGridViewID]');
                            reportgrid.initGrid({xtType:window.xtType,configid:window.configid});
                        }

                    }else if(view.activeTab.title == '声像系统'){
                        window.xtType='声像系统';
                        if(window.sxConfigid){
                            var reportgrid=view.down('[itemId=systemConfigSxGridViewID]');
                            reportgrid.initGrid({xtType:window.xtType,configid:window.sxConfigid});
                        }
                    }
                }
            },
            'systemConfigGridView': {
                beforedrop: function (node, data, overmodel, position, dropHandlers) {
                    dropHandlers.cancelDrop();
                    if (data.records.length > 1) {
                        XD.msg('不支持批量选择拖拽排序，请选择一条数据');
                    } else {
                        XD.confirm('确认将分类[ ' + data.records[0].get('code') + ' ]移动到[ '
                            + overmodel.get('code') + ' ]的' + ("before" == position ? '前面吗' : '后面吗'), function () {
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
                                    xtType:window.xtType,
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
            'systemConfigSxGridView': {
                beforedrop: function (node, data, overmodel, position, dropHandlers) {
                    dropHandlers.cancelDrop();
                    if (data.records.length > 1) {
                        XD.msg('不支持批量选择拖拽排序，请选择一条数据');
                    } else {
                        XD.confirm('确认将分类[ ' + data.records[0].get('code') + ' ]移动到[ '
                            + overmodel.get('code') + ' ]的' + ("before" == position ? '前面吗' : '后面吗'), function () {
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
                                    xtType:window.xtType,
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
            'systemConfigGridView button[itemId=add]': {
                click: function (view) {
                    var treeid=window.treesettingview.selection.get('fnid')==''? 'undefined':window.treesettingview.selection.get('fnid');
                    Ext.Ajax.request({
                        url: '/systemconfig/testAdd/' + treeid,
                        params: {
                            xtType:window.xtType
                        },
                        method: 'post',
                        success: function (response) {
                            var respText = Ext.decode(response.responseText);
                            if (respText.success == true) {
                                var win = new Ext.create('SystemConfig.view.SystemConfigWindow', {
                                    title: '增加参数',
                                    classview: view.findParentByType('systemConfigView')
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
            'systemConfigSxGridView button[itemId=add]': {
                click: function (view) {
                    var treeid=window.sxTreesettingview.selection.get('fnid')==''? 'undefined':window.sxTreesettingview.selection.get('fnid');
                    Ext.Ajax.request({
                        url: '/systemconfig/testAdd/' + treeid,
                        params: {
                            xtType:window.xtType
                        },
                        method: 'post',
                        success: function (response) {
                            var respText = Ext.decode(response.responseText);
                            if (respText.success == true) {
                                var win = new Ext.create('SystemConfig.view.SystemConfigWindow', {
                                    title: '增加参数',
                                    classview: view.findParentByType('systemConfigView')
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
            'systemConfigGridView button[itemId=update]': {
                click: function (view) {
                    var systemConfigGridView = view.findParentByType('systemConfigGridView');
                    var select = systemConfigGridView.getSelectionModel();
                    if (select.getSelection().length != 1) {
                        XD.msg("请选择一条操作记录!");
                    } else if (select.getSelection().length > 1) {
                        XD.msg("只能选择一条操作记录!");
                    } else {
                        var win = new Ext.create('SystemConfig.view.SystemConfigWindow', {
                            title: '修改参数',
                            classview: view.findParentByType('systemConfigView')
                        });
                        win.down('form').loadRecord(select.getLastSelected());
                        win.show();
                    }
                }
            },
            'systemConfigSxGridView button[itemId=update]': {
                click: function (view) {
                    var systemConfigGridView = view.findParentByType('systemConfigSxGridView');
                    var select = systemConfigGridView.getSelectionModel();
                    if (select.getSelection().length != 1) {
                        XD.msg("请选择一条操作记录!");
                    } else if (select.getSelection().length > 1) {
                        XD.msg("只能选择一条操作记录!");
                    } else {
                        var win = new Ext.create('SystemConfig.view.SystemConfigWindow', {
                            title: '修改参数',
                            classview: view.findParentByType('systemConfigView')
                        });
                        win.down('form').loadRecord(select.getLastSelected());
                        win.show();
                    }
                }
            },
            'systemConfigGridView button[itemId=delete]': {
                click: function (view) {
                    var systemConfigGridView = view.findParentByType('systemConfigGridView');
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
            'systemConfigGridView [itemId=exportXLSX]':{
                click:function (btn) {
                    var systemConfigGridView = btn.findParentByType('systemConfigGridView');
                    var select = systemConfigGridView.getSelectionModel();
                    if (!select.hasSelection()) {
                        XD.msg("请选择操作记录!");
                        return;
                    }
                    var var1=select.getSelection().map(function (item) {
                        return item.data.id;
                    });
                    var node=window.treesettingview.selection.get('fnid');
                    if(!node){
                        XD.msg("请选择操作记录!");
                    }
                    Ext.Ajax.request({
                        url: '/systemconfig/exportXLSX',
                        params: {
                            ids:var1
                        },
                        method: 'get',
                        success:function(res){
                           var data=Ext.decode(res.responseText);
                            window.location.href="/systemconfig/getExport?fileName="+data.data
                        },
                        failure: function () {
                            XD.msg("操作失败");
                        }
                    });

                }
            },
            'systemConfigGridView [itemId=exportXLS]':{
                click:function (btn) {
                    var systemConfigGridView = btn.findParentByType('systemConfigGridView');
                    var select = systemConfigGridView.getSelectionModel();
                    if (!select.hasSelection()) {
                        XD.msg("请选择操作记录!");
                        return;
                    }
                    var var1=select.getSelection().map(function (item) {
                        return item.data.id;
                    });
                    var node=window.treesettingview.selection.get('fnid');
                    if(!node){
                        XD.msg("请选择操作记录!");
                    }
                    Ext.Ajax.request({
                        url: '/systemconfig/exportXLS',
                        params: {
                            ids:var1
                        },
                        method: 'get',
                        success:function(res){
                            var data=Ext.decode(res.responseText);
                            window.location.href="/systemconfig/getExport?fileName="+data.data
                        },
                        failure: function () {
                            XD.msg("操作失败");
                        }
                    });

                }

            },
            'systemConfigSxGridView [itemId=exportXLSX]':{
                click:function (btn) {
                    var systemConfigGridView = btn.findParentByType('systemConfigSxGridView');
                    var select = systemConfigGridView.getSelectionModel();
                    if (!select.hasSelection()) {
                        XD.msg("请选择操作记录!");
                        return;
                    }
                    var var1=select.getSelection().map(function (item) {
                        return item.data.id;
                    });
                    var node=window.sxTreesettingview.selection.get('fnid');
                    if(!node){
                        XD.msg("请选择操作记录!");
                    }
                    Ext.Ajax.request({
                        url: '/systemconfig/exportSXXLSX',
                        params: {
                            ids:var1
                        },
                        method: 'get',
                        success:function(res){
                            var data=Ext.decode(res.responseText);
                            window.location.href="/systemconfig/getExport?fileName="+data.data
                        },
                        failure: function () {
                            XD.msg("操作失败");
                        }
                    });
                }
            },
            'systemConfigSxGridView [itemId=exportXLS]':{
                click:function (btn) {
                    var systemConfigGridView = btn.findParentByType('systemConfigSxGridView');
                    var select = systemConfigGridView.getSelectionModel();
                    if (!select.hasSelection()) {
                        XD.msg("请选择操作记录!");
                        return;
                    }
                    var var1=select.getSelection().map(function (item) {
                        return item.data.id;
                    });
                    var node=window.sxTreesettingview.selection.get('fnid');
                    if(!node){
                        XD.msg("请选择操作记录!");
                    }
                    Ext.Ajax.request({
                        url: '/systemconfig/exportSXXLS',
                        params: {
                            ids:var1
                        },
                        method: 'get',
                        success:function(res){
                            var data=Ext.decode(res.responseText);
                            window.location.href="/systemconfig/getExport?fileName="+data.data
                        },
                        failure: function () {
                            XD.msg("操作失败");
                        }
                    });
                }
            },
            'systemConfigSxGridView [itemId=import]':{
                click:function (btn) {
                    var node=window.sxTreesettingview.selection.get('fnid');
                    var grid=btn.findParentByType('systemConfigSxGridView');
                    new Ext.create('SystemConfig.view.SxSystemConfigImportView',{
                        parentid:node,
                        wuserGridView:grid
                    }).show();
                }

            },
            'systemConfigGridView [itemId=import]':{
                click:function (btn) {
                    var node=window.treesettingview.selection.get('fnid');
                    var grid=btn.findParentByType('systemConfigGridView');
                    new Ext.create('SystemConfig.view.SystemConfigImportView',{
                        parentid:node,
                        wuserGridView:grid
                    }).show();
                }
            },
            'systemConfigImportView [itemId=import]':{
                click:function (btn) {
                    var win=btn.findParentByType('systemConfigImportView');
                    var form = win.down('form');
                    if(!form.isValid()){
                        XD.msg('有必填项没有填写，请处理后再提交');
                        return;
                    }
                    var parentid = form.down('[itemId=parentItemID]').setValue(win.parentid);

                    var that=this;
                    form.submit({
                        waitTitle: '提示',
                        waitMsg: '正在处理请稍后...',
                        url: '/systemconfig/importExcel',
                        method: 'POST',
                        success: function(reps) {
                            // var respText = Ext.decode(action.response.responseText);
                            // if (respText.success == true) {
                            XD.msg("导入成功");
                                // var url = respText.loginIp;
                                // var userid = respText.msg;
                                // url = url + '?szType=2&userid=' + userid;//用户增加修改2
                                // //发送跨域请求
                            //     // that.crossDomainByUrl(url);
                            // }else{
                            //     XD.msg(respText.msg);
                            // }
                            win.close();//添加成功后关闭窗口
                            win.wuserGridView.getStore().reload();
                        },
                        failure: function (reps,action) {
                            var msg = Ext.decode(action.response.responseText).msg;
                            XD.msg(msg);
                            // window.wuserGridView.getStore().reload();
                        }
                    });
                }
            },
            'sxSystemConfigImportView [itemId=import]':{
                click:function (btn) {
                    var win=btn.findParentByType('sxSystemConfigImportView');
                    var form = win.down('form');
                    if(!form.isValid()){
                        XD.msg('有必填项没有填写，请处理后再提交');
                        return;
                    }
                    var parentid = form.down('[itemId=parentItemID]').setValue(win.parentid);

                    var that=this;
                    form.submit({
                        waitTitle: '提示',
                        waitMsg: '正在处理请稍后...',
                        url: '/systemconfig/importSXExcel',
                        method: 'POST',
                        success: function(reps) {
                            // var respText = Ext.decode(action.response.responseText);
                            // if (respText.success == true) {
                            XD.msg("导入成功");
                            // var url = respText.loginIp;
                            // var userid = respText.msg;
                            // url = url + '?szType=2&userid=' + userid;//用户增加修改2
                            // //发送跨域请求
                            //     // that.crossDomainByUrl(url);
                            // }else{
                            //     XD.msg(respText.msg);
                            // }
                            win.close();//添加成功后关闭窗口
                            win.wuserGridView.getStore().reload();
                        },
                        failure: function (reps,action) {
                            var msg = Ext.decode(action.response.responseText).msg;
                            XD.msg("导入失败");
                            // window.wuserGridView.getStore().reload();
                        }
                    });
                }
            },'sxSystemConfigImportView [itemId=cancel]':{
                click:function (btn) {
                    var win =btn.findParentByType('sxSystemConfigImportView');
                    win.close()
                }
            },
            'systemConfigImportView [itemId=cancel]' :{
                click:function (btn) {
                    var win=btn.findParentByType('systemConfigImportView');
                    win.close()
                }
            },
            'systemConfigSxGridView button[itemId=delete]': {
                click: function (view) {
                    var systemConfigGridView = view.findParentByType('systemConfigSxGridView');
                    var select = systemConfigGridView.getSelectionModel();
                    if (!select.hasSelection()) {
                        XD.msg("请选择操作记录!");
                    } else {
                        if(window.sxTreesettingview.selection.get('fnid') == ''){
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
            'systemConfigWindow button[itemId=cancel]': {
                click: function (view) {
                    var systemConfigWindow = view.findParentByType('systemConfigWindow');
                    systemConfigWindow.close();
                }
            },
            'systemConfigWindow button[itemId=save]': {
                click: function (view) {
                    var systemConfigWindow = view.findParentByType('systemConfigWindow');
                    var form = systemConfigWindow.down('form');
                    var data = form.getValues();
                    if (data['code'] == '' || data['value'] == '') {
                        XD.msg("有必填项未填写");
                        return;
                    }
                    var URL = '/systemconfig/addSystemConfig';
                    var configid;
                    if(window.xtType=='声像系统'){
                        configid=window.sxTreesettingview.selection.get('fnid');
                    }else{
                        configid=window.treesettingview.selection.get('fnid')
                    }
                    if (systemConfigWindow.title == '增加参数') {
                        form.getForm().findField('parentconfigid').setValue(configid);
                        this.formSubmit(systemConfigWindow,form,URL);
                    }else{
                        if(configid == ''){
                            if(form.getForm().findField('value').isDirty()){
                                this.confirm("警告信息","请注意！<br/>该参数类型在相关界面可能已使用，如果修改参数值将导致其不能正常使用，请确认是否还要继续保存修改？", function () {
                                    this.formSubmit(systemConfigWindow,form,URL);
                                },this);
                            }else{
                                this.formSubmit(systemConfigWindow,form,URL);
                            }
                        }else{
                            if(form.getForm().findField('value').isDirty()||form.getForm().findField('code').isDirty()){
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
    },
    formSubmit : function (systemConfigWindow,form,URL) {
        form.submit({
            waitTitle: '提示',
            waitMsg: '正在提交数据请稍后...',
            url: URL,
            params: {
                xtType:window.xtType
            },
            method: 'POST',
            success: function (form, action) {
                var respText = Ext.decode(action.response.responseText);
                if (respText.success == true) {
                    var treeview;
                    var systemConfigGridView;
                    if(window.xtType=='声像系统'){
                        treeview= window.sxTreesettingview;
                        systemConfigGridView = systemConfigWindow.classview.down('[itemId=systemConfigSxGridViewID]');
                    }else{
                        treeview= window.treesettingview;
                        systemConfigGridView = systemConfigWindow.classview.down('[itemId=systemConfigGridViewID]');
                    }
                    var childNodes = treeview.selection.childNodes;
                    if (systemConfigWindow.title == '增加参数') {
                        systemConfigGridView.getStore().reload({
                            callback: function () {
                            }
                        });//重置callback
                        systemConfigWindow.close();
                        if (treeview.selection.data.fnid=='') {
                            treeview.selection.insertChild(childNodes.length, {
                                text: respText.data.configcode,
                                leaf: true,
                                fnid: respText.data.configid,
                                parentid: respText.data.parentconfigid
                            })
                        }
                        XD.msg(respText.msg);
                    } else {//修改
                        if (childNodes.length > 0) {
                            var treeStore = treeview.getStore();
                            treeStore.proxy.extraParams.parentconfigid = treeview.selection.get('fnid');
                            treeStore.load({node: treeview.selection, scope: this});
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
                XD.msg(respText.msg);
            }
        });
    },
    deleteRecord : function (systemConfigGridView,select) {
        var gridselections = select.getSelection();
        var gridstore = systemConfigGridView.getStore();
        var configid = [];
        for (var i = 0; i < gridselections.length; i++) {
            configid.push(gridselections[i].get("configid"));
        }
        var configids = configid.join(',');
        Ext.Ajax.request({
            url: '/systemconfig/systemconfigs/' + configids+'/'+window.xtType,
            method: 'delete',
            sync: true,
            success: function (response) {
                var respText = Ext.decode(response.responseText);
                XD.msg(respText.msg);
                var successid = respText.data.split(',');
                gridstore.loadPage(1, {//默认重新加载第一页
                    callback: function () {
                        var treeview;
                        if(window.xtType=='声像系统'){
                            treeview= window.sxTreesettingview;
                        }else{
                            treeview= window.treesettingview;
                        }
                        var newnode = {
                            text: treeview.selection.get('text'),
                            leaf: true,//改变样式
                            fnid: treeview.selection.get('fnid')
                        };
                        if (systemConfigGridView.getStore().totalCount == 0) {//无子
                            var selectid = treeview.selection.get('fnid');
                            if (selectid == '') {
                                var node = treeview.node;
                                while (node.hasChildNodes()) {
                                    node.removeChild(node.firstChild);
                                }
                            }else{
                                var parentnode = treeview.selection.parentNode;
                                // parentnode.replaceChild(newnode, window.treesettingview.selection);

                                var childNodes = parentnode.childNodes;
                                for (var j = 0; j < childNodes.length; j++) {
                                    if (childNodes[j].get('fnid') == selectid) {
                                        treeview.getSelectionModel().select(childNodes[j]);//重新选中
                                        break;
                                    }
                                }
                            }
                        } else {
                            var childNodes = treeview.selection.childNodes;
                            if (childNodes.length > 0) {
                                for (var i = 0; i < successid.length; i++) {
                                    var selectid = successid[i];
                                    for (var j = 0; j < childNodes.length; j++) {
                                        if (childNodes[j].get('fnid') == selectid||childNodes[j].get('fnid') == selectid.trim()) {
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