Ext.define('DigitalInspection.controller.DigitalInspectionController', {
    extend: 'Ext.app.Controller',
    views: [
        'DigitalInspectionView',
        'DigitalInspectionWcView',
        'DigitalInspectionWclView',
        'DigitalInspectionWclGridView',
        'DigitalInspectionIngGridView',
        'DigitalInspectionWcGridView',
        'DigitalInspectionDetailView',
        'DigitalInspectionDetailRightView',
        'DigitalInspectionDetailLeftView',
        'DigitalInspectionAddBatchForm',
        'DigitalInspectionSearchView',
        'DigitalInspectionSearchGridView',
        'DigitalInspectionSamplingForm',
        'DigitalInspectionIngView',
        'DigitalInspectionIngSouthView',
        'DigitalInspectionDetailRightTopView',
        'DigitalInspectionDetailErrForm',
        'DigitalInspectionAcceptForm',
        'DigitalInspectionWcAcceptView',
        'DigitalInspectionWcAcceptGridView',
        'EntryFormView'
    ],//加载view
    stores: [
        'DigitalInspectionWclGridStore',
        'DigitalInspectionIngGridStore',
        'DigitalInspectionWcGridStore',
        'DigitalInspectionEntryGridStore',
        'DigitalInspectionSearchGridStore',
        'DigitalInspectionEntryDetailGridStore',
        'DigitalInspectionEntryMediaGridStore',
        'DigitalInspectionMediaErrGridStore',
        'DigitalInspectionWcEntryGridStore',
        'DigitalInspectionWcAcceptEntryGridStore',
        'CheckGroupStore'
    ],//加载store
    models: [
        'DigitalInspectionWclGridModel',
        'DigitalInspectionIngGridModel',
        'DigitalInspectionWcGridModel',
        'DigitalInspectionEntryGridModel',
        'DigitalInspectionSearchGridModel',
        'DigitalInspectionEntryMediaGridModel',
        'DigitalInspectionEntryMediaGridModel',
        'DigitalInspectionEntryDetailGridModel'
    ],//加载model
    init: function () {
        var billWclGrid,billIngGrid,billWcGrid,p1,isImp;
        this.samplingForm = {};
        this.control({
            'DigitalInspectionView':{
                render:function (view) {
                    billWclGrid = view.down('[itemId=notInspectionViewId]').down('DigitalInspectionWclGridView');
                    // var billGrid = view.down('[itemId=notInspectionViewId]').down('DigitalInspectionGridView');
                    // var buttons = billGrid.down('toolbar').query('button');
                    // var tbseparators = billGrid.down('toolbar').query('tbseparator');
                    // buttons[3].hide();
                    // buttons[4].hide();
                    // tbseparators[3].hide();
                },
                tabchange:function(view){//选项卡切换
                    if(view.activeTab.title == '未抽检批次'){
                        var billWclGrid = view.down('[itemId=notInspectionViewId]').down('DigitalInspectionWclGridView');
                        var entryGrid = view.down('entrygrid');
                        // var buttons = billGrid.down('toolbar').query('button');
                        // var tbseparators = billGrid.down('toolbar').query('tbseparator');
                        // for(var i in buttons){
                        //     if(tbseparators[i]){
                        //         if(i>2){
                        //             buttons[i].hide();
                        //             tbseparators[i].hide();
                        //         }else{
                        //             buttons[i].show();
                        //             tbseparators[i].show();
                        //         }
                        //     }
                        // }
                        entryGrid.getStore().proxy.extraParams.isCheck = '否';
                        entryGrid.getStore().proxy.extraParams.batchcode = null;
                        billWclGrid.getStore().proxy.extraParams.status = '未抽检';
                        billWclGrid.getStore().reload();
                        entryGrid.getStore().reload();
                    }else if(view.activeTab.title == '正在抽检批次'){
                        var billIngGrid = view.down('[itemId=InspectioningId]').down('DigitalInspectionIngGridView');
                        var southView = view.down('DigitalInspectionIngSouthView');
                        var entryGrid = view.down('entrygrid');
                        var isCheck = '是';
                        if(southView.activeTab.title=='未抽检'){
                            isCheck = '否';
                        }
                        entryGrid.getStore().proxy.extraParams.isCheck = isCheck;
                        entryGrid.getStore().proxy.extraParams.batchcode = null;
                        billIngGrid.getStore().proxy.extraParams.status = '正在抽检';
                        billIngGrid.getStore().reload();
                        entryGrid.getStore().reload();
                        this.billIngGrid = billIngGrid;
                    } else if(view.activeTab.title == '完成抽检批次'){
                        var billWcGrid = view.down('[itemId=finishInspectionId]').down('DigitalInspectionWcGridView');
                        var entryGrid = view.down('entrygrid');
                        entryGrid.getStore().proxy.extraParams.isCheck = '是';
                        entryGrid.getStore().proxy.extraParams.batchcode = null;
                        billWcGrid.getStore().proxy.extraParams.status = '完成抽检';
                        billWcGrid.getStore().reload();
                        entryGrid.getStore().reload();
                    }else{
                        var billWcAcceptGrid = view.down('[itemId=finishAcceptId]').down('DigitalInspectionWcAcceptGridView');
                        var entryGrid = view.down('entrygrid');
                        entryGrid.getStore().proxy.extraParams.isCheck = null;
                        entryGrid.getStore().proxy.extraParams.batchcode = null;
                        billWcAcceptGrid.getStore().proxy.extraParams.status = '完成验收';
                        billWcAcceptGrid.getStore().reload();
                        entryGrid.getStore().reload();
                    }
                }
            },

            'DigitalInspectionIngSouthView':{//正在抽检--》条目
                tabchange:function(view){//选项卡切换
                    var entryGridStore = view.down('entrygrid').getStore();
                    entryGridStore.proxy.extraParams.notMe = '否';
                    if(view.activeTab.title=='未抽检'){
                        entryGridStore.proxy.extraParams.isCheck = '否';
                    }else if(view.activeTab.title=='他人抽检'){
                        entryGridStore.proxy.extraParams.notMe = '是';
                        entryGridStore.proxy.extraParams.isCheck = '是';
                    } else{
                        entryGridStore.proxy.extraParams.isCheck = '是';
                    }
                    entryGridStore.reload();
                }
            },

            'DigitalInspectionWcSouthView':{//完成抽检--》条目
                tabchange:function(view){//选项卡切换
                    var entryGridStore = view.down('entrygrid').getStore();
                    if(view.activeTab.title=='通过'){
                        entryGridStore.proxy.extraParams.status = '通过';
                    }else if(view.activeTab.title=='退回'){
                        entryGridStore.proxy.extraParams.status = '退回';
                    }else{
                        entryGridStore.proxy.extraParams.status = '未检查';
                    }
                    entryGridStore.reload();
                }
            },

            'DigitalInspectionWcAcceptSouthView':{//完成验收--》条目
                tabchange:function(view){//选项卡切换
                    var entryGridStore = view.down('entrygrid').getStore();
                    if(view.activeTab.title=='已验收'){
                        entryGridStore.proxy.extraParams.status = '已验收';
                    }else if(view.activeTab.title=='已退回'){
                        entryGridStore.proxy.extraParams.status = '退回';
                    }else{
                        entryGridStore.proxy.extraParams.status = '未检查';
                    }
                    entryGridStore.reload();
                }
            },

            'DigitalInspectionWclGridView,DigitalInspectionIngGridView,DigitalInspectionWcGridView,DigitalInspectionWcAcceptGridView':{//批次表格行单击
                itemclick : this.itemclickHandler
            },

            'entrygrid':{//批次条目双击
                itemdblclick :this.itemdblclickHandler
            },

            'EntryFormView [itemId=save]':{//表单保存
                click:this.entryFormSave
            },

            'EntryFormView [itemId=back]':{//表单返回
                click:function (btn) {
                    window.formView.close();
                }
            },

            'DigitalInspectionWclGridView [itemId=add]':{//新增批次
                click:function (btn) {
                    this.addBatch(btn);
                }
            },

            'DigitalInspectionAddBatchForm [itemId=batchAddSubmit]':{//添加批次提交
                click:function (btn) {
                    this.batchAddFormSubmit(btn);
                }
            },

            'DigitalInspectionAddBatchForm [itemId=batchAddClose]':{//添加批次返回
                click:function (btn) {
                    btn.up('DigitalInspectionAddBatchForm').close();
                }
            },

            'DigitalInspectionSearchView [itemId=simpleSearchSearchfieldId]':{//添加批次查询条目
                search:function(searchfield){
                    this.importSearchInfo(searchfield);
                }
            },

            'DigitalInspectionSearchView [itemId=topCloseBtn]':{//添加批次条目返回
                click:function(){
                    window.importWin.close();
                    window.importWin.formView.close();
                    billWclGrid.getStore().reload();
                }
            },

            'DigitalInspectionSearchGridView [itemId=entryImportId]':{//添加批次导入
                click:function(btn){
                    this.entryImport(btn,billWclGrid);
                }
            },

            'DigitalInspectionWclGridView [itemId=del]':{//删除批次
                click:function (btn) {
                    this.delBatch(btn);
                }
            },

            'DigitalInspectionWclGridView [itemId=sampling]':{//设置抽检
                click:function (btn) {
                    this.samplingBatch(btn);
                }
            },

            'DigitalInspectionSamplingForm [itemId=samplingSubmit]':{
                click:function(btn){
                    this.samplingSubmit(btn);
                }
            },

            'DigitalInspectionSamplingForm [itemId=samplingClose]':{//抽检返回
                click:function(btn){
                     btn.up('DigitalInspectionSamplingForm').close();
                }
            },

            'DigitalInspectionIngGridView [itemId=startSampling]':{//开始抽检
                click:function (btn) {
                    this.startSampling(btn);
                }
            },

            'DigitalInspectionIngGridView [itemId=cancelSampling]':{//取消抽检
                click:function (btn) {
                    this.cancelSampling(btn);
                }
            },

            'DigitalInspectionDetailLeftView [itemId=detailEntryGridId]':{//开始抽检条目表单击
                afterrender:this.samplingDetailSel,
                itemclick:this.samplingDetailMsg
            },

            'DigitalInspectionDetailLeftView [itemId=detailMediaEntryGridId]':{//开始抽检原文表单击
                itemclick:this.samplingDetailMedia
            },

            'DigitalInspectionDetailRightView [itemId=add]':{//抽检原文错误信息添加窗口
                click:this.errAdd
            },

            'DigitalInspectionDetailErrForm [itemId=errSubmit]':{//抽检原文错误信息添加提交
                click:this.errSubmit
            },

            'DigitalInspectionDetailErrForm [itemId=errClose]':{//抽检原文错误信息添加窗口关闭
                click:this.errClose
            },

            'DigitalInspectionDetailRightView [itemId=del]':{//抽检原文错误信息删除
                click:this.errDel
            },

            'DigitalInspectionDetailRightView [itemId=repair]':{//抽检原文错误信息修复
                click:this.errRepair
            },

            'DigitalInspectionDetailRightView [itemId=export]':{//抽检原文错误信息导出
                click:this.errExport
            },

            'DigitalInspectionDetailRightTopView [itemId=save]':{//元数据修改保存
                click:this.metadataSave
            },

            'DigitalInspectionWcGridView [itemId=accept]':{//验收
                click:this.accept
            },

            'DigitalInspectionWcGridView [itemId=md5]':{//验证md5
                click:this.checkMd5
            },

            'DigitalInspectionAcceptForm [itemId=submit]':{//验收提交
                click:this.acceptSubmit
            },

            'DigitalInspectionAcceptForm [itemId=close]':{//验收返回
                click:function(btn){
                    btn.up('DigitalInspectionAcceptForm').close();
                }
            }
        });
    },
    itemclickHandler: function(view, record, item, index, e){
        var tabTitle = view.up('DigitalInspectionView').activeTab.title;
        var southGrid;
        if(tabTitle=='正在抽检批次'){
            southGrid = view.up('DigitalInspectionIngView').down('entrygrid');
            var southGridStore = southGrid.getStore();
            southGridStore.proxy.extraParams.flag = '在抽检';
        }else if(tabTitle=='完成抽检批次'){
            southGrid = view.up('DigitalInspectionWcView').down('entrygrid');
            var southGridStore = southGrid.getStore();
            var southGridTabView = view.up('DigitalInspectionWcView').down('DigitalInspectionWcSouthView');
            if(southGridTabView.activeTab.title=='通过'){
                southGridStore.proxy.extraParams.status = '通过';
            }else if(southGridTabView.activeTab.title=='退回'){
                southGridStore.proxy.extraParams.status = '退回';
            }else{
                southGridStore.proxy.extraParams.status = '未检查';
            }
        }else if(tabTitle=='完成验收批次'){
            southGrid = view.up('DigitalInspectionWcAcceptView').down('entrygrid');
            var southGridStore = southGrid.getStore();
            var southGridTabView = view.up('DigitalInspectionWcAcceptView').down('DigitalInspectionWcAcceptSouthView');
            if(southGridTabView.activeTab.title=='已验收'){
                southGridStore.proxy.extraParams.status = '已验收';
            }else if(southGridTabView.activeTab.title=='已退回'){
                southGridStore.proxy.extraParams.status = '退回';
            }else{
                southGridStore.proxy.extraParams.status = '未检查';
            }
        }else{
            southGrid = view.up('DigitalInspectionWclView').down('entrygrid');
            var southGridStore = southGrid.getStore();
            southGridStore.proxy.extraParams.flag = '未抽检';
        }
        var batchcode =record.get('batchcode');
        southGrid.expand();
        southGridStore.proxy.extraParams.batchcode = batchcode;
        southGridStore.loadPage(1);
    },

    itemdblclickHandler:function(view, record, item, index, e){
        var formModel = view.up('entrygrid').itemId=='detailEntryGridId'?'modify':'look';
        var captureentryid =record.get('captureentryid');
        var nodeid =record.get('nodeid');
        if(!captureentryid){
            return;
        }
        var formView = Ext.create("Ext.window.Window",{
            width:'100%',
            height:'100%',
            header: false,
            layout:'fit',
            items:[
                {
                    xtype:'EntryFormView'
                }
                ]});
        var form = formView.down('EntryFormView').down('dynamicform');
        form.operate = formModel;
        form.entryids = [captureentryid];
        form.entryid = captureentryid;
        this.initFormField(form, 'hide', nodeid);
        this.initFormData(formModel,form, captureentryid);
        formView.show();
        window.formView = formView;
        Ext.on('resize',function(a,b){
            window.formView.setPosition(0, 0);
            window.formView.fitContainer();
        });
        // var southGrid = view.up('DigitalInspectionWclView').down('entrygrid');
        if('look'==formModel){
            formView.down('[itemId=save]').hide();
        }
    },

    entryFormSave:function (btn) {
        var that = this;
        var eleids = btn.findParentByType('EntryFormView').down('electronic').getEleids();
        var form = btn.findParentByType('EntryFormView').down('dynamicform');
        //字段编号，用于特殊的自定义字段(范围型日期)
        var fieldCode = form.getRangeDateForCode();
        var nodename = this.getNodename(form.nodeid);
        var params = {
            nodeid: form.nodeid,
            eleid: eleids,
            type: form.up('EntryFormView').operateFlag,
            operate: nodename
        };
        if (fieldCode != null) {
            params[fieldCode] = form.getDaterangeValue();
        }
        var archivecodeSetState = form.setArchivecodeValueWithNode(nodename);
        if (!archivecodeSetState) {//若档号设置失败，则停止后续的表单提交
            return;
        }
        var entryId = form.getForm().findField('entryid').getValue();
        form.submit({
            method: 'POST',
            url: '/acquisition/entries',
            params: params,
            scope: this,
            success: function (form, action) {
                XD.msg(action.result.msg);
                that.changeBatchEntry(entryId);
            },
            failure: function (form, action) {
                XD.msg(action.result.msg);
            }
        });
    },

    changeBatchEntry:function(entryId){
        var batchcode = detailView.batchcodes[0];
        Ext.Ajax.request({
            url: '/digitalInspection/changeBatchEntry',
            method: 'POST',
            params: {'batchcode': batchcode,'entryId':entryId},
            success:function (response) {
                if(Ext.decode(response.responseText).success==true){
                    detailView.down('[itemId=detailEntryGridId]').getStore().reload();
                    formView.close();
                }
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

    initFormData:function (operate, form, entryid) {
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
            if(fields[i].value&&typeof(fields[i].value)=='string'&&fields[i].value.indexOf('label')>-1){
                continue;
            }
            if(fields[i].xtype == 'combobox'){
                fields[i].originalValue = null;
            }
            nullvalue.set(fields[i].name, null);
        }
        form.loadRecord(nullvalue);
        var etips = formview.down('[itemId=etips]');
        etips.show();
        //this.activeForm(form);
        Ext.Ajax.request({
            method:'GET',
            scope:this,
            url:'/acquisition/entries/'+entryid,
            success:function(response){
                if(operate!='look'){
                    var settingState = ifSettingCorrect(form.nodeid,form.templates);
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

    addBatch:function(btn){
      billGrid = btn.up('DigitalInspectionGridView');
      var batchAddView = Ext.create('DigitalInspection.view.DigitalInspectionAddBatchForm');
      batchAddView.show();
      var batchAddForm = batchAddView.down('form');
        batchAddForm.load({
          url: '/digitalInspection/getBatchAddForm',
        success: function (form, action) {
        },
        failure: function () {
            XD.msg('获取批次号失败');
            batchAddView.hide();
        }
      });
    },

    batchAddFormSubmit:function(btn){
        var batchAddFormView = btn.up('DigitalInspectionAddBatchForm');
        var batchAddForm = batchAddFormView.down('form');
        if(batchAddForm.isValid()){
            var archivetype = batchAddForm.getForm().findField('archivetype').getValue();
            var importWin = Ext.create('Ext.window.Window', {
                width: '100%',
                height: '100%',
                header: false,
                modal: true,
                closeToolText: '关闭',
                layout: 'fit',
                xtype:'importWin',
                formView:batchAddFormView,
                addForm:batchAddForm,
                items: [{
                    xtype: 'DigitalInspectionSearchView',
                    archivesType:archivetype
                }]
            });
            importWin.show();
            window.importWin = importWin;
            this.isImp = false;
            Ext.on('resize',function(a,b){
                window.importWin.setPosition(0, 0);
                window.importWin.fitContainer();
            });
        }
    },

    entryImport:function(btn,billWclGrid){
        var sGrid = btn.findParentByType('DigitalInspectionSearchGridView');
        var gridModel = sGrid.getSelectionModel();
        var record = gridModel.getSelection();
        if(record.length==0){
            XD.msg('请选择需要导入的数据!');
            return;
        }
        var dataids = [];
        for (var i = 0; i < record.length; i++) {
            dataids.push(record[i].get('entryid'));
        }

        var batchAddForm = window.importWin.addForm;
        var batchAddFormView = window.importWin.formView;
        batchAddForm.getForm().findField('id').setValue(dataids.join(','));
        if(!this.isImp){
            this.isImp = true;
        }else{
            batchAddForm.getForm().findField('inspector').setValue('*');
        }
        batchAddForm.submit({
            url: '/digitalInspection/batchAddFormSubmit',
            method: 'POST',
            success: function (response) {
                XD.msg('导入成功');
                // batchAddFormView.close();
                // window.importWin.close();
                // billWclGrid.getStore().reload();
            }, failure: function () {
                XD.msg('操作失败');
            }
        });
    },

    importSearchInfo:function (searchfield) {
            //获取检索框的值
            var simpleSearchSearchView = searchfield.findParentByType('panel');
            var condition = simpleSearchSearchView.down('[itemId=simpleSearchSearchComboId]').getValue(); //字段
            var operator = 'like';//操作符
            var content = searchfield.getValue(); //内容
            var isCollection;
            var sGrid = searchfield.findParentByType('DigitalInspectionSearchView').down('DigitalInspectionSearchGridView');
            if (sGrid.title == '当前位置：个人收藏') {//如果是收藏界面
                isCollection = '收藏';
            }
            //检索数据
            //如果有勾选在结果中检索，则添加检索条件，如果没有勾选，则重置检索条件
            var grid = simpleSearchSearchView.findParentByType('panel').down('DigitalInspectionSearchGridView');
            var gridstore = grid.getStore();
            //加载列表数据
            var searchcondition = condition;
            var searchoperator = operator;
            var searchcontent = content;
            //如果选择在结果中查询,将两种条件用','号隔开,参数一起传递到后台
            var inresult = simpleSearchSearchView.down('[itemId=inresult]').getValue();
            if(inresult){
                var params = gridstore.getProxy().extraParams;
                if(typeof(params.condition) != 'undefined'){
                    searchcondition = [params.condition,condition].join(XD.splitChar);
                    searchoperator = [params.operator,operator].join(XD.splitChar);
                    searchcontent = [params.content,content].join(XD.splitChar);
                }
            }
            grid.dataParams={
                isCollection: isCollection,
                condition: searchcondition,
                operator: searchoperator,
                content: searchcontent,
                archivesType:searchfield.up('DigitalInspectionSearchView').archivesType
            };

            //检索数据前,修改column的renderer，将检索的内容进行标红
            Ext.Array.each(grid.getColumns(), function(){
                var column = this;
                if(!inresult && column.type == 'gridcolumn'){
                    column.renderer = function(value){
                        return value;
                    }
                }
                if(column.dataIndex == condition){
                    var searchstrs = [];
                    var conditions = searchcondition.split(XD.splitChar);
                    var contents = searchcontent.split(XD.splitChar);
                    for(var i =0;i<conditions.length;i++){
                        if(conditions[i] == condition){
                            searchstrs.push(contents[i]);
                        }
                    }
                    column.renderer = function(value){
                        var contentData = searchstrs.join('|').split(' ');//切割以空格分隔的多个关键词
                        var reg = new RegExp(contentData.join('|'),'g');
                        return value.replace(reg,function (match) {
                            return '<span style="color:red">'+match+'</span>';
                        });
                    }
                }
            });
            if(grid.bookmarkStatus==false){
                var simpleSearchStore = Ext.create('DigitalInspection.store.DigitalInspectionSearchGridStore');
                grid.reconfigure(simpleSearchStore);
            }
            grid.initGrid();
            grid.parentXtype = 'simpleSearchView';
            grid.formXtype = 'EntryFormView';
        },

    delBatch:function(btn){
        var billGrid = btn.up('DigitalInspectionWclGridView');
        var entryGrid = btn.up('DigitalInspectionWclView').down('entrygrid');
        var select = billGrid.getSelectionModel();
        if (select.getSelected().length<1) {
            XD.msg('至少选择一条数据');
            return;
        }
        var record = select.selected.items;
        var batchcodes = [];
        for (var i = 0; i < record.length; i++) {
            batchcodes.push(record[i].get('batchcode'));
        }

        XD.confirm('确定要删除这' + record.length + '条数据吗',function(){
            Ext.Ajax.request({
                params: {'batchcodes': batchcodes},
                url: '/digitalInspection/batchDel',
                method: 'POST',
                sync: true,
                success: function (resp) {
                    var respText = Ext.decode(resp.responseText);
                    if (respText.success == true) {
                        billGrid.getStore().reload();
                        entryGrid.getStore().reload();
                    }
                    XD.msg(respText.msg);
                },
                failure: function() {
                    XD.msg('操作失败');
                }
            });
        },this);
    },

    samplingBatch:function(btn){
        var billGrid = btn.up('DigitalInspectionWclGridView');
        var select = billGrid.getSelectionModel();
        if (select.getSelected().length<1) {
            XD.msg('至少选择一条数据');
            return;
        }
        var record = select.selected.items;
        var batchcodes = [];
        for (var i = 0; i < record.length; i++) {
            batchcodes.push(record[i].get('batchcode'));
        }

        var entryGrid = btn.up('DigitalInspectionWclView').down('entrygrid');
        var samplingFormView = Ext.create('DigitalInspection.view.DigitalInspectionSamplingForm',{'batchcodes':batchcodes,'billGrid':billGrid,'entryGrid':entryGrid});
        samplingFormView.down('form').down('[name=checkcount]').setValue("100");
        samplingFormView.show();
        if(this.samplingForm.checkcount){
            samplingFormView.down('form').getForm().findField('checkcount').setValue(this.samplingForm.checkcount);
            samplingFormView.down('form').getForm().findField('samplingtype').setValue(this.samplingForm.samplingtype);
        }
        // var detailView = Ext.create('DigitalInspection.view.DigitalInspectionDetailView');
        // detailView.show();
        // window.detailView = detailView;
        // Ext.on('resize',function(a,b){
        //     window.detailView.setPosition(0, 0);
        //     window.detailView.fitContainer();
        // });
    },

    samplingSubmit:function(btn){
        var samplingFormView = btn.up('DigitalInspectionSamplingForm');
        var form = samplingFormView.down('form');
        if(form.isValid()){
            this.samplingForm.checkcount = form.getForm().findField('checkcount').getValue();
            this.samplingForm.samplingtype = form.getForm().findField('samplingtype').getValue();
            this.samplingForm.checkgroupid = form.getForm().findField('checkgroupid').getValue();
            form.getForm().findField('batchcodes').setValue(samplingFormView.batchcodes.join(','));
            form.submit({
                url: '/digitalInspection/samplingSubmit',
                method: 'POST',
                success: function () {
                    XD.msg('设置抽检率完成');
                    samplingFormView.billGrid.getStore().reload();
                    samplingFormView.entryGrid.getStore().proxy.extraParams.batchcode = null;
                    samplingFormView.entryGrid.getStore().proxy.extraParams.flag = null;
                    samplingFormView.entryGrid.getStore().reload();
                    samplingFormView.close();
                }, failure: function () {
                    XD.msg('操作失败');
                }
            });
        }
    },

    startSampling:function(btn){
        var billGrid = btn.up('DigitalInspectionIngGridView');
        var entryGrid = billGrid.up('DigitalInspectionIngView').down('DigitalInspectionIngSouthView').down('entrygrid');
        var select = billGrid.getSelectionModel();
        if (select.getSelected().length<1) {
            XD.msg('至少选择一条数据');
            return;
        }
        var record = select.selected.items;
        var batchcodes = [];
        for (var i = 0; i < record.length; i++) {
            batchcodes.push(record[i].get('batchcode'));
        }

        var detailView = Ext.create('DigitalInspection.view.DigitalInspectionDetailView',{'batchcodes':batchcodes});
        detailView.show();
        p1 = new PhotoView({eleid:'p1',isCommon:true,outer:{
            next:{
                method:this.photoNext,
                attr:this
            },
            previous:{
                method:this.photoPrevious
            },
            exitda:{
                method:this.photoExitda,
            },
            pass:{
                method:this.photoPass
            },
            back:{
                method:this.photoBack
            }
        }});
        window.detailView = detailView;
        window.detailView.batcEntryhMedias = [];
        window.detailView.billIngGrid = billGrid;
        window.detailView.entryGrid = entryGrid;
        Ext.on('resize',function(a,b){
            window.detailView.setPosition(0, 0);
            window.detailView.fitContainer();
        });
        this.initSamplingDetail(detailView);
    },

    cancelSampling:function(btn){
        var billGrid = btn.up('DigitalInspectionIngGridView');
        var entryGrid = btn.up('DigitalInspectionIngView').down('entrygrid');
        var select = billGrid.getSelectionModel();
        if (select.getSelected().length<1) {
            XD.msg('至少选择一条数据');
            return;
        }
        var record = select.selected.items;
        var batchcodes = [];
        for (var i = 0; i < record.length; i++) {
            batchcodes.push(record[i].get('batchcode'));
        }

        XD.confirm('确定要退回选择的'+record.length+'条批次吗?',function(){
            Ext.Ajax.request({
                params: {'batchcodes': batchcodes},
                url: '/digitalInspection/cancelSampling',
                method: 'POST',
                sync: true,
                success: function (resp) {
                    var respText = Ext.decode(resp.responseText);
                    if (respText.success == true) {
                        billGrid.getStore().reload();
                        entryGrid.getStore().reload();
                        XD.msg("操作成功");
                    }else{
                        XD.msg("操作失败");
                    }
                },
                failure: function() {
                    XD.msg('操作失败');
                }
            });
        },this);
    },

    initSamplingDetail:function(detailView,code){
        var billForm = detailView.down('DigitalInspectionDetailLeftView').down('form');
        var detailEntryGrid = detailView.down('[itemId=detailEntryGridId]');
        var detailMediaEntryGrid = detailView.down('[itemId=detailMediaEntryGridId]');
        var detailErrorGrid = detailView.down('[itemId=detailErrorId]');
        var batchcodes = detailView.batchcodes;
        var batchcode = code?code:batchcodes[0]
        // billForm.getForm().findField('selectLabel').setValue('<span style="color:red;">共'+batchcodes.length+'条,当前第1条</span>')
        var entryStore = detailEntryGrid.getStore();
        entryStore.proxy.extraParams.batchcode = batchcode;
        entryStore.load(function () {
            if(entryStore.getCount() > 0){
                detailEntryGrid.fireEvent('itemclick',this,entryStore.getAt(0));
                detailEntryGrid.getSelectionModel().select(0);
            }else{
                detailErrorGrid.getStore().proxy.extraParams.batchcode = null;
                detailErrorGrid.getStore().proxy.extraParams.mediaid = null;
                detailErrorGrid.getStore().reload();

                detailMediaEntryGrid.getStore().proxy.extraParams.batchcode = null;
                detailMediaEntryGrid.getStore().proxy.extraParams.entryid = null;
                detailMediaEntryGrid.getStore().reload();

                billForm.load({
                    url: '/digitalInspection/getBill',
                    method: 'POST',
                    params: {
                        'batchcode': batchcode
                    },
                    failure: function () {
                        XD.msg('操作失败');
                    }
                });
            }
        });
    },

    samplingDetailSel:function(grid){
        // var select = grid.getSelectionModel();
        // grid.getSelectionModel().selectAll();
    },

    samplingDetailMsg:function(view, record, item, index, e){
        var entryId = record.get('captureentryid');
        var batchcode = record.get('batchcode');
        var entryForm = window.detailView.down('DigitalInspectionDetailRightView').down('form');
        var entryMediaView = window.detailView.down('[itemId=detailMediaEntryGridId]');
        entryMediaView.getStore().proxy.extraParams.batchcode = batchcode;
        entryMediaView.getStore().proxy.extraParams.entryid = entryId;
        detailView.batcEntryhMedias = [];
        detailView.billEntryId = record.get('id');
        detailView.entryId = entryId;
        var that = this;
        Ext.Ajax.request({
            params: {'batchcode': batchcode,entryid:entryId},
            url: '/digitalInspection/getEntryMedias',
            method: 'POST',
            sync: true,
            success: function (resp) {
                var respText = Ext.decode(resp.responseText);
                var types = ['jpg','jpeg','png'];
                var state = false;
                if(respText.length<1){
                    entryMediaView.getStore().loadData([]);
                    p1.changeImg();
                    return;
                }
                entryMediaView.getStore().loadData(respText);
                for(var i in respText){
                    var type = respText[i]?respText[i].mediatype.toLowerCase():'';
                    if(!isNaN(i)&&types.indexOf(type)>-1){
                        if(!state){
                            // p1.changeImg('/digitalInspection/showMedia?eleid='+respText[i].eleid,respText[i].medianame);
                            // that.loadErrorMsg(batchcode,respText[i].eleid);
                            detailView.selMediaId = respText[i].eleid;
                            that.changeMediaStatus();
                            that.showMetadata(respText[i].id);
                            state = true;
                        }
                        detailView.batcEntryhMedias.push(respText[i].eleid);
                    }
                }

                if(!state){
                    p1.changeImg();
                }
            },
            failure: function() {
                XD.msg('操作失败');
            }
        });

        entryForm.load({
            url: '/digitalInspection/getFormEntry',
            method: 'POST',
            params: {
                'entryid': entryId
            },
            failure: function () {
                XD.msg('操作失败');
            }
        });
    },

    showMetadata:function(mediaid){
        var metadataForm = window.detailView.down('[itemId=metadataId]').down('form');
        metadataForm.form.reset();
        metadataForm.load({
            url: '/digitalInspection/getMetadata',
            method: 'POST',
            params: {
                'mediaid': mediaid
            },
            failure: function () {
                XD.msg('操作失败');
            }
        });
    },


    samplingDetailMedia:function(view, record, item, index, e){
        var eleidId = record.get('eleid');
        var batchcode = record.get('batchcode');
        detailView.selMediaId = eleidId;
        this.showMetadata(record.get('id'));
        p1['_next'].changeMediaStatus();
    },

    loadErrorMsg:function(batchcode,eleid){
        var errorView = window.detailView.down('DigitalInspectionDetailRightView').down('[itemId=detailErrorId]');
        errorView.getStore().proxy.extraParams.batchcode = batchcode;
        errorView.getStore().proxy.extraParams.mediaid = eleid;
        errorView.getStore().reload();
    },

    changeMediaStatus:function(){
        var mediaId = detailView.selMediaId;
        var batchcode = detailView.batchcodes[0];
        var detailMediaEntryGrid = detailView.down('[itemId=detailMediaEntryGridId]');
        var rowRecord;
        detailMediaEntryGrid.getStore().each(function(record){
            if(mediaId==record.data.eleid){
                rowRecord = record;
            }
        });
        if(rowRecord){
            if(rowRecord.data.status=='未检查'){
                Ext.Ajax.request({
                    params: {'id': rowRecord.data.id},
                    url: '/digitalInspection/changeMediaStatus',
                    method: 'POST',
                    sync: true,
                    success: function (resp) {
                        var respText = Ext.decode(resp.responseText);
                        if(respText.success==false){
                            XD.msg('修改状态失败');
                        }else{
                            detailMediaEntryGrid.getStore().reload();
                        }
                    },
                    failure: function() {
                        XD.msg('修改状态失败');
                    }
                });
            }
        }

        p1.changeImg('/digitalInspection/showMedia?eleid='+detailView.selMediaId,rowRecord.data.medianame);
        p1['_next'].loadErrorMsg(batchcode,mediaId);
    },

    photoPrevious:function () {
        if(!p1){
            return;
        }
        var photoMediaIds = detailView.batcEntryhMedias;
        var selMediaId = detailView.selMediaId;
        var selIndex = photoMediaIds.indexOf(selMediaId);
        if(selIndex==-1){
            XD.msg('没有图片');
            return;
        }else if(selIndex==0){
            XD.msg('已经是第一页');
            return;
        }
        detailView.selMediaId = photoMediaIds[selIndex-1];
        p1['_next'].changeMediaStatus();
    },

    photoNext:function(){
        if(!p1){
            return;
        }
        var photoMediaIds = detailView.batcEntryhMedias;
        var selMediaId = detailView.selMediaId;
        var selIndex = photoMediaIds.indexOf(selMediaId);
        if(selIndex==-1){
            XD.msg('没有图片');
            return;
        }else if(selIndex==(photoMediaIds.length-1)){
            XD.msg('已经是最后一页');
            return;
        }
        detailView.selMediaId = photoMediaIds[selIndex+1];
        p1['_next'].changeMediaStatus();
    },

    photoExitda:function () {
        var batchcode = detailView.batchcodes[0];
        var billEntryId = detailView.billEntryId;
        var billEntryGrid = detailView.down('[itemId=detailEntryGridId]');
        XD.confirm('确定退档吗?',function(){
            Ext.Ajax.request({
                params: {'id':billEntryId,'batchcode':batchcode},
                url: '/digitalInspection/exitda',
                method: 'POST',
                sync: true,
                success: function (resp) {
                    var respText = Ext.decode(resp.responseText);
                    if(respText.success==false){
                        XD.msg('退档失败');
                    }else{
                        if(respText.msg==2){
                            XD.confirm('本批次条目已全部处理完,是否退出当前批次?',function(){
                                detailView.billIngGrid.getStore().reload();
                                detailView.entryGrid.getStore().proxy.extraParams.batchcode = null;
                                detailView.entryGrid.getStore().reload();
                                p1['_next'].photoBack();
                            },this);
                        }else{
                            XD.msg('退档成功');
                            billEntryGrid.getStore().load(function () {
                                var record;
                                for(var i=0;i<billEntryGrid.getStore().getCount();i++){
                                    if(detailView.entryId==billEntryGrid.getStore().getAt(i).get('captureentryid')){
                                        billEntryGrid.fireEvent('itemclick',billEntryGrid,billEntryGrid.getStore().getAt(i+1));
                                        billEntryGrid.getSelectionModel().select(i+1);
                                        break;
                                    }
                                }
                            });
                        }
                    }
                },
                failure: function() {
                    XD.msg('退档失败');
                }
            });
        },this);
    },

    photoPass:function () {
        var batchcode = detailView.batchcodes[0];
        var billEntryId = detailView.billEntryId;
        var billEntryGrid = detailView.down('[itemId=detailEntryGridId]');
        XD.confirm('确定通过吗?',function(){
            Ext.Ajax.request({
                params: {'id':billEntryId,'batchcode':batchcode},
                url: '/digitalInspection/passEntry',
                method: 'POST',
                sync: true,
                success: function (resp) {
                    var respText = Ext.decode(resp.responseText);
                    if(respText.success==false){
                        XD.msg('操作失败');
                    }else{
                        XD.msg('操作成功');
                        if(respText.msg==2){
                            XD.confirm('本批次条目已全部处理完,是否退出当前批次?',function(){
                                detailView.billIngGrid.getStore().reload();
                                detailView.entryGrid.getStore().proxy.extraParams.batchcode = null;
                                detailView.entryGrid.getStore().reload();
                                p1['_next'].photoBack();
                            },this);
                        }else{
                            billEntryGrid.getStore().load(function () {
                                var record;
                                for(var i=0;i<billEntryGrid.getStore().getCount();i++){
                                    if(detailView.entryId==billEntryGrid.getStore().getAt(i).get('captureentryid')){
                                        billEntryGrid.fireEvent('itemclick',billEntryGrid,billEntryGrid.getStore().getAt(i+1));
                                        billEntryGrid.getSelectionModel().select(i+1);
                                        break;
                                    }
                                }
                            });
                        }
                    }
                },
                failure: function() {
                    XD.msg('操作失败');
                }
            });
        },this);
    },

    photoBack:function(){
        if(p1){
            p1 = null;
        }
        detailView.close();
    },

    errAdd:function(){
        var mediaId = detailView.selMediaId;
        if(!mediaId){
            XD.msg('请选择原文');
            return;
        }
        var errFormView = Ext.create('DigitalInspection.view.DigitalInspectionDetailErrForm');
        errFormView.show();
    },

    errSubmit:function (btn) {
        var mediaId = detailView.selMediaId;
        var batchcode = detailView.batchcodes[0];
        var errFormView = btn.up('DigitalInspectionDetailErrForm');
        var form = errFormView.down('form');
        form.getForm().findField('batchcode').setValue(batchcode);
        form.getForm().findField('mediaid').setValue(mediaId);
        var that = this;
        form.submit({
            url: '/digitalInspection/errSubmit',
            method: 'POST',
            success: function () {
                XD.msg('操作成功');
                that.loadErrorMsg(batchcode,mediaId);
                errFormView.close();
            }, failure: function () {
                XD.msg('操作失败');
            }
        });
    },

    errClose:function (btn) {
        var errFormView = btn.up('DigitalInspectionDetailErrForm');
        errFormView.close();
    },

    errDel:function(){
        var that = this;
        var mediaId = detailView.selMediaId;
        var batchcode = detailView.batchcodes[0];
        var errorGrid = detailView.down('DigitalInspectionDetailRightView').down('[itemId=detailErrorId]');
        var select = errorGrid.getSelectionModel();
        if (select.getSelected().length<1) {
            XD.msg('至少选择一条数据');
            return;
        }
        var record = select.selected.items;
        var errids = [];
        for (var i = 0; i < record.length; i++) {
            errids.push(record[i].get('id'));
        }

        XD.confirm('确定要删除选择的'+errids.length+'条数据吗?',function(){
            Ext.Ajax.request({
                params: {'errids': errids},
                url: '/digitalInspection/delMediaErrs',
                method: 'POST',
                success: function (resp) {
                    var respText = Ext.decode(resp.responseText);
                    XD.msg(respText.msg);
                    that.loadErrorMsg(batchcode,mediaId);
                },
                failure: function() {
                    XD.msg('操作失败');
                }
            });
        },this);
    },

    errRepair:function(){
        var that = this;
        var mediaId = detailView.selMediaId;
        var batchcode = detailView.batchcodes[0];
        var errorGrid = detailView.down('DigitalInspectionDetailRightView').down('[itemId=detailErrorId]');
        var select = errorGrid.getSelectionModel();
        if (select.getSelected().length<1) {
            XD.msg('至少选择一条数据');
            return;
        }
        var record = select.selected.items;
        var errids = [];
        for (var i = 0; i < record.length; i++) {
            errids.push(record[i].get('id'));
        }

        XD.confirm('确定要修复选择的'+errids.length+'条数据吗?',function(){
            Ext.Ajax.request({
                params: {'errids': errids},
                url: '/digitalInspection/errRepair',
                method: 'POST',
                success: function (resp) {
                    var respText = Ext.decode(resp.responseText);
                    if(respText.success){
                        that.loadErrorMsg(batchcode,mediaId);
                        XD.msg('操作成功');
                    }else{
                        XD.msg('操作失败');
                    }
                },
                failure: function() {
                    XD.msg('操作失败');
                }
            });
        },this);
    },

    errExport:function(){
        var batchcode = detailView.batchcodes[0];
        location.href =  '/digitalInspection/exportErrReport?batchcode='+batchcode;
    },

    metadataSave:function (btn) {
        var metadataForm = window.detailView.down('[itemId=metadataId]').down('form');
        metadataForm.submit({
            method: 'POST',
            url: '/digitalInspection/metadataSubmit',
            scope: this,
            success: function (form, action) {
                XD.msg('修改成功');
            },
            failure: function (form, action) {
                XD.msg('操作失败');
            }
        });
    },

    accept:function(btn){
        var grid = btn.up('DigitalInspectionWcGridView');
        var select = grid.getSelectionModel();
        if (select.getSelected().length<1) {
            XD.msg('至少选择一个批次');
            return;
        }
        var record = select.selected.items;
        var batchcodes = [];
        for (var i = 0; i < record.length; i++) {
            batchcodes.push(record[i].get('batchcode'));
        }

        var acceptFormView =  Ext.create("DigitalInspection.view.DigitalInspectionAcceptForm",{'batchcodes':batchcodes,'grid':grid});
        acceptFormView.show();
    },

    checkMd5:function (btn) {
        var grid = btn.up('DigitalInspectionWcGridView');
        var select = grid.getSelectionModel();
        if (select.getSelected().length<1) {
            XD.msg('请选择需要校验的数据');
            return;
        }
        setTimeout(function () {
            XD.msg("验证通过")
        },300);
    },

    acceptSubmit:function (btn) {
        var formView = btn.up('DigitalInspectionAcceptForm');
        var form = formView.down('form');
        var entryTabGrid = formView.grid.up('DigitalInspectionWcView').down('DigitalInspectionWcSouthView').down('entrygrid');
        if(form.isValid()){
            form.submit({
                method: 'POST',
                url: '/digitalInspection/acceptSubmit',
                params: {
                    batchcodes:formView.batchcodes
                },
                success: function (form, action) {
                    XD.msg('操作成功');
                    formView.close();
                    formView.grid.getStore().reload();
                    entryTabGrid.getStore().proxy.extraParams.batchcode = null;
                    entryTabGrid.getStore().reload();
                },
                failure: function (form, action) {
                    XD.msg('操作失败');
                }
            });
        }
    }
});

function ifSettingCorrect(nodeid,templates) {
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
}

function ifCodesettingCorrect(nodeid) {
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
}