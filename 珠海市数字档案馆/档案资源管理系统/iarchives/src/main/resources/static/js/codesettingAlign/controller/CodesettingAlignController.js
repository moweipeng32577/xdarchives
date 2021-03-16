/**
 * Created by RonJiang on 2018/01/24
 */
// var backupConfirmMsg = '修改数据后，将不能进行还原，建议进行备份，备份需要较长时间，是否备份?';
Ext.define('CodesettingAlign.controller.CodesettingAlignController', {
    extend: 'Ext.app.Controller',
    views: ['CodesettingAlignView','CodesettingAlignResultGridView',
        'CodesettingAlignPromptView','CodesettingAlignFormView',
        'CodesettingAlignTreeView','CodesettingSelectedFormView'],
    models: ['CodesettingAlignTreeModel','CodesettingJsonModel'],
    stores: ['CodesettingAlignTreeStore','CodesettingSelectStore'],
    init : function() {
        var ifShowRightPanel = false;
        var codesettingAlignResultGridView ;
        var count = 0;
        this.control({
            'codesettingAlignTreeView':{
                select: function (treemodel, record) {
                    var codesettingAlignView = treemodel.view.findParentByType('codesettingAlign');
                    var codesettingAlignPromptView = codesettingAlignView.down('codesettingAlignPromptView');
                    if(!ifShowRightPanel){
                        codesettingAlignPromptView.removeAll();
                        codesettingAlignPromptView.add({
                            xtype: 'codesettingAlignFormView'
                        });
                        ifShowRightPanel = true;
                    }
                    var codesettingAlignResultgrid = codesettingAlignView.down('codesettingAlignResultGridView');
                    codesettingAlignResultgrid.nodeid = record.get('fnid');
                    codesettingAlignResultgrid.initGrid({nodeid:codesettingAlignResultgrid.nodeid});
                    var advancedSearchDynamicForm = codesettingAlignView.down('advancedSearchDynamicForm');
                    this.initAdvancedSearchFormField(advancedSearchDynamicForm,record.get('fnid'));
                }
            },
            'codesettingAlignFormView':{
                render:function(field){
                    var topLogicCombo = field.getComponent('topLogicCombo');
                    var bottomLogicCombo = field.getComponent('bottomLogicCombo');
                    topLogicCombo.on('change',function (view) {//点击顶部逻辑下拉选，则同步底部逻辑下拉选的值
                        bottomLogicCombo.setValue(view.lastValue);
                    });
                    bottomLogicCombo.on('change',function (view) {//点击底部逻辑下拉选，则同步顶部逻辑下拉选的值
                        topLogicCombo.setValue(view.lastValue);
                    })
                }
            },
            'codesettingAlignFormView button[itemId=topSearchBtn]':{click:this.doAdvancedSearch},
            'codesettingAlignFormView button[itemId=bottomSearchBtn]':{click:this.doAdvancedSearch},
            'codesettingAlignFormView button[itemId=topClearBtn]':{click:this.doAdvancedSearchClear},
            'codesettingAlignFormView button[itemId=bottomClearBtn]':{click:this.doAdvancedSearchClear},
            'codesettingAlignFormView button[itemId=topCloseBtn]':{click:this.doAdvancedSearchClose},
            'codesettingAlignFormView button[itemId=bottomCloseBtn]':{click:this.doAdvancedSearchClose},
            'codesettingAlignResultGridView button[itemId=codesettingAlignBackId]':{click:this.doAdvancedSearchBack},
            'codesettingAlignResultGridView button[itemId=codesettingAlignShowId]':{
                click:function(btn){
                    var codesettingAlignResultGridView = btn.findParentByType('codesettingAlignResultGridView');
                    var record = codesettingAlignResultGridView.selModel.getSelection();
                    if(record.length==0){
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
                    this.initFormField(form, 'hide', record[0].get('nodeid'));
                    this.initFormData('look',form, entryid);
                }
            },
            'codesettingAlignResultGridView': {
                rowdblclick: function (view,record) {
                    var entryid = record.get('entryid');
                    var form = this.findFormView(view).down('dynamicform');
                    this.initFormField(form, 'hide', record.get('nodeid'));
                    this.initFormData('look',form, entryid);
                }
            },
            'codesettingAlignResultGridView ':{
                eleview: this.activeEleForm
            },
            'EntryFormView [itemId=preBtn]':{
                click:this.preHandler
            },
            'EntryFormView [itemId=nextBtn]':{
                click:this.nextHandler
            },
            'EntryFormView button[itemId=back]':{
                click:function(btn){
                    this.activeGrid(btn);
                }
            },
            'codesettingAlignResultGridView button[itemId=codesettingAlignSettingId]':{//档号设置
                click:this.doCodesetting
            },
            'codesettingSelectedFormView button[itemId=back]':{//档号设置--返回
                click:function (btn) {
                    btn.up('window').hide();
                }
            },
            'codesettingItemSelectedFormView': {
                render: function (field) {
                    field.getComponent("itemselectorID").toField.boundList.on('select', function () {
                        var codesettingSelectedFormView = this.findParentByType('codesettingSelectedFormView');
                        var codesettingDetailFormView = codesettingSelectedFormView.down('[itemId=codesettingDetailFormViewItemID]');
                        var areatextfield = codesettingDetailFormView.down('[itemId=areaid]');
                        var splitcodetextfield = codesettingDetailFormView.down('[itemId=splitcodeid]');
                        var lengthtextfield = codesettingDetailFormView.down('[itemId=lengthid]');
                        var hideidfield = codesettingDetailFormView.down('[itemId=hiddenfieldId]');

                        var temp = this.selModel.selected.items[0].get('fieldcode').split('∪');
                        if (temp[0] == "") {
                            //将从模板中获得的字段传到输入框中
                            areatextfield.setValue(temp[2]);
                            splitcodetextfield.setValue(temp[3]);
                            lengthtextfield.setValue(temp[4]);
                            //把字段全称保存在隐藏域中，输入框修改保存时用到
                            hideidfield.setValue(temp[1]);
                        } else {
                            areatextfield.setValue(temp[1]);
                            splitcodetextfield.setValue(temp[2]);
                            lengthtextfield.setValue(temp[3]);
                            hideidfield.setValue(temp[4]);
                        }
                    });
                }
            },
            'codesettingDetailFormView': {
                render: function (field) {
                    field.getComponent("splitcodeid").on('keyup', function (ob) {
                        var codesettingSelectedFormView = this.findParentByType('codesettingSelectedFormView');
                        var codesettingItemSelectedFormView = codesettingSelectedFormView.down('[itemId=itemselectorItemID]');
                        var codesettingDetailFormView = codesettingSelectedFormView.down('[itemId=codesettingDetailFormViewItemID]');
                        changeToMultiselect(ob, codesettingItemSelectedFormView, codesettingDetailFormView);
                    });
                    field.getComponent("lengthid").on('keyup', function (ob) {
                        var codesettingSelectedFormView = this.findParentByType('codesettingSelectedFormView');
                        var codesettingItemSelectedFormView = codesettingSelectedFormView.down('[itemId=itemselectorItemID]');
                        var codesettingDetailFormView = codesettingSelectedFormView.down('[itemId=codesettingDetailFormViewItemID]');
                        changeToMultiselect(ob, codesettingItemSelectedFormView, codesettingDetailFormView);
                    });
                }
            },
            'codesettingSelectedFormView button[itemId=codesettingSaveBtnId]': {
                click: function (btn) {
                    var codesettingSelectedFormView = btn.findParentByType('codesettingSelectedFormView');
                    var codesettingItemSelectedFormView = codesettingSelectedFormView.down('[itemId=itemselectorItemID]');
                    var tostore = codesettingItemSelectedFormView.getComponent("itemselectorID").toField.boundList.store;
                    if (tostore.getCount() <= 0) {
                        XD.msg("请至少选择一个字段");
                        return;
                    }
                    var recordslist = [];
                    for (var i = 0; i < tostore.getCount(); i++) {
                        recordslist.push(tostore.getAt(i).get('fieldcode'));
                    }
                    Ext.Ajax.request({
                        params: {
                            datanodeid: codesettingSelectedFormView.datanodeid,
                            fieldcodelist: recordslist
                        },
                        url: '/codesetting/setCode',
                        method: 'post',
                        success: function (resp) {
                            XD.msg(Ext.decode(resp.responseText).msg);
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                    btn.up('window').close();
                }
            },
            'codesettingSelectedFormView button[itemId=close]':{
                click:function (btn) {
                    btn.up('window').close();
                }
            },
            'codesettingAlignResultGridView button[itemId=codesettingAlignAlignId]':{//档号对齐
                click:this.doCodesettingAlign
            }
        });
    },
    //获取档号对齐应用视图
    findView: function (btn) {
        return btn.findParentByType('codesettingAlign');
    },
    //获取检索表单界面视图
    findSearchformView: function (btn) {
        return this.findView(btn).down('[itemId=formview]');
    },
    //获取查看动态表单界面视图
    findFormView: function (btn) {
        return this.findView(btn).down('EntryFormView');
    },

    //获取列表界面视图
    findGridView: function (btn) {
        return this.findView(btn).down('codesettingAlignResultGridView');
    },
    //切换到列表界面视图
    activeGrid: function (btn,flag) {
        var view = this.findView(btn);
        view.setActiveItem(this.findGridView(btn));
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
            var grid = this.findGridView(btn);
            grid.notResetInitGrid();
        }
    },
    //切换到检索表单界面视图
    activeSearchform: function (btn) {
        var view = this.findView(btn);
        var Searchformview = this.findSearchformView(btn);
        view.setActiveItem(Searchformview);
        return Searchformview;
    },
    //切换到查看动态表单界面视图
    activeForm: function (btn) {
        var view = this.findView(btn);
        var formview = this.findFormView(btn);
        view.setActiveItem(formview);
        formview.items.get(0).enable();
        formview.setActiveTab(0);
        return formview;
    },
    activeEleForm:function(obj){
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
        formview.setActiveTab(1);
        return formview;
    },

    doAdvancedSearch:function (btn){//分类检索页面查询方法
        /*查询参数处理*/
        var form = this.findView(btn).down('codesettingAlignFormView');
        var filedateStartField = form.getForm().findField('filedatestart');
        var datasoure = form.down('[itemId=datasoureId]').getChecked()[0].inputValue;
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
        for(var name in formValues){//遍历表单中的所有值
            formParams[name] = formValues[name];
            if(typeof(formValues[name]) != "undefined" && formValues[name] != '' && formValues[name] != 'and' &&
                formValues[name] != 'like' && formValues[name] != 'equal' && formValues[name] != 'or'){
                fieldColumn.push(name);
            }
        }
        var grid = this.findView(btn).down('codesettingAlignResultGridView');
        formParams.datasoure=datasoure;
        formParams.nodeid = grid.nodeid;
        //点击非叶子节点时，是否查询出其包含的所有叶子节点数据
        formParams.ifSearchLeafNode = false;
        //点击非叶子节点时，是否查询出当前非叶子节点及其包含的所有非叶子节点数据
        formParams.ifContainSelfNode = false;
        /*切换至列表界面*/
        Ext.Array.each(grid.getColumns(), function(item){
            if(item.xtype == 'gridcolumn'){
                item.renderer = function(value){
                    return value;
                }
            }
            var columnValue = formParams[item.dataIndex];
            if ($.inArray(item.dataIndex,fieldColumn)!=-1) {
                var searchstrs=[];
                searchstrs.push(columnValue);
                item.renderer = function (v) {
                    if(typeof(v) != "undefined"){
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
        this.activeGrid(btn);
        /*加载页面*/
        if(fieldColumn.length==0){
            grid.initGrid(formParams)
        }else{
            var store = grid.getStore();
            grid.dataParams = formParams;
            Ext.apply(store.getProxy(),{
                extraParams:formParams
            });
            store.loadPage(1);
        }
    },
    doAdvancedSearchClear:function(btn){//清除检索条件页面所有控件的输入值
        this.findView(btn).down('codesettingAlignFormView').getForm().reset();//表单重置
    },
    doAdvancedSearchClose:function(){
        parent.closeObj.close(parent.layer.getFrameIndex(window.name));
    },
    doAdvancedSearchBack:function(btn){//返回检索条件输入页面
        var grid = this.findView(btn).down('codesettingAlignResultGridView');
        grid.getStore().proxy.extraParams = {};//清空参数内容
        /*检索条件重置*/
        var conditionCombo = grid.down('[itemId=condition]');
        var conditionStore = conditionCombo.getStore();
        if (conditionStore.getCount() > 0) {
            conditionCombo.select(conditionStore.getAt(0));
        }
        /*检索操作符重置*/
        var operatorCombo = grid.down('[itemId=operator]');
        var operatorStore = operatorCombo.getStore();
        if (operatorStore.getCount() > 0) {
            operatorCombo.select(operatorStore.getAt(0));
        }
        /*检索内容重置*/
        var searchfield = grid.down('[itemId=value]');
        searchfield.reset();
        this.activeSearchform(btn);
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

    getCurrentCodesettingAlignform:function (btn) {
        return btn.up('EntryFormView');
    },

    //点击上一条
    preHandler:function(btn){
        var currentCodesettingAlignform = this.getCurrentCodesettingAlignform(btn);
        var form = currentCodesettingAlignform.down('dynamicform');
        this.refreshFormData(form, 'pre');
    },

    //点击下一条
    nextHandler:function(btn){
        var currentCodesettingAlignform = this.getCurrentCodesettingAlignform(btn);
        var form = currentCodesettingAlignform.down('dynamicform');
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
        this.activeForm(form);
        Ext.Ajax.request({
            method:'GET',
            scope:this,
            url:'/management/entries/'+entryid,
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

    doCodesettingAlign:function (btn) {//档号对齐（对所有检索出的数据进行操作）
        var grid = this.findView(btn).down('codesettingAlignResultGridView');
        var gridStore = grid.getStore();
        if(gridStore.data.length==0){
            XD.msg('没有可操作数据');
            return;
        }
        var alignConfirmMsg = '本次操作将依据档号设置中设定的单位长度，在所有检索结果的相应字段前面补0至指定长度（单位长度设置为0的字段不作处理），是否继续？';
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

    doCodesetting:function (btn) {
        var resultgrid = this.findGridView(btn);
        var codesettingAlignSettingWin = Ext.create('Ext.window.Window',{
            width:'100%',
            height:'100%',
            title:'档号设置',
            header:false,
            draggable : false,//禁止拖动
            resizable : false,//禁止缩放
            modal:true,
            // closeToolText:'关闭',
            layout:'fit',
            items:[{
                xtype: 'codesettingSelectedFormView',
                datanodeid:resultgrid.nodeid
            }]
        });
        var codesettingSelectedFormView = codesettingAlignSettingWin.down('codesettingSelectedFormView');
        var detailformview = codesettingSelectedFormView.down('[itemId=codesettingDetailFormViewItemID]');
        detailformview.down('[itemId=areaid]').reset();//清空值
        detailformview.down('[itemId=splitcodeid]').reset();
        detailformview.down('[itemId=lengthid]').reset();
        detailformview.down('[itemId=hiddenfieldId]').reset();
        var itemselectorView = codesettingSelectedFormView.down('[itemId=itemselectorID]');
        itemselectorView.store.proxy.extraParams = {datanodeid: codesettingSelectedFormView.datanodeid};
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
        codesettingAlignSettingWin.show();
    },

    initAdvancedSearchFormField:function(form, nodeid){
        if (form.nodeid != nodeid) {//切换节点后，form和tree的节点id不相等
            form.nodeid = nodeid;//用左侧树节点的id初始化form的nodeid参数
            form.removeAll();//移除form中的所有表单控件
            var formField = form.getFormField();//根据节点id查询表单字段
            if(formField.length==0){
                XD.msg('请检查模板设置信息是否正确');
                return;
            }
            form.templates = formField;
            form.initSearchConditionField(formField);//重新动态添加表单控件
        }
        return '加载表单控件成功';
    }
});

function changeToMultiselect(variable, SelectedFormView, DetailFormView) {
    if (variable.getName() == 'splitcodetext') {
        if (!validaSplitCode(variable)) {
            return;
        }
    } else {
        if (!validaLength(variable)) {
            return;
        }
    }

    var boundlist = SelectedFormView.getComponent("itemselectorID").toField.boundList;
    var tostore = boundlist.store;
    var records = [];
    var hiddenvalue = DetailFormView.down('[itemId=hiddenfieldId]').getValue();
    if (tostore.getCount() > 0) {
        for (var i = 0; i < tostore.getCount(); i++) {
            var record = tostore.getAt(i);
            var num = tostore.indexOf(record);
            var temp = record.data.fieldcode.split('∪');
            if (hiddenvalue == temp[1] || hiddenvalue == temp[4]) {
                var changeValue = insertChange(variable.getValue(), record.data.fieldcode, variable.getName());
                record.data.fieldcode = changeValue;   //要改变提交到后台的值
                records.push(record);
                tostore.remove(record);
                tostore.insert(num, records);
                records = [];
            }
        }
    }
}

function validaSplitCode(splitcode) {
    var str = splitcode.getValue();
    if (str.length > 1) {
        XD.msg("只能输入一个符号!");
        splitcode.setValue('');
        return false;
    }
    // var reg = /~|!|@|#|%|_|-|=|\*|\.|\+|\?|\||·/;
    var reg = /_|-|=|(|)|\*|\.|\||·/;
    if (str.match(reg) == null) {
        if (str != "") {
            // XD.msg("本系统只支持以下分割符号：~ ! @ # % _ - = * . + ? | ·");
            XD.msg("支持分割符号为：&nbsp;&nbsp;_&nbsp;&nbsp;-&nbsp;&nbsp;=&nbsp;&nbsp;*&nbsp;&nbsp;.&nbsp;&nbsp;|&nbsp;&nbsp;·");
            splitcode.setValue('');
        } else {
            XD.msg("[分割符号]不能为空!");
        }
        return false;
    } else {
        return true;
    }
}

function validaLength(length) {
    var str = length.getValue();
    var reg = new RegExp("^([0-9])$");
    if (!reg.test(str)) {
        XD.msg("请输入0到9的一位数字");
        length.setValue('');
        return false;
    } else {
        return true;
    }
}

function insertChange(str, changeValue, isSign) {
    var temp = changeValue.split("∪");
    var haveChange = temp[0] + "∪";
    if (temp[0] == "") {
        if (isSign == 'splitcodetext')//分割符号的改变
            temp[3] = str;
        else//单位长度的改变
            temp[4] = str;
        for (var i = 1; i < temp.length; i++) {
            if (i != temp.length - 1)
                haveChange = haveChange + temp[i] + "∪";
            else
                haveChange = haveChange + temp[i];
        }
    } else {
        //从数据库中获得的字段
        if (isSign == 'splitcodetext')//分割符号的改变
            temp[2] = str;
        else//单位长度的改变
            temp[3] = str;
        for (var i = 1; i < temp.length; i++) {
            if (i != temp.length - 1)
                haveChange = haveChange + temp[i] + "∪";
            else
                haveChange = haveChange + temp[i];
        }
    }
    return haveChange
}

function alignCodesetting(btn) {
    var grid = btn.up('codesettingAlign').down('codesettingAlignResultGridView');
    var allSearchParams = grid.getStore().proxy.extraParams;
    Ext.Msg.wait('正在进行档号对齐，请耐心等待……', '正在操作');

    var columnArray = [];
    var columns = grid.columnManager.getColumns();
    for (var j = 0; j < columns.length; j++) {
        if (columns[j].xtype == 'gridcolumn') {
            var subtext = columns[j].dataIndex + '-' + columns[j].text;
            columnArray.push(subtext);
        }
    }
    allSearchParams['columnArray'] = columnArray;
    var downloadForm = document.createElement('form');
    document.body.appendChild(downloadForm);
    var inputTextElement;
    for (var prop in allSearchParams){
        inputTextElement = document.createElement('input');
        inputTextElement.name = prop;
        inputTextElement.value = allSearchParams[prop];
        downloadForm.appendChild(inputTextElement);
    }
    downloadForm.action = '/codesettingAlign/export';
    downloadForm.submit();
    Ext.Ajax.setTimeout(XD.timeout);
    Ext.Ajax.request({
        scope:this,
        url: '/codesettingAlign/align',
        params:allSearchParams,
        sync : true,
        success: function (response) {
        	Ext.MessageBox.hide();
            XD.msg(Ext.decode(response.responseText).msg);
            grid.notResetInitGrid();
        }
    });
}