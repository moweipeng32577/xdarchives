Ext.define('AssemblyAdmin.controller.AssemblyAdminController', {
    extend: 'Ext.app.Controller',

    views: ['AssemblyAdminView','AssemblyAdminLinkSetView','AssemblyUserSetOrganTreeView',
        'AssemblyAdminUserSetView','AssemblyAdminLeftView','AssemblyAdminUserGridView',
        'AssemblyAdminUserSelectView','AssemblyAdminLinkTabView','AssemblyAdminPreLinkView',
        'AssemblyAdminAddView'],//加载view
    stores: ['AssemblyAdminGridStore','AssemblyAdminLinkSetStore','AssemblyUserSetStore',
        'AssemblyUserSetOrganTreeStore','AssemblyFlowStore','AssemblyAdminUserGridStore',
        'AssemblyAdminUserSelectStore','AssemblyPreflowStore'],//加载store
    models: ['AssemblyAdminGridModel','AssemblyAdminLinkSetModel','AssemblyUserSetOrganTreeModel',
        'AssemblyUserSetModel','AssemblyAdminUserGridModel'],//加载model

    init: function () {
        this.control({
            'AssemblyAdminView button[itemId=add]':{   //新增流水线
                click:function (btn) {
                    var assemblyAdminView = btn.findParentByType('AssemblyAdminView');
                    window.assemblyAdminView = assemblyAdminView;
                    var assemblyAdminAddView = Ext.create("AssemblyAdmin.view.AssemblyAdminAddView");
                    assemblyAdminAddView.subtype = 'add';
                    assemblyAdminAddView.code = null;
                    assemblyAdminAddView.show();
                }
            },
            'assemblyAdminAddView button[itemId=assemblyAddSubmit]':{   //新增流水线 提交
                click:function (btn) {
                    var assemblyAdminAddView = btn.findParentByType('assemblyAdminAddView');
                    var form = assemblyAdminAddView.down('form');
                    var title = form.down('[name=title]').getValue();
                    title = title.replace(/(^\s*)|(\s*$)/g, '');//去除空格;
                    if (title == '' || title == undefined || title == null) {
                        XD.msg('请输入流水线名称！');
                        return;
                    }
                    var remark = form.down('[name=remark]').getValue();
                    form.submit({
                        url:"/assemblyAdmin/setAssembly",
                        params:{
                            title:title,
                            remark:remark,
                            subtype:assemblyAdminAddView.subtype,
                            code:assemblyAdminAddView.code
                        },
                        success:function (form, action) {
                            XD.msg(action.result.msg);
                            window.assemblyAdminView.getStore().reload();
                            assemblyAdminAddView.close();
                        },
                        failure:function (form, action) {
                            XD.msg('操作失败，'+action.result.msg);
                        }
                    });
                }
            },
            'AssemblyAdminView button[itemId=edit]':{   //修改流水线
                click:function (btn) {
                    var assemblyAdminView = btn.findParentByType('AssemblyAdminView');
                    window.assemblyAdminView = assemblyAdminView;
                    var select = assemblyAdminView.getSelectionModel().getSelection();
                    if(select.length!=1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    var code = select[0].get("code");
                    var assemblyAdminAddView = Ext.create("AssemblyAdmin.view.AssemblyAdminAddView");
                    var form = assemblyAdminAddView.down('form');
                    assemblyAdminAddView.setTitle('修改流水线');
                    form.load({
                        url:"/assemblyAdmin/getAssembly",
                        params:{
                            code:code
                        },
                        success:function (form, action) {
                        },
                        failure:function (form, action) {
                            XD.msg('操作失败');
                        }
                    });
                    assemblyAdminAddView.subtype = 'edit';
                    assemblyAdminAddView.code =code;
                    assemblyAdminAddView.show();
                }
            },

            'AssemblyAdminView button[itemId=del]':{   //删除流水线
                click:function (btn) {
                    var assemblyAdminView = btn.findParentByType('AssemblyAdminView');
                    var select = assemblyAdminView.getSelectionModel().getSelection();
                    if(select.length < 1){
                        XD.msg('至少选择一条数据');
                        return;
                    }
                    var codes = [];
                    for(var i=0;i<select.length;i++){
                        codes.push(select[i].get('code'));
                    }
                    XD.confirm("将同时删除该流水线下所有加工批次及批次明细。是否确定要删除该"+select.length+"条流水线？",function () {
                        Ext.Ajax.request({
                            url:"/assemblyAdmin/delAssembly",
                            params:{
                                codes:codes
                            },
                            method:'POST',
                            success:function (response) {
                                XD.msg('删除成功');
                                assemblyAdminView.getStore().reload();
                                assemblyAdminView.getSelectionModel().clearSelections();
                            },
                            failure:function (response) {
                                XD.msg('操作失败');
                            }
                        });
                    });
                }
            },
            'AssemblyAdminView button[itemId=flows]':{   //环节设置
                click:function (btn) {
                    var assemblyAdminView = btn.findParentByType('AssemblyAdminView');
                    var select = assemblyAdminView.getSelectionModel().getSelection();
                    if(select.length != 1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    var id = select[0].get('id');
                    var title = select[0].get('title');
                    var adminLinkView = Ext.create('Ext.window.Window', {
                        width: 800,
                        height: 600,
                        title: '',
                        draggable: true,//可拖动
                        resizable: false,//禁止缩放
                        modal: true,
                        closeAction: 'hide',
                        closeToolText: '关闭',
                        layout: 'fit',
                        // header: false,
                        // closable: false,
                        items: [{
                            xtype: 'assemblyAdminLinkTabView'
                        }]
                    });
                    var assemblyAdminLinkSetView = adminLinkView.down('assemblyAdminLinkTabView').down('assemblyAdminLinkSetView');
                    var assemblyAdminPreLinkView = adminLinkView.down('assemblyAdminLinkTabView').down('assemblyAdminPreLinkView');
                    Ext.Ajax.request({
                        params: {
                            id:id,
                            type:null
                        },
                        url: '/assemblyAdmin/getLinkByid',
                        method: 'POST',
                        async:false,
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            if (respText.success == true) {
                                assemblyAdminLinkSetView.down('itemselector').getStore().load({
                                    callback:function(){
                                        assemblyAdminLinkSetView.down('itemselector').setValue(respText.data);
                                    }
                                });
                            }
                        },
                        failure: function() {
                            XD.msg('操作失败');
                        }
                    });
                    assemblyAdminLinkSetView.assemblyid = id;
                    assemblyAdminPreLinkView.assemblyid = id;
                    adminLinkView.title="流水线名："+title;
                    adminLinkView.show();
                }
            },
            'assemblyAdminLinkTabView':{   //切换选项卡
                tabchange:function (view) {
                    if(view.activeTab.title=='环节配置'){
                        var assemblyAdminLinkSetView = view.down('assemblyAdminLinkSetView');
                        Ext.Ajax.request({
                            params: {
                                id:assemblyAdminLinkSetView.assemblyid,
                                type:null
                            },
                            url: '/assemblyAdmin/getLinkByid',
                            method: 'POST',
                            success: function (resp) {
                                var respText = Ext.decode(resp.responseText);
                                if (respText.success == true) {
                                    assemblyAdminLinkSetView.down('itemselector').toField.store.removeAll();
                                    assemblyAdminLinkSetView.down('itemselector').getStore().load({
                                        callback:function(){
                                            assemblyAdminLinkSetView.down('itemselector').setValue(respText.data);
                                        }
                                    });
                                }
                            },
                            failure: function() {
                                XD.msg('操作失败');
                            }
                        });
                    }else if(view.activeTab.title=='前置环节'){
                        var assemblyAdminPreLinkView = view.down('assemblyAdminPreLinkView');
                        var store = assemblyAdminPreLinkView.down('combobox').getStore();
                        store.proxy.extraParams.id = assemblyAdminPreLinkView.assemblyid ;
                        store.proxy.extraParams.type = 'preflow';
                        store.load(function () {
                            assemblyAdminPreLinkView.down('combobox').select(store.getAt(0));
                            Ext.Ajax.request({
                                params: {
                                    id: assemblyAdminPreLinkView.assemblyid,
                                    assemblyflowid: store.getAt(0).get('id')
                                },
                                url: '/assemblyAdmin/getAssemblyPreflowByid',
                                method: 'POST',
                                success: function (resp) {
                                    var respText = Ext.decode(resp.responseText);
                                    if (respText.success == true) {
                                        assemblyAdminPreLinkView.down('itemselector').toField.store.removeAll();
                                        var linkStore = assemblyAdminPreLinkView.down('itemselector').getStore();
                                        linkStore.proxy.extraParams.id = assemblyAdminPreLinkView.assemblyid;
                                        linkStore.proxy.extraParams.assemblyflowid = store.getAt(0).get('id');
                                        linkStore.load({
                                            callback: function () {
                                                assemblyAdminPreLinkView.down('itemselector').setValue(respText.data);
                                            }
                                        });
                                    }
                                },
                                failure: function () {
                                    XD.msg('操作失败');
                                }
                            });
                        });
                    }
                }
            },
            'assemblyAdminLinkSetView button[itemId=linksetSubmit]':{
                click:function (btn) {
                    var assemblyAdminLinkSetView = btn.findParentByType('assemblyAdminLinkSetView');
                    if(assemblyAdminLinkSetView.down('itemselector').getValue().length==0){
                        XD.msg('至少选择一个环节');
                        return;
                    }
                    XD.confirm("修改环节设置需要重新设置前置环节，是否确定要修改",function () {
                        Ext.Ajax.request({
                            params: {
                                assemblyid: assemblyAdminLinkSetView.assemblyid,
                                ids:assemblyAdminLinkSetView.down('itemselector').getValue()},
                            url: '/assemblyAdmin/setLinkByid',
                            method: 'POST',
                            success: function (resp) {
                                var respText = Ext.decode(resp.responseText);
                                if (respText.success == true) {
                                    XD.msg("设置成功");
                                }else{
                                    XD.msg("设置失败");
                                }
                            },
                            failure: function() {
                                XD.msg('操作失败');
                            }
                        });
                    });
                }
            },
            'assemblyAdminLinkSetView button[itemId=linksetClose]':{
                click:function (btn) {
                    btn.findParentByType('window').close();
                }
            },
            'assemblyAdminPreLinkView button[itemId=setPrelinkSubmit]':{  //前置环节提交
                click:function (btn) {
                    var assemblyAdminPreLinkView = btn.findParentByType('assemblyAdminPreLinkView');
                    if(assemblyAdminPreLinkView.down('itemselector').getValue().length!=1){
                        XD.msg('选择一个环节');只能
                        return;
                    }
                    Ext.Ajax.request({
                        params: {
                            assemblyid: assemblyAdminPreLinkView.assemblyid,
                            preflowids:assemblyAdminPreLinkView.down('itemselector').getValue(),
                            assemblyflowid:assemblyAdminPreLinkView.assemblyflowid
                        },
                        url: '/assemblyAdmin/setPreLink',
                        method: 'POST',
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            if (respText.success == true) {
                                XD.msg("设置成功");
                            }else{
                                XD.msg("设置失败");
                            }
                        },
                        failure: function() {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'assemblyAdminPreLinkView button[itemId=setPrelinkClose]':{
                click:function (btn) {
                    btn.findParentByType('window').close();
                }
            },
            'AssemblyAdminView button[itemId=allot]':{   //人员分配
                click:function (btn) {
                    var assemblyAdminView = btn.findParentByType('AssemblyAdminView');
                    var select = assemblyAdminView.getSelectionModel().getSelection();
                    if(select.length != 1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    var id = select[0].get('id');
                    var title = select[0].get('title');
                    var assemblyAdminUserSetView = Ext.create('AssemblyAdmin.view.AssemblyAdminUserSetView');
                    assemblyAdminUserSetView.title="流水线名："+title;
                    assemblyAdminUserSetView.assemblyid = id;
                    Ext.Ajax.request({
                        params: {id:id},
                        url: '/assemblyAdmin/getAssemblyUserByid',
                        method: 'POST',
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            if (respText.success == true) {
                                assemblyAdminUserSetView.down('itemselector').getStore().load({
                                    callback:function(){
                                        assemblyAdminUserSetView.down('itemselector').setValue(respText.data);
                                    }
                                });
                                var store = assemblyAdminUserSetView.down('assemblyAdminLeftView').down('combobox').getStore();
                                store.proxy.extraParams.id = id ;
                                store.proxy.extraParams.type = null;
                                store.reload();
                            }
                        },
                        failure: function() {
                            XD.msg('操作失败');
                        }
                    });
                    assemblyAdminUserSetView.show();
                }
            },
            'assemblyUserSetOrganTreeView':{
                select:function (treemodel, record) {
                    var organid = record.data.fnid;
                    var userstore;
                    if(typeof treemodel.view.findParentByType('assemblyAdminUserSetView')=='undefined'){
                        userstore = treemodel.view.findParentByType('assemblyAdminUserSelectView').down('itemselector').getStore();
                    }else{
                        userstore = treemodel.view.findParentByType('assemblyAdminUserSetView').down('itemselector').getStore();
                    }
                    userstore.reload({params:{organid:organid}});
                }
            },
            'assemblyAdminUserSetView button[itemId=setUserSubmit]':{
                click:function (btn) {
                    var assemblyAdminUserSetView = btn.findParentByType('assemblyAdminUserSetView');
                    if(assemblyAdminUserSetView.down('itemselector').getValue().length==0){
                        XD.msg('至少选择一个用户');
                        return;
                    }
                    var assemblyflowid = assemblyAdminUserSetView.down('combobox').getValue();
                    Ext.Ajax.request({
                        params: {
                            assemblyid: assemblyAdminUserSetView.assemblyid,
                            ids:assemblyAdminUserSetView.down('itemselector').getValue(),
                            assemblyflowid:assemblyflowid
                        },
                        url: '/assemblyAdmin/setAssemblyUser',
                        method: 'POST',
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            if (respText.success == true) {
                                XD.msg("设置成功");
                            }else{
                                XD.msg("设置失败");
                            }
                        },
                        failure: function() {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'assemblyAdminUserSetView button[itemId=setUserClose]':{
                click:function (btn) {
                    btn.findParentByType('assemblyAdminUserSetView').close();
                }
            },
            'AssemblyAdminView button[itemId=adminuser]': {
                click: function (btn) {
                    var AssemblyAdminView = btn.findParentByType('AssemblyAdminView');
                    var select = AssemblyAdminView.getSelectionModel().getSelection();
                    if(select.length != 1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    var assemblyid = select[0].get('id');
                    var adminUserView = Ext.create('Ext.window.Window', {
                        width: '100%',
                        height: '100%',
                        title: '',
                        draggable: true,//可拖动
                        resizable: false,//禁止缩放
                        modal: true,
                        closeAction: 'hide',
                        closeToolText: '关闭',
                        layout: 'fit',
                        header: false,
                        closable: false,
                        items: [{
                            xtype: 'assemblyAdminUserGridView'
                        }]
                    });
                    var assemblyAdminUserGridView = adminUserView.down('assemblyAdminUserGridView');
                    assemblyAdminUserGridView.getStore().reload();
                    assemblyAdminUserGridView.getSelectionModel().clearSelections();
                    assemblyAdminUserGridView.assemblyid = assemblyid;
                    adminUserView.show();
                }
            },
            'assemblyAdminUserGridView button[itemId=backuser]': {
                click: function (btn) {
                    btn.findParentByType('assemblyAdminUserGridView').up('window').close();
                }
            },

            'assemblyAdminUserGridView button[itemId=adduser]':{   //添加管理员
                click:function (btn) {
                    var assemblyAdminUserGridView = btn.findParentByType('assemblyAdminUserGridView');
                    window.adminUserGridView = assemblyAdminUserGridView;
                    var assemblyAdminUserSelectView = Ext.create('AssemblyAdmin.view.AssemblyAdminUserSelectView');
                    Ext.Ajax.request({
                        params: {},
                        url: '/assemblyAdmin/getAdminUser',
                        method: 'POST',
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            if (respText.success == true) {
                                var store = assemblyAdminUserSelectView.down('itemselector').getStore();
                                store.proxy.extraParams['assemblyid'] = assemblyAdminUserGridView.assemblyid;
                                assemblyAdminUserSelectView.down('itemselector').getStore().load({
                                    callback:function(){
                                        assemblyAdminUserSelectView.down('itemselector').setValue(respText.data);
                                    }
                                });
                            }
                        },
                        failure: function() {
                            XD.msg('操作失败');
                        }
                    });
                    assemblyAdminUserSelectView.show();
                }
            },
            'assemblyAdminUserSelectView button[itemId=setUserSubmit]':{
                click:function (btn) {
                    var assemblyAdminUserSelectView = btn.findParentByType('assemblyAdminUserSelectView');
                    if(assemblyAdminUserSelectView.down('itemselector').getValue().length==0){
                        XD.msg('至少选择一个用户');
                        return;
                    }
                    Ext.Ajax.request({
                        params: {
                            ids:assemblyAdminUserSelectView.down('itemselector').getValue()
                        },
                        url: '/assemblyAdmin/setAdminUser',
                        method: 'POST',
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            if (respText.success == true) {
                                XD.msg("设置成功");
                                window.adminUserGridView.getStore().reload();
                                assemblyAdminUserSelectView.close();
                            }else{
                                XD.msg("设置失败");
                            }
                        },
                        failure: function() {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'assemblyAdminUserSelectView button[itemId=setUserClose]':{
                click:function (btn) {
                    btn.findParentByType('assemblyAdminUserSelectView').close();
                }
            },
            'assemblyAdminUserGridView button[itemId=deluser]':{
                click:function (btn) {
                    var assemblyAdminUserGridView = btn.findParentByType('assemblyAdminUserGridView');
                    var select = assemblyAdminUserGridView.getSelectionModel().getSelection();
                    if(select.length<1){
                        XD.msg('请至少选择一条数据');
                        return;
                    }
                    var userids = [];
                    for(var i=0;i<select.length;i++){
                        userids.push(select[i].get('id'));
                    }
                    XD.confirm("确定要删除这"+select.length+"条数据",function () {
                        Ext.Ajax.request({
                            url:'/assemblyAdmin/delAdminUser',
                            params:{
                                userids:userids
                            },
                            method: 'POST',
                            success: function (resp) {
                                var respText = Ext.decode(resp.responseText);
                                if (respText.success == true) {
                                    XD.msg("成功删除"+respText.data+"条数据");
                                    assemblyAdminUserGridView.getStore().reload();
                                }else{
                                    XD.msg("设置失败");
                                }
                            },
                            failure: function() {
                                XD.msg('操作失败');
                            }
                        });
                    });
                }
            }
        });
    }
});