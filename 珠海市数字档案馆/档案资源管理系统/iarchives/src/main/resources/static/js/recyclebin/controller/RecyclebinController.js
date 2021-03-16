/**
 * Created by RonJiang on 2018/04/23
 */
Ext.define('Recyclebin.controller.RecyclebinController',{
    extend : 'Ext.app.Controller',
    views :  ['RecyclebinView','RecyclebinGridView','RecyclebinFormView'],
    stores:  ['RecyclebinGridStore'],
    models:  ['RecyclebinGridModel'],
    init : function() {
        this.control({
            'recyclebingrid':{
                afterrender:function (view) {
                    view.initGrid();
                }
            },
            'recyclebingrid button[itemId=restore]':{//还原
                click:this.restoreHandler
            },
            'recyclebingrid button[itemId=del]':{//删除
                click:this.delHandler
            },
            'recyclebingrid button[itemId=look]':{//查看
                click:this.lookHandler
            },
            'recyclebingrid button[itemId=download]':{//下载
                click:this.downloadHandler
            },
            'recyclebinform button[itemId=back]':{
                click:function (btn) {
                    this.activeGrid(btn, false);
                }
            }
        });
    },

    //获取回收管理应用视图
    findView: function (btn) {
        return btn.up('recyclebin');
    },

    //获取表单界面视图
    findFormView: function (btn) {
        return this.findView(btn).down('recyclebinform');
    },

    //获取列表界面视图
    findGridView: function (btn) {
        return this.findView(btn).down('recyclebingrid');
    },

    //切换到列表界面视图
    activeGrid: function (btn, flag) {
        var view = this.findView(btn);
        var form = this.findFormView(btn);
        var grid = this.findGridView(btn);
        view.setActiveItem(grid);
        if(flag){//根据参数确定是否需要刷新数据
            grid.notResetInitGrid();
        }
    },

    //切换到表单界面视图
    activeForm: function (btn) {
        var view = this.findView(btn);
        var formview = this.findFormView(btn);
        view.setActiveItem(formview);
        return formview;
    },

    initFormData:function(form, recycleid){
        form.reset();
        this.activeForm(form);
        Ext.Ajax.request({
            method: 'GET',
            scope: this,
            url: '/recyclebin/recyclebins/' + recycleid,
            success: function (response) {
                var recyclebin = Ext.decode(response.responseText);
                form.loadRecord({getData: function () {return recyclebin;}});
            }
        });
    },

    restoreHandler:function (btn) {//还原电子文件
        var grid = this.findGridView(btn);
        var record = grid.selModel.getSelection();
        if (record.length == 0) {
            XD.msg('请至少选择一条需要还原的电子文件');
            return;
        }
        var tmp = [];
        for (var i = 0; i < record.length; i++) {
            tmp.push(record[i].id);
        }
        var recycleids = tmp.join(',');
        XD.confirm('还原后，所选记录将被清除，电子文件将恢复到具体条目中（条目存在同名的电子文件时直接替换），是否确定还原电子文件？',function () {
            Ext.Ajax.request({
                method: 'GET',
                scope: this,
                url: '/recyclebin/restore/' + recycleids,
                success: function (response) {
                    var responseText = Ext.decode(response.responseText);
                    if(responseText.success==true){
                        XD.msg(responseText.msg);
                        grid.notResetInitGrid();
                    }
                }
            });
        },this);
    },

    delHandler:function (btn) {//彻底删除文件
        var grid = this.findGridView(btn);
        var record = grid.selModel.getSelection();
        if (record.length == 0) {
            XD.msg('请至少选择一份需要删除的电子文件');
            return;
        }
        XD.confirm('确定要彻底删除这' + record.length + '份电子文件吗',function(){
            var tmp = [];
            for (var i = 0; i < record.length; i++) {
                tmp.push(record[i].id);
            }
            var recycleids = tmp.join(',');
            Ext.Ajax.request({
                method: 'POST',
                url: '/recyclebin/thoroughDelete',
                params: {recycleids:recycleids},
                success: function (response) {
                    XD.msg(Ext.decode(response.responseText).msg);
                    grid.delReload(record.length);
                }
            })
        },this);
    },

    lookHandler: function (btn) {//查看回收数据
        var grid = this.findGridView(btn);
        var record = grid.selModel.getSelection();
        if (record.length != 1) {
            XD.msg('请选择一条需要查看的数据');
            return;
        }
        var recycleid = record[0].get('recycleid');
        var form = this.findFormView(btn);
        this.initFormData(form, recycleid);
    },

    downloadHandler:function (btn) {
        var grid = this.findGridView(btn);
        var record = grid.selModel.getSelection();
        if (record.length < 1) {
            XD.msg('请选择需要下载的电子文件数据');
            return;
        }
        var tmp = [];
        for (var i = 0; i < record.length; i++) {
            tmp.push(record[i].id);
        }
        var recycleids = tmp.join(',');
        location.href = '/recyclebin/download/' + recycleids;
    }
});