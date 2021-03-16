/**
 * Created by xd on 2017/10/21.
 */
var isCopyFun;
Ext.define('User.controller.UserController', {
    extend: 'Ext.app.Controller',

    views: ['UserView', 'UserPromptView', 'UserTreeView',
        'UserGridView', 'UserAddForm', 'UserSetGnView',
        'UserSetGnWin', 'UserSetSjWin', 'UserSetSjView',
        'UserResetPWWin', 'UserGroupSetView','UserTreeComboboxView','UserImportTreeComboboxView',
        'UserSetOrganView','UserSetOrganWin','UserAddAdmin','UserSequenceGridView','UserSequenceView',
        'UserOutAddFormView','UserCopySelectView','UserOrganTreeView','UserXitongTabView',
        'UserSetDeviceJoinView','UserSetDeviceJoinWin','UserSetDeviceView','UserSetDeviceWin',
        'UserSetAreaView','UserSetAreaWin','UserSetWjWin', 'UserSetWjView','UserBindForm',
        'OrganTreeView','FillingSortUserSelectView'
    ],//加载view
    stores: ['UserTreeStore', 'UserGridStore', 'UserSetGnStore', 'UserSetSjStore',
        'UserGroupSetStore','UserSetOrganStore','UserSequenceStore','UserCopySelectStore',
        'UserOrganTreeStore','UserSetDeviceJoinStore','UserSetDeviceStore','UserSetAreaStore',
        'UserSetWjStore','OrganTreeStore','FillingSortUserSelectStore'],//加载store
    models: ['UserTreeModel', 'UserGridModel', 'UserSetGnModel', 'UserSetSjModel',
        'UserGroupSetModel','UserSetOrganModel','UserOrganTreeModel','OrganTreeModel',
        'FillingSortUserSelectModel'],//加载model
    init: function () {
        var ifShowRightPanel = false;
        var usergridView;
        this.control({
            'userGridView button[itemId=userAdd]': {
                click: function () {
                    if(window.wuserGridView.treeNodeid==='0')  {
                        XD.msg('请选择有效机构节点');
                        return;
                    }
                    Ext.create('User.view.UserAddForm').show();
                }
            },
            'userGridView button[itemId="userEdit"]': {
                click: function (view) {
                    var userGridView = view.findParentByType('userGridView');
                    var select = userGridView.getSelectionModel();
                    var users = select.getSelection();
                    var selectCount = users.length;
                    if (selectCount != 1) {
                        XD.msg('请选择一条记录');
                        return;
                    }
                    var flag = true;
                    Ext.each(users, function (user) {
                        var name = user.data.realname;
                        if (name === '安全保密管理员' || name === '系统管理员' || name === '安全审计员') {
                            flag = false;
                        }
                    });
                    if (!flag) {
                        XD.msg('请勿对三员用户的进行该操作');
                        return;
                    }
                    var form;
                    if(users[0].get('outuserstate')=='外来人员'){
                        form = Ext.create("User.view.UserOutAddFormView", {title: '修改用户'});
                    }else{
                        form = Ext.create("User.view.UserAddForm", {title: '修改用户'});
                    }
                    form.show();
                    form.down('form').loadRecord(users[0]);
                }
            },
            'userGridView button[itemId="userBind"]': {
                click: function (view) {
                    var userGridView = view.findParentByType('userGridView');
                    var select = userGridView.getSelectionModel();
                    var users = select.getSelection();
                    var selectCount = users.length;
                    if (selectCount != 1) {
                        XD.msg('请选择一条记录');
                        return;
                    }
                    var flag = true;

                    var form = Ext.create("User.view.UserBindForm", {title: '绑定证书'});
                    form.show();
                    getUserCert(form);
                    form.down('form').loadRecord(users[0]);
                }
            },
            'userGridView button[itemId="userDel"]': {
                click: function (view) {
                    var userGridView = view.findParentByType('userGridView');
                    var select = userGridView.getSelectionModel();
                    var users = select.getSelection();
                    var selectCount = users.length;
                    if (selectCount == 0) {
                        XD.msg('请选择操作记录');
                    } else {
                        var flag = true;
                        Ext.each(users, function (user) {
                            var name = user.data.realname;
                            if (name === '安全保密管理员' || name === '系统管理员' || name === '安全审计员') {
                                flag = false;
                            }
                        });
                        if (!flag) {
                            XD.msg('请勿对三员用户的进行该操作');
                            return;
                        }
                        var that=this;
                        XD.confirm('是否确定删除', function () {
                            var logins = [];
                            var userids='';
                            for (var i = 0; i < selectCount; i++) {
                                logins.push(users[i].get('userid'));
                                userids+=users[i].get('userid')+',';
                            }
                            Ext.Msg.wait('删除用户中，请耐心等待……','正在操作');
                            Ext.Ajax.request({
                                params: {logins: logins,xtType: window.userGridViewTab},
                                url: '/user/userDel',
                                method: 'POST',
                                sync: true,
                                timeout:XD.timeout,
                                success: function (resp) {
                                    Ext.Msg.wait('成功','正在操作').hide();
                                    var respText = Ext.decode(resp.responseText);
                                    XD.msg(respText.msg);
                                    if (respText.success == true) {
                                       /* var url=respText.data.login_ip;
                                        var userid=userids.substring(0,userids.length-1);
                                        url=url+'?szType=20&userid='+userid;//用户删除20
                                        //发送跨域请求
                                        that.crossDomainByUrl(url);*/
                                    }
                                    userGridView.delReload(selectCount);
                                },
                                failure: function (form, action) {
                                    Ext.Msg.wait('失败','正在操作').hide();
                                    var respText = Ext.decode(action.response.responseText);
                                    XD.msg(respText.msg);
                                }
                            });
                        });
                    }
                }
            },
            'userGridView button[itemId=userRegister]': {//外来人员查档登记
                click: function () {
                    if(window.wuserGridView.treeNodeid==='0')  {
                        XD.msg('请选择有效机构节点');
                        return;
                    }
                    Ext.create('User.view.UserOutAddFormView').show();
                }
            },
            'userOutAddFormView button[itemId=userOutAddSubmit]': {//新增外来人员临时用户
                click: function (view) {
                    var form = view.findParentByType('userOutAddFormView').down('form');
                    var URL = '/user/userOutAddSubmit';
                    if (view.findParentByType('userOutAddFormView').title == '修改用户') {
                        URL = '/user/userOutEditSubmit'
                    }
                    var data = form.getValues();
                    if (data['realname'] === '安全保密管理员' || data['realname'] === '系统管理员' || data['realname'] === '安全审计员') {
                        XD.msg('请勿使用该用户姓名');
                        return;
                    }
                    if (data['loginname'] == '' || data['realname'] == '') {
                        XD.msg('有必填项未填写');
                        return;
                    }
                    if (data['loginname'].length<2) {
                        XD.msg('帐号字段输入长度不应少于2位');
                        return;
                    }
                    if (data['loginname'].length>30) {
                        XD.msg('帐号字段输入长度超限');
                        return;
                    }
                    if (data['realname'].length>10) {
                        XD.msg('用户姓名字段输入长度超限');
                        return;
                    }

                    var that=this;
                    form.submit({
                        waitTitle: '提示',// 标题
                        waitMsg: '正在提交数据请稍后...',// 提示信息
                        url: URL,
                        method: 'POST',
                        params: { // 此处可以添加额外参数
                            treetext: window.wuserGridView.treeNodeid,xtType: window.userGridViewTab
                        },
                        success: function (form, action) {
                            var respText = Ext.decode(action.response.responseText);
                            if (respText.success == true) {
                                XD.msg(respText.msg);

                                var url=respText.data.login_ip;
                                var userid=respText.data.userid;
                                url=url+'?szType=2&userid='+userid;//用户增加修改2
                                //发送跨域请求
                                that.crossDomainByUrl(url);

                                view.findParentByType('userOutAddFormView').close();//添加成功后关闭窗口
                                window.wuserGridView.getStore().reload();
                            } else {
                                XD.msg(respText.msg);
                            }
                        },
                        failure: function (form, action) {
                            var respText = Ext.decode(action.response.responseText);
                            XD.msg(respText.msg);
                        }
                    });
                }
            },
            'userOutAddFormView button[itemId=userOutAddClose]': {//外来人员查档登记 返回
                click: function (view) {
                    view.findParentByType('userOutAddFormView').close();
                }
            },
            'userGridView button[itemId="setUserGroup"]': {
                click: function (view) {
                    var userGridView = view.findParentByType('userGridView');
                    var select = userGridView.getSelection();
                    if (select.length < 1) {
                        XD.msg('请选择操作记录');
                    } else {
                        var flag = true;
                        Ext.each(select, function (user) {
                            var name = user.data.realname;
                            if (name.indexOf('安全保密管理员')!=-1  || name.indexOf('系统管理员')!=-1  || name.indexOf('安全审计员')!=-1) {
                                flag = false;
                            }
                        });
                        if (!flag) {
                            XD.msg('请勿对三员用户的进行该操作');
                            return;
                        }
                        var list = new Array();
                        Ext.each(select, function (user) {
                            list.push(user.data.userid);
                        });
                        var userStr = list.join(',');
                        Ext.Ajax.request({
                            params: {
                                userId: userStr,xtType: window.userGridViewTab
                            },
                            url: '/userGroup/myUserGroup',
                            method: 'post',
                            success: function (resp) {
                                var respText = Ext.decode(resp.responseText);
                                if (respText.success == true) {
                                    isCopyFun="undefined";
                                    var selectItem = Ext.create("User.view.UserGroupSetView", {
                                        useridOfWin: userStr
                                    });
                                    selectItem.items.get(0).getStore().load({
                                        callback: function () {
                                            if(typeof(respText.data) !== 'undefined'){
                                                selectItem.items.get(0).setValue(respText.data);
                                            }
                                        }
                                    });
                                    if(typeof(respText.data) === 'undefined'){
                                        selectItem.title = '批量设置用户组';
                                    }
                                    selectItem.show();
                                } else {
                                    XD.msg("操作失败");
                                }
                            }
                        });
                    }
                }
            },
            'UserGroupSetView button[itemId="save"]': {
                click: function (view) {
                    var selectView = view.findParentByType('UserGroupSetView');
                    var win = view.findParentByType('UserGroupSetView');

                    var selectedStore = selectView.items.get(0).toField.getStore();
                    for (var i = 0; i < selectedStore.data.length; i++) {
                        var name = selectedStore.getAt(i).get('rolename');
                        if (name === '安全保密管理员' || name === '系统管理员' || name === '安全审计员') {
                            XD.msg('请勿将普通用户设置为三员用户');
                            return false;
                        }
                    }
                    var tip = '是否确认对该用户进行设置？';
                    if (win.useridOfWin.split(',').length > 1) {
                        tip = '当前为批量设置，是否确认对多个用户设置用户组？'
                    }
                    var that=this;
                    XD.confirm(tip,function(){
                        Ext.Ajax.request({
                            params: {
                                groupids: selectView.items.get(0).getValue(),
                                userid: win.useridOfWin,xtType: window.userGridViewTab
                            },
                            url: '/userGroup/userGroupSeting',
                            method: 'post',
                            success: function (resp) {
                                var respText = Ext.decode(resp.responseText);
                                win.close();
                                XD.msg(respText.msg);

                                var url=respText.data.login_ip;
                                var groupidStr=respText.data.servicesname;
                                url=url+'?szType=5&userid='+win.useridOfWin+'&groupids='+groupidStr;//角色权限5
                                //发送跨域请求
                                that.crossDomainByUrl(url);
                            },
                            failure: function () {
                                XD.msg('操作中断');
                            }
                        });
                    });
                }
            },

            //调序
            'userGridView button[itemId=userSortq]': {
                click: function (view) {
                    var userGridView = view.findParentByType('userGridView');
                    usergridView = view.findParentByType('userGridView');
                    var select = userGridView.getSelectionModel();
                    if (!select.hasSelection()) {
                        XD.msg('请选择操作记录');
                        return;
                    } else {
                        var gridselections = select.getSelection();
                        var array = [];
                        for (var i = 0; i < gridselections.length; i++) {
                            array.push(gridselections[i].get("userid"));
                        }
                    }
                    var sequenceUser = Ext.create('Ext.window.Window',{
                        width:'60%',
                        height:'75%',
                        modal:true,
                        title:'用户调序',
                        closeToolText:'关闭',
                        closeAction:'hide',
                        layout:'fit',
                        items:[{
                            xtype: 'userSequenceView'//调序视图
                        }]
                    });
                    sequenceUser.show();
                    window.sequenceUser=sequenceUser;
                    var view = sequenceUser.down('userSequenceView').down('userSequenceGridView');
                    view.initGrid({userid: array});
                }
            },

            'userSequenceView button[itemId="up"]': {
                click: function (view) {
                    var grid = view.findParentByType('userSequenceView').down('userSequenceGridView');
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
                        array.push(recordall[i].get("userid"));
                    }
                    Ext.Ajax.request({
                        method: 'post',
                        url: '/user/usersequence',
                        params: {
                            userid: array,
                            currentcount:currentcount,
                            operate:'up',xtType: window.userGridViewTab
                        },
                        success: function (response) {
                            grid.getStore().reload();
                        },
                        failure:function(response){
                            XD.msg('操作失败');
                        }
                    });

                }
            },

            'userSequenceView button[itemId="down"]': {
                click: function (view) {
                    var grid = view.findParentByType('userSequenceView').down('userSequenceGridView');
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
                        array.push(recordall[i].get("userid"));
                    }
                    Ext.Ajax.request({
                        method: 'post',
                        url: '/user/usersequence',
                        params: {
                            userid: array,
                            currentcount:currentcount,
                            operate:'down',xtType: window.userGridViewTab
                        },
                        success: function (response) {
                            grid.getStore().reload();
                        },
                        failure:function(response){
                            XD.msg('操作失败');
                        }
                    });

                }
            },
            'userGridView button[itemId="copyAuth"]': {//-打开复制权限窗口
                click: function (view) {
                    var userGridView = view.findParentByType('userGridView');
                    var select = userGridView.getSelection();
                    if (select.length < 1) {
                        XD.msg('请选择一条操作记录');
                    } else if (select.length > 1) {
                        XD.msg('请选择单条操作记录');
                    } else {
                        var name = select[0].get('realname');
                        if (name.indexOf('安全保密管理员')!=-1  || name.indexOf('系统管理员')!=-1  || name.indexOf('安全审计员')!=-1) {
                            XD.msg('请勿复制三员用户的权限');
                            return;
                        }
                        var count = 0;
                        //计算最大复制人数
                        Ext.Ajax.request({
                            params: {sourceId: select[0].get('userid'),xtType: window.userGridViewTab},
                            timeout: 1000000000,
                            url: '/user/countMax',
                            method: 'post',
                            async:false,
                            success: function (resp) {
                                var respText = Ext.decode(resp.responseText);
                                count = respText.data;
                            },
                            failure: function () {
                                XD.msg('计算最大可复制权限人数失败！');
                            }
                        });
                        var maxCount = Math.floor(50000/count);//向下取整
                        var copywin = Ext.create('User.view.UserCopySelectView', {selectedUserId: select[0].get('userid')});
                        copywin.down('itemselector').getStore().proxy.extraParams.xtType = window.userGridViewTab;
                        isCopyFun="true";
                        if(count==0){
                            maxCount = 100;
                            XD.confirm('所选用户无数据权限，确定复制该用户的权限吗？',function(){
                                copywin.down('itemselector').getStore().load({
                                    callback: function () {
                                        copywin.down('itemselector').setValue(null);
                                        copywin.title = '复制【' + name + '】权限到以下所选用户';
                                        copywin.down('[itemId=maxMsg]').setText('温馨提示：已选用户不能超过'+maxCount+"人,最少不能低于1人");
                                        copywin.maxCount = maxCount;
                                        copywin.down('[itemId=maxMsg]').show();
                                        copywin.show();
                                    },
                                });
                            },this);
                        }else{
                            XD.confirm('确定复制该用户的权限吗？',function(){
                                copywin.down('itemselector').getStore().load({
                                    callback: function () {
                                        copywin.down('itemselector').setValue(null);
                                        copywin.title = '复制【' + name + '】权限到以下所选用户';
                                        copywin.down('[itemId=maxMsg]').setText('温馨提示：已选用户不能超过'+maxCount+"人,最少不能低于1人");
                                        copywin.maxCount = maxCount;
                                        copywin.down('[itemId=maxMsg]').show();
                                        copywin.show();
                                    }
                                });
                            },this);
                        }
                    }
                }
            },
            'userOrganTreeView': {
                select: function (treemodel, record) {
                    var organid = record.data.fnid;
                    var userstore = treemodel.view.findParentByType('userCopySelectView').down('itemselector').getStore();
                    var username = treemodel.view.findParentByType('userCopySelectView').down('[itemId=usernameSearchId]').getValue();
                    userstore.reload({
                        params: {
                            organid: organid,
                            sourceId: treemodel.view.findParentByType('userCopySelectView').selectedUserId,xtType: window.userGridViewTab,
                            username:username,
                            xtType: window.userGridViewTab
                        }
                    });
                }
            },
            'userCopySelectView button[itemId=allOrNotSelect]': {
                click: function (view) {
                    var itemSelector = view.findParentByType('userCopySelectView').down('itemselector');
                    if (view.getText() == '全选') {
                        var fromList = itemSelector.fromField.boundList,
                            allRec = fromList.getStore().getRange();
                        fromList.getStore().remove(allRec);
                        itemSelector.toField.boundList.getStore().add(allRec);
                        itemSelector.syncValue();//
                        view.setText('取消全选');
                    } else {
                        var toList = itemSelector.toField.boundList,
                            allRec = toList.getStore().getRange();
                        toList.getStore().remove(allRec);
                        itemSelector.fromField.boundList.getStore().add(allRec);
                        itemSelector.syncValue();
                        view.setText('全选');
                    }
                }
            },
            'userCopySelectView button[itemId=copySelectClose]': {
                click: function (view) {
                    view.findParentByType('userCopySelectView').close();
                }
            },
            'userCopySelectView button[itemId=copySelectSubmit]': {
                click: function (view) {
                    var copySelectView = view.findParentByType('userCopySelectView');
                    var dataCheck = copySelectView.down('[itemId=dataCheck]').getValue();
                    var organCheck = copySelectView.down('[itemId=organCheck]').getValue();
                    var fnCheck = copySelectView.down('[itemId=fnCheck]').getValue();
                    var roleCheck = copySelectView.down('[itemId=roleCheck]').getValue();
                    var nodeCheck = copySelectView.down('[itemId=nodeCheck]').getValue();
                    var fileCheck = copySelectView.down('[itemId=fileCheck]').getValue();
                    if (!dataCheck && !organCheck && !fnCheck && !roleCheck && !nodeCheck && !fileCheck) {
                        XD.msg('请勾选复制内容');
                        return;
                    }
                    var copys = copySelectView.down('itemselector').getValue();
                    if (copys.length === 0 || copys[0] === null) {
                        XD.msg("请选择用户");
                        return;
                    }
                    var maxCount = copySelectView.maxCount;
                    if(copys.length>maxCount){
                        XD.msg("已选用户数量超过最大数");
                        return;
                    }
                    var isAllowed = false;
                    var respMsg = '';
                    if (nodeCheck) {
                        Ext.Ajax.request({
                            params: {
                                sourceId: copySelectView.selectedUserId,
                                copys: copys,xtType: window.userGridViewTab
                            },
                            timeout: 1000000000,
                            url: '/user/copyCheck',
                            method: 'post',
                            async:false,
                            success: function (resp) {
                                var respText = Ext.decode(resp.responseText);
                                isAllowed = respText.success;
                                respMsg = respText.msg;
                            },
                            failure: function () {
                                XD.msg('操作失败');
                            }
                        });
                    }
                    if (!nodeCheck || isAllowed) {
                        XD.confirm('本操作将覆盖已选用户原有的相关权限，是否继续', function () {
                            Ext.Msg.wait('正在进行复制操作，请耐心等待……', '正在操作');
                            Ext.Ajax.request({
                                params: {
                                    sourceId: copySelectView.selectedUserId,
                                    copys: copys,
                                    dataCheck: dataCheck,
                                    organCheck: organCheck,
                                    fnCheck: fnCheck,
                                    roleCheck: roleCheck,
                                    nodeCheck: nodeCheck,
                                    fileCheck: fileCheck,
                                    xtType: window.userGridViewTab
                                },
                                timeout: 1000000000,
                                url: '/user/copyUser',
                                method: 'POST',
                                success: function (resp) {
                                    Ext.MessageBox.close();
                                    copySelectView.close();
                                    var respText = Ext.decode(resp.responseText);
                                    XD.msg(respText.msg);
                                },
                                failure: function () {
                                    Ext.MessageBox.close();
                                    XD.msg('操作失败');
                                }
                            });
                        });
                    } else {
                        XD.msg('以下用户仍有待审批的流程，请勿覆盖其工作流权限：<br/>' + respMsg);
                    }
                }
            },
            'userSequenceView button[itemId="back"]': {
                click: function (view) {
                    usergridView.getStore().reload();
                    window.sequenceUser.close();
                }
            },
            'UserGroupSetView button[itemId="close"]': {
                click: function (view) {
                    view.findParentByType('UserGroupSetView').close();
                }
            },
            'userGridView button[itemId="userGnqx"]': {
                click: function (view) {
                    var userGridView = view.findParentByType('userGridView');
                    var select = userGridView.getSelection();
                    if (select.length != 1) {
                        XD.msg('请选择一条操作记录');
                    } else {
                        var flag=true;
                        Ext.each(select, function (user) {
                            var name = user.data.realname;
                            if (name.indexOf('安全保密管理员')!=-1  || name.indexOf('系统管理员')!=-1  || name.indexOf('安全审计员')!=-1) {
                                flag=false;
                            }
                        });
                        if(!flag){
                            XD.msg('请勿设置三员用户的功能权限');
                            return;
                        }
                        var userids = new Array();
                        Ext.each(select, function (user) {
                            userids.push(user.data.userid);
                        });
                        var userStr = userids.join(',');

                        window.wuserGridView.userids = userStr;

                        var form = Ext.create("User.view.UserSetGnWin",{height:window.innerHeight * 6 / 7,width:380});
                        if (userids.length > 1) {
                            form.title = '批量设置功能权限';
                        }
                        var treeView = form.getComponent("userSetGnView");
                        if (window.userGridViewTab=='声像系统'||window.userGridViewTab=='新闻系统') {
                            form.title = '设置'+window.userGridViewTab+'功能权限';
                            if(window.userGridViewTab=='声像系统'){
                                treeView.getStore().proxy.extraParams.xtType = window.userGridViewTab;
                            }
                        }
                        Ext.Msg.wait('正在打开功能权限设置，请耐心等待……','正在操作');
                        treeView.getStore().proxy.extraParams.fnid = 1;
                        treeView.getStore().load();
                        treeView.on('load', function () {
                            form.show();
                            //treeView.expandAll();//展开全部节点
                        });
                        setTimeout(function () {
                            Ext.Msg.hide();
                        },1628);
                        
                    }
                }
            },
            'userGridView button[itemId="userSjqx"]': {
                click: function (view) {
                    var userGridView = view.findParentByType('userGridView');
                    var select = userGridView.getSelection();
                    if (select.length != 1) {
                        XD.msg('请选择一条操作记录');
                    } else {
                        var userids = new Array();
                        Ext.each(select, function (user) {
                            userids.push(user.data.userid);
                        });
                        var userStr = userids.join(',');

                        window.wuserGridView.userids = userStr;
                        var form = Ext.create("User.view.UserSetSjWin",{height:window.innerHeight * 6 / 7,width:380});
                        var treeView = form.down('userSetSjView');
                        Ext.each(select, function (user) {
                            var name = user.data.realname;
                            if (name.indexOf('安全保密管理员')!=-1  || name.indexOf('系统管理员')!=-1  || name.indexOf('安全审计员')!=-1) {
                                form.down('[itemId=userSetSjSubmit]').hide();
                                return false;
                            }
                        });
                        if (userids.length > 1) {
                            form.title = '批量设置数据权限 (右键--快捷操作)';
                        }
                        treeView.getStore().proxy.extraParams.pcid = '';
                        if (window.userGridViewTab=='声像系统'||window.userGridViewTab=='新闻系统') {
                            form.title = '设置'+window.userGridViewTab+'数据权限 (右键--快捷操作)';
                            if(window.userGridViewTab=='声像系统'){
                                treeView.getStore().proxy.extraParams.xtType = window.userGridViewTab;
                            }
                        }
                        treeView.getStore().load();
                        treeView.on('load', function () {
                            // findchildnode(treeView.getRootNode());
                            treeView.getRootNode().expandChildren();//展开首层节点
                            treeView.up('[itemId=userSetSjWinId]').down('[itemId=selectedCountItem]').setText('已选择 '+treeView.getChecked().length+' 个节点');
                        });
                        form.show();
                        // var findchildnode = function (node) {
                        //     var childnodes = node.childNodes;
                        //     for(var i=0;i<childnodes.length;i++){  //从节点中取出子节点依次遍历
                        //         var rootnode = childnodes[i];
                        //         if(rootnode.get('roottype')==='classification'){
                        //             rootnode.set('checked',null);
                        //         }
                        //         if(rootnode.childNodes.length>0){  //判断子节点下是否存在子节点
                        //             findchildnode(rootnode);    //如果存在子节点  递归
                        //         }
                        //     }
                        // };
                    }
                }
            },
            'userGridView button[itemId="userSetOrgan"]': {
                click: function (view) {
                    var userGridView = view.findParentByType('userGridView');
                    var select = userGridView.getSelection();
                    if (select.length < 1) {
                        XD.msg('请选择操作记录');
                    } else {
                        var userids = new Array();
                        Ext.each(select, function (user) {
                            userids.push(user.data.userid);
                        });
                        var userStr = userids.join(',');

                        window.wuserGridView.userids = userStr;
                        var form = Ext.create("User.view.UserSetOrganWin",{height:window.innerHeight * 6 / 7,width:380});

                        Ext.each(select, function (user) {
                            var name = user.data.realname;
                            if (name.indexOf('安全保密管理员')!=-1  || name.indexOf('系统管理员')!=-1  || name.indexOf('安全审计员')!=-1) {
                                form.down('[itemId=userSetOrganSubmit]').hide();
                                return false;
                            }
                        });
                        var treeView = form.getComponent("userSetOrganView");
                        treeView.getStore().proxy.extraParams.pcid = '0';
                        treeView.getStore().load();
                        treeView.on('load', function () {
                            treeView.expandAll();//展开全部节点
                        });
                        if (userids.length > 1) {
                            form.title = '批量设置机构权限';
                        }
                        form.show();
                    }
                }
            },
            'userGridView button[itemId="resetPW"]': {
                click: function (view) {
                    var userGridView = view.findParentByType('userGridView');
                    var select = userGridView.getSelectionModel();
                    var users = select.getSelection();
                    var selectCount = users.length;
                    if (selectCount == 0) {
                        XD.msg('请选择一条记录');
                    } else {
                        var userids = new Array();
                        for (var i = 0; i < selectCount; i++) {
                            userids.push(users[i].get('userid'));
                        }
                        window.wuserGridView.userids = userids;
                        var form = Ext.create("User.view.UserResetPWWin");
                        form.show();
                    }
                }
            },
            'userAddForm button[itemId="userAddSubmit"]': {
                click: function (view) {
                    var form = view.findParentByType('userAddForm').down('form');
                    var URL = '/user/userAdd/userAddSubmit';
                    if (view.findParentByType('userAddForm').title == '修改用户') {
                        URL = '/user/userEdit/userEditSubmit'
                    }
                    var data = form.getValues();
                    if (data['realname'] === '安全保密管理员' || data['realname'] === '系统管理员' || data['realname'] === '安全审计员') {
                        XD.msg('请勿使用该用户姓名');
                        return;
                    }
                    if (data['loginname'] == '' || data['realname'] == '') {
                        XD.msg('有必填项未填写');
                        return;
                    }
                    if (data['loginname'].length<2) {
                        XD.msg('帐号字段输入长度不应少于2位');
                        return;
                    }
                    if (data['loginname'].length>30) {
                        XD.msg('帐号字段输入长度超限');
                        return;
                    }
                    if (data['realname'].length>10) {
                        XD.msg('用户姓名字段输入长度超限');
                        return;
                    }

                    var that=this;
                    form.submit({
                        waitTitle: '提示',// 标题
                        waitMsg: '正在提交数据请稍后...',// 提示信息
                        url: URL,
                        method: 'POST',
                        params: { // 此处可以添加额外参数
                            treetext: window.wuserGridView.treeNodeid,xtType: window.userGridViewTab
                        },
                        success: function (form, action) {
                            var respText = Ext.decode(action.response.responseText);
                            if (respText.success == true) {
                                XD.msg(respText.msg);
                                /*var url=respText.data.login_ip;
                                var userid=respText.data.userid;
                                url=url+'?szType=2&userid='+userid;//用户增加修改2
                                //发送跨域请求
                                that.crossDomainByUrl(url);*/
                                view.findParentByType('userAddForm').close();//添加成功后关闭窗口
                                window.wuserGridView.getStore().reload();
                            } else {
                                XD.msg(respText.msg);
                            }
                        },
                        failure: function (form, action) {
                            var respText = Ext.decode(action.response.responseText);
                            XD.msg(respText.msg);
                        }
                    });
                }
            },
            'userAddForm button[itemId="userAddClose"]': {
                click: function (view) {
                    view.findParentByType('userAddForm').close();
                }
            },
            'userBindForm button[itemId="userBindSubmit"]': {
                click: function (view) {
                    var form = view.findParentByType('userBindForm').down('form');
                    var URL = '/user/userBind/userBindSubmit';
                    var data = form.getValues();
                    if (data['loginname'] == '' || data['nickname'] == '') {
                        XD.msg('未读取到证书信息');
                        return;
                    }

                    /*if(data['caUsername'] !== data['realname']){
                        XD.msg('证书的用户名和系统账号的用户名不一致');
                        return;
                    }
                    var that=this;
                    Ext.Ajax.request({
                        url: URL,
                        async:false,
                        params:{
                            userid:data['userid'],
                            loginname:data['loginname'],
                            realname:data['realname'],
                            nickname:data['nickname'],
                            cacode:data['cacode'],
                            signcode:data['signcode']
                        },
                        success: function (response) {
                            var respText = Ext.decode(response.responseText);
                            if (respText.success == true) {
                                XD.msg(respText.msg);
                                view.findParentByType('userBindForm').close();//添加成功后关闭窗口
                                window.wuserGridView.getStore().reload();
                            } else {
                                XD.msg(respText.msg);
                            }
                        },
                        failure: function (response) {
                            var respText = Ext.decode(response.responseText);
                            XD.msg(respText.msg);
                        }
                    });*/

                    var msg="证书的用户名和系统账号的用户名比对一致，可以绑定用户";
                    if(data['caUsername'] !== data['realname']){
                      msg='证书的用户名和系统账号的用户名不一致，是否确定要继续绑定用户？';
                    }
                    XD.confirm(msg,function () {
                        Ext.Ajax.request({
                            url: URL,
                            async:false,
                            params:{
                                userid:data['userid'],
                                loginname:data['loginname'],
                                realname:data['realname'],
                                nickname:data['nickname'],
                                cacode:data['cacode'],
                                signcode:data['signcode']
                            },
                            success: function (response) {
                                var respText = Ext.decode(response.responseText);
                                if (respText.success == true) {
                                    XD.msg(respText.msg);
                                    view.findParentByType('userBindForm').close();//添加成功后关闭窗口
                                    window.wuserGridView.getStore().reload();
                                } else {
                                    XD.msg(respText.msg);
                                }
                            },
                            failure: function (response) {
                                var respText = Ext.decode(response.responseText);
                                XD.msg(respText.msg);
                            }
                        });
                    },this);
                }
            },
            'userBindForm button[itemId="userBindClose"]': {
                click: function (view) {
                    view.findParentByType('userBindForm').close();
                }
            },
            'userTreeView': {
                select: function (treemodel, record) {
                    //通过当前控件找出父控件
                    var userView = treemodel.view.findParentByType('userView');
                    //找出子控件
                    var userPromptView = userView.down('[itemId=userPromptViewId]');
                    if (!ifShowRightPanel) {
                        userPromptView.removeAll();
                        var showView='userGridView';
                        if(userType=='bm'){//安全保密员显示tab页面
                            showView='UserXitongTabView'
                        }
                        userPromptView.add({
                            xtype:showView
                        });
                        ifShowRightPanel = true;
                    }
                    var usergrid = userPromptView.down('[itemId=userGridViewID]');
                    usergrid.setTitle("当前位置：" + record.get('text'));
                    if(userPromptView.down('[itemId=userBind]')){//用户绑定
                        if(netcatUse=='0'){
                            userPromptView.down('[itemId=userBind]').hide();//隐藏用户绑定
                        }
                    }
                    if(!userPromptView.down('[itemId=setUserGroup]')){//过滤aqbm-用户管理操作
                        var tbseparator = userPromptView.down("toolbar").query('tbseparator');
                        userPromptView.down('[itemId=userAdd]').hide();//隐藏增加
                        // userPromptView.down('[itemId="userEdit"]').hide();//隐藏修改
                       // userPromptView.down('[itemId="userDel"]').hide();//隐藏删除
                        userPromptView.down('[itemId="changeOrgan"]').hide();//隐藏用户移动
                     //   userPromptView.down('[itemId="userSortq"]').hide();//隐藏调序
                        userPromptView.down('[itemId="importUser"]').hide();//隐藏导入用户
                        userPromptView.down('[itemId="userRegister"]').hide();//隐藏外来人员查档登记
                        var parentaddAdmin = userPromptView.down('[itemId="parentaddAdmin"]');
                        if(parentaddAdmin){
                            parentaddAdmin.hide();
                        }
                        userPromptView.down('[itemId="deleteAdmin"]').hide();//隐藏设置三员
                        userPromptView.down('[itemId="addAdmin"]').hide();//隐藏设置三员
                        tbseparator[0].hide();
                        tbseparator[1].hide();
                        tbseparator[2].hide();
                        tbseparator[3].hide();
                        tbseparator[tbseparator.length-1].hide();
                        tbseparator[tbseparator.length-2].hide();
                        tbseparator[tbseparator.length-3].hide();
                        tbseparator[tbseparator.length-4].hide();
                        if(record.data.text.indexOf('用户管理')!=-1){

                        }else if(record.data.text.indexOf('外来人员')!=-1 || record.parentNode.data.text.indexOf('外来人员')!=-1){//外来人员机构
                            tbseparator[tbseparator.length-2].show();
                            userPromptView.down('[itemId="userRegister"]').show();
                        }else{
                            userPromptView.down('[itemId=userAdd]').show();
                            userPromptView.down('[itemId="userEdit"]').show();
                            userPromptView.down('[itemId="userDel"]').show();
                            userPromptView.down('[itemId="changeOrgan"]').show();
                            userPromptView.down('[itemId="userSortq"]').show();
                            userPromptView.down('[itemId="importUser"]').show();
                            var parentaddAdmin = userPromptView.down('[itemId="parentaddAdmin"]');
                            if(parentaddAdmin){
                                parentaddAdmin.show();
                            }
                            userPromptView.down('[itemId="deleteAdmin"]').show();
                            userPromptView.down('[itemId="addAdmin"]').show();
                            tbseparator[0].show();
                            tbseparator[1].show();
                            tbseparator[2].show();
                            tbseparator[3].show();
                            tbseparator[tbseparator.length-2].show();
                            tbseparator[tbseparator.length-3].show();
                            tbseparator[tbseparator.length-4].show();
                        }
                    }
                    window.wuserGridView = usergrid;
                    window.wuserGridView.nodename =  record.get('text');
                    window.wuserGridView.treeNodeid = record.get('fnid');
                    //ifSearchLeafNode设置是否查询当前节点下的叶子节点，ifContainSelfNode设置是否查询当前节点下的非叶子节点及当前非叶子节点
                    usergrid.initGrid({organName:record.get('text'), organID: record.get('fnid'),ifSearchLeafNode:showChildUser,ifContainSelfNode:true,xtType: window.userGridViewTab});
                }
            },
            'UserXitongTabView':{
                tabchange: function (view) {//tab页面切换触发
                    var tabview;
                    if (view.activeTab.title == '档案系统') {
                        window.userGridViewTab='档案系统';
                        tabview=view.down('[itemId=daxtId]').down('userGridView');
                        var tbseparator = tabview.getDockedItems('toolbar')[0].query('tbseparator');
                        //tbseparator[0].setVisible(true);
                        //tabview.down('[itemId=setUserGroup]').setVisible(true);//显示角色设置按钮

                    }
                    if (view.activeTab.title == '声像系统') {
                        window.userGridViewTab='声像系统';
                        tabview=view.down('[itemId=sxxtId]').down('userGridView');
                        var tbseparator = tabview.getDockedItems('toolbar')[0].query('tbseparator');
                        //tbseparator[0].setVisible(false);
                        //tabview.down('[itemId=setUserGroup]').setVisible(false);//隐藏角色设置按钮
                    }

                    tabview.initGrid({organName:window.wuserGridView.nodename, organID: window.wuserGridView.treeNodeid,ifSearchLeafNode:true,
                        ifContainSelfNode:true, xtType:window.userGridViewTab});
                    /*if (view.activeTab.title == '新闻系统') {
                        window.userGridViewTab='新闻系统';
                        var tabview=view.down('[itemId=xwxtId]').down('userGridView');
                        var tbseparator = tabview.getDockedItems('toolbar')[0].query('tbseparator');
                        tbseparator[0].setVisible(false);
                        tabview.down('[itemId=setUserGroup]').setVisible(false);//隐藏角色设置按钮
                    }*/
                    view.down('userGridView').getSelectionModel().clearSelections(); // 取消选择
                }
            },
            'userGridView': {
                beforedrop: function (node, data, overmodel, position, dropHandlers) {
                    dropHandlers.cancelDrop();
                    if (data.records.length > 1) {
                        XD.msg('不支持批量选择拖拽排序，请选择一条数据');
                    } else {
                        XD.confirm('确认将用户[ ' + data.records[0].get('loginname') + ' ]移动到[ '
                            + overmodel.get('loginname') + ' ]的' + ("before" == position ? '前面吗' : '后面吗'), function () {
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
                                url: '/user/order/' + data.records[0].get('userid') + '/' + target,
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
            'userSetGnWin button[itemId="userSetGnSubmit"]': {
                click: function (view) {
                    var treeWinView = view.findParentByType('userSetGnWin');
                    var treeView = treeWinView.getComponent("userSetGnView");
                    var selNodes = treeView.getChecked();
                    var gnList = new Array();
                    Ext.each(selNodes, function (node) {
                        if(node.data.text.indexOf("gray")!=-1){//跳过用户组权限节点
                        }else{
                            gnList.push(node.data.fnid);
                        }
                    });
                    var tip = '是否确认对该用户进行授权？';
                    if (window.wuserGridView.userids.split(',').length > 1) {
                        tip = '当前为批量设置，是否确认对多个用户设置以上权限？'
                    }
                    var that=this;
                    XD.confirm(tip,function(){
                        Ext.Msg.wait('正在进行功能权限设置操作，请耐心等待……','正在操作');
                        Ext.Ajax.request({
                            params: {gnList: gnList, userId: window.wuserGridView.userids,xtType: window.userGridViewTab},
                            url: '/user/UserSetGnSubmit',
                            method: 'POST',
                            sync: true,
                            timeout:XD.timeout,
                            success: function (resp) {
                                var respText = Ext.decode(resp.responseText);
                                Ext.Msg.wait('功能权限设置操作完成','正在操作').hide();
                                XD.msg(respText.msg);
                                /*if(window.userGridViewTab=='声像系统'){
                                    var url=respText.data.login_ip;
                                    url=url+'?szType=4&userid='+window.wuserGridView.userids;
                                    //发送跨域请求
                                    that.crossDomainByUrl(url);
                                }*/
                                view.findParentByType('userSetGnWin').close();
                            },
                            failure: function () {
                                Ext.Msg.wait('功能权限设置操作失败','正在操作').hide();
                                XD.msg('操作失败');
                            }
                        });
                    });

                }
            },
            'userSetGnWin button[itemId="userSetGnWinClose"]': {
                click: function (view) {
                    view.findParentByType('userSetGnWin').close();
                }
            },
            'userSetSjWin button[itemId="userSetSjWinClose"]': {
                click: function (view) {
                    view.findParentByType('userSetSjWin').close();
                }
            },
            'userSetOrganWin button[itemId="userSetOrganWinClose"]': {
                click: function (view) {
                    view.findParentByType('userSetOrganWin').close();
                }
            },
            'userSetOrganWin [itemId="userSetOrganView"]': {
                // render: function (view) {
                //     var setLoop = function (node, check) {
                //         node.set('checked', check);
                //         if (node.isNode) {
                //             node.eachChild(function (child) {
                //                 setLoop(child, check);
                //             });
                //         }
                //     };
                //     view.on('checkchange', function(node, checked) {
                //         if(checked){
                //             node.expand();
                //         }
                //         node.eachChild(function(child) {
                //             child.set("checked",checked);
                //             setLoop(child,checked);
                //         });
                //     }, view);
                // }
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
                    view.on('beforecheckchange', function (node, checked) {
                        if(node.data.text.indexOf("gray")!=-1){//用户组权限不允取消勾选
                            return false;
                        }
                    }, view);
                    view.on('checkchange', function (node, checked) {
                        node.expand();
                        node.eachChild(function (child) {
                            if(child.data.text.indexOf("gray")!=-1){//用户组权限不允取消勾选
                                return;
                            }else {
                                child.set("checked", checked);
                            }
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
            'userSetGnWin [itemId="userSetGnView"]': {
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
                    view.on('beforecheckchange', function (node, checked) {
                        if(node.data.text.indexOf("gray")!=-1){//用户组权限不允取消勾选
                            return false;
                        }
                    }, view);
                    view.on('checkchange', function (node, checked) {
                        node.expand();
                        node.eachChild(function (child) {
                            if(child.data.text.indexOf("gray")!=-1){//用户组权限不允取消勾选
                                return;
                            }else {
                                child.set("checked", checked);
                            }
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
            'userSetSjWin [itemId="userSetSjViewId"]': {
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
                    view.on('beforecheckchange', function (node, checked) {
                        if(node.data.text.indexOf("gray")!=-1){//用户组权限不允取消勾选
                            return false;
                        }
                    }, view);
                    view.on('checkchange', function (node, checked) {
                        node.expand();
                        node.eachChild(function (child) {
                            if(child.data.text.indexOf("gray")!=-1){//用户组权限不允取消勾选
                                return;
                            }else {
                                child.set("checked", checked);
                            }
                            node.expand();
                            setChildLoop(child, checked);
                        });
                        if(!checked){
                            setParentUncheckedLoop(node.parentNode);
                        }else{
                            setParentCheckedLoop(node.parentNode,checked);
                        }
                        view.up('[itemId=userSetSjWinId]').down('[itemId=selectedCountItem]').setText('已选择 '+view.getChecked().length+' 个节点');
                    }, view);
                }
            },
            'userSetSjWin button[itemId="userSetSjSubmit"]': {
                click: function (view) {
                    var treeWinView = view.findParentByType('userSetSjWin');
                    var treeView = treeWinView.down('[itemId=userSetSjViewId]');
                    var selNodes = treeView.getChecked();
                    var gnList = new Array();
                    Ext.each(selNodes, function (node) {
                        if(node.data.text.indexOf("gray")!=-1){//跳过用户组权限节点
                        }else{
                            gnList.push(node.data.fnid);
                        }
                    });
                    var nodeStr = gnList.join(',');

                    var tip = '是否确认对该用户进行授权？';
                    if (window.wuserGridView.userids.split(',').length > 1) {
                        tip = '当前为批量设置，是否确认对多个用户授予以上权限？'
                    }
                    var that=this;
                    XD.confirm(tip,function(){
                        Ext.Msg.wait('正在进行数据权限设置操作，请耐心等待……','正在操作');
                        Ext.Ajax.request({
                            params: {nodeStr: nodeStr, userId: window.wuserGridView.userids,xtType: window.userGridViewTab},
                            url: '/user/UserSetSjSubmit',
                            method: 'POST',
                            timeout:1000000000,
                            sync: true,
                            success: function (resp) {
                                var respText = Ext.decode(resp.responseText);
                                Ext.Msg.wait('数据权限设置操作成功','正在操作').hide();
                                XD.msg(respText.msg);
                                /*if(window.userGridViewTab=='声像系统'){
                                    var url=respText.data.login_ip;
                                    url=url+'?szType=6&userid='+window.wuserGridView.userids;//数据节点权限6
                                    //发送跨域请求
                                    that.crossDomainByUrl(url);
                                }*/
                                view.findParentByType('userSetSjWin').close();
                            },
                            failure: function () {
                                Ext.Msg.wait('数据权限设置操作失败','正在操作').hide();
                                XD.msg('操作失败');
                            }
                        });
                    });
                }
            },
            'userSetOrganWin button[itemId="userSetOrganSubmit"]': {
                click: function (view) {
                    var treeWinView = view.findParentByType('userSetOrganWin');
                    var treeView = treeWinView.getComponent("userSetOrganView");
                    var selNodes = treeView.getChecked();
                    var organList = [];
                    Ext.each(selNodes, function (node) {
                        organList.push(node.data.fnid);
                    });

                    var tip = '是否确认对该用户进行授权？';
                    if (window.wuserGridView.userids.split(',').length > 1) {
                        tip = '当前为批量设置，是否确认对多个用户授予以上权限？'
                    }
                    XD.confirm(tip,function(){
                        Ext.Msg.wait('正在进行机构权限设置操作，请耐心等待……','正在操作');
                        Ext.Ajax.request({
                            params: {organList: organList, userId: window.wuserGridView.userids,xtType: window.userGridViewTab},
                            url: '/user/userSetOrganSubmit',
                            method: 'POST',
                            sync: true,
                            timeout:XD.timeout,
                            success: function (resp) {
                                var respText = Ext.decode(resp.responseText);
                                Ext.Msg.wait('机构权限设置操作成功','正在操作').hide();
                                XD.msg(respText.msg);
                                view.findParentByType('userSetOrganWin').close();
                            },
                            failure: function () {
                                Ext.Msg.wait('机构权限设置操作失败','正在操作').hide();
                                XD.msg('操作失败');
                            }
                        });
                    });
                }
            },
            'userSearchView [itemId=topCloseBtn]': {
                click: function () {
                    parent.closeObj.close(parent.layer.getFrameIndex(window.name));
                }
            },
            'userResetPWWin button[itemId="submitBtnID"]': {
                click: function (view) {
                    var win = view.findParentByType('userResetPWWin');
                    var form = win.down('form');
                    var that=this;
                    form.submit({
                        method: 'POST',
                        url: '/user/resetUserPW',
                        params: {userIds: window.wuserGridView.userids,xtType: window.userGridViewTab},
                        scope: this,
                        success: function (form, action) {
                            var respText = action.result;
                            XD.msg(respText.msg);
                            if (respText.success == true) {
                                var url=respText.data.login_ip;
                                var userid=respText.data.userid;
                                url=url+'?szType=2&userid='+ userid;//用户初始化密码2
                                //发送跨域请求
                                // that.crossDomainByUrl(url);
                            }
                            win.close();
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'userResetPWWin button[itemId="closeBtnID"]': {
                click: function (view) {
                    view.findParentByType('userResetPWWin').close();
                }
            },
            'userGridView [itemId=endisable]':{//启用/禁用功能
                click:function (view) {
                    var userGridView=view.findParentByType('userGridView')
                        ,users=userGridView.getSelectionModel().getSelection()
                        ,selectedCount=users.length;
                    if(selectedCount!=1){
                        XD.msg('请选择一条用户记录');
                        return;
                    }
                    var flag = true;
                    Ext.each(users, function (user) {
                        var name = user.data.realname;
                        if (name === '安全保密管理员' || name === '系统管理员' || name === '安全审计员') {
                            flag = false;
                        }
                    });
                    if (!flag) {
                        XD.msg('请勿对三员用户的进行该操作');
                        return;
                    }
                    var that=this;
                    Ext.Ajax.request({
                        url:'/user/endisableUser',
                        params:{
                            userid:users[0].get('id')
                        },
                        method:'POST',
                        success:function (res,opt) {
                            var responseText=Ext.decode(res.responseText);
                            if(responseText.success==true){
                                var url=responseText.data.login_ip;
                                var userid=responseText.data.userid;
                                url=url+'?szType=2&userid='+userid;//用户启动或禁用2,直接更新整个用户信息
                                //发送跨域请求
                                that.crossDomainByUrl(url);
                                userGridView.getStore().reload();
                                XD.msg(responseText.msg);
                            }
                        },
                        failure:function (res,opt) {
                            XD.msg('修改用户权限失败');
                        },
                        scope:this
                    });
                }
            },
            'userGridView button[itemId="changeOrgan"]': {//用户移动
                click: function (view) {
                    var userGridView = view.findParentByType('userGridView');
                    var select = userGridView.getSelectionModel();
                    if (select.getSelection() == 0) {
                        XD.msg('请选择用户');
                    } else {
                        var flag = true;
                        Ext.each(select.getSelection(), function (user) {
                            var name = user.data.realname;
                            if (name === '安全保密管理员' || name === '系统管理员' || name === '安全审计员') {
                                flag = false;
                            }
                        });
                        if (!flag) {
                            XD.msg('请勿对三员用户的进行该操作');
                            return;
                        }
                        var treeView = view.up('userView').down('userTreeView');
                        var selectid=treeView.selection.get('fnid');
                        if (treeView.selection.isRoot()) {
                            // XD.msg('请选择有效的机构');
                            // return;
                            selectid= '0';
                        }

                        var users = userGridView.getSelectionModel().getSelection();
                        var userIds = [];
                        for (var i = 0; i < users.length; i++) {
                            userIds.push(users[i].get('userid'));
                        }
                        var win = Ext.create("User.view.UserMoveSelectView", {
                            userIds: userIds,
                            userGridView:userGridView
                        });
                        var picker = win.down('[itemId=orgnameitemid]');
                        picker.extraParams = {pcid:selectid };
                        picker.on('render', function (picker) {
                            picker.store.load();
                        });
                        win.show();
                    }
                }
            },
            'userMoveSelectView [itemId=cancel]': {
                click: function (view) {
                    view.findParentByType('userMoveSelectView').close();
                }
            },
            'userMoveSelectView [itemId=save]': {
                click: function (view) {
                    var winView = view.findParentByType('userMoveSelectView');
                    var refid = winView.down('form').getValues()['refid'];
                    if (refid == '') {
                        XD.msg('请选择机构');
                        return;
                    }
                    var that=this;
                    winView.down('form').submit({
                        waitTitle: '提示',
                        waitMsg: '正在提交数据请稍后...',
                        url: '/user/changeOrgan',
                        method: 'post',
                        params: {userIds: winView.userIds,xtType: window.userGridViewTab},
                        success: function (form, action) {
                            winView.userGridView.delReload(winView.userIds.length);
                            var respText = Ext.decode(action.response.responseText);
                            var url=respText.data.login_ip;
                            var userid=respText.data.userid;
                            url=url+'?szType=2&userid='+userid;//用户移动2
                            //发送跨域请求
                            that.crossDomainByUrl(url);
                            winView.close();
                            XD.msg('保存成功');
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'userSetSjView':{
                itemcontextmenu: function (view, record, item, index, e, eOpts) {
                    //勾选与去掉勾选
                    var setCheckLoop = function (node, check) {
                        node.set('checked', check);
                        node.expand();
                        if (node.isNode) {
                            node.eachChild(function (child) {
                                setCheckLoop(child, check);
                            });
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
                        if (node!==null) {
                            if(node.get('roottype')==='classification'){
                                node.set('checked', check);
                            }
                            setParentCheckedLoop(node.parentNode, check);
                        }
                    };
                    //获取node属于的分类节点
                    var getParentLoop = function (node) {
                        if (node!==null) {
                            if(node.get('roottype')==='classification'){
                                return node;
                            }else{
                                return getParentLoop(node.parentNode);
                            }
                        }else{
                            return null;
                        }
                    };
                    //node下是否有被勾选的节点
                    var hasCheckedChild = function (node,found) {
                        for(var i=0;i<node.childNodes.length;i++){
                            var child = node.childNodes[i];
                            if(child.get('checked')){
                                found = true;
                                break;
                            }else if(child.childNodes!==null){
                                found = hasCheckedChild(child,found);
                                if(found){
                                    break;
                                }
                            }
                        }
                        return found;
                    };
                    //设置父节点不被选中
                    var setParentUncheckedLoop = function (node) {
                        if (node!==null) {
                            var parentNode = getParentLoop(node);
                            if (parentNode !== null && parentNode.childNodes !== null && !hasCheckedChild(parentNode, false)) {
                                // parentNode.set('checked', false);
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
                            text: singleTitle,
                            itemId: 'selectedAll',
                            iconCls: 'x-ctxmenu-checked-icon',
                            handler: function () {
                                if (singleTitle === '单选') {
                                    view.selection.set("checked", true);
                                    setParentCheckedLoop(view.selection,true);
                                } else {
                                    view.selection.set("checked", false);
                                    if(view.selection.parentNode!==null) {//不是最高节点
                                        setParentUncheckedLoop(view.selection.parentNode);
                                    }
                                }
                                view.up('[itemId=userSetSjWinId]').down('[itemId=selectedCountItem]').setText('已选择 '+view.getChecked().length+' 个节点');
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
                            // , {
                            //     text: "取消单选",
                            //     itemId: 'cancelSelected',
                            //     // iconCls: 'x-ctxmenu-delete-icon',
                            //     handler: function () {
                            //         view.selection.set("checked", false);
                            //         view.selection.expand();
                            //         view.selection.eachChild(function (child) {
                            //             child.set("checked", false);
                            //             child.expand();
                            //             setCheckLoop(child, false);
                            //         });
                            //     }
                            // }
                        ]
                    }).showAt(e.getXY());

                    if (view.selection.get('roottype') === 'classification') {
                        item.ctxMenu.remove(item.ctxMenu.getComponent('selectedAll'));
                    }
                }
            },
            'userGridView [itemId="addAdmin"]':{
                click:function(view){
                    if(window.wuserGridView.treeNodeid==='0')  {
                        XD.msg('请选择有效机构节点');
                        return;
                    }
                    Ext.Ajax.request({
                        url:'/user/userAdd/addAdminValidation',
                        params: {
                            organId: window.wuserGridView.treeNodeid,xtType: window.userGridViewTab
                        },
                        method:'POST',
                        success:function (res) {
                            var responseText=Ext.decode(res.responseText);
                            if(responseText.success===false){
                                // 查看当前节点机构名称
                            	var nodename = window.wuserGridView.nodename;
                                var userAddAdmin = Ext.create("User.view.UserAddAdmin");

                                var aqbmName = userAddAdmin.down('[name=aqbmName]');
                                aqbmName.setValue(nodename+'-安全保密管理员');
                                var xitongName = userAddAdmin.down('[name=xitongName]');
                                xitongName.setValue(nodename+'-系统管理员');
                                var aqsjName = userAddAdmin.down('[name=aqsjName]');
                                aqsjName.setValue(nodename+'-安全审计员');

                                userAddAdmin.show();
                            }else{
                                XD.msg('该机构已有三员用户');
                            }
                        },
                        failure:function () {
                            XD.msg('操作中断');
                        },
                        scope:this
                    });
                }
            },
            'userGridView [itemId="deleteAdmin"]':{
                click:function(view){
                    if(window.wuserGridView.treeNodeid==='0')  {
                        XD.msg('请选择有效机构节点');
                        return;
                    }
                    var that=this;
                    Ext.Ajax.request({
                        url:'/user/userAdd/addAdminValidation',
                        params: {
                            organId: window.wuserGridView.treeNodeid,xtType: window.userGridViewTab
                        },
                        method:'POST',
                        success:function (res) {
                            var responseText = Ext.decode(res.responseText);
                            if (responseText.success === true) {
                                XD.confirm('是否确定删除该机构的三员用户',function(){
                                    Ext.Ajax.request({
                                        params: {organId: window.wuserGridView.treeNodeid,xtType: window.userGridViewTab},
                                        url: '/user/deleteAdmin',
                                        method: 'post',
                                        success: function (resp) {
                                            view.up('userGridView').getStore().reload();
                                            var respText = Ext.decode(resp.responseText);

                                            if (respText.success == true) {
                                                var url=respText.data.login_ip;
                                                url=url+'?szType=22&organid='+window.wuserGridView.treeNodeid;//用户三员删除22
                                                //发送跨域请求
                                                // that.crossDomainByUrl(url);
                                            }

                                            XD.msg(respText.msg);
                                        },
                                        failure: function () {
                                            XD.msg('删除失败');
                                        }
                                    });
                                });
                            } else {
                                if ("不允许删除当前机构的三员账号。"==responseText.msg){
                                    XD.msg(responseText.msg);
                                } else {
                                    XD.msg('该机构不存在三员用户');
                                }
                            }
                        },
                        failure:function () {
                            XD.msg('操作中断');
                        },
                        scope:this
                    });
                }
            },
            'userAddAdmin button[itemId="close"]': {
                click: function (view) {
                    view.findParentByType('userAddAdmin').close();
                }
            },
            'userAddAdmin button[itemId="addSubmit"]': {
                click: function (view) {
                    var form = view.findParentByType('userAddAdmin').down('form');
                    var data = form.getValues();
                    if (data['secretAdmin'] === '' || data['systemAdmin'] === ''||data['auditor'] === '') {
                        XD.msg('有必填项未填写');
                        return;
                    }
                    if (data['secretAdmin'].length<2||data['systemAdmin'].length<2||data['auditor'].length<2) {
                        XD.msg('帐号字段输入长度不应少于2位');
                        return;
                    }
                    if (data['secretAdmin'].length>30||data['systemAdmin'].length>30||data['auditor'].length>30) {
                        XD.msg('帐号字段输入长度超限');
                        return;
                    }
                    if(data['secretAdmin']===data['systemAdmin']||data['secretAdmin']===data['auditor']||data['systemAdmin']===data['auditor']){
                        XD.msg('请勿输入相同的帐号');
                        return;
                    }

                    var that=this;
                    form.submit({
                        waitTitle: '提示',// 标题
                        waitMsg: '正在提交数据请稍后...',// 提示信息
                        url: '/user/userAdd/addAdmin',
                        method: 'POST',
                        params: {
                            organId: window.wuserGridView.treeNodeid,xtType: window.userGridViewTab
                        },
                        success: function (form, action) {
                            var respText = Ext.decode(action.response.responseText);
                            if (respText.success == true) {
                                XD.msg(respText.msg);

                                var url=respText.data.login_ip;
                                var userid=respText.data.userid;
                                var organid=respText.data.organid;
                                var adminUserid=respText.data.remark;//当前系统管理员账号
                                url=url+'?szType=21&userid='+userid+'&organid='+organid+'&adminUserid='+adminUserid;//用户三员增加21
                                //发送跨域请求
                                // that.crossDomainByUrl(url);

                                view.findParentByType('userAddAdmin').close();//添加成功后关闭窗口
                                window.wuserGridView.getStore().reload();
                            } else {
                                XD.msg(respText.msg);
                            }
                        },
                        failure: function (form, action) {
                            var respText = Ext.decode(action.response.responseText);
                            XD.msg(respText.msg);
                        }
                    });
                }
            },
            'userGridView [itemId=importUser]': {//导入用户
                click: function (view) {
                    new Ext.create('User.view.UserImportView',{
                        userGridView:view.findParentByType('userGridView')
                    }).show();
                }
            },
            'userGridView [itemId=expUse]': {//导出用户
                click: function (view) {
                    var userGridView = view.findParentByType('userGridView');
                    var select = userGridView.getSelectionModel();
                    var users = select.getSelection();
                    var selectCount = users.length;
                    if (selectCount == 0) {
                        XD.msg('请选择操作记录');
                        return;
                    }
                    var userids = [];
                    for (var i = 0; i < selectCount; i++) {
                        userids.push(users[i].get('userid'));
                    }
                    var downloadForm = document.createElement('form');
                    document.body.appendChild(downloadForm);
                    var inputTextElement = document.createElement('input');
                    inputTextElement.name ='userid';
                    inputTextElement.value = userids;
                    downloadForm.appendChild(inputTextElement);
                    downloadForm.action='/export/expUse';
                    downloadForm.method = "post";
                    downloadForm.submit();
                }
            },
            'userGridView [itemId=exportUser]': {//导出用户模板
                click: function (btn) {
                    var columnNames = ["账号","用户姓名","电话","地址","性别",'机构人员类型','人员职位',"用户状态"];
                    var reqUrl = "/export/exportColumnNames?columnNames=" + columnNames;
                    window.location.href = encodeURI(reqUrl);//解决IE浏览器的中文乱码问题
                }
            },
            'userImportView button[itemId=cancel]': {//导入用户-取消
                click: function (btn) {
                    btn.findParentByType('userImportView').close();
                }
            },
            'userImportView button[itemId=import]': {//导入用户-导入
                click: function (btn) {
                    var userImportView = btn.findParentByType('userImportView');
                    var form = userImportView.down('form');
                    if(!form.isValid()){
                        XD.msg('有必填项没有填写，请处理后再提交');
                        return;
                    }
                    var parentid = form.getValues()['parentid'];
                    var that=this;
                    form.submit({
                        waitTitle: '提示',
                        waitMsg: '正在处理请稍后...',
                        url: '/user/importUser',
                        method: 'POST',
                        params:{

                        },
                        success: function(reps,action) {
                            var respText = Ext.decode(action.response.responseText);
                            if (respText.success == true) {
                                XD.msg("导入成功");
                                var url = respText.loginIp;
                                var userid = respText.msg;
                                url = url + '?szType=2&userid=' + userid;//用户增加修改2
                                //发送跨域请求
                                that.crossDomainByUrl(url);
                            }else{
                                XD.msg(respText.msg);
                            }
                            userImportView.close();//添加成功后关闭窗口
                            window.wuserGridView.getStore().reload();
                        },
                        failure: function (reps,action) {
                            var msg = Ext.decode(action.response.responseText).msg;
                            XD.msg(msg);
                            window.wuserGridView.getStore().reload();
                        }
                    });
                }
            },

            'userGridView button[itemId=userInsertDevice]':{
                click : function (btn) {
                    var userGrid = btn.findParentByType('userGridView');
                    var select = userGrid.getSelectionModel().getSelection();
                    if(select<1){
                        XD.msg("请至少选择一条数据");
                        return;
                    }
                    var flag = true;
                    Ext.each(select, function (user) {
                        var name = user.data.realname;
                        if (name.indexOf('安全保密管理员')!=-1  || name.indexOf('系统管理员')!=-1  || name.indexOf('安全审计员')!=-1) {
                            flag = false;
                        }
                    });
                    if (!flag) {
                        XD.msg('请勿对三员用户的进行该操作');
                        return;
                    }
                    var form = Ext.create("User.view.UserSetDeviceJoinWin", {
                        height: window.innerHeight * 6 / 7,
                        width: 380
                    });
                    var userids = [];
                    for(var i=0;i<select.length;i++){
                        userids.push(select[i].get('userid'));
                    }
                    var treeView = form.getComponent("userSetDeviceJoinView");
                    treeView.getStore().proxy.extraParams.type = "";
                    treeView.getStore().proxy.extraParams.userids = userids;
                    treeView.getStore().load();
                    treeView.on('load', function () {
                        treeView.expandAll();//展开全部节点
                    });
                    treeView.userids = userids;
                    form.show();
                }
            },'userSetDeviceJoinWin button[itemId=userSetDeviceJoinSubmit]':{//提交设备接入权限
                click:function (btn) {
                    var treeWinView = btn.findParentByType('userSetDeviceJoinWin');
                    var treeView = treeWinView.getComponent("userSetDeviceJoinView");
                    var selNodes = treeView.getChecked();
                    var deviceList = new Array();
                    Ext.each(selNodes, function (node) {
                        if(node.data.leaf){
                            deviceList.push(node.data.deviceid);
                        }
                    });

                    var tip = '是否确认对该设备进行接入授权？';

                    XD.confirm(tip,function(){
                        Ext.Msg.wait('正在进行设备权限操作，请耐心等待……','正在操作');
                        Ext.Ajax.request({
                            params: {
                                deviceList: deviceList,
                                userids:treeView.userids,xtType: window.userGridViewTab
                            },
                            url: '/user/saveDeviceJoinAuthority',
                            method: 'POST',
                            sync: true,
                            success: function (resp) {
                                var respText = Ext.decode(resp.responseText);
                                Ext.Msg.wait('设备权限设置操作完成','正在操作').hide();
                                XD.msg(respText.msg);
                                btn.findParentByType('userSetDeviceJoinWin').close();
                            },
                            failure: function () {
                                Ext.Msg.wait('设备权限设置操作失败','正在操作').hide();
                                XD.msg('操作失败');
                            }
                        });
                    });
                }
            },
            'userSetDeviceJoinWin button[itemId=userSetDeviceJoinWinClose]':{//关闭设备权限设置窗口
                click:function (btn) {
                    btn.up('userSetDeviceJoinWin').close();
                }
            },

            'userSetDeviceJoinWin [itemId="userSetDeviceJoinView"]': {
                render: this.nodeLoopAndNotLoop
            },

            'userGridView button[itemId=userDevice]':{
                click : function (btn) {
                    var userGridView = btn.findParentByType('userGridView');
                    var select = userGridView.getSelection();
                    if (select.length < 1) {
                        XD.msg('请选择操作记录');
                    } else {
                        var flag = true;
                        Ext.each(select, function (user) {
                            var name = user.data.realname;
                            if (name.indexOf('安全保密管理员')!=-1  || name.indexOf('系统管理员')!=-1  || name.indexOf('安全审计员')!=-1) {
                                flag = false;
                            }
                        });
                        if (!flag) {
                            XD.msg('请勿对三员用户的进行该操作');
                            return;
                        }
                        var userids = new Array();
                        Ext.each(select, function (user) {
                            userids.push(user.data.userid);
                        });
                        var userStr = userids.join(',');

                        window.wuserGridView.userids = userStr;

                        var form = Ext.create("User.view.UserSetDeviceWin",{height:window.innerHeight * 6 / 7,width:380});
                        var treeView = form.getComponent("userSetDeviceView");
                        treeView.getStore().proxy.extraParams.type = "";
                        treeView.getStore().load();
                        treeView.on('load', function () {
                            treeView.expandAll();//展开全部节点
                        });
                        if (userids.length > 1) {
                            form.title = '批量设置设备权限';
                        }
                        form.show();
                    }
                }
            },

            'userSetDeviceWin button[itemId=userSetDeviceSubmit]':{//提交设备权限
                click:function (btn) {
                    var treeWinView = btn.findParentByType('userSetDeviceWin');
                    var treeView = treeWinView.getComponent("userSetDeviceView");
                    var selNodes = treeView.getChecked();
                    var deviceList = new Array();
                    Ext.each(selNodes, function (node) {
                        if(node.data.leaf){
                            deviceList.push(node.data.deviceid);
                        }
                    });

                    var tip = '是否确认对该用户进行授权？';
                    if (window.wuserGridView.userids.split(',').length > 1) {
                        tip = '当前为批量设置，是否确认对多个用户设置以上权限？'
                    }
                    XD.confirm(tip,function(){
                        Ext.Msg.wait('正在进行设备权限设置操作，请耐心等待……','正在操作');
                        Ext.Ajax.request({
                            params: {deviceList: deviceList, userId: window.wuserGridView.userids,xtType: window.userGridViewTab},
                            url: '/user/saveDeviceAuthority',
                            method: 'POST',
                            sync: true,
                            success: function (resp) {
                                var respText = Ext.decode(resp.responseText);
                                Ext.Msg.wait('功能权限设置操作完成','正在操作').hide();
                                XD.msg(respText.msg);
                                btn.findParentByType('userSetDeviceWin').close();
                            },
                            failure: function () {
                                Ext.Msg.wait('设备权限设置操作失败','正在操作').hide();
                                XD.msg('操作失败');
                            }
                        });
                    });
                }
            },

            'userSetDeviceWin button[itemId=userSetDeviceWinClose]':{//关闭设备权限设置窗口
                click:function (btn) {
                    btn.up('userSetDeviceWin').close();
                }
            },

            'userSetDeviceWin [itemId="userSetDeviceView"]': {
                render: this.nodeLoopAndNotLoop
            },

            'userGridView button[itemId=userWjqx]':{ //文件权限设置
                click : function (btn) {
                    var userGridView = btn.findParentByType('userGridView');
                    var select = userGridView.getSelection();
                    if (select.length != 1) {
                        XD.msg('请选择一条操作记录');
                    } else {
                        var flag = true;
                        Ext.each(select, function (user) {
                            var name = user.data.realname;
                            if (name.indexOf('安全保密管理员')!=-1  || name.indexOf('系统管理员')!=-1  || name.indexOf('安全审计员')!=-1) {
                                flag = false;
                            }
                        });
                        if (!flag) {
                            XD.msg('请勿设置三员用户的文件权限');
                            return;
                        }
                        var userids = new Array();
                        Ext.each(select, function (user) {
                            userids.push(user.data.userid);
                        });
                        var userStr = userids.join(',');

                        window.wuserGridView.userids = userStr;

                        var form = Ext.create("User.view.UserSetWjWin", {
                            height: window.innerHeight * 6 / 7,
                            width: 380
                        });

                        var treeView = form.getComponent("userSetWjView");
                        treeView.getStore().load();
                        form.show();
                    }
                }
            },
            'userSetWjWin [itemId="userSetWjView"]': {
                render: this.nodeLoopAndNotLoop
            },

            'userSetWjWin button[itemId=userSetWjSubmit]': {//文件权限提交
                click: function (btn) {
                    var userSetWjWin = btn.findParentByType('userSetWjWin');
                    var userSetWjView = userSetWjWin.down('userSetWjView');
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
                        url: '/user/setWJQXbtn',
                        method: 'post',
                        params:{
                            lylist:lylist,
                            gllist:gllist,
                            userid: window.wuserGridView.userids
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

            'userSetWjWin button[itemId=userSetWjClose]': {//文件权限关闭
                click: function (btn) {
                    var userSetWjWin = btn.findParentByType('userSetWjWin');
                    userSetWjWin.close();
                }
            },

            //设备区域权限
            'userGridView button[itemId=userArea]':{
                click : function (btn) {
                    var userGridView = btn.findParentByType('userGridView');
                    var select = userGridView.getSelection();
                    if (select.length < 1) {
                        XD.msg('请选择操作记录');
                    } else {
                        var flag = true;
                        Ext.each(select, function (user) {
                            var name = user.data.realname;
                            if (name.indexOf('安全保密管理员')!=-1  || name.indexOf('系统管理员')!=-1  || name.indexOf('安全审计员')!=-1) {
                                flag = false;
                            }
                        });
                        if (!flag) {
                            XD.msg('请勿对三员用户的进行该操作');
                            return;
                        }
                        var userids = new Array();
                        Ext.each(select, function (user) {
                            userids.push(user.data.userid);
                        });
                        var userStr = userids.join(',');

                        window.wuserGridView.userids = userStr;

                        var form = Ext.create("User.view.UserSetAreaWin",{height:window.innerHeight * 6 / 7,width:380});
                        var treeView = form.down("[itemId=userSetAreaId]");
                        treeView.getStore().load();
                        /*    treeView.on('load', function () {
                         treeView.expandAll();//展开全部节点
                         });*/
                        if (userids.length > 1) {
                            form.title = '批量设置区域权限';
                        }
                        form.show();
                    }
                }
            },'userSetAreaWin button[itemId=userSetAreaSubmit]':{
                click : function (btn) {
                    var treeWinView = btn.findParentByType('userSetAreaWin');
                    var treeView = treeWinView.down("[itemId=userSetAreaId]");
                    var selNodes = treeView.getChecked();
                    var areaList = new Array();
                    Ext.each(selNodes, function (node) {
                        if(node.data.leaf) {
                            areaList.push(node.data.areaid);
                        }
                    });

                    var tip = '是否确认对该用户进行授权？';
                    if (window.wuserGridView.userids.split(',').length > 1) {
                        tip = '当前为批量设置，是否确认对多个用户设置以上权限？'
                    }
                    XD.confirm(tip,function(){
                        Ext.Msg.wait('正在进行设备权限设置操作，请耐心等待……','正在操作');
                        Ext.Ajax.request({
                            params: {areaList: areaList, userId: window.wuserGridView.userids,xtType: window.userGridViewTab},
                            url: '/user/saveAreaAuthority',
                            method: 'POST',
                            sync: true,
                            success: function (resp) {
                                var respText = Ext.decode(resp.responseText);
                                Ext.Msg.wait('区域权限设置操作完成','正在操作').hide();
                                XD.msg(respText.msg);
                                btn.findParentByType('userSetAreaWin').close();
                            },
                            failure: function () {
                                Ext.Msg.wait('区域权限设置操作失败','正在操作').hide();
                                XD.msg('操作失败');
                            }
                        });
                    });
                }

            },"userSetAreaWin button[itemId=userSetAreaWinClose]":{
                click : function (btn) {
                    btn.up("userSetAreaWin").close();
                }
            },

            'userSetDeviceJoinView,userSetAreaView,userSetDeviceView': {
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
                    //node下是否有被勾选的节点
                    var hasCheckedChild = function (node,found) {
                        for(var i=0;i<node.childNodes.length;i++){
                            var child = node.childNodes[i];
                            if(child.get('checked')){
                                found = true;
                                break;
                            }else if(child.childNodes!==null){
                                found = hasCheckedChild(child,found);
                                if(found){
                                    break;
                                }
                            }
                        }
                        return found;
                    };
                    view.on('checkchange', function(node, checked) {
                        node.expand();
                        node.eachChild(function(child) {
                            child.set("checked", checked);
                            node.expand();
                            setChildLoop(child, checked);
                        });
                    }, view);
                }
            },

            'userGridView button[itemId=setFillinger]':{  //设置归档排序权限
                click: function (btn) {
                    Ext.MessageBox.wait('正在处理请稍后...', '提示');
                    Ext.Ajax.request({
                        params: {
                        },
                        url: '/user/getFillSortUser',
                        method: 'POST',
                        sync: true,
                        success: function (resp) {
                            Ext.MessageBox.hide();
                            var respText = Ext.decode(resp.responseText);
                            var selectItem = Ext.create("User.view.FillingSortUserSelectView");
                            selectItem.down('itemselector').getStore().load({
                                callback:function(){
                                    selectItem.down('itemselector').setValue(respText.data);
                                }
                            });
                            selectItem.show();
                        },
                        failure: function() {
                            Ext.MessageBox.hide();
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'fillingSortUserSelectView button[itemId="selectSubmit"]':{
                click:function(view){
                    var selectView = view.findParentByType('fillingSortUserSelectView');
                    var userids = selectView.down('itemselector').getValue();
                    Ext.Ajax.request({
                        params: {
                            userids:userids
                        },
                        url: '/user/setFillSortUser',
                        method: 'POST',
                        sync: true,
                        timeout:XD.timeout,
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            XD.msg(respText.msg);
                            selectView.close();
                        },
                        failure: function() {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'fillingSortUserSelectView button[itemId="selectClose"]':{
                click:function(view){
                    view.findParentByType('fillingSortUserSelectView').close();
                }
            },

            'organTreeView':{
                select:function(treemodel, record){
                    // 查找到已选择的用户id
                    var nodeUserSelectStore = treemodel.view.findParentByType('fillingSortUserSelectView').items.items[1].lastValue;
                    var userid = [];
                    for (var i = 0; i < nodeUserSelectStore.length; i++) {
                        userid.push(nodeUserSelectStore[i]);
                    }
                    var organid = record.data.fnid;
                    var itemselector = treemodel.view.findParentByType('fillingSortUserSelectView').down('itemselector');
                    var userstore = itemselector.getStore();
                    // 重新加载机构用户数据
                    userstore.reload({params:{organid:organid}});
                }
            },

            'userCopySelectView [itemId=usernameSearchId]':{
                search:function(searchfield){
                    var userCopySelectView = searchfield.findParentByType('userCopySelectView');
                    var username = searchfield.getValue(); //内容
                    var organtree = userCopySelectView.down('userOrganTreeView');
                    var node = organtree.selModel.getSelected().items[0];
                    var organid;
                    if (!node) {
                        organid = '0';
                    }else{
                        organid = node.get('fnid');
                    }
                    var itemselector = userCopySelectView.down('itemselector');
                    itemselector.getStore().reload({params:{organid:organid,username:username}});
                }
            }
        });
    },

    //跨域请求
    crossDomainByUrl:function(url){
        var store = Ext.create('Ext.data.Store', {
            model: 'User.model.UserGridModel',
            autoLoad: false,
            proxy: new Ext.data.ScriptTagProxy({
                url: url
            }),
            reader: new Ext.data.JsonReader({
                rootProperty: 'objList'
            })
        });
        store.load();
    },

    nodeLoopAndNotLoop:function(view){
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
        view.on('beforecheckchange', function (node, checked) {
            if(node.data.text.indexOf("gray")!=-1){//用户组权限不允取消勾选
                return false;
            }
        }, view);
        view.on('checkchange', function (node, checked) {
            node.expand();
            node.eachChild(function (child) {
                if(child.data.text.indexOf("gray")!=-1){//用户组权限不允取消勾选
                    return;
                }else {
                    child.set("checked", checked);
                }
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
});