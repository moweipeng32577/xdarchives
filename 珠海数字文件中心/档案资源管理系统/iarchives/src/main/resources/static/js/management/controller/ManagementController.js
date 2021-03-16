/**
 * Created by Rong on 2017/10/24.
 * 数据管理控制器
 *
 */
var filecount = 0;//计算文件数
// var backupConfirmMsg = '修改数据后，将不能进行还原，建议进行备份，备份需要较长时间，是否备份?';
var formvisible,formlayout;
var entryids = "";
var NodeIdf = "";
var userFieldCode = "";
var acquisitionGrid, btnInfo;
var ManagementIsArchivecode="";
var tempParams;
var formAndGridView, eventBtn, addEventView, eventid, targetNodeid;
var managementSelectWin;
Ext.define('Management.controller.ManagementController',{
    extend: 'Ext.app.Controller',

    views:[
        'ManagementGridView','ManagementTreeComboboxView',
        'ManagementFormAndGridView','ManagementFormView',
        'FormAndGridView','FormAndInnerGridView','FormView',
        'ManagementFilingView','DynamicFilingFormView',
        'ManagementReportGridView','ManagementDismantleFormView',
        'AdvancedSearchFormView','RetentionAdjustFromView',
        'ManagementGroupSetView','ManagementMessageView',
        'ManagementSequenceGridView','ManagementSequenceView',//调序
        'ManagementClassificationGridView','ManagementClassificationView','ManagementDictionaryView',
        'ManagementExportMissView','ManagementLookMediaView','ManagementMissPageCheck',
        'ManagementMissPageDetailView','MissPageElectronicView',//分类设置'
        'ManagementAssociationView','DataEventGridView','DataEventDetailGridView',//数据关联
        'ManagementSelectView','ManagementSelectWin','ManagementFieldView',//数据转移
        'ElectronicVersionGridView','ElectronicVersionView',
        'QRCodeGroupSetView',
        'EntryEditFromView',
        'OrderSettingSelectedFormView','OrdersettingItemSelectedFormView','OrdersettingDetailFormView',//归档排序
        'MetadataLogEntryGridView',
        'MediaItemsDataView','BasicMediaDataView','BackCaptureFormView',
        'LookBackCaptureDocEntryView','LookBackCaptureDocGridView','LookBackCaptureDocView',
        'ServiceMetadataGridView'
    ],
    models:['ManagementModel','ManagementFilingModel','ReportGridModel',
        'BatchModifyTemplatefieldModel','BatchModifyTemplateEnumfieldModel','FieldModifyPreviewGridModel',
        'ManagementSequenceModel','ManagementGroupSetModel',
        'ManagementSequenceModel','ManagementClassificationModel',
        'ManagementMissPageDetailModel', 'ElectronicVersionGridModel',
        'ManagementMissPageDetailModel',
        'DataEventModel',//数据关联
        'ManagementSelectModel','ManagementFieldModel',//数据转移
        'OrdersettingJsonModel','MetadataLogEntryModel',
        'MediaDataModel','LookBackCaptureDocGridModel','ServiceMetadataGridModel'
    ],
    stores:['ManagementStore','ManagementFilingStore','ReportGridStore',
        'BatchModifyTemplatefieldStore','BatchModifyTemplateEnumfieldStore','FieldModifyPreviewGridStore',
        'ManagementSequenceStore','ManagementGroupSetStore',
        'ManagementSequenceStore','ManagementClassificationStore',
        'ManagementMissPageDetailStore',
        'DataEventStore','ElectronicVersionGridStore',
        'ManagementSelectStore','ImportGridStore', 'TemplateStore',//数据转移
        'OrdersettingSelectStore','MetadataLogEntryStore',
        'MediaItemsDtStore','LookBackCaptureDocGridStore','ServiceMetadataGridStore'
    ],

    init:function(){
        var treeNode;
        this.control({
            'managementFormAndGrid [itemId=treepanelId]':{
            	render: function (view) {
        		    if(key == 1){
                        view.getRootNode().on('expand', function (node) {
                            for (var i = 0; i < node.childNodes.length; i++) {
                                if (node.childNodes[i].parentNode.id == 'root' && node.childNodes[i].raw.text != '全宗卷管理' ) {
                                    node.childNodes[i].raw.visible = false;
                                }
                            }
                        })
                    } else {
        		        if(parentNodeids.length>0){
        		            //审核入库成功提醒,展开到指定节点
        		            var count = parentNodeids.length - 1;
                            view.getRootNode().on('expand', function (node) {
                                var treeNodefind;
                                for (var i = 0; i < node.childNodes.length; i++) {
                                        if(node.childNodes[i].get('fnid')==parentNodeids[count]){
                                            treeNodefind = node.childNodes[i];
                                        }
                                }
                                if(treeNodefind){
                                    node.getOwnerTree().expandPath(treeNodefind.raw.id, "id");
                                    if(count==0){
                                        node.getOwnerTree().getSelectionModel().select(treeNodefind);
                                    }
                                    count--;
                                }
                            })
                        }else{
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
                        }
                    }
                },
                select: function(treemodel, record){
                    window.nodeid = record.get('fnid');
                    window.isMedia = false;
                    var _this = this;

                    //列表\缩列图切换用
                    window._this=this;
                    window.qhTreemodel=treemodel;
                    window.qhRecord=record;
                    var gridcard = this.findView(treemodel.view).down('[itemId=gridcard]');
                    formAndGridView = gridcard.up('managementFormAndGrid');
                    if(sessionStorage.getItem('mediaNodeid_' + window.nodeid) == "1"){//说明是声像文件，切换成缩略图显示
                        this.toMediaList();
                    } else{
                        window.isMedia = false;
                        this.listTab(_this,treemodel,record);
                    }
                    //获取该nodeid的档号设置、表格模板、模板检索项检索
                    //window.codeset=getCodeSettingFields(window.nodeid);
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
                                url: '/management/order/'+data.records[0].get('entryid')+'/'+target+'/'+window.fileArchivecode,
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
            'mediaItemsDataView [itemId=importOA]': {//打开OA接收
                click:this.importOA
            },
            'oAImportView [itemId=downloadImportMsg]':{
                click: function (view) {
                    var userGridView = view.findParentByType('oAImportGridView');    //获取父页面
                    var selectAll = userGridView.down('[itemId=selectAll]').checked;  //获取选中按钮，检查获得boolean对象
                    var record = userGridView.getSelection();   //父页面中被选中的记录数组
                    var isSelectAll = false;
                    if (selectAll) {
                        record = userGridView.acrossDeSelections;
                        isSelectAll = true;
                    }
                    var tmp = [];
                    for (var i = 0; i < record.length; i++) {
                        tmp.push(record[i].get('id'));
                    }
                    var entryids = tmp.join(',');
                    Ext.Ajax.request({
                        method: 'post',
                        url: '/export/expImportOAMsg',
                        timeout: XD.timeout,
                        scope: this,
                        async: true,
                        params: {
                            ids:entryids,
                            isSelectAll:isSelectAll
                        },
                        success: function (res) {
                            var obj = Ext.decode(res.responseText).data;
                            window.location.href = "/export/downloadZipFile?fpath=" + encodeURIComponent(obj);
                            Ext.MessageBox.hide();
                            XD.msg('文件生成成功，正在准备下载');
                        },
                        failure: function () {
                            Ext.MessageBox.hide();
                            XD.msg('文件生成失败');
                        }
                    });
                }
            },
            'managementGroupSetView button[itemId="close"]': {
                click: function (view) {
                    view.findParentByType('managementGroupSetView').close();
                }
            },
            'managementGroupSetView button[itemId="save"]': {
                click: this.chooseSave
            },
            'ManagementMessage button[itemId="cancelExport"]': {
                click: function (view) {
                    view.findParentByType('ManagementMessage').close();
                }
            },
            'import button[itemId="back"]':{//导入-返回
                click: function(btn){
                    formAndGridView.down('managementgrid').initGrid();
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
            'ManagementMessage button[itemId="SaveExport"]': {//导出
                click: function (view) {
                    var ManagementMessageView = view.up('ManagementMessage');
                    var fileName = ManagementMessageView.down('[itemId=userFileName]').getValue();
                    var zipPassword = ManagementMessageView.down('[itemId=zipPassword]').getValue();
                    var b = ManagementMessageView.down('[itemId=addZipKey]').checked;
                    var form = ManagementMessageView.down('[itemId=form]');
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
                        var entryLength =0;
                        if(tempParams.exportState=="Xml"&&tempParams.indexLength>10000){
                            Ext.Msg.alert("提示", "提示：导出xml文件只支持导入1万条以内！");
                            return;
                        }
                        Ext.MessageBox.wait('正在处理请稍后...');
                        Ext.Ajax.request({
                            method: 'post',
                            url:'/export/chooseFieldExport',
                            timeout:XD.timeout,
                            scope: this,
                            async:true,
                            params: tempParams,
                            success:function(res){
                                var obj = Ext.decode(res.responseText).data;
                                if(obj.fileSizeMsg=="NO"){
                                    XD.msg('原文总大小超出限制，一次只支持导出10G内的原文！');
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
                                window.location.href="/export/downloadZipFile?fpath="+encodeURIComponent(obj.filePath);
                                Ext.MessageBox.hide();
                                XD.msg('文件生成成功，正在准备下载');
                                ManagementMessageView.close();
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
            'managementGroupSetView button[itemId="addAllOrNotAll"]': {
                click:function(view){
                    var itemSelector = view.findParentByType('managementGroupSetView').down('itemselector');
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
            'managementgrid ':{
                // render:this.initGrid,
                eleview: this.activeEleForm,
                itemdblclick:this.lookHandler
            },
            'managementgrid [itemId=lookServiceMetadata]': {//查看追溯元数据
                click: this.lookServiceMetadata
            },
            'managementgrid [itemId=createQRcode]': {//下载二维码
                click: this.downloadQRCode
            },
            'managementgrid [itemId=printQRCode]': {//打印二维码
                click: this.printQRCode
            },
            'managementgrid [itemId=save]':{//著录
                click:this.saveHandler
            },
            'managementgrid [itemId=modify]':{//修改
                click:this.modifyHandler
            },
            'managementgrid [itemId=del]':{//删除
                click:this.delHandler
            },
            'managementgrid [itemId=look]':{//查看
                click:this.lookHandler
            },
            'managementgrid [itemId=dataTransfor]':{//数据转移
            	click:this.dataTransforHandler
            },
            'managementSelectWin [itemId=setField]': {//数据转移 - 提交
            	click:this.transforSetFieldHandler
            },
            'managementSelectWin [itemId=close]': {//数据转移 - 关闭
            	click:this.transforCloseHandler
            },
            'managementSelectView': {
            	select: function(treemodel, record){
            		// 记录目标节点id信息
            		targetNodeid = record.data.fnid;
            		// 初始化字段设置的下拉列表
            		var templateStore = this.getStore('TemplateStore');
                    templateStore.proxy.extraParams = {nodeid: record.data.fnid};
                    templateStore.reload();
            	}
            },
            'managementFieldView [itemId=submit]': {//字段设置 - 提交
            	click:this.transforSubmitHandler
            },
            'managementFieldView [itemId=close]': {//字段设置 - 关闭
            	click:this.transforCloseHandler
            },
            //------------------------------------------------------------- 事件管理start
            'managementgrid [itemId=dataAssociation]':{//数据关联
            	click:this.associationHandler
            },
            'managementgrid [itemId=lookAssociation]':{//查看关联
            	click:this.lookAssociationHandler
            },
            'managementAssociationView [itemId=sure]':{
            	click:this.associationSure
            },
            // 数据关联 - 关闭
            'managementAssociationView [itemId=close]':{
            	click:this.associationClose
            },
            // 数据关联 - 选择事件
            'managementAssociationView [itemId=selectEvent]':{
            	click:this.selectEventHandler
            },
            // 选择事件
            'dataEventGridView [itemId=selectEvent]': {
            	click:this.selectEvent
            },
            // 选择事件 - 返回
            'dataEventGridView [itemId=backEvent]': {
            	click:this.backEvent
            },
            // 显示电子文件视图
            'dataEventDetailGridView': {
            	eleview: this.activeEleForm
            },
            // 查看事件条目 - 查看具体条目
            'dataEventDetailGridView [itemId=seeBtnID]': {
            	click:this.seeBtnIDHandler
            },
            // 从查看事件条目返回
            'dataEventDetailGridView [itemId=back]': {
            	click:this.backHandler
            },
            //------------------------------------------------------------- 事件管理结束end
            'managementgrid [itemId=Excel]':{//导出excel
                //click:this.exportExcel
                click: this.chooseFieldExportExcel
            },
            'managementgrid [itemId=Xml]':{//导出xml
                //click:this.exportXml
                click: this.chooseFieldExportXml
            },
            'managementgrid [itemId=ExcleAndElectronic]':{//导出excel和原文
                //click:this.exportExcelAndElectronic
                click: this.chooseFieldExportExcelAndFile
            },
            'managementgrid [itemId=XmlAndElectronic]':{//导出xml和原文
                //click:this.exportXmlAndElectronic
                click: this.chooseFieldExportXmlAndFile
            },
            'managementgrid [itemId=FieldTemp]':{//导出字段模板
                click:this.downloadFieldTemp
            },
            // 'managementgrid [itemId=importSipBtnID]':{//导入
            //     //click:this.importHandler
            //     click: this.importHandler
            // },
            //卷内文件调序功能start-----------------------
            'entrygrid [itemId=sequence]':{//卷内文件调序
                click:this.sequenceHandler
            },
            'entrygrid [itemId=iprint]':{//卷内文件打印
                click:this.iprintHandler
            },
            'managementSequenceView [itemId=up]': {//上调
                click:this.upHandler
            },
            'managementSequenceView [itemId=down]': {//下调
                click:this.downHandler
            },
            'managementSequenceView [itemId=save]': {//保存
                click:this.sequenceSaveHandler
            },
            'managementSequenceView [itemId=back]': {//返回
                click: this.backToGrid
            },
            //卷内文件调序功能end-------------------------
            ///////////分类设置--------------------start////////////////////////////
            'managementgrid [itemId=classificationManagement]':{
                click: this.classificationHandler                                  //进入分类设置管理窗口
            },
            'managementclassification [itemId=classificationBackBtn]':{
                click: this.backToGrid                                             //分类设置返回，即关闭窗口
            },
            'managementclassification [itemId=classificationSet]':{
                click: this.setHandler                                              //分类设置窗口，分类设置
            },
            'managementclassification [itemId=classificationAutoSet]':{
                click: this.autoSetHandler                                          //分类设置窗口，分类自动设置
            },
            'managementclassification [itemId=previousStepBtn]':{
                click: this.activeClassificationFirstForm                           //分类自动设置面板返回上一步
            },
            'managementclassification [itemId=setInfo]':{
                click: this.toSetHandler                                            //分类自动设置，设置按钮
            },
            'managementDictionaryView [name=organ]':{
                select: this.organHandler                                          //分类设置窗口，选择机构问题下拉框
            },
            'managementDictionaryView [name=resetOrgan]':{
                click: this.resetOrganHandler                                      //分类设置窗口中重置机构
            },
            ///////////分类设置--------------------start////////////////////////////
            'managementgrid [itemId=statisticUpdate]':{//打开报表显示列表
                click:this.statisticUpdateHandler
            },
            ///////////报表－－－－－－－－－－－－－－－start////////////////////////////
            'managementgrid [itemId=print]':{//打开报表显示列表
                click:this.chooseReport
            },
            'managementReportGridView [itemId=print]':{//打印报表
                click:function (btn) {
                    var reportGrid = btn.findParentByType('managementReportGridView');
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
            'managementReportGridView [itemId=showAllReport]':{//显示所有报表
                click:function (btn) {
                    var reportGrid = btn.findParentByType('managementReportGridView');
                    if(reportGrid.down('[itemId=showAllReport]').text=='显示所有报表'){
                        reportGrid.down('[itemId=showAllReport]').setText('显示当前报表');
                        reportGrid.initGrid({nodeid:reportGrid.nodeid,flag:'all'});
                    }else if(reportGrid.down('[itemId=showAllReport]').text=='显示当前报表'){
                        reportGrid.down('[itemId=showAllReport]').setText('显示所有报表');
                        reportGrid.initGrid({nodeid:reportGrid.nodeid});
                    }
                }
            },
            ///////////报表－－－－－－－－－－－－－－－end////////////////////////////
            ///////////高级检索－－－－－－－－－－－－－－－start////////////////////////////
            'managementgrid [itemId=advancedsearch]':{//高级检索
                click:this.advancedSearch
            },
            'advancedSearchFormView':{
                render:function(field){
                    var topLogicCombo = field.getComponent('topLogicCombo');
                    var bottomLogicCombo = field.getComponent('bottomLogicCombo');
                    topLogicCombo.on('change',function (view) {//点击顶部逻辑下拉选，则同步底部逻辑下拉选的值
                        bottomLogicCombo.setValue(view.lastValue);
                    });
                    bottomLogicCombo.on('change',function (view) {//点击底部逻辑下拉选，则同步顶部逻辑下拉选的值
                        topLogicCombo.setValue(view.lastValue);
                    });
                }
            },
            'advancedSearchFormView [itemId=topSearchBtn]':{click:this.doAdvancedSearch},
            'advancedSearchFormView [itemId=bottomSearchBtn]':{click:this.doAdvancedSearch},
            'advancedSearchFormView [itemId=topClearBtn]':{click:this.doAdvancedSearchClear},
            'advancedSearchFormView [itemId=bottomClearBtn]':{click:this.doAdvancedSearchClear},
            'advancedSearchFormView [itemId=topCloseBtn]':{click:this.doAdvancedSearchClose},
            'advancedSearchFormView [itemId=bottomCloseBtn]':{click:this.doAdvancedSearchClose},
            ///////////高级检索－－－－－－－－－－－－－－－end////////////////////////////
            ///////////批量操作－－－－－－－－－－－－－－－start////////////////////////////
            'managementgrid [itemId=batchmodify]':{//批量修改
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
            'batchModifyModifyFormView button[itemId=getPreview]': {//批量修改窗口　获取预览
                click: function (btn) {
                    var formview = btn.up('batchModifyModifyFormView');
                    var formWin = formview.up('window');
                    var fieldModifyPreviewGrid = formview.down('grid');
                    var fieldModifyPreviewGridStore = fieldModifyPreviewGrid.getStore();
                    var fieldModifyPreviewGridData = fieldModifyPreviewGridStore.data.items;
                    if (fieldModifyPreviewGridData.length == 0) {
                        XD.msg('请选择修改字段');
                        return;
                    }
                    var fieldModifyData = "";
                    var operateFieldcodes = [];
                    if (fieldModifyPreviewGridData.length > 0) {//如果有已存在的修改字段
                        for (var i = 0; i < fieldModifyPreviewGridData.length; i++) {
                            var item = fieldModifyPreviewGridData[i];
                            if (i < fieldModifyPreviewGridData.length - 1) {
                                fieldModifyData += item.get('fieldcode') + '∪' + item.get('fieldname') + '∪' + item.get('fieldvalue') + "∩";
                            } else {
                                fieldModifyData += item.get('fieldcode') + '∪' + item.get('fieldname') + '∪' + item.get('fieldvalue');
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
                                    type:"数据管理"
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
                        params['type'] = '数据管理';
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
            'batchModifyModifyFormView button[itemId=exit]': {//批量修改窗口　退出
                click: function (btn) {
                    formAndGridView.down('managementgrid').getStore().proxy.url = '/management/entriesPost';
                    formAndGridView.down('managementgrid').getStore().getProxy().actionMethods = {read: 'POST'};
                    // formAndGridView.down('managementgrid').getStore().proxy.extraParams.entryidArr='';
                    // formAndGridView.down('managementgrid').notResetInitGrid();
                    btn.up('window').close();
                }
            },
            'batchModifyResultPreviewGrid button[itemId=batchUpdateBtn]': {//批量操作预览－执行批量更新
                click: this.doBatchUpdate
            },
            'batchModifyResultPreviewGrid button[itemId=backBtn]': {//批量操作预览－返回
                click: function (btn) {
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

            'managementgrid [itemId=batchreplace]': {//批量替换
                click: this.doBatchReplace
            },
            'batchModifyReplaceFormView [itemId=templatefieldCombo]': {
                render: this.loadReplaceTemplatefieldCombo
            },
            'batchModifyReplaceFormView button[itemId=getPreview]': {//批量替换窗口　获取预览
                click: function (btn) {
                    var formview = btn.up('batchModifyReplaceFormView');
                    var formWin = formview.up('window');
                    var fieldcodeandnameCombo = formview.getForm().findField('fieldname');
                    var fieldcodeAndName = fieldcodeandnameCombo.getValue();
                    var searchcontentField = formview.getForm().findField('searchcontent');
                    var searchcontent = searchcontentField.getValue();
                    var replacecontentField = formview.getForm().findField('replacecontent');
                    var replacecontent = replacecontentField.getValue();
                    var containspace = formview.down('[itemId=ifContainSpaces]').getValue();
                    var ifContainspace = containspace ? true : false;
                    var allowempty = formview.down('[itemId=ifAllowEmpty]').getValue();

                    if (!fieldcodeAndName) {
                        XD.msg('请选择修改字段');
                        return;
                    }
                    if (!searchcontent) {
                        XD.msg('查找内容不允许为空');
                        return;
                    }
                    if (!replacecontent && !allowempty) {
                        XD.msg('替换值不允许为空');
                        return;
                    }
                    if (searchcontent == replacecontent && !ifContainspace) {
                        XD.msg('替换前后数据无变化');
                        return;
                    }
                    var fieldReplaceData = [fieldcodeAndName + '∪' + searchcontent + '∪' + replacecontent];
                    var operateFieldcodes = [fieldcodeAndName.split('_')[0]];
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
                                    type:"数据管理"
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
                    }else{
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
                        params['type'] = '数据管理';
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
            'batchModifyReplaceFormView button[itemId=exit]': {//批量替换窗口　退出
                click: function (btn) {
                    formAndGridView.down('managementgrid').getStore().proxy.url = '/management/entriesPost';
                    formAndGridView.down('managementgrid').getStore().getProxy().actionMethods = {read: 'POST'};
                    // formAndGridView.down('managementgrid').getStore().proxy.extraParams.entryidArr='';
                    // formAndGridView.down('managementgrid').notResetInitGrid();
                    btn.up('window').close();
                }
            },
            'managementgrid [itemId=pageNumberCorrect]': {//页数矫正
                click: this.pageNumberCorrectHandler
            },
            'managementgrid [itemId=editNewFile]': {//编辑新案卷
                click: this.newFileHandler
            },
            'managementgrid [itemId=batchadd]': {//批量增加
                click: this.doBatchAdd
            },
            'batchModifyAddFormView [itemId=templatefieldCombo]': {
                render: this.loadAddTemplatefieldCombo
            },
            'batchModifyAddFormView button[itemId=getPreview]': {//批量增加窗口　获取预览
                click: function (btn) {
                    var formview = btn.up('batchModifyAddFormView');
                    var formWin = formview.up('window');
                    var fieldcodeandnameCombo = formview.getForm().findField('fieldname');
                    var fieldcodeAndName = fieldcodeandnameCombo.getValue();
                    var addcontentField = formview.getForm().findField('addcontent');
                    var addcontent = addcontentField.getValue();
                    var inserttype = formview.getValues()['insertPlace'];
                    var insertplaceindexField = formview.getForm().findField('insertPlaceIndex');
                    var insertplaceindex = insertplaceindexField.getValue();

                    if (!fieldcodeAndName) {
                        XD.msg('请选择修改字段');
                        return;
                    }
                    if (!addcontent) {
                        XD.msg('添加内容不允许为空');
                        return;
                    }
                    if (!inserttype) {
                        XD.msg('请检查位置设置信息');
                        return;
                    }
                    if (inserttype == 'anywhere' && !insertplaceindex) {
                        XD.msg('请输入插入字符位置');
                        return;
                    }
                    if (isNaN(insertplaceindex)) {
                        XD.msg('插入字符位置输入项格式不正确');
                        return;
                    }
                    if (parseInt(insertplaceindex) < 1) {
                        XD.msg('插入字符位置输入项最小值为1');
                        return;
                    }
                    if (parseInt(insertplaceindex) > 8000) {
                        XD.msg('插入字符位置输入项最大值为8000');
                        return;
                    }

                    var inserttypeAndPlaceindex, fieldModifyData;
                    if (inserttype == 'anywhere') {
                        inserttypeAndPlaceindex = inserttype + '_' + insertplaceindex;
                        fieldModifyData = [fieldcodeAndName + '∪' + addcontent + '∪' + inserttypeAndPlaceindex];
                    } else {
                        fieldModifyData = [fieldcodeAndName + '∪' + addcontent + '∪' + inserttype];
                    }
                    var operateFieldcodes = [fieldcodeAndName.split('_')[0]];
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
                                    type:"数据管理"
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
                    }else{
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
                        params['type'] = '数据管理';
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
            'batchModifyAddFormView button[itemId=exit]': {//批量增加窗口　退出
                click: function (btn) {
                    formAndGridView.down('managementgrid').getStore().proxy.url = '/management/entriesPost';
                    formAndGridView.down('managementgrid').getStore().getProxy().actionMethods = {read: 'POST'};
                    //formAndGridView.down('managementgrid').notResetInitGrid();
                    btn.up('window').close();
                }
            },
            ///////////批量操作－－－－－－－－－－－－－－－end////////////////////////////
            ///////////拆插件－－－－－－－－－－－－－－－start////////////////////////////
            'managementgrid [itemId=dismantle]':{//拆件
                click:this.dismantleHandler
            },
            'entrygrid [itemId=innerfileDismantle]':{//卷内文件拆卷
                click:this.innerfileDismantleHandler
            },
            'managementDismantleFormView [itemId=dismantleSave]':{//拆件（卷）－保存
                click:this.dismantleSubmitForm
            },
            'managementDismantleFormView [itemId=dismantleBack]':{//拆件（卷）－返回
                click:function (btn) {
                    btn.up('window').close();
                }
            },
            'managementgrid [itemId=insertion]':{//插件
                click:this.insertionHandler
            },
            'entrygrid [itemId=innerfileInsertion]':{//卷内文件插卷
                click:this.innerfileInsertionHandler
            },
            ///////////拆插件－－－－－－－－－－－－－－－end////////////////////////////
            'managementFormAndGrid [itemId=northgrid]':{
                itemclick : this.itemclickHandler
            },
            'managementFormAndGrid [itemId=southgrid]':{//卷内文件grid
                // afterrender : this.southrender,
                eleview: this.activeEleForm,
                itemdblclick: this.ilookHandler
            },
            'managementFormAndGrid [itemId=isave]':{//卷内文件　著录
                click : this.isaveHandler
            },
            'managementFormAndGrid [itemId=imodify]':{//卷内文件　修改
                click : this.imodifyHandler
            },
            'managementFormAndGrid [itemId=idel]':{//卷内文件　删除
                click : this.idelHandler
            },
            'managementFormAndGrid [itemId=ilook]':{//卷内文件　查看
                click : this.ilookHandler
            },
            'managementgrid [itemId=ilookfile]':{//卷内文件　查看案卷
                click : this.ilookfileHandler
            },
            ///////////归档－－－－－－－－－－－－－－－start////////////////////////////
            'managementgrid [itemId=filing]':{//归档
                click:this.filingHandler
            },
            'managementfiling [itemId=filingNextStepBtn]':{//归档窗口　下一步 至归档预览grid
                click : this.filingSubmitForm
            },
            'managementfiling [itemId=filingBackBtn]':{//归档窗口　返回 至选择归档条目grid
                click : this.backToManagementgrid
            },
            'managementfiling [itemId=filingBackTwoBtn]':{//预归档窗口　返回 至选择归档条目grid
                click : this.backToManagementgrid
            },
            'managementfiling button[itemId=ordersettingSaveBtnId]': {//预归档 归档排序设置
                click: this.saveOrder
            },
            'managementfiling [itemId=generateArchivecode]':{//归档窗口　生成档号
                click : this.generateArchivecode
            },
            'managementfiling [itemId=retentionAdjust]':{//归档窗口　保管期限调整
                click : this.retentionAdjust
            },
            'managementfiling [itemId=ygdBackBtn]':{//归档窗口　取消预归档
                click : this.ygdBack
            },
            'retentionAdjustFromView [itemId=retentionAjustConfirm]':{//保管期限调整 确定
                click:this.retentionAjustConfirm
            },
            'managementfiling [itemId=ygdBackBtn]':{//归档窗口　取消预归档
                click : this.ygdBack
            },
            'retentionAdjustFromView [itemId=retentionAjustBack]':{//保管期限调整 返回
                click:function (btn) {
                    btn.up('window').close();
                }
            },
            'managementfiling [itemId=moveup]': {//归档窗口　上移
                click: this.moveup
            },
            'managementfiling [itemId=movedown]': {//归档窗口　下移
                click: this.movedown
            },
            'managementfiling [itemId=filingBtn]':{//归档窗口　归档
                click : this.filing
            },
            'managementfiling [itemId=addGdBtn]':{//未归窗口　增加预归档
                click : this.addGd
            },
            'managementfiling [itemId=insertFront]':{//未归窗口　插入预归档-首位
                click : this.insertGd
            },
            'managementfiling [itemId=insertBehind]':{//未归窗口　插入预归档-末位
                click : this.insertGd
            },
            'managementfiling [itemId=insertAnywhere]':{//未归窗口　插入预归档-位置
                click : this.insertGd
            },
            'InsertFilingView [itemId=checkInsert]':{//未归窗口　插入预归档-确定
                click : this.checkInsert
            },
            'managementfiling [itemId=addOrderSetBtn]':{//未归窗口　预归档排序设置
                click : this.addOrderSet
            },
            'managementfiling [itemId=ygdEditBtn]':{//归档窗口　预归档修改
                click : this.ygdEdit
            },
            'managementfiling [itemId=filingpreviousStepBtn]':{//归档窗口　上一步　至档案分类选择的form
                click : this.activeFilingFirstForm
            },
            'dynamicfilingform [itemId=autoAppraisal]':{//归档动态表单 自动鉴定复选框
                change:this.changeComboState
            },
            'managementfiling [itemId=batchModifyModifyId]':{//结果列表界面　批量修改
                click:this.doFilingBatchModify
            },
            'managementfiling [itemId=batchModifyReplaceId]':{//结果列表界面　批量替换
                click:this.doFilingBatchReplace
            },
            'managementfiling [itemId=batchModifyAddId]':{//结果列表界面　批量增加
                click:this.doFilingBatchAdd
            },
            'managementfiling': {
                beforetabchange:function(view){
                    if (view.activeTab.title == '归档设置') {
                        //检查与归档设置
                        var filingFirstForm = view.down('[itemId=filingFirstStep]');
                        var dynamicFilingForm = view.down('dynamicfilingform');
                        /*if(!dynamicFilingForm.initedstate){
                         XD.msg('模板或档号设置异常，请在“系统设置”-“模板维护”中设置该节点的模板及档号');
                         return false;
                         }*/
                        var treeComboboxView = filingFirstForm.down('managementTreeComboboxView');
                        var ordertxtLab=view.down('[itemId=ordertxtId]');
                        var ordertxt= ordertxtLab.title;
                        ordertxt=ordertxt.substring(ordertxt.indexOf('序')+3);
                        if((!treeComboboxView.rawValue)&&(ordertxt.indexOf('序')==-1)){
                            XD.msg('请选择归档的档案分类，以及未归记录加入预归档的先后排序！');
                            return false;
                        }else if(!treeComboboxView.rawValue){
                            XD.msg('请选择归档的档案分类！');
                            return false;
                        }else if(ordertxt.indexOf('序')==-1){
                            XD.msg('请选择未归记录加入预归档的先后排序！');
                            return false;
                        }
                    }
                },
                tabchange: function (view) {//tab页面切换触发
                    if (view.activeTab.title == '未归') {
                        var gridcard=view.down('[itemId=wgNodeId]');
                        gridcard.getStore().reload();
                        //window.orderType=0;//标记插入预归档清空
                    }
                    if (view.activeTab.title == '预归档') {
                        var gridcard=view.down('[itemId=ylId]');
                        var btn=view.down('[itemId=filingBtn]');
                        var filingFormAndGrid =view.down('[itemId=filingSecondStep]');
                        var ygType='ygd';
                        var dataSource = 'next';
                        if((window.orderType==1||window.moveUpOrDown)&&window.orderType!=0){//orderType  0是增加，重排序   1是插入，不重新排序
                            dataSource = '';//按顺序排，不重新赋值序号
                        }
                        //window.ygdNodeid=filingFormAndGrid.nodeid;
                        if(window.wgChange){//未归页面没设置时，不重新加载表格
                            gridcard.getStore().proxy.extraParams.dataSource =dataSource;//修改参数避免重复增加临时条目
                            gridcard.getStore().proxy.extraParams.ygType=ygType;
                            gridcard.getStore().proxy.extraParams.nodeid=window.ygdNodeid;
                            gridcard.getStore().proxy.extraParams.allEntryids='';//避免get请求头过大
                            gridcard.getStore().proxy.extraParams.entryids='';//避免get请求头过大
                            gridcard.getStore().proxy.extraParams.isSelectAll=false;
                            gridcard.initGrid();
                            gridcard.getStore().reload();
                        }
                        window.orderType=1;//刷新后增加标记清空，不用重新排序
                        window.wgChange=false;//标记未归页面设置标记清空
                    }
                    if (view.activeTab.title == '归档设置') {
                        this.initOrderset(view);
                    }
                }
            },
            'entryEditFromView [itemId=saveArchivecodeBtn]':{//预归档 修改 保存
                click:this.saveEntry
            },
            'entryEditFromView [itemId=closeEntryWinBtn]':{//预归档 修改 关闭
                click:this.closeEntry
            },
            'entryEditFromView [itemId=ygdPreBtn]': {//上一条
                click: this.ygdPreHandler
            },
            'entryEditFromView [itemId=ygdNextBtn]': {//下一条
                click: this.ygdNextHandler
            },
            'ordersettingSelectedFormView [itemId=ordersettingSaveTwoBtnId]':{//排序设置弹窗 保存
                click : this.saveOrderTwo
            },
            'ordersettingItemSelectedFormView': {
                render: function (field) {
                    field.getComponent("itemselectorID").toField.boundList.on('select', function () {
                        var ordersettingSelectedFormView = this.findParentByType('ordersettingSelectedFormView');
                        var ordersettingDetailFormView = ordersettingSelectedFormView.down('[itemId=ordersettingDetailFormViewItemID]');
                        var areatextfield = ordersettingDetailFormView.down('[itemId=areaid]');
                        var direction = ordersettingDetailFormView.down('[itemId=directionId]');
                        var hideidfield = ordersettingDetailFormView.down('[itemId=hiddenfieldId]');
                        var temp = this.selModel.selected.items[0].get('fieldcode').split('∪');
                        if (temp[0] == "") {
                            //将从模板中获得的字段传到输入框中
                            areatextfield.setValue(temp[2]);
                            direction.setValue('0');//默认升序
                            //把字段全称保存在隐藏域中，输入框修改保存时用到
                            hideidfield.setValue(temp[1]);
                        } else {
                            areatextfield.setValue(temp[1]);
                            direction.setValue(temp[2]);
                            hideidfield.setValue(temp[3]);
                        }
                    });
                }
            },
            'ordersettingDetailFormView': {
                render: function (field) {
                    field.getComponent("directionId").on('select', function (ob) {
                        var ordersettingSelectedFormView = this.findParentByType('ordersettingSelectedFormView');
                        var ordersettingItemSelectedFormView = ordersettingSelectedFormView.down('[itemId=itemselectorItemID]');
                        var ordersettingDetailFormView = ordersettingSelectedFormView.down('[itemId=ordersettingDetailFormViewItemID]');
                        changeToMultiselect(ob, ordersettingItemSelectedFormView, ordersettingDetailFormView);
                    });
                }
            },
            ///////////归档－－－－－－－－－－－－－－－end////////////////////////////
            'managementform':{
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
            'managementform [itemId=preBtn]':{
                click:this.preHandler
            },
            'managementform [itemId=nextBtn]':{
                click:this.nextHandler
            },
            'managementform [itemId=save]':{//保存按钮
                click:this.submitForm
            },
            'managementform [itemId=continuesave]':{//连续录入按钮
                click:this.continueSubmitForm
            },
            'managementform [itemId=backEntry]':{//查看,上一条数据
                click:this.backEntry
            },
            'managementform [itemId=nextEntry]':{//查看,下一条数据
                click:this.nextEntry
            },
            'managementform [itemId=back]':{//返回按钮
                click: function(btn){
                    var treepanel = this.findTreeView(btn);
                    var nodeid = treepanel.selModel.getSelected().items[0].get('fnid');
                    var currentManagementform = this.getCurrentManagementform(btn);
                    var formview = currentManagementform.down('dynamicform');
                    if (formAndGridView.type == '数据关联') {
                    	// 把视图给替换回来
				    	var dataEventDetailGridView = this.findView(btn).down('dataEventDetailGridView');
				    	formAndGridView.setActiveItem(dataEventDetailGridView);
				    	formAndGridView.type = null;
                    } else if (formview.nodeid != nodeid) {
                    	//切换到列表界面,同时刷新列表数据
                        this.activeGrid(btn, false);
                        this.findInnerGrid(btn).getStore().reload();
                    } else {
                        this.activeGrid(btn,true);
                        this.findInnerGrid(btn).getStore().removeAll();
                    }
                }
            },
            'managementgrid button[itemId=numberAlignment]':{
                click:this.doCodesettingAlign
            },
            'managementgrid [itemId=metadataLog]':{//查看档案元数据日志
                click:this.metadataLogHandler
            },'managementgrid [itemId=toMediaBtn]':{//切换到缩略图页面
                click:this.toMediaList
            },
            'managementReportGridView [itemId=back]':{//报表列表返回至数据列表
                click:function (btn) {
                    btn.up('window').hide();
                }
            },
             // 'managementform':{
             //      'tabchange':function (t, n) {
             //        if(n.title=='条目'){
             //           var dynamicform = n.findParentByType('managementform').down('dynamicform');
             //           var pagesfield = dynamicform.down('[name=pages]');
             //           var uploadview = n.findParentByType('managementform').down('electronic').down('uploadview');
             //           var store = uploadview.down('grid').getStore();
             //           var treeNodes = uploadview.down('treepanel').getStore().getRoot().childNodes;
             //           var treeNodesNum =treeNodes.length;//树节点的文件数量
             //
             //        }
             //    }
             // }
            'managementgrid [itemId=misspagecheck]':{//漏页检测
                click:this.missPageCheck
            },

            'managementMissPageDetailView [itemId=mxBtn]':{ //查看明细
                click:this.lookMedia
            },
            'managementMissPageDetailView [itemId=export]':{  //导出漏页信息
                click:this.openExport
            },
            'managementExportMissView button[itemId="SaveExport"]': {//导出
                click:this.missExport
            },
            'managementExportMissView button[itemId="cancelExport"]': {//导出 关闭
                click:function (view) {
                    view.findParentByType('managementExportMissView').close();
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
                    window.eletype='management';
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
                            url: '/management/delVersion',
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
                            url: '/management/rebackVersion',
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
                        url: '/management/ifVersionFileExist',
                        params: {
                            eleVersionids: eleVersionids
                        },
                        success: function (response) {
                            var responseText = Ext.decode(response.responseText);
                            if(responseText.success==true){
                                if(eleVersionids.length==1){
                                    var eleVersionid = eleVersionids[0];
                                    location.href = '/management/downloadEleVersion/eleVersionid/'+ eleVersionid;
                                }else{
                                    location.href = '/management/downloadEleVersion/eleVersionids/'+ eleVersionids;
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

            //生成二维码--全选
            'QRCodeGroupSetView button[itemId="addAllOrNotAll"]': {
                click: function (view) {
                    var itemSelector = view.findParentByType('QRCodeGroupSetView').down('itemselector');
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

            //生成二维码--关闭
            'QRCodeGroupSetView button[itemId="close"]': {
                click: function (view) {
                    view.findParentByType('QRCodeGroupSetView').close();
                }
            },

            //生成二维码--保存
            'QRCodeGroupSetView button[itemId="save"]': {
                click: function (view) {
                    var selectView = view.findParentByType('QRCodeGroupSetView');
                    var fieldCode = selectView.items.get(0).getValue();
                    if (fieldCode.length == 0) {
                        XD.msg("请选择需要导出的字段");
                        return;
                    }
                    Ext.MessageBox.wait('正在处理请稍后...');
                    Ext.Ajax.request({
                        url: '/management/createQRcode',
                        timeout: XD.timeout,
                        params:{
                            userFieldCode:fieldCode,
                            entryids:QRCodeParams['entryids'],
                            isSelectAll:QRCodeParams['isSelectAll'],
                            nodeid:QRCodeParams['nodeid']
                        },
                        method: 'POST',
                        success: function (resp) {
                            var obj = Ext.decode(resp.responseText).data;
                            window.location.href = "/export/downloadZipFile?fpath=" + encodeURIComponent(obj);
                            Ext.MessageBox.hide();
                            XD.msg('文件生成成功，正在准备下载');
                            return;
                        },
                        failure: function (resp) {
                            Ext.MessageBox.hide();
                            XD.msg('文件生成失败');
                        }
                    });
                }
            },

            //打印二维码--预览
            'QRCodeGroupSetView button[itemId="printSave"]': {
                click:this.prinQR
            }
            ,
            'mediaItemsDataView ': {
                // render:this.initGrid,
                eleview: this.activeEleForm,
                itemdblclick: this.lookHandler
            },
            'mediaItemsDataView button[itemId=dataDuplicate]': {click: this.doDuplicateChecking},

            'duplicateCheckingSelectView button[itemId=close]': {
                click: function (btn) {
                    btn.findParentByType('duplicateCheckingSelectView').close();
                }
            },
            //档号对齐

            'mediaItemsDataView button[itemId=numberAlignment]':{
                click:this.doCodesettingAlign
            },

            'mediaItemsDataView [itemId=save]': {//著录
                click: this.saveHandler
            },
            'mediaItemsDataView [itemId=modify]': {//修改
                click: this.modifyHandler
            },
            'mediaItemsDataView [itemId=del]': {//删除
                click: this.delHandler
            },
            'mediaItemsDataView [itemId=look]': {//查看
                click: this.lookHandler
            },
            'mediaItemsDataView [itemId=dataTransfor]': {//数据转移
                click: this.dataTransforHandler
            },'mediaItemsDataView [itemId=metadataLog]':{//查看档案元数据日志
                click:this.metadataLogHandler
            },'mediaItemsDataView [itemId=gridList]':{//切换列表显示
                click:this.changeToList
            },

            'mediaItemsDataView [itemId=createQRcode]': {//下载二维码
                click: this.downloadQRCode
            },
            'mediaItemsDataView [itemId=printQRCode]': {//打印二维码
                click: this.printQRCode
            },

            'mediaItemsDataView [itemId=dataAssociation]': {//数据关联
                click: this.associationHandler
            },
            'mediaItemsDataView [itemId=lookAssociation]': {//查看关联
                click: this.lookAssociationHandler
            },

            'mediaItemsDataView [itemId=Excel]': {//导出excel
                //click:this.exportExcel
                click: this.chooseFieldExportExcel
            },
            'mediaItemsDataView [itemId=Xml]': {//导出xml
                //click:this.exportXml
                click: this.chooseFieldExportXml
            },
            'mediaItemsDataView [itemId=ExcleAndElectronic]': {//导出excel和原文
                //click:this.exportExcelAndElectronic
                click: this.chooseFieldExportExcelAndFile
            },
            'mediaItemsDataView [itemId=XmlAndElectronic]': {//导出xml和原文
                //click:this.exportXmlAndElectronic
                click: this.chooseFieldExportXmlAndFile
            },
            'mediaItemsDataView [itemId=FieldTemp]': {//导出字段模板
                click: this.downloadFieldTemp
            },
            'mediaItemsDataView [itemId=classificationManagement]': {
                click: this.classificationHandler                                  //进入分类设置管理窗口
            },
            'mediaItemsDataView [itemId=statisticUpdate]': {//打开报表显示列表
                click: this.statisticUpdateHandler
            },
            ///////////报表－－－－－－－－－－－－－－－start////////////////////////////
            'mediaItemsDataView [itemId=print]': {//打开报表显示列表
                click: this.chooseReport
            },
            'mediaItemsDataView [itemId=advancedsearch]': {//高级检索
                click: this.advancedSearch
            },
            'mediaItemsDataView [itemId=batchmodify]': {//批量修改
                click: this.doBatchModify
            },
            'mediaItemsDataView [itemId=batchreplace]': {//批量替换
                click: this.doBatchReplace
            },

            'mediaItemsDataView [itemId=pageNumberCorrect]': {//页数矫正
                click: this.pageNumberCorrectHandler
            },
            'mediaItemsDataView [itemId=editNewFile]': {//编辑新案卷
                click: this.newFileHandler
            },
            'mediaItemsDataView [itemId=batchadd]': {//批量增加
                click: this.doBatchAdd
            },
            'mediaItemsDataView [itemId=dismantle]': {//拆件
                click: this.dismantleHandler
            },
            'mediaItemsDataView [itemId=insertion]': {//插件
                click: this.insertionHandler
            },
            'mediaItemsDataView [itemId=ilookfile]': {//卷内文件　查看案卷
                click: this.ilookfileHandler
            },
            ///////////归档－－－－－－－－－－－－－－－start////////////////////////////
            'mediaItemsDataView [itemId=filing]': {//归档
                click: this.filingHandler
            },
            'mediaItemsDataView [itemId=misspagecheck]': {//漏页检测
                click: this.missPageCheck
            },
            'mediaItemsDataView [itemId=lookfileEle]':{//查看整卷文件
                click:this.lookfileEle
            },
            'mediaItemsDataView [itemId=transferApprove]':{//移交送审
                click:this.sendToCheck
            },
            "mediaItemsDataView button[itemId=backCapture]":{   //退回采集
                click:this.backCapture
            },
            'mediaItemsDataView [itemId=lookMeida]': {//查看
                click:this.lookHandler
            },

            'managementgrid [itemId=setSequence]':{  //排序设置
                click:function (btn) {
                    var view = Ext.create("Management.view.SetSortSequenceView");
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

            "managementgrid [itemId=backCapture]":{   //退回采集
                click:function (btn) {
                    var grid = this.getGrid(btn);
                    var selectAll=grid.down('[itemId=selectAll]').checked;
                    var tree = this.findGridView(btn).down('treepanel');
                    var node = tree.selModel.getSelected().items[0];
                    var record = grid.selModel.getSelection();
                    if (grid.selModel.getSelectionLength() == 0) {
                        XD.msg('请至少选择一条需要退回的数据');
                        return;
                    }
                    var backCaptureFormWin = Ext.create("Management.view.BackCaptureFormView", {
                        grid:grid,
                        nodeid:node.get('fnid')
                    });//传递innergrid对象
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
                    var tempParams = grid.getStore().proxy.extraParams;
                    tempParams['entryids'] = entryids;
                    tempParams['isSelectAll'] = isSelectAll;
                    var backCaptureForm=backCaptureFormWin.down('[itemId=formId]');
                    backCaptureForm.load({
                        url: '/management/getNewBackCaptureDoc',
                        params: tempParams,
                        success: function (form, action) {},
                        failure: function () {XD.msg('操作中断');}
                    });
                    backCaptureFormWin.show();
                }
            },

            "backCaptureFormView button[itemId=backCapture]":{   //退回
                click:function (btn) {
                    var backCaptureFormView = btn.findParentByType('backCaptureFormView');
                    var form = backCaptureFormView.down("form");
                    var entryids = form.down("[name=id]").getValue();
                    var backreason = form.down("[name=backreason]").getValue();
                    var backer = form.down("[name=backer]").getValue();
                    var backcount = form.down("[name=backcount]").getValue();
                    var backorgan = form.down("[name=backorgan]").getValue();
                    var backtime = form.down("[name=backtime]").getValue();
                    if(!form.isValid()){
                        XD.msg("存在必填项未填写");
                        return;
                    }
                    XD.confirm("是否确认退回采集",function () {
                        Ext.MessageBox.wait('正在退回采集请稍后...','提示');
                        Ext.Ajax.request({
                            url: '/management/backCaptureSubmit',
                            method: 'POST',
                            timeout: XD.timeout,
                            params: {
                                nodeid:backCaptureFormView.nodeid,
                                entryids:entryids,
                                backreason:backreason,
                                backer:backer,
                                backcount:backcount,
                                backorgan:backorgan,
                                backtime:backtime
                            },
                            success: function (opts) {
                                Ext.MessageBox.hide();
                                var responseText = Ext.decode(opts.responseText);
                                if(responseText.success == true){
                                    XD.msg(responseText.msg);
                                    backCaptureFormView.grid.getStore().reload();
                                    backCaptureFormView.close();
                                }else{
                                    XD.msg(responseText.msg+"，"+responseText.data);
                                }
                            },
                            failure: function () {
                                Ext.MessageBox.hide();
                                XD.msg('操作失败');
                            }
                        });
                    });
                }
            },

            "backCaptureFormView button[itemId=closeBtnID]":{   //退回采集 返回
                click:function (btn) {
                    btn.findParentByType("window").close();
                }
            },

            "managementgrid [itemId=lookBackCapture]":{   //查看退回
                click:function (btn) {
                    var tree = this.findGridView(btn).down('treepanel');
                    var node = tree.selModel.getSelected().items[0];
                    var lookBackCaptureDocViewWin = Ext.create('Ext.window.Window',{
                        modal:true,
                        width:1000,
                        height:600,
                        title:'查看退回单据',
                        layout:'fit',
                        closeToolText:'关闭',
                        closeAction:'hide',
                        items:[{
                            xtype: 'lookBackCaptureDocView',
                            nodeid:node.get("fnid")
                        }]
                    });
                    var lookBackCaptureDocView = lookBackCaptureDocViewWin.down('lookBackCaptureDocView');
                    var docGrid = lookBackCaptureDocView.down('lookBackCaptureDocGridView');
                    docGrid.initGrid({nodeid:node.get("fnid")});
                    lookBackCaptureDocViewWin.show();
                }
            },

            "lookBackCaptureDocGridView button[itemId=lookEntry]":{   //查看退回 详细内容
                click:function (btn) {
                    var lookBackCaptureDocGrid = btn.findParentByType('lookBackCaptureDocGridView');
                    var lookBackCaptureDocView = lookBackCaptureDocGrid.findParentByType("lookBackCaptureDocView");
                    var lookBackCaptureDocEntry = lookBackCaptureDocView.down("lookBackCaptureDocEntryView");
                    var select = lookBackCaptureDocGrid.getSelectionModel().getSelection();
                    if(select.length != 1){
                        XD.msg("只能选择一条数据");
                        return;
                    }
                    var backdocid = select[0].get("id");
                    lookBackCaptureDocEntry.initGrid({backdocid:backdocid,nodeid:lookBackCaptureDocView.nodeid});
                    this.activeDocEntryGrid(btn);
                }
            },

            "lookBackCaptureDocGridView button[itemId=lookBackId]":{   //查看退回 返回
                click:function (btn) {
                    btn.findParentByType('window').close();
                }
            },

            "lookBackCaptureDocEntryView button[itemId=look]": {   //查看退回 详细内容 查看
                click: function (btn) {
                    var grid = btn.findParentByType('lookBackCaptureDocEntryView');
                    var lookBackCaptureDocView = btn.findParentByType('lookBackCaptureDocView');
                    var form = lookBackCaptureDocView.down('formView').down('managementform').down('dynamicform');;
                    var records = grid.selModel.getSelection();
                    var nodeid = lookBackCaptureDocView.nodeid;
                    if (records.length == 0) {
                        XD.msg('请至少选择一条需要查看的数据');
                        return;
                    }
                    var entryids = [];
                    for (var i = 0; i < records.length; i++) {
                        entryids.push(records[i].get('entryid'));
                    }
                    var entryid = records[0].get('entryid');
                    var initFormFieldState = this.initFormField(form, 'hide', nodeid);
                    form.operate = 'look';
                    form.entryids = entryids;
                    form.entryid = entryids[0];
                    form.backDocType = true; //退回表单标志
                    if (!initFormFieldState) {//表单控件加载失败
                        return;
                    }
                    this.initFormData('look', form, entryid);
                    this.activeDocToForm(form);
                    this.loadFormRecord('look', form, entryid);//最后加载表单条目数据
                }
            },

            "lookBackCaptureDocView button[itemId=lookBackEntryId]": {   //查看退回 详细内容 返回
                click: function (btn) {
                    var lookBackCaptureDocView = btn.findParentByType('lookBackCaptureDocView');
                    var lookBackCaptureDocGridView = lookBackCaptureDocView.down("lookBackCaptureDocGridView");
                    lookBackCaptureDocView.setActiveItem(lookBackCaptureDocGridView);
                }
            }
        })
    },

    //切换到退回采集单个表单界面视图
    activeDocToForm: function (form) {
        var view = form.findParentByType('lookBackCaptureDocView');
        var formView = view.down('formView');
        var acquisitionform = formView.down('managementform');
        view.setActiveItem(formView);
        acquisitionform.items.get(0).enable();
        acquisitionform.setActiveTab(0);
        return formView;
    },

    //切换到查看退回采集单据界面的条目详情列表视图
    activeDocEntryGrid: function (btn) {
        var view = btn.findParentByType("lookBackCaptureDocView");
        var docentryview = view.down("lookBackCaptureDocEntryView");
        view.setActiveItem(docentryview);
        return docentryview;
    },

    backCapture:function (btn) {
        var grid = this.getGrid(btn);
        var selectAll
        var record;
        if(window.isMedia != true) {
            var selectAll = grid.down('[itemId=selectAll]').checked;
            if (grid.selModel.getSelectionLength() == 0) {
                XD.msg('请至少选择一条需要退回的数据');
                return;
            }
            record = grid.selModel.getSelection();
        }
        else{
            record = grid.acrossSelections;
            grid = grid.down('dataview');
        }

        var tree = this.findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];


        var backCaptureFormWin = Ext.create("Management.view.BackCaptureFormView", {
            grid:grid,
            nodeid:node.get('fnid')
        });//传递innergrid对象
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
        var tempParams = grid.getStore().proxy.extraParams;
        tempParams['entryids'] = entryids;
        tempParams['isSelectAll'] = isSelectAll;
        var backCaptureForm=backCaptureFormWin.down('[itemId=formId]');
        backCaptureForm.load({
            url: '/management/getNewBackCaptureDoc',
            params: tempParams,
            success: function (form, action) {},
            failure: function () {XD.msg('操作中断');}
        });
        backCaptureFormWin.show();
    },
    //移交送审
    sendToCheck:function (btn) {
        var grid = this.getGrid(btn);
        var selectAll;
        if(window.isMedia != true)
            selectAll = grid.down('[itemId=selectAll]').checked;

        var tree = this.findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        var record;
        if(grid.selModel == null) {
            if (grid.acrossSelections.length == 0) {
                XD.msg('请至少选择一条需要漏页检查的数据');
                return;
            }
            record = grid.acrossSelections;
            grid = grid.down('dataview');
        } else {
            if (grid.selModel.getSelectionLength() == 0) {
                XD.msg('请至少选择一条需要漏页检查的数据');
                return;
            }
            record = grid.selModel.getSelection();
        }
        var isSelectAll = false;
        if (selectAll) {
            record = grid.acrossDeSelections;
            isSelectAll = true;
        }
        var ids = [];
        for (var i = 0; i < record.length; i++) {
            ids.push(record[i].get('entryid'));
        }
        var transferApproveWin = Ext.create("Management.view.TransferApproveAddView");
        var params = grid.getStore().proxy.extraParams;
        var form = transferApproveWin.down('transferFormItemView');
        form.load({
            url:"/management/getTransferApprove",
            params: {
                ids:ids,
                nodeid: node.get("fnid"),
                isSelectAll:isSelectAll,
                basicCondition:params.condition,
                basicOperator: params.operator,
                basicContent:params.content
            },
            success: function (form, action) {
            },
            failure: function () {
                XD.msg('初始化移交表单失败');
            }
        });
        var transferFormGridStore = transferApproveWin.down('transferFormGridView').getStore();
        transferFormGridStore.proxy.extraParams.ids = ids;
        transferFormGridStore.proxy.extraParams.nodeid = node.get("fnid");
        transferFormGridStore.proxy.extraParams.isSelectAll = isSelectAll;
        transferFormGridStore.proxy.extraParams['basicCondition'] = params.condition;
        transferFormGridStore.proxy.extraParams['basicOperator'] = params.operator;
        transferFormGridStore.proxy.extraParams['basicContent'] = params.content;
        transferFormGridStore.reload();
        //机构id
        var organid = this.getOrganid(node.get("fnid"));
        form.organid = organid;
        form.nodeid = node.get("fnid");
        form.ids = ids;
        form.isSelectAll = isSelectAll;
        form.condition = params.condition;
        form.operator = params.operator;
        form.content = params.content;
        transferApproveWin.show();
    },
    lookfileEle:function (btn) {
        var grid = this.getGrid(btn);
        var select;
        if(window.isMedia != true) {
            select = grid.getSelectionModel().getSelection();
        }
        else {
            select = grid.acrossSelections;
        }
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
    },

    //进入模块主页面时加载列表数据
    initGrid:function(view){
        // var tree = this.findGridView(view).down('treepanel');
        // var selectedNode = tree.selModel.getSelected().items[0];
        // if(selectedNode){
        //     return;
        // }
        // Ext.defer(function(){
        //     view.nodeid = tree.getStore().getRoot().firstChild.get('fnid');
        //     view.getStore().proxy.extraParams.nodeid = view.nodeid;//加载列表数据
        //     view.initColumns(view);
        //     view.initGrid();
        // },1);
    },

//    //获取数据管理应用视图
//    findView:function(btn){
//        return btn.findParentByType('management');
//    },
    
    //获取数据管理应用视图
    findView:function(btn){
        return btn.up('managementFormAndGrid');
    },
    findTreeView : function (btn) {
        return btn.up('managementFormAndGrid').down('treepanel');
    },
    //获取表单界面视图
    findFormView: function (btn) {
        return this.findView(btn).down('formAndGrid').down('managementform');
    },
    
    findFormInnerView: function (btn) {
		return this.findView(btn).down('formAndInnerGrid').down('managementform');
    },
    
    findFormToView: function (btn) {
    	return this.findView(btn).down('formView').down('managementform');
    },
    
    findDfView: function (btn) {
    	return this.findView(btn).down('formView').down('managementform').down('dynamicform');
    },

    //获取列表界面视图
    findGridView:function(btn){
        return this.findView(btn).getComponent('gridview');
    },
    
    findGridToView: function (btn) {
    	return this.findView(btn).down('formAndGrid').down('managementgrid');
    },
    
    findInnerGridView: function (btn) {
    	return this.findView(btn).down('formAndInnerGrid').down('managementgrid');
    },
    
    findSequenceView: function(btn) {
    	return btn.findParentByType('managementSequenceView').down('managementSequenceGridView');
    },

    //获取数据归档视图
    findFilingView:function (btn) {
        return btn.up('managementfiling');
    },

    //获取数据归档第一步窗口视图
    findFilingFirstFormView:function (btn) {
        return this.findFilingView(btn).down('[itemId=filingFirstStep]');
    },

    //获取数据归档第二步窗口视图
    findFilingFormAndGridView:function (btn) {
        return this.findFilingView(btn).down('[itemId=filingSecondStep]');
    },

    //获取数据归档第二步窗口中表单视图
    findFilingFormView:function (btn) {
        return this.findFilingFormAndGridView(btn).down('dynamicfilingform');
    },

    //获取数据归档第二步窗口中列表视图
    findFilingGridView:function (btn) {
        return this.findFilingFormAndGridView(btn).down('entrygrid');
    },
    
    //分类设置视图
//    findClassificationFirstView:function (btn) {
//    	return this.findClassificationGrid(btn).down('[itemId=classificationFirstStep]');
//    },
//    
//    findClassificationSencondView:function (btn) {
//    	return this.findClassificationGrid(btn).down('[itemId=classificationSecondStep]');
//    },
//    
//    findClassificationGridView:function (btn) {
//        return this.findClassificationGrid(btn).down('entrygrid');
//    },

    findActiveGrid:function(btn){
        var active = this.findView(btn).down('[itemId=gridcard]').getLayout().getActiveItem();
        if (active.getXType() == "managementgrid" || active.getXType() == 'mediaItemsDataView') {//添加声像页面的获取
            return active;
        }else if(active.getXType() == "panel"){
            return active.down('[itemId=northgrid]');
        }
    },
    
    findActiveInnerGrid:function(btn){
    	var active = this.findView(btn).down('[itemId=gridcard]').getLayout().getActiveItem();
        if(active.getXType() == "managementgrid"){
            return active;
        }else if(active.getXType() == "panel"){
            return this.findView(btn).down('formAndInnerGrid').down('managementgrid');
        }
    },

    findInnerGrid:function(btn){
        return this.findView(btn).down('[itemId=southgrid]');
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
        if (form.findParentByType('formView')) {
        	var managementform = formAndGridView.down('formView').down('managementform');
        	savebtn = managementform.down('[itemId=save]');
        	continuesave = managementform.down('[itemId=continuesave]');
        	tbseparator = managementform.getDockedItems('toolbar')[0].query('tbseparator');
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
        view.setActiveItem(this.findGridView(btn));
        formAndGridView.setActiveItem(this.findGridView(btn));
        this.findFormView(btn).saveBtn = undefined;
        this.findFormView(btn).continueSaveBtn = undefined;
        this.findFormInnerView(btn).saveBtn = undefined;
        this.findFormInnerView(btn).continueSaveBtn = undefined;
        var allMediaFrame = document.querySelectorAll('#mediaFrame');
        if(allMediaFrame){
            for (var i = 0; i < allMediaFrame.length; i++) {
                allMediaFrame[i].setAttribute('src','');
            }
        }
        if(document.getElementById('solidFrame')){
            document.getElementById('solidFrame').setAttribute('src','');
        }
        // if(document.getElementById('longFrame')){
        //     document.getElementById('longFrame').setAttribute('src','');
        // }
        if(flag){//根据参数确定是否需要刷新数据
            var grid = this.findActiveGrid(btn);
//            grid.initGrid();
            grid.notResetInitGrid();
        }
    },

    //切换到表单界面视图
    activeForm: function (form) {
        var view = this.findView(form);
        var formAndGridView = view.down('formAndGrid');//保存表单与表格视图
        view.setActiveItem(formAndGridView);
        
        var formview = formAndGridView.down('managementform');
        formview.items.get(0).enable();
        formview.setActiveTab(0);
        return formAndGridView;
    },
    
    //切换到卷内表单界面视图
    activeInnerForm: function (form) {
        var view = this.findView(form);
        var formAndInnerGridView = view.down('formAndInnerGrid');//保存表单与表格视图
        var formview = formAndInnerGridView.down('managementform');
        view.setActiveItem(formAndInnerGridView);
        formview.items.get(0).enable();
        formview.setActiveTab(0);
        return formAndInnerGridView;
    },
    
    //切换到单个表单界面视图
    activeToForm: function (form) {
    	var view = this.findView(form);
    	var formView = view.down('formView');
    	var managementform = formView.down('managementform');
    	view.setActiveItem(formView);
    	managementform.items.get(0).enable();
    	managementform.setActiveTab(0);
    	return formView;
    },
    
    initSouthGrid:function (form) {
        var formAndGridView = this.findView(form).down('formAndGrid');//保存表单与表格视图
        var gridview = formAndGridView.down('managementgrid');
        gridview.initGrid({nodeid:form.nodeid});
    },
    
    //获取到卷内的表格
    initSouthInnerGrid:function (form, entryid) {
    	var formAndInnerGrid = this.findView(form).down('formAndInnerGrid');//保存表单与表格视图
        var gridview = formAndInnerGrid.down('managementgrid');
        gridview.dataUrl = '/management/entries/innerfile/'+entryid + '/';
        gridview.initGrid({nodeid:form.nodeid});
    },
    
    activeEleForm:function(obj){
        var view = this.findView(obj.grid);
        var formview = null;
        if (obj.grid.xtype == 'dataEventDetailGridView') {
        	formview = formAndGridView.down('formView').down('managementform');
        	formAndGridView.type = '数据关联';
        	var savebtn = formview.down('[itemId=save]');
        	var continuesavebtn = formview.down('[itemId=continuesave]');
        	savebtn.hide();
        	continuesavebtn.hide();
        } else {
            formview = this.getCurrentManagementform(obj.grid);
        }
        view.setActiveItem(formview.findParentByType('panel'));
        formview.items.get(0).disable();
        var eleview = formview.down('electronic');
        var solidview = formview.down('solid');
        eleview.operateFlag = "look"; //电子文件查看标识符
        solidview.operateFlag = "look";//利用文件查看标识符
        eleview.initData(obj.entryid);
        solidview.initData(obj.entryid);
        var from =formview.down('dynamicform');
        //原始文件工具栏按钮
        var elebtns = eleview.down('toolbar').query('button');
        from.getELetopBtn(elebtns,eleview.operateFlag);
        //固化文件工具栏按钮
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

    //加载新案卷表单
    initNewFileFormData:function(form, entryid,archivecode){
        form.reset();
        var eleview = this.getCurrentManagementform(form).down('electronic');
        var solidview = this.getCurrentManagementform(form).down('solid');
        var filingyearField = form.getForm().findField('filingyear');
        var descriptiondateField = form.getForm().findField('descriptiondate');
        Ext.Ajax.request({
            url: '/management/initNewFileFormData/'+entryid+"/"+archivecode+"/"+form.nodeid,
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

    itemclickHandler: function(view, record, item, index, e){
        var fileArchivecode = record.get('archivecode');//案卷档号
        //用于案卷点击显示卷内文件条目，之前是根据案卷档号匹配，改为通过entryid获取档号设置，通过档号设置字段匹配
        var entryid =record.get('entryid');
        window.fileArchivecode = fileArchivecode;
        var southgrid = this.findInnerGrid(view);
        southgrid.dataUrl = '/management/entries/innerfile/'+entryid + '/';
        var nodeid = this.getNodeid(record.get('nodeid'));
        southgrid.initGrid({nodeid:nodeid});
        southgrid.setTitle('查看'+fileArchivecode+'案卷的卷内');
        var treeSelctedRecord = this.findGridView(view).down('treepanel').selModel.getSelected().items[0];//获取当前左侧树所选取的节点（与对应卷内文件同级）
        var fullname=treeSelctedRecord.get('text');
        while(treeSelctedRecord.parentNode.get('text')!='数据管理'){
            fullname=treeSelctedRecord.parentNode.get('text')+'_'+fullname;
            treeSelctedRecord=treeSelctedRecord.parentNode;
        }
        southgrid.nodefullname = fullname.substring(0,fullname.lastIndexOf('_'))+'_'+'卷内文件';
        southgrid.parentXtype = 'managementFormAndGrid';
        southgrid.formXtype = 'managementform';
    },

    // southrender:function(grid){
    //     var items = this.findInnerGrid(grid).getDockedItems('toolbar[dock="top"]')[0].items.items;
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

    initFormData:function(operate, form, entryid, state){
        var nullvalue = new Ext.data.Model();
        var managementform = form.up('managementform');
        var fields = form.getForm().getFields().items;
        var prebtn = form.down('[itemId=preBtn]');
        var nextbtn = form.down('[itemId=nextBtn]');
        var savebtn = managementform.down('[itemId=save]');
        var continuesavebtn = managementform.down('[itemId=continuesave]');
        var count = 1;
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
        var etips = form.up('managementform').down('[itemId=etips]');
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
        var eleview = this.getCurrentManagementform(form).down('electronic');
        var solidview = this.getCurrentManagementform(form).down('solid');
        // var longview = this.getCurrentManagementform(form).down('long');
        if(operate == 'modify'){//修改的时候需要直接加载原文列表，以免保存为0
            eleview.initData(entryid);
        }else{
            eleview.initData();
        }
        solidview.initData();

//        form.formStateChange(operate);
        form.fileLabelStateChange(eleview,operate);
        form.fileLabelStateChange(solidview,operate);
        // form.fileLabelStateChange(longview,operate);
        this.changeBtnStatus(form,operate);
    },

    //加载默认数据或条目数据
    loadFormRecord:function(operate, form, entryid, state){
        var managementform = form.up('managementform');
        var prebtn = form.down('[itemId=preBtn]');
        var nextbtn = form.down('[itemId=nextBtn]');
        var savebtn = managementform.down('[itemId=save]');
        var continuesavebtn = managementform.down('[itemId=continuesave]');
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
                url: '/management/entries/' + entryid,
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
                        delete entry.entrystorage;  //删除库房位置
                        delete entry.flagopen;   //删除开放状态
                        entry.filingyear = new Date().getFullYear();
                        entry.descriptiondate = Ext.util.Format.date(new Date(),'Y-m-d H:i:s');
                        if (data.keyword && entry.keyword) {
                            entry.keyword = data.keyword;//主题词
                        }
                        entry.descriptionuser =userRealname;
                    }
                    if (operate == 'add' || operate == 'modify') {
                        if (!data.organ) {
                            if(window.organTitle){
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
                    var fieldCode = form.getRangeDateForCode();//字段编号，用于特殊的自定义字段(范围型日期)
                    if (fieldCode != null) {
                        //动态解析数据库日期范围数据并加载至两个datefield中
                        form.initDaterangeContent(entry);
                    }
                }
            });
        }
    },
    
    getInfo: function (nodeid) {
    	var res;
    	Ext.Ajax.request({
			async:false,
            method: 'get',
            params:{
                nodeid:nodeid
            },
            url: '/nodesetting/getRefid/',//通过节点id获取机构id
            success:function (response) {
            	res = response;
            }
		});
		return res;
    },

    initAdvancedSearchFormField:function(form, nodeid){
        if (form.nodeid != nodeid) {//切换节点后，form和tree的节点id不相等
            form.nodeid = nodeid;//用左侧树节点的id初始化form的nodeid参数
            form.removeAll();//移除form中的所有表单控件
            var formField = form.getFormField();//根据节点id查询表单字段
            formField.type = '高级检索';
            if(formField.length==0){
                XD.msg('请检查模板设置信息是否正确');
                return;
            }
            form.templates = formField;
            form.initSearchConditionField(formField);//重新动态添加表单控件
        }
        return '加载表单控件成功';
    },
    
    getGrid: function (btn) {
    	var grid;
        if (!btn.findParentByType('formAndGrid')) {
        	grid = this.findActiveGrid(btn);
        } else {
        	grid = this.findGridToView(btn);
        }
        return grid;
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

    doCodesettingAlign:function (btn) {//档号对齐（对所有检索出的数据进行操作）
        //  var grid = this.findView(btn).down('codesettingAlignResultGridView');
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

        var tree = this.findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        var SimulationArchivecode;
        Ext.Ajax.request({
            url: '/codesetting/getSimulationArchivecode',
            async:false,
            params: {
                nodeid: node.get('fnid')
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
        var alignConfirmMsg = '本次操作将依据档号设置中设定的单位长度（当前档号设置的长度：'+SimulationArchivecode+'），在所有检索结果的想要字段前面补0至指定长度（值超过设定的单位长度将不处理），是否继续？';
        Ext.Ajax.request({
            url: '/codesetting/getCodeSettingFields',
            async:false,
            params: {
                nodeid: node.get('fnid')
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

    //生成二维码
    downloadQRCode: function (btn) {
        var gridview = btn.up('managementgrid');
        var records = gridview.getSelectionModel().getSelection();
        var selectCount = gridview.getSelectionModel().getSelectionLength();
        if (selectCount == 0) {
            XD.msg('请选择数据后再进行操作！');
            return;
        }
        var selectAll = gridview.down('[itemId=selectAll]').checked;
        var isSelectAll = false;
        if (selectAll) {
            records = gridview.acrossDeSelections;
            isSelectAll = true;
        }
        var tmp = [];
        for (var i = 0; i < records.length; i++) {
            tmp.push(records[i].get('entryid'));
        }
        var entryids = tmp.join(',');

        var selectItem = Ext.create("Management.view.QRCodeGroupSetView");
        selectItem.items.get(0).getStore().load();
        selectItem.down('[itemId=printSave]').hide();
        selectItem.show();
        QRCodeParams = [];
        QRCodeParams['entryids'] = entryids;
        QRCodeParams['isSelectAll'] = isSelectAll;
        QRCodeParams['nodeid'] = NodeIdf;
    },

    printQRCode: function (btn) {
        var gridview = btn.up('managementgrid');
        var records = gridview.getSelectionModel().getSelection();
        var selectCount = gridview.getSelectionModel().getSelectionLength();
        if (selectCount == 0) {
            XD.msg('请选择数据后再进行操作！');
        }
        var selectAll = gridview.down('[itemId=selectAll]').checked;
        var isSelectAll = false;
        if (selectAll) {
            records = gridview.acrossDeSelections;
            isSelectAll = true;
        }
        var tmp = [];
        for (var i = 0; i < records.length; i++) {
            tmp.push(records[i].get('entryid'));
        }
        var entryids = tmp.join(',');
        var selectItem = Ext.create("Management.view.QRCodeGroupSetView");
        selectItem.items.get(0).getStore().load();
        selectItem.down('[itemId=save]').hide();
        selectItem.show();
        QRCodeParams = [];
        QRCodeParams['entryids'] = entryids;
        QRCodeParams['isSelectAll'] = isSelectAll;
        QRCodeParams['nodeid'] = NodeIdf;
    },

    prinQR:function(view){
        //生成二维码 返回路径参数
        var html = "";
        var newhtml = "";
        var selectView = view.findParentByType('QRCodeGroupSetView');
        var fieldCode = selectView.items.get(0).getValue();
        if (fieldCode.length == 0) {
            XD.msg("请选择需要导出的字段");
            return;
        }
        Ext.MessageBox.wait('正在处理请稍后...');
        Ext.Ajax.request({
            url: '/management/printQRCode',
            timeout: XD.timeout,
            scope: this,
            async: false,
            params: {
                entryids:QRCodeParams['entryids'],
                isSelectAll:QRCodeParams['isSelectAll'],
                nodeid: QRCodeParams['nodeid'],
                userFieldCode:fieldCode
            },
            method: 'POST',
            success: function (resp) {
                var obj = Ext.decode(resp.responseText).data;
                var delPath = obj.delPath;
                Ext.each(obj.QRCodePath, function (str) {
                    //var s1 = str.substring(str.indexOf("\\"),str.length);
                    var imgstr = "<img src='data:img/jpg;base64," + str + "' width='150px' height='200px'" + ">";
                    // var imgstr="<img src='data:img/jpg;base64,"+"+str+"+"' width='150px' height='200px'"+">";
                    html += imgstr;
                });
                //newhtml="<div id='princodes'>"+newhtml+"</div>";
                var win = Ext.create('Ext.window.Window', {
                    header: false,
                    maximized: true,
                    closeToolText: '关闭',
                    //html:'<iframe src="' + reportUrl + '" frameborder="0" style="width: 100%;height: 100%"></iframe>',
                    html: html,
                    buttons: [{
                        text: '关闭',
                        handler: function () {
                            win.close();
                            Ext.MessageBox.hide();
                            //进行删除文件操作
                            Ext.Ajax.request({
                                url: '/management/delPrintQRCode',
                                timeout: XD.timeout,
                                scope: this,
                                async: true,
                                params: {
                                    delPath: delPath
                                },
                                method: 'POST',
                                success: function (resp) {
                                }
                            });
                        }
                    }, {
                        text: '打印',
                        handler: function () {
                            //判断iframe是否存在，不存在则创建iframe
                            iframe = document.getElementById("print-iframe");
                            if (!iframe) {
                                //var el = document.getElementById("printcontent");
                                var iframe = document.createElement('IFRAME');
                                var doc = null;
                                iframe.setAttribute("id", "print-iframe");
                                iframe.setAttribute('style', 'position:absolute;width:0px;height:0px;left:-500px;top:-500px;');
                                document.body.appendChild(iframe);
                                doc = iframe.contentWindow.document;
                                //这里可以自定义样式
                                //doc.write("<LINK rel="stylesheet" type="text/css" href="css/print.css">");
                                doc.write('<div>' + html + '</div>');
                                doc.close();
                                iframe.contentWindow.focus();
                                Ext.MessageBox.hide();
                                iframe.contentWindow.print();
                                //进行删除文件操作
                                Ext.Ajax.request({
                                    url: '/management/delPrintQRCode',
                                    timeout: XD.timeout,
                                    scope: this,
                                    async: true,
                                    params: {
                                        delPath: delPath
                                    },
                                    method: 'POST',
                                    success: function (resp) {

                                    }
                                });
                            } else {
                                var iframe = document.getElementById("print-iframe");
                                var doc = iframe.contentWindow.document;
                                doc.write('<div>' + html + '</div>');
                                doc.close();
                                iframe.contentWindow.focus();
                                Ext.MessageBox.hide();
                                iframe.contentWindow.print();//js执行print函数会阻塞 后面的代码不会执行 只有关掉打印页面才会继续
                                //进行删除文件操作
                                Ext.Ajax.request({
                                    url: '/management/delPrintQRCode',
                                    timeout: XD.timeout,
                                    scope: this,
                                    async: true,
                                    params: {
                                        delPath: delPath
                                    },
                                    method: 'POST',
                                    success: function (resp) {

                                    }
                                });
                            }
                        }
                    }]
                });
                win.show();
            },
            failure: function (resp) {
                Ext.MessageBox.hide();
                XD.msg('加载失败');
            }
        });
    },
    
    //数据著录
    saveHandler:function(btn){
    	formvisible = true;
        formlayout = 'formgrid';
        var managementform = this.findFormView(btn);
        managementform.down('electronic').operateFlag='add';
        managementform.saveBtn = managementform.down('[itemId=save]');
        managementform.continueSaveBtn = managementform.down('[itemId=continuesave]');
        managementform.operateFlag = 'add';
        var grid = this.getGrid(btn);
        var form = managementform.down('dynamicform');
        var tree = this.findGridView(btn).down('treepanel');
        var selectCount;
        var selectAll;
        if(grid.selModel == null)
            selectCount= grid.acrossSelections.length;
        else{
            selectCount= grid.selModel.getSelection().length;
            selectAll = grid.down('[itemId=selectAll]').checked;
            if (selectAll) {
                selectCount = grid.selModel.selected.length;//当前页选中
            }
        }
        var node = tree.selModel.getSelected().items[0];
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
			        this.initFormData('add',form,'','案卷著录');
		            this.activeForm(form);
                    this.loadFormRecord('add',form,'','案卷著录');//最后加载表单条目数据
		        	this.initSouthGrid(form);
		        }else if(selectCount!=1){
		            XD.msg('只能选择一条数据')
		        } else {
		            //选择数据著录，则加载当前数据到表单界面
		            var entryid;
                    if(window.isMedia == true)
                        entryid = grid.acrossSelections[0].get('entryid');
                    else
                        if (selectAll) {
                            entryid = grid.selModel.selected.items[0].get("entryid");
                        } else {
                            entryid = grid.selModel.getSelection()[0].get("entryid");
                        }
		            this.initFormData('add', form, entryid,'案卷数据著录');
		            this.activeForm(form);
                    this.loadFormRecord('add', form, entryid,'案卷数据著录');//最后加载表单条目数据
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

    //卷内文件著录
    isaveHandler:function(btn){
    	formvisible = true;
        formlayout = 'forminnergrid';
        var grid = this.getInnerGrid(btn);
    	var lastGrid = this.findActiveGrid(btn);
    	var count = lastGrid.selModel.getSelection().length;
    	if (count == 0) {
    		XD.msg('请选择一条需要进行卷内文件操作的数据')
    	} else if (count > 1) {
    		XD.msg('只能选择一条数据进行卷内文件操作')
    	} else {
            //比较案卷和卷内的档号设置是否一致，除了卷内文件档号组成字段多出最后一个“卷内顺序号”外，其它都要一致(字段、顺序、分隔符、长度都要一致)
            var res = this.compareCodeset(lastGrid.dataParams.nodeid,grid.dataParams.nodeid);
            var responseText=Ext.decode(res.responseText);
            if (responseText.success==false) {
                XD.msg(responseText.msg);
                return ;
            }
    		var managementform = this.findFormInnerView(btn);
            managementform.down('electronic').operateFlag='add';
	        managementform.saveBtn = managementform.down('[itemId=save]');
	        managementform.continueSaveBtn = managementform.down('[itemId=continuesave]');
	        managementform.operateFlag = 'add';
	        var form = managementform.down('dynamicform');
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
			            this.initFormData('add',form, lastGrid.selModel.getSelection()[0].get('entryid'), '卷内著录');
			            this.activeInnerForm(form);
                        this.loadFormRecord('add',form, lastGrid.selModel.getSelection()[0].get('entryid'), '卷内著录');//最后加载表单条目数据
		        		this.initSouthInnerGrid(form, entryid);
			        }else if(selectCount!=1){
			            XD.msg('只能选择一条数据');
			        } else {
			            //选择数据著录，则加载当前数据到表单界面
			            this.initFormData('add',form, grid.selModel.getSelection()[0].get('entryid'), '卷内数据著录');
			            this.activeInnerForm(form);
                        this.loadFormRecord('add',form, grid.selModel.getSelection()[0].get('entryid'), '卷内数据著录');//最后加载表单条目数据
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

    //修改
    modifyHandler:function(btn){
    	formvisible = true;
        formlayout = 'formview';
        var managementform = this.findFormToView(btn);
        managementform.down('electronic').operateFlag='modify';
        managementform.down('electronic').eletype = "management";
        // managementform.down('electronic').down('[itemId=toolbar2]').hide()  //隐藏保存版本 查看历史按钮
        managementform.saveBtn = managementform.down('[itemId=save]');
        managementform.continueSaveBtn = managementform.down('[itemId=continuesave]');
        managementform.operateFlag = 'modify';
        var grid = this.getGrid(btn);
        var form = managementform.down('dynamicform');
        var record;
        var selectAll;
        if(grid.selModel == null)
            record= grid.acrossSelections;
        else{
            record = grid.selModel.getSelection();
            selectAll = grid.down('[itemId=selectAll]').checked
        }
        var selectCount = record.length;
        if(selectAll){
            XD.msg('不支持选择所有页修改');
            return;
        }
        var tree = this.findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if(!node){
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
		        //     entryid = record[0].get("entryid");
		        // }
                var entryids = [];
                for(var i=0;i<record.length;i++){
                    entryids.push(record[i].get('entryid'));
                }
                form.operate = 'modify';
                form.entryids = entryids;
                form.entryid = entryids[0];
		        this.initFormData('modify', form, entryids[0]);
		        this.activeToForm(form);
                this.loadFormRecord('modify', form, entryids[0]);//最后加载表单条目数据
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
        var managementform = this.findFormToView(btn);
        managementform.down('electronic').operateFlag='modify';
        managementform.down('electronic').eletype = "management";
        managementform.saveBtn = managementform.down('[itemId=save]');
        managementform.continueSaveBtn = managementform.down('[itemId=continuesave]');
        managementform.operateFlag = 'modify';
        var grid = this.getInnerGrid(btn);
        var lastGrid = this.findActiveGrid(btn);
        var form = managementform.down('dynamicform');
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
    
    getCodesetting: function (nodeid,form){
    	var isExist = false;//档号构成字段的集合
        /*if(nodeid==window.nodeid){//与页面全局变量一致
            if(window.codeset){//存在档号设置
                return true;
            }else{
                return false;
            }
        }*/
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

    //删除
    delHandler:function(btn){
        var grid = this.getGrid(btn);
        var selectAll;
        var selLen;
        if(window.isMedia != true){
            selectAll = grid.down('[itemId=selectAll]').checked;
            selLen = grid.selModel.getSelectionLength();
        }
        else {
            selLen = grid.acrossSelections.length;
        }
        var tree = this.findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if(!node){
            XD.msg('请选择节点');
            return;
        }
        if (selLen == 0) {
            XD.msg('请至少选择一条需要删除的数据');
            return;
        }
        XD.confirm('确定要删除这 '+selLen+' 条数据吗?',function(){
            var record;
            if(window.isMedia != true)
                record = grid.selModel.getSelection();
            else
                record = grid.acrossSelections;
            var isSelectAll = false;
            if(selectAll){
                record = grid.acrossDeSelections;
                isSelectAll = true;
            }
            var tmp = [];
            for(var i = 0; i < record.length; i++){
                tmp.push(record[i].get('entryid'));
            }
            var entryids = tmp.join(',');
            // var tempParams = grid.getStore().proxy.extraParams;
            var tempParams;
            if(window.isMedia == true)
                tempParams = grid.down('dataview').getStore().proxy.extraParams
            else
                tempParams = grid.getStore().proxy.extraParams;
            tempParams['entryids'] = entryids;
            tempParams['isSelectAll'] = isSelectAll;
            tempParams['model'] = "数据管理:"+grid.nodefullname;
            Ext.Msg.wait('正在删除数据，请耐心等待……', '正在操作');
            Ext.Ajax.request({
                method: 'post',
                scope: this,
                url: '/management/delete',
                params:tempParams,
                timeout:XD.timeout,
                success:function(response) {
                    var resp = Ext.decode(response.responseText);
                        if('无法删除'==resp.msg){
                            Ext.Msg.close();
                            var titles = resp.data;
                            var title;
                            for(var i=0;i<titles.length;i++){
                                if(i==0){
                                    title = '['+titles[i]+']';
                                }else{
                                    title = title + '，' + '['+titles[i]+']';
                                }
                            }
                            XD.msg('无法删除，这  '+titles.length+'  条题名为  '+title+'  还处于未归状态')
                        }else {
                            XD.msg(resp.msg);
                            if(window.isMedia == true)
                                grid.initGrid({nodeid: node.data.fnid});
                            else {
                                grid.getStore().proxy.url = '/management/entriesPost';
                                grid.getStore().proxy.extraParams.entryids = '';
                                grid.delReload(grid.selModel.getSelectionLength());
                                //grid.initGrid({nodeid: node.data.fnid},true);//刷新整个数据管理列表以及下面的数据显示
                                this.findInnerGrid(btn).getStore().removeAll();
                                this.findInnerGrid(btn).setTitle('查看卷内');
                            }
                        Ext.MessageBox.hide();
                    }
                }
            })
        },this);
    },

    idelHandler: function (btn) {
		var grid = this.getInnerGrid(btn);
        var nodeid = grid.dataParams.nodeid;
        var record = grid.selModel.getSelection();
        if (record.length == 0) {
            XD.msg('请至少选择一条需要删除的数据');
            return;
        }
        XD.confirm('确定要删除这' + record.length + '条数据吗?', function () {
            var tmp = [];
            for (var i = 0; i < record.length; i++) {
                tmp.push(record[i].get('entryid'));
            }
            var entryids = tmp.join(',');
            var tempParams = grid.getStore().proxy.extraParams;
            tempParams['entryids'] = entryids;
            tempParams['isSelectAll'] = false;
            tempParams['model'] = "数据管理:"+grid.nodefullname;
            Ext.Msg.wait('正在删除数据，请耐心等待……', '正在操作');
            Ext.Ajax.request({
                method: 'post',
                url: '/management/delete' ,
                params:tempParams,
                success: function (response) {
                    var resp = Ext.decode(response.responseText);
                    if('无法删除'==resp.msg){
                        Ext.Msg.close();
                        var titles = resp.data;
                        var title;
                        for(var i=0;i<titles.length;i++){
                            if(i==0){
                                title = '['+titles[i]+']';
                            }else{
                                title = title + '，' + '['+titles[i]+']';
                            }
                        }
                        XD.msg('无法删除，这  '+titles.length+'  条题名为  '+title+'  还处于未归状态')
                    } else {
                        XD.msg(resp.msg);
//                        grid.initGrid({nodeid: nodeid});//刷新整个数据管理列表以及下面的数据显示
                        grid.delReload(record.length);
                    }
                    Ext.MessageBox.hide();
                }
            })
        },this);
    },

    //查看
    lookHandler:function(btn){
        var grid = this.getGrid(btn);
        var form = this.findDfView(btn);
        var record;
        var selectAll;
        if(window.isMedia != true) {
            record = grid.selModel.getSelection();
            selectAll = grid.down('[itemId=selectAll]').checked;
        }
        else
            record = grid.acrossSelections;
        var selectCount = record.length;

        // if (selectAll) {
        //     selectCount = grid.selModel.selected.length;//当前页选中
        // }
        if (selectAll) {
            XD.msg('不支持选择所有页查看');
            return;
        }
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
        //         XD.msg('请选择一条数据');
        //     }
        //     return;
        // }
        if (selectCount == 0) {
            XD.msg('请至少选择一条需要查看的数据');
            return;
        }
        var initFormFieldState = this.initFormField(form, 'hide', node.get('fnid'));
        if (!initFormFieldState) {//表单控件加载失败
            return;
        }
        // var entryid;
        // if (selectAll) {
        //     entryid = grid.selModel.selected.items[0].get("entryid");
        // } else {
        //     entryid = record[0].get("entryid");
        // }
        var entryids = [];
        for (var i = 0; i < record.length; i++) {
            entryids.push(record[i].get('entryid'));
        }
        form.operate = 'look';
        form.entryids = entryids;
        form.entryid = entryids[0];
        this.initFormData('look', form, entryids[0]);
        this.activeToForm(form);
        this.loadFormRecord('look', form, entryids[0]);//最后加载表单条目数据
    },

    ilookHandler: function (btn) {
        var grid = this.getInnerGrid(btn);
        var form = this.findDfView(btn);
        var records = grid.selModel.getSelection();
        var nodeid = grid.dataParams.nodeid;
        // if (records.length == 0) {
        //     XD.msg('请选择一条数据');
        //     return;
        // }
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
        var selectAll
        if(window.isMedia != true) {
            records = grid.selModel.getSelection();
            selectAll = grid.down('[itemId=selectAll]').checked;
        }
        else
            records = grid.acrossSelections;
        var selectCount = records.length;

        if (selectAll) {
            selectCount = grid.selModel.selected.length;//当前页选中
        }
        if (selectCount == 0) {
            if (selectAll) {
                XD.msg('当前页没有选择数据');
            } else {
                XD.msg('请选择一条数据');
            }
            return;
        }
        if (selectCount != 1) {
            XD.msg('查看只能选中一条数据');
            return;
        }
        var entryid = records[0].get('entryid');
        var nodeid;
        Ext.Ajax.request({
            url: '/management/fileNodeidAndEntryid',
            async: false,
            params: {
                entryid: entryid
            },
            success: function (response) {
                var data = Ext.decode(response.responseText);
                entryid = data.entryid;
                nodeid = data.nodeid;
            },
            failure: function (response) {
                entryid = '';
            }
        });
        //如果entryid为空字符串，即找不到案卷
        if (entryid == '') {
            XD.msg('无对应的案卷记录，请编制新案卷后再进行查看');
            return;
        }
        var entryids = [];
        entryids.push(entryid);
        form.entryids = entryids;
        form.winType = 'gllook';//数据管理标记,日志用
        var initFormFieldState = this.initFormField(form, 'hide', nodeid);
        if (!initFormFieldState) {//表单控件加载失败
            return;
        }
        this.initFormData('lookfile', form, entryid,'',nodeid);
        this.activeToForm(form);
        this.loadFormRecord('lookfile', form, entryid,'',nodeid);//最后加载表单条目数据
    },
    
    //数据转移
    dataTransforHandler:function (btn) {
    	var record = formAndGridView.down('managementgrid').selModel.selected;
    	if (record.length < 1) {
    		XD.msg('请至少选择一条数据进行数据转移！');
    		return;
    	}
    	var form = Ext.create("Management.view.ManagementSelectWin",{height:window.innerHeight * 6 / 7,width:420});
    	var treeView = form.down("managementSelectView");
    	
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
    	managementSelectWin = btn.up('managementSelectWin');
    	var nodeid = formAndGridView.down('managementgrid').dataParams.nodeid;
    	
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
			                xtype: 'managementFieldView'
			            }]
			        });
			        fieldView.show();
			        var managementFieldView = fieldView.down('managementFieldView');
			        managementFieldView.nodeid = nodeid;
	                //提交成功后，刷新字段设置
	                var workspace = managementFieldView.down('[itemId=workspace]');
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
    	var entryid = "";
    	var grid = formAndGridView.down('managementgrid');
    	//查找到字段设置视图
		var managementFieldView = btn.up('managementFieldView');
		
    	var fieldCodes = "";
    	var targetFieldCodes = "";
		var field = managementFieldView.down('[dataIndex=fieldName]');
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
		var record;
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
            url: '/transfor/entriesTransfer',
            timeout:XD.timeout,
            params: {
            	nodeid: managementFieldView.nodeid,
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
            	XD.msg(respText.msg);
            	// 关闭字段设置窗口
            	btn.up('window').hide();
                // 关闭数据转移窗口
        		// “XX条数据从【节点名称】转移到【节点名称】”提示语
            	managementSelectWin.hide();
        		// 刷新数据管理表单
        		grid.initGrid();
            },
            failure: function () {
                XD.msg("操作中断");
            }
        });
    },
    
    //数据关联
    associationHandler:function (btn) {
    	eventBtn = btn;
    	var grid = this.getGrid(btn);
    	var managementform = this.findFormToView(btn);
        var form = managementform.down('dynamicform');
        var record = grid.selModel.getSelection();
        var selectCount = record.length;
        var selectAll = grid.down('[itemId=selectAll]').checked;
        if(selectAll){
            XD.msg('不支持选择所有页修改');
            return;
        }
        var tree = this.findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if(!node){
            XD.msg('请选择节点');
            return;
        }
        if (selectCount == 0) {
			XD.msg('请至少选择一条需要进行数据关联的数据');
			return;
		}
		var entryid = "";
		for (var i = 0; i < record.length; i++) {
			if (i < record.length - 1) {
				entryid += record[i].id + ",";
			} else {
				entryid += record[i].id;
			}
		}
        Ext.Ajax.request({
        	method: 'POST',
            url: '/dataEvent/eventExist',
            params: {
            	entryid: entryid
            },
            success: function (resp) {
            	var respText = Ext.decode(resp.responseText);
                if (respText.success) {
                	XD.confirm('当前选择了'+selectCount+'条数据，本节点单位数据为'+grid.store.totalCount+'条，本节点单位将被处理', function () {
						var associationView = new Ext.create('Management.view.ManagementAssociationView');
						associationView.record = record;
						associationView.show();
			        });
                } else {
                	XD.msg(respText.msg);
                }
            },
            failure: function () {
                XD.msg("操作中断");
            }
        });
    },
    
    //查看数据信息关联
    lookAssociationHandler: function(btn) {
    	eventBtn = btn;
    	//查找到事件关联表格视图
    	var dataEventDetailGridView = this.findView(btn).down('dataEventDetailGridView');
    	var record = formAndGridView.down('managementgrid').getSelectionModel().getSelected();
    	if (record.length > 1) {
    		XD.msg('只能选择一条数据进行事件关联查看');
    		return;
    	}
    	if (record.length < 1) {
    		XD.msg('请选择一条数据进行事件关联查看');
    		return;
    	}
    	dataEventDetailGridView.nodeid = formAndGridView.down('managementgrid').nodeid;
    	dataEventDetailGridView.initGrid({entryid: record.items[0].id});
        Ext.Ajax.request({
            method:'POST',
            url:'/dataEvent/lookEventEntry',
            params:{
                entryid: record.items[0].id,
                page:1,
                limit:50
            },
            success:function (resp) {
                if(resp.responseText == ""){
                    XD.msg('无关联事件,请先关联相关事件！');
                    return;
                }else {
                    formAndGridView.setActiveItem(dataEventDetailGridView);
                }
            },
            failure:function (resp) {
                XD.msg('操作失败！');
            }
        });
    },
    
    //数据关联 - 确定
    associationSure: function(btn) {
    	//获取到数据关联页面所选的单选值
    	var asView = btn.up('managementAssociationView');
    	var newAs = asView.down('[itemId=newAssociationId]').value;
    	
    	var entryid = "";
    	for (var i = 0; i < asView.record.length; i++) {
    		if (i < asView.record.length - 1) {
    			entryid += asView.record[i].id + ",";
    		} else {
    			entryid += asView.record[i].id;
    		}
    	}
    	//创建新关联
    	var params = {};
    	if (newAs) {
    		var eventname = asView.down('[itemId=refiditemid]').value;
    		if (typeof(eventname) == 'undefined' || eventname == '') {
    			XD.msg('事件描述不能为空！');
            	return;
    		}
    		var eventnumber = asView.down('[itemId=transunitid]').value;
    		if (typeof(eventnumber) == 'undefined' || eventnumber == '') {
    			XD.msg('事件编号不能为空！');
            	return;
    		}
    		params = {
            	eventname: eventname,
            	eventnumber: eventnumber,
            	entryid: entryid,
            	type: '添加'
            }
    	} else {//关联已有事件
    		// 获取用户所选择的关联事件
    		if (typeof(eventid) == 'undefined' || eventid == '') {
    			XD.msg('请选择关联事件！');
            	return;
    		}
    		params = {
            	eventid: eventid,
            	entryid: entryid
            }
    	}
    	this.addDataEventSubmit(btn, params);
    },
    
    // 数据关联 - 关闭
    associationClose: function(btn) {
    	// 关闭创建数据关联窗口
        btn.up('window').hide();
    },
    
    addDataEventSubmit: function(btn, params, type) {
    	var url = "";
    	if (typeof(params.type) != 'undefined' && params.type != '') {
    		url = '/dataEvent/addDataEvent';
    	} else {
    		url = '/dataEvent/leadInEntry';
    	}
    	Ext.Msg.wait('正在进行数据关联，请耐心等待……', '正在操作');
        Ext.Ajax.request({
        	method: 'POST',
            url: url,
            params: params,
            success: function (resp) {
            	Ext.MessageBox.hide();
                var respText = Ext.decode(resp.responseText);
                XD.msg(respText.msg);
                // 关闭创建数据关联窗口
                btn.up('window').hide();
               	formAndGridView.down('[itemId=onlygrid]').initGrid();
            },
            failure: function (resp) {
            	Ext.MessageBox.hide();
                var respText = Ext.decode(resp.responseText);
                XD.msg(respText.msg);
                btn.up('window').hide();
            }
        });
    },
    
    //已关联事件 - 选择事件视图显示
    selectEventHandler: function(btn) {
    	// 创建
    	var dataEventGridView = Ext.create('Management.view.DataEventGridView');
    	dataEventGridView.initGrid();
    	addEventView = btn.up('window');
    	btn.up('window').hide();
    	formAndGridView.setActiveItem(dataEventGridView);
    },
    
    //选择事件
    selectEvent: function(btn) {
    	// 保存选择的关联事件信息
    	var eventInfo = btn.up('dataEventGridView').getSelectionModel().getSelected().items;
    	if (eventInfo.length > 1) {
    		XD.msg('条目数据只能关联一条事件！');
    		return;
    	}
    	eventid = eventInfo[0].id;
    	// 把视图给替换回来
    	this.activeGrid(eventBtn, false);
    	// 显示数据关联添加窗口
    	addEventView.show();
    	
    },
    
    //查看事件条目 - 查看条目具体信息
    seeBtnIDHandler: function(btn) {
    	var dataEventDetailGridView = btn.up('dataEventDetailGridView');
    	
    	// 获取到当前表单中的已选择数据
		var record = dataEventDetailGridView.getSelectionModel().selected;
		if (record.length < 1) {
			XD.msg('请选择一条需要查看的数据');
			return;
		}
		if (record.length > 1) {
			XD.msg('只能选择一条需要查看的数据');
			return;
		}

		// 显示条目的表单信息
		var formView = Ext.create('Management.view.FormView');
		var dynamicform = formView.down('managementform').down('dynamicform');
		this.initFormField(dynamicform, 'look', dataEventDetailGridView.nodeid);
		
		var entryids = [];
        for(var i = 0; i < record.length; i++){
            entryids.push(record.items[i].get('entryid'));
        }
		dynamicform.operate = 'look';
        dynamicform.entryids = entryids;
        dynamicform.entryid = entryids[0];
		
		this.initFormData('look', dynamicform, dynamicform.entryid);
        this.loadFormRecord('look', dynamicform, dynamicform.entryid);//最后加载表单条目数据
		// 保存按钮
		var savebtn = formView.down('managementform').down('[itemId=save]');
		savebtn.hide();
		// 连续录入按钮
        var continuesavebtn = formView.down('managementform').down('[itemId=continuesave]');
        continuesavebtn.hide();
        
        formView.down('managementform').down('electronic').entryid = record.items[0].id;
        
        formAndGridView.type = '数据关联';
		formAndGridView.setActiveItem(formView);
    },

    //从查看事件条目返回
    backHandler: function(btn) {
    	// 把视图给替换回来
    	this.activeGrid(eventBtn, false);
    },
    
    //从事件管理返回
    backEvent: function(btn) {
    	// 把视图给替换回来
    	this.activeGrid(eventBtn, false);
    	// 显示数据关联添加窗口
    	addEventView.show();
    },
    
    //打开报表显示列表
    chooseReport:function(btn){
        var grid = this.getGrid(btn);
        var ids = [];
        var selectAll = grid.down('[itemId=selectAll]').checked;
        if (selectAll) {
            var record = grid.acrossDeSelections;
            var tmp = [];
            if(record.length>0){
                for (var i = 0; i < record.length; i++) {
                    tmp.push(record[i].get('entryid'));
                }
            }
            var entryids = tmp.join(',');
            var tempParams = grid.getStore().proxy.extraParams;
            tempParams['entryids'] = entryids;
            Ext.Ajax.request({
                method: 'post',
                url: '/management/getSelection',
                async: false,
                params: tempParams,
                success: function (response) {
                    var responseText = Ext.decode(response.responseText);
                    if (responseText.success == true) {
                        ids = responseText.data.split(',');
                    } else {
                        XD.msg('操作失败');
                    }
                }
            });
        } else {
            Ext.each(grid.getSelectionModel().getSelection(), function () {
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
                xtype: 'managementReportGridView',
                entryids:ids,
                nodeid:grid.nodeid
            }]
        });
        var reportGrid = reportGridWin.down('managementReportGridView');
        reportGrid.initGrid({nodeid:reportGrid.nodeid});
        reportGridWin.show();
    },

    //高级检索
    advancedSearch:function (btn) {
       var grid = this.getGrid(btn);
        if(window.isMedia == true)
        {
            grid = grid.down("dataview");
        }
       var gridview = btn.findParentByType('panel');
        if(grid.down('[itemId=advancedsearch]').getText()=='取消高级检索'){
            grid.down('[itemId=advancedsearch]').setText('高级检索');
            //取消高级检索，需要把标红清空
            Ext.Array.each(grid.getColumns(), function(){
                var column = this;
                if(column.renderer != false && column.renderer.isSearchRender){
                    column.renderer = false;
                }
            });
            grid.notResetInitGrid({nodeid:grid.nodeid});
        }else{
	        var tree = this.findGridView(btn).down('treepanel');
	        var node = tree.selModel.getSelected().items[0];
	        if(!node){
	            XD.msg('请选择节点');
	            return;
	        }
	        var advancedSearchFormWin = Ext.create('Ext.window.Window',{
	            width:'100%',
	            height:'100%',
	            title:'高级检索',
	            draggable : false,//禁止拖动
	            resizable : false,//禁止缩放
	            modal:true,
	            closeToolText:'关闭',
	            layout:'fit',
	            items:[{
	                xtype: 'advancedSearchFormView',
	                grid:grid,
	                nodeid:node.get('fnid')
	            }]
	        });
	        var advancedSearchDynamicForm = advancedSearchFormWin.down('advancedSearchDynamicForm');
            if(window.isMedia != true){
                grid.down('[itemId=value]').setValue('');//高级检索时，清空简单检索的条件
                Ext.Array.each(grid.getColumns(), function () {//把标红清空
                    var column = this;
                    if (column.renderer != false && column.renderer.isSearchRender) {
                        column.renderer = false;
                    }
                });
            }
            else {
                grid.up('mediaItemsDataView').down('[itemId=value]').setValue('');
            }

	        this.initAdvancedSearchFormField(advancedSearchDynamicForm,grid.nodeid);
	        advancedSearchFormWin.show();
        }
    },

    doAdvancedSearch:function (btn) {//高级检索页面查询方法
        /*查询参数处理*/
        var form = btn.up('window').down('advancedSearchFormView');
        var filedateStartField = form.getForm().findField('filedatestart');
        var filedateEndField = form.getForm().findField('filedateend');
        if(filedateStartField!=null && filedateEndField!=null){
            var filedateStartValue = filedateStartField.getValue();
            var filedateEndValue = filedateEndField.getValue();
            if(filedateStartValue>filedateEndValue){
                XD.msg('开始日期必须小于或等于结束日期');
                return;
            }
        }
        var formValues = form.getValues();//获取表单中的所有值(类型：js对象)
        var formParams = {};
        var fieldColumn = [];
        var fieldValue = [];
        for(var name in formValues){//遍历表单中的所有值
            formParams[name] = formValues[name];
            if(typeof(formValues[name]) != "undefined" && formValues[name] != '' && formValues[name] != 'and' &&
                formValues[name] != 'like' && formValues[name] != 'equal' && formValues[name] != 'or'){
                fieldColumn.push(name);
                fieldValue.push(formValues[name]);
            }
        }
        var grid = form.grid;
        //检索数据前,修改column的renderer，将检索的内容进行标红
        if(window.isMedia != true)
            Ext.Array.each(grid.getColumns(), function (item) {
                var columnValue = formParams[item.dataIndex];
                if ($.inArray(item.dataIndex, fieldColumn) != -1) {
                    var searchstrs = [];
                    searchstrs.push(columnValue);
                    item.renderer = function (v) {
                        if (typeof(v) != "undefined") {
                            var reTag = /<(?:.|\s)*?>/g;
                            var value = v.replace(reTag, "");
                            var reg = new RegExp(searchstrs.join('|'), 'g');
                            return value.replace(reg, function (match) {
                                return '<span style="color:red">' + match + '</span>'
                            });
                        }
                    }
                    item.renderer.isSearchRender = true;
                }
            });
        formParams.nodeid = grid.nodeid;
        //点击非叶子节点时，是否查询出其包含的所有叶子节点数据
        formParams.ifSearchLeafNode = false;
        //点击非叶子节点时，是否查询出当前非叶子节点及其包含的所有非叶子节点数据
        formParams.ifContainSelfNode = false;
        /*切换至列表界面(关闭表单界面)*/
        btn.up('window').close();
        /*加载页面*/
        //检索数据前,修改column的renderer，将检索的内容进行标红
        grid.dataParams=formParams;
        grid.down('[itemId=advancedsearch]').setText('取消高级检索');
        if(fieldColumn.length==0){
            grid.notResetInitGrid(formParams)
        }else{
            var store = grid.getStore();
            Ext.apply(store.getProxy(),{
                extraParams:formParams
            });
            store.loadPage(1);
        }
        Ext.Ajax.request({
            method: 'post',
            url:'/classifySearch/setLastSearchInfo',
            timeout:XD.timeout,
            scope: this,
            async: true,
            params: {
            	nodeid: grid.nodeid,
            	fieldColumn: fieldColumn,
            	fieldValue: fieldValue,
            	type: '高级检索'
            },
            success:function(res){
            },
            failure:function(){
                Ext.MessageBox.hide();
                XD.msg('操作失败！');
            }
        });
    },

    doAdvancedSearchClear:function(btn){//清除检索条件页面所有控件的输入值
    	Ext.Ajax.request({
	        url: '/classifySearch/clearSearchInfo',
	        async:true,
	        params:{
	            nodeid:NodeIdf,
	            type:'高级检索'
	        },
	        success: function (response) {
	            var formView = btn.up('window').down('advancedSearchFormView').down('advancedSearchDynamicForm');
                formView._reset(formView);//表单重置
	        },
	        failure:function(){
                XD.msg('操作失败！');
            }
	    });
    },

    doAdvancedSearchClose:function (btn) {
        btn.up('window').close();
    },
    
    //获取表单字段
    getFormField:function (nodeid) {
        var formField;
        Ext.Ajax.request({
            url: '/template/form',
            async:false,
            params:{
                nodeid:nodeid
            },
            success: function (response) {
                formField = Ext.decode(response.responseText);
                console.log(formField);
            }
        });
        return formField;
    },

    //批量修改
    doBatchModify:function (btn) {
        var resultGrid = this.getGrid(btn);
        var records;
        var selectCount;
        var selectAll;
        var params;
        if(window.isMedia != true) {
            records = resultGrid.getSelectionModel().getSelection();
            selectCount = resultGrid.getSelectionModel().getSelectionLength();
            selectAll = resultGrid.down('[itemId=selectAll]').checked;
            params = resultGrid.getStore().proxy.extraParams;
        } else {
            records = resultGrid.acrossSelections;
            selectCount = records.length;
            params = resultGrid.down('dataview').getStore().proxy.extraParams;
        }
        if (selectCount == 0) {
            XD.msg('请选择数据');
            return;
        }
        if (selectCount > 5000) {
            XD.msg('最多只能选择5000条数据操作');
            return;
        }

        var tree = this.findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];

        if (!node) {
            XD.msg('请选择节点');
            return;
        }

        var isSelectAll = false;
        if (selectAll) {
            records = resultGrid.getSelectionModel().getSelection();
            isSelectAll = true;
        }
        if(window.isMedia == true) {
            resultGrid = resultGrid.up('[itemId=gridview]').down('managementgrid');
            resultGrid.initGrid({nodeid:node.get('fnid')})
        }
        var tmp = [];
        for (var i = 0; i < records.length; i++) {
            tmp.push(records[i].get('entryid'));
        }
        var entryids = tmp.join(',');
        var fromOutside = false;
        if (typeof(params.content) !== 'undefined') {
            fromOutside = true;
        }
        var batchModifyModifyWin = Ext.create('Ext.window.Window', {
            width: '100%',
            height: '100%',
            title: '批量修改',
            // draggable : true,//可拖动
            // resizable : false,//禁止缩放
            modal: true,
            closeToolText: '关闭',
            layout: 'fit',
            items: [{
                xtype: 'batchModifyModifyFormView',
                entryids: entryids,
                resultgrid: resultGrid,
                isSelectAll: isSelectAll,
                fromOutside: fromOutside
            }]
        });
        var fieldModifyPreviewGrid = batchModifyModifyWin.down('grid');
        if (fieldModifyPreviewGrid.getStore().data.length > 0) {
            fieldModifyPreviewGrid.getStore().removeAll();
        }
        batchModifyModifyWin.show();
//        window.batchModifyModifyWins = batchModifyModifyWin;
//        Ext.on('resize',function(a,b){
//            window.batchModifyModifyWins.setPosition(0, 0);
//            window.batchModifyModifyWins.fitContainer();
//        });
    },

    loadModifyTemplatefieldCombo: function (view) {//加载批量修改form的下拉框
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

    loadReplaceTemplatefieldCombo: function (view) {//加载批量替换form的下拉框
        var combostore = view.getStore();
        var batchModifyReplaceFormView = view.up('batchModifyReplaceFormView');
        if(batchModifyReplaceFormView.filingtype){ //归档批量操作标识
            combostore.proxy.extraParams.datanodeidAndFieldcodes = batchModifyReplaceFormView.filingnodeid;
        }else{
            combostore.proxy.extraParams.datanodeidAndFieldcodes = batchModifyReplaceFormView.resultgrid.nodeid;
        }
        combostore.load();
    },
    loadAddTemplatefieldCombo: function (view) {//加载批量增加form的下拉框
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
        if(!allowEmpty){
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
        fieldModifyPreviewGridStore.load({
        	callback:function () {
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
        var params = resultGrid.getStore().proxy.extraParams;
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
            // resizable : false,//禁止缩放
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
        if(operateCount == 0){
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
            updateConfirmMsg = '本次操作将在['+resultPreviewGrid.formview.resultgrid.nodefullname+']所选记录的['+fieldname+']的第'+placeindex+'位增加“'+addcontent+'”字符串，记录数：共'+operateCount+'条, 是否继续?';
        }
        if(inserttype=='front'){
            updateConfirmMsg = '本次操作将在['+resultPreviewGrid.formview.resultgrid.nodefullname+']所选记录的['+fieldname+']前面增加“'+addcontent+'”字符串，记录数：共'+operateCount+'条, 是否继续?';
        }
        if(inserttype=='behind'){
            updateConfirmMsg = '本次操作将在['+resultPreviewGrid.formview.resultgrid.nodefullname+']所选记录的['+fieldname+']后面增加“'+addcontent+'”字符串，记录数：共'+operateCount+'条, 是否继续?';
        }

        XD.confirm(updateConfirmMsg,function (){
            updateData(btn,'add');
        },this);
    },
    //页数矫正处理方法
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
                url: '/management/pgNumCorrect',
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
                    Ext.Msg.wait('操作失败!','正在操作').hide();
                    grid.down('[itemId=selectAll]').setValue(false);
                    grid.getStore().reload();
                },
                scope: this
            });
        });
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
            url: '/management/newFileNodeidAndEntryid',
            method:'GET',
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
            },failure: function (resp) {
                Ext.Msg.hide();
            }
        });

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
                url: '/management/statisticUpdate',
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
    //拆卷
    dismantleHandler:function (btn) {
        var btnText = btn.getText();
        var grid = this.getGrid(btn);
        var selectAll
        var record
        if(window.isMedia != true){
            record = grid.selModel.getSelection();
            selectAll = grid.down('[itemId=selectAll]').checked;
        }
        else {
            record = grid.acrossSelections;
        }


        var selectCount = record.length;
        if (selectAll) {
            selectCount = grid.selModel.selected.length;//当前页选中
        }
        var tree = this.findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if (!node) {
            XD.msg('请选择节点');
            return;
        }
        if (selectCount == 0) {
            if (selectAll) {
                XD.msg('当前页没有选择数据');
            } else {
                XD.msg('请选择一条数据');
            }
            return;
        }
        if (selectCount != 1) {
            if (btnText == '拆卷') {
                XD.msg('拆卷只能选中一条数据。');
            } else {
                XD.msg('拆件只能选中一条数据。');
            }
            return;
        }
        var entryid;
        if (selectAll) {
            entryid = grid.selModel.selected.items[0].get("entryid");
        } else {
            entryid = record[0].get("entryid");
        }

        var dismantleWin = Ext.create('Ext.window.Window', {
            width: '40%',
            height: 300,
            title: btnText,
            draggable: true,//允许拖动
            resizable: false,//禁止缩放
            modal: true,
            closeToolText: '关闭',
            layout: 'fit',
            items: [{
                xtype: 'managementDismantleFormView',
                grid: grid,
                entryid: entryid
            }]
        });
        if (btnText == '拆卷') {
            dismantleWin.down('[itemId=dismantleType]').setFieldLabel('拆卷方式');
            dismantleWin.down('[itemId=dismantleNode]').setFieldLabel('拆卷到');
        } else {
            dismantleWin.down('[itemId=syncInnerFile]').hide();
        }
        dismantleWin.show();
    },

    //卷内文件拆卷
    innerfileDismantleHandler:function (btn) {
        var grid = this.getInnerGrid(btn);
        var record = grid.selModel.getSelection();
        var selectCount = record.length;
        var tree = this.findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if(!node){
            XD.msg('请选择节点');
            return;
        }
        if(selectCount != 1){
            XD.msg('拆件只能选中一条数据');
            return;
        }
        var entryid;
        entryid = record[0].get("entryid");
        var dismantleWin = Ext.create('Ext.window.Window',{
            width:'40%',
            height:'40%',
            title:'卷内拆件',
            draggable : true,//允许拖动
            resizable : false,//禁止缩放
            modal:true,
            closeToolText:'关闭',
            layout:'fit',
            items:[{
                xtype: 'managementDismantleFormView',
                grid:grid,
                entryid:entryid

            }]
        });
        dismantleWin.down('[itemId=syncInnerFile]').hide();
        dismantleWin.show();
    },

    //拆件（卷）　保存
    dismantleSubmitForm:function (btn) {
        var dismantleForm = btn.up('form');
        var dismantleWin = btn.up('window');
        var title=dismantleWin.title;//获取当前是拆卷还是拆件
        if (title=='拆卷'){
            title='1';
        }else if(title=='卷内拆件'){
            title='2';
        }else if(title=='拆件'){
            title='3';
        }
        var dismantleType = dismantleForm.getForm().getValues()['dismantleType'];
        var syncType = dismantleForm.getForm().getValues()['syncInnerFile'];//判断是否同步卷内文件
        var managementTreeComboboxView = dismantleForm.down('managementTreeComboboxView');
        if(dismantleType==''){
            XD.msg('请选择拆件方式');
            return;
        }
        if(dismantleType=='node' && managementTreeComboboxView.rawValue==''){
            XD.msg('请选择拆件目标节点');
            return;
        }
        if (dismantleType=='node' && dismantleForm.grid.nodefullname==managementTreeComboboxView.rawValue) {
            XD.msg('不允许选择当前数据节点');
            return;
        }
        //拆件操作必须先处理后续数据，再拆除所选件？
        /*var state = this.updateSubsequentData(dismantleForm.entryid,'dismantle');
        if(!state){
            return;
        }*/
        Ext.Ajax.request({
            url: '/management/dismantle',
            async:false,
            params:{
                entryid:dismantleForm.entryid,
                dismantleType:dismantleType,
                nodeid:dismantleForm.nodeid,
                title:title,//
                syncType:syncType
            },
            success: function (resp) {
                var data = Ext.decode(resp.responseText);
                if(data.success){
                    dismantleForm.grid.notResetInitGrid();
                    //判断是否是上下层grid，如果是，则清空下层列表数据
                    if(dismantleForm.grid.itemId == 'northgrid'){
                        dismantleForm.grid.up('managementFormAndGrid').down('[itemId=southgrid]').getStore().removeAll();
                    }
                }
                XD.msg(data.msg);
                dismantleWin.close();

            },
            failure:function () {
                XD.msg('操作失败');
            }
        });
    },

    //插件
    insertionHandler:function (btn) {
        var btnText = btn.getText();
        var managementform = this.findFormToView(btn);
        managementform.down('electronic').operateFlag = 'insertion';
        managementform.saveBtn = managementform.down('[itemId=save]');
        managementform.continueSaveBtn = managementform.down('[itemId=continuesave]');
        managementform.operateFlag = 'insertion';
        var grid = this.getGrid(btn);
        var form = managementform.down('dynamicform');
        var record
        var selectAll
        if(window.isMedia != true){
            record = grid.selModel.getSelection();
            selectAll = grid.down('[itemId=selectAll]').checked;
        }
        else {
            record = grid.acrossSelections;
        }
        var selectCount = record.length;
        if (selectAll) {
            selectCount = grid.selModel.selected.length;//当前页选中
        }
        var tree = this.findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if (!node) {
            XD.msg('请选择节点');
            return;
        }
        if (selectCount == 0) {
            if (selectAll) {
                XD.msg('当前页没有选择数据');
            } else {
                XD.msg('请选择一条数据');
            }
            return;
        }
        if (selectCount != 1) {
            if (btnText == '插卷') {
                XD.msg('插卷只能选中一条数据');
            } else {
                XD.msg('插件只能选中一条数据');
            }
            return;
        }
        var entryid;
        if (selectAll) {
            entryid = grid.selModel.selected.items[0].get("entryid");
        } else {
            entryid = record[0].get("entryid");
        }
        var initFormFieldState = this.initFormField(form, 'show', node.get('fnid'));
        if (!initFormFieldState) {//表单控件加载失败
            return;
        }
        // form.operateFlag = 'insertion';//区别著录add和修改modify
        this.initFormData('insertion', form, entryid);
        this.activeToForm(form);
        this.loadFormRecord('insertion', form, entryid);//最后加载表单条目数据
        form.operateType = 'insertion';//点击保存按钮时判断是否更新后续统计项及档号值
        form.submitType = 'chji';//插件
        if (btnText == '插卷') {
            form.submitType = 'chju';//插卷
            XD.confirm('是否同步卷内文件', function () {
                form.submitType = 'syncJn';//插卷同步卷内
            });
        }
    },

    //卷内文件插卷
    innerfileInsertionHandler:function (btn) {
        var grid = this.getInnerGrid(btn);
		var form = this.findDfView(btn);
        var record = grid.selModel.getSelection();
        var selectCount = record.length;
        var tree = this.findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if(!node){
            XD.msg('请选择节点');
            return;
        }
        if(selectCount != 1){
            XD.msg('插件只能选中一条数据');
            return;
        }
        var entryid;
        entryid = record[0].get("entryid");
        var initFormFieldState = this.initFormField(form, 'hide', grid.dataParams.nodeid);
        if(!initFormFieldState){//表单控件加载失败
            return;
        }
        form.operateFlag = 'insertion';//区别著录add和修改modify
        this.initFormData('insertion',form, entryid);
        this.activeToForm(form);
        this.loadFormRecord('insertion',form, entryid);//最后加载表单条目数据
        form.operateType = 'insertion';//点击保存按钮时判断是否更新后续统计项及档号值
        form.submitType='jnchj';//卷内插件
    },

    //归档
    filingHandler: function (btn) {
        var grid = this.getGrid(btn);
        var selectAll
        var record
        if(window.isMedia != true) {
            selectAll = grid.down('[itemId=selectAll]').checked;
            record = grid.selModel.getSelection();
            if(grid.getStore().getCount() < 1){
                XD.msg('没有可以归档的数据，请先增加数据。');
                return;
            }
        }
        else
        {
            record = grid.acrossSelections;
            if(grid.down('dataview').getStore().getCount() < 1){
                XD.msg('没有可以归档的数据，请先增加数据。');
                return;
            }
        }
        var tree = this.findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        window.wgOrderNodeid= node.get('fnid');//标记数据采集列表节点（未归节点）id
        if (!node) {
            XD.msg('请选择节点');
            return;
        }

        if(grid.getStore().getCount() < 1){
            XD.msg('没有可以归档的数据，请先增加数据。');
            return;
        }else{
            //获取临时表的数据
            var temSum = 0;
            Ext.Ajax.request({
                method: 'POST',
                url: '/management/getTempSize',
                async: false,
                scope: this,
                success: function (response) {
                    temSum = Ext.decode(response.responseText).msg;
                },
                failure: function () {
                    XD.msg('操作失败');
                }

            });
            var chooseSum
            var tempParams
            if (window.isMedia != true) {
                chooseSum = grid.selModel.getSelectionLength();
                tempParams = grid.getStore().proxy.extraParams
            } else {
                chooseSum = grid.acrossSelections;
                tempParams = grid.down('dataview').getStore().proxy.extraParams
            }

            if (chooseSum + parseInt(temSum) > 5000) {
                XD.msg('归档数据不能超过5000条,预归档页面有' + temSum + '条数据');
                return;
            }
            var tempParams = grid.getStore().proxy.extraParams;
            var tmp = [];
            var allTmp = [];
            var isSelectAll = false;
            if (selectAll) {
                record = grid.acrossDeSelections;
                isSelectAll = true;
                Ext.Ajax.request({
                    async: false,
                    url: '/management/getSelectAllEntryid',
                    params: tempParams,
                    success: function (response) {
                        var records = Ext.decode(response.responseText);
                        if (grid.acrossDeSelections.length > 0) {
                            //获取取消选择的条目
                            var cancles = [];
                            for (var i = 0; i < grid.acrossDeSelections.length; i++) {
                                cancles.push(grid.acrossDeSelections[i].get('entryid'))
                            }
                            if (cancles.length > 0) {
                                var strCancles = cancles.join(',');
                                //遍历总条目，获取取消选择的条目中不包含遍历的条目
                                for (var i = 0; i < records.length; i++) {
                                    if (strCancles.indexOf(records[i]) == -1) {
                                        allTmp.push(records[i]);
                                    }
                                }
                            } else {
                                allTmp = records;
                            }
                        } else {
                            allTmp = records;
                        }
                    }
                });
            }
            for (var i = 0; i < record.length; i++) {
                tmp.push(record[i].get('entryid'));
            }
            var entryids = tmp.join(',');
            var allEntryids;
            if (selectAll) {
                allEntryids = allTmp.join(',');
                if (allTmp.length + parseInt(temSum) > 5000) {
                    XD.msg('归档数据不能超过5000条,预归档页面有' + temSum + '条数据');
                    return;
                }
            } else {
                allEntryids = tmp.join(',');
            }
            var filingWin = Ext.create('Ext.window.Window', {
                modal: true,
                width: '100%',
                height: '100%',
                //title:'归档',
                header: false,
                layout: 'fit',
                closeToolText: '关闭',
                items: [{
                    xtype: 'managementfiling',
                    tempParams: tempParams,
                    isSelectAll: isSelectAll,
                    entryids: entryids,
                    allEntryids: allEntryids,
                    managementgrid: grid,
                    managementgridNodeid: node.get('fnid')
                }]
            });
            if (showElectronicRename == 'false') {
                filingWin.down('[itemId=radioGroupIds]').hide();//隐藏“电子文件重名命名设置”
            }
            //隐藏归档和上一步按钮
            this.initWgGrid(filingWin);//未归标签页赋值

            //显示现有排序值
            var ordertxtLab = filingWin.down('[itemId=ordertxtId]');
            var ordertxtLabTwo = filingWin.down('[itemId=orderTxtLabelId]');
            var ordertxtLabThr = filingWin.down('[itemId=orderTxtLabelYgdId]');
            var returnStr = getOrderTxt(node.get('fnid'));
            var ordertxt = returnStr[0];
            ordertxtLab.setTitle(ordertxt);//预归档页面
            ordertxtLabTwo.setText(ordertxt);//未归页面
            ordertxtLabThr.setText(ordertxt + " 。");//预归档页面
            window.ordertxtLab = ordertxtLab;
            window.ordertxtLabThr = ordertxtLabThr;

            //排序设置界面初始化
            this.initOrderset(filingWin);
            var ordersettingSaveTwoBtn = filingWin.down('[itemId=ordersettingSaveTwoBtnId]');
            ordersettingSaveTwoBtn.setVisible(false);//隐藏页面本身的保存按钮

            //初始化预归档表格
            var filingpreviousStep = filingWin.down('[itemId=filingpreviousStepBtn]');
            this.activeFilingFormAndGrid(filingpreviousStep, 'first');
            var filingview = filingWin.down('managementfiling');
            filingview.tabBar.items.items[0].show();
            filingview.tabBar.items.items[1].hide();
            filingview.tabBar.items.items[2].hide();
            filingWin.show();
        }
    },

    initOrderset:function(view){
        var detailformview = view.down('[itemId=ordersettingDetailFormViewItemID]');
        detailformview.down('[itemId=areaid]').reset();//清空值
        detailformview.down('[itemId=hiddenfieldId]').reset();
        var itemselectorView = view.down('[itemId=itemselectorID]');
        itemselectorView.store.proxy.extraParams = {datanodeid: window.wgOrderNodeid};
        itemselectorView.getStore().load(function (storedata) {
            if(storedata.length===0){
                XD.msg('请先去模板维护设置模板信息');
            }
            var records = [];
            for (var i = 0; i < storedata.length; i++) {
                var temp = storedata[i].data.fieldcode.split('∪');
                if (temp[0] != "") {
                    records.push(storedata[i]);
                }
            }
            itemselectorView.toField.store.removeAll();
            itemselectorView.setValue(records);
            itemselectorView.toField.boundList.select(0);//默认选中第一个
        });
    },

    //提交表单内容（“下一步”按钮的单击事件）
    filingSubmitForm: function (btn) {
        var filingView = this.findFilingView(btn);
        var filingFirstForm = this.findFilingFirstFormView(btn);
        var treeComboboxView = filingFirstForm.down('managementTreeComboboxView');
        var ordertxtLab = filingFirstForm.down('[itemId=ordertxtId]');
        var ordertxt= ordertxtLab.title;
        if (!treeComboboxView.rawValue) {
            XD.msg('请选择需要归档的档案分类！');
            return;
        }
        if(ordertxt.indexOf('序')==-1){
            XD.msg('请选择未归记录加入预归档的先后排序！');
            return false;
        }
        var dynamicFilingForm = this.findFilingFormView(btn);
        var boolean=filingView.down('[itemId = radioGroupIds]').getValue();
        var labelTxt=filingView.down('[itemId = labelTxtGdIds]');
        if (!dynamicFilingForm.initedstate) {
            XD.msg('模板或档号设置异常，请在“系统设置”-“模板维护”中设置该节点的模板及档号');
            return;
        }
        if(showElectronicRename == 'false'){
            labelTxt.hide();//隐藏电子文件重名命名提示信息
            filingView.down('[itemId=ylId]').query('tbseparator')[6].hide()
        }

        filingView.tabBar.items.items[0].hide();
        filingView.tabBar.items.items[1].show();
        filingView.tabBar.items.items[2].show();
        window.wgChange=true;//标记未归页面有设置变动
        var allEntryids=filingView.allEntryids;
        if((allEntryids!=undefined&&allEntryids.length>0&&(window.ygdNodeid==undefined||window.ygdNodeid==''))||(window.ygdNodeid!=undefined&&window.ygdNodeid!=''&&window.ygdNodeid!=dynamicFilingForm.nodeid)){//首次有数据加载或者切换归档节点，先插入临时表，再切换到预归档页面
            var addType=1;//首次加载
            if(window.ygdNodeid!=undefined&&window.ygdNodeid!=dynamicFilingForm.nodeid){//切换归档节点
                addType=2;
            }
            //加入预归档
            var params={
                entryids:allEntryids,       //选定的记录的条目ID
                selectAll:'0',
                targetNodeid:dynamicFilingForm.nodeid,
                addType:addType,//标记首次加载或者切换归档节点
                nodeid:filingGrid.dataParams.nodeid  //未归节点
            };
            Ext.MessageBox.wait('正在加入预归档请稍后...', '提示');
            Ext.Ajax.request({
                method:'POST',
                url: '/management/entryIndexYgd',
                params:params,
                scope:this,
                timeout:XD.timeout,
                success: function (response) {
                    Ext.MessageBox.hide();
                    XD.msg(Ext.decode(response.responseText).msg);
                    window.ygdNodeid=dynamicFilingForm.nodeid;
                    window.orderType=0;
                    window.moveUpOrDown=false;
                    ManagementIsArchivecode=false;//标记全部生成档号失效
                    filingView.setActiveTab(1);
                },
                failure:function () {
                    Ext.MessageBox.hide();
                    XD.msg('操作失败');
                }
            });
        }else if(window.ygdNodeid==undefined||window.ygdNodeid==''){//首次无数据加载切换
            window.ygdNodeid=dynamicFilingForm.nodeid;
            filingView.setActiveTab(1);
        }else{//直接切换到预归档页面
            if(window.orderType!=0){//没有重新设置排序
                window.wgChange=false;//标记未归页面设置标记清空,切换到预归档不重新加载
            }
            filingView.setActiveTab(1);
        }
        if(boolean.rename=='true'){
            labelTxt.setText('将根据档号重命名电子文件名。');
            var param= {
                param: 'true'
            };
        }else{
            labelTxt.setText('无需根据档号重命名电子文件。');
            var param= {
                param: 'false'
            };
        }
        Ext.Ajax.request({
            method: 'post',
            url: '/management/getParam',
            params: param,
            scope: this,
            timeout: XD.timeout,
            success: function(response){
                //XD.msg('设置成功');
            },
            failure:function () {
                //XD.msg('设置失败');
            }
        });
    },

    //加入预归档
    addGd:function (btn) {
        var filingView = this.findFilingView(btn);
        var dynamicfilingform = filingView.down('dynamicfilingform');
        var filingGrid = filingView.down('[itemId=wgNodeId]');
        var record = filingGrid.getSelectionModel().getSelection();
        var selectAll = filingGrid.down('[itemId=selectAll]').checked;
        //获取临时表的数据
        var temSum=0;
        Ext.Ajax.request({
            method:'POST',
            url: '/management/getTempSize',
            async:false,
            scope:this,
            success: function (response) {
                temSum=Ext.decode(response.responseText).msg;
            },
            failure:function () {
                XD.msg('操作失败');
            }

        });
        var params = {};
        var entryids = [];
        var allTmp=[];
        if (selectAll) {//选择所有页
            var selectCount =filingGrid.getStore().totalCount;
            if (selectCount+parseInt(temSum) > 5000) {
                XD.msg('最多只能选择5000条数据进行归档操作，预归档页面已有'+temSum+'条记录');
                return;
            }else if (selectCount < 1){
                XD.msg('请选择一条或多条记录');
                return;
            }
            Ext.Ajax.request({
                async:false,
                url: '/management/getSelectAllEntryid',
                params:filingGrid.dataParams,
                success:function (response) {
                    var records = Ext.decode(response.responseText);
                    if (filingGrid.acrossDeSelections.length > 0) {
                        //获取取消选择的条目
                        var cancles=[];
                        for(var i = 0; i < filingGrid.acrossDeSelections.length; i++){
                            cancles.push(filingGrid.acrossDeSelections[i].get('entryid'))
                        }
                        if(cancles.length>0){
                            var strCancles =cancles.join(',');
                            //遍历总条目，获取取消选择的条目中不包含遍历的条目
                            for(var i = 0; i < records.length; i++){
                                if(strCancles.indexOf(records[i])==-1){
                                    allTmp.push(records[i]);
                                }
                            }
                        }else{
                            allTmp = records;
                        }
                    }else{
                        allTmp = records;
                    }
                }
            });
            params={
                entryids:allTmp,       //选定的记录的条目ID
                selectAll:'1',
                targetNodeid:dynamicfilingform.nodeid,
                nodeid:filingGrid.dataParams.nodeid  //未归节点
            };
            entryids = allTmp;
        }else{
            if(record.length<1){
                XD.msg('请选择一条或多条记录');
                return;
            }else if (record.length+parseInt(temSum) > 5000) {
                XD.msg('最多只能选择5000条数据进行归档操作，预归档页面已有'+temSum+'条记录');
                return;
            }
            var entryids = [];
            for(var i=0;i<record.length;i++){
                entryids.push(record[i].get('entryid'));
            }
            params={
                entryids:entryids,       //选定的记录的条目ID
                selectAll:'0',
                targetNodeid:dynamicfilingform.nodeid,
                nodeid:filingGrid.dataParams.nodeid  //未归节点
            };
        }

        var ygGrid=filingView.down('[itemId=ylId]');
        var ygStore = ygGrid.getStore();
        var that=this;
        if(ygStore.totalCount<1) {//没有预归档记录,不用确认框
            this.addAjax(params,filingView,filingGrid,entryids,that);
        }else{
            XD.confirm('确定通过该功能追加预归档记录？ <br /><span style="color: red;">   <br />温馨提示：<br />进行该操作后，预归档中所有记录，将重新按排序设置进行排序。<br />如需保留现有预归档排序，可通过【插入预归档】功能来追加预归档记录</span>', function () {
                that.addAjax(params,filingView,filingGrid,entryids,that);
            });
        }
    },

    //增加预归档ajax
    addAjax:function(params,filingView,filingGrid,entryids,that){
        Ext.MessageBox.wait('正在加入预归档请稍后...', '提示');
        Ext.Ajax.request({
            method:'POST',
            url: '/management/entryIndexYgd',
            params:params,
            scope:that,
            timeout:XD.timeout,
            success: function (response) {
                Ext.MessageBox.hide();
                XD.msg(Ext.decode(response.responseText).msg);
                //刷新列表数据
                filingGrid.delReload(filingGrid.selModel.getSelectionLength());
                window.orderType=0;//标记重新排序
                window.wgChange=true;//标记未归页面有设置变动
                ManagementIsArchivecode=false;//标记全部生成档号失效

            },
            failure:function () {
                Ext.MessageBox.hide();
                XD.msg('操作失败');
            }

        });
    },

    //插入入预归档
    insertGd:function (btn) {
        var filingView = this.findFilingView(btn);
        var dynamicfilingform = filingView.down('dynamicfilingform');
        // var ygGrid=filingView.down('[itemId=ylId]');
        // var entryids = [];
        // var ygStore = ygGrid.getStore();
        // if(ygStore.totalCount<1){//有预归档记录
        //     XD.msg('预归档还没有选择好的记录，无法做插入操作');
        //     return;
        // }
        var filingGrid = filingView.down('[itemId=wgNodeId]');
        var record = filingGrid.getSelectionModel().getSelection();
        var temSum=0;//临时表的数据
        if(record.length<1){
            XD.msg('请选择一条或多条记录');
            return;
        }else{
            //获取临时表的数据
            Ext.Ajax.request({
                method:'POST',
                url: '/management/getTempSize',
                async:false,
                scope:this,
                success: function (response) {
                    temSum=Ext.decode(response.responseText).msg;
                },
                failure:function () {
                    XD.msg('操作失败');
                }

            });
            if(parseInt(temSum)<1){
                XD.msg('预归档还没有选择好的记录，无法做插入操作');
                return;
            }

            if (record.length+parseInt(temSum) > 5000) {
                XD.msg('最多只能选择5000条数据进行归档操作，预归档页面已有'+temSum+'条记录');
                return;
            }

        }
        var entryids = [];
        for(var i=0;i<record.length;i++){
            entryids.push(record[i].get('entryid'));
        }
        var inserttype = btn.inputValue;
        if(inserttype=='anywhere'){
            var win = Ext.create('Ext.window.Window',{
                width:'45%',
                // height:'20%',
                title:'插入预归档',
                draggable : true,//可拖动
                resizable : false,//禁止缩放
                modal:true,
                closeToolText:'关闭',
                layout:'fit',
                items:[{
                    xtype: 'InsertFilingView',
                    entryids:entryids,
                    resultgrid:dynamicfilingform,
                    filingGrid:filingGrid,
                    temSum:temSum
                }]
            });
            win.show();
            return;
        }
        var insertplaceindex ;
        if(inserttype=='front'){
            insertplaceindex = 1;
        }else if(inserttype=='behind') {
            insertplaceindex = parseInt(temSum)+1;
        }
        var params={
            entryids:entryids,       //选定的记录的条目ID
            targetNodeid:dynamicfilingform.nodeid,
            insertLine:insertplaceindex
        };
        Ext.MessageBox.wait('正在加入预归档请稍后...', '提示');
        Ext.Ajax.request({
            method:'POST',
            url: '/management/entryIndexInsertYgd',
            params:params,
            scope:this,
            timeout:XD.timeout,
            success: function (response) {
                Ext.MessageBox.hide();
                XD.msg(Ext.decode(response.responseText).msg);
                //刷新列表数据
                filingGrid.delReload(filingGrid.selModel.getSelectionLength());
                window.orderType=1;//标记插入预归档，不用重新排序
                window.wgChange=true;//标记未归页面有设置变动
                ManagementIsArchivecode=false;//标记全部生成档号失效
            },
            failure:function () {
                Ext.MessageBox.hide();
                XD.msg('操作失败');
            }
        });
    },

    //插入入预归档-确定
    checkInsert:function (btn) {
        var formview = btn.up('InsertFilingView');
        var formWin = formview.up('window');
        var inserttype = formview.getValues()['insertPlace'];
        var insertplaceindexField = formview.getForm().findField('insertPlaceIndex');
        var insertplaceindex = insertplaceindexField.getValue();
        if(!inserttype){XD.msg('请检查位置设置信息');return;}
        if(inserttype=='anywhere' && !insertplaceindex){XD.msg('请输入插入字符位置');return;}
        if(isNaN(insertplaceindex)){
            var reg=/^[1-9]+[0-9]*]*$/; //判断正整数
            if(!reg.test(insertplaceindex)){
                XD.msg("请输入数字");
                return;
            }
        }
        if(parseInt(insertplaceindex)<1){XD.msg('插入字符位置输入项最小值为1');return;}
        if(parseInt(insertplaceindex)>8000){XD.msg('插入字符位置输入项最大值为8000');return;}
        if(inserttype=='front'){
            insertplaceindex = 1;
        }else if(inserttype=='behind') {
            insertplaceindex = parseInt(formview.temSum)+1;
        }
        var params={
            entryids:formview.entryids,       //选定的记录的条目ID
            targetNodeid:formview.resultgrid.nodeid,
            insertLine:insertplaceindex
        };
        Ext.MessageBox.wait('正在加入预归档请稍后...', '提示');
        Ext.Ajax.request({
            method:'POST',
            url: '/management/entryIndexInsertYgd',
            params:params,
            scope:this,
            timeout:XD.timeout,
            success: function (response) {
                Ext.MessageBox.hide();
                formWin.close();
                XD.msg(Ext.decode(response.responseText).msg);
                //刷新列表数据
                var filingGrid = formview.filingGrid;
                filingGrid.delReload(filingGrid.selModel.getSelectionLength());
                window.orderType=1;//标记插入预归档，不用重新排序
                window.wgChange=true;//标记未归页面有设置变动
                ManagementIsArchivecode=false;//标记全部生成档号失效
            },
            failure:function () {
                Ext.MessageBox.hide();
                XD.msg('操作失败');
            }
        });
    },

    //取消预归档
    ygdBack:function(btn){
        var filingView = this.findFilingView(btn);
        var dynamicfilingform = filingView.down('dynamicfilingform');
        var filingGrid = this.findFilingGridView(btn);
        var record = filingGrid.getSelectionModel().getSelection();
        if(record.length<1){
            XD.msg('请选择需要取消预归档的记录');
            return;
        }
        var entryids = [];
        for(var i=0;i<record.length;i++){
            entryids.push(record[i].get('entryid'));
        }
        var params={
            entryids:entryids,       //选定的记录的条目ID
            nodeid:dynamicfilingform.nodeid
        };
        Ext.MessageBox.wait('正在取消预归档请稍后...', '提示');
        Ext.Ajax.request({
            method:'POST',
            url: '/management/entryIndexYgdDel',
            params:params,
            scope:this,
            timeout:XD.timeout,
            success: function (response) {
                Ext.MessageBox.hide();
                XD.msg(Ext.decode(response.responseText).msg);
                //刷新列表数据
                filingGrid.getStore().proxy.extraParams.dataSource='del';//重新赋值序号
                filingGrid.getStore().proxy.extraParams.ygType='ygd';
                filingGrid.delReload(filingGrid.selModel.getSelectionLength());
            },
            failure:function () {
                Ext.MessageBox.hide();
                XD.msg('操作失败');
            }

        });
    },

    //预归档修改
    ygdEdit:function(btn){
        var filingView = this.findFilingView(btn);
        var dynamicfilingform = filingView.down('dynamicfilingform');
        var filingGrid = this.findFilingGridView(btn);
        var objectjson = dynamicfilingform.getForm().getValues();//获取上边的档号生成字段表单记录
        var record = filingGrid.getSelectionModel().getSelection();
        if(record.length==0){
            XD.msg('请至少选择一条需要修改的数据');
            return;
        }
        var entryids = [];
        for (var i = 0; i < record.length; i++) {
            entryids.push(record[i].get('entryid'));
        }

        var nodeid=dynamicfilingform.nodeid;
        var entryWin = Ext.create('Ext.window.Window',{
            modal:true,
            width:'100%',
            height:'100%',
            itemId:'entryEditWinId',
            //title:'条目',
            header:false,
            layout:'fit',
            closeToolText:'关闭',
            items:[{
                xtype:'entryEditFromView'
            }]
        });
        var dynamicform = entryWin.down('dynamicform');
        initFormField(dynamicform, 'show', nodeid);
        dynamicform.entryids = entryids;
        dynamicform.entryid = entryids[0];
        dynamicform.objectjson=objectjson;
        dynamicform.nodeid=nodeid;
        this.initFormDataOnly('modify', dynamicform, entryids[0]);//条目赋值
        entryWin.down('entryEditFromView').tabBar.items.items[1].hide();
        entryWin.show();
    },

    //预归档排序设置
    addOrderSet:function(btn){
        var filingView = this.findFilingView(btn);
        var filingGrid = filingView.down('[itemId=wgNodeId]');

        var addOrderSetWin = Ext.create('Ext.window.Window',{
            modal:true,
            width:'80%',
            height:'60%',
            itemId:'addOrderSetWinId',
            title:'预归档排序设置',
            layout:'fit',
            closeToolText:'关闭',
            items:[{
                xtype:'ordersettingSelectedFormView'
            }]
        });
        //排序设置界面初始化
        this.initOrderset(addOrderSetWin);
        addOrderSetWin.down('[itemId=ordersettingSaveBtnId]').hide();
        addOrderSetWin.show();
    },
    //从分类设置返回表格视图
    backToGrid:function (btn) {
    	btn.up('window').close();
    },
    //返回至数据管理列表
    backToManagementgrid: function (btn) {
        var filingView = this.findFilingView(btn);
        var ygGrid=filingView.down('[itemId=ylId]');
        var ygStore = ygGrid.getStore();
        if(ygStore.totalCount>0){//有预归档记录
            XD.confirm('返回将取消本次预归档操作，确定返回？', function () {
                btn.up('window').close();
                window.ygdNodeid='';
                delTempByUniquetagType('glgd');//清除临时表缓存
            });
        }else{
            btn.up('window').close();
            window.ygdNodeid='';
            delTempByUniquetagType('glgd');//清除临时表缓存
        }
    },
    /**
     * 获取数据管理主控制器
     * @returns {*|Ext.app.Controller}
     */
    findMainControl:function(){
        return this.application.getController('ManagementController');
    },
    //生成档号（仅生成档号，不改变条目的数据节点id）
    generateArchivecode: function (btn) {
        var filingView = this.findFilingView(btn);
        var filingGrid = this.findFilingGridView(btn);
        var dynamicfilingform = filingView.down('dynamicfilingform');
        var dynamicfilingFormValues = dynamicfilingform.getValues();
        var ygGrid=filingView.down('[itemId=ylId]');
        var ygStore = ygGrid.getStore();
        var filingValues = {};
        if(ygStore.data.length==0){
            XD.msg('无预归档记录，请在“未归”中选择预归档的记录');
            return;
        }
        Ext.Ajax.request({
            method: 'POST',
            url: '/management/getCalculation',
            params: {
                nodeid: dynamicfilingform.nodeid
            },
            scope: this,
            timeout: XD.timeout,
            success: function (response) {
                var calculation = Ext.decode(response.responseText).data;
                for (var name in dynamicfilingFormValues) {//遍历表单中的所有值
                    if (name == calculation) {
                        if (dynamicfilingFormValues[name] == '') {
                            if (typeof(dynamicfilingFormValues['autoAppraisal']) == 'undefined') {//如果没勾选自动鉴定
                                XD.msg('有必填项未填写');
                                return;
                            }
                        } else if (dynamicfilingFormValues[name].match(/^\d+$/) == null) {//未输入正整型数字
                            XD.msg('请检查件号数据是否规范正确!');
                            return;
                        }
                        else {
                            if (typeof(dynamicfilingFormValues['autoAppraisal']) != 'undefined') {//如果勾选了自动鉴定
                                continue;
                            }
                        }
                    } else {
                        if (dynamicfilingFormValues[name] == '') {
                            XD.msg('有必填项未填写');
                            return;
                        }
                    }
                    if (name == 'appraisaltype' || name == 'autoAppraisal') {
                        continue;
                    }
                    if (name == calculation) {
                        if (dynamicfilingFormValues[name] != '') {
                            if (typeof(dynamicfilingFormValues['autoAppraisal']) == 'undefined') {//如果没勾选自动鉴定
                                filingValues[name] = dynamicfilingFormValues[name];
                            }
                        }
                    } else {
                        filingValues[name] = dynamicfilingFormValues[name];
                    }
                }
                var appraisaltype = dynamicfilingFormValues['appraisaltype'];
                var filingValuesStrArr = this.findMainControl().objectToStringArray(filingValues);//传入后台的字符串数组
                var params = {
                    //entryids: entryids,       //预归档表格的所有条目ID
                    nodeid: dynamicfilingform.nodeid,         //归档目标节点的节点ID
                    filingValuesStrArr: filingValuesStrArr,   //档号设置字段的值（表单中的输入值）
                    appraisaltype: appraisaltype              //鉴定类型（规则）
                };
                var that=this;
                XD.confirm('确定根据下面列表中档案的顺序，生成档号？', function () {
                    var progressBarWin = Ext.create('Ext.window.Window', {
                        width: '35%',
                        height: 10,
                        header: false,
                        modal: true,
                        draggable: true,//禁止拖动
                        resizable: true,//禁止缩放
                        closeToolText: '关闭',
                        layout: 'fit',
                        items: [{
                            itemId:'progressbarId',
                            xtype: 'progressbar',
                            text: '生成归档进度'
                        }]
                    });
                    progressBarWin.show();
                    window.generateArchivecodeBar =progressBarWin.down('progressbar');//进度条
                    var socket = new SockJS("/websocket");
                    stompClient = Stomp.over(socket);
                    stompClient.connect({}, function(frame) {
                        stompClient.subscribe('/user/'+userid+'/generateArchiveBar', function(respnose){
                            var progressText = respnose.body;//压缩进度
                            var num=progressText.substring(0,progressText.indexOf('&'));
                            var count=progressText.substring(progressText.indexOf('&')+1);
                            var percentage = 0;
                            percentage = num/count;
                            progressText = num+'/'+count;
                            window.generateArchivecodeBar.updateProgress(percentage,progressText);//刷新进度
                        });
                    });

                    Ext.Ajax.request({
                        method: 'POST',
                        url: '/management/generateArchivecode',
                        params: params,
                        scope: that,
                        timeout: XD.timeout,
                        success: function (response) {
                            var responseText = Ext.decode(response.responseText);
                            if (responseText.success == true) {
                                var info;
                                if (responseText.msg == '需要调整计算项值') {
                                    info = 'ok';
                                } else {
                                    info = 'no';
                                }
                                Ext.Ajax.request({//调整计算项数值
                                    url: '/management/ajustAllCalData',
                                    params: {
                                        /*entryids: filingView.allEntryids,
                                         entryids: entryids,*/
                                        nodeid: dynamicfilingform.nodeid,         //归档目标节点的节点ID
                                        info: info
                                    },
                                    method: 'POST',
                                    timeout: XD.timeout,
                                    success: function (res) {
                                        ManagementIsArchivecode = true;
                                        //Ext.Msg.wait('生成档号操作完成', '正在操作').hide();
                                        progressBarWin.close();
                                        XD.msg((Ext.decode(res.responseText)).msg);
                                        //此处dataSource参数为列表数据来源标识（''为直接从临时表中读取数据）
                                        Ext.apply(filingGrid.getStore().getProxy().extraParams, {
                                            dataSource: '',
                                            ygType:'ygd'
                                        });
                                        filingGrid.notResetInitGrid();
                                    },
                                    failure: function () {
                                        //Ext.MessageBox.hide();
                                        progressBarWin.close();
                                        XD.msg('操作失败');
                                    }
                                });
                            } else {
                                ManagementIsArchivecode = true;
                                //Ext.MessageBox.hide();
                                progressBarWin.close();
                                XD.msg(responseText.msg);
                            }
                        },
                        failure: function () {
                            Ext.MessageBox.hide();
                            XD.msg('操作失败');
                        }
                    });
                });


            }
        });
    },

    moveup:function (btn) {
        var filingView = this.findFilingView(btn);
        var filingGrid = this.findFilingGridView(btn);
        var records = filingGrid.getSelectionModel().getSelection();
        if(records.length<1){
            XD.msg('请选择一条需要移动的数据');
            return;
        }else if(records.length>1){
            XD.msg('只能选择一条数据');
            return;
        }
        Ext.Ajax.request({
            method: 'post',
            url: '/management/moveup',
            params: {
                currentId: records[0].data.entryid,
                nodeid: records[0].data.nodeid
            },
            success:function (response) {
                var responseText = Ext.decode(response.responseText);
                if (responseText.success == true) {
                    filingGrid.getStore().proxy.extraParams.dataSource='';
                    filingGrid.getStore().proxy.extraParams.ygType='ygd';
                    filingGrid.getStore().reload();
                }
                window.moveUpOrDown=true;
                window.orderType=1;
                if(records[0].data.archivecode==''||records[0].data.archivecode==undefined){
                    XD.msg(responseText.msg);
                }else{
                    XD.msg(responseText.msg+'！点击生成档号，将根据新顺序重新生成档号！');
                }

            },
            failure:function () {
                Ext.MessageBox.hide();
                XD.msg('操作中断');
            }
        })
    },
    movedown:function (btn) {
        var filingView = this.findFilingView(btn);
        var filingGrid = this.findFilingGridView(btn);
        var records = filingGrid.getSelectionModel().getSelection();
        if(records.length<1){
            XD.msg('请选择一条需要移动的数据');
            return;
        }else if(records.length>1){
            XD.msg('只能选择一条数据');
            return;
        }
        Ext.Ajax.request({
            method: 'post',
            url: '/management/movedown',
            params: {
                currentId: records[0].data.entryid,
                // nodeid: filingView.managementgridNodeid
                nodeid: records[0].data.nodeid
            },
            success:function (response) {
                var responseText = Ext.decode(response.responseText);
                if (responseText.success == true) {
                    filingGrid.getStore().proxy.extraParams.dataSource='';
                    filingGrid.getStore().proxy.extraParams.ygType='ygd';
                    filingGrid.getStore().reload();
                }
                window.moveUpOrDown=true;
                window.orderType=1;
                if(records[0].data.archivecode==''||records[0].data.archivecode==undefined){
                    XD.msg(responseText.msg);
                }else{
                    XD.msg(responseText.msg+'！点击生成档号，将根据新顺序重新生成档号！');
                }
            },
            failure:function () {
                Ext.MessageBox.hide();
                XD.msg('操作中断');
            }
        })
    },

    //预归档页面的归档排序设置
    saveOrder:function(btn){
        var filingView = this.findFilingView(btn);
        var ordertxtLab=filingView.down('[itemId=ordertxtId]');
        var ordertxtLabTwo=window.wgGrid.down('[itemId=orderTxtLabelId]');//未归页面标签
        var codesettingItemSelectedFormView = filingView.down('[itemId=itemselectorItemID]');
        var tostore = codesettingItemSelectedFormView.getComponent("itemselectorID").toField.boundList.store;
        if (tostore.getCount() <= 0) {
            XD.msg("请至少选择一个字段");
            return;
        }
        var that=this;
        var ordersettingSelectedFormView=filingView;
        if(window.moveUpOrDown){
            XD.confirm('预归档所有记录，会重新排序。之前若有进行预归档页面的上下移操作将无效，清谨慎操作！确定保存？', function () {
                that.orderset(tostore,ordertxtLabTwo,ordersettingSelectedFormView,that);
                window.orderType=0;//切换到预归档要重新排序
            });
        }else{
            that.orderset(tostore,ordertxtLabTwo,ordersettingSelectedFormView,that);
            window.orderType=0;//切换到预归档要重新排序
        }

    },

    //未归页面的归档排序设置
    saveOrderTwo:function(btn){
        var ordersettingSelectedFormView = btn.findParentByType('ordersettingSelectedFormView');
        var ordertxtLabTwo=window.wgGrid.down('[itemId=orderTxtLabelId]');
        var codesettingItemSelectedFormView = ordersettingSelectedFormView.down('[itemId=itemselectorItemID]');
        var tostore = codesettingItemSelectedFormView.getComponent("itemselectorID").toField.boundList.store;
        if (tostore.getCount() <= 0) {
            XD.msg("请至少选择一个字段");
            return;
        }
        var that=this;
        if(window.moveUpOrDown){
            XD.confirm('预归档所有记录，会重新排序。之前若有进行预归档页面的上下移操作将无效，清谨慎操作！确定保存？', function () {
                that.orderset(tostore,ordertxtLabTwo,ordersettingSelectedFormView,that);
                window.wgChange=true;//标记未归页面有设置变动
            });
        }else{
            that.orderset(tostore,ordertxtLabTwo,ordersettingSelectedFormView,that);
            window.wgChange=true;//标记未归页面有设置变动
        }
    },

    //排序设置
    orderset:function(tostore,ordertxtLabTwo,ordersettingSelectedFormView,that){
        var recordslist = [];
        for (var i = 0; i < tostore.getCount(); i++) {
            recordslist.push(tostore.getAt(i).get('fieldcode'));
        }
        Ext.Ajax.request({
            params: {
                datanodeid: window.wgOrderNodeid,
                fieldcodelist: recordslist
            },
            url: '/ordersetting/setCode',
            async:false,
            method: 'post',
            success: function (resp) {
                XD.msg('保存成功');
                //保存成功后更新排序提示,刷新编辑字段表
                var orderTxt=Ext.decode(resp.responseText).msg;
                window.ordertxtLab.setTitle('当前归档顺序: '+orderTxt);
                ordertxtLabTwo.setText('当前归档顺序: '+orderTxt);
                window.ordertxtLabThr.setText('当前归档顺序: '+orderTxt+" 。");
                that.initOrderset(ordersettingSelectedFormView);
            },
            failure: function () {
                XD.msg('操作失败');
            }
        });
    },
    //保管期限调整（选择列表中数据，调整保管期限值）
    retentionAdjust:function (btn) {
        var filingView = this.findFilingView(btn);
        var filingGrid = this.findFilingGridView(btn);
        var record = filingGrid.getSelectionModel().getSelection();
        if(record.length<1){
            XD.msg('请选择需要调整保管期限的记录');
            return;
        }
        var entryids = [];
        for(var i=0;i<record.length;i++){
            entryids.push(record[i].get('entryid'));
        }
        var retentionAdjustWin = Ext.create('Ext.window.Window',{
            modal:true,
            title:'保管期限调整',
            closeToolText:'关闭',
            items:[{
                xtype: 'retentionAdjustFromView',
                entryids:entryids.join(','),            //需调整保管期限记录的条目id
                filingGrid:filingGrid,                  //归档预览列表
                filingEntryids:filingView.allEntryids   //归档预览列表中所有数据的条目id
            }]
        });
        retentionAdjustWin.show();
    },
    //保管期限调整确定
    retentionAjustConfirm: function (btn) {
        var form = btn.up('form');
        var entryretention = form.getValues()['entryretention'];
        var params = {
            entryids: form.entryids,         //归档预览列表中选定需要调整保管期限记录的条目ID
            entryretention: entryretention, //调整后的保管期限值
            nodeid: form.filingGrid.dataParams.nodeid,   //归档目标节点id
            type: '数据管理'
        };
        Ext.Ajax.request({
            method: 'POST',
            url: '/management/retentionAjust',
            params: params,
            scope: this,
            timeout: XD.timeout,
            success: function (response) {
                XD.msg(Ext.decode(response.responseText).msg);
                btn.up('window').close();
                form.filingGrid.notResetInitGrid();
            }
        });
        if (form.entryids.split(",").length > 49) {
            var progressBarWin = Ext.create('Ext.window.Window', {
                width: '35%',
                height: 10,
                header: false,
                modal: true,
                draggable: true,//禁止拖动
                resizable: true,//禁止缩放
                closeToolText: '关闭',
                layout: 'fit',
                items: [{
                    itemId: 'progressbarId2',
                    xtype: 'progressbar',
                    text: '调整保管期限'
                }]
            });
            progressBarWin.show();
            window.storageTimeBar = progressBarWin.down('progressbar');//进度条
            var socket = new SockJS("/websocket");
            stompClient = Stomp.over(socket);
            stompClient.connect({}, function (frame) {
                stompClient.subscribe('/user/' + userid + '/storageTimeBar', function (respnose) {
                    var progressText = respnose.body;
                    var num = progressText.substring(0, progressText.indexOf('&'));
                    var count = progressText.substring(progressText.indexOf('&') + 1);
                    var percentage = 0;
                    percentage = num / count;
                    progressText = num + '/' + count;
                    window.storageTimeBar.updateProgress(percentage, progressText);//刷新进度
                    if (1 == percentage) {
                        progressBarWin.close();
                    }
                });
            });
        }
    },

    //修改预归档字段值
    saveEntry:function(btn){
        var win=btn.up('[itemId=entryEditWinId]');
        var formview=win.down('dynamicform');
        Ext.MessageBox.wait('正在保存请稍后...', '提示');
        formview.submit({
            url: '/management/entriesTemp',//保存到临时表
            method: 'POST',
            params: {dataNodeid:window.ygdNodeid},
            scope: this,
            success: function (form, action) {
                Ext.MessageBox.hide();
                //XD.msg(action.result.msg);
                var msg=action.result.msg;
                if(msg==0){
                    XD.msg("修改失败，档号重复！");
                    return;
                }else{
                    XD.msg("修改成功！");
                    //多条时切换到下一条。单条时或最后一条时切换到列表界面,同时刷新列表数据
                    if (formview.entryids && formview.entryids.length > 1 && formview.entryid != formview.entryids[formview.entryids.length - 1]) {
                        this.ygdRefreshFormData(formview, 'next');
                        window.updateType=true;
                    }else{
                        btn.up('[itemId=entryEditWinId]').close();//关闭修改窗口
                        //更新预归档列表
                        var gridcard=window.filingGrid;
                        gridcard.getStore().proxy.extraParams.dataSource = '';//修改参数避免重复增加临时条目
                        gridcard.getStore().proxy.extraParams.ygType='ygd';
                        gridcard.getStore().reload();
                        window.updateType=false;
                    }
                }
            },
            failure: function (form, action) {
                Ext.MessageBox.hide();
                XD.msg("保存失败,请查看必填项是否已全部填写");
            }
        });
    },

    closeEntry:function(btn){
        btn.up('[itemId=entryEditWinId]').close();//关闭修改窗口
        //更新预归档列表
        if(window.updateType){//有更新时刷新表格
            var gridcard=window.filingGrid;
            gridcard.getStore().proxy.extraParams.dataSource = '';//修改参数避免重复增加临时条目
            gridcard.getStore().proxy.extraParams.ygType='ygd';
            gridcard.getStore().reload();
            window.updateType=false;
        }
    },

    //文件归档最后一步（“归档”按钮的单击事件）
    filing: function (btn) {
        var filingView = this.findFilingView(btn);
        var filingGrid = this.findFilingGridView(btn);
        var records = filingGrid.getSelectionModel().getSelection();
        var allSelect = [];
        var tmp = [];
        if(records.length==0){
            Ext.Ajax.request({
                method:'POST',
                url: '/acquisition/getTempEntryids',
                params:{nodeid:window.filingGrid.dataParams.nodeid,type:'management'},
                async:false,
                scope:this,
                timeout:XD.timeout,
                success: function (response) {
                    allSelect=Ext.decode(response.responseText).data;
                },
                failure:function () {
                    XD.msg('操作失败');
                }
            });
            if(allSelect.length>5000){
                XD.msg('一次只能归档5千条档案数据，请勾选记录，分批归档。归档会进行档号重复验证，防止数据量过大时，导致速度过慢及操作中断异常。');
                return;
            }
            for (var i = 0; i < allSelect.length; i++) {
                if(allSelect[i].archivecode!=undefined&&allSelect[i].archivecode.trim()!=''){
                    tmp.push(allSelect[i].entryid);
                }else{
                    XD.msg('有档号为空的记录，无法归档!');
                    return;
                }
            }
            var that = this;
            XD.confirm("不勾选记录进行归档，将归档所有预归档条目",function () {
                that.dofiling(tmp,filingGrid,filingView,btn);
            });
        }else{
            if(records.length>5000){
                XD.msg('一次只能归档5千条档案数据，请勾选记录，分批归档。归档会进行档号重复验证，防止数据量过大时，导致速度过慢及操作中断异常。');
                return;
            }
            for (var i = 0; i < records.length; i++) {
                if(records[i].get('archivecode')!=undefined&&records[i].get('archivecode').trim()!=''){
                    tmp.push(records[i].get('entryid'));
                }else{
                    XD.msg('有档号为空的记录，无法归档!');
                    return;
                }
            }
            this.dofiling(tmp,filingGrid,filingView,btn);
        }

    },

    //执行归档
    dofiling:function (tmp,filingGrid,filingView,btn) {
        var entryids='';
        entryids = tmp.join(',');
        var params = {
            entryids: entryids,       //选定的记录的条目ID,在filingHandler方法中定义并赋值
            nodeid:window.filingGrid.dataParams.nodeid
        };
        //Ext.MessageBox.wait('正在归档请稍后...', '提示');

        var progressBarWin = Ext.create('Ext.window.Window', {
            width: '35%',
            height: 10,
            header: false,
            modal: true,
            draggable: true,//禁止拖动
            resizable: true,//禁止缩放
            closeToolText: '关闭',
            layout: 'fit',
            items: [{
                itemId:'progressbarId',
                xtype: 'progressbar',
                text: '归档进度'
            }]
        });
        progressBarWin.show();
        window.progressBar =progressBarWin.down('progressbar');//进度条
        var socket = new SockJS("/websocket");
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function(frame) {
            stompClient.subscribe('/user/'+userid+'/archiveBar', function(respnose){
                var progressText = respnose.body;//压缩进度
                var num=progressText.substring(0,progressText.indexOf('&'));
                var count=progressText.substring(progressText.indexOf('&')+1);
                var percentage = 0;
                percentage = num/count;
                progressText = num+'/'+count;
                window.progressBar.updateProgress(percentage,progressText);//刷新进度
            });
        });

        Ext.Ajax.request({
            method: 'POST',
            url: '/management/entryIndexes/filing',
            params: params,
            scope: this,
            timeout: XD.timeout,
            success: function (response) {
                //Ext.MessageBox.hide();
                progressBarWin.close();
                XD.msg(Ext.decode(response.responseText).msg);
                //刷新列表数据，同时判断是否所有都已归档
                filingGrid.getStore().proxy.extraParams.dataSource='del';//重新赋值序号
                filingGrid.getStore().proxy.extraParams.ygType='ygd';
                filingGrid.delReload(filingGrid.selModel.getSelectionLength(),function () {
                    if(filingGrid.getStore().getCount()<1){
                        window.ygdNodeid='';//返回节点表单页面，预归档标记为空
                        btn.up('window').close();
                        filingView.managementgrid.notResetInitGrid();
                        ManagementIsArchivecode = false;
                    }else{
                        filingView.managementgrid.notResetInitGrid();
                    }
                });

                var data = Ext.decode(response.responseText).data;
                var ids = [];
                for (var i = 0; i < data.length; i++) {
                    ids.push(data[i].entryid);
                }
                //进行采集业务元数据
                captureServiceMetadataByZL(ids,'数据管理','归档');
            },
            failure: function () {
                //Ext.MessageBox.hide();
                progressBarWin.close();
                XD.msg('操作失败');
            }
        });
    },
    //返回至归档第一步form（“上一步”按钮的单击事件）
    activeFilingFirstForm: function (btn) {
        var filingView = this.findFilingView(btn);
        filingView.tabBar.items.items[0].show();
        filingView.tabBar.items.items[1].hide();
        filingView.tabBar.items.items[2].hide();
        var filingViewSub = btn.up('[itemId=gdszId]');
        var filingFirstForm = this.findFilingFirstFormView(btn);
        filingView.setActiveTab(0);
    },
    //切换到归档第二步窗口（dynamicform动态表单及basicgrid列表）
    activeFilingFormAndGrid: function (btn,state) {
        var filingView = this.findFilingView(btn);
        var filingViewSub = btn.up('[itemId=ygdId]');
        var filingFormAndGrid = this.findFilingFormAndGridView(btn);
        var filingGrid = this.findFilingGridView(btn);
        var ygType='';//首次加载选中条目
        var allEntryids='';//选择所有页的条目ID
        allEntryids=filingView.allEntryids;
        //不传入multiValue参数，后台会根据nodeid获取档号构成字段，自动将其字段编码传入multiValue，使列表列加载档号构成字段
        var params = {
            isSelectAll: filingView.isSelectAll,
            dataNodeid: filingView.tempParams['nodeid'],
            condition: filingView.tempParams['condition'],
            operator: filingView.tempParams['operator'],
            content: filingView.tempParams['content'],
            entryids: filingView.entryids,       //选定的记录的条目ID,在filingHandler方法中定义并赋值
            allEntryids:allEntryids,  //选择所有页的条目ID
            nodeid: filingFormAndGrid.nodeid,  //归档目标节点的节点ID
            dataSource: 'capture',             //此处dataSource参数为列表数据来源标识（capture为通过采集表查询数据，处理后存至临时表，再从临时表中读取数据）
            type: '保管期限调整',
            ygType:ygType  //标记预归档
        };
        filingGrid.initGrid(params, true);
        window.filingGrid=filingGrid;
    },

    initWgGrid:function (view) {//显示本节点的条目，且条目不在临时表
        var nodeid =view.config.items[0].managementgridNodeid;
        var filingGrid = view.down('[itemId=wgNodeId]');
        filingGrid.initGrid({nodeid:nodeid});
        window.wgGrid=filingGrid;
    },
    changeComboState:function (view) {//改变保管期限下拉框状态
        var dynamicfilingform = view.up('dynamicfilingform');
        var entryretentionCombo = dynamicfilingform.down('[itemId=entryretention]');
        var appraisaltypeCombo = dynamicfilingform.down('[itemId=appraisaltype]');
        if(view.checked){
            Ext.Msg.alert('提示', '根据所选鉴定类型的标准自动鉴定保管期限，鉴定失败则默认为“短期”！');
        }
        Ext.Ajax.request({
        	method:'POST',
            url: '/management/getCalculation',
            params:{
            	nodeid: dynamicfilingform.nodeid
            },
            scope:this,
            success: function (response) {
                var calculation = Ext.decode(response.responseText).data;
                var calculationCombo = dynamicfilingform.down('[itemId='+calculation+']');
                var calculationbutton = dynamicfilingform.down('[itemId='+calculation+'calBtn]');
                var calculationField = dynamicfilingform.down('[itemId='+calculation+'Field]');
		        if(entryretentionCombo.disabled){//若保管期限下拉框状态为disable，则执行控件激活
		        	calculationCombo.show();
		        	calculationbutton.show();
		            entryretentionCombo.enable();
		            if (calculationField) {
		            	calculationField.show();
		            }
		        }else{//若保管期限下拉框状态为激活状态，则使控件失效
		        	calculationCombo.hide();
		        	calculationbutton.hide();
		            entryretentionCombo.disable();
		            if (calculationField) {
		            	calculationField.hide();
		            }
		        }
		        if(appraisaltypeCombo.disabled){//若鉴定类型下拉框状态为disable，则执行控件激活
		        	calculationCombo.hide();
		        	calculationbutton.hide();
		            appraisaltypeCombo.enable();
		            if (calculationField) {
		            	calculationField.hide();
		            }
		        }else{//若鉴定类型下拉框状态为激活状态，则使控件失效
		        	calculationCombo.show();
		        	calculationbutton.show();
		            appraisaltypeCombo.disable();
		            if (calculationField) {
		            	calculationField.show();
		            }
		        }
            }
        });
    },
    initFormDataOnly:function(operate, form, entryid) {
        form.type = '预归档';
        var nullvalue = new Ext.data.Model();
        var acquisitionform = form.up('acquisitionform');
        var fields = form.getForm().getFields().items;
        var preNextPanel = form.down('[itemId=preNextPanel]');
        preNextPanel.hide();
        var editFrom = form.up('entryEditFromView');
        var totaltext = editFrom.down('[itemId=ygdTotalText]');
        var nowtext = editFrom.down('[itemId=ygdNowText]');
        var count = 1;
        for (var i = 0; i < form.entryids.length; i++) {
            if (form.entryids[i] == entryid) {
                count = i + 1;
                break;
            }
        }
        var total = form.entryids.length;
        totaltext.setText('当前共有  ' + total + '  条，');
        nowtext.setText('当前记录是第  ' + count + '  条');
        for (var i = 0; i < fields.length; i++) {
            if (fields[i].value && typeof (fields[i].value) == 'string' && fields[i].value.indexOf('label') > -1) {
                continue;
            }
            if (fields[i].xtype == 'combobox') {
                fields[i].originalValue = null;
            }
            nullvalue.set(fields[i].name, null);
        }
        form.loadRecord(nullvalue);
        /*var settingState = this.ifSettingCorrect(form.nodeid,form.templates);
         if(!settingState){
         return;
         }*/
        Ext.each(fields, function (item) {
            item.setReadOnly(false);
            if (item.freadOnly) {
                item.setReadOnly(true);
            }
        });
    },
    initFormDataOnly:function(operate, form, entryid){
        form.type = '预归档';
        var nullvalue = new Ext.data.Model();
        var acquisitionform = form.up('acquisitionform');
        var fields = form.getForm().getFields().items;
        var  preNextPanel= form.down('[itemId=preNextPanel]');
        preNextPanel.hide();
        var editFrom=form.up('entryEditFromView');
        var totaltext = editFrom.down('[itemId=ygdTotalText]');
        var nowtext = editFrom.down('[itemId=ygdNowText]');
        var count = 1;
        for(var i=0;i<form.entryids.length;i++){
            if(form.entryids[i]==entryid){
                count=i+1;
                break;
            }
        }
        var total = form.entryids.length;
        totaltext.setText('当前共有  ' + total + '  条，');
        nowtext.setText('当前记录是第  ' + count + '  条');
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
        /*var settingState = this.ifSettingCorrect(form.nodeid,form.templates);
         if(!settingState){
         return;
         }*/
        Ext.each(fields,function (item) {
            item.setReadOnly(false);
            if(item.freadOnly){
                item.setReadOnly(true);
            }
        });

        var eleview = form.up('entryEditFromView').down('electronic');
        eleview.initData(entryid);
        Ext.Ajax.request({
            method: 'GET',
            scope: this,
            url: '/management/entryTemp/' + entryid,
            success: function (response) {
                var entry = Ext.decode(response.responseText);

                //获取上边的档号字段值给修改的条目赋值
                //var objectjson = dynamicfilingform.getForm().getValues();//获取上边的档号生成字段表单记录
                var records=JSON.stringify(form.objectjson);//转码，用encode会出现中文乱码
                records=records.substring(1,records.length-1);
                records=records.split(",");//各字段record组合
                for(var i=0;i<records.length-1;i++){//不设置最后一个统计字段
                    var fieldAndValue=records[i].split(":");
                    var fieldCode=fieldAndValue[0].substring(1,fieldAndValue[0].length-1);//字段
                    var value=fieldAndValue[1].substring(1,fieldAndValue[1].length-1).trim();//字段值
                    if(value!=''&&(entry[fieldCode]==''||entry[fieldCode]==undefined)){//如果条目中档号组成字段本身有值，就直接显示即可，如果没有，才去获取顶部设置
                        entry[fieldCode]=value;
                    }else{
                        /*XD.msg("请先填写上边的档号字段表单");
                         return;*/
                    }
                }

                var data = Ext.decode(response.responseText);
                if (data.organ) {
                    entry.organ = data.organ;//机构
                }
                //著录、修改时，机构/问题字段的类型为“字符型”时，获取机构名。
                if (operate == 'add' || operate == 'modify') {
                    if (!data.organ) {
                        Ext.Ajax.request({
                            async:false,
                            url: '/nodesetting/findByNodeid/' + form.nodeid,
                            success:function (response) {
                                var data = Ext.decode(response.responseText);
                                if (data.success){
                                    entry.organ = data.data;
                                }
                            }
                        });
                    }
                }
                var fieldCode = form.getRangeDateForCode();//字段编号，用于特殊的自定义字段(范围型日期)
                if (fieldCode != null) {
                    //动态解析数据库日期范围数据并加载至两个datefield中
                    form.initDaterangeContent(entry);
                }
                form.loadRecord({getData: function () {return entry;}});

            }
        });
    },

    //预归档修改 点击上一条
    ygdPreHandler: function (btn) {
        var form = btn.up('entryEditFromView').down('dynamicform');
        this.ygdPreNextHandler(form, 'pre');
    },

    //预归档修改 点击下一条
    ygdNextHandler: function (btn) {
        var form = btn.up('entryEditFromView').down('dynamicform');
        this.ygdPreNextHandler(form, 'next');
    },

    //预归档修改 条目切换，上一条下一条
    ygdPreNextHandler: function (form, type) {
        var dirty = !!form.getForm().getFields().findBy(function (f) {
            return f.wasDirty;
        });
        if (dirty) {
            XD.confirm('数据已修改，确定保存吗？', function () {
                //保存数据
                var formview = this.form;
                var nodename = this.ref.getNodename(formview.nodeid);
                var params = {
                    nodeid: formview.nodeid,
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

                formview.submit({
                    url: '/management/entriesTemp',//保存到临时表
                    method: 'POST',
                    params: {dataNodeid:formview.nodeid},
                    scope: this,
                    success: function (form, action) {
                        Ext.MessageBox.hide();
                        //XD.msg(action.result.msg);
                        var msg=action.result.msg;
                        if(msg==0){
                            XD.msg("修改失败，档号重复！");
                            return;
                        }else{
                            XD.msg("修改成功！");
                            this.ref.ygdRefreshFormData(this.form, this.type);
                            window.updateType=true;
                        }
                    },
                    failure: function (form, action) {
                        Ext.MessageBox.hide();
                        XD.msg("保存失败,请查看必填项是否已全部填写");
                    }
                });
            }, {
                ref: this,
                form: form,
                type: type
            }, function () {
                this.ref.ygdRefreshFormData(this.form, this.type)
            });
        } else {
            this.ygdRefreshFormData(form, type);
        }
    },

    //预归档修改 刷新下一条
    ygdRefreshFormData: function (form, type) {
        var entryids = form.entryids;
        var currentEntryid = form.entryid;
        var entryid;
        for (var i = 0; i < entryids.length; i++) {
            if (type == 'pre' && entryids[i] == currentEntryid) {
                if (i == 0) {
                    i = entryids.length;
                }
                entryid = entryids[i - 1];
                break;
            } else if (type == 'next' && entryids[i] == currentEntryid) {
                if (i == entryids.length - 1) {
                    i = -1;
                }
                entryid = entryids[i + 1];
                break;
            }
        }
        form.entryid = entryid;
        this.initFormDataOnly('modify',form, entryid);//条目赋值
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
			if (ids.length > 1) {
				var filecode = "";
				for (var i = 0; i < record.length; i++) {
					if (i < ids.length - 1) {
						filecode = filecode + record[i].get('archivecode') + "∪";
					} else {
						filecode = filecode + record[i].get('archivecode');
					}
				}
				Ext.Ajax.request({
	                async:false,
	                url: '/acquisition/getFilecode',
	                params:{filecode: filecode},
	                success:function (response) {
	                    if(!Ext.decode(response.responseText).success) {
	                    	value = true;
	                    }
	                }
	            });
			}
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
	                xtype: 'managementSequenceView'//调序视图
	            }]
	        });
	        sequenceWin.show();
	        var view = sequenceWin.down('managementSequenceView').down('managementSequenceGridView');
	        var nodeid = this.getNodeid(northGrid.nodeid);
	        view.nodeid = nodeid;
	        view.currentNodeid = northGrid.nodeid;
	        //刷新调序表单
	        view.getStore().setPageSize(XD.pageSize);
	        view.initGrid({nodeid: nodeid, entryids: entryids, dataSource: 'capture'});
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
                xtype: 'managementReportGridView',
                entryids:ids,
                nodeid:nodeid
            }]
        });
        var reportGrid = reportGridWin.down('managementReportGridView');
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
				nodeid: grid.nodeid
            },
            success: function (response) {
                var responseText = Ext.decode(response.responseText);
                if (!responseText.success) {
                    XD.msg(responseText.msg);
                } else {
                	var store = grid.store;
			        store.proxy.url = '/management/sqEntryIndexes';
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
				nodeid: grid.nodeid
            },
            success: function (response) {
                var responseText = Ext.decode(response.responseText);
                if (!responseText.success) {
                    XD.msg(responseText.msg);
                } else {
                	var store = grid.store;
			        store.proxy.url = '/management/sqEntryIndexes';
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
            url: '/management/getArchivecodeValue',
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
			                url: '/management/saveSqtemp',
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
            url: '/management/changeState',
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
    
    //监听键盘按下事件
    addKeyAction:function (view) {
        var controller = this;
        var currentView;
        document.onkeydown = function () {
            if(formlayout == 'formgrid'){
                currentView = view.up('managementFormAndGrid').down('formAndGrid').down('managementform');
            }else if(formlayout == 'forminnergrid'){
                currentView = view.up('managementFormAndGrid').down('formAndInnerGrid').down('managementform');
            }else if(formlayout == 'formview'){
            	currentView = view.up('managementFormAndGrid').down('formView').down('managementform');
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
                 //XD.msg('Ctrl+Shift+S');
                Ext.defer(function () {
                    if(view.continueSaveBtn && view.operateFlag=='add' && formvisible){//此处增加operateFlag判断的目的是：屏蔽修改界面连续录入快捷键功能
                        if(uploadView){
                            controller.getUpload(eleview,uploadView);
                            uploadView.close();
                        }
                        controller.continueSubmitForm(view.continueSaveBtn,view.operateFlag);//连续录入
                    }
                },1);
                event.returnValue = false;//阻止event的默认行为
            }
            if (oEvent.ctrlKey && !oEvent.shiftKey && !oEvent.altKey && oEvent.keyCode == 83) { //这里只能用alt，shift，ctrl等去组合其他键event.altKey、event.ctrlKey、event.shiftKey 属性
                 //XD.msg('Ctrl+S');
                Ext.defer(function () {
                    if(view.saveBtn && formvisible){
                        if(uploadView){
                            controller.getUpload(eleview,uploadView);
                            uploadView.close();
                        }
                        controller.submitForm(view.saveBtn,view.operateFlag);//保存
                    }
                },1);
                event.returnValue = false;//阻止event的默认行为
                // return false;//阻止event的默认行为
            }
        }
    },
    
    getCurrentManagementform:function (btn) {
        if (btn.up('formAndGrid')) {//如果是案卷表单
            return this.findFormView(btn);
        }
        if (btn.up('formAndInnerGrid')){//如果是卷内表单
            return this.findFormInnerView(btn);
        }
        if (btn.up('formView') || btn.xtype == 'entrygrid' || btn.xtype == 'managementgrid') {
        	return formAndGridView.down('formView').down('managementform');
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

    //点击上一条
    preHandler:function(btn){
        var currentManagementform = this.getCurrentManagementform(btn);
        var form = currentManagementform.down('dynamicform');
        this.preNextHandler(form, 'pre');
    },

    //点击下一条
    nextHandler:function(btn){
        var currentManagementform = this.getCurrentManagementform(btn);
        var form = currentManagementform.down('dynamicform');
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
                    type: formview.findParentByType('managementform').operateFlag,
                    eleid: formview.findParentByType('managementform').down('electronic').getEleids(),
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
                    url: '/management/entries',
                    params: params,
                    scope: this,
                    success: function (form, action) {
                        Ext.MessageBox.hide();
                        this.ref.refreshFormData(this.form, this.type);
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

    //保存表单数据，返回列表界面视图
    submitForm:function(btn){
        var currentManagementform = this.getCurrentManagementform(btn);
        var eleids = currentManagementform.down('electronic').getEleids();
        var formview = currentManagementform.down('dynamicform');
        //字段编号，用于特殊的自定义字段(范围型日期)
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
                            entrytype:"management"
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
        var fieldCode = formview.getRangeDateForCode();
        var params = {
            nodeid: formview.nodeid,
            eleid: eleids,
            isMedia:window.isMedia,
            type: currentManagementform.operateFlag,
            operate: nodename
        };
        if (fieldCode != null) {
            params[fieldCode]=formview.getDaterangeValue();
        }
        var archivecodeSetState = formview.setArchivecodeValueWithNode(nodename);
        if(!archivecodeSetState){//若档号设置失败，则停止后续的表单提交
            return;
        }
        var operateType = formview.operateType;
        var submitType = formview.submitType;
        var formview_operate = formview.operate;
        Ext.MessageBox.wait('正在保存请稍后...','提示');
        formview.submit({
            method:'POST',
            url: '/management/entries',
            params:params,
            scope:this,
            success: function (form,action) {
                Ext.MessageBox.hide();
                var treepanel = this.findTreeView(btn);
                var nodeid = treepanel.selModel.getSelected().items[0].get('fnid');
                if(action.result.success==true){
                    if(operateType=='insertion'){//插件、插卷
                        var pages = action.result.data.pages;
                        var state = this.updateSubsequentData(this.entryID,submitType,pages);
                        //切换到列表界面,同时刷新列表数据
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
                    }else{
                        if(formview.nodeid != nodeid){
                            this.activeGrid(btn, false);
                            this.findInnerGrid(btn).getStore().reload();
                        }else{
                            this.activeGrid(btn,true);
                            this.findInnerGrid(btn).getStore().removeAll();
                        }
                    }
                    XD.msg(action.result.msg);
                    var entryids = [action.result.data.entryid];
                    //进行采集业务元数据
                    if("modify"==formview_operate){
                        captureServiceMetadataByZL(entryids,'数据管理','编辑');
                    }else {
                        captureServiceMetadataByZL(entryids,'数据管理','著录');
                    }
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
    continueSubmitForm:function(btn){
        var currentManagementform = this.getCurrentManagementform(btn);
        var solidview = currentManagementform.down('solid');
        var eleids = currentManagementform.down('electronic').getEleids();
        var formview = currentManagementform.down('dynamicform');
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
        //字段编号，用于特殊的自定义字段(范围型日期)
        var nodename = this.getNodename(formview.nodeid);
        var fieldCode = formview.getRangeDateForCode();
        var params={
            nodeid: formview.nodeid,
            eleid:eleids,
            isMedia:window.isMedia,
            type: currentManagementform.operateFlag,
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
            method:'POST',
            url: '/management/entries',
            params:params,
            scope:this,
            success: function (form,action) {
                Ext.MessageBox.hide();
                var treepanel = this.findTreeView(btn);
                var nodeid = treepanel.selModel.getSelected().items[0].get('fnid');
                //每次点击连续著录时刷新列表
                if(formview.nodeid != nodeid){
                    this.findInnerGrid(btn).getStore().reload();//刷新卷内表单
                    this.findInnerGridView(btn).getStore().reload();
                    this.findFormInnerView(btn).down('electronic').initData();
                    this.findFormInnerView(btn).down('solid').initData();
                    // this.findFormInnerView(btn).down('long').initData();
                }else{
                    if(window.isMedia){
                    }else{
                        this.findActiveGrid(btn).getStore().reload();//刷新案卷表单
                    }
                    this.findGridToView(btn).getStore().reload();
                    this.findFormView(btn).down('electronic').initData();
                    this.findFormView(btn).down('solid').initData();
                    // this.findFormView(btn).down('long').initData();
                }
                this.addCalValue(formview);
                if(archivecodeSetState!='无档号节点'){
                    formview.setArchivecodeValueWithNode(nodename);
                }
                XD.msg(action.result.msg);
                //点击连续录入后，遍历表单中所有控件，将光标移动至第一个非隐藏且非只读的控件
                var fields = form.getFields().items;
                for(var i=0;i<fields.length;i++){
                    if(fields[i].xtype!='hidden' && fields[i].xtype!='displayfield' && fields[i].readOnly==false){
                        fields[i].focus(true);
                        if(fields[i].getValue()!=null){
                            fields[i].selectText(0,fields[i].getValue().length);
                        }
                        break;
                    }
                }
                var allMediaFrame = document.querySelectorAll('#mediaFrame');
                if(allMediaFrame){
                    for (var i = 0; i < allMediaFrame.length; i++) {
                        allMediaFrame[i].setAttribute('src','');
                    }
                }
                //进行采集业务元数据
                var captureService_entryid = [action.result.data.entryid];
                captureServiceMetadataByZL(captureService_entryid,'数据管理','著录');
            },
            failure: function (form, action) {
            	Ext.MessageBox.hide();
            	XD.msg(action.result.msg);
            }
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
            url: '/management/updateSubsequentData',
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
    hideFilingSecondStepBtn: function (view) {
        var filingnextstep = view.down('[itemId=filingNextStepBtn]');
        var filingback = view.down('[itemId=filingBackBtn]');
        var filing = view.down('[itemId=filingBtn]');
        var filingpreviousStep = view.down('[itemId=filingpreviousStepBtn]');
        var ordersetSaveBtn = view.down('[itemId=ordersettingSaveBtnId]');
        /*if (view.xtype == 'window') {
         view = view.down('managementfiling');
         }*/
        view = view.down('[itemId=ygdId]');
        var tbseparator = view.getDockedItems('toolbar')[0].query('tbseparator');
        filingnextstep.setVisible(true);
        filingback.setVisible(true);
        filing.setVisible(false);
        ordersetSaveBtn.setVisible(true);
        filingpreviousStep.setVisible(false);
        tbseparator[0].setVisible(true);
        tbseparator[1].setVisible(true);
        tbseparator[2].setVisible(false);
        tbseparator[3].setVisible(false);
    },
    hideFilingFirstStepBtn: function (view) {
        var filingnextstep = view.down('[itemId=filingNextStepBtn]');
        var filingback = view.down('[itemId=filingBackBtn]');
        var filing = view.down('[itemId=filingBtn]');
        var filingpreviousStep = view.down('[itemId=filingpreviousStepBtn]');
        var ordersetSaveBtn = view.down('[itemId=ordersettingSaveBtnId]');
        /*if (view.xtype == 'window') {
         view = view.down('managementfiling');
         }*/
        view = view.down('[itemId=ygdId]');
        var tbseparator = view.getDockedItems('toolbar')[0].query('tbseparator');
        filingnextstep.setVisible(false);
        filingback.setVisible(false);
        filing.setVisible(true);
        ordersetSaveBtn.setVisible(false);
        filingpreviousStep.setVisible(true);
        tbseparator[0].setVisible(false);
        tbseparator[1].setVisible(false);
        tbseparator[2].setVisible(false);
        tbseparator[3].setVisible(true);
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
     * 6(文件管理) - 显示分类管理按钮
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
         * 未归管理、资料管理、文件管理的分类管理按钮
         */
        //隐藏分类管理按钮
        this.hideToolbarBtnTbsByItemId('classificationManagement',buttons,tbseparator);
        this.hideToolbarBtnTbsByItemId('classificationManagement',btns,tbs);

        //显示数据关联按钮
        this.showToolbarBtnTbsByItemId(classlevel,'dataAssociation',buttons,tbseparator);
        this.showToolbarBtnTbsByItemId(classlevel,'dataAssociation',btns,tbs);
        
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
            //资料管理、文件管理
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
                if(tbs.length >= 1){
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
    
    //分类设置 -------------- start ---------------------
    //隐藏分类第一步窗口按钮
    hideFirstStepBtn:function (view) {
        var previousStepBtn = view.down('[itemId=previousStepBtn]');
        var setInfo = view.down('[itemId=setInfo]');
        previousStepBtn.setVisible(false);
        setInfo.setVisible(false);
        var classificationSet = view.down('[itemId=classificationSet]');
        var classificationAutoSet = view.down('[itemId=classificationAutoSet]');
        var classificationBackBtn = view.down('[itemId=classificationBackBtn]');
        classificationSet.setVisible(true);
        classificationAutoSet.setVisible(true);
        classificationBackBtn.setVisible(true);
    },
    //隐藏分类第二步窗口按钮
    hideSecondStepBtn:function (view) {
        var classificationSet = view.down('[itemId=classificationSet]');
        var classificationAutoSet = view.down('[itemId=classificationAutoSet]');
        var classificationBackBtn = view.down('[itemId=classificationBackBtn]');
        classificationSet.setVisible(false);
        classificationAutoSet.setVisible(false);
        classificationBackBtn.setVisible(false);
        var previousStepBtn = view.down('[itemId=previousStepBtn]');
        var setInfo = view.down('[itemId=setInfo]');
        previousStepBtn.setVisible(true);
        setInfo.setVisible(true);
    },
    //从分类设置返回表格视图
    backToGrid:function (btn) {
        btn.up('window').hide();
    },
    //从分类设置第二步面板返回分类设置第一步面板
    activeClassificationFirstForm:function (btn) {
        var classificationView = this.findClassificationGrid(btn);
        var firstForm = this.findClassificationFirstView(btn);
        classificationView.setActiveItem(firstForm);
        //隐藏上一步和设置按钮
        this.hideFirstStepBtn(classificationView);
    },

    /**
     * 分类管理功能
     * @param btn 分类管理功能按钮
     */
    classificationHandler:function (btn) {
    	btnInfo = btn;
    	acquisitionGrid = this.getGrid(btn);//获取到数据管理表单视图
        var tree = this.findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        if(!node){
            XD.msg('请选择节点');
            return;
        }
        var classificationWin = Ext.create('Ext.window.Window',{
            modal:true,
            width:'65%',
            height:'80%',
            title:'当前位置: 分类管理',
            layout:'fit',
            closeToolText:'关闭',
            closeAction:'hide',
            items:[{
                xtype: 'managementclassification'//加载分类设置视图
            }]
        });
        //弹出分类设置界面时刷新下拉框数据
        var dictionaryform = classificationWin.down('managementDictionaryView').getForm();
        dictionaryform.findField('filingyear').getStore().reload();
        dictionaryform.findField('entryretention').getStore().reload();
        dictionaryform.findField('organ').getStore().reload();
        //隐藏上一步和设置按钮
        var classificationView = classificationWin.down('managementclassification');
        classificationWin.show();
        var view = classificationView.down('managementClassificationGridView');
        view.initGrid({nodeid:node.get('fnid')});//先初始化表单信息
        this.hideFirstStepBtn(classificationView);
    },

    /**
     * 分类管理窗口，进行分类设置
     * @param btn
     */
    setHandler: function(btn) {
        var grid = this.findClassificationView(btn);
        var record = grid.selModel.getSelection();
        if (record.length < 1) {
            XD.msg('请选择一条需要分类设置的数据');
            return;
        }
        var tmp = [];
        for (var i = 0; i < record.length; i++) {
            tmp.push(record[i].get('entryid'));
        }
        var entryid = tmp.join(',');
        var view = this.findDictionaryView(btn);
        var year = view.getForm().findField('filingyear').getValue();
        if (year == null) {
        	XD.msg('请选择归档年度');
        	return;
        }
        var retention = view.getForm().findField('entryretention').getValue();
        if (retention == null) {
        	XD.msg('请选择保管期限');
        	return;
        }
        var organ = view.getForm().findField('organInfo').getValue();
        if (typeof(organ) == 'undefined' || organ == '') {
        	XD.msg('请选择相关机构');
        	return;
        }
        Ext.Ajax.request({
            method: 'post',
            params:{
                entryid:entryid,
                year:year,
                retention:retention,
                organ:organ
            },
            url: '/categoryDictionary/setCategory',
            success: function (response) {
            	btn.up('window').hide();//关闭分类设置窗口
                XD.msg(Ext.decode(response.responseText).msg);
                acquisitionGrid.initGrid();
            }
        });
    },

    /**
     * 分类管理窗口，进行分类自动设置
     * @param btn
     */
    autoSetHandler: function(btn) {
        var grid = this.findClassificationView(btn);
        var record = grid.selModel.getSelection();
        if (record.length < 1) {
            XD.msg('请选择一条需要分类自动设置的数据');
            return;
        }
        var tmp = [];
        for (var i = 0; i < record.length; i++) {
            tmp.push(record[i].get('entryid'));
        }
        var entryid = tmp.join(',');
        this.activeAutoSetGrid(btn, entryid);//切换到自动设置预览表单
        var view = this.findClassificationGrid(btn);
        this.hideSecondStepBtn(view);
    },

    /**
     *  切换到自动设置预览界面
     * @param btn
     * @param entryid
     */
    activeAutoSetGrid: function(btn, entryid) {
        var classificationView = this.findClassificationSencondView(btn);
        var grid = this.findClassificationGrid(btn);
        grid.setActiveItem(classificationView);
        var preGrid = this.findClassificationView(btn);
        var classificationGrid = this.findClassificationGridView(btn);
        var params={
            entryids:entryid,
            nodeid: preGrid.dataParams.nodeid,
            dataSource:'capture'
        };
        classificationGrid.initGrid(params);
    },

    /**
     * 分类自动设置窗口，设置功能
     * @param btn
     */
    toSetHandler: function(btn) {
        var grid = this.findClassificationView(btn);
        var record = grid.selModel.getSelection();
        if (record.length < 1) {
            XD.msg('请选择一条需要分类自动设置的数据');
            return;
        }
        var tmp = [];
        for (var i = 0; i < record.length; i++) {
            tmp.push(record[i].get('entryid'));
        }
        var entryid = tmp.join(',');
        Ext.Ajax.request({
            method: 'post',
            params: {
            	entryid :entryid,
            	type: '数据管理'
            },
            url: '/categoryDictionary/autoSetCategory',//只需要传入选中的数据id就行了
            success: function (response) {
                XD.msg(Ext.decode(response.responseText).msg);
                btn.up('window').hide();//关闭分类设置窗口
            },
            failure: function () {
            	XD.msg('操作中断');
            }
        });
        this.activeGrid(btnInfo, true);//刷新数据管理界面
    },

    //机构文本框显示选中的机构数据
    organHandler: function(combo, record) {
        var view = combo.findParentByType('managementDictionaryView');
        var organInfo = view.down('[name=organInfo]');
        var value = organInfo.getValue();
        if (value == null || value == '') {
            organInfo.setValue(combo.getValue());
        } else {
            var exist = false;
            var str = value.split("/");
            for(var i = 0; i < str.length; i++){
                if(str[i] == combo.getValue()){
                    exist = true;
                }
                if(exist){
                    organInfo.setValue(value);
                }else{
                    organInfo.setValue(value + "/" + combo.getValue());//那么用"/"将两个词进行分隔显示
                }
            }
        }
    },

    /**
     * 重置机构问题
     * @param form
     */
    resetOrganHandler: function(form) {
        var view = form.findParentByType('managementDictionaryView');
        view.down('[name=organ]').setValue('');//清空机构字段
        view.down('[name=organInfo]').setValue('');//清空机构信息字段
    },
    //分类设置 -------------- end

    //--------自选字段导出--s----//
    exportFunction:function(view, state){
        var userGridView = view.findParentByType('managementgrid');
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
        if(selectAll == false && entryids.length == 0){
            XD.msg('请至少选择一条需要导出的数据');
            return;
        }
        if(isSelectAll==true){
            tempParams['indexLength'] = gridStore.totalCount;
        }else {
            tempParams['indexLength'] = record.length;
        }
        var selectItem = Ext.create("Management.view.ManagementGroupSetView");
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
        var selectView = view.findParentByType('managementGroupSetView');
        var FieldCode = selectView.items.get(0).getValue();
        userFieldCode = FieldCode;
        var exporUrl = "";
        if (FieldCode.length>0) {
            var win = Ext.create("Management.view.ManagementMessageView");
            win.show();
        }else {
            XD.msg("请选择需要导出的字段");
        }
    },
    //----------导入 ---s--//
    importHandler: function (btn) {
        var win = Ext.create('Management.view.ImportView');
        win.show();
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
        var MissPageCheck = Ext.create('Management.view.ManagementMissPageCheck');
        var checkstore = MissPageCheck.down('managementMissPageDetailView').getStore();
        checkstore.proxy.extraParams.ids = ids;
        checkstore.proxy.extraParams= grid.getStore().proxy.extraParams;
        checkstore.proxy.extraParams.isSelectAll = isSelectAll;
        checkstore.reload();
        var numbertotal = [];
        numbertotal = this.getTotal(checkstore.proxy.extraParams);
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
        var lookMediaView = Ext.create('Management.view.ManagementLookMediaView');
        var tree = lookMediaView.down('missPageElectronicView');
        tree.initData(entryId);
        lookMediaView.show();
    },
    getTotal:function (params) {
        var number=[];
        Ext.Ajax.request({
            url: '/management/getMissPageCheckTotal',
            async:false,
            params:params,
            success: function (response) {
                number = Ext.decode(response.responseText);
            }
        });
        return number;
    },
    openExport:function (view) {
        var MissPageDetailView = view.findParentByType('managementMissPageDetailView');
        var select = MissPageDetailView.getSelectionModel().getSelection();
        if(select.length<1){
            XD.msg('请至少选择一条数据');
            return;
        }
        var ids = [];
        for(var i=0;i<select.length;i++){
            ids.push(select[i].get('id'));
        }
        var exportMissView = Ext.create('Management.view.ManagementExportMissView');
        exportMissView.ids =ids;
        exportMissView.show();
    },
    missExport:function (view) {
        var ExportMissView = view.findParentByType('managementExportMissView');
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
                url:'/export/missPageFieldExportManagement',
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
                    window.location.href="/export/downloadZipFile?fpath="+encodeURIComponent(obj.filePath);
                    Ext.MessageBox.hide();
                    XD.msg('文件生成成功，正在准备下载');
                    ExportMissView.close();
                },
                failure:function(){
                    Ext.MessageBox.hide();
                    XD.msg('文件生成失败');
                }
            });
        } else {
            XD.msg("文件名不能为空")
        }
    },
    //预归档的批量修改
    doFilingBatchModify:function (btn) {
        var filingView = this.findFilingView(btn);
        var filingGrid = this.findFilingGridView(btn);
        var records = filingGrid.getSelectionModel().getSelection();
        var selectCount = records.length;
        var allSelect = [];
        var tmp = [];
        if(selectCount==0){
            tmp = this.selectAllEntry(allSelect,tmp);
            XD.confirm("不勾选记录进行批量处理，将处理所有预归档条目",function () {
                var entryids = tmp.join(',');
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
                        resultgrid:filingView.acquisitiongrid,
                        filingtype:true,       //归档批量操作标识
                        filinggrid:filingGrid,
                        filingnodeid:window.ygdNodeid  //归档节点id
                    }]
                });
                var fieldModifyPreviewGrid = batchModifyModifyWin.down('grid');
                if(fieldModifyPreviewGrid.getStore().data.length>0){
                    fieldModifyPreviewGrid.getStore().removeAll();
                }
                batchModifyModifyWin.down("[itemId=getPreview]").setText("批量修改");
                batchModifyModifyWin.show();
            });
        }else{
            for(var i = 0; i < records.length; i++){
                tmp.push(records[i].get('entryid'));
            }
            var entryids = tmp.join(',');
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
                    resultgrid:filingView.acquisitiongrid,
                    filingtype:true,       //归档批量操作标识
                    filinggrid:filingGrid,
                    filingnodeid:window.ygdNodeid  //归档节点id
                }]
            });
            var fieldModifyPreviewGrid = batchModifyModifyWin.down('grid');
            if(fieldModifyPreviewGrid.getStore().data.length>0){
                fieldModifyPreviewGrid.getStore().removeAll();
            }
            batchModifyModifyWin.down("[itemId=getPreview]").setText("批量修改");
            batchModifyModifyWin.show();
        }
    },
    //预归档的批量替换
    doFilingBatchReplace:function (btn) {
        var filingView = this.findFilingView(btn);
        var filingGrid = this.findFilingGridView(btn);
        var records = filingGrid.getSelectionModel().getSelection();
        var selectCount = records.length;
        var allSelect = [];
        var tmp = [];
        if(selectCount==0){
            tmp = this.selectAllEntry(allSelect,tmp);
            XD.confirm("不勾选记录进行批量处理，将处理所有预归档条目",function () {
                var entryids = tmp.join(',');
                var batchModifyReplaceWin = Ext.create('Ext.window.Window', {
                    width: '100%',
                    height: '100%',
                    title: '批量替换',
                    // draggable : true,//可拖动
                    resizable: false,//禁止缩放
                    modal: true,
                    closeToolText: '关闭',
                    layout: 'fit',
                    items: [{
                        xtype: 'batchModifyReplaceFormView',
                        entryids: entryids,
                        resultgrid: filingView.acquisitiongrid,
                        filingtype: true,       //归档批量操作标识
                        filinggrid: filingGrid,
                        filingnodeid: window.ygdNodeid  //归档节点id
                    }]
                });
                batchModifyReplaceWin.down("[itemId=getPreview]").setText("批量替换");
                batchModifyReplaceWin.show();
            });
        }else{
            for(var i = 0; i < records.length; i++){
                tmp.push(records[i].get('entryid'));
            }
            var entryids = tmp.join(',');
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
                    resultgrid:filingView.acquisitiongrid,
                    filingtype:true,       //归档批量操作标识
                    filinggrid:filingGrid,
                    filingnodeid:window.ygdNodeid  //归档节点id
                }]
            });
            batchModifyReplaceWin.down("[itemId=getPreview]").setText("批量替换");
            batchModifyReplaceWin.show();
        }
    },
    //预归档的批量增加
    doFilingBatchAdd:function (btn) {
        var filingView = this.findFilingView(btn);
        var filingGrid = this.findFilingGridView(btn);
        var records = filingGrid.getSelectionModel().getSelection();
        var selectCount = records.length;
        var allSelect = [];
        var tmp = [];
        if(selectCount==0){
            tmp = this.selectAllEntry(allSelect,tmp);
            XD.confirm("不勾选记录进行批量处理，将处理所有预归档条目",function () {
                var entryids = tmp.join(',');
                var batchModifyAddWin = Ext.create('Ext.window.Window',{
                    width:'100%',
                    height:'100%',
                    title:'批量增加',
                    // draggable : true,//可拖动
                    // resizable : false,//禁止缩放
                    modal:true,
                    closeToolText:'关闭',
                    layout:'fit',
                    items:[{
                        xtype: 'batchModifyAddFormView',
                        entryids:entryids,
                        resultgrid:filingView.acquisitiongrid,
                        filingtype:true,       //归档批量操作标识
                        filinggrid:filingGrid,
                        filingnodeid:window.ygdNodeid  //归档节点id
                    }]
                });
                batchModifyAddWin.down("[itemId=getPreview]").setText("批量增加");
                batchModifyAddWin.show();
            });
        }else {
            for (var i = 0; i < records.length; i++) {
                tmp.push(records[i].get('entryid'));
            }
            var entryids = tmp.join(',');
            var batchModifyAddWin = Ext.create('Ext.window.Window', {
                width: '100%',
                height: '100%',
                title: '批量增加',
                // draggable : true,//可拖动
                // resizable : false,//禁止缩放
                modal: true,
                closeToolText: '关闭',
                layout: 'fit',
                items: [{
                    xtype: 'batchModifyAddFormView',
                    entryids: entryids,
                    resultgrid: filingView.acquisitiongrid,
                    filingtype: true,       //归档批量操作标识
                    filinggrid: filingGrid,
                    filingnodeid: window.ygdNodeid  //归档节点id
                }]
            });
            batchModifyAddWin.down("[itemId=getPreview]").setText("批量增加");
            batchModifyAddWin.show();
        }
    },

    selectAllEntry:function (allSelect,tmp) {
        Ext.Ajax.request({
            method:'POST',
            url: '/acquisition/getTempEntryids',
            params:{nodeid:window.filingGrid.dataParams.nodeid,type:'management'},
            async:false,
            scope:this,
            timeout:XD.timeout,
            success: function (response) {
                allSelect=Ext.decode(response.responseText).data;
            },
            failure:function () {
                XD.msg('操作失败');
            }
        });
        if(allSelect.length>5000){
            XD.msg('一次只能归档5千条档案数据，请勾选记录，分批归档。归档会进行档号重复验证，防止数据量过大时，导致速度过慢及操作中断异常。');
            return;
        }
        for (var i = 0; i < allSelect.length; i++) {
            tmp.push(allSelect[i].entryid);
        }
        return tmp;
    },
    metadataLogHandler:function(btn){
        var grid = this.getGrid(btn);
        var records;
        if(grid.selModel != null) {
            records = grid.selModel.getSelection();
        } else {
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
        formAndGridView = gridcard.up('managementFormAndGrid');
    },
    changeToList:function(btn){
        var _this=window._this;
        var treemodel = window.qhTreemodel;
        var record = window.qhRecord;
        this.listTab(_this,treemodel,record);
    },
    listTab:function(_this,treemodel,record){
        var gridcard = _this.findView(treemodel.view).down('[itemId=gridcard]');

        //声像档案节点显示缩略图按钮,非声像档案节点隐藏
        var toMediaBtn=gridcard.down('[itemId=toMediaBtn]');
        if(window.isMedia){
            toMediaBtn.show();
        }else{
            toMediaBtn.hide();
        }

        var onlygrid = gridcard.down('[itemId=onlygrid]');
        // onlygrid.show()
        var pairgrid = gridcard.down('[itemId=pairgrid]');
        var grid;
        var nodeType = record.data.nodeType;
        var bgSelectOrgan = gridcard.down('[itemId=bgSelectOrgan]');
        treepanelInfo = _this.findView(treemodel.view).down('treepanel');
        //树节点为分类，更改右边页面为“请选择机构节点”
        if (nodeType == 2) {
            gridcard.setActiveItem(bgSelectOrgan);
        } else {
            if (record.data.classlevel == 2) {
                gridcard.setActiveItem(pairgrid);
                var ajgrid = pairgrid.down('[itemId=northgrid]');
                ajgrid.setTitle("当前位置：" + record.data.text);
                var jngrid = pairgrid.down('[itemId=southgrid]');
                jngrid.setTitle("查看卷内");
                if (jngrid.expandOrcollapse == 'expand') {
                    jngrid.expand();
                } else {
                    jngrid.collapse();
                }
                _this.refreshToolbarBtnShowAll(jngrid); //显示所有按钮
                jngrid.dataUrl = '/management/entries/innerfile/' + '' + '/';
                jngrid.initGrid(_this.getNodeid(record.get('nodeid')));
                grid = ajgrid;
            } else {
                gridcard.setActiveItem(onlygrid);
                onlygrid.setTitle("当前位置：" + record.data.text);
                grid = onlygrid;
                window.jnNodeid="";//标记点击案卷的卷内标记nodeid为空
            }
            window.organTitle=record.data.text;//节点的机构信息

            var gridview = gridcard.up('managementFormAndGrid').down('formAndGrid').down('managementgrid');
            gridview.setTitle("当前位置：" + record.data.text);//将表单与表格视图标题改成当前位置

            _this.refreshToolbarBtn(record.data.classlevel, grid, gridview, record.get('fnid'));
            grid.nodeid = record.get('fnid');
            grid.initGrid({nodeid: record.get('fnid')});
            _this.refreshToolbarBtn(record.data.classlevel,grid,gridview);
            //---
            record.get('fnid');
            NodeIdf = record.get('fnid');
            var demoStore = Ext.getStore('ManagementGroupSetStore');
            demoStore.proxy.extraParams.fieldNodeid = record.get('fnid');
            //--
            var fullname = record.get('text');
            while (record.parentNode.get('text') != '数据管理') {
                fullname = record.parentNode.get('text') + '_' + fullname;
                record = record.parentNode;
                isManagement = false;
            }
            grid.nodefullname = fullname;
            grid.parentXtype = 'managementFormAndGrid';
            grid.formXtype = 'managementform';
            grid.winType='gllook';//日志用
        }
        formAndGridView = gridcard.up('managementFormAndGrid');
    },
    //显示所有按钮
    refreshToolbarBtnShowAll: function (grid) {
        //著录表单下方的grid按钮
        var btns = grid.down("toolbar").query('button');
        //著录表单下方的grid按钮间的分隔符
        var tbs = grid.down("toolbar").query('tbseparator');
        //显示所有
        this.showAll(btns);
        this.showAll(tbs);
    },
    //隐藏所有分隔符
    hideAll:function (hideType) {
        for(var num in hideType){
            hideType[num].hide();
        }
    },
    //显示所有
    showAll:function (hideType) {
        for(var num in hideType){
            hideType[num].show();
        }
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
    }

});

function backdemo(view){
    console.log(view)
}

function delTempByUniquetag() {
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
    params['type'] = '数据管理';
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
        method: 'post',
        params: params,
        sync : true,
        //timeout:XD.timeout,
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
            var managementgrid = resultPreviewGrid.formview.resultgrid;
            managementgrid.getStore().proxy.extraParams.entryidArr='';
            managementgrid.notResetInitGrid();
        },
        failure:function () {
            Ext.Msg.wait(resultPreviewGrid.operateFlag+'操作失败','正在操作').hide();
            XD.msg('更新失败');
        }
    })
}

function initFormField(form, operate, nodeid) {
    if (form.nodeid != nodeid) {//切换节点后，form和tree的节点id不相等
        form.nodeid = nodeid;//用左侧树节点的id初始化form的nodeid参数
        form.removeAll();//移除form中的所有表单控件
        var field = {
            xtype: 'hidden',
            name: 'entryid'
        };
        form.add(field);
        var formField = getFormFieldEdit(nodeid);//根据节点id查询表单字段
        if(formField.length==0){
            XD.msg('请检查档号设置信息是否正确');
            return;
        }
        form.templates = formField;
        form.editType='archiveEdit';
        form.initField(formField,operate);//重新动态添加表单控件
    }
    return '加载表单控件成功';
}

//获取可以进行档号编辑的表单字段
function getFormFieldEdit(nodeid) {
    var formField;
    Ext.Ajax.request({
        url: '/template/formEdit',
        async:false,
        params:{
            nodeid:nodeid
        },
        success: function (response) {
            formField = Ext.decode(response.responseText);
        }
    });
    return formField;
}

function getOrderTxt(nodeid) {
    var returnStr = [];
    var text='';
    Ext.Ajax.request({
        url: '/ordersetting/getOrderTxt',
        async:false,
        params:{
            nodeid:nodeid,
            type:'management'
        },
        success: function (response) {
            var respText = Ext.decode(response.responseText);
            if(""!=respText.msg){
                text=text+'当前归档顺序: '+ respText.msg;
            }
            returnStr.push(text);
            returnStr.push(respText.data);
        }
    });
    return returnStr;
}

function changeToMultiselect(variable, SelectedFormView, DetailFormView) {

    var boundlist = SelectedFormView.getComponent("itemselectorID").toField.boundList;
    var tostore = boundlist.store;
    var records = [];
    var hiddenvalue = DetailFormView.down('[itemId=hiddenfieldId]').getValue();
    if (tostore.getCount() > 0) {
        for (var i = 0; i < tostore.getCount(); i++) {
            var record = tostore.getAt(i);
            var num = tostore.indexOf(record);
            var temp = record.data.fieldcode.split('∪');
            if (hiddenvalue == temp[1] || hiddenvalue == temp[3]) {
                var changeValue = insertChange(variable.getValue(), record.data.fieldcode, variable.getName());
                var changeName=changeNameOrder(variable.getValue(), record.data.fieldcode);//修改已选字段显示
                record.data.fieldcode = changeValue;   //要改变提交到后台的值
                record.data.fieldname=changeName;
                records.push(record);
                tostore.remove(record);
                tostore.insert(num, records);
                records = [];
            }
        }
    }
}


function insertChange(str, changeValue, isSign) {
    var temp = changeValue.split("∪");
    var haveChange = temp[0] + "∪";
    if(temp[0]==''){
        temp[3] = str;
    }else{
        temp[2] = str;
    }
    for (var i = 1; i < temp.length; i++) {
        if (i != temp.length - 1)
            haveChange = haveChange + temp[i] + "∪";
        else
            haveChange = haveChange + temp[i];
    }
    return haveChange
}

function changeNameOrder(str,fieldcode) {
    var temp = fieldcode.split("∪");
    var fieldname="";
    if(temp[0]==''){//未选字段
        fieldname=temp[1]+"_"+temp[2];
    }else{//已选子段
        fieldname=temp[3]+"_"+temp[1];
    }
    if(str=='0'){//升序
        fieldname=fieldname+" ↓";
    }else{//降序
        fieldname=fieldname+" ↑";
    }
    return fieldname
}

function delTempByUniquetagType(type) {//清除本机当前用户关联的的临时条目数据
    Ext.Ajax.request({
        method: 'POST',
        params: {archiveType: type},
        url: '/acquisition/delTempByUniquetag',
        asych:false,
        success: function (response) {
        }
    });

}

/**
 *获取业务元数据
 * @param entryids 条目集合
 * @param module  模块名
 * @oaram operation 业务行为（著录..）
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

/*function getCodeSettingFields(nodeid){
    var codeSettingFieldList;//档号构成字段的集合
    Ext.Ajax.request({//获得档号构成字段的集合
        url:'/codesetting/getCodeSettingFields',
        async:false,
        params:{
            nodeid:nodeid
        },
        success:function(response){
            codeSettingFieldList = Ext.decode(response.responseText).data;
        }
    });
    return codeSettingFieldList;
}*/
