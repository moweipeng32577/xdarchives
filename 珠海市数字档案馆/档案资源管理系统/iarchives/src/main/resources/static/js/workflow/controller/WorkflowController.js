/**
 * Created by xd on 2017/10/21.
 */
var selectView,title,nodeid,SxselectView,Sxtitle,Sxnodeid;
Ext.define('Workflow.controller.WorkflowController', {
    extend: 'Ext.app.Controller',
    views: [
    	'WorkflowView','WorkflowTreeView','WorkflowSxTreeView','WorkflowPromptView','WorkflowSxPromptView','WorkflowGridView','WorkflowRightView',
    	'NodeAddFormView','NodeUserSelectView','NodeQxSelectView','OrganTreeView','NodeSequenceView',
        'NodeSequenceGridView','NodeSetApproveScopeView'
    ],//加载view
    stores: [
    	'WorkflowTreeStore','WorkflowSxTreeStore','WorkflowGridStore','NodeUserSelectStore','NodeQxSelectStore','OrganTreeStore','NodeSequenceStore'
    ],//加载store
    models: [
    	'WorkflowTreeModel','WorkflowGridModel','WorkflowSelectModel','NodeQxSelectModel','OrganTreeModel'
    ],//加载model
    init: function () {
        var ifShowRightPanel=false;
        var ifSxShowRightPanel=false;
        this.control({
            'workflowTreeView': {
                select: function (treemodel, record) {
                    if (record.get('leaf')) {
                        var workflowView = treemodel.view.findParentByType('workflowView');
                        var workflowPromptView = workflowView.down('[itemId=workflowPromptViewId]');
                        if (!ifShowRightPanel) {
                            workflowPromptView.removeAll();
                            var items = {
                                xtype: 'workflowRightView'
                            };
                            workflowPromptView.add(items);
                            ifShowRightPanel = true;
                        }
                        var workflowGridView = workflowPromptView.down('[itemId=workflowGridViewID]');
                        workflowGridView.setTitle("当前位置：" + record.get('text'));
                        var treeview = treemodel.view;
                        window.wworkflowGridView = workflowGridView;
                        window.wtreeView = treeview;
                        workflowGridView.initGrid({work_id:record.get('id')});
                        title = record.get('text');
                        if(record.get('text')=='公车预约审批'||record.get('text')=='场地预约审批'||record.get('text')=='采集移交审核'){
                            workflowGridView.down('[itemId=sendmsgId]').hide();
                        }else{
                            workflowGridView.down('[itemId=sendmsgId]').show();
                        }
                        if(window.parent.realname=="系统管理员"){

                        }else {
                            workflowGridView.down('[itemId=nodeAdd]').hide();
                            workflowGridView.down('[itemId=nodeEdit]').hide();
                            workflowGridView.down('[itemId=nodeDel]').hide();
                            workflowGridView.down('[itemId=nodeUser]').hide();
                            workflowGridView.down('[itemId=nodeQx]').hide();
                            workflowGridView.down('[itemId=nodeSortq]').hide();
                            workflowGridView.down('[itemId=approveScope]').hide();
                            workflowGridView.down('[itemId=urging]').hide();
                            workflowGridView.down('[itemId=sendmsgId]').hide();
                        }
                        Ext.Ajax.request({
                            url: '/workflow/findByWorkId',
                            method: 'GET',
                            params: {xtType:window.xtType,workid:record.get('id')},
                            success: function (resp) {
                                var respDate = Ext.decode(resp.responseText).data;
                                workflowGridView.down('[itemId=urging]').setValue(respDate.urgingstate=="1"?true:false);
                                workflowGridView.down('[itemId=sendmsgId]').setValue(respDate.sendmsgstate=="1"?true:false);
                            }
                        });
                    }
                }
            },
            'workflowSxTreeView': {
                select: function (treemodel, record) {
                    if (record.get('leaf')) {
                        var workflowView = treemodel.view.findParentByType('workflowView');
                        var workflowPromptView = workflowView.down('[itemId=workflowSxPromptViewId]');
                        if (!ifSxShowRightPanel) {
                            workflowPromptView.removeAll();
                            var items = {
                                xtype: 'workflowRightView'
                            };
                            workflowPromptView.add(items);
                            ifSxShowRightPanel = true;
                        }
                        var workflowGridView = workflowPromptView.down('[itemId=workflowGridViewID]');
                        workflowGridView.setTitle("当前位置：" + record.get('text'));
                        var treeview = treemodel.view;
                        window.SxwworkflowGridView = workflowGridView;
                        window.SxwtreeView = treeview;
                        workflowGridView.initGrid({xtType:window.xtType,work_id:record.get('id')});
                        Sxtitle = record.get('text');
                        if(record.get('text')=='公车预约审批'||record.get('text')=='场地预约审批'||record.get('text')=='采集移交审核'){
                            workflowGridView.down('[itemId=sendmsgId]').hide();
                        }else{
                            workflowGridView.down('[itemId=sendmsgId]').show();
                        }
                        Ext.Ajax.request({
                            url: '/workflow/findByWorkId',
                            method: 'GET',
                            params: {xtType:window.xtType,workid:record.get('id')},
                            success: function (resp) {
                                var respDate = Ext.decode(resp.responseText).data;
                                workflowGridView.down('[itemId=urging]').setValue(respDate.urgingstate=="1"?true:false);
                                workflowGridView.down('[itemId=sendmsgId]').setValue(respDate.sendmsgstate=="1"?true:false);
                            }
                        });
                    }
                }
            },
            'workflowView':{
                tabchange:function(view){
                    if(view.activeTab.title == '档案系统'){
                        window.xtType='档案系统';
                        if(window.wtreeView){//重新加载表单
                            var reportgrid=view.down('[itemId=workflowGridViewID]');
                            reportgrid.initGrid({xtType:window.xtType,work_id:window.wtreeView.getSelectionModel().getSelected().items[0].get('id')});
                        }

                    }else if(view.activeTab.title == '声像系统'){
                        window.xtType='声像系统';
                        if(window.SxwtreeView){
                            var reportgrid=view.down('[itemId=workflowGridViewID]');
                            reportgrid.initGrid({xtType:window.xtType,work_id:window.SxwtreeView.getSelectionModel().getSelected().items[0].get('id')});
                        }
                    }
                }
            },
            'workflowGridView button[itemId="nodeAdd"]':{
                click:function(){
                   Ext.create("Workflow.view.NodeAddFormView").show();
                }
            },

            //增加，修改节点
            'nodeAddFormView button[itemId="nodeAddSubmit"]':{
                click:function(view){
                    var form = view.findParentByType('nodeAddFormView').down('form');
                    var URL = '/workflow/workflowAdd/workflowAddSubmit';
                    var data = form.getValues();
                    var a = data['orders'];
                    if(view.findParentByType('nodeAddFormView').title=='修改节点'){
                        URL = '/workflow/workflowEdit/workflowEditSubmit';
                    }
                    if(view.findParentByType('nodeAddFormView').title=='修改节点'){
                        if(data['text']==''||data['desci']==''){
                            XD.msg('有必填项未填写');
                            return;
                        }
                    }else {
                        if(data['text']==''||data['desci']==''||data['orders']==''){
                            XD.msg('有必填项未填写');
                            return;
                        }
                    }
                    var workflowGridView;
                    var treeid;
                    if(window.xtType=='声像系统'){
                        workflowGridView=window.SxwworkflowGridView;
                        treeid = window.SxwtreeView.getSelectionModel().getSelected().items[0].get('id');
                    }else{
                        workflowGridView=window.wworkflowGridView;
                        treeid = window.wtreeView.getSelectionModel().getSelected().items[0].get('id')
                    }
                    form.submit({
                        waitTitle : '提示',// 标题
                        waitMsg : '正在提交数据请稍后...',// 提示信息
                        url : URL,
                        method : 'POST',
                        params : { // 此处可以添加额外参数
                            treeid : treeid,
                            text : data['text'],
                            desci : data['desci'],
                            xtType:window.xtType,
                            sortsequence : a
                        },
                        success : function(form, action) {
                            var respText = Ext.decode(action.response.responseText);
                            if (respText.success == true) {
                                XD.msg(respText.msg);
                                view.findParentByType('nodeAddFormView').close();//添加成功后关闭窗口
                                workflowGridView.notResetInitGrid();
                            } else {
                                XD.msg(respText.msg);
                            }
                        },
                        failure : function() {
                            XD.msg('操作失败，请选择需要修改的审批流程');
                        }
                    });
                }
            },

            //节点调序
            'workflowGridView button[itemId="nodeSortq"]':{
                click:function (view) {
                    var nodeGridView=view.findParentByType('workflowGridView');
                    var select=nodeGridView.getSelectionModel();
                    if(!select.hasSelection()){
                        XD.msg('请选择操作记录');
                        return;
                    }else{
                        var gridselections=select.getSelection();//被选中的数据
                        var array=[];
                        for(var i = 0; i<gridselections.length; i++){
                            if(gridselections[i].get("text") == "结束" || gridselections[i].get("text") == "启动"){
                                XD.msg("开始和结束不能调序");
                                return;
                            }
                            array.push(gridselections[i].get("orders"));
                        }
                    }
                    var sequenceNode = Ext.create('Ext.window.Window',{
                        width:'60%',
                        height:'75%',
                        model:true,
                        title:'节点调序',
                        closeAction:'hide',
                        layout:'fit',
                        items:[{
                            xtype:'nodeSequenceView' //调序视图
                        }]
                    });
                    sequenceNode.show();
                    window.sequenceNode=sequenceNode;
                    var view=sequenceNode.down('nodeSequenceView').down('nodeSequenceGridView');
                    var workflowGridView;
                    var work_id;
                    if(window.xtType=='声像系统'){
                        workflowGridView=window.SxwworkflowGridView;
                        work_id=window.SxwtreeView.getSelectionModel().getSelected().items[0].get('id')
                    }else{
                        workflowGridView=window.wworkflowGridView;
                        work_id=window.wtreeView.getSelectionModel().getSelected().items[0].get('id')
                    }
                    view.initGrid({xtType:window.xtType,work_id:work_id,sequence:array});
                }
            },

            //上调
            'nodeSequenceView button[itemId="up"]':{
                click:function(view){
                    var grid = view.findParentByType('nodeSequenceView').down('nodeSequenceGridView');
                    var record = grid.selModel.getSelection();//当前选择的数据
                    if(record.length < 1){
                        XD.msg('请选择一条需要上调的数据');
                        return;
                    }else if(record.length > 1){
                        XD.msg('只能选择一条数据进行操作');
                        return;
                    }
                    var count = grid.getStore().getTotalCount();//获取条目总数
                    var recordall = grid.getStore().getRange(0,count);//获取条目数组
                    var currentcount = 0;
                    for(var i=0; i<count;i++){
                        if(record[0] == recordall[i])
                            currentcount = i;
                    }
                    if(currentcount == 0){
                        XD.msg('当前选择的是第一条数据无法进行上调操作');
                        return;
                    }
                    var array = [];
                    for(var i = 0; i < recordall.length; i++){
                        array.push(recordall[i].get("orders"));
                    }
                    var workflowGridView;
                    var work_id;
                    if(window.xtType=='声像系统'){
                        workflowGridView=window.SxwworkflowGridView;
                        work_id=window.SxwtreeView.getSelectionModel().getSelected().items[0].get('id');
                    }else{
                        workflowGridView=window.wworkflowGridView;
                        work_id=window.wtreeView.getSelectionModel().getSelected().items[0].get('id');
                    }
                    Ext.Ajax.request({
                        method: 'post',
                        url: '/workflow/findNodeBySortsequence',
                        params: {
                            sortSequences:array,      //调序选中的节点
                            currentcount:currentcount,//上调节点的序号
                            operate:'up',
                            xtType:window.xtType,
                            work_id:work_id
                        },
                        success: function (response) {
                            grid.getStore().reload();
                        },
                        failure: function (response) {
                            XD.msg('操作失败');
                        }
                    });
                    workflowGridView.initGrid({xtType:window.xtType,work_id:work_id});
                }
            },

            //下调
            'nodeSequenceView button[itemId="down"]':{
                click:function(view){
                    var grid = view.findParentByType('nodeSequenceView').down('nodeSequenceGridView');
                    var record = grid.selModel.getSelection();//当前选择的数据
                    if(record.length < 1){
                        XD.msg('请选择一条需要上调的数据');
                        return;
                    }else if(record.length > 1){
                        XD.msg('只能选择一条数据进行操作');
                        return;
                    }
                    var count = grid.getStore().getTotalCount();
                    var recordall = grid.getStore().getRange(0,count);
                    var currentcount = 0;
                    for(var i=0; i<count;i++){
                        if(record[0] == recordall[i])
                            currentcount = i;
                    }
                    if(currentcount == count-1){
                        XD.msg('当前选择的是最后一条数据无法进行下调操作');
                        return;
                    }
                    var array = [];
                    for(var i = 0; i < recordall.length; i++){
                        array.push(recordall[i].get("orders"));
                    }
                    var workflowGridView;
                    var work_id;
                    if(window.xtType=='声像系统'){
                        workflowGridView=window.SxwworkflowGridView;
                        work_id=window.SxwtreeView.getSelectionModel().getSelected().items[0].get('id');
                    }else{
                        workflowGridView=window.wworkflowGridView;
                        work_id=window.wtreeView.getSelectionModel().getSelected().items[0].get('id');
                    }
                    Ext.Ajax.request({
                        method: 'post',
                        url: '/workflow/findNodeBySortsequence',
                        params: {
                            sortSequences:array,
                            currentcount:currentcount,//下调节点的序号
                            operate:'down',
                            xtType:window.xtType,
                            work_id:work_id
                        },
                        success: function (response) {
                            grid.getStore().reload();
                        },
                        failure: function (response) {
                            XD.msg('操作失败');
                        }
                    });
                    workflowGridView.initGrid({xtType:window.xtType,work_id:work_id});
                }
            },

            //节点调完序后返回
            'nodeSequenceView button[itemId="back"]': {
                click: function (view) {
                    //nodegridView.getStore().reload();
                    window.sequenceNode.close();
                }
            },

            'nodeUserSelectView button[itemId="nodeSelectSubmit"]':{
                click:function(view){
                    var selectView = view.findParentByType('nodeUserSelectView');
                    if(selectView.down('itemselector').getValue().length==0){
                        XD.msg('至少选择一个用户');
                        return;
                    }
                    var workflowGridView;
                    if(window.xtType=='声像系统'){
                        workflowGridView=window.SxwworkflowGridView;
                    }else{
                        workflowGridView=window.wworkflowGridView;
                    }
                    var select = workflowGridView.getSelectionModel();
                    var node = select.getSelected().items;
                    Ext.Ajax.request({
                        params: {
                            xtType:window.xtType,
                        	nodeid: node[0].get('id')
                        },
                        url: '/workflow/setUserNode',
                        method: 'POST',
                        sync: true,
                        timeout:XD.timeout,
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            if (respText.success == true) {
                                XD.msg(respText.msg);
                                workflowGridView.notResetInitGrid();
                                view.findParentByType('nodeUserSelectView').close();
                            }else{
                                XD.msg(respText.msg);
                            }
                        },
                        failure: function() {
                            XD.msg('操作失败');
                        }
                    });

                }
            },

            'nodeUserSelectView button[itemId="nodeSelectClose"]':{
                click:function(view){
                    view.findParentByType('nodeUserSelectView').close();
                }
            },

            'nodeQxSelectView button[itemId="nodeQxSelectSubmit"]':{
                click:function(view){
                    var selectView = view.findParentByType('nodeQxSelectView');
                    if(selectView.down('itemselector').getValue().length==0){
                        XD.msg('至少选择一个权限');
                        return;
                    }
                    var workflowGridView;
                    if(window.xtType=='声像系统'){
                        workflowGridView=window.SxwworkflowGridView;
                    }else{
                        workflowGridView=window.wworkflowGridView;
                    }
                    var select = workflowGridView.getSelectionModel();
                    var node = select.getSelected().items;

                    Ext.Ajax.request({
                        params: {xtType:window.xtType,nodeid: node[0].get('id'),qxs:selectView.down('itemselector').getValue()},
                        url: '/workflow/setUserNodeQx',
                        method: 'POST',
                        sync: true,
                        timeout:XD.timeout,
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            if (respText.success == true) {
                                XD.msg(respText.msg);
                                view.findParentByType('nodeQxSelectView').close();
                                workflowGridView.notResetInitGrid();
                            }else{
                                XD.msg(respText.msg);
                            }

                        },
                        failure: function() {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'nodeQxSelectView button[itemId="nodeQxSelectClose"]':{
                click:function(view){
                    var selectView = view.findParentByType('nodeQxSelectView');
                    selectView.close();
                }
            },

            'nodeAddFormView button[itemId="nodeAddClose"]':{
                click:function(view){
                    view.findParentByType('nodeAddFormView').close();
                }
            },

            'workflowGridView button[itemId="nodeEdit"]':{
                click:function(view){
                    var workflowGridView;
                    if(window.xtType=='声像系统'){
                        workflowGridView=window.SxwworkflowGridView;
                    }else{
                        workflowGridView=window.wworkflowGridView;
                    }
                    var select = workflowGridView.getSelectionModel();
                    if (select.getSelected().length!=1) {
                        XD.msg('请选择一条数据');
                        return;
                    }

                    var node = select.getSelected().items;
                    if(node[0].get('text')=='启动'||node[0].get('text')=='结束'){
                        XD.msg('此节点不能修改');
                        return;
                    }
                    var form = Ext.create("Workflow.view.NodeAddFormView",{title:'修改节点'});
                    var sortNode=form.down('[itemId=nodeSort]');
                    sortNode.hide();
                    form.show();

                    form.down('form').load({
                        url: '/workflow/nodeEdit?nodeid='+node[0].get('id')+'&&xtType='+window.xtType,
                        //waitMsg: '请稍后......',
                        success : function(form, action) {
                            var data=action.result.data;
                        },
                        failure: function() {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'workflowGridView button[itemId="nodeDel"]':{
                click:function(){
                    var workflowGridView;
                    if(window.xtType=='声像系统'){
                        workflowGridView=window.SxwworkflowGridView;
                    }else{
                        workflowGridView=window.wworkflowGridView;
                    }
                    var select = workflowGridView.getSelectionModel();
                    if (select.getSelected().length == 0) {
                        XD.msg('请选择数据');
                        return;
                    }
                    if (select.getSelected().length != 1) {
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    var node = select.getSelected().items;
                    if(node[0].get('text')=='启动'||node[0].get('text')=='结束'){
                        XD.msg('此节点不能删除');
                        return;
                    }

                    XD.confirm('是否确定删除',function(){
                        Ext.Ajax.request({
                            params: {xtType:window.xtType,nodeid: node[0].get('id')},
                            url: '/workflow/nodeDel',
                            method: 'POST',
                            sync: true,
                            success: function (resp) {
                                var respText = Ext.decode(resp.responseText);
                                if (respText.success == true) {
                                    var selectCount = select.getSelected().length;
                                    XD.msg(respText.msg);
                                    workflowGridView.delReload(selectCount);

                                }else{
                                    XD.msg(respText.msg);
                                }
                            },
                            failure: function() {
                                XD.msg('操作失败');
                            }
                        });
                    },this);
                }
            },
            'workflowGridView button[itemId="nodeUser"]':{
                click:function(){
                    var workflowGridView;
                    if(window.xtType=='声像系统'){
                        workflowGridView=window.SxwworkflowGridView;
                    }else{
                        workflowGridView=window.wworkflowGridView;
                    }
                    var select = workflowGridView.getSelectionModel();
                    if (select.getSelected().length!=1) {
                        XD.msg('只能选择一条数据');
                        return;
                    }

                    var node = select.getSelected().items;
                    if(node[0].get('text')=='启动'||node[0].get('text')=='结束'){
                        XD.msg('此节点不能设置');
                        return;
                    }
                    nodeid = node[0].get('id')
					Ext.MessageBox.wait('正在处理请稍后...', '提示');
                    Ext.Ajax.request({
                        params: {
                            xtType:window.xtType,
                        	id: nodeid
                        },
                        url: '/workflow/getNodeUser',
                        method: 'POST',
                        timeout:XD.timeout,
                        sync: true,
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            if (respText.success == true) {
                                var selectItem = Ext.create("Workflow.view.NodeUserSelectView");
                                selectItem.down('itemselector').getStore().load({
                                    callback:function(){
                                        selectItem.down('itemselector').setValue(respText.data);
                                    }
                                });
                                selectItem.show();
                                Ext.MessageBox.hide();
                            }else{
                                XD.msg(respText.msg);
                                Ext.MessageBox.hide();
                            }
                        },
                        failure: function() {
                            XD.msg('操作失败');
                            Ext.MessageBox.hide();
                        }
                    });
                }
            },

            'workflowGridView button[itemId="nodeQx"]':{
                click:function(){
                    var workflowGridView;
                    if(window.xtType=='声像系统'){
                        workflowGridView=window.SxwworkflowGridView;
                    }else{
                        workflowGridView=window.wworkflowGridView;
                    }
                    var select = workflowGridView.getSelectionModel();
                    if (select.getSelected().length!=1) {
                        XD.msg('请选择一条数据');
                        return;
                    }
                    var node = select.getSelected().items;
                    if(node[0].get('text')=='结束'){
                        XD.msg('此节点不能设置');
                        return;
                    }
                    Ext.Ajax.request({
                        params: {xtType:window.xtType,id:node[0].get('id')},
                        url: '/workflow/getNodeUserQx',
                        method: 'POST',
                        sync: true,
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            if (respText.success == true) {
                                var params = {nodeid:node[0].get('id')};
                                var selectItem = Ext.create("Workflow.view.NodeQxSelectView");
                                Ext.apply(selectItem.down('itemselector').getStore().proxy.extraParams, params);

                                selectItem.down('itemselector').getStore().load({
                                    callback:function(){
                                        selectItem.down('itemselector').setValue(respText.data);
                                    }
                                });
                                selectItem.show();
                            }else{
                                XD.msg(respText.msg);
                            }
                        },
                        failure: function() {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'organTreeView':{
                select: this.userfilter
            },

            'workflowGridView button[itemId="approveScope"]':{  //获取审批范围
                click:function(){
                    var workflowGridView;
                    if(window.xtType=='声像系统'){
                        workflowGridView=window.SxwworkflowGridView;
                    }else{
                        workflowGridView=window.wworkflowGridView;
                    }
                    var select = workflowGridView.getSelectionModel();
                    if (select.getSelected().length!=1) {
                        XD.msg('请选择一条数据');
                        return;
                    }
                    var node = select.getSelected().items;
                    if(node[0].get('text')=='启动'||node[0].get('text')=='结束'){
                        XD.msg('此节点不能设置');
                        return;
                    }
                    Ext.Ajax.request({
                        params: {xtType:window.xtType,id:node[0].get('id')},
                        url: '/workflow/getNodeApproveScope',
                        method: 'POST',
                        sync: true,
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            var nodeSetApproveScopeView = Ext.create("Workflow.view.NodeSetApproveScopeView");
                            nodeSetApproveScopeView.nodeid = node[0].get('id');
                            var approveScope = nodeSetApproveScopeView.down("[itemId=approveScope]");
                            if(respText.data=="仅本单位"){
                                approveScope.setValue("仅本单位");
                            }else{
                                approveScope.setValue(respText.data);
                            }
                            nodeSetApproveScopeView.show();
                        },
                        failure: function() {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            "nodeSetApproveScopeView button[itemId=setApproveScope]":{    //设置审批范围
                click:function (view) {
                    var nodeSetApproveScopeView = view.findParentByType('nodeSetApproveScopeView');
                    var approveScope = nodeSetApproveScopeView.down("[itemId=approveScope]");
                    Ext.Ajax.request({
                        params: {
                            xtType:window.xtType,
                            id:nodeSetApproveScopeView.nodeid,
                            approveScope:approveScope.getValue()
                        },
                        url: '/workflow/setNodeApproveScope',
                        method: 'POST',
                        sync: true,
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            if(respText.success){
                                XD.msg("设置成功");
                                nodeSetApproveScopeView.close();
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

            "nodeSetApproveScopeView button[itemId=setApproveScopeClose]":{    //设置审批范围 返回
                click:function (view) {
                    view.findParentByType('nodeSetApproveScopeView').close();
                }
            },

            'nodeUserSelectView [itemId=usernameSearchId]':{
                search:function(searchfield){
                    var nodeUserSelectView = searchfield.findParentByType('nodeUserSelectView');
                    var username = searchfield.getValue(); //内容
                    var organtree = nodeUserSelectView.down('organTreeView');
                    var node = organtree.selModel.getSelected().items[0];
                    var organid;
                    if (!node) {
                        organid = '0';
                    }else{
                        organid = node.get('fnid');
                    }
                    var itemselector = nodeUserSelectView.down('itemselector');
                    itemselector.getStore().reload({params:{organid:organid,username:username}});
                }
            }
        });
    },

    userfilter:function(treemodel, record){
    	// 查找到已选择的用户id
        var nodeUserSelectStore = treemodel.view.findParentByType('nodeUserSelectView').items.items[1].lastValue;
        var userid = [];
        for (var i = 0; i < nodeUserSelectStore.length; i++) {
        	userid.push(nodeUserSelectStore[i]);
        }
        var username = treemodel.view.findParentByType('nodeUserSelectView').down('[itemId=usernameSearchId]').getValue();
        var organid = record.data.fnid;
        var itemselector = treemodel.view.findParentByType('nodeUserSelectView').down('itemselector');
		var userstore = itemselector.getStore();
		// 重新加载机构用户数据
		userstore.reload({params:{organid:organid,username:username}});
		var value = itemselector.value;
		var storeInfo = itemselector.toField.getStore();
		// 重新加载已选用户数据
		Ext.Ajax.request({
            params: {
            	organid: organid, 
            	userid: userid,
                xtType:window.xtType,
            	nodeid: nodeid
            },
            url: '/workflow/updateSelectedUser',
            method: 'POST',
            sync: true,
            success: function (resp) {
                var respText = Ext.decode(resp.responseText);
                if (respText.length > 0) {
                	var storeInfo = itemselector.toField.getStore();
                	storeInfo.load({
                        callback:function(){
                            itemselector.setValue(respText);
                        }
                    });
                }
            },
            failure: function() {
                XD.msg('操作失败');
            }
        });
    }
});