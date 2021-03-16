Ext.define('MetadataAdmin.controller.MetadataAdminController', {
     extend: 'Ext.app.Controller',

    views: ['MetadataAdminView','MetadataFormView'],//加载view
    stores: ['MetadataAdminGridStore'],//加载store
    models: ['MetadataAdminGridModel'],//加载model

    init: function () {
        this.control({
            'MetadataAdminView [itemId=edit]':{
                click:function (btn) {
                    var grid = btn.up('MetadataAdminView');
                    var select = grid.getSelectionModel();
                    if (select.getSelected().length!=1) {
                        XD.msg('请选择一条数据');
                        return;
                    }
                    var metadataFormView = Ext.create('MetadataAdmin.view.MetadataFormView',{medtaDatagrid:grid});
                    metadataFormView.show();
                    var record = select.selected.items;
                    var id = record[0].get('id')
                    var form = metadataFormView.down('form');
                    form.load({
                        url: '/metadataAdmin/getMetadataForm',
                        method: 'POST',
                        params: {'id':id},
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'MetadataAdminView [itemId=del]':{
                click:function (btn) {
                    var grid = btn.up('MetadataAdminView');
                    var select = grid.getSelectionModel();
                    if (select.getSelected().length<1) {
                        XD.msg('至少选择一条数据');
                        return;
                    }
                    var ids = [];
                    var record = select.selected.items;
                    for (var i = 0; i < record.length; i++) {
                        ids.push(record[i].get('id'));
                    }

                    XD.confirm('确定要删除选择的'+ids.length+'条数据吗?',function(){
                        Ext.Ajax.request({
                            params: {'ids': ids},
                            url: '/metadataAdmin/delMetadatas',
                            method: 'POST',
                            success: function (resp) {
                                var respText = Ext.decode(resp.responseText);
                                if(respText.success){
                                    XD.msg('删除成功');
                                    grid.getStore().reload();
                                }else{
                                    XD.msg('删除失败');
                                }
                            },
                            failure: function() {
                                XD.msg('操作失败');
                            }
                        });
                    },this);
                }
            },

            'MetadataFormView [itemId=submit]':{
                click:function(btn){
                    var formView =  btn.up('MetadataFormView');
                    var form = formView.down('form');
                    form.submit({
                        method: 'POST',
                        url: '/metadataAdmin/metadataFormSubmit',
                        scope: this,
                        success: function (form, action) {
                            XD.msg('修改成功');
                            formView.close();
                            formView.medtaDatagrid.getStore().reload();
                        },
                        failure: function (form, action) {
                            XD.msg('操作失败');
                        }
                    });
                }
            },

            'MetadataFormView [itemId=close]':{
                click:function(btn){
                    btn.up('MetadataFormView').close();
                }
            }
        });
    },
});