/**
 * Created by Administrator on 2019/5/28.
 */


Ext.define('JyAdmins.view.PrintEleDetailView',{
    entryid: '',     //条目主键ID
    entrytype: 'electronic',   //数据类型（采集、管理、利用）

    extend: 'Ext.panel.Panel',
    xtype: 'printEleDetailView',
    layout: 'border',
    operateFlag:'',
    bodyBorder: false,
    defaults: {
        split: true
    },
    items: [{
        region: 'west',
        layout: {
            type:'vbox',
            align:'stretch'
        },
        width: 500,
        header: false,
        hideHeaders: true,
        items: [{
            xtype:'grid',
            width:'100%',
            itemId:'eleGrid',
            rowLines:true,
            columnLines:true,
            store:'PrintEleDetailGridStore',
            selType:'checkboxmodel',
            columns:[{
                text:'文件名称',
                dataIndex:'filename',
                flex:2
            },{
                text:'打印范围',
                dataIndex:'printstate',
                flex:1
            },{
                text:'页数范围',
                dataIndex:'scopepage',
                flex:1
            },{
                text:'打印份数',
                dataIndex:'copies',
                flex:1
            },{
                text:'通过状态',
                dataIndex:'pass',
                flex:1
            }],
            listeners: {
                itemclick: function ( view, record, item, index, e, eOpts )  {
                    if(e.getTarget('.x-grid-checkcolumn',1,true)){
                        return;
                    }
                    var view = this.findParentByType('printEleDetailView');
                    var grid =view.down('[itemId=eleGrid]');
                    // var mediaFrame = document.getElementById('mediaFrame');
                    //当采集、管理模块在未归已归、案卷、卷内点击著录或修改时，会创建多个相同ID的iframe
                    //document.getElementById只会拿第一个，导致下面的src对应不了正确显示的那个iframe
                    var allMediaFrame = document.querySelectorAll('#mediaFramePrint');
                    var mediaFrame;
                    //创建electronicview需要指定是著录还是修改类型，经调试，著录的iframe是第一个，修改的是最后一个
                    if (allMediaFrame.length > 0 && view.operateFlag == 'add') {
                        mediaFrame = allMediaFrame[allMediaFrame.length - 1];
                    } else {
                        mediaFrame = allMediaFrame[0];
                    }
                    var filename = record.get('filename');
                    if(view.type=="allPass"){
                        mediaFrame.setAttribute('src', '/electronic/jyMediaPrint?entrytype=' + 'solid' + '&eleid=' + record.get('eleid')+ '&filetype=' + filename.substring(filename.lastIndexOf('.') + 1)+'&type='+'hasWm');
                    }else {
                        mediaFrame.setAttribute('src', '/electronic/jyMediaPrint?entrytype=' + 'solid' + '&eleid=' + record.get('eleid')+ '&filetype=' + filename.substring(filename.lastIndexOf('.') + 1)+'&type='+'noWm');
                    }
                }
            }
        }],
        autoScroll: true,
        rootVisible: true,
        checkPropagation: 'both',
        dockedItems: [{
            xtype: 'toolbar',
            dock: 'top',
            items: [{
                xtype: 'button',
                itemId:'print',
                text: '打印',
                hidden:true
            }]

        }]

    },

        {
            region: 'center',
            layout: 'border',
            items: [
                {
                    region: 'center',
                    width: '100%',
                    height: '100%',
                    html: '<iframe id="mediaFramePrint" src=""  width="100%" height="100%" style="border:0px;"></iframe>'
                }
            ]
        }]
});
