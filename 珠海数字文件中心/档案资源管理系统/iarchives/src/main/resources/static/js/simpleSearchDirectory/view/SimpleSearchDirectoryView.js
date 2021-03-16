/**
 * Created by Administrator on 2019/6/26.
 */

Ext.define('SimpleSearchDirectory.view.SimpleSearchDirectoryView', {
    extend: 'Ext.panel.Panel',
    xtype:'simpleSearchDirectoryView',
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
                columnWidth: .13,
                xtype: 'checkboxfield',
                boxLabel: '在结果中检索',
                style: "margin-left:6px;margin-right:10px",
                margin:'-5 0 0 5',
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
                    var grid = btn.findParentByType('simpleSearchDirectoryView').down('simpleSearchDirectoryGridView');
                    for(var i = 0; i < grid.getStore().getCount(); i++){
                        grid.getSelectionModel().deselect(grid.getStore().getAt(i));
                    }
                    grid.acrossSelections = [];
                }
            },
                {
                columnWidth: .2,
                xtype : 'displayfield'
            },{
                columnWidth: .3,
                xtype : 'displayfield'
            }, {
                xtype: "radiogroup",
                columnWidth: .3,
                itemId:'datasoure',
                fieldLabel: "数据源",
                labelWidth: 50,
                items:[
                    {boxLabel: systemLoginType=='0'? '目录中心':'共享平台',inputValue: 'directory',checked:'true'},
                    {boxLabel: '档案系统',inputValue: 'management'},
                    {boxLabel: '声像系统',inputValue: 'soundimage',
                        listeners:{
                            'change':function(group,checked){
                                var gridview = this.up('simpleSearchDirectoryView').down('simpleSearchDirectoryGridView');
                                var print = gridview.down('[itemId=print]');
                                if(checked){
                                    print.hide();
                                }else{
                                    print.show();
                                }
                            }
                        }
                    }
                ]
            }, {
                columnWidth: .4,
                xtype : 'displayfield'
            }]
        },{
            region:'center',
            xtype:'simpleSearchDirectoryGridView'
        }]
    },{
        xtype:'simpleSearchDirectoryFormView'
    }]
});
