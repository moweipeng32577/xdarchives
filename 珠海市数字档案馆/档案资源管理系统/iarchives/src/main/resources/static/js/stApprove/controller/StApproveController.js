/**
 * Created by xd on 2017/10/21.
 */
var dataSourceType;//数据源类型
Ext.define('StApprove.controller.StApproveController', {
     extend: 'Ext.app.Controller',

    views: ['StApproveView','StApproveGridView','StApproveFormView','StAddView','SimpleSearchView'
        ,'SimpleSearchGridView','ElectronicView'],//加载view
    stores: ['StApproveGridStore','NextNodeStore','NextSpmanStore','SimpleSearchGridStore'],//加载store
    models: ['StApproveGridModel','SimpleSearchGridModel'],//加载model
    init: function (view) {
        var isAddPostil = false;//判断是否已经添加过批注
        var flag = false;
        this.control({
            'stApproveFormView':{
                render:function(view){
                    window.wform = view;
                    view.load({
                        url: '/stApprove/getBorrowDocByTaskid',
                        params : {
                            taskid:taskid
                        },
                        success : function() {
                            window.wapprove = window.wform.getValues()['approve'];
                            dataSourceType=window.wform.getComponent('datasourcetypeId').getValue();
                        },
                        failure: function() {
                            XD.msg('表单读取失败');
                        }
                    });
                    var nextNode = view.down('[itemId=nextNodeId]');
                    var nextSpman = view.down('[itemId=nextSpmanId]');
                    nextNode.on('change',function(val){
                        nextSpman.getStore().proxy.extraParams.nodeId = val.value;
                        nextSpman.getStore().load(function(){
                            if(this.getCount() > 0){
                                nextSpman.select(this.getAt(0));
                            }else{
                                nextSpman.setValue(null);
                            }
                        });
                    });
                }
            },

            'stApproveFormView button[itemId=approveAdd]':{
                click:function(){
                   Ext.create('StApprove.view.StAddView').show();
                }
            },
            'stAddView':{
                render:function(field){
                   field.down('[itemId=selectApproveId]').on('change',function(val){
                       field.down('[itemId=approveId]').setValue(val.value);
                   });
                },
                afterrender:function (field) {
                    if(typeof window.wareatext!='undefined'){
                        field.down('[itemId=approveId]').setValue(window.wareatext);
                    }
                }
            },
            'stApproveGridView':{
                afterrender:function (view) {
                    view.initGrid({taskid:taskid});
                    var worktextflag =this.getWorkText();
                    Ext.Ajax.request({
                        params: {
                            taskid:taskid
                        },
                        method: 'POST',
                        url:'/electronApprove/getTaskInfo',
                        success: function(response) {
                            var data = Ext.decode(response.responseText).data;
                            if (typeof(data) != 'undefined' && data.state == '完成' || data.approvetext == '结束') {
                                //当类型为'完成'时,界面只显示查看按钮
                                var buttons = view.down("toolbar").query('button');
                                var tbseparator = view.down("toolbar").query('tbseparator');
                                //隐藏拒绝权限按钮
                                hideToolbarBtnTbsByItemId('reject',buttons,tbseparator);
                                //隐藏借出权限按钮
                                hideToolbarBtnTbsByItemId('lend',buttons,tbseparator);
                                //隐藏添加按钮
                                hideToolbarBtnTbsByItemId('addId',buttons,tbseparator);
                                //隐藏删除按钮
                                hideToolbarBtnTbsByItemId('deleteBtnID',buttons,tbseparator);
                                var lendbtn = view.down('[itemId=lend]');
                                lendbtn.hide();
                                var formView = view.findParentByType('stApproveView').down('stApproveFormView');
                                var store = formView.down("toolbar").items.items;
                                for (var i = 0; i < store.length; i++) {
                                    //隐藏下一环节&审批人&添加批示&完成&退回
                                    if (i < store.length - 1) {
                                        store[i].hide();
                                    }
                                }
                            }
                            if(worktextflag){
                                var buttons = view.down("toolbar").query('button');
                                var tbseparator = view.down("toolbar").query('tbseparator');
                                //隐藏拒绝权限按钮
                                hideToolbarBtnTbsByItemId('reject',buttons,tbseparator);
                                //隐藏借出权限按钮
                                hideToolbarBtnTbsByItemId('lend',buttons,tbseparator);
                                view.down('[itemId=addId]').show();
                                view.down('[itemId=deleteBtnID]').show();
                            }else{
                                var buttons = view.down("toolbar").query('button');
                                var tbseparator = view.down("toolbar").query('tbseparator');
                                //隐藏添加按钮
                                hideToolbarBtnTbsByItemId('addId',buttons,tbseparator);
                                //隐藏删除按钮
                                hideToolbarBtnTbsByItemId('deleteBtnID',buttons,tbseparator);
                            }
                        }
                    });
                }
            },
            'stApproveGridView button[itemId=reject]':{
                click:function(view){
                    var select = view.findParentByType('stApproveGridView') .getSelectionModel();
                    var datas = select.getSelection();
                    var dataids = [];
                    for(var i=0;i<datas.length;i++){
                        dataids.push(datas[i].get('entryid'));
                    }

                    if (select.getCount()<1) {
                        XD.msg('至少选择一条数据');
                        return;
                    }

                    Ext.Ajax.request({
                        params: {
                            dataids:dataids,
                            taskid:taskid,
                            lyqx:'拒绝'
                        },
                        url: '/stApprove/setlyqx',
                        method: 'POST',
                        sync: true,
                        success: function (resp) {
                            XD.msg('操作成功');
                            var grid = view.findParentByType('stApproveGridView');
                            grid.initGrid({taskid:taskid});
                        },
                        failure : function() {
                            XD.msg('操作失败');
                        }
                    });

                }
            },

            'stApproveGridView button[itemId=look]':{
                click:function(view){
                    var grid = view.findParentByType('stApproveGridView');
                    stApproveGridView =view.findParentByType('stApproveGridView');
                    var record = grid.selModel.getSelection();
                    if(record.length != 1){
                        XD.msg('请选择一条需要查看的数据');
                        return;
                    }
                    var entryids = [];
                    var nodeids = [];
                    for(var i=0;i<record.length;i++){
                        entryids.push(record[i].get('entryid'));
                        nodeids.push(record[i].get('nodeid'));
                    }
                    var entryid = record[0].get("entryid");
                    if(dataSourceType=="soundimage"){
                        var nodename=record[0].get('nodefullname');
                        var mediaFormView = this.getNewMediaFormView(view,'look',nodename,record);
                        var form = mediaFormView.down('[itemId=dynamicform]');
                        var initFormFieldState = this.initFormField(form, 'hide', record[0].get('nodeid'));
                        if(!initFormFieldState){//表单控件加载失败
                            return;
                        }
                        form.operate = 'look';
                        var records = record;
                        form.entryids = entryids;
                        form.nodeids = nodeids;
                        form.selectItem = records;
                        form.entryid = records[0].get('entryid');
                        this.initMediaFormData('look', form, records[0].get('entryid'), records[0]);
                        form.down('[itemId=preBtn]').hide();
                        form.down('[itemId=nextBtn]').hide();
                        mediaFormView.down("[itemId=batchUploadBtn]").hide();
                        mediaFormView.down('[itemId=save]').hide();
                        if(nodename.indexOf('音频') !== -1){
                            mediaFormView.down('[itemId=mediaDetailViewItem]').expand();
                        }
                        form.up('stApproveView').setActiveItem(mediaFormView);
                    }else {
                        var approve =  Ext.create("Ext.window.Window",{
                            width:'100%',
                            height:'100%',
                            plain: true,
                            header: false,
                            border: false,
                            closable: false,
                            frame:false,
                            draggable : false,//禁止拖动
                            resizable : false,//禁止缩放
                            modal:true,
                            closeToolText:'关闭',
                            layout:'fit',
                            items:[{
                                xtype: 'EntryFormView'
                            }]
                        });
                        var form = approve.down('dynamicform');
                        form.nodeid = record[0].get("nodeid");
                        form.operate = 'look';
                        form.entryids = entryids;
                        form.nodeids = nodeids;
                        form.entryid = entryids[0];
                        this.initFormField(form, 'hide', record[0].get('nodeid'));
                        this.initFormData('look', form, entryid);
                        approve.show();
                        window.approves = approve;
                        Ext.on('resize', function (a, b) {
                            window.approves.setPosition(0, 0);
                            window.approves.fitContainer();
                        });
                    }
                }
            },
            'EntryFormView button[itemId=back]':{
                click:function(btn){
                    if (typeof btn.findParentByType('simpleSearchView') === 'undefined') {
                        stApproveGridView.getStore().reload();
                        window.approves.close();
                    } else {
                        this.activeGrid(btn,true);
                    }
                }
            },
            'EntryFormView [itemId=preBtn]':{
                click:this.preHandler
            },
            'EntryFormView [itemId=nextBtn]':{
                click:this.nextHandler
            },

            'stApproveGridView button[itemId=lend]':{
                click:function(view){
                    var select = view.findParentByType('stApproveGridView') .getSelectionModel();
                    var datas = select.getSelection();
                    var dataids = [];
                    for(var i=0;i<datas.length;i++){
                        dataids.push(datas[i].get('entryid'));
                    }
                    if (select.getCount()<1) {
                        XD.msg('至少选择一条数据');
                        return;
                    }
                    Ext.Ajax.request({
                        params: {
                            dataids:dataids,
                            taskid:taskid,
                            lyqx:'借出'
                        },
                        url: '/stApprove/setlyqx',
                        method: 'POST',
                        sync: true,
                        success: function (resp) {
                            XD.msg('操作成功');
                            var grid = view.findParentByType('stApproveGridView');
                            grid.initGrid({taskid:taskid });
                        },
                        failure : function() {
                            XD.msg('操作失败');
                        }
                    });

                }
            },

            'stAddView button[itemId=stAddSubmit]':{
                click:function(view){
                    var areaText = view.up('stAddView').down('[itemId=approveId]').getValue();
                    if(''==areaText){
                        XD.msg('请输入批示');
                         return;
                    }

                    if(isAddPostil){
                        XD.msg('您已添加过批示');
                        return;
                    }
                    window.wareatext=areaText;
                    //var text = window.wform.getValues()['approve']+'\n'+flowsText+':'+areaText;
                    var curdate=getNowFormatDate();
                    var rname = window.parent.realname ? window.parent.realname : window.parent.parent.realname;
                    var text = '意见：'+areaText+'\n'+flowsText+'：' + rname +'\n'+curdate;
                    if(window.wapprove!=''){
                        text = window.wapprove+'\n\n'+text;
                    }

                    window.wform.getComponent('approveId').setValue(text);
                    view.findParentByType('stAddView').close();
                    isAddPostil = true;
                }
            },

            'stAddView button[itemId=stAddClose]':{
                click:function(view){
                    view.findParentByType('stAddView').close();
                }
            },
            'stApproveFormView button[itemId=stApproveFormSubmit]':{
                click:function(view){
                    var textArea = window.wform.getComponent('approveId').getValue();
                    var borrowtyts = window.wform.getComponent('borrowtytsId').getValue();
                    var nextNode = view.up('stApproveFormView').down('[itemId=nextNodeId]').getValue();
                    var nextSpman = view.up('stApproveFormView').down('[itemId=nextSpmanId]').getValue();
                    var sendMsg = view.up('stApproveFormView').down("[itemId=sendmsgId]").getValue();
                    if(borrowtyts==''||borrowtyts==null||isNaN(borrowtyts)||parseInt(borrowtyts)<=0||borrowtyts.indexOf('.')>-1){
                        XD.msg('非法同意天数');
                        return ;
                    }

                    if(nextNode==null){
                        XD.msg('下一环节不能为空');
                       return ;
                    }

                    if(view.up('stApproveFormView').down('[itemId=nextNodeId]').rawValue!='结束'
                        &&(view.up('stApproveFormView').down('[itemId=nextSpmanId]').rawValue=='')){
                        XD.msg('下一环节审批人不能为空');
                        return ;
                    }
                    var grid = view.findParentByType('stApproveView').down('stApproveGridView');
                    var count =0;
                    var gridcount = grid.getStore().getCount();
                    for(var i =0;i<gridcount;i++){
                        if(grid.getStore().getAt(i).data.lyqx=='拒绝'){
                            count++;
                        }
                    }

                    if(''==textArea||!isAddPostil){
                        var curdate=getNowFormatDate();
                        var rname = window.parent.realname ? window.parent.realname : window.parent.parent.realname;
                        if(count==gridcount){
                            textArea = '意见：不通过\n'+flowsText+'：' + rname +'\n'+curdate;
                        }else{
                            textArea = '意见：通过\n'+flowsText+'：' + rname +'\n'+curdate;
                        }
                        if(window.wapprove!=''){
                            textArea = window.wapprove+'\n\n'+textArea;
                        }
                    }

                    Ext.Ajax.request({
                        params: {
                            textArea:textArea,
                            nextNode:nextNode,
                            nextSpman:nextSpman,
                            taskid:taskid,
                            nodeId:nodeId,
                            borrowtyts:borrowtyts,
                            sendMsg:sendMsg
                        },
                        url: '/stApprove/approveSubmit',
                        method: 'POST',
                        sync: true,
                        success: function (resp) {
                            var data = Ext.decode(resp.responseText);
                            if(data.success){
                                Ext.defer(function () {
                                    if(iflag=='1'){
                                        parent.wgridView.notResetInitGrid({state:'待处理',type:'实体查档'});
                                        parent.approve.close();
                                    }else{
                                        parent.closeObj.close(parent.layer.getFrameIndex(window.name));
                                    }
                                },1000);
                            }

                             XD.msg(data.msg);

                        },
                        failure : function() {
                            XD.msg('审批失败');
                        }
                    });
                }
            },
            'stApproveFormView button[itemId=stApproveFormZz]':{
                click:function(view){
//                    var textArea = window.wform.getComponent('approveId').value;
//                    if(textArea==''){
//                        XD.msg('请填写退回批示');
//                        return;
//                    }
                    XD.confirm('是否确定退回',function () {
                    	var textArea = window.wform.getComponent('approveId').value;
	                    var curdate = getNowFormatDate();
	                    var rname = window.parent.realname ? window.parent.realname : window.parent.parent.realname;
            			if (textArea == '') {
            				textArea += '意见：不通过\n'+flowsText+'：' + rname +'\n'+curdate;
                		} else if (textArea.indexOf('驳回') < 0) {
                			textArea += '\n\n意见：不通过\n'+flowsText+'：' + rname +'\n'+curdate;
                		}
                        Ext.Ajax.request({
                            params: {
                                taskid:taskid,
                                nodeId:nodeId,
                                textarea:textArea
                            },
                            url: '/stApprove/returnBorrow',
                            method: 'POST',
                            sync: true,
                            success: function (resp) {
                                XD.msg('审批完成');
                                Ext.defer(function () {
                                    if(iflag=='1'){
                                        parent.wgridView.notResetInitGrid({state:'待处理',type:'实体查档'});
                                        parent.approve.close();
                                    }else{
                                        parent.closeObj.close(parent.layer.getFrameIndex(window.name));
                                    }
                                },1000);
                            },
                            failure : function() {
                                XD.msg('操作失败');
                            }
                        });
                    },this);
                }
            },
            'stApproveFormView button[itemId=stApproveFormFn]':{
                click:function(view){
                    XD.confirm('是否确定查无此档？',function () {
                        var textArea = window.wform.getComponent('approveId').value;
                        var curdate = getNowFormatDate();
                        var rname = window.parent.realname ? window.parent.realname : window.parent.parent.realname;
                        if (textArea == '') {
                            textArea += '意见：查无此档\n'+flowsText+'：' + rname +'\n'+curdate;
                        } else if (textArea.indexOf('查无此档') < 0) {
                            textArea += '\n\n意见：查无此档\n'+flowsText+'：' + rname +'\n'+curdate;
                        }
                        Ext.Ajax.request({
                            params: {
                                taskid:taskid,
                                nodeId:nodeId,
                                textarea:textArea
                            },
                            url: '/stApprove/nofindBorrow',
                            method: 'POST',
                            sync: true,
                            success: function (resp) {
                                XD.msg('审批完成');
                                Ext.defer(function () {
                                    if(iflag=='1'){
                                        parent.wgridView.notResetInitGrid({state:'待处理',type:'实体查档'});
                                        parent.approve.close();
                                    }else{
                                        parent.closeObj.close(parent.layer.getFrameIndex(window.name));
                                    }
                                },1000);
                            },
                            failure : function() {
                                XD.msg('操作失败');
                            }
                        });
                    },this);
                }
            },
            'stApproveFormView button[itemId=stApproveFormClose]': {
                click: function () {
                    if (iflag == '1') {
                        parent.approve.close();
                    } else {
                        parent.closeObj.close(parent.layer.getFrameIndex(window.name));
                    }
                }
            },
            'stApproveGridView button[itemId=addId]': {
                click: function (view) {
                    var upStApproveGridView = view.findParentByType('stApproveGridView');
                    window.leadIn = Ext.create("Ext.window.Window", {
                        width: '100%',
                        height: '100%',
                        title: '办理',
                        modal: true,
                        header: false,
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        closeToolText: '关闭',
                        layout: 'fit',
                        items: [{xtype: 'simpleSearchView'}],
                        upStApproveGridView:upStApproveGridView,
                        dataSourceType:dataSourceType
                    }).show();
                    window.leadIn.down('simpleSearchGridView').acrossSelections = [];
                    Ext.on('resize', function (a, b) {
                        window.leadIn.setPosition(0, 0);
                        window.leadIn.fitContainer();
                    });
                }
            },
            'simpleSearchGridView [itemId=simpleSearchBackId]': {
                click: function (btn) {
                    if (window.leadIn != null) {
                        window.leadIn.setVisible(false);
                    }
                }
            },
            'simpleSearchView [itemId=simpleSearchSearchfieldId]': {
                search: function (searchfield) {
                    //获取检索框的值
                    var simpleSearchSearchView = searchfield.findParentByType('panel');
                    var condition = simpleSearchSearchView.down('[itemId=simpleSearchSearchComboId]').getValue(); //字段
                    var operator = 'like';//操作符
                    var content = searchfield.getValue(); //内容
                    //检索数据
                    //如果有勾选在结果中检索，则添加检索条件，如果没有勾选，则重置检索条件
                    var grid = simpleSearchSearchView.findParentByType('panel').down('simpleSearchGridView');
                    var gridstore = grid.getStore();
                    //加载列表数据
                    var searchcondition = condition;
                    var searchoperator = operator;
                    var searchcontent = content;
                    var inresult = simpleSearchSearchView.down('[itemId=inresult]').getValue();
                    if (inresult) {
                        var params = gridstore.getProxy().extraParams;
                        if (typeof(params.condition) != 'undefined') {
                            searchcondition = [params.condition, condition].join(XD.splitChar);
                            searchoperator = [params.operator, operator].join(XD.splitChar);
                            searchcontent = [params.content, content].join(XD.splitChar);
                        }
                    }

                    grid.dataParams = {
                        condition: searchcondition,
                        operator: searchoperator,
                        content: searchcontent
                    };

                    //检索数据前,修改column的renderer，将检索的内容进行标红
                    Ext.Array.each(grid.getColumns(), function () {
                        var column = this;
                        if (column.dataIndex == condition) {
                            column.renderer = function (value) {
                                var contentData = content.split(' ');//切割以空格分隔的多个关键词
                                var reg = new RegExp(contentData.join('|'), 'g');
                                return value.replace(reg, function (match) {
                                    return '<span style="color:red">' + match + '</span>';
                                });
                            }
                        }
                    });
                    grid.notResetInitGrid({datasoure:dataSourceType,condition: searchcondition,operator: searchoperator,content: searchcontent});
                    grid.parentXtype = 'simpleSearchView';
                    grid.formXtype = 'EntryFormView';
                }
            },
            'mediaFormView [itemId=mediaBack]': {//查看返回
                click: this.lookBack
            },
            'simpleSearchGridView [itemId=simpleSearchShowId]': {
                click: function (btn) {
                    var simpleSearchGridView = btn.findParentByType('simpleSearchGridView');
                    SimpleSearchGridView =  btn.findParentByType('simpleSearchGridView');
                    var record = simpleSearchGridView.getSelectionModel().getSelection();
                    if (record.length == 0) {
                        XD.msg('请至少选择一条需要查看的数据');
                        return;
                    }
                    var entryid = record[0].get('entryid');
                    var form = simpleSearchGridView.up('simpleSearchView').down('EntryFormView').down('dynamicform');
                    var entryids = [];
                    var nodeids = [];
                    for(var i=0;i<record.length;i++){
                        entryids.push(record[i].get('entryid'));
                        nodeids.push(record[i].get('nodeid'));
                    }
                    if(dataSourceType=="soundimage"){
                        var nodename=record[0].get('nodefullname');
                        var mediaFormView = this.getNewMediaFormView(btn,'look',nodename,record);
                        var form = mediaFormView.down('[itemId=dynamicform]');
                        var initFormFieldState = this.initFormField(form, 'hide', record[0].get('nodeid'));
                        if(!initFormFieldState){//表单控件加载失败
                            return;
                        }
                        form.operate = 'look';
                        var records = record;
                        form.entryids = entryids;
                        form.nodeids = nodeids;
                        form.selectItem = records;
                        form.entryid = records[0].get('entryid');
                        this.initMediaFormData('look', form, records[0].get('entryid'), records[0]);
                        mediaFormView.down("[itemId=batchUploadBtn]").hide();
                        mediaFormView.down('[itemId=save]').hide();
                        if(nodename.indexOf('音频') !== -1){
                            mediaFormView.down('[itemId=mediaDetailViewItem]').expand();
                        }
                        form.up('simpleSearchView').setActiveItem(mediaFormView);
                    }else {
                        form.operate = 'look';
                        form.entryids = entryids;
                        form.entryid = entryids[0];
                        form.nodeids = nodeids;
                        flag = true;
                        this.initFormField(form, 'hide', record[0].get('nodeid'));
                        this.initFormData('look', form, entryid,flag);
                    }
                }
            },
            'simpleSearchGridView button[itemId=searchleadinId]': {
                click: function (view) {
                    var simpleSearchGridView = view.findParentByType('simpleSearchGridView');
                    var select = simpleSearchGridView.getSelectionModel();
                    if (!select.hasSelection()) {
                        XD.msg('请选择需要导入的数据');
                        return;
                    }

                    var datas = select.getSelection();
                    var array = [];
                    for (var i = 0; i < datas.length; i++) {
                        array[i] = datas[i].get('entryid');
                    }
                    Ext.Ajax.request({
                        params: {
                            dataids: array,
                            taskid:taskid,
                            type:dataSourceType
                        },
                        url: '/stApprove/searchAdd',
                        method: 'POST',
                        sync: true,
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            if (respText.success) {
                                window.leadIn.upStApproveGridView.getStore().reload();
                                var tip = '成功导入' + (array.length - parseInt(respText.msg)) + '条！';
                                if (respText.msg !== '0') {
                                    tip += '重复' + respText.msg + '条！'
                                }
                                XD.msg(tip);
                            }else{
                                XD.msg(respText.msg);
                            }
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'stApproveGridView button[itemId=deleteBtnID]': {
                click: function (views) {
                    var stApproveGridView = views.findParentByType('stApproveGridView');
                    var select = stApproveGridView.getSelectionModel();
                    var records = select.getSelection();
                    if (records.length === 0) {
                        XD.msg('请至少选择一条需要删除的数据！');
                        return;
                    }
                    XD.confirm('确定要删除这' + records.length + '条数据吗', function () {
                        var array = [];
                        for (var i = 0; i < records.length; i++) {
                            array[i] = records[i].get('entryid');
                        }
                        Ext.Ajax.request({
                            params: {
                                dataids: array,
                                taskid: taskid
                            },
                            url: '/stApprove/deleteEntries',
                            method: 'POST',
                            sync: true,
                            success: function (resp) {
                                if (Ext.decode(resp.responseText).success) {
                                    XD.msg('删除成功');
                                    stApproveGridView.delReload(records.length);
                                } else {
                                    XD.msg('删除失败！');
                                    stApproveGridView.delReload(records.length);
                                }
                            },
                            failure: function (resp, opts) {
                                XD.msg('操作中断！');
                            }
                        });
                    }, this);
                }
            },
            'stApproveFormView button[itemId=electronId]': {
                click: function (view) {
                    window.leadIn = Ext.create("Ext.window.Window", {
                        width: '100%',
                        height: '100%',
                        title: '查看附件',
                        modal: true,
                        header: false,
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        closeToolText: '关闭',
                        layout: 'fit',
                        items: [{xtype: 'electronicPro'}]
                    });
                    window.wmedia = [];
                    var borrowcode = this.getBorrowcode();
                    window.wform = view.findParentByType('stApproveFormView');
                    window.leadIn.down('electronicPro').initData(borrowcode);
                    window.leadIn.show();
                }
            }
        });
    },
    //获取新的mediaFormView
    getNewMediaFormView: function (btn, operate, mediaType,record) {
         var formAndGrid;
         if(btn.up("stApproveGridView")){
             formAndGrid= btn.up("stApproveGridView")
         }else{
             formAndGrid= btn.up("simpleSearchGridView")
         }
        var entryid = '';
        var across=record[0];
        if (typeof across !== 'undefined' && (operate === 'look' || operate === 'modify')) {
            entryid = across.get('entryid');
        }
        var accept, uploadLabel;
        var dynamicRegion = 'west', header = true, col_spli = true, collapsed = false, collapsible = false;
        if (mediaType.indexOf('照片') !== -1) {
            accept = {
                title: 'Images',
                extensions: 'jpeg,jpg,png,bmp,gif,tiff,tif,crw,cr2,nef,raf,raw,kdc,mrw,nef,orf,dng,ptx,pef,arw,x3f,rw2',
                mimeTypes: 'image/*'
            };
            uploadLabel = '上传照片';
        } else if (mediaType.indexOf('视频') !== -1) {
            accept = {
                title: 'Videos',
                extensions: 'mp4,avi',
                mimeTypes: 'video/*'
            };
            uploadLabel = '上传视频';
        } else if (mediaType.indexOf('音频') !== -1) {
            accept = {
                title: 'Audio',
                extensions: 'mp3',
                mimeTypes: 'audio/*'
            };
            uploadLabel = '上传音频';
            dynamicRegion = 'south';
            header = false;
            col_spli = false;
            collapsed = true;
            collapsible = true;
        }
        formAndGrid.remove(formAndGrid.down('[itemId=amediaFormView]'));//删除原有的mediaFormView，确保干净
        var dynamicFromItem = {
            region: dynamicRegion,
            title: '条目',
            iconCls: 'x-tab-entry-icon',
            itemId: 'dynamicform',
            xtype: 'dynamicform',
            calurl: '/management/getCalValue',
            items: [{
                xtype: 'hidden',
                name: 'entryid'
            }],
            width: '70%',
            flex: 4,
            collapsible: col_spli,
            split: col_spli
        };
        var detailViewItem = {
            region: 'center',
            header: header,
            title: uploadLabel.substr(2, 2),
            iconCls: 'x-tab-electronic-icon',
            itemId: 'mediaDetailViewItem',
            entrytype: '',
            layout: 'fit',
            xtype: 'panel',
            items: [{
                itemId: 'mediaHtml',
                html: '<div id="mediaDiv" class="pw-view" style="background:white"></div>'
            }],
            flex: 1,
            collapsed: collapsed,
            collapsible: collapsible
        };
        formAndGrid.add({
            itemId: 'amediaFormView',
            xtype: 'mediaFormView',
            entryid: entryid,
            flag: false,//默认不用刷新
            acceptMedia: accept,
            uploadLabel: uploadLabel,
            mediaType: mediaType,
            items: [dynamicFromItem, detailViewItem]
        });
        return formAndGrid.down('[itemId=amediaFormView]');
    },

    initMediaFormData: function (operate, form, entryid, record) {
        var nullvalue = new Ext.data.Model();
        var mediaFormView = form.up('mediaFormView');
        var fields = form.getForm().getFields().items;
        var prebtn = mediaFormView.down('[itemId=MpreBtn]');
        var nextbtn = mediaFormView.down('[itemId=MnextBtn]');
        var count;
        if (operate == 'modify' || operate == 'look') {
            for (var i = 0; i < form.entryids.length; i++) {
                if (form.entryids[i] == entryid) {
                    count = i + 1;
                    break;
                }
            }
            var total = form.entryids.length;
            var totaltext = form.up("mediaFormView").down('[itemId=MtotalText]');
            totaltext.setText('当前共有  ' + total + '  条，');
            var nowtext = form.up("mediaFormView").down('[itemId=MnowText]');
            nowtext.setText('当前记录是第  ' + count + '  条');
            totaltext.show();
            nowtext.show();
            prebtn.hide();
            nextbtn.hide();
        }
        for (var i = 0; i < fields.length; i++) {
            if (fields[i].value && typeof(fields[i].value) == 'string' && fields[i].value.indexOf('label') > -1) {
                continue;
            }
            if (fields[i].xtype == 'combobox') {
                fields[i].originalValue = null;
            }
            nullvalue.set(fields[i].name, null);
        }
        form.loadRecord(nullvalue);
        if (operate != 'look' && operate != 'lookfile') {

        } else {
            Ext.each(fields, function (item) {
                item.setReadOnly(true);
            });
        }
        var urls= '/management/entries/' + entryid+"?xtType="+"声像系统";
        Ext.Ajax.request({
            method: 'GET',
            scope: this,
            url: urls,
            success: function (response) {
                var entry = Ext.decode(response.responseText);
                var data = Ext.decode(response.responseText);
                if (data.organ) {
                    entry.organ = data.organ;//机构
                }
                var fieldCode = form.getRangeDateForCode();//字段编号，用于特殊的自定义字段(范围型日期)
                if (fieldCode != null) {
                    //动态解析数据库日期范围数据并加载至两个datefield中
                    form.initDaterangeContent(entry);
                }
                form.loadRecord({
                    getData: function () {
                        return entry;
                    }
                });
                form.entryid = entry.entryid;
                if (operate == 'look' || operate == 'modify') {
                    Ext.Ajax.request({
                        method: 'POST',
                        params: {entryid: entry.entryid},
                        url: '/electronic/getSxElectronicByEntryid',
                        async: false,
                        success: function (response) {
                            var eleRecord = Ext.decode(response.responseText).data;
                            mediaFormView.currentMD5 = eleRecord.md5;
                            if (record.get('background') === '') {
                                mediaFormView.compressing = true;
                                var videoHtml = '<img src="/img/defaultMedia/videoloading.gif" style="position:absolute;top:0;right:0;left:0;bottom:0;margin:auto;width:350px;height:240px"/>';
                                document.getElementById('mediaDiv').innerHTML = videoHtml;
                            } else if (mediaFormView.mediaType.indexOf('照片') !== -1) {
                                if (typeof(mediaFormView.photoView) == 'undefined') {
                                    mediaFormView.photoView = new PhotoView({
                                        eleid: 'mediaDiv',
                                        src: '/electronic/loadSpecialMedia?entryid=' + entryid+"&fileType=photo",
                                        initWidth: '90%'
                                    });
                                } else {
                                    Ext.apply(mediaFormView.uploader.options, {
                                        server: '/electronic/serelectronics/' + mediaFormView.entrytype + "/" + form.entryid
                                    });
                                    mediaFormView.photoView.changeImg('/electronic/loadSpecialMedia?entryid=' + entryid);
                                }
                            } else if (mediaFormView.mediaType.indexOf('视频') !== -1) {
                                mediaFormView.compressing = false;
                                var videoHtml = '<a href="/electronic/loadSpecialMedia?entryid=' + entryid + '&fileType=video" style="position:absolute;top:0;right:0;left:0;bottom:0;margin:auto;width:520px;height:320px" id="player"></a>';
                                document.getElementById('mediaDiv').innerHTML = videoHtml;
                                flowplayer("player", "../js/flowplayerFlash/flowplayer.swf", {
                                    plugins: {
                                        controls: {
                                            height: 30,
                                            tooltips: {
                                                buttons: true,
                                                play: '播放',
                                                fullscreen: '全屏',
                                                fullscreenExit: '退出全屏',
                                                pause: '暂停',
                                                mute: '静音',
                                                unmute: '取消静音'
                                            }
                                        }
                                    },
                                    canvas: {
                                        backgroundColor: '#000',
                                        backgroundGradient: [0, 0]//无渐变色
                                    },
                                    clip: {
                                        autoPlay: false,
                                        autoBuffering: true
                                    },
                                    onStart: function (clip) {
                                        animate(this, clip, {
                                            height: 320,
                                            width: 520
                                        })
                                    },
                                    onFullscreen: function (clip) {
                                        setTimeout(function () {
                                            animate(this, clip, {
                                                height: screen.height,
                                                width: screen.width
                                            }, clip);
                                        }, 1000);
                                    }
                                });
                            } else if (mediaFormView.mediaType.indexOf('音频') !== -1) {
                                mediaFormView.compressing = false;
                                var videoHtml = '<div class="audio-box"></div>';
                                document.getElementById('mediaDiv').innerHTML = videoHtml;
                                Ext.Ajax.request({
                                    params: {entryid: entryid},
                                    url: '/electronic/getBrowseByEntryid',
                                    success: function (response) {
                                        var responseText = Ext.decode(response.responseText);
                                        if (responseText.data !== null) {
                                            var name = responseText.data.filename;
                                            name = name.substring(0, name.lastIndexOf('.'));
                                            var audioFn = audioPlay({
                                                song: [{
                                                    title: name,
                                                    src: responseText.data.filepath + "/" + responseText.data.filename,
                                                    cover: '../../img/defaultMedia/default_audio.png'
                                                }],
                                                error: function (msg) {
                                                    XD.msg(msg.meg);
                                                    console.log(msg)
                                                }
                                            });
                                            if (audioFn) {
                                                audioFn.loadFile(false);
                                            }
                                        }
                                    },
                                    failure: function () {
                                        XD.msg('获取浏览音频中断');
                                    }
                                });
                            }
                        },
                        failure: function () {
                            XD.msg('操作失败！');
                        }
                    });
                }
            }
        });
    },
    lookBack: function (btn) {
         if(btn.up('simpleSearchView')){
             btn.up('simpleSearchView').setActiveItem(btn.up('simpleSearchView').down('[itemId=gridview]'));
         }else {
             btn.up('stApproveView').setActiveItem(btn.up('stApproveView').down('[itemId=gridview]'));
         }
        if (window.play) {
            window.play(false);//音频停止播放
        }
        btn.up('mediaFormView').destroy();//销毁，防止视频在后台继续播放
    },
    initFormField:function(form, operate, nodeid){
//        if(form.nodeid!=nodeid){
        form.nodeid = nodeid;//用左侧树节点的id初始化form的nodeid参数
        form.removeAll();//移除form中的所有表单控件
        var field = {
            xtype: 'hidden',
            name: 'entryid'
        };
        form.add(field);
        var formField;
        if(dataSourceType=='soundimage'){  //声像系统
            formField = form.getSxFormField();//根据节点id查询表单字段
            form.xtType="声像系统";
        }else{
            formField = form.getFormField();//根据节点id查询表单字段
            form.xtType="档案系统";
        }
        if(formField.length==0){
            XD.msg('请检查模板设置信息是否正确');
            return;
        }
        form.templates = formField;
        form.initField(formField,operate);//重新动态添加表单控件
//        }
        return '加载表单控件成功';
    },

    getCurrentStApproveform:function(btn) {
        return btn.up('EntryFormView');
    },

//点击上一条
    preHandler:function(btn){
        var currentStApproveform = this.getCurrentStApproveform(btn);
        var form = currentStApproveform.down('dynamicform');
        this.refreshFormData(form, 'pre');
    },

//点击下一条
    nextHandler:function(btn){
        var currentStApproveform = this.getCurrentStApproveform(btn);
        var form = currentStApproveform.down('dynamicform');
        this.refreshFormData(form, 'next');
    },

    refreshFormData:function(form, type){
        var entryids = form.entryids;
        var nodeids = form.nodeids;
        var currentEntryid = form.entryid;
        var entryid;
        var nodeid;
        for(var i=0;i<entryids.length;i++){
            if(type == 'pre' && entryids[i] == currentEntryid){
                if(i==0){
                    i=entryids.length;
                }
                entryid = entryids[i-1];
                nodeid = nodeids[i-1];
                break;
            }else if(type == 'next' && entryids[i] == currentEntryid){
                if(i==entryids.length-1){
                    i=-1;
                }
                entryid = entryids[i+1];
                nodeid = nodeids[i+1];
                break;
            }
        }
        form.entryid = entryid;
        if(form.operate != 'undefined'){
            this.initFormField(form, 'hide', nodeid);//上下条时切换模板
            this.initFormData(form.operate, form, entryid);
            return;
        }
        this.initFormField(form, 'hide', nodeid);
        this.initFormData('look', form, entryid);
    },

    initFormData:function (operate, form, entryid,flag) {
        var formview = form.up('EntryFormView');
        var nullvalue = new Ext.data.Model();
        var fields = form.getForm().getFields().items;
        if(operate == 'look') {
            for (var i = 0; i < form.entryids.length; i++) {
                if (form.entryids[i] == entryid) {
                    count = i + 1;
                    break;
                }
            }
            var total = form.entryids.length;
            var totaltext = form.down('[itemId=totalText]');
            totaltext.setText('当前共有  ' + total + '  条，');
            var nowtext = form.down('[itemId=nowText]');
            nowtext.setText('当前记录是第  ' + count + '  条');

            Ext.each(fields,function (item) {
                item.setReadOnly(true);
            });
        }else{
            Ext.each(fields,function (item) {
                if(!item.freadOnly){
                    item.setReadOnly(false);
                }
            });
        }
        for(var i = 0; i < fields.length; i++){
            nullvalue.set(fields[i].name, null);
        }
        form.loadRecord(nullvalue);
        var etips = formview.down('[itemId=etips]');
        etips.show();
        form.reset();
        if(flag){
            this.activeForm(form);
        }
        Ext.Ajax.request({
            method:'GET',
            scope:this,
            url:'/management/entries/'+entryid,
            success:function(response){
                var entry = Ext.decode(response.responseText);
                form.loadRecord({getData:function(){return entry;}});
                //字段编号，用于特殊的自定义字段(范围型日期)
                var fieldCode = form.getRangeDateForCode();
                if(fieldCode!=null){
                    //动态解析数据库日期范围数据并加载至两个datefield中
                    form.initDaterangeContent(entry);
                }
                //初始化原文数据
                var eleview = formview.down('electronic');
                eleview.initData(entryid);
                var solidview = formview.down('solid');
                solidview.initData(entryid);
                // var longview = formview.down('long');
                // longview.initData(entryid);
//                form.formStateChange(operate);
                form.fileLabelStateChange(eleview,operate);
                form.fileLabelStateChange(solidview,operate);
                // form.fileLabelStateChange(longview,operate);
            }
        });
    },
    getWorkText:function () {
        var worktext;
        Ext.Ajax.request({
            url: '/stApprove/getWorkText',
            async:false,
            params:{
                taskid:taskid
            },
            success: function (response) {
                worktext = Ext.decode(response.responseText).data;
            }
        });
        return worktext;
    },
    //获取简单检索应用视图
    findSearchView: function (btn) {
        return btn.up('simpleSearchView');
    },

    //获取简单检索查看动态表单界面视图
    findFormView: function (btn) {
        return this.findSearchView(btn).down('EntryFormView');
    },
    //获取简单检索列表界面视图
    findSearchGridView: function (btn) {
        return this.findSearchView(btn).down('simpleSearchGridView');
    },
    //切换到简单检索查看动态表单界面视图
    activeForm: function (btn) {
        var view = this.findSearchView(btn);
        var formview = this.findFormView(btn);
        view.setActiveItem(formview);
        formview.items.get(0).enable();
        formview.setActiveTab(0);
        return formview;
    },
    //切换到简单检索列表界面视图
    activeGrid: function (btn, flag) {
        var view = this.findSearchView(btn);
        var grid = this.findSearchGridView(btn);
        view.setActiveItem(view.down('[itemId=gridview]'));
        if (document.getElementById('mediaFrame')) {
            document.getElementById('mediaFrame').setAttribute('src', '');
        }
        if (document.getElementById('solidFrame')) {
            document.getElementById('solidFrame').setAttribute('src', '');
        }
        // if (document.getElementById('longFrame')) {
        //     document.getElementById('longFrame').setAttribute('src', '');
        // }
        if (flag) {//根据参数确定是否需要刷新数据
            grid.initGrid();
        }
    },
    getSubmitstate:function () {
        var submitstate;
        Ext.Ajax.request({
            url: '/electronApprove/getSubmitstate',
            async:false,
            params:{
                taskid:taskid
            },
            success: function (response) {
                submitstate = Ext.decode(response.responseText).data;
            }
        });
        return submitstate;
    },
    getBorrowcode:function () {
        var borrowcode;
        Ext.Ajax.request({
            url: '/electronApprove/getBorrowcode',
            async:false,
            params:{
                taskid:taskid
            },
            success: function (response) {
                borrowcode = Ext.decode(response.responseText).data;
            }
        });
        return borrowcode;
    }
});
//itemId为要隐藏的按钮functioncode
function hideToolbarBtnTbsByItemId(itemId,btns,tbs) {
    for (var num in btns) {
        if (itemId == btns[num].itemId) {
            btns[num].hide();
            if (num > 1) {
                tbs[num-1].hide();
            } else {
                tbs[num].hide();
            }
        }
    }
}

function getNowFormatDate() {
    var date = new Date();
    var seperator1 = "";
    var seperator2 = ":";
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    var hour= date.getHours();
    var minutes=date.getMinutes();
    var second = date.getSeconds();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    if (hour >= 0 && hour <= 9) {
        hour = "0" + hour;
    }
    if (minutes >= 0 && minutes <= 9) {
        minutes = "0" + minutes;
    }
    if (second >= 0 && second <= 9) {
        second = "0" + second;
    }
    var currentdate = date.getFullYear() + '年' + month + '月' + strDate + '日 '+hour+":"+minutes+":"+second;
    // + " " + date.getHours() + seperator2 + date.getMinutes();
    // + seperator2 + date.getSeconds();
    return currentdate;
}