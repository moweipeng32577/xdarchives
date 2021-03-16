/**
 * Created by RonJiang on 2018/2/27 0027
 */
Ext.define('Report.controller.ReportController', {
    extend: 'Ext.app.Controller',
    views: ['ReportView','ReportPromptView','ReportSxPromptView',
        'ReportGridView','ReportSxGridView','ReportFormView','ReportSxFormView',
        'ReportTreeComboboxView','ElectronicView'],
    stores: ['ReportTreeStore','ReportSxTreeStore','ReportGridStore'],
    models: ['ReportTreeModel','ReportGridModel'],
    init: function () {
        var ifShowRightPanel = false;
        var ifSxShowRightPanel = false;
        this.control({
            'report [itemId=reportTreeId]':{
                select: function (treemodel, record) {
                    var reportView = treemodel.view.findParentByType('report');
                    var reportPromptView = reportView.down('reportPromptView');
                    if(!ifShowRightPanel){
                        reportPromptView.removeAll();
                        reportPromptView.add({
                            xtype: 'reportGridView'
                        });
                        ifShowRightPanel = true;
                    }
                    var reportgrid = reportPromptView.down('[itemId=reportGridViewID]');
                    reportgrid.setTitle("当前位置：" + record.get('text'));
                    var nodeid = record.get('fnid');
                    reportgrid.nodeid = nodeid;
                    window.nodeid=nodeid;
                    // var setReportFieldBtn = reportgrid.down('[itemId=setReportField]');//设置报表字段
                    // var setOrderFieldBtn = reportgrid.down('[itemId=setOrderField]');//设置排序字段
                    // var reportDataSourceBtn = reportgrid.down('[itemId=reportDataSource]');//报表数据源
                    // var tbseparator = reportgrid.down('toolbar').query('tbseparator');
                    // if (record.data.text!='其它报表' && record.data.text!='自定义报表') {
                    //     setReportFieldBtn.show();
                    //     setOrderFieldBtn.show();
                    //     reportDataSourceBtn.hide();
                    //     if(tbseparator.length>0){
                    //         tbseparator[tbseparator.length-1].hide();
                    //         tbseparator[tbseparator.length-2].show();
                    //         tbseparator[tbseparator.length-3].show();
                    //     }
                    // }else{
                    //     setReportFieldBtn.hide();
                    //     setOrderFieldBtn.hide();
                    //     reportDataSourceBtn.show();
                    //     if(tbseparator.length>0){
                    //         tbseparator[tbseparator.length-1].show();
                    //         tbseparator[tbseparator.length-2].hide();
                    //         tbseparator[tbseparator.length-3].hide();
                    //     }
                    // }
                    var fullname=record.get('text');
                    while(record.parentNode.get('text')!='报表管理'){
                        fullname=record.parentNode.get('text')+'_'+fullname;
                        record=record.parentNode;
                    }
                    reportgrid.nodefullname = fullname;
                    window.xtType=reportView.activeTab.title;
                    reportgrid.initGrid({xtType:window.xtType,nodeid:nodeid});
                }
            },
            'report [itemId=reportSxTreeId]':{
                select: function (treemodel, record) {
                    var reportView = treemodel.view.findParentByType('report');
                    var reportPromptView = reportView.down('reportSxPromptView');
                    if(!ifSxShowRightPanel){
                        reportPromptView.removeAll();
                        reportPromptView.add({
                            xtype: 'reportSxGridView'
                        });
                        ifSxShowRightPanel = true;
                    }
                    var reportgrid = reportPromptView.down('[itemId=reportSxGridViewID]');
                    reportgrid.setTitle("当前位置：" + record.get('text'));
                    var nodeid = record.get('fnid');
                    reportgrid.nodeid = nodeid;
                    window.sxnodeid=nodeid;
                    var fullname=record.get('text');
                    while(record.parentNode.get('text')!='报表管理'){
                        fullname=record.parentNode.get('text')+'_'+fullname;
                        record=record.parentNode;
                    }
                    reportgrid.nodefullname = fullname;
                    window.xtType=reportView.activeTab.title;
                    reportgrid.initGrid({xtType:window.xtType,nodeid:nodeid});
                }
            },
            'report':{
                tabchange:function(view){
                    if(view.activeTab.title == '档案系统'){
                        window.xtType='档案系统';
                        if(window.nodeid){//重新加载表单
                            var reportgrid=view.down('[itemId=reportGridViewID]');
                            reportgrid.initGrid({xtType:window.xtType,nodeid:window.nodeid});
                        }

                    }else if(view.activeTab.title == '声像系统'){
                        window.xtType='声像系统';
                        if(window.sxnodeid){
                            var reportgrid=view.down('[itemId=reportSxGridViewID]');
                            reportgrid.initGrid({xtType:window.xtType,nodeid:window.sxnodeid});
                        }
                    }
                }
            },
            'reportGridView button[itemId=save]':{//增加
                click:this.saveHandler
            },
            'reportGridView button[itemId=modify]':{//修改
                click:this.modifyHandler
            },
            'reportGridView button[itemId=del]':{//删除
                click:this.delHandler
            },
            'reportGridView button[itemId=look]':{//查看
                click:this.lookHandler
            },
            'reportSxGridView button[itemId=save]':{//增加
                click:this.saveSxHandler
            },
            'reportSxGridView button[itemId=modify]':{//修改
                click:this.modifySxHandler
            },
            'reportSxGridView button[itemId=del]':{//删除
                click:this.delSxHandler
            },
            'reportSxGridView button[itemId=look]':{//查看
                click:this.lookSxHandler
            },
            'reportform':{
                afterrender:this.addKeyAction
            },
            'reportform button[itemId=save]':{//保存
                click: this.submitForm
            },
            'reportform button[itemId=continuesave]':{//连续保存
                click: this.continueSubmitForm
            },
            'reportform button[itemId=back]':{
                click:function (btn) {
                    window.reportWin.close();
                }
            },'reportSxform':{
                afterrender:this.addKeyAction
            },
            'reportSxform button[itemId=save]':{//保存
                click: this.submitForm
            },
            'reportSxform button[itemId=continuesave]':{//连续保存
                click: this.continueSubmitForm
            },
            'reportSxform button[itemId=back]':{
                click:function (btn) {
                    window.reportWin.close();
                }
            },
            'reportGridView button[itemId=reportStyleFileManage]': {//报表样式管理
                click: this.reportStyleFileManage
            },
            'reportSxGridView button[itemId=reportStyleFileManage]': {//报表样式管理
                click: this.reportStyleFileManage
            },
            'reportform button[itemId=electronUpId]':{
                click: this.reportStyleFile
            }
        });
    },

    //获取报表管理应用视图
    findView: function (btn) {
        return btn.up('report');
    },

    //获取表单界面视图
    findFormView: function (btn) {
        return this.findView(btn).down('reportform');
    },
    //获取声像表单界面视图
    findSxFormView: function (btn) {
        return this.findView(btn).down('reportSxform');
    },

    //获取列表界面视图
    findGridView: function (btn) {
        return this.findView(btn).down('[itemId=treegridview]');
    },
    //获取声像列表界面视图
    findSxGridView: function (btn) {
        return this.findView(btn).down('[itemId=sxtreegridview]');
    },

    //点击查看、著录、修改时设置表单中控件只读属性
    formStateChange:function (form,operate) {
        var fields = form.getForm().getFields().items;
        if(operate == 'look'){
            Ext.each(fields,function (item) {
                item.setReadOnly(true);//设置查看表单中控件属性为只读
            });
        }else{
            Ext.each(fields,function (item) {
                item.setReadOnly(false);//设置查看表单中控件属性为非只读
            });
        }
    },

    changeBtnStatus:function(form, operate){
        var savebtn = form.down('[itemId=save]');
        var continuesave = form.down('[itemId=continuesave]');
        var tbseparator = form.getDockedItems('toolbar')[0].query('tbseparator');
        if(operate == 'look'){//查看时隐藏保存及连续录入按钮
            savebtn.setVisible(false);
            continuesave.setVisible(false);
            tbseparator[0].setVisible(false);
            tbseparator[1].setVisible(false);
        }else if(operate == 'modify'){//修改时隐藏连续录入按钮
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
        var form = this.findFormView(btn);
        var treegrid = this.findGridView(btn);
        if(window.xtType=='声像系统'){
            form = this.findSxFormView(btn);
            treegrid = this.findSxGridView(btn);
            view=view.down('[itemId=sxxtId]');
        }else{
            view=view.down('[itemId=daxtId]');
        }
        view.setActiveItem(treegrid);
        form.saveBtn = undefined;
        form.continueSaveBtn = undefined;
        if(flag){//根据参数确定是否需要刷新数据
            var grid = treegrid.down('reportGridView');
            if(window.xtType=='声像系统'){
                grid = treegrid.down('reportSxGridView');
            }
            grid.notResetInitGrid({xtType:window.xtType,nodeid:form.nodeid});
        }
    },

    //切换到表单界面视图
    activeForm: function (btn) {
        var view = this.findView(btn);
        var formview = this.findFormView(btn);
        if(window.xtType=='声像系统'){
            formview = this.findSxFormView(btn);
            view=view.down('[itemId=sxxtId]');
        }else{
            view=view.down('[itemId=daxtId]');
        }
        view.setActiveItem(formview);
        return formview;
    },

    initFormField:function(form, nodeid){
        if(form.nodeid!=nodeid){
            form.nodeid = nodeid;
        }
    },

    initFormData:function(operate, form, gridtype ,reportid){
        var publicReport = form.down('[itemId=publicReport]');
        var privateReport = form.down('[itemId=privateReport]');
        var nodeName = form.down('[itemId=nodeName]');
        form.reset();
        nodeName.setRawValue('');//form的reset方法无法重置pickerfield控件的值
        // this.activeForm(form);
        var title = "新增报表";
        if(operate=='modify'){
            title = "修改报表"
        }else if(operate=='look'){
            title = "查看报表"
        }
        var reportWin = Ext.create('Ext.window.Window',{
            width: '70%',
            height: '60%',
            title:title,
            layout:'fit',
            modal:true,
            draggable: false,//禁止拖动
            resizable: false,//禁止缩放
            closeToolText:'关闭',
            closeAction:'hide',
            items:[{
                xtype: form.xtype,
                nodeid:form.nodeid,
                reportid:reportid
            }]
        });
        window.reportWin = reportWin;
        window.gridtype = gridtype;
        window.formtype = form.xtype;
        reportWin.show();
        var reprotform = reportWin.down(form.xtype);
        reprotform.nodeid = form.nodeid;
        form = reprotform;
        var fields = form.getForm().getFields().items;
        if(operate!='look'){
            Ext.each(fields,function (item) {
                if(!item.freadOnly){
                    item.setReadOnly(false);
                }
            });
        }else{
            reprotform.down('[itemId=electronUpId]').hide();
        	Ext.each(fields,function (item) {
                item.setReadOnly(true);
            });
        }
        if(typeof(reportid) != 'undefined'){
            Ext.Ajax.request({
                method: 'GET',
                scope: this,
                url: '/report/reports/' + reportid+'?xtType='+window.xtType,
                success: function (response) {
                    var report = Ext.decode(response.responseText);
                    if (operate == 'add') {
                        delete report.reportid;
                        delete report.filename;
                        window.reportWin.items.items[0].reportid='';
                        window.wmedia='';
                    }
                    form.loadRecord({getData: function () {return report;}});

                    // var record;
                    // var store = nodeName.store;
                    // var rec = store.getCount();
                    // for (var i = 0; i < rec; i++) {
                    //     if (store.getAt(i).data.fnid == report.nodeid) {
                    //         record = store.getAt(i);
                    //     }
                    // }
                    // var treepanal = nodeName.picker;
                    // var treepanal = nodeName.createPicker();
                    // var treeview = treepanal.view;
                    // var selectModel = treeview.getSelectionModel();
                    // selectModel.select(record); //下拉树默认选择。

                    if (report.reporttype == '公有报表') {
                        publicReport.setValue(true);
                    }
                    if (report.reporttype == '私有报表') {
                        privateReport.setValue(true);
                    }
                }
            });
        }else{
            privateReport.setValue(true);
        }
        this.formStateChange(form,operate);
        this.changeBtnStatus(form,operate);
    },

    saveHandler:function (btn) {//增加报表
        var form = this.findFormView(btn);
        form.saveBtn = form.down('[itemId=save]');
        form.continueSaveBtn = form.down('[itemId=continuesave]');
        form.operateFlag = 'add';
        var grid = this.findView(btn).down('reportGridView');
        var tree = this.findView(btn).down('[itemId=reportTreeId]');
        var selectCount = grid.selModel.getSelection().length;
        var node = tree.selModel.getSelected().items[0];
        if (!node) {//若点击增加时左侧未选中任何节点，则提示选择节点
            XD.msg('请选择节点');
            return;
        }
        this.initFormField(form, node.get('fnid'));
        if (selectCount == 0) {
            this.initFormData('add',form, grid);
            var fullname = node.get('text');
            while (!node.parentNode.isRoot()) {
                fullname = node.parentNode.get('text') + '_' + fullname;
                node = node.parentNode;
            }
            window.reportWin.down(window.formtype).items.get('nodeName').setValue(fullname);
        }else if(selectCount!=1){
            XD.msg('只能选择一条数据');
        } else {
            //选择数据增加，则加载当前数据到表单界面
            this.initFormData('add',form, grid, grid.selModel.getSelection()[0].get('reportid'));
        }
    },
    saveSxHandler:function (btn) {//增加报表
        var form = this.findSxFormView(btn);
        form.saveBtn = form.down('[itemId=save]');
        form.continueSaveBtn = form.down('[itemId=continuesave]');
        form.operateFlag = 'add';
        var grid = this.findView(btn).down('reportSxGridView');
        var tree = this.findView(btn).down('[itemId=reportSxTreeId]');
        var selectCount = grid.selModel.getSelection().length;
        var node = tree.selModel.getSelected().items[0];
        if (!node) {//若点击增加时左侧未选中任何节点，则提示选择节点
            XD.msg('请选择节点');
            return;
        }
        this.initFormField(form, node.get('fnid'));
        if (selectCount == 0) {
            this.initFormData('add',form ,grid);
            var fullname = node.get('text');
            while (!node.parentNode.isRoot()) {
                fullname = node.parentNode.get('text') + '_' + fullname;
                node = node.parentNode;
            }
            window.reportWin.down(window.formtype).items.get('nodeName').setValue(fullname);
        }else if(selectCount!=1){
            XD.msg('只能选择一条数据');
        } else {
            //选择数据增加，则加载当前数据到表单界面
            this.initFormData('add',form, grid, grid.selModel.getSelection()[0].get('reportid'));
        }
    },

    modifyHandler:function (btn) {//修改报表
        var form = this.findFormView(btn);
        form.saveBtn = form.down('[itemId=save]');
        form.continueSaveBtn = form.down('[itemId=continuesave]');
        form.operateFlag = 'modify';
        var grid = this.findView(btn).down('reportGridView');
        var record = grid.selModel.getSelection();
        var tree = this.findView(btn).down('[itemId=reportTreeId]');
        var node = tree.selModel.getSelected().items[0];
        if (!node) {//若点击修改时左侧未选中任何节点，则提示选择节点
            XD.msg('请选择节点');
            return;
        }
        if (record.length != 1) {
            XD.msg('请选择一条需要修改的数据');
            return;
        }
        this.initFormField(form, record[0].get('nodeid'));
        this.initFormData('modify', form, grid ,record[0].get('reportid'));
    },
    modifySxHandler:function (btn) {//修改报表
        var form = this.findSxFormView(btn);
        form.saveBtn = form.down('[itemId=save]');
        form.continueSaveBtn = form.down('[itemId=continuesave]');
        form.operateFlag = 'modify';
        var grid = this.findView(btn).down('reportSxGridView');
        var record = grid.selModel.getSelection();
        var tree = this.findView(btn).down('[itemId=reportSxTreeId]');
        var node = tree.selModel.getSelected().items[0];
        if (!node) {//若点击修改时左侧未选中任何节点，则提示选择节点
            XD.msg('请选择节点');
            return;
        }
        if (record.length != 1) {
            XD.msg('请选择一条需要修改的数据');
            return;
        }
        this.initFormField(form, record[0].get('nodeid'));
        this.initFormData('modify', form, grid, record[0].get('reportid'));
    },

    delHandler:function (btn) {//删除报表
        var grid = this.findView(btn).down('reportGridView');
        var record = grid.selModel.getSelection();
        var tree = this.findView(btn).down('[itemId=reportTreeId]');
        var node = tree.getSelectionModel().getSelected().items[0];
        if (!node) {
            XD.msg('请选择节点');
            return;
        }
        if (record.length == 0) {
            XD.msg('请至少选择一条需要删除的数据');
            return;
        }
        XD.confirm('确定要删除这' + record.length + '条数据吗',function(){
            var tmp = [];
            for (var i = 0; i < record.length; i++) {
                tmp.push(record[i].get('reportid'));
            }
            var reportids = tmp.join(',');
            Ext.Ajax.request({
                method: 'DELETE',
                url: '/report/reports/' + reportids+'?xtType='+window.xtType,
                success: function (response) {
                    XD.msg(Ext.decode(response.responseText).msg);
                    grid.delReload(record.length);
                }
            })
        },this);
    },
    delSxHandler:function (btn) {//删除报表
        var grid = this.findView(btn).down('reportSxGridView');
        var record = grid.selModel.getSelection();
        var tree = this.findView(btn).down('[itemId=reportSxTreeId]');
        var node = tree.getSelectionModel().getSelected().items[0];
        if (!node) {
            XD.msg('请选择节点');
            return;
        }
        if (record.length == 0) {
            XD.msg('请至少选择一条需要删除的数据');
            return;
        }
        XD.confirm('确定要删除这' + record.length + '条数据吗',function(){
            var tmp = [];
            for (var i = 0; i < record.length; i++) {
                tmp.push(record[i].get('reportid'));
            }
            var reportids = tmp.join(',');
            Ext.Ajax.request({
                method: 'DELETE',
                url: '/report/reports/' + reportids+'?xtType='+window.xtType,
                success: function (response) {
                    XD.msg(Ext.decode(response.responseText).msg);
                    grid.delReload(record.length);
                }
            })
        },this);
    },

    lookHandler: function (btn) {//查看报表
        var grid = this.findView(btn).down('reportGridView');
        var record = grid.selModel.getSelection();
        var tree = this.findView(btn).down('[itemId=reportTreeId]');
        var node = tree.getSelectionModel().getSelected().items[0];
        if (!node) {
            XD.msg('请选择节点');
            return;
        }
        if (record.length != 1) {
            XD.msg('请选择一条需要查看的数据');
            return;
        }
        var reportid = record[0].get('reportid');
        var form = this.findFormView(btn);
        this.initFormField(form, node.get('fnid'));
        this.initFormData('look',form, grid ,reportid);
    },
    lookSxHandler: function (btn) {//查看报表
        var grid = this.findView(btn).down('reportSxGridView');
        var record = grid.selModel.getSelection();
        var tree = this.findView(btn).down('[itemId=reportSxTreeId]');
        var node = tree.getSelectionModel().getSelected().items[0];
        if (!node) {
            XD.msg('请选择节点');
            return;
        }
        if (record.length != 1) {
            XD.msg('请选择一条需要查看的数据');
            return;
        }
        var reportid = record[0].get('reportid');
        var form = this.findSxFormView(btn);
        this.initFormField(form, node.get('fnid'));
        this.initFormData('look',form, grid, reportid);
    },

    //监听键盘按下事件
    addKeyAction:function (view) {
        var controller = this;
        view.saveBtn = view.down('[itemId=save]');
        view.continueSaveBtn = view.down('[itemId=continuesave]');
        document.onkeydown = function () {
            var oEvent = window.event;
            if (oEvent.ctrlKey && oEvent.shiftKey && !oEvent.altKey && oEvent.keyCode == 83) { //这里只能用alt，shift，ctrl等去组合其他键event.altKey、event.ctrlKey、event.shiftKey 属性
                // XD.msg('Ctrl+Shift+S');
                Ext.defer(function () {
                    if(view.continueSaveBtn && view.operateFlag=='add'){//此处增加operateFlag判断的目的是：屏蔽修改界面连续录入快捷键功能
                        controller.continueSubmitForm(view.continueSaveBtn);//连续录入
                    }
                },1);
                event.returnValue = false;//阻止event的默认行为
            }
            if (oEvent.ctrlKey && !oEvent.shiftKey && !oEvent.altKey && oEvent.keyCode == 83) { //这里只能用alt，shift，ctrl等去组合其他键event.altKey、event.ctrlKey、event.shiftKey 属性
                // XD.msg('Ctrl+S');
                Ext.defer(function () {
                    if(view.saveBtn && view.operateFlag){//此处若不增加operateFlag判断，点击树节点后初次渲染reportform时，按下ctrl+s会调用此方法
                        controller.submitForm(view.saveBtn);//保存
                    }
                },1);
                event.returnValue = false;//阻止event的默认行为
                // return false;//阻止event的默认行为
            }
        }
    },

    //保存表单数据，返回列表界面视图
    submitForm: function (btn) {
        var grid = window.gridtype;
        var form = window.reportWin.down(window.formtype);
        //var reportParamCounts = form.down('[itemId=reportParamCounts]').getValue();
        // if(isNaN(reportParamCounts)||parseInt(reportParamCounts)<0||reportParamCounts%1!=0){
        //     XD.msg('报表参数个数输入格式不正确，请输入合法的正整数');
        //     return;
        // }
        var nodeName = form.down('[itemId=nodeName]');
        var nodename = nodeName.rawValue;
        var params={
            eleid:window.wmedia,
            modul:grid.nodefullname,
            xtType:window.xtType,
            nodeid: nodeName.nodeid == ''? form.nodeid:nodeName.nodeid, //是否在表单中选择节点（yes:节点中的nodeid, no:原表中的nodeid）
            realnodename:nodename
        };
        form.submit({
            method: 'POST',
            url: '/report/reports',
            params: params,
            scope: this,
            success: function (formm, action) {
                //切换到列表界面,同时刷新列表数据
                window.reportWin.close();
                form.saveBtn = undefined;
                form.continueSaveBtn = undefined;
                grid.notResetInitGrid({xtType:window.xtType,nodeid:form.nodeid});
                XD.msg(action.result.msg);
            },
            failure: function () {
                XD.msg('操作失败');
            }
        });
    },

    //连续保存
    continueSubmitForm: function (btn) {
        var grid = window.gridtype;
        var form = window.reportWin.down(window.formtype);
        var nodeid = form.nodeid;
        // var reportParamCounts = form.down('[itemId=reportParamCounts]').getValue();
        // if(isNaN(reportParamCounts)||parseInt(reportParamCounts)<0||reportParamCounts%1!=0){
        //     XD.msg('报表参数个数输入格式不正确，请输入合法的正整数');
        //     return;
        // }
        var nodeName = form.down('[itemId=nodeName]');
        var nodename = nodeName.rawValue;
        var params={
            eleid:window.wmedia,
            modul:grid.nodefullname,
            nodeid: nodeid,
            xtType:window.xtType,
            realnodename:nodename
        };
        form.submit({
            method: 'POST',
            url: '/report/reports',
            params: params,
            scope: this,
            success: function (form, action) {
                //每次点击连续录入时刷新列表
                grid.notResetInitGrid({xtType:window.xtType,nodeid:nodeid});
                window.reportWin.items.items[0].reportid='';
                window.wmedia='';
                window.reportWin.down(window.formtype).down('[itemId=media]').setValue('');
                XD.msg(action.result.msg);
                //点击连续录入后，遍历表单中所有控件，将光标移动至第一个非隐藏的控件
                var fields = form.getFields().items;
                for(var i=0;i<fields.length;i++){
                    if(fields[i].xtype!='hidden' && fields[i].xtype!='displayfield'){
                        fields[i].focus(true);
                        if(fields[i].getValue()!=null){
                            Ext.defer(function () {
                                fields[i].selectText(0,fields[i].getValue().length);
                            },1);
                        }
                        break;
                    }
                }
            },
            failure: function () {
                XD.msg('操作失败');
            }
        });
    },

    //报表样式管理
    reportStyleFileManage:function (btn) {
        var grid = this.findView(btn).down('reportGridView');
        var tree = this.findView(btn).down('[itemId=reportTreeId]');
        if(window.xtType=='声像系统'){
            grid = this.findView(btn).down('reportSxGridView');
            tree = this.findView(btn).down('[itemId=reportSxTreeId]');
        }
        var record = grid.selModel.getSelection();
        var node = tree.getSelectionModel().getSelected().items[0];
        if (!node) {
            XD.msg('请选择节点');
            return;
        }
        if (record.length != 1) {
            XD.msg('请选择一条数据');
            return;
        }
        var reportid = record[0].get('reportid');
        var reportUploadWindow = Ext.create("Ext.window.Window", {
            width: '70%',
            height: '60%',
            title: '管理报表样式文件',
            modal: true,
            draggable: false,//禁止拖动
            resizable: false,//禁止缩放
            closeToolText:'关闭',
            layout: 'fit',
            items: [{
                xtype: 'electronicRep',
                reportid: reportid
            }]
        });
        window.reportUploadWindows = reportUploadWindow;
        Ext.on('resize', function (a, b) {
            window.reportUploadWindows.setPosition(0, 0);
            window.reportUploadWindows.fitContainer();
        });
        var treeStore = reportUploadWindow.down('treepanel').getStore();
        Ext.defer(function () {
            treeStore.proxy.extraParams = {xtType:window.xtType,reportid:reportid};
            treeStore.reload();
        },300);
        reportUploadWindow.show();
    },

    reportStyleFile:function (view){
        var reportid = window.reportWin.items.items[0].reportid;
        var reportUploadWindow = Ext.create("Ext.window.Window", {
            width: '70%',
            height: '60%',
            title: '管理报表样式文件',
            modal: true,
            draggable: false,//禁止拖动
            resizable: false,//禁止缩放
            closeToolText:'关闭',
            closeAction: 'hide',
            layout: 'fit',
            items: [{
                xtype: 'electronicRep',
                reportid: reportid
            }],
            listeners :{
                close: function (win) {
                    var wmedia = window.wmedia;
                    var electronFormItemView = view.findParentByType('reportform');

                    //加载附件文本框
                    Ext.Ajax.request({
                        url: '/report/reports/tree',
                        async: false,
                        traditional: true,//后台接收数组为null这里设为true就可以了
                        params: {
                            eleid: window.wmedia,
                            reportid: reportid,
                            xtType:window.xtType
                        },
                        success: function (response) {
                            var filename = [];
                            if(response.responseText==''){
                            }else {
                                var result = JSON.parse(response.responseText);
                                for (var i = 0; i < result.length; i++) {
                                    filename.push(result[i].text);
                                }
                            }
                            electronFormItemView.down('[itemId = media]').setValue(filename);
                        }
                    });
                }
            }
        });
        var treeStore = reportUploadWindow.down('treepanel').getStore();
        Ext.defer(function () {
            treeStore.proxy.extraParams = {reportid:reportid,eleid:window.wmedia,xtType:window.xtType};
            treeStore.reload();
        },300);
        window.wform = view.findParentByType('reportform');
        reportUploadWindow.down('[itemId=download]').hide();
        reportUploadWindow.show();
    }
});