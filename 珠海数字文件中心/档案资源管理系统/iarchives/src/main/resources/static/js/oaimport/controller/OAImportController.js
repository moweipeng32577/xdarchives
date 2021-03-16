/**
 * Created by tanly on 2018/1/22 0022.
 */
Ext.define('OAImport.controller.OAImportController', {
    extend: 'Ext.app.Controller',
    views: ['OAImportView', 'OAImportTabView'],
    init: function () {
        this.control({
            'oaimportView button[itemId=importEntries]': {
                click: function () {
                    XD.confirm("是否确认导入历史条目数据？", function () {
                        Ext.MessageBox.wait('正在进行导入，请耐心等待……','正在操作');
                        Ext.Ajax.request({
                            timeout: 1000000000,//20h
                            url: '/oaimport/importEntries',
                            method: 'post',
                            success: function (resp) {
                                Ext.MessageBox.close();
                                var respText = Ext.decode(resp.responseText);
                                Ext.Msg.alert('提示', respText.msg);
                            },
                            failure: function () {
                                Ext.MessageBox.close();
                                Ext.Msg.alert('提示', '操作失败！');
                            }
                        });
                    });
                }
            },
            'oaimportView button[itemId=importOrgan]': {
                click: function () {
                    XD.confirm("此操作将删除原有的机构和用户数据，是否确认导入新机构数据？", function () {
                        Ext.MessageBox.wait('正在进行导入，请耐心等待……', '正在操作');
                        Ext.Ajax.request({
                            timeout: 72000000,//20h
                            url: '/oaimport/importOrgan',
                            method: 'post',
                            success: function (resp) {
                                Ext.MessageBox.close();
                                var respText = Ext.decode(resp.responseText);
                                Ext.Msg.alert('提示', respText.msg);
                            },
                            failure: function () {
                                Ext.MessageBox.close();
                                Ext.Msg.alert('提示', '操作失败！');
                            }
                        });
                    });
                }
            },
            'oaimportView button[itemId=importUser]': {
                click: function () {
                    XD.confirm("此操作将覆盖原有用户数据，是否确认导入新用户数据？", function () {
                        Ext.MessageBox.wait('正在进行导入，请耐心等待……', '正在操作');
                        Ext.Ajax.request({
                            timeout: 72000000,//20h
                            url: '/oaimport/importUser',
                            method: 'post',
                            success: function (resp) {
                                Ext.MessageBox.close();
                                var respText = Ext.decode(resp.responseText);
                                Ext.Msg.alert('提示', respText.msg);
                            },
                            failure: function () {
                                Ext.MessageBox.close();
                                Ext.Msg.alert('提示', '操作失败！');
                            }
                        });
                    });
                }
            },
            'oaimportView button[itemId=roleAuthorize]': {
                click: function () {
                    var win = Ext.create('OAImport.view.RoleSelectorView');
                    win.show();
                }
            },
            'oaimportView button[itemId=nodeAuthorize]': {
                click: function () {
                    var win = Ext.create('OAImport.view.NodeSelectorView');
                    win.show();
                }
            },
            'roleSelectorView button[itemId=cancel]': {
                click: function (btn) {
                    var view = btn.findParentByType('roleSelectorView');
                    view.close();
                }
            },
            'nodeSelectorView button[itemId=cancel]': {
                click: function (btn) {
                    var view = btn.findParentByType('nodeSelectorView');
                    view.close();
                }
            },
            'roleSelectorView button[itemId=submit]': {
                click: function (btn) {
                    var view = btn.findParentByType('roleSelectorView');
                    var includeStr = view.down('[itemId=includeItem]').getValue() ? '（包含下属机构）' : '';
                    XD.confirm("此操作将对【" + view.down('[itemId=rolenameItem]').getRawValue() + "】中的用户授予对应机构" + includeStr + "的数据权限，是否确认执行？", function () {
                        Ext.MessageBox.wait('正在进行授权，请耐心等待……', '正在操作');
                        Ext.Ajax.request({
                            timeout: 100000000,
                            url: '/oaimport/authorizeByRole',
                            params:{
                                roleid:view.down('[itemId=rolenameItem]').getValue(),
                                isIncluded:view.down('[itemId=includeItem]').getValue()
                            },
                            method: 'post',
                            success: function () {
                                view.close();
                                Ext.MessageBox.close();
                                Ext.Msg.alert('提示', '授权完成！');
                            },
                            failure: function () {
                                Ext.MessageBox.close();
                                Ext.Msg.alert('提示', '操作失败！');
                            }
                        });
                    });
                }
            },
            'nodeSelectorView button[itemId=submit]': {
                click: function (btn) {
                    var view = btn.findParentByType('nodeSelectorView');
                    var includeStr = view.down('[itemId=includeItem]').getValue() ? '（包含下属机构）' : '';
                    XD.confirm("此操作将对【" + view.down('[itemId=nodeItem]').getRawValue() + "】中的用户授予对应机构" + includeStr + "的数据权限，是否确认执行？", function () {
                        Ext.MessageBox.wait('正在进行授权，请耐心等待……', '正在操作');
                        Ext.Ajax.request({
                            timeout: 100000000,
                            url: '/oaimport/authorizeByNode',
                            params:{
                                nodeid:view.down('[itemId=nodeItem]').getValue(),
                                isIncluded:view.down('[itemId=includeItem]').getValue()
                            },
                            method: 'post',
                            success: function () {
                                view.close();
                                Ext.MessageBox.close();
                                Ext.Msg.alert('提示', '授权完成！');
                            },
                            failure: function () {
                                Ext.MessageBox.close();
                                Ext.Msg.alert('提示', '操作失败！');
                            }
                        });
                    });
                }
            }
        })
    }
});