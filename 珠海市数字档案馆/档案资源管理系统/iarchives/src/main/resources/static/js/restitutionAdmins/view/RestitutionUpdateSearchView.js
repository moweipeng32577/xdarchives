/**
 * Created by SunK on 2018/10/29 0029.
 */
Ext.define('Restitution.view.RestitutionUpdateSearchView', {
    extend: 'Ext.panel.Panel',
    xtype:'restitutionUpdateSearchView',
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
                itemId:'simpleSearchSearchComboId',
                store : [['title','题名'],
                    ['archivecode','档号'],
                    ['filedate','文件日期'],
                    ['responsible','责任者'],
                    ['filenumber','文件编号']],
                value: 'title',
                style: 'margin-right:2px',
                editable:false//只能从下拉菜单中选择，不可手动编辑
            },{
                columnWidth: .3,
                xtype: 'searchfield',
                itemId:'simpleSearchSearchfieldId',
                style: "margin-right:2px"
            },{
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
                    var grid = btn.findParentByType('restitutionSearchView').down('restitutionGridView');
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
                text: '<span style="color: #000000 !important;">关闭</span>',
                tooltip:'关闭当前页面',
                style:{
                    'background-color':'#f6f6f6 !important',
                    'border-color':'#e4e4e4 !important'
                },
                handler:function(){
                    parent.closeObj.close(parent.layer.getFrameIndex(window.name));
                }
            },
                //     {
                //     columnWidth: .07,
                //     xtype: 'button',
                //     margin:'0 0 0 5',
                //     itemId:'advancedSearchBtn',
                //     text: '<span style="color: #000000 !important;">高级检索</span>',
                //     tooltip:'打开高级检索界面',
                //     style:{
                //         'background-color':'#f6f6f6 !important',
                //         'border-color':'#e4e4e4 !important'
                //     }
                // },
                {
                    columnWidth: .1,
                    xtype : 'displayfield'
                }]
        },{
            region:'center',
            xtype:'restitutionUpdateView'
        }]
    }
        // ,{
        //     xtype:'EntryFormView'
        // }
    ]
});
