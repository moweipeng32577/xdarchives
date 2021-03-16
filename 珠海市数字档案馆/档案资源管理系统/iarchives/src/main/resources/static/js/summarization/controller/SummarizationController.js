/**
 * Created by RonJiang on 2018/01/24
 */
Ext.define('Summarization.controller.SummarizationController', {
    extend: 'Ext.app.Controller',
    views: ['SummarizationView','SummarizationPromptView','SummarizationFormView','SummarizationSettingView'],
    models: ['SummarizationTreeModel'],
    stores: ['SummarizationTreeStore','SummarizationSelectStore'],
    init : function() {
        var ifShowRightPanel = false;
        this.control({
            'treepanel':{
                select: function (treemodel, record) {
                    var summarizationView = treemodel.view.findParentByType('summarization');
                    var summarizationPromptView = summarizationView.down('summarizationPromptView');
                    if(!ifShowRightPanel){
                        summarizationPromptView.removeAll();
                        summarizationPromptView.add({
                            xtype: 'summarizationFormView'
                        });
                        ifShowRightPanel = true;
                    }
                    var advancedSearchDynamicForm = summarizationView.down('advancedSearchDynamicForm');
                    this.initAdvancedSearchFormField(advancedSearchDynamicForm,record.get('fnid'));
                }
            },
            'summarizationFormView':{
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
            'SummarizationSettingView [itemId=submitBtn]':{
                click:function (btn) {
                    var settingView=btn.findParentByType('SummarizationSettingView');
                    var multiValue=settingView.down('[itemId=multiItemId]').getValue();
                    var comboValue=settingView.down('[itemId=comboItem]').getValue();
                    var msg="请选择";
                    if(multiValue.length==0){
                        msg+="汇总对象";
                    }
                    if(comboValue==null||comboValue==''){
                        if(multiValue.length==0){
                            msg+="和";
                        }
                        msg+="统计项";
                    }

                    if(msg!="请选择"&&comboValue!='count'){
                        XD.msg(msg);
                        return;
                    }

                    var form = settingView.treeView.findParentByType('summarization').down('[itemId=summarizationFormViewId]');
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
                    for(var name in formValues){//遍历表单中的所有值
                        formParams[name] = formValues[name];
                    }
                    formParams['multiValue'] = multiValue;
                    formParams['comboValue'] = comboValue;
                    formParams['nodeId'] = settingView.treeView.selection.get('fnid');
                    formParams['ifSearchLeafNode'] = true;//点击非叶子节点时，是否查询出其包含的所有叶子节点数据
                    formParams['ifContainSelfNode'] = false;//点击非叶子节点时，是否查询出当前非叶子节点及其包含的所有非叶子节点数据
					Ext.MessageBox.wait('正在处理请稍后...', '提示');
                    Ext.Ajax.request({
                        url:'/summarization/summary',
                        params:formParams,
                        timeout:XD.timeout,//解决超时问题
                        success:function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            settingView.down('[itemId=textareaItem]').setValue(respText.data);
                            Ext.MessageBox.hide();
                        },
                        failure:function () {
                            XD.msg('统计失败');
                            Ext.MessageBox.hide();
                        }
                    })
                }
            },
            'SummarizationSettingView [itemId=resetBtn]':{
                click:function (btn) {
                    btn.findParentByType('SummarizationSettingView').down('[itemId=textareaItem]').setValue('');
                }
            },
            'summarizationFormView button[itemId=topSummarizationBtn]':{click:this.doSummarization},
            'summarizationFormView button[itemId=bottomSummarizationBtn]':{click:this.doSummarization},
            'summarizationFormView button[itemId=topClearBtn]':{click:this.doAdvancedSearchClear},
            'summarizationFormView button[itemId=bottomClearBtn]':{click:this.doAdvancedSearchClear},
            'summarizationFormView button[itemId=topCloseBtn]':{click:this.doAdvancedSearchClose},
            'summarizationFormView button[itemId=bottomCloseBtn]':{click:this.doAdvancedSearchClose}
        });
    },

    //获取数据汇总应用视图
    findView: function (btn) {
        return btn.findParentByType('summarization');
    },
    //获取检索表单界面视图
    findSearchformView: function (btn) {
        return this.findView(btn).down('[itemId=formview]');
    },

    //切换到检索表单界面视图
    activeSearchform: function (btn) {
        var view = this.findView(btn);
        var formview = this.findSearchformView(btn);
        view.setActiveItem(formview);
        return formview;
    },
    doAdvancedSearchClear:function(btn){//清除检索条件页面所有控件的输入值
        this.findView(btn).down('summarizationFormView').getForm().reset();//表单重置
    },
    doAdvancedSearchClose:function(){
        parent.closeObj.close(parent.layer.getFrameIndex(window.name));
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
    },
    doSummarization:function (btn) {
        var treeView=btn.findParentByType('summarization').down('[itemId=summarizationTreeId]');
        var selectWin=Ext.create('Summarization.view.SummarizationSettingView',{treeView:treeView});
        var form = treeView.findParentByType('summarization').down('[itemId=summarizationFormViewId]');
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
        for(var name in formValues){//遍历表单中的所有值
            formParams[name] = formValues[name];
        }
        formParams['nodeId'] = treeView.selection.get('fnid');
        formParams['ifSearchLeafNode'] = true;//点击非叶子节点时，是否查询出其包含的所有叶子节点数据
        formParams['ifContainSelfNode'] = false;//点击非叶子节点时，是否查询出当前非叶子节点及其包含的所有非叶子节点数据
        Ext.MessageBox.wait('正在处理请稍后...', '提示');
        Ext.Ajax.request({
            url:'/summarization/getallentryindex',
            timeout:XD.timeout,
            params:formParams,
            success:function (resp) {
            	Ext.MessageBox.hide();
                var flag = Ext.decode(resp.responseText);
                if(!flag){
					XD.msg('未检索到结果');
		        }
            }
        })
        selectWin.down('[itemId=multiItemId]').getStore().proxy.extraParams = {nodeid: treeView.selection.get('fnid')};
        selectWin.down('[itemId=multiItemId]').getStore().load();
        selectWin.show();
        Ext.MessageBox.hide();
    }
});