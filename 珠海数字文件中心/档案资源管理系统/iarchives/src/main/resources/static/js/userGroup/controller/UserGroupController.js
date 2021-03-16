/**
 * Created by xd on 2017/10/21.
 */
Ext.define('UserGroup.controller.UserGroupController', {
     extend: 'Ext.app.Controller',

    views: ['UserGroupView','UserGroupAddForm','UserGroupSetGnView','UserGroupSetSjWin','UserGroupSetSjView',
        'UserGroupSetOrganWin','UserGroupSetOrganView','UserXitongTabView','UserGroupSetWjView','UserGroupSetWjWin',
        'LookUserGroupGridView','LookUserGroupView', 'LookUserGroupAddView','OrganTreeView'],//加载view
    stores: ['UserGroupStore','UserGroupSetGnStore','UserGroupSetSjStore','UserGroupSetOrganStore','UserGroupSetWjStore',
        'LookUserGroupGridStore','UserGroupSelectStore','OrganTreeStore'],//加载store
    models: ['UserGroupModel','UserGroupSetGnModel','UserGroupSetSjModel','UserGroupSetOrganModel',
        'LookUserGroupGridModel','OrganTreeModel','UserGroupSelectModel'],//加载model
    init: function () {
        this.control({
            'userGroupView':{
                afterrender:function (grid) {
                    grid.initGrid();
                },
                beforedrop: function (node, data, overmodel, position, dropHandlers) {
                    dropHandlers.cancelDrop();
                    if(data.records.length > 1){
                        XD.msg('不支持批量选择拖拽排序，请选择一条数据');
                    }else{
                        XD.confirm('确认将用户组[ '+data.records[0].get('rolename')+' ]移动到[ '
                            + overmodel.get('rolename')+' ]的' + ("before" == position?'前面吗':'后面吗'),function(){
                            var overorder = overmodel.get('orders');
                            var target;
                            if(typeof(overorder) == 'undefined'){
                                target = -1;
                            }else if("before" == position){
                                target = overorder;
                            }else if("after" == position){
                                target = overorder + 1;
                            }
                            Ext.Ajax.request({
                                url: '/userGroup/order/'+data.records[0].get('roleid')+'/'+target,
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

            'UserXitongTabView':{
                tabchange: function (view) {//tab页面切换触发
                    if (view.activeTab.title == '档案系统') {
                        window.userGroupViewTab='档案系统';
                    }
                    if (view.activeTab.title == '声像系统') {
                        window.userGroupViewTab='声像系统';
                    }
                    view.down('userGroupView').initGrid({xtType:window.userGroupViewTab});
                    view.down('userGroupView').getSelectionModel().clearSelections(); // 取消选择
                }
            },

            //新增用户组
            'userGroupView button[itemId="userGroupAdd"]':{
                click:function(view){
                    Ext.create("UserGroup.view.UserGroupAddForm").show();
                    window.wuserGroupGrid = view.findParentByType('userGroupView');
                }
            },

            //修改用户组
            'userGroupView button[itemId="userGroupUpdate"]':{
                click:function(view){
                    window.wuserGroupGrid = view.findParentByType('userGroupView');
                    var select = view.findParentByType('userGroupView').getSelectionModel();
                    var userGroup = select.getSelection();
                    var selectCount = userGroup.length;
                    if(selectCount!=1){
                        XD.msg('请选择一条数据');
                        return;
                    }
                    var editForm = Ext.create("UserGroup.view.UserGroupAddForm");
                    editForm.setTitle('修改用户组');
                    var name = userGroup[0].get('rolename');
                    if (name === '安全保密管理员' || name === '系统管理员' || name === '安全审计员') {
                        editForm.down('[itemId=userGroupAddSubmit]').hide();
                    }
                    editForm.down('form').load({
                        url: '/userGroup/getUserGroupByid?roleid='+userGroup[0].get('roleid'),
                        success : function(form, action) {
                            var data=action.result.data;
                            editForm.show();
                        },
                        failure: function() {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            //删除用户组
            'userGroupView button[itemId="userGroupDel"]':{
                click:function(view){
                    var grid = view.findParentByType('userGroupView');
                    var select = grid.getSelectionModel();
                    var userGroups = select.getSelection();
                    var selectCount = userGroups.length;
                    if(selectCount==0){
                        XD.msg('至少选择一条数据');
                        return;
                    }else{
                        var flag=true;
                        Ext.each(userGroups, function (userGroup) {
                            var name = userGroup.get('rolename');
                            if (name === '安全保密管理员' || name === '系统管理员' || name === '安全审计员') {
                                flag=false;
                            }
                        });
                        if(!flag){
                            XD.msg('请勿对三员进行删除操作');
                            return;
                        }
                        var that=this;
                        XD.confirm('是否确定删除',function () {
                            var groupIds = [];
                            for(var i=0;i<userGroups.length;i++){
                                groupIds.push(userGroups[i].get('roleid'));
                            }
                            Ext.Ajax.request({
                                params: {groupIds:groupIds},
                                url: '/userGroup/userGroupDel',
                                method: 'POST',
                                sync: true,
                                success: function (resp,opts) {
                                    var respText=Ext.decode(resp.responseText);
                                    XD.msg(respText.msg);
                                    grid.delReload(selectCount);

                                    /*if(respText.success == true){
                                        var url=respText.data.login_ip;
                                        var roleid=respText.data.remark;
                                        url=url+'?szType=90&roleid='+roleid;//删除用户组90
                                        //发送跨域请求
                                        that.crossDomainByUrl(url);
                                    }*/
                                },
                                failure: function() {
                                    XD.msg('操作失败');
                                }
                            });
                        });
                    }
                }
            },

            //设置用户组功能权限
            'userGroupView button[itemId="userGroupGnSeting"]':{
                click:function(view){
                    var grid = view.findParentByType('userGroupView');
                    var select = grid.getSelectionModel();
                    var userGroup = select.getSelection();
                    var selectCount = userGroup.length;
                    if(selectCount!=1){
                        XD.msg('请选择一条数据');
                        return;
                    }
                    var name = userGroup[0].get('rolename');
                    if (name === '安全保密管理员' || name === '系统管理员' || name === '安全审计员') {
                        XD.msg('请勿设置三员的功能权限');
                        return;
                    }
                    window.roleid = userGroup[0].get('roleid');
                    var gnWin = Ext.create('UserGroup.view.UserGroupSetGnWin');
                    var treeView = gnWin.getComponent("userGroupSetGnView");
                    treeView.getStore().proxy.extraParams.fnid = 1;
                    treeView.getStore().proxy.extraParams.xtType = window.userGroupViewTab;
                    treeView.getStore().load();
                    treeView.on('load', function () {
                        //treeView.expandAll();//展开全部节点
                    });
                    gnWin.show();
                }
            },

            //设置用户组数据权限
            'userGroupView button[itemId="userGroupSjSeting"]':{
                click:function(view){
                    var grid = view.findParentByType('userGroupView');
                    var select = grid.getSelectionModel();
                    var userGroup = select.getSelection();
                    var selectCount = userGroup.length;
                    if(selectCount!=1){
                        XD.msg('请选择一条数据');
                        return;
                    }

                    var name = userGroup[0].get('rolename');
                    if (name === '安全保密管理员' || name === '系统管理员' || name === '安全审计员') {
                        XD.msg('请勿设置三员的数据权限');
                        return;
                    }
                    window.roleid = userGroup[0].get('roleid');
                    var gnWin = Ext.create('UserGroup.view.UserGroupSetSjWin');
                    var treeView = gnWin.getComponent("userGroupSetSjView");
                    treeView.getStore().proxy.extraParams.pcid = '';
                    treeView.getStore().proxy.extraParams.xtType = window.userGroupViewTab;
                    treeView.getStore().load();
                    treeView.on('load', function () {
                        treeView.getRootNode().expandChildren();//展开首层节点
                        treeView.up('[itemId=userGroupSetSjWinId]').down('[itemId=selectedCountItem]').setText('已选择 '+treeView.getChecked().length+' 个节点');
                        //
                        // for(var i=0;i<treeView.getStore().getCount();i++){
                        //     var record = treeView.getStore().getAt(i);
                        //
                        //     // if(record.get('roottype')==='classification'){
                        //     //     record.set("checked",false);
                        //     // }
                        // }
                    });
                    gnWin.show();
                }
            },

            'userGroupSetGnWin [itemId="userGroupSetGnView"]': {
                render: function (view) {
                    //设置子节点被选中/不选中
                    var setChildLoop = function (node, check) {
                        node.set('checked', check);
                        node.expand();
                        if (node.isNode) {
                            node.eachChild(function (child) {
                                setChildLoop(child, check);
                            });
                        }
                    };
                    //设置父节点被选中
                    var setParentCheckedLoop = function (node, check) {
                        if (node !== null) {
                            node.set('checked', check);
                            setParentCheckedLoop(node.parentNode, check);
                        }
                    };
                    //node下是否有被勾选的节点
                    var hasCheckedChild = function (node, found) {
                        for (var i = 0; i < node.childNodes.length; i++) {
                            var child = node.childNodes[i];
                            if (child.get('checked')) {
                                found = true;
                                break;
                            } else if (child.childNodes !== null) {
                                found = hasCheckedChild(child, found);
                                if (found) {
                                    break;
                                }
                            }
                        }
                        return found;
                    };
                    //设置父节点不被选中
                    var setParentUncheckedLoop = function (node) {
                        if (node !== null && node.childNodes !== null && !hasCheckedChild(node, false)) {
                            node.set('checked', false);
                            setParentUncheckedLoop(node.parentNode);
                        }
                    };
                    view.on('checkchange', function (node, checked) {
                        node.expand();
                        node.eachChild(function (child) {
                            child.set("checked", checked);
                            node.expand();
                            setChildLoop(child, checked);
                        });
                        if (!checked) {
                            setParentUncheckedLoop(node.parentNode);
                        } else {
                            setParentCheckedLoop(node.parentNode, checked);
                        }
                    }, view);
                }
            },
            'userGroupSetSjWin [itemId="userGroupSetSjView"]': {
                render: function (view) {
                    //设置子节点被选中/不选中
                    var setChildLoop = function (node, check) {
                        node.set('checked', check);
                        node.expand();
                        if (node.isNode) {
                            node.eachChild(function (child) {
                                setChildLoop(child, check);
                            });
                        }
                    };
                    //设置父节点被选中
                    var setParentCheckedLoop = function (node, check) {
                        if (node !== null) {
                            if (node.get('roottype') === 'classification') {
                                node.set('checked', check);
                            }
                            setParentCheckedLoop(node.parentNode, check);
                        }
                    };
                    //获取node属于的分类节点
                    var getParentLoop = function (node) {
                        if (node !== null) {
                            if (node.get('roottype') === 'classification') {
                                return node;
                            } else {
                                return getParentLoop(node.parentNode);
                            }
                        } else {
                            return null;
                        }
                    };
                    //node下是否有被勾选的节点
                    var hasCheckedChild = function (node, found) {
                        for (var i = 0; i < node.childNodes.length; i++) {
                            var child = node.childNodes[i];
                            if (child.get('checked')) {
                                found = true;
                                break;
                            } else if (child.childNodes !== null) {
                                found = hasCheckedChild(child, found);
                                if (found) {
                                    break;
                                }
                            }
                        }
                        return found;
                    };
                    //设置父节点不被选中
                    var setParentUncheckedLoop = function (node) {
                        if (node !== null) {
                            var parentNode = getParentLoop(node);
                            if (parentNode !== null && parentNode.childNodes !== null && !hasCheckedChild(parentNode, false)) {
                                parentNode.set('checked', false);
                                setParentUncheckedLoop(node.parentNode);
                            }
                        }
                    };

                    view.on('checkchange', function(node, checked) {
                        node.expand();
                        node.eachChild(function(child) {
                            child.set("checked",checked);
                            node.expand();
                            setChildLoop(child,checked);
                        });

                        if (!checked) {
                            if (node.parentNode !== null) {//不是最高节点
                                setParentUncheckedLoop(node.parentNode);
                            }
                        } else {
                            setParentCheckedLoop(node.parentNode, checked);
                        }
                        view.up('[itemId=userGroupSetSjWinId]').down('[itemId=selectedCountItem]').setText('已选择 '+view.getChecked().length+' 个节点');
                    }, view);
                }
            },
            //设置用户组机构权限
            'userGroupView button[itemId="userGroupOrganSet"]':{
                click:function(view){
                    var grid = view.findParentByType('userGroupView');
                    var select = grid.getSelectionModel();
                    var userGroup = select.getSelection();
                    var selectCount = userGroup.length;
                    if(selectCount!=1){
                        XD.msg('请选择一条数据');
                        return;
                    }
                    window.roleid = userGroup[0].get('roleid');
                    var gnWin = Ext.create('UserGroup.view.UserGroupSetOrganWin');
                    var treeView = gnWin.getComponent("userGroupSetOrganView");
                    treeView.getStore().proxy.extraParams.pcid = '0';
                    treeView.getStore().load();
                    treeView.on('load', function () {
                        treeView.expandAll();//展开全部节点
                    });
                    gnWin.show();
                }
            },

            //增加用户组提交表单
           'userGroupAddForm button[itemId="userGroupAddSubmit"]':{
                click:function(view){
                    var form = view.findParentByType('userGroupAddForm').down('form');
                    var formdata = form.getValues();
                    var name = formdata['rolename'];
                    if (name === '') {
                        XD.msg('用户组名不能为空');
                        return;
                    }
                    if (name === '安全保密管理员' || name === '系统管理员' || name === '安全审计员') {
                        XD.msg('请勿添加与三员同名用户组');
                        return;
                    }
                    form.submit({
                        url : '/userGroup/userGroupAddSubmit',
                        method : 'POST',
                        scope: this,
                        params : { // 此处可以添加额外参数

                        },
                        success : function(form, action) {
                            var respText = Ext.decode(action.response.responseText);
                            if (respText.success == true) {
                                XD.msg(respText.msg);
                                view.findParentByType('userGroupAddForm').close();//添加成功后关闭窗口

                               /* var url=respText.data.login_ip;
                                var roleid=respText.data.remark;
                                url=url+'?szType=9&roleid='+roleid;//同步用户组
                                //发送跨域请求
                                this.crossDomainByUrl(url);*/

                                window.wuserGroupGrid.notResetInitGrid();
                            } else {
                                XD.msg(respText.msg);
                            }
                        },
                        failure : function() {
                            XD.msg('操作失败');
                        }
                    });
               }
            },

            //增加用户组窗口关闭
            'userGroupAddForm button[itemId="userGroupAddClose"]':{
                click:function(view){
                    view.findParentByType('userGroupAddForm').close();
                }
            },

            'userGroupSetGnWin button[itemId="userGroupSetGnSubmit"]':{
                click:function(view){
                    var treeWinView = view.findParentByType('userGroupSetGnWin');
                    var treeView = treeWinView.getComponent("userGroupSetGnView");
                    var selNodes = treeView.getChecked();
                    var gnList = new Array();
                    Ext.each(selNodes, function (node) {
                        gnList.push(node.data.fnid);
                    });
                    var that=this;
                    Ext.Msg.wait('正在进行功能权限设置操作，请耐心等待……','正在操作');
                    Ext.Ajax.request({
                        params: {gnList:gnList,roleId:window.roleid,xtType: window.userGroupViewTab},
                        url: '/userGroup/UserGroupSetGnSubmit',
                        method: 'POST',
                        sync: true,
                        timeout:XD.timeout,
                        success: function (resp) {
                            Ext.Msg.wait('功能权限设置操作完成','正在操作').hide();
                            var respText=Ext.decode(resp.responseText);
                            XD.msg(respText.msg);
                            /*if(window.userGroupViewTab=='声像系统'){
                                var url=respText.data.login_ip;
                                url=url+'?szType=7&roleid='+window.roleid;
                                //发送跨域请求
                                that.crossDomainByUrl(url);
                            }*/
                            treeWinView.close();
                        },
                        failure: function() {
                            Ext.Msg.wait('功能权限设置操作失败','正在操作').hide();
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'userGroupSetGnWin button[itemId="userGroupSetGnWinClose"]':{
                click:function(view){
                    view.findParentByType('userGroupSetGnWin').close();
                }
            },

            'userGroupSetSjWin button[itemId="userGroupSetSjSubmit"]':{
                click:function(view){
                    var treeWinView = view.findParentByType('userGroupSetSjWin');
                    var treeView = treeWinView.getComponent("userGroupSetSjView");
                    var selNodes = treeView.getChecked();
                    var gnList = new Array();
                    Ext.each(selNodes, function (node) {
                        gnList.push(node.data.fnid);
                    });
                    var nodeStr = gnList.join(',');
                    var that=this;
                    Ext.Msg.wait('正在进行数据权限设置操作，请耐心等待……','正在操作');
                    Ext.Ajax.request({
                        params: {nodeStr:nodeStr,roleId:window.roleid,xtType: window.userGroupViewTab},
                        url: '/userGroup/UserGroupSetSjSubmit',
                        timeout:1000000,
                        method: 'POST',
                        sync: true,
                        success: function (resp) {
                            Ext.Msg.wait('数据权限设置操作成功','正在操作').hide();
                            var respText=Ext.decode(resp.responseText);
                            XD.msg(respText.msg);
                            /*if(window.userGroupViewTab=='声像系统'){
                                var url=respText.data.login_ip;
                                url=url+'?szType=8&roleid='+window.roleid;//用户组数据节点权限8
                                //发送跨域请求
                                that.crossDomainByUrl(url);
                            }*/
                            view.findParentByType('userGroupSetSjWin').close();
                        },
                        failure: function() {
                            Ext.Msg.wait('数据权限设置操作失败','正在操作').hide();
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'userGroupSetOrganWin button[itemId="userGroupSetOrganSubmit"]':{
                click:function(view){
                    var treeWinView = view.findParentByType('userGroupSetOrganWin');
                    var treeView = treeWinView.getComponent("userGroupSetOrganView");
                    var selNodes = treeView.getChecked();
                    var organList = [];
                    Ext.each(selNodes, function (node) {
                        organList.push(node.data.fnid);
                    });
                    Ext.Msg.wait('正在进行机构权限设置操作，请耐心等待……','正在操作');
                    Ext.Ajax.request({
                        params: {organList:organList,roleId:window.roleid},
                        url: '/userGroup/userGroupSetOrganSubmit',
                        method: 'POST',
                        sync: true,
                        success: function (resp) {
                            Ext.Msg.wait('机构权限设置操作成功','正在操作').hide();
                            XD.msg(Ext.decode(resp.responseText).msg);
                            view.findParentByType('userGroupSetOrganWin').close();
                        },
                        failure: function() {
                            Ext.Msg.wait('机构权限设置操作失败','正在操作').hide();
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'userGroupSetSjWin button[itemId="userGroupSetSjWinClose"]':{
                click:function(view){
                    view.findParentByType('userGroupSetSjWin').close();
                }
            },
            'userGroupSetOrganWin button[itemId="userGroupSetOrganWinClose"]':{
                click:function(view){
                    view.findParentByType('userGroupSetOrganWin').close();
                }
            },
            'userGroupSetSjView':{
                itemcontextmenu: function (view, record, item, index, e, eOpts) {
                    //设置分类子节点被选中/不选中
                    var setCheckLoop = function (node, check) {
                        if (node.get('roottype') === 'classification') {
                            node.set('checked', check);
                            node.expand();
                            if (node.isNode) {
                                node.eachChild(function (child) {
                                    setCheckLoop(child, check);
                                });
                            }
                        }
                    };
                    //展开与收起
                    var setExpandLoop = function (node, expand) {
                        if (expand) {
                            node.expand();
                        } else {
                            node.collapse();
                        }
                        if (node.isNode) {
                            node.eachChild(function (child) {
                                setExpandLoop(child, expand);
                            });
                        }
                    };
                    //设置父节点被选中
                    var setParentCheckedLoop = function (node, check) {
                        if (node !== null) {
                            if (node.get('roottype') === 'classification') {
                                node.set('checked', check);
                            }
                            setParentCheckedLoop(node.parentNode, check);
                        }
                    };
                    //获取node属于的分类节点
                    var getParentLoop = function (node) {
                        if (node !== null) {
                            if (node.get('roottype') === 'classification') {
                                return node;
                            } else {
                                return getParentLoop(node.parentNode);
                            }
                        } else {
                            return null;
                        }
                    };
                    //node下是否有被勾选的节点
                    var hasCheckedChild = function (node, found) {
                        for (var i = 0; i < node.childNodes.length; i++) {
                            var child = node.childNodes[i];
                            if (child.get('checked')) {
                                found = true;
                                break;
                            } else if (child.childNodes !== null) {
                                found = hasCheckedChild(child, found);
                                if (found) {
                                    break;
                                }
                            }
                        }
                        return found;
                    };
                    //设置父节点不被选中
                    var setParentUncheckedLoop = function (node) {
                        if (node !== null) {
                            var parentNode = getParentLoop(node);
                            if (parentNode !== null && parentNode.childNodes !== null && !hasCheckedChild(parentNode, false)) {
                                parentNode.set('checked', false);
                                setParentUncheckedLoop(node.parentNode);
                            }
                        }
                    };

                    var singleTitle = '单选';
                    if(view.selection.get('checked')){
                        singleTitle = '取消单选'
                    }
                    e.preventDefault();//取消浏览器的默认右键点击事件
                    item.ctxMenu = new Ext.menu.Menu({
                        margin: '0 0 10 0',
                        items: [{
                            text: '单选分类',
                            itemId: 'classSingle',
                            iconCls: 'x-ctxmenu-checked-icon',
                            handler:function () {
                                view.selection.set("checked", true);//当前节点选中
                                setCheckLoop(view.selection, true);//分类子节点选中
                                setParentCheckedLoop(view.selection, true);//父节点选中
                            }
                        },{
                            text: singleTitle,
                            itemId: 'selectedAll',
                            iconCls: 'x-ctxmenu-checked-icon',
                            handler: function () {
                                if (singleTitle === '单选') {
                                    view.selection.set("checked", true);
                                    setParentCheckedLoop(view.selection, true);
                                } else {
                                    view.selection.set("checked", false);
                                    if (view.selection.parentNode !== null) {//不是最高节点
                                        setParentUncheckedLoop(view.selection.parentNode);
                                    }
                                }
                                view.up('[itemId=userGroupSetSjWinId]').down('[itemId=selectedCountItem]').setText('已选择 '+view.getChecked().length+' 个节点');
                            }
                        },{
                            text: "全部展开",
                            itemId: 'expandId',
                            iconCls: 'x-ctxmenu-expand-icon',
                            handler: function () {
                                view.selection.expand();
                                view.selection.eachChild(function (child) {
                                    child.expand();
                                    setExpandLoop(child, true);
                                });
                            }
                        }, {
                            text: "全部收缩",
                            itemId: 'collapseId',
                            iconCls: 'x-ctxmenu-collapse-icon',
                            handler: function () {
                                view.selection.collapse();
                                view.selection.eachChild(function (child) {
                                    child.collapse();
                                    setExpandLoop(child, false);
                                });
                            }
                        }
                        ]
                    }).showAt(e.getXY());

                    if (view.selection.get('roottype') === 'classification') {
                        if(view.selection.get('checked')){
                            item.ctxMenu.remove(item.ctxMenu.getComponent('classSingle'));
                        }
                        item.ctxMenu.remove(item.ctxMenu.getComponent('selectedAll'));
                    }
                    if (view.selection.get('roottype') === 'unit') {
                        item.ctxMenu.remove(item.ctxMenu.getComponent('classSingle'));
                    }
                }
            },

            'userGroupView button[itemId=userGroupWJQX]':{ //文件权限设置
                click : function (btn) {
                    var userGroupView = btn.findParentByType('userGroupView');
                    var select = userGroupView.getSelection();
                    if (select.length != 1) {
                        XD.msg('请选择一条操作');
                    } else {
                        var flag = true;
                        Ext.each(select, function (user) {
                            var name = user.data.realname;
                            if (name === '安全保密管理员' || name === '系统管理员' || name === '安全审计员') {
                                flag = false;
                            }
                        });
                        if (!flag) {
                            XD.msg('请勿设置三员用户的文件权限');
                            return;
                        }
                        var userGroupids = new Array();
                        Ext.each(select, function (useGroup) {
                            userGroupids.push(useGroup.data.roleid);
                        });
                        var userGroupStr = userGroupids.join(',');
                        window.userGroupids = userGroupStr;
                        var form = Ext.create("UserGroup.view.UserGroupSetWjWin");
                        var treeView = form.getComponent("userGroupSetWjView");
                        treeView.getStore().proxy.extraParams.xtType = window.userGroupViewTab;
                        treeView.getStore().load();
                        form.show();
                    }
                }
            },
            'userGroupSetWjWin [itemId="userGroupSetWjView"]': {
                render: function (view) {
                    //设置子节点被选中/不选中
                    var setChildLoop = function (node, check) {
                        node.set('checked', check);
                        node.expand();
                        if (node.isNode) {
                            node.eachChild(function (child) {
                                setChildLoop(child, check);
                            });
                        }
                    };
                    //设置父节点被选中
                    var setParentCheckedLoop = function (node, check) {
                        if (node !== null) {
                            node.set('checked', check);
                            setParentCheckedLoop(node.parentNode, check);
                        }
                    };
                    //node下是否有被勾选的节点
                    var hasCheckedChild = function (node, found) {
                        for (var i = 0; i < node.childNodes.length; i++) {
                            var child = node.childNodes[i];
                            if (child.get('checked')) {
                                found = true;
                                break;
                            } else if (child.childNodes !== null) {
                                found = hasCheckedChild(child, found);
                                if (found) {
                                    break;
                                }
                            }
                        }
                        return found;
                    };
                    //设置父节点不被选中
                    var setParentUncheckedLoop = function (node) {
                        if (node !== null && node.childNodes !== null && !hasCheckedChild(node, false)) {
                            node.set('checked', false);
                            setParentUncheckedLoop(node.parentNode);
                        }
                    };
                    view.on('checkchange', function (node, checked) {
                        node.expand();
                        node.eachChild(function (child) {
                            child.set("checked", checked);
                            node.expand();
                            setChildLoop(child, checked);
                        });
                        if (!checked) {
                            setParentUncheckedLoop(node.parentNode);
                        } else {
                            setParentCheckedLoop(node.parentNode, checked);
                        }
                    }, view);
                }
            },
            'userGroupSetWjWin button[itemId=userGroupSetWjSubmit]': {//文件权限提交
                click: function (btn) {
                    var userSetWjWin = btn.findParentByType('userGroupSetWjWin');
                    var userSetWjView = userSetWjWin.down('userGroupSetWjView');
                    var selNodes = userSetWjView.getChecked();
                    var lylist = new Array();
                    var gllist = new Array();
                    Ext.each(selNodes, function (node) {
                        switch( node.data.fnid){
                            case '利用平台':
                                lylist.push(node.data.text);
                                break;
                            case '管理平台':
                                gllist.push(node.data.text);
                                break;
                            default:
                                break;
                        }
                    });
                    Ext.Ajax.request({
                        url: '/userGroup/setWJQXbtn',
                        method: 'post',
                        params:{
                            lylist:lylist,
                            gllist:gllist,
                            userid: window.userGroupids
                        },
                        async:false,
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            XD.msg(respText.msg);
                            userSetWjWin.close();
                        }
                    });
                }
            },

            'userGroupSetWjWin button[itemId=userGroupSetWjClose]': {//文件权限关闭
                click: function (btn) {
                    var userSetWjWin = btn.findParentByType('userGroupSetWjWin');
                    userSetWjWin.close();
                }
            },

            //查看组内用户
            'userGroupView button[itemId="lookUserGroup"]':{
                click:function(view){
                    var select = view.findParentByType('userGroupView').getSelectionModel().getSelection();
                    if(select.length != 1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    var roleid = select[0].get('roleid');
                    var lookUserGroupView = Ext.create("UserGroup.view.LookUserGroupView");
                    var lookUserGroupGridView = lookUserGroupView.down('lookUserGroupGridView');
                    lookUserGroupGridView.roleid = roleid;
                    lookUserGroupGridView.initGrid({roleid:roleid});
                    lookUserGroupView.show();
                }
            },

            //查看组内用户-返回
            'lookUserGroupGridView button[itemId="closeUserOnGroup"]':{
                click:function(view){
                    view.findParentByType('lookUserGroupView').close();
                }
            },

            //查看组内用户-设置
            'lookUserGroupGridView button[itemId="addUserOnGroup"]':{
                click:function(view){
                    var lookUserGroupGridView = view.findParentByType('lookUserGroupGridView');
                    Ext.Ajax.request({
                        params: {
                            roleid: lookUserGroupGridView.roleid
                        },
                        url: '/userGroup/getUsers',
                        method: 'POST',
                        sync: true,
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            var selectItem = Ext.create("UserGroup.view.LookUserGroupAddView");
                            selectItem.down('itemselector').getStore().proxy.extraParams.organid = '';
                            selectItem.down('itemselector').getStore().load({
                                callback: function () {
                                    selectItem.down('itemselector').setValue(respText.data);
                                }
                            });
                            selectItem.roleid = lookUserGroupGridView.roleid;
                            selectItem.lookUserGroupGridView = lookUserGroupGridView;
                            selectItem.show();
                        },
                        failure: function() {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            //设置组内用户-提交
            'lookUserGroupAddView button[itemId="addSubmit"]':{
                click:function(view){
                    var lookUserGroupAddView = view.findParentByType('lookUserGroupAddView');
                    var itemselector = lookUserGroupAddView.down('itemselector');
                    var userids = itemselector.getValue();
                    if(userids.length == 0){
                        XD.msg("请至少选择一个用户");
                        return;
                    }
                    Ext.Msg.wait('正在进行设置组内用户操作，请耐心等待……','正在操作');
                    Ext.Ajax.request({
                        params: {
                            roleid: lookUserGroupAddView.roleid,
                            userids:userids
                        },
                        url: '/userGroup/addUsers',
                        method: 'POST',
                        success: function (resp) {
                            Ext.MessageBox.hide();
                            var rsp = Ext.decode(resp.responseText);
                            if(rsp.success){
                                XD.msg("设置成功");
                                lookUserGroupAddView.lookUserGroupGridView.getStore().reload();
                                lookUserGroupAddView.close();
                            }else{
                                XD.msg("设置失败");
                            }
                        },
                        failure: function() {
                            Ext.MessageBox.hide();
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            //设置组内用户-关闭
            'lookUserGroupAddView button[itemId="addClose"]':{
                click:function(view){
                    view.findParentByType('lookUserGroupAddView').close();
                }
            },

            //查看组内用户-删除
            'lookUserGroupGridView button[itemId="delUserOnGroup"]':{
                click:function(view){
                    var lookUserGroupGridView = view.findParentByType('lookUserGroupGridView');
                    var select = lookUserGroupGridView.getSelectionModel().getSelection();
                    if(select.length == 0){
                        XD.msg("至少选择一条数据");
                        return;
                    }
                    var userids = [];
                    for(var i=0;i<select.length;i++){
                        userids.push(select[i].get('userid'));
                    }
                    XD.confirm("是否确定要删除这"+select.length+"个组内用户",function () {
                        Ext.Ajax.request({
                            params: {
                                roleid: lookUserGroupGridView.roleid,
                                userids:userids
                            },
                            url: '/userGroup/delUserOnGroup',
                            method: 'POST',
                            success: function (resp) {
                                var rsp = Ext.decode(resp.responseText);
                                if(rsp.success){
                                    XD.msg("删除成功");
                                    lookUserGroupGridView.getStore().reload();
                                }else{
                                    XD.msg("删除失败");
                                }
                            },
                            failure: function() {
                                XD.msg('操作失败');
                            }
                        });
                    });
                }
            },

            'organTreeView': {
                select: function (treemodel, record) {
                    var selectItem = treemodel.view.findParentByType('lookUserGroupAddView');
                    var organid = record.data.fnid;
                    var itemselector = selectItem.down('itemselector');
                    var selectStore = itemselector.getStore();
                    selectStore.proxy.extraParams.organid = organid;
                    Ext.Ajax.request({
                        params: {
                            roleid: selectItem.roleid
                        },
                        url: '/userGroup/getUsers',
                        method: 'POST',
                        sync: true,
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            selectStore.load({
                                callback: function () {
                                    itemselector.setValue(respText.data);
                                }
                            });
                        },
                        failure: function() {
                            XD.msg('操作失败');
                        }
                    });
                }
            }
        });
    },

    //跨域请求
    crossDomainByUrl:function(url){
        var store = Ext.create('Ext.data.Store', {
            model: 'UserGroup.model.UserGroupModel',
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