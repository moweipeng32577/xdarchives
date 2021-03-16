/**
 * Created by Rong on 2018/4/27.
 */
Ext.define('Shelves.controller.ShelvesController',{
    extend: 'Ext.app.Controller',

    views:['ShelvesView','ShelvesFormView','TreeComboboxView'],
    stores:['ShelvesStore','DetailStore','DeviceStore','FloorStore','RoomStore'],
    model:['ShelvesGridModel','DetailGridModel'],

    init:function(){
        this.control({
            '[itemId=shelvesgrid]':{
                itemclick:this.itemClickHandler
            },
            '[itemId=shelvesgrid] button[itemId=add]':{
                click:this.addHandler
            },
            '[itemId=shelvesgrid] button[itemId=modify]':{
                click:this.modifyHandler
            },
            '[itemId=shelvesgrid] button[itemId=del]':{
                click:this.delHandler
            },
            'shelveswindow button[itemId=save]':{
                click:this.saveHandler
            },
            'shelveswindow button[itemId=cancel]':{
                click:function(btn){
                    btn.findParentByType('shelveswindow').close();
                }
            }
        });
    },

    addHandler:function(btn){
        var shelvesView=btn.up('shelves');
        var grid=shelvesView.down('[itemId=shelvesgrid]');
        var win = this.getView('ShelvesFormView').create({
            title:'新增密集架',
            width:600,
            height:410,
            itemId:'addKfView'
        });
        //绑定子窗口到父窗口
        shelvesView.addKfView = win;
        win.grid = grid;
        win.show();
    },

    modifyHandler: function (btn) {
        var shelvesView = btn.up('shelves');
        var grid = shelvesView.down('[itemId=shelvesgrid]');
        var detailgrid = shelvesView.down('[itemId=detailgrid]');
        var select = grid.getSelectionModel().selected.items;
        if (select.length != 1) {
            XD.msg("只能选择一条数据");
            return;
        }
        var zoneid = select[0].get('zoneid');
        var win = this.getView('ShelvesFormView').create({
            title: '修改密集架（暂不支持修改列数、行数、层数、容量、固定列）',
            width: 600,
            height: 410,
            itemId: 'modifyKfView'
        });
        var form = win.down('form');
        form.load({
            url: '/shelves/getZoneShel',
            method: 'POST',
            params: {
                zoneid: zoneid
            },
            success: function (form, action) {
            },
            failure: function () {
                XD.msg('操作失败');
            }
        });
        //绑定子窗口到父窗口
        shelvesView.modifyKfView = win;
        win.grid = grid;
        win.detailgrid = detailgrid;
        win.show();
    },

    delHandler:function(btn){
        var grid = btn.up('shelves').down('[itemId=shelvesgrid]');
        var select = grid.getSelectionModel();
        var shelvesSel = select.selected.items;
        var selectCount = shelvesSel.length;
        if(selectCount>1){
            XD.msg('只能选一个库房');
            return;
        }else if(selectCount<1){
            XD.msg('请选定一个库房');
            return;
        }
        var zoneid=shelvesSel[0].get('zoneid');
        var zoneName=shelvesSel[0].get('zonedisplay');
        var msgStr='是否确定删除【'+zoneName+'】';
        XD.confirm(msgStr,function () {
            Ext.Ajax.request({
                params: {zoneid: zoneid},
                url: '/shelves/del',
                method: 'post',
                sync: true,
                success: function (response) {
                    var responseText = Ext.decode(response.responseText);
                    if(responseText.success==true){
                        XD.msg(responseText.msg);
                    }
                    grid.getStore().reload();
                },
                failure: function () {
                    XD.msg('库房有存档，不能删除');
                    grid.getStore().reload();
                }
            });
        });

    },

    saveHandler:function(btn){
        var formpanel = btn.up('shelveswindow').down('form');
        var values = formpanel.getValues();
        var win= btn.up('shelveswindow');
        if(win.title.substring(0,5) == '修改密集架'){
            var zoneid = formpanel.down('[name=zoneid]').getValue();
            var result = this.isHasStorages(zoneid);
            if(result){
                XD.msg('该密集架已经进行入库出库，无法修改');
                return;
            }
        }
        if(!formpanel.getForm().isValid()){
            XD.msg('存在必填项没有填写');
            return;
        }
        Ext.Msg.wait('正在初始化库房，请耐心等待……','正在操作');
        formpanel.getForm().submit({
            url:'/shelves/zone',
            scope: this,
            success: function (form, action) {
                var respText = Ext.decode(action.response.responseText);
                if (respText.success == true) {
                    Ext.Msg.close();
                    XD.msg(respText.msg);
                    win.close();//添加成功后关闭窗口
                    win.grid.getStore().reload();
                    if(win.detailgrid){
                        win.detailgrid.getStore().reload();
                    }
                } else {
                    Ext.Msg.close();
                    XD.msg(respText.msg);
                }
            },
            failure: function (form, action) {
                Ext.Msg.wait('增加库房中断','正在操作').hide();
                XD.msg('增加库房中断');
            }
        })
    },

    itemClickHandler:function(view, record, item){
        var zoneid = record.get('zoneid');
        var detailgrid = view.findParentByType('shelves').down('[itemId=detailgrid]');
        /*detailgrid.getStore().load({
         url:'/shelves/zone/'+zoneid
         });*/
        detailgrid.getStore().proxy.url='/shelves/zone/'+zoneid;
        detailgrid.getStore().loadPage(1);
    },

    isHasStorages:function(zoneid){//根据id判断密集架是否进行了入库出库
        var result;
        Ext.Ajax.request({
            url: '/shelves/isHasStorages',
            async:false,
            params:{
                zoneid:zoneid
            },
            success: function (response) {
                result = Ext.decode(response.responseText);
            },
            failure:function(){
                XD.msg('操作失败！');
            }
        });
        return result;
    }

});