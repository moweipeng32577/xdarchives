/**
 * Created by RonJiang on 2017/10/25 0025.
 */

Ext.define('MetadataSearch.view.SimpleSearchView', {
    extend: 'Ext.panel.Panel',
    xtype:'simpleSearchView',
    layout:'card',
    activeItem:0,
    items:[{
        itemId:'gridview',
        layout: 'border',
        bodyBorder: false,
        items: [{
            region: 'north',
            height: 130,
            margin: '0 0 0 0',
            layout:'column',
            items:[{
                columnWidth: .35,
                xtype : 'displayfield'
            },{
                columnWidth: .65,
                xtype: 'label',
                text: '温馨提示：查多个关键字中间用一个空格分开！',
                style:{
                    color:'red',
                    'font-size':'17px',
                    'font-weight':'bold'
                },
                margin:'40 0 15 0'
            },{
                columnWidth: .2,
                xtype : 'displayfield'
            },{
                columnWidth: .1,
                xtype : 'combo',
                itemId:'metadataSearchTypeComboId',//simpleSearchSearchComboId
                store : [
                    ['m1','照片'],
                    ['m2','录音'],
                    ['m3','视频'],
                    ['m4','文书'],
                ],
                value: 'm1',
                style: 'margin-right:2px',
                editable:false,//只能从下拉菜单中选择，不可手动编辑
                listeners: {
                    select:function(combo){
                        var simpleSearchView = combo.findParentByType('simpleSearchView');
                        var metadataSearchComboId = simpleSearchView.down('[itemId=metadataSearchComboId]');
                        metadataSearchComboId.setValue('');
                        metadataSearchComboId.getStore().proxy.extraParams.metadataType=combo.getValue();
                        metadataSearchComboId.getStore().reload();
                    },
                    render:function (combo) {
                        var simpleSearchView = combo.findParentByType('simpleSearchView');
                        var metadataSearchComboId = simpleSearchView.down('[itemId=metadataSearchComboId]');
                        metadataSearchComboId.getStore().proxy.extraParams.metadataType=combo.getValue();
                        metadataSearchComboId.getStore().reload();
                    }
                }
            },{
                columnWidth: .1,
                xtype : 'combo',
                itemId:'metadataSearchComboId',
                store :'MetadataSearchTypeStore',
                displayField: 'fieldname',
                valueField: 'templateid',
                style: 'margin-right:2px',
                editable:false,//只能从下拉菜单中选择，不可手动编辑
                listeners: {
                    // expand: function (combo) {
                    //     //获取检索类型combo的值 重新加载combo
                    //     var simpleSearchView = combo.findParentByType('simpleSearchView');
                    //     var mType = simpleSearchView.down('[itemId=metadataSearchTypeComboId]');
                    //     var mValue = mType.getValue();
                    //     combo.getStore().proxy.extraParams.metadataType=mValue;
                    //     combo.getStore().reload();
                    // }
                }
            },{
                columnWidth: .3,
                xtype: 'searchfield',
                itemId:'simpleSearchSearchfieldId',
                style: "margin-right:2px"
            },/*{
                columnWidth: .09,
                xtype: 'checkboxfield',
                boxLabel: '即时搜索',
                style: "margin-left:6px;margin-right:10px",
                itemId:'instantSearch'
            },*/{
                columnWidth: .09,
                xtype: 'checkboxfield',
                boxLabel: '在结果中检索',
                style: "margin-left:6px;margin-right:10px",
                itemId:'inresult'
            },{
                columnWidth: .07,
                xtype: 'button',
                margin:'0 0 0 5',
                text: '<span style="color: #000000 !important;">取消选择</span>',
                tooltip:'取消所有跨页选择项',
                style:{
                    'background-color':'#f6f6f6 !important',
                    'border-color':'#e4e4e4 !important'
                },
                handler:function(btn){
                    var grid = btn.findParentByType('simpleSearchView').down('simpleSearchGridView');
                    for(var i = 0; i < grid.getStore().getCount(); i++){
                        grid.getSelectionModel().deselect(grid.getStore().getAt(i));
                    }
                    grid.acrossSelections = [];
                }
            },{
                columnWidth: .07,
                xtype: 'button',
                margin:'0 0 0 5',
                itemId:'topCloseBtn',
                hidden:true,
                text: '<span style="color: #000000 !important;">关闭</span>',
                tooltip:'关闭当前页面',
                style:{
                    'background-color':'#f6f6f6 !important',
                    'border-color':'#e4e4e4 !important'
                },
                handler:function(){
                    parent.closeObj.close(parent.layer.getFrameIndex(window.name));
                }
            },{
                columnWidth: .07,
                xtype: 'button',
                margin:'0 0 0 5',
                itemId:'advancedSearchBtn',
                text: '<span style="color: #000000 !important;">高级检索</span>',
                tooltip:'打开高级检索界面',
                style:{
                    'background-color':'#f6f6f6 !important',
                    'border-color':'#e4e4e4 !important'
                }
            },{
                columnWidth: .1,
                xtype : 'displayfield'
            }]
        },{
            region:'center',
            xtype:'simpleSearchGridView'
        }]
    },{
        xtype:'EntryFormView'
    }]
});
