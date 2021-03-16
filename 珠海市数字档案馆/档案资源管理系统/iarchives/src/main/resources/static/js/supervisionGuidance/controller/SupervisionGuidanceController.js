/**
 * Created by Administrator on 2020/7/8.
 */


Ext.define('SupervisionGuidance.controller.SupervisionGuidanceController',{
    extend : 'Ext.app.Controller',
    views :  [
        'OrganTreeView','SupervisionGuidanceCenterTopView','SupervisionGuidanceCenterView','SupervisionGuidanceFileUserGridView',
        'SupervisionGuidanceLeaderGridView','SupervisionGuidanceOrganGridView','SupervisionGuidanceView',
        'SupervisionGuidanceWorkFundsGridView','SupervisionGuidanceWorkPlanGridView'
    ],
    stores:  [
        'SelectYearStore','SupervisionGuidanceFileUserGridStore','SupervisionGuidanceLeaderGridStore',
        'SupervisionGuidanceOrganGridStore','SupervisionGuidanceWorkFundsGridStore','SupervisionGuidanceWorkPlanGridStore'
    ],
    models:  [
        'SupervisionGuidanceFileUserGridModel','SupervisionGuidanceLeaderGridModel','SupervisionGuidanceOrganGridModel',
        'SupervisionGuidanceWorkFundsGridModel','SupervisionGuidanceWorkPlanGridModel'
    ],
    init : function() {
        this.control({
            'supervisionGuidanceView':{
                afterrender:function (view) {
                    var selectYear = view.down('[itemId=selectYearId]');
                    window.supervisionGuidanceLeaderGridView = view.down('supervisionGuidanceLeaderGridView');
                    window.supervisionGuidanceOrganGridView = view.down('supervisionGuidanceOrganGridView');
                    window.supervisionGuidanceFileUserGridView = view.down('supervisionGuidanceFileUserGridView');
                    window.supervisionGuidanceWorkFundsGridView = view.down('supervisionGuidanceWorkFundsGridView');
                    window.supervisionGuidanceWorkPlanGridView = view.down('supervisionGuidanceWorkPlanGridView');
                    selectYear.getStore().load();
                }
            },

            'supervisionGuidanceView button[itemId=topSearchBtn]': {  //检索
                click: function (view) {
                    var supervisionGuidanceView = view.findParentByType('supervisionGuidanceView');
                    var supervisionGuidanceTabView = supervisionGuidanceView.down('supervisionGuidanceTabView');
                    var selectOrgan = supervisionGuidanceView.down('[itemId=selectOrganId]').getValue();
                    var selectYear = supervisionGuidanceView.down('[itemId=selectYearId]').getValue();
                    if (!selectOrgan || !selectYear) {
                        XD.msg('请选择具体机构、年度条件！');
                        return;
                    }
                    var supervisionGuidanceLeaderGridView = supervisionGuidanceView.down('supervisionGuidanceLeaderGridView');
                    supervisionGuidanceLeaderGridView.initGrid({
                        organid: selectOrgan,
                        selectyear: selectYear
                    });
                    var supervisionGuidanceOrganGridView = supervisionGuidanceView.down('supervisionGuidanceOrganGridView');
                    supervisionGuidanceOrganGridView.initGrid({
                        organid: selectOrgan,
                        selectyear: selectYear
                    });
                    var supervisionGuidanceFileUserGridView = supervisionGuidanceView.down('supervisionGuidanceFileUserGridView');
                    supervisionGuidanceFileUserGridView.initGrid({
                        organid: selectOrgan,
                        selectyear: selectYear
                    });
                    var supervisionGuidanceWorkFundsGridView = supervisionGuidanceView.down('supervisionGuidanceWorkFundsGridView');
                    supervisionGuidanceWorkFundsGridView.initGrid({
                        organid: selectOrgan,
                        selectyear: selectYear
                    });
                    var supervisionGuidanceWorkPlanGridView = supervisionGuidanceView.down('supervisionGuidanceWorkPlanGridView');
                    supervisionGuidanceWorkPlanGridView.initGrid({
                        organid: selectOrgan,
                        selectyear: selectYear
                    });
                    var form = supervisionGuidanceView.down('[itemId=fieldsetFormId]');
                    form.reset();
                    form.load({
                        params: {
                            organid: selectOrgan,
                            selectyear: selectYear
                        },
                        url: '/supervisionGuidance/getGuidanceSafeKeep',
                        success: function (response) {
                        },
                        failure: function () {
                            XD.msg('获取表单信息失败');
                        }
                    });
                }
            },

            'supervisionGuidanceCenterView button[itemId=saveAllId]':{  //保存
                click:function (view) {
                    var supervisionGuidanceCenterView = view.findParentByType('supervisionGuidanceCenterView');
                    var supervisionGuidanceView = view.findParentByType('supervisionGuidanceView');
                    var selectOrgan = supervisionGuidanceView.down('[itemId=selectOrganId]').getValue();
                    var selectYear = supervisionGuidanceView.down('[itemId=selectYearId]').getValue();
                    if(!selectOrgan||!selectYear){
                        XD.msg('请选择具体机构、年度条件！');
                        return;
                    }
                    var supervisionGuidanceLeaderGridView = supervisionGuidanceCenterView.down('supervisionGuidanceLeaderGridView');
                    var supervisionGuidanceOrganGridView = supervisionGuidanceCenterView.down('supervisionGuidanceOrganGridView');
                    var supervisionGuidanceFileUserGridView = supervisionGuidanceCenterView.down('supervisionGuidanceFileUserGridView');
                    var supervisionGuidanceWorkFundsGridView = supervisionGuidanceCenterView.down('supervisionGuidanceWorkFundsGridView');
                    var supervisionGuidanceWorkPlanGridView = supervisionGuidanceCenterView.down('supervisionGuidanceWorkPlanGridView');
                    var leaderItem = supervisionGuidanceLeaderGridView.getStore().getNewRecords();
                    var organItem = supervisionGuidanceOrganGridView.getStore().getNewRecords();
                    var fileuserItem = supervisionGuidanceFileUserGridView.getStore().getNewRecords();
                    var workfundsItem = supervisionGuidanceWorkFundsGridView.getStore().getNewRecords();
                    var workplanItem = supervisionGuidanceWorkPlanGridView.getStore().getNewRecords();
                    var leaderData = [];
                    var organData = [];
                    var fileuserData = [];
                    var workfundsData = [];
                    var workplanData = [];
                    Ext.each(leaderItem,function(item){
                        leaderData.push(item.data);
                    });
                    Ext.each(organItem,function(item){
                        organData.push(item.data);
                    });
                    Ext.each(fileuserItem,function(item){
                        fileuserData.push(item.data);
                    });
                    Ext.each(workfundsItem,function(item){
                        workfundsData.push(item.data);
                    });
                    Ext.each(workplanItem,function(item){
                        workplanData.push(item.data);
                    });
                    var form = supervisionGuidanceCenterView.down('[itemId=fieldsetFormId]');
                    form.submit({
                        method:'POST',
                        params: {
                            organid: selectOrgan,
                            selectyear:selectYear,
                            leaderData:JSON.stringify(leaderData),
                            organData:JSON.stringify(organData),
                            fileuserData:JSON.stringify(fileuserData),
                            workfundsData:JSON.stringify(workfundsData),
                            workplanData:JSON.stringify(workplanData)
                        },
                        url:'/supervisionGuidance/setGuidances',
                        success:function(response){
                            XD.msg('保存成功');
                            supervisionGuidanceLeaderGridView.getStore().proxy.extraParams.organid = selectOrgan;
                            supervisionGuidanceLeaderGridView.getStore().proxy.extraParams.selectyear = selectYear;
                            supervisionGuidanceLeaderGridView.getStore().load();
                            supervisionGuidanceOrganGridView.getStore().proxy.extraParams.organid = selectOrgan;
                            supervisionGuidanceOrganGridView.getStore().proxy.extraParams.selectyear = selectYear;
                            supervisionGuidanceOrganGridView.getStore().load();
                            supervisionGuidanceFileUserGridView.getStore().proxy.extraParams.organid = selectOrgan;
                            supervisionGuidanceFileUserGridView.getStore().proxy.extraParams.selectyear = selectYear;
                            supervisionGuidanceFileUserGridView.getStore().load();
                            supervisionGuidanceWorkFundsGridView.getStore().proxy.extraParams.organid = selectOrgan;
                            supervisionGuidanceWorkFundsGridView.getStore().proxy.extraParams.selectyear = selectYear;
                            supervisionGuidanceWorkFundsGridView.getStore().load();
                            supervisionGuidanceWorkPlanGridView.getStore().proxy.extraParams.organid = selectOrgan;
                            supervisionGuidanceWorkPlanGridView.getStore().proxy.extraParams.selectyear = selectYear;
                            supervisionGuidanceWorkPlanGridView.getStore().load();
                            supervisionGuidanceView.down('[itemId=selectYearId]').selctValue = selectYear;
                            supervisionGuidanceView.down('[itemId=selectYearId]').getStore().reload();
                        },
                        failure:function(){
                            XD.msg('操作失败');
                        }
                    });
                }
            }
        });
    }
});
