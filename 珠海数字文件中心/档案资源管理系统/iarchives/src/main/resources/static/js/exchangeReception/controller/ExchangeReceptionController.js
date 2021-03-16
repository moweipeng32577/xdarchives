/**
 * Created by yl on 2017/11/2.
 */
Ext.define('ExchangeReception.controller.ExchangeReceptionController', {
    extend: 'Ext.app.Controller',

    // 其实翻译出来就是“从根 app 开始找 view（注意没带 s 哦） 目录，在这个目录下找到 student 目录，然后加载 List.js 这个文件”
    views: ['ExchangeReceptionView', 'ExchangeReceptionGridView','ExchangeReceptionValidate'],//加载view
    stores: ['ExchangeReceptionGridStore'],//加载store
    models: ['ExchangeReceptionGridModel'],//加载model
    init: function () {
        var exchangeReceptionGridView;
        this.control({
            'exchangeReceptionGridView ': {
                afterrender: function (view, e, eOpts) {
                    view.initGrid();
                }
            },
            'exchangeReceptionGridView button[itemId=importXlsBtnID]': {
                click: function (view, e, eOpts) {
                    var appWindow = window.open("/exchangeReception/extportExchange?fileName=" + encodeURI("模版"));
                    appWindow.focus();
                }
            },
            'exchangeReceptionGridView button[itemId=deleteBtnID]': {
                click: function (view, e, eOpts) {
                    var exchangeReceptionGridView = view.findParentByType('exchangeReceptionGridView');
                    var records = exchangeReceptionGridView.selModel.getSelection();
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
                                        exchangeReceptionGridView.delReload(records.length);
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
            }
        });
    }
});