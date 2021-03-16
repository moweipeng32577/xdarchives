/**
 * Created by Administrator on 2020/10/12.
 */


Ext.define('SupervisionWork.controller.SupervisionWorkController',{
    extend : 'Ext.app.Controller',
    views :  [
        'OrganTreeView','SupervisionWorkCenterView','SupervisionWorkView','SupervisionWorkElectronicView'
    ],
    stores:  [
        'SelectYearStore'
    ],
    models:  [
    ],
    init : function() {
        this.control({
            'supervisionWorkView':{
                afterrender:function (view) {
                    var selectYear = view.down('[itemId=selectYearId]');
                    selectYear.getStore().load();
                }
            },
            'supervisionWorkView button[itemId=topSearchBtn]': {  //检索
                click: function (view) {
                    var supervisionWorkView = view.findParentByType('supervisionWorkView');
                    var selectOrgan = supervisionWorkView.down('[itemId=selectOrganId]').getValue();
                    var selectYear = supervisionWorkView.down('[itemId=selectYearId]').getValue();
                    if (!selectOrgan || !selectYear) {
                        XD.msg('请选择具体机构、年度条件！');
                        return;
                    }
                    var formview = supervisionWorkView.down('[itemId=fieldsetFormId]');
                    formview.reset();
                    formview.load({
                        params: {
                            organid: selectOrgan,
                            selectyear: selectYear
                        },
                        url: '/supervisionWork/getSupervisionWork',
                        success: function (form, action) {
                            var data = action.result.data;
                            var receivetype = formview.down('[name=receivetype]');
                            //默认选中复选框
                            if(data.receivetype!=null&&data.receivetype!=''){
                                var receivetypeArr = data.receivetype.split(',');
                                var receivetypeStore = receivetype.getStore();
                                var selectRecord = [];
                                for(var i=0;i<receivetypeArr.length;i++){
                                    for(var j=0;j<receivetypeStore.getCount();j++){
                                        if(receivetypeArr[i]==receivetypeStore.getAt(j).get('text')){
                                            selectRecord.push(receivetypeStore.getAt(j));
                                            break;
                                        }
                                    }
                                }
                                receivetype.select(selectRecord);
                            }
                            Ext.Ajax.request({
                                url: '/supervisionWork/getElectronicCount',
                                params:{
                                    organid: selectOrgan,
                                    selectyear: selectYear
                                },
                                success: function (response) {
                                    var text = Ext.decode(response.responseText).data;
                                    formview.down('[itemId=fillingnamecount]').setText('共'+text.fillingnameNum+'份');
                                    formview.down('[itemId=classplannamecount]').setText('共'+text.classplannameNum+'份');
                                    formview.down('[itemId=fundsfilescount]').setText('共'+text.fundsfilesNum+'份');
                                    formview.down('[itemId=setindexcount]').setText('共'+text.setindexNum+'份');
                                    formview.down('[itemId=normativefilenamecount]').setText('共'+text.normativefilenameNum+'份');
                                }
                            });
                        },
                        failure: function () {
                            XD.msg('获取表单信息失败');
                        }
                    });
                }
            },

            'supervisionWorkCenterView button[itemId=saveAllId]':{  //保存
                click:function (view) {
                    var supervisionWorkCenterView = view.findParentByType('supervisionWorkCenterView');
                    var supervisionWorkView = view.findParentByType('supervisionWorkView');
                    var selectOrgan = supervisionWorkView.down('[itemId=selectOrganId]').getValue();
                    var selectYear = supervisionWorkView.down('[itemId=selectYearId]').getValue();
                    if(!selectOrgan||!selectYear){
                        XD.msg('请选择具体机构、年度条件！');
                        return;
                    }
                    var form = supervisionWorkCenterView.down('[itemId=fieldsetFormId]');
                    form.submit({
                        method:'POST',
                        params: {
                            organid: selectOrgan,
                            selectyear:selectYear
                        },
                        url:'/supervisionWork/setSupervisionWork',
                        success:function(response){
                            XD.msg('保存成功');
                        },
                        failure:function(){
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'supervisionWorkCenterView button[itemId=fillingnameUpId]':{  //归档-归档范围和档案保管期限表-上传
                click:function (view) {
                    var supervisionWorkCenterView = view.findParentByType('supervisionWorkCenterView');
                    var supervisionWorkView = supervisionWorkCenterView.findParentByType('supervisionWorkView');
                    var supervisionWorkElectronicView = supervisionWorkView.down('supervisionWorkElectronicView');
                    var selectOrgan = supervisionWorkView.down('[itemId=selectOrganId]').getValue();
                    var selectYear = supervisionWorkView.down('[itemId=selectYearId]').getValue();
                    if(!selectOrgan||!selectYear){
                        XD.msg('请选择具体机构、年度条件！');
                        return;
                    }
                    supervisionWorkElectronicView.organid = selectOrgan;
                    supervisionWorkElectronicView.selectyear = selectYear;
                    supervisionWorkElectronicView.savetype = 'fillingname'; //上传保存类型
                    supervisionWorkElectronicView.initData();
                    supervisionWorkView.setActiveItem(supervisionWorkElectronicView);
                }
            },

            'supervisionWorkCenterView button[itemId=classplannameUpId]':{  //整理-档案分类标识方案-上传
                click:function (view) {
                    var supervisionWorkCenterView = view.findParentByType('supervisionWorkCenterView');
                    var supervisionWorkView = supervisionWorkCenterView.findParentByType('supervisionWorkView');
                    var supervisionWorkElectronicView = supervisionWorkView.down('supervisionWorkElectronicView');
                    var selectOrgan = supervisionWorkView.down('[itemId=selectOrganId]').getValue();
                    var selectYear = supervisionWorkView.down('[itemId=selectYearId]').getValue();
                    if(!selectOrgan||!selectYear){
                        XD.msg('请选择具体机构、年度条件！');
                        return;
                    }
                    supervisionWorkElectronicView.organid = selectOrgan;
                    supervisionWorkElectronicView.selectyear = selectYear;
                    supervisionWorkElectronicView.savetype = 'classplanname'; //上传保存类型
                    supervisionWorkElectronicView.initData();
                    supervisionWorkView.setActiveItem(supervisionWorkElectronicView);
                }
            },

            'supervisionWorkCenterView button[itemId=fundsfilesUpId]':{  //保管-建立全宗卷-上传
                click:function (view) {
                    var supervisionWorkCenterView = view.findParentByType('supervisionWorkCenterView');
                    var supervisionWorkView = supervisionWorkCenterView.findParentByType('supervisionWorkView');
                    var supervisionWorkElectronicView = supervisionWorkView.down('supervisionWorkElectronicView');
                    var selectOrgan = supervisionWorkView.down('[itemId=selectOrganId]').getValue();
                    var selectYear = supervisionWorkView.down('[itemId=selectYearId]').getValue();
                    if(!selectOrgan||!selectYear){
                        XD.msg('请选择具体机构、年度条件！');
                        return;
                    }
                    supervisionWorkElectronicView.organid = selectOrgan;
                    supervisionWorkElectronicView.selectyear = selectYear;
                    supervisionWorkElectronicView.savetype = 'fundsfiles'; //上传保存类型
                    supervisionWorkElectronicView.initData();
                    supervisionWorkView.setActiveItem(supervisionWorkElectronicView);
                }
            },

            'supervisionWorkCenterView button[itemId=setindexUpId]':{  //保管-档案存放索引-上传
                click:function (view) {
                    var supervisionWorkCenterView = view.findParentByType('supervisionWorkCenterView');
                    var supervisionWorkView = supervisionWorkCenterView.findParentByType('supervisionWorkView');
                    var supervisionWorkElectronicView = supervisionWorkView.down('supervisionWorkElectronicView');
                    var selectOrgan = supervisionWorkView.down('[itemId=selectOrganId]').getValue();
                    var selectYear = supervisionWorkView.down('[itemId=selectYearId]').getValue();
                    if(!selectOrgan||!selectYear){
                        XD.msg('请选择具体机构、年度条件！');
                        return;
                    }
                    supervisionWorkElectronicView.organid = selectOrgan;
                    supervisionWorkElectronicView.selectyear = selectYear;
                    supervisionWorkElectronicView.savetype = 'setindex'; //上传保存类型
                    supervisionWorkElectronicView.initData();
                    supervisionWorkView.setActiveItem(supervisionWorkElectronicView);
                }
            },

            'supervisionWorkCenterView button[itemId=normativefilenameUpId]':{  //保管-指定本系统档案工作的规范性文件-上传
                click:function (view) {
                    var supervisionWorkCenterView = view.findParentByType('supervisionWorkCenterView');
                    var supervisionWorkView = supervisionWorkCenterView.findParentByType('supervisionWorkView');
                    var supervisionWorkElectronicView = supervisionWorkView.down('supervisionWorkElectronicView');
                    var selectOrgan = supervisionWorkView.down('[itemId=selectOrganId]').getValue();
                    var selectYear = supervisionWorkView.down('[itemId=selectYearId]').getValue();
                    if(!selectOrgan||!selectYear){
                        XD.msg('请选择具体机构、年度条件！');
                        return;
                    }
                    supervisionWorkElectronicView.organid = selectOrgan;
                    supervisionWorkElectronicView.selectyear = selectYear;
                    supervisionWorkElectronicView.savetype = 'normativefilename'; //上传保存类型
                    supervisionWorkElectronicView.initData();
                    supervisionWorkView.setActiveItem(supervisionWorkElectronicView);
                }
            }
        });
    }
});
