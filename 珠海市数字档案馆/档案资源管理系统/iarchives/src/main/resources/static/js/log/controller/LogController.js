/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Log.controller.LogController', {
    extend: 'Ext.app.Controller',

    views: ['LogView', 'LogPromptView', 'LogTreeView', 'LogGridView', 'LogShowInfo'],//加载view
    stores: ['LogGridStore'],//加载store
    models: ['LogGridModel'],//加载model
    init: function () {
        this.control({
            'logGridView': {
                afterrender: function (view, e, eOpts) {
                    view.initGrid();
                },

                rowdblclick: function (view, record) {
                    showData(record);
                }
            },
            'logView':{
                tabchange:function(view){
                    var grid = view.down('logGridView');
                    if(view.activeTab.title == '档案系统'){
                        grid.initGrid({flag:'档案系统'});
                        window.flag='档案系统';
                    }else if(view.activeTab.title == '声像系统'){
                        grid.initGrid({flag:'声像系统'});
                        window.flag='声像系统';
                    }else if(view.activeTab.title == '新闻系统'){
                        grid.initGrid({flag:'新闻系统'});
                        window.flag='新闻系统';
                    }
                }
            },
            'logGridView button[itemId=logShowBtnID]': {
                click: function (view) {
                    var logGridView = view.findParentByType('logGridView');
                    var record = logGridView.getSelectionModel().getSelection();
                    if(record.length==0){
                        XD.msg('请选择一条需要查看的数据');
                        return;
                    }else if(record.length>1){
                        XD.msg('只能选择一条需要查看的数据');
                        return;
                    }
                    showData(record[0]);
                }
            },
            'logGridView button[itemId=exportOtherFormat]': {
                click: function (view) {
                    var grid = view.findParentByType('logGridView');
                    var selectAll=grid.down('[itemId=selectAll]').checked;
                    if(grid.selModel.getSelectionLength() == 0){
                        XD.msg('请至少选择一条需要导出的数据');
                        return;
                    }
                    var record = grid.selModel.getSelection();
                    var isSelectAll = false;
                    if(selectAll){
                        record = grid.acrossDeSelections;
                        isSelectAll = true;
                    }
                    var tmp = [];
                    for (var i = 0; i < record.length; i++) {
                        tmp.push(record[i].get('id'));
                    }
                    var lmids = tmp.join(",");
                    Ext.Msg.wait('正在进行导出操作，请耐心等待……','提示');
                    Ext.Ajax.request({
                        method: 'POST',
                        params: {
                            condition:grid.getStore().proxy.extraParams.condition,
                            operator:grid.getStore().proxy.extraParams.operator,
                            content:grid.getStore().proxy.extraParams.content,
                            lmids:lmids,
                            isSelectAll:isSelectAll,
                            flag:window.flag,
                            fileName:'日志管理' + getDateStr(0),
                            sheetName:'日志管理'
                        },
                        url: '/log/exportParameter',
                        timeout:XD.timeout,
                        success: function (res) {
                            var obj = Ext.decode(res.responseText).data;
                            window.location.href='/log/downloadLogFile?filePath='+encodeURIComponent(obj);
                            Ext.MessageBox.hide();
                        },
                        failure: function () {
                            Ext.MessageBox.hide();
                            XD.msg('操作中断');
                        }
                    });
                }
            },

            'logGridView button[itemId=logDeleteBtnID]': {
                click: function (view) {
                    var logGridView = view.findParentByType('logGridView');
                    var logGridStore = logGridView.getStore();
                    var select = logGridView.getSelectionModel();
                    var logDetails = select.getSelection();
                    var selectCount = logDetails.length;
                    if (selectCount==0) {
                        XD.msg('请选择操作记录');
                    } else {
                        XD.confirm('是否确定删除这'+selectCount+"条数据?", function () {
                            var array = [];
                            for (var i = 0; i < logDetails.length; i++) {
                                array[i] = logDetails[i].get("id");
                            }
                            Ext.Ajax.request({
                                params: {ids: array},
                                url: '/log/deleteLogDetail',
                                method: 'POST',
                                sync: true,
                                success: function (resp) {
                                    var respText = Ext.decode(resp.responseText);
                                    XD.msg(respText.msg);
                                    logGridView.delReload(selectCount);
                                }
                            });
                        });
                    }
                }
            },
            'logShowInfo button[itemId=logShowInfoBackID]': {
                click: function (view) {
                    var window = view.up('logShowInfo');
                    window.close();
                }
            }
        });
    }
});
function getDateStr(AddDayCount) {
    var dd = new Date();
    dd.setDate(dd.getDate()+AddDayCount);//获取AddDayCount天后的日期
    var y = dd.getFullYear();
    var m = dd.getMonth()+1;//获取当前月份的日期
    var d = dd.getDate();
    if (m >= 1 && m <= 9) {
        m = "0" + m;
    }
    if (d >= 0 && d <= 9) {
        d = "0" + d;
    }
    return y+""+m+""+d;
}
function showData(record) {
    var logShowInfoWin = Ext.create('Log.view.LogShowInfo');
    logShowInfoWin.down('form').loadRecord(record);
    logShowInfoWin.show();
    window.logShowInfoWins = logShowInfoWin;
    Ext.on('resize',function(a,b){
        window.logShowInfoWins.setPosition(0, 0);
        window.logShowInfoWins.fitContainer();
    });
}