Ext.define('Lot.view.deviceArea.DeviceAreaGridView',{
    extend:'Ext.grid.Panel',
    xtype:'DeviceAreaGridView',
    title: '区域管理',
    store: 'DeviceAreaStore',
    columns: [
        {xtype: 'rownumberer', align: 'center', width: 40},
        {text: '名称', dataIndex: 'name', flex: 1},
        {text: '楼层', dataIndex: 'floor', flex: 1,
            renderer:function(value){
                return value == null ? "" : value.floorName;
            }
        },
        {text: '类型', dataIndex: 'type', flex: 1,
            renderer:function(value){
               if( value != null && value.toLowerCase() == 'kf')
               {
                   return '库房';
               }
               else{
                   return '区域';
               }
            }
        }
    ],
    tbar: [{text: '增加', itemId: 'add'}, {text: '修改', itemId: 'edit'}, {text: '删除', itemId: 'delete'}],
    listeners : {
        'render' : function(view) {
            var store = view.getStore();
            if(store.getCount()>0){
                var record = view.getStore().getAt(0);
                view.getSelectionModel().select(record);
            }
        },
        'select':function(model, record, index) {
            var itemselector = model.view.up('[itemId=areaPanel]').down('itemselector');
            // 重新加载机构用户数据
            itemselector.getStore().reload({params:{areaid:record.id}});

            Ext.Msg.wait('正在读取数据...');
            setTimeout(function () {
                Ext.Ajax.request({
                    params: {areaid: record.id},
                    url: '/enabledOrDisableDevice',
                    method: 'post',
                    sync: true,
                    success: function (resp) {
                        var respText = Ext.decode(resp.responseText);
                        var data = [];
                        for (var i = 0; i < respText.length; i++) {
                            data.push(respText[i].id);
                        }
                        itemselector.toField.getStore().load({
                            params:{areaid:record.id},
                            callback: function () {
                                itemselector.setValue(data);
                            }
                        })
                        Ext.MessageBox.hide();
                    },
                    failure: function () {
                        XD.msg('操作中断');
                    }
                });
            },500)
        }
    }
});