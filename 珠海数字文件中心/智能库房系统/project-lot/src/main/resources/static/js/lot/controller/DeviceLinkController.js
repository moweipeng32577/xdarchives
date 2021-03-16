/**
 * 设备联动管理控制器
 * Created by Rong on 2019-06-13.
 */
Ext.define('Lot.controller.DeviceLinkController', {

    extend: 'Ext.app.Controller',

    stores: ['DeviceLinkStore'],
    views: ['DeviceLinkFormView'],

    init: function () {
        this.control({
            '[itemId=eventPanel] [itemId=linkAdd]': {
                click : this.addHandler
            },
            '[itemId=eventPanel] [itemId=linkDel]':{
                click : this.delHandler
            },
            '[itemId=eventPanel] [itemId=linkUpdate]':{
                click : this.updateHandler
            }
        });
    },

    addHandler: function (btn) {
        var win = Ext.create('Ext.window.Window', {
            title: '新增设备联动',
            modal: true,
            width: 500,
            height: 320,
            items: {xtype: 'deviceLinkForm'},
            buttons: [{
                text: '确定',
                handler:function(){
                    if(!win.down('form').isValid()) {
                        XD.msg('有必填项未填写，请处理后再提交');
                        return;
                    }
                    win.down('form').submit({
                        url:'/devicelink',
                        method:'post',
                        success:function(){
                            XD.msg('增加成功');
                            btn.up('grid').getStore().reload();
                            win.close();
                        }
                    });
                }
            }, {
                text: '取消', handler: function () {
                    win.close();
                }
            }]
        });
        win.show();
    },

    delHandler:function(btn){
        var grid = btn.up('[itemId=eventPanel]').down('grid');
        var select = grid.getSelectionModel().getSelection();
        var ids = [];
        if (select.length == 0) {
            XD.msg("请选择数据");
            return;
        }else{
            for (var i = 0; i < select.length; i++) {
                ids.push(select[i].get('id'))
            }
        }
        XD.confirm("确认删除？", function () {
            Ext.Ajax.request({
                method: 'post',
                scope: this,
                params: {ids: ids},
                url: '/linkdel',
                success: function (response) {
                    var respText = Ext.decode(response.responseText);
                    XD.msg(respText.msg);
                    grid.getStore().load();
                }
            });
        },this);
    },

    updateHandler:function(btn){
        var grid = btn.up('[itemId=eventPanel]').down('grid');
        var selected = grid.getSelectionModel().selected;
        if (selected.length == 0) {
            XD.msg("请选择一条数据");
            return;
        }
        if (selected.length >1) {
            XD.msg("只能选择一条数据");
            return;
        }else {
            var win = Ext.create('Ext.window.Window', {
                title: '修改设备联动',
                modal: true,
                width: 500,
                height: 320,
                items: {xtype: 'deviceLinkForm'},
                buttons: [{
                    text: '确定',
                    handler:function(){
                        win.down('form').submit({
                            url:'/devicelink',
                            method:'post',
                            success:function(){
                                XD.msg('修改成功');
                                btn.up('grid').getStore().reload();
                                win.close();
                            }
                        });
                    }
                }, {
                    text: '取消', handler: function () {
                        win.close();
                    }
                }]
            });
            win.down('form').loadRecord(selected.items[0]);
            win.down('form').down('[name=device]').select(selected.items[0].data.device.id);//名称设备
            win.down('form').down('[name=linkArea]').select(selected.items[0].data.linkArea.id);//联动分区
            win.down('form').down('[name=linkDevice]').select(selected.items[0].data.linkDevice.id);//联动设备
            win.show();
        }
    }

});