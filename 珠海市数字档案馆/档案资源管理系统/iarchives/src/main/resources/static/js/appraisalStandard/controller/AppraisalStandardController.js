/**
 * Created by yl on 2017/10/25.
 */
Ext.define('AppraisalStandard.controller.AppraisalStandardController', {
    extend: 'Ext.app.Controller',

    views: [
        'AppraisalStandardView', 'AppraisalStandardFormView',
        'AppraisalStandardGridView'
        ,'AutoRetentionGridView','SetInitProbabilityFromView'
    ],//加载view
    stores: [
        'AppraisalStandardGridStore','AppraisalStandardTreeStore'
        ,'AutoRetentionGridStore'
    ],//加载store
    models: [
        'AppraisalStandardGridModel','AppraisalStandardTreeModel'
        ,'AutoRetentionGridModel'
    ],//加载model
    init: function () {
        this.control({
            // 'autoRetentionGridView':{
            //     afterrender:function (grid) {
            //         grid.initGrid();
            //         // grid.setVisible(false);
            //     }
            // }
            // ,
            'treepanel':{
                select:function (treemodel, record) {
                    if(record.get('fnid') == 'b93d77667a8d40ef8f74e242670aeabc'){
                        var gridview = this.findView(treemodel.view).down('[itemId=gridview]');
                        var grid = gridview.down('appraisalStandardGridView');
                        var autoGrid = gridview.down('autoRetentionGridView');//自动识别的Grid
                        grid.setVisible(false);
                        autoGrid.initGrid();
                        autoGrid.setVisible(true);
                    }else {
                        var gridview = this.findView(treemodel.view).down('[itemId=gridview]');
                        var grid = gridview.down('appraisalStandardGridView');
                        var autoGrid = gridview.down('autoRetentionGridView');//自动识别的Grid
                        grid.appraisaltypevalue = record.get('text');
                        autoGrid.setVisible(false);
                        grid.initGrid({appraisaltypevalue: record.get('text')});
                        grid.setVisible(true);
                    }
                    if(window.parent.realname=="系统管理员"){

                    }else {
                        grid.down('[itemId=save]').hide();
                        grid.down('[itemId=modify]').hide();
                        grid.down('[itemId=del]').hide();
                    }
                }
            },
            'appraisalStandardGridView button[itemId=save]':{//增加
                click:this.saveHandler
            },
            'appraisalStandardGridView button[itemId=modify]':{//修改
                click:this.modifyHandler
            },
            'appraisalStandardGridView button[itemId=del]':{//删除
                click:this.delHandler
            },
            'appraisalStandardFormView button[itemId=save]':{//鉴定标准保存
                click:this.submitForm
            },
            'appraisalStandardFormView button[itemId=back]':{//鉴定标准表单　返回
                click:function (btn) {
                    btn.up('window').close();
                }
            },
            'autoRetentionGridView button[itemId=initTable]':{//初始化词库
                click:this.initAutoRetention
            },
            'autoRetentionGridView button[itemId=setInitProbability]':{//设置初始概率按钮
                click:this.setProbabilityHandler
            },
            'setInitProbabilityFromView button[itemId=save]':{//鉴定标准保存
                click:this.probabilityFormSubmit
            },
            'setInitProbabilityFromView button[itemId=back]':{//鉴定标准表单　返回
                click:function (btn) {
                    btn.up('window').close();
                }
            }
        });
    },
    //获取鉴定标准管理应用视图
    findView: function (btn) {
        return btn.findParentByType('appraisalStandard');
    },

    //获取列表界面视图
    findGridView: function (btn) {
        return this.findView(btn).getComponent('gridview');
    },

    saveHandler:function (btn) {//增加鉴定标准
        var gridview = this.findGridView(btn);
        var tree = gridview.down('treepanel');
        var grid = gridview.down('appraisalStandardGridView');
        var node = tree.selModel.getSelected().items[0];
        // if (!node) {//若点击增加时左侧未选中任何节点，则提示选择节点
        //     XD.msg('请选择节点');
        //     return;
        // }
        var appraisaltypevalue = node!=null?node.get('text'):'';
        var saveAppraisalStandardWin = Ext.create('Ext.window.Window',{
            width: '55%',
            height: '55%',
            title: '增加 '+ appraisaltypevalue + ' 标准值',
            modal: true,
            closeToolText: '关闭',
            layout:'fit',
            items:[{
                xtype:'appraisalStandardFormView',
                grid:grid,
                appraisaltypevalue:appraisaltypevalue
            }]
        });
        var form = saveAppraisalStandardWin.down('appraisalStandardFormView');
        if(node){
            form.getForm().findField('appraisaltypevalue').setValue(node.get('text'));
        }
        saveAppraisalStandardWin.show();
    },

    modifyHandler:function (btn) {//修改鉴定标准
        var gridview = this.findGridView(btn);
        var tree = gridview.down('treepanel');
        var grid = gridview.down('appraisalStandardGridView');
        var node = tree.selModel.getSelected().items[0];
        var record = grid.selModel.getSelection();
        if (record.length != 1) {
            XD.msg('请选择一条需要修改的数据');
            return;
        }
        var appraisaltypevalue;
        if (!node) {//自动刷新树和列表时，树节点未选中，但列表数据可选中
            appraisaltypevalue = record[0].get('appraisaltypevalue');
        }else{
            appraisaltypevalue = node.get('text');
        }
        var modifyAppraisalStandardWin = Ext.create('Ext.window.Window',{
            width: '55%',
            height: '55%',
            title: '修改 '+ appraisaltypevalue + ' 标准值',
            modal: true,
            closeToolText: '关闭',
            layout:'fit',
            items:[{
                xtype:'appraisalStandardFormView',
                grid:grid,
                appraisaltypevalue:appraisaltypevalue
            }]
        });
        var form = modifyAppraisalStandardWin.down('appraisalStandardFormView');
        this.initFormData(form, record[0].get('appraisalstandardid'));
        modifyAppraisalStandardWin.show();
    },

    delHandler:function (btn) {//删除鉴定标准
        var gridview = this.findGridView(btn);
        var tree = gridview.down('treepanel');
        var grid = gridview.down('appraisalStandardGridView');
        var record = grid.selModel.getSelection();
        if (record.length == 0) {
            XD.msg('请选择数据');
            return;
        }
        XD.confirm('确定要删除这' + record.length + '条数据吗',function(){
            var tmp = [];
            for (var i = 0; i < record.length; i++) {
                tmp.push(record[i].get('appraisalstandardid'));
            }
            var appraisalstandardids = tmp.join(',');
            Ext.Ajax.request({
                method: 'DELETE',
                url: '/appraisalStandard/appraisalStandards/' + appraisalstandardids,
                success: function (response) {
                    var msg = Ext.decode(response.responseText).msg;
                    grid.delReload(record.length,function () {
                        if(grid.getStore().data.length!=0){
                            XD.msg(msg);
                        }else{
                            XD.confirm(msg+'!当前鉴定类型不包含任何鉴定标准，是否需要从系统中删除此鉴定类型？',function () {
                                Ext.Ajax.request({
                                    method: 'DELETE',
                                    url: '/appraisalStandard/delAppraisalType/' + grid.appraisaltypevalue,
                                    success: function (response) {
                                        XD.msg(Ext.decode(response.responseText).msg);
                                        tree.getStore().reload();
                                    }
                                });
                            },this);
                        }
                    });
                }
            })
        },this);
    },

    submitForm:function (btn) {//鉴定标准保存
        var win = btn.up('window');
        var formview = win.down('appraisalStandardFormView');
        var appraisalTypeValue = formview.getForm().findField('appraisaltypevalue').getValue();
        var appraisalStandardValue = formview.getForm().findField('appraisalstandardvalue').getValue();
        var appraisalRetentionValue = formview.getForm().findField('appraisalretention').getValue();
        if(appraisalTypeValue==null || appraisalTypeValue==''){
            XD.msg('鉴定类型不能为空');
            return;
        }
        if(appraisalStandardValue== null || appraisalStandardValue==''){
            XD.msg('鉴定标准值不能为空');
            return;
        }
        if(appraisalRetentionValue==null || appraisalRetentionValue==''){
            XD.msg('保管期限不能为空');
            return;
        }
        var ifAppraisaltypeExists = false;
        Ext.Ajax.request({
            url: '/appraisalStandard/ifAppraisaltypeExists',
            async:false,
            params:{
                appraisalTypeValue:appraisalTypeValue
            },
            success: function (response) {
                ifAppraisaltypeExists = Ext.decode(response.responseText).success;
            }
        });
        if(!ifAppraisaltypeExists){//鉴定类型不存在,提示是否创建新的鉴定类型
            XD.confirm('鉴定类型不存在，是否新增鉴定类型并保存鉴定标准？',function () {
                this.saveAppraisalstandardData(formview,appraisalTypeValue);
            },this);
        }
        else if(appraisalTypeValue!=this.getCurrentAppraisaltype(formview)){//保存的鉴定标准不为当前选定节点(或当前未选定任何节点及列表数据)，刷新新保存的鉴定标准所在列表
            this.saveAppraisalstandardData(formview,appraisalTypeValue);
        }
        else{//保存的鉴定标准为当前选定节点
            this.saveAppraisalstandardData(formview);
        }
    },

    //根据判断树及列表是否被选中，返回当前树或列表的鉴定类型值，若均未选中，则返回值为空
    getCurrentAppraisaltype:function (formview) {
        var treeAppraisaltype;
        var tree = this.findGridView(formview.grid).down('treepanel');
        var treeselect = tree.selModel.getSelected();
        if(treeselect.length>0){
            treeAppraisaltype = treeselect.items[0].get('text');
        }
        var gridAppraisaltype;
        var gridselect = formview.grid.selModel.getSelected();
        if(gridselect.length>0){
            gridAppraisaltype = gridselect.items[0].get('appraisaltypevalue');
        }
        if(treeAppraisaltype!=undefined || gridAppraisaltype!=undefined){//树和列表至少有一个被选中
            return treeAppraisaltype != undefined ? treeAppraisaltype : gridAppraisaltype;
        }
        return null;//树和列表均未被选中
    },

    saveAppraisalstandardData:function (formview,type) {//保存鉴定标准
        formview.submit({
            method: 'POST',
            url: '/appraisalStandard/appraisalStandards',
            params:{appraisaltypevalue:formview.appraisaltypevalue},
            scope: this,
            success: function (form, action) {
                //刷新列表数据，关闭表单窗口
                if(typeof type != 'undefined'){//保存鉴定标准及鉴定类型后，回调方法中刷新鉴定类型节点树及鉴定标准列表数据
                    this.findGridView(formview.grid).down('treepanel').getStore().reload();
                    formview.grid.notResetInitGrid({appraisaltypevalue:type});
                }else{
                    formview.grid.notResetInitGrid({appraisaltypevalue:formview.appraisaltypevalue});
                }
                formview.up('window').close();
                XD.msg(action.result.msg);
            },
            failure: function () {
                XD.msg('操作失败');
            }
        });
    },

    initFormData: function (form, appraisalstandardid) {
        form.reset();
        Ext.Ajax.request({
            scope: this,
            url: '/appraisalStandard/getAppraisalStandard',
            params:{appraisalstandardid:appraisalstandardid},
            success: function (response) {
                var appraisalStandard = Ext.decode(response.responseText);
                form.loadRecord({getData: function () {return appraisalStandard;}});
            }
        });
    },
    initAutoRetention: function(btn){//初始化词库的function
        var gridview = this.findGridView(btn)
        var autoGrid = gridview.down('autoRetentionGridView');
        var myMask = new Ext.LoadMask({msg: '正在初始化数据，数据量大请耐心等待...', target: autoGrid});
        XD.confirm("初始化词库将清空已学习的库，重新在已归的条目表上获取,您是否确定？",function(){
            myMask.show();
            Ext.Ajax.request({
                url: '/appraisalStandard/resetAlgRetentionTable',
                async:true,
                timeout: 1000000000,
                success: function (response) {
                    myMask.hide();
                    var responseJson = response.responseText;
                    if(responseJson == undefined)
                        return;
                    responseJson = JSON.parse(responseJson);
                    var isSuccess =responseJson.success;
                    if(isSuccess == true)
                        autoGrid.initGrid();
                    XD.msg(responseJson.msg);
                }
            });
        })
    },
    setProbabilityHandler:function (btn) {//增加鉴定标准
        var gridview = this.findGridView(btn);
        var autoGrid = gridview.down('autoRetentionGridView');
        // if (!node) {//若点击增加时左侧未选中任何节点，则提示选择节点
        //     XD.msg('请选择节点');
        //     return;
        // }
        var saveInitProbabilityWin = Ext.create('Ext.window.Window',{
            width: '55%',
            height: '55%',
            title: '设置初始概率',
            modal: true,
            closeToolText: '关闭',
            layout:'fit',
            items:[{
                xtype:'setInitProbabilityFromView',
                grid:autoGrid
            }]
        });
        saveInitProbabilityWin.show();
    }
    ,
    probabilityFormSubmit:function (btn) {//初始概率保存
        var win = btn.up('window');
        var formview = win.down('setInitProbabilityFromView');
        var Y = formview.getForm().findField('Y').getValue();   //永久
        var CQ = formview.getForm().findField('CQ').getValue(); //长期
        var DQ = formview.getForm().findField('DQ').getValue(); //短期
        Ext.Ajax.request({
            scope:this,
            url: '/appraisalStandard/saveInitProbabilityTable', //保存初始概率值
            async:false,
            params:{
                Y:Y,
                CQ:CQ,
                DQ:DQ
            },
            success: function (response){
                var responseJson = response.responseText;
                if(responseJson == undefined)
                    return;
                responseJson = JSON.parse(responseJson);
                win.close();
                XD.msg(responseJson.msg);
            }
        });

    }
});