var userInfo, accountGirdView;
Ext.define('FindAccount.controller.FindAccountController', {
    extend: 'Ext.app.Controller',

    views: [
    	'FindAccountGridView',
    	'FindAccountOutAddFormView',
    	'FindAccountResetPWWin',
        'FindAccountSetEXFromView',
        'FindAccountEditTimeView',
        'UserAddForm',
        'ElectronicView',
        'ElectronFormView','LookAddFormView','ElectronFormItemView', 'LookAddFormItemView'
    ],//加载view
    stores: [
    	'FindAccountGridStore','ApproveManStore','ApproveOrganStore','PurposeStore'
    ],//加载store
    models: [
    	'FindAccountGridModel'
    ],//加载model
    init: function () {
    	this.control({
    		// 初始化密码
    		'findAccountGridView button[itemId=resetPW]': {
    			click: function (view) {
                    var userGridView = view.findParentByType('findAccountGridView');
                    var select = userGridView.getSelectionModel();
                    var users = select.getSelection();
                    var selectCount = users.length;
                    if (selectCount == 0) {
                        XD.msg('至少选择一条初始化密码的数据！');
                    } else {
                        var userids = new Array();
                        for (var i = 0; i < selectCount; i++) {
                            userids.push(users[i].get('userid'));
                        }
                        userInfo = userids;
                        var form = Ext.create("FindAccount.view.FindAccountResetPWWin");
                        form.show();
                    }
                }
    		},
    		'findAccountResetPWWin button[itemId="submitBtnID"]': {
                click: function (view) {
                    var win = view.findParentByType('findAccountResetPWWin');
                    var form = win.down('form');
                    form.submit({
                        method: 'POST',
                        url: '/user/resetUserPW',
                        params: {userIds: userInfo},
                        scope: this,
                        success: function (form, action) {
                            XD.msg(action.result.msg);
                            win.close();
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'findAccountResetPWWin button[itemId="closeBtnID"]': {
                click: function (view) {
                    view.findParentByType('findAccountResetPWWin').close();
                }
            },
    		// 启用、禁用
    		'findAccountGridView button[itemId=endisable]': {
    			click:function (view) {
                    var userGridView = view.findParentByType('findAccountGridView')
                        , users = userGridView.getSelectionModel().getSelected().items[0]
                        , selectedCount = userGridView.getSelectionModel().getSelected().length;
                    if (selectedCount != 1) {
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

                    //如果该用户已到期，重新启用时候，先现在有限期。 到期时间为当前时间+有限期。
                    if (CompareDate(nowTime(), users.data.exdate) && users.data.status == '禁用') {
                        Ext.create('FindAccount.view.FindAccountSetEXFromView',{GridView:userGridView}).show();
                    }
                    else {
                        Ext.Ajax.request({
                            url: '/user/endisableUser',
                            params: {
                                userid: users.get('id')
                            },
                            method: 'POST',
                            success: function (res, opt) {
                                var responseText = Ext.decode(res.responseText);
                                if (responseText.success == true) {
                                    userGridView.getStore().reload();
                                    XD.msg(responseText.msg);
                                }
                            },
                            failure: function (res, opt) {
                                XD.msg('修改用户权限失败');
                            },
                            scope: this
                        });
                    }
                }
    		},

            //到期人员重新启动，选择有限期，提交

    		// 外来人员查档登记
	    	'findAccountGridView button[itemId=userRegister]': {
	    		click: function (view) {
	    			accountGirdView = view.up("findAccountGridView");
	    			Ext.create('FindAccount.view.FindAccountOutAddFormView').show();
	    		}
	    	},
            //外来人员表的修改
            'findAccountGridView button[itemId="userEdit"]': {
                click: function (view) {
                    accountGirdView = view.up("findAccountGridView");
                    var select = accountGirdView.getSelectionModel();
                    var users = select.getSelection();
                    var selectCount = users.length;
                    if (selectCount != 1) {
                        XD.msg('请选择一条记录');
                        return;
                    }
                    var form = Ext.create("FindAccount.view.FindAccountOutAddFormView", {title: '修改用户'});
                    form.show();
                    form.down('form').load({
                        url: '/user/getUser',
                        params: {
                            userid: users[0].get("id")
                        },
                        success : function(form, action) {
                        },
                        failure: function() {
                            XD.msg('操作失败');
                        }
                    });
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

                    form.submit({
                        waitTitle: '提示',// 标题
                        waitMsg: '正在提交数据请稍后...',// 提示信息
                        url: URL,
                        method: 'POST',
                        params: { // 此处可以添加额外参数

                        },
                        success: function (form, action) {
                            var respText = Ext.decode(action.response.responseText);
                            if (respText.success == true) {
                                XD.msg(respText.msg);
                                view.findParentByType('userAddForm').close();//添加成功后关闭窗口
                                accountGirdView.getStore().reload();
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
            //表单的条目删除
            'findAccountGridView button[itemId="userDel"]': {
                click: function (view) {
                    var userGridView = view.findParentByType('findAccountGridView');
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
                        XD.confirm('是否确定删除', function () {
                            var logins = [];
                            for (var i = 0; i < selectCount; i++) {
                                logins.push(users[i].get('userid'));
                            }
                            Ext.Ajax.request({
                                params: {logins: logins},
                                url: '/user/userDel',
                                method: 'POST',
                                sync: true,
                                timeout:XD.timeout,
                                success: function (resp) {
                                    XD.msg(Ext.decode(resp.responseText).msg);
                                    userGridView.delReload(selectCount);
                                }
                            });
                        });
                    }
                }
            },
            'userAddForm button[itemId="userAddClose"]': {
                click: function (view) {
                    view.findParentByType('userAddForm').close();
                }
            },


            //修改到期时间窗口的创建
            'findAccountGridView button[itemId=exdate]': {
                click: function (view) {
                    accountGirdView = view.up("findAccountGridView");
                    var record = accountGirdView.selModel.getSelection();
                    if (record.length != 1) {
                        XD.msg('请选择一条需要修改的数据');
                        return;
                    }
                    Ext.create('FindAccount.view.FindAccountEditTimeView',{userid:record[0].id}).show();

                }
            },
            'findAccountEditTimeView button[itemId=ExpireDateClose]': {
                click: function (view) {
                    view.findParentByType('findAccountEditTimeView').close();
            }},
                //修改到期时间的保存
            'findAccountEditTimeView button[itemId=ExpireDateSubmit]': {
                    click: function (view) {
                        var formView = view.findParentByType('findAccountEditTimeView');
                        var form = formView.down('form');
                        var URL = '/user/UpdateExpireDate';
                        var data = form.getValues();
                        if (data['exdate'] == '') {
                            XD.msg('请输入到期时间');
                            return;
                        }
                        exdate  = data['exdate'],
                        Ext.Ajax.request({
                            url: URL,
                            method: 'POST',
                            params: {
                                exdate:exdate,
                                userid:formView.userid
                            },
                            success: function (response) {
                                var obj = Ext.decode(response.responseText);
                                if (obj.success == true) {
                                    XD.msg(obj.msg);
                                    formView.close();//添加成功后关闭窗口
                                    accountGirdView.getStore().reload();
                                } else {
                                    XD.msg(obj.msg);
                                }
                            }
                        });

              }},

	    	'findAccountOutAddFormView button[itemId=userOutAddSubmit]': {//新增外来人员临时用户
                click: function (view) {
                	var formView = view.findParentByType('findAccountOutAddFormView');
                    var form = formView.down('form');
                    var URL = '/user/userOutAddSubmit';
                    if (formView.title == '修改用户') {
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
                    if (data['loginname'].length<15) {
                        XD.msg('请输入正确的格式');
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

                    form.submit({
                        waitTitle: '提示',// 标题
                        waitMsg: '正在提交数据请稍后...',// 提示信息
                        url: URL,
                        method: 'POST',
                        params: {},
                        success: function (form, action) {
                            var respText = Ext.decode(action.response.responseText);
                            if (respText.success == true) {
                                XD.msg(respText.msg);
                                formView.close();//添加成功后关闭窗口
                                accountGirdView.getStore().reload();
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

            'findAccountOutAddFormView button[itemId=userOutAddClose]': {//外来人员查档登记 返回
                click: function (view) {
                    view.findParentByType('findAccountOutAddFormView').close();
                }
            },

            //提交查档单
            'findAccountGridView [itemId=submiteleborrow]':{
                click: function (view) {
                    accountGirdView =  view.up("findAccountGridView");
                    var userGridView = view.findParentByType('findAccountGridView')
                        , users = userGridView.getSelectionModel().getSelected().items[0]
                        , selectedCount = userGridView.getSelectionModel().getSelected().length;
                    if (selectedCount > 1) {
                        XD.msg('只能选择一条用户记录');
                        return;
                    }

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
                        userid:users == undefined?'add':users.get('id'),
                        items: [{xtype: 'electronFormView'}]
                    });
                    window.wmedia = [];
                    window.borrowElectronadd = borrowRoquest;
                    var form = borrowRoquest.down('[itemId=electronFormItemViewId]');
                    var approveOrganStore = form.down('[itemId=approveOrgan]').getStore();
                    approveOrganStore.proxy.extraParams.type = "submit"; //申请时获取审批单位
                    approveOrganStore.proxy.extraParams.taskid = "";
                    approveOrganStore.proxy.extraParams.worktext = "查档审批";
                    approveOrganStore.proxy.extraParams.nodeid = "";
                    approveOrganStore.load(); //加载数据
                    var realname=users == undefined?'':users.get('realname');

                    if(selectedCount == 1){
                        form.load({
                            url: '/electron/getBorrowDocByTemporaryUser',
                            params:{
                                userid: users.get('id')
                            },
                            success: function (form, action) {
                            },
                            failure: function () {
                                XD.msg('操作失败');
                            }
                        });
                    }
                    borrowRoquest.show();
                }
            },

            //提交实体查档单
            'findAccountGridView [itemId = submitstborrow]': {
                click: function (view) {
                    accountGirdView =  view.up("findAccountGridView");
                    var userGridView = view.findParentByType('findAccountGridView')
                        , users = userGridView.getSelectionModel().getSelected().items[0]
                        , selectedCount = userGridView.getSelectionModel().getSelected().length;
                    if (selectedCount > 1) {
                        XD.msg('只能选择一条用户记录');
                        return;
                    }
                    var borrowRoquest = Ext.create('Ext.window.Window', {
                        height: '100%',
                        width: '100%',
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        modal: true,
                        closeToolText:'关闭',
                        title: '实体查档申请',
                        closeAction: 'hide',
                        layout: 'fit',
                        closable:false,
                        userid:users==undefined?'add':users.get('id'),
                        items: [{xtype: 'lookAddFormView'}]
                    });
                    window.wmedia = [];
                    window.borrowStadd = borrowRoquest;
                    var form = borrowRoquest.down('[itemId=lookAddFormItemViewId]');
                    var realname= users==undefined?'':users.get('realname');

                    if(selectedCount == 1){
                        form.load({
                            url: '/electron/getBorrowDocByTemporaryUser',
                            params:{
                                userid: users.get('id')
                            },
                            success: function (form, action) {
                            },
                            failure: function () {
                                XD.msg('操作失败');
                            }
                        });
                    }

                    borrowRoquest.show();
                }
            },
            'electronFormItemView button[itemId=electronFormSubmit]': {//电子查档单--提交
                click: function (btn) {
                    var form = btn.findParentByType('electronFormItemView');
                    var borrowts = form.getComponent('borrowtsId').getValue();
                    var spman = form.getComponent('spmanId').getValue();
                    var sendMsg = form.down("[itemId=sendmsgId]").getValue();
                    var certificatetype = form.down('[itemId=certificatetypeId]').getValue();
                    var certificatenumber = form.down('[itemId=certificatenumberId]').getValue();
                    if(!form.isValid()) {
                        XD.msg('有必填项未填写，请处理后再提交');
                        return;
                    }
                    if (borrowts == '' || borrowts == null||String(borrowts).indexOf(".")>-1||isNaN(borrowts)||parseInt(borrowts)<1 ) {XD.msg('查档天数不合法');return;}
                    if(certificatetype=='身份证'){
                        if(!certificatenumber.match(/^[0-9]{15}$/) && !certificatenumber.match(/^[0-9]{17}[0-9xX]$/)){
                            XD.msg('请输入正确的身份证号码');
                            return;
                        }
                    }
                    var electronAddWin = btn.findParentByType('electronAddView');
                    Ext.MessageBox.wait('正在提交数据请稍后...','提示');
                    form.submit({
                        url: '/electron/electronAddForm',
                        method: 'POST',
                        params: {
                            eleids:window.wmedia,
                            userid:window.borrowElectronadd.userid,
                            sendMsg:sendMsg
                        },
                        success: function (form, action) {
                            var respText = Ext.decode(action.response.responseText);
                            Ext.Msg.hide();
                            XD.msg(respText.msg);
                            // window.wlookAddMxGrid.getStore().loadPage(1);
                            if(electronAddWin) {
                                electronAddWin.close();
                            }else{
                                btn.findParentByType('window').close();
                            }
                            accountGirdView.getStore().reload();
                        },
                        failure: function () {
                            Ext.Msg.hide();
                            XD.msg('操作失败');
                        }
                    });
                }
            }, 'electronFormItemView button[itemId=electronFormClose]': {//电子查档单界面----关闭
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
                    if( view.findParentByType("electronAddView")){
                        view.findParentByType("electronAddView").close();
                    }else {
                        view.findParentByType("window").close();
                    }
                }
            }, 'lookAddFormItemView button[itemId=lookAddFormSubmit]': { //实体查档单界面----提交
                click: function (btn) {
                    var form = btn.findParentByType('lookAddFormItemView');
                    var lookAddSqWin = form.findParentByType('lookAddSqView');
                    var borrowts = form.getComponent('borrowtsId').getValue();
                    var spman = form.getComponent('spmanId').getValue();
                    var sendMsg = form.down("[itemId=sendmsgId]").getValue();

                    if(!form.isValid()) {
                        XD.msg('有必填项未填写，请处理后再提交');
                        return;
                    }
                    if (borrowts == '' || borrowts == null||String(borrowts).indexOf(".")>-1||isNaN(borrowts)||parseInt(borrowts)<1 ) {XD.msg('查档天数不合法');return;}
                    if (spman==null) {XD.msg('受理人不能为空');return;}

                    if (isNaN(borrowts) || parseInt(borrowts) <= 0) {
                        XD.msg('非法查档天数输入值');
                        return;
                    }
                    Ext.MessageBox.wait('正在提交数据请稍后...','提示');
                    form.submit({
                        waitTitle: '',// 标题
                        url: '/electron/stLookAddForm',
                        params:{
                            eleids:window.wmedia,
                            userid:window.borrowStadd.userid,
                            sendMsg:sendMsg
                        },
                        method: 'POST',
                        success: function (form, action) {
                            var respText = Ext.decode(action.response.responseText);
                            Ext.Msg.hide();
                            XD.msg(respText.msg);
                            // window.wlookAddMxGrid.getStore().loadPage(1);
                            if(lookAddSqWin) {
                                lookAddSqWin.close();
                            }else{
                                btn.findParentByType('window').close();
                            }
                            accountGirdView.getStore().reload();
                        },
                        failure: function () {
                            Ext.Msg.hide();
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'lookAddFormItemView button[itemId=lookAddFormClose]': { //提交实体查档单界面---关闭
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
                    if( btn.findParentByType("lookAddSqView")){
                        btn.findParentByType("lookAddSqView").close();
                    }else {
                        btn.findParentByType("window").close();
                    }
                }
            },


            'electronFormItemView button[itemId=electronUpId]': {//电子查档单--查看附件
                click: function (view) {
                    var win= Ext.create("Ext.window.Window", {
                        width: '70%',
                        height: '70%',
                        title: '查看附件',
                        modal: true,
                        closeToolText: '关闭',
                        layout: 'fit',
                        items: [{xtype: 'FindAccountElectronic'}],
                        listeners :{
                            close: function (win) {
                                var wmedia = window.wmedia;
                                var electronFormItemView = view.findParentByType('electronFormView').down('electronFormItemView');
                                var count;
                                if (wmedia == '') {
                                    count = 0;
                                }
                                else {
                                    count = wmedia.split(",").length;
                                }
                                electronFormItemView.down('[itemId = mediacount]').setText('共' + count + '份');

                                //加载附件文本框
                                Ext.Ajax.request({
                                    url: '/electronic/getByeleid',
                                    async: false,
                                    traditional: true,//后台接收数组为null这里设为true就可以了
                                    params: {
                                        eleids: window.wmedia,
                                    },
                                    success: function (response) {
                                        var result = JSON.parse(response.responseText).data;
                                        var filename = [];
                                        for (var i = 0; i < result.length; i++) {
                                            filename.push(result[i].filename);
                                        }
                                        var mediatext=filename.join(",");
                                        mediatext = mediatext.length>70?mediatext.substring(0,70)+"..":mediatext.substring(1);
                                        electronFormItemView.down('[itemId = media]').setValue(mediatext);
                                    }
                                });
                            }
                        }
                    });

                    window.wform = view.findParentByType('electronFormItemView');
                    win.show();
                }
            },
            'lookAddFormItemView button[itemId=stElectronUpId]': { //提交实体借阅-查看附件
                click: function (view) {
                    var win = Ext.create("Ext.window.Window", {
                        width: '70%',
                        height: '70%',
                        title: '查看附件',
                        modal: true,
                        closeToolText: '关闭',
                        layout: 'fit',
                        items: [{xtype: 'FindAccountElectronic'}],
                        listeners:{
                            close:function (win) {
                                var wmedia = window.wmedia;

                                var lookAddFormItemView = view.findParentByType('lookAddFormView').down('lookAddFormItemView');
                                var count;
                                if(wmedia==''){
                                    count = 0;
                                }
                                else{
                                    count =  wmedia.split(",").length;
                                }
                                lookAddFormItemView.down('[itemId = mediacount]').setText('共'+count+'份') ;

                                //加载附件文本框
                                Ext.Ajax.request({
                                    url: '/electronic/getByeleid',
                                    async: false,
                                    traditional: true,//后台接收数组为null这里设为true就可以了
                                    params: {
                                        eleids: window.wmedia,
                                    },
                                    success: function (response) {
                                        var result = JSON.parse(response.responseText).data;
                                        var filename = [];
                                        for (var i = 0; i < result.length; i++) {
                                            filename.push(result[i].filename);
                                        }
                                        lookAddFormItemView.down('[itemId = media]').setValue(filename.join(","));
                                    }
                                });
                            }
                        }
                    });
                    window.wform = view.findParentByType('lookAddFormItemView');
                    win.show();
                }
            }

    	})
    }
})

//获取当前时间
function nowTime()
{
    var now = new Date();

    var year = now.getFullYear();       //年
    var month = now.getMonth() + 1;     //月
    var day = now.getDate();            //日

    var hh = now.getHours();            //时
    var mm = now.getMinutes();          //分
    var ss = now.getSeconds();           //秒

    var clock = year + "-";

    if(month < 10)
        clock += "0";

    clock += month + "-";

    if(day < 10)
        clock += "0";

    clock += day + " ";

    if(hh < 10)
        clock += "0";

    clock += hh + ":";
    if (mm < 10) clock += '0';
    clock += mm + ":";

    if (ss < 10) clock += '0';
    clock += ss;
    return(clock);
}

//比较时间大小
function CompareDate(d1,d2)
{
    return ((new Date(d1.replace(/-/g,"\/"))) > (new Date(d2.replace(/-/g,"\/"))));
}