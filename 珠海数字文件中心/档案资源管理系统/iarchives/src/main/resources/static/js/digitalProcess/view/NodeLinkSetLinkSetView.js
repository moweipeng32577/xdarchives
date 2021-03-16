/**
 * Created by Administrator on 2019/9/18.
 */



Ext.define('DigitalProcess.view.NodeLinkSetLinkSetView', {
    extend: 'Ext.window.Window',
    xtype: 'NodeLinkSetLinkSetView',
    itemId:'NodeLinkSetLinkSetViewId',
    title: '返工-退回处理',
    width:600,
    height:500,
    bodyPadding: 0,
    layout:'border',
    modal:true,
    closeToolText:'关闭',
    items:[
        {
            region: 'north',
            height:50,
            html:'<div style="color: red;margin: 10px 10px 10px 10px;">温馨提示：所选的条目记录将退回至所选环节-未签收中。全部退回，将当前所选记录整批退回，且记录的问题描述都一样。</div>'
        },
        {
            xtype:'form',
            layout:'column',
            region: 'center',
            items:[
                {
                    columnWidth: 1,
                    xtype: 'label',
                    itemId:'relevancyId',
                    text: '关联环节：',
                    style:{
                        color:'green',
                        'font-size':'16px'
                    },
                    margin:'10 10 10 10'
                },
                {  xtype: "combobox",
                    columnWidth: 1,
                    margin:'10 10 10 10',
                    name: "link",
                    fieldLabel: "退回至环节",
                    store: 'ShlinkStore',
                    editable: false,
                    displayField: "nodename",
                    valueField: "id",
                    queryMode: "local",
                    itemId:'shLinkId',
                    getRelevancyLinks:function (view) {
                        var assemblyId;
                        if(view.up('NodeLinkSetLinkSetView').btn){
                            assemblyId = view.up('NodeLinkSetLinkSetView').btn.up('DigitalProcessView').down('[itemId=assemblyBoxId]').assemblyid;
                        }else{
                            assemblyId = view.up('NodeLinkSetLinkSetView').assemblyCombobox.assemblyid
                        }
                        Ext.Ajax.request({
                            params: {assemblyid:assemblyId,linkId:view.getValue()},
                            url: '/digitalProcess/getRelevancyLinks',
                            method: 'POST',
                            sync: true,
                            success: function (resp) {
                                var respText = Ext.decode(resp.responseText);
                                if (respText.success == true) {
                                    view.up('form').down('[itemId=relevancyId]').setText('关联环节：'+respText.data);
                                }else{
                                    XD.msg("操作失败");
                                }
                            },
                            failure: function() {
                                XD.msg('操作失败');
                            }
                        });
                    },
                    listeners: {
                        afterrender: function (combo) {
                            setTimeout(function(){
                                var store = combo.getStore();
                                if (store.getCount() > 0) {
                                    combo.select(store.getAt(0));
                                    combo.getRelevancyLinks(combo);
                                }
                            },300);
                        },
                        select:function (view) {
                            view.getRelevancyLinks(view);
                        }
                    }
                },
                {xtype: 'textfield', fieldLabel: '退回人',name:'id',value:realname,disabled:true,columnWidth: 1,margin:'10 10 10 10',},
                {xtype: 'textfield', fieldLabel: '退回时间',name:'bz',value:new Date().format('yyyy-MM-dd hh:mm:ss'),disabled:true,columnWidth: 1,margin:'10 10 10 10',},
                {xtype: 'textarea', itemId:'depict',fieldLabel: '退回原因',name:'depict',allowBlank: false,columnWidth: 1,margin:'10 10 10 10',},
                {
                    columnWidth: 1,
                    xtype: 'label',
                    itemId:'recordId',
                    text: '',
                    style:{
                        color:'red',
                        'font-size':'18px'
                    },
                    margin:'30 10 10 340'
                },
                {xtype: 'textfield',hidden:true, fieldLabel: '当前环节',itemId:'backText',value:realname,disabled:true,columnWidth: 1,margin:'10 10 10 10',}
            ]
        }],
    buttons: [
        { text: '退回',itemId:'linksetSubmit'},
        { text: '关闭',itemId:'linksetClose'}
    ]
});

