/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Inform.controller.InformController', {
     extend: 'Ext.app.Controller',

    views: ['InformView','InformAddFormView','PostedSelectView','InformLookView','OrganTreeView','StickView'],//加载view
    stores: ['InformGridStore','PostedSelectStore','OrganTreeStore'],//加载store
    models: ['InformGridModel','PostedSelectModel','OrganTreeModel'],//加载model

    init: function () {
        this.control({
            'informGridView':{
                afterrender:function (view) {
                    if(buttonflag=='1'){//利用平台
                        var buttons = view.getDockedItems("toolbar[dock=top]")[0].items.items;
                        for(var i=0;i<buttons.length;i++){
                            if(i!=6){
                                buttons[i].hide();
                            }
                        }
                        view.initGrid({ flag : '1'});
                        view.columns[6].hide();
                        view.columns[7].hide();
                    }else{//管理平台
                        view.initGrid();
                    }
                }
            },

            'informGridView button[itemId=informAdd]':{
                click:function(view){
                    window.cardobj = {grid:view.findParentByType('informGridView')};
                    var postedview = Ext.create('Inform.view.InformAddFormView',{title: '新增公告',operate:'add'});
                    var container=postedview.down('[itemId=container]');
                    container.eleids=[];
                    container.fileName=[];
                    window.wpostedview = postedview;
                    window.wpostedview.postedUserData = undefined;
                    window.wpostedview.postedUsergroupData = undefined;
                    postedview.down('[itemId=formPostedUser]').show();
                    postedview.down('[itemId=formPostedUsergroup]').show();
                    postedview.show();
                }
            },

            'informGridView button[itemId=informEdit]':{
                click:function(view){
                    var select = view.findParentByType('informGridView').getSelectionModel();
                    if (select.getCount()!=1) {
                        XD.msg('请选择一条数据');
                        return;
                    }

                    window.cardobj = {grid:view.findParentByType('informGridView')};
                    var postedview = Ext.create('Inform.view.InformAddFormView',{title:'修改公告',operate:'edit'});
                    window.wpostedview = postedview;
                    postedview.down('[itemId=formPostedUser]').show();
                    postedview.down('[itemId=formPostedUsergroup]').show();
                    postedview.show();

                    var container=postedview.down('[itemId=container]');
                    container.eleids=[];
                    container.fileName=[];

                    var informs = view.findParentByType('informGridView').getSelectionModel().getSelection();
                    var ids = [];
                    for(var i=0;i<informs.length;i++){
                        ids.push(informs[i].get('id'));
                    }
                    setTimeout(function () {
                        Ext.Ajax.request({
                            method: 'POST',
                            url: '/inform/electronicsFile/'+informs[0].get('id') + '/',
                            success: function (response, opts) {
                                var data = Ext.decode(response.responseText);
                                var lable = [];
                                for(var i=0;i<data.length;i++){
                                    container.eleids.push(data[i].eleid);
                                    container.fileName.push(data[i].filename);
                                    lable.push({xtype: 'label',text: data[i].filename
                                        ,eleid:data[i].eleid});
                                }
                                container.add(lable);
                            }
                        });

                        postedview.down('form').load({
                            url: '/inform/getInform?id='+informs[0].get('id'),
                            //waitMsg: '请稍后......',
                            success : function(form, action) {
                                var data=action.result.data;
                                postedview.down('form').down('datefield').setValue(new Date(data['limitdate']));
                                postedview.down('form').down('datefield').setMinValue(new Date(data['informdate']));
                                document.getElementById('editFrame').contentWindow.setHtml(data['text']);
                            },
                            failure: function() {
                                XD.msg('操作失败');
                            }
                        });
                    }, 100);
                }
            },

            'informGridView button[itemId=informLook]':{
                click:function(view){
                    var select = view.findParentByType('informGridView').getSelectionModel();
                    if (select.getCount()!=1) {
                        XD.msg('请选择一条数据');
                        return;
                    }
                    var informs = select.getSelection();
                    var win = Ext.create('Inform.view.InformLookView',{});
                    win.show();
                    setTimeout(function () {
                        Ext.Ajax.request({
                            method: 'POST',
                            url: '/inform/getInform',
                            params:{
                                id:informs[0].get('id')
                            },
                            scope: this,
                            success: function (response, opts) {
                                var data = Ext.decode(response.responseText).data;
                                win.down('[itemId=title]').setText(data['title']);
                                console.log(String(data['informdate']))
                                win.down('[itemId=date]').setText('发布日期：'+new Date(data['informdate']).format("yyyy-MM-dd hh:mm:ss"));
                                document.getElementById('editFrame').contentWindow.setHtml(data['text']);
                                document.getElementById('editFrame').contentWindow.hideButton();
                            }
                        });
                        Ext.Ajax.request({
                            method: 'POST',
                            url: '/inform/electronicsFile/'+informs[0].get('id') + '/',
                            success: function (response, opts) {
                                var data = Ext.decode(response.responseText);
                                if(data.length>0){
                                    var lable = [];
                                    lable.push({xtype: 'label',text: '相关附件：(双击文件可以下载)',margin: '0 0 5 20'});
                                    for(var i=0;i<data.length;i++){
	                                    lable.push({
	                                    	xtype: 'label',text: data[i].filename,eleid:data[i].eleid,margin: '0 0 5 35',listeners: {
			                                    render: function (view) {//渲染后添加双击事件
			                                        view.addListener("dblclick", function () {
			                                            window.open("/inform/openFile?eleid=" + view.eleid +'&fileName=' +view.text);
			                                        }, null, {element: 'el'});
			                                    },
			                                    scope: this
											}
										});
                                    }
                                }
                                win.add(lable);
                            }
                        });
                    }, 100);
                }
            },

            'informGridView button[itemId=informDel]':{
                click:function(view){
                    var informGridView = view.findParentByType('informGridView');
                    var select = informGridView.getSelectionModel();
                    if (select.getCount()==0) {
                        XD.msg('至少选择一条数据');
                        return;
                    }

                    XD.confirm('确定删除吗',function(){
                        var informs = select.getSelection();
                        var ids = [];
                        for(var i=0;i<informs.length;i++){
                            ids.push(informs[i].get('id'));
                        }
                        Ext.MessageBox.wait('正在处理请稍后...', '提示');
                        Ext.Ajax.request({
                            params: {ids:ids},
                            url: '/inform/informDel',
                            method: 'POST',
                            sync: true,
                            success: function (resp) {
                                Ext.MessageBox.hide();
                                var respText = Ext.decode(resp.responseText);
                                if(respText.success==true){
                                    XD.msg(respText.msg);
                                    informGridView.delReload(select.getCount());
                                }
                            },
                            failure: function() {
                                Ext.MessageBox.hide();
                                XD.msg('操作失败');
                            }
                        });
                    })
                }
            },

            'informGridView button[itemId=postedUser]':{//列表－推送到用户组
                click:function(view){
                    window.cardobj = {grid:view.findParentByType('informGridView')};
                    var select = view.findParentByType('informGridView').getSelectionModel();
                    if (select.getCount()!=1) {
                        XD.msg('请选择一条数据');
                        return;
                    }
                    var inform = select.getSelection();
                    Ext.Msg.wait('正在查询用户，请稍等......','正在操作');
                    Ext.Ajax.request({
                        params: {id:inform[0].get('id'),flag:'userto'},
                        url: '/inform/getHasPosteds',
                        method: 'POST',
                        sync: true,
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            if (respText.success == true) {
                                var postedwin = Ext.create('Inform.view.PostedSelectView',{flag:'userselect',btnPosition:'grid'});
                                postedwin.down('itemselector').getStore().proxy.extraParams.flag = 'userfrom';
                                postedwin.down('itemselector').getStore().load({
                                    callback:function(){
                                        postedwin.down('itemselector').setValue(respText.data);
                                        postedwin.show();
                                    }
                                });
                            }else{
                                XD.msg(respText.msg);
                            }
                            Ext.defer(function () {
                                Ext.Msg.wait('查询完毕','正在操作').hide();
                            },1000);
                        },
                        failure: function() {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'informGridView button[itemId=postedUserGroup]':{//列表－推送到用户组
                click:function(view){
                    window.cardobj = {grid:view.findParentByType('informGridView')};
                    var select = view.findParentByType('informGridView').getSelectionModel();
                    if (select.getCount()!=1) {
                        XD.msg('请选择一条数据');
                        return;
                    }

                    var inform = select.getSelection();
                    Ext.Msg.wait('正在查询用户组，请稍等......','正在操作');
                    Ext.Ajax.request({
                        params: {id:inform[0].get('id'),flag:'usergroupto'},
                        url: '/inform/getHasPosteds',
                        method: 'POST',
                        sync: true,
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            if (respText.success == true) {
                                var postedwin = Ext.create('Inform.view.PostedSelectView',{flag:'usergroupselect',btnPosition:'grid'});
                                postedwin.down('itemselector').getStore().proxy.extraParams.flag = 'usergroupfrom';
                                postedwin.down('itemselector').getStore().load({
                                    callback:function(){
                                        postedwin.down('itemselector').setValue(respText.data);
                                        postedwin.show();
                                    }
                                });
                                postedwin.down('organTreeView').hide();
                            }else{
                                XD.msg(respText.msg);
                            }
                            Ext.defer(function () {
                                Ext.Msg.wait('查询完毕','正在操作').hide();
                            },1000);
                        },
                        failure: function() {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'informAddFormView button[itemId=formPostedUser]':{//表单－推送到用户
                click:function (view) {
                    var informForm = view.findParentByType('informAddFormView');
                    var informFormvalues = informForm.down('form').getValues();
                    if(informFormvalues['title']==''){
                        XD.msg('公告标题不能为空');
                        return;
                    }

                    var postedwin = Ext.create('Inform.view.PostedSelectView',{flag:'userselect',btnPosition:'form',formTitle:informForm.title});
                    postedwin.down('itemselector').getStore().proxy.extraParams.flag = 'userfrom';
                    if(view.up('window').title=='修改公告'){
                        Ext.Ajax.request({
                            params: {id:window.cardobj.grid.getSelectionModel().getSelection()[0].get('id'),flag:'userto'},
                            url: '/inform/getHasPosteds',
                            method: 'POST',
                            sync: true,
                            success: function (resp) {
                                var respText = Ext.decode(resp.responseText);
                                if (respText.success == true) {
                                    postedwin.down('itemselector').getStore().load({
                                        callback:function(){
                                            postedwin.down('itemselector').setValue(respText.data);
                                            postedwin.show();
                                        }
                                    });
                                }else{
                                    XD.msg(respText.msg);
                                }
                            },
                            failure: function() {
                                XD.msg('操作失败');
                            }
                        });
                    }else{
                        postedwin.down('itemselector').getStore().reload({
                            callback:function(){
                                postedwin.down('itemselector').setValue(null);
                                postedwin.show();
                            }
                        });
                    }
                }
            },

            'informAddFormView button[itemId=formPostedUsergroup]':{//表单－推送到用户组
                click:function (view) {
                    var informForm = view.findParentByType('informAddFormView');
                    var informFormvalues = informForm.down('form').getValues();
                    if(informFormvalues['title']==''){
                        XD.msg('公告标题不能为空');
                        return;
                    }

                    var postedwin = Ext.create('Inform.view.PostedSelectView',{flag:'usergroupselect',btnPosition:'form',formTitle:informForm.title});
                    postedwin.down('itemselector').getStore().proxy.extraParams.flag = 'usergroupfrom';
                    if(view.up('window').title=='修改公告'){
                        Ext.Ajax.request({
                            params: {id:window.cardobj.grid.getSelectionModel().getSelection()[0].get('id'),flag:'usergroupto'},
                            url: '/inform/getHasPosteds',
                            method: 'POST',
                            sync: true,
                            success: function (resp) {
                                var respText = Ext.decode(resp.responseText);
                                if (respText.success == true) {
                                    postedwin.down('itemselector').getStore().load({
                                        callback:function(){
                                            postedwin.down('itemselector').setValue(respText.data);
                                            postedwin.show();
                                        }
                                    });

                                }else{
                                    XD.msg(respText.msg);
                                }
                            },
                            failure: function() {
                                XD.msg('操作失败');
                            }
                        });
                    }else{
                        postedwin.down('itemselector').getStore().reload({
                            callback:function(){
                                postedwin.down('itemselector').setValue(null);
                                postedwin.show();
                            }
                        });
                    }
                }
            },
            'postedSelectView button[itemId=allOrNotSelect]':{
                click:function(view){
                    var itemSelector = view.findParentByType('postedSelectView').down('itemselector');
                    if(view.getText()=='全选'){
                        var fromList = itemSelector.fromField.boundList,
                            allRec = fromList.getStore().getRange();
                            fromList.getStore().remove(allRec);
                            itemSelector.toField.boundList.getStore().add(allRec);
                            itemSelector.syncValue();//
                             view.setText('取消全选');
                    }else{
                        var toList = itemSelector.toField.boundList,
                            allRec = toList.getStore().getRange();
                        toList.getStore().remove(allRec);
                        itemSelector.fromField.boundList.getStore().add(allRec);
                        itemSelector.syncValue();
                        view.setText('全选');
                    }

                }
            },

            'postedSelectView button[itemId=postedSelectSubmit]':{
                click:function(view){
                    var postedSelectView = view.findParentByType('postedSelectView');
                    var posteds = postedSelectView.down('itemselector').getValue();
                    if(posteds[0]==""){
                       posteds = null;
                    }
                    var userid = [];
                    var postedview = window.cardobj.grid.getSelectionModel().getSelection();
                    for (var i = 0; i < postedview.length; i++) {
                        userid.push(postedview[i].id);
                    }
                    var hideValue = postedSelectView.down('itemselector').toField.getStore().hideValue;
                    if(hideValue){
                        for (var i = 0; i < hideValue.length; i++) {
                            userid.push(hideValue[i]);
                        }
                    }
                    Ext.MessageBox.wait('正在处理请稍后...', '提示');
                    Ext.Ajax.request({
                        params: {
                        	id: postedSelectView.btnPosition=='grid' || postedSelectView.formTitle=='修改公告'?(postedview[0].get('id')):'',
                        	posteds:posteds,
                        	flag:postedSelectView.flag
                        },
                        url: '/inform/postedInform',
                        method: 'POST',
                        sync: true,
                        timeout:XD.timeout,
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            if (respText.success == true) {
                                Ext.MessageBox.hide();
                                var data = respText.data;
                                if(respText.data==null){//列表或修改公告表单-推送用户或用户组
                                    XD.msg(respText.msg);
                                    if(postedSelectView.btnPosition=='grid'){
                                        window.cardobj.grid.notResetInitGrid();
                                    }
                                    postedSelectView.close();
                                }else{//增加公告表单-推送用户或用户组
                                    if(respText.msg=='设置用户推送成功，请点击提交按钮完成发布'){
                                        window.wpostedview.postedUserData = data;
                                    }
                                    if(respText.msg=='设置用户组推送成功，请点击提交按钮完成发布'){
                                        window.wpostedview.postedUsergroupData = data;
                                    }
                                    Ext.MessageBox.alert("提示", respText.msg, function(){
                                        postedSelectView.close();
                                    });
                                }
                            }else{
                                Ext.MessageBox.hide();
                                XD.msg(respText.msg);
                                view.up('window').hide();
                                window.cardobj.grid.notResetInitGrid();
                            }
                        },
                        failure: function() {
                            Ext.MessageBox.hide();
                            XD.msg('操作失败');
                        }
                    });
                    //Ext.MessageBox.hide();
                }
            },

            'postedSelectView button[itemId=postedSelectClose]':{
                click:function(view){
                    var postedSelectView = view.findParentByType('postedSelectView');
                    // if(postedSelectView.btnPosition=='grid'){
                    //     window.cardobj.grid.initGrid();
                    // }
                    postedSelectView.close();
                }
            },

            'informAddFormView button[itemId=informAddSubmit]':{
                click:function(view){
                    var informAddFormView = view.findParentByType('informAddFormView');
                    var container=informAddFormView .down('[itemId=container]');
                    var form = informAddFormView.down('form');
                    var data = form.getValues();
                    var id = data['id'];
                    var title = data['title'];
                    var date = data['limitdate'];
                    var html = document.getElementById('editFrame').contentWindow.getHtml();

                    var url = '/inform/addInform';
                    var params = {
                        title:title,
                        limitdate:date,
                        html:html,
                        eleids:container.eleids
                    };
                    var postedUserData = window.wpostedview.postedUserData;
                    var postedUsergroupData = window.wpostedview.postedUsergroupData;
                    if(typeof postedUserData != 'undefined'){
                        var postedUserDatas = postedUserData.split(',');
                        params.postedUser = postedUserDatas[0];
                        params.postedUserids = postedUserDatas[1];
                    }
                    if(typeof postedUsergroupData != 'undefined'){
                        var postedUsergroupDatas = postedUsergroupData.split(',');
                        params.postedUsergroup = postedUsergroupDatas[0];
                        params.postedUsergroupids = postedUsergroupDatas[1];
                    }

                    if(informAddFormView.operate=='edit'){
                        url = '/inform/editInform';
                        params.id = id;
                    }
                    Ext.Ajax.request({
                        params: params,
                        url: url,
                        method: 'POST',
                        sync: true,
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            if(informAddFormView.operate=='add'){
                                var posteds = [];
                                if(typeof postedUserData != 'undefined'){
                                    var postedUserDatas = postedUserData.split(',');
                                    var postedUsersArr = postedUserDatas[1].split('∪');
                                    for(var i=0;i<postedUsersArr.length;i++){
                                        if(postedUsersArr[i]!=''){
                                            posteds.push(postedUsersArr[i]);
                                        }
                                    }
                                }
                                if(typeof postedUsergroupData != 'undefined'){
                                    var postedUsergroupDatas = postedUsergroupData.split(',');
                                    var postedUsergroupsArr = postedUsergroupDatas[1].split('∪');
                                    for(var i=0;i<postedUsergroupsArr.length;i++){
                                        if(postedUsergroupsArr[i]!=''){
                                            posteds.push(postedUsergroupsArr[i]);
                                        }
                                    }
                                }
                                if(posteds[0]==""){
                                    posteds = null;
                                }
                                var postparams = {
                                    id: respText.data,
                                    posteds:posteds,
                                    flag:''
                                };
                                Ext.Ajax.request({
                                    params: postparams,
                                    url: '/inform/postedInform',
                                    method: 'POST',
                                    sync: true,
                                    success: function (resp) {
                                        console.log('111')
                                    }
                                });
                            }
                            XD.msg(respText.msg);
                            window.cardobj.grid.notResetInitGrid();
                            view.findParentByType('informAddFormView').close();
                        },
                        failure : function() {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'informAddFormView button[itemId=informAddClose]':{
                click:function(view){
                    view.findParentByType('informAddFormView').close();
                }
            },

            'organTreeView':{
                select: this.userfilter
            },

            'informGridView button[itemId=informStick]': {
                click: function (view) {
                    var select = view.findParentByType('informGridView').getSelectionModel();
                    var informs = select.getSelection();
                    if (select.getCount() == 0) {
                        XD.msg('至少选择一条数据');
                        return;
                    }
                    var ids = [];
                    for(var i=0;i<informs.length;i++){
                        ids.push(informs[i].get('id'));
                    }
                    var stickWin = Ext.create('Inform.view.StickView',{informIds:ids,informGrid:view.up('informGridView')});
                    stickWin.show();
                }
            },

            'stickView button[itemId=stickSubmit]':{
                click:function (view) {
                    var form = view.findParentByType('stickView').down('form');
                    Ext.Ajax.request({
                        params: {ids:view.up('stickView').informIds,level:form.down('combobox').value},
                        url: '/inform/informStick',
                        method: 'POST',
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            XD.msg(respText.msg);
                            view.up('stickView').informGrid.notResetInitGrid();
                            view.up('stickView').close();
                        },
                        failure: function() {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'stickView button[itemId=stickClose]':{
                click:function (view) {
                    view.up('stickView').close();
                }
            },

            'informGridView button[itemId=cancelStick]': {
                click: function (view) {
                    var select = view.findParentByType('informGridView').getSelectionModel();
                    var informs = select.getSelection();
                    if (select.getCount() < 1) {
                        XD.msg('至少选择一条数据');
                        return;
                    }
                    var ids = [];
                    for(var i=0;i<informs.length;i++){
                        ids.push(informs[i].get('id'));
                    }
                    Ext.Ajax.request({
                        params: {'ids':ids },
                        url: '/inform/cancelStick',
                        method: 'POST',
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            XD.msg(respText.msg);
                            view.findParentByType('informGridView').getStore().reload();
                        },
                        failure: function() {
                            XD.msg('操作失败');
                        }
                    });
                }
            }
        });
    },

    userfilter:function(treemodel, record){
        // 查找到已选择的用户id
    	var postedSelectView = treemodel.view.findParentByType('postedSelectView');
        var nodeUserSelectStore = postedSelectView.items.items[1].lastValue;
        var userid = [];
        for (var i = 0; i < nodeUserSelectStore.length; i++) {
        	userid.push(nodeUserSelectStore[i]);
        }
        var organid = record.data.fnid;
        var itemselector = postedSelectView.down('itemselector');
        var userstore = itemselector.getStore();
        userstore.reload({params:{organid:organid}});
        // 重新加载已选用户数据
		var value = itemselector.value;
		var storeInfo = itemselector.toField.getStore();
		storeInfo.proxy.url = '/inform/updatePosteds';
		storeInfo.proxy.extraParams = {flag: itemselector.up('postedSelectView').flag, organid: organid, userid: userid};
		storeInfo.reload();
		storeInfo.hideValue = value;
    }
});