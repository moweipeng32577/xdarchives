/**
 * Created by Administrator on 2018/11/8.
 */

Ext.define('Thematicelectronic.controller.ThematicElectronicController', {
    extend: 'Ext.app.Controller',

    views: ['ThematicEleView','ThematicDetailGridView'],//加载view
    stores: ['ThematicDetailGridStore'],//加载store
    models: ['ThematicDetailGridModel'],//加载model
    init: function () {
        this.control({
            'thematicDetailGridView': {
                afterrender: function (view, e, eOpts) {
                    view.initGrid({thematicId:thematicid});
                }
            },
            'thematicDetailGridView button[itemId=seeBtnID]': {
                click: function (view) {
                    var thematicDetailGridView = view.findParentByType('thematicDetailGridView');
                    var select = thematicDetailGridView.getSelectionModel();
                    var records = select.getSelection();
                    if (records.length == 0) {
                        XD.msg('请选择数据');
                        return;
                    } else if (records.length > 1) {
                        XD.msg('查看只能选中一条数据');
                        return;
                    } else {
                        var record = records[0];
                        window.leadIn = Ext.create("Ext.window.Window", {
                            width: '100%',
                            height: '100%',
                            title: '专题电子文件',
                            modal: true,
                            header: false,
                            draggable: false,//禁止拖动
                            resizable: false,//禁止缩放
                            closeToolText: '关闭',
                            layout: 'fit',
                            items: [{xtype: 'thematicEleView'}]
                        });
                        Ext.on('resize', function (a, b) {
                            window.leadIn.setPosition(0, 0);
                            window.leadIn.fitContainer();
                        });
                        window.wmedia = 'undefined';
                        window.leadIn.down('thematicEleView').initData(record.data.thematicdetilid);
                        window.leadIn.show();
                    }
                }
            }
        });
    }
});
