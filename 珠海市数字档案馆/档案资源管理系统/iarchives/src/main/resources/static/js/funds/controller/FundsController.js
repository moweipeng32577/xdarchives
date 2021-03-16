/**
 * Created by RonJiang on 2018/04/08
 */
Ext.define('Funds.controller.FundsController',{
    extend : 'Ext.app.Controller',
    views :  ['FundsView','FundsGridView','FundsFormView'],
    stores:  ['FundsGridStore'],
    models:  ['FundsGridModel'],
    init : function() {
        this.control({
            'fundsgrid':{
                afterrender:function (view) {
                    var gridView = view.initGrid();
                    if(window.parent.realname=="系统管理员"){

                    }else {
                        gridView.down('[itemId=save]').hide();
                        gridView.down('[itemId=modify]').hide();
                        gridView.down('[itemId=del]').hide();
                        gridView.down('[itemId=look]').hide();
                        gridView.down('[itemId=summary]').hide();
                        gridView.down('[itemId=print]').hide();
                        gridView.down('[itemId=printfunds]').hide();
                    }
                }

            },
            'fundsgrid button[itemId=save]':{//增加
                click:this.saveHandler
            },
            'fundsgrid button[itemId=modify]':{//修改
                click:this.modifyHandler
            },
            'fundsgrid button[itemId=del]':{//删除
                click:this.delHandler
            },
            'fundsgrid button[itemId=look]':{//查看
                click:this.lookHandler
            },
            'fundsgrid button[itemId=print]':{//打印
                click:this.printHandler
            },
            'fundsgrid button[itemId=summary]':{//汇总
                click:this.summaryInfo
            },
            'fundsgrid button[itemId=printfunds]':{//打印全宗信息
                click:this.printfundsHandler
            },
            'fundsform':{//添加键盘监控
                afterrender:this.addKeyAction
            },
            'fundsform button[itemId=save]':{//保存
                click:this.submitForm
            },
            'fundsform button[itemId=back]':{
                click:function (btn) {
                    this.activeGrid(btn, false);
                }
            }
        });
    },

    //获取全宗管理应用视图
    findView: function (btn) {
        return btn.up('funds');
    },

    //获取表单界面视图
    findFormView: function (btn) {
        return this.findView(btn).down('fundsform');
    },

    //获取列表界面视图
    findGridView: function (btn) {
        return this.findView(btn).down('fundsgrid');
    },

    //点击查看、增加、修改时设置表单中控件只读属性
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
        var tbseparator = form.getDockedItems('toolbar')[0].query('tbseparator');
        if(operate == 'look'){//查看时隐藏保存按钮
            savebtn.setVisible(false);
            tbseparator[0].setVisible(false);
        }else{
            savebtn.setVisible(true);
            tbseparator[0].setVisible(true);
        }
    },

    //切换到列表界面视图
    activeGrid: function (btn, flag) {
        var view = this.findView(btn);
        var form = this.findFormView(btn);
        var grid = this.findGridView(btn);
        view.setActiveItem(grid);
        form.saveBtn = undefined;
        if(flag){//根据参数确定是否需要刷新数据
            grid.notResetInitGrid();
        }
    },

    //切换到表单界面视图
    activeForm: function (btn) {
        var view = this.findView(btn);
        var formview = this.findFormView(btn);
        view.setActiveItem(formview);
        return formview;
    },

    initFormData:function(operate, form, fundsid){
        form.reset();
        this.activeForm(form);
        var currentForm = this.findFormView(form);
        var fields = form.getForm().getFields();//所有Filed对象的集合
        if (operate != 'look') {
        	var btns = currentForm.query('label');
        	btns[0].show();
            for (var i = 0; i < fields.length; i++) {
            	if(!fields.items[i].freadOnly){
            		fields.items[i].setReadOnly(false);
            	}
            }
        } else {
        	var btns = currentForm.query('label');
        	btns[0].hide();
            for (var i = 0; i < fields.length; i++) {
            	fields.items[i].setReadOnly(true);
            }
        }
        if(typeof(fundsid) != 'undefined'){
            Ext.Ajax.request({
                method: 'GET',
                scope: this,
                url: '/funds/fundss/' + fundsid,
                success: function (response) {
                    var funds = Ext.decode(response.responseText);
                    if(operate == 'add'){
                        delete funds.fundsid;
                    }
                    form.loadRecord({getData: function () {return funds;}});
                    form.organid = funds.organid;
                }
            });
        }
        this.formStateChange(form,operate);
        this.changeBtnStatus(form,operate);
    },

    saveHandler:function (btn) {//同步全宗
    	var grid = this.findGridView(btn);
    	Ext.Msg.wait('正在进行机构同步，请耐心等待……', '正在操作');
    	Ext.Ajax.request({
            method: 'POST',
            url: '/funds/initFunds',
            success: function (response) {
            	XD.msg(Ext.decode(response.responseText).msg);
        		grid.notResetInitGrid();//刷新全宗表格信息
        		Ext.MessageBox.hide();
            },
            failure: function () {
                XD.msg('操作中断');
                Ext.MessageBox.hide();
            }
        });
    },

    modifyHandler:function (btn) {//修改全宗
        var form = this.findFormView(btn);
        form.saveBtn = form.down('[itemId=save]');
        form.operateFlag = 'modify';
        var grid = this.findGridView(btn);
        var record = grid.selModel.getSelection();
        if (record.length != 1) {
            XD.msg('请选择一条需要修改的数据');
            return;
        }
        this.initFormData('modify', form, record[0].get('fundsid'));
    },

    delHandler:function (btn) {//删除全宗
        var grid = this.findGridView(btn);
        var record = grid.selModel.getSelection();
        if (record.length == 0) {
            XD.msg('请至少选择一条需要删除的数据');
            return;
        }
        XD.confirm('确定要删除这' + record.length + '条数据吗',function(){
            var tmp = [];
            for (var i = 0; i < record.length; i++) {
                tmp.push(record[i].get('fundsid'));
            }
            var fundsids = tmp.join(',');
            Ext.Ajax.request({
                method: 'DELETE',
                url: '/funds/fundss/' + fundsids,
                success: function (response) {
                    XD.msg(Ext.decode(response.responseText).msg);
                    grid.delReload(record.length);
                }
            })
        },this);
    },

    lookHandler: function (btn) {//查看全宗
        var grid = this.findGridView(btn);
        var record = grid.selModel.getSelection();
        if (record.length < 1) {
            XD.msg('请选择一条需要查看的数据');
            return;
        } else if (record.length > 1) {
        	XD.msg('只能选择一条数据');
            return;
        }
        var fundsid = record[0].get('fundsid');
        var form = this.findFormView(btn);
        this.initFormData('look',form, fundsid);
    },
    printHandler:function (btn) {
        var grid = this.findGridView(btn);
        var record = grid.selModel.getSelection();
        if (record.length == 0) {
            XD.msg('请至少选择一条需要打印的数据');
            return;
        }
        var tmp = [];
        var params = {};
        for (var i = 0; i < record.length; i++) {
            tmp.push(record[i].get('fundsid').trim());
        }
        var fundsids = tmp.join(',');
        if(reportServer == 'UReport') {
            params['fundsid'] = fundsids;
            XD.UReportPrint(null, '全宗目录表', params);
        }
        else if(reportServer == 'FReport') {
            XD.FRprint(null, '全宗目录表', fundsids.length > 0 ? "'fundsid':'" + fundsids + "'" : '');
        }
    },
    printfundsHandler:function (btn) {
        var grid = this.findGridView(btn);
        var record = grid.selModel.getSelection();
        if (record.length == 0) {
            XD.msg('请至少选择一条需要打印的数据');
            return;
        }
        var tmp = [];
        var params = {};
        for (var i = 0; i < record.length; i++) {
            tmp.push(record[i].get('fundsid').trim());
        }
        var fundsids = tmp.join(',');
        if(reportServer == 'UReport') {
            params['fundsid'] = fundsids;
            XD.UReportPrint(null, '全宗信息表', params);
        }
        else if(reportServer == 'FReport') {
            XD.FRprint(null, '全宗信息表', fundsids.length > 0 ? "'fundsid':'" + fundsids + "'" : '');
        }
    },
    
    summaryInfo: function (btn) {//汇总
    	var grid = this.findGridView(btn);
        var record = grid.selModel.getSelection();
        if (record.length < 1) {
            XD.msg('请选择一条需要进行汇总的数据');
            return;
        }
        var organid = [];
        for (var i = 0; i < record.length; i++) {
        	var info = record[i];
        	if (typeof (info.get('fundsid')) == 'undefined' || info.get('fundsid') == '') {
        		XD.msg('当前记录无全宗信息，无法进行汇总操作');
        		return;
        	}
        	if (typeof (info.get('organid')) == 'undefined' || info.get('organid') == '') {
        		XD.msg('当前记录无机构信息，无法进行汇总操作');
        		return;
        	}
        	if (typeof (info.get('funds')) == 'undefined' || info.get('funds') == '') {
        		XD.msg('当前记录无全宗号信息，无法进行汇总操作');
        		return;
        	}
        	if (record[i].get('fundsid') && record[i].get('organid') && record[i].get('funds')) {
        		organid.push(record[i].get('fundsid') + "-" + record[i].get('organid') + "-" + record[i].get('funds'));
        	}
        }
        var info = organid.join(",");
        Ext.Msg.wait('正在进行全宗汇总，请耐心等待……', '正在操作');
        Ext.Ajax.request({
            method: 'POST',
            timeout:10000000,
            params: {
            	info: info
            },
            url: '/funds/summaryInfo',
            success: function (response) {
            	if (Ext.decode(response.responseText).success) {
            		grid.notResetInitGrid();//刷新全宗表格信息
            	}
            	Ext.MessageBox.hide();
            	XD.msg(Ext.decode(response.responseText).msg);
            },
            failure: function () {
                Ext.MessageBox.hide();
                XD.msg('操作中断');
            }
        });
    },
    
    //保存表单数据，返回列表界面视图
    submitForm: function (btn) {
        var form = this.findFormView(btn);
        form.submit({
            method: 'POST',
            url: '/funds/fundss',
            params: {
	        	operate: form.operateFlag,
	        	organid: form.organid
	        },
            scope: this,
            success: function (form, action) {
        		//切换到列表界面,同时刷新列表数据
                this.activeGrid(btn,true);
            	XD.msg(action.result.msg);
            },
            failure: function (form, action) {
                XD.msg(action.result.msg);
            }
        });
    },

    //监听键盘按下事件
    addKeyAction:function (view) {
        var controller = this;
        view.saveBtn = view.down('[itemId=save]');
        document.onkeydown = function () {
            var oEvent = window.event;
            if (oEvent.ctrlKey && !oEvent.shiftKey && !oEvent.altKey && oEvent.keyCode == 83) { //这里只能用alt，shift，ctrl等去组合其他键event.altKey、event.ctrlKey、event.shiftKey 属性
                // XD.msg('Ctrl+S');
                Ext.defer(function () {
                    if(view.saveBtn && view.operateFlag){//此处若不增加operateFlag判断，点击树节点后初次渲染fundsform时，按下ctrl+s会调用此方法
                        controller.submitForm(view.saveBtn);//保存
                    }
                },1);
                event.returnValue = false;//阻止event的默认行为
                // return false;//阻止event的默认行为
            }
        }
    }
});