/**
 * Created by Administrator on 2019/2/19.
 */

Ext.define('StApprove.view.SimpleSearchView', {
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
                itemId:'simpleSearchSearchComboId',
                store : [['title','题名'],
                    ['archivecode','档号'],
                    ['filedate','文件日期'],
                    ['responsible','责任者'],
                    ['filenumber','文件编号']
                    // ['archiveclassify','档案门类']
                ],
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
                text: '<span style="color: #606060 !important;">取消选择</span>',
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
                text: '<span style="color: #606060 !important;">返回</span>',
                tooltip:'返回',
                style:{
                    'background-color':'#f6f6f6 !important',
                    'border-color':'#e4e4e4 !important'
                },
                handler:function(btn){
                    if (window.leadIn != null) {
                        window.leadIn.setVisible(false);
                    }
                }
            }]
        },{
            region:'center',
            xtype:'simpleSearchGridView'
        }]
    },{
        xtype:'EntryFormView'
    },{
        itemId: 'mediaFormView',
        xtype: 'mediaFormView'
    }]
});
