/**
 * Created by RonJiang on 2018/2/27 0027
 */
Ext.define('ConsultStandingBook.controller.ConsultStandingBookController', {
    extend: 'Ext.app.Controller',
    views: ['StatisticsGridView','StandingBookManagementView','StatisticsPrintView'],
    stores: ['ConsultStandingBookStore'],
    models: ['ConsultStandingBookModel'],
    init: function () {
        var statisticsGridView;
        this.control({
            'statisticsGridView': {
                afterrender: function (view) {
                    view.initGrid();
                }
            }, 'statisticsGridView button[itemId=findId]': {//搜索
                click: function (btn) {
                    var view=btn.findParentByType('statisticsGridView');
                    var startdate=Ext.util.Format.date(view.down('[itemId=startdateid]').getRawValue(), 'Y-m-d');
                    var enddate=Ext.util.Format.date(view.down('[itemId=enddateid]').getRawValue(), 'Y-m-d');
                    var store=view.getStore();
                    store.proxy.extraParams = {startdate:startdate, enddata:enddate};
                    store.reload();
                }
            },'statisticsGridView button[itemId=managementId]': {//管理
                click: function (btn) {
                    statisticsGridView = btn.findParentByType('statisticsGridView');
                    var win = Ext.create('Ext.window.Window', {
                        height: '69%',
                        width: '70%',
                        draggable: false,//禁止拖动
                        resizable: false,//禁止缩放
                        modal: true,
                        closeToolText: '关闭',
                        title: '台账管理',
                        closeAction: 'hide',
                        layout: 'fit',
                        closable: false,
                        items: [{xtype: 'standingBookManagementView'}]
                    });
                    var dateTime= Ext.util.Format.date(win.down('[itemId=consultDateId]').getRawValue(), 'Y-m-d');
                    loadConsultStatistics(win.down('standingBookManagementView'),dateTime);
                    win.show();
                }
            },
            'standingBookManagementView button[itemId=managementClose]': {//管理界面关闭
                click: function (btn) {
                    statisticsGridView.getStore().reload();
                    btn.findParentByType('window').close();
                }
            },
            'standingBookManagementView button[itemId=statistics]': {//统计
                click: function (btn) {
                    var view=btn.findParentByType('standingBookManagementView');
                    var dateTime=Ext.util.Format.date(view.down('[itemId=consultDateId]').getRawValue(), 'Y-m-d');
                    Ext.MessageBox.wait('正在处理请稍后...');
                    Ext.Ajax.request({
                        url: '/consultStatistics/consultStatistics',
                        params: {
                            dateTime: dateTime
                        },
                        timeout:XD.timeout,
                        success: function (response) {
                            Ext.MessageBox.hide();
                            XD.msg('统计完成');
                            var data = Ext.decode(response.responseText).data;
                            ergodicData(data,view);
                        }, failure: function () {
                            Ext.MessageBox.hide();
                            XD.msg('操作失败');
                        }
                    })
                }
            },'standingBookManagementView button[itemId=save]': {//保存修改
                click: function (btn) {
                    var formView = btn.findParentByType('standingBookManagementView');
                    var dateTime=Ext.util.Format.date(formView.down('[itemId=consultDateId]').getValue(), 'Y-m-d');
                    //var data=formView.getValues();
                    if(!formView.isValid()){
                        XD.msg("有选项填写错误，请处理");
                        return;
                    }
                    XD.confirm('是否修改日期'+dateTime+"的统计记录",function() {
                        Ext.MessageBox.wait('正在处理请稍后...');
                        formView.submit({
                            url: '/consultStatistics/updateStatistics',
                            method: 'POST',
                            success: function (response,action) {
                                Ext.MessageBox.hide();
                                XD.msg(action.result.msg);
                            },failure: function() {
                                Ext.MessageBox.hide();
                                XD.msg('操作失败');
                            }
                        })
                    },this)
                }
            },'standingBookManagementView button[itemId=delete]': {//删除
                click: function (btn) {
                    var formView = btn.findParentByType('standingBookManagementView');
                    var dateTime=Ext.util.Format.date(formView.down('[itemId=consultDateId]').getValue(), 'Y-m-d');
                    XD.confirm('是否删除日期'+dateTime+"所有的统计记录",function() {
                        Ext.Ajax.request({
                            url: '/consultStatistics/deleteConsultStatistics',
                            params: {
                                dateTime: dateTime
                            },
                            success: function (response) {
                                var msg = Ext.decode(response.responseText).msg;
                                XD.msg(msg);
                            }, failure: function () {
                                XD.msg('操作失败');
                            }
                        })
                    },this)
                }
            }, 'statisticsGridView button[itemId=printId]': {//打开打印界面
                click: function (btn) {
                    var params={};
                    var view=btn.findParentByType('statisticsGridView');
                    var startDate=Ext.util.Format.date(view.down('[itemId=startdateid]').getRawValue(), 'Y-m-d');
                    var endDate=Ext.util.Format.date(view.down('[itemId=enddateid]').getRawValue(), 'Y-m-d');
                    //var reportName=formView.down('[itemId=reportTypeId]').getValue();
                    if (startDate != '' && startDate != undefined) {
                        params['starttime'] = startDate;
                    }
                    if (endDate != '' && endDate != undefined) {
                        params['endtime'] = endDate;
                    }
                    XD.UReportPrint("查档台账统计", "统计管理_查档类型统计表", params);
                    // var window = Ext.create('ConsultStandingBook.view.StatisticsPrintView');
                    // window.show();
                }
            },'statisticsPrintView button[itemId=printID]': {//打印
                click: function (btn) {
                    var params={};
                    var formView = btn.findParentByType('statisticsPrintView');
                    var statrDate=Ext.util.Format.date(formView.down('[itemId=startdateid]').getValue(), 'Y-m-d');
                    var endDate=Ext.util.Format.date(formView.down('[itemId=enddateid]').getValue(), 'Y-m-d');
                    var reportName=formView.down('[itemId=reportTypeId]').getValue();
                    if (statrDate != '' && statrDate != undefined) {
                        params['starttime'] = statrDate;
                    }
                    if (endDate != '' && endDate != undefined) {
                        params['endtime'] = endDate;
                    }
                    XD.UReportPrint("查档台账统计", "统计管理_"+reportName, params);
                }
            },'statisticsPrintView button[itemId=closeBtnID]': {//打印界面关闭
                click: function (view, e, eOpts) {
                    var window = view.up('statisticsPrintView');
                    window.close();
                }
            }
        })
    },
})

function loadConsultStatistics(view,dateTime) {
    view.form.reset();
    view.down('[itemId=consultDateId]').setValue(dateTime);
    Ext.Ajax.request({
        url: '/consultStatistics/findConsultStatisticsByDateTime',
        params: {
            dateTime: dateTime
        },
        success: function (response) {
            var data = Ext.decode(response.responseText).data;
            ergodicData(data,view);
        },failure: function() {
            XD.msg('操作失败');
        }
    })
}

function ergodicData(data,view) {
    for (var i = 0; i < data.length; i++) {
        if (data[i].type == "文书档案") {
            setFindValue(data[i],view,'wsFormId');
        }else if (data[i].type == "婚姻档案") {
            setFindValue(data[i],view,'hyFormId');
        }else if(data[i].type=="退伍档案"){
            setFindValue(data[i],view,'twFormId');
        }else if(data[i].type=="人员/已故人员档案"){
            setFindValue(data[i],view,'ryFormId');
        }else if(data[i].type=="土地档案"){
            setFindValue(data[i],view,'tdFormId');
        }else if(data[i].type=="林政档案"){
            setFindValue(data[i],view,'lzFormId');
        }else if(data[i].type=="合同档案"){
            setFindValue(data[i],view,'htFormId');
        }else if(data[i].type=="科技/城建/基建档案"){
            setFindValue(data[i],view,'jjFormId');
        }else if(data[i].type=="业务/工龄档案"){
            setFindValue(data[i],view,'ywglFormId');
        }else if(data[i].type=="其他档案/资料"){
            setFindValue(data[i],view,'qtzlFormId');
        }else if(data[i].type=="电话/现场咨询"){
            setFindValue(data[i],view,'dhxcFormId');
        }
    }
}

//将统计数据填写到输入框
function setFindValue(data,view,form) {
    var form = view.items.get(form);
    form.items.get(1).setValue(data.type);
    form.items.get(2).setValue(data.company);
    form.items.get(3).setValue(data.personal);
    form.items.get(4).setValue(data.volume);
    form.items.get(5).setValue(data.piece);
    form.items.get(6).setValue(data.tocopy);
    form.items.get(7).setValue(data.prove);
}