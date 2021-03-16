/**
 * Created by Administrator on 2020/6/4.
 */


Ext.define('ElectronApprove.view.SetBorrowTypeView', {
    extend: 'Ext.window.Window',
    xtype: 'setBorrowTypeView',
    itemId:'setBorrowTypeViewId',
    title: '设置查档类型',
    frame: true,
    resizable: true,
    width: 400,
    height: 180,
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
        layout: 'column',
        bodyPadding: 16,
        items: [
            {
                columnWidth:.3,
                xtype: 'label',
                labelWidth: 85,
                margin: '15 0 0 0',
                itemId:'checklabeltype',
                html:'查档类型:<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
            },{
                columnWidth:.05,
                labelWidth: 85,
                xtype: 'checkbox',
                itemId:'electronCheckId',
                margin: '8 0 0 0',
                inputValue : true,
                hideLabel: true,
                listeners: {
                    'change':function(group,checked){
                        var transferCheckId = this.up('form').down('[itemId=transferCheckId]');
                        if(checked){
                            transferCheckId.setValue(false);
                        }
                    }
                }
            },{
                columnWidth:.15,
                xtype: 'label',
                margin: '15 0 0 0',
                itemId:'electronChecktype',
                text:"电子查档"
            },{
                columnWidth:.05,
                xtype:'displayfield'
            },{
                columnWidth:.05,
                xtype: 'checkbox',
                labelWidth: 85,
                itemId:'stCheckId',
                margin: '8 0 0 0',
                inputValue : true,
                hideLabel: true,
                listeners: {
                    'change':function(group,checked){
                        var transferCheckId = this.up('form').down('[itemId=transferCheckId]');
                        if(checked){
                            transferCheckId.setValue(false);
                        }
                    }
                }
                // listeners:{
                //     change:function (view,record) {
                //         if(record){
                //             var setBorrowTypeView = view.findParentByType('setBorrowTypeView');
                //             var select = setBorrowTypeView.select;
                //             if(select){
                //                 var flag = false;
                //                 for(var i=0;i<select.length;i++){
                //                     if((select[i].get('kccount')<1||select[i].get('kccount')==undefined)&&select[i].get('type')=='电子查档'){
                //                         flag = true;
                //                     }
                //                 }
                //                 if(flag){
                //                     XD.msg('存在条目库存份数为0');
                //                     setTimeout(function () {
                //                         setBorrowTypeView.down('[itemId=stCheckId]').setValue(false);
                //                     },300);
                //                 }
                //             }
                //         }
                //     }
                // }
            },{
                columnWidth:.15,
                xtype: 'label',
                margin: '15 0 0 0',
                itemId:'stChecktype',
                text:"实体查档"
            },{
                columnWidth:.05,
                xtype:'displayfield'
            },{
                columnWidth:.05,
                labelWidth: 85,
                xtype: 'checkbox',
                itemId:'transferCheckId',
                margin: '8 0 0 0',
                inputValue : true,
                hideLabel: true,
                listeners: {
                    'change':function(group,checked){
                        var electronCheckId = this.up('form').down('[itemId=electronCheckId]');
                        var stCheckId = this.up('form').down('[itemId=stCheckId]');
                        if(checked){
                            electronCheckId.setValue(false);
                            stCheckId.setValue(false);
                        }
                    }
                }
            },{
                columnWidth:.15,
                xtype: 'label',
                margin: '15 0 0 0',
                itemId:'transferChecktype',
                text:"调档"
            }
        ]
    }],

    buttons: [
        { text: '提交',itemId:'stTypeSubmit'},
        { text: '关闭',itemId:'stTypeClose'}
    ]
});
