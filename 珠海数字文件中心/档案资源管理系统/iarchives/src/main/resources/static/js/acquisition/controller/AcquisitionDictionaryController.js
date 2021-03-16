/**
 * 数据采集-分类管理控制器
 * Created by Rong on 2018/6/19.
 */
var acquisitionGrid;
Ext.define('Acquisition.controller.AcquisitionDictionaryController', {
    extend: 'Ext.app.Controller',
    views:['dictionary.AcquistionDictionaryView',
        'dictionary.AcquisitionClassificationGridView',
        'dictionary.AcquisitionClassificationView'],
    init:function(){
        this.control({
            'acquisitiongrid [itemId=classificationManagement]':{
                click: this.classificationHandler                                  //进入分类设置管理窗口
            },
            'acquisitionclassification [itemId=classificationBackBtn]':{
                click: this.backToGrid                                             //分类设置返回，即关闭窗口
            },
            'acquisitionclassification [itemId=classificationSet]':{
                click: this.setHandler                                              //分类设置窗口，分类设置
            },
            'acquisitionclassification [itemId=classificationAutoSet]':{
                click: this.autoSetHandler                                          //分类设置窗口，分类自动设置
            },
            'acquisitionclassification [itemId=previousStepBtn]':{
                click: this.activeClassificationFirstForm                           //分类自动设置面板返回上一步
            },
            'acquisitionclassification [itemId=setInfo]':{
                click: this.toSetHandler                                            //分类自动设置，设置按钮
            },
            'acquistionDictionaryView [name=organ]':{
                select: this.organHandler                                          //分类设置窗口，选择机构问题下拉框
            },
            'acquistionDictionaryView [name=resetOrgan]':{
                click: this.resetOrganHandler                                      //分类设置窗口中重置机构
            }
        })
    },

    /**
     * 获取数据采集主控制器
     * @returns {*|Ext.app.Controller}
     */
    findMainControl:function(){
        return this.application.getController('AcquisitionController');
    },
    //查找到分类管理视图
    findClassificationGrid:function(btn){
        return btn.up('acquisitionclassification');
    },
    //查找到引入字典视图
    findDictionaryView:function(btn){
        return this.findClassificationGrid(btn).down('acquistionDictionaryView');
    },
    //查找到引入表单视图
    findClassificationView:function(btn){
        return this.findClassificationGrid(btn).down('acquisitionClassificationGridView');
    },
    /**
     * 查找分类设置第一步面板
     * @param btn
     */
    findClassificationFirstView:function (btn) {
        return this.findClassificationGrid(btn).down('[itemId=classificationFirstStep]');
    },
    /**
     * 查找分类设置第二布面板
     * @param btn
     */
    findClassificationSencondView:function (btn) {
        return this.findClassificationGrid(btn).down('[itemId=classificationSecondStep]');
    },
    findClassificationGridView:function (btn) {
        return this.findClassificationGrid(btn).down('entrygrid');
    },

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
    	acquisitionGrid = this.findMainControl().getGrid(btn);//获取到数据采集表单视图
        var tree = this.findMainControl().findGridView(btn).down('treepanel');
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
            items:[{
                xtype: 'acquisitionclassification'//加载分类设置视图
            }]
        });
        //弹出分类设置界面时刷新下拉框数据
        var dictionaryform = classificationWin.down('acquistionDictionaryView').getForm();
        dictionaryform.findField('filingyear').getStore().reload();
        dictionaryform.findField('entryretention').getStore().reload();
        dictionaryform.findField('organ').getStore().reload();
        //隐藏上一步和设置按钮
        var classificationView = classificationWin.down('acquisitionclassification');
        classificationWin.show();
        var view = classificationView.down('acquisitionClassificationGridView');
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
            	type: '数据采集'
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
        this.findMainControl().activeGrid(btnInfo, true);//刷新数据采集界面
    },

    //机构文本框显示选中的机构数据
    organHandler: function(combo, record) {
        var view = combo.findParentByType('acquistionDictionaryView');
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
        var view = form.findParentByType('acquistionDictionaryView');
        view.down('[name=organ]').setValue('');//清空机构字段
        view.down('[name=organInfo]').setValue('');//清空机构信息字段
    }
});