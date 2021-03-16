
Ext.define('Lot.controller.ManagementHistoryController',{
    extend: 'Ext.app.Controller',
    stores:['ManagementHistoryStore'],
    models:['managementHistoryModel'],
    init:function(){
        this.control({
            'visualization [itemId=expHistoryBtn]':{//数据管理-导出
                click:function (view) {
                    var visualization = view.up('visualization');
                    var managementId = visualization.down('[itemId=managementId]');
                    // 获取到当前表单中的已选择数据
                    var record = managementId.getSelectionModel().selected;
                    if (record.length < 1) {
                        XD.msg('请选择需要导出的数据');
                        return;
                    }
                    var ids = [];
                    for (var i = 0; i < record.length; i++) {
                        ids.push(record.items[i].get('id'));
                    }
                    var downloadForm = document.createElement('form');
                    document.body.appendChild(downloadForm);
                    var inputTextElement = document.createElement('input');
                    inputTextElement.name ='ids';
                    inputTextElement.value = ids;
                    downloadForm.appendChild(inputTextElement);
                    downloadForm.action='/managementHistory/expHistory';
                    downloadForm.method = "post";
                    downloadForm.submit();
                }
            },
            'visualization [itemId=historySelectBtn]':{//设备信息-查询
                click:function (btn) {
                    var visualization = btn.up('visualization');
                    var informationview = visualization.down('[itemId=managementId]');
                    var content = informationview.down('[itemId=content]').getValue();//查询内容
                    var searchcombo = informationview.down('[itemId=searchcombo]').getValue();//查询字段
                    if(searchcombo==null){
                        XD.msg('请选择需要查询的关键字');
                        return;
                    }
                    informationview.getStore().proxy.url='/managementHistory/findHistory';
                    informationview.getStore().proxy.extraParams.searchcombo = searchcombo;
                    informationview.getStore().proxy.extraParams.content = content;
                    informationview.getStore().load();
                }
            },
        });
    }
});