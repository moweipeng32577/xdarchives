/**
 * Created by yl on 2017/11/2.
 */
Ext.define('ExchangeTransfer.controller.ExchangeTransferController', {
    extend: 'Ext.app.Controller',

    // 其实翻译出来就是“从根 app 开始找 view（注意没带 s 哦） 目录，在这个目录下找到 student 目录，然后加载 List.js 这个文件”
    views: ['ExchangeTransferView', 'ExchangeTransferGridView', 'NodesettingTreeComboboxView', 'ExchangeTransferWindow'],//加载view
    stores: ['ExchangeTransferGridStore'],//加载store
    models: ['ExchangeTransferGridModel'],//加载model
    init: function () {
        var exchangeTransferGridView;
        this.control({
            'exchangeTransferGridView ': {
                afterrender: function (view, e, eOpts) {
                    view.initGrid();
                }
            },
            'exchangeTransferGridView button[itemId=transferBtnID]': {
                click: function (view, e, eOpts) {
                    exchangeTransferGridView = view.findParentByType('exchangeTransferGridView');
                    var records = exchangeTransferGridView.selModel.getSelection();
                    if (records.length == 0) {
                        XD.msg('请选择操作记录!');
                    } else {
                        var window = Ext.create('ExchangeTransfer.view.ExchangeTransferWindow');
                        window.show();
                    }
                }
            },
            'exchangeTransferGridView button[itemId=delete]': {
                click: function (view, e, eOpts) {
                    var exchangeTransferGridView = view.findParentByType('exchangeTransferGridView');
                    var records = exchangeTransferGridView.selModel.getSelection();
                    if (records.length == 0) {
                        XD.msg('请至少选择一条需要删除的数据');
                    } else {
                        XD.confirm('确定要删除这' + records.length+ '条数据吗', function () {
                            var exchangeids = [];
                            for (var i = 0; i < records.length; i++) {
                                exchangeids.push(records[i].get('id'));
                            }
                            Ext.Ajax.request({
                                params: {exchangeids: exchangeids},
                                url: '/exchangeReception/deleteExchange',
                                method: 'POST',
                                sync: true,
                                success: function (response) {
                                    var respText = Ext.decode(response.responseText);
                                    if (respText.success == true) {
                                        XD.msg(respText.msg);
                                        exchangeTransferGridView.delReload(records.length);
                                    } else {
                                        XD.msg(respText.msg);
                                    }
                                },
                                failure: function () {
                                    XD.msg('操作失败');
                                }
                            });
                        }, this);
                    }
                }
            },
            'exchangeTransferWindow button[itemId=confirmBtnID]': {
                click: function (view, e, eOpts) {
                    var nodesettingTreeComboboxView = view.findParentByType('exchangeTransferWindow').down('nodesettingTreeComboboxView');
                    if (!nodesettingTreeComboboxView.rawValue) {
                        XD.msg('请选择档案分类');
                        return;
                    } else {
                        var exchangeTransferWindow = view.findParentByType('exchangeTransferWindow');
                        var records = exchangeTransferGridView.selModel.getSelection();
                        var form = exchangeTransferWindow.down('form');
                        var nodeId = form.getForm().findField('refid').getValue();
                        XD.confirm('确定要移交这' + records.length+ '条数据吗', function () {
                            var array = [];
                            for (var i = 0; i < records.length; i++) {
                                array.push(records[i].get('id'));
                            }
                            form.submit({
                                waitTitle: '提示',// 标题
                                waitMsg: '正在移交数据请稍后...',// 提示信息
                                clientValidation: true,
                                url: '/exchangeTransfer/exchangeTransferData',
                                method: 'POST',
                                timeout: XD.timeout,
                                params: {
                                    exchangeids: array,
                                    nodeId: nodeId
                                },
                                success: function (form, action) {
                                    var respText = Ext.decode(action.response.responseText);
                                    if (respText.success == true) {
                                        Ext.MessageBox.alert("提示", respText.msg, callBack);
                                        function callBack() {
                                            exchangeTransferWindow.close();
                                        }
                                    } else {
                                        XD.msg(respText.msg);
                                    }
                                },
                                failure: function () {
                                    XD.msg('操作失败');
                                }
                            });
                        }, this);
                    }
                }
            },
            'exchangeTransferWindow button[itemId=closeBtnID]': {
                click: function (view, e, eOpts) {
                    view.findParentByType('exchangeTransferWindow').close();
                }
            }
        });
    }
});