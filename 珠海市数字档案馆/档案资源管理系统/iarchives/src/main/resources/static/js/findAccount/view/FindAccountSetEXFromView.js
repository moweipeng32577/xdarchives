/**
 * Created by Administrator on 2017/10/23 0023.
 */
var infodateStore = Ext.create("Ext.data.Store", {
    fields: ["Name", "Value"],
    data: [
        { Name: "一天", Value: '一天'},
        { Name: "一周", Value: '一周'},
        { Name: "一月", Value: '一月'}
    ]
});
Ext.define('FindAccount.view.FindAccountSetEXFromView', {
    extend: 'Ext.window.Window',
    xtype: 'FindAccountSetEXFromView',
    itemId:'FindAccountSetEXFromViewId',
    title: '该用户已过期，请修改有效期！',
    frame: true,
    resizable: true,
    width: 300,
    minWidth: 250,
    minHeight: 130,
    modal:true,
    closeToolText:'关闭',
    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    defaults: {
        layout: 'form',
        xtype: 'container',
        defaultType: 'textfield',
        style: 'width: 50%'
    },
    items: [{
        xtype: 'form',
        modelValidation: true,
        itemId:'selectInfodate',
        items: [
            {
                xtype: "combobox",
                name: "infodate",
                fieldLabel: "有效期",
                store: infodateStore,
                editable: false,
                displayField: "Name",
                valueField: "Value",
                queryMode: "local",
                value: '一月'
            },
        ]
    }],

    buttons: [
        { text: '完成',itemId:'setExDateSubmit',
            handler:function(btn){
             var grid = btn.findParentByType('FindAccountSetEXFromView');
             var data = grid.down('form').getValues();
             var expiryDate =data['infodate'];
                Ext.Ajax.request({
                    url: '/user/setNewTime',
                    params: {
                        userid:grid.GridView.selModel.getSelection()[0].id,
                        expiryDate:expiryDate
                    },
                    method: 'POST',
                    success: function (res, opt) {
                        var responseText = Ext.decode(res.responseText);
                        if (responseText.success == true) {
                            grid.close();
                            grid.GridView.getStore().reload();
                            XD.msg(responseText.msg);
                        }
                    },
                    scope: this
                });


            }
        },
        { text: '关闭',itemId:'setExDateClose',
            handler:function(btn){
                btn.findParentByType('FindAccountSetEXFromView').close();
            }
        }
    ]
});