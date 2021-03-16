/**
 * 采集管理-归档控制器
 * Created by Rong on 2018/6/20.
 */
var acIsArchivecode="";
Ext.define('Acquisition.controller.AcquisitionFilingController', {
    extend: 'Ext.app.Controller',
    views:['filing.AcquisitionFilingView',
        'filing.DynamicFilingFormView',
        'filing.EntryEditFromView',
        'filing.RetentionAdjustFromView','filing.OrderSettingSelectedFormView','filing.OrdersettingItemSelectedFormView','filing.OrdersettingDetailFormView',
        'filing.InsertFilingView'
    ],
    stores: ['OrdersettingSelectStore'],
    models: ['OrdersettingJsonModel'],
    init:function(){
        this.control({
            'mediaItemsDataView [itemId=filing]':{//归档
                click:this.filingHandler
            },
            'acquisitiongrid [itemId=filing]':{//归档
                click:this.filingHandler
            },
            'acquisitionfiling [itemId=filingNextStepBtn]':{//归档窗口　下一步 至归档预览grid
                click : this.filingSubmitForm
            },
            'acquisitionfiling [itemId=filingBackBtn]':{//归档窗口　返回 至选择归档条目grid
                click : this.backToAcquisitiongrid
            },
            'acquisitionfiling [itemId=filingBackTwoBtn]':{//预归档窗口　返回 至选择归档条目grid
                click : this.backToAcquisitiongrid
            },
            'acquisitionfiling button[itemId=ordersettingSaveBtnId]': {//归档顺序设置 保存
                click: this.saveOrder
            },
            'acquisitionfiling [itemId=generateArchivecode]':{//归档窗口　生成档号
                click : this.generateArchivecode
            },
            'acquisitionfiling [itemId=retentionAdjust]':{//归档窗口　保管期限调整
                click : this.retentionAdjust
            },
            'acquisitionfiling [itemId=ygdBackBtn]':{//归档窗口　取消预归档
                click : this.ygdBack
            },
            'retentionAdjustFromView [itemId=retentionAjustConfirm]':{//保管期限调整 确定
                click:this.retentionAjustConfirm
            },
            'retentionAdjustFromView [itemId=retentionAjustBack]':{//保管期限调整 返回
                click:function (btn) {
                    btn.up('window').close();
                }
            },
            'entryEditFromView [itemId=saveArchivecodeBtn]':{//修改 保存
                click:this.saveEntry
            },
            'entryEditFromView [itemId=closeEntryWinBtn]':{//修改 关闭
                click:this.closeEntry
            },
            'entryEditFromView [itemId=ygdPreBtn]': {//上一条
                click: this.preHandler
            },
            'entryEditFromView [itemId=ygdNextBtn]': {//下一条
                click: this.nextHandler
            },
            'acquisitionfiling [itemId=filingBtn]':{//归档窗口　归档
                click : this.filing
            },
            'acquisitionfiling [itemId=addGdBtn]':{//未归窗口　增加预归档
                click : this.addGd
            },
            'acquisitionfiling [itemId=insertFront]':{//未归窗口　插入预归档-首位
                click : this.insertGd
            },
            'acquisitionfiling [itemId=insertBehind]':{//未归窗口　插入预归档-末位
                click : this.insertGd
            },
            'acquisitionfiling [itemId=insertAnywhere]':{//未归窗口　插入预归档-位置
                click : this.insertGd
            },
            'InsertFilingView [itemId=checkInsert]':{//未归窗口　插入预归档-确定
                click : this.checkInsert
            },
            'acquisitionfiling [itemId=addOrderSetBtn]':{//未归窗口　预归档排序设置
                click : this.addOrderSet
            },
            'acquisitionfiling [itemId=ygdEditBtn]':{//归档窗口　预归档修改
                click : this.ygdEdit
            },
            'acquisitionfiling [itemId=filingpreviousStepBtn]':{//归档窗口　上一步　至档案分类选择的form
                click : this.activeFilingFirstForm
            },
            'dynamicfilingform [itemId=autoAppraisal]':{//归档动态表单 自动鉴定复选框
                change:this.changeComboState
            },
            'acquisitionfiling [itemId=batchModifyModifyId]':{//结果列表界面　批量修改
                click:this.doBatchModify
            },
            'acquisitionfiling [itemId=batchModifyReplaceId]':{//结果列表界面　批量替换
                click:this.doBatchReplace
            },
            'acquisitionfiling [itemId=batchModifyAddId]':{//结果列表界面　批量增加
                click:this.doBatchAdd
            },

            'acquisitionfiling': {
                beforetabchange:function(view){
                    if (view.activeTab.title == '归档设置') {
                        //检查与归档设置
                        var filingFirstForm = view.down('[itemId=filingFirstStep]');
                        var dynamicFilingForm = view.down('dynamicfilingform');
                        /*if(!dynamicFilingForm.initedstate){
                         XD.msg('模板或档号设置异常，请在“系统设置”-“模板维护”中设置该节点的模板及档号');
                         return false;
                         }*/
                        var treeComboboxView = filingFirstForm.down('acquisitionTreeComboboxView');
                        var ordertxtLab=view.down('[itemId=ordertxtId]');
                        // var ordertxt= ordertxtLab.title;
                        // ordertxt=ordertxt.substring(ordertxt.indexOf('序')+3);
                        // if((!treeComboboxView.rawValue)&&(ordertxt.indexOf('序')==-1)){
                        //     XD.msg('请选择归档的档案分类，以及未归记录加入预归档的先后排序！');
                        //     return false;
                        //}else
                        //     if(!treeComboboxView.rawValue){
                        //     XD.msg('请选择归档的档案分类！');
                        //     return false;
                        // }else if(ordertxt.indexOf('序')==-1){
                        //     XD.msg('请选择未归记录加入预归档的先后排序！');
                        //     return false;
                        // }
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
                        var ygType='ygd';
                        /* if(!(window.ygdNodeid==undefined||window.ygdNodeid==window.filingGrid.nodeid)){
                         ygType='ygdChange';//切换，nodeid改变
                         window.orderType=0;
                         window.moveUpOrDown=false;
                         }*/
                        var dataSource = 'next';
                        if((window.orderType==1||window.moveUpOrDown)&&window.orderType!=0){//orderType  0是增加，重排序   1是插入，不重新排序
                            dataSource = '';//按顺序排，不重新赋值序号
                        }
                        //window.ygdNodeid=window.filingGrid.nodeid;
                        if(window.wgChange){//未归页面没设置时，不重新加载表格
                            gridcard.getStore().proxy.extraParams.dataSource = dataSource;//修改参数避免重复增加临时条目
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
                        //this.initOrderset(view);
                    }
                    if (view.activeTab.title == '归档设置') {
                        this.initOrderset(view,window.hasPower);
                    }
                }
            },
            'acquisitionfiling [itemId=moveup]':{//预归档窗口　上移
                click : this.moveup
            },
            'acquisitionfiling [itemId=movedown]':{//预归档窗口　上移
                click : this.movedown
            },
            'ordersettingSelectedFormView [itemId=ordersettingSaveTwoBtnId]':{//排序设置弹窗 保存
                click : this.saveOrderTwo
            },
            'ordersettingItemSelectedFormView': {//可选字段 选择触发
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
            'ordersettingDetailFormView': {//排序设置  排序 选择触发
                render: function (field) {
                    field.getComponent("directionId").on('select', function (ob) {
                        var ordersettingSelectedFormView = this.findParentByType('ordersettingSelectedFormView');
                        var ordersettingItemSelectedFormView = ordersettingSelectedFormView.down('[itemId=itemselectorItemID]');
                        var ordersettingDetailFormView = ordersettingSelectedFormView.down('[itemId=ordersettingDetailFormViewItemID]');
                        changeToMultiselect(ob, ordersettingItemSelectedFormView, ordersettingDetailFormView);
                    });
                }
            },
        })
    },

    /**
     * 获取数据采集主控制器
     * @returns {*|Ext.app.Controller}
     */
    findMainControl:function(){
        return this.application.getController('AcquisitionController');
    },
    //获取数据归档视图
    findFilingView:function (btn) {
        return btn.up('acquisitionfiling');
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

    //返回至归档第一步form（“上一步”按钮的单击事件）
    activeFilingFirstForm:function (btn) {
        var filingView = this.findFilingView(btn);
        filingView.tabBar.items.items[0].show();
        filingView.tabBar.items.items[1].hide();
        filingView.tabBar.items.items[2].hide();
        var filingViewSub = btn.up('[itemId=gdszId]');
        var filingFirstForm = this.findFilingFirstFormView(btn);
        //filingViewSub.setActiveItem(filingFirstForm);
        //隐藏下一步和返回按钮
        //this.hideFilingSecondStepBtn(filingView);
        filingView.setActiveTab(0);
    },
    //切换到归档第二步窗口（dynamicform动态表单及basicgrid列表）
    activeFilingFormAndGrid:function (btn,state) {
        var filingView = this.findFilingView(btn);
        var filingViewSub = btn.up('[itemId=ygdId]');
        var filingFormAndGrid = this.findFilingFormAndGridView(btn);
        var filingGrid = this.findFilingGridView(btn);
        //filingViewSub.setActiveItem(filingFormAndGrid);
        var ygType='';//首次加载选中条目
        var allEntryids='';//选择所有页的条目ID
        /*if(state=='next'){//初次加载采集页面选择，继续切换就不再加载
         if(window.ygdNodeid==undefined||window.ygdNodeid==window.filingGrid.nodeid){
         ygType='ygd';//切换，nodeid不变
         }else{
         ygType='ygdChange';//切换，nodeid改变
         window.moveUpOrDown=false;//标记移动状态清空
         }
         window.ygdNodeid=window.filingGrid.nodeid;
         }else{//初次加载

         }*/
        allEntryids=filingView.allEntryids;
        //不传入multiValue参数，后台会根据nodeid获取档号构成字段，自动将其字段编码传入multiValue，使列表列加载档号构成字段
        var params={
            isSelectAll:filingView.isSelectAll,
            dataNodeid:filingView.tempParams['nodeid'],
            condition:filingView.tempParams['condition'],
            operator:filingView.tempParams['operator'],
            content:filingView.tempParams['content'],
            entryids:filingView.entryids,       //选定的记录的条目ID,在filingHandler方法中定义并赋值
            allEntryids:allEntryids,  //选择所有页的条目ID
            nodeid: filingGrid.nodeid,         //归档目标节点的节点ID
            dataSource:'capture',             //此处dataSource参数为列表数据来源标识（capture为通过采集表查询数据，处理后存至临时表，再从临时表中读取数据）
            type:'保管期限调整',
            ygType:ygType  //标记预归档
        };
        filingGrid.initGrid(params,true);//显示临时表中从未归加入的数据
        window.filingGrid=filingGrid;
        //window.filingGrid.nodeid=filingGrid.nodeid;
    },

    initWgGrid:function (view) {//显示本节点的条目，且条目不在临时表
        var nodeid =view.config.items[0].acquisitiongridNodeid;
        var filingGrid = view.down('[itemId=wgNodeId]');
        filingGrid.initGrid({nodeid:nodeid});
        window.wgGrid=filingGrid;
    },

    hideFilingSecondStepBtn:function (view) {
        var filingnextstep = view.down('[itemId=filingNextStepBtn]');
        var filingback = view.down('[itemId=filingBackBtn]');
        var filing = view.down('[itemId=filingBtn]');
        var filingpreviousStep = view.down('[itemId=filingpreviousStepBtn]');
        var ordersetSaveBtn = view.down('[itemId=ordersettingSaveBtnId]');
        /*if(view.xtype=='window'){
         view = view.down('acquisitionfiling');
         }*/
        view = view.down('[itemId=ygdId]');
        var tbseparator = view.getDockedItems('toolbar')[0].query('tbseparator');
        filingnextstep.setVisible(true);
        filingback.setVisible(true);
        ordersetSaveBtn.setVisible(true);
        filing.setVisible(false);
        filingpreviousStep.setVisible(false);
        tbseparator[0].setVisible(true);
        tbseparator[1].setVisible(true);
        tbseparator[2].setVisible(false);
        tbseparator[3].setVisible(false);
    },
    hideFilingFirstStepBtn:function (view) {
        var filingnextstep = view.down('[itemId=filingNextStepBtn]');
        var filingback = view.down('[itemId=filingBackBtn]');
        var filing = view.down('[itemId=filingBtn]');
        var filingpreviousStep = view.down('[itemId=filingpreviousStepBtn]');
        var ordersetSaveBtn = view.down('[itemId=ordersettingSaveBtnId]');
        /*if(view.xtype=='window'){

         }*/
        view = view.down('[itemId=ygdId]');
        var tbseparator = view.getDockedItems('toolbar')[0].query('tbseparator');
        filingnextstep.setVisible(false);
        filingback.setVisible(false);
        ordersetSaveBtn.setVisible(false);
        filing.setVisible(true);
        filingpreviousStep.setVisible(true);
        tbseparator[0].setVisible(false);
        tbseparator[1].setVisible(false);
        tbseparator[2].setVisible(false);
        tbseparator[3].setVisible(true);
    },


    //归档
    filingHandler:function (btn) {
        var grid = this.findMainControl().getGrid(btn);
        var selectAll
        var record
        var store
        var selLen
        if(grid.selModel != null) {
            selectAll=grid.down('[itemId=selectAll]').checked;
            record = grid.selModel.getSelection();
            store = grid.getStore();
            selLen = grid.selModel.getSelectionLength();
        }
        else {
            record = grid.acrossSelections;
            store = grid.down('dataview').getStore()
            selLen = record.length;
        }
        var tree = this.findMainControl().findGridView(btn).down('treepanel');
        var node = tree.selModel.getSelected().items[0];
        window.wgOrderNodeid= node.get('fnid');//标记数据采集列表节点（未归节点）id
        if(!node){
            XD.msg('请选择节点');
            return;
        }
        if(store.getCount() < 1){
            XD.msg('没有可以归档的数据，请先增加数据。');
            return;
        }
        //获取临时表的数据
        var temSum=0;
        Ext.Ajax.request({
            method:'POST',
            url: '/acquisition/getTempSize',
            async:false,
            scope:this,
            success: function (response) {
                temSum=Ext.decode(response.responseText).msg;
            },
            failure:function () {
                XD.msg('操作失败');
            }

        });
        var chooseSum=selLen;
        if(chooseSum+parseInt(temSum) > 5000){
            XD.msg('归档数据不能超过5000条,预归档页面已有'+temSum+'条数据');
            return;
        }
        var tempParams = store.proxy.extraParams;
        var tmp = [];
        var allTmp=[];
        var isSelectAll = false;
        if(selectAll){
            record = grid.acrossDeSelections;
            isSelectAll = true;
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
        }
        for(var i = 0; i < record.length; i++){
            tmp.push(record[i].get('entryid'));
        }
        var entryids = tmp.join(',');
        var allEntryids;
        if(selectAll){
            allEntryids= allTmp.join(',');
            if(allTmp.length+parseInt(temSum) > 5000){
                XD.msg('归档数据不能超过5000条,预归档页面已有'+temSum+'条数据');
                return;
            }
        }else{
            allEntryids= tmp.join(',');
        }
        var filingWin = Ext.create('Ext.window.Window',{
            //modal:true,
            width:'100%',
            height:'100%',
            //title:'归档',
            header: false,
            layout:'fit',
            closeToolText:'关闭',
            items:[{
                xtype: 'acquisitionfiling',
                tempParams:tempParams,
                isSelectAll:isSelectAll,
                entryids:entryids,                          //需归档的条目id
                allEntryids:allEntryids,
                acquisitiongrid:grid,                       //数据采集列表（未归节点对应列表）
                acquisitiongridNodeid:node.get('fnid')      //数据采集列表节点（未归节点）id
            }]
        });
        if(showElectronicRename == 'false'){
            filingWin.down('[itemId=radioGroupId]').hide();//隐藏“电子文件重名命名设置”
        }
        //隐藏归档和上一步按钮
        //this.hideFilingSecondStepBtn(filingWin);
        this.initWgGrid(filingWin);//未归标签页赋值

        //显示现有排序值
        var ordertxtLab=filingWin.down('[itemId=ordertxtId]');
        var ordertxtLabTwo=filingWin.down('[itemId=orderTxtLabelId]');
        var ordertxtLabThr=filingWin.down('[itemId=orderTxtLabelYgdId]');
        var returnStr = getOrderTxt(node.get('fnid'));
        var ordertxt = returnStr[0];
        var hasPower = returnStr[1];  //是否有设置归档排序的权限
        window.hasPower = returnStr[1];
        ordertxtLab.setTitle(ordertxt);//预归档页面
        ordertxtLabTwo.setText(ordertxt);//未归页面
        ordertxtLabThr.setText(ordertxt+" 。");//预归档页面
        window.ordertxtLab=ordertxtLab;
        window.ordertxtLabThr=ordertxtLabThr;

        //排序设置界面初始化
        this.initOrderset(filingWin,hasPower);
        var ordersettingSaveTwoBtn = filingWin.down('[itemId=ordersettingSaveTwoBtnId]');
        ordersettingSaveTwoBtn.setVisible(false);//隐藏页面本身的保存按钮

        //初始化预归档表格
        var filingnextstep = filingWin.down('[itemId=filingNextStepBtn]');
        var filingpreviousStep = filingWin.down('[itemId=filingpreviousStepBtn]');
        this.activeFilingFormAndGrid(filingpreviousStep,'first');
        //this.activeFilingFirstForm(filingnextstep);

        var ygView = filingWin.down('[itemId=ygdId]');
        var filingview=filingWin.down('acquisitionfiling');
        filingview.tabBar.items.items[0].show();
        filingview.tabBar.items.items[1].hide();
        filingview.tabBar.items.items[2].hide();
        filingWin.show();
    },

    initOrderset:function(view,hasPower){
        var detailformview = view.down('[itemId=ordersettingDetailFormViewItemID]');
        detailformview.down('[itemId=areaid]').reset();//清空值
        detailformview.down('[itemId=hiddenfieldId]').reset();
        if(hasPower=='true'){
            if(detailformview.down('[itemId=ordersettingSaveBtnId]')){
                detailformview.down('[itemId=ordersettingSaveBtnId]').show();
            }
            if(view.down('[itemId=ordersettingSaveTwoBtnId]')){
                view.down('[itemId=ordersettingSaveTwoBtnId]').show();
            }
        }else{ //没有权限隐藏保存按钮
            if(detailformview.down('[itemId=ordersettingSaveBtnId]')){
                detailformview.down('[itemId=ordersettingSaveBtnId]').hide();
            }
            if(view.down('[itemId=ordersettingSaveTwoBtnId]')){
                view.down('[itemId=ordersettingSaveTwoBtnId]').hide();
            }
        }
        var itemselectorView = view.down('[itemId=itemselectorID]');
        itemselectorView.store.proxy.extraParams = {datanodeid: window.wgOrderNodeid};
        itemselectorView.getStore().load(function (storedata) {
            if(storedata.length===0){
                XD.msg('请先去模板维护设置模板信息');
            }
            var records = [];
            var defaultField; //默认字段，如果当前归档顺序号为空就采用这个
            for (var i = 0; i < storedata.length; i++) {
                var temp = storedata[i].data.fieldcode.split('∪');
                if (temp[0] != "") {
                    records.push(storedata[i]);
                }
                if ("filedate"==temp[1]){  //默认文件日期
                    defaultField=storedata[i];
                }
            }
            if (records.length==0){
                records.push(defaultField)
            }
            itemselectorView.toField.store.removeAll();
            itemselectorView.setValue(records);
            itemselectorView.toField.boundList.select(0);//默认选中第一个
        });
    },

    //提交表单内容（“下一步”按钮的单击事件）
    filingSubmitForm:function (btn) {
        var filingView = this.findFilingView(btn);
        var boolean=filingView.down('[itemId = radioGroupId]').getValue();
        var labelTxt=filingView.down('[itemId = labelTxtGdId]');
        var filingFirstForm = this.findFilingFirstFormView(btn);
        var treeComboboxView = filingFirstForm.down('acquisitionTreeComboboxView');
        var ordertxtLab = filingFirstForm.down('[itemId=ordertxtId]');
        var ordertxt= ordertxtLab.title;
        if(!treeComboboxView.rawValue){
            XD.msg('请选择需要归档的档案分类！');
            return;
        }
        // if(ordertxt.indexOf('序')==-1){
        //     XD.msg('请选择未归记录加入预归档的先后排序！');
        //     return false;
        // }
        var dynamicFilingForm = this.findFilingFormView(btn);
        if(!dynamicFilingForm.initedstate){
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
        //this.activeFilingFormAndGrid(btn,'next');
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
                targetNodeid:dynamicFilingForm.nodeid,//归档节点
                addType:addType,//标记首次有数据加载或者切换归档节点
                nodeid:window.wgOrderNodeid  //未归节点
            };
            Ext.MessageBox.wait('正在加入预归档请稍后...', '提示');
            Ext.Ajax.request({
                method:'POST',
                url: '/acquisition/entryIndexYgd',
                params:params,
                scope:this,
                timeout:XD.timeout,
                success: function (response) {
                    Ext.MessageBox.hide();
                    XD.msg(Ext.decode(response.responseText).msg);
                    window.ygdNodeid=dynamicFilingForm.nodeid;
                    window.orderType=0;
                    window.moveUpOrDown=false;
                    acIsArchivecode=false;//标记全部生成档号失效
                    filingView.setActiveTab(1);
                    /*filingView.allEntryids = filingView.allEntryids + ","+ entryids.join(',');
                     //刷新列表数据
                     filingGrid.delReload(filingGrid.selModel.getSelectionLength());
                     window.orderType=0;//标记重新排序
                     window.wgChange=true;//标记未归页面有设置变动*/
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
        //this.hideFilingFirstStepBtn(filingView);
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
            url: '/acquisition/getParam',
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

    //返回至数据采集列表
    backToAcquisitiongrid:function (btn) {
        var filingView = this.findFilingView(btn);
        var ygGrid=filingView.down('[itemId=ylId]');
        var ygStore = ygGrid.getStore();
        if(ygStore.totalCount>0){//有预归档记录
            XD.confirm('返回将取消本次预归档操作，确定返回？', function () {
                btn.up('window').close();
                window.ygdNodeid='';
                delTempByUniquetag('cjgd');//清除临时表缓存
            });
        }else{
            btn.up('window').close();
            window.ygdNodeid='';
            delTempByUniquetag('cjgd');//清除临时表缓存
        }
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
            url: '/acquisition/moveup',
            params: {
                currentId: records[0].data.entryid,
                // nodeid: filingView.acquisitiongridNodeid
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
            url: '/acquisition/movedown',
            params: {
                currentId: records[0].data.entryid,
                nodeid: records[0].data.nodeid
                // nodeid: filingView.acquisitiongridNodeid
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

    saveOrderTwo:function(btn){
        var ordersettingSelectedFormViewWin = btn.findParentByType('addOrderSetWinId');
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
                that.initOrderset(ordersettingSelectedFormView,window.hasPower);
            },
            failure: function () {
                XD.msg('操作失败');
            }
        });
    },

    //生成档号（仅生成档号，不改变条目的数据节点id）
    generateArchivecode:function (btn) {
        var filingView = this.findFilingView(btn);
        var filingGrid = this.findFilingGridView(btn);
        var dynamicfilingform = filingView.down('dynamicfilingform');
        var ygGrid=filingView.down('[itemId=ylId]');
        var ygStore = ygGrid.getStore();
        if(ygStore.data.length==0){
            XD.msg('无预归档记录，请在“未归”中选择预归档的记录');
            return;
        }
        var dynamicfilingFormValues = dynamicfilingform.getValues();
        var filingValues = {};
        Ext.Ajax.request({
            method:'POST',
            url: '/acquisition/getCalculation',
            params:{
                nodeid: dynamicfilingform.nodeid
            },
            scope:this,
            success: function (response) {
                var calculation = Ext.decode(response.responseText).data;
                for(var name in dynamicfilingFormValues){//遍历表单中的所有值
                    if (name == calculation) {
                        if (dynamicfilingFormValues[name]=='') {
                            if (typeof(dynamicfilingFormValues['autoAppraisal']) == 'undefined') {//如果没勾选自动鉴定
                                XD.msg('有必填项未填写');
                                return;
                            }
                        } else {
                            if (typeof(dynamicfilingFormValues['autoAppraisal']) != 'undefined') {//如果勾选了自动鉴定
                                continue;
                            }
                        }
                    } else {
                        if(dynamicfilingFormValues[name]==''){
                            XD.msg('有必填项未填写');
                            return;
                        }
                    }
                    if(name=='appraisaltype' || name=='autoAppraisal'){
                        continue;
                    }
                    if (name == calculation) {
                        if (dynamicfilingFormValues[name]!='') {
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
                var params={
                    //entryids:entryids,       //预归档表格的所有条目ID
                    nodeid: dynamicfilingform.nodeid,         //归档目标节点的节点ID
                    filingValuesStrArr:filingValuesStrArr,   //档号设置字段的值（表单中的输入值）
                    appraisaltype:appraisaltype              //鉴定类型（规则）
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
                        method:'POST',
                        url: '/acquisition/generateArchivecode',
                        params:params,
                        scope:that,
                        timeout:XD.timeout,
                        success: function (response) {
                            var responseText = Ext.decode(response.responseText);
                            if(responseText.success==true){
                                var info;
                                if (responseText.msg=='需要调整计算项值') {
                                    info = 'ok';
                                } else {
                                    info = 'no';
                                }
                                Ext.Ajax.request({//调整计算项数值
                                    url: '/acquisition/ajustAllCalData',
                                    params:{
                                        /*entryids:entryids,*/
                                        nodeid: dynamicfilingform.nodeid,         //归档目标节点的节点ID
                                        info: info
                                    },
                                    method:'POST',
                                    timeout:XD.timeout,
                                    success: function (res) {
                                        acIsArchivecode=true;
                                        progressBarWin.close();
                                        XD.msg((Ext.decode(res.responseText)).msg);
                                        Ext.apply(filingGrid.getStore().getProxy().extraParams, {
                                            dataSource: '',
                                            ygType:'ygd'
                                        });
                                        filingGrid.initGrid();
//		                            filingGrid.notResetInitGrid();
                                    }
                                });
                            }else{
                                acIsArchivecode=true;
                                //Ext.MessageBox.hide();
                                progressBarWin.close();
                                XD.msg(responseText.msg);
                            }
                        }
                    });

                });

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
            url: '/acquisition/entryIndexYgdDel',
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
        var objectjson = dynamicfilingform.getForm().getValues();//获取上边的档号生成字段表单记录
        var filingGrid = this.findFilingGridView(btn);
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
        this.initOrderset(addOrderSetWin,window.hasPower);
        addOrderSetWin.down('[itemId=ordersettingSaveBtnId]').hide();
        addOrderSetWin.show();
    },

    //保管期限调整确定
    retentionAjustConfirm:function (btn) {
        var form = btn.up('form');
        var entryretention = form.getValues()['entryretention'];
        var params = {
            entryids: form.entryids,         //归档预览列表中选定需要调整保管期限记录的条目ID
            entryretention: entryretention, //调整后的保管期限值
            nodeid: form.filingGrid.nodeid,//归档目标节点id
            type: '数据采集'
        };

        Ext.Ajax.request({
            method: 'POST',
            url: '/acquisition/retentionAjust',
            params: params,
            scope: this,
            timeout: XD.timeout,
            success: function (response) {
                XD.msg(Ext.decode(response.responseText).msg);
                btn.up('window').close();
                form.filingGrid.initGrid();
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
            url: '/acquisition/entriesTemp',//保存到临时表
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
                        this.refreshFormData(formview, 'next');
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
    filing:function (btn) {
        var filingView = this.findFilingView(btn);
        var filingGrid = this.findFilingGridView(btn);
        var records = filingGrid.getSelectionModel().getSelection();
        var allSelect = [];
        var tmp = [];
        if(records.length==0){
            Ext.Ajax.request({
                method:'POST',
                url: '/acquisition/getTempEntryids',
                params:{nodeid:window.filingGrid.dataParams.nodeid,type:'capture'},
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
        }else {
            if (records.length > 5000) {
                XD.msg('一次只能归档5千条档案数据，请勾选记录，分批归档。归档会进行档号重复验证，防止数据量过大时，导致速度过慢及操作中断异常。');
                return;
            }
            for (var i = 0; i < records.length; i++) {
                if (records[i].get('archivecode') != undefined && records[i].get('archivecode').trim() != '') {
                    tmp.push(records[i].get('entryid'));
                } else {
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
            nodeid: window.filingGrid.dataParams.nodeid
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
                itemId: 'progressbarId',
                xtype: 'progressbar',
                text: '归档进度'
            }]
        });
        progressBarWin.show();
        window.progressBar = progressBarWin.down('progressbar');//进度条
        var socket = new SockJS("/websocket");
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            stompClient.subscribe('/user/' + userid + '/archiveBar', function (respnose) {
                var progressText = respnose.body;//压缩进度
                var num = progressText.substring(0, progressText.indexOf('&'));
                var count = progressText.substring(progressText.indexOf('&') + 1);
                var percentage = 0;
                percentage = num / count;
                progressText = num + '/' + count;
                window.progressBar.updateProgress(percentage, progressText);//刷新进度
            });
        });

        Ext.Ajax.request({
            method: 'POST',
            url: '/acquisition/entryIndexCaptures/filing',
            params: params,
            scope: this,
            timeout: XD.timeout,
            success: function (response) {
                //Ext.MessageBox.hide();
                progressBarWin.close();
                XD.msg(Ext.decode(response.responseText).msg);
                //刷新列表数据，同时判断是否所有都已归档
                filingGrid.getStore().proxy.extraParams.dataSource = 'del';//重新赋值序号
                filingGrid.getStore().proxy.extraParams.ygType = 'ygd';
                filingGrid.delReload(filingGrid.selModel.getSelectionLength(), function () {
                    if (filingGrid.getStore().getCount() < 1) {
                        window.ygdNodeid='';//返回节点表单页面，预归档标记为空
                        btn.up('window').close();
                        filingView.acquisitiongrid.notResetInitGrid();
                        acIsArchivecode = false;
                    } else {
                        filingView.acquisitiongrid.notResetInitGrid();
                    }
                });
                var data = Ext.decode(response.responseText).data;
                var ids = [];
                for (var i = 0; i < data.length; i++) {
                    ids.push(data[i].entryid);
                }
                //进行采集业务元数据
                captureServiceMetadataByZL(ids,'数据采集','归档');
            },
            failure: function () {
                //Ext.MessageBox.hide();
                progressBarWin.close();
                XD.msg('操作失败');
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
            url: '/acquisition/getTempSize',
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
                url: '/acquisition/getSelectAllEntryid',
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
        if(ygStore.totalCount<1){//没有预归档记录,不用确认框
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
            url: '/acquisition/entryIndexYgd',
            params:params,
            scope:that,
            timeout:XD.timeout,
            success: function (response) {
                Ext.MessageBox.hide();
                XD.msg(Ext.decode(response.responseText).msg);
                //filingView.allEntryids = filingView.allEntryids + ","+ entryids.join(',');
                //刷新列表数据
                filingGrid.delReload(filingGrid.selModel.getSelectionLength());
                window.orderType=0;//标记重新排序
                window.wgChange=true;//标记未归页面有设置变动
                acIsArchivecode=false;//标记全部生成档号失效
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
                url: '/acquisition/getTempSize',
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
            url: '/acquisition/entryIndexInsertYgd',
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
            url: '/acquisition/entryIndexInsertYgd',
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

    changeComboState:function (view) {//改变保管期限下拉框状态
        var dynamicfilingform = view.up('dynamicfilingform');
        var entryretentionCombo = dynamicfilingform.down('[itemId=entryretention]');
        var appraisaltypeCombo = dynamicfilingform.down('[itemId=appraisaltype]');
        if(view.checked){
            Ext.Msg.alert('提示', '根据所选鉴定类型的标准自动鉴定保管期限，鉴定失败则默认为“短期”！');
        }
        Ext.Ajax.request({
            method:'POST',
            url: '/acquisition/getCalculation',
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

    initFormDataOnly:function(operate, form, entryid){
        form.type = '预归档';
        var nullvalue = new Ext.data.Model();
        var acquisitionform = form.up('acquisitionform');
        var fields = form.getForm().getFields().items;
        var  preNextPanel= form.down('[itemId=preNextPanel]');
        preNextPanel.hide();
        /*var prebtn = form.down('[itemId=preBtn]');
         var nextbtn = form.down('[itemId=nextBtn]');
         var totaltext = form.down('[itemId=totalText]');
         var nowtext = form.down('[itemId=nowText]');*/
        /*prebtn.setVisible(false);
         nextbtn.setVisible(false);*/

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
            url: '/acquisition/entryTemp/' + entryid,
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
    preHandler: function (btn) {
        var form = btn.up('entryEditFromView').down('dynamicform');
        this.preNextHandler(form, 'pre');
    },

    //点击下一条
    nextHandler: function (btn) {
        var form = btn.up('entryEditFromView').down('dynamicform');
        this.preNextHandler(form, 'next');
    },

    //条目切换，上一条下一条
    preNextHandler: function (form, type) {
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
                    //type: formview.findParentByType('managementform').operateFlag,
                    //eleid: formview.findParentByType('managementform').down('electronic').getEleids(),
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

                /*var formValues = formview.getValues();
                 for (var name in formValues) {//遍历表单中的所有值
                 if (name == 'kccount' || name == 'fscount') {
                 if (formValues[name] == '' || formValues[name] == null) {
                 formValues[name] = "0";
                 }
                 }
                 }*/

                formview.submit({
                    url: '/acquisition/entriesTemp',//保存到临时表
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
                            this.ref.refreshFormData(this.form, this.type);
                            window.updateType=true;
                        }
                        /*btn.up('[itemId=entryEditWinId]').close();//关闭修改窗口
                         //更新预归档列表
                         var gridcard=window.filingGrid;
                         gridcard.getStore().proxy.extraParams.dataSource = '';//修改参数避免重复增加临时条目
                         gridcard.getStore().proxy.extraParams.ygType='ygd';
                         gridcard.getStore().reload();*/
                    },
                    failure: function (form, action) {
                        Ext.MessageBox.hide();
                        XD.msg("保存失败,请查看必填项是否已全部填写");
                    }
                });

                /*formview.submit({
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
                 });*/
            }, {
                ref: this,
                form: form,
                type: type
            }, function () {
                this.ref.refreshFormData(this.form, this.type)
            });
        } else {
            this.refreshFormData(form, type);
        }
    },

    refreshFormData: function (form, type) {
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
        /*if (form.operate != 'undefined') {
         this.initFormData(form.operate, form, entryid);
         return;
         }*/
        //this.initFormData('look', form, entryid);
        this.initFormDataOnly('modify',form, entryid);//条目赋值
    },

    getNodename: function (nodeid) {
        var nodename;
        Ext.Ajax.request({
            async: false,
            url: '/nodesetting/getFirstLevelNode/' + nodeid,
            success: function (response) {
                nodename = Ext.decode(response.responseText);
            }
        });
        return nodename;
    },
    doBatchReplace:function (btn) {
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
                window.batchModifyReplaceWins = batchModifyReplaceWin;
                Ext.on('resize',function(a,b){
                    window.batchModifyReplaceWins.setPosition(0, 0);
                    window.batchModifyReplaceWins.fitContainer();
                });
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
            window.batchModifyReplaceWins = batchModifyReplaceWin;
            Ext.on('resize',function(a,b){
                window.batchModifyReplaceWins.setPosition(0, 0);
                window.batchModifyReplaceWins.fitContainer();
            });
        }
    },
    doBatchModify:function (btn) {
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
                window.batchModifyModifyWins = batchModifyModifyWin;
                Ext.on('resize',function(a,b){
                    window.batchModifyModifyWins.setPosition(0, 0);
                    window.batchModifyModifyWins.fitContainer();
                });
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
            window.batchModifyModifyWins = batchModifyModifyWin;
            Ext.on('resize',function(a,b){
                window.batchModifyModifyWins.setPosition(0, 0);
                window.batchModifyModifyWins.fitContainer();
            });
        }
    },
    doBatchAdd:function (btn) {
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
                window.batchModifyAddWins = batchModifyAddWin;
                Ext.on('resize',function(a,b){
                    window.batchModifyAddWins.setPosition(0, 0);
                    window.batchModifyAddWins.fitContainer();
                });
            });
        }else{
            for(var i = 0; i < records.length; i++){
                tmp.push(records[i].get('entryid'));
            }
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
            window.batchModifyAddWins = batchModifyAddWin;
            Ext.on('resize',function(a,b){
                window.batchModifyAddWins.setPosition(0, 0);
                window.batchModifyAddWins.fitContainer();
            });
        }
    },

    selectAllEntry:function (allSelect,tmp) {
        Ext.Ajax.request({
            method:'POST',
            url: '/acquisition/getTempEntryids',
            params:{nodeid:window.filingGrid.dataParams.nodeid,type:'capture'},
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
    }

});

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
function getOrderTxt(nodeid) {
    var returnStr = [];
    var text='';
    Ext.Ajax.request({
        url: '/ordersetting/getOrderTxt',
        async:false,
        params:{
            nodeid:nodeid,
            type:'capture'
        },
        success: function (response) {
            var respText = Ext.decode(response.responseText);
            if(""!=respText.msg){
                text=text+'     当前归档顺序: '+ respText.msg;
            }
            returnStr.push(text);
            returnStr.push(respText.data);
        }
    });
    return returnStr;
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

function delTempByUniquetag(type) {//清除本机当前用户关联的的临时条目数据
    Ext.Ajax.request({
        method: 'POST',
        params: {archiveType: type},
        url: '/acquisition/delTempByUniquetag',
        async:false,
        success: function (response) {
        }
    });

}