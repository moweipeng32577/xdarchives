/**
 * Created by RonJiang on 2018/1/22 0022.
 */
Ext.define('Backup.controller.BackupController', {
    extend: 'Ext.app.Controller',
    views: ['BackupView','BackupSettingView','BackupDataView','BackupDownloadGridView',
        'BackupHistoryGridView','BackupStrategyView','BackupDatabaseView'],
    models: ['BackupSettingModel','BackupDataModel','BackupDownloadGridModel'],
    stores: ['BackupSettingStore','BackupDataStore','BackupDownloadGridStore'],
    init:function () {
        this.control({
            'backupSetting button[itemId=settingBackupBtn]':{
                click:function (btn) {
                    var tree = btn.findParentByType('backup').down('[itemId=settingTreeId]');
                    var records = tree.getView().getChecked();
                    if (records.length == 0) {
                        XD.msg('请选择需要备份的设置');
                        return;
                    }
                    XD.confirm('是否确定将选中的数据进行备份',function(){
                        this.backup(records,'setting');
                    },this);
                }
            },
            'backupData button[itemId=dataBackupBtn]':{
                click:function (btn) {
                    var tree = btn.findParentByType('backup').down('[itemId=dataTreeId]');
                    var records = tree.getView().getChecked();
                    if (records.length == 0) {
                        XD.msg('请选择需要备份的数据');
                        return;
                    }
                    XD.confirm('是否确定将选中的数据进行备份',function(){
                        this.backup(records,'data');
                    },this);
                }
            },
            'backupSetting button[itemId=historygrid]':{
                click:function () {
                    var win=Ext.create('Backup.view.BackupHistoryGridView');
                    win.down('[itemId=backupDownloadGridItem]').getStore().proxy.extraParams={tab:'设置数据'};
                    win.down('[itemId=backupDownloadGridItem]').getStore().load();
                    win.show();
                }
            },
            'backupData button[itemId=historygrid]':{
                click:function () {
                    var win=Ext.create('Backup.view.BackupHistoryGridView');
                    win.down('[itemId=backupDownloadGridItem]').getStore().proxy.extraParams={tab:'业务数据'};
                    win.down('[itemId=backupDownloadGridItem]').getStore().load();
                    win.show();
                }
            },
            'backupSetting button[itemId=settingBackupStrategy]':{
                click:function () {
                    var backupStrategyWin = Ext.create('Ext.window.Window',{
                        width:'35%',
                        height:'52%',
                        title:'设置数据备份策略',
                        draggable : true,//允许拖动
                        resizable : false,//禁止缩放
                        modal:true,
                        closeToolText:'关闭',
                        layout:'fit',
                        items:[{
                            xtype: 'backupStrategyView',
                            backupContent:'setting'
                        }]
                    });
                    this.initFormData(backupStrategyWin);
                    backupStrategyWin.show();
                }
            },
            'backupData button[itemId=dataBackupStrategy]':{
                click:function () {
                    var backupStrategyWin = Ext.create('Ext.window.Window',{
                        width:'35%',
                        height:'52%',
                        title:'业务数据备份策略设置',
                        draggable : true,//允许拖动
                        resizable : false,//禁止缩放
                        modal:true,
                        closeToolText:'关闭',
                        layout:'fit',
                        items:[{
                            xtype: 'backupStrategyView',
                            backupContent:'data'
                        }]
                    });
                    this.initFormData(backupStrategyWin);
                    backupStrategyWin.show();
                }
            },
            'backupStrategyView [itemId=backupStrategySave]':{
                click:function (btn) {
                    var backupStrategyForm = btn.up('form');
                    var backupStrategyWin = btn.up('window');
                    var formValues = backupStrategyForm.getForm().getValues();
                    var backupFrequency = formValues['backupfrequency'];
                    var backupTime = formValues['backuptime'];
                    var backupType = formValues['backuptype'];
                    if(backupFrequency==''){
                        XD.msg('请选择备份频率');
                        return;
                    }
                    if(backupTime==''){
                        XD.msg('请选择备份时间');
                        return;
                    }
                    if(backupType==''){
                        XD.msg('请选择备份方式');
                        return;
                    }
                    var backupStrategySaveConfirmMsg = '本次操作将覆盖此前已设置的'+(backupStrategyForm.backupContent=='setting'?'设置':'业务')+'数据备份策略，是否继续？';
                    XD.confirm(backupStrategySaveConfirmMsg,function () {
                        Ext.Ajax.request({
                            url: '/backupRestore/saveBackupStrategy',
                            async:false,
                            params:{
                                backupFrequency:backupFrequency,
                                backupTime:backupTime,
                                backupType:backupType,
                                backupContent:backupStrategyForm.backupContent
                            },
                            success: function (resp) {
                                XD.msg(Ext.decode(resp.responseText).msg);
                                backupStrategyWin.close();
                            },
                            failure:function () {
                                XD.msg('操作失败');
                            }
                        });
                    },this);
                }
            },
            'backupStrategyView [itemId=backupStrategyBack]':{
                click:function (btn) {
                    btn.up('window').close();
                }
            },
            'backupDownloadGrid [itemId=close]':{
                click:function (btn) {
                    btn.findParentByType('BackupHistoryGridView').close();
                }
            },
            'backupDownloadGrid [itemId=delete]':{
                click:function (btn) {
                    var backupGrid=btn.findParentByType('backupDownloadGrid');
                    var select = backupGrid.getSelectionModel();
                    if (!select.hasSelection()) {
                        XD.msg("请选择操作记录!");
                    } else {
                        XD.confirm("是否确定删除选中的备份数据", function () {
                            var gridselections = select.getSelection();
                            var array = [],filename;
                            for (var i = 0; i < gridselections.length; i++) {
                                array[i] = gridselections[i].get("filename");
                            }
                            Ext.Ajax.request({
                                params: {filenames: array},
                                url: '/backupRestore/deletebackup',
                                method: 'post',
                                success: function (resp) {
                                    backupGrid.getStore().load();
                                    var respText = Ext.decode(resp.responseText);
                                    XD.msg(respText.msg);
                                },
                                failure: function () {
                                    XD.msg('删除失败');
                                }
                            });
                        });
                    }
                }
            },
            'backupDownloadGrid [itemId=download]': {
                click: function (btn) {
                    var backupGrid=btn.findParentByType('backupDownloadGrid');
                    var select = backupGrid.getSelectionModel();
                    if (select.selected.items.length<1) {
                        XD.msg("请选择操作记录");
                    }else if(select.selected.items.length>1){
                        XD.msg("只能一条操作记录");
                    } else {
                        var downloadForm = document.createElement("form");
                        document.body.appendChild(downloadForm);
                        downloadForm.action = "/backupRestore/downloadbackup/" + select.selected.items[0].get("filename");
                        console.log(downloadForm);
                        downloadForm.submit();
                    }
                }
            },
            'backupSetting [itemId=settingTreeId]':{
                render:function (view) {//考虑是否可以合并//TODO
                    view.on('checkchange', function(node, checked) {
                        node.expand();
                        node.eachChild(function(child) {
                            child.set("checked",checked);
                        });
                        if(node.parentNode!=null){
                            var allchecked=true;
                            Ext.each(node.parentNode.childNodes, function (node) {
                                if(!node.get('checked')){
                                    allchecked=false;
                                }
                            });
                            if(allchecked){
                                node.parentNode.set("checked",true);
                            }else{
                                node.parentNode.set("checked",false);
                            }
                        }
                    }, view);
                }
            },
            'backupData [itemId=dataTreeId]':{
                render:function (view) {
                    view.on('checkchange', function(node, checked) {
                        node.expand();
                        node.eachChild(function(child) {
                            child.set("checked",checked);
                            // child.fireEvent('checkchange', child, checked);
                        });
                        if(node.parentNode!=null){
                            var allchecked=true;
                            Ext.each(node.parentNode.childNodes, function (node) {
                                if(!node.get('checked')){
                                    allchecked=false;
                                }
                            });
                            if(allchecked){
                                node.parentNode.set("checked",true);
                            }else{
                                node.parentNode.set("checked",false);
                            }

                        }
                    }, view);
                }
            },
            //整库备份-执行备份
            'backupDatabase button[itemId=backupAll]':{
                click:this.backupDatabase
            },
            //整库备份-备份管理（记录）
            'backupDatabase button[itemId=historygrid]':{
                click:this.backupDatabaseHistory
            },
            //整库备份-备份策略
            'backupDatabase button[itemId=dataBackupStrategy]':{
                click:this.backupDatabaseStrategy
            }
        })
    },

    //执行备份
    backup:function (records,backupcontent) {
        var ids = [];
        for (var i = 0; i < records.length; i++) {
            if(typeof(records[i].get('fnid'))!=='undefined'){
                ids.push(records[i].get('fnid'));
            }
        }
        Ext.Msg.wait('正在进行备份，请耐心等待……','正在操作');
        Ext.Ajax.request({
            params: {
                fnidarr:ids.join(','),
                backupContent:backupcontent
            },
            url: '/backupRestore/backup',
            method: 'POST',
            timeout:XD.timeout,
            success: function (resp) {
            	Ext.MessageBox.hide();
                var respText = Ext.decode(resp.responseText);
                XD.msg(respText.msg);
            },
            failure: function () {
            	Ext.MessageBox.hide();
                XD.msg("备份失败!");
            }
        });
    },

    initFormData:function (win) {
        var backupStrategyForm = win.down('backupStrategyView');
        var backupStrategy;
        Ext.Ajax.request({
            url: '/backupRestore/getBackupStrategy',
            async:false,
            params:{
                backupContent:backupStrategyForm.backupContent
            },
            success: function (resp) {
                backupStrategy = Ext.decode(resp.responseText).data;
            },
            failure:function () {
                XD.msg('操作失败');
            }
        });
        backupStrategyForm.loadRecord({getData: function () {return backupStrategy;}});
    },

    //数据库备份
    backupDatabase:function(){
        XD.confirm("确认备份所有数据？", function () {
            Ext.Ajax.request({
                url: '/backupRestore/backupdatabasefull',
                method: 'get',
                params: {userid:userid},
                success: function (resp) {
                    var respText = Ext.decode(resp.responseText);
                    XD.msg(respText.msg);
                    var socket = new SockJS("/websocket");
                    stompClient = Stomp.over(socket);
                    var backupFlag=true;
                    stompClient.connect({}, function(frame) {
                        stompClient.subscribe('/user/'+userid+'/backupdatabase', function(respnose){
                           var progressText = respnose.body;//
                           if (backupFlag) {
                               Ext.Msg.alert("提示",progressText);
                               backupFlag=false;
                           }
                        });
                    });
                },
                failure: function () {
                    XD.msg('备份失败');
                }
            });
        });
    },

    //数据库备份记录
    backupDatabaseHistory:function(){
        var win=Ext.create('Backup.view.BackupHistoryGridView');
        win.down('[itemId=backupDownloadGridItem]').getStore().proxy.extraParams={tab:'数据库备份'};
        win.down('[itemId=backupDownloadGridItem]').getStore().load();
        win.show();
    },

    //数据库备份策略
    backupDatabaseStrategy:function(){
        var backupStrategyWin = Ext.create('Ext.window.Window',{
            width:'35%',
            height:'52%',
            title:'数据库备份策略设置',
            draggable : true,//允许拖动
            resizable : false,//禁止缩放
            modal:true,
            closeToolText:'关闭',
            layout:'fit',
            items:[{
                xtype: 'backupStrategyView',
                backupContent:'database'
            }]
        });
        this.initFormData(backupStrategyWin);
        backupStrategyWin.show();
    }

});