/**
 * Created by yl on 2017/11/3.
 */
Ext.define('ExchangeStorage.controller.ExchangeStorageController', {
    extend: 'Ext.app.Controller',

    // 其实翻译出来就是“从根 app 开始找 view（注意没带 s 哦） 目录，在这个目录下找到 student 目录，然后加载 List.js 这个文件”
    views: ['ExchangeStorageView','ExchangeStorageGridView', 'ExchangeStorageDetailGridView','ExchangeStorageShow','ExchangeStorageValidate'],//加载view
    stores: ['ExchangeStorageGridStore','ExchangeStorageDetailGridStore'],//加载store
    models: ['ExchangeStorageGridModel','ExchangeStorageDetailGridModel','ExchangeStorageTreeModel'],//加载model
    init: function () {
        this.control({
            'exchangeStorageGridView ': {
                afterrender: function (view, e, eOpts) {
                    view.getStore().on('load',function(store,records){
                        Ext.Function.defer(function(){
                            for(var i=0;i<records.length;i++){
                                var record = records[i];
                                Ext.Ajax.request({
                                    params: {exchangeid: record.get('id')},
                                    url: '/exchangeStorage/verifyExchangeStorage',
                                    method: 'POST',
                                    async:false,
                                    timeout:15000,
                                    success : function(response,opts) {
                                        var respText = Ext.decode(response.responseText);
                                        if(respText.success == true){
                                            record.set('validate','<span style="color:green">通过</span>');
                                        }else{
                                            record.set('validate','<span style="color:orange">未通过</span>');
                                        }
                                        record.commit();
                                    }
                                });
                            }
                        },50);
                    });
                    view.initGrid();
                }
            },
            'exchangeStorageGridView': {
                itemclick :function  ( view, record, item, index, e, eOpts ){

                    if(e.position.colIdx == view.grid.columns.length){
                        var win = Ext.create('ExchangeStorage.view.ExchangeStorageValidate');
                        win.down('[itemId=closeBtn]').on('click',function(){win.close()});
                        win.down('[itemId=authenticity]').html = '<span style="color:green">验证通过：</br></br>无篡改痕迹</br></br>元数据检查通过</span>';
                        win.down('[itemId=integrity]').html = '<span style="color:green">验证通过：</br></br>背景信息检查通过</br></br>逻辑结构检查通过</br></br>物理结构检查通过</br></br>内容信息检查通过</span>';
                        win.down('[itemId=usability]').html = (record.get('validate').indexOf('未通过') > -1 ?'<span style="color:orange">验证未通过：</br></br>目录数据检查未通过</span>':'<span style="color:green">验证通过：</br></br>目录数据检查通过</br></br>内容数据检查通过</span>');
                        win.down('[itemId=safety]').html = '<span style="color:green">验证通过：</br></br>密级检查通过</br></br>控制标识检查通过</br></br>病毒检查通过</span>';
                        win.show();
                        return;
                    }

                    var exchangeStorageDetailGridStore = this.findStorageDetailView(view).getStore();
                    if(record.get('validate').indexOf('未通过') > -1){
                        exchangeStorageDetailGridStore.removeAll();
                        return;
                    }
                    var exchangeStorageView = view.findParentByType('exchangeStorageView');
                    var treepanel= this.findStorageTreeView(view);
                    treepanel.getStore().proxy.extraParams.pcid = '';

                    var dataview = this.findDataView(view);
                    dataview.getStore().proxy.extraParams.parentid = '';
                    dataview.getStore().reload();

                    Ext.MessageBox.wait('正在解析数据请稍后...','提示');
                    Ext.Ajax.request({
                        params: {exchangeid: record.get('id')},
                        url: '/exchangeStorage/verifyExchangeStorage',
                        method: 'POST',
                        sync: true,
                        timeout:15000,
                        success : function(response,opts) {
                            treepanel.getStore().reload();
                            var respText = Ext.decode(response.responseText);
                            if(respText.success == true){
                                Ext.MessageBox.hide();
                                exchangeStorageDetailGridStore.loadPage(1);
                            }else{
                                Ext.MessageBox.alert('提示', respText.msg);
                                exchangeStorageDetailGridStore.loadPage(1);
                            }
                        },
                        failure:function(response,opts) {
                            treepanel.getStore().reload();
                            Ext.MessageBox.alert('提示', '操作失败');
                            exchangeStorageDetailGridStore.loadPage(1);
                        }
                    });
                }
            },
            'exchangeStorageDetailGridView combobox[itemId=pagesizeComboID]': {
                change: function (view, newValue, oldValue, eOpts) {
                    var exchangeStorageDetailGridView = view.findParentByType('exchangeStorageDetailGridView');
                    var exchangeStorageDetailGridStore = exchangeStorageDetailGridView.getStore();
                    if (newValue != oldValue) {
                        exchangeStorageDetailGridStore.pageSize = view.getValue();
                        exchangeStorageDetailGridStore.loadPage(1);
                    }
                }
            },
            'treepanel': {
                select: function (treemodel, record) {
                    var dataview = this.findDataView(treemodel.view);
                    dataview.getStore().proxy.extraParams.parentid = record.get('fnid');
                    dataview.getStore().reload();
                }
            }
        });
    },
    findView:function (view) {
        return view.findParentByType('exchangeStorageView');
    },
    findStorageTreeView:function (view) {
        return this.findView(view).down('treepanel');
    },
    findStorageDetailView:function (view) {
        return this.findView(view).down('exchangeStorageDetailGridView');
    },
    findDataView : function (view) {
        return this.findView(view).down('[itemId=dataview]');
    }
});