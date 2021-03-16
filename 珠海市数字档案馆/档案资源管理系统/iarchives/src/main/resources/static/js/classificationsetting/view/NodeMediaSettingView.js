/**
 * Created by Leo 2019 12 17
 */

Ext.define('Classificationsetting.view.NodeMediaSettingView', {
    extend: 'Ext.window.Window',
    xtype: 'nodeMediaSettingView',
    itemId:'nodeMediaSettingViewid',
    title: '',
    width: 750,
    height: 150,
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
    items:[{
        xtype: 'form',
        margin:'25',
        modelValidation: true,
        trackResetOnLoad:true,
        items: [{
            fieldLabel: '',
            name:'classid',
            hidden:true,
            itemId:'classiditemid'
        },{
            fieldLabel: '',
            name:'organid',
            hidden:true,
            itemId:'organiditemid'
        },{
            fieldLabel: '',
            name:'parentclassid',
            hidden:true,
            itemId:'parentclassiditemid'
        },{
            fieldLabel: '',
            name:'codelevel',
            hidden:true,
            itemId:'codelevelitemid'
        },{
            xtype: 'combo',
            fieldLabel: '是否为声像节点',
            name: 'is_media',
            itemId: 'is_mediaItem',
            editable: false,
            store: new Ext.data.ArrayStore({
                fields: ['value', 'text'],
                data: [['0', '非声像节点'],['1', '声像节点']]
            }),
            valueField: 'value',
            displayField: 'text',
            listeners:{
                afterrender:function(combo){
                    if(combo.getValue()===null){
                        var store = combo.getStore();
                        if(store.getCount() > 0){
                            combo.select(store.getAt(0));
                        }
                        //nodesetting
                        var classid = combo.up('form').down('[itemId=classiditemid]').value;
                        Ext.Ajax.request({
                            url:"/nodesetting/checkMediaNodeIdByClassId?classid=" + classid,
                            anysc:false,
                            method:'get',
                            success:function (response){
                                if(store.getCount() > 0){
                                    combo.select(store.getAt(response.responseText));
                                }
                            }
                        })
                    }
                }
            }
        }
        ]
    }]
    ,
    buttons: [{
        text: '确定',
        itemId:'setMedia'
    },{
        text: '取消',
        itemId:'cancelSetMedia'
    }
    ]
});
