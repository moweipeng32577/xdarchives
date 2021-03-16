Ext.define('DigitalProcess.controller.DigitalProcessController', {
    extend: 'Ext.app.Controller',
    views: [
        'DigitalProcessView',
        'DigitalProcessTabView',
        'DigitalProcessLeftView',
        'DigitalProcessWqsGridView',
        'DigitalProcessYqsGridView',
        'DigitalProcessYwcGridView',
        'DigitalProcessWchjGridView',
        'DigitalProcessRecordForm',
        'TreeComboboxView',
        'EntryFormView',
        'EntryFormView1',
        'DealDetailsGridView',
        'DigitalProcessMergeView',
        'DigitalReportGridView',
        'DigitalProcessMessageRightView',
        'ElectronicView',
        'NodeLinkSetLinkSetView',
        'DigitalProcessAuditLeftUpView',
        'DigitalProcessAuditView',
        'ShUploadView'
    ],
    stores: [
        'DigitalProcessTreeStore',
        'DigitalProcessCalloutTreeStore',
        'DigitalProcessWqsGridStore',
        'DigitalProcessYqsGridStore',
        'DigitalProcessYwcGridStore',
        'DigitalProcessWchjGridStore',
        'AssemblyStore',
        'DealDetailsGridStore',
        'OperateUserStore',
        'DigitalReportStore',
        'ShlinkStore',
        'DigitalProcessMediaGridStore',
        'DigitalProcessAuditAuditGridStore'
    ],
    models: [
        'DigitalProcessTreeModel',
        'DigitalProcessWqsGridModel',
        'DigitalProcessYqsGridModel',
        'DigitalProcessYwcGridModel',
        'DigitalProcessWchjGridModel',
        'DealDetailsGridModel',
        'DigitalReportModel',
        'DigitalProcessMediaGridModel',
        'DigitalProcessAuditAuditGridModel'
    ],
    init: function () {
        var flowsSelId,flowsSelNode,batchcode,assemblyCode,p7,p5;
        window.xdSzhCache = {imgCache:{},scope:5,refreshCache:{}};//图片缓存对象：图片缓存对象,步长,需要刷新的缓存对象
        this.control({
            'DigitalProcessLeftView [itemId=treepanelId]':{//环节树选中
                select:this.flowsTreeSel
            },

            'DigitalProcessLeftView [itemId=treepanelCalloutId]':{//批次树选中
                select:this.calloutTreeSel
            },

            'DigitalProcessTabView':{
                tabchange:function(view){//选项卡状态切换
                    var gridStore = view.down('DigitalProcessWqsGridView').getStore();
                    if(view.activeTab.title == '已签收'){
                        var yqsGrid = view.down('DigitalProcessYqsGridView');
                        var recordBtn = yqsGrid.down('[itemId=record]');
                        var finishBtn =  yqsGrid.down('[itemId=finish]');
                        var lookBtn =  yqsGrid.down('[itemId=look]');
                        var auditBtn =  yqsGrid.down('[itemId=audit]');
                        auditBtn.hide();
                        if("著录"==this.flowsSelNode){
                            recordBtn.show();
                            lookBtn.show();
                       } else{
                            recordBtn.hide();
                            lookBtn.hide();
                            finishBtn.show();
                       }

                        if("图像处理"==this.flowsSelNode||"审核"==this.flowsSelNode){
                            lookBtn.show();
                            if("审核"==this.flowsSelNode){
                                auditBtn.show();
                            }
                        }
                       gridStore = view.down('DigitalProcessYqsGridView').getStore();
                    }else if(view.activeTab.title == '已处理'){
                        var ywcGrid = view.down('DigitalProcessYwcGridView');
                        gridStore = ywcGrid.getStore();
                        var printBtn =  ywcGrid.down('[itemId=print]');

                        if("审核"==this.flowsSelNode){

                        }
                        else if("著录"==this.flowsSelNode){
                            printBtn.show();
                        }
                        else if("属性定义" == this.flowsSelNode){
                            printBtn.show();
                        }
                        else{
                            printBtn.hide();
                        }
                    }
                    gridStore.proxy.extraParams.type = view.activeTab.title;
                    gridStore.proxy.extraParams.status = this.flowsSelNode;
                    gridStore.proxy.extraParams.batchcode = this.batchcode;
                    gridStore.reload();
                }
            },
            //对view添加表格行单击事件
            'DigitalProcessWqsGridView,DigitalProcessYqsGridView,DigitalProcessYwcGridView,DigitalProcessWchjGridView':{
                itemclick:this.itemclickGetMessage
            },

            'DigitalProcessWqsGridView [itemId=sign]':{//【未签收】签收---》已签收
                click:this.changeStatus
            },

            'DigitalProcessYqsGridView [itemId=finish]':{//【已签收】完成---》已处理
                click:this.changeStatus
            },

            'DigitalProcessYqsGridView [itemId=record]':{//【已签收】著录
                click:this.entryLook
            },

            'EntryFormView [itemId=preButton]':{//【已签收】著录 上一条
                click:this.preHandler
            },

            'EntryFormView [itemId=nextButton]':{//【已签收】著录  下一条
                click:this.nextHandler
            },

            'EntryFormView [itemId=save]':{//【已签收】著录保存
                click:this.entryFormSave
            },

            'EntryFormView [itemId=saveAndfinish]':{//【已签收】著录保存并完成！
                click:this.entryFormSaveAndFinish
            },

            'EntryFormView [itemId=back]':{//【已签收】著录退回
                click:function (btn) {
                    formView.close();
                }
            },

            'DigitalProcessWchjGridView [itemId=putStorage]':{//完成环节 入库选择节点
                click:this.putStorage
            },

            'DigitalProcessYwcGridView [itemId=print]':{//【已处理】打印报表
                click:this.printHandler
            },

            'DigitalReportGridView [itemId=back]':{ //报表列表返回至数据列表
                click:function (btn) {
                    btn.up('window').hide();
                }
            },

            'DigitalReportGridView [itemId=print]':{ //打印报表
                click:function (btn) {
                    var reportGrid = btn.findParentByType('DigitalReportGridView');
                    var records = reportGrid.getSelectionModel().getSelection();
                    if (records.length == 0) {
                        XD.msg('请选择需要打印的报表!');
                        return;
                    }
                    if(records[0].data.reportname == '卷内目录' || records[0].data.reportname == '卷内目录_套打'){
                        Ext.Ajax.request({
                            params: {
                                entryids:reportGrid.ids
                            },
                            url: '/digitalProcess/reportData',
                            method: 'POST',
                            sync: true,
                            success: function (resp) {
                                var param = {};
                                param['entryid'] = reportGrid.ids.join(",");
                                var filename = records[0].get('reportname');
                                XD.UReportPrint(null, filename,param);
                            },
                            failure: function() {

                            }
                        });
                    }
                    var param = {};
                    param['entryid'] = reportGrid.ids.join(",");
                    param['batchcode'] = reportGrid.batchcode;
                    var filename = records[0].get('reportname');
                    XD.UReportPrint(null, filename,param);
                }
            },

            'DigitalProcessYqsGridView [itemId=look]':{//【已处理】查看条目
                click:function(btn){
                    btn.isLook = true;
                    this.entryLook(btn);
                }
            },

            'DigitalProcessYqsGridView [itemId=audit]':{//【已签收】审核并整理
                click:this.entryAudit
            },

            'DigitalProcessRecordForm [itemId=submit]':{//【入库】提交打开著录界面

            },

            'DigitalProcessRecordForm [itemId=close]':{//【入库】返回
                click:function(btn){
                    btn.up('DigitalProcessRecordForm').close();
                }
            },

            'DigitalProcessWchjGridView [itemId=DealDetailsId]':{//【办理详情】完成环节
                click:this.dealDetails
            },

            'DigitalProcessWchjGridView [itemId=printReport]':{//【打印报表】完成环节
                click:this.printReport
            },

            // 'EntryFormView1':{
            //     afterrender:function(v){
            //         var mergeView = v.up('DigitalProcessMergeView');
            //         // this.p2 = new PhotoView({eleid:'p2',src:''});
            //         // this.p2.changeImg("/digitalProcess/showMedia?eleid="+mergeView.eleid,mergeView.filename);
            //         var operateUser = v.down('[itemId=operateUserId]');
            //         var s = operateUser.getStore();
            //         s.proxy.extraParams.calloutId = this.calloutId;
            //         s.load({
            //             scope: this,
            //             callback: function() {
            //                 if (s.getCount() > 0) {
            //                     operateUser.select(s.getAt(0));
            //                 }
            //             }
            //         });
            //         var that = this;
            //         operateUser.on('select',function(f,r){
            //             var form = v.down('dynamicform1');
            //             that.initFormData('add',form,mergeView.nodeid,{calloutId:mergeView.calloutid,userId:f.getValue()});
            //         });
            //     }
            // },

            'MergeGridView':{
                celldblclick:function (thisp,row,col,model) {//单元格双击修改表单对应值
                    var mergeView = thisp.up('DigitalProcessMergeView');
                    var form = mergeView.down('dynamicform1');
                    var dataIndex = thisp.getHeaderAtIndex(col).dataIndex;
                    var cellVal = model.get(dataIndex);
                    var fieldObject = form.getForm().getValues();
                    for(var key in fieldObject){
                        if(fieldObject.hasOwnProperty(key)){
                            if(key==dataIndex){
                                form.getForm().findField(key).setValue(cellVal);
                            }
                        }
                    }
                }
            },

            'MergeGridView [itemId=save]':{//【入库】保存
                click:this.mergeFormSave
            },

            'DigitalProcessAuditLeftUpView [itemId=backLink]':{//退回环节
                click:this.backLink
            },

            'NodeLinkSetLinkSetView [itemId=linksetSubmit]':{  //审核  退回环节提交
                click:this.linksetSubmit
            },

            'NodeLinkSetLinkSetView [itemId=linksetClose]':{ //审核  退回环节返回
                click:function(btn){
                    btn.up('NodeLinkSetLinkSetView').close();
                }
            },

            'MergeGridView [itemId=back]':{//【入库】返回
                click:function(btn){
                    btn.up('DigitalProcessMergeView').close();
                }
            },

            'ShUploadView [itemId=submit]':{  //审核 上传提交
                click:this.shUploadSubmit
            },

            'ShUploadView [itemId=close]':{   //审核 上传关闭
                click:function(btn){
                    btn.up('ShUploadView').close();
                }
            },

            'DigitalProcessAuditLeftUpView [itemId=detailEntryGridId]':{
                itemclick:this.entryClickHandler,
                render:this.leftEntryRender
            },

            'DigitalProcessAuditLeftUpView [itemId=detailMediaEntryGridId]':{
                itemclick:this.detailMediaEntryGrid,
                render:this.leftMediaRender
            },

            'DigitalProcessAuditLeftUpView [itemId=pre]':{  //审核 条目上一条
                click:this.detailPre
            },

            'DigitalProcessAuditLeftUpView [itemId=next]':{   //审核 条目下一条
                click:this.detailNext
            }
        });
    },

    //下一条目
    detailNext:function () {
        var leftgrid = p5['_back'].down('[itemId=detailEntryGridId]');
        var sel = leftgrid.getSelection()[0];
        var leftGridStore = leftgrid.getStore();
        var selIndex = leftGridStore.indexOf(sel);
        if(selIndex == leftGridStore.getCount() - 1){
            XD.msg('已经是最后的条目了');
            return;
        }
        var next = leftGridStore.getAt(selIndex+1);
        leftgrid.getSelectionModel().select(next);
        leftgrid.fireEvent('itemclick', leftgrid, next);
    },

    //上一条目
    detailPre:function () {
        var leftgrid = p5['_back'].down('[itemId=detailEntryGridId]');
        var sel = leftgrid.getSelection()[0];
        var leftGridStore = leftgrid.getStore();
        var selIndex = leftGridStore.indexOf(sel);
        if(selIndex == 0){
            XD.msg('已经是第一条条目了');
            return;
        }
        var pre = leftGridStore.getAt(selIndex-1);
        leftgrid.getSelectionModel().select(pre);
        leftgrid.fireEvent('itemclick', leftgrid, pre);
    },

    entryClickHandler:function(view, record, item, index, e){
        var auditView = view.up('DigitalProcessAuditView');
        var leftUpView = view.up('DigitalProcessAuditLeftUpView');
        var mediaView = leftUpView.down('[itemId=detailMediaEntryGridId]');
        var panel = leftUpView.down('[itemId=eleGridId]');
        var id = record.get('id');
        var nodeid = this.getNodeid(id);
        leftUpView.calloutId = id;
        auditView.calloutId = id;
        auditView.archivecode = record.get('archivecode');
        var entryid = this.getEntryid(id);
        mediaView.getStore().proxy.extraParams.entryid = entryid;
        mediaView.getStore().load(function () {
            var count = mediaView.getStore().getCount();
            panel.setTitle('原文信息（总数：'+count+'）');
        });
        var entryFormView = view.up('DigitalProcessAuditView').down('EntryFormView1');
        var form = entryFormView.down('dynamicform1');
        form.operate = 'look';
        form.entryids = [];
        form.entryid = entryid;
        this.initFormField(form, 'hide', nodeid);
        this.initFormData('look', form, entryid, record.get('id'));
        entryFormView.down('[itemId=preBtn]').hide();
        entryFormView.down('[itemId=nextBtn]').hide();
        window.xdSzhCache.imgCache = {};
        imgPreload(mediaView,0);
    },

    leftEntryRender:function(grid){
        grid.getStore().on('load',function(store){
            if(store.getCount() > 0){
                this.fireEvent('itemclick',this,store.getAt(0));
                this.getSelectionModel().select(0);
            }
        },grid);
    },

    detailMediaEntryGrid:function(view, record, item, index, e){
        var auditView = view.up('DigitalProcessAuditView');
        var metadataView = auditView.down('[itemId=metadataId]');
        //显示电子文件
        var eleid = record.get('mediaid');
        var filename = record.get('filename');
        auditView.mediaId = eleid;
        var isRefresh = xdSzhCache.refreshCache.hasOwnProperty(eleid);
        var refreshVal = xdSzhCache.refreshCache[eleid];
        if(window.refashflag){
            p5.changeImg('/digitalProcess/showMedia?eleid='+eleid+(isRefresh?'&r='+refreshVal:''+'&number='+Math.random()),filename);
            window.refashflag = false;
        }else{
            p5.changeImg('/digitalProcess/showMedia?eleid='+eleid+(isRefresh?'&r='+refreshVal:''),filename);
        }
        if(record.get('bz')=='未检'){
            Ext.Ajax.request({
                url: '/digitalProcess/changeAuditStatus',
                async:false,
                params:{
                    mediaid:eleid
                },
                success: function (response) {
                    var obj = Ext.decode(response.responseText);
                    if(obj.success==true){
                        record.set('bz','已检');
                    }
                }
            });
        }
        imgPreload(view,index);
    },

    leftMediaRender:function(grid){
        grid.getStore().on('load',function(store){
            if(store.getCount() > 0){
                this.fireEvent('itemclick',this,store.getAt(0));
                this.getSelectionModel().select(0);
                imgPreload(grid,0);
            }
        },grid);
    },

    //行点击显示日志相关信息
    itemclickGetMessage:function (view,record,item,index,e) {
        var calloutId=record.get('id');
        // var entryid=this.getEntryid(sfrid);
        var eview=view.up('DigitalProcessView').down('DigitalProcessMessageRightView');
        var rightview = eview.down('[itemId=treepanelMessageId]');//右边框view的itemId
        Ext.Ajax.request({
            params: {
                calloutId: calloutId
            },
            url: '/digitalProcess/getSzhFlowsRecordMessage',
            method: "post",
            success: function (response) {
                var messages = Ext.decode(response.responseText);
                var html='<span style="color: black;font-size: 15px;">当前案卷号:'+record.get('archivecode')+'</span></br></br>';
                html+='<span>实体最新消息：</span></br>';
                if(messages[0].length>0){
                    var etmessagecolor=messages[0][0][1]=='已签收'?'blue':'red';
                    var etstatus=messages[0][0][1]==null?'':messages[0][0][1];
                    var rename=messages[0][0][4]==null?'':'<span style="color: red;">备注信息：'+messages[0][0][4]+'</span></br>';
                    html+='<span style="color:'+etmessagecolor+ ';">' +messages[0][0][0]+"：" +etstatus+ '</span></br><span>' +'处理人：'+messages[0][0][2]+','+messages[0][0][3]+ '</span></br>'+rename;
                }

                html+='</br><span>各环节最新消息：</span></br>';
                var flags=[true,true,true,true,true,true,true,true];
                for(var i=0;i<messages[1].length;i++){
                    var color=returncolor(messages[1][i][1]);
                    var replace=messages[1][i][5]==null?'':'<span style="color: red;">（代替：'+messages[1][i][5]+'）</span>';
                    switch(messages[1][i].nodename){
                        case "整理":
                            if(flags[0]){
                                html+='整理：'+'<span style="color:'+color+ ';">' +messages[1][i].status+ '</span></br><span>' +'处理人：'+messages[1][i].operator+replace+','+messages[1][i].operatetime+ '</span></br>';
                                var error=errorMessage(messages[2],'整理');
                                html+=error+'</br>';
                            }
                            flags[0]=false;
                            break;
                        case "扫描":
                            if(flags[1]){
                                html+='扫描：'+'<span style="color:'+color+ ';">' +messages[1][i].status+ '</span></br><span>' +'处理人：'+messages[1][i].operator+replace+'，'+messages[1][i].operatetime+ '</span></br>';
                                var error=errorMessage(messages[2],'扫描');
                                html+=error+'</br>';
                            }
                            flags[1]=false;
                            break;
                        case "图像处理":
                            if(flags[2]){
                                html+='图像处理：'+'<span style="color:'+color+ ';">' +messages[1][i].status+ '</span></br><span>' +'处理人：'+messages[1][i].operator+replace+','+messages[1][i].operatetime+ '</span></br>';
                                var error=errorMessage(messages[2],'图像处理');
                                html+=error+'</br>';
                            }
                            flags[2]=false;
                            break;
                        case "著录":
                            if(flags[3]){
                                html+='著录：'+'<span style="color:'+color+ ';">' +messages[1][i].status+ '</span></br><span>' +'处理人：'+messages[1][i].operator+replace+','+messages[1][i].operatetime+ '</span></br>';
                                var error=errorMessage(messages[2],'著录');
                                html+=error+'</br>';
                            }
                            flags[3]=false;
                            break;
                        case "属性定义":
                            if(flags[4]){
                                html+='属性定义：'+'<span style="color:'+color+ ';">' +messages[1][i].status+ '</span></br><span>' +'处理人：'+messages[1][i].operator+replace+','+messages[1][i].operatetime+ '</span></br>';
                                var error=errorMessage(messages[2],'属性定义');
                                html+=error+'</br>';
                            }
                            flags[4]=false;
                            break;
                        case "审核":
                            if(flags[5]){
                                html+='审核：'+'<span style="color:'+color+ ';">' +messages[1][i].status+ '</span></br><span>' +'处理人：'+messages[1][i].operator+replace+'，'+messages[1][i].operatetime+ '</span></br>';
                                var error=errorMessage(messages[2],'审核');
                                html+=error+'</br>';
                            }
                            flags[5]=false;
                            break;
                        case "装订":
                            if(flags[6]){
                                html+='装订：'+'<span style="color:'+color+ ';">' +messages[1][i].status+ '</span></br><span>' +'处理人：'+messages[1][i].operator+replace+','+messages[1][i].operatetime+ '</span></br>';
                                var error=errorMessage(messages[2],'装订');
                                html+=error+'</br>';
                            }
                            flags[6]=false;
                            break;
                        case "完成环节":
                            if(flags[7]){
                                html+='完成环节：'+'<span style="color:'+color+ ';">' +messages[1][i].status+ '</span></br><span>' +'处理人：'+messages[1][i].operator+replace+','+messages[1][i].operatetime+ '</span></br>';
                                var error=errorMessage(messages[2],'完成环节');
                                html+=error+'</br>';
                            }
                            flags[7]=false;
                            break;
                    }
                }
                rightview.setHtml(html);
            },
            failure: function () {
                XD.msg('相关日志信息出错！');
            },
        });
    },

    mergeFormSave:function (btn) {
        var that = this;
        var mergeView = btn.up('DigitalProcessMergeView');
        var form = mergeView.down('EntryFormView1').down('dynamicform1');
        //字段编号，用于特殊的自定义字段(范围型日期)
        var fieldCode = form.getRangeDateForCode();
        var params = {
            type: form.operate,
            nodeid:mergeView.nodeid
        };
        if (fieldCode != null) {
            params[fieldCode] = form.getDaterangeValue();
        }
        form.submit({
            method: 'POST',
            url: '/digitalProcess/mergeEntries',
            params: params,
            scope: this,
            success: function (form, action) {
                XD.msg(action.result.msg);
                mergeView.close();
            },
            failure: function (form, action) {
                XD.msg(action.result.msg);
            }
        });
    },

    backLink:function(btn){      //退回环节
        var digitalProcessMergeView = btn.up('DigitalProcessAuditView');
        var record = digitalProcessMergeView.selrecord;
        var ids = [record[0].get('id')];
        var dhs= [record[0].get('archivecode')];
        var assemblyCombobox = digitalProcessMergeView.grid.up('DigitalProcessView').down('[itemId=assemblyBoxId]');
        var nodeSetView = Ext.create('DigitalProcess.view.NodeLinkSetLinkSetView',{
            ids:ids,
            dhs:dhs,
            isSh:true,
            counter:0,
            assemblyCombobox:assemblyCombobox
        });
        nodeSetView.show();
        nodeSetView.down('form').down('[itemId = recordId]').setText('当前第'+1+'条记录('+record[0].get('archivecode')+')， 共'+1+'条记录。');
        var store = nodeSetView.down('[itemId=shLinkId]').getStore();
        store.proxy.extraParams.assemblyid = assemblyCombobox.assemblyid;
        store.reload();
    },

    linksetSubmit:function(btn){
        var that = this;
        var NodeLinkSetLinkSetView = btn.up('NodeLinkSetLinkSetView');
        var form = NodeLinkSetLinkSetView.down('form');
        var backText = form.down('[itemId = backText]').getValue();
        var linkName = form.getForm().findField('link').getDisplayValue();
        if(form.isValid()){
            var ids = NodeLinkSetLinkSetView.ids;
            var dhs = NodeLinkSetLinkSetView.dhs;
            var counter = NodeLinkSetLinkSetView.counter;
            form.submit({
                url: '/digitalProcess/linkback',
                method: 'POST',
                params:{
                    ids:ids,
                    assemblyid:NodeLinkSetLinkSetView.assemblyCombobox.assemblyid,
                    linkName:linkName,
                    relateLink:form.down('[itemId=relevancyId]').text,
                    backText:that.flowsSelNode
                },
                success: function (form, action) {
                    XD.msg('修改成功');
                    var entryGrid = p5['_back'].down('[itemId=detailEntryGridId]');
                    var mediaGrid = p5['_back'].down('[itemId=detailMediaEntryGridId]');
                    entryGrid.getStore().reload();
                    mediaGrid.getStore().proxy.extraParams.entryid = null;
                    mediaGrid.getStore().reload();
                    NodeLinkSetLinkSetView.close();
                },
                failure: function (form, action) {
                    XD.msg('操作失败');
                }
            });
        }
    },

    flowsTreeSel:function(view, record) {
        var tabView = view.up('DigitalProcessView').down('DigitalProcessTabView');
        var leftView = view.up('DigitalProcessLeftView');
        var calloutTreeView = leftView.down('[itemId=treepanelCalloutId]');
        var assemblyValue = leftView.down('combobox').getValue();//获取流水线批编号
        var nodeid = record.get('fnid');//获取节点id
        var nodename = record.get('text');
        calloutTreeView.setTitle(nodename);
        this.flowsSelId = nodeid;
        this.flowsSelNode = nodename;
        this.assemblyCode = assemblyValue;
        calloutTreeView.getStore().proxy.extraParams.batchcode = assemblyValue;
        calloutTreeView.getStore().reload();
        var card = leftView.up('DigitalProcessView').down('[itemId=rightCard]');
        tabView.setActiveTab(0);
        if(nodename=='完成环节'){
            card.setActiveItem(1);
            var finishGridStore = leftView.up('DigitalProcessView').down('DigitalProcessWchjGridView').getStore();
            finishGridStore .proxy.extraParams.batchcode = '*';
            finishGridStore.reload();
        }else{
            card.setActiveItem(0);
            var wqsGridStore = leftView.up('DigitalProcessView').down('DigitalProcessWqsGridView').getStore();
            wqsGridStore.proxy.extraParams.batchcode = '*';
            wqsGridStore.reload();
        }
    },

    calloutTreeSel:function(treemodel, record) {
        var tabView = this.findByTabView(treemodel.view);
        var wqsGridStore = tabView.down('DigitalProcessWqsGridView').getStore();
        tabView.setActiveTab(0);
        this.batchcode = record.get('fnid');
        if('完成环节'==this.flowsSelNode){
            var finishGridStore = tabView.up('DigitalProcessView').down('DigitalProcessWchjGridView').getStore();
            finishGridStore.proxy.extraParams.type = null;
            finishGridStore.proxy.extraParams.status = null;
            finishGridStore.proxy.extraParams.batchcode = record.get('fnid');
            finishGridStore.reload();
        }else{
            var assemblyid = tabView.up('DigitalProcessView').down('[itemId=assemblyBoxId]').assemblyid;
            wqsGridStore.proxy.extraParams.assemblyid = assemblyid;
            wqsGridStore.proxy.extraParams.type = tabView.activeTab.title;
            wqsGridStore.proxy.extraParams.batchcode = record.get('fnid');
            wqsGridStore.proxy.extraParams.status = this.flowsSelNode;
            wqsGridStore.proxy.extraParams.flownodeid = this.flowsSelId;
            wqsGridStore.reload();
        }
    },

    changeStatus:function (btn) {
        var that = this;
        var tabView = btn.up('DigitalProcessTabView');
        var grid = btn.up('DigitalProcessWqsGridView')||btn.up('DigitalProcessYqsGridView');
        var select = grid.getSelectionModel();
        if (select.getSelected().length<1) {
            XD.msg('至少选择一条数据');
            return;
        }
        var record = select.selected.items;
        var ids = [];
        for (var i = 0; i < record.length; i++) {
            ids.push(record[i].get('id'));
        }
        var assemblyid = btn.up('DigitalProcessView').down('[itemId=assemblyBoxId]').assemblyid;
        XD.confirm('确定要处理这' + ids.length + '条数据吗',function(){
            that.changeStatusAction(ids,that.flowsSelNode,tabView.activeTab.title,grid,assemblyid);
        },this);
    },

    changeStatusAction:function(ids,node,status,grid,assemblyid,callback){
        Ext.Ajax.request({
            params: {'ids': ids,'node':node,'status':status,'assemblyid':assemblyid},
            url: '/digitalProcess/calloutSign',
            method: 'POST',
            sync: true,
            success: function (resp) {
                var respText = Ext.decode(resp.responseText);
                if (respText.success == true) {
                    if(grid){
                        grid.getStore().reload();
                    }
                    if(callback){
                        callback();
                    }
                    XD.msg("操作成功");
                }else{
                    XD.msg("操作失败");
                }
            },
            failure: function() {
                XD.msg('操作失败');
            }
        });
    },

    record:function(btn){
        var grid = btn.up('DigitalProcessYqsGridView');
        var select = grid.getSelectionModel();
        if (select.getSelected().length!=1) {
            XD.msg('请选择一条数据');
            return;
        }
        var record = select.selected.items;
        var id = record[0].get('id');
        var dh = record[0].get('archivecode');
        var formView = Ext.create("DigitalProcess.view.DigitalProcessRecordForm",{calloutId:id,archivecode:dh,recordBtn:btn});
        formView.show();
    },

    entryAudit: function (btn) {
        var digitalProcessYqsGridView = btn.up('DigitalProcessYqsGridView');
        var select = digitalProcessYqsGridView.getSelectionModel();
        if (select.getSelected().length == 0) {
            XD.msg('请至少选择一条数据');
            return;
        }
        var record = select.selected.items;
        var ids = [];
        for (var i = 0; i < record.length; i++) {
            ids.push(record[i].get('id'));
        }
        var nodeid = this.getNodeid(record[0].get('id'));
        var entryid = this.getEntryid(record[0].get('id'));
        var that = this;
        this.calloutId = record[0].get('id');
        // Ext.Ajax.request({
        //     params: {'calloutId': record[0].get('id')},
        //     url: '/digitalProcess/getEntryidNodeid',
        //     method: 'POST',
        //     sync: true,
        //     success: function (resp) {
        //         var respText = Ext.decode(resp.responseText);
        //         if (respText.success) {
        //             var entryid = respText.data[1];
        //             var eleid = respText.data[2];
        //             var filename = respText.data[3];
        //             var nodeid = respText.data[0];
        var mergeView = window.AuitformView;
        if(!mergeView) {
            mergeView = Ext.create('DigitalProcess.view.DigitalProcessAuditView', {
                nodeid: nodeid,
                entryid: entryid,
                calloutid: record[0].get('id'),
                selrecord: record,
                grid: digitalProcessYqsGridView
            });
        }
            /////////////////初始化表单////////////////////////
            var entryFormView = mergeView.down('EntryFormView1');
            var form = entryFormView.down('dynamicform1');
            form.operate = 'look';
            form.entryids = [];
            form.entryid = entryid;
            that.initFormField(form, 'hide', nodeid);
            that.initFormData('look', form, entryid, record[0].get('id'));
            entryFormView.down('[itemId=preBtn]').hide();
            entryFormView.down('[itemId=nextBtn]').hide();
            mergeView.show(null, function () {
                var leftUpGrid = mergeView.down('[itemId=detailEntryGridId]');
                leftUpGrid.getStore().proxy.extraParams.ids = ids;
                leftUpGrid.getStore().reload();
                p5 = new PhotoView({
                    eleid: 'p5', isCommon: true, outer: {
                        next: {//下一条
                            method: this.photoNext,
                            attr: that
                        },
                        previous: {//上一条
                            method: this.photoPrevious
                        },
                        pass: {//通过
                            method: this.photoPass,
                            attr: digitalProcessYqsGridView
                        },
                        senior: {//下载
                            method: this.photoSenior
                        },
                        exitda: {//上传
                            method: this.photoExitda
                        },
                        back: {//返回
                            method: this.photoBack,
                            attr: mergeView
                        }
                    }
                });
                p5.assemblyid = btn.up('DigitalProcessView').down('[itemId=assemblyBoxId]').assemblyid;
                p5.that = that;
                var photoView = document.querySelector('#p5');
                photoView.querySelector('[eval=senior]').innerText = '↓下载';
                photoView.querySelector('[eval=exitda]').innerText = '↑上传';
            }, this);
        window.AuitformView = mergeView;
        Ext.on('resize', function (a, b) {
            window.AuitformView.setPosition(0, 0);
            window.AuitformView.fitContainer();
        });
        //         }else{
        //             XD.msg(respText.msg);
        //         }
        //     },
        //     failure: function() {
        //         XD.msg('获取模板失败');
        //     }
        // });

    },

    shUploadSubmit:function(btn){   //审核 上传提交
        var mediaId = p5['_back'].mediaId;
        var shUploadView = btn.up('ShUploadView');
        var form = shUploadView.down('form');
        if(form.isValid()){
            form.getForm().submit({
                url: '/digitalProcess/upload',
                waitTitle: '提示',
                waitMsg: '请稍后,正在上传...',
                scope: this,
                params: {
                    eleid: mediaId
                },
                success: function (basic, action) {
                    var result = action.result;
                    if(result.success){
                        p5.changeImg('/digitalProcess/showMedia?eleid='+mediaId+'&number'+Math.random(),result.filename);
                        shUploadView.close();
                    }
                    var msg = result.msg;
                    XD.msg(msg);
                },
                failure: function (form, action) {
                    var msg = action.result.msg;
                    XD.msg(msg);
                }
            });
        }
    },

    entryLook:function(btn){
        var digitalProcessYqsGridView = btn.up('DigitalProcessYqsGridView');
        var select = digitalProcessYqsGridView.getSelectionModel();
        if(select.getSelected().length == 0){
            XD.msg('请至少选择一条数据');
            return;
        }
        var record = select.selected.items;
        var calloutId = record[0].get('id');
        var nodeid = this.getNodeid(calloutId);
        var entryid = this.getEntryid(calloutId);
        var entryids = [];
        for(var i=0;i<record.length;i++){
            entryids.push(this.getEntryid(record[i].get('id')));
        }
        var formView = window.formView;
        if(!formView){
            formView = Ext.create("Ext.window.Window",{
                width:'100%',
                height:'100%',
                header: false,
                closeAction:'hide',
                layout:'fit',
                calloutId:calloutId,
                gridview:digitalProcessYqsGridView,
                nodeId:nodeid,
                items:[
                    {
                        xtype:'EntryFormView'
                    }
                ]});
        }else{
            p7.changeImg(null);
        }
        window.formView = formView;
        var entryFormView = formView.down('EntryFormView');
        var form = entryFormView.down('Dpdynamicform');
        var formele = entryFormView.down('electronicDp');


        form.entryids = entryids;
        form.entryid =entryid;
        form.nodeid = nodeid;
        this.initFormField(form, 'hide', nodeid);
        if(btn.isLook){
            this.initFormData('look',form,entryid);
            form.operate = 'look';
        }else{
            this.initFormData('modify',form,entryid);
            form.operate = 'modify';
        }
        entryFormView.down('[itemId=totalText]').hide();
        entryFormView.down('[itemId=nowText]').hide();
        entryFormView.down('[itemId=preBtn]').hide();
        entryFormView.down('[itemId=nextBtn]').hide();
        formView.show();
        p7 = new PhotoView({eleid:'p7'});
        var treepanel = formView.down('treepanel');
        if(btn.isLook){
            entryFormView.down('[itemId=save]').hide();
            entryFormView.down('[itemId=saveAndfinish]').hide();
        }else{
            entryFormView.down('[itemId=save]').show();
            entryFormView.down('[itemId=saveAndfinish]').show();
        }
        Ext.on('resize',function(a,b){
            window.formView.setPosition(0, 0);
            window.formView.fitContainer();
        });
    },


    // selNodeSubmit:function(btn){
    //     var digitalProcessYqsGridView = btn.up('DigitalProcessYqsGridView');
    //     var select = digitalProcessYqsGridView.getSelectionModel();
    //     if(select.getSelected().length!=1){
    //         XD.msg('只能选择一条数据');
    //         return;
    //     }
    //     var record = select.selected.items;
    //     var calloutId = record[0].get('id');
    //     var nodeid = this.getNodeid(calloutId);
    //     var entryid = this.getEntryid(calloutId);
    //     var formView = Ext.create("Ext.window.Window",{
    //         width:'100%',
    //         height:'100%',
    //         header: false,
    //         layout:'fit',
    //         calloutId:calloutId,
    //         gridview:digitalProcessYqsGridView,
    //         nodeId:nodeid,
    //         items:[
    //             {
    //                 xtype:'EntryFormView'
    //             }
    //         ]});
    //     var entryFormView = formView.down('EntryFormView');
    //     var form = entryFormView.down('dynamicform');
    //     form.operate = 'add';
    //     form.entryids = [];
    //     form.entryid =entryid;
    //     this.initFormField(form, 'hide', nodeid);
    //     this.initFormData('add',form,entryid);
    //     entryFormView.down('[itemId=preBtn]').hide();
    //     entryFormView.down('[itemId=nextBtn]').hide();
    //     entryFormView.down('electronic').setDisabled(true);
    //     formView.show();
    //     window.formView = formView;
    //     Ext.on('resize',function(a,b){
    //         window.formView.setPosition(0, 0);
    //         window.formView.fitContainer();
    //     });
    // },

    initFormField:function(form, operate, nodeid){
//        if(form.nodeid!=nodeid){
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

    //点击上一条
    preHandler:function(btn){
        var entryFormView = btn.up('EntryFormView');
        var form = entryFormView.down('Dpdynamicform');
        this.preNextHandler(form, 'pre');
    },

    //点击下一条
    nextHandler:function(btn){
        var entryFormView = btn.up('EntryFormView');
        var form = entryFormView.down('Dpdynamicform');
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
                var params={
                    nodeid: formview.nodeid,
                    type: form.operate,
                };
                var fieldCode = formview.getRangeDateForCode();//字段编号，用于特殊的自定义字段(范围型日期)
                if (fieldCode != null) {
                    params[fieldCode] = formview.getDaterangeValue();
                }
                Ext.MessageBox.wait('正在保存请稍后...', '提示');
                formview.submit({
                    method: 'POST',
                    url: '/digitalProcess/entries',
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

    refreshFormData:function(form, type,finishtype){
        p7.changeImg(null);
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
        if(finishtype){
            for(var i=0;i<entryids.length;i++){
                if(entryids[i] == currentEntryid){
                    entryids.splice(i,1);
                    break;
                }
            }
            form.entryids = entryids;
        }
        form.entryid = entryid;
        if(form.operate != 'undefined'){
            this.initFormData(form.operate, form, entryid);
            return;
        }
        this.initFormData('modify', form, entryid);
    },


    initFormData:function (operate, form, entryid) {
        var formview;
        if(form.up('EntryFormView')){
            formview = form.up('EntryFormView');
        }else{
            formview = form.up('EntryFormView1');
        }
        var nullvalue = new Ext.data.Model();
        var fields = form.getForm().getFields().items;
        var count ;
        for (var i = 0; i < form.entryids.length; i++) {
            if (form.entryids[i] == entryid) {
                count = i + 1;
                break;
            }
        }
        if(form.up('EntryFormView')) {
            var total = form.entryids.length;
            var totaltext = formview.down('[itemId=totalTextnew]');
            totaltext.setText('共有  ' + total + '  条，');
            var nowtext = formview.down('[itemId=nowTextnew]');
            nowtext.setText('当前是第  ' + count + '  条');
        }
        if(operate == 'look') {
            Ext.each(fields,function (item) {
                if(!item.freadOnly){
                    item.setReadOnly(true);
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
        // var etips = formview.down('[itemId=etips]');
        // etips.show();
        Ext.Ajax.request({
            method:'GET',
            scope:this,
            params:{
                entryid:entryid
            },
            url:'/digitalProcess/getEntryIndex/',
            success:function(response){
                if(operate!='look'){
                    var settingState = this.ifSettingCorrect(form.nodeid,form.templates);
                    if(!settingState){
                        return;
                    }
                }
                var entry = Ext.decode(response.responseText);
                form.loadRecord({getData:function(){return entry;}});
                //字段编号，用于特殊的自定义字段(范围型日期)
                var fieldCode = form.getRangeDateForCode();
                if(fieldCode!=null){
                    //动态解析数据库日期范围数据并加载至两个datefield中
                    form.initDaterangeContent(entry);
                }
                // //初始化原文数据
                var eleview = formview.down('electronic');
                if(eleview){
                    eleview.initData(entryid);
                }
            }
        });
    },

    putStorage:function(btn){
        var grid = btn.up('DigitalProcessWchjGridView');
        var select = grid.getSelectionModel();
        if (select.getSelected().length<1) {
            XD.msg('至少选择一条数据');
            return;
        }
        var record = select.selected.items;
        var ids = [];
        for (var i = 0; i < record.length; i++) {
            ids.push(record[i].get('id'));
        }

        Ext.Ajax.request({
            params: {'ids': ids},
            url: '/digitalProcess/putStorage',
            method: 'POST',
            sync: true,
            success: function (resp) {
                var respText = Ext.decode(resp.responseText);
                if (respText.success == true) {
                    grid.getStore().reload();
                    XD.msg("操作成功");
                }else{
                    XD.msg("操作失败");
                }
            },
            failure: function() {
                XD.msg('操作失败');
            }
        });
    },

    entryFormSave:function (btn) {
        var that = this;
        var tabView = formView.gridview.up('DigitalProcessTabView');
        var grid = tabView.down('DigitalProcessYqsGridView');
        // var eleids = btn.findParentByType('EntryFormView').down('electronic').getEleids();
        var form = btn.findParentByType('EntryFormView').down('Dpdynamicform');
        //字段编号，用于特殊的自定义字段(范围型日期)
        var fieldCode = form.getRangeDateForCode();
        // var nodename = this.getNodename(form.nodeid);
        form.getForm().findField('entryid').setValue(form.entryid);
        var params = {
            type: form.operate,
            nodeid:formView.nodeId
        };
        if (fieldCode != null) {
            params[fieldCode] = form.getDaterangeValue();
        }
        var entryids = form.entryids;
        var formdy = form;
        if(!form.isValid()){
            XD.msg("存在必填项未填写");
            return;
        }
        // var archivecodeSetState = form.setArchivecodeValueWithNode(nodename);
        // if (!archivecodeSetState) {//若档号设置失败，则停止后续的表单提交
        //     return;
        // }
        form.submit({
            method: 'POST',
            url: '/digitalProcess/entries',
            params: params,
            scope: this,
            success: function (form, action) {
                XD.msg(action.result.msg);
                if(entryids.length>1){
                    this.refreshFormData(formdy,"next");
                }else{
                    grid.getStore().reload();
                    formView.close();
                }
                // that.changeStatusAction([formView.calloutId],that.flowsSelNode,tabView.activeTab.title,grid);
            },
            failure: function (form, action) {
                XD.msg('保存失败，未归档或档号重复!');
            }
        });
    },

    //保存并完成
    entryFormSaveAndFinish:function (btn) {
        var that = this;
        var tabView = formView.gridview.up('DigitalProcessTabView');
        var grid = tabView.down('DigitalProcessYqsGridView');
        var form = btn.findParentByType('EntryFormView').down('Dpdynamicform');
        //字段编号，用于特殊的自定义字段(范围型日期)
        var fieldCode = form.getRangeDateForCode();
        form.getForm().findField('entryid').setValue(form.entryid);
        var params = {
            type: form.operate,
            nodeid:formView.nodeId
        };
        if (fieldCode != null) {
            params[fieldCode] = form.getDaterangeValue();
        }
        if(!form.isValid()){
            XD.msg("存在必填项未填写");
            return;
        }
        var entryId = form.entryid
        var entryids = form.entryids;
        var formdy = form;
        var calloutId=this.getCalloutid(entryId);//formView.calloutId未曾改变，通过entryid找回calloutid实现完成
        form.submit({
            method: 'POST',
            url: '/digitalProcess/entries',
            params: params,
            scope: this,
            success: function (form, action) {
                that.changeStatusAction(calloutId,that.flowsSelNode,tabView.activeTab.title,grid);
                if(entryids.length>1){
                    this.refreshFormData(formdy,"next","finish");
                }else{
                    formView.close();
                }
            },
            failure: function (form, action) {
                XD.msg('保存失败，操作未完成!');
            }
        });
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

    dealDetails:function (btn) {
        var grid = btn.up('DigitalProcessWchjGridView');
        var select = grid.getSelectionModel();
        if (select.getSelected().length!=1) {
            XD.msg('请选择一条数据');
            return;
        }
        var record = select.selected.items;
        var id = record[0].get('id')

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
        store.proxy.extraParams.id = id;
        store.proxy.extraParams.batchcode = this.batchcode;
        store.reload();
        dealDetailsWin.show();
    },

    findByTabView:function(view){
        return  view.up('DigitalProcessView').down('DigitalProcessTabView');
    },

    getNodeid:function (calloutId) {
        var nodeid;
        Ext.Ajax.request({
            url: '/digitalProcess/getNodeid',
            async:false,
            params:{
                calloutId:calloutId
            },
            success: function (response) {
                nodeid = Ext.decode(response.responseText);
            }
        });
        return nodeid;
    },

    getEntryid:function (calloutId) {
        var entryid;
        Ext.Ajax.request({
            url: '/digitalProcess/getEntryid',
            async:false,
            params:{
                calloutId:calloutId
            },
            success: function (response) {
                entryid = Ext.decode(response.responseText);
            }
        });
        return entryid;
    },

    printReport:function(btn){
            var grid = btn.up('DigitalProcessWchjGridView');
            var record = grid.getStore().getAt(0);
            var batchcode = record.get('batchcode');

            var url = '/ureport/preview?batchcode='+batchcode+'&_u=file:tj.ureport.xml&_t=1,4,5,6,7';
            var win = Ext.create('Ext.window.Window',{
                title:'统计报表',
                width:900,
                height:500,
                closeToolText:'关闭',
                html:'<iframe src="' + url + '" frameborder="0" style="width: 100%;height: 100%"></iframe>',
                buttons:[{
                    text:'关闭',
                    handler:function(){
                        win.close();
                    }
                }]
            });
            win.show();
    },

    printHandler:function(btn){
        var girdView;
        if(this.flowsSelNode =='完成环节')
        {
            girdView = btn.up('DigitalProcessWchjGridView')
        }
        else
        {
            girdView = btn.up('DigitalProcessYwcGridView');
        }
        var select = girdView.getSelectionModel();
        if(!this.batchcode){
            XD.msg('请选择批次');
            return;
        }
        if(select.getSelected().length < 1){
            XD.msg('请选择需要打印的数据');
            return;
        }
        var ids = [];
        for(var i = 0;i<select.getSelected().length;i++){
            var record = select.selected.items;
            var calloutId = record[i].get('id');
            var batchcode = record[0].get('batchcode');
            var entryid = this.getEntryid(calloutId);
            ids.push(entryid);
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
                xtype: 'DigitalReportGridView',
                ids:ids,
                batchcode:batchcode
            }]
        });
        var reportgrid = reportGridWin.down('DigitalReportGridView');
        var store = reportgrid.getStore();
        //如果不清空，将在上次过滤的基础上再次过滤
        store.clearFilter(true);
        if(this.flowsSelNode == '著录')
        {
            store.filterBy(function(record){
                return record.get('reportid') == '1';
            })
        }
        if(this.flowsSelNode == '属性定义')
        {
            store.filter('reportid','2');
            store.totalCount = store.data.length;
        }
        if(this.flowsSelNode == '完成环节'){
            store.filterBy(function(record){
                return record.get('reportid') != '2';
            })
        }
        reportGridWin.show();
    },

    getCalloutid:function (entryId) {
        var calloutid;
        Ext.Ajax.request({
            url: '/digitalProcess/getCalloutid',
            async:false,
            params:{
                entryId:entryId
            },
            success: function (response) {
                calloutid = Ext.decode(response.responseText);
            }
        });
        return calloutid;
    },

    photoBack:function(){
        var win = p5['_back'];
        if(p5){
            p5['_pass'].getStore().reload();
            var select =   p5['_pass'].getSelectionModel();
            select.clearSelections();// 取消选择
            p5 = null;
        }
        win.close();
    },

    photoPass:function(){
        var that = p5['_next'];
        if(!p5['_back'].calloutId){
            XD.msg('请选择数据');
            return;
        }else{
            XD.confirm('确定通过?',function(){
                that.changeStatusAction([p5['_back'].calloutId],that.flowsSelNode,"已签收",null,p5.assemblyid,function(){
                    var entryGrid = p5['_back'].down('[itemId=detailEntryGridId]');
                    var mediaGrid = p5['_back'].down('[itemId=detailMediaEntryGridId]');
                    // var errGrid = p5['_back'].down('[itemId=detailErrorId]');
                    // var entryForm = p5['_back'].down('[itemId=entryForm]');
                    entryGrid.getStore().reload();
                    mediaGrid.getStore().proxy.extraParams.entryid = null;
                    mediaGrid.getStore().reload();
                    // errGrid.getStore().proxy.extraParams.entryid = null;
                    // errGrid.getStore().reload();
                });
            },this);
        }
    },

    photoNext:function(){
        var that = p5.that;
        var leftgrid = p5['_back'].down('[itemId=detailMediaEntryGridId]');
        var sel = leftgrid.getSelection()[0];
        var leftGridStore = leftgrid.getStore();
        var selIndex = leftGridStore.indexOf(sel);
        if(selIndex == leftGridStore.getCount() - 1){
            // XD.msg('已经是最后一页了');
            leftgrid.scrollTo(0, 0);
            that.detailNext();
            return;
        }
        leftgrid.scrollTo(0, selIndex*40);
        var next = leftGridStore.getAt(selIndex+1);
        leftgrid.getSelectionModel().select(next);
        leftgrid.fireEvent('itemclick', leftgrid, next);
    },

    photoPrevious:function(){
        var that = p5.that;
        var leftgrid = p5['_back'].down('[itemId=detailMediaEntryGridId]');
        var sel = leftgrid.getSelection()[0];
        var leftGridStore = leftgrid.getStore();
        var selIndex = leftGridStore.indexOf(sel);
        if(selIndex == 0){
            // XD.msg('已经是第一页了');
            leftgrid.scrollTo(0, 0);
            that.detailPre();
            return;
        }
        leftgrid.scrollTo(0, selIndex*40);
        var pre = leftGridStore.getAt(selIndex-1);
        leftgrid.getSelectionModel().select(pre);
        leftgrid.fireEvent('itemclick', leftgrid, pre);
    },

    photoSenior:function(){
        var mediaId = p5['_back'].mediaId;
        if(!mediaId){
            XD.msg('请选择需要下载的原文');
            return;
        }else{
            location.href = '/digitalProcess/showMedia?eleid='+mediaId;
        }
    },

    photoExitda:function(){
        var mediaId = p5['_back'].mediaId;
        if(!mediaId){
            XD.msg('请选择需要上传的原文');
            return;
        }else{
            var uploadView = Ext.create('DigitalProcess.view.ShUploadView');
            uploadView.show();
        }
    }
});

//调整字体颜色
function returncolor(status){
    switch(status){
        case "已处理":
            return 'black';
            break;
        case "审核退回":
            return 'purple';
            break;
        case "退回":
            return 'red';
            break;
        case "已退回":
            return 'red';
            break;
        default:
            return 'blue';
            break;
    }
}
//相关日志备注信息或错误信息1
function errorMessage(marr,nodename){
    var html='';
    var depict=returnarr(marr,nodename,'备注登记');
    var err=returnarr(marr,nodename,'退回');
    html=typeof(depict) == "undefined"?html+'':html+ '<span  style="color: red;">备注信息：'+depict[4]+'</span></br><span style="color: red;">' +'备注人：'+depict[2]+','+depict[3]+ '</span></br>';
    html=typeof(err) == "undefined"?html+'':html+ '<span  style="color: red;">错误信息：'+err[4]+ '</span></br><span style="color: red;">' +'处理人：'+err[2]+','+err[3]+ '</span></br>';
    return html;
}
//相关日志备注信息或错误信息
function returnarr(marr,nodename,status) {
    for(var i=0;i<marr.length;i++){
        if(marr[i][0]==nodename && status==marr[i][1]){
            return marr[i];
        }
    }
}

/**
 *1.点击条目,默认加载前五张图片
 *2.点击图片时加载前后五张图(预先判断缓存对象是否加载)
 *3.条目切换情况缓存图片对象
 * @param grid   原文列表
 * @param scope  加载范围
 * @param index  加载中心坐标
 */
function imgPreload(grid,index){
    var scope = xdSzhCache.scope;
    var store = grid.getStore();
    var start = index-scope<0?0:index-scope;//开始下标
    var end = index+scope>store.getCount()?store.getCount():index+scope;//结束下标
    for(;start<end;start++){
        var eleid = store.getAt(start).get('eleid');
        if(!eleid){
            eleid = store.getAt(start).get('mediaid');
        }
        var isRefresh = xdSzhCache.refreshCache.hasOwnProperty(eleid);
        var refreshVal = xdSzhCache.refreshCache[eleid];
        if(!xdSzhCache.imgCache.hasOwnProperty(eleid)||isRefresh){
            var img = new Image();
            img.src = '/digitalProcess/showMedia?eleid='+eleid+(isRefresh?'&r='+refreshVal:'');
            xdSzhCache.imgCache[eleid] = img;
        }
    }
}

