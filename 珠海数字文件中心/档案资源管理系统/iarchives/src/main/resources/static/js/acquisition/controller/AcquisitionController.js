/**
 * 数据采集控制器
 * Created by Rong on 2017/10/24.
 *
 */
var filecount = 0;//计算文件数
var treepanelInfo;
var formvisible,formlayout;
var entryids = "";
var NodeIdf = "";
var userFieldCode = "";
var tempParams;
var AcFormAndGridView, acquisitionSelectWin;
Ext.define('Acquisition.controller.AcquisitionController', {
    extend: 'Ext.app.Controller',

    views: [
    	'FormAndGridView','FormAndInnerGridView','FormView',
    	'AcquisitionGridView','AcquisitionFormView','AcquisitionFormAndGridView',//加载表单与表格视图
        'AcquisitionTreeComboboxView','AcquisitionReportGridView','AcquisitionDismantleFormView',
        'AcquisitionGroupSetView','AcquisitionMessageView',
        'AcquisitionSequenceView','AcquisitionSequenceGridView',//调序
        'AcquisitionSelectView','AcquisitionSelectWin','AcquisitionFieldView',//数据转移
        'AcquisitionExportMissView','AcquisitionLookMediaView',
        'AcquisitionMissPageCheck','AcquisitionMissPageDetailView','MissPageElectronicView',
        'ElectronicVersionGridView','ElectronicVersionView',
        'MetadataLogEntryGridView',
        'MediaItemsDataView','BasicMediaDataView','SetSortSequenceView','ServiceMetadataGridView',
        'OAImportGridView','OAImportView','OfflineAccessionPackageView'
    ],
    models: [
    	'AcquisitionModel','ReportGridModel','TransdocGridModel',
    	'AcquisitionClassificationModel','BatchModifyTemplatefieldModel',
    	'FieldModifyPreviewGridModel','AcquisitionGroupSetModel',
    	'AcquisitionSequenceModel',//调序
    	'AcquisitionSelectModel','AcquisitionFieldModel',//数据转移
        'AcquisitionMissPageDetailModel',
        'BatchModifyTemplateEnumfieldModel','ElectronicVersionGridModel',
        'MetadataLogEntryModel',
        'LongRetentionGridModel',
        'MediaDataModel','ServiceMetadataGridModel','OAImportGridModel',
        'OfflineAccessionResultGridModel'
    ],
    stores: [
    	'AcquisitionStore','ReportGridStore','TransdocGridStore',
    	'AcquisitionClassificationStore','BatchModifyTemplatefieldStore',
    	'FieldModifyPreviewGridStore','AcquisitionGroupSetStore',
    	'AcquisitionSequenceStore',//调序
		'AcquisitionSelectStore','ImportGridStore', 'TemplateStore',//数据转移
        'AcquisitionMissPageDetailStore',
        'BatchModifyTemplateEnumfieldStore','ElectronicVersionGridStore',
        'MetadataLogEntryStore',
        'LongRetentionGridStore','OAImportGridStore',
        'MediaItemsDtStore','ApproveManStore','ApproveOrganStore','ServiceMetadataGridStore','ApproveNodeStore'
    ],

    init: function () {
        var count = 0;
        var treeNode;
        var p4;
        this.control({
            'acquisitionFormAndGrid [itemId=treepanelId]': {
            	render: function (view) {
            		view.getRootNode().on('expand', function (node) {
            	        for (var i = 0; i < node.childNodes.length; i++) {
                            if (node.childNodes[i].raw.text == '全宗卷管理') {//隐藏全宗卷管理
                                node.childNodes[i].raw.visible = false;
                            }
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
                    window.nodeid = record.get('fnid');
                    window.isMedia = false;
                    var _this = this;

                    //列表\缩列图切换用
                    window._this=this;
                    window.qhTreemodel=treemodel;
                    window.qhRecord=record;

                    if(sessionStorage.getItem('mediaNodeid_' + window.nodeid) == "1"){//说明是声像文件，切换成缩略图显示
                        this.toMediaList();
                    }
                    else {
                        this.listTab(_this,treemodel,record);
                    }

                }
            },
            'AcquisitionMessage button[itemId="cancelExport"]': {
                click: function (view) {
                    view.findParentByType('AcquisitionMessage').close();
                }
            },
            'import button[itemId="back"]':{//导入-返回
                click: function(btn){
                    AcFormAndGridView.down('acquisitiongrid').initGrid();
                    var a=btn.up('window').down('import');
                    var fieldstore=a.down('[itemId=fieldgrid]').getStore();
                    fieldstore.removeAll();
                    btn.up('window').hide();
                    Ext.Ajax.request({
                        method: 'post',
                        url:'/import/deletUploadFile',
                        timeout:3600000,
                        params: {filePath:UnZipPath}
                    })
                }
            },
            'AcquisitionMessage button[itemId="SaveExport"]': {//导出
                click: function (view) {
                    var AcquisitionMessageView = view.up('AcquisitionMessage');
                    var fileName = AcquisitionMessageView.down('[itemId=userFileName]').getValue();
                    var zipPassword = AcquisitionMessageView.down('[itemId=zipPassword]').getValue();
                    var b = AcquisitionMessageView.down('[itemId=addZipKey]').checked;
                    var form = AcquisitionMessageView.down('[itemId=form]');
                    tempParams['fileName'] = fileName;
                    tempParams['zipPassword'] = zipPassword;
                    tempParams['userFieldCode'] = userFieldCode;

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
                        if(tempParams.exportState=="Xml"&&tempParams.indexLength>10000){
                            Ext.Msg.alert("提示", "提示：导出xml文件只支持导入1万条以内！");
                            return;
                        }
                        Ext.MessageBox.wait('正在处理请稍后...', '提示');
                        Ext.Ajax.request({
                            method: 'post',
                            url:'/export/capturechooseFieldExport',
                            timeout:XD.timeout,
                            scope: this,
                            async:true,
                            params: tempParams,
                            success:function(res){
                                var obj = Ext.decode(res.responseText).data;
                                if(obj.fileSizeMsg=="NO"){
                                    XD.msg('原文总大小超出限制');
                                    Ext.MessageBox.hide();
                                    return;
                                }
                                if(obj.entrySizeMsg=="NO"){
                                    if(tempParams.exportState=="XmlAndFile"||tempParams.exportState=="ExcelAndFile"){
                                        XD.msg('条目数超出限制，一次只支持导出10万含原文的条目！');
                                    }
                                    if(tempParams.exportState=="Excel"||tempParams.exportState=="Xml"){
                                        XD.msg('条目数超出限制，一次只支持导出50w的条目！');
                                    }
                                    Ext.MessageBox.hide();
                                    return;
                                }
                                window.location.href="/export/downloadZipFile?fpath="+encodeURIComponent(obj.filePath)
                                Ext.MessageBox.hide();
                                XD.msg('文件生成成功，正在准备下载');
                                AcquisitionMessageView.close()
                            },
                            failure:function(){
                                Ext.MessageBox.hide();
                                XD.msg('文件生成失败');
                            }
                        });
                    } else {
                        XD.msg("文件名不能为空")
                    }
                }
            },
            'entrygrid':{//卷内文件列表
                beforedrop: function (node, data, overmodel, position, dropHandlers) {
                    dropHandlers.cancelDrop();
                    if(data.records.length > 1){
                        XD.msg('不支持批量选择拖拽排序，请选择一条数据');
                    }else{
                        XD.confirm('确认将卷内文件[ '+data.records[0].get('archivecode')+' ]移动到[ '
                            + overmodel.get('archivecode')+' ]的' + ("before" == position?'前面吗':'后面吗'),function(){
                            var overorder = parseInt(overmodel.get('innerfile'));
                            if(isNaN(overorder)){
                                XD.msg('卷内顺序号包含非法字符，无法排序');
                                return;
                            }
                            var target;
                            if(typeof(overorder) == 'undefined'){
                                target = -1;
                            }else if("before" == position){
                                target = overorder;
                            }else if("after" == position){
                                target = overorder + 1;
                            }
                            Ext.Ajax.request({
                                url: '/acquisition/order/'+data.records[0].get('entryid')+'/'+target+'/'+window.fileArchivecode,
                                method: 'post',
                                success: function (response) {
                                    data.view.getStore().reload();
                                    var responseText = Ext.decode(response.responseText);
                                    if(responseText.success==false){
                                        XD.msg(responseText.msg);
                                        return;
                                    }else{
                                        XD.msg(responseText.msg);
                                        var grid = Ext.getCmp(data.view.id);
                                        // var dragzone = grid.getPlugins()[0].dropZone;
                                        // dragzone.invalidateDrop();
                                        // dragzone.handleNodeDrop(data, overmodel, position);
                                        // dragzone.fireViewEvent('drop', node, data, overmodel, position);
                                        grid.initGrid();
                                    }
                                }
                            });
                        });
                    }
                }
            },
            'acquisitiongrid ': {
                //render: this.initGrid,
                eleview: this.activeEleForm,
                itemdblclick:this.lookHandler
            },
            'mediaItemsDataView [itemId=save]':{
                click: this.saveHandler
            },
            'mediaItemsDataView [itemId=modify]':{
                click: this.modifyHandler
            },
            'mediaItemsDataView [itemId=del]':{
                click: this.delHandler
            },
            'mediaItemsDataView [itemId=look]': {//查看
                click: this.lookHandler
            },
            'mediaItemsDataView [itemId=gridList]':{//切换列表显示
                click:this.changeToList
            },
            'acquisitiongrid [itemId=toMediaBtn]':{//切换到缩略图页面
                click:this.toMediaList
            },
            'mediaItemsDataView button[itemId=numberAlignment]':{
                click:this.doCodesettingAlign
            },
            'mediaItemsDataView [itemId=metadataLog]': {//查看元数据日志
                click: this.metadataLogHandler
            },
            'acquisitiongrid [itemId=metadataLog]': {//查看元数据日志
                click: this.metadataLogHandler
            },
            'acquisitiongrid [itemId=lookServiceMetadata]': {//查看追溯元数据
                click: this.lookServiceMetadata
            },
            'mediaItemsDataView [itemId=ilookfile]':{//卷内文件　查看
                click : this.ilookfileHandler
            },

            'mediaItemsDataView [itemId=lookfileEle]':{//查看整卷文件
                click:function (btn) {
                    var grid = this.getGrid(btn);
                    var select = grid.acrossSelections;
                    var tree = this.findGridView(btn).down('treepanel');
                    var node = tree.selModel.getSelected().items[0];
                    if(select.length != 1){
                        XD.msg('只能选择一条数据');
                        return;
                    }
                    var lookFileElectronicWin = Ext.create("Ext.window.Window",{
                        width:'100%',
                        height:'100%',
                        header: false,
                        closeAction:'hide',
                        layout:'fit',
                        items:[{
                            xtype:'lookFileElectronicView'
                        }]
                    });
                    var lookFileElectronicView = lookFileElectronicWin.down('lookFileElectronicView');
                    var lookEleGrid = lookFileElectronicView.down('[itemId=lookFileGridId]');
                    var lookEleGridStore = lookEleGrid.getStore();
                    lookEleGridStore.proxy.extraParams.entryid = select[0].get('entryid');
                    var innernodeid = this.getNodeid(node.get("fnid"));
                    lookEleGridStore.proxy.extraParams.innernodeid = innernodeid; //卷内文件节点
                    lookEleGridStore.proxy.extraParams.nodeid = node.get("fnid");  //当前节点
                    lookEleGridStore.load(function () {
                        if(lookEleGridStore.getCount()>0){  //默认选择第一行
                            lookEleGrid.fireEvent('itemclick',lookEleGrid,lookEleGridStore.getAt(0), null, 0);
                            lookEleGrid.getSelectionModel().select(lookEleGridStore.getAt(0));
                        }
                    });
                    lookFileElectronicView.entryid = select[0].get('entryid');
                    var lookElectronicView = lookFileElectronicWin.down('lookElectronicView');
                    lookElectronicView.innernodeid = innernodeid;
                    lookElectronicView.nodeid = node.get("fnid");
                    lookFileElectronicWin.show();
                }
            },

            'acquisitiongrid [itemId=save]': {//著录
                click: this.saveHandler
            },
            'acquisitiongrid [itemId=modify]': {//修改
                click: this.modifyHandler
            },
            'acquisitiongrid [itemId=del]': {//删除
                click: this.delHandler
            },
            'acquisitiongrid [itemId=look]': {//查看
                click: this.lookHandler
            },
            'acquisitiongrid [itemId=Excel]': {//导出excel--
                click: this.chooseFieldExportExcel
            },
            'acquisitiongrid [itemId=Xml]': {//导出xml
                click: this.chooseFieldExportXml
            },
            'acquisitiongrid [itemId=ExcleAndElectronic]': {//导出excel和原文
                click: this.chooseFieldExportExcelAndFile
            },
            'acquisitiongrid [itemId=XmlAndElectronic]': {//导出xml和原文
                click: this.chooseFieldExportXmlAndFile
            },
            /*'acquisitiongrid [itemId=importSipBtnID]': {//导入
                click: this.importHandler
            },*/
            'acquisitiongrid [itemId=FieldTemp]': {//导出字段模板
                click: this.downloadFieldTemp
            },
            'acquisitiongrid [itemId=pageNumberCorrect]': {//页数矫正
                click: this.pageNumberCorrectHandler
            },
            'acquisitiongrid [itemId=editNewFile]': {//编辑新案卷
                click: this.newFileHandler
            },
            'acquisitiongrid [itemId=print]':{//打开报表显示列表
                click:this.printHandler
            },
            'acquisitiongrid [itemId=dataTransfor]': {//数据转移
            	click:this.dataTransforHandler
            },
            'acquisitionSelectWin [itemId=setField]': {//数据转移 - 提交
            	click:this.transforSetFieldHandler
            },
            'acquisitionSelectWin [itemId=close]': {//数据转移 - 关闭
            	click:this.transforCloseHandler
            },
            'acquisitionSelectView': {
            	select: function(treemodel, record){
            		// 记录目标节点id信息
            		targetNodeid = record.data.fnid;
            		// 初始化字段设置的下拉列表
            		var templateStore = this.getStore('TemplateStore');
                    templateStore.proxy.extraParams = {nodeid: record.data.fnid};
                    templateStore.reload();
            	}
            },
            'acquisitionFieldView [itemId=submit]': {//字段设置 - 提交
            	click:this.transforSubmitHandler
            },
            'acquisitionFieldView [itemId=close]': {//字段设置 - 关闭
            	click:this.transforCloseHandler
            },
            //卷内文件调序功能start-----------------------
            'entrygrid [itemId=sequence]':{//卷内文件调序
                click:this.sequenceHandler
            },
            'entrygrid [itemId=iprint]':{//卷内文件打印
                click:this.iprintHandler
            },
            'acquisitionSequenceView [itemId=up]': {//上调
                click:this.upHandler
            },
            'acquisitionSequenceView [itemId=down]': {//下调
                click:this.downHandler
            },
            'acquisitionSequenceView [itemId=save]': {//保存
                click:this.sequenceSaveHandler
            },
            'acquisitionSequenceView [itemId=back]': {//返回
                click: this.backToGrid
            },
            //卷内文件调序功能end-------------------------
            'acquisitionFormAndGrid [itemId=northgrid]':{
                itemclick : this.itemclickHandler
            },
            'acquisitionFormAndGrid [itemId=southgrid]':{
                //afterrender : this.southrender,
                eleview: this.activeEleForm,
                itemdblclick: this.ilookHandler
            },
            'acquisitionFormAndGrid [itemId=isave]':{//卷内文件著录
                click : this.isaveHandler
            },
            'acquisitionFormAndGrid [itemId=imodify]':{//卷内文件修改
                click : this.imodifyHandler
            },
            'acquisitionFormAndGrid [itemId=idel]':{//卷内文件删除
                click : this.idelHandler
            },
            'acquisitionFormAndGrid [itemId=ilook]':{//卷内文件查看
                click : this.ilookHandler
            },
            'acquisitiongrid [itemId=ilookfile]':{//卷内文件　查看
                click : this.ilookfileHandler
            },
            //////////统计项更新----------------------start//////////////////////////
            'acquisitiongrid [itemId=statisticUpdate]':{
           		click: this.statisticUpdateHandler
            },
            //////////统计项更新----------------------end//////////////////////////
            'acquisitionform':{
                afterrender:this.addKeyAction,
                tabchange:function(view){
                    var formview = view.down('dynamicform');
                    var eleview = view.down('electronic');
                    var solidview = view.down('solid');
                    if(view.activeTab.title == '原始文件'){
                        if(formview.entryid){//有entryid才加载
                            eleview.initData(formview.entryid);
                        }
                    }else if(view.activeTab.title == '利用文件'){
                        if(formview.entryid){
                            solidview.initData(formview.entryid);
                        }
                    }
                }
            },
            'acquisitionform [itemId=preBtn]':{
                click:this.preHandler
            },
            'acquisitionform [itemId=nextBtn]':{
                click:this.nextHandler
            },
            'acquisitionform [itemId=save]': {//保存
                click: this.submitForm
            },
            'acquisitionform [itemId=continuesave]': {//连续录入
                click: this.continueSubmitForm
            },
            'acquisitionform [itemId=back]': {//返回
                click: function(btn){
                    var treepanel = treepanelInfo;
                    var nodeid = treepanel.selModel.getSelected().items[0].get('fnid');
                    var currentAcquisitionform = this.getCurrentAcquisitionform(btn);
                    var formview = currentAcquisitionform.down('dynamicform');
                    //切换到列表界面,同时刷新列表数据(判断树节点nodeid是否和表单指定的nodeid)
                    if(formview.nodeid != nodeid){
                        this.activeGrid(btn,false);
                    }else{
                        this.activeGrid(btn,true);
                    }
                }
            },
            'acquisitionGroupSetView button[itemId="close"]': {
                click: function (view) {
                    view.findParentByType('acquisitionGroupSetView').close();
                }
            },
            'acquisitionGroupSetView button[itemId="save"]': {
                click: this.chooseSave
            },
            'acquisitionGroupSetView button[itemId="addAllOrNotAll"]': {
                click:function(view){
                    var itemSelector = view.findParentByType('acquisitionGroupSetView').down('itemselector');
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
            'acquisitionReportGridView [itemId=print]':{//打印报表
                click:function (btn) {
                    var reportGrid = btn.findParentByType('acquisitionReportGridView');
                    var records = reportGrid.getSelectionModel().getSelection();
                    if(records.length==0){
                        XD.msg('请选择需要打印的报表');
                        return;
                    }
                    var record = records[0];
                    var filename = record.get('filename');
                    if(!filename){
                        XD.msg('无报表样式文件，请在报表管理中上传');
                        return;
                    }
                    if(reportServer == 'UReport'){
                        Ext.Ajax.request({
                            method: 'GET',
                            url: '/report/ifFileExist/' + record.get('reportid'),
                            scope: this,
                            async: false,
                            success: function (response) {
                                var responseText = Ext.decode(response.responseText);
                                if (responseText.success == true) {
                                    var params = {};
                                    params['entryid'] = reportGrid.entryids.join(",");
                                    XD.UReportPrint(null, filename, params);
                                } else {
                                    XD.msg('打印失败！' + responseText.msg);
                                    return;
                                }
                            }
                        });
                    }
                    else if(reportServer == 'FReport') {
                        Ext.Ajax.request({
                            method: 'GET',
                            url: '/report/ifFileExist/' + record.get('reportid'),
                            scope: this,
                            async: false,
                            success: function (response) {
                                var responseText = Ext.decode(response.responseText);
                                if (responseText.success == true) {
                                    //在ajax里面异步打开新标签页，会被拦截(谷歌会拦截，360不会)
                                    var win = null;
                                    XD.FRprint(win, filename, reportGrid.entryids.length > 0 ? "'entryid':'" + reportGrid.entryids.join(",") + "','nodeid':'" + reportGrid.nodeid + "'" : '');
                                } else {
                                    XD.msg('打印失败！' + responseText.msg);
                                    return;
                                }
                            }
                        });
                    }
                }
            },
            'acquisitionReportGridView [itemId=showAllReport]':{//显示所有报表
                click:function (btn) {
                    var reportGrid = btn.findParentByType('acquisitionReportGridView');
                    if(reportGrid.down('[itemId=showAllReport]').text=='显示所有报表'){
                        reportGrid.down('[itemId=showAllReport]').setText('显示当前报表');
                        reportGrid.initGrid({nodeid:reportGrid.nodeid,flag:'all'});
                    }else if(reportGrid.down('[itemId=showAllReport]').text=='显示当前报表'){
                        reportGrid.down('[itemId=showAllReport]').setText('显示所有报表');
                        reportGrid.initGrid({nodeid:reportGrid.nodeid});
                    }
                }
            },
            'acquisitionReportGridView [itemId=back]':{//报表列表返回至数据列表
                click:function (btn) {
                    btn.up('window').hide();
                }
            },
            ///////////批量操作－－－－－－－－－－－－－－－start////////////////////////////
            'acquisitiongrid [itemId=batchmodify]':{//批量修改
                click:this.doBatchModify
            },
            'batchModifyModifyFormView [itemId=templatefieldCombo]':{
                render:this.loadModifyTemplatefieldCombo,
                select:this.loadModifyTemplateEnumfieldCombo
            },
            'batchModifyModifyFormView button[itemId=addToModify]':{//批量修改窗口 加入修改
                click:this.addToModify
            },
            'batchModifyModifyFormView button[itemId=deleteModify]':{//批量修改窗口　删除修改
                click:this.deleteModify
            },
            'batchModifyModifyFormView button[itemId=clear]':{//批量修改窗口　清除
                click:function (btn) {
                    var formview = btn.up('batchModifyModifyFormView');
                    var fieldModifyPreviewGrid = formview.down('grid');
                    fieldModifyPreviewGrid.getStore().removeAll();
                    this.loadModifyTemplatefieldCombo(formview.getForm().findField('fieldname'));
                }
            },
            'batchModifyModifyFormView button[itemId=getPreview]':{//批量修改窗口　获取预览
                click:function (btn) {
                    var formview = btn.up('batchModifyModifyFormView');
                    var formWin = formview.up('window');
                    var fieldModifyPreviewGrid = formview.down('grid');
                    var fieldModifyPreviewGridStore = fieldModifyPreviewGrid.getStore();
                    var fieldModifyPreviewGridData = fieldModifyPreviewGridStore.data.items;
                    if(fieldModifyPreviewGridData.length==0){
                        XD.msg('请选择修改字段');
                        return;
                    }
                    var fieldModifyData = "";
                    var operateFieldcodes = [];
                    if(fieldModifyPreviewGridData.length > 0){//如果有已存在的修改字段
                        for (var i = 0; i < fieldModifyPreviewGridData.length; i++) {
                            var item = fieldModifyPreviewGridData[i];
                            if (i < fieldModifyPreviewGridData.length - 1) {
                                fieldModifyData += item.get('fieldcode')+'∪'+item.get('fieldname')+'∪'+item.get('fieldvalue')+"∩";
                            } else {
                                fieldModifyData += item.get('fieldcode')+'∪'+item.get('fieldname')+'∪'+item.get('fieldvalue');
                            }
                            operateFieldcodes.push(item.get('fieldcode'));
                        }
                    }
                    if (formview.filingtype) {   //归档时临时归档条目直接执行批量操作
                        var that = this;
                        var fieldData = fieldModifyData.split('∩');
                        var modifyDetail = '';
                        for (var i = 0; i < fieldData.length; i++) {
                            var data = fieldData[i].split('∪');
                            modifyDetail += '['+data[1]+']设置为“'+data[2]+'”，';
                        }
                        modifyDetail = modifyDetail.substring(0, modifyDetail.length-1);
                        var updateConfirmMsg = '本次操作将把临时归档所选记录的'+modifyDetail+',记录数：共'+formview.entryids.split(',').length+'条, 是否继续?';
                        XD.confirm(updateConfirmMsg,function () {
                            Ext.Msg.wait('正在进行批量修改，请耐心等待……', '正在操作');
                            Ext.Ajax.request({
                                url: '/batchModify/updateFileModify',
                                params: {
                                    entryidArr : formview.entryids.split(','),
                                    nodeid:formview.filingnodeid,
                                    fieldModifyData: fieldModifyData,
                                    flag:"批量修改",
                                    type:"数据采集"
                                },
                                scope: that,
                                success: function (response) {
                                    Ext.MessageBox.hide();
                                    XD.msg("批量修改成功");
                                    formview.filinggrid.getStore().reload();      //刷新归档条目
                                    formWin.close();
                                },
                                failure: function (response) {
                                    Ext.MessageBox.hide();
                                    XD.msg("操作失败");
                                }
                            });
                        });
                    }else{
                        var batchModifyResultPreviewWin = Ext.create('Ext.window.Window',{
                            width:'65%',
                            height:'70%',
                            title:'批量操作预览',
                            draggable : true,//可拖动
                            resizable : false,//禁止缩放
                            modal:true,
                            closeAction: 'hide',
                            closeToolText:'关闭',
                            layout:'fit',
                            items:[{
                                xtype:'batchModifyResultPreviewGrid',
                                fieldmodifydata:fieldModifyData,
                                operateFlag:formWin.title,
                                formview:formview
                            }]
                        });
                        delTempByUniqueType('modi');
                        var resultPreviewGrid = batchModifyResultPreviewWin.down('batchModifyResultPreviewGrid');
                        var params = resultPreviewGrid.formview.resultgrid.getStore().proxy.extraParams;
                        if (formview.fromOutside && typeof(params.content) !== 'undefined') {//外面
                            params['basicCondition'] = params.condition;
                            params['basicOperator'] = params.operator;
                            params['basicContent'] = params.content;
                        }
                        delete params.condition;
                        delete params.operator;
                        delete params.content;
                        params['entryidArr'] = resultPreviewGrid.formview.entryids.split(',');
                        params['fieldModifyData'] = resultPreviewGrid.fieldmodifydata;
                        params['multiValue'] = operateFieldcodes;
                        params['excludeValues'] = operateFieldcodes;
                        params['flag'] = resultPreviewGrid.operateFlag;
                        params['isSelectAll'] = resultPreviewGrid.formview.isSelectAll;
                        params['type'] = '数据采集';
                        params['info'] = '批量操作';
                        params['pageState'] = 'false';//标记翻页状态

                        var type = true;
                        resultPreviewGrid.initGrid(params,type);
                        batchModifyResultPreviewWin.show();
                        window.batchModifyResultPreviewWins = batchModifyResultPreviewWin;
                        Ext.on('resize',function(a,b){
                            window.batchModifyResultPreviewWins.setPosition(0, 0);
                            window.batchModifyResultPreviewWins.fitContainer();
                        });
                    }
                }
            },
            'batchModifyModifyFormView button[itemId=exit]':{//批量修改窗口　退出
                click:function (btn) {
                    var batchModifyModifyFormView = btn.findParentByType('batchModifyModifyFormView');
                    if(batchModifyModifyFormView.filingtype){   //判断是否归档时批量操作
                        batchModifyModifyFormView.filinggrid.getStore().reload();      //刷新归档条目
                    }else{
                        AcFormAndGridView.down('acquisitiongrid').getStore().proxy.url='/acquisition/entriesPost';
                        AcFormAndGridView.down('acquisitiongrid').getStore().getProxy().actionMethods={read:'POST'};
                    }
                    btn.up('window').close();
                }
            },
            'batchModifyResultPreviewGrid button[itemId=batchUpdateBtn]':{//批量操作预览－执行批量更新
                click:this.doBatchUpdate
            },
            'batchModifyResultPreviewGrid button[itemId=backBtn]':{//批量操作预览－返回
                click:function (btn) {
                    var batchType=btn.up('batchModifyResultPreviewGrid').operateFlag;
                    if(batchType=='批量修改'){
                        batchType='modi';
                    }else if(batchType=='批量增加'){
                        batchType='add';
                    }else{
                        batchType='repl';
                    }
                    delTempByUniqueType(batchType);
                    btn.up('batchModifyResultPreviewGrid').formview.fromOutside = false;
                    btn.up('window').hide();
                }
            },

            'acquisitiongrid [itemId=batchreplace]':{//批量替换
                click:this.doBatchReplace
            },
            'batchModifyReplaceFormView [itemId=templatefieldCombo]':{
                render:this.loadReplaceTemplatefieldCombo
            },
            'batchModifyReplaceFormView button[itemId=getPreview]':{//批量替换窗口　获取预览
                click:function (btn) {
                    var formview = btn.up('batchModifyReplaceFormView');
                    var formWin = formview.up('window');
                    var fieldcodeandnameCombo = formview.getForm().findField('fieldname');
                    var fieldcodeAndName = fieldcodeandnameCombo.getValue();
                    var searchcontentField = formview.getForm().findField('searchcontent');
                    var searchcontent = searchcontentField.getValue();
                    var replacecontentField = formview.getForm().findField('replacecontent');
                    var replacecontent = replacecontentField.getValue();
                    var containspace = formview.down('[itemId=ifContainSpaces]').getValue();
                    var ifContainspace = containspace?true:false;
                    var allowempty = formview.down('[itemId=ifAllowEmpty]').getValue();

                    if(!fieldcodeAndName){XD.msg('请选择修改字段');return;}
                    if(!searchcontent){XD.msg('查找内容不允许为空');return;}
                    if(!replacecontent && !allowempty){XD.msg('替换值不允许为空');return;}
                    if(searchcontent==replacecontent && !ifContainspace){XD.msg('替换前后数据无变化');return;}

                    var fieldReplaceData = [fieldcodeAndName+'∪'+searchcontent+'∪'+replacecontent];
                    var operateFieldcodes =[fieldcodeAndName.split('_')[0]];
                    if (formview.filingtype) {   //归档时临时归档条目直接执行批量操作
                        var that = this;
                        var fieldmodifydatas = fieldReplaceData[0].split('∪');//[fieldcode_fieldname,addcontent,inserttype(或inserttype_insertplaceindex)]
                        var fieldname = fieldmodifydatas[0].split('_')[1];
                        var replaceDetail = '“'+fieldmodifydatas[1]+'”字符串';
                        var ifcontainspace = ifContainspace;
                        if(ifcontainspace){
                            replaceDetail+='及其前后空格值';
                        }
                        var replacecontent = fieldmodifydatas[2];
                        if (replacecontent != '') {
                            replaceDetail += '替换为“'+replacecontent+'”字符串';
                        } else {
                            replaceDetail += '替换为空字符串';
                        }
                        var updateConfirmMsg = '本次操作将把临时归档所选记录的['+fieldname+']里的'+replaceDetail+',记录数：共'+formview.entryids.split(',').length+'条, 是否继续?';
                        XD.confirm(updateConfirmMsg,function () {
                            Ext.Msg.wait('正在进行批量替换，请耐心等待……', '正在操作');
                            Ext.Ajax.request({
                                url: '/batchModify/updateFileModify',
                                params: {
                                    entryidArr : formview.entryids.split(','),
                                    nodeid:formview.filingnodeid,
                                    fieldReplaceData: fieldReplaceData,
                                    ifcontainspace: ifContainspace,
                                    flag:"批量替换",
                                    type:"数据采集"
                                },
                                scope: that,
                                success: function (response) {
                                    Ext.MessageBox.hide();
                                    XD.msg("批量替换成功");
                                    formview.filinggrid.getStore().reload();      //刷新归档条目
                                    formWin.close();
                                },
                                failure: function (response) {
                                    Ext.MessageBox.hide();
                                    XD.msg("操作失败");
                                }
                            });
                        });
                    }else {
                        var batchModifyResultPreviewWin = Ext.create('Ext.window.Window', {
                            width: '65%',
                            height: '70%',
                            title: '批量操作预览',
                            draggable: true,//可拖动
                            resizable: false,//禁止缩放
                            modal: true,
                            closeAction: 'hide',
                            closeToolText: '关闭',
                            layout: 'fit',
                            items: [{
                                xtype: 'batchModifyResultPreviewGrid',
                                fieldreplacedata: fieldReplaceData,
                                ifcontainspace: ifContainspace,
                                operateFlag: formWin.title,
                                formview: formview
                            }]
                        });
                        delTempByUniqueType('repl');
                        var resultPreviewGrid = batchModifyResultPreviewWin.down('batchModifyResultPreviewGrid');
                        var params = resultPreviewGrid.formview.resultgrid.getStore().proxy.extraParams;
                        if (formview.fromOutside && typeof(params.content) !== 'undefined') {//外面
                            params['basicCondition'] = params.condition;
                            params['basicOperator'] = params.operator;
                            params['basicContent'] = params.content;
                        }
                        delete params.condition;
                        delete params.operator;
                        delete params.content;
                        params['entryidArr'] = resultPreviewGrid.formview.entryids.split(',');
                        params['fieldReplaceData'] = resultPreviewGrid.fieldreplacedata;
                        params['ifContainSpace'] = resultPreviewGrid.ifcontainspace;
                        params['multiValue'] = operateFieldcodes;
                        params['excludeValues'] = operateFieldcodes;
                        params['flag'] = resultPreviewGrid.operateFlag;
                        params['isSelectAll'] = resultPreviewGrid.formview.isSelectAll;
                        params['type'] = '数据采集';
                        params['info'] = '批量操作';
                        params['pageState'] = 'false';//标记翻页状态

                        var type = true;
                        resultPreviewGrid.initGrid(params, type);
                        resultPreviewGrid.getStore().on('load', function (store) {
                            if (!resultPreviewGrid.operateCount) {//仅第一次load该store完成时给列表的operateCount赋值
                                resultPreviewGrid.operateCount = resultPreviewGrid.getStore().totalCount;
                            }
                        });
                        batchModifyResultPreviewWin.show();
                        window.batchModifyResultPreviewWins = batchModifyResultPreviewWin;
                        Ext.on('resize', function (a, b) {
                            window.batchModifyResultPreviewWins.setPosition(0, 0);
                            window.batchModifyResultPreviewWins.fitContainer();
                        });
                    }
                }
            },
            'batchModifyReplaceFormView button[itemId=exit]':{//批量替换窗口　退出
                click:function (btn) {
                    var batchModifyReplaceFormView = btn.findParentByType('batchModifyReplaceFormView');
                    if(batchModifyReplaceFormView.filingtype){   //判断是否归档时批量操作
                        batchModifyReplaceFormView.filinggrid.getStore().reload();      //刷新归档条目
                    }else {
                        AcFormAndGridView.down('acquisitiongrid').getStore().proxy.url = '/acquisition/entriesPost';
                        AcFormAndGridView.down('acquisitiongrid').getStore().getProxy().actionMethods = {read: 'POST'};
                    }
                    btn.up('window').close();
                }
            },
            'acquisitiongrid [itemId=batchadd]':{//批量增加
                click:this.doBatchAdd
            },
            'batchModifyAddFormView [itemId=templatefieldCombo]':{
                render:this.loadAddTemplatefieldCombo
            },
            'batchModifyAddFormView button[itemId=getPreview]':{//批量增加窗口　获取预览
                click:function (btn) {
                    var formview = btn.up('batchModifyAddFormView');
                    var formWin = formview.up('window');
                    var fieldcodeandnameCombo = formview.getForm().findField('fieldname');
                    var fieldcodeAndName = fieldcodeandnameCombo.getValue();
                    var addcontentField = formview.getForm().findField('addcontent');
                    var addcontent = addcontentField.getValue();
                    var inserttype = formview.getValues()['insertPlace'];
                    var insertplaceindexField = formview.getForm().findField('insertPlaceIndex');
                    var insertplaceindex = insertplaceindexField.getValue();

                    if(!fieldcodeAndName){XD.msg('请选择修改字段');return;}
                    if(!addcontent){XD.msg('添加内容不允许为空');return;}
                    if(!inserttype){XD.msg('请检查位置设置信息');return;}
                    if(inserttype=='anywhere' && !insertplaceindex){XD.msg('请输入插入字符位置');return;}
                    if(isNaN(insertplaceindex)){XD.msg('插入字符位置输入项格式不正确');return;}
                    if(parseInt(insertplaceindex)<1){XD.msg('插入字符位置输入项最小值为1');return;}
                    if(parseInt(insertplaceindex)>8000){XD.msg('插入字符位置输入项最大值为8000');return;}

                    var inserttypeAndPlaceindex, fieldModifyData;
                    if(inserttype=='anywhere'){
                        inserttypeAndPlaceindex = inserttype+'_'+insertplaceindex;
                        fieldModifyData = [fieldcodeAndName+'∪'+addcontent+'∪'+inserttypeAndPlaceindex];
                    }else{
                        fieldModifyData = [fieldcodeAndName+'∪'+addcontent+'∪'+inserttype];
                    }
                    var operateFieldcodes =[fieldcodeAndName.split('_')[0]];
                    if (formview.filingtype) {   //归档时临时归档条目直接执行批量操作
                        var that = this;
                        var fieldmodifydatas = fieldModifyData[0].split('∪');//[fieldcode_fieldname,searchcontent,replacement]
                        var fieldname = fieldmodifydatas[0].split('_')[1];
                        var addcontent = fieldmodifydatas[1];
                        var insertPlaceInfo = fieldmodifydatas[2];
                        var inserttype,placeindex;
                        if(insertPlaceInfo.indexOf('_')!=-1){
                            inserttype = insertPlaceInfo.split('_')[0];
                            placeindex = insertPlaceInfo.split('_')[1];
                        }else{
                            inserttype = insertPlaceInfo;
                        }
                        var updateConfirmMsg;
                        if(inserttype=='anywhere' && placeindex){
                            updateConfirmMsg = '本次操作将在临时归档所选记录的['+fieldname+']的第'+placeindex+'位增加“'+addcontent+'”字符串，记录数：共'+formview.entryids.split(',').length+'条, 是否继续?';
                        }
                        if(inserttype=='front'){
                            updateConfirmMsg = '本次操作将在临时归档所选记录的前面增加“'+addcontent+'”字符串，记录数：共'+formview.entryids.split(',').length+'条, 是否继续?';
                        }
                        if(inserttype=='behind'){
                            updateConfirmMsg = '本次操作将在临时归档所选记录的后面增加“'+addcontent+'”字符串，记录数：共'+formview.entryids.split(',').length+'条, 是否继续?';
                        }
                        XD.confirm(updateConfirmMsg,function () {
                            Ext.Msg.wait('正在进行批量增加，请耐心等待……', '正在操作');
                            Ext.Ajax.request({
                                url: '/batchModify/updateFileModify',
                                params: {
                                    entryidArr : formview.entryids.split(','),
                                    nodeid:formview.filingnodeid,
                                    fieldModifyData: fieldModifyData,
                                    flag:"批量增加",
                                    type:"数据采集"
                                },
                                scope: that,
                                success: function (response) {
                                    Ext.MessageBox.hide();
                                    XD.msg("批量增加成功");
                                    formview.filinggrid.getStore().reload();      //刷新归档条目
                                    formWin.close();
                                },
                                failure: function (response) {
                                    Ext.MessageBox.hide();
                                    XD.msg("操作失败");
                                }
                            });
                        });
                    }else {
                        var batchModifyResultPreviewWin = Ext.create('Ext.window.Window', {
                            width: '65%',
                            height: '70%',
                            title: '批量操作预览',
                            draggable: true,//可拖动
                            resizable: false,//禁止缩放
                            modal: true,
                            closeAction: 'hide',
                            closeToolText: '关闭',
                            layout: 'fit',
                            items: [{
                                xtype: 'batchModifyResultPreviewGrid',
                                fieldmodifydata: fieldModifyData,
                                operateFlag: formWin.title,
                                formview: formview
                            }]
                        });
                        delTempByUniqueType('add');
                        var resultPreviewGrid = batchModifyResultPreviewWin.down('batchModifyResultPreviewGrid');
                        var params = resultPreviewGrid.formview.resultgrid.getStore().proxy.extraParams;
                        if (formview.fromOutside && typeof(params.content) !== 'undefined') {//外面
                            params['basicCondition'] = params.condition;
                            params['basicOperator'] = params.operator;
                            params['basicContent'] = params.content;
                        }
                        delete params.condition;
                        delete params.operator;
                        delete params.content;
                        params['entryidArr'] = resultPreviewGrid.formview.entryids.split(',');
                        params['fieldModifyData'] = resultPreviewGrid.fieldmodifydata;
                        params['multiValue'] = operateFieldcodes;
                        params['excludeValues'] = operateFieldcodes;
                        params['flag'] = resultPreviewGrid.operateFlag;
                        params['isSelectAll'] = resultPreviewGrid.formview.isSelectAll;
                        params['type'] = '数据采集';
                        params['info'] = '批量操作';
                        params['pageState'] = 'false';//标记翻页状态

                        var type = true;
                        resultPreviewGrid.initGrid(params, type);
                        batchModifyResultPreviewWin.show();
                        window.batchModifyResultPreviewWins = batchModifyResultPreviewWin;
                        Ext.on('resize', function (a, b) {
                            window.batchModifyResultPreviewWins.setPosition(0, 0);
                            window.batchModifyResultPreviewWins.fitContainer();
                        });
                    }
                }
            },
            'batchModifyAddFormView button[itemId=exit]':{//批量增加窗口　退出
                click:function (btn) {
                    var batchModifyAddFormView = btn.findParentByType('batchModifyAddFormView');
                    if(batchModifyAddFormView.filingtype){   //判断是否归档时批量操作
                        batchModifyAddFormView.filinggrid.getStore().reload();      //刷新归档条目
                    }else {
                        AcFormAndGridView.down('acquisitiongrid').getStore().proxy.url = '/acquisition/entriesPost';
                        AcFormAndGridView.down('acquisitiongrid').getStore().getProxy().actionMethods = {read: 'POST'};
                    }
                    btn.up('window').close();
                }
            },
            ///////////批量操作－－－－－－－－－－－－－－－end////////////////////////////
            'acquisitiongrid [itemId=misspagecheck]':{//漏页检测
                click:this.missPageCheck
            },

            'acquisitionMissPageDetailView [itemId=mxBtn]':{ //查看明细
                click:this.lookMedia
            },
            'acquisitionMissPageDetailView [itemId=export]':{  //导出漏页信息
                click:this.openExport
            },
            'acquisitionExportMissView button[itemId="SaveExport"]': {//导出
                click:this.missExport
            },
            'acquisitionExportMissView button[itemId="cancelExport"]': {//导出 关闭
                click:function (view) {
                    view.findParentByType('acquisitionExportMissView').close();
                }
            },

            'electronic [itemId=getEleVersion]':{//获取电子文件历史版本
                click:function (btn) {
                    var electronic = btn.findParentByType('electronic');
                    var eleTree = electronic.down('treepanel');
                    var records = eleTree.getView().getChecked();
                    var ids = [];
                    for(var i=0;i<records.length;i++){
                        if(records[i].get('fnid')&&records[i].get('fnid')!==''){
                            ids.push(records[i].get('fnid'));
                        }
                    }
                    if (ids.length != 1) {
                        XD.msg('请选择一条需要查看电子版本的数据');
                        return;
                    }
                    var eleid = ids[0];
                    var getVersionGridView = Ext.create('Ext.window.Window',{
                        width: 1000,
                        height: 600,
                        title: '电子文件历史版本',
                        draggable: true,//可拖动
                        resizable: false,//禁止缩放
                        modal: true,
                        closeToolText: '关闭',
                        layout: 'fit',
                        items:[{
                            xtype: 'electronicVersionGridView'
                        }],
                        listeners:{
                            "close":function () {
                                eleTree.getStore().reload();
                            }
                        }
                    });
                    var store = getVersionGridView.down('electronicVersionGridView').getStore();
                    store.proxy.extraParams.eleid = eleid;
                    store.load({callback:function(r,options,success){
                        if (r.length < 1){
                            XD.msg('无历史版本记录');
                            return;
                        }else{
                    getVersionGridView.down('electronicVersionGridView').getSelectionModel().clearSelections();
                    getVersionGridView.eleTree = eleTree;
                    window.getVersionGridView = getVersionGridView;
                            getVersionGridView.eleid = eleid;
                    getVersionGridView.show();
                }
                    }});
                }
            },

            'electronicVersionGridView [itemId=lookeleVersion]':{//查看电子文件历史版本
                click:function (btn) {
                    var electronicVersionGridView = btn.findParentByType('electronicVersionGridView');
                    var select = electronicVersionGridView.getSelectionModel().getSelection();
                    if(select.length != 1){
                        XD.msg("只能选择一条数据");
                        return;
                    }
                    var eleVersionid = select[0].get('id');
                    window.eleVersionid = eleVersionid;
                    window.eletype = 'capture';
                    var electronicVersion = Ext.create("Ext.window.Window", {
                        width: '100%',
                        height: '100%',
                        title: '查看电子文件历史版本',
                        modal: true,
                        header: false,
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        closeToolText: '关闭',
                        layout: 'fit',
                        items: [{xtype: 'electronicVersion'}]
                    });
                    electronicVersion.down('electronicVersion').initData(eleVersionid);
                    electronicVersion.show();
                }
            },
            'electronicVersionGridView [itemId=deleleVersion]':{//删除电子文件历史版本
                click:function (btn) {
                    var electronicVersionGridView = btn.findParentByType('electronicVersionGridView');
                    var select = electronicVersionGridView.getSelectionModel().getSelection();
                    if(select.length == 0){
                        XD.msg("请至少选择一条需要操作的数据");
                        return;
                    }
                    var eleVersionids = [];
                    for(var i=0;i<select.length;i++){
                        eleVersionids.push(select[i].get('id'));
                    }
                    XD.confirm('确定要删除这 '+select.length+' 条数据吗?',function() {
                        Ext.Ajax.request({
                            url: '/acquisition/delVersion',
                            params: {
                                eleVersionids: eleVersionids
                            },
                            success: function (response) {
                                var info = Ext.decode(response.responseText);
                                if (info.success) {
                                    XD.msg('成功删除' + info.data + '条数据');
                                    electronicVersionGridView.getStore().reload();
                                }
                            },
                            failure: function () {
                                XD.msg('操作失败');
                            }
                        });
                    },this);
                }
            },
            'electronicVersionGridView [itemId=rebackVersion]': {//回滚电子文件历史版本
                click: function (btn) {
                    var electronicVersionGridView = btn.findParentByType('electronicVersionGridView');
                    var select = electronicVersionGridView.getSelectionModel().getSelection();
                    if (select.length != 1) {
                        XD.msg("只能选择一条需要操作的数据");
                        return;
                    }
                    var eleVersionid = select[0].get('id');
                    XD.confirm('确定回滚至' + select[0].get('version') + '版本吗？回滚后，当前版本将不可恢复，请先做好备份', function () {
                    Ext.Ajax.request({
                        url: '/acquisition/rebackVersion',
                        params: {
                            eleVersionid: eleVersionid
                        },
                        success: function (response) {
                            XD.msg('回滚成功');
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                    });
                }
            },
            'electronicVersionGridView [itemId=loadVersion]':{//下载电子文件历史版本
                click:function (btn) {
                    var electronicVersionGridView = btn.findParentByType('electronicVersionGridView');
                    var select = electronicVersionGridView.getSelectionModel().getSelection();
                    if(select.length == 0){
                        XD.msg("请至少选择一条需要操作的数据");
                        return;
                    }
                    var eleVersionids = [];
                    for(var i=0;i<select.length;i++){
                        eleVersionids.push(select[i].get('id'));
                    }
                    Ext.Ajax.request({
                        url: '/acquisition/ifVersionFileExist',
                        params: {
                            eleVersionids: eleVersionids
                        },
                        success: function (response) {
                            var responseText = Ext.decode(response.responseText);
                            if(responseText.success==true){
                                if(eleVersionids.length==1){
                                    var eleVersionid = eleVersionids[0];
                                    location.href = '/acquisition/downloadEleVersion/eleVersionid/'+ eleVersionid;
                                }else{
                                    location.href = '/acquisition/downloadEleVersion/eleVersionids/'+ eleVersionids;
                                }

                            }else{
                                XD.msg('下载失败！'+responseText.msg);
                                return;
                            }
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },
            'electronicVersionGridView [itemId=backVersion]':{//电子文件历史版本 返回
                click:function (btn) {
                    window.getVersionGridView.eleTree.getStore().reload();
                    window.getVersionGridView.close();
                }
            },

            'acquisitiongrid [itemId=setSequence]':{  //排序设置
                click:function (btn) {
                    var view = Ext.create("Acquisition.view.SetSortSequenceView");
                    var tree = this.findGridView(btn).down('treepanel');
                    var node = tree.selModel.getSelected().items[0];
                    var multiItemGetStore = view.down('[itemId=multiItemGetId]').getStore();
                    var multiItemSetStore = view.down('[itemId=multiItemSetId]').getStore();
                    var userSort = this.getUserNodeSort(node.get('fnid'));
                    Ext.Ajax.request({
                        method: 'post',
                        url: '/summarization/getSelectedByNodeId',
                        params:{
                            nodeid: node.get('fnid')
                        },
                        success: function (response) {
                            var data = Ext.decode(response.responseText);
                            for(var i=0;i<data.length;i++){
                                var flag = true;
                                for (var j = 0; j < userSort.length; j++) {
                                    var fieldnameStr = userSort[j].fieldcode+'_'+userSort[j].fieldname;
                                    if (data[i].fieldname == fieldnameStr) {
                                        flag = false;
                                        break;
                                    }
                                }
                                if (flag) {
                                    var itemObj = new Object();
                                    itemObj.value = data[i].fieldname;
                                    var record = new Ext.data.Record(itemObj);
                                    multiItemGetStore.add(record);
                                }
                            }
                            for (var j = 0; j < userSort.length; j++) {
                                var text;
                                if(userSort[j].sorttype=='asc'){
                                    text = userSort[j].fieldcode+'_'+userSort[j].fieldname+'_升序';
                                }else{
                                    text = userSort[j].fieldcode+'_'+userSort[j].fieldname+'_降序';
                                }
                                var itemObj = new Object();
                                itemObj.text = text;
                                itemObj.value = userSort[j].fieldcode+'_'+userSort[j].fieldname;
                                var record = new Ext.data.Record(itemObj);
                                multiItemSetStore.add(record);
                            }
                        }
                    });
                    view.nodeid = node.get('fnid');
                    view.show();
                }
            },

            'setSortSequenceView button[itemId=sortSubmit]': {   //设置排序提交
                click: function (view) {
                    var setSortSequenceView = view.findParentByType('setSortSequenceView');
                    var multiItemSet = setSortSequenceView.down('[itemId=multiItemSetId]');
                    var setStore = multiItemSet.getStore();
                    var sortStr = [];
                    for(var i=0;i<setStore.getCount();i++){
                        sortStr.push(setStore.getAt(i).get('text'));
                    }
                    Ext.Ajax.request({
                        url: '/acquisition/setSortSequence',
                        params: {
                            sortStr: sortStr,
                            nodeid:setSortSequenceView.nodeid
                        },
                        success: function (response) {
                            XD.msg('设置成功');
                            setSortSequenceView.close();
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'setSortSequenceView button[itemId=sortClose]':{   //设置排序关闭
                click:function (view) {
                    view.findParentByType('setSortSequenceView').close();
                }
            },
            'acquisitiongrid [itemId=importOA]': {//OA接收
                click:this.importOA
            },
            'oAImportView [itemId=back]':{ //oa接收 返回
                click:function (btn) {
                    btn.up('window').close();
                }
            },
            'oAImportView [itemId=lookPackpage]': {//查看数据包(上传)
                click: this.lookPackpage
            },
            'oAImportGridView [itemId=lookdetail]': {//查看验证明细
                click: this.lookdetailsHandler
            },'packageWindow [itemId=treepanelId]': {
                render: function (view) {
                    view.getRootNode().on('expand', function (node) {
                        node.getOwnerTree().getSelectionModel().select(node.childNodes[0]);
                    })
                },
                select: function (treemodel, record) {
                    if(record.get('children')!=null){
                        var dataview = this.findPackageWindow(treemodel.view).down('[itemId=dataview]');
                        var childrens = [];
                        for (var i = 0; i < record.get('children').length; i++) {
                            childrens.push(record.get('children')[i].text);
                        }
                        dataview.getStore().proxy.extraParams.childrens = childrens;
                        dataview.getStore().reload();
                    }
                    if(record.data.text.indexOf('.xml')>-1){
                        Ext.MessageBox.wait('正在解析获取元数据...', '提示');
                        var form = this.findMetadataForm(treemodel.view).getForm();
                        setTimeout(function(){
                            Ext.Ajax.request({
                                url:'/offlineAccession/getMetadata',
                                params:{
                                    fileName:window.filename,
                                    xmlName:record.data.text
                                },
                                success:function(response){
                                    Ext.MessageBox.hide();
                                    var responseText = Ext.decode(response.responseText);
                                    form.reset();
                                    form.setValues(responseText);
                                }
                            });
                        },100);
                    }
                }
            }
        })
    },

    //进入模块主页面时加载列表数据
    // initGrid: function (view) {
        // var tree = this.findGridView(view).down('treepanel');
        // Ext.defer(function () {
        //     view.nodeid = tree.getStore().getRoot().firstChild.get('fnid');
        //     view.getStore().proxy.extraParams.nodeid = view.nodeid;//加载列表数据
        //     view.initColumns(view);
        //     view.initGrid();
        // }, 1);
    // },
    findPackageWindow: function (btn) {
        return btn.up('packageWindow');
    },
    findMetadataForm: function (btn) {
        return this.findPackageWindow(btn).down('[itemId=metadataForm]');
    },
    //获取数据采集应用视图
    findView: function (btn) {
        if(btn.up('acquisitionTransdocView')){
            return btn.up('acquisitionTransdocView');
        }else if(btn.up('acquisitionPreviewTransView')){
            return btn.up('acquisitionPreviewTransView')
        }
        return btn.up('acquisitionFormAndGrid');
    },
    findTreeView : function (btn) {
        return btn.up('acquisitionFormAndGrid').down('treepanel');
    },

    //获取表单界面视图
    findFormView: function (btn) {
        return this.findView(btn).down('formAndGrid').down('acquisitionform');
    },

    findFormInnerView: function (btn) {
		return this.findView(btn).down('formAndInnerGrid').down('acquisitionform');
    },

    findFormToView: function (btn) {
    	return this.findView(btn).down('formView').down('acquisitionform');
    },

    findDfView: function (btn) {
    	return this.findView(btn).down('formView').down('acquisitionform').down('dynamicform');
    },
    
    //获取列表界面视图
    findGridView: function (btn) {
        return this.findView(btn).getComponent('gridview');
    },

    findGridToView: function (btn) {
    	return this.findView(btn).down('formAndGrid').down('acquisitiongrid');
    },
    
    findInnerGridView: function (btn) {
    	return this.findView(btn).down('formAndInnerGrid').down('acquisitiongrid');
    },

    findActiveGrid:function(btn){
        var active = this.findView(btn).down('[itemId=gridcard]').getLayout().getActiveItem();
        if(active.getXType() == "acquisitiongrid" || active.getXType() == 'mediaItemsDataView'){
            return active;
        }else if(active.getXType() == "panel"){
            return active.down('[itemId=northgrid]');
        }
    },

    findActiveInnerGrid:function(btn){
    	var active = this.findView(btn).down('[itemId=gridcard]').getLayout().getActiveItem();
        if(active.getXType() == "acquisitiongrid"){
            return active;
        }else if(active.getXType() == "panel"){
            return this.findInnerGridView(btn);
        }
    },

    findInnerGrid:function(btn){
        return this.findView(btn).down('[itemId=southgrid]');
    },
    
    findSequenceView: function(btn) {
    	return btn.findParentByType('acquisitionSequenceView').down('acquisitionSequenceGridView');
    },

    changeBtnStatus:function(form, operate){
    	var savebtn,continuesave,tbseparator;
    	if (form.findParentByType('formAndGrid')) {
        	savebtn = this.findFormView(form).down('[itemId=save]');
        	continuesave = this.findFormView(form).down('[itemId=continuesave]');
        	tbseparator = this.findFormView(form).getDockedItems('toolbar')[0].query('tbseparator');
        }
        if (form.findParentByType('formAndInnerGrid')) {
        	savebtn = this.findFormInnerView(form).down('[itemId=save]');
        	continuesave = this.findFormInnerView(form).down('[itemId=continuesave]');
        	tbseparator = this.findFormInnerView(form).getDockedItems('toolbar')[0].query('tbseparator');
        }
        if (form.findParentByType('formView')||form.findParentByType('acquisitionTransdocView')) {
        	savebtn = this.findFormToView(form).down('[itemId=save]');
        	continuesave = this.findFormToView(form).down('[itemId=continuesave]');
        	tbseparator = this.findFormToView(form).getDockedItems('toolbar')[0].query('tbseparator');
        }
        if(operate == 'look'){//查看时隐藏保存及连续录入按钮
            savebtn.setVisible(false);
            continuesave.setVisible(false);
            tbseparator[0].setVisible(false);
            tbseparator[1].setVisible(false);
        }else if(operate == 'modify' || operate == 'insertion'){//修改或插件时隐藏连续录入按钮
            savebtn.setVisible(true);
            continuesave.setVisible(false);
            tbseparator[0].setVisible(false);
            tbseparator[1].setVisible(true);
        }else{
            savebtn.setVisible(true);
            continuesave.setVisible(true);
            tbseparator[0].setVisible(true);
            tbseparator[1].setVisible(true);
        }
    },

    //切换到列表界面视图
    activeGrid: function (btn, flag) {
        var view = this.findView(btn);
        if(view.xtype=='acquisitionTransdocView'){
            this.application.getController('AcquisitionTransforController').activeDocEntryGrid(btn);
            return;
        }
        view.setActiveItem(this.findGridView(btn));
        AcFormAndGridView.setActiveItem(this.findGridView(btn));
        this.findFormView(btn).saveBtn = undefined;
        this.findFormView(btn).continueSaveBtn = undefined;
        this.findFormInnerView(btn).saveBtn = undefined;
        this.findFormInnerView(btn).continueSaveBtn = undefined;
        formvisible = false;
        var allMediaFrame = document.querySelectorAll('#mediaFrame');
        if(allMediaFrame){
            for (var i = 0; i < allMediaFrame.length; i++) {
                allMediaFrame[i].setAttribute('src','');
            }
        }
        if(document.getElementById('solidFrame')){
            document.getElementById('solidFrame').setAttribute('src','');
        }
        // if (document.getElementById('longFrame')) {
        //     document.getElementById('longFrame').setAttribute('src', '');
        // }
        if(flag){//根据参数确定是否需要刷新数据
            var grid = this.findActiveGrid(btn);
            grid.notResetInitGrid();
        }
    },

    //切换到表单界面视图
    activeForm: function (form) {
        var view = this.findView(form);
        var formAndGridView = view.down('formAndGrid');//保存表单与表格视图
        var formview = formAndGridView.down('acquisitionform');
        view.setActiveItem(formAndGridView);
        formview.items.get(0).enable();
        formview.setActiveTab(0);
        return formAndGridView;
    },

    //切换到卷内表单界面视图
    activeInnerForm: function (form) {
        var view = this.findView(form);
        var formAndInnerGridView = view.down('formAndInnerGrid');//保存表单与表格视图
        var formview = formAndInnerGridView.down('acquisitionform');
        view.setActiveItem(formAndInnerGridView);
        formview.items.get(0).enable();
        formview.setActiveTab(0);
        return formAndInnerGridView;
    },

    //切换到单个表单界面视图
    activeToForm: function (form) {
    	var view = this.findView(form);
    	var formView = view.down('formView');
    	var acquisitionform = formView.down('acquisitionform');
    	view.setActiveItem(formView);
    	acquisitionform.items.get(0).enable();
    	acquisitionform.setActiveTab(0);
    	return formView;
    },

    initSouthGrid:function (form) {
        var formAndGridView = this.findView(form).down('formAndGrid');//保存表单与表格视图
        var gridview = formAndGridView.down('acquisitiongrid');
        gridview.initGrid({nodeid:form.nodeid});
    },

    //获取到卷内的表格
    initSouthInnerGrid:function (form, entryid) {
    	var formAndInnerGrid = this.findView(form).down('formAndInnerGrid');//保存表单与表格视图
        var gridview = formAndInnerGrid.down('acquisitiongrid');
        gridview.dataUrl = '/acquisition/entries/innerfile/'+entryid + '/';
        gridview.initGrid({nodeid:form.nodeid});
    },

    activeEleForm:function(obj){
        var view = this.findView(obj.grid);
        var formview = this.getCurrentAcquisitionform(obj.grid);
        view.setActiveItem(formview.findParentByType('panel'));
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
        formview.setActiveTab(1);
        return formview;
    },

    //从页面获取计算项数据并+1
    addCalValue:function (form) {
        var codeSettingFieldList;//档号构成字段的集合
        Ext.Ajax.request({//获得档号构成字段的集合
            url:'/codesetting/getCodeSettingFields',
            async:false,
            params:{
                nodeid:form.nodeid
            },
            success:function(response){
                codeSettingFieldList = Ext.decode(response.responseText).data;
            }
        });
        if(!codeSettingFieldList && this.getNodename(form.nodeid)!='未归管理'){
            XD.msg('请检查档号设置信息是否正确');
            return;
        }
        var calFieldName = codeSettingFieldList[codeSettingFieldList.length-1];
        var calValue = form.getForm().findField(calFieldName).getValue();
        var calFieldLength = calValue.length;
        var addedCalValue = Number(calValue)+1;
        form.getForm().findField(calFieldName).setValue(Ext.String.leftPad(addedCalValue,calFieldLength,'0'));
    },

    //拼接开始日期与结束日期
    getDaterangeValue: function (btn) {
        var form = this.getCurrentAcquisitionform(btn).down('dynamicform');
        var fields = form.getForm().getFields();//所有Filed对象的集合
        var startdayName;//日期控件中开始日期框的name,与其itemId内容一致
        var enddayName;//日期控件中结束日期框的name,与其itemId内容一致
        for (var i = 0; i < fields.length; i++) {
            var itemid = fields.get(i).getItemId();
            if (Ext.String.endsWith(itemid,'startday',true)) {
                startdayName = itemid;
            } else if (Ext.String.endsWith(itemid,'endday',true)) {
                enddayName = itemid;
            }
        }
        var startdayValue = form.getForm().findField(startdayName).getValue();//若不修改日期，则获得string，若修改，则获得日期object
        var enddayValue = form.getForm().findField(enddayName).getValue();//若不修改日期，则获得string，若修改，则获得日期object
        if (startdayValue == null && enddayValue == null) {//null类型为object，若为null，直接返回null
            return null;
        }
        if (startdayValue == null) {
            startdayValue = '';
        }
        if (enddayValue == null) {
            enddayValue = '';
        }
        if (typeof startdayValue == 'object') {
            startdayValue = startdayValue.format('yyyyMMdd');
        }
        if (typeof enddayValue == 'object') {
            enddayValue = enddayValue.format('yyyyMMdd');
        }
        return startdayValue + '-' + enddayValue;
    },
    
    //卷内文件进行调序
    sequenceHandler:function (btn) {
    	delSqTempByUniquetag()
        northGridInfo = this.getGrid(btn);
        southGridInfo = this.getInnerGrid(btn);
    	var value = false;
    	var northGrid = this.getGrid(btn);
        var northRecord = northGrid.selModel.getSelection();
        var selectAll = northGrid.down('[itemId=selectAll]').checked;
        if (selectAll) {
            XD.msg('不支持选择所有页调序');
            return;
        }
    	if (northRecord < 1) {
    		if (northGrid.nodefullname.indexOf('卷内') > -1) {
    			XD.msg('请选择数据');
    		} else {
    			XD.msg('请选择一条案卷文件');
    		}
			return;
    	}
        var grid;
        if (northGrid.nodefullname.indexOf('卷内') > -1) {
			grid = northGrid;
        } else {
			grid = this.getInnerGrid(btn);
        }
        var record = grid.selModel.getSelection();
        var ids = [];
        //如果没有选择数据,那么加载全部数据
        if (record.length < 1) {//案卷列表 - 卷内文件
			var map = grid.getStore().data.map;
			for (key in map) {
				ids.push(map[key].id);
			}
		} else {//卷内文件列表
			Ext.each(record,function(){
				ids.push(this.get('entryid'));
			});
			Ext.Ajax.request({
                async:false,
                url: '/acquisition/getFilecode',
                params:{ids: ids, type: '数据采集'},
                success:function (response) {
                    if(!Ext.decode(response.responseText).success) {
                    	value = true;
                    }
                }
            });
		}
		if (!value) {
			var entryids = "";
			for (var i = 0; i < ids.length; i++) {
				if (i < ids.length - 1) {
					if (northGrid.nodefullname.indexOf('卷内') > -1) {
						entryids = entryids + record[i].get('id') + "∪";
					} else {
						entryids = entryids + ids[i] + "∪";
					}
				} else {
					if (northGrid.nodefullname.indexOf('卷内') > -1) {
						entryids = entryids + record[i].get('id');
					} else {
						entryids = entryids + ids[i];
					}
				}
			}
			var sequenceWin = Ext.create('Ext.window.Window',{
	            width:'60%',
	            height:'75%',
	            modal:true,
	            title:'文件调序',
	            closeToolText:'关闭',
	            closeAction:'hide',
	            layout:'fit',
	            items:[{
	                xtype: 'acquisitionSequenceView'//调序视图
	            }]
	        });
	        sequenceWin.show();
	        var view = sequenceWin.down('acquisitionSequenceView').down('acquisitionSequenceGridView');
	        var nodeid = this.getNodeid(northGrid.nodeid);
	        view.nodeid = nodeid;
	        view.currentNodeid = northGrid.nodeid;
	        //刷新调序表单
	        view.getStore().setPageSize(XD.pageSize);
	        view.initGrid({entryids: entryids, dataSource: 'capture', nodeid: nodeid});
		} else {
			XD.msg('请选择同一案卷的卷内记录！');
	        return;
		}
    },
    //卷内文件打印
    iprintHandler:function (btn) {
        var grid = this.getInnerGrid(btn);
        var tree = this.findGridView(btn).down('treepanel');
        var nodeid = tree.selModel.getSelected().items[0].get('fnid');
        var ids = [];
        Ext.each(grid.getSelectionModel().getSelection(),function(){
            ids.push(this.get('entryid'));
        });
        if(ids.length==0){
            XD.msg('请至少选择一条需要打印的数据');
            return;
        }
        var reportGridWin = Ext.create('Ext.window.Window',{
            width:'100%',
            height:'100%',
            header:false,
            draggable : false,//禁止拖动
            resizable : false,//禁止缩放
            modal:true,
            closeToolText:'关闭',
            layout:'fit',
            items:[{
                xtype: 'acquisitionReportGridView',
                entryids:ids,
                nodeid:nodeid
            }]
        });
        var reportGrid = reportGridWin.down('acquisitionReportGridView');
        reportGrid.initGrid({nodeid:nodeid + ',publicreportfnid'});
        reportGridWin.show();
    },
    //上调
    upHandler: function (btn) {
    	var grid = this.findSequenceView(btn);
    	var record = grid.selModel.getSelection();//当前选择的数据
    	if (record.length < 1) {
            XD.msg('请选择一条需要上调的数据');
            return;
        } else if (record.length > 1) {
        	XD.msg('只能选择一条数据进行操作');
            return;
        }
        var entryids = "";
        if (northGridInfo.nodefullname.indexOf('卷内') > -1) {
        	entryids = this.getEntryids(northGridInfo);
        } else {
        	entryids = this.getEntryids(southGridInfo);
        }
    	Ext.Ajax.request({
    		method: 'post',
            url: '/acquisition/upInnerFile',
            params: {
				currentId: record[0].data.entryid,
				nodeid: grid.nodeid,
				type: '数据采集'
            },
            success: function (response) {
                var responseText = Ext.decode(response.responseText);
                if (!responseText.success) {
                    XD.msg(responseText.msg);
                } else {
                	var store = grid.store;
			        store.proxy.url = '/acquisition/entryIndexSqCaptures';
			        store.proxy.extraParams = {entryids: entryids, nodeid: grid.nodeid};
			        store.reload();
                }
            },
            failure: function () {
                Ext.MessageBox.hide();
                XD.msg('操作中断');
            }
    	});
	},
	
	getEntryids: function (gridInfo) {
		var entryids = "";
    	var record = gridInfo.selModel.getSelection();
        if (record.length > 0) {
	        for (var i = 0; i < record.length; i++) {
				if (i < record.length - 1) {
					entryids = entryids + record[i].id + "∪";
				} else {
					entryids = entryids + record[i].id;
				}
			}
    	} else {
    		var ids = [];
    		var map = gridInfo.getStore().data.map;
    		for (key in map) {
	            ids.push(map[key].id);
	        }
	        for (var i = 0; i < ids.length; i++) {
				if (i < ids.length - 1) {
					entryids = entryids + ids[i] + "∪";
				} else {
					entryids = entryids + ids[i];
				}
			}
    	}
        return entryids;
	},
	
	//下调
	downHandler: function (btn) {
		var grid = this.findSequenceView(btn);
		var record = grid.selModel.getSelection();
        if (record.length < 1) {
            XD.msg('请选择一条需要下调的数据');
            return;
        } else if (record.length > 1) {
        	XD.msg('只能选择一条数据进行操作');
            return;
        }
        var entryids = "";
        if (northGridInfo.nodefullname.indexOf('卷内') > -1) {
        	entryids = this.getEntryids(northGridInfo);
        } else {
        	entryids = this.getEntryids(southGridInfo);
        }
		Ext.Ajax.request({
    		method: 'post',
            url: '/acquisition/downInnerFile',
            params: {
				currentId: record[0].data.entryid,
				nodeid: grid.nodeid,
				type: '数据采集'
            },
            success: function (response) {
                var responseText = Ext.decode(response.responseText);
                if (!responseText.success) {
                    XD.msg(responseText.msg);
                } else {
                	var store = grid.store;
			        store.proxy.url = '/acquisition/entryIndexSqCaptures';
			        store.proxy.extraParams = {entryids: entryids, nodeid: grid.nodeid};
			        store.reload();
                }
            },
            failure: function () {
                Ext.MessageBox.hide();
                XD.msg('操作中断');
            }
    	});
	},
	
	//调序保存
	sequenceSaveHandler: function (btn) {
		var grid = this.findSequenceView(btn);
		Ext.Ajax.request({
			params: {
                nodeid: grid.nodeid,
                entryids: grid.dataParams.entryids
            },
            url: '/acquisition/getArchivecodeValue',
            method: 'post',
            success: function (response) {
                var responseText = Ext.decode(response.responseText);
                if (responseText.msg == '档号重复') {
                	XD.msg('此次操作存在档号重复的记录，请先处理后再进行保存。<br />重复的档号：' + responseText.data);
					return;
                }
                if (responseText.msg == '无重复档号') {
                	XD.confirm('请确定数据准备无误，进行保存操作。',function(){
						Ext.Msg.wait('正在进行保存，请耐心等待……', '正在操作');
						Ext.Ajax.request({
							params: {
			                    nodeid: grid.nodeid
			                },
			                url: '/acquisition/saveSqtemp',
			                method: 'post',
			                sync: true,
			                success: function (response) {
			                	Ext.MessageBox.hide();
			                    btn.up('window').hide();
			                    if (northGridInfo.nodefullname.indexOf('案卷') > -1) {
			                    	var entryid = northGridInfo.selModel.getSelection()[0].id;
			                    	southGridInfo.notResetInitGrid({entryid: entryid,nodeid:grid.nodeid});//刷新卷内文件表格
			                    }
			                    if (northGridInfo.nodefullname.indexOf('卷内') > -1) {
			                    	northGridInfo.notResetInitGrid({nodeid:grid.currentNodeid});//刷新主页面表格
			                    }
			                },
			                failure: function () {
			                	Ext.MessageBox.hide();
			                    XD.msg('操作中断');
			                }
						});
					},this);
                }
            },
            failure: function () {
                XD.msg('操作中断');
            }
		});
	},
	
    //从调序返回表格视图
    backToGrid:function (btn) {
    	var grid;
        if (northGridInfo.nodefullname.indexOf('卷内') > -1) {
			grid = northGridInfo;
        } else {
			grid = southGridInfo;
        }
    	var ids = this.getEntryids(grid);
        Ext.Ajax.request({
			params: {
                entries: ids
            },
            url: '/acquisition/changeState',
            method: 'post',
            success: function (response) {
            	var responseText = Ext.decode(response.responseText);
                if (responseText.success) {
                	XD.confirm('离开此界面，系统将不会保存您所做的更改。',function(){
			    		btn.up('window').hide();
			    	});
                } else {
                	btn.up('window').hide();
                }
            },
            failure: function () {
                XD.msg('操作中断');
            }
        })
    },

    itemclickHandler: function(view, record, item, index, e){
        var fileArchivecode = record.get('archivecode');//案卷档号
        //用于案卷点击显示卷内文件条目，之前是根据案卷档号匹配，改为通过entryid获取档号设置，通过档号设置字段匹配
        var entryid =record.get('entryid');
        window.fileArchivecode = fileArchivecode;
        var southgrid = this.findInnerGrid(view);
        southgrid.dataUrl = '/acquisition/entries/innerfile/'+entryid + '/';
        var nodeid = this.getNodeid(record.get('nodeid'));
        southgrid.initGrid({nodeid:nodeid});
        southgrid.setTitle('查看'+fileArchivecode+'案卷的卷内');
        var treeSelctedRecord = this.findGridView(view).down('treepanel').selModel.getSelected().items[0];//获取当前左侧树所选取的节点（与对应卷内文件同级）
        var fullname=treeSelctedRecord.get('text');
        while(treeSelctedRecord.parentNode.get('text')!='数据采集'){
            fullname=treeSelctedRecord.parentNode.get('text')+'_'+fullname;
            treeSelctedRecord=treeSelctedRecord.parentNode;
        }
        southgrid.nodefullname = fullname.substring(0,fullname.lastIndexOf('_'))+'_'+'卷内文件';

//        southgrid.parentXtype = 'acquisition';
        southgrid.parentXtype = 'acquisitionFormAndGrid';
        southgrid.formXtype = 'acquisitionform';
    },

    // southrender:function(grid){
    //     var items = grid.getDockedItems('toolbar[dock="top"]')[0].items.items;
    //     for(var i = 13;i < items.length; i++){
    //         items[i].setVisible(false);
    //     }
    // },

    ifCodesettingCorrect:function (nodeid) {
        var codesetting = [];
        Ext.Ajax.request({
            url: '/codesetting/getCodeSettingFields',
            async:false,
            params:{
                nodeid:nodeid
            },
            success: function (response, opts) {
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
                XD.msg('请检查档号设置信息是否正确');
                return;
            }
            form.templates = formField;
            form.initField(formField,operate);//重新动态添加表单控件
//        }
        return '加载表单控件成功';
    },

    getInfo: function (nodeid) {
    	var res;
    	Ext.Ajax.request({
			async:false,
            method: 'get',
            params:{
                nodeid:nodeid
            },
            url: '/nodesetting/getRefid',//通过节点id获取机构id
            success:function (response) {
            	res = response;
            },
            failure:function () {
                XD.msg('操作失败');
            }
		});
		return res;
    },

    //点击上一条
    preHandler:function(btn){
        var currentAcquisitionform = this.getCurrentAcquisitionform(btn);
        var form = currentAcquisitionform.down('dynamicform');
        this.preNextHandler(form, 'pre');
    },

    //点击下一条
    nextHandler:function(btn){
        var currentAcquisitionform = this.getCurrentAcquisitionform(btn);
        var form = currentAcquisitionform.down('dynamicform');
        this.preNextHandler(form, 'next');
    },

    //条目切换，上一条下一条
    preNextHandler:function(form,type){
        var dirty = !!form.getForm().getFields().findBy(function(f){
            return f.wasDirty;
        });
        if(form.operate == 'modify' && dirty){
            XD.confirm('数据已修改，确定保存吗？',function() {
                //保存数据
                var formview = this.form;
                var nodename = this.ref.getNodename(formview.nodeid);
                var params={
                    nodeid: formview.nodeid,
                    type: formview.findParentByType('acquisitionform').operateFlag,
                    eleid: formview.findParentByType('acquisitionform').down('electronic').getEleids(),
                    operate: nodename
                };
                var fieldCode = formview.getRangeDateForCode();//字段编号，用于特殊的自定义字段(范围型日期)
                if (fieldCode != null) {
                    params[fieldCode] = formview.getDaterangeValue();
                }
                var archivecodeSetState = formview.setArchivecodeValueWithNode(nodename);
                if (!archivecodeSetState) {//若档号设置失败，则停止后续的表单提交
                    return;
                }
                Ext.MessageBox.wait('正在保存请稍后...', '提示');
                var formValues = formview.getValues();
                for (var name in formValues) {//遍历表单中的所有值
                    if (name == 'kccount' || name == 'fscount') {
                        if (formValues[name] == '' || formValues[name] == null) {
                            formValues[name] = "0";
                        }
                    }
                }
                formview.submit({
                    method: 'POST',
                    url: '/acquisition/entries',
                    params: params,
                    scope: this,
                    success: function (form, action) {
                        Ext.MessageBox.hide();
                        this.ref.refreshFormData(this.form, this.type);
                        var entryids = [formview.entryid];
                        captureServiceMetadataByZL(entryids,'数据采集',"修改");
                    },
                    failure: function (form, action) {
                        Ext.MessageBox.hide();
                        XD.msg(action.result.msg);
                    }
                });
            },{
                ref:this,
                form:form,
                type:type
            },function(){
                this.ref.refreshFormData(this.form, this.type)
            });
        }else{
            this.refreshFormData(form, type);
        }
    },

    refreshFormData:function(form, type){
        var entryids = form.entryids;
        var currentEntryid = form.entryid;
        var entryid;
        for(var i=0;i<entryids.length;i++){
            if(type == 'pre' && entryids[i] == currentEntryid){
                if(i==0){
                    i=entryids.length;
                }
                entryid = entryids[i-1];
                break;
            }else if(type == 'next' && entryids[i] == currentEntryid){
                if(i==entryids.length-1){
                    i=-1;
                }
                entryid = entryids[i+1];
                break;
            }
        }
        form.entryid = entryid;
        if(form.operate != 'undefined'){
            this.initFormData(form.operate, form, entryid);
            this.loadFormRecord(form.operate, form, entryid);//最后加载表单条目数据
            return;
        }
        this.initFormData('look', form, entryid);
        this.loadFormRecord('look', form, entryid);//最后加载表单条目数据
    },
    
    initFormData:function(operate, form, entryid, state){
        var nullvalue = new Ext.data.Model();
        var acquisitionform = form.up('acquisitionform');
        var fields = form.getForm().getFields().items;
        var prebtn = form.down('[itemId=preBtn]');
        var nextbtn = form.down('[itemId=nextBtn]');
        var savebtn = acquisitionform.down('[itemId=save]');
        var continuesavebtn = acquisitionform.down('[itemId=continuesave]');
        if(operate == 'modify' || operate == 'look'){
            for(var i=0;i<form.entryids.length;i++){
                if(form.entryids[i]==entryid){
                    count=i+1;
                    break;
                }

            }
            var total = form.entryids.length;
            var totaltext = form.down('[itemId=totalText]');
            totaltext.setText('当前共有  ' + total + '  条，');
            var nowtext = form.down('[itemId=nowText]');
            nowtext.setText('当前记录是第  ' + count + '  条');
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
        var etips = form.up('acquisitionform').down('[itemId=etips]');
        etips.show();
        if(operate!='look'&&operate!='lookfile'){
            /*var settingState = this.ifSettingCorrect(form.nodeid,form.templates);
            if(!settingState){
                return;
            }*/
            Ext.each(fields,function (item) {
                if(!item.freadOnly){
                    item.setReadOnly(false);
                }
            });
        }else{
        	Ext.each(fields,function (item) {
                item.setReadOnly(true);
            });
        }
        var eleview = this.getCurrentAcquisitionform(form).down('electronic');
        var solidview = this.getCurrentAcquisitionform(form).down('solid');
        // var longview = this.getCurrentAcquisitionform(form).down('long');
        if(operate == 'modify'){//修改的时候需要直接加载原文列表，以免保存为0
            eleview.initData(entryid);
        }else{
            eleview.initData();
        }
//        form.formStateChange(operate);
        form.fileLabelStateChange(eleview,operate);
        form.fileLabelStateChange(solidview,operate);
        // form.fileLabelStateChange(longview,operate);
        this.changeBtnStatus(form,operate);
    },

    //加载默认数据或条目数据
    loadFormRecord:function(operate, form, entryid, state){
        var acquisitionform = form.up('acquisitionform');
        var prebtn = form.down('[itemId=preBtn]');
        var nextbtn = form.down('[itemId=nextBtn]');
        var savebtn = acquisitionform.down('[itemId=save]');
        var continuesavebtn = acquisitionform.down('[itemId=continuesave]');
        if(state == '案卷著录' || state == '卷内著录'){
            //设置模板默认值
            var templates=form.templates;
            var defaultData={};
            for(var i=0;i<templates.length;i++){
                if(templates[i].fdefault){//有默认值
                    defaultData[templates[i].fieldcode]=templates[i].fdefault;
                }
            }
            defaultData['descriptionuser']=userRealname;
            defaultData['descriptiondate']=Ext.util.Format.date(new Date(), 'Y-m-d H:i:s');
            if(window.organTitle){//之前有机构名
                defaultData['organ']=window.organTitle;
            }else{//没有时再请求获取
                Ext.Ajax.request({
                    async:false,
                    url: '/nodesetting/findByNodeid/' + form.nodeid,
                    success:function (response) {
                        defaultData['organ']= Ext.decode(response.responseText).data.nodename;
                    }
                });
            }
            //案卷页面选择条目进行卷内著录(没选卷内条目)时要复制案卷的档号字段
            if(state == '卷内著录'){
                var ajEntry= form.ajEntry;
                var codesets=form.codeSets;
                if(ajEntry&&codesets){
                    for(var i=0;i<codesets.length;i++){
                        var codeValue=ajEntry.get(codesets[i]);//案卷的档号字段值
                        if(codeValue){
                            defaultData[codesets[i]]=codeValue;
                        }
                    }
                }
            }
            form.loadRecord({getData: function () {return defaultData;}});
        } else {
            Ext.Ajax.request({
                method: 'GET',
                scope: this,
                url: '/acquisition/entries/' + entryid,
                success: function (response) {
                    var entry = Ext.decode(response.responseText);
                    if(operate == 'insertion'){
                        prebtn.setVisible(false);
                        nextbtn.setVisible(false);
                        this.entryID = entryid;
                        delete entry.entryid;
                    }
                    if(operate == 'lookfile'){
                        prebtn.setVisible(false);
                        nextbtn.setVisible(false);
                        savebtn.setVisible(false);
                        continuesavebtn.setVisible(false);
                    }
                    var data = Ext.decode(response.responseText);
                    if (data.organ) {
                        entry.organ = data.organ;//机构
                    }
                    if(operate == 'add'){
                        delete entry.entryid;
                        entry.filingyear = new Date().getFullYear();
                        entry.descriptiondate = Ext.util.Format.date(new Date(),'Y-m-d H:i:s');
                        if (data.keyword && entry.keyword) {
                            entry.keyword = data.keyword;//主题词
                        }
                        entry.descriptionuser =userRealname;
                    }
                    if (operate == 'add' || operate == 'modify') {
                        if (!data.organ) {
                            if(window.organTitle){//默认前端获取
                                entry.organ=window.organTitle;
                            }else{
                                Ext.Ajax.request({
                                    async:false,
                                    url: '/nodesetting/findByNodeid/' + form.nodeid,
                                    success:function (response) {
                                        entry.organ = Ext.decode(response.responseText).data.nodename;
                                    }
                                });
                            }
                        }
                    }
                    var fieldCode = form.getRangeDateForCode();//字段编号，用于特殊的自定义字段(范围型日期)
                    if (fieldCode != null) {
                        //动态解析数据库日期范围数据并加载至两个datefield中
                        form.initDaterangeContent(entry);
                    }
                    form.loadRecord({getData: function () {return entry;}});
                    if (operate == 'add') {
                        var formValues = form.getValues();
                        var formParams = {};
                        for(var name in formValues){//遍历表单中的所有值
                            formParams[name] = formValues[name];
                        }
                        formParams.nodeid = form.nodeid;
                        formParams.nodename = this.getNodename(form.nodeid);
                        var archive = '';
                        var calFieldName = '';
                        var calValue = '';
                        Ext.Ajax.request({//计算项的数值获取并设置
                            url:form.calurl,//动态URL
                            async:true,
                            params:formParams,
                            success:function(response){
                                var result = Ext.decode(response.responseText).data;
                                if(result){
                                    calFieldName = result.calFieldName;
                                    calValue = result.calValueStr;
                                    archive = result.archive;
                                }
                                var calField = form.getForm().findField(calFieldName);
                                if(calField==null){
                                    return;
                                }
                                calField.setValue(calValue);//设置档号最后一个构成字段的值，填充至文本框中
                                var archiveCode = form.getForm().findField('archivecode');
                                if(archiveCode==null){
                                    return;
                                }
                                archiveCode.setValue(archive);
                            }
                        });
                    }
                }
            });
        }
    },

    //加载新案卷表单
    initNewFileFormData:function(form, entryid,archivecode){
        form.reset();
        var eleview = this.getCurrentAcquisitionform(form).down('electronic');
        var solidview = this.getCurrentAcquisitionform(form).down('solid');
        var filingyearField = form.getForm().findField('filingyear');
        var descriptiondateField = form.getForm().findField('descriptiondate');
        Ext.Ajax.request({
            url: '/acquisition/initNewFileFormData/'+entryid+"/"+archivecode+"/"+form.nodeid,
            async:false,
            method: 'GET',
            success: function (response) {
                var entry = Ext.decode(response.responseText);
                delete entry.entryid;
                form.loadRecord({getData: function () {return entry;}});
                if(filingyearField){
                    filingyearField.setValue(new Date().getFullYear());
                }
                if(descriptiondateField){
                    descriptiondateField.setValue(Ext.util.Format.date(new Date(),'Y-m-d H:i:s'));
                }
                var fieldCode = form.getRangeDateForCode();//字段编号，用于特殊的自定义字段(范围型日期)
                if (fieldCode != null) {
                    //动态解析数据库日期范围数据并加载至两个datefield中
                    form.initDaterangeContent(entry);
                }
            }
        });
        var prebtn = form.down('[itemId=preBtn]');
        var nextbtn = form.down('[itemId=nextBtn]');
        prebtn.setVisible(false);
        nextbtn.setVisible(false);
        eleview.initData();
        solidview.initData();
        form.fileLabelStateChange(eleview,"add");
        form.fileLabelStateChange(solidview,"add");
        this.changeBtnStatus(form,"modify");
    },

    getNodeid:function (parentid) {
        var nodeid;
        var params = {};
        if(typeof(parentid) != 'undefined' && parentid != ''){
            params.parentid = parentid;
        }
        Ext.Ajax.request({
            url:'/publicUtil/getNodeid',
            async:false,
            params:params,
            success:function(response){
                nodeid = Ext.decode(response.responseText).data;
            }
        });
        return nodeid;
    },
    
    getCodesetting: function (nodeid,form){
    	var isExist = false;//档号构成字段的集合
        Ext.Ajax.request({//获得档号构成字段的集合
            url:'/codesetting/getCodeSettingFields',
            async:false,
            params:{
                nodeid:nodeid
            },
            success:function(response){
                var res = Ext.decode(response.responseText);
                var successMsg = res.success;
                if(form){
                    form.codeSets=res.data;//绑定模板
                }
                if (successMsg) {
                    isExist = true;
                }
            }
        });
        return isExist;
    },

    //数据著录
    saveHandler: function (btn) {
        formvisible = true;
        formlayout = 'formgrid';
        var acquisitionform = this.findFormView(btn);
        acquisitionform.down('electronic').operateFlag='add';
        acquisitionform.operateFlag = 'add';
        var grid = this.getGrid(btn);
        var form = acquisitionform.down('dynamicform');
        var tree = this.findGridView(btn).down('treepanel');
        var selectCount;
        var selectAll
        if(grid.selModel != null) {
            selectCount = grid.selModel.getSelection().length;
            selectAll = grid.down('[itemId=selectAll]').checked;
        }
        else {
            selectCount = grid.acrossSelections.length;
        }

        if (selectAll) {
            selectCount = grid.selModel.selected.length;//当前页选中
        }
        var node = tree.selModel.getSelected().items[0];
        if (!node) {//若点击著录时左侧未选中任何节点，则提示选择节点
            XD.msg('请选择节点');
            return;
        }
        var initFormFieldState = this.initFormField(form, 'show', node.get('fnid'));
        form.down('[itemId=preNextPanel]').setVisible(false);
        var codesetting = this.getCodesetting(node.get('fnid'),form);
        var nodename = this.getNodename(node.get('fnid'));
        var info = false;
        if(codesetting){//如果设置了档号字段
        	info = true;
        }else{
        	if (nodename == '未归管理' || nodename == '文件管理' || nodename == '资料管理') {
        		info = true;
        	}
        }
        if(info){
        	if (typeof(initFormFieldState) != 'undefined') {
        		if (selectCount == 0) {
		            this.initFormData('add',form,'','案卷著录');//非卷内文件著录
		            this.activeForm(form);
                    this.loadFormRecord('add',form,'','案卷著录');//最后加载表单条目数据
		        	this.initSouthGrid(form);
		        }else if(selectCount!=1){
		            XD.msg('只能选择一条数据');
		        } else {
                    var entryid;
                    if (selectAll) {
                        entryid = grid.selModel.selected.items[0].get("entryid");
                    } else {
                        if(window.isMedia != true){
                            entryid = grid.selModel.getSelection()[0].get("entryid");
                        }
                        else {
                            entryid = grid.acrossSelections[0].get("entryid");
                        }
                    }
		            //选择数据著录，则加载当前数据到表单界面
		            this.initFormData('add',form, entryid,'案卷数据著录');//数据著录
		            this.activeForm(form);
                    this.loadFormRecord('add',form, entryid,'案卷数据著录');//最后加载表单条目数据
		        	this.initSouthGrid(form);
		        }
		        if(form.operateType){
		            form.operateType = undefined;
		        }
        	}
        }else{
        	XD.msg('请检查档号模板信息是否正确');
        }
    },
    
    getInnerGrid: function(btn) {
    	var grid;
        if (!btn.findParentByType('formAndInnerGrid')) {
        	grid = this.findInnerGrid(btn);
        } else {
        	grid = this.findInnerGridView(btn);
        }
        return grid;
    },

    //卷内文件著录
    isaveHandler:function(btn){
        formvisible = true;
        formlayout = 'forminnergrid';
    	var grid = this.getInnerGrid(btn);
    	var lastGrid = this.findActiveGrid(btn);
    	var count = lastGrid.selModel.getSelection().length;
    	if (count == 0) {
    		XD.msg('请选择一条需要进行卷内文件操作的数据');
    	} else if (count > 1) {
    		XD.msg('只能选择一条数据进行卷内文件操作');
    	} else {
            //比较案卷和卷内的档号设置是否一致，除了卷内文件档号组成字段多出最后一个“卷内顺序号”外，其它都要一致(字段、顺序、分隔符、长度都要一致)
            var res = this.compareCodeset(lastGrid.dataParams.nodeid,grid.dataParams.nodeid);
            var responseText=Ext.decode(res.responseText);
            if (responseText.success==false) {
                XD.msg(responseText.msg);
                return ;
            }
    		var acquisitionform = this.findFormInnerView(btn);
            acquisitionform.down('electronic').operateFlag='add';
	        acquisitionform.operateFlag = 'add';
	        var form = acquisitionform.down('dynamicform');
            //用于案卷点击显示卷内文件条目，之前是根据案卷档号匹配，改为通过entryid获取档号设置，通过档号设置字段匹配
	        var entryid = grid.dataUrl.split("/")[4];
	        var selectCount = grid.selModel.getSelection().length;
	        var nodeid =  grid.dataParams.nodeid;
	        var initFormFieldState = this.initFormField(form, 'show', nodeid);
            form.down('[itemId=preNextPanel]').setVisible(false);
	        var codesetting = this.getCodesetting(nodeid,form);
	        var nodename = this.getNodename(nodeid);
	        var info = false;
	        if(codesetting){//如果设置了档号字段
	        	info = true;
	        }else{
                if (nodename == '未归管理' || nodename == '文件管理' || nodename == '资料管理') {
	        		info = true;
	        	}
	        }
	        if(info){
        		if (typeof(initFormFieldState) != 'undefined') {
		            if (selectCount == 0) {
                        var ajRecord = lastGrid.getSelectionModel().getSelection();
                        form.ajEntry=ajRecord[0];//绑定选择的案卷条目
			            this.initFormData('add',form, lastGrid.selModel.getSelection()[0].get('entryid'), '卷内著录');//卷内著录
			            this.activeInnerForm(form);
                        this.loadFormRecord('add',form, lastGrid.selModel.getSelection()[0].get('entryid'), '卷内著录');//最后加载表单条目数据
		        		this.initSouthInnerGrid(form, entryid);
			        }else if(selectCount!=1){
			            XD.msg('只能选择一条数据')
			        } else {
			        	var initFormFieldState = this.initFormField(form, 'show', nodeid);
				        if(!initFormFieldState){//表单控件加载失败
				            return;
				        }
			            //选择数据著录，则加载当前数据到表单界面
			            this.initFormData('add',form, grid.selModel.getSelection()[0].get('entryid'),'卷内数据著录');//卷内数据著录
			            this.activeInnerForm(form);
                        this.loadFormRecord('add',form, grid.selModel.getSelection()[0].get('entryid'),'卷内数据著录');//最后加载表单条目数据
		        		this.initSouthInnerGrid(form, entryid);
			        }
			        if(form.operateType){
			            form.operateType = undefined;
			        }
        		}
	        }else{
	        	XD.msg('请检查档号模板信息是否正确');
	        }
    	}
    },
    
    modifyHandler: function (btn) {
        formvisible = true;
        formlayout = 'formview';
        var acquisitionform = this.findFormToView(btn);
        acquisitionform.down('electronic').operateFlag='modify';
        acquisitionform.operateFlag = 'modify';
        var grid = this.getGrid(btn);
        var form = acquisitionform.down('dynamicform');
        var records;
        var selectAll;
        if(grid.selModel != null) {
            records = grid.selModel.getSelection();
            selectAll = grid.down('[itemId=selectAll]').checked;
        }
        else
        {
            records = grid.acrossSelections;
        }
        var selectCount = records.length;
        // if (selectAll) {
        //     selectCount = grid.selModel.selected.length;//当前页选中
        // }
        if(selectAll){
            XD.msg('不支持选择所有页修改');
            return;
        }
        var tree = this.findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if (!node) {//若点击修改时左侧未选中任何节点，则提示选择节点
            XD.msg('请选择节点');
            return;
        }
        if (selectCount == 0) {
            XD.msg('请至少选择一条需要修改的数据');
            return;
        }
        var initFormFieldState = this.initFormField(form, 'show', node.get('fnid'));
        var codesetting = this.getCodesetting(node.get('fnid'),form);
        var nodename = this.getNodename(node.get('fnid'));
        var info = false;
        if(codesetting){//如果设置了档号字段
        	info = true;
        }else{
            if (nodename == '未归管理' || nodename == '文件管理' || nodename == '资料管理') {
        		info = true;
        	}
        }
        if(info){
        	if (typeof(initFormFieldState) != 'undefined') {
                // var entryid;
                // if (selectAll) {
                //     entryid = grid.selModel.selected.items[0].get("entryid");
                // } else {
                //     entryid = records[0].get("entryid");
                // }
                var entryids = [];
                for(var i=0;i<records.length;i++){
                    entryids.push(records[i].get('entryid'));
                }
                form.operate = 'modify';
                form.entryids = entryids;
                form.entryid = entryids[0];
	            this.initFormData('modify', form, entryids[0]);
		        this.activeToForm(form);
                this.loadFormRecord('modify',form, entryids[0]);//最后加载表单条目数据
		        if(form.operateType){
		            form.operateType = undefined;
		        }
        	}
        }else{
        	XD.msg('请检查档号模板信息是否正确');
        }
    },

    imodifyHandler: function (btn) {
        formvisible = true;
        formlayout = 'formview';
        var acquisitionform = this.findFormToView(btn);
        acquisitionform.down('electronic').operateFlag='modify';
        acquisitionform.operateFlag = 'modify';
        var grid = this.getInnerGrid(btn);
        var lastGrid = this.findActiveGrid(btn);
        var form = acquisitionform.down('dynamicform');
        var nodeid = grid.dataParams.nodeid;
        var records = grid.selModel.getSelection();
        if (records.length == 0) {
            XD.msg('请至少选择一条需要修改的数据');
            return;
        }
        //比较案卷和卷内的档号设置是否一致，除了卷内文件档号组成字段多出最后一个“卷内顺序号”外，其它都要一致(字段、顺序、分隔符、长度都要一致)
        var res = this.compareCodeset(lastGrid.dataParams.nodeid,grid.dataParams.nodeid);
        var responseText=Ext.decode(res.responseText);
        if (responseText.success==false) {
            XD.msg(responseText.msg);
            return ;
        }
        var initFormFieldState = this.initFormField(form, 'show', nodeid);
        var codesetting = this.getCodesetting(nodeid,form);
        var nodename = this.getNodename(nodeid);
        var info = false;
        if(codesetting){//如果设置了档号字段
        	info = true;
        }else{
            if (nodename == '未归管理' || nodename == '文件管理' || nodename == '资料管理') {
        		info = true;
        	}
        }
        if(info){
        	if (typeof(initFormFieldState) != 'undefined') {
                var entryids = [];
                for(var i=0;i<records.length;i++){
                    entryids.push(records[i].get('entryid'));
                }
                form.operate = 'modify';
                form.entryids = entryids;
                form.entryid = entryids[0];
	            this.initFormData('modify',form, entryids[0]);
		        this.activeToForm(form);
                this.loadFormRecord('modify',form, entryids[0]);//最后加载表单条目数据
		        if(form.operateType){
		            form.operateType = undefined;
		        }
        	}
        }else{
        	XD.msg('请检查档号模板信息是否正确');
        }
    },

    delHandler: function (btn) {
        var grid = this.getGrid(btn);
        var selectAll;
        var selLen;
        if(grid.selModel != null){
            selectAll=grid.down('[itemId=selectAll]').checked;
            if(grid.selModel.getSelectionLength() == 0){
                XD.msg('请至少选择一条需要删除的数据');
                return;
            }
            selLen = grid.selModel.getSelectionLength()
        }
        else {
            if(grid.acrossSelections.length == 0){
                XD.msg('请至少选择一条需要删除的数据');
                return;
            }
            selLen = grid.acrossSelections.length;
        }
        var tree = this.findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if (!node) {
            XD.msg('请选择节点');
            return;
        }
        XD.confirm('确定要删除这' + selLen + '条数据吗',function(){
            Ext.MessageBox.wait('正在删除数据...','提示');
            var record;
            if(grid.selModel != null) {
                record = grid.selModel.getSelection()
            }
            else {
                record = grid.acrossSelections;
                grid = grid.down('dataview');
            }
            var isSelectAll = false;
            if(selectAll){
                record = grid.acrossDeSelections;
                isSelectAll = true;
            }
            var tmp = [];
            for (var i = 0; i < record.length; i++) {
                tmp.push(record[i].get('entryid'));
            }
            var entryids = tmp.join(",");
            grid.getStore().proxy.url='/acquisition/entriesPost';
            var tempParams = grid.getStore().proxy.extraParams;
            tempParams['entryids'] = entryids;
            tempParams['isSelectAll'] = isSelectAll;
            tempParams['model'] = "数据采集:"+grid.nodefullname;
            Ext.Msg.wait('正在删除数据，请耐心等待……', '正在操作');
            Ext.Ajax.request({
                method: 'post',
                scope: this,
                url: '/acquisition/delete',
                params:tempParams,
                timeout:XD.timeout,
                success: function (response, opts) {
                    XD.msg(Ext.decode(response.responseText).msg);
                    grid.getStore().proxy.extraParams.entryids='';
                    if(window.isMedia != true){
                        grid.delReload(grid.selModel.getSelectionLength());
                        this.findInnerGrid(btn).getStore().removeAll();
                        this.findInnerGrid(btn).setTitle('查看卷内');
                    }
                    else{
                        grid.up('mediaItemsDataView').initGrid({nodeid: node.data.fnid});
                    }
//                    grid.initGrid({nodeid: node.data.fnid});//刷新整个数据管理列表以及下面的数据显示
                    this.findInnerGrid(btn).getStore().removeAll();
                    this.findInnerGrid(btn).setTitle('查看卷内');
                    Ext.MessageBox.hide();
                },
                failure : function() {
                    Ext.MessageBox.hide();
                    XD.msg('操作失败');
                }
            })
        },this);
    },

    idelHandler: function (btn) {
        var grid = this.getInnerGrid(btn);
        var record = grid.selModel.getSelection();
        if (record.length == 0) {
            XD.msg('请至少选择一条需要删除的数据');
            return;
        }
        XD.confirm('确定要删除这' + record.length + '条数据吗',function(){
            var tmp = [];
            for (var i = 0; i < record.length; i++) {
                tmp.push(record[i].get('entryid'));
            }
            var entryids = tmp.join(",");
            var tempParams = grid.getStore().proxy.extraParams;
            tempParams['entryids'] = entryids;
            tempParams['isSelectAll'] = false;
            tempParams['model'] = "数据采集:"+grid.nodefullname;
            Ext.Msg.wait('正在删除数据，请耐心等待……', '正在操作');
            Ext.Ajax.request({
                method: 'post',
                url: '/acquisition/delete',
                params:tempParams,
                success: function (response, opts) {
                    XD.msg(Ext.decode(response.responseText).msg);
                    grid.delReload(record.length);
//                    grid.initGrid({nodeid: nodeid});//刷新整个数据管理列表以及下面的数据显示
                    Ext.MessageBox.hide();
                }
            })
        },this);
    },

    lookHandler: function (btn) {
        var grid = this.getGrid(btn);
        var form = this.findDfView(btn);
        var records;
        var selectAll
        if( grid.selModel != null) {
            records = grid.selModel.getSelection();
            selectAll = grid.down('[itemId=selectAll]').checked;
        }
        else {
            records = grid.acrossSelections;
        }
        var selectCount = records.length;
        if(selectAll){
            XD.msg('不支持选择所有页查看');
            return;
        }
        // if (selectAll) {
        //     selectCount = grid.selModel.selected.length;//当前页选中
        // }
        var tree = this.findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if (!node) {
            XD.msg('请选择节点');
            return;
        }
        // if (selectCount == 0) {
        //     if(selectAll){
        //         XD.msg('当前页没有选择数据');
        //     }else{
        //         XD.msg('请选择数据');
        //     }
        //     return;
        // }
        // if(selectCount != 1){
        //     XD.msg('查看只能选中一条数据');
        //     return;
        // }
        // var entryid;
        // if (selectAll) {
        //     entryid = grid.selModel.selected.items[0].get("entryid");
        // } else {
        //     entryid = records[0].get("entryid");
        // }
        if(selectCount == 0){
            XD.msg('请至少选择一条需要查看的数据');
            return;
        }
        var entryids = [];
        for(var i=0;i<records.length;i++){
            entryids.push(records[i].get('entryid'));
        }
        var initFormFieldState = this.initFormField(form, 'hide', node.get('fnid'));
        if(!initFormFieldState){//表单控件加载失败
            return;
        }
        form.operate = 'look';
        form.entryids = entryids;
        form.entryid = entryids[0];
        this.initFormData('look',form, entryids[0]);
        this.activeToForm(form);
        this.loadFormRecord('look',form, entryids[0]);//最后加载表单条目数据
    },

    ilookHandler: function (btn) {
        var grid = this.getInnerGrid(btn);
        var form = this.findDfView(btn);
        var records = grid.selModel.getSelection();
        if(records.length == 0){
            XD.msg('请选择数据');
            return;
        }
        var nodeid = grid.dataParams.nodeid;
        // if (records.length != 1) {
        //     XD.msg('查看只能选中一条数据');
        //     return;
        // }
        // var entryid = records[0].get('entryid');
        var initFormFieldState = this.initFormField(form, 'hide', nodeid);
        if(!initFormFieldState){//表单控件加载失败
            return;
        }
        var entryids = [];
        for(var i=0;i<records.length;i++){
            entryids.push(records[i].get('entryid'));
        }
        form.operate = 'look';
        form.entryids = entryids;
        form.entryid = entryids[0];
        this.initFormData('look',form, entryids[0]);
        this.activeToForm(form);
        this.loadFormRecord('look',form, entryids[0]);//最后加载表单条目数据
    },

    ilookfileHandler:function (btn) {
        var grid = this.getGrid(btn);
        var form = this.findDfView(btn);
        var records;
        var selectAll;
        if(grid.selModel != null ) {
            records = grid.selModel.getSelection();
            selectAll = grid.down('[itemId=selectAll]').checked;
        }
        else {
            records = grid.acrossSelections;
        }

        var selectCount = records.length;
        if (selectAll) {
            selectCount = grid.selModel.selected.length;//当前页选中
        }
        if (selectCount == 0) {
            if(selectAll){
                XD.msg('当前页没有选择数据');
            }else{
                XD.msg('请选择一条数据');
            }
            return;
        }
        if(selectCount != 1){
            XD.msg('查看只能选中一条数据');
            return;
        }
        var entryid = records[0].get('entryid');
        var nodeid;
        Ext.Ajax.request({
            url: '/acquisition/fileNodeidAndEntryid',
            async:false,
            params:{
                entryid:entryid
            },
            success: function (response) {
                var data = Ext.decode(response.responseText);
                entryid = data.entryid;
                nodeid = data.nodeid;
            },
            failure: function (response) {
                entryid='';
            }
        });
        //如果entryid为空字符串，即找不到案卷
        if(entryid == ''){
            XD.msg('无对应的案卷记录，请编制新案卷后再进行查看');
            return;
        }
        var entryids = [];
        entryids.push(entryid);
        form.entryids = entryids;
        var initFormFieldState = this.initFormField(form, 'hide', nodeid);
        if(!initFormFieldState){//表单控件加载失败
            return;
        }
        this.initFormData('lookfile',form, entryid);
        this.activeToForm(form);
        this.loadFormRecord('lookfile',form, entryid);//最后加载表单条目数据
    },

    //打开报表显示列表
    printHandler:function(btn){
        var grid = this.getGrid(btn);
        var selectAll=grid.down('[itemId=selectAll]').checked;
        var ids = [];
        if(selectAll){
            var tempParams = grid.getStore().proxy.extraParams;
            Ext.Ajax.request({
                async:false,
                url: '/acquisition/getSelectAllEntryid',
                params:tempParams,
                success:function (response) {
                    var records = Ext.decode(response.responseText);
                    if (grid.acrossDeSelections.length > 0) {
                        //获取取消选择的条目
                        var cancles=[];
                        for(var i = 0; i < grid.acrossDeSelections.length; i++){
                            cancles.push(grid.acrossDeSelections[i].get('entryid'))
                        }
                        if(cancles.length>0){
                            var strCancles =cancles.join(',');
                            //遍历总条目，获取取消选择的条目中不包含遍历的条目
                            for(var i = 0; i < records.length; i++){
                                if(strCancles.indexOf(records[i])==-1){
                                    ids.push(records[i]);
                                }
                            }
                        }else{
                            ids = records;
                        }
                    }else{
                        ids = records;
                    }
                }
            });
        }else{
            Ext.each(grid.getSelectionModel().getSelection(),function(){
                ids.push(this.get('entryid'));
            });
        }
        if(ids.length==0){
            XD.msg('请至少选择一条需要打印的数据');
            return;
        }
        var reportGridWin = Ext.create('Ext.window.Window',{
            width:'100%',
            height:'100%',
            header:false,
            draggable : false,//禁止拖动
            resizable : false,//禁止缩放
            modal:true,
            closeToolText:'关闭',
            layout:'fit',
            items:[{
                xtype: 'acquisitionReportGridView',
                entryids:ids,
                nodeid:grid.nodeid
            }]
        });
        var reportGrid = reportGridWin.down('acquisitionReportGridView');
        reportGrid.initGrid({nodeid:reportGrid.nodeid});
        reportGridWin.show();
    },

    getUpload:function (view,uploadView) {
        var store = uploadView.down('grid').getStore();
        for (var i = 0; i < store.getCount(); i++) {
            var data = store.getAt(i).data;
            if (data.progress == 1) {
                view.down('treepanel').getRootNode().appendChild({
                    fnid: data.eleid,
                    text: data.name,
                    checked: false,
                    leaf: true
                });
            }
        }
    },
    
    //数据转移
    dataTransforHandler:function (view) {
    	var record = AcFormAndGridView.down('acquisitiongrid').selModel.selected;
    	if (record.length < 1) {
    		XD.msg('请至少选择一条数据进行数据转移！');
    		return;
    	}
    	var form = Ext.create("Acquisition.view.AcquisitionSelectWin",{height:window.innerHeight * 6 / 7,width:420});
    	var treeView = form.down("acquisitionSelectView");
    	
    	var tempParams = treeView.getStore().proxy.extraParams;
        tempParams['nodeinfo'] = '';
    	
        treeView.getStore().load();
        treeView.on('load', function () {
            treeView.expandAll();//展开全部节点
        });
        form.title = '目的节点';
    	form.show();
    },
    
    //数据转移 - 字段设置
    transforSetFieldHandler:function (btn) {
    	acquisitionSelectWin = btn.up('acquisitionSelectWin');
    	var nodeid = AcFormAndGridView.down('acquisitiongrid').dataParams.nodeid;
    	
    	if (typeof(targetNodeid) == 'undefined') {
    	    XD.msg('请选择目的节点！');
    		return;
    	}
    	Ext.Ajax.request({
        	method: 'POST',
            url: '/template/getFieldInfo',
            params: {
            	nodeid: nodeid,
            	targetNodeid: targetNodeid
            },
            success: function (resp) {
            	var info = Ext.decode(resp.responseText);
            	if (info != null) {
            		var fieldView = Ext.create('Ext.window.Window', {
			            width: '35%',
						height: '75%',
			            header: false,
			            modal: true,
			            draggable: true,//禁止拖动
						resizable: true,//禁止缩放
			            closeToolText: '关闭',
			            layout: 'fit',
			            items: [{
			                xtype: 'acquisitionFieldView'
			            }]
			        });
			        fieldView.show();
			        var acquisitionFieldView = fieldView.down('acquisitionFieldView');
			        acquisitionFieldView.nodeid = nodeid;
	                //提交成功后，刷新字段设置
	                var workspace = acquisitionFieldView.down('[itemId=workspace]');
	                var fieldstore = workspace.down('[itemId=fieldgrid]').getStore();
	                fieldstore.removeAll();
	                for (var i = 0; i < info.length; i++) {
	                    fieldstore.add({
	                    	fieldName: info[i].fieldName, 
	                    	fieldCode: info[i].fieldCode,
	                    	targetFieldName: info[i].targetFieldName,
	                    	targetFieldCode: info[i].targetFieldCode
	                    });
	                }
        			fieldView.show();
            	} else {
            		XD.msg("当前节点不能进行数据转移操作！");
            		return;
            	}
            }
    	})
    },

    //数据转移 - 关闭
    transforCloseHandler:function (btn) {
    	// 关闭数据转移窗口
        btn.up('window').hide();
    },
    
    //数据转移 - 提交
    transforSubmitHandler: function (btn) {
    	var grid = AcFormAndGridView.down('acquisitiongrid');
    	//查找到字段设置视图
		var acquisitionFieldView = btn.up('acquisitionFieldView');
		
    	var fieldCodes = "";
    	var targetFieldCodes = "";
		var field = acquisitionFieldView.down('[dataIndex=fieldName]');
		var fieldValues = field.config.$initParent.view.store.data.items;
    	for (var i = 0; i < fieldValues.length; i++) {
			if (i < fieldValues.length - 1) {
				fieldCodes += fieldValues[i].data.fieldCode + ",";
				targetFieldCodes += fieldValues[i].data.targetFieldCode + ",";
			} else {
				fieldCodes += fieldValues[i].data.fieldCode;
				targetFieldCodes += fieldValues[i].data.targetFieldCode;
			}
		}
		var condition = grid.down('[itemId=condition]').value;
		var operator = grid.down('[itemId=operator]').value;
		var content = grid.down('[itemId=value]').value;
		var isSelectAll = grid.down('[itemId=selectAll]').checked;
		var record = "";
		if (isSelectAll) {
			record = grid.acrossDeSelections;
		} else {
			record = grid.getSelectionModel().getSelection();
		}
		var entryid = "";
		for (var i = 0; i < record.length; i++) {
    		if (i < record.length - 1) {
				entryid += record[i].id + ",";
			} else {
				entryid += record[i].id;
			}
    	}
		Ext.Msg.wait('正在进行数据转移，请耐心等待……', '正在操作');
    	Ext.Ajax.request({
        	method: 'POST',
            url: '/transfor/captureEntriesTransfer',
            timeout:XD.timeout,
            params: {
            	nodeid: acquisitionFieldView.nodeid,
            	targetNodeid: targetNodeid,
            	fieldCodes: fieldCodes,
            	targetFieldCodes: targetFieldCodes,
            	entryids: entryid,
            	isSelectAll: isSelectAll,
            	condition: condition,
            	operator: operator,
            	content: content,
            	type: ''
            },
            success: function (resp) {
            	Ext.MessageBox.hide();
            	var respText = Ext.decode(resp.responseText);
            	// “XX条数据从【节点名称】转移到【节点名称】”提示语
            	XD.msg(respText.msg);
            	// 关闭字段设置窗口
            	btn.up('window').hide();
                // 关闭数据转移窗口
            	acquisitionSelectWin.hide();
        		// 刷新数据管理表单
        		grid.initGrid();
            },
            failure: function () {
                XD.msg("操作中断");
            }
        });
    },

    //监听键盘按下事件
    addKeyAction:function (view) {
        var controller = this;
        var currentView;
        document.onkeydown = function () {
            if(formlayout == 'formgrid'){
                currentView = view.up('acquisitionFormAndGrid').down('formAndGrid').down('acquisitionform');
            }else if(formlayout == 'forminnergrid'){
                currentView = view.up('acquisitionFormAndGrid').down('formAndInnerGrid').down('acquisitionform');
            }else if(formlayout == 'formview'){
            	currentView = view.up('acquisitionFormAndGrid').down('formView').down('acquisitionform');
            }
            var uploadView = window.UploadView;
            if(typeof currentView !='undefined'){
                var eleview = currentView.down('electronic');
            }
            if(!view.saveBtn && currentView){
                view.saveBtn = currentView.down('[itemId=save]');
                view.continueSaveBtn = currentView.down('[itemId=continuesave]');
                view.operateFlag = currentView.operateFlag;
            }
            var oEvent = window.event;
            if (oEvent.ctrlKey && oEvent.shiftKey && !oEvent.altKey && oEvent.keyCode == 83) { //这里只能用alt，shift，ctrl等去组合其他键event.altKey、event.ctrlKey、event.shiftKey 属性
                // XD.msg('Ctrl+Shift+S');
                Ext.defer(function () {
                    if(view.continueSaveBtn && view.operateFlag=='add' && formvisible){//此处增加operateFlag判断的目的是：屏蔽修改界面连续录入快捷键功能
                        if(uploadView){
                            controller.getUpload(eleview,uploadView);
                            uploadView.close();
                        }
                        controller.continueSubmitForm(view.continueSaveBtn);//连续录入
                    }
                },1);
                event.returnValue = false;//阻止event的默认行为
            }
            if (oEvent.ctrlKey && !oEvent.shiftKey && !oEvent.altKey && oEvent.keyCode == 83) { //这里只能用alt，shift，ctrl等去组合其他键event.altKey、event.ctrlKey、event.shiftKey 属性
                // XD.msg('Ctrl+S');
                Ext.defer(function () {
                    if(view.saveBtn && formvisible){
                        if(uploadView){
                            controller.getUpload(eleview,uploadView);
                            uploadView.close();
                        }
                        controller.submitForm(view.saveBtn);//保存
                    }
                },1);
                event.returnValue = false;//阻止event的默认行为
                // return false;//阻止event的默认行为
            }
        }
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

    //保存表单数据，返回列表界面视图
    submitForm: function (btn) {
        var currentAcquisitionform = this.getCurrentAcquisitionform(btn);
        var eleids = currentAcquisitionform.down('electronic').getEleids();
        var formview = currentAcquisitionform.down('dynamicform');
        var fieldCode = formview.getRangeDateForCode();//字段编号，用于特殊的自定义字段(范围型日期)
        var nodename = this.getNodename(formview.nodeid);
        if(nodename=='未归管理'){  //未归管理下，自动获取页数
            var pages = formview.down('[name=pages]');
            if(pages){
                if(pages.getValue()==""||(pages.getValue()=="0"&&eleids!="")){
                    Ext.Ajax.request({
                        method: 'POST',
                        scope: this,
                        url: "/electronic/saveSetPages",
                        async:false,
                        params: {
                            eleids: eleids,
                            entrytype:"capture"
                        },
                        success: function (response) {
                            var data = Ext.decode(response.responseText).data;
                            pages.setValue(data);
                        },
                        failure: function () {
                            XD.msg("自动获取页数失败");
                        }
                    })
                }
            }
        }
        if(!formview.isValid()){//表单必填项验证
            var fieldObject = formview.getForm().getValues();
            for(var key in fieldObject){
                if(fieldObject.hasOwnProperty(key)){
                    if(formview.getForm().findField(key).allowBlank==false&&fieldObject[key]==''){
                        formview.getForm().findField(key).focus(true, 100);
                        break;
                    }
                }
            }
            XD.msg('有必填项未填写');
            return;
        }
        var params = {
            nodeid: formview.nodeid,
            eleid: eleids,
            type: currentAcquisitionform.operateFlag,
            operate: nodename
        };
        if (fieldCode != null) {
            params[fieldCode]=formview.getDaterangeValue();
        }
        var archivecodeSetState = formview.setArchivecodeValueWithNode(nodename);
        if(!archivecodeSetState){//若档号设置失败，则停止后续的表单提交
            return;
        }
        var operateType = currentAcquisitionform.operateFlag;
        var submitType = formview.submitType;
        var formview_operate = formview.operate;
        Ext.MessageBox.wait('正在保存请稍后...','提示');
        formview.submit({
            method: 'POST',
            url: '/acquisition/entries',
            params: params,
            scope: this,
            success: function (form, action) {
                Ext.MessageBox.hide();
                var treepanel = this.findTreeView(btn);
                var nodeid = treepanel.selModel.getSelected().items[0].get('fnid');
                if(action.result.success==true){
                    if(operateType=='insertion'){
                        var pages = action.result.data.pages;
                        var state = this.updateSubsequentData(this.entryID,submitType,pages);
                        //切换到列表界面,同时刷新列表数据(判断树节点nodeid是否和表单指定的nodeid)
                        if(formview.nodeid != nodeid){
                            this.activeGrid(btn, false);
                            this.findInnerGrid(btn).getStore().reload();
                        }else{
                            this.activeGrid(btn,true);
                            this.findInnerGrid(btn).getStore().removeAll();
                        }
                        if(!state){
                            return;//保存条目成功，但插件后更新后续数据计算项及档号失败
                        }
                        //XD.msg(action.result.msg);//避免两个提示同时出现
                    }
                    //多条时切换到下一条。单条时或最后一条时切换到列表界面,同时刷新列表数据
                    if(formview.entryids && formview.entryids.length > 1 && formview.entryid != formview.entryids[formview.entryids.length-1]){
                        var allMediaFrame = document.querySelectorAll('#mediaFrame');
                        if(allMediaFrame){
                            for (var i = 0; i < allMediaFrame.length; i++) {
                                allMediaFrame[i].setAttribute('src','');
                            }
                        }
                        this.refreshFormData(formview, 'next');
                    }else {
                        if (formview.nodeid != nodeid) {
                            this.activeGrid(btn, false);
                            this.findInnerGrid(btn).getStore().reload();
                        } else {
                            this.activeGrid(btn, true);
                            this.findInnerGrid(btn).getStore().removeAll();
                        }
                    }
                    XD.msg(action.result.msg);
                    var entryids = [action.result.data.entryid];
                    //进行采集业务元数据
                    var operation = "著录";
                    if (operateType == "modify"){
                        operation = "修改";
                    }
                    captureServiceMetadataByZL(entryids,'数据采集',operation);
                }
            },
            failure: function (form, action) {
            	Ext.MessageBox.hide();
            	XD.msg(action.result.msg);
            }
        });
        if(formview.operateType){
            formview.operateType = undefined;
        }
    },

    //连续保存
    continueSubmitForm: function (btn) {
        var currentAcquisitionform = this.getCurrentAcquisitionform(btn);
        var solidview = currentAcquisitionform.down('solid');
        var eleidview=currentAcquisitionform.down('electronic').down('treepanel');
        var eleids;
        var formview = currentAcquisitionform.down('dynamicform');
        if (formview.operate == "modify"){
            eleids = currentAcquisitionform.down('electronic').getEleids();
        }else{
            eleids = currentAcquisitionform.down('electronic').getEleidsNoEntryid().join();
        }
        if(!formview.isValid()){//表单必填项验证
            var fieldObject = formview.getForm().getValues();
            for(var key in fieldObject){
                if(fieldObject.hasOwnProperty(key)){
                    if(formview.getForm().findField(key).allowBlank==false&&fieldObject[key]==''){
                        formview.getForm().findField(key).focus(true, 100);
                        break;
                    }
                }
            }
            XD.msg('有必填项未填写');
            return;
        }
        var fieldCode = formview.getRangeDateForCode();//字段编号，用于特殊的自定义字段(范围型日期)
        var nodename = this.getNodename(formview.nodeid);
        var params={
            nodeid: formview.nodeid,
            eleid:eleids,
            type: currentAcquisitionform.operateFlag,
            operate: nodename
        };
        if (fieldCode != null) {
            params[fieldCode]=formview.getDaterangeValue();
        }
        var archivecodeSetState = formview.setArchivecodeValueWithNode(nodename);
        if(!archivecodeSetState){//若档号设置失败，则停止后续的表单提交
            return;
        }
        Ext.MessageBox.wait('正在保存请稍后...','提示');
        formview.submit({
            method: 'POST',
            url: '/acquisition/entries',
            params: params,
            scope: this,
            success: function (form, action) {
                Ext.MessageBox.hide();
                //每次点击连续著录时刷新表单下面列表
                var grid = formview.findParentByType('panel').findParentByType('panel').down('grid');
                grid.getStore().reload();
                this.findFormView(btn).down('electronic').initData();
                this.findFormView(btn).down('solid').initData();
                // this.findFormView(btn).down('long').initData();
                //this.addCalValue(formview);
                if(archivecodeSetState!='无档号节点'){
                    formview.setArchivecodeValueWithNode(nodename);
                }

                if(formview.nodeid != nodeid){//卷内
                    var grid = formview.findParentByType('panel').findParentByType('panel').down('grid');
                    this.findFormInnerView(btn).down('electronic').initData(undefined,formview.nodeid);
                    this.findFormInnerView(btn).down('solid').initData(undefined,formview.nodeid);
                    grid.getStore().reload();
                }else {//案卷
                    // this.findActiveGrid(btn).getStore().reload();
                    this.findGridToView(btn).getStore().reload();
                }
                var entryid;
                this.findFormView(btn).down('electronic').initData(entryid,formview.nodeid);
                this.findFormView(btn).down('solid').initData(entryid,formview.nodeid);
                // this.findFormView(btn).down('long').initData();
                if(action.result&&action.result.data&&action.result.data.archivecode){//未归节点档号可能为空
                    this.addCalValue(formview,action.result.data.archivecode,nodename);
                }else{
                    this.addCalValue(formview,'',nodename);
                }

                XD.msg(action.result.msg);
                //点击连续录入后，遍历表单中所有控件，将光标移动至第一个非隐藏且非只读的控件
                var fields = form.getFields().items;
                for(var i=0;i<fields.length;i++){
                    if(fields[i].xtype!='hidden' && fields[i].xtype!='displayfield' && fields[i].readOnly==false){
                        fields[i].focus(true);
                        if(fields[i].getValue()!=null){
                            fields[i].selectText(0,fields[i].getValue().length);
                            /*Ext.defer(function () {
                                fields[i].selectText(0,fields[i].getValue().length);
                            },1);*/
                        }
                        break;
                    }
                }
                eleidview.getStore().reload();//刷新原始文件列表
                var allMediaFrame = document.querySelectorAll('#mediaFrame');
                if(allMediaFrame){
                    for (var i = 0; i < allMediaFrame.length; i++) {
                        allMediaFrame[i].setAttribute('src','');
                    }
                }
                this.findInnerGrid(btn).getStore().reload();
                //进行采集业务元数据
                var captureService_entryid = [action.result.data.entryid];
                if("modify"==formview.operate){
                    captureServiceMetadataByZL(captureService_entryid,'数据采集','编辑');
                }else {
                    captureServiceMetadataByZL(captureService_entryid,'数据采集','著录');
                }
            },
            failure: function (form, action) {
            	Ext.MessageBox.hide();
            	XD.msg(action.result.msg);
            }
        });
    },

    getCurrentAcquisitionform:function (btn) {
        if (btn.up('formAndGrid')) {//如果是案卷表单
            return this.findFormView(btn);
        }
        if (btn.up('formAndInnerGrid')){//如果是卷内表单
            return this.findFormInnerView(btn);
        }
        if (btn.up('formView') || btn.xtype == 'entrygrid' || btn.xtype == 'acquisitiongrid' || btn.up('acquisitionTransdocView')) {
        	return this.findFormToView(btn);
        }
    },
    
    getGrid: function (btn) {
    	var grid;
        if (!btn.findParentByType('formAndGrid')) {
        	grid = this.findActiveGrid(btn);
        } else {
        	grid = this.findGridToView(btn);
        }
        return grid;
    },//页数矫正处理方法
    pageNumberCorrectHandler:function(btn){
        var grid = this.getGrid(btn);
        var records = grid.getSelectionModel().getSelection();
        var selectCount = grid.getSelectionModel().getSelectionLength();
        if(selectCount==0){
            XD.msg('请选择数据');
            return;
        }
        var selectAll=grid.down('[itemId=selectAll]').checked;
        var isSelectAll = false;
        if(selectAll){
            records = grid.acrossDeSelections;
            isSelectAll = true;
        }
        var tmp = [];
        for(var i = 0; i < records.length; i++){
            tmp.push(records[i].get('entryid'));
        }
        var tempParams = grid.getStore().proxy.extraParams;
        tempParams['entryids'] = tmp.join('、');
        tempParams['isSelectAll'] = isSelectAll;
        XD.confirm('页数矫正操作会替换当前页数，请确认是否执行', function () {
            Ext.Msg.wait('正在进行页数矫正，请耐心等待……', '正在操作');
            Ext.Ajax.request({
                url: '/acquisition/pgNumCorrect',
                params: tempParams,
                method: 'POST',
                success: function (resp) {
                    var respText = Ext.decode(resp.responseText);
                    Ext.Msg.wait('操作成功!','正在操作').hide();
                    XD.msg(respText.msg);
                    grid.down('[itemId=selectAll]').setValue(false);
                    grid.getStore().reload();
                },
                failure: function (resp) {
                    var respText = Ext.decode(resp.responseText);
                    XD.msg(respText.msg);
                    grid.down('[itemId=selectAll]').setValue(false);
                    Ext.Msg.wait('操作失败!','正在操作').hide();
                    grid.getStore().reload();
                },
                scope: this
            });
        });
    },

    updateSubsequentData:function (entryid,operate,pages) {
        var state;
        var params = {
            entryid:entryid,
            flag:operate
        };
        if(pages!=undefined){
            params.pages = pages;
        }
        Ext.Ajax.request({
            url: '/acquisition/updateSubsequentData',
            async:false,
            params:params,
            success: function (resp) {
                var resp = Ext.decode(resp.responseText);
                if(resp.success==false){
                    Ext.MessageBox.alert("提示信息", resp.msg, function(){
                    });
                }else{
                    state = '更新后续数据成功';
                }
            }
        });
        return state;
    },
    objectToStringArray:function (obj) {
        var result = [];
        var keys = Object.keys(obj);
        var values = [];
        for(var i=0;i<keys.length;i++){
            values[i] = obj[keys[i]];//得到每个字段对应的值
            result[i] = keys[i]+'∪'+values[i];//将键和值拼接成以∪隔开的字符串
        }
        return result;
    },
    //统计项更新
    statisticUpdateHandler: function(btn) {
        var grid = this.getGrid(btn);
        var innerGrid = this.findInnerGrid(btn);
        var record = grid.selModel.getSelection();
        var tree = this.findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        var ids = [];
        Ext.each(grid.getSelectionModel().getSelection(),function(){
            ids.push(this.get('entryid'));
        });
        if (!node) {
            XD.msg('请选择节点');
            return;
        }
        if (record.length == 0) {
            XD.msg('请选择一条需要操作的数据!');
            return;
        }
        if(record.length >1){
            XD.msg('只能选中一条数据');
            return;
        }
        XD.confirm('统计项更新操作将替换当前案卷条目的卷内文件数和卷内文件页数，请确认是否执行', function () {
            Ext.Msg.wait('正在进行统计项更新，请耐心等待……', '正在操作');
            Ext.Ajax.request({
                url: '/acquisition/statisticUpdate',
                params: {
                    entryids: ids
                },
                method: 'POST',
                success: function (resp) {
                    var respText = Ext.decode(resp.responseText);
                    Ext.Msg.wait('操作成功!','正在操作').hide();
                    XD.msg(respText.msg);
                    grid.getStore().reload();
                    innerGrid.getStore().removeAll();
                },
                failure: function (resp) {
                    var respText = Ext.decode(resp.responseText);
                    XD.msg(respText.msg);
                    Ext.Msg.wait('操作失败!','正在操作').hide();
                    grid.getStore().reload();
                    innerGrid.getStore().removeAll();
                },
                scope: this
            });
        });
    },
    /**
     ** 根据后台获取权限按钮，并显示在列表中，根据分类类型，专门屏蔽或显示某些按钮（classlevel即分类类型）
     * 一开始将所有特殊按钮都隐藏，再根据分类类型，把相对应的按钮显示出来
     * 目前0-无,1-卷内文件,2-案卷管理,3-未归管理,4-已归管理,5-资料管理,6-文本管理
     * 0(无) - 全部按钮不显示
     * 1(卷内文件) - 显示拆、插件、查看案卷、页数矫正、编辑新案卷、调序按钮
     * 2(案卷管理) - 显示拆、插卷(即拆插件更改名称为拆插卷)、统计项更新按钮
     * 3(未归管理) - 显示归档按钮、分类设置按钮
     * 4(已归管理) - 显示拆、插件按钮
     * 5(资料管理) - 显示分类管理按钮
     * 6(文本管理) - 显示分类管理按钮
     * @param classlevel 分类类型
     * @param grid 主页grid
     * @param gridview 著录表单下方的grid
     */
    refreshToolbarBtn: function (classlevel,grid,gridview) {
        //主页grid按钮
        var buttons = grid.down("toolbar").query('button');
        //主页grid按钮间的分隔符
        var tbseparator = grid.down("toolbar").query('tbseparator');
        //著录表单下方的gridgrid按钮
        var btns = gridview.down("toolbar").query('button');
        //著录表单下方的gridgrid按钮间的分隔符
        var tbs = gridview.down("toolbar").query('tbseparator');
        //当classlevel==0时，即无分类类型，需要将所有按钮都隐藏
        if (classlevel == 0) {
            grid.down("toolbar").hide();
            gridview.down("toolbar").hide();
            this.findView(grid).down('[itemId=southgrid]').down("toolbar").hide();
            return;
        }
        grid.down("toolbar").show();
        gridview.down("toolbar").show();
        /**
         * 卷内、案卷、已归管理的拆插件(卷)按钮
         */
        //隐藏拆件(卷)按钮(其中卷内、已归为拆插件，案卷为拆插卷)
        this.hideToolbarBtnTbsByItemId('dismantle',buttons,tbseparator);
        this.hideToolbarBtnTbsByItemId('dismantle',btns,tbs);
        //隐藏插件(卷)按钮(其中卷内、已归为拆插件，案卷为拆插卷)
        this.hideToolbarBtnTbsByItemId('insertion',buttons,tbseparator);
        this.hideToolbarBtnTbsByItemId('insertion',btns,tbs);
        /**
         * 卷内文件的查看案卷、页数矫正、编辑新案卷、调序按钮
         */
        //隐藏查看案卷按钮
        this.hideToolbarBtnTbsByItemId('ilookfile',buttons,tbseparator);
        this.hideToolbarBtnTbsByItemId('ilookfile',btns,tbs);
        //隐藏页数矫正按钮
        this.hideToolbarBtnTbsByItemId('pageNumberCorrect',buttons,tbseparator);
        this.hideToolbarBtnTbsByItemId('pageNumberCorrect',btns,tbs);
        //隐藏编辑新案卷按钮
        this.hideToolbarBtnTbsByItemId('editNewFile',buttons,tbseparator);
        this.hideToolbarBtnTbsByItemId('editNewFile',btns,tbs);
        //隐藏调序按钮
        this.hideToolbarBtnTbsByItemId('sequence',buttons,tbseparator);
        this.hideToolbarBtnTbsByItemId('sequence',btns,tbs);
        /**
         * 案卷管理的统计项更新按钮
         */
        //隐藏统计项更新按钮
        this.hideToolbarBtnTbsByItemId('statisticUpdate',buttons,tbseparator);
        this.hideToolbarBtnTbsByItemId('statisticUpdate',btns,tbs);
        /**
         * 未归管理的归档按钮
         */
        //隐藏归档按钮
        this.hideToolbarBtnTbsByItemId('filing',buttons,tbseparator);
        this.hideToolbarBtnTbsByItemId('filing',btns,tbs);
        /**
         * 未归管理、资料管理、文本管理的分类管理按钮
         */
        //隐藏分类管理按钮
        this.hideToolbarBtnTbsByItemId('classificationManagement',buttons,tbseparator);
        this.hideToolbarBtnTbsByItemId('classificationManagement',btns,tbs);

        //隐藏OA接收按钮
        this.hideToolbarBtnTbsByItemId('importOA',buttons,tbseparator);
        this.hideToolbarBtnTbsByItemId('importOA',btns,tbs);

        //显示移交按钮
        this.showToolbarBtnTbsByItemId('transfor',buttons,tbseparator);
        this.showToolbarBtnTbsByItemId('transfor',btns,tbs);

        switch(classlevel)
        {
            //卷内文件
            case 1:
                //显示拆件按钮
                this.showToolbarBtnTbsByItemId(classlevel,'dismantle',buttons,tbseparator);
                this.showToolbarBtnTbsByItemId(classlevel,'dismantle',btns,tbs);
                //显示插件按钮
                this.showToolbarBtnTbsByItemId(classlevel,'insertion',buttons,tbseparator);
                this.showToolbarBtnTbsByItemId(classlevel,'insertion',btns,tbs);
                //显示查看案卷按钮
                this.showToolbarBtnTbsByItemId(classlevel,'ilookfile',buttons,tbseparator);
                this.showToolbarBtnTbsByItemId(classlevel,'ilookfile',btns,tbs);
                //显示页数矫正按钮
                this.showToolbarBtnTbsByItemId(classlevel,'pageNumberCorrect',buttons,tbseparator);
                this.showToolbarBtnTbsByItemId(classlevel,'pageNumberCorrect',btns,tbs);
                //显示编辑新案卷按钮
                this.showToolbarBtnTbsByItemId(classlevel,'editNewFile',buttons,tbseparator);
                this.showToolbarBtnTbsByItemId(classlevel,'editNewFile',btns,tbs);
                //显示调序按钮
                this.showToolbarBtnTbsByItemId(classlevel,'sequence',buttons,tbseparator);
                this.showToolbarBtnTbsByItemId(classlevel,'sequence',btns,tbs);
                break;
            //案卷管理
            case 2:
                //显示拆件按钮
                this.showToolbarBtnTbsByItemId(classlevel,'dismantle',buttons,tbseparator);
                this.showToolbarBtnTbsByItemId(classlevel,'dismantle',btns,tbs);
                //显示插件按钮
                this.showToolbarBtnTbsByItemId(classlevel,'insertion',buttons,tbseparator);
                this.showToolbarBtnTbsByItemId(classlevel,'insertion',btns,tbs);
                //显示统计项更新按钮
                this.showToolbarBtnTbsByItemId(classlevel,'statisticUpdate',buttons,tbseparator);
                this.showToolbarBtnTbsByItemId(classlevel,'statisticUpdate',btns,tbs);
                //案卷管理下方的卷内文件grid
                this.findView(grid).down('[itemId=southgrid]').down("toolbar").show();
                var jngrid = this.findView(grid).down('[itemId=southgrid]');
                
                this.showToolbarBtnTbsByItemId(classlevel,'sequence',btns,tbs);
                this.refreshJnToolbarBtn(grid,jngrid);
                break;
            //未归管理
            case 3:
                //显示归档按钮
                this.showToolbarBtnTbsByItemId(classlevel,'filing',buttons,tbseparator);
                this.showToolbarBtnTbsByItemId(classlevel,'filing',btns,tbs);
                //显示分类设置按钮
                this.showToolbarBtnTbsByItemId(classlevel,'classificationManagement',buttons,tbseparator);
                this.showToolbarBtnTbsByItemId(classlevel,'classificationManagement',btns,tbs);
                //显示OA接收按钮
                this.showToolbarBtnTbsByItemId(classlevel,'importOA',buttons,tbseparator);
                this.showToolbarBtnTbsByItemId(classlevel,'importOA',btns,tbs);
                //隐藏移交按钮
                this.hideToolbarBtnTbsByItemId('transfor',buttons,tbseparator);
                this.hideToolbarBtnTbsByItemId('transfor',btns,tbs);
                break;
            //已归管理
            case 4:
                //显示拆件按钮
                this.showToolbarBtnTbsByItemId(classlevel,'dismantle',buttons,tbseparator);
                this.showToolbarBtnTbsByItemId(classlevel,'dismantle',btns,tbs);
                //显示插件按钮
                this.showToolbarBtnTbsByItemId(classlevel,'insertion',buttons,tbseparator);
                this.showToolbarBtnTbsByItemId(classlevel,'insertion',btns,tbs);
                break;
            //资料管理、文本管理
            case 5:
            case 6:
                //显示分类管理按钮
                this.showToolbarBtnTbsByItemId(classlevel,'classificationManagement',buttons,tbseparator);
                this.showToolbarBtnTbsByItemId(classlevel,'classificationManagement',btns,tbs);
                break;
        }
    },
    //itemId为要隐藏的按钮functioncode
    hideToolbarBtnTbsByItemId:function (itemId,btns,tbs) {
        for(var num in btns){
            if(itemId == btns[num].itemId){
                btns[num].hide();
                if(tbs.length>=1){
                    tbs[num-1].hide();
                }else{
                    tbs[num].hide();
                }
            }
        }
    },
    //itemId为要显示的按钮functioncode
    showToolbarBtnTbsByItemId:function (classlevel,itemId,btns,tbs) {
        for(var num in btns){
            if(itemId == btns[num].itemId){
                //属于案卷，需要将拆、插件改为拆、插卷
                if(classlevel==2){
                    if(itemId=='dismantle'){
                        btns[num].setText('拆卷');
                    }else if (itemId=='insertion'){
                        btns[num].setText('插卷');
                    }
                }else{
                    if(itemId=='dismantle'){
                        btns[num].setText('拆件');
                    }else if (itemId=='insertion'){
                        btns[num].setText('插件');
                    }
                }
                btns[num].show();
                if (tbs.length>1) {
                    tbs[num - 1].show();
                }
            }
        }
    },
    refreshJnToolbarBtn: function (angrid,jngrid) {
        var anBtns = angrid.down("toolbar").query('button');

        var jnBtns = jngrid.down("toolbar").query('button');
        var jnTbs = jngrid.down("toolbar").query('tbseparator');

        //获取案卷列表中显示的按钮名称
        var anName = [];
        for (var num in anBtns) {
            //判断案卷是否有调序权限，有则下方卷内也有调序权限
            if(anBtns[num].itemId=='sequence'){
                anName.push('调序');
            }
            if(!anBtns[num].isHidden()){
                if(anBtns[num].itemId=='dismantle'){
                    anName.push('拆件');
                }else if (anBtns[num].itemId=='insertion'){
                    anName.push('插件');
                }else{
                    anName.push(anBtns[num].text);
                }
            }
        }
        //隐藏按钮
        for(var num in jnBtns){
            if($.inArray(jnBtns[num].text,anName)==-1){
                jnBtns[num].hide();
            }
        }
        //隐藏按钮间分隔符
        for (var num in jnBtns) {
            if (jnBtns[num].isHidden()) {
                if (num > 0) {
                    jnTbs[num - 1].hide();
                } else {
                    jnTbs[num].hide();
                }
            }
        }
    },
    //比较案卷和卷内的档号设置是否一致
    compareCodeset: function (ajNodeid,jnNodeid) {
        var res;
        Ext.Ajax.request({
            async:false,
            method: 'get',
            url: '/codesetting/comparecodeset/' + ajNodeid+'/'+jnNodeid,
            success:function (response) {
                res = response;
            },
            failure:function () {
                XD.msg('操作失败');
            }
        });
        return res;
    },
    //批量修改
    doBatchModify:function (btn) {
        var resultGrid = this.getGrid(btn);
        var records = resultGrid.getSelectionModel().getSelection();
        var selectCount = resultGrid.getSelectionModel().getSelectionLength();
        if(selectCount==0){
            XD.msg('请选择数据');
            return;
        }
        if(selectCount>5000){
            XD.msg('最多只能选择5000条数据操作');
            return;
        }
        var selectAll=resultGrid.down('[itemId=selectAll]').checked;
        var isSelectAll = false;
        if(selectAll){
            records = resultGrid.acrossDeSelections;
            isSelectAll = true;
        }
        var tmp = [];
        for(var i = 0; i < records.length; i++){
            tmp.push(records[i].get('entryid'));
        }
        var entryids = tmp.join(',');
        var fromOutside = false;
        var params  = resultGrid.getStore().proxy.extraParams;
        if(typeof(params.content)!=='undefined'){
            fromOutside = true;
        }
        var batchModifyModifyWin = Ext.create('Ext.window.Window',{
            width:'100%',
            height:'100%',
            title:'批量修改',
            // draggable : true,//可拖动
            // resizable : false,//禁止缩放
            modal:true,
            closeToolText:'关闭',
            layout:'fit',
            items:[{
                xtype: 'batchModifyModifyFormView',
                entryids:entryids,
                resultgrid:resultGrid,
                isSelectAll:isSelectAll,
                fromOutside:fromOutside
            }]
        });
        var fieldModifyPreviewGrid = batchModifyModifyWin.down('grid');
        if(fieldModifyPreviewGrid.getStore().data.length>0){
            fieldModifyPreviewGrid.getStore().removeAll();
        }
        batchModifyModifyWin.show();
        window.batchModifyModifyWins = batchModifyModifyWin;
        Ext.on('resize',function(a,b){
            window.batchModifyModifyWins.setPosition(0, 0);
            window.batchModifyModifyWins.fitContainer();
        });
    },

    loadModifyTemplatefieldCombo:function (view) {//加载批量修改form的下拉框
        var combostore = view.getStore();
        var batchModifyModifyFormView = view.up('batchModifyModifyFormView');
        if(batchModifyModifyFormView.filingtype) { //归档批量操作标识
            combostore.proxy.extraParams.datanodeidAndFieldcodes = batchModifyModifyFormView.filingnodeid;
        }else{
            combostore.proxy.extraParams.datanodeidAndFieldcodes = batchModifyModifyFormView.resultgrid.nodeid;
        }
        combostore.load();
    },
    
    loadModifyTemplateEnumfieldCombo:function (view) {//加载批量修改form的下拉框(处理枚举值)
    	var data = view.valueCollection.items[0].data;
    	var batchModifyModifyFormView = view.up('batchModifyModifyFormView');
        var updateFieldvalue = batchModifyModifyFormView.down('[itemId=updateFieldvalue]');
        var enumfieldCombo = batchModifyModifyFormView.down('[itemId=enumfieldCombo]');
        var codeInfo = batchModifyModifyFormView.getForm().findField('code');
        Ext.Ajax.request({
            url: '/template/getInactiveformfield',
            params: {
            	nodeid: data.nodeid,
            	field: data.fieldcode
            },
            method: 'POST',
            success: function (resp) {
            	var value = Ext.decode(resp.responseText);
                if (data.ftype == 'enum') {
                	codeInfo.setValue('');
                	var store = enumfieldCombo.getStore('BatchModifyTemplateEnumfieldStore');
		            store.proxy.extraParams = {configCode: data.fieldname};
		            store.load();
                	
		            updateFieldvalue.hide();
		            enumfieldCombo.show();
		            
		            if (value.msg == '隐藏') {
		            	codeInfo.editable = false;
                		enumfieldCombo.editable = false;
                	} else {
                		codeInfo.editable = true;
                		enumfieldCombo.editable = true;
                	}
		        } else {
		        	updateFieldvalue.show();
		            enumfieldCombo.hide();
		        }
            },
            failure: function (resp) {
                XD.msg('操作失败！');
            }
        });
    },

    loadReplaceTemplatefieldCombo:function (view) {//加载批量替换form的下拉框
        var combostore = view.getStore();
        var batchModifyReplaceFormView = view.up('batchModifyReplaceFormView');
        if(batchModifyReplaceFormView.filingtype){ //归档批量操作标识
            combostore.proxy.extraParams.datanodeidAndFieldcodes = batchModifyReplaceFormView.filingnodeid;
        }else{
            combostore.proxy.extraParams.datanodeidAndFieldcodes = batchModifyReplaceFormView.resultgrid.nodeid;
        }
        combostore.load();
    },
    loadAddTemplatefieldCombo:function (view) {//加载批量增加form的下拉框
        var combostore = view.getStore();
        var batchModifyReplaceFormView = view.up('batchModifyAddFormView');
        if(batchModifyReplaceFormView.filingtype){ //归档批量操作标识
            combostore.proxy.extraParams.datanodeidAndFieldcodes = batchModifyReplaceFormView.filingnodeid;
        }else{
            combostore.proxy.extraParams.datanodeidAndFieldcodes = batchModifyReplaceFormView.resultgrid.nodeid;
        }
        combostore.load();
    },
    //加入修改
    addToModify:function (btn) {
        /*第一步：判断字段及替换值是否填写正确*/
        var formview = btn.up('batchModifyModifyFormView');
        var combobox = formview.getForm().findField('fieldname');
        var fieldStr = combobox.getValue();
        if(!fieldStr){
            XD.msg('字段值不允许为空');
            return;
        }
        var value = formview.down('[itemId=enumfieldCombo]').lastMutatedValue;
        var updateFieldvalue = formview.getForm().findField('fieldvalue').getValue();
        var allowEmpty = formview.down('[itemId=ifAllowEmpty]').getValue();
        if(!allowEmpty && !updateFieldvalue){
            if(!updateFieldvalue && typeof(value) == 'undefined') {
                XD.msg('替换值不允许为空');
                return;
            }
        }
        /*第二步：将需要修改的字段数据追加至列表显示*/
        var fieldModifyPreviewGrid = formview.down('grid');
        var fieldModifyPreviewGridStore = fieldModifyPreviewGrid.getStore();
        var existedData = fieldModifyPreviewGridStore.data.items;
        var existedDataArr = [];
        if(existedData.length>0){
            Ext.each(existedData,function (item) {
                existedDataArr.push(item.get('fieldcode')+'∪'+item.get('fieldname')+'∪'+item.get('fieldvalue'));
            });
        }
        var params = {
            fieldcode:fieldStr.split('_')[0],
            fieldname:fieldStr.split('_')[1],
            fieldvalue:!updateFieldvalue?value:updateFieldvalue,
            existedDataArr:existedDataArr
        };
        fieldModifyPreviewGridStore.proxy.extraParams = params;
        fieldModifyPreviewGridStore.load({callback:function () {
            formview.getForm().findField('fieldname').setValue('');
            formview.getForm().findField('fieldvalue').setValue('');
            // 清除枚举值的下拉框
            formview.getForm().findField('code').setValue('');
        }
        });
        /*第三步：刷新字段值下拉框store*/
        this.initModifyComboStore(btn,'add');
    },
    //删除修改
    deleteModify:function (btn) {
        /*第一步：判断是否选定需要删除项*/
        var formview = btn.up('batchModifyModifyFormView');
        var fieldModifyPreviewGrid = formview.down('grid');
        var records = fieldModifyPreviewGrid.getSelectionModel().getSelected().items;
        var selectCount = records.length;
        if(selectCount==0){
            XD.msg('请选择需要删除的记录');
            return;
        }
        /*第二步：删除已选择的字段数据，刷新列表*/
        var delIds = [];
        for(var i = 0; i < records.length; i++){
            delIds.push(records[i].get('fieldcode'));
        }
        fieldModifyPreviewGrid.delIds = delIds;
        var fieldModifyPreviewGridStore = fieldModifyPreviewGrid.getStore();
        var allData = fieldModifyPreviewGridStore.data.items;
        var allIds = [];
        for(var i = 0; i < allData.length; i++){
            allIds.push(allData[i].get('fieldcode'));
        }
        for(var i=0;i<delIds.length;i++){
            for(var j=0;j<allIds.length;j++){
                if(delIds[i]==allIds[j]){
                    allIds.splice(j,1);
                    j--;
                }
            }
        }
        var remainIds = allIds;
        var remainData = [];
        for(var i=0;i<allData.length;i++){
            for(var j=0;j<remainIds.length;j++){
                if(allData[i].get('fieldcode')==remainIds[j]){
                    remainData.push(allData[i]);
                }
            }
        }
        var remainDataArr = [];
        if(remainData.length==0){
            fieldModifyPreviewGridStore.removeAll();
        }else{
            Ext.each(remainData,function (item) {
                remainDataArr.push(item.get('fieldcode')+'∪'+item.get('fieldname')+'∪'+item.get('fieldvalue'));
            });
            Ext.apply(fieldModifyPreviewGridStore.proxy.extraParams, {remainDataArr:remainDataArr});
            fieldModifyPreviewGridStore.load();
        }
        /*第三步：刷新字段值下拉框store*/
        this.initModifyComboStore(btn,'delete');
    },
    //初始化字段值下拉框
    initModifyComboStore:function (btn,operate) {
        var formview = btn.up('batchModifyModifyFormView');
        var gridview = formview.down('grid');
        var combobox = formview.getForm().findField('fieldname');

        var gridRecords = gridview.getStore().data.items;
        var fieldcodes = [];
        for(var i = 0; i < gridRecords.length; i++){
            fieldcodes.push(gridRecords[i].get('fieldcode'));
        }
        if(operate=='add'){
            fieldcodes.push(combobox.getValue().split('_')[0]);
        }
        if(operate=='delete'){
            for(var i=fieldcodes.length-1;i>=0;i--){//倒着检测，不用考虑位置影响
                Ext.each(gridview.delIds,function (item) {
                    if(fieldcodes[i]==item){
                        fieldcodes.splice(i,1);
                    }
                });
            }
        }
        fieldcodes.join(',');

        var combostore = combobox.getStore();
        combostore.proxy.extraParams.datanodeidAndFieldcodes = formview.resultgrid.nodeid+'∪'+fieldcodes;
        combostore.reload();
        
        var enumfieldCombo = formview.down('[itemId=enumfieldCombo]');
        enumfieldCombo.getStore().removeAll();
    },
    doBatchReplace:function (btn) {
        var resultGrid = this.getGrid(btn);
        var records = resultGrid.getSelectionModel().getSelection();
        var selectCount = resultGrid.getSelectionModel().getSelectionLength();
        if(selectCount==0){
            XD.msg('请选择数据');
            return;
        }
        if(selectCount>5000){
            XD.msg('最多只能选择5000条数据操作');
            return;
        }
        var selectAll=resultGrid.down('[itemId=selectAll]').checked;
        var isSelectAll = false;
        if(selectAll){
            records = resultGrid.acrossDeSelections;
            isSelectAll = true;
        }
        var tmp = [];
        for(var i = 0; i < records.length; i++){
            tmp.push(records[i].get('entryid'));
        }
        var entryids = tmp.join(',');
        var fromOutside = false;
        var params  = resultGrid.getStore().proxy.extraParams;
        if(typeof(params.content)!=='undefined'){
            fromOutside = true;
        }
        var batchModifyReplaceWin = Ext.create('Ext.window.Window',{
            width:'100%',
            height:'100%',
            title:'批量替换',
            // draggable : true,//可拖动
            resizable : false,//禁止缩放
            modal:true,
            closeToolText:'关闭',
            layout:'fit',
            items:[{
                xtype: 'batchModifyReplaceFormView',
                entryids:entryids,
                resultgrid:resultGrid,
                isSelectAll:isSelectAll,
                fromOutside:fromOutside
            }]
        });
        batchModifyReplaceWin.show();
        window.batchModifyReplaceWins = batchModifyReplaceWin;
        Ext.on('resize',function(a,b){
            window.batchModifyReplaceWins.setPosition(0, 0);
            window.batchModifyReplaceWins.fitContainer();
        });
    },
    doBatchAdd:function (btn) {
        var resultGrid = this.getGrid(btn);
        var records = resultGrid.getSelectionModel().getSelection();
        var selectCount = resultGrid.getSelectionModel().getSelectionLength();
        if(selectCount==0){
            XD.msg('请选择数据');
            return;
        }
        if(selectCount>5000){
            XD.msg('最多只能选择5000条数据操作');
            return;
        }
        var selectAll = resultGrid.down('[itemId=selectAll]').checked;
        var isSelectAll = false;
        if (selectAll) {
            records = resultGrid.acrossDeSelections;
            isSelectAll = true;
        }
        var tmp = [];
        for(var i = 0; i < records.length; i++){
            tmp.push(records[i].get('entryid'));
        }
        var entryids = tmp.join(',');
        var fromOutside = false;
        var params = resultGrid.getStore().proxy.extraParams;
        if(typeof(params.content)!=='undefined'){
            fromOutside = true;
        }
        var batchModifyAddWin = Ext.create('Ext.window.Window',{
            width:'100%',
            height:'100%',
            title:'批量增加',
            // draggable : true,//可拖动
            modal:true,
            closeToolText:'关闭',
            layout:'fit',
            items:[{
                xtype: 'batchModifyAddFormView',
                entryids:entryids,
                resultgrid:resultGrid,
                isSelectAll:isSelectAll,
                fromOutside:fromOutside
            }]
        });
        batchModifyAddWin.show();
        window.batchModifyAddWins = batchModifyAddWin;
        Ext.on('resize',function(a,b){
            window.batchModifyAddWins.setPosition(0, 0);
            window.batchModifyAddWins.fitContainer();
        });
    },
    //执行批量更新
    doBatchUpdate:function (btn) {
        var previewGrid = btn.up('batchModifyResultPreviewGrid');
        if(previewGrid.operateFlag=='批量修改'){
            this.doModifyBatchUpdate(btn);
        }
        if(previewGrid.operateFlag=='批量替换'){
            this.doReplaceBatchUpdate(btn);
        }
        if(previewGrid.operateFlag=='批量增加'){
            this.doAddBatchUpdate(btn);
        }
    },
    doModifyBatchUpdate:function (btn) {
        var resultPreviewGrid = btn.findParentByType('batchModifyResultPreviewGrid');
        var fieldData = resultPreviewGrid.fieldmodifydata.split('∩');//fieldcode∪fieldname∪fieldvalue
        var modifyDetail = '';
        for (var i = 0; i < fieldData.length; i++) {
        	var data = fieldData[i].split('∪');
			modifyDetail += '['+data[1]+']设置为“'+data[2]+'”，';
		}
		modifyDetail = modifyDetail.substring(0, modifyDetail.length-1);
        var operateCount = resultPreviewGrid.formview.resultgrid.getSelectionModel().getSelectionLength();//此处操作记录条数为所有选定记录总条数
        var updateConfirmMsg = '本次操作将把['+resultPreviewGrid.formview.resultgrid.nodefullname+']所选记录的'+modifyDetail+',记录数：共'+operateCount+'条, 是否继续?';
        XD.confirm(updateConfirmMsg,function (){
            updateData(btn,'modi');
        },this);
    },
    doReplaceBatchUpdate:function (btn) {
        var resultPreviewGrid = btn.findParentByType('batchModifyResultPreviewGrid');
        var fieldmodifydatas = resultPreviewGrid.fieldreplacedata[0].split('∪');//[fieldcode_fieldname,addcontent,inserttype(或inserttype_insertplaceindex)]
        var fieldname = fieldmodifydatas[0].split('_')[1];
        var replaceDetail = '“'+fieldmodifydatas[1]+'”字符串';
        var ifcontainspace = resultPreviewGrid.ifcontainspace;
        if(ifcontainspace){
            replaceDetail+='及其前后空格值';
        }
        var replacecontent = fieldmodifydatas[2];
        if (replacecontent != '') {
        	replaceDetail += '替换为“'+replacecontent+'”字符串';
        } else {
        	replaceDetail += '替换为空字符串';
        }
        var operateCount = resultPreviewGrid.operateCount;//列表load完成后的记录总条数
        var updateConfirmMsg = '本次操作将把['+resultPreviewGrid.formview.resultgrid.nodefullname+']所选记录的['+fieldname+']里的'+replaceDetail+',记录数：共'+operateCount+'条, 是否继续?';
        if(operateCount===0){
            XD.msg('未找到包含需替换内容的记录');
            return;
        }
        XD.confirm(updateConfirmMsg,function (){
            updateData(btn,'repl');
        },this);
    },
    doAddBatchUpdate:function (btn) {
        var resultPreviewGrid = btn.findParentByType('batchModifyResultPreviewGrid');
        var fieldmodifydatas = resultPreviewGrid.fieldmodifydata[0].split('∪');//[fieldcode_fieldname,searchcontent,replacement]
        var fieldname = fieldmodifydatas[0].split('_')[1];
        var addcontent = fieldmodifydatas[1];
        var insertPlaceInfo = fieldmodifydatas[2];
        var inserttype,placeindex;
        if(insertPlaceInfo.indexOf('_')!=-1){
            inserttype = insertPlaceInfo.split('_')[0];
            placeindex = insertPlaceInfo.split('_')[1];
        }else{
            inserttype = insertPlaceInfo;
        }
        var updateConfirmMsg;
        var operateCount = resultPreviewGrid.formview.resultgrid.getSelectionModel().getSelectionLength();//此处操作记录条数为所有选定记录总条数
        if(inserttype=='anywhere' && placeindex){
            updateConfirmMsg = '本次操作将在['+resultPreviewGrid.formview.resultgrid.nodefullname+']相应条件下的记录的['+fieldname+']的第'+placeindex+'位增加“'+addcontent+'”字符串，记录数：共'+operateCount+'条, 是否继续?';
        }
        if(inserttype=='front'){
            updateConfirmMsg = '本次操作将在['+resultPreviewGrid.formview.resultgrid.nodefullname+']相应条件下的记录的前面增加“'+addcontent+'”字符串，记录数：共'+operateCount+'条, 是否继续?';
        }
        if(inserttype=='behind'){
            updateConfirmMsg = '本次操作将在['+resultPreviewGrid.formview.resultgrid.nodefullname+']相应条件下的记录的后面增加“'+addcontent+'”字符串，记录数：共'+operateCount+'条, 是否继续?';
        }

        XD.confirm(updateConfirmMsg,function (){
            updateData(btn,'add');
        },this);
    },

    //编辑新案卷
    newFileHandler: function (btn) {
        var grid = this.getGrid(btn);
        var form = this.findDfView(btn);
        var records = grid.selModel.getSelection();
        var selectCount = records.length;
        var selectAll = grid.down('[itemId=selectAll]').checked;
        if (selectAll) {
            selectCount = grid.selModel.selected.length;//当前页选中
        }
        if (selectCount == 0) {
            XD.msg('请选择一条数据');
            return;
        }
        if(selectCount != 1){
            XD.msg('编制新案卷只能选中一条数据');
            return;
        }
        var records = grid.selModel.getSelection();
        var entryid = records[0].get('entryid');
        var tree = this.findGridView(btn).down('treepanel');
        var nodeid = tree.selModel.getSelected().items[0].get('fnid');
        var ajNodeid="";
        var ajEntryid="";
        var ajArchivecode="";
        Ext.Msg.wait("正在打开，请耐心等待……","数据初始化......");
        Ext.Ajax.request({
            url: '/acquisition/newFileNodeidAndEntryid',
            scope:this,
            params:{
                nodeid:nodeid,
                entryid:entryid
            },
            success: function (response) {
                var data = Ext.decode(response.responseText);
                ajEntryid = data.entryid;
                ajNodeid = data.nodeid;
                ajArchivecode = data.archivecode;
                Ext.Msg.hide();
                //查找新案卷时，如果能查找到卷内对应案卷，提示***（档号）案卷记录已存在！
                if(ajEntryid != ""){
                    XD.msg(ajArchivecode + '（档号）案卷记录已存在！');
                    return;
                }
                var initFormFieldState = this.initFormField(form, 'show', ajNodeid);
                if(!initFormFieldState){//表单控件加载失败
                    return;
                }
                this.initNewFileFormData(form, entryid,ajArchivecode);
                this.activeToForm(form);
            },failure:function(){
                Ext.Msg.hide();
            }
        });

    },
    //------导出----st----//
    //导出xml
    exportXml:function(btn) {
        var filenames="";
        var isbtn="";
        var tree = this.findGridView(btn).down('treepanel');
        var nodeid = tree.selModel.getSelected().items[0].get('fnid');
        var grid = btn.up('acquisitiongrid');
        var ids = [];
        var selectAll = grid.down('[itemId=selectAll]').checked;
        var pattern = new RegExp("[/:*?\"<>|]");
        Ext.each(grid.getSelectionModel().getSelection(), function () {
            ids.push(this.get('entryid'));
        });

        if (ids.length == 0&&selectAll==false) {
            XD.msg('请至少选择一条需要导出的数据');
            return;
        }

        Ext.MessageBox.prompt("输入导出后的文件名","文件名",function (btn2,text) {
            filenames=text;
            isbtn=btn2
            if(isbtn=="ok"){
                if(filenames){
					if(pattern.test(filenames) || filenames.indexOf('\\') > -1) {
						XD.msg("文件名称不能包含下列任何字符：\\/:*?\"<>|");
						return;
					}
                    Ext.Ajax.request({
                        async: false,
                        method: 'POST',
                        params: {
                            ids: ids,
                            nodeId: nodeid,
                            fileName: filenames,
                            selectAll:selectAll
                        },
                        url: '/export/exportParameter',
                        success: function () {
                        }
                    });
                    var nw=window.open("/export/captureExportXml");
                    nw.document.title = '正在努力下载文件中.....';
                }else {
                    XD.msg('文件名不能为空');
                    return;
                }
            }
        });
    },

    //导出excel和原文
    exportExcelAndElectronic:function (btn) {
        var filenames="";
        var isbtn="";
        var tree = this.findGridView(btn).down('treepanel');
        var nodeid = tree.selModel.getSelected().items[0].get('fnid');
        var grid = btn.up('acquisitiongrid');
        var ids = [];
        var selectAll = grid.down('[itemId=selectAll]').checked;
        var pattern = new RegExp("[/:*?\"<>|]");
        Ext.each(grid.getSelectionModel().getSelection(), function () {
            ids.push(this.get('entryid'));
        });
        if (ids.length == 0&&selectAll==false) {
            XD.msg('请至少选择一条需要导出的数据');
            return;
        }
        Ext.MessageBox.prompt("输入导出后的文件名","文件名",function (btn2,text) {
            filenames=text;
            isbtn=btn2
            if(isbtn=="ok"){
                if(filenames){
					if(pattern.test(filenames) || filenames.indexOf('\\') > -1) {
						XD.msg("文件名称不能包含下列任何字符：\\/:*?\"<>|");
						return;
					}
                    Ext.Ajax.request({
                        async :  false,//同步
                        method: 'POST',
                        params:{
                            ids:ids,
                            nodeId:nodeid,
                            fileName:filenames,
                            selectAll:selectAll
                        },
                        url: '/export/exportParameter',
                        success: function () {
                        }
                    });
                    var nw=window.open("/export/captureExporteExcelAndElectronic");
                    nw.document.title = '正在努力下载文件中.....';
                }else {
                    XD.msg('文件名不能为空');
                    return;
                }
            }
        });
    },
    //导出Xml和原文
    exportXmlAndElectronic:function (btn) {
        var filenames="";
        var isbtn="";
        var tree = this.findGridView(btn).down('treepanel');
        var nodeid = tree.selModel.getSelected().items[0].get('fnid');
        var grid = btn.up('acquisitiongrid');
        var ids = [];
        var selectAll = grid.down('[itemId=selectAll]').checked;
        var pattern = new RegExp("[/:*?\"<>|]");
        Ext.each(grid.getSelectionModel().getSelection(), function () {
            ids.push(this.get('entryid'));
        });

        if (ids.length == 0&&selectAll==false) {
            XD.msg('请至少选择一条需要导出的数据');
            return;
        }
        //var selectAll = grid.down('[itemId=selectAll]').checked;
        Ext.MessageBox.prompt("输入导出后的文件名","文件名",function (btn2,text) {
            filenames=text;
            isbtn=btn2
            if(isbtn=="ok"){
                if(filenames){
					if(pattern.test(filenames) || filenames.indexOf('\\') > -1) {
						XD.msg("文件名称不能包含下列任何字符：\\/:*?\"<>|");
						return;
					}
					
                    Ext.Msg.wait('正在进行导出，请耐心等候......','正在操作');
                    Ext.Ajax.request({
                        async :  true,
                        method: 'POST',
                        params:{
                            ids:ids,
                            nodeId:nodeid,
                            fileName:filenames,
                            selectAll:selectAll
                        },
                        url: '/export/exportParameter',
                        success: function (data) {
                            var nw=window.open('/export/captureExporteXmlAndElectronic');
                            nw.document.title = '正在努力下载文件中.....';
                            Ext.MessageBox.hide();
                        }
                    });
                }else {
                    XD.msg('文件名不能为空');
                    return;
                }
            }
        });
    },
    //导出excel
    exportExcel:function (btn) {
        var filenames="";
        var isbtn="";
        var tree = this.findGridView(btn).down('treepanel');
        var nodeid = tree.selModel.getSelected().items[0].get('fnid');
        var grid = btn.up('acquisitiongrid');
        var ids = [];
        var selectAll = grid.down('[itemId=selectAll]').checked;
        var pattern = new RegExp("[/:*?\"<>|]");
        Ext.each(grid.getSelectionModel().getSelection(), function () {
            ids.push(this.get('entryid'));
        });

        if (ids.length == 0&&selectAll==false) {
            XD.msg('请至少选择一条需要导出的数据');
            return;
        }
        //var selectAll = grid.down('[itemId=selectAll]').checked;
        Ext.MessageBox.prompt("输入导出后的文件名","文件名",function (btn2,text) {
            filenames=text;
            isbtn=btn2
            if(isbtn=="ok"){
                if(filenames){
					if(pattern.test(filenames) || filenames.indexOf('\\') > -1) {
						XD.msg("文件名称不能包含下列任何字符：\\/:*?\"<>|");
						return;
					}
					
                    Ext.Ajax.request({
                        async :  false,
                        method: 'POST',
                        params:{
                            ids:ids,
                            nodeId:nodeid,
                            fileName:filenames,
                            selectAll:selectAll
                        },
                        url: '/export/exportParameter',
                        success: function () {
                        }
                    });
                    var nw=window.open("/export/captureExportExcle");
                    nw.document.title = '正在努力下载文件中.....';
                }else {
                    XD.msg('文件名不能为空');
                    return;
                }
            }
        });
    },
    //----------导出 ---en--//
    importHandler: function (btn) {
        //var view = this.findParentByType('managementgrid');
        var view = btn.up('acquisitiongrid');

        var tree = btn.up('acquisitionFormAndGrid').down('treepanel');
        NodeIdf = tree.selModel.getSelected().items[0].get('fnid');
        //var grid = btn.up('importgrid');


        var win = Ext.create('Comps.view.AcUploadView', {});
        win.on('close', function () {
            view.notResetInitGrid();
        }, view);
        win.show();
    },

    //--------自选字段导出--s----//
    exportFunction:function(view, state){
        var userGridView = view.findParentByType('acquisitiongrid');
        var selectAll = userGridView.down('[itemId=selectAll]').checked;
        var record = userGridView.getSelection();
        var isSelectAll = false;
        if(selectAll){
            record = userGridView.acrossDeSelections;
            isSelectAll = true;
        }
        var tmp = [];
        for(var i = 0; i < record.length; i++){
            tmp.push(record[i].get('entryid'));
        }
        var entryids = tmp.join(',');
        tempParams = userGridView.getStore().proxy.extraParams;
        tempParams['entryids'] = entryids;
        tempParams['isSelectAll'] = isSelectAll;
        tempParams['exportState'] = state;
        var gridStore=userGridView.getStore();
        tempParams['indexLength'] = gridStore.totalCount;
        if(selectAll == false && entryids.length == 0){
            XD.msg('请至少选择一条需要导出的数据');
            return;
        }
        var selectItem = Ext.create("Acquisition.view.AcquisitionGroupSetView");
        var selector=selectItem.items.get(0);
        var selectstore = selectItem.items.get(0).getStore();
        selectstore.load({
            callback:function(records, operation, success){
                selector.onBindStore(selectstore);
                selectItem.show();
            }
        });
    },
    chooseFieldExportExcel: function (view) {
        this.exportFunction(view, "Excel");
    },
    chooseFieldExportXml: function (view) {
        this.exportFunction(view, "Xml");
    },
    chooseFieldExportXmlAndFile: function (view) {
        this.exportFunction(view, "XmlAndFile");
    },
    chooseFieldExportExcelAndFile: function (view) {
        this.exportFunction(view, "ExcelAndFile");
    },
    chooseSave: function (view) {
        var filenames = "";
        var isbtn = "";
        var pattern = new RegExp("[/:*?\"<>|]");
        var selectView = view.findParentByType('acquisitionGroupSetView');
        var FieldCode = selectView.items.get(0).getValue()
        userFieldCode = FieldCode;
        var exporUrl = "";
        if (FieldCode.length>0) {
            var win = Ext.create("Acquisition.view.AcquisitionMessageView", {});
            win.show();
        }else {
            XD.msg("请选择需要导出的字段")
        }
    },
    //--下载节点字段模板
    downloadFieldTemp:function(btn){
        var tree = this.findGridView(btn).down('treepanel');
        var nodeid = tree.selModel.getSelected().items[0].get('fnid');
        var reqUrl="/export/downloadFieldTemp?nodeid="+nodeid;
        window.location.href=reqUrl;
    },

    missPageCheck:function (btn) {
        var grid = this.getGrid(btn);
        var selectAll=grid.down('[itemId=selectAll]').checked;
        var tree = this.findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if (!node) {
            XD.msg('请选择节点');
            return;
        }
        if(grid.selModel.getSelectionLength() == 0){
            XD.msg('请至少选择一条需要漏页检查的数据');
            return;
        }
        var record = grid.selModel.getSelection();
        var isSelectAll = false;
        if(selectAll){
            record = grid.acrossDeSelections;
            isSelectAll = true;
        }
        var ids = [];
        for (var i = 0; i < record.length; i++) {
            ids.push(record[i].get('entryid'));
        }
        var MissPageCheck = Ext.create('Acquisition.view.AcquisitionMissPageCheck');
        var checkstore = MissPageCheck.down('acquisitionMissPageDetailView').getStore();
        checkstore.proxy.extraParams.ids = ids;
        checkstore.proxy.extraParams['isSelectAll']=isSelectAll;
        checkstore.reload();
        var numbertotal = [];
        numbertotal = this.getTotal(ids);
        for(var i=0;i<numbertotal.length;i++){
            if(typeof numbertotal[i]=='undefined'){
                numbertotal[i]='';
            }
        }
        MissPageCheck.down('label').setHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;总共 "+numbertotal[0]+" 条档案记录，档案页数共计 "
            +numbertotal[1]+" ，档案的电子原文数量为 "+numbertotal[2]+" 份");
        MissPageCheck.show();
    },

    lookMedia:function(view, record, item, index, e){
        var entryId = view.getStore().getAt(item).id;
        var lookMediaView = Ext.create('Acquisition.view.AcquisitionLookMediaView');
        var tree = lookMediaView.down('missPageElectronicView');
        tree.initData(entryId);
        lookMediaView.show();
    },
    getTotal:function (ids) {
        var number=[];
        Ext.Ajax.request({
            url: '/acquisition/getMissPageCheckTotal',
            async:false,
            params:{
                ids:ids
            },
            success: function (response) {
                number = Ext.decode(response.responseText);
            }
        });
        return number;
    },
    lookMediaEle:function (view,record) {
        var eleid = record.get('eleid');
        var filename = record.get('filename');
        var url = '/acquisition/szhShowMedia?eleid='+eleid;
        p4.changeImg(url,filename);
    },
    openExport:function (view) {
        var MissPageDetailView = view.findParentByType('acquisitionMissPageDetailView');
        var select = MissPageDetailView.getSelectionModel().getSelection();
        if(select.length<1){
            XD.msg('请至少选择一条数据');
            return;
        }
        var ids = [];
        for(var i=0;i<select.length;i++){
            ids.push(select[i].get('id'));
        }
        var exportMissView = Ext.create('Acquisition.view.AcquisitionExportMissView');
        exportMissView.ids =ids;
        exportMissView.show();
    },
    missExport:function (view) {
        var ExportMissView = view.findParentByType('acquisitionExportMissView');
        var fileName = ExportMissView.down('[itemId=userFileName]').getValue();
        var zipPassword = ExportMissView.down('[itemId=zipPassword]').getValue();
        var b = ExportMissView.down('[itemId=addZipKey]').checked;
        var form = ExportMissView.down('[itemId=form]');

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
            Ext.MessageBox.wait('正在处理请稍后...');
            Ext.Ajax.request({
                method: 'post',
                url:'/export/missPageFieldExport',
                timeout:XD.timeout,
                scope: this,
                async:true,
                params: {
                    fileName:fileName,
                    zipPassword:zipPassword,
                    ids:ExportMissView.ids
                },
                success:function(res){
                    var obj = Ext.decode(res.responseText).data;
                    if(obj.entrySizeMsg=="NO"){
                        XD.msg('条目数超出限制，一次只支持导出50w的条目！');
                        Ext.MessageBox.hide();
                        return;
                    }
                    window.location.href="/export/downloadZipFile?fpath="+encodeURIComponent(obj.filePath)
                    Ext.MessageBox.hide();
                    XD.msg('文件生成成功，正在准备下载');
                    ExportMissView.close()
                },
                failure:function(){
                    Ext.MessageBox.hide();
                    XD.msg('文件生成失败');
                }
            });
        } else {
            XD.msg("文件名不能为空")
        }
    }
    ,
    //档号对齐
    doCodesettingAlign:function (btn) {//档号对齐（对所有检索出的数据进行操作）
        // var grid = btn.findParentByType('acquisitiongrid');
        var grid
        if(window.isMedia != true) {
            grid = btn.findParentByType('managementgrid');
        }
        else {
            grid = btn.findParentByType('mediaItemsDataView').down('dataview');
        }

        if(grid.store.data.length>100000){
            XD.msg('当前数据量过大无法使用该功能');
            return;
        }
        var SimulationArchivecode;
        Ext.Ajax.request({
            url: '/codesetting/getSimulationArchivecode',
            async:false,
            params: {
                nodeid: grid.nodeid
            },
            timeout:XD.timeout,
            success: function (resp) {
                var respText = Ext.decode(resp.responseText).data;
                if (respText) {
                    SimulationArchivecode=respText;
                } else {
                    XD.msg('未设置档号，请先进行档号设置！');
                }
            }
        });

        var gridStore = grid.getStore();
        if(gridStore.data.length==0){
            XD.msg('没有可操作数据');
            return;
        }
        var alignConfirmMsg = '本次操作将依据档号设置中设定的单位长度（当前档号设置的长度：'+SimulationArchivecode+'），在所有检索结果的想要字段前面补0至指定长度（值超过设定的单位长度将不处理）,是否继续？';
        Ext.Ajax.request({
            url: '/codesetting/getCodeSettingFields',
            async:false,
            params: {
                nodeid: grid.nodeid
            },
            timeout:XD.timeout,
            success: function (resp) {
                var respText = Ext.decode(resp.responseText).data;
                if (respText) {
                    XD.confirm(alignConfirmMsg,function (){
                        alignCodesetting(btn);
                    },this);
                } else {
                    XD.msg('未设置档号，请先进行档号设置！');
                }
            }
        });
    },
    lookServiceMetadata:function(btn){
        var grid = this.getGrid(btn);
        var records;

        if(grid.selModel != null) {
            records = grid.selModel.getSelection();
        }
        else {
            records = grid.acrossSelections;
        }
        var selectCount = records.length;
        var tree = this.findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if (!node) {//若点击修改时左侧未选中任何节点，则提示选择节点
            XD.msg('请选择节点');
            return;
        }
        if (selectCount != 1) {
            XD.msg('请选择一条数据');
            return;
        }

        var entryid = records[0].get('entryid');
        var metadataLogWin = Ext.create('Ext.window.Window',{
            modal:true,
            width:900,
            height:530,
            title:'档案追溯元数据',
            layout:'fit',
            closeToolText:'关闭',
            closeAction:'hide',
            items:[{
                xtype: 'serviceMetadataGridView'
            }]
        });
        var grid = metadataLogWin.down('serviceMetadataGridView');
        grid.initGrid({entryid:entryid});
        metadataLogWin.show();
    },

    metadataLogHandler:function(btn){
        var grid = this.getGrid(btn);
        var records

        if(grid.selModel != null) {
            records = grid.selModel.getSelection();
        }
        else {
            records = grid.acrossSelections;
        }
        var selectCount = records.length;
        var tree = this.findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if (!node) {//若点击修改时左侧未选中任何节点，则提示选择节点
            XD.msg('请选择节点');
            return;
        }
        if (selectCount != 1) {
            XD.msg('请选择一条数据');
            return;
        }

        var entryid = records[0].get('entryid');
        var metadataLogWin = Ext.create('Ext.window.Window',{
            modal:true,
            width:900,
            height:530,
            title:'档案元数据日志',
            layout:'fit',
            closeToolText:'关闭',
            closeAction:'hide',
            items:[{
                xtype: 'MetadataLogEntryGridView'
            }]
        });
        var grid = metadataLogWin.down('MetadataLogEntryGridView');
        grid.initGrid({entryid:entryid});
        metadataLogWin.show();
    },

    toMediaList:function(){
        var _this=window._this;
        var treemodel = window.qhTreemodel;
        var record = window.qhRecord;

        var gridcard = _this.findView(treemodel.view).down('[itemId=gridcard]');
        treepanelInfo = this.findView(treemodel.view).down('treepanel');
        var nodeType = record.data.nodeType;
        var bgSelectOrgan = gridcard.down('[itemId=bgSelectOrgan]');
        if (nodeType == 2) {
            window.isMedia = false;
            gridcard.setActiveItem(bgSelectOrgan);
        }
        else{
            window.isMedia = true;
            var mediasView = gridcard.down('[itemId=mediaItemsDataView]');
            gridcard.setActiveItem(mediasView);
            mediasView.initGrid({nodeid: record.get('fnid')});
            if(record.data.classlevel != 3) {
                mediasView.down("[itemId=filing]").hide();
            }
            else {
                mediasView.down("[itemId=filing]").show();
            }
        }
        AcFormAndGridView = gridcard.up('acquisitionFormAndGrid');
    },
    changeToList:function(btn){
        var _this=window._this;
        var treemodel = window.qhTreemodel;
        var record = window.qhRecord;
        this.listTab(_this,treemodel,record);
    },
    listTab:function(_this,treemodel,record){
        var gridcard = this.findView(treemodel.view).down('[itemId=gridcard]');

        //声像档案节点显示缩略图按钮,非声像档案节点隐藏
        var toMediaBtn=gridcard.down('[itemId=toMediaBtn]');
        if(window.isMedia){
            toMediaBtn.show();
        }else{
            toMediaBtn.hide();
        }

        var onlygrid = gridcard.down('[itemId=onlygrid]');
        var pairgrid = gridcard.down('[itemId=pairgrid]');
        var grid;
        var nodeType = record.data.nodeType;
        var bgSelectOrgan =gridcard.down('[itemId=bgSelectOrgan]');
        treepanelInfo = this.findView(treemodel.view).down('treepanel');
        //树节点为分类，更改右边页面为“请选择机构节点”
        // var functionBtn = isUserOrganPower(record.get('fnid'),"capture",isp);
        if(nodeType == 2){
            gridcard.setActiveItem(bgSelectOrgan);
        }else {
            if(record.data.classlevel == 2){
                gridcard.setActiveItem(pairgrid);
                var ajgrid = pairgrid.down('[itemId=northgrid]');
                ajgrid.setTitle("当前位置：" + record.data.text);
                var jngrid = pairgrid.down('[itemId=southgrid]');
                jngrid.setTitle("查看卷内");
                if(jngrid.expandOrcollapse == 'expand'){
                    jngrid.expand();
                }else{
                    jngrid.collapse();
                }
 //               this.refreshToolbarBtnShowAll(jngrid);
                for(var i=0;i<jngrid.query("button").length;i++)//显示所有按钮
                jngrid.query("button")[i].show();
                jngrid.dataUrl = '/acquisition/entries/innerfile/'+ '' + '/';
                jngrid.initGrid({nodeid:this.getNodeid(record.get('nodeid'))});
                grid = ajgrid;
            } else {
                gridcard.setActiveItem(onlygrid);
                onlygrid.setTitle("当前位置：" + record.data.text);
                grid = onlygrid;
            }
            window.organTitle=record.data.text;//节点的机构信息
            var gridview = gridcard.up('acquisitionFormAndGrid').down('formAndGrid').down('acquisitiongrid');
            gridview.setTitle("当前位置：" + record.data.text);//将表单与表格视图标题改成当前位置

            var funitem = grid.down("toolbar");
            funitem.removeAll();
            funitem.add(functionButton);
            this.refreshToolbarBtn(record.data.classlevel,grid,gridview);
            grid.nodeid = record.get('fnid');
            grid.initGrid({nodeid:record.get('fnid')});

            //---
            NodeIdf = record.get('fnid');
            var demoStore = Ext.getStore('AcquisitionGroupSetStore');
            demoStore.proxy.extraParams.fieldNodeid = record.get('fnid');
            //--
            var fullname=record.get('text');
            while(record.parentNode.get('text')!='数据采集'){
                fullname=record.parentNode.get('text')+'_'+fullname;
                record=record.parentNode;
            }
            grid.nodefullname = fullname;
            grid.parentXtype = 'acquisitionFormAndGrid';
            grid.formXtype = 'acquisitionform';
            grid.winType='cjlook';//日志用
        }
        AcFormAndGridView = gridcard.up('acquisitionFormAndGrid');
    },

    //获取节点排序设置
    getUserNodeSort: function (nodeid) {
        var result;
        Ext.Ajax.request({
            url: '/acquisition/getUserNodeSort',
            async: false,
            params: {
                nodeid: nodeid
            },
            timeout: XD.timeout,
            success: function (resp) {
                var respText = Ext.decode(resp.responseText);
                result = respText;
            },
            failure:function () {
                XD.msg('获取用户节点排序失败');
            }
        });
        return result;
    },

    lookdetailsHandler: function (btn) {//查看详细
        var grid = btn.up('oAImportGridView');
        var record = grid.selModel.getSelection();
        if (record.length == 0) {
            XD.msg('请选择一条需要查看的数据');
            return;
        }
        if (record.length > 1) {
            XD.msg('只能选择一条数据');
            return;
        }
        Ext.Ajax.request({
            url: '/longRetention/getFsexRecord',
            async: false,
            params: {
                entryid: record[0].data.entryid
            },
            timeout: XD.timeout,
            success: function (resp) {
                var respText = Ext.decode(resp.responseText);
                var recordFsex = respText.data;
                if(!recordFsex){
                    XD.msg('数据未检测！');
                    return;
                }
                var win = Ext.create('Acquisition.view.transfor.LongRetentionDetailView');
                win.down('[itemId=closeBtn]').on('click', function () {
                    win.close()
                });
                win.down('[itemId=authenticity]').html = recordFsex.authenticity;
                 win.down('[itemId=integrity]').html = recordFsex.integrity;
                 win.down('[itemId=usability]').html = recordFsex.usability;
                 win.down('[itemId=safety]').html = recordFsex.safety;
                win.show();
            },
            failure:function () {
                XD.msg('获取验证信息失败');
            }
        });
    },

    //OA接收
    importOA:function (view) {
        var oaGridWin = Ext.create('Ext.window.Window', {
            width: '100%',
            height: '100%',
            header: false,
            draggable: false,//禁止拖动
            resizable: false,//禁止缩放
            modal: true,
            closeToolText: '关闭',
            layout: 'fit',
            items: [{
                xtype: 'oAImportView'
            }]
        });
        var oAImportView = oaGridWin.down('oAImportView');
        var oaGrid = oAImportView.down('oAImportGridView');
        oaGrid.getStore().proxy.extraParams.nodeid=NodeIdf;
        oaGrid.getStore().reload();
        oaGridWin.show();
    },
    lookPackpage:function (btn) {
        var grid = btn.up('oAImportGridView');

        var records = grid.selModel.getSelection();
        if(records.length != 1){
            XD.msg("请选择一条数据进行查看！");
            return
        }
        window.filename = records[0].data.filename;
        var win = Ext.create('Acquisition.view.OfflineAccessionPackageView');
        win.show();
    }
});

function delTempByUniquetag() {//清除本机当前用户关联的的临时条目数据
    Ext.Ajax.request({
        method: 'DELETE',
        url: '/batchModify/delTempByUniquetag',
        async:false,
        success: function (response) {
        }
    })
}

function delTempByUniqueType(type) {//清除本机当前用户关联的的临时条目数据
    Ext.Ajax.request({
        method: 'POST',
        params: {batchType: type},
        url: '/batchModify/delTempByUniqueType',
        async:false,
        success: function (response) {
        }
    });

}

function delSqTempByUniquetag() {
    Ext.Ajax.request({
        method: 'DELETE',
        url: '/batchModify/delSqTempByUniquetag',
        async:false,
        success: function (response) {
        }
    })
}

function updateData(btn,batchtype) {
    var batchModifyResultPreviewWin = btn.up('window');
    var resultPreviewGrid = batchModifyResultPreviewWin.down('batchModifyResultPreviewGrid');
    var params = resultPreviewGrid.formview.resultgrid.getStore().proxy.extraParams;
    params['entryidArr']= resultPreviewGrid.formview.entryids.split(',');
    params['fieldModifyData'] = resultPreviewGrid.fieldmodifydata;
    params['fieldReplaceData'] = resultPreviewGrid.fieldreplacedata;
    params['flag'] = resultPreviewGrid.operateFlag;
    params['isSelectAll'] = resultPreviewGrid.formview.isSelectAll;
    params['type'] = '数据采集';
    params['batchtype'] = batchtype; //区分不同类型的批量操作

    if(resultPreviewGrid.ifcontainspace!=undefined){
        params.ifContainSpace = resultPreviewGrid.ifcontainspace;
    }
    Ext.Msg.wait('正在执行'+resultPreviewGrid.operateFlag+'操作，请耐心等待……','正在操作');

    var columnArray = [];
    var columns = resultPreviewGrid.columnManager.getColumns();
    for (var j = 0; j < columns.length; j++) {
        if (columns[j].xtype === 'gridcolumn') {
            var subtext = columns[j].dataIndex + '-' + columns[j].text;
            columnArray.push(subtext);
        }
    }
    params['columnArray'] = columnArray;
    var downloadForm = document.createElement('form');
    document.body.appendChild(downloadForm);
    var inputTextElement;
    for (var prop in params){
        inputTextElement = document.createElement('input');
        inputTextElement.name = prop;
        inputTextElement.value = params[prop];
        downloadForm.appendChild(inputTextElement);
    }
    downloadForm.action = '/batchModify/export';
    downloadForm.method = "post";
    downloadForm.submit();
    Ext.Ajax.setTimeout(36000000);
    Ext.Ajax.request({
        url: '/batchModify/updateEntryindex',
        params: params,
        sync : true,
        timeout:XD.timeout,
        success: function (resp) {
            var respText = Ext.decode(resp.responseText);
            batchModifyResultPreviewWin.hide();
            Ext.Msg.wait(resultPreviewGrid.operateFlag+'操作成功','正在操作').hide();
            XD.msg(respText.msg);
            resultPreviewGrid.formview.resultgrid.down('[itemId=selectAll]').setValue(false);
            Ext.apply(resultPreviewGrid.formview.resultgrid.getStore().getProxy().extraParams, {
                info: '',
                condition:resultPreviewGrid.formview.resultgrid.getStore().proxy.extraParams['basicCondition'],
                operator:resultPreviewGrid.formview.resultgrid.getStore().proxy.extraParams['basicOperator'],
                content:resultPreviewGrid.formview.resultgrid.getStore().proxy.extraParams['basicContent']
            });
            var acgrid = resultPreviewGrid.formview.resultgrid;
            acgrid.getStore().proxy.extraParams.entryidArr='';
            acgrid.notResetInitGrid();
        },
        failure:function () {
            Ext.Msg.wait(resultPreviewGrid.operateFlag+'操作失败','正在操作').hide();
            XD.msg('更新失败');
        }
    })
}

/**
 *获取业务元数据
 * @param entryids 条目集合
 * @param module  模块名
 * @param operation 业务行为
 * @returns {*}
 */
function captureServiceMetadataByZL(entryids,module,operation) {
    var r;
    Ext.Ajax.request({
        url: '/serviceMetadata/captureServiceMetadataByZL',
        async:true,
        methods:'Post',
        params:{
            entryids:entryids,
            module:module,
            operation:operation
        },
        success: function (response) {
            r = Ext.decode(response.responseText);
            console.log(r.msg+",条目数："+r.data);
        }
    });
    return r;
}