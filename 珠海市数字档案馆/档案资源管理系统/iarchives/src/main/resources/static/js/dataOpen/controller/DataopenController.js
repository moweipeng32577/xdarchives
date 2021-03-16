/**
 * Created by tanly on 2017/12/1 0001.
 */
var dataopengridInfo,num;
var exportState = "";
var entryids = "";
var NodeIdf = "";
var dataopenfieldcod="";//用户选择的字段
Ext.define('Dataopen.controller.DataopenController', {
    extend: 'Ext.app.Controller',

    views: [
        'DataopenView','DataopenTabView',
        'DataopenGridView','DataopenFormView',
        'DataopenDealGridView','DataopenSendFormView',
        'DataopenSendGridView','DataopenSendNextView',
        'DataopenOpenedGridView','DataopenDontOpenGridView',
        'DataopenDocView','DataopenShowDocGridView',
        'DataopenDocEntryGridView','DataopenDocumentInfoView','DealDetailsGridView',
        'DataopenGroupSetView',
        'DataopenMessageView','DataopenDocFormView','DataopenDocFormView'
    ],
    stores: [
        'DataopenTreeStore', 'DataopenDealGridStore',
        'DataopenSendGridStore', 'DataopenNodeStore',
        'DataopenNodeuserStore','OpendocGridStore','DealDetailsGridStore',
        'DataopenGroupSetStore','ApproveOrganStore'
    ],
    models: [
        'DataopenTreeModel','DataopenDealGridModel',
        'OpendocGridModel','DealDetailsGridModel',
        'DataopenGroupSetModel'
    ],
    init: function () {
        var count = 0;
        var treeNode;
        this.control({
            'dataopengrid': {
                eleview: this.activeEleForm
            },
            'dataopenOpenedGridView': {
                eleview: this.activeEleForm
            },
            'dataopenDontOpenGridView': {
                eleview: this.activeEleForm
            },
            'dataopengrid [itemId=look]': {
                click: this.lookHandler
            },
            'dataopenOpenedGridView [itemId=look]': {
                click: this.lookHandler
            },
            'dataopenDontOpenGridView [itemId=look]': {
                click: this.lookHandler
            },
            'dataopengrid [itemId=Excel]': {//导出--st
                click: this.chooseFieldExportExcel
            },
            'dataopengrid [itemId=Xml]': {
                click: this.chooseFieldExportXml
            },
            'dataopengrid [itemId=ExcleAndElectronic]': {
                click: this.chooseFieldExportExcelAndFile
            },
            'dataopengrid [itemId=XmlAndElectronic]': {//-导出--en
                click: this.chooseFieldExportXmlAndFile
            },
            'dataopenDontOpenGridView [itemId=Excel]': {//不开放导出--st
                click: this.bkfchooseFieldExportExcel
            },
            'dataopenDontOpenGridView [itemId=Xml]': {
                click: this.bkfchooseFieldExportXml
            },
            'dataopenDontOpenGridView [itemId=ExcleAndElectronic]': {
                click: this.bkfchooseFieldExportExcelAndFile
            },
            'dataopenDontOpenGridView [itemId=XmlAndElectronic]': {//不开放导出--en
                click: this.bkfchooseFieldExportXmlAndFile
            },
            'dataopenOpenedGridView [itemId=Excel]': {//已开放导出--st
                click: this.ykfchooseFieldExportExcel
            },
            'dataopenOpenedGridView [itemId=Xml]': {
                click: this.ykfchooseFieldExportXml
            },
            'dataopenOpenedGridView [itemId=ExcleAndElectronic]': {
                click: this.ykfchooseFieldExportExcelAndFile
            },
            'dataopenOpenedGridView [itemId=XmlAndElectronic]': {//已开放导出--en
                click: this.ykfchooseFieldExportXmlAndFile
            },
            'dataopenOpenedGridView [itemId=release]': {//发布到政务网
                click: this.ykfrelease
            },
            'DataopenMessage button[itemId="cancelExport"]': {
                click: function (view) {
                    view.findParentByType('DataopenMessage').close();
                }
            },'dataopenGroupSetView button[itemId="addAllOrNotAll"]': {
                    click:function(view){
                        var itemSelector = view.findParentByType('dataopenGroupSetView').down('itemselector');
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
            'DataopenMessage button[itemId="SaveExport"]': {//导出
                click: function (view) {
                    var DataopenMessageView = view.up('DataopenMessage');
                    var fileName = DataopenMessageView.down('[itemId=userFileName]').getValue();
                    var zipPassword = DataopenMessageView.down('[itemId=zipPassword]').getValue();
                    var b = DataopenMessageView.down('[itemId=addZipKey]').checked
                    var form = DataopenMessageView.down('[itemId=form]')

                    if (fileName!=null&&fileName!="请输入..."&&fileName!="") {
                        var pattern = new RegExp("[/:*?\"<>|]");
                        if (pattern.test(fileName) || fileName.indexOf('\\') > -1) {
                            XD.msg("文件名称不能包含下列任何字符：\\/:*?\"<>|");
                            return;
                        }
                        if(zipPassword==""&&b){
                            XD.msg("zip压缩密码不能为空");
                            return;
                        }

                        Ext.MessageBox.wait('正在处理请稍后...')
                        Ext.Ajax.request({
                            method: 'post',
                            url:'/export/chooseFieldExport',
                            timeout:XD.timeout,
                            scope: this,
                            async:true,
                            params:{
                                nodeid: NodeIdf,
                                fileName: fileName,
                                exportState: exportState,
                                zipPassword: zipPassword,
                                entryids: entryids,
                                userFieldCode: dataopenfieldcod
                            },
                            success:function(res){
                                var obj = Ext.decode(res.responseText).data;
                                if(obj.fileSizeMsg=="NO"){
                                    XD.msg('原文总大小超出限制');
                                    Ext.MessageBox.hide()
                                    return;
                                }
                                if(obj.entrySizeMsg=="NO"){
                                    if(exportState=="XmlAndFile"||exportState=="ExcelAndFile"){
                                        XD.msg('条目数超出限制，一次支持支导出10万含原文的条目！');
                                    }
                                    if(exportState=="Excel"||exportState=="Xml"){
                                        XD.msg('条目数超出限制，一次只支持导出50w的条目！');
                                    }
                                    Ext.MessageBox.hide()
                                    return;
                                }
                                window.location.href="/export/downloadZipFile?fpath="+encodeURIComponent(obj.filePath)
                                Ext.MessageBox.hide()
                                XD.msg('文件生成成功，正在准备下载');
                                DataopenMessageView.close()
                            },
                            failure:function(){
                                Ext.MessageBox.hide()
                                XD.msg('文件生成失败');
                            }
                        });
                    } else {
                        XD.msg("文件名不能为空")
                    }
                }
            },
            'dataopenGroupSetView button[itemId="close"]': {
                click: function (view) {
                    view.findParentByType('dataopenGroupSetView').close();
                }
            },
            'dataopenGroupSetView button[itemId="save"]': {
                click: this.chooseSave
            },
            'dataopengrid [itemId=open]': {
                click: this.openHandler
            },
            'dataopengrid [itemId=add]': {
                click: this.addHandler
            },
            'dataopengrid [itemId=deal]': {
                click: this.dealHandler
            },
            'dataopengrid [itemId=showopendoc]': {
                click: this.showOpendocHandler
            },
            'dataopenShowDocGridView [itemId=showOpenInfo]': {//查看开放单据列表 查看直接开放单据
            	click:this.showOpenInfoHandler
            },
            'dataopenShowDocGridView [itemId=showEntryDetail]':{//查看开放单据列表 查看详细条目
                click:this.showEntryDetailHandler
            },
            'dataopenShowDocGridView [itemId=showEntryInfo]':{//查看单据详情
                click:this.showEntryInfoHandler
            },
            'dataopenShowDocGridView [itemId=kfDealDetailsId]':{//查看办理详情
                click:this.kfDealDetailsIdHandler
            },
            'dataopenDocumentInfoView [itemId=back]':{
            	click:this.backGridHandler
            },
            'dataopenShowDocGridView [itemId=print]':{//查看开放单据列表 打印
                click:this.printDocHandler
            },
            'dataopenShowDocGridView [itemId=printOpen]':{//查看开放单据列表 打印开放审查登记表
                click:this.printDocHandler
            },
            'dataopenShowDocGridView [itemId=printNotOpen]':{//查看开放单据列表 打印不开放审查登记表
                click:this.printDocHandler
            },
            'dataopenShowDocGridView [itemId=back]':{//查看开放单据列表 返回
                click:function (btn) {
                    btn.up('window').hide();
                }
            },
            'dataopenDocEntryGridView [itemId=back]':{//查看开放单据条目详情列表 返回
                click:function (btn) {
                    this.activeOpendocGrid(btn);
                }
            },
            'dataopenDocEntryGridView [itemId=look]':{//查看开放单据条目详情列表 查看
            	click:function (view, btn) {
                    this.activeLookdocGrid(view, btn);
                }
            },'dataopenShowDocGridView [itemId=urging]': {//催办
                click:this.urging
            },
            'EntryFormView [itemId=preBtn]':{
                click:this.preHandler
            },
            'EntryFormView [itemId=nextBtn]':{
                click:this.nextHandler
            },
            'dataopenFormView [itemId=preBtn]':{
                click:this.preHandler
            },
            'dataopenFormView [itemId=nextBtn]':{
                click:this.nextHandler
            },
            'EntryFormView [itemId=back]': {
                click: this.activeGrid
            },
            'dataopenDealGridView [itemId=back]': {
                click: this.activeGrid
            },
            'dataopenFormView [itemId=back]': {
            	click: this.activeInfoGrid
            },
            'dataopenOpenedGridView [itemId=dontopen]': {
                click: this.dontopenHandler
            },
            'dataopenDontOpenGridView [itemId=cancelban]': {
                click: this.cancelbanHandler
            },
            'dataopenDealGridView [itemId=send]': {
                click: this.sendHandler
            },
            'dataopenDealGridView [itemId=remove]': {
                click: this.removeHandler
            },
            'dataopenTabView': {
                render: function () {
                    window.gridtype = 'dataopengrid';
                },
                tabchange: function (view) {
                    if (view.activeTab.title == '未开放') {
                        window.gridtype = 'dataopengrid';
                    } else if (view.activeTab.title == '已开放') {
                        window.gridtype = 'dataopenOpenedGridView';
                    } else if (view.activeTab.title == '不开放') {
                        window.gridtype = 'dataopenDontOpenGridView';
                    }
                }
            },
            'dataopenSendNextView [itemId=submit]': {
                click: function (btn) {
                    var dataopenview = this.findView(btn);
                    var formview = dataopenview.down('dataopenSendNextView');
                    var nodeuser = formview.getForm().findField('nodeuser').getValue();//环节用户
			        if(nodeuser==null || nodeuser==''){
			            XD.msg('环节用户不能为空');
			            return;
			        }
                    var dataopenSendGridView = dataopenview.down('dataopenSendGridView');
                    var datanodeid = dataopenSendGridView.getStore().data.items[0].get('nodeid');
                    Ext.MessageBox.wait('正在提交中...', '提示');
                    dataopenview.down('dataopenSendFormView').submit({
                        waitTitle: '',
                        url: '/dataopen/sendformSubmit',
                        method: 'post',
                        params: {
                            taskname: dataopenview.down('dataopenSendNextView').items.get('taskItem').getValue(),
                            nodeid: dataopenview.down('dataopenSendNextView').items.get('nodenameItem').getValue(),
                            nodeuserid: dataopenview.down('dataopenSendNextView').items.get('nodeuserItem').getValue(),
                            datanodeid:datanodeid,
                            sendMsg:dataopenview.down('dataopenSendNextView').down('[itemId=sendmsgId]').getValue()
                        },
                        success: function (form, action) {
                            Ext.MessageBox.hide();
                            var respText = Ext.decode(action.response.responseText);
                            XD.msg(respText.msg);
                            dataopenview.down('dataopenDealGridView').getStore().loadPage(1);
                            dataopenview.setActiveItem(dataopenview.down('dataopenDealGridView'));
                        },
                        failure: function () {
                            Ext.MessageBox.hide();
                            XD.msg('提交中断');
                        }
                    });
                }
            },
            'dataopenSendFormView [itemId=back]': {
                click: function (btn) {
                    var dataopenview = this.findView(btn);
                    var sendView = dataopenview.down('dataopenSendFormView');
                    var nextbtn = sendView.down('[itemId=next]');
                    if (nextbtn.hidden) {
                    	this.activeGrid(btn);
                    } else {
                    	dataopenview.setActiveItem(dataopenview.down('dataopenDealGridView'));
                    }
                }
            },
            'dataopenSendNextView': {
                render: function (view) {
                    var spmanOrgan = view.down("[itemId=approveOrgan]");
                    view.items.get('nodenameItem').on('change', function (val) {
                        view.items.get('nodeuserItem').getStore().proxy.extraParams.nodeId = val.value;
                        spmanOrgan.getStore().proxy.extraParams.type = "submit"; //申请时获取审批单位
                        spmanOrgan.getStore().proxy.extraParams.taskid = null;
                        spmanOrgan.getStore().proxy.extraParams.nodeid = val.value;
                        spmanOrgan.getStore().proxy.extraParams.worktext = null;
                        spmanOrgan.getStore().reload(); //刷新审批单位
                    });
                }
            },
            'dataopenSendNextView [itemId=back]': {
                click: function (btn) {
                    var dataopenview = this.findView(btn);
                    dataopenview.setActiveItem(dataopenview.getComponent('sendformview'));
                }
            },
            'dataopenSendFormView [itemId=submit]': {//直接开放提交
            	click: function (btn) {
            		var dataopenview = this.findView(btn);
				    var formview = dataopenview.down('dataopenSendFormView');
			        var doctitle = formview.getForm().findField('doctitle').getValue();//题名
			        var submitter = formview.getForm().findField('submitter').getValue();//送审人
			        var batchnum = formview.getForm().findField('batchnum').getValue();//开放批次号
			        var submitdate = formview.getForm().findField('submitdate').getValue();//送审时间
			        var entrycount = formview.getForm().findField('entrycount').getValue();//条目总数
			        var opentype = formview.getForm().findField('opentype').getValue();//开放类型
			        if(doctitle==null || doctitle==''){
			            XD.msg('题名不能为空');
			            return;
			        }
                	if(submitter==null || submitter==''){
			            XD.msg('送审人不能为空');
			            return;
			        }
			        if(batchnum==null || batchnum==''){
			            XD.msg('开放批次号不能为空');
			            return;
			        }
			        if(submitdate==null || submitdate==''){
			            XD.msg('送审时间不能为空');
			            return;
			        }
			        if(entrycount==null || entrycount==''){
			            XD.msg('条目总数不能为空');
			            return;
			        }
			        if(opentype==null || opentype==''){
			            XD.msg('开放类型不能为空');
			            return;
			        }
                    dataopenview.down('dataopenSendFormView').submit({
                        url: '/dataopen/submitForm',
                        method: 'post',
                        params: {
                            nodeid: window.selectednodeid
                        },
                        success: function () {
                        	dataopenview.down('dataopengrid').getStore().reload();
                        	dataopenview.down('dataopenTabView').down('dataopenOpenedGridView').getStore().reload();//找到已开放视图
                            XD.msg('提交成功');
                        },
                        failure: function () {
                            XD.msg('提交中断');
                        }
                    });
                    this.activeGrid(btn);//回到数据开放主界面
            	}
            },
            'dataopenSendFormView [itemId=next]': {
                click: function (btn) {
                	var dataopenview = this.findView(btn);
				    var formview = dataopenview.down('dataopenSendFormView');
			        var doctitle = formview.getForm().findField('doctitle').getValue();//题名
			        var submitter = formview.getForm().findField('submitter').getValue();//送审人
			        var batchnum = formview.getForm().findField('batchnum').getValue();//开放批次号
			        var submitdate = formview.getForm().findField('submitdate').getValue();//送审时间
			        var entrycount = formview.getForm().findField('entrycount').getValue();//条目总数
			        var opentype = formview.getForm().findField('opentype').getValue();//开放类型
			        if(doctitle==null || doctitle==''){
			            XD.msg('题名不能为空');
			            return;
			        }
                	if(submitter==null || submitter==''){
			            XD.msg('送审人不能为空');
			            return;
			        }
			        if(batchnum==null || batchnum==''){
			            XD.msg('开放批次号不能为空');
			            return;
			        }
			        if(submitdate==null || submitdate==''){
			            XD.msg('送审时间不能为空');
			            return;
			        }
			        if(entrycount==null || entrycount==''){
			            XD.msg('条目总数不能为空');
			            return;
			        }
			        if(opentype==null || opentype==''){
			            XD.msg('开放类型不能为空');
			            return;
			        }
                	Ext.Ajax.request({
	                    params: {batchnum: batchnum},
	                    url: '/dataopen/getBatchnum',
	                    method: 'post',
	                    sync: true,
	                    success: function (resp) {
	                    	var respText = Ext.decode(resp.responseText);
		                    if (respText.success) {
		                    	var name = formview.items.get('submitterItem').getValue();
			                    var doctitle = dataopenview.down('dataopenSendFormView').items.get('doctitleItem').getValue();
			                    var taskname = name + '提交的关于“' + doctitle + '”的开放审批申请';
			                    dataopenview.down('dataopenSendNextView').items.get('taskItem').setValue(taskname);
			                    dataopenview.setActiveItem(dataopenview.getComponent('sendnextview'));
		                    } else {
		                    	XD.msg(respText.msg);
		                    }
	                    },
	                    failure: function () {
	                        XD.msg('操作中断');
	                    }
	                });
                }
            },
            'dataopenView [itemId=treepanelId]': {
            	render: function (view) {
            		view.getRootNode().on('expand', function (node) {
            	        for (var i = 0; i < node.childNodes.length; i++) {
            	            if (node.childNodes[i].raw.text == '已归管理') {//默认打开已归管理第一条节点
            	            	treeNode = node.childNodes[i].raw.id;
                            }
                            if (node.childNodes[i].raw.parentId == treeNode) {//找到已归管理下的所有节点
                            	treeNode = node.childNodes[0].raw.id;
                            	node.getOwnerTree().expandPath(node.childNodes[0].raw.id, "id");
                            	node.getOwnerTree().getSelectionModel().select(node.childNodes[0]);
                            }
            	        }
            	    })
                },
                select: function (treemodel, record) {
                	var dataopenOpenView = treemodel.view.findParentByType('dataopenView');
                    var tabarr = dataopenOpenView.down('[itemId=tabviewID]').items.items;
                    for (var i = 0; i < tabarr.length; i++) {
                        var grid = tabarr[i].items.items[0];
                        grid.initGrid({nodeid: record.get('fnid'), type: '数据开放'});
                    }
                    var openButton = dataopenOpenView.down('dataopengrid').down('[itemId=open]');
                    var tbseparator = dataopenOpenView.down('dataopengrid').down("toolbar").query('tbseparator');
                    if (state == 'true') {
                    	openButton.show();
                    	tbseparator[0].show();
                    } else {
                    	openButton.hide();
                    	tbseparator[0].hide();
                    }
                    window.selectednodeid = record.get('fnid');
                    dataopengridInfo = dataopenOpenView.down('dataopenTabView').down(window.gridtype);
                    //---
                    NodeIdf = record.get('fnid');
                    var demoStore = Ext.getStore('DataopenGroupSetStore');
                    demoStore.proxy.extraParams.fieldNodeid = NodeIdf;
                    //--
                }
            },
            'dataopenDocFormView [itemId=releaseID]': {//发布
                click: this.kfrelease
            }
        });
    },
    urging:function(view){
        var destructionBillGridView = view.findParentByType('dataopenShowDocGridView');
        var select = destructionBillGridView.getSelectionModel();
        if (!select.hasSelection()) {
            XD.msg('请选择一条数据!');
            return;
        }
        var details = select.getSelection();
        if(details.length!=1){
            XD.msg('只支持单条数据催办!');
            return;
        }
        if(details[0].get("state")!="待处理"){
            XD.msg('请选择正确的数据催办!');
            return;
        }
        Ext.MessageBox.wait('正在处理请稍后...');
        Ext.Ajax.request({
            params: {batchnum: details[0].get("batchnum"),sendMsg:destructionBillGridView.down("[itemId=message]").checked},
            url: '/dataopen/manualUrging',
            method: 'POST',
            sync: true,
            success: function (response) {
                Ext.MessageBox.hide();
                var respText = Ext.decode(response.responseText);
                XD.msg(respText.msg);
            },
            failure: function () {
                Ext.MessageBox.hide();
                XD.msg('操作失败');
            }
        });
    },
    dealHandler: function (btn) {
        var view = this.findView(btn);
        var dealGridView = this.findView(btn).down('dataopenDealGridView');
        dealGridView.initGrid();
        view.setActiveItem(dealGridView);
    },
    showOpendocHandler:function (btn) {//查看开放单据
        var tree = this.findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if(!node){
            XD.msg('请选择节点');
            return;
        }
        var showOpendocWin = Ext.create('Ext.window.Window',{
            modal:true,
            width:900,
            height:530,
            title:'查看开放单据',
            layout:'fit',
            closeToolText:'关闭',
            closeAction:'hide',
            items:[{
                xtype: 'dataopenDocView',
                nodeid:window.selectednodeid
            }]
        });
        var showOpendocView = showOpendocWin.down('dataopenDocView');
        var opendocGrid = showOpendocView.down('dataopenShowDocGridView');
        opendocGrid.getStore().setPageSize(XD.pageSize);
        opendocGrid.initGrid({nodeid:showOpendocView.nodeid});
        Ext.Ajax.request({//根据审批id判断是否可以催办
            url: '/dataopen/findByWorkId',
            method: 'GET',
            success: function (resp) {
                var respDate = Ext.decode(resp.responseText).data;
                if(respDate.urgingstate=="1"){
                    showOpendocWin.down('[itemId=urging]').show();
                    showOpendocWin.down('[itemId=message]').show();
                }
            }
        });
        showOpendocWin.show();
    },
    
    showEntryDetailHandler:function (btn) {//查看与单据相关联的条目详细内容
        var showOpendocView = this.findOpendocView(btn);
        var opendocGrid = this.findOpendocGridView(btn);
        var entryGrid = this.findOpendocEntryGridView(btn);
        var record = opendocGrid.getSelectionModel().getSelection();
        if(record.length!=1){
            XD.msg('请选择一条需要查看的单据');
            return;
        }
        var batchnum = record[0].get('batchnum');
        num = record[0].get('batchnum');
        var params = {
            batchnum:batchnum,
            nodeid:showOpendocView.nodeid,//请求模板数据，显示列表
            type:'数据开放',
            info:'详细内容'
        };
        entryGrid.initGrid(params);
        this.activeOpendocEntryGrid(btn);
    },
    
    //查看单据详情
    showEntryInfoHandler:function (btn) {
    	var grid = this.findOpendocGridView(btn);//查找到查看开放单据表单视图
        var record = grid.selModel.getSelection();
        if (record.length == 0) {
            XD.msg('请至少选择一条需要查看的数据');
            return;
        } else if (record.length != 1) {
            XD.msg('查看只能选中一条数据');
            return;
        }
        var dataopenView = dataopengridInfo.up('dataopenTabView').up('dataopenView');
        var dataopenDocumentInfoView = dataopenView.down('dataopenDocumentInfoView');//获取到单据详情视图

        dataopenDocumentInfoView.load({
            url: '/dataopen/getDocumentInfo/' + record[0].id,
            success: function (form, action) {},
            failure: function () {
                XD.msg('操作中断');
            }
        });
        
        var tabview = dataopenView.getComponent('documentInfoView');
        dataopenView.setActiveItem(tabview);
        dataopenDocumentInfoView.infoWindow = grid.up('window');
        dataopenDocumentInfoView.infoWindow.hide();
    },

    //办理详情
    kfDealDetailsIdHandler:function (btn) {
        var grid = this.findOpendocGridView(btn);//查找到查看开放单据表单视图
        var record = grid.selModel.getSelection();
        if (record.length != 1) {
            XD.msg('请选择一条数据');
            return;
        }
        //如果是直接开放的单据,那么提示没有办理详情
		if (record[0].data.approve == '直接开放') {
			XD.msg('直接开放的文件无办理环节');
            return;
		}
        this.showDealDetailsWin(record[0].id);
    },

    //从查看单据返回
    backGridHandler:function (btn) {
    	var view = btn.up('dataopenDocumentInfoView');
        // var showOpendocWin = Ext.create('Ext.window.Window',{
        //     modal:true,
        //     width:900,
        //     height:530,
        //     title:'查看开放单据',
        //     layout:'fit',
        //     closeToolText:'关闭',
        //     closeAction:'hide',
        //     items:[{
        //         xtype: 'dataopenDocView',
        //         nodeid: window.selectednodeid
        //     }]
        // });
        // //首先显示查看开放单据视图
        // var showOpendocView = showOpendocWin.down('dataopenDocView');
        // showOpendocWin.show();
        //
        // var opendocGrid = showOpendocView.down('dataopenShowDocGridView');
        // opendocGrid.initGrid({nodeid:showOpendocView.nodeid});
        view.infoWindow.show();
        //然后加载数据开放视图
        var dataopenView = dataopengridInfo.up('dataopenTabView').up('dataopenView');
        dataopenView.setActiveItem(dataopenView.getComponent('gridview'));
    },

    printDocHandler:function (btn) {//打印开放单据
        var ids = [],batchnum=[];
        var params= {};
        var opendocGrid = this.findOpendocGridView(btn);
        var record = opendocGrid.getSelectionModel().getSelection();
        if(record.length != 1){
            XD.msg('请选择一条需要打印的开放单据');
            return;
        }
        Ext.each(record,function(){
            batchnum.push(this.get('batchnum'));
            ids.push(this.get('id'));
        });

        var reportName='数据开放单据管理';//默认打印数据开放单据管理
        if(btn.itemId=='printOpen'){//打印开放审查登记表
            reportName='开放审批-开放审查登记表';
        }else if('printNotOpen'){//打印不开放审查登记表
            reportName='开放审批-不开放审查登记表';
        }
        var type="初审",approve="通过";
        Ext.Ajax.request({
            params: {opendocid: ids},
            url: '/dataopen/getDealDetails',
            method: 'post',
            sync: false,
            success: function (resp) {
                var respText = Ext.decode(resp.responseText);
                var data=respText.content;
                for(var i=0;i<data.length;i++){
                    if(data[i].node=="复审"){
                        if(data[i].status!="处理中") {
                            type="复审";
                            if (data[i].approve.indexOf("不通过") != -1) {
                                approve = "不通过"
                            }
                        }else{
                            approve = "不通过"
                        }
                    }
                }
                if(reportServer == 'UReport') {
                    params['batchnum'] = batchnum.join(",");
                    params['type'] =type;
                    params['approve'] =approve;
                    XD.UReportPrint(null, reportName, params);
                }else if(reportServer == 'FReport') {
                    XD.FRprint(null, reportName, batchnum.length > 0 ? "'batchnum':'" + batchnum.join(",") + "'" : '');
                }
            }
        });
    },
    
    toSendView: function (btn, grid) {
    	var select = grid.getSelectionModel();
        if (select.getCount() < 1) {
            XD.msg('请至少选择一条记录');
            return;
        }
    	var dataids = [];
        for (var i = 0; i < select.getSelection().length; i++) {
            dataids.push(select.getSelection()[i].get('entryid'));
        }
        var sendgrid = this.findView(btn).down('[itemId=sendgridID]');
        sendgrid.initGrid({dataids:dataids});

        var sendform = this.findView(btn).down('[itemId=sendformID]');
        sendform.load({
            url: '/dataopen/getOpenDoc',
            params: {
                dataids: dataids
            },
            success: function (form, action) {},
            failure: function (form, action) {
                XD.msg("操作失败！");
            }
        });

        var view = this.findView(btn);
        var tabview = this.findView(btn).getComponent('sendformview');
        tabview.down('[itemId=submit]').hide();
        tabview.down('[itemId=next]').show();
        view.setActiveItem(tabview);
    },

    sendHandler: function (btn) {
        var grid = btn.findParentByType('dataopenDealGridView');
        this.toSendView(btn, grid);
    },
    
    removeHandler: function (btn) {
        var dealgrid = btn.findParentByType('dataopenDealGridView');
        var select = dealgrid.getSelectionModel();
        if (select.getCount() < 1) {
            XD.msg('请至少选择一条记录');
        } else {
            XD.confirm('是否确定移除选中的记录', function () {
                var dataids = [];
                for (var i = 0; i < select.getSelection().length; i++) {
                    dataids.push(select.getSelection()[i].get('entryid'));
                }
                Ext.MessageBox.wait('移除中...');
                Ext.Ajax.request({
                    params: {ids: dataids.join(XD.splitChar)},
                    url: '/dataopen/deleteOpenbox',
                    method: 'post',
                    sync: true,
                    success: function () {
                        Ext.MessageBox.hide();
                        XD.msg('移除成功');
                        dealgrid.getStore().loadPage(1);
                    },
                    failure: function () {
                        Ext.MessageBox.hide();
                        XD.msg('操作中断');
                    }
                });
            });
        }
    },
    //直接开放
    openHandler: function (btn) {
    	var grid = btn.findParentByType('dataopengrid');
        this.toSendView(btn, grid);
        var tabview = this.findView(btn).getComponent('sendformview');
        tabview.down('[itemId=submit]').show();
        tabview.down('[itemId=next]').hide();
    },
    addHandler: function (btn) {
        var select = btn.findParentByType('dataopengrid').getSelectionModel();
        if (select.getCount() < 1) {
            XD.msg('请至少选择1条记录');
            return;
        }

        var dataids = [];
        for (var i = 0; i < select.getSelection().length; i++) {
            dataids.push(select.getSelection()[i].get('entryid'));
        }
        Ext.Ajax.request({
            params: {
                dataids: dataids
            },
            url: '/dataopen/addtobox',
            method: 'post',
            sync: true,
            success: function (resp) {
                var respText = Ext.decode(resp.responseText);
                if (respText.success == true) {
                    XD.msg(respText.msg);
                }
            },
            failure: function () {
                XD.msg('添加失败');
            }
        });
    },
    dontopenHandler: function (btn) {
        var select = btn.findParentByType('dataopenOpenedGridView').getSelectionModel();
        if (select.getCount() < 1) {
            XD.msg('请至少选择1条记录');
            return;
        }

        XD.confirm("是否确认取消开放？", function () {
            var dataids = [];
            for (var i = 0; i < select.getSelection().length; i++) {
                dataids.push(select.getSelection()[i].get('entryid'));
            }
            Ext.Ajax.request({
                params: {
                    dataids: dataids
                },
                url: '/dataopen/dontopen',
                method: 'post',
                sync: true,
                success: function (resp) {
                    var respText = Ext.decode(resp.responseText);
                    if (respText.success == true) {
                        XD.msg(respText.msg);
                    }
                    btn.findParentByType('dataopenOpenedGridView').getStore().reload();
                    btn.findParentByType('dataopenTabView').down('dataopenDontOpenGridView').getStore().reload();
                },
                failure: function () {
                    XD.msg('操作中断');
                }
            });
        });
    },
    cancelbanHandler: function (btn) {
        var select = btn.findParentByType('dataopenDontOpenGridView').getSelectionModel();
        if (select.getCount() < 1) {
            XD.msg('请至少选择1条记录');
            return;
        }

        XD.confirm("是否确认撤销不开放？", function () {
            var dataids = [];
            for (var i = 0; i < select.getSelection().length; i++) {
                dataids.push(select.getSelection()[i].get('entryid'));
            }
            Ext.Ajax.request({
                params: {
                    dataids: dataids
                },
                url: '/dataopen/cancelban',
                method: 'post',
                sync: true,
                success: function (resp) {
                    var respText = Ext.decode(resp.responseText);
                    if (respText.success === true) {
                        XD.msg(respText.msg);
                    }
                    btn.findParentByType('dataopenDontOpenGridView').getStore().reload();
                    btn.findParentByType('dataopenTabView').down('dataopengrid').getStore().reload();
                },
                failure: function () {
                    XD.msg('操作中断');
                }
            });
        });
    },
    initFormField:function(form, operate, nodeid){
//        if (form.nodeid != nodeid) {//切换节点后，form和tree的节点id不相等
            form.nodeid = nodeid;//用左侧树节点的id初始化form的nodeid参数
            form.removeAll();//移除form中的所有表单控件
            var field = {
                xtype: 'hidden',
                name: 'entryid'
            };
            form.add(field);
            var formField = form.getFormField();//根据节点id查询表单字段
            if(formField.length==0){
                XD.msg('请检查模板设置信息是否正确');
                return;
            }
            form.templates = formField;
            form.initField(formField,operate);//重新动态添加表单控件
//        }
        return '加载表单控件成功';
    },
    
    getNodename: function (nodeid) {
    	var nodename;
        Ext.Ajax.request({
            async:false,
            url: '/nodesetting/getFirstLevelNode/' + nodeid,
            success:function (response) {
                nodename = Ext.decode(response.responseText);
            }
        });
        return nodename;
    },

    getCurrentform:function (form) {
        if(form.up('dataopenFormView')){
            return form.up('dataopenFormView');
        }
        return form.up('EntryFormView');
    },

    getCurrentDataOpenform:function (btn) {
        if(btn.up('dataopenFormView')){
            return btn.up('dataopenFormView');
        }
        return btn.up('EntryFormView');
    },

    //点击上一条
    preHandler:function(btn){
        var currentDataOpenform = this.getCurrentDataOpenform(btn);
        var form = currentDataOpenform.down('dynamicform');
        this.refreshFormData(form, 'pre');
    },

    //点击下一条
    nextHandler:function(btn){
        var currentDataOpenform = this.getCurrentDataOpenform(btn);
        var form = currentDataOpenform.down('dynamicform');
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


    initFormData:function(operate, form, entryid){
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
            if(fields[i].value&&typeof(fields[i].value)=='string'&&fields[i].value.indexOf('label')>-1){
                continue;
            }
            if(fields[i].xtype == 'combobox'){
                fields[i].originalValue = null;
            }
            nullvalue.set(fields[i].name, null);
        }
        form.loadRecord(nullvalue);
        var formview = this.getCurrentform(form);
        var etips = formview.down('[itemId=etips]');
        etips.show();
        if(operate!='look'){
            var settingState = this.ifSettingCorrect(form.nodeid,form.templates);
            if(!settingState){
                return;
            }
        }
        this.activeForm(form);
        var eleview,solidview;
        // var longview;
        if (form.findParentByType('EntryFormView')) {
        	eleview = this.findFormView(form).down('electronic');
	        solidview = this.findFormView(form).down('solid');
	        // longview = this.findFormView(form).down('long');
        } else {
        	eleview = this.findDataopenFormView(form).down('electronic');
	        solidview = this.findDataopenFormView(form).down('solid');
	        // longview = this.findDataopenFormView(form).down('long');
        }
        form.nodeid = window.selectednodeid;
        if(typeof(entryid) != 'undefined'){
            Ext.Ajax.request({
                method: 'GET',
                scope: this,
                url: '/management/entries/' + entryid,
                success: function (response) {
                    var entry = Ext.decode(response.responseText);
                    if(operate == 'add'){
                        delete entry.entryid;
                    }
                    form.loadRecord({getData: function () {return entry;}});
                    //字段编号，用于特殊的自定义字段(范围型日期)
                    var fieldCode = form.getRangeDateForCode();
                    if (fieldCode != null) {
                        //动态解析数据库日期范围数据并加载至两个datefield中
                        form.initDaterangeContent(entry);
                    }
                    //初始化原文数据
                    eleview.initData(entry.entryid);
                    solidview.initData(entry.entryid);
                    // longview.initData(entry.entryid);
                }
            });
        }else{
            eleview.initData();
            solidview.initData();
            // longview.initData();
        }
//        form.formStateChange(operate);
        form.fileLabelStateChange(eleview,operate);
        form.fileLabelStateChange(solidview,operate);
        // form.fileLabelStateChange(longview,operate);
    },
    lookHandler: function (btn) {
        var grid = btn.findParentByType(window.gridtype);
        var record = grid.selModel.getSelection();
        var tree = grid.findParentByType('dataopenView').down('[itemId=treepanelId]');
        var node = tree.selModel.getSelected().items[0];
        if (!node) {
            XD.msg('请选择节点');
            return;
        }
        if (record.length == 0) {
            XD.msg('请至少选择一条需要查看的数据');
            return;
        }
        var entryids = [];
        var nodeids = [];
        for(var i=0;i<record.length;i++){
            entryids.push(record[i].get('entryid'));
            nodeids.push(record[i].get('nodeid'));
        }
        var entryid = record[0].get('entryid');
        var form = this.findFormView(btn).down('dynamicform');
        form.operate = 'look';
        form.entryids = entryids;
        form.nodeids = nodeids;
        form.entryid = entryids[0];
        this.initFormField(form, 'hide', node.get('fnid'));
        this.initFormData('look',form,entryid);
    },
    ifCodesettingCorrect:function (nodeid) {
        var codesetting = [];
        Ext.Ajax.request({
            url: '/codesetting/getCodeSettingFields',
            async:false,
            params:{
                nodeid:nodeid
            },
            success: function (response) {
                if(Ext.decode(response.responseText).success==true){
                    codesetting = Ext.decode(response.responseText).data;
                }
            }
        });
        if(codesetting.length==0){
            return;
        }
        return '档号设置信息正确';
    },

    ifSettingCorrect:function (nodeid,templates) {
        var hasArchivecode = false;//表单字段是否包含档号（archivecode）
        Ext.each(templates,function (item) {
            if(item.fieldcode=='archivecode'){
                hasArchivecode = true;
            }
        });
        if(hasArchivecode){//若表单字段包含档号，则判断档号设置是否正确
            var codesettingState = this.ifCodesettingCorrect(nodeid);
            if(!codesettingState){
                XD.msg('请检查档号设置信息是否正确');
                return;
            }
        }
        return '档号设置正确';
    },
    //切换到详细内容视图
    activeInfoGrid: function (btn) {
        // var showOpendocWin = Ext.create('Ext.window.Window',{
        //     modal:true,
        //     width:900,
        //     height:530,
        //     title:'查看开放单据',
        //     layout:'fit',
        //     closeToolText:'关闭',
        //     closeAction:'hide',
        //     items:[{
        //         xtype: 'dataopenDocView',
        //         nodeid: window.selectednodeid
        //     }]
        // });
        // var showOpendocView = showOpendocWin.down('dataopenDocView');
        // var opendocGrid = showOpendocView.down('dataopenDocEntryGridView');
        // opendocGrid.initGrid({batchnum:num,nodeid:showOpendocView.nodeid,type:'数据开放',info:'详细内容'});
        // showOpendocView.setActiveItem(opendocGrid);
        // showOpendocWin.show();
        btn.up('dataopenFormView').activeWindow.show();
        //然后加载数据开放视图
        var dataopenView = dataopengridInfo.up('dataopenTabView').up('dataopenView');
        dataopenView.setActiveItem(dataopenView.getComponent('gridview'));
    },
    //切换到列表界面视图
    activeGrid: function (btn) {
        var dataopenview = this.findView(btn);
        dataopenview.setActiveItem(this.findGridView(btn));
        if (document.getElementById('mediaFrame')) {
            document.getElementById('mediaFrame').setAttribute('src', '');
        }
        if (document.getElementById('solidFrame')) {
            document.getElementById('solidFrame').setAttribute('src', '');
        }
        // if (document.getElementById('longFrame')) {
        //     document.getElementById('longFrame').setAttribute('src', '');
        // }
        var grid = this.findGridView(btn).down(window.gridtype);
        grid.notResetInitGrid();
    },
    //切换到表单界面视图
    activeForm: function (btn) {
        var view = this.findView(btn);
        var formview;
        if (btn.findParentByType('EntryFormView')) {
        	formview = this.findFormView(btn);
        } else {
        	formview = this.findDataopenFormView(btn);
        }
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
        var solidview = formview.down('solid');
        eleview.operateFlag = "look"; //电子文件查看标识符
        solidview.operateFlag = "look";//利用文件查看标识符
        eleview.initData(obj.entryid);
        solidview.initData(obj.entryid);
        var from =formview.down('dynamicform');
        //电子文件按钮权限
        var elebtns = eleview.down('toolbar').query('button');
        from.getELetopBtn(elebtns,eleview.operateFlag );
        var soildbtns = solidview.down('toolbar').query('button');
        from.getELetopBtn(soildbtns,solidview.operateFlag);
        //电子文件按钮权限
        formview.setActiveTab(1);
        return formview;
    },
    //获取数据开放应用视图
    findView: function (btn) {
        return btn.findParentByType('dataopenView');
    },

    //获取列表界面视图
    findGridView: function (btn) {
        return this.findView(btn).getComponent('gridview');
    },
    //获取表单界面视图
    findFormView: function (btn) {
        return this.findView(btn).down('EntryFormView');
    },
    //获取数据开放界面视图
    findDataopenFormView: function (btn) {
    	return this.findView(btn).down('dataopenFormView');
    },

    //获取数据开放查看开放单据视图
    findOpendocView: function (btn) {
        return btn.findParentByType('dataopenDocView');
    },

    //获取数据开放查看开放单据界面的单据列表视图
    findOpendocGridView:function (btn) {
        return this.findOpendocView(btn).down('dataopenShowDocGridView');
    },

    //获取数据开放查看开放单据界面的条目详情列表视图
    findOpendocEntryGridView:function (btn) {
        return this.findOpendocView(btn).down('dataopenDocEntryGridView');
    },
    
    //切换到数据开放查看开放单据界面的单据列表视图
    activeOpendocGrid: function (btn) {
        var view = this.findOpendocView(btn);
        var opendocview = this.findOpendocGridView(btn);
        view.setActiveItem(opendocview);
        return opendocview;
    },

    showDealDetailsWin:function(id){
        var dealDetailsWin = Ext.create('Ext.window.Window',{
            modal:true,
            width:1000,
            height:530,
            title:'办理详情',
            layout:'fit',
            closeToolText:'关闭',
            closeAction:'hide',
            items:[{
                xtype: 'DealDetailsGridView'
            }]
        });
        var store = dealDetailsWin.down('DealDetailsGridView').getStore();
        store.proxy.extraParams.opendocid = id;
        store.reload();
        dealDetailsWin.show();
    },
    
    //切换到数据开放查看开放单据界面的条目详情列表视图
    activeOpendocEntryGrid: function (btn) {
        var view = this.findOpendocView(btn);
        var opendocentryview = this.findOpendocEntryGridView(btn);
        view.setActiveItem(opendocentryview);
        return opendocentryview;
    },
    
    //切换到数据开放查看开放单据界面的详细信息列表视图
    activeLookdocGrid: function (view, btn) {
    	var grid = dataopengridInfo;
    	var dataopenView = dataopengridInfo.up('dataopenTabView').up('dataopenView');
    	var currentGrid = view.findParentByType('dataopenDocEntryGridView');
        var record = currentGrid.selModel.getSelection();
        if (record.length == 0) {
            XD.msg('请至少选择一条需要查看的数据');
            return;
        }
        var entryids = [];
        var nodeids = [];
        for(var i=0;i<record.length;i++){
            entryids.push(record[i].get('entryid'));
            nodeids.push(record[i].get('nodeid'));
        }
        var entryid = record[0].get('entryid');
        var form = dataopenView.down('dataopenFormView').down('dynamicform');
        form.operate = 'look';
        form.entryids = entryids;
        form.nodeids = nodeids;
        form.entryid = entryids[0];
        dataopenView.down('dataopenFormView').activeWindow=view.up('window');
        dataopenView.down('dataopenFormView').activeWindow.hide();
        this.initFormField(form, 'hide', grid.dataParams.nodeid);
        this.initFormData('look',form,entryid);
    },


    //--------未开放自选字段导出--s----//
    chooseFieldExportExcel: function (view) {
        exportState = "Excel";
        var userGridView = view.findParentByType('dataopengrid');
        var select = userGridView.getSelection();
        var tree = this.findGridView(view).down('treepanel');
        var ids = [];
        //var selectAll = userGridView.down('[itemId=selectAll]').checked;
        var pattern = new RegExp("[`*':'.<>/?*|‘”“'\\\\？\"\"]");
        Ext.each(userGridView.getSelectionModel().getSelection(), function () {
            ids.push(this.get('entryid'));
        });
        entryids = ids;
        if (ids.length == 0) {
            XD.msg('请至少选择一条需要导出的数据');
            return;
        } else {
            var selectItem = Ext.create("Dataopen.view.DataopenGroupSetView", {});
            selectItem.items.get(0).getStore().load({});
            selectItem.show();
        }
    },
    chooseFieldExportXml: function (view) {
        exportState = "Xml";
        var userGridView = view.findParentByType('dataopengrid');
        var select = userGridView.getSelection();
        var tree = this.findGridView(view).down('treepanel');
        var ids = [];
        //var selectAll = userGridView.down('[itemId=selectAll]').checked;
        var pattern = new RegExp("[`*':'.<>/?*|‘”“'\\\\？\"\"]");
        Ext.each(userGridView.getSelectionModel().getSelection(), function () {
            ids.push(this.get('entryid'));
        });
        entryids = ids;
        if (ids.length == 0) {
            XD.msg('请至少选择一条需要导出的数据');
            return;
        } else {
            var selectItem = Ext.create("Dataopen.view.DataopenGroupSetView", {});
            selectItem.items.get(0).getStore().load({});
            selectItem.show();
        }
    },
    chooseFieldExportXmlAndFile: function (view) {
        exportState = "XmlAndFile";
        var userGridView = view.findParentByType('dataopengrid');
        var select = userGridView.getSelection();
        var tree = this.findGridView(view).down('treepanel');
        var ids = [];
        //var selectAll = userGridView.down('[itemId=selectAll]').checked;
        var pattern = new RegExp("[`*':'.<>/?*|‘”“'\\\\？\"\"]");
        Ext.each(userGridView.getSelectionModel().getSelection(), function () {
            ids.push(this.get('entryid'));
        });
        entryids = ids;
        if (ids.length == 0) {
            XD.msg('请至少选择一条需要导出的数据');
            return;
        } else {
            var selectItem = Ext.create("Dataopen.view.DataopenGroupSetView", {});
            selectItem.items.get(0).getStore().load({});
            selectItem.show();
        }
    },
    chooseFieldExportExcelAndFile: function (view) {
        exportState = "ExcelAndFile";
        var userGridView = view.findParentByType('dataopengrid');
        var select = userGridView.getSelection();
        var tree = this.findGridView(view).down('treepanel');
        var ids = [];
        //var selectAll = userGridView.down('[itemId=selectAll]').checked;
        var pattern = new RegExp("[`*':'.<>/?*|‘”“'\\\\？\"\"]");
        Ext.each(userGridView.getSelectionModel().getSelection(), function () {
            ids.push(this.get('entryid'));
        });
        entryids = ids;
        if (ids.length == 0) {
            XD.msg('请至少选择一条需要导出的数据');
            return;
        } else {
            var selectItem = Ext.create("Dataopen.view.DataopenGroupSetView", {});
            selectItem.items.get(0).getStore().load({});
            selectItem.show();
        }
    },
    chooseSave: function (view) {
        var filenames = "";
        var isbtn = "";
        var pattern = new RegExp("[/:*?\"<>|]");
        var selectView = view.findParentByType('dataopenGroupSetView');
        var FieldCode = selectView.items.get(0).getValue()
        dataopenfieldcod = FieldCode;
        var exporUrl = "";
        if (FieldCode.length>0) {
            var win = Ext.create("Dataopen.view.DataopenMessageView", {});
            win.show();
        }else {
            XD.msg("请选择需要导出的字段")
        }
    },
    //--------未开放自选字段导出----e----//

    //--------以开放开放自选字段导出--s----//
    ykfchooseFieldExportExcel: function (view) {
        exportState = "Excel";
        var userGridView = view.findParentByType('dataopenOpenedGridView');
        var select = userGridView.getSelection();
        var tree = this.findGridView(view).down('treepanel');
        var ids = [];
        //var selectAll = userGridView.down('[itemId=selectAll]').checked;
        var pattern = new RegExp("[`*':'.<>/?*|‘”“'\\\\？\"\"]");
        Ext.each(userGridView.getSelectionModel().getSelection(), function () {
            ids.push(this.get('entryid'));
        });
        entryids = ids;
        if (ids.length == 0) {
            XD.msg('请至少选择一条需要导出的数据');
            return;
        } else {
            var selectItem = Ext.create("Dataopen.view.DataopenGroupSetView", {});
            selectItem.items.get(0).getStore().load({});
            selectItem.show();
        }
    },
    ykfchooseFieldExportXml: function (view) {
        exportState = "Xml";
        var userGridView = view.findParentByType('dataopenOpenedGridView');
        var select = userGridView.getSelection();
        var tree = this.findGridView(view).down('treepanel');
        var ids = [];
        //var selectAll = userGridView.down('[itemId=selectAll]').checked;
        var pattern = new RegExp("[`*':'.<>/?*|‘”“'\\\\？\"\"]");
        Ext.each(userGridView.getSelectionModel().getSelection(), function () {
            ids.push(this.get('entryid'));
        });
        entryids = ids;
        if (ids.length == 0) {
            XD.msg('请至少选择一条需要导出的数据');
            return;
        } else {
            var selectItem = Ext.create("Dataopen.view.DataopenGroupSetView", {});
            selectItem.items.get(0).getStore().load({});
            selectItem.show();
        }
    },
    ykfchooseFieldExportXmlAndFile: function (view) {
        exportState = "XmlAndFile";
        var userGridView = view.findParentByType('dataopenOpenedGridView');
        var select = userGridView.getSelection();
        var tree = this.findGridView(view).down('treepanel');
        var ids = [];
        //var selectAll = userGridView.down('[itemId=selectAll]').checked;
        var pattern = new RegExp("[`*':'.<>/?*|‘”“'\\\\？\"\"]");
        Ext.each(userGridView.getSelectionModel().getSelection(), function () {
            ids.push(this.get('entryid'));
        });
        entryids = ids;
        if (ids.length == 0) {
            XD.msg('请至少选择一条需要导出的数据');
            return;
        } else {
            var selectItem = Ext.create("Dataopen.view.DataopenGroupSetView", {});
            selectItem.items.get(0).getStore().load({});
            selectItem.show();
        }
    },
    ykfchooseFieldExportExcelAndFile: function (view) {
        exportState = "ExcelAndFile";
        var userGridView = view.findParentByType('dataopenOpenedGridView');
        var select = userGridView.getSelection();
        var tree = this.findGridView(view).down('treepanel');
        var ids = [];
        //var selectAll = userGridView.down('[itemId=selectAll]').checked;
        var pattern = new RegExp("[`*':'.<>/?*|‘”“'\\\\？\"\"]");
        Ext.each(userGridView.getSelectionModel().getSelection(), function () {
            ids.push(this.get('entryid'));
        });
        entryids = ids;
        if (ids.length == 0) {
            XD.msg('请至少选择一条需要导出的数据');
            return;
        } else {
            var selectItem = Ext.create("Dataopen.view.DataopenGroupSetView", {});
            selectItem.items.get(0).getStore().load({});
            selectItem.show();
        }
    },
    ykfrelease: function (view) {
        var userGridView = view.findParentByType('dataopenOpenedGridView');
        var tree = this.findGridView(view).down('treepanel');
        var record = tree.selModel.getSelected().items[0];
        var nodeid = record.get('fnid');
        var currentnode = this.getCurrentNode(record);
        var ids = [];
        Ext.each(userGridView.getSelectionModel().getSelection(), function () {
            ids.push(this.get('entryid'));
        });
        entryids = ids;
        if (ids.length == 0) {
            XD.msg('请至少选择一条需要发布的数据');
            return;
        } else {
            var dataopenDocFormView=new Ext.create("Dataopen.view.DataopenDocFormView");
            dataopenDocFormView.entryids = ids;
            dataopenDocFormView.nodeid = nodeid;
            dataopenDocFormView.currentnode = currentnode;
            var docform=dataopenDocFormView.down('[itemId=formitemid]');
            docform.load({
                url: '/dataopen/getDataopenDoc',
                params: {
                    entryids: ids
                },
                success: function (form, action) {},
                failure: function () {XD.msg('操作中断');}
            });
            dataopenDocFormView.show();
        }
    },
    kfrelease: function (btn) {
        var dataopenDocFormView = btn.up('dataopenDocFormView');
        XD.confirm('确定要发布吗?', function () {
            Ext.MessageBox.wait('正在发布请稍后...', '提示');
            dataopenDocFormView.down('[itemId=formitemid]').submit({
                url: '/dataopen/dataopenRelease',
                method: 'post',
                params:{
                    entryids:dataopenDocFormView.entryids,
                    nodeid:dataopenDocFormView.nodeid,
                    currentnode:dataopenDocFormView.currentnode
                },
                success: function (form, action) {
                    location.href = '/dataopen/downLoadDataOpenRelease';
                    XD.msg('发布成功');
                    Ext.MessageBox.hide();
                    dataopenDocFormView.close();
                },
                failure: function () {
                    Ext.MessageBox.hide();
                    XD.msg('操作失败');
                }
            });
        },this);
    },
    getCurrentNode:function(record){
        var currentNode = record.data.text;
        var recordTemp = record.parentNode;
        while(recordTemp != null && recordTemp.data.parentId != null){
            currentNode = recordTemp.data.text + "_" + currentNode;
            recordTemp = recordTemp.parentNode;
        }
        return currentNode;
    }
    //--------已开放自选字段导出----e----//
    ,
    //--------不开放自选字段导出--s----//
    bkfchooseFieldExportExcel: function (view) {
        exportState = "Excel";
        var userGridView = view.findParentByType('dataopenDontOpenGridView');
        var select = userGridView.getSelection();
        var tree = this.findGridView(view).down('treepanel');
        var ids = [];
        //var selectAll = userGridView.down('[itemId=selectAll]').checked;
        var pattern = new RegExp("[`*':'.<>/?*|‘”“'\\\\？\"\"]");
        Ext.each(userGridView.getSelectionModel().getSelection(), function () {
            ids.push(this.get('entryid'));
        });
        entryids = ids;
        if (ids.length == 0) {
            XD.msg('请至少选择一条需要导出的数据');
            return;
        } else {
            var selectItem = Ext.create("Dataopen.view.DataopenGroupSetView", {});
            selectItem.items.get(0).getStore().load({});
            selectItem.show();
        }
    },
    bkfchooseFieldExportXml: function (view) {
        exportState = "Xml";
        var userGridView = view.findParentByType('dataopenDontOpenGridView');
        var select = userGridView.getSelection();
        var tree = this.findGridView(view).down('treepanel');
        var ids = [];
        //var selectAll = userGridView.down('[itemId=selectAll]').checked;
        var pattern = new RegExp("[`*':'.<>/?*|‘”“'\\\\？\"\"]");
        Ext.each(userGridView.getSelectionModel().getSelection(), function () {
            ids.push(this.get('entryid'));
        });
        entryids = ids;
        if (ids.length == 0) {
            XD.msg('请至少选择一条需要导出的数据');
            return;
        } else {
            var selectItem = Ext.create("Dataopen.view.DataopenGroupSetView", {});
            selectItem.items.get(0).getStore().load({});
            selectItem.show();
        }
    },
    bkfchooseFieldExportXmlAndFile: function (view) {
        exportState = "XmlAndFile";
        var userGridView = view.findParentByType('dataopenDontOpenGridView');
        var select = userGridView.getSelection();
        var tree = this.findGridView(view).down('treepanel');
        var ids = [];
        //var selectAll = userGridView.down('[itemId=selectAll]').checked;
        var pattern = new RegExp("[`*':'.<>/?*|‘”“'\\\\？\"\"]");
        Ext.each(userGridView.getSelectionModel().getSelection(), function () {
            ids.push(this.get('entryid'));
        });
        entryids = ids;
        if (ids.length == 0) {
            XD.msg('请至少选择一条需要导出的数据');
            return;
        } else {
            var selectItem = Ext.create("Dataopen.view.DataopenGroupSetView", {});
            selectItem.items.get(0).getStore().load({});
            selectItem.show();
        }
    },
    bkfchooseFieldExportExcelAndFile: function (view) {
        exportState = "ExcelAndFile";
        var userGridView = view.findParentByType('dataopenDontOpenGridView');
        var select = userGridView.getSelection();
        var tree = this.findGridView(view).down('treepanel');
        var ids = [];
        //var selectAll = userGridView.down('[itemId=selectAll]').checked;
        var pattern = new RegExp("[`*':'.<>/?*|‘”“'\\\\？\"\"]");
        Ext.each(userGridView.getSelectionModel().getSelection(), function () {
            ids.push(this.get('entryid'));
        });
        entryids = ids;
        if (ids.length == 0) {
            XD.msg('请至少选择一条需要导出的数据');
            return;
        } else {
            var selectItem = Ext.create("Dataopen.view.DataopenGroupSetView", {});
            selectItem.items.get(0).getStore().load({});
            selectItem.show();
        }
    }
    //--------未开放自选字段导出----e----//

});