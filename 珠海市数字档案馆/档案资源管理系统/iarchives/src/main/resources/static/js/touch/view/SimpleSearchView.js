/**
 * Created by RonJiang on 2017/10/25 0025.
 */

Ext.define('Touch.view.SimpleSearchView', {
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
            height: 150,
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
                    ['funds','全宗号'],
                    ['catalog','目录号'],
                    ['filecode','案卷号'],
                    ['recordcode','件号'],
                    ['filingyear','归档年度'],
                    ['filenumber','文件编号']],
                value: 'title',
                style: 'margin-right:2px',
                editable:false//只能从下拉菜单中选择，不可手动编辑
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
                itemId:'search',
                text: '<span style="color: #000000 !important;">搜索</span>',
                tooltip:'搜索',
                style:{
                    'background-color':'#f6f6f6 !important',
                    'border-color':'#e4e4e4 !important'
                },
                // handler:function(){
                //     parent.closeObj.close(parent.layer.getFrameIndex(window.name));
                // }
            },{
                columnWidth: .07,
                xtype: 'displayfield',
                margin:'0 0 0 5',
                itemId:'logout',
                // text: '<span style="color: #000000 !important;">退出</span>',
                // tooltip:'退出自助查询',
                // style:{
                //     'background-color':'#f6f6f6 !important',
                //     'border-color':'#e4e4e4 !important'
                // }
            },{
                columnWidth: .1,
                xtype : 'displayfield'
            },{
                columnWidth: .33,
                xtype : 'displayfield'
            },{
                xtype: "radiogroup",
                columnWidth: .2,
                itemId:'datasoure',
                fieldLabel: "数据源",
                labelWidth: 50,
                items:[
                    {boxLabel: '正式库',inputValue: 'management',checked:'true'},
                    {boxLabel: '采集库',inputValue: 'capture'},
                ],
                hidden:true//titleflag == "2"?false:true
            },{
                xtype: "radiogroup",
                columnWidth: .2,
                itemId:'datasoure2',
                fieldLabel: "数据源",
                labelWidth: 50,
                items:[
                    {boxLabel: '档案系统',inputValue: 'management',checked:'true'},
                    {boxLabel: '声像系统',inputValue: 'soundimage'}
                ],
                hidden:true//titleflag == "2"?false:true
            },{
                columnWidth: .47,
                xtype : 'displayfield'
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
